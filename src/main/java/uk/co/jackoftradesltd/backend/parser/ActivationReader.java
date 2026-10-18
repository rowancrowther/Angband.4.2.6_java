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
import uk.co.jackoftradesltd.backend.parser.activation.ActivationAssembler;
import uk.co.jackoftradesltd.backend.parser.activation.ActivationParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.activations.ActivationsLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.Activation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/activation.txt} into {@link Activation} objects - the catalogue of
 * named powers (CURE_POISON, FIRE_BOLT, DRAGON_POWER and so on) that artifacts and some objects
 * activate. It is the thin hand-written bridge between the generated {@code ActivationsLexer} /
 * {@code ActivationsGrammar} pair and the game, plugged into the shared {@link GrammarDriver}
 * pipeline and implementing the common {@link Reader} contract.
 * <p>
 * It stands in for {@code act_parser} in {@code obj-init.c}:
 * <ul>
 *   <li>{@code init_parse_act} and the {@code parse_act_*} handlers become the grammar plus
 *       {@link ActivationAssembler}: the grammar collects each record's raw text, and the assembler
 *       turns {@code aim:} into a boolean, {@code level:} and {@code power:} into ints, and hands
 *       the effect blocks to {@code EffectAssembler};</li>
 *   <li>{@code run_parse_act} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_act} becomes the list handed back, one {@link Activation} per
 *       {@code name:} block. Counting {@code z_info->act_max}, copying the chain into the
 *       {@code activations} array and numbering each entry's {@code index} are not done here:
 *       {@code ActivationAssembler} numbers the list through {@code Activation.setIndex}, and
 *       {@code ObjectDataLoader.loadActivations} hands it to {@code ObjectRegistry}.</li>
 * </ul>
 * This class itself only supplies the pieces {@link GrammarDriver#run} needs - the lexer and parser
 * constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * <strong>List order.</strong> {@link #extract} returns the parse records in file order, but the
 * {@link Activation} list that comes back is in <em>reverse</em> file order, because
 * {@link ActivationAssembler} puts each activation at the front. That is C's order:
 * {@code parse_act_name} pushes each record on the head of a chain, and {@code finish_parse_act}
 * fills {@code activations} from slot 1 upward starting at that head, so slot 1 is the last record
 * in the file (DRAGON_POWER in the shipped data). It matters wherever a name is used twice, since
 * C's {@code findact} then finds the later record first.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error; the
 *       upstream {@code activation.txt} has no such line, and this repository's copy adds it.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but an
 *       assembler error (a number too large for an int, an effect that does not resolve) skips only
 *       the offending record and the rest still load.</li>
 *   <li>C takes the directives in any order and a repeated {@code aim:}, {@code level:} or
 *       {@code power:} overwrites. The grammar does the same, but C <em>appends</em> a repeated
 *       {@code msg:} or {@code desc:} and the grammar overwrites, which the shipped data hits:
 *       RESIST_ALL and TELE_LONG each split their description over two {@code desc:} lines. See
 *       {@code docs/grammar_issues.md}.</li>
 *   <li>A missing {@code msg:} or {@code desc:} arrives in the {@link Activation} as an empty
 *       string where C holds {@code NULL}, and {@code aim:} is true only for exactly {@code 1}
 *       where C accepts any non-zero number; see {@code docs/Assembler_Issues.md}.</li>
 *   <li>The list is base 0 and holds only real records: no blank slot 0. This is the accepted
 *       deviation recorded in the registry's class block, so {@code act_max} is the plain record
 *       count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class ActivationReader coded before 260915, commented in full on 261017.
 *
 * @author Rowan Crowther
 */
public class ActivationReader implements Reader<Activation> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code activation.txt} is logged under this reader's name before it is rethrown. Syntax and
     * soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parse the given file and return just the assembled activations, in C's order (the reverse of
     * the file's, see the class block), discarding the soft-error channel (use
     * {@link #parseWithResult} to inspect the errors). Satisfies the {@link Reader} contract.
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list, and a record skipped for a number too large for an int or an
     * effect that does not resolve just goes missing. Callers that must tell a partial or failed
     * load from a clean one have to use {@link #parseWithResult}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the activation data file to read
     * @return the assembled {@link Activation} list (empty if the parse failed closed)
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<Activation> parse(@NotNull String filename) throws IOException {
        return parseWithResult(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: run the parser's {@code file} entry
     * rule, fail closed on any hard lexer/parser error, validate the {@code record-count:} header,
     * and hand back the raw {@link ActivationParseRecord}s, still holding unresolved text.
     * <p>
     * Ordering matters: {@link ParseErrors#throwIfAny()} must fire <em>after</em> the parse but
     * <em>before</em> the records are used, so a malformed file aborts with a
     * {@code ParseCancellationException} here rather than reaching the assembler, and a half-built
     * record list is never measured against the declared count to produce a second, misleading
     * error. That covers the cases where C returns a parser error too: a directive before any
     * {@code name:} line (C's {@code PARSE_ERROR_MISSING_RECORD_HEADER}) and a numeric directive
     * with no number. The header check is soft - {@link GrammarDriver#checkRecordCount} only appends
     * to {@code errors}, so a miscount never discards the records that did parse.
     * <p>
     * The records stay in file order here. The reversal into C's order is done later, by
     * {@link ActivationAssembler}. The copy at the end detaches the result from the parse tree, so
     * the returned list is the caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the generated Activations parser, positioned at the start of the file
     * @param errorCatcher the live hard-error collector installed by the driver
     * @param errors       the soft-error sink, mutated in place
     * @return the parsed {@link ActivationParseRecord}s in file order
     */
    private static List<ActivationParseRecord> extract(
            @NotNull ActivationsGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        ActivationsGrammar.FileContext output = parser.file();
        List<ActivationParseRecord> result = output.records;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, result.size(), errors);

        return new ArrayList<>(result);
    }

    /**
     * Parse the given file, returning both the assembled {@link Activation} list (in C's order, the
     * reverse of the file's) and any soft errors gathered along the way, for callers that need to
     * tell a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_act} and the {@code parse_act_*} handlers in
     * {@code obj-init.c}: it wires {@code ActivationsLexer}, {@code ActivationsGrammar},
     * {@link #extract} and {@link ActivationAssembler} into {@link GrammarDriver#run}. A syntax
     * error returns no items and the collected messages; a number too large for an int, an effect
     * that does not resolve or a {@code record-count:} mismatch leaves the good records in the
     * result alongside the messages, so the list can be short without anything having been thrown.
     * C would have stopped at the first of any of these. An unreadable file is not a result at all
     * but an {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s
     * {@code quit()}.
     * <p>
     * This is the variant callers should prefer: {@code ObjectDataLoader.loadActivations} gates on
     * the errors for exactly that reason. Most of the other readers spell this method
     * {@code parseWithResults}; this one is singular.
     *
     * <p>Function parseWithResult coded before 260915, commented in full on 261017.
     *
     * @param filename the activation data file to read
     * @return a {@link ParseResult} of the assembled activations plus any soft errors
     * @throws IOException if the file cannot be read
     */
    public ParseResult<Activation> parseWithResult(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                ActivationsLexer::new,
                ActivationsGrammar::new,
                ActivationReader::extract,
                new ActivationAssembler(), logger);
    }
}