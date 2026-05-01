grammar ObjectPropertyFormatter;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

NAME    :   'name:'
        ;

TYPE    :   'type:'
        ;

SUBTYPE :   'subtype:'
        ;

ID_TYPE :   'id-type:'
        ;

CODE    :   'code:'
        ;

POWER   :   'power:'
        ;

MULT    :   'mult:'
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

MSG     :   'msg:'
        ;

BINDUI  :   'bindui:'
        ;

DESC    :   'desc:'
        ;

LWORD   :   ('a'..'z'|'-'|'_')+
        ;

WORD    :   ('A'..'Z'|'a'..'z')+
        ;

SPACE   :   ' '
        ;

NUMBER  :   ('-')? ('0'..'9')+
        ;