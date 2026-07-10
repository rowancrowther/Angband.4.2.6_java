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
import uk.co.jackoftrades.backend.parser.grammars.itemobject.ItemObjectGrammar;
import uk.co.jackoftrades.backend.parser.grammars.itemobject.ItemObjectLexer;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectAssembler;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParseRecord;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the relevant data-file entries into {@link ObjectKind} objects by driving the
 * matching ANTLR-generated lexer/parser. The thin hand-written bridge between
 * the generated grammar code and the game, implementing the shared
 * {@link Reader} contract (Java port of the equivalent C data-file parser).
 *
 * @author Rowan Crowther
 */
public class ItemObjectReader implements Reader<ObjectKind> {
    /**
     * Logger used to report file-loading failures.
     *
     * @author Rowan Crowther
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Run the parser and generate the ArrayList from the file
     *
     * @param filename the name of the file
     * @return an ArrayList of items read from the file
     */
    @Override
    public @NotNull List<ObjectKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * Parses the file and returns the full {@link ParseResult} — both the assembled object kinds
     * and the collected soft/hard error messages — by handing the standard pipeline to
     * {@link GrammarDriver} (lex, parse, extract records, assemble). {@link #parse} is the
     * items-only convenience over this.
     *
     * @param filename the object data file to load
     * @return the assembled object kinds plus any error messages
     * @throws IOException if the file cannot be read
     */
    public ParseResult<ObjectKind> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ItemObjectLexer::new,
                ItemObjectGrammar::new,
                ItemObjectReader::extract,
                new ItemObjectAssembler(), logger);
    }

    /**
     * The grammar-specific extraction step handed to {@link GrammarDriver}: runs the top-level
     * {@code file} rule, surfaces any hard grammar/lexer errors, soft-checks the declared
     * {@code record-count:} header against the number of records read, and returns the raw
     * parse records for the assembler.
     *
     * @param parser       the constructed {@code ItemObjectGrammar} positioned at the token stream
     * @param errorCatcher the hard-error channel; {@link ParseErrors#throwIfAny()} aborts on a
     *                     grammar/lexer error before the records are used
     * @param errors       the soft-error channel; a record-count mismatch is reported here without
     *                     discarding the records
     * @return the raw object parse records in source order
     */
    private static @NotNull List<ItemObjectParseRecord> extract(
            @NotNull ItemObjectGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ItemObjectGrammar.FileContext output = parser.file();
        List<ItemObjectParseRecord> records = output.itemObjects;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }
}
