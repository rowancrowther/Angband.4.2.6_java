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
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The central event bus: a static registry mapping each {@link GameEventType}
 * to its list of {@link EventHandlerInterface} listeners, with dispatch and a
 * family of {@code eventSignal*} convenience methods that build the appropriate
 * {@link GameEventData} payload. State and behaviour are entirely static - the
 * per-type lists are created once by a static initialiser on class load - mirroring
 * the C original's file-scope {@code event_handlers} array rather than an object.
 * This is the Java port of the C game-event system ({@code src/game-event.c}),
 * decoupling game logic from the UI that reacts to it.
 *
 * @author Rowan Crowther
 */
public class EventsHandler {
    /**
     * The registry of listeners: each {@link GameEventType} mapped to its list of
     * registered handlers. This is the Java port of the C original's
     * {@code event_handlers[N_GAME_EVENTS]} array ({@code src/game-event.c}) - one
     * handler list per event type.
     *
     * <p>The per-type list is a {@link CopyOnWriteArrayList} rather than a plain
     * {@code ArrayList} to make dispatch re-entrant-safe. {@link #gameEventDispatch}
     * iterates the list while handlers run, and a handler may register or deregister
     * another - or itself - as a side effect. Copy-on-write hands the dispatch loop a
     * stable snapshot for the duration of the walk, so such a mutation cannot raise
     * {@link java.util.ConcurrentModificationException} the way an {@code ArrayList}'s
     * fail-fast iterator would; the change instead takes effect on the next dispatch.
     *
     * <p>This is <em>not</em> a thread-safety choice - the game runs single-threaded -
     * but the fit is the same one copy-on-write is built for: an observer registry
     * whose writes are rare (handlers are registered at initialisation and screen
     * transitions) but whose reads are frequent and hot ({@code EVENT_MAP} alone
     * fires per changed grid), where copy-on-write reads are lock- and
     * allocation-free. The write cost - copying the backing array on each
     * registration - is paid off the hot path and is trivial here, as the lists are
     * short (typically one handler, at most a few).
     *
     * @author Rowan Crowther
     */
    private static final HashMap<GameEventType, CopyOnWriteArrayList<EventHandlerInterface>> handlers = new HashMap<>();

    static {
        for (GameEventType type : GameEventType.values()) {
            handlers.put(type, new CopyOnWriteArrayList<>());
        }
    }

    /**
     * Add a handler to the events handler
     *
     * @param eventType The event type that we are listening for
     * @param handler   the EventHandlerInterface which will handle the event type
     */
    public static void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        CopyOnWriteArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        currentList.add(handler);
    }

    /**
     * Removes a handler from the events table
     *
     * @param eventType The event type we are going to remove a handler from
     * @param handler   The event handler we are removing
     */
    public static void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        CopyOnWriteArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        currentList.remove(handler);
    }

    /**
     * Reset the registry for a new game: removes every registered handler, leaving
     * each event type with an empty list. The lists themselves already exist (built
     * by the static initialiser on class load); this only clears their contents.
     *
     * @author Rowan Crowther
     */
    public static void init() {
        eventRemoveAllHandlers();
    }

    /**
     * Dispatch an event to every handler registered for its type, passing the
     * event type and payload to each.
     *
     * <p><b>Ordering contract.</b> Handlers are dispatched in registration order
     * (first registered, first called). This is a deterministic port-specific
     * guarantee: the C original prepended to a linked-list head and so dispatched
     * most-recently-registered first, but that order was an artifact of O(1)
     * head-insertion, not designed behaviour, and - because dispatch is
     * non-consuming, with every handler always running - nothing depended on it.
     * Handlers therefore must not rely on firing order for correctness; the fixed
     * registration order exists only to make dispatch deterministic and testable
     * (see {@code EventsHandlerTest.handlersFireInRegistrationOrder}).
     *
     * @param eventType the kind of event being triggered
     * @param data      the payload sent to each handler
     */
    public static void gameEventDispatch(GameEventType eventType, GameEventData data) {
        CopyOnWriteArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        for (EventHandlerInterface handler : currentList) {
            handler.dispatch(eventType, data);
        }
    }

    /**
     * Remove all the handlers of a specified type
     *
     * @param eventType The event type we are removing the handlers of
     */
    public static void eventRemoveHandlerType(GameEventType eventType) {
        CopyOnWriteArrayList<EventHandlerInterface> newList = new CopyOnWriteArrayList<>();
        handlers.put(eventType, newList);
    }

    /**
     * Clear down all event handlers
     */
    public static void eventRemoveAllHandlers() {
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
    public static void eventAddHandlerSet(List<GameEventType> eventTypes, @NotNull EventHandlerInterface record) {
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
    public static void eventRemoveHandlerSet(List<GameEventType> eventTypes, @NotNull EventHandlerInterface record) {
        for (GameEventType eventType : eventTypes) {
            eventRemoveHandler(eventType, record);
        }
    }

    /**
     * Send the signal to dispatch all the events for a given event type
     *
     * @param eventType The event type we are signalling
     */
    public static void eventSignal(GameEventType eventType) {
        gameEventDispatch(eventType, null);
    }

    /**
     * Send a signal to dispatch all the events of a given type with a boolean data type
     *
     * @param eventType The event type we are signalling
     * @param flag      The boolean value we are sending
     */
    public static void eventSignalFlag(GameEventType eventType, boolean flag) {
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
    public static void eventSignalPoint(GameEventType eventType, int x, int y) {
        gameEventDispatch(eventType, new EventDataPoint(x, y));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a Loc data type
     *
     * @param eventType The event type we are signalling
     * @param point     The Loc we are using to signal the event
     */
    public static void eventSignalPoint(GameEventType eventType, Loc point) {
        gameEventDispatch(eventType, new EventDataPoint(point));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a String data type
     *
     * @param eventType The event type we are signalling
     * @param string    The String we are using in signalling the event
     */
    public static void eventSignalString(GameEventType eventType, String string) {
        gameEventDispatch(eventType, new EventDataString(string));
    }

    /**
     * Send a signal to dispatch all events of a given type with a Message data type
     *
     * @param eventType The event type we are signalling
     * @param message   The Message we are using in signalling the event
     */
    public static void eventSignalMessage(GameEventType eventType, int type, String message) {
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
    public static void eventSignalBirthpoints(GameEventType eventType,
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
    public static void eventSignalBlast(GameEventType eventType,
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
    public static void eventSignalBolt(GameEventType eventType,
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
    public static void eventSignalMissile(GameEventType eventType,
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
    public static void eventSignalSize(GameEventType eventType, int height, int width) {
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
    public static void eventSignalTunnel(GameEventType eventType, int nStep, int nPierce, int nDug,
                                   int dStart, int dEnd, boolean early) {
        gameEventDispatch(eventType, new EventDataTunnel(nStep, nPierce, nDug, dStart, dEnd, early));
    }
}