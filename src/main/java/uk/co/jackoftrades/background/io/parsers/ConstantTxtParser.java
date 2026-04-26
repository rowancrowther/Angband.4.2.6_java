package uk.co.jackoftrades.background.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.background.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.background.io.parsers.antlr.parsers.constantformatter.ConstantsFormatterLexer;
import uk.co.jackoftrades.background.io.parsers.antlr.parsers.constantformatter.ConstantsFormatterParser;
import uk.co.jackoftrades.background.utils.globalvalues.*;

import java.io.IOException;
import java.util.HashMap;

public class ConstantTxtParser {
    private static final Logger logger = LogManager.getLogger();

    public void parse() throws IOException, InvalidTokenFoundDuringParse {
        CharStream stream = CharStreams.fromFileName("C:\\Users\\rowan\\OneDrive\\Documents\\Desktop\\Angband-4.2.6\\lib\\gamedata\\constants.txt");
        ConstantsFormatterLexer lexer = new ConstantsFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConstantsFormatterParser parser = new ConstantsFormatterParser(tokens);
        ConstantsFormatterParser.FileContext result = parser.file();
        HashMap<String, String> values = result.keyValues;

        // Loop through the file and pull out the various requirements
        for (String value : values.keySet()) {
            switch (value) {
                case "level-max":
                    LevelMaxConstants.setValue(values.get(value));
                    break;

                case "mon-gen":
                    MonsterGenerationConstants.setValue(values.get(value));
                    break;

                case "mon-play":
                    MonsterGameplayConstants.setValue(values.get(value));
                    break;

                case "dun-gen":
                    DungeonGenerationConstants.setValue(values.get(value));
                    break;

                case "world":
                    GameWorldConstants.setValue(values.get(value));
                    break;

                case "carry-cap":
                    CarryCapacityConstants.setValue(values.get(value));
                    break;

                case "store":
                    StoreConstants.setValue(values.get(value));
                    break;

                case "obj-make":
                    ObjectConstants.setValue(values.get(value));
                    break;

                case "melee-critical":
                    MeleeCriticalConstants.setValue(values.get(value));
                    break;

                case "melee-critical-level":
                    CriticalLevelConstants.setValue(values.get(value), CriticalLevelConstants.CriticalType.MELEE);
                    break;

                case "ranged-critical":
                    RangedCriticalConstants.setValue(values.get(value));
                    break;

                case "ranged-critical-level":
                    CriticalLevelConstants.setValue(values.get(value), CriticalLevelConstants.CriticalType.RANGED);
                    break;

                case "o-melee-critical":
                    O_MeleeCriticalConstants.setValue(values.get(value));
                    break;

                case "o-melee-critical-level":
                    O_CriticalLevelConstants.setValue(values.get(value), O_CriticalLevelConstants.CriticalType.MELEE);
                    break;

                case "o-ranged-critical":
                    O_RangedCriticalConstants.setValue(values.get(value));
                    break;

                case "o-ranged-critical-level":
                    O_CriticalLevelConstants.setValue(values.get(value), O_CriticalLevelConstants.CriticalType.RANGED);
                    break;

                default:
                    String message = "Fatal error: Unable to recognise incoming tokens in constants.txt file. Tokens received: " + value + ":" + values.get(value);
                    logger.fatal(message);
                    throw new InvalidTokenFoundDuringParse(message);
            }
        }
    }
}