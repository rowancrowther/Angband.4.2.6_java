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

package uk.co.jackoftradesltd.backend.parser.playerrace;

import java.util.List;
import java.util.Map;

/**
 * Immutable extraction record for one {@code p_race.txt} entry: the raw, still-unresolved
 * fields parsed by the grammar, later turned into the {@code PlayerRace} domain type by
 * {@link PlayerRaceAssembler}.
 *
 * @param name            the race's display name, e.g. {@code "Half-Troll"}
 * @param stats           per-stat adjustments, keyed by {@code STAT_*} name, values as raw text
 * @param playerSkills    per-skill adjustments, keyed by {@code SKILL_*} name, values as raw text
 * @param hitdie          the race's hit-die contribution, as raw text
 * @param exp             the race's experience-penalty percentage, as raw text
 * @param infraVision     the race's infravision range, as raw text
 * @param history         the {@code history:} chart index, as raw text (resolved to a
 *                        {@code PlayerHistoryChart} by the assembler)
 * @param ageBase         the base starting age, as raw text
 * @param ageModifier     the random age modifier, as raw text
 * @param baseHeight      the base height, as raw text
 * @param heightModifier  the random height modifier, as raw text
 * @param baseWeight      the base weight, as raw text
 * @param weightModifier  the random weight modifier, as raw text
 * @param objectFlags     object-property flag names (unprefixed), to be resolved to {@code ObjectFlag}s
 * @param playerFlags     player-property flag names (unprefixed), to be resolved to {@code PlayerFlag}s
 * @param values          per-element resistance values, keyed by {@code RES_*} name, values as raw text
 * @param line            the 1-based source line, retained so soft errors can point back at the file
 * @author Rowan Crowther
 */
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
