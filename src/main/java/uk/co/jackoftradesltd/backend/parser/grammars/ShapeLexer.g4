lexer grammar ShapeLexer;

import EffectBlockLexer, CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(SHAPE_FREE_TEXT_MODE)
        ;

COMBAT
        :   'combat:'
        ;

SKILL_D_P
        :   'skill-disarm-phys:'
        ;

SKILL_D_M
        :   'skill-disarm-magic:'
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

SKILL_THROW
        :   'skill-throw:'
        ;

SKILL_DIG
        :   'skill-dig:'
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

BLOW
        :   'blow:' -> pushMode(SHAPE_FREE_TEXT_MODE)
        ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   ('\r' | '\n')+ -> skip, popMode
        ;

mode VALUE_MODE;

VALUE_MOD
        :   ('A'..'Z' | '_')+
        ;

LBRACKET
        :   '['
        ;

RBRACKET
        :   ']'
        ;

VALUE_INT
        :   '-'? ('0'..'9')+
        ;

VALUE_OR
        :   ' '? '|' ' '?
        ;

VALUE_EOL
        :   ('\r' | '\n')+ -> skip, popMode
        ;

mode SHAPE_FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
