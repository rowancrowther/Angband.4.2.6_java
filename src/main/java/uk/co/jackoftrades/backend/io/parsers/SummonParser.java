package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.summon.SummonFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.summon.SummonFormatterParser;
import uk.co.jackoftrades.middle.monsters.Summon;

import java.util.ArrayList;

public class SummonParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<Summon> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            SummonFormatterLexer lexer = new SummonFormatterLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            SummonFormatterParser parser = new SummonFormatterParser(tokens);
            SummonFormatterParser.FileContext result = parser.file();

            return result.summons;

        } catch (Exception e) {
            logger.error("Error while parsing MonsterBase", e);
        }

        return null;
    }
}
