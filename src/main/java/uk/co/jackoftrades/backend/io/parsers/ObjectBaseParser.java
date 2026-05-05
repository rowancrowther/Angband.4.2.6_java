package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter.ObjectBaseFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.objectbaseformatter.ObjectBaseFormatterParser;
import uk.co.jackoftrades.middle.objects.ObjectBase;

import java.io.IOException;
import java.util.ArrayList;

public class ObjectBaseParser {
    private static final Logger logger = LogManager.getLogger();

    private ArrayList<ObjectBase> objectBases;
    private int defaultBreakPerc;
    private int defaultMaxStack;

    public ArrayList<ObjectBase> parse(String filename) throws IOException {
        CharStream stream = CharStreams.fromFileName(filename);
        ObjectBaseFormatterLexer lexer = new ObjectBaseFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ObjectBaseFormatterParser parser = new ObjectBaseFormatterParser(tokens);
        ObjectBaseFormatterParser.FileContext result = parser.file();

        objectBases = result.bases;
        defaultBreakPerc = result.defaultBreakChance;
        defaultMaxStack = result.defaultMaxStack;

        for (ObjectBase base : objectBases) {
            if (base.getBreakPerc() == -1)
                base.setBreakPerc(defaultBreakPerc);

            if (base.getMaxStack() == -1)
                base.setMaxStack(defaultMaxStack);
        }

        return objectBases;
    }

    @Contract(pure = true)
    public ArrayList<ObjectBase> getBases() {
        return objectBases;
    }
}