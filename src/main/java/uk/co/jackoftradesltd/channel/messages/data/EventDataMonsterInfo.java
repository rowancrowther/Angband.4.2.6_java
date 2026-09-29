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

package uk.co.jackoftradesltd.channel.messages.data;

/**
 * {@link GameEventData} payload carrying everything the sidebar's monster health bar needs about
 * the tracked monster - the Java stand-in for C's {@code prt_health_aux} and
 * {@code monster_health_attr} ({@code [C] ui-display.c}) reading {@code player->upkeep->health_who}
 * and, through it, {@code mon->hp}, {@code mon->maxhp}, {@code monster_is_visible(mon)} and
 * {@code mon->m_timed[]} straight off the shared monster, plus {@code player->timed[TMD_IMAGE]}
 * off the shared {@code player}. A handler on the far side of the core-to-front-end boundary has
 * neither, so every value either function reads travels with the signal instead.
 *
 * <p>{@code monsterExists} is C's {@code health_who != NULL}: when it is {@code false} nothing is
 * being tracked, the bar is erased, and every other component is a placeholder ({@code 0} or
 * {@code false}) that the front end must not interpret - except {@code playerTmdImage}, which is
 * still filled in. The seven monster booleans ({@code feared} to {@code held}) are C's
 * {@code mon->m_timed[MON_TMD_x] != 0} tests, not durations: the bar's colour depends only on
 * whether an effect is active, and the last-listed active one wins.
 *
 * <p>One instance is dispatched per {@code PR_HEALTH} redraw, by
 * {@link uk.co.jackoftradesltd.middle.player.PlayerCalcs#redrawStuff}'s {@code PR_HEALTH} case,
 * under {@code EVENT_MONSTERHEALTH}; {@code RedrawRouter.setMonsterHealth} unpacks it into
 * {@code SidebarModel}.
 *
 * <p>Class EventDataMonsterInfo coded on 260929, commented in full on 260929.
 *
 * @param monHP          the tracked monster's current hit points - C's {@code mon->hp}; negative
 *                       means dead, and is drawn as an unknown bar
 * @param monMaxHP       the tracked monster's maximum hit points - C's {@code mon->maxhp}
 * @param monsterExists  whether a monster is being tracked at all - C's {@code health_who != NULL}
 * @param monsterVisible whether the player can see the tracked monster - C's
 *                       {@code monster_is_visible(mon)}
 * @param feared         whether the monster is afraid - C's {@code m_timed[MON_TMD_FEAR]}
 * @param disen          whether the monster is disenchanted - C's {@code m_timed[MON_TMD_DISEN]}
 * @param command        whether the monster is commanded - C's {@code m_timed[MON_TMD_COMMAND]}
 * @param conf           whether the monster is confused - C's {@code m_timed[MON_TMD_CONF]}
 * @param stuned         whether the monster is stunned - C's {@code m_timed[MON_TMD_STUN]}
 * @param slept          whether the monster is asleep - C's {@code m_timed[MON_TMD_SLEEP]}
 * @param held           whether the monster is held - C's {@code m_timed[MON_TMD_HOLD]}
 * @param playerTmdImage whether the player is hallucinating - C's {@code player->timed[TMD_IMAGE]}
 * @author Rowan Crowther
 */
public record EventDataMonsterInfo(int monHP,
                                   int monMaxHP,
                                   boolean monsterExists,
                                   boolean monsterVisible,
                                   boolean feared,
                                   boolean disen,
                                   boolean command,
                                   boolean conf,
                                   boolean stuned,
                                   boolean slept,
                                   boolean held,
                                   boolean playerTmdImage) implements GameEventData {
}
