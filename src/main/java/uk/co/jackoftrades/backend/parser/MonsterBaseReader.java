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
import uk.co.jackoftrades.backend.parser.monsterbase.MonsterBaseLexer;
import uk.co.jackoftrades.backend.parser.monsterbase.MonsterBaseParser;
import uk.co.jackoftrades.middle.monsters.MonsterBase;

import java.io.IOException;
import java.util.List;

public class MonsterBaseReader implements Parser<MonsterBase> {
    private final static Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @NotNull
    @Override
    public List<MonsterBase> parse(@NotNull String filename) throws IOException {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            MonsterBaseLexer lexer = new MonsterBaseLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MonsterBaseParser parser = new MonsterBaseParser(tokens);
            MonsterBaseParser.FileContext output = parser.file();

            return output.bases;
        } catch (IOException ex) {
            logger.error("Error while loading file {}", filename, ex);
            throw ex;
        }
    }
}
