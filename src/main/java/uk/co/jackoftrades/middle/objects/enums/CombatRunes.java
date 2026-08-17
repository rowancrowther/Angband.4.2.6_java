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

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The three fixed combat runes — the enchantments to armour, to-hit and to-damage that any object
 * can carry. Unlike every other rune variety these are not loaded from a data file: they are a
 * closed set known at compile time, which is why the descriptions live on the constants rather than
 * being looked up. Mirrors the C original's {@code enum combat_runes}
 * ({@code src/obj-knowledge.h}), and the descriptions port its {@code c_rune[]} table
 * ({@code src/obj-knowledge.c}).
 *
 * <p>{@link #getDescription()} is the port of {@code c_rune[]}, and is the only combat-rune text
 * the player ever sees: {@code rune_name} is the sole place C displays one, both as a knowledge-menu
 * entry and as the title of a rune's detail page. There is deliberately no second, shorter label —
 * a caller wanting one would be inventing text the original does not have.
 *
 * <p>{@code COMBAT_RUNE_MAX} is the count sentinel and carries an empty description; it is not a
 * rune and callers must skip it, as C's {@code i < COMBAT_RUNE_MAX} bound does.
 *
 * <p>The declaration order is significant. It fixes the order the combat runes appear in the rune
 * list, and the knowledge code branches on the constant to decide which field of the player's
 * knowledge object a rune refers to — {@code to_a}, {@code to_h} or {@code to_d} respectively.
 *
 * @author Rowan Crowther
 */
public enum CombatRunes {
    COMBAT_RUNE_TO_A("enchantment to armour"),
    COMBAT_RUNE_TO_H("enchantment to hit"),
    COMBAT_RUNE_TO_D("enchantment to damage"),
    COMBAT_RUNE_MAX("");

    /**
     * The player-visible description of this rune, as shown in the knowledge menu. Empty for the
     * {@code COMBAT_RUNE_MAX} sentinel.
     */
    private final String description;

    /**
     * Bind a combat rune to its player-visible description.
     *
     * @param description the description shown to the player
     */
    CombatRunes(String description) {
        this.description = description;
    }

    /**
     * @return this rune's player-visible description, or the empty string for the sentinel
     */
    public String getDescription() {
        return description;
    }
}
