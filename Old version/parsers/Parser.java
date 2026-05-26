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
import uk.co.jackoftrades.backend.io.parsers.antlr.randomformatter.RandomFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.randomformatter.RandomFormatterParser;
import uk.co.jackoftrades.backend.numerics.Random;

public class Parser {
    public static Random parseDiceString(String dice) {
        CharStream stream = CharStreams.fromString(dice);
        RandomFormatterLexer lexer = new RandomFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RandomFormatterParser parser = new RandomFormatterParser(tokens);
        RandomFormatterParser.RandomContext result = parser.random();

        return result.randomDice;
    }
}
