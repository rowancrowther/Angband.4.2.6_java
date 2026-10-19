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
import org.junit.jupiter.api.io.TempDir;
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
 * <p>Error severity, as the reader documents it:
 * <ul>
 *   <li><b>Soft</b> (levels load, error comes back): a {@code record-count:} header that differs
 *   from the number of levels, and one that does not fit an {@code int}. C has no header.</li>
 *   <li><b>Hard</b> (no levels): a grammar error, and a level number that does not fit an
 *   {@code int}. C's {@code strtol} would clamp and truncate it instead.</li>
 * </ul>
 * The tests with {@code WhereC} in the name pin a place where this reader refuses input that C
 * accepts; they are written up in {@code docs/grammar_issues.md}.
 *
 * <p>Expected values come from the C source and the upstream file, not from the Java: the shipped
 * file lists the Town (depth 0, up {@code None}) then {@code Angband 1} to {@code Angband 127}, each
 * level's up and down being its neighbours, and the last level's down being {@code None}.
 *
 * <p>Test class WorldReaderTest coded before 261014, commented in full on 261019.
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

    private static boolean anyError(ParseResult<World> result, String... fragments) {
        return result.errors().stream().anyMatch(e -> {
            for (String fragment : fragments) {
                if (!e.contains(fragment)) {
                    return false;
                }
            }
            return true;
        });
    }

    // ---------------------------------------------------------------- the shipped file

    @Test
    void cleanLoadOfTheRealFileReportsNoErrorsAndAllLevels() throws IOException {
        ParseResult<World> result = new WorldReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());

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
        ParseResult<World> result = read("record-count:1\nlevel:0:Town:None:None\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of(new World(0, "Town", "None", "None")), result.items());
    }

    @Test
    void levelsComeBackInFileOrderNotDepthOrder() throws IOException {
        ParseResult<World> result = read(
                "record-count:3\nlevel:2:Two:One:None\nlevel:0:Zero:None:One\nlevel:1:One:Zero:Two\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of(2, 0, 1), result.items().stream().map(World::levelNumber).toList());
    }

    @Test
    void namesWithSpacesAndDigitsAreKeptWhole() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevel:5:Angband 5:Angband 4:Angband 6\n");

        assertEquals(new World(5, "Angband 5", "Angband 4", "Angband 6"), result.items().get(0));
    }

    @Test
    void aNegativeDepthIsAcceptedAsInC() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevel:-1:Pit:None:None\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-1, result.items().get(0).levelNumber());
    }

    @Test
    void theLargestIntDepthIsAcceptedAndOneMoreIsHard() throws IOException {
        ParseResult<World> ok = read("record-count:1\nlevel:2147483647:Max:None:None\n");
        assertFalse(ok.hasErrors(), () -> ok.errors().toString());
        assertEquals(Integer.MAX_VALUE, ok.items().get(0).levelNumber());

        ParseResult<World> over = read("record-count:1\nlevel:2147483648:Over:None:None\n");
        assertTrue(over.items().isEmpty());
        assertTrue(anyError(over, "Invalid level number", "2147483648"), over.errors()::toString);
    }

    @Test
    void aLeadingZeroDepthIsReadAsDecimalWhereCReadsItAsOctal() throws IOException {
        // strtol(tok, &z, 0) reads "010" as 8; the Java INTEGER token and parseInt read 10.
        ParseResult<World> result = read("record-count:1\nlevel:010:Ten:None:None\n");

        assertEquals(10, result.items().get(0).levelNumber());
    }

    @Test
    void commentsAndBlankLinesAreSkipped() throws IOException {
        ParseResult<World> result = read(
                "# a comment\n\nrecord-count:1\n\n# another\nlevel:0:Town:None:None\n");

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    // ---------------------------------------------------------------- the record-count header

    @Test
    void aShortHeaderIsASoftErrorAndTheLevelsStillLoad() throws IOException {
        ParseResult<World> result = read("record-count:5\nlevel:0:Town:None:Angband 1\n");

        assertEquals(1, result.items().size());
        assertTrue(anyError(result, "declares 5", "contains 1"), result.errors()::toString);
    }

    @Test
    void anOverShortHeaderIsASoftErrorToo() throws IOException {
        ParseResult<World> result = read(
                "record-count:1\nlevel:0:Town:None:Angband 1\nlevel:1:Angband 1:Town:None\n");

        assertEquals(2, result.items().size());
        assertTrue(anyError(result, "declares 1", "contains 2"), result.errors()::toString);
    }

    @Test
    void aHeaderThatOverflowsIntIsASoftErrorNotACrash() throws IOException {
        ParseResult<World> result = read("record-count:99999999999\nlevel:0:Town:None:Angband 1\n");

        assertEquals(1, result.items().size());
        assertTrue(anyError(result, "Invalid number format", "99999999999"), result.errors()::toString);
    }

    @Test
    void aNonNumericHeaderIsAHardGrammarError() throws IOException {
        ParseResult<World> result = read("record-count:abc\nlevel:0:Town:None:Angband 1\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aMissingHeaderIsAHardErrorHereWhereCHasNone() throws IOException {
        ParseResult<World> result = read("level:0:Town:None:Angband 1\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    // ---------------------------------------------------------------- hard errors

    @Test
    void anOverflowingLevelNumberIsHardAndNamesTheLine() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevel:99999999999:Deep:None:None\n");

        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(anyError(result, "line 2", "Invalid level number", "99999999999"), result.errors()::toString);
    }

    @Test
    void aBadLevelNumberAbortsBeforeTheHeaderIsChecked() throws IOException {
        // The header over-declares AND the second depth overflows. The hard error aborts the parse
        // first, so the count mismatch is never reported and nothing loads.
        ParseResult<World> result = read(String.join("\n",
                "record-count:5",
                "level:0:Town:None:Angband 1",
                "level:99999999999:Deep:Angband 1:None",
                ""));

        assertTrue(result.items().isEmpty(), result.items()::toString);
        assertTrue(anyError(result, "line 3", "Invalid level number"), result.errors()::toString);
        assertFalse(anyError(result, "declares"), result.errors()::toString);
    }

    @Test
    void aLevelWithTooFewFieldsIsHardAsInC() throws IOException {
        // PARSE_ERROR_MISSING_FIELD in C, which run_parser turns into a quit.
        ParseResult<World> result = read("record-count:1\nlevel:0:Town:None\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void anUnknownDirectiveIsHardAsInC() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevl:0:Town:None:None\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aNonNumericDepthIsHardAsInC() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevel:zero:Town:None:None\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    // ---------------------------------------------------------------- places C is more lenient

    @Test
    void aHeaderWithNoLevelsIsHardHereWhereCLoadsAnEmptyWorld() throws IOException {
        ParseResult<World> result = read("record-count:0\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void anEmptyFileIsHardHereWhereCLoadsAnEmptyWorld() throws IOException {
        ParseResult<World> result = read("");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aFieldAfterTheDownNameIsHardHereWhereCIgnoresIt() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevel:0:Town:None:Angband 1:extra\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aHyphenInANameIsHardHereWhereCTakesAnySymExceptColon() throws IOException {
        ParseResult<World> result = read("record-count:1\nlevel:0:Hell-hole:None:None\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aHexadecimalDepthIsHardHereWhereCReadsBaseZero() throws IOException {
        // strtol(tok, &z, 0) reads "0x10" as 16.
        ParseResult<World> result = read("record-count:1\nlevel:0x10:Town:None:None\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }
}
