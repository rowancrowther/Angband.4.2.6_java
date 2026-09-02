/*
 * Lexer for lib/gamedata/realm.txt - the magic realms (arcane, divine, nature,
 * shadow) and their spellcasting flavour: primary stat, casting verb, spell
 * noun and spellbook item-type.
 *
 * Every directive value is free text and may contain spaces (e.g. the book-noun
 * "magic book"), so a catch-all STRING in the default mode would out-munch the
 * directive keywords (maximal munch swallows the whole line). Instead each
 * keyword switches into the FREE_TEXT mode at its colon and the value is
 * captured there, popping back at end of line - the same pattern the
 * PlayerRace/Body lexers use. record-count: stays in the default mode because
 * its value is a plain INTEGER, not free text.
 *
 * @author Rowan Crowther
 */
lexer grammar RealmLexer;

// Shared skip-rules: COMMENT ('#' to end of line) and EOL (a blank line). Both
// are discarded, so comments and blank lines may fall between any tokens.
import CommentsAndEol;

// record-count:<n> - the port-added header; its value is read as INTEGER (below)
// and checked against the number of records actually parsed by the reader.
RECORD_COUNT
        :   'record-count:'
        ;

// Each directive keyword consumes up to its colon, then pushes FREE_TEXT so the
// remainder of the line is captured verbatim as one STRING value.
NAME
        :   'name:' -> pushMode(FREE_TEXT)
        ;

STAT
        :   'stat:' -> pushMode(FREE_TEXT)
        ;

VERB
        :   'verb:' -> pushMode(FREE_TEXT)
        ;

SPELL_NOUN
        :   'spell-noun:' -> pushMode(FREE_TEXT)
        ;

BOOK_NOUN
        :   'book-noun:' -> pushMode(FREE_TEXT)
        ;

// The record-count value - the only number in the file, lexed in the default
// mode (record-count: deliberately does not switch to FREE_TEXT).
INTEGER
        :   '-'? ('0'..'9')+
        ;

// Free-text value mode: entered after a directive colon, left at end of line.
mode FREE_TEXT;

// The whole remainder of the directive line, captured verbatim - interior
// spaces and case preserved. One-or-more, so an empty value yields no token.
STRING
        :   ~('\r' | '\n')+
        ;

// End of a value line: consume the newline (and any preceding CR), discard it,
// and pop back to the default mode ready for the next directive.
FREE_TEXT_EOL
        :   '\r'* '\n' -> skip, popMode
        ;
