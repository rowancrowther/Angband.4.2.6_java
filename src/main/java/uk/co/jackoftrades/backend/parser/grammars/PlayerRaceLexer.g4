lexer grammar PlayerRaceLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

STATS
        :   'stats:'
        ;

SKILL_DISARM_PHYS
        :   'skill-disarm-phys:'
        ;

SKILL_DISARM_MAGIC
        :   'skill-disarm-magic:'
        ;

SKILL_DEVICE
        :   'skill-device:'
        ;

SKILL_SAVE
        :   'skill-save:'
        ;

SKILL_STEALTH
        :   'skill-stealth:'
        ;

SKILL_SEARCH
        :   'skill-search:'
        ;


SKILL_MELEE
        :   'skill-melee:'
        ;


SKILL_SHOOT
        :   'skill-shoot:'
        ;


SKILL_THROW
        :   'skill-throw:'
        ;


SKILL_DIG
        :   'skill-dig:'
        ;


HITDIE
        :   'hitdie:'
        ;

EXP
        :   'exp:'
        ;

INFRAVISION
        :   'infravision:'
        ;

HISTORY
        :   'history:'
        ;

AGE
        :   'age:'
        ;

HEIGHT
        :   'height:'
        ;

WEIGHT
        :   'weight:'
        ;

OBJ_FLAGS
        :   'obj-flags:' -> pushMode(FLAG_MODE)
        ;

PLAYER_FLAGS
        :   'player-flags:' -> pushMode(FLAG_MODE)
        ;

VALUES
        :   'values:' -> pushMode(VALUE_MODE)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

COLON
        :   ':'
        ;

mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

FREE_TEXT_EOL
        :   '\r'* '\n' -> skip, popMode
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

mode VALUE_MODE;

VALUE_NAME
        :   FLAG_BODY
        ;

VALUE_LBRACKET
        :   '['
        ;

VALUE_INT
        :   '-'? ('0'..'9')+
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
