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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.CurseFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The second-round tests for the damage components of {@link ItemObject}, the ports of
 * {@code slay_power}, {@code extra_shots_power}, {@code extra_blows_power} and
 * {@code launcher_ammo_damage_power} ({@code obj-power.c}). They add to
 * {@code ItemObjectDamagePowerTest} rather than repeat it, and cover what its first pass left out:
 * a best figure between 1 and 100 (which is penalised by {@code best - 100}, not by 99), a set of
 * slays that holds the same slay twice, negative blows and ammunition truncating toward zero, and
 * the curse overloads at the same arithmetic as the item ones.
 *
 * <p>Every expected figure is worked out by hand from the C source and the constants in
 * {@code obj-power.h}, with the arithmetic in the comment on each case. None is read back from the
 * port.
 *
 * <p>Class ItemObjectPartERound2Test coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectPartERound2Test {

    private static Object savedArchery;

    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * Installs the archery table with the figures C gives it.
     */
    @BeforeAll
    static void seedArchery() {
        Map<TValue, Archery> archery = new HashMap<>();
        archery.put(TValue.TV_SHOT, new Archery(TValue.TV_SHOT, 10, 9, 4));
        archery.put(TValue.TV_ARROW, new Archery(TValue.TV_ARROW, 12, 9, 5));
        archery.put(TValue.TV_BOLT, new Archery(TValue.TV_BOLT, 14, 9, 7));
        savedArchery = ItemFixture.setStatic(ObjectRegistry.class, "archery", archery);
    }

    /**
     * Puts the archery table back.
     */
    @AfterAll
    static void restoreArchery() {
        ItemFixture.setStatic(ObjectRegistry.class, "archery", savedArchery);
    }

    private static Object invoke(Object target, String name, Class<?>[] types, Object... args) throws Exception {
        Method method = ItemObject.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private static int price(ItemObject item, String name, int power) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{int.class}, power);
    }

    private static int price(ItemObject item, String name, Curse curse, int power) throws Exception {
        return (int) invoke(curse.getItemObject(), name, new Class<?>[]{int.class}, power);
    }

    private static int slayPrice(ItemObject item, int power, int dicePower) throws Exception {
        return (int) invoke(item, "slayPower", new Class<?>[]{int.class, boolean.class, int.class},
                power, false, dicePower);
    }

    private static ItemObject item(TValue tValue) {
        return ItemFixture.item(tValue).build();
    }

    private static Curse curse(ObjectModifier modifier, int value) {
        Map<ObjectModifier, Integer> modifiers = new HashMap<>();
        modifiers.put(modifier, value);
        return CurseFixture.curse("test curse", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                modifiers, new HashMap<>(), 0, 0, 0, List.of(),
                new Flag<>(ObjectFlag.class), "", "", 1);
    }

    private static Brand brand(String code, int power) {
        return new Brand(code, code, "burns", null, null, 2, 1, power);
    }

    private static Slay slay(String monster, int mult, int power) {
        return new Slay(monster + "_2", monster.toLowerCase(), null, "slays", "slays",
                MonsterRaceFlag.valueOf("RF_" + monster), mult, 1, power);
    }

    /**
     * Installs a player with a body.
     */
    @BeforeEach
    void installPlayer() {
        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());
    }

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * C's {@code best_power} starts at 1 and the penalty is {@code best_power - 100}, so a brand
     * scoring 50 on dice of 60 is 60 * 60 * (50 - 100) / 2500 = -180000 / 2500 = -72, and one scoring
     * 99 is 3600 * -1 / 2500 = -1.44, truncated toward zero to -1. Only a best of 1 gives the 99
     * penalty.
     */
    @ParameterizedTest(name = "a brand of power {0} on dice 60 prices at {1}")
    @CsvSource({"50, -72", "99, -1", "100, 0", "1, -142"})
    @DisplayName("a weak brand is penalised by its distance below 100")
    void weakBrandBelowBaseline(int brandPower, int expected) throws Exception {
        ItemObject sword = item(TValue.TV_SWORD);
        sword.getBrands().add(brand("FIRE_2", brandPower));

        assertEquals(expected, slayPrice(sword, 0, 60));
    }

    /**
     * A set holds a slay once, as C's one-bool-per-index array does. Adding the same slay twice
     * leaves one: 60 * 60 * (120 - 100) / 2500 = 72000 / 2500 = 28, with no multiple-slays bonus
     * (which would add 4 * 60 / 25 = 9).
     */
    @Test
    @DisplayName("the same slay added twice is counted once")
    void duplicateSlay() throws Exception {
        ItemObject sword = item(TValue.TV_SWORD);
        sword.getSlays().add(slay("EVIL", 2, 120));
        sword.getSlays().add(slay("EVIL", 2, 120));

        assertEquals(28, slayPrice(sword, 0, 60));
    }

    /**
     * Two different slays at the same strength do earn the bonus: 28 + 2 * 2 * 60 / 25 = 28 + 9.
     */
    @Test
    @DisplayName("two different slays earn the multiple-slays bonus")
    void twoSlays() throws Exception {
        ItemObject sword = item(TValue.TV_SWORD);
        sword.getSlays().add(slay("EVIL", 2, 120));
        sword.getSlays().add(slay("ORC", 2, 120));

        assertEquals(37, slayPrice(sword, 0, 60));
    }

    /**
     * A kill beside a brand earns no pair bonus (the pair test is slays and brands, and kills are
     * counted apart): the best is the brand's 161, so 60 * 60 * 61 / 2500 = 219600 / 2500 = 87.
     */
    @Test
    @DisplayName("a kill and a brand earn no pair bonus")
    void killAndBrand() throws Exception {
        ItemObject sword = item(TValue.TV_SWORD);
        sword.getBrands().add(brand("FIRE_2", 161));
        sword.getSlays().add(slay("DRAGON", 5, 110));

        assertEquals(87, slayPrice(sword, 0, 60));
    }

    /**
     * Negative blows go through the same scaling and the boost truncates toward zero. Blows -3 on 100
     * is 100 * (5 - 3) / 5 = 40, then 15 * -3 * 5 / 2 = -225 / 2 = -112, giving -72. Blows -2 is
     * 100 * 3 / 5 = 60 and 15 * -2 * 5 / 2 = -75, giving -15.
     */
    @ParameterizedTest(name = "blows {0} on 100 prices at {1}")
    @CsvSource({"-3, -72", "-2, -15"})
    @DisplayName("negative blows reduce the total, truncating toward zero")
    void negativeBlows(int blows, int expected) throws Exception {
        ItemObject ring = item(TValue.TV_RING);
        ring.getModifiers().put(ObjectModifier.OM_BLOWS, blows);

        assertEquals(expected, price(ring, "extraBlowsPower", 100));
        assertEquals(expected, price(item(TValue.TV_RING), "extraBlowsPower", curse(ObjectModifier.OM_BLOWS, blows), 100));
    }

    /**
     * Shots scale as {@code p * (10 + q)} then {@code / 10}: 7 on 99 is 99 * 17 = 1683 / 10 = 168,
     * and 3 on 105 is 105 * 13 = 1365 / 10 = 136.
     */
    @ParameterizedTest(name = "shots {0} on {1} prices at {2}")
    @CsvSource({"7, 99, 168", "3, 105, 136"})
    @DisplayName("shots truncate after the multiply")
    void shotsTruncate(int shots, int power, int expected) throws Exception {
        ItemObject sling = item(TValue.TV_BOW);
        sling.getModifiers().put(ObjectModifier.OM_SHOTS, shots);

        assertEquals(expected, price(sling, "extraShotsPower", power));
        assertEquals(expected, price(item(TValue.TV_BOW), "extraShotsPower", curse(ObjectModifier.OM_SHOTS, shots), power));
    }

    /**
     * A negative running total truncates toward zero as it is rescaled: shot -15 * 4 / 10 = -6,
     * arrow -15 * 5 / 10 = -7 (-75 / 10), bolt -15 * 7 / 10 = -10 (-105 / 10).
     */
    @ParameterizedTest(name = "{0} at -15 prices at {1}")
    @CsvSource({"TV_SHOT, -6", "TV_ARROW, -7", "TV_BOLT, -10"})
    @DisplayName("negative ammunition totals truncate toward zero")
    void negativeAmmo(TValue tValue, int expected) throws Exception {
        assertEquals(expected, price(item(tValue), "launcherAmmoDamagePower", -15));
    }
}
