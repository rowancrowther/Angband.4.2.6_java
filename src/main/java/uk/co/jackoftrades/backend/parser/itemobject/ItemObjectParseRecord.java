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

package uk.co.jackoftrades.backend.parser.itemobject;

import uk.co.jackoftrades.backend.parser.grammars.EffectParseRecord;

import java.util.List;
import java.util.Map;

/**
 * The raw, un-interpreted capture of one {@code object.txt} record, produced by
 * {@code ItemObjectGrammar} and consumed by {@link ItemObjectAssembler}.
 *
 * <p>Following the suite-wide parse-record convention, every field is a raw {@link String}
 * (or list/map of them): no enum resolution, integer/dice parsing or cross-field validation
 * happens here — that is all deferred to the assembler, which turns this into an
 * {@link uk.co.jackoftrades.middle.objects.ObjectKind} (C's {@code struct object_kind}). Absent
 * directives arrive as empty strings / empty collections (the grammar seeds them), so the only
 * required field is {@link #name}. The rand-bearing fields ({@code attack*}, {@code armourToa},
 * {@code charges}, {@code pileNumber}, {@code pVal}) are captured as dice strings for the assembler
 * to resolve into {@link uk.co.jackoftrades.backend.numerics.Random}s.
 *
 * @param name             the kind's display name (the record header; the only required field)
 * @param glyph            display glyph character from {@code graphics:}
 * @param colour           display colour (code or name) from {@code graphics:}
 * @param tValue           the base type name (tval) from {@code type:}
 * @param level            raw native level, or {@code ""}
 * @param weight           raw weight, or {@code ""}
 * @param cost             raw cost, or {@code ""}
 * @param attackBaseDamage raw base-damage dice ({@code hd}) from {@code attack:}
 * @param attackToh        raw to-hit dice from {@code attack:}
 * @param attackTod        raw to-damage dice from {@code attack:}
 * @param armourBaseAC     raw base AC (int) from {@code armor:}
 * @param armourToa        raw to-AC dice from {@code armor:}
 * @param allocCommon      raw allocation commonness from {@code alloc:}
 * @param allocLower       raw allocation min depth from {@code alloc:}
 * @param allocUpper       raw allocation max depth from {@code alloc:}
 * @param charges          raw charges dice from {@code charges:}
 * @param pileChance       raw pile probability (int) from {@code pile:}
 * @param pileNumber       raw pile stack-size dice from {@code pile:}
 * @param power            raw power rating from {@code power:}
 * @param message          accumulated {@code msg:} text (object-level effect message)
 * @param visMessage       accumulated {@code vis-msg:} text (object-level seen message)
 * @param effects          one raw {@link EffectParseRecord} per effect block
 * @param flags            flag tokens from every {@code flags:} line, unprefixed
 * @param values           {@code values:} entries as token -> dice-string (obj_mods and
 *                         {@code RES_}-prefixed elements; the assembler splits the two)
 * @param brand            brand codes from every {@code brand:} line
 * @param slay             slay codes from every {@code slay:} line
 * @param curse            {@code curse:} entries as name -> power-string
 * @param pVal             raw extra-parameter dice from {@code pval:}
 * @param desc             accumulated {@code desc:} text
 * @param line             1-based source line of the {@code name:} header, for error reporting
 * @author Rowan Crowther
 */
public record ItemObjectParseRecord(
        String name,
        String glyph,
        String colour,
        String tValue,
        String level,
        String weight,
        String cost,
        String attackBaseDamage,
        String attackToh,
        String attackTod,
        String armourBaseAC,
        String armourToa,
        String allocCommon,
        String allocLower,
        String allocUpper,
        String charges,
        String pileChance,
        String pileNumber,
        String power,
        String message,
        String visMessage,
        List<EffectParseRecord> effects,
        List<String> flags,
        Map<String, String> values,
        List<String> brand,
        List<String> slay,
        Map<String, String> curse,
        String pVal,
        String desc,
        int line
) {
}
