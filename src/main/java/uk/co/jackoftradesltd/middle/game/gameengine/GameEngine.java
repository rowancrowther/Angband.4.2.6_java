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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.game.event.EventsBusHandler;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.loaders.DungeonLoader;
import uk.co.jackoftradesltd.middle.player.Player;

/**
 * The top-level game runtime: a singleton that performs middle-end start-up - game
 * state, the event bus and the game constants - roughly the Java counterpart of the C
 * original's initialization bootstrap, {@code init_angband()} ({@code src/init.c}) as
 * called from {@code main()} ({@code src/main.c}).
 *
 * <p><b>Built once, on the game thread.</b> {@link Core#gameLoop()} reaches
 * {@link #getGame()} as its first act and holds what it gets, so the engine is
 * constructed on {@code angband-core} and everything it initializes is confined to
 * that thread. No other live path builds one, and the front end cannot: it names
 * nothing of the middle end.
 *
 * <p><b>Two steps, as in C.</b> Building the engine ({@link #getGame()}) only stands up the event
 * bus; the data load waits in {@link #loadGameConstants(Core)}. C splits start-up the same way,
 * with {@code init_display()} between creating the event machinery and {@code init_angband()}
 * ({@code src/main.c}), and the gap is what lets the caller register handlers before the load
 * starts raising events.
 *
 * <p>The bus is held in a static field and the engine in a static singleton, so everything here is
 * shared across the JVM; a test that touches either must put it back afterwards.
 *
 * <p>Class GameEngine coded before 261010, commented in full on 261010.
 *
 * @author Rowan Crowther
 */
public class GameEngine {
    /**
     * Logger for start-up diagnostics. Nothing in this class writes to it at present; it is kept so
     * the engine has somewhere to report to once start-up grows steps that can fail softly. C has
     * no counterpart: its {@code init_angband()} reports only through {@code event_signal_string()}
     * and {@code quit()}.
     *
     * <p>Field logger coded before 261010, commented in full on 261010.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The live event bus, held here as the game-wide boundary other layers reach through
     * {@link #getEventsBusHandler()}. Typed to the {@link EventsHandler} interface so a
     * test can swap in its own bus via {@link #setEventsBusHandler(EventsHandler)}.
     *
     * <p><b>Never null.</b> A working bus is installed here, at class load, so any caller can
     * signal an event without first arranging for one - the same guarantee
     * {@code GameInputHolder} gives its boundary by installing a default instance at declaration.
     * {@link #initGame()} replaces it with a fresh one, so a game that starts gets a clean bus,
     * but the field is never empty in between.
     *
     * <p>It was previously assigned only inside {@code initGame()}, which left it null for
     * anything that ran without building an engine. Since callers signal through it unguarded,
     * that surfaced as {@code NullPointerException}s in every test that exercised game logic
     * without first standing up a bus of its own.
     *
     * <p>C keeps its handler table as file-scope statics in {@code game-event.c}, reachable by
     * any file that includes {@code game-event.h}; this static field plays the same role.
     *
     * <p>Field eventsBusHandler coded before 261010, commented in full on 261010.
     */
    private static EventsHandler eventsBusHandler = new EventsBusHandler();

    /**
     * The singleton, built on first {@link #getGame()}. Null until then. C has no equivalent: its
     * start-up state is a set of globals rather than an object, so there is nothing to hold.
     *
     * <p>Field instance coded before 261010, commented in full on 261010.
     */
    private static GameEngine instance;


    /**
     * Private constructor - the singleton is reached through {@link #getGame()}, never
     * built directly. All it does is run {@link #initGame()}, which replaces the event bus; no
     * data file is read here.
     *
     * <p>Constructor GameEngine coded before 261010, commented in full on 261010.
     */
    private GameEngine() {
        initGame();
    }

    /**
     * Get the game engine singleton, building it - and so running the whole of
     * {@link #initGame()} - the first time this is called. Later calls just return the
     * existing instance.
     *
     * <p>Not thread-safe: the check-then-create is unsynchronized, so two threads
     * calling this at once could each build an engine and load the game data twice.
     * Safe as long as the call stays confined to the single game thread.
     *
     * <p>Method getGame coded before 261010, commented in full on 261010.
     *
     * @return the singleton game engine
     */
    @CheckReturnValue
    public static GameEngine getGame() {
        if (instance == null) instance = new GameEngine();
        return instance;
    }

