grammar History;

@header {
    import uk.co.jackoftrades.middle.player.PlayerHistoryEntry;
    import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

    import java.util.List;
    import java.util.ArrayList;
}

chart
        returns[int chartInt, int nextInt, int percInt]
        :   CHART chartNum=NUMBER COLON nextChartNum=NUMBER COLON perc=NUMBER EOL {
                $chartInt = Integer.parseInt($chartNum.getText());
                $nextInt = Integer.parseInt($nextChartNum.getText());
                $percInt = Integer.parseInt($perc.getText());
            }
        ;

phrase
        returns[String phraseStr]
        :   PHRASE STRING EOL { $phraseStr = $STRING.getText(); }
        ;

history
        returns[PlayerHistoryChart historyChart]
        @init{
            int chartNumber = 0;
            List<PlayerHistoryEntry> entries = new ArrayList<>();
        }
        @after {
            $historyChart = new PlayerHistoryChart(chartNumber);
            for (PlayerHistoryEntry entry : entries)
                $historyChart.addEntry(entry);
        }
        :
            EOL*
            (chart
            phrase {
                int chartNum   = $chart.chartInt;
                int nextChart  = $chart.nextInt;
                int percentage = $chart.percInt;
                String string  = $phrase.phraseStr;
                PlayerHistoryEntry entry;
                entry = new PlayerHistoryEntry(chartNum, nextChart, percentage, string);
                entries.add(entry);
                if (chartNumber == 0)
                    chartNumber = chartNum;
                else if (chartNumber != chartNum)
                    throw new InvalidTokenFoundDuringParse("History entry found out of order: " + chartNum + ":"
                    + nextChart + ":" + percentage + ":" + string);
            })+
            EOL+
        ;

file
        returns[List<PlayerHistoryChart> historyCharts]
        @init {
            $historyCharts = new ArrayList<>();
        }
        :   (history {
                $historyCharts.add($history.historyChart);
            })+ EOF
        ;

COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

EOL
        :   ' '* '\r'? '\n'
        ;

CHART
        :   'chart:'
        ;

PHRASE
        :   'phrase:'
        ;

COLON
        :   ':'
        ;

NUMBER
        :   ('0'..'9')+
        ;

STRING
        :   ('A'..'Z' | 'a'..'z' | ' ' | '.' | ',' | '-' | '0'..'9' | '\'')+
        ;