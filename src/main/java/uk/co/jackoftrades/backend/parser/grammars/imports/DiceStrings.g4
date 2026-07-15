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
 * "2d8", "1+1d3", "M80", "4d$S", "$Dd5", "$Dd$S", "$B+d$S").
 *
 * It has no @header instructions or java-domain-type coupling.
 *
 * === The dice-string shape ===
 *
 * A dice string is the port of the C original's z-dice.c format (parsed there
 * by dice_parse_string / dice_parse_state_transition). Ignoring the two
 * features no gamedata file currently uses (see "Deliberate omissions" below),
 * that format is exactly:
 *
 *     ['-'? base '+']   [count] 'd' sides   [('M'|'m') bonus]
 *
 * where every one of the four numeric slots (base, count, sides, bonus) is
 * independently either a literal integer or a '$'-variable, and:
 *
 *   - a base is ONLY ever present when immediately followed by '+' (a lone
 *     leading number with nothing after it is a base; a leading number
 *     followed by 'd' is a dice COUNT, not a base - this is why the C needs a
 *     lookahead state machine);
 *   - 'd' with no count in front of it means one die (C's z-dice.c:414-419);
 *   - the whole thing collapses to a bare base ("5"), a bare bonus ("M4"), or
 *     any combination of the three segments.
 *
 * === Simple vs complex, and why the bodies look identical ===
 *
 * Two flavours of the SAME structure are exposed:
 *
 *   - SIMPLE_DICE_STRING(_BODY): every numeric slot is a literal integer
 *     (DICE_SIMPLE_NUMBER). A simple value carries no forward reference and can
 *     be evaluated immediately (e.g. Random.parseStr()).
 *   - COMPLEX_DICE_STRING(_BODY): every numeric slot may ALSO be a '$'-variable
 *     (DICE_ANY_NUMBER), which refers forward to an expr: line and so cannot be
 *     evaluated until that line is resolved.
 *
 * The only difference between the two bodies is which "number" fragment fills
 * the slots - the structural skeleton (the four alternatives) is written once
 * per flavour and is otherwise character-for-character the same. This replaces
 * the previous hand-enumerated permutation list, which had to spell out every
 * literal-vs-variable combination by hand across all four slots and, being a
 * flat list, silently omitted the "$base + d$sides" case ("$B+d$S") that eight
 * spells in class.txt use. Factoring the slot into a fragment makes every
 * combination fall out of the grammar by construction, so no combination can
 * be missed.
 *
 * Note that DICE_ANY_NUMBER admits a literal integer too, so COMPLEX_* will
 * also match a variable-free string. That is intentional and safe BECAUSE the
 * simple token is always declared before the complex token wherever the two
 * are consumed together (here, and in the DICE_STRING_MODE of the importing
 * lexers). ANTLR takes the longest match and breaks ties by declaration order,
 * so a variable-free string is claimed by SIMPLE_* (equal length, declared
 * first), and COMPLEX_* only wins when a '$' makes the simple body fail to
 * cover the whole string.
 *
 * === Deliberate omissions vs the full C grammar ===
 *
 *   - the '&' rounding marker (C accepts it after sides / after a variable);
 *   - multi-character variable names (C's DICE_DOLLAR_LETTER equivalent can run
 *     on: "$AB..."). DICE_DOLLAR_LETTER here is a single upper-case letter.
 *
 * Neither appears in any current lib/gamedata file, and neither was supported
 * by the previous version of this grammar; they are called out here so a future
 * data file that needs them has a documented starting point.
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
 * A single numeric slot in a SIMPLE dice string: a literal integer only, with
 * no forward '$'-variable reference. Used to fill every slot of
 * SIMPLE_DICE_STRING_BODY.
 */
fragment DICE_SIMPLE_NUMBER
        :   DICE_INTEGER
        ;

/*
 * @author Rowan Crowther
 *
 * A single numeric slot in a COMPLEX dice string: either a literal integer or a
 * '$'-variable. Used to fill every slot of COMPLEX_DICE_STRING_BODY. This is the
 * sole point of difference between the simple and complex bodies.
 */
fragment DICE_ANY_NUMBER
        :   DICE_INTEGER
        |   DICE_DOLLAR_LETTER
        ;

/*
 * @author Rowan Crowther
 *
 * The complex-dice-string body: the canonical dice shape with every numeric
 * slot widened to DICE_ANY_NUMBER (integer OR '$'-variable). The four
 * alternatives are:
 *
 *   1. base '+' (dice and/or bonus)   e.g. "$B+d$S", "$B+3d6", "5+1d3", "$B+m$M"
 *   2. dice, no base                  e.g. "$Dd$S", "2d6", "d$S"
 *   3. bonus only                     e.g. "M$M"
 *   4. base only                      e.g. "$B", "-5"
 *
 * See the top-of-file note on why this also matches variable-free strings and
 * why that is harmless given simple-before-complex declaration order.
 */
fragment COMPLEX_DICE_STRING_BODY
        :   '-'? DICE_ANY_NUMBER '+' ( DICE_ANY_NUMBER? DICE_D DICE_ANY_NUMBER (DICE_M DICE_ANY_NUMBER)?
                                     | DICE_M DICE_ANY_NUMBER )
        |   DICE_ANY_NUMBER? DICE_D DICE_ANY_NUMBER (DICE_M DICE_ANY_NUMBER)?
        |   DICE_M DICE_ANY_NUMBER
        |   '-'? DICE_ANY_NUMBER
        ;

/*
 * @author Rowan Crowther
 *
 * The simple-dice-string body: the identical canonical dice shape as
 * COMPLEX_DICE_STRING_BODY, but with every numeric slot restricted to a literal
 * integer (DICE_SIMPLE_NUMBER) - i.e. no '$'-variables. Exposed as a fragment so
 * all grammars can reuse this exact body in their own mode-scoped wrapper
 * without redeclaring it.
 */
fragment SIMPLE_DICE_STRING_BODY
        :   '-'? DICE_SIMPLE_NUMBER '+' ( DICE_SIMPLE_NUMBER? DICE_D DICE_SIMPLE_NUMBER (DICE_M DICE_SIMPLE_NUMBER)?
                                        | DICE_M DICE_SIMPLE_NUMBER )
        |   DICE_SIMPLE_NUMBER? DICE_D DICE_SIMPLE_NUMBER (DICE_M DICE_SIMPLE_NUMBER)?
        |   DICE_M DICE_SIMPLE_NUMBER
        |   '-'? DICE_SIMPLE_NUMBER
        ;

/*
 * @author Rowan Crowther
 *
 * A simple dice string token, as opposed to a fragment, allowing consumers
 * to swallow dice expressions as one token. Declared BEFORE COMPLEX_DICE_STRING
 * so a variable-free string is claimed here (see top-of-file note).
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
