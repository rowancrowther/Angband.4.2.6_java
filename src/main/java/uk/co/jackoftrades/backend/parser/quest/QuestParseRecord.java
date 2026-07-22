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

package uk.co.jackoftrades.backend.parser.quest;

/**
 * The raw, still-textual form of a single {@code quest.txt} record, produced by
 * {@code QuestGrammar} and consumed by {@link QuestAssembler}.
 *
 * <p>Per the suite's "grammars extract, readers assemble" split, the grammar does no interpretation:
 * it copies each directive's value out verbatim as a {@code String} and leaves all conversion
 * (integer parsing, monster-race lookup) to the assembler. Keeping the values as raw text here means
 * a malformed field survives parsing intact and can be reported with its original spelling, rather
 * than being lost to an exception mid-parse.
 *
 * @param name   the quest's display name (C {@code quest.name}); starts a new record
 * @param level  the dungeon depth as raw text (C {@code quest.level}), parsed to an int downstream
 * @param race   the target monster's full name as written (C {@code quest.race}), resolved to a
 *               {@link uk.co.jackoftrades.middle.monsters.MonsterRace} downstream
 * @param number the required kill count as raw text (C {@code quest.max_num}), parsed downstream
 * @param line   the source line of this record's {@code name:} directive, carried so the assembler
 *               can point any soft error at the offending record
 * @author Rowan Crowther
 */
public record QuestParseRecord(String name,
                               String level,
                               String race,
                               String number,
                               int line) {
}
