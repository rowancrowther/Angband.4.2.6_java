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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

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
 * <p>Class PlayerBirthEmbodyTest coded on 260901, extended on 260902, commented in full on
 * 260902.
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
    @DisplayName("a player with no race keeps the body they already had")
    void noRaceIsHarmless() throws Exception {
        set(player, "race", null);
        PlayerBody before = player.getPlayerBody();

        PlayerBirth.embody(player);

        assertSame(before, player.getPlayerBody(),
                "the early return leaves the constructor's body in place");
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
        assertNotSame(testRace.getBody().getSlot(0), player.getPlayerBody().getSlot(0),
                "C allocates a fresh slot array; the slots must not be the race's own");
    }

    /**
     * C's {@code memcpy} carries the template's name across and {@code string_make} gives the
     * player a copy of it; the loop then copies each slot's type and name in body order. So the
     * player's body has to read back as the same layout, slot for slot and in the same order — the
     * order is the slots' identity, the gear system addressing them by index.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @Test
    @DisplayName("name, slot count, order, types and names all carry across")
    void layoutIsCarriedAcross() throws Exception {
        PlayerBody template = new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "right hand"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "left hand"))));
        set(player, "race", race(template));

        PlayerBirth.embody(player);

        PlayerBody body = player.getPlayerBody();
        assertEquals("Humanoid", body.getName());
        assertEquals(3, body.getCount());
        for (int i = 0; i < template.getCount(); i++) {
            assertEquals(template.getSlot(i).getType(), body.getSlot(i).getType(),
                    "slot " + i + " keeps its type");
            assertEquals(template.getSlot(i).getName(), body.getSlot(i).getName(),
                    "slot " + i + " keeps its name");
        }
    }

    /**
     * C allocates the slot array with {@code mem_zalloc}, so every slot's {@code obj} starts NULL:
     * a new character wears nothing, whatever the template happens to be holding. The template in
     * the registry is never worn, so this can only be checked by wearing something on it first.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @Test
    @DisplayName("the new body is unworn, whatever the template holds")
    void theCopyIsUnworn() throws Exception {
        PlayerBody template = new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"))));
        set(template.getSlot(0), "item", new ItemObject());
        set(player, "race", race(template));

        PlayerBirth.embody(player);

        assertNull(player.getPlayerBody().getSlot(0).getItem(),
                "mem_zalloc leaves every slot empty");
    }

    /**
     * The reason the copy exists: a race's body is held once in the registry and pointed at by
     * every character of that race, so two characters embodied from the same race must come away
     * with separate slots. Sharing them would have one character's equipment appear on the other.
     *
     * @throws Exception if a fixture field cannot be reached
     */
    @Test
    @DisplayName("two characters of a race do not share slots")
    void twoCharactersOfARaceAreIndependent() throws Exception {
        PlayerRace shared = race(new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")))));
        Player other = new Player();
        set(player, "race", shared);
        set(other, "race", shared);

        PlayerBirth.embody(player);
        PlayerBirth.embody(other);

        assertNotSame(player.getPlayerBody(), other.getPlayerBody());
        assertNotSame(player.getPlayerBody().getSlot(0), other.getPlayerBody().getSlot(0));
    }
}
