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
import uk.co.jackoftrades.backend.io.parsers.antlr.pain.PainLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.pain.PainParser;
import uk.co.jackoftrades.middle.monsters.MonsterPain;

import java.util.ArrayList;

public class MonsterPainParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<MonsterPain> parse(String fileName) {
        try {
            CharStream stream = CharStreams.fromFileName(fileName);
            PainLexer lexer = new PainLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PainParser parser = new PainParser(tokens);
            PainParser.PainMessagesContext result = parser.painMessages();

            return result.monsterPainMessages;
        } catch (Exception e) {
            logger.error("Error occurred while parsing monster pain file", e);
        }

        return null;
    }
}