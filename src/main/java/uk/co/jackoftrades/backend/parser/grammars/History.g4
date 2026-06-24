// Parser+lexer for lib/gamedata/history.txt - the player backstory "charts":
// weighted-random phrase tables, chained together (e.g. Human chart 1 -> 2
// -> 3 -> 50 -> ...) to build up a character's background text. Cf.
// src/init.c: struct file_parser history_parser (init.c:2545), directives
// "chart uint chart int next int roll" / "phrase str text" ->
// parse_history_chart/_phrase (init.c:2448-2477).
//
// This file is in good shape - verified field order/semantics against
// init.c, and verified against the actual data that no chart number's
// lines are ever split into two non-adjacent blocks (which `history` below
// would reject - see problem #1, which is a deliberate stricter check, not
// a bug).
//
// POTENTIAL PROBLEMS (both minor/latent, not active bugs):
//
//   1. `history` requires every chart:/phrase: pair for a given chart
//      number to be contiguous (it throws InvalidTokenFoundDuringParse if
//      a later pair's chart number differs from the block's first one).
//      The C parser is more lenient: parse_history_chart's findchart()
//      (init.c:2454) looks up charts by index across the *whole*
//      accumulated list, so it would still merge two same-numbered blocks
//      even if something else appeared between them in the file. Currently
//      moot - no chart index in history.txt is ever split into two
//      non-adjacent blocks - but worth knowing this grammar is stricter
//      than the format technically requires.
//
//   2. The C parser's parse_history_phrase (init.c:2469) *appends* text to
//      the current entry (string_append), implying one chart:/roll pair
//      could originally be followed by more than one phrase: line whose
//      text gets concatenated. `history` below only ever matches exactly
//      one `phrase` per `chart` (chart phrase)+, never chart phrase phrase.
//      No line in history.txt currently does this (every chart: is
//      followed by exactly one phrase:), so this is unused capacity rather
//      than a gap that's actually being hit.
//
// See "POTENTIAL SOLUTIONS" at the bottom of this file.

grammar History;

@header {
    import uk.co.jackoftrades.middle.player.PlayerHistoryEntry;
    import uk.co.jackoftrades.middle.player.PlayerHistoryChart;
    import uk.co.jackoftrades.backend.io.bespokeexceptions.InvalidTokenFoundDuringParse;

    import java.util.List;
    import java.util.ArrayList;
}

// "chart:<chart number>:<next chart, or 0>:<cumulative percentage cutoff>" -
// cf. parse_history_chart (init.c:2448). The "next chart" and "cutoff" are
// per-entry (`roll`), not per-chart - every entry sharing a chart number
// should repeat the same "next" value (see history.txt's own header note).
chart
        returns[int chartInt, int nextInt, int percInt]
        :   CHART chartNum=NUMBER COLON nextChartNum=NUMBER COLON perc=NUMBER EOL {
                $chartInt = Integer.parseInt($chartNum.getText());
                $nextInt = Integer.parseInt($nextChartNum.getText());
                $percInt = Integer.parseInt($perc.getText());
            }
        ;

// "phrase:<text>" - the actual backstory text for the preceding chart: line
// - cf. parse_history_phrase (init.c:2469). See problem #2 re: only ever
// matching one phrase per chart entry here.
phrase
        returns[String phraseStr]
        :   PHRASE STRING EOL { $phraseStr = $STRING.getText(); }
        ;

// One whole chart: a contiguous run of chart:/phrase: pairs that all share
// the same chart number, bounded by blank lines. See problem #1 re: the
// contiguity requirement enforced by the explicit consistency check below.
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

// Top-level rule: the whole file is one or more chart blocks.
file
        returns[List<PlayerHistoryChart> historyCharts]
        @init {
            $historyCharts = new ArrayList<>();
        }
        :   (history {
                $historyCharts.add($history.historyChart);
            })+ EOF
        ;

// Comment line: '#' to end of line, plus any blank lines immediately after.
COMMENT
        :   '#' (~'\n')* '\n'+ -> skip
        ;

// A line ending, INCLUDING any blank line on its own - deliberately not
// skipped (unlike every other grammar in this directory) since `history`
// uses EOL*/EOL+ to detect the blank-line boundaries between chart blocks.
EOL
        :   ' '* '\r'? '\n'
        ;

CHART
        :   'chart:'
        ;

PHRASE
        :   'phrase:'
        ;

// Field separator within a chart: line.
COLON
        :   ':'
        ;

// A bare non-negative integer - used for chart:'s 3 numeric fields.
NUMBER
        :   ('0'..'9')+
        ;

// Free-running backstory text - letters, spaces, and common punctuation.
STRING
        :   ('A'..'Z' | 'a'..'z' | ' ' | '.' | ',' | '-' | '0'..'9' | '\'')+
        ;

// POTENTIAL SOLUTIONS
//
//   1. Only worth relaxing if history.txt ever splits one chart index into
//      two non-adjacent blocks - at that point, track charts in a
//      Map<Integer, PlayerHistoryChart> across the whole `file` rule
//      (mirroring findchart()) instead of one-chart-per-`history`-block.
//
//   2. Only worth extending if a chart entry ever needs more than one
//      phrase: line - change `(chart phrase {...})+` to
//      `(chart (phrase {...})+)+` and concatenate the phrase text the same
//      way string_append does.