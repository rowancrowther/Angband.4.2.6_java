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
import uk.co.jackoftrades.backend.parser.egoitem.EgoItemLexer;
import uk.co.jackoftrades.backend.parser.egoitem.EgoItemParser;
import uk.co.jackoftrades.middle.objects.EgoItem;

import java.io.IOException;
import java.util.List;

public class EgoItemReader implements Parser<EgoItem> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<EgoItem> parse(@NotNull String filename) throws IOException {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            EgoItemLexer lexer = new EgoItemLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            EgoItemParser parser = new EgoItemParser(tokens);
            EgoItemParser.FileContext output = parser.file();

            return output.egoItems;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}