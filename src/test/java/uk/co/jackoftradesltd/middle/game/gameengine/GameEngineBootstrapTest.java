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

package uk.co.jackoftradesltd.middle.game.gameengine;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs the game's start-up once, against the real data files, and checks what it leaves behind —
 * the port of C's {@code init_angband} ({@code init.c}) reached through {@link GameEngine#getGame}.
 *
 * <p>This is the one test in the suite that loads {@code lib/gamedata} for real, and it is
 * deliberately shallow: it asserts that the load <em>happened</em> and that each registry it fills
 * came back non-empty, not what any particular row contains. The individual parsers have their own
 * tests for that. What this catches is the thing none of those can — a data file that stops parsing,
 * a loader dropped from the sequence, or a registry left unfilled because two steps were ordered
 * wrongly.
 *
 * <p><b>It disturbs every global it touches</b>, so the whole of that state is saved before the load
 * and put back afterwards: the constants, the player, the cave, the engine singleton and the bus.
 * The registries are not restored — they are filled from the same files every time, so a later test
 * seeing them populated sees what a running game would.
 *
 * <p>The tests are ordered because the first performs the load and the rest read what it left; JUnit
 * gives no ordering by default, and each would otherwise have to load again.
 *
 * @author Rowan Crowther
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GameEngineBootstrapTest {

    /**
     * The engine singleton before this class ran.
     */
    private static Object savedInstance;

    /**
     * The constants holder before this class ran.
     */
    private static Object savedConstants;

    /**
     * The player before this class ran.
     */
    private static Object savedPlayer;

    /**
     * The cave before this class ran.
     */
    private static Object savedCave;

    /**
     * Saves the globals the load will overwrite.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeAll
    static void saveGlobals() throws Exception {
        savedInstance = engineField().get(null);
        savedConstants = constantsField().get(null);
        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();
    }

    /**
     * Puts them back, including the singleton — so a later test asking for the engine builds its own
     * rather than inheriting this one.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterAll
    static void restoreGlobals() throws Exception {
        engineField().set(null, savedInstance);
        constantsField().set(null, savedConstants);
        GameState.setPlayer((uk.co.jackoftradesltd.middle.player.Player) savedPlayer);
        GameState.setCave((uk.co.jackoftradesltd.middle.cave.Chunk) savedCave);
    }

    /**
     * The engine's singleton field, made accessible.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field engineField() throws Exception {
        Field field = GameEngine.class.getDeclaredField("instance");
        field.setAccessible(true);
        return field;
    }

    /**
     * The constants holder, made accessible.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    /**
     * How many cave profiles the dungeon registry holds.
     *
     * @return the count
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private static int caveProfileCount() throws Exception {
        Field field = uk.co.jackoftradesltd.middle.game.globals.registry.DungeonRegistry.class
                .getDeclaredField("caveProfiles");
        field.setAccessible(true);
        java.util.List<Object> profiles = (java.util.List<Object>) field.get(null);
        return profiles == null ? 0 : profiles.size();
    }

    /**
     * Building the engine does <em>not</em> load the game, and that gap is deliberate: the engine's
     * constructor creates the event bus and stops, so the caller has a window to register handlers
     * before the load starts raising events into it. C leaves the same gap between
     * {@code init_display()} and {@code init_angband()}.
     *
     * @throws Exception if a field cannot be reached
     */
    @Test
    @Order(1)
    @DisplayName("building the engine does not load the game")
    void buildingDoesNotLoad() throws Exception {
        engineField().set(null, null);
        constantsField().set(null, null);

        GameEngine engine = GameEngine.getGame();

        assertNotNull(engine);
        assertNotNull(GameEngine.getEventsBusHandler(), "the bus is up, ready for handlers");
        assertNull(constantsField().get(null),
                "and nothing has been read yet - the load is a separate call");
    }

    /**
     * The load itself, which reads every file under {@code lib/gamedata}. Everything below reads
     * what it left behind.
     */
    @Test
    @Order(2)
    @DisplayName("loading reads the data files")
    void loadingReadsTheDataFiles() {
        GameEngine.getGame().loadGameConstants();

        assertEquals(128, GameConstants.getWorldMaxDepth(),
                "the constants were read, so the load reached the first file");
    }

    /**
     * The singleton is a singleton: the second call hands back the engine the first built rather
     * than loading everything again.
     */
    @Test
    @Order(3)
    @DisplayName("a second call returns the same engine")
    void secondCallReturnsTheSameEngine() {
        assertSame(GameEngine.getGame(), GameEngine.getGame());
    }

    /**
     * The scalar tunables come from {@code constants.txt}, and these are its real values — the one
     * place in the suite where the figures are the game's rather than a fixture's.
     */
    @Test
    @Order(4)
    @DisplayName("the constants hold the values in constants.txt")
    void constantsMatchTheFile() {
        assertEquals(128, GameConstants.getWorldMaxDepth());
        assertEquals(10000, GameConstants.getWorldDayLength());
        assertEquals(23, GameConstants.getCarryCapPackSize());
        assertEquals(10, GameConstants.getCarryCapQuiverSize());
        assertEquals(40, GameConstants.getCarryCapQuiverSlotSize());
        assertEquals(1024, GameConstants.getLevelMaxMonsters());
    }

    /**
     * The object tables are filled. Each is asserted non-empty rather than counted, because the
     * counts move whenever the data files do and a test that pinned them would fail for the wrong
     * reason.
     */
    @Test
    @Order(5)
    @DisplayName("the object registry is filled")
    void objectRegistryIsFilled() {
        assertFalse(ObjectRegistry.getObjectKinds().isEmpty(), "object kinds");
        assertFalse(ObjectRegistry.getObjectBases().isEmpty(), "object bases");
        assertFalse(ObjectRegistry.getArtifacts().isEmpty(), "artifacts");
        assertFalse(ObjectRegistry.getEgoItems().isEmpty(), "ego items");
        assertFalse(ObjectRegistry.getCurses().isEmpty(), "curses");
    }

    /**
     * The power tables are filled too, and by the same load — they are built in code rather than
     * parsed, so a step dropped from the sequence would leave them null where the pricing expects
     * rows.
     */
    @Test
    @Order(6)
    @DisplayName("the power tables are built")
    void powerTablesAreBuilt() {
        assertNotNull(ObjectRegistry.archery);
        assertNotNull(ObjectRegistry.elementPowers);
        assertNotNull(ObjectRegistry.elementSets);
        assertNotNull(ObjectRegistry.flagSets);

        assertFalse(ObjectRegistry.elementPowers.isEmpty());
        assertFalse(ObjectRegistry.elementSets.isEmpty());
    }

    /**
     * The monster and player tables are filled, which between them cover the other two big loaders.
     */
    @Test
    @Order(7)
    @DisplayName("the monster and player registries are filled")
    void otherRegistriesAreFilled() {
        assertFalse(MonsterRegistry.monsterRaces.isEmpty(), "monster races");
        assertFalse(PlayerRegistry.getPlayerRaces().isEmpty(), "player races");
        assertFalse(PlayerRegistry.getPlayerClasses().isEmpty(), "player classes");
        assertFalse(PlayerRegistry.getPlayerBodies().isEmpty(), "player bodies");
    }

    /**
     * The load leaves a player and a level standing, so the game has somewhere to begin. The level
     * is the stub one the loader builds until level generation is ported, and it knows itself to be
     * the current level.
     */
    @Test
    @Order(8)
    @DisplayName("the load leaves a player standing on a level")
    void playerAndLevelExist() {
        assertNotNull(GameState.getPlayer());
        assertNotNull(GameState.getCave());
        assertSame(GameState.getCave(), GameState.getCave().getCurrentLevel(),
                "the level points at itself as the current one");
    }

    /**
     * The template loader is a second entry point into the dungeon-profile load, and is callable on
     * its own once the engine is up.
     *
     * <p>It reloads the profiles rather than adding to them, so calling it after the full load
     * leaves the registry holding the same number of rows it already had — which is the property
     * that makes it safe to call twice, and the only thing worth asserting about a method that
     * announces progress and delegates. The table has no accessor, so it is read through the field.
     *
     * @throws Exception if the profile table cannot be reached
     */
    @Test
    @Order(9)
    @DisplayName("the template parser reloads the dungeon profiles")
    void templateParserReloadsProfiles() throws Exception {
        int before = caveProfileCount();

        GameConstants.runTemplateParser();

        assertEquals(before, caveProfileCount(), "the profiles were reloaded, not appended to");
        assertTrue(before > 0, "and there were profiles to reload");
    }

    /**
     * The flavour table is read, but no object kind has been given a flavour from it.
     *
     * <p><b>Outstanding.</b> {@code loadFlavours} parses {@code flavor.txt} into the miscellaneous
     * registry and stops there. C goes on to shuffle the flavours and attach one to each flavoured
     * kind, from a fixed seed so that a given savefile always sees the same colours
     * ({@code flavor_init}, {@code obj-util.c:154}). Until that runs, every potion is nameless
     * rather than "a murky red potion", and {@code ObjectKind.getFlavour()} answers null across the
     * board.
     *
     * <p>Asserted as it behaves, so the test fails the day the assignment is ported — at which point
     * it becomes a test that flavoured kinds get their flavours.
     */
    @Test
    @Order(10)
    @DisplayName("flavours are loaded but not yet attached to kinds")
    void flavoursLoadedButNotAttached() {
        assertFalse(uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry.getFlavours().isEmpty(),
                "the flavour table itself was read");

        for (uk.co.jackoftradesltd.middle.objects.ObjectKind kind : ObjectRegistry.getObjectKinds()) {
            assertNull(kind.getFlavour(),
                    "no kind is given a flavour yet, including " + kind.getName());
        }
    }
}
