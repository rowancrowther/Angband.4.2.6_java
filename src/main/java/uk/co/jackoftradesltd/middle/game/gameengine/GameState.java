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

package uk.co.jackoftradesltd.middle.game.gameengine;

import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.player.Player;

/**
 * The mutable state of the game currently in progress — the port's home for the scattered
 * file-scope globals that C uses as its single implicit "current game" object.
 *
 * <p>In the original these live across several translation units: {@code game-world.c} defines
 * {@code turn}, {@code daycount}, {@code seed_randart}, {@code seed_flavor} and
 * {@code character_generated}, {@code player.c} defines {@code player}, {@code cave.c} defines
 * {@code cave}, and {@code cmd-core.c} keeps its command ring as file-scope statics. They are bound
 * together only by all being globals, and C reads and writes them directly, so apart from
 * {@code target_okay()} none of the members below has a C function to be checked against. Each
 * accessor here is the boundary's stand-in for the bare reads and writes C makes at its call sites.
 *
 * <p>The port gathers the data half here, next to the {@link Player}, {@link Chunk} and
 * {@link CommandQueue} it tracks, and leaves the turn-loop <em>behaviour</em> to
 * {@link GameWorld}. What belongs here is anything that is part of "this game right now" and is
 * reset on a new character or restored from a save. The day counter is the exception: C also keeps
 * {@code daycount} in {@code game-world.c}, but the port owns it in {@link GameWorld}, and
 * {@link #getDaycount()} only forwards there.
 *
 * <p>Everything is static, so the state is shared across the JVM; tests that write it must put it
 * back afterwards.
 *
 * <p>Class GameState coded before 260903, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
public class GameState {
    /**
     * The player character being controlled — C's {@code player} global ({@code player.c}). C
     * allocates it in {@code init_player()} and nulls it again in {@code cleanup_player()}; here
     * it is {@code null} until {@link #setPlayer(Player)} is first called.
     *
     * <p>Field mainPlayer coded before 260903, commented in full on 261006.
     */
    private static Player mainPlayer;
    /**
     * The current dungeon level the player occupies — C's {@code cave} global ({@code cave.c}).
     * C assigns it in {@code prepare_next_level()} ({@code generate.c}) and it starts as
     * {@code NULL}; here it is {@code null} until {@link #setCave(Chunk)} is first called.
     *
     * <p>Field cave coded before 260903, commented in full on 261006.
     */
    private static Chunk cave;
    /**
     * The {@link CommandQueue} that feeds the engine this session. This one is a port-only
     * member: C holds the equivalent as the file-scope statics {@code cmd_queue}, {@code cmd_head}
     * and {@code cmd_tail} in {@code cmd-core.c}, which no other file can reach, so there is no
     * global to mirror. {@code null} until {@link #setCommandQueue(CommandQueue)} is called.
     *
     * <p>Field commandQueue coded before 260903, commented in full on 261006.
     */
    private static CommandQueue commandQueue;
    /**
     * The game-turn counter — C's {@code turn} global ({@code int32_t}, {@code game-world.c}).
     * It is incremented once per game turn by the world loop, set to 1 when a new player is
     * initialized ({@code player_init()} in {@code player-birth.c}) and overwritten from the save
     * by {@code rd_misc()} in {@code load.c}. Java's {@code int} is 32-bit two's complement, so an
     * overflow wraps exactly as C's {@code int32_t} does in practice. Starts at 0, as a C global
     * does.
     *
     * <p>Field turn coded before 260903, commented in full on 261006.
     */
    private static int turn;
    /**
     * RNG seed giving this game a consistent set of random artifacts — C's {@code seed_randart}
     * ({@code uint32_t}, {@code game-world.c}), held as a {@code long} here to stay unsigned. C
     * sets it once in {@code do_cmd_accept_character()} ({@code player-birth.c}) and writes and
     * reads it with the save ({@code save.c}, {@code load.c}).
     *
     * <p>This field has no accessor yet: nothing in the port reads or writes it until birth and
     * save/load need it.
     *
     * <p>Field seedRandart coded before 260903, commented in full on 261006.
     */
    private static long seedRandart;
    /**
     * RNG seed giving this game a consistent object-flavour (colour) assignment — C's
     * {@code seed_flavor} ({@code uint32_t}, {@code game-world.c}), held as a {@code long} here to
     * stay unsigned. C sets it once in {@code do_cmd_accept_character()} ({@code player-birth.c})
     * as {@code randint0(0x10000000)}, restores it in {@code rd_misc()} ({@code load.c}), and
     * {@code flavor_init()} ({@code obj-util.c}) loads it into the "simple" RNG so the flavour
     * assignment comes out the same every time.
     *
     * <p>Field seedFlavour coded before 260903, commented in full on 261006.
     */
    private static long seedFlavour;
    /**
     * True once a character exists — C's {@code character_generated} global
     * ({@code game-world.c}). It starts {@code false}, and is a guard in C's
     * {@code calc_spells()}, {@code update_stuff()} and {@code redraw_stuff()} that stops them
     * running before there is a character to work on.
     *
     * <p>Field characterGenerated coded before 260903, commented in full on 261006.
     */
    private static boolean characterGenerated;

    /**
     * Reads the game-turn counter — the port of reading C's {@code turn} global
     * ({@code game-world.c}). C has no accessor function for it; every call site reads the global
     * directly, and this getter stands in for those bare reads at the boundary.
     *
     * <p>Function getTurn coded before 260903, commented in full on 261006.
     *
     * @return the current game-turn count
     */
    public static int getTurn() {
        return turn;
    }

    /**
     * Replaces the game-turn counter directly - the port of writing C's global {@code turn}
     * ({@code int32_t}, {@code game-world.c}). C has no single setter for this global; every call
     * site assigns it directly, and this is the boundary's general-purpose counterpart to those
     * sites, alongside the load-specific {@link #resetTurnFromSave(int)}. No bounds check in
     * either language. Birth uses it for C's {@code turn = 1} in {@code player_init()}
     * ({@code player-birth.c}).
     *
     * <p>Function setTurn coded on 260903, commented in full on 261006.
     *
     * @param turn the new game-turn count
     */
    public static void setTurn(int turn) {
        GameState.turn = turn;
    }

    /**
     * Reads the number of game days elapsed — the port of reading C's {@code daycount} global
     * ({@code game-world.c}). Unlike the other members here the counter is not stored in
     * {@link GameState}: it lives in {@link GameWorld}, which advances it from its world pass, and
     * this method only forwards to {@link GameWorld#getDaycount()}.
     *
     * <p>Function getDaycount coded before 260903, commented in full on 261006.
     *
     * @return the number of game days elapsed
     */
    public static int getDaycount() {
        return GameWorld.getDaycount();
    }

    /**
     * Advances the game clock by one turn — the port of C's {@code turn++} in
     * {@code run_game_loop()} ({@code game-world.c}), which is its only increment. Wraps at
     * {@link Integer#MAX_VALUE} as a 32-bit {@code int32_t} does in practice.
     *
     * <p>Function incrementTurn coded before 260903, commented in full on 261006.
     */
    public static void incrementTurn() {
        turn++;
    }

    /**
     * Restores the game clock to a value read back from a save file — the port of
     * {@code rd_s32b(&turn)} in {@code rd_misc()} ({@code load.c}), which stores the saved count
     * straight into the global with no check. Nothing calls this yet, because loading is not
     * ported.
     *
     * <p>Function resetTurnFromSave coded before 260903, commented in full on 261006.
     *
     * @param savedTurnValue the turn count recorded in the save
     */
    public static void resetTurnFromSave(int savedTurnValue) {
        turn = savedTurnValue;
    }

    /**
     * Reads the player character — the port of reading C's {@code player} global
     * ({@code player.c}). C has no accessor function for it; every call site reads the global
     * directly, and this getter stands in for those bare reads at the boundary. Returns
     * {@code null} before a player has been set, as C's global is {@code NULL} until
     * {@code init_player()}.
     *
     * <p>Function getPlayer coded before 260903, commented in full on 261006.
     *
     * @return the player character currently being controlled, or {@code null} if none is set
     */
    public static Player getPlayer() {
        return GameState.mainPlayer;
    }

    /**
     * Sets the player character — the port of writing C's {@code player} global
     * ({@code player.c}), which {@code init_player()} assigns and {@code cleanup_player()} resets
     * to {@code NULL}. This setter stands in for both direct assignments at the boundary, so
     * passing {@code null} is the equivalent of the cleanup.
     *
     * <p>Function setPlayer coded before 260903, commented in full on 261006.
     *
     * @param mainPlayer the player to make current, or {@code null} to clear it
     */
    public static void setPlayer(Player mainPlayer) {
        GameState.mainPlayer = mainPlayer;
    }

    /**
     * Reads the dungeon level the player occupies — the port of reading C's {@code cave} global
     * ({@code cave.c}). C has no accessor function for it; every call site reads the global
     * directly, and this getter stands in for those bare reads at the boundary. Returns
     * {@code null} before a level has been set, as C's global is {@code NULL} until a level is
     * generated.
     *
     * <p>Function getCave coded before 260903, commented in full on 261006.
     *
     * @return the dungeon level the player currently occupies, or {@code null} if none is set
     */
    public static Chunk getCave() {
        return GameState.cave;
    }

    /**
     * Sets the dungeon level the player occupies — the port of writing C's {@code cave} global
     * ({@code cave.c}), which {@code prepare_next_level()} ({@code generate.c}) assigns when a
     * level is generated or restored and sets to {@code NULL} while the old one is stored. This
     * setter stands in for those direct assignments at the boundary.
     *
     * <p>Function setCave coded before 260903, commented in full on 261006.
     *
     * @param cave the level to make current, or {@code null} to clear it
     */
    public static void setCave(Chunk cave) {
        GameState.cave = cave;
    }

    /**
     * Reads the {@link CommandQueue} backing this session. Port-only, as for
     * {@link #setCommandQueue(CommandQueue)}; {@code null} until one is set.
     *
     * <p>Function getCommandQueue coded before 260903, commented in full on 261006.
     *
     * @return the queue backing this session, or {@code null} if none is set
     */
    public static CommandQueue getCommandQueue() {
        return GameState.commandQueue;
    }

    /**
     * Sets the {@link CommandQueue} backing this session. Port-only: C has no global for this,
     * because its command ring is a set of file-scope statics in {@code cmd-core.c}.
     *
     * <p>Function setCommandQueue coded before 260903, commented in full on 261006.
     *
     * @param commandQueue the queue to use
     */
    public static void setCommandQueue(CommandQueue commandQueue) {
        GameState.commandQueue = commandQueue;
    }

    /**
     * Once stood up a fresh game state: a new player, a placeholder current level around them, and
     * the {@link CommandQueue} that feeds the engine. Its body is now empty and nothing calls it.
     *
     * <p><b>Superseded, and safe to delete.</b> {@link GameEngine#loadGameConstants(Core)} builds
     * all three itself as the port of {@code player_module.init} ({@code init_player()} in
     * {@code player.c}), and it does so <em>after</em> the game data is read - which is where C
     * puts it, {@code player_module} following {@code arrays_module} in the module table in
     * {@code init.c}, because {@code init_player()} sizes the pack, quiver and rune arrays from
     * values that only exist once {@code constants.txt} has been read. There is no C function of
     * this name to compare it with.
     *
     * <p>Method initGameState coded before 260903, commented in full on 261006.
     */
    public static void initGameState() {
    }

    /**
     * Reports whether the current health-bar target is still valid to fire at - the port of C's
     * {@code target_okay()} ({@code target.c}). {@link Command#getTarget} calls this before
     * honouring a queued {@code DIR_TARGET} argument, so a target that has since died or moved out
     * of sight forces a fresh aim rather than being reused.
     *
     * <p>In C the answer is {@code false} when no target is set. For a monster target it is
     * {@code true} only while {@code target_able()} still holds, and it also refreshes the stored
     * target grid from the monster's current position. For a grid target with no monster it is
     * {@code true} whenever both coordinates are non-zero, and otherwise {@code false}.
     *
     * <p>Stub for now: always reports the target as usable until real targeting exists.
     *
     * <p>Function targetOkay coded before 260903, commented in full on 261006.
     *
     * @return {@code true} while the current target may be used
     */
    public static boolean targetOkay() {
        // TODO: Stub function
        return true;
    }

    /**
     * Reports whether a character currently exists — the port of reading C's
     * {@code character_generated} global ({@code game-world.c}). C has no accessor function for
     * it; every call site reads the global directly (e.g. {@code update_stuff()} in
     * {@code player-calcs.c}), and this getter stands in for those bare reads at the boundary.
     *
     * <p>{@code false} until birth completes ({@code do_cmd_accept_character()} in
     * {@code player-birth.c}) or a save loads ({@code savefile_load()} in {@code savefile.c}), and
     * reset to {@code false} again ahead of a fresh birth after death or a new game
     * ({@code start_game()} in {@code ui-game.c}).
     *
     * <p>Function getCharacterGenerated coded on 260907, commented in full on 261006.
     *
     * @return {@code true} once a character has been generated
     */
    public static boolean getCharacterGenerated() {
        return characterGenerated;
    }

    /**
     * Sets whether a character currently exists — the port of writing C's
     * {@code character_generated} global ({@code game-world.c}). C has no setter function for
     * it either; every call site assigns it directly, {@code true} once birth completes
     * ({@code do_cmd_accept_character()} in {@code player-birth.c}) or a save loads
     * ({@code savefile_load()} in {@code savefile.c}), and {@code false} again ahead of a fresh
     * birth after death or a new game ({@code start_game()} in {@code ui-game.c}), and this
     * setter stands in for those direct assignments at the boundary.
     *
     * <p>Function setCharacterGenerated coded on 260908, commented in full on 261006.
     *
     * @param characterGenerated {@code true} once a character has been generated
     */
    public static void setCharacterGenerated(boolean characterGenerated) {
        GameState.characterGenerated = characterGenerated;
    }

    /**
     * Reads the RNG seed used to give this game a consistent object-flavour (colour)
     * assignment — the port of reading C's {@code seed_flavor} global ({@code game-world.c}).
     * C has no accessor function for it; every call site reads the global directly, for example
     * to re-seed the "simple" RNG before assigning flavours ({@code flavor_init()} in
     * {@code obj-util.c}), and this getter stands in for those bare reads at the boundary.
     *
     * <p>Function getSeedFlavour coded on 260908, commented in full on 261006.
     *
     * @return the object-flavour RNG seed
     */
    public static long getSeedFlavour() {
        return seedFlavour;
    }

    /**
     * Sets the RNG seed used to give this game a consistent object-flavour (colour)
     * assignment — the port of writing C's {@code seed_flavor} global ({@code game-world.c}).
     * C has no setter function for it either; every call site assigns it directly, typically once
     * at birth ({@code seed_flavor = randint0(0x10000000)} in {@code do_cmd_accept_character()},
     * {@code player-birth.c}) or when restoring it from a save ({@code rd_misc()} in
     * {@code load.c}), and this setter stands in for those direct assignments at the boundary.
     *
     * <p>Function setSeedFlavour coded on 260908, commented in full on 261006.
     *
     * @param seedFlavour the object-flavour RNG seed
     */
    public static void setSeedFlavour(long seedFlavour) {
        GameState.seedFlavour = seedFlavour;
    }
}
