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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.projection.ProjectionGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.projection.ProjectionLexer;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionAssembler;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link Projection} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class ProjectionReader implements Reader<Projection> {
    /**
     * Logger used to report file-loading failures
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     * @throws IOException if there is an issue with creating the CharStream
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<Projection> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule, fails closed on any hard grammar/lexer error via {@link ParseErrors#throwIfAny()},
     * soft-checks the declared {@code record-count:} header against the number of rows actually
     * parsed, then builds a {@link ProjectionParseRecord} per row.
     *
     * <p>Each row's trailing field is its declared line number, parsed here rather than left as a
     * string; a row whose line number does not parse as an integer is not fatal - it is reported into
     * {@code errors} and the record is built with {@code -1} in that field instead, so a single bad
     * row does not lose the rest of the file.
     *
     * <p>Function extract coded before 260915, commented in full on 260915.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch and any per-row bad line number
     *                     are both appended here
     * @return the parsed projection records, one per row, in file order
     */
    private static List<ProjectionParseRecord> extract(
            @NotNull ProjectionGrammar parser, @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {

        List<ProjectionParseRecord> records = new ArrayList<>();
        ProjectionGrammar.FileContext output = parser.file();
        List<List<String>> results = output.projections;
        errorCatcher.throwIfAny();
        String declaredRecordCount = output.records;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        for (List<String> result : results) {
            String lineStr = result.getLast();
            int line;
            try {
                line = Integer.parseInt(lineStr);
            } catch (NumberFormatException e) {
                errors.add("Invalid number format for declared line: " + lineStr +
                        " Projection code: " + result.getFirst());
                line = -1;
            }

            records.add(new ProjectionParseRecord(result.get(0), result.get(1),
                    result.get(2), result.get(3), result.get(4), result.get(5),
                    result.get(6), result.get(7), result.get(8), result.get(9),
                    result.get(10), result.get(11), result.get(12), result.get(13),
                    result.get(14), line));
        }

        return records;
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link Projection}s - each row's trailing field resolved to an {@code int} line number by
     * {@link #extract} - together with any soft errors collected during parsing and assembly.
     * {@link #parse} is the items-only convenience over this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 260915.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} of type {@link Projection}
     * @throws IOException when there is a problem finding or reading
     *                     the file
     */
    public ParseResult<Projection> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename, ProjectionLexer::new,
                ProjectionGrammar::new,
                ProjectionReader::extract,
                new ProjectionAssembler(), logger);
    }
}