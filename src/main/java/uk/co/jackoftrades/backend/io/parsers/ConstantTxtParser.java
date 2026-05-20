/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.backend.io.parsers;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;
import uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter.ConstantsFormatterLexer;
import uk.co.jackoftrades.backend.io.parsers.antlr.constantformatter.ConstantsFormatterParser;
import uk.co.jackoftrades.backend.utils.globalvalues.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConstantTxtParser {
    private static final Logger logger = LogManager.getLogger();

    public void parse() throws IOException, InvalidTokenFoundDuringParse {
        CharStream stream = CharStreams.fromFileName("C:\\Users\\rowan\\Documents\\IntelliJProjects\\Angband.4.2.6\\lib\\gamedata\\constants.txt");
        ConstantsFormatterLexer lexer = new ConstantsFormatterLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ConstantsFormatterParser parser = new ConstantsFormatterParser(tokens);
        ConstantsFormatterParser.FileContext result = parser.file();
        ArrayList<HashMap<String, String>> values = result.results;

        // Loop through the file and pull out the various requirements
        for (HashMap<String, String> map : values) {
            String[] keys = map.keySet().toArray(new String[0]);
            String value = keys[0];
            String set = map.get(value);
            switch (value) {
                case "level-max":
                    LevelMaxConstants.setValue(set);
                    break;

                case "mon-gen":
                    MonsterGenerationConstants.setValue(set);
                    break;

                case "mon-play":
                    MonsterGameplayConstants.setValue(set);
                    break;

                case "dun-gen":
                    DungeonGenerationConstants.setValue(set);
                    break;

                case "world":
                    GameWorldConstants.setValue(set);
                    break;

                case "carry-cap":
                    CarryCapacityConstants.setValue(set);
                    break;

                case "store":
                    StoreConstants.setValue(set);
                    break;

                case "obj-make":
                    ObjectConstants.setValue(set);
                    break;

                case "player":
                    PlayerConstants.setValue(set);
                    break;

                case "melee-critical":
                    MeleeCriticalConstants.setValue(set);
                    break;

                case "melee-critical-level":
                    CriticalLevelConstants.setValue(set, CriticalLevelConstants.CriticalType.MELEE);
                    break;

                case "ranged-critical":
                    RangedCriticalConstants.setValue(set);
                    break;

                case "ranged-critical-level":
                    CriticalLevelConstants.setValue(set, CriticalLevelConstants.CriticalType.RANGED);
                    break;

                case "o-melee-critical":
                    O_MeleeCriticalConstants.setValue(set);
                    break;

                case "o-melee-critical-level":
                    O_CriticalLevelConstants.setValue(set, O_CriticalLevelConstants.CriticalType.MELEE);
                    break;

                case "o-ranged-critical":
                    O_RangedCriticalConstants.setValue(set);
                    break;

                case "o-ranged-critical-level":
                    O_CriticalLevelConstants.setValue(set, O_CriticalLevelConstants.CriticalType.RANGED);
                    break;

                default:
                    String message = "Fatal error: Unable to recognise incoming tokens in constants.txt file. " +
                            "Tokens received: " + value + ":" + set;
                    logger.fatal(message);
                    throw new InvalidTokenFoundDuringParse(message);
            }
        }
    }
}