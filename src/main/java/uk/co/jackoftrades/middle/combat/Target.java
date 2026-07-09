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

package uk.co.jackoftrades.middle.combat;

import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.monsters.Monster;

/**
 * The current attack/spell target: either a specific {@link Monster} or a bare
 * grid {@link Loc} the player is aiming at. This is the Java port of the C
 * original's {@code struct target} ({@code src/target.h}).
 *
 * @author Rowan Crowther
 */
public class Target {
    /**
     * The targeted grid location.
     *
     * @author Rowan Crowther
     */
    private Loc grid;
    /**
     * The targeted monster, or {@code null} when only a grid is targeted.
     *
     * @author Rowan Crowther
     */
    private Monster monster;
}
