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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.grammars.projection.ProjectionGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.projection.ProjectionLexer;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionAssembler;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads {@code projection.txt} into {@link Projection}s by driving {@code ProjectionLexer} and
 * {@code ProjectionGrammar}, the hand-written bridge between the generated parser and the game. It
 * stands in for {@code projection_parser} in the C original's {@code obj-init.c}:
 * {@code init_parse_projection} (the fifteen directives, which the grammar's {@code projection}
 * rule takes), {@code run_parse_projection} (a single call to {@code parse_file_quit_not_found}),
 * and the list-building half of {@code finish_parse_projection}.
 *
 * <p>The rest of C's work is done elsewhere. The {@code parse_projection_*} handlers, the
 * 25-element check and the element-order check live in {@link ProjectionAssembler}. C's
 * {@code z_info->projection_max} and global {@code projections} array have no separate Java
 * counterpart: the count is the size of the list this reader returns, and
 * {@code WorldDataLoader.loadProjections} installs that list in {@code WorldRegistry}.
 *
 * <p>The projections come back in file order, which is the order of C's {@code projections[]}.
 * C's array has no blank slot, so the accepted base-0 deviation for blank-slot lists does not
 * arise here. Only the element block is positional: C checks the first 25 records against
 * {@code element_names} and nothing after that, so a list index is the enum ordinal minus one for
 * those 25 ({@code PROJ_NONE} takes ordinal 0) and nothing more.
 *
 * <p>C quits on the first bad line. This reader follows the shared two-channel contract instead: a
 * lexer or grammar error returns no projections and the messages, and a bad record or a
 * {@code record-count:} mismatch is a soft error that leaves the other records loaded. The
 * {@code record-count:} header is Java-only; the upstream file has none. A file that cannot be
 * opened is an {@link IOException}, where C quits.
 *
 * <p>Class ProjectionReader coded before 260915, commented in full on 261018.
 *
 * @author Rowan Crowther
 */
public class ProjectionReader implements Reader<Projection> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself never logs.
     *
     * <p>Field logger coded before 260915, commented in full on 261018.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Reads {@code filename} and returns the assembled projections in file order, discarding the
     * error messages. The convenience form of {@link #parseWithResults}, which is the one to call
     * when the errors matter: with this one a hard grammar error and an empty file look the same.
     *
     * <p>Function parse coded before 260915, commented in full on 261018.
     *
     * @param filename the data file to read
     * @return the projections read from the file, empty if the parse failed
     * @throws IOException if the file cannot be opened or read
     */
    @NotNull
    @Contract("_ -> !null")
    @Override
    public List<Projection> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The grammar-specific step handed to {@link GrammarDriver}: runs the grammar's {@code file}
     * rule, fails closed on any hard lexer or grammar error through {@link ParseErrors#throwIfAny()},
     * soft-checks the {@code record-count:} header against the number of records parsed, and builds
     * one {@link ProjectionParseRecord} per record. No value is interpreted here: that is the
     * assembler's work, as it is C's in the {@code parse_projection_*} handlers.
     *
     * <p>Each row the grammar hands back holds sixteen strings: the fifteen directives in the order
     * {@code code}, {@code name}, {@code type}, {@code desc}, {@code player-desc},
     * {@code blind-desc}, {@code lash-desc}, {@code numerator}, {@code denominator},
     * {@code divisor}, {@code damage-cap}, {@code msgt}, {@code obvious}, {@code wake} and
     * {@code color}, with {@code ""} for a directive the record does not have, then the line the
     * record starts on. The line is turned into an {@code int} here. The grammar writes it with
     * {@code String.valueOf}, so the {@code NumberFormatException} branch, which would report the
     * problem and store {@code -1}, cannot be reached from a data file.
     *
     * <p>Function extract coded before 260915, commented in full on 261018.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @param errors       the soft-error sink: a record-count mismatch is appended here, as is a
     *                     line number that does not parse
     * @return the parsed projection records, one per record, in file order
     */
    private static List<ProjectionParseRecord> extract(
            @NotNull ProjectionGrammar parser, @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {

        List<ProjectionParseRecord> records = new ArrayList<>();
        ProjectionGrammar.FileContext output = parser.file();
        List<List<String>> results = output.projections;
        errorCatcher.throwIfAny();
        String declaredRecordCount = output.records;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        for (List<String> result : results) {
            String lineStr = result.getLast();
            int line;
            try {
                line = Integer.parseInt(lineStr);
            } catch (NumberFormatException e) {
                errors.add("Invalid number format for declared line: " + lineStr +
                        " Projection code: " + result.getFirst());
                line = -1;
            }

            records.add(new ProjectionParseRecord(result.get(0), result.get(1),
                    result.get(2), result.get(3), result.get(4), result.get(5),
                    result.get(6), result.get(7), result.get(8), result.get(9),
                    result.get(10), result.get(11), result.get(12), result.get(13),
                    result.get(14), line));
        }

        return records;
    }

    /**
     * Reads {@code filename} and returns the full {@link ParseResult}: the assembled
     * {@link Projection}s in file order, and every error message collected on the way. This is
     * the Java form of C's {@code run_parse_projection} followed by {@code finish_parse_projection}.
     *
     * <p>There are three outcomes. A clean file gives 56 projections and no errors for the shipped
     * data. A lexer or grammar error gives no projections and the parser's messages. Anything
     * the assembler objects to is a message beside the records that survived, except that a count
     * of surviving {@code type:element} records other than 25 empties the list, as C's
     * {@code quit_fmt} stops the game. A file that cannot be opened is an {@link IOException}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} of type {@link Projection}
     * @throws IOException when the file cannot be found or read
     */
    public ParseResult<Projection> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename, ProjectionLexer::new,
                ProjectionGrammar::new,
                ProjectionReader::extract,
                new ProjectionAssembler(), logger);
    }
}