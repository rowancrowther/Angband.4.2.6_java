lexer grammar UIEntryBaseLexer;

@header {
    package uk.co.jackoftrades.backend.parser.uientrybase;
}

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

RENDERER
        :   'renderer:'
        ;

COMBINE
        :   'combine:'
        ;

CATEGORY
        :   'category:'
        ;

FLAGS
        :   'flags:'
        ;

DESC
        :   'desc:' -> pushMode(DESC_MODE)
        ;

LCASEWORD
        :   ('a'..'z'|'_'|'0'..'9')+
        ;

UCASEWORD
        :   ('A'..'Z'|'_'|'0'..'9')+
        ;

mode    DESC_MODE;
DESC_TEXT
        :   ~[\r\n]+
        ;

DESC_EOL
        :   '\r'? '\n'  -> popMode, skip
        ;