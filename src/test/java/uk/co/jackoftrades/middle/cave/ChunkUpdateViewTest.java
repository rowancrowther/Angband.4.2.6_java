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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.SquareEnum;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.data.PlayerData;
import uk.co.jackoftrades.middle.game.globals.data.WorldData;
import uk.co.jackoftrades.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.player.PlayerState;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code Chunk.updateView}, the port of C's {@code update_view} ({@code cave-view.c:871}).
 *
 * <p>The expected values are taken from the C body, which does six things in order: snapshot and
 * wipe the visibility flags ({@code mark_wasseen}); recompute the light ({@code calc_lighting});
 * put {@code SQUARE_VIEW} on the player's own grid unconditionally, and {@code SQUARE_SEEN} with
 * {@code SQUARE_CLOSE_PLAYER} only if {@code cur_light > 0}, the grid is lit, or the player has
 * {@code PF_UNLIGHT}; forget the remembered terrain under a blind player whose memory says
 * impassable; sweep the whole level with {@code update_view_one}; and sweep it again with
 * {@code update_one}.
 *
 * <p><b>The blind clause is the part worth testing hardest.</b> C asks its two questions of two
 * different chunks — {@code square_isknown(c, p->grid)} of the live level, but
 * {@code !square_ispassable(p->cave, p->grid)} of the player's remembered copy. The two chunks are
 * the same Java type, so a port that asked both of the live level would compile and would look
 * right; it would simply never fire, because a player is nearly always standing on terrain that is
 * genuinely passable. {@link BlindMemory#keepsMemoryThatSaysPassable} is the test that separates
 * the two readings: the memory says floor while the live level says wall, which is the one
 * arrangement where consulting the wrong chunk forgets terrain C keeps.
 *
 * <p>The sweeps are checked through their effects rather than by counting calls: a grid the player
 * cannot reach ends with none of the three flags however they started, a grid that has just gone
 * out of sight raises a map redraw, and {@code SQUARE_WASSEEN} is off everywhere afterwards —
 * which can only be true if the second sweep visited every grid the first one did.
 *
 * <p>Light is set through {@code SQUARE_GLOW} rather than by writing a square's light level,
 * because {@code calc_lighting} rebuilds every light level from scratch before anything else reads
 * one, so a level written into the fixture would be gone by the time it mattered.
 *
 * <p>The level is not square and no grid used is its own transpose, so a chunk indexing
 * {@code squares[y][x]} would fail here rather than quietly agree with itself.
 *
 * <p>Class ChunkUpdateViewTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkUpdateViewTest {

    /**
     * The level's width, in grids. Different from {@link #HEIGHT} so that a transposed index shows.
     */
    private static final int WIDTH = 9;

    /**
     * The level's height, in grids. Different from {@link #WIDTH} so that a transposed index shows.
     */
    private static final int HEIGHT = 6;

    /**
     * Where the player stands. Off the diagonal, and clear of the walled row.
     */
    private static final Loc PLAYER = Loc.row(1).col(3);

    /**
     * The row walled from edge to edge, cutting the level in two.
     */
    private static final int WALL_ROW = 3;

    /**
     * A grid on the far side of the wall, which no line from the player can reach.
     */
    private static final Loc HIDDEN = Loc.row(5).col(7);

    /**
     * A grid on the player's own side of the wall, in the open.
     */
    private static final Loc NEAR = Loc.row(2).col(5);

    /**
     * The "unknown grid" feature, C's {@code FEAT_NONE} — what forgetting writes.
     */
    private static Feature none;

    /**
     * Open ground: transparent, projectable and passable.
     */
    private static Feature floor;

    /**
     * An impassable, opaque wall.
     */
    private static Feature granite;

    /**
     * What the terrain registry held before this class replaced it.
     */
    private List<Feature> savedFeatures;

    /**
     * What {@code GameConstants.data} held before this class replaced it.
     */
    private Object savedConstants;

    /**
     * The events bus the engine held before.
     */
    private EventsHandler savedBus;

    /**
     * The player the game held before.
     */
    private Player savedPlayer;

    /**
     * The live level the game held before.
     */
    private Chunk savedCave;

    /**
     * The live level — the chunk the method is called on.
     */
    private Chunk level;

    /**
     * The player's remembered copy of the level, which is what forgetting writes to.
     */
    private Chunk memory;

    /**
     * The player whose view is recalculated.
     */
    private Player player;

    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;

    /**
     * Reads the terrain registry without insisting it has been loaded.
     *
     * @return a copy of the registry's features, or {@code null} if nothing has been loaded
     */
    private static List<Feature> currentFeatures() {
        try {
            return new ArrayList<>(TerrainRegistry.getFeatures());
        } catch (NullPointerException notLoaded) {
            return null;
        }
    }

    /**
     * Builds a feature carrying only the code, name and flags a test needs; everything the data
     * files supply beyond that is left null or zero, because nothing on this path reads it.
     *
     * @param code  the terrain code
     * @param name  the feature's name, so a failure message names it
     * @param setOn the terrain flags to switch on
     * @return the feature
     */
    private static Feature feature(TerrainFlags code, String name, TerrainFeatureFlags... setOn) {
        Flag<TerrainFeatureFlags> flags = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : setOn) flags.on(flag);

        return new Feature(code, name, "", null, 0, 0, flags, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class));
    }

    /**
     * Writes a static field, returning what it held before.
     *
     * @param owner the class declaring the field
     * @param name  the field's name
     * @param value the value to write
     * @return the previous value
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
     * Seeds the global state the method reads through: the terrain registry, so {@code FEAT_NONE}
     * can be resolved when a grid is forgotten; {@code player:max-sight}, which the first sweep
     * compares distances against; {@code world:feeling-need}, which the second sweep reads; and the
     * carry-cap block, without which a {@link Player} cannot be built at all.
     *
     * <p>Seeded per test rather than per class because the extension's own seeding is undone at the
     * end of every nested container, taking the table with it.
     */
    @BeforeEach
    void seedGlobals() {
        savedFeatures = currentFeatures();
        none = feature(TerrainFlags.FEAT_NONE, "unknown grid");
        granite = feature(TerrainFlags.FEAT_GRANITE, "granite wall");
        floor = feature(TerrainFlags.FEAT_FLOOR, "open floor", TerrainFeatureFlags.TF_LOS,
                TerrainFeatureFlags.TF_PROJECT, TerrainFeatureFlags.TF_PASSABLE);
        TerrainRegistry.setFeatures(List.of(none, granite, floor));

        GameConstantsData seed = new GameConstantsData(
                null, null, null, null,
                new WorldData(128, 0, 0, 0, 0, 0, 0, 10, 0, 0),
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, new PlayerData(20, 20, 0, 0),
                null, null, null, null, null, null, null, null);
        savedConstants = setStatic(GameConstants.class, "data", seed);
    }

    /**
     * A live level and a matching remembered copy, both floored throughout and cut in two by a wall
     * across {@link #WALL_ROW}, with the player standing at {@link #PLAYER} carrying no light.
     */
    @BeforeEach
    void newLevel() {
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
        savedBus = GameEngine.getEventsBusHandler();

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);
        memory = new Chunk("memory", 0, 0, 0, 0, 0, false,
                HEIGHT, WIDTH, 0, 4, 3, 0, 0, 0, player);

        level.setCurrentLevel(level);
        memory.setCurrentLevel(level);
        player.setCave(memory);
        GameState.setPlayer(player);
        GameState.setCave(level);

        lay(level, floor);
        lay(memory, floor);
        for (int x = 0; x < WIDTH; x++)
            level.getSquare(Loc.row(WALL_ROW).col(x)).setFeature(granite);

        set("state", new PlayerState());
        set("level", 1);
        set("grid", PLAYER);
        player.getPlayerState().setCurLight(0);

        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    /**
     * Puts back every piece of global state this class replaced, so a class running later in the
     * same JVM finds what it expects.
     */
    @AfterEach
    void restoreGlobals() {
        GameEngine.setEventsBusHandler(savedBus);
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
        setStatic(GameConstants.class, "data", savedConstants);
        TerrainRegistry.setFeatures(savedFeatures);
    }

    /**
     * Puts one feature on every grid of a chunk.
     *
     * @param chunk   the chunk to lay
     * @param feature the feature to lay it with
     */
    private void lay(Chunk chunk, Feature feature) {
        for (int y = 0; y < HEIGHT; y++)
            for (int x = 0; x < WIDTH; x++)
                chunk.getSquare(Loc.row(y).col(x)).setFeature(feature);
    }

    /**
     * Writes a private {@link Player} field. Location, level and calculated state have getters but
     * no setters, and a freshly built player has no state at all — birth fills that in, and birth
     * is not ported yet.
     *
     * @param name  the field's name
     * @param value the value to write
     */
    private void set(String name, Object value) {
        try {
            Field field = Player.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(player, value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Player." + name + " is no longer reachable", e);
        }
    }

    /**
     * Blinds the player, or restores their sight.
     *
     * @param turns the turns of {@code TMD_BLIND} left, zero for a sighted player
     */
    @SuppressWarnings("unchecked")
    private void blind(int turns) {
        try {
            Field field = Player.class.getDeclaredField("timed");
            field.setAccessible(true);
            ((Map<TimedEffect, Integer>) field.get(player)).put(TimedEffect.TMD_BLIND, turns);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("cannot reach Player.timed", e);
        }
    }

    /**
     * Whether a grid of the live level holds an info flag.
     *
     * @param grid the grid to read
     * @param flag the flag to look for
     * @return true if the grid holds the flag
     */
    private boolean has(Loc grid, SquareEnum flag) {
        return level.getSquare(grid).hasInfoFlag(flag);
    }

    /**
     * @return whether a map redraw was signalled for any grid during this test
     */
    private boolean redrewMap() {
        return bus.types.contains(GameEventType.EVENT_MAP);
    }

    /**
     * An events bus that records the event types dispatched through it and does nothing else.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * Every event type seen, in order.
         */
        private final List<GameEventType> types = new ArrayList<>();

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
            types.add(eventType);
        }
    }

    /**
     * What happens to the grid the player is standing on, which C settles by hand before either
     * sweep runs.
     */
    @Nested
    @DisplayName("the player's own grid")
    class PlayerGrid {

        /**
         * C sets {@code SQUARE_VIEW} on the player's grid with no condition attached — there is
         * always a line of sight to where you are standing — while {@code SQUARE_SEEN} and
         * {@code SQUARE_CLOSE_PLAYER} wait on there being something to see by. With no light
         * carried, unlit terrain and no {@code PF_UNLIGHT}, all three conditions fail.
         */
        @Test
        @DisplayName("is in view but unseen in the dark")
        void viewWithoutSeenInTheDark() {
            level.updateView(player);

            assertTrue(has(PLAYER, SquareEnum.SQUARE_VIEW), "the player's grid is always in view");
            assertFalse(has(PLAYER, SquareEnum.SQUARE_SEEN),
                    "there is no light to see the grid by");
            assertFalse(has(PLAYER, SquareEnum.SQUARE_CLOSE_PLAYER),
                    "close-player follows seen");
        }

        /**
         * The first of C's three conditions: {@code p->state.cur_light > 0}.
         */
        @Test
        @DisplayName("a carried light makes it seen and close")
        void carriedLightMakesItSeen() {
            player.getPlayerState().setCurLight(3);

            level.updateView(player);

            assertTrue(has(PLAYER, SquareEnum.SQUARE_SEEN), "a carried light shows the grid");
            assertTrue(has(PLAYER, SquareEnum.SQUARE_CLOSE_PLAYER),
                    "the grid the player stands on is close to them");
        }

        /**
         * The second condition: {@code square_islit(c, p->grid)}. The grid is made lit through
         * {@code SQUARE_GLOW}, since {@code calc_lighting} would overwrite a light level written
         * directly into the fixture.
         */
        @Test
        @DisplayName("lit terrain makes it seen with no light carried")
        void litTerrainMakesItSeen() {
            level.getSquare(PLAYER).sqInfoOn(SquareEnum.SQUARE_GLOW);

            level.updateView(player);

            assertTrue(has(PLAYER, SquareEnum.SQUARE_SEEN), "the grid lights itself");
            assertTrue(has(PLAYER, SquareEnum.SQUARE_CLOSE_PLAYER), "and so counts as close");
        }

        /**
         * The third condition: {@code player_has(p, PF_UNLIGHT)}, the personality that sees in the
         * dark, with neither a carried light nor lit terrain to help it.
         */
        @Test
        @DisplayName("PF_UNLIGHT makes it seen in the dark")
        void unlightMakesItSeen() {
            player.getPlayerState().playerFlagOn(PlayerFlag.PF_UNLIGHT);

            level.updateView(player);

            assertTrue(has(PLAYER, SquareEnum.SQUARE_SEEN), "an unlight player sees in the dark");
            assertTrue(has(PLAYER, SquareEnum.SQUARE_CLOSE_PLAYER), "and is close to their grid");
        }

        /**
         * The second sweep takes {@code SQUARE_SEEN} and {@code SQUARE_CLOSE_PLAYER} off every grid
         * for a blind player, including the one the first half of the method just put them on, so a
         * blind player with a lantern still sees nothing. {@code SQUARE_VIEW} is untouched by that
         * branch and survives.
         */
        @Test
        @DisplayName("blindness takes seen off again but leaves view")
        void blindnessClearsSeen() {
            player.getPlayerState().setCurLight(3);
            blind(4);

            level.updateView(player);

            assertFalse(has(PLAYER, SquareEnum.SQUARE_SEEN), "a blind player sees nothing");
            assertFalse(has(PLAYER, SquareEnum.SQUARE_CLOSE_PLAYER), "close-player goes with it");
            assertTrue(has(PLAYER, SquareEnum.SQUARE_VIEW),
                    "the blind branch does not touch view");
        }
    }

    /**
     * C's blind clause, which forgets remembered terrain that the player has just disproved by
     * standing on it.
     */
    @Nested
    @DisplayName("the blind memory clause")
    class BlindMemory {

        /**
         * All three of C's conditions met: blind, the grid known, and the remembered terrain
         * impassable. The remembered feature becomes {@code FEAT_NONE} — asserted on identity with
         * the registry's own entry, which separates "forgotten" from "overwritten with whatever
         * came to hand" — and the live level is left alone.
         */
        @Test
        @DisplayName("forgets terrain the memory says is impassable")
        void forgetsMemoryThatSaysImpassable() {
            memory.getSquare(PLAYER).setFeature(granite);
            blind(4);

            level.updateView(player);

            assertSame(none, memory.getSquare(PLAYER).getFeature(),
                    "the disproved memory is forgotten");
            assertSame(floor, level.getSquare(PLAYER).getFeature(),
                    "the real level keeps its terrain");
        }

        /**
         * The test that tells the two chunks apart. The memory says floor while the live level says
         * wall, so C — which asks {@code square_ispassable} of {@code p->cave} — finds a passable
         * memory and forgets nothing, while the same clause asked of the live level would find a
         * wall and wipe a memory that is perfectly good.
         */
        @Test
        @DisplayName("keeps terrain the memory says is passable, whatever the live level says")
        void keepsMemoryThatSaysPassable() {
            level.getSquare(PLAYER).setFeature(granite);
            memory.getSquare(PLAYER).setFeature(floor);
            blind(4);

            level.updateView(player);

            assertSame(floor, memory.getSquare(PLAYER).getFeature(),
                    "the memory is passable, so C forgets nothing");
        }

        /**
         * Blindness is the first condition, so a sighted player keeps even a memory that contradicts
         * the ground under them.
         */
        @Test
        @DisplayName("a sighted player forgets nothing")
        void sightedForgetsNothing() {
            memory.getSquare(PLAYER).setFeature(granite);
            blind(0);

            level.updateView(player);

            assertSame(granite, memory.getSquare(PLAYER).getFeature(),
                    "only a blind player forgets");
        }

        /**
         * {@code square_isknown} is the second condition, and it is false when the memory already
         * holds {@code FEAT_NONE}. There is nothing to forget, and in particular the live level's
         * terrain is not written over in its place.
         */
        @Test
        @DisplayName("an unknown grid is left alone")
        void unknownGridIsLeftAlone() {
            memory.getSquare(PLAYER).setFeature(none);
            blind(4);

            level.updateView(player);

            assertSame(floor, level.getSquare(PLAYER).getFeature(),
                    "the live level is never the chunk that is forgotten");
        }
    }

    /**
     * The two full-level sweeps, checked through what they leave behind.
     */
    @Nested
    @DisplayName("the sweeps")
    class Sweeps {

        /**
         * A grid on the player's own side of the wall, within a carried light's reach, comes out of
         * the first sweep both in view and seen.
         */
        @Test
        @DisplayName("a reachable grid is put in view")
        void reachableGridEntersView() {
            player.getPlayerState().setCurLight(4);

            level.updateView(player);

            assertTrue(has(NEAR, SquareEnum.SQUARE_VIEW), "a clear line reaches the grid");
            assertTrue(has(NEAR, SquareEnum.SQUARE_SEEN), "and the light reaches it too");
        }

        /**
         * The wipe at the start is unconditional, so flags left over from a previous view do not
         * survive into a view that no longer supports them. The hidden grid is behind a wall and
         * cannot be reached by any line, so all three flags are off afterwards however they started.
         */
        @Test
        @DisplayName("stale flags on an unreachable grid are wiped")
        void staleFlagsAreWiped() {
            player.getPlayerState().setCurLight(4);
            Square hidden = level.getSquare(HIDDEN);
            hidden.sqInfoOn(SquareEnum.SQUARE_VIEW);
            hidden.sqInfoOn(SquareEnum.SQUARE_SEEN);
            hidden.sqInfoOn(SquareEnum.SQUARE_CLOSE_PLAYER);

            level.updateView(player);

            assertFalse(has(HIDDEN, SquareEnum.SQUARE_VIEW), "no line reaches behind the wall");
            assertFalse(has(HIDDEN, SquareEnum.SQUARE_SEEN), "and so it cannot be seen");
            assertFalse(has(HIDDEN, SquareEnum.SQUARE_CLOSE_PLAYER), "nor be close");
        }

        /**
         * The snapshot exists so the second sweep can spot a change, and a grid that was seen and no
         * longer is has to be redrawn. That the redraw happens at all is what shows the two sweeps
         * ran in order: the snapshot was taken before the recalculation and consumed after it.
         */
        @Test
        @DisplayName("a grid that has gone out of sight is redrawn")
        void gridGoneOutOfSightIsRedrawn() {
            level.getSquare(HIDDEN).sqInfoOn(SquareEnum.SQUARE_SEEN);

            level.updateView(player);

            assertTrue(redrewMap(), "the grid has just gone out of sight");
        }

        /**
         * The second sweep clears {@code SQUARE_WASSEEN} on every grid it visits, so a snapshot flag
         * surviving the call anywhere on the level would mean a grid the sweep never reached.
         */
        @Test
        @DisplayName("no snapshot flag survives anywhere on the level")
        void snapshotIsClearedEverywhere() {
            player.getPlayerState().setCurLight(4);
            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    level.getSquare(Loc.row(y).col(x)).sqInfoOn(SquareEnum.SQUARE_SEEN);

            level.updateView(player);

            for (int y = 0; y < HEIGHT; y++)
                for (int x = 0; x < WIDTH; x++)
                    assertFalse(has(Loc.row(y).col(x), SquareEnum.SQUARE_WASSEEN),
                            "grid " + y + "," + x + " was never reached by the second sweep");
        }
    }
}
