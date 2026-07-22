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

package uk.co.jackoftrades.backend.parser.flavour;

import java.util.List;

/**
 * The raw form of one {@code kind:} block from {@code flavor.txt}: an object type,
 * the glyph its flavours draw with, and the run of {@code flavor:}/{@code fixed:}
 * lines that follow it. Produced by {@code FlavourGrammar}; the tval and glyph are
 * left as source strings for {@link FlavourKindAssembler} to resolve.
 *
 * @param tVal    the object type name from the {@code kind:} line (e.g. "ring")
 * @param glyph   the display glyph for this block's flavours, still as text
 * @param records the block's flavour/fixed lines, in file order
 * @param line    the source line number of the {@code kind:} line, for errors
 * @author Rowan Crowther
 */
public record FlavourKindParseRecord(String tVal,
                                     String glyph,
                                     List<FlavourParseRecord> records,
                                     int line) {
}
