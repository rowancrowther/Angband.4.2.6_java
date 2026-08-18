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

package uk.co.jackoftrades.middle.player.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerNotice} and {@link PlayerUpdateEnum}, the ports of C's {@code PN_*} and
 * {@code PU_*} bitflag families ({@code player-calcs.h:28-45}).
 *
 * <p>Both are transcriptions of a list of {@code #define}s, so the check that matters is that the
 * list is complete and in C's order. Completeness is the substantive half: a missing {@code PU_*}
 * means some derived quantity can never be marked stale, and the symptom is a stat that quietly
 * stops updating rather than anything that fails.
 *
 * <p>Order is checked too, though nothing depends on it today — every lookup in the port is by
 * enum identity, not by ordinal. It is asserted because it is free, and because keeping the
 * declaration order aligned with the header is what makes the two readable side by side when the
 * next flag is added upstream.
 *
 * <p>{@code PlayerUpdateEnum} was renamed from {@code PlayerUpkeepEnum} in this commit, which is
 * why it gets a test now: the rename touched every reference to it, and a transcription that
 * survived the rename intact is worth recording.
 *
 * <p>The third group covers the two being used as {@link Flag} domains, since that is how both are
 * actually held — {@link uk.co.jackoftrades.middle.player.PlayerUpkeep} keeps a {@code Flag} over
 * each rather than C's packed {@code uint32_t}.
 *
 * <p>Class PlayerCalcsFlagEnumsTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class PlayerCalcsFlagEnumsTest {

    /**
     * The {@code PN_*} family.
     */
    @Nested
    class Notices {

        @Test
        void holdsCsThreeNoticesInCsOrder() {
            assertEquals(
                    List.of(PlayerNotice.PN_COMBINE, PlayerNotice.PN_IGNORE,
                            PlayerNotice.PN_MON_MESSAGE),
                    List.of(PlayerNotice.values()));
        }

        @Test
        void hasNoExtraConstants() {
            assertEquals(3, PlayerNotice.values().length);
        }
    }

    /**
     * The {@code PU_*} family.
     */
    @Nested
    class Updates {

        @Test
        void holdsCsTenUpdatesInCsOrder() {
            assertEquals(
                    List.of(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_TORCH,
                            PlayerUpdateEnum.PU_HP, PlayerUpdateEnum.PU_MANA,
                            PlayerUpdateEnum.PU_SPELLS, PlayerUpdateEnum.PU_UPDATE_VIEW,
                            PlayerUpdateEnum.PU_MONSTERS, PlayerUpdateEnum.PU_DISTANCE,
                            PlayerUpdateEnum.PU_PANEL, PlayerUpdateEnum.PU_INVEN),
                    List.of(PlayerUpdateEnum.values()));
        }

        @Test
        void hasNoExtraConstants() {
            assertEquals(10, PlayerUpdateEnum.values().length);
        }
    }

    /**
     * Both families as flag-set domains, which is how the upkeep holds them.
     */
    @Nested
    class AsFlagDomains {

        @Test
        void aNoticeSetStartsEmptyAndTakesEveryConstant() {
            Flag<PlayerNotice> notices = new Flag<>(PlayerNotice.class);

            assertTrue(notices.isEmpty());
            notices.setAll();
            assertTrue(notices.isFull());
            assertEquals(PlayerNotice.values().length, notices.count());
        }

        @Test
        void anUpdateSetStartsEmptyAndTakesEveryConstant() {
            Flag<PlayerUpdateEnum> updates = new Flag<>(PlayerUpdateEnum.class);

            assertTrue(updates.isEmpty());
            updates.setAll();
            assertTrue(updates.isFull());
            assertEquals(PlayerUpdateEnum.values().length, updates.count());
        }

        /**
         * The batch raise is additive, as C's {@code |=} is.
         *
         * <p>The reason {@code updateFlagsOn} exists at all: C writes
         * {@code p->upkeep->update |= (PU_BONUS | PU_HP | PU_SPELLS)} inline, and several parts of
         * a turn each ask for their own recalculations before the update pass runs, so a batch
         * that replaced rather than added would lose whichever request came first.
         */
        @Test
        void raisingSeveralUpdatesLeavesEarlierOnesRaised() {
            Flag<PlayerUpdateEnum> updates = new Flag<>(PlayerUpdateEnum.class);
            updates.on(PlayerUpdateEnum.PU_TORCH);

            updates.set(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_HP);

            assertTrue(updates.has(PlayerUpdateEnum.PU_TORCH));
            assertTrue(updates.has(PlayerUpdateEnum.PU_BONUS));
            assertTrue(updates.has(PlayerUpdateEnum.PU_HP));
            assertFalse(updates.has(PlayerUpdateEnum.PU_MANA));
        }
    }
}
