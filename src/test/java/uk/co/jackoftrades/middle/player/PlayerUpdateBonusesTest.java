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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.EventDataMessage;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.CalcBonusesFixture;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#updateBonuses()}, the port of C's {@code update_bonuses}
 * ({@code player-calcs.c:2336-2456}) — the method that recalculates the whole derived state and
 * then reports what moved.
 *
 * <p><b>Almost everything worth testing here is a negative that no other test can see.</b> The
 * method's second half raises update and redraw flags by comparing a freshly derived state against
 * the one the character was carrying. C takes its "before" picture with
 * {@code struct player_state state = p->state;}, a by-value copy of the whole struct; the natural
 * Java transcription of that line aliases instead, and under an alias the recalculation writes
 * straight through into the field, every comparison tests an object against itself, and the entire
 * second half raises nothing at all. Nothing throws. The character sheet simply stops updating, the
 * hit-point recalculation is never queued after a constitution ring goes on, and the first symptom
 * is a wrong number on screen several subsystems away.
 *
 * <p>So the tests below are built around changing exactly one thing between two calls and asserting
 * that the matching flag was raised — each one fails under an aliased "copy" and passes under a real
 * one. The companion test in {@code PlayerStateTest.Copy} pins the copy itself; these pin that this
 * method uses it.
 *
 * <p><b>The first call is a case of its own.</b> C's {@code p->state} is an inline struct member and
 * so exists, zeroed, from the moment the player does; the port's field starts null and is filled
 * here. That makes the first call compare a derived state against a zeroed one and report everything
 * as changed — which is correct rather than merely tolerable, since C behaves identically and a
 * newly created character does need all of it serviced.
 *
 * <p><b>Fixture notes.</b> {@link CalcBonusesFixture} supplies a character that contributes nothing
 * of its own, so anything a test changes is the only thing in the answer. {@link PlayerUpkeep}
 * exposes no reader for the update flags and no setter for partial mode, so both are reached by
 * reflection rather than by widening production code for a test's benefit. {@code Message} signals
 * through the static engine bus, so a capture is installed for the duration — without it the
 * message block would reach an unset bus, and with it the messages can be asserted.
 *
 * <p>Class PlayerUpdateBonusesTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerUpdateBonusesTest {

    /**
     * A constitution high enough to move the compressed stat index off its neutral rung, which is
     * what the hit-point and mana flags are keyed to.
     */
    private static final int STRONG_CON = 18;

    /**
     * A weapon weight no neutral-strength character can hold, in tenths of a pound.
     */
    private static final int UNLIFTABLE = 9999;

    /**
     * The character under test, rebuilt for each test.
     */
    private CalcBonusesFixture fixture;
    private Player player;
    private EventsHandler realBus;
    private CapturingBus bus;

    @BeforeEach
    void setUp() throws ReflectiveOperationException {
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);

        fixture = CalcBonusesFixture.plainCharacter();
        player = fixture.player();
    }

    @AfterEach
    void tearDown() {
        GameEngine.setEventsBusHandler(realBus);
    }

    /**
     * Reads the update flags, which {@link PlayerUpkeep} raises but never exposes.
     *
     * @param flag the flag to test for
     * @return whether it is raised
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private boolean updateAsked(PlayerUpdateEnum flag) throws ReflectiveOperationException {
        Field field = PlayerUpkeep.class.getDeclaredField("updateFlags");
        field.setAccessible(true);
        return ((Flag<PlayerUpdateEnum>) field.get(player.getPlayerUpkeep())).has(flag);
    }

    /**
     * @param flag the flag to test for
     * @return whether a redraw was asked for
     */
    private boolean redrawAsked(PlayerRedraw flag) {
        return player.getPlayerUpkeep().getRedrawFlags().has(flag);
    }

    /**
     * Empties both flag sets, so that what a second call raises can be told apart from what the
     * first one did.
     *
     * <p>The redraw set is cleared through {@link PlayerUpkeep#clearRedrawFlags}, which is the only
     * route to it: {@link PlayerUpkeep#getRedrawFlags} hands back a snapshot the caller owns, so
     * wiping what it returns clears nothing at all. The update set has no such pairing and is
     * reached directly.
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void clearFlags() throws ReflectiveOperationException {
        Field update = PlayerUpkeep.class.getDeclaredField("updateFlags");
        update.setAccessible(true);
        ((Flag<PlayerUpdateEnum>) update.get(player.getPlayerUpkeep())).wipe();
        player.getPlayerUpkeep().clearRedrawFlags(player.getPlayerUpkeep().getRedrawFlags());
        bus.messages.clear();
    }

    /**
     * Puts the character into partial-update mode — C's {@code only_partial}, set around the
     * full-screen rebuild on arriving at a new level.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void partialMode() throws ReflectiveOperationException {
        Field field = PlayerUpkeep.class.getDeclaredField("onlyPartial");
        field.setAccessible(true);
        field.set(player.getPlayerUpkeep(), true);
    }

    /**
     * Sets both state fields to null, the condition a character is in before anything has been
     * calculated.
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    private void uncalculatedCharacter() throws ReflectiveOperationException {
        for (String name : List.of("state", "knownState")) {
            Field field = Player.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(player, null);
        }
    }

    /**
     * Reads one of the player's state fields.
     *
     * @param name the field's name
     * @return the state it holds
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private PlayerState state(String name) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        return (PlayerState) field.get(player);
    }

    /**
     * Captures what the engine would have shown the player.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * The text of every message signalled since the last clear.
         */
        private final List<String> messages = new ArrayList<>();

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
            if (data instanceof EventDataMessage message) messages.add(message.message());
        }
    }

    /**
     * The very first calculation a character ever has, where the "before" picture is a zeroed state
     * rather than a previous one.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the first calculation")
    class FirstCalculation {

        /**
         * C's {@code p->state} is part of the player struct and so exists from birth; the port's
         * starts null. If the method did not fill it, the first call would fail on the copy — and if
         * the zeroed state it fills with had empty stat maps, the first comparison would fail on
         * unboxing instead. Both are covered by asking only that the call survives.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("survives a character whose state has never been calculated")
        void survivesAnUncalculatedCharacter() throws ReflectiveOperationException {
            uncalculatedCharacter();

            assertDoesNotThrow(() -> PlayerCalcs.updateBonuses(player));

            assertAll(
                    () -> assertNotNull(state("state")),
                    () -> assertNotNull(state("knownState")));
        }

        /**
         * Every stat has moved off zero, so the stat redraw and the three flags a stat change feeds
         * are all due. This is what C does with its zeroed struct too, and it is how a newly created
         * character gets its hit points and spells worked out at all.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("reports every stat as changed")
        void reportsEveryStatAsChanged() throws ReflectiveOperationException {
            uncalculatedCharacter();

            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertTrue(redrawAsked(PlayerRedraw.PR_STATS), "stats should be redrawn"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_HP), "hit points should be due"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_MANA), "mana should be due"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_SPELLS), "spells should be due"));
        }

        /**
         * The states the method installs must be the ones it derived, not the ones it started from —
         * C finishes with two {@code memcpy}s for exactly this reason. A method that compared
         * correctly but forgot to install would leave the character permanently on its zeroed state
         * and report everything as changed on every single call.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("installs the states it derived")
        void installsTheDerivedStates() throws ReflectiveOperationException {
            uncalculatedCharacter();
            PlayerCalcs.updateBonuses(player);
            PlayerState first = state("state");

            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertNotSame(first, state("state")),
                    () -> assertEquals(110, state("state").getSpeed()));
        }
    }

    /**
     * Noticing what moved between one calculation and the next — the half of the method an aliased
     * copy would silently disable.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("noticing changes")
    class NoticingChanges {

        /**
         * The keystone test. Constitution is the one stat whose compressed index feeds hit points,
         * so raising it must queue {@code PU_HP} as well as the mana and spell recalculations that
         * any stat change queues. Under an aliased copy the recalculation would overwrite the very
         * state being compared against, all four assertions would find nothing raised, and the
         * character's maximum hit points would never follow their constitution.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("a constitution change queues hit points, mana and spells")
        void constitutionChangeQueuesHitPoints() throws ReflectiveOperationException {
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.stat(Stats.STAT_CON, STRONG_CON);
            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertTrue(redrawAsked(PlayerRedraw.PR_STATS), "stats should be redrawn"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_HP), "hit points should be due"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_MANA), "mana should be due"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_SPELLS), "spells should be due"));
        }

        /**
         * Only constitution reaches hit points. A different stat moving must still queue mana and
         * spells — every stat feeds those — but leaving {@code PU_HP} alone is what stops an
         * unrelated ring from re-clamping the character's hit points.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("another stat changing leaves hit points alone")
        void otherStatsLeaveHitPointsAlone() throws ReflectiveOperationException {
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.stat(Stats.STAT_INT, STRONG_CON);
            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_MANA), "mana should be due"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_SPELLS), "spells should be due"),
                    () -> assertFalse(updateAsked(PlayerUpdateEnum.PU_HP), "hit points should not be"));
        }

        /**
         * A recalculation that finds nothing different must ask for nothing. Most calls are of this
         * kind — the flag is raised generously all over the game and cleared once per turn — so a
         * method that reported a change every time would have the display repainting and the hit
         * points re-clamping continuously.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("an unchanged recalculation asks for nothing")
        void unchangedRecalculationAsksForNothing() throws ReflectiveOperationException {
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertFalse(redrawAsked(PlayerRedraw.PR_STATS), "no stat redraw"),
                    () -> assertFalse(redrawAsked(PlayerRedraw.PR_SPEED), "no speed redraw"),
                    () -> assertFalse(redrawAsked(PlayerRedraw.PR_ARMOR), "no armour redraw"),
                    () -> assertFalse(redrawAsked(PlayerRedraw.PR_INVEN), "no inventory redraw"),
                    () -> assertFalse(updateAsked(PlayerUpdateEnum.PU_HP), "no hit points"),
                    () -> assertFalse(updateAsked(PlayerUpdateEnum.PU_MANA), "no mana"),
                    () -> assertFalse(updateAsked(PlayerUpdateEnum.PU_MONSTERS), "no monster update"),
                    () -> assertFalse(updateAsked(PlayerUpdateEnum.PU_UPDATE_VIEW), "no view update"));
        }

        /**
         * Speed reaches the status line and nothing else, so it asks for a redraw and queues no
         * recalculation.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("a speed change asks for a speed redraw")
        void speedChangeAsksForRedraw() throws ReflectiveOperationException {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.setModifiers(Map.of(ObjectModifier.OM_SPEED, 5));
            fixture.knows(ObjectModifier.OM_SPEED);
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.wear("right hand", ring);
            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertTrue(redrawAsked(PlayerRedraw.PR_SPEED), "speed should be redrawn"),
                    () -> assertFalse(updateAsked(PlayerUpdateEnum.PU_HP), "hit points are unaffected"));
        }

        /**
         * Telepathy changes what the character can see, so the monster list has to be reconsidered
         * rather than merely repainted — C raises {@code PU_MONSTERS} for it and for see-invisible
         * alike.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("gaining telepathy asks for a monster update")
        void telepathyAsksForMonsterUpdate() throws ReflectiveOperationException {
            ItemObject crown = CalcBonusesFixture.item(TValue.TV_CROWN);
            crown.setFlag(ObjectFlag.OF_TELEPATHY);
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.wear("head", crown);
            PlayerCalcs.updateBonuses(player);

            assertTrue(updateAsked(PlayerUpdateEnum.PU_MONSTERS));
        }

        /**
         * A change of light radius changes what is visible, so both the view and the monster list
         * are reconsidered. This is one of the two comparisons that reads a different pair of states
         * from its neighbours: the light radius is taken from the plain state rather than the known
         * one, because how far the character can actually see is a fact about the world and not a
         * matter of what they have identified.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("a light source asks for the view and the monsters to be updated")
        void lightChangeAsksForViewUpdate() throws ReflectiveOperationException {
            ItemObject lantern = CalcBonusesFixture.item(TValue.TV_LIGHT, 500);
            lantern.setFlag(ObjectFlag.OF_LIGHT_2);
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.wear("light", lantern);
            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_UPDATE_VIEW), "the view should be updated"),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_MONSTERS), "monsters should be updated"));
        }

        /**
         * Armour is the other comparison that reads its own pair of states — both sides are the
         * <em>known</em> state, because the armour class on the character sheet is what the
         * character believes it to be rather than what it truly is. A test that equipped unidentified
         * armour would therefore see no redraw, and rightly so; this one identifies it first.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("identified armour asks for an armour redraw")
        void armourChangeAsksForRedraw() throws ReflectiveOperationException {
            ItemObject mail = CalcBonusesFixture.identifiedItem(TValue.TV_SOFT_ARMOR);
            mail.setBaseAC(20);
            CalcBonusesFixture.learnCombatValues(mail);
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.wear("body", mail);
            PlayerCalcs.updateBonuses(player);

            assertTrue(redrawAsked(PlayerRedraw.PR_ARMOR));
        }

        /**
         * The weight limit is derived from strength, and it decides what the character can carry
         * without slowing down — so the inventory display has to be told when it moves.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("a strength change asks for an inventory redraw")
        void strengthChangeAsksForInventoryRedraw() throws ReflectiveOperationException {
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.stat(Stats.STAT_STR, STRONG_CON);
            PlayerCalcs.updateBonuses(player);

            assertTrue(redrawAsked(PlayerRedraw.PR_INVEN));
        }
    }

    /**
     * The messages, and the mode that suppresses them.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("messages")
    class Messages {

        /**
         * Picking up a weapon too heavy to swing is worth saying out loud, and the message is driven
         * by the same before-and-after comparison as the flags — so it is a second witness to the
         * copy being real.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("wielding an unliftable weapon says so")
        void heavyWeaponIsAnnounced() throws ReflectiveOperationException {
            ItemObject sword = CalcBonusesFixture.item(TValue.TV_SWORD);
            sword.setWeight(UNLIFTABLE);
            PlayerCalcs.updateBonuses(player);
            clearFlags();

            fixture.wear("weapon", sword);
            PlayerCalcs.updateBonuses(player);

            assertTrue(bus.messages.stream().anyMatch(m -> m.contains("heavy weapon")),
                    "expected a heavy weapon message, got " + bus.messages);
        }

        /**
         * Partial-update mode is set around the full-screen rebuild on arriving at a new level,
         * where the state is recomputed wholesale rather than in response to anything the character
         * did. Announcing the weapon there would be reporting a change that never happened. The flags
         * are still raised — only the talking is suppressed — which is why the redraw is asserted
         * alongside the silence.
         *
         * @throws ReflectiveOperationException if a field cannot be reached
         */
        @Test
        @DisplayName("partial mode suppresses the message but not the flags")
        void partialModeSuppressesMessagesOnly() throws ReflectiveOperationException {
            ItemObject sword = CalcBonusesFixture.item(TValue.TV_SWORD);
            sword.setWeight(UNLIFTABLE);
            PlayerCalcs.updateBonuses(player);
            clearFlags();
            partialMode();

            fixture.wear("weapon", sword);
            fixture.stat(Stats.STAT_CON, STRONG_CON);
            PlayerCalcs.updateBonuses(player);

            assertAll(
                    () -> assertTrue(bus.messages.isEmpty(), "expected silence, got " + bus.messages),
                    () -> assertTrue(updateAsked(PlayerUpdateEnum.PU_HP), "flags are still raised"));
        }
    }
}
