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

package uk.co.jackoftrades.backend.parser.trap;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * The raw, pre-assembly capture of one {@code trap.txt} record — every field a {@link String} (or
 * list of strings) exactly as the grammar read it, before {@code TrapAssembler} resolves colours,
 * flags, dice and effects into a {@link uk.co.jackoftrades.middle.cave.TrapKind}. The parse layer
 * deliberately does no interpretation.
 *
 * <p>Two subtleties are baked into the field wiring (matching C's {@code parse_trap_name}/
 * {@code parse_trap_desc}): the {@code name:} line's second field is captured as {@link #description}
 * (C {@code desc}, the {@code lookupTrap} key) while the {@code desc:} directive is captured as
 * {@link #text} (C {@code text}); and {@link #index} is vestigial — there is no {@code index:}
 * directive, so it is always {@code "0"} and the assembler assigns the real {@code trapKindIndex}
 * by file position instead.
 *
 * @param name        the {@code name:} line's first field — the (non-unique) grouping name
 * @param text        the {@code desc:} directive's flavour text (C {@code text})
 * @param description the {@code name:} line's second field — the short description / lookup key
 * @param message     the {@code msg:} on-trigger message
 * @param saveMessage the {@code msg-good:} message (shown on a successful save)
 * @param failMessage the {@code msg-bad:} message (shown on a failed save)
 * @param xtraMessage the {@code msg-xtra:} message (shown when the extra effect fires)
 * @param index       vestigial — always {@code "0"} (see above)
 * @param glyph       the display glyph from {@code graphics:}
 * @param colour      the raw colour code or name from {@code graphics:}
 * @param rarity      the {@code appear:} line's first field
 * @param minDepth    the {@code appear:} line's second field (minimum depth)
 * @param maxNum      the {@code appear:} line's third field (max number on a level)
 * @param power       the {@code visibility:} dice string
 * @param flags       raw {@code flags:} names (resolved to {@code TRF_} trap flags by the assembler)
 * @param saveFlags   raw {@code save:} names (resolved to {@code OF_} object flags)
 * @param effects     the primary effect blocks
 * @param xtraEffects the secondary ("extra") effect blocks
 * @param line        the 1-based source line where this record's {@code name:} starts
 * @author Rowan Crowther
 */
public record TrapParseRecord(String name,
                              String text,
                              String description,
                              String message,
                              String saveMessage,
                              String failMessage,
                              String xtraMessage,
                              String index,
                              String glyph,
                              String colour,
                              String rarity,
                              String minDepth,
                              String maxNum,
                              String power,
                              List<String> flags,
                              List<String> saveFlags,
                              List<EffectParseRecord> effects,
                              List<EffectParseRecord> xtraEffects,
                              int line) {
}
