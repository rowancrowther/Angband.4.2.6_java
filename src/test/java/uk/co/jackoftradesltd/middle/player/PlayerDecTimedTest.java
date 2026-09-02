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
 * Tests {@code PlayerTimed.playerDecTimed}, the port of C's {@code player_dec_timed}
 * ({@code src/player-timed.c:1097}).
 *
 * <p>Every expectation below is read off that C. The function is four lines - a subtraction and a
 * choice between two calls to {@code player_set_timed} - so almost all of it is one rule, and the
 * tests are shaped around that rule: an effect that is finishing announces itself whatever the
 * caller asked for, and an effect that is merely getting shorter obeys the caller. The pairs in
 * {@link Notification} exist to make that difference visible, since the two branches are otherwise
 * indistinguishable from the counter alone.
 *
 * <p><b>The return value is not "did it shrink".</b> C hands back whatever
 * {@code player_set_timed} returns, which is whether the player was notified. A silent decrement
 * that really did move the counter answers {@code false}, so the tests check the counter and the
 * answer separately wherever they disagree.
 *
 * <p>The negative side is deliberate. C forwards {@code p->timed[idx] - v} untouched even when it
 * has gone below zero, and lets {@code player_set_timed}'s lower bound raise it; the port must do
 * the same rather than pre-clamping, because {@code set_timed} compares the value it is given
 * against the stored one to decide whether anything changed. {@link Overshooting} pins that.
 *
 * <p>Fixtures follow {@code PlayerIncTimedTest}: the registry is loaded by hand with a single
 * definition of {@link #EFFECT}, the grade list always carries the implicit "off" band C's parser
 * inserts at the head, and the registry - global static state shared with the reader suites - is
 * saved and put back around every test.
 *
 * <p>Class PlayerDecTimedTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry.class)
@DisplayName("PlayerTimed.playerDecTimed")
class PlayerDecTimedTest {

