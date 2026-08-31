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
 * Tests for {@code Monster.monsterIsSmart()}, the port of C's {@code monster_is_smart} in
 * {@code mon-predicate.c}.
 *
 * <p>Expected values come from the C, which is two clauses rather than one: an original race
 * carrying {@code RF_SMART} answers true outright, and only failing that does the current race
 * decide. The cases pin both clauses, the order between them, and the short-circuit on a null
 * original race. The pair of shapechange cases is where this predicate and its neighbour
 * {@code monster_is_stupid} part company — smart is remembered across a shapechange, stupid is
 * not — so both directions are covered, and the mirror-image assertions in
 * {@code MonsterIsStupidTest} are what make the contrast visible.
 *
 * <p>The method under test is private, so it is reached by reflection rather than by widening
 * production code for the benefit of a test.
 *
 * <p>Class MonsterIsSmartTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@DisplayName("Monster.monsterIsSmart")
class MonsterIsSmartTest {

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
    private static boolean isSmart(Monster mon) throws Exception {
        Method method = Monster.class.getDeclaredMethod("monsterIsSmart");
        method.setAccessible(true);
        return (boolean) method.invoke(mon);
    }

    @Test
    @DisplayName("RF_SMART on an unchanged monster's race reports smart")
    void flagSetIsSmart() throws Exception {
        assertTrue(isSmart(monsterOf(raceWith(MonsterRaceFlag.RF_SMART), null)));
    }

    @Test
    @DisplayName("an empty race flag set reports not smart")
    void noFlagsIsNotSmart() throws Exception {
        assertFalse(isSmart(monsterOf(raceWith(), null)));
    }

    @Test
    @DisplayName("other race flags do not imply smartness")
    void otherFlagsAreNotSmart() throws Exception {
        Monster mon = monsterOf(raceWith(MonsterRaceFlag.RF_UNIQUE, MonsterRaceFlag.RF_STUPID), null);

        assertFalse(isSmart(mon));
    }

    /**
     * C tests {@code RF_SMART} and {@code RF_STUPID} through separate predicates and never treats
     * one as the negation of the other; {@code update_smart_learn} consults both in turn. A race
     * carrying only {@code RF_STUPID} must therefore answer false here, and one carrying neither
     * must answer false too.
     */
    @Test
    @DisplayName("smart and stupid are independent flags")
    void smartAndStupidAreIndependent() throws Exception {
        assertFalse(isSmart(monsterOf(raceWith(MonsterRaceFlag.RF_STUPID), null)));
        assertFalse(isSmart(monsterOf(raceWith(), null)));
    }

    /**
     * The "or was" half of C's comment: a smart monster wearing a dull shape. The first clause
     * fires on {@code original_race} and the current race is never reached, so it stays smart for
     * as long as the shape holds. This is the case that would fail if the port read
     * {@code mon->race} alone, the way {@code monster_is_stupid} does.
     */
    @Test
    @DisplayName("a smart monster in a dull shape is still smart")
    void originalRaceSmartCurrentNot() throws Exception {
        Monster mon = monsterOf(raceWith(), raceWith(MonsterRaceFlag.RF_SMART));

        assertTrue(isSmart(mon));
    }

    /**
     * The mirror case: a dull monster wearing a clever shape. The first clause fails, so the
     * current race decides and the monster is smart while the shape holds. A port that preferred
     * {@code original_race} outright — the shape of {@code monster_is_unique} — would wrongly
     * answer false.
     */
    @Test
    @DisplayName("a dull monster in a clever shape is smart")
    void currentRaceSmartOriginalNot() throws Exception {
        Monster mon = monsterOf(raceWith(MonsterRaceFlag.RF_SMART), raceWith());

        assertTrue(isSmart(mon));
    }

    /**
     * Both races carrying the flag, and neither carrying it, are the uninteresting corners of the
     * two-clause test, but they pin that the clauses are an OR rather than an AND.
     */
    @Test
    @DisplayName("both races smart is smart, neither is not")
    void bothAndNeither() throws Exception {
        assertTrue(isSmart(monsterOf(raceWith(MonsterRaceFlag.RF_SMART),
                raceWith(MonsterRaceFlag.RF_SMART))));
        assertFalse(isSmart(monsterOf(raceWith(MonsterRaceFlag.RF_STUPID),
                raceWith(MonsterRaceFlag.RF_STUPID))));
    }

    /**
     * A null original race is the ordinary state of a monster that has never shapechanged, and C
     * guards the first clause on that pointer. The current race must decide, both ways, without
     * the null being dereferenced.
     */
    @Test
    @DisplayName("a null original race falls through to the current race")
    void nullOriginalRaceFallsThrough() throws Exception {
        assertTrue(isSmart(monsterOf(raceWith(MonsterRaceFlag.RF_SMART), null)));
        assertFalse(isSmart(monsterOf(raceWith(), null)));
    }

    /**
     * The flags live on the races, not on the individual monster, so two monsters sharing a race
     * must answer alike however their transient state differs.
     */
    @Test
    @DisplayName("monsters sharing a race answer alike")
    void raceIsShared() throws Exception {
        MonsterRace shared = raceWith(MonsterRaceFlag.RF_SMART);

        assertTrue(isSmart(monsterOf(shared, null)));
        assertTrue(isSmart(monsterOf(shared, null)));
    }
}
