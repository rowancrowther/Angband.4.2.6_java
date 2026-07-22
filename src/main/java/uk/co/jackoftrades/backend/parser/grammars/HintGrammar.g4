parser grammar HintGrammar;

options { tokenVocab = HintLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.hint.HintParseRecord;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

hint
        returns[String hintLine, int line]
        :   HINT STRING { $hintLine = $STRING.getText();
                          $line = $start.getLine(); }
        ;

hintRecord
        returns[HintParseRecord record]
        :   hint { $record = new HintParseRecord($hint.hintLine,
                $hint.line); }
        ;

file
        returns[String declaredRecordCount, List<HintParseRecord> hints]
        @init { $hints = new ArrayList<>(); }
        :   recordCount { $declaredRecordCount = $recordCount.count; }
            ( hintRecord { $hints.add($hintRecord.record); })+ EOF
        ;
