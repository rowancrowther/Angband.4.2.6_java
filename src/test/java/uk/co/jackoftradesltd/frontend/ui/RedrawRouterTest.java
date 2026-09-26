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
import uk.co.jackoftradesltd.channel.messages.data.EventDataString;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStrings;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every {@code RedrawRouter} setter against what its own payload record is documented to carry,
 * not against re-reading {@link RedrawRouter}'s own body. {@link RedrawRouter#setHP} and
 * {@link RedrawRouter#setSP} are pinned against the two things C's {@code prt_hp}/{@code prt_sp}
 * and {@code get_panel_topleft} ({@code [C] ui-display.c} and {@code [C] ui-player.c}) rely on when
 * they read {@code player->chp}/{@code mhp} or {@code player->csp}/{@code msp} directly: which
 * value is current and which is the maximum, and that there is always a pair to read.
 * {@link RedrawRouter#setRaceClass} gets the same "which value lands where" treatment for
 * {@link EventDataStrings#strings()}'s two elements; {@link RedrawRouter#setTitle} and
 * {@link RedrawRouter#setName} each carry only one value, so there is nothing to transpose. There
 * is no C function to diverge from for any of them - this is the port's own translation step.
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
    private int savedCurrentSp;
    private int savedMaxSp;
    private String savedTitle;
    private String savedClassName;
    private String savedRaceName;
    private String savedName;

    @BeforeEach
    void saveModel() {
        savedCurrentHp = SidebarModel.getCurrentHP();
        savedMaxHp = SidebarModel.getMaxHP();
        savedCurrentSp = SidebarModel.getCurrentSP();
        savedMaxSp = SidebarModel.getMaxSP();
        savedTitle = SidebarModel.getTitle();
        savedClassName = SidebarModel.getClassName();
        savedRaceName = SidebarModel.getRaceName();
        savedName = SidebarModel.getName();
    }

    @AfterEach
    void restoreModel() {
        SidebarModel.setCurrentHP(savedCurrentHp);
        SidebarModel.setMaxHP(savedMaxHp);
        SidebarModel.setCurrentSP(savedCurrentSp);
        SidebarModel.setMaxSP(savedMaxSp);
        SidebarModel.setTitle(savedTitle);
        SidebarModel.setClassName(savedClassName);
        SidebarModel.setRaceName(savedRaceName);
        SidebarModel.setName(savedName);
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

    /**
     * {@link EventDataStat#current()} becomes {@link SidebarModel#getCurrentSP()} and
     * {@link EventDataStat#other()} becomes {@link SidebarModel#getMaxSP()} - the same shape as
     * {@link #currentAndMaxAreNotSwapped()}, for {@code EVENT_MANA} rather than {@code EVENT_HP}.
     */
    @Test
    void spCurrentAndMaxAreNotSwapped() {
        RedrawRouter.setSP(new EventDataStat(5, 40));

        assertEquals(5, SidebarModel.getCurrentSP(), "current() must land in currentSP");
        assertEquals(40, SidebarModel.getMaxSP(), "other() must land in maxSP");
    }

    /**
     * A later {@code EVENT_MANA} overwrites the model rather than merging with it, matching a
     * redraw always sending the player's whole current state.
     */
    @Test
    void aSecondSpMessageOverwritesTheFirst() {
        RedrawRouter.setSP(new EventDataStat(5, 40));
        RedrawRouter.setSP(new EventDataStat(2, 15));

        assertEquals(2, SidebarModel.getCurrentSP());
        assertEquals(15, SidebarModel.getMaxSP());
    }

    /**
     * A payload of the wrong shape is dropped rather than routed - the same guard
     * {@code RedrawHandlers.eventSP} applies on the sending side.
     */
    @Test
    void aNonStatSpPayloadLeavesTheModelUntouched() {
        RedrawRouter.setSP(new EventDataStat(5, 40));

        RedrawRouter.setSP(new EventDataBoolean(true));

        assertEquals(5, SidebarModel.getCurrentSP(), "a mismatched payload must not overwrite current");
        assertEquals(40, SidebarModel.getMaxSP(), "a mismatched payload must not overwrite max");
    }

    /**
     * {@link EventDataString#string()} becomes {@link SidebarModel#getTitle()}.
     */
    @Test
    void titleIsWrittenFromTheStringPayload() {
        RedrawRouter.setTitle(new EventDataString("Rogue"));

        assertEquals("Rogue", SidebarModel.getTitle());
    }

    /**
     * A payload of the wrong shape is dropped rather than routed, the same guard {@link #setHP}'s
     * own test pins for {@link RedrawRouter#setHP}.
     */
    @Test
    void aNonStringTitlePayloadLeavesTheModelUntouched() {
        RedrawRouter.setTitle(new EventDataString("Rogue"));

        RedrawRouter.setTitle(new EventDataBoolean(true));

        assertEquals("Rogue", SidebarModel.getTitle(), "a mismatched payload must not overwrite title");
    }

    /**
     * {@link EventDataString#string()} becomes {@link SidebarModel#getName()}.
     */
    @Test
    void nameIsWrittenFromTheStringPayload() {
        RedrawRouter.setName(new EventDataString("Legolas"));

        assertEquals("Legolas", SidebarModel.getName());
    }

    /**
     * A payload of the wrong shape is dropped rather than routed.
     */
    @Test
    void aNonStringNamePayloadLeavesTheModelUntouched() {
        RedrawRouter.setName(new EventDataString("Legolas"));

        RedrawRouter.setName(new EventDataBoolean(true));

        assertEquals("Legolas", SidebarModel.getName(), "a mismatched payload must not overwrite name");
    }

    /**
     * {@link EventDataStrings#strings()}'s first element becomes {@link SidebarModel#getRaceName()}
     * and its second becomes {@link SidebarModel#getClassName()} - deliberately distinct values, so
     * a router that swapped the pair would be caught rather than passing by coincidence.
     */
    @Test
    void raceAndClassAreNotSwapped() {
        RedrawRouter.setRaceClass(new EventDataStrings("Elf", "Ranger"));

        assertEquals("Elf", SidebarModel.getRaceName(), "strings()[0] must land in raceName");
        assertEquals("Ranger", SidebarModel.getClassName(), "strings()[1] must land in className");
    }

    /**
     * A payload of the wrong shape is dropped rather than routed.
     */
    @Test
    void aNonStringsRaceClassPayloadLeavesTheModelUntouched() {
        RedrawRouter.setRaceClass(new EventDataStrings("Elf", "Ranger"));

        RedrawRouter.setRaceClass(new EventDataBoolean(true));

        assertEquals("Elf", SidebarModel.getRaceName(), "a mismatched payload must not overwrite race");
        assertEquals("Ranger", SidebarModel.getClassName(), "a mismatched payload must not overwrite class");
    }
}
