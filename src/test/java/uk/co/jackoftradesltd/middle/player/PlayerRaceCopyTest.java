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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerRace#copy()}, the method {@code PlayerBirth.playerGenerate} hands a player its
 * own race definition through ({@code PlayerBirth.java:428}).
 *
 * <p><b>There is no C function to compare against, and that is the point.</b> C's
 * {@code player_generate} writes the shared pointer — {@code p->race = r}
 * ({@code player-birth.c:991}) — because a race record is read-only data loaded once from
 * {@code p_race.txt}. The port hands out objects with mutable interiors, so the expected behaviour
 * is not a number taken from C but the property that makes the copy worth having: the copy must
 * agree with the source on every field, and must share no structure through which a character could
 * write back into the registry's template and so into every other character of the race.
 *
 * <p>The mutation cases below therefore change the <em>source</em> after copying and assert the copy
 * did not move. That is the failure a shallow copy actually produces: invisible while one character
 * exists, and showing up later as one character's race data following another's.
 *
 * <p>{@link PlayerHistoryChart} is deliberately shared by reference, so it is asserted with
 * {@code assertSame} rather than counted an omission — a chart is a node in the global chart graph
 * and C shares that graph across every character.
 *
 * <p>{@code PlayerRace} publishes no accessor for its index, hit-dice, experience factor or history
 * chart, so those four are read by reflection, as {@code PlayerBodyCopyTest} reads slots.
 *
 * <p>Class PlayerRaceCopyTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
class PlayerRaceCopyTest {

    /**
     * Stat adjustments of the source race; mutable, so a shared map can be detected.
     */
    private Map<Stats, Integer> statsAdj;
    /**
     * Skill adjustments of the source race; mutable for the same reason.
     */
    private Map<PlayerSkill, Integer> skillsAdj;
    /**
     * The source's object flags, held here so the test can write to them after copying.
     */
    private Flag<ObjectFlag> oFlags;
    /**
     * The source's player flags, held for the same reason.
     */
    private Flag<PlayerFlag> pFlags;
    /**
     * The source's element info, held so the test can write through the live flag set.
     */
    private ElementInfo fireInfo;
    /**
     * The source's resistance map.
     */
    private Map<ElementEnum, ElementInfo> resists;
    /**
     * The source's equipment template.
     */
    private PlayerBody body;
    /**
     * The source's history chart, held to assert the copy shares it.
     */
    private PlayerHistoryChart history;
    /**
     * The race being copied.
     */
    private PlayerRace source;

    /**
     * Reads a private field of a race, for the four the class has no accessor for.
     *
     * @param race the race to read
     * @param name the field's name
     * @return the field's value
     * @throws Exception if the field cannot be reached
     */
    private static Object field(PlayerRace race, String name) throws Exception {
        Field f = PlayerRace.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(race);
    }

    /**
     * A source race with a distinct value in every numeric field, so that a mis-ordered constructor
     * argument shows up rather than cancelling out against its neighbour.
     */
    @BeforeEach
    void setUp() {
        statsAdj = new EnumMap<>(Stats.class);
        statsAdj.put(Stats.STAT_STR, 4);
        statsAdj.put(Stats.STAT_INT, -4);

        skillsAdj = new EnumMap<>(PlayerSkill.class);
        skillsAdj.put(PlayerSkill.SKILL_STEALTH, -1);

        oFlags = new Flag<>(ObjectFlag.class);
        oFlags.on(ObjectFlag.OF_SUST_STR);
        pFlags = new Flag<>(PlayerFlag.class);
        pFlags.on(PlayerFlag.PF_BRAVERY_30);

        fireInfo = new ElementInfo();
        fireInfo.setResLevel(-1);
        fireInfo.on(ElementInfoEnum.EL_INFO_HATES);
        resists = new EnumMap<>(ElementEnum.class);
        resists.put(ElementEnum.ELEM_FIRE, fireInfo);

        List<EquipSlot> slots = new ArrayList<>(List.of(
                new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "weapon"),
                new EquipSlot(EquipmentSlotsEnum.EQUIP_RING, "on right hand")));
        body = new PlayerBody("Humanoid", slots);

        history = new PlayerHistoryChart(1, 2);

