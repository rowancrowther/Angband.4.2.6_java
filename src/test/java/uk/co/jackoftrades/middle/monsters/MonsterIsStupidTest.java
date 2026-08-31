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

package uk.co.jackoftrades.middle.monsters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@code Monster.monsterIsStupid()}, the port of C's {@code monster_is_stupid} in
 * {@code mon-predicate.c}.
 *
 * <p>Expected values come from the C, whose whole body is
 * {@code return rf_has(mon->race->flags, RF_STUPID);}. The cases therefore pin what that one line
 * says and, just as importantly, what it does not: the flag set and clear, that no other race flag
 * stands in for it, and — the case where C and a plausible port most easily diverge — that the
 * <em>current</em> race alone is consulted. The neighbouring {@code monster_is_unique} does fall
 * back to {@code original_race}, and this one deliberately does not, so both shapechange
 * directions are covered here.
 *
 * <p>The method under test is private, so it is reached by reflection rather than by widening
 * production code for the benefit of a test.
 *
 * <p>Class MonsterIsStupidTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@DisplayName("Monster.monsterIsStupid")
class MonsterIsStupidTest {

    /**
     * Build a bare race carrying the given race flags. Every other field is left null or zero; the
     * predicate reads nothing but the flag set.
     *
     * @param raceFlags the race flags to switch on
     * @return a shell race carrying those flags
     */
    private static MonsterRace raceWith(MonsterRaceFlag... raceFlags) {
        Flag<MonsterRaceFlag> flags = new Flag<>(MonsterRaceFlag.class);
        for (MonsterRaceFlag flag : raceFlags) {
            flags.on(flag);
        }
        return new MonsterRace("test", "", "", null, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                flags, null, List.of(), 0, 0, null, null, List.of(), List.of(), List.of(),
                List.of(), List.of(), 0, null);
    }

    /**
     * Build a bare monster with the given current and original races.
     *
     * @param current  the monster's current race
     * @param original the monster's original race, or {@code null} if it has not shapechanged
     * @return a shell monster wearing those races
     */
    private static Monster monsterOf(MonsterRace current, MonsterRace original) {
        return new Monster(current, original, null, 0, 0, null, 0, 0, 0,
                new Flag<>(MonsterFlag.class), null, null, null, null, null, null, null, 0, 0);
    }

    /**
     * Invoke the private predicate on the given monster.
     *
     * @param mon the monster to ask
     * @return the predicate's answer
     * @throws Exception if reflection fails, which would itself be a real failure
     */
    private static boolean isStupid(Monster mon) throws Exception {
        Method method = Monster.class.getDeclaredMethod("monsterIsStupid");
        method.setAccessible(true);
        return (boolean) method.invoke(mon);
    }

    @Test
    @DisplayName("RF_STUPID on the race reports stupid")
    void flagSetIsStupid() throws Exception {
        assertTrue(isStupid(monsterOf(raceWith(MonsterRaceFlag.RF_STUPID), null)));
    }

    @Test
    @DisplayName("an empty race flag set reports not stupid")
    void noFlagsIsNotStupid() throws Exception {
        assertFalse(isStupid(monsterOf(raceWith(), null)));
    }

    @Test
    @DisplayName("other race flags do not imply stupidity")
    void otherFlagsAreNotStupid() throws Exception {
        Monster mon = monsterOf(raceWith(MonsterRaceFlag.RF_UNIQUE, MonsterRaceFlag.RF_SMART), null);

        assertFalse(isStupid(mon));
    }

    /**
     * C tests {@code RF_STUPID} and {@code RF_SMART} through separate predicates and never treats
     * one as the negation of the other; {@code update_smart_learn} consults both in turn. A race
     * carrying neither must therefore answer false here without being called smart.
     */
    @Test
    @DisplayName("stupid and smart are independent flags")
    void stupidAndSmartAreIndependent() throws Exception {
        assertTrue(isStupid(monsterOf(raceWith(MonsterRaceFlag.RF_STUPID), null)));
        assertFalse(isStupid(monsterOf(raceWith(MonsterRaceFlag.RF_SMART), null)));
    }

    /**
     * The shapechange case in the direction that would break a copy of {@code monster_is_unique}:
     * a clever monster wearing a stupid shape. C reads {@code mon->race} with no fallback, so it is
     * stupid while the shape holds, even though its original race is not.
     */
    @Test
    @DisplayName("a clever monster in a stupid shape is stupid")
    void currentRaceStupidOriginalNot() throws Exception {
        Monster mon = monsterOf(raceWith(MonsterRaceFlag.RF_STUPID),
                raceWith(MonsterRaceFlag.RF_SMART));

        assertTrue(isStupid(mon));
    }

    /**
     * The mirror case: a stupid monster wearing a clever shape. Falling back to
     * {@code original_race} would wrongly answer true, so this pins the absence of that fallback.
     */
    @Test
    @DisplayName("a stupid monster in a clever shape is not stupid")
    void originalRaceStupidCurrentNot() throws Exception {
        Monster mon = monsterOf(raceWith(), raceWith(MonsterRaceFlag.RF_STUPID));

        assertFalse(isStupid(mon));
    }

    /**
     * The flag lives on the race, not on the individual monster, so two monsters sharing a race
     * must answer alike however their transient state differs.
     */
    @Test
    @DisplayName("monsters sharing a race answer alike")
    void raceIsShared() throws Exception {
        MonsterRace shared = raceWith(MonsterRaceFlag.RF_STUPID);

        assertTrue(isStupid(monsterOf(shared, null)));
        assertTrue(isStupid(monsterOf(shared, null)));
    }
}
