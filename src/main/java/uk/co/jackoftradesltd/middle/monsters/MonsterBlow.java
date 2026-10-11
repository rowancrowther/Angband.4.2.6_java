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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.numerics.Random;

/**
 * One melee attack ("blow") of a monster: how it is delivered ({@link BlowMethod}),
 * what it does ({@link BlowEffect}), its damage dice, and how many times the
 * player has observed it (for lore). This is the Java port of the C original's
 * {@code struct monster_blow} ({@code monster.h}).
 *
 * <p>C uses that one struct in two places, and so does this class. A race's {@code blow} array
 * holds the real attacks (method, effect and dice, with {@code times_seen} unused), and a lore
 * record's {@code blows} array holds the player's knowledge of them, where {@code times_seen} counts
 * sightings. A lore blow that has never been seen is the all-zero record that {@code mem_zalloc}
 * gives {@code finish_parse_monster} ({@code mon-init.c}), which the no-argument constructor stands
 * in for.
 *
 * <p>Differences from the C struct worth knowing about:
 * <ul>
 *   <li>C's {@code next} pointer is not carried. It is only used while parsing, and the blows are
 *       held in a {@link java.util.List}.</li>
 *   <li>Whether the player knows the blow is not part of C's {@code monster_blow}. It lives in a
 *       parallel {@code bool} array, {@code blow_known}, on {@code struct monster_lore}
 *       ({@code mon-lore.h}). Here it is the {@link #known} field, so it is carried by every blow,
 *       including the race's own blows, where it means nothing.</li>
 *   <li>C's {@code dice} is a zero-filled {@code random_value} on a blank blow, so it can always be
 *       read. Here a blank blow's {@link #dice} is {@code null}.</li>
 * </ul>
 *
 * <p>Class MonsterBlow coded before 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class MonsterBlow {
    /**
     * The most sightings a blow can record, C's {@code UCHAR_MAX}. The two places that count a
     * sighting in {@code mon-attack.c} ({@code make_attack_normal} and {@code monster_attack_monster})
     * stop at this value, so the count never wraps.
     *
     * <p>Field MAX_TIMES_SEEN coded before 261011, commented in full on 261011.
     */
    private static final int MAX_TIMES_SEEN = 255;

    /**
     * How the blow is delivered (hit, bite, claw, …), C's {@code blow->method}. {@code null} on a
     * blank blow.
     *
     * <p>Field method coded before 261011, commented in full on 261011.
     */
    private BlowMethod method;
    /**
     * What the blow does on landing, C's {@code blow->effect}. {@code null} on a blank blow. A race's
     * real blow always has one, because C's {@code parse_monster_blow} ({@code mon-init.c}) gives a
     * {@code blow:} line with no effect the {@code NONE} effect.
     *
     * <p>Field effect coded before 261011, commented in full on 261011.
     */
    private BlowEffect effect;
    /**
     * The blow's damage dice, C's {@code blow->dice}. {@code null} on a blank blow (see the class
     * block).
     *
     * <p>Field dice coded before 261011, commented in full on 261011.
     */
    private Random dice;
    /**
     * How many times the player has seen this blow land, C's {@code blow->times_seen}. Only the
     * blows held by a lore record use it. Zero on a new blow, and held to 255 by
     * {@link #incrementBlowTimesSeen()}.
     *
     * <p>Field timesSeen coded before 261011, commented in full on 261011.
     */
    private int timesSeen;
    /**
     * Whether the player knows this blow, standing in for one element of C's {@code lore->blow_known}
     * array (see the class block). {@code lore_update} in {@code mon-lore.c} sets it once the blow has
     * been seen or the whole race is known, and C only clears it again by wiping the lore record
     * ({@code wipe_monster_lore}).
     *
     * <p>Field known coded before 261011, commented in full on 261011.
     */
    private boolean known;

    /**
     * Build a monster blow from its method, effect and damage dice. Every argument is stored as
     * given, with no check that the effect or dice are present. The blow starts with no sightings
     * and unknown. The assembler and the lore grammar both build blows this way.
     *
     * <p>Constructor MonsterBlow(BlowMethod, BlowEffect, Random) coded before 261011, commented in
     * full on 261011.
     *
     * @param method how the blow is delivered
     * @param effect what the blow does
     * @param dice   the damage dice
     */
    public MonsterBlow(BlowMethod method, BlowEffect effect, Random dice) {
        this.method = method;
        this.effect = effect;
        this.dice = dice;
        timesSeen = 0;
        known = false;
    }

    /**
     * Build a blank blow: no method, effect or dice, no sightings and unknown. This is the Java
     * stand-in for one zeroed element of the {@code lore->blows} array that C allocates for every
     * race at the end of {@code finish_parse_monster} ({@code mon-init.c}), and
     * {@link MonsterLore#initblows(int)} fills a lore record with these.
     *
     * <p>Where C's blank record has a zero-valued {@code dice} that can be read, this one has a
     * {@code null} {@link #getDice()}. C's monster recall reads the dice from the race's own blow,
     * and its lore-file writer skips a lore blow with no method before it reads that blow's dice, so
     * neither reaches a blank one.
     *
     * <p>Constructor MonsterBlow() coded before 261011, commented in full on 261011.
     */
    public MonsterBlow() {
        method = null;
        effect = null;
        dice = null;
        timesSeen = 0;
        known = false;
    }

    /**
     * Returns the blow's damage dice, C's {@code blow->dice}. The object is the live one, not a
     * copy.
     *
     * <p>Function getDice coded before 261011, commented in full on 261011.
     *
     * @return the damage dice, or {@code null} for a blank blow
     */
    public Random getDice() {
        return dice;
    }

    /**
     * Returns what the blow does on landing, C's {@code blow->effect}.
     *
     * <p>Function getEffect coded before 261011, commented in full on 261011.
     *
     * @return the blow effect, or {@code null} for a blank blow
     */
    public BlowEffect getEffect() {
        return effect;
    }

    /**
     * Returns how the blow is delivered, C's {@code blow->method}.
     *
     * <p>Function getMethod coded before 261011, commented in full on 261011.
     *
     * @return the blow method, or {@code null} for a blank blow
     */
    public BlowMethod getMethod() {
        return method;
    }

    /**
     * Returns how many times the player has seen this blow land, C's {@code blow->times_seen}.
     *
     * <p>Function getTimesSeen coded before 261011, commented in full on 261011.
     *
     * @return the sighting count, from 0 to 255
     */
    public int getTimesSeen() {
        return timesSeen;
    }

    /**
     * Adds one to the sighting count and holds it at 255. C writes this as
     * {@code if (times_seen < UCHAR_MAX) times_seen++} in {@code make_attack_normal} and
     * {@code monster_attack_monster} ({@code mon-attack.c}), which gives the same counts as the
     * increment and clamp here: 0 to 254 each go up by one, and 255 stays at 255.
     *
     * <p>The condition C puts around that line, a visible monster whose blow was obvious, did damage
     * or has already been seen more than ten times, belongs to the attack code. This method counts
     * unconditionally.
     *
     * <p>Function incrementBlowTimesSeen coded before 261011, commented in full on 261011.
     */
    public void incrementBlowTimesSeen() {
        timesSeen++;
        if (timesSeen > MAX_TIMES_SEEN) {
            timesSeen = MAX_TIMES_SEEN;
        }
    }

    /**
     * Returns whether the player knows this blow, standing in for C's {@code lore->blow_known[i]}
     * (see {@link #known}).
     *
     * <p>Function isKnown coded before 261011, commented in full on 261011.
     *
     * @return true if the blow is known
     */
    public boolean isKnown() {
        return known;
    }

    /**
     * Sets whether the player knows this blow. Nothing is derived from the argument: C's
     * {@code lore_update} ({@code mon-lore.c}) sets the flag when the sighting count is above zero
     * or the whole race is known, and it is the caller's job to apply that rule.
     *
     * <p>Function setKnown coded before 261011, commented in full on 261011.
     *
     * @param known true to mark the blow as known
     */
    public void setKnown(boolean known) {
        this.known = known;
    }
}