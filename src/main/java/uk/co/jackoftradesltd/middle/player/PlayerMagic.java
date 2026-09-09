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

package uk.co.jackoftradesltd.middle.player;

public class PlayerMagic {
    /**
     * Allocates and resets the player's per-spell tracking lists - the port of C's
     * {@code player_spells_init} ({@code player-spell.c:139-153}). Reads the player's
     * class's total spell count and, if it is zero (a class with no spellbook), returns
     * immediately without touching {@code spellFlags}/{@code spellOrder} at all, matching
     * C's early {@code if (!num_spells) return;}. Otherwise hands off to
     * {@link Player#initSpellOrder(int, int)} to clear and refill both lists, passing
     * {@code 99} as the "no spell learned" sentinel C always uses for {@code spell_order}.
     *
     * <p>Called from {@code PlayerBirth.doCmdAcceptCharacter} ({@code PlayerBirth.java:2446}),
     * once per character birth.
     *
     * <p>Function playerSpellsInit coded on 260908, commented in full on 260908.
     *
     * @param player the player whose spell-tracking lists are (re)initialised
     */
    public static void playerSpellsInit(Player player) {
        int numSpells = player.getPlayerClass().getMagic().getTotalSpells();

        // none
        if (numSpells == 0) return;

        // Allocate
        player.initSpellOrder(numSpells, 99);
    }
}
