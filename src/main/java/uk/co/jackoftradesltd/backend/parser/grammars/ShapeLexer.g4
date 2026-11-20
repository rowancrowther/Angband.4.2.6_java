lexer grammar ShapeLexer;

import EffectBlockLexer, CommentsAndEol, Flags;

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
        :   'values:' -> mode(VALUE_INTRO)
        ;

BLOW
        :   'blow:' -> pushMode(SHAPE_FREE_TEXT_MODE)
        ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '+ '|'* ' '*
        |   ' '* '|'+ ' '*
        ;

FLAG_EOL
        :   NL+ -> skip, popMode
        ;

mode VALUE_INTRO;

VALUE_MOD
        :   FLAG_BODY -> mode(VALUE_INT_MODE)
        ;

VALUE_INTRO_OR
        :   ' '+ '|'* ' '*
        |   ' '* '|'+ ' '*
        ;

VALUE_INTRO_EOL
        :   NL ->skip, mode(DEFAULT_MODE)
        ;

mode VALUE_INT_MODE;

VALUE_L_BRACKET
        :   '['
        ;

VALUE_INT
        :   ('-' | '+')? ('0'..'9')+
        ;

VALUE_R_BRACKET
        :   ']' -> mode(VALUE_BREAK)
        ;

mode VALUE_BREAK;

VALUE_OR
        :  (' '+ '|'* ' '*
        |   ' '* '|'+ ' '*) -> mode(VALUE_INTRO)
        ;

VALUE_EOL
        :   NL+ -> skip, mode(DEFAULT_MODE)
        ;

mode SHAPE_FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
