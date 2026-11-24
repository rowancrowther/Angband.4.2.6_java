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

// Lexer for lib/gamedata/terrain.txt - tokenizes every terrain feature (floor,
// walls, doors, staircases, shop entrances, rubble, lava, ...) into the
// directive keywords, integers, flag names and free text that its paired parser
// TerrainFeatureGrammar assembles. Cf. src/init.c struct file_parser feat_parser,
// whose parse_feat_code/_name/_graphics/_mimic/_priority/_flags/_digging/_desc +
// *_msg handlers this pipeline ports.
//
// Three lexical modes keep otherwise-ambiguous payloads apart:
//   - default mode reads the directive keywords and bare INTEGERs;
//   - FREE_TEXT_MODE (entered by name:/*-msg:/desc:) grabs a whole line as one
//     STRING, so punctuation in prose is never mistaken for a token;
//   - FLAG_MODE (entered by code:/mimic:/flags:/resist-flag:) reads FLAG tokens
//     and the blank-or-bar separators between them. Only flags: is a list; the
//     other three take one FLAG, so a second name is a parser error.
// A FLAG is letter-initial (imports/Flags.g4 FLAG_BODY), so it can never collide
// with INTEGER even when its tail carries digits (e.g. RAND_50).
//
// @author Rowan Crowther

lexer grammar TerrainFeatureLexer;
import CommentsAndEol, Flags, AngbandDisplayCharacter;

// CODE through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
CODE
        :   'code:' -> pushMode(NO_SPACE_FLAG_MODE)
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

MIMIC
        :   'mimic:' -> pushMode(NO_SPACE_FLAG_MODE)
        ;

PRIORITY
        :   'priority:'
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

DIGGING
        :   'digging:'
        ;

WALK_MESSAGE
        :   'walk-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

RUN_MESSAGE
        :   'run-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

HURT_MESSAGE
        :   'hurt-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

DIE_MESSAGE
        :   'die-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

CONFUSED_MESSAGE
        :   'confused-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

LOOK_PREFIX
        :   'look-prefix:' -> pushMode(FREE_TEXT_MODE)
        ;

LOOK_IN_PREPOSITION
        :   'look-in-preposition:' -> pushMode(FREE_TEXT_MODE)
        ;

RESIST_FLAG
        :   'resist-flag:' -> pushMode(R_FLAG_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

// A bare negative integer - used by priority:/digging:.
INTEGER
        :   ('-' | '+')? ('0' ('x' | 'X')?)? ('a'..'f' | 'A'..'F' | '0'..'9')+
        ;

// One line of arbitrary text (name/messages/description). Consumes everything up
// to the newline, then popMode returns to the default mode for the next directive.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

// Flag names for code/mimic/flags/resist-flag. FLAG wraps the shared FLAG_BODY
// fragment. FLAG_OR is C's strtok(s, " |") separator: any run of blanks and bars,
// split into two alternatives so each takes at least one character; a blank, a
// bar, a blank and a bar arrive as two FLAG_OR tokens, which the flags rule's
// (FLAG_OR FLAG?)* loop takes. FLAG_EOL is skipped as well as popping back to the
// default mode, so no parser rule waits for it and a last line with no final
// newline needs no EOF alternative.
mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '+ '|'* ' '*
        |   ' '* '|'+ ' '*
        ;

FLAG_EOL
        :   NL -> skip, popMode
        ;

// Flag names for code/mimic/flags/resist-flag. FLAG wraps the shared FLAG_BODY
// fragment. FLAG_OR is C's strtok(s, " |") separator: any run of blanks and bars,
// split into two alternatives so each takes at least one character; a blank, a
// bar, a blank and a bar arrive as two FLAG_OR tokens, which the flags rule's
// (FLAG_OR FLAG?)* loop takes. FLAG_EOL is skipped as well as popping back to the
// default mode, so no parser rule waits for it and a last line with no final
// newline needs no EOF alternative.
mode NO_SPACE_FLAG_MODE;

NO_SPACE_FLAG
        :   FLAG_BODY
        ;

NO_SPACE_FLAG_OR
        :   ' '+ '|'* ' '*
        |   ' '* '|'+ ' '*
        ;

NO_SPACE_FLAG_EOL
        :   NEWLINE -> skip, popMode
        ;

// Flag names for code/mimic/flags/resist-flag. FLAG wraps the shared FLAG_BODY
// fragment. FLAG_OR is C's strtok(s, " |") separator: any run of blanks and bars,
// split into two alternatives so each takes at least one character; a blank, a
// bar, a blank and a bar arrive as two FLAG_OR tokens, which the flags rule's
// (FLAG_OR FLAG?)* loop takes. FLAG_EOL is skipped as well as popping back to the
// default mode, so no parser rule waits for it and a last line with no final
// newline needs no EOF alternative.
mode R_FLAG_MODE;

R_FLAG
        :   FLAG_BODY
        ;

R_FLAG_OR
        :   ' '+ '|'* ' '*
        |   ' '* '|'+ ' '*
        ;

R_FLAG_COLON
        :   ':' ~('\r' | '\n')* -> skip
        ;

R_FLAG_EOL
        :   NEWLINE -> skip, popMode
        ;
