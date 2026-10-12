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

package uk.co.jackoftradesltd.middle.objects;

import java.util.Objects;

/**
 * The per-object instance data for an applied {@link Curse} — its current power
 * and the timeout until its next effect. This is the small mutable counterpart to
 * the immutable {@link Curse} template; the Java port of the C original's
 * {@code struct curse_data} ({@code object.h}), two plain {@code int} fields with no behaviour of
 * their own.
 *
 * <p>C keeps one of these per curse in the game, in an array hung off the object and indexed by
 * curse, and reads a power of zero as "this object does not carry that curse". The port holds a
 * map from {@link Curse} to {@code CurseData} instead (see {@code ItemObject.getCurses()}), in
 * which absence says the same thing, so this class never needs to represent "no curse" itself —
 * though an entry left at power zero by {@link #setPower(int)} can still exist, which is why the
 * readers ({@code GameWorld}'s tick, {@code ItemObject.cursesAreEqual}) guard on {@link #getPower()}.
 * A power of 100 or more marks a curse that cannot be removed (the test in {@code uncurse_object},
 * {@code effect-handler-general.c}).
 *
 * <p>The behaviour C wraps around this struct lives elsewhere: the per-turn tick is in
 * {@code GameWorld}, the equality test in {@code ItemObject.cursesAreEqual}, and the removal in
 * {@code ItemObject.removeCurse}.
 *
 * <p>Class CurseData coded before 260817, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
public class CurseData {
    /**
     * The curse's current power on this object — C's {@code curse_data.power}. Zero means the
     * curse is off; 100 or more means it is permanent. Compared by {@link #equals(Object)}.
     *
     * <p>Field power commented in full on 261012.
     */
    private int power;
    /**
     * Turns until the curse's next effect fires — C's {@code curse_data.timeout}. Rolled from the
     * curse object's {@code time} dice when the curse is attached to an object, counted down once
     * per game turn while the item is worn, and re-rolled each time the effect fires. Not compared
     * by {@link #equals(Object)}.
     *
     * <p>Field timeout commented in full on 261012.
     */
    private int timeout;

    /**
     * Build curse instance data from a power and a timeout, filling in both fields of C's
     * {@code struct curse_data}.
     *
     * <p>Neither argument is checked or clamped, as in C, where the fields are bare {@code int}s.
     * Parser assemblers pass a timeout of zero for a template and leave the real roll to the code
     * that attaches the curse to an object; C does the same roll in {@code copy_curses} and
     * {@code append_object_curse} ({@code obj-curse.c}).
     *
     * <p>Constructor CurseData(int, int) coded before 260817, commented in full on 261012.
     *
     * @param power   the curse power
     * @param timeout the effect timeout
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
     * <p>Constructor CurseData(CurseData) coded on 260817, commented in full on 261012.
     *
     * @param other the curse data to copy
     */
    public CurseData(CurseData other) {
        this.power = other.getPower();
        this.timeout = other.getTimeout();
    }

    /**
     * Reads the curse's power — C's {@code obj->curses[i].power}, the field every C reader tests
     * to ask whether an object carries curse {@code i}. Zero means it does not.
     *
     * <p>Method getPower coded before 260817, commented in full on 261012.
     *
     * @return the curse's current power
     */
    public int getPower() {
        return power;
    }

    /**
     * Reads the countdown to the curse's next effect — C's {@code obj->curses[i].timeout}.
     *
     * <p>Method getTimeout coded before 260817, commented in full on 261012.
     *
     * @return turns until the curse's next effect
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the curse's power on this object, leaving the timeout alone.
     *
     * <p>This is a plain field write, the port of C's {@code obj->curses[pick].power = power}
     * in {@code append_object_curse} ({@code obj-curse.c}), where a duplicate curse is raised to
     * the stronger power. It is <em>not</em> how a curse is removed: C's {@code remove_object_curse}
     * ({@code obj-curse.c}) zeroes the power and the timeout together and then frees the array if
     * that was the last curse, whereas this leaves the timeout as it was and the map entry in
     * place. To take a curse off an {@link ItemObject}, use {@code ItemObject.removeCurse}, which
     * deletes the entry. Calling this with zero on an entry already in a map leaves a zero-power
     * entry, which readers treat as "not cursed" by testing {@link #getPower()}.
     *
     * <p>Method setPower coded before 260817, commented in full on 261012.
     *
     * @param power the new curse power
     */
    public void setPower(int power) {
        this.power = power;
    }

    /**
     * Ticks the timeout down by one turn, the port of C's {@code curse[j].timeout--} in
     * {@code decrease_timeouts} ({@code game-world.c}).
     *
     * <p>Unguarded, as in C: the caller tests for a non-zero power first, and tests for a result of
     * exactly zero afterwards ({@code if (!curse[j].timeout)}). A timeout already at zero therefore
     * goes to {@code -1} and does not fire again until it has wrapped, in C as here; no clamp is
     * applied. When the countdown reaches zero the caller fires the curse's effect and re-arms the
     * timeout with {@link #setTimeout(int)}.
     *
     * <p>Method decrementTimeout coded before 260817, commented in full on 261012.
     */
    public void decrementTimeout() {
        this.timeout--;
    }

    /**
     * Sets the timeout outright — an assignment, not a decrement.
     *
     * <p>The port of C's {@code curse[j].timeout = randcalc(c->obj->time, 0, RANDOMISE)}, which
     * {@code decrease_timeouts} ({@code game-world.c}) runs after a curse's effect has fired, and
     * which {@code copy_curses} and {@code append_object_curse} ({@code obj-curse.c}) run when a
     * curse is first attached. The dice roll is the caller's job; this takes the rolled value.
     *
     * <p>Method setTimeout coded before 260817, commented in full on 261012.
     *
     * @param amount the new timeout, in turns
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
     * <p>{@code ItemObject.cursesAreEqual} does <em>not</em> rely on this. It walks every curse in
     * the registry and compares {@link #getPower()} itself, because C's loop reads a curse absent
     * from one object as power zero and so matches it against a zero-power entry on the other,
     * which {@link java.util.Map#equals} would call different. Anything comparing two curse maps
     * with {@code Map.equals} gets this method for the values and that stricter answer.
     *
     * <p>Method equals coded on 260817, commented in full on 261012.
     *
     * @param o the object to compare against
     * @return {@code true} if that object is curse data of the same power
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CurseData curseData)) return false;
        return power == curseData.power;
    }

    /**
     * Hashes on power alone, to stay consistent with {@link #equals(Object)}.
     *
     * <p>The class is mutable and {@link #setPower(int)} changes the hash, so an instance must not be
     * used as a key in a hashed collection; the port only ever uses it as a map value.
     *
     * <p>Method hashCode coded on 260817, commented in full on 261012.
     *
     * @return a hash of this curse's power
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(power);
    }

}
