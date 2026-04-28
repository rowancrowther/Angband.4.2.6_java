package uk.co.jackoftrades.background.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.co.jackoftrades.background.io.parsers.antlr.parsers.world.WorldReaderLexer;
import uk.co.jackoftrades.background.io.parsers.antlr.parsers.world.WorldReaderParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorldParser {
    public HashMap<Integer, ArrayList<String>> parse() throws IOException {
        CharStream stream = CharStreams.fromFileName("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\world.txt");
        WorldReaderLexer lexer = new WorldReaderLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WorldReaderParser parser = new WorldReaderParser(tokens);
        WorldReaderParser.FileContext result = parser.file();

        return result.levels;
    }
}