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

import uk.co.jackoftrades.channel.enums.GameEventType;
import uk.co.jackoftrades.channel.messages.data.GameEventData;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The concrete event bus: the {@link EventsHandler} implementation that holds the
 * per-type registry of {@link EventHandlerInterface} listeners and supplies the four
 * abstract primitives ({@link #eventAddHandler}, {@link #eventRemoveHandler},
 * {@link #eventRemoveHandlerType} and {@link #gameEventDispatch}) the interface's
 * {@code default} convenience layer is built on. This is the Java port of the state
 * behind C's {@code event_handlers[N_GAME_EVENTS]} array ({@code src/game-event.c}).
 *
 * <p>Each instance owns its own {@link #handlers} map, so buses are fully isolated: the
 * live one is held by {@code GameEngine}, while a test can build its own with
 * {@code new EventsBusHandler()} and never disturb another. That isolation is the whole
 * reason the bus is an instance rather than the static global it ports from.
 *
 * @author Rowan Crowther
 */
public class EventsBusHandler implements EventsHandler {
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
     */
    private final HashMap<GameEventType, CopyOnWriteArrayList<EventHandlerInterface>> handlers = new HashMap<>();

    /**
     * Build a bus with a fully-populated, empty registry. The constructor delegates to
     * {@link #init()}, which fills {@link #handlers} with an empty handler list for every
     * {@link GameEventType} - so "first populate" and "reset for a new game" are the one
     * operation, and no signal or registration can ever hit a missing (null) list.
     *
     * <p>Calling the overridable {@code init()} from a constructor is safe here because
     * {@link #handlers} is assigned at its declaration, which runs before this body; the
     * class is not designed to be subclassed with an {@code init()} that reads other,
     * not-yet-initialised fields.
     */
    public EventsBusHandler() {
        init();
    }

    /**
     * Add a handler to the events handler
     *
     * @param eventType The event type that we are listening for
     * @param handler   the EventHandlerInterface which will handle the event type
     */
    @Override
    public void eventAddHandler(GameEventType eventType, EventHandlerInterface handler) {
        CopyOnWriteArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        currentList.add(handler);
    }

    /**
     * Removes a handler from the events table
     *
     * @param eventType The event type we are going to remove a handler from
     * @param handler   The event handler we are removing
     */
    @Override
    public void eventRemoveHandler(GameEventType eventType, EventHandlerInterface handler) {
        CopyOnWriteArrayList<EventHandlerInterface> currentList = handlers.get(eventType);
        currentList.remove(handler);
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
    @Override
    public void gameEventDispatch(GameEventType eventType, GameEventData data) {
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
    @Override
    public void eventRemoveHandlerType(GameEventType eventType) {
        CopyOnWriteArrayList<EventHandlerInterface> newList = new CopyOnWriteArrayList<>();
        handlers.put(eventType, newList);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This class adds no behaviour of its own - it simply defers to the interface
     * default via {@code EventsHandler.super.init()}. It is retained as an explicit,
     * concrete reset entry point and as the method the {@linkplain #EventsBusHandler()
     * constructor} calls to first populate the registry.
     */
    @Override
    public void init() {
        EventsHandler.super.init();
    }
}
