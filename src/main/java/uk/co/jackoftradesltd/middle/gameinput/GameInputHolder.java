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

package uk.co.jackoftradesltd.middle.gameinput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

/**
 * The static switchboard for the {@link GameInput} boundary: it holds the one implementation currently
 * installed and hands it to callers. This is where C's file-scope {@code get_*_hook} function
 * pointers (declared in {@code game-input.c}) land in the port - the middle layer reaches the UI
 * through {@code getInstance()} rather than importing any front-end, so the dependency points inward.
 *
 * <p>A {@link DefaultGameInput} (the always-aborting stub, C's {@code else} branches) is installed
 * from the start, so {@code getInstance()} is never {@code null}. That matches C, where every hook
 * pointer starts {@code NULL} and each wrapper falls back when its pointer is unset. A front-end
 * installs its real implementation with {@link #setInstance}, as C's {@code textui_input_init()} in
 * {@code ui-input.c} assigns the hooks; a test installs a fake and calls {@link #resetInstance}
 * afterwards to restore the default.
 *
 * <p>Two deliberate differences from C. First, the hooks are installed as one object, so a fake that
 * wants to override a single hook (as the C tests do with {@code panel_contains_hook}) extends
 * {@link DefaultGameInput} and overrides just that method. Second, C lets a hook be set back to
 * {@code NULL}, whereas {@link #setInstance} rejects {@code null}; {@link #resetInstance} is the
 * equivalent of clearing the pointers.
 *
 * <p>It is the input-level twin of {@code CommandGetterHolder} and is locked down the same way -
 * {@code final} with a private constructor - because a static switchboard is never subclassed or
 * instantiated.
 *
 * <p>Class GameInputHolder coded before 261012, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public final class GameInputHolder {
    /**
     * Logs the rejected {@code null} in {@link #setInstance} before it throws.
     *
     * <p>Field logger coded before 261012, commented in full on 261012.
     */
    private final static Logger logger = LogManager.getLogger(GameInputHolder.class);

    /**
     * The implementation currently answering input requests; never {@code null}. It starts as a
     * {@link DefaultGameInput}, the equivalent of every C hook pointer being unset, and is replaced
     * by {@link #setInstance} or restored by {@link #resetInstance}. It is {@code volatile} so that a
     * swap made on one thread (a front-end installing itself, a test installing a fake) is seen by
     * the core thread that reads it through {@link #getInstance()}.
     *
     * <p>Field instance coded before 261012, commented in full on 261012.
     */
    private static volatile GameInput instance = new DefaultGameInput();

    /**
     * Not instantiable - all state and behaviour is static.
     *
     * <p>Constructor GameInputHolder coded before 261012, commented in full on 261012.
     */
    private GameInputHolder() {
    }

    /**
     * Installs {@code instance} as the live implementation - used by a front-end at start-up or by a
     * test to swap in a fake. This is the port of C assigning the {@code get_*_hook} pointers in
     * {@code textui_input_init()}, except that all of them change together. A {@code null} argument
     * is logged and rejected with an {@link IllegalArgumentException} so the "never {@code null}"
     * guarantee on {@link #getInstance()} holds; C's way of unsetting the hooks is
     * {@link #resetInstance()}.
     *
     * <p>Function setInstance coded before 261012, commented in full on 261012.
     *
     * @param instance the implementation to install
     * @throws IllegalArgumentException if {@code instance} is {@code null}
     */
    public static void setInstance(GameInput instance) {
        if (instance == null) {
            logger.fatal("Cannot set GameInputHolder instance to null.");
            throw new IllegalArgumentException("Cannot set GameInputHolder instance to null.");
        }
        
        GameInputHolder.instance = instance;
    }

    /**
     * Restores the always-installed {@link DefaultGameInput}, discarding whatever was set - typically
     * called by a test tearing down after {@link #setInstance}. This is the equivalent of every C
     * hook pointer being {@code NULL} again, so each request falls back to its absent-hook behaviour.
     *
     * <p>Function resetInstance coded before 261012, commented in full on 261012.
     */
    public static void resetInstance() {
        GameInputHolder.instance = new DefaultGameInput();
    }

    /**
     * Hands out the installed implementation, so callers write
     * {@code GameInputHolder.getInstance().getCheck(...)} where C wrote {@code get_check(...)} and
     * tested the hook inside the wrapper. No check for an unset hook is needed at the call site,
     * because the default stub supplies the fall-back.
     *
     * <p>Function getInstance coded before 261012, commented in full on 261012.
     *
     * @return the implementation currently answering input requests (never {@code null})
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static GameInput getInstance() {
        return instance;
    }
}
