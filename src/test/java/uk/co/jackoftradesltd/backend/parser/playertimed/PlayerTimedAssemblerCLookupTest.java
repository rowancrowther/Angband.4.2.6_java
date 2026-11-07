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

package uk.co.jackoftradesltd.backend.parser.playertimed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the places where {@link PlayerTimedAssembler} leans on a C lookup or number reader and the
 * two could plausibly part: the numeric forms of {@code msgt:}, the first and last slots of the
 * flag lists, the base-0 integer reading of {@code parser_getint}/{@code parser_getuint}, and the
 * colour-name path. {@link PlayerTimedAssemblerTest} holds the ordinary path for each directive.
 *
 * <p>Every expected value comes from the C source: {@code message_lookup_by_name} in
 * {@code message.c} and {@code list-message.h} (153 real entries, {@code GENERIC} at 0 and
 * {@code SCRAMBLE} at 152, then a {@code MAX} entry that the name table also holds);
 * {@code lookup_flag} in {@code datafile.c} (starts at {@code FLAG_START}, 1, so slot 0 is never
 * matched); {@code parser_parse} in {@code parser.c} ({@code strtol}/{@code strtoul} with base 0, so
 * {@code 010} is eight, {@code 0x10} is sixteen and trailing text after the digits is ignored; a
 * {@code uint} refuses a leading {@code -}); and {@code color_text_to_attr} and
 * {@code color_char_to_attr} in {@code z-color.c} (an unmatched name or letter is white).
 *
 * <p>Class PlayerTimedAssemblerCLookupTest coded on 261107, commented in full on 261107.
 *
 * @author Rowan Crowther
 */
class PlayerTimedAssemblerCLookupTest {

    private static final String FILE = "player_timed.txt";
    private static final int LINE = 11;

    private static final PlayerTimedParseRecord.PlayerTimedGradeParseRecord GRADE =
            new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", "10", "Status", null, "Up");

    /**
     * A {@code FAST} record with the given fields and one ordinary grade.
     *
     * @param msgT         the {@code msgt:} token
     * @param resist       the {@code resist:} token
     * @param synonym      the {@code flag-synonym:} code
     * @param synonymExact the {@code flag-synonym:} exact value
     * @param lowerBound   the {@code lower-bound:} text
     * @return the parse record
     */
    private static PlayerTimedParseRecord fields(String msgT, String resist, String synonym, String synonymExact,
                                                 String lowerBound) {
        return new PlayerTimedParseRecord("FAST", "", List.of(GRADE), "", "", "", msgT, List.of(), null, null,
                resist, "", "", synonym, synonymExact, lowerBound, List.of(), LINE);
    }

    private static PlayerTimedParseRecord withFails(PlayerTimedParseRecord.FailureParseRecord... fails) {
        return new PlayerTimedParseRecord("FAST", "", List.of(GRADE), "", "", "", "", List.of(fails), null, null,
                "", "", "", "", "", "", List.of(), LINE);
    }

    private static PlayerTimedParseRecord withGrade(String colour, String max) {
        return new PlayerTimedParseRecord("FAST", "", List.of(
                new PlayerTimedParseRecord.PlayerTimedGradeParseRecord(colour, max, "Status", null, "Up")),
                "", "", "", "", List.of(), null, null, "", "", "", "", "", "", List.of(), LINE);
    }

    private static PlayerTimedParseRecord.FailureParseRecord fail(String type, String value) {
        return new PlayerTimedParseRecord.FailureParseRecord(type, value);
    }

    private static PlayerTimedEffect assembleOne(PlayerTimedParseRecord record) {
        List<PlayerTimedEffect> effects = new PlayerTimedAssembler().assemble(List.of(record), FILE);
        assertEquals(1, effects.size());
        return effects.get(0);
    }

