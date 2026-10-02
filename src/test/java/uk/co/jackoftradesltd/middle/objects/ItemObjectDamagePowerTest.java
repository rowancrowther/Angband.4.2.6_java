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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the damage components of the power calculation in {@link ItemObject}: {@code slayPower},
 * {@code extraMightPower}, {@code extraShotsPower}, {@code extraBlowsPower},
 * {@code launcherAmmoDamagePower}, {@code bowMulitplier}, {@code ammoDamagePower},
 * {@code damageDicePower} and {@code toDamagePower}, each with its plain and {@link Curse}
 * overload where it has one. They are the ports of {@code slay_power}, {@code extra_might_power},
 * {@code extra_shots_power}, {@code extra_blows_power}, {@code launcher_ammo_damage_power},
 * {@code bow_multiplier}, {@code ammo_damage_power}, {@code damage_dice_power} and
 * {@code to_damage_power} ({@code obj-power.c}).
 *
 * <p>Every expected figure is worked out by hand from the C source and the constants in
 * {@code obj-power.h}, not read back from the port, and the comment on each case shows the
 * arithmetic. Most of the cases are there because C's integer division truncates toward zero, so a
 * port that floored, or that divided before it multiplied, would be a point out on exactly the
 * figures chosen.
 *
 * <p>The boundaries worth the most are these. Extra blows are priced at 2 and refused at 3, shots at
 * 20 and 21, might at 3 and 4. A slay with a multiplier of 3 is a slay and one with 4 is a kill,
 * which changes which bonus two of them earn. The brand search starts from 1, so a brand of power 0
 * still costs 99 below the bare weapon rather than 100. And a ring's to-damage is priced three times
 * over what a sword's is, which only the second lot for non-weapons explains.
 *
 * <p>The player is installed before any item is built, because an item captures the player at
 * construction, and the archery table is seeded with the figures in C and put back afterwards so
 * the class passes alone and leaves nothing behind for another suite.
 *
 * <p>Class ItemObjectDamagePowerTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectDamagePowerTest {

    private static Object savedArchery;

    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * Installs the archery table with the figures C gives it:
     * {@code { tval, ammo_dam, launch_dam, launch_mult }}.
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

    /**
     * Calls a private method, unwrapping any exception it throws.
     *
     * @param target the object to call it on
     * @param name   the method's name
     * @param types  the parameter types
     * @param args   the argument values
     * @return what it returns
     */
    private static Object invoke(Object target, String name, Class<?>[] types, Object... args) throws Exception {
        Method method = ItemObject.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    /**
     * Prices an item with a component that takes only the running total.
     *
     * @param item  the item
     * @param name  the component
     * @param power the running total handed in
     * @return the running total handed back
     */
    private static int price(ItemObject item, String name, int power) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{int.class}, power);
    }

    /**
     * Prices a curse with the {@link Curse} overload of a component that takes only the running
     * total.
     *
     * @param item  any item; the curse overloads do not read it
     * @param name  the component
     * @param curse the curse
     * @param power the running total handed in
     * @return the running total handed back
     */
    private static int price(ItemObject item, String name, Curse curse, int power) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{Curse.class, int.class}, curse, power);
    }

    /**
     * Calls a component that takes no argument, such as {@code toDamagePower}.
     *
     * @param item the item
     * @param name the component
     * @return its answer
     */
    private static int priceAlone(ItemObject item, String name) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{});
    }

    /**
     * Calls the {@link Curse} overload of a component that takes only the curse.
     *
     * @param item  any item; the curse overloads do not read it
     * @param name  the component
     * @param curse the curse
     * @return its answer
     */
    private static int priceAlone(ItemObject item, String name, Curse curse) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{Curse.class}, curse);
    }

    /**
     * A bare item of the given type.
     *
     * @param tValue the item's type
     * @return the item
     */
    private static ItemObject item(TValue tValue) {
        return ItemFixture.item(tValue).build();
    }

    /**
     * A bare item of the given type with its dice and to-damage set.
     *
     * @param tValue the item's type
     * @param dice   C's {@code dd}
     * @param sides  C's {@code ds}
     * @param toDam  C's {@code to_d}
     * @return the item
     */
    private static ItemObject item(TValue tValue, int dice, int sides, int toDam) {
        ItemObject item = item(tValue);
        set(item, "damageDice", dice);
        set(item, "damageSides", sides);
        set(item, "toDam", toDam);
        return item;
    }

    /**
     * A launcher of the given type whose kind fires the given ammunition.
     *
     * @param flags the kind flags to raise
     * @return the launcher
     */
    private static ItemObject launcher(ObjectKindFlag... flags) {
        return ItemFixture.item(TValue.TV_BOW).kind(kindFiring(flags)).build();
    }

    /**
     * A kind with the given kind flags raised. The flags are written through the field, because
     * {@code getKindFlags()} answers a read-only view.
     *
     * @param flags the kind flags to raise
     * @return the kind
     */
    @SuppressWarnings("unchecked")
    private static ObjectKind kindFiring(ObjectKindFlag... flags) {
        ObjectKind kind = new ObjectKind();
        Flag<ObjectKindFlag> own = (Flag<ObjectKindFlag>) ItemFixture.read(kind, "kindFlags");
        for (ObjectKindFlag flag : flags) {
            own.on(flag);
        }
        return kind;
    }

    /**
     * An ego, which here exists only to be non-null: {@code launcher_ammo_damage_power} tests the
     * pointer and reads nothing from it.
     *
     * @return an ego with every field empty
     */
    private static EgoItem ego() {
        return new EgoItem("of Testing", null, 0, 0, new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectFlag.class), null, Map.of(), Map.of(), Map.of(),
                java.util.Set.of(), java.util.Set.of(), Map.of(), 0, 0, 0, 0, List.of(),
                null, null, null, 0, 0, 0, null, null, false);
    }

    /**
     * Gives an item a modifier.
     *
     * @param item     the item
     * @param modifier the modifier
     * @param value    its value
     * @return the item, for chaining
     */
    private static ItemObject with(ItemObject item, ObjectModifier modifier, int value) {
        item.getModifiers().put(modifier, value);
        return item;
    }

    /**
     * A curse with the given modifiers and to-damage and nothing else.
     *
     * @param modifier the one modifier it carries, or {@code null} for none
     * @param value    that modifier's value
     * @param damage   C's {@code to_d}
     * @return the curse
     */
    private static Curse curse(ObjectModifier modifier, int value, int damage) {
        Map<ObjectModifier, Integer> modifiers = new HashMap<>();
        if (modifier != null) {
            modifiers.put(modifier, value);
        }
        return new Curse("test curse", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                modifiers, new HashMap<>(), 0, damage, 0, List.of(),
                new Flag<>(ObjectFlag.class), "", "", 1);
    }

    /**
     * A brand with the given code and power.
     *
     * @param code  the brand's code, distinct per brand because a set holds them
     * @param power its power rating
     * @return the brand
     */
    private static Brand brand(String code, int power) {
        return new Brand(code, code, "burns", null, null, 2, 1, power);
    }

    /**
     * A slay with the given monster, multiplier and power.
     *
     * @param monster the monster flag's name without its prefix, for example {@code "EVIL"}
     * @param mult    its multiplier, which decides whether it counts as a slay or a kill
     * @param power   its power rating
     * @return the slay
     */
    private static Slay slay(String monster, int mult, int power) {
        return new Slay(monster + "_2", monster.toLowerCase(), null, "slays", "slays",
                MonsterRaceFlag.valueOf("RF_" + monster), mult, 1, power);
    }

    /**
     * Installs a player with a body, which every shooting-slot comparison needs.
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
     * C's {@code to_damage_power}: half of DAMAGE_POWER a point, and the full figure again for
     * anything that is not a weapon, missile or launcher.
     */
    @Nested
    @DisplayName("toDamagePower")
    class ToDamage {

        /**
         * DAMAGE_POWER is 5, so a point is 5 / 2 = 2.5 and the division truncates toward zero. A
         * weapon, a missile and a launcher take that lot only: 4 is 20 / 2 = 10, 3 is 15 / 2 = 7
         * and -3 is -15 / 2 = -7, not the -8 a floor would give.
         */
        @ParameterizedTest(name = "{0} at to_d {1} is priced at {2}")
        @CsvSource({
                "TV_SWORD, 4, 10", "TV_SWORD, 3, 7", "TV_SWORD, -3, -7", "TV_SWORD, 0, 0",
                "TV_HAFTED, 4, 10", "TV_POLEARM, 4, 10", "TV_DIGGING, 4, 10",
                "TV_ARROW, 4, 10", "TV_SHOT, 3, 7", "TV_BOLT, -3, -7",
                "TV_BOW, 4, 10"})
        @DisplayName("a weapon, missile or launcher takes the first lot only")
        void firstLotOnly(TValue tValue, int toDam, int expected) throws Exception {
            assertEquals(expected, priceAlone(item(tValue, 0, 0, toDam), "toDamagePower"));
        }

        /**
         * Anything else takes a second lot at the full 5 a point on top of the first, so 4 is
         * 10 + 20 = 30, 3 is 7 + 15 = 22 and -3 is -7 + -15 = -22: three times a weapon's figure
         * before the truncation.
         */
        @ParameterizedTest(name = "{0} at to_d {1} is priced at {2}")
        @CsvSource({
                "TV_RING, 4, 30", "TV_RING, 3, 22", "TV_RING, -3, -22", "TV_RING, 0, 0",
                "TV_CLOAK, 4, 30", "TV_AMULET, 4, 30"})
        @DisplayName("a non-weapon takes a second lot as well")
        void secondLot(TValue tValue, int toDam, int expected) throws Exception {
            assertEquals(expected, priceAlone(item(tValue, 0, 0, toDam), "toDamagePower"));
        }

        /**
         * A curse object is never a weapon, missile or launcher, so C always reaches the second lot:
         * 4 is 10 + 20 = 30, 3 is 7 + 15 = 22, -3 is -7 + -15 = -22.
         */
        @ParameterizedTest(name = "curse to_d {0} is priced at {1}")
        @CsvSource({"4, 30", "3, 22", "-3, -22", "0, 0"})
        @DisplayName("the curse overload always takes both lots")
        void curseOverload(int damage, int expected) throws Exception {
            assertEquals(expected, priceAlone(item(TValue.TV_SWORD), "toDamagePower", curse(null, 0, damage)));
        }

        /**
         * An item built before a character exists holds no player. The calculation must read the
         * live one, as C reads its global at the call: a ring built with no player and priced after
         * one is installed is 30, not a crash.
         */
        @Test
        @DisplayName("an item built before a character exists is priced against the live player")
        void livePlayer() throws Exception {
            GameState.setPlayer(null);
            ItemObject ring = item(TValue.TV_RING, 0, 0, 4);
            GameState.setPlayer(new Player());

            assertEquals(30, priceAlone(ring, "toDamagePower"));
        }

        /**
         * With no character at all there is no body to find the shooting slot on. C would crash on
         * its null global; the port throws a {@link NullPointerException}.
         */
        @Test
        @DisplayName("with no player at all the slot lookup throws")
        void noPlayer() {
            ItemObject ring = item(TValue.TV_RING, 0, 0, 4);
            GameState.setPlayer(null);

            assertThrows(NullPointerException.class, () -> priceAlone(ring, "toDamagePower"));
        }
    }

    /**
     * C's {@code damage_dice_power}: dice for a weapon or missile, a flat figure for a non-weapon
     * with combat bonuses, nothing for a launcher.
     */
    @Nested
    @DisplayName("damageDicePower")
    class DamageDice {

        /**
         * {@code dd * (ds + 1) * 5 / 4}, truncating: 2d5 is 2 * 6 * 5 / 4 = 15, 1d4 is
         * 1 * 5 * 5 / 4 = 25 / 4 = 6, 3d7 is 3 * 8 * 5 / 4 = 30, and an arrow of 1d9 is
         * 1 * 10 * 5 / 4 = 12.
         */
        @ParameterizedTest(name = "{0} {1}d{2} is priced at {3}")
        @CsvSource({
                "TV_SWORD, 2, 5, 15", "TV_SWORD, 1, 4, 6", "TV_SWORD, 3, 7, 30",
                "TV_HAFTED, 2, 5, 15", "TV_POLEARM, 2, 5, 15", "TV_DIGGING, 2, 5, 15",
                "TV_ARROW, 1, 9, 12", "TV_SHOT, 1, 4, 6", "TV_BOLT, 1, 9, 12",
                "TV_SWORD, 0, 0, 0"})
        @DisplayName("a weapon or missile is priced from its dice")
        void diceFormula(TValue tValue, int dice, int sides, int expected) throws Exception {
            assertEquals(expected, priceAlone(item(tValue, dice, sides, 0), "damageDicePower"));
        }

        /**
         * A weapon takes the first branch, so its brand does not also earn the flat figure:
         * 2d5 with a brand is still 15, not 15 + 60.
         */
        @Test
        @DisplayName("a weapon with a brand is still priced from its dice alone")
        void weaponWithBrand() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD, 2, 5, 0);
            sword.getBrands().add(brand("FIRE_2", 120));

            assertEquals(15, priceAlone(sword, "damageDicePower"));
        }

        /**
         * A ring has no dice to price, even when some are recorded, and with nothing that improves
         * the player's attacks it is worth nothing.
         */
        @Test
        @DisplayName("a non-weapon with nothing combat-related is priced at zero, dice or no dice")
        void plainRing() throws Exception {
            assertEquals(0, priceAlone(item(TValue.TV_RING, 2, 5, 0), "damageDicePower"));
        }

        /**
         * A non-weapon with a brand or a slay is credited with WEAP_DAMAGE * DAMAGE_POWER, which is
         * 12 * 5 = 60.
         */
        @Test
        @DisplayName("a non-weapon with a brand or a slay is credited with a flat 60")
        void ringWithBrandOrSlay() throws Exception {
            ItemObject branded = item(TValue.TV_RING);
            branded.getBrands().add(brand("FIRE_2", 120));
            ItemObject slaying = item(TValue.TV_RING);
            slaying.getSlays().add(slay("EVIL", 2, 120));

            assertEquals(60, priceAlone(branded, "damageDicePower"));
            assertEquals(60, priceAlone(slaying, "damageDicePower"));
        }

        /**
         * Blows, shots and might each earn the flat figure when above zero. The test is {@code > 0},
         * so zero and a negative both earn nothing.
         */
        @ParameterizedTest(name = "{0} at {1} is priced at {2}")
        @CsvSource({
                "OM_BLOWS, 1, 60", "OM_SHOTS, 1, 60", "OM_MIGHT, 1, 60",
                "OM_BLOWS, 0, 0", "OM_SHOTS, 0, 0", "OM_MIGHT, 0, 0",
                "OM_BLOWS, -1, 0", "OM_SHOTS, -1, 0", "OM_MIGHT, -1, 0"})
        @DisplayName("a non-weapon is credited for blows, shots or might above zero")
        void ringWithModifier(ObjectModifier modifier, int value, int expected) throws Exception {
            assertEquals(expected, priceAlone(with(item(TValue.TV_RING), modifier, value), "damageDicePower"));
        }

        /**
         * A launcher is sent to the shooting slot, so it takes neither branch and its term is zero
         * however much it carries.
         */
        @Test
        @DisplayName("a launcher is priced at zero even with brands and might")
        void launcherIsZero() throws Exception {
            ItemObject bow = with(item(TValue.TV_BOW, 2, 5, 0), ObjectModifier.OM_MIGHT, 1);
            bow.getBrands().add(brand("FIRE_2", 120));

            assertEquals(0, priceAlone(bow, "damageDicePower"));
        }

        /**
         * The curse overload has only the second branch: blows, shots or might above zero is 60 and
         * anything else is zero.
         */
        @ParameterizedTest(name = "curse {0} at {1} is priced at {2}")
        @CsvSource({
                "OM_BLOWS, 1, 60", "OM_SHOTS, 1, 60", "OM_MIGHT, 1, 60",
                "OM_BLOWS, 0, 0", "OM_BLOWS, -1, 0", "OM_STEALTH, 3, 0"})
        @DisplayName("the curse overload credits blows, shots or might above zero")
        void curseOverload(ObjectModifier modifier, int value, int expected) throws Exception {
            assertEquals(expected, priceAlone(item(TValue.TV_RING), "damageDicePower", curse(modifier, value, 0)));
        }

        /**
         * A curse with no modifiers at all earns nothing.
         */
        @Test
        @DisplayName("a curse with no modifiers is priced at zero")
        void curseWithNothing() throws Exception {
            assertEquals(0, priceAlone(item(TValue.TV_RING), "damageDicePower", curse(null, 0, 0)));
        }

        /**
         * As for {@code toDamagePower}: the live player is used, not the one captured at
         * construction.
         */
        @Test
        @DisplayName("an item built before a character exists is priced against the live player")
        void livePlayer() throws Exception {
            GameState.setPlayer(null);
            ItemObject ring = with(item(TValue.TV_RING), ObjectModifier.OM_BLOWS, 1);
            GameState.setPlayer(new Player());

            assertEquals(60, priceAlone(ring, "damageDicePower"));
        }
    }

    /**
     * C's {@code ammo_damage_power}: what a launcher's ammunition is assumed to average.
     */
    @Nested
    @DisplayName("ammoDamagePower")
    class AmmoDamage {

        /**
         * The increment is {@code ammo_dam * 5 / 2}: shots 10 * 5 / 2 = 25, arrows 12 * 5 / 2 = 30,
         * bolts 14 * 5 / 2 = 35.
         */
        @ParameterizedTest(name = "a launcher of {0} adds {1}")
        @CsvSource({"KF_SHOOTS_SHOTS, 25", "KF_SHOOTS_ARROWS, 30", "KF_SHOOTS_BOLTS, 35"})
        @DisplayName("a launcher adds half DAMAGE_POWER times the average ammunition damage")
        void perAmmunition(ObjectKindFlag flag, int expected) throws Exception {
            assertEquals(expected, price(launcher(flag), "ammoDamagePower", 100));
        }

        /**
         * The flags are tested in the order shots, arrows, bolts, and the first found wins.
         */
        @Test
        @DisplayName("shots are tested before arrows, and arrows before bolts")
        void order() throws Exception {
            assertEquals(25, price(launcher(ObjectKindFlag.KF_SHOOTS_SHOTS, ObjectKindFlag.KF_SHOOTS_ARROWS),
                    "ammoDamagePower", 100));
            assertEquals(30, price(launcher(ObjectKindFlag.KF_SHOOTS_ARROWS, ObjectKindFlag.KF_SHOOTS_BOLTS),
                    "ammoDamagePower", 100));
        }

        /**
         * A launcher whose kind fires nothing adds nothing.
         */
        @Test
        @DisplayName("a launcher that fires nothing adds zero")
        void firesNothing() throws Exception {
            assertEquals(0, price(launcher(), "ammoDamagePower", 100));
        }

        /**
         * Only what {@code wield_slot} sends to the shooting slot is priced: a sword whose kind
         * somehow carries the flag, and the ammunition itself, add nothing.
         */
        @ParameterizedTest(name = "{0} adds zero")
        @CsvSource({"TV_SWORD", "TV_ARROW", "TV_RING"})
        @DisplayName("an object that is not a launcher adds zero")
        void notALauncher(TValue tValue) throws Exception {
            ObjectKind kind = kindFiring(ObjectKindFlag.KF_SHOOTS_ARROWS);

            assertEquals(0, price(ItemFixture.item(tValue).kind(kind).build(), "ammoDamagePower", 100));
        }

        /**
         * An object with no kind answers zero, where C would dereference null.
         */
        @Test
        @DisplayName("an object with no kind adds zero")
        void noKind() throws Exception {
            assertEquals(0, price(ItemFixture.item(TValue.TV_BOW).kind(null).build(), "ammoDamagePower", 100));
        }

        /**
         * A curse is not worn in the shooting slot.
         */
        @Test
        @DisplayName("the curse overload adds zero")
        void curseOverload() throws Exception {
            assertEquals(0, price(launcher(ObjectKindFlag.KF_SHOOTS_ARROWS), "ammoDamagePower",
                    curse(null, 0, 0), 100));
        }
    }

    /**
     * C's {@code launcher_ammo_damage_power}: ammunition multiplied for its launcher and rescaled.
     */
    @Nested
    @DisplayName("launcherAmmoDamagePower")
    class LauncherAmmo {

        /**
         * The total is multiplied by launch_mult and divided by 2 * MAX_BLOWS = 10, truncating: shots
         * 100 * 4 / 10 = 40 and 103 * 4 / 10 = 41, arrows 100 * 5 / 10 = 50 and 103 * 5 / 10 = 51,
         * bolts 100 * 7 / 10 = 70 and 103 * 7 / 10 = 72, and -7 arrows is -35 / 10 = -3, not -4.
         */
        @ParameterizedTest(name = "{0} at {1} prices at {2}")
        @CsvSource({
                "TV_SHOT, 100, 40", "TV_SHOT, 103, 41",
                "TV_ARROW, 100, 50", "TV_ARROW, 103, 51", "TV_ARROW, -7, -3", "TV_ARROW, 0, 0",
                "TV_BOLT, 100, 70", "TV_BOLT, 103, 72"})
        @DisplayName("ammunition is multiplied for its launcher and divided by twice MAX_BLOWS")
        void multiplied(TValue tValue, int power, int expected) throws Exception {
            assertEquals(expected, price(item(tValue), "launcherAmmoDamagePower", power));
        }

        /**
         * Ego ammunition first takes {@code launch_dam * 5 / 2} = 9 * 5 / 2 = 22 for every type,
         * and the sum is then multiplied: shots 122 * 4 / 10 = 48, arrows 122 * 5 / 10 = 61, bolts
         * 122 * 7 / 10 = 85.
         */
        @ParameterizedTest(name = "ego {0} at 100 prices at {1}")
        @CsvSource({"TV_SHOT, 48", "TV_ARROW, 61", "TV_BOLT, 85"})
        @DisplayName("ego ammunition takes the launcher's assumed bonus first")
        void egoAmmunition(TValue tValue, int expected) throws Exception {
            assertEquals(expected, price(ItemFixture.item(tValue).ego(ego()).build(), "launcherAmmoDamagePower", 100));
        }

        /**
         * Only ammunition is touched: a launcher, a weapon and a ring come back as they went in,
         * and an ego on a sword does not make it ammunition.
         */
        @ParameterizedTest(name = "{0} is unchanged")
        @CsvSource({"TV_BOW", "TV_SWORD", "TV_RING"})
        @DisplayName("an object that is not ammunition is unchanged")
        void notAmmunition(TValue tValue) throws Exception {
            assertEquals(103, price(ItemFixture.item(tValue).ego(ego()).build(), "launcherAmmoDamagePower", 103));
        }

        /**
         * A curse is not ammunition.
         */
        @Test
        @DisplayName("the curse overload is unchanged")
        void curseOverload() throws Exception {
            assertEquals(103, price(item(TValue.TV_ARROW), "launcherAmmoDamagePower", curse(null, 0, 0), 103));
        }
    }

    /**
     * C's {@code bow_multiplier}: a bow's pval, and one for anything else.
     */
    @Nested
    @DisplayName("bowMulitplier")
    class BowMultiplier {

        /**
         * A bow's multiplier is its pval, including zero, which C returns as it is.
         */
        @ParameterizedTest(name = "a bow with pval {0} multiplies by {0}")
        @CsvSource({"2", "3", "5", "0"})
        @DisplayName("a bow's multiplier is its pval")
        void bow(int pValue) throws Exception {
            ItemObject bow = item(TValue.TV_BOW);
            set(bow, "pValue", pValue);

            assertEquals(pValue, priceAlone(bow, "bowMulitplier"));
        }

        /**
         * Anything that is not a bow multiplies by one, whatever its pval says.
         */
        @ParameterizedTest(name = "{0} with pval 5 multiplies by 1")
        @CsvSource({"TV_SWORD", "TV_ARROW", "TV_WAND", "TV_RING"})
        @DisplayName("anything that is not a bow multiplies by one")
        void notABow(TValue tValue) throws Exception {
            ItemObject other = item(tValue);
            set(other, "pValue", 5);

            assertEquals(1, priceAlone(other, "bowMulitplier"));
        }

        /**
         * A curse is not a bow.
         */
        @Test
        @DisplayName("the curse overload is one")
        void curseOverload() throws Exception {
            assertEquals(1, priceAlone(item(TValue.TV_BOW), "bowMulitplier", curse(null, 0, 0)));
        }
    }

    /**
     * C's {@code extra_blows_power}: scaled by the blows relative to MAX_BLOWS, plus a flat boost.
     */
    @Nested
    @DisplayName("extraBlowsPower")
    class ExtraBlows {

        /**
         * No blows modifier leaves the total alone.
         */
        @Test
        @DisplayName("no extra blows leaves the total unchanged")
        void none() throws Exception {
            assertEquals(100, price(item(TValue.TV_SWORD), "extraBlowsPower", 100));
        }

        /**
         * The total is {@code p * (5 + b) / 5} and then {@code 15 * b * 5 / 2} is added, each
         * division truncating toward zero:
         * <ul>
         * <li>1 blow at 100: 100 * 6 / 5 = 120, plus 75 / 2 = 37, is 157.</li>
         * <li>2 blows at 100: 100 * 7 / 5 = 140, plus 150 / 2 = 75, is 215.</li>
         * <li>1 blow at 103: 103 * 6 / 5 = 618 / 5 = 123, plus 37, is 160.</li>
         * <li>-1 blow at 100: 100 * 4 / 5 = 80, plus -75 / 2 = -37 (not -38), is 43.</li>
         * </ul>
         */
        @ParameterizedTest(name = "{1} blows at {0} prices at {2}")
        @CsvSource({"100, 1, 157", "100, 2, 215", "103, 1, 160", "100, -1, 43"})
        @DisplayName("the total is scaled by the blows and a flat boost added")
        void scaled(int power, int blows, int expected) throws Exception {
            assertEquals(expected, price(with(item(TValue.TV_SWORD), ObjectModifier.OM_BLOWS, blows),
                    "extraBlowsPower", power));
        }

        /**
         * INHIBIT_BLOWS is 3: 2 is priced and 3 is refused, adding INHIBIT_POWER of 20000 to the
         * total as it stood and not scaling it.
         */
        @ParameterizedTest(name = "{0} blows prices at {1}")
        @CsvSource({"2, 215", "3, 20100", "4, 20100"})
        @DisplayName("blows at three or more are refused")
        void inhibit(int blows, int expected) throws Exception {
            assertEquals(expected, price(with(item(TValue.TV_SWORD), ObjectModifier.OM_BLOWS, blows),
                    "extraBlowsPower", 100));
        }

        /**
         * The curse overload prices a curse's own modifier by the same figures.
         */
        @ParameterizedTest(name = "curse with {0} blows prices at {1}")
        @CsvSource({"1, 157", "2, 215", "-1, 43", "3, 20100", "0, 100"})
        @DisplayName("the curse overload prices the curse's blows")
        void curseOverload(int blows, int expected) throws Exception {
            assertEquals(expected, price(item(TValue.TV_SWORD), "extraBlowsPower",
                    curse(ObjectModifier.OM_BLOWS, blows, 0), 100));
        }

        /**
         * A curse with no modifiers has no blows and leaves the total alone.
         */
        @Test
        @DisplayName("a curse with no modifiers leaves the total unchanged")
        void curseWithNothing() throws Exception {
            assertEquals(100, price(item(TValue.TV_SWORD), "extraBlowsPower", curse(null, 0, 0), 100));
        }
    }

    /**
     * C's {@code extra_shots_power}: each point raises the total by a tenth of itself.
     */
    @Nested
    @DisplayName("extraShotsPower")
    class ExtraShots {

        /**
         * The total is {@code p * (10 + s) / 10}, truncating: 1 shot at 100 is 1100 / 10 = 110, 5 is
         * 150, 10 is 200, and 1 shot at 105 is 1155 / 10 = 115.
         */
        @ParameterizedTest(name = "{1} shots at {0} prices at {2}")
        @CsvSource({"100, 1, 110", "100, 5, 150", "100, 10, 200", "105, 1, 115"})
        @DisplayName("each point of shots raises the total by a tenth")
        void scaled(int power, int shots, int expected) throws Exception {
            assertEquals(expected, price(with(item(TValue.TV_BOW), ObjectModifier.OM_SHOTS, shots),
                    "extraShotsPower", power));
        }

        /**
         * No shots leaves the total alone, and so does a negative figure, which C says it cannot
         * handle: neither the refusal nor the scaling applies.
         */
        @ParameterizedTest(name = "{0} shots leaves 100 as it is")
        @CsvSource({"0", "-1", "-5"})
        @DisplayName("no shots or negative shots leaves the total unchanged")
        void noneOrNegative(int shots) throws Exception {
            assertEquals(100, price(with(item(TValue.TV_BOW), ObjectModifier.OM_SHOTS, shots),
                    "extraShotsPower", 100));
        }

        /**
         * INHIBIT_SHOTS is 21: 20 is priced, at 100 * 30 / 10 = 300, and 21 is refused.
         */
        @ParameterizedTest(name = "{0} shots prices at {1}")
        @CsvSource({"20, 300", "21, 20100", "22, 20100"})
        @DisplayName("shots at twenty-one or more are refused")
        void inhibit(int shots, int expected) throws Exception {
            assertEquals(expected, price(with(item(TValue.TV_BOW), ObjectModifier.OM_SHOTS, shots),
                    "extraShotsPower", 100));
        }

        /**
         * The curse overload prices a curse's own modifier by the same figures.
         */
        @ParameterizedTest(name = "curse with {0} shots prices at {1}")
        @CsvSource({"1, 110", "10, 200", "20, 300", "21, 20100", "0, 100", "-1, 100"})
        @DisplayName("the curse overload prices the curse's shots")
        void curseOverload(int shots, int expected) throws Exception {
            assertEquals(expected, price(item(TValue.TV_BOW), "extraShotsPower",
                    curse(ObjectModifier.OM_SHOTS, shots, 0), 100));
        }

        /**
         * A curse with no modifiers has no shots and leaves the total alone.
         */
        @Test
        @DisplayName("a curse with no modifiers leaves the total unchanged")
        void curseWithNothing() throws Exception {
            assertEquals(100, price(item(TValue.TV_BOW), "extraShotsPower", curse(null, 0, 0), 100));
        }
    }

    /**
     * C's {@code extra_might_power}: the multiplier rises by the might and the whole total is
     * multiplied by it.
     */
    @Nested
    @DisplayName("extraMightPower")
    class ExtraMight {

        private Object pair(int power, int mult) throws Exception {
            Constructor<?> constructor = Class.forName("uk.co.jackoftradesltd.middle.objects.ItemObject$PowerAndMult")
                    .getDeclaredConstructor(int.class, int.class);
            constructor.setAccessible(true);
            return constructor.newInstance(power, mult);
        }

        private int read(Object pair, String accessor) throws Exception {
            Method method = pair.getClass().getDeclaredMethod(accessor);
            method.setAccessible(true);
            return (int) method.invoke(pair);
        }

        private Object price(ItemObject item, int might, int power, int mult) throws Exception {
            Class<?> type = Class.forName("uk.co.jackoftradesltd.middle.objects.ItemObject$PowerAndMult");
            return invoke(with(item, ObjectModifier.OM_MIGHT, might), "extraMightPower",
                    new Class<?>[]{type}, pair(power, mult));
        }

        private Object priceCurse(int might, int power, int mult) throws Exception {
            Class<?> type = Class.forName("uk.co.jackoftradesltd.middle.objects.ItemObject$PowerAndMult");
            return invoke(item(TValue.TV_BOW), "extraMightPower", new Class<?>[]{Curse.class, type},
                    curse(ObjectModifier.OM_MIGHT, might, 0), pair(power, mult));
        }

        /**
         * The multiplier becomes {@code mult + might} and the total is multiplied by it. With no
         * might the total is still multiplied by the launcher's own figure:
         * <ul>
         * <li>might 0, mult 1: 100 * 1 = 100.</li>
         * <li>might 0, mult 2: 100 * 2 = 200.</li>
         * <li>might 1, mult 2: mult 3, 100 * 3 = 300.</li>
         * <li>might 2, mult 3: mult 5, 100 * 5 = 500.</li>
         * <li>might 3, mult 1: mult 4, 100 * 4 = 400, the largest not refused.</li>
         * <li>might -1, mult 2: mult 1, 100 * 1 = 100.</li>
         * </ul>
         */
        @ParameterizedTest(name = "might {0} on mult {1} gives power {2} and mult {3}")
        @CsvSource({"0, 1, 100, 1", "0, 2, 200, 2", "1, 2, 300, 3", "2, 3, 500, 5", "3, 1, 400, 4", "-1, 2, 100, 1"})
        @DisplayName("the multiplier rises by the might and multiplies the total")
        void multiplies(int might, int mult, int expectedPower, int expectedMult) throws Exception {
            Object result = price(item(TValue.TV_BOW), might, 100, mult);

            assertEquals(expectedPower, read(result, "power"));
            assertEquals(expectedMult, read(result, "mult"));
        }

        /**
         * INHIBIT_MIGHT is 4: 3 is priced and 4 is refused, adding 20000 to the total as it stood and
         * leaving the multiplier as it came in.
         */
        @ParameterizedTest(name = "might {0} gives power {1}")
        @CsvSource({"4, 20100", "5, 20100"})
        @DisplayName("might at four or more is refused and the multiplier is untouched")
        void inhibit(int might, int expectedPower) throws Exception {
            Object result = price(item(TValue.TV_BOW), might, 100, 2);

            assertEquals(expectedPower, read(result, "power"));
            assertEquals(2, read(result, "mult"));
        }

        /**
         * The curse overload gives the same answers from the curse's own modifier.
         */
        @ParameterizedTest(name = "curse might {0} on mult {1} gives power {2} and mult {3}")
        @CsvSource({"0, 1, 100, 1", "1, 2, 300, 3", "3, 1, 400, 4", "4, 2, 20100, 2"})
        @DisplayName("the curse overload prices the curse's might")
        void curseOverload(int might, int mult, int expectedPower, int expectedMult) throws Exception {
            Object result = priceCurse(might, 100, mult);

            assertEquals(expectedPower, read(result, "power"));
            assertEquals(expectedMult, read(result, "mult"));
        }
    }

    /**
     * C's {@code slay_power}: the best brand or slay, scaled by the damage dice squared, plus the
     * bonuses for carrying several.
     */
    @Nested
    @DisplayName("slayPower")
    class SlayPower {

        private int price(ItemObject item, int power, boolean verbose, int dicePower) throws Exception {
            return (int) invoke(item, "slayPower", new Class<?>[]{int.class, boolean.class, int.class},
                    power, verbose, dicePower);
        }

        /**
         * With no brand or slay C returns the total at once, whatever the dice.
         */
        @Test
        @DisplayName("nothing to price returns the total unchanged")
        void nothing() throws Exception {
            assertEquals(100, price(item(TValue.TV_SWORD), 100, false, 15));
        }

        /**
         * One brand of power 120 on dice of 15: 15 * 15 * (120 - 100) / 2500 = 4500 / 2500 = 1. The
         * verbose path logs more and returns the same.
         */
        @Test
        @DisplayName("one brand is scaled by the dice squared and verbose changes nothing")
        void oneBrand() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getBrands().add(brand("FIRE_2", 120));

            assertEquals(101, price(sword, 100, false, 15));
            assertEquals(101, price(sword, 100, true, 15));
        }

        /**
         * The same brand is worth more on heavier dice: 60 * 60 * 30 / 2500 = 108000 / 2500 = 43.
         */
        @Test
        @DisplayName("the same brand is worth more on a larger dice term")
        void largerDice() throws Exception {
            ItemObject ring = item(TValue.TV_RING);
            ring.getBrands().add(brand("FIRE_2", 130));

            assertEquals(143, price(ring, 100, false, 60));
        }

        /**
         * The best of several is used, not the sum: a slay of 120 beside a brand of 110 on dice of
         * 30 takes 120, so 900 * 20 / 2500 = 7, not the figure for 110 and 120 together. The pair
         * bonus adds 1 * 1 * 30 / 25 = 1, so 8 in all.
         */
        @Test
        @DisplayName("the best brand or slay is used, not the sum")
        void bestOfSeveral() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getBrands().add(brand("FIRE_2", 110));
            sword.getSlays().add(slay("EVIL", 2, 120));

            // best 120: 900 * 20 / 2500 = 7, plus slay-and-brand 1 * 1 * 30 / 25 = 1
            assertEquals(108, price(sword, 100, false, 30));
        }

        /**
         * A brand below 100 is a penalty, and the division truncates toward zero: power 80 on dice of
         * 15 is 225 * -20 / 2500 = -4500 / 2500 = -1, not -2.
         */
        @Test
        @DisplayName("a weak brand prices negatively, truncating toward zero")
        void weakBrand() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getBrands().add(brand("FIRE_2", 80));

            assertEquals(99, price(sword, 100, false, 15));
        }

        /**
         * The search for the best starts from 1, not from 0, as in C. A brand of power 0 therefore
         * counts as 1: 225 * (1 - 100) / 2500 = -22275 / 2500 = -8. Starting from 0 would give
         * -22500 / 2500 = -9.
         */
        @Test
        @DisplayName("the best figure starts at one, so a worthless brand costs 99 and not 100")
        void floorOfOne() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getBrands().add(brand("FIRE_2", 0));

            assertEquals(92, price(sword, 100, false, 15));
        }

        /**
         * Two slays of 110 and 120 on dice of 30: 900 * 20 / 2500 = 7, and for multiple slays
         * 2 * 2 * 30 / 25 = 120 / 25 = 4, so 11 in all.
         */
        @Test
        @DisplayName("several slays earn a bonus")
        void multipleSlays() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getSlays().add(slay("EVIL", 2, 110));
            sword.getSlays().add(slay("ORC", 2, 120));

            assertEquals(111, price(sword, 100, false, 30));
        }

        /**
         * Two brands of 110 and 130 on dice of 30: 900 * 30 / 2500 = 10, and for multiple brands
         * 2 * 2 * 2 * 30 / 25 = 240 / 25 = 9, so 19 in all.
         */
        @Test
        @DisplayName("several brands earn a bonus")
        void multipleBrands() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getBrands().add(brand("FIRE_2", 110));
            sword.getBrands().add(brand("COLD_2", 130));

            assertEquals(119, price(sword, 100, false, 30));
        }

        /**
         * One slay of 120 and one brand of 110 on dice of 30: 900 * 20 / 2500 = 7, and for the pair
         * 1 * 1 * 30 / 25 = 1, so 8 in all. Neither multiple bonus applies to one of each.
         */
        @Test
        @DisplayName("one slay and one brand earn the pair bonus only")
        void slayAndBrand() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getSlays().add(slay("EVIL", 2, 120));
            sword.getBrands().add(brand("FIRE_2", 110));

            assertEquals(108, price(sword, 100, false, 30));
        }

        /**
         * Two kills of 150 and 160 on dice of 30: 900 * 60 / 2500 = 21, and for multiple kills
         * 3 * 2 * 2 * 30 / 25 = 360 / 25 = 14, so 35 in all. They are not counted as slays.
         */
        @Test
        @DisplayName("several kills earn their own bonus")
        void multipleKills() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getSlays().add(slay("EVIL", 5, 150));
            sword.getSlays().add(slay("UNDEAD", 5, 160));

            assertEquals(135, price(sword, 100, false, 30));
        }

        /**
         * A kill and a slay of 140 and 120 on dice of 30: 900 * 40 / 2500 = 14, and neither has a
         * partner of its own kind, so there is no multiple bonus.
         */
        @Test
        @DisplayName("one kill and one slay earn no multiple bonus")
        void killAndSlay() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getSlays().add(slay("EVIL", 5, 140));
            sword.getSlays().add(slay("UNDEAD", 2, 120));

            assertEquals(114, price(sword, 100, false, 30));
        }

        /**
         * A multiplier of 3 is a slay and 4 is a kill. Two of power 100 earn nothing for the best
         * (900 * 0 / 2500), so what is left is the multiple bonus: as slays 2 * 2 * 30 / 25 = 4, as
         * kills 3 * 2 * 2 * 30 / 25 = 14.
         */
        @ParameterizedTest(name = "two slays of multiplier {0} earn {1}")
        @CsvSource({"3, 4", "4, 14"})
        @DisplayName("a multiplier of three is a slay and of four is a kill")
        void killBoundary(int mult, int expectedBonus) throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getSlays().add(slay("EVIL", mult, 100));
            sword.getSlays().add(slay("ORC", mult, 100));

            assertEquals(100 + expectedBonus, price(sword, 100, false, 30));
        }

        /**
         * A full set of slays is eight: 8 * 8 * 30 / 25 = 1920 / 25 = 76, plus a flat 10, is 86. Seven
         * is 7 * 7 * 30 / 25 = 1470 / 25 = 58 and no flat bonus.
         */
        @ParameterizedTest(name = "{0} slays earn {1}")
        @CsvSource({"7, 58", "8, 86"})
        @DisplayName("a full set of eight slays earns a flat 10")
        void fullSlays(int count, int expectedBonus) throws Exception {
            String[] monsters = {"EVIL", "UNDEAD", "ANIMAL", "ORC", "TROLL", "GIANT", "DEMON", "DRAGON"};
            ItemObject sword = item(TValue.TV_SWORD);
            for (int i = 0; i < count; i++) {
                sword.getSlays().add(slay(monsters[i], 2, 100));
            }

            assertEquals(100 + expectedBonus, price(sword, 100, false, 30));
        }

        /**
         * A full set of brands is five: 2 * 5 * 5 * 30 / 25 = 1500 / 25 = 60, plus a flat 20, is 80.
         * Four is 2 * 4 * 4 * 30 / 25 = 960 / 25 = 38 and no flat bonus.
         */
        @ParameterizedTest(name = "{0} brands earn {1}")
        @CsvSource({"4, 38", "5, 80"})
        @DisplayName("a full set of five brands earns a flat 20")
        void fullBrands(int count, int expectedBonus) throws Exception {
            String[] codes = {"ACID_2", "ELEC_2", "FIRE_2", "COLD_2", "POIS_2"};
            ItemObject sword = item(TValue.TV_SWORD);
            for (int i = 0; i < count; i++) {
                sword.getBrands().add(brand(codes[i], 100));
            }

            assertEquals(100 + expectedBonus, price(sword, 100, false, 30));
        }

        /**
         * A full set of kills is three: 3 * 3 * 3 * 30 / 25 = 810 / 25 = 32, plus a flat 20, is 52.
         * Two is 3 * 2 * 2 * 30 / 25 = 14 and no flat bonus.
         */
        @ParameterizedTest(name = "{0} kills earn {1}")
        @CsvSource({"2, 14", "3, 52"})
        @DisplayName("a full set of three kills earns a flat 20")
        void fullKills(int count, int expectedBonus) throws Exception {
            String[] monsters = {"EVIL", "UNDEAD", "DEMON"};
            ItemObject sword = item(TValue.TV_SWORD);
            for (int i = 0; i < count; i++) {
                sword.getSlays().add(slay(monsters[i], 5, 100));
            }

            assertEquals(100 + expectedBonus, price(sword, 100, false, 30));
        }

        /**
         * With no dice term, nothing the brand is scaled by is left: a brand of 150 on dice of 0 adds
         * 0 * 0 * 50 / 2500 = 0, and two of them add 0 for the multiple bonus as well.
         */
        @Test
        @DisplayName("a dice term of zero leaves the total unchanged")
        void zeroDice() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD);
            sword.getBrands().add(brand("FIRE_2", 150));
            sword.getBrands().add(brand("COLD_2", 150));

            assertEquals(100, price(sword, 100, false, 0));
        }

        /**
         * A curse carries no brands or slays, so the curse overload returns its input.
         */
        @Test
        @DisplayName("the curse overload is unchanged")
        void curseOverload() throws Exception {
            assertEquals(103, (int) invoke(item(TValue.TV_SWORD), "slayPower",
                    new Class<?>[]{Curse.class, int.class, boolean.class, int.class},
                    curse(null, 0, 0), 103, true, 30));
        }
    }
}
