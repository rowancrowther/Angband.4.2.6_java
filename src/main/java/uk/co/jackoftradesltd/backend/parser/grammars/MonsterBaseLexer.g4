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

// Lexer for lib/gamedata/monster_base.txt - the templates that groups of
// monsters share (display glyph, inherited race flags, pain-message set and
// lore text), referenced by monster.txt's base: directive. Cf. the C directive
// table in init_parse_mon_base() (src/mon-init.c:1069) and struct file_parser
// mon_base_parser (src/mon-init.c:1105).
//
// This lexer only tokenises; resolving the RF_ race flags, the glyph and the
// pain: index is left to MonsterBaseAssembler. The glyph, free-text and flag
// values each read in a dedicated sub-mode so their contents never collide
// with the default-mode directive keywords.
//
// @author Rowan Crowther

lexer grammar MonsterBaseLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling; Flags contributes the shared FLAG_BODY fragment.
import CommentsAndEol, Flags;

// "record-count:<n>" - header declaring how many template records follow.
RECORD_COUNT
        :   'record-count:'
        ;

// "name:<text>" - opens a new template record; this name is what monster.txt's
// base: lines reference. Read as free text.
NAME
        :   'name:' -> pushMode(FREE_TEXT)
        ;

// "glyph:<char>" - default display character for monsters using this template.
// Read as a single character (see GLYPH_MODE).
GLYPH
        :   'glyph:' -> pushMode(GLYPH_MODE)
        ;

// "pain:<index>" - which pain.txt message set these monsters use; the value is
// a bare INTEGER.
PAIN
        :   'pain:'
        ;

// "flags:<RF_FLAG> [| <RF_FLAG> ...]" - race flags inherited by every monster
// using this template. Read in FLAG_MODE, where multiple flags may be '|'-
// separated on one line.
FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

// "desc:<text>" - lore description shown by the '/' recall command. Read as
// free text.
DESC
        : 'desc:' -> pushMode(FREE_TEXT)
        ;

// A bare non-negative integer - the pain-message-set index.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Value mode for glyph: exactly one character, then pop back.
mode GLYPH_MODE;

SINGLE_CHAR
        :   ~('\r' | '\n') -> popMode
        ;

// Value mode for the free-text directives (name, desc): everything up to end-
// of-line is one STRING, then pop back to the default mode.
mode FREE_TEXT;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

// Value mode for flags: a run of FLAG tokens optionally '|'-separated, closed
// by the end-of-line which pops back to the default mode. Note FLAG does NOT
// pop - the mode stays open so several flags can be read from one line, and
// FLAG_EOL is what returns to the default mode.
mode FLAG_MODE;

FLAG
        : FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   ('\r' | '\n')+ -> popMode
        ;
