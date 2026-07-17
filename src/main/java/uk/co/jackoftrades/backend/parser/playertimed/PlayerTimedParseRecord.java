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

package uk.co.jackoftrades.backend.parser.playertimed;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * The raw, still-textual capture of one {@code name:} record in {@code player_timed.txt}, exactly
 * as the grammar read it and before any resolution to domain types. It is the hand-off between the
 * generated parser and {@link PlayerTimedAssembler}: every field is a {@code String} (or a list of
 * strings/sub-records) so that parsing stays lossless and all validation — enum look-ups, integer
 * parsing, brand/slay resolution, the "1-character means dummy" grade rule — happens in one place,
 * the assembler, where errors can be collected and reported per record.
 *
 * <p>Fields that a record may omit are captured as the empty string (or an empty list); the two
 * effect fields are captured as {@code null} when their directive is absent. Directives that may
 * legitimately repeat ({@code grade:}, {@code fail:}, {@code flags:}) are lists; {@code brand:} and
 * {@code slay:} are single strings because a timed effect grants at most one of each (C's single
 * {@code temp_brand}/{@code temp_slay} index).
 *
 * @param name             the {@code name:} token — a {@code TMD_*} identifier, still unprefixed
 * @param description      the {@code desc:} free text
 * @param grades           the ordered {@code grade:} lines (see {@link PlayerTimedGradeParseRecord})
 * @param onEnd            the {@code on-end:} message, or empty
 * @param onIncrease       the {@code on-increase:} message, or empty
 * @param onDecrease       the {@code on-decrease:} message, or empty
 * @param msgT             the {@code msgt:} message-type token, or empty
 * @param fail             the {@code fail:} conditions, one per line (see {@link FailureParseRecord})
 * @param onBeginEffect    the {@code on-begin-effect:} block as a raw {@link EffectParseRecord}, or null
 * @param onEndEffect      the {@code on-end-effect:} block as a raw {@link EffectParseRecord}, or null
 * @param resist           the {@code resist:} element token, or empty
 * @param brand            the {@code brand:} code, or empty (at most one)
 * @param slay             the {@code slay:} code, or empty (at most one)
 * @param flagSynonymFlag  the object-property code from {@code flag-synonym:}, or empty
 * @param flagSynonymValue the {@code flag-synonym:} exactness flag ("0"/non-"0"), or empty
 * @param lowerBound       the {@code lower-bound:} value, or empty
 * @param flags            the accumulated {@code flags:} tokens (e.g. {@code NONSTACKING})
 * @param line             the source line the record opened on, for error messages
 * @author Rowan Crowther
 */
public record PlayerTimedParseRecord(String name,
                                     String description,
                                     List<PlayerTimedGradeParseRecord> grades,
                                     String onEnd,
                                     String onIncrease,
                                     String onDecrease,
                                     String msgT,
                                     List<FailureParseRecord> fail,
                                     EffectParseRecord onBeginEffect,
                                     EffectParseRecord onEndEffect,
                                     String resist,
                                     String brand,
                                     String slay,
                                     String flagSynonymFlag,
                                     String flagSynonymValue,
                                     String lowerBound,
                                     List<String> flags,
                                     int line) {

    /**
     * One {@code grade:} line, still textual. A grade is
     * {@code colour:max:status:up-message[:down-message]}; the down-message is optional and may be
     * a single character (the "dummy" sentinel the assembler collapses away). The assembler resolves
     * {@code colour} to a {@link uk.co.jackoftrades.frontend.colour.enums.ColourType} and parses
     * {@code max} to an int.
     *
     * @param colour  the one-letter code or full colour name
     * @param max     the inclusive upper bound of this grade's range
     * @param status  the status-line label (single character = suppress)
     * @param msgDown the message shown on falling into this grade, or empty/dummy
     * @param msgUp   the message shown on rising into this grade, or single-character dummy
     * @author Rowan Crowther
     */
    public record PlayerTimedGradeParseRecord(String colour,
                                              String max,
                                              String status,
                                              String msgDown,
                                              String msgUp) {
    }

    /**
     * One {@code fail:} line, still textual — a condition under which the status fails to apply.
     * A record may carry several. {@code type} is the numeric selector ("1"–"5": object flag,
     * resist, vulnerability, player flag, timed effect) and {@code value} is the flag/element/effect
     * token the assembler resolves against the matching enum.
     *
     * @param type  the numeric condition selector, "1"–"5"
     * @param value the flag/element/effect token to resolve
     * @author Rowan Crowther
     */
    public record FailureParseRecord(String type,
                                     String value) {
    }
}
