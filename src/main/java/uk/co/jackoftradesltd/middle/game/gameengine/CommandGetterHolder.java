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
import org.jetbrains.annotations.Contract;

/**
 * The static switchboard for the {@link CommandGetter} boundary: it holds the one implementation
 * currently installed and hands it to callers. This is where C's file-scope {@code cmd_get_hook}
 * function pointer lands in the port - the game loop reaches the UI through {@code getInstance()}
 * rather than importing any front-end, so the dependency points inward.
 *
 * <p>A {@link DefaultCommandGetter} (the always-fail stub) is installed from the start, so
 * {@code getInstance()} is never {@code null}. In C the pointer starts {@code NULL} and the one
 * call site, {@code cmd_get_hook(CTX_GAME)} in {@code play_game()} ({@code ui-game.c}), would crash
 * if no front-end had assigned it; the stub turns that crash into an empty queue. A front-end
 * installs its real implementation with {@link #setInstance} (C's {@code cmd_get_hook =
 * textui_get_cmd} in {@code main.c}); a test installs a fake and calls {@link #resetInstance}
 * afterwards to restore the default.
 *
 * <p>Two deliberate differences from C. C lets the pointer be set back to {@code NULL}, whereas
 * {@link #setInstance} rejects {@code null}; {@link #resetInstance} is the way to clear it. And C
 * declares the pointer in {@code cmd-core.c} and never tests it at the call site, which here is
 * safe because the default stub is always installed.
 *
 * <p>It is the command-level twin of {@code GameInputHolder}, and is locked down the same way -
 * {@code final} with a private constructor - because a static switchboard is never subclassed or
 * instantiated.
 *
 * <p>Class CommandGetterHolder coded before 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public final class CommandGetterHolder {
    /**
     * Logs the rejected {@code null} in {@link #setInstance} before it throws.
     *
     * <p>Field logger coded on 261014, commented in full on 261014.
     */
    private static final Logger logger = LogManager.getLogger(CommandGetterHolder.class);

    /**
     * The implementation currently answering command-fetch requests; never {@code null}. It starts
     * as a {@link DefaultCommandGetter}, the equivalent of C's unset {@code cmd_get_hook}, and is
     * replaced by {@link #setInstance} or restored by {@link #resetInstance}. It is {@code volatile}
     * so that a swap made on one thread (a front-end installing itself, a test installing a fake) is
     * seen by the core thread that reads it through {@link #getInstance()}.
     *
     * <p>Field instance coded before 261014, commented in full on 261014.
     */
    private static volatile CommandGetter instance = new DefaultCommandGetter();

    /**
     * Not instantiable - all state and behaviour is static.
     *
     * <p>Constructor CommandGetterHolder coded before 261014, commented in full on 261014.
     */
    private CommandGetterHolder() {
    }

    /**
     * Hands out the installed implementation, so the game loop writes
     * {@code CommandGetterHolder.getInstance().getCommand(CTX_GAME, queue)} where C wrote
     * {@code cmd_get_hook(CTX_GAME)}. No check for an unset hook is needed at the call site, because
     * the default stub supplies the fall-back.
     *
     * <p>Function getInstance coded before 261014, commented in full on 261014.
     *
     * @return the implementation currently answering command-fetch requests (never {@code null})
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static CommandGetter getInstance() {
        return instance;
    }

    /**
     * Installs {@code instance} as the live implementation - used by a front-end at start-up or by a
     * test to swap in a fake. This is the port of C assigning {@code cmd_get_hook}. A {@code null}
     * argument is logged and rejected with an {@link IllegalArgumentException}, leaving the
     * installed implementation untouched, so the "never {@code null}" guarantee on
     * {@link #getInstance()} holds; C's way of unsetting the pointer is {@link #resetInstance()}.
     *
     * <p>Function setInstance coded before 261014, null rejection added on 261014, commented in full
     * on 261014.
     *
     * @param instance the implementation to install
     * @throws IllegalArgumentException if {@code instance} is {@code null}
     */
    public static void setInstance(CommandGetter instance) {
        if (instance == null) {
            String message = "Command Getter instance was null";
            logger.fatal(message);
            throw new IllegalArgumentException(message);
        }
        CommandGetterHolder.instance = instance;
    }

    /**
     * Restores the always-installed {@link DefaultCommandGetter}, discarding whatever was set -
     * typically called by a test tearing down after {@link #setInstance}. This is the equivalent of
     * C's {@code cmd_get_hook} being {@code NULL} again, except that the next fetch gets the stub's
     * failure instead of a crash.
     *
     * <p>Function resetInstance coded before 261014, commented in full on 261014.
     */
    public static void resetInstance() {
        instance = new DefaultCommandGetter();
    }
}
