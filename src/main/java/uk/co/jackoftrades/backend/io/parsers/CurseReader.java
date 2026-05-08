package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.curse.CurseReaderLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.curse.CurseReaderParser;
import uk.co.jackoftrades.middle.objects.Curse;

import java.util.ArrayList;

public class CurseReader {
    private static final Logger logger = LogManager.getLogger(CurseReader.class);

    public ArrayList<Curse> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            CurseReaderLexer lexer = new CurseReaderLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CurseReaderParser parser = new CurseReaderParser(tokens);
            CurseReaderParser.CursesContext curses = parser.curses();


            return curses.curseList;
        } catch (Exception e) {
            logger.error("Exception while reading file " + filename, e);
        }

        return null;
    }
}
