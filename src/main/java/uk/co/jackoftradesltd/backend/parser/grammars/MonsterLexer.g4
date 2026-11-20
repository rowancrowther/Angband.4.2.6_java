lexer grammar MonsterLexer;

import CommentsAndEol, DiceStrings, Flags, Colours;

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
        :   'speed:' -> pushMode(INTEGER_MODE)
        ;

HIT_POINTS
        :   'hit-points:' -> pushMode(INTEGER_MODE)
        ;

LIGHT
        :   'light:' -> pushMode(INTEGER_MODE)
        ;

HEARING
        :   'hearing:' -> pushMode(INTEGER_MODE)
        ;

ARMOUR_CLASS
        :   'armor-class:' -> pushMode(INTEGER_MODE)
        ;

SLEEPINESS
        :   'sleepiness:' -> pushMode(INTEGER_MODE)
        ;

DEPTH
        :   'depth:' -> pushMode(INTEGER_MODE)
        ;

RARITY
        :   'rarity:' -> pushMode(INTEGER_MODE)
        ;

EXPERIENCE
        :   'experience:' -> pushMode(INTEGER_MODE)
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
        :   'innate-freq:' -> pushMode(INTEGER_MODE)
        ;

SPELL_FREQ
        :   'spell-freq:' -> pushMode(INTEGER_MODE)
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
        :   'drop:' -> mode(DROP_TYPE_STRING_MODE)
        ;

DROP_BASE
        :   'drop-base:' -> mode(DROP_NAME_STRING_MODE)
        ;

MIMIC
        :   'mimic:' -> pushMode(DELIMITED)
        ;

FRIENDS
        :   'friends:' -> mode(FRIEND_CHANCE)
        ;

FRIENDS_BASE
        :   'friends-base:' -> mode(FRIEND_CHANCE)
        ;

SMELL
        :   'smell:' -> pushMode(INTEGER_MODE)
        ;

SHAPE
        :   'shape:' -> pushMode(REST_OF_LINE)
        ;

COLOUR_CYCLE
        :   'color-cycle:' -> pushMode(DELIMITED)
        ;

MON_UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

mode REST_OF_LINE;

STRING
        :   ~('\r' | '\n')+
        ;

ROL_EOL
        :   NL -> skip, popMode
        ;

mode INTEGER_MODE;

INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ -> popMode
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
        :   NL -> skip, popMode
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
        :   NL -> skip, popMode
        ;

mode DELIMITED;

COLON
        :   ':'
        ;

DELIMITED_STRING
        :   ~('\r' | '\n' | ':')+
        ;

DELIMITED_EOL
        :   NEWLINE -> skip, popMode
        ;

mode FRIENDS_MODE;

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
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode FRIEND_CHANCE;

FRIEND_UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')* -> mode(FRIENDS_MODE)
        ;

mode DROP_MODE;

DROP_COLON
        :   ':' -> skip
        ;

DROP_UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

DROP_EOL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode DROP_TYPE_STRING_MODE;

DROP_TYPE_STRING
        :   ~('\r' | '\n' | ':')+
        ;

DROP_COLON_STR
        :   ':' -> skip, mode(DROP_NAME_STRING_MODE)
        ;

mode DROP_NAME_STRING_MODE;

DROP_NAME_STRING
        :   ~('\r' | '\n' | ':')+
        ;

DROP_COLON_NAME_STR
        :   ':' -> skip, mode(DROP_MODE)
        ;

mode COLOUR_MODE;

COLOUR_LINE
        :   ~('\n' | '\r')+
        ;

COLOUR_EOL
        :   NEWLINE -> popMode, skip
        ;
