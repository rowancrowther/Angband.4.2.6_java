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

package uk.co.jackoftradesltd.frontend.screen;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link TermWin#init}: the port of C's {@code term_win_init} ({@code [C] src/ui-term.c}),
 * which allocates each layer's flat {@code height * width} buffer and points its row array
 * into it, leaving every cell zeroed - attr {@code 0} ({@code COLOUR_DARK}) and character
 * {@code 0} ({@code '\0'}).
 *
 * <p>Deliberately exercises non-square dimensions (width != height, both ways), since an
 * earlier version of {@link TermWin#init} declared its grids {@code [width][height]} while
 * filling them {@code [row < height][col < width]} - correct only when width and height
 * happened to be equal, and an {@link ArrayIndexOutOfBoundsException} otherwise.
 *
 * <p>Read back by reflection, since {@link TermWin} exposes no accessor for its content
 * layers - {@link TermWinCursorTest} uses the same helper shape for the cursor fields.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TermWinInitTest {

    private static Field field(String name) throws Exception {
        Field field = TermWin.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    private static AngbandDisplayCharacter[][] layer(TermWin win, String name) throws Exception {
        return (AngbandDisplayCharacter[][]) field(name).get(win);
    }

    /**
     * A wide-short window (width &gt; height, the shape of a real terminal - e.g. Angband's
     * default 80x24) sizes every layer {@code height} rows by {@code width} columns. This is
     * the exact shape that threw {@link ArrayIndexOutOfBoundsException} before the
     * {@code [row][col]} ordering was corrected.
     */
    @Test
    void initSizesEveryLayerHeightRowsByWidthColumnsWhenWiderThanTall() throws Exception {
        TermWin win = new TermWin();
        win.init(80, 24);

        for (String name : new String[]{"a", "va", "ta", "vta"}) {
            AngbandDisplayCharacter[][] grid = layer(win, name);
            assertEquals(24, grid.length, name + " should have one row per height");
            for (AngbandDisplayCharacter[] row : grid) {
                assertEquals(80, row.length, name + " should have one column per width");
            }
        }
    }

    /**
     * A tall-narrow window (height &gt; width) sizes every layer the same way, the other
     * direction - checked separately since the pre-fix bug only failed in this direction for
     * some dimension pairs and not others, depending on which was larger.
     */
    @Test
    void initSizesEveryLayerHeightRowsByWidthColumnsWhenTallerThanWide() throws Exception {
        TermWin win = new TermWin();
        win.init(10, 40);

        for (String name : new String[]{"a", "va", "ta", "vta"}) {
            AngbandDisplayCharacter[][] grid = layer(win, name);
            assertEquals(40, grid.length, name + " should have one row per height");
            for (AngbandDisplayCharacter[] row : grid) {
                assertEquals(10, row.length, name + " should have one column per width");
            }
        }
    }

    /**
     * Every cell in every layer is filled with the same blank placeholder C's
     * {@code mem_zalloc} produces for free: character {@code '\0'} at
     * {@link ColourEnum#COLOUR_DARK} (attr {@code 0}).
     */
    @Test
    void initFillsEveryCellWithTheZeroedCState() throws Exception {
        TermWin win = new TermWin();
        win.init(80, 24);

        AngbandDisplayCharacter blank = new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK);

        for (String name : new String[]{"a", "va", "ta", "vta"}) {
            AngbandDisplayCharacter[][] grid = layer(win, name);
            for (AngbandDisplayCharacter[] row : grid) {
                for (AngbandDisplayCharacter cell : row) {
                    assertEquals(blank, cell, name + " cell should be the blank/zeroed placeholder");
                }
            }
        }
    }

    /**
     * A 1x1 window is the smallest legal shape - a single blank cell in every layer, with no
     * off-by-one on either dimension.
     */
    @Test
    void initHandlesTheSmallestWindow() throws Exception {
        TermWin win = new TermWin();
        win.init(1, 1);

        for (String name : new String[]{"a", "va", "ta", "vta"}) {
            AngbandDisplayCharacter[][] grid = layer(win, name);
            assertEquals(1, grid.length, name + " should have exactly one row");
            assertEquals(1, grid[0].length, name + " should have exactly one column");
            assertEquals(new AngbandDisplayCharacter('\0', ColourEnum.COLOUR_DARK), grid[0][0]);
        }
    }
}
