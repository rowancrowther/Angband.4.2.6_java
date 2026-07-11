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

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;

import uk.co.jackoftrades.backend.parser.grammars.activations.ActivationsGrammar;
import uk.co.jackoftrades.backend.parser.grammars.activations.ActivationsLexer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ParseErrors}, the shared collect-and-report error listener.
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
        // A record-count header (C12) is required before the records themselves.
        String src = "record-count:1\nname:CURE_POISON\naim:0\nlevel:5\npower:1\neffect:CURE:POISONED\ndesc:neutralizes poison\n";

        ParseErrors errors = parse(src, "clean.txt");

        assertFalse(errors.hasErrors(), () -> errors.getErrors().toString());
    }

    @Test
    void malformedParseCollectsAtLeastOneError() {
        // The header is present, but a record must begin with "name:"; starting
        // with "power:" is a syntax error the collector accumulates.
        ParseErrors errors = parse("record-count:1\npower:5\n", "malformed.txt");

        assertTrue(errors.hasErrors());
    }

    @Test
    void getErrorsReturnsAnUnmodifiableView() {
        ParseErrors errors = freshCollector("test.txt");
        errors.add(1, "boom");

        List<String> view = errors.getErrors();

        assertThrows(UnsupportedOperationException.class, () -> view.add("nope"));
    }
}
