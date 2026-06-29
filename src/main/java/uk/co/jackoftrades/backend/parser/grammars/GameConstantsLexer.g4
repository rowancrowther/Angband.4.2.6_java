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

// Lexer for lib/gamedata/constants.txt - Angband's miscellaneous
// engine-tuning constants (level/monster-generation maxima, critical-hit
// tables, dungeon-generation limits, etc). Cf. the C parser in src/init.c:
// struct file_parser constants_parser (init.c:1051), whose directive table
// is registered in init_parse_constants() (init.c:949-980). Every directive
// there has the shape "<category> sym label int value" EXCEPT the 4
// critical-hit-level ones, which instead carry several int fields plus a
// trailing str message - that 3-vs-4-numeric-field split is exactly what
// `furtherValue`/`multiValue` below exist to handle:
//   - "category:label:value"                       -> `section`
//     (level-max/mon-gen/mon-play/dun-gen/world/carry-cap/store/obj-make/
//     player/melee-critical/ranged-critical/o-melee-critical/
//     o-ranged-critical)
//   - "category:value:value:MSG_NAME"               -> `furtherValue`
//     (o-melee-critical-level: "uint chance uint dice str msg",
//      o-ranged-critical-level: same)
//   - "category:value:value:value:MSG_NAME"         -> `multiValue`
//     (melee-critical-level: "int cutoff int mult int add str msg",
//      ranged-critical-level: same)
lexer grammar GameConstantsLexer;

import CommentsAndEol;

// A directive's category/label name, e.g. "level-max", "monsters",
// "melee-critical-level" - lowercase words, optionally hyphenated.
GC_NAME
        :   ('a'..'z'|'-')+
        ;

// The trailing message-name field on a critical-level line, e.g. "HIT_GOOD".
GC_MSG
        :   ('A'..'Z'|'_')+
        ;

// Field separator used throughout every directive.
COLON   :   ':'
        ;

// A (possibly negative) literal integer field.
INTEGER
       :   '-'? ('0'..'9')+
       ;
