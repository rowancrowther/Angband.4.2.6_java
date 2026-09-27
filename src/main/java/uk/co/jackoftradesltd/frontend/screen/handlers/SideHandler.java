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

package uk.co.jackoftradesltd.frontend.screen.handlers;

import uk.co.jackoftradesltd.channel.enums.GameEventType;

import java.util.function.BiFunction;

/**
 * The Java port of C's anonymous {@code side_handler_t} struct ({@code [C] ui-display.c}), which
 * pairs a sidebar-row drawing hook with the priority that decides whether it is drawn when space is
 * tight and the {@code game_event_type} that triggers it. Each instance is one row of C's
 * {@code side_handlers[]} table; {@link HandlersHolder#initHandlers()} builds the Java equivalent of
 * that table, one {@code SideHandler} at a time, as each hook is ported.
 *
 * <p>Class SideHandler coded on 260927, commented in full on 260927.
 *
 * @author Rowan Crowther
 */
public class SideHandler {
    /**
     * The drawing hook itself - the port of C's {@code void (*hook)(int, int)} function pointer.
     * Typed as a {@link BiFunction} rather than a bespoke void-returning functional interface, so
     * the wrapped hook has to return something even though every C hook it stands in for is
     * {@code void}; see {@link #getResult(int, int)} for what the returned value means today.
     *
     * <p>Field hook coded on 260927, commented in full on 260927.
     */
    private BiFunction<Integer, Integer, Integer> hook;

    /**
     * The display priority - the port of C's {@code int priority}. A lower number is more
     * important and is always displayed; when sidebar space is tight, C drops the
     * highest-numbered rows first.
     *
     * <p>Field priority coded on 260927, commented in full on 260927.
     */
    private int priority;

    /**
     * The redraw event this hook answers - the port of C's {@code game_event_type type}, the
     * {@code PR_*} flag {@code side_handlers[]}'s row corresponds to.
     *
     * <p>Field type coded on 260927, commented in full on 260927.
     */
    private GameEventType type;

    /**
     * Builds one row of the sidebar handler table - the port of populating one entry of C's
     * {@code side_handlers[]} initializer ({@code [C] ui-display.c}), in the same
     * (hook, priority, type) order the C struct literal lists them in.
     *
     * <p>Constructor SideHandler coded on 260927, commented in full on 260927.
     *
     * @param hook     the drawing function this row invokes
     * @param priority the display priority, lower is more important
     * @param type     the redraw event that triggers this hook
     */
    public SideHandler(BiFunction<Integer, Integer, Integer> hook, int priority, GameEventType type) {
        this.hook = hook;
        this.priority = priority;
        this.type = type;
    }

    /**
     * Reads the display priority last set by the constructor.
     *
     * <p>Method getPriority coded on 260927, commented in full on 260927.
     *
     * @return this row's priority, lower is more important
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Invokes this row's drawing hook at the given row and column. C's hooks are all
     * {@code void (int, int)} and are called for their side effect of writing to the terminal; the
     * {@code int} this returns is a placeholder the port's {@link BiFunction} typing requires, not
     * something C's {@code side_handlers[]} dispatch reads back - today's only registered hook,
     * {@link HandlersHolder#prtRace(int, int)}, always returns {@code 1}.
     *
     * <p>Method getResult coded on 260927, commented in full on 260927.
     *
     * @param x the column to draw at
     * @param y the row to draw at
     * @return the hook's placeholder return value
     */
    public int getResult(int x, int y) {
        return hook.apply(x, y);
    }

    /**
     * Reads the redraw event type last set by the constructor.
     *
     * <p>Method getType coded on 260927, commented in full on 260927.
     *
     * @return the {@code game_event_type} this row's hook answers
     */
    public GameEventType getType() {
        return type;
    }
}
