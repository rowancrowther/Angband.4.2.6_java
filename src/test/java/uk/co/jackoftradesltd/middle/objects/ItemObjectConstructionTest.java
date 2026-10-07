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
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Method;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.read;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the construction, copy, wipe and collection-editing surface of {@link ItemObject} that
 * round two of the {@code ItemObject} pass calls Part A: {@code object_new}, {@code object_copy} and
 * {@code object_wipe} ({@code obj-pile.c}), the loop in {@code copy_curses} and the array edits of
 * {@code obj-curse.c} that the curse methods stand for, and the {@code bool} arrays of brands and
 * slays.
 *
 * <p><b>Expected values come from the C.</b> {@code object_new} is a {@code mem_zalloc}, so a blank
 * item holds zero in every member. {@code object_copy} is a {@code memcpy} followed by a fresh
 * duplicate of the slay, brand and curse arrays, so a copy walks every ordered container in the order
 * of the original and shares only the pointers. {@code copy_curses} begins {@code if (!source) return;}
 * and so leaves the object alone for a {@code NULL} source. The tests that pin a place where the
 * port deliberately differs from {@code append_object_curse} and {@code remove_object_curse} say so
 * in their names.
 *
 * <p>The other {@code ItemObject*Test} classes already cover the individual curse writers, the copy
 * independence rules and the wipe field resets; what is here is what they leave open: the blank
 * item's defaults, the full constructor, the order of the copied maps, the {@code null} arguments,
 * and the brand and slay editors.
 *
 * <p>Class ItemObjectConstructionTest coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
class ItemObjectConstructionTest {

    private ItemObject item;
    private Curse low;
    private Curse high;

    /**
     * A minimal curse definition with the given registry index, the figure {@link ItemObject#CURSE_ORDER}
     * sorts on.
     */
    private static Curse curse(String name, int index) {
        return uk.co.jackoftradesltd.testsupport.CurseFixture.curse(name, List.of(), 0, null,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0, List.of(),
                new Flag<>(ObjectFlag.class), "", "", index);
    }

    private static Brand brand(String code) {
        return new Brand(code, "fire", "burns", null, null, 3, 3, 5);
    }

    private static Slay slay(String code) {
        return new Slay(code, "evil", null, "smite", "pierces", null, 2, 2, 5);
    }

    private static void nullField(ItemObject target, String name) {
        set(target, name, null);
    }

    @BeforeEach
    void setUp() {
        item = new ItemObject();
        low = curse("low", 2);
        high = curse("high", 5);
    }

    /**
     * What {@code mem_zalloc(sizeof(struct object))} leaves.
     */
    @Nested
    @DisplayName("a blank item, as object_new leaves it")
    class Blank {

        @Test
        @DisplayName("every number is zero, origin is ORIGIN_NONE and the type is TV_NONE")
        void numbersAreZero() {
            assertAll(
                    () -> assertEquals(0, item.getNumber()),
                    () -> assertEquals(0, item.getWeight()),
                    () -> assertEquals(0, item.getToHit()),
                    () -> assertEquals(0, item.getToDam()),
                    () -> assertEquals(0, item.getToAC()),
                    () -> assertEquals(0, item.getBaseAC()),
                    () -> assertEquals(0, item.getDamageDice()),
                    () -> assertEquals(0, item.getDamageSides()),
                    () -> assertEquals(0, item.getTimeout()),
                    () -> assertEquals(0, item.getMimickingMIndex()),
                    () -> assertEquals(0, read(item, "heldMIndex")),
                    () -> assertEquals(0, read(item, "sValue")),
                    () -> assertEquals(0, read(item, "pValue")),
                    () -> assertEquals(0, read(item, "originDepth")),
                    () -> assertEquals(ObjectOriginEnum.ORIGIN_NONE, read(item, "origin")),
                    () -> assertEquals(TValue.TV_NONE, item.gettValue()));
        }

