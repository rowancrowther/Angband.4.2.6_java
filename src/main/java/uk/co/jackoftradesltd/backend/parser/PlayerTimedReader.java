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
 * <p><b>Where it differs from C.</b> The file needs a {@code record-count:} header that the upstream
 * file does not carry and at least one record, and the grammar accepts a narrower set of lines than
 * C's parser (see {@code docs/grammar_issues.md}). A value that fails to resolve drops only its own
 * record and is reported as a soft error, where C stops on the first bad line; the assembler is also
 * more lenient than C in places (see {@code docs/Assembler_Issues.md}). A missing file is an
 * {@code IOException}, where C quits.
 *
 * <p>Class commented in full on 261019.
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
     * Parses {@code filename} and returns just the assembled player timed effects, discarding
     * the soft-error channel (use {@link #parseWithResults} to inspect errors).
     *
     * <p>A file broken enough to fail the grammar comes back as an empty list with no hint why;
     * callers that need the reason use {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to read
     * @return the player timed effects read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<PlayerTimedEffect> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule, fails closed on any hard grammar/lexer error via {@link ParseErrors#throwIfAny()}, then
     * soft-checks the declared {@code record-count:} header against the number of records actually
     * parsed.
     *
     * <p>The count is this port's own addition - C's player timed parser registers no
     * {@code record-count} directive - so the check exists to catch a hand-edited file, not to
     * mirror the original. A wrong or unparseable count is a soft error and the records still come
     * back. The returned list is a copy, so the assembler cannot disturb the parse tree.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the records are used
     * @param errors       the soft-error sink, appended to on a record-count mismatch
     * @return a defensive copy of the parsed player-timed-effect records for the assembler
     * @throws IOException never thrown here, but declared by the {@link GrammarDriver.Extractor} contract
     */
    private static List<PlayerTimedParseRecord> extract(
            @NotNull PlayerTimedGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) throws IOException {
        PlayerTimedGrammar.FileContext output = parser.file();
        List<PlayerTimedParseRecord> records = output.results;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link PlayerTimedEffect}s together with any soft errors collected during parsing and
     * assembly. {@link #parse} is the items-only convenience over this.
     *
     * <p>The port of {@code run_parse_player_timed}. A missing file is an {@code IOException}, where
     * C quits. A hard grammar or lexer failure comes back as errors with no effects, not as an
     * exception, and an unresolvable record is skipped with a soft error while the rest still load.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the player-timed-effect data file to read
     * @return the parse result: the player-timed effects plus any error messages
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
