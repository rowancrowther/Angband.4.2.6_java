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
import uk.co.jackoftradesltd.backend.parser.grammars.world.WorldGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.world.WorldLexer;
import uk.co.jackoftradesltd.backend.parser.world.WorldAssembler;
import uk.co.jackoftradesltd.backend.parser.world.WorldParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the data-file lib/gamedata/world.txt entries into {@link World} records by driving
 * {@code WorldLexer} and {@code WorldGrammar}. The thin hand-written bridge between the generated
 * grammar code and the game, implementing the shared {@link Reader} contract.
 *
 * <p>Stands in for the {@code world_parser} in {@code init.c}: {@code parse_world_level} (one
 * {@code level:} record per line, filled by {@code WorldAssembler}) and {@code run_parse_world}
 * (read the file, quitting if it is missing). {@code finish_parse_world}, which checks that every
 * named neighbour exists, is not done here; it is {@code WorldDataLoader.loadWorld}'s job.
 *
 * <p>Differences from C worth knowing:
 * <ul>
 *   <li>The file must start with a {@code record-count:} header and hold at least one level. Upstream
 *   {@code world.txt} has no header, and C loads an empty file as an empty world. See
 *   {@code docs/grammar_issues.md}.</li>
 *   <li>A mismatch between the header and the number of levels is a soft error, as in the other
 *   readers: the levels still load and the mismatch comes back with them.</li>
 *   <li>A level number outside the range of {@code int} is a hard error. C's {@code strtol} clamps it
 *   to a {@code long} and truncates it to {@code int} without complaint.</li>
 *   <li>The {@code "None"} that marks a missing neighbour is passed through as text, not turned into
 *   {@code NULL}; see {@code WorldAssembler}.</li>
 * </ul>
 *
 * <p>Class WorldReader coded before 260915, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class WorldReader implements Reader<World> {
    /**
     * Logger used to report file-loading failures. Handed to {@link GrammarDriver}, which logs an
     * unreadable file here before rethrowing the {@link IOException}. C has no counterpart: it reports
     * through {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled worlds, discarding the
     * soft-error channel (use {@link #parseWithResults} to inspect errors, including a
     * {@code record-count:} mismatch, which this method cannot show you).
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to read
     * @return the worlds read from the file
     * @throws IOException if the file cannot be read
     */
    @NotNull
    @Override
    public List<World> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule, checks each level number, checks the {@code record-count:} header, and builds one
     * {@link WorldParseRecord} per level.
     *
     * <p>Two kinds of problem, as in the other readers:
     * <ul>
     *   <li><b>Hard</b>, reported to {@code errorCatcher} and aborted by
     *   {@link ParseErrors#throwIfAny()}: any grammar error, and a level number that does not fit an
     *   {@code int}. The lexer's {@code INTEGER} token lets any run of digits through, so only the
     *   overflow can fail here. C reads the same field with {@code strtol} and never rejects it.
     *   Nothing loads.</li>
     *   <li><b>Soft</b>, reported to {@code errors} by {@link GrammarDriver#checkRecordCount}: a
     *   header that is not a number that fits an {@code int}, and a header that differs from the number
     *   of levels read. The levels still load. C has no header, so has no counterpart.</li>
     * </ul>
     *
     * <p>The level-number check runs, and aborts, before the header check, so a file with both
     * problems reports only the level number.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; the per-level number check reports into it and
     *                     {@code throwIfAny} aborts if it found a problem
     * @param errors       the soft-error channel; the record-count check reports into it
     * @return the parsed world-level records, in file order
     */
    private static List<WorldParseRecord> extract(
            @NotNull WorldGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        List<WorldParseRecord> records = new ArrayList<>();
        WorldGrammar.FileContext output = parser.file();
        List<List<String>> results = output.levels;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;

        // A level number that overflows int is a hard error, like a grammar error. The record-count
        // header is only checked softly below, as in every other reader.
        for (List<String> result : results) {
            int line = Integer.parseInt(result.getLast());
            try {
                Integer.parseInt(result.getFirst());
            } catch (NumberFormatException e) {
                errorCatcher.add(line, "Invalid level number: " + result.getFirst());
            }
        }
        errorCatcher.throwIfAny();
        
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);
        
        for (List<String> result : results) {
            records.add(new WorldParseRecord(result.get(0),
                    result.get(1), result.get(2), result.get(3),
                    result.getLast()));
        }
        return records;
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link World} levels together with any soft errors. The only soft errors are the two
     * record-count ones from {@link #extract}, since {@code WorldAssembler} raises none; the levels
     * are still returned beside them. A grammar error or an overflowing level number is hard and
     * gives no levels. A file that cannot be read throws, where C quits. {@link #parse} is the
     * items-only convenience over this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} of type {@link World}
     * @throws IOException when there is a problem finding or reading
     *                     the file
     */
    public ParseResult<World> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                WorldLexer::new,
                WorldGrammar::new,
                WorldReader::extract,
                new WorldAssembler(), logger);
    }
}