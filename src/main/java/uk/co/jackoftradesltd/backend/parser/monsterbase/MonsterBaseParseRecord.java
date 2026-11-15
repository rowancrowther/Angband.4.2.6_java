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

package uk.co.jackoftradesltd.backend.parser.monsterbase;

import java.util.List;

/**
 * Immutable extraction record for one {@code monster_base.txt} entry: the raw, still-unresolved
 * fields parsed by the grammar, later turned into the {@code MonsterBase} domain type by
 * {@link MonsterBaseAssembler}.
 *
 * <p>{@code monster_base.txt} has no line for a displayed name separate from the description, and
 * C's {@code struct monster_base} has a single {@code text} member that {@code desc:} fills. The
 * grammar's {@code monsterBase} rule therefore passes the same {@code desc:} text as both
 * {@code inGameName} and {@code description}, so the two components are always equal.
 *
 * <p>After {@code name:} the other directives may come in any order, repeat and be left out, as in
 * C, where each {@code parse_mon_base_*} handler needs only the record header. A directive that is
 * left out arrives as its empty value: {@code ""} for the glyph and the pain, an empty list for the
 * flags, and {@code null} for the text, as C's {@code text} is NULL until a {@code desc:} line sets
 * it. {@link MonsterBaseAssembler} turns those into C's zeroed defaults: glyph 0, no pain set, no
 * flags, no text. A repeated {@code glyph:} or {@code pain:} keeps the last one, the
 * {@code flags:} lines accumulate into one list and the {@code desc:} lines are joined end to end,
 * wherever they sit in the record.
 *
 * <p>Record MonsterBaseParseRecord coded before 261010, commented in full on 261010, updated on
 * 261113 for the unordered, optional directives (Stage 7a) and the null text.
 *
 * @param codeName    the base's internal name ({@code name:}), used as its registry key
 * @param inGameName  the base's displayed name: the {@code desc:} text, identical to
 *                    {@code description} (the data file has no {@code plural:} line); {@code null}
 *                    when the record has no {@code desc:} line
 * @param flags       the monster race flag codes ({@code flags:}), each without its {@code RF_}
 *                    prefix, unresolved, from every {@code flags:} line in the record; empty when
 *                    there is none
 * @param glyph       the display glyph as written, expected to be a single character; the last
 *                    {@code glyph:} line if there are several, {@code ""} when there is none
 * @param pain        the pain-message serial number ({@code pain:}), as text; the last
 *                    {@code pain:} line if there are several, {@code ""} when there is none
 * @param description the base's descriptive text ({@code desc:}); where a base has several
 *                    {@code desc:} lines they are joined end to end with no separator, as C does;
 *                    {@code null} when there is none
 * @param line        the source line the block started on, for error messages
 * @author Rowan Crowther
 */
public record MonsterBaseParseRecord(String codeName, String inGameName, List<String> flags,
                                     String glyph, String pain, String description, int line) {
}
