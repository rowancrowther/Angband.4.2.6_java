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

package uk.co.jackoftrades.middle.objects.enums;

/**
 * The kind of chest a grid search is looking for — the port of C's {@code enum chest_query}
 * ({@code obj-chest.h:26-30}).
 *
 * <p>Passed to the chest search that scans one grid's objects and returns the first chest matching
 * the query ({@code chest_check}, {@code obj-chest.c:423-451}). The three constants are not
 * degrees of the same test: each has its own condition, and the last one turns on what the player
 * has learned rather than on what is true.
 *
 * @author Rowan Crowther
 */
public enum ChestQuery {
    /**
     * Any chest at all, opened, empty or locked — C tests only the tval.
     */
    CHEST_ANY,
    /**
     * A chest that is still worth opening: a chest whose pval is non-zero, meaning it is locked or
     * still holds something. An emptied chest has a pval of zero and is skipped.
     */
    CHEST_OPENABLE,
    /**
     * A chest the player <em>knows</em> to be trapped. C requires both that the chest really is
     * trapped and that the player's known copy of it has a non-zero pval
     * ({@code obj-chest.c:443-446}) — an undiscovered trap does not match, so this asks about
     * knowledge, not about the chest.
     */
    CHEST_TRAPPED
}
