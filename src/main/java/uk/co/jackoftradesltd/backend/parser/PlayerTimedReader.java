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
import uk.co.jackoftradesltd.backend.parser.grammars.playertimed.PlayerTimedGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.playertimed.PlayerTimedLexer;
import uk.co.jackoftradesltd.backend.parser.playertimed.PlayerTimedAssembler;
import uk.co.jackoftradesltd.backend.parser.playertimed.PlayerTimedParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for {@code lib/gamedata/player_timed.txt}, producing one {@link PlayerTimedEffect} per
 * {@code name:} record (the 53 timed effects, {@code FAST} to {@code FREE_ACT}, in the order of C's
 * {@code list-player-timed.h}). Wires the generated {@code PlayerTimedLexer}/{@code PlayerTimedGrammar}
 * to {@link PlayerTimedAssembler} through the shared {@link GrammarDriver}. This is the reader
 * {@code PlayerDataLoader.loadPlayerTimedProperties()} drives.
 *
 * <p><b>What it stands in for.</b> C's {@code player_timed_parser} in {@code player-timed.c}:
 * {@code init_parse_player_timed} is the grammar and {@code run_parse_player_timed} is
 * {@link GrammarDriver#run}. The twenty {@code parse_player_timed_*} handlers are split between the
 * grammar (which directive is legal where) and {@link PlayerTimedAssembler} (what each value means).
 * {@code finish_parse_player_timed} only destroys the parser, and {@code cleanup_player_timed} resets
 * a static array this port does not have, so neither needs a port. The {@code PY_FOOD_*} globals that
 * C fills in {@code parse_player_timed_grade} are filled by {@code PlayerRegistry.setPlayerTimedEffects}
 * instead.
 *
 * <p><b>Load order.</b> A {@code brand:} or {@code slay:} line resolves its code through the object
 * registry, so {@code brand.txt} and {@code slay.txt} must already be loaded when this runs, as in
 * C's {@code init_arrays}. A grade's food figures are scaled by {@code player:food-value}, so
 * {@code constants.txt} must be loaded too.
 *
 * <p><b>Errors.</b> As C stops on the first bad line, so does this reader: a syntax error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a value that fails to resolve
 * is thrown by the assembler as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here, and
 * no effects are returned. A missing file is an {@code IOException}, where C quits.
 *
 * <p><b>Where it differs from C.</b> The file needs at least one record, and the grammar accepts a
 * narrower set of lines than C's parser (see {@code docs/grammar_issues.md}). The assembler is
 * also more lenient than C in places (see {@code docs/Assembler_Issues.md}).
 *
 * <p>Class commented in full on 261019, updated on 261105 for the removal of the soft-error path
 * and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class PlayerTimedReader implements Reader<PlayerTimedEffect> {
    /**
     * Logger handed to {@link GrammarDriver#run}, so an I/O failure opening the data file is
     * reported under this reader's name. C has no counterpart: {@code run_parse_player_timed} calls
     * {@code parse_file_quit_not_found}, which quits on a missing file, where this reader logs and
     * throws an {@code IOException}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled player timed effects, unwrapped from
     * the {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it
     * does there.
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every player timed effect in the file, in file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<PlayerTimedEffect> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule and fails closed on any hard grammar/lexer error via {@link ParseErrors#throwIfAny()},
     * which throws a {@code ParseCancellationException} before any record is used.
     *
     * <p>The returned list is a copy, so the assembler cannot disturb the parse tree.
     *
     * <p>Function extract coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the records are used
     * @return a defensive copy of the parsed player-timed-effect records for the assembler
     */
    private static List<PlayerTimedParseRecord> extract(
            @NotNull PlayerTimedGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        PlayerTimedGrammar.FileContext output = parser.file();
        List<PlayerTimedParseRecord> records = output.results;
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Parses {@code filename} and returns the assembled {@link PlayerTimedEffect}s wrapped in a
     * {@link ParseResult}. {@link #parse} is the items-only convenience over this.
     *
     * <p>The port of {@code run_parse_player_timed}. A missing file is an {@code IOException}, where
     * C quits. A hard grammar or lexer failure throws a {@code ParseCancellationException}, and an
     * unresolvable record throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}; no result is returned
     * for a file that fails.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the player-timed-effect data file to read
     * @return every player timed effect in the file, in file order
     * @throws IOException if the file cannot be read
     */
    public ParseResult<PlayerTimedEffect> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerTimedLexer::new,
                PlayerTimedGrammar::new,
                PlayerTimedReader::extract,
                new PlayerTimedAssembler(), logger);
    }
}
