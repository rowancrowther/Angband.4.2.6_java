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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.monsters.MonsterPain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
