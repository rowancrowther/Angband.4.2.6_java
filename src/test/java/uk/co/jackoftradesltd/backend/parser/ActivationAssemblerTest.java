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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.activation.ActivationAssembler;
import uk.co.jackoftradesltd.backend.parser.activation.ActivationParseRecord;
import uk.co.jackoftradesltd.middle.Activation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ActivationAssembler}.
 *
 * <p>These construct {@link ActivationParseRecord}s directly - no grammar, no reader - to
 * exercise the assembler in isolation. The grammar hands the assembler raw strings
 * ({@code level}, {@code power}, {@code aim}) and leaves every numeric conversion and default to
 * this stage. The lexer's {@code INTEGER} token guarantees an optional {@code -} and a run of ASCII
 * digits, so text such as {@code high}, {@code 12abc} or a digit from another script never survives
 * lexing and has to be injected by hand to reach the conversion's edge cases.
 *
 * <p>The expected values are worked out from C, not from the Java: {@code parse_act_aim},
 * {@code parse_act_level} and {@code parse_act_power} in {@code obj-init.c}, and the {@code int}
 * and {@code uint} parsing in {@code parser.c}, which reads the number with {@code strtol} or
 * {@code strtoul} and base 0 and refuses a token only if it holds no digits (or, for a
 * {@code uint}, starts with {@code -}). The groups are:
 * <ul>
 *   <li>the empty-string guards - an absent directive arrives as {@code ""} and gives false or 0;</li>
 *   <li>base 0 reading - a leading {@code 0} is octal, reading stops at the first character that
 *       is not a digit of the base;</li>
 *   <li>the refusals - no digits, or a {@code -} on {@code aim:} or {@code power:}, stop the load;</li>
 *   <li>the stop-the-load policy - the first bad record in file order throws
 *       {@link DataFileParseException} naming the file and the line, and within a record the
 *       fault reported is the first of {@code aim:}, {@code level:}, {@code power:};</li>
 *   <li>the accepted deviation from C's wrap to 32 bits - an out of range number clamps (see
 *       {@code docs/Accepted_Deviations.md}).</li>
 * </ul>
 *
 * <p>Every record here carries an <em>empty</em> effect list, so the tests stay independent of
 * {@code EffectAssembler} and the game-data registries it consults; effect failures are covered
 * through the reader in {@code ActivationReaderTest}.
 *
 * <p>Class ActivationAssemblerTest coded before 261017, rewritten and commented in full on 261022
 * for the stop-the-load policy and C's base 0 reading.
 *
 * @author Rowan Crowther
 */
class ActivationAssemblerTest {

    /**
     * Builds a parse record with the given raw directive strings and an empty effect list,
     * mirroring what the grammar hands the assembler. {@code message} and {@code desc} are left
     * blank as they need no conversion.
     *
     * @param name  the activation code name
     * @param aim   the raw {@code aim:} value (may be "" for absent)
     * @param level the raw {@code level:} value (may be "" for absent)
     * @param power the raw {@code power:} value (may be "" for absent)
     * @param line  the source line the record is reported against
     * @return a fully populated {@link ActivationParseRecord} with no effects
     */
    private static ActivationParseRecord rec(String name, String aim, String level,
                                             String power, int line) {
        return new ActivationParseRecord(name, aim, level, power, "", "", List.of(), line);
    }

    /**
     * Assembles one record and returns its activation.
     */
    private static Activation one(String aim, String level, String power) {
        List<Activation> out = new ActivationAssembler()
                .assemble(List.of(rec("T", aim, level, power, 3)), new ArrayList<>(), "test.txt");
        assertEquals(1, out.size());
        return out.get(0);
    }

    /**
     * Assembles one record that must stop the load, and returns the exception's message.
     */
    private static String stopMessage(String aim, String level, String power) {
        return assertThrows(DataFileParseException.class, () -> one(aim, level, power)).getMessage();
    }

    private static Object field(Activation a, String name) throws Exception {
        Field f = Activation.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(a);
    }

