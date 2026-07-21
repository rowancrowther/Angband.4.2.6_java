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
import uk.co.jackoftrades.backend.parser.grammars.pit.PitGrammar;
import uk.co.jackoftrades.backend.parser.grammars.pit.PitLexer;
import uk.co.jackoftrades.backend.parser.pit.PitAssembler;
import uk.co.jackoftrades.backend.parser.pit.PitParseRecord;
import uk.co.jackoftrades.middle.cave.PitProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link PitProfile} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class PitReader implements Reader<PitProfile> {
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
    public @NotNull List<PitProfile> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parse the file and return the assembled profiles together with any soft errors gathered along
     * the way. All the shared plumbing lives in {@link GrammarDriver#run}; this method just supplies
     * the four pit-specific knobs - the generated lexer/parser constructors, the {@link #extract}
     * step, and the {@link PitAssembler} - so the reader itself stays a thin wiring layer.
     *
     * @param filename the data file to parse
     * @return the parsed profiles plus any soft errors (empty items if the parse was cancelled)
     * @throws IOException if the file cannot be read
     * @author Rowan Crowther
     */
    public @NotNull ParseResult<PitProfile> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PitLexer::new,
                PitGrammar::new,
                PitReader::extract,
                new PitAssembler(), logger);
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot do itself: run the entry rule and
     * pull the parse-records out of the pit-shaped {@code FileContext}.
     *
     * <p>Order matters. {@link ParseErrors#throwIfAny()} fires immediately after the parse and
     * before the record-count check, so hard grammar/lexer errors fail closed (an empty result)
     * rather than letting a half-parsed file be validated. The record-count check is soft: a
     * mismatched {@code record-count:} header is reported into {@code errors} but does not stop the
     * records that did parse from loading.
     *
     * @param parser       the generated parser, already wired to the token stream
     * @param errorCatcher the shared error listener, checked here for hard errors
     * @param errors       the soft-error sink (record-count mismatches are appended here)
     * @return the parsed pit records
     * @author Rowan Crowther
     */
    private static List<PitParseRecord> extract(@NotNull PitGrammar parser,
                                                @NotNull ParseErrors errorCatcher,
                                                @NotNull List<String> errors) {
        PitGrammar.FileContext output = parser.file();
        List<PitParseRecord> pits = output.pits;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, pits.size(), errors);

        return new ArrayList<>(pits);
    }
}
