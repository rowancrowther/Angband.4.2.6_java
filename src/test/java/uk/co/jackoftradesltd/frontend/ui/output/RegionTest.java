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
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests for {@link Region}, the port of C's {@code struct region} and {@code SCREEN_REGION} in
 * {@code ui-output.h}. Expected values come from the C definition, not from the Java code:
 * {@code SCREEN_REGION} is {@code {0, 0, 0, 0}}, and the struct stores whatever it is given.
 */
class RegionTest {

    @Test
    void screenRegionIsAllZeros() {
        Region r = Region.screenRegion();
        assertEquals(0, r.getCol());
        assertEquals(0, r.getRow());
        assertEquals(0, r.getWidth());
        assertEquals(0, r.getPageRows());
    }

    @Test
    void screenRegionReturnsFreshCopyEachCall() {
        Region a = Region.screenRegion();
        Region b = Region.screenRegion();
        assertNotSame(a, b);
    }

    @Test
    void mutatingOneScreenRegionDoesNotAffectLaterOnes() {
        // C's SCREEN_REGION is const; the Java factory must give the same guarantee.
        Region a = Region.screenRegion();
        a.setCol(5);
        a.setRow(6);
        a.setWidth(7);
        a.setPageRows(8);

        Region b = Region.screenRegion();
        assertEquals(0, b.getCol());
        assertEquals(0, b.getRow());
        assertEquals(0, b.getWidth());
        assertEquals(0, b.getPageRows());
    }

    @Test
    void constructorStoresFieldsInDeclarationOrder() {
        // C's field order: col, row, width, page_rows.
        Region r = new Region(1, 2, 3, 4);
        assertEquals(1, r.getCol());
        assertEquals(2, r.getRow());
        assertEquals(3, r.getWidth());
        assertEquals(4, r.getPageRows());
    }

    @Test
    void constructorStoresRelativeAndDefaultValuesRaw() {
        // width 1 = system default; non-positive width and page_rows are relative. Stored, not resolved.
        Region r = new Region(0, 0, 1, 0);
        assertEquals(1, r.getWidth());
        assertEquals(0, r.getPageRows());

        Region neg = new Region(-3, -4, -10, -2);
        assertEquals(-3, neg.getCol());
        assertEquals(-4, neg.getRow());
        assertEquals(-10, neg.getWidth());
        assertEquals(-2, neg.getPageRows());
    }

    @Test
    void settersChangeOnlyTheirOwnField() {
        Region r = new Region(1, 2, 3, 4);

        r.setCol(10);
        assertEquals(10, r.getCol());
        assertEquals(2, r.getRow());
        assertEquals(3, r.getWidth());
        assertEquals(4, r.getPageRows());

        r.setRow(20);
        assertEquals(10, r.getCol());
        assertEquals(20, r.getRow());
        assertEquals(3, r.getWidth());
        assertEquals(4, r.getPageRows());

        r.setWidth(30);
        assertEquals(10, r.getCol());
        assertEquals(20, r.getRow());
        assertEquals(30, r.getWidth());
        assertEquals(4, r.getPageRows());

        r.setPageRows(40);
        assertEquals(10, r.getCol());
        assertEquals(20, r.getRow());
        assertEquals(30, r.getWidth());
        assertEquals(40, r.getPageRows());
    }

    @Test
    void settersStoreNegativeValuesRaw() {
        Region r = new Region(0, 0, 0, 0);
        r.setWidth(-1);
        r.setPageRows(-1);
        assertEquals(-1, r.getWidth());
        assertEquals(-1, r.getPageRows());
    }
}
