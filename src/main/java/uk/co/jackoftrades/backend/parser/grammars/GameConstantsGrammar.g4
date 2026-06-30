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
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

/*
 * @author Rowan Crowther
 *
 * Reader for lib/gamedata/constants.txt - Angband's miscellaneous
 * engine-tuning constants (level/monster-generation maxima, critical-hit
 * tables, dungeon-generation limits, etc). Cf. the C parser in src/init.c:
 * struct file_parser constants_parser (init.c:1051), whose directive table
 * is registered in init_parse_constants() (init.c:949-980). Every directive
 * there has the shape "<category> sym label int value" EXCEPT the 4
 * critical-hit-level ones, which instead carry several int fields plus a
 * trailing str message.
 *
 * <p>Lines consisting of fields (separated by colon ':') are read in
 * where the first field value should be a string and is stored as a
 * category. The remaining fields are stored as a list of strings.
 *
 * Downstream, the field values are separated into String label/Integer
 * value pairs (for everything apart from the 4 critical-hit-level ones, which
 * parse into a record of Integer, Integer, (Integer), String. All of the
 * parsed values are put into records, and collected into a GameConstantData
 * record. This record is stored on GameConstants, and queried for access to
 * constants.txt values.
 */

parser grammar GameConstantsGrammar;

options { tokenVocab = GameConstantsLexer; }

@header
        {
            import uk.co.jackoftrades.backend.parser.gameconstants.GameConstantsParseRecord;

            import java.util.List;
            import java.util.ArrayList;
        }

/*
 * @author Rowan Crowther
 *
 * A 'field' value, either a name stored in a String, an integer stored in
 * a String, or a MessageType stored in a String
 */
field
        returns[String fieldValue]
        :   GC_NAME { $fieldValue = $GC_NAME.getText(); }
        |   INTEGER { $fieldValue = $INTEGER.getText(); }
        |   GC_MSG { $fieldValue = $GC_MSG.getText(); }
        ;

/*
 * @author Rowan Crowther
 *
 * A single line of game constant data, consisting of an initial
 * category label, a list of values stored as strings, and the
 * line number of the line
 */
line
        returns[String category, List<String> fields, int lineNo]
        @init {
            $fields = new ArrayList<>();
        }
        :   { $lineNo = $start.getLine(); }
            GC_NAME { $category = $GC_NAME.getText(); }
            (COLON f=field { $fields.add($f.fieldValue); })+
        ;

/*
 * @author Rowan Crowther
 *
 * Top level, returns a DTO of GameConstantsParseRecord, which consists of
 * three fields: String category, List<String> fields, int lineNo.
 * Contents of this are parsed to the GameConstantsReader which parses
 * the list of field values.
 */
file    returns[ArrayList<GameConstantsParseRecord> results]
        @init {
            $results = new ArrayList<>();
        }
        :   (line {
                        $results.add(new GameConstantsParseRecord($line.category,
                                                                   $line.fields,
                                                                   $line.lineNo));
                  })*
            EOF
        ;