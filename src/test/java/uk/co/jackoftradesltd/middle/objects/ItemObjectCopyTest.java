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

import uk.co.jackoftradesltd.middle.numerics.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.read;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link ItemObject#copy(boolean)}, the port of C's {@code object_copy} ({@code obj-pile.c}).
 *
 * <p>C copies the struct with {@code memcpy} and then gives the copy its own arrays for slays,
 * brands and curses, so every other field arrives by value. The port has to do the same by hand, and
 * what is worth pinning is the line it draws: scalars and shared templates arrive as they are, and
 * every mutable container is rebuilt so that writing to the copy never writes to the original.
 *
 * <p>The one deliberate departure is the known half. C's {@code memcpy} carries the {@code known}
 * pointer across, so the copy aliases the original's known object; the port copies the known half
 * when asked and otherwise leaves it null. Nothing in C reads the aliased pointer, which is why the
 * difference does not show.
 *
 * @author Rowan Crowther
 */
class ItemObjectCopyTest {

    /**
     * The race the original was dropped by, shared by reference with the copy.
     */
    private MonsterRace race;

    /**
     * The kind the original is built on, shared by reference with the copy.
     */
    private ObjectKind kind;

    /**
     * The curse laid on the original.
     */
    private Curse curse;

    /**
     * The item under test, with every field given a distinguishable value.
     */
    private ItemObject original;

