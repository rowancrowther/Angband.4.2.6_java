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
 * Lexer for lib/gamedata/world.txt - the dungeon's linear sequence of
 * levels (Town, Angband 1..127) and which level is up/down from each.
 * Cf. the C parser in src/init.c: struct file_parser world_parser
 * (init.c:1162), registered directive "level int depth sym name sym up sym
 * down" -> parse_world_level (init.c:1083-1107)
 */

lexer grammar WorldsLexer;
import CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * The number of records in this file defined in the file itself. Used to
 * check against during reading.
 * cf Worlds.g4's 'recordCount' rule.
 */
RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * Literal directive keyword introducing every record.
 * cf Worlds.g4's 'line' rule.
 */
LEVEL   :   'level:'
        ;

/*
 * @author Rowan Crowther
 *
 * Field separator used throughout a "level:" line.
 * cf Worlds.g4's 'levelNum', 'levelName' and 'upAndDown' rules.
 */
COLON   :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * A bare (optionally signed) integer - the record-count value and the depth
 * field.
 * cf Worlds.g4's 'recordCount' and 'levelNum' rules.
 */
INTEGER
        :  '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * Free-running letters/digits/spaces - used for the name/up/down fields
 * (e.g. "Angband 1", "Town", "None").
 * cf Worlds.g4's 'levelName' and 'upAndDown' rules.
 */
NAME    :   ('A'..'Z'|'a'..'z'|' '|'0'..'9')+
        ;