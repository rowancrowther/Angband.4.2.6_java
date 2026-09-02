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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.SquareEnum;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.calcLighting}, the port of C's {@code calc_lighting} ({@code cave-view.c}).
 *
 * <p>The method rebuilds the light level of every grid on the level from scratch, in two stages: a
 * full sweep that lays down a base level from glowing and bright terrain, then the moving sources —
 * the player's own light and every monster's — added on top. Every expected value below is worked
 * out from the C: the base is {@code = 1} or {@code = 0} for glow, {@code += 2} for bright terrain
 * with {@code += 1} spilled into each of the eight neighbours from {@code ddgrid_ddd[0..7]}, and the
 * sources follow {@code add_light}'s {@code inten - dist} falloff over C's integer {@code distance}
 * approximation.
 *
 * <p>Two details of the C are easy to lose in a port and are pinned down here. The bright-terrain
 * contribution <em>accumulates</em> onto the glow base, so a grid that is both glowing and bright
 * reaches three rather than two; and the neighbour set stops at the eighth entry of
 * {@code ddgrid_ddd}, deliberately skipping the {@code (0, 0)} centre that closes that table, so a
 * bright grid is not brightened a second time by its own loop.
 *
 * <p>A third is the sweep's write order. The base level is written with an assignment while
 * neighbour spills are additions, and the sweep runs row by row, so a spill onto a grid the sweep
 * has not reached yet is thrown away when that grid's own base is assigned, while a spill onto a
 * grid already passed survives. Two bright grids side by side therefore end on different levels in
 * C, and {@link TwoBrightGrids} asserts that asymmetry rather than the tidy answer.
 *
 * <p>Every grid of the level is furnished during setup with a feature carrying both {@code TF_LOS}
 * and {@code TF_PROJECT} for open ground: the wall tests read the first through
 * {@code squareAllowsLOS} while {@code add_light} traces line of sight through the second, and a
 * grid that was open for one and not the other would make the fixture lie. Squares are also
 * explicitly darkened, because a freshly built {@link Chunk} fills itself with squares holding a
 * light level of one.
 *
 * <p>{@code GameConstants} is seeded with {@code player:max-sight} at {@code constants.txt}'s real
 * value of twenty, because the monster range cut-off is compared against it. Reaching that cut-off
 * needs more room than the other tests want, so {@link MonsterLight} builds a wider level of its own
 * for the boundary pair.
 *
 * <p>The method under test is private, so the tests reach it by reflection rather than through
 * {@code updateView}, whose other half is a separate port. That is also how the player is placed and
 * given a light radius: {@link Player} exposes {@link Player#getGrid()} and
 * {@link Player#getStateLight()} but nothing to set either.
 *
 * <p>Class ChunkCalcLightingTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
@DisplayName("Chunk.calcLighting")
class ChunkCalcLightingTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 11;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 9;

    /**
     * Where the player stands in most cases. Off the diagonal, and clear of every edge so that all
     * eight neighbours are inside the level.
     */
    private static final Loc PLAYER = Loc.row(4).col(2);

    /**
     * The grid the terrain tests light up. Off the diagonal, clear of every edge, and far enough
     * from {@link #PLAYER} that the player's own light never reaches it.
     */
    private static final Loc SUBJECT = Loc.row(4).col(7);

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private Object savedConstants;

    /**
     * The level under test.
     */
    private Chunk level;

    /**
     * The player the lighting is calculated for.
     */
    private Player player;

    /**
     * A terrain feature carrying the given flags and nothing else.
     *
     * @param flags the terrain flags the feature carries
     * @return the constructed feature
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags)
            set.on(flag);
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class));
    }

    /**
     * A race carrying nothing but the given light value.
     *
     * @param light the light intensity the race emits
     * @return the race
     */
    private static MonsterRace raceWithLight(int light) {
        return new MonsterRace("test", "", "", null, 0, 0, 0, 0, 0, 0, light, 0, 0, 0, 0,
                null, null, List.of(), 0, 0, null, null,
                List.of(), List.of(), List.of(), List.of(), List.of(), 0, null);
    }

    /**
     * Puts a monster on the level at the given index.
     *
     * @param chunk the level to place it on
     * @param index the monster index; C reserves zero as a dummy, so callers start at one
     * @param grid  the grid it stands on
     * @param light the light intensity its race emits
     * @param flags the transient flags it carries
     */
    private static void placeMonster(Chunk chunk, int index, Loc grid, int light,
                                     MonsterFlag... flags) {
        Flag<MonsterFlag> mflag = new Flag<>(MonsterFlag.class);
        for (MonsterFlag flag : flags)
            mflag.on(flag);
        chunk.getMonsters()[index] = new Monster(raceWithLight(light), null, grid, 0, 0, null,
                0, 0, 0, mflag, null, null, null, null, null, null, null, 0, 0);
    }

    /**
     * Writes a private static field, returning its previous value.
     *
     * @param owner the declaring class
     * @param name  the declared field name
     * @param value the value to write
     * @return the value the field held beforehand
     */
    private static Object setStatic(Class<?> owner, String name, Object value) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            Object previous = field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(owner.getSimpleName() + "." + name
                    + " is no longer settable by reflection", e);
        }
    }

    /**
     * Seeds {@code GameConstants.data} with {@code player:max-sight} and the carry-cap figures.
     *
     * <p>{@code calcLighting} compares each monster's range against {@code max-sight}, and
     * {@link SeededPlayerRegistry} seeds carry-cap only, so the table it leaves would throw on the
     * first monster. The sight value is {@code constants.txt}'s own twenty, and carry-cap is carried
     * across so that {@link Player} can still be built.
     *
     * <p>This runs per test rather than once for the class because the extension's own seeding is
     * undone at the end of every nested container, taking the table with it; a class-level seed
     * would survive only as far as the first nested class.
     */
    @BeforeEach
    void seedConstants() {
        GameConstantsData seed = new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, new PlayerData(20, 20, 0, 0),
                null, null, null, null, null, null, null, null);
        savedConstants = setStatic(GameConstants.class, "data", seed);
    }

    /**
     * Puts back whatever {@code GameConstants.data} held beforehand, so a test running later in the
     * same JVM sees the state it expects.
     */
    @AfterEach
    void restoreConstants() {
        setStatic(GameConstants.class, "data", savedConstants);
    }

    /**
     * A level whose every grid is open ground and dark, with the player standing at {@link #PLAYER}
     * carrying no light at all. Tests add glow, bright terrain, walls, a light radius and monsters
     * as each case requires.
     */
    @BeforeEach
    void newLevel() {
        player = new Player();
        level = newChunk(HEIGHT, WIDTH);
        placePlayer(PLAYER);
        setPlayerLight(0);
    }

    /**
     * A level of the given size, every grid open ground and dark.
     *
     * @param height the level's height in grids
     * @param width  the level's width in grids
     * @return the constructed level
     */
    private Chunk newChunk(int height, int width) {
        Chunk chunk = new Chunk("test level", 0, 0, 0, 0, 0, false,
                height, width, 0, 4, 4, 0, 0, 0, player);
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                Loc grid = Loc.row(y).col(x);
                chunk.getSquare(grid).setFeature(
                        feature(TerrainFeatureFlags.TF_LOS, TerrainFeatureFlags.TF_PROJECT));
                chunk.getSquare(grid).setLight(0);
            }
        return chunk;
    }

    /**
     * Makes a grid a wall: opaque, and blocking a projection traced through it.
     *
     * @param grid the grid to wall off
     */
    private void wall(Loc grid) {
        level.getSquare(grid).setFeature(feature());
    }

    /**
     * Makes a grid internally lit terrain, still open and passable.
     *
     * @param grid the grid to brighten
     */
    private void bright(Loc grid) {
        level.getSquare(grid).setFeature(feature(TerrainFeatureFlags.TF_LOS,
                TerrainFeatureFlags.TF_PROJECT, TerrainFeatureFlags.TF_BRIGHT));
    }

    /**
     * Makes a grid internally lit terrain that is also a wall: opaque and bright.
     *
     * @param grid the grid to brighten
     */
    private void brightWall(Loc grid) {
        level.getSquare(grid).setFeature(feature(TerrainFeatureFlags.TF_BRIGHT));
    }

    /**
     * Marks a grid as permanently glowing.
     *
     * @param grid the grid to light
     */
    private void glow(Loc grid) {
        level.getSquare(grid).sqInfoOn(SquareEnum.SQUARE_GLOW);
    }

    /**
     * Stands the player on a grid. {@link Player} has no setter for its location, so the field is
     * written directly.
     *
     * @param grid the grid to place the player on
     */
    private void placePlayer(Loc grid) {
        try {
            Field field = Player.class.getDeclaredField("grid");
            field.setAccessible(true);
            field.set(player, grid);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Player.grid is no longer reachable", e);
        }
    }

    /**
     * Gives the player a light intensity. {@code calcLighting} reads it through
     * {@link Player#getStateLight()}, which goes to the calculated state, and a freshly built player
     * has none, so a state is installed the first time this is called.
     *
     * @param intensity the light intensity to carry, negative for an unlight
     */
    private void setPlayerLight(int intensity) {
        try {
            Field field = Player.class.getDeclaredField("state");
            field.setAccessible(true);
            PlayerState state = (PlayerState) field.get(player);
            if (state == null) {
                state = new PlayerState();
                field.set(player, state);
            }
            state.setCurLight(intensity);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Player.state is no longer reachable", e);
        }
    }

    /**
     * Runs the method under test on the standard level. Private, and reached by reflection rather
     * than through {@code updateView}, whose other half is a separate port.
     */
    private void calcLighting() {
        calcLighting(level);
    }

    /**
     * Runs the method under test on a given level.
     *
     * @param chunk the level to light
     */
    private void calcLighting(Chunk chunk) {
        try {
            Method method = Chunk.class.getDeclaredMethod("calcLighting", Player.class);
            method.setAccessible(true);
            method.invoke(chunk, player);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("calcLighting threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("calcLighting is no longer reachable", e);
        }
    }

    /**
     * The light level a grid ended up holding.
     *
     * @param grid the grid to read
     * @return its light level
     */
    private int light(Loc grid) {
        return level.getSquare(grid).getLight();
    }

    /**
     * The base level laid down by permanently glowing terrain, before any source is added.
     */
    @Nested
    @DisplayName("the glow base")
    class GlowBase {

        /**
         * With nothing glowing, nothing bright and no light on the player, C leaves every grid at
         * zero — the base assignment is the {@code else} branch everywhere, and both sources
         * contribute nothing.
         */
        @Test
        @DisplayName("a dark level with an unlit player stays wholly dark")
        void darkLevelStaysDark() {
            calcLighting();

            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    assertEquals(0, light(Loc.row(y).col(x)),
                            "grid " + y + "," + x + " should be dark");
        }

        /**
         * A glowing grid that lets light through takes the base of one; its neighbours, not glowing,
         * stay dark. Glow does not spread.
         */
        @Test
        @DisplayName("a glowing open grid reads one and does not spread")
        void glowingOpenGrid() {
            glow(SUBJECT);

            calcLighting();

            assertEquals(1, light(SUBJECT));
            assertEquals(0, light(Loc.row(4).col(8)));
            assertEquals(0, light(Loc.row(3).col(7)));
        }

        /**
         * The starting level is overwritten, not added to: a grid left bright by a previous pass
         * that is no longer glowing goes back to zero.
         */
        @Test
        @DisplayName("the base overwrites whatever the grid held before")
        void baseOverwritesPreviousLevel() {
            level.getSquare(SUBJECT).setLight(7);

            calcLighting();

            assertEquals(0, light(SUBJECT));
        }

        /**
         * A glowing wall is only lit when {@code glow_can_light_wall} agrees. With the player far
         * off and every grid around the wall dark, it does not, and the wall stays at zero even
         * though it carries the glow flag.
         */
        @Test
        @DisplayName("a glowing wall with nothing to light it stays dark")
        void glowingWallWithNoIlluminationStaysDark() {
            wall(SUBJECT);
            glow(SUBJECT);

            calcLighting();

            assertEquals(0, light(SUBJECT));
        }

        /**
         * When the player stands on the glowing wall itself, C's {@code glow_can_light_wall} returns
         * true at once — every face is visible from inside the grid — so the wall takes the base of
         * one.
         */
        @Test
        @DisplayName("a glowing wall the player stands on reads one")
        void glowingWallUnderThePlayerIsLit() {
            wall(SUBJECT);
            glow(SUBJECT);
            placePlayer(SUBJECT);

            calcLighting();

            assertEquals(1, light(SUBJECT));
        }

        /**
         * A glowing wall with a glowing open grid between it and the player is lit by that grid, so
         * it takes the base of one.
         */
        @Test
        @DisplayName("a glowing wall lit by the glowing grid towards the player reads one")
        void glowingWallLitFromTowardsThePlayer() {
            wall(SUBJECT);
            glow(SUBJECT);
            glow(Loc.row(4).col(6));

            calcLighting();

            assertEquals(1, light(SUBJECT));
        }
    }

    /**
     * The contribution of internally lit terrain, which accumulates onto the glow base and spills
     * into the eight neighbours.
     */
    @Nested
    @DisplayName("bright terrain")
    class BrightTerrain {

        /**
         * Bright terrain that is not glowing scores {@code 0 + 2}. Not three: the neighbour loop
         * runs over the eight surrounding grids only, and must not come back round to the centre.
         */
        @Test
        @DisplayName("a bright grid that is not glowing reads two")
        void brightNotGlowing() {
            bright(SUBJECT);

            calcLighting();

            assertEquals(2, light(SUBJECT));
        }

        /**
         * Bright terrain that is also glowing scores {@code 1 + 2}, because C adds the bright
         * contribution to the glow base rather than replacing it.
         */
        @Test
        @DisplayName("a bright grid that is glowing reads three")
        void brightAndGlowing() {
            bright(SUBJECT);
            glow(SUBJECT);

            calcLighting();

            assertEquals(3, light(SUBJECT));
        }

        /**
         * Each of the eight surrounding grids takes a spill of one — the diagonals included, since
         * {@code ddgrid_ddd} holds four cardinals and four diagonals. Only the grids the sweep has
         * already passed can keep it, so the row above the bright grid and the grid to its west are
         * asserted here, and the rest in {@link #spillOntoLaterGridsIsOverwritten()}.
         */
        @Test
        @DisplayName("the eight neighbours each take a spill of one")
        void neighboursTakeASpill() {
            bright(SUBJECT);

            calcLighting();

            assertEquals(1, light(Loc.row(3).col(6)));
            assertEquals(1, light(Loc.row(3).col(7)));
            assertEquals(1, light(Loc.row(3).col(8)));
            assertEquals(1, light(Loc.row(4).col(6)));
        }

        /**
         * The sweep writes each grid's base with an assignment as it reaches it, so a spill onto a
         * grid it has not yet reached is thrown away. The grid east of the bright one and the three
         * below it all end dark, and this is C's behaviour, not a rounding of the port.
         */
        @Test
        @DisplayName("a spill onto a grid the sweep has not reached is overwritten")
        void spillOntoLaterGridsIsOverwritten() {
            bright(SUBJECT);

            calcLighting();

            assertEquals(0, light(Loc.row(4).col(8)));
            assertEquals(0, light(Loc.row(5).col(6)));
            assertEquals(0, light(Loc.row(5).col(7)));
            assertEquals(0, light(Loc.row(5).col(8)));
        }

        /**
         * The spill reaches no further than one grid.
         */
        @Test
        @DisplayName("the spill does not reach a second ring")
        void spillDoesNotReachASecondRing() {
            bright(SUBJECT);

            calcLighting();

            assertEquals(0, light(Loc.row(2).col(7)));
            assertEquals(0, light(Loc.row(4).col(9)));
        }

        /**
         * A spill lands on a glowing neighbour on top of its base of one.
         */
        @Test
        @DisplayName("a spill accumulates onto a glowing neighbour")
        void spillAccumulatesOntoGlow() {
            bright(SUBJECT);
            glow(Loc.row(3).col(7));

            calcLighting();

            assertEquals(2, light(Loc.row(3).col(7)));
        }

        /**
         * A wall neighbour is only brightened when {@code source_can_light_wall} agrees. With the
         * player far to the west and the bright grid immediately east of the wall, the two lie on
         * opposite sides of it: C follows the shared component of the two view directions to the
         * wall grid itself, finds it opaque, and leaves it alone. The wall is west of the bright
         * grid, so the sweep has already written it and a spill would have survived.
         */
        @Test
        @DisplayName("a wall neighbour whose lit face the player cannot see is not brightened")
        void wallNeighbourFacingAwayIsNotBrightened() {
            bright(SUBJECT);
            wall(Loc.row(4).col(6));

            calcLighting();

            assertEquals(0, light(Loc.row(4).col(6)));
        }

        /**
         * With the player beyond the bright grid rather than opposite it, the direction from the
         * wall to the player and the direction from the wall to the light are the same step, so C
         * returns true on its first shared-component test and the wall takes the spill.
         */
        @Test
        @DisplayName("a wall neighbour on the player's side of the light is brightened")
        void wallNeighbourFacingThePlayerIsBrightened() {
            bright(SUBJECT);
            wall(Loc.row(3).col(7));
            placePlayer(Loc.row(5).col(7));

            calcLighting();

            assertEquals(1, light(Loc.row(3).col(7)));
        }

        /**
         * A bright grid that is itself a wall still takes the full two: the bright contribution is
         * applied to the grid unconditionally, without the visibility test the neighbours face.
         */
        @Test
        @DisplayName("bright terrain that is a wall still reads two")
        void brightWallStillScoresTwo() {
            brightWall(SUBJECT);

            calcLighting();

            assertEquals(2, light(SUBJECT));
        }

        /**
         * A bright grid on the border spills only into the neighbours that exist; the out-of-bounds
         * offsets are skipped rather than throwing.
         */
        @Test
        @DisplayName("a bright grid on the border skips its out-of-bounds neighbours")
        void brightGridOnTheBorder() {
            Loc corner = Loc.row(0).col(0);
            bright(corner);

            calcLighting();

            assertEquals(2, light(corner));
            assertEquals(0, light(Loc.row(0).col(1)));
            assertEquals(0, light(Loc.row(1).col(0)));
            assertEquals(0, light(Loc.row(1).col(1)));
        }
    }

    /**
     * Two bright grids side by side, which pin down the order the sweep writes in.
     */
    @Nested
    @DisplayName("two bright grids in a row")
    class TwoBrightGrids {

        /**
         * With bright grids at columns seven and eight of row four, C's sweep reaches column seven
         * first: it scores two and spills one into column eight, which is then overwritten when the
         * sweep reaches it and scores two of its own — and spills one back into column seven, which
         * has already been written and so keeps it. The pair ends on three and two, not on a matched
         * pair, and the port has to reproduce the asymmetry.
         */
        @Test
        @DisplayName("the earlier grid keeps the later grid's spill, but not the other way round")
        void spillSurvivesOnlyBackwards() {
            bright(Loc.row(4).col(7));
            bright(Loc.row(4).col(8));

            calcLighting();

            assertEquals(3, light(Loc.row(4).col(7)));
            assertEquals(2, light(Loc.row(4).col(8)));
        }

        /**
         * A grid in the row above is adjacent to both bright grids and was written before either, so
         * it keeps both spills and ends on two.
         */
        @Test
        @DisplayName("a grid above both takes both spills")
        void gridAboveTakesBothSpills() {
            bright(Loc.row(4).col(7));
            bright(Loc.row(4).col(8));

            calcLighting();

            assertEquals(2, light(Loc.row(3).col(8)));
        }

        /**
         * A grid in the row below is adjacent to both, but the sweep reaches it afterwards and
         * assigns its base over both spills.
         */
        @Test
        @DisplayName("a grid below both keeps neither spill")
        void gridBelowKeepsNeitherSpill() {
            bright(Loc.row(4).col(7));
            bright(Loc.row(4).col(8));

            calcLighting();

            assertEquals(0, light(Loc.row(5).col(8)));
        }
    }

    /**
     * The player's own light, the first source added on top of the terrain base.
     */
    @Nested
    @DisplayName("the player's light")
    class PlayerLight {

        /**
         * An intensity of two gives a radius of one, and {@code add_light} contributes
         * {@code 2 - dist}: two on the player's own grid and one on each of the eight around it, the
         * diagonals included because C's approximation puts them at distance one.
         */
        @Test
        @DisplayName("a light of intensity two reaches one grid")
        void lightOfIntensityTwo() {
            setPlayerLight(2);

            calcLighting();

            assertEquals(2, light(PLAYER));
            assertEquals(1, light(Loc.row(4).col(1)));
            assertEquals(1, light(Loc.row(4).col(3)));
            assertEquals(1, light(Loc.row(3).col(1)));
            assertEquals(0, light(Loc.row(4).col(4)));
        }

        /**
         * An intensity of one gives a radius of zero, so only the player's own grid is visited.
         */
        @Test
        @DisplayName("a light of intensity one reaches only the player's grid")
        void lightOfIntensityOne() {
            setPlayerLight(1);

            calcLighting();

            assertEquals(1, light(PLAYER));
            assertEquals(0, light(Loc.row(4).col(3)));
        }

        /**
         * No light at all gives a radius of minus one, which makes {@code add_light}'s loops empty.
         */
        @Test
        @DisplayName("no light leaves even the player's own grid dark")
        void noLightAddsNothing() {
            setPlayerLight(0);

            calcLighting();

            assertEquals(0, light(PLAYER));
        }

        /**
         * The player's light is added on top of the terrain base rather than replacing it: a glowing
         * grid within reach carries both.
         */
        @Test
        @DisplayName("the light accumulates onto the glow base")
        void lightAccumulatesOntoGlow() {
            glow(PLAYER);
            glow(Loc.row(4).col(3));
            setPlayerLight(2);

            calcLighting();

            assertEquals(3, light(PLAYER));
            assertEquals(2, light(Loc.row(4).col(3)));
        }

        /**
         * A negative intensity is an unlight: {@code add_light} contributes {@code inten + dist}, so
         * a glowing area around the player is pushed down rather than up, hardest at the centre.
         */
        @Test
        @DisplayName("a negative light darkens, hardest at the player")
        void negativeLightDarkens() {
            for (int y = 3; y <= 5; y++)
                for (int x = 1; x <= 3; x++)
                    glow(Loc.row(y).col(x));
            setPlayerLight(-2);

            calcLighting();

            assertEquals(-1, light(PLAYER));
            assertEquals(0, light(Loc.row(4).col(3)));
        }

        /**
         * Light does not pass through a wall: with a wall due east of the player, the open grid
         * beyond it is left at the base even though it is within the radius.
         */
        @Test
        @DisplayName("light does not leak past a wall")
        void lightDoesNotLeakPastAWall() {
            wall(Loc.row(4).col(3));
            setPlayerLight(3);

            calcLighting();

            assertEquals(0, light(Loc.row(4).col(4)));
        }
    }

    /**
     * Monster light and unlight, added after the player's own.
     */
    @Nested
    @DisplayName("monster light")
    class MonsterLight {

        /**
         * A monster whose race emits light contributes exactly as the player's own light does,
         * centred on the monster.
         */
        @Test
        @DisplayName("a lit monster lights the grids around it")
        void litMonsterLightsItsSurroundings() {
            placeMonster(level, 1, SUBJECT, 2);

            calcLighting();

            assertEquals(2, light(SUBJECT));
            assertEquals(1, light(Loc.row(4).col(6)));
            assertEquals(1, light(Loc.row(3).col(8)));
            assertEquals(0, light(Loc.row(4).col(5)));
        }

        /**
         * A monster race emitting nothing is skipped, and its grid is left at the terrain base.
         */
        @Test
        @DisplayName("a monster with no light contributes nothing")
        void monsterWithNoLightContributesNothing() {
            placeMonster(level, 1, SUBJECT, 0);

            calcLighting();

            assertEquals(0, light(SUBJECT));
        }

        /**
         * A camouflaged monster is not showing its light, and C skips it before reading its race's
         * light at all.
         */
        @Test
        @DisplayName("a camouflaged monster contributes nothing")
        void camouflagedMonsterContributesNothing() {
            placeMonster(level, 1, SUBJECT, 2, MonsterFlag.MFLAG_CAMOUFLAGE);

            calcLighting();

            assertEquals(0, light(SUBJECT));
        }

        /**
         * An empty monster slot — C's reserved index zero, and any slot a dead monster has left
         * behind — is passed over rather than dereferenced.
         */
        @Test
        @DisplayName("empty monster slots are passed over")
        void emptySlotsArePassedOver() {
            placeMonster(level, 2, SUBJECT, 2);

            calcLighting();

            assertEquals(2, light(SUBJECT));
        }

        /**
         * A monster emitting darkness pushes a glowing area down, hardest on its own grid.
         */
        @Test
        @DisplayName("a monster emitting darkness lowers the light around it")
        void darkeningMonsterLowersTheLight() {
            for (int y = 3; y <= 5; y++)
                for (int x = 6; x <= 8; x++)
                    glow(Loc.row(y).col(x));
            placeMonster(level, 1, SUBJECT, -2);

            calcLighting();

            assertEquals(-1, light(SUBJECT));
            assertEquals(0, light(Loc.row(4).col(6)));
        }

        /**
         * Several monsters each add their own contribution, and the player's light is added as well,
         * so an overlap carries the sum.
         */
        @Test
        @DisplayName("contributions from several sources add up")
        void severalSourcesAddUp() {
            setPlayerLight(2);
            placeMonster(level, 1, Loc.row(4).col(4), 2);
            placeMonster(level, 2, Loc.row(4).col(6), 2);

            calcLighting();

            assertEquals(2, light(Loc.row(4).col(4)));
            assertEquals(2, light(Loc.row(4).col(3)));
            assertEquals(2, light(Loc.row(4).col(5)));
        }

        /**
         * The range cut-off compares {@code distance - radius} against {@code max-sight}, and skips
         * the monster only when it is strictly greater. With a sight of twenty and a monster of
         * intensity three — radius two — the grid at distance twenty-two is the last one still
         * lighting, since {@code 22 - 2} is not greater than twenty.
         *
         * <p>This needs more room than the standard level, so a wider one is built for it.
         */
        @Test
        @DisplayName("a monster just inside the sight cut-off still lights")
        void monsterJustInsideTheCutOff() {
            Chunk wide = newChunk(HEIGHT, 30);
            placePlayer(Loc.row(4).col(1));
            Loc monsterGrid = Loc.row(4).col(23);
            placeMonster(wide, 1, monsterGrid, 3);

            calcLighting(wide);

            assertEquals(3, wide.getSquare(monsterGrid).getLight());
        }

        /**
         * One grid further out, {@code 23 - 2} is greater than twenty, and C skips the monster
         * entirely — its own grid included, so nothing is lit at all.
         */
        @Test
        @DisplayName("a monster just outside the sight cut-off is skipped")
        void monsterJustOutsideTheCutOff() {
            Chunk wide = newChunk(HEIGHT, 30);
            placePlayer(Loc.row(4).col(1));
            Loc monsterGrid = Loc.row(4).col(24);
            placeMonster(wide, 1, monsterGrid, 3);

            calcLighting(wide);

            assertEquals(0, wide.getSquare(monsterGrid).getLight());
        }
    }

    /**
     * The redraw flag raised when the player's own grid changes light level.
     */
    @Nested
    @DisplayName("the light indicator")
    class LightIndicator {

        /**
         * The player's grid started dark and ends dark, so nothing is flagged.
         */
        @Test
        @DisplayName("an unchanged level leaves the flag down")
        void unchangedLevelLeavesFlagDown() {
            calcLighting();

            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_LIGHT));
        }

        /**
         * Lighting the player's own grid changes its level, so the indicator is flagged for redraw.
         */
        @Test
        @DisplayName("a changed level raises the flag")
        void changedLevelRaisesFlag() {
            setPlayerLight(2);

            calcLighting();

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_LIGHT));
        }

        /**
         * The comparison is against the level held on entry, not against zero: a grid that was
         * already lit to the same level, and is lit to it again, leaves the flag down.
         */
        @Test
        @DisplayName("a level that ends where it started leaves the flag down")
        void unchangedNonZeroLevelLeavesFlagDown() {
            glow(PLAYER);
            level.getSquare(PLAYER).setLight(1);

            calcLighting();

            assertEquals(1, light(PLAYER));
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_LIGHT));
        }

        /**
         * A grid that was lit and is now dark is a change like any other.
         */
        @Test
        @DisplayName("a level that drops raises the flag")
        void droppingLevelRaisesFlag() {
            level.getSquare(PLAYER).setLight(4);

            calcLighting();

            assertEquals(0, light(PLAYER));
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_LIGHT));
        }
    }
}
