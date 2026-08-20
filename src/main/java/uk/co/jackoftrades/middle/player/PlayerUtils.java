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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.cave.store.Store;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.player.enums.PlayerOverExertion;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

/**
 * Free-standing player helper routines — the port of C's {@code player-util.c}.
 *
 * <p>Holds behaviour that operates on a {@link Player} but does not belong to the player's own data
 * model: resting/interrupt handling, terrain interaction and similar utilities the game loop and
 * commands call through. Modelled as static methods over a passed-in {@link Player}, mirroring C's
 * free functions that take {@code struct player *} rather than reading a global.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop; individual routines are ported as callers
 * need them.
 *
 * @author Rowan Crowther
 */
public class PlayerUtils {
    /**
     * The size of one point of stat bonus above {@link #statNormalMax} — the {@code 10} in C's
     * {@code modify_stat_value}, where 18/10 is stored as 28 and 18/20 as 38.
     */
    private static final int statBigStep = 10;
    /**
     * The value at which the stat scale changes gear: at or below this a point of bonus is worth
     * one, above it {@link #statBigStep}. C writes the number 18 inline throughout
     * {@code player-util.c:339-369}.
     */
    private static final int statNormalMax = 18;
    /**
     * The floor a stat penalty cannot drive a stat below — C's bare {@code 3} in the descending
     * branch of {@code modify_stat_value}. Three is the lowest a stat can be rolled or reduced to.
     */
    private static final int statNormalMin = 3;
    /**
     * The player these utilities act on, cached from {@link GameState} — the port of C's
     * {@code player} global, which its free functions reach for directly.
     *
     * <p>Static and resolved once in the initialiser below, so it is fixed for the life of the
     * class rather than following a later change of character. Methods that take a {@link Player}
     * parameter should use that instead; this is for the ones ported from C functions that read the
     * global.
     */
    private static Player player;

    /**
     * Caches the current player at class-initialisation time.
     *
     * <p><b>Load-order dependency:</b> this runs the first time anything touches this class, so
     * {@link GameState} must already hold a player by then. Nothing re-runs it, so a character
     * created or loaded afterwards is not picked up.
     */
    static {
        player = GameState.getPlayer();
    }

    /**
     * Check whether an in-progress rest should end for a special reason — the port of C's
     * {@code player_resting_complete_special}. Called at the top of the player-processing pass so a
     * rest can be interrupted the moment its stopping condition (e.g. HP/SP restored) is met.
     *
     * <p><b>Stub:</b> not yet implemented — takes no action until the resting subsystem is ported.
     */
    public static void restingCompleteSpecial() {
        // Stub class TODO: implement
    }

    /**
     * Apply any damage the terrain under the player inflicts — the port of C's
     * {@code player_take_terrain_damage} ({@code player-util.c}). Some features (lava and similar)
     * hurt whatever stands on them; this works out the damage for the player's current grid, applies
     * it, and reports the cause. Called once per turn as part of the post-command cleanup.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param grid   the grid to test — normally the player's own location
     */
    public static void takeTerrainDamage(Loc grid) {
        // Stub class TODO: implement
    }

    /**
     * Applies damage to the player from a named source, handling death if it is fatal — the port of
     * C's {@code take_hit} ({@code player-util.c}).
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param damage the amount of damage to inflict
     * @param cause  the death-message cause, used if the hit is fatal
     */
    public static void takeHit(int damage, String cause) {
        // Stub class TODO: implement
    }

    /**
     * Reduces an incoming damage figure by the player's protections — the port of C's percentage
     * damage-reduction handling. Invulnerability nullifies non-massive hits outright; otherwise the
     * player's percentage reduction is applied, and the result is floored at zero.
     *
     * @param damage the raw incoming damage
     * @return the damage remaining after reduction (never negative)
     */
    public static int applyDamageReduction(int damage) {
        // Hack - apply invulnerability
        if (player.getTimedEffect(TimedEffect.TMD_INVULN) != 0 && (damage < 9000)) return 0;

        damage -= player.getPlayerState().perDamRed();

        if (damage > 0 && player.getPlayerState().perDamRed() != 0) {
            damage -= (damage * player.getPlayerState().perDamRed() / 100);
        }

        return Math.max(damage, 0);
    }

    /**
     * Applies the cost of over-exerting the player (e.g. casting a spell without enough mana) — the
     * port of C's {@code player_over_exert} ({@code player-util.c}). The {@code flag} selects which
     * penalties (mana loss, stun, HP damage, fainting) may apply, each with the given probability.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param flag   the set of over-exertion penalties permitted
     * @param chance the percentage chance each penalty triggers
     * @param amount the magnitude of the penalty
     */
    public static void overExert(Flag<PlayerOverExertion> flag, int chance, int amount) {
        // Stub class TODO: implement
    }

    /**
     * Interrupts the player's current rest/run/repeat and refreshes state — the port of C's
     * {@code disturb} ({@code player-util.c}), called when something demands the player's attention.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public static void disturb() {
        // Stub class TODO: implement
    }

    /**
     * Regenerates the player's hit points for the turn — the port of C's HP regeneration in
     * {@code player-util.c}.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public static void regenHP() {
        // Stub class TODO: implement
    }

    /**
     * Regenerates the player's spell points (mana) for the turn — the port of C's mana regeneration
     * in {@code player-util.c}.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public static void regenMana() {
        // Stub class TODO: implement
    }

    /**
     * Recomputes the player's light radius from equipped light sources and effects — the port of C's
     * light-update handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public static void updateLight() {
        // Stub class TODO: Implement this
    }

    /**
     * Moves the player to a new dungeon depth — the port of C's {@code dungeon_change_level}
     * ({@code player-util.c}). Records the new depth, restocks the stores when returning to town
     * after the first day, flags a fresh level for generation, and requests an autosave.
     *
     * @param dungeonLevel the depth to descend/ascend to (0 = town)
     */
    public static void dungeonChangeLevel(int dungeonLevel) {
        // Record the new depth
        player.setDepth(dungeonLevel);

        // Are we returning to town
        if (dungeonLevel == 0 && GameState.getDaycount() != 0)
            Store.storeUpdate();

        // Make new level
        player.getPlayerUpkeep().setGenerateLevel(true);

        // Save the game when we reach the new level
        player.getPlayerUpkeep().setAutosave(true);
    }

