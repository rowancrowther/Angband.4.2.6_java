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
import uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapAssembler;
import uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.chesttrap.ChestTrapGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.chesttrap.ChestTrapLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for {@code lib/gamedata/chest_trap.txt}, producing one {@link ChestTrap} per record (the
 * "locked" no-trap entry, gas traps, poison needles, summoning runes, the explosion device). Wires
 * the generated {@code ChestTrapLexer}/{@code ChestTrapGrammar} to {@link ChestTrapAssembler}
 * through the shared {@link GrammarDriver}, mirroring the C {@code chest_trap_parser} in
 * {@code src/obj-chest.c}. This is the reader {@code ObjectDataLoader.loadChestTraps()} drives.
 *
 * <p><b>Load order.</b> The {@code SUMMON} trap's effect resolves a summon name, so
 * {@code summon.txt} must already be loaded when this runs - and summons in turn need monster bases,
 * which need pains. Running this first instead throws {@code IllegalStateException} out of the
 * registry rather than reporting a soft error.
 *
 * @author Rowan Crowther
 */
public class ChestTrapReader implements Reader<ChestTrap> {
    /**
     * Logger handed to {@link GrammarDriver#run}, explicitly scoped to {@code ChestTrapReader.class}
     * rather than inferred, so an I/O failure opening the data file is reported under this reader's
     * name.
     *
     * <p>Field logger commented in full on 260915.
     */
    private static final Logger logger = LogManager.getLogger(ChestTrapReader.class);

    /**
     * {@link GrammarDriver} extraction hook: runs the {@code file} rule, surfaces any hard parse
     * errors via {@code errorCatcher}, and soft-validates the declared {@code record-count:} header
     * against the number of records actually parsed.
     *
     * <p>The count is this port's own addition - C's chest trap parser registers no
     * {@code record-count} directive - so the check exists to catch a hand-edited file, not to
     * mirror the original.
     *
     * <p>Function extract commented in full before 260915, provenance stamp added on 260915.
     *
     * @param parser       the grammar positioned at the start of the file
     * @param errorCatcher collects hard lexer/parser errors; {@code throwIfAny} aborts on failure
     * @param errors       the soft-error channel (e.g. a record-count mismatch)
     * @return the raw parsed chest trap records for the assembler
     */
    private static List<ChestTrapParseRecord> extract(
            @NotNull ChestTrapGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ChestTrapGrammar.FileContext output = parser.file();
        List<ChestTrapParseRecord> records = output.chestTraps;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parses {@code filename} and returns just the assembled chest traps, discarding the
     * soft-error channel (use {@link #parseWithResults} to inspect errors).
     *
     * <p>Function parse coded before 260915, commented in full on 260915.
     *
     * @param filename the data file to read
     * @return the chest traps read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ChestTrap> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse {@code filename}, returning both the assembled {@link ChestTrap}s and the error channels
     * (soft assembly errors plus any hard grammar/lexer failure), so callers can gate on
     * {@link ParseResult#hasErrors()}.
     *
     * <p>Note that this file is validated as a whole, not record by record: {@link ChestTrapAssembler}
     * returns an <em>empty</em> list if the file breaks any of the structural rules
     * {@code chest_trap.txt} states in prose, so an empty result with errors reported means the file
     * was rejected outright rather than that it held no traps.
     *
     * <p>Function parseWithResults commented in full before 260915, provenance stamp added on 260915.
     *
     * @param filename path to the chest trap data file
     * @return the parse result: the chest traps and any errors collected
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<ChestTrap> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ChestTrapLexer::new,
                ChestTrapGrammar::new,
                ChestTrapReader::extract,
                new ChestTrapAssembler(), logger);
    }
}
