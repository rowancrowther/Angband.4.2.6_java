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

/**
 * The mutable state of the game currently in progress — the port's home for the scattered
 * file-scope globals that C uses as its single implicit "current game" object.
 *
 * <p>In the original these live across several translation units ({@code game-world.c} holds the
 * turn counter, day count and RNG seeds; birth and save/load touch the character-stage flags),
 * bound together only by all being globals. The port gathers the data half here, next to the
 * player, cave and command queue it already tracks, and leaves the turn-loop <em>behaviour</em>
 * to {@link uk.co.jackoftrades.middle.game.GameWorld}. What belongs here is anything that is part
 * of "this game right now" and is reset on a new character or restored from a save.
 *
 * @author Rowan Crowther
 */
public class GameState {
    /**
     * The player character being controlled — C's {@code player} global.
     */
    private static Player mainPlayer;
    /** The current dungeon level the player occupies — C's {@code cave} global. */
    private static Chunk cave;
    /** The queue of commands waiting to be executed this session. */
    private static CommandQueue commandQueue;
    /** The game-turn counter — C's {@code turn} ({@code int32_t}); ticks for every game turn. */
    private static int turn;
    /**
     * Days elapsed in game time, driving the day/night cycle — C's {@code daycount}.
     */
    private static int dayCount;
    /**
     * RNG seed giving this game a consistent set of random artifacts — C's {@code seed_randart}
     * ({@code uint32_t}, held as a {@code long} here to stay unsigned). Set once at birth and
     * persisted with the save.
     */
    private static long seedRandart;
    /**
     * RNG seed giving this game a consistent object-flavour (colour) assignment — C's
     * {@code seed_flavor}. Set once at birth and persisted with the save.
     */
    private static long seedFlavour;
    /**
     * True once a character exists — C's {@code character_generated}.
     */
    private static boolean characterGenerated;
    /**
     * True once that character has a dungeon around them — C's {@code character_dungeon}.
     */
    private static boolean characterDungeon;

    /**
     * @return the current game-turn count
     */
    public static int getTurn() {
        return turn;
    }

    /**
     * @return the number of game days elapsed
     */
    public static int getDaycount() {
        return dayCount;
    }

    /**
     * Advances the game clock by one turn.
     */
    public static void incrementTurn() {
        turn++;
    }

    /**
     * Resets the game clock to zero, as done when a fresh character is born.
     */
    public static void resetTurnForNewPlayer() {
        turn = 0;
    }

    /**
     * Restores the game clock to a value read back from a save file.
     *
     * @param savedTurnValue the turn count recorded in the save
     */
    public static void resetTurnFromSave(int savedTurnValue) {
        turn = savedTurnValue;
    }

    /**
     * @return the player character currently being controlled
     */
    public static Player getPlayer() {
        return GameState.mainPlayer;
    }

    /**
     * Sets the player character for the current game.
     *
     * @param mainPlayer the player to make current
     */
    public static void setPlayer(Player mainPlayer) {
        GameState.mainPlayer = mainPlayer;
    }

    /**
     * @return the dungeon level the player currently occupies
     */
    public static Chunk getCave() {
        return GameState.cave;
    }

    /**
     * Sets the dungeon level the player currently occupies.
     *
     * @param cave the level to make current
     */
    public static void setCave(Chunk cave) {
        GameState.cave = cave;
    }

    /**
     * Sets the command queue backing this session.
     *
     * @param commandQueue the queue to use
     */
    public static void setCommandQueue(CommandQueue commandQueue) {
        GameState.commandQueue = commandQueue;
    }

    /**
     * @return the command queue backing this session
     */
    public static CommandQueue getCommandQueue() {
        return GameState.commandQueue;
    }

    /**
     * Stands up a fresh game state: a new player, a placeholder current level around them, and the
     * command queue that feeds the engine. Called once when a game begins, before the turn loop
     * starts.
     *
     * <p><b>Superseded, and safe to empty.</b> {@link GameEngine#loadGameConstants()} now builds
     * all three itself as the port of {@code player_module.init} ({@code init_player()},
     * {@code [C] src/player.c:476}), and it does so <em>after</em> the game data is read - which is
     * where C puts it, {@code player_module} following {@code arrays_module} in the module table
     * ({@code [C] src/init.c:4445-4460}), because {@code init_player()} sizes the pack, quiver and
     * rune arrays from values that only exist once {@code constants.txt} has been read.
     *
     * <p>This method runs from {@code GameEngine.initGame()}, i.e. <em>before</em> that load, so
     * everything it makes here is discarded moments later. Nothing reads it in between.
     *
     * @author Rowan Crowther
     */
    public static void initGameState() {
    }

    /**
     * Reports whether the current health-bar target is still valid to fire at - the port of C's
     * {@code target_okay}. {@link Command#getTarget} calls this before honouring a queued
     * {@code DIR_TARGET} argument, so a target that has since died or moved out of sight forces a
     * fresh aim rather than being reused.
     *
     * <p>Stub for now: always reports the target as usable until real targeting exists.
     *
     * @return {@code true} while the current target may be used
     */
    public static boolean targetOkay() {
        // TODO: Stub function
        return true;
    }
}
