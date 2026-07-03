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
 * Lexer for lib/gamedata/ui_entry_base.txt - reusable templates for
 * ui_entry.txt: which renderer to use, how multiple sources combine, what
 * category/categories it belongs to, flags, and a description. These
 * templates don't appear directly in the UI - ui_entry.txt entries pull
 * them in by name. Cf. src/ui-entry.c's parsing around the "ui_entry_base"
 * file (ui-entry.c:2259) - part of the same parser machinery as
 * ui_entry.txt rather than its own file_parser struct.
 */

lexer grammar UIEntryBaseLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * name directive
 * cf UIEntryGrammar.g4's 'recordCount' rule
 */
NAME
        :   'name:'
        ;

RENDERER
        :   'renderer:'
        ;

COMBINE
        :   'combine:' -> pushMode(FLAG_MODE)
        ;

CATEGORY
        :   'category:' -> pushMode(FREE_TEXT_MODE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

// INTEGER is declared before LCASE deliberately. LCASE also admits digits, so a pure-digit token
// (only the record-count: value) matches both rules at the same length; ANTLR's equal-length
// tie-break favours the rule declared first, so INTEGER must precede LCASE or the record-count
// header would lex as LCASE and the `recordCount: RECORD_COUNT INTEGER` rule could never match.
// Mixed identifiers like good_flag_ui_compact_0 still win via longest-match on LCASE.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// A lowercase_with_underscores_or_digits symbolic name - used for name:/renderer:/category:.
LCASE
        :   ('a'..'z' | '_' | '0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * A mode to bring in the FLAG lexer token which will match any FLAG enum in
 * the system.
 */
mode FLAG_MODE;

/*
 * @author Rowan Crowther
 *
 * A string representative of an enum, downstream converted to the value of
 * the enum
 */
FLAG
        :   FLAG_BODY
        ;

OR
        :   (' ')? '|' (' ')?
        ;

FLAG_EOL
        :   ' '* '\r'? '\n' -> skip, popMode;

/*
 * @author Rowan Crowther
 *
 * A mode to bring in free-text STRING lexer token which matches everything
 * up to the end of the line
 */
mode FREE_TEXT_MODE;

/*
 * @author Rowan Crowther
 *
 * String token - matches everything up to the end of the line
 */
STRING
        :   ~('\r' | '\n')+ -> popMode
        ;