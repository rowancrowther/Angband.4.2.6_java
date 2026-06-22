lexer grammar LoreLexer;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:' -> pushMode(MONSTER_NAME_MODE)
        ;

COUNTS
        :   'counts:'
        ;

BLOW
        :   'blow:' -> pushMode(BLOW_MODE)
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

SPELLS
        :   'spells:' -> pushMode(SPELL_MODE)
        ;

BASE
        :   'base:'
        ;

DROP
        :   'drop:'
        ;

DROP_BASE
        :   'drop-base:'
        ;

FRIENDS
        :   'friends:'
        ;

FRIENDS_BASE
        :   'friends-base:'
        ;

MIMIC
        :   'mimic:'
        ;

fragment INTEGER_FRAGMENT
        :   ('0'..'9')+
        ;

INTEGER
        :   INTEGER_FRAGMENT
        ;

COLON
        :   ':'
        ;

STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '-' | '\'' | '*' | '&' | '~' | '<' | '>')+
        ;

TVAL
        :   ('a'..'z' | ' ')+
        ;

FRIENDS_NAME
        :   [A-Za-z0-9 \-,'<>\u00E1\u00E9\u00EB\u00EE\u00F4\u00F6\u00FA\u00FB]+
        ;

FRIENDS_BASE_NAME
        :   ('a'..'z' | 'M') ('a'..'z')+ (' ' [a-z]+)*
        ;

DICE_STRING
        :   '-'? INTEGER '+' INTEGER 'd' INTEGER 'M' INTEGER
        ;

mode MONSTER_NAME_MODE;

MONSTER_NAME
        :   (~[\r\n])+ -> popMode
        ;

mode FLAG_MODE;

FLAG
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

OR
        :   ' | '
        ;

FLAG_EOL
        :   '\r'? '\n' -> popMode, skip
        ;

mode SPELL_MODE;

SPELL
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

SPELL_EOL
        :   '\r'? '\n' -> popMode, skip
        ;

SPELL_OR
        :   ' | '
        ;

mode BLOW_MODE;

BLOW_MODE_VALUES
        :   UCASE_FLAG COLON UCASE_FLAG COLON DICE_STRING_FRAGMENT COLON INTEGER_FRAGMENT COLON INTEGER_FRAGMENT
        ;

fragment UCASE_FLAG
        :   ('A'..'Z' | '_')+
        ;

fragment DICE_STRING_FRAGMENT
        :   MINUS_FRAGMENT? INTEGER_FRAGMENT
            PLUS_FRAGMENT INTEGER_FRAGMENT
            D_FRAGMENT INTEGER_FRAGMENT
            M_FRAGMENT INTEGER_FRAGMENT
        ;

fragment PLUS_FRAGMENT
        :   '+'
        ;

fragment MINUS_FRAGMENT
        :   '-'
        ;

fragment D_FRAGMENT
        :   'd'
        ;

fragment M_FRAGMENT
        :   'M'
        ;

BLOW_EOL
        :   '\r'? '\n' -> popMode, skip
        ;
