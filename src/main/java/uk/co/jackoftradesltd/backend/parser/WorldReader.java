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
 * Loads the data-file lib/gamedata/world.txt entries into the corresponding
 * data objects by driving the matching ANTLR-generated lexer/parser. The
 * thin hand-written bridge between the generated grammar code and the game,
 * implementing the shared {@link Reader} contract (Java port of the
 * equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class WorldReader implements Reader<World> {
    /**
     * Logger used to report file-loading failures.
     *
     * <p>Field logger commented in full on 260915.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @NotNull
    @Override
    public List<World> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule, then validates the parsed levels before building any {@link WorldParseRecord}.
     *
     * <p>Unlike the other readers in this package, both checks here are <em>hard</em> rather than
     * soft: a level whose number does not parse as an integer, or a mismatch between the declared
     * {@code record-count:} header and the number of levels actually parsed, is appended to
     * {@code errorCatcher} rather than {@code errors}, and {@link ParseErrors#throwIfAny()} is called
     * again after both checks so either failure aborts the parse. {@code world.txt} describes the
     * whole cave-system map, so a broken or short map is treated the same as a grammar error - the
     * file is rejected outright rather than loaded with gaps.
     *
     * <p>Function extract coded before 260915, commented in full on 260915.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; both the per-level number check and the
     *                     record-count check report into it, and {@code throwIfAny} aborts if either
     *                     found a problem
     * @param errors       unused; this extractor reports every problem as a hard error instead
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

        // world.txt is the whole cave-system map: a bad level number OR a record-count
        // mismatch means missing/degraded map data, so both are hard errors on the same
        // channel as a grammar error - the whole file refuses to load rather than load partial.
        for (List<String> result : results) {
            int line = Integer.parseInt(result.getLast());
            try {
                Integer.parseInt(result.get(0));
            } catch (NumberFormatException e) {
                errorCatcher.add(line, "Invalid level number: " + result.get(0));
            }
        }
        int declared = Integer.parseInt(declaredRecordCount);
        if (declared != results.size()) {
            errorCatcher.add("record-count header declares " + declared
                    + " levels, but file contains " + results.size());
        }
        errorCatcher.throwIfAny();

        for (List<String> result : results) {
            records.add(new WorldParseRecord(result.get(0),
                    result.get(1), result.get(2), result.get(3),
                    result.getLast()));
        }
        return records;
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link World} levels together with any soft errors collected during assembly. Note that
     * {@link #extract} itself never contributes to that soft-error list - a bad level number or a
     * record-count mismatch is a hard error there, so this result's items are either complete or
     * empty, never partial. {@link #parse} is the items-only convenience over this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 260915 (the
     * {@code @return} previously named {@link WorldParseRecord}, the raw per-grammar record type,
     * rather than {@link World}, the type this method actually returns).
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