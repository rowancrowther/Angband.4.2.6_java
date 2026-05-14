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

package uk.co.jackoftrades.middle.enums;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TrapEnumTest {
    private final TrapEnum none = TrapEnum.TRF_NONE;
    private final TrapEnum glyph = TrapEnum.TRF_GLYPH;
    private final TrapEnum trapEnum = TrapEnum.TRF_TRAP;
    private final TrapEnum visible = TrapEnum.TRF_VISIBLE;
    private final TrapEnum invisible = TrapEnum.TRF_INVISIBLE;
    private final TrapEnum floor = TrapEnum.TRF_FLOOR;
    private final TrapEnum down = TrapEnum.TRF_DOWN;
    private final TrapEnum pit = TrapEnum.TRF_PIT;
    private final TrapEnum oneTime = TrapEnum.TRF_ONETIME;
    private final TrapEnum magical = TrapEnum.TRF_MAGICAL;
    private final TrapEnum saveThrow = TrapEnum.TRF_SAVE_THROW;
    private final TrapEnum saveArmour = TrapEnum.TRF_SAVE_ARMOUR;
    private final TrapEnum lock = TrapEnum.TRF_LOCK;
    private final TrapEnum delay = TrapEnum.TRF_DELAY;
    private final TrapEnum web = TrapEnum.TRF_WEB;

    ArrayList<TrapEnum> allValues;

    @BeforeEach
    void setUp() {
        allValues = new ArrayList<>();
        allValues.add(none);
        allValues.add(glyph);
        allValues.add(trapEnum);
        allValues.add(visible);
        allValues.add(invisible);
        allValues.add(floor);
        allValues.add(down);
        allValues.add(pit);
        allValues.add(oneTime);
        allValues.add(magical);
        allValues.add(saveThrow);
        allValues.add(saveArmour);
        allValues.add(lock);
        allValues.add(delay);
        allValues.add(web);
    }

    @Test
    void getDescription() {
        assertAll(
                () -> assertEquals("", none.getDescription()),
                () -> assertEquals("Is a glyph", glyph.getDescription()),
                () -> assertEquals("Is a player trapEnum", trapEnum.getDescription()),
                () -> assertEquals("Is visible", visible.getDescription()),
                () -> assertEquals("Is invisible", invisible.getDescription()),
                () -> assertEquals("Can be set on a floor", floor.getDescription()),
                () -> assertEquals("Takes the player down a level", down.getDescription()),
                () -> assertEquals("Moves the player onto the trapEnum", pit.getDescription()),
                () -> assertEquals("Disappears after being activated", oneTime.getDescription()),
                () -> assertEquals("Has magical activation _absence of this flag means physical),", magical.getDescription()),
                () -> assertEquals("Allows a save from all effects by standard saving throw", saveThrow.getDescription()),
                () -> assertEquals("Allows a save from all effects due to AC", saveArmour.getDescription()),
                () -> assertEquals("Is a door lock", lock.getDescription()),
                () -> assertEquals("Has a delayed effect", delay.getDescription()),
                () -> assertEquals("Is a web", web.getDescription())
        );
    }

    @Test
    void values() {
        for (TrapEnum t : TrapEnum.values()) {
            if (!allValues.contains(t))
                fail("TrapEnum: " + t.toString() + " not found in manual list.");
        }
    }

    @Test
    void valueOf() {
        assertAll(
                () -> assertEquals(none, TrapEnum.valueOf("TRF_NONE")),
                () -> assertEquals(glyph, TrapEnum.valueOf("TRF_GLYPH")),
                () -> assertEquals(trapEnum, TrapEnum.valueOf("TRF_TRAP")),
                () -> assertEquals(visible, TrapEnum.valueOf("TRF_VISIBLE")),
                () -> assertEquals(invisible, TrapEnum.valueOf("TRF_INVISIBLE")),
                () -> assertEquals(floor, TrapEnum.valueOf("TRF_FLOOR")),
                () -> assertEquals(down, TrapEnum.valueOf("TRF_DOWN")),
                () -> assertEquals(pit, TrapEnum.valueOf("TRF_PIT")),
                () -> assertEquals(oneTime, TrapEnum.valueOf("TRF_ONETIME")),
                () -> assertEquals(magical, TrapEnum.valueOf("TRF_MAGICAL")),
                () -> assertEquals(saveThrow, TrapEnum.valueOf("TRF_SAVE_THROW")),
                () -> assertEquals(saveArmour, TrapEnum.valueOf("TRF_SAVE_ARMOUR")),
                () -> assertEquals(lock, TrapEnum.valueOf("TRF_LOCK")),
                () -> assertEquals(delay, TrapEnum.valueOf("TRF_DELAY")),
                () -> assertEquals(web, TrapEnum.valueOf("TRF_WEB"))
        );
    }
}