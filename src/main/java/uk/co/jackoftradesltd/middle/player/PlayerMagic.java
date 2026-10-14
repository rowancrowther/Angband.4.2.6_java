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

/**
 * Player-side spell bookkeeping - the port of the player-owned parts of C's
 * {@code player-spell.c}. Only {@code player_spells_init} is ported so far; the learning,
 * casting and spell-lookup functions of that file arrive in a later chapter.
 *
 * <p>Holds no state of its own: every method is static and works on the {@link Player} it is
 * given. The per-spell lists it initializes ({@code spellFlags}, {@code spellOrder}) are
 * fields of {@link Player}.
 *
 * <p>Class PlayerMagic coded on 260908, commented in full on 261014.
 */
public class PlayerMagic {
    /**
     * Allocates and resets the player's per-spell tracking lists - the port of C's
     * {@code player_spells_init} in {@code player-spell.c}. Reads the player's class's total
     * spell count and, if it is zero (a class with no spellbook), returns immediately without
     * touching {@code spellFlags}/{@code spellOrder} at all, matching C's early
     * {@code if (!num_spells) return;}. Otherwise hands off to
     * {@link Player#initSpellOrder(int, int)} to clear and refill both lists, passing
     * {@code 99} as the "no spell learned" sentinel C always uses for {@code spell_order}.
     *
     * <p>Because the zero-spell return happens before any list is touched, a non-caster's
     * lists keep whatever they already held - empty for a freshly constructed {@link Player},
     * exactly as C's never-allocated null pointers.
     *
     * <p>Called from {@code PlayerBirth.doCmdAcceptCharacter}, once per character birth, after
     * the class has been embodied and the starting money given. C also calls it when loading
     * a savefile ({@code load.c}) and from the borg's reincarnation code; neither of those
     * paths calls this method in the port yet.
     *
     * <p>Function playerSpellsInit coded on 260908, commented in full on 261014.
     *
     * @param player the player whose spell-tracking lists are (re)initialized
     */
    public static void playerSpellsInit(Player player) {
        int numSpells = player.getPlayerClass().getMagic().getTotalSpells();

        // none
        if (numSpells == 0) return;

        // Allocate
        player.initSpellOrder(numSpells, 99);
    }
}
