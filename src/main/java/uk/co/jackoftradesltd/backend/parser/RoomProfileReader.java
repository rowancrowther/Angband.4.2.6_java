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
import uk.co.jackoftradesltd.backend.parser.grammars.roomprofiler.RoomProfileGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.roomprofiler.RoomProfileLexer;
import uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileAssembler;
import uk.co.jackoftradesltd.backend.parser.roomprofile.RoomProfileParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code room_template.txt} into a list of {@link RoomTemplate}s. The four
 * {@code room_template}-specific pieces — {@link RoomProfileLexer}, {@link RoomProfileGrammar},
 * {@link #extract} and {@link RoomProfileAssembler} — are handed to {@link GrammarDriver}, which
 * owns the shared lex/parse/assemble ritual every {@code lib/gamedata} reader follows.
 *
 * <p>The name is a leftover: this is the room <em>template</em> file, not the room profiles of
 * {@code dungeon_profile.txt}, which are a separate reader.
 *
 * <p><b>C counterpart:</b> {@code room_parser} in {@code generate.c}, run as the second of the
 * three parsers in {@code run_template_parser}. Its nine {@code parse_room_*} handlers,
 * registered by {@code init_parse_room}, are performed by {@link RoomProfileAssembler}, as is the
 * building of the list that {@code finish_parse_room} hands to the global {@code room_templates};
 * {@code run_parse_room} ({@code parse_file_quit_not_found}) is the file open inside
 * {@link GrammarDriver}, where a missing file is an {@link IOException} for the caller to turn
 * into a fatal error. This class itself only wires the grammar to the driver, so it has no C logic
 * of its own to mismatch.
 *
 * <p><b>Order of the result:</b> the list is in reverse file order, because
 * {@link RoomProfileAssembler} prepends each template, as {@code parse_room_name} does by pointing
 * the new record's {@code next} at the previous head. {@link #extract} itself keeps file order.
 *
 * <p><b>Where the Java differs from C</b> (the grammar half is recorded in
 * {@code docs/grammar_issues.md}, the assembler half in {@code docs/Assembler_Issues.md}): C
 * registers nine independent one-line directives and so accepts them in any order, missing,
 * repeated or indented; {@code RoomProfileGrammar} demands a {@code record-count:} header, which
 * the shipped C file does not carry, and which
 * {@link GrammarDriver#checkRecordCount} checks, and then one or more records in the fixed order
 * {@code name:} to {@code tval:}, at most one {@code flags:} line, then the {@code D:} lines. A bad
 * record costs only itself in Java where it can be caught in the assembler, where every one of C's
 * {@code PARSE_ERROR_*} results stops the load.
 *
 * <p>Class RoomProfileReader coded before 260915, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
public class RoomProfileReader implements Reader<RoomTemplate> {
    /**
     * Logger handed to {@link GrammarDriver#run}, explicitly scoped to
     * {@code RoomProfileReader.class} rather than inferred, so an I/O failure opening the data file
     * is reported under this reader's name. C has no counterpart: it reports a missing file through
     * {@code quit()}.
     *
     * <p>Field logger coded before 260915, commented in full on 261019.
     */
    private static final Logger logger = LogManager.getLogger(RoomProfileReader.class);

    /**
     * Parse the file and return just the assembled room templates, discarding the error list.
     *
     * <p>This is {@link #parseWithResults} with its soft errors thrown away, so a caller that needs
     * to know a template was dropped (a size over the {@code room template} builder's cap, a
     * ragged layout, an unknown {@code tval:} or flag, a record-count mismatch) must use that
     * method instead. A hard grammar error gives an empty list here, indistinguishable from a file
     * with no templates. The C counterpart is the {@code room_parser} run as a whole
     * ({@code generate.c}); C has no partial result.
     *
     * <p>Function parse coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to parse
     * @return the successfully assembled templates, in reverse file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<RoomTemplate> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The one grammar-specific step {@link GrammarDriver} cannot perform itself: run the parser's
     * {@code file} rule and pull the raw records out of its result.
     *
     * <p>Ordering is deliberate. {@link ParseErrors#throwIfAny()} fires straight after the parse, so
     * a hard grammar or lexer error fails closed before the soft
     * {@link GrammarDriver#checkRecordCount} check runs against whatever did parse. That check
     * compares the file's {@code record-count:} header with the number of records parsed and is a
     * soft error on mismatch; C's own parser never reads this header, so a mismatch here is
     * stricter than the original. The records come back in file order, and the grammar's own
     * {@code ArrayList} is copied so the caller owns the result. {@link RoomProfileAssembler} then
     * type-checks them, and is where the order becomes C's.
     *
     * <p>Function extract coded before 260915, commented in full on 261019.
     *
     * @param parser       the constructed parser, ready to run its entry rule
     * @param errorCatcher the installed error listener; {@link ParseErrors#throwIfAny()} must run
     *                     before the records are read out, so a lexer/parser failure aborts here
     *                     rather than handing back a partially-built list
     * @param errors       soft-error sink for the record-count check
     * @return the raw parse records, in file order
     */
    private static List<RoomProfileParseRecord> extract(
            @NotNull RoomProfileGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        RoomProfileGrammar.FileContext output = parser.file();
        List<RoomProfileParseRecord> result = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);
    }

    /**
     * As {@link #parse}, but keeps the soft errors alongside the assembled templates rather than
     * discarding them. The preferred entry point when the caller wants to gate on
     * {@link ParseResult#hasErrors()}.
     *
     * <p>A missing or unreadable file is an {@link IOException} (C: {@code parse_file_quit_not_found}
     * quits). A hard grammar or lexer error gives an empty list with the messages as errors. Every
     * problem {@link RoomProfileAssembler} finds, where C would stop on a {@code PARSE_ERROR_*}, is
     * a soft error that drops that one template and leaves the rest.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261019.
     *
     * @param filename the data file to parse
     * @return the assembled templates, in reverse file order, plus any soft errors gathered along
     * the way
     * @throws IOException if the file cannot be read
     */
    public ParseResult<RoomTemplate> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                RoomProfileLexer::new,
                RoomProfileGrammar::new,
                RoomProfileReader::extract,
                new RoomProfileAssembler(), logger);
    }
}
