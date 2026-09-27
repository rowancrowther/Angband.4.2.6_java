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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.TermData;
import uk.co.jackoftradesltd.frontend.ui.SidebarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The Java port of C's {@code side_handlers[]} table ({@code [C] ui-display.c}) - the sidebar rows
 * that redraw themselves in response to a {@code game_event_type} flag, as opposed to the "short"
 * topbar path ({@code update_topbar}, {@code [C] ui-display.c}), which this class does not cover.
 * C's table lists roughly twenty rows; only {@code prt_race} is ported and registered so far, so
 * {@link #sideHandlers} today holds a single {@link SideHandler}, with the rest joining one at a
 * time as each hook is ported.
 *
 * <p>Class HandlersHolder coded on 260927, commented in full on 260927.
 *
 * @author Rowan Crowther
 */
public class HandlersHolder {
    /**
     * The Java equivalent of C's {@code side_handlers[]} array - one {@link SideHandler} per row
     * that has been ported, built once by the static initialiser calling {@link #initHandlers()}.
     *
     * <p>Field sideHandlers coded on 260927, commented in full on 260927.
     */
    private static List<SideHandler> sideHandlers;

    /**
     * The terminal this holder's hooks draw to, written by {@link #setTermData(TermData)}. Stands
     * in for C's implicit target: C's {@code c_put_str} ({@code [C] z-term.c}) always writes to the
     * active {@code Term}, so the port has to be handed that reference explicitly instead.
     *
     * <p>Field term coded on 260927, commented in full on 260927.
     */
    private static Term term;

    static {
        sideHandlers = new ArrayList<>();
        initHandlers();
    }

    /**
     * Builds {@link #sideHandlers} - the port of C's {@code side_handlers[]} initializer
     * ({@code [C] ui-display.c}). Only the {@code prt_race} row is registered today, at the same
     * priority, {@code 19}, and against the same {@code EVENT_RACE_CLASS} flag C's table gives it;
     * the remaining rows join this method as their own hooks are ported.
     *
     * <p>Method initHandlers coded on 260927, commented in full on 260927.
     */
    private static void initHandlers() {
        SideHandler handler = new SideHandler(HandlersHolder::prtRace, 19, GameEventType.EVENT_RACE_CLASS);
        sideHandlers.add(handler);
    }

    /**
     * Draws the sidebar's race-name row - the port of C's {@code prt_race} ({@code [C]
     * ui-display.c}), which blanks the field for a shapechanged player and otherwise writes
     * {@code player->race->name}.
     *
     * <p><b>Outstanding:</b> the shapechanged branch is not wired up yet. C re-checks
     * {@code player_is_shapechanged(player)} against the live global on every draw, but this port
     * reads {@link SidebarModel#isPlayerIsShapechanged()}, and nothing currently writes that flag -
     * {@code RedrawRouter.setRaceClass} only carries the race and class names across the boundary,
     * and the {@code EVENT_RACE_CLASS} payload it reads (built in {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_MISC} arm) never packs a shapechanged flag in the first place. So this branch is
     * currently unreachable and the race name is always drawn, even for a shapechanged player -
     * deliberately not yet implemented, not a discrepancy to fix here.
     *
     * <p>Method prtRace coded on 260927, commented in full on 260927.
     *
     * @param row the row to draw at
     * @param col the column to draw at
     * @return a placeholder value; see {@link SideHandler#getResult(int, int)}
     */
    private static int prtRace(int row, int col) {
        if (SidebarModel.isPlayerIsShapechanged())
            prtField("", row, col);
        else
            prtField(SidebarModel.getRaceName(), row, col);

        return 1;
    }

    /**
     * Draws a 13-character sidebar field - the port of C's {@code prt_field} ({@code [C]
     * ui-display.c}), which blanks the field with 13 spaces before writing the new text over it, so
     * a shorter replacement value never leaves stray characters from a longer previous one.
     *
     * <p>Method prtField coded on 260927, commented in full on 260927.
     *
     * @param text the text to write, or {@code ""} to leave the field blank
     * @param row  the row to draw at
     * @param col  the column to draw at
     */
    private static void prtField(String text, int row, int col) {
        // Dump 13 spaces to clear
        term.cPutStr(ColourEnum.COLOUR_WHITE, " ".repeat(13), row, col);

        // Output the text
        term.cPutStr(ColourEnum.COLOUR_LIGHT_BLUE, text, row, col);
    }

    /**
     * Writes the {@link Term} that {@link #prtField(String, int, int)} draws to. Has no single C
     * counterpart - C's drawing calls reach the active {@code Term} directly, with nothing to hand
     * it in.
     *
     * <p>Method setTermData coded on 260927, commented in full on 260927.
     *
     * @param termData the wrapper this holder reads its {@link Term} out of
     */
    public void setTermData(TermData termData) {
        this.term = termData.getTerm();
    }
}
