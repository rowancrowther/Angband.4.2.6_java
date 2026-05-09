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
