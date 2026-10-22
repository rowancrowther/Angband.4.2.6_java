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

package uk.co.jackoftradesltd.backend.parser.activation;

import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * The raw, still-textual result of parsing a single {@code activation.txt} block,
 * as produced by the grammar and handed to {@link ActivationAssembler} for
 * resolution into a domain {@link uk.co.jackoftradesltd.middle.Activation}.
 * <p>
 * Every scalar field is kept as its original {@link String} rather than being
 * parsed here. An absent {@code aim:}, {@code level:} or {@code power:} line is
 * left empty, but an absent {@code msg:} or {@code desc:} line is {@code null}, as
 * C leaves those two pointers NULL. Interpretation (integer conversion, effect
 * resolution, error reporting) is deliberately deferred to the assembler, which stops the
 * load with the file and the line this record starts on if a value cannot be used. C does
 * that work inside the {@code parse_act_*} handlers in {@code obj-init.c} as it reads each
 * line; the {@code activation} struct is only filled in once the handlers have converted
 * the text, and {@code finish_parse_act} just counts, copies and numbers the records.
 *
 * <p>Class ActivationParseRecord coded before 260815, commented in full on 261022; absent
 * {@code msg:} and {@code desc:} documented as {@code null} on 261020.
 *
 * @param name    the activation's name (the {@code name:} line)
 * @param aim     the raw {@code aim:} text, or empty if the line is absent; any non-zero number
 *                means the power must be aimed, as C reads it
 * @param level   the raw {@code level:} integer, or empty if the line is absent
 * @param power   the raw {@code power:} integer, or empty if the line is absent
 * @param message the {@code msg:} text shown when the activation fires; repeated lines are
 *                joined with no separator, as C's {@code string_append} does, and it is
 *                {@code null} if the line is absent
 * @param desc    the {@code desc:} human-readable description; repeated lines are joined
 *                with no separator, and it is {@code null} if the line is absent
 * @param effects the parsed-but-unresolved effect blocks this activation runs
 * @param line    the source line the block begins on, for error messages
 * @author Rowan Crowther
 */
public record ActivationParseRecord(String name, String aim, String level,
                                    String power, String message, String desc,
                                    List<EffectParseRecord> effects, int line) {
}