        @Test
        @DisplayName("the pointers C leaves NULL are null")
        void pointersAreNull() {
            assertAll(
                    () -> assertNull(item.getKind()),
                    () -> assertNull(item.getEgo()),
                    () -> assertNull(read(item, "artifact")),
                    () -> assertNull(item.getKnown()),
                    () -> assertNull(read(item, "location")),
                    () -> assertNull(read(item, "effectMessage")),
                    () -> assertNull(read(item, "activation")),
                    () -> assertNull(read(item, "originRace")),
                    () -> assertNull(read(item, "note")));
        }

        @Test
        @DisplayName("the recharge time is a zero Random, C's four zero dice, and never null")
        void timeIsZeroRandom() {
            Random time = item.getTime();

            assertAll(
                    () -> assertNotNull(time),
                    () -> assertEquals(0, time.getBase()),
                    () -> assertEquals(0, time.getDice()),
                    () -> assertEquals(0, time.getSides()),
                    () -> assertEquals(0, time.getMBonus()));
        }

        @Test
        @DisplayName("the collections C leaves NULL or zeroed are empty, not null")
        void collectionsAreEmpty() {
            assertAll(
                    () -> assertTrue(((Flag<?>) read(item, "flags")).isEmpty()),
                    () -> assertTrue(((Flag<?>) read(item, "notice")).isEmpty()),
                    () -> assertTrue(item.getModifiers().isEmpty()),
                    () -> assertTrue(item.getElInfo().isEmpty()),
                    () -> assertTrue(item.getBrands().isEmpty()),
                    () -> assertTrue(item.getSlays().isEmpty()),
                    () -> assertTrue(item.getCurses().isEmpty()),
                    () -> assertTrue(item.getEffect().isEmpty()));
        }

        @Test
        @DisplayName("the curse map is a real, empty map that walks in curse index order")
        void curseMapIsOrdered() {
            item.addCurse(high, 1, 0);
            item.addCurse(low, 1, 0);

            assertEquals(List.of(low, high), new ArrayList<>(item.getCurses().keySet()));
        }

        @Test
        @DisplayName("two blank items share no collection")
        void blanksShareNothing() {
            ItemObject other = new ItemObject();

            item.addCurse(low, 1, 0);
            item.addBrand(brand("FIRE_3"));
            item.addSlay(slay("EVIL_2"));

            assertAll(
                    () -> assertTrue(other.getCurses().isEmpty()),
                    () -> assertTrue(other.getBrands().isEmpty()),
                    () -> assertTrue(other.getSlays().isEmpty()));
        }
    }

    /**
     * The constructor that takes every field. C has no counterpart, so the expectations are what the
     * Javadoc promises plus the one C fact that applies: a curse map that arrives as {@code NULL}
     * is an item with no curses.
     */
    @Nested
    @DisplayName("the full constructor")
    class FullConstructor {

        private Map<Curse, CurseData> callerCurses;
        private Flag<ObjectFlag> callerFlags;
        private Map<ObjectModifier, Integer> callerModifiers;
        private Map<ElementEnum, ElementInfo> callerElInfo;
        private Set<Brand> callerBrands;
        private Set<Slay> callerSlays;
        private List<Effect> callerEffect;

        @BeforeEach
        void callerCollections() {
            callerCurses = new HashMap<>();
            callerCurses.put(high, new CurseData(40, 6));
            callerCurses.put(low, new CurseData(20, 3));
            callerFlags = new Flag<>(ObjectFlag.class);
            callerModifiers = new LinkedHashMap<>();
            callerElInfo = new LinkedHashMap<>();
            callerBrands = new HashSet<>();
            callerSlays = new HashSet<>();
            callerEffect = new ArrayList<>();
        }

        private ItemObject build(String pValue, String time, Map<Curse, CurseData> curses,
                                 Flag<ObjectFlag> flags, Flag<ObjectNotice> notice) {
            return new ItemObject(null, null, null, null, null, TValue.TV_SWORD, 4, pValue, 25, 2, 5,
                    6, 1, "", 2, 3, flags, callerModifiers, callerElInfo, callerBrands, callerSlays,
                    curses, callerEffect, "It hums.", null, time, 9, 7, notice, 5, 6,
                    ObjectOriginEnum.ORIGIN_FLOOR, 8, null, "@w1");
        }

