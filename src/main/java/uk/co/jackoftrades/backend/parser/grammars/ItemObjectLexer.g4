lexer grammar ItemObjectLexer;

import CommentsAndEol, Flags, EffectBlockLexer, AngbandDisplayCharacter, DiceStrings;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

TYPE
        :   'type:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

LEVEL
        :   'level:'
        ;

WEIGHT
        :   'weight:'
        ;

COST
        :   'cost:'
        ;

ATTACK
        :   'attack:' -> pushMode(OBJECT_RAND_MODE)
        ;

ARMOUR
        :   'armor:' -> pushMode(OBJECT_RAND_MODE)
        ;

ALLOC
        :   'alloc:' -> pushMode(ALLOC_MODE)
        ;

CHARGES
        :   'charges:' -> pushMode(OBJECT_RAND_MODE)
        ;

PILE
        :   'pile:' -> pushMode(OBJECT_RAND_MODE)
        ;

POWER
        :   'power:'
        ;

MSG
        :   'msg:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

VIS_MSG
        :   'vis-msg:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAGS_MODE)
        ;

VALUES
        :   'values:' -> pushMode(VALUES_MODE)
        ;

BRAND
        :   'brand:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

SLAY
        :   'slay:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

CURSE
        :   'curse:' -> pushMode(CURSE_MODE)
        ;

PVAL
        :   'pval:' -> pushMode(OBJECT_RAND_MODE)
        ;

DESC
        :   'desc:' -> pushMode(OBJ_FREE_TEXT_MODE)
        ;

mode CURSE_MODE;

CURSE_STRING
        :   ~(':' | '\r' | '\n')+
        ;

CURSE_COLON
        :   ':' -> mode(CURSE_POWER_MODE)
        ;

mode CURSE_POWER_MODE;

CURSE_INT
        :   '-'? ('0'..'9')+
        ;

CURSE_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode FLAGS_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
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
        :   ' '? 'to' ' '?
        ;

ALLOC_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode OBJ_FREE_TEXT_MODE;

OBJECT_STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

mode VALUES_MODE;

VALUE_TYPE
        :   FLAG_BODY
        ;

VALUE_OR
        :   ' '? '|' ' '?
        ;

VALUE_LBRACKET
        :   '['
        ;

VALUE_INT
        :   SIMPLE_DICE_STRING
        ;

VALUE_RBRACKET
        :   ']'
        ;

VALUE_EOL
        :   '\r'* '\n' -> skip, popMode
        ;

mode OBJECT_RAND_MODE;

RAND_VALUE
        :   SIMPLE_DICE_STRING_BODY
        ;

RAND_COLON
        :   ':'
        ;

RAND_EOL
        :   '\r'* '\n' -> skip, popMode
        ;
