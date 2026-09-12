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

package uk.co.jackoftradesltd.frontend.ui.output;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link Region}, the port of C's {@code struct region} ({@code ui-output.h:35-41}).
 *
 * <p>C's struct itself does no interpretation of the conditional {@code width}/{@code page_rows}
 * values ("1 - use system default", "non-positive - rel to right of screen", and "non-positive
 * value is relative to the bottom of the screen") - that resolution happens in callers like
 * {@code region_calculate}, not in the struct's own storage. So what these tests pin is only that
 * the four fields are stored and returned exactly as given, for every value shape C's comments
 * call out: an ordinary positive value, {@code 0}, the {@code 1} sentinel, and a negative value.
 *
 * @author Rowan Crowther
 */
class RegionTest {

    @Test
    void gettersReturnWhatTheConstructorWasGiven() {
        Region region = new Region(3, 7, 40, 20);

        assertEquals(3, region.getCol());
        assertEquals(7, region.getRow());
        assertEquals(40, region.getWidth());
        assertEquals(20, region.getPageRows());
    }

    @Test
    void screenRegionIsAllZeroes() {
        // C's SCREEN_REGION = {0, 0, 0, 0} (ui-output.h:46), the region meaning "the full screen".
        Region region = new Region(0, 0, 0, 0);

        assertEquals(0, region.getCol());
        assertEquals(0, region.getRow());
        assertEquals(0, region.getWidth());
        assertEquals(0, region.getPageRows());
    }

    @Test
    void widthOfOneIsStoredAsTheSystemDefaultSentinelUnchanged() {
        // C: "width of display area. 1 - use system default."
        Region region = new Region(0, 0, 1, 0);

        assertEquals(1, region.getWidth());
    }

    @Test
    void nonPositiveWidthAndPageRowsAreStoredUnchangedAsRelativeValues() {
        // C: negative width is "rel to right of screen"; non-positive page_rows is "relative to
        // the bottom of the screen". Neither is clamped or resolved here - that is a caller's job.
        Region region = new Region(0, 0, -10, -5);

        assertEquals(-10, region.getWidth());
        assertEquals(-5, region.getPageRows());
    }
}
