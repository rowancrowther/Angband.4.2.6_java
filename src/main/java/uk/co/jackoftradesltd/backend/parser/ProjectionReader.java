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
import uk.co.jackoftradesltd.channel.parser.*;
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
 * <p>C quits on the first bad line, and so does this reader. A lexer or grammar error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}. Every fault in a record, and the
 * faults C quits on in {@code parse_projection_code} and {@code finish_parse_projection}, make
 * {@link ProjectionAssembler} throw a {@link DataFileParseException}. {@link GrammarDriver} catches
 * neither, so both come out of {@link #parseWithResults} and {@link #parse} with no
 * {@link ParseResult}. A file that cannot be opened is an {@link IOException}, where C quits.
 *
 * <p>Class ProjectionReader coded before 260915, commented in full on 261018; the stop-the-load
 * paragraph rewritten on 261021, and again on 261105 for the removal of the soft-error path and
 * the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
public class ProjectionReader implements Reader<Projection> {
    /**
     * Logger handed to {@link GrammarDriver#run}, which logs an {@link IOException} under this
     * reader's name before rethrowing it. The reader itself logs only in {@link #extract}'s
     * line-number branch, which a data file cannot reach.
     *
     * <p>Field logger coded before 260915, commented in full on 261018, updated on 261105 to
     * mention the {@link #extract} warning.
     */
    private static final Logger logger = LogManager.getLogger(ProjectionReader.class);

    /**
     * Reads {@code filename} and returns the assembled projections in file order, unwrapped from
     * the {@link ParseResult} that {@link #parseWithResults} gives. Any error throws exactly as it
     * does there.
     *
     * <p>Function parse coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to read
     * @return every projection in the file, in file order
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
     * and builds one {@link ProjectionParseRecord} per record. No value is interpreted here: that is the
     * assembler's work, as it is C's in the {@code parse_projection_*} handlers.
     *
     * <p>Each row the grammar hands back holds sixteen strings: the fifteen directives in the order
     * {@code code}, {@code name}, {@code type}, {@code desc}, {@code player-desc},
     * {@code blind-desc}, {@code lash-desc}, {@code numerator}, {@code denominator},
     * {@code divisor}, {@code damage-cap}, {@code msgt}, {@code obvious}, {@code wake} and
     * {@code color}, with {@code ""} for a directive the record does not have, then the line the
     * record starts on. The line is turned into an {@code int} here. The grammar writes it with
     * {@code String.valueOf}, so the {@code NumberFormatException} branch, which would log a
     * warning and store {@code -1}, cannot be reached from a data file.
     *
     * <p>Function extract coded before 260915, commented in full on 261018, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser, positioned at the start of the file
     * @param errorCatcher the hard-error channel; {@code throwIfAny} aborts before the rows are used
     * @return the parsed projection records, one per record, in file order
     */
    private static List<ProjectionParseRecord> extract(
            @NotNull ProjectionGrammar parser, @NotNull ParseErrors errorCatcher) {

        List<ProjectionParseRecord> records = new ArrayList<>();
        ProjectionGrammar.FileContext output = parser.file();
        List<List<String>> results = output.projections;
        errorCatcher.throwIfAny();

        for (List<String> result : results) {
            String lineStr = result.getLast();
            int line;
            try {
                line = Integer.parseInt(lineStr);
            } catch (NumberFormatException e) {
                String message = "Invalid number format for declared line: " + lineStr +
                        " Projection code: " + result.getFirst();
                logger.fatal(message, e);
                throw new DataFileParseException(message);
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
     * Reads {@code filename} and returns the assembled {@link Projection}s, in file order, wrapped
     * in a {@link ParseResult}. This is the Java form of C's {@code run_parse_projection} followed
     * by {@code finish_parse_projection}.
     *
     * <p>A clean file gives 56 projections for the shipped data. A lexer or grammar error, which
     * includes a {@code code:} with no value, throws a {@code ParseCancellationException}. Any
     * fault in a record, an unknown or misplaced code in one of the first 25 records, or a count
     * of {@code type:element} records other than 25, throws the assembler's
     * {@link DataFileParseException} out of this method. A file that cannot be opened is an
     * {@link IOException}. No result is returned for a file that fails.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261018; the
     * stop-the-load outcome documented on 261021, and the outcomes rewritten on 261105 for the
     * removal of the soft-error path.
     *
     * @param filename the name of the file to parse
     * @return a {@link ParseResult} of type {@link Projection}
     * @throws IOException when the file cannot be found or read
     * @throws DataFileParseException if the assembler refuses a record or the element block, as C
     *         quits in its {@code parse_projection_*} handlers or {@code finish_parse_projection}
     */
    public ParseResult<Projection> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename, ProjectionLexer::new,
                ProjectionGrammar::new,
                ProjectionReader::extract,
                new ProjectionAssembler(), logger);
    }
}