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

/**
 * One shape a monster can change into, the Java form of C's {@code struct monster_shape}
 * ({@code monster.h}). A {@code shape:} line in {@code monster.txt} names either a monster base, a
 * generic shapechange that draws a random race of that family, or one specific race.
 *
 * <p>C keeps a race's shapes as a singly linked list (the {@code next} pointer), built in reverse
 * file order, with a separate {@code num_shapes} count. Here the shapes sit in a
 * {@code List<MonsterShape>} on {@link MonsterRace}, in file order, so the class has no {@code next}.
 * C also frees {@code name} once the shape is resolved; here it is kept, which does no harm.
 *
 * <p>The class is a plain holder with no behaviour of its own. {@link MonsterRace#resolveShapes()}
 * fills in {@link #base} or {@link #race} after every race has loaded, and the shapechange code
 * (C's {@code monster_change_shape} in {@code mon-util.c}, not yet ported) reads them.
 *
 * <p>Class MonsterShape coded before 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class MonsterShape {
    /**
     * The name written on the {@code shape:} line, C's {@code shape->name}: a monster base's code
     * name or a race's name. It is what {@link MonsterRace#resolveShapes()} looks up.
     *
     * <p>Field name coded before 261011, commented in full on 261011.
     */
    private String name;
    /**
     * The specific race to change into, C's {@code shape->race}. It is {@code null} until
     * {@link MonsterRace#resolveShapes()} runs, and stays {@code null} for a shape that names a base.
     *
     * <p>Field race coded before 261011, commented in full on 261011.
     */
    private MonsterRace race;
    /**
     * The monster base to draw a random race from, C's {@code shape->base}, for a generic
     * (whole-family) shapechange. It is {@code null} until {@link MonsterRace#resolveShapes()} runs,
     * and stays {@code null} for a shape that names a specific race. Once resolved, exactly one of
     * {@link #race} and {@link #base} is set.
     *
     * <p>Field base coded before 261011, commented in full on 261011.
     */
    private MonsterBase base;

    /**
     * Builds an unresolved shape from its name, as C's {@code parse_monster_shape} does with a
     * zero-filled record. Both {@link #race} and {@link #base} start {@code null}. C also looks the
     * base up at this point; here {@link MonsterRace#resolveShapes()} does both lookups later, which
     * gives the same answer because every base loads before the races.
     *
     * <p>Constructor MonsterShape coded before 261011, commented in full on 261011.
     *
     * @param name the target base or race name, as written in {@code monster.txt}
     */
    public MonsterShape(String name) {
        this.name = name;
    }

    /**
     * Returns the name written on the {@code shape:} line, C's {@code shape->name}.
     *
     * <p>Function getName coded before 261011, commented in full on 261011.
     *
     * @return the shape's target name, used to resolve it to a base or race
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the specific race this shape becomes, C's {@code shape->race}.
     *
     * <p>Function getRace coded before 261011, commented in full on 261011.
     *
     * @return the race, or {@code null} if the shape is unresolved or names a base
     */
    public MonsterRace getRace() {
        return race;
    }

    /**
     * Returns the base whose family this shape draws from, C's {@code shape->base}.
     *
     * <p>Function getBase coded before 261011, commented in full on 261011.
     *
     * @return the base, or {@code null} if the shape is unresolved or names a specific race
     */
    public MonsterBase getBase() {
        return base;
    }

    /**
     * Sets the specific race this shape becomes, C's {@code s->race = lookup_monster(s->name)}. This
     * is a plain assignment: it does not touch {@link #base}, so a caller resolving to a race must
     * also pass {@code null} to {@link #setBase(MonsterBase)}, as {@link MonsterRace#resolveShapes()}
     * does.
     *
     * <p>Function setRace coded before 261011, commented in full on 261011.
     *
     * @param race the race this shape becomes, or {@code null} to clear it
     */
    public void setRace(MonsterRace race) {
        this.race = race;
    }

    /**
     * Sets the base this shape draws a random race from, C's
     * {@code s->base = lookup_monster_base(s->name)}. This is a plain assignment: it does not touch
     * {@link #race}, so a caller resolving to a base must also pass {@code null} to
     * {@link #setRace(MonsterRace)}, as {@link MonsterRace#resolveShapes()} does.
     *
     * <p>Function setBase coded before 261011, commented in full on 261011.
     *
     * @param base the base this shape draws from, or {@code null} to clear it
     */
    public void setBase(MonsterBase base) {
        this.base = base;
    }
}
