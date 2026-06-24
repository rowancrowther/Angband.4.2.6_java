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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.visuals.VisualsLexer;
import uk.co.jackoftrades.backend.parser.visuals.VisualsParser;
import uk.co.jackoftrades.frontend.colour.VisualsCycler;

import java.io.IOException;

/**
 * Loads the visuals/colour-cycling configuration into a single
 * {@link VisualsCycler} by driving the ANTLR-generated {@code VisualsLexer}/
 * {@code VisualsParser}. Unlike the other readers it does not implement
 * {@link Parser}, because the visuals file produces one aggregate object rather
 * than a list of entries (Java port of the C {@code flavor}/visuals loading).
 *
 * @author ClaudeCode
 */
public class VisualsReader {
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
     * @return a VisualsCycler read from the file
     */
    public @NotNull VisualsCycler parse(@NotNull String filename) throws IOException {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            VisualsLexer lexer = new VisualsLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            VisualsParser parser = new VisualsParser(tokens);
            VisualsParser.FileContext output = parser.file();

            return output.cycler;
        } catch (Exception e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
