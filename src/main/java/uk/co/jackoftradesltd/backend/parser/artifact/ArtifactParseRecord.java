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

import java.util.List;
import java.util.Map;

/**
 * Immutable extraction record for one {@code artifact.txt} entry: the raw, still-unresolved
 * fields parsed by the grammar, later turned into the {@code Artifact} domain type by
 * {@link ArtifactAssembler}.
 *
 * @param name        the artifact's display name
 * @param tValue      the base object type's tvalue name, resolved to a
 *                    {@link uk.co.jackoftradesltd.middle.objects.enums.TValue} by the assembler
 * @param sValue      the base object type's sub-value name
 * @param glyph       the display glyph override, or {@code ""} if none is given
 * @param colour      the display colour paired with {@code glyph}
 * @param level       the artifact's native depth, unparsed
 * @param weight      the artifact's weight, unparsed
 * @param cost        the artifact's base cost, unparsed
 * @param commonness  the allocation commonness weight, unparsed
 * @param min         the allocation minimum depth, unparsed
 * @param max         the allocation maximum depth, unparsed
 * @param baseDamage  the base weapon/ammo damage dice string, stored verbatim
 * @param toh         the combat to-hit bonus, unparsed
 * @param tod         the combat to-damage bonus, unparsed
 * @param baseAC      the base armour class, unparsed
 * @param toa         the combat to-AC bonus, unparsed
 * @param flagList    the object flag names in file order (bare, no {@code OF_} prefix)
 * @param activation  the activation's name, or {@code ""} if the artifact has none
 * @param time        the activation recharge-time dice string, or {@code ""} if none
 * @param msg         the message shown when the activation fires
 * @param values      the {@code values:} directive as label/value text pairs; a
 *                    {@code RES_}-prefixed key is an element resistance level, everything
 *                    else an object modifier
 * @param brand       the brand codes this artifact carries, resolved by registry lookup
 * @param slay        the slay codes this artifact carries, resolved by registry lookup
 * @param curse       the curse names mapped to their raw power text
 * @param desc        the artifact's flavour/description text
 * @param line        the source line the record begins on, for error reporting
 * @author Rowan Crowther
 */
public record ArtifactParseRecord(String name,
                                  String tValue,
                                  String sValue,
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
                                  Map<String, String> curse,
                                  String desc,
                                  int line) {
}
