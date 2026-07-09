lexer grammar CurseLexer;

import CommentsAndEol, EffectBlockLexer, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

TYPE
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
        :   'values:' -> pushMode(VALUES_MODE)
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

mode VALUES_MODE;

LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;

VALUE_FLAG
        :   FLAG_BODY
        ;

VALUE_OR
        :   ' '? '|' ' '?
        ;

VALUE_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

VALUE_INT
        :   '-'? ('0'..'9')+
        ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   '\r'* '\n' -> skip, popMode
        ;