    /**
     * The effect every fixture defines. As in {@code PlayerIncTimedTest} it carries no update or
     * redraw flags of its own, which keeps the notification tail off a player who has never been
     * through birth.
     */
    private static final TimedEffect EFFECT = TimedEffect.TMD_NONE;

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
     * Loads the registry with a single definition of {@link #EFFECT}: two real bands, topping out
     * at ten and thirty.
     *
     * @throws Exception if the registry field cannot be reached
     */
    private static void loadPlain() throws Exception {
        PlayerTimedEffect definition = new PlayerTimedEffect(EFFECT, "test effect", "it ends",
                "it grows", "it fades", MessageType.MSG_GENERIC, List.of(),
                List.of(head(), grade(1, 10), grade(2, 30)), null, null, false, 0,
                ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition);
        registryField().set(null, all);
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
     * @param value the counter value to give {@link #EFFECT}
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void startAt(int value) throws Exception {
        Field f = Player.class.getDeclaredField("timed");
        f.setAccessible(true);
        ((Map<TimedEffect, Integer>) f.get(player)).put(EFFECT, value);
    }

    /**
     * Calls the method under test on {@link #EFFECT}.
     *
     * @param amount     how much to take off
     * @param notify     whether an ordinary change should be announced
     * @param canDisturb whether a notifying change may interrupt resting
     * @return what the method returned
     */
    private boolean dec(int amount, boolean notify, boolean canDisturb) {
        return PlayerTimed.playerDecTimed(player, EFFECT, amount, notify, canDisturb);
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
                () -> PlayerTimed.playerDecTimed(player, TimedEffect.TMD_AFRAID, 5, true, true));
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
     * The arithmetic: C passes {@code p->timed[idx] - v} to {@code player_set_timed}, with no
     * clamping of its own.
     */
    @Nested
    @DisplayName("the decrease itself")
    class Decrease {

        @Test
        @DisplayName("the amount is taken off the current duration")
        void subtractsFromTheCurrentDuration() throws Exception {
            loadPlain();
            startAt(9);

            dec(3, false, false);

            assertEquals(6, stored());
        }

        @Test
        @DisplayName("a decrement of one is the turn-by-turn decay in game-world.c")
        void aSingleTickShortensByOne() throws Exception {
            loadPlain();
            startAt(20);

            dec(1, false, true);

            assertEquals(19, stored());
        }

        @Test
        @DisplayName("taking off exactly the current duration ends the effect")
        void anExactDecrementEndsTheEffect() throws Exception {
            loadPlain();
            startAt(10);

            dec(10, false, false);

            assertEquals(0, stored());
        }

        @Test
        @DisplayName("an amount of zero is a no-change request and is refused below")
        void zeroChangesNothing() throws Exception {
            loadPlain();
            startAt(5);

            assertFalse(dec(0, true, true));
            assertEquals(5, stored());
        }

        @Test
        @DisplayName("a negative amount lengthens the effect, since nothing here clamps it")
        void aNegativeAmountLengthens() throws Exception {
            loadPlain();
            startAt(5);

            dec(-4, false, false);

            assertEquals(9, stored());
        }

        @Test
        @DisplayName("a lengthening beyond the top band is coerced by the bounds, not by this method")
        void theTotalIsCoercedFurtherDown() throws Exception {
            loadPlain();
            startAt(25);

            dec(-20, false, false);

            // The top band tops out at 30, so setTimed caps the requested 45.
            assertEquals(30, stored());
        }
    }

    /**
     * C forwards the difference even when it has gone below zero, and leaves the coercion to
     * {@code player_set_timed}'s lower bound. Passing a pre-clamped zero instead would look the
     * same from the counter but is not what C does.
     */
    @Nested
    @DisplayName("taking off more than is there")
    class Overshooting {

        @Test
        @DisplayName("an overshoot ends the effect rather than leaving a negative counter")
        void anOvershootEndsTheEffect() throws Exception {
            loadPlain();
            startAt(5);

            dec(20, false, false);

            assertEquals(0, stored());
        }

        @Test
        @DisplayName("an overshoot does not set the effect to the amount subtracted")
        void anOvershootDoesNotStoreTheAmount() throws Exception {
            loadPlain();
            startAt(5);

            dec(20, false, false);

            assertEquals(0, stored());
            assertFalse(stored() == 20);
        }

        @Test
        @DisplayName("decrementing an effect that is already off changes nothing")
        void decrementingAnIdleEffectDoesNothing() throws Exception {
            loadPlain();

            assertFalse(dec(5, true, true));
            assertEquals(0, stored());
        }
    }

    /**
     * The one rule this method owns: {@code notify} is obeyed while the effect survives, and
     * overridden to true on the call that finishes it. C's comment says so outright - "Obey
     * {@code notify} if not finishing; if finishing, always notify".
     */
    @Nested
    @DisplayName("notification")
    class Notification {

        @Test
        @DisplayName("a silent decrement inside one band answers false even though it happened")
        void aSilentDecrementAnswersFalse() throws Exception {
            loadPlain();
            startAt(8);

            assertFalse(dec(2, false, false));
            assertEquals(6, stored());
        }

        @Test
        @DisplayName("the same decrement answers true when the caller asks to be told")
        void anAnnouncedDecrementAnswersTrue() throws Exception {
            loadPlain();
            startAt(8);

            assertTrue(dec(2, true, true));
            assertEquals(6, stored());
        }

        @Test
        @DisplayName("a silent caller is overridden on the decrement that finishes the effect")
        void finishingAnnouncesItselfAnyway() throws Exception {
            loadPlain();
            startAt(5);

            assertTrue(dec(5, false, false));
            assertEquals(0, stored());
        }

        @Test
        @DisplayName("an overshoot finishes too, so it is announced as well")
        void anOvershootIsAnnounced() throws Exception {
            loadPlain();
            startAt(5);

            assertTrue(dec(20, false, false));
        }

        @Test
        @DisplayName("the override cannot manufacture a notification out of no change at all")
        void theOverrideDoesNotAnnounceANonEvent() throws Exception {
            loadPlain();

            assertFalse(dec(3, false, false));
        }

        @Test
        @DisplayName("dropping a band with no lapse message stays silent for a silent caller")
        void aBandChangeWithNoLapseMessageIsSilent() throws Exception {
            loadPlain();
            startAt(20);

            // C forces notification on a downward gradation only when the band that lapses has a
            // message for it; these bands do not, so the caller's silence stands.
            assertFalse(dec(12, false, false));
            assertEquals(8, stored());
        }
    }
}
