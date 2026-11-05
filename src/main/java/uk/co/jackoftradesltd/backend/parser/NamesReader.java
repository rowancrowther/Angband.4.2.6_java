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
import uk.co.jackoftradesltd.backend.parser.grammars.names.NamesGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.names.NamesLexer;
import uk.co.jackoftradesltd.backend.parser.names.NamesAssembler;
import uk.co.jackoftradesltd.backend.parser.names.NamesParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.game.Name;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code names.txt} into {@link Name}s by driving {@code NamesLexer} and {@code NamesGrammar},
 * the hand-written bridge between the generated parser and the game. It stands in for
 * {@code names_parser} in the C original's {@code init.c}: {@code init_parse_names} (the two
 * directives {@code section:} and {@code word:}, which the grammar's {@code section} and
 * {@code word} rules take), {@code run_parse_names} (a single call to
 * {@code parse_file_quit_not_found}) and the reading half of {@code finish_parse_names}.
 *
 * <p>The rest of C's work is done elsewhere. {@code parse_names_section} and
 * {@code parse_names_word} are the grammar and {@link NamesAssembler}: the grammar gathers each
 * {@code section:} line and the {@code word:} lines that follow it into a {@link NamesParseRecord},
 * and the assembler turns the section text into the {@code int} of a {@link Name}. The flattening
 * of the words into one list per section, which {@code finish_parse_names} does into
 * {@code name_sections}, is {@code MiscRegistry.setNames}, called by
 * {@code MiscDataLoader.loadNames()}. The range check on the section number, C's
 * {@code PARSE_ERROR_OUT_OF_BOUNDS}, is made by {@link NamesAssembler}, and {@code setNames}
 * checks it again. {@code cleanup_names} has no counterpart, as the garbage collector frees the
 * words.
 *
 * <p>A record here is a whole section block, not a single word. C keeps a running section number
 * and a word list per section, so a second {@code section:1} block adds to the first; this reader
 * returns two {@link Name}s with section 1 and leaves it to {@code setNames} to join them. Each
 * block's words come back in file order where C's list holds them reversed, which nothing can tell
 * apart because {@code build_prob} in {@code randname.c} only counts letter transitions.
 *
 * <p>The text of a word is everything after {@code word:} to the end of the line, as it is in C.
 *
 * <p>C quits on the first bad line, and so does this reader: a lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a section number the
 * assembler refuses (negative, zero or past the last name type) is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here, and
 * no names are returned. A file that cannot be opened is an {@link IOException}, where C quits.
 *
 * <p>Class NamesReader coded before 260915, commented in full on 261018, updated on 261105 for the
 * removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class NamesReader implements Reader<Name> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled names, one per {@code section:} block in
     * file order, unwrapped from the {@link ParseResult} that {@link #parseWithResults} gives. Any
     * error throws exactly as it does there, so an empty list only ever means a file with no
     * sections.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the names data file to read
     * @return every name section in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    @Override
    public @NotNull List<Name> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule and fails closed on any hard lexer or grammar error through
     * {@link ParseErrors#throwIfAny()}. No value is interpreted here: the section text is parsed to
     * an {@code int} by the assembler, and the words are copied through.
     *
     * <p>The ordering is deliberate: {@code throwIfAny} fires straight after the parse, so a hard
     * grammar error (a {@code word:} line before the first {@code section:}, a non-numeric section,
     * a line the lexer does not know) fails closed before any row is used. The grammar builds each
     * {@link NamesParseRecord} itself, so the rows are only copied into a new list. The rule takes
     * zero or more sections, as C accepts a file with none.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parsed name records, one per {@code section:} block, in file order
     */
    private static List<NamesParseRecord> extract(
            @NotNull NamesGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        NamesGrammar.FileContext output = parser.file();
        List<NamesParseRecord> records = output.records;
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Reads {@code filename} and returns the assembled {@link Name}s, in file order, wrapped in a
     * {@link ParseResult}. This is the Java form of C's {@code run_parse_names} followed by the
     * reading half of {@code finish_parse_names}, and the entry point
     * {@code MiscDataLoader.loadNames()} uses.
     *
     * <p>A clean file gives the names; the shipped file gives two, section 1 with 601 words and
     * section 2 with 587. A lexer or grammar error throws a {@code ParseCancellationException}, a
     * section number out of range throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and neither gives a
     * result. A file that cannot be opened is an {@link IOException}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the names data file to read
     * @return every name section in the file, in file order
     * @throws IOException if the file cannot be opened or read
     */
    public ParseResult<Name> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                NamesLexer::new,
                NamesGrammar::new,
                NamesReader::extract,
                new NamesAssembler(), logger);
    }
}
