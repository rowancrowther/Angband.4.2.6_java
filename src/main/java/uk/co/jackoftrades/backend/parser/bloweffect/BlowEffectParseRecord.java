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

package uk.co.jackoftrades.backend.parser.bloweffect;

/**
 * One {@code blow_effects.txt} entry exactly as the grammar read it, before any of it has
 * been resolved into enums or domain objects.
 * <p>
 * Every field is a {@code String} because that is all the parse stage knows: the numeric
 * and enum-valued directives are validated and converted by {@link BlowEffectAssembler},
 * which can report a bad value against {@link #line} instead of throwing out of the parse.
 * A directive the entry omits arrives as the empty string, never {@code null}, so the
 * assembler tests absence with {@code isEmpty()} throughout.
 *
 * @param name             the effect's name, as referenced from {@code monster.txt}
 * @param power            power rating, used in the to-hit calculation
 * @param eval             evaluation weight, used when rating a monster's danger
 * @param desc             description shown in monster recall
 * @param loreColourBase   lore colour used when the player has no protection
 * @param loreColourResist lore colour used when the player resists
 * @param loreColourImmune lore colour used when the player is immune
 * @param effectType       what kind of attribute protects against the effect
 * @param resist           the flag or element that protects; what it names depends on
 *                         {@code effectType}
 * @param lashType         the projection this effect becomes in its lash form
 * @param line             the data-file line the entry's {@code name:} sits on, used to
 *                         locate the entry in assembler error messages
 * @author Rowan Crowther
 */
public record BlowEffectParseRecord(String name,
                                    String power,
                                    String eval,
                                    String desc,
                                    String loreColourBase,
                                    String loreColourResist,
                                    String loreColourImmune,
                                    String effectType,
                                    String resist,
                                    String lashType,
                                    int line) {
}
