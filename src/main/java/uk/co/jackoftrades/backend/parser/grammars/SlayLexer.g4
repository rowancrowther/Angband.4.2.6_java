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

// Lexer for lib/gamedata/slay.txt - the catalogue of slays that weapons and
// other objects can carry (each identified by a code referenced from
// artifact.txt, ego_item.txt, object.txt and player_timed.txt). Cf. the C
// directive table in init_parse_slay() (src/obj-init.c:786-798) and
// struct file_parser slay_parser (src/obj-init.c:851).
//
// A record is a set of "keyword:value" directives. This lexer only tokenises;
// all interpretation - resolving the race flag / monster base, and enforcing
// that a slay names exactly one of race-flag: or base: - is deferred to
// SlayAssembler, keeping the grammar decoupled from the domain API.
//
// The free-text values (name, base, verbs) and the race-flag value are read in
// dedicated sub-modes so their contents can never collide with the default-mode
// directive keywords; each sub-mode emits a single token and pops straight back.
//
// @author Rowan Crowther

lexer grammar SlayLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling; Flags contributes the shared FLAG_BODY fragment.
import CommentsAndEol, Flags;

// "record-count:<n>" - header declaring how many slay records follow; checked
// against the number actually parsed by the reader.
RECORD_COUNT
        :   'record-count:'
        ;

// "code:<CODE>" - opens a new slay record. The value is the cross-reference
// code (e.g. EVIL_2) other data files use, read as free text.
CODE
        :   'code:' -> pushMode(FREE_TEXT_MODE)
        ;

// "name:<text>" - name of the slain creatures, used in object descriptions.
NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

// "race-flag:<FLAG>" - the monster race flag this slay affects (e.g. EVIL).
// Read in FLAG_MODE so the value lexes as a FLAG. Mutually exclusive with
// base: - the constraint is enforced in the assembler, not here.
RACE_FLAG
        :   'race-flag:' -> pushMode(FLAG_MODE)
        ;

// "base:<name>" - the monster base this slay affects, matched against
// monster_base.txt. Read as free text. Mutually exclusive with race-flag:.
BASE
        :   'base:' -> pushMode(FREE_TEXT_MODE)
        ;

// "multiplier:<n>" - damage-dice multiplier applied in standard combat.
MULTIPLIER
        :   'multiplier:'
        ;

// "o-multiplier:<n>" - damage-dice multiplier applied in O-combat.
O_MULTIPLIER
        :   'o-multiplier:'
        ;

// "power:<n>" - weighting in object-power calculations (100 is neutral).
POWER
        :   'power:'
        ;

// "melee-verb:<text>" - verb used when a susceptible monster is hit in melee.
MELEE_VERB
        :   'melee-verb:' -> pushMode(FREE_TEXT_MODE)
        ;

// "range-verb:<text>" - verb used when a susceptible monster is hit at range.
RANGE_VERB
        :   'range-verb:' -> pushMode(FREE_TEXT_MODE)
        ;

// A literal integer for the numeric directives. Sign-capable by shared suite
// convention, though slay values are always non-negative (C reads them as
// uint); range is validated in the assembler.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Value mode for race-flag: a single upper-case flag, then pop back so the
// following directive keyword is recognised in the default mode.
mode FLAG_MODE;

FLAG
        :   FLAG_BODY -> popMode
        ;

// Value mode for the free-text directives: everything up to end-of-line is one
// STRING, then pop back so the next line's keyword is seen in the default mode.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
