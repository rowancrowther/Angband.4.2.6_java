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

package uk.co.jackoftrades.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.hint.HintGrammar;
import uk.co.jackoftrades.backend.parser.grammars.hint.HintLexer;
import uk.co.jackoftrades.backend.parser.hint.HintAssembler;
import uk.co.jackoftrades.backend.parser.hint.HintParseRecord;
import uk.co.jackoftrades.middle.game.Hint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code hints.txt} into a list of {@link Hint}s.
 *
 * <p>Like the other list-shaped readers it is a thin front for {@link GrammarDriver}: it supplies
 * the four grammar-specific knobs ({@link HintLexer}, {@link HintGrammar}, the {@link #extract}
 * step, and a {@link HintAssembler}) and lets the shared driver own the ANTLR plumbing, the
 * error-listener install, and the {@link ParseResult} wrapping. Ports the load side of C's
 * {@code hints_parser} ({@code init.c}).
 *
 * @author Rowan Crowther
 */
public class HintReader implements Reader<Hint> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the file and return just the assembled hints, discarding the error list.
     *
     * @param filename the hint data file to read
     * @return the parsed hints
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Hint> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return the assembled hints together with any soft errors (in practice only
     * a record-count mismatch, since hints never fail assembly). The entry point
     * {@code GameConstants.loadHints()} uses so it can gate on {@link ParseResult#hasErrors()} before
     * storing the result.
     *
     * @param filename the hint data file to read
     * @return the parsed hints plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<Hint> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                HintLexer::new,
                HintGrammar::new,
                HintReader::extract,
                new HintAssembler(), logger);
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the parser's
     * {@code file} rule and pull the raw records out of its result.
     *
     * <p>Ordering is deliberate - {@link ParseErrors#throwIfAny()} fires straight after the parse so
     * that a hard grammar error (e.g. a missing {@code record-count} header) fails closed before the
     * soft {@link GrammarDriver#checkRecordCount} check runs against whatever did parse.
     *
     * @param parser       the parser positioned at the start of the file
     * @param errorCatcher the shared collector for hard grammar/lexer errors
     * @param errors       the soft-error sink, passed on to the record-count check
     * @return the raw parse records, ready for the assembler
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
}
