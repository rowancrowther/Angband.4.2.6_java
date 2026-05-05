package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature.TerrainLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.terrainfeature.TerrainParser;
import uk.co.jackoftrades.middle.cave.TerrainFeature;

import java.util.ArrayList;

public class TerrainFeatureReader {
    private static final Logger logger = LogManager.getLogger();

    @CheckReturnValue
    public ArrayList<TerrainFeature> parse(String filename) {
        try {
            CharStream stream = CharStreams.fromFileName(filename);
            TerrainLexer lexer = new TerrainLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TerrainParser parser = new TerrainParser(tokens);
            TerrainParser.FeaturesContext result = parser.features();

            return result.terrainFeatures;
        } catch (Exception e) {
            logger.error("Exception thrown duing parsing of Terrain Features.", e);
        }

        return null;
    }
}