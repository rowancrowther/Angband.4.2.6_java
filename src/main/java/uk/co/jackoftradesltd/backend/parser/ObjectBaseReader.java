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
import uk.co.jackoftradesltd.backend.parser.grammars.objectbase.ObjectBaseGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.objectbase.ObjectBaseLexer;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseAssembler;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link ObjectBase} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class ObjectBaseReader implements Reader<ObjectBase> {
    /**
     * Logger used to report file-loading failures.
     *
     * <p>Field logger commented in full on 260915.
     */
    public static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled object bases, discarding the
     * soft-error channel (use {@link #parseWithResults} to inspect errors).
     *
     * <p>Function parse coded before 260915, commented in full on 260915.
     *
     * @param filename the data file to read
     * @return the object bases read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ObjectBase> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the {@code file}
     * rule, fails closed on any hard grammar/lexer error via {@link ParseErrors#throwIfAny()}, then
     * soft-checks the declared {@code record-count:} header against the number of records actually
     * parsed.
     *
     * <p>Function extract coded before 260915, commented in full on 260915.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the records are used
     * @param errors       the soft-error sink, appended to on a record-count mismatch
     * @return a defensive copy of the parsed object-base records for the assembler
     */
    private static List<ObjectBaseParseRecord> extract(
            @NotNull ObjectBaseGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ObjectBaseGrammar.FileContext output = parser.file();
        List<ObjectBaseParseRecord> result = output.objectBaseList;
        errorCatcher.throwIfAny();

        String declaredCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredCount, result.size(), errors);

        return new ArrayList<>(result);
    }

    /**
     * Parses {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link ObjectBase}s together with any soft errors collected during parsing and assembly.
     * {@link #parse} is the items-only convenience over this.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 260915.
     *
     * @param filename the object-base data file to read
     * @return the parse result: the object bases plus any error messages
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<ObjectBase> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ObjectBaseLexer::new,
                ObjectBaseGrammar::new,
                ObjectBaseReader::extract,
                new ObjectBaseAssembler(), logger);
    }
}