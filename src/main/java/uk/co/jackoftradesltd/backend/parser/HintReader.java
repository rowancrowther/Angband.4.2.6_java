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
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no hints and the messages, and a {@code record-count:} mismatch is
 * a soft error that leaves the hints that did parse loaded. The {@code record-count:} header is
 * Java-only; the upstream file has none. A file that cannot be opened is an {@link IOException},
 * where C quits. Hints cannot fail assembly, so a mismatch is the only soft error.
 *
 * <p>Class HintReader coded before 260915, commented in full on 261018.
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
     * Reads {@code filename} and returns the assembled hints in file order, discarding the error
     * messages. The convenience form of {@link #parseWithResults}, which is the one to call when
     * the errors matter: with this one a hard grammar error and a file with no hints look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the hint data file to read
     * @return the hints read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<Hint> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * and soft-checks the {@code record-count:} header against the number of {@code H:} lines
     * parsed. No value is interpreted here: the text is copied into a {@link Hint} by the assembler.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a missing {@code record-count:} header, an {@code H:} line with no text, a
     * line the lexer does not know) fails closed before the count check runs against whatever did
     * parse. The grammar builds each {@link HintParseRecord} itself, so the rows are only copied
     * into a new list. The rule takes zero or more hints, as C accepts a file with none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here
     * @return the parsed hint records, one per {@code H:} line, in file order
     */
    private static List<HintParseRecord> extract(
            @NotNull HintGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        HintGrammar.FileContext output = parser.file();
        List<HintParseRecord> records = output.hints;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled {@link Hint}s
     * in file order, and every error message collected on the way. This is the Java form of C's
     * {@code run_parse_hints} followed by {@code finish_parse_hints}, and the entry point
     * {@code MiscDataLoader.loadHints()} uses so that it can gate on {@link ParseResult#hasErrors()}
     * before storing the result.
     *
     * <p>There are three outcomes. A clean file gives the hints and no errors; the shipped file
     * gives 99. A lexer or grammar error gives no hints and the parser's messages. A
     * {@code record-count:} that disagrees with the number of {@code H:} lines gives every hint
     * beside the message. A file that cannot be opened is an {@link IOException}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the hint data file to read
     * @return the parse result: the hints plus any error messages
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
