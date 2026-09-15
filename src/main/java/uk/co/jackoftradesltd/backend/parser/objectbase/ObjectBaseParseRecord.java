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
 * @param name        the base's display name ({@code name:})
 * @param tVal        the object tval name as text, resolved to {@code TValue} by the assembler
 * @param colour      the display colour code as text, resolved to {@code ColourEnum}
 * @param flags       the flag codes ({@code flags:}), unresolved; a {@code HATES_}-prefixed
 *                    entry resolves to an element rather than a kind flag
 * @param breakChance the break-chance percentage as text
 * @param maxStack    the maximum stack size as text
 * @param line        the source line the block started on, for error messages
 * @author Rowan Crowther
 */
public record ObjectBaseParseRecord(String name, String tVal, String colour,
                                    List<String> flags, String breakChance,
                                    String maxStack, int line) {
}
