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

package uk.co.jackoftrades.backend.parser.visuals;

import java.util.List;

/**
 * The raw, unassembled form of one {@code cycle:} block from visuals.txt - the grammar's output
 * before any colour resolution. A block is a {@code cycle:<group>:<name>} header followed by one or
 * more {@code cycle-color:<code>} step lines; this record carries those parts verbatim, as text, so
 * {@code VisualsCycleAssembler} can turn each colour code into a {@code ColourType} and bucket the
 * result into the {@code VisualsCycler} keyed group-then-name.
 * <p>
 * Ports the parse-time half of the C {@code visuals_cycler} loader ({@code ui-visuals.c}), which
 * likewise reads the group/name/colour-list before installing the cycle.
 *
 * @param group   the cycle's group (the first {@code cycle:} field, e.g. {@code fancy} or
 *                {@code flicker}); the outer selection key in {@code VisualsCycler}
 * @param name    the cycle's name (the second field, e.g. {@code rainbow}); the inner key
 * @param colours the ordered {@code cycle-color:} step codes, one single-character colour code per
 *                entry, still as {@code String}s awaiting {@code ColourType} resolution
 * @param line    the source line of the {@code cycle:} header, used for error reporting
 * @author Rowan Crowther
 */
public record VisualsCycleParseRecord(String group,
                                      String name,
                                      List<String> colours,
                                      int line) {
}
