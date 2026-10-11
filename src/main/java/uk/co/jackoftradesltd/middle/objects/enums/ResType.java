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

package uk.co.jackoftradesltd.middle.objects.enums;

/**
 * Which group of elements an element row belongs to when object power is priced — the port of the
 * anonymous {@code enum { T_LRES, T_HRES }} in {@code obj-power.c}.
 *
 * <p>The group is the join key between the two element tables. {@code ElementSet.getType()} says which
 * group a combination row (immunities, low resists, high resists) counts, and
 * {@code ElementPowers.getType()} says which group each element's pricing row falls into;
 * {@code ItemObject.elementPower} increments a set's count only when the two are equal. So the four
 * basic elements (acid, electricity, fire, cold) are {@link #T_LRES} and the nine others priced in
 * {@code el_powers[]} (poison to disenchantment) are {@link #T_HRES}.
 *
 * <p>C declares the values as an unnamed enum and stores them in plain {@code int} columns; nothing in
 * C or in the port depends on their numeric values, only on equality, so the constants are used as
 * identities. Declaration order is C's (low first, then high), which makes {@code T_LRES} ordinal 0
 * and {@code T_HRES} ordinal 1 as in C.
 *
 * <p>Enum ResType coded on 261009, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public enum ResType {
    /**
     * The "low resist" group: the four basic elements, acid, electricity, fire and cold. These are the
     * only elements that can be immune ({@code res_level} 3) and the only ones with ignore and
     * vulnerability power. C's {@code T_LRES}, ordinal 0.
     *
     * <p>Constant T_LRES coded on 261009, commented in full on 261011.
     */
    T_LRES,
    /**
     * The "high resist" group: poison, light, dark, sound, shards, nexus, nether, chaos and
     * disenchantment. C's {@code T_HRES}, ordinal 1.
     *
     * <p>Constant T_HRES coded on 261009, commented in full on 261011.
     */
    T_HRES
}
