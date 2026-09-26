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
 * <p>Today it wires {@code EVENT_HP} and {@code EVENT_MANA}; a routing method joins here as each
 * further {@code PR_*} flag gets its own payload record and model, per
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
     * Unpacks an {@code EVENT_HP} payload and writes its pair into {@link SidebarModel}, C's
     * {@code prt_hp} ({@code [C] ui-display.c}, function {@code prt_hp}) reading
     * {@code player->chp}/{@code mhp} directly by comparison. Guarded on the payload shape: a
     * signal for {@code EVENT_HP} carrying anything other than an {@link EventDataStat} is dropped
     * rather than written, mirroring the same guard {@code RedrawHandlers.eventHP} already applies
     * core-side.
     *
     * <p>Method setHP coded on 260926, commented in full on 260926.
     *
     * @param gameEventData the routed payload; must be an {@link EventDataStat} of
     *                      (current, maximum) hit points or nothing is written
     */
    public static void setHP(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataStat(int current, int other)) {
            SidebarModel.setCurrentHP(current);
            SidebarModel.setMaxHP(other);
        }
    }

    /**
     * Unpacks an {@code EVENT_MANA} payload and writes its pair into {@link SidebarModel}, C's
     * {@code prt_sp} ({@code [C] ui-display.c}, function {@code prt_sp}) reading
     * {@code player->csp}/{@code msp} directly by comparison. Guarded on the payload shape, the
     * same way {@link #setHP} is guarded.
     *
     * <p>Method setSP coded on 260926, commented in full on 260926.
     *
     * @param gameEventData the routed payload; must be an {@link EventDataStat} of
     *                      (current, maximum) spell points or nothing is written
     */
    public static void setSP(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataStat(int current, int other)) {
            SidebarModel.setCurrentSP(current);
            SidebarModel.setMaxSP(other);
        }
    }
}
