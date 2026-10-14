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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link EgoItem#clearIgnoreType} and {@link EgoItem#copy}.
 *
 * <p><b>Clearing.</b> C's {@code obj-ignore.c} keeps the player's per-ego choices in
 * {@code ego_ignore_types[eidx][itype]}, and writes {@code false} to one entry in
 * {@code ego_ignore_clear} and to every entry in {@code ignore_birth_init}. The Java ego holds its own
 * row of that table, so the expected values below are what a {@code false} write to one entry of the
 * row must leave: that entry false, every other entry as it was, and a write to an entry already
 * false changing nothing.
 *
 * <p><b>Copying.</b> {@code copy()} has no C counterpart (C shares {@code struct ego_item} pointers),
 * so the expected values are the contract its Javadoc states: equal in value, independent in every
 * mutable part, and sharing only the immutable registry entries and the {@link ObjectKind} templates.
 *
 * <p>Class EgoItemClearAndCopyTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class EgoItemClearAndCopyTest {

    private static EgoItem bareEgo() {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectKindFlag.class), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(),
                0, 0, 0, 0, new ArrayList<>(), null, null, null, 0, 0, 0,
                null, null, false);
    }

    private static Curse curse() {
        return new Curse("test curse", List.of(), null, List.of(), new Flag<>(ObjectFlag.class), "", 0);
    }

    /**
     * An ego with something in every member the copy has to treat differently.
     */
    private static EgoItem loadedEgo(ObjectKind kind, Curse curse) {
        Flag<ObjectFlag> on = new Flag<>(ObjectFlag.class);
        on.on(ObjectFlag.OF_FEATHER);
        Flag<ObjectFlag> off = new Flag<>(ObjectFlag.class);
        off.on(ObjectFlag.OF_AGGRAVATE);
        Map<ObjectModifier, Random> modifiers = new HashMap<>();
        modifiers.put(ObjectModifier.OM_STEALTH, new Random(2, 0, 0, 0, false));
        Map<ObjectModifier, Integer> minModifiers = new HashMap<>();
        minModifiers.put(ObjectModifier.OM_STEALTH, 1);
        Map<ElementEnum, ElementInfo> elInfo = new HashMap<>();
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        elInfo.put(ElementEnum.ELEM_FIRE, fire);
        Map<Curse, CurseData> curses = new HashMap<>();
        curses.put(curse, new CurseData(20, 0));
        List<ObjectKind> kinds = new ArrayList<>();
        kinds.add(kind);
        return new EgoItem("of Loading", "a loaded ego", 7, 500,
                on, off, new Flag<>(ObjectKindFlag.class),
                modifiers, minModifiers, elInfo, new HashSet<>(), new HashSet<>(), curses,
                30, 40, 5, 60, kinds, new Random(3, 0, 0, 0, false), null, new Random(4, 0, 0, 0, false),
                2, 255, 0, null, null, true);
    }

    @Nested
    @DisplayName("clearIgnoreType")
    class ClearIgnoreType {

        /**
         * C's {@code ego_ignore_clear}: the one entry goes back to {@code false}.
         */
        @Test
        @DisplayName("clears a marked category")
        void clearsMarkedCategory() {
            EgoItem ego = bareEgo();
            ego.setIgnoreType(IgnoreType.ITYPE_SHARP);

            ego.clearIgnoreType(IgnoreType.ITYPE_SHARP);

            assertFalse(ego.getIgnoreType(IgnoreType.ITYPE_SHARP));
        }

        /**
         * A write to one entry of C's row leaves the others as they were.
         */
        @Test
        @DisplayName("leaves every other marked category alone")
        void leavesOtherCategories() {
            EgoItem ego = bareEgo();
            ego.setIgnoreType(IgnoreType.ITYPE_SHARP);
            ego.setIgnoreType(IgnoreType.ITYPE_RING);

            ego.clearIgnoreType(IgnoreType.ITYPE_SHARP);

            assertFalse(ego.getIgnoreType(IgnoreType.ITYPE_SHARP));
            assertTrue(ego.getIgnoreType(IgnoreType.ITYPE_RING));
            assertFalse(ego.getIgnoreType(IgnoreType.ITYPE_BOW));
        }

        /**
         * Writing {@code false} over a {@code false} entry is not an error and changes nothing.
         */
        @Test
        @DisplayName("clearing an unmarked category is harmless")
        void clearingUnmarkedIsHarmless() {
            EgoItem ego = bareEgo();

            ego.clearIgnoreType(IgnoreType.ITYPE_AMULET);

            for (IgnoreType type : IgnoreType.values()) {
                assertFalse(ego.getIgnoreType(type), type + " should still be unmarked");
            }
        }

        /**
         * Set, clear, set again: the last write wins, as it does for C's boolean entry.
         */
        @Test
        @DisplayName("a category can be marked again after being cleared")
        void markClearMark() {
            EgoItem ego = bareEgo();

            ego.setIgnoreType(IgnoreType.ITYPE_BOLT);
            ego.clearIgnoreType(IgnoreType.ITYPE_BOLT);
            ego.setIgnoreType(IgnoreType.ITYPE_BOLT);

            assertTrue(ego.getIgnoreType(IgnoreType.ITYPE_BOLT));
        }

        /**
         * The two ends of the enum, which C's birth sweep treats differently: {@code ITYPE_NONE} is
         * index 0 of the row and is cleared by it, {@code ITYPE_MAX} is one past the row's last index.
         * The setter and the clearer treat both like any other key; it is the caller's loop
         * ({@code ObjectIgnore.ignoreBirthInit}) that stops short of {@code ITYPE_MAX}.
         */
        @Test
        @DisplayName("the NONE and MAX keys clear like any other")
        void endsOfTheEnumClear() {
            EgoItem ego = bareEgo();
            ego.setIgnoreType(IgnoreType.ITYPE_NONE);
            ego.setIgnoreType(IgnoreType.ITYPE_MAX);

            ego.clearIgnoreType(IgnoreType.ITYPE_NONE);
            ego.clearIgnoreType(IgnoreType.ITYPE_MAX);

            assertFalse(ego.getIgnoreType(IgnoreType.ITYPE_NONE));
            assertFalse(ego.getIgnoreType(IgnoreType.ITYPE_MAX));
        }
    }

    @Nested
    @DisplayName("copy")
    class Copy {

        @Test
        @DisplayName("scalar fields come across by value")
        void scalarsCopied() {
            Curse curse = curse();
            EgoItem original = loadedEgo(new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test",
                    null, false), curse);

            EgoItem copy = original.copy();

            assertNotSame(original, copy);
            assertTrue(copy.isEverSeen());
            assertTrue(copy.getFlags().has(ObjectFlag.OF_FEATHER));
            assertTrue(copy.getOffFlags().has(ObjectFlag.OF_AGGRAVATE));
        }

        /**
         * The three flag sets are rebuilt, so adding a flag to the original's set later is not seen by
         * the copy.
         */
        @Test
        @DisplayName("flag sets are independent")
        void flagSetsIndependent() {
            EgoItem original = loadedEgo(new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test",
                    null, false), curse());
            EgoItem copy = original.copy();

            original.getFlags().on(ObjectFlag.OF_PROT_FEAR);

            assertNotSame(original.getFlags(), copy.getFlags());
            assertFalse(copy.getFlags().has(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        @DisplayName("modifier ranges are copied, not shared")
        void modifiersCopied() {
            EgoItem original = loadedEgo(new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test",
                    null, false), curse());

            EgoItem copy = original.copy();

            Random mine = original.getModifier(ObjectModifier.OM_STEALTH);
            Random theirs = copy.getModifier(ObjectModifier.OM_STEALTH);
            assertNotSame(mine, theirs);
            assertEquals(mine.getBase(), theirs.getBase());
            assertNull(copy.getModifier(ObjectModifier.OM_SPEED), "a modifier the ego never had stays absent");
        }

        @Test
        @DisplayName("element info is copied entry by entry")
        void elInfoCopied() {
            EgoItem original = loadedEgo(new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test",
                    null, false), curse());

            EgoItem copy = original.copy();

            ElementInfo mine = original.getElInfo().get(ElementEnum.ELEM_FIRE);
            ElementInfo theirs = copy.getElInfo().get(ElementEnum.ELEM_FIRE);
            assertNotSame(mine, theirs);
            assertEquals(1, theirs.getResLevel());

            mine.setResLevel(3);
            assertEquals(1, theirs.getResLevel(), "changing the original's entry must not reach the copy");
        }

        /**
         * The reason {@link CurseData} has a copy constructor: the game tick counts timeouts down in
         * place, so a shared instance would be decremented once per carrier.
         */
        @Test
        @DisplayName("curse data is rebuilt, so the copy's power does not follow the original's")
        void curseDataRebuilt() {
            Curse curse = curse();
            EgoItem original = loadedEgo(new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test",
                    null, false), curse);

            EgoItem copy = original.copy();

            CurseData mine = original.getCurses().get(curse);
            CurseData theirs = copy.getCurses().get(curse);
            assertNotSame(mine, theirs);
            assertEquals(20, theirs.getPower());

            mine.setPower(99);
            assertEquals(20, theirs.getPower());
        }

        /**
         * The brand and slay sets are rebuilt so that adding to one does not add to the other.
         */
        @Test
        @DisplayName("brand and slay sets are independent")
        void brandAndSlaySetsIndependent() {
            EgoItem original = loadedEgo(new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test",
                    null, false), curse());
            EgoItem copy = original.copy();

            Set<Slay> originalSlays = original.getSlays();
            originalSlays.add(null);

            assertNotSame(originalSlays, copy.getSlays());
            assertTrue(copy.getSlays().isEmpty());
            assertNotSame(original.getBrands(), copy.getBrands());
        }

        /**
         * C shares its {@code kind} pointers, so the list is new but the kinds in it are the same
         * templates.
         */
        @Test
        @DisplayName("the possible-items list is rebuilt around the same kinds")
        void possibleItemsShareKinds() {
            ObjectKind dagger = new ObjectKind(null, 0, 0, 0, 0, "Dagger", TValue.TV_SWORD, "test", null, false);
            EgoItem original = loadedEgo(dagger, curse());

            EgoItem copy = original.copy();

            assertTrue(copy.egoHasIgnoreType(IgnoreType.ITYPE_SHARP), "the copy still knows its kinds");
            assertEquals(original.egoHasIgnoreType(IgnoreType.ITYPE_SHARP),
                    copy.egoHasIgnoreType(IgnoreType.ITYPE_SHARP));
        }

        /**
         * The combat dice, activation and recharge time are each allowed to be absent, and the copy
         * keeps them absent rather than failing on the {@code null}.
         */
        @Test
        @DisplayName("absent dice, activation and time do not stop the copy")
        void absentOptionalsDoNotStopTheCopy() {
            EgoItem original = bareEgo();

            EgoItem copy = original.copy();

            assertNotSame(original, copy);
            assertNull(copy.getModifier(ObjectModifier.OM_STEALTH));
            assertFalse(copy.isEverSeen());
        }

        /**
         * The ignore row is copied by value, then independent: the marks the original carries are
         * present in the copy, and a later clear on the copy leaves the original marked.
         */
        @Test
        @DisplayName("ignore marks are carried across, then independent")
        void ignoreMarksCarriedThenIndependent() {
            EgoItem original = bareEgo();
            original.setIgnoreType(IgnoreType.ITYPE_SHARP);
            original.setIgnoreType(IgnoreType.ITYPE_RING);

            EgoItem copy = original.copy();

            assertTrue(copy.getIgnoreType(IgnoreType.ITYPE_SHARP));
            assertTrue(copy.getIgnoreType(IgnoreType.ITYPE_RING));
            assertFalse(copy.getIgnoreType(IgnoreType.ITYPE_BOW));

            copy.clearIgnoreType(IgnoreType.ITYPE_SHARP);
            assertTrue(original.getIgnoreType(IgnoreType.ITYPE_SHARP));

            original.setIgnoreType(IgnoreType.ITYPE_BOW);
            assertFalse(copy.getIgnoreType(IgnoreType.ITYPE_BOW));
        }

        @Test
        @DisplayName("a copy of a copy is still complete")
        void copyOfCopy() {
            EgoItem original = bareEgo();
            original.setIgnoreType(IgnoreType.ITYPE_AMULET);

            EgoItem second = original.copy().copy();

            assertTrue(second.getIgnoreType(IgnoreType.ITYPE_AMULET));
            assertFalse(second.getIgnoreType(IgnoreType.ITYPE_BOW));
        }
    }
}
