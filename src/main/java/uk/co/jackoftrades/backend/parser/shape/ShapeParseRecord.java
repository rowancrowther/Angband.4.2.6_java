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

package uk.co.jackoftrades.backend.parser.shape;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;
import java.util.Map;

/**
 * The raw, un-interpreted capture of one {@code shape.txt} record, produced by
 * {@code ShapeGrammar} and consumed by {@link ShapeAssembler}.
 *
 * <p>Following the suite-wide parse-record convention, every field is a raw {@link String}
 * (or list/map of them): no enum resolution, integer parsing or cross-field validation happens
 * here — that is all deferred to the assembler, which turns this into a
 * {@link uk.co.jackoftrades.middle.player.PlayerShape}. Optional directives that were absent
 * arrive as empty strings / empty collections rather than {@code null} (the grammar seeds them),
 * so the only required field is {@link #name}.
 *
 * <p>The repeatable directives ({@code obj-flags}, {@code player-flags}, {@code values},
 * {@code blow}, and each effect block) are already flattened into their respective collections by
 * the grammar's per-shape accumulation, so a record carries the union of every such line.
 *
 * @param name             the shape's display name (the record header; the only required field)
 * @param combatToh        raw to-hit modifier from the {@code combat:} line, or {@code ""}
 * @param combatTod        raw to-damage modifier from the {@code combat:} line, or {@code ""}
 * @param combatToa        raw to-armour modifier from the {@code combat:} line, or {@code ""}
 * @param skillDisarmPhys  raw {@code skill-disarm-phys:} value, or {@code ""}
 * @param skillDisarmMagic raw {@code skill-disarm-magic:} value, or {@code ""}
 * @param skillSave        raw {@code skill-save:} value, or {@code ""}
 * @param skillStealth     raw {@code skill-stealth:} value, or {@code ""}
 * @param skillSearch      raw {@code skill-search:} value, or {@code ""}
 * @param skillMelee       raw {@code skill-melee:} value, or {@code ""}
 * @param skillThrow       raw {@code skill-throw:} value, or {@code ""}
 * @param skillDig         raw {@code skill-dig:} value, or {@code ""}
 * @param oFlags           object-flag tokens from every {@code obj-flags:} line, unprefixed
 * @param pFlags           player-flag tokens from every {@code player-flags:} line, unprefixed
 * @param values           {@code values:} entries as token -> int-string (both {@code obj_mods}
 *                         and {@code RES_}-prefixed elements; the assembler splits the two)
 * @param effects          one raw {@link EffectParseRecord} per effect block on the shape
 * @param blowMethod       every {@code blow:} verb in order, duplicates preserved (they weight
 *                         selection frequency)
 * @param line             1-based source line of the {@code name:} header, for error reporting
 * @author Rowan Crowther
 */
public record ShapeParseRecord(String name,
                               String combatToh,
                               String combatTod,
                               String combatToa,
                               String skillDisarmPhys,
                               String skillDisarmMagic,
                               String skillSave,
                               String skillStealth,
                               String skillSearch,
                               String skillMelee,
                               String skillThrow,
                               String skillDig,
                               List<String> oFlags,
                               List<String> pFlags,
                               Map<String, String> values,
                               List<EffectParseRecord> effects,
                               List<String> blowMethod,
                               int line) {
}
