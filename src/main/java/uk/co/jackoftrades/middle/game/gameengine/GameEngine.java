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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.game.event.EventsBusHandler;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.loaders.DungeonLoader;
import uk.co.jackoftrades.middle.player.Player;

/**
 * The top-level game runtime: a singleton that performs middle-end start-up - game
 * state, the event bus and the game constants - roughly the Java counterpart of the C
 * original's initialisation bootstrap, {@code init_angband()} ({@code src/init.c}) as
 * called from {@code main()} ({@code src/main.c}).
 *
 * <p><b>Nothing constructs this yet.</b> It is meant to be built once on the game
 * thread, by {@link GameRunner}, so that everything it initialises is confined to that
 * thread; but {@code GameRunner} does not hold an engine, so no live code path reaches
 * {@link #getGame()}.
 *
 * @author Rowan Crowther
 */
public class GameEngine {
    /**
     * Logger for start-up diagnostics.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The live event bus, held here as the game-wide seam other layers reach through
     * {@link #getEventsBusHandler()}. Typed to the {@link EventsHandler} interface so a
     * test can swap in its own bus via {@link #setEventsBusHandler(EventsHandler)}.
     *
     * <p><b>Never null.</b> A working bus is installed here, at class load, so any caller can
     * signal an event without first arranging for one - the same guarantee
     * {@code GameInputHolder} gives its seam by installing a default instance at declaration.
     * {@link #initGame()} replaces it with a fresh one, so a game that starts gets a clean bus,
     * but the field is never empty in between.
     *
     * <p>It was previously assigned only inside {@code initGame()}, which left it null for
     * anything that ran without building an engine. Since callers signal through it unguarded,
     * that surfaced as {@code NullPointerException}s in every test that exercised game logic
     * without first standing up a bus of its own.
     *
     * @author Rowan Crowther
     */
    private static EventsHandler eventsBusHandler = new EventsBusHandler();

    /**
     * The singleton, built on first {@link #getGame()}. Null until then.
     *
     * @author Rowan Crowther
     */
    private static GameEngine instance;


    /**
     * Private constructor - the singleton is reached through {@link #getGame()}, never
     * built directly. All it does is run {@link #initGame()}.
     *
     * @author Rowan Crowther
     */
    private GameEngine() {
        initGame();
    }

    /**
     * Initialise the middle end far enough that events can be signalled: reset the game state, then
     * install a fresh event bus. The data load itself is deliberately <em>not</em> here - it waits
     * in {@link #loadGameConstants()} so the caller gets a window to register handlers first.
     *
     * <p><b>Known duplication:</b> {@link GameState#initGameState()} still builds a player, level
     * and command queue of its own, and this runs before the data load - so every engine creates
     * that set twice, and the pre-load set is thrown away unread by {@link #loadGameConstants()}.
     * Only the second set is the port of {@code player_module.init}; the first is left over from
     * when this was the only place they were made.
     *
     * <p>The bus assignment here <em>replaces</em> the one installed at class load, giving each
     * newly built engine a bus with no handlers left over from before.
     *
     * <p>The bus is created <em>before</em> {@link GameConstants#init()} deliberately.
     * {@code GameConstants.init()} is this port's {@code init_angband()} ({@code [C] src/init.c}),
     * the step C signals {@code EVENT_ENTER_INIT} from - so any bus created after it would miss
     * every event raised during loading, exactly as C requires {@code init_display()} to precede
     * {@code init_angband()} in {@code main()} ({@code [C] src/main.c}).
     *
     * <p>The gap C leaves between those two calls for registering handlers now exists here too: it
     * is the space between an engine being built and {@code loadGameConstants()} being called, and
     * {@code GameRunner.gameLoop()} is what uses it.
     *
     * @author Rowan Crowther
     */
    private void initGame() {
        eventsBusHandler = new EventsBusHandler();
    }

