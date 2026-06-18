grammar Visuals;

flicker
        :   FLICKER COLOUR_CHAR COLON LCASE
        ;

flickerColour
        :   FLICKER_COLOUR COLOUR_CHAR
        ;

flickerBlock
        :   flicker
            flickerColour+
        ;

cycle
        :   CYCLE (FLICKER | FANCY) (LCASE | COLOUR_CHAR)
        ;

cycleColour
        :   CYCLE_COLOUR COLOUR_CHAR
        ;

cycleBlock
        :   cycle
            cycleColour+
        ;

file
        :   flickerBlock+
            cycleBlock+
            EOF
        ;

COMMENT :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL     :   '\r'? '\n' -> skip
        ;

FLICKER
        :   'flicker:'
        ;

FLICKER_COLOUR
        :   'flicker-color:'
        ;

CYCLE
        :   'cycle:'
        ;

FANCY
        :   'fancy:'
        ;

CYCLE_COLOUR
        :   'cycle-color:'
        ;

COLON
        :   ':'
        ;

COLOUR_CHAR
        :   [dwsorgbuDWPyRGBUpvtmYiTVIMzZ]
        ;

LCASE
        :   ('a'..'z' | ' ' | '-' | '/' | '(' | ')' | ',')+
        ;