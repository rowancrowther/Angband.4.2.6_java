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
 * through the shared {@link GrammarDriver}. This is the reader {@code ObjectDataLoader.loadChestTraps()}
 * drives.
 *
 * <p><b>What it stands in for.</b> The whole of C's {@code chest_trap_parser} in {@code obj-chest.c}:
 * {@code init_parse_chest_trap} is the grammar, {@code run_parse_chest_trap} is
 * {@link GrammarDriver#run}, and {@code finish_parse_chest_trap} is nothing at all - it only destroys
 * the parser. The ten {@code parse_chest_trap_*} handlers are split between the grammar (which
 * directive is legal where) and {@link ChestTrapAssembler} (what each value means). C's pval doubling
 * in {@code parse_chest_trap_name} is not done here either; {@code ChestTrapCode} derives each bit
 * from its position.
 *
 * <p><b>Load order.</b> The {@code SUMMON} trap's effect resolves a summon name, so
 * {@code summon.txt} must already be loaded when this runs - as in C's {@code init_arrays}, where
 * {@code summon_parser} comes before {@code chest_trap_parser} - and summons in turn need monster
 * bases, which need pains. Running this first instead throws {@code IllegalStateException} out of
 * {@code MonsterRegistry.lookupSummon} rather than reporting a soft error.
 *
 * <p><b>Where it differs from C.</b> The file needs a {@code record-count:} header that the upstream
 * file does not carry, and a record needs {@code code:} straight after {@code name:}; both are
 * grammar choices, written up in {@code docs/grammar_issues.md}. The assembler rejects the whole file
 * if its structural rules fail, which C cannot do (see {@code docs/Assembler_Issues.md}).
 *
 * <p>Class commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class ChestTrapReader implements Reader<ChestTrap> {
    /**
     * Logger handed to {@link GrammarDriver#run}, explicitly scoped to {@code ChestTrapReader.class}
     * rather than inferred, so an I/O failure opening the data file is reported under this reader's
     * name. C has no counterpart: {@code run_parse_chest_trap} calls {@code parse_file_quit_not_found},
     * which quits on a missing file, where this reader logs and throws an {@code IOException}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger(ChestTrapReader.class);

    /**
     * {@link GrammarDriver} extraction hook: runs the {@code file} rule, surfaces any hard parse
     * errors via {@code errorCatcher}, and soft-validates the declared {@code record-count:} header
     * against the number of records actually parsed.
     *
     * <p>The count is this port's own addition - C's chest trap parser registers no
     * {@code record-count} directive - so the check exists to catch a hand-edited file, not to
     * mirror the original. A wrong count is a soft error and the records still come back; a
     * non-numeric count is a hard error and stops the parse. The returned list is a copy, so the
     * assembler cannot disturb the parse tree.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
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
     * <p>A file broken enough to be rejected whole comes back as an empty list with no hint why;
     * callers that need the reason use {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
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
     * <p>A missing file is an {@code IOException}, where C quits. Hard grammar or lexer failures come
     * back as errors with no traps, not as an exception. This is the port of
     * {@code run_parse_chest_trap}; C's {@code finish_parse_chest_trap} has nothing to port.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
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
