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
import uk.co.jackoftradesltd.backend.parser.grammars.realm.RealmGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.realm.RealmLexer;
import uk.co.jackoftradesltd.backend.parser.realm.RealmAssembler;
import uk.co.jackoftradesltd.backend.parser.realm.RealmParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code realm.txt} into {@link MagicRealm}s by driving {@code RealmLexer} and
 * {@code RealmGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code realm_parser} in the C original's {@code init.c}: {@code init_parse_realm}
 * (the five directives {@code name}, {@code stat}, {@code verb}, {@code spell-noun} and
 * {@code book-noun}, which the grammar's {@code realm} rule takes), {@code run_parse_realm} (a
 * single call to {@code parse_file_quit_not_found}) and the list-building half of
 * {@code finish_parse_realm}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_realm_*} handlers, including the
 * {@code stat:} lookup, live in {@link RealmAssembler}. C's global {@code realms} list has no
 * counterpart in this class: the list this reader returns is installed by the loader that calls it.
 * The list comes back in reverse file order, as C's does, because C pushes each new realm onto the
 * head; the assembler does the reversing, so this reader hands the records over in file order.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a realm whose stat does not
 * resolve is thrown by the assembler as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here, and
 * no realms are returned. A file that cannot be opened is an {@link IOException}, where C quits.
 *
 * <p>Class RealmReader coded before 260915, commented in full on 261018, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class RealmReader implements Reader<MagicRealm> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled realms in C's order (the last realm in the
     * file first), unwrapped from the {@link ParseResult} that {@link #parseWithResults} gives. Any
     * error throws exactly as it does there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every magic realm in the file, in C's order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<MagicRealm> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the {@code stat:} text is
     * resolved by the assembler, as it is C's in {@code parse_realm_stat}.
     *
     * <p>The grammar builds each {@link RealmParseRecord} itself, with {@code ""} for a directive
     * the record does not have, so the rows are only copied into a new list. A directive repeated
     * within a record keeps its last value, as C's handlers overwrite. The rule requires at least
     * one realm, where C accepts an empty file.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parsed realm records, one per record, in file order
     */
    private static @NotNull List<RealmParseRecord> extract(
            @NotNull RealmGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        RealmGrammar.FileContext output = parser.file();
        List<RealmParseRecord> result = output.realms;
        errorCatcher.throwIfAny();

        return new ArrayList<>(result);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link MagicRealm}s, in C's order, wrapped
     * in a {@link ParseResult}. This is the Java form of C's {@code run_parse_realm} followed by
     * {@code finish_parse_realm}.
     *
     * <p>A clean file gives four realms for the shipped data. A lexer or grammar error throws a
     * {@code ParseCancellationException}. A realm whose {@code stat:} does not resolve throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, as C raises a parse
     * error and quits. A file that cannot be opened is an {@link IOException}. No result is
     * returned for a file that fails.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the realm data file to read
     * @return every magic realm in the file, in C's order
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<MagicRealm> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                RealmLexer::new,
                RealmGrammar::new,
                RealmReader::extract,
                new RealmAssembler(), logger);
    }
}
