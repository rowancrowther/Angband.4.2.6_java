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

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.grammars.pain.PainGrammar;
import uk.co.jackoftrades.backend.parser.grammars.pain.PainLexer;
import uk.co.jackoftrades.backend.parser.pain.PainParseRecord;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Grammar-level tests for the split {@code PainLexer}/{@code PainGrammar} pair, driving them straight
 * into the {@link PainParseRecord} list the {@code file} rule builds. There is deliberately no
 * {@link PainReader}/assembler in the loop yet - these pin only what the two {@code .g4} files are
 * responsible for, so they can act as the green target the reader and {@code PainAssembler} are built
 * against later (mirroring how {@code ObjectBaseReaderTest} drops to the raw parser for its
 * flag-merge case).
 *
 * <p>The behaviours under test are the ones we reasoned through while fixing the grammar:
 * <ul>
 *   <li>the lexer's {@code FREE_TEXT_MODE} keeps {@code STRING} from swallowing the {@code type:} /
 *       {@code record-count:} keyword lines (the maximal-munch bug);</li>
 *   <li>the {@code file} rule reads the {@code record-count:} header once, then loops the entries;</li>
 *   <li>each {@code painEntry} yields a record of {@code [typeNum, msg1..msg7]} (the serial number is
 *       carried as element 0 for the assembler to peel off) plus the {@code type:} line number;</li>
 *   <li>the {@code [s]} / {@code [ies|y]} pluralisation and {@code |} alternation syntax in the
 *       message text survives verbatim through the lexer.</li>
 * </ul>
 *
 * <p>Both lexer and parser are wired to {@link #FAIL_FAST} so any hard syntax error surfaces as a
 * test failure rather than ANTLR's silent recovery.
 *
 * @author Rowan Crowther
 */
class PainGrammarTest {

    private static final String REAL_FILE = "lib/gamedata/pain.txt";

    /**
     * Seven filler messages - the minimum a well-formed {@code painEntry} requires.
     */
    private static final String[] SEVEN =
            {"m1", "m2", "m3", "m4", "m5", "m6", "m7"};

    /**
     * Error listener that turns any lexer/parser syntax error into an {@link AssertionError}, so a
     * misparse fails the test loudly instead of being recovered from and hidden.
     */
    private static final BaseErrorListener FAIL_FAST = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
            throw new AssertionError("syntax error at " + line + ":" + charPositionInLine + " - " + msg);
        }
    };

    // ---- fixture helpers -------------------------------------------------

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

    private static PainGrammar.FileContext parse(CharStream stream) {
        PainLexer lexer = new PainLexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_FAST);

        PainGrammar parser = new PainGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_FAST);
        return parser.file();
    }

    private static PainGrammar.FileContext parse(String source) {
        return parse(CharStreams.fromString(source));
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileParsesAll12Sets() throws IOException {
        PainGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        assertEquals("12", file.declaredRecords, "record-count header captured raw");
        assertEquals(12, file.monsterPain.size(), "one PainParseRecord per type: block");
        for (PainParseRecord record : file.monsterPain) {
            assertEquals(8, record.messages().size(),
                    () -> "expected typeNum + 7 messages, got: " + record.messages());
        }
    }

    @Test
    void firstSetKeepsItsTypeThenSevenMessagesInFileOrder() throws IOException {
        PainGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        PainParseRecord first = file.monsterPain.get(0);
        assertEquals("1", first.messages().get(0), "serial number carried as element 0");
        assertEquals("shrug[s] off the attack.", first.messages().get(1), "first graduated message");
        assertEquals("cr[ies|y] out feebly.", first.messages().get(7), "last graduated message");
        assertEquals(23, first.line(), "line is the type: line in pain.txt");
    }

    @Test
    void pluralisationAndAlternationSyntaxSurvivesVerbatim() throws IOException {
        // Proves STRING (inside FREE_TEXT_MODE) captures '[', '|', ']' and '.' as ordinary text
        // rather than choking on them or splitting the message.
        PainGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        PainParseRecord first = file.monsterPain.get(0);
        assertEquals("cr[ies|y] out in pain.", first.messages().get(3),
                () -> "alternation syntax should round-trip intact, got: " + first.messages());
    }

    // ---- Synthetic structure checks --------------------------------------

    @Test
    void headerIsReadOnceAndEveryEntryBecomesItsOwnRecord() {
        // Regression for the earlier "record-count inside the loop" bug: one header, many entries.
        PainGrammar.FileContext file = parse(
                withHeader(2, entry(1, SEVEN) + entry(2, SEVEN)));

        assertEquals("2", file.declaredRecords);
        assertEquals(2, file.monsterPain.size());
        assertEquals("1", file.monsterPain.get(0).messages().get(0));
        assertEquals("2", file.monsterPain.get(1).messages().get(0));
    }

    @Test
    void declaredCountIsCapturedRawAndNotValidatedByTheGrammar() {
        // The header over-declares (5 vs the single entry). Validation is the assembler's job, so the
        // grammar must still parse and simply hand back "5".
        PainGrammar.FileContext file = parse(withHeader(5, entry(1, SEVEN)));

        assertEquals("5", file.declaredRecords);
        assertEquals(1, file.monsterPain.size());
    }

    @Test
    void aMessageContainingPipesAndBracketsLexesAsOneString() {
        // Same alternation syntax, but synthetic and sitting in an arbitrary slot, to prove it is the
        // lexer mode (not the specific shipped data) that keeps the message whole.
        String tricky = "cr[ies|y] out | [s] in pain.";
        PainGrammar.FileContext file = parse(
                withHeader(1, entry(1, tricky, "m2", "m3", "m4", "m5", "m6", "m7")));

        List<String> messages = file.monsterPain.get(0).messages();
        assertEquals(8, messages.size());
        assertEquals(tricky, messages.get(1), "the whole tricky line should be one message");
        assertTrue(messages.get(1).contains("|"), "pipes must survive as literal text");
    }

    // ---- Whitespace / comment / line-ending handling ---------------------

    @Test
    void commentsAndBlankLinesAreSkippedAroundAndWithinEntries() {
        // The shared CommentsAndEol import skips '#' comment lines and blank lines; they must be
        // invisible to the parser whether they sit between entries or between an entry's messages.
        String src = "record-count:1\n"
                + "# a leading comment\n"
                + "\n"
                + "type:1\n"
                + "message:m1\n"
                + "# an interleaved comment\n"
                + "message:m2\n"
                + "\n"
                + "message:m3\n"
                + "message:m4\n"
                + "message:m5\n"
                + "message:m6\n"
                + "message:m7\n";
        PainGrammar.FileContext file = parse(src);

        assertEquals(1, file.monsterPain.size());
        assertEquals(8, file.monsterPain.get(0).messages().size());
    }

    @Test
    void crlfLineEndingsAreHandledAndNotCapturedInMessages() {
        // On Windows the file arrives CRLF; STRING stops at '\r' and the EOL rule eats "\r\n", so no
        // stray carriage return should cling to a message.
        String src = ("record-count:1\n" + entry(1, SEVEN)).replace("\n", "\r\n");
        PainGrammar.FileContext file = parse(src);

        PainParseRecord record = file.monsterPain.get(0);
        assertEquals("1", record.messages().get(0));
        assertEquals("m1", record.messages().get(1), "no trailing \\r should cling to the message");
    }
}
