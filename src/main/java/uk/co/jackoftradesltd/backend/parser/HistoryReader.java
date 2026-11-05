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
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a record the assembler
 * refuses (a bad chart number, a successor that names no chart or disagrees with an earlier one)
 * is thrown as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is
 * caught here, and no charts are returned. A file that cannot be opened is an
 * {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places: a {@code chart:} line with no {@code phrase:}
 * after it, a second {@code phrase:} for the same entry, a negative chart number and anything after
 * the roll on a {@code chart:} line all differ, and are written up in {@code docs/grammar_issues.md}.
 * The assembler's own differences are in {@code docs/Assembler_Issues.md}.
 *
 * <p>Class HistoryReader coded before 260915, commented in full on 261018, updated on 261105 for
 * the removal of the soft-error path and the {@code record-count:} header.
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
     * first-appearance order, unwrapped from the {@link ParseResult} that
     * {@link #parseWithResults} gives. Any error throws exactly as it does there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the history data file to read
     * @return every chart in the file, in first-appearance order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<PlayerHistoryChart> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the chart, next and roll text
     * is parsed to {@code int}s by the assembler, and the phrase is copied through.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a {@code phrase:} line before any {@code chart:}, a {@code chart:} with no
     * {@code phrase:}, a line the lexer does not know) fails closed before any row is used. The
     * grammar builds each {@link HistoryParseRecord} itself, so the rows are only copied into a new
     * list. The rule needs at least one pair, where C accepts a file with none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parse records, one per {@code chart:}/{@code phrase:} pair, in file order
     */
    private static @NotNull List<HistoryParseRecord> extract(
            @NotNull HistoryGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        HistoryGrammar.FileContext output = parser.file();
        List<HistoryParseRecord> records = output.records;
        
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link PlayerHistoryChart}s wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_history} followed by
     * {@code finish_parse_history}, and the entry point
     * {@code PlayerDataLoader.loadPlayerHistories()} uses. {@link GrammarDriver#run} does the lex,
     * parse, extract and assemble.
     *
     * <p>As C's {@code finish_parse_history} returns {@code -1} and the load stops when a successor
     * names no chart, the assembler throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException} there, as it does for a
     * bad chart number; a grammar error throws a {@code ParseCancellationException}. No result is
     * returned for a file that fails. A file that cannot be opened is an {@link IOException}, where
     * C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the history data file to load
     * @return every chart in the file, in first-appearance order
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