    /**
     * Works out the depth the player would actually arrive at when descending, honouring the
     * force-descend stair-skip, the dungeon's depth bounds, and any intervening quest level — the
     * port of C's {@code dungeon_get_next_level} ({@code player-util.c}). If a quest sits between the
     * current and target depths, the player is stopped at the quest level.
     *
     * @param dungeonLevel the current depth
     * @param added        the number of levels to move (may be scaled by the stair-skip)
     * @return the resulting depth, clamped to the dungeon's limits and to any intervening quest
     */
    public static int dungeonGetNextLevel(int dungeonLevel, int added) {
        int targetLevel;
        int index;

        // Get Target Level
        targetLevel = dungeonLevel + added * GameConstants.getWorldStairSkip();

        // Don't allow levels below dungeon max
        targetLevel = Math.min(targetLevel, GameConstants.getWorldMaxDepth() - 1);

        // Don't allow levels above the town
        targetLevel = Math.max(targetLevel, 0);

        // Check intermediate levels for quests
        for (index = dungeonLevel; index < targetLevel; index++) {
            if (player.isQuest(index))
                return index;
        }

        return targetLevel;
    }

    /**
     * Searches the player's surroundings for hidden things — the port of C's {@code search}.
     *
     * <p><b>Stub:</b> not yet implemented. Left in place so the callers that C invokes it from can
     * be ported without a hole; it will take effect once the trap and door detection it depends on
     * exists.
     */
    public static void search() {
        // Stub function TODO: Implement
    }

    /**
     * Applies a bonus or penalty to a stat value, in the game's two-speed stat scale — the port of
     * C's {@code modify_stat_value} ({@code player-util.c:339}).
     *
     * <p>A stat is stored as a number that runs 3 to 18 and then jumps: 18/10, 18/20 and so on are
     * held as 28, 38, and the "percentile" part is the amount above 18. So a point of bonus is
     * worth one below 18 and ten above it, which is the whole of what this method encodes. It is
     * called wherever a stat's raw value has to be turned into the value actually in play:
     * {@code calc_bonuses} runs it twice per stat, once on {@code stat_max} to get
     * {@code state->stat_top} and once on {@code stat_cur} to get {@code state->stat_use}
     * ({@code player-calcs.c:2060}), and birth does the same to show the character's starting
     * numbers ({@code player-birth.c:272}).
     *
     * <p>The points are applied one at a time in a loop rather than in one arithmetic step, and
     * that is not laziness in the original: because the step size changes at 18, a bonus that
     * crosses the boundary is worth different amounts in its two halves, and only walking it point
     * by point gets that right. Adding two to a 17 gives 18 then 28, not 19 or 37.
     *
     * <p>Gains are unbounded — above 18 every point simply adds another ten, and nothing here caps
     * the result. The cap is imposed elsewhere, by the stat table the value is later looked up in.
     *
     * <p>Losses are bounded twice, and asymmetrically:
     *
     * <ul>
     *     <li>at or above 28, a point removes ten;</li>
     *     <li>between 19 and 27 — a value the scale should never hold, since the percentile part
     *     only ever moves in tens — a point snaps the stat back to a clean 18. C comments this
     *     branch "Prevent weirdness";</li>
     *     <li>at or below 3, a point does nothing. Three is the floor of the scale, and a drained
     *     character cannot be pushed below it.</li>
     * </ul>
     *
     * <p>The floor makes losses non-reversible in a way gains are not: eight points of drain on a
     * stat of 5 leaves 3, and returning the eight points leaves 11. That is the game's behaviour,
     * not an artefact of the port.
     *
     * <p>Neither bound is applied to the value on the way in. A caller passing a stat already below
     * 3 gets it back untouched when the amount is negative, but the positive branch will happily
     * walk it up from there.
     *
     * <p><b>Minor divergence from the C original.</b> C returns {@code int16_t}, so a large enough
     * result would wrap; this returns {@code int} and does not. It takes about three thousand points
     * of bonus to tell the two apart, which no caller can supply, so the difference is a note rather
     * than a behaviour change.
     *
     * <p>Function modifyStatValue coded on 260818, commented in full on 260818.
     *
     * @param value  the stat value to modify, in the 3-to-18-then-tens scale
     * @param amount the bonus (positive) or penalty (negative) in points; zero returns the value
     *               unchanged
     * @return the modified stat value
     */
    public static int modifyStatValue(int value, int amount) {
        if (amount > 0) {
            for (int index = 0; index < amount; index++) {
                if (value < statNormalMax)
                    value++;
                else
                    value += statBigStep;
            }
        } else if (amount < 0) {
            for (int index = 0; index < (-amount); index++) {
                if (value >= statNormalMax + statBigStep)
                    value -= statBigStep;
                else if (value > statNormalMax)
                    value = statNormalMax;
                else if (value > statNormalMin)
                    value--;
            }
        }

        return value;
    }
}
