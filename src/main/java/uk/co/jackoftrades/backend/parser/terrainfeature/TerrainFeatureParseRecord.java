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

package uk.co.jackoftrades.backend.parser.terrainfeature;

import java.util.List;

/**
 * The raw parse record for a single {@code terrain.txt} block - one-to-one with the directives the
 * grammar saw, before any interpretation. Following the suite-wide convention, every component is a
 * <strong>raw String</strong> (or list of Strings) exactly as it appeared in the file; all
 * resolution - {@code FEAT_}/{@code TF_}/{@code RF_} enum lookups, integer parsing, glyph/colour
 * construction - is deferred to {@link TerrainFeatureAssembler}, keeping this DTO a dumb, testable
 * carrier between the {@code TerrainFeatureGrammar} and the assembler.
 *
 * <p>Optional directives that were absent arrive as the empty string (or an empty list), never
 * {@code null} - the grammar's {@code @init} blocks seed those defaults - so the assembler can
 * distinguish "absent" with an {@code isEmpty()} guard rather than a null check.
 *
 * @param code              the raw terrain code (grammar strips the {@code code:} keyword); the
 *                          assembler prefixes {@code FEAT_} to resolve it
 * @param name              the printable terrain name
 * @param desc              the flavour description (concatenated across repeated {@code desc:} lines)
 * @param mimic             the raw code of the terrain this one mimics, or empty if none
 * @param priority          the mini-map display priority as text, or empty
 * @param dig               the digging-difficulty index as text, or empty
 * @param flags             the raw {@code TF_} flag names (keyword and {@code TF_} prefix stripped)
 * @param glyph             the raw display glyph from {@code graphics:}
 * @param colour            the raw display colour from {@code graphics:}
 * @param walkMsg           the message shown when walking onto this terrain, or empty
 * @param runMsg            the message shown when running onto this terrain, or empty
 * @param hurtMsg           the message shown when this terrain hurts the player, or empty
 * @param dieMsg            the message shown when this terrain kills the player, or empty
 * @param confusedMsg       the message for a confused monster moving into this terrain, or empty
 * @param lookPrefix        the prefix shown before the name when looking, or empty
 * @param lookInPreposition the preposition used when looking at this terrain, or empty
 * @param resistFlags       the raw {@code RF_} resist-flag names ({@code RF_} prefix stripped)
 * @param line              the 1-based source line the record began on, for error reporting
 * @author Rowan Crowther
 */
public record TerrainFeatureParseRecord(String code, String name, String desc,
                                        String mimic, String priority,
                                        String dig, List<String> flags, String glyph,
                                        String colour, String walkMsg, String runMsg,
                                        String hurtMsg, String dieMsg, String confusedMsg,
                                        String lookPrefix, String lookInPreposition,
                                        List<String> resistFlags, int line) {
}
