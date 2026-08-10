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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

/**
 * The static switchboard for the {@link CommandGetter} boundary: it holds the one implementation
 * currently installed and hands it to callers. This is where C's file-scope {@code cmd_get_hook}
 * function pointer lands in the port - the game loop reaches the UI through {@code getInstance()}
 * rather than importing any front-end, so the dependency points inward.
 *
 * <p>A {@link DefaultCommandGetter} (the always-fail stub, C's unset pointer) is installed from the
 * start, so {@code getInstance()} is never {@code null}. A front-end installs its real implementation
 * with {@link #setInstance} (C's {@code cmd_get_hook = textui_get_cmd}); a test installs a fake and
 * calls {@link #resetInstance} afterwards to restore the default.
 *
 * <p>It is the command-level twin of {@code GameInputHolder}, and is locked down the same way -
 * {@code final} with a private constructor - because a static switchboard is never subclassed or
 * instantiated.
 *
 * @author Rowan Crowther
 */
public final class CommandGetterHolder {

    /**
     * The implementation currently answering command-fetch requests; never {@code null}.
     */
    private static CommandGetter instance = new DefaultCommandGetter();

    /**
     * Not instantiable - all state and behaviour is static.
     */
    private CommandGetterHolder() {
    }

    /**
     * @return the implementation currently answering command-fetch requests (never {@code null})
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static CommandGetter getInstance() {
        return instance;
    }

    /**
     * Installs {@code instance} as the live implementation - used by a front-end at start-up or by a
     * test to swap in a fake.
     *
     * @param instance the implementation to install
     */
    public static void setInstance(CommandGetter instance) {
        CommandGetterHolder.instance = instance;
    }

    /**
     * Restores the always-installed {@link DefaultCommandGetter}, discarding whatever was set -
     * typically called by a test tearing down after {@link #setInstance}.
     */
    public static void resetInstance() {
        instance = new DefaultCommandGetter();
    }
}
