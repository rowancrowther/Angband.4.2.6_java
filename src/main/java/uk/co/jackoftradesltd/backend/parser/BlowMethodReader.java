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
import uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodAssembler;
import uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.blowmethod.BlowMethodGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.blowmethod.BlowMethodLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/blow_methods.txt} into {@link BlowMethod} objects - the ways a monster
 * can deliver a blow (HIT, BITE, GAZE, INSULT and so on) that a {@code blow:} line in
 * {@code monster.txt} names. It is the thin hand-written bridge between the generated
 * {@code BlowMethodLexer} / {@code BlowMethodGrammar} pair and the game, plugged into the shared
 * {@link GrammarDriver} pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code meth_parser} in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_meth} and the {@code parse_meth_*} handlers become the grammar plus
 *       {@link BlowMethodAssembler}: the grammar collects each record's raw text, and the assembler
 *       turns the four flags into booleans and looks up the {@code msg:} name;</li>
 *   <li>{@code run_parse_meth} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_meth} becomes the list handed back, one {@link BlowMethod} per
 *       {@code name:} block. Copying the chain into the {@code blow_methods} array and setting
 *       {@code z_info->blow_methods_max} is not done here; the loader hands the list to
 *       {@code MonsterRegistry}.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> {@link #extract} returns the parse records in file order, but the
 * {@link BlowMethod} list that comes back is in <em>reverse</em> file order, because
 * {@link BlowMethodAssembler} puts each method at the front. That is C's order: {@code parse_meth_name}
 * pushes each record on the head of a chain, and {@code finish_parse_meth} fills {@code blow_methods}
 * from slot 1 upward starting at that head, so slot 1 is the last record in the file (MOAN in the
 * shipped data) and HIT comes last. {@code finish_parse_eff} fills downward and so keeps file order,
 * which is why the blow-effect reader does not reverse. It matters when two records share a name,
 * where {@code findmeth} (and so {@code MonsterRegistry.lookupBlowMethod}) finds the later one.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code blow_methods.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unresolved {@code msg:} name) skips only the offending record and the
 *       rest still load.</li>
 *   <li>C takes the directives in any order, leaves any of them out, and appends repeated
 *       {@code desc:} lines. The grammar demands one fixed sequence with exactly one {@code desc:};
 *       see {@code docs/grammar_issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code blow_methods_max} is the plain
 *       record count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class BlowMethodReader coded before 260915, commented in full on 261017.
 */
public class BlowMethodReader implements Reader<BlowMethod> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code blow_methods.txt} is logged under this reader's name before it is rethrown. Syntax and
     * soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled blow methods, in C's order (the reverse of
     * the file's, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unresolved {@code msg:} just goes
     * missing. Callers that must tell a partial or failed load from a clean one have to use
     * {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the assembled {@link BlowMethod} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<BlowMethod> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link BlowMethodParseRecord}s, still holding unconverted text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: an {@code act:} line before
     * any {@code name:} (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}) and a {@code cut:} line with
     * no number. The header check is soft - {@link GrammarDriver#checkRecordCount} only appends to
     * {@code errors}, so a miscount never discards the records that did parse.
     * <p>
     * The records stay in file order here. The reversal into C's order is done later, by
     * {@link BlowMethodAssembler}. The copy at the end detaches the result from the parse tree, so
     * the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link BlowMethodParseRecord}s in file order
     */
    private static List<BlowMethodParseRecord> extract(
            @NotNull BlowMethodGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        BlowMethodGrammar.FileContext output = parser.file();
        List<BlowMethodParseRecord> results = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }

    /**
     * Parse the given file, returning both the assembled {@link BlowMethod} list (in C's order, the
     * reverse of the file's) and any soft errors gathered along the way, for callers that need to
     * tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_meth} and the {@code parse_meth_*} handlers
     * in {@code mon-init.c}: it wires {@code BlowMethodLexer}, {@code BlowMethodGrammar},
     * {@link #extract} and {@link BlowMethodAssembler} into {@link GrammarDriver#run}. A syntax
     * error returns no items and the collected messages; an unresolved {@code msg:} name or a
     * {@code record-count:} mismatch leaves the good records in the result alongside the messages,
     * so the list can be short without anything having been thrown. C would have stopped at the
     * first of any of these. An unreadable file is not a result at all but an {@link IOException},
     * the analogue of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code MonsterDataLoader.loadBlowMethods} gates on
     * {@link ParseResult#hasErrors()} for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to parse
     * @return a {@link ParseResult} of the assembled methods plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<BlowMethod> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                BlowMethodLexer::new,
                BlowMethodGrammar::new,
                BlowMethodReader::extract,
                new BlowMethodAssembler(), logger);
    }
}