    /**
     * Read every file under {@code lib/gamedata} into the registries and then stand up the objects
     * that depend on what was read - this port's {@code init_angband()} ({@code [C] src/init.c}).
     *
     * <p>Separate from {@link #initGame()}, and that separation is the point. The bus is created in
     * {@code initGame()} but the load is deferred to here, which gives the caller a window between
     * the two to register handlers - exactly the gap C leaves between {@code init_display()} and
     * {@code init_angband()}. {@code Core.gameLoop()} uses it to wire {@code InitHandlers}
     * before the load raises {@code EVENT_ENTER_INIT} from inside it.
     *
     * <p>The two halves below mirror C's own two halves of {@code init_angband()}:
     * {@link GameConstants#init(Core)} covers both {@code init_game_constants()} and the data-file
     * parsing C does in its {@code arrays_module}, and the player creation that follows is
     * {@code player_module.init} - that is, {@code init_player()} ({@code [C] src/player.c}),
     * which C's module table runs immediately after {@code arrays_module}
     * ({@code [C] src/init.c}, the {@code modules[]} table).
     *
     * <p><b>That order is a dependency, not a convention.</b> C's {@code init_player()} sizes the
     * pack from {@code z_info->pack_size}, the quiver from {@code quiver_size}, and the
     * rune-knowledge arrays from {@code brand_max}/{@code slay_max}/{@code curse_max} - all values
     * that exist only once {@code constants.txt} has been read. The port's {@link Player}
     * constructor uses growable collections and so does not depend on them <em>yet</em>, but it
     * will as soon as inventory and rune knowledge are ported; creating the player after the load
     * keeps the port correct in advance rather than after the fact.
     *
     * <p><b>Order against C's {@code modules[]} table.</b> C runs {@code arrays_module}, then
     * {@code player_module}, then {@code generate_module}; this method runs the same three in that
     * order. The third is the one that is easy to misplace: dungeon profiles, room templates and
     * vaults are <em>not</em> read by C's {@code init_arrays()} but by {@code generate_module}'s
     * {@code run_template_parser()} ({@code [C] src/generate.c}), which is why they follow the
     * player here and not {@link GameConstants#init(Core)}. They announce themselves with the same
     * three {@code EVENT_INITSTATUS} strings C does.
     *
     * <p><b>What C's {@code init_angband()} does that this method does not.</b> The remaining
     * modules ({@code z_quark_module}, {@code messages_module}, {@code ui_visuals_module},
     * {@code rune_module}, {@code obj_make_module}, {@code ignore_module}, {@code mon_make_module},
     * {@code store_module}, {@code options_module}, {@code ui_player_module},
     * {@code ui_equip_cmp_module}), then {@code monster_list_init()}, {@code object_list_init()} and
     * {@code Rand_init()} with their "Initializing other stuff..." and "Getting the dice rolling..."
     * progress messages, are not run from this method. Each is the business of a later chapter of
     * the port, so the absence is recorded here rather than treated as a mismatch.
     *
     * <p><b>The return value is inverted from C's.</b> C's {@code init_angband()} always returns
     * {@code true}; here {@code true} means "stopped early", so that the caller can end the thread.
     * The name {@code loadGameConstants} is also narrower than the job: this is the port of the
     * whole of {@code init_angband()}, not of {@code init_game_constants()} alone.
     *
     * <p><b>The load can be cut short by a stop request.</b> Part-way through,
     * {@link GameConstants#init(Core)} waits on the core's inbox for the front end's
     * {@code UIEntriesLoaded} message and hands any other message it reads to
     * {@link Core#handleChannelOutput(uk.co.jackoftradesltd.channel.messages.UIMessage)}. If that
     * says to stop, the load is abandoned and this method returns {@code true} straight away,
     * before the player, level or command queue are built, so {@link GameState} is left as it was.
     * The {@code STOPPED} reply has been sent already; the caller only has to end the thread.
     *
     * <p>Long-running and file-bound, so it belongs on the game thread. A failure while reading a
     * data file, and an interrupt during the wait for {@code UIEntriesLoaded}, are both caught
     * inside {@code GameConstants.init(Core)} and rethrown as a {@code RuntimeException} naming the
     * file, so neither returns normally. An interrupt is therefore reported as a data-load error,
     * which is what happens today if the window is closed while the game is still starting up.
     *
     * <p>On a normal return, every call replaces the player, level and command queue held in
     * {@link GameState}, so calling this twice on one engine discards the first set entirely.
     *
     * <p>Method loadGameConstants coded before 261010, commented in full on 261010.
     *
     * @param core the core whose inbox the load reads while it waits for the front end
     * @return {@code true} if a stop request was handled during the load and the caller should end
     *         the game thread without going on; {@code false} once the data is loaded and the
     *         player, level and command queue are built
     */
    public boolean loadGameConstants(Core core) {
        if (GameConstants.init(core)) return true;

        // The port of init_player() ([C] src/player.c): allocate the player and its sub-structs.
        // Everything C mem_zallocs there - upkeep, the timed-effect table, obj_k, the default
        // options - the Player constructor does for itself, so a bare `new` is the whole of it here.
        Player mainPlayer = new Player();
        GameState.setPlayer(mainPlayer);

        // Not part of init_player(). A stub level so the game has somewhere to stand until level
        // generation is ported; in C the cave comes from generate_module and the level builders.
        // TODO: replace with real level generation
        Chunk cave = new Chunk("Current Level", 0, 0, 0, 0,
                0, false, 10, 10, 4, 3, 3,
                1, -1, 15, mainPlayer);
        cave.setCurrentLevel(cave);
        GameState.setCave(cave);

        // The level-generation data, read from dungeon_profile.txt into DungeonRegistry. In C this
        // is the first of the three run_parser() calls in run_template_parser(), the init of
        // generate_module ([C] src/generate.c) - not init_arrays() - which signals the same
        // progress message as it goes.
        GameEngine.getEventsBusHandler().eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays... (dungeon profiles)");
        DungeonLoader.loadDungeonProfiles();
        
        GameEngine.getEventsBusHandler().eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays... (room templates)");
        DungeonLoader.loadRoomTemplates();
        
        // The vaults, read from vault.txt into DungeonRegistry - one file covering the three vault
        // sizes, their newer variants and the interesting rooms. Must follow GameConstants.init(),
        // since vault.txt writes max-depth:0 to mean "no maximum" and resolving that needs the
        // world's maximum depth.
        GameEngine.getEventsBusHandler().eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays... (vaults)");
        DungeonLoader.loadVaultTemplates();

        // Also not part of init_player(), but it has to follow the player: the queue binds to the
        // player it feeds commands to. C needs no equivalent step - its cmdq is a file-scope array
        // in cmd-core.c that reaches the player through the global instead.
        CommandQueue commandQueue = new CommandQueue(mainPlayer);
        GameState.setCommandQueue(commandQueue);

        return false;
    }

