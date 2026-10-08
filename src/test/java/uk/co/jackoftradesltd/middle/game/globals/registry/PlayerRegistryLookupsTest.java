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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.data.EventDataMessage;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.event.EventHandlerInterface;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;
import uk.co.jackoftradesltd.middle.objects.enums.EquipmentSlotsEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.EquipSlot;
import uk.co.jackoftradesltd.middle.player.PlayerBody;
import uk.co.jackoftradesltd.middle.player.PlayerHistoryChart;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the shape, body, race and history-chart side of {@link PlayerRegistry}, added on 261008:
 * the two derived counters, the four lookups, and the list getters. Realms, timed effects and the
 * food thresholds have suites of their own.
 *
 * <p>The expectations come from the C each piece ports, not from the Java:
 *
 * <ul>
 *   <li>{@code finish_parse_body} ({@code init.c}) resets {@code z_info->equip_slots_max} to zero
 *       and raises it to any body's larger {@code count}, so the result is the largest count, and
 *       zero for no bodies.</li>
 *   <li>{@code parse_shape_name} ({@code init.c}) adds one to {@code z_info->shape_max} per shape,
 *       so the counter is the number of shapes; {@code shape.txt} ships nine.</li>
 *   <li>{@code lookup_player_shape} ({@code player-util.c}) takes the first exact {@code streq}
 *       match, and on a miss sends {@code "Could not find %s shape!"} through {@code msg} and
 *       returns {@code NULL}. The shape returned is the list's own record.</li>
 *   <li>The race loop in {@code rd_player} ({@code load.c}) takes the first exact {@code streq}
 *       match and leaves {@code NULL} on a miss.</li>
 *   <li>{@code findchart} ({@code init.c}) takes the first chart whose {@code idx} equals the
 *       number asked for, and returns {@code NULL} at the end of the list.</li>
 *   <li>{@code player_embody} ({@code player-birth.c}) {@code memcpy}s {@code bodies[race->body]}
 *       into the player, so the player's body is a copy, never the shared record.</li>
 * </ul>
 *
 * <p>The port throws {@link IllegalStateException} when a list has not been loaded; C has no
 * counterpart (its lists are simply {@code NULL}), so those cases test the port's own guard.
 *
 * <p>The registry is global static state shared with the other suites, so every field these tests
 * touch is saved and put back around each test, and the engine's event bus is swapped for a
 * capturing one.
 *
 * <p>Class PlayerRegistryLookupsTest coded on 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class PlayerRegistryLookupsTest {

    /**
     * The registry fields these tests write, saved before each test and restored after it.
     */
    private static final String[] FIELDS = {"playerShapes", "playerShapeMax", "playerBodies",
            "playerEquipmentSlotsMax", "playerRaces", "playerHistoryCharts"};

    /**
     * The saved values of {@link #FIELDS}, by name.
     */
    private final Map<String, Object> saved = new HashMap<>();

    /**
     * The engine's real event bus, restored after each test.
     */
    private EventsHandler realBus;

    /**
     * The capturing bus installed for the test.
     */
    private CapturingBus bus;

    /**
     * @param name a private static field of {@link PlayerRegistry}
     * @return that field, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field field(String name) throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * @param name  the body's name
     * @param slots how many slots it has; all are weapon slots, since only the count matters here
     * @return the body
     */
    private static PlayerBody body(String name, int slots) {
        List<EquipSlot> list = new ArrayList<>();
        for (int i = 0; i < slots; i++) list.add(new EquipSlot(EquipmentSlotsEnum.EQUIP_WEAPON, "slot" + i));
        return new PlayerBody(name, list);
    }

    /**
     * @param name the shape's name; nothing else about it is read by the registry
     * @return the shape
     */
    private static PlayerShape shape(String name) {
        return new PlayerShape(name, 0, 0, 0, new HashMap<>(), new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), new HashMap<>(), new HashMap<>(), new ArrayList<>(),
                0, new ArrayList<>());
    }

    /**
     * @return the nine shapes {@code shape.txt} defines, in file order
     */
    private static List<PlayerShape> theGameShapes() {
        List<PlayerShape> list = new ArrayList<>();
        for (String n : List.of("normal", "fox", "Pukel-man", "bear", "eagle", "bat", "warg",
                "vampire", "werewolf")) {
            list.add(shape(n));
        }
        return list;
    }

    // ---- fixtures ----

    /**
     * @param name the race's name; nothing else about it is read by the registry
     * @return the race
     */
    private static PlayerRace race(String name) {
        return new PlayerRace(name, 0, 10, 100, 14, 6, 69, 10, 165, 35, 0, null, Map.of(), Map.of(),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), null, Map.of());
    }

    @BeforeEach
    void snapshot() throws Exception {
        for (String name : FIELDS) saved.put(name, field(name).get(null));
        realBus = GameEngine.getEventsBusHandler();
        bus = new CapturingBus();
        GameEngine.setEventsBusHandler(bus);
    }

    @AfterEach
    void restore() throws Exception {
        for (String name : FIELDS) field(name).set(null, saved.get(name));
        GameEngine.setEventsBusHandler(realBus);
    }

    @Test
    void equipSlotsMaxIsTheShippedHumanoidsTwelve() {
        PlayerRegistry.setPlayerBodies(List.of(body("Humanoid", 12)));

        assertEquals(12, PlayerRegistry.getPlayerEquipmentSlotsMax());
    }

    // ---- bodies and equip_slots_max ----

    @Test
    void equipSlotsMaxIsTheLargestCountWhereverItSits() {
        PlayerRegistry.setPlayerBodies(List.of(body("a", 3), body("b", 12), body("c", 5)));
        assertEquals(12, PlayerRegistry.getPlayerEquipmentSlotsMax());

        PlayerRegistry.setPlayerBodies(List.of(body("a", 3), body("b", 5), body("c", 12)));
        assertEquals(12, PlayerRegistry.getPlayerEquipmentSlotsMax());

        PlayerRegistry.setPlayerBodies(List.of(body("a", 7), body("b", 7)));
        assertEquals(7, PlayerRegistry.getPlayerEquipmentSlotsMax());
    }

    @Test
    void equipSlotsMaxStartsFromZeroOnEveryLoadLikeC() {
        PlayerRegistry.setPlayerBodies(List.of(body("big", 12)));
        PlayerRegistry.setPlayerBodies(List.of(body("small", 3)));
        assertEquals(3, PlayerRegistry.getPlayerEquipmentSlotsMax());

        PlayerRegistry.setPlayerBodies(List.of());
        assertEquals(0, PlayerRegistry.getPlayerEquipmentSlotsMax());
    }

    @Test
    void lookupPlayerBodyHandsBackACopyNotTheSharedRecord() {
        PlayerBody stored = body("Humanoid", 12);
        PlayerRegistry.setPlayerBodies(List.of(stored));

        PlayerBody got = PlayerRegistry.lookupPlayerBody(0);

        assertNotSame(stored, got);
        assertEquals("Humanoid", got.getName());
        assertEquals(12, got.getCount());
        assertNotSame(stored.getSlots().get(0), got.getSlots().get(0));
    }

    @Test
    void lookupPlayerBodyIndexesByPositionInLoadOrder() {
        PlayerRegistry.setPlayerBodies(List.of(body("first", 2), body("second", 4)));

        assertEquals("first", PlayerRegistry.lookupPlayerBody(0).getName());
        assertEquals("second", PlayerRegistry.lookupPlayerBody(1).getName());
    }

    @Test
    void lookupPlayerBodyRejectsAnIndexOffEitherEnd() {
        PlayerRegistry.setPlayerBodies(List.of(body("Humanoid", 12)));

        assertThrows(IndexOutOfBoundsException.class, () -> PlayerRegistry.lookupPlayerBody(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> PlayerRegistry.lookupPlayerBody(1));
    }

    @Test
    void lookupPlayerBodyRefusesToReadBeforeTheBodiesAreLoaded() throws Exception {
        field("playerBodies").set(null, null);

        assertThrows(IllegalStateException.class, () -> PlayerRegistry.lookupPlayerBody(0));
    }

    @Test
    void shapeMaxCountsTheNineShippedShapes() {
        PlayerRegistry.setPlayerShape(theGameShapes());

        assertEquals(9, PlayerRegistry.getPlayerShapeMax());
    }

    // ---- shapes and shape_max ----

    @Test
    void shapeMaxIsZeroForNoShapes() {
        PlayerRegistry.setPlayerShape(theGameShapes());
        PlayerRegistry.setPlayerShape(List.of());

        assertEquals(0, PlayerRegistry.getPlayerShapeMax());
    }

    @Test
    void lookupPlayerShapeFindsTheStoredRecordAndSaysNothing() {
        List<PlayerShape> shapes = theGameShapes();
        PlayerRegistry.setPlayerShape(shapes);

        assertSame(shapes.get(0), PlayerRegistry.lookupPlayerShape("normal"));
        assertSame(shapes.get(3), PlayerRegistry.lookupPlayerShape("bear"));
        assertSame(shapes.get(2), PlayerRegistry.lookupPlayerShape("Pukel-man"));
        assertTrue(bus.types.isEmpty());
    }

    @Test
    void lookupPlayerShapeComparesCaseExactlyBecauseCUsesStreq() {
        PlayerRegistry.setPlayerShape(theGameShapes());

        assertNull(PlayerRegistry.lookupPlayerShape("Bear"));
        assertNull(PlayerRegistry.lookupPlayerShape("pukel-man"));
        assertNull(PlayerRegistry.lookupPlayerShape("bea"));
    }

    @Test
    void lookupPlayerShapeMissSendsCsMessage() {
        PlayerRegistry.setPlayerShape(theGameShapes());

        assertNull(PlayerRegistry.lookupPlayerShape("dragon"));

        assertEquals(List.of(GameEventType.EVENT_MESSAGE), bus.types);
        EventDataMessage sent = assertInstanceOf(EventDataMessage.class, bus.payloads.get(0));
        assertEquals("Could not find dragon shape!", sent.message());
    }

    @Test
    void lookupPlayerShapeTakesTheFirstMatch() {
        PlayerShape first = shape("bear");
        PlayerRegistry.setPlayerShape(List.of(shape("fox"), first, shape("bear")));

        assertSame(first, PlayerRegistry.lookupPlayerShape("bear"));
    }

    @Test
    void lookupPlayerShapeRefusesToReadBeforeTheShapesAreLoaded() throws Exception {
        field("playerShapes").set(null, null);

        assertThrows(IllegalStateException.class, () -> PlayerRegistry.lookupPlayerShape("normal"));
    }

    @Test
    void lookupPlayerRaceFindsAnExactName() {
        PlayerRace troll = race("Half-Troll");
        PlayerRegistry.setPlayerRaces(List.of(race("Human"), race("Half-Elf"), troll));

        assertSame(troll, PlayerRegistry.lookupPlayerRace("Half-Troll"));
    }

    // ---- races ----

    @Test
    void lookupPlayerRaceComparesCaseExactlyAndWholeNames() {
        PlayerRegistry.setPlayerRaces(List.of(race("Human"), race("Half-Troll")));

        assertNull(PlayerRegistry.lookupPlayerRace("half-troll"));
        assertNull(PlayerRegistry.lookupPlayerRace("Half"));
        assertNull(PlayerRegistry.lookupPlayerRace("Dwarf"));
    }

    @Test
    void lookupPlayerRaceTakesTheFirstMatch() {
        PlayerRace first = race("Human");
        PlayerRegistry.setPlayerRaces(List.of(first, race("Human")));

        assertSame(first, PlayerRegistry.lookupPlayerRace("Human"));
    }

    @Test
    void lookupPlayerRaceRefusesToReadBeforeTheRacesAreLoaded() throws Exception {
        field("playerRaces").set(null, null);

        assertThrows(IllegalStateException.class, () -> PlayerRegistry.lookupPlayerRace("Human"));
    }

    /**
     * The registry keeps races in file order, so that a list position is C's {@code ridx}
     * ({@code finish_parse_p_race} numbers the races in file order) and the last entry is C's
     * list head, the default {@code player_init} gives a new character.
     */
    @Test
    void racesAreKeptInFileOrder() {
        PlayerRace human = race("Human");
        PlayerRace kobold = race("Kobold");
        PlayerRegistry.setPlayerRaces(List.of(human, race("Half-Elf"), kobold));

        assertSame(human, PlayerRegistry.getPlayerRaces().getFirst());
        assertSame(kobold, PlayerRegistry.getPlayerRaces().getLast());
    }

    @Test
    void lookupPlayerHistoryChartFindsAChartByNumber() {
        PlayerHistoryChart two = new PlayerHistoryChart(2, 3);
        PlayerRegistry.setPlayerHistoryCharts(List.of(new PlayerHistoryChart(1, 2), two,
                new PlayerHistoryChart(3, 0)));

        assertSame(two, PlayerRegistry.lookupPlayerHistoryChart(2));
    }

    // ---- history charts ----

    @Test
    void lookupPlayerHistoryChartReturnsNullForAnUnknownNumber() {
        PlayerRegistry.setPlayerHistoryCharts(List.of(new PlayerHistoryChart(1, 2),
                new PlayerHistoryChart(2, 0)));

        assertNull(PlayerRegistry.lookupPlayerHistoryChart(0));
        assertNull(PlayerRegistry.lookupPlayerHistoryChart(99));
        assertNull(PlayerRegistry.lookupPlayerHistoryChart(-1));
    }

    @Test
    void lookupPlayerHistoryChartTakesTheFirstMatch() {
        PlayerHistoryChart first = new PlayerHistoryChart(5, 0);
        PlayerRegistry.setPlayerHistoryCharts(List.of(first, new PlayerHistoryChart(5, 1)));

        assertSame(first, PlayerRegistry.lookupPlayerHistoryChart(5));
    }

    @Test
    void lookupPlayerHistoryChartRefusesToReadBeforeTheChartsAreLoaded() throws Exception {
        field("playerHistoryCharts").set(null, null);

        assertThrows(IllegalStateException.class, () -> PlayerRegistry.lookupPlayerHistoryChart(1));
    }

    @Test
    void theListGettersAreReadOnlyViews() {
        PlayerRegistry.setPlayerShape(new ArrayList<>(theGameShapes()));
        PlayerRegistry.setPlayerBodies(new ArrayList<>(List.of(body("Humanoid", 12))));
        PlayerRegistry.setPlayerRaces(new ArrayList<>(List.of(race("Human"))));
        PlayerRegistry.setPlayerHistoryCharts(new ArrayList<>(List.of(new PlayerHistoryChart(1, 0))));

        assertThrows(UnsupportedOperationException.class,
                () -> PlayerRegistry.getPlayerShapes().add(shape("x")));
        assertThrows(UnsupportedOperationException.class,
                () -> PlayerRegistry.getPlayerBodies().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> PlayerRegistry.getPlayerRaces().remove(0));
        assertThrows(UnsupportedOperationException.class,
                () -> PlayerRegistry.getPlayerHistoryCharts().add(new PlayerHistoryChart(2, 0)));
    }

    // ---- getters ----

    @Test
    void theListGettersFailBeforeTheListIsLoaded() throws Exception {
        field("playerShapes").set(null, null);
        field("playerBodies").set(null, null);

        assertThrows(NullPointerException.class, PlayerRegistry::getPlayerShapes);
        assertThrows(NullPointerException.class, PlayerRegistry::getPlayerBodies);
    }

    /**
     * Records every dispatched event, so a test can see whether a message was sent.
     */
    private static final class CapturingBus implements EventsHandler {
        final List<GameEventType> types = new ArrayList<>();
        final List<GameEventData> payloads = new ArrayList<>();

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
            types.add(eventType);
            payloads.add(data);
        }
    }
}
