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
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2Assembler;
import uk.co.jackoftradesltd.backend.parser.bloweffect.BlowEffect2ParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.bloweffect2.BlowEffect2Grammar;
import uk.co.jackoftradesltd.backend.parser.grammars.bloweffect2.BlowEffect2Lexer;
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
 * ANTLR-generated {@link BlowEffect2Lexer} / {@link BlowEffect2Grammar}, which turn each record
 * into a {@link BlowEffect2ParseRecord}. It is the thin hand-written bridge between the generated
 * grammar code and the game, implementing the shared {@link Reader} contract. The loader that
 * calls it is {@code MonsterDataLoader.loadBlowEffects}, which hands the list to
 * {@code MonsterRegistry.setBlowEffects}.
 * <p>
 * It stands in for the blow-effect data-file machinery in {@code mon-init.c}:
 * <ul>
 *   <li>{@code init_parse_eff} and the {@code parse_eff_*} handlers become the grammar plus
 *       {@link BlowEffect2Assembler}, which resolves each raw directive into its enum or object;</li>
 *   <li>{@code run_parse_eff} (which calls {@code parse_file_quit_not_found}) becomes
 *       {@link GrammarDriver#run}, which opens the file; a file that cannot be read surfaces as an
 *       {@link IOException}, and the caller decides that it is fatal, as C's {@code quit()} is;</li>
 *   <li>{@code finish_parse_eff} becomes the ordered list handed back, one {@link BlowEffect} per
 *       record in file order.</li>
 * </ul>
 * This class itself only supplies the four pieces {@link GrammarDriver#run} needs - the lexer and
 * parser constructors, the {@link #extract} step and the assembler - and owns the logger.
 * <p>
 * As in C, the first error stops the load and nothing is returned: a syntax error is thrown
 * from {@link #extract} as a {@code ParseCancellationException}, and a record the assembler
 * refuses is thrown as a {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}.
 * Neither is caught here.
 * <p>
 * Differences from C worth knowing about:
 * <ul>
 *   <li>The list is base 0 and holds only real records, with no blank trailing slot; this is the
 *       accepted deviation recorded in the {@code MonsterRegistry} class block, so the
 *       {@code blow_effects_max} counter is the plain record count, one lower than C's.</li>
 *   <li>The whole file is parsed before any record is assembled ({@link GrammarDriver#run}), where
 *       C acts on each line as it is read. A file with a refused record early and a syntax error
 *       later is therefore reported as the syntax error, where C reports the earlier line. Either
 *       way the load stops and nothing is returned.</li>
 *   <li>The file is opened at exactly the path given. C's {@code parse_file} would look for a
 *       customised copy in the user directory first.</li>
 * </ul>
 *
 * <p>Class BlowEffectReader coded before 260915, commented in full on 261116, updated on 261116
 * for the move to {@code BlowEffect2Lexer} / {@code BlowEffect2Grammar}.
 *
 * @author Rowan Crowther
 */
public class BlowEffectReader implements Reader<BlowEffect> {
    /**
     * Logger handed to {@link GrammarDriver#run} so that an {@link IOException} while reading
     * {@code blow_effects.txt} is logged under this reader's name before it is rethrown. Syntax
     * and assembler errors do not go through it; they are thrown unlogged to the caller.
     *
     * <p>Field logger coded before 260915, commented in full on 261017, updated on 261105 for the
     * removal of the soft-error path.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses {@code filename} and returns just the assembled blow effects, in file order,
     * unwrapped from the {@link ParseResult} that {@link #parseWithResults} gives.
     * <p>
     * A syntax error or a refused record throws, exactly as in {@link #parseWithResults}, so an
     * empty list only ever means a clean file with no records.
     *
     * <p>Function parse coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path.
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
     * back its records as {@link BlowEffect2ParseRecord}s, still holding raw text.
     * <p>
     * Syntax errors are fatal: {@link ParseErrors#throwIfAny()} runs straight after the parse and
     * throws a {@code ParseCancellationException}, which {@link GrammarDriver#run} does not catch,
     * so a caller never sees a half-read file. Records come back in file order, which is the order
     * C's {@code finish_parse_eff} files them into {@code blow_effects}.
     * <p>
     * The copy at the end detaches the result from the parse tree, so the returned list is the
     * caller's to keep.
     *
     * <p>Function extract coded before 260915, commented in full on 261017, updated on 261105 for
     * the removal of the soft-error path.
     *
     * @param parser       the parser, positioned at the start of the token stream
     * @param errorCatcher collector for the lexer/parser syntax errors
     * @return the parsed records in file order
     */
    private static List<BlowEffect2ParseRecord> extract(
            @NotNull BlowEffect2Grammar parser,
            @NotNull ParseErrors errorCatcher) {
        BlowEffect2Grammar.FileContext output = parser.file();
        List<BlowEffect2ParseRecord> results = output.blowEffects;
        errorCatcher.throwIfAny();

        return new ArrayList<>(results);
    }

    /**
     * Run the parser and return the assembled effects wrapped in a {@link ParseResult}.
     * <p>
     * This is the Java counterpart of {@code run_parse_eff} and the {@code parse_eff_*} handlers
     * in {@code mon-init.c}: it wires {@link BlowEffect2Lexer}, {@link BlowEffect2Grammar},
     * {@link #extract} and {@link BlowEffect2Assembler} into {@link GrammarDriver#run}. As C stops
     * at the first error, so does this: a syntax error throws a
     * {@code ParseCancellationException} and a refused record throws a
     * {@link uk.co.jackoftradesltd.channel.parser.DataFileParseException}, and no result is
     * returned. An unreadable file is an {@link IOException}, the analogue of
     * {@code parse_file_quit_not_found}'s {@code quit()}.
     *
     * <p>Function parseWithResults coded before 260915, commented in full on 261116, updated on
     * 261116 for the move to {@code BlowEffect2Lexer} / {@code BlowEffect2Grammar}.
     *
     * @param filename the name of the file
     * @return every blow effect in the file, in file order
     * @throws IOException if the file cannot be read
     */
    public ParseResult<BlowEffect> parseWithResults(@NotNull String filename) throws IOException {
        return GrammarDriver.run(filename,
                BlowEffect2Lexer::new,
                BlowEffect2Grammar::new,
                BlowEffectReader::extract,
                new BlowEffect2Assembler(),
                logger);
    }
}
