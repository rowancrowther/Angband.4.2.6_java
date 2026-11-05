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

package uk.co.jackoftradesltd.channel.parser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsLexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link GrammarDriver#run}.
 *
 * <p>{@code run} has no single C original; it stands in for the parse-then-finish sequence of
 * {@code run_parser()} ({@code datafile.c}), which quits on the first error. The tests pin the
 * one rule that follows from that: every failure leaves {@code run} as an exception, and a
 * {@link ParseResult} is only built for a clean file. The extractor and assembler here are
 * test doubles, so the tests are about the driver's plumbing rather than any one grammar:
 * <ul>
 *   <li>a clean run hands the extractor's records to the assembler and wraps its list;</li>
 *   <li>a grammar error raised through {@link ParseErrors#throwIfAny()} inside the extractor
 *       propagates as a {@link ParseCancellationException}, and the assembler never runs;</li>
 *   <li>a {@link DataFileParseException} from the assembler propagates unchanged;</li>
 *   <li>a missing file is an {@link IOException}, and neither callback runs.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class GrammarDriverTest coded on 260915, commented in full on 260915, rewritten on 261104
 * for the removal of the soft-error path and of {@code checkRecordCount}.
 */
class GrammarDriverTest {

    private static final Logger logger = LogManager.getLogger();

    @TempDir
    Path tempDir;

    /**
     * Writes a throwaway file for {@code run} to open. The test-double extractors never run the
     * parser, so the content only has to exist.
     *
     * <p>Function anyFile coded on 261104, commented in full on 261104.
     *
     * @return the path of the written file
     * @throws IOException if the temp file cannot be written
     */
    private String anyFile() throws IOException {
        Path file = tempDir.resolve("any.txt");
        Files.writeString(file, "");
        return file.toString();
    }

    @Test
    void aCleanRunWrapsTheAssemblersListInFileOrder() throws IOException {
        GrammarDriver.Extractor<ActivationsGrammar, String> extractor = (parser, errorCatcher) -> {
            errorCatcher.throwIfAny();
            return List.of("first", "second");
        };
        Assembler<String, List<String>> assembler = (records, filename) ->
                records.stream().map(String::toUpperCase).toList();

        ParseResult<String> result = GrammarDriver.run(anyFile(), ActivationsLexer::new,
                ActivationsGrammar::new, extractor, assembler, logger);

        assertEquals(List.of("FIRST", "SECOND"), result.items());
    }

    @Test
    void aGrammarErrorPropagatesAndTheAssemblerNeverRuns() throws IOException {
        // C: run_parser() quits on the first parser error, before finish_parse_* is called.
        GrammarDriver.Extractor<ActivationsGrammar, String> extractor = (parser, errorCatcher) -> {
            errorCatcher.add(1, "a bad line");
            errorCatcher.throwIfAny();
            return List.of("unreachable");
        };
        Assembler<String, List<String>> assembler = (records, filename) -> {
            fail("assembler should not run after a grammar error");
            return List.of();
        };
        String file = anyFile();

        assertThrows(ParseCancellationException.class, () -> GrammarDriver.run(file,
                ActivationsLexer::new, ActivationsGrammar::new, extractor, assembler, logger));
    }

    @Test
    void anAssemblerErrorPropagatesUnchanged() throws IOException {
        // C: a non-zero finish_parse_* return makes run_parser() quit; here the assembler throws.
        DataFileParseException thrown = new DataFileParseException("refused");
        GrammarDriver.Extractor<ActivationsGrammar, String> extractor = (parser, errorCatcher) -> List.of("x");
        Assembler<String, List<String>> assembler = (records, filename) -> {
            throw thrown;
        };
        String file = anyFile();

        DataFileParseException caught = assertThrows(DataFileParseException.class, () -> GrammarDriver.run(file,
                ActivationsLexer::new, ActivationsGrammar::new, extractor, assembler, logger));
        assertSame(thrown, caught);
    }

    @Test
    void runPropagatesIOExceptionForAMissingFile() {
        GrammarDriver.Extractor<ActivationsGrammar, Object> extractor = (parser, errorCatcher) -> {
            fail("extractor should not run when the file cannot be opened");
            return List.of();
        };
        Assembler<Object, List<Object>> assembler = (records, filename) -> {
            fail("assembler should not run when the file cannot be opened");
            return List.of();
        };

        assertThrows(IOException.class, () -> GrammarDriver.run(
                tempDir.resolve("no-such-file-should-exist.txt").toString(),
                ActivationsLexer::new,
                ActivationsGrammar::new,
                extractor,
                assembler,
                logger));
    }
}
