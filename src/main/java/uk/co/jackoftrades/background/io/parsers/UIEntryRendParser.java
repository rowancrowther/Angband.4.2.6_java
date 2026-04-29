package uk.co.jackoftrades.background.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.background.io.parsers.antlr.uiEntryRenderer.UIEntryRendererLexer;
import uk.co.jackoftrades.background.io.parsers.antlr.uiEntryRenderer.UIEntryRendererParser;
import uk.co.jackoftrades.frontend.screen.UIEntry;

import java.util.ArrayList;


public class UIEntryRendParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<UIEntry> parse(String filename) {
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
