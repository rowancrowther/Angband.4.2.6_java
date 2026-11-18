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

import org.antlr.v4.runtime.*;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2ParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.Directive;
import uk.co.jackoftradesltd.backend.parser.grammars.bloweffect2.BlowEffect2Grammar;
import uk.co.jackoftradesltd.backend.parser.grammars.bloweffect2.BlowEffect2Lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Grammar-level tests for the split {@code BlowEffect2Lexer}/{@code BlowEffect2Grammar} pair, driving
 * them straight into the {@link BlowEffect2ParseRecord} list that the {@code file} rule builds. There
 * is deliberately no reader or assembler in the loop: these pin only what the two {@code .g4} files
 * are responsible for, which is turning {@code blow_effects.txt} text into one ordered
 * {@link Directive} list per record.
 *
 * <p>The behaviours under test:
 * <ul>
 *   <li>each directive keyword yields a {@link Directive} carrying its parse-record name
 *       (five of the ten differ from C's keywords, an accepted deviation in
 *       {@code docs/Accepted_Deviations.md}), carrying the raw text of its one value and the line it was read
 *       from;</li>
 *   <li>the lexer's {@code FREE_TEXT_MODE} lets a value with spaces ({@code Light Green},
 *       {@code drain charges}) arrive as one string, and pops back so the next keyword is matched;</li>
 *   <li>directives keep file order, and a repeated directive is kept whole rather than collapsed;</li>
 *   <li>comments, blank lines, CRLF line endings and a missing final newline do not disturb the
 *       list;</li>
 *   <li>text the grammar cannot match is a syntax error, not a silently shortened file.</li>
 * </ul>
 *
 * <p>Both lexer and parser are wired to {@link #FAIL_FAST}, so a misparse fails the test instead of
 * being recovered from by ANTLR.
 *
 * <p>Class BlowEffect2GrammarTest coded on 261116, updated on 261116 for the accepted directive names.
 *
 * @author Rowan Crowther
 */
class BlowEffect2GrammarTest {

    private static final String REAL_FILE = "/home/rowan/Desktop/Angband-4.2.6/lib/gamedata/blow_effects.txt";

    /**
     * Turns any lexer or parser syntax error into an {@link AssertionError}.
     */
    private static final BaseErrorListener FAIL_FAST = new BaseErrorListener() {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
                                int charPositionInLine, String msg, RecognitionException e) {
            throw new AssertionError("syntax error at " + line + ":" + charPositionInLine + " - " + msg);
        }
    };

    // ---- helpers ---------------------------------------------------------

    private static List<BlowEffect2ParseRecord> parse(CharStream stream) {
        BlowEffect2Lexer lexer = new BlowEffect2Lexer(stream);
        lexer.removeErrorListeners();
        lexer.addErrorListener(FAIL_FAST);

        BlowEffect2Grammar parser = new BlowEffect2Grammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(FAIL_FAST);
        return parser.file().blowEffects;
    }

    private static List<BlowEffect2ParseRecord> parse(String source) {
        return parse(CharStreams.fromString(source));
    }

    /**
     * The one value of a directive, asserting there is exactly one.
     */
    private static String only(Directive d) {
        assertEquals(1, d.values().size(), () -> "expected one value in " + d);
        return d.values().get(0);
    }

    // ---- the real shipped file -------------------------------------------

    @Test
    void realFileParsesOneRecordPerNameLine() throws IOException {
        String text = Files.readString(Path.of(REAL_FILE));
        long names = text.lines().filter(l -> l.startsWith("name:")).count();

        List<BlowEffect2ParseRecord> records = parse(text);

        assertEquals(names, records.size(), "one record per name: line");
        for (BlowEffect2ParseRecord r : records) {
            assertEquals("name", r.directives().get(0).name(),
                    () -> "every record must open with its name: line, got " + r.directives());
        }
    }

    @Test
    void realFilePoisonRecordKeepsEveryDirectiveInFileOrder() throws IOException {
        List<BlowEffect2ParseRecord> records = parse(CharStreams.fromFileName(REAL_FILE));

        BlowEffect2ParseRecord poison = records.stream()
                .filter(r -> only(r.directives().get(0)).equals("POISON"))
                .findFirst().orElseThrow();

        assertEquals(
                List.of("name", "power", "eval", "desc", "base-colour", "resist-colour",
                        "effect", "resist", "lash"),
                poison.directives().stream().map(Directive::name).toList());
        assertEquals(
                List.of("POISON", "20", "10", "poison", "Orange", "Light Green", "element", "POIS", "POIS"),
                poison.directives().stream().map(BlowEffect2GrammarTest::only).toList());
    }

    // ---- one directive at a time -----------------------------------------

    @Test
    void everyDirectiveKeywordGivesItsParseRecordName() {
        String source = """
                name:X
                power:1
                eval:2
                desc:d
                lore-color-base:b
                lore-color-resist:r
                lore-color-immune:i
                effect-type:element
                resist:FIRE
                lash-type:FIRE
                """;

        List<Directive> ds = parse(source).get(0).directives();

        assertEquals(
                List.of("name", "power", "eval", "desc", "base-colour", "resist-colour",
                        "immune-colour", "effect", "resist", "lash"),
                ds.stream().map(Directive::name).toList(),
                "five names differ from C's keywords: an accepted deviation");
        assertEquals(
                List.of("X", "1", "2", "d", "b", "r", "i", "element", "FIRE", "FIRE"),
                ds.stream().map(BlowEffect2GrammarTest::only).toList());
    }

    @Test
    void valuesWithSpacesArriveAsOneString() {
        List<Directive> ds = parse("""
                name:DRAIN_CHARGES
                desc:drain charges
                lore-color-base:Light Red
                """).get(0).directives();

        assertEquals("drain charges", only(ds.get(1)));
        assertEquals("Light Red", only(ds.get(2)));
    }

    @Test
    void numbersStayAsText() {
        List<Directive> ds = parse("name:X\npower:007\neval:-5\n").get(0).directives();

        assertEquals("007", only(ds.get(1)), "no number is parsed at this stage");
        assertEquals("-5", only(ds.get(2)));
    }

    // ---- line numbers ----------------------------------------------------

    @Test
    void eachDirectiveCarriesItsOwnLineNumber() {
        String source = """
                # a comment

                name:A
                power:1
                # another comment
                eval:2
                """;

        List<Directive> ds = parse(source).get(0).directives();

        assertEquals(List.of(3, 4, 6), ds.stream().map(Directive::line).toList());
    }

    // ---- ordering and repetition ----------------------------------------

    @Test
    void directivesKeepFileOrderNotCanonicalOrder() {
        List<Directive> ds = parse("""
                name:A
                resist:FIRE
                effect-type:element
                power:3
                """).get(0).directives();

        assertEquals(List.of("name", "resist", "effect", "power"),
                ds.stream().map(Directive::name).toList(),
                "resist: above effect-type: must stay above it, C reads them in that order");
    }

    @Test
    void repeatedDirectiveIsKeptWholeNotCollapsed() {
        List<Directive> ds = parse("""
                name:A
                desc:first
                desc:second
                power:1
                power:2
                """).get(0).directives();

        assertEquals(List.of("name", "desc", "desc", "power", "power"),
                ds.stream().map(Directive::name).toList());
        assertEquals("first", only(ds.get(1)));
        assertEquals("second", only(ds.get(2)));
        assertEquals("2", only(ds.get(4)));
    }

    @Test
    void nameOnlyRecordIsAccepted() {
        List<BlowEffect2ParseRecord> records = parse("name:A\n");

        assertEquals(1, records.size());
        assertEquals(1, records.get(0).directives().size());
    }

    // ---- record boundaries ------------------------------------------------

    @Test
    void secondNameLineStartsAFreshRecord() {
        List<BlowEffect2ParseRecord> records = parse("""
                name:A
                power:1

                name:B
                eval:9
                """);

        assertEquals(2, records.size());
        assertEquals(List.of("name", "power"),
                records.get(0).directives().stream().map(Directive::name).toList());
        assertEquals(List.of("name", "eval"),
                records.get(1).directives().stream().map(Directive::name).toList());
        assertEquals("B", only(records.get(1).directives().get(0)));
    }

    @Test
    void emptyAndCommentOnlyFilesGiveNoRecords() {
        assertEquals(0, parse("").size());
        assertEquals(0, parse("# nothing\n\n# here\n").size());
    }

    // ---- line-ending and whitespace handling ------------------------------

    @Test
    void crlfLineEndingsDoNotLeakIntoValues() {
        List<Directive> ds = parse("name:A\r\ndesc:attack\r\npower:4\r\n").get(0).directives();

        assertEquals("A", only(ds.get(0)));
        assertEquals("attack", only(ds.get(1)), "the \\r belongs to the line ending");
        assertEquals("4", only(ds.get(2)));
    }

    @Test
    void missingFinalNewlineStillYieldsTheLastDirective() {
        List<Directive> ds = parse("name:A\ndesc:attack").get(0).directives();

        assertEquals(2, ds.size());
        assertEquals("attack", only(ds.get(1)));
    }

    @Test
    void commentBetweenDirectivesIsSkipped() {
        List<Directive> ds = parse("name:A\n# note\npower:1\n").get(0).directives();

        assertEquals(List.of("name", "power"), ds.stream().map(Directive::name).toList());
    }

    // ---- refusals ---------------------------------------------------------

    @Test
    void directiveBeforeAnyNameLineIsRefused() {
        assertThrows(AssertionError.class, () -> parse("power:5\nname:A\n"));
    }

    @Test
    void nameWithNoValueIsRefused() {
        assertThrows(AssertionError.class, () -> parse("name:\npower:1\n"));
    }

    @Test
    void nonNumericPowerIsRefused() {
        assertThrows(AssertionError.class, () -> parse("name:A\npower:abc\n"));
    }

    @Test
    void unknownDirectiveIsRefused() {
        assertThrows(AssertionError.class, () -> parse("name:A\nbogus:1\n"));
    }
}
