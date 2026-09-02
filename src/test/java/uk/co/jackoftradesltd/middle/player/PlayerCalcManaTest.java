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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#calcMana}, the port of C's {@code calc_mana} ({@code player-calcs.c}).
 *
 * <p>Three things decide a caster's mana, and each has an edge the tests here pin. A class that
 * knows no spells has none at all, and is answered before anything else is worked out. A caster
 * below the level at which their class gains its first spell has none either — the level term goes
 * to zero rather than negative. And armour beyond the class's allowance costs mana a point at a time,
 * with the weapon, launcher, rings, amulet and light exempt from the weighing, because a spellcaster
 * is hampered by what they wear rather than by what they carry.
 *
 * <p>The {@code update} flag is the other half: with it false the figure is computed and discarded,
 * which is how the bonus recalculation tries a change before committing to it.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcManaTest {

    /**
     * The class's spell-weight allowance, in tenth-pounds.
     */
    private static final int SPELL_WEIGHT = 300;

    /**
     * The level at which the test class gains its first spell.
     */
    private static final int FIRST_SPELL_LEVEL = 5;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The state the calculation fills.
     */
    private PlayerState state;

    /**
     * A class that knows the given number of spells, drawn from one realm scaled by intelligence.
     *
     * @param spells how many spells its single book holds
     * @return the class
     */
    private static PlayerClass casterClass(int spells) {
        MagicRealm arcane = new MagicRealm("arcane", Stats.STAT_INT, "cast", "spell",
                TValue.TV_MAGIC_BOOK);
        MagicBook book = new MagicBook(TValue.TV_MAGIC_BOOK, "Magic for Beginners", false,
                spells, arcane, null, 0, 0, 0, 0, List.of());

        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<PlayerSkill, Integer> extra = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            skills.put(skill, 0);
            extra.put(skill, 0);
        }

        return new PlayerClass("Test Caster", List.of(), stats, skills, extra, 0, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                5, 30, 5, List.of(),
                new ClassMagic(FIRST_SPELL_LEVEL, SPELL_WEIGHT, 1, List.of(book)));
    }

    /**
     * A caster at level 10 with a neutral intelligence and an empty body.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void newCaster() throws Exception {
        player = new Player();
        state = new PlayerState();

        set("playerClass", casterClass(1));
        set("level", 10);
        set("body", new PlayerBody("Humanoid", new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, "body"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon")))));

        Map<Stats, Integer> indices = new HashMap<>();
        for (Stats stat : Stats.values()) {
            indices.put(stat, 10);
        }
        Field field = PlayerState.class.getDeclaredField("statInd");
        field.setAccessible(true);
        field.set(state, indices);
    }

    /**
     * Writes one of the player's private fields.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private void set(String name, Object value) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * Reads one of the player's private int fields.
     *
     * @param name the field's name
     * @return its value
     * @throws Exception if the field cannot be reached
     */
    private int intField(String name) throws Exception {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * Puts a piece of body armour of the given weight on the player.
     *
     * @param weight the armour's weight in tenth-pounds
     * @throws Exception if a field cannot be reached
     */
    private void wearBodyArmour(int weight) throws Exception {
        wear(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, weight);
    }

    /**
     * Puts a weapon of the given weight on the player.
     *
     * @param weight the weapon's weight in tenth-pounds
     * @throws Exception if a field cannot be reached
     */
    private void wearWeapon(int weight) throws Exception {
        wear(EquipmentSlotsEnum.EQUIP_WEAPON, weight);
    }

    /**
     * Puts an item of the given weight into the first slot of a given type.
     *
     * @param type   the slot type to fill
     * @param weight the item's weight in tenth-pounds
     * @throws Exception if a field cannot be reached
     */
    private void wear(EquipmentSlotsEnum type, int weight) throws Exception {
        uk.co.jackoftradesltd.middle.objects.ItemObject item =
                new uk.co.jackoftradesltd.middle.objects.ItemObject();
        Field weightField = uk.co.jackoftradesltd.middle.objects.ItemObject.class
                .getDeclaredField("weight");
        weightField.setAccessible(true);
        weightField.setInt(item, weight);

        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            if (slot.getType() != type) continue;

            Field itemField = EquipSlot.class.getDeclaredField("item");
            itemField.setAccessible(true);
            itemField.set(slot, item);
            return;
        }
    }

    /**
     * The two ways to have no mana at all.
     */
    @Nested
    @DisplayName("non-casters")
    class NonCasters {

        /**
         * A class that knows no spells has no mana, and is answered before the level or the armour
         * is looked at — the literacy test is the first thing the method does.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a class that knows no spells has no mana")
        void spelllessClassHasNoMana() throws Exception {
            set("playerClass", casterClass(0));
            set("maxSP", 30);

            PlayerCalcs.calcMana(player, state, true);

            assertEquals(0, intField("maxSP"));
            assertEquals(0, intField("curSp"));
        }

        /**
         * A caster below their class's first-spell level has none either: the level term goes to
         * zero rather than negative, so a mage at level 1 is not in mana debt.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a caster below the first-spell level has no mana")
        void tooLowLevelHasNoMana() throws Exception {
            set("level", FIRST_SPELL_LEVEL - 1);

            PlayerCalcs.calcMana(player, state, true);

            assertEquals(0, intField("maxSP"));
        }

        /**
         * At exactly the first-spell level the caster has some, so the boundary is inclusive.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("at the first-spell level the caster has mana")
        void firstSpellLevelHasMana() throws Exception {
            set("level", FIRST_SPELL_LEVEL);

            PlayerCalcs.calcMana(player, state, true);

            assertTrue(intField("maxSP") > 0, "the boundary level is inclusive");
        }
    }

    /**
     * The armour weighing, which is what makes plate mail a poor choice for a mage.
     */
    @Nested
    @DisplayName("armour encumbrance")
    class Encumbrance {

        /**
         * A caster wearing nothing is not encumbered.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("wearing nothing is not encumbering")
        void nothingWornIsNotEncumbering() throws Exception {
            PlayerCalcs.calcMana(player, state, true);

            assertFalse(state.isCumberArmour());
        }

        /**
         * Armour within the allowance is not encumbering either, so a mage may wear something.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("armour within the allowance is not encumbering")
        void lightArmourIsNotEncumbering() throws Exception {
            wearBodyArmour(SPELL_WEIGHT - 50);

            PlayerCalcs.calcMana(player, state, true);

            assertFalse(state.isCumberArmour());
        }

        /**
         * Beyond the allowance the caster is encumbered and loses mana — a point for every ten
         * tenth-pounds over.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("armour beyond the allowance costs mana")
        void heavyArmourCostsMana() throws Exception {
            PlayerCalcs.calcMana(player, state, true);
            int unencumbered = intField("maxSP");

            wearBodyArmour(SPELL_WEIGHT + 200);
            PlayerCalcs.calcMana(player, state, true);

            assertTrue(state.isCumberArmour());
            assertTrue(intField("maxSP") < unencumbered, "the armour cost mana");
        }

        /**
         * The weapon is exempt from the weighing, however heavy — a caster is hampered by what they
         * wear, not by what they wield. This is the exemption most easily lost in the slot loop.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the weapon is not weighed")
        void weaponIsExempt() throws Exception {
            wearWeapon(SPELL_WEIGHT * 3);

            PlayerCalcs.calcMana(player, state, true);

            assertFalse(state.isCumberArmour(), "a heavy weapon does not encumber a caster");
        }

        /**
         * Mana never falls below zero, however crushing the armour.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("mana never goes negative")
        void manaNeverGoesNegative() throws Exception {
            wearBodyArmour(SPELL_WEIGHT + 100000);

            PlayerCalcs.calcMana(player, state, true);

            assertEquals(0, intField("maxSP"));
        }
    }

    /**
     * The update flag, which decides whether the answer is kept.
     */
    @Nested
    @DisplayName("the update flag")
    class Updating {

        /**
         * With updates off the figure is computed and discarded, so the player's own maximum is
         * untouched — the recalculation tries a change before committing to it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("without updating, the player's mana is untouched")
        void withoutUpdatingNothingIsStored() throws Exception {
            set("maxSP", 99);

            PlayerCalcs.calcMana(player, state, false);

            assertEquals(99, intField("maxSP"));
        }

        /**
         * The encumbrance flag is written into the state either way, because it describes the
         * calculation rather than the player — the caller reads it from the state it passed in.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("the encumbrance flag is written even without updating")
        void encumbranceIsWrittenRegardless() throws Exception {
            wearBodyArmour(SPELL_WEIGHT + 200);

            PlayerCalcs.calcMana(player, state, false);

            assertTrue(state.isCumberArmour());
        }

        /**
         * A change to the maximum asks for the mana display to be repainted, so the player sees it.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("a changed maximum asks for a redraw")
        void changedMaximumAsksForRedraw() throws Exception {
            PlayerCalcs.calcMana(player, state, true);

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_MANA));
        }

        /**
         * Current mana is capped at the new maximum, so a caster whose maximum falls does not keep
         * more than they can hold.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("current mana is capped at the new maximum")
        void currentManaIsCapped() throws Exception {
            PlayerCalcs.calcMana(player, state, true);
            int maximum = intField("maxSP");
            set("curSp", maximum + 50);

            wearBodyArmour(SPELL_WEIGHT + 200);
            PlayerCalcs.calcMana(player, state, true);

            assertTrue(intField("curSp") <= intField("maxSP"));
        }
    }
}
