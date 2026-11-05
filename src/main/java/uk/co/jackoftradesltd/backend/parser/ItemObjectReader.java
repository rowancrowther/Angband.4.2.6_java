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
import uk.co.jackoftradesltd.backend.parser.grammars.itemobject.ItemObjectGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.itemobject.ItemObjectLexer;
import uk.co.jackoftradesltd.backend.parser.itemobject.ItemObjectAssembler;
import uk.co.jackoftradesltd.backend.parser.itemobject.ItemObjectParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/object.txt} into {@link ObjectKind} objects - the table of item
 * <em>kinds</em> (a Wooden Torch, a Potion of Cure Light Wounds, a Dagger) that every object in the
 * game is made from. It is the thin hand-written bridge between the generated
 * {@code ItemObjectLexer} / {@code ItemObjectGrammar} pair and the game, plugged into the shared
 * {@link GrammarDriver} pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code object_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_object} and the {@code parse_object_*} handlers become the grammar plus
 *       {@link ItemObjectAssembler}: the grammar collects each record's raw text, and the assembler
 *       resolves the tval, base, colour, flags, {@code values:} entries, brands, slays, curses and
 *       effects and turns the numbers and dice into ints and {@code Random}s;</li>
 *   <li>{@code run_parse_object} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_object} becomes the list handed back, one {@link ObjectKind} per
 *       {@code name:} block. Of what C does there, the merging of the base's kind flags into each
 *       kind is done in {@link ItemObjectAssembler}; the numbering is done by
 *       {@code ObjectDataLoader.loadItemObjects}, which registers each kind through
 *       {@code ObjectRegistry.addObjectKind} (the next sval under its base and its index in the
 *       table) and then calls {@code ObjectRegistry.updateObjectBaseKindMax} for C's
 *       {@code ordinary_kind_max}. A kind straight out of this reader therefore still has sval 0
 *       and index 0.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> The list is in file order, which is {@code k_info} order:
 * {@code parse_object_name} pushes each record on the head of a chain, but
 * {@code finish_parse_object} starts at that head with the highest index and counts down, so the
 * first record in the file is {@code kidx} 0. (The sibling readers whose C lists run last-first,
 * such as {@code SlayReader}, are the opposite case.) The first four records of the shipped file
 * are the sentinels {@code <pile>}, {@code <unknown item>}, {@code <unknown treasure>} and
 * {@code <curse object>}; they load like any other record, and {@code ObjectDataLoader} picks the
 * ones it needs out afterwards.
 * <p>
 * As in C, the first error stops the load and nothing is returned: a syntax error is thrown from
 * {@link #extract} as a {@code ParseCancellationException}, and a record the assembler refuses (an
 * unknown tval, flag, value name, element, brand, slay or curse, or a number or dice string that
 * does not parse) is thrown as a
 * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}. Neither is caught here. A
 * colour that does not resolve is not an error; it becomes white, as in C.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>C takes the directives in any order and lets a repeated scalar one overwrite while
 *       {@code flags:}, {@code values:}, {@code slay:}, {@code brand:}, {@code msg:},
 *       {@code vis-msg:} and {@code desc:} accumulate (the three texts are joined with no
 *       separator). The grammar does the same, but a record with nothing after its {@code name:}
 *       line is a syntax error here; see {@code docs/grammar_issues.md}.</li>
 *   <li>A dice directive the record leaves out is {@code null} on the kind for {@code attack:}'s
 *       to-hit and to-damage, {@code armor:}'s to-AC, {@code charges:}, {@code pile:}'s stack and
 *       {@code pval:}, where C's zeroed record holds a zero {@code struct random}; see
 *       {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot at the end, which is where
 *       C keeps it. This is the accepted deviation recorded in the registry's class block, so the
 *       count is the plain record count, one lower than C's {@code k_max}.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class ItemObjectReader coded before 260915, commented in full on 261017, updated on 261105 for
 * the removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
public class ItemObjectReader implements Reader<ObjectKind> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code object.txt} is logged under this reader's name before it is rethrown. Syntax and
     * assembler errors do not go through it; they are thrown unlogged to the caller.
     *
     * <p>Field logger coded before 260915, commented in full on 261017, updated on 261105 for the
     * removal of the soft-error path.
     */
    private final static Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled kinds, in file order, unwrapped from the
     * {@link ParseResult} that {@link #parseWithResults} gives. Satisfies the {@link Reader}
     * contract.
     * <p>
     * A syntax error or a refused record throws, exactly as in {@link #parseWithResults}, so an
     * empty list only ever means a clean file with no records.
     *
     * <p>Function parse coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param filename the object data file to read
     * @return every {@link ObjectKind} in the file, in file order
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ObjectKind> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, and hand back the raw
     * {@link ItemObjectParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler. That covers the
     * cases where C returns a parser error too: a directive before any {@code name:} line (C's
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}), an unknown directive, and a directive missing one
     * of its fields, such as an {@code attack:} with two of its three dice.
     * <p>
     * The records stay in file order, which is the order the kinds are returned in. The copy at the
     * end detaches the result from the parse tree, so the returned list is the caller's to keep.
     *
     * <p>Function extract commented in full before 260915, provenance stamp added on 260915,
     * commented in full again on 261017, updated on 261105 for the removal of the soft-error path
     * and the {@code record-count:} header.
     *
     * @param parser       the generated {@code ItemObjectGrammar} parser
     * @param errorCatcher the live hard-error collector installed by the driver
     * @return the parsed {@link ItemObjectParseRecord}s in file order
     */
    private static @NotNull List<ItemObjectParseRecord> extract(
            @NotNull ItemObjectGrammar parser,
            @NotNull ParseErrors errorCatcher) {
        ItemObjectGrammar.FileContext output = parser.file();
        List<ItemObjectParseRecord> records = output.itemObjects;
        
        errorCatcher.throwIfAny();

        return new ArrayList<>(records);
    }

    /**
     * Parse the given file, returning the assembled {@link ObjectKind} list (in file order) wrapped
     * in a {@link ParseResult}.
     * <p>
     * This is the Java counterpart of {@code run_parse_object} and the {@code parse_object_*}
     * handlers in {@code obj-init.c}: it wires {@code ItemObjectLexer}, {@code ItemObjectGrammar},
     * {@link #extract} and {@link ItemObjectAssembler} into {@link GrammarDriver#run}. As C stops at
     * the first error, so does this: a syntax error throws a {@code ParseCancellationException},
     * and an unresolved tval, flag, value name, brand, slay or curse, or a number or dice string
     * that does not parse, throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and no result is
     * returned. An unreadable file is an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}.
     *
     * <p>Function parseWithResults commented in full before 260915, provenance stamp added on 260915,
     * commented in full again on 261017, updated on 261105 for the removal of the soft-error path.
     *
     * @param filename the object data file to read
     * @return every object kind in the file, in file order
     * @throws IOException if the file cannot be read
     */
    public ParseResult<ObjectKind> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ItemObjectLexer::new,
                ItemObjectGrammar::new,
                ItemObjectReader::extract,
                new ItemObjectAssembler(), logger);
    }
}
