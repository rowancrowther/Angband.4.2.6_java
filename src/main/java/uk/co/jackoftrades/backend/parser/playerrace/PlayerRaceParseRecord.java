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

package uk.co.jackoftrades.backend.parser.playerrace;

import java.util.List;
import java.util.Map;

public record PlayerRaceParseRecord(String name,
                                    Map<String, String> stats,
                                    Map<String, String> playerSkills,
                                    String hitdie,
                                    String exp,
                                    String infraVision,
                                    String history,
                                    String ageBase, String ageModifier,
                                    String baseHeight, String heightModifier,
                                    String baseWeight, String weightModifier,
                                    List<String> objectFlags,
                                    List<String> playerFlags,
                                    Map<String, String> values,
                                    int line) {
}
