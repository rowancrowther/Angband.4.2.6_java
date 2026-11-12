// Lexer for lib/gamedata/pit.txt - the "monster pit / nest" recipes (which
// monsters may fill a themed room, how deep/rare it is, its theme colours).
// Cf. the C original's pit parser in src/mon-init.c.
//
// Only room: keeps its value in the default mode (as a UINT). The other value
// shapes need their own lexer mode so a value's characters are not
// mis-tokenized as keywords, flags or each other's numbers:
//   * REST_OF_LINE     - free text (names, monster-base names) taken verbatim
//                        to end of line.
//   * FLAG_MODE        - one or more '|'-separated upper-case flag/spell codes.
//   * ALLOC_MODE       - the "rarity:level" pair, split on a ':' that must not
//                        be confused with the ':' ending a keyword.
//   * OBJ_RARITY_MODE  - the uint after obj-rarity:, read as OBJ_RARITY_UINT.
//   * INNATE_FREQ_MODE - the signed int after innate-freq:, read as
//                        PIT_INTEGER, so it is not captured by the uint rule.
//   * COLOUR_MODE      - the rest of the line after color: (a colour-code
//                        letter or a colour name), taken verbatim and resolved
//                        by the assembler, kept apart so the value is only read
//                        as a colour right after color:.
// Each mode ends with its own *_EOL rule, which pops back to the default mode
// and skips the newline, so no end-of-line token reaches the grammar.
// Shared bits (FLAG_BODY, comment/EOL skipping) come from the imported
// grammars Flags and CommentsAndEol.
lexer grammar PitLexer;

import Flags, CommentsAndEol;

// "name:<text>" - starts a record; the name runs to end of line, so free-text mode.
NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

// "room:<n>" - pit/nest/other room type (1, 2 or 3); the number is a UINT.
ROOM
        :   'room:'
        ;

// "alloc:<rarity>:<level>" - the two fields share a ':' separator, handled in ALLOC_MODE.
ALLOC
        :   'alloc:' -> pushMode(ALLOC_MODE)
        ;

// "obj-rarity:<n>" - per-square object rarity percentage. The number is lexed in
// OBJ_RARITY_MODE as an OBJ_RARITY_UINT.
OBJ_RARITY
        :   'obj-rarity:' -> pushMode(OBJ_RARITY_MODE)
        ;

// "color:<code>" - a theme colour; American spelling, matching the data file.
// The value is lexed in COLOUR_MODE so it is not read as a keyword.
COLOUR
        :   'color:' -> pushMode(COLOUR_MODE)
        ;

// "mon-base:<name>" - an allowed monster template; the name is free text.
MON_BASE
        :   'mon-base:' -> pushMode(REST_OF_LINE)
        ;

// "flags-req:<FLAG> | ..." / "flags-ban:..." - '|'-separated race-flag codes.
FLAGS_REQ
        :   'flags-req:' -> pushMode(FLAG_MODE)
        ;

FLAGS_BAN
        :   'flags-ban:' -> pushMode(FLAG_MODE)
        ;

// "innate-freq:<n>" - required innate-attack frequency, as a percentage. The
// number is lexed in INNATE_FREQ_MODE as a PIT_INTEGER, not a UINT, because C
// registers the field as a signed "int" ("innate-freq int freq"), so a leading
// '-' is legal text here and the assembler's 1-to-100 range check refuses it.
INNATE_FREQ
        :   'innate-freq:' -> pushMode(INNATE_FREQ_MODE)
        ;

// "spell-req:<FLAG> | ..." / "spell-ban:..." - '|'-separated spell-flag codes,
// same shape as the flag directives above.
SPELL_REQ
        :   'spell-req:' -> pushMode(FLAG_MODE)
        ;

SPELL_BAN
        :   'spell-ban:' -> pushMode(FLAG_MODE)
        ;

// The value of "room:<n>" - C registers it as "room uint type", read by
// parser_parse in parser.c with strtoul in base 0. (OBJ_RARITY_UINT below has
// the same shape for obj-rarity:.) The token follows that reading:
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

// "mon-ban:<name>" - a specific forbidden monster; free-text name.
MON_BAN
        :   'mon-ban:' -> pushMode(REST_OF_LINE)
        ;

mode INNATE_FREQ_MODE;

