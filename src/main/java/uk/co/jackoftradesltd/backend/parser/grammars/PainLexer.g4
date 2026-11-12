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
// messages, indexed by a type number that monster_base.txt refers to through
// its pain: directive. Cf. the C directive table in init_parse_pain() and
// struct file_parser pain_parser, both in mon-init.c.
//
// Each record is a "type:<n>" header followed by up to seven "message:<text>"
// lines (C accepts fewer and leaves the missing slots empty). This lexer only
// tokenizes; peeling the type number off and validating the message count is
// left to the reader/assembler.
//
// The type number is the one place this lexer is looser than it looks. C reads
// it as a uint with strtoul in base 0, so the lexer hands the text over as
// written (sign, 0x prefix, tail and all) and PainAssembler decides what it
// means. Nothing here refuses a '-': the assembler needs to see it to refuse
// it with the record's line and file.
//
// @author Rowan Crowther

lexer grammar PainLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling.
import CommentsAndEol;

// "type:<n>" - opens a new pain-message set; the value is its index.
TYPE
        :   'type:'
        ;

// "message:<text>" - one graduated pain message; the value is read as free
// text (see FREE_TEXT_MODE below).
MESSAGE
        :   'message:' -> pushMode(FREE_TEXT_MODE)
        ;

// The value of "type:<n>" - C's "type uint index" field, read by parser_parse in
// parser.c with strtoul in base 0. The token follows that reading:
//  - an optional '-' or '+' sign. A '-' is kept so the assembler can refuse it,
//    "-0" included, as C does for a uint.
//  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with
//    or without the prefix, so "ff" lexes here; the assembler refuses it
//    because strtoul finds no digit in it.
//  - any tail up to the colon or the end of the line, because strtoul stops at
//    the first character it cannot use and ignores the rest ("5abc" is 5, and a
//    trailing blank or tab is part of the tail). A decimal digit run is the
//    same token, so 010 is read as octal by the assembler, not here.
UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

// Free-running message text, including the "[s]"/"[ies|y]" pluralization-
// suffix and '|' alternation syntax pain messages use (e.g. "cr[ies|y] out
// in pain").
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
