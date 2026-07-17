/*
 * @author Rowan Crowther
 *
 * Lexer for player_timed.txt. Each directive keyword (name:, grade:, fail:, …) is its own token
 * that switches the lexer into the mode matching that directive's value shape, so the parser sees
 * cleanly-typed value tokens:
 *   - FLAG_MODE          : upper-case flag/enum tokens (name:, msgt:, resist:, brand:, slay:, flags:)
 *   - PT_FREE_TEXT_MODE  : arbitrary message text to end-of-line (desc:, on-end:, on-increase:, …)
 *   - DELIMITED          : colon-separated fields (grade:, fail:, on-*-effect:, flag-synonym:)
 * The shared skip rules (comments, blank lines) and the FLAG_BODY / colour-code character classes
 * come from the imported CommentsAndEol / Flags / Colours grammars.
 */
lexer grammar PlayerTimedLexer;

import CommentsAndEol, Flags, Colours;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(FLAG_MODE)
        ;

DESC
        :   'desc:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

GRADE
        :   'grade:' -> pushMode(DELIMITED)
        ;

ON_END
        :   'on-end:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

ON_INCREASE
        :   'on-increase:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

ON_DECREASE
        :   'on-decrease:' -> pushMode(PT_FREE_TEXT_MODE)
        ;

MSGT
        :   'msgt:' -> pushMode(FLAG_MODE)
        ;

FAIL
        :   'fail:' -> pushMode(DELIMITED)
        ;

ON_BEGIN_EFFECT
        :   'on-begin-effect:' -> pushMode(DELIMITED)
        ;

ON_END_EFFECT
        :   'on-end-effect:' -> pushMode(DELIMITED)
        ;

EFFECT_DICE
        :   'effect-dice:'
        ;

RESIST
        :   'resist:' -> pushMode(FLAG_MODE)
        ;

BRAND
        :   'brand:' -> pushMode(FLAG_MODE)
        ;

SLAY
        :   'slay:' -> pushMode(FLAG_MODE)
        ;

FLAG_SYNONYM
        :   'flag-synonym:' -> pushMode(DELIMITED)
        ;

LOWER_BOUND
        :   'lower-bound:'
        ;

FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

fragment EOL_FRAGMENT
        :   '\r'* '\n'
        ;

mode FLAG_MODE;

FLAG
        :   FLAG_BODY
        ;

FLAG_OR
        :   ' '? '|' ' '?
        ;

FLAG_EOL
        :   EOL_FRAGMENT -> skip, popMode
        ;

mode PT_FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

PT_FREE_TEXT_MODE_EOL
        :   EOL_FRAGMENT -> skip, popMode
        ;

mode DELIMITED;

DELIM_INTEGER
        :   '-'? ('0'..'9')+
        ;

DELIM_COLOUR_CODE
        :   COLOUR_CODE
        ;

DELIM_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

DELIM_COLON
        :   ':'
        ;

DELIM_EOL
        :   EOL_FRAGMENT -> skip, popMode
        ;