    /**
     * Read every file under {@code lib/gamedata} into the registries and then stand up the objects
     * that depend on what was read - this port's {@code init_angband()} ({@code [C] src/init.c}).
     *
     * <p>Separate from {@link #initGame()}, and that separation is the point. The bus is created in
     * {@code initGame()} but the load is deferred to here, which gives the caller a window between
     * the two to register handlers - exactly the gap C leaves between {@code init_display()} and
     * {@code init_angband()}. {@code GameRunner.gameLoop()} uses it to wire {@code InitHandlers}
     * before the load raises {@code EVENT_ENTER_INIT} from inside it.
     *
     * <p>The two halves below mirror C's own two halves of {@code init_angband()}:
     * {@link GameConstants#init()} covers both {@code init_game_constants()} and the data-file
     * parsing C does in its {@code arrays_module}, and the player creation that follows is
     * {@code player_module.init} - that is, {@code init_player()} ({@code [C] src/player.c:476}),
     * which C's module table runs immediately after {@code arrays_module}
     * ({@code [C] src/init.c:4445-4460}).
     *
     * <p><b>That order is a dependency, not a convention.</b> C's {@code init_player()} sizes the
     * pack from {@code z_info->pack_size}, the quiver from {@code quiver_size}, and the
     * rune-knowledge arrays from {@code brand_max}/{@code slay_max}/{@code curse_max} - all values
     * that exist only once {@code constants.txt} has been read. The port's {@link Player}
     * constructor uses growable collections and so does not depend on them <em>yet</em>, but it
     * will as soon as inventory and rune knowledge are ported; creating the player after the load
     * keeps the port correct in advance rather than after the fact.
     *
     * <p>Long-running and file-bound, so it belongs on the game thread. An interrupt arriving during
     * it does not stop it cleanly: the reader's channel closes and the resulting failure is
     * reported as a data-load error, which is what happens today if the window is closed while the
     * game is still starting up.
     *
     * <p>Every call replaces the player, level and command queue held in {@link GameState}, so
     * calling this twice on one engine discards the first set entirely.
     *
     * @author Rowan Crowther
     */
    public void loadGameConstants() {
        GameConstants.init();

        // The port of init_player() ([C] src/player.c:476): allocate the player and its sub-structs.
        // Everything C mem_zallocs there - upkeep, the timed-effect table, obj_k, the default
        // options - the Player constructor does for itself, so a bare `new` is the whole of it here.
        Player mainPlayer = new Player();
        GameState.setPlayer(mainPlayer);

        // Not part of init_player(). A stub level so the game has somewhere to stand until level
        // generation is ported; in C the cave comes from generate_module and the level builders.
        // TODO: replace with real level generation
        Chunk cave = new Chunk("Current Level", 0, 0, 0, 0,
                0, false, 10, 10, 4, 3, 3,
                1, 1, 15, mainPlayer);
        GameState.setCave(cave);

        // The level-generation data, read from dungeon_profile.txt into DungeonRegistry. In C this
        // is one of the run_parser() calls in init_arrays ([C] src/generate.c:644), which also
        // signals the same progress message as it goes.
        GameEngine.getEventsBusHandler().eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays... (dungeon profiles)");
        DungeonLoader.loadDungeonProfiles();
        
        GameEngine.getEventsBusHandler().eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays... (room templates)");
        DungeonLoader.loadRoomTemplates();
        
        // The vaults, read from vault.txt into DungeonRegistry - one file covering the three vault
        // sizes, their newer variants and the interesting rooms. Must follow loadGameConstants
        // above, since vault.txt writes max-depth:0 to mean "no maximum" and resolving that needs
        // the world's maximum depth.
        GameEngine.getEventsBusHandler().eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays... (vault templates)");
        DungeonLoader.loadVaultTemplates();

        // Also not part of init_player(), but it has to follow the player: the queue binds to the
        // player it feeds commands to. C needs no equivalent step - its cmdq is a file-scope array
        // in cmd-core.c that reaches the player through the global instead.
        CommandQueue commandQueue = new CommandQueue(mainPlayer);
        GameState.setCommandQueue(commandQueue);
    }

    /**
     * The live event bus that game logic signals through.
     *
     * <p>Never {@code null}: a bus is installed at class load and only ever replaced, so callers
     * may signal without checking.
     *
     * @return the current event bus
     * @author Rowan Crowther
     */
    public static EventsHandler getEventsBusHandler() {
        return eventsBusHandler;
    }

    /**
     * Replace the live event bus - the injection seam for tests, which can install their
     * own {@link EventsBusHandler} (or a spy over one) to observe what gets signalled.
     *
     * <p>A bus installed here survives until {@link #initGame()} runs, which overwrites the field
     * with a fresh {@link EventsBusHandler} - so a test that installs a spy and then triggers the
     * first {@link #getGame()} loses it. Tests that never build an engine keep what they set.
     *
     * <p>The field is process-wide, so a test that swaps the bus should capture the previous one
     * and put it back afterwards, or it will leak into everything that runs later in the same JVM.
     *
     * @param eventsBusHandler the bus to install
     * @author Rowan Crowther
     */
    public static void setEventsBusHandler(EventsHandler eventsBusHandler) {
        GameEngine.eventsBusHandler = eventsBusHandler;
    }

    /**
     * Get the game engine singleton, building it - and so running the whole of
     * {@link #initGame()} - the first time this is called. Later calls just return the
     * existing instance.
     *
     * <p>Not thread-safe: the check-then-create is unsynchronised, so two threads
     * calling this at once could each build an engine and load the game data twice.
     * Safe as long as the call stays confined to the single game thread.
     *
     * @return the singleton game engine
     * @author Rowan Crowther
     */
    @CheckReturnValue
    public static GameEngine getGame() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }
}