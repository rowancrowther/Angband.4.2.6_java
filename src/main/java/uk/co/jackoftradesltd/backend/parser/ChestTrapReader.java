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
 * {@code MonsterRegistry.lookupSummon}.
 *
 * <p><b>Errors.</b> As in C, the first error stops the load and nothing is returned: a syntax error
 * is thrown from {@link #extract} as a {@code ParseCancellationException}, and a record or file the
 * assembler refuses is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here.
 *
 * <p><b>Where it differs from C.</b> A record needs {@code code:} straight after {@code name:}, a
 * grammar choice written up in {@code docs/grammar_issues.md}. The assembler also checks the
 * structural rules {@code chest_trap.txt} states in prose and ends the load if one fails, which C
 * cannot do (see {@code docs/Assembler_Issues.md}).
 *
 * <p>Class commented in full on 261019, updated on 261105 for the removal of the soft-error path
 * and the {@code record-count:} header.
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
     * {@link GrammarDriver} extraction hook: runs the {@code file} rule and surfaces any hard parse
     * errors via {@code errorCatcher}, whose {@code throwIfAny} throws a
     * {@code ParseCancellationException} before any record is used.
     *
     * <p>The returned list is a copy, so the assembler cannot disturb the parse tree.
     *
     * <p>Function extract coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the grammar positioned at the start of the file
     * @param errorCatcher collects hard lexer/parser errors; {@code throwIfAny} aborts on failure
     * @return the raw parsed chest trap records for the assembler
     */
    private static List<ChestTrapParseRecord> extract(
            @NotNull ChestTrapGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        ChestTrapGrammar.FileContext output = parser.file();
        List<ChestTrapParseRecord> records = output.chestTraps;
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Parses {@code filename} and returns just the assembled chest traps, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it
     * does there.
     *
     * <p>Function parse coded before 260915, commented in full on 261019, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every chest trap in the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ChestTrap> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse {@code filename}, returning the assembled {@link ChestTrap}s wrapped in a
     * {@link ParseResult}.
     *
     * <p>Every error is fatal, as in C. A grammar or lexer failure throws a
     * {@code ParseCancellationException}. {@link ChestTrapAssembler} checks each record and also
     * the structural rules {@code chest_trap.txt} states in prose, and throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException} on the first that fails.
     * A missing file is an {@code IOException}, where C quits. This is the port of
     * {@code run_parse_chest_trap}; C's {@code finish_parse_chest_trap} has nothing to port.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename path to the chest trap data file
     * @return every chest trap in the file
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
