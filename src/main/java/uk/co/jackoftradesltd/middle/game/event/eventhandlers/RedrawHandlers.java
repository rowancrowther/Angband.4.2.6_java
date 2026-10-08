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

package uk.co.jackoftradesltd.middle.game.event.eventhandlers;

import uk.co.jackoftradesltd.channel.Sender;
import uk.co.jackoftradesltd.channel.enums.GameEventType;
import uk.co.jackoftradesltd.channel.messages.CoreMessage;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;
import uk.co.jackoftradesltd.channel.messages.data.EventDataString;
import uk.co.jackoftradesltd.channel.messages.data.EventDataStrings;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;
import uk.co.jackoftradesltd.middle.game.event.EventsHandler;
import uk.co.jackoftradesltd.middle.game.gameengine.GameEngine;

/**
 * The core-side handlers that forward {@link uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw}
 * events across the core-to-front-end
 * boundary as {@link CoreMessage.GameEventCoreMessage}s. There is no single C counterpart: C's
 * {@code redraw_stuff} ({@code player-calcs.c:2696}) calls {@code event_signal} and its UI-side
 * handlers (for example {@code prt_hp}, {@code src/ui-display.c:207}) read the pair they need
 * straight off the shared {@code player} global at signal time. The port has no such global on
 * the far side of the boundary, so each redraw event that carries data has to be re-sent as a
 * message whose payload holds the values a C handler would have read for itself.
 *
 * <p>This class exists to hold that translation in one place, following the same shape
 * {@link InitHandlers} already uses: a {@link Sender} handed in at construction,
 * {@link #initHandlers()} subscribing bound method references against the live bus at call time,
 * and a private handler per event that is guarded on the payload it expects. Today it wires
 * {@code EVENT_HP}, {@code EVENT_MANA}, {@code EVENT_RACE_CLASS}, {@code EVENT_PLAYERTITLE} and
 * {@code EVENT_PLAYER_NAME} - the vertical slices of the redraw-to-message migration recorded in
 * {@code docs/implementation/260926_change_in_architecture_from_cache_to_messages.md} ported so far
 * - and more handlers are expected to join it as the rest of
 * {@link uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw}'s {@code PR_*} flags gain their
 * own payload records.
 *
 * <p>Constructed and wired from {@code Core.gameLoop()}, alongside {@code InitHandlers} and on the
 * same {@code coreSender}, so both classes' start-up and redraw narration leave by the one queue.
 *
 * <p>Class RedrawHandlers coded before 260926, commented in full on 260926.
 *
 * @author Rowan Crowther
 */
public class RedrawHandlers {
    /**
     * The core's writing end of the UI thread's inbox, exactly as {@code InitHandlers.coreSender}
     * is: the one thing every handler in this class needs in order to put a
     * {@link CoreMessage.GameEventCoreMessage} on the queue.
     *
     * <p>{@code final}, so it is safely published without needing {@code volatile}: the object is
     * constructed on the game thread and never handed to another thread before that thread starts
     * reading it.
     */
    private final Sender<CoreMessage> coreSender;

    /**
     * Build the handlers around the channel end they forward redraw events on.
     *
     * <p>Constructing does not subscribe anything - {@link #initHandlers()} does that, on the live
     * bus, once one exists.
     *
     * @param coreSender the core's sending end of the core channel; not checked for null, and a
     *                   null would fail at the first event rather than here
     */
    public RedrawHandlers(Sender<CoreMessage> coreSender) {
        this.coreSender = coreSender;
    }

    /**
     * Subscribe every redraw handler this class owns to the live event bus.
     *
     * <p>Reads the bus through {@code GameEngine.getEventsBusHandler()} at call time, as
     * {@link InitHandlers#initHandlers()} does, so it always wires the bus that is actually live.
     * Today that is five registrations: {@link #eventHP} against {@code EVENT_HP},
     * {@link #eventSP} against {@code EVENT_MANA}, {@link #eventRaceClass} against
     * {@code EVENT_RACE_CLASS}, {@link #eventTitle} against {@code EVENT_PLAYERTITLE} and
     * {@link #eventName} against {@code EVENT_PLAYER_NAME}; the rest of
     * {@link uk.co.jackoftradesltd.middle.player.enums.PlayerRedraw}'s flags join here as their
     * payload records are ported.
     *
     * <p>Not idempotent, for the same reason {@link InitHandlers#initHandlers()} is not: dispatch
     * is non-consuming, so calling this twice on one bus would register every handler above twice
     * each and they would run twice per signal.
     */
    public void initHandlers() {
        EventsHandler eventsHandler = GameEngine.getEventsBusHandler();
        eventsHandler.eventAddHandler(GameEventType.EVENT_HP, this::eventHP);
        eventsHandler.eventAddHandler(GameEventType.EVENT_MANA, this::eventSP);
        eventsHandler.eventAddHandler(GameEventType.EVENT_RACE_CLASS, this::eventRaceClass);
        eventsHandler.eventAddHandler(GameEventType.EVENT_PLAYERTITLE, this::eventTitle);
        eventsHandler.eventAddHandler(GameEventType.EVENT_PLAYER_NAME, this::eventName);
    }

