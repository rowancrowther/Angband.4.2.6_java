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

/**
 * A shape a monster can change into — either a specific {@link MonsterRace} or
 * (for a generic shapechange) a base race to draw from. This is the Java port of
 * the C original's {@code struct monster_shape} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterShape {
    /**
     * The shape's name (the target race/base name).
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * The specific race to change into, if named directly.
     *
     * @author Rowan Crowther
     */
    private MonsterRace race;
    /**
     * The monster base to draw a random shape from, for a generic (whole-family) shapechange.
     * Mutually exclusive with {@link #race}: exactly one is set once the shape is resolved.
     *
     * @author Rowan Crowther
     */
    private MonsterBase base;

    /**
     * Build an unresolved shape from its name. The {@link #base} or {@link #race} it refers to is
     * filled in later by {@link MonsterRace#resolveShapes()}, once every race has loaded.
     *
     * @param name the target base or race name, as written in {@code monster.txt}
     * @author Rowan Crowther
     */
    public MonsterShape(String name) {
        this.name = name;
    }

    /**
     * @return the shape's target name, used to resolve it to a base or race
     * @author Rowan Crowther
     */
    public String getName() {
        return name;
    }

    /**
     * @return the specific race this shape becomes, or {@code null} if it is a by-base shape
     * @author Rowan Crowther
     */
    public MonsterRace getRace() {
        return race;
    }

    /**
     * @return the base whose family this shape draws from, or {@code null} if it names a specific race
     * @author Rowan Crowther
     */
    public MonsterBase getBase() {
        return base;
    }

    /**
     * Resolve this shape to a specific race (clearing any base).
     *
     * @param race the race this shape becomes
     * @author Rowan Crowther
     */
    public void setRace(MonsterRace race) {
        this.race = race;
    }

    /**
     * Resolve this shape to a base family (clearing any race).
     *
     * @param base the base this shape draws a random member from
     * @author Rowan Crowther
     */
    public void setBase(MonsterBase base) {
        this.base = base;
    }
}
