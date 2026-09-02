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
 * Lexer for lib/gamedata/ui_entry.txt - tokenises the UI-element definitions
 * parsed by UIEntryGrammar.g4: the record-count header, the directive keywords
 * (name:/parameter:/renderer:/combine:/priority:/category:/flags:/desc:/label:/
 * label5:/label2:/template:), the '<TAG>' punctuation with its upper-case
 * element/stat value, lower-case symbolic names and integers.
 *
 * Directive values come in three flavours and are lexed accordingly: a bare
 * symbolic name (LCASE) stays in the default mode, a free-text value switches
 * into FREE_TEXT_MODE so it can contain otherwise-significant characters up to
 * end of line, and an enum/flag value switches into FLAG_MODE so the shared
 * FLAG token (and '|' separators) can match it. Each value mode pops back at
 * end of line.
 *
 * Shared comment/whitespace/EOL handling and the FLAG token body are inherited
 * from the imported CommentsAndEol and Flags grammars.
 */
lexer grammar UIEntryLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

// NAME through TEMPLATE below: one literal directive-keyword token each,
// matching the parser rule of the same purpose. Those whose value is free text
// or an enum switch into FREE_TEXT_MODE / FLAG_MODE respectively; NAME (whose
// value is a bare LCASE symbol) and the value-less directives stay in the
// default mode.
NAME
        :   'name:'
        ;

PARAMETER
        :   'parameter:' -> pushMode(FREE_TEXT_MODE)
        ;

RENDERER
        :   'renderer:' -> pushMode(FREE_TEXT_MODE)
        ;

COMBINE
        :   'combine:' -> pushMode(FLAG_MODE)
        ;

PRIORITY
        :   'priority:' -> pushMode(FREE_TEXT_MODE)
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

LABEL
        :   'label:' -> pushMode(FREE_TEXT_MODE)
        ;

LABEL5
        :   'label5:' -> pushMode(FREE_TEXT_MODE)
        ;

LABEL2
        :   'label2:' -> pushMode(FREE_TEXT_MODE)
        ;

TEMPLATE
        :   'template:' -> pushMode(FREE_TEXT_MODE)
        ;

// The '<' and '>' delimiting a specialized entry's "<TAG>" suffix, e.g. the
// angle brackets around ACID in resist_ui_compact_0<ACID>.
LTHAN
        :   '<'
        ;

GTHAN
        :   '>'
        ;

// A (possibly negative) literal integer. Only the record-count: header uses it
// (RECORD_COUNT INTEGER); every other numeric value - including a numeric
// priority: - is a free-text STRING, so it never reaches this token. Declared
// before LCASE so a pure-digit run matches INTEGER rather than LCASE.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// A lower-case symbolic name (letters, digits, underscores) - the value of a
// name: line and the undecorated part of a specialized entry's name.
LCASE
        :   ('a'..'z' | '0'..'9' | '_')+
        ;

// The upper-case element/stat value inside a "<TAG>" suffix (e.g. ACID, STR).
PARAMETER_VALUE
        :   ('A'..'Z')+
        ;

// FLAG_MODE - entered from combine:/flags:. Matches one or more enum/flag names
// (the shared FLAG token from the Flags import), '|'-separated, until end of
// line, then pops back to the default mode.
mode FLAG_MODE;

// A single enum/flag symbolic name - a combiner (combine:) or an entry flag
// (flags:); resolved to the enum value later, by the reader/assembler.
FLAG
        :   FLAG_BODY
        ;

// The '|' separator between flags in a multi-flag flags: line, tolerating a
// surrounding space on either side.
FLAG_OR
        :   ' '? '|' ' '?
        ;

// End of a flag line: consumed (skipped) and pops back to the default mode.
FLAG_EOL
        :   ' '* '\r'? '\n'+ -> skip, popMode
        ;

// FREE_TEXT_MODE - entered from the free-text directives (renderer:, priority:,
// category:, desc:, the label:s, template:). Matches the rest of the line
// verbatim, then pops back to the default mode.
mode FREE_TEXT_MODE;

// Everything from just after the directive colon to end of line, kept verbatim.
STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
