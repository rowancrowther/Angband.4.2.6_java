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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.gameconstants.GameConstantsAssembler;
import uk.co.jackoftradesltd.backend.parser.gameconstants.GameConstantsParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.gameconstants.GameConstantsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.gameconstants.GameConstantsLexer;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/constants.txt} into a single {@link GameConstantsData} by driving
 * {@code GameConstantsLexer} and {@code GameConstantsGrammar}, and hands it back to
 * {@code GameConstants.loadGameConstants}. The thin hand-written bridge between the generated grammar
 * code and the game.
 *
 * <p>Stands in for the {@code constants_parser} in {@code init.c}: {@code init_parse_constants} (the
 * table of {@code <category>:<label>:<value>} directives, each handled by a {@code parse_constants_*}
 * function, here {@code GameConstantsAssembler}), {@code run_parse_constants} (read the file, quitting
 * if it is missing) and {@code finish_parse_constants} (reject melee or ranged critical levels whose
 * cutoffs do not strictly increase, here {@code GameConstantsData.GameConstantsBuilder}'s
 * {@code checkCriticalLevelDataLists}).
 *
 * <p>Unlike the list readers in this package it does not implement {@code Reader} or delegate to
 * {@link uk.co.jackoftradesltd.channel.parser.GrammarDriver}: the assembler yields one aggregate, not
 * a {@code List}, so the lex/parse/assemble steps are written out in {@link #parseWithResults}.
 *
 * <p>Differences from C worth knowing:
 * <ul>
 *   <li>There is no {@code record-count:} header, as in C, so there is no count check.</li>
 *   <li>Any error means no constants. A grammar error is hard and a bad line or cutoff order is
 *   collected as a soft error, but in both cases the result carries {@code null} data and the
 *   messages. C stops at the first error; this collects every error the assembler finds. The caller
 *   treats any error as fatal, as C's {@code quit_fmt} does.</li>
 *   <li>The grammar is stricter than C's line parser about the shape of a line. See
 *   {@code docs/grammar_issues.md}.</li>
 *   <li>A file that cannot be read throws, where C quits.</li>
 * </ul>
 *
 * <p>Class GameConstantsReader coded before 260915, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class GameConstantsReader {
    /**
     * Logger used to report file-loading failures: an unreadable file is logged here before the
     * {@link IOException} is rethrown, and a hard grammar failure is logged before the empty result is
     * returned. C has no counterpart: it reports through {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Alias for {@link #parseWithResults}: unlike every other reader in this package, {@code parse}
     * does not unwrap to an items-only view - {@link GameConstantsParseResult} is a single aggregate
     * rather than a {@code List}, so there is nothing to discard and no soft-error channel to drop.
     * The two methods are identical; this one exists only so the class carries the conventional
     * {@code parse} entry point other readers expose.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the name of the file to parse
     * @return the assembled game constants plus any soft errors, or a null-data result on a hard
     * parse failure
     * @throws IOException if the file cannot be read
     */
    public GameConstantsParseResult parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename);
    }

    /**
     * Parses {@code constants.txt} and returns the full {@link GameConstantsParseResult}: the
     * assembled {@link GameConstantsData} together with any soft errors collected during assembly.
     * Unlike the other readers in this package, this does not delegate to 
     * {@link uk.co.jackoftradesltd.channel.parser.GrammarDriver} -
     * {@code GameConstantsAssembler} produces a single aggregate rather than a {@code List}, so the
     * lex/parse/assemble ritual is inlined here instead.
     *
     * <p>Two kinds of problem:
     * <ul>
     *   <li><b>Hard</b>, collected by {@link ParseErrors} and aborted by its {@code throwIfAny}: any
     *   lexer or grammar error. The result carries {@code null} data and those messages.</li>
     *   <li><b>Soft</b>, added to {@code errors} by {@code GameConstantsAssembler}: an unknown
     *   category or label, a wrong number of fields, a value that is not an {@code int}, an unknown
     *   message name, or critical-level cutoffs that do not strictly increase (C's
     *   {@code finish_parse_constants}). The builder returns {@code null} when {@code errors} is not
     *   empty, so the result again carries {@code null} data, with the messages.</li>
     * </ul>
     * Either way {@link GameConstantsParseResult#hasErrors()} is true and
     * {@link GameConstantsParseResult#getData()} is {@code null}. A clean file gives the data and no
     * errors. An empty file, or one of only comments, is clean and gives every constant as {@code 0}
     * and empty critical-level lists, as C's {@code mem_zalloc}ed {@code struct angband_constants}
     * would.
     *
     * <p>The second {@code throwIfAny} after assembling cannot fire: the assembler reports into
     * {@code errors}, not into the {@link ParseErrors} listener.
     *
     * <p>An {@link IOException} is logged and rethrown rather than wrapped. {@link #parse} is the
     * data-only convenience over this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the name of the file to parse
     * @return the assembled game constants plus any soft errors, or a null-data result on a hard
     * parse failure
     * @throws IOException if the file cannot be read
     */
    @CheckReturnValue
    public GameConstantsParseResult parseWithResults(@NotNull String filename) throws IOException {
        GameConstantsData data = null;
        ParseErrors errorCatcher = null;
        List<String> errors = new ArrayList<>();

        try {
            CharStream input = CharStreams.fromFileName(filename);
            GameConstantsLexer lexer = new GameConstantsLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GameConstantsGrammar parser = new GameConstantsGrammar(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);

            GameConstantsGrammar.FileContext output = parser.file();

            errorCatcher.throwIfAny();

            List<GameConstantsParseRecord> records = output.results;

            data = new GameConstantsAssembler().assemble(records, errors);

            errorCatcher.throwIfAny();
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        } catch (ParseCancellationException e) {
            logger.error("Error while parsing file {}", filename, e);
            return new GameConstantsParseResult(null, errorCatcher.getErrors());
        }

        return new GameConstantsParseResult(data, errors);
    }
}