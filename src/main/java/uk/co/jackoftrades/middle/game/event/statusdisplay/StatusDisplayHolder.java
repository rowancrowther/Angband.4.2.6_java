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

package uk.co.jackoftrades.middle.game.event.statusdisplay;

/**
 * The registration point for the front end's {@link StatusDisplay}: one process-wide slot the
 * middle end reads and the front end writes.
 *
 * <p>Same shape as the port's other UI boundaries ({@code CommandGetterHolder}, {@code GameInputHolder})
 * and for the same reason. The middle end must be able to reach the display from deep inside
 * {@code GameConstants.init()} without that code holding a reference to the front end, and C solves
 * the same problem with a file-scope function pointer installed by {@code init_display()}
 * ({@code [C] src/ui-display.c}). A static slot is the direct port of that.
 *
 * <p>The slot starts holding a {@link DefaultStatusDisplay} rather than {@code null}, so callers
 * never need a null check. That default is what runs in every headless test and on any path where
 * the front end has not registered yet.
 *
 * <p><b>Not thread-safe.</b> {@link #instance} is a plain static, written by the front end on
 * Swing's event dispatch thread and read by the middle end on the game thread. That is safe today
 * only by accident of ordering: {@code Frontend.init} registers before {@code gameRunner.start()},
 * and {@link Thread#start()} publishes everything written before it. A registration made after the
 * game thread is running has no such guarantee and might never be seen - making the field
 * {@code volatile} is what would fix that.
 *
 * @author Rowan Crowther
 */
public final class StatusDisplayHolder {
    /**
     * The display in force. Never {@code null}: it starts as the no-op default and is only ever
     * replaced, never cleared.
     *
     * @author Rowan Crowther
     */
    private static StatusDisplay instance = new DefaultStatusDisplay();

    /**
     * Private constructor preventing instantiation of this static-only holder.
     *
     * @author Rowan Crowther
     */
    private StatusDisplayHolder() {

    }

    /**
     * The display the middle end should report progress to.
     *
     * <p>Read at the point of use rather than cached, which is what lets a front end register
     * after the handlers are wired and still receive the calls.
     *
     * @return the registered display, or the no-op default if none has been registered
     * @author Rowan Crowther
     */
    public static StatusDisplay getInstance() {
        return instance;
    }

    /**
     * Register the front end's display, replacing whatever was there. Called once during start-up,
     * by {@code Frontend.init}.
     *
     * @param instance the display to install; not checked for null, and a null would break every
     *                 later {@code getInstance} caller
     * @author Rowan Crowther
     */
    public static void setInstance(StatusDisplay instance) {
        StatusDisplayHolder.instance = instance;
    }

    /**
     * Put the no-op default back, discarding any registered display.
     *
     * <p>Exists for tests: the slot is process-wide, so a test that installs a fake has to undo it
     * or the fake follows the JVM into everything that runs afterwards. Builds a fresh default
     * rather than keeping one around, so no state can survive a reset.
     *
     * @author Rowan Crowther
     */
    public static void resetInstance() {
        instance = new DefaultStatusDisplay();
    }
}
