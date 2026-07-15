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

package uk.co.jackoftrades.backend.parser.playerclass;

import java.util.List;
import java.util.Map;

/**
 * The raw, unvalidated capture of one whole class entry from {@code class.txt} — the root DTO the
 * grammar hands to {@link PlayerClassAssembler} to become a
 * {@link uk.co.jackoftrades.middle.player.PlayerClass}. Every scalar is held as verbatim file text
 * and every collection is keyed by the file's own token names (e.g. {@code "STAT_STR"},
 * {@code "SKILL_TO_HIT_MELEE"}); resolving those to enums, parsing the integers and folding in the
 * cross-file references all happen in the assembler, so a bad value drops one class rather than
 * failing the file.
 *
 * <p>The {@code skill-*} lines each carry a base and an increment: the base goes into
 * {@link #baseSkills} and the per-level increment into {@link #skillIncrements}, under the same key.
 *
 * @param name            the class's display name, e.g. {@code "Ranger"}
 * @param stats           per-stat adjustments, keyed by {@code STAT_*} name, values as raw text
 * @param baseSkills      starting skill levels, keyed by {@code SKILL_*} name, values as raw text
 * @param skillIncrements per-ten-level skill growth, keyed by {@code SKILL_*} name, values as raw text
 * @param hitdie          the class's hit-die contribution, as raw text
 * @param exp             the class's experience-penalty percentage, as raw text
 * @param maxAttacks      the maximum blows per round, as raw text
 * @param minWeight       the no-penalty weapon-weight threshold (tenths of a pound), as raw text
 * @param strengthMult    the strength multiplier in the blows formula, as raw text
 * @param equipment       the starting-equipment entries (from both class-level and in-book {@code equip:} lines)
 * @param oFlags          object-property flag names (unprefixed), to be resolved to {@code ObjectFlag}s
 * @param pFlags          player-property flag names (unprefixed), to be resolved to {@code PlayerFlag}s
 * @param titles          the per-level class titles, in file order
 * @param magic           the caster profile, or {@code null} for a non-caster class
 * @param line            the 1-based source line of the {@code name:} line, for error reporting
 * @author Rowan Crowther
 */
public record PlayerClassParseRecord(String name,
                                     Map<String, String> stats,
                                     Map<String, String> baseSkills,
                                     Map<String, String> skillIncrements,
                                     String hitdie,
                                     String exp,
                                     String maxAttacks,
                                     String minWeight,
                                     String strengthMult,
                                     List<ClassEquipParseRecord> equipment,
                                     List<String> oFlags,
                                     List<String> pFlags,
                                     List<String> titles,
                                     ClassMagicParseRecord magic,
                                     int line) {
}