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
import uk.co.jackoftradesltd.backend.parser.curse.CurseAssembler;
import uk.co.jackoftradesltd.backend.parser.curse.CurseParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.curse.CurseGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.curse.CurseLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.Curse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/curse.txt} into {@link Curse} objects - the catalogue of curses
 * (vulnerability, teleportation, siren and so on) that can be rolled onto a piece of equipment,
 * each with the object kinds it may afflict, the modifiers, flags and random effect it carries and
 * the curses it cannot share an item with. It is the thin hand-written bridge between the generated
 * {@code CurseLexer} / {@code CurseGrammar} pair and the game, plugged into the shared
 * {@link GrammarDriver} pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code curse_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_curse} and the {@code parse_curse_*} handlers become the grammar plus
 *       {@link CurseAssembler}: the grammar collects each record's raw text, and the assembler
 *       resolves the {@code type:} names to object bases, splits {@code flags:} and
 *       {@code values:} into object flags, modifiers and per-element data, builds the effects and
 *       links the {@code conflict:} names;</li>
 *   <li>{@code run_parse_curse} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_curse} becomes the list handed back, one {@link Curse} per
 *       {@code name:} block. Both of its checks are made in {@link CurseAssembler}, not here: no
 *       more than 254 curses, and no {@code MULTIPLY_WEIGHT} curse with a negative weight
 *       adjustment. Copying the chain into the {@code curses} array and setting
 *       {@code z_info->curse_max} are done by {@code ObjectDataLoader.loadCurses}, which hands the
 *       list to {@code ObjectRegistry};</li>
 *   <li>{@code cleanup_curse} has no counterpart, as the garbage collector frees the records.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> {@link #extract} returns the parse records in file order, but the
 * {@link Curse} list that comes back is in <em>reverse</em> file order, because
 * {@link CurseAssembler} reverses it before numbering. That is C's order: {@code parse_curse_name}
 * pushes each record on the head of a chain, and {@code finish_parse_curse} fills {@code curses}
 * from slot 1 upward starting at that head, so slot 1 is the last record in the file
 * ({@code air swing} in the shipped data) and {@code vulnerability} comes last. The order shows
 * wherever two curses share a name and the first is kept ({@code lookup_curse}), and a curse's
 * index is its position in this list.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code curse.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown type, flag, modifier or element, a weight outside 16 bits, an
 *       unknown effect) skips only the offending record and the rest still load. The 254-curse limit
 *       is the exception: it is thrown as an {@link IllegalArgumentException} that escapes
 *       {@link #parseWithResults}.</li>
 *   <li>C takes the directives after {@code name:} in any order and accepts a record that is only a
 *       name. The grammar accepts any order for {@code type:}, {@code weight:}, {@code combat:},
 *       {@code flags:}, {@code values:}, {@code msg:}, {@code desc:}, {@code conflict:},
 *       {@code conflict-flags:} and {@code time:}, but needs one directive after {@code name:}, and
 *       takes the effect lines as a block: {@code effect:} first, then {@code effect-yx:} or
 *       {@code dice:} (not both, and only one {@code dice:}), then an optional
 *       {@code effect-msg:}, which C has no directive for. A second {@code msg:} replaces the first
 *       where C appends it, and {@code flags:} lists need a pipe between names where C also splits
 *       on spaces; see {@code docs/grammar_issues.md}.</li>
 *   <li>A curse with no {@code msg:} line carries an empty message where C's is {@code NULL}, and a
 *       {@code conflict:} name no curse has is a soft error where C never checks it; see
 *       {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code curse_max} is the plain record
 *       count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class CurseReader coded before 260915, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public class CurseReader implements Reader<Curse> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code curse.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled curses, in C's order (the reverse of the
     * file's, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unknown type or flag just goes
     * missing. Callers that must tell a partial or failed load from a clean one have to use
     * {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the curse data file to read
     * @return the assembled {@link Curse} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Curse> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link CurseParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} line (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an unknown directive
     * ({@code PARSE_ERROR_UNDEFINED_DIRECTIVE}) and a numeric directive with no number
     * ({@code PARSE_ERROR_NOT_NUMBER}). The header check is soft - {@link GrammarDriver#checkRecordCount}
     * only appends to {@code errors}, so a miscount never discards the records that did parse.
     * <p>
     * The records stay in file order here. The reversal into C's order is done later, by
     * {@link CurseAssembler}. The copy at the end detaches the result from the parse tree, so the
     * returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated Curse parser, positioned at the start of the file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, appended to on a record-count mismatch
     * @return the parsed {@link CurseParseRecord}s in file order
     */
    private static List<CurseParseRecord> extract(
            @NotNull CurseGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        CurseGrammar.FileContext output = parser.file();
        List<CurseParseRecord> records = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, records.size(), errors);

        return new ArrayList<>(records);
    }

    /**
     * Parse the given file, returning both the assembled {@link Curse} list (in C's order, the
     * reverse of the file's) and any soft errors gathered along the way, for callers that need to
     * tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_curse} and the {@code parse_curse_*}
     * handlers in {@code obj-init.c}: it wires {@code CurseLexer}, {@code CurseGrammar},
     * {@link #extract} and {@link CurseAssembler} into {@link GrammarDriver#run}. A syntax error
     * returns no items and the collected messages; an unresolved type, flag or modifier, a weight
     * outside 16 bits, a negative weight on a {@code MULTIPLY_WEIGHT} curse, an unknown effect, an
     * unmatched {@code conflict:} name or a {@code record-count:} mismatch leaves the good records
     * in the result alongside the messages, so the list can be short without anything having been
     * thrown. C would have stopped at the first of any of these. An unreadable file is not a result
     * at all but an {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s
     * {@code quit()}. A file of more than 254 curses is not a result either: the assembler throws
     * an {@link IllegalArgumentException}, which this method does not catch.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadCurses} gates on the
     * errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the curse data file to read
     * @return a {@link ParseResult} of the assembled curses plus any soft errors
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file holds more than 254 curses
     */
    public @NotNull ParseResult<Curse> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                CurseLexer::new,
                CurseGrammar::new,
                CurseReader::extract,
                new CurseAssembler(), logger);
    }
}