lexer grammar EgoItemsLexer;

import CommentsAndEol, Flags, DiceStrings;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT)
        ;

INFO
        :   'info:'
        ;

ALLOC
        :   'alloc:' -> pushMode(ALLOC_MODE)
        ;

COMBAT
        :   'combat:' -> pushMode(COMBAT_MODE)
        ;

MIN_COMBAT
        :   'min-combat:'
        ;

TYPE
        :   'type:' -> pushMode(FREE_TEXT)
        ;

ITEM
        :   'item:' -> pushMode(ITEM_MODE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

FLAGS_OFF
        :   'flags-off:' -> pushMode(FLAG_MODE)
        ;

VALUES
        :   'values:' -> pushMode(VALUE_MODE)
        ;

MIN_VALUES
        :   'min-values:' -> pushMode(VALUE_MODE)
        ;

ACT
        :   'act:' -> pushMode(FREE_TEXT)
        ;

TIME
        :   'time:' -> pushMode(COMBAT_MODE)
        ;

BRAND
        :   'brand:' -> pushMode(FREE_TEXT)
        ;

SLAY
        :   'slay:' -> pushMode(FREE_TEXT)
        ;

CURSE
        :   'curse:' -> pushMode(CURSE_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

COLON
        :   ':'
        ;

mode CURSE_MODE;

CURSE_NAME
        :   ~(':' | '\r' | '\n')+
        ;

CURSE_COLON
        :   ':'
        ;

CURSE_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode COMBAT_MODE;

COMBAT_DICE
        :   SIMPLE_DICE_STRING_BODY
        ;

COMBAT_COLON
        :   ':'
        ;

COMBAT_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode ALLOC_MODE;

ALLOC_INT
        :   '-'? ('0'..'9')+
        ;

ALLOC_COLON
        :   ':'
        ;

ALLOC_TO
        :   ' to '
        ;

ALLOC_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode VALUE_MODE;

VALUE_FLAG
        :   FLAG_BODY
        ;

VALUE_LBRACKET
        :   '['
        ;

VALUE_DICE
        :   SIMPLE_DICE_STRING_BODY
        ;

VALUE_RBRACKET
        :   ']'
        ;

VALUE_OR
        :   ' '? '|' ' '?
        ;

VALUE_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode FLAG_MODE;

FLAG
        :   ' '? FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode FREE_TEXT;

STRING
        :   ~('\r' | '\n')+
        ;

FREE_TEXT_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode ITEM_MODE;

ITEM_STRING
        :   ~(':' | '\r' | '\n')+
        ;

ITEM_COLON
        :   ':'
        ;

ITEM_EOL
        :   '\r'* '\n' -> skip, popMode
        ;