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

import uk.co.jackoftradesltd.channel.messages.data.*;

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
 * <p>Today it wires {@code EVENT_HP}, {@code EVENT_MANA}, {@code EVENT_RACE_CLASS},
 * {@code EVENT_PLAYERTITLE}, {@code EVENT_PLAYER_NAME} and {@code EVENT_PLAYERLEVEL}; a routing
 * method joins here as each further {@code PR_*} flag gets its own payload record and model, per
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

    /**
     * Unpacks an {@code EVENT_PLAYERTITLE} payload and writes its four parts into
     * {@link SidebarModel}, C's {@code prt_title} ({@code [C] ui-display.c}, function
     * {@code prt_title}) reading {@code player->wizard}, {@code player->total_winner},
     * {@code player->shape->name} and the class title table directly by comparison. Guarded on the
     * payload shape: a signal for {@code EVENT_PLAYERTITLE} carrying anything other than an
     * {@link EventDataStrings} of exactly four strings is dropped rather than written -
     * {@code strings[0]} is always taken as the title text, {@code strings[1]} as the wizard flag,
     * {@code strings[2]} as the total-winner flag and {@code strings[3]} as the shape name, matching
     * the order {@code PlayerCalcs.redrawStuff}'s {@code PR_TITLE} arm sends them in.
     *
     * <p>Method setTitle coded before 260926, commented in full on 260927.
     *
     * @param gameEventData the routed payload; must be an {@link EventDataStrings} of (title,
     *                      wizard flag, total-winner flag, shape name) or nothing is written
     */
    public static void setTitle(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataStrings(String[] strings)) {
            SidebarModel.setTitle(strings[0]);
            SidebarModel.setWizard(strings[1].equals(Boolean.toString(true)));
            SidebarModel.setTotalWinner(strings[2].equals(Boolean.toString(true)));
            SidebarModel.setShapeName(strings[3]);
        }
    }

    /**
     * Unpacks an {@code EVENT_PLAYER_NAME} payload and writes it into {@link SidebarModel}. There
     * is no single C handler this mirrors - {@code get_panel_topleft} ({@code [C] ui-player.c})
     * reads {@code player->full_name} directly rather than through a redraw event, and
     * {@code EVENT_PLAYER_NAME} itself is a port-only addition for the reason its own Javadoc gives.
     * Guarded on the payload shape, the same way {@link #setTitle} is guarded.
     *
     * <p>Method setName coded before 260926, commented in full on 260926.
     *
     * @param gameEventData the routed payload; must be an {@link EventDataString} of the player's
     *                      full name or nothing is written
     */
    public static void setName(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataString(String name)) {
            SidebarModel.setName(name);
        }
    }

    /**
     * Unpacks an {@code EVENT_RACE_CLASS} payload and writes its three parts into
     * {@link SidebarModel}, C's {@code prt_race}/{@code prt_class} ({@code [C] ui-display.c},
     * functions {@code prt_race} and {@code prt_class}) reading {@code player->race->name},
     * {@code class->name} and {@code player_is_shapechanged(player)} directly by comparison.
     * Guarded on the payload shape: a signal for {@code EVENT_RACE_CLASS} carrying anything other
     * than an {@link EventDataStrings} of exactly a race name, a class name and a shapechanged flag
     * is dropped rather than written - {@code raceClass[0]} is always taken as the race,
     * {@code raceClass[1]} as the class and {@code raceClass[2]} as the shapechanged flag, matching
     * the order {@code PlayerCalcs.redrawStuff}'s {@code PR_MISC} arm sends them in.
     *
     * <p>Method setRaceClass coded before 260926, commented in full on 260927.
     *
     * @param gameEventData the routed payload; must be an {@link EventDataStrings} of (race name,
     *                      class name, shapechanged flag) or nothing is written
     */
    public static void setRaceClass(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataStrings(String[] raceClass)) {
            SidebarModel.setRaceName(raceClass[0]);
            SidebarModel.setClassName(raceClass[1]);
            SidebarModel.setPlayerIsShapechanged(raceClass[2].equals(Boolean.toString(true)));
        }
    }

    /**
     * Unpacks an {@code EVENT_PLAYERLEVEL} payload and writes its pair into {@link SidebarModel},
     * C's {@code prt_level} ({@code [C] ui-display.c}, function {@code prt_level}) reading
     * {@code player->lev}/{@code max_lev} directly by comparison. Guarded on the payload shape: a
     * signal for {@code EVENT_PLAYERLEVEL} carrying anything other than an {@link EventDataStat} is
     * dropped rather than written, mirroring the same guard {@link #setHP} and {@link #setSP} apply.
     *
     * <p>Method setPlayerLevel coded on 260927, commented in full on 260927.
     *
     * @param gameEventData the routed payload; must be an {@link EventDataStat} of (current,
     *                      maximum) character level or nothing is written
     */
    public static void setPlayerLevel(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataStat(int level, int maxLevel)) {
            SidebarModel.setLevel(level);
            SidebarModel.setMaxLevel(maxLevel);
        }
    }

    /**
     * Unpacks an {@code EVENT_EXPERIENCE} payload and writes it into {@link SidebarModel}, C's
     * {@code prt_exp} ({@code [C] ui-display.c}, function {@code prt_exp}) reading
     * {@code player->exp}, {@code player->max_exp}, {@code player->lev} and {@code player_exp[]}
     * directly by comparison.
     *
     * <p><b>Unlike this class's other routing methods, {@code EVENT_EXPERIENCE} carries two
     * separate payloads on two separate dispatches</b>, because a single number and a pair of
     * numbers cannot share one payload shape. {@code PlayerCalcs.redrawStuff}'s {@code PR_EXP}
     * arm sends an {@link EventDataLongStat} of (current, maximum) experience first, then a lone
     * {@link EventDataLong} carrying whichever figure C's local {@code xp} would hold - the
     * experience to the next level, or the current total at level fifty. This method is routed
     * both times and writes whichever of the two shapes it is handed; neither {@code if} is an
     * {@code else}, and a single call only ever matches one of them.
     *
     * <p>Method setExperience coded on 260927, commented in full on 260928.
     *
     * @param gameEventData the routed payload; an {@link EventDataLongStat} of (current, maximum)
     *                      experience writes {@link SidebarModel#setExperience} and
     *                      {@link SidebarModel#setMaxXp}, an {@link EventDataLong} of the
     *                      displayed figure writes {@link SidebarModel#setXpToLevel}, and
     *                      anything else is dropped
     */
    public static void setExperience(GameEventData gameEventData) {
        if (gameEventData instanceof EventDataLong(long exp)) {
            SidebarModel.setXpToLevel(exp);
        }
        if (gameEventData instanceof EventDataLongStat(long current, long other)) {
            SidebarModel.setExperience(current);
            SidebarModel.setMaxXp(other);
        }
    }
}
