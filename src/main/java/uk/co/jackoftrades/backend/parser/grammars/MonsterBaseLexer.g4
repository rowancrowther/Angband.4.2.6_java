lexer grammar MonsterBaseLexer;

import CommentsAndEol, Flags;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT)
        ;

GLYPH
        :   'glyph:' -> pushMode(GLYPH_MODE)
        ;

PAIN
        :   'pain:'
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

DESC
        : 'desc:' -> pushMode(FREE_TEXT)
        ;

// A bare non-negative integer - the pain-message-set index.
INTEGER
        :   '-'? ('0'..'9')+
        ;

mode GLYPH_MODE;

SINGLE_CHAR
        :   ~('\r' | '\n') -> popMode
        ;

mode FREE_TEXT;

STRING
        :   ~('\r' | '\n')+ -> popMode
        ;

mode FLAG_MODE;

FLAG
        : FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   ('\r' | '\n')+ -> popMode
        ;