    /**
     * A redraw has raised {@code PR_HP}, and {@code PlayerCalcs.redrawStuff} has signalled
     * {@code EVENT_HP} with the player's current and maximum hit points already attached as an
     * {@link EventDataStat}. This forwards that record unchanged onto the core channel - unlike
     * {@code InitHandlers.splashScreenNote}, which unwraps a string payload before resending it,
     * there is nothing here to unwrap: the shape {@code EventDataStat} arrives in is the shape the
     * front end needs it in.
     *
     * <p>Guarded on the payload: a signal for {@code EVENT_HP} carrying anything other than an
     * {@link EventDataStat} is dropped rather than forwarded, which is really a check that the
     * caller used {@link EventsHandler#eventSignalStat} rather than a bare
     * {@link EventsHandler#eventSignal}. Nothing sends {@code EVENT_HP} that way today, but the
     * guard means a future caller that did would lose the redraw silently rather than send a
     * malformed message.
     *
     * @param eventType the event being handled, always {@code EVENT_HP}; forwarded as the
     *                  message's type
     * @param data      the payload; must be an {@link EventDataStat} or nothing is forwarded
     */
    private void eventHP(GameEventType eventType, GameEventData data) {
        if (data instanceof EventDataStat hp) {
            coreSender.send(new CoreMessage.GameEventCoreMessage(eventType, hp));
        }
    }

    /**
     * A redraw has raised {@code PR_MANA}, and {@code PlayerCalcs.redrawStuff} has signalled
     * {@code EVENT_MANA} with the player's current and maximum spell points already attached as an
     * {@link EventDataStat}. This forwards that record unchanged onto the core channel, exactly as
     * {@link #eventHP} does for {@code EVENT_HP}.
     *
     * <p>Guarded on the payload the same way {@link #eventHP} is: a signal for {@code EVENT_MANA}
     * carrying anything other than an {@link EventDataStat} is dropped rather than forwarded.
     *
     * @param eventType the event being handled, always {@code EVENT_MANA}; forwarded as the
     *                  message's type
     * @param data      the payload; must be an {@link EventDataStat} or nothing is forwarded
     */
    private void eventSP(GameEventType eventType, GameEventData data) {
        if (data instanceof EventDataStat sp) {
            coreSender.send(new CoreMessage.GameEventCoreMessage(eventType, sp));
        }
    }

    /**
     * A redraw has raised {@code PR_MISC}, and {@code PlayerCalcs.redrawStuff} has signalled
     * {@code EVENT_PLAYER_NAME} with the player's full name already attached as an
     * {@link EventDataString}. This forwards that record unchanged onto the core channel, exactly
     * as {@link #eventHP} does for {@code EVENT_HP}.
     *
     * <p>Guarded on the payload the same way {@link #eventHP} is: a signal for
     * {@code EVENT_PLAYER_NAME} carrying anything other than an {@link EventDataString} is dropped
     * rather than forwarded.
     *
     * @param eventType the event being handled, always {@code EVENT_PLAYER_NAME}; forwarded as the
     *                  message's type
     * @param data      the payload; must be an {@link EventDataString} or nothing is forwarded
     */
    private void eventName(GameEventType eventType, GameEventData data) {
        if (data instanceof EventDataString name) {
            coreSender.send(new CoreMessage.GameEventCoreMessage(eventType, name));
        }
    }

    /**
     * A redraw has raised {@code PR_MISC}, and {@code PlayerCalcs.redrawStuff} has signalled
     * {@code EVENT_RACE_CLASS} with the player's race and class names already attached as an
     * {@link EventDataStrings}. This forwards that record unchanged onto the core channel, exactly
     * as {@link #eventHP} does for {@code EVENT_HP}.
     *
     * <p>Guarded on the payload the same way {@link #eventHP} is: a signal for
     * {@code EVENT_RACE_CLASS} carrying anything other than an {@link EventDataStrings} is dropped
     * rather than forwarded.
     *
     * @param eventType the event being handled, always {@code EVENT_RACE_CLASS}; forwarded as the
     *                  message's type
     * @param data      the payload; must be an {@link EventDataStrings} or nothing is forwarded
     */
    private void eventRaceClass(GameEventType eventType, GameEventData data) {
        if (data instanceof EventDataStrings raceClass) {
            coreSender.send(new CoreMessage.GameEventCoreMessage(eventType, raceClass));
        }
    }

    /**
     * A redraw has raised {@code PR_TITLE}, and {@code PlayerCalcs.redrawStuff} has signalled
     * {@code EVENT_PLAYERTITLE} with the player's title already attached as an
     * {@link EventDataString}. This forwards that record unchanged onto the core channel, exactly
     * as {@link #eventHP} does for {@code EVENT_HP}.
     *
     * <p>Guarded on the payload the same way {@link #eventHP} is: a signal for
     * {@code EVENT_PLAYERTITLE} carrying anything other than an {@link EventDataString} is dropped
     * rather than forwarded.
     *
     * @param eventType the event being handled, always {@code EVENT_PLAYERTITLE}; forwarded as the
     *                  message's type
     * @param data      the payload; must be an {@link EventDataString} or nothing is forwarded
     */
    private void eventTitle(GameEventType eventType, GameEventData data) {
        if (data instanceof EventDataString title) {
            coreSender.send(new CoreMessage.GameEventCoreMessage(eventType, title));
        }
    }
}
