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

package uk.co.jackoftradesltd.backend.parser.curse;

import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;

import java.util.List;
import java.util.Map;

/**
 * Immutable extraction record for one {@code curse.txt} entry: the raw, still-unresolved fields
 * parsed by the grammar, later turned into the {@code Curse} domain type by
 * {@link CurseAssembler}.
 *
 * @param name             the curse's name
 * @param type             the object base types this curse can appear on, by tvalue name
 * @param weightAdjustment the weight adjustment applied to a cursed item, unparsed; {@code ""}
 *                         if the line is absent (no adjustment)
 * @param combatToH        the combat to-hit adjustment, unparsed
 * @param combatToD        the combat to-damage adjustment, unparsed
 * @param combatToA        the combat to-AC adjustment, unparsed
 * @param effects          the curse's effect blocks in file order
 * @param flags            the {@code flags:} directive's raw tokens; a {@code HATES_}/
 *                         {@code IGNORE_}-prefixed token sets an element flag, everything else
 *                         is an object flag
 * @param values           the {@code values:} directive as label/value text pairs; a
 *                         {@code RES_}-prefixed key is an element resistance level, everything
 *                         else an additive object modifier
 * @param message          the message shown when the curse triggers
 * @param desc             the curse's description lines, joined by the assembler
 * @param conflict         the names of curses this one conflicts with, resolved to instances
 *                         by the assembler's second pass
 * @param cFlag            the object flags applied when this curse conflicts with another
 * @param line             the source line the record begins on, for error reporting
 * @author Rowan Crowther
 */
public record CurseParseRecord(String name,
                               List<String> type,
                               String weightAdjustment,
                               String combatToH,
                               String combatToD,
                               String combatToA,
                               List<EffectParseRecord> effects,
                               List<String> flags,
                               Map<String, String> values,
                               String message,
                               List<String> desc,
                               List<String> conflict,
                               List<String> cFlag,
                               int line) {
}
