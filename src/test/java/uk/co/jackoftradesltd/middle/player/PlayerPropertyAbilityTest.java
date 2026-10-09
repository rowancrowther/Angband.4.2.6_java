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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.PlayerProperty.PlayerPropertyType;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlagType;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the ability half of {@link PlayerProperty} - {@code classHasAbility}, {@code raceHasAbility}
 * and the {@code group} field - against C's {@code class_has_ability} and {@code race_has_ability}
 * ({@code player-properties.c}) and the {@code PLAYER_FLAG_*} enum ({@code player-properties.h}).
 *
 * <p>Expected values come from reading the C: both functions are chains of
 * {@code streq(ability->type, ...) && flag_has(...)}, with a final {@code false}, and only
 * {@code race_has_ability} has an element branch, which compares {@code res_level} with
 * {@code ability->value} for exact equality. The two methods are private, so they are reached by
 * reflection.
 *
 * <p><b>What is not covered:</b> {@link PlayerProperty#viewAbilities()}. It builds its list of
 * copies, then discards it because the call to the ability menu is still commented out, so nothing
 * it does is observable. Its two behaviours - copies grouped {@code CLASS} then {@code RACE}, and an
 * ability both have listed twice - need a test once {@code viewAbilityMenu} is ported and receives
 * the list.
 *
 * <p>Class PlayerPropertyAbilityTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
class PlayerPropertyAbilityTest {

    /**
     * Builds a property of the given type carrying one code.
     *
     * @param type  the property type
     * @param pCode the player flag, or {@code null}
     * @param oCode the object flag, or {@code null}
     * @param eCode the element, or {@code null}
     * @param value the element level (0 for the other types)
     * @return the property
     */
    private static PlayerProperty property(PlayerPropertyType type, PlayerFlag pCode, ObjectFlag oCode,
                                           ElementEnum eCode, int value) {
        return new PlayerProperty(type, pCode, oCode, eCode, null, List.of(), "test", "", value);
    }

    /**
     * Builds a class with the given flags and nothing else.
     *
     * @param oFlags the class's object flags
     * @param pFlags the class's player flags
     * @return the class
     */
    private static PlayerClass classWith(Flag<ObjectFlag> oFlags, Flag<PlayerFlag> pFlags) {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        return new PlayerClass("Test Class", List.of(), stats, skills, new HashMap<>(skills), 0, 0,
                oFlags, pFlags, 6, 100, 1, List.of(), ClassMagic.NONE);
    }

    /**
     * Builds a race with the given flags and one resistance level.
     *
     * @param oFlags  the race's object flags
     * @param pFlags  the race's player flags
     * @param element the element with a level, or {@code null} for none
     * @param level   the level for that element
     * @return the race
     */
    private static PlayerRace raceWith(Flag<ObjectFlag> oFlags, Flag<PlayerFlag> pFlags, ElementEnum element,
                                       int level) {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<ElementEnum, ElementInfo> resists = new HashMap<>();
        if (element != null) {
            ElementInfo info = new ElementInfo();
            info.setResLevel(level);
            resists.put(element, info);
        }
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0,
                SeededPlayerRegistry.humanoidBody(), stats, skills, oFlags, pFlags, null, resists);
    }

    /**
     * Calls the private {@code classHasAbility}.
     *
     * @param playerClass the class
     * @param property    the property
     * @return the result
     */
    private static boolean classHas(PlayerClass playerClass, PlayerProperty property) {
        return invoke("classHasAbility", PlayerClass.class, playerClass, property);
    }

    /**
     * Calls the private {@code raceHasAbility}.
     *
     * @param race     the race
     * @param property the property
     * @return the result
     */
    private static boolean raceHas(PlayerRace race, PlayerProperty property) {
        return invoke("raceHasAbility", PlayerRace.class, race, property);
    }

    /**
     * Reflection helper for the two private tests, which are instance methods that read nothing from
     * the receiver.
     */
    private static boolean invoke(String method, Class<?> holderType, Object holder, PlayerProperty property) {
        try {
            Method m = PlayerProperty.class.getDeclaredMethod(method, holderType, PlayerProperty.class);
            m.setAccessible(true);
            return (boolean) m.invoke(property, holder, property);
        } catch (InvocationTargetException e) {
            throw new AssertionError(e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * C declares {@code PLAYER_FLAG_NONE, _SPECIAL, _RACE, _CLASS} in that order, so 0 to 3.
     *
     * <p>Function playerFlagTypeOrdinalsMatchC coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("PlayerFlagType keeps C's declaration order: NONE, SPECIAL, RACE, CLASS = 0..3")
    void playerFlagTypeOrdinalsMatchC() {
        assertEquals(0, PlayerFlagType.PLAYER_FLAG_NONE.ordinal());
        assertEquals(1, PlayerFlagType.PLAYER_FLAG_SPECIAL.ordinal());
        assertEquals(2, PlayerFlagType.PLAYER_FLAG_RACE.ordinal());
        assertEquals(3, PlayerFlagType.PLAYER_FLAG_CLASS.ordinal());
        assertEquals(4, PlayerFlagType.values().length);
    }

    /**
     * The data file never sets {@code group}, so every registry entry has {@code group == 0}; a
     * null would throw in a {@code switch} where C falls through to its default.
     *
     * <p>Function newPropertyGroupIsNone coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("A new property's group is PLAYER_FLAG_NONE, as in C's zeroed struct")
    void newPropertyGroupIsNone() {
        PlayerProperty p = property(PlayerPropertyType.PROP_TYPE_PLAYER, PlayerFlag.PF_ZERO_FAIL, null, null, 0);

        assertEquals(PlayerFlagType.PLAYER_FLAG_NONE, p.getGroup());
    }

    /**
     * {@code group} holds one value: the second assignment replaces the first.
     *
     * <p>Function setGroupReplacesTheValue coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("setGroup replaces the group rather than adding to it")
    void setGroupReplacesTheValue() {
        PlayerProperty p = property(PlayerPropertyType.PROP_TYPE_PLAYER, PlayerFlag.PF_ZERO_FAIL, null, null, 0);

        p.setGroup(PlayerFlagType.PLAYER_FLAG_CLASS);
        assertEquals(PlayerFlagType.PLAYER_FLAG_CLASS, p.getGroup());
        p.setGroup(PlayerFlagType.PLAYER_FLAG_RACE);
        assertEquals(PlayerFlagType.PLAYER_FLAG_RACE, p.getGroup());
    }

    /**
     * {@code class_has_ability}: a "player" ability is true iff the class has the player flag.
     *
     * <p>Function classPlayerFlagProperty coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("classHasAbility: a player-flag property applies iff the class has that player flag")
    void classPlayerFlagProperty() {
        PlayerProperty p = property(PlayerPropertyType.PROP_TYPE_PLAYER, PlayerFlag.PF_ZERO_FAIL, null, null, 0);

        PlayerClass has = classWith(new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class, PlayerFlag.PF_ZERO_FAIL));
        PlayerClass hasOther = classWith(new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class, PlayerFlag.PF_FAST_SHOT));
        PlayerClass hasNone = classWith(new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class));

        assertTrue(classHas(has, p));
        assertFalse(classHas(hasOther, p));
        assertFalse(classHas(hasNone, p));
    }

    /**
     * {@code class_has_ability}: an "object" ability is true iff the class has the object flag, and
     * the player flags are not consulted for it.
     *
     * <p>Function classObjectFlagProperty coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("classHasAbility: an object-flag property applies iff the class has that object flag")
    void classObjectFlagProperty() {
        PlayerProperty p = property(PlayerPropertyType.PROP_TYPE_OBJECT, null, ObjectFlag.OF_PROT_FEAR, null, 0);

        PlayerClass has = classWith(new Flag<>(ObjectFlag.class, ObjectFlag.OF_PROT_FEAR),
                new Flag<>(PlayerFlag.class));
        PlayerClass hasOther = classWith(new Flag<>(ObjectFlag.class, ObjectFlag.OF_SUST_STR),
                new Flag<>(PlayerFlag.class));
        PlayerClass hasPlayerFlagOnly = classWith(new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class, PlayerFlag.PF_ZERO_FAIL));

        assertTrue(classHas(has, p));
        assertFalse(classHas(hasOther, p));
        assertFalse(classHas(hasPlayerFlagOnly, p));
    }

    /**
     * {@code class_has_ability} has no element branch, so an element ability never applies to a
     * class, whatever flags the class has.
     *
     * <p>Function classNeverHasElementProperty coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("classHasAbility: an element property never applies to a class")
    void classNeverHasElementProperty() {
        PlayerProperty p = property(PlayerPropertyType.PROP_TYPE_ELEMENT, null, null, ElementEnum.ELEM_COLD, 1);
        PlayerClass flagged = classWith(new Flag<>(ObjectFlag.class, ObjectFlag.OF_PROT_FEAR),
                new Flag<>(PlayerFlag.class, PlayerFlag.PF_ZERO_FAIL));

        assertFalse(classHas(flagged, p));
    }

    /**
     * {@code race_has_ability}: the player-flag and object-flag branches mirror the class's.
     *
     * <p>Function raceFlagProperties coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("raceHasAbility: player-flag and object-flag properties apply iff the race has the flag")
    void raceFlagProperties() {
        PlayerProperty player = property(PlayerPropertyType.PROP_TYPE_PLAYER, PlayerFlag.PF_ZERO_FAIL, null, null, 0);
        PlayerProperty object = property(PlayerPropertyType.PROP_TYPE_OBJECT, null, ObjectFlag.OF_PROT_FEAR, null, 0);

        PlayerRace both = raceWith(new Flag<>(ObjectFlag.class, ObjectFlag.OF_PROT_FEAR),
                new Flag<>(PlayerFlag.class, PlayerFlag.PF_ZERO_FAIL), null, 0);
        PlayerRace neither = raceWith(new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), null, 0);

        assertTrue(raceHas(both, player));
        assertTrue(raceHas(both, object));
        assertFalse(raceHas(neither, player));
        assertFalse(raceHas(neither, object));
    }

    /**
     * {@code race_has_ability}'s element branch is {@code res_level == value}, so it is exact: the
     * documented levels are -1, 1 and 3, and a race at 3 does not satisfy the level-1 property.
     *
     * <p>Function raceElementPropertyIsExactEquality coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("raceHasAbility: an element property needs the exact level (-1, 1 or 3), not 'at least'")
    void raceElementPropertyIsExactEquality() {
        Flag<ObjectFlag> noObj = new Flag<>(ObjectFlag.class);
        Flag<PlayerFlag> noPlayer = new Flag<>(PlayerFlag.class);

        for (int level : new int[]{-1, 1, 3}) {
            PlayerProperty p = property(PlayerPropertyType.PROP_TYPE_ELEMENT, null, null, ElementEnum.ELEM_COLD, level);
            assertTrue(raceHas(raceWith(noObj, noPlayer, ElementEnum.ELEM_COLD, level), p), "level " + level);
        }

        PlayerProperty resist = property(PlayerPropertyType.PROP_TYPE_ELEMENT, null, null, ElementEnum.ELEM_COLD, 1);
        assertFalse(raceHas(raceWith(noObj, noPlayer, ElementEnum.ELEM_COLD, 3), resist), "immune is not resist");
        assertFalse(raceHas(raceWith(noObj, noPlayer, ElementEnum.ELEM_COLD, -1), resist), "vulnerable is not resist");
    }

    /**
     * The element is part of the match: a race resistant to fire does not satisfy the cold property.
     * And a race with no entry for the element reads as level 0, as C's zeroed {@code el_info} does,
     * so it fails a level-1 property.
     *
     * <p>Function raceElementPropertyIsPerElement coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("raceHasAbility: the level is read for the property's own element; a missing entry is 0")
    void raceElementPropertyIsPerElement() {
        Flag<ObjectFlag> noObj = new Flag<>(ObjectFlag.class);
        Flag<PlayerFlag> noPlayer = new Flag<>(PlayerFlag.class);
        PlayerProperty coldResist = property(PlayerPropertyType.PROP_TYPE_ELEMENT, null, null,
                ElementEnum.ELEM_COLD, 1);

        assertFalse(raceHas(raceWith(noObj, noPlayer, ElementEnum.ELEM_FIRE, 1), coldResist));
        assertFalse(raceHas(raceWith(noObj, noPlayer, null, 0), coldResist));
    }

    /**
     * Types other than player, object and element fall through C's {@code streq} chain to
     * {@code false}, even when the race has a flag the property happens to carry.
     *
     * <p>Function raceUnrecognizedTypeMatchesNothing coded on 261009, commented in full on 261009.
     */
    @Test
    @DisplayName("raceHasAbility: an unrecognized property type applies to nothing")
    void raceUnrecognizedTypeMatchesNothing() {
        PlayerRace race = raceWith(new Flag<>(ObjectFlag.class, ObjectFlag.OF_PROT_FEAR),
                new Flag<>(PlayerFlag.class, PlayerFlag.PF_ZERO_FAIL), ElementEnum.ELEM_COLD, 1);

        PlayerProperty modifier = new PlayerProperty(PlayerPropertyType.PROP_TYPE_OBJECT_MODIFIER,
                PlayerFlag.PF_ZERO_FAIL, ObjectFlag.OF_PROT_FEAR, ElementEnum.ELEM_COLD,
                ObjectModifier.values()[1], List.of(), "test", "", 1);
        PlayerProperty reserved = new PlayerProperty(PlayerPropertyType.PROP_TYPE_PROPERTY,
                PlayerFlag.PF_ZERO_FAIL, ObjectFlag.OF_PROT_FEAR, ElementEnum.ELEM_COLD,
                null, List.of(), "test", "", 1);

        assertFalse(raceHas(race, modifier));
        assertFalse(raceHas(race, reserved));
    }
}
