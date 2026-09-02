// Lexer for class.txt (feeds PlayerClassGrammar). The default mode tokenises the line-leading
// directives (name:, stats:, equip:, book:, spell:, ...); most directives switch into a purpose-
// built sub-mode so the remainder of the line lexes under the right rules — free text (names,
// titles, descriptions), colon-delimited fields (equip/book/spell), flag lists, book graphics and
// book properties each get their own mode, popping back at end-of-line.
lexer grammar PlayerClassLexer;

import CommentsAndEol, Flags, EffectBlockLexer, AngbandDisplayCharacter;

RECORD_COUNT
        :   'record-count:'
        ;

NAME
        :   'name:' -> pushMode(CLASS_FREE_TEXT_MODE)
        ;

STATS
        :   'stats:' // Colon delimited integers
        ;

SKILL_DISARM_PHYS
        :   'skill-disarm-phys:' // Colon delimited integers
        ;

SKILL_DISARM_MAGIC
        :   'skill-disarm-magic:' // Colon delimited integers
        ;

SKILL_DEVICE
        :   'skill-device:' // Colon delimited integers
        ;

SKILL_SAVE
        :   'skill-save:' // Colon delimited integers
        ;

SKILL_STEALTH
        :   'skill-stealth:' // Colon delimited integers
        ;

SKILL_SEARCH
        :   'skill-search:' // Colon delimited integers
        ;

SKILL_MELEE
        :   'skill-melee:' // Colon delimited integers
        ;

SKILL_SHOOT
        :   'skill-shoot:' // Colon delimited integers
        ;

SKILL_THROW
        :   'skill-throw:' // Colon delimited integers
        ;

SKILL_DIG
        :   'skill-dig:' // Colon delimited integers
        ;

HITDIE
        :   'hitdie:' // integer
        ;

EXP
        :   'exp:'  // never used
        ;

MAX_ATTACKS
        :   'max-attacks:' // integer
        ;

MIN_WEIGHT
        :   'min-weight:' // integer
        ;

STRENGTH
        :   'strength-multiplier:' // integer
        ;

TITLE
        :   'title:' -> pushMode(CLASS_FREE_TEXT_MODE)
        ;

EQUIP
        :   'equip:' -> pushMode(DELIMITED_STRINGS) // Colon delimited TValue, string, integer, integer, string
        ;

OBJ_FLAGS
        :   'obj-flags:' -> pushMode(FLAG_MODE)
        ;

PLAYER_FLAGS
        :   'player-flags:' -> pushMode(FLAG_MODE)
        ;

MAGIC
        :   'magic:' // Colon delimited integers
        ;

BOOK
        :   'book:' -> pushMode(DELIMITED_STRINGS) // Colon delimited TValue, string, string, integer, string
        ;

BOOK_GRAPHICS
        :   'book-graphics:' -> mode(GLYPH_MODE)
        ;

BOOK_PROPERTIES
        :   'book-properties:' -> pushMode(PROPERTIES_MODE)// Colon delimited integer, integer, integer ' to ' integer
        ;

SPELL
        :   'spell:' -> pushMode(DELIMITED_STRINGS) // Colon delimited string, 4 x integer
        ;

DESC
        :   'desc:' -> pushMode(CLASS_FREE_TEXT_MODE)
        ;

COLON
        :   ':'
        ;

INTEGER
        :   '-'? ('0'..'9')+
        ;

mode PROPERTIES_MODE;

PROP_INT
        :   '-'? ('0'..'9')+
        ;

PROP_COLON
        :   ':'
        ;

PROP_TO
        :   ' '? 'to' ' '?
        ;

PROP_EOL
        :   EOL_SKIP -> skip, popMode
        ;

mode DELIMITED_STRINGS;

DELIM_INTEGER
        :   '-'? ('0'..'9')+
        ;

STRING_BETWEEN_COLONS
        :   ~('\n' | '\r' | ':')+
        ;

DELIM_COLON
        :   ':'
        ;

END_SKIP
        :   EOL_SKIP -> skip, popMode
        ;

fragment EOL_SKIP
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
        :   EOL_SKIP -> skip, popMode
        ;

mode CLASS_FREE_TEXT_MODE;

STRING
        :   ~('\n' | '\r')+
        ;

FREE_TEXT_EOL
        :   EOL_SKIP -> skip, popMode
        ;
