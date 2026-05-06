package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader.MonsterBaseReaderLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.monsterbasereader.MonsterBaseReaderParser;
import uk.co.jackoftrades.middle.monsters.MonsterBase;

import java.io.IOException;
import java.util.ArrayList;

public class MonsterBaseParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<MonsterBase> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            MonsterBaseReaderLexer lexer = new MonsterBaseReaderLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MonsterBaseReaderParser parser = new MonsterBaseReaderParser(tokens);
            MonsterBaseReaderParser.MonsterBasesContext result = parser.monsterBases();

            return result.entries;

        } catch (IOException e) {
            logger.error("Error while reading file " + filename, e);
        }

        return null;
    }
}
