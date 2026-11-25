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
 * (in init.c), registered directive "level int depth sym name sym up sym
 * down" -> parse_world_level
 */

lexer grammar WorldLexer;
import CommentsAndEol;

@members {
    private int colons = 0;
}

COMMENT
        :   INDENT? '#' (~('\r' | '\n'))* (NL | EOF) -> skip
        ;

/*
 * @author Rowan Crowther
 *
 * Literal directive keyword introducing every record.
 * cf WorldGrammar.g4's 'line' rule.
 */
LEVEL   :   INDENT? 'level:' { colons = 0; }
        ;

/*
 * @author Rowan Crowther
 *
 * Field separator used throughout a "level:" line.
 * cf WorldGrammar.g4's 'levelNum', 'levelName' and 'upAndDown' rules.
 */
COLON   :   {colons < 3}? ':' { colons++; }
        ;

/*
 * @author Rowan Crowther
 *
 * A bare (optionally signed) integer - the depth field.
 * cf WorldGrammar.g4's 'levelNum' rule.
 */
INTEGER
        :  ('-' | '+')? ('0' ('x' | 'X')?)? ('a'..'f' | 'A'..'F' | '0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * Free-running letters/digits/spaces - used for the name/up/down fields
 * (e.g. "Angband 1", "Town", "None").
 * cf WorldGrammar.g4's 'levelName' and 'upAndDown' rules.
 */
NAME    :   ~('\r' | '\n' | ':')+
        ;

COLON_TO_END
        :   {colons == 3}? ':' ~('\r' | '\n')*
        ;
