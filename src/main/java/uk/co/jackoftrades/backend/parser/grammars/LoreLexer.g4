// Lexer for lib/user/lore.txt - see LoreGrammar.g4 for the full picture
// (including the severe "file rule always returns null" bug that's
// downstream of this lexer, not in it).

lexer grammar LoreLexer;

// A blank line on its own (not part of a comment block - lore.txt has no
// comment syntax at all, unlike every gamedata file).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

// "name:" - switches to MONSTER_NAME_MODE to capture the rest of the line
// as free text (monster names can contain almost anything).
NAME
        :   'name:' -> pushMode(MONSTER_NAME_MODE)
        ;

COUNTS
        :   'counts:'
        ;

// "blow:" - switches to BLOW_MODE for the structured 5-field blow value.
BLOW
        :   'blow:' -> pushMode(BLOW_MODE)
        ;

// "flags:" - switches to FLAG_MODE for the '|'-separated flag-name token stream.
FLAGS
        :   'flags:' -> pushMode(FLAG_MODE)
        ;

// "spells:" - switches to SPELL_MODE for the '|'-separated spell-name token stream.
SPELLS
        :   'spells:' -> pushMode(SPELL_MODE)
        ;

BASE
        :   'base:'
        ;

DROP
        :   'drop:'
        ;

DROP_BASE
        :   'drop-base:'
        ;

FRIENDS
        :   'friends:'
        ;

FRIENDS_BASE
        :   'friends-base:'
        ;

MIMIC
        :   'mimic:'
        ;

fragment INTEGER_FRAGMENT
        :   ('0'..'9')+
        ;

// A bare non-negative integer.
INTEGER
        :   INTEGER_FRAGMENT
        ;

// Field separator within counts:/drop:/drop-base:/friends:/friends-base:/mimic: lines.
COLON
        :   ':'
        ;

// Free-running text - used for drop:/mimic:'s sval field.
STRING
        :   ('a'..'z' | 'A'..'Z' | ' ' | '-' | '\'' | '*' | '&' | '~' | '<' | '>')+
        ;

// All-lowercase text with spaces - used for drop:/drop-base:/mimic:'s tval field.
TVAL
        :   ('a'..'z' | ' ')+
        ;

// Mixed-case text (with accented characters) - used for friends:'s monster-name field.
FRIENDS_NAME
        :   [A-Za-z0-9 \-,'<>\u00E1\u00E9\u00EB\u00EE\u00F4\u00F6\u00FA\u00FB]+
        ;

// Lowercase, space-separated words (or starting with 'M') - used for
// friends-base:'s monster_base.txt name field.
FRIENDS_BASE_NAME
        :   ('a'..'z' | 'M') ('a'..'z')+ (' ' [a-z]+)*
        ;

// A "<base>+<dice>d<sides>M<bonus>" dice expression - used for friends:/
// friends-base:'s number-appearing field.
DICE_STRING
        :   '-'? INTEGER '+' INTEGER 'd' INTEGER 'M' INTEGER
        ;

// Entered after name: to capture the rest of the line as free text.
mode MONSTER_NAME_MODE;

MONSTER_NAME
        :   (~[\r\n])+ -> popMode
        ;

// Entered after flags: to tokenize a '|'-separated flag-name list.
mode FLAG_MODE;

// A single RF_* flag name (the "RF_" prefix itself is added in
// LoreGrammar.g4's actions, not present in the raw text).
FLAG
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// The " | " separator between flag names on a flags: line.
OR
        :   ' | '
        ;

// End of a flags: line - pops back to the default mode.
FLAG_EOL
        :   '\r'? '\n' -> popMode, skip
        ;

// Entered after spells: to tokenize a '|'-separated spell-name list.
mode SPELL_MODE;

// A single RSF_* spell name.
SPELL
        :   ('A'..'Z' | '_' | '0'..'9')+
        ;

// End of a spells: line - pops back to the default mode.
SPELL_EOL
        :   '\r'? '\n' -> popMode, skip
        ;

// The " | " separator between spell names on a spells: line.
SPELL_OR
        :   ' | '
        ;

// Entered after blow: to tokenize the structured 5-field blow value as one token.
mode BLOW_MODE;

// "<METHOD>:<EFFECT>:<dice>:<times seen>:<blow index>" as a single token -
// split apart by LoreGrammar.g4's `blow` action (which only reads the
// first 4 fields - see that file's problem #2).
BLOW_MODE_VALUES
        :   UCASE_FLAG COLON UCASE_FLAG COLON DICE_STRING_FRAGMENT COLON INTEGER_FRAGMENT COLON INTEGER_FRAGMENT
        ;

fragment UCASE_FLAG
        :   ('A'..'Z' | '_')+
        ;

fragment DICE_STRING_FRAGMENT
        :   MINUS_FRAGMENT? INTEGER_FRAGMENT
            PLUS_FRAGMENT INTEGER_FRAGMENT
            D_FRAGMENT INTEGER_FRAGMENT
            M_FRAGMENT INTEGER_FRAGMENT
        ;

fragment PLUS_FRAGMENT
        :   '+'
        ;

fragment MINUS_FRAGMENT
        :   '-'
        ;

fragment D_FRAGMENT
        :   'd'
        ;

fragment M_FRAGMENT
        :   'M'
        ;

// End of a blow: line - pops back to the default mode.
BLOW_EOL
        :   '\r'? '\n' -> popMode, skip
        ;
