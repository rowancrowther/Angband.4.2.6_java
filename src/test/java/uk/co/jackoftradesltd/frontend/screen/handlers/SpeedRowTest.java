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
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataInt;
import uk.co.jackoftradesltd.channel.messages.data.EventDataPlayerSpeed;
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
 * {@link HandlersHolder}'s {@code prtSpeed} and the {@link RedrawRouter#setPlayerSpeed} payload that
 * feeds it, checked against C's originals ({@code [C] ui-display.c}, functions {@code prt_speed}
 * and {@code prt_speed_aux}). The model is written only through {@link RedrawRouter}, the way the
 * running game writes it.
 *
 * <p>Expected values are worked from the C source: 110 draws nothing; above it "Fast" in light
 * green, below it "Slow" in light umber; the offset form {@code "%s (%+d)"}; the multiplier form
 * {@code 10 * extract_energy[i] / extract_energy[110]} split into whole and tenths in {@code int}
 * arithmetic, with the table values read from {@code [C] game-world.c}; and the field padded to
 * eleven columns by {@code "%-11s"}.
 *
 * <p>Class SpeedRowTest coded on 260929, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
class SpeedRowTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;
    private static final int ROW = 5;

    private CellGrid grid;
    private Term savedTerm;

    private static Field termField() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("term");
        field.setAccessible(true);
        return field;
    }

    private static void prtSpeed(int row, int col) throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtSpeed", int.class, int.class);
        method.setAccessible(true);
        method.invoke(null, row, col);
    }

    /**
     * Sends the payload the core would, then draws the row at column 0.
     */
    private void draw(int speed, boolean effective, int energy, int energyNormal) throws Exception {
        RedrawRouter.setPlayerSpeed(new EventDataPlayerSpeed(speed, effective, energy, energyNormal));
        prtSpeed(ROW, 0);
    }

    /**
     * The eleven cells of the field, as text.
     */
    private String field() {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < 11; col++) {
            sb.append(grid.get(ROW, col).getCharacter());
        }
        return sb.toString();
    }

    private ColourEnum colourAt(int col) {
        return grid.get(ROW, col).getAttributeColour();
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
        RedrawRouter.setPlayerSpeed(new EventDataPlayerSpeed(0, false, 0, 0));
    }

    /**
     * Normal speed prints nothing: the whole field is blanks.
     */
    @Test
    void normalSpeedBlanksTheField() throws Exception {
        draw(110, false, 10, 10);

        assertEquals("           ", field());
    }

    /**
     * A shorter string must wipe a longer one drawn before it.
     */
    @Test
    void returningToNormalSpeedWipesThePreviousText() throws Exception {
        draw(199, false, 49, 10);
        draw(110, false, 10, 10);

        assertEquals("           ", field());
    }

    /**
     * {@code "%s (%+d)"} with the offset from 110, padded to eleven.
     */
    @Test
    void offsetFormFast() throws Exception {
        draw(120, false, 20, 10);

        assertEquals("Fast (+10) ", field());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, colourAt(0));
    }

    @Test
    void offsetFormSlow() throws Exception {
        draw(100, false, 5, 10);

        assertEquals("Slow (-10) ", field());
        assertEquals(ColourEnum.COLOUR_LIGHT_UMBER, colourAt(0));
    }

    /**
     * Either side of 110 flips the type and the sign.
     */
    @Test
    void oneStepEitherSideOfNormal() throws Exception {
        draw(111, false, 11, 10);
        assertEquals("Fast (+1)  ", field());

        draw(109, false, 9, 10);
        assertEquals("Slow (-1)  ", field());
    }

    /**
     * The table's ends: 0 gives the longest text C can print, 199 the largest offset.
     */
    @Test
    void extremesOfTheSpeedRange() throws Exception {
        draw(0, false, 1, 10);
        assertEquals("Slow (-110)", field());

        draw(199, false, 49, 10);
        assertEquals("Fast (+89) ", field());
    }

    /**
     * {@code extract_energy[]} values from game-world.c: 20, 5, 38 against 10.
     */
    @Test
    void multiplierForm() throws Exception {
        draw(120, true, 20, 10);
        assertEquals("Fast (2.0x)", field());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, colourAt(0));

        draw(100, true, 5, 10);
        assertEquals("Slow (0.5x)", field());
        assertEquals(ColourEnum.COLOUR_LIGHT_UMBER, colourAt(0));

        draw(140, true, 38, 10);
        assertEquals("Fast (3.8x)", field());
    }

    /**
     * The multiplier is truncated, not rounded: 10 * 199 / 100 = 19 gives 1.9x, not 2.0x.
     */
    @Test
    void multiplierTruncates() throws Exception {
        draw(150, true, 199, 100);

        assertEquals("Fast (1.9x)", field());
    }

    /**
     * At normal speed the option makes no difference: still blank.
     */
    @Test
    void normalSpeedWithEffectiveOptionIsBlank() throws Exception {
        draw(110, true, 10, 10);

        assertEquals("           ", field());
    }

    /**
     * The router writes all four parts of the payload into the model.
     */
    @Test
    void routerWritesAllFourParts() {
        RedrawRouter.setPlayerSpeed(new EventDataPlayerSpeed(133, true, 33, 10));

        assertEquals(133, SidebarModel.getPlayerSpeed());
        assertEquals(true, SidebarModel.getPlayerEffectiveSpeed());
        assertEquals(33, SidebarModel.getEnergy());
        assertEquals(10, SidebarModel.getEnergyNormal());
    }

    /**
     * A payload of another shape is ignored, as with the other routing methods.
     */
    @Test
    void routerIgnoresOtherPayloads() {
        RedrawRouter.setPlayerSpeed(new EventDataPlayerSpeed(133, true, 33, 10));
        RedrawRouter.setPlayerSpeed(new EventDataInt(5));

        assertEquals(133, SidebarModel.getPlayerSpeed());
    }

    /**
     * The speed row is registered at priority 13 against EVENT_PLAYERSPEED, as C's table has it.
     */
    @Test
    void registeredAtPriority13() throws Exception {
        Field f = HandlersHolder.class.getDeclaredField("sideHandlers");
        f.setAccessible(true);
        java.util.List<?> list = (java.util.List<?>) f.get(null);
        SideHandler last = (SideHandler) list.get(list.size() - 1);
        assertEquals(13, last.getPriority());
        assertEquals(GameEventType.EVENT_PLAYERSPEED, last.getType());
        assertEquals(21, list.size());
    }
}
