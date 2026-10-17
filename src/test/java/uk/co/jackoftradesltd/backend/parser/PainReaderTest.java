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
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link PainReader}: file text -> {@code PainLexer}/{@code
 * PainGrammar} -> {@code PainParseRecord} -> {@code PainAssembler} -> {@link MonsterPain}. Where
 * {@code PainGrammarTest} stops at the raw parse record, these pin the assembler half - the
 * {@code type:} serial being peeled back off the front of the message list and parsed, the
 * seven-message contract, and the skip-and-continue soft-error channel that {@code GrammarDriver}
 * threads through {@link ParseResult}.
 *
 * @author Rowan Crowther
 */
class PainReaderTest {

    private static final String REAL_FILE = "lib/gamedata/pain.txt";

    @TempDir
    Path tempDir;

    /**
     * Prefix a body with the {@code record-count:} header the {@code file} rule requires.
     */
    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    /**
     * Render one {@code type:}/{@code message:} block; the grammar demands exactly seven messages.
     */
    private static String entry(int type, String... sevenMessages) {
        StringBuilder sb = new StringBuilder("type:").append(type).append("\n");
        for (String m : sevenMessages) {
            sb.append("message:").append(m).append("\n");
        }
        return sb.toString();
    }

    private static final String[] SEVEN = {"m1", "m2", "m3", "m4", "m5", "m6", "m7"};

    private ParseResult<MonsterPain> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new PainReader().parseWithResults(file.toString());
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileYieldsTwelveMonsterPains() throws IOException {
        ParseResult<MonsterPain> result = new PainReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(12, result.items().size());
    }

    @Test
    void firstMonsterPainHasTheRightIndexAndGraduatedMessages() throws IOException {
        ParseResult<MonsterPain> result = new PainReader().parseWithResults(REAL_FILE);

        MonsterPain first = result.items().get(0);
        assertEquals(1, first.getPainIndex(), "type: serial peeled off and parsed as the index");
        // The type number must NOT leak into the messages; level 0 is the first real message.
        assertEquals("shrug[s] off the attack.", first.getMessage(0));
        assertEquals("cr[ies|y] out in pain.", first.getMessage(2), "alternation syntax survives");
        assertEquals("cr[ies|y] out feebly.", first.getMessage(6), "seventh/last message");
    }

    // ---- Assembler soft-error branches -----------------------------------

