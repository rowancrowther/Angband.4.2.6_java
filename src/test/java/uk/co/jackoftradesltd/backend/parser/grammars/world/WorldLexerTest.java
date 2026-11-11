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

package uk.co.jackoftradesltd.backend.parser.grammars.world;

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the generated {@link WorldLexer} (grammar-track step 8, lexer half).
 *
 * <p>These exercise the lexer in isolation, asserting the exact token stream — symbolic name plus
 * text — for each {@code world.txt} directive shape. They pin down the behaviours designed by hand:
 * the four token kinds ({@code LEVEL}, {@code COLON}, {@code INTEGER}, {@code NAME}), the single sign-capable {@code INTEGER} (C7), {@code INTEGER} winning the
 * longest-match tie against {@code NAME} for a bare depth, names carrying embedded spaces, and the
 * imported {@code CommentsAndEol} comment/EOL rules being skipped (so CRLF gamedata tokenises clean
 * — C9).
 *
 * <p>{@code NAME} is deliberately permissive (anything except CR, LF and colon), so the lexer has
 * no unrecognised-character case on value text; a malformed value is refused by the parser instead.
 *
 * <p>Class WorldLexerTest coded before 261104, updated on 261104 once the {@code record-count:}
 * header and its {@code RECORD_COUNT} token were removed, and on 261109 once the permissive
 * {@code NAME} replaced the unrecognised-character test.
 *
 * @author Rowan Crowther
 */
class WorldLexerTest {

    /**
     * Tokenises {@code src}, returning one {@code "SYMBOLIC_NAME=text"} entry per token (EOF excluded).
     * Skipped tokens (comments, EOLs) never appear, so the result is the bare directive stream.
     */
    private List<String> tokens(String src) {
        WorldLexer lexer = new WorldLexer(CharStreams.fromString(src));
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
        WorldLexer lexer = new WorldLexer(CharStreams.fromString(src));
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
    void levelRecordTokenises() {
        // Locks in three things at once: the depth "0" is an INTEGER (not a NAME) thanks to the
        // longest-match-then-declaration-order tie-break; "Angband 1" is a single NAME despite the
        // embedded space; and "None" is just a NAME.
        assertEquals(
                List.of("LEVEL=level:", "INTEGER=0", "COLON=:", "NAME=Town", "COLON=:", "NAME=None",
                        "COLON=:", "NAME=Angband 1"),
                tokens("level:0:Town:None:Angband 1"));
    }

    @Test
    void signedIntegerIsOneToken() {
        // World depths are never negative, but INTEGER is sign-capable (C7): '-5' is one token.
        assertEquals(List.of("LEVEL=level:", "INTEGER=-5"), tokens("level:-5"));
    }

    @Test
    void commentsAndBlankLinesAreSkipped() {
        assertEquals(
                List.of("LEVEL=level:", "INTEGER=0"),
                tokens("# a header comment\n\nlevel:0\n"));
    }

    @Test
    void crlfLineEndingsTokeniseCleanly() {
        String src = "# a comment\r\nlevel:0:Town:None:Angband 1\r\n";
        assertEquals(
                List.of("LEVEL=level:", "INTEGER=0", "COLON=:", "NAME=Town", "COLON=:", "NAME=None",
                        "COLON=:", "NAME=Angband 1"),
                tokens(src));
        assertEquals(0, lexErrors(src));
    }

    /**
     * Pins that {@code NAME} is deliberately permissive: it accepts anything except CR, LF and
     * colon, so a decimal depth such as {@code 1.5} is not a lexer error. It beats {@code INTEGER}
     * on longest match ({@code 1.5} is three characters against {@code INTEGER}'s one) and arrives
     * as a single {@code NAME} token. Rejecting a non-integer depth is the parser's job, covered in
     * {@code WorldGrammarTest}.
     *
     * <p>Function decimalDepthLexesAsOneNameToken coded on 261109, commented in full on 261109;
     * replaces unrecognisedCharacterRaisesLexerError, whose premise (that '.' matches no token rule)
     * was wrong.
     */
    @Test
    void decimalDepthLexesAsOneNameToken() {
        assertEquals(List.of("LEVEL=level:", "NAME=1.5"), tokens("level:1.5"));
        assertEquals(0, lexErrors("level:1.5"), "NAME accepts '.', so there is no lexer error");
    }
}
