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
 * diverge from here, since C's {@code prt_hp}/{@code prt_sp} ({@code [C] ui-display.c}, functions
 * {@code prt_hp} and {@code prt_sp}) read {@code player->chp}/{@code mhp} and
 * {@code player->csp}/{@code msp} straight off the shared global rather than through a model at
 * all. What is under test is the model's own contract: a value written to one field is the value
 * read back from it, and no two fields alias each other.
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
     * The model's state before this test ran, since all four fields are static and shared across
     * the whole suite.
     */
    private int savedCurrentHp;
    private int savedMaxHp;
    private int savedCurrentSp;
    private int savedMaxSp;

    @BeforeEach
    void saveModel() {
        savedCurrentHp = SidebarModel.getCurrentHP();
        savedMaxHp = SidebarModel.getMaxHP();
        savedCurrentSp = SidebarModel.getCurrentSP();
        savedMaxSp = SidebarModel.getMaxSP();
    }

    @AfterEach
    void restoreModel() {
        SidebarModel.setCurrentHP(savedCurrentHp);
        SidebarModel.setMaxHP(savedMaxHp);
        SidebarModel.setCurrentSP(savedCurrentSp);
        SidebarModel.setMaxSP(savedMaxSp);
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

    /**
     * A written current-SP value is the value read back, unrelated to whatever maximum, or the HP
     * pair, is already held.
     */
    @Test
    void currentSpRoundTrips() {
        SidebarModel.setMaxSP(30);
        SidebarModel.setCurrentSP(11);

        assertEquals(11, SidebarModel.getCurrentSP());
        assertEquals(30, SidebarModel.getMaxSP(), "writing current must not disturb max");
    }

    /**
     * A written maximum-SP value is the value read back, unrelated to whatever current, or the HP
     * pair, is already held.
     */
    @Test
    void maxSpRoundTrips() {
        SidebarModel.setCurrentSP(11);
        SidebarModel.setMaxSP(30);

        assertEquals(30, SidebarModel.getMaxSP());
        assertEquals(11, SidebarModel.getCurrentSP(), "writing max must not disturb current");
    }

    /**
     * Zero is a value a player can genuinely be at (0 SP), so the SP pair must round-trip zero
     * like any other value too.
     */
    @Test
    void zeroRoundTripsForBothSpFields() {
        SidebarModel.setCurrentSP(0);
        SidebarModel.setMaxSP(0);

        assertEquals(0, SidebarModel.getCurrentSP());
        assertEquals(0, SidebarModel.getMaxSP());
    }

    /**
     * The HP and SP pairs are independent fields, not aliases of one another: writing one must not
     * disturb the other.
     */
    @Test
    void hpAndSpDoNotAliasEachOther() {
        SidebarModel.setCurrentHP(42);
        SidebarModel.setMaxHP(50);
        SidebarModel.setCurrentSP(8);
        SidebarModel.setMaxSP(12);

        assertEquals(42, SidebarModel.getCurrentHP());
        assertEquals(50, SidebarModel.getMaxHP());
        assertEquals(8, SidebarModel.getCurrentSP());
        assertEquals(12, SidebarModel.getMaxSP());
    }
}
