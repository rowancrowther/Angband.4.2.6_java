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
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.EventDataMessage;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.KnownObject;
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
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
 * Tests {@code PlayerTimed.setTimed}, the port of C's {@code player_set_timed}
 * ({@code src/player-timed.c:787}).
 *
 * <p>Every expectation below is read off that C rather than off the Java. The function is a chain of
 * decisions - coerce the value, decide whether anything changed, work out which grade band each end
 * of the change falls in, decide whether to speak and what to say, fire the begin/end chains, store
 * the value, then pay the notification costs - and each decision is pinned separately here.
 *
 * <p><b>The return value is not what most callers assume.</b> C returns {@code notify}, which is
 * whether the player was told, not whether the stored value moved. A silent change that really did
 * alter the counter returns {@code false}; a notifying change whose message field happens to be
 * {@code null} still returns {@code true}. {@link Returning} is the group that fixes that, because a
 * port that returned "did it change" would pass almost every other test in this class.
 *
 * <p><b>Grades are compared by number in C and by list index here.</b> The two agree only because
 * {@code PlayerTimedAssembler} numbers the bands sequentially from an implicit "off" band of maximum
 * {@code 0} at the head - the same band C's parser inserts. The fixtures therefore always build that
 * head band, and {@link Grades} exercises the walk over it: without the head, a counter of zero would
 * not be in a band at all.
 *
 * <p>The method's visible outputs are its return value, the message it raises, the counter it stores
 * and the upkeep flags it sets, so each test reads whichever of those the clause under test decides.
 * Messages are read by standing a capturing bus in front of the real one.
 *
 * <p>{@link PlayerRegistry} is global static state shared with the reader suites, so whatever it held
 * is saved and put back around every test.
 *
 * <p>Class PlayerSetTimedTest coded on 260829, commented in full on 260829.
 *
 * @author Rowan Crowther
 */
@ExtendWith(uk.co.jackoftrades.testsupport.SeededPlayerRegistry.class)
@DisplayName("PlayerTimed.setTimed")
class PlayerSetTimedTest {

    /**
     * The effect every fixture defines, chosen because it carries no update or redraw flags of its
     * own. That keeps the notification tail from recalculating bonuses on a player who has never
     * been through birth, and leaves {@code PR_STATUS} - which C raises unconditionally - as the one
     * redraw flag a test can attribute to this method.
     */
    private static final TimedEffect EFFECT = TimedEffect.TMD_NONE;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The bus this test listens on.
     */
    private CapturingBus bus;

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
     * @param number  its place in the ascending order, counting the head as zero
     * @param max     the highest counter value the band covers
     * @param upMsg   what is said on entering it from below, or {@code null}
     * @param downMsg what is said on entering it from above, or {@code null}
     * @return the band
     */
    private static TimedGrade grade(int number, int max, String upMsg, String downMsg) {
        return new TimedGrade(number, ColourEnum.COLOUR_WHITE, max, "band " + number, upMsg, downMsg);
    }

    /**
     * Loads the registry with a single definition of {@link #EFFECT}.
     *
     * @param onEnd      the message for a change that reaches zero
     * @param onIncrease the message for a rise inside a band
     * @param onDecrease the message for a fall inside a band
     * @param lowerBound the floor the requested value is raised to
     * @param grades     the bands, head first
     * @throws Exception if the registry field cannot be reached
     */
    private static void load(String onEnd, String onIncrease, String onDecrease, int lowerBound,
                             TimedGrade... grades) throws Exception {
        load(onEnd, onIncrease, onDecrease, lowerBound, ObjectFlag.OF_NONE, false,
                ElementEnum.ELEM_NONE, grades);
    }

