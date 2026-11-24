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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.backend.parser.random.RandomLexer;
import uk.co.jackoftradesltd.backend.parser.random.RandomParser;
import uk.co.jackoftradesltd.channel.parser.ParseErrors;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.numerics.Random;

/**
 * Turns one dice-notation string (for example {@code "2d6"}, {@code "1+1d3"} or {@code "M80"})
 * into a {@link Random} by driving the generated {@code RandomLexer}/
 * {@code RandomParser}. It is the thin hand-written bridge between that grammar and
 * {@link Random#parseStr}, the caller that hands it the whole string, a leading {@code -} included.
 * The leading {@code -} is the grammar's to read: it flags the value as negated and {@link Random}
 * shifts the base so the whole range flips sign, as the negation block of {@code parse_random} in
 * {@code parser.c} does. A second {@code -} is read only as a signed zero, as {@code strtoul} reads
 * it, so {@code "--0"} loads and {@code "--1"} is refused.
 *
 * <p><b>Why it is not like the other readers.</b> It does not implement {@link Reader}. The input is
 * a string, not a data file, and there is exactly one value to read, not a list, so there is no
 * {@code GrammarDriver} run, no assembler and no {@code record-count:} header - only the
 * grammar's own errors.
 *
 * <p><b>Two C parsers, one grammar.</b> C has two parsers for this notation. {@code parse_random} in
 * {@code parser.c} reads every {@code rand} field of the data files and has no {@code $} variables;
 * {@code dice_parse_string} in {@code z-dice.c} reads the {@code dice:} and {@code time:} lines of an
 * effect and does. This reader serves both through {@code Random.g4}, and differs from
 * {@code parse_random} in one place: it accepts a lower-case {@code m}, as {@code dice_parse_string}
 * does. It reads no {@code $} variables, as {@code parse_random} has none, and it refuses a base
 * straight into {@code M} ({@code "5M3"}), as both C parsers do; the effect lines that use
 * {@code $} variables are read by the effect-block lexers through {@code DiceStrings}, not here. A
 * repeated {@code d} or {@code M} ({@code "dd1"}, {@code "M1M2"}) and a run of blanks directly
 * before a number are read, as C's loop reads them. Wherever both accept a string they produce the
 * same four terms. The remaining difference is written up in {@code docs/Accepted_Deviations.md},
 * "Random".
 *
 * <p>Class commented in full on 261019, updated on 261105 for the removal of the soft-error path
 * and on 261124 for the whole-string hand-over and the rewritten grammar.
 *
 * @author Rowan Crowther
 */
public class RandomReader {
    /**
     * Logger used to report dice strings that fail to parse.
     *
     * <p>Field logger commented in full on 260915, reworded on 261019.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Parses one dice-notation string (for example {@code "2d6"}) into a single {@link Random}.
     * Despite the parameter name, {@code filename} is not a path: the text itself is read via
     * {@link CharStreams#fromString}.
     *
     * <p>The ways it can end:
     * <ul>
     *   <li><b>Success:</b> the {@link Random} the grammar's {@code dice} rule built.</li>
     *   <li><b>Malformed text:</b> a lexer or grammar error is collected by {@link ParseErrors}
     *       and logged, and the method returns {@code null}. {@link Random#parseStr} passes it on.
     *       It covers a number missing after {@code M} or {@code d} ({@code "5M"}, {@code "-1d"}) as
     *       much as stray characters: ANTLR conjures a placeholder token for the gap, the grammar's
     *       {@code numberOrMissing} turns it into a marker instead of parsing it, and the collected
     *       error then ends the parse here. C rejects the same strings.</li>
     *   <li><b>A number too large for an {@code int}:</b> the grammar's {@code numberOrMissing}
     *       gives the same marker for a number above {@code INT_MAX}, including one too long to fit
     *       a {@code long}, and the {@code dice} rule leaves its {@link Random} {@code null}. No
     *       parse error is collected for it, so the check after {@code throwIfAny()} logs the string
     *       and returns {@code null}. C's {@code parse_random} rejects anything above
     *       {@code INT_MAX} too, and {@code 2147483647} itself is accepted by both.</li>
     *   <li><b>Anything unexpected:</b> any other exception is logged and rethrown. None of them is
     *       checked, so the signature declares none.</li>
     * </ul>
     *
     * <p>Function parse coded before 260915, commented in full on 260915; return type and error
     * handling changed and re-commented on 261019, and the missing-number case moved into the grammar
     * the same day. Re-commented on 261020 once a number above {@code INT_MAX} began returning
     * {@code null} instead of throwing, and {@code throws Exception} was dropped.
     *
     * @param filename the dice-notation text to parse, despite the name (e.g. {@code "2d6"})
     * @return the parsed {@link Random}, or {@code null} if the text is malformed or holds a number
     *         above {@code INT_MAX}
     */
    public @Nullable Random parse(@NotNull String filename) {
        try {
            CharStream stream = CharStreams.fromString(filename);
            RandomLexer lexer = new RandomLexer(stream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            RandomParser parser = new RandomParser(tokens);

            ParseErrors errorCatcher = ParseErrors.install(lexer, parser, filename);

            RandomParser.DiceContext output = parser.dice();

            errorCatcher.throwIfAny();

            if (output.random == null) {
                logger.error("Invalid value found while parsing Random string: {}", filename);
                return null;
            }

            return output.random;
        } catch (ParseCancellationException pce) {
            logger.error("Error while parsing random string: {}", filename, pce);
            return null;
        } catch (Exception e) {
            logger.error("Error while reading random {}", filename, e);
            throw e;
        }
    }
}
