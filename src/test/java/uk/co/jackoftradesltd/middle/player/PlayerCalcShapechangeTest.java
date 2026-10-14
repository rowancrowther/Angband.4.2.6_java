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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.PlayerCalcs.Extras;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerCalcs#calcShapechange}, the port of C's {@code calc_shapechange}
 * ({@code player-calcs.c}): what a shape adds to a state that the equipment walk has already filled.
 *
 * <p>Each case sets up a state with a known starting figure and a shape with a known contribution,
 * and the expected value is the sum C's function would leave: every line of
 * {@code calc_shapechange} is an {@code +=} or, for resistances, a "better of the two" and a
 * remembered vulnerability.
 *
 * <p>Class PlayerCalcShapechangeTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
class PlayerCalcShapechangeTest {

    /**
     * The state the shape adds to, wiped so every figure starts at zero.
     */
    private PlayerState state;

    /**
     * The vulnerabilities gathered so far, as {@code calcBonuses} would hold them.
     */
    private Map<ElementEnum, Boolean> vulnerabilities;

    /**
     * Wipes a fresh state and starts with no vulnerabilities.
     */
    @BeforeEach
    void setUp() {
        state = new PlayerState();
        state.wipe();
        vulnerabilities = new HashMap<>();
    }

    /**
     * Reads one of {@link PlayerState}'s combat totals, which have no public getter.
     *
     * @param name the field: {@code toH} or {@code toD}
     * @return the total
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private int total(String name) throws ReflectiveOperationException {
        Field field = PlayerState.class.getDeclaredField(name);
        field.setAccessible(true);
        return (int) field.get(state);
    }

    /**
     * A shape that contributes only what the test passes in.
     *
     * @param toAc      armour bonus
     * @param toHit     to-hit bonus
     * @param toDam     to-dam bonus
     * @param skills    skill bonuses
     * @param flags     object flags
     * @param pflags    player flags
     * @param modifiers object modifiers
     * @param elements  element levels
     * @return the shape
     */
    private static PlayerShape shape(int toAc, int toHit, int toDam,
                                     Map<PlayerSkill, Integer> skills,
                                     Flag<ObjectFlag> flags, Flag<PlayerFlag> pflags,
                                     Map<ObjectModifier, Integer> modifiers,
                                     Map<ElementEnum, ElementInfo> elements) {
        return new PlayerShape("test shape", toAc, toHit, toDam, skills, flags, pflags,
                modifiers, elements, List.of(), 0, List.of());
    }

    /**
     * A shape with only modifiers.
     *
     * @param modifiers the object modifiers
     * @return the shape
     */
    private static PlayerShape withModifiers(Map<ObjectModifier, Integer> modifiers) {
        return shape(0, 0, 0, Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                modifiers, Map.of());
    }

    /**
     * A shape with only element levels.
     *
     * @param levels each element and the level the shape gives it
     * @return the shape
     */
    private static PlayerShape withElements(Map<ElementEnum, Integer> levels) {
        Map<ElementEnum, ElementInfo> elements = new HashMap<>();
        levels.forEach((element, level) -> {
            ElementInfo info = new ElementInfo();
            info.setResLevel(level);
            elements.put(element, info);
        });
        return shape(0, 0, 0, Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                Map.of(), elements);
    }

    /**
     * The cases that end the call at once or leave things alone.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("no shape")
    class NoShape {

        /**
         * Port-only: C always has a shape ("normal"); the port may have none, and then the totals come
         * back as they went in and the state is untouched.
         */
        @Test
        @DisplayName("a null shape returns the totals untouched")
        void nullShape() {
            Extras incoming = new Extras(1, 2, 3, 4);
            state.setSpeed(110);

            Extras outgoing = PlayerCalcs.calcShapechange(state, vulnerabilities, null, incoming);

            assertAll(
                    () -> assertSame(incoming, outgoing),
                    () -> assertEquals(110, state.getSpeed()),
                    () -> assertTrue(vulnerabilities.isEmpty()));
        }

        /**
         * The "normal" shape C gives every character contributes nothing: every figure it adds is
         * zero.
         */
        @Test
        @DisplayName("a shape that contributes nothing changes nothing")
        void emptyShape() throws Exception {
            state.setSpeed(110);

            Extras outgoing = PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withModifiers(Map.of()), new Extras(1, 2, 3, 4));

