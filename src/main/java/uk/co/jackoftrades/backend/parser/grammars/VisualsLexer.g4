lexer grammar VisualsLexer;

import CommentsAndEol, Colours;

FLICKER
        :   'flicker:' -> mode(COLOUR)
        ;

FLICKER_COLOUR
        :   'flicker-color:' -> mode(COLOUR)
        ;

CYCLE
        :   'cycle:' -> mode(DELIMITED)
        ;

CYCLE_COLOUR
        :   'cycle-color:' -> mode(COLOUR)
        ;

mode REST_OF_LINE;

STRING
        : ~('\r' | '\n')+
        ;

ROL_EOL
        :   '\r'* '\n' -> skip, mode(DEFAULT_MODE)
        ;

mode COLOUR;

COLOUR_CHAR
        :   COLOUR_CODE
        ;

SWAP_COLON
        :   ':' -> mode(REST_OF_LINE)
        ;

COLOUR_EOL
        :   '\r'* '\n' -> skip, mode(DEFAULT_MODE)
        ;

mode DELIMITED;

DELIM_COLON
        :   ':' -> mode(REST_OF_LINE)
        ;

STRING_BETWEEN_COLON
        :   ~('\n' | '\r' | ':')+
        ;

POP_EOL
        :   '\r'* '\n' -> skip, mode(DEFAULT_MODE)
        ;
