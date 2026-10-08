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
import uk.co.jackoftradesltd.channel.utils.Flag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerHistoryType} — the port of the history type enum in C's
 * {@code player-history.h}, generated from {@code list-history-types.h}.
 *
 * <p><b>Every expectation here is read from {@code list-history-types.h}, not from the port.</b>
 * The header lists eleven {@code HIST()} lines in a fixed order, so the constants are numbered
 * 0 to 10 and {@code HIST_MAX} is 11. The description strings are copied from the header
 * character for character, including its "IDd" and "IDed" spellings, which are C's and not
 * to be tidied.
 *
 * <p>The numbering is the boundary worth pinning: {@code HIST_NONE} is zero, a real bit
 * position rather than "no flags", and {@code HIST_MAX} is the first value past the last type.
 * C's {@code type} field is a bit array, so one entry can hold several types; the last test
 * checks that the Java {@link Flag} keeps the bits independent.
 *
 * @author Rowan Crowther
 */
@DisplayName("PlayerHistoryType")
class PlayerHistoryTypeTest {

    @Nested
    @DisplayName("constants")
    class Constants {

        /**
         * The header's order, with HIST_MAX last as in the C enum, gives these twelve in turn.
         */
        @Test
        @DisplayName("are C's eleven types then HIST_MAX, in C's order")
        void orderMatchesC() {
            PlayerHistoryType[] expected = {
                    PlayerHistoryType.HIST_NONE,
                    PlayerHistoryType.HIST_PLAYER_BIRTH,
                    PlayerHistoryType.HIST_ARTIFACT_UNKNOWN,
                    PlayerHistoryType.HIST_ARTIFACT_KNOWN,
                    PlayerHistoryType.HIST_ARTIFACT_LOST,
                    PlayerHistoryType.HIST_PLAYER_DEATH,
                    PlayerHistoryType.HIST_SLAY_UNIQUE,
                    PlayerHistoryType.HIST_USER_INPUT,
                    PlayerHistoryType.HIST_SAVEFILE_IMPORT,
                    PlayerHistoryType.HIST_GAIN_LEVEL,
                    PlayerHistoryType.HIST_GENERIC,
                    PlayerHistoryType.HIST_MAX
            };
            assertEquals(12, PlayerHistoryType.values().length);
            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], PlayerHistoryType.values()[i], "position " + i);
            }
        }

        /**
         * C numbers the enum from zero, so the first constant is zero and the end marker is the
         * count of real types.
         */
        @Test
        @DisplayName("are numbered from zero with HIST_MAX at eleven")
        void numbering() {
            assertEquals(0, PlayerHistoryType.HIST_NONE.ordinal());
            assertEquals(1, PlayerHistoryType.HIST_PLAYER_BIRTH.ordinal());
            assertEquals(4, PlayerHistoryType.HIST_ARTIFACT_LOST.ordinal());
            assertEquals(10, PlayerHistoryType.HIST_GENERIC.ordinal());
            assertEquals(11, PlayerHistoryType.HIST_MAX.ordinal());
        }
    }

    @Nested
    @DisplayName("descriptions")
    class Descriptions {

        /**
         * Strings copied from the second argument of each {@code HIST()} line.
         */
        @Test
        @DisplayName("match list-history-types.h for every type")
        void matchHeader() {
            assertEquals("", PlayerHistoryType.HIST_NONE.getDescription());
            assertEquals("Player was born", PlayerHistoryType.HIST_PLAYER_BIRTH.getDescription());
            assertEquals("Player found but not IDd an artifact",
                    PlayerHistoryType.HIST_ARTIFACT_UNKNOWN.getDescription());
            assertEquals("Player has IDed an artifact",
                    PlayerHistoryType.HIST_ARTIFACT_KNOWN.getDescription());
            assertEquals("Player had an artifact and lost it",
                    PlayerHistoryType.HIST_ARTIFACT_LOST.getDescription());
            assertEquals("Player has been slain", PlayerHistoryType.HIST_PLAYER_DEATH.getDescription());
            assertEquals("Player has slain a unique monster",
                    PlayerHistoryType.HIST_SLAY_UNIQUE.getDescription());
            assertEquals("User-added note", PlayerHistoryType.HIST_USER_INPUT.getDescription());
            assertEquals("Added when an older version savefile is imported",
                    PlayerHistoryType.HIST_SAVEFILE_IMPORT.getDescription());
            assertEquals("Player gained a level", PlayerHistoryType.HIST_GAIN_LEVEL.getDescription());
            assertEquals("Anything else not covered here (unused)",
                    PlayerHistoryType.HIST_GENERIC.getDescription());
        }

        /**
         * C has no {@code HIST()} line for the end marker, so it has no text to carry.
         */
        @Test
        @DisplayName("are empty for the end marker, which has no HIST() line in C")
        void endMarkerHasNoText() {
            assertEquals("", PlayerHistoryType.HIST_MAX.getDescription());
        }
    }

    @Nested
    @DisplayName("as flags")
    class AsFlags {

        /**
         * The artifact life cycle in {@code player-history.c}: {@code history_lose_artifact}
         * creates an entry with UNKNOWN and LOST both set, and {@code history_unmask_unknown}
         * then turns UNKNOWN off and KNOWN on while LOST stays. Each bit must move alone.
         */
        @Test
        @DisplayName("keep each bit independent, as in the unmask-unknown life cycle")
        void bitsAreIndependent() {
            Flag<PlayerHistoryType> type = new Flag<>(PlayerHistoryType.class);
            type.on(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN);
            type.on(PlayerHistoryType.HIST_ARTIFACT_LOST);
            assertEquals(2, type.count());

            type.off(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN);
            type.on(PlayerHistoryType.HIST_ARTIFACT_KNOWN);

            assertFalse(type.has(PlayerHistoryType.HIST_ARTIFACT_UNKNOWN));
            assertTrue(type.has(PlayerHistoryType.HIST_ARTIFACT_KNOWN));
            assertTrue(type.has(PlayerHistoryType.HIST_ARTIFACT_LOST));
            assertEquals(2, type.count());
        }
    }
}
