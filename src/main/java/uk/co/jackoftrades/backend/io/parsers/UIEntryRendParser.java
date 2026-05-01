package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.uientryrenderer.UIEntryRendererLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.uientryrenderer.UIEntryRendererParser;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;

import java.util.ArrayList;


public class UIEntryRendParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<UIEntryRenderer> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            UIEntryRendererLexer lexer = new UIEntryRendererLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UIEntryRendererParser parser = new UIEntryRendererParser(tokens);
            UIEntryRendererParser.EntriesContext result = parser.entries();

            return result.allEntries;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }
}
