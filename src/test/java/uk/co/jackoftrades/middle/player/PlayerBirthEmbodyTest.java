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
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.game.globals.data.CarryCapData;
import uk.co.jackoftrades.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static uk.co.jackoftrades.testsupport.ItemFixture.set;

/**
 * Tests {@link PlayerBirth#embody}, the port of C's {@code player_embody}
 * ({@code player-birth.c:369}), which gives a player the body their race is built with.
 *
 * <p>The method is one assignment because {@link PlayerBody#copy} is both halves of what C does —
 * the struct copy and the fresh, unworn slot array. What is left to test here is the pair of
 * decisions {@code embody} itself makes: that a player with no race is left alone rather than
 * failing, and that the body taken is a <em>copy</em> and not the race's own.
 *
 * <p>That second point is the one that matters and is invisible until two characters of a race
 * exist: a race's body is held once in the registry and shared by every member of the race, so
 * taking the reference would have them all wearing the same equipment.
 *
 * <p>{@link Player}'s constructor reads the registry for a race, so the fixture seeds it and puts
 * the old tables back afterwards.
 *
 * <p>Class PlayerBirthEmbodyTest coded on 260901, commented in full on 260901.
 *
 * @author Rowan Crowther
 */
class PlayerBirthEmbodyTest {

    /**
     * The pack size the upkeep's arrays are sized from.
     */
    private static final int PACK_SIZE = 23;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * Saved globals, put back after the test.
     */
    private Object savedConstants;

    /**
     * The registry's bodies before.
     */
    private Object savedBodies;

    /**
     * The registry's races before.
     */
    private Object savedRaces;

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
     * Seeds a body and a race for the constructor to find, then builds the player.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void seedRegistry() throws Exception {
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

        player = new Player();
    }

    /**
     * Puts the registry back.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterEach
    void restoreRegistry() throws Exception {
        registryField("playerBodies").set(null, savedBodies);
        registryField("playerRaces").set(null, savedRaces);
        constantsField().set(null, savedConstants);
    }

    /**
     * With no race there is no body to take, and the call is harmless rather than an error — the
     * character is built in stages and this one may run before the race is chosen.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @Test
    @DisplayName("a player with no race is left alone")
    void noRaceIsHarmless() throws Exception {
        set(player, "race", null);

        PlayerBirth.embody(player);
    }

    /**
     * With a race, the player takes a <em>copy</em> of its body rather than the race's own — so two
     * characters of the same race do not share their equipment slots.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @Test
    @DisplayName("the body is copied from the race, not shared with it")
    void bodyIsCopiedFromTheRace() throws Exception {
        PlayerRace testRace = race(new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")))));
        set(player, "race", testRace);

        PlayerBirth.embody(player);

        assertEquals(1, player.getPlayerBody().getSlots().size());
        assertNotSame(testRace.getBody(), player.getPlayerBody(),
                "the player's body is its own");
    }
}
