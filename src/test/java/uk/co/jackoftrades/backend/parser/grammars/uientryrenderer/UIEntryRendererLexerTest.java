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

package uk.co.jackoftrades.backend.parser.grammars.uientryrenderer;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the generated {@link UIEntryRendererLexer} (grammar-track step 8, lexer half).
 *
 * <p>The interesting behaviour is the mode switching: {@code name:}/{@code symbols:} push into
 * {@code FREE_TEXT_MODE} (one {@code STRING} token to end of line, spaces and punctuation included),
 * while {@code code:}/{@code sign:} push into {@code FLAG_MODE} where the shared {@code Flags} import's
 * {@code FLAG} token matches an enum-style symbolic name and pops back. {@code colors:}/{@code labelcolors:}
 * stay in the default mode and take a {@code COLOURCHARS} run built from the shared {@code Colours}
 * import; numeric fields are the single sign-capable {@code INTEGER} (C7).
 *
 * @author Rowan Crowther
 */
class UIEntryRendererLexerTest {

    /**
     * Tokenises {@code src}, returning one {@code "SYMBOLIC_NAME=text"} entry per token (EOF excluded).
     * The imported CommentsAndEol comment/EOL rules are skipped, so the result is the bare stream.
     */
    private List<String> tokens(String src) {
        UIEntryRendererLexer lexer = new UIEntryRendererLexer(CharStreams.fromString(src));
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
        UIEntryRendererLexer lexer = new UIEntryRendererLexer(CharStreams.fromString(src));
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
    void aWholeRecordExercisesEveryDirectiveAndBothTextModes() {
        // One directive of each shape, showing FREE_TEXT_MODE (name/symbols), FLAG_MODE (code/sign)
        // and the default-mode COLOURCHARS/INTEGER all push/pop cleanly between directives.
        assertEquals(
                List.of("RECORD_COUNT=record-count:", "INTEGER=1",
                        "NAME=name:", "STRING=foo",
                        "CODE=code:", "FLAG=BAR",
                        "COLOURS=colors:", "COLOURCHARS=wG",
                        "LABELCOLOURS=labelcolors:", "COLOURCHARS=sr",
                        "SYMBOLS=symbols:", "STRING=?.+",
                        "NDIGITS=ndigit:", "INTEGER=1",
                        "SIGN=sign:", "FLAG=NO_SIGN"),
                tokens("record-count:1\nname:foo\ncode:BAR\ncolors:wG\nlabelcolors:sr\n"
                        + "symbols:?.+\nndigit:1\nsign:NO_SIGN\n"));
    }

    @Test
    void codeAndSignPushIntoFlagModeAndFlagAcceptsADigitTail() {
        // code: value is a backend renderer code; sign: is one of NO_SIGN/ALWAYS_SIGN/NEGATIVE_SIGN.
        assertEquals(List.of("CODE=code:", "FLAG=COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX"),
                tokens("code:COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX\n"));
        assertEquals(List.of("SIGN=sign:", "FLAG=ALWAYS_SIGN"), tokens("sign:ALWAYS_SIGN\n"));
        // FLAG admits digits after the leading letter (RAND_50-style names elsewhere in the gamedata);
        // proven here so a regression to a no-digit form would fail.
        assertEquals(List.of("CODE=code:", "FLAG=FOO_2"), tokens("code:FOO_2\n"));
    }

    @Test
    void coloursTakeAColourCodeRunFromTheSharedColoursImport() {
        assertEquals(List.of("COLOURS=colors:", "COLOURCHARS=wwwwwwGGGrr"),
                tokens("colors:wwwwwwGGGrr\n"));
        assertEquals(List.of("LABELCOLOURS=labelcolors:", "COLOURCHARS=swBrgwBrwBwBr"),
                tokens("labelcolors:swBrgwBrwBwBr\n"));
    }

    @Test
    void symbolsIsFreeTextCapturingSpacesAndPunctuationToEndOfLine() {
        // The real char_screen1_stat_mod_renderer uses "? .s*=" - a space and punctuation.
        assertEquals(List.of("SYMBOLS=symbols:", "STRING=? .s*="), tokens("symbols:? .s*=\n"));
    }

    @Test
    void commentsAndBlankLinesAreSkippedAndCrlfIsClean() {
        String src = "# File: ui_entry_renderer.txt\r\n\r\nrecord-count:5\r\nname:foo\r\n";
        assertEquals(
                List.of("RECORD_COUNT=record-count:", "INTEGER=5", "NAME=name:", "STRING=foo"),
                tokens(src));
        assertEquals(0, lexErrors(src));
    }
}
