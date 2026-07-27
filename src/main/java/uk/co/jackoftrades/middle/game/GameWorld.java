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

package uk.co.jackoftrades.middle.game;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

/**
 * The game-clock and turn-loop machinery — the port of C's {@code game-world.c}.
 *
 * <p>This is where the passage of game time and the per-turn processing live: how much energy an
 * actor banks each game turn for its speed, the day/night cycle, and (as the port grows) the
 * {@code process_world} / {@code process_player} / {@code run_game_loop} pass that drives every
 * creature's turn. It is deliberately kept separate from {@link
 * uk.co.jackoftrades.middle.game.gameengine.GameState}: {@code GameState} owns the mutable
 * "current game" <em>data</em> (the turn counter, day count, RNG seeds, character-stage flags),
 * while {@code GameWorld} owns the <em>behaviour</em> that reads and advances it. In C both sat in
 * one file only because C uses file-scope globals as its singleton.
 *
 * <p>The one purely constant piece of this subsystem — the speed-to-energy lookup table — is
 * modelled here as {@link #extractEnergy}; unlike the values in {@link GameConstants} it is baked
 * into the source rather than loaded from {@code lib/gamedata}, exactly as in the original.
 *
 * @author Rowan Crowther
 */
public class GameWorld {
    /**
     * Energy gained per game turn as a function of speed, indexed directly by the speed value
     * (0–199, with 110 being normal speed) — the port of C's {@code extract_energy[200]}.
     *
     * <p>The scale is non-linear and deliberately so: below normal, speed is cheap and most
     * indices collapse to a single point of energy; above normal, each further step buys less, so
     * a normal actor banks 10 per turn, {@code +10} speed banks 20 (a true doubling), but the
     * gains flatten out and cap at 49 near the top of the table. Values are looked up rather than
     * computed to preserve those hand-tuned break-points exactly.
     */
    private static final int[] extractEnergy = {
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* S-50 */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            /* S-40 */     2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
            /* S-30 */     2, 2, 2, 2, 2, 2, 2, 3, 3, 3,
            /* S-20 */     3, 3, 3, 3, 3, 4, 4, 4, 4, 4,
            /* S-10 */     5, 5, 5, 5, 6, 6, 7, 7, 8, 9,
            /* Norm */    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
            /* F+10 */    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
            /* F+20 */    30, 31, 32, 33, 34, 35, 36, 36, 37, 37,
            /* F+30 */    38, 38, 39, 39, 40, 40, 40, 41, 41, 41,
            /* F+40 */    42, 42, 42, 43, 43, 43, 44, 44, 44, 44,
            /* F+50 */    45, 45, 45, 45, 45, 46, 46, 46, 46, 46,
            /* F+60 */    47, 47, 47, 47, 47, 48, 48, 48, 48, 48,
            /* F+70 */    49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
            /* Fast */    49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
    };

    /**
     * The amount of energy gained in one game turn by an actor moving at the given speed — the
     * port of C's {@code turn_energy}.
     *
     * <p>Scales the raw table value from {@link #extractEnergy} by the world's move-energy
     * constant ({@link GameConstants#getWorldMoveEnergy}, C's {@code z_info->move_energy}) so the
     * cost of a "move" is data-driven while the speed curve stays fixed. The division is integer
     * division, matching the C semantics exactly.
     *
     * @param speed the actor's speed, used directly as the index into {@link #extractEnergy}
     *              (0–199, 110 = normal)
     * @return the energy banked this game turn at that speed
     */
    @Contract(pure = true)
    @CheckReturnValue
    private static int turnEnergy(int speed) {
        return extractEnergy[speed] * GameConstants.getWorldMoveEnergy() / 100;
    }
}
