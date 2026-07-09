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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.parser.grammars.slay.SlayGrammar;
import uk.co.jackoftrades.backend.parser.grammars.slay.SlayLexer;
import uk.co.jackoftrades.backend.parser.slay.SlayAssembler;
import uk.co.jackoftrades.backend.parser.slay.SlayParseRecord;
import uk.co.jackoftrades.middle.objects.Slay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code slay.txt} into {@link Slay} objects, driving the ANTLR-generated
 * {@code SlayLexer}/{@code SlayGrammar} through the shared {@link GrammarDriver}
 * pipeline: lex and parse to {@link SlayParseRecord}s, which {@link SlayAssembler}
 * then resolves into domain slays. The thin hand-written bridge between the
 * generated grammar code and the game, implementing the shared {@link Reader}
 * contract (Java port of C's {@code slay_parser}, {@code src/obj-init.c:851}).
 *
 * @author Rowan Crowther
 */
public class SlayReader implements Reader<Slay> {
    /**
     * Logger used to report file-loading (IO) failures.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the file and return just the assembled slays, discarding the soft-
     * error channel (use {@link #parseWithResults} to inspect errors).
     *
     * @param filename the slay data file to read
     * @return the slays read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Slay> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file into a {@link ParseResult} carrying both the assembled slays
     * and any soft (recoverable) errors, by handing the grammar-specific pieces
     * to the shared {@link GrammarDriver}.
     *
     * @param filename the slay data file to read
     * @return the assembled slays plus any soft errors gathered en route
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<Slay> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                SlayLexer::new,
                SlayGrammar::new,
                SlayReader::extract,
                new SlayAssembler(), logger);
    }

    /**
     * The grammar-specific step for {@link GrammarDriver}: run the parser's
     * {@code file} rule, fail closed on any hard grammar/lexer error via
     * {@link ParseErrors#throwIfAny()}, check the declared {@code record-count}
     * against the number actually parsed (a soft error), and hand back the raw
     * records for the assembler.
     *
     * @param parser       the generated Slay parser
     * @param errorCatcher the shared hard-error channel
     * @param errors       the soft-error sink
     * @return the parsed slay records
     */
    @Contract("_, _, _ -> new")
    private static @NotNull List<SlayParseRecord> extract(
            @NotNull SlayGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        SlayGrammar.FileContext output = parser.file();
        List<SlayParseRecord> results = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }
}
