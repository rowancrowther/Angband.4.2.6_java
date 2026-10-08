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
import org.jetbrains.annotations.*;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.objects.*;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

/**
 * Runtime holder for all object-domain game data — object bases, object kinds, slays, brands,
 * curses, item objects, activations, ego items, artifacts, chest traps, and object properties —
 * together with the derived {@code *Max} counters and the lookups the running game queries.
 *
 * <p>Unlike the other registries, the object-kind table is a live, mutable registry rather than a
 * load-once list: {@link #addObjectKind} appends a kind and indexes it in {@link #kindsByTvalSval}
 * (the tval&rarr;sval&rarr;kind index kept in sync with {@code objectKinds}), and {@link #reset}
 * clears both so a re-initialization does not double-register. {@link #unknownGoldKind} and
 * {@link #unknownItemKind} are the sentinel kinds for unidentified gold and items.
 *
 * <p>This is the read side of the object slice: it is populated at startup by
 * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.ObjectDataLoader} — ordinary kinds
 * registered by {@code loadItemObjects} and special artifact kinds synthesized into the same table
 * by {@code loadArtifacts}. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * <p>Where C keeps this data in parallel global arrays indexed by a number ({@code k_info},
 * {@code curses}, {@code slays}, {@code brands}, {@code obj_properties}) and looks entries up by
 * walking them, the port keeps lists of objects and answers lookups with the object itself, or
 * {@code null} for a miss. C's index-0 placeholder entries, and the {@code *_max} counters that
 * include them, have no counterpart here. The lookups that do have a C original are
 * {@code lookup_kind} and {@code lookup_sval} ({@code obj-util.c}), {@code lookup_curse}
 * ({@code obj-curse.c}), {@code lookup_obj_property} ({@code obj-properties.c}), {@code findact}
 * ({@code obj-init.c}) and {@code max_runes} ({@code obj-knowledge.c}). The slay and brand lookups
 * are loops written inline in C's parsers that the port has named, and the base lookups replace
 * C's direct {@code kb_info[tval]} indexing.
 *
 * <p>Every lookup that walks a list, except {@link #lookupObjectProperty}, throws
 * {@link IllegalStateException} if the list it needs has not been set yet, where C would simply
 * read a zeroed array. That guard, and the logger call that accompanies it, is the port's alone.
 *
 * <p>The tables the power calculation prices by ({@link #archery}, {@link #flagSets},
 * {@link #elementSets}, {@link #elementPowers} and {@link #abilityPower}) and the {@code obj-power.h}
 * constants live here too, because C keeps them next to the object data they describe. The
 * figures in {@link #archery}, {@link #flagSets}, {@link #elementSets} and {@link #elementPowers}
 * are not written here: {@code GameConstants} fills them in, in the order C declares its rows.
 *
 * <p>Class ObjectRegistry coded before 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public class ObjectRegistry {
    /**
     * Assumed off-weapon damage, used to boost an object that grants extra blows without being a
     * weapon itself. C's {@code NONWEAP_DAMAGE} ({@code obj-power.h}), "fudge to boost extra blows".
     *
     * <p>Field NONWEAP_DAMAGE coded before 261008, commented in full on 261008.
     */
    public static final int NONWEAP_DAMAGE = 15;

    /*
     * The object power constants - the port of C's obj-power.h. Four of them (damage, to-hit, base
     * AC and to-AC) are doubled, so that the halves the algorithm actually wants survive integer
     * arithmetic; every use divides by two afterwards. C's header says the same in its comments,
     * which are reproduced on each constant below.
     */
    /**
     * Assumed damage for off-weapon combat flags - what a non-weapon carrying brands, slays or
     * combat modifiers is treated as hitting for. C's {@code WEAP_DAMAGE}.
     *
     * <p>Field WEAP_DAMAGE coded before 261008, commented in full on 261008.
     */
    public static final int WEAP_DAMAGE = 12;
    /**
     * Flat power every piece of jewellery starts from. C's {@code BASE_JEWELRY_POWER} (spelled the
     * American way there).
     *
     * <p>Field BASE_JEWELERY_POWER coded before 261008, commented in full on 261008.
     */
    public static final int BASE_JEWELERY_POWER = 4;
    /**
     * Flat power every armour item starts from, for halving acid damage. C's
     * {@code BASE_ARMOUR_POWER}.
     *
     * <p>Field BASE_ARMOUR_POWER coded before 261008, commented in full on 261008.
     */
    public static final int BASE_ARMOUR_POWER = 1;
    /**
     * Power per point of damage, doubled - the algorithm wants 2.5. C's {@code DAMAGE_POWER}.
     *
     * <p>Field DAMAGE_POWER coded before 261008, commented in full on 261008.
     */
    public static final int DAMAGE_POWER = 5;
    /**
     * Power per point of to-hit, doubled - the algorithm wants 1.5. C's {@code TO_HIT_POWER}.
     *
     * <p>Field TO_HIT_POWER coded before 261008, commented in full on 261008.
     */
    public static final int TO_HIT_POWER = 3;
    /**
     * Power per point of base armour class, doubled - the algorithm wants 1. C's
     * {@code BASE_AC_POWER}.
     *
     * <p>Field BASE_AC_POWER coded before 261008, commented in full on 261008.
     */
    public static final int BASE_AC_POWER = 2;
    /**
     * Power per point of to-armour, doubled - the algorithm wants 1. C's {@code TO_AC_POWER}.
     *
     * <p>Field TO_AC_POWER coded before 261008, commented in full on 261008.
     */
    public static final int TO_AC_POWER = 2;
    /**
     * The number of blows a melee weapon is assumed to land per turn. Launchers are rescaled by
     * this so that the two can be compared. C's {@code MAX_BLOWS}.
     *
     * <p>Field MAX_BLOWS coded before 261008, commented in full on 261008.
     */
    public static final int MAX_BLOWS = 5;
    /**
     * Numerator of the weight adjustment for an object with no base armour class. One, so the
     * multiply is a no-op; it exists to mirror C's pairing of a numerator with each denominator.
     *
     * <p>Field WGT_POWER_NUM_NOBASEAC coded before 261008, commented in full on 261008.
     */
    public static final int WGT_POWER_NUM_NOBASEAC = 1;
    /**
     * Denominator of the weight adjustment for an object with no base armour class - five pounds
     * lighter than standard adds one point of power, and heavier subtracts. Easily confused with
     * {@link #WGT_POWER_NUM_NOBASEAC} beside it, which is the wrong one to divide by.
     *
     * <p>Field WGT_POWER_DEN_NOBASEAC coded before 261008, commented in full on 261008.
     */
    public static final int WGT_POWER_DEN_NOBASEAC = 50;
    /**
     * Numerator of the weight adjustment for a {@code THROWING} object, where heavier is better.
     * C explains the figure: the throwing multiplier is {@code 2 + weight / 12} and shooting rates
     * at 30, so throwing - typically less useful - is priced at half that.
     *
     * <p>Field WGT_POWER_NUM_THROW coded before 261008, commented in full on 261008.
     */
    public static final int WGT_POWER_NUM_THROW = 15;
    /**
     * Denominator of the weight adjustment for a {@code THROWING} object. C's
     * {@code WGT_POWER_DEN_THROW}.
     *
     * <p>Field WGT_POWER_DEN_THROW coded before 261008, commented in full on 261008.
     */
    public static final int WGT_POWER_DEN_THROW = 12;
    /**
     * The refusal value. Added rather than a price: an object that reaches it is not meant to
     * exist, and the power calculation returns early once it is exceeded.
     *
     * <p>Field INHIBIT_POWER coded before 261008, commented in full on 261008.
     */
    public static final int INHIBIT_POWER = 20000;
    /**
     * Extra blows at or above which an object is refused - so the most it may carry is one less.
     *
     * <p>Field INHIBIT_BLOWS coded before 261008, commented in full on 261008.
     */
    public static final int INHIBIT_BLOWS = 3;
    /**
     * Extra shooting might at or above which an object is refused.
     *
     * <p>Field INHIBIT_MIGHT coded before 261008, commented in full on 261008.
     */
    public static final int INHIBIT_MIGHT = 4;
    /**
     * Extra shots at or above which an object is refused.
     *
     * <p>Field INHIBIT_SHOTS coded before 261008, commented in full on 261008.
     */
    public static final int INHIBIT_SHOTS = 21;
    /**
     * To-armour above which each further point is priced again, on top of the ordinary rate.
     *
     * <p>Field HIGH_TO_AC coded before 261008, commented in full on 261008.
     */
    public static final int HIGH_TO_AC = 26;
    /**
     * To-armour above which each further point is priced twice again.
     *
     * <p>Field VERYHIGH_TO_AC coded before 261008, commented in full on 261008.
     */
    public static final int VERYHIGH_TO_AC = 36;
    /**
     * To-armour at or above which an object is refused.
     *
     * <p>Field INHIBIT_AC coded before 261008, commented in full on 261008.
     */
    public static final int INHIBIT_AC = 56;
    /**
     * To-hit above which the randart generator treats the bonus as high. Not read by the power
     * calculation itself.
     *
     * <p>Field HIGH_TO_HIT coded before 261008, commented in full on 261008.
     */
    public static final int HIGH_TO_HIT = 16;
    /**
     * To-hit above which the randart generator treats the bonus as very high.
     *
     * <p>Field VERYHIGH_TO_HIT coded before 261008, commented in full on 261008.
     */
    public static final int VERYHIGH_TO_HIT = 26;
    /**
     * To-damage above which the randart generator treats the bonus as high.
     *
     * <p>Field HIGH_TO_DAM coded before 261008, commented in full on 261008.
     */
    public static final int HIGH_TO_DAM = 16;
    /**
     * To-damage above which the randart generator treats the bonus as very high.
     *
     * <p>Field VERYHIGH_TO_DAM coded before 261008, commented in full on 261008.
     */
    public static final int VERYHIGH_TO_DAM = 26;
    /**
     * Divisor that brings a single missile down to a fair share of a weapon's power - a stack of
     * this many is reckoned equal to a weapon of the same damage output. C notes it is used for
     * torches too.
     *
     * <p>Field AMMO_RESCALER coded before 261008, commented in full on 261008.
     */
    public static final int AMMO_RESCALER = 20;
    /**
     * Shared logger for the loaders and lookups below, which report a data problem rather than
     * failing silently.
     *
     * <p>Field logger coded before 261008, commented in full on 261008.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The loaded chest traps, in file order. Order is load-bearing twice over: each trap's pval bit
     * is its position, and {@code pick_one_chest_trap} draws only from the entries <em>after</em> the
     * first, which is always the "locked" no-trap entry. C holds the same data as a linked list
     * headed by its global {@code chest_traps} ({@code obj-chest.c}).
     *
     * <p>Final, and refilled in place by {@link #setChestTraps}.
     *
     * <p>Field chestTraps coded before 261008, commented in full on 261008.
     */
    private static final List<ChestTrap> chestTraps = new ArrayList<>();
    /**
     * The launcher-and-ammo pricing assumptions, keyed by ammunition type - the port of C's
     * {@code archery[]} table ({@code obj-power.c}). See {@link Archery} for what the rows mean
     * and why the port keys them where C indexes.
     *
     * <p>Filled in by {@code GameConstants} with C's three rows (shot, arrow, bolt); null until it
     * has run.
     *
     * <p>Field archery coded before 261008, commented in full on 261008.
     */
    public static Map<TValue, Archery> archery;
    /**
     * The flag families that are worth more held together, keyed by family - the port of C's
     * {@code flag_sets[]} table ({@code obj-power.c}).
     *
     * <p>Shared mutable state: each row carries a count that the power calculation zeroes and
     * increments in place, exactly as C does on its static table. Two power calculations must
     * therefore not interleave.
     *
     * <p>Filled in by {@code GameConstants} with C's three rows (sustains, protections, misc
     * abilities); null until it has run.
     *
     * <p>Field flagSets coded before 261008, commented in full on 261008.
     */
    public static Map<ObjectFlagType, FlagSet> flagSets;
    /**
     * The elemental protection combinations that are worth more held together - the port of C's
     * {@code element_sets[]} table ({@code obj-power.c}). Counted in place like
     * {@link #flagSets}, and with the same caution.
     *
     * <p>A list rather than a map because two rows share a type: C's three rows are immunities and
     * low resists, both {@code T_LRES}, then high resists. List order is C's row order. Filled in by
     * {@code GameConstants}; null until it has run.
     *
     * <p>Field elementSets coded before 261008, commented in full on 261008.
     */
    public static List<ElementSet> elementSets;
    /**
     * What each element is worth to an object that ignores, resists, is immune to or is vulnerable
     * to it - the port of C's {@code el_powers[]} table ({@code obj-power.c}). Read only; unlike
     * the two set tables above, nothing writes to these rows.
     *
     * <p>Held in C's row order: acid, electricity, fire, cold, then the nine high resists. Filled in
     * by {@code GameConstants}; null until it has run.
     *
     * <p>Field elementPowers coded before 261008, commented in full on 261008.
     */
    public static List<ElementPowers> elementPowers;
    /**
     * Sentinel kind representing an unidentified pile of gold.
     *
     * <p>The {@code <unknown treasure>} entry of {@code object.txt}; C's {@code unknown_gold_kind}
     * ({@code obj-init.c}). Set by {@link #loadUnknownKinds}, cleared by {@link #reset}, and null
     * outside that window.
     *
     * <p>Field unknownGoldKind coded before 261008, commented in full on 261008.
     */
    public static ObjectKind unknownGoldKind;
    /**
     * Sentinel kind representing an unidentified item.
     *
     * <p>The {@code <unknown item>} entry of {@code object.txt}; C's {@code unknown_item_kind}
     * ({@code obj-init.c}). Set by {@link #loadUnknownKinds}, cleared by {@link #reset}, and null
     * outside that window.
     *
     * <p>Field unknownItemKind coded before 261008, commented in full on 261008.
     */
    public static ObjectKind unknownItemKind;
    /**
     * Boost ratings for combinations of ability bonuses, indexed by the combined bonus divided by
     * ten - the port of C's {@code ability_power[]} ({@code obj-power.c}).
     *
     * <p>Rises faster than linearly, so an object with several large modifiers is worth more than
     * the sum of them; the first seven entries are zero, which is what makes a small total worth no
     * bonus at all. C's comment notes the table runs to +24 and that anything higher is inhibited.
     *
     * <p>Field abilityPower coded before 261008, commented in full on 261008.
     */
    public static int[] abilityPower = new int[]{0, 0, 0, 0, 0, 0, 0, 2, 4, 6, 8,
            12, 16, 20, 24, 30, 36, 42, 48, 56, 64,
            74, 84, 96, 110};
    /**
     * Number of ordinary object kinds loaded from {@code object.txt} — the artifact-synthesis ceiling.
     *
     * <p>Set by {@link #updateObjectBaseKindMax} once the file's kinds are registered and before any
     * special artifact kind is, so a kind whose index is at or above it was synthesized. The port's
     * equivalent of C's {@code ordinary_kind_max} ({@code obj-init.c}, {@code finish_parse_object}),
     * and one lower than it: C's figure also counts a spare slot that {@code k_info} keeps after its
     * last kind.
     *
     * <p>Field objectBaseKindMax coded before 261008, commented in full on 261008.
     */
    private static int objectBaseKindMax;
    /**
     * Number of loaded artifacts.
     *
     * <p>Set from {@link #setArtifacts}; read through {@link #getArtifactKindMax}. The port's
     * equivalent of C's {@code a_max}, without the index-0 placeholder C counts in it.
     *
     * <p>Field artifactKindMax coded before 261008, commented in full on 261008.
     */
    private static int artifactKindMax;
    /**
     * Number of loaded ego items (set from {@link #setEgoItems}).
     *
     * <p>The port's equivalent of C's {@code e_max}, without the index-0 placeholder C counts in it.
     *
     * <p>Field egoItemKindMax coded before 261008, commented in full on 261008.
     */
    private static int egoItemKindMax;
    /**
     * Number of activations available to random artifacts.
     *
     * <p>Set from {@link #setActivations}, so it is the size of the whole activation list rather
     * than a separately maintained figure.
     *
     * <p>Field randartActivationsMax coded before 261008, commented in full on 261008.
     */
    private static int randartActivationsMax;
    /**
     * Number of loaded curses (set from {@link #setCurses}).
     *
     * <p>The port's equivalent of C's {@code curse_max}, without the index-0 placeholder C counts in
     * it.
     *
     * <p>Field curseMax coded before 261008, commented in full on 261008.
     */
    private static int curseMax;
    /**
     * Number of loaded slays (set from {@link #setSlays}).
     *
     * <p>The port's equivalent of C's {@code slay_max}, without the index-0 placeholder C counts in
     * it.
     *
     * <p>Field slayMax coded before 261008, commented in full on 261008.
     */
    private static int slayMax;
    /**
     * Number of loaded brands (set from {@link #setBrands}).
     *
     * <p>The port's equivalent of C's {@code brand_max}, without the index-0 placeholder C counts in
     * it.
     *
     * <p>Field brandMax coded before 261008, commented in full on 261008.
     */
    private static int brandMax;
    /**
     * Number of loaded object properties (set from {@link #setObjectProperties}).
     *
     * <p>The port's equivalent of C's {@code property_max}, without the index-0 placeholder C counts
     * in it.
     *
     * <p>Field objectPropertyMax coded before 261008, commented in full on 261008.
     */
    private static int objectPropertyMax;
    /**
     * The loaded object bases, resolved by name/tval via {@link #lookupObjectBase}.
     *
     * <p>Null until {@link #setObjectBases} runs; the lookups that read it throw rather than treat
     * null as empty. Stands in for C's {@code kb_info[]}, which C indexes directly by tval.
     *
     * <p>Field objectBases coded before 261008, commented in full on 261008.
     */
    private static List<ObjectBase> objectBases;
    /**
     * The loaded slays, resolved by code via {@link #lookupSlay}.
     *
     * <p>Null until {@link #setSlays} runs. Stands in for C's {@code slays[]}.
     *
     * <p>Field slays coded before 261008, commented in full on 261008.
     */
    private static List<Slay> slays;
    /**
     * The loaded brands, resolved by code via {@link #lookupBrandCode}.
     *
     * <p>Null until {@link #setBrands} runs. Stands in for C's {@code brands[]}.
     *
     * <p>Field brands coded before 261008, commented in full on 261008.
     */
    private static List<Brand> brands;
    /**
     * The loaded curses, resolved by name via {@link #lookupCurse}.
     *
     * <p>Null until {@link #setCurses} runs. Stands in for C's {@code curses[]}.
     *
     * <p>Field curses coded before 261008, commented in full on 261008.
     */
    private static List<Curse> curses;
    /**
     * The loaded item-object templates.
     *
     * <p>Null until {@link #setItemObjects} runs, and, unlike most lists here, no lookup reads it
     * and no counter records its size; it is only handed out by {@link #getItemObjects}.
     *
     * <p>Field itemObjects coded before 261008, commented in full on 261008.
     */
    private static List<ItemObject> itemObjects;
    /**
     * The loaded activations, resolved by name via {@link #lookupActivation}.
     *
     * <p>Null until {@link #setActivations} runs. Stands in for C's {@code activations} list,
     * which {@code findact} walks.
     *
     * <p>Field activations coded before 261008, commented in full on 261008.
     */
    private static List<Activation> activations;
    /**
     * The loaded ego-item templates.
     *
     * <p>Null until {@link #setEgoItems} runs. Stands in for C's {@code e_info[]}.
     *
     * <p>Field egoItems coded before 261008, commented in full on 261008.
     */
    private static List<EgoItem> egoItems;
    /**
     * The loaded artifacts.
     *
     * <p>Null until {@link #setArtifacts} runs. Stands in for C's {@code a_info[]}.
     *
     * <p>Field artifacts coded before 261008, commented in full on 261008.
     */
    private static List<Artifact> artifacts;
    /**
     * The loaded object properties.
     *
     * <p>Null until {@link #setObjectProperties} runs; {@link #lookupObjectProperty} walks it
     * without a null guard, so calling that first fails with a {@link NullPointerException} rather
     * than the {@link IllegalStateException} the other lookups throw. Stands in for C's
     * {@code obj_properties[]}.
     *
     * <p>Field objectProperties coded before 261008, commented in full on 261008.
     */
    private static List<ObjectProperty> objectProperties;
    /**
     * The tval&rarr;sval&rarr;kind index over {@link #objectKinds}, maintained by {@link #addObjectKind}.
     *
     * <p>It is what makes {@link #lookupObjectKind(TValue, int)} constant-time where C's
     * {@code lookup_kind} scans {@code k_info}. {@link #setObjectKinds} does not rebuild it, and
     * {@link #reset} clears it with the table.
     *
     * <p>Field kindsByTvalSval coded before 261008, commented in full on 261008.
     */
    private static Map<TValue, Map<Integer, ObjectKind>> kindsByTvalSval = new HashMap<>();
    /**
     * The live, mutable object-kind table — grown by {@link #addObjectKind}, cleared by {@link #reset}.
     *
     * <p>Stands in for C's {@code k_info[]}; a kind's position in it is its {@code kidx}. Ordinary
     * kinds come first, in file order, followed by any special artifact kinds synthesized later (see
     * {@link #objectBaseKindMax}). While it is empty every kind lookup throws, which is how a lookup
     * before loading is caught.
     *
     * <p>Field objectKinds coded before 261008, commented in full on 261008.
     */
    private static List<ObjectKind> objectKinds = new ArrayList<>();
    /**
     * The complete rune list, built by {@link Rune#initRunes()}. Order is significant — it is the
     * order runes are listed in the knowledge menu, and C identifies a rune in its savefile by
     * position in this list.
     *
     * <p>Held as an immutable list that {@link #setRunes} <em>replaces</em>, rather than as a
     * mutable list refilled in place like the others here. The field cannot be final as a result,
     * but nothing outside {@code setRunes} can reach it and no published list ever changes, which
     * is what lets {@link #getRunes} hand out the list itself instead of copying it. It starts as
     * an empty list rather than null so the accessors answer sensibly before {@code initRunes} has
     * run.
     *
     * <p>Stands in for C's {@code rune_list}.
     *
     * <p>Field allRunes coded before 261008, commented in full on 261008.
     */
    private static List<Rune> allRunes = List.of();

    /**
     * Replaces the loaded chest traps with the ones just read; set once by {@code ObjectDataLoader}.
     * This copies into the existing list rather than rebinding the field, so the list itself stays
     * final — the older of the two patterns here. {@link #setRunes} takes the other one, publishing
     * a fresh immutable list on each call; see {@link #getRunes} for what that buys and why the
     * runes needed it.
     *
     * <p>Function setChestTraps coded before 261008, commented in full on 261008.
     *
     * @param chestTraps the chest traps to store, in file order
     */
    public static void setChestTraps(List<ChestTrap> chestTraps) {
        ObjectRegistry.chestTraps.clear();
        ObjectRegistry.chestTraps.addAll(chestTraps);
    }

    /**
     * The rune list as built by {@link Rune#initRunes()}, in its significant order — the order the
     * knowledge menu lists runes in, and the order C's savefile identifies them by.
     *
     * <p>The list is immutable, so position — which is a rune's identity here — cannot be disturbed
     * by a caller, and it is a snapshot rather than a view: {@link #setRunes} publishes a new list
     * instead of refilling this one, so a caller holding an earlier result keeps the runes it asked
     * for and never sees a re-init arrive halfway through.
     *
     * <p>Nothing is allocated on the way out. The stored list is already immutable, and
     * {@code List.copyOf} of such a list returns that same list rather than duplicating it, so the
     * one copy in the rune path is the one {@code setRunes} makes per load. That matters because
     * {@link Rune#runeIndex(uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag)} and its siblings
     * call this once per lookup, and the learning code asks about whole flag sets at a time.
     *
     * <p>Two consequences worth knowing. Repeated calls return the <em>same</em> list until the
     * runes are rebuilt, so {@code before != getRunes()} is a complete and O(1) test of whether a
     * rebuild has happened — there is no need to re-run {@code initRunes} to refresh a stale
     * reference, only to call this again. And immutability is structural only: the list holds the
     * live {@link Rune} objects, so an auto-inscription set through one of them is visible through
     * every list ever handed out, which is what the knowledge menu needs.
     *
     * <p>Function getRunes coded before 261008, commented in full on 261008.
     *
     * @return the loaded runes in list order, immutable, empty if {@code initRunes} has not run
     */
    public static List<Rune> getRunes() {
        return allRunes;
    }

    /**
     * Replaces the rune list with the one just built; called only by {@link Rune#initRunes()}.
     *
     * <p>Copies on the way in, so the caller's list — an {@code ArrayList} that {@code initRunes}
     * grows — cannot be written through afterwards to disturb rune positions. That copy is the only
     * one in the rune path: it happens once per load, where copying in {@code getRunes} instead
     * would happen once per lookup.
     *
     * <p>Rebinding rather than refilling is what makes a published list safe to keep: see
     * {@link #getRunes}.
     *
     * <p>Function setRunes coded before 261008, commented in full on 261008.
     *
     * @param runes the runes to store, in list order
     */
    public static void setRunes(List<Rune> runes) {
        allRunes = List.copyOf(runes);
    }

    /**
     * The number of loaded runes — the port of C's {@code max_runes()}
     * ({@code obj-knowledge.c}), which returns the {@code rune_max} that {@code init_rune} sets
     * alongside {@code rune_list} (also {@code obj-knowledge.c}).
     *
     * <p>Derived from the list rather than stored beside it as C's counter is, so the two cannot
     * drift apart. Unlike the {@code *Max} counters above it this needs no setter for the same
     * reason.
     *
     * <p>Function getMaxRunes coded before 261008, commented in full on 261008.
     *
     * @return the number of runes {@link Rune#initRunes()} built, or 0 if it has not run
     */
    public static int getMaxRunes() {
        return allRunes.size();
    }

    /**
     * Records the current object-kind count as {@code objectBaseKindMax} — the ordinary-kind ceiling.
     *
     * <p>Called by {@code ObjectDataLoader} once the kinds from {@code object.txt} are registered
     * and before any special artifact kind is, so that {@link #getObjectBaseKindMax} divides the two
     * groups. Calling it at any other point moves the ceiling.
     *
     * <p>Function updateObjectBaseKindMax coded before 261008, commented in full on 261008.
     */
    public static void updateObjectBaseKindMax() {
        ObjectRegistry.objectBaseKindMax = ObjectRegistry.objectKinds.size();
    }

    /**
     * The number of loaded artifacts, as recorded by {@link #setArtifacts}.
     *
     * <p>Function getArtifactKindMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded artifacts, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getArtifactKindMax() {
        return artifactKindMax;
    }

    /**
     * The number of loaded ego items, as recorded by {@link #setEgoItems}.
     *
     * <p>Function getEgoItemKindMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded ego items, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getEgoItemKindMax() {
        return egoItemKindMax;
    }

    /**
     * The number of activations available to random artifacts, as recorded by
     * {@link #setActivations}.
     *
     * <p>Function getRandartActivationsMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded activations, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getRandartActivationsMax() {
        return randartActivationsMax;
    }

    /**
     * The number of loaded curses, as recorded by {@link #setCurses}.
     *
     * <p>Function getCurseMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded curses, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCurseMax() {
        return curseMax;
    }

    /**
     * The number of loaded slays, as recorded by {@link #setSlays}.
     *
     * <p>Function getSlayMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded slays, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getSlayMax() {
        return slayMax;
    }

    /**
     * The number of loaded brands, as recorded by {@link #setBrands}.
     *
     * <p>Function getBrandMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded brands, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getBrandMax() {
        return brandMax;
    }

    /**
     * The number of loaded object properties, as recorded by {@link #setObjectProperties}. The odd
     * name ({@code Objects}, not {@code Object}) is the port's, not C's.
     *
     * <p>Function getObjectsPropertyMax coded before 261008, commented in full on 261008.
     *
     * @return the number of loaded object properties, 0 before they are set
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsPropertyMax() {
        return objectPropertyMax;
    }

    /**
     * The current number of registered object kinds, ordinary and synthesized together. Unlike
     * {@link #getObjectBaseKindMax} it grows each time {@link #addObjectKind} runs.
     *
     * <p>Function getObjectKindCount coded before 261008, commented in full on 261008.
     *
     * @return the current number of registered object kinds (ordinary plus synthesized)
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectKindCount() {
        return objectKinds.size();
    }

    /**
     * Hands out the live object-kind table as a read-only view. It is a view rather than a copy, so
     * kinds registered later appear in a list already held; callers that need a snapshot must copy
     * it themselves.
     *
     * <p>Function getObjectKinds coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the live object-kind table
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectKind> getObjectKinds() {
        return Collections.unmodifiableList(objectKinds);
    }

    /**
     * Replaces the object-kind table wholesale. Note this does <em>not</em> rebuild
     * {@link #kindsByTvalSval}; prefer {@link #addObjectKind} for individual registration.
     *
     * <p>Keeps the caller's list rather than copying it, so the table and the caller's list are one
     * object afterwards, and {@link #reset} will clear the caller's list too.
     *
     * <p>Function setObjectKinds coded before 261008, commented in full on 261008.
     *
     * @param objectKinds the kinds to use as the table
     */
    public static void setObjectKinds(@NotNull List<ObjectKind> objectKinds) {
        ObjectRegistry.objectKinds = objectKinds;
    }

    /**
     * The ordinary-kind ceiling recorded by {@link #updateObjectBaseKindMax}: kinds whose index is
     * below it came from {@code object.txt}, and the rest were synthesized for special artifacts.
     *
     * <p>Function getObjectBaseKindMax coded before 261008, commented in full on 261008.
     *
     * @return the ordinary-kind ceiling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectBaseKindMax() {
        return objectBaseKindMax;
    }

    /**
     * Hands out the loaded object bases as a read-only view.
     *
     * <p>Function getObjectBases coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded object bases
     * @throws NullPointerException if {@link #setObjectBases} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectBase> getObjectBases() {
        return Collections.unmodifiableList(objectBases);
    }

    /**
     * Stores the loaded object bases; set once by {@code ObjectDataLoader} (before the kinds that reference them).
     *
     * <p>Keeps the caller's list rather than copying it, and records no count.
     *
     * <p>Function setObjectBases coded before 261008, commented in full on 261008.
     *
     * @param objectBases the object bases to store
     */
    public static void setObjectBases(@NotNull List<ObjectBase> objectBases) {
        ObjectRegistry.objectBases = objectBases;
    }

    /**
     * Hands out the loaded slays as a read-only view.
     *
     * <p>Function getSlays coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded slays
     * @throws NullPointerException if {@link #setSlays} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Slay> getSlays() {
        return Collections.unmodifiableList(slays);
    }

    /**
     * Stores the loaded slays and records their count in {@code slayMax}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setSlays coded before 261008, commented in full on 261008.
     *
     * @param slays the slays to store
     */
    public static void setSlays(@NotNull List<Slay> slays) {
        ObjectRegistry.slays = slays;
        slayMax = slays.size();
    }

    /**
     * Hands out the loaded brands as a read-only view.
     *
     * <p>Function getBrands coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded brands
     * @throws NullPointerException if {@link #setBrands} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Brand> getBrands() {
        return Collections.unmodifiableList(brands);
    }

    /**
     * Stores the loaded brands and records their count in {@code brandMax}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setBrands coded before 261008, commented in full on 261008.
     *
     * @param brands the brands to store
     */
    public static void setBrands(@NotNull List<Brand> brands) {
        ObjectRegistry.brands = brands;
        brandMax = brands.size();
    }

    /**
     * Hands out the loaded curses as a read-only view.
     *
     * <p>Function getCurses coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded curses
     * @throws NullPointerException if {@link #setCurses} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Curse> getCurses() {
        return Collections.unmodifiableList(curses);
    }

    /**
     * Stores the loaded curses and records their count in {@code curseMax}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setCurses coded before 261008, commented in full on 261008.
     *
     * @param curses the curses to store
     */
    public static void setCurses(@NotNull List<Curse> curses) {
        ObjectRegistry.curses = curses;
        curseMax = curses.size();
    }

    /**
     * Hands out the loaded item-object templates as a read-only view.
     *
     * <p>Function getItemObjects coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded item-object templates
     * @throws NullPointerException if {@link #setItemObjects} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ItemObject> getItemObjects() {
        return Collections.unmodifiableList(itemObjects);
    }

    /**
     * Stores the loaded item-object templates; set once by {@code ObjectDataLoader}.
     *
     * <p>Keeps the caller's list rather than copying it, and records no count.
     *
     * <p>Function setItemObjects coded before 261008, commented in full on 261008.
     *
     * @param itemObjects the item-object templates to store
     */
    public static void setItemObjects(@NotNull List<ItemObject> itemObjects) {
        ObjectRegistry.itemObjects = itemObjects;
    }

    /**
     * Hands out the loaded activations as a read-only view.
     *
     * <p>Function getActivations coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded activations
     * @throws NullPointerException if {@link #setActivations} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Activation> getActivations() {
        return Collections.unmodifiableList(activations);
    }

    /**
     * Stores the loaded activations and records their count in {@code randartActivationsMax}; set
     * once by {@code ObjectDataLoader}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setActivations coded before 261008, commented in full on 261008.
     *
     * @param activations the activations to store
     */
    public static void setActivations(@NotNull List<Activation> activations) {
        ObjectRegistry.activations = activations;
        randartActivationsMax = activations.size();
    }

    /**
     * Hands out the loaded ego-item templates as a read-only view.
     *
     * <p>Function getEgoItems coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded ego-item templates
     * @throws NullPointerException if {@link #setEgoItems} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<EgoItem> getEgoItems() {
        return Collections.unmodifiableList(egoItems);
    }

    /**
     * Stores the loaded ego items and records their count in {@code egoItemKindMax}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setEgoItems coded before 261008, commented in full on 261008.
     *
     * @param egoItems the ego items to store
     */
    public static void setEgoItems(@NotNull List<EgoItem> egoItems) {
        ObjectRegistry.egoItems = egoItems;
        egoItemKindMax = egoItems.size();
    }

    /**
     * Hands out the loaded artifacts as a read-only view.
     *
     * <p>Function getArtifacts coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded artifacts
     * @throws NullPointerException if {@link #setArtifacts} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Artifact> getArtifacts() {
        return Collections.unmodifiableList(artifacts);
    }

    /**
     * Stores the loaded artifacts and records their count in {@code artifactKindMax}; set once by
     * {@code ObjectDataLoader}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setArtifacts coded before 261008, commented in full on 261008.
     *
     * @param artifacts the artifacts to store
     */
    public static void setArtifacts(@NotNull List<Artifact> artifacts) {
        ObjectRegistry.artifacts = artifacts;
        artifactKindMax = artifacts.size();
    }

    /**
     * Hands out the loaded object properties as a read-only view.
     *
     * <p>Function getObjectProperties coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded object properties
     * @throws NullPointerException if {@link #setObjectProperties} has not run
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectProperty> getObjectProperties() {
        return Collections.unmodifiableList(objectProperties);
    }

    /**
     * Stores the loaded object properties and records their count in {@code objectPropertyMax}.
     *
     * <p>Keeps the caller's list rather than copying it.
     *
     * <p>Function setObjectProperties coded before 261008, commented in full on 261008.
     *
     * @param objectProperties the object properties to store
     */
    public static void setObjectProperties(@NotNull List<ObjectProperty> objectProperties) {
        ObjectRegistry.objectProperties = objectProperties;
        objectPropertyMax = objectProperties.size();
    }

    /**
     * Hands out the tval&rarr;sval&rarr;kind index as a read-only view. Only the outer map is
     * wrapped: the inner sval maps are the live ones, so a caller must not write through them.
     *
     * <p>Function getKindsByTvalSval coded before 261008, commented in full on 261008.
     *
     * @return an unmodifiable view of the kindsByTvalSval map.
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static Map<TValue, Map<Integer, ObjectKind>> getKindsByTvalSval() {
        return Collections.unmodifiableMap(kindsByTvalSval);
    }

    /**
     * Resolves the two sentinel kinds, {@link #unknownGoldKind} and {@link #unknownItemKind}, by
     * their {@code object.txt} names, {@code <unknown treasure>} and {@code <unknown item>}.
     *
     * <p>C does this at the end of {@code finish_parse_artifact} ({@code obj-init.c}), once the
     * object kinds are done: {@code lookup_kind} on tval {@code none} and the sval that
     * {@code lookup_sval} resolves from the name. The port looks the kind up by name alone, which
     * finds the same kind because each name occurs once in {@code object.txt}, under tval
     * {@code none}.
     *
     * <p>Returns without doing anything if the kind table is empty, where every other kind lookup
     * throws; the sentinels are then left as they were. If the file lacks either name the field is
     * set to {@code null} rather than failing. {@code ObjectDataLoader} calls this straight after
     * {@link #updateObjectBaseKindMax}.
     *
     * <p>Function loadUnknownKinds coded before 261008, commented in full on 261008.
     */
    public static void loadUnknownKinds() {
        if (objectKinds.isEmpty()) return;
        unknownGoldKind = lookupObjectKind("<unknown treasure>");
        unknownItemKind = lookupObjectKind("<unknown item>");
    }

    /**
     * Clears the object-kind table and its {@link #kindsByTvalSval} index together, so a
     * re-initialization ({@code GameConstants.init()}) starts from an empty registry rather than
     * double-registering kinds.
     *
     * <p>The two sentinel kinds are nulled with them, since they point into the cleared table. The
     * other lists, the {@code *Max} counters and {@link #objectBaseKindMax} are not touched: the
     * lists are replaced wholesale by their setters on the next load, and the ceiling by
     * {@link #updateObjectBaseKindMax}.
     *
     * <p>Function reset coded before 261008, commented in full on 261008.
     */
    public static void reset() {
        objectKinds.clear();
        unknownItemKind = null;
        unknownGoldKind = null;
        kindsByTvalSval.clear();
    }

    /**
     * Looks up an object kind by its numeric (tval, sval) via the {@link #kindsByTvalSval} index —
     * the constant-time counterpart to the name-based {@link #lookupObjectKind(TValue, String)}.
     *
     * <p>The port of C's {@code lookup_kind} ({@code obj-util.c}), which scans {@code k_info} for
     * the first kind with both values. The two agree on every input because {@link #addObjectKind}
     * hands each base the next sval in turn, so a (tval, sval) pair is never shared. C reports a
     * miss with {@code msg} and returns {@code NULL}; this method returns {@code null} without
     * saying anything, and the message is left to the caller, {@code ObjectUtils.lookupKind}.
     *
     * <p>Function lookupObjectKind coded before 261008, commented in full on 261008.
     *
     * @param tValue the object type value
     * @param sValue the numeric sub-type value
     * @return the matching {@link ObjectKind}, or {@code null} if none is indexed
     * @throws IllegalStateException if object kinds have not been loaded
     */
    @CheckReturnValue
    @Nullable
    public static ObjectKind lookupObjectKind(TValue tValue, int sValue) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        Map<Integer, ObjectKind> map = kindsByTvalSval.get(tValue);
        return map == null ? null : map.get(sValue);
    }

    /**
     * Get an ObjectKind based on its name
     *
     * <p>Compares against the kind's {@code name} exactly as stored, case-sensitively and with any
     * {@code &} and {@code ~} markers still in it, so a caller wanting the bare name must use
     * {@link #lookupObjectKind(TValue, String)} instead. There is no C original; it serves the
     * sentinel kinds, whose names carry no markers.
     *
     * <p>Function lookupObjectKind coded before 261008, commented in full on 261008.
     *
     * @param name the name of the object kind we are searching for
     * @return the object kind with that name or null if it doesn't
     * exist
     * @throws IllegalStateException if object kinds have not been loaded
     */
    @CheckReturnValue
    @Nullable
    public static ObjectKind lookupObjectKind(@NotNull String name) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectKinds.stream()
                .filter(e -> name.equals(e.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Look up an object kind by its tval and an sval <em>reference</em>, resolving the reference the
     * way the data files use it (C's {@code lookup_sval}): if {@code ref} is all digits it is treated
     * as a literal numeric sval and dispatched to {@link #lookupObjectKind(TValue, int)}; otherwise it
     * is matched case-insensitively against each kind's {@link ObjectKind#getsValueName() sval name}.
     *
     * <p>C's {@code lookup_sval} returns the sval and leaves {@code lookup_kind} to fetch the kind;
     * this does both, so a miss at either step is {@code null}. The name it matches is the kind's
     * name with C's {@code &} and {@code ~} markers already removed, which is what C's
     * {@code obj_desc_name_format} leaves for a name without a {@code |} form (none in
     * {@code object.txt} has one).
     *
     * <p>The numeric test is looser than C's, which takes a leading unsigned number and rejects the
     * reference outright if anything but spaces or tabs follows it. Here only a reference that
     * {@code Integer.parseInt} accepts after trimming counts as a number, and anything else falls
     * through to the name match, so {@code "12abc"} is looked up as a name where C answers "none".
     * No kind in {@code object.txt} begins with a digit, so the two give the same answer on the
     * shipped data. A negative or out-of-range number finds no kind either way.
     *
     * <p>Function lookupObjectKind coded before 261008, commented in full on 261008.
     *
     * @param tval the object type value
     * @param ref  the sval reference — either a decimal sval or a sub-type name
     * @return the matching {@link ObjectKind}, or {@code null} if none matches
     * @throws IllegalStateException if object kinds have not been loaded
     */
    @CheckReturnValue
    @Nullable
    public static ObjectKind lookupObjectKind(@NotNull TValue tval, @NotNull String ref) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        try {
            return lookupObjectKind(tval, Integer.parseInt(ref.trim()));
        } catch (NumberFormatException NAN) {
            return objectKinds.stream()
                    .filter(k -> tval.equals(k.gettValue()) &&
                            ref.equalsIgnoreCase(k.getsValueName()))
                    .findFirst().orElse(null);
        }
    }

    /**
     * Collect every loaded object kind of a given type — the tval fan-out used, for example, when an
     * ego {@code type:} line applies to all kinds of a tval.
     *
     * <p>No single C function does this; C's {@code parse_ego_type} ({@code obj-init.c}) loops over
     * {@code k_info} comparing tvals inline. The result is a new list, in table order, that the caller may change freely.
     *
     * <p>Function lookupObjectKind coded before 261008, commented in full on 261008.
     *
     * @param tval the object type value
     * @return every {@link ObjectKind} with that tval (possibly empty, never {@code null})
     * @throws IllegalStateException if object kinds have not been loaded
     */
    @NotNull
    @CheckReturnValue
    public static List<ObjectKind> lookupObjectKind(@NotNull TValue tval) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        List<ObjectKind> results = new ArrayList<>();
        for (ObjectKind k : objectKinds) {
            if (tval.equals(k.gettValue())) {
                results.add(k);
            }
        }

        return results;
    }

    /**
     * Search through the slays to get a slay with the same code as
     * the incoming parameter
     *
     * <p>The port gives a name to the loop that C's object, ego and artifact parsers each write
     * inline over {@code slays[]}, comparing {@code code} with {@code streq}; the comparison here
     * is {@code equals}, so it is case-sensitive in the same way.
     *
     * <p>Function lookupSlay coded before 261008, commented in full on 261008.
     *
     * @param slayName the name/code of the slay to find
     * @return A slay where the code name is the same as the incoming
     * parameter, or null
     * @throws IllegalStateException if {@link #setSlays} has not run
     */
    @Nullable
    @CheckReturnValue
    public static Slay lookupSlay(String slayName) {
        if (slays == null) {
            String message = "Invalid attempt to access slays when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return slays.stream()
                .filter(s -> s.getCode().equals(slayName))
                .findFirst().orElse(null);
    }

    /**
     * Locate a curse by its name
     *
     * <p>The port of C's {@code lookup_curse} ({@code obj-curse.c}), which compares names with
     * {@code streq} and so is case-sensitive, as {@code equals} is here. C returns the curse's index
     * and answers 0, the placeholder entry, for a miss; this returns the curse itself and
     * {@code null} for a miss, so the "not found" test is a null check rather than a comparison
     * with zero.
     *
     * <p>Function lookupCurse coded before 261008, commented in full on 261008.
     *
     * @param curseName the name of the curse we are looking for
     * @return The curse with the relevant name or null
     * @throws IllegalStateException if {@link #setCurses} has not run
     */
    @Nullable
    @CheckReturnValue
    public static Curse lookupCurse(String curseName) {
        if (curses == null) {
            String message = "Invalid attempt to access curses when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return curses.stream()
                .filter(c -> c.getName().equals(curseName))
                .findFirst().orElse(null);
    }

    /**
     * Register an object kind: allocate it the next sval under its base (svals are 1-based per base),
     * append it to {@link #objectKinds}, and index it in {@link #kindsByTvalSval} for fast
     * (tval, sval) lookup. This is the single choke-point that keeps a kind's numeric sval and the
     * lookup index in step, so synthesized kinds (e.g. spellbooks) register exactly like file-loaded
     * ones.
     *
     * <p>C does the same in two places: the object parser bumps {@code base->num_svals} and copies
     * it to {@code k->sval} (the {@code obj-init.c} step that finishes a kind), and the special
     * artifact path does likewise for the kinds it writes. The kind's index is its position in the
     * table, from 0, the same as C's {@code kidx}.
     *
     * <p>Mutates the kind and its base as well as the registry: the base's sval counter moves on, and
     * the kind's sval and index are overwritten.
     *
     * <p>Function addObjectKind coded before 261008, commented in full on 261008.
     *
     * @param toAdd the ObjectKind to register
     */
    public static void addObjectKind(@NotNull ObjectKind toAdd) {
        ObjectBase base = toAdd.getBase();
        int sVal = base.getNumSvals() + 1;      // svals are 1-based within each base
        base.setNumSvals(sVal);
        toAdd.setsVal(sVal);
        toAdd.setKindIndex(objectKinds.size());
        objectKinds.add(toAdd);
        kindsByTvalSval
                .computeIfAbsent(toAdd.gettValue(), k -> new HashMap<>())
                .put(sVal, toAdd);
    }

    /**
     * Get the ObjectBase which has a given string as its name
     *
     * <p>Has no direct C original: C reaches a base by indexing {@code kb_info[tval]}, and finds
     * one by name only while parsing {@code object_base.txt}. Prefer
     * {@link #lookupObjectBase(String, TValue)} or {@link #getBaseFromTVal} when the tval is known,
     * since a name alone is not necessarily unique across tvals.
     *
     * <p>Function lookupObjectBase coded before 261008, commented in full on 261008.
     *
     * @param name the name we are searching the object base list for
     * @return the object base with given name or null
     * @throws IllegalStateException if {@link #setObjectBases} has not run
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase lookupObjectBase(@NotNull String name) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(o -> name.equals(o.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the ObjectBase which has a given string as its name and TValue as its TValue
     *
     * <p>Has no direct C original; see {@link #lookupObjectBase(String)}. Both the name and the tval
     * must match.
     *
     * <p>Function lookupObjectBase coded before 261008, commented in full on 261008.
     *
     * @param name   the name we are searching the object base list for
     * @param tValue the TValue we are searching for
     * @return the object base with given name or null
     * @throws IllegalStateException if {@link #setObjectBases} has not run
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase lookupObjectBase(@NotNull String name, @NotNull TValue tValue) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(o -> (name.equals(o.getName()) && tValue == o.gettVal()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lookup an item base based on its tval and return the first base found
     *
     * <p>The port's replacement for C's direct {@code kb_info[tval]} indexing: C keeps one base per
     * tval and can address it by number, where the port walks the list for the first match.
     *
     * <p>Function getBaseFromTVal coded before 261008, commented in full on 261008.
     *
     * @param tVal The tval we are looking for
     * @return the first item base with that tVal
     * @throws IllegalStateException if {@link #setObjectBases} has not run
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase getBaseFromTVal(@NotNull TValue tVal) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(ob -> tVal.equals(ob.gettVal()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Return a brand based on the brand name
     *
     * <p>The port gives a name to the loop that C's object, ego and artifact parsers each write
     * inline over {@code brands[]}, comparing {@code code} with {@code streq}; the comparison here
     * is {@code equals}, so it is case-sensitive in the same way. The argument is a brand's code,
     * despite its name.
     *
     * <p>Function lookupBrandCode coded before 261008, commented in full on 261008.
     *
     * @param name the name of the brand to return
     * @return the brand or null if it isn't found
     * @throws IllegalStateException if {@link #setBrands} has not run
     */
    @Nullable
    public static Brand lookupBrandCode(@NotNull String name) {
        if (brands == null) {
            String message = "Invalid attempt to access brands when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return brands.stream().filter(b -> name.equals(b.getCode()))
                .findFirst().orElse(null);
    }

    /**
     * Get an activation by its name
     *
     * <p>The port of C's {@code findact} ({@code obj-init.c}), which walks the activation list from
     * its first real entry comparing names with {@code streq} and ends on {@code NULL} if none
     * matches. The comparison here is {@code equals}, so it is case-sensitive in the same way, and a
     * miss is {@code null}.
     *
     * <p>Function lookupActivation coded before 261008, commented in full on 261008.
     *
     * @param name The name of the activation we are searching for
     * @return the Activation in the List activations with the name equal to the incoming parameter
     * @throws IllegalStateException if {@link #setActivations} has not run
     */
    @Nullable
    public static Activation lookupActivation(@NotNull String name) {
        if (activations == null) {
            String message = "Invalid attempt to access activations when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return activations.stream().filter(e -> name.equals(e.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Finds the property definition describing a given flag, modifier or element. Ports C's
     * {@code lookup_obj_property} ({@code src/obj-properties.c}), which searches the same loaded
     * property list.
     * <p>
     * A request for {@code OBJ_PROPERTY_MOD} also matches properties declared as
     * {@code OBJ_PROPERTY_STAT}. The two are one contiguous range in C — the stats occupy the first
     * few modifier slots — so a lookup by modifier finds a stat without needing to know which of
     * the two it is asking about.
     *
     * <p>C tests the type and an integer index separately; the port folds the index into the
     * payload, whose {@code equals} compares the flag, modifier and element and deliberately not the
     * type, so this method is the one place the type is tested. The first property in list order
     * that qualifies wins, as in C.
     *
     * <p>Unlike the other lookups here there is no guard for an unset list: calling this before
     * {@link #setObjectProperties} fails with a {@link NullPointerException}.
     *
     * <p>Function lookupObjectProperty coded before 261008, commented in full on 261008.
     *
     * @param type    the category of property wanted
     * @param payload the flag, modifier or element to find the definition for
     * @return the matching property, or {@code null} if the loaded data declares none, which means
     * the data files and the enums have drifted apart
     */
    public static ObjectProperty lookupObjectProperty(ObjPropertyType type, ObjectPropertyTypeWrapper payload) {
        for (ObjectProperty property : objectProperties) {
            if (property.getType().equals(type) && property.getPayload().equals(payload))
                return property;

            if (type.equals(ObjPropertyType.OBJ_PROPERTY_MOD) && property.getType().equals(ObjPropertyType.OBJ_PROPERTY_STAT)
                    && property.getPayload().equals(payload))
                return property;
        }

        return null;
    }
}
