// Lexer for lib/gamedata/blow_methods.txt (see BlowMethodGrammar for the full file
// description). Directive keywords are matched in the default mode; a single
// FREE_TEXT_MODE handles the four values that are arbitrary text to end-of-line.
//
// name:/msg: carry a bare code (HIT, MON_HIT) that the default mode could match on
// its own, but act:/desc: carry prose with spaces, braces and punctuation
// ("insults {target}!", "release spores") that no keyword rule can. All four push
// FREE_TEXT_MODE so the value shape is uniform: everything after the colon, verbatim.
//
// NOTE the asymmetry with most of the suite: FREE_TEXT_EOL below matches a bare
// newline, so entering FREE_TEXT_MODE and immediately leaving it is legal. A valueless
// "msg:" therefore lexes cleanly rather than erroring, which is exactly what C's
// optional "msg ?str msg" registration allows - see the msg rule in BlowMethodGrammar.
//
// CommentsAndEol supplies the comment/EOL skipping used in the default mode.
lexer grammar BlowMethodLexer;

import CommentsAndEol;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

CUT
        :   'cut:'
        ;

STUN
        :   'stun:'
        ;

MISS
        :   'miss:'
        ;

PHYS
        :   'phys:'
        ;

MSG
        :   'msg:' -> pushMode(FREE_TEXT_MODE)
        ;

ACT
        :   'act:' -> pushMode(FREE_TEXT_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

// Value of cut:/stun:/miss:/phys:. C reads these with parser_getuint, which rejects a
// leading '-'; the '-'? here is more permissive than the original. Harmless for the
// shipped data (every value is 0 or 1) but note that the assembler tests non-zero, so
// a negative would read as TRUE rather than being rejected.
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Free-form value text, entered from name:/msg:/act:/desc: and left at end-of-line.
mode FREE_TEXT_MODE;

// Everything up to (but excluding) the line terminator. Excluding '\r' as well as '\n'
// keeps a CRLF file's carriage return out of the captured value, so the token text is
// identical on both line-ending conventions and needs no trailing trim.
STRING
        :   ~('\r' | '\n')+
        ;

// Ends the free-text run and returns to the default mode. Matching a bare newline (no
// preceding STRING) is deliberate: it makes an empty value such as "msg:" legal - see
// the mode note in the header block above.
FREE_TEXT_EOL
        :   '\r'* '\n' -> skip, popMode
        ;
