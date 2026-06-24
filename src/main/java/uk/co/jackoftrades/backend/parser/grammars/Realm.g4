// Parser+lexer for lib/gamedata/realm.txt - the 4 magic realms (arcane,
// divine, nature, shadow) and their spellcasting flavour text: primary
// stat, casting verb, spell noun, and book item-type. Cf. src/init.c:
// struct file_parser realm_parser (init.c:2956), directives registered in
// init_parse_realm() (init.c:2919-2926): name/stat/verb/spell-noun/
// book-noun -> parse_realm_name/_stat/_verb/_spell_noun/_book_noun - a
// direct 1:1 match with the rules below and with realm.txt's own header.
//
// No problems found - verified TValue.fromName()'s "TV_" + uppercased-and-
// underscored book-noun text (e.g. "TV_MAGIC_BOOK") resolves correctly
// since fromName() does a plain TValue.valueOf() lookup by constant name,
// and every realm.txt book-noun has a matching TV_*_BOOK constant.

grammar Realm;

@header {
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.magic.MagicRealm;
    import uk.co.jackoftrades.middle.enums.Stats;

    import java.util.List;
    import java.util.ArrayList;
}

// "name:<realm name>" - starts a new realm record, e.g. "name:arcane".
name
        returns[String nameStr]
        :   NAME LCASE { $nameStr = $LCASE.getText(); }
        ;

// "stat:<STAT>" - the realm's primary spellcasting stat, e.g. "stat:INT".
stat
        returns[Stats statEnum]
        :   STAT UCASE {
                String raw = "STAT_" + $UCASE.getText().toUpperCase().trim();
                $statEnum = Stats.valueOf(raw);
            }
        ;

// "verb:<text>" - the verb used when casting, e.g. "verb:cast"/"verb:recite".
verb
        returns[String verbStr]
        :   VERB LCASE { $verbStr = $LCASE.getText(); }
        ;

// "spell-noun:<text>" - what a casting of this realm is called, e.g.
// "spell-noun:spell"/"spell-noun:prayer".
spell_noun
        returns[String nounStr]
        :   SPELL_NOUN LCASE { $nounStr = $LCASE.getText(); }
        ;

// "book-noun:<text>" - the item-type name of this realm's spellbooks, e.g.
// "book-noun:magic book" -> TValue.TV_MAGIC_BOOK.
book_noun
        returns[TValue bookTVal]
        :   BOOK_NOUN LCASE { $bookTVal = TValue.fromName("TV_" + $LCASE.getText().replace(' ', '_').toUpperCase()); }
        ;

// One full realm record: name, then stat/verb/spell-noun/book-noun in any order.
realm
        returns[MagicRealm magicRealm]
        @init {
            String nameInit = "";
            Stats statInit = Stats.STAT_NONE;
            String verbInit = "";
            String nounInit = "";
            TValue bookInit = TValue.TV_NONE;
        }
        @after {
            $magicRealm = new MagicRealm(nameInit, statInit, verbInit, nounInit, bookInit);
        }
        :   name { nameInit = $name.nameStr; }
        (   stat { statInit = $stat.statEnum; }
        |   verb { verbInit = $verb.verbStr; }
        |   spell_noun { nounInit = $spell_noun.nounStr; }
        |   book_noun { bookInit = $book_noun.bookTVal; }
        )+
        ;

// Top-level rule: the whole file is one or more realm records.
file
        returns[List<MagicRealm> realms]
        @init {
            $realms = new ArrayList<>();
        }
        :   (realm { $realms.add($realm.magicRealm); })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A blank line on its own (not part of a comment block).
EOL
        :   ' '* '\r'? '\n' -> skip
        ;

NAME
        :   'name:'
        ;

STAT
        :   'stat:'
        ;

VERB
        :   'verb:'
        ;

SPELL_NOUN
        :   'spell-noun:'
        ;

BOOK_NOUN
        :   'book-noun:'
        ;

// An uppercase stat code, e.g. "INT", "WIS".
UCASE
        :   ('A'..'Z')+
        ;

// Free-running lowercase text (with spaces) - used for name/verb/spell-noun/
// book-noun's text fields.
LCASE
        :   ('a'..'z' | ' ')+
        ;