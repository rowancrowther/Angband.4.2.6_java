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
import uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase.UIEntryBaseLexer;
import uk.co.jackoftrades.frontend.entries.UIEntryBase;

import java.io.IOException;
import java.util.ArrayList;

public class UIEntryBaseParser {

    public ArrayList<UIEntryBase> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        UIEntryBaseLexer lexer = new UIEntryBaseLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase.UIEntryBaseParser parser = new uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase.UIEntryBaseParser(tokens);
        uk.co.jackoftrades.backend.io.parsers.antlr.uientrybase.UIEntryBaseParser.BasesContext bases = parser.bases();

        return bases.allBases;
    }
    
    /*
        public HashMap<Integer, ArrayList<String>> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        WorldReaderLexer lexer = new WorldReaderLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WorldReaderParser parser = new WorldReaderParser(tokens);
        WorldReaderParser.FileContext result = parser.file();

        return result.levels;
    }
     */
}
