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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.names.NamesAssembler;
import uk.co.jackoftradesltd.backend.parser.names.NamesParseRecord;
import uk.co.jackoftradesltd.middle.game.Name;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link NamesAssembler}.
 *
 * <p>These build {@link NamesParseRecord}s directly, with no grammar, so section text the lexer
 * would never produce ({@code NaN}, an empty string, a leading space) still reaches the assembler.
 * End-to-end cases live in {@code NamesReaderTest}.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>the reading of the number from the {@code uint} branch of {@code parser_parse} in
 *       {@code parser.c}: {@code strtoul(tok, &z, 0)}, so base 0 ({@code 010} is octal 8,
 *       {@code 0x2} is 2, {@code 08} stops at the 8 and is 0), and {@code PARSE_ERROR_NOT_NUMBER}
 *       when no digit is read or the token starts with {@code -};</li>
 *   <li>the range from {@code parse_names_section} in {@code init.c}: a section at or above
 *       {@code RANDNAME_NUM_TYPES} (3, {@code randname.h}) is {@code PARSE_ERROR_OUT_OF_BOUNDS};</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 * Two accepted deviations are pinned as well: section 0, which C accepts and Java refuses, and a
 * number outside the {@code int} range, which C wraps to 32 bits and Java refuses.
 *
 * <p>Class NamesAssemblerTest coded on 261027, commented in full on 261027.
 *
 * @author Rowan Crowther
 */
class NamesAssemblerTest {

    private static final String FILE = "names.txt";

    private static List<Name> assemble(List<NamesParseRecord> records, List<String> errors) {
        return new NamesAssembler().assemble(records, errors, FILE);
    }

    private static Name one(String section) {
        List<Name> names = assemble(List.of(new NamesParseRecord(section, List.of("w"), 7)),
                new ArrayList<>());
        assertEquals(1, names.size());
        return names.getFirst();
    }

