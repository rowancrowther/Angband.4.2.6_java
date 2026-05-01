package uk.co.jackoftrades.backend.numerics;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.co.jackoftrades.backend.io.parsers.antlr.randomformatter.RandomFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.randomformatter.RandomFormatterParser;

/**
 * STUB CLASS: TODO Code, comment and test this
 */

public class Dice {
    private final Random dice;
    private final String diceString;

    public Dice(String diceString) {
        this.diceString = diceString;

        CharStream stream = CharStreams.fromString(diceString);
        RandomFormatterLexer lexer = new RandomFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RandomFormatterParser parser = new RandomFormatterParser(tokens);
        RandomFormatterParser.RandomContext result = parser.random();
        dice = result.randomDice;
    }
}