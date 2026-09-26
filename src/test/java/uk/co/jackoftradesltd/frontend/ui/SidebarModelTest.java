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

package uk.co.jackoftradesltd.frontend.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * {@link SidebarModel} holding exactly what is written to it - the port has no C counterpart to
 * diverge from here, since C's {@code prt_hp} ({@code [C] ui-display.c}, function {@code prt_hp})
 * reads {@code player->chp}/{@code mhp} straight off the shared global rather than through a model
 * at all. What is under test is the model's own contract: a value written to one field is the value
 * read back from it, and the two fields do not alias each other.
 *
 * <p>In the same package as {@link SidebarModel} so the package-private setters can be exercised
 * directly, without going through {@link RedrawRouter}, which has its own test.
 *
 * <p>Class SidebarModelTest coded on 260926, commented in full on 260926.
 *
 * @author Rowan Crowther
 */
class SidebarModelTest {

    /**
     * The model's state before this test ran, since both fields are static and shared across the
     * whole suite.
     */
    private int savedCurrentHp;
    private int savedMaxHp;

    @BeforeEach
    void saveModel() {
        savedCurrentHp = SidebarModel.getCurrentHP();
        savedMaxHp = SidebarModel.getMaxHP();
    }

    @AfterEach
    void restoreModel() {
        SidebarModel.setCurrentHP(savedCurrentHp);
        SidebarModel.setMaxHP(savedMaxHp);
    }

    /**
     * A written current-HP value is the value read back, unrelated to whatever maximum is already
     * held.
     */
    @Test
    void currentHpRoundTrips() {
        SidebarModel.setMaxHP(50);
        SidebarModel.setCurrentHP(23);

        assertEquals(23, SidebarModel.getCurrentHP());
        assertEquals(50, SidebarModel.getMaxHP(), "writing current must not disturb max");
    }

    /**
     * A written maximum-HP value is the value read back, unrelated to whatever current is already
     * held.
     */
    @Test
    void maxHpRoundTrips() {
        SidebarModel.setCurrentHP(23);
        SidebarModel.setMaxHP(50);

        assertEquals(50, SidebarModel.getMaxHP());
        assertEquals(23, SidebarModel.getCurrentHP(), "writing max must not disturb current");
    }

    /**
     * Zero is a value a player can genuinely be at (0 HP, or an as-yet-uninitialised maximum before
     * birth), so it must round-trip like any other value rather than being mistaken for "unset".
     */
    @Test
    void zeroRoundTripsForBothFields() {
        SidebarModel.setCurrentHP(0);
        SidebarModel.setMaxHP(0);

        assertEquals(0, SidebarModel.getCurrentHP());
        assertEquals(0, SidebarModel.getMaxHP());
    }
}
