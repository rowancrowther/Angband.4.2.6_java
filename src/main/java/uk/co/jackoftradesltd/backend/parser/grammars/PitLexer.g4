// Lexer for lib/gamedata/pit.txt - the "monster pit / nest" recipes (which
// monsters may fill a themed room, how deep/rare it is, its theme colours).
// Cf. the C original's pit parser in src/mon-init.c.
//
// Most directives are simple "keyword:value" lines, but three value shapes
// need their own lexer mode so a value's characters are not mis-tokenised as
// keywords or flags:
//   * REST_OF_LINE - free text (names, monster-base names) taken verbatim to
//                    end of line.
//   * FLAG_MODE    - one or more '|'-separated upper-case flag/spell codes.
//   * ALLOC_MODE   - the "rarity:level" pair, split on a ':' that must not be
//                    confused with the ':' ending a keyword.
//   * COLOUR_MODE  - a single colour-code letter (from the shared Colours
//                    alphabet), kept apart so a bare letter is only a colour
//                    right after color:.
// Shared bits (FLAG_BODY, colour codes, comment/EOL skipping) come from the
// imported grammars.
lexer grammar PitLexer;

import Flags, CommentsAndEol, Colours;

// "record-count:<n>" - the leading count header every gamedata list file carries.
RECORD_COUNT
        :   'record-count:'
        ;

// "name:<text>" - starts a record; the name runs to end of line, so free-text mode.
NAME
        :   'name:' -> pushMode(REST_OF_LINE)
        ;

// "room:<digit>" - pit/nest/other room type; the digit is a PIT_INTEGER.
ROOM
        :   'room:'
        ;

// "alloc:<rarity>:<level>" - the two fields share a ':' separator, handled in ALLOC_MODE.
ALLOC
        :   'alloc:' -> pushMode(ALLOC_MODE)
        ;

// "obj-rarity:<n>" - per-square object rarity percentage.
OBJ_RARITY
        :   'obj-rarity:'
        ;

// "color:<code>" - a theme colour; American spelling, matching the data file.
// The single-letter value is lexed in COLOUR_MODE so it is not read as a keyword.
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

// "innate-freq:<n>" - required innate-attack frequency.
INNATE_FREQ
        :   'innate-freq:'
        ;

// "spell-req:<FLAG> | ..." / "spell-ban:..." - '|'-separated spell-flag codes,
// same shape as the flag directives above.
SPELL_REQ
        :   'spell-req:' -> pushMode(FLAG_MODE)
        ;

SPELL_BAN
        :   'spell-ban:' -> pushMode(FLAG_MODE)
        ;

// "mon-ban:<name>" - a specific forbidden monster; free-text name.
MON_BAN
        :   'mon-ban:' -> pushMode(REST_OF_LINE)
        ;

// A signed integer, used by the room:/obj-rarity:/innate-freq: values.
PIT_INTEGER
        :   '-'? ('0'..'9')+
        ;

// Local end-of-line fragment, reused by every mode's pop rule below. Kept as a
// fragment (never a token) because line ends inside a mode are always skipped.
fragment PIT_EOL
        :   '\r'* '\n'
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
        :   PIT_EOL -> popMode, skip
        ;

// --- REST_OF_LINE: verbatim free text (names, monster names) --------------
mode REST_OF_LINE;

// Everything up to the line end, taken as-is (may contain spaces, e.g. "ancient dragon").
STRING
        :   ~('\r' | '\n')+
        ;

REST_OF_LINE_EOL
        :   PIT_EOL -> popMode, skip
        ;

// --- ALLOC_MODE: the "rarity:level" pair ----------------------------------
mode ALLOC_MODE;

// The ':' between the two alloc fields - distinct from the ':' that ends a
// keyword (already consumed by the ALLOC token), so it needs its own name here.
ALLOC_COLON
        :   ':'
        ;

ALLOC_INTEGER
        :   '-'? ('0'..'9')+
        ;

ALLOC_EOL
        :   PIT_EOL -> popMode, skip
        ;

// --- COLOUR_MODE: a single colour-code letter -----------------------------
mode COLOUR_MODE;

// One colour code (COLOUR_CODE is the shared single-letter alphabet from Colours).
COLOUR_CHAR
        :   COLOUR_CODE
        ;

COLOUR_EOL
        :   PIT_EOL -> popMode, skip
        ;
