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
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.game.GameWorld;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#handleStuff()}, the port of C's {@code handle_stuff}
 * ({@code player-calcs.c:2728}).
 *
 * <p>C is two guarded calls and nothing else:
 *
 * <pre>
 * if (p-&gt;upkeep-&gt;update) update_stuff(p);
 * if (p-&gt;upkeep-&gt;redraw) redraw_stuff(p);
 * </pre>
 *
 * <p>so the only behaviour there is to pin is which of the two halves runs, and in what order. Both
 * guards are "is the bitmask non-zero", and the port's two halves have suites of their own
 * ({@link PlayerUpdateStuffTest}, {@link PlayerRedrawStuffTest}), so most of what follows uses a
 * player whose halves record that they were called and do nothing else. That is what makes the
 * guards observable at all - with the real halves running, a guard that always fired would look
 * exactly like a guard that fired correctly, because both halves start by re-testing their own
 * flags and returning.
 *
 * <p>The one case that needs more than recording is the ordering. C evaluates the redraw guard
 * <em>after</em> {@code update_stuff} has returned, and recalculations routinely raise redraw flags
 * for the figures they have just changed ({@code calc_hitpoints} raising {@code PR_HP}, and so on).
 * A flag raised inside the update half is therefore serviced on the same pass, not the next one;
 * that is checked here with the real redraw half, so the event actually reaches the bus.
 *
 * <p>Globals are involved once the real redraw half runs ({@link GameWorld#characterGenerated}, the
 * {@code GameInput} boundary and the events bus), so all three are set explicitly here and put back
 * afterwards.
 *
 * <p>Class PlayerHandleStuffTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerHandleStuffTest {

    /**
     * The player under test.
     */
    private RecordingPlayer player;

    /**
     * The bus installed for the test, capturing every event signalled.
     */
    private CapturingBus bus;

    /**
     * The bus that was installed before the test, put back afterwards.
     */
    private EventsHandler realBus;

    /**
     * Whether a character was generated before the test, put back afterwards.
     */
    private boolean realCharacterGenerated;

    /**
     * A generated character, a visible map and a capturing bus - the ordinary mid-game conditions
     * under which both halves are reachable.
     */
    @BeforeEach
    void newPlayer() {
        player = new RecordingPlayer();

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        realCharacterGenerated = GameWorld.characterGenerated;
        GameWorld.characterGenerated = true;
        GameInputHolder.resetInstance();
    }

    /**
     * Puts the globals back, so nothing here decides another class's outcome.
     */
    @AfterEach
    void restoreGlobals() {
        GameEngine.setEventsBusHandler(realBus);
        GameWorld.characterGenerated = realCharacterGenerated;
        GameInputHolder.resetInstance();
    }

    /**
     * A player that records which half was called, and runs neither of them.
     *
     * <p>{@link #realRedraw} hands the real redraw half back for the ordering test, where it has to
     * actually reach the bus. The update half stays stubbed throughout: the real one dispatches to
     * recalculations that need a fully built character, and none of them is under test here.
     *
     * @author Rowan Crowther
     */
    private static class RecordingPlayer extends Player {

        /**
         * The halves called, in the order they were called.
         */
        private final List<String> calls = new ArrayList<>();

        /**
         * Whether the real redraw half runs underneath the recording.
         */
        private boolean realRedraw;

        /**
         * A flag the update half raises as it runs, standing in for a recalculation that dirties
         * part of the screen; {@code null} for an update that dirties nothing.
         */
        private PlayerRedraw raisedByUpdate;

        @Override
        public void updateStuff() {
            calls.add("update");
            if (raisedByUpdate != null) {
                getPlayerUpkeep().setRedrawFlagsOn(raisedByUpdate);
            }
        }

        @Override
        public void redrawStuff() {
            calls.add("redraw");
            if (realRedraw) super.redrawStuff();
        }
    }

    /**
     * Captures the events signalled during the test.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingBus implements EventsHandler {

        /**
         * Every event type signalled since the bus was installed, in order.
         */
        private final List<GameEventType> events = new ArrayList<>();

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
        public void gameEventDispatch(GameEventType eventType, GameEventData eventData) {
            events.add(eventType);
        }
    }

    /**
     * Which half runs for a given set of pending flags.
     */
    @Nested
    @DisplayName("guards")
    class Guards {

        /**
         * Both of C's bitmasks are zero, so neither call is made.
         */
        @Test
        @DisplayName("nothing pending calls neither half")
        void nothingPending() {
            player.handleStuff();

            assertTrue(player.calls.isEmpty(), "neither half should have been called");
        }

        /**
         * A non-zero update mask and an empty redraw mask: the first guard fires and the second
         * does not.
         */
        @Test
        @DisplayName("an update flag alone calls only the update half")
        void updateOnly() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_BONUS);

            player.handleStuff();

            assertEquals(List.of("update"), player.calls);
        }

        /**
         * The mirror image: a non-zero redraw mask alone reaches only the second call.
         */
        @Test
        @DisplayName("a redraw flag alone calls only the redraw half")
        void redrawOnly() {
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);

            player.handleStuff();

            assertEquals(List.of("redraw"), player.calls);
        }

        /**
         * Both masks non-zero: both calls are made, recalculation before repaint, as C writes them.
         */
        @Test
        @DisplayName("both flags call both halves, update first")
        void bothInOrder() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_HP);
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_HP);

            player.handleStuff();

            assertEquals(List.of("update", "redraw"), player.calls);
        }

        /**
         * Any raised update flag arms the first guard, not one particular flag - C tests the whole
         * mask.
         */
        @Test
        @DisplayName("any update flag arms the first guard")
        void anyUpdateFlag() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);

            player.handleStuff();

            assertEquals(List.of("update"), player.calls);
        }
    }

    /**
     * The consequence of the redraw guard being evaluated after the update half has returned.
     */
    @Nested
    @DisplayName("ordering")
    class Ordering {

        /**
         * A recalculation that dirties part of the screen has its repaint serviced on the same
         * pass: nothing was pending on entry but the update half raises {@code PR_GOLD}, and the
         * second guard - read after that half returns - therefore fires.
         */
        @Test
        @DisplayName("a redraw raised by the update half runs in the same pass")
        void redrawRaisedByUpdateIsServiced() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_HP);
            player.raisedByUpdate = PlayerRedraw.PR_GOLD;

            player.handleStuff();

            assertEquals(List.of("update", "redraw"), player.calls);
        }

        /**
         * The same, with the real redraw half underneath: the event actually reaches the bus and
         * the flag is cleared, so the caller is left with a consistent screen rather than a repaint
         * owed until next time. The update half stays stubbed and simply raises the flag, standing
         * in for the recalculation that would have raised it.
         */
        @Test
        @DisplayName("the event reaches the bus and the flag is cleared")
        void redrawRaisedByUpdateReachesTheBus() {
            player.realRedraw = true;
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_HP);
            player.raisedByUpdate = PlayerRedraw.PR_GOLD;

            player.handleStuff();

            assertTrue(bus.events.contains(GameEventType.EVENT_GOLD),
                    "the gold repaint should have been signalled on this pass");
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_GOLD),
                    "the serviced flag should have been cleared");
        }

        /**
         * Neither guard clears anything itself - C's two masks are cleared inside the halves. With
         * the halves stubbed out, both sets of flags survive the call untouched.
         */
        @Test
        @DisplayName("handleStuff clears no flags of its own")
        void clearsNothingItself() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_BONUS);
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);

            player.handleStuff();

            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_BONUS),
                    "the update flag should be left to the update half");
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_GOLD),
                    "the redraw flag should be left to the redraw half");
        }
    }
}
