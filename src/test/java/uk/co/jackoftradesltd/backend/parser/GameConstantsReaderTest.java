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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.data.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reader/assembly tests for {@link GameConstantsReader} (grammar-suite reader-track R4).
 *
 * <p>Unlike the list readers, this one produces a single {@link GameConstantsData} carrier. As in C,
 * the carrier is not completeness- or duplicate-checked. {@code init_parse_constants()} in
 * {@code init.c} {@code mem_zalloc}s the struct, so a constant no line sets reads {@code 0}, and each
 * {@code parse_constants_*()} handler simply assigns its field, so a later line for the same label
 * overwrites the earlier one with no error. {@link #missingConstantIsLeftAtZero} and
 * {@link #duplicateScalarConstantOverwritesEarlierValue} pin both.
 *
 * <p>The happy-path test runs against the real shipped {@code lib/gamedata/constants.txt}, and the
 * expected values in {@link #theShippedFileGivesEveryValueTheCFileHolds} are the ones in the C
 * original's {@code constants.txt}. Most of the error-path tests start from that same complete file
 * and inject defects on appended lines, so the only error is the one under test.
 *
 * <p>A bad line does not come back as a message in the result. {@code GameConstantsAssembler} throws
 * a {@link DataFileParseException} for it, as C's {@code parse_constants_*()} handlers return a
 * {@code PARSE_ERROR_*} and the load stops, so those tests use {@code assertThrows}. Only two faults
 * reach the result's error list: an O-combat level row with a {@code chance} of 0, and critical
 * cutoffs that do not strictly increase. A grammar error is the third kind and comes back as a
 * result with no data.
 *
 * <p>Class GameConstantsReaderTest coded before 261002, updated on 261002 once missing constants
 * defaulted to 0 and duplicates overwrote, as in C, and on 261025 once {@code GameConstantsAssembler}
 * was brought in line with the C handlers (sign rules, message lookup, extra fields, base 0 numbers).
 *
 * @author Rowan Crowther
 */
class GameConstantsReaderTest {

    /**
     * The real shipped data file, relative to the Gradle working directory (project root).
     */
    private static final String REAL_FILE = "lib/gamedata/constants.txt";

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

    /**
     * Reads the real data file, guaranteeing a trailing newline so an appended line lands on its
     * own line (and so {@link #lineCount} gives the index the appended line will occupy minus one).
     */
    private String realText() throws IOException {
        String base = Files.readString(Path.of(REAL_FILE));
        return base.endsWith("\n") ? base : base + "\n";
    }

    /**
     * The real data file with every line whose start matches {@code linePrefix} removed.
     */
    private String realTextWithout(String linePrefix) throws IOException {
        return Files.readAllLines(Path.of(REAL_FILE)).stream()
                .filter(line -> !line.startsWith(linePrefix))
                .collect(Collectors.joining("\n", "", "\n"));
    }

    /**
     * Number of newline-terminated lines in {@code text}; a line appended after it sits at
     * {@code lineCount(text) + 1}.
     */
    private int lineCount(String text) {
        return (int) text.chars().filter(c -> c == '\n').count();
    }

    /**
     * Loads {@code content} and returns the data, failing the test if the load reports any error.
     */
    private GameConstantsData load(String content) throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(tempFile("load.txt", content));

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        assertNotNull(result.getData());
        return result.getData();
    }

    /**
     * Loads {@code content}, expects the assembler to refuse it with a {@link DataFileParseException}
     * and returns that exception's message.
     */
    private String refusal(String content) throws IOException {
        String path = tempFile("refused.txt", content);

        return assertThrows(DataFileParseException.class, () -> new GameConstantsReader().parseWithResults(path))
                .getMessage();
    }

    /**
     * Loads {@code content}, expects it to come back with errors in the result and no data, and
     * returns the errors.
     */
    private List<String> softErrors(String content) throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(tempFile("soft.txt", content));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        return result.getErrors();
    }

    @Test
    void cleanLoadReportsNoErrorsAndPopulatesTheCarrier() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());

        GameConstantsData data = result.getData();
        assertNotNull(data);

        // A couple of scalar spot-checks straight from constants.txt.
        assertEquals(1024, data.levelMax().monsters());
        assertEquals(128, data.world().maxDepth());

        // The four critical-level lists keep file order and the counts shipped in the file.
        assertEquals(5, data.meleeCriticalLevel().size());
        assertEquals(3, data.rangedCriticalLevel().size());
        assertEquals(5, data.oMeleeCriticalLevel().size());
        assertEquals(3, data.oRangedCriticalLevel().size());
    }

    /**
     * The first bad line ends the load. Two defects are appended, a non-integer value and then an
     * unknown category, and only the first is reported, with its own line, because the assembler
     * throws at once. C does the same: a {@code PARSE_ERROR_*} from a handler stops the parse.
     *
     * <p>Function aBadLineEndsTheLoadAtOnce coded on 261025, commented in full on 261025. It replaces
     * twoDistinctErrorsAreBothReportedWithTheirLines, which expected every error to be collected.
     */
    @Test
    void aBadLineEndsTheLoadAtOnce() throws IOException {
        String base = realText();
        int badIntLine = lineCount(base) + 1;

        String message = refusal(base + "world:max-depth:notanumber\n" + "nonsense:foo:1\n");

        assertTrue(message.contains("line: " + badIntLine + " "), message);
        assertTrue(message.contains("invalid integer value: notanumber"), message);
        assertFalse(message.contains("unknown category"), message);
    }

    /**
     * The two faults that are reported through the result are both collected, each tagged by the
     * builder's own wording, and the result has no data. A {@code chance} of 0 on an O-combat melee
     * row and on an O-combat ranged row are each C's {@code INVALID_VALUE}, but here the rest of the
     * file is still read.
     *
     * <p>Function softErrorsAreCollectedTogetherAndGiveNoData coded on 261025, commented in full on
     * 261025.
     */
    @Test
    void softErrorsAreCollectedTogetherAndGiveNoData() throws IOException {
        List<String> errors = softErrors(realText()
                + "o-melee-critical-level:0:1:HIT_GOOD\n" + "o-ranged-critical-level:0:1:HIT_GOOD\n");

        assertEquals(2, errors.size(), errors::toString);
        assertTrue(errors.stream().anyMatch(e -> e.contains("oMelee") && e.contains("chance")), errors::toString);
        assertTrue(errors.stream().anyMatch(e -> e.contains("oRanged") && e.contains("chance")), errors::toString);
    }

    /**
     * Removing {@code world:max-depth} is not an error. The load is clean and the constant reads
     * {@code 0}, as it would in C's zeroed {@code struct angband_constants}. The rest of the file is
     * unaffected.
     *
     * <p>Function missingConstantIsLeftAtZero coded on 261002, commented in full on 261002.
     */
    @Test
    void missingConstantIsLeftAtZero() throws IOException {
        String path = tempFile("missing.txt", realTextWithout("world:max-depth:"));

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());

        GameConstantsData data = result.getData();
        assertNotNull(data);
        assertEquals(0, data.world().maxDepth());
        assertEquals(1024, data.levelMax().monsters());
    }

    /**
     * A second {@code world:max-depth} line is not an error. The later value wins, because C's
     * {@code parse_constants_world()} simply assigns {@code z->max_depth} again.
     *
     * <p>Function duplicateScalarConstantOverwritesEarlierValue coded on 261002, commented in full on
     * 261002.
     */
    @Test
    void duplicateScalarConstantOverwritesEarlierValue() throws IOException {
        String path = tempFile("duplicate.txt", realText() + "world:max-depth:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());

        GameConstantsData data = result.getData();
        assertNotNull(data);
        assertEquals(1, data.world().maxDepth());
    }

    /**
     * A value that is not a number ends the load, naming the line. C's {@code strtol} reads no digits
     * and the parser returns {@code PARSE_ERROR_NOT_NUMBER}. {@code notanumber} is a legal
     * {@code GC_NAME}, so it gets as far as the number reader.
     *
     * <p>Function nonIntegerValueIsReportedWithItsLine coded before 261002, rewritten on 261025 for
     * the throw.
     */
    @Test
    void nonIntegerValueIsReportedWithItsLine() throws IOException {
        String base = realText();
        int badLine = lineCount(base) + 1;

        String message = refusal(base + "world:max-depth:notanumber\n");

        assertTrue(message.contains("line: " + badLine + " "), message);
        assertTrue(message.contains("invalid integer value: notanumber"), message);
    }

    /**
     * {@code melee-critical-level} needs four fields (cutoff, mult, add and the message). With three
     * the load ends, naming the line. C's parser finds no value for {@code msg} and returns
     * {@code PARSE_ERROR_MISSING_FIELD}.
     *
     * <p>Function wrongArityCriticalLevelLineIsReportedWithItsLine coded before 261002, rewritten on
     * 261025 for the throw.
     */
    @Test
    void wrongArityCriticalLevelLineIsReportedWithItsLine() throws IOException {
        String base = realText();
        int badLine = lineCount(base) + 1;

        String message = refusal(base + "melee-critical-level:400:2:5\n");

        assertTrue(message.contains("line: " + badLine + " "), message);
        assertTrue(message.contains("melee critical fields. Expected 4 got 3"), message);
    }

    /**
     * An empty file is clean and every constant reads {@code 0}, with empty critical-level lists. C
     * {@code mem_zalloc}s {@code struct angband_constants} in {@code init_parse_constants()} and has no
     * header or completeness check, so an empty {@code constants.txt} loads.
     *
     * <p>Function anEmptyFileLoadsAllZeroes coded on 261019, commented in full on 261019.
     */
    @Test
    void anEmptyFileLoadsAllZeroes() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(tempFile("empty.txt", ""));

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        GameConstantsData data = result.getData();
        assertNotNull(data);
        assertEquals(0, data.levelMax().monsters());
        assertEquals(0, data.world().maxDepth());
        assertTrue(data.meleeCriticalLevel().isEmpty());
        assertTrue(data.rangedCriticalLevel().isEmpty());
        assertTrue(data.oMeleeCriticalLevel().isEmpty());
        assertTrue(data.oRangedCriticalLevel().isEmpty());
    }

    /**
     * A file of only comments and blank lines is the same as an empty one. C's {@code parse_file()}
     * skips both.
     *
     * <p>Function aCommentOnlyFileLoadsAllZeroes coded on 261019, commented in full on 261019.
     */
    @Test
    void aCommentOnlyFileLoadsAllZeroes() throws IOException {
        String path = tempFile("comments.txt", "# File: constants.txt\n\n# nothing here\n\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().levelMax().monsters());
    }

    /**
     * A file that does not exist throws {@link IOException} from both entry points. C's
     * {@code parse_file_quit_not_found()} quits.
     *
     * <p>Function aMissingFileThrowsFromBothEntryPoints coded on 261019, commented in full on 261019.
     */
    @Test
    void aMissingFileThrowsFromBothEntryPoints() {
        String path = tempDir.resolve("no-such-file.txt").toString();

        assertThrows(IOException.class, () -> new GameConstantsReader().parseWithResults(path));
        assertThrows(IOException.class, () -> new GameConstantsReader().parse(path));
    }

    /**
     * {@code parse} is the same call as {@code parseWithResults}: same data, same errors.
     *
     * <p>Function parseGivesTheSameResultAsParseWithResults coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void parseGivesTheSameResultAsParseWithResults() throws IOException {
        GameConstantsParseResult viaParse = new GameConstantsReader().parse(REAL_FILE);
        GameConstantsParseResult viaResults = new GameConstantsReader().parseWithResults(REAL_FILE);

        assertFalse(viaParse.hasErrors(), () -> viaParse.getErrors().toString());
        assertEquals(viaResults.getData(), viaParse.getData());
        assertEquals(viaResults.getErrors(), viaParse.getErrors());

        String bad = tempFile("bad.txt", "Level-Max:monsters:1\n");
        GameConstantsParseResult badParse = new GameConstantsReader().parse(bad);
        GameConstantsParseResult badResults = new GameConstantsReader().parseWithResults(bad);
        assertNull(badParse.getData());
        assertEquals(badResults.getErrors(), badParse.getErrors());
    }

    /**
     * A grammar error is hard: no data, the lexer/parser message with the file and line, and nothing
     * from the assembler. An upper-case category cannot be a {@code GC_NAME}, so it is a syntax error
     * here, where C would report an undefined directive. Either way the file is refused.
     *
     * <p>Function aGrammarErrorGivesNoDataAndTheLexerMessage coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void aGrammarErrorGivesNoDataAndTheLexerMessage() throws IOException {
        String path = tempFile("hard.txt", "level-max:monsters:1024\nLEVEL-MAX:monsters:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertTrue(result.getErrors().stream().allMatch(e -> e.contains("line 2")), result.getErrors()::toString);
    }

    /**
     * Soft and hard errors do not mix: a hard error stops before the assembler runs, so a bad
     * category on an earlier line is not reported alongside it.
     *
     * <p>Function aHardErrorStopsTheAssemblerBeforeItReportsSoftOnes coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aHardErrorStopsTheAssemblerBeforeItReportsSoftOnes() throws IOException {
        String path = tempFile("mixed.txt", "nonsense:foo:1\nLEVEL-MAX:monsters:1\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertNull(result.getData());
        assertFalse(result.getErrors().isEmpty());
        assertTrue(result.getErrors().stream().noneMatch(e -> e.contains("unknown category")),
                result.getErrors()::toString);
    }

    /**
     * Melee cutoffs that do not strictly increase refuse the file, as C's
     * {@code finish_parse_constants()} does with {@code PARSE_ERROR_NON_SEQUENTIAL_RECORDS}. C compares
     * every cutoff but the last against the one before: 10, 5, 20 fails on the 5.
     *
     * <p>Function nonIncreasingMeleeCutoffsRefuseTheFile coded on 261019, commented in full on 261019.
     */
    @Test
    void nonIncreasingMeleeCutoffsRefuseTheFile() throws IOException {
        String path = tempFile("melee-order.txt",
                "melee-critical-level:10:2:5:HIT_GOOD\n"
                        + "melee-critical-level:5:3:10:HIT_GREAT\n"
                        + "melee-critical-level:20:4:15:HIT_SUPERB\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertEquals(1, result.getErrors().size(), result.getErrors()::toString);
        assertTrue(result.getErrors().get(0).toLowerCase().contains("melee"), result.getErrors()::toString);
    }

    /**
     * The same for ranged cutoffs, which C checks second, after the melee list.
     *
     * <p>Function nonIncreasingRangedCutoffsRefuseTheFile coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void nonIncreasingRangedCutoffsRefuseTheFile() throws IOException {
        String path = tempFile("ranged-order.txt",
                "ranged-critical-level:10:2:5:HIT_GOOD\n"
                        + "ranged-critical-level:10:3:10:HIT_GREAT\n"
                        + "ranged-critical-level:20:4:15:HIT_SUPERB\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertNull(result.getData());
        assertEquals(1, result.getErrors().size(), result.getErrors()::toString);
        assertTrue(result.getErrors().get(0).toLowerCase().contains("ranged"), result.getErrors()::toString);
    }

    /**
     * The last cutoff is never compared, and a list of one or two rows always passes. C's
     * {@code check_critical_levels()} skips the final row, so 10, 20, 1 loads, and so does 10, 5.
     *
     * <p>Function theLastCutoffIsNotComparedAndShortListsPass coded on 261019, commented in full on
     * 261019.
     */
    @Test
    void theLastCutoffIsNotComparedAndShortListsPass() throws IOException {
        String path = tempFile("last-cutoff.txt",
                "melee-critical-level:10:2:5:HIT_GOOD\n"
                        + "melee-critical-level:20:3:10:HIT_GREAT\n"
                        + "melee-critical-level:1:4:15:HIT_SUPERB\n"
                        + "ranged-critical-level:10:2:5:HIT_GOOD\n"
                        + "ranged-critical-level:5:3:10:HIT_GREAT\n");

        GameConstantsParseResult result = new GameConstantsReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.getErrors().toString());
        assertEquals(3, result.getData().meleeCriticalLevel().size());
        assertEquals(2, result.getData().rangedCriticalLevel().size());
    }

    /**
     * An unknown message name refuses the file. C's {@code message_lookup_by_name()} returns -1 and the
     * handler returns {@code PARSE_ERROR_INVALID_MESSAGE}.
     *
     * <p>Function anUnknownMessageNameRefusesTheFile coded on 261019, commented in full on 261019,
     * rewritten on 261025 for the throw.
     */
    @Test
    void anUnknownMessageNameRefusesTheFile() throws IOException {
        String message = refusal("melee-critical-level:10:2:5:NOT_A_MESSAGE\n");

        assertTrue(message.contains("line: 1 "), message);
        assertTrue(message.contains("illegal message type value"), message);
    }

    /**
     * A field after the value is ignored, as in C. The line parser reads only the fields the
     * directive registers, so {@code world:max-depth:5:6} loads with 5 and the 6 is never looked at.
     * The extra field does not even have to be a number.
     *
     * <p>Function aFieldAfterTheValueIsIgnored coded on 261025, commented in full on 261025. It
     * replaces aFieldAfterTheValueIsRefusedHereWhereCIgnoresIt.
     */
    @Test
    void aFieldAfterTheValueIsIgnored() throws IOException {
        assertEquals(5, load("world:max-depth:5:6\n").world().maxDepth());
        assertEquals(5, load("world:max-depth:5:notanumber\n").world().maxDepth());
        assertEquals(500, load("mon-gen:chance:500:7\n").monGen().chance());
        assertEquals(-60, load("melee-critical:chance-offset:-60:7\n").meleeCritical().chanceOffset());
    }

    /**
     * A hexadecimal value is hard here. C reads an {@code int} with {@code strtol(tok, &z, 0)}, so
     * {@code 0x10} is 16.
     *
     * <p>Function aHexadecimalValueIsRefusedHereWhereCReadsBaseZero coded on 261019, commented in full
     * on 261019.
     */
    @Test
    void aHexadecimalValueIsRefusedHereWhereCReadsBaseZero() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("hex.txt", "world:max-depth:0x10\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A leading zero is read as octal, as C's base-zero {@code strtol} reads it: {@code 010} is 8,
     * and {@code 0} on its own is 0. {@code 08} has no octal digit after the {@code 0}, so
     * {@code strtol} reads the {@code 0} and stops, giving 0.
     *
     * <p>Function aLeadingZeroValueIsReadAsOctalAsInC coded on 261025, commented in full on 261025.
     * It replaces aLeadingZeroValueIsReadAsDecimalWhereCReadsItAsOctal.
     */
    @Test
    void aLeadingZeroValueIsReadAsOctalAsInC() throws IOException {
        assertEquals(8, load("world:max-depth:010\n").world().maxDepth());
        assertEquals(0, load("world:max-depth:0\n").world().maxDepth());
        assertEquals(0, load("world:max-depth:08\n").world().maxDepth());
        assertEquals(10, load("world:max-depth:10\n").world().maxDepth());
    }

    /**
     * An indented line is hard here. C's {@code parse_file()} skips leading whitespace before the
     * directive, so {@code "  world:max-depth:7"} loads.
     *
     * <p>Function anIndentedLineIsRefusedHereWhereCSkipsTheIndent coded on 261019, commented in full
     * on 261019.
     */
    @Test
    void anIndentedLineIsRefusedHereWhereCSkipsTheIndent() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("indent.txt", "  world:max-depth:7\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A lower-case message name is hard here. C's {@code message_lookup_by_name()} compares with
     * {@code my_stricmp()}, so {@code hit_good} resolves.
     *
     * <p>Function aLowerCaseMessageNameIsRefusedHereWhereCIsCaseBlind coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aLowerCaseMessageNameIsRefusedHereWhereCIsCaseBlind() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("lower-msg.txt", "melee-critical-level:10:2:5:hit_good\n"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * A value too big for an {@code int} is clamped to {@code Integer.MAX_VALUE} and loads. C reads it
     * with {@code strtol} and keeps the low 32 bits without complaint, so the two differ on the value
     * but neither refuses the line. The clamp is the {@code ParseUtils} behaviour, see its Javadoc.
     *
     * <p>Function anOverflowingValueIsClampedWhereCTruncatesIt coded on 261025, commented in full on
     * 261025. It replaces anOverflowingValueIsRefusedHereWhereCTruncatesIt.
     */
    @Test
    void anOverflowingValueIsClampedWhereCTruncatesIt() throws IOException {
        assertEquals(Integer.MAX_VALUE, load("world:max-depth:99999999999\n").world().maxDepth());
    }

    /**
     * A comment on the last line with no newline after it is hard here: the lexer's {@code COMMENT}
     * rule needs the {@code \n}. C skips it.
     *
     * <p>Function aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt coded on 261019, commented in
     * full on 261019.
     */
    @Test
    void aFinalCommentWithoutANewlineIsRefusedHereWhereCSkipsIt() throws IOException {
        GameConstantsParseResult result = new GameConstantsReader()
                .parseWithResults(tempFile("tail-comment.txt", "world:max-depth:7\n# the end"));

        assertTrue(result.hasErrors());
        assertNull(result.getData());
    }

    /**
     * Builds one {@code category:label:value} line per label, counting up from {@code start}.
     */
    private static String block(String category, int start, String... labels) {
        StringBuilder text = new StringBuilder();

        for (int index = 0; index < labels.length; index++) {
            text.append(category).append(':').append(labels[index]).append(':').append(start + index).append('\n');
        }
        return text.toString();
    }

    /**
     * The message type of the only row a level line produced, from whichever of the four lists the
     * line's category fills.
     */
    private static MessageType messageOf(String levelCategory, GameConstantsData data) {
        return switch (levelCategory) {
            case "melee-critical-level" -> data.meleeCriticalLevel().getFirst().messageType();
            case "ranged-critical-level" -> data.rangedCriticalLevel().getFirst().messageType();
            case "o-melee-critical-level" -> data.oMeleeCriticalLevel().getFirst().messageType();
            case "o-ranged-critical-level" -> data.oRangedCriticalLevel().getFirst().messageType();
            default -> throw new IllegalArgumentException(levelCategory);
        };
    }

    /**
     * The shipped file loads and every value in it is the one the C original's {@code constants.txt}
     * holds, in file order for the four level lists. Includes the values that depend on C having no
     * sign check on the critical handlers: {@code chance-offset:-60} and the {@code -1} cutoff that
     * ends the melee and ranged lists.
     *
     * <p>Function theShippedFileGivesEveryValueTheCFileHolds coded on 261025, commented in full on
     * 261025.
     */
    @Test
    void theShippedFileGivesEveryValueTheCFileHolds() throws IOException {
        GameConstantsData data = load(realText());

        assertEquals(new LevelMaxData(1024), data.levelMax());
        assertEquals(new MonGenData(500, 14, 4, 8, 100, 25, 10, 25, 5), data.monGen());
        assertEquals(new MonPlayData(550, 8, 2, 5, 5), data.monPlay());
        assertEquals(new DunGenData(100, 200, 500, 900, 9, 3, 3, 2), data.dunGen());
        assertEquals(new WorldData(128, 10000, 66, 198, 22, 66, 100, 10, 1, 100), data.world());
        assertEquals(new CarryCapData(23, 10, 40, 5, 23), data.carryCap());
        assertEquals(new StoreData(24, 1000, 25, 5), data.store());
        assertEquals(new ObjMakeData(100, 20, 20, 5000, 15000, 7500), data.objMake());
        assertEquals(new PlayerData(20, 20, 600, 100), data.player());
        assertEquals(new MeleeCriticalData(10, 1, 5, 0, 1, -60, 5000, 1, 650), data.meleeCritical());
        assertEquals(new RangedCriticalData(10, 1, 4, 2, 0, 0, 0, 5000, 1, 500), data.rangedCritical());
        assertEquals(new OMeleeCriticalData(10, 1, 3, 1, 1, 240), data.oMeleeCritical());
        assertEquals(new ORangedCriticalData(10, 1, 1, 3, 2, 1, 1, 360), data.oRangedCritical());

        assertEquals(List.of(
                new MeleeCriticalLevelData(400, 2, 5, MessageType.MSG_HIT_GOOD),
                new MeleeCriticalLevelData(700, 2, 10, MessageType.MSG_HIT_GREAT),
                new MeleeCriticalLevelData(900, 3, 15, MessageType.MSG_HIT_SUPERB),
                new MeleeCriticalLevelData(1300, 3, 20, MessageType.MSG_HIT_HI_GREAT),
                new MeleeCriticalLevelData(-1, 4, 20, MessageType.MSG_HIT_HI_SUPERB)), data.meleeCriticalLevel());
        assertEquals(List.of(
                new RangedCriticalLevelData(500, 2, 5, MessageType.MSG_HIT_GOOD),
                new RangedCriticalLevelData(1000, 2, 10, MessageType.MSG_HIT_GREAT),
                new RangedCriticalLevelData(-1, 3, 15, MessageType.MSG_HIT_SUPERB)), data.rangedCriticalLevel());
        assertEquals(List.of(
                new OMeleeCriticalLevelData(40, 5, MessageType.MSG_HIT_HI_SUPERB),
                new OMeleeCriticalLevelData(12, 4, MessageType.MSG_HIT_HI_GREAT),
                new OMeleeCriticalLevelData(3, 3, MessageType.MSG_HIT_SUPERB),
                new OMeleeCriticalLevelData(2, 2, MessageType.MSG_HIT_GREAT),
                new OMeleeCriticalLevelData(1, 1, MessageType.MSG_HIT_GOOD)), data.oMeleeCriticalLevel());
        assertEquals(List.of(
                new ORangedCriticalLevelData(50, 3, MessageType.MSG_HIT_SUPERB),
                new ORangedCriticalLevelData(10, 2, MessageType.MSG_HIT_GREAT),
                new ORangedCriticalLevelData(1, 1, MessageType.MSG_HIT_GOOD)), data.oRangedCriticalLevel());
    }

    /**
     * Each label of each scalar category sets its own field and no other. Every label is given a
     * different number, so a label wired to the wrong setter, or two wired to the same one, shows up
     * as a record that is not the expected one. The order of the labels in each directive is the
     * order {@code init.c} tests them in.
     *
     * <p>Function everyLabelSetsItsOwnField coded on 261025, commented in full on 261025.
     */
    @Test
    void everyLabelSetsItsOwnField() throws IOException {
        String text = block("level-max", 1, "monsters")
                + block("mon-gen", 10, "chance", "level-min", "town-day", "town-night", "repro-max", "ood-chance",
                "ood-amount", "group-max", "group-dist")
                + block("mon-play", 20, "break-glyph", "mult-rate", "life-drain", "flee-range", "turn-range")
                + block("dun-gen", 30, "cent-max", "door-max", "wall-max", "tunn-max", "amt-room", "amt-item",
                "amt-gold", "pit-max")
                + block("world", 40, "max-depth", "day-length", "dungeon-hgt", "dungeon-wid", "town-hgt",
                "town-wid", "feeling-total", "feeling-need", "stair-skip", "move-energy")
                + block("carry-cap", 50, "pack-size", "quiver-size", "quiver-slot-size", "thrown-quiver-mult",
                "floor-size")
                + block("store", 60, "inven-max", "turns", "shuffle", "magic-level")
                + block("obj-make", 70, "max-depth", "great-obj", "great-ego", "fuel-torch", "fuel-lamp",
                "default-lamp")
                + block("player", 80, "max-sight", "max-range", "start-gold", "food-value")
                + block("melee-critical", 90, "debuff-toh", "chance-weight-scale", "chance-toh-scale",
                "chance-level-scale", "chance-toh-skill-scale", "chance-offset", "chance-range",
                "power-weight-scale", "power-random")
                + block("ranged-critical", 100, "debuff-toh", "chance-weight-scale", "chance-toh-scale",
                "chance-level-scale", "chance-launched-toh-skill-scale", "chance-thrown-toh-skill-scale",
                "chance-offset", "chance-range", "power-weight-scale", "power-random")
                + block("o-melee-critical", 110, "debuff-toh", "power-toh-scale-numerator",
                "power-toh-scale-denominator", "chance-power-scale-numerator", "chance-power-scale-denominator",
                "chance-add-denominator")
                + block("o-ranged-critical", 120, "debuff-toh", "power-launched-toh-scale-numerator",
                "power-launched-toh-scale-denominator", "power-thrown-toh-scale-numerator",
                "power-thrown-toh-scale-denominator", "chance-power-scale-numerator",
                "chance-power-scale-denominator", "chance-add-denominator");

        GameConstantsData data = load(text);

        assertEquals(new LevelMaxData(1), data.levelMax());
        assertEquals(new MonGenData(10, 11, 12, 13, 14, 15, 16, 17, 18), data.monGen());
        assertEquals(new MonPlayData(20, 21, 22, 23, 24), data.monPlay());
        assertEquals(new DunGenData(30, 31, 32, 33, 34, 35, 36, 37), data.dunGen());
        assertEquals(new WorldData(40, 41, 42, 43, 44, 45, 46, 47, 48, 49), data.world());
        assertEquals(new CarryCapData(50, 51, 52, 53, 54), data.carryCap());
        assertEquals(new StoreData(60, 61, 62, 63), data.store());
        assertEquals(new ObjMakeData(70, 71, 72, 73, 74, 75), data.objMake());
        assertEquals(new PlayerData(80, 81, 82, 83), data.player());
        assertEquals(new MeleeCriticalData(90, 91, 92, 93, 94, 95, 96, 97, 98), data.meleeCritical());
        assertEquals(new RangedCriticalData(100, 101, 102, 103, 104, 105, 106, 107, 108, 109), data.rangedCritical());
        assertEquals(new OMeleeCriticalData(110, 111, 112, 113, 114, 115), data.oMeleeCritical());
        assertEquals(new ORangedCriticalData(120, 121, 122, 123, 124, 125, 126, 127), data.oRangedCritical());
    }

    /**
     * A negative value is refused in each of the nine plain categories, as C's handlers return
     * {@code PARSE_ERROR_INVALID_VALUE} when {@code value < 0}. The line is named.
     *
     * <p>Function aNegativeValueIsRefusedInTheNinePlainCategories coded on 261025, commented in full on
     * 261025. It replaces the first half of aNegativeConstantLoadsHereWhereCRefusesIt.
     */
    @ParameterizedTest
    @ValueSource(strings = {"level-max:monsters", "mon-gen:chance", "mon-play:flee-range", "dun-gen:pit-max",
            "world:max-depth", "carry-cap:floor-size", "store:turns", "obj-make:great-obj", "player:start-gold"})
    void aNegativeValueIsRefusedInTheNinePlainCategories(String categoryAndLabel) throws IOException {
        String message = refusal(categoryAndLabel + ":-1\n");

        assertTrue(message.contains("line: 1 "), message);
        assertTrue(message.contains("-1"), message);
    }

    /**
     * {@code -0} is not negative to {@code value < 0}, so it loads as 0 in every plain category, as in
     * C. Zero itself and the largest {@code int} load too.
     *
     * <p>Function negativeZeroAndZeroLoadInThePlainCategories coded on 261025, commented in full on
     * 261025.
     */
    @Test
    void negativeZeroAndZeroLoadInThePlainCategories() throws IOException {
        assertEquals(0, load("world:max-depth:-0\n").world().maxDepth());
        assertEquals(0, load("level-max:monsters:-0\n").levelMax().monsters());
        assertEquals(0, load("player:start-gold:0\n").player().startGold());
        assertEquals(Integer.MAX_VALUE, load("player:start-gold:2147483647\n").player().startGold());
    }

    /**
     * The four critical scalar categories have no sign check in C, so a negative value loads. The
     * shipped file needs {@code melee-critical:chance-offset:-60}.
     *
     * <p>Function aNegativeValueLoadsInTheFourCriticalCategories coded on 261025, commented in full
     * on 261025. It replaces the second half of aNegativeConstantLoadsHereWhereCRefusesIt.
     */
    @Test
    void aNegativeValueLoadsInTheFourCriticalCategories() throws IOException {
        assertEquals(-60, load("melee-critical:chance-offset:-60\n").meleeCritical().chanceOffset());
        assertEquals(-1, load("ranged-critical:chance-offset:-1\n").rangedCritical().chanceOffset());
        assertEquals(-1, load("o-melee-critical:debuff-toh:-1\n").oMeleeCritical().debuffToh());
        assertEquals(-1, load("o-ranged-critical:debuff-toh:-1\n").oRangedCritical().debuffToh());
    }

    /**
     * A line with no value is refused in every scalar category, naming the line, as C's parser finds
     * no {@code value} field and returns {@code PARSE_ERROR_MISSING_FIELD}. It is a
     * {@link DataFileParseException}, not an index error from reading the missing field.
     *
     * <p>Function aLineWithNoValueIsRefusedInEveryScalarCategory coded on 261025, commented in full on
     * 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"level-max:monsters", "mon-gen:chance", "mon-play:flee-range", "dun-gen:pit-max",
            "world:max-depth", "carry-cap:floor-size", "store:turns", "obj-make:great-obj", "player:start-gold",
            "melee-critical:chance-offset", "ranged-critical:chance-offset", "o-melee-critical:debuff-toh",
            "o-ranged-critical:debuff-toh"})
    void aLineWithNoValueIsRefusedInEveryScalarCategory(String categoryAndLabel) throws IOException {
        String message = refusal(categoryAndLabel + "\n");

        assertTrue(message.contains("line: 1 "), message);
        assertTrue(message.contains("illegal number of"), message);
    }

    /**
     * A level line short of its fields is refused, naming the line: three for the O-combat lines,
     * four for the standard ones.
     *
     * <p>Function aLevelLineShortOfItsFieldsIsRefused coded on 261025, commented in full on 261025.
     */
    @Test
    void aLevelLineShortOfItsFieldsIsRefused() throws IOException {
        for (String line : new String[]{"melee-critical-level:10:2:5", "ranged-critical-level:10:2:5",
                "o-melee-critical-level:1:1", "o-ranged-critical-level:1:1"}) {
            String message = refusal(line + "\n");

            assertTrue(message.contains("line: 1 "), message);
            assertTrue(message.contains("illegal number of"), message);
        }
    }

    /**
     * An unknown label is refused in every scalar category, naming the label, as C's handlers return
     * {@code PARSE_ERROR_UNDEFINED_DIRECTIVE}. A label that is valid in a neighbouring category is
     * still unknown here.
     *
     * <p>Function anUnknownLabelIsRefusedInEveryScalarCategory coded on 261025, commented in full on
     * 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"level-max", "mon-gen", "mon-play", "dun-gen", "world", "carry-cap", "store",
            "obj-make", "player", "melee-critical", "ranged-critical", "o-melee-critical", "o-ranged-critical"})
    void anUnknownLabelIsRefusedInEveryScalarCategory(String category) throws IOException {
        String message = refusal(category + ":no-such-label:1\n");

        assertTrue(message.contains("line: 1 "), message);
        assertTrue(message.contains("no-such-label"), message);
    }

    /**
     * Labels are matched exactly. {@code chance-launched-toh-skill-scale} exists on a ranged critical
     * line only, so it is unknown on a melee one.
     *
     * <p>Function aLabelFromTheNeighbouringCategoryIsUnknown coded on 261025, commented in full on
     * 261025.
     */
    @Test
    void aLabelFromTheNeighbouringCategoryIsUnknown() throws IOException {
        String message = refusal("melee-critical:chance-launched-toh-skill-scale:1\n");

        assertTrue(message.contains("illegal melee-critical value: chance-launched-toh-skill-scale"), message);
    }

    /**
     * An unknown category is refused, naming it, as C's {@code parser_parse()} finds no hook and returns
     * {@code PARSE_ERROR_UNDEFINED_DIRECTIVE}.
     *
     * <p>Function anUnknownCategoryIsRefused coded on 261025, commented in full on 261025.
     */
    @Test
    void anUnknownCategoryIsRefused() throws IOException {
        String message = refusal("nonsense:foo:1\n");

        assertTrue(message.contains("line: 1 "), message);
        assertTrue(message.contains("unknown category: nonsense"), message);
    }

    /**
     * The three numbers of a standard level line are signed and unchecked, as in C, so negatives load
     * on every one of them and the {@code -1} cutoff sentinel loads on its own.
     *
     * <p>Function theNumbersOfAStandardLevelLineMayBeNegative coded on 261025, commented in full on
     * 261025.
     */
    @Test
    void theNumbersOfAStandardLevelLineMayBeNegative() throws IOException {
        GameConstantsData data = load("melee-critical-level:-1:-2:-3:HIT_GOOD\n"
                + "ranged-critical-level:-4:-5:-6:HIT_GREAT\n");

        assertEquals(List.of(new MeleeCriticalLevelData(-1, -2, -3, MessageType.MSG_HIT_GOOD)),
                data.meleeCriticalLevel());
        assertEquals(List.of(new RangedCriticalLevelData(-4, -5, -6, MessageType.MSG_HIT_GREAT)),
                data.rangedCriticalLevel());
    }

    /**
     * A number that is not a number on a level line ends the load, naming the line.
     *
     * <p>Function aNonNumberOnALevelLineIsRefused coded on 261025, commented in full on 261025.
     */
    @Test
    void aNonNumberOnALevelLineIsRefused() throws IOException {
        String message = refusal("melee-critical-level:abc:2:5:HIT_GOOD\n");

        assertTrue(message.contains("line: 1 "), message);
        assertTrue(message.contains("invalid integer value: abc"), message);
    }

    /**
     * A number is a message name, as C's {@code message_lookup_by_name()} reads the digits first:
     * {@code 0} is {@code MSG_GENERIC}, {@code 1} is {@code MSG_BIRTH} and {@code 2} is
     * {@code MSG_HIT} in {@code list-message.h}. The same on all four level lines.
     *
     * <p>Function aMessageNumberIsAccepted coded on 261025, commented in full on 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"melee-critical-level:10:2:5", "ranged-critical-level:10:2:5",
            "o-melee-critical-level:1:1", "o-ranged-critical-level:1:1"})
    void aMessageNumberIsAccepted(String prefix) throws IOException {
        assertEquals(MessageType.MSG_GENERIC, messageOf(prefix.substring(0, prefix.indexOf(":")),
                load(prefix + ":0\n")));
        assertEquals(MessageType.MSG_BIRTH, messageOf(prefix.substring(0, prefix.indexOf(":")),
                load(prefix + ":1\n")));
        assertEquals(MessageType.MSG_HIT, messageOf(prefix.substring(0, prefix.indexOf(":")),
                load(prefix + ":2\n")));
    }

    /**
     * A name is matched without regard to case, as C's {@code my_stricmp()} does, so {@code hit}
     * finds {@code MSG_HIT}, and {@code MAX} and {@code max} find the end marker as C's table does.
     * A name with an underscore cannot be lower case here because the lexer's {@code GC_NAME} has no
     * underscore, which {@link #aLowerCaseMessageNameIsRefusedHereWhereCIsCaseBlind} pins.
     *
     * <p>Function aMessageNameIsMatchedWithoutRegardToCase coded on 261025, commented in full on
     * 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"melee-critical-level:10:2:5", "ranged-critical-level:10:2:5",
            "o-melee-critical-level:1:1", "o-ranged-critical-level:1:1"})
    void aMessageNameIsMatchedWithoutRegardToCase(String prefix) throws IOException {
        String category = prefix.substring(0, prefix.indexOf(":"));

        assertEquals(MessageType.MSG_HIT, messageOf(category, load(prefix + ":hit\n")));
        assertEquals(MessageType.MSG_HIT, messageOf(category, load(prefix + ":HIT\n")));
        assertEquals(MessageType.MSG_MAX, messageOf(category, load(prefix + ":max\n")));
        assertEquals(MessageType.MSG_MAX, messageOf(category, load(prefix + ":MAX\n")));
    }

    /**
     * {@code NONE} is refused on all four level lines. {@code MessageType.MSG_NONE} is Java-only, and
     * C's table has no such name, so {@code message_lookup_by_name()} returns -1 and the handler
     * returns {@code PARSE_ERROR_INVALID_MESSAGE}. A number past the last message is refused for the
     * same reason.
     *
     * <p>Function theSentinelNoneAndANumberPastTheEndAreRefused coded on 261025, commented in full on
     * 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"melee-critical-level:10:2:5", "ranged-critical-level:10:2:5",
            "o-melee-critical-level:1:1", "o-ranged-critical-level:1:1"})
    void theSentinelNoneAndANumberPastTheEndAreRefused(String prefix) throws IOException {
        for (String message : new String[]{"NONE", "none", "153", "9999"}) {
            String refusal = refusal(prefix + ":" + message + "\n");

            assertTrue(refusal.contains("line: 1 "), refusal);
            assertTrue(refusal.contains("illegal message type value"), refusal);
        }
    }

    /**
     * The message is the rest of the line, as a {@code str} is in C's parser. A name followed by a
     * further field is therefore a different name and is refused, and so is a number followed by one:
     * {@code 12:3} is not message 123 and not message 12. The same on all four level lines.
     *
     * <p>Function theMessageIsTheRestOfTheLine coded on 261025, commented in full on 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"melee-critical-level:10:2:5", "ranged-critical-level:10:2:5",
            "o-melee-critical-level:1:1", "o-ranged-critical-level:1:1"})
    void theMessageIsTheRestOfTheLine(String prefix) throws IOException {
        for (String message : new String[]{"HIT_GOOD:extra", "HIT_GOOD:7", "12:3", "2:3"}) {
            String refusal = refusal(prefix + ":" + message + "\n");

            assertTrue(refusal.contains("line: 1 "), refusal);
            assertTrue(refusal.contains("illegal message type value"), refusal);
        }
    }

    /**
     * A {@code uint} field that starts with {@code -} is refused on the O-combat level lines, {@code -0}
     * included, because C's parser tests the first character of the text and not the value. A plain
     * negative number is refused for the same reason. This holds for both fields and both lines.
     *
     * <p>Function aMinusSignOnAnOCombatChanceOrDiceIsRefused coded on 261025, commented in full on
     * 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"o-melee-critical-level", "o-ranged-critical-level"})
    void aMinusSignOnAnOCombatChanceOrDiceIsRefused(String category) throws IOException {
        for (String fields : new String[]{"1:-0", "-0:1", "1:-1", "-1:1", "-0:-0"}) {
            String message = refusal(category + ":" + fields + ":HIT_GOOD\n");

            assertTrue(message.contains("line: 1 "), message);
        }
    }

    /**
     * On the O-combat level lines a {@code dice} of 0 loads, and a {@code chance} of 0 does not. C
     * refuses the 0 chance with {@code PARSE_ERROR_INVALID_VALUE}; here the builder reports it in
     * the result's error list, and the result has no data.
     *
     * <p>Function anOCombatChanceOfZeroIsReportedAndADiceOfZeroLoads coded on 261025, commented in
     * full on 261025.
     */
    @ParameterizedTest
    @ValueSource(strings = {"o-melee-critical-level", "o-ranged-critical-level"})
    void anOCombatChanceOfZeroIsReportedAndADiceOfZeroLoads(String category) throws IOException {
        GameConstantsData data = load(category + ":1:0:HIT_GOOD\n");

        assertEquals(MessageType.MSG_HIT_GOOD, messageOf(category, data));

        List<String> errors = softErrors(category + ":0:1:HIT_GOOD\n");

        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("chance"), errors::toString);
    }

    /**
     * Rows keep file order, and the same label on two lines keeps the later value. Three melee rows
     * come back in the order written, whatever their cutoffs.
     *
     * <p>Function levelRowsKeepFileOrder coded on 261025, commented in full on 261025.
     */
    @Test
    void levelRowsKeepFileOrder() throws IOException {
        GameConstantsData data = load("melee-critical-level:10:1:1:HIT_GOOD\n"
                + "melee-critical-level:20:2:2:HIT_GREAT\n"
                + "melee-critical-level:30:3:3:HIT_SUPERB\n");

        assertEquals(List.of(
                new MeleeCriticalLevelData(10, 1, 1, MessageType.MSG_HIT_GOOD),
                new MeleeCriticalLevelData(20, 2, 2, MessageType.MSG_HIT_GREAT),
                new MeleeCriticalLevelData(30, 3, 3, MessageType.MSG_HIT_SUPERB)), data.meleeCriticalLevel());
    }
}
