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
import uk.co.jackoftradesltd.channel.messages.data.EventDataColourString;
import uk.co.jackoftradesltd.channel.messages.data.EventDataGrid;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStrings;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.gameinput.DefaultGameInput;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link PlayerCalcs#redrawStuff()}, the port of C's {@code redraw_stuff}
 * ({@code player-calcs.c:2678}).
 *
 * <p>Nothing is painted by the method, so what is worth pinning is which events reach the bus, which
 * flags are cleared, and which of C's four ways out was taken. All of it is read off the C function
 * and its {@code redraw_events} table ({@code player-calcs.c:2634}) rather than off the port: the
 * flag-to-event pairing below is a transcription of that table, with {@code PR_MAP} - which the
 * table deliberately omits - handled separately, as C does, because it carries data. {@code PR_HP}
 * carries data too, but for a port-only reason: C's table treats it like any other flag because its
 * handler, {@code prt_hp}, reads the pair off the shared {@code player} global; the port has no such
 * global on the far side of the core-to-front-end boundary, so {@code redrawStuff} attaches the pair
 * to the signal itself instead.
 *
 * <p>Ordering is only partly checked, and deliberately so. C emits its events in table order; the
 * port iterates the flag set instead, and Rowan has chosen not to reproduce the table's order. What
 * C's structure does guarantee, and what is therefore checked here, is that the map comes after the
 * other events and {@code EVENT_END} comes last of all.
 *
 * <p>The interesting boundaries are the resting/running hack ({@code % 100}, with a pending message
 * or map overriding it) and the hidden-map narrowing to {@code PR_SUBWINDOW}. The two interact: the
 * narrowing happens first, so with the map hidden the two overrides have already been masked out of
 * the snapshot and the hack always returns - a case worth its own test because it is easy to port in
 * the wrong order and hard to notice.
 *
 * <p>Globals are involved ({@link GameState#getCharacterGenerated()}, the {@code GameInput} boundary and
 * the events bus), so all three are set explicitly here and put back afterwards.
 *
 * <p>Class PlayerRedrawStuffTest coded on 260828, commented in full on 260828.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerRedrawStuffTest {

    /**
     * C's {@code redraw_events} table, transcribed: the event each flag signals. {@code PR_MAP} is
     * absent from the C table and so absent here.
     */
    private static final Map<PlayerRedraw, GameEventType> C_TABLE = new EnumMap<>(PlayerRedraw.class);
    /**
     * C's {@code PR_SUBWINDOW} group ({@code player-calcs.h:95-96}), the only flags that survive a
     * hidden map.
     */
    private static final Set<PlayerRedraw> C_SUBWINDOW = Set.of(PlayerRedraw.PR_MONSTER,
            PlayerRedraw.PR_OBJECT, PlayerRedraw.PR_MONLIST, PlayerRedraw.PR_ITEMLIST);

    static {
        C_TABLE.put(PlayerRedraw.PR_MISC, GameEventType.EVENT_RACE_CLASS);
        C_TABLE.put(PlayerRedraw.PR_TITLE, GameEventType.EVENT_PLAYERTITLE);
        C_TABLE.put(PlayerRedraw.PR_LEV, GameEventType.EVENT_PLAYERLEVEL);
        C_TABLE.put(PlayerRedraw.PR_EXP, GameEventType.EVENT_EXPERIENCE);
        C_TABLE.put(PlayerRedraw.PR_STATS, GameEventType.EVENT_STATS);
        C_TABLE.put(PlayerRedraw.PR_ARMOR, GameEventType.EVENT_AC);
        C_TABLE.put(PlayerRedraw.PR_HP, GameEventType.EVENT_HP);
        C_TABLE.put(PlayerRedraw.PR_MANA, GameEventType.EVENT_MANA);
        C_TABLE.put(PlayerRedraw.PR_GOLD, GameEventType.EVENT_GOLD);
        C_TABLE.put(PlayerRedraw.PR_HEALTH, GameEventType.EVENT_MONSTERHEALTH);
        C_TABLE.put(PlayerRedraw.PR_DEPTH, GameEventType.EVENT_DUNGEONLEVEL);
        C_TABLE.put(PlayerRedraw.PR_SPEED, GameEventType.EVENT_PLAYERSPEED);
        C_TABLE.put(PlayerRedraw.PR_STATE, GameEventType.EVENT_STATE);
        C_TABLE.put(PlayerRedraw.PR_STATUS, GameEventType.EVENT_STATUS);
        C_TABLE.put(PlayerRedraw.PR_STUDY, GameEventType.EVENT_STUDYSTATUS);
        C_TABLE.put(PlayerRedraw.PR_DTRAP, GameEventType.EVENT_DETECTIONSTATUS);
        C_TABLE.put(PlayerRedraw.PR_FEELING, GameEventType.EVENT_FEELING);
        C_TABLE.put(PlayerRedraw.PR_LIGHT, GameEventType.EVENT_LIGHT);
        C_TABLE.put(PlayerRedraw.PR_INVEN, GameEventType.EVENT_INVENTORY);
        C_TABLE.put(PlayerRedraw.PR_EQUIP, GameEventType.EVENT_EQUIPMENT);
        C_TABLE.put(PlayerRedraw.PR_MONLIST, GameEventType.EVENT_MONSTERLIST);
        C_TABLE.put(PlayerRedraw.PR_ITEMLIST, GameEventType.EVENT_ITEMLIST);
        C_TABLE.put(PlayerRedraw.PR_MONSTER, GameEventType.EVENT_MONSTERTARGET);
        C_TABLE.put(PlayerRedraw.PR_OBJECT, GameEventType.EVENT_OBJECTTARGET);
        C_TABLE.put(PlayerRedraw.PR_MESSAGE, GameEventType.EVENT_MESSAGE);
    }

    /**
     * The player under test.
     */
    private Player player;

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
     * Writes {@link GameState}'s private {@code characterGenerated} field directly, since
     * {@link GameState} exposes no setter for it.
     *
     * @param value the value to force the field to
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static void setCharacterGenerated(boolean value) throws ReflectiveOperationException {
        Field field = GameState.class.getDeclaredField("characterGenerated");
        field.setAccessible(true);
        field.set(null, value);
    }

    /**
     * A generated character, a visible map and a capturing bus - the ordinary mid-game conditions
     * under which every clause is reachable.
     *
     * <p>A race and class are installed too, real ones in C's own model - a player always has both
     * from the moment of birth ({@code player-birth.c}) - and load-bearing here since the
     * {@code PR_MISC}/{@code PR_TITLE} arms read {@code player.getRace()}/{@code getPlayerClass()}
     * directly; a bare {@code new Player()} would NPE the moment either flag is raised. A wiped
     * state and known state are installed too, for the same reason: C's {@code state}/
     * {@code known_state} are always valid by the time {@code redraw_stuff} can run, since it is
     * {@code update_bonuses} ({@code player-calcs.c}) that both fills them and raises
     * {@code PR_STATS}/{@code PR_ARMOR}, and the port's {@link PlayerCalcs#updateBonuses} does the
     * same (its null-guard before ever touching either) - but this test raises those flags
     * directly, bypassing that guard, so it must supply what the real path would already have set.
     * {@link PlayerState#wipe()} is what populates the per-stat maps {@code getStatUse}/etc. read;
     * a bare {@code new PlayerState()} leaves them empty and would NPE on unboxing just as readily.
     */
    @BeforeEach
    void newPlayer() throws ReflectiveOperationException {
        player = new Player();
        player.setRace(SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody()));
        player.setClass(CalcBonusesFixture.plainClass());
        PlayerState state = new PlayerState();
        state.wipe();
        player.setState(state);
        PlayerState knownState = new PlayerState();
        knownState.wipe();
        player.setKnownState(knownState);

        bus = new CapturingBus();
        realBus = GameEngine.getEventsBusHandler();
        GameEngine.setEventsBusHandler(bus);

        realCharacterGenerated = GameState.getCharacterGenerated();
        setCharacterGenerated(true);
        GameInputHolder.resetInstance();
    }

    /**
     * Puts the globals back, so nothing here decides another class's outcome.
     *
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    @AfterEach
    void restoreGlobals() throws ReflectiveOperationException {
        GameEngine.setEventsBusHandler(realBus);
        setCharacterGenerated(realCharacterGenerated);
        GameInputHolder.resetInstance();
    }

    /**
     * Raises the given redraw flags on the player's upkeep.
     *
     * @param flags the flags to raise
     */
    private void raise(PlayerRedraw... flags) {
        for (PlayerRedraw flag : flags) {
            player.getPlayerUpkeep().setRedrawFlagsOn(flag);
        }
    }

    /**
     * @return the flags left raised on the upkeep
     */
    private Flag<PlayerRedraw> pending() {
        return player.getPlayerUpkeep().getRedrawFlags();
    }

    /**
     * Sets one of the upkeep's counters, neither of which has a setter.
     *
     * @param name  the field to set, {@code restingCounter} or {@code runningCounter}
     * @param value the value to set it to
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private void setCounter(String name, int value) throws ReflectiveOperationException {
        Field field = PlayerUpkeep.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(player.getPlayerUpkeep(), value);
    }

    /**
     * Captures the events signalled during the test, with the data each carried.
     *
     * @author Rowan Crowther
     */
    private static class CapturingBus implements EventsHandler {

        /**
         * Every event type signalled since the bus was installed, in order.
         */
        private final List<GameEventType> events = new ArrayList<>();

        /**
         * The data carried by each of those events, positionally.
         */
        private final List<GameEventData> data = new ArrayList<>();

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
            data.add(eventData);
        }
    }

    /**
     * A hidden map, as when a menu or the character sheet is covering it.
     *
     * @author Rowan Crowther
     */
    private static final class HiddenMapInput extends DefaultGameInput {

        @Override
        public boolean mapIsVisible() {
            return false;
        }
    }

    /**
     * The events sent for the raised flags, and the flags cleared afterwards.
     */
    @Nested
    @DisplayName("signalling")
    class Signalling {

        /**
         * With nothing stale the method returns at C's leading {@code if (!redraw) return;} and the
         * bus is never touched.
         */
        @Test
        @DisplayName("nothing stale means nothing signalled")
        void noFlagsMeansNoEvents() {
            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "no event was signalled");
        }

        /**
         * The ordinary path: one event per raised flag, taken from C's table, then the closing
         * {@code EVENT_END}.
         */
        @Test
        @DisplayName("each raised flag signals its C event, then EVENT_END")
        void raisedFlagsSignalTheirEvents() {
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_GOLD, PlayerRedraw.PR_DEPTH);

            PlayerCalcs.redrawStuff(player);

            assertEquals(Set.of(GameEventType.EVENT_HP, GameEventType.EVENT_GOLD,
                            GameEventType.EVENT_DUNGEONLEVEL, GameEventType.EVENT_END),
                    new LinkedHashSet<>(bus.events));
            assertEquals(5, bus.events.size(),
                    "no event was signalled twice, except PR_HP's second dispatch of its own "
                            + "single EVENT_HP (the hit-point warning option after the pair)");
            assertEquals(GameEventType.EVENT_END, bus.events.get(bus.events.size() - 1),
                    "EVENT_END closes the batch");
        }

        /**
         * Every flag at once, checked against the transcribed C table so that a flag wired to the
         * wrong event is caught. {@code PR_MAP} is the extra one the table omits, and
         * {@code EVENT_PLAYER_NAME} is a second, port-only signal {@code PR_MISC} sends alongside
         * {@code EVENT_RACE_CLASS} - see that constant's own Javadoc for why C's table has no
         * equivalent entry for it. {@code PR_EXP} dispatches twice as well, but under its own
         * single event type rather than a second one - a (current, maximum) pair and a lone
         * display figure cannot share one payload shape, so
         * {@code eventSignalLongStat}/{@code eventSignalLong} each fire once under
         * {@code EVENT_EXPERIENCE} (see {@code RedrawRouter.setExperience}'s Javadoc) - one raw
         * dispatch beyond its one distinct event type. {@code PR_STATS} dispatches five times under
         * its own single {@code EVENT_STATS} - one {@code eventSignalFullStat} per stat, since C's
         * single {@code event_signal(EVENT_STATS)} fans out to five listeners on the UI side, and
         * message-passing has no fan-out to reuse (see {@code PlayerCalcs.redrawStuff}'s
         * {@code PR_STATS} clause Javadoc) - four raw dispatches beyond its one distinct event type.
         * {@code PR_HP} dispatches twice under {@code EVENT_HP} (the hit-point pair, then the
         * warning option C's {@code player_hp_attr} reads) and {@code PR_MANA} three times under
         * {@code EVENT_MANA} (the pair, the first-spell level, and whether the class has any
         * spells at all, which {@code prt_sp} reads) - one and two raw dispatches beyond their
         * single event types.
         * So the raw dispatch count is eight higher than the set of distinct event types: one from
         * {@code PR_EXP}, four from {@code PR_STATS}, one from {@code PR_HP}, two from
         * {@code PR_MANA}.
         */
        @Test
        @DisplayName("every flag maps to the event C's table gives it")
        void everyFlagMapsToItsCEvent() {
            for (PlayerRedraw flag : PlayerRedraw.values()) {
                raise(flag);
            }

            PlayerCalcs.redrawStuff(player);

            Set<GameEventType> expected = new LinkedHashSet<>(C_TABLE.values());
            expected.add(GameEventType.EVENT_MAP);
            expected.add(GameEventType.EVENT_PLAYER_NAME);
            expected.add(GameEventType.EVENT_END);

            assertEquals(expected, new LinkedHashSet<>(bus.events));
            assertEquals(expected.size() + 8, bus.events.size(),
                    "no event was signalled twice, except PR_MISC's second distinct event "
                            + "(EVENT_PLAYER_NAME alongside EVENT_RACE_CLASS), PR_EXP's second "
                            + "dispatch of its own single EVENT_EXPERIENCE, PR_STATS' five "
                            + "dispatches of its own single EVENT_STATS (one per stat), PR_HP's "
                            + "second EVENT_HP and PR_MANA's second and third EVENT_MANA");
        }

        /**
         * C signals the map after the table loop and {@code EVENT_END} after everything; that much
         * of the order is honoured, and is what this checks.
         */
        @Test
        @DisplayName("the map comes after the other events, and EVENT_END last")
        void mapThenEnd() {
            raise(PlayerRedraw.PR_MAP, PlayerRedraw.PR_HP, PlayerRedraw.PR_MESSAGE,
                    PlayerRedraw.PR_MONLIST);

            PlayerCalcs.redrawStuff(player);

            int map = bus.events.indexOf(GameEventType.EVENT_MAP);
            assertEquals(bus.events.size() - 2, map, "the map is the last event before EVENT_END");
            assertEquals(GameEventType.EVENT_END, bus.events.get(bus.events.size() - 1));
        }

        /**
         * The map event is the one carrying data: C's {@code event_signal_point(EVENT_MAP, -1, -1)},
         * its sentinel for "the whole map" rather than one grid.
         */
        @Test
        @DisplayName("the map event carries C's whole-map sentinel")
        void mapCarriesWholeMapSentinel() {
            raise(PlayerRedraw.PR_MAP);

            PlayerCalcs.redrawStuff(player);

            int map = bus.events.indexOf(GameEventType.EVENT_MAP);
            assertEquals(new EventDataGrid(-1, -1), bus.data.get(map));
        }

        /**
         * {@code PR_HP} is special-cased to carry the player's current and maximum hit points -
         * the port's substitute for C's {@code prt_hp} reading {@code p->chp}/{@code p->mhp} off
         * the shared player global, which the far side of the boundary has no access to. Chosen to
         * be distinct and non-symmetric, so a swap of the two arguments cannot pass by accident.
         */
        @Test
        @DisplayName("the HP event carries the player's current and maximum hit points")
        void hpCarriesCurrentAndMaxHitPoints() {
            player.setPlayerMaxHP(30);
            player.setCurrentHP(17);
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            int hp = bus.events.indexOf(GameEventType.EVENT_HP);
            assertEquals(new EventDataStat(17, 30), bus.data.get(hp));
        }

        /**
         * {@code PR_LEV} is special-cased to carry the player's current and maximum character
         * level - the port's substitute for C's {@code prt_level} reading
         * {@code p->lev}/{@code p->max_lev} off the shared player global. Chosen distinct and
         * non-symmetric, so a swap of the two arguments cannot pass by accident.
         */
        @Test
        @DisplayName("the level event carries the player's current and maximum level")
        void levelCarriesCurrentAndMaxLevel() throws ReflectiveOperationException {
            Field level = Player.class.getDeclaredField("level");
            level.setAccessible(true);
            level.set(player, 9);
            player.setMaxLevel(12);
            raise(PlayerRedraw.PR_LEV);

            PlayerCalcs.redrawStuff(player);

            int lev = bus.events.indexOf(GameEventType.EVENT_PLAYERLEVEL);
            assertEquals(new EventDataStat(9, 12), bus.data.get(lev));
        }

        /**
         * {@code PR_EQUIP} is special-cased to carry one {@link AngbandDisplayCharacter} per
         * equipment slot - the port's substitute for C's {@code prt_equippy} reading
         * {@code player->body} directly off the shared global and calling
         * {@code object_attr}/{@code object_char} on each slot's object itself. An occupied slot
         * carries its item's glyph/colour ({@link ItemObject#getItemObjectADC()}); an empty slot
         * carries a blank white space, matching C's {@code obj ? ... : (L' ', COLOUR_WHITE)}
         * fallback.
         */
        @Test
        @DisplayName("the equipment event carries one glyph per slot, blank for an empty one")
        void equipCarriesOneGlyphPerSlotBlankForEmpty() throws ReflectiveOperationException {
            ObjectKind kind = new ObjectKind();
            Field tValueField = ObjectKind.class.getDeclaredField("tValue");
            tValueField.setAccessible(true);
            tValueField.set(kind, TValue.TV_SWORD);
            Field characterField = ObjectKind.class.getDeclaredField("character");
            characterField.setAccessible(true);
            characterField.set(kind, new AngbandDisplayCharacter('|', ColourEnum.COLOUR_WHITE));

            ItemObject sword = new ItemObject();
            Field kindField = ItemObject.class.getDeclaredField("kind");
            kindField.setAccessible(true);
            kindField.set(sword, kind);

            Field itemField = EquipSlot.class.getDeclaredField("item");
            itemField.setAccessible(true);
            itemField.set(player.getPlayerBody().getSlot(0), sword);

            raise(PlayerRedraw.PR_EQUIP);

            PlayerCalcs.redrawStuff(player);

            int equip = bus.events.indexOf(GameEventType.EVENT_EQUIPMENT);
            EventDataColourString payload = (EventDataColourString) bus.data.get(equip);
            assertEquals('|', payload.string()[0].getCharacter(),
                    "the occupied slot carries the item's glyph");
            assertEquals(' ', payload.string()[1].getCharacter(), "an empty slot is blank");
            assertEquals(ColourEnum.COLOUR_WHITE, payload.string()[1].getAttributeColour());
        }

        /**
         * {@code PR_TITLE} must not throw when the player has no shape - the ordinary state today,
         * since nothing has ported the shapechange effect yet (see {@link Player#getShape}'s own
         * Javadoc). The guard sends an empty string for the shape name rather than letting
         * {@code getShape().getName()} NPE.
         */
        @Test
        @DisplayName("the title event does not NPE and sends an empty shape name when the player has no shape")
        void titleArmDoesNotNpeWithNoShape() {
            raise(PlayerRedraw.PR_TITLE);

            PlayerCalcs.redrawStuff(player);

            int title = bus.events.indexOf(GameEventType.EVENT_PLAYERTITLE);
            EventDataStrings payload = (EventDataStrings) bus.data.get(title);
            assertEquals("", payload.strings()[3], "no shape means an empty shape name, not an NPE");
        }

        /**
         * Once a shape is set, {@code PR_TITLE} carries its real name as the fourth string -
         * the port's substitute for C's {@code prt_title}/{@code fmt_title} reading
         * {@code player->shape->name} off the shared global.
         */
        @Test
        @DisplayName("the title event carries the real shape name once one is set")
        void titleArmCarriesTheRealShapeName() {
            player.setShape(new PlayerShape("bat", 0, 0, 0, Map.of(),
                    new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                    Map.of(), Map.of(), List.of(), 1, List.of()));
            raise(PlayerRedraw.PR_TITLE);

            PlayerCalcs.redrawStuff(player);

            int title = bus.events.indexOf(GameEventType.EVENT_PLAYERTITLE);
            EventDataStrings payload = (EventDataStrings) bus.data.get(title);
            assertEquals("bat", payload.strings()[3]);
        }

        /**
         * C clears exactly the flags it acted on ({@code p->upkeep->redraw &= ~redraw}).
         */
        @Test
        @DisplayName("the flags acted on are cleared")
        void actedFlagsAreCleared() {
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_MAP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(pending().isEmpty(), "nothing is left pending");
        }

        /**
         * Because C clears the snapshot rather than the live field, a flag raised by a handler
         * during the pass survives it. Here the bus dirties the gold display on seeing the hit
         * points change.
         */
        @Test
        @DisplayName("a flag raised during the pass survives it")
        void flagRaisedDuringPassSurvives() {
            GameEngine.setEventsBusHandler(new CapturingBus() {
                @Override
                public void gameEventDispatch(GameEventType eventType, GameEventData eventData) {
                    super.gameEventDispatch(eventType, eventData);
                    if (eventType == GameEventType.EVENT_HP) {
                        player.getPlayerUpkeep().setRedrawFlagsOn(PlayerRedraw.PR_GOLD);
                    }
                }
            });
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(pending().has(PlayerRedraw.PR_GOLD), "the new flag is still pending");
            assertFalse(pending().has(PlayerRedraw.PR_HP), "the handled flag was cleared");
        }

        /**
         * The snapshot is the caller's own, so the pass cannot be derailed by the upkeep's set
         * changing under it - and asking twice gives two objects.
         */
        @Test
        @DisplayName("the snapshot is not the upkeep's own set")
        void snapshotIsACopy() {
            raise(PlayerRedraw.PR_HP);

            Flag<PlayerRedraw> first = pending();
            Flag<PlayerRedraw> second = pending();

            assertFalse(first == second, "each call gives its own snapshot");
            first.off(PlayerRedraw.PR_HP);
            assertTrue(pending().has(PlayerRedraw.PR_HP), "the upkeep kept its flag");
        }
    }

    /**
     * C's two early returns, and the narrowing that looks like one but is not.
     */
    @Nested
    @DisplayName("guards")
    class Guards {

        /**
         * Before the character exists C returns without signalling, leaving the work for the first
         * pass after birth.
         */
        @Test
        @DisplayName("no character means nothing signalled and nothing cleared")
        void ungeneratedCharacterDoesNothing() throws ReflectiveOperationException {
            setCharacterGenerated(false);
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_MAP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP), "the flags are still pending");
            assertTrue(pending().has(PlayerRedraw.PR_MAP));
        }

        /**
         * A hidden map narrows the snapshot to C's {@code PR_SUBWINDOW}: the detachable panes still
         * refresh, everything else stays pending, and there is no {@code EVENT_END}.
         */
        @Test
        @DisplayName("a hidden map refreshes the subwindows only")
        void hiddenMapDoesSubwindowsOnly() {
            GameInputHolder.setInstance(new HiddenMapInput());
            for (PlayerRedraw flag : PlayerRedraw.values()) {
                raise(flag);
            }

            PlayerCalcs.redrawStuff(player);

            Set<GameEventType> expected = new LinkedHashSet<>();
            for (PlayerRedraw flag : C_SUBWINDOW) {
                expected.add(C_TABLE.get(flag));
            }
            assertEquals(expected, new LinkedHashSet<>(bus.events));
            assertFalse(bus.events.contains(GameEventType.EVENT_END),
                    "the batch is not closed when only subwindows were refreshed");

            for (PlayerRedraw flag : PlayerRedraw.values()) {
                assertEquals(!C_SUBWINDOW.contains(flag), pending().has(flag),
                        flag + " pending after a hidden-map pass");
            }
        }

        /**
         * With the map hidden and nothing in the subwindow group raised, the narrowed snapshot is
         * empty: the loop signals nothing, the clear removes nothing, and every flag is still
         * waiting for the map to come back.
         */
        @Test
        @DisplayName("a hidden map with no subwindow flags leaves everything pending")
        void hiddenMapWithNoSubwindowFlags() {
            GameInputHolder.setInstance(new HiddenMapInput());
            raise(PlayerRedraw.PR_HP, PlayerRedraw.PR_MAP, PlayerRedraw.PR_MESSAGE);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP));
            assertTrue(pending().has(PlayerRedraw.PR_MAP));
            assertTrue(pending().has(PlayerRedraw.PR_MESSAGE));
        }
    }

    /**
     * C's speed hack: while resting or running, refresh only every hundredth turn.
     */
    @Nested
    @DisplayName("the resting and running hack")
    class RestingHack {

        /**
         * Mid-rest, an ordinary flag waits: C returns while the resting counter is not a multiple
         * of a hundred.
         */
        @Test
        @DisplayName("mid-rest an ordinary flag is left pending")
        void midRestSkipsTheRedraw() throws ReflectiveOperationException {
            setCounter("restingCounter", 50);
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP), "the flag is still pending");
        }

        /**
         * The running counter is the other half of C's {@code ||} and skips the redraw on its own.
         */
        @Test
        @DisplayName("mid-run an ordinary flag is left pending")
        void midRunSkipsTheRedraw() throws ReflectiveOperationException {
            setCounter("runningCounter", 7);
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP));
        }

        /**
         * Every hundredth turn the hack lets a pass through - the boundary the {@code % 100} draws.
         */
        @Test
        @DisplayName("on the hundredth turn the redraw happens")
        void hundredthTurnRedraws() throws ReflectiveOperationException {
            setCounter("restingCounter", 100);
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.contains(GameEventType.EVENT_HP), "the redraw happened");
            assertTrue(pending().isEmpty());
        }

        /**
         * A "rest until healed" sentinel is negative, and C's {@code % 100} is negative with it, so
         * the hack fires exactly as it does mid-count. Java's remainder keeps the sign too, which is
         * what makes the two agree.
         */
        @Test
        @DisplayName("a negative rest sentinel counts as mid-rest")
        void negativeRestSentinelSkipsTheRedraw() throws ReflectiveOperationException {
            setCounter("restingCounter", -1);
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "no event was signalled");
            assertTrue(pending().has(PlayerRedraw.PR_HP));
        }

        /**
         * A pending message overrides the hack, and once through, everything else raised is
         * redrawn with it.
         */
        @Test
        @DisplayName("a pending message overrides the hack")
        void pendingMessageOverridesTheHack() throws ReflectiveOperationException {
            setCounter("restingCounter", 50);
            raise(PlayerRedraw.PR_MESSAGE, PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.contains(GameEventType.EVENT_MESSAGE));
            assertTrue(bus.events.contains(GameEventType.EVENT_HP),
                    "the other flags ride through with it");
            assertTrue(pending().isEmpty());
        }

        /**
         * A pending map redraw is the other override.
         */
        @Test
        @DisplayName("a pending map redraw overrides the hack")
        void pendingMapOverridesTheHack() throws ReflectiveOperationException {
            setCounter("runningCounter", 50);
            raise(PlayerRedraw.PR_MAP, PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.contains(GameEventType.EVENT_MAP));
            assertTrue(bus.events.contains(GameEventType.EVENT_HP));
            assertEquals(GameEventType.EVENT_END, bus.events.get(bus.events.size() - 1));
        }

        /**
         * The narrowing happens before the hack, so with the map hidden the two overrides have
         * already been masked out of the snapshot and cannot save the pass. Reversing the two steps
         * would let the subwindows refresh here, which is the divergence this pins.
         */
        @Test
        @DisplayName("a hidden map masks the overrides away before the hack is tested")
        void hiddenMapMasksTheOverridesFirst() throws ReflectiveOperationException {
            GameInputHolder.setInstance(new HiddenMapInput());
            setCounter("restingCounter", 50);
            raise(PlayerRedraw.PR_MESSAGE, PlayerRedraw.PR_MAP, PlayerRedraw.PR_MONLIST);

            PlayerCalcs.redrawStuff(player);

            assertTrue(bus.events.isEmpty(), "the hack returned before the subwindows refreshed");
            assertTrue(pending().has(PlayerRedraw.PR_MONLIST), "even the subwindow flag waits");
        }

        /**
         * Not resting and not running is the ordinary case: both counters are zero, both remainders
         * are zero, and the hack never fires.
         */
        @Test
        @DisplayName("standing still, the hack never fires")
        void standingStillAlwaysRedraws() {
            raise(PlayerRedraw.PR_HP);

            PlayerCalcs.redrawStuff(player);

            assertSame(GameEventType.EVENT_HP, bus.events.get(0));
        }
    }
}