    private static int level(Activation a) throws Exception {
        return (int) field(a, "level");
    }

    private static boolean aim(Activation a) throws Exception {
        return (boolean) field(a, "aim");
    }

    @Test
    void cleanRecordAssemblesToOneActivation() {
        List<String> errors = new ArrayList<>();

        List<Activation> out = new ActivationAssembler()
                .assemble(List.of(rec("CURE_LIGHT", "0", "5", "10", 3)), errors, "test.txt");

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        assertEquals("CURE_LIGHT", out.get(0).getName());
        assertEquals(10, out.get(0).getPower());
    }

    @Test
    void absentAimLevelAndPowerAreFalseAndZeroWithoutError() throws Exception {
        // parse_act_name zero-fills the record, so a directive that never appears leaves false, 0, 0.
        Activation a = one("", "", "");

        assertFalse(aim(a));
        assertEquals(0, level(a));
        assertEquals(0, a.getPower());
    }

    @Test
    void anyNonZeroAimIsTrueAsInC() throws Exception {
        // parse_act_aim: act->aim = val ? true : false
        assertTrue(aim(one("1", "", "")));
        assertTrue(aim(one("2", "", "")));
        assertTrue(aim(one("01", "", "")));
        assertTrue(aim(one("010", "", "")));
        assertTrue(aim(one("07", "", "")));
        assertFalse(aim(one("0", "", "")));
        assertFalse(aim(one("00", "", "")));
    }

    @Test
    void anAimWhoseOctalDigitsAreAllZeroIsFalseAsInC() throws Exception {
        // strtoul base 0 reads "08" as the octal 0 and stops at the 8 without an error.
        assertFalse(aim(one("08", "", "")));
        assertFalse(aim(one("09", "", "")));
        assertFalse(aim(one("0008", "", "")));
        // "0018" is octal 01, then stops at the 8.
        assertTrue(aim(one("0018", "", "")));
    }

    @Test
    void aLeadingZeroIsOctalAsInC() throws Exception {
        // strtol(tok, &z, 0): a leading 0 selects base 8.
        assertEquals(8, level(one("", "010", "")));
        assertEquals(7, level(one("", "07", "")));
        assertEquals(15, level(one("", "017", "")));
        assertEquals(0, level(one("", "0", "")));
        assertEquals(8, one("", "", "010").getPower());
        assertEquals(15, one("", "", "017").getPower());
    }

    @Test
    void anOctalReadStopsAtTheFirstEightOrNineAsInC() throws Exception {
        // "08" is 0 and "019" is 1: the read stops at the first character that is not 0-7,
        // and C accepts the token because some digits were read.
        assertEquals(0, level(one("", "08", "")));
        assertEquals(1, level(one("", "019", "")));
        assertEquals(0, one("", "", "09").getPower());
        assertEquals(10, level(one("", "0128", "")));
    }

    @Test
    void aDecimalReadStopsAtTheFirstNonDigitAsInC() throws Exception {
        // strtol reads the leading digits and ignores the rest; only "no digits at all" is refused.
        assertEquals(12, level(one("", "12abc", "")));
        assertEquals(12, one("", "", "12abc").getPower());
        assertEquals(12, level(one("", "12-3", "")));
    }

    @Test
    void anOctalReadStopsAtAnyNonDigitAsInC() throws Exception {
        // (C would read "0x7" as hex 7, but the lexer's INTEGER token never lets a 0x through:
        // see ActivationReaderTest.aHexLevelIsAHardErrorHereWhereCReadsItAsSixteen.)
        assertEquals(0, level(one("", "0z", "")));
        assertEquals(7, level(one("", "07z", "")));
        assertEquals(7, level(one("", "07-3", "")));
    }

    @Test
    void aDigitFromAnotherScriptIsNotADigitAsInC() throws Exception {
        // Arabic-Indic digits: Character.isDigit is true for them, but strtol stops at the first byte.
        assertEquals(1, level(one("", "1٢", "")));
        assertEquals(0, level(one("", "0٣", "")));
        assertTrue(stopMessage("", "١٢", "").contains("invalid level"));
    }

