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

package uk.co.jackoftradesltd.frontend.screen.grid;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import javax.swing.*;
import java.util.List;

/**
 * The owner of the screen's live state, and the only route out of it — the third of the three
 * concerns recorded in {@code docs/UIPanelArchitecture.md}: {@link CellGrid} is the data,
 * {@link Region} is the operations, and this class is the boundary between the thread that
 * composes a screen and the thread that paints it. No C original: this class is new
 * architecture, not a port.
 *
 * <p>{@link #live} is mutated freely by whatever holds this {@code Screen} — through
 * {@link #root()} and the {@link Region} it returns — on the composing thread only. Nothing
 * about that mutation is thread-safe, by design: safety comes from a single owner, not from
 * synchronisation, per {@code docs/UIPanelArchitecture.md}'s "Option B" ("Who owns the grid").
 *
 * <p>{@link #frame()} is the one crossing point. It publishes an immutable {@link Frame} —
 * a defensive copy of both {@link #live} and {@link #hotspots} — for handing to the event
 * dispatch thread, so the invariant "once a frame is handed to the EDT, the composing thread
 * must never touch that grid again" is enforced here in one place rather than trusted at
 * every call site.
 *
 * <p>Class Screen coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
public class Screen {
    /**
     * The screen's live contents, mutated in place by the composing thread through
     * {@link #root()}. Never handed out directly — {@link #frame()} always hands out a copy.
     *
     * <p>Field live coded on 260909, commented in full on 260909.
     */
    private final CellGrid live;
    /**
     * The clickable regions currently live on {@link #live}, mutated in place the same way.
     * Never handed out directly — {@link #frame()} always hands out a copy via
     * {@link List#copyOf}.
     *
     * <p>Field hotspots coded on 260909, commented in full on 260909.
     */
    private final List<Hotspot> hotspots;

    private Frame frame;

    /**
     * Wraps an existing grid and hotspot list as the live screen. Neither argument is copied —
     * this constructor trusts the caller to hand over ownership, not merely a reference it
     * intends to keep mutating from elsewhere.
     *
     * <p>Constructor Screen coded on 260909, commented in full on 260909.
     *
     * @param live     the grid this screen owns and mutates
     * @param hotspots the hotspot list this screen owns and mutates
     */
    public Screen(CellGrid live, List<Hotspot> hotspots) {

        this.live = live;
        this.hotspots = hotspots;
        frame = new Frame(live, hotspots);
    }

    /**
     * A region spanning the whole of {@link #live}, at offset {@code (0, 0)} and sized to
     * exactly match the grid. This is the entry point the composing thread draws through —
     * every write to the screen passes through the {@link Region} this returns, or a
     * sub-rectangle built from it elsewhere.
     *
     * <p>Method root coded on 260909, commented in full on 260909.
     *
     * @return a region covering the entire live grid
     */
    public Region root() {
        return new Region(live, 0, 0, live.rows(), live.cols());
    }

    /**
     * An immutable snapshot of the screen, safe to publish to the event dispatch thread.
     * Copies both halves of the live state — {@link CellGrid#copy()} for the grid,
     * {@link List#copyOf} for the hotspots — so the returned {@link Frame} can never be
     * mutated by anything this {@code Screen} goes on to do afterwards.
     *
     * <p>Must be called on the composing thread, and the result posted rather than built
     * inside the {@code invokeLater} lambda itself — calling it there would hand the EDT a
     * frame built on its own thread instead of the composing thread's, per
     * {@code docs/UIPanelArchitecture.md}'s "The flush point".
     *
     * <p>Method frame coded on 260909, commented in full on 260909.
     *
     * @return an independent snapshot of the current grid and hotspots
     */
    public Frame frame() {
        return new Frame(live.copy(), List.copyOf(hotspots));
    }

    public void splashScreenNote(String eventMessage) {
        int row = 23;

        // clear the status line
        this.root().erase(row, 0, 80);

        String toWrite = String.format("[%s]", eventMessage);
        int col = (80 - toWrite.length()) / 2;

        this.root().put(row, col, toWrite, ColourEnum.COLOUR_WHITE);
    }

    /**
     * Queue a block to run on Swing's event dispatch thread.
     *
     * <p>A named wrapper over {@code SwingUtilities.invokeLater} rather than the call itself, so the
     * painting methods read as a statement of where the work goes. {@code invokeLater} and not
     * {@code invokeAndWait}: the UI thread has no reason to wait for a repaint, and waiting is
     * how it would deadlock if the EDT ever came to need something from it.
     *
     * @param event the block to run on the EDT
     */
    private void onEventDispatchThread(Runnable event) {
        SwingUtilities.invokeLater(event);
    }
}