        @Test
        @DisplayName("scalar arguments land in their members")
        void scalarsLand() {
            ItemObject built = build("12", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            assertAll(
                    () -> assertEquals(TValue.TV_SWORD, built.gettValue()),
                    () -> assertEquals(4, read(built, "sValue")),
                    () -> assertEquals(12, read(built, "pValue")),
                    () -> assertEquals(25, built.getWeight()),
                    () -> assertEquals(2, built.getDamageDice()),
                    () -> assertEquals(5, built.getDamageSides()),
                    () -> assertEquals(6, built.getBaseAC()),
                    () -> assertEquals(1, built.getToAC()),
                    () -> assertEquals(2, built.getToDam()),
                    () -> assertEquals(3, built.getToHit()),
                    () -> assertEquals(9, built.getTimeout()),
                    () -> assertEquals(7, built.getNumber()),
                    () -> assertEquals(5, read(built, "heldMIndex")),
                    () -> assertEquals(6, built.getMimickingMIndex()),
                    () -> assertEquals(ObjectOriginEnum.ORIGIN_FLOOR, read(built, "origin")),
                    () -> assertEquals(8, read(built, "originDepth")),
                    () -> assertEquals("@w1", read(built, "note")),
                    () -> assertEquals("It hums.", read(built, "effectMessage")));
        }

        @Test
        @DisplayName("an empty pValue string is zero")
        void emptyPValueIsZero() {
            ItemObject built = build("", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            assertEquals(0, read(built, "pValue"));
        }

        @Test
        @DisplayName("an empty time string is a zero Random, not null")
        void emptyTimeIsZeroRandom() {
            ItemObject built = build("0", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            Random time = built.getTime();
            assertAll(
                    () -> assertNotNull(time),
                    () -> assertEquals(0, time.getBase()),
                    () -> assertEquals(0, time.getDice()),
                    () -> assertEquals(0, time.getSides()));
        }

        @Test
        @DisplayName("the curse map is copied, so a later change to the caller's map does not reach the item")
        void curseMapIsCopied() {
            ItemObject built = build("0", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            callerCurses.remove(low);
            callerCurses.put(curse("extra", 9), new CurseData(1, 1));

            assertAll(
                    () -> assertEquals(2, built.getCurses().size()),
                    () -> assertTrue(built.getCurses().containsKey(low)),
                    () -> assertTrue(built.getCurses().containsKey(high)));
        }

        @Test
        @DisplayName("the copied curse map walks in curse index order whatever the caller's order")
        void curseMapIsOrdered() {
            ItemObject built = build("0", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            assertEquals(List.of(low, high), new ArrayList<>(built.getCurses().keySet()));
        }

        @Test
        @DisplayName("the CurseData instances are shared with the caller, as the Javadoc says")
        void curseDataIsShared() {
            CurseData data = callerCurses.get(low);

            ItemObject built = build("0", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            assertSame(data, built.getCurses().get(low));
        }

        @Test
        @DisplayName("a null curse map gives an item with no curses, as C reads a NULL array")
        void nullCurseMapIsEmpty() {
            ItemObject built = build("0", "", null, callerFlags, new Flag<>(ObjectNotice.class));

            assertAll(
                    () -> assertNotNull(read(built, "curses")),
                    () -> assertTrue(built.getCurses().isEmpty()));
        }

        @Test
        @DisplayName("null flags and notice arguments become empty sets")
        void nullFlagSetsBecomeEmpty() {
            ItemObject built = build("0", "", callerCurses, null, null);

            assertAll(
                    () -> assertNotNull(read(built, "flags")),
                    () -> assertNotNull(read(built, "notice")),
                    () -> assertTrue(((Flag<?>) read(built, "flags")).isEmpty()),
                    () -> assertTrue(((Flag<?>) read(built, "notice")).isEmpty()));
        }

        @Test
        @DisplayName("every other collection is stored by reference")
        void otherCollectionsAreShared() {
            ItemObject built = build("0", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            assertAll(
                    () -> assertSame(callerFlags, read(built, "flags")),
                    () -> assertSame(callerModifiers, read(built, "modifiers")),
                    () -> assertSame(callerElInfo, read(built, "elInfo")),
                    () -> assertSame(callerBrands, read(built, "brands")),
                    () -> assertSame(callerSlays, read(built, "slays")),
                    () -> assertSame(callerEffect, read(built, "effect")));
        }

        @Test
        @DisplayName("a new item belongs to no pile")
        void belongsToNoPile() {
            ItemObject built = build("0", "", callerCurses, callerFlags, new Flag<>(ObjectNotice.class));

            assertNull(built.getOwningPile());
        }
    }

    /**
     * What {@code object_copy} adds to the {@code memcpy}: arrays duplicated, pointers shared.
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        @Test
        @DisplayName("the modifier map keeps the original's walk order")
        void modifierOrderIsKept() {
            ObjectModifier[] order = {ObjectModifier.OM_INFRA, ObjectModifier.OM_STEALTH, ObjectModifier.OM_CON,
                    ObjectModifier.OM_DEX, ObjectModifier.OM_WIS, ObjectModifier.OM_INT, ObjectModifier.OM_STR};
            Map<ObjectModifier, Integer> mods = new LinkedHashMap<>();
            int value = 1;
            for (ObjectModifier modifier : order)
                mods.put(modifier, value++);
            item.setModifiers(mods);

            ItemObject copy = item.copy(false);

            assertAll(
                    () -> assertEquals(List.of(order), new ArrayList<>(copy.getModifiers().keySet())),
                    () -> assertEquals(new ArrayList<>(item.getModifiers().values()),
                            new ArrayList<>(copy.getModifiers().values())));
        }

        @Test
        @DisplayName("the element map keeps the original's walk order")
        void elementOrderIsKept() {
            ElementEnum[] order = {ElementEnum.ELEM_FIRE, ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC};
            Map<ElementEnum, ElementInfo> elements = new LinkedHashMap<>();
            for (ElementEnum element : order)
                elements.put(element, new ElementInfo());
            item.setElInfo(elements);

            ItemObject copy = item.copy(false);

            assertEquals(List.of(order), new ArrayList<>(copy.getElInfo().keySet()));
        }

        @Test
        @DisplayName("the copy's curse map walks in curse index order and rebuilds each entry")
        void curseOrderIsKept() {
            item.addCurse(high, 40, 6);
            item.addCurse(low, 20, 3);

            ItemObject copy = item.copy(false);

            assertAll(
                    () -> assertEquals(List.of(low, high), new ArrayList<>(copy.getCurses().keySet())),
                    () -> assertEquals(20, copy.getCurses().get(low).getPower()),
                    () -> assertEquals(3, copy.getCurses().get(low).getTimeout()),
                    () -> assertNotSame(item.getCurses().get(low), copy.getCurses().get(low)));
        }

        @Test
        @DisplayName("the effect list is shared, as C copies the struct effect pointer")
        void effectIsShared() {
            List<Effect> effects = new ArrayList<>();
            item.setEffect(effects);

            ItemObject copy = item.copy(false);

            assertSame(effects, read(copy, "effect"));
        }

        @Test
        @DisplayName("the activation list is shared, as C copies the struct activation pointer")
        void activationIsShared() {
            List<Activation> activations = new ArrayList<>();
            set(item, "activation", activations);

            ItemObject copy = item.copy(false);

            assertSame(activations, read(copy, "activation"));
        }

        @Test
        @DisplayName("the copy belongs to no pile, as object_copy detaches prev and next")
        void copyIsDetached() {
            ItemObject copy = item.copy(false);

            assertNull(copy.getOwningPile());
        }

        @Test
        @DisplayName("an item with no recharge time copies to a zero Random, never null")
        void nullTimeCopiesToZero() {
            set(item, "time", null);

            ItemObject copy = item.copy(false);

            assertAll(
                    () -> assertNotNull(copy.getTime()),
                    () -> assertEquals(0, copy.getTime().getDice()));
        }
    }

    /**
     * The one rule of {@code object_wipe} the field-reset tests do not state: a wiped item's curse map
     * is as ordered as a new one's.
     */
    @Nested
    @DisplayName("wipe")
    class Wipe {

        @Test
        @DisplayName("the wiped curse map walks in curse index order")
        void wipedCurseMapIsOrdered() {
            item.addCurse(low, 1, 0);

            item.wipe();
            item.addCurse(high, 1, 0);
            item.addCurse(low, 1, 0);

            assertEquals(List.of(low, high), new ArrayList<>(item.getCurses().keySet()));
        }

        @Test
        @DisplayName("a wiped item keeps its player snapshot, which C has no counterpart for")
        void playerIsKept() {
            Object before = read(item, "player");

            item.wipe();

            assertSame(before, read(item, "player"));
        }

        @Test
        @DisplayName("initCurses empties the map even when it held curses, with no NULL guard as in copy_curses")
        void initCursesDiscards() {
            item.addCurse(low, 1, 0);

            item.initCurses();

            assertTrue(item.getCurses().isEmpty());
        }
    }

    /**
     * The {@code null} arguments of the curse editors. C's {@code copy_curses} starts
     * {@code if (!source) return;}, so a {@code NULL} source leaves the object as it was.
     */
    @Nested
    @DisplayName("null arguments to the curse editors")
    class NullArguments {

        @BeforeEach
        void carryACurse() {
            item.addCurse(low, 30, 4);
        }

        @Test
        @DisplayName("setCurses(null) leaves the curses the item had")
        void setCursesNullKeepsCurses() {
            item.setCurses(null);

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertEquals(30, item.getCurses().get(low).getPower()));
        }

        @Test
        @DisplayName("clearAndPutCurses(null) leaves the curses the item had")
        void clearAndPutNullKeepsCurses() {
            item.clearAndPutCurses(null);

            assertAll(
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertEquals(30, item.getCurses().get(low).getPower()));
        }

        @Test
        @DisplayName("setCurses with an empty map does clear, so the null guard is not an empty guard")
        void setCursesEmptyClears() {
            item.setCurses(Map.of());

            assertTrue(item.getCurses().isEmpty());
        }

        @Test
        @DisplayName("setCurses puts the entries in curse index order whatever order the argument is in")
        void setCursesOrders() {
            Map<Curse, CurseData> given = new LinkedHashMap<>();
            given.put(high, new CurseData(1, 0));
            given.put(low, new CurseData(1, 0));

            item.setCurses(given);

            assertEquals(List.of(low, high), new ArrayList<>(item.getCurses().keySet()));
        }

        @Test
        @DisplayName("addCurses(null) throws, the null being the parameter and not the field")
        void addCursesNullThrows() {
            assertThrows(NullPointerException.class, () -> item.addCurses(null));
        }

        @Test
        @DisplayName("a null curse is ignored by both addCurse forms and by removeCurse")
        void nullCurseIsIgnored() {
            item.addCurse(null, 5, 5);
            item.addCurse(null, new CurseData(5, 5));
            item.removeCurse(null);

            assertEquals(1, item.getCurses().size());
        }
    }

    /**
     * Where the curse writers deliberately differ from {@code append_object_curse} and
     * {@code remove_object_curse} ({@code obj-curse.c}). Each test states C's answer in its name and
     * pins the port's, so a later port of those functions has to change them knowingly.
     */
    @Nested
    @DisplayName("deliberate differences from append_object_curse and remove_object_curse")
    class Simplifications {

        @Test
        @DisplayName("addCurse over a stronger curse replaces it, where C refuses unless power > existing")
        void addCurseReplacesStrongerCurse() {
            item.addCurse(low, 70, 9);

            item.addCurse(low, 50, 1);

            assertAll(
                    () -> assertEquals(50, item.getCurses().get(low).getPower()),
                    () -> assertEquals(1, item.getCurses().get(low).getTimeout()));
        }

        @Test
        @DisplayName("addCurse keeps the timeout it is given, where C rolls the curse object's time")
        void addCurseKeepsGivenTimeout() {
            item.addCurse(low, 50, 0);

            assertEquals(0, item.getCurses().get(low).getTimeout());
        }

        @Test
        @DisplayName("addCurse stores a power-zero entry, where C's array slot at zero is no curse")
        void addCurseStoresPowerZero() {
            item.addCurse(low, 0, 0);

            assertTrue(item.getCurses().containsKey(low));
        }

        @Test
        @DisplayName("removeCurse removes a power-zero entry, where remove_object_curse returns false and leaves it")
        void removeCurseRemovesPowerZero() {
            item.addCurse(low, 0, 0);

            item.removeCurse(low);

            assertFalse(item.getCurses().containsKey(low));
        }

        @Test
        @DisplayName("removing the last curse leaves an empty map, as check_object_curses frees the array")
        void removingLastCurseEmptiesMap() {
            item.addCurse(low, 5, 0);

            item.removeCurse(low);

            assertTrue(item.getCurses().isEmpty());
        }

        @Test
        @DisplayName("setCursePower on an absent curse writes a timeout of zero, as a zalloc'd slot holds")
        void setCursePowerAddsWithZeroTimeout() {
            item.setCursePower(high, 5);

            assertAll(
                    () -> assertEquals(5, item.getCurses().get(high).getPower()),
                    () -> assertEquals(0, item.getCurses().get(high).getTimeout()));
        }
    }

    /**
     * The brand and slay editors stand for {@code obj->brands[i] = true/false} and for freeing the
     * array. The sets can be {@code null} (a full constructor handed {@code null}, or a copy of such an
     * item), where C's arrays can be {@code NULL}.
     */
    @Nested
    @DisplayName("brand and slay editors")
    class BrandsAndSlays {

        @Test
        @DisplayName("addBrand and addSlay create the set when the field is null")
        void addCreatesTheSet() {
            nullField(item, "brands");
            nullField(item, "slays");

            Brand fire = brand("FIRE_3");
            Slay evil = slay("EVIL_2");
            item.addBrand(fire);
            item.addSlay(evil);

            assertAll(
                    () -> assertEquals(Set.of(fire), item.getBrands()),
                    () -> assertEquals(Set.of(evil), item.getSlays()));
        }

        @Test
        @DisplayName("adding the same brand twice is the same as adding it once")
        void addingTwiceIsOnce() {
            Brand fire = brand("FIRE_3");

            item.addBrand(fire);
            item.addBrand(fire);

            assertEquals(1, item.getBrands().size());
        }

        @Test
        @DisplayName("removing a brand or slay the item does not carry is quiet, as assigning false over false")
        void removingAbsentIsQuiet() {
            item.addBrand(brand("FIRE_3"));

            item.removeBrand(brand("COLD_3"));
            item.removeSlay(slay("EVIL_2"));

            assertAll(
                    () -> assertEquals(1, item.getBrands().size()),
                    () -> assertTrue(item.getSlays().isEmpty()));
        }

        @Test
        @DisplayName("removeBrand and removeSlay on a null field leave an empty set, not null")
        void removeOnNullFieldMakesEmptySet() {
            nullField(item, "brands");
            nullField(item, "slays");

            item.removeBrand(brand("FIRE_3"));
            item.removeSlay(slay("EVIL_2"));

            assertAll(
                    () -> assertNotNull(read(item, "brands")),
                    () -> assertNotNull(read(item, "slays")));
        }

        @Test
        @DisplayName("removing one brand leaves the others")
        void removeLeavesOthers() {
            Brand fire = brand("FIRE_3");
            Brand cold = brand("COLD_3");
            item.addBrand(fire);
            item.addBrand(cold);

            item.removeBrand(fire);

            assertEquals(Set.of(cold), item.getBrands());
        }

        @Test
        @DisplayName("clearBrands and clearSlays empty the sets, and are safe on a null field")
        void clearEmptiesTheSets() {
            item.addBrand(brand("FIRE_3"));
            item.addSlay(slay("EVIL_2"));
            item.clearBrands();
            item.clearSlays();

            nullField(item, "brands");
            nullField(item, "slays");
            item.clearBrands();
            item.clearSlays();

            assertAll(
                    () -> assertTrue(item.getBrands().isEmpty()),
                    () -> assertTrue(item.getSlays().isEmpty()));
        }

        @Test
        @DisplayName("getBrands and getSlays answer an immutable empty set for a null field")
        void gettersAnswerImmutableEmpty() {
            nullField(item, "brands");
            nullField(item, "slays");

            assertAll(
                    () -> assertTrue(item.getBrands().isEmpty()),
                    () -> assertTrue(item.getSlays().isEmpty()),
                    () -> assertThrows(UnsupportedOperationException.class, () -> item.getBrands().add(brand("FIRE_3"))),
                    () -> assertThrows(UnsupportedOperationException.class, () -> item.getSlays().add(slay("EVIL_2"))));
        }

        @Test
        @DisplayName("getBrands and getSlays hand out the live set, as C hands out the array pointer")
        void gettersAreLive() {
            item.addBrand(brand("FIRE_3"));
            item.addSlay(slay("EVIL_2"));

            Set<Brand> brands = item.getBrands();
            Set<Slay> slays = item.getSlays();
            item.addBrand(brand("COLD_3"));
            item.addSlay(slay("ORC_2"));

            assertAll(
                    () -> assertEquals(2, brands.size()),
                    () -> assertEquals(2, slays.size()));
        }

        @Test
        @DisplayName("setSlays stores the set it is given, and null reads back as an empty set")
        void setSlaysStoresTheSet() {
            Set<Slay> given = new HashSet<>();
            given.add(slay("EVIL_2"));

            item.setSlays(given);
            assertSame(given, item.getSlays());

            item.setSlays(null);
            assertTrue(item.getSlays().isEmpty());
        }
    }

    /**
     * The three private helpers {@code curse_power} reaches through {@code mem_free}: curses, then
     * brands and slays, on a scratch copy.
     */
    @Nested
    @DisplayName("free helpers on a scratch copy")
    class FreeHelpers {

        private void call(ItemObject target, String name) throws ReflectiveOperationException {
            Method method = ItemObject.class.getDeclaredMethod(name);
            method.setAccessible(true);
            method.invoke(target);
        }

        @Test
        @DisplayName("each helper empties its collection on the copy and leaves the original alone")
        void helpersEmptyTheCopyOnly() throws ReflectiveOperationException {
            item.addCurse(low, 30, 4);
            item.addBrand(brand("FIRE_3"));
            item.addSlay(slay("EVIL_2"));
            ItemObject scratch = item.copy(true);

            call(scratch, "freeCurses");
            call(scratch, "freeBrands");
            call(scratch, "freeSlays");

            assertAll(
                    () -> assertTrue(scratch.getCurses().isEmpty()),
                    () -> assertTrue(scratch.getBrands().isEmpty()),
                    () -> assertTrue(scratch.getSlays().isEmpty()),
                    () -> assertEquals(1, item.getCurses().size()),
                    () -> assertEquals(1, item.getBrands().size()),
                    () -> assertEquals(1, item.getSlays().size()));
        }

        @Test
        @DisplayName("an emptied copy still takes writes, the empty collections being real")
        void emptiedCopyTakesWrites() throws ReflectiveOperationException {
            ItemObject scratch = item.copy(true);
            call(scratch, "freeCurses");
            call(scratch, "freeBrands");
            call(scratch, "freeSlays");

            scratch.addCurse(high, 1, 0);
            scratch.addBrand(brand("FIRE_3"));
            scratch.addSlay(slay("EVIL_2"));

            assertAll(
                    () -> assertEquals(1, scratch.getCurses().size()),
                    () -> assertEquals(1, scratch.getBrands().size()),
                    () -> assertEquals(1, scratch.getSlays().size()));
        }
    }
}
