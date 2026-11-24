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
// init_parse_brand() (src/obj-init.c) and struct file_parser brand_parser.
//
// A record is a set of "keyword:value" directives. This lexer only tokenizes;
// resolving the resist/vuln race flags and parsing the numeric fields is left to
// BrandAssembler; the three numeric directives (multiplier, o-multiplier, power)
// each take one UINT token, kept as raw text. Both flag directives share
// FLAG_MODE, and the free-text values
// (code, name, verb) each read in FREE_TEXT_MODE; every sub-mode emits a single
// token and pops straight back.
//
// @author Rowan Crowther

lexer grammar BrandLexer;

// CommentsAndEol skips '#' comments and blank lines and supplies the default-
// mode end-of-line handling; Flags contributes the shared FLAG_BODY fragment.
import CommentsAndEol, Flags;

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

// The value of multiplier, o-multiplier and power, which C registers as uint in
// init_parse_brand() and reads with strtoul(tok, &z, 0), so the token is kept as
// raw text and BrandAssembler reads it in base 0 (010 is 8, 0x10 is 16). The rule
// is deliberately wider than a number:
//   - an optional '+' or '-' sign. C refuses a token that starts with '-' (even
//     -0), so BrandAssembler ends the load on it; the lexer lets it through so
//     the refusal is one place, in the assembler.
//   - an optional 0x or 0X prefix, then one or more hex digits in either case.
//     The digits are not checked against the base: 1f lexes, and strtoul reads
//     the 1 and stops, as the tail below does.
//   - a tail of anything up to ':' or end of line, which C's strtoul also
//     ignores (3abc is 3, 0xg is 0). A trailing blank is part of the tail.
// What reaches the assembler with no digit (ab, abc) ends the load there. A blank
// BEFORE the number, an empty value or a lone sign is a lexer error, where C's
// strtoul would skip the blank (accepted deviation, see Accepted_Deviations.md).
// Because a hex letter can start the token, a mistyped keyword such as "extra"
// lexes as UINT here and is then refused by BrandGrammar, not by this lexer.
UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
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
