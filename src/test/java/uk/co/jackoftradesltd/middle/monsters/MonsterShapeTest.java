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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link MonsterShape} against {@code struct monster_shape} in {@code monster.h} and C's
 * {@code parse_monster_shape} in {@code mon-init.c}.
 *
 * <p>The class is a plain holder, so the tests state what C's zero-filled record and plain pointer
 * assignments give: a new shape has its name and nothing else, and setting the race or the base
 * leaves the other field alone. {@code tests/parse/r-info.c} ({@code test_shape0}) is the source of
 * the two example names: {@code townsfolk}, which is a base, and {@code blubbering idiot}, which is
 * a race. Resolving a name to a base or a race is {@link MonsterRace#resolveShapes()}'s job and is
 * pinned in {@code MonsterRaceResolutionTest}.
 *
 * <p>Class MonsterShapeTest coded on 261011, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
class MonsterShapeTest {

    /**
     * A new shape carries its name, with both targets unset as in C's {@code mem_zalloc} record
     * (C's {@code test_shape0} checks {@code name} and, for a race name, a {@code NULL} base).
     */
    @Test
    @DisplayName("a new shape has its name and no base or race")
    void newShapeIsUnresolved() {
        MonsterShape shape = new MonsterShape("blubbering idiot");

        assertEquals("blubbering idiot", shape.getName());
        assertNull(shape.getBase());
        assertNull(shape.getRace());
    }

    /**
     * The name is stored as written, with no case folding or trimming, because the base lookup in C
     * is an exact, case-sensitive match on it.
     */
    @Test
    @DisplayName("the name is kept exactly as written")
    void nameIsKeptVerbatim() {
        assertEquals("Townsfolk", new MonsterShape("Townsfolk").getName());
        assertEquals(" townsfolk ", new MonsterShape(" townsfolk ").getName());
        assertEquals("", new MonsterShape("").getName());
    }

    /**
     * Setting the base stores it by identity, as C's {@code s->base = ...} does, and leaves the race
     * as it was.
     */
    @Test
    @DisplayName("setBase stores the base and leaves the race alone")
    void setBaseLeavesRace() {
        MonsterBase townsfolk = new MonsterBase("townsfolk");
        MonsterShape shape = new MonsterShape("townsfolk");

        shape.setBase(townsfolk);

        assertSame(townsfolk, shape.getBase());
        assertNull(shape.getRace());
    }

    /**
     * Setting the race stores it by identity and leaves the base as it was, as C's
     * {@code s->race = ...} does.
     */
    @Test
    @DisplayName("setRace stores the race and leaves the base alone")
    void setRaceLeavesBase() {
        MonsterRace idiot = race("blubbering idiot");
        MonsterShape shape = new MonsterShape("blubbering idiot");

        shape.setRace(idiot);

        assertSame(idiot, shape.getRace());
        assertNull(shape.getBase());
    }

    /**
     * The two setters are independent plain assignments: a shape given both keeps both, and it is
     * the caller's job (as {@link MonsterRace#resolveShapes()} does) to clear the other one.
     */
    @Test
    @DisplayName("the setters do not clear each other")
    void settersAreIndependent() {
        MonsterBase base = new MonsterBase("townsfolk");
        MonsterRace idiot = race("blubbering idiot");
        MonsterShape shape = new MonsterShape("odd");

        shape.setBase(base);
        shape.setRace(idiot);

        assertSame(base, shape.getBase());
        assertSame(idiot, shape.getRace());
    }

    /**
     * Passing {@code null} clears a target, which is how a resolver makes the two alternatives
     * exclusive.
     */
    @Test
    @DisplayName("null clears a target")
    void nullClears() {
        MonsterShape shape = new MonsterShape("townsfolk");
        shape.setBase(new MonsterBase("townsfolk"));
        shape.setRace(race("blubbering idiot"));

        shape.setBase(null);
        shape.setRace(null);

        assertNull(shape.getBase());
        assertNull(shape.getRace());
    }

    /**
     * Resetting a target replaces it, and the name never changes through either setter.
     */
    @Test
    @DisplayName("a later set replaces the earlier target and the name is unchanged")
    void laterSetReplaces() {
        MonsterBase first = new MonsterBase("townsfolk");
        MonsterBase second = new MonsterBase("dragon");
        MonsterShape shape = new MonsterShape("townsfolk");

        shape.setBase(first);
        shape.setBase(second);

        assertSame(second, shape.getBase());
        assertEquals("townsfolk", shape.getName());
    }

    /**
     * A bare race for the setters to hold.
     *
     * @param name the race's name
     * @return the race
     */
    private static MonsterRace race(String name) {
        return new MonsterRace(name, "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                null, null, List.of(), 0, 0, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), 0, null);
    }
}
