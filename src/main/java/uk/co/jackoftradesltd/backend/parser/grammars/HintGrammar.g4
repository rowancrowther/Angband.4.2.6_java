parser grammar HintGrammar;

options { tokenVocab = HintLexer; }

@header {
    import uk.co.jackoftradesltd.backend.parser.hint.HintParseRecord;
}

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
        returns[List<HintParseRecord> hints]
        @init { $hints = new ArrayList<>(); }
        :   ( hintRecord { $hints.add($hintRecord.record); })* EOF
        ;
