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
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileAssembler;
import uk.co.jackoftradesltd.backend.parser.dungeonprofile.DungeonProfileParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile.DungeonProfileGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.dungeonprofile.DungeonProfileLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code lib/gamedata/dungeon_profile.txt} into {@link CaveProfile}s. The four
 * {@code dungeon_profile}-specific pieces — {@link DungeonProfileLexer}, {@link DungeonProfileGrammar},
 * {@link #extract} and {@link DungeonProfileAssembler} — are handed to {@link GrammarDriver}, which
 * owns the shared lex/parse/assemble ritual every {@code lib/gamedata} reader follows.
 *
 * <p><b>C counterpart:</b> {@code profile_parser} in {@code generate.c}, run as the first of the
 * three parsers in {@code run_template_parser}. Its seven
 * {@code parse_profile_*} handlers, registered by {@code init_parse_profile}, are performed by
 * {@link DungeonProfileAssembler} and its tunnel, streamer and room sub-assemblers, as is the
 * list-building half of {@code finish_parse_profile}; {@code run_parse_profile}
 * ({@code parse_file_quit_not_found}) is the file open inside {@link GrammarDriver}, where a
 * missing file is an {@link IOException} for the caller to turn into a fatal error. This class
 * itself only wires the grammar to the driver, so it has no C logic of its own to mismatch.
 *
 * <p><b>Order and count of the result:</b> C builds the list by prepending, then
 * {@code finish_parse_profile} fills {@code cave_profiles} from the far end, so the array runs in
 * file order, and {@code z_info->profile_max} is the number of records. The result here is also in
 * file order, and the count is just the size of the list; nothing in the Java needs a separate
 * counter.
 *
 * <p><b>Where the Java differs from C</b> (the grammar half is recorded in
 * {@code docs/grammar_issues.md}, the assembler half in {@code docs/Assembler_Issues.md}): C
 * registers seven independent one-line directives, so after the opening {@code name:} it accepts
 * them in any order, repeated, left out, indented or followed by extra fields, and accepts a file
 * with no profiles at all. {@code DungeonProfileGrammar} demands one or more profiles, each a
 * {@code name:} and at least one directive.
 *
 * <p><b>Errors:</b> as in C, where every {@code PARSE_ERROR_*} result stops the load, the first
 * error stops it here and nothing is returned. A syntax error is thrown from {@link #extract} as a
 * {@code ParseCancellationException}, and a bad number or an unknown profile or room name is
 * thrown by the assembler as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}.
 * Neither is caught here.
 *
 * <p>Class DungeonProfileReader coded before 260915, commented in full on 261019, updated on
 * 261105 for the removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class DungeonProfileReader implements Reader<CaveProfile> {
    /**
     * Logger handed to {@link GrammarDriver#run}, explicitly scoped to
     * {@code DungeonProfileReader.class} rather than inferred, so an I/O failure opening the data
     * file is reported under this reader's name. C has no counterpart: it reports a missing file
     * through {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger(DungeonProfileReader.class);

    /**
     * Parses {@code filename} and returns just the assembled cave profiles, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives.
     *
     * <p>Any error throws exactly as it does there, so there is no partial result, as in C. The C
     * counterpart is the {@code profile_parser} run as a whole ({@code generate.c}).
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return the cave profiles read from the file, in file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<CaveProfile> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform for itself: run the
     * parser's {@code file} rule and hand back the parse records.
     *
     * <p>Ordering is deliberate. {@link ParseErrors#throwIfAny()} fires straight after the parse,
     * so a file with a hard syntax error is abandoned with a {@code ParseCancellationException}
     * before any record is used. The records come back in file order, and the grammar's own
     * {@code ArrayList} is copied so the caller owns the result. {@link DungeonProfileAssembler}
     * then converts them, and is where the narrowing from text to {@code int} happens.
     *
     * <p>Function extract coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the parser, positioned at the start of the file
     * @param errorCatcher collects hard lexer/parser errors; fails the parse if any were seen
     * @return the parsed profile records, in file order
     */
    private static List<DungeonProfileParseRecord> extract(
            @NotNull DungeonProfileGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        DungeonProfileGrammar.FileContext output = parser.file();
        List<DungeonProfileParseRecord> records = output.profiles;
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Parses {@code filename} and returns the assembled profiles, in file order, wrapped in a
     * {@link ParseResult}.
     *
     * <p>A missing or unreadable file is an {@link IOException} (C: {@code parse_file_quit_not_found}
     * quits). A hard grammar or lexer error throws a {@code ParseCancellationException}. A number
     * that will not convert, or an unknown profile or room name, throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, where C stops on a
     * {@code PARSE_ERROR_*}. No result is returned for a file that fails.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every cave profile in the file, in file order
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<CaveProfile> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                DungeonProfileLexer::new,
                DungeonProfileGrammar::new,
                DungeonProfileReader::extract,
                new DungeonProfileAssembler(), logger);
    }
}
