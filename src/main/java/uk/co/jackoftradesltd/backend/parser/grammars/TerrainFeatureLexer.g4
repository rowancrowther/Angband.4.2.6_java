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

// Lexer for lib/gamedata/terrain.txt - tokenises every terrain feature (floor,
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
//   - FLAG_MODE (entered by code:/mimic:/flags:/resist-flag:) reads '|'-separated
//     FLAG tokens.
// A FLAG is letter-initial (imports/Flags.g4 FLAG_BODY), so it can never collide
// with INTEGER even when its tail carries digits (e.g. RAND_50).
//
// @author Rowan Crowther

lexer grammar TerrainFeatureLexer;
import CommentsAndEol, Flags, AngbandDisplayCharacter;

// CODE through DESC below: one literal directive-keyword token each,
// matching the rule of the same purpose above.
RECORD_COUNT
        : 'record-count:'
        ;

CODE
        :   'code:' -> pushMode(FLAG_MODE)
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

MIMIC
        :   'mimic:' -> pushMode(FLAG_MODE)
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
        :   'resist-flag:' -> pushMode(FLAG_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

// A bare non-negative integer - used by priority:/digging:.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// One line of arbitrary text (name/messages/description). Consumes everything up
// to the newline, then popMode returns to the default mode for the next directive.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

// A '|'-separated list of flag names (code/mimic/flags/resist-flag). FLAG wraps
// the shared FLAG_BODY fragment; FLAG_OR is the separator (optional surrounding
// spaces); FLAG_EOL ends the list and popModes back to the default mode.
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
