/*
 * Parser for lib/gamedata/realm.txt, over the tokens from RealmLexer. A realm
 * record opens with name: and then carries stat/verb/spell-noun/book-noun in any
 * order; every value is captured as raw text into a RealmParseRecord and the
 * typed resolution (stat name -> Stats, book-noun -> TValue) is deferred to
 * RealmAssembler, so this grammar stays free of domain lookups.
 *
 * Mirrors C's realm_parser (src/init.c): the same five directives registered by
 * init_parse_realm(), which - like the (alt|...)+ body here - accepts them in any
 * order. Presence of the fields beyond name: is enforced softly downstream (an
 * empty stat or book-noun fails to resolve and the assembler drops the record).
 *
 * @author Rowan Crowther
 */
parser grammar RealmGrammar;

options { tokenVocab = RealmLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.realm.RealmParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

// record-count:<n> header - returns the declared count as text for the reader to
// validate against the number of records actually parsed.
recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

// name:<realm name> - opens a record; also captures the source line so the
// assembler can report any error against the record's position.
name
        returns[String nameStr, int line]
        :   NAME STRING { $nameStr = $STRING.getText(); $line = $start.getLine(); }
        ;

// stat:<STAT> - the primary spellcasting stat, as raw text (e.g. "INT").
stat
        returns[String statStr]
        :   STAT STRING { $statStr = $STRING.getText(); }
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
// directive that is absent is read as "" rather than null.
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
        |   bookNoun { bookNounInit = $bookNoun.bookNounStr; } )+
        ;

// The whole file: the record-count header, then one or more realm records to
// end of input.
file
        returns[List<RealmParseRecord> realms, String declaredRecordCount]
        @init {
            $realms = new ArrayList<>();
        }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            (realm { $realms.add($realm.record); })+ EOF
        ;
