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

// Lexer for lib/gamedata/brand.txt - the elemental brands weapons and ammo can
// carry (each identified by a code referenced from artifact.txt, ego_item.txt,
// object.txt and player_timed.txt). Cf. the C directive table in
// init_parse_brand() (src/obj-init.c:959) and struct file_parser brand_parser.
//
// A record is a set of "keyword:value" directives. This lexer only tokenises;
// resolving the resist/vuln race flags and parsing the numeric fields is left to
// BrandAssembler. Both flag directives share FLAG_MODE, and the free-text values
// (code, name, verb) each read in FREE_TEXT_MODE; every sub-mode emits a single
// token and pops straight back.
//
// @author Rowan Crowther

lexer grammar BrandLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling; Flags contributes the shared FLAG_BODY fragment.
import CommentsAndEol, Flags;

// "record-count:<n>" - header declaring how many brand records follow.
RECORD_COUNT
        :   'record-count:'
        ;

// "code:<CODE>" - opens a new brand record; the value (e.g. FIRE_3) is the
// cross-reference code other data files use, read as free text.
CODE
        :   'code:' -> pushMode(FREE_TEXT_MODE)
        ;

// "name:<text>" - the element's display name (e.g. lightning), which need not
// equal the element code; read as free text.
NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
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

// "verb:<text>" - verb used when a susceptible monster is hit; read as free text.
VERB
        :   'verb:' -> pushMode(FREE_TEXT_MODE)
        ;

// "resist-flag:<FLAG>" - monster race flag granting resistance to this brand.
// Read in FLAG_MODE. Independent of vuln-flag (a brand may have both).
RESIST_FLAG
        :   'resist-flag:' -> pushMode(FLAG_MODE)
        ;

// "vuln-flag:<FLAG>" - monster race flag marking special vulnerability to this
// brand. Read in FLAG_MODE. Optional and independent of resist-flag.
VULN_FLAG
        :   'vuln-flag:' -> pushMode(FLAG_MODE)
        ;

// A literal integer for the numeric directives. Sign-capable by shared suite
// convention, though brand values are always non-negative (C reads them as
// uint); range is validated in the assembler.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Value mode shared by resist-flag and vuln-flag: a single upper-case flag,
// then pop back so the following directive keyword is seen in the default mode.
mode FLAG_MODE;

FLAG
        :   FLAG_BODY -> popMode
        ;

// Value mode for the free-text directives (code, name, verb): everything up to
// end-of-line is one STRING, then pop back to the default mode.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
