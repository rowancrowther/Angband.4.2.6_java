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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.messages.data.EventDataInt;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMonsterInfo;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.grid.CellGrid;
import uk.co.jackoftradesltd.frontend.screen.grid.Screen;
import uk.co.jackoftradesltd.frontend.ui.RedrawRouter;
import uk.co.jackoftradesltd.frontend.ui.SidebarModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link HandlersHolder}'s {@code prtHealth}/{@code prtHealthAux}/{@code monsterHealthAttr}, and
 * {@link RedrawRouter#setMonsterHealth}, which feeds them, checked against C's originals
 * ({@code [C] ui-display.c}, functions {@code prt_health}, {@code prt_health_aux} and
 * {@code monster_health_attr}). The model is written only through
 * {@link RedrawRouter#setMonsterHealth}, the way the running game writes it.
 *
 * <p>Expected values are worked from the C source, not from the port: percentage
 * {@code 100L * hp / maxhp} with truncating division; star count {@code (pct < 10) ? 1 :
 * (pct < 90) ? (pct / 10 + 1) : 10}; colour red below 10, light red from 10, orange from 25, yellow
 * from 60, light green at 100; then the timed effects override in the order fear, disenchant,
 * command, confuse, stun, sleep, hold.
 *
 * <p>Class HealthBarTest coded on 260929, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
class HealthBarTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;
    private static final int ROW = 3;
    private static final int COL = 0;

    private CellGrid grid;
    private Term savedTerm;

    private static Field termField() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("term");
        field.setAccessible(true);
        return field;
    }

    private static Object invoke(String name, int row, int col) throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod(name, int.class, int.class);
        method.setAccessible(true);
        return method.invoke(null, row, col);
    }

    private static ColourEnum attr() throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("monsterHealthAttr");
        method.setAccessible(true);
        return (ColourEnum) method.invoke(null);
    }

    /**
     * Tracked, visible monster with no timed effects; the player is not hallucinating.
     */
    private static void track(int hp, int max) {
        RedrawRouter.setMonsterHealth(new EventDataMonsterInfo(hp, max, true, true,
                false, false, false, false, false, false, false, false));
    }

    private static void trackWith(int hp, int max, boolean visible, boolean feared, boolean disen,
                                  boolean command, boolean conf, boolean stunned, boolean slept,
                                  boolean held, boolean image) {
        RedrawRouter.setMonsterHealth(new EventDataMonsterInfo(hp, max, true, visible,
                feared, disen, command, conf, stunned, slept, held, image));
    }

    private static void untrack(boolean image) {
        RedrawRouter.setMonsterHealth(new EventDataMonsterInfo(0, 0, false, false,
                false, false, false, false, false, false, false, image));
    }

    private int stars() {
        int count = 0;
        for (int index = 1; index <= 10; index++) {
            if (grid.get(ROW, COL + index).getCharacter() == '*') count++;
        }
        return count;
    }

    private ColourEnum starColour() {
        return grid.get(ROW, COL + 1).getAttributeColour();
    }

    private ColourEnum colourAt(int percentHp) throws Exception {
        track(percentHp, 100);
        invoke("prtHealth", ROW, COL);
        return starColour();
    }

    @BeforeEach
    void buildTerm() throws Exception {
        grid = new CellGrid(HEIGHT, WIDTH);
        Term term = new Term();
        term.termInit(WIDTH, HEIGHT, 1024, null, new Screen(grid, new ArrayList<>()));
        savedTerm = (Term) termField().get(null);
        termField().set(null, term);
    }

    @AfterEach
    void restore() throws Exception {
        termField().set(null, savedTerm);
        untrack(false);
    }

    /**
     * The payload is unpacked field for field, none crossed with a neighbour.
     */
    @Test
    void routerWritesEveryComponent() {
        RedrawRouter.setMonsterHealth(new EventDataMonsterInfo(7, 20, true, true,
                true, false, true, false, true, false, true, true));

        assertEquals(7, SidebarModel.getMonHealth());
        assertEquals(20, SidebarModel.getMonMaxHealth());
        assertEquals(true, SidebarModel.monExists());
        assertEquals(true, SidebarModel.isMonVisible());
        assertEquals(true, SidebarModel.isMonFeared());
        assertEquals(false, SidebarModel.isMonDisen());
        assertEquals(true, SidebarModel.isMonCommand());
        assertEquals(false, SidebarModel.isMonConf());
        assertEquals(true, SidebarModel.isMonStunned());
        assertEquals(false, SidebarModel.isMonSlept());
        assertEquals(true, SidebarModel.isMonHeld());
        assertEquals(true, SidebarModel.isPlayerTmdImage());
    }

    /**
     * Any other payload is dropped, leaving the model as it was.
     */
    @Test
    void routerIgnoresOtherPayloads() {
        track(7, 20);

        RedrawRouter.setMonsterHealth(new EventDataInt(99));

        assertEquals(7, SidebarModel.getMonHealth());
        assertEquals(20, SidebarModel.getMonMaxHealth());
    }

    /**
     * C: {@code if (!mon) { Term_erase(col, row, 12); return 0; }} - twelve cells blanked, the
     * thirteenth untouched, and a zero width returned.
     */
    @Test
    void notTrackingErasesTwelveCellsAndReturnsZero() throws Exception {
        Term term = (Term) termField().get(null);
        term.putStr("XXXXXXXXXXXXXXX", ROW, COL);
        untrack(false);

        Object width = invoke("prtHealthAux", ROW, COL);

        assertEquals(0, width);
        for (int index = 0; index < 12; index++) {
            assertEquals(' ', grid.get(ROW, COL + index).getCharacter(), "cell " + index);
        }
        assertEquals('X', grid.get(ROW, COL + 12).getCharacter());
    }

    /**
     * C: {@code monster_health_attr} returns {@code COLOUR_DARK} when not tracking.
     */
    @Test
    void notTrackingColourIsDark() throws Exception {
        untrack(false);

        assertEquals(ColourEnum.COLOUR_DARK, attr());
    }

    /**
     * C: an unseen, hallucinated or dead monster shows {@code "[----------]"} in the
     * {@code monster_health_attr} colour, which is white for all three; a tracked bar returns 12.
     */
    @Test
    void unknownBarIsWhiteForUnseenHallucinatedAndDead() throws Exception {
        boolean[][] cases = {
                // visible, image, hp
                {false, false, true},
                {true, true, true},
                {true, false, false},
        };
        for (boolean[] c : cases) {
            trackWith(c[2] ? 50 : -1, 100, c[0], false, false, false, false, false, false, false, c[1]);

            Object width = invoke("prtHealthAux", ROW, COL);

            assertEquals(12, width);
            String bar = "[----------]";
            for (int index = 0; index < 12; index++) {
                assertEquals(bar.charAt(index), grid.get(ROW, COL + index).getCharacter());
                assertEquals(ColourEnum.COLOUR_WHITE, grid.get(ROW, COL + index).getAttributeColour());
            }
            assertEquals(ColourEnum.COLOUR_WHITE, attr());
        }
    }

    /**
     * Star counts across the boundaries of C's {@code (pct < 10) ? 1 : (pct < 90) ? pct/10 + 1 : 10}.
     */
    @Test
    void starCountFollowsCsThresholds() throws Exception {
        int[][] hpToStars = {
                {0, 1}, {5, 1}, {9, 1}, {10, 2}, {19, 2}, {20, 3}, {24, 3}, {25, 3}, {50, 6}, {59, 6},
                {60, 7}, {89, 9}, {90, 10}, {99, 10}, {100, 10},
        };
        for (int[] row : hpToStars) {
            track(row[0], 100);
            invoke("prtHealth", ROW, COL);
            assertEquals(row[1], stars(), "hp " + row[0] + "/100");
        }
    }

    /**
     * The percentage truncates: 1/3 is 33 per cent (four stars), 2/3 is 66 (seven), and 99/100 stays
     * 99 rather than rounding up to a full bar's colour.
     */
    @Test
    void percentageTruncates() throws Exception {
        track(1, 3);
        invoke("prtHealth", ROW, COL);
        assertEquals(4, stars());
        assertEquals(ColourEnum.COLOUR_ORANGE, starColour());

        track(2, 3);
        invoke("prtHealth", ROW, COL);
        assertEquals(7, stars());
        assertEquals(ColourEnum.COLOUR_YELLOW, starColour());

        assertEquals(ColourEnum.COLOUR_YELLOW, colourAt(99));
    }

    /**
     * The unfilled part of the bar stays white and the brackets are always white, whatever colour
     * the stars are: C draws the white unknown bar first, then the stars over it from
     * {@code col + 1}.
     */
    @Test
    void unfilledPartAndBracketsStayWhite() throws Exception {
        track(50, 100);

        invoke("prtHealth", ROW, COL);

        assertEquals('[', grid.get(ROW, COL).getCharacter());
        assertEquals(ColourEnum.COLOUR_WHITE, grid.get(ROW, COL).getAttributeColour());
        for (int index = 1; index <= 6; index++) {
            assertEquals('*', grid.get(ROW, COL + index).getCharacter());
            assertEquals(ColourEnum.COLOUR_ORANGE, grid.get(ROW, COL + index).getAttributeColour());
        }
        for (int index = 7; index <= 10; index++) {
            assertEquals('-', grid.get(ROW, COL + index).getCharacter());
            assertEquals(ColourEnum.COLOUR_WHITE, grid.get(ROW, COL + index).getAttributeColour());
        }
        assertEquals(']', grid.get(ROW, COL + 11).getCharacter());
    }

    /**
     * The health colour at each of C's thresholds, one either side of each.
     */
    @Test
    void healthColourBoundaries() throws Exception {
        assertEquals(ColourEnum.COLOUR_RED, colourAt(0));
        assertEquals(ColourEnum.COLOUR_RED, colourAt(9));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, colourAt(10));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, colourAt(24));
        assertEquals(ColourEnum.COLOUR_ORANGE, colourAt(25));
        assertEquals(ColourEnum.COLOUR_ORANGE, colourAt(59));
        assertEquals(ColourEnum.COLOUR_YELLOW, colourAt(60));
        assertEquals(ColourEnum.COLOUR_YELLOW, colourAt(99));
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, colourAt(100));
    }

    /**
     * Each timed effect on its own, on a healthy monster, gives C's override colour.
     */
    @Test
    void eachTimedEffectOverridesTheHealthColour() throws Exception {
        trackWith(100, 100, true, true, false, false, false, false, false, false, false);
        assertEquals(ColourEnum.COLOUR_VIOLET, attr());
        trackWith(100, 100, true, false, true, false, false, false, false, false, false);
        assertEquals(ColourEnum.COLOUR_LIGHT_UMBER, attr());
        trackWith(100, 100, true, false, false, true, false, false, false, false, false);
        assertEquals(ColourEnum.COLOUR_LIGHT_PURPLE, attr());
        trackWith(100, 100, true, false, false, false, true, false, false, false, false);
        assertEquals(ColourEnum.COLOUR_UMBER, attr());
        trackWith(100, 100, true, false, false, false, false, true, false, false, false);
        assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, attr());
        trackWith(100, 100, true, false, false, false, false, false, true, false, false);
        assertEquals(ColourEnum.COLOUR_BLUE, attr());
        trackWith(100, 100, true, false, false, false, false, false, false, true, false);
        assertEquals(ColourEnum.COLOUR_BLUE, attr());
    }

    /**
     * Where two effects are active the later line in C wins: fear then disenchant gives light
     * umber, confuse then stun gives light blue, and everything then sleep gives blue.
     */
    @Test
    void laterEffectWinsOverEarlier() throws Exception {
        trackWith(100, 100, true, true, true, false, false, false, false, false, false);
        assertEquals(ColourEnum.COLOUR_LIGHT_UMBER, attr());
        trackWith(100, 100, true, false, false, false, true, true, false, false, false);
        assertEquals(ColourEnum.COLOUR_LIGHT_BLUE, attr());
        trackWith(100, 100, true, true, true, true, true, true, true, false, false);
        assertEquals(ColourEnum.COLOUR_BLUE, attr());
    }

    /**
     * An effect recolours the stars but never the star count: fear does not change a half-health
     * monster's six stars.
     */
    @Test
    void effectsDoNotChangeStarCount() throws Exception {
        trackWith(50, 100, true, true, false, false, false, false, false, false, false);

        invoke("prtHealth", ROW, COL);

        assertEquals(6, stars());
        assertEquals(ColourEnum.COLOUR_VIOLET, starColour());
    }

    /**
     * C's {@code side_handlers[]} has twenty-two rows; the one not yet ported is
     * {@code prt_depth}, so the port's table holds twenty-one.
     */
    @Test
    void sideTableHoldsTwentyOneRows() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("sideHandlers");
        field.setAccessible(true);
        java.util.List<?> handlers = (java.util.List<?>) field.get(null);

        assertEquals(21, handlers.size());
    }
}
