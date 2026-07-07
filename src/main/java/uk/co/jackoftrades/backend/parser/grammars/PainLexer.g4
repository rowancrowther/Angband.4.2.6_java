lexer grammar PainLexer;

import CommentsAndEol;

RECORD_COUNT
        :   'record-count:'
        ;

TYPE
        :   'type:'
        ;

MESSAGE
        :   'message:' -> pushMode(FREE_TEXT_MODE)
        ;

// A bare non-negative integer.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Free-running message text, including the "[s]"/"[ies|y]" pluralization-
// suffix and '|' alternation syntax pain messages use (e.g. "cr[ies|y] out
// in pain").
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
