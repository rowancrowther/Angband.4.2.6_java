package uk.co.jackoftrades.background.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import uk.co.jackoftrades.background.io.parsers.antlr.projectionreader.ProjectionReaderLexer;
import uk.co.jackoftrades.background.io.parsers.antlr.projectionreader.ProjectionReaderParser;
import uk.co.jackoftrades.middle.game.Projection;

import java.io.IOException;
import java.util.ArrayList;

public class ProjectionParser {

    public ArrayList<Projection> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        ProjectionReaderLexer lexer = new ProjectionReaderLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ProjectionReaderParser parser = new ProjectionReaderParser(tokens);
        ProjectionReaderParser.FileContext result = parser.file();

        return result.projections;
    }
}
