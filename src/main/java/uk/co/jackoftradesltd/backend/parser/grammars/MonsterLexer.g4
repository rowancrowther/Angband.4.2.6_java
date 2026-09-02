lexer grammar MonsterLexer;

import CommentsAndEol, DiceStrings, Flags, Colours;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

PLURAL
        :   'plural:' -> pushMode(REST_OF_LINE)
        ;

BASE
        :   'base:' -> pushMode(REST_OF_LINE)
        ;

GLYPH
        :   'glyph:' -> pushMode(REST_OF_LINE)
        ;

COLOUR
        :   'color:' -> pushMode(COLOUR_MODE)
        ;

SPEED
        :   'speed:'
        ;

HIT_POINTS
        :   'hit-points:'
        ;

LIGHT
        :   'light:'
        ;

HEARING
        :   'hearing:'
        ;

ARMOUR_CLASS
        :   'armor-class:'
        ;

SLEEPINESS
        :   'sleepiness:'
        ;

DEPTH
        :   'depth:'
        ;

RARITY
        :   'rarity:'
        ;

EXPERIENCE
        :   'experience:'
        ;

BLOW
        :   'blow:' -> pushMode(BLOW_MODE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

FLAGS_OFF
        :   'flags-off:' ' '? -> pushMode(FLAG_MODE)
        ;

INNATE_FREQ
        :   'innate-freq:'
        ;

SPELL_FREQ
        :   'spell-freq:'
        ;

SPELL_POWER
        :   'spell-power:'
        ;

SPELLS
        :   'spells:'  -> pushMode(FLAG_MODE)
        ;

MESSAGE_VIS
        :   'message-vis:' -> pushMode(DELIMITED)
        ;

MESSAGE_INVIS
        :   'message-invis:' -> pushMode(DELIMITED)
        ;

MESSAGE_MISS
        :   'message-miss:' -> pushMode(DELIMITED)
        ;

DESC
        :   'desc:' -> pushMode(REST_OF_LINE)
        ;

DROP
        :   'drop:' -> pushMode(DROP_MODE)
        ;

DROP_BASE
        :   'drop-base:' -> pushMode(DROP_MODE)
        ;

MIMIC
        :   'mimic:' -> pushMode(DELIMITED)
        ;

FRIENDS
        :   'friends:' -> pushMode(FRIENDS_MODE)
        ;

FRIENDS_BASE
        :   'friends-base:' -> pushMode(FRIENDS_MODE)
        ;

SMELL
        :   'smell:'
        ;

SHAPE
        :   'shape:' -> pushMode(REST_OF_LINE)
        ;

COLOUR_CYCLE
        :   'color-cycle:' -> pushMode(DELIMITED)
        ;

MON_INTEGER
        :   '-'? ('0'..'9')+
        ;

fragment MON_EOL
        :   '\r'* '\n'
        ;

mode REST_OF_LINE;

STRING
        :   ~('\r' | '\n')+
        ;

ROL_EOL
        :   MON_EOL -> skip, popMode
        ;

mode BLOW_MODE;

BLOW_COLON
        :   ':'
        ;

BLOW_DICESTRING
        :   SIMPLE_DICE_STRING_BODY
        ;

BLOW_WORD
        :   ~('\r' | '\n' | ':')+
        ;

BLOW_EOL
        :   MON_EOL -> skip, popMode
        ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   MON_EOL -> skip, popMode
        ;

mode DELIMITED;

COLON
        :   ':'
        ;

DELIMITED_STRING
        :   ~('\r' | '\n' | ':')+
        ;

DELIMITED_EOL
        :   MON_EOL -> skip, popMode
        ;

mode FRIENDS_MODE;

FRIEND_INTEGER
        :   '-'? ('0'..'9')+
        ;

FRIEND_DICE
        :   SIMPLE_DICE_STRING_BODY
        ;

FRIEND_COLON
        :   ':'
        ;

FRIEND_NAME
        :   ~('\n' | '\r' | ':')+
        ;

FRIEND_EOL
        :   MON_EOL -> skip, popMode
        ;

mode DROP_MODE;

DROP_COLON
        :   ':'
        ;

DROP_INTEGER
        :   '-'? ('0'..'9')+
        ;

DROP_STRING
        :   ~('\r' | '\n' | ':')+
        ;

DROP_EOL
        :   MON_EOL -> popMode, skip
        ;

mode COLOUR_MODE;

COLOUR_CHAR
        :   COLOUR_CODE
        ;

COLOUR_EOL
        :   MON_EOL -> popMode, skip
        ;
