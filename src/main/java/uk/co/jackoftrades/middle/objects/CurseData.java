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

    /**
     * Set the curse's power on this object. Setting the power to zero is how a
     * curse is removed from an object (the C original's {@code remove_object_curse}
     * clears both power and timeout).
     *
     * @param power the new curse power
     * @author Rowan Crowther
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Tick the timeout down by one turn. Called once per game turn while the curse
     * is active; when the timeout reaches zero the curse's effect fires and the
     * timeout is re-rolled via {@link #setTimeout(int)}.
     *
     * @author Rowan Crowther
     */
    public void decrementTimeout() {
        this.timeout--;
    }

    /**
     * Reset the timeout to a freshly rolled value after the curse's effect has
     * fired. This is an assignment, not a decrement — it re-arms the countdown to
     * the curse template's next interval (the C original's
     * {@code timeout = randcalc(c->obj->time, ...)}).
     *
     * @param amount the new timeout, in turns
     * @author Rowan Crowther
     */
    public void setTimeout(int amount) {
        this.timeout = amount;
    }
}
