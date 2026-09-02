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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerTimed.playerIncTimed}, the port of C's {@code player_inc_timed}
 * ({@code src/player-timed.c:1053}).
 *
 * <p>Every expectation below is read off that C. The function itself is small - three gates and a
 * delegation - so the tests pin the gates and the arithmetic, and lean on {@code setTimed} only for
 * the things C leans on it for: coercion, announcement and the meaning of the answer.
 *
 * <p><b>The return value is not "did it grow".</b> C hands back whatever
 * {@code player_set_timed} returns, which is whether the player was notified. A silent change that
 * really did move the counter answers {@code false}, exactly as a refused one does, so
 * {@link Returning} tests the counter and the answer separately in the cases where they disagree.
 *
 * <p>The two refusals differ in a way only the side effects show. A failed check has consulted
 * {@code player_inc_check} and may have learned from equipment on the way; a blocked non-stacking
 * increase never asks. Both leave the counter alone, so the tests distinguish them by whether
 * {@code check} was set at all.
 *
 * <p>Fixtures follow {@code PlayerSetTimedTest}: the registry is loaded by hand with a single
 * definition of {@link #EFFECT}, the grade list always carries the implicit "off" band C's parser
 * inserts at the head, and the registry - global static state shared with the reader suites - is
 * saved and put back around every test.
 *
 * <p>Class PlayerIncTimedTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry.class)
@DisplayName("PlayerTimed.playerIncTimed")
class PlayerIncTimedTest {

    /**
     * The effect every fixture defines. As in {@code PlayerSetTimedTest} it carries no update or
     * redraw flags of its own, which keeps the notification tail off a player who has never been
     * through birth.
     */
    private static final TimedEffect EFFECT = TimedEffect.TMD_NONE;

    /**
     * A second effect, never defined in the registry, used only as the referent of a timed-effect
     * failure condition. {@code incCheck} reads the player's counter for it and never its
     * definition.
     */
    private static final TimedEffect REFERENT = TimedEffect.TMD_STUN;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The bus the engine had before, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * Whatever the registry held before this test, put back afterwards.
     */
    private Object savedEffects;

    /**
     * @return the registry's private list of loaded timed effects, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField() throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        f.setAccessible(true);
        return f;
    }

    /**
     * The implicit "off" band C's parser puts at the head of every grade list: number zero, maximum
     * zero, no messages.
     *
     * @return the head band
     */
    private static TimedGrade head() {
        return new TimedGrade(0, ColourEnum.COLOUR_DARK, 0, null, null, null);
    }

    /**
     * A named band.
     *
     * @param number its place in the ascending order, counting the head as zero
     * @param max    the highest counter value the band covers
     * @return the band
     */
    private static TimedGrade grade(int number, int max) {
        return new TimedGrade(number, ColourEnum.COLOUR_WHITE, max, "band " + number,
                "up to " + number, null);
    }

    /**
     * Loads the registry with a single definition of {@link #EFFECT}.
     *
     * @param nonStacking whether re-application refuses to stack
     * @param failures    the conditions {@code incCheck} walks
     * @throws Exception if the registry field cannot be reached
     */
    private static void load(boolean nonStacking, List<TimedFailure> failures) throws Exception {
        PlayerTimedEffect definition = new PlayerTimedEffect(EFFECT, "test effect", "it ends",
                "it grows", "it fades", MessageType.MSG_GENERIC, failures,
                List.of(head(), grade(1, 10), grade(2, 30)), null, null, nonStacking, 0,
                ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition);
        registryField().set(null, all);
    }

    /**
     * The ordinary fixture: stacking, and nothing prevents it.
     *
     * @throws Exception if the registry field cannot be reached
     */
    private static void loadPlain() throws Exception {
        load(false, List.of());
    }

    /**
     * A condition that fails while {@link #REFERENT} is running.
     *
     * @return the condition
     */
    private static TimedFailure blockedByReferent() {
        return new TimedFailure(REFERENT, TimedEffectReasonType.TYPE_TIMED_EFFECT);
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        player = new Player();

        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(new SilentBus());

        // Message coalesces a repeat of the newest entry into a "(x2)" count, and the log is static.
        Field log = uk.co.jackoftradesltd.middle.Message.class.getDeclaredField("messageLog");
        log.setAccessible(true);
        ((Deque<?>) log.get(null)).clear();
    }