        source = new PlayerRace("Half-Troll", 7, 12, 135, 20, 10, 96, 10, 255, 50, 3, body,
                statsAdj, skillsAdj, oFlags, pFlags, history, resists);
    }

    /**
     * A copy is as copyable as its source, which is what makes the copy safe to hand on — nothing
     * in the first copy is a view onto the second.
     */
    @Test
    void aCopyOfACopyIsAlsoIndependent() {
        PlayerRace first = source.copy();
        PlayerRace second = first.copy();

        assertAll(
                () -> assertNotSame(first.getBody(), second.getBody()),
                () -> assertEquals(-1, second.getResistanceLevel(ElementEnum.ELEM_FIRE)),
                () -> assertEquals(4, second.getStatAdjust(Stats.STAT_STR)));
    }

    /**
     * Every field arrives on the copy with the source's value, and in the right place — the
     * constructor takes ten consecutive ints, so a transposition is the likely mistake.
     */
    @Nested
    class TheValuesAreCarriedAcross {

        @Test
        void theScalarsAreCopied() throws Exception {
            PlayerRace copy = source.copy();

            assertAll(
                    () -> assertEquals("Half-Troll", copy.getName()),
                    () -> assertEquals(7, field(copy, "rIndex")),
                    () -> assertEquals(12, field(copy, "raceMhp")),
                    () -> assertEquals(135, field(copy, "raceExp")),
                    () -> assertEquals(20, copy.getBaseAge()),
                    () -> assertEquals(10, copy.getModAge()),
                    () -> assertEquals(96, copy.getBaseHeight()),
                    () -> assertEquals(10, copy.getModHeight()),
                    () -> assertEquals(255, copy.getBaseWeight()),
                    () -> assertEquals(50, copy.getModWeight()),
                    () -> assertEquals(3, copy.getInfravision()));
        }

        @Test
        void theAdjustmentsAreCopied() {
            PlayerRace copy = source.copy();

            assertAll(
                    () -> assertEquals(4, copy.getStatAdjust(Stats.STAT_STR)),
                    () -> assertEquals(-4, copy.getStatAdjust(Stats.STAT_INT)),
                    () -> assertEquals(-1, copy.getSkill(PlayerSkill.SKILL_STEALTH)));
        }

        @Test
        void theFlagsAreCopied() {
            PlayerRace copy = source.copy();

            assertAll(
                    () -> assertTrue(copy.getoFlags().has(ObjectFlag.OF_SUST_STR)),
                    () -> assertFalse(copy.getoFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertTrue(copy.getpFlags().has(PlayerFlag.PF_BRAVERY_30)));
        }

        @Test
        void theResistancesAreCopied() {
            PlayerRace copy = source.copy();

            assertAll(
                    () -> assertEquals(-1, copy.getResistanceLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(copy.getResistKnowledge(ElementEnum.ELEM_FIRE)),
                    () -> assertEquals(0, copy.getResistanceLevel(ElementEnum.ELEM_COLD)),
                    () -> assertFalse(copy.getResistKnowledge(ElementEnum.ELEM_COLD)));
        }

        @Test
        void theBodyPlanIsCopied() {
            PlayerBody copied = source.copy().getBody();

            assertAll(
                    () -> assertEquals("Humanoid", copied.getName()),
                    () -> assertEquals(2, copied.getCount()),
                    () -> assertEquals(EquipmentSlotsEnum.EQUIP_WEAPON, copied.getSlot(0).getType()),
                    () -> assertEquals("on right hand", copied.getSlot(1).getName()));
        }
    }

    /**
     * Nothing mutable is shared with the source. Each case writes to the source after copying,
     * which is the shape the bug would actually take: the registry's template being edited through
     * a character that was handed a shallow copy of it.
     */
    @Nested
    class NothingMutableIsShared {

        @Test
        void theCopyIsADifferentObject() {
            assertNotSame(source, source.copy());
        }

        @Test
        void theStatMapIsNotShared() {
            PlayerRace copy = source.copy();

            statsAdj.put(Stats.STAT_STR, 99);

            assertEquals(4, copy.getStatAdjust(Stats.STAT_STR));
        }

        @Test
        void theSkillMapIsNotShared() {
            PlayerRace copy = source.copy();

            skillsAdj.put(PlayerSkill.SKILL_STEALTH, 99);

            assertEquals(-1, copy.getSkill(PlayerSkill.SKILL_STEALTH));
        }

        @Test
        void theObjectFlagsAreNotShared() {
            PlayerRace copy = source.copy();

            oFlags.on(ObjectFlag.OF_FREE_ACT);
            oFlags.off(ObjectFlag.OF_SUST_STR);

            assertAll(
                    () -> assertFalse(copy.getoFlags().has(ObjectFlag.OF_FREE_ACT)),
                    () -> assertTrue(copy.getoFlags().has(ObjectFlag.OF_SUST_STR)));
        }

        @Test
        void thePlayerFlagsAreNotShared() {
            PlayerRace copy = source.copy();

            pFlags.off(PlayerFlag.PF_BRAVERY_30);

            assertTrue(copy.getpFlags().has(PlayerFlag.PF_BRAVERY_30));
        }

        @Test
        void theResistMapIsNotShared() {
            PlayerRace copy = source.copy();

            resists.remove(ElementEnum.ELEM_FIRE);

            assertEquals(-1, copy.getResistanceLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * The element infos themselves are copied, not just the map that holds them — the level is
         * settable and {@link ElementInfo#getFlags()} hands back its live set, so a shared entry is
         * writable through in both halves.
         */
        @Test
        void theElementInfosAreNotShared() {
            PlayerRace copy = source.copy();

            fireInfo.setResLevel(3);
            fireInfo.getFlags().on(ElementInfoEnum.EL_INFO_IGNORE);

            assertAll(
                    () -> assertEquals(-1, copy.getResistanceLevel(ElementEnum.ELEM_FIRE)),
                    () -> assertTrue(copy.getResistKnowledge(ElementEnum.ELEM_FIRE)));
        }

        @Test
        void theBodyIsNotShared() {
            PlayerRace copy = source.copy();

            assertAll(
                    () -> assertNotSame(body, copy.getBody()),
                    () -> assertNotSame(body.getSlot(0), copy.getBody().getSlot(0)));
        }
    }

    /**
     * What sharing is correct: a history chart is a node in the global graph, and C shares it.
     */
    @Nested
    class WhatIsSharedIsSharedOnPurpose {

        @Test
        void theHistoryChartIsShared() throws Exception {
            assertSame(history, field(source.copy(), "history"));
        }
    }
}
