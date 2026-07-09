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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.projection.ProjectionGrammar;
import uk.co.jackoftrades.backend.parser.grammars.projection.ProjectionLexer;
import uk.co.jackoftrades.backend.parser.projection.ProjectionAssembler;
import uk.co.jackoftrades.backend.parser.projection.ProjectionParseRecord;
import uk.co.jackoftrades.middle.game.Projection;

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
     * @author Rowan Crowther
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<Projection> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Run the parser and generate the ArrayList from the file
     * logging all errors that occur during the run. Once the parse
     * has been complete, change the incoming values to values
     * acceptable to the data format of the stored values
     *
     * @param filename The name of the file to parse
     * @return A {@link ParseResult} of type {@link Projection}
     * @throws IOException when there is a problem finding or reading
     *                     the file
     * @author Rowan Crowther
     */
    public ParseResult<Projection> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename, ProjectionLexer::new,
                ProjectionGrammar::new,
                ProjectionReader::extract,
                new ProjectionAssembler(), logger);
    }

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
}