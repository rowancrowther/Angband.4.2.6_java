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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.frontend.SwingUI;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link Window#getCharacterWidth()} and {@link Window#getCharacterHeight()}, a pass-through
 * to {@link SwingUI.JPanelArea#getDisplayWidth()}/{@code getDisplayHeight()} - already checked
 * against C's {@code Term_get_size} ({@code [C] src/ui-term.c}) by
 * {@code JPanelAreaDisplaySizeTest}. What this class checks is that the wrapper reaches the
 * same grid the panel does, the same relationship {@link WindowEraseTest} checks for
 * {@link Window#erase}.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class WindowCharacterSizeTest {

    private Window window;

    @BeforeEach
    void buildAWindowWithAPanel() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: a Window is a JFrame");

        SwingUI swingUi = new SwingUI(null, null, null);
        window = swingUi.getActiveWindow();
        window.add(swingUi.new JPanelArea());
    }

    /**
     * The standard grid is eighty columns wide.
     */
    @Test
    void widthIsEightyColumns() {
        assertEquals(80, window.getCharacterWidth());
    }

    /**
     * The standard grid is twenty-four rows tall.
     */
    @Test
    void heightIsTwentyFourRows() {
        assertEquals(24, window.getCharacterHeight());
    }

    /**
     * The wrapper reaches the panel's live grid, not a value captured once: replacing the
     * grid through {@link SwingUI.JPanelArea#setChars} is visible through the window too.
     */
    @Test
    void reflectsTheCurrentGridAfterSetChars() {
        AngbandDisplayCharacter[][] replacement = new AngbandDisplayCharacter[24][80];
        for (int row = 0; row < 24; row++) {
            for (int col = 0; col < 80; col++) {
                replacement[row][col] = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);
            }
        }

        window.getArea().setChars(replacement);

        assertEquals(80, window.getCharacterWidth());
        assertEquals(24, window.getCharacterHeight());
    }
}