    @AfterEach
    void tearDown() throws Exception {
        registryField().set(null, savedEffects);
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Writes a counter directly, so a test can start from a state without going through the method
     * under test.
     *
     * @param effect the effect whose counter to write
     * @param value  the counter value to give it
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void startAt(TimedEffect effect, int value) throws Exception {
        Field f = Player.class.getDeclaredField("timed");
        f.setAccessible(true);
        ((Map<TimedEffect, Integer>) f.get(player)).put(effect, value);
    }

    /**
     * Calls the method under test on {@link #EFFECT}.
     *
     * @param amount     how much to add
     * @param notify     whether an ordinary change should be announced
     * @param canDisturb whether a notifying change may interrupt resting
     * @param check      whether the player may resist
     * @return what the method returned
     */
    private boolean inc(int amount, boolean notify, boolean canDisturb, boolean check) {
        return PlayerTimed.playerIncTimed(player, EFFECT, amount, notify, canDisturb, check);
    }

    /**
     * @return the counter now held for {@link #EFFECT}
     */
    private int stored() {
        return player.getTimedEffect(EFFECT);
    }

    /**
     * An effect the registry has no definition for is a programming error, not a game state: C
     * asserts on the index, and the port reaches the same conclusion one call deeper.
     */
    @Test
    @DisplayName("an effect with no loaded definition is rejected")
    void anUnknownEffectThrows() throws Exception {
        loadPlain();

        assertThrows(IllegalArgumentException.class,
                () -> PlayerTimed.playerIncTimed(player, TimedEffect.TMD_AFRAID, 5, true, true, false));
    }

    /**
     * A bus that swallows the events the notification tail raises.
     */
    private static final class SilentBus implements EventsHandler {

        @Override
        public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        }

        @Override
        public void eventRemoveHandlerType(GameEventType eventType) {
        }

        @Override
        public void gameEventDispatch(GameEventType eventType, GameEventData data) {
        }
    }

    /**
     * The arithmetic: C passes {@code p->timed[idx] + v} to {@code player_set_timed}, with no
     * clamping of its own.
     */
    @Nested
    @DisplayName("the increase itself")
    class Increase {

        @Test
        @DisplayName("the amount is added to the current duration")
        void addsToTheCurrentDuration() throws Exception {
            loadPlain();
            startAt(EFFECT, 4);

            inc(3, false, false, false);

            assertEquals(7, stored());
        }

        @Test
        @DisplayName("an increase from nothing starts the effect at the amount asked for")
        void startsFromZero() throws Exception {
            loadPlain();

            inc(6, false, false, false);

            assertEquals(6, stored());
        }

        @Test
        @DisplayName("a negative amount shortens the effect, since nothing here clamps it")
        void aNegativeAmountShortens() throws Exception {
            loadPlain();
            startAt(EFFECT, 10);

            inc(-4, false, false, false);

            assertEquals(6, stored());
        }

        @Test
        @DisplayName("an amount of zero is a no-change request and is refused below")
        void zeroChangesNothing() throws Exception {
            loadPlain();
            startAt(EFFECT, 5);

            assertFalse(inc(0, true, true, false));
            assertEquals(5, stored());
        }

        @Test
        @DisplayName("the total is coerced by the effect's bounds, not by this method")
        void theTotalIsCoercedFurtherDown() throws Exception {
            loadPlain();
            startAt(EFFECT, 25);

            inc(20, false, false, false);

            // The top band tops out at 30, so setTimed caps the requested 45.
            assertEquals(30, stored());
        }

        @Test
        @DisplayName("a negative total is raised to the effect's lower bound of zero")
        void aNegativeTotalBecomesZero() throws Exception {
            loadPlain();
            startAt(EFFECT, 2);

            inc(-9, false, false, false);

            assertEquals(0, stored());
        }
    }

    /**
     * The {@code check} gate: C consults {@code player_inc_check(p, idx, false)} only when
     * {@code check} is set, and answers {@code false} without touching the counter if it vetoes.
     */
    @Nested
    @DisplayName("the resistance check")
    class Check {

        @Test
        @DisplayName("a vetoed effect is refused and the counter is left alone")
        void aVetoRefusesTheIncrease() throws Exception {
            load(false, List.of(blockedByReferent()));
            startAt(REFERENT, 1);
            startAt(EFFECT, 4);

            assertFalse(inc(5, true, true, true));
            assertEquals(4, stored());
        }

        @Test
        @DisplayName("a check that passes lets the increase through")
        void aPassingCheckAllowsTheIncrease() throws Exception {
            load(false, List.of(blockedByReferent()));
            startAt(REFERENT, 0);
            startAt(EFFECT, 4);

            inc(5, false, false, true);

            assertEquals(9, stored());
        }

        @Test
        @DisplayName("without check the conditions are never consulted")
        void checkFalseSkipsTheConditionsEntirely() throws Exception {
            load(false, List.of(blockedByReferent()));
            startAt(REFERENT, 1);
            startAt(EFFECT, 4);

            inc(5, false, false, false);

            assertEquals(9, stored());
        }
    }

    /**
     * The non-stacking gate: C blocks the increase when
     * {@code timed_effects[idx].flags & TMD_FLAG_NONSTACKING} and the effect is already running.
     */
    @Nested
    @DisplayName("non-stacking effects")
    class NonStacking {

        @Test
        @DisplayName("a running non-stacking effect refuses to be extended")
        void aRunningNonStackingEffectIsBlocked() throws Exception {
            load(true, List.of());
            startAt(EFFECT, 3);

            assertFalse(inc(5, true, true, false));
            assertEquals(3, stored());
        }

        @Test
        @DisplayName("a non-stacking effect that is not running starts normally")
        void anIdleNonStackingEffectStarts() throws Exception {
            load(true, List.of());

            inc(5, false, false, false);

            assertEquals(5, stored());
        }

        @Test
        @DisplayName("the block is on the duration being positive, not on the effect existing")
        void theBlockTestsTheCounterNotTheDefinition() throws Exception {
            load(true, List.of());
            startAt(EFFECT, 0);

            inc(2, false, false, false);

            assertEquals(2, stored());
        }

        @Test
        @DisplayName("a running non-stacking effect refuses even a shortening amount")
        void aRunningNonStackingEffectRefusesANegativeAmount() throws Exception {
            load(true, List.of());
            startAt(EFFECT, 8);

            assertFalse(inc(-3, false, false, false));
            assertEquals(8, stored());
        }

        @Test
        @DisplayName("a stacking effect that is already running extends as usual")
        void aStackingEffectExtends() throws Exception {
            loadPlain();
            startAt(EFFECT, 3);

            inc(5, false, false, false);

            assertEquals(8, stored());
        }

        @Test
        @DisplayName("the check runs before the non-stacking test, so a veto wins")
        void aVetoBeatsTheNonStackingBlock() throws Exception {
            load(true, List.of(blockedByReferent()));
            startAt(REFERENT, 1);

            assertFalse(inc(5, true, true, true));
            assertEquals(0, stored());
        }
    }

    /**
     * What the answer means. C returns {@code player_set_timed}'s answer, which is whether the
     * player was notified.
     */
    @Nested
    @DisplayName("the return value")
    class Returning {

        @Test
        @DisplayName("a change that crosses a grade is announced and so answers true")
        void aGradeChangeAnswersTrue() throws Exception {
            loadPlain();

            assertTrue(inc(5, true, true, false));
        }

        @Test
        @DisplayName("a silent change inside one grade answers false even though it happened")
        void aSilentChangeAnswersFalse() throws Exception {
            loadPlain();
            startAt(EFFECT, 5);

            assertFalse(inc(3, false, false, false));
            assertEquals(8, stored());
        }

        @Test
        @DisplayName("a requested announcement inside one grade answers true")
        void anAnnouncedChangeAnswersTrue() throws Exception {
            loadPlain();
            startAt(EFFECT, 5);

            assertTrue(inc(3, true, true, false));
        }

        @Test
        @DisplayName("a grade change announces itself whatever the caller asked for")
        void aGradeChangeOverridesASilentCaller() throws Exception {
            loadPlain();
            startAt(EFFECT, 5);

            assertTrue(inc(10, false, false, false));
            assertEquals(15, stored());
        }
    }
}
