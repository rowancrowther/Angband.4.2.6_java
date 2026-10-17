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
import uk.co.jackoftradesltd.backend.parser.grammars.summon.SummonGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.summon.SummonLexer;
import uk.co.jackoftradesltd.backend.parser.summon.SummonAssembler;
import uk.co.jackoftradesltd.backend.parser.summon.SummonParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.Summon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/summon.txt} into {@link Summon} objects - the kinds of monster a
 * summoning spell can call up (ANY, KIN, DEMON, UNDEAD and so on), each with its message type, the
 * monster bases or race flag it is limited to, whether uniques may appear, and the summon to try
 * if it finds nothing. It is the thin hand-written bridge between the generated
 * {@code SummonLexer} / {@code SummonGrammar} pair and the game, plugged into the shared
 * {@link GrammarDriver} pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code summon_parser} in {@code mon-summon.c} (not {@code mon-init.c}, where
 * the other monster-data parsers live):
 * <ul>
 *   <li>{@code init_parse_summon} and the {@code parse_summon_*} handlers become the grammar plus
 *       {@link SummonAssembler}: the grammar collects each record's raw text, and the assembler
 *       resolves the {@code msgt:} name to a message type, each {@code base:} name to a
 *       monster base, the {@code race-flag:} name to a race flag, and the {@code uniques:}
 *       number to a boolean;</li>
 *   <li>{@code run_parse_summon} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_summon} becomes the list handed back, one {@link Summon} per
 *       {@code name:} block. Copying the chain into the {@code summons} array and setting
 *       {@code summon_max} is not done here; {@code MonsterDataLoader.loadSummons} hands the list
 *       to {@code MonsterRegistry}. The back-patching of each summon's {@code fallback} index from
 *       its {@code fallback:} name is done by the assembler's second pass, which links the
 *       {@link Summon} references instead;</li>
 *   <li>{@code cleanup_summon} has no counterpart, since the garbage collector frees the list.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The list that comes back is in file order, and so is C's
 * {@code summons} array. C's chain is built head-first, so it holds the records in reverse, but
 * {@code finish_parse_summon} starts at the chain head with the highest index and counts down, which
 * puts the last record in the file in the last slot. (Contrast {@code finish_parse_meth}, which
 * counts up and so reverses the blow methods.) The data file asks for {@code ANY} to be first;
 * Java finds a summon by name through {@code MonsterRegistry.lookupSummon}, so the order only decides
 * which record wins when two share a name, and the first does in both, as in
 * {@code summon_name_to_idx}.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code summon.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error, and an unresolved {@code msgt:}, {@code base:} or
 *       {@code race-flag:} is one. Here a syntax error is fatal and returns no items, but an
 *       assembler error skips only the offending record and the rest still load.</li>
 *   <li>An unresolved {@code fallback:} name is no error in C, which stores -1. Here it is reported as a
 *       soft error, but the summon is kept with a {@code null} fallback.</li>
 *   <li>C takes the directives in any order and leaves any but {@code name:} out. The grammar
 *       demands one fixed sequence; see {@code docs/grammar_issues.md}. The assembler's reading of
 *       {@code uniques:} and {@code msgt:} differs too; see {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot at the end. This is the
 *       accepted deviation recorded in the registry's class block, so C's {@code summon_max}, one
 *       higher than the record count, has no counterpart: {@code MonsterRegistry} keeps no count.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customized copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class SummonReader coded before 260915, commented in full on 261017.
 */
public class SummonReader implements Reader<Summon> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code summon.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled summons, in file order (which is C's
     * order too, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unresolved {@code msgt:},
     * {@code base:} or {@code race-flag:} just goes missing. Callers that must tell a partial or
     * failed load from a clean one have to use {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the assembled {@link Summon} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Summon> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link SummonParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C fails too: a {@code msgt:} line before any {@code name:}
     * (an {@code assert} in {@code parse_summon_message_type}, as in every handler but
     * {@code parse_summon_name}) and a {@code uniques:} line with no number
     * ({@code PARSE_ERROR_MISSING_FIELD}). The header check is soft -
     * {@link GrammarDriver#checkRecordCount} only appends to {@code errors}, so a miscount never
     * discards the records that did parse.
     * <p>
     * The records stay in file order, and so does the result: nothing later reverses them. The copy
     * at the end detaches the result from the parse tree, so the returned list is the caller's to
     * keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated parser for this file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link SummonParseRecord}s in file order
     */
    private static List<SummonParseRecord> extract(
            @NotNull SummonGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        SummonGrammar.FileContext output = parser.file();
        List<SummonParseRecord> records = output.summons;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parse the given file, returning both the assembled {@link Summon} list (in file order) and
     * any soft errors gathered along the way, for callers that need to tell a clean load from a
     * partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_summon} and the {@code parse_summon_*}
     * handlers in {@code mon-summon.c}: it wires {@code SummonLexer}, {@code SummonGrammar},
     * {@link #extract} and {@link SummonAssembler} into {@link GrammarDriver#run}. A syntax error
     * returns no items and the collected messages; an unresolved {@code msgt:}, {@code base:} or
     * {@code race-flag:} name, an unresolved {@code fallback:} name or a {@code record-count:}
     * mismatch leaves the good records in the result alongside the messages, so the list can be
     * short without anything having been thrown. C would have stopped at the first of the
     * first three and ignored the fourth. An unreadable file is not a result at all but an
     * {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code MonsterDataLoader.loadSummons} gates on
     * the soft errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to parse
     * @return a {@link ParseResult} of the assembled summons plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<Summon> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                SummonLexer::new,
                SummonGrammar::new,
                SummonReader::extract,
                new SummonAssembler(),
                logger);
    }
}
