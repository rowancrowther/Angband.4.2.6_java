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
 * Standalone lexer grammar for the Angband dice-string mini-language (e.g.
 * "2d8", "1+1d3", "M80", "4d$S", "$Dd5", "$Dd$S").
 *
 * It has no @header instructions or java-domain-type coupling.
 *
 * It allows for two types of DICE_STRINGS, simple and complex. Simple values
 * are of a portion of the full form "-<base>+<dice>d<sides>M<bonus>", where
 * each of the <> tokens are unsigned DICE_INTEGERs. Complex values follow the same
 * structure, but one or more of the <> tokens are replaced by a $<char> token,
 * where <char> is usually one of B, D, M, or S (base, dice, m-bonus, sides).
 */
lexer grammar DiceStrings;

/*
 * @author Rowan Crowther
 *
 * Dice-count/sides separator, always lowercase 'd'
 */
fragment DICE_D
        :   'd'
        ;

/*
 * @author Rowan Crowther
 *
 * M-bonus marker, normally 'M' or 'm'
 */
fragment DICE_M
        :   'M'
        |   'm'
        ;

/*
 * @author Rowan Crowther
 *
 * Literal non-negative (unsigned) DICE_INTEGER.
 */
fragment DICE_INTEGER
        :   ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * Bound, dollar-char variable such as '$M'.
 */
fragment DICE_DOLLAR_LETTER
        :   '$' ('A'..'Z')
        ;

/*
 * @author Rowan Crowther
 *
 * The complex-dice-string permutation space. Similar to the simple dice
 * string, but where the DICE_INTEGER values can be replaced in one or more
 * places with a $X string value.
 */
fragment COMPLEX_DICE_STRING_BODY
        :   (   '-'? DICE_DOLLAR_LETTER '+' DICE_INTEGER DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_DOLLAR_LETTER '+' DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?

            |    '-'? DICE_INTEGER '+' DICE_INTEGER DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_INTEGER '+' DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?
            |   DICE_INTEGER DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?
            |   DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?

            |   '-'? DICE_INTEGER '+' DICE_INTEGER DICE_D DICE_INTEGER DICE_M DICE_DOLLAR_LETTER
            |   DICE_INTEGER DICE_D DICE_INTEGER DICE_M DICE_DOLLAR_LETTER
            |   DICE_D DICE_INTEGER DICE_M DICE_DOLLAR_LETTER
            |   DICE_INTEGER DICE_M DICE_DOLLAR_LETTER
            |   DICE_M DICE_DOLLAR_LETTER

            |   '-'? DICE_INTEGER '+' DICE_DOLLAR_LETTER DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_DOLLAR_LETTER '+' DICE_DOLLAR_LETTER DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   DICE_DOLLAR_LETTER DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   DICE_DOLLAR_LETTER DICE_D DICE_INTEGER (DICE_M DICE_DOLLAR_LETTER)?

            |   '-'? DICE_INTEGER '+' DICE_DOLLAR_LETTER DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?
            |   DICE_DOLLAR_LETTER DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_INTEGER)?
            |   DICE_DOLLAR_LETTER DICE_D DICE_DOLLAR_LETTER (DICE_M DICE_DOLLAR_LETTER)?

            |   '-'? DICE_DOLLAR_LETTER '+' DICE_M DICE_DOLLAR_LETTER
            |   '-'? DICE_DOLLAR_LETTER '+' DICE_M DICE_INTEGER
            |   '-'? DICE_INTEGER       '+' DICE_M DICE_DOLLAR_LETTER
            )
        ;

/*
 * @author Rowan Crowther
 *
 * The simple-dice-string permutation space: base (absent, literal)
 * '+' dice-count (absent, literal) 'd' sides (literal) ('M' bonus (literal))?
 * - plus the bare "M-bonus"-only or base '+' M-bonus. Exposed as a fragment (see
 * top-of-file comment) so all grammars can reuse this exact body in
 * their own mode-scoped wrapper without redeclaring it.
 */
fragment SIMPLE_DICE_STRING_BODY
        :   ( // literal base, literal/absent dice-count, literal sides
                '-'? DICE_INTEGER '+' DICE_INTEGER DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_INTEGER '+' DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_INTEGER DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   DICE_D DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_INTEGER (DICE_M DICE_INTEGER)?
            |   '-'? DICE_INTEGER '+' DICE_M DICE_INTEGER
            |   DICE_M DICE_INTEGER
            )
        ;

/*
 * @author Rowan Crowther
 *
 * A simple dice string token, as opposed to a fragment, allowing consumers
 * to swallow dice expressions as one token.
 */
SIMPLE_DICE_STRING
        :   SIMPLE_DICE_STRING_BODY
        ;

/*
 * @author Rowan Crowther
 *
 * A complex dice string token, as opposed to a fragment, allowing consumers
 * to swallow dice expressions as one token.
 */
COMPLEX_DICE_STRING
        :   COMPLEX_DICE_STRING_BODY
        ;
