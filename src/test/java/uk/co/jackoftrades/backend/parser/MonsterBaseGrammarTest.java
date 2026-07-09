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
import uk.co.jackoftrades.backend.parser.grammars.monsterbase.MonsterBaseGrammar;
import uk.co.jackoftrades.backend.parser.grammars.monsterbase.MonsterBaseLexer;
import uk.co.jackoftrades.backend.parser.monsterbase.MonsterBaseParseRecord;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Grammar-level tests for the split {@code MonsterBaseLexer}/{@code MonsterBaseGrammar} pair, driving
 * them straight into the {@link MonsterBaseParseRecord} list the {@code file} rule builds. There is
 * deliberately no {@code MonsterBaseReader}/assembler in the loop - these pin only what the two
 * {@code .g4} files own, following the same pattern as {@code PainGrammarTest}.
 *
 * <p>The behaviours under test are the ones we reasoned through while fixing the grammar:
 * <ul>
 *   <li>the lexer's {@code FREE_TEXT} mode captures a whole {@code name:} / {@code desc:} line as one
 *       {@code STRING} (the missing-{@code +} maximal-munch bug), while {@code GLYPH_MODE} keeps the
 *       glyph a single character;</li>
 *   <li>the {@code file} rule reads the {@code record-count:} header once, then loops the entries;</li>
 *   <li>each {@code monsterBase} captures name/glyph/pain/desc raw and accumulates repeated
 *       {@code flags:} lines, with {@code flags:} optional;</li>
 *   <li>{@code inGameName} is currently wired to the {@code desc:} text (so it mirrors
 *       {@code description}).</li>
 * </ul>
 *
 * <p>Both lexer and parser are wired to {@link #FAIL_FAST} so any hard syntax error surfaces as a
 * test failure rather than ANTLR's silent recovery.
 *
 * @author Rowan Crowther
 */
class MonsterBaseGrammarTest {

    private static final String REAL_FILE = "lib/gamedata/monster_base.txt";

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
     * Render one template block. {@code flags} may be empty (the grammar makes {@code flags:}
     * optional); each entry always carries the mandatory name/glyph/pain/desc.
     */
    private static String entry(String name, char glyph, int pain, String desc, String... flagLines) {
        StringBuilder sb = new StringBuilder("name:").append(name).append("\n")
                .append("glyph:").append(glyph).append("\n")
                .append("pain:").append(pain).append("\n");
        for (String f : flagLines) {
            sb.append("flags:").append(f).append("\n");
        }
        sb.append("desc:").append(desc).append("\n");
        return sb.toString();
    }

    private static MonsterBaseGrammar.FileContext parse(CharStream stream) {
        MonsterBaseLexer lexer = new MonsterBaseLexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_FAST);

        MonsterBaseGrammar parser = new MonsterBaseGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_FAST);
        return parser.file();
    }

    private static MonsterBaseGrammar.FileContext parse(String source) {
        return parse(CharStreams.fromString(source));
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileParsesAll56Templates() throws IOException {
        MonsterBaseGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        assertEquals("56", file.declaredRecordCount, "record-count header captured raw");
        assertEquals(56, file.bases.size(), "one record per name: template");
    }

    @Test
    void firstTemplateCapturesEveryFieldRaw() throws IOException {
        MonsterBaseGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        MonsterBaseParseRecord first = file.bases.get(0);
        assertEquals("ancient dragon", first.codeName());
        assertEquals("D", first.glyph(), "glyph captured as a single-char string");
        assertEquals("1", first.pain(), "pain index captured raw, unresolved");
        assertEquals("Ancient Dragon/Wyrm", first.description());
        assertEquals(34, first.line(), "line is the name: line in monster_base.txt");
    }

    @Test
    void repeatedFlagsLinesAccumulateAcrossTheRecord() throws IOException {
        MonsterBaseGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        // "ancient dragon" spreads its flags over five flags: lines - all must be gathered.
        List<String> flags = file.bases.get(0).flags();
        assertTrue(flags.containsAll(List.of("DRAGON", "EVIL", "POWERFUL", "SMART", "SPIRIT",
                        "DROP_4", "MOVE_BODY", "CLEAR_WEB", "NO_CONF", "NO_SLEEP", "NO_HOLD", "FORCE_SLEEP")),
                () -> "expected all five flags: lines merged, got: " + flags);
    }

    @Test
    void inGameNameCurrentlyMirrorsTheDescription() throws IOException {
        // Pins the present wiring: monsterBase's @after feeds desc: into both inGameName and
        // description. If that intentionally changes, this test should change with it.
        MonsterBaseGrammar.FileContext file = parse(CharStreams.fromFileName(REAL_FILE));

        MonsterBaseParseRecord first = file.bases.get(0);
        assertEquals(first.description(), first.inGameName());
    }

    // ---- Synthetic structure checks --------------------------------------

    @Test
    void headerIsReadOnceAndEveryTemplateBecomesItsOwnRecord() {
        MonsterBaseGrammar.FileContext file = parse(withHeader(2,
                entry("orc", 'o', 3, "Orc", "ANIMAL")
                        + entry("troll", 'T', 6, "Troll", "GIANT | HURT_LIGHT")));

        assertEquals("2", file.declaredRecordCount);
        assertEquals(2, file.bases.size());
        assertEquals("orc", file.bases.get(0).codeName());
        assertEquals("troll", file.bases.get(1).codeName());
    }

    @Test
    void aTemplateWithNoFlagsLinesStillParses() {
        // monsterBase makes flags: a zero-or-more group, so a base can carry none.
        MonsterBaseGrammar.FileContext file = parse(withHeader(1, entry("mold", 'm', 2, "Mold")));

        assertEquals(1, file.bases.size());
        assertTrue(file.bases.get(0).flags().isEmpty(), "no flags: lines -> empty flag list");
    }

    @Test
    void nameWithSpacesIsCapturedWhole() {
        // Regression for the single-char STRING bug: multi-word names must survive intact.
        MonsterBaseGrammar.FileContext file =
                parse(withHeader(1, entry("creeping coins", '$', 9, "Creeping Coins")));

        assertEquals("creeping coins", file.bases.get(0).codeName());
    }

    @Test
    void commentsAndBlankLinesAreSkippedAroundEntries() {
        String src = "record-count:1\n"
                + "# a leading comment\n"
                + "\n"
                + entry("ant", 'a', 7, "Ant", "ANIMAL | WEIRD_MIND");
        MonsterBaseGrammar.FileContext file = parse(src);

        assertEquals(1, file.bases.size());
        assertEquals("ant", file.bases.get(0).codeName());
    }
}
