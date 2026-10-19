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
 * with no profiles at all. {@code DungeonProfileGrammar} demands a {@code record-count:} header,
 * which the shipped C file does not carry and which {@link GrammarDriver#checkRecordCount}
 * checks, and then one or more profiles, each a {@code name:} and at least one directive. A bad
 * number costs its own profile (or room) in Java where it can be caught in the assembler, where
 * every one of C's {@code PARSE_ERROR_*} results stops the load.
 *
 * <p>Class DungeonProfileReader coded before 260915, commented in full on 261019.
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
     * Parses {@code filename} and returns just the assembled cave profiles, discarding the
     * soft-error channel.
     *
     * <p>This is {@link #parseWithResults} with its errors thrown away, so a caller that needs to
     * know a profile or room was dropped (an overflowing number, a record-count mismatch) must use
     * that method instead. A hard grammar error gives an empty list here, indistinguishable from a
     * file with no profiles. The C counterpart is the {@code profile_parser} run as a whole
     * ({@code generate.c}); C has no partial result.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
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
     * before the record count is checked, so a file with a hard syntax error is abandoned rather
     * than being reported as having the wrong number of records. {@link GrammarDriver#checkRecordCount}
     * then compares the file's {@code record-count:} header with the number of profiles parsed and
     * records a soft error on a mismatch; C's own parser never reads this header, so that check is
     * stricter than the original. The records come back in file order, and the grammar's own
     * {@code ArrayList} is copied so the caller owns the result. {@link DungeonProfileAssembler}
     * then converts them, and is where the narrowing from text to {@code int} happens.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
     *
     * @param parser       the parser, positioned at the start of the file
     * @param errorCatcher collects hard lexer/parser errors; fails the parse if any were seen
     * @param errors       the soft-error sink, appended to on a record-count mismatch
     * @return the parsed profile records, in file order
     */
    private static List<DungeonProfileParseRecord> extract(
            @NotNull DungeonProfileGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        DungeonProfileGrammar.FileContext output = parser.file();
        List<DungeonProfileParseRecord> records = output.profiles;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * As {@link #parse}, but keeps the soft errors alongside the assembled profiles rather than
     * discarding them. The preferred entry point when the caller wants to gate on
     * {@link ParseResult#hasErrors()}.
     *
     * <p>A missing or unreadable file is an {@link IOException} (C: {@code parse_file_quit_not_found}
     * quits). A hard grammar or lexer error gives an empty list with the messages as errors. A number
     * that will not convert, where C stops on a {@code PARSE_ERROR_*}, is a soft error that drops
     * that one profile (or, on a {@code room:} line, that one room) and leaves the rest. An unknown
     * profile or room name is not an error here at all; see {@code docs/Assembler_Issues.md}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to read
     * @return the assembled profiles and any soft errors
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
