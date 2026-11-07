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

package uk.co.jackoftradesltd.backend.parser.itemobject;

import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.middle.numerics.Random;

import java.util.List;
import java.util.Map;

/**
 * The raw, un-interpreted capture of one {@code object.txt} record, produced by
 * {@code ItemObjectGrammar} and consumed by {@link ItemObjectAssembler}.
 *
 * <p>Following the suite-wide parse-record convention, every field is a raw {@link String}
 * (or list/map of them): no enum resolution, integer/dice parsing or cross-field validation
 * happens here — that is all deferred to the assembler, which turns this into an
 * {@link uk.co.jackoftradesltd.middle.objects.ObjectKind} (C's {@code struct object_kind}). Absent
 * directives arrive as empty strings / empty collections (the grammar seeds them), so the only
 * required field is {@link #name}. The one exception to the empty-string rule is the accumulated
 * text: {@code msg:}, {@code vis-msg:} and {@code desc:} arrive as {@code null} when no such line
 * was given, matching C's zero-filled {@code effect_msg}, {@code vis_msg} and {@code text}
 * pointers. The rand-bearing fields ({@code attack*}, {@code armourToa},
 * {@code charges}, {@code pileNumber}, {@code pVal}) are captured as dice strings for the assembler
 * to resolve into {@link Random}s.
 *
 * <p>Every directive that {@code init_parse_object} in {@code obj-init.c} registers has a home
 * here: {@code effect}, {@code effect-yx}, {@code dice} and {@code expr} all fold into one
 * {@link EffectParseRecord} per effect block, and every other directive maps to the component
 * named in its {@code @param} line. C's {@code struct object_kind} fields that are not read
 * straight from a directive have no component: {@code sval} is derived in
 * {@code parse_object_type} by counting the kinds already seen under that tval, {@code kidx}
 * is assigned in {@code finish_parse_object}, and {@code flavor} and the savefile-only
 * {@code aware}/{@code tried}/{@code ignore} are set elsewhere.
 *
 * <p>Being a record, this type has no behaviour of its own, so there is no C function to compare
 * line by line: C's per-directive "last one wins" (scalars) and "append" ({@code msg:},
 * {@code vis-msg:}, {@code desc:}) rules, its set semantics for {@code brand:}/{@code slay:} and its
 * {@code power > 0} filter on {@code curse:} are all applied by whatever fills or consumes the
 * record, not here.
 *
 * <p>Class ItemObjectParseRecord coded before 261107, commented in full on 261107.
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
 * @param effects          one raw {@link EffectParseRecord} per effect block (C's {@code effect:}
 *                         line plus any {@code effect-yx:}, {@code dice:} and {@code expr:} lines that
 *                         follow it)
 * @param flags            flag tokens from every {@code flags:} line, unprefixed
 * @param values           {@code values:} entries as token -> dice-string (obj_mods and
 *                         {@code RES_}-prefixed elements; the assembler splits the two)
 * @param brand            brand codes from every {@code brand:} line, in file order and unchecked
 *                         (C stores them as a set, so a repeated code is harmless there)
 * @param slay             slay codes from every {@code slay:} line, in file order and unchecked
 *                         (C stores them as a set, so a repeated code is harmless there)
 * @param curse            one {@link FullCurseDataParseRecord} per {@code curse:} line, holding the
 *                         raw name and power strings; C's directive carries only a name and a power,
 *                         so the record's {@code timeout} component has no counterpart in
 *                         {@code object.txt} (the grammar fills it with the literal {@code "0"})
 * @param pVal             raw extra-parameter dice from {@code pval:}
 * @param desc             accumulated {@code desc:} text
 * @param time             raw dice string from the record's own {@code time:} line (C's
 *                         {@code kind->time}), or {@code ""} if absent; the assembler parses it
 *                         into the kind's {@link Random}, zero when empty. It belongs to the record,
 *                         not to the last {@link EffectParseRecord}
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
        List<FullCurseDataParseRecord> curse,
        String pVal,
        String desc,
        String time,
        int line) {
}
