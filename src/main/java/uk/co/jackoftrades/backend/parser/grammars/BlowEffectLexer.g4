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
 * Lexer for lib/gamedata/blow_effects.txt, the monster melee blow effects.
 *
 * Every directive is a keyword ending in ':' and every record runs from one name:
 * to the next. The directives divide cleanly in two:
 *
 *  - power: and eval: take a number, so they stay in the default mode and let
 *    INTEGER match; and
 *  - the rest take free text that may contain spaces and punctuation (a desc: of
 *    "inflict Black Breath", a colour of "Light Green"), so they push into
 *    FREE_TEXT_MODE, where STRING swallows the remainder of the line whole.
 *
 * Because every free-text directive is the last thing on its line, EOL_POP can
 * pop the mode on the newline and no directive ever has to pop it explicitly.
 */
lexer grammar BlowEffectLexer;

import CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * The record-count header this port adds to each data file, giving the number of
 * records that should follow. Validated softly by the reader, not here.
 */
RECORD_COUNT
        :   'record-count:'
        ;

/*
 * @author Rowan Crowther
 *
 * Opens a record. The name is free text only so that one rule can serve every
 * directive that runs to end-of-line; the values themselves are code-like
 * (HURT, DRAIN_CHARGES) and are matched against the monster.txt references.
 */
NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * The blow's contribution to the monster's to-hit chance. Numeric, so no mode change.
 */
POWER
        :   'power:'
        ;

/*
 * @author Rowan Crowther
 *
 * The blow's weight when rating how dangerous a monster is. Numeric, so no mode change.
 */
EVAL
        :   'eval:'
        ;

/*
 * @author Rowan Crowther
 *
 * The monster-recall description, e.g. "reduce strength". Contains spaces.
 */
DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player has no protection. Colour names contain
 * spaces ("Light Green"), hence free text.
 *
 * Note the American spelling in the literal: the data file is upstream Angband's
 * and writes "color", while the rule name follows this port's British spelling.
 */
LORE_COLOUR_BASE
        :   'lore-color-base:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player resists the effect.
 */
LORE_COLOUR_RESIST
        :   'lore-color-resist:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * Lore colour used when the player resists the effect strongly, i.e. is immune.
 */
LORE_COLOUR_IMMUNE
        :   'lore-color-immune:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * What kind of player attribute protects against the effect - "element", "flag",
 * "theft" and so on. Determines how the following resist: is resolved.
 */
EFFECT_TYPE
        :   'effect-type:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * The precise flag or element that protects against the effect. Which of the two
 * it names depends on the effect-type: above, so the lexer cannot tell them apart
 * and the assembler resolves it.
 */
RESIST
        :   'resist:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * The projection this effect translates to when delivered as a lash.
 */
LASH
        :   'lash-type:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * A whole number, optionally negative. Only power: and eval: use it; neither is
 * negative in the shipped file, but the sign is accepted for consistency with
 * the other numeric grammars in this suite.
 */
INTEGER
        :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * The mode entered after any directive whose value runs to the end of the line.
 */
mode FREE_TEXT_MODE;

/*
 * @author Rowan Crowther
 *
 * Everything up to but not including the line ending. Spaces and punctuation are
 * all part of the value, so this is a single token rather than a word sequence.
 */
STRING
        :   ~('\r' | '\n')+
        ;

/*
 * @author Rowan Crowther
 *
 * The line ending that closes a free-text value. Skipped, and pops back to the
 * default mode so the next directive keyword is matched normally.
 */
EOL_POP
        :   '\r'* '\n' -> skip, popMode
        ;
