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

// Lexer for lib/gamedata/object_base.txt - tokenises the tval-level defaults
// every object kind of a base type inherits (display colour, breakage chance,
// max stack size, base-level flags like HATES_ACID/SHOW_DICE) into the directive
// keywords, integers, flag names and free text that its paired parser
// ObjectBaseGrammar assembles. Cf. src/obj-init.c struct file_parser
// object_base_parser, whose parse_object_base_default_*/_name/_graphics/_break/
// _max_stack/_flags handlers this pipeline ports.
//
// Four lexical modes keep otherwise-ambiguous payloads apart:
//   - default mode reads the directive keywords and bare INTEGERs;
//   - NAME_TVAL (entered by name:) reads the tval as T_VAL up to a ':' or the line
//     end. The ':' (T_VAL_COLON) switches to FREE_TEXT_MODE for the plural name;
//     a bare line end (NAME_EOL) returns to the default mode for a base with no
//     plural (e.g. "name:gold"). Both are EMITTED, not skipped - the parser needs
//     them to tell the two `name` forms apart;
//   - FREE_TEXT_MODE (also entered by graphics:) grabs a whole line as one STRING;
//   - FLAG_MODE (entered by flags:) reads '|'-separated FLAG tokens.
// A FLAG is letter-initial (imports/Flags.g4 FLAG_BODY), so it can never collide
// with INTEGER even when its tail carries digits.
//
// @author Rowan Crowther

lexer grammar ObjectBaseLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

DEFAULT_MAX_STACK
        :   'default:max-stack:'
        ;

DEFAULT_BREAK_CHANCE
        :   'default:break-chance:'
        ;

// NAME through FLAGS below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
NAME
        :   'name:' -> mode(NAME_TVAL)
        ;

GRAPHICS
        :   'graphics:' -> mode(FREE_TEXT_MODE)
        ;

BREAK
        :   'break:'
        ;

MAX_STACK
        :   'max-stack:'
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

// A bare integer.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// The tval that follows name:. T_VAL runs to the ':' or line end (excluding both
// and the newline). A trailing ':' (T_VAL_COLON) means a plural name follows, so
// switch to FREE_TEXT_MODE; a bare line end (NAME_EOL) means no plural, so return
// to the default mode. Both separators are emitted so the parser can pick the
// right `name` alternative - marking either -> skip breaks that choice.
mode NAME_TVAL;

T_VAL
        :   ~(':' | '\r' | '\n')+
        ;

T_VAL_COLON
        :   ':' -> mode(FREE_TEXT_MODE)
        ;

NAME_EOL
        :   '\r'? '\n' -> mode(DEFAULT_MODE)
        ;

// One line of arbitrary text (the plural name after name:, or the colour after
// graphics:). STRING consumes to the newline; STRING_EOL is a pure terminator, so
// it is skipped (unlike the NAME_TVAL separators) before returning to default mode.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

STRING_EOL
        :   '\r'? '\n'  -> skip, mode(DEFAULT_MODE)
        ;

// A '|'-separated list of flag names (flags:). FLAG wraps the shared FLAG_BODY
// fragment; FLAG_OR is the separator (optional surrounding spaces); FLAG_EOL ends
// the list and popModes back to the default mode.
mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   ('\r'? '\n') -> popMode
        ;