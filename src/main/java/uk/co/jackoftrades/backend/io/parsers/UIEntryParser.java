package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.uientry.UIEntryGramLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.uientry.UIEntryGramParser;
import uk.co.jackoftrades.frontend.entries.UIEntry;

import java.util.ArrayList;

public class UIEntryParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<UIEntry> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            UIEntryGramLexer lexer = new UIEntryGramLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            UIEntryGramParser parser = new UIEntryGramParser(tokens);
            UIEntryGramParser.EntriesContext result = parser.entries();

            return result.entryList;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return null;
    }
}
