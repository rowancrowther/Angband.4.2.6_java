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

package uk.co.jackoftradesltd.frontend.ui.player;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.frontend.ui.output.Region;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link CharSheetConfig}, the Java form of C's {@code struct char_sheet_config}
 * ({@code ui-player.c}). Expected values come from the C function {@code configure_char_sheet}:
 * {@code res_cols = res_nlabel + 1 + body.count}, region {@code col = i * (res_cols + 1)},
 * {@code row = 2 + STAT_MAX}, {@code width = res_cols}, {@code page_rows = res_rows + 2}.
 *
 * <p>Class CharSheetConfigTest coded on 260929, commented in full on 260929.
 */
class CharSheetConfigTest {

    /**
     * A fresh configuration has four zero-valued regions, four empty maps and zeroed scalars.
     */
    @Test
    void constructorPopulatesRegionsAndMaps() {
        CharSheetConfig config = new CharSheetConfig();
        for (int i = 0; i < 4; i++) {
            Region region = config.getResRegion(i);
            assertNotNull(region);
            assertEquals(0, region.getCol());
            assertEquals(0, region.getRow());
            assertEquals(0, region.getWidth());
            assertEquals(0, region.getPageRows());
            assertNull(config.getResistsByRegion(i, 0));
            assertEquals(0, config.getnResistsByRegion(i));
        }
        assertEquals(0, config.getResCols());
        assertEquals(0, config.getResRows());
        assertEquals(0, config.getResNLabel());
        assertNull(config.stat_mod_entries);
    }

