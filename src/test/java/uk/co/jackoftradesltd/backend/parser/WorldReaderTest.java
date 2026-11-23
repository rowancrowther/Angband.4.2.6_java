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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.cave.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link WorldReader} (grammar-suite reader track R4), against
 * {@code parse_world_level}, {@code run_parse_world} and {@code finish_parse_world} in
 * {@code init.c}.
 *
 * <p>These tests run the full reader-through-assembler pipeline, so they assert on the assembled
 * {@link World} domain records, not the intermediate {@code WorldParseRecord} DTOs. The
 * {@code "None"} that C turns into {@code NULL} for a missing neighbour is kept as the literal text
 * by {@link uk.co.jackoftradesltd.backend.parser.world.WorldAssembler}.
 *
 * <p>Every error is hard, as the reader documents it: nothing loads after either of these.
 * <ul>
 *   <li>A grammar error, thrown as a {@code ParseCancellationException}.</li>
 *   <li>A level number that does not fit an {@code int}, thrown as a
 *   {@code DataFileParseException}. C's {@code strtol} would clamp and truncate it instead.</li>
 * </ul>
 * There is no {@code record-count:} header, as in C.
 *
 * <p>The tests with {@code WhereC} in the name pin a place where this reader refuses input that C
 * accepts; they are written up in {@code docs/grammar_issues.md}.
 *
 * <p>Expected values come from the C source and the upstream file, not from the Java: the shipped
 * file lists the Town (depth 0, up {@code None}) then {@code Angband 1} to {@code Angband 127}, each
 * level's up and down being its neighbours, and the last level's down being {@code None}.
 *
 * <p>Test class WorldReaderTest coded before 261014, commented in full on 261019, and updated on
 * 261122 once a name, up or down made only of digits loaded as in C.
 *
 * @author Rowan Crowther
 */
class WorldReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/world.txt";

    @TempDir
    Path tempDir;

    /**
     * Writes {@code content} to a file in the temp dir and returns its absolute path.
     */
    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    private ParseResult<World> read(String content) throws IOException {
        return new WorldReader().parseWithResults(tempFile("world.txt", content));
    }

    /**
     * Reads {@code content}, expects the load to stop with a {@link DataFileParseException}, and
     * returns that exception's message.
     *
     * <p>Function refusal coded on 261104, commented in full on 261104. It replaces anyError, which
     * searched the errors list a failed load no longer has.
     */
    private String refusal(String content) {
        return assertThrows(DataFileParseException.class, () -> read(content)).getMessage();
    }

    // ---------------------------------------------------------------- the shipped file

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllLevels() throws IOException {
        ParseResult<World> result = new WorldReader().parseWithResults(REAL_FILE);


        List<World> levels = result.items();
        assertEquals(128, levels.size());

        World town = levels.get(0);
        assertEquals(0, town.levelNumber());
        assertEquals("Town", town.levelName());
        assertEquals("None", town.prevLevel());
        assertEquals("Angband 1", town.nextLevel());

        World deepest = levels.get(127);
        assertEquals(127, deepest.levelNumber());
        assertEquals("Angband 127", deepest.levelName());
        assertEquals("Angband 126", deepest.prevLevel());
        assertEquals("None", deepest.nextLevel());
    }

    @Test
    void theRealFileIsInDepthOrderWithEachLevelLinkedToItsNeighbours() throws IOException {
        List<World> levels = new WorldReader().parseWithResults(REAL_FILE).items();

        for (int i = 1; i < levels.size(); i++) {
            World level = levels.get(i);
            assertEquals(i, level.levelNumber());
            assertEquals("Angband " + i, level.levelName());
            assertEquals(i == 1 ? "Town" : "Angband " + (i - 1), level.prevLevel());
            assertEquals(i == 127 ? "None" : "Angband " + (i + 1), level.nextLevel());
        }
    }

    @Test
    void parseReturnsTheSameLevelsAsParseWithResults() throws IOException {
        WorldReader reader = new WorldReader();

        assertEquals(reader.parseWithResults(REAL_FILE).items(), reader.parse(REAL_FILE));
    }

    @Test
    void aMissingFileIsAnIoExceptionWhereCQuits() {
        assertThrows(IOException.class,
                () -> new WorldReader().parseWithResults(tempDir.resolve("nope.txt").toString()));
    }

    // ---------------------------------------------------------------- ordinary records

    @Test
    void aSingleLevelWithNoNeighboursKeepsTheNoneTextOnBothSides() throws IOException {
        ParseResult<World> result = read("level:0:Town:None:None\n");

        assertEquals(List.of(new World(0, "Town", "None", "None")), result.items());
    }

    @Test
    void levelsComeBackInFileOrderNotDepthOrder() throws IOException {
        ParseResult<World> result = read(
                "level:2:Two:One:None\nlevel:0:Zero:None:One\nlevel:1:One:Zero:Two\n");

        assertEquals(List.of(2, 0, 1), result.items().stream().map(World::levelNumber).toList());
    }

    @Test
    void namesWithSpacesAndDigitsAreKeptWhole() throws IOException {
        ParseResult<World> result = read("level:5:Angband 5:Angband 4:Angband 6\n");

        assertEquals(new World(5, "Angband 5", "Angband 4", "Angband 6"), result.items().get(0));
    }

    @Test
    void aNegativeDepthIsAcceptedAsInC() throws IOException {
        ParseResult<World> result = read("level:-1:Pit:None:None\n");

        assertEquals(-1, result.items().get(0).levelNumber());
    }

    @Test
    void theLargestIntDepthIsAcceptedAndOneMoreIsHard() throws IOException {
        ParseResult<World> ok = read("level:2147483647:Max:None:None\n");
        assertEquals(Integer.MAX_VALUE, ok.items().get(0).levelNumber());

        String message = refusal("level:2147483648:Over:None:None\n");
        assertTrue(message.contains("illegal level value") && message.contains("2147483648"), message);
    }

    @Test
    void aLeadingZeroDepthIsReadAsDecimalWhereCReadsItAsOctal() throws IOException {
        // strtol(tok, &z, 0) reads "010" as 8; the Java INTEGER token and parseInt read 10.
        ParseResult<World> result = read("level:010:Ten:None:None\n");

        assertEquals(10, result.items().get(0).levelNumber());
    }

    @Test
    void commentsAndBlankLinesAreSkipped() throws IOException {
        ParseResult<World> result = read(
                "# a comment\n\n# another\nlevel:0:Town:None:None\n");

        assertEquals(1, result.items().size());
    }

    // ---------------------------------------------------------------- hard errors

    @Test
    void anOverflowingLevelNumberIsHardAndNamesTheLine() throws IOException {
        String message = refusal("level:99999999999:Deep:None:None\n");

        assertTrue(message.contains("line: 1") && message.contains("illegal level value")
                && message.contains("99999999999"), message);
    }

    @Test
    void aLevelWithTooFewFieldsIsHardAsInC() throws IOException {
        // PARSE_ERROR_MISSING_FIELD in C, which run_parser turns into a quit.
        assertThrows(ParseCancellationException.class, () -> read("level:0:Town:None\n"));

    }

    @Test
    void anUnknownDirectiveIsHardAsInC() throws IOException {
        assertThrows(ParseCancellationException.class, () -> read("levl:0:Town:None:None\n"));

    }

    @Test
    void aNonNumericDepthIsHardAsInC() throws IOException {
        assertThrows(ParseCancellationException.class, () -> read("level:zero:Town:None:None\n"));

    }

    // ---------------------------------------------------------------- places C is more lenient

    @Test
    void anEmptyFileLoadsAnEmptyWorldAsInC() throws IOException {
        assertEquals(0, read("").items().size());
    }

    @Test
    void aFileOfOnlyCommentsLoadsAnEmptyWorldAsInC() throws IOException {
        assertEquals(0, read("# nothing but a comment\n").items().size());
    }

    @Test
    void aFieldAfterTheDownNameIsHardHereWhereCIgnoresIt() throws IOException {
        assertThrows(ParseCancellationException.class, () -> read("level:0:Town:None:Angband 1:extra\n"));

    }

    @Test
    void aHyphenInANameIsKeptAsInC() throws IOException {
        // A sym is any text without a colon, so C keeps "Hell-hole" whole.
        ParseResult<World> result = read("level:0:Hell-hole:None:None\n");

        assertEquals(List.of(new World(0, "Hell-hole", "None", "None")), result.items());
    }

    /**
     * A {@code sym} is any text without a colon, so {@code parse_world_level} keeps {@code 7} as a
     * level name. The lexer still gives {@code 7} as an {@code INTEGER}, because that rule is listed
     * before {@code NAME} and wins the tie on length, so {@code levelName} takes
     * {@code NAME | INTEGER} and reads the token's text.
     *
     * <p>Function aNameOfOnlyDigitsLoadsAsInC coded on 261122, commented in full on 261122. It
     * replaces {@code aNameOfOnlyDigitsIsHardHereWhereCTakesAnySymExceptColon}, which asserted the
     * refusal.
     */
    @Test
    void aNameOfOnlyDigitsLoadsAsInC() throws IOException {
        ParseResult<World> result = read("level:5:7:None:None\n");

        assertEquals(List.of(new World(5, "7", "None", "None")), result.items());
    }

    /**
     * A minus sign or leading zeros make no difference to a {@code sym}: {@code -5} and {@code 007}
     * are kept as written, not read as numbers. Both lex as {@code INTEGER}.
     *
     * <p>Function aSignedOrZeroPaddedNameLoadsAsWrittenAsInC coded on 261122, commented in full on
     * 261122.
     */
    @Test
    void aSignedOrZeroPaddedNameLoadsAsWrittenAsInC() throws IOException {
        assertEquals(List.of(new World(5, "-5", "None", "None")), read("level:5:-5:None:None\n").items());
        assertEquals(List.of(new World(5, "007", "None", "None")), read("level:5:007:None:None\n").items());
    }

    /**
     * The up and down fields are {@code sym}s too, so each may be made only of digits. In C the
     * pair is cut at the colon like the name, so {@code -3} and {@code 007} load as written.
     *
     * <p>Function anUpOrDownNameOfOnlyDigitsLoadsAsInC coded on 261122, commented in full on 261122.
     */
    @Test
    void anUpOrDownNameOfOnlyDigitsLoadsAsInC() throws IOException {
        assertEquals(List.of(new World(5, "Name", "7", "None")), read("level:5:Name:7:None\n").items());
        assertEquals(List.of(new World(5, "Name", "None", "7")), read("level:5:Name:None:7\n").items());
        assertEquals(List.of(new World(5, "Name", "-3", "007")), read("level:5:Name:-3:007\n").items());
        assertEquals(List.of(new World(5, "1", "2", "3")), read("level:5:1:2:3\n").items());
    }

    /**
     * A name that is too long for an {@code int} is still just a {@code sym}. Only the depth field
     * is read as a number, so only it can overflow.
     *
     * <p>Function aDigitOnlyNameBeyondIntRangeLoadsAsInC coded on 261122, commented in full on
     * 261122.
     */
    @Test
    void aDigitOnlyNameBeyondIntRangeLoadsAsInC() throws IOException {
        assertEquals(List.of(new World(5, "99999999999", "None", "None")),
                read("level:5:99999999999:None:None\n").items());
    }

    /**
     * An empty down field is a missing field, which C refuses with {@code PARSE_ERROR_MISSING_FIELD}.
     * Letting {@code INTEGER} stand in for {@code NAME} must not let an empty field through.
     *
     * <p>Function anEmptyDownNameIsHardAsInC coded on 261122, commented in full on 261122.
     */
    @Test
    void anEmptyDownNameIsHardAsInC() {
        assertThrows(ParseCancellationException.class, () -> read("level:5:Name:None:\n"));
    }

    @Test
    void aHexadecimalDepthIsHardHereWhereCReadsBaseZero() throws IOException {
        // strtol(tok, &z, 0) reads "0x10" as 16.
        assertThrows(ParseCancellationException.class, () -> read("level:0x10:Town:None:None\n"));

    }
}
