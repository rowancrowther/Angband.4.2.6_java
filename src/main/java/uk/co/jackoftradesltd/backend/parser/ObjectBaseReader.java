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
import uk.co.jackoftradesltd.backend.parser.grammars.objectbase.ObjectBaseGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.objectbase.ObjectBaseLexer;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseAssembler;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseParseRecord;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/object_base.txt} into {@link ObjectBase} objects - the per-tval
 * defaults (display colour, breakage chance, maximum stack size, shared kind flags and hated
 * elements) that every object kind of that type inherits. It is the thin hand-written bridge
 * between the generated {@code ObjectBaseLexer} / {@code ObjectBaseGrammar} pair and the game,
 * plugged into the shared {@link GrammarDriver} pipeline and implementing the common {@link Reader}
 * contract.
 * <p>
 * It stands in for {@code object_base_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_object_base} and its handlers ({@code parse_object_base_defaults},
 *       {@code _name}, {@code _graphics}, {@code _break}, {@code _max_stack} and {@code _flags})
 *       become the grammar plus {@link ObjectBaseAssembler}. The grammar collects each record's raw
 *       text and folds the file-wide {@code default:} values into any record that left out its own
 *       {@code break:} or {@code max-stack:}, which is what C's {@code memcpy} of the defaults
 *       into each new record does. The assembler then resolves the tval name, the colour and the
 *       flag names, and turns the two numbers into ints;</li>
 *   <li>{@code run_parse_object_base} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_object_base} becomes the list handed back, one {@link ObjectBase} per
 *       {@code name:} block. C copies each record into {@code kb_info}, an array with one slot per
 *       tval, and frees the chain; here {@code ObjectDataLoader.loadObjectBases} hands the list to
 *       {@code ObjectRegistry}, which finds a base by walking it.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List shape and duplicate tvals.</strong> The list is in file order, followed by one
 * synthetic {@code TV_NONE} base that {@link ObjectBaseAssembler} always appends to stand in for a
 * zeroed {@code kb_info} slot, so the shipped file's 34 records come back as 35 bases. C's chain is
 * walked from its head, the last record, and each copy overwrites the slot for its tval, so when two
 * records name the same tval the <em>earlier</em> one is the one C keeps. The Java list keeps both,
 * but {@code ObjectRegistry.getBaseFromTVal} takes the first match, so a lookup by tval finds the
 * same base. Order matters nowhere else: a lookup by name also takes the first match.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code object_base.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error, and so does this reader, by two routes. A syntax error is
 *       collected, and returns no items and the messages in the {@link ParseResult}. An error found
 *       by {@link ObjectBaseAssembler} (an unknown tval, an unknown flag, a break chance or maximum
 *       stack with no digits) is thrown as a {@code DataFileParseException} out of
 *       {@link #parseWithResults} and {@link #parse}, so no record is skipped and no partial list
 *       comes back. Only the {@code record-count:} mismatch is left as a soft error that keeps
 *       the records.</li>
 *   <li>C takes the directives in any order, leaves any of them out and lets a repeated one
 *       overwrite, and accepts any number of {@code default:} lines anywhere, each affecting only
 *       the records that follow it. The grammar insists on at least one {@code default:} line before
 *       the first record, then {@code name:}, {@code graphics:}, an optional {@code break:}, an
 *       optional {@code max-stack:} and any number of {@code flags:} lines in that order, so any
 *       other shape is a syntax error and costs the whole file; see {@code docs/grammar_issues.md}.</li>
 *   <li>The assembler agrees with C where it once did not: an unknown colour is white, an
 *       {@code IGNORE_} element flag sets the ignore bit, a default the file left out is 0 and the
 *       synthetic {@code TV_NONE} base, like a record with no plural part, has a {@code null} name.
 *       A number outside the range of an {@code int} is clamped where C wraps it; see
 *       {@link ObjectBaseAssembler} and {@code docs/Accepted_Deviations.md}.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class ObjectBaseReader coded before 260915, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
public class ObjectBaseReader implements Reader<ObjectBase> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code object_base.txt} is logged under this reader's name before it is rethrown. Syntax and
     * soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Unlike the other readers' loggers this one is {@code public}; nothing outside this class
     * reads it.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    public static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled bases, discarding the soft-error channel
     * (use {@link #parseWithResults} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error is visible only as an empty list, where a clean
     * load always ends in the synthetic {@code TV_NONE} base. A {@code record-count:} mismatch is
     * invisible here, since it keeps every record. An error found by {@link ObjectBaseAssembler} is
     * not dropped but thrown as a {@code DataFileParseException}, by this method as by
     * {@link #parseWithResults}. Callers that must see a syntax message or a count mismatch have to use
     * {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261028.
     *
     * @param filename the object-base data file to read
     * @return the assembled {@link ObjectBase} list, ending in the synthetic {@code TV_NONE} base
     * (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<ObjectBase> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link ObjectBaseParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a {@code graphics:},
     * {@code break:}, {@code max-stack:} or {@code flags:} line before any {@code name:} (C's
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER}), a {@code default:} label other than the two C
     * knows (C's {@code PARSE_ERROR_UNDEFINED_DIRECTIVE}) and a numeric directive with no number. The
     * header check is soft - {@link GrammarDriver#checkRecordCount} only appends to {@code errors},
     * so a miscount never discards the records that did parse.
     * <p>
     * The records come back in file order, with the file-wide {@code default:} values already folded
     * into any record that omitted {@code break:} or {@code max-stack:}; that folding is done by the
     * grammar's {@code file} rule, not here. The copy at the end detaches the result from the parse
     * tree, so the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated ObjectBase parser, positioned at the start of the file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link ObjectBaseParseRecord}s in file order
     */
    private static List<ObjectBaseParseRecord> extract(
            @NotNull ObjectBaseGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ObjectBaseGrammar.FileContext output = parser.file();
        List<ObjectBaseParseRecord> result = output.objectBaseList;
        errorCatcher.throwIfAny();

        String declaredCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredCount, result.size(), errors);

        return new ArrayList<>(result);
    }

    /**
     * Parse the given file, returning both the assembled {@link ObjectBase} list (file order, then
     * the synthetic {@code TV_NONE} base) and any soft errors gathered along the way, for callers
     * that need to tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_object_base} and the
     * {@code parse_object_base_*} handlers in {@code obj-init.c}: it wires {@code ObjectBaseLexer},
     * {@code ObjectBaseGrammar}, {@link #extract} and {@link ObjectBaseAssembler} into
     * {@link GrammarDriver#run}. A syntax error returns no items and the collected messages; a
     * {@code record-count:} mismatch leaves every record in the result alongside a message. An
     * unresolved tval or flag, or a break chance or maximum stack with no digits, is not a result at
     * all but a {@code DataFileParseException} from {@link ObjectBaseAssembler}, as C stops at the
     * first of these. So is an unreadable file, which is an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}. An unknown colour is neither: it is white,
     * as in C.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadObjectBases} gates on
     * the errors for exactly that reason.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261028.
     *
     * @param filename the object-base data file to read
     * @return a {@link ParseResult} of the assembled bases plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public @NotNull ParseResult<ObjectBase> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ObjectBaseLexer::new,
                ObjectBaseGrammar::new,
                ObjectBaseReader::extract,
                new ObjectBaseAssembler(), logger);
    }
}