lexer grammar QuestLexer;

import CommentsAndEol;

NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

LEVEL
        :   'level:'
        ;

RACE
        :   'race:' -> pushMode(REST_OF_LINE)
        ;

NUMBER
        :   'number:'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

mode REST_OF_LINE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
