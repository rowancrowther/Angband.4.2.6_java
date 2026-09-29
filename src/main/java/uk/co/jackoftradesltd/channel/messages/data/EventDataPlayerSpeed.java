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
 * The payload of an {@code EVENT_PLAYERSPEED} message - everything C's {@code prt_speed_aux}
 * ({@code [C] ui-display.c}, function {@code prt_speed_aux}) reads at draw time, packed so the
 * front end never has to follow a pointer back into the core. C reads {@code player->state.speed},
 * {@code OPT(player, effective_speed)} and, for the multiplier form, the global
 * {@code extract_energy[]} table ({@code game-world.c}) at two indices; none of them is visible
 * across the channel boundary, so their values travel with the signal. C's {@code redraw_events}
 * table ({@code player-calcs.c}) fires {@code EVENT_PLAYERSPEED} as a bare signal.
 *
 * <p>The two energy figures are the table entries, not the speed: {@code extractEnergy} is
 * {@code extract_energy[speed]} and {@code extractEnergyNormal} is {@code extract_energy[110]}, so
 * the front end forms the "Fast (1.5x)" multiplier as
 * {@code 10 * extractEnergy / extractEnergyNormal} without holding the table. They are meaningful
 * only when {@code optEffectiveSpeed} is {@code true}, but are always filled in.
 *
 * <p>One instance is dispatched per {@code PR_SPEED} redraw, by
 * {@link uk.co.jackoftradesltd.middle.player.PlayerCalcs#redrawStuff}'s {@code PR_SPEED} case;
 * {@code RedrawRouter.setPlayerSpeed} unpacks it into {@code SidebarModel}.
 *
 * <p>Class EventDataPlayerSpeed coded on 260929, commented in full on 260929.
 *
 * @param speed               the player's speed, C's {@code player->state.speed}; 110 is normal
 *                            and draws nothing
 * @param optEffectiveSpeed   whether to show the speed as a multiplier rather than a signed
 *                            offset, C's {@code OPT(player, effective_speed)}
 * @param extractEnergy       the energy gained per game turn at {@code speed}, C's
 *                            {@code extract_energy[speed]}
 * @param extractEnergyNormal the energy gained per game turn at normal speed, C's
 *                            {@code extract_energy[110]}
 * @author Rowan Crowther
 */
public record EventDataPlayerSpeed(int speed,
                                   boolean optEffectiveSpeed,
                                   int extractEnergy,
                                   int extractEnergyNormal) implements GameEventData {
}
