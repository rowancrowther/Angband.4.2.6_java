/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.utils.Combiner;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.channel.utils.UIEntryCombinerState;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.cached.CachedPlayerData;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.BoundPlayerAbility;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.*;

/**
 * The runtime lookup table binding {@link ObjectProperty}/{@link PlayerProperty} pairs to named UI
 * entries and computing each entry's displayed value for an object or a player — the Java port of
 * the entry-bookkeeping half of C's {@code ui-entry.c}: {@code bind_object_property_to_ui_entry_by_name},
 * {@code bind_player_ability_to_ui_entry_by_name}, {@code compute_ui_entry_values_for_object},
 * {@code compute_ui_entry_values_for_player} and {@code is_ui_entry_for_known_rune}, plus the three
 * file-private helpers the player computation leans on, {@code modifier_to_skill},
 * {@code get_timed_modifier_effect} and {@code get_timed_element_effect}.
 *
 * <p>C keeps one {@code struct ui_entry} per named entry, each carrying its own growable arrays of
 * bound object properties and player abilities ({@code entry->obj_props}, {@code entry->p_abilities}),
 * appended to one at a time by the two {@code bind_*} functions as {@code object_property.txt} and
 * {@code player_property.txt} are parsed. This class inverts that: {@link EntryBinding} holds the
 * bound {@link ObjectProperty} and {@link PlayerProperty} lists for one entry name, keyed by that
 * name in {@link #bindingByEntry}, and {@link #addEntryBinding} is the single upsert point
 * {@code GameConstants} calls once an entry's bindings are known, rather than growing the lists
 * incrementally. C's {@code ui_entry_search} (a binary search of the sorted {@code entries} array)
 * has no counterpart here; the map lookup replaces it. The display half of {@code struct ui_entry}
 * — labels, categories, renderer — lives on {@code UIEntry}, not here.
 *
 * <p>The two {@code computeFor*} methods are the boundary's read side: given an entry name they
 * resolve the {@link EntryBinding}, thread the bound properties through the entry's
 * {@link CombinerName}, and hand back a single {@link UIEntryValue} the UI can render, mirroring
 * C's out-parameter pair ({@code *val}, {@code *auxval}). Both expect the registry to have been
 * loaded or cleared first: until {@link #addEntryBinding} or {@link #clearEntryBindings()} has run,
 * {@link #bindingByEntry} is {@code null} and the read methods throw {@link NullPointerException}.
 * C has no such state, since its {@code entries} array exists for the life of the process.
 *
 * <p>Class UIEntryValueRegistry coded before 260924, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
public class UIEntryValueRegistry {
    /**
     * Logger for the one failure this class treats as fatal: a resolved {@link EntryBinding} whose
     * {@link CombinerName} is {@code null} — see {@link #computeForPlayer}. C's equivalent is the
     * {@code assert(0)} after {@code ui_entry_combiner_get_funcs} in
     * {@code compute_ui_entry_values_for_player}.
     *
     * <p>Field logger coded before 260924, commented in full on 261006.
     */
    private static final Logger logger = LogManager.getLogger(UIEntryValueRegistry.class);

    /**
     * The registry of UI entry bindings, keyed by entry name — the port of C's {@code entries} array
     * of {@code struct ui_entry}, which C searches by name with {@code ui_entry_search}. Populated by
     * {@link #addEntryBinding} and read by {@link #computeForObject}, {@link #computeForPlayer} and
     * {@link #isKnownRune}.
     *
     * <p>{@code null} until the first {@link #addEntryBinding} call or the first
     * {@link #clearEntryBindings()}; the three read methods do not guard against that, so reading a
     * never-initialised registry throws {@link NullPointerException}. Only entries that have at least
     * one binding are ever stored, so a name that is absent here stands for a C entry whose
     * {@code obj_props} and {@code p_abilities} arrays are both empty.
     *
     * <p>Field bindingByEntry coded before 260924, commented in full on 261006.
     */
    private static Map<String, EntryBinding> bindingByEntry;

    /**
     * Resets {@link #bindingByEntry} to an empty map, discarding every previously registered binding.
     * Has no C counterpart — C's {@code entries} array lives for the process and is never cleared —
     * this exists so each test starts from a known-empty registry rather than one carrying bindings a
     * prior test added. It also gives the map a value where it would otherwise still be {@code null}.
     *
     * <p>Function clearEntryBindings coded before 260924, commented in full on 261006.
     */
    @TestOnly
    public static void clearEntryBindings() {
        bindingByEntry = new HashMap<>();
    }

    /**
     * Registers (or updates) the bindings for one named UI entry — the upsert counterpart of C's
     * {@code bind_object_property_to_ui_entry_by_name} and
     * {@code bind_player_ability_to_ui_entry_by_name}, both in {@code ui-entry.c}. Where C appends one
     * property or one ability at a time to the growable arrays on a {@code struct ui_entry} (doubling
     * the allocation from four when full), this stores the whole {@link ObjectProperty} and
     * {@link PlayerProperty} lists for {@code entryName} in one call, replacing whatever
     * {@link EntryBinding} was there before.
     *
     * <p>A {@code null} list is treated as "leave that half alone": if {@code objProperties} is
     * {@code null} and an {@link EntryBinding} already exists for {@code entryName} with a non-null
     * object-property list, the existing list is kept rather than being wiped by the {@code null};
     * the same applies to {@code playerProperties}. This lets a caller that only knows one half of an
     * entry's bindings — object properties from {@code object_property.txt}, player abilities from
     * {@code player_property.txt} — update just that half without erasing the other.
     * {@code combinerName} and {@code entryFlags} are always replaced, never merged.
     *
     * <p>Two things C's {@code bind_*} functions do are not done here. They return 1 when
     * {@code name} is not configured in {@code ui_entry.txt}; this method accepts any name, so the
     * caller must have checked (the one caller, {@code GameConstants}, only registers names it found
     * among its {@code UIEntrySpec}s). And C copies each property's value, {@code have_value} and
     * {@code isaux} into the new array slot; here those travel on the properties themselves
     * ({@link ObjectProperty.UIBinding}, {@link PlayerProperty.BindUI}) and the lists are stored by
     * reference, not copied.
     *
     * <p>Function addEntryBinding coded before 260924, commented in full on 261006.
     *
     * @param entryName        the UI entry name to bind, as configured in {@code ui_entry.txt}
     * @param objProperties    the object properties bound to the entry, or {@code null} to keep the
     *                         existing list
     * @param playerProperties the player properties bound to the entry, or {@code null} to keep the
     *                         existing list
     * @param combinerName     the combiner used to merge multiple bound properties' values for this
     *                         entry
     * @param entryFlags       the entry's configured flags (for example
     *                         {@code ENTRY_FLAG_TIMED_AS_AUX})
     */
    public static void addEntryBinding(String entryName, List<ObjectProperty> objProperties,
                                       List<PlayerProperty> playerProperties, CombinerName combinerName,
                                       FlagView<ChannelEntryFlag> entryFlags) {
        if (bindingByEntry == null) {
            bindingByEntry = new HashMap<>();
        }

        EntryBinding existing = bindingByEntry.get(entryName);
        if (objProperties == null && existing != null && existing.objectProperties() != null) {
            objProperties = existing.objectProperties();
        }

        if (playerProperties == null && existing != null && existing.playerProperties() != null) {
            playerProperties = existing.playerProperties();
        }

        bindingByEntry.put(entryName, new EntryBinding(objProperties, playerProperties, combinerName, entryFlags));
    }

    /**
     * Computes the combined display value(s) for one named UI entry on a given object — the port of
     * C's {@code compute_ui_entry_values_for_object} in {@code ui-entry.c}.
     *
     * <p>Walks the object properties bound to {@code entryName} (see {@link #addEntryBinding}) once
     * for the item itself and once more for each curse it carries with non-zero power
     * ({@code item.getCurses()}'s keys, in ascending curse index as C's scan of {@code obj->curses}
     * visits them, skipping any whose {@link CurseData#getPower()} is zero), feeding every known,
     * non-zero property value through the entry's {@link Combiner}. For every property family — a
     * stat/modifier, a flag, an ignore, or a resist/immunity/vulnerability — the item's own value is
     * used unless the caller supplied a fixed {@link ObjectProperty.UIBinding#value()} for that
     * binding, in which case the fixed value stands in for any non-zero real one — C's
     * {@code have_value} check. A property the {@code player} cannot yet identify (per
     * {@link ObjectUtils#objectFlagIsKnown}, {@link ObjectUtils#objectElementIsKnown},
     * {@link ObjectUtils#curseObjectFlagIsKnown} or {@link KnownObject#modifierIsKnown}) contributes
     * {@link Combiner#UI_ENTRY_UNKNOWN_VALUE} instead of its real value; passing a {@code null} player
     * is C's "assume everything is known" case.
     *
     * <p>A binding flagged {@link ObjectProperty.UIBinding#aux()} contributes to the auxiliary total
     * rather than the main one (the {@code v}/{@code a} swap C performs before calling into the
     * combiner), unless the entry carries {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX}, in which
     * case auxiliary bindings are skipped here entirely — they are surfaced as the player's timed
     * effect instead, via {@link #getTimedModifierEffect}/{@link #getTimedElementEffect} in
     * {@link #computeForPlayer}.
     *
     * <p>{@code cache} caches the item's own flags across repeated calls for the same item/player
     * knowledge state (C's {@code *cache}, populated once and reused). It is populated after the
     * early return for a missing item or an entry with nothing bound, as in C, and must not be
     * {@code null}: C allocates the cache through its {@code **cache} out-parameter, but Java cannot
     * hand a new object back, so the caller supplies it. Only the item's own flag lookup reads it. A
     * curse's flags are read straight off the curse's object ({@link Curse#getItemObject()})
     * instead: with a {@code player}, the flags its known shadow shows intersected with its real
     * flags, then the kind's flags if the kind is aware, then an easy-known ego's flags added and its
     * suppressed flags removed (C's {@code object_flags_known}); with no player, its real flags (C's
     * {@code object_flags}). C refills a second cache ({@code cache2}) with each curse's flags
     * before reading them back, which the port has no use for.
     *
     * <p>Returns {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} for both values if {@code entryName} is
     * not registered, or {@code item} is {@code null}, or nothing is bound to the entry. Otherwise
     * the main total is {@code 0} when every bound property is auxiliary, and the auxiliary total is
     * {@code 0} when no auxiliary property was walked (auxiliary properties skipped for
     * {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX} do not count); any other case is the
     * combiner's finished total, or {@code 0} if no property contributed a non-zero value.
     *
     * <p>An entry whose {@link CombinerName} is {@link CombinerName#NONE} is the state C stops on
     * with {@code assert(0)}; the port does not stop here but fails with a
     * {@link NullPointerException} at the first non-zero contribution, because
     * {@link CombinerName#NONE} carries no {@link Combiner} to clone. C's {@code hatch_embryo}
     * rejects a new entry with no combiner and so does {@code UIEntryAssembler}, so loaded data never
     * reaches this state.
     *
     * <p>Function computeForObject coded before 260924, commented in full on 261006, cache note
     * corrected on 261005.
     *
     * @param entryName the UI entry to compute a value for
     * @param item      the object being assessed, or {@code null}
     * @param player    the player used to gate property visibility by knowledge, or {@code null} to
     *                  treat every property as known
     * @param cache     flag cache for {@code item}, reused across calls for the same item/player state
     * @return the entry's combined main and auxiliary values for {@code item}
     */
    public static UIEntryValue computeForObject(String entryName, ItemObject item, Player player, ObjectValueCache cache) {
        EntryBinding props = bindingByEntry.getOrDefault(entryName, null);
        if (props == null) return new UIEntryValue(Combiner.UI_ENTRY_VALUE_NOT_PRESENT,
                Combiner.UI_ENTRY_VALUE_NOT_PRESENT, false);

        List<ObjectProperty> bound = props.objectProperties();

        if (item == null || bound == null || bound.isEmpty()) {
            return new UIEntryValue(Combiner.UI_ENTRY_VALUE_NOT_PRESENT,
                    Combiner.UI_ENTRY_VALUE_NOT_PRESENT, false);
        }

        cache.populateFlags(item, player);

        UIEntryCombinerState cst;

        Combiner combiner = props.combinerName().getCombiner();
        boolean first = true;
        boolean anyAux = false;
        boolean allAux = true;
        Iterator<Curse> curseIterator = item.getCurses().keySet().iterator();

        Map<Curse, CurseData> curseList = item.getCurses();
        Curse currentCurse = null;
        boolean processedBase = false;

        while (!processedBase || currentCurse != null) {
            for (ObjectProperty prop : bound) {
                ObjectProperty.UIBinding binding = prop.getBoundEntries().stream()
                        .filter(b -> b.entry().equals(entryName)).findFirst().orElseThrow();
                int v = 0;
                int a = 0;

                if (binding.aux()) {
                    if (props.entryFlags.has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX))
                        continue;
                    anyAux = true;
                } else {
                    allAux = false;
                }

                switch (prop.getType()) {
                    case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                        ObjectModifier modifier;
                        modifier = prop.getPayload().getModifier(prop.getType());
                        if (currentCurse == null) {
                            if (player == null || player.getItemKnowledge().modifierIsKnown(modifier)
                                    || item.getModifierValue(modifier) == 0) {
                                v = item.getModifierValue(modifier);
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        } else if (curseList.get(currentCurse).getPower() != 0) {
                            int curseModifier = currentCurse.getItemObject().getModifierValue(modifier);
                            if (player == null || player.getItemKnowledge().modifierIsKnown(modifier)
                                    || curseModifier == 0) {
                                v = curseModifier;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        }
                    }
                    case OBJ_PROPERTY_FLAG -> {
                        ObjectFlag flag = prop.getPayload().getFlag(prop.getType());
                        if (currentCurse == null) {
                            if (player == null || ObjectUtils.objectFlagIsKnown(player, item, flag)) {
                                v = cache.getResolvedFlags().has(flag) ? 1 : 0;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        } else if (curseList.get(currentCurse).getPower() != 0) {
                            if (player == null || ObjectUtils.curseObjectFlagIsKnown(player, currentCurse, flag)) {
                                if (player != null) {
                                    Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
                                    flags.copyFrom(currentCurse.getItemObject().getFlags());
                                    flags.inter(currentCurse.getItemObject().getKnown().getFlags());
                                    if (currentCurse.getItemObject().getKind() != null) {
                                        if (currentCurse.getItemObject().getKind().isAware())
                                            flags.union(currentCurse.getItemObject().getKind().getFlags());
                                        if (currentCurse.getItemObject().getEgo() != null
                                                && currentCurse.getItemObject().easyKnow()) {
                                            flags.union(currentCurse.getItemObject().getEgo().getFlags());
                                            flags.diff(currentCurse.getItemObject().getEgo().getOffFlags());
                                        }
                                    }

                                    v = flags.has(flag) ? 1 : 0;
                                }
                                else
                                    v = currentCurse.getItemObject().hasFlag(flag) ? 1 : 0;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        }
                    }
                    case OBJ_PROPERTY_IGNORE -> {
                        ElementEnum element = prop.getPayload().getElement(prop.getType());
                        if (currentCurse == null) {
                            if (player == null || player.getItemKnowledge().objectElementIsKnown(player, item, element)) {
                                ElementInfo elementInfo = item.getElInfo().getOrDefault(element, null);

                                v = elementInfo != null && elementInfo.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE)
                                        ? 1 : 0;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        } else if (curseList.get(currentCurse).getPower() != 0) {
                            if (player == null || ObjectUtils.objectElementIsKnown(player, currentCurse, element)) {
                                ElementInfo elementInfo = currentCurse.getItemObject().getElInfo()
                                        .getOrDefault(element, null);
                                v = elementInfo != null && elementInfo.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE)
                                        ? 1 : 0;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        }
                    }
                    case OBJ_PROPERTY_RESIST, OBJ_PROPERTY_IMM, OBJ_PROPERTY_VULN -> {
                        ElementEnum element = prop.getPayload().getElement(prop.getType());
                        if (currentCurse == null) {
                            if (player == null || player.getItemKnowledge().objectElementIsKnown(player, item, element)) {
                                ElementInfo elementInfo = item.getElInfo().getOrDefault(element, null);
                                v = elementInfo != null ? elementInfo.getResLevel() : 0;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        } else if (curseList.get(currentCurse).getPower() != 0) {
                            if (player == null || ObjectUtils.objectElementIsKnown(player, currentCurse, element)) {
                                ElementInfo elementInfo = currentCurse.getItemObject().getElInfo()
                                        .getOrDefault(element, null);
                                v = elementInfo != null ? elementInfo.getResLevel() : 0;
                                if (v != 0 && binding.value() != null) {
                                    v = binding.value();
                                }
                            } else {
                                v = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                                a = Combiner.UI_ENTRY_UNKNOWN_VALUE;
                            }
                        }
                    }
                    default -> v = 0;
                }

                if (v != 0) {
                    if (binding.aux()) {
                        int t = a;
                        a = v;
                        v = t;
                    }
                    if (first) {
                        combiner = bindingByEntry.get(entryName).combinerName.init(v, a);
                        first = false;
                    } else {
                        combiner.accum(v, a);
                    }
                }
            }

            processedBase = true;
            currentCurse = curseIterator.hasNext() ? curseIterator.next() : null;
        }

        int accum = 0;
        int accumAux = 0;

        if (!first) {
            cst = combiner.finish();
            accum = cst.getAccum();
            accumAux = cst.getAccumAux();
        }

        return new UIEntryValue(allAux ? 0 : accum,
                anyAux ? accumAux : 0, false);
    }

    /**
     * Computes the combined display value(s) for one named UI entry on the player themself — the
     * port of C's {@code compute_ui_entry_values_for_player} in {@code ui-entry.c}.
     *
     * <p>Walks the entry's bound {@link PlayerProperty} list first. A {@code PROP_TYPE_PLAYER}
     * property only contributes if the player has the underlying {@link PlayerFlag} (C's
     * {@code player_has}); its value is either the bound fixed value, or — for the two abilities C
     * special-cases outside the ordinary value lookup — a computed one: {@code PF_FAST_SHOT}
     * contributes the character's level divided by three while a launcher that shoots arrows is
     * wielded, and {@code PF_BRAVERY_30} contributes {@code 1} once the character reaches level 30.
     * Any other valueless {@code PROP_TYPE_PLAYER} property contributes nothing, matching C's switch
     * having no default case. A {@code PROP_TYPE_OBJECT} property contributes whether
     * {@code cachedPlayerData} carries the corresponding {@link ObjectFlag} untimed (and, if the
     * entry is {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX}, whether it carries it timed, as the
     * auxiliary value), then separately accumulates a further contribution if the player's current
     * shape grants the flag and the player can identify it. A {@code PROP_TYPE_ELEMENT} property does
     * the same for the player's racial resistance level and, if applicable, the shape's element
     * modifier.
     *
     * <p>The entry's bound {@link ObjectProperty} list is then walked a second time for stat/modifier
     * properties only (C: {@code entry->n_obj_prop}, since stats and modifiers are not recorded as
     * abilities); each contributes the player's current shape modifier, plus — via
     * {@link #modifierToSkill} — any racial skill this modifier maps to, plus, for {@code OM_INFRA},
     * the race's own infravision, each as a separate combiner contribution.
     *
     * <p>Every contribution above is subject to the same auxiliary swap and
     * {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX} skip as {@link #computeForObject}, and where
     * the entry carries that flag the auxiliary half of a contribution comes from the player's
     * current timed effects, via {@link #getTimedModifierEffect}/{@link #getTimedElementEffect},
     * rather than from {@code cachedPlayerData}'s timed flags alone.
     *
     * <p>{@code cachedPlayerData} is filled by {@link CachedPlayerData#populateFlags(Player)}, which
     * does the work only on its first call (C's {@code *cache == NULL} branch): the player's untimed
     * and timed flags, with {@code TMD_TRAPSAFE} folded in as {@code OF_TRAP_IMMUNE}. A
     * {@code null} {@code cachedPlayerData} is accepted and replaced by a fresh one, but unlike C's
     * {@code **cache} the replacement cannot be handed back, so a caller that passes {@code null}
     * on every call recomputes the flags on every call.
     *
     * <p>Returns {@link Combiner#UI_ENTRY_VALUE_NOT_PRESENT} for both values if {@code entryName} is
     * not registered, {@code player} is {@code null}, or no bound property ever contributed;
     * otherwise the combiner's finished totals. Unlike {@link #computeForObject}, there is no
     * {@code 0} substitution for an all-auxiliary or no-auxiliary entry: both values are the
     * combiner's totals as they stand.
     *
     * <p>C stops with {@code assert(0)} for an entry with no combiner, before it reads any
     * binding. The port throws only for a {@code null} {@link CombinerName}; the
     * {@link CombinerName#NONE} sentinel passes the check and fails later with a
     * {@link NullPointerException} at the first contribution, or not at all if nothing contributes.
     * {@code hatch_embryo} in C and {@code UIEntryAssembler} here both refuse to finish an entry
     * with no combiner, so loaded data never reaches either state.
     *
     * <p>Function computeForPlayer coded before 260924, commented in full on 261006.
     *
     * @param entryName        the UI entry to compute a value for
     * @param player           the player being assessed, or {@code null}
     * @param cachedPlayerData cached untimed/timed flags for {@code player}, populated on first use
     *                         and reused across calls for the same player state
     * @return the entry's combined main and auxiliary values for {@code player}
     * @throws RuntimeException if the resolved entry's {@link CombinerName} is {@code null}
     */
    public static UIEntryValue computeForPlayer(String entryName, Player player, CachedPlayerData cachedPlayerData) {
        int val;
        int auxVal;
        EntryBinding props = bindingByEntry.getOrDefault(entryName, null);
        if (props == null) return new UIEntryValue(Combiner.UI_ENTRY_VALUE_NOT_PRESENT,
                Combiner.UI_ENTRY_VALUE_NOT_PRESENT, false);

        List<PlayerProperty> bound = props.playerProperties();
        UIEntryCombinerState combinerState = new UIEntryCombinerState(0, 0, 0, 0);
        Combiner combiner = null;
        boolean first;

        if (player == null) {
            val = Combiner.UI_ENTRY_VALUE_NOT_PRESENT;
            auxVal = Combiner.UI_ENTRY_VALUE_NOT_PRESENT;
            return new UIEntryValue(val, auxVal, false);
        }

        if (cachedPlayerData == null)
            cachedPlayerData = new CachedPlayerData();

        cachedPlayerData.populateFlags(player);
    
        first = true;

        CombinerName combinerName = props.combinerName();
        if (combinerName == null) {
            logger.fatal("Entry found with no valid combiner - exiting game.");
            throw new RuntimeException("Entry found with no valid combiner - exiting game.");
        }

        List<PlayerProperty> playerProperties = bound;
        if (playerProperties == null) playerProperties = new ArrayList<>();
        for (PlayerProperty prop : playerProperties) {
            PlayerFlag pFlag = prop.getpCode();
            ObjectFlag oFlag = prop.getoCode();
            ElementEnum eCode = prop.geteCode();

            PlayerProperty.BindUI bindUI = prop.getEntries().stream().filter(p -> p.uiEntry().equals(entryName))
                    .findFirst().orElse(null);

            if (bindUI == null)
                continue;

            BoundPlayerAbility boundPlayerAbility = new BoundPlayerAbility(prop, bindUI.value(),
                    !bindUI.special(), bindUI.aux());

            if (props.entryFlags().has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX) && bindUI.aux())
                continue;

            if (prop.getPlayerPropertyType() == PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER) {
                if (!player.getPlayerState().hasPFlag(pFlag)) {
                    continue;
                }

                if (boundPlayerAbility.isHaveValue()) {
                    int v = boundPlayerAbility.getValue();
                    int a = Combiner.UI_ENTRY_VALUE_NOT_PRESENT;

                    if (boundPlayerAbility.isAux()) {
                        int t = v;
                        v = a;
                        a = t;
                    }
                    if (first) {
                        combiner = combinerName.init(v, a);
                        first = false;
                    } else {
                        combiner.accum(v, a);
                    }
                } else {
                    int v;
                    int a;
                    ItemObject launcher;

                    switch (pFlag) {
                        case PF_FAST_SHOT -> {
                            launcher = player.getPlayerBody().equippedItemBySlotName("shooting");
                            if (launcher != null && launcher.getKind().getKindFlags()
                                    .has(ObjectKindFlag.KF_SHOOTS_ARROWS)) {
                                v = player.getLevel() / 3;
                                a = 0;
                            } else {
                                v = 0;
                                a = 0;
                            }
                            if (boundPlayerAbility.isAux()) {
                                int t = v;
                                v = a;
                                a = t;
                            }
                            if (first) {
                                combiner = combinerName.init(v, a);
                                first = false;
                            } else {
                                combiner.accum(v, a);
                            }
                        }

                        case PF_BRAVERY_30 -> {
                            v = player.getLevel() >= 30 ? 1 : 0;
                            a = 0;
                            if (boundPlayerAbility.isAux()) {
                                int t = v;
                                v = a;
                                a = t;
                            }
                            if (first) {
                                combiner = combinerName.init(v, a);
                                first = false;
                            } else {
                                combiner.accum(v, a);
                            }
                        }

                        default -> {
                        }
                    }
                }
            } else if (prop.getPlayerPropertyType() == PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT) {
                int v = cachedPlayerData.getUntimedFlags().has(oFlag) ? 1 : 0;
                int a;

                if (props.entryFlags.has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX)) {
                    a = cachedPlayerData.hasTimedFlag(oFlag) ? 1 : 0;
                } else {
                    a = 0;
                }
                if (boundPlayerAbility.isAux()) {
                    int t = v;
                    v = a;
                    a = t;
                }
                if (first) {
                    combiner = combinerName.init(v, a);
                    first = false;
                } else {
                    combiner.accum(v, a);
                }

                v = player.getShape().getFlags().has(oFlag) ? 1 : 0;
                a = 0;
                if (v != 0 && player.getItemKnowledge().flagIsKnown(oFlag)) {
                    if (boundPlayerAbility.isAux()) {
                        int t = v;
                        v = a;
                        a = t;
                    }
                    combiner.accum(v, a);
                }
            } else if (prop.getPlayerPropertyType() == PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT) {
                int v = player.getRace().getResistanceLevel(eCode);
                int a;

                if (props.entryFlags.has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX)) {
                    a = getTimedElementEffect(player, eCode);
                } else {
                    a = 0;
                }
                if (boundPlayerAbility.isAux()) {
                    int t = v;
                    v = a;
                    a = t;
                }
                if (first) {
                    combiner = combinerName.init(v, a);
                    first = false;
                } else {
                    combiner.accum(v, a);
                }
                ElementInfo elementInfo = player.getShape().getElementValueModifiers().get(eCode);
                if (elementInfo == null)
                    v = 0;
                else
                    v = elementInfo.getResLevel();
                a = 0;
                if (v != 0 && player.getItemKnowledge().resistanceIsKnown(eCode)) {
                    if (boundPlayerAbility.isAux()) {
                        int t = v;
                        v = a;
                        a = t;
                    }
                    combiner.accum(v, a);
                }
            }
        }

        List<ObjectProperty> objectProperties = props.objectProperties();
        if (objectProperties == null) objectProperties = new ArrayList<>();
        for (ObjectProperty prop : objectProperties) {
            PlayerSkill skillIndex;
            int skillCnvNum;
            int skillCnvDen;
            int v;
            int a;

            ObjectProperty.UIBinding binding = prop.getBoundEntries().stream().filter(e -> e.entry().equals(entryName))
                    .findFirst().orElse(null);

            ObjectModifier om = prop.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD);

            if (binding != null && binding.aux() && props.entryFlags.has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX))
                continue;

            switch (prop.getType()) {
                case OBJ_PROPERTY_STAT, OBJ_PROPERTY_MOD -> {
                    if (player.getShape().getObjectValueModifiers() != null)
                        v = player.getShape().getObjectValueModifiers().getOrDefault(om, 0);
                    else
                        v = 0;
                    if (props.entryFlags.has(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX)) {
                        a = getTimedModifierEffect(player, om);
                    } else {
                        a = 0;
                    }
                    if (binding != null && binding.aux()) {
                        int t = v;
                        v = a;
                        a = t;
                    }
                    if (first) {
                        combiner = combinerName.init(v, a);
                        first = false;
                    } else {
                        combiner.accum(v, a);
                    }
                    // Racial information
                    PlayerSkillAndTwoInts result = modifierToSkill(prop);
                    skillIndex = result.skill();
                    skillCnvNum = result.skillToModNum();
                    skillCnvDen = result.skillToModDen();

                    if (skillIndex != PlayerSkill.SKILL_NONE && skillIndex != PlayerSkill.SKILL_MAX) {
                        v = player.getRace().getSkill(skillIndex) * skillCnvNum / skillCnvDen;
                        a = 0;
                        if (binding != null && binding.aux()) {
                            int t = v;
                            v = a;
                            a = t;
                        }
                        combiner.accum(v, a);
                    }

                    // Handle Infravision separately
                    if (om == ObjectModifier.OM_INFRA) {
                        v = player.getRace().getInfravision();
                        a = 0;
                        if (binding != null && binding.aux()) {
                            int t = v;
                            v = a;
                            a = t;
                        }
                        combiner.accum(v, a);
                    }
                }
                default -> {
                }
            }
        }
        if (first) {
            val = Combiner.UI_ENTRY_VALUE_NOT_PRESENT;
            auxVal = Combiner.UI_ENTRY_VALUE_NOT_PRESENT;
        } else {
            UIEntryCombinerState cst = combiner.finish();
            val = cst.getAccum();
            auxVal = cst.getAccumAux();
        }

        return new UIEntryValue(val, auxVal, false);
    }

    /**
     * Maps an {@link ObjectModifier} bound as a stat/mod property to the racial skill it corresponds
     * to and the fraction that converts that skill into the modifier's units — the port of C's
     * {@code modifier_to_skill} in {@code ui-entry.c}. C takes the modifier's index and returns
     * {@code -1} for no skill; the port takes the property and returns {@link PlayerSkill#SKILL_NONE}.
     * Only {@code OM_TUNNEL} maps to a skill
     * ({@link PlayerSkill#SKILL_DIGGING}, converted by dividing by 20); every other modifier returns
     * {@link PlayerSkill#SKILL_NONE} with an identity conversion (1/1), matching C's {@code default}
     * branch. C's commented-out {@code OM_STEALTH}/search cases are left out here too, for the same
     * reason C gives: the racial contribution to those was never part of what the second character
     * screen showed.
     *
     * <p>Function modifierToSkill coded before 260924, commented in full on 261006.
     *
     * @param prop the stat/mod {@link ObjectProperty} to map
     * @return the corresponding skill (or {@link PlayerSkill#SKILL_NONE}) and its conversion fraction
     */
    private static PlayerSkillAndTwoInts modifierToSkill(ObjectProperty prop) {
        ObjectPropertyTypeWrapper wrapper = prop.getPayload();
        return switch (wrapper.getModifier(ObjPropertyType.OBJ_PROPERTY_MOD)) {
            case OM_TUNNEL -> {
                yield new PlayerSkillAndTwoInts(PlayerSkill.SKILL_DIGGING, 1, 20);
            }
            default -> {
                yield new PlayerSkillAndTwoInts(PlayerSkill.SKILL_NONE, 1, 1);
            }
        };
    }

    /**
     * Computes the timed-effect contribution for a stat/modifier property, used as the auxiliary
     * value when an entry is {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX} — the port of C's
     * {@code get_timed_modifier_effect} in {@code ui-entry.c}, which the C comment notes mirrors
     * the equivalent calculations in {@code player-calcs.c}. {@code OM_BLOWS} yields
     * {@link TimedEffect#TMD_BLOODLUST} divided by 20 while active; {@code OM_INFRA} yields 5 while
     * {@link TimedEffect#TMD_SINFRA} is active; {@code OM_SPEED} yields 10 while
     * {@link TimedEffect#TMD_FAST} or {@link TimedEffect#TMD_SPRINT} is active, adjusted by -5 for
     * {@link TimedEffect#TMD_STONESKIN}, -10 for {@link TimedEffect#TMD_SLOW} and +10 for
     * {@link TimedEffect#TMD_TERROR}; {@code OM_STEALTH} yields 10 while
     * {@link TimedEffect#TMD_STEALTH} is active. Any other modifier yields 0.
     *
     * <p>Function getTimedModifierEffect coded before 260924, commented in full on 261006.
     *
     * @param player the player whose timed effects are consulted
     * @param om     the modifier to compute the timed contribution for
     * @return the timed contribution to {@code om}, which may be negative
     */
    private static int getTimedModifierEffect(Player player, ObjectModifier om) {
        return switch (om) {
            case OM_BLOWS -> {
                yield player.getTimedEffect(TimedEffect.TMD_BLOODLUST) != 0
                        ? player.getTimedEffect(TimedEffect.TMD_BLOODLUST) / 20
                        : 0;
            }

            case OM_INFRA -> {
                yield player.getTimedEffect(TimedEffect.TMD_SINFRA) != 0
                        ? 5
                        : 0;
            }

            case OM_SPEED -> {
                int result = player.getTimedEffect(TimedEffect.TMD_FAST) != 0
                        || player.getTimedEffect(TimedEffect.TMD_SPRINT) != 0
                        ? 10
                        : 0;
                if (player.getTimedEffect(TimedEffect.TMD_STONESKIN) != 0) {
                    result -= 5;
                }
                if (player.getTimedEffect(TimedEffect.TMD_SLOW) != 0) {
                    result -= 10;
                }
                if (player.getTimedEffect(TimedEffect.TMD_TERROR) != 0) {
                    result += 10;
                }
                yield result;
            }

            case OM_STEALTH -> {
                yield player.getTimedEffect(TimedEffect.TMD_STEALTH) != 0
                        ? 10
                        : 0;
            }

            default -> 0;
        };
    }

    /**
     * Computes the timed-effect contribution for an element resistance property, used as the
     * auxiliary value when an entry is {@link ChannelEntryFlag#ENTRY_FLAG_TIMED_AS_AUX} — the port
     * of C's {@code get_timed_element_effect} in {@code ui-entry.c}. Scans every
     * {@link TimedEffect} currently active on {@code player} and returns 1 if any of them temporarily
     * resists {@code eCode} (per {@link PlayerRegistry#getPlayerTimedEffects()}'s recorded
     * {@code getTempResist()}), otherwise 0. {@link TimedEffect#TMD_NONE} is skipped explicitly since
     * it is never itself an active effect; C relies on {@code p->timed[TMD_NONE]} always being zero to
     * the same end. An effect that resists nothing carries {@code ELEM_NONE} here where C stores
     * {@code -1}; neither equals a real element, so such an effect never matches. A timed effect with
     * no loaded {@link PlayerTimedEffect} is skipped, where C reads its zeroed table slot.
     *
     * <p>Function getTimedElementEffect coded before 260924, commented in full on 261006.
     *
     * @param player the player whose timed effects are consulted
     * @param eCode  the element to check for a temporary resistance
     * @return 1 if a currently active timed effect resists {@code eCode}, otherwise 0
     */
    private static int getTimedElementEffect(Player player, ElementEnum eCode) {
        for (TimedEffect tmd : TimedEffect.values()) {
            if (tmd == TimedEffect.TMD_NONE) continue;

            PlayerTimedEffect playerTimedEffect = PlayerRegistry.lookupPlayerTimedEffect(tmd);
            if (playerTimedEffect == null) continue;
            if (player.getTimedEffect(tmd) != 0 && playerTimedEffect.getTempResist() == eCode) {
                return 1;
            }
        }

        return 0;
    }

    /**
     * Reports whether every property or ability bound to a UI entry is currently known to the
     * player — the port of C's {@code is_ui_entry_for_known_rune} in {@code ui-entry.c}. A
     * stat/mod property is known if {@link KnownObject#modifierIsKnown} says so for its modifier; a
     * flag property if {@link KnownObject#flagIsKnown} says so for its flag; an ignore/resist/vuln/imm
     * property if {@link KnownObject#resistanceIsKnown} says so for its element. Any other object
     * property type counts as unknown, matching C's {@code default} branch.
     *
     * <p>Of the bound {@link PlayerProperty} list, a {@code PROP_TYPE_PLAYER} property is skipped —
     * C's comment explains it is "not so easy to associate with a rune", so it cannot make the entry
     * read as unknown. A {@code PROP_TYPE_OBJECT} property is known if its {@link ObjectFlag} is
     * known; a {@code PROP_TYPE_ELEMENT} property is known if its element's resistance is known,
     * read straight off {@link KnownObject#getElementResistInfo()} (C's
     * {@code p->obj_k->el_info[ind].res_level}). Any other player-property type counts as unknown.
     *
     * <p>C's two loops stop as soon as {@code result} turns {@code false}; the port keeps walking
     * to the end. The walk reads state and changes nothing, so the answer is the same.
     *
     * <p>Returns {@code true} immediately if {@code entryName} has no registered
     * {@link EntryBinding}. C's function starts from {@code result = true} and its loops can only
     * ever set it to {@code false}, so an entry with nothing bound loops zero times and reads as
     * known. C has no separate "no such entry" state (it is handed an {@code entry} pointer), and
     * {@link #addEntryBinding} is only called for entries that have at least one binding, so an
     * unregistered name stands in for C's entry with empty bound arrays. A registered entry whose
     * lists are empty or {@code null} gives the same answer.
     *
     * <p>Function isKnownRune coded before 260924, commented in full on 261006.
     *
     * @param entryName the UI entry to test
     * @param player    the player whose knowledge is consulted
     * @return true if every property and ability bound to {@code entryName} is known to {@code player}
     */
    public static boolean isKnownRune(String entryName, Player player) {
        boolean result = true;
        if (!bindingByEntry.containsKey(entryName))
            return true;

        EntryBinding binding = bindingByEntry.get(entryName);

        // mark it as known if all the properties/abilities bound to the entry are known
        List<ObjectProperty> objectProperties = binding.objectProperties();
        if (objectProperties == null) objectProperties = new ArrayList<>();
        for (ObjectProperty prop : objectProperties) {
            switch (prop.getType()) {
                case OBJ_PROPERTY_STAT,
                     OBJ_PROPERTY_MOD -> {
                    if (!player.getItemKnowledge().modifierIsKnown(prop.getPayload().getModifier(prop.getType())))
                        result = false;
                }
                case OBJ_PROPERTY_FLAG -> {
                    if (!player.getItemKnowledge().flagIsKnown(prop.getPayload().getFlag(prop.getType())))
                        result = false;
                }
                case OBJ_PROPERTY_IGNORE,
                     OBJ_PROPERTY_RESIST,
                     OBJ_PROPERTY_VULN,
                     OBJ_PROPERTY_IMM -> {
                    if (!player.getItemKnowledge().resistanceIsKnown(prop.getPayload().getElement(prop.getType())))
                        result = false;
                }
                default -> result = false;
            }
        }
        List<PlayerProperty> playerProperties = binding.playerProperties();
        if (playerProperties == null) playerProperties = new ArrayList<>();
        for (PlayerProperty prop : playerProperties) {
            if (prop.getPlayerPropertyType() == PlayerProperty.PlayerPropertyType.PROP_TYPE_PLAYER)
                continue;
            else if (prop.getPlayerPropertyType() == PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT) {
                if (!player.getItemKnowledge().flagIsKnown(prop.getoCode())) {
                    result = false;
                }
            } else if (prop.getPlayerPropertyType() == PlayerProperty.PlayerPropertyType.PROP_TYPE_ELEMENT) {
                if (!player.getItemKnowledge().getElementResistInfo().get(prop.geteCode())) {
                    result = false;
                }
            } else result = false;
        }

        return result;
    }

    /**
     * The result of {@link #modifierToSkill}: the racial skill an object modifier corresponds to, if
     * any, and the numerator/denominator that converts a skill value into the modifier's units — the
     * port of C's three out-parameters to {@code modifier_to_skill} in {@code ui-entry.c} bundled
     * into one value.
     *
     * <p>Record PlayerSkillAndTwoInts coded before 260924, commented in full on 261006.
     *
     * @param skill         the corresponding racial skill, or {@link PlayerSkill#SKILL_NONE}
     * @param skillToModNum the conversion fraction's numerator
     * @param skillToModDen the conversion fraction's denominator
     */
    private record PlayerSkillAndTwoInts(PlayerSkill skill, int skillToModNum, int skillToModDen) {
    }

    /**
     * The object properties and player properties bound to one named UI entry, together with the
     * combiner and flags configured for that entry — the value type stored in
     * {@link #bindingByEntry}. The port's counterpart to the bound-property/bound-ability arrays and
     * the combiner/flags fields that together make up C's {@code struct ui_entry}
     * in {@code ui-entry.c}.
     *
     * <p>Record EntryBinding coded before 260924, commented in full on 261006.
     *
     * @param objectProperties the object properties bound to this entry, or {@code null}
     * @param playerProperties the player properties bound to this entry, or {@code null}
     * @param combinerName     the combiner used to merge this entry's bound property values
     * @param entryFlags       this entry's configured flags
     */
    private record EntryBinding(List<ObjectProperty> objectProperties,
                                List<PlayerProperty> playerProperties,
                                CombinerName combinerName,
                                FlagView<ChannelEntryFlag> entryFlags) {
    }
}
