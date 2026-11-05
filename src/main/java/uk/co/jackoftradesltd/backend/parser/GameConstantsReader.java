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
 *   <li>Any error means no constants, and the load stops, as C's {@code quit_fmt} does. A grammar
 *   or lexer error is thrown by {@link ParseErrors#throwIfAny()}, after every such error in the file
 *   has been logged; C stops at the first. A bad line or a bad cutoff order is thrown by the
 *   assembler or the builder at the first one found, as in C.</li>
 *   <li>The grammar is stricter than C's line parser about the shape of a line. See
 *   {@code docs/grammar_issues.md}.</li>
 *   <li>A file that cannot be read throws, where C quits.</li>
 * </ul>
 *
 * <p>Class GameConstantsReader coded before 260915, commented in full on 261019, updated on 261104
 * for the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class GameConstantsReader {
    /**
     * Logger used to report an unreadable file, which is logged here before the {@link IOException}
     * is rethrown. Grammar and assembler errors are not logged here: {@link ParseErrors} and the
     * assembler log their own. C has no counterpart: it reports through {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019, updated on 261104 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Alias for {@link #parseWithResults}: the two methods are identical. Every other reader in this
     * package has a {@code parse} that unwraps a {@code ParseResult} to its items; this reader returns
     * one {@link GameConstantsData} with no wrapper, so there is nothing to unwrap. This method exists
     * only so the class carries the conventional {@code parse} entry point other readers expose.
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261104 once the
     * {@code GameConstantsParseResult} wrapper was deleted.
     *
     * @param filename the name of the file to parse
     * @return the assembled game constants, never {@code null}
     * @throws IOException if the file cannot be read
     */
    public GameConstantsData parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename);
    }

    /**
     * Parses {@code constants.txt} and returns the assembled {@link GameConstantsData}. Unlike the
     * other readers in this package, this does not delegate to
     * {@link uk.co.jackoftradesltd.channel.parser.GrammarDriver} -
     * {@code GameConstantsAssembler} produces a single aggregate rather than a {@code List}, so the
     * lex/parse/assemble ritual is inlined here instead.
     *
     * <p>Every error is fatal, as in C's {@code run_parser()}, and none is caught here:
     * <ul>
     *   <li>a lexer or grammar error is collected by {@link ParseErrors} and thrown by its
     *   {@code throwIfAny}, before anything is assembled;</li>
     *   <li>an unknown category or label, a wrong number of fields, a value that is not an
     *   {@code int}, or an unknown message name is thrown by {@code GameConstantsAssembler} as a
     *   {@code DataFileParseException};</li>
     *   <li>critical-level cutoffs that do not strictly increase (C's {@code finish_parse_constants})
     *   are thrown by {@code GameConstantsData.GameConstantsBuilder}, also as a
     *   {@code DataFileParseException}.</li>
     * </ul>
     * A clean file gives the data. An empty file, or one of only comments, is clean and gives every
     * constant as {@code 0} and empty critical-level lists, as C's {@code mem_zalloc}ed
     * {@code struct angband_constants} would.
     *
     * <p>The second {@code throwIfAny} after assembling cannot fire: the assembler throws its own
     * exceptions and adds nothing to the {@link ParseErrors} listener.
     *
     * <p>An {@link IOException} is logged and rethrown rather than wrapped. {@link #parse} is an alias
     * for this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on 261104
     * for the removal of the soft-error path.
     *
     * @param filename the name of the file to parse
     * @return the assembled game constants, never {@code null}
     * @throws IOException if the file cannot be read
     */
    @CheckReturnValue
    public GameConstantsData parseWithResults(@NotNull String filename) throws IOException {
        ParseErrors errorCatcher = null;

        try {
            CharStream input = CharStreams.fromFileName(filename);
            GameConstantsLexer lexer = new GameConstantsLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            GameConstantsGrammar parser = new GameConstantsGrammar(tokens);

            errorCatcher = ParseErrors.install(lexer, parser, filename);

            GameConstantsGrammar.FileContext output = parser.file();

            errorCatcher.throwIfAny();

            List<GameConstantsParseRecord> records = output.results;

            GameConstantsData data = new GameConstantsAssembler().assemble(records, filename);

            errorCatcher.throwIfAny();
            
            return data;
        } catch (IOException e) {
            logger.error("Error while loading file {}", filename, e);
            throw e;
        }
    }
}