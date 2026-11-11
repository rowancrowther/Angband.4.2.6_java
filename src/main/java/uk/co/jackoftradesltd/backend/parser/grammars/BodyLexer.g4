lexer grammar BodyLexer;

import CommentsAndEol;

BODY
        :   'body:' -> pushMode(FREE_TEXT_MODE)
        ;

SLOT
        :   'slot:'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

SLOT_NAME
        :   ('A'..'Z' | '_')+
        ;

COLON
        :   ':' -> pushMode(FREE_TEXT_MODE)
        ;

mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

FREE_TEXT_EOL
        :   NL -> skip, popMode
        ;