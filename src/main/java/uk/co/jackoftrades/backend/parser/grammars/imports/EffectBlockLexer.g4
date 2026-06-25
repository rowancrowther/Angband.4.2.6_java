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
 * Standalone lexer for the effect class. Imports DiceStrings and
 * CommentsAndEol. Paired with EffectBlock.g4
 *
 * This is a standalone module to be used whenever an Effect is called
 * for.
 */

lexer grammar EffectBlockLexer;
import DiceStrings, CommentsAndEol;

/*
 * @author Rowan Crowther
 *
 * "effect:" token - introduces the primary effect directive.
 * cf EffectBlock.g4's 'effect' rule.
 */
EFFECT
        :   'effect:'
        ;

/*
 * @author Rowan Crowther
 *
 * "effect-msg:" token - introduces the effect-msg directive.
 * Pushes into FREE_TEXT_MODE which pulls in all the characters
 * up to the end of the line.
 * cf EffectBlock.g4's 'effectMsg' rule.
 */
EFFECT_MESSAGE
        :   'effect-msg:' -> pushMode(FREE_TEXT_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * "dice:" token - introduces the dice-string directive.
 *
 * pushes into DICE_STRING_MODE to handle the production of the dice
 * string token.
 *
 * Note, dice allows two different values, a 'complex' one and a 'simple'
 * one. The 'complex' one is one where one or more of the integer values
 * are replaced by a $char placeholder variable, which depends on a following
 * expr line. Take care when assuming that a dice is simple
 *
 * cf EffectBlock.g4's 'dice' rule.
 */
DICE
        :   'dice:' -> pushMode(DICE_STRING_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * "time:" token - introduces the time directive. This should
 * return a string representation of a 'simple' dice-string.
 *
 * pushes into DICE_STRING_MODE to handle the production of the dice
 * string token.
 *
 * cf EffectBlock.g4's 'time' rule.
 */
TIME
        :   'time:' -> pushMode(DICE_STRING_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * "effect-yx:" token - introduces the primary effect-yx directive.
 *
 * cf EffectBlock.g4's 'effectYX' rule.
 */
EFFECT_YX
        :   'effect-yx:'
        ;

/*
 * @author Rowan Crowther
 *
 * "expr:" token - introduces the expr (expression) directive.
 *
 * This is used in the dice rule in EffectBlock.g4.
 *
 * Switches into EXPR_MODE for the letter, base_name and operations
 * structures.
 *
 * cf EffectBlock.g4's 'expr' and 'dice' rules.
 */
EXPR
        :   'expr:' -> pushMode(EXPR_MODE)
        ;

/*
 * @author Rowan Crowther
 *
 * ":" standard field separator used in all gamedata files
 */
COLON
        :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * An uppercase symbol used to determine TYPE/SUBTYPE in EFFECT lines.
 */
UCASE
        :   ('A'..'Z' | '_')+
        ;

/*
 * @author Rowan Crowther
 *
 * A bare signed integer - used for effect:'s optional radius/other segments.
 */
INTEGER
        :   '-'? ('0'..'9')+
        ;

/*
 * @author Rowan Crowther
 *
 * Entered after effect-msg: to allow reading of an entire line
 * up to and including any special characters.
 */
mode FREE_TEXT_MODE;

/*
 * @author Rowan Crowther
 *
 * A token which greedily eats all non-new line characters and
 * returns them as a single token.
 */
FREE_TEXT
        :   ~('\r' | '\n')+ -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * Entered after dice: to tokenise a dice as either a single or complex
 * string token. Used as a wrapper to the DiceStrings.g4 grammar
 */
mode DICE_STRING_MODE;

/*
 * @author Rowan Crowther
 *
 * A simple dice string token, one without any '$'char variables in it.
 * Currently resulting String token can be parsed by Random.parseStr().
 */
DICE_SIMPLE_VALUE
        :   SIMPLE_DICE_STRING_BODY -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * A complex dice string token, involving one or more '$'char variables in it.
 * Currently resulting String token cannot be parsed.
 */
DICE_COMPLEX_VALUE
        :   COMPLEX_DICE_STRING_BODY -> popMode
        ;

/*
 * @author Rowan Crowther
 *
 * Entered after expr: to tokenise "letter:BASE_NAME:operation"
 */
mode EXPR_MODE;

/*
 * @author Rowan Crowther
 *
 * The single letter variable (normally one of B, S, D or M) used in the
 * following expr line to determine which value in the complex dice string
 * that this is made up with is replaced by the value calculated from the
 * other expr: tokens.
 */
EXPR_CHAR
        :   ('A'..'Z')
        ;

/*
 * @author Rowan Crowther
 *
 * ":" standard field separator used in all gamedata files
 */
EXPR_COLON
        :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * EXPR_UCASE name supporting the base value for this expression. Must be
 * one of EffectBaseType enum value'S (with an initial "EFB_"). Confirming
 * this is the importing grammar's job at the Java-action level.
 */
EXPR_UCASE
        :   ('A'..'Z' | '_')+
        ;

/*
 * @author Rowan Crowther
 *
 * EXPR_OP name supporting the base operation for the given base name.
 * Only fires with an initial operation symbol (+, /, *, -) and then
 * grabs everything up to the end of line.
 */
EXPR_OP
        :   ('/' | '*' | '+' | '-') ~('\r' | '\n')+
        ;

/*
 * @author Rowan Crowther
 *
 * EOL for an expr: line - pops back to default mode and skips
 * the EOL characters.
 */
EXPR_EOL
        :   '\r'* '\n' -> popMode, skip
        ;