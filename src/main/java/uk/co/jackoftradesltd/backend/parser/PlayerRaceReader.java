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
import uk.co.jackoftradesltd.backend.parser.grammars.playerrace.PlayerRaceGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.playerrace.PlayerRaceLexer;
import uk.co.jackoftradesltd.backend.parser.playerrace.PlayerRaceAssembler;
import uk.co.jackoftradesltd.backend.parser.playerrace.PlayerRaceParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.player.PlayerRace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code p_race.txt} into {@link PlayerRace}s by driving {@code PlayerRaceLexer} and
 * {@code PlayerRaceGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code p_race_parser} in the C original's {@code init.c}: {@code init_parse_p_race}
 * (the directives {@code name}, {@code stats}, the eleven {@code skill-*} lines, {@code hitdie},
 * {@code exp}, {@code infravision}, {@code history}, {@code age}, {@code height}, {@code weight},
 * {@code obj-flags}, {@code player-flags} and {@code values}, which the grammar's {@code playerRace}
 * rule takes), {@code run_parse_p_race} (a single call to {@code parse_file_quit_not_found}) and the
 * list-building half of {@code finish_parse_p_race}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_p_race_*} handlers are
 * {@link PlayerRaceAssembler}: it reads each number, resolves the flag, skill, stat and resist names
 * to their enums, and looks up the {@code history:} chart and the body through {@code PlayerRegistry}
 * where C uses {@code findchart} and a body index of 0. {@code cleanup_p_race} has no counterpart, as
 * the garbage collector frees the races. The caller is {@code PlayerDataLoader.loadPlayerRaces()},
 * which runs after the bodies and history charts the races refer to.
 *
 * <p>The races come back in file order. C's list is newest first, because {@code parse_p_race_name}
 * pushes each new race on the front, and {@code finish_parse_p_race} then numbers {@code ridx} down
 * the list so that the first race in the file is {@code ridx} 0. The port's list position is that
 * {@code ridx}, so {@code PlayerRace.getRaceFromIndex} and the birth code that takes C's list head,
 * the last race in the file, both work from file order. The assembler passes 0 for every race's own
 * index field instead of numbering it, which is written up in {@code docs/Assembler_Issues.md}.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a value the assembler cannot
 * resolve is thrown as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}.
 * Neither is caught here, and no races are returned. A file that cannot be opened is an
 * {@link IOException}, where C quits.
 *
 * <p>The grammar is stricter than C in several places: a {@code name:} line with nothing after it,
 * and flags or values separated by spaces alone, are errors where C accepts them, and a file with
 * no races is an error where C accepts an empty file. They are written up in
 * {@code docs/grammar_issues.md}. The assembler's own differences are in
 * {@code docs/Assembler_Issues.md}.
 *
 * <p>Class PlayerRaceReader coded before 260915, commented in full on 261018, updated on 261105 for
 * the removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class PlayerRaceReader implements Reader<PlayerRace> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled races in file order, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it does
     * there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the player-race data file to read
     * @return every race in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<PlayerRace> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: numbers, flag names and the
     * history chart index stay as text for the assembler, and the name is copied through.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a directive line before any {@code name:}, a {@code name:} with nothing after
     * it, a line the lexer does not know) fails closed before any row is used. The grammar builds
     * each {@link PlayerRaceParseRecord} itself, so the rows are only copied into a new list. The
     * rule needs at least one race, where C accepts a file with none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parse records, one per {@code name:} block, in file order
     */
    private static List<PlayerRaceParseRecord> extract(
            @NotNull PlayerRaceGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        PlayerRaceGrammar.FileContext output = parser.file();
        List<PlayerRaceParseRecord> result = output.races;
        errorCatcher.throwIfAny();

        return new ArrayList<>(result);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link PlayerRace}s wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_p_race} followed by
     * {@code finish_parse_p_race}, and the entry point {@code PlayerDataLoader.loadPlayerRaces()}
     * uses. {@link GrammarDriver#run} does the lex, parse, extract and assemble.
     *
     * <p>As C fails the load on a flag, resist or history chart it cannot resolve, the assembler
     * throws a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException} there, and a
     * grammar error throws a {@code ParseCancellationException}; no result is returned for a file
     * that fails. The assembler needs the body and history registries loaded first, and throws
     * {@link IllegalStateException} from {@code PlayerRegistry} if they are not. A file that cannot
     * be opened is an {@link IOException}, where C's {@code parse_file_quit_not_found} quits.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the player-race data file to load
     * @return every race in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    public @NotNull ParseResult<PlayerRace> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PlayerRaceLexer::new,
                PlayerRaceGrammar::new,
                PlayerRaceReader::extract,
                new PlayerRaceAssembler(), logger);
    }
}
