lexer grammar NamesLexer;

import CommentsAndEol;

RECORD_COUNT
        :   'record-count:'
        ;

SECTION
        :   'section:'
        ;

WORD
        :   'word:' -> pushMode(STRING_MODE)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

mode STRING_MODE;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;
