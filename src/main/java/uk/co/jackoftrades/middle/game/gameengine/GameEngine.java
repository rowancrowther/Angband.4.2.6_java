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
import uk.co.jackoftrades.middle.game.event.EventsBusHandler;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

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
     * Initialise the middle end's subsystems, in the order they depend on each other:
     * game state, then the event bus, then the game constants loaded from
     * {@code lib/gamedata}.
     *
     * <p>The bus assignment here <em>replaces</em> the one installed at class load, giving each
     * newly built engine a bus with no handlers left over from before.
     *
     * <p>It is created <em>before</em> {@link GameConstants#init()} deliberately.
     * {@code GameConstants.init()} is this port's {@code init_angband()}
     * ({@code src/init.c}), the step C signals {@code EVENT_ENTER_INIT} from - so any
     * bus created after it would miss every event raised during loading, exactly as C
     * requires {@code init_display()} to precede {@code init_angband()} in
     * {@code main()} ({@code src/main.c}).
     *
     * <p>Not yet reached: C registers its handlers between those two calls. There is no
     * equivalent hook here, because this method both creates the bus and starts loading
     * with nothing in between, so the front end still has no point at which to register
     * for events raised during initialisation.
     *
     * @author Rowan Crowther
     */
    private void initGame() {
        GameState.initGameState();
        eventsBusHandler = new EventsBusHandler();
    }

    public void loadGameConstants() {
        GameConstants.init();
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