    private static void assertStopsTheLoad(PlayerTimedParseRecord record, String tail) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new PlayerTimedAssembler().assemble(List.of(record), FILE));

        assertTrue(thrown.getMessage().contains(tail), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: " + LINE + " "), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    private static MessageType msgt(String token) {
        return assembleOne(fields(token, "", "", "", "")).getMsgT();
    }

    @Test
    @DisplayName("no records give no effects")
    void noRecordsGiveNoEffects() {
        assertTrue(new PlayerTimedAssembler().assemble(List.of(), FILE).isEmpty());
    }

    @Nested
    @DisplayName("msgt: as message_lookup_by_name reads it")
    class MessageTypeToken {

        @Test
        @DisplayName("a number below MSG_MAX selects the message at that position")
        void aNumberSelectsByPosition() {
            assertAll(
                    () -> assertEquals(MessageType.MSG_GENERIC, msgt("0")),
                    () -> assertEquals(MessageType.MSG_BIRTH, msgt("1")),
                    () -> assertEquals(MessageType.MSG_HIT, msgt("2")),
                    () -> assertEquals(MessageType.MSG_SCRAMBLE, msgt("152"), "the last real entry"));
        }

        @Test
        @DisplayName("a number at or above MSG_MAX, or a negative one, stops the load")
        void aNumberOutOfRangeStopsTheLoad() {
            assertStopsTheLoad(fields("153", "", "", "", ""), "has an illegal message type value: 153");
            assertStopsTheLoad(fields("154", "", "", "", ""), "has an illegal message type value: 154");
            assertStopsTheLoad(fields("-1", "", "", "", ""), "has an illegal message type value: -1");
        }

        @Test
        @DisplayName("a sign, trailing spaces and -0 are read as strtoul reads them")
        void signAndSpacesFollowStrtoul() {
            assertAll(
                    () -> assertEquals(MessageType.MSG_HIT, msgt("+2")),
                    () -> assertEquals(MessageType.MSG_HIT, msgt("2 ")),
                    () -> assertEquals(MessageType.MSG_GENERIC, msgt("-0")));
        }

        @Test
        @DisplayName("text after the digits stops the load, since only spaces may follow")
        void textAfterTheDigitsStopsTheLoad() {
            assertStopsTheLoad(fields("2x", "", "", "", ""), "has an illegal message type value: 2x");
        }

        @Test
        @DisplayName("NONE is not a message name, and MAX is, because the name table holds MAX")
        void noneIsRefusedAndMaxIsHeld() {
            assertStopsTheLoad(fields("NONE", "", "", "", ""), "has an illegal message type value: NONE");
            assertEquals(MessageType.MSG_MAX, msgt("MAX"));
        }
    }

    @Nested
    @DisplayName("fail: lists, as lookup_flag and the element and timed-effect scans read them")
    class FailLists {

        @Test
        @DisplayName("the first real player flag is found, though lookup_flag skips slot 0")
        void theFirstRealPlayerFlagIsFound() {
            PlayerTimedEffect fast = assembleOne(withFails(fail("4", "FAST_SHOT")));

            assertAll(
                    () -> assertEquals(TimedEffectReasonType.TYPE_PLAYER_FLAG, fast.getFail().get(0).getCode()),
                    () -> assertEquals(PlayerFlag.PF_FAST_SHOT, fast.getFail().get(0).getPlayerFlagCode()));
        }

        @Test
        @DisplayName("the player-flag list and the vulnerability list have no MAX")
        void maxIsRefusedWhereTheListEndsWithoutIt() {
            assertStopsTheLoad(withFails(fail("4", "MAX")), "has an illegal fail value: MAX");
            assertStopsTheLoad(withFails(fail("3", "MAX")),
                    "has an illegal vulnerable element sentinel value: MAX");
        }

        @Test
        @DisplayName("the fail code is read in base 0, and a leading minus is refused")
        void theCodeIsReadInBaseZero() {
            assertAll(
                    () -> assertEquals(ObjectFlag.OF_FREE_ACT,
                            assembleOne(withFails(fail("01", "FREE_ACT"))).getFail().get(0).getObjFlagCode(),
                            "octal 01 is 1"),
                    () -> assertEquals(ElementEnum.ELEM_POIS,
                            assembleOne(withFails(fail("0x2", "POIS"))).getFail().get(0).getElementCode(),
                            "hex 0x2 is 2"),
                    () -> assertEquals(ElementEnum.ELEM_FIRE,
                            assembleOne(withFails(fail("+3", "FIRE"))).getFail().get(0).getElementCode(),
                            "a plus sign is allowed"),
                    () -> assertEquals(TimedEffect.TMD_OPP_POIS,
                            assembleOne(withFails(fail("05", "OPP_POIS"))).getFail().get(0).getEffectCode(),
                            "octal 05 is 5"));
            assertStopsTheLoad(withFails(fail("-1", "POIS")), "has an illegal fail type value: -1");
        }
    }

    @Nested
    @DisplayName("resist:, only the elements count")
    class ResistElements {

        @Test
        @DisplayName("a projection name past the elements stops the load, as idx >= ELEM_MAX does")
        void aNonElementProjectionIsRefused() {
            assertStopsTheLoad(fields("", "LIGHT_WEAK", "", "", ""), "has an illegal resist value: LIGHT_WEAK");
        }

        @Test
        @DisplayName("the first and last elements load")
        void theEndsOfTheElementListLoad() {
            assertEquals(ElementEnum.ELEM_ACID, assembleOne(fields("", "ACID", "", "", "")).getTempResist());
            assertEquals(ElementEnum.ELEM_ARROW, assembleOne(fields("", "ARROW", "", "", "")).getTempResist());
        }
    }

    @Nested
    @DisplayName("integers, as strtol and strtoul with base 0 read them")
    class IntegerReading {

        @Test
        @DisplayName("lower-bound: reads octal, hex, a plus sign, -0 and trailing text")
        void lowerBoundIsReadInBaseZero() {
            assertAll(
                    () -> assertEquals(8, assembleOne(fields("", "", "", "", "010")).getLowerBound()),
                    () -> assertEquals(16, assembleOne(fields("", "", "", "", "0x10")).getLowerBound()),
                    () -> assertEquals(5, assembleOne(fields("", "", "", "", "+5")).getLowerBound()),
                    () -> assertEquals(0, assembleOne(fields("", "", "", "", "-0")).getLowerBound()),
                    () -> assertEquals(7, assembleOne(fields("", "", "", "", "7x")).getLowerBound()));
        }

        @Test
        @DisplayName("a grade maximum reads octal and hex")
        void gradeMaximumIsReadInBaseZero() {
            assertAll(
                    () -> assertEquals(8, assembleOne(withGrade("w", "010")).getGrade().get(1).max()),
                    () -> assertEquals(16, assembleOne(withGrade("w", "0x10")).getGrade().get(1).max()));
        }

        @Test
        @DisplayName("the flag-synonym exact value is true for any non-zero reading, false for a zero one")
        void exactIsReadInBaseZero() {
            assertAll(
                    () -> assertFalse(assembleOne(fields("", "", "AFRAID", "0x0", "")).isoFlagExactlySyn()),
                    () -> assertFalse(assembleOne(fields("", "", "AFRAID", "-0", "")).isoFlagExactlySyn()),
                    () -> assertTrue(assembleOne(fields("", "", "AFRAID", "0x1", "")).isoFlagExactlySyn()),
                    () -> assertTrue(assembleOne(fields("", "", "AFRAID", "010", "")).isoFlagExactlySyn()),
                    () -> assertTrue(assembleOne(fields("", "", "AFRAID", "1x", "")).isoFlagExactlySyn(),
                            "trailing text after the digits is ignored"));
        }

        @Test
        @DisplayName("a flag-synonym exact value with no digits stops the load")
        void exactWithNoDigitsStopsTheLoad() {
            assertStopsTheLoad(fields("", "", "AFRAID", "abc", ""),
                    "has an illegal flag synonym value: abc");
        }
    }

    @Nested
    @DisplayName("grade colours, as color_text_to_attr and color_char_to_attr read them")
    class GradeColours {

        @Test
        @DisplayName("a full colour name matches without regard to case")
        void aNameMatchesWithoutRegardToCase() {
            assertEquals(ColourEnum.COLOUR_GREEN, assembleOne(withGrade("Green", "10")).getGrade().get(1).colour());
            assertEquals(ColourEnum.COLOUR_GREEN, assembleOne(withGrade("GREEN", "10")).getGrade().get(1).colour());
        }

        @Test
        @DisplayName("an unmatched name becomes white")
        void anUnmatchedNameIsWhite() {
            assertEquals(ColourEnum.COLOUR_WHITE,
                    assembleOne(withGrade("Nonsense", "10")).getGrade().get(1).colour());
        }

        @Test
        @DisplayName("a single letter is matched with its case")
        void aLetterIsMatchedWithItsCase() {
            assertEquals(ColourEnum.COLOUR_GREEN, assembleOne(withGrade("g", "10")).getGrade().get(1).colour());
            assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, assembleOne(withGrade("G", "10")).getGrade().get(1).colour());
        }
    }

    @Test
    @DisplayName("a name written with the TMD_ prefix is not in C's list, so it stops the load")
    void aPrefixedNameStopsTheLoad() {
        assertStopsTheLoad(new PlayerTimedParseRecord("TMD_FAST", "", List.of(GRADE), "", "", "", "", List.of(),
                null, null, "", "", "", "", "", "", List.of(), LINE), "has an illegal name value: TMD_FAST");
    }
}
