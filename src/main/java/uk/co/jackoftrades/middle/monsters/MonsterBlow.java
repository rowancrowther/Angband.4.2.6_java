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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.middle.combat.BlowMethod;

public class MonsterBlow {
    private BlowMethod method;
    private BlowEffect effect;
    private Random dice;
    private int timesSeen;

    public MonsterBlow(BlowMethod method, BlowEffect effect, Random dice) {
        this.method = method;
        this.effect = effect;
        this.dice = dice;
        this.timesSeen = 0;
    }
}