    /**
     * The section is refused: a {@link DataFileParseException} naming the record's line and the
     * file, as C's parse error stops the load.
     */
    private static void assertRefused(String section, String message) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> one(section));
        assertTrue(thrown.getMessage().contains(message), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 7"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    // ---- Ordinary path -----------------------------------------------------

    /** The two sections C uses: {@code RANDNAME_TOLKIEN} is 1 and {@code RANDNAME_SCROLL} is 2. */
    @ParameterizedTest
    @CsvSource({"1, 1", "2, 2"})
    void theTwoRealSectionsLoad(String text, int expected) {
        assertEquals(expected, one(text).getSection());
    }

    /**
     * {@code strtoul} in base 0: leading zeros switch to octal, which agrees with decimal for 1 and
     * 2; {@code 08} reads only the {@code 0}, as {@code 8} is not an octal digit, and is then section
     * 0, which is refused.
     */
    @ParameterizedTest
    @CsvSource({"01, 1", "02, 2", "002, 2", "0x1, 1", "0X2, 2"})
    void prefixedSectionsAreReadInBaseZero(String text, int expected) {
        assertEquals(expected, one(text).getSection());
    }

    @Test
    void theWordsAndTheirOrderPassThroughUnchanged() {
        List<String> words = List.of("b", " a ", "b", "Gil-galad's");
        List<Name> names = assemble(List.of(new NamesParseRecord("1", words, 3)), new ArrayList<>());

        assertEquals(words, names.getFirst().getWord());
    }

    @Test
    void aSectionWithNoWordsGivesAnEmptyList() {
        assertTrue(assemble(List.of(new NamesParseRecord("2", List.of(), 3)), new ArrayList<>())
                .getFirst().getWord().isEmpty());
    }

    /**
     * One {@link Name} per record, in file order, with no joining: C files a repeated section into
     * the same list, which here is {@code MiscRegistry.setNames}'s work.
     */
    @Test
    void recordsComeBackOnePerBlockInFileOrder() {
        List<Name> names = assemble(List.of(
                new NamesParseRecord("2", List.of("x"), 3),
                new NamesParseRecord("1", List.of("y"), 5),
                new NamesParseRecord("2", List.of("z"), 7)), new ArrayList<>());

        assertEquals(List.of(2, 1, 2), names.stream().map(Name::getSection).toList());
        assertEquals(List.of("z"), names.get(2).getWord());
    }

    /** The word list is copied, so a later change to the record's list does not reach the {@link Name}. */
    @Test
    void theWordListIsCopied() {
        List<String> words = new ArrayList<>(List.of("a"));
        Name name = assemble(List.of(new NamesParseRecord("1", words, 3)), new ArrayList<>()).getFirst();
        words.add("b");

        assertEquals(List.of("a"), name.getWord());
    }

    @Test
    void anEmptyListGivesAnEmptyResultAndNoErrors() {
        List<String> errors = new ArrayList<>();

        assertTrue(assemble(List.of(), errors).isEmpty());
        assertTrue(errors.isEmpty());
    }

    /** Nothing is a soft error any longer: a good load leaves the sink empty. */
    @Test
    void nothingIsWrittenToTheErrorList() {
        List<String> errors = new ArrayList<>();
        assemble(List.of(new NamesParseRecord("1", List.of("a"), 3),
                new NamesParseRecord("2", List.of("b"), 5)), errors);

        assertTrue(errors.isEmpty(), errors::toString);
    }

    // ---- Refused sections --------------------------------------------------

    /** {@code parse_names_section}: 3 is {@code RANDNAME_NUM_TYPES} itself, and anything above it is out. */
    @ParameterizedTest
    @ValueSource(strings = {"3", "4", "255", "2147483647"})
    void aSectionAtOrAboveThreeIsOutOfBounds(String text) {
        assertRefused(text, "illegal section value: " + text);
    }

    /** {@code 010} is octal 8 to {@code strtoul} base 0, and {@code 0x3} is 3: both out of bounds in C. */
    @ParameterizedTest
    @ValueSource(strings = {"010", "03", "0x3", "0xff"})
    void aPrefixedSectionOutOfRangeIsRefusedByItsBaseZeroValue(String text) {
        assertRefused(text, "illegal section value: " + text);
    }

    /**
     * C's {@code *tok == '-'} test refuses any token that starts with a minus, so {@code -0} goes
     * too although its value is 0.
     */
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0", "-2", "-3"})
    void aLeadingMinusIsRefused(String text) {
        assertRefused(text, "illegal negative section value: " + text);
    }

    /** The minus test fires before the range test, so {@code -5} gets the negative message. */
    @Test
    void theMinusTestComesBeforeTheRangeTest() {
        assertRefused("-5", "negative");
    }

    /**
     * Accepted deviation: C accepts section 0 and files its words in a slot nothing reads; Java
     * refuses it. {@code 08} and {@code 0x0} are also 0 under base 0.
     */
    @ParameterizedTest
    @ValueSource(strings = {"0", "00", "08", "0x0"})
    void sectionZeroIsRefused(String text) {
        assertRefused(text, "illegal section value of zero: " + text);
    }

    /**
     * Accepted deviation: C wraps {@code strtoul}'s result into a 32-bit {@code unsigned int}, so
     * {@code 4294967296} is 0 and {@code 4294967297} is 1, both loading. Java clamps to
     * {@code Integer.MAX_VALUE} and refuses.
     */
    @ParameterizedTest
    @ValueSource(strings = {"2147483648", "4294967296", "4294967297", "99999999999"})
    void aNumberOutsideTheIntRangeIsRefused(String text) {
        assertRefused(text, "illegal section value: " + text);
    }

    /** {@code strtoul} reads no digit ({@code z == tok}), {@code PARSE_ERROR_NOT_NUMBER}. */
    @ParameterizedTest
    @ValueSource(strings = {"NaN", "x1", "  "})
    void textWithNoDigitIsRefused(String text) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> one(text));
        assertTrue(thrown.getMessage().contains("section"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 7"), thrown::getMessage);
    }

    /** The first bad record stops the load, even when good ones follow it. */
    @Test
    void aBadRecordStopsTheWholeLoad() {
        List<NamesParseRecord> records = List.of(
                new NamesParseRecord("1", List.of("a"), 3),
                new NamesParseRecord("3", List.of("b"), 5),
                new NamesParseRecord("2", List.of("c"), 7));

        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> assemble(records, new ArrayList<>()));
        assertTrue(thrown.getMessage().contains("line: 5"), thrown::getMessage);
    }
}
