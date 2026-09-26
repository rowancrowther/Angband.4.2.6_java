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

package uk.co.jackoftradesltd.channel.globals;

import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.channel.messages.data.PlayerStatusView;

public class ChannelRegistry {
    public static final int STAT_MAX = PlayerEventStatusUpdate.getPlayerStatusView().maxStats().length;
    public static int[] extractEnergy = new int[]
            {
                    /* Slow */
                    1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* Slow */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* S-50 */     1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                    /* S-40 */     2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
                    /* S-30 */     2, 2, 2, 2, 2, 2, 2, 3, 3, 3,
                    /* S-20 */     3, 3, 3, 3, 3, 4, 4, 4, 4, 4,
                    /* S-10 */     5, 5, 5, 5, 6, 6, 7, 7, 8, 9,
                    /* Norm */    10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                    /* F+10 */    20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                    /* F+20 */    30, 31, 32, 33, 34, 35, 36, 36, 37, 37,
                    /* F+30 */    38, 38, 39, 39, 40, 40, 40, 41, 41, 41,
                    /* F+40 */    42, 42, 42, 43, 43, 43, 44, 44, 44, 44,
                    /* F+50 */    45, 45, 45, 45, 45, 46, 46, 46, 46, 46,
                    /* F+60 */    47, 47, 47, 47, 47, 48, 48, 48, 48, 48,
                    /* F+70 */    49, 49, 49, 49, 49, 49, 49, 49, 49, 49,
                    /* Fast */    49, 49, 49, 49, 49, 49, 49, 49, 49, 49
            };
    private static int PY_MAX_LEVEL;

    public static int getPYMaxLevel() {
        return PY_MAX_LEVEL;
    }

    public static void setPYMaxLevel(int val) {
        PY_MAX_LEVEL = val;
    }
}
