/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code and ANTLR4 grammars copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftradesltd.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.history.HistoryGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.history.HistoryLexer;
import uk.co.jackoftradesltd.backend.parser.history.HistoryAssembler;
import uk.co.jackoftradesltd.backend.parser.history.HistoryParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code history.txt} into {@link PlayerHistoryChart}s by driving {@code HistoryLexer} and
 * {@code HistoryGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code history_parser} in the C original's {@code init.c}: {@code init_parse_history}
 * (the two directives {@code chart:} and {@code phrase:}, which the grammar's {@code chart} and
 * {@code phrase} rules take), {@code run_parse_history} (a single call to
 * {@code parse_file_quit_not_found}) and the reading half of {@code finish_parse_history}.
 *
 * <p>The rest of C's work is done elsewhere. {@code parse_history_chart} and
 * {@code parse_history_phrase} are the grammar and {@link HistoryAssembler}: the grammar pairs each
 * {@code chart:} line with the {@code phrase:} line after it into a {@link HistoryParseRecord}, and
 * the assembler folds the records that share a chart number into one {@link PlayerHistoryChart}.
 * The second half of {@code finish_parse_history}, resolving each successor number to a chart with
 * {@code findchart}, is the assembler's second pass. The reversal of each chart's entry list that
 * {@code finish_parse_history} does is not needed, as the assembler appends in file order.
 * {@code cleanup_history} has no counterpart, as the garbage collector frees the charts. The
 * caller is {@code PlayerDataLoader.loadPlayerHistories()}, which registers the result in
 * {@code PlayerRegistry} before the races, each of which starts at a named chart.
 *
 * <p>The charts come back in first-appearance order where C's list is newest first, which
 * nothing can tell apart because the one later lookup goes by chart number. Each chart's entries
 * are in file order, as in C once reversed, and that order is what the roll depends on.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no charts and the messages, and a {@code record-count:} mismatch
 * is a soft error that leaves the charts that did parse loaded. The {@code record-count:} header
 * is Java-only, the upstream file has none, and it counts {@code chart:}/{@code phrase:} pairs, not
 * charts. A file that cannot be opened is an {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places: a {@code chart:} line with no {@code phrase:}
 * after it, a second {@code phrase:} for the same entry, a negative chart number and anything after
 * the roll on a {@code chart:} line all differ, and are written up in {@code docs/grammar_issues.md}.
 * The assembler's own differences, including its soft errors for a dangling or non-uniform
 * successor where C fails the load, are in {@code docs/Assembler_Issues.md}.
 *
 * <p>Class HistoryReader coded before 260915, commented in full on 261018.
 *
 * @author Rowan Crowther
 */
public class HistoryReader implements Reader<PlayerHistoryChart> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled charts, one per distinct chart number in
     * first-appearance order, discarding the error messages. The convenience form of
     * {@link #parseWithResults}, which is the one to call when the errors matter: with this one a
     * hard grammar error and a file with no charts look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the history data file to read
     * @return the charts read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<PlayerHistoryChart> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of
     * {@code chart:}/{@code phrase:} pairs parsed. No value is interpreted here: the chart, next and
     * roll text is parsed to {@code int}s by the assembler, and the phrase is copied through.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a missing {@code record-count:} header, a {@code phrase:} line before any
     * {@code chart:}, a {@code chart:} with no {@code phrase:}, a line the lexer does not know)
     * fails closed before the count check runs against whatever did parse. The grammar builds each
     * {@link HistoryParseRecord} itself, so the rows are only copied into a new list. The rule
     * needs at least one pair, where C accepts a file with none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here
     * @return the parse records, one per {@code chart:}/{@code phrase:} pair, in file order
     */
    private static @NotNull List<HistoryParseRecord> extract(
            @NotNull HistoryGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        HistoryGrammar.FileContext output = parser.file();
        List<HistoryParseRecord> records = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link PlayerHistoryChart}s and every error message collected on the way. This is the Java
     * form of C's {@code run_parse_history} followed by {@code finish_parse_history}, and the entry
     * point {@code PlayerDataLoader.loadPlayerHistories()} uses so that it can gate on
     * {@link ParseResult#hasErrors()} before registering the result. {@link GrammarDriver#run}
     * does the lex, parse, extract and assemble.
     *
     * <p>Where C's {@code finish_parse_history} returns {@code -1} and the load stops when a
     * successor names no chart, the assembler records a soft error and the chart loads without a
     * successor. A file that cannot be opened is an {@link IOException}, where C's
     * {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the history data file to load
     * @return the assembled charts plus any error messages; the charts are empty if the parse
     *         failed
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<PlayerHistoryChart> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                HistoryLexer::new,
                HistoryGrammar::new,
                HistoryReader::extract,
                new HistoryAssembler(), logger);
    }
}