    @Test
    void aNegativeLevelIsAcceptedAsInC() throws Exception {
        // "level int level": an int, so a leading '-' is fine.
        assertEquals(-5, level(one("", "-5", "")));
        assertEquals(-8, level(one("", "-010", "")));
        assertEquals(-7, level(one("", "-07", "")));
        assertEquals(0, level(one("", "-0", "")));
        assertEquals(0, level(one("", "-08", "")));
    }

    @Test
    void aNegativePowerStopsTheLoadAsInC() {
        // "power uint power": parser.c refuses a uint token that starts with '-', even -0.
        assertTrue(stopMessage("", "", "-3").contains("negative power"));
        assertTrue(stopMessage("", "", "-0").contains("negative power"));
        assertTrue(stopMessage("", "", "-010").contains("negative power"));
    }

    @Test
    void aNegativeAimStopsTheLoadAsInC() {
        // "aim uint aim": same refusal as power.
        String message = stopMessage("-1", "", "");
        assertTrue(message.contains("aim"), message);
        assertTrue(stopMessage("-0", "", "").contains("aim"));
    }

    @Test
    void textWithNoDigitsStopsTheLoadAsInC() {
        // z == tok in parser.c: nothing was read.
        String level = stopMessage("", "high", "");
        assertTrue(level.contains("invalid level"), level);
        String power = stopMessage("", "", "lots");
        assertTrue(power.contains("invalid power"), power);
        String aim = stopMessage("yes", "", "");
        assertTrue(aim.contains("invalid aim"), aim);
    }

    @Test
    void theStopMessageNamesTheFileAndTheLineTheRecordStartsOn() {
        String message = assertThrows(DataFileParseException.class, () -> new ActivationAssembler()
                .assemble(List.of(rec("BAD_LEVEL", "0", "high", "1", 7)), new ArrayList<>(), "activation.txt"))
                .getMessage();

        assertTrue(message.contains("line: 7 of file: activation.txt"), message);
        assertTrue(message.contains("invalid level"), message);
        assertTrue(message.contains("high"), message);
    }

    @Test
    void theFirstBadRecordStopsTheLoadAndNothingAfterItIsRead() {
        // C quits on the first line that fails; the good record after it is never reached.
        String message = assertThrows(DataFileParseException.class, () -> new ActivationAssembler().assemble(
                List.of(rec("FINE", "0", "5", "1", 2),
                        rec("BROKEN", "0", "nope", "1", 4),
                        rec("WORSE", "0", "alsonot", "1", 8)),
                new ArrayList<>(), "test.txt")).getMessage();

        assertTrue(message.contains("line: 4 of"), message);
        assertFalse(message.contains("line: 8"), message);
    }

    @Test
    void aRecordWithSeveralFaultsReportsAimThenLevelThenPower() {
        // Accepted deviation: C reports the fault nearest the top of the block; the assembler
        // checks aim:, then level:, then power:, whatever order the lines came in.
        assertTrue(stopMessage("-1", "high", "-3").contains("aim"));
        assertTrue(stopMessage("0", "high", "-3").contains("invalid level"));
        assertTrue(stopMessage("0", "5", "-3").contains("negative power"));
    }

    @Test
    void aNumberTooBigForAnIntIsClampedHereWhereCWraps() throws Exception {
        // Accepted deviation. C: strtol saturates at 64 bits and the int keeps the low 32, so
        // 99999999999999 becomes 276447231 and 3000000000 becomes -1294967296. Java clamps.
        assertEquals(Integer.MAX_VALUE, level(one("", "99999999999999", "")));
        assertEquals(Integer.MAX_VALUE, level(one("", "3000000000", "")));
        assertEquals(Integer.MAX_VALUE, level(one("", "2147483648", "")));
        assertEquals(Integer.MAX_VALUE, one("", "", "99999999999").getPower());
        assertEquals(Integer.MAX_VALUE, one("", "", "3000000000").getPower());
        // 2^64 + 5 must clamp, not wrap to 5 on the way: the clamp is applied to the whole number.
        assertEquals(Integer.MAX_VALUE, level(one("", "18446744073709551621", "")));
    }

