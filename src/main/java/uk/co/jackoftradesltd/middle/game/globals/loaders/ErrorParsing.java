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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.backend.parser.ParseResult;

import java.util.List;

/**
 * Reporting sink for the <em>soft</em> errors a data-file parse collects.
 * <p>
 * The loading pipeline has two error channels, and they behave differently on
 * purpose. Hard <em>syntax</em> errors are raised by
 * {@code ParseErrors.throwIfAny()} while the file is still being read: the text
 * is malformed, nothing about it can be trusted, and the parse aborts. Soft
 * errors are the ones an extractor or assembler records about an individual
 * record it could not use - an unknown flag, a name that resolves to nothing -
 * and they accumulate in {@link ParseResult#errors()} while the records that
 * <em>did</em> assemble ride out alongside them. That is the partial-results
 * contract described on {@code GrammarDriver.run}.
 * <p>
 * Until this class existed the soft channel was write-only: every loader tested
 * {@link ParseResult#hasErrors()} and logged a single generic "Invalid
 * &lt;file&gt; file" line, so the per-record messages - the ones naming the
 * offending line and value - were built, carried back, and then discarded when
 * the {@link ParseResult} went out of scope, leaving them visible only to the
 * reader tests. This class is the consumer that gives them a voice at runtime,
 * and it is what lets one run surface every soft error across every data file
 * at once instead of one crash at a time.
 *
 * @author Rowan Crowther
 */
public class ErrorParsing {
    /**
     * Log every soft error a parse collected, then report whether the load is
     * still usable.
     * <p>
     * Each error is written on its own line at {@code ERROR}, prefixed with the
     * file it came from, because the assembler messages carry a record line
     * number but no filename and a single run reports on nearly forty files. A
     * summary line follows giving the error count beside the number of records
     * that survived - the line that separates "one bad record out of eighty"
     * from "nothing loaded at all". A clean parse logs nothing.
     * <p>
     * Deliberately non-fatal, and deliberately throws nothing: under the
     * partial-results contract a soft error is a report, not a stop. Whether to
     * continue is the caller's decision, which is what the return value serves.
     * Callers are expected to register {@link ParseResult#items()} either way;
     * a {@code false} marks the structural case, where a file other loaders
     * depend on yielded no records at all and carrying on would only move the
     * failure somewhere less obvious.
     *
     * @param filename    the data file the parse was reading, used to attribute
     *                    each message
     * @param parseResult the completed parse, supplying both the collected
     *                    errors and the records that assembled
     * @param logger      the calling loader's logger, so each message is
     *                    attributed to that loader rather than to this helper
     * @return {@code true} if any records loaded - including when the parse was
     * clean - and {@code false} only when the file yielded nothing
     */
    public static boolean reportAndCheck(@NotNull String filename, @NotNull ParseResult<?> parseResult, @NotNull Logger logger) {
        if (!parseResult.hasErrors())
            return true;

        List<String> errors = parseResult.errors();
        for (String error : errors) {
            logger.error(filename + ": " + error);
        }
        logger.error(filename + ": " + errors.size() + " error(s) " + parseResult.items().size() + " item(s) loaded.");

        return !parseResult.items().isEmpty();
    }
}
