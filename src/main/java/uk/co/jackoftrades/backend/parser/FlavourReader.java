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
import uk.co.jackoftrades.backend.parser.flavour.FlavourKindAssembler;
import uk.co.jackoftrades.backend.parser.flavour.FlavourKindParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.flavour.FlavourGrammar;
import uk.co.jackoftrades.backend.parser.grammars.flavour.FlavourLexer;
import uk.co.jackoftrades.middle.objects.FlavourKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code flavor.txt} into a list of {@link FlavourKind}s, wiring the
 * {@code FlavourLexer}/{@code FlavourGrammar} to the {@link FlavourKindAssembler}
 * through the shared {@link GrammarDriver}. Callers wanting the collected soft
 * errors as well as the items use {@link #parseWithResults}; {@link #parse}
 * returns just the items.
 *
 * @author Rowan Crowther
 */
public class FlavourReader implements Reader<FlavourKind> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<FlavourKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parses the file and returns both the assembled flavour kinds and any soft
     * errors collected along the way (unknown tval/colour, unresolvable sval,
     * record-count mismatch).
     *
     * @param filename the file to read
     * @return the parse result — items plus collected errors
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public @NotNull ParseResult<FlavourKind> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                FlavourLexer::new,
                FlavourGrammar::new,
                FlavourReader::extract,
                new FlavourKindAssembler(), logger);
    }

    /**
     * Drives the parse and pulls the raw kind blocks off the tree: runs the
     * {@code file} rule, surfaces any syntax errors, then soft-checks the
     * declared record-count against the number of kind blocks parsed.
     *
     * @param parser       the grammar to run
     * @param errorCatcher collects syntax errors raised during the parse
     * @param errors       the soft-error sink for the record-count check
     * @return the raw {@link FlavourKindParseRecord}s for the assembler
     * @author Rowan Crowther
     */
    private static List<FlavourKindParseRecord> extract(
            @NotNull FlavourGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        FlavourGrammar.FileContext output = parser.file();
        List<FlavourKindParseRecord> records = output.sections;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }
}
