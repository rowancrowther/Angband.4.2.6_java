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
 * {@link GameEventData} payload carrying one stat row's full redraw state — the Java stand-in for
 * C's {@code prt_stat} ({@code [C] ui-display.c}, function {@code prt_stat}) reading
 * {@code player->stat_cur[stat]}, {@code player->stat_max[stat]} and
 * {@code player->state.stat_use[stat]} directly off the shared {@code player} global. C's five
 * {@code prt_str}/{@code prt_int}/{@code prt_wis}/{@code prt_dex}/{@code prt_con} wrappers
 * ({@code [C] ui-display.c}) all funnel into that one function, passing only which stat index to
 * read; this record carries the same index across the boundary alongside the three values that
 * index would otherwise be used to look up directly.
 *
 * <p>{@code statIndex} is C's {@code STAT_STR}..{@code STAT_CON} constant (0..4, see
 * {@link uk.co.jackoftradesltd.middle.enums.Stats#getValue()}); {@code current} and {@code max}
 * are {@code stat_cur[statIndex]}/{@code stat_max[statIndex]}, compared by {@code prt_stat} to
 * decide whether the row is drawn drained (reduced name, yellow) or full (normal name, light
 * green); {@code use} is {@code state.stat_use[statIndex]}, the value actually displayed, which
 * can differ from {@code current} once equipment or a temporary effect has modified it.
 *
 * <p>One instance is dispatched per stat, in
 * {@link uk.co.jackoftradesltd.middle.player.PlayerCalcs#redrawStuff}'s {@code PR_STATS} case,
 * unlike every other {@code PR_*} handler here which sends a single event for the whole redraw —
 * C's own {@code PR_STATS} bit likewise redraws all five stat rows under the one flag, but each
 * row is still an independent call to {@code prt_stat}.
 *
 * <p>Class EventDataFullStat coded on 260927, commented in full on 260928.
 *
 * @param statIndex the stat being reported, C's {@code STAT_STR}..{@code STAT_CON} index
 * @param current   the stat's current value — C's {@code player->stat_cur[statIndex]}
 * @param max       the stat's recorded maximum — C's {@code player->stat_max[statIndex]}
 * @param use       the value actually displayed — C's {@code player->state.stat_use[statIndex]}
 * @author Rowan Crowther
 */
public record EventDataFullStat(int statIndex, int current, int max, int use) implements GameEventData {
}
