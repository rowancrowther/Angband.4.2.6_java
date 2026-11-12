// Lexer for lib/gamedata/object_property.txt (see ObjectPropertyGrammar for the
// full file description). Directive keywords are matched in the default mode;
// three sub-modes handle the free-form and colon-delimited value shapes:
//
//   FREE_TEXT_MODE - the value is arbitrary text to end-of-line (STRING). Entered
//     by name:/subtype:/id-type:/msg:/desc:, whose values contain spaces and
//     punctuation (e.g. "sustain strength", "misc ability", "curse-only",
//     "Slows your metabolism") that the default-mode LCASE/UCASE rules cannot match.
//   DELIMITER - the colon-separated "sym : int" of type-mult:, where the symbol
//     may itself contain a space ("dragon armor") so must be a distinct token type
//     from the value integer.
//
// FLAG_BODY (an upper-case name allowing digits, e.g. DIG_1/LIGHT_2) comes from the
// shared Flags import and backs UCASE; CommentsAndEol supplies comment/EOL skipping.
lexer grammar ObjectPropertyLexer;

import CommentsAndEol, Flags;

NAME
        :   'name:' -> pushMode(FREE_TEXT_MODE)
        ;

TYPE
        :   'type:'
        ;

SUBTYPE
        :   'subtype:' -> pushMode(FREE_TEXT_MODE)
        ;

ID_TYPE
        :   'id-type:' -> pushMode(FREE_TEXT_MODE)
        ;

CODE
        :   'code:'
        ;

POWER
        :   'power:' -> pushMode(INTEGER_MODE)
        ;

MULT
        :   'mult:' -> pushMode(INTEGER_MODE)
        ;

TYPE_MULT
        :   'type-mult:' -> pushMode(DELIMITER)
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
        :   'bindui:' -> mode(BINDUI_INTRO_MODE)
        ;

DESC
        :   'desc:' -> pushMode(FREE_TEXT_MODE)
        ;

COLON
        :   ':'
        ;

// Lower-case token for single-word values (type:, adjective:, neg-adjective:).
LCASE
        :   ('a'..'z' | '0'..'9' | '_')+
        ;

// Upper-case symbolic name for code: (e.g. STR, FREE_ACT, DIG_1). FLAG_BODY allows
// trailing digits, which several codes carry.
UCASE
        :   FLAG_BODY
        ;

mode BINDUI_INTRO_MODE;

// Lower-case token for single-word values (type:, adjective:, neg-adjective:).
BINDUI_LCASE
        :   ('a'..'z' | '0'..'9' | '_')+
        ;

BINDUI_INTRO_COLON
        :   ':' -> skip, mode(BINDUI_INTEGER_MODE)
        ;

BINDUI_LTHAN
        :   '<' -> skip, mode(BINDUI_TAG_MODE)
        ;

mode BINDUI_TAG_MODE;

BINDUI_UCASE
        :   FLAG_BODY
        ;

BINDUI_GTHAN
        :   '>' -> skip, mode(BINDUI_INTEGER_MODE)
        ;

mode BINDUI_INTEGER_MODE;

BINDUI_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+
        ;

BINDUI_COLON
        :   ':' -> skip
        ;

BINDUI_NL
        :   NL -> skip, mode(DEFAULT_MODE)
        ;

mode INTEGER_MODE;

INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ -> popMode
        ;

// Free-form value text to end of line, popped back to the default mode on newline.
mode FREE_TEXT_MODE;

STRING
        :   ~('\r' | '\n')+
        ;

EOL_POP
        :   NL -> popMode, skip
        ;

// Colon-delimited value list for type-mult: (sym ":" int). DELIM_INTEGER precedes
// STRING_BETWEEN_COLONS so a purely numeric field lexes as the integer, while the
// symbol field (which may contain spaces) takes STRING_BETWEEN_COLONS.
mode DELIMITER;

DELIM_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ 
        ;

STRING_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

DELIM_COLON
        :   ':'
        ;

END_SKIP
        :   NL -> skip, popMode
        ;

