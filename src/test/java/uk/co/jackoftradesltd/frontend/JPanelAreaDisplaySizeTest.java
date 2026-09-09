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

package uk.co.jackoftradesltd.frontend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.awt.GraphicsEnvironment;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * {@link SwingUI.JPanelArea#getDisplayWidth} and {@link SwingUI.JPanelArea#getDisplayHeight},
 * checked against C's {@code Term_get_size} ({@code [C] src/ui-term.c}), which reports the same
 * two dimensions through out-parameters and falls back to 80x24 when its global {@code Term}
 * pointer is unset - the same values these getters always return, since {@link
 * SwingUI.JPanelArea#display} has no unset state of its own.
 *
 * <p><b>Constructing a {@code JPanelArea} means constructing a {@code SwingUI}</b>, the same
 * reason {@link JPanelAreaEraseTest} needs a display.
 *
 * @author Rowan Crowther
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class JPanelAreaDisplaySizeTest {

    private SwingUI.JPanelArea area;

    @BeforeEach
    void buildAnArea() {
        assumeFalse(GraphicsEnvironment.isHeadless(),
                "needs a display: constructing a JPanelArea constructs the SwingUI enclosing it");

        SwingUI swingUi = new SwingUI(null, null, null);
        area = swingUi.new JPanelArea();
    }

    /**
     * The width is the column count, 80 for the standard grid - matching {@code Term->wid} for
     * the main window.
     */
    @Test
    void widthIsEightyColumns() {
        assertEquals(80, area.getDisplayWidth());
    }

    /**
     * The height is the row count, 24 for the standard grid - matching {@code Term->hgt} for the
     * main window.
     */
    @Test
    void heightIsTwentyFourRows() {
        assertEquals(24, area.getDisplayHeight());
    }

    /**
     * Both getters read {@link SwingUI.JPanelArea#setChars}'s grid, not a stored field - a grid
     * of a different (still valid) shape changes what they report. {@code setChars} only accepts
     * 24x80, so this exercises the getters against a freshly replaced array rather than proving
     * anything about other sizes.
     */
    @Test
    void reflectsTheCurrentGridAfterSetChars() {
        AngbandDisplayCharacter[][] replacement = new AngbandDisplayCharacter[24][80];
        for (int row = 0; row < 24; row++) {
            for (int col = 0; col < 80; col++) {
                replacement[row][col] = new AngbandDisplayCharacter(' ', ColourEnum.COLOUR_WHITE);
            }
        }

        area.setChars(replacement);

        assertEquals(80, area.getDisplayWidth());
        assertEquals(24, area.getDisplayHeight());
    }
}