    /**
     * Loads the registry with a single definition of {@link #EFFECT}, including the two fields that
     * decide whether a change is worth mentioning.
     *
     * @param onEnd      the message for a change that reaches zero
     * @param onIncrease the message for a rise inside a band
     * @param onDecrease the message for a fall inside a band
     * @param lowerBound the floor the requested value is raised to
     * @param oFlagDup   the object flag the status duplicates
     * @param oFlagSyn   whether it is an exact synonym of that flag
     * @param tempResist the element the status temporarily resists
     * @param grades     the bands, head first
     * @throws Exception if the registry field cannot be reached
     */
    private static void load(String onEnd, String onIncrease, String onDecrease, int lowerBound,
                             ObjectFlag oFlagDup, boolean oFlagSyn, ElementEnum tempResist,
                             TimedGrade... grades) throws Exception {
        PlayerTimedEffect definition = new PlayerTimedEffect(EFFECT, "test effect", onEnd,
                onIncrease, onDecrease, MessageType.MSG_GENERIC, List.of(), List.of(grades),
                null, null, false, lowerBound, oFlagDup, oFlagSyn, tempResist, null, null);
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition);
        registryField().set(null, all);
    }

    /**
     * The three-band ladder most tests run on: off at 0, then bands topping out at 10, 20 and 30.
     * Only the middle band carries a down message, which is what lets the "moving down says nothing"
     * case be told apart from the "moving down speaks" one.
     *
     * @throws Exception if the registry field cannot be reached
     */
    private static void loadLadder() throws Exception {
        load("it ends", "it grows", "it fades", 0,
                head(),
                grade(1, 10, "up to one", null),
                grade(2, 20, "up to two", "down to two"),
                grade(3, 30, "up to three", null));
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        player = new Player();

        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        // Message coalesces a repeat of the newest entry into a "(x2)" count, and the log is static,
        // so two tests expecting the same text would collide on whichever ran second.
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
     * Writes the counter directly, so a test can start from a state without going through the method
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
     * @param amount     the requested value
     * @param notify     whether an ordinary change should be announced
     * @param canDisturb whether a notifying change may interrupt resting
     * @return what the method returned
     */
    private boolean set(int amount, boolean notify, boolean canDisturb) {
        return PlayerTimed.setTimed(player, EFFECT, amount, notify, canDisturb);
    }

    /**
     * @return the counter now held for {@link #EFFECT}
     */
    private int stored() {
        return player.getTimedEffect(EFFECT);
    }

    /**
     * An effect the registry has no definition for is a programming error, not a game state: C
     * asserts on the index rather than answering.
     */
    @Test
    @DisplayName("an effect with no loaded definition is rejected")
    void anUnknownEffectThrows() throws Exception {
        loadLadder();

        assertThrows(IllegalArgumentException.class,
                () -> PlayerTimed.setTimed(player, TimedEffect.TMD_STUN, 5, true, true));
    }

    /**
     * A bus that records the message events it is handed instead of forwarding them.
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * The text of every message event seen, in order.
         */
        private final List<String> messages = new ArrayList<>();

        /**
         * The type of every message event seen, in order.
         */
        private final List<MessageType> types = new ArrayList<>();

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
            if (data instanceof EventDataMessage message) {
                messages.add(message.message());
                types.add(message.type());
            }
        }
    }

    /**
     * Coercion of the requested value, and the two ways the method decides nothing has happened.
     */
    @Nested
    @DisplayName("bounds and the no-change exits")
    class Bounds {

        @Test
        @DisplayName("the requested value is raised to the effect's lower bound")
        void lowerBoundRaisesTheValue() throws Exception {
            load(null, null, null, 5, head(), grade(1, 30, null, null));

            set(2, false, false);

            assertEquals(5, stored());
        }

        @Test
        @DisplayName("a negative request is raised to a lower bound of zero")
        void aNegativeRequestBecomesZero() throws Exception {
            loadLadder();
            startAt(5);

            set(-40, false, false);

            assertEquals(0, stored());
        }

        @Test
        @DisplayName("setting the value already held changes nothing and returns false")
        void settingTheCurrentValueIsNoChange() throws Exception {
            loadLadder();
            startAt(7);

            assertFalse(set(7, true, true));
            assertEquals(7, stored());
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("the no-change test runs after the lower bound, not before")
        void theLowerBoundIsAppliedBeforeTheNoChangeTest() throws Exception {
            load(null, null, null, 5, head(), grade(1, 30, "up", "down"));
            startAt(5);

            // C raises v to 5 first, so this is a request to stay where we are, not a fall to 2.
            assertFalse(set(2, true, true));
            assertEquals(5, stored());
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("a request above the top band is capped at that band's maximum")
        void aRequestAboveTheTopIsCapped() throws Exception {
            loadLadder();

            set(500, false, false);

            assertEquals(30, stored());
        }

        @Test
        @DisplayName("asking to exceed the top while already pinned to it returns false")
        void alreadyAtTheTopIsNoChange() throws Exception {
            loadLadder();
            startAt(30);

            assertFalse(set(500, true, true));
            assertEquals(30, stored());
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("asking to exceed the top from below still moves up to it")
        void belowTheTopStillMovesToIt() throws Exception {
            loadLadder();
            startAt(25);

            // 25 and 30 are both in the top band, so nothing is said and C's return - which is
            // notify, not "did it change" - stays false even though the counter moved.
            assertFalse(set(500, false, false));
            assertEquals(30, stored());
        }
    }

    /**
     * The band walk, and the messages a change of band forces.
     */
    @Nested
    @DisplayName("grade transitions")
    class Grades {

        @Test
        @DisplayName("going up a band speaks its up message even when not asked to notify")
        void goingUpAlwaysSpeaks() throws Exception {
            loadLadder();
            startAt(5);

            assertTrue(set(15, false, false));
            assertEquals(List.of("up to two"), bus.messages);
        }

        @Test
        @DisplayName("a band's up message is the one for the band being entered")
        void theUpMessageIsTheDestinationBands() throws Exception {
            loadLadder();

            set(25, false, false);

            assertEquals(List.of("up to three"), bus.messages);
        }

        @Test
        @DisplayName("skipping a band still speaks only the destination band's message")
        void skippingBandsSpeaksOnce() throws Exception {
            loadLadder();

            set(30, false, false);

            assertEquals(List.of("up to three"), bus.messages);
        }

        @Test
        @DisplayName("coming down into a band that has a down message speaks it")
        void goingDownSpeaksWhenTheBandHasAMessage() throws Exception {
            loadLadder();
            startAt(25);

            assertTrue(set(15, false, false));
            assertEquals(List.of("down to two"), bus.messages);
        }

        @Test
        @DisplayName("coming down into a band with no down message says nothing and does not notify")
        void goingDownIsSilentWithoutAMessage() throws Exception {
            loadLadder();
            startAt(15);

            assertFalse(set(5, false, false));
            assertTrue(bus.messages.isEmpty());
            assertEquals(5, stored());
        }

        @Test
        @DisplayName("a counter of zero sits in the head band, so leaving zero is a rise in band")
        void zeroIsTheHeadBand() throws Exception {
            loadLadder();

            assertTrue(set(1, false, false));
            assertEquals(List.of("up to one"), bus.messages);
        }

        @Test
        @DisplayName("a value exactly on a band's maximum is still inside that band")
        void aBoundaryValueStaysInTheLowerBand() throws Exception {
            loadLadder();
            startAt(5);

            // 10 is band one's maximum, so this is a rise within band one, not into band two.
            assertFalse(set(10, false, false));
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("one past a band's maximum enters the next band")
        void onePastABoundaryEntersTheNextBand() throws Exception {
            loadLadder();
            startAt(5);

            assertTrue(set(11, false, false));
            assertEquals(List.of("up to two"), bus.messages);
        }

        @Test
        @DisplayName("a null up message leaves a band change silent but still notifying")
        void aBandChangeWithNoUpMessageStillNotifies() throws Exception {
            load(null, null, null, 0, head(), grade(1, 30, null, null));

            assertTrue(set(5, false, false));
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * What is said when the band does not change and the caller asked to be told.
     */
    @Nested
    @DisplayName("messages within a band")
    class WithinABand {

        @Test
        @DisplayName("a change to zero speaks the end message as a recovery")
        void reachingZeroSpeaksTheEndMessage() throws Exception {
            // A single band, so falling to zero is a fall into the head band, which has no down
            // message - the notify branch is the one that gets to speak.
            load("it ends", "it grows", "it fades", 0, head(), grade(1, 30, "up", null));
            startAt(5);

            assertTrue(set(0, true, false));
            assertEquals(List.of("it ends"), bus.messages);
            assertEquals(List.of(MessageType.MSG_RECOVER), bus.types);
        }

        @Test
        @DisplayName("a fall inside a band speaks the decrease message with the effect's type")
        void fallingSpeaksTheDecreaseMessage() throws Exception {
            loadLadder();
            startAt(8);

            assertTrue(set(3, true, false));
            assertEquals(List.of("it fades"), bus.messages);
            assertEquals(List.of(MessageType.MSG_GENERIC), bus.types);
        }

        @Test
        @DisplayName("a rise inside a band speaks the increase message with the effect's type")
        void risingSpeaksTheIncreaseMessage() throws Exception {
            loadLadder();
            startAt(3);

            assertTrue(set(8, true, false));
            assertEquals(List.of("it grows"), bus.messages);
            assertEquals(List.of(MessageType.MSG_GENERIC), bus.types);
        }

        @Test
        @DisplayName("nothing is said inside a band when notify is false")
        void silentWhenNotAskedToNotify() throws Exception {
            loadLadder();
            startAt(3);

            assertFalse(set(8, false, false));
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("a missing decrease message is simply not spoken")
        void aMissingDecreaseMessageSaysNothing() throws Exception {
            load("it ends", "it grows", null, 0, head(), grade(1, 30, "up", null));
            startAt(8);

            assertTrue(set(3, true, false));
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("a missing increase message is simply not spoken")
        void aMissingIncreaseMessageSaysNothing() throws Exception {
            load("it ends", null, "it fades", 0, head(), grade(1, 30, "up", null));
            startAt(3);

            assertTrue(set(8, true, false));
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("the end message wins over the decrease message when the value reaches zero")
        void zeroIsTestedBeforeTheDirection() throws Exception {
            load("it ends", "it grows", "it fades", 0, head(), grade(1, 30, "up", null));
            startAt(5);

            set(0, true, false);

            assertEquals(List.of("it ends"), bus.messages);
        }
    }

    /**
     * Whether the message is phrased about the wielded weapon or about the player's bare hands.
     */
    @Nested
    @DisplayName("the weapon the message is phrased around")
    class Weapon {

        /**
         * Puts an item in the weapon slot, standing in for the wield code.
         *
         * @param item the weapon to wear
         * @throws Exception if the slot's field cannot be reached
         */
        private void wieldWeapon(ItemObject item) throws Exception {
            EquipSlot slot = null;
            for (EquipSlot candidate : player.getPlayerBody().getSlots()) {
                if (candidate.getName().equals("weapon")) {
                    slot = candidate;
                    break;
                }
            }
            Field f = EquipSlot.class.getDeclaredField("item");
            f.setAccessible(true);
            f.set(slot, item);
        }

        @Test
        @DisplayName("with no weapon the message is phrased about hands, which are plural")
        void bareHandsArePlural() throws Exception {
            load(null, null, null, 0, head(), grade(1, 30, "your {kind} {is} glowing", null));

            set(5, false, false);

            assertEquals(List.of("your hands are glowing"), bus.messages);
        }

        @Test
        @DisplayName("with a weapon the message is phrased about it, and a single item is singular")
        void aWieldedWeaponIsSingular() throws Exception {
            load(null, null, null, 0, head(), grade(1, 30, "it {is} glowing", null));
            ItemObject sword = new ItemObject();
            sword.setNumber(1);
            wieldWeapon(sword);

            set(5, false, false);

            assertEquals(List.of("it is glowing"), bus.messages);
        }
    }

    /**
     * The two conditions under which C decides the player would learn nothing from being told.
     */
    @Nested
    @DisplayName("suppressing a change the player already knows about")
    class AlreadyKnown {

        /**
         * Installs a knowledge record, which a fresh player does not have - C's {@code obj_k} is
         * built at birth.
         *
         * @return the record installed
         * @throws Exception if the field cannot be reached
         */
        private KnownObject installKnowledge() throws Exception {
            KnownObject knowledge = new KnownObject();
            Field f = Player.class.getDeclaredField("itemKnowledge");
            f.setAccessible(true);
            f.set(player, knowledge);
            return knowledge;
        }

        /**
         * Installs a calculated state, which a fresh player also lacks.
         *
         * @return the state installed
         * @throws Exception if the field cannot be reached
         */
        private PlayerState installState() throws Exception {
            PlayerState state = new PlayerState();
            Field f = Player.class.getDeclaredField("state");
            f.setAccessible(true);
            f.set(player, state);
            return state;
        }

        /**
         * Installs a class contributing no flags. {@code playerOfHasNotTimed} folds the class's
         * innate flags in through {@code playerFlags}, and a fresh player has no class - one is
         * chosen at birth.
         *
         * @throws Exception if the field cannot be reached
         */
        private void installEmptyClass() throws Exception {
            PlayerClass empty = new PlayerClass("Test Class", List.of(), Map.of(), Map.of(),
                    Map.of(), 0, 0, new uk.co.jackoftrades.channel.utils.Flag<>(ObjectFlag.class),
                    new uk.co.jackoftrades.channel.utils.Flag<>(
                            uk.co.jackoftrades.middle.player.enums.PlayerFlag.class),
                    0, 0, 0, List.of(), null);
            Field f = Player.class.getDeclaredField("playerClass");
            f.setAccessible(true);
            f.set(player, empty);
        }

        @Test
        @DisplayName("a resistance the player knows they are already immune to is not announced")
        void aKnownImmunitySilencesTheChange() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_NONE, false,
                    ElementEnum.ELEM_FIRE, head(), grade(1, 30, "up", null));
            installKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            installState().setResLevel(ElementEnum.ELEM_FIRE, 3);
            startAt(3);

            assertFalse(set(8, true, false));
            assertTrue(bus.messages.isEmpty());
            assertEquals(8, stored());
        }

        @Test
        @DisplayName("knowing the resistance without being immune leaves the change announced")
        void knowledgeWithoutImmunityDoesNotSilence() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_NONE, false,
                    ElementEnum.ELEM_FIRE, head(), grade(1, 30, "up", null));
            installKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            installState().setResLevel(ElementEnum.ELEM_FIRE, 1);
            startAt(3);

            assertTrue(set(8, true, false));
            assertEquals(List.of("it grows"), bus.messages);
        }

        @Test
        @DisplayName("being immune without knowing it leaves the change announced")
        void immunityWithoutKnowledgeDoesNotSilence() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_NONE, false,
                    ElementEnum.ELEM_FIRE, head(), grade(1, 30, "up", null));
            installKnowledge();
            installState().setResLevel(ElementEnum.ELEM_FIRE, 3);
            startAt(3);

            assertTrue(set(8, true, false));
            assertEquals(List.of("it grows"), bus.messages);
        }

        @Test
        @DisplayName("a known duplicated object flag the player permanently has is not announced")
        void aKnownDuplicatedFlagSilencesTheChange() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_PROT_FEAR, true,
                    ElementEnum.ELEM_NONE, head(), grade(1, 30, "up", null));
            installKnowledge().learnFlag(ObjectFlag.OF_PROT_FEAR);
            installState();
            installEmptyClass();
            installRaceWith(ObjectFlag.OF_PROT_FEAR);
            startAt(3);

            assertFalse(set(8, true, false));
            assertTrue(bus.messages.isEmpty());
        }

        @Test
        @DisplayName("a duplicated flag that is not an exact synonym is still announced")
        void anInexactSynonymDoesNotSilence() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_PROT_FEAR, false,
                    ElementEnum.ELEM_NONE, head(), grade(1, 30, "up", null));
            installKnowledge().learnFlag(ObjectFlag.OF_PROT_FEAR);
            installState();
            installEmptyClass();
            installRaceWith(ObjectFlag.OF_PROT_FEAR);
            startAt(3);

            assertTrue(set(8, true, false));
            assertEquals(List.of("it grows"), bus.messages);
        }

        @Test
        @DisplayName("a known flag the player does not permanently have is still announced")
        void aFlagTheyDoNotActuallyHaveDoesNotSilence() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_PROT_FEAR, true,
                    ElementEnum.ELEM_NONE, head(), grade(1, 30, "up", null));
            installKnowledge().learnFlag(ObjectFlag.OF_PROT_FEAR);
            installState();
            installEmptyClass();
            installRaceWith();
            startAt(3);

            assertTrue(set(8, true, false));
            assertEquals(List.of("it grows"), bus.messages);
        }

        @Test
        @DisplayName("suppression is overridden by a change of band, which always speaks")
        void aBandChangeOverridesSuppression() throws Exception {
            load("it ends", "it grows", "it fades", 0, ObjectFlag.OF_NONE, false,
                    ElementEnum.ELEM_FIRE, head(),
                    grade(1, 10, "up to one", null),
                    grade(2, 30, "up to two", null));
            installKnowledge().learnResistance(ElementEnum.ELEM_FIRE);
            installState().setResLevel(ElementEnum.ELEM_FIRE, 3);
            startAt(3);

            assertTrue(set(15, true, false));
            assertEquals(List.of("up to two"), bus.messages);
        }

        /**
         * Gives the player a race of its own, carrying the given flags and nothing else.
         *
         * <p>A fresh {@code Player} takes its race from the registry, and that object is shared with
         * every other test in the run. Writing a flag onto it would leak: the case below that asks
         * what happens when the player does <em>not</em> have the flag would then find one another
         * test had granted. So each test that cares gets its own race.
         *
         * @param flags the object flags the race grants innately
         * @throws Exception if the field cannot be reached
         */
        @SuppressWarnings("unchecked")
        private void installRaceWith(ObjectFlag... flags) throws Exception {
            PlayerRace race = uk.co.jackoftrades.testsupport.SeededPlayerRegistry.plainRace(
                    uk.co.jackoftrades.testsupport.SeededPlayerRegistry.humanoidBody());
            Field oFlags = PlayerRace.class.getDeclaredField("oFlags");
            oFlags.setAccessible(true);
            for (ObjectFlag flag : flags) {
                ((uk.co.jackoftrades.channel.utils.Flag<ObjectFlag>) oFlags.get(race)).on(flag);
            }
            Field f = Player.class.getDeclaredField("race");
            f.setAccessible(true);
            f.set(player, race);
        }
    }

    /**
     * What the method returns, which is C's {@code notify} and not "did the value change".
     */
    @Nested
    @DisplayName("the return value")
    class Returning {

        @Test
        @DisplayName("a silent change that really moved the counter returns false")
        void aSilentRealChangeReturnsFalse() throws Exception {
            loadLadder();
            startAt(3);

            assertFalse(set(8, false, false));
            assertEquals(8, stored());
        }

        @Test
        @DisplayName("a notifying change with no message to speak still returns true")
        void aNotifyingChangeWithNoMessageReturnsTrue() throws Exception {
            load(null, null, null, 0, head(), grade(1, 30, null, null));
            startAt(3);

            assertTrue(set(8, true, false));
            assertTrue(bus.messages.isEmpty());
        }
    }

    /**
     * The tail C only runs for a notifying change.
     */
    @Nested
    @DisplayName("the notification tail")
    class Tail {

        @Test
        @DisplayName("a notifying change raises PR_STATUS")
        void notifyingRaisesStatus() throws Exception {
            loadLadder();
            startAt(3);

            set(8, true, false);

            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_STATUS));
        }

        @Test
        @DisplayName("a silent change raises nothing")
        void silentRaisesNothing() throws Exception {
            loadLadder();
            startAt(3);

            set(8, false, false);

            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_STATUS));
            assertFalse(player.getPlayerUpkeep().getUpdate());
        }

        @Test
        @DisplayName("the value is stored whether or not the change is announced")
        void theValueIsStoredEitherWay() throws Exception {
            loadLadder();
            startAt(3);

            set(8, false, false);
            assertEquals(8, stored());

            set(9, true, false);
            assertEquals(9, stored());
        }
    }
}
