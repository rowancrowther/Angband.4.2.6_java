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

package uk.co.jackoftradesltd.backend.parser.chesttrap;

import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

import java.util.List;

/**
 * Immutable extraction record for one {@code chest_trap.txt} record, as captured by
 * {@code ChestTrapGrammar}: the raw, still-unresolved fields, later turned into a
 * {@link uk.co.jackoftradesltd.middle.objects.ChestTrap} by {@link ChestTrapAssembler}.
 *
 * <p>Every value that is an integer, a boolean or an enum in the domain type is carried here as a
 * {@code String}, so that a malformed value survives parsing and can be reported against its line
 * rather than failing at token level. Two conventions follow from that, and the assembler relies on
 * both: a directive that was <em>absent</em> arrives as {@code ""} (the grammar's {@code @init}
 * defaults), while a directive that was <em>present but malformed</em> arrives as {@code null},
 * because ANTLR's error recovery leaves the sub-rule's return unset.
 *
 * <p>There is no {@code pval} component. C synthesises that field while parsing, one bit per record
 * in file order ({@code obj-chest.c:64-72}); here it belongs to
 * {@link uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode}, so nothing at parse time needs it.
 *
 * @param name     the trap's display name; not unique across records
 * @param code     the trap's identifier, resolved to a {@code ChestTrapCode} by the assembler
 * @param level    the minimum chest level this trap can appear on, unparsed
 * @param effect   the trap's effect blocks in file order; may be empty, and may hold several
 * @param destroy  {@code "1"} if springing the trap destroys the chest's contents
 * @param magic    {@code "1"} if the trap is magical rather than physical
 * @param msg      the message shown when the trap fires
 * @param msgDeath the message shown if the trap kills the character
 * @param line     the line the record's {@code name:} was found on, for error reporting
 * @author Rowan Crowther
 */
public record ChestTrapParseRecord(String name,
                                   String code,
                                   String level,
                                   List<EffectParseRecord> effect,
                                   String destroy,
                                   String magic,
                                   String msg,
                                   String msgDeath,
                                   int line) {
}