    /**
     * The live event bus that game logic signals through.
     *
     * <p>Never {@code null}: a bus is installed at class load and only ever replaced, so callers
     * may signal without checking.
     *
     * <p>Method getEventsBusHandler coded before 261010, commented in full on 261010.
     *
     * @return the current event bus
     */
    public static EventsHandler getEventsBusHandler() {
        return eventsBusHandler;
    }

    /**
     * Replace the live event bus - the injection boundary for tests, which can install their
     * own {@link EventsBusHandler} (or a spy over one) to observe what gets signalled.
     *
     * <p>A bus installed here survives until {@link #initGame()} runs, which overwrites the field
     * with a fresh {@link EventsBusHandler} - so a test that installs a spy and then triggers the
     * first {@link #getGame()} loses it. Tests that never build an engine keep what they set.
     *
     * <p>The field is process-wide, so a test that swaps the bus should capture the previous one
     * and put it back afterwards, or it will leak into everything that runs later in the same JVM.
     *
     * <p>Method setEventsBusHandler coded before 261010, commented in full on 261010.
     *
     * @param eventsBusHandler the bus to install
     */
    public static void setEventsBusHandler(EventsHandler eventsBusHandler) {
        GameEngine.eventsBusHandler = eventsBusHandler;
    }

    /**
     * Initialize the middle end far enough that events can be signalled: install a fresh event
     * bus, and nothing else. The data load itself is deliberately <em>not</em> here - it waits in
     * {@link #loadGameConstants(Core)} so the caller gets a window to register handlers first.
     *
     * <p>The bus assignment <em>replaces</em> the one installed at class load, giving each newly
     * built engine a bus with no handlers left over from before.
     *
     * <p>The bus is created <em>before</em> the load deliberately. {@code EVENT_ENTER_INIT} is
     * signalled from inside {@link GameConstants#init(Core)}, which {@link #loadGameConstants(Core)}
     * calls first; C signals the same event as the first act of {@code init_angband()}
     * ({@code [C] src/init.c}) and requires {@code init_display()} to have registered its handlers
     * beforehand in {@code main()} ({@code [C] src/main.c}). A bus created after the load began
     * would miss every event raised during it.
     *
     * <p>The gap C leaves between those two calls for registering handlers exists here too: it
     * is the space between an engine being built and {@code loadGameConstants()} being called, and
     * {@code Core.gameLoop()} is what uses it.
     *
     * <p>History: this once called {@link GameState#initGameState()} as well, which built a
     * player, level and command queue before the data load only for the load to discard them. That
     * method is now an empty body and this no longer calls it, so no pre-load set is made;
     * {@link #loadGameConstants(Core)} builds the only set.
     *
     * <p>Method initGame coded before 261010, commented in full on 261010.
     */
    private void initGame() {
        eventsBusHandler = new EventsBusHandler();
    }
}