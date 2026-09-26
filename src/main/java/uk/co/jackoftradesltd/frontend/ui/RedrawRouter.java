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

package uk.co.jackoftradesltd.frontend.ui;

import uk.co.jackoftradesltd.channel.messages.data.EventDataStat;
import uk.co.jackoftradesltd.channel.messages.data.GameEventData;

/**
 * The UI half's dispatcher from a decoded redraw payload to the model that owns the value it
 * carries. {@link uk.co.jackoftradesltd.frontend.inputfromuser.UILoop#loop()} hands this class the
 * {@link GameEventData} it unpacked from each {@code CoreMessage.GameEventCoreMessage}, and this is
 * the one place that payload is written into a UI-owned model rather than read back off a shared
 * global.
 *
 * <p>There is no single C counterpart: C's {@code prt_hp} ({@code [C] ui-display.c}, function
 * {@code prt_hp}) reads {@code player->chp}/{@code mhp} straight off the shared {@code player}
 * global at draw time. The port has no such global on the front-end side of the boundary, so the
 * value has to arrive as a message and be written somewhere the drawing code can later read it —
 * this class is that write, following the pattern
 * {@link uk.co.jackoftradesltd.middle.game.event.eventhandlers.RedrawHandlers} already uses
 * core-side.
 *
 * <p>Today it wires only {@code EVENT_HP}; a routing method joins here as each further
 * {@code PR_*} flag gets its own payload record and model, per
 * {@code docs/implementation/260926_change_in_architecture_from_cache_to_messages.md}. That design
 * doc is also why {@link SidebarModel} exists at all —
 * {@link uk.co.jackoftradesltd.channel.messages.data.PlayerStatusView} and its sibling caches are
 * being architected out field by field in favour of models this class writes.
 *
 * <p>Class RedrawRouter coded on 260926, commented in full on 260926.
 *
 * @author Rowan Crowther
 */
public class RedrawRouter {
    /**
     * Write an {@code EVENT_HP} payload into {@link SidebarModel} — the value C's {@code prt_hp}
     * and {@code get_panel_topleft} ({@code [C] ui-player.c}, function {@code get_panel_topleft})
     * both read straight off {@code player} at their own call time.
     *
     * <p>Guarded on the payload shape: anything other than an {@link EventDataStat} is dropped
     * rather than routed, the same guard
     * {@link uk.co.jackoftradesltd.middle.game.event.eventhandlers.RedrawHandlers#eventHP} applies
     * on the sending side. Nothing sends {@code EVENT_HP} any other way today, but a future caller
     * that did would lose the redraw silently rather than write a mismatched pair into the model.
     *
     * <p>Writes {@link EventDataStat#current()} before {@link EventDataStat#other()}, so
     * {@link SidebarModel#setCurrentHP(int)} gets {@code chp} and
     * {@link SidebarModel#setMaxHP(int)} gets {@code mhp} — the same order C's HP row passes
     * {@code player->chp} then {@code player->mhp}.
     *
     * <p>Method setHP coded on 260926, commented in full on 260926.
     *
     * @param gameEventData the decoded payload from a {@code CoreMessage.GameEventCoreMessage} of
     *                      type {@code EVENT_HP}; anything other than an {@link EventDataStat} is
     *                      ignored
     */
    public static void setHP(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataStat data) {
            SidebarModel.setCurrentHP(data.current());
            SidebarModel.setMaxHP(data.other());
        }
    }
}
