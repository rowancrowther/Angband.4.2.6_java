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

package uk.co.jackoftrades.middle.gameinput;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

/**
 * The static switchboard for the {@link GameInput} boundary: it holds the one implementation currently
 * installed and hands it to callers. This is where C's file-scope {@code get_*_hook} function
 * pointers land in the port - the middle layer reaches the UI through {@code getInstance()} rather
 * than importing any front-end, so the dependency points inward.
 *
 * <p>A {@link DefaultGameInput} (the always-aborting stub, C's {@code else} branches) is installed
 * from the start, so {@code getInstance()} is never {@code null}. A front-end installs its real
 * implementation with {@link #setInstance}; a test installs a fake and calls {@link #resetInstance}
 * afterwards to restore the default.
 *
 * @author Rowan Crowther
 */
public final class GameInputHolder {

    /**
     * The implementation currently answering input requests; never {@code null}.
     */
    private static GameInput instance = new DefaultGameInput();

    private GameInputHolder() {
    }

    /**
     * Installs {@code instance} as the live implementation - used by a front-end at start-up or by a
     * test to swap in a fake.
     *
     * @param instance the implementation to install
     */
    public static void setInstance(GameInput instance) {
        GameInputHolder.instance = instance;
    }

    /**
     * Restores the always-installed {@link DefaultGameInput}, discarding whatever was set - typically
     * called by a test tearing down after {@link #setInstance}.
     */
    public static void resetInstance() {
        GameInputHolder.instance = new DefaultGameInput();
    }

    /**
     * @return the implementation currently answering input requests (never {@code null})
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static GameInput getInstance() {
        return instance;
    }
}
