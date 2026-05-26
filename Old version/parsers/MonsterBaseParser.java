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

package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader.MonsterBaseReaderLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader.MonsterBaseReaderParser;
import uk.co.jackoftrades.middle.monsters.MonsterBase;

import java.io.IOException;
import java.util.ArrayList;

public class MonsterBaseParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<MonsterBase> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            MonsterBaseReaderLexer lexer = new MonsterBaseReaderLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MonsterBaseReaderParser parser = new MonsterBaseReaderParser(tokens);
            MonsterBaseReaderParser.MonsterBasesContext result = parser.monsterBases();

            return result.entries;

        } catch (IOException e) {
            logger.error("Error while reading file " + filename, e);
        }

        return null;
    }
}
