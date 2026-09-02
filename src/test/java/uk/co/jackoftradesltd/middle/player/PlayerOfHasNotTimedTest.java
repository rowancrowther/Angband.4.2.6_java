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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerUtils#playerOfHasNotTimed(Player, ObjectFlag)}, the port of C's
 * {@code player_of_has_not_timed}
 * ({@code player-timed.c:747}).
 *
 * <p>The C is five clauses: wipe a collecting set by filling it from {@code player_flags}, walk
 * every body slot, skip the empty ones, {@code object_flags} each worn item into a scratch set and
 * {@code of_union} that into the collector, then answer {@code of_has}. The expected values below
 * are read off those clauses.
 *
 * <p><b>What the function is for is what makes it worth testing.</b> Its sibling
 * {@code player_of_has} reads {@code p->state.flags}, and that set is not innocent: {@code
 * calc_bonuses} finishes by folding in the object-flag duplicate of every running timed effect
 * ({@code player_flags_timed}, {@code player-calcs.c:2135}). So the state answers "yes" for a
 * player who is merely temporarily heroic. This function exists to give the other answer — what the
 * player has permanently — and the case that pins it is a state carrying a flag that neither the
 * race, the class nor the equipment grants: the answer must be {@code false}.
 *
 * <p>The other cases are the union itself (innate and worn flags both count, and flags from
 * different items add rather than replace) and the slot walk (an empty slot is skipped, and a slot
 * late in the body is reached — a loop that stopped at the first empty slot would pass a test that
 * only ever equipped the weapon).
 *
 * <p>The method is called directly rather than through its one caller, {@code setTimed}, which has
 * conditions of its own and would not isolate this. The player's race, class, body and state are
 * still installed by reflection, birth being the only thing that would otherwise fill them.
 *
 * <p>Class PlayerOfHasNotTimedTest coded on 260829, commented in full on 260829, reworked on 260901
 * for the move of the method to {@link PlayerUtils}.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerOfHasNotTimedTest {

    /**
     * The player under test, fresh for each test.
     */
    private Player player;

    /**
     * The calculated state the method hands to {@code playerFlags}, and whose own object flags it
     * must ignore.
     */
    private PlayerState state;

    /**
     * The body whose slots the method walks; a full humanoid layout, every slot empty until a test
     * fills one.
     */
    private PlayerBody body;

    /**
     * @param on the flags to switch on
     * @return a set holding exactly those flags
     */
    private static Flag<ObjectFlag> flagsOf(ObjectFlag... on) {
        Flag<ObjectFlag> set = new Flag<>(ObjectFlag.class);
        for (ObjectFlag flag : on) set.on(flag);
        return set;
    }

    /**
     * Writes a private field, there being no setter for any of the fields this test needs.
     *
     * @param target    the object to write to
     * @param type      the class declaring the field
     * @param fieldName the field's name
     * @param value     the value to write
     * @throws Exception if the field cannot be reached
     */
    private static void setField(Object target, Class<?> type, String fieldName, Object value)
            throws Exception {
        Field field = type.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    /**
     * Builds a race carrying the given object flags and contributing nothing else.
     *
     * @param oFlags the race's innate object flags
     * @return the race
     */
    private static PlayerRace race(Flag<ObjectFlag> oFlags) {
        Map<Stats, Integer> stats = Map.of();
        Map<PlayerSkill, Integer> skills = Map.of();
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, null,
                stats, skills, oFlags, new Flag<>(PlayerFlag.class), null, Map.of());
    }

    /**
     * Builds a class carrying the given object flags and contributing nothing else. The class's own
     * player flags are left empty deliberately: {@code playerFlags} reads
     * {@link PlayerFlag#PF_BRAVERY_30} from the state, never from the class.
     *
     * @param oFlags the class's innate object flags
     * @return the class
     */
    private static PlayerClass playerClass(Flag<ObjectFlag> oFlags) {
        return new PlayerClass("Test Class", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                oFlags, new Flag<>(PlayerFlag.class), 0, 0, 0, List.of(), null);
    }

    /**
     * Builds an item carrying the given flags and nothing else.
     *
     * @param flags the flag set the item should hold
     * @return the item
     */
    private static ItemObject itemWithFlags(Flag<ObjectFlag> flags) {
        return new ItemObject(new ObjectKind(), null, null, null, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                flags, Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    /**
     * Puts an item into the named body slot, {@link EquipSlot} having no setter for its item.
     *
     * @param slotName the slot's display name
     * @param item     the item to wear
     * @throws Exception if the field cannot be reached
     */
    private void wear(String slotName, ItemObject item) throws Exception {
        for (EquipSlot slot : body.getSlots()) {
            if (slot.getName().equals(slotName)) {
                setField(slot, EquipSlot.class, "item", item);
                return;
            }
        }
        throw new IllegalArgumentException("no slot named " + slotName);
    }

    /**
     * A player with an empty state, an empty race and class, and an empty humanoid body — so that
     * any flag the method finds is one the test put there.
     *
     * @throws Exception if a field or method cannot be reached
     */
    @BeforeEach
    void newPlayer() throws Exception {
        player = new Player();
        state = new PlayerState();
        body = SeededPlayerRegistry.humanoidBody();
        setField(player, Player.class, "state", state);
        setField(player, Player.class, "body", body);
        setField(player, Player.class, "race", race(flagsOf()));
        setField(player, Player.class, "playerClass", playerClass(flagsOf()));
        setField(player, Player.class, "level", 1);
    }

    /**
     * Calls the method under test.
     *
     * @param flag the flag to ask about
     * @return what the method returned
     */
    private boolean has(ObjectFlag flag) {
        return PlayerUtils.playerOfHasNotTimed(player, flag);
    }

    /**
     * Nothing innate and nothing worn: every slot is empty and the collector stays as
     * {@code player_flags} left it.
     */
    @Test
    @DisplayName("a bare player with empty slots has nothing")
    void barePlayerHasNothing() throws Exception {
        assertFalse(has(ObjectFlag.OF_FREE_ACT));
        assertFalse(has(ObjectFlag.OF_PROT_FEAR));
        assertFalse(has(ObjectFlag.OF_SEE_INVIS));
    }

    /**
     * The race's flags reach the collector, C's {@code player_flags} opening with a copy of them.
     */
    @Test
    @DisplayName("a racial flag counts")
    void racialFlagCounts() throws Exception {
        setField(player, Player.class, "race", race(flagsOf(ObjectFlag.OF_FREE_ACT)));
        assertTrue(has(ObjectFlag.OF_FREE_ACT));
        assertFalse(has(ObjectFlag.OF_SEE_INVIS));
    }

    /**
     * The class's flags are unioned in on top of the race's.
     */
    @Test
    @DisplayName("a class flag counts, alongside the race's")
    void classFlagCounts() throws Exception {
        setField(player, Player.class, "race", race(flagsOf(ObjectFlag.OF_FREE_ACT)));
        setField(player, Player.class, "playerClass", playerClass(flagsOf(ObjectFlag.OF_HOLD_LIFE)));
        assertTrue(has(ObjectFlag.OF_FREE_ACT));
        assertTrue(has(ObjectFlag.OF_HOLD_LIFE));
    }

    /**
     * {@code player_flags}' third clause: a class with {@code PF_BRAVERY_30} is granted
     * {@code OF_PROT_FEAR}, but only from level 30. The level below the boundary is the case that
     * separates the conjunction from either half of it.
     */
    @Test
    @DisplayName("bravery grants prot-fear at level 30 and not at 29")
    void braveryIsLevelGated() throws Exception {
        state.playerFlagOn(PlayerFlag.PF_BRAVERY_30);

        setField(player, Player.class, "level", 29);
        assertFalse(has(ObjectFlag.OF_PROT_FEAR), "level 29 is below the grant");

        setField(player, Player.class, "level", 30);
        assertTrue(has(ObjectFlag.OF_PROT_FEAR), "level 30 is the grant");
    }

    /**
     * A flag from a worn item counts — the loop's whole purpose.
     */
    @Test
    @DisplayName("a flag on a worn item counts")
    void wornItemFlagCounts() throws Exception {
        wear("weapon", itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS)));
        assertTrue(has(ObjectFlag.OF_SEE_INVIS));
        assertFalse(has(ObjectFlag.OF_FREE_ACT));
    }

    /**
     * The walk covers the whole body, not just its opening slots: the item here sits in the last
     * slot of the humanoid layout, with every earlier slot empty.
     */
    @Test
    @DisplayName("a late slot is reached, and empty slots before it are skipped")
    void lateSlotIsReached() throws Exception {
        wear("feet", itemWithFlags(flagsOf(ObjectFlag.OF_FEATHER)));
        assertTrue(has(ObjectFlag.OF_FEATHER));
    }

    /**
     * {@code of_union} accumulates: two items each keep their contribution, even though
     * {@code object_flags} wipes the scratch set between them.
     */
    @Test
    @DisplayName("flags from different items add rather than replace")
    void flagsFromSeveralItemsAccumulate() throws Exception {
        wear("weapon", itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS)));
        wear("head", itemWithFlags(flagsOf(ObjectFlag.OF_HOLD_LIFE)));
        wear("body", itemWithFlags(flagsOf()));

        assertTrue(has(ObjectFlag.OF_SEE_INVIS), "the weapon's flag survives the later items");
        assertTrue(has(ObjectFlag.OF_HOLD_LIFE), "the helmet's flag is added");
        assertFalse(has(ObjectFlag.OF_FREE_ACT), "nothing grants free action");
    }

    /**
     * Innate and worn flags are both in the same collector.
     */
    @Test
    @DisplayName("innate and worn flags are collected together")
    void innateAndWornCombine() throws Exception {
        setField(player, Player.class, "race", race(flagsOf(ObjectFlag.OF_FREE_ACT)));
        wear("neck", itemWithFlags(flagsOf(ObjectFlag.OF_SEE_INVIS)));

        assertTrue(has(ObjectFlag.OF_FREE_ACT));
        assertTrue(has(ObjectFlag.OF_SEE_INVIS));
    }

    /**
     * The case the function exists for. The calculated state's own flag set carries the object-flag
     * duplicates of running timed effects, so a temporarily heroic player has {@code OF_PROT_FEAR}
     * there. This function never reads that set — it rebuilds from race, class and equipment — so
     * the answer is {@code false}.
     */
    @Test
    @DisplayName("a flag present only in the calculated state, as a timed effect leaves it, does not count")
    void timedFlagInStateDoesNotCount() throws Exception {
        state.getObjectFlag().on(ObjectFlag.OF_PROT_FEAR);
        assertTrue(state.hasOFlag(ObjectFlag.OF_PROT_FEAR), "the state carries the flag");

        assertFalse(has(ObjectFlag.OF_PROT_FEAR), "but nothing permanent grants it");
    }

    /**
     * A running timed effect on the player is likewise invisible here: the function looks at slots
     * and innate flags, never at {@code p->timed}.
     */
    @Test
    @DisplayName("a running timed effect grants nothing")
    void runningTimedEffectGrantsNothing() throws Exception {
        Field field = Player.class.getDeclaredField("timed");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<TimedEffect, Integer> timed = (Map<TimedEffect, Integer>) field.get(player);
        timed.put(TimedEffect.TMD_HERO, 20);

        assertFalse(has(ObjectFlag.OF_PROT_FEAR));
    }
}
