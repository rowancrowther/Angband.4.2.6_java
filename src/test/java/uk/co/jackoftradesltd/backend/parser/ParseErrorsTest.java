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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;

import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsLexer;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ParseErrors}, the shared collect-and-report error listener.
 *
 * <p>Expected values come from {@code datafile.c}, functions {@code parse_file()},
 * {@code run_parser()} and {@code print_error()}: an error names the file and a line counted
 * from 1, and any error makes the load fail. The deliberate differences (all errors kept, not
 * just the first; ANTLR's character offset in place of C's field number) are pinned here so a
 * change to them is noticed.
 *
 * <p>Two groups of behaviour are exercised:
 * <ul>
 *   <li>the pure collector API — {@code add}, {@code hasErrors}, {@code getErrors},
 *       {@code throwIfAny} — driven directly, without parsing;</li>
 *   <li>the {@code syntaxError} hook, driven by actually parsing valid and malformed
 *       sources through the Activations grammar with the collector installed, to prove
 *       it accumulates parser errors rather than throwing on the first one.</li>
 * </ul>
 *
 * <p>Class ParseErrorsTest coded before 261104, extended on 261104 for the port pass.
 *
 * @author Rowan Crowther
 */
class ParseErrorsTest {

    /**
     * Builds a {@link ParseErrors} attached to a fresh Activations lexer/parser over
     * {@code src}, without running the parser. Used for the pure-collector tests, where
     * we only need an instance to call {@code add}/{@code throwIfAny} on.
     */
    private static ParseErrors freshCollector(String filename) {
        ActivationsLexer lexer = new ActivationsLexer(CharStreams.fromString(""));
        ActivationsGrammar parser = new ActivationsGrammar(new CommonTokenStream(lexer));
        return ParseErrors.install(lexer, parser, filename);
    }

    /**
     * Installs a collector, runs the parser over {@code src}, and returns the collector
     * so its accumulated errors can be inspected. The default error strategy is left in
     * place, so the parser recovers and reports rather than throwing.
     */
    private static ParseErrors parse(String src, String filename) {
        ActivationsLexer lexer = new ActivationsLexer(CharStreams.fromString(src));
        ActivationsGrammar parser = new ActivationsGrammar(new CommonTokenStream(lexer));
        ParseErrors errors = ParseErrors.install(lexer, parser, filename);
        parser.file();
        return errors;
    }

    @Test
    void freshCollectorHasNoErrors() {
        ParseErrors errors = freshCollector("test.txt");

        assertFalse(errors.hasErrors());
        assertTrue(errors.getErrors().isEmpty());
    }

