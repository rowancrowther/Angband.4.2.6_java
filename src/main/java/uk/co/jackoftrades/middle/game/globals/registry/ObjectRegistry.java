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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.*;

/**
 * Runtime holder for all object-domain game data — object bases, object kinds, slays, brands,
 * curses, item objects, activations, ego items, artifacts, chest traps, and object properties —
 * together with the derived {@code *Max} counters and the lookups the running game queries.
 *
 * <p>Unlike the other registries, the object-kind table is a live, mutable registry rather than a
 * load-once list: {@link #addObjectKind} appends a kind and indexes it in {@link #kindsByTvalSval}
 * (the tval&rarr;sval&rarr;kind index kept in sync with {@code objectKinds}), and {@link #reset}
 * clears both so a re-initialisation does not double-register. {@link #unknownGoldKind} and
 * {@link #unknownItemKind} are the sentinel kinds for unidentified gold and items.
 *
 * <p>This is the read side of the object slice: it is populated at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.ObjectDataLoader} — ordinary kinds
 * registered by {@code loadItemObjects} and special artifact kinds synthesised into the same table
 * by {@code loadArtifacts}. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class ObjectRegistry {
    /**
     * Assumed off-weapon damage, used to boost an object that grants extra blows without being a
     * weapon itself. C's {@code NONWEAP_DAMAGE}, "fudge to boost extra blows".
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
     */
    public static final int WEAP_DAMAGE = 12;
    /**
     * Flat power every piece of jewellery starts from. C's {@code BASE_JEWELRY_POWER} (spelled the
     * American way there).
     */
    public static final int BASE_JEWELERY_POWER = 4;
    /**
     * Flat power every armour item starts from, for halving acid damage. C's
     * {@code BASE_ARMOUR_POWER}.
     */
    public static final int BASE_ARMOUR_POWER = 1;
    /**
     * Power per point of damage, doubled - the algorithm wants 2.5. C's {@code DAMAGE_POWER}.
     */
    public static final int DAMAGE_POWER = 5;
    /**
     * Power per point of to-hit, doubled - the algorithm wants 1.5. C's {@code TO_HIT_POWER}.
     */
    public static final int TO_HIT_POWER = 3;
    /**
     * Power per point of base armour class, doubled - the algorithm wants 1. C's
     * {@code BASE_AC_POWER}.
     */
    public static final int BASE_AC_POWER = 2;
    /**
     * Power per point of to-armour, doubled - the algorithm wants 1. C's {@code TO_AC_POWER}.
     */
    public static final int TO_AC_POWER = 2;
    /**
     * The number of blows a melee weapon is assumed to land per turn. Launchers are rescaled by
     * this so that the two can be compared. C's {@code MAX_BLOWS}.
     */
    public static final int MAX_BLOWS = 5;
    /**
     * Numerator of the weight adjustment for an object with no base armour class. One, so the
     * multiply is a no-op; it exists to mirror C's pairing of a numerator with each denominator.
     */
    public static final int WGT_POWER_NUM_NOBASEAC = 1;
    /**
     * Denominator of the weight adjustment for an object with no base armour class - five pounds
     * lighter than standard adds one point of power, and heavier subtracts. Easily confused with
     * {@link #WGT_POWER_NUM_NOBASEAC} beside it, which is the wrong one to divide by.
     */
    public static final int WGT_POWER_DEN_NOBASEAC = 50;
    /**
     * Numerator of the weight adjustment for a {@code THROWING} object, where heavier is better.
     * C explains the figure: the throwing multiplier is {@code 2 + weight / 12} and shooting rates
     * at 30, so throwing - typically less useful - is priced at half that.
     */
    public static final int WGT_POWER_NUM_THROW = 15;
    /**
     * Denominator of the weight adjustment for a {@code THROWING} object. C's
     * {@code WGT_POWER_DEN_THROW}.
     */
    public static final int WGT_POWER_DEN_THROW = 12;
    /**
     * The refusal value. Added rather than a price: an object that reaches it is not meant to
     * exist, and the power calculation returns early once it is exceeded.
     */
    public static final int INHIBIT_POWER = 20000;
    /**
     * Extra blows at or above which an object is refused - so the most it may carry is one less.
     */
    public static final int INHIBIT_BLOWS = 3;
    /**
     * Extra shooting might at or above which an object is refused.
     */
    public static final int INHIBIT_MIGHT = 4;
    /**
     * Extra shots at or above which an object is refused.
     */
    public static final int INHIBIT_SHOTS = 21;
    /**
     * To-armour above which each further point is priced again, on top of the ordinary rate.
     */
    public static final int HIGH_TO_AC = 26;
    /**
     * To-armour above which each further point is priced twice again.
     */
    public static final int VERYHIGH_TO_AC = 36;
    /**
     * To-armour at or above which an object is refused.
     */
    public static final int INHIBIT_AC = 56;
    /**
     * To-hit above which the randart generator treats the bonus as high. Not read by the power
     * calculation itself.
     */
    public static final int HIGH_TO_HIT = 16;
    /**
     * To-hit above which the randart generator treats the bonus as very high.
     */
    public static final int VERYHIGH_TO_HIT = 26;
    /**
     * To-damage above which the randart generator treats the bonus as high.
     */
    public static final int HIGH_TO_DAM = 16;
    /**
     * To-damage above which the randart generator treats the bonus as very high.
     */
    public static final int VERYHIGH_TO_DAM = 26;
    /**
     * Divisor that brings a single missile down to a fair share of a weapon's power - a stack of
     * this many is reckoned equal to a weapon of the same damage output. C notes it is used for
     * torches too.
     */
    public static final int AMMO_RESCALER = 20;
    /**
     * Shared logger for the loaders and lookups below, which report a data problem rather than
     * failing silently.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The launcher-and-ammo pricing assumptions, keyed by ammunition type - the port of C's
     * {@code archery[]} table ({@code obj-power.c:47}). See {@link Archery} for what the rows mean
     * and why the port keys them where C indexes.
     */
    public static Map<TValue, Archery> archery;

    /**
     * The flag families that are worth more held together, keyed by family - the port of C's
     * {@code flag_sets[]} table ({@code obj-power.c:71}).
     *
     * <p>Shared mutable state: each row carries a count that the power calculation zeroes and
     * increments in place, exactly as C does on its static table. Two power calculations must
     * therefore not interleave.
     */
    public static Map<ObjectFlagType, FlagSet> flagSets;

    /**
     * The elemental protection combinations that are worth more held together - the port of C's
     * {@code element_sets[]} table ({@code obj-power.c:93}). Counted in place like
     * {@link #flagSets}, and with the same caution.
     */
    public static List<ElementSet> elementSets;

    /**
     * What each element is worth to an object that ignores, resists, is immune to or is vulnerable
     * to it - the port of C's {@code el_powers[]} table ({@code obj-power.c:112}). Read only; unlike
     * the two set tables above, nothing writes to these rows.
     */
    public static List<ElementPowers> elementPowers;

    /**
     * Number of ordinary object kinds loaded from {@code object.txt} — the artifact-synthesis ceiling.
     */
    private static int objectBaseKindMax;
    /**
     * Number of loaded artifacts.
     */
    private static int artifactKindMax;
    /**
     * Number of loaded ego items (set from {@link #setEgoItems}).
     */
    private static int egoItemKindMax;
    /**
     * Number of activations available to random artifacts.
     */
    private static int randartActivationsMax;
    /**
     * Number of loaded curses (set from {@link #setCurses}).
     */
    private static int curseMax;
    /**
     * Number of loaded slays (set from {@link #setSlays}).
     */
    private static int slayMax;
    /**
     * Number of loaded brands (set from {@link #setBrands}).
     */
    private static int brandMax;
    /**
     * Number of object-power calculation records.
     */
    private static int objectPowerCalculationMax;
    /**
     * Number of loaded object properties (set from {@link #setObjectProperties}).
     */
    private static int objectPropertyMax;
    /**
     * Number of object records declared in {@code object.txt}.
     */
    private static int objectsInObject_txt;
    /**
     * The loaded object bases, resolved by name/tval via {@link #lookupObjectBase}.
     */
    private static List<ObjectBase> objectBases;
    /**
     * The loaded slays, resolved by code via {@link #lookupSlay}.
     */
    private static List<Slay> slays;
    /**
     * The loaded brands, resolved by code via {@link #lookupBrandCode}.
     */
    private static List<Brand> brands;
    /**
     * The loaded curses, resolved by name via {@link #lookupCurse}.
     */
    private static List<Curse> curses;
    /**
     * The loaded item-object templates.
     */
    private static List<ItemObject> itemObjects;
    /**
     * The loaded activations, resolved by name via {@link #lookupActivation}.
     */
    private static List<Activation> activations;
    /**
     * The loaded ego-item templates.
     */
    private static List<EgoItem> egoItems;
    /**
     * The loaded artifacts.
     */
    private static List<Artifact> artifacts;
    /**
     * The loaded object properties.
     */
    private static List<ObjectProperty> objectProperties;
    /**
     * The tval&rarr;sval&rarr;kind index over {@link #objectKinds}, maintained by {@link #addObjectKind}.
     */
    private static Map<TValue, Map<Integer, ObjectKind>> kindsByTvalSval = new HashMap<>();
    /**
     * The live, mutable object-kind table — grown by {@link #addObjectKind}, cleared by {@link #reset}.
     */
    private static List<ObjectKind> objectKinds = new ArrayList<>();

    /**
     * The loaded chest traps, in file order. Order is load-bearing twice over: each trap's pval bit
     * is its position, and {@code pick_one_chest_trap} draws only from the entries <em>after</em> the
     * first, which is always the "locked" no-trap entry. C holds the same data as a linked list
     * headed by its global {@code chest_traps} ({@code obj-chest.c:53}).
     */
    private static final List<ChestTrap> chestTraps = new ArrayList<>();

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
     */
    private static List<Rune> allRunes = List.of();

    /**
     * Sentinel kind representing an unidentified pile of gold.
     */
    public static final ObjectKind unknownGoldKind = new ObjectKind();
    /**
     * Sentinel kind representing an unidentified item.
     */
    public static final ObjectKind unknownItemKind = new ObjectKind();

    /**
     * Boost ratings for combinations of ability bonuses, indexed by the combined bonus divided by
     * ten - the port of C's {@code ability_power[]} ({@code obj-power.c:132}).
     *
     * <p>Rises faster than linearly, so an object with several large modifiers is worth more than
     * the sum of them; the first seven entries are zero, which is what makes a small total worth no
     * bonus at all. C's comment notes the table runs to +24 and that anything higher is inhibited.
     */
    public static int[] abilityPower = new int[]{0, 0, 0, 0, 0, 0, 0, 2, 4, 6, 8,
            12, 16, 20, 24, 30, 36, 42, 48, 56, 64,
            74, 84, 96, 110};

    /**
     * Replaces the loaded chest traps with the ones just read; set once by {@code ObjectDataLoader}.
     * This copies into the existing list rather than rebinding the field, so the list itself stays
     * final — the older of the two patterns here. {@link #setRunes} takes the other one, publishing
     * a fresh immutable list on each call; see {@link #getRunes} for what that buys and why the
     * runes needed it.
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
     * {@link Rune#runeIndex(uk.co.jackoftrades.middle.objects.enums.ObjectFlag)} and its siblings
     * call this once per lookup, and the learning code asks about whole flag sets at a time.
     *
     * <p>Two consequences worth knowing. Repeated calls return the <em>same</em> list until the
     * runes are rebuilt, so {@code before != getRunes()} is a complete and O(1) test of whether a
     * rebuild has happened — there is no need to re-run {@code initRunes} to refresh a stale
     * reference, only to call this again. And immutability is structural only: the list holds the
     * live {@link Rune} objects, so an auto-inscription set through one of them is visible through
     * every list ever handed out, which is what the knowledge menu needs.
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
     * @param runes the runes to store, in list order
     */
    public static void setRunes(List<Rune> runes) {
        allRunes = List.copyOf(runes);
    }

    /**
     * The number of loaded runes — the port of C's {@code max_runes()}
     * ({@code [C] src/obj-knowledge.c:230}), which returns the {@code rune_max} that
     * {@code init_rune} sets alongside {@code rune_list} ({@code [C] src/obj-knowledge.c:131}).
     *
     * <p>Derived from the list rather than stored beside it as C's counter is, so the two cannot
     * drift apart. Unlike the {@code *Max} counters above it this needs no setter for the same
     * reason.
     *
     * @return the number of runes {@link Rune#initRunes()} built, or 0 if it has not run
     */
    public static int getMaxRunes() {
        return allRunes.size();
    }

    /**
     * Records the current object-kind count as {@code objectBaseKindMax} — the ordinary-kind ceiling.
     */
    public static void updateObjectBaseKindMax() {
        ObjectRegistry.objectBaseKindMax = ObjectRegistry.objectKinds.size();
    }

    /**
     * @return the number of loaded artifacts
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getArtifactKindMax() {
        return artifactKindMax;
    }

    /**
     * @return the number of loaded ego items
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getEgoItemKindMax() {
        return egoItemKindMax;
    }

    /**
     * @return the number of activations available to random artifacts
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getRandartActivationsMax() {
        return randartActivationsMax;
    }

    /**
     * @return the number of loaded curses
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCurseMax() {
        return curseMax;
    }

    /**
     * @return the number of loaded slays
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getSlayMax() {
        return slayMax;
    }

    /**
     * @return the number of loaded brands
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getBrandMax() {
        return brandMax;
    }

    /**
     * @return the number of object-power calculation records
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsPowerCalculationMax() {
        return objectPowerCalculationMax;
    }

    /**
     * @return the number of loaded object properties
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsPropertyMax() {
        return objectPropertyMax;
    }

    /**
     * @return the number of object records declared in {@code object.txt}
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsInObject_txt() {
        return objectsInObject_txt;
    }

    /**
     * @return the current number of registered object kinds (ordinary plus synthesised)
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectKindCount() {
        return objectKinds.size();
    }

    /**
     * @return an unmodifiable view of the live object-kind table
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectKind> getObjectKinds() {
        return Collections.unmodifiableList(objectKinds);
    }

    /**
     * @return the ordinary-kind ceiling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectBaseKindMax() {
        return objectBaseKindMax;
    }

    /**
     * @return an unmodifiable view of the loaded object bases
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectBase> getObjectBases() {
        return Collections.unmodifiableList(objectBases);
    }

    /**
     * Stores the loaded object bases; set once by {@code ObjectDataLoader} (before the kinds that reference them).
     */
    public static void setObjectBases(@NotNull List<ObjectBase> objectBases) {
        ObjectRegistry.objectBases = objectBases;
    }

    /**
     * @return an unmodifiable view of the loaded slays
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Slay> getSlays() {
        return Collections.unmodifiableList(slays);
    }

    /**
     * Stores the loaded slays and records their count in {@code slayMax}.
     */
    public static void setSlays(@NotNull List<Slay> slays) {
        ObjectRegistry.slays = slays;
        slayMax = slays.size();
    }

    /**
     * @return an unmodifiable view of the loaded brands
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Brand> getBrands() {
        return Collections.unmodifiableList(brands);
    }

    /**
     * Stores the loaded brands and records their count in {@code brandMax}.
     */
    public static void setBrands(@NotNull List<Brand> brands) {
        ObjectRegistry.brands = brands;
        brandMax = brands.size();
    }

    /**
     * @return an unmodifiable view of the loaded curses
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Curse> getCurses() {
        return Collections.unmodifiableList(curses);
    }

    /**
     * Stores the loaded curses and records their count in {@code curseMax}.
     */
    public static void setCurses(@NotNull List<Curse> curses) {
        ObjectRegistry.curses = curses;
        curseMax = curses.size();
    }

    /**
     * @return an unmodifiable view of the loaded item-object templates
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ItemObject> getItemObjects() {
        return Collections.unmodifiableList(itemObjects);
    }

    /**
     * Stores the loaded item-object templates; set once by {@code ObjectDataLoader}.
     */
    public static void setItemObjects(@NotNull List<ItemObject> itemObjects) {
        ObjectRegistry.itemObjects = itemObjects;
    }

    /**
     * @return an unmodifiable view of the loaded activations
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Activation> getActivations() {
        return Collections.unmodifiableList(activations);
    }

    /**
     * Stores the loaded activations; set once by {@code ObjectDataLoader}.
     */
    public static void setActivations(@NotNull List<Activation> activations) {
        ObjectRegistry.activations = activations;
    }

    /**
     * @return an unmodifiable view of the loaded ego-item templates
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<EgoItem> getEgoItems() {
        return Collections.unmodifiableList(egoItems);
    }

    /**
     * Stores the loaded ego items and records their count in {@code egoItemKindMax}.
     */
    public static void setEgoItems(@NotNull List<EgoItem> egoItems) {
        ObjectRegistry.egoItems = egoItems;
        egoItemKindMax = egoItems.size();
    }

    /**
     * @return an unmodifiable view of the loaded artifacts
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Artifact> getArtifacts() {
        return Collections.unmodifiableList(artifacts);
    }

    /**
     * Stores the loaded artifacts; set once by {@code ObjectDataLoader}.
     */
    public static void setArtifacts(@NotNull List<Artifact> artifacts) {
        ObjectRegistry.artifacts = artifacts;
    }

    /**
     * @return an unmodifiable view of the loaded object properties
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectProperty> getObjectProperties() {
        return Collections.unmodifiableList(objectProperties);
    }

    /**
     * Stores the loaded object properties and records their count in {@code objectPropertyMax}.
     */
    public static void setObjectProperties(@NotNull List<ObjectProperty> objectProperties) {
        ObjectRegistry.objectProperties = objectProperties;
        objectPropertyMax = objectProperties.size();
    }

    /**
     * @return an unmodifiable view of the kindsByTvalSval map.
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static Map<TValue, Map<Integer, ObjectKind>> getKindsByTvalSval() {
        return Collections.unmodifiableMap(kindsByTvalSval);
    }

    /**
     * Replaces the object-kind table wholesale. Note this does <em>not</em> rebuild
     * {@link #kindsByTvalSval}; prefer {@link #addObjectKind} for individual registration.
     */
    public static void setObjectKinds(@NotNull List<ObjectKind> objectKinds) {
        ObjectRegistry.objectKinds = objectKinds;
    }

    /**
     * Clears the object-kind table and its {@link #kindsByTvalSval} index together, so a
     * re-initialisation ({@code GameConstants.init()}) starts from an empty registry rather than
     * double-registering kinds.
     */
    public static void reset() {
        objectKinds.clear();
        kindsByTvalSval.clear();
    }

    /**
     * Looks up an object kind by its numeric (tval, sval) via the {@link #kindsByTvalSval} index —
     * the constant-time counterpart to the name-based {@link #lookupObjectKind(TValue, String)}.
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
     * @param name the name of the object kind we are searching for
     * @return the object kind with that name or null if it doesn't
     * exist
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
     * @param slayName the name/code of the slay to find
     * @return A slay where the code name is the same as the incoming
     * parameter, or null
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
     * Locate a cruse by its name
     *
     * @param curseName the name of the curse we are looking for
     * @return The curse with the relevant name or null
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
     * lookup index in step, so synthesised kinds (e.g. spellbooks) register exactly like file-loaded
     * ones.
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
     * @param name the name we are searching the object base list for
     * @return the object base with given name or null
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
     * @param name   the name we are searching the object base list for
     * @param tValue the TValue we are searching for
     * @return the object base with given name or null
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
     * @param tVal The tval we are looking for
     * @return the first item base with that tVal
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
     * @param name the name of the brand to return
     * @return the brand or null if it isn't found
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
     * @param name The name of the activation we are searching for
     * @return the Activation in the List activations with the name equal to the incoming parameter
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
