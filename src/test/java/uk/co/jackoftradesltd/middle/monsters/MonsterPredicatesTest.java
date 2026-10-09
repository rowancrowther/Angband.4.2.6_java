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

package uk.co.jackoftradesltd.middle.monsters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the small {@code struct monster} predicates and flag macros in {@link Monster}:
 * {@code isUnique} ({@code monster_is_unique}), {@code monsterIsVisible}
 * ({@code monster_is_visible}), {@code isObvious} ({@code monster_is_obvious}), and
 * {@code hasMonsterFlag}, {@code monsterFlagOn} and {@code monsterFlagOff} ({@code mflag_has},
 * {@code mflag_on}, {@code mflag_off}).
 *
 * <p>Expected values come from the C. {@code monster_is_unique} reads {@code RF_UNIQUE} from
 * {@code original_race} when that is non-null and from {@code race} otherwise, and never combines
 * them, so the shapechange cases are the ones where a plausible port diverges.
 * {@code monster_is_obvious} is {@code visible && !camouflaged}, giving a four-row truth table.
 *
 * <p>Class MonsterPredicatesTest coded on 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
@DisplayName("Monster predicates and monster flags")
class MonsterPredicatesTest {

    /**
     * Build a bare race carrying the given race flags.
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
     * Build a bare monster with the given races and transient flags.
     *
     * @param current  the current race
     * @param original the original race, or {@code null}
     * @param flags    the transient flags to switch on
     * @return a shell monster
     */
    private static Monster monster(MonsterRace current, MonsterRace original, MonsterFlag... flags) {
        Flag<MonsterFlag> mflag = new Flag<>(MonsterFlag.class);
        for (MonsterFlag flag : flags) {
            mflag.on(flag);
        }
        return new Monster(current, original, null, 0, 0, null, 0, 0, 0, mflag,
                null, null, null, null, null, null, null, 0, 0);
    }

    private static Monster monsterWith(MonsterFlag... flags) {
        return monster(null, null, flags);
    }

    @Test
    @DisplayName("isUnique: no original race, current race decides")
    void uniqueWithoutOriginal() {
        assertTrue(monster(raceWith(MonsterRaceFlag.RF_UNIQUE), null).isUnique());
        assertFalse(monster(raceWith(), null).isUnique());
    }

    @Test
    @DisplayName("isUnique: a unique that shapechanged into a common race is still unique")
    void uniqueOriginalCommonShape() {
        assertTrue(monster(raceWith(), raceWith(MonsterRaceFlag.RF_UNIQUE)).isUnique());
    }

    @Test
    @DisplayName("isUnique: a common monster in a unique's shape is not unique")
    void commonOriginalUniqueShape() {
        assertFalse(monster(raceWith(MonsterRaceFlag.RF_UNIQUE), raceWith()).isUnique());
    }

    @Test
    @DisplayName("isUnique: unique in both forms, and unique in neither")
    void uniqueBothOrNeither() {
        assertTrue(monster(raceWith(MonsterRaceFlag.RF_UNIQUE),
                raceWith(MonsterRaceFlag.RF_UNIQUE)).isUnique());
        assertFalse(monster(raceWith(), raceWith()).isUnique());
    }

    @Test
    @DisplayName("isUnique: other race flags do not stand in for RF_UNIQUE")
    void uniqueIgnoresOtherFlags() {
        assertFalse(monster(raceWith(MonsterRaceFlag.RF_SMART, MonsterRaceFlag.RF_STUPID), null)
                .isUnique());
    }

    @Test
    @DisplayName("monsterIsVisible reads MFLAG_VISIBLE and nothing else")
    void visibleReadsOnlyVisibleFlag() {
        assertTrue(monsterWith(MonsterFlag.MFLAG_VISIBLE).monsterIsVisible());

        assertFalse(monsterWith(MonsterFlag.MFLAG_CAMOUFLAGE).monsterIsVisible());
        assertFalse(monsterWith().monsterIsVisible());
    }

    @Test
    @DisplayName("isObvious: visible and not camouflaged is the only true row")
    void obviousTruthTable() {
        assertTrue(monsterWith(MonsterFlag.MFLAG_VISIBLE).isObvious());
        assertFalse(monsterWith(MonsterFlag.MFLAG_VISIBLE, MonsterFlag.MFLAG_CAMOUFLAGE).isObvious());
        assertFalse(monsterWith(MonsterFlag.MFLAG_CAMOUFLAGE).isObvious());
        assertFalse(monsterWith().isObvious());
    }

    @Test
    @DisplayName("monsterFlagOn sets a flag and is idempotent")
    void flagOn() {
        Monster mon = monsterWith();
        assertFalse(mon.hasMonsterFlag(MonsterFlag.MFLAG_CAMOUFLAGE));
        mon.monsterFlagOn(MonsterFlag.MFLAG_CAMOUFLAGE);
        assertTrue(mon.hasMonsterFlag(MonsterFlag.MFLAG_CAMOUFLAGE));
        mon.monsterFlagOn(MonsterFlag.MFLAG_CAMOUFLAGE);
        assertTrue(mon.hasMonsterFlag(MonsterFlag.MFLAG_CAMOUFLAGE));
    }

    @Test
    @DisplayName("monsterFlagOff clears a flag whether or not it was set, and leaves others alone")
    void flagOff() {
        Monster mon = monsterWith(MonsterFlag.MFLAG_CAMOUFLAGE, MonsterFlag.MFLAG_AWARE);
        mon.monsterFlagOff(MonsterFlag.MFLAG_CAMOUFLAGE);
        assertFalse(mon.hasMonsterFlag(MonsterFlag.MFLAG_CAMOUFLAGE));
        assertTrue(mon.hasMonsterFlag(MonsterFlag.MFLAG_AWARE));

        mon.monsterFlagOff(MonsterFlag.MFLAG_CAMOUFLAGE);
        assertFalse(mon.hasMonsterFlag(MonsterFlag.MFLAG_CAMOUFLAGE));
    }

    @Test
    @DisplayName("setting then clearing MFLAG_VISIBLE toggles monsterIsVisible")
    void visibleToggles() {
        Monster mon = monsterWith();
        mon.monsterFlagOn(MonsterFlag.MFLAG_VISIBLE);
        assertTrue(mon.monsterIsVisible());
        mon.monsterFlagOff(MonsterFlag.MFLAG_VISIBLE);
        assertFalse(mon.monsterIsVisible());
    }
}
