lexer grammar CurseLexer;

import CommentsAndEol, EffectBlockLexer, Flags;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

TYPE_TAG
        :   'type:' -> pushMode(FREE_TEXT_MODE)
        ;

WEIGHT
        :   'weight:'
        ;

COMBAT
        :   'combat:'
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

VALUES
        :   'values:' -> pushMode(VALUE_FLAG_MODE)
        ;

CURSE_MSG
        :   'msg:' -> pushMode(FREE_TEXT_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

CONFLICT
        :   'conflict:' -> pushMode(FREE_TEXT_MODE)
        ;

CONFLICT_FLAGS
        :   'conflict-flags:' -> pushMode(FLAG_MODE)
        ;

mode VALUE_FLAG_MODE;

VALUE_FLAG
        :   FLAG_BODY -> mode(VALUE_L_BRACKET)
        ;

mode VALUE_L_BRACKET;

L_BRACKET
        : '[' -> skip, mode(VALUE_UINT_MODE)
        ;

mode VALUE_UINT_MODE;

VALUE_INT
        :   ('-' | '+')? ('0'..'9')+ -> mode(VALUE_R_BRACKET)
        ;

mode VALUE_R_BRACKET;

R_BRACKET
        :   ']' -> skip, mode(SWITCH_VALUE)
        ;

mode SWITCH_VALUE;

VALUE_OR
        :   ' '? '|' ' '? -> mode(VALUE_FLAG_MODE)
        ;

VALUE_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   NL -> skip, popMode
        ;