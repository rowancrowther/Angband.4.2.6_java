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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ElementInfo}, the port of C's {@code struct element_info} ({@code src/object.h}).
 *
 * <p>The class is two fields and five methods, so most of what is worth checking is not arithmetic
 * but aliasing: which accessors hand out the live {@link Flag} and which hand out a copy. That
 * distinction is the whole reason {@link ElementInfo#copy()} exists alongside
 * {@link ElementInfo#getFlags()}, and getting it backwards would be invisible until two object
 * kinds silently began sharing a flag set.
 *
 * @author Rowan Crowther
 */
class ElementInfoTest {

    /**
     * A fresh instance is C's zeroed struct: no flags, no resistance.
     */
    @Test
    @DisplayName("a new element info is empty and neutral")
    void startsEmpty() {
        ElementInfo info = new ElementInfo();

        assertEquals(0, info.getResLevel());
        assertTrue(info.getFlags().isEmpty());
        assertFalse(info.has(ElementInfoEnum.EL_INFO_HATES));
    }

    /**
     * Tests of the resistance level, which the port stores and returns without interpretation —
     * the scale is C's, including its negatives.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("resistance level")
    class ResistanceLevel {

        @Test
        @DisplayName("round-trips a positive level")
        void roundTripsPositive() {
            ElementInfo info = new ElementInfo();

            info.setResLevel(3);

            assertEquals(3, info.getResLevel());
        }

        /**
         * A negative level is a vulnerability rather than a resistance, and is stored as given.
         * C's field is an {@code int16_t}, so nothing clamps it at zero and neither does this.
         */
        /**
         * The three values {@code obj-info.c} tests for: -1 vulnerable, 1 resist, 3 immune. Each
         * must come back exactly as stored.
         */
        @Test
        @DisplayName("keeps C's vulnerable, resistant and immune levels distinct")
        void keepsCsThreeLevels() {
            ElementInfo info = new ElementInfo();

            for (int level : new int[]{-1, 1, 3}) {
                info.setResLevel(level);
                assertEquals(level, info.getResLevel());
            }
        }

        /**
         * C's field is an {@code int16_t}, so storing 40000 there wraps to -25536 and reading it
         * back gives that. The getter returns a {@code short}, so the port reads back what C would
         * hold. The data files never go near the range; this pins the 16-bit wrap at the edges and
         * for a value a whole multiple of 65536 away from a small one.
         */
        @Test
        @DisplayName("reads back as a signed sixteen-bit value, as C's int16_t holds it")
        void narrowsToSixteenBits() {
            ElementInfo info = new ElementInfo();

            info.setResLevel(40000);
            assertEquals(-25536, info.getResLevel());

            info.setResLevel(32767);
            assertEquals(32767, info.getResLevel());

            info.setResLevel(32768);
            assertEquals(-32768, info.getResLevel());

            info.setResLevel(-32768);
            assertEquals(-32768, info.getResLevel());

            info.setResLevel(-32769);
            assertEquals(32767, info.getResLevel());

            info.setResLevel(65536 + 3);
            assertEquals(3, info.getResLevel());
        }

        /**
         * The copy carries the stored level, so a value that reads back wrapped on the source reads
         * back the same wrapped value on the copy.
         */
        @Test
        @DisplayName("a copy reads back the same sixteen-bit value as its source")
        void copyKeepsTheNarrowedValue() {
            ElementInfo info = new ElementInfo();
            info.setResLevel(40000);

            assertEquals(info.getResLevel(), info.copy().getResLevel());
        }

        @Test
        @DisplayName("stores a negative level unchanged")
        void keepsNegative() {
            ElementInfo info = new ElementInfo();

            info.setResLevel(-1);

            assertEquals(-1, info.getResLevel());
        }
    }

    /**
     * Tests of the hates/ignores/random flags.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("flags")
    class Flags {

        /**
         * {@code on} delegates to {@link Flag#on}, so it answers C's "did this change anything"
         * question: true the first time, false on a repeat.
         */
        @Test
        @DisplayName("on reports only the first set as a change")
        void onReportsNoveltyOnly() {
            ElementInfo info = new ElementInfo();

            assertTrue(info.on(ElementInfoEnum.EL_INFO_IGNORE));
            assertFalse(info.on(ElementInfoEnum.EL_INFO_IGNORE));
        }

        @Test
        @DisplayName("has sees what on set, and nothing else")
        void hasSeesWhatOnSet() {
            ElementInfo info = new ElementInfo();

            info.on(ElementInfoEnum.EL_INFO_HATES);

            assertTrue(info.has(ElementInfoEnum.EL_INFO_HATES));
            assertFalse(info.has(ElementInfoEnum.EL_INFO_IGNORE));
            assertFalse(info.has(ElementInfoEnum.EL_INFO_RANDOM));
        }

        /**
         * The three flags are independent — C packs them into one bitflag, and a port that
         * accidentally made them exclusive would still pass a single-flag test.
         */
        @Test
        @DisplayName("all three flags can be on at once")
        void flagsAreIndependent() {
            ElementInfo info = new ElementInfo();

            info.on(ElementInfoEnum.EL_INFO_HATES);
            info.on(ElementInfoEnum.EL_INFO_IGNORE);
            info.on(ElementInfoEnum.EL_INFO_RANDOM);

            assertTrue(info.has(ElementInfoEnum.EL_INFO_HATES));
            assertTrue(info.has(ElementInfoEnum.EL_INFO_IGNORE));
            assertTrue(info.has(ElementInfoEnum.EL_INFO_RANDOM));
        }

        /**
         * C's {@code EL_INFO_HATES}, {@code EL_INFO_IGNORE} and {@code EL_INFO_RANDOM} are 0x01,
         * 0x02 and 0x04 in {@code object.h}, in that order; the savefile writes the byte, so the
         * port's enum must keep the same order for the bit positions to line up.
         */
        @Test
        @DisplayName("the enum keeps C's hates, ignore, random order")
        void enumOrderMatchesCBits() {
            assertEquals(0, ElementInfoEnum.EL_INFO_HATES.ordinal());
            assertEquals(1, ElementInfoEnum.EL_INFO_IGNORE.ordinal());
            assertEquals(2, ElementInfoEnum.EL_INFO_RANDOM.ordinal());
            assertEquals(3, ElementInfoEnum.values().length);
        }

        /**
         * {@code getFlags} deliberately hands out the live set, matching C, where
         * {@code el_info[i].flags} is a bitflag inside the owning struct that callers manipulate in
         * place. This pins that down, because the opposite choice would look equally reasonable
         * from the signature alone.
         */
        @Test
        @DisplayName("getFlags returns the live set, so writes through it are seen")
        void getFlagsIsLive() {
            ElementInfo info = new ElementInfo();

            Flag<ElementInfoEnum> flags = info.getFlags();
            flags.on(ElementInfoEnum.EL_INFO_RANDOM);

            assertTrue(info.has(ElementInfoEnum.EL_INFO_RANDOM));
            assertSame(flags, info.getFlags());
        }
    }

    /**
     * Tests of {@link ElementInfo#copy()}, whose only job is to be the accessor
     * {@link ElementInfo#getFlags()} is not.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        @Test
        @DisplayName("carries both fields across")
        void copiesBothFields() {
            ElementInfo original = new ElementInfo();
            original.setResLevel(2);
            original.on(ElementInfoEnum.EL_INFO_IGNORE);

            ElementInfo copy = original.copy();

            assertEquals(2, copy.getResLevel());
            assertTrue(copy.has(ElementInfoEnum.EL_INFO_IGNORE));
        }

        /**
         * C's struct assignment copies the whole {@code bitflag}, so all three flags and a
         * negative level must survive, not just one of each.
         */
        @Test
        @DisplayName("carries all three flags and a vulnerability across")
        void copiesEveryFlag() {
            ElementInfo original = new ElementInfo();
            original.setResLevel(-1);
            original.on(ElementInfoEnum.EL_INFO_HATES);
            original.on(ElementInfoEnum.EL_INFO_IGNORE);
            original.on(ElementInfoEnum.EL_INFO_RANDOM);

            ElementInfo copy = original.copy();

            assertEquals(-1, copy.getResLevel());
            assertTrue(copy.has(ElementInfoEnum.EL_INFO_HATES));
            assertTrue(copy.has(ElementInfoEnum.EL_INFO_IGNORE));
            assertTrue(copy.has(ElementInfoEnum.EL_INFO_RANDOM));
        }

        /**
         * The copy is deep in the sense that matters: the flag set is a new object, so folding a
         * base's defaults onto a derived kind leaves the derived kind free to diverge.
         */
        @Test
        @DisplayName("gives the copy its own flag set")
        void flagSetIsNotShared() {
            ElementInfo original = new ElementInfo();
            original.on(ElementInfoEnum.EL_INFO_HATES);

            ElementInfo copy = original.copy();

            assertNotSame(original.getFlags(), copy.getFlags());
        }

        /**
         * Both directions, because a copy that shares state usually fails only one of them: a
         * {@code copyFrom} into a shared set breaks the first, and returning the receiver breaks
         * the second.
         */
        @Test
        @DisplayName("neither copy nor original can change the other")
        void neitherSideLeaks() {
            ElementInfo original = new ElementInfo();
            original.on(ElementInfoEnum.EL_INFO_HATES);
            original.setResLevel(1);

            ElementInfo copy = original.copy();

            copy.on(ElementInfoEnum.EL_INFO_RANDOM);
            copy.setResLevel(5);
            assertFalse(original.has(ElementInfoEnum.EL_INFO_RANDOM));
            assertEquals(1, original.getResLevel());

            original.on(ElementInfoEnum.EL_INFO_IGNORE);
            assertFalse(copy.has(ElementInfoEnum.EL_INFO_IGNORE));
        }

        /**
         * A copy taken from an empty instance must not pick up the source's flags <em>later</em>,
         * which is the failure mode of copying a reference and only filling it in afterwards.
         */
        @Test
        @DisplayName("copying an empty info still detaches it")
        void emptyCopyIsStillDetached() {
            ElementInfo original = new ElementInfo();

            ElementInfo copy = original.copy();
            original.on(ElementInfoEnum.EL_INFO_HATES);

            assertFalse(copy.has(ElementInfoEnum.EL_INFO_HATES));
        }
    }
}
