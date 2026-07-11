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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.activation.ActivationAssembler;
import uk.co.jackoftrades.backend.parser.activation.ActivationParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.activations.ActivationsGrammar;
import uk.co.jackoftrades.backend.parser.grammars.activations.ActivationsLexer;
import uk.co.jackoftrades.middle.Activation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link Activation} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class ActivationReader implements Reader<Activation> {
    /**
     * Logger used to report file-loading failures.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     *
     * @author Rowan Crowther
     */
    @Override
    public @NotNull List<Activation> parse(@NotNull String filename) throws IOException {
        return parseWithResult(filename).items();
    }

    /**
     * Return an ArrayList of items read from the file, and handle any
     * parse errors found during parsing
     *
     * @param filename the name of the file we are parsing
     * @return a ParseResult record, which pairs the list of items read
     * (activations in this case) with the list of soft error messages
     * @throws IOException when the file could not be found
     * @author Rowan Crowther
     */
    public ParseResult<Activation> parseWithResult(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ActivationsLexer::new,
                ActivationsGrammar::new,
                ActivationReader::extract,
                new ActivationAssembler(), logger);
    }

    private static List<ActivationParseRecord> extract(
            @NotNull ActivationsGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ActivationsGrammar.FileContext output = parser.file();
        List<ActivationParseRecord> result = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);
    }
}