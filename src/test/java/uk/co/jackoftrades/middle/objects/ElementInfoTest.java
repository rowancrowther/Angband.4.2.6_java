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
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ElementInfo}, the port of C's {@code struct element_info} ({@code src/object.h}).
 *
 * <p>The class is two fields and five methods, so most of what is worth checking is not arithmetic
 * but aliasing: which accessors hand out the live {@link Flag} and which hand out a copy. That
 * distinction is the whole reason {@link ElementInfo#copy()} exists alongside
 * {@link ElementInfo#getFlags()}, and getting it backwards would be invisible until two object
 * kinds silently began sharing a flag set.
 *
 * @author ClaudeCode
 */
class ElementInfoTest {

    /**
     * A fresh instance is C's zeroed struct: no flags, no resistance.
     *
     * @author ClaudeCode
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
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("resistance level")
    class ResistanceLevel {

        /**
         * @author ClaudeCode
         */
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
         *
         * @author ClaudeCode
         */
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
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("flags")
    class Flags {

        /**
         * {@code on} delegates to {@link Flag#on}, so it answers C's "did this change anything"
         * question: true the first time, false on a repeat.
         *
         * @author ClaudeCode
         */
        @Test
        @DisplayName("on reports only the first set as a change")
        void onReportsNoveltyOnly() {
            ElementInfo info = new ElementInfo();

            assertTrue(info.on(ElementInfoEnum.EL_INFO_IGNORE));
            assertFalse(info.on(ElementInfoEnum.EL_INFO_IGNORE));
        }

        /**
         * @author ClaudeCode
         */
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
         *
         * @author ClaudeCode
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
         * {@code getFlags} deliberately hands out the live set, matching C, where
         * {@code el_info[i].flags} is a bitflag inside the owning struct that callers manipulate in
         * place. This pins that down, because the opposite choice would look equally reasonable
         * from the signature alone.
         *
         * @author ClaudeCode
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
     * @author ClaudeCode
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * @author ClaudeCode
         */
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
         * The copy is deep in the sense that matters: the flag set is a new object, so folding a
         * base's defaults onto a derived kind leaves the derived kind free to diverge.
         *
         * @author ClaudeCode
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
         *
         * @author ClaudeCode
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
         *
         * @author ClaudeCode
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
