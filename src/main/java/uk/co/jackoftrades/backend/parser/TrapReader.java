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
import uk.co.jackoftrades.backend.parser.grammars.trap.TrapGrammar;
import uk.co.jackoftrades.backend.parser.grammars.trap.TrapLexer;
import uk.co.jackoftrades.backend.parser.trap.TrapAssembler;
import uk.co.jackoftrades.backend.parser.trap.TrapParseRecord;
import uk.co.jackoftrades.middle.cave.TrapKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for {@code lib/gamedata/trap.txt}, producing one {@link TrapKind} per record (traps,
 * runes/glyphs, decoys, door locks, …). Wires the generated {@code TrapLexer}/{@code TrapGrammar}
 * to {@link TrapAssembler} through the shared {@link GrammarDriver}, mirroring the C
 * {@code trap_parser} in {@code src/init.c}. This is the reader {@code GameConstants.loadTraps()}
 * drives.
 *
 * @author Rowan Crowther
 */
public class TrapReader implements Reader<TrapKind> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<TrapKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse {@code filename}, returning both the assembled {@link TrapKind}s and the error channels
     * (soft assembly errors plus any hard grammar/lexer failure), so callers can gate on
     * {@link ParseResult#hasErrors()}.
     *
     * @param filename path to the trap data file
     * @return the parse result: the trap kinds and any errors collected
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<TrapKind> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                TrapLexer::new,
                TrapGrammar::new,
                TrapReader::extract,
                new TrapAssembler(), logger);
    }

    /**
     * {@link GrammarDriver} extraction hook: runs the {@code file} rule, surfaces any hard parse
     * errors via {@code errorCatcher}, and soft-validates the declared {@code record-count:} header
     * against the number of records actually parsed.
     *
     * @param parser       the grammar positioned at the start of the file
     * @param errorCatcher collects hard lexer/parser errors; {@code throwIfAny} aborts on failure
     * @param errors       the soft-error channel (e.g. a record-count mismatch)
     * @return the raw parsed trap records for the assembler
     * @author Rowan Crowther
     */
    private static List<TrapParseRecord> extract(
            @NotNull TrapGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        TrapGrammar.FileContext output = parser.file();
        List<TrapParseRecord> results = output.traps;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }
}
