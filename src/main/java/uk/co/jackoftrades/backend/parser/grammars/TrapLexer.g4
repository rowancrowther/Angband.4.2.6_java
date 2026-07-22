lexer grammar TrapLexer;

import CommentsAndEol, EffectBlockLexer, AngbandDisplayCharacter, Flags;


RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(DELIMITED_MODE)
        ;

APPEAR
        :   'appear:' -> pushMode(DELIMITED_MODE)
        ;

VISIBILITY
        :   'visibility:' -> pushMode(DICE_STRING_MODE)
        ;

DESC
        :   'desc:' -> pushMode(REST_OF_LINE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAGS_MODE)
        ;

MSG
        :   'msg:' -> pushMode(REST_OF_LINE)
        ;

SAVE
        :   'save:' -> pushMode(FLAGS_MODE)
        ;

MSG_GOOD
        :   'msg-good:' -> pushMode(REST_OF_LINE)
        ;

MSG_BAD
        :   'msg-bad:' -> pushMode(REST_OF_LINE)
        ;

EFFECT_XTRA
        :   'effect-xtra:'
        ;

DICE_XTRA
        :   'dice-xtra:' -> pushMode(DICE_STRING_MODE)
        ;

EXPR_XTRA
        :   'expr-xtra:' -> pushMode(EXPR_XTRA_MODE)
        ;

MSG_XTRA
        :   'msg-xtra:' -> pushMode(REST_OF_LINE)
        ;

fragment TRAP_EOL
        :   '\r'* '\n'
        ;

mode EXPR_XTRA_MODE;

/*
 * @author Rowan Crowther
 *
 * The single letter variable (normally one of B, S, D or M) used in the
 * following expr line to determine which value in the complex dice string
 * that this is made up with is replaced by the value calculated from the
 * other expr: tokens.
 */
EXPR_XTRA_CHAR
        :   ('A'..'Z')
        ;

/*
 * @author Rowan Crowther
 *
 * ":" standard field separator used in all gamedata files
 */
EXPR_XTRA_COLON
        :   ':'
        ;

/*
 * @author Rowan Crowther
 *
 * EXPR_UCASE name supporting the base value for this expression. Must be
 * one of EffectBaseType enum value'S (with an initial "EFB_"). Confirming
 * this is the importing grammar's job at the Java-action level.
 */
EXPR_XTRA_UCASE
        :   ('A'..'Z' | '_')+
        ;

/*
 * @author Rowan Crowther
 *
 * EXPR_OP name supporting the base operation for the given base name.
 * Only fires with an initial operation symbol (+, /, *, -) and then
 * grabs everything up to the end of line.
 */
EXPR_XTRA_OP
        :   ('/' | '*' | '+' | '-') ~('\r' | '\n')+
        ;

/*
 * @author Rowan Crowther
 *
 * EOL for an expr: line - pops back to default mode and skips
 * the EOL characters.
 */
EXPR_XTRA_EOL
        :   TRAP_EOL -> skip, popMode
        ;

mode DELIMITED_MODE;

DELIMITER_COLON
        :   ':'
        ;

DELIMITED_INTEGER
        :   '-'? ('0'..'9')+
        ;

DELIMITED_STRING
        :   ~('\r' | '\n' | ':')+
        ;

DELIMITED_EOL
        :   TRAP_EOL -> skip, popMode
        ;

mode REST_OF_LINE;

STRING
        :   ~('\r' | '\n')+
        ;

REST_OF_LINE_EOL
        :   TRAP_EOL -> skip, popMode
        ;

mode FLAGS_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   TRAP_EOL -> skip, popMode
        ;
