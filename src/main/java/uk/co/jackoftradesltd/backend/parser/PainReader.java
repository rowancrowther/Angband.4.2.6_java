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
import uk.co.jackoftradesltd.backend.parser.grammars.pain.PainGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.pain.PainLexer;
import uk.co.jackoftradesltd.backend.parser.pain.PainAssembler;
import uk.co.jackoftradesltd.backend.parser.pain.PainParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/pain.txt} into {@link MonsterPain} objects - the monster "pain
 * message" sets that a {@code pain:} line in {@code monster_base.txt} refers to by index. It is the
 * thin hand-written bridge between the generated {@code PainLexer} / {@code PainGrammar} pair and
 * the game, plugged into the shared {@link GrammarDriver} pipeline and implementing the common
 * {@link Reader} contract.
 * <p>
 * It stands in for the pain-message data-file machinery in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_pain}, {@code parse_pain_type} and {@code parse_pain_message} become the
 *       grammar plus {@link PainAssembler}: the grammar collects each {@code type:} line and its
 *       seven {@code message:} lines, and the assembler parses the serial into the pain index;</li>
 *   <li>{@code run_parse_pain} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_pain} becomes the list handed back, one {@link MonsterPain} per
 *       {@code type:} block in file order. Filing the sets into {@code pain_messages} under their
 *       index and setting {@code z_info->mp_max} is not done here; the loader hands the list to
 *       {@code MonsterRegistry}.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * As in C, the first error stops the load and nothing is returned: a syntax error is thrown from
 * {@link #extract} as a {@code ParseCancellationException}, and a record the assembler refuses is
 * thrown as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is
 * caught here.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>C accepts a {@code type:} block with fewer than seven {@code message:} lines and leaves the
 *       missing slots {@code NULL}. The grammar demands exactly seven, so such a block is a syntax
 *       error here. The file's own header says every entry must have seven.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0, and no blank slot for an
 *       index the file skips. This is the accepted deviation recorded in the registry's class
 *       block, so {@code mp_max} is the plain record count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class PainReader coded before 260915, commented in full on 261017, updated on 261105 for the
 * removal of the soft-error path.
 */
public class PainReader implements Reader<MonsterPain> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code pain.txt} is logged under this reader's name before it is rethrown. Syntax and
     * assembler errors do not go through it; they are thrown unlogged to the caller.
     *
     * <p>Field logger coded before 260915, commented in full on 261017, updated on 261105 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled pain sets, in file order, unwrapped from
     * the {@link ParseResult} that {@link #parseWithResults} gives. Satisfies the {@link Reader}
     * contract.
     * <p>
     * A syntax error or a refused record throws, exactly as in {@link #parseWithResults}, so an
     * empty list only ever means a clean file with no records.
     *
     * <p>Function parse coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the data file to parse
     * @return every {@link MonsterPain} in the file, in file order
     * @throws IOException if the file cannot be read
     */
    @NotNull
    @Override
    public List<MonsterPain> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, and hand back the raw
     * {@link PainParseRecord}s, still holding unparsed text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler. That covers the
     * cases where C returns a parser error too: a {@code message:} line before any {@code type:}
     * (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an eighth {@code message:} line (C's
     * {@code PARSE_ERROR_TOO_MANY_ENTRIES}), and a {@code type:} line with no number. Records come
     * back in file order, which is the order C's {@code finish_parse_pain} files them in once it
     * has indexed them by {@code pain_idx}, for a file whose serials ascend as the header requires.
     * <p>
     * The copy at the end detaches the result from the parse tree, so the returned list is the
     * caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path and the {@code record-count:} header.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @return the parsed {@link PainParseRecord}s in file order
     */
    private static List<PainParseRecord> extract(
            @NotNull PainGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        PainGrammar.FileContext output = parser.file();
        List<PainParseRecord> results = output.monsterPain;
        errorCatcher.throwIfAny();

        return new ArrayList<>(results);
    }

    /**
     * Parse the given file, returning the assembled {@link MonsterPain} list wrapped in a
     * {@link ParseResult}.
     * <p>
     * This is the Java counterpart of {@code run_parse_pain} and the {@code parse_pain_*} handlers
     * in {@code mon-init.c}: it wires {@code PainLexer}, {@code PainGrammar}, {@link #extract} and
     * {@link PainAssembler} into {@link GrammarDriver#run}. As C stops at the first error, so does
     * this: a syntax error throws a {@code ParseCancellationException}, an assembler error throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and no result is
     * returned. An unreadable file is an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017, updated on
     * 261105 for the removal of the soft-error path.
     *
     * @param filename the data file to parse
     * @return every pain set in the file, in file order
     * @throws IOException if the file cannot be read
     */
    public ParseResult<MonsterPain> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                PainLexer::new,
                PainGrammar::new,
                PainReader::extract,
                new PainAssembler(), logger);
    }
}
