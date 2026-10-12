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
import uk.co.jackoftradesltd.middle.cave.Loc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests the grid a {@link Monster} holds. C's {@code mon->grid} is a {@code struct loc} filled with
 * zeros by {@code mem_zalloc} until the monster is placed, so it is never a null pointer. The port
 * stores {@link Loc#zero} for a {@code null} constructor argument, and {@link Monster#getGrid()}
 * never answers {@code null}.
 *
 * <p>Class MonsterGridTest coded on 261012, commented in full on 261012.
 *
 * @author Rowan Crowther
 */
@DisplayName("Monster grid")
class MonsterGridTest {

    /**
     * Builds a bare monster at the given grid. Every other collaborator is null, which the
     * {@code Monster} constructor tolerates.
     *
     * @param grid the grid to give the constructor, possibly {@code null}
     * @return a shell monster
     */
    private static Monster monsterAt(Loc grid) {
        return new Monster(null, null, grid, 0, 0, null, 0, 0, 0, null,
                null, null, null, null, null, null, null, 0, 0);
    }

    @Test
    @DisplayName("a null grid is stored as Loc.zero, C's zeroed mon->grid")
    void nullGridBecomesOrigin() {
        assertSame(Loc.zero, monsterAt(null).getGrid());
    }

    @Test
    @DisplayName("a given grid is stored as given, the Loc itself and not a copy")
    void givenGridIsKept() {
        Loc grid = Loc.row(4).col(7);

        Monster monster = monsterAt(grid);

        assertSame(grid, monster.getGrid());
        assertEquals(7, monster.getGrid().getX());
        assertEquals(4, monster.getGrid().getY());
    }

    @Test
    @DisplayName("an explicit origin is an ordinary grid: it is kept and equals Loc.zero")
    void explicitOriginIsKept() {
        Loc origin = Loc.row(0).col(0);

        assertSame(origin, monsterAt(origin).getGrid());
        assertEquals(Loc.zero, monsterAt(origin).getGrid());
    }
}
