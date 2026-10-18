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
import uk.co.jackoftradesltd.backend.parser.egoitem.EgoItemAssembler;
import uk.co.jackoftradesltd.backend.parser.egoitem.EgoItemParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.egoitems.EgoItemsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.egoitems.EgoItemsLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.EgoItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/ego_item.txt} into {@link EgoItem} objects - the table of ego templates
 * ("of Speed", "(Defender)", "of Slay Evil") that {@code ego_apply_magic} stamps onto a base item. It
 * is the thin hand-written bridge between the generated {@code EgoItemsLexer} /
 * {@code EgoItemsGrammar} pair and the game, plugged into the shared {@link GrammarDriver} pipeline
 * and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code ego_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_ego} and the {@code parse_ego_*} handlers become the grammar plus
 *       {@link EgoItemAssembler}: the grammar collects each record's raw text, and the assembler
 *       resolves the {@code type:} and {@code item:} lines into object kinds, the flags into object,
 *       kind and element flags, the {@code values:} entries into modifiers and resistances, and the
 *       brands, slays, curses and activation into their registry entries, and turns the numbers and
 *       dice into ints and {@code Random}s;</li>
 *   <li>{@code run_parse_ego} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_ego} becomes the list handed back, one {@link EgoItem} per {@code name:}
 *       block. C counts the chain to set {@code e_max}, copies it into {@code e_info} and numbers each
 *       entry {@code eidx}; here the assembler numbers each ego from 0 as it builds it, and
 *       {@code ObjectDataLoader.loadEgoItems} hands the list to {@code ObjectRegistry.setEgoItems},
 *       which records the count.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The list is in file order, which is {@code e_info} order:
 * {@code parse_ego_name} pushes each record on the head of a chain, but {@code finish_parse_ego}
 * starts at that head with the highest index and counts down, so the first record in the file is
 * {@code eidx} 0. (The sibling readers whose C lists run last-first, such as {@code SlayReader}, are
 * the opposite case.) The first record of the shipped file is "of Shadows" and the last is
 * "of Speed". C's {@code type:} and {@code item:} handlers resolve against {@code k_info} while
 * parsing, and the assembler does the same while assembling, so {@code object.txt} must already be
 * loaded; the brands, slays, curses and activations likewise.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code ego_item.txt} has no such line, and this repository's copy adds one.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (an unknown tval, sval, flag, value name, element, brand, slay, curse or
 *       activation, or a number or dice string that does not parse) skips only the offending record
 *       and the rest still load. A skipped record takes no {@code eidx}, so every ego after it is
 *       numbered one lower than its place in the file.</li>
 *   <li>C takes the directives in any order and lets a repeated scalar one overwrite while
 *       {@code type:}, {@code item:}, {@code flags:}, {@code values:}, {@code slay:},
 *       {@code brand:} and {@code desc:} accumulate (the description lines are joined with no
 *       separator). The grammar does the same, but a record with nothing after its {@code name:}
 *       line is a syntax error here, and the grammar and assembler differ from C in several smaller
 *       ways; see {@code docs/grammar_issues.md} and {@code docs/Assembler_Issues.md}.</li>
 *   <li>A {@code combat:} or {@code time:} line the record leaves out is {@code null} on the ego,
 *       where C's zeroed record holds a zero {@code struct random}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot at the end, which is where
 *       C keeps it. This is the accepted deviation recorded in the registry's class block, so the
 *       count is the plain record count, one lower than C's {@code e_max}.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class EgoItemReader coded before 260915, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public class EgoItemReader implements Reader<EgoItem> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code ego_item.txt} is logged under this reader's name before it is rethrown. Syntax and soft
     * errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled ego items, in file order, discarding the
     * soft-error channel (use {@link #parseWithResults} to inspect the errors). Satisfies the
     * {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for an unknown tval, sval, flag, brand,
     * slay, curse or activation just goes missing. Callers that must tell a partial or failed load
     * from a clean one have to use {@link #parseWithResults}, as {@code ObjectDataLoader.loadEgoItems}
     * does.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the ego-item data file to read
     * @return the assembled {@link EgoItem} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<EgoItem> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link EgoItemParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} line (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an unknown directive, and
     * a directive missing one of its fields, such as a {@code combat:} with two of its three dice.
     * The header check is soft - {@link GrammarDriver#checkRecordCount} only appends to
     * {@code errors}, so a miscount never discards the records that did parse.
     * <p>
     * The records stay in file order, which is the order the egos are returned in. The copy at the
     * end detaches the result from the parse tree, so the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated {@code EgoItemsGrammar} parser
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link EgoItemParseRecord}s in file order
     */
    private static List<EgoItemParseRecord> extract(
            @NotNull EgoItemsGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        EgoItemsGrammar.FileContext output = parser.file();
        List<EgoItemParseRecord> result = output.items;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);

    }

    /**
     * Parse the given file, returning both the assembled {@link EgoItem} list (in file order) and
     * any soft errors gathered along the way, for callers that need to tell a clean load from a
     * partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_ego} and the {@code parse_ego_*} handlers in
     * {@code obj-init.c}: it wires {@code EgoItemsLexer}, {@code EgoItemsGrammar}, {@link #extract}
     * and {@link EgoItemAssembler} into {@link GrammarDriver#run}. A syntax error returns no items
     * and the collected messages; an unresolved tval, sval, flag, value name, brand, slay, curse or
     * activation, a number or dice string that does not parse, or a {@code record-count:} mismatch
     * leaves the good records in the result alongside the messages, so the list can be short without
     * anything having been thrown. C would have stopped at the first of any of these (bar an unknown
     * {@code act:} name, which C does not treat as an error at all). An unreadable file is not a
     * result at all but an {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s
     * {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadEgoItems} gates on the
     * errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the ego-item data file to read
     * @return a {@link ParseResult} of the assembled egos plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<EgoItem> parseWithResults(String filename) throws IOException {
        return GrammarDriver.run(filename,
                EgoItemsLexer::new,
                EgoItemsGrammar::new,
                EgoItemReader::extract,
                new EgoItemAssembler(), logger);
    }
}