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
import uk.co.jackoftradesltd.backend.parser.grammars.hint.HintGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.hint.HintLexer;
import uk.co.jackoftradesltd.backend.parser.hint.HintAssembler;
import uk.co.jackoftradesltd.backend.parser.hint.HintParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.game.Hint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code hints.txt} into {@link Hint}s by driving {@code HintLexer} and {@code HintGrammar},
 * the hand-written bridge between the generated parser and the game. It stands in for
 * {@code hints_parser} in the C original's {@code init.c}: {@code init_parse_hints} (the single
 * directive {@code H:}, which the grammar's {@code hint} rule takes), {@code run_parse_hints} (a
 * single call to {@code parse_file_quit_not_found}) and {@code finish_parse_hints}.
 *
 * <p>The rest of C's work is done elsewhere. {@code parse_hint}, which copies the text of an
 * {@code H:} line into a new {@code struct hint}, is {@link HintAssembler}, and C's global
 * {@code hints} list has no counterpart in this class: the list this reader returns is installed
 * in {@code MiscRegistry} by {@code MiscDataLoader.loadHints()}. {@code cleanup_hints} has no
 * counterpart at all, as the garbage collector frees the hints. C pushes each new hint onto the
 * head of its list, so it holds them in reverse file order; this reader hands them over in file
 * order, which nothing can tell apart because {@code random_hint} in {@code ui-store.c} treats
 * every position alike.
 *
 * <p>The text of a hint is everything after {@code H:} to the end of the line, as it is in C, so
 * leading and trailing spaces and any {@code :} or {@code #} in it are kept.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, which nothing here catches, and
 * no hints are returned. Hints cannot fail assembly, so that is the only error a readable file can
 * give. A file that cannot be opened is an {@link IOException}, where C quits.
 *
 * <p>Class HintReader coded before 260915, commented in full on 261018, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class HintReader implements Reader<Hint> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled hints in file order, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. A grammar error throws exactly as
     * it does there, so an empty list only ever means a file with no hints.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the hint data file to read
     * @return every hint in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<Hint> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the text is copied into a
     * {@link Hint} by the assembler.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (an {@code H:} line with no text, a line the lexer does not know) fails closed
     * before any row is used. The grammar builds each {@link HintParseRecord} itself, so the rows
     * are only copied into a new list. The rule takes zero or more hints, as C accepts a file with
     * none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parsed hint records, one per {@code H:} line, in file order
     */
    private static List<HintParseRecord> extract(
            @NotNull HintGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        HintGrammar.FileContext output = parser.file();
        List<HintParseRecord> records = output.hints;
        
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link Hint}s, in file order, wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_hints} followed by
     * {@code finish_parse_hints}, and the entry point {@code MiscDataLoader.loadHints()} uses.
     *
     * <p>There are three outcomes. A clean file gives the hints; the shipped file gives 99. A lexer
     * or grammar error throws a {@code ParseCancellationException} and gives no result. A file that
     * cannot be opened is an {@link IOException}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the hint data file to read
     * @return every hint in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<Hint> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                HintLexer::new,
                HintGrammar::new,
                HintReader::extract,
                new HintAssembler(), logger);
    }
}