// The value of "innate-freq:<n>" - C's "int" field, read by parser_parse in
// parser.c with strtol in base 0 (no check for a '-', unlike the uint fields).
// The token has the same shape as UINT without the tail:
//  - an optional '-' or '+' sign.
//  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with
//    or without the prefix, so "ff" lexes here; the assembler refuses it
//    because strtol finds no digit in it. A decimal digit run is the same token,
//    so 010 is read as octal by the assembler, not here.
//  - nothing after the digits. C ignores trailing text ("5xyz" is 5), but here
//    the lexer reports a token recognition error for the 'x', which ends the
//    load. (A tail made only of a-f, such as "5abc", is all digits to this rule.)
PIT_INTEGER
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+
        ;

INNATE_FREQ_EOL 
        :   NL -> popMode, skip
        ;

// --- FLAG_MODE: the '|'-separated flag/spell lists ------------------------
mode FLAG_MODE;

// One flag/spell code (FLAG_BODY is the shared upper-case-code shape from Flags).
FLAG
        :   FLAG_BODY
        ;

// The " | " (spaces optional) separating codes on one line.
FLAG_OR
        :   ' '? '|' ' '?
        ;

// End of the flag line: leave the mode and drop the newline.
FLAG_EOL
        :   NL -> popMode, skip
        ;

// --- REST_OF_LINE: verbatim free text (names, monster names) --------------
mode REST_OF_LINE;

// Everything up to the line end, taken as-is (may contain spaces, e.g. "ancient dragon").
STRING
        :   ~('\r' | '\n')+
        ;

REST_OF_LINE_EOL
        :   NL -> popMode, skip
        ;

// --- ALLOC_MODE: the "rarity:level" pair ----------------------------------
mode ALLOC_MODE;

// The ':' between the two alloc fields - distinct from the ':' that ends a
// keyword (already consumed by the ALLOC token), so it needs its own name here.
ALLOC_COLON
        :   ':'
        ;

// A rarity or level in "alloc:<rarity>:<level>" - C registers both as uint
// ("alloc uint rarity uint level"), read by parser_parse in parser.c with
// strtoul in base 0. The token has the same shape as UINT, and its tail stops
// at ':' so the first field ends where the ALLOC_COLON begins. The token follows
// that reading:
//  - an optional '-' or '+' sign. A '-' is kept so the assembler can refuse it,
//    "-0" included, as C does for a uint.
//  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with
//    or without the prefix, so "ff" lexes here; the assembler refuses it
//    because strtoul finds no digit in it.
//  - any tail up to the colon or the end of the line, because strtoul stops at
//    the first character it cannot use and ignores the rest ("5abc" is 5, and a
//    trailing blank or tab is part of the tail). A decimal digit run is the
//    same token, so 010 is read as octal by the assembler, not here.
ALLOC_UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

ALLOC_EOL
        :   NL -> popMode, skip
        ;

mode OBJ_RARITY_MODE;

// The value of "obj-rarity:<n>" - C registers it as "obj-rarity uint obj_rarity",
// read by parser_parse in parser.c with strtoul in base 0. The token has the
// same shape as UINT and follows the same reading:
//  - an optional '-' or '+' sign. A '-' is kept so the assembler can refuse it,
//    "-0" included, as C does for a uint.
//  - an optional 0x or 0X prefix, then one or more digits. a-f are allowed with
//    or without the prefix, so "ff" lexes here; the assembler refuses it
//    because strtoul finds no digit in it.
//  - any tail up to the colon or the end of the line, because strtoul stops at
//    the first character it cannot use and ignores the rest ("5abc" is 5, and a
//    trailing blank or tab is part of the tail). A decimal digit run is the
//    same token, so 010 is read as octal by the assembler, not here.
OBJ_RARITY_UINT
        :   ('-' | '+')? ('0x' | '0X')? ('0'..'9' | 'a'..'f' | 'A'..'F')+ ~('\r' | '\n' | ':')*
        ;

OBJ_RARITY_EOL
        :   NEWLINE -> popMode, skip
        ;

// --- COLOUR_MODE: a colour code or colour name ----------------------------
mode COLOUR_MODE;

// The whole value, verbatim: a one-letter code or a name such as "Light Blue".
// Not checked here - C treats an unmatched value as white, so the assembler does.
COLOUR_LINE
        :   ~('\r' | '\n')+
        ;

COLOUR_EOL
        :   NEWLINE -> popMode, skip
        ;
