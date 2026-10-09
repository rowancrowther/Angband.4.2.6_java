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
 * The three fixed combat runes — the enchantments to armour, to-hit and to-damage that any object
 * can carry. Unlike every other rune variety these are not loaded from a data file: they are a
 * closed set known at compile time, which is why the descriptions live on the constants rather than
 * being looked up. Mirrors the C original's {@code enum combat_runes}
 * ({@code obj-knowledge.h}), and the descriptions port its {@code c_rune[]} table
 * ({@code obj-knowledge.c}).
 *
 * <p>{@link #getDescription()} is the port of {@code c_rune[]}, and is the only combat-rune text
 * the player ever sees. C reads it through {@code rune_name} alone — as a knowledge-menu entry and
 * as the title of a rune's detail page in {@code ui-knowledge.c}, and in the "You have learned the
 * rune of …" message that {@code player_learn_rune} prints. There is deliberately no second,
 * shorter label — a caller wanting one would be inventing text the original does not have. The
 * wording is Oxford spelling ("armour") where C's table says "armor".
 *
 * <p>{@code COMBAT_RUNE_MAX} is the count sentinel and carries an empty description; it is not a
 * rune and callers must skip it, as C's {@code i < COMBAT_RUNE_MAX} bound does. C also uses it as
 * the starting value of the rune count in {@code init_rune}, because the combat runes occupy the
 * first {@code COMBAT_RUNE_MAX} slots of the rune list.
 *
 * <p>The declaration order is significant. It fixes the order the combat runes appear in the rune
 * list, and the knowledge code branches on the constant to decide which field of the player's
 * knowledge object a rune refers to — {@code to_a}, {@code to_h} or {@code to_d} respectively.
 *
 * <p>Class CombatRunes coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public enum CombatRunes {
    /**
     * The rune for enchantment to armour class, which the player learns by noticing a non-zero
     * {@code to_a} on an object. Port of {@code COMBAT_RUNE_TO_A}, value 0 in C.
     *
     * <p>Constant COMBAT_RUNE_TO_A coded before 261009, commented in full on 261009.
     */
    COMBAT_RUNE_TO_A("enchantment to armour"),

    /**
     * The rune for enchantment to hit, which the player learns by noticing a non-standard
     * {@code to_h} on an object. Port of {@code COMBAT_RUNE_TO_H}, value 1 in C.
     *
     * <p>Constant COMBAT_RUNE_TO_H coded before 261009, commented in full on 261009.
     */
    COMBAT_RUNE_TO_H("enchantment to hit"),

    /**
     * The rune for enchantment to damage, which the player learns by noticing a non-zero
     * {@code to_d} on an object. Port of {@code COMBAT_RUNE_TO_D}, value 2 in C.
     *
     * <p>Constant COMBAT_RUNE_TO_D coded before 261009, commented in full on 261009.
     */
    COMBAT_RUNE_TO_D("enchantment to damage"),

    /**
     * The count sentinel — C's {@code COMBAT_RUNE_MAX}, value 3. Not a rune: it has no entry in
     * {@code c_rune[]}, so its description is empty and loops over the combat runes must stop
     * before it.
     *
     * <p>Constant COMBAT_RUNE_MAX coded before 261009, commented in full on 261009.
     */
    COMBAT_RUNE_MAX("");

    /**
     * The player-visible description of this rune, as shown in the knowledge menu. Empty for the
     * {@code COMBAT_RUNE_MAX} sentinel.
     *
     * <p>Field description coded before 261009, commented in full on 261009.
     */
    private final String description;

    /**
     * Bind a combat rune to its player-visible description. The Java stand-in for indexing C's
     * {@code c_rune[]} table by the {@code combat_runes} value.
     *
     * <p>Constructor CombatRunes coded before 261009, commented in full on 261009.
     *
     * @param description the description shown to the player
     */
    CombatRunes(String description) {
        this.description = description;
    }

    /**
     * Returns the text C would take from {@code c_rune[]} for this rune, which {@code rune_name}
     * hands to the knowledge menu and the learn message.
     *
     * <p>Function getDescription coded before 261009, commented in full on 261009.
     *
     * @return this rune's player-visible description, or the empty string for the sentinel
     */
    public String getDescription() {
        return description;
    }
}
