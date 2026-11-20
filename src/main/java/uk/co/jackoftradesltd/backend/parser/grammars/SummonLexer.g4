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

// Lexer for lib/gamedata/summon.txt - the summon-type definitions (what each
// summon spell can call up: which monster bases / race flag, whether uniques are
// allowed, the message type, and a fallback type). Cf. the C directive table in
// init_parse_summon() (src/mon-summon.c:146) and struct file_parser summon_parser.
//
// This lexer only tokenizes; resolving the message type, monster bases, race
// flag and the fallback (a reference to a sibling summon by name) is left to
// SummonAssembler. The free-text values (name, base, fallback, desc) read in
// FREE_TEXT_MODE; the msgt value reads in MSGT_FREE_TEXT; the race-flag value, an
// upper-case symbol, reads in FLAG_MODE. Each sub-mode emits a single token and
// goes straight back to the default mode.
//
// @author Rowan Crowther

lexer grammar SummonLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling; Flags contributes the shared FLAG_BODY fragment.
import CommentsAndEol, Flags;

// "name:<CODE>" - opens a new summon record; the value (e.g. HI_UNDEAD) is the
// summon type's identifier, read as free text. Referenced by fallback: lines.
NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

// "msgt:<TYPE>" - the message type (e.g. SUM_MONSTER, sum_monster or 20). C
// matches a name in any case or takes a message number, so the value is not
// limited to the flag alphabet: the whole rest of the line is read in
// MSGT_FREE_TEXT, and SummonAssembler cuts it at a ':' and resolves it.
MSGT
        :   'msgt:' -> mode(MSGT_FREE_TEXT)
        ;

// "uniques:<0|1>" - whether the summon may include unique monsters. The one
// numeric directive; read as a bare INTEGER.
UNIQUES
        :   'uniques:'
        ;

// "base:<name>" - an allowed monster base (e.g. zephyr hound). May appear more
// than once (the parser accumulates them); read as free text (base names have
// spaces).
BASE
        :   'base:' -> pushMode(FREE_TEXT_MODE)
        ;

// "race-flag:<FLAG>" - an allowed monster race flag (e.g. DEMON). The genuine
// flag directive, read in FLAG_MODE.
RACE_FLAG
        :   'race-flag:' -> pushMode(FLAG_MODE)
        ;

// "fallback:<NAME>" - the name of another summon type to try on failure (e.g.
// HI_UNDEAD). A reference to a sibling summon, read as free text.
FALLBACK
        :   'fallback:' -> pushMode(FREE_TEXT_MODE)
        ;

// "desc:<text>" - the summon type's description used in messages; read as free
// text.
DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

// A bare integer - the uniques flag (0 or 1). Sign-capable by shared suite
// convention.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Value mode for race-flag: a single upper-case symbol, then pop back so the
// following directive keyword is seen in the default mode.
mode FLAG_MODE;

FLAG
        : FLAG_BODY -> popMode
        ;

// Value mode for the free-text directives (name, base, fallback, desc):
// everything up to end-of-line is one STRING, then pop back to the default mode.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

// Value mode for msgt: everything up to end-of-line is one MSGT_STRING, then
// back to the default mode. The token keeps any ':' in the value; C's
// parser_parse reads the field with strtok(..., ":"), which skips
// leading colons and stops at the next one, and SummonAssembler does the same
// before calling MessageType.lookupByName. An empty value has no token and is a
// syntax error, where C reports a missing field.
mode MSGT_FREE_TEXT;

MSGT_STRING
        :   ~('\n' | '\r')+ -> mode(DEFAULT_MODE)
        ;