    /**
     * The four regions must be distinct objects, since each is mutated independently.
     */
    @Test
    void regionsAreDistinctObjects() {
        CharSheetConfig config = new CharSheetConfig();
        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                assertNotSame(config.getResRegion(i), config.getResRegion(j));
            }
        }
        config.getResRegion(2).setCol(42);
        assertEquals(0, config.getResRegion(1).getCol());
        assertEquals(0, config.getResRegion(3).getCol());
    }

    /**
     * Same layout arithmetic as configure_char_sheet, for a body with 12 slots.
     */
    @Test
    void layoutArithmeticMatchesC() {
        CharSheetConfig config = new CharSheetConfig();
        final int statMax = 5;
        final int bodyCount = 12;

        config.setResNlabel(6);
        config.setResCols(config.getResNLabel() + 1 + bodyCount);
        assertEquals(6, config.getResNLabel());
        assertEquals(19, config.getResCols());

        int[] counts = {7, 12, 3, 12};
        config.setResRows(0);
        for (int i = 0; i < 4; i++) {
            Region region = config.getResRegion(i);
            region.setCol(i * (config.getResCols() + 1));
            region.setRow(2 + statMax);
            region.setWidth(config.getResCols());
            config.setnResistsByRegion(i, counts[i]);
            if (config.getResRows() < config.getnResistsByRegion(i)) {
                config.setResRows(config.getnResistsByRegion(i));
            }
        }
        for (int i = 0; i < 4; i++) {
            config.getResRegion(i).setPageRows(config.getResRows() + 2);
        }

        assertEquals(12, config.getResRows());
        int[] expectedCols = {0, 20, 40, 60};
        for (int i = 0; i < 4; i++) {
            assertEquals(expectedCols[i], config.getResRegion(i).getCol());
            assertEquals(7, config.getResRegion(i).getRow());
            assertEquals(19, config.getResRegion(i).getWidth());
            assertEquals(14, config.getResRegion(i).getPageRows());
            assertEquals(counts[i], config.getnResistsByRegion(i));
        }
    }

    /**
     * The per-region counts are independent of one another.
     */
    @Test
    void resistCountsAreIndependentPerRegion() {
        CharSheetConfig config = new CharSheetConfig();
        config.setnResistsByRegion(1, 9);
        assertEquals(0, config.getnResistsByRegion(0));
        assertEquals(9, config.getnResistsByRegion(1));
        assertEquals(0, config.getnResistsByRegion(2));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> config.getnResistsByRegion(4));
    }

    /**
     * Stored resist entries come back by position, per region, and unset slots read as null.
     */
    @Test
    void resistsByRegionStoreAndRetrieve() {
        CharSheetConfig config = new CharSheetConfig();
        CharSheetResist a = new CharSheetResist(null);
        CharSheetResist b = new CharSheetResist(null);
        CharSheetResist c = new CharSheetResist(null);

        config.setResistsByRegion(0, 0, a);
        config.setResistsByRegion(0, 1, b);
        config.setResistsByRegion(3, 0, c);

        assertSame(a, config.getResistsByRegion(0, 0));
        assertSame(b, config.getResistsByRegion(0, 1));
        assertSame(c, config.getResistsByRegion(3, 0));
        assertNull(config.getResistsByRegion(1, 0));
        assertNull(config.getResistsByRegion(0, 2));

        // replacing an occupied position overwrites
        config.setResistsByRegion(0, 0, c);
        assertSame(c, config.getResistsByRegion(0, 0));
    }

    /**
     * The label is mutated through the stored entry, as configureCharSheet does.
     */
    @Test
    void storedResistLabelIsMutableThroughConfig() {
        CharSheetConfig config = new CharSheetConfig();
        config.setResistsByRegion(2, 0, new CharSheetResist(null));
        config.getResistsByRegion(2, 0).setLabel("rAcid:");
        assertEquals("rAcid:", config.getResistsByRegion(2, 0).getLabel());
    }

    /**
     * initStatModEntries allocates the requested number of null slots; setStatModEntry rejects nothing in range.
     */
    @Test
    void statModEntriesAllocateAndFill() {
        CharSheetConfig config = new CharSheetConfig();
        config.setNStatModEntries(5);
        config.initStatModEntries(5);
        assertEquals(5, config.stat_mod_entries.length);
        assertEquals(5, config.nStatModEntries);
        for (int i = 0; i < 5; i++) {
            assertNull(config.stat_mod_entries[i]);
            config.setStatModEntry(i, null);
        }
    }

    /**
     * Zero is a legitimate count (C: mem_alloc(0)); any store is then out of range and ignored.
     */
    @Test
    void statModEntriesZeroLength() {
        CharSheetConfig config = new CharSheetConfig();
        config.initStatModEntries(0);
        assertEquals(0, config.stat_mod_entries.length);
        config.setStatModEntry(0, null);
        assertEquals(0, config.stat_mod_entries.length);
    }

    /**
     * Java deviation from C: an index at or past the end is ignored, not written.
     */
    @Test
    void setStatModEntryIgnoresOutOfRangeIndex() {
        CharSheetConfig config = new CharSheetConfig();
        config.initStatModEntries(2);
        config.setStatModEntry(2, null);
        config.setStatModEntry(99, null);
        assertEquals(2, config.stat_mod_entries.length);
    }

    /**
     * Storing before allocation is a caller error and throws, rather than being silently skipped.
     */
    @Test
    void setStatModEntryBeforeInitThrows() {
        CharSheetConfig config = new CharSheetConfig();
        assertThrows(NullPointerException.class, () -> config.setStatModEntry(0, null));
    }

    /**
     * Re-initialising discards the previous array.
     */
    @Test
    void initStatModEntriesReplacesArray() {
        CharSheetConfig config = new CharSheetConfig();
        config.initStatModEntries(3);
        var first = config.stat_mod_entries;
        config.initStatModEntries(5);
        assertNotSame(first, config.stat_mod_entries);
        assertEquals(5, config.stat_mod_entries.length);
    }

    /**
     * setResRegion swaps the reference; the getter then returns the new object.
     */
    @Test
    void setResRegionReplacesReference() {
        CharSheetConfig config = new CharSheetConfig();
        Region old = config.getResRegion(1);
        Region replacement = new Region(20, 7, 19, 14);
        config.setResRegion(1, replacement);
        assertSame(replacement, config.getResRegion(1));
        assertNotSame(old, config.getResRegion(1));
        assertEquals(20, config.getResRegion(1).getCol());
    }

    /**
     * Two configurations never share state.
     */
    @Test
    void instancesDoNotShareState() {
        CharSheetConfig one = new CharSheetConfig();
        CharSheetConfig two = new CharSheetConfig();
        one.setResistsByRegion(0, 0, new CharSheetResist(null));
        one.getResRegion(0).setCol(9);
        one.setResCols(19);
        assertNull(two.getResistsByRegion(0, 0));
        assertEquals(0, two.getResRegion(0).getCol());
        assertEquals(0, two.getResCols());
    }
}
