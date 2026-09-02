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
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code lib/gamedata/dungeon_profile.txt} into {@link CaveProfile}s.
 *
 * <p>A thin binding: everything invariant lives in {@link GrammarDriver}, and this supplies the
 * four things that vary — the lexer, the parser, the {@link #extract} step, and the assembler.
 * The port of {@code run_parse_profile} (generate.c:219) plus the record-marshalling half of
 * {@code finish_parse_profile} (generate.c:223).
 *
 * @author Rowan Crowther
 */
public class DungeonProfileReader implements Reader<CaveProfile> {
    private static final Logger logger = LogManager.getLogger(DungeonProfileReader.class);

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<CaveProfile> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return the profiles together with any soft errors gathered on the way.
     *
     * <p>Prefer this over {@link #parse} where the caller wants to report data problems:
     * {@code parse} keeps only the items and drops the messages.
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

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform for itself: run the
     * parser's entry rule and hand back the parse records.
     *
     * <p>The ordering matters and is the reason this lives here rather than in the driver.
     * {@link ParseErrors#throwIfAny()} fires after the parse but before the record count is
     * checked, so a file with hard syntax errors is abandoned rather than being reported as having
     * the wrong number of records.
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
}
