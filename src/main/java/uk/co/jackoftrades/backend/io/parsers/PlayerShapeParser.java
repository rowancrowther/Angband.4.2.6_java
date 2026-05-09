package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.playershape.PlayerShapeAntlrLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.playershape.PlayerShapeAntlrParser;
import uk.co.jackoftrades.middle.player.PlayerShape;

import java.util.ArrayList;

public class PlayerShapeParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<PlayerShape> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            PlayerShapeAntlrLexer lexer = new PlayerShapeAntlrLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PlayerShapeAntlrParser parser = new PlayerShapeAntlrParser(tokens);
            PlayerShapeAntlrParser.ShapesContext result = parser.shapes();

            return result.shapeArray;
        } catch (Exception e) {
            logger.error("Error while parsing file " + filename, e);
        }

        return null;
    }
}