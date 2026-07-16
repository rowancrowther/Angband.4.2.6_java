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

package uk.co.jackoftrades.backend.parser.artifact;

import java.util.List;
import java.util.Map;

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
