lexer grammar HintLexer;

import CommentsAndEol;

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


