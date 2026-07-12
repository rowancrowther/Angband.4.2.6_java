parser grammar HistoryGrammar;

options { tokenVocab = HistoryLexer; }

@header {
    import uk.co.jackoftrades.backend.parser.history.HistoryParseRecord;

    import java.util.List;
    import java.util.ArrayList;
}

recordCount
        returns[String count]
        :   RECORD_COUNT INTEGER { $count = $INTEGER.getText(); }
        ;

chart
        returns[String currentChart, String nextChart, String percent, int line]
        :   CHART cc=INTEGER COLON nc=INTEGER COLON p=INTEGER {
                $currentChart = $cc.getText();
                $nextChart = $nc.getText();
                $percent = $p.getText();
                $line = $start.getLine();
            }
        ;

phrase
        returns[String phraseStr]
        :   PHRASE STRING { $phraseStr = $STRING.getText(); }
        ;

record
        returns[HistoryParseRecord hist]
        @init {
            String currentInit = "";
            String nextInit = "";
            String percentInit = "";
            String phraseInit = "";
            int lineInit = 0;
        }
        @after {
            $hist = new HistoryParseRecord(currentInit,
                nextInit, percentInit, phraseInit,
                lineInit);
        }
        :   chart {
                currentInit = $chart.currentChart;
                nextInit    = $chart.nextChart;
                percentInit = $chart.percent;
                lineInit    = $chart.line;
            }
            phrase { phraseInit = $phrase.phraseStr; }
        ;

file
        returns[List<HistoryParseRecord> records, String declaredRecordCount]
        : {
            { $records = new ArrayList<>(); }
        }   recordCount { $declaredRecordCount = $recordCount.count; }
            ( record { $records.add($record.hist); } )+ EOF
        ;