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
import uk.co.jackoftrades.backend.parser.grammars.summon.SummonGrammar;
import uk.co.jackoftrades.backend.parser.grammars.summon.SummonLexer;
import uk.co.jackoftrades.backend.parser.summon.SummonAssembler;
import uk.co.jackoftrades.backend.parser.summon.SummonParseRecord;
import uk.co.jackoftrades.middle.monsters.Summon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link Summon} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class SummonReader implements Reader<Summon> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse {@code summon.txt} and return just the assembled summons, discarding any soft-error
     * detail. Convenience wrapper over {@link #parseWithResults} for callers (such as
     * {@code GameConstants}) that only want the successfully loaded items.
     *
     * @param filename the data file to read
     * @return the summons read from the file (empty if the parse failed outright)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Summon> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse {@code summon.txt} and return both the assembled summons and any collected errors.
     * Delegates the whole lex -> parse -> extract -> assemble pipeline to the shared
     * {@link GrammarDriver}, supplying the Summon-specific lexer, parser, record extractor and
     * {@link SummonAssembler}.
     *
     * @param filename the data file to read
     * @return the parse result carrying the assembled summons and any soft/hard errors
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<Summon> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                SummonLexer::new,
                SummonGrammar::new,
                SummonReader::extract,
                new SummonAssembler(),
                logger);
    }

    /**
     * Pull the raw parse records out of a parsed {@code summon.txt}. Runs the top-level rule, raises
     * any hard grammar/lexer errors, then validates the declared record count (a soft check) before
     * handing the records to the assembler.
     *
     * @param parser       the Summon parser positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@link ParseErrors#throwIfAny()} aborts the parse
     *                     if the grammar or lexer reported anything
     * @param errors       accumulator for soft errors, here the record-count mismatch
     * @return the raw parse records for the assembler to resolve
     */
    private static List<SummonParseRecord> extract(
            @NotNull SummonGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        SummonGrammar.FileContext output = parser.file();
        List<SummonParseRecord> records = output.summons;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }
}
