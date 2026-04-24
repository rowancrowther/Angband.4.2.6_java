grammar ConstantsFormatter;

section :   TOKEN COLON
        ;

name    :   TOKEN COLON VALUE
        ;

line    :   section name EOL
        ;

file    :   line (EOL line)* EOF
        ;

TOKEN   :   ('a'..'z'|'-')+
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

COLON   :   ':'
        ;

VALUE   :   ('0'..'9')+
        ;

EOL     :   '\r'? '\n'
        ;