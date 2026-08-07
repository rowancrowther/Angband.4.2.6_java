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

package uk.co.jackoftrades.backend.parser.roomprofile;

import java.util.List;

/**
 * One raw room record straight out of {@link uk.co.jackoftrades.backend.parser.grammars.roomprofiler.RoomProfileGrammar}
 * — everything the grammar could pull out of a {@code room_template.txt} entry, still in its
 * as-written text form. Numeric fields ({@code type}, {@code rating}, {@code rows}, {@code columns},
 * {@code doors}) are kept as {@link String} rather than parsed here, because parsing and validating
 * them is {@link RoomProfileAssembler}'s job, not the grammar's; this record is the boundary between
 * "the file parsed" and "the file makes sense".
 *
 * <p>{@code tval} is likewise left as text: the data uses both numeric ({@code "0"}) and word
 * ({@code "rod"}, {@code "wand"}, ...) forms, and resolving either to a {@code TValue} is also the
 * assembler's job.
 *
 * <p>{@code flags} is never {@code null} even when the record's {@code flags:} directive is absent
 * — the grammar makes {@code flags:} optional, and an absent line simply leaves this an empty list.
 *
 * @param profileName   the room's name, from {@code name:}
 * @param type          the room's type, as written after {@code type:}
 * @param rating        the room's rating, as written after {@code rating:}
 * @param rows          the row count, as written after {@code rows:}
 * @param columns       the column count, as written after {@code columns:}
 * @param doors         the door count, as written after {@code doors:}
 * @param tval          the tval text, as written after {@code tval:} — numeric or a name
 * @param flags         the flag names from {@code flags:}, in file order; empty if the directive
 *                      was absent
 * @param roomMap       the room layout, one string per {@code D:} line, in file order
 * @param profileLineNo the source line of this record's {@code name:} directive
 * @param roomMapLineNo the source line of this record's first {@code D:} line
 * @author Rowan Crowther
 */
public record RoomProfileParseRecord(String profileName,
                                     String type,
                                     String rating,
                                     String rows,
                                     String columns,
                                     String doors,
                                     String tval,
                                     List<String> flags,
                                     List<String> roomMap,
                                     int profileLineNo,
                                     int roomMapLineNo) {
}
