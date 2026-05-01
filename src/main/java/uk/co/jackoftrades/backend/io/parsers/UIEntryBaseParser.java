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
