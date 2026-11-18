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

package uk.co.jackoftradesltd.backend.parser.quest;

import uk.co.jackoftradesltd.backend.parser.grammars.Directive;

import java.util.List;

/**
 * The raw, still-textual form of a single {@code quest.txt} record, produced by
 * {@code QuestGrammar} and consumed by {@link QuestAssembler}.
 *
 * <p>A record is one {@code name:} line and the {@code level:}, {@code race:} and {@code number:} lines
 * that follow it, up to the next {@code name:}. C has no such grouping: its four hooks fill whichever
 * quest {@code parse_quest_name} started last, and the grammar's {@code quest} rule recreates that
 * grouping so that the assembler gets one record per quest.
 *
 * <p>Per the suite's "grammars extract, readers assemble" split, the grammar does no interpretation:
 * it copies each directive's value out verbatim as a {@code String} inside a {@link Directive} and
 * leaves all conversion (integer parsing, monster-race lookup) to the assembler. Keeping the values as
 * raw text here means a malformed field survives parsing intact and can be reported with its original
 * spelling, rather than being lost to an exception mid-parse.
 *
 * <p>The {@code directives} are in file order. The first is always {@code name}; the rest are any mix of
 * {@code level}, {@code race} and {@code number}, each of which may repeat or be absent, as C allows.
 * Nothing in the record enforces that: a hand-built one can start with any directive, which is why
 * {@link QuestAssembler} checks for a name itself.
 *
 * <p>Record QuestParseRecord coded before 261105, commented in full on 261118 (directive list and
 * grouping described; earlier update 261105 for the removal of the soft-error path).
 *
 * @param directives the record's directives in file order, each carrying its raw value and its line
 * @author Rowan Crowther
 */
public record QuestParseRecord(List<Directive> directives) {
}
