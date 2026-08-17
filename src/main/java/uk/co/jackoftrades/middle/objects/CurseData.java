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

import java.util.Objects;

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
     * Build an independent copy of another curse's instance data.
     *
     * <p>Needed because this type is mutable and shared by reference wherever it is put into a map.
     * A template's {@link CurseData} must not be handed to the objects made from it — the tick in
     * {@code GameWorld} decrements timeouts in place, so a shared instance would count down once
     * per cursed object and the template would drift. {@link ObjectKind}'s constructor copies every
     * curse in on the way through for exactly that reason.
     *
     * <p>Copies both fields, which is a plain copy rather than the knowledge-side copy C makes in
     * {@code player_know_object} — that one takes the power alone and leaves the timeout at zero,
     * and is written out at its call site rather than reaching for this.
     *
     * <p>Constructor CurseData(CurseData) coded on 260817, commented in full on 260817.
     *
     * @param other the curse data to copy
     * @author Rowan Crowther
     */
    public CurseData(CurseData other) {
        this.power = other.getPower();
        this.timeout = other.getTimeout();
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

    /**
     * Compares two curses' instance data by power alone, ignoring the timeout — the port of the
     * comparison C makes in {@code curses_are_equal} ({@code obj-curse.c}), which walks the two
     * curse arrays testing nothing but {@code power}.
     *
     * <p>The timeout is deliberately excluded. It is a countdown to the curse's next effect, so two
     * otherwise identical cursed items will hold different values simply because they were made at
     * different moments; comparing it would stop them stacking for a reason the player cannot see.
     * The power is what the curse <em>is</em>.
     *
     * <p>This is what lets {@code ItemObject.cursesAreEqual} be a plain {@link java.util.Map}
     * comparison: {@code Map.equals} defers to this for the values, so the map comparison and C's
     * loop reach the same answer.
     *
     * <p>Method equals coded on 260817, commented in full on 260817.
     *
     * @param o the object to compare against
     * @return {@code true} if that object is curse data of the same power
     * @author Rowan Crowther
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CurseData curseData)) return false;
        return power == curseData.power;
    }

    /**
     * Hashes on power alone, to stay consistent with {@link #equals(Object)}.
     *
     * <p>Method hashCode coded on 260817, commented in full on 260817.
     *
     * @return a hash of this curse's power
     * @author Rowan Crowther
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(power);
    }
}
