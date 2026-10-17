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
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffectAssembler;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffectParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.bloweffect.BlowEffectGrammar;
import uk.co.jackoftradesltd.backend.parser.grammars.bloweffect.BlowEffectLexer;
import uk.co.jackoftradesltd.channel.parser.GrammarDriver;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads {@code lib/gamedata/blow_effects.txt} into {@link BlowEffect} objects by driving the
 * ANTLR-generated {@code BlowEffectLexer} / {@code BlowEffectGrammar}. It is the thin
 * hand-written bridge between the generated grammar code and the game, implementing the shared
 * {@link Reader} contract.
 * <p>
 * It stands in for the blow-effect data-file machinery in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_eff} and the {@code parse_eff_*} handlers become the grammar plus
 *       {@link BlowEffectAssembler}, which resolves each raw directive into its enum or object;</li>
 *   <li>{@code run_parse_eff} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_eff} becomes the ordered list handed back, one {@link BlowEffect} per
 *       record in file order.</li>
 * </ul>
 * This class itself only supplies the four pieces {@link GrammarDriver#run} needs - the lexer and
 * parser constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The {@code record-count:} header is a Java-only addition, checked as a soft error;
 *       {@code blow_effects.txt} in C has no such line.</li>
 *   <li>C stops at the first parse error. Here a syntax error is fatal and returns no items, but
 *       an assembler error skips only the offending record and the rest still load.</li>
 *   <li>The list is base 0 and holds only real records, with no blank trailing slot; this is the
 *       accepted deviation recorded in the registry's class block, so the {@code blow_effects_max}
 *       counter is the plain record count, one lower than C's.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * @author Rowan Crowther
 *
 * <p>Class BlowEffectReader coded before 260915, commented in full on 261017.
 */
public class BlowEffectReader implements Reader<BlowEffect> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code blow_effects.txt} is logged under this reader's name before it is rethrown. Syntax
     * and soft errors do not go through it; they travel in the {@link ParseResult}.
     *
     * <p>Field logger coded before 260915, commented in full on 261017.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled blow effects, in file order,
     * discarding the soft-error channel (use {@link #parseWithResults} to inspect errors).
     * <p>
     * Because the errors are dropped, a syntax error and a clean empty file are indistinguishable
     * here: both give an empty list. Callers that must tell a partial or failed load from a clean
     * one have to use {@link #parseWithResults}.
     *
     * <p>Function parse coded before 260915, commented in full on 261017.
     *
     * @param filename the data file to read
     * @return the blow effects read from the file
     * @throws IOException if the file cannot be read
     */
    @Override
    public @NotNull List<BlowEffect> parse(@NotNull String filename) throws IOException {
        return parseWithResults(filename).items();
    }

    /**
     * The {@link GrammarDriver.Extractor} for this grammar: drive the {@code file} rule and hand
     * back its records as {@link BlowEffectParseRecord}s, still holding raw text.
     * <p>
     * Syntax errors are fatal: {@link ParseErrors#throwIfAny()} runs straight after the parse and
     * throws, which {@link GrammarDriver#run} turns into an empty {@link ParseResult}, so a caller
     * never sees a half-read file. The {@code record-count:} header, by contrast, is a soft check
     * that only adds to {@code errors}. Records come back in file order, which is the order C's
     * {@code finish_parse_eff} files them into {@code blow_effects}.
     * <p>
     * The copy at the end detaches the result from the parse tree, so the returned list is the
     * caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017.
     *
     * @param parser       the parser, positioned at the start of the token stream
     * @param errorCatcher collector for the lexer/parser syntax errors
     * @param errors       collector for soft errors, shared with the assembler
     * @return the parsed records in file order
     */
    private static List<BlowEffectParseRecord> extract(
            @NotNull BlowEffectGrammar parser,
            @NotNull ParseErrors errorCatcher,
            @NotNull List<String> errors) {
        BlowEffectGrammar.FileContext output = parser.file();
        List<BlowEffectParseRecord> results = output.blowEffects;
        errorCatcher.throwIfAny();

        String declaredRecordCount = output.declaredRecordCount;
        GrammarDriver.checkRecordCount(declaredRecordCount, results.size(), errors);

        return new ArrayList<>(results);
    }

    /**
     * Run the parser and return the assembled effects together with any soft errors, for
     * callers that need to distinguish a clean load from a partial one.
     * <p>
     * This is the Java counterpart of {@code run_parse_eff} and the {@code parse_eff_*} handlers
     * in {@code mon-init.c}: it wires {@code BlowEffectLexer}, {@code BlowEffectGrammar},
     * {@link #extract} and {@link BlowEffectAssembler} into {@link GrammarDriver#run}. A syntax
     * error returns no items and the collected messages; an assembler error or a
     * {@code record-count:} mismatch leaves the good records in the result alongside the messages.
     * C would have stopped at the first of any of these. An unreadable file is not a result at all
     * but an {@link IOException}, the analogue of {@code parse_file_quit_not_found}'s
     * {@code quit()}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261017.
     *
     * @param filename the name of the file
     * @return the assembled blow effects plus the errors collected while assembling them
     * @throws IOException if the file cannot be read
     */
    public ParseResult<BlowEffect> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                BlowEffectLexer::new,
                BlowEffectGrammar::new,
                BlowEffectReader::extract,
                new BlowEffectAssembler(),
                logger);
    }
}
