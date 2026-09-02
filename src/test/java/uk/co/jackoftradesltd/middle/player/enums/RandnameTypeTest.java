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

package uk.co.jackoftradesltd.middle.player.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link RandnameType} — the port of C's {@code randname_type} ({@code src/randname.h:25}).
 *
 * <p><b>Every expectation here comes from the C header, not from the port.</b> C writes the enum
 * as {@code RANDNAME_TOLKIEN = 1}, {@code RANDNAME_SCROLL} (so 2) and {@code RANDNAME_NUM_TYPES}
 * (so 3, and marked "not a valid name type"). The numbers matter beyond the header: the
 * {@code section} directive in {@code lib/gamedata/names.txt} carries them literally, and
 * {@code randname_make} indexes {@code name_sections} with the type value itself
 * ({@code src/randname.c:77}).
 *
 * <p>The interesting boundary is three. It is a real C enumerator, so a test written from the
 * Java alone might expect a constant back; the header says it is an end marker, so the expected
 * answer is that it names no usable type. Zero is the other boundary, and the one place the port
 * is deliberately stricter than C — {@code parse_names_section} ({@code src/init.c:1430}) rejects
 * only {@code section >= RANDNAME_NUM_TYPES}, so C accepts zero and files those words in a slot
 * no generator ever reads. That difference is pinned below as a recorded decision.
 *
 * @author Rowan Crowther
 */
@DisplayName("RandnameType")
class RandnameTypeTest {

    @Nested
    @DisplayName("constants")
    class Constants {

        /**
         * C declares the three enumerators in this order, and both {@code names.txt} and
         * {@code randname_make} depend on that order rather than on the names.
         */
        @Test
        @DisplayName("are C's three, in C's order")
        void constantsMatchC() {
            assertArrayEquals(
                    new RandnameType[]{
                            RandnameType.RANDNAME_TOLKIEN,
                            RandnameType.RANDNAME_SCROLL,
                            RandnameType.RANDNAME_NUM_TYPES},
                    RandnameType.values());
        }

        /**
         * C numbers from one, Java's ordinals from zero, so the C value of every constant is its
         * ordinal plus one. Callers and test fixtures rely on that arithmetic when they build a
         * {@code Name} with a section number, so it is asserted rather than assumed.
         */
        @Test
        @DisplayName("carry C's values as ordinal + 1")
        void ordinalsAreCValuesLessOne() {
            assertEquals(1, RandnameType.RANDNAME_TOLKIEN.ordinal() + 1);
            assertEquals(2, RandnameType.RANDNAME_SCROLL.ordinal() + 1);
            assertEquals(3, RandnameType.RANDNAME_NUM_TYPES.ordinal() + 1);
        }

        /**
         * C uses {@code RANDNAME_NUM_TYPES} as the width of the {@code name_sections} array
         * ({@code src/init.c:1472}); here {@code values().length} is what plays that part, so the
         * two must agree.
         */
        @Test
        @DisplayName("number as many as C's end marker")
        void valuesLengthIsCsNumTypes() {
            assertEquals(3, RandnameType.values().length);
        }
    }

    @Nested
    @DisplayName("fromIndex")
    class FromIndex {

        /**
         * The two values C actually passes to {@code randname_make}: one from
         * {@code player_random_name} ({@code src/player.c:380}) and two from the scroll title
         * builder ({@code src/obj-util.c:200}).
         */
        @Test
        @DisplayName("maps C's one and two onto the two real sections")
        void mapsTheRealSections() {
            assertSame(RandnameType.RANDNAME_TOLKIEN, RandnameType.fromIndex(1));
            assertSame(RandnameType.RANDNAME_SCROLL, RandnameType.fromIndex(2));
        }

        /**
         * Three is {@code RANDNAME_NUM_TYPES}, which the header calls an end-of-type marker and
         * not a valid name type; {@code randname_make} asserts {@code name_type < NUM_TYPES}
         * ({@code src/randname.c:86}). So a section of three names nothing usable.
         */
        @Test
        @DisplayName("rejects C's end marker")
        void rejectsTheMarker() {
            assertNull(RandnameType.fromIndex(3));
        }

        /**
         * Above the marker, C's parser returns {@code PARSE_ERROR_OUT_OF_BOUNDS}
         * ({@code src/init.c:1434}) and the port has nothing to return either.
         */
        @Test
        @DisplayName("rejects anything past the end marker")
        void rejectsPastTheMarker() {
            assertNull(RandnameType.fromIndex(4));
            assertNull(RandnameType.fromIndex(100));
            assertNull(RandnameType.fromIndex(Integer.MAX_VALUE));
        }

        /**
         * The one deliberate divergence. C accepts {@code section:0} and quietly parks those words
         * in {@code name_sections[0]}, which no caller reads; here zero is out of range and the
         * caller treats it as a bad data file. The shipped {@code names.txt} uses only one and
         * two, so no real load differs — the case is pinned so the choice stays a recorded one.
         */
        @Test
        @DisplayName("rejects zero, which C tolerates in an unread slot")
        void rejectsZero() {
            assertNull(RandnameType.fromIndex(0));
        }

        /**
         * A negative section cannot come out of C's parser, which reads the number as an unsigned
         * int, but it can reach a Java method; nothing in range answers to it.
         */
        @Test
        @DisplayName("rejects a negative section")
        void rejectsNegative() {
            assertNull(RandnameType.fromIndex(-1));
            assertNull(RandnameType.fromIndex(Integer.MIN_VALUE));
        }

        /**
         * The round trip the callers actually perform: a constant's C value fed back in must give
         * the same constant, for every constant that has one.
         */
        @Test
        @DisplayName("round-trips every real section")
        void roundTripsRealSections() {
            for (RandnameType type : RandnameType.values()) {
                if (type == RandnameType.RANDNAME_NUM_TYPES) {
                    continue;
                }
                assertSame(type, RandnameType.fromIndex(type.ordinal() + 1));
            }
        }
    }
}
