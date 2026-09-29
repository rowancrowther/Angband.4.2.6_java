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
import uk.co.jackoftradesltd.channel.messages.data.EventDataBoolean;
import uk.co.jackoftradesltd.channel.messages.data.EventDataInt;
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
 * {@link HandlersHolder}'s {@code prtDepth} and {@code fmtDepths} and the
 * {@link RedrawRouter#setPlayerDepth} payload that feeds them, checked against C's originals
 * ({@code [C] ui-display.c}, functions {@code prt_depth} and {@code fmt_depth}). The model is
 * written only through {@link RedrawRouter}, the way the running game writes it.
 *
 * <p>Expected values are worked from the C source: level 0 is {@code "Town"}; any other level is
 * {@code "%d' (L%d)"} with {@code depth * 50} feet first; and the field is padded to thirteen
 * columns by {@code "%-13s"}, left-justified, in white.
 *
 * <p>Class DepthRowTest coded on 260929, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
class DepthRowTest {

    private static final int WIDTH = 80;
    private static final int HEIGHT = 24;
    private static final int ROW = 5;
    private static final int FIELD = 13;

    private CellGrid grid;
    private Term savedTerm;

    private static Field termField() throws Exception {
        Field field = HandlersHolder.class.getDeclaredField("term");
        field.setAccessible(true);
        return field;
    }

    private static void prtDepth(int row, int col) throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("prtDepth", int.class, int.class);
        method.setAccessible(true);
        method.invoke(null, row, col);
    }

    private static String fmtDepths(int length) throws Exception {
        Method method = HandlersHolder.class.getDeclaredMethod("fmtDepths", int.class);
        method.setAccessible(true);
        return (String) method.invoke(null, length);
    }

    private void draw(int depth) throws Exception {
        RedrawRouter.setPlayerDepth(new EventDataInt(depth));
        prtDepth(ROW, 0);
    }

    private String field() {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < FIELD; col++) {
            sb.append(grid.get(ROW, col).getCharacter());
        }
        return sb.toString();
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
        RedrawRouter.setPlayerDepth(new EventDataInt(0));
    }

    /**
     * {@code player->depth == 0} is the town, padded to thirteen.
     */
    @Test
    void levelZeroIsTown() throws Exception {
        draw(0);

        assertEquals("Town         ", field());
    }

    /**
     * Level 1 is 50 feet: {@code "50' (L1)"}.
     */
    @Test
    void levelOne() throws Exception {
        draw(1);

        assertEquals("50' (L1)     ", field());
    }

    /**
     * Level 12 is 600 feet, the figure the character sheet's max-depth line also uses.
     */
    @Test
    void levelTwelve() throws Exception {
        draw(12);

        assertEquals("600' (L12)   ", field());
    }

    /**
     * The deepest level, 127, is 6350 feet - twelve characters, still inside the thirteen columns.
     */
    @Test
    void levelOneTwentySeven() throws Exception {
        draw(127);

        assertEquals("6350' (L127) ", field());
    }

    /**
     * A shorter string must wipe a longer one drawn before it: going up from level 127 to the
     * town leaves no digits behind.
     */
    @Test
    void returningToTownWipesThePreviousText() throws Exception {
        draw(127);
        draw(0);

        assertEquals("Town         ", field());
    }

    /**
     * Drawn with {@code put_str}, so every cell of the field is white, padding included.
     */
    @Test
    void drawnInWhite() throws Exception {
        draw(12);

        for (int col = 0; col < FIELD; col++) {
            assertEquals(ColourEnum.COLOUR_WHITE, grid.get(ROW, col).getAttributeColour());
        }
    }

    /**
     * {@code fmt_depth} returns the bare text, unpadded; the padding is {@code prt_depth}'s.
     */
    @Test
    void fmtDepthsIsUnpadded() throws Exception {
        RedrawRouter.setPlayerDepth(new EventDataInt(3));

        assertEquals("150' (L3)", fmtDepths(32));
    }

    /**
     * The model starts at level 0, so a row drawn before any message reads "Town" rather than
     * failing.
     */
    @Test
    void beforeAnyMessageReadsTown() throws Exception {
        assertEquals(0, SidebarModel.getPlayerDepth());
        assertEquals("Town", fmtDepths(32));
    }

    /**
     * A payload of the wrong shape is dropped, leaving the last good level in place.
     */
    @Test
    void wrongShapedPayloadIsDropped() throws Exception {
        RedrawRouter.setPlayerDepth(new EventDataInt(7));
        RedrawRouter.setPlayerDepth(new EventDataBoolean(true));

        assertEquals(7, SidebarModel.getPlayerDepth());
    }
}
