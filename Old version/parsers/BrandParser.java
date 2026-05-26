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
import uk.co.jackoftrades.backend.io.parsers.antlr.brandreader.BrandReaderLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.brandreader.BrandReaderParser;
import uk.co.jackoftrades.middle.objects.Brand;

import java.util.ArrayList;

public class BrandParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<Brand> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            BrandReaderLexer lexer = new BrandReaderLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            BrandReaderParser parser = new BrandReaderParser(tokens);
            BrandReaderParser.BrandsContext result = parser.brands();

            return result.brandList;
        } catch (Exception e) {
            logger.error("Error occurred during parsing of brand.txt", e);
        }

        return null;
    }
}
