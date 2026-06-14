grammar Realm;

@header {
    import uk.co.jackoftrades.middle.objects.enums.TValue;
    import uk.co.jackoftrades.middle.magic.MagicRealm;
    import uk.co.jackoftrades.middle.enums.Stats;

    import java.util.List;
    import java.util.ArrayList;
}

name
        returns[String nameStr]
        :   NAME LCASE { $nameStr = $LCASE.getText(); }
        ;

stat
        returns[Stats statEnum]
        :   STAT UCASE {
                String raw = "STAT_" + $UCASE.getText().toUpperCase().trim();
                $statEnum = Stats.valueOf(raw);
            }
        ;

verb
        returns[String verbStr]
        :   VERB LCASE { $verbStr = $LCASE.getText(); }
        ;

spell_noun
        returns[String nounStr]
        :   SPELL_NOUN LCASE { $nounStr = $LCASE.getText(); }
        ;

book_noun
        returns[TValue bookTVal]
        :   BOOK_NOUN LCASE { $bookTVal = TValue.fromName("TV_" + $LCASE.getText().replace(' ', '_').toUpperCase()); }
        ;

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

file
        returns[List<MagicRealm> realms]
        @init {
            $realms = new ArrayList<>();
        }
        :   (realm { $realms.add($realm.magicRealm); })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

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

UCASE
        :   ('A'..'Z')+
        ;

LCASE
        :   ('a'..'z' | ' ')+
        ;