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

package uk.co.jackoftradesltd.backend.parser.objectbase;

import java.util.List;

/**
 * Immutable extraction record for one {@code object_base.txt} entry: the raw, still-unresolved
 * fields parsed by the grammar, later turned into the {@code ObjectBase} domain type by
 * {@link ObjectBaseAssembler}.
 *
 * @author Rowan Crowther
 */
public record ObjectBaseParseRecord(String name, String tVal, String colour,
                                    List<String> flags, String breakChance,
                                    String maxStack, int line) {
}