    @Test
    void aNegativeNumberTooBigForAnIntClampsToIntMinHereWhereCWraps() throws Exception {
        assertEquals(Integer.MIN_VALUE, level(one("", "-99999999999999", "")));
        assertEquals(Integer.MIN_VALUE, level(one("", "-3000000000", "")));
        assertEquals(Integer.MIN_VALUE, level(one("", "-2147483648", "")));
        assertEquals(-2147483647, level(one("", "-2147483647", "")));
        assertEquals(Integer.MIN_VALUE, level(one("", "-18446744073709551621", "")));
    }

    @Test
    void anAimTooBigForAnUnsignedIntIsTrueHereWhereCWrapsToFalse() throws Exception {
        // Accepted deviation. C: strtoul gives 4294967296, narrowed to unsigned int it is 0, so false.
        assertTrue(aim(one("4294967296", "", "")));
    }

    @Test
    void anOctalNumberTooBigForAnIntIsClampedToo() throws Exception {
        // 0777777777777 is 8^12 - 1, far past an int.
        assertEquals(Integer.MAX_VALUE, level(one("", "0777777777777", "")));
        assertEquals(Integer.MIN_VALUE, level(one("", "-0777777777777", "")));
    }

    @Test
    void theResultIsInReverseFileOrderAndNumberedFromZero() {
        List<Activation> out = new ActivationAssembler().assemble(
                List.of(rec("FIRST", "0", "5", "1", 2), rec("SECOND", "1", "6", "2", 6), rec("THIRD", "0", "7", "3", 10)),
                new ArrayList<>(), "test.txt");

        assertEquals(List.of("THIRD", "SECOND", "FIRST"), out.stream().map(Activation::getName).toList());
    }

    @Test
    void eachActivationIsNumberedByItsPlaceInTheReversedListFromZero() throws Exception {
        // finish_parse_act numbers the prepended list from its head, so the last record is first.
        // C counts from 1; the accepted base-0 deviation makes the Java index one lower.
        List<Activation> out = new ActivationAssembler().assemble(
                List.of(rec("FIRST", "0", "5", "1", 2), rec("SECOND", "1", "6", "2", 6), rec("THIRD", "0", "7", "3", 10)),
                new ArrayList<>(), "test.txt");

        assertEquals(0, field(out.get(0), "index"));
        assertEquals(1, field(out.get(1), "index"));
        assertEquals(2, field(out.get(2), "index"));
    }

    @Test
    void messageAndDescPassThroughUnchangedAndNullStaysNull() throws Exception {
        // parse_act_msg and parse_act_desc string_append onto a zero-filled record: absent is NULL.
        List<Activation> out = new ActivationAssembler().assemble(
                List.of(new ActivationParseRecord("BOTH", "", "", "", "You fire.", "fires a bolt", List.of(), 2),
                        new ActivationParseRecord("NEITHER", "", "", "", null, null, List.of(), 6)),
                new ArrayList<>(), "test.txt");

        assertEquals(null, field(out.get(0), "message"));
        assertEquals(null, field(out.get(0), "desc"));
        assertEquals("You fire.", field(out.get(1), "message"));
        assertEquals("fires a bolt", field(out.get(1), "desc"));
    }

    @Test
    void aRecordWithNoEffectsGivesAnEmptyEffectListWhereCHoldsNull() throws Exception {
        // C's act->effect stays NULL; the Java list is empty, never null, so copy() can walk it.
        List<?> effects = (List<?>) field(one("", "", ""), "effects");

        assertTrue(effects.isEmpty());
    }

    @Test
    void anEmptyRecordListAssemblesToAnEmptyList() {
        assertTrue(new ActivationAssembler().assemble(List.of(), new ArrayList<>(), "test.txt").isEmpty());
    }
}
