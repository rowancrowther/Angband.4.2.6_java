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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.combat.BlowMethod;

/**
 * One melee attack ("blow") of a monster: how it is delivered ({@link BlowMethod}),
 * what it does ({@link BlowEffect}), its damage dice, and how many times the
 * player has observed it (for lore). This is the Java port of the C original's
 * {@code struct monster_blow} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterBlow {
    /**
     * How the blow is delivered (hit, bite, claw, …).
     *
     * @author Rowan Crowther
     */
    private BlowMethod method;
    /**
     * What the blow does on landing.
     *
     * @author Rowan Crowther
     */
    private BlowEffect effect;
    /**
     * The blow's damage dice.
     *
     * @author Rowan Crowther
     */
    private Random dice;
    /**
     * How many times the player has seen this blow land (for lore).
     *
     * @author Rowan Crowther
     */
    private int timesSeen;

    /**
     * Build a monster blow from its method, effect and damage dice.
     *
     * @param method how the blow is delivered
     * @param effect what the blow does
     * @param dice   the damage dice
     * @author Rowan Crowther
     */
    public MonsterBlow(BlowMethod method, BlowEffect effect, Random dice) {
        this.method = method;
        this.effect = effect;
        this.dice = dice;
    }
}