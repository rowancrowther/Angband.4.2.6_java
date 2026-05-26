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
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter.ObjectBaseFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter.ObjectBaseFormatterParser;
import uk.co.jackoftrades.middle.objects.ObjectBase;

import java.io.IOException;
import java.util.ArrayList;

public class ObjectBaseParser {
    private static final Logger logger = LogManager.getLogger();

    private ArrayList<ObjectBase> objectBases;
    private int defaultBreakPerc;
    private int defaultMaxStack;

    public ArrayList<ObjectBase> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        ObjectBaseFormatterLexer lexer = new ObjectBaseFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ObjectBaseFormatterParser parser = new ObjectBaseFormatterParser(tokens);
        ObjectBaseFormatterParser.FileContext result = parser.file();

        objectBases = result.bases;
        defaultBreakPerc = result.defaultBreakChance;
        defaultMaxStack = result.defaultMaxStack;

        for (ObjectBase base : objectBases) {
            if (base.getBreakPerc() == -1)
                base.setBreakPerc(defaultBreakPerc);

            if (base.getMaxStack() == -1)
                base.setMaxStack(defaultMaxStack);
        }

        return objectBases;
    }

    @Contract(pure = true)
    public ArrayList<ObjectBase> getBases() {
        return objectBases;
    }
}