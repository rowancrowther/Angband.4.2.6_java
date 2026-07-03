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

package uk.co.jackoftrades.backend.parser.grammars.uientrybase;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the generated {@link UIEntryBaseLexer} (grammar-suite step, lexer half).
 *
 * <p>The interesting behaviour is the mode switching. {@code combine:}/{@code flags:} push into
 * {@code FLAG_MODE}, where the shared {@code Flags} import's {@code FLAG} token matches an enum-style
 * symbolic name and {@code FLAG_EOL} pops back at end of line; {@code category:}/{@code desc:} push
 * into {@code FREE_TEXT_MODE} where a single {@code STRING} token captures the rest of the line
 * (spaces and punctuation included) and pops. {@code name:}/{@code renderer:} stay in the default
 * mode and take an {@code LCASE} lowercase-with-underscore/digit run; the header count is
 * {@code INTEGER}.
 *
 * @author ClaudeCode
 */
class UIEntryBaseLexerTest {

    /**
     * Tokenises {@code src}, returning one {@code "SYMBOLIC_NAME=text"} entry per token (EOF excluded).
     * The imported CommentsAndEol comment/EOL rules are skipped, so the result is the bare stream.
     */
    private List<String> tokens(String src) {
        UIEntryBaseLexer lexer = new UIEntryBaseLexer(CharStreams.fromString(src));
        Vocabulary vocab = lexer.getVocabulary();
        List<String> out = new ArrayList<>();
        for (Token t = lexer.nextToken(); t.getType() != Token.EOF; t = lexer.nextToken()) {
            out.add(vocab.getSymbolicName(t.getType()) + "=" + t.getText());
        }
        return out;
    }

    /**
     * Counts lexer recognition errors raised while tokenising {@code src}.
     */
    private int lexErrors(String src) {
        UIEntryBaseLexer lexer = new UIEntryBaseLexer(CharStreams.fromString(src));
        int[] count = {0};
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> r, Object sym, int line, int pos,
                                    String msg, RecognitionException e) {
                count[0]++;
            }
        });
        while (lexer.nextToken().getType() != Token.EOF) {
            // drain
        }
        return count[0];
    }

    @Test
    void aWholeRecordExercisesEveryDirectiveAndAllThreeModes() {
        // One directive of each shape, showing default-mode NAME/RENDERER (LCASE) and INTEGER,
        // FLAG_MODE (combine/flags) and FREE_TEXT_MODE (category/desc) all push/pop cleanly.
        assertEquals(
                List.of("RECORD_COUNT=record-count:", "INTEGER=3",
                        "NAME=name:", "LCASE=foo",
                        "RENDERER=renderer:", "LCASE=bar_renderer",
                        "COMBINE=combine:", "FLAG=LOGICAL_OR",
                        "CATEGORY=category:", "STRING=CHAR_SCREEN1",
                        "FLAGS=flags:", "FLAG=TIMED_AS_AUX",
                        "DESC=desc:", "STRING=hello world"),
                tokens("record-count:3\nname:foo\nrenderer:bar_renderer\ncombine:LOGICAL_OR\n"
                        + "category:CHAR_SCREEN1\nflags:TIMED_AS_AUX\ndesc:hello world\n"));
    }

    @Test
    void combineAndFlagsPushIntoFlagModeAndFlagAdmitsADigitTail() {
        assertEquals(List.of("COMBINE=combine:", "FLAG=LOGICAL_OR"), tokens("combine:LOGICAL_OR\n"));
        assertEquals(List.of("FLAGS=flags:", "FLAG=TIMED_AS_AUX"), tokens("flags:TIMED_AS_AUX\n"));
        // FLAG admits digits after the leading letter (RAND_50-style names elsewhere in the gamedata);
        // proven here so a regression to a no-digit form would fail. Covers both FLAG-bearing fields.
        assertEquals(List.of("COMBINE=combine:", "FLAG=ADD_2"), tokens("combine:ADD_2\n"));
        assertEquals(List.of("FLAGS=flags:", "FLAG=RAND_50"), tokens("flags:RAND_50\n"));
    }

    @Test
    void categoryAndDescAreFreeTextToEndOfLine() {
        // category: is either UPPER_CASE or lower_case (both forms appear in the file); desc: is prose.
        assertEquals(List.of("CATEGORY=category:", "STRING=CHAR_SCREEN1"), tokens("category:CHAR_SCREEN1\n"));
        assertEquals(List.of("CATEGORY=category:", "STRING=abilities"), tokens("category:abilities\n"));
        assertEquals(List.of("DESC=desc:", "STRING= screen.  Punctuation, and spaces kept."),
                tokens("desc: screen.  Punctuation, and spaces kept.\n"));
    }

    @Test
    void nameAndRendererTakeAnLcaseTokenNotFreeText() {
        assertEquals(List.of("NAME=name:", "LCASE=good_flag_ui_compact_0"),
                tokens("name:good_flag_ui_compact_0\n"));
        assertEquals(List.of("RENDERER=renderer:", "LCASE=char_screen1_flag_renderer"),
                tokens("renderer:char_screen1_flag_renderer\n"));
    }

    @Test
    void commentsAndBlankLinesAreSkippedAndCrlfIsClean() {
        String src = "# File: ui_entry_base.txt\r\n\r\nrecord-count:3\r\nname:foo\r\n";
        assertEquals(
                List.of("RECORD_COUNT=record-count:", "INTEGER=3", "NAME=name:", "LCASE=foo"),
                tokens(src));
        assertEquals(0, lexErrors(src));
    }
}
