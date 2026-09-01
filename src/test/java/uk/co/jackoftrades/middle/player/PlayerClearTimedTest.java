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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

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
 * Tests {@code PlayerTimed.playerClearTimed}, the port of C's {@code player_clear_timed}
 * ({@code src/player-timed.c:1127}).
 *
 * <p>Every expectation below is read off that C. The function is one line - two assertions and
 * {@code player_set_timed(p, idx, 0, notify, can_disturb)} - so there is no arithmetic of its own
 * to test. What there is to test is the shape of the delegation, and one point where a reader can
 * reasonably guess wrong.
 *
 * <p><b>The guess worth pinning is the notify flag.</b> Its sibling {@code player_dec_timed}
 * forces {@code notify} to true on the call that finishes an effect, and it would be natural to
 * assume the function whose whole purpose is finishing an effect does the same. It does not: the
 * caller's {@code notify} goes through untouched, which is why {@code game-world.c:1078} can clear
 * {@code TMD_COMMAND} without a word to the player. {@link Notification} exists for that
 * asymmetry, and {@link Notification#aSilentClearIsNotOverridden} is its centre.
 *
 * <p>The return value is not "was it running". C hands back whatever {@code player_set_timed}
 * returns, which is whether the player was notified, so the tests check the counter and the answer
 * separately wherever the two disagree.
 *
 * <p>Zero is a request, not a guarantee. {@code player_set_timed} raises the value it is given to
 * the effect's lower bound before storing it, so an effect whose lower bound is above zero cannot
 * be cleared to zero at all; {@link LowerBound} pins that, since nothing in this method's own text
 * hints at it.
 *
 * <p>Fixtures follow {@code PlayerDecTimedTest}: the registry is loaded by hand with a single
 * definition of {@link #EFFECT}, the grade list always carries the implicit "off" band C's parser
 * inserts at the head, and the registry - global static state shared with the reader suites - is
 * saved and put back around every test.
 *
 * <p>Class PlayerClearTimedTest coded on 260831, commented in full on 260831.
 *
 * @author Rowan Crowther
 */
@ExtendWith(uk.co.jackoftrades.testsupport.SeededPlayerRegistry.class)
@DisplayName("PlayerTimed.playerClearTimed")
class PlayerClearTimedTest {

    /**
     * The effect every fixture defines. It carries no update or redraw flags of its own, which
     * keeps the notification tail off a player who has never been through birth.
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
     * A named band with no message for lapsing into it, so that a drop into it leaves the caller's
     * {@code notify} to stand.
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
     * Loads the registry with one definition of {@link #EFFECT}.
     *
     * @param lowerBound the floor {@code setTimed} coerces every requested value up to
     * @throws Exception if the registry field cannot be reached
     */
    private static void load(int lowerBound) throws Exception {
        PlayerTimedEffect definition = new PlayerTimedEffect(EFFECT, "test effect", "it ends",
                "it grows", "it fades", MessageType.MSG_GENERIC, List.of(),
                List.of(head(), grade(1, 10), grade(2, 30)), null, null, false, lowerBound,
                ObjectFlag.OF_NONE, false, ElementEnum.ELEM_NONE, null, null);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition);
        registryField().set(null, all);
    }

    /**
     * The ordinary fixture: two real bands topping out at ten and thirty, floor at zero.
     *
     * @throws Exception if the registry field cannot be reached
     */
    private static void loadPlain() throws Exception {
        load(0);
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        player = new Player();

        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(new SilentBus());

        // Message coalesces a repeat of the newest entry into a "(x2)" count, and the log is static.
        Field log = uk.co.jackoftrades.middle.Message.class.getDeclaredField("messageLog");
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
     * @param notify     whether the change should be announced
     * @param canDisturb whether a notifying change may interrupt resting
     * @return what the method returned
     */
    private boolean clear(boolean notify, boolean canDisturb) {
        return PlayerTimed.playerClearTimed(player, EFFECT, notify, canDisturb);
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
                () -> PlayerTimed.playerClearTimed(player, TimedEffect.TMD_AFRAID, true, true));
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
     * The counter itself: whatever it held, it ends at the floor, and it gets there in one call
     * regardless of how far away it was.
     */
    @Nested
    @DisplayName("the clearing itself")
    class Clearing {

        @Test
        @DisplayName("a running effect is taken to zero")
        void aRunningEffectGoesToZero() throws Exception {
            loadPlain();
            startAt(9);

            clear(true, true);

            assertEquals(0, stored());
        }

        @Test
        @DisplayName("distance makes no difference - the top of the top band clears in one call")
        void theLongestDurationClearsInOneCall() throws Exception {
            loadPlain();
            startAt(30);

            clear(true, true);

            assertEquals(0, stored());
        }

        @Test
        @DisplayName("an effect that is already off stays off")
        void anIdleEffectStaysOff() throws Exception {
            loadPlain();

            clear(true, true);

            assertEquals(0, stored());
        }

        @Test
        @DisplayName("canDisturb does not change where the counter ends up")
        void canDisturbDoesNotAffectTheCounter() throws Exception {
            loadPlain();
            startAt(12);

            clear(false, false);

            assertEquals(0, stored());
        }
    }

    /**
     * The delegation's own arithmetic is none, but {@code player_set_timed} still applies the
     * effect's lower bound to the zero it is handed. An effect with a floor above zero therefore
     * cannot be cleared to zero, and if it is already sitting on its floor the call is a no-change
     * request and answers false.
     */
    @Nested
    @DisplayName("the lower bound")
    class LowerBound {

        @Test
        @DisplayName("a floor above zero is where clearing actually lands")
        void clearingStopsAtTheFloor() throws Exception {
            load(3);
            startAt(9);

            clear(true, true);

            assertEquals(3, stored());
        }

        @Test
        @DisplayName("clearing an effect already resting on its floor changes nothing")
        void clearingAtTheFloorIsANoChange() throws Exception {
            load(3);
            startAt(3);

            assertFalse(clear(true, true));
            assertEquals(3, stored());
        }
    }

    /**
     * The point of the suite. {@code player_clear_timed} forwards {@code notify} verbatim; it does
     * not do what {@code player_dec_timed} does and force it true because the effect is ending.
     */
    @Nested
    @DisplayName("notification")
    class Notification {

        @Test
        @DisplayName("clearing a running effect with notify true announces it")
        void anAnnouncedClearAnswersTrue() throws Exception {
            loadPlain();
            startAt(8);

            assertTrue(clear(true, true));
            assertEquals(0, stored());
        }

        @Test
        @DisplayName("a silent clear really is silent - notify is not forced true as it is in dec")
        void aSilentClearIsNotOverridden() throws Exception {
            loadPlain();
            startAt(8);

            // player_dec_timed(.., 8, false, ..) from 8 would answer true here; clear does not.
            assertFalse(clear(false, false));
            assertEquals(0, stored());
        }

        @Test
        @DisplayName("the same silence holds when a whole band lapses, since that band has no down message")
        void aSilentClearFromTheTopBandStaysSilent() throws Exception {
            loadPlain();
            startAt(30);

            assertFalse(clear(false, false));
            assertEquals(0, stored());
        }

        @Test
        @DisplayName("clearing an effect that was not running is not an event, whatever notify says")
        void clearingAnIdleEffectAnswersFalse() throws Exception {
            loadPlain();

            assertFalse(clear(true, true));
        }

        @Test
        @DisplayName("a notified clear answers true even when the player may not be disturbed")
        void notifyIsIndependentOfCanDisturb() throws Exception {
            loadPlain();
            startAt(5);

            assertTrue(clear(true, false));
        }
    }
}
