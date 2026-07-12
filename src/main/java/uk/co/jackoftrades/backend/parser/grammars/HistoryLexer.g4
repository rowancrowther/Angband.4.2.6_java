lexer grammar HistoryLexer;

import CommentsAndEol;

RECORD_COUNT
        :   'record-count:'
        ;

CHART
        :   'chart:'
        ;

PHRASE
        :   'phrase:' -> pushMode(FREE_TEXT_MODE)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

COLON
        :   ':'
        ;

mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

STRING_EOL
        :   '\r'* '\n' -> popMode, skip
        ;
