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
import uk.co.jackoftradesltd.channel.messages.data.EventDataLong;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;
import uk.co.jackoftradesltd.channel.messages.data.EventDataString;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStrings;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Every {@code RedrawRouter} setter against what its own payload record is documented to carry,
 * not against re-reading {@link RedrawRouter}'s own body. {@link RedrawRouter#setHP},
 * {@link RedrawRouter#setSP} and {@link RedrawRouter#setPlayerLevel} are pinned against the two
 * things C's {@code prt_hp}/{@code prt_sp}/{@code prt_level} and {@code get_panel_topleft}
 * ({@code [C] ui-display.c} and {@code [C] ui-player.c}) rely on when they read
 * {@code player->chp}/{@code mhp}, {@code player->csp}/{@code msp} or
 * {@code player->lev}/{@code max_lev} directly: which value is current and which is the maximum,
 * and that there is always a pair to read. {@link RedrawRouter#setRaceClass} gets the same "which
 * value lands where" treatment for {@link EventDataStrings#strings()}'s three elements, and
 * {@link RedrawRouter#setTitle} for its four; {@link RedrawRouter#setName} carries only one value,
 * so there is nothing to transpose. There is no C function to diverge from for any of them - this
 * is the port's own translation step.
 *
 * <p>Class RedrawRouterTest coded on 260926, commented in full on 260928.
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
    private String savedShapeName;
    private boolean savedWizard;
    private boolean savedTotalWinner;
    private boolean savedShapechanged;
    private int savedLevel;
    private int savedMaxLevel;
    private long savedGold;

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
        savedShapeName = SidebarModel.getShapeName();
        savedWizard = SidebarModel.isWizard();
        savedTotalWinner = SidebarModel.isTotalWinner();
        savedShapechanged = SidebarModel.isPlayerIsShapechanged();
        savedLevel = SidebarModel.getLevel();
        savedMaxLevel = SidebarModel.getMaxLevel();
        savedGold = SidebarModel.getGold();
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
        SidebarModel.setShapeName(savedShapeName);
        SidebarModel.setWizard(savedWizard);
        SidebarModel.setTotalWinner(savedTotalWinner);
        SidebarModel.setPlayerIsShapechanged(savedShapechanged);
        SidebarModel.setLevel(savedLevel);
        SidebarModel.setMaxLevel(savedMaxLevel);
        SidebarModel.setGold(savedGold);
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
     * {@link EventDataStrings#strings()}'s four elements land in {@link SidebarModel#getTitle()},
     * {@link SidebarModel#isWizard()}, {@link SidebarModel#isTotalWinner()} and
     * {@link SidebarModel#getShapeName()} respectively - deliberately distinct values, so a router
     * that mis-ordered them would be caught rather than passing by coincidence.
     */
    @Test
    void titleWizardWinnerAndShapeAreNotSwapped() {
        RedrawRouter.setTitle(new EventDataStrings("Rogue", "true", "false", "Wolf"));

        assertEquals("Rogue", SidebarModel.getTitle(), "strings()[0] must land in title");
        assertEquals(true, SidebarModel.isWizard(), "strings()[1] must land in wizard");
        assertEquals(false, SidebarModel.isTotalWinner(), "strings()[2] must land in totalWinner");
        assertEquals("Wolf", SidebarModel.getShapeName(), "strings()[3] must land in shapeName");
    }

    /**
     * A payload of the wrong shape is dropped rather than routed, the same guard {@link #setHP}'s
     * own test pins for {@link RedrawRouter#setHP}.
     */
    @Test
    void aNonStringsTitlePayloadLeavesTheModelUntouched() {
        RedrawRouter.setTitle(new EventDataStrings("Rogue", "true", "false", "Wolf"));

        RedrawRouter.setTitle(new EventDataBoolean(true));

        assertEquals("Rogue", SidebarModel.getTitle(), "a mismatched payload must not overwrite title");
        assertEquals(true, SidebarModel.isWizard(), "a mismatched payload must not overwrite wizard");
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
     * {@link EventDataStrings#strings()}'s first element becomes {@link SidebarModel#getRaceName()},
     * its second {@link SidebarModel#getClassName()} and its third
     * {@link SidebarModel#isPlayerIsShapechanged()} - deliberately distinct values, so a router
     * that mis-ordered them would be caught rather than passing by coincidence.
     */
    @Test
    void raceClassAndShapechangedAreNotSwapped() {
        RedrawRouter.setRaceClass(new EventDataStrings("Elf", "Ranger", "true"));

        assertEquals("Elf", SidebarModel.getRaceName(), "strings()[0] must land in raceName");
        assertEquals("Ranger", SidebarModel.getClassName(), "strings()[1] must land in className");
        assertEquals(true, SidebarModel.isPlayerIsShapechanged(), "strings()[2] must land in playerIsShapechanged");
    }

    /**
     * A payload of the wrong shape is dropped rather than routed.
     */
    @Test
    void aNonStringsRaceClassPayloadLeavesTheModelUntouched() {
        RedrawRouter.setRaceClass(new EventDataStrings("Elf", "Ranger", "true"));

        RedrawRouter.setRaceClass(new EventDataBoolean(true));

        assertEquals("Elf", SidebarModel.getRaceName(), "a mismatched payload must not overwrite race");
        assertEquals("Ranger", SidebarModel.getClassName(), "a mismatched payload must not overwrite class");
        assertEquals(true, SidebarModel.isPlayerIsShapechanged(), "a mismatched payload must not overwrite shapechanged");
    }

    /**
     * {@link EventDataStat#current()} becomes {@link SidebarModel#getLevel()} and
     * {@link EventDataStat#other()} becomes {@link SidebarModel#getMaxLevel()} - deliberately
     * distinct values, so a router that swapped the pair would be caught rather than passing by
     * coincidence.
     */
    @Test
    void levelAndMaxLevelAreNotSwapped() {
        RedrawRouter.setPlayerLevel(new EventDataStat(9, 12));

        assertEquals(9, SidebarModel.getLevel(), "current() must land in level");
        assertEquals(12, SidebarModel.getMaxLevel(), "other() must land in maxLevel");
    }

    /**
     * A later {@code EVENT_PLAYERLEVEL} overwrites the model rather than merging with it, matching
     * a redraw always sending the player's whole current state.
     */
    @Test
    void aSecondLevelMessageOverwritesTheFirst() {
        RedrawRouter.setPlayerLevel(new EventDataStat(9, 12));
        RedrawRouter.setPlayerLevel(new EventDataStat(13, 13));

        assertEquals(13, SidebarModel.getLevel());
        assertEquals(13, SidebarModel.getMaxLevel());
    }

    /**
     * A payload of the wrong shape is dropped rather than routed, the same guard {@link #setHP}'s
     * own test pins for {@link RedrawRouter#setHP}.
     */
    @Test
    void aNonStatLevelPayloadLeavesTheModelUntouched() {
        RedrawRouter.setPlayerLevel(new EventDataStat(9, 12));

        RedrawRouter.setPlayerLevel(new EventDataBoolean(true));

        assertEquals(9, SidebarModel.getLevel(), "a mismatched payload must not overwrite level");
        assertEquals(12, SidebarModel.getMaxLevel(), "a mismatched payload must not overwrite maxLevel");
    }

    /**
     * {@link EventDataLong#value()} becomes {@link SidebarModel#getGold()}.
     */
    @Test
    void goldIsWrittenFromTheLongPayload() {
        RedrawRouter.setGold(new EventDataLong(1234L));

        assertEquals(1234L, SidebarModel.getGold());
    }

    /**
     * A later {@code EVENT_GOLD} overwrites the model rather than merging with it, matching a
     * redraw always sending the player's whole current state.
     */
    @Test
    void aSecondGoldMessageOverwritesTheFirst() {
        RedrawRouter.setGold(new EventDataLong(1234L));
        RedrawRouter.setGold(new EventDataLong(50L));

        assertEquals(50L, SidebarModel.getGold());
    }

    /**
     * A payload of the wrong shape is dropped rather than routed, the same guard {@link #setHP}'s
     * own test pins for {@link RedrawRouter#setHP}.
     */
    @Test
    void aNonLongGoldPayloadLeavesTheModelUntouched() {
        RedrawRouter.setGold(new EventDataLong(1234L));

        RedrawRouter.setGold(new EventDataBoolean(true));

        assertEquals(1234L, SidebarModel.getGold(), "a mismatched payload must not overwrite gold");
    }
}
