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
import uk.co.jackoftradesltd.channel.parser.*;
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
 *   <li>The file must hold at least one level, where C loads an empty file as an empty world. See
 *   {@code docs/grammar_issues.md}.</li>
 *   <li>A level number outside the range of {@code int} ends the load with a
 *   {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. C's {@code strtol} clamps
 *   it to a {@code long} and truncates it to {@code int} without complaint.</li>
 *   <li>The {@code "None"} that marks a missing neighbour is passed through as text, not turned into
 *   {@code NULL}; see {@code WorldAssembler}.</li>
 * </ul>
 *
 * <p>Every error is fatal, as in C: a grammar error is thrown from {@link #extract} as a
 * {@code ParseCancellationException}, and the level-number check above throws as described. Neither
 * is caught here, and no levels are returned.
 *
 * <p>Class WorldReader coded before 260915, commented in full on 261019, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class WorldReader implements Reader<World> {
    private static String filename;    
    /**
     * Logger used to report file-loading failures. Handed to {@link GrammarDriver}, which logs an
     * unreadable file here before rethrowing the {@link IOException}. C has no counterpart: it reports
     * through {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled worlds, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it does
     * there.
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every world level in the file, in file order
     * @throws IOException if the file cannot be read
     */
    @NotNull
    @Override
    public List<World> parse(@NotNull String filename) throws IOException {
        WorldReader.filename = filename;
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule, checks each level number, and builds one {@link WorldParseRecord} per level.
     *
     * <p>Two checks, both fatal, and nothing loads after either:
     * <ul>
     *   <li>any grammar error, collected by {@code errorCatcher} and thrown as a
     *   {@code ParseCancellationException} by {@link ParseErrors#throwIfAny()} straight after the
     *   parse;</li>
     *   <li>a level number that does not fit an {@code int}, thrown directly as a
     *   {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. The lexer's
     *   {@code INTEGER} token lets any run of digits through, so only the overflow can fail here. C
     *   reads the same field with {@code strtol} and never rejects it.</li>
     * </ul>
     *
     * <p>Function extract coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts on a grammar error
     * @return the parsed world-level records, in file order
     */
    private static List<WorldParseRecord> extract(
            @NotNull WorldGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        List<WorldParseRecord> records = new ArrayList<>();
        WorldGrammar.FileContext output = parser.file();
        List<List<String>> results = output.levels;
        errorCatcher.throwIfAny();

        // A level number that overflows int is a fatal error, like a grammar error.
        for (List<String> result : results) {
            int line = Integer.parseInt(result.getLast());
            try {
                Integer.parseInt(result.getFirst());
            } catch (NumberFormatException e) {
                String message = "World reader starting at line: " + line + " of file: " + filename + " has " +
                        "an illegal level value: " + result.getFirst();
                logger.fatal(message, e);
                throw new DataFileParseException(message);
            }
        }
        
        for (List<String> result : results) {
            records.add(new WorldParseRecord(result.get(0),
                    result.get(1), result.get(2), result.get(3),
                    result.getLast()));
        }
        return records;
    }

    /**
     * Parses {@code filename} and returns the assembled {@link World} levels wrapped in a
     * {@link ParseResult}. {@code WorldAssembler} refuses nothing, so the only errors are the two
     * from {@link #extract}: a grammar error and an overflowing level number, each of which throws
     * and gives no result. A file that cannot be read throws an {@link IOException}, where C quits.
     * {@link #parse} is the items-only convenience over this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} of type {@link World}
     * @throws IOException when there is a problem finding or reading
     *                     the file
     */
    public ParseResult<World> parseWithResults(@NotNull String filename) throws IOException {
        WorldReader.filename = filename;
        return GrammarDriver.run(filename,
                WorldLexer::new,
                WorldGrammar::new,
                WorldReader::extract,
                new WorldAssembler(), logger);
    }
}