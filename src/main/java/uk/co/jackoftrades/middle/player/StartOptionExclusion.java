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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.game.enums.Options;

/**
 * A single birth-option constraint on a piece of starting equipment (the {@code eopts} field of a
 * {@code class.txt} {@code equip:} line). Ports the C {@code start_item.eopts} entries.
 *
 * <p>The constraint reads: <em>exclude this item when {@link #option} is set</em> — unless
 * {@link #negated} is {@code true} (the file's {@code NOT-} prefix), which inverts it to
 * <em>exclude this item when {@link #option} is <b>not</b> set</em>. For example
 * {@code birth_no_recall} on a Word of Recall scroll withholds the scroll from players who chose the
 * "no recall" birth option.
 *
 * @param option  the birth option this constraint tests
 * @param negated {@code true} if the {@code NOT-} prefix was present, inverting the test
 * @author Rowan Crowther
 */
public record StartOptionExclusion(Options option, boolean negated) {
}
