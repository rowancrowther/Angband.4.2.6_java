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

package uk.co.jackoftrades.middle.game.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.enums.ProjectionEnum;
import uk.co.jackoftrades.channel.messages.data.EventDataBoolean;
import uk.co.jackoftrades.channel.messages.data.EventDataBolt;
import uk.co.jackoftrades.channel.messages.data.EventDataGrid;
import uk.co.jackoftrades.channel.messages.data.EventDataMessage;
import uk.co.jackoftrades.channel.messages.data.EventDataSize;
import uk.co.jackoftrades.channel.messages.data.EventDataString;
import uk.co.jackoftrades.channel.messages.data.EventDataTunnel;
import uk.co.jackoftrades.channel.messages.data.GameEventData;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.MessageType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@code eventSignal*} family on {@link EventsHandler} — the port of C's
 * {@code event_signal_*} functions ({@code src/game-event.c}). Each takes the caller's loose
 * arguments, wraps them in the matching {@link GameEventData} shape and dispatches.
 *
 * <p>Wrapping is the whole job, so the only thing that can go wrong is <b>wrapping them in the
 * wrong order</b> — and every one of these signatures has a run of same-typed parameters that
 * makes that a silent mistake. The tests therefore drive each signal with values chosen so no
 * permutation reproduces them, and assert the constructed payload as a whole.
 *
 * <p>The bus itself is replaced by a capture, so what is under test is the wrapping alone rather
 * than the wrapping plus the dispatch. {@link EventsHandler} is an interface whose signal methods
 * are all {@code default}, so implementing the four abstract members is enough to exercise them.
 *
 * @author Rowan Crowther
 */
class EventsHandlerSignalTest {

    private CapturingHandler handler;

    @BeforeEach
    void setUp() {
        handler = new CapturingHandler();
    }

    /**
     * The payload-free signal sends a null payload, which the bus is required to deliver as-is.
     * This is the shape most of the 65 event types travel in.
     */
    @Test
    void aBareSignalCarriesTheEventTypeAndNoPayload() {
        handler.eventSignal(GameEventType.EVENT_ENTER_INIT);

        assertEquals(GameEventType.EVENT_ENTER_INIT, handler.lastType);
        assertNull(handler.lastData);
    }

    @Test
    void aFlagSignalWrapsItsBoolean() {
        handler.eventSignalFlag(GameEventType.EVENT_GEN_LEVEL_END, true);

        assertEquals(GameEventType.EVENT_GEN_LEVEL_END, handler.lastType);
        assertEquals(new EventDataBoolean(true), handler.lastData);
    }

    @Test
    void aStringSignalWrapsItsText() {
        handler.eventSignalString(GameEventType.EVENT_INITSTATUS, "Initializing arrays...");

        assertEquals(new EventDataString("Initializing arrays..."), handler.lastData);
    }

    @Test
    void aMessageSignalWrapsItsTypeAndText() {
        handler.eventSignalMessage(GameEventType.EVENT_MESSAGE, MessageType.MSG_HIT, "You hit the orc.");

        assertEquals(new EventDataMessage(MessageType.MSG_HIT, "You hit the orc."), handler.lastData);
    }

    /**
     * The coordinate signal takes {@code (x, y)} — C's parameter order in
     * {@code event_signal_point(game_event_type type, int x, int y)} — while
     * {@link EventDataGrid} is declared row-first. So the wrapping has to <em>cross</em> the pair,
     * and this is the assertion that says so.
     *
     * <p>Driven with distinct values on purpose: {@code (17, 3)} and {@code (3, 17)} are different
     * grids, so a straight-through wrapping cannot pass this by accident.
     */
    @Test
    void aPointSignalCrossesFromXyToRowColumn() {
        handler.eventSignalPoint(GameEventType.EVENT_MAP, 17, 3);

        assertEquals(new EventDataGrid(3, 17), handler.lastData,
                "x=17,y=3 must become row=3,col=17 - EventDataGrid is row-first, "
                        + "event_signal_point takes x first");
    }

    /**
     * The {@link Loc} overload has to agree with the loose-coordinate one: the same square
     * expressed either way must produce the same payload. This is the cheapest guard against the
     * two overloads drifting apart, and it needs no reference to C at all.
     */
    @Test
    void bothPointOverloadsAgreeOnTheSameSquare() {
        handler.eventSignalPoint(GameEventType.EVENT_MAP, 17, 3);
        GameEventData fromCoordinates = handler.lastData;

        handler.eventSignalPoint(GameEventType.EVENT_MAP, Loc.row(3).col(17));
        GameEventData fromLoc = handler.lastData;

        assertEquals(fromLoc, fromCoordinates,
                "signalling the same square as (x, y) and as a Loc must build the same payload");
    }

    /**
     * The size signal is height-first, matching C's {@code event_signal_size(type, h, w)} and its
     * {@code struct { int h, w; }}.
     */
    @Test
    void aSizeSignalKeepsHeightFirst() {
        handler.eventSignalSize(GameEventType.EVENT_GEN_ROOM_CHOOSE_SIZE, 11, 33);

        assertEquals(new EventDataSize(11, 33), handler.lastData);
    }

    /**
     * Six components, five of them {@code int}. Each given a distinct value so a transposed pair
     * changes the payload.
     */
    @Test
    void aTunnelSignalKeepsItsCountsInOrder() {
        handler.eventSignalTunnel(GameEventType.EVENT_GEN_TUNNEL_FINISHED, 40, 3, 31, 25, 7, true);

        assertEquals(new EventDataTunnel(40, 3, 31, 25, 7, true), handler.lastData);
    }

    /**
     * The bolt signal now takes grids rather than four loose coordinates, so the transposition
     * risk has moved to the caller — but the three adjacent booleans remain, and are driven here
     * in a pattern that no single swap reproduces.
     */
    @Test
    void aBoltSignalPassesEveryComponentThrough() {
        EventDataGrid origin = new EventDataGrid(2, 5);
        EventDataGrid current = new EventDataGrid(7, 11);

        handler.eventSignalBolt(GameEventType.EVENT_BOLT, ProjectionEnum.PROJ_FIRE,
                true, false, true, origin, current);

        assertEquals(new EventDataBolt(ProjectionEnum.PROJ_FIRE, true, false, true, origin, current),
                handler.lastData);
    }

    /**
     * The event type reaches the bus unchanged. Trivial, but it is the half of the dispatch that
     * says which handlers run, and nothing else here asserts it for a payload-carrying signal.
     */
    @Test
    void theEventTypeIsPassedThroughAlongsideThePayload() {
        handler.eventSignalString(GameEventType.EVENT_GEN_ROOM_CHOOSE_SUBTYPE, "moat");

        assertEquals(GameEventType.EVENT_GEN_ROOM_CHOOSE_SUBTYPE, handler.lastType);
        assertInstanceOf(EventDataString.class, handler.lastData);
    }

    /**
     * An {@link EventsHandler} that keeps what it was asked to dispatch instead of delivering it.
     * The three registration methods are unused here and left empty.
     *
     * @author Rowan Crowther
     */
    private static final class CapturingHandler implements EventsHandler {
        private GameEventType lastType;
        private GameEventData lastData;

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
            lastType = eventType;
            lastData = data;
        }
    }
}