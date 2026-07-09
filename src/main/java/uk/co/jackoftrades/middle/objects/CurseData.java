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

package uk.co.jackoftrades.middle.objects;

/**
 * The per-object instance data for an applied {@link Curse} — its current power
 * and the timeout until its next effect. This is the small mutable counterpart to
 * the immutable {@link Curse} template; the Java port of the C original's
 * {@code struct curse_data} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class CurseData {
    /**
     * The curse's current power on this object.
     *
     * @author Rowan Crowther
     */
    private int power;
    /**
     * Turns until the curse's next effect fires.
     *
     * @author Rowan Crowther
     */
    private int timeout;

    /**
     * Build curse instance data.
     *
     * @param power   the curse power
     * @param timeout the effect timeout
     * @author Rowan Crowther
     */
    public CurseData(int power, int timeout) {
        this.power = power;
        this.timeout = timeout;
    }

    /**
     * @return the curse's current power
     * @author Rowan Crowther
     */
    public int getPower() {
        return power;
    }

    /**
     * @return turns until the curse's next effect
     * @author Rowan Crowther
     */
    public int getTimeout() {
        return timeout;
    }
}
