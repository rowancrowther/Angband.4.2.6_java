package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.backend.io.parsers.antlr.world.WorldReaderLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.world.WorldReaderParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class WorldParser {
    @CheckReturnValue
    public HashMap<Integer, ArrayList<String>> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        WorldReaderLexer lexer = new WorldReaderLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        WorldReaderParser parser = new WorldReaderParser(tokens);
        WorldReaderParser.FileContext result = parser.file();

        return result.levels;
    }
}