/*
 * Parser for lib/gamedata/realm.txt, over the tokens from RealmLexer. A realm
 * record opens with name: and then carries stat/verb/spell-noun/book-noun in any
 * order; every value is captured as raw text into a RealmParseRecord and the
 * typed resolution (stat name -> Stats, book-noun -> TValue) is deferred to
 * RealmAssembler, so this grammar stays free of domain lookups.
 *
 * Mirrors C's realm_parser (src/init.c): the same five directives registered by
 * init_parse_realm(), which - like the (alt|...)+ body here - accepts them in any
 * order. Presence of the fields beyond name: is not enforced here. A record that
 * lacks a directive carries "" for it, and the one field RealmAssembler checks is
 * the stat, which it cuts at the first colon and looks up there; a bad stat stops
 * the whole load, no record is dropped. verb:, spell-noun: and book-noun: need a
 * value (no value is a syntax error here); stat: does not, see the stat rule.
 *
 * @author Rowan Crowther
 */
parser grammar RealmGrammar;

options { tokenVocab = RealmLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.realm.RealmParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// name:<realm name> - opens a record; also captures the source line so the
// assembler can report any error against the record's position.
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

// stat:<STAT> - the primary spellcasting stat, as raw text (e.g. "INT"). C reads
// it as a sym, cut at the first colon with leading colons skipped, but the cut is
// RealmAssembler's job: the text is kept raw here, so "INT:extra" and "::INT"
// arrive as they were written.
//
// Unlike verb/spell-noun/book-noun the value is optional, so that a stat: line with
// nothing after it reaches the assembler as null, which it refuses with a message
// naming the record (C: MISSING_FIELD). COLON* only ever matches when the rest of
// the line is exactly one colon: in FREE_TEXT a lone ':' ties with STRING on length
// and COLON is listed first, while any longer run, "::" or ":INT", is one STRING.
//
// The three cases the record's stat field can hold: null (a stat: line with no
// name), "" (no stat: line at all, seeded by the realm rule, read as STAT_STR as C's
// zero-filled realm is), or the raw text.
stat
        returns[String statStr]
        @init { $statStr = null; }
        :   STAT COLON* (STRING { $statStr = $STRING.getText(); })?
        ;

// verb:<text> - the casting verb (e.g. "cast", "recite").
verb
        returns[String verbStr]
        :   VERB STRING { $verbStr = $STRING.getText(); }
        ;

// spell-noun:<text> - what a casting of this realm is called (e.g. "spell").
spellNoun
        returns[String spellNounStr]
        :   SPELL_NOUN STRING { $spellNounStr = $STRING.getText(); }
        ;

// book-noun:<text> - the spellbook's item-type name (e.g. "magic book"),
// resolved to a TValue later by the assembler.
bookNoun
        returns[String bookNounStr]
        :   BOOK_NOUN STRING { $bookNounStr = $STRING.getText(); }
        ;

// One realm record: name: opens it, then any mix of the four flavour directives
// in any order (each stored into its own @init var - last write wins on repeats).
// @after gathers them into a single RealmParseRecord; @init seeds the vars so a
// directive that is absent is read as "" rather than null. The one exception is a
// stat: line with no name, whose null overwrites the seed (see the stat rule).
realm
        returns[RealmParseRecord record]
        @init {
            String nameInit = "";
            String statInit = "";
            String verbInit = "";
            String spellNounInit = "";
            String bookNounInit = "";
            int lineInit = 0;
        }
        @after {
            $record = new RealmParseRecord(nameInit, statInit, verbInit,
                        spellNounInit, bookNounInit, lineInit);
        }
        :   name { lineInit = $name.line;
                   nameInit = $name.nameStr; }
        (   stat { statInit = $stat.statStr; }
        |   verb { verbInit = $verb.verbStr; }
        |   spellNoun { spellNounInit = $spellNoun.spellNounStr; }
        |   bookNoun { bookNounInit = $bookNoun.bookNounStr; } )*
        ;

// The whole file: one or more realm records to end of input.
file
        returns[List<RealmParseRecord> realms]
        @init {
            $realms = new ArrayList<>();
        }
        :   (realm { $realms.add($realm.record); })* EOF
        ;
