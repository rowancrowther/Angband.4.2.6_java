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

package uk.co.jackoftradesltd.middle.game.event;

import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.messages.data.*;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     */
    void gameEventDispatch(GameEventType eventType, GameEventData data);

    /**
     * Clear every handler registered for one event type, leaving that type with an
     * empty (but still present) list - the port of C's {@code event_remove_handler_type}.
     * Other event types are untouched.
     *
     * @param eventType the event type to clear
     */
    void eventRemoveHandlerType(GameEventType eventType);

    /**** Default methods ****/

    /**
     * Reset the registry for a new game: clears every registered handler, leaving each
     * event type with an empty list. The per-type lists themselves stay in place (the
     * implementation populates them once when the bus is built), so this only empties
     * their contents. The port of C's start-of-game handler reset.
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
     * Send a signal to dispatch all events of a given type with an array of per-cell display
     * characters - the sibling of {@link #eventSignalString} for a redraw that hands across a
     * whole row of glyph/colour pairs rather than plain text. Used today only for
     * {@code EVENT_EQUIPMENT}, sent by {@code PlayerCalcs.redrawStuff}'s {@code PR_EQUIP} arm.
     *
     * <p>There is no {@code event_signal_*} counterpart for this shape in C, for the same reason
     * {@link #eventSignalStat} has none: C's {@code prt_equippy} ({@code [C] ui-display.c}) reads
     * {@code player->body} and calls {@code object_attr}/{@code object_char} on each slot directly
     * at draw time, rather than through a signal. See {@link EventDataColourString}'s Javadoc for
     * the full rationale.
     *
     * <p>Function eventSignalColourString coded on 260927, commented in full on 260928.
     *
     * @param eventType The event type we are signalling
     * @param string    one {@link AngbandDisplayCharacter} per cell, in display order; becomes
     *                  {@link EventDataColourString#string()}
     */
    default void eventSignalColourString(GameEventType eventType, AngbandDisplayCharacter[] string) {
        gameEventDispatch(eventType, new EventDataColourString(string));
    }

    /**
     * Send a signal to dispatch all events of a given type with one stat row's full redraw state -
     * the sibling of {@link #eventSignalStat} for a redraw that needs a per-item index and a
     * third, displayed value alongside the current/maximum pair. Used today only for
     * {@code EVENT_STATS}, sent once per stat by {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_STATS} arm.
     *
     * <p>There is no {@code event_signal_*} counterpart for this shape in C: C's {@code prt_stat}
     * ({@code [C] ui-display.c}, function {@code prt_stat}) reads {@code player->stat_cur[stat]},
     * {@code player->stat_max[stat]} and {@code player->state.stat_use[stat]} straight off the
     * shared {@code player} global, indexed by whichever stat its {@code prt_str}/{@code prt_int}/
     * {@code prt_wis}/{@code prt_dex}/{@code prt_con} wrapper was called for. A handler on the far
     * side of the core-to-front-end boundary has no such global or index to read, so all four
     * values travel with the signal instead - see {@link EventDataFullStat}'s Javadoc for the full
     * rationale.
     *
     * <p>Function eventSignalFullStat coded on 260927, commented in full on 260928.
     *
     * @param eventType The event type we are signalling
     * @param index     which stat this row reports; becomes {@link EventDataFullStat#statIndex()}
     * @param main      the stat's current value; becomes {@link EventDataFullStat#current()}
     * @param other     the stat's recorded maximum; becomes {@link EventDataFullStat#max()}
     * @param use       the value actually displayed; becomes {@link EventDataFullStat#use()}
     */
    default void eventSignalFullStat(GameEventType eventType, int index, int main, int other, int use) {
        gameEventDispatch(eventType, new EventDataFullStat(index, main, other, use));
    }

    /**
     * Send a signal to dispatch all events of a given type with a pair of numbers - the value the
     * redraw is about, and whatever second value it is paired with for display.
     *
     * <p>There is no {@code event_signal_*} counterpart for this shape in C: C's UI handlers for
     * these events ({@code prt_hp}, {@code prt_sp}, {@code prt_level} -
     * {@code src/ui-display.c:207,314,332}) read the pair straight off the shared {@code player}
     * global at signal time rather than receiving it as an argument. This method exists because a
     * handler on the far side of the core-to-front-end boundary has no such global to read, so the
     * values have to travel with the signal instead. See {@link EventDataStat}'s Javadoc for the
     * full rationale and the naming of {@code current}/{@code other} over {@code current}/{@code
     * max}.
     *
     * <p>Function eventSignalStat coded before 260926, commented in full on 260928.
     *
     * @param eventType The event type we are signalling
     * @param main      the value the redraw is about; becomes {@link EventDataStat#current()}
     * @param other     whatever {@code main} is paired with for display; becomes
     *                  {@link EventDataStat#other()}
     */
    default void eventSignalStat(GameEventType eventType, int main, int other) {
        gameEventDispatch(eventType, new EventDataStat(main, other));
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
     * coordinates. Note, the EventDataGrid is y, x, but the data coming into this function is x, y.
     *
     * @param eventType The event type we are signalling
     * @param x         The x coordinate of the Loc
     * @param y         The y coordinate of the Loc
     */
    default void eventSignalPoint(GameEventType eventType, int x, int y) {
        gameEventDispatch(eventType, new EventDataGrid(y, x));
    }

    /**
     * Send a signal to dispatch all the events of a given type with a Loc data type
     *
     * @param eventType The event type we are signalling
     * @param point     The Loc we are using to signal the event
     */
    default void eventSignalPoint(GameEventType eventType, Loc point) {
        gameEventDispatch(eventType, new EventDataGrid(point.getY(), point.getX()));
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
     * Send a signal to dispatch all events of a given type with several pieces of text at once - the
     * multi-value sibling of {@link #eventSignalString}, used where a redraw needs more than one
     * label in the same signal.
     *
     * <p>There is no {@code event_signal_*} counterpart for this shape in C, for the same reason
     * {@link #eventSignalStat} has none: C's {@code redraw_events} table ({@code player-calcs.c})
     * fires {@code EVENT_RACE_CLASS} as a bare signal, and its handlers ({@code prt_race},
     * {@code prt_class} - {@code src/ui-display.c}) read {@code player->race->name}/
     * {@code class->name} straight off the shared global rather than from a payload. A handler on
     * the far side of the core-to-front-end boundary has no such global to read, so the values have
     * to travel with the signal instead. See {@link EventDataStrings}'s Javadoc for the full
     * rationale.
     *
     * <p>Function eventSignalStrings coded before 260926, commented in full on 260926.
     *
     * @param eventType The event type we are signalling
     * @param strings   the pieces of text to carry, in a fixed, caller-defined order; becomes
     *                  {@link EventDataStrings#strings()}
     */
    default void eventSignalStrings(GameEventType eventType, String... strings) {
        gameEventDispatch(eventType, new EventDataStrings(strings));
    }

    /**
     * Send a signal to dispatch all events of a given type with a Message data type
     *
     * @param eventType The event type we are signalling
     * @param message   The Message we are using in signalling the event
     */
    default void eventSignalMessage(GameEventType eventType, MessageType type, String message) {
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
                                        Map<Stats, Integer> stats,
                                        Map<Stats, Integer> incPoints,
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
     * @param origin    The origin of the bolt
     * @param current   The target
     */
    default void eventSignalBolt(GameEventType eventType,
                                 ProjectionEnum projType,
                                 boolean drawing,
                                 boolean seen,
                                 boolean beam,
                                 EventDataGrid origin,
                                 EventDataGrid current) {
        gameEventDispatch(eventType, new EventDataBolt(projType, drawing, seen, beam, origin, current));
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

    /**
     * Send a signal to dispatch all events of a given type with a pair of {@code long} numbers - the
     * {@code long}-valued sibling of {@link #eventSignalStat}, for a redraw whose current/other pair
     * does not fit an {@code int}. Used today only by {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_EXP} arm, sending the player's (current, maximum) experience - see
     * {@link EventDataLongStat}'s Javadoc for why the wider type and why there is no
     * {@code event_signal_*} counterpart in C.
     *
     * <p>Function eventSignalLongStat coded on 260927, commented in full on 260928.
     *
     * @param eventType The event type we are signalling
     * @param value     the value the redraw is about; becomes {@link EventDataLongStat#value()}
     * @param other     whatever {@code value} is paired with for display; becomes
     *                  {@link EventDataLongStat#other()}
     */
    default void eventSignalLongStat(GameEventType eventType, long value, long other) {
        gameEventDispatch(eventType, new EventDataLongStat(value, other));
    }

    /**
     * Send a signal to dispatch all events of a given type with a single {@code long} number - the
     * {@code long}-valued sibling of a bare stat value, for a redraw whose figure does not fit an
     * {@code int}. Used today only by {@code PlayerCalcs.redrawStuff}'s {@code PR_EXP} arm, sending
     * the experience needed for the next level (or the current total at level fifty) as a second,
     * separate dispatch alongside the {@link #eventSignalLongStat} pair - see {@link EventDataLong}'s
     * Javadoc for why the wider type and why there is no {@code event_signal_*} counterpart in C.
     *
     * <p>Function eventSignalLong coded on 260927, commented in full on 260928.
     *
     * @param eventType The event type we are signalling
     * @param value     the number this event is reporting; becomes {@link EventDataLong#value()}
     */
    default void eventSignalLong(GameEventType eventType, long value) {
        gameEventDispatch(eventType, new EventDataLong(value));
    }
}