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
import uk.co.jackoftrades.backend.parser.blowmethod.BlowMethodAssembler;
import uk.co.jackoftrades.backend.parser.blowmethod.BlowMethodParseRecord;
import uk.co.jackoftrades.backend.parser.grammars.blowmethod.BlowMethodGrammar;
import uk.co.jackoftrades.backend.parser.grammars.blowmethod.BlowMethodLexer;
import uk.co.jackoftrades.middle.combat.BlowMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link BlowMethod} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class BlowMethodReader implements Reader<BlowMethod> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<BlowMethod> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return the assembled methods <em>together with</em> any soft
     * errors gathered on the way, rather than discarding them as {@link #parse} does.
     * <p>
     * This is the variant callers should prefer: a blow method whose {@code msg:} does
     * not resolve is skipped rather than fatal, so the returned list can be short
     * without anything having been thrown. {@code GameConstants.loadBlowMethods} gates
     * on {@link ParseResult#hasErrors()} for exactly that reason.
     *
     * @param filename the data file to parse
     * @return the assembled blow methods plus any soft errors
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public ParseResult<BlowMethod> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                BlowMethodLexer::new,
                BlowMethodGrammar::new,
                BlowMethodReader::extract,
                new BlowMethodAssembler(), logger);
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the
     * {@code file} entry rule and pull the parse records off its context.
     * <p>
     * The ordering here is load-bearing. {@link ParseErrors#throwIfAny()} fires
     * immediately after the parse and <em>before</em> the record count is checked, so a
     * hard lexer/parser failure aborts via {@code ParseCancellationException} rather
     * than letting a half-built record list be measured against the declared count and
     * produce a second, misleading error. The count check that follows is soft by
     * contrast - it only appends to {@code errors}.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the shared hard-error listener installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed records, in file order
     * @author Rowan Crowther
     */
    private static List<BlowMethodParseRecord> extract(
            @NotNull BlowMethodGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        BlowMethodGrammar.FileContext output = parser.file();
        List<BlowMethodParseRecord> results = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }
}
