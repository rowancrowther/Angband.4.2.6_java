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

package uk.co.jackoftradesltd.backend.parser.artifact;

import uk.co.jackoftradesltd.backend.parser.curse.FullCurseDataParseRecord;

import java.util.List;
import java.util.Map;

/**
 * One {@code artifact.txt} entry as the grammar delivers it: the text of every directive, kept
 * raw and unresolved, for {@link ArtifactAssembler} to turn into the {@code Artifact} domain type.
 * It holds what C's {@code parse_artifact_*} handlers in {@code obj-init.c} read from the file
 * before they write it into {@code struct artifact} ({@code object.h}); nothing is looked up,
 * range-checked or converted here.
 *
 * <p>The grammar takes the directives in any order, as C does, and keeps only each directive's
 * values, not the order they came in. A directive that is absent leaves its component at the
 * grammar's default, which stands for the zero or {@code NULL} that C's zero-filled
 * {@code struct artifact} would hold: a string component is {@code ""} (the numeric ones parse to
 * 0), a list component is empty and {@code values} is an empty map. The two exceptions are
 * {@code desc}, which is {@code null} when no {@code desc:} line came, as C's {@code text} starts
 * {@code NULL}, and {@code msg}, which is {@code ""} where C's {@code alt_msg} is {@code NULL}.
 * {@code msg:} and {@code desc:} lines are joined in file order, as C's {@code string_append}
 * does. A repeated {@code values:} label keeps the last figure, as C's later write replaces the
 * earlier. Repeated {@code flags:} lines add to {@code flagList}, as C's flag bits accumulate.
 *
 * <p>The one place order is kept is the {@code base-object:} lines. C's
 * {@code write_dummy_object_record} copies the artifact's level at the moment it reads
 * {@code base-object:}, so each {@link ArtifactBaseObjectParseRecord} carries the {@code level:}
 * text in force at its own line, and the assembler gives a new special kind that level. A record
 * with two {@code base-object:} lines is an accepted deviation: C sends each later weight, cost,
 * activation, time and glyph write to whichever kind was current at that line, and this record
 * cannot say which that was, so {@link ArtifactAssembler} sends them all to the last one.
 *
 * <p>The record is shallowly immutable: the components cannot be reassigned, but the lists and the
 * map are the grammar's own {@code ArrayList} and {@code HashMap}, not copied or frozen, so the
 * {@code values} map does not keep file order.
 *
 * <p>Record ArtifactParseRecord coded before 260915, commented in full on 261107.
 *
 * @param name        the artifact's display name, from {@code name:}
 * @param baseObjects the {@code base-object:} lines in file order, each with the {@code level:}
 *                    text that had been read when its line was reached; empty if the record has
 *                    none, which the assembler refuses
 * @param glyph       the {@code graphics:} glyph character, or {@code ""} if none is given
 * @param colour      the {@code graphics:} colour, as a one-letter code or a colour name, paired
 *                    with {@code glyph}; {@code ""} if none is given
 * @param level       the {@code level:} figure as text, or {@code ""} if absent
 * @param weight      the {@code weight:} figure as text, or {@code ""} if absent
 * @param cost        the {@code cost:} figure as text, or {@code ""} if absent
 * @param commonness  the {@code alloc:} commonness (C's {@code alloc_prob}) as text, or {@code ""}
 *                    if absent
 * @param min         the {@code alloc:} minimum depth as text, or {@code ""} if absent
 * @param max         the {@code alloc:} maximum depth as text, or {@code ""} if absent
 * @param baseDamage  the {@code attack:} dice string, such as {@code 2d5}, stored verbatim;
 *                    {@code ""} if absent
 * @param toh         the {@code attack:} to-hit bonus as text, or {@code ""} if absent
 * @param tod         the {@code attack:} to-damage bonus as text, or {@code ""} if absent
 * @param baseAC      the {@code armor:} base armour class as text, or {@code ""} if absent
 * @param toa         the {@code armor:} to-AC bonus as text, or {@code ""} if absent
 * @param flagList    every name on the {@code flags:} lines in file order: object flags bare,
 *                    without the {@code OF_} prefix, and the element flags {@code IGNORE_} and
 *                    {@code HATES_}, which the assembler tells apart; empty if there are none
 * @param activation  the {@code act:} activation name, or {@code ""} if the artifact has none
 * @param time        the {@code time:} recharge dice string, or {@code ""} if none
 * @param msg         the {@code msg:} lines joined in file order, shown when the activation
 *                    fires; {@code ""} if there are none
 * @param values      the {@code values:} directive as label/value text pairs; a
 *                    {@code RES_}-prefixed key is an element resistance level, everything
 *                    else an object modifier
 * @param brand       the {@code brand:} codes in file order, as written; the assembler resolves
 *                    each against the brand registry
 * @param slay        the {@code slay:} codes in file order, as written; the assembler resolves
 *                    each against the slay registry
 * @param curse       the {@code curse:} lines in file order, each a curse name with its raw
 *                    power text; the assembler keeps a curse only if its power is above 0
 * @param desc        the {@code desc:} lines joined in file order, or {@code null} if there are
 *                    none
 * @param line        the source line the record's {@code name:} directive is on, for error
 *                    reporting
 * @author Rowan Crowther
 */
public record ArtifactParseRecord(String name,
                                  List<ArtifactBaseObjectParseRecord> baseObjects,
                                  String glyph,
                                  String colour,
                                  String level,
                                  String weight,
                                  String cost,
                                  String commonness,
                                  String min,
                                  String max,
                                  String baseDamage,
                                  String toh,
                                  String tod,
                                  String baseAC,
                                  String toa,
                                  List<String> flagList,
                                  String activation,
                                  String time,
                                  String msg,
                                  Map<String, String> values,
                                  List<String> brand,
                                  List<String> slay,
                                  List<FullCurseDataParseRecord> curse,
                                  String desc,
                                  int line) {

    /**
     * One {@code base-object:} line of an artifact record: the type and sub-type names C's
     * {@code parse_artifact_base_object} reads with {@code tval_find_idx} and {@code lookup_sval},
     * plus the level in force when the line was reached.
     *
     * <p>The level is kept because {@code write_dummy_object_record} copies the artifact's level
     * at the moment it makes a special kind, so a {@code level:} line after {@code base-object:}
     * does not reach that kind. Every shipped record puts {@code level:} after
     * {@code base-object:}, so the level here is {@code ""} and every special kind is level 0.
     *
     * <p>Record ArtifactBaseObjectParseRecord coded before 261023, commented in full on 261107.
     *
     * @param tValue the type name, such as {@code light}, resolved by the assembler
     * @param sValue the sub-type name, such as {@code Phial}, resolved by the assembler; an
     *               unknown name makes a new special kind, as in C
     * @param level  the {@code level:} text read before this line, or {@code ""} if none had been
     */
    public record ArtifactBaseObjectParseRecord(String tValue,
                                                String sValue,
                                                String level) {
        
    }
}
