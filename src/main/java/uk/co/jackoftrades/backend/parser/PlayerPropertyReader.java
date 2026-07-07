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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.playerproperty.PlayerPropertyGrammar;
import uk.co.jackoftrades.backend.parser.grammars.playerproperty.PlayerPropertyLexer;
import uk.co.jackoftrades.backend.parser.playerproperty.PlayerPropertyAssembler;
import uk.co.jackoftrades.backend.parser.playerproperty.PlayerPropertyParseRecord;
import uk.co.jackoftrades.middle.player.PlayerProperty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code player_property.txt} into {@link PlayerProperty} objects by driving the
 * matching ANTLR-generated lexer/parser. A thin hand-written bridge that delegates the
 * shared load skeleton to {@link GrammarDriver} and contributes only the grammar-specific
 * {@link #extract} step, implementing the shared {@link Reader} contract (Java port of
 * {@code run_parse_player_prop} / {@code parse_player_prop_*} in {@code init.c}).
 *
 * @author Rowan Crowther
 */
public class PlayerPropertyReader implements Reader<PlayerProperty> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Load the file and return only the resolved {@link PlayerProperty} objects, discarding
     * the soft-error channel. Convenience form of {@link #parseWithResults(String)} for the
     * {@link Reader} contract; callers that need to inspect soft errors should use that method.
     *
     * @param filename the {@code player_property.txt} file to load.
     * @return the assembled player properties, in file order (empty if a hard grammar/lexer
     * error aborted the parse).
     * @throws IOException if the file cannot be read.
     */
    @NotNull
    @Override
    @Contract("_ -> _")
    public List<PlayerProperty> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Load the file into a {@link ParseResult} carrying both the resolved
     * {@link PlayerProperty} objects and the collected soft errors. The invariant load
     * skeleton (char stream, lex, parse, error wiring, assemble, cancellation handling)
     * lives in {@link GrammarDriver#run}; this method only names the lexer, parser,
     * {@link #extract} step and {@link PlayerPropertyAssembler} to use.
     *
     * @param filename the {@code player_property.txt} file to load.
     * @return the parse result: assembled items plus any soft errors.
     * @throws IOException if the file cannot be read.
     */
    public ParseResult<PlayerProperty> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerPropertyLexer::new,
                PlayerPropertyGrammar::new,
                PlayerPropertyReader::extract,
                new PlayerPropertyAssembler(),
                logger);
    }

    /**
     * Grammar-specific residue handed to {@link GrammarDriver#run}: run the top-level
     * {@code file} rule, surface any accumulated hard grammar/lexer errors, then verify the
     * declared {@code record-count:} header against the number of records actually parsed.
     * The ordering matters - {@link ParseErrors#throwIfAny()} must fire after {@code file()}
     * but before the (soft) record-count check, so a hard parse error aborts before the count
     * is even considered.
     *
     * @param parser       the grammar bound to the token stream, ready to run.
     * @param errorCatcher the hard-error channel; {@link ParseErrors#throwIfAny()} throws
     *                     (and the driver turns that into an empty, errors-carrying result)
     *                     if the lexer/parser reported anything.
     * @param errors       the soft-error sink, passed through to the record-count check.
     * @return the parsed records (a defensive copy), in file order.
     */
    private static List<PlayerPropertyParseRecord> extract(
            @NotNull PlayerPropertyGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        PlayerPropertyGrammar.FileContext output = parser.file();
        List<PlayerPropertyParseRecord> result = output.properties;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecords;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);
        return new ArrayList<>(result);
    }
}