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

package uk.co.jackoftradesltd.middle.player.enums;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.FlagView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link TimedEffect}, the port of C's {@code TMD_*} table ({@code list-player-timed.h}).
 *
 * <p>The enum is a transcription of a C header, so the test that earns its keep is the one that
 * checks the transcription. Each constant carries the screen regions to repaint and the derived
 * quantities to recompute when its counter moves, and a wrong pair is invisible at runtime — the
 * game simply fails to refresh something, or refreshes more than it needed to, with nothing
 * throwing. {@link #expected()} therefore restates the C table by hand, and the flags of every
 * constant are compared against it.
 *
 * <p>Restating rather than deriving is the point: a check written by reading the Java enum would
 * agree with any typo it contained. The list below was transcribed from
 * {@code list-player-timed.h} and should be re-derived from that file, not from
 * {@code TimedEffect.java}, if it ever needs changing.
 *
 * <p>The second group covers the accessors. They hand out the constants' own sets as
 * {@link FlagView}s rather than copying, which is safe because the fields are {@code final} and
 * the return type withholds mutation. The tests pin that as a decision rather than an accident,
 * since a defensive copy is the reflex it replaced.
 *
 * <p>Class TimedEffectTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class TimedEffectTest {

    /**
     * Shorthand for a row whose flags are the overwhelmingly common {@code PR_STATUS} /
     * {@code PU_BONUS} pair.
     *
     * @param effect the constant taking the common flags
     * @return the row for it
     */
    private static Row plain(TimedEffect effect) {
        return new Row(effect, List.of(PlayerRedraw.PR_STATUS), List.of(PlayerUpdateEnum.PU_BONUS));
    }

    /**
     * The C table, transcribed by hand from {@code list-player-timed.h}.
     *
     * <p>Order follows the header, so the list doubles as a check that no constant has been
     * dropped, added or moved.
     *
     * @return every {@code TMD_*} row C declares, in C's order
     */
    private static List<Row> expected() {
        List<Row> rows = new ArrayList<>();
        rows.add(plain(TimedEffect.TMD_FAST));
        rows.add(plain(TimedEffect.TMD_SLOW));
        rows.add(new Row(TimedEffect.TMD_BLIND, List.of(PlayerRedraw.PR_MAP),
                List.of(PlayerUpdateEnum.PU_UPDATE_VIEW, PlayerUpdateEnum.PU_MONSTERS)));
        rows.add(plain(TimedEffect.TMD_PARALYZED));
        rows.add(plain(TimedEffect.TMD_CONFUSED));
        rows.add(plain(TimedEffect.TMD_AFRAID));
        rows.add(new Row(TimedEffect.TMD_IMAGE,
                List.of(PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST),
                List.of(PlayerUpdateEnum.PU_BONUS)));
        rows.add(plain(TimedEffect.TMD_POISONED));
        rows.add(plain(TimedEffect.TMD_CUT));
        rows.add(plain(TimedEffect.TMD_STUN));
        rows.add(plain(TimedEffect.TMD_FOOD));
        rows.add(plain(TimedEffect.TMD_PROTEVIL));
        rows.add(plain(TimedEffect.TMD_INVULN));
        rows.add(plain(TimedEffect.TMD_HERO));
        rows.add(plain(TimedEffect.TMD_SHERO));
        rows.add(plain(TimedEffect.TMD_SHIELD));
        rows.add(plain(TimedEffect.TMD_BLESSED));
        rows.add(new Row(TimedEffect.TMD_SINVIS, List.of(PlayerRedraw.PR_STATUS),
                List.of(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS)));
        rows.add(new Row(TimedEffect.TMD_SINFRA, List.of(PlayerRedraw.PR_STATUS),
                List.of(PlayerUpdateEnum.PU_BONUS, PlayerUpdateEnum.PU_MONSTERS)));
        rows.add(plain(TimedEffect.TMD_OPP_ACID));
        rows.add(plain(TimedEffect.TMD_OPP_ELEC));
        rows.add(plain(TimedEffect.TMD_OPP_FIRE));
        rows.add(plain(TimedEffect.TMD_OPP_COLD));
        rows.add(plain(TimedEffect.TMD_OPP_POIS));
        rows.add(plain(TimedEffect.TMD_OPP_CONF));
        rows.add(plain(TimedEffect.TMD_AMNESIA));
        rows.add(plain(TimedEffect.TMD_TELEPATHY));
        rows.add(plain(TimedEffect.TMD_STONESKIN));
        rows.add(plain(TimedEffect.TMD_TERROR));
        rows.add(plain(TimedEffect.TMD_SPRINT));
        rows.add(plain(TimedEffect.TMD_BOLD));
        rows.add(plain(TimedEffect.TMD_SCRAMBLE));
        rows.add(plain(TimedEffect.TMD_TRAPSAFE));
        rows.add(plain(TimedEffect.TMD_FASTCAST));
        rows.add(plain(TimedEffect.TMD_ATT_ACID));
        rows.add(plain(TimedEffect.TMD_ATT_ELEC));
        rows.add(plain(TimedEffect.TMD_ATT_FIRE));
        rows.add(plain(TimedEffect.TMD_ATT_COLD));
        rows.add(plain(TimedEffect.TMD_ATT_POIS));
        rows.add(plain(TimedEffect.TMD_ATT_CONF));
        rows.add(plain(TimedEffect.TMD_ATT_EVIL));
        rows.add(plain(TimedEffect.TMD_ATT_DEMON));
        rows.add(plain(TimedEffect.TMD_ATT_VAMP));
        rows.add(plain(TimedEffect.TMD_HEAL));
        rows.add(plain(TimedEffect.TMD_COMMAND));
        rows.add(plain(TimedEffect.TMD_ATT_RUN));
        rows.add(plain(TimedEffect.TMD_COVERTRACKS));
        rows.add(plain(TimedEffect.TMD_POWERSHOT));
        rows.add(plain(TimedEffect.TMD_TAUNT));
        rows.add(plain(TimedEffect.TMD_BLOODLUST));
        rows.add(plain(TimedEffect.TMD_BLACKBREATH));
        rows.add(plain(TimedEffect.TMD_STEALTH));
        rows.add(plain(TimedEffect.TMD_FREE_ACT));
        return rows;
    }

    /**
     * Collects a flag set's contents in iteration order.
     *
     * @param flags the set to drain
     * @param <E>   the flag domain
     * @return its members
     */
    private static <E extends Enum<E>> List<E> contentsOf(Iterable<E> flags) {
        List<E> out = new ArrayList<>();
        flags.forEach(out::add);
        return out;
    }

    /**
     * One row of the C table: an effect and the two flag sets it declares.
     *
     * @param effect  the constant under test
     * @param redraws the {@code PR_*} regions C lists for it
     * @param updates the {@code PU_*} recalculations C lists for it
     */
    private record Row(TimedEffect effect, List<PlayerRedraw> redraws, List<PlayerUpdateEnum> updates) {
    }

    /**
     * The transcription of C's table.
     */
    @Nested
    class CTable {

        @Test
        void everyEffectDeclaresTheRedrawFlagsCListsForIt() {
            for (Row row : expected()) {
                assertEquals(row.redraws(), contentsOf(row.effect().getRedrawFlags()),
                        "redraw flags for " + row.effect());
            }
        }

        @Test
        void everyEffectDeclaresTheUpdateFlagsCListsForIt() {
            for (Row row : expected()) {
                assertEquals(row.updates(), contentsOf(row.effect().getUpdateFlags()),
                        "update flags for " + row.effect());
            }
        }

        @Test
        void theEnumHoldsCsEffectsInCsOrderAfterThePortsOwnSentinel() {
            List<TimedEffect> declared = List.of(TimedEffect.values());

            assertEquals(TimedEffect.TMD_NONE, declared.get(0),
                    "the sentinel is the port's own addition and comes first");
            assertEquals(expected().stream().map(Row::effect).toList(),
                    declared.subList(1, declared.size()));
        }

        @Test
        void theSentinelCarriesNoFlagsAtAll() {
            assertTrue(TimedEffect.TMD_NONE.getRedrawFlags().isEmpty());
            assertTrue(TimedEffect.TMD_NONE.getUpdateFlags().isEmpty());
        }

        @Test
        void onlyBlindAndHallucinationRepaintSomethingOtherThanTheStatusLine() {
            List<TimedEffect> unusual = new ArrayList<>();
            for (Row row : expected()) {
                if (!row.redraws().equals(List.of(PlayerRedraw.PR_STATUS))) {
                    unusual.add(row.effect());
                }
            }

            assertEquals(List.of(TimedEffect.TMD_BLIND, TimedEffect.TMD_IMAGE), unusual);
        }
    }

    /**
     * The accessors, which hand out the constants' own sets as read-only views.
     */
    @Nested
    class Accessors {

        /**
         * Pins the no-copy decision.
         *
         * <p>The accessors returned a defensive copy until 260818, when the {@link FlagView}
         * return type made the copy redundant and it was dropped. Asserting identity is how that
         * choice stays deliberate: a copy reintroduced by reflex would fail here, and the reader
         * would be sent to the Javadoc explaining why the field is safe to share — {@code final},
         * written only by the constructor, and unreachable for mutation through the declared type.
         */
        @Test
        void eachCallHandsBackTheConstantsOwnSetRatherThanACopy() {
            assertSame(TimedEffect.TMD_FAST.getRedrawFlags(), TimedEffect.TMD_FAST.getRedrawFlags());
            assertSame(TimedEffect.TMD_FAST.getUpdateFlags(), TimedEffect.TMD_FAST.getUpdateFlags());
        }

        @Test
        void differentConstantsDoNotShareASet() {
            assertNotSame(TimedEffect.TMD_FAST.getRedrawFlags(), TimedEffect.TMD_BLIND.getRedrawFlags());
            assertNotSame(TimedEffect.TMD_FAST.getUpdateFlags(), TimedEffect.TMD_BLIND.getUpdateFlags());
        }

        @Test
        void readingOneConstantsViewDoesNotDisturbAnother() {
            contentsOf(TimedEffect.TMD_BLIND.getRedrawFlags());

            assertEquals(List.of(PlayerRedraw.PR_STATUS),
                    contentsOf(TimedEffect.TMD_FAST.getRedrawFlags()));
        }

        @Test
        void theViewReportsTheSameContentsOnEveryCall() {
            List<PlayerRedraw> first = contentsOf(TimedEffect.TMD_IMAGE.getRedrawFlags());
            List<PlayerRedraw> second = contentsOf(TimedEffect.TMD_IMAGE.getRedrawFlags());

            assertEquals(first, second);
        }
    }
}
