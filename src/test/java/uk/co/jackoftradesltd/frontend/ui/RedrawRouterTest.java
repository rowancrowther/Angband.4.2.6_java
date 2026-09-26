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

package uk.co.jackoftradesltd.frontend.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.messages.data.EventDataBoolean;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link RedrawRouter#setHP} against the two things C's {@code prt_hp} and {@code get_panel_topleft}
 * ({@code [C] ui-display.c} and {@code [C] ui-player.c}) rely on when they read
 * {@code player->chp}/{@code mhp} directly: which value is current and which is the maximum, and
 * that there is always a pair to read. There is no C function to diverge from here - this is the
 * port's own translation step - so the expected values are derived from what
 * {@link EventDataStat#current()}/{@link EventDataStat#other()} are documented to carry, not from
 * re-reading {@link RedrawRouter}'s own body.
 *
 * <p>Class RedrawRouterTest coded on 260926, commented in full on 260926.
 *
 * @author Rowan Crowther
 */
class RedrawRouterTest {

    /**
     * {@link SidebarModel}'s state before this test ran, since it is static and shared across the
     * whole suite.
     */
    private int savedCurrentHp;
    private int savedMaxHp;

    @BeforeEach
    void saveModel() {
        savedCurrentHp = SidebarModel.getCurrentHP();
        savedMaxHp = SidebarModel.getMaxHP();
    }

    @AfterEach
    void restoreModel() {
        SidebarModel.setCurrentHP(savedCurrentHp);
        SidebarModel.setMaxHP(savedMaxHp);
    }

    /**
     * {@link EventDataStat#current()} becomes {@link SidebarModel#getCurrentHP()} and
     * {@link EventDataStat#other()} becomes {@link SidebarModel#getMaxHP()} - deliberately distinct
     * values, so a router that swapped the pair would be caught rather than passing by
     * coincidence.
     */
    @Test
    void currentAndMaxAreNotSwapped() {
        RedrawRouter.setHP(new EventDataStat(7, 99));

        assertEquals(7, SidebarModel.getCurrentHP(), "current() must land in currentHP");
        assertEquals(99, SidebarModel.getMaxHP(), "other() must land in maxHP");
    }

    /**
     * A later {@code EVENT_HP} overwrites the model rather than merging with it, matching a redraw
     * always sending the player's whole current state.
     */
    @Test
    void aSecondMessageOverwritesTheFirst() {
        RedrawRouter.setHP(new EventDataStat(7, 99));
        RedrawRouter.setHP(new EventDataStat(3, 40));

        assertEquals(3, SidebarModel.getCurrentHP());
        assertEquals(40, SidebarModel.getMaxHP());
    }

    /**
     * A payload of the wrong shape is dropped rather than routed - the same guard
     * {@code RedrawHandlers.eventHP} applies on the sending side. Nothing sends {@code EVENT_HP}
     * this way today, but the model must be left exactly as it was rather than reading garbage
     * out of an unrelated record.
     */
    @Test
    void aNonStatPayloadLeavesTheModelUntouched() {
        RedrawRouter.setHP(new EventDataStat(7, 99));

        RedrawRouter.setHP(new EventDataBoolean(true));

        assertEquals(7, SidebarModel.getCurrentHP(), "a mismatched payload must not overwrite current");
        assertEquals(99, SidebarModel.getMaxHP(), "a mismatched payload must not overwrite max");
    }
}
