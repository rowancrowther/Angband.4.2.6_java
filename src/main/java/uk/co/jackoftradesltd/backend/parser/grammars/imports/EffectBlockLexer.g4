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
        :   'effect:' -> mode(TYPE_MODE)
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
        :   'effect-yx:' -> pushMode(YX_INTEGER_MODE)
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
 * An "int" or "uint" field value, read the way C's strtol/strtoul in base 0 read one. Used for
 * effect:'s optional radius/other segments, effect-yx:'s y and x, and by the grammars that
 * import this lexer for their own integer directives (power-cutoff:, level:, weight: and so on).
 *
 * The token is:
 *  - an optional '+' or '-' sign. A '-' is kept as text so the assembler can refuse it where C
 *    refuses it (a uint, "-0" included).
 *  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with or without the
 *    prefix, so "ff" lexes here; the assembler refuses it because strtol finds no digit in it.
 *  - any tail up to a ':' or the end of the line, because strtol stops at the first character it
 *    cannot use and ignores the rest ("5abc" is 5, and a trailing blank or tab is part of the
 *    tail). A decimal digit run is the same token, so 010 is read as octal by the assembler,
 *    not here.
 *
 * Nothing allows a blank before the number, or an empty ':' field (trailing or doubled), where C
 * skips both; see docs/Accepted_Deviations.md, "Numbers in every data file".
 *
 * Token EFFECT_INTEGER coded before 261112, commented in full on 261112.
 */
EFFECT_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

mode YX_INTEGER_MODE;

YX_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

YX_COLON
        :   ':' -> skip
        ;

YX_EOL
        :   NL -> skip, popMode
        ;

mode TYPE_MODE;

/*
 * @author Rowan Crowther
 *
 * An uppercase symbol used to determine TYPE/SUBTYPE in EFFECT lines.
 */
TYPE
        :   ('a'..'z' | 'A'..'Z' | '_' | '-')+
        ;

TYPE_MODE_COLON
        :   ':' -> skip, mode(SUBTYPE_MODE)
        ;

TYPE_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode SUBTYPE_MODE;

/*
 * @author Rowan Crowther
 *
 * An uppercase symbol used to determine TYPE/SUBTYPE in EFFECT lines.
 */
SUBTYPE
        :   ('a'..'z' | 'A'..'Z' | '_' | '-')+
        ;

SUBTYPETYPE_MODE_COLON
        :   ':' -> skip, mode(EFFECT_RAD_AND_PARM)
        ;

SUBTYPE_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode EFFECT_RAD_AND_PARM;

R_P_EFFECT_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

R_P_COLON
        :   ':' -> skip
        ;

R_P_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
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
 * Entered after dice: to tokenize a dice as either a single or complex
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
 * Entered after expr: to tokenize "letter:BASE_NAME:operation"
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
        :   NL -> popMode, skip
        ;