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
 * directly, without going through {@link RedrawRouter}, which has its own test. Covers the HP/SP
 * pair and the title/race/class/name quartet the same way: a value written to one field is the
 * value read back from it, and no field disturbs another.
 *
 * <p>Class SidebarModelTest coded on 260926, commented in full on 260928.
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
    private String savedTitle;
    private String savedClassName;
    private String savedRaceName;
    private String savedName;
    private boolean savedShapechanged;
    private boolean savedWizard;
    private boolean savedTotalWinner;
    private String savedShapeName;
    private int savedLevel;
    private int savedMaxLevel;
    private long savedGold;

    @BeforeEach
    void saveModel() {
        savedCurrentHp = SidebarModel.getCurrentHP();
        savedMaxHp = SidebarModel.getMaxHP();
        savedCurrentSp = SidebarModel.getCurrentSP();
        savedMaxSp = SidebarModel.getMaxSP();
        savedTitle = SidebarModel.getTitle();
        savedClassName = SidebarModel.getClassName();
        savedRaceName = SidebarModel.getRaceName();
        savedName = SidebarModel.getName();
        savedShapechanged = SidebarModel.isPlayerIsShapechanged();
        savedWizard = SidebarModel.isWizard();
        savedTotalWinner = SidebarModel.isTotalWinner();
        savedShapeName = SidebarModel.getShapeName();
        savedLevel = SidebarModel.getLevel();
        savedMaxLevel = SidebarModel.getMaxLevel();
        savedGold = SidebarModel.getGold();
    }

    @AfterEach
    void restoreModel() {
        SidebarModel.setCurrentHP(savedCurrentHp);
        SidebarModel.setMaxHP(savedMaxHp);
        SidebarModel.setCurrentSP(savedCurrentSp);
        SidebarModel.setMaxSP(savedMaxSp);
        SidebarModel.setTitle(savedTitle);
        SidebarModel.setClassName(savedClassName);
        SidebarModel.setRaceName(savedRaceName);
        SidebarModel.setName(savedName);
        SidebarModel.setPlayerIsShapechanged(savedShapechanged);
        SidebarModel.setWizard(savedWizard);
        SidebarModel.setTotalWinner(savedTotalWinner);
        SidebarModel.setShapeName(savedShapeName);
        SidebarModel.setLevel(savedLevel);
        SidebarModel.setMaxLevel(savedMaxLevel);
        SidebarModel.setGold(savedGold);
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

    /**
     * A written title is the title read back, unrelated to whatever race, class or name is already
     * held.
     */
    @Test
    void titleRoundTrips() {
        SidebarModel.setRaceName("Elf");
        SidebarModel.setClassName("Ranger");
        SidebarModel.setName("Legolas");
        SidebarModel.setTitle("Rogue");

        assertEquals("Rogue", SidebarModel.getTitle());
        assertEquals("Elf", SidebarModel.getRaceName(), "writing title must not disturb race");
        assertEquals("Ranger", SidebarModel.getClassName(), "writing title must not disturb class");
        assertEquals("Legolas", SidebarModel.getName(), "writing title must not disturb name");
    }

    /**
     * A written class name is the class name read back, unrelated to the other three fields.
     */
    @Test
    void classNameRoundTrips() {
        SidebarModel.setTitle("Rogue");
        SidebarModel.setRaceName("Elf");
        SidebarModel.setName("Legolas");
        SidebarModel.setClassName("Ranger");

        assertEquals("Ranger", SidebarModel.getClassName());
        assertEquals("Rogue", SidebarModel.getTitle(), "writing class must not disturb title");
        assertEquals("Elf", SidebarModel.getRaceName(), "writing class must not disturb race");
        assertEquals("Legolas", SidebarModel.getName(), "writing class must not disturb name");
    }

    /**
     * A written race name is the race name read back, unrelated to the other three fields.
     */
    @Test
    void raceNameRoundTrips() {
        SidebarModel.setTitle("Rogue");
        SidebarModel.setClassName("Ranger");
        SidebarModel.setName("Legolas");
        SidebarModel.setRaceName("Elf");

        assertEquals("Elf", SidebarModel.getRaceName());
        assertEquals("Rogue", SidebarModel.getTitle(), "writing race must not disturb title");
        assertEquals("Ranger", SidebarModel.getClassName(), "writing race must not disturb class");
        assertEquals("Legolas", SidebarModel.getName(), "writing race must not disturb name");
    }

    /**
     * A written full name is the name read back, unrelated to the other three fields.
     */
    @Test
    void nameRoundTrips() {
        SidebarModel.setTitle("Rogue");
        SidebarModel.setClassName("Ranger");
        SidebarModel.setRaceName("Elf");
        SidebarModel.setName("Legolas");

        assertEquals("Legolas", SidebarModel.getName());
        assertEquals("Rogue", SidebarModel.getTitle(), "writing name must not disturb title");
        assertEquals("Ranger", SidebarModel.getClassName(), "writing name must not disturb class");
        assertEquals("Elf", SidebarModel.getRaceName(), "writing name must not disturb race");
    }

    /**
     * A written shapechanged flag is the flag read back, unrelated to the race/class/title/name
     * quartet - and {@code false} round-trips as faithfully as {@code true} does, since a player
     * who has resumed their normal shape is a real, not an "unset", state.
     */
    @Test
    void playerIsShapechangedRoundTrips() {
        SidebarModel.setRaceName("Elf");
        SidebarModel.setClassName("Ranger");

        SidebarModel.setPlayerIsShapechanged(true);
        assertEquals(true, SidebarModel.isPlayerIsShapechanged());
        assertEquals("Elf", SidebarModel.getRaceName(), "writing shapechanged must not disturb race");

        SidebarModel.setPlayerIsShapechanged(false);
        assertEquals(false, SidebarModel.isPlayerIsShapechanged());
        assertEquals("Ranger", SidebarModel.getClassName(), "writing shapechanged must not disturb class");
    }

    /**
     * A written wizard flag is the flag read back, unrelated to the total-winner flag - and
     * {@code false} round-trips as faithfully as {@code true} does.
     */
    @Test
    void wizardRoundTrips() {
        SidebarModel.setTotalWinner(true);

        SidebarModel.setWizard(true);
        assertEquals(true, SidebarModel.isWizard());
        assertEquals(true, SidebarModel.isTotalWinner(), "writing wizard must not disturb totalWinner");

        SidebarModel.setWizard(false);
        assertEquals(false, SidebarModel.isWizard());
    }

    /**
     * A written total-winner flag is the flag read back, unrelated to the wizard flag - and
     * {@code false} round-trips as faithfully as {@code true} does.
     */
    @Test
    void totalWinnerRoundTrips() {
        SidebarModel.setWizard(true);

        SidebarModel.setTotalWinner(true);
        assertEquals(true, SidebarModel.isTotalWinner());
        assertEquals(true, SidebarModel.isWizard(), "writing totalWinner must not disturb wizard");

        SidebarModel.setTotalWinner(false);
        assertEquals(false, SidebarModel.isTotalWinner());
    }

    /**
     * A written shape name is the name read back, unrelated to the wizard/total-winner pair.
     */
    @Test
    void shapeNameRoundTrips() {
        SidebarModel.setWizard(true);
        SidebarModel.setTotalWinner(true);
        SidebarModel.setShapeName("Wolf");

        assertEquals("Wolf", SidebarModel.getShapeName());
        assertEquals(true, SidebarModel.isWizard(), "writing shapeName must not disturb wizard");
        assertEquals(true, SidebarModel.isTotalWinner(), "writing shapeName must not disturb totalWinner");
    }

    /**
     * A written current level is the level read back, unrelated to whatever maximum level is
     * already held.
     */
    @Test
    void levelRoundTrips() {
        SidebarModel.setMaxLevel(20);
        SidebarModel.setLevel(15);

        assertEquals(15, SidebarModel.getLevel());
        assertEquals(20, SidebarModel.getMaxLevel(), "writing level must not disturb maxLevel");
    }

    /**
     * A written maximum level is the level read back, unrelated to whatever current level is
     * already held.
     */
    @Test
    void maxLevelRoundTrips() {
        SidebarModel.setLevel(15);
        SidebarModel.setMaxLevel(20);

        assertEquals(20, SidebarModel.getMaxLevel());
        assertEquals(15, SidebarModel.getLevel(), "writing maxLevel must not disturb level");
    }

    /**
     * A written gold total is the total read back, unrelated to whatever level pair is already
     * held.
     */
    @Test
    void goldRoundTrips() {
        SidebarModel.setLevel(15);
        SidebarModel.setGold(1234L);

        assertEquals(1234L, SidebarModel.getGold());
        assertEquals(15, SidebarModel.getLevel(), "writing gold must not disturb level");
    }

    /**
     * Zero gold is a value a player can genuinely be at (broke at birth or after spending it all),
     * so it must round-trip like any other value rather than being mistaken for "unset".
     */
    @Test
    void zeroGoldRoundTrips() {
        SidebarModel.setGold(0L);

        assertEquals(0L, SidebarModel.getGold());
    }
}