    @Test
    void overflowingTypeSerialIsReportedAndRecordSkipped() throws IOException {
        // type: is an INTEGER token, so the only route into the NumberFormatException branch is a
        // value too large for int. Build the block by hand so the oversized serial is raw text.
        String oversized = "type:99999999999999999999\n"
                + "message:m1\nmessage:m2\nmessage:m3\nmessage:m4\n"
                + "message:m5\nmessage:m6\nmessage:m7\n";
        ParseResult<MonsterPain> result = load("bad-type.txt", withHeader(1, oversized));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid type value")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsReportedButValidRecordsStillLoad() throws IOException {
        // Header over-declares (5 vs the single entry). Soft: the entry still assembles.
        ParseResult<MonsterPain> result = load("bad-count.txt", withHeader(5, entry(1, SEVEN)));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    @Test
    void twoWellFormedEntriesBothAssemble() throws IOException {
        ParseResult<MonsterPain> result =
                load("two.txt", withHeader(2, entry(1, SEVEN) + entry(2, SEVEN)));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals(1, result.items().get(0).getPainIndex());
        assertEquals(2, result.items().get(1).getPainIndex());
    }

    @Test
    void underDeclaredRecordCountIsReportedButAllRecordsLoad() throws IOException {
        // Complement of the over-declared case: header says 1, file has 2. Still soft.
        ParseResult<MonsterPain> result =
                load("under.txt", withHeader(1, entry(1, SEVEN) + entry(2, SEVEN)));

        assertEquals(2, result.items().size());
        assertTrue(result.errors().stream().anyMatch(
                        e -> e.contains("declares 1") && e.contains("contains 2")),
                result.errors()::toString);
    }

    @Test
    void negativeTypeSerialIsAcceptedWithoutValidation() throws IOException {
        // INTEGER allows a leading '-', and neither the grammar nor the assembler range-checks the
        // serial, so a negative index flows straight through. Pins current (unvalidated) behaviour.
        ParseResult<MonsterPain> result = load("neg.txt", withHeader(1, entry(-1, SEVEN)));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(-1, result.items().get(0).getPainIndex());
    }

    // ---- Hard (fail-closed) parse errors: empty result + errors ----------

    @Test
    void tooFewMessagesIsAHardErrorAndLoadsNothing() throws IOException {
        // painEntry demands exactly seven message: lines. Six then EOF is a syntax error; the hard
        // channel throws in extract(), so the assembler never runs and the result is empty-with-errors.
        String sixMsgs = "type:1\n"
                + "message:m1\nmessage:m2\nmessage:m3\nmessage:m4\nmessage:m5\nmessage:m6\n";
        ParseResult<MonsterPain> result = load("too-few.txt", withHeader(1, sixMsgs));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void tooManyMessagesIsAHardError() throws IOException {
        // An eighth message: line has nowhere to go - painEntry has closed and the next entry must
        // start with type: - so it is a syntax error.
        String eightMsgs = entry(1, SEVEN) + "message:extra\n";
        ParseResult<MonsterPain> result = load("too-many.txt", withHeader(1, eightMsgs));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        // file starts with recordCount; a bare type: block with no header cannot match it.
        ParseResult<MonsterPain> result = load("no-header.txt", entry(1, SEVEN));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void emptyMessageBodyIsAHardError() throws IOException {
        // "message:" with no text leaves the lexer in FREE_TEXT_MODE facing a newline it has no rule
        // for (STRING needs >=1 char), so it is a hard lexer error - not a silently empty message.
        String emptyFirst = "type:1\n"
                + "message:\nmessage:m2\nmessage:m3\nmessage:m4\nmessage:m5\nmessage:m6\nmessage:m7\n";
        ParseResult<MonsterPain> result = load("empty-msg.txt", withHeader(1, emptyFirst));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Contract of the reader itself (expected values from C's pain.txt and mon-init.c) ----

    @Test
    void recordsComeBackInFileOrderWithIndicesOneToTwelve() throws IOException {
        // Upstream lib/gamedata/pain.txt has twelve type: lines, 1 to 12 ascending. C's
        // finish_parse_pain files each into pain_messages[pain_idx], so reading the array from 1
        // upwards gives file order; here the list is base 0 (no blank slot 0), so index = position + 1.
        List<MonsterPain> items = new PainReader().parseWithResults(REAL_FILE).items();

        for (int i = 0; i < 12; i++) {
            assertEquals(i + 1, items.get(i).getPainIndex(), "record " + i);
        }
    }

    @Test
    void everyRecordOfTheRealFileCarriesSevenMessages() throws IOException {
        // Upstream pain.txt has 84 message: lines, 7 under each of the 12 types.
        for (MonsterPain pain : new PainReader().parseWithResults(REAL_FILE).items()) {
            for (int level = 0; level < 7; level++) {
                assertFalse(pain.getMessage(level).isEmpty(),
                        "type " + pain.getPainIndex() + " level " + level);
            }
        }
    }

    @Test
    void lastRecordAndAMiddleRecordMatchTheUpstreamText() throws IOException {
        // Messages copied from upstream lib/gamedata/pain.txt, types 12 and 7.
        List<MonsterPain> items = new PainReader().parseWithResults(REAL_FILE).items();

        MonsterPain seventh = items.get(6);
        assertEquals(7, seventh.getPainIndex());
        assertEquals("ignore[s] the attack.", seventh.getMessage(0));
        assertEquals("twitch[es] in pain.", seventh.getMessage(3));
        assertEquals("jerk[s] feebly.", seventh.getMessage(6));

        MonsterPain last = items.get(11);
        assertEquals(12, last.getPainIndex());
        assertEquals("laugh[s] off the attack.", last.getMessage(0));
        assertEquals("bellow[s] in rage.", last.getMessage(3));
        assertEquals("wince[s].", last.getMessage(6));
    }

    @Test
    void parseReturnsTheSameSetsAsParseWithResults() throws IOException {
        PainReader reader = new PainReader();

        List<Integer> viaParse = reader.parse(REAL_FILE).stream().map(MonsterPain::getPainIndex).toList();
        List<Integer> viaResults = reader.parseWithResults(REAL_FILE).items().stream()
                .map(MonsterPain::getPainIndex).toList();

        assertEquals(viaResults, viaParse);
        assertEquals(12, viaParse.size());
    }

    @Test
    void parseDropsErrorsSoASyntaxErrorLooksLikeAnEmptyList() throws IOException {
        Path file = tempDir.resolve("broken.txt");
        Files.writeString(file, withHeader(1, "type:1\nmessage:only one\n"));

        assertTrue(new PainReader().parse(file.toString()).isEmpty());
    }

    @Test
    void missingFileIsAnIOExceptionNotAnEmptyResult() {
        // C: parse_file_quit_not_found() quits with "Cannot open 'pain.txt'".
        String missing = tempDir.resolve("no_such_file.txt").toString();

        assertThrows(IOException.class, () -> new PainReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new PainReader().parse(missing));
    }

    @Test
    void messageBeforeAnyTypeLineIsAHardError() throws IOException {
        // C: parse_pain_message returns PARSE_ERROR_MISSING_RECORD_HEADER while the private pointer
        // is still NULL, and the init code quits on any parser error.
        ParseResult<MonsterPain> result =
                load("headless.txt", withHeader(1, "message:m1\n" + entry(1, SEVEN)));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void typeSerialsAreNotRequiredToAscendAndFileOrderIsKept() throws IOException {
        // pain.txt's header says serials "must increase", but parse_pain_type only reads a uint and
        // checks nothing, so a descending pair loads. Here the list stays in file order.
        ParseResult<MonsterPain> result =
                load("desc.txt", withHeader(2, entry(5, SEVEN) + entry(2, SEVEN)));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(List.of(5, 2), result.items().stream().map(MonsterPain::getPainIndex).toList());
    }

    @Test
    void repeatedTypeSerialKeepsBothRecordsInFileOrder() throws IOException {
        // C would file both into the same pain_messages slot, the earlier one in the file winning
        // (the parser list is newest-first, so the earlier record is copied last). The list here
        // keeps both; MonsterRegistry.lookupMonsterPain finds the earlier, so lookups agree with C.
        ParseResult<MonsterPain> result = load("dup.txt", withHeader(2,
                entry(3, "a1", "a2", "a3", "a4", "a5", "a6", "a7")
                        + entry(3, "b1", "b2", "b3", "b4", "b5", "b6", "b7")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(2, result.items().size());
        assertEquals("a1", result.items().get(0).getMessage(0));
        assertEquals("b1", result.items().get(1).getMessage(0));
    }

    @Test
    void messageTextRunsToTheEndOfTheLineColonsAndSpacesIncluded() throws IOException {
        // C declares the field "str message", and parser_parse hands a str the whole remainder of
        // the line (strtok with an empty delimiter set): colons are kept and nothing is trimmed.
        ParseResult<MonsterPain> result = load("colon.txt", withHeader(1,
                entry(1, "a: b", " lead", "x:y:z", "m4", "m5", "m6", "m7")));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        MonsterPain pain = result.items().get(0);
        assertEquals("a: b", pain.getMessage(0));
        assertEquals(" lead", pain.getMessage(1));
        assertEquals("x:y:z", pain.getMessage(2));
    }

    @Test
    void messagesComeBackInTheOrderTheLinesAppear() throws IOException {
        // C fills messages[0..6] in order; level 0 is the first message: line.
        ParseResult<MonsterPain> result = load("order.txt", withHeader(1,
                entry(4, "m1", "m2", "m3", "m4", "m5", "m6", "m7")));

        MonsterPain pain = result.items().get(0);
        for (int level = 0; level < 7; level++) {
            assertEquals("m" + (level + 1), pain.getMessage(level));
        }
    }

    @Test
    void indexZeroIsAcceptedAsARealRecord() throws IOException {
        // C reads the serial with parser_getuint, so 0 is legal there (it would simply land in the
        // blank slot 0 of pain_messages). Nothing here treats 0 as special.
        ParseResult<MonsterPain> result = load("zero.txt", withHeader(1, entry(0, SEVEN)));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(0, result.items().get(0).getPainIndex());
    }

    // ---- Known differences from C's line handling (see docs/grammar_issues.md) ----

    @Test
    void indentedDirectiveLineIsAHardErrorHereWhereCSkipsTheIndent() throws IOException {
        // C: parser_parse skips leading isspace() before it looks for the directive, so "  type:1"
        // reads as "type:1". CommentsAndEol only skips trailing spaces before a newline, so the
        // lexer has no rule for a line that starts with a space. Pins the current behaviour.
        ParseResult<MonsterPain> result =
                load("indented.txt", withHeader(1, "  " + entry(1, SEVEN)));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void indentedCommentLineIsAHardErrorHereWhereCSkipsIt() throws IOException {
        // C: after the leading whitespace is skipped, a line starting '#' is a comment. COMMENT
        // here must start in column 0. Pins the current behaviour.
        ParseResult<MonsterPain> result =
                load("indented-comment.txt", withHeader(1, "  # note\n" + entry(1, SEVEN)));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
