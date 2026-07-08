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

// Lexer for lib/gamedata/pain.txt - the sets of graduated "monster in pain"
// messages, indexed by a type number that monster_base.txt / monster.txt refer
// to via their pain: directive. Cf. the C directive table in init_parse_pain()
// (src/mon-init.c:516) and struct file_parser pain_parser (src/mon-init.c:569).
//
// Each record is a "type:<n>" header followed by seven "message:<text>" lines.
// This lexer only tokenises; peeling the type number off and validating the
// message count is left to the reader/assembler.
//
// @author Rowan Crowther

lexer grammar PainLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling.
import CommentsAndEol;

// "record-count:<n>" - header declaring how many pain-message sets follow.
RECORD_COUNT
        :   'record-count:'
        ;

// "type:<n>" - opens a new pain-message set; the value is its index.
TYPE
        :   'type:'
        ;

// "message:<text>" - one graduated pain message; the value is read as free
// text (see FREE_TEXT_MODE below).
MESSAGE
        :   'message:' -> pushMode(FREE_TEXT_MODE)
        ;

// A bare non-negative integer.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Free-running message text, including the "[s]"/"[ies|y]" pluralization-
// suffix and '|' alternation syntax pain messages use (e.g. "cr[ies|y] out
// in pain").
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