            assertAll(
                    () -> assertEquals(new Extras(1, 2, 3, 4), outgoing),
                    () -> assertEquals(110, state.getSpeed()),
                    () -> assertEquals(0, state.getToAc()),
                    () -> assertEquals(0, total("toH")),
                    () -> assertEquals(0, total("toD")));
        }
    }

    /**
     * Combat bonuses, skills, flags and stats.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("what a shape adds")
    class WhatItAdds {

        /**
         * C's three combat lines are {@code +=}: a shape of +3 armour, -2 to-hit and +5 to-dam
         * added to a state already holding 10, 10 and 10 leaves 13, 8 and 15.
         */
        @Test
        @DisplayName("combat bonuses are added to what is already there")
        void combatBonusesAdd() throws Exception {
            state.toAcAdd(10);
            state.toHitAdd(10);
            state.toDamAdd(10);
            PlayerShape shape = shape(3, -2, 5, Map.of(), new Flag<>(ObjectFlag.class),
                    new Flag<>(PlayerFlag.class), Map.of(), Map.of());

            PlayerCalcs.calcShapechange(state, vulnerabilities, shape, new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertEquals(13, state.getToAc()),
                    () -> assertEquals(8, total("toH")),
                    () -> assertEquals(15, total("toD")));
        }

        /**
         * A shape's skills add to the state's: stealth 4 on a starting 10 is 14, and a skill the
         * shape does not list is left as it was.
         */
        @Test
        @DisplayName("skills are added, and unlisted skills are untouched")
        void skillsAdd() {
            state.setStateSkill(PlayerSkill.SKILL_STEALTH, 10);
            state.setStateSkill(PlayerSkill.SKILL_SAVE, 7);
            PlayerShape shape = shape(0, 0, 0, Map.of(PlayerSkill.SKILL_STEALTH, 4),
                    new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), Map.of(), Map.of());

            PlayerCalcs.calcShapechange(state, vulnerabilities, shape, new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertEquals(14, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertEquals(7, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)));
        }

        /**
         * Object flags and player flags are unioned: the shape's join the state's and nothing the
         * state held is dropped.
         */
        @Test
        @DisplayName("flags are unioned, not replaced")
        void flagsUnion() {
            state.getObjectFlag().on(ObjectFlag.OF_SEE_INVIS);
            state.playerFlagOn(PlayerFlag.PF_FAST_SHOT);
            Flag<ObjectFlag> objectFlags = new Flag<>(ObjectFlag.class);
            objectFlags.on(ObjectFlag.OF_FREE_ACT);
            Flag<PlayerFlag> playerFlags = new Flag<>(PlayerFlag.class);
            playerFlags.on(PlayerFlag.PF_UNLIGHT);
            PlayerShape shape = shape(0, 0, 0, Map.of(), objectFlags, playerFlags, Map.of(), Map.of());

            PlayerCalcs.calcShapechange(state, vulnerabilities, shape, new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertTrue(state.hasOFlag(ObjectFlag.OF_SEE_INVIS), "kept"),
                    () -> assertTrue(state.hasOFlag(ObjectFlag.OF_FREE_ACT), "added"),
                    () -> assertTrue(state.hasPFlag(PlayerFlag.PF_FAST_SHOT), "kept"),
                    () -> assertTrue(state.hasPFlag(PlayerFlag.PF_UNLIGHT), "added"));
        }

        /**
         * A shape's strength and constitution modifiers add to the state's stat additions, one stat
         * to one stat, and the other stats are untouched.
         */
        @Test
        @DisplayName("stat modifiers are added to the matching stat only")
        void statModifiersAdd() {
            state.statAdd(Stats.STAT_STR, 1);
            PlayerShape shape = withModifiers(Map.of(ObjectModifier.OM_STR, 2, ObjectModifier.OM_CON, -1));

            PlayerCalcs.calcShapechange(state, vulnerabilities, shape, new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertEquals(3, state.getStatAdd(Stats.STAT_STR)),
                    () -> assertEquals(-1, state.getStatAdd(Stats.STAT_CON)),
                    () -> assertEquals(0, state.getStatAdd(Stats.STAT_DEX)));
        }

        /**
         * The remaining modifiers go where C sends them: stealth to the stealth skill, search times
         * five to the search skill, tunnelling times twenty to digging, speed to speed, and damage
         * reduction to damage reduction.
         */
        @Test
        @DisplayName("stealth, search, tunnel, speed and damage reduction land where C puts them")
        void otherModifiers() {
            PlayerShape shape = withModifiers(Map.of(
                    ObjectModifier.OM_STEALTH, 2,
                    ObjectModifier.OM_SEARCH, 3,
                    ObjectModifier.OM_TUNNEL, 4,
                    ObjectModifier.OM_SPEED, 5,
                    ObjectModifier.OM_DAM_RED, 6));
            state.setSpeed(110);

            PlayerCalcs.calcShapechange(state, vulnerabilities, shape, new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertEquals(2, state.getPlayerSkill(PlayerSkill.SKILL_STEALTH)),
                    () -> assertEquals(15, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH), "3 * 5"),
                    () -> assertEquals(80, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING), "4 * 20"),
                    () -> assertEquals(115, state.getSpeed()),
                    () -> assertEquals(6, state.getDamRed()));
        }

        /**
         * Blows, shots, might and moves are returned added to the incoming totals, each to its own:
         * (1, 2, 3, 4) plus (10, 20, 30, 40) is (11, 22, 33, 44).
         */
        @Test
        @DisplayName("blows, shots, might and moves are added to the incoming totals")
        void extrasAdd() {
            PlayerShape shape = withModifiers(Map.of(
                    ObjectModifier.OM_BLOWS, 10,
                    ObjectModifier.OM_SHOTS, 20,
                    ObjectModifier.OM_MIGHT, 30,
                    ObjectModifier.OM_MOVES, 40));

            Extras outgoing = PlayerCalcs.calcShapechange(state, vulnerabilities, shape, new Extras(1, 2, 3, 4));

            assertEquals(new Extras(11, 22, 33, 44), outgoing);
        }
    }

    /**
     * Resistances and vulnerabilities: the shape's level is taken only if better, and a vulnerability
     * is remembered rather than applied.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("resistances")
    class Resistances {

        /**
         * A shape's resistance above the state's is taken: fire 0 becomes 1.
         */
        @Test
        @DisplayName("a better resistance replaces the state's")
        void betterResistanceTaken() {
            PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withElements(Map.of(ElementEnum.ELEM_FIRE, 1)), new Extras(0, 0, 0, 0));

            assertEquals(1, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * A shape's resistance below the state's is not taken: acid stays at 2 against the shape's 1.
         */
        @Test
        @DisplayName("a worse resistance leaves the state's alone")
        void worseResistanceIgnored() {
            state.setResLevel(ElementEnum.ELEM_ACID, 2);

            PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withElements(Map.of(ElementEnum.ELEM_ACID, 1)), new Extras(0, 0, 0, 0));

            assertEquals(2, state.getResLevel(ElementEnum.ELEM_ACID));
        }

        /**
         * Immunity (3) over a resistance (1) is taken.
         */
        @Test
        @DisplayName("immunity replaces a resistance")
        void immunityTaken() {
            state.setResLevel(ElementEnum.ELEM_ELEC, 1);

            PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withElements(Map.of(ElementEnum.ELEM_ELEC, 3)), new Extras(0, 0, 0, 0));

            assertEquals(3, state.getResLevel(ElementEnum.ELEM_ELEC));
        }

        /**
         * A vulnerability (-1) is not applied to the level — C leaves that to the pass after all the
         * sources are in — but is remembered: the state's cold stays at 0 and the set gains cold.
         */
        @Test
        @DisplayName("a vulnerability is remembered, not applied")
        void vulnerabilityRemembered() {
            PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withElements(Map.of(ElementEnum.ELEM_COLD, -1)), new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertEquals(0, state.getResLevel(ElementEnum.ELEM_COLD)),
                    () -> assertTrue(vulnerabilities.getOrDefault(ElementEnum.ELEM_COLD, false)));
        }

        /**
         * An element the shape gives level 0 is neither a vulnerability nor an improvement.
         */
        @Test
        @DisplayName("a neutral element changes nothing")
        void neutralElement() {
            PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withElements(Map.of(ElementEnum.ELEM_POIS, 0)), new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertEquals(0, state.getResLevel(ElementEnum.ELEM_POIS)),
                    () -> assertFalse(vulnerabilities.getOrDefault(ElementEnum.ELEM_POIS, false)));
        }

        /**
         * Elements the shape does not mention are untouched, and the existing vulnerabilities are
         * kept: the set is added to, never rebuilt.
         */
        @Test
        @DisplayName("unmentioned elements and earlier vulnerabilities survive")
        void unmentionedSurvive() {
            vulnerabilities.put(ElementEnum.ELEM_FIRE, true);
            state.setResLevel(ElementEnum.ELEM_NEXUS, 1);

            PlayerCalcs.calcShapechange(state, vulnerabilities,
                    withElements(Map.of(ElementEnum.ELEM_COLD, -1)), new Extras(0, 0, 0, 0));

            assertAll(
                    () -> assertTrue(vulnerabilities.getOrDefault(ElementEnum.ELEM_FIRE, false)),
                    () -> assertTrue(vulnerabilities.getOrDefault(ElementEnum.ELEM_COLD, false)),
                    () -> assertEquals(1, state.getResLevel(ElementEnum.ELEM_NEXUS)));
        }
    }
}
