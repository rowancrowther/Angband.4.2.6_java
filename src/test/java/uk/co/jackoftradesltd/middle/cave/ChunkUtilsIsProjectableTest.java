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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.LevelMaxData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ChunkUtils#isProjectable}, the port of C's {@code projectable}
 * ({@code project.c}).
 *
 * <p>Expected values come from reading the C, not the Java. C answers {@code true} only when
 * {@code project_path} returns a non-empty path whose <em>last</em> grid is passable and equal to
 * the requested grid. The cases are built to separate that from the likely slips: checking the
 * wrong end of the path (a single-grid path and a path stopped by a wall), reading the equality
 * test the wrong way round, and the range shortening, which in C applies only when
 * {@code PROJECT_SHORT} is set <em>and</em> {@code TMD_COVERTRACKS} is running, and divides the
 * range by four with integer division.
 *
 * <p>The range limit is taken from {@code GameConstants}, so the fixture swaps the constants table
 * for one with a known {@code maxRange} and restores the original afterwards.
 *
 * <p>Test class ChunkUtilsIsProjectableTest coded on 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkUtilsIsProjectableTest {

    /**
     * The level's size in both directions, larger than any distance below.
     */
    private static final int SIZE = 45;

    /**
     * The player's maximum range for the fixture.
     */
    private static final int MAX_RANGE = 20;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the method consults for {@code TMD_COVERTRACKS}.
     */
    private Player player;

    /**
     * The player that was current before the fixture installed its own, put back afterwards.
     */
    private Player realPlayer;

    /**
     * The constants table that was in place before the fixture replaced it, put back afterwards.
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
     * Builds a flag set from the given values.
     */
    private static Flag<ProjectEnum> set(ProjectEnum... flags) {
        Flag<ProjectEnum> set = new Flag<>(ProjectEnum.class);
        for (ProjectEnum flag : flags)
            set.on(flag);
        return set;
    }

    /**
     * Swaps the constants table for one whose only populated player entry is the range, installs a
     * fresh player, and builds a level whose every grid is both projectable and passable.
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
        realPlayer = GameState.getPlayer();
        GameState.setPlayer(player);

        level = new Chunk("test level", 0, 0, 0, 0, 0, false,
                SIZE, SIZE, 0, 4, 3, 0, 0, 0, player);
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++)
                level.getSquare(at(x, y)).setFeature(
                        feature(TerrainFeatureFlags.TF_PROJECT, TerrainFeatureFlags.TF_PASSABLE));
    }

    /**
     * Puts the original player and constants table back.
     */
    @AfterEach
    void restore() throws ReflectiveOperationException {
        GameState.setPlayer(realPlayer);
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, realConstants);
    }

    /**
     * Calls the method under test with the given flags, from (0,0) along the row to (x, 0).
     */
    private boolean along(int x, ProjectEnum... flags) {
        return ChunkUtils.isProjectable(level, at(0, 0), at(x, 0), set(flags));
    }

    @Test
    @DisplayName("no grid is projectable from itself: the path is empty")
    void sameGrid() {
        assertFalse(ChunkUtils.isProjectable(level, at(5, 5), at(5, 5), set(ProjectEnum.PROJECT_NONE)));
    }

    @Test
    @DisplayName("an adjacent open grid is projectable: the path is a single grid, the target itself")
    void adjacent() {
        assertTrue(along(1, ProjectEnum.PROJECT_NONE));
        assertTrue(ChunkUtils.isProjectable(level, at(5, 5), at(5, 4), set(ProjectEnum.PROJECT_NONE)));
        assertTrue(ChunkUtils.isProjectable(level, at(5, 5), at(4, 6), set(ProjectEnum.PROJECT_NONE)));
    }

    @Test
    @DisplayName("an open straight line, diagonal and slanted path all arrive")
    void openPaths() {
        assertTrue(along(10, ProjectEnum.PROJECT_NONE));
        assertTrue(ChunkUtils.isProjectable(level, at(5, 5), at(9, 9), set(ProjectEnum.PROJECT_NONE)));
        assertTrue(ChunkUtils.isProjectable(level, at(9, 3), at(3, 7), set(ProjectEnum.PROJECT_NONE)));
        assertTrue(ChunkUtils.isProjectable(level, at(2, 2), at(12, 5), set(ProjectEnum.PROJECT_NONE)));
    }

    @Test
    @DisplayName("a target that is projectable but not passable fails: the last grid must be passable")
    void targetNotPassable() {
        level.getSquare(at(6, 0)).setFeature(feature(TerrainFeatureFlags.TF_PROJECT));
        assertFalse(along(6, ProjectEnum.PROJECT_NONE));
    }

    @Test
    @DisplayName("a wall in the way stops the path short of the target, so the target is not reached")
    void wallInTheWay() {
        level.getSquare(at(4, 0)).setFeature(feature());
        assertFalse(along(8, ProjectEnum.PROJECT_NONE));
        // the grids short of the wall are still reachable
        assertTrue(along(3, ProjectEnum.PROJECT_NONE));
    }

    @Test
    @DisplayName("a wall as the target itself fails")
    void wallAsTarget() {
        level.getSquare(at(5, 0)).setFeature(feature());
        assertFalse(along(5, ProjectEnum.PROJECT_NONE));
    }

    @Test
    @DisplayName("the range limit: a straight path reaches exactly max range and no further")
    void rangeLimit() {
        assertTrue(along(MAX_RANGE, ProjectEnum.PROJECT_NONE));
        assertFalse(along(MAX_RANGE + 1, ProjectEnum.PROJECT_NONE));
        assertFalse(along(40, ProjectEnum.PROJECT_NONE));
    }

    @Test
    @DisplayName("PROJECT_SHORT with TMD_COVERTRACKS divides the range by four: 20 becomes 5")
    void shortenedRange() {
        player.putTimed(TimedEffect.TMD_COVERTRACKS, 10);
        assertTrue(along(5, ProjectEnum.PROJECT_SHORT));
        assertFalse(along(6, ProjectEnum.PROJECT_SHORT));
    }

    @Test
    @DisplayName("the range is shortened only when both PROJECT_SHORT and TMD_COVERTRACKS hold")
    void shortenedRangeNeedsBoth() {
        // PROJECT_SHORT without the timed effect
        assertTrue(along(6, ProjectEnum.PROJECT_SHORT));

        // the timed effect without PROJECT_SHORT
        player.putTimed(TimedEffect.TMD_COVERTRACKS, 10);
        assertTrue(along(6, ProjectEnum.PROJECT_NONE));
    }

    @Test
    @DisplayName("the shortened range uses integer division: 23 / 4 is 5, not 5.75")
    void shortenedRangeRounding() throws ReflectiveOperationException {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, new GameConstantsData(
                new LevelMaxData(1024), null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, new PlayerData(20, 23, 0, 0),
                null, null, null, null, null, null, null, null));

        player.putTimed(TimedEffect.TMD_COVERTRACKS, 10);
        assertTrue(along(5, ProjectEnum.PROJECT_SHORT));
        assertFalse(along(6, ProjectEnum.PROJECT_SHORT));
    }

    @Test
    @DisplayName("PROJECT_SHORT is still honoured when it is one of several flags")
    void shortenedRangeAmongOtherFlags() {
        player.putTimed(TimedEffect.TMD_COVERTRACKS, 10);
        assertTrue(along(5, ProjectEnum.PROJECT_SHORT, ProjectEnum.PROJECT_STOP));
        assertFalse(along(6, ProjectEnum.PROJECT_SHORT, ProjectEnum.PROJECT_STOP));
    }

    @Test
    @DisplayName("PROJECT_THRU carries the path past the target, so the last grid is not the target")
    void thruOvershoots() {
        assertFalse(along(3, ProjectEnum.PROJECT_THRU));
    }
}
