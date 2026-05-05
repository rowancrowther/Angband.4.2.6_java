package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.parsers.antlr.brandreader.BrandReaderLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.brandreader.BrandReaderParser;
import uk.co.jackoftrades.middle.objects.Brand;

import java.util.ArrayList;

public class BrandParser {
    private static final Logger logger = LogManager.getLogger();

    public ArrayList<Brand> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            BrandReaderLexer lexer = new BrandReaderLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            BrandReaderParser parser = new BrandReaderParser(tokens);
            BrandReaderParser.BrandsContext result = parser.brands();

            return result.brandList;
        } catch (Exception e) {
            logger.error("Error occurred during parsing of brand.txt", e);
        }

        return null;
    }
}
