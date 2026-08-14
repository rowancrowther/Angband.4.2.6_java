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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject}'s construction and its plain accessors — the port of C's
 * {@code struct object} ({@code src/object.h}) at rest, as opposed to the recharge arithmetic
 * covered by {@code ItemObjectRechargeTest} and the stacking rules covered by
 * {@code ItemObjectSimilarTest}.
 *
 * <p>Most of what is here is a round-trip, and would not be worth writing on its own. Three things
 * are: the long constructor does real work on four of its arguments rather than only assigning them
 * (it parses {@code pValue} from a string and three dice strings into {@link
 * uk.co.jackoftrades.middle.numerics.Random}s), the empty constructor has to leave the blank-slot
 * state C gets from {@code mem_zalloc}, and the two collection accessors disagree with each other
 * about mutability — {@link ItemObject#getBrands()} hands out the live set while
 * {@link ItemObject#getCurses()} wraps its map in an unmodifiable view. That asymmetry is easy to
 * trip over and so is pinned down here rather than left to be discovered.
 *
 * @author ClaudeCode
 */
class ItemObjectAccessorsTest {

    /**
     * Writes a private field on an {@link ItemObject} by reflection, for the fields with no setter.
     *
     * @param item  the item to modify
     * @param name  the declared field name
     * @param value the value to write
     * @author ClaudeCode
     */
    private static void set(ItemObject item, String name, Object value) {
        try {
            Field field = ItemObject.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(item, value);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("ItemObject." + name + " is no longer settable by reflection", e);
        }
    }

    /**
     * Builds an item through the long constructor with everything either empty or zero, so that a
     * test can state the one argument it cares about and let the rest be uninteresting.
     *
     * @param tValue the item type
     * @param pValue the extra-parameter value, as the constructor takes it: a string
     * @param note   the inscription
     * @return the constructed item
     * @author ClaudeCode
     */
    private static ItemObject item(TValue tValue, String pValue, String note) {
        return new ItemObject(new ObjectKind(), null, null, null, Loc.zero, tValue, 0, pValue,
                0, 0, 0, 0, "0", "0", "0", "0",
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), Map.of(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, note);
    }

    /**
     * Reads a private field on an {@link ItemObject} by reflection, for the fields with no getter.
     *
     * @param item the item to read
     * @param name the declared field name
     * @return the field's value
     * @author ClaudeCode
     */
    private static Object read(ItemObject item, String name) throws Exception {
        Field field = ItemObject.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(item);
    }

    /**
     * A minimal artifact definition. {@link ItemObject#isArtifact()} only asks whether the field is
     * null, so nothing here is read — the constructor is pure assignment, and the fixture exists
     * solely to be a non-null {@link Artifact}.
     *
     * @return an artifact with every field empty
     * @author ClaudeCode
     */
    private static Artifact artifact() {
        return new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), Map.of(),
                0, 0, 0, 0, null, null, null);
    }

    /**
     * A sanity check that the element map the constructor is given is the one the item keeps, since
     * {@code similar} walks it for every element including the sentinels and would throw on a gap.
     *
     * @author ClaudeCode
     */
    @Test
    @DisplayName("the element map is kept as given")
    void elementMapIsKept() throws Exception {
        Map<ElementEnum, ElementInfo> elInfo = Map.of(ElementEnum.ELEM_FIRE, new ElementInfo());
        ItemObject item = new ItemObject();
        set(item, "elInfo", elInfo);

        Field field = ItemObject.class.getDeclaredField("elInfo");
        field.setAccessible(true);
        assertSame(elInfo, field.get(item));
    }

    /**
     * @author ClaudeCode
     */
    @Test
    @DisplayName("the kind is kept as given")
    void kindIsKept() {
        ObjectKind kind = new ObjectKind();
        ItemObject item = new ItemObject(kind, null, null, null, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, "0", "0", "0", "0",
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), Map.of(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);

        assertSame(kind, item.getKind());
    }

    /**
     * The stack count is the one numeric field the constructor takes and the recharge code divides
     * by, so it is worth knowing it survives construction unaltered.
     *
     * @author ClaudeCode
     */
    @Test
    @DisplayName("the stack count is kept as given")
    void numberIsKept() {
        assertEquals(1, item(TValue.TV_SWORD, "0", null).getNumber());
    }

    /**
     * The empty constructor, which is C's {@code mem_zalloc}'d blank slot.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("a blank item")
    class Blank {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("has no kind, no location and no inscription")
        void isEmpty() {
            ItemObject item = new ItemObject();

            assertNull(item.getKind());
            assertNull(item.getGrid());
            assertNull(item.getNote());
        }

        /**
         * An item with no artifact definition is not an artifact — the test C writes as
         * {@code obj->artifact != NULL}.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("is not an artifact")
        void isNotAnArtifact() {
            assertFalse(new ItemObject().isArtifact());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("carries no charges and no timeout")
        void hasNoCharge() {
            ItemObject item = new ItemObject();

            assertEquals(0, item.getTimeout());
            assertEquals(0, item.getNumber());
            assertNull(item.getTime());
        }
    }

    /**
     * The long constructor, and specifically the four arguments it does not simply assign.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("the parsing constructor")
    class Parsing {

        /**
         * The empty string is how the data files spell "no pval", and it has to become zero rather
         * than reach {@code Integer.parseInt} — which is the guard the constructor opens with.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("reads an empty pValue as zero")
        void emptyPValue() throws Exception {
            ItemObject item = item(TValue.TV_SWORD, "", null);

            assertEquals(TValue.TV_SWORD, item.gettValue());
            assertEquals(0, read(item, "pValue"));
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("parses a numeric pValue")
        void numericPValue() throws Exception {
            ItemObject item = item(TValue.TV_WAND, "12", null);

            assertEquals(TValue.TV_WAND, item.gettValue());
            assertEquals(12, read(item, "pValue"));
        }

        /**
         * A dice string becomes a {@link uk.co.jackoftrades.middle.numerics.Random}, so {@code time}
         * is non-null even when the interval is a flat zero — which is what distinguishes a rod with
         * no wait from a non-rod, where C leaves the field alone entirely.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("turns the recharge string into a dice value")
        void recheargeStringBecomesDice() {
            ItemObject item = item(TValue.TV_ROD, "0", null);

            assertEquals(0, item.getTime().getBase());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("keeps the inscription it was given")
        void keepsTheNote() {
            ItemObject item = item(TValue.TV_POTION, "0", "@q1");

            assertEquals("@q1", item.getNote());
        }

        /**
         * C stores the inscription as a {@code quark_t} where {@code 0} means "uninscribed"; the
         * port holds the text, so {@code null} is the equivalent and callers test for it.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an uninscribed item has a null note rather than an empty one")
        void uninscribedIsNull() {
            assertNull(item(TValue.TV_POTION, "0", null).getNote());
        }
    }

    /**
     * The accessors with observable behaviour beyond returning a field.
     *
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("accessors")
    class Accessors {

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("the grid round-trips, and null means not on the floor")
        void gridRoundTrips() {
            ItemObject item = new ItemObject();

            item.setGrid(Loc.row(7).col(3));
            assertEquals(Loc.row(7).col(3), item.getGrid());

            item.setGrid(null);
            assertNull(item.getGrid());
        }

        /**
         * @author ClaudeCode
         */
        @Test
        @DisplayName("an item with an artifact definition is an artifact")
        void artifactIsDetected() {
            ItemObject item = new ItemObject();
            set(item, "artifact", artifact());

            assertTrue(item.isArtifact());
        }

        /**
         * {@code orNotice} is C's {@code obj->notice |= flag}, so it accumulates rather than
         * replaces — the name says so, and a setter would look identical from the call site.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("notice flags accumulate rather than replace")
        void noticeAccumulates() {
            ItemObject item = item(TValue.TV_SWORD, "0", null);

            item.orNotice(ObjectNotice.OBJ_NOTICE_WORN);
            item.orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);

            Flag<ObjectNotice> notice = noticeOf(item);
            assertTrue(notice.has(ObjectNotice.OBJ_NOTICE_WORN));
            assertTrue(notice.has(ObjectNotice.OBJ_NOTICE_ASSESSED));
            assertFalse(notice.has(ObjectNotice.OBJ_NOTICE_IGNORE));
        }

        /**
         * The brand set is handed out live, matching C, where {@code obj->brands} is an array on the
         * struct that callers read and write in place. Stated because the opposite choice would look
         * equally reasonable from the signature.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("getBrands returns the live set")
        void brandsAreLive() {
            Brand acid = new Brand("ACID_2", "acid", "burns", MonsterRaceFlag.RF_IM_FIRE,
                    MonsterRaceFlag.RF_HURT_FIRE, 17, 3, 15);
            ItemObject item = new ItemObject();
            Set<Brand> brands = new java.util.HashSet<>();
            set(item, "brands", brands);

            item.getBrands().add(acid);

            assertSame(brands, item.getBrands());
            assertTrue(brands.contains(acid));
        }

        /**
         * The curses accessor does <em>not</em> match {@link ItemObject#getBrands()}: it wraps the
         * map in an unmodifiable view, so a caller can read the curses but not add one. The two
         * neighbouring accessors making opposite choices is worth stating rather than leaving to be
         * discovered, since a caller reaching for one after using the other will not expect it.
         *
         * <p>It is a view and not a copy, so writes to the backing map are still seen through it.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("getCurses returns an unmodifiable view over the live map")
        void cursesAreAnUnmodifiableView() {
            Map<Curse.CurseEntry, Boolean> curses = new java.util.HashMap<>();
            ItemObject item = new ItemObject();
            set(item, "curses", curses);

            Map<Curse.CurseEntry, Boolean> view = item.getCurses();
            assertThrows(UnsupportedOperationException.class, () -> view.put(null, true));

            Curse.CurseEntry entry = new Curse.CurseEntry(null, null);
            curses.put(entry, true);
            assertEquals(1, view.size());
            assertTrue(view.get(entry));
        }

        /**
         * The two monster indices have setters and no getters, so the round-trip is read back by
         * reflection. They are worth covering at all because {@code similar} refuses to stack a
         * mimicking item, and that refusal is only as good as the field it reads.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("the monster indices round-trip")
        void monsterIndicesRoundTrip() throws Exception {
            ItemObject item = new ItemObject();

            item.setHeldMIndex(4);
            item.setMimickingMIndex(9);

            assertEquals(4, read(item, "heldMIndex"));
            assertEquals(9, read(item, "mimickingMIndex"));
        }

        @SuppressWarnings("unchecked")
        private Flag<ObjectNotice> noticeOf(ItemObject item) {
            try {
                Field field = ItemObject.class.getDeclaredField("notice");
                field.setAccessible(true);
                return (Flag<ObjectNotice>) field.get(item);
            } catch (ReflectiveOperationException e) {
                throw new AssertionError("ItemObject.notice is no longer readable by reflection", e);
            }
        }

    }
}
