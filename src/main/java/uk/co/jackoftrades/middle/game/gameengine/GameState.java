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

package uk.co.jackoftrades.middle.game.gameengine;

import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.player.Player;

public class GameState {
    private static Player mainPlayer;
    private static Chunk cave;
    private static CommandQueue commandQueue;

    public static Player getPlayer() {
        return GameState.mainPlayer;
    }

    public static void setPlayer(Player mainPlayer) {
        GameState.mainPlayer = mainPlayer;
    }

    public static Chunk getCave() {
        return GameState.cave;
    }

    public static void setCave(Chunk cave) {
        GameState.cave = cave;
    }

    public static void setCommandQueue(CommandQueue commandQueue) {
        GameState.commandQueue = commandQueue;
    }

    public static CommandQueue getCommandQueue() {
        return GameState.commandQueue;
    }

    public static void initGameState() {
        mainPlayer = new Player();
        cave = new Chunk("Current Level", 0, 0, 0, 0,
                0, false, 10, 10, 4, 3, 3,
                1, 1, 15, mainPlayer);
        commandQueue = new CommandQueue(mainPlayer);
    }
}
