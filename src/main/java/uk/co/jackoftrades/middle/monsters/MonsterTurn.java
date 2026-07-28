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

package uk.co.jackoftrades.middle.monsters;

/**
 * The monster turn engine — the entry points of C's {@code mon-move.c}.
 *
 * <p>Owns how monsters take their turns: which monsters get to act, in what order, and (as the port
 * grows) the movement and AI that decides what each does. It is deliberately kept out of
 * {@link uk.co.jackoftrades.middle.game.GameWorld}, mirroring C's split between {@code game-world.c}
 * (the clock) and {@code mon-move.c} (the monsters). Static methods over {@link uk.co.jackoftrades.middle.game.gameengine.GameState},
 * matching C's globals-driven free functions.
 *
 * <p><b>Status:</b> the game-loop entry points are stubbed; the AI body of {@code mon-move.c} is
 * ported later.
 *
 * @author Rowan Crowther
 */
public class MonsterTurn {
    /**
     * Give a turn to every monster with at least the given energy — the port of C's
     * {@code process_monsters} ({@code mon-move.c}). The game loop calls it with a high threshold to
     * let fast monsters act ahead of the player, and with {@code 0} to run the rest of the monsters
     * once the player's turn is done.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param minimumEnergy only monsters holding at least this much energy act this pass
     */
    public static void processMonsters(int minimumEnergy) {
        // Stub TODO: flesh this out
    }

    /**
     * Mark every monster ready to act again once it has the energy — the port of C's
     * {@code reset_monsters} ({@code mon-move.c}). Run at the end of the world loop so the
     * "already moved this turn" bookkeeping is cleared for the next round.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public static void resetMonsters() {
        // Stub class TODO: implement
    }
}
