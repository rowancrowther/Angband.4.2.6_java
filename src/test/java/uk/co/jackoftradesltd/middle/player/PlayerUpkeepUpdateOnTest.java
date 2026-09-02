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
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerUpkeep.updateOn}, the port of C's {@code p->upkeep->update |= (PU_X)}.
 *
 * <p>There is no C function to point at - the operation is written inline wherever a caller changes
 * something a derived quantity depends on. What is being tested is therefore the three properties
 * of {@code |=} that the port has to preserve now that a {@link uk.co.jackoftradesltd.channel.utils.Flag}
 * stands in for the bitfield: it adds rather than replaces, it is idempotent, and it leaves every
 * other flag alone.
 *
 * <p>The return value has no C counterpart at all - {@code |=} yields nothing a caller reads - so it
 * is tested against what the port promises instead: {@code true} for a fresh request, {@code false}
 * for a duplicate. That is the mirror of {@code updateOff}'s answer, and the pairing is asserted
 * directly, since a set-and-clear cycle answering the same way in both directions would make both
 * answers useless.
 *
 * <p>{@code updateOn} and {@code setUpdateFlagOn} are the same operation, one returning the answer
 * and one discarding it. {@link #theTwoRaisingFormsAgree} pins that, because two ways to raise a
 * flag are two chances for them to drift apart.
 *
 * <p>Class PlayerUpkeepUpdateOnTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpkeepUpdateOnTest {

    /**
     * The instance under test, fresh for each test since the flags are mutable.
     */
    private PlayerUpkeep upkeep;

    /**
     * A new upkeep, as the constructor leaves one.
     */
    @BeforeEach
    void newUpkeep() {
        upkeep = new PlayerUpkeep();
    }

    /**
     * A flag raised is a flag pending, which is the whole purpose.
     */
    @Test
    @DisplayName("raising a flag makes it pending")
    void raisingMakesItPending() {
        assertFalse(upkeep.updateHas(PlayerUpdateEnum.PU_BONUS));

        assertTrue(upkeep.updateOn(PlayerUpdateEnum.PU_BONUS));

        assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_BONUS));
    }

    /**
     * C's {@code |=} adds, and several parts of a turn each raise their own flags before the update
     * pass runs, so a second request must not displace the first.
     */
    @Test
    @DisplayName("raising one does not disturb the others")
    void raisingOneLeavesTheRest() {
        upkeep.updateOn(PlayerUpdateEnum.PU_BONUS);
        upkeep.updateOn(PlayerUpdateEnum.PU_HP);
        upkeep.updateOn(PlayerUpdateEnum.PU_INVEN);

        assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_BONUS));
        assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_HP));
        assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_INVEN));
    }

    /**
     * The request carries no count: raising twice before an update pass asks for one recalculation,
     * not two. C has no choice about this, and the second call reports the duplicate.
     */
    @Test
    @DisplayName("raising twice is one request, and says so")
    void raisingIsIdempotent() {
        assertTrue(upkeep.updateOn(PlayerUpdateEnum.PU_BONUS));

        assertFalse(upkeep.updateOn(PlayerUpdateEnum.PU_BONUS),
                "a second raise is a duplicate, not a fresh request");
        assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_BONUS));
    }

    /**
     * The answer must distinguish the two directions of a cycle, or it distinguishes nothing.
     */
    @Test
    @DisplayName("the answer mirrors updateOff across a cycle")
    void theAnswerMirrorsUpdateOff() {
        assertTrue(upkeep.updateOn(PlayerUpdateEnum.PU_TORCH));
        assertTrue(upkeep.updateOff(PlayerUpdateEnum.PU_TORCH));
        assertFalse(upkeep.updateOff(PlayerUpdateEnum.PU_TORCH));
        assertTrue(upkeep.updateOn(PlayerUpdateEnum.PU_TORCH),
                "after a clear the flag is fresh again");
    }

    /**
     * Two ways to raise a flag are two chances to drift apart, so they are checked against each
     * other rather than each on its own.
     */
    @Test
    @DisplayName("updateOn and setUpdateFlagOn are the same operation")
    void theTwoRaisingFormsAgree() {
        upkeep.setUpdateFlagOn(PlayerUpdateEnum.PU_BONUS);

        assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_BONUS));
        assertFalse(upkeep.updateOn(PlayerUpdateEnum.PU_BONUS),
                "setUpdateFlagOn raised it, so updateOn must see a duplicate");
    }

    /**
     * {@code getUpdate} is the port of C's truth test on the whole bitfield, which is what decides
     * whether an update pass runs at all. A raise that did not make it true would be a request the
     * turn loop never looks at.
     */
    @Test
    @DisplayName("a raise makes the whole set pending")
    void aRaiseMakesTheSetPending() {
        assertFalse(upkeep.getUpdate());

        upkeep.updateOn(PlayerUpdateEnum.PU_BONUS);

        assertTrue(upkeep.getUpdate());
    }

    /**
     * Every flag in the enum is storable, so a value missing from the set's backing would show up
     * here rather than as a recalculation that silently never happens.
     */
    @Test
    @DisplayName("every update flag can be raised")
    void everyFlagCanBeRaised() {
        for (PlayerUpdateEnum flag : PlayerUpdateEnum.values()) {
            assertTrue(upkeep.updateOn(flag), flag + " could not be raised");
            assertTrue(upkeep.updateHas(flag), flag + " was not pending after raising");
        }
    }
}
