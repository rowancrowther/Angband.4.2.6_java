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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
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
 * @author ClaudeCode
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
        List<List<String>> projectionRecords;
        int recordCount = 0;

        ParseErrors errorCatcher = null;

        List<ProjectionParseRecord> records = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            CharStream input = CharStreams.fromFileName(filename);
            ProjectionLexer lexer = new ProjectionLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ProjectionGrammar parser = new ProjectionGrammar(tokens);

            // install the error catcher
            errorCatcher = ParseErrors.install(lexer, parser, filename);

            ProjectionGrammar.FileContext output = parser.file();
            projectionRecords = output.projections;

            errorCatcher.throwIfAny();

            try {
                recordCount = Integer.parseInt(output.records);
            } catch (NumberFormatException e) {
                errors.add("Invalid number format for declared record count");
                recordCount = -1;
            }

            if (recordCount != -1 && recordCount != projectionRecords.size()) {
                errors.add("record-count header declares " + recordCount +
                        " but file contains " + projectionRecords.size());
            }

            for (List<String> record : projectionRecords) {
                String lineStr = record.getLast();
                int line;
                try {
                    line = Integer.parseInt(lineStr);
                } catch (NumberFormatException e) {
                    errors.add("Invalid number format for declared line: " + lineStr +
                            " Projection code: " + record.getFirst());
                    line = -1;
                }

                records.add(new ProjectionParseRecord(record.get(0), record.get(1),
                        record.get(2), record.get(3), record.get(4), record.get(5),
                        record.get(6), record.get(7), record.get(8), record.get(9),
                        record.get(10), record.get(11), record.get(12), record.get(13),
                        record.get(14), line));
            }
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            return new ParseResult<>(List.of(), errorCatcher.getErrors());
        }

        List<Projection> projections = new ProjectionAssembler().assemble(records, errors);

        return new ParseResult<>(projections, errors);
    }
}