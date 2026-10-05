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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the parts of {@link ItemObject}'s field surface that the other accessor tests leave open:
 * what the two constructors leave in each field, what the whole-collection setters do with the
 * collection they are handed and the one they replace, and the stat overload of
 * {@code getModifierValue}.
 *
 * <p>C has no counterpart for most of this. {@code struct object} is plain data, and C reaches
 * into it inline, so the expected values come from what C's plain assignment would leave. A
 * blank object is {@code object_new}, a {@code mem_zalloc} of the struct, so every member is zero.
 * Assigning an array or pointer member to itself changes nothing. {@code copy_curses} in
 * {@code obj-curse.c} writes both the power and a freshly rolled timeout for every curse it is
 * given, even when the power is the one already there.
 *
 * <p>The whole-collection setters are covered for one hazard in particular: a setter that clears
 * a collection before replacing it empties the item when the caller hands back the item's own live
 * collection, and wrecks a collection that two items share.
 *
 * <p>Class ItemObjectFieldSurfaceTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
class ItemObjectFieldSurfaceTest {

    /**
     * A curse definition, distinct from every other by identity ({@link Curse} declares no
     * {@code equals}). The name is carried only so a failure names the curse.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return curse(name, 0);
    }

    /**
     * As {@link #curse(String)}, with the registry index the item's curse map orders by.
     *
     * @param name  the curse's name
     * @param index the curse's index in the registry
     * @return a curse with every other field empty
     */
    private static Curse curse(String name, int index) {
        return new Curse(name, List.of(), new ItemObject(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "", index);
    }

    /**
     * Builds an item through the full constructor, with the collections the test cares about
     * supplied and everything else empty or zero.
     *
     * @param modifiers the modifier map, possibly {@code null}
     * @param elInfo    the element map, possibly {@code null}
     * @param brands    the brand set, possibly {@code null}
     * @param slays     the slay set, possibly {@code null}
     * @param curses    the curse map, possibly {@code null}
     * @param effect    the effect list, possibly {@code null}
     * @param pValue    the extra parameter, as the constructor takes it: a string
     * @param time      the recharge dice string
     * @return the item
     */
    private static ItemObject full(Map<ObjectModifier, Integer> modifiers, Map<ElementEnum, ElementInfo> elInfo,
                                   Set<Brand> brands, Set<Slay> slays, LinkedHashMap<Curse, CurseData> curses,
                                   List<Effect> effect, String pValue, String time) {
        return new ItemObject(new ObjectKind(), null, null, null, Loc.zero, TValue.TV_SWORD, 0, pValue,
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), modifiers, elInfo, brands, slays, curses,
                effect, null, null, time, 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    /**
     * An item with every collection present and empty.
     *
     * @return the item
     */
    private static ItemObject loaded() {
        return full(new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                new ArrayList<>(), "0", "0");
    }

    /**
     * A brand, for the set-sharing test.
     *
     * @param code the brand's code
     * @return the brand
     */
    private static Brand brand(String code) {
        return new Brand(code, "fire", "burns", MonsterRaceFlag.RF_IM_FIRE,
                MonsterRaceFlag.RF_HURT_FIRE, 2, 3, 20);
    }

    /**
     * A slay, likewise.
     *
     * @param code the slay's code
     * @return the slay
     */
    private static Slay slay(String code) {
        return new Slay(code, "evil", null, "smites", "smites", MonsterRaceFlag.RF_EVIL, 3, 3, 15);
    }

    /**
     * What the no-argument constructor leaves, which should be what C's zero-filled
     * {@code object_new} holds.
     */
    @Nested
    @DisplayName("a blank item")
    class Blank {

        /**
         * C's blank object has tval 0, which is {@code TV_NONE} in this port. A null here would
         * make the first {@code gettValue().isAmmo()} on a blank item throw where C reads a zero.
         */
        @Test
        @DisplayName("has the none type, as C's zeroed tval 0")
        void typeIsNone() {
            assertEquals(TValue.TV_NONE, new ItemObject().gettValue());
        }

        /**
         * A wiped item and a freshly built one are both C's zero fill, so they must agree on the
         * type. The wipe landed on {@code TV_NONE} first; the constructor now matches it.
         */
        @Test
        @DisplayName("agrees with a wiped item about its type")
        void typeMatchesWipe() {
            ItemObject wiped = loaded();
            wiped.settValue(TValue.TV_SWORD);
            wiped.wipe();

            assertEquals(wiped.gettValue(), new ItemObject().gettValue());
        }

        /**
         * Every numeric member of a zeroed struct reads zero.
         */
        @Test
        @DisplayName("has every numeric member at zero")
        void numbersAreZero() {
            ItemObject blank = new ItemObject();

            assertAll(
                    () -> assertEquals(0, blank.getsValue()),
                    () -> assertEquals(0, blank.getpValue()),
                    () -> assertEquals(0, blank.getWeight()),
                    () -> assertEquals(0, blank.getDamageDice()),
                    () -> assertEquals(0, blank.getDamageSides()),
                    () -> assertEquals(0, blank.getBaseAC()),
                    () -> assertEquals(0, blank.getToAC()),
                    () -> assertEquals(0, blank.getToHit()),
                    () -> assertEquals(0, blank.getToDam()),
                    () -> assertEquals(0, blank.getMimickingMIndex()));
        }

        /**
         * The collections are present and empty, not missing, and the two maps keep insertion
         * order so the order they are walked in does not depend on how enum keys hash.
         */
        @Test
        @DisplayName("has empty collections, and the maps keep insertion order")
        void collectionsAreEmpty() {
            ItemObject blank = new ItemObject();

            assertAll(
                    () -> assertTrue(blank.getModifiers().isEmpty()),
                    () -> assertInstanceOf(LinkedHashMap.class, blank.getModifiers()),
                    () -> assertTrue(blank.getElInfo().isEmpty()),
                    () -> assertInstanceOf(LinkedHashMap.class, blank.getElInfo()),
                    () -> assertTrue(blank.getBrands().isEmpty()),
                    () -> assertTrue(blank.getSlays().isEmpty()),
                    () -> assertTrue(blank.getCurses().isEmpty()),
                    () -> assertNotNull(blank.getEffect()),
                    () -> assertTrue(blank.getEffect().isEmpty()),
                    () -> assertEquals(0, blank.getFlags().toList().size()),
                    () -> assertEquals(0, blank.getNotice().toList().size()));
        }

        /**
         * A blank item reads every modifier as zero, as C's zeroed array does, and takes a write
         * without first having to build a map.
         */
        @Test
        @DisplayName("reads every modifier as zero and takes a write")
        void modifiersReadZero() {
            ItemObject blank = new ItemObject();
            blank.putModifier(ObjectModifier.OM_STR, 4);

            assertAll(
                    () -> assertEquals(4, blank.getModifierValue(ObjectModifier.OM_STR)),
                    () -> assertEquals(0, blank.getModifierValue(ObjectModifier.OM_DEX)));
        }
    }

    /**
     * The full constructor, which assigns rather than copies, and the arguments it parses.
     */
    @Nested
    @DisplayName("the full constructor")
    class FullConstructor {

        /**
         * The collections are stored as given, so the item and the caller share them. A change
         * through the caller's handle shows in the item.
         */
        @Test
        @DisplayName("stores its collections by reference")
        void storesByReference() {
            Map<ObjectModifier, Integer> modifiers = new HashMap<>();
            Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
            Set<Brand> brands = new HashSet<>();
            Set<Slay> slays = new HashSet<>();
            List<Effect> effect = new ArrayList<>();
            ItemObject item = full(modifiers, elInfo, brands, slays, new LinkedHashMap<>(), effect, "0", "0");

            assertAll(
                    () -> assertSame(modifiers, item.getModifiers()),
                    () -> assertSame(elInfo, item.getElInfo()),
                    () -> assertSame(brands, item.getBrands()),
                    () -> assertSame(slays, item.getSlays()),
                    () -> assertSame(effect, item.getEffect()));
        }

        /**
         * The curse map is copied, unlike the other collections: the item keeps its own map, ordered
         * by curse index, so a curse put on the caller's map afterwards does not appear on the item.
         * The {@link CurseData} values are the caller's instances, not copies.
         */
        @Test
        @DisplayName("copies the entries of the curse map rather than sharing the map")
        void copiesTheCurseMap() {
            Curse siren = curse("siren");
            CurseData given = new CurseData(3, 7);
            LinkedHashMap<Curse, CurseData> curses = new LinkedHashMap<>();
            curses.put(siren, given);
            ItemObject item = full(new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(), curses,
                    new ArrayList<>(), "0", "0");

            curses.put(curse("itches"), new CurseData(1, 1));

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertSame(given, item.getCurses().get(siren)));
        }

        /**
         * The grid is the caller's {@link Loc}. It is immutable, so sharing it is safe where C
         * copies its {@code struct loc}.
         */
        @Test
        @DisplayName("keeps the grid it was given")
        void keepsTheGrid() {
            assertSame(Loc.zero, loaded().getGrid());
        }

        /**
         * A {@code null} collection stays null and the getters turn it into an empty answer, so a
         * caller never sees the null. {@code getEffect} is the exception, and hands it back.
         */
        @Test
        @DisplayName("absorbs null collections in the getters")
        void nullCollectionsAreAbsorbed() {
            ItemObject item = full(null, null, null, null, null, null, "0", "0");

            assertAll(
                    () -> assertTrue(item.getModifiers().isEmpty()),
                    () -> assertTrue(item.getElInfo().isEmpty()),
                    () -> assertTrue(item.getBrands().isEmpty()),
                    () -> assertTrue(item.getSlays().isEmpty()),
                    () -> assertTrue(item.getCurses().isEmpty()),
                    () -> assertNull(item.getEffect()));
        }

        /**
         * Reading a modifier off an item whose modifier map is null gives zero, as C's zeroed
         * array does. The one-argument read goes through the null-safe getter.
         */
        @Test
        @DisplayName("reads a modifier as zero when the map is null")
        void modifierOnNullMapIsZero() {
            ItemObject item = full(null, null, null, null, null, null, "0", "0");

            assertAll(
                    () -> assertEquals(0, item.getModifierValue(ObjectModifier.OM_STR)),
                    () -> assertEquals(0, item.getModifierValue(Stats.STAT_STR)));
        }

        /**
         * The recharge string goes through the dice parser, which answers null for the empty
         * string. The constructor swaps that null for a zero value, as C's zeroed
         * {@code random_value} would be, so the getter never answers null.
         */
        @Test
        @DisplayName("gives a zero recharge interval for an empty string")
        void emptyTimeIsZero() {
            Random time = full(new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(),
                    new LinkedHashMap<>(), new ArrayList<>(), "0", "").getTime();

            assertAll(
                    () -> assertEquals(0, time.getBase()),
                    () -> assertEquals(0, time.getDice()),
                    () -> assertEquals(0, time.getSides()),
                    () -> assertEquals(0, time.getMBonus()));
        }

        @Test
        @DisplayName("parses a flat recharge string")
        void flatTimeParses() {
            assertEquals(7, full(new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(),
                    new LinkedHashMap<>(), new ArrayList<>(), "0", "7").getTime().getBase());
        }

        /**
         * A non-number for the extra parameter is a data error and throws; a {@code null} does
         * too, since only the empty string is read as zero.
         */
        @Test
        @DisplayName("rejects a pValue that is not a number")
        void badPValueThrows() {
            assertAll(
                    () -> assertThrows(NumberFormatException.class, () -> full(new HashMap<>(), new HashMap<>(),
                            new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(), new ArrayList<>(), "x", "0")),
                    () -> assertThrows(NullPointerException.class, () -> full(new HashMap<>(), new HashMap<>(),
                            new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(), new ArrayList<>(), null, "0")));
        }
    }

    /**
     * The setters that take a whole collection. C assigns the array or pointer, so assigning an
     * item's collection to itself changes nothing, and the collection it replaces is left alone.
     */
    @Nested
    @DisplayName("whole-collection setters")
    class WholeCollections {

        private ItemObject item;

        @BeforeEach
        void setUp() {
            item = loaded();
        }

        /**
         * Handing the item its own live map back is a no-op, as C's self-assignment is.
         */
        @Test
        @DisplayName("setModifiers given the item's own map keeps its entries")
        void setModifiersSelfAssignment() {
            item.putModifier(ObjectModifier.OM_STR, 3);

            item.setModifiers(item.getModifiers());

            assertEquals(3, item.getModifierValue(ObjectModifier.OM_STR));
        }

        /**
         * The map being replaced is not cleared on the way out, so a map the item shared with
         * another is left as it was.
         */
        @Test
        @DisplayName("setModifiers leaves the map it replaces untouched")
        void setModifiersLeavesOldMap() {
            Map<ObjectModifier, Integer> old = new HashMap<>(Map.of(ObjectModifier.OM_STR, 2));
            item.setModifiers(old);

            item.setModifiers(new HashMap<>());

            assertAll(
                    () -> assertEquals(2, old.get(ObjectModifier.OM_STR)),
                    () -> assertEquals(0, item.getModifierValue(ObjectModifier.OM_STR)));
        }

        @Test
        @DisplayName("setModifiers stores the map as given")
        void setModifiersStoresAsGiven() {
            Map<ObjectModifier, Integer> given = new LinkedHashMap<>(Map.of(ObjectModifier.OM_DEX, 5));

            item.setModifiers(given);

            assertSame(given, item.getModifiers());
        }

        @Test
        @DisplayName("setModifiers(null) leaves an item that reads every modifier as zero")
        void setModifiersNull() {
            item.putModifier(ObjectModifier.OM_STR, 3);

            item.setModifiers(null);

            assertAll(
                    () -> assertTrue(item.getModifiers().isEmpty()),
                    () -> assertEquals(0, item.getModifierValue(ObjectModifier.OM_STR)));
        }

        @Test
        @DisplayName("setElInfo given the item's own map keeps its entries")
        void setElInfoSelfAssignment() {
            item.setElInfoResLevel(ElementEnum.ELEM_FIRE, 1);

            item.setElInfo(item.getElInfo());

            assertEquals(1, item.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
        }

        @Test
        @DisplayName("setElInfo leaves the map it replaces untouched")
        void setElInfoLeavesOldMap() {
            ItemObject other = loaded();
            Map<ElementEnum, ElementInfo> shared = new HashMap<>();
            ElementInfo info = new ElementInfo();
            info.setResLevel(3);
            shared.put(ElementEnum.ELEM_COLD, info);
            item.setElInfo(shared);
            other.setElInfo(shared);

            item.setElInfo(new HashMap<>());

            assertAll(
                    () -> assertEquals(3, other.getElInfo().get(ElementEnum.ELEM_COLD).getResLevel()),
                    () -> assertTrue(item.getElInfo().isEmpty()));
        }

        @Test
        @DisplayName("setElInfo(null) leaves an item that reads as having no element info")
        void setElInfoNull() {
            item.setElInfo(null);

            assertTrue(item.getElInfo().isEmpty());
        }

        @Test
        @DisplayName("setEffect turns null into an empty list and stores a list as given")
        void setEffectNullAndList() {
            List<Effect> given = new ArrayList<>();
            item.setEffect(given);
            assertSame(given, item.getEffect());

            item.setEffect(null);

            assertAll(
                    () -> assertNotNull(item.getEffect()),
                    () -> assertTrue(item.getEffect().isEmpty()));
        }

        @Test
        @DisplayName("setSlays stores the set as given, and null reads as no slays")
        void setSlaysStoresAndNull() {
            Set<Slay> given = new HashSet<>(Set.of(slay("EVIL_2")));
            item.setSlays(given);
            assertSame(given, item.getSlays());

            item.setSlays(null);

            assertTrue(item.getSlays().isEmpty());
        }

        /**
         * The brand and slay sets the item replaces are not cleared either.
         */
        @Test
        @DisplayName("clearing an item's slays does not reach a set it once shared")
        void slaysReplacedNotCleared() {
            Set<Slay> shared = new HashSet<>(Set.of(slay("EVIL_2")));
            item.setSlays(shared);

            item.setSlays(new HashSet<>());

            assertEquals(1, shared.size());
        }

        @Test
        @DisplayName("the brand editors work on a set the item was built with")
        void brandEditors() {
            Brand fire = brand("FIRE_2");
            item.addBrand(fire);
            assertTrue(item.getBrands().contains(fire));

            item.removeBrand(fire);
            assertTrue(item.getBrands().isEmpty());
        }
    }

    /**
     * Replacing the curses wholesale. C's {@code copy_curses} writes the power and a freshly
     * rolled timeout into every curse it is given, so a replacement carrying the same power and a
     * different timeout must not be mistaken for no change. The map handed in is copied, so
     * changes to it afterwards do not reach the item.
     */
    @Nested
    @DisplayName("replacing the curses")
    class ReplacingCurses {

        private ItemObject item;
        private Curse siren;
        private Curse teleport;

        @BeforeEach
        void setUp() {
            item = new ItemObject();
            siren = curse("siren");
            teleport = curse("teleportation");
        }

        /**
         * The writers are run against both replacing methods, which share a contract.
         */
        private void replaceWith(boolean viaSetCurses, Map<Curse, CurseData> given) {
            if (viaSetCurses) item.setCurses(given);
            else item.clearAndPutCurses(given);
        }

        @Test
        @DisplayName("a disjoint set replaces what was there")
        void disjointSetReplaces() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                item.addCurse(siren, 50, 12);

                replaceWith(viaSetCurses, Map.of(teleport, new CurseData(30, 4)));

                assertAll(
                        () -> assertEquals(1, item.getCurses().size()),
                        () -> assertEquals(30, item.getCurses().get(teleport).getPower()),
                        () -> assertNull(item.getCurses().get(siren)));
            }
        }

        /**
         * The same curse at the same power with a new timeout is a change. {@code CurseData}
         * compares by power alone, so a replacement guarded by map equality would drop it.
         */
        @Test
        @DisplayName("the same power with a new timeout takes the new timeout")
        void samePowerNewTimeout() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                item.addCurse(siren, 50, 12);

                replaceWith(viaSetCurses, Map.of(siren, new CurseData(50, 3)));

                assertEquals(3, item.getCurses().get(siren).getTimeout());
            }
        }

        @Test
        @DisplayName("the instance data handed in is the instance stored")
        void instanceIsShared() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                item.addCurse(siren, 50, 12);
                CurseData given = new CurseData(50, 3);

                replaceWith(viaSetCurses, Map.of(siren, given));

                assertSame(given, item.getCurses().get(siren));
            }
        }

        /**
         * The map is copied, not kept: later changes to the caller's map do not reach the item.
         */
        @Test
        @DisplayName("the map handed in is copied, not kept")
        void argumentIsCopied() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                Map<Curse, CurseData> given = new HashMap<>();
                given.put(siren, new CurseData(5, 1));

                replaceWith(viaSetCurses, given);
                given.put(teleport, new CurseData(9, 9));

                assertAll(
                        () -> assertEquals(1, item.getCurses().size()),
                        () -> assertNull(item.getCurses().get(teleport)));
            }
        }

        /**
         * Handing the item its own live view back leaves the curses as they were. C assigning an
         * array to itself changes nothing.
         */
        @Test
        @DisplayName("given the item's own curses view, the curses are kept")
        void ownViewIsSafe() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                item.addCurse(siren, 50, 12);
                item.addCurse(teleport, 20, 6);

                replaceWith(viaSetCurses, item.getCurses());

                assertAll(
                        () -> assertEquals(2, item.getCurses().size()),
                        () -> assertEquals(50, item.getCurses().get(siren).getPower()),
                        () -> assertEquals(6, item.getCurses().get(teleport).getTimeout()));
            }
        }

        @Test
        @DisplayName("an empty set takes every curse off")
        void emptySetClears() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                item.addCurse(siren, 50, 12);

                replaceWith(viaSetCurses, Map.of());

                assertTrue(item.getCurses().isEmpty());
            }
        }

        /**
         * The curses end up in ascending curse index, as C's loop over {@code obj->curses[i]} walks
         * them, whatever order the map handed in held them in.
         */
        @Test
        @DisplayName("the curses walk in curse index order, not the order they were handed in")
        void orderIsByIndex() {
            Curse low = curse("low", 1);
            Curse high = curse("high", 2);
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = new ItemObject();
                LinkedHashMap<Curse, CurseData> given = new LinkedHashMap<>();
                given.put(high, new CurseData(1, 1));
                given.put(low, new CurseData(2, 2));

                replaceWith(viaSetCurses, given);

                assertEquals(List.of(low, high), new ArrayList<>(item.getCurses().keySet()));
            }
        }

        /**
         * An item the full constructor built from a null curse map, which it reads as empty, takes a
         * replacement without throwing.
         */
        @Test
        @DisplayName("works on an item whose curse map is null")
        void nullMapIsReplaced() {
            for (boolean viaSetCurses : new boolean[]{true, false}) {
                item = full(new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(), null,
                        new ArrayList<>(), "0", "0");

                replaceWith(viaSetCurses, Map.of(siren, new CurseData(5, 1)));

                assertEquals(5, item.getCurses().get(siren).getPower());
            }
        }

        /**
         * Setting a power to zero takes the entry out of the map. C's zero power means "off", and in
         * the map "off" is a missing key, so a zero-power entry is never left behind. Calling
         * {@code removeCurse} afterwards finds nothing to remove and is quiet about it.
         */
        @Test
        @DisplayName("a power of zero removes the entry, and removeCurse then finds nothing")
        void zeroPowerRemovesEntry() {
            item.addCurse(siren, 50, 12);

            item.setCursePower(siren, 0);
            assertFalse(item.getCurses().containsKey(siren));
            assertTrue(item.getCurses().isEmpty());

            item.removeCurse(siren);
            assertTrue(item.getCurses().isEmpty());
        }
    }

    /**
     * The stat overload of {@code getModifierValue}, which resolves a stat to a modifier by name.
     */
    @Nested
    @DisplayName("modifier value by stat")
    class ByStat {

        /**
         * Each of the five stats reads its own modifier. Distinct values show that no two are
         * crossed.
         */
        @Test
        @DisplayName("each stat reads its own modifier")
        void eachStatReadsItsOwn() {
            ItemObject item = new ItemObject();
            item.putModifier(ObjectModifier.OM_STR, 1);
            item.putModifier(ObjectModifier.OM_INT, 2);
            item.putModifier(ObjectModifier.OM_WIS, 3);
            item.putModifier(ObjectModifier.OM_DEX, 4);
            item.putModifier(ObjectModifier.OM_CON, 5);

            assertAll(
                    () -> assertEquals(1, item.getModifierValue(Stats.STAT_STR)),
                    () -> assertEquals(2, item.getModifierValue(Stats.STAT_INT)),
                    () -> assertEquals(3, item.getModifierValue(Stats.STAT_WIS)),
                    () -> assertEquals(4, item.getModifierValue(Stats.STAT_DEX)),
                    () -> assertEquals(5, item.getModifierValue(Stats.STAT_CON)));
        }

        /**
         * The two sentinels resolve to {@code OM_NONE} and {@code OM_MAX}, which no item has a
         * value for, so they answer zero rather than throwing.
         */
        @Test
        @DisplayName("the sentinels answer zero rather than throwing")
        void sentinelsAnswerZero() {
            ItemObject item = new ItemObject();
            item.putModifier(ObjectModifier.OM_STR, 9);

            assertAll(
                    () -> assertEquals(0, item.getModifierValue(Stats.STAT_NONE)),
                    () -> assertEquals(0, item.getModifierValue(Stats.STAT_MAX)));
        }
    }
}
