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
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectLexer;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParser;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.io.IOException;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link ItemObject} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class ItemObjectReader implements Reader<ItemObject> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<ItemObject> parse(@NotNull String filename) throws IOException {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            ItemObjectLexer lexer = new ItemObjectLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ItemObjectParser parser = new ItemObjectParser(tokens);
            ItemObjectParser.FileContext output = parser.file();

            return output.itemObjects;
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }

    /**
     * Parse the same item-object data file but return the {@link ObjectKind}
     * view of each entry rather than the {@link ItemObject} view. The data file
     * defines both aspects of every base item, so this is a second pass over the
     * same grammar output exposing the "kind" template objects.
     *
     * @param filename the name of the file
     * @return the list of object kinds read from the file
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public @NotNull List<ObjectKind> parseKinds(@NotNull String filename) throws IOException {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            ItemObjectLexer lexer = new ItemObjectLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ItemObjectParser parser = new ItemObjectParser(tokens);
            ItemObjectParser.FileContext output = parser.file();

            return output.objectKinds;
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}
