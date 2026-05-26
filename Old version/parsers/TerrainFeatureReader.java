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
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature.TerrainLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature.TerrainParser;
import uk.co.jackoftrades.middle.cave.Feature;

import java.util.ArrayList;

public class TerrainFeatureReader {
    private static final Logger logger = LogManager.getLogger();

    @CheckReturnValue
    public ArrayList<Feature> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            TerrainLexer lexer = new TerrainLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TerrainParser parser = new TerrainParser(tokens);
            TerrainParser.FeaturesContext result = parser.features();

            return result.terrainFeatures;
        } catch (Exception e) {
            logger.error("Exception thrown during parsing of Terrain Features.", e);
        }

        return null;
    }
}