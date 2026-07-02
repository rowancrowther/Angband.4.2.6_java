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
 * Lexer for lib/gamedata/projection.txt - every "projection" (element
 * damage types like fire/cold, plus non-element ones like turn-undead,
 * stinking cloud, etc): descriptions, damage formula (numerator/
 * denominator/divisor/cap), message type, and display colour. Cf.
 * src/obj-init.c: struct file_parser projection_parser (obj-init.c:470),
 * directive table at obj-init.c:400-414 (parse_projection_code/_name/
 * _type/_desc/_player_desc/_blind_desc/_lash_desc/_numerator/_denominator/
 * _divisor/_damage_cap/_message_type/_obvious/_wake/_color).
 */
lexer grammar ProjectionLexer;

import CommentsAndEol, DiceStrings, Flags;

/*
 * @author Rowan Crowther
 *
 * record-count directive
 * cf ProjectionGrammar.g4's 'recordCount' rule
 */
RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * code directive
 * cf ProjectionGrammar.g4's 'code' rule
 */
CODE    :   'code:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * name directive
 * cf ProjectionGrammar.g4's 'name' rule
 */
NAME    :   'name:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * type directive
 * cf ProjectionGrammar.g4's 'type' rule
 */
TYPE    :   'type:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * desc directive
 * cf ProjectionGrammar.g4's 'desc' rule
 */
DESC    :   'desc:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * player-desc directive
 * cf ProjectionGrammar.g4's 'playerDesc' rule
 */
PLAYER_DESC
        :   'player-desc:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * blind-desc directive
 * cf ProjectionGrammar.g4's 'blindDesc' rule
 */
BLIND_DESC
        :   'blind-desc:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * lash-desc directive
 * cf ProjectionGrammar.g4's 'lashDesc' rule
 */
LASH_DESC
        :   'lash-desc:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * numerator directive
 * cf ProjectionGrammar.g4's 'numerator' rule
 */
NUMERATOR
        :   'numerator:'
        ;

/*
 * @author Rowan Crowther
 *
 * denominator directive
 * cf ProjectionGrammar.g4's 'denominator' rule
 */
DENOMINATOR
        :   'denominator:' -> pushMode(DICE_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * divisor directive
 * cf ProjectionGrammar.g4's 'divisor' rule
 */
DIVISOR :   'divisor:'
        ;

/*
 * @author Rowan Crowther
 *
 * damage-cap directive
 * cf ProjectionGrammar.g4's 'damageCap' rule
 */
DAMAGE_CAP
        :   'damage-cap:'
        ;

/*
 * @author Rowan Crowther
 *
 * msgt directive
 * cf ProjectionGrammar.g4's 'msgt' rule
 */
MSGT    :   'msgt:' -> pushMode(FLAG_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * obvious directive
 * cf ProjectionGrammar.g4's 'obvious' rule
 */
OBVIOUS :   'obvious:'
        ;

/*
 * @author Rowan Crowther
 *
 * wake directive
 * cf ProjectionGrammar.g4's 'wake' rule
 */
WAKE    :   'wake:'
        ;

/*
 * @author Rowan Crowther
 *
 * colour directive
 * cf ProjectionGrammar.g4's 'colour' rule
 */
COLOUR  :   'color:' -> pushMode(FREE_TEXT)
        ;

/*
 * @author Rowan Crowther
 *
 * A bare (optionally signed) integer
 * cf ProjectionGrammar.g4's 'recordCount', 'numerator', 'divisor',
 * 'damageCap', 'obvious' and 'wake' rules
 */
INTEGER :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * A mode to pull in the FLAG token that will match an enum value.
 */
mode FLAG_MODE;

/*
 * @author Rowan Crowther
 *
 * A flag token that matches a ('A'..'Z')('A'..'Z'|'0'..'9'|'_')* enum value.
 * cf ProjectionGrammar.g4's 'code' and 'msgt' rules
 */
FLAG
        :   FLAG_BODY -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * A mode to pull in the 'free-text' STRING token that will match anything up to the end of the line.
 */
mode FREE_TEXT;

/*
 * @author Rowan Crowther
 *
 * A free text string that matches everything up to the end of the line.
 * cf ProjectionGrammar.g4's 'name', 'type', 'desc', 'playerDesc', 'blindDesc',
 * 'lashDesc' and 'colour' rules
 */
STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * A mode to pull in the SIMPLE_DICE_STRING_BODY token from the DiceStrings import
 */
mode DICE_MODE;

/*
 * @author Rowan Crowther
 *
 * A token to allow a simple dice token in the form of 1+2d3M4 to be read in and
 * stored as a string, where the integers 1, 2, 3, & 4 can be any integer value.
 * cf ProjectionGrammar.g4's 'denominator' rule
 */
SIMPLE_DICE
        :   SIMPLE_DICE_STRING_BODY -> popMode
        ;