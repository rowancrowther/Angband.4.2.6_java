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
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.GameWorld;
import uk.co.jackoftrades.middle.game.event.EventHandlerInterface;
import uk.co.jackoftrades.middle.game.event.EventsHandler;
import uk.co.jackoftrades.middle.game.gameengine.GameEngine;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.monsters.MonsterUtils;
import uk.co.jackoftrades.middle.player.enums.PlayerRedraw;
import uk.co.jackoftrades.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftrades.testsupport.CalcBonusesFixture;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#handleStuff(Player)}, the port of C's {@code handle_stuff}
 * ({@code player-calcs.c:2728}).
 *
 * <p>C is two guarded calls and nothing else:
 *
 * <pre>
 * if (p-&gt;upkeep-&gt;update) update_stuff(p);
 * if (p-&gt;upkeep-&gt;redraw) redraw_stuff(p);
 * </pre>
 *
 * <p>so the only behaviour there is to pin is which of the two halves runs, and in what order.
 *
 * <p><b>How the guards are observed.</b> Both halves are static on {@link PlayerCalcs}, so nothing
 * can be put in front of them; and both begin by re-testing the very mask their guard tested, so a
 * guard that always fired would be invisible from outside. What is visible is the reading itself: a
 * {@link RecordingUpkeep} notes each call to {@link PlayerUpkeep#getUpdate()} and
 * {@link PlayerUpkeep#getRedrawFlags()}, and the count says which half was entered - one reading of
 * a mask means the guard alone looked at it, two means the half it guards looked at it as well. The
 * sequence of those readings is C's source order, so the ordering falls out of the same record.
 *
 * <p>Both halves then run for real underneath, on a {@link CalcBonusesFixture} plain character.
 * That is what makes the ordering test worth having: {@code calc_hitpoints} raising {@code PR_HP}
 * is not a contrivance of the test but the actual reason C evaluates the redraw guard after
 * {@code update_stuff} returns, and the event reaching the bus on the same pass is the whole point
 * of the arrangement.
 *
 * <p>The recalculation the guard tests are driven with is
 * {@link MonsterUtils#updateMonsters(boolean)}, a chapter-6 stub that does nothing: it gives the
 * suite an update flag whose recalculation dirties no part of the screen, and so a case where the
 * second guard legitimately does not fire. Being static and empty it leaves no mark of its own
 * either, so that the update half ran at all is read from {@link RecordingUpkeep#cleared} - the
 * clause clears its flag before calling, and only a clause that was reached can clear.
 *
 * <p>Globals are involved once the real halves run ({@link GameWorld#characterGenerated}, the
 * {@code GameInput} boundary and the events bus), so all three are set explicitly here and put back
 * afterwards.
 *
 * <p>Class PlayerHandleStuffTest coded on 260828, commented in full on 260828, reworked on 260901
 * for the move of the two halves to {@link PlayerCalcs} and of the monster pass to
 * {@link MonsterUtils}.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerHandleStuffTest {

    /**
     * The reading of the update mask, as {@link RecordingUpkeep} records it.
     */
    private static final String UPDATE_READ = "update?";

    /**
     * The reading of the redraw mask, as {@link RecordingUpkeep} records it.
     */
    private static final String REDRAW_READ = "redraw?";

    /**
     * The hit points every level rolls, so the hit point recalculation has a table to read.
     */
    private static final int HP_PER_LEVEL = 10;

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The player's upkeep, recording which masks were read.
     */
    private RecordingUpkeep upkeep;

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
     *
     * @throws ReflectiveOperationException if a field cannot be reached
     */
    @BeforeEach
    void newPlayer() throws ReflectiveOperationException {
        player = new Player();
        CalcBonusesFixture.plainCharacter(player);

        upkeep = new RecordingUpkeep();
        set("playerUpkeep", upkeep);

        int[] hitPoints = new int[50];
        for (int i = 0; i < hitPoints.length; i++) hitPoints[i] = HP_PER_LEVEL * (i + 1);
        set("playerHP", hitPoints);

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
     * Writes one of {@link Player}'s private fields.
     *
     * @param name  the field
     * @param value the value
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void set(String name, Object value) throws ReflectiveOperationException {
        Field field = Player.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player, value);
    }

    /**
     * Runs the method under test and takes the readings away from the recorder, so that the
     * assertions which follow can read the masks themselves without adding to the record.
     *
     * @return the masks read during the call, in order
     */
    private List<String> handleStuffAndTakeReadings() {
        PlayerCalcs.handleStuff(player);
        return List.copyOf(upkeep.reads);
    }

    /**
     * An upkeep that notes which of the two masks was read, and when.
     *
     * <p>Every answer is the real one - this only listens. A mask read once was read by the guard
     * alone; read twice, the half that guard protects read it too.
     *
     * @author Rowan Crowther
     */
    private static final class RecordingUpkeep extends PlayerUpkeep {

        /**
         * The masks read, in the order they were read.
         */
        private final List<String> reads = new ArrayList<>();

        /**
         * The update flags the update half cleared, in the order it cleared them - the mark left by
         * a recalculation that does nothing else.
         */
        private final List<PlayerUpdateEnum> cleared = new ArrayList<>();

        @Override
        public boolean updateOff(PlayerUpdateEnum flag) {
            cleared.add(flag);
            return super.updateOff(flag);
        }

        @Override
        public boolean getUpdate() {
            reads.add(UPDATE_READ);
            return super.getUpdate();
        }

        @Override
        public Flag<PlayerRedraw> getRedrawFlags() {
            reads.add(REDRAW_READ);
            return super.getRedrawFlags();
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
         * Both of C's bitmasks are zero, so each is read once - by its guard - and neither half is
         * entered.
         */
        @Test
        @DisplayName("nothing pending calls neither half")
        void nothingPending() {
            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, REDRAW_READ), reads,
                    "each guard looked once, and neither half looked again");
            assertTrue(upkeep.cleared.isEmpty(), "no recalculation ran");
            assertTrue(bus.events.isEmpty());
        }

        /**
         * A non-zero update mask and an empty redraw mask: the first guard fires and the second does
         * not. The monster pass is the recalculation used because it dirties nothing, so the redraw
         * mask really is still empty when the second guard reads it.
         */
        @Test
        @DisplayName("an update flag alone calls only the update half")
        void updateOnly() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);

            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, UPDATE_READ, REDRAW_READ), reads);
            assertEquals(List.of(PlayerUpdateEnum.PU_MONSTERS), upkeep.cleared,
                    "the update half ran");
            assertTrue(bus.events.isEmpty(), "and the redraw half did not");
        }

        /**
         * The mirror image: a non-zero redraw mask alone reaches only the second call.
         */
        @Test
        @DisplayName("a redraw flag alone calls only the redraw half")
        void redrawOnly() {
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);

            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, REDRAW_READ, REDRAW_READ), reads);
            assertTrue(upkeep.cleared.isEmpty(), "the update half did not run");
            assertTrue(bus.events.contains(GameEventType.EVENT_GOLD), "the redraw half did");
        }

        /**
         * Both masks non-zero: both halves are entered, recalculation before repaint, as C writes
         * them.
         */
        @Test
        @DisplayName("both flags call both halves, update first")
        void bothInOrder() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_MONSTERS);
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);

            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, UPDATE_READ, REDRAW_READ, REDRAW_READ), reads);
            assertEquals(List.of(PlayerUpdateEnum.PU_MONSTERS), upkeep.cleared);
            assertTrue(bus.events.contains(GameEventType.EVENT_GOLD));
        }

        /**
         * Any raised update flag arms the first guard, not one particular flag - C tests the whole
         * mask.
         */
        @Test
        @DisplayName("any update flag arms the first guard")
        void anyUpdateFlag() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_DISTANCE);

            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, UPDATE_READ, REDRAW_READ), reads);
            assertEquals(List.of(PlayerUpdateEnum.PU_DISTANCE, PlayerUpdateEnum.PU_MONSTERS),
                    upkeep.cleared, "the distance clause subsumed the monster one");
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
         * pass: nothing was pending for the screen on entry, the hit point pass raises
         * {@code PR_HP} as it changes the maximum, and the second guard - read after the update
         * half returns - therefore fires.
         */
        @Test
        @DisplayName("a redraw raised by the update half runs in the same pass")
        void redrawRaisedByUpdateIsServiced() {
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_HP);
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().isEmpty(),
                    "nothing was owed to the screen on entry");
            upkeep.reads.clear();

            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, UPDATE_READ, REDRAW_READ, REDRAW_READ), reads,
                    "the redraw half was entered on the strength of a flag raised by the update "
                            + "half");
            assertTrue(bus.events.contains(PlayerRedraw.PR_HP.getEventType()),
                    "the hit point repaint was signalled on this pass");
            assertFalse(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_HP),
                    "the serviced flag was cleared");
        }

        /**
         * Neither guard clears anything itself - C's two masks are cleared inside the halves, and
         * only for the work those halves actually carried out. With no character generated both
         * halves return at their own first guard, so both flags are still raised afterwards and
         * nothing has been silently consumed on the way past.
         */
        @Test
        @DisplayName("handleStuff clears no flags of its own")
        void clearsNothingItself() {
            GameWorld.characterGenerated = false;
            player.getPlayerUpkeep().setUpdateFlagOn(PlayerUpdateEnum.PU_PANEL);
            player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);

            List<String> reads = handleStuffAndTakeReadings();

            assertEquals(List.of(UPDATE_READ, UPDATE_READ, REDRAW_READ, REDRAW_READ), reads,
                    "both halves were entered");
            assertTrue(player.getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_PANEL),
                    "the update flag was left for a later pass");
            assertTrue(player.getPlayerUpkeep().getRedrawFlags().has(PlayerRedraw.PR_GOLD),
                    "and so was the redraw flag");
            assertTrue(bus.events.isEmpty(), "neither half did any work");
        }
    }
}
