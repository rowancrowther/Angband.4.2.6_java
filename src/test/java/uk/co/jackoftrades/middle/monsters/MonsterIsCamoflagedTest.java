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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link Monster#monsterIsCamouflaged()}, the port of C's
 * {@code monster_is_camouflaged} in {@code mon-predicate.c}.
 *
 * <p>Expected values come from the C, which is
 * {@code return mflag_has(mon->mflag, MFLAG_CAMOUFLAGE);} — a pure read of one transient flag.
 * The cases therefore pin three things the C states: the flag set and clear, that no other
 * transient flag stands in for it, and that {@code mimicked_obj} plays no part (that field
 * separates {@code monster_is_mimicking} from this predicate, and C never consults it here).
 *
 * <p>Class MonsterIsCamoflagedTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@DisplayName("Monster.monsterIsCamouflaged")
class MonsterIsCamoflagedTest {

    /**
     * Build a bare monster carrying the given transient flags. Every other field is left null or
     * zero; the predicate reads nothing else.
     *
     * @param flags the transient flags to switch on
     * @return a shell monster carrying those flags
     */
    private static Monster monsterWith(MonsterFlag... flags) {
        Flag<MonsterFlag> mflag = new Flag<>(MonsterFlag.class);
        for (MonsterFlag flag : flags) {
            mflag.on(flag);
        }
        return new Monster(null, null, null, 0, 0, null, 0, 0, 0, mflag,
                null, null, null, null, null, null, null, 0, 0);
    }

    @Test
    @DisplayName("the flag set reports camouflaged")
    void flagSetIsCamouflaged() {
        assertTrue(monsterWith(MonsterFlag.MFLAG_CAMOUFLAGE).monsterIsCamouflaged());
    }

    @Test
    @DisplayName("an empty flag set reports not camouflaged")
    void noFlagsIsNotCamouflaged() {
        assertFalse(monsterWith().monsterIsCamouflaged());
    }

    @Test
    @DisplayName("other transient flags do not imply camouflage")
    void otherFlagsAreNotCamouflage() {
        Monster mon = monsterWith(MonsterFlag.MFLAG_VISIBLE, MonsterFlag.MFLAG_VIEW);

        assertFalse(mon.monsterIsCamouflaged());
    }

    /**
     * C's {@code monster_is_obvious} is {@code visible && !camouflaged}, so a visible monster may
     * still be camouflaged; visibility must not mask the flag.
     */
    @Test
    @DisplayName("a visible monster can still be camouflaged")
    void visibleAndCamouflaged() {
        Monster mon = monsterWith(MonsterFlag.MFLAG_VISIBLE, MonsterFlag.MFLAG_CAMOUFLAGE);

        assertTrue(mon.monsterIsCamouflaged());
    }

    /**
     * C separates the item-mimic from the feature-mimic by {@code mimicked_obj}, but that is
     * {@code monster_is_mimicking}'s business. With no mimicked object at all this predicate still
     * answers true off the flag alone.
     */
    @Test
    @DisplayName("camouflage without a mimicked object still reports true")
    void camouflageWithoutMimickedObject() {
        assertTrue(monsterWith(MonsterFlag.MFLAG_CAMOUFLAGE).monsterIsCamouflaged());
    }

    /**
     * The flag is transient state, cleared when the monster is revealed; the predicate must follow
     * it back down.
     */
    @Test
    @DisplayName("clearing the flag reveals the monster")
    void clearingTheFlagRevealsIt() {
        Monster mon = monsterWith(MonsterFlag.MFLAG_CAMOUFLAGE);
        assertTrue(mon.monsterIsCamouflaged());

        mon.monsterFlagOff(MonsterFlag.MFLAG_CAMOUFLAGE);

        assertFalse(mon.monsterIsCamouflaged());
    }
}
