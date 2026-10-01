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

package uk.co.jackoftradesltd.middle.combat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.cave.Square;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.LevelMaxData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Target#setTargetMonster}, {@link Target#getTargetMonster} and the private
 * {@code targetable} they depend on, ports of {@code target_set_monster}, {@code target_get_monster}
 * and {@code target_able} in C's {@code target.c}.
 *
 * <p>Expected values come from reading the C. {@code target_able} is a short-circuit chain: the
 * monster exists, has a race, is obvious (visible and not camouflaged), is reachable by a
 * {@code PROJECT_NONE} projection, and the player is not under {@code TMD_IMAGE}. In
 * {@code target_set_monster} a targetable monster sets the target, and anything else either keeps
 * the target (index zeroed, grid kept) when it is fixed, or resets index and grid to zero.
 *
 * <p>The target is static state with no reset method, so the fixture clears it through the public
 * method with the fixed flag off, and reads and sets the private fields by reflection.
 *
 * <p>Test class TargetTest coded on 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class TargetTest {

    /**
     * The level's size in both directions.
     */
    private static final int SIZE = 30;

    /**
     * The player's maximum projection range for the fixture.
     */
    private static final int MAX_RANGE = 20;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the fixture installs.
     */
    private Player player;

    /**
     * The player that was current before the fixture installed its own.
     */
    private Player realPlayer;

    /**
     * The level that was current before the fixture installed its own.
     */
    private Chunk realCave;

    /**
     * The constants table that was in place before the fixture replaced it.
     */
    private GameConstantsData realConstants;

    /**
     * A terrain feature carrying the given flags and nothing else.
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags)
            set.on(flag);
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class), 0);
    }

    /**
     * Shorthand for a grid, in column/row order.
     */
    private static Loc at(int x, int y) {
        return Loc.row(y).col(x);
    }

    /**
     * Sets a private static field of {@link Target}.
     */
    private static void setField(String name, Object value) throws ReflectiveOperationException {
        Field field = Target.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * Reads a private static field of {@link Target}.
     */
    private static Object field(String name) throws ReflectiveOperationException {
        Field field = Target.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }

    /**
     * Sets a grid's terrain; {@code Square.setFeature} is package-private, so it is reached by
     * reflection.
     */
    private void setFeature(Loc grid, Feature feature) throws ReflectiveOperationException {
        Square square = level.getSquare(grid);
        Method method = Square.class.getDeclaredMethod("setFeature", Feature.class);
        method.setAccessible(true);
        method.invoke(square, feature);
    }

    /**
     * Builds a monster of the given index at the given grid. Visible monsters carry
     * {@code MFLAG_VISIBLE}; a camouflaged one carries {@code MFLAG_CAMOUFLAGE} as well.
     */
    private Monster monster(int index, Loc grid, boolean visible, boolean camouflaged, MonsterRace race) {
        Monster mon = new Monster(race, null, grid, 0, 0, null, 0, 0, 0,
                new Flag<>(MonsterFlag.class), null, null, null, null, null, null, null, 0, 0);
        mon.setMonIndex(index);
        if (visible)
            mon.monsterFlagOn(MonsterFlag.MFLAG_VISIBLE);
        if (camouflaged)
            mon.monsterFlagOn(MonsterFlag.MFLAG_CAMOUFLAGE);
        return mon;
    }

    /**
     * A visible, uncamouflaged monster with a race, the plain targetable case.
     */
    private Monster good(int index, Loc grid) {
        return monster(index, grid, true, false, new MonsterRace());
    }

    /**
     * Swaps the constants table for one with a known range, installs a player at (0,0) and an open
     * level, and clears the static target.
     */
    @BeforeEach
    void fixture() throws ReflectiveOperationException {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        realConstants = (GameConstantsData) data.get(null);
        data.set(null, new GameConstantsData(
                new LevelMaxData(1024), null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, new PlayerData(20, MAX_RANGE, 0, 0),
                null, null, null, null, null, null, null, null));

        player = new Player();
        Field grid = Player.class.getDeclaredField("grid");
        grid.setAccessible(true);
        grid.set(player, at(0, 0));
        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                SIZE, SIZE, 0, 4, 3, 0, 0, 0, player);
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++)
                setFeature(at(x, y), feature(TerrainFeatureFlags.TF_PROJECT, TerrainFeatureFlags.TF_PASSABLE));
        realCave = GameState.getCave();
        GameState.setCave(level);

        setField("targetFixed", false);
        Target.setTargetMonster(null);
    }

    /**
     * Puts the original player, level and constants table back and clears the target.
     */
    @AfterEach
    void restore() throws ReflectiveOperationException {
        setField("targetFixed", false);
        Target.setTargetMonster(null);
        GameState.setPlayer(realPlayer);
        GameState.setCave(realCave);
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, realConstants);
    }

    @Test
    @DisplayName("a visible, projectable monster becomes the target: set flag, index from the monster, its grid")
    void ordinaryTarget() throws ReflectiveOperationException {
        Monster mon = good(3, at(5, 0));
        level.getMonsters()[3] = mon;

        assertTrue(Target.setTargetMonster(mon));

        assertEquals(true, field("targetSet"));
        assertEquals(3, field("targetMonsterIndex"));
        assertEquals(at(5, 0), field("grid"));
        assertSame(mon, Target.getTargetMonster(level));
    }

    @Test
    @DisplayName("the stored index is the monster's own index, not a caller's: a different monster gives a different index")
    void indexComesFromMonster() throws ReflectiveOperationException {
        Monster mon = good(7, at(2, 2));
        level.getMonsters()[7] = mon;

        Target.setTargetMonster(mon);

        assertEquals(7, field("targetMonsterIndex"));
        assertSame(mon, Target.getTargetMonster(level));
    }

    @Test
    @DisplayName("a null monster with the target not fixed resets the target and returns false")
    void nullResets() throws ReflectiveOperationException {
        Monster mon = good(3, at(5, 0));
        level.getMonsters()[3] = mon;
        Target.setTargetMonster(mon);

        assertFalse(Target.setTargetMonster(null));

        assertEquals(false, field("targetSet"));
        assertEquals(0, field("targetMonsterIndex"));
        assertNull(field("monster"));
        assertEquals(Loc.zero, field("grid"));
        assertNull(Target.getTargetMonster(level));
    }

    @Test
    @DisplayName("an invisible monster is not targetable: the target is reset")
    void invisible() throws ReflectiveOperationException {
        Monster mon = monster(3, at(5, 0), false, false, new MonsterRace());
        assertFalse(Target.setTargetMonster(mon));
        assertEquals(false, field("targetSet"));
    }

    @Test
    @DisplayName("a camouflaged monster is not obvious, so not targetable even when visible")
    void camouflaged() throws ReflectiveOperationException {
        Monster mon = monster(3, at(5, 0), true, true, new MonsterRace());
        assertFalse(Target.setTargetMonster(mon));
        assertEquals(false, field("targetSet"));
    }

    @Test
    @DisplayName("a monster with no race is not targetable: C's m->race test")
    void noRace() throws ReflectiveOperationException {
        Monster mon = monster(3, at(5, 0), true, false, null);
        assertFalse(Target.setTargetMonster(mon));
        assertEquals(false, field("targetSet"));
    }

    @Test
    @DisplayName("a monster out of projection range is not targetable: max range is reachable, one more is not")
    void outOfRange() throws ReflectiveOperationException {
        assertTrue(Target.setTargetMonster(good(1, at(MAX_RANGE, 0))));
        assertFalse(Target.setTargetMonster(good(2, at(MAX_RANGE + 1, 0))));
        assertEquals(false, field("targetSet"));
    }

    @Test
    @DisplayName("a monster behind a wall is not targetable")
    void behindWall() throws ReflectiveOperationException {
        setFeature(at(3, 0), feature());
        assertFalse(Target.setTargetMonster(good(1, at(6, 0))));
    }

    @Test
    @DisplayName("hallucination (TMD_IMAGE) makes every monster untargetable")
    void hallucinating() throws ReflectiveOperationException {
        player.putTimed(TimedEffect.TMD_IMAGE, 10);
        assertFalse(Target.setTargetMonster(good(1, at(5, 0))));
        assertEquals(false, field("targetSet"));
    }

    @Test
    @DisplayName("fixed target: a failing monster zeroes the index but keeps the grid and the set flag, and returns true")
    void fixedKeepsGrid() throws ReflectiveOperationException {
        Monster mon = good(3, at(5, 0));
        level.getMonsters()[3] = mon;
        Target.setTargetMonster(mon);
        setField("targetFixed", true);

        player.putTimed(TimedEffect.TMD_IMAGE, 10);
        assertTrue(Target.setTargetMonster(mon));

        assertEquals(0, field("targetMonsterIndex"));
        assertNull(field("monster"));
        assertEquals(at(5, 0), field("grid"));
        assertEquals(true, field("targetSet"));
    }

    @Test
    @DisplayName("fixed target: a null monster is also kept, with the index zeroed")
    void fixedNull() throws ReflectiveOperationException {
        Monster mon = good(3, at(5, 0));
        level.getMonsters()[3] = mon;
        Target.setTargetMonster(mon);
        setField("targetFixed", true);

        assertTrue(Target.setTargetMonster(null));

        assertEquals(0, field("targetMonsterIndex"));
        assertEquals(at(5, 0), field("grid"));
        assertNull(Target.getTargetMonster(level));
    }

    @Test
    @DisplayName("fixed target: a targetable monster still replaces the target as normal")
    void fixedStillRetargets() throws ReflectiveOperationException {
        setField("targetFixed", true);
        Monster mon = good(4, at(6, 0));
        level.getMonsters()[4] = mon;

        assertTrue(Target.setTargetMonster(mon));

        assertEquals(4, field("targetMonsterIndex"));
        assertEquals(at(6, 0), field("grid"));
    }

    @Test
    @DisplayName("getTargetMonster is null for index zero and for an empty slot")
    void getTargetMonsterEmpty() throws ReflectiveOperationException {
        assertNull(Target.getTargetMonster(level));
        setField("targetMonsterIndex", 9);
        assertNull(Target.getTargetMonster(level));
    }
}
