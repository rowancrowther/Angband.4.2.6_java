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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.projection.ProjectionLexer;
import uk.co.jackoftrades.backend.parser.projection.ProjectionParser;
import uk.co.jackoftrades.middle.game.Projection;

import java.io.IOException;
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
     * Logger used to report file-loading failures.
     *
     * @author ClaudeCode
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
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            ProjectionLexer lexer = new ProjectionLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ProjectionParser parser = new ProjectionParser(tokens);
            ProjectionParser.FileContext output = parser.file();

            return output.projections != null ? output.projections : List.of();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
