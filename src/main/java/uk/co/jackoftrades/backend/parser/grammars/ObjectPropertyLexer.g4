lexer grammar ObjectPropertyLexer;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:'
        ;

TYPE
        :   'type:'
        ;

SUBTYPE
        :   'subtype:'
        ;

ID_TYPE
        :   'id-type:' -> pushMode(FREE_TEXT_MODE)
        ;

CODE
        :   'code:'
        ;

POWER
        :   'power:'
        ;

MULT
        :   'mult:'
        ;

TYPE_MULT
        :   'type-mult:'
        ;

ADJECTIVE
        :   'adjective:'
        ;

NEG_ADJECTIVE
        :   'neg-adjective:'
        ;

MSG
        :   'msg:' -> pushMode(FREE_TEXT_MODE)
        ;

BINDUI
        :   'bindui:'
        ;

DESC
        :   'desc:'
        ;

COLON
        :   ':'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

LTHAN
        :   '<'
        ;

GTHAN
        :   '>'
        ;

LCASE
        :   ('a'..'z' | '0'..'9' | '_')+
        ;

UCASE
        :   ('A'..'Z' | '_')+
        ;

mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

EOL_POP
        :   '\r'* '\n' -> popMode, skip
        ;

mode DELIMITER;

DELIM_INTEGER
        :   '-'? ('0'..'9')+
        ;

STRING_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

DELIM_COLON
        :   ':'
        ;

END_SKIP
        :   '\r'* '\n' -> skip, popMode
        ;

