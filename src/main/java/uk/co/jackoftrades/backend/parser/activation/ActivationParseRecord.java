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

package uk.co.jackoftrades.backend.parser.activation;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * The raw, still-textual result of parsing a single {@code activation.txt} block,
 * as produced by the grammar and handed to {@link ActivationAssembler} for
 * resolution into a domain {@link uk.co.jackoftrades.middle.Activation}.
 * <p>
 * Every scalar field is kept as its original {@link String} (and left empty when
 * the corresponding line is absent) rather than being parsed here: interpretation
 * — integer conversion, effect resolution, error reporting — is deliberately
 * deferred to the assembler so that a malformed value produces a soft, per-record
 * error instead of aborting the parse. This mirrors the split in the C loader
 * between {@code parse_act_*} (which only stashes strings on the {@code activation}
 * struct) and the later finalisation pass.
 *
 * @param name    the activation's name (the {@code name:} line)
 * @param aim     the raw {@code aim:} flag; {@code "1"} means the power must be aimed
 * @param level   the raw {@code level:} integer, or empty if the line is absent
 * @param power   the raw {@code power:} integer, or empty if the line is absent
 * @param message the {@code msg:} text shown when the activation fires
 * @param desc    the {@code desc:} human-readable description
 * @param effects the parsed-but-unresolved effect blocks this activation runs
 * @param line    the source line the block begins on, for error messages
 * @author Rowan Crowther
 */
public record ActivationParseRecord(String name, String aim, String level,
                                    String power, String message, String desc,
                                    List<EffectParseRecord> effects, int line) {
}
