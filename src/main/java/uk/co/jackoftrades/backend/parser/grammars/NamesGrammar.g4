parser grammar NamesGrammar;

options { tokenVocab = NamesLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.names.NamesParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

section
        returns[String sectionNum]
        :   SECTION INTEGER { $sectionNum = $INTEGER.getText(); }
        ;

word
        returns[String wordVal]
        :   WORD STRING { $wordVal = $STRING.getText(); }
        ;

record
        returns[NamesParseRecord rec]
        @init { List<String> wordList = new ArrayList<>(); String sec = ""; }
        @after {
            $rec = new NamesParseRecord(sec, wordList);
        }
        :   section { sec = $section.sectionNum; }
            (word { wordList.add($word.wordVal); })+
        ;

file
    returns[String declaredRecordCount, List<NamesParseRecord> records]
    @init { $records = new ArrayList<>(); }
    :   recordCount { $declaredRecordCount = $recordCount.count; }
        (record { $records.add($record.rec); })+ EOF
    ;
