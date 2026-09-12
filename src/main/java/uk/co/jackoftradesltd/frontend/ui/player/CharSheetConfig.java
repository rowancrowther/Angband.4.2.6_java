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

import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.output.Region;

public class CharSheetConfig {
    UIEntry stat_mod_entries;
    Region[] resRegions = new Region[4];
    CharSheetResist[] resistsByRegion = new CharSheetResist[4];
    int[] nResistsByRegion = new int[4];
    int nStatModEntries;
    int resCols;
    int resRows;
    int resNLabel;

    public int getResCols() {
        return resCols;
    }

    public int getResRows() {
        return resRows;
    }

    public int getResNLabel() {
        return resNLabel;
    }
}
