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

package uk.co.jackoftrades.middle.game;

/**
 * A single piece of gameplay advice, loaded from {@code hints.txt}.
 *
 * <p>Ports the C {@code struct hint} ({@code hint.h}), which is one line of text plus a {@code next}
 * pointer; the port drops the pointer and holds the collection as a {@code List<Hint>} on
 * {@link uk.co.jackoftrades.middle.game.globals.GameConstants} instead. In the original game these
 * are the shopkeeper's remarks: entering or browsing a store has a one-in-three chance of the
 * keeper muttering a randomly chosen hint (C {@code random_hint}, {@code ui-store.c}).
 *
 * @author Rowan Crowther
 */
public class Hint {
    /**
     * The hint text, exactly as written on the {@code H:} line (C: {@code hint.hint}).
     */
    String hint;

    /**
     * Construct a hint from its text.
     *
     * @param hint the advice line
     */
    public Hint(String hint) {
        this.hint = hint;
    }

    /**
     * @return this hint's text
     */
    public String getHint() {
        return hint;
    }
}
