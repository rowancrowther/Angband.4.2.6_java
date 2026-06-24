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

/**
 * A shape a monster can change into — either a specific {@link MonsterRace} or
 * (for a generic shapechange) a base race to draw from. This is the Java port of
 * the C original's {@code struct monster_shape} ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class MonsterShape {
    /**
     * The shape's name (the target race/base name).
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The specific race to change into, if named directly.
     *
     * @author ClaudeCode
     */
    private MonsterRace race;
    /**
     * The base race to draw a shape from, for generic shapechanges.
     *
     * @author ClaudeCode
     */
    private MonsterRace base;
}
