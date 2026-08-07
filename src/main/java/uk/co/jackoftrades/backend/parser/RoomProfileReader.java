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

package uk.co.jackoftrades.backend.parser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.roomprofiler.RoomProfileGrammar;
import uk.co.jackoftrades.backend.parser.grammars.roomprofiler.RoomProfileLexer;
import uk.co.jackoftrades.backend.parser.roomprofile.RoomProfileAssembler;
import uk.co.jackoftrades.backend.parser.roomprofile.RoomProfileParseRecord;
import uk.co.jackoftrades.middle.cave.profilers.room.RoomTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code room_template.txt} into a list of {@link RoomTemplate}s. The four
 * {@code room_template}-specific pieces — {@link RoomProfileLexer}, {@link RoomProfileGrammar},
 * {@link #extract} and {@link RoomProfileAssembler} — are handed to {@link GrammarDriver}, which
 * owns the shared lex/parse/assemble ritual every {@code lib/gamedata} reader follows.
 *
 * @author Rowan Crowther
 */
public class RoomProfileReader implements Reader<RoomTemplate> {
    private static final Logger logger = LogManager.getLogger(RoomProfileReader.class);

    /**
     * @param filename the data file to parse
     * @return the successfully assembled templates; soft errors are logged but not surfaced here
     * @author Rowan Crowther
     */
    @Override
    public @NotNull List<RoomTemplate> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * As {@link #parse}, but keeps the soft errors alongside the assembled templates rather than
     * discarding them.
     *
     * @param filename the data file to parse
     * @return the assembled templates plus any soft errors gathered along the way
     * @author Rowan Crowther
     */
    public ParseResult<RoomTemplate> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                RoomProfileLexer::new,
                RoomProfileGrammar::new,
                RoomProfileReader::extract,
                new RoomProfileAssembler(), logger);
    }

    /**
     * Runs the grammar's entry rule, fails closed on hard parse errors, checks the file's declared
     * {@code record-count:} against how many records actually parsed (a soft error on mismatch —
     * note C's own parser never validates this header at all, so a mismatch here is stricter than
     * the original), and hands back the raw records for {@link RoomProfileAssembler} to type-check.
     *
     * @param parser       the constructed parser, ready to run its entry rule
     * @param errorCatcher the installed error listener; {@link ParseErrors#throwIfAny()} must run
     *                     before the records are read out, so a lexer/parser failure aborts here
     *                     rather than handing back a partially-built list
     * @param errors       soft-error sink for the record-count check
     * @return the raw parse records, in file order
     * @author Rowan Crowther
     */
    private static List<RoomProfileParseRecord> extract(
            @NotNull RoomProfileGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        RoomProfileGrammar.FileContext output = parser.file();
        List<RoomProfileParseRecord> result = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);
    }
}
