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
import uk.co.jackoftradesltd.backend.parser.grammars.slay.SlayGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.slay.SlayLexer;
import uk.co.jackoftradesltd.backend.parser.slay.SlayAssembler;
import uk.co.jackoftradesltd.backend.parser.slay.SlayParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.Slay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/slay.txt} into {@link Slay} objects - the catalogue of slays (EVIL_2,
 * DRAGON_5 and so on) that ego items, artifacts and object kinds name by code. It is the thin hand-written bridge between the generated {@code SlayLexer} /
 * {@code SlayGrammar} pair and the game, plugged into the shared {@link GrammarDriver} pipeline and
 * implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code slay_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_slay} and the {@code parse_slay_*} handlers become the grammar plus
 *       {@link SlayAssembler}: the grammar collects each record's raw text, and the assembler
 *       resolves the {@code race-flag:} or {@code base:} target and turns the three numbers into
 *       ints;</li>
 *   <li>{@code run_parse_slay} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_slay} becomes the list handed back, one {@link Slay} per {@code code:}
 *       block. Copying the chain into the {@code slays} array, setting {@code z_info->slay_max} and
 *       the 254-entry limit are not done here; {@code ObjectDataLoader.loadSlays} hands the list to
 *       {@code ObjectRegistry}, and its Javadoc records that the limit is not applied.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> {@link #extract} returns the parse records in file order, but the
 * {@link Slay} list that comes back is in <em>reverse</em> file order, because {@link SlayAssembler}
 * puts each slay at the front. That is C's order: {@code parse_slay_code} pushes each record on the
 * head of a chain, and {@code finish_parse_slay} fills {@code slays} from slot 1 upward starting at
 * that head, so slot 1 is the last record in the file (UNDEAD_5 in the shipped data) and EVIL_2
 * comes last. It matters wherever two slays are equivalent and the first is kept, such as
 * {@code Rune.initRunes}.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code slay.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown race flag or monster base, both or neither of them, a number too
 *       large for an int) skips only the offending record and the rest still load.</li>
 *   <li>C takes the directives in any order, leaves any of them out, and lets a repeated one
 *       overwrite. The grammar takes them in any order and a repeat keeps the last value, but a
 *       directive left out is read as empty text, which the assembler rejects for the three numbers
 *       where C leaves them 0; see {@code docs/Assembler_Issues.md}.</li>
 *   <li>C rejects a second {@code race-flag:} or {@code base:} in one record; here the last one
 *       wins; see {@code docs/grammar_issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code slay_max} is the plain record
 *       count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class SlayReader coded before 260915, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public class SlayReader implements Reader<Slay> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code slay.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled slays, in C's order (the reverse of the
     * file's, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unknown race flag or base just goes
     * missing. Callers that must tell a partial or failed load from a clean one have to use
     * {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the slay data file to read
     * @return the assembled {@link Slay} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Slay> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link SlayParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code code:} line (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}) and a numeric directive
     * with no number. The header check is soft - {@link GrammarDriver#checkRecordCount} only appends
     * to {@code errors}, so a miscount never discards the records that did parse.
     * <p>
     * The records stay in file order here. The reversal into C's order is done later, by
     * {@link SlayAssembler}. The copy at the end detaches the result from the parse tree, so the
     * returned list is the caller's to keep.
     *
     * <p>Function extract commented in full before 260915, provenance stamp added on 260915,
     * commented in full again on 261017.
     *
     * @param parser       the generated Slay parser
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link SlayParseRecord}s in file order
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

    /**
     * Parse the given file, returning both the assembled {@link Slay} list (in C's order, the
     * reverse of the file's) and any soft errors gathered along the way, for callers that need to
     * tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_slay} and the {@code parse_slay_*} handlers
     * in {@code obj-init.c}: it wires {@code SlayLexer}, {@code SlayGrammar}, {@link #extract} and
     * {@link SlayAssembler} into {@link GrammarDriver#run}. A syntax error returns no items and the
     * collected messages; an unresolved race flag or monster base, a record naming both or neither,
     * or a {@code record-count:} mismatch leaves the good records in the result alongside the
     * messages, so the list can be short without anything having been thrown. C would have stopped
     * at the first of any of these. An unreadable file is not a result at all but an
     * {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadSlays} gates on the
     * errors for exactly that reason.
     *
     * <p>Function parseWithResults commented in full before 260915, provenance stamp added on 260915,
     * commented in full again on 261017.
     *
     * @param filename the slay data file to read
     * @return a {@link ParseResult} of the assembled slays plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<Slay> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                SlayLexer::new,
                SlayGrammar::new,
                SlayReader::extract,
                new SlayAssembler(), logger);
    }
}