    @Test
    void addRecordsAFormattedError() {
        ParseErrors errors = freshCollector("test.txt");

        errors.add(42, "boom");

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrors().size());
        String only = errors.getErrors().get(0);
        assertTrue(only.contains("test.txt"), only);
        assertTrue(only.contains("line 42"), only);
        assertTrue(only.contains("boom"), only);
    }

    @Test
    void throwIfAnyDoesNotThrowWhenEmpty() {
        ParseErrors errors = freshCollector("test.txt");

        assertDoesNotThrow(errors::throwIfAny);
    }

    @Test
    void throwIfAnyThrowsWhenErrorsPresent() {
        ParseErrors errors = freshCollector("test.txt");
        errors.add(1, "boom");

        assertThrows(ParseCancellationException.class, errors::throwIfAny);
    }

    @Test
    void cleanParseCollectsNothing() {
        // A well-formed record, so the parse reports nothing.
        String src = "name:CURE_POISON\naim:0\nlevel:5\npower:1\neffect:CURE:POISONED\ndesc:neutralizes poison\n";

        ParseErrors errors = parse(src, "clean.txt");

        assertFalse(errors.hasErrors(), () -> errors.getErrors().toString());
    }

    @Test
    void malformedParseCollectsAtLeastOneError() {
        // A record must begin with "name:"; starting with "power:" is a syntax error the
        // collector accumulates.
        ParseErrors errors = parse("power:5\n", "malformed.txt");

        assertTrue(errors.hasErrors());
    }

    @Test
    void getErrorsReturnsAnUnmodifiableView() {
        ParseErrors errors = freshCollector("test.txt");
        errors.add(1, "boom");

        List<String> view = errors.getErrors();

        assertThrows(UnsupportedOperationException.class, () -> view.add("nope"));
    }

    @Test
    void installMakesTheCollectorTheOnlyListenerOnLexerAndParser() {
        // C has one reporting path per file; ANTLR's ConsoleErrorListener must be gone.
        ActivationsLexer lexer = new ActivationsLexer(CharStreams.fromString(""));
        ActivationsGrammar parser = new ActivationsGrammar(new CommonTokenStream(lexer));

        ParseErrors errors = ParseErrors.install(lexer, parser, "test.txt");

        assertEquals(List.of(errors), parser.getErrorListeners());
        assertEquals(List.of(errors), lexer.getErrorListeners());
    }

    @Test
    void syntaxErrorFormatsFileLineAndOffset() {
        ParseErrors errors = freshCollector("f.txt");

        errors.syntaxError(null, null, 7, 3, "bad token", null);

        assertEquals(List.of("file: f.txt line 7:3 bad token"), errors.getErrors());
        assertTrue(errors.hasErrors());
    }

    @Test
    void addWithLineHasNoColumn() {
        ParseErrors errors = freshCollector("test.txt");

        errors.add(42, "boom");

        assertEquals(List.of("file: test.txt line 42 boom"), errors.getErrors());
    }

    @Test
    void addWithoutLineNamesOnlyTheFile() {
        ParseErrors errors = freshCollector("test.txt");

        errors.add("record count mismatch");

        assertTrue(errors.hasErrors());
        assertEquals(List.of("file: test.txt record count mismatch"), errors.getErrors());
    }

    @Test
    void errorsAreKeptInTheOrderReported() {
        ParseErrors errors = freshCollector("o.txt");

        errors.add(3, "first");
        errors.syntaxError(null, null, 1, 0, "second", null);
        errors.add("third");

        assertEquals(List.of("file: o.txt line 3 first", "file: o.txt line 1:0 second",
                "file: o.txt third"), errors.getErrors());
    }

    @Test
    void malformedParseReportsTheLineCountedFromOne() {
        // C's parse_file counts lines from 1, so the bad line after a blank one is line 2.
        ParseErrors errors = parse("\npower:5\n", "malformed.txt");

        String first = errors.getErrors().get(0);
        assertTrue(first.startsWith("file: malformed.txt line 2:"), first);
    }

    @Test
    void parseKeepsGoingPastTheFirstError() {
        // C stops at the first bad line; this collector deliberately keeps them all.
        String src = "name:A\naim:x\nlevel:5\nname:B\naim:y\nlevel:5\n";

        ParseErrors errors = parse(src, "two.txt");

        assertTrue(errors.getErrors().size() >= 2, () -> errors.getErrors().toString());
    }

    @Test
    void throwIfAnyUsesTheFixedMessageAndThrowsAgainOnSecondCall() {
        ParseErrors errors = freshCollector("test.txt");
        errors.add(1, "boom");

        ParseCancellationException ex =
                assertThrows(ParseCancellationException.class, errors::throwIfAny);
        assertEquals("Errors occurred during parsing, check Error logfile for details.",
                ex.getMessage());
        // The list is not cleared, so a repeat call is still fatal.
        assertThrows(ParseCancellationException.class, errors::throwIfAny);
        assertEquals(1, errors.getErrors().size());
    }

    @Test
    void throwIfAnyThrowsForASyntaxErrorAlone() {
        ParseErrors errors = parse("power:5\n", "malformed.txt");

        assertThrows(ParseCancellationException.class, errors::throwIfAny);
    }

    @Test
    void getErrorsIsALiveView() {
        ParseErrors errors = freshCollector("test.txt");
        List<String> view = errors.getErrors();

        errors.add("later");

        assertEquals(1, view.size());
    }
}