    /**
     * A minimal curse definition, non-null and nothing more.
     *
     * @return a curse with every other field empty
     */
    private static Curse curse() {
        return new Curse("test", List.of(), 0, null, new Flag<>(ObjectFlag.class), Map.of(), Map.of(),
                0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * Builds the original with a value in every field the copy has to carry.
     */
    @BeforeEach
    void buildOriginal() {
        race = new MonsterRace();
        kind = ItemFixture.loadedKind(TValue.TV_SWORD, "sword", 1);
        curse = curse();

        original = ItemFixture.item(TValue.TV_SWORD).kind(kind).number(7).timeout(9)
                .flags(ObjectFlag.OF_SUST_STR)
                .curse(curse, new CurseData(40, 6))
                .location(Loc.row(3).col(4))
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 8, race)
                .build();
        set(original, "sValue", 3);
        set(original, "pValue", 4);
        set(original, "weight", 25);
        set(original, "damageDice", 2);
        set(original, "damageSides", 5);
        set(original, "baseAC", 6);
        set(original, "toAC", 1);
        set(original, "toDam", 2);
        set(original, "toHit", 3);
        set(original, "heldMIndex", 5);
        set(original, "mimickingMIndex", 6);
        set(original, "effectMessage", "It hums.");
        set(original, "note", "@w1");
        // getNotice() answers a defensive copy, so the field itself is written.
        @SuppressWarnings("unchecked")
        Flag<ObjectNotice> notice = (Flag<ObjectNotice>) read(original, "notice");
        notice.on(ObjectNotice.OBJ_NOTICE_ASSESSED);
    }

    /**
     * What arrives by value.
     */
    @Nested
    @DisplayName("scalars and shared templates")
    class Carried {

        /**
         * Every plain field arrives with the value it had.
         */
        @Test
        @DisplayName("scalar fields are copied")
        void scalarsAreCopied() {
            ItemObject copy = original.copy(false);

            assertEquals(3, read(copy, "sValue"));
            assertEquals(4, read(copy, "pValue"));
            assertEquals(25, read(copy, "weight"));
            assertEquals(2, read(copy, "damageDice"));
            assertEquals(5, read(copy, "damageSides"));
            assertEquals(6, read(copy, "baseAC"));
            assertEquals(1, read(copy, "toAC"));
            assertEquals(2, read(copy, "toDam"));
            assertEquals(3, read(copy, "toHit"));
            assertEquals(9, read(copy, "timeout"));
            assertEquals(7, copy.getNumber());
            assertEquals(5, read(copy, "heldMIndex"));
            assertEquals(6, read(copy, "mimickingMIndex"));
            assertEquals(8, read(copy, "originDepth"));
            assertEquals(ObjectOriginEnum.ORIGIN_FLOOR, read(copy, "origin"));
            assertEquals("@w1", copy.getNote());
            assertEquals("It hums.", read(copy, "effectMessage"));
            assertEquals(TValue.TV_SWORD, read(copy, "tValue"));
        }

        /**
         * The kind and the monster race are registry entries, shared by reference as C shares the
         * pointers: identity is what tells two origins apart.
         */
        @Test
        @DisplayName("the kind and the origin race are shared")
        void templatesAreShared() {
            ItemObject copy = original.copy(false);

            assertSame(kind, copy.getKind());
            assertSame(race, read(copy, "originRace"));
        }

        /**
         * The copy is a different object, and a number written to it does not reach the original.
         */
        @Test
        @DisplayName("the copy is a separate object")
        void copyIsSeparate() {
            ItemObject copy = original.copy(false);

            copy.setNumber(1);

            assertNotSame(original, copy);
            assertEquals(7, original.getNumber());
        }
    }

    /**
     * The mutable containers, which must each be rebuilt.
     */
    @Nested
    @DisplayName("independence")
    class Independence {

        /**
         * Raising a flag on the copy leaves the original's flags alone, and the copy starts with the
         * original's.
         */
        @Test
        @DisplayName("the flags are rebuilt")
        void flagsAreIndependent() {
            ItemObject copy = original.copy(false);

            assertTrue(copy.getFlags().has(ObjectFlag.OF_SUST_STR));
            copy.getFlags().on(ObjectFlag.OF_SUST_INT);

            assertFalse(original.getFlags().has(ObjectFlag.OF_SUST_INT));
        }

        /**
         * The notice flags are rebuilt, so noticing something on the copy does not mark the original.
         */
        @Test
        @DisplayName("the notice flags are rebuilt")
        void noticeIsIndependent() {
            ItemObject copy = original.copy(false);

            // getNotice() answers a defensive copy, so the field itself is read and written here.
            @SuppressWarnings("unchecked")
            Flag<ObjectNotice> copyNotice = (Flag<ObjectNotice>) read(copy, "notice");
            @SuppressWarnings("unchecked")
            Flag<ObjectNotice> originalNotice = (Flag<ObjectNotice>) read(original, "notice");

            assertTrue(copyNotice.has(ObjectNotice.OBJ_NOTICE_ASSESSED));
            assertNotSame(originalNotice, copyNotice);
            copyNotice.on(ObjectNotice.OBJ_NOTICE_WORN);

            assertFalse(originalNotice.has(ObjectNotice.OBJ_NOTICE_WORN));
        }

        /**
         * The modifier map is rebuilt with the same values, so a modifier written to the copy does
         * not reach the original.
         */
        @Test
        @DisplayName("the modifiers are rebuilt")
        void modifiersAreIndependent() {
            @SuppressWarnings("unchecked")
            Map<ObjectModifier, Integer> originalMods =
                    (Map<ObjectModifier, Integer>) read(original, "modifiers");
            originalMods.put(ObjectModifier.OM_STR, 2);

            ItemObject copy = original.copy(false);
            @SuppressWarnings("unchecked")
            Map<ObjectModifier, Integer> copyMods = (Map<ObjectModifier, Integer>) read(copy, "modifiers");

            assertEquals(2, copyMods.get(ObjectModifier.OM_STR));
            copyMods.put(ObjectModifier.OM_STR, 9);

            assertEquals(2, originalMods.get(ObjectModifier.OM_STR));
        }

        /**
         * Each element's info is copied in turn, not just the map around them: the resistance level
         * written on the copy's entry does not reach the original's.
         */
        @Test
        @DisplayName("each element entry is copied")
        void elementInfoIsIndependent() {
            ItemObject copy = original.copy(false);
            ElementInfo copyFire = copy.getElInfo().get(ElementEnum.ELEM_FIRE);
            ElementInfo originalFire = original.getElInfo().get(ElementEnum.ELEM_FIRE);

            assertNotSame(originalFire, copyFire);
            copyFire.setResLevel(3);

            assertEquals(0, originalFire.getResLevel());
        }

        /**
         * Each curse's data is rebuilt, so the copy's power and timeout can change without touching
         * the original's, while the curse definition itself is shared.
         */
        @Test
        @DisplayName("each curse's data is rebuilt")
        void curseDataIsIndependent() {
            ItemObject copy = original.copy(false);

            CurseData copyData = copy.getCurses().get(curse);
            CurseData originalData = original.getCurses().get(curse);

            assertEquals(40, copyData.getPower());
            assertEquals(6, copyData.getTimeout());
            assertNotSame(originalData, copyData);
        }

        /**
         * The recharge dice are copied rather than shared, so the copy shares no dice with the
         * original.
         */
        @Test
        @DisplayName("the recharge dice are rebuilt")
        void timeIsIndependent() {
            ItemObject copy = original.copy(false);

            assertNotSame(read(original, "time"), read(copy, "time"));
        }

        /**
         * The grid is copied by value, so moving the copy does not move the original.
         */
        @Test
        @DisplayName("the grid is copied by value")
        void locationIsIndependent() {
            ItemObject copy = original.copy(false);

            assertEquals(Loc.row(3).col(4), read(copy, "location"));
            assertNotSame(read(original, "location"), read(copy, "location"));
        }

        /**
         * The brand and slay sets are rebuilt, so adding to the copy's does not add to the original's.
         */
        @Test
        @DisplayName("the brand and slay sets are rebuilt")
        void brandAndSlaySetsAreIndependent() {
            ItemObject copy = original.copy(false);

            assertNotSame(read(original, "brands"), read(copy, "brands"));
            assertNotSame(read(original, "slays"), read(copy, "slays"));
        }
    }

    /**
     * Where the original has nothing, the copy has nothing: elsewhere in the class "no collection"
     * and "an empty one" mean different things, so the copy must not turn one into the other.
     */
    @Nested
    @DisplayName("absent collections stay absent")
    class Absent {

        /**
         * Brands, slays and curses that were never created are still null on the copy.
         */
        @Test
        @DisplayName("null brands, slays and curses stay null")
        void collectionsStayNull() {
            set(original, "brands", null);
            set(original, "slays", null);
            set(original, "curses", null);

            ItemObject copy = original.copy(false);

            assertNull(read(copy, "brands"));
            assertNull(read(copy, "slays"));
            assertNull(read(copy, "curses"));
        }

        /**
         * A recharge interval that has somehow been left null arrives as a zero one, so the copy
         * never holds a null that the getter would fail on; no damage dice and no grid arrive as
         * none, rather than failing on the copy of nothing.
         */
        @Test
        @DisplayName("null dice become a zero recharge interval, and grid stays null")
        void diceAndGridStayNull() {
            set(original, "time", null);
            set(original, "baseDamage", null);
            set(original, "location", null);

            ItemObject copy = original.copy(false);

            Random time = (Random) read(copy, "time");
            assertEquals(0, time.getBase());
            assertEquals(0, time.getDice());
            assertEquals(0, time.getSides());
            assertEquals(0, time.getMBonus());
            assertNull(read(copy, "baseDamage"));
            assertNull(read(copy, "location"));
        }
    }

    /**
     * The known half, which is copied only when asked for.
     */
    @Nested
    @DisplayName("the known half")
    class KnownHalf {

        /**
         * A known half of the original's own, itself carrying a known half of its own, so the test
         * can tell how deep the copy goes.
         */
        private ItemObject counterpart;

        /**
         * Gives the original a known half that has a known half of its own.
         */
        @BeforeEach
        void attachKnown() {
            counterpart = ItemFixture.item(TValue.TV_SWORD).kind(kind).number(7).build();
            set(counterpart, "known", ItemFixture.item(TValue.TV_SWORD).kind(kind).build());
            set(original, "known", counterpart);
        }

        /**
         * Asked to include it, the copy gets its own known object, not a second reference to the
         * original's.
         */
        @Test
        @DisplayName("copy(true) gives the copy its own known object")
        void includingKnownCopiesIt() {
            ItemObject copy = original.copy(true);

            assertNotSame(counterpart, copy.getKnown());
            assertEquals(7, copy.getKnown().getNumber());
        }

        /**
         * The copied known half is a one-level copy: it does not carry a known half of its own, which
         * is also what ends the recursion.
         */
        @Test
        @DisplayName("the copied known half has no known half of its own")
        void knownCopyIsOneLevel() {
            ItemObject copy = original.copy(true);

            assertNull(copy.getKnown().getKnown());
        }

        /**
         * Not asked, the copy has no known half. C's {@code memcpy} would have aliased the
         * original's; the port leaves it null instead.
         */
        @Test
        @DisplayName("copy(false) leaves the known half null")
        void excludingKnownLeavesItNull() {
            ItemObject copy = original.copy(false);

            assertNull(copy.getKnown());
        }

        /**
         * An original with no known half gives a copy with none, whichever way it is asked.
         */
        @Test
        @DisplayName("an unknown original copies to an unknown copy")
        void unknownOriginalStaysUnknown() {
            set(original, "known", null);

            assertNull(original.copy(true).getKnown());
            assertNull(original.copy(false).getKnown());
        }
    }
}
