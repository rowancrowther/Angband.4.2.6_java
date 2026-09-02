lexer grammar MonsterSpellLexer;

import CommentsAndEol, EffectBlockLexer;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

MSGT
        :   'msgt:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

HIT
        :   'hit:'
        ;

POWER_CUTOFF
        :   'power-cutoff:'
        ;

LORE
        :   'lore:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

LORE_COLOUR_BASE
        :   'lore-color-base:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

LORE_COLOUR_RESIST
        :   'lore-color-resist:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

LORE_COLOUR_IMMUNE
        :   'lore-color-immune:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

MESSAGE_SAVE
        :   'message-save:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

MESSAGE_VIS
        :   'message-vis:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

MESSAGE_INVIS
        :   'message-invis:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

MESSAGE_MISS
        :   'message-miss:' -> pushMode(MS_FREE_TEXT_MODE)
        ;

mode MS_FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

FREE_TEXT_EOL
        :   '\r'* '\n'  -> skip, popMode
        ;
