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

// The value of "cut:<n>", "stun:<n>", "miss:<n>" and "phys:<n>" - C registers each
// as a "uint" field ("cut uint cut" and so on), read by parser_parse in
// parser.c with strtoul in base 0. The token follows that reading:
//  - an optional '-' or '+' sign. A '-' is kept so the assembler can refuse it,
//    "-0" included, as C does for a uint.
//  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with
//    or without the prefix, so "ff" lexes here; the assembler refuses it
//    because strtoul finds no digit in it.
//  - any tail up to the colon or the end of the line, because strtoul stops at
//    the first character it cannot use and ignores the rest ("5abc" is 5, and a
//    trailing blank or tab is part of the tail). A decimal digit run is the
//    same token, so 010 is read as octal by the assembler, not here.
UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
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
        :   NL -> skip, popMode
        ;
