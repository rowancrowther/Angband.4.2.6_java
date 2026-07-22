lexer grammar HintLexer;

import CommentsAndEol;

RECORD_COUNT
        :   'record-count:'
        ;

HINT
        :   'H:' -> pushMode(STRING_MODE)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

mode STRING_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;


