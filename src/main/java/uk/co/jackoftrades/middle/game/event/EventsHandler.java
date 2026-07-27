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

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.enums.GameEventType;
import uk.co.jackoftrades.middle.objects.ItemObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The event bus abstraction: the interface through which game logic broadcasts
 * {@link GameEventType} notifications to whatever listeners - a front-end, a test spy,
 * or nothing at all - have registered for them. This is the Java port of the C
 * game-event system ({@code src/game-event.c}), decoupling the middle layer from the
 * UI that reacts to it.
 *
 * <p>The surface splits in two. Four <em>primitives</em> - {@link #eventAddHandler},
 * {@link #eventRemoveHandler}, {@link #eventRemoveHandlerType} and
 * {@link #gameEventDispatch} - are abstract, because they are the only operations that
 * touch the handler registry: an implementation supplies that state and these four
 * methods over it. Everything else ({@link #init}, {@link #eventRemoveAllHandlers}, the
 * {@code eventAddHandlerSet}/{@code eventRemoveHandlerSet} pair and the whole
 * {@code eventSignal*} family) is a {@code default} method expressed purely in terms of
 * the primitives - each {@code eventSignal*} builds the appropriate
 * {@link GameEventData} payload and hands it to {@link #gameEventDispatch}. So a single
 * concrete class ({@link EventsBusHandler}) need only implement the four primitives to
 * inherit the entire convenience layer.
 *
 * <p>Unlike the C original's file-scope {@code event_handlers} array, this is an
 * instance abstraction rather than a global: the live bus is held by {@code GameEngine}
 * and can be swapped (see {@code GameEngine.setEventsBusHandler}), so a test can run
 * against its own isolated bus or inject a spy to assert what was signalled.
 *
 * @author Rowan Crowther
 */
public interface EventsHandler {
    /**** Abstract methods ****/

    /**
     * Register a handler to be dispatched whenever the given event type is signalled -
     * the port of C's {@code event_add_handler} ({@code src/game-event.c}). One handler
     * may be registered against several types, and the same handler instance may be
     * registered more than once against a single type (each registration fires
     * independently).
     *
     * @param eventType the event type to listen for
     * @param handler   the handler to dispatch when that type is signalled
     * @author Rowan Crowther
     */
    void eventAddHandler(GameEventType eventType, EventHandlerInterface handler);

    /**
     * Deregister a handler from the given event type - the port of C's
     * {@code event_remove_handler}. Only the first matching registration is removed
     * (mirroring C, which unlinks the first matching node and returns), and removing a
     * handler that was never registered is a silent no-op.
     *
     * @param eventType the event type to stop dispatching the handler for
     * @param handler   the handler to remove
     * @author Rowan Crowther
     */
    void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler);

    /**
     * Dispatch an event to every handler registered for its type, passing the type and
     * payload to each in turn - the port of C's {@code game_event_dispatch}, and the one
     * primitive every {@code eventSignal*} default method funnels through.
     *
     * <p><b>Ordering contract.</b> Handlers fire in registration order (first
     * registered, first called). This is a deterministic port-specific choice: the C
     * original prepended to a linked-list head and so dispatched most-recently-registered
     * first, but that order was an artifact of O(1) head-insertion, not designed
     * behaviour, and - because dispatch is non-consuming, with every handler always
     * running - nothing depended on it. Handlers therefore must not rely on firing order
     * for correctness; the fixed order exists only to make dispatch deterministic and
     * testable.
     *
     * @param eventType the kind of event being dispatched
     * @param data      the payload handed to each handler, or {@code null} for a bare signal
     * @author Rowan Crowther
     */
    void gameEventDispatch(GameEventType eventType, GameEventData data);

    /**
     * Clear every handler registered for one event type, leaving that type with an
     * empty (but still present) list - the port of C's {@code event_remove_handler_type}.
     * Other event types are untouched.
     *
     * @param eventType the event type to clear
     * @author Rowan Crowther
     */
    void eventRemoveHandlerType(GameEventType eventType);

    /**** Default methods ****/

    /**
     * Reset the registry for a new game: clears every registered handler, leaving each
     * event type with an empty list. The per-type lists themselves stay in place (the
     * implementation populates them once when the bus is built), so this only empties
     * their contents. The port of C's start-of-game handler reset.
     *
     * @author Rowan Crowther
     */
    default void init() {
        eventRemoveAllHandlers();
    }

    /**
     * Clear down all event handlers
     */
    default void eventRemoveAllHandlers() {
        for (GameEventType eventType : GameEventType.values()) {
            eventRemoveHandlerType(eventType);
        }
    }

    /**
     * Register a single handler against every event type in a set, binding one
     * listener to a group of related events in one call. This is the Java port of
     * the C original's {@code event_add_handler_set} ({@code src/game-event.c}):
     * the "set" is a set of event <em>types</em> sharing one handler, not a set of
     * handlers - each type gets its own registration via {@link #eventAddHandler}.
     * Mirrors the C caller in {@code ui-display.c}, where one {@code update_sidebar}
     * handler is bound across the whole {@code player_events} group at once.
     *
     * @param eventTypes the event types to register the handler against
     * @param record     the handler to register for each of those event types
     */
    default void eventAddHandlerSet(List<GameEventType> eventTypes, @NotNull EventHandlerInterface record) {
        for (GameEventType eventType : eventTypes) {
            eventAddHandler(eventType, record);
        }
    }

    /**
     * Deregister a single handler from every event type in a set, the inverse of
     * {@link #eventAddHandlerSet} and the Java port of the C original's
     * {@code event_remove_handler_set} ({@code src/game-event.c}). Each type is
     * unbound individually via {@link #eventRemoveHandler}. Symmetric with the add
     * side: passing the same {@code eventTypes} group used to register a handler
     * tears down exactly those bindings, as the C caller does in {@code ui-display.c}
     * by handing the same {@code player_events} group back to remove
     * {@code update_sidebar}.
     *
     * @param eventTypes the event types to remove the handler from
     * @param record     the handler to deregister from each of those event types
     */
    default void eventRemoveHandlerSet(List<GameEventType> eventTypes, @NotNull EventHandlerInterface record) {
        for (GameEventType eventType : eventTypes) {
            eventRemoveHandler(eventType, record);
        }
    }

    /**
     * Send the signal to dispatch all the events for a given event type
     *
     * @param eventType The event type we are signalling
     */
    default void eventSignal(GameEventType eventType) {
        gameEventDispatch(eventType, null);
    }

    /**
     * Send a signal to dispatch all the events of a given type with a boolean data type
     *
     * @param eventType The event type we are signalling
     * @param flag      The boolean value we are sending
     */
    default void eventSignalFlag(GameEventType eventType, boolean flag) {
        gameEventDispatch(eventType, new EventDataBoolean(flag));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a Loc data type determined by its x and y
     * coordinates
     *
     * @param eventType The event type we are signalling
     * @param x         The x coordinate of the Loc
     * @param y         The y coordinate of the Loc
     */
    default void eventSignalPoint(GameEventType eventType, int x, int y) {
        gameEventDispatch(eventType, new EventDataPoint(x, y));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a Loc data type
     *
     * @param eventType The event type we are signalling
     * @param point     The Loc we are using to signal the event
     */
    default void eventSignalPoint(GameEventType eventType, Loc point) {
        gameEventDispatch(eventType, new EventDataPoint(point));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a String data type
     *
     * @param eventType The event type we are signalling
     * @param string    The String we are using in signalling the event
     */
    default void eventSignalString(GameEventType eventType, String string) {
        gameEventDispatch(eventType, new EventDataString(string));
    }

    /**
     * Send a signal to dispatch all events of a given type with a Message data type
     *
     * @param eventType The event type we are signalling
     * @param message   The Message we are using in signalling the event
     */
    default void eventSignalMessage(GameEventType eventType, int type, String message) {
        gameEventDispatch(eventType, new EventDataMessage(type, message));
    }

    /**
     * Send a signal to dispatch all events of a given type with Birthpoint data
     *
     * @param eventType The event we are signalling
     * @param stats     A HashMap of Stats to amount of points already spent for each stat
     * @param incPoints A HashMap of Stats to the amount it would take to increase the stat by a further point for each
     *                  stat
     * @param remaining The remaining number of points to spend
     */
    default void eventSignalBirthpoints(GameEventType eventType,
                                        HashMap<Stats, Integer> stats,
                                        HashMap<Stats, Integer> incPoints,
                                        int remaining) {
        gameEventDispatch(eventType, new EventDataBirthPoints(stats, incPoints, remaining));
    }

    /**
     * Send a signal to dispatch all events of a given type with an Explosion
     *
     * @param eventType      The event we are signalling
     * @param projType       The projection type - TODO: currently integer, probably will change to an enum
     * @param numGrids       The number of grids affected by the explosion
     * @param distanceToGrid The distance to the grids from the Loc of the player
     * @param drawing        Whether we are drawing the explosion?
     * @param playerSeesGrid Whether the player sees the explosion on a particular grid
     * @param blastGrid      The grids we are blasting with this explosion
     * @param centre         The centre of the explosion
     */
    default void eventSignalBlast(GameEventType eventType,
                                  int projType,
                                  int numGrids,
                                  ArrayList<Integer> distanceToGrid,
                                  boolean drawing,
                                  ArrayList<Boolean> playerSeesGrid,
                                  ArrayList<Loc> blastGrid,
                                  Loc centre) {
        gameEventDispatch(eventType, new EventDataExplosion(projType, numGrids, distanceToGrid, drawing, playerSeesGrid,
                blastGrid, centre));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a Bolt
     *
     * @param eventType The event we are signalling
     * @param projType  The projection type - TODO: currently integer, probably will change to an enum
     * @param drawing   Whether we are drawing the bolt?
     * @param seen      Whether the bolt is seen?
     * @param beam      Whether the bolt is a beam?
     * @param oy        The origin-Y of the bolt?
     * @param ox        The origin-X of the bolt?
     * @param y         The target-Y of the bolt?
     * @param x         The target-X of the bolt
     */
    default void eventSignalBolt(GameEventType eventType,
                                 int projType,
                                 boolean drawing,
                                 boolean seen,
                                 boolean beam,
                                 int oy,
                                 int ox,
                                 int y,
                                 int x) {
        gameEventDispatch(eventType, new EventDataBolt(projType, drawing, seen, beam, oy, ox, y, x));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a Missile
     *
     * @param eventType  The event we are signalling
     * @param itemObject The object which is the missile?
     * @param seen       Whether the missile is seen?
     * @param y          The y location of the missile (start/end/current)?
     * @param x          The x location of the missile (start/end/current)?
     */
    default void eventSignalMissile(GameEventType eventType,
                                    ItemObject itemObject,
                                    boolean seen,
                                    int y,
                                    int x) {
        gameEventDispatch(eventType, new EventDataMissile(itemObject, seen, y, x));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a size
     *
     * @param eventType The event type we are signalling
     * @param height    The height of the area/size
     * @param width     The width of the area/size
     */
    default void eventSignalSize(GameEventType eventType, int height, int width) {
        gameEventDispatch(eventType, new EventDataSize(height, width));
    }

    /**
     * Sends a signal to dispatch all events of a given type with a tunnel
     *
     * @param eventType The event type we are signalling
     * @param nStep     The number of steps in the tunnel
     * @param nPierce   The number of wall piercings
     * @param nDug      The number of spaces dug ignoring wall piercings
     * @param dStart    The city block distance from the start of the tunnel
     * @param dEnd      The city block distance to the goal of the tunnel
     * @param early     Whether the tunnelling has been stopped early
     */
    default void eventSignalTunnel(GameEventType eventType, int nStep, int nPierce, int nDug,
                                   int dStart, int dEnd, boolean early) {
        gameEventDispatch(eventType, new EventDataTunnel(nStep, nPierce, nDug, dStart, dEnd, early));
    }
}