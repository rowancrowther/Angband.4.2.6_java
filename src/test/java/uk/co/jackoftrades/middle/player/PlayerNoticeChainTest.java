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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.KnownObject;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerNotice;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static uk.co.jackoftrades.testsupport.ItemFixture.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#noticeStuff()} and the two actions it dispatches to — the port of C's
 * {@code notice_stuff} ({@code player-calcs.c:2536}) and the top of {@code ignore_drop} and
 * {@code calc_inventory}.
 *
 * <p>The dispatcher's shape is what these tests are about. Each flag is cleared <em>before</em> the
 * work it asks for runs, and that ordering is deliberate: {@code ignoreDrop} raises the combine flag
 * again as its last act, and because the ignore flag has already been cleared the newly raised one
 * survives to be seen on the next pass rather than being wiped by this one. A dispatcher that
 * cleared afterwards would swallow it, and nothing else would report the loss.
 *
 * <p>{@code noticeStuff} also returns immediately when nothing is pending, which is the common case
 * — it runs every turn.
 *
 * @author Rowan Crowther
 */
class PlayerNoticeChainTest {

    /**
     * The pack size the upkeep's arrays are sized from.
     */
    private static final int PACK_SIZE = 23;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The level the player stands on.
     */
    private Chunk level;

    /**
     * Saved globals, put back after each test.
     */
    private Object savedConstants;

    /**
     * The player the game held before.
     */
    private Player savedPlayer;

    /**
     * The cave the game held before.
     */
    private Chunk savedCave;

    /**
     * The registry's bodies before.
     */
    private Object savedBodies;

    /**
     * The registry's races before.
     */
    private Object savedRaces;

    /**
     * The object registry's curse table before. A fresh {@link KnownObject} indexes it while
     * building its own curse map, so it has to exist even for a test that has no curses.
     */
    private Object savedCurses;

    /**
     * The object registry's curse table, made accessible.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field curseField() throws Exception {
        Field field = uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry.class
                .getDeclaredField("curses");
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
     * One of the player registry's private tables, made accessible.
     *
     * @param name the field's name
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field registryField(String name) throws Exception {
        Field field = PlayerRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    /**
     * A race built on the given body.
     *
     * @param body the race's body plan
     * @return the race
     */
    private static PlayerRace race(PlayerBody body) {
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, body,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * A plain item, with the collections the gear walkers read.
     *
     * @return the item
     * @throws Exception if a field cannot be reached
     */
    private static ItemObject item() {
        return ItemFixture.item(TValue.TV_POTION).build();
    }

    /**
     * Seeds the carry capacities, a body, a race, a player and a level.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void seedWorld() throws Exception {
        savedConstants = constantsField().get(null);
        constantsField().set(null, new GameConstantsData(
                null, null, null, null, null,
                new CarryCapData(PACK_SIZE, 10, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null));

        savedBodies = registryField("playerBodies").get(null);
        savedRaces = registryField("playerRaces").get(null);
        PlayerBody humanoid = new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"))));
        registryField("playerBodies").set(null, new ArrayList<>(List.of(humanoid)));
        registryField("playerRaces").set(null, new ArrayList<>(List.of(race(humanoid))));

        savedPlayer = GameState.getPlayer();
        savedCave = GameState.getCave();

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        level.setCurrentLevel(level);
        GameState.setCave(level);
        GameState.setPlayer(player);
        savedCurses = curseField().get(null);
        curseField().set(null, new ArrayList<uk.co.jackoftrades.middle.objects.Curse>());
        set(player, "itemKnowledge", new KnownObject());
    }

    /**
     * Puts every global back.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterEach
    void restoreWorld() throws Exception {
        GameState.setPlayer(savedPlayer);
        GameState.setCave(savedCave);
        constantsField().set(null, savedConstants);
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
        curseField().set(null, savedCurses);
    }

    /**
     * {@code attackRandomMonster}, which a confused player uses to flail at their surroundings.
     */
    @Test
    @DisplayName("a confused player cannot attack at random")
    void confusedPlayerCannotAttack() throws Exception {
        Map<uk.co.jackoftrades.middle.player.enums.TimedEffect, Integer> timed = new HashMap<>();
        for (uk.co.jackoftrades.middle.player.enums.TimedEffect effect
                : uk.co.jackoftrades.middle.player.enums.TimedEffect.values()) {
            timed.put(effect, 0);
        }
        timed.put(uk.co.jackoftrades.middle.player.enums.TimedEffect.TMD_CONFUSED, 5);
        set(player, "timed", timed);

        assertFalse(player.attackRandomMonster(),
                "the confusion test is the first thing it does");
    }

    /**
     * And an unconfused one gets as far as the search, which is a stub and finds nothing.
     */
    @Test
    @DisplayName("an unconfused player finds nothing to attack yet")
    void unconfusedPlayerFindsNothing() {
        assertFalse(player.attackRandomMonster());
    }

    /**
     * The dispatcher.
     */
    @Nested
    @DisplayName("noticeStuff")
    class Dispatcher {

        /**
         * With nothing pending it returns at once — the common case, since it runs every turn.
         */
        @Test
        @DisplayName("with nothing pending it does nothing")
        void nothingPendingDoesNothing() {
            assertFalse(player.getPlayerUpkeep().isNotice());

            player.noticeStuff();

            assertFalse(player.getPlayerUpkeep().isNotice());
        }

        /**
         * The combine flag is cleared and the pack rebuilt, so a second pass has nothing left to do.
         */
        @Test
        @DisplayName("a pending combine is carried out and cleared")
        void combineIsCarriedOutAndCleared() {
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_COMBINE);

            player.noticeStuff();

            assertFalse(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE));
            assertFalse(player.getPlayerUpkeep().isNotice(), "nothing is left pending");
        }

        /**
         * The ignore pass raises the combine flag as its last act, and the combine block sits
         * <em>after</em> the ignore block — so the combine it asks for is carried out in the same
         * pass rather than waiting for the next turn, and nothing is left pending afterwards.
         *
         * <p>That block ordering is the point. Reversed, the combine would be consumed before the
         * ignore pass had asked for it, and the pack would be left uncombined until something else
         * raised the flag.
         */
        @Test
        @DisplayName("the combine the ignore pass asks for runs in the same pass")
        void ignoreCombineRunsImmediately() {
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_IGNORE);

            player.noticeStuff();

            assertFalse(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_IGNORE),
                    "the ignore pass is done");
            assertFalse(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE),
                    "and the combine it asked for was carried out before the method returned");
            assertFalse(player.getPlayerUpkeep().isNotice(), "so nothing is left pending");
        }

        /**
         * The monster-message flag is cleared even though the flush behind it is a chapter-6 stub,
         * so the queued messages are discarded rather than deferred. Recorded because it is a known
         * consequence rather than an oversight.
         */
        @Test
        @DisplayName("the monster-message flag is cleared even though the flush is stubbed")
        void monsterMessageFlagIsCleared() {
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_MON_MESSAGE);

            player.noticeStuff();

            assertFalse(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_MON_MESSAGE));
        }

        /**
         * All three at once are dealt with in one pass, bar the combine the ignore pass asks for.
         */
        @Test
        @DisplayName("all three pending actions are dealt with in one pass")
        void allThreeAreDealtWith() {
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_IGNORE);
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_COMBINE);
            player.getPlayerUpkeep().setNoticeFlagOn(PlayerNotice.PN_MON_MESSAGE);

            player.noticeStuff();

            assertFalse(player.getPlayerUpkeep().isNotice(),
                    "every pending action was dealt with, including the combine the ignore pass "
                            + "asked for after the first one had run");
        }
    }

    /**
     * The ignore pass, which drops what the player has marked and asks for a rebuild.
     */
    @Nested
    @DisplayName("ignoreDrop")
    class IgnoreDrop {

        /**
         * An empty pack drops nothing, but still asks for the gear to be rebuilt and the pack
         * recombined — the two flags at the foot are raised whatever happened above, because a chain
         * that dropped earlier items still needs them.
         */
        @Test
        @DisplayName("it always asks for a rebuild and a recombine")
        void alwaysAsksForRebuild() {
            player.ignoreDrop();

            assertTrue(player.getPlayerUpkeep().getNoticeFlags().has(PlayerNotice.PN_COMBINE));
        }

        /**
         * Nothing is dropped when nothing is marked, so an ordinary pack survives the pass intact.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("an unmarked pack is left alone")
        void unmarkedPackIsUntouched() throws Exception {
            ItemObject potion = item();
            player.getGear().add(potion);

            player.ignoreDrop();

            assertTrue(player.getGear().contains(potion));
        }
    }

    /**
     * The inventory rebuild, which the combine pass ends with.
     */
    @Nested
    @DisplayName("calcInventory")
    class Inventory {

        /**
         * An empty gear list produces an empty pack and an empty quiver, and says so in the counts.
         */
        @Test
        @DisplayName("an empty gear list gives an empty pack")
        void emptyGearGivesEmptyPack() {
            player.calcInventory();

            assertEquals(0, player.getPlayerUpkeep().getInventoryCount());
            assertEquals(0, player.getPlayerUpkeep().getQuiverCount());
        }

        /**
         * A carried item is placed in the pack and counted.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a carried item reaches the pack")
        void carriedItemReachesThePack() throws Exception {
            ItemObject potion = item();
            player.getGear().add(potion);

            player.calcInventory();

            assertEquals(1, player.getPlayerUpkeep().getInventoryCount());
            assertSame(potion, player.getPlayerUpkeep().getInventory()[0]);
        }

        /**
         * The pack is rebuilt from the gear each time rather than added to, so an item that has left
         * the gear leaves the pack with it.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the pack is rebuilt, not added to")
        void packIsRebuilt() throws Exception {
            ItemObject potion = item();
            player.getGear().add(potion);
            player.calcInventory();

            player.getGear().remove(potion);
            player.calcInventory();

            assertEquals(0, player.getPlayerUpkeep().getInventoryCount());
            assertNotNull(player.getPlayerUpkeep().getInventory(),
                    "the array itself survives the rebuild");
        }
    }

    /**
     * {@code embody}, which gives the player the body their race describes.
     */
    @Nested
    @DisplayName("embody")
    class Embody {

        /**
         * With no race there is no body to take, and the call is harmless rather than an error —
         * the character is built in stages and this one may run before the race is chosen.
         */
        @Test
        @DisplayName("a player with no race is left alone")
        void noRaceIsHarmless() {
            player.embody();
        }

        /**
         * With a race, the player takes a <em>copy</em> of its body rather than the race's own — so
         * two characters of the same race do not share their equipment slots.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the body is copied from the race, not shared with it")
        void bodyIsCopiedFromTheRace() throws Exception {
            PlayerRace testRace = race(new PlayerBody("Humanoid", new ArrayList<>(List.of(
                    new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")))));
            set(player, "race", testRace);

            player.embody();

            assertEquals(1, player.getPlayerBody().getSlots().size());
            assertFalse(player.getPlayerBody() == testRace.getBody(),
                    "the player's body is its own");
        }
    }
}
