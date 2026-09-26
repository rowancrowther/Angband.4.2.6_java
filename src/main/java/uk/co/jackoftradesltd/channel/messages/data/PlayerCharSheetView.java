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
 * Immutable snapshot of the character-sheet values the still-unported {@code display_panel}
 * family in {@code src/ui-player.c} reads directly from {@code player}/{@code player->upkeep} —
 * the same boundary-crossing shape as {@link PlayerStatusView}, split out into its own record
 * because these fields have nothing to do with the {@code prt_*} sidebar family
 * {@link PlayerStatusView} otherwise mirrors. As with {@link PlayerStatusView}, there is no
 * single C struct behind this shape; {@link PlayerEventStatusUpdate} accumulates the pushed
 * values into one {@code PlayerCharSheetView} per change and hands the whole snapshot to the UI
 * thread through {@link PlayerEventStatusUpdate#getPlayerCharSheetView()}.
 *
 * <p>Being a record, every field here is set once at construction and never mutated in place;
 * {@link PlayerEventStatusUpdate}'s {@code updatePlayerCharSheet*} family changes one field at a
 * time only by building a whole new {@code PlayerCharSheetView} from the current one, unlike C's
 * direct mutation of the live {@code player} struct.
 *
 * <p>Record PlayerCharSheetView coded on 260925, commented in full on 260925.
 *
 * @param bodyCount              the player's body-part count, the port of
 *                               {@code player->body.count} as read by
 *                               {@code configure_char_sheet}/{@code have_valid_char_sheet_config}
 *                               ({@code src/ui-player.c}) to size the character screen's
 *                               resistance-panel column count; a separate field from
 *                               {@link PlayerStatusView#equipmentSlotCount()} even though both
 *                               trace back to the same C value, because C reads
 *                               {@code player->body.count} live at each call site and this
 *                               snapshot design has no single global to read from
 * @param playerIsPlaying        whether the player is actively in a live game, the port of C's
 *                               {@code player->upkeep->playing} check in {@code display_player}
 *                               ({@code src/ui-player.c}), which skips repainting the character
 *                               screen in a background sub-window once play has ended
 * @param playerRaceStatBonuses  the five racial stat-bonus values, the "RB" column in
 *                               {@code display_player_stat_info} ({@code src/ui-player.c}), which
 *                               reads {@code player->race->r_adj[stat]}
 * @param playerClassStatBonuses the five class stat-bonus values, the "CB" column in
 *                               {@code display_player_stat_info} ({@code src/ui-player.c}), which
 *                               reads {@code player->class->c_adj[stat]}
 * @param playerEquipStatBonuses the five equipment stat-bonus values, the "EB" column in
 *                               {@code display_player_stat_info} ({@code src/ui-player.c}), which
 *                               reads {@code player->state.stat_add[stat]}
 * @param playerTotalStatBonuses the five resulting-maximum stat values, the "Best" column in
 *                               {@code display_player_stat_info} ({@code src/ui-player.c}), which
 *                               reads {@code player->state.stat_top[stat]} — the natural maximum
 *                               after racial, class and equipment bonuses are applied
 * @param playerCurrModStat      the five current (drained) stat values, the port of C's
 *                               {@code player->state.stat_use[stat]} read in
 *                               {@code display_player_stat_info} ({@code src/ui-player.c}), shown
 *                               only for a stat currently below its maximum
 * @param totalWeight            the total weight the player is carrying, in tenth-pounds — C's
 *                               {@code player->upkeep->total_weight} ({@code player.h:487}), read
 *                               by the "Burden" line in {@code get_panel_midleft}
 *                               ({@code src/ui-player.c}); the Java side reads the same value via
 *                               {@code Player.getPlayerUpkeep().getTotalWeight()}
 * @param weightLimit            the player's carry capacity before burden, in tenth-pounds —
 *                               C's raw {@code adj_str_wgt[player->state.stat_ind[STAT_STR]]}
 *                               table entry ({@code src/player-calcs.c}), deliberately
 *                               unscaled: C's own {@code weight_limit} helper (also
 *                               {@code src/player-calcs.c}) returns that same table entry ×100
 *                               for a different calculation, but C's {@code weight_remaining}
 *                               reads the table directly rather than through that helper, and
 *                               this field matches that raw reading for
 *                               {@code uk.co.jackoftradesltd.frontend.ui.UIPlayer#weightRemaining()}
 *                               to use — not the same quantity as
 *                               {@link uk.co.jackoftradesltd.middle.player.PlayerState#weightLimit()}
 *                               despite the shared name; computed on the middle side and pushed
 *                               across so the UI thread does not have to reach back over the
 *                               frontend/middle boundary to derive it itself
 * @author Rowan Crowther
 */
public record PlayerCharSheetView(int bodyCount,
                                  boolean playerIsPlaying,
                                  int[] playerRaceStatBonuses,
                                  int[] playerClassStatBonuses,
                                  int[] playerEquipStatBonuses,
                                  int[] playerTotalStatBonuses,
                                  int[] playerCurrModStat,
                                  int totalWeight,
                                  int weightLimit,
                                  long[] expToLevel,
                                  int expFactor,
                                  int height,
                                  int weight,
                                  int age,
                                  int toA,
                                  int toD,
                                  int toH,
                                  int meleeSkill,
                                  int shootSkill,
                                  int bthPlusAdj,
                                  int meleeDice,
                                  int meleeSides,
                                  int numBlows,
                                  int numShots,
                                  int saveSkill,
                                  int stealthSkill,
                                  int disarmPhysSkill,
                                  int disarmMagicSkill,
                                  int deviceSkill,
                                  int searchSkill,
                                  int infra,
                                  int calcSpeed,
                                  boolean optionEffectiveSpeed) {
}