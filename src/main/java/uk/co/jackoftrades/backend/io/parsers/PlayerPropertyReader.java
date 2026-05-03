package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import uk.co.jackoftrades.backend.io.parsers.antlr.playerpropertyformatter.PlayerPropertyLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.playerpropertyformatter.PlayerPropertyParser;
import uk.co.jackoftrades.middle.player.PlayerProperty;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerPropertyReader {
    public ArrayList<PlayerProperty> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        PlayerPropertyLexer lexer = new PlayerPropertyLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PlayerPropertyParser parser = new PlayerPropertyParser(tokens);
        PlayerPropertyParser.PlayerPropertiesContext result = parser.playerProperties();

        return result.properties;
    }
}
