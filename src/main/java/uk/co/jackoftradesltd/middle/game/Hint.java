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

package uk.co.jackoftradesltd.middle.game;

/**
 * A single piece of gameplay advice, loaded from {@code hints.txt}.
 *
 * <p>Ports the C {@code struct hint} ({@code hint.h}), which is one line of text plus a {@code next}
 * pointer. The port drops the pointer and holds the collection as a {@code List<Hint>} in
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry}, filled by
 * {@code MiscDataLoader.loadHints()}. Because the C list is built by prepending each parsed
 * {@code H:} line, it ends up in reverse file order; the port keeps file order. Nothing depends on
 * the difference, since C {@code random_hint} ({@code ui-store.c}) picks uniformly with a
 * reservoir sample and so treats every position alike.
 *
 * <p>In the original game these are the shopkeeper's remarks. When you enter a store other than the
 * Home, {@code prt_welcome} ({@code ui-store.c}) returns silently half the time; otherwise, if any
 * hints are loaded, there is a one-in-three chance the keeper mutters a randomly chosen hint
 * through one of the {@code comment_hint} templates - about one entry in six overall. Hints are
 * not shown when merely browsing stock.
 *
 * <p>Class coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class Hint {
    /**
     * The hint text, exactly as written on the {@code H:} line (C: {@code hint.hint}).
     *
     * <p>Package-private; read it through {@link #getHint()}. Field coded before 261001, commented
     * in full on 261001.
     */
    String hint;

    /**
     * Construct a hint from its text, as {@code HintAssembler} does for each parsed {@code H:}
     * record (C: {@code parse_hint} in {@code init.c}, minus the list linking).
     *
     * <p>Constructor coded before 261001, commented in full on 261001.
     *
     * @param hint the advice line
     */
    public Hint(String hint) {
        this.hint = hint;
    }

    /**
     * Return the advice line, for the caller to feed into a {@code comment_hint} template.
     *
     * <p>Function getHint coded before 261001, commented in full on 261001.
     *
     * @return this hint's text
     */
    public String getHint() {
        return hint;
    }
}
