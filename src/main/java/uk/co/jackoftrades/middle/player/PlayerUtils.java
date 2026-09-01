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
import uk.co.jackoftrades.middle.numerics.RandomValueUtils;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
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
 * <p>Two shapes of method live here side by side. Those ported from C functions that read the
 * {@code player} global take no player and use the cached {@link #player} field; those ported from
 * C functions that take a {@code struct player *} - and the ones moved here from {@link Player} as
 * the port caught up with C's file layout - take the player as their first parameter. Prefer the
 * parameter form: the cached field is fixed at class-initialisation time and does not follow a
 * later change of character.
 *
 * <p><b>Status:</b> a stub landed to unblock the game loop; individual routines are ported as callers
 * need them, so several methods below still do nothing and say so.
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
     * player's percentage reduction is applied, and the result is floored at zero. C runs the flat
     * reduction first ({@code dam -= p->state.dam_red}) and only then the percentage one
     * ({@code perc_dam_red}), so a player with both keeps the flat points off the top.
     *
     * @param damage the raw incoming damage
     * @return the damage remaining after reduction (never negative)
     */
    public static int applyDamageReduction(int damage) {
        // Hack - apply invulnerability
        if (player.getTimedEffect(TimedEffect.TMD_INVULN) != 0 && (damage < 9000)) return 0;

        damage -= player.getPlayerState().getDamRed();

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
            if (PlayerQuest.isQuest(player, index))
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

    /**
     * Makes a bloodlust-driven attack on a random adjacent monster in place of the player's chosen
     * command - the port of C's {@code player_attack_random_monster}, invoked from
     * {@link uk.co.jackoftrades.middle.game.gameengine.CommandProcessor#processCommand} when a
     * bloodlust check fires. <b>Stub:</b> the attack itself is not yet ported, so this currently takes
     * no action and reports that no attack was made.
     *
     * @param player the character making the attack; a confused player never does, which is the one
     *               clause of C's that is ported
     * @return {@code true} if an attack was made (so the original command should be abandoned);
     * {@code false} otherwise - always {@code false} while stubbed
     */
    public static boolean attackRandomMonster(Player player) {
        int index;
        int direction = RandomValueUtils.randInt0(8);

        if (player.getTimedEffect(TimedEffect.TMD_CONFUSED) != 0) return false;

        for (index = 0; index < 8; index++, direction++) {

            // DO stuff - this is currently a stub class

        }

        return false;
    }

    /**
     * Reads the resting counter — the port of C's {@code player_resting_count}.
     *
     * <p>A plain read of the upkeep's {@code resting} field, with no interpretation: a positive value
     * is the number of rest turns still to run, zero means not resting, and a negative value is one of
     * the "rest until a condition is met" sentinels classified by {@link #restingIsSpecial(int)}. C
     * stores the field as an {@code int16_t} and this returns a Java {@code int}, which is a widening
     * of the same value; the sentinels are compared for equality rather than ordered, so the wider type
     * changes nothing.
     *
     * <p>Function playerRestingCount coded on 260828, commented in full on 260828.
     *
     * @param player the character whose upkeep is read; the counter belongs to the player, not to
     *               anything shared, so two characters rest independently
     * @return the resting counter: turns of rest remaining, or a special "rest until…" sentinel
     */
    public static int playerRestingCount(Player player) {
        return player.getPlayerUpkeep().getRestingCounter();
    }

    /**
     * Tests whether the player holds the given object flag permanently - the port of C's
     * {@code player_of_has_not_timed} ({@code player-timed.c:747}).
     *
     * <p>The answer is rebuilt from scratch rather than read off the calculated state, and that is
     * the whole point of the method. Its sibling {@code player_of_has} reads
     * {@code p->state.flags}, and {@code calcBonuses} finishes by folding the object-flag duplicate
     * of every running timed effect into that set, so the state answers "yes" for a player who is
     * merely temporarily heroic. Here the collector is filled from {@link Player#playerFlags} - race,
     * class, and the level-30 bravery grant - and then unioned with the flags of every worn item,
     * so a flag that arrived by a timed effect is not seen.
     *
     * <p>{@code setTimed} uses it for exactly that distinction: a message about gaining a
     * protection is suppressed only when the player already has that protection for keeps.
     *
     * <p>Empty slots are skipped; the scratch set handed to {@link ItemObject#objectFlags} is wiped
     * on entry there, so items accumulate into the collector rather than overwriting one another.
     *
     * <p>Function playerOfHasNotTimed coded on 260829, commented in full on 260829.
     *
     * @param player     the character to ask about; its race, class, level and worn equipment are
     *                   what the answer is built from
     * @param objectFlag the flag to ask about
     * @return {@code true} if the race, the class or a worn item grants it, ignoring timed effects
     */
    public static boolean playerOfHasNotTimed(Player player, ObjectFlag objectFlag) {
        Flag<ObjectFlag> collectFlags = new Flag<>(ObjectFlag.class);
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);

        player.playerFlags(player.getPlayerState(), collectFlags);

        for (EquipSlot slot : player.getPlayerBody().getSlots()) {
            ItemObject slotObject = slot.getItem();

            if (slotObject == null) continue;
            slotObject.objectFlags(flags);
            collectFlags.union(flags);
        }

        return (collectFlags.has(objectFlag));
    }

    /**
     * Tests whether the player is immune to the given element - the port of C's
     * {@code player_is_immune}.
     *
     * <p>Immunity is not a separate flag: it is the top of the resistance scale, so the test is an
     * exact match on a resistance level of 3 (C's {@code p->state.el_info[element].res_level == 3}).
     * The literal is deliberate rather than a {@code >=} comparison, exactly as in the original -
     * nothing raises a player's level above 3, and the equality is what C checks. Vulnerability
     * ({@code -1}), no resistance ({@code 0}) and ordinary resistance ({@code 1}) all return
     * {@code false}.
     *
     * <p>The reading is taken from the calculated state, not the known state, so it reflects what is
     * true of the player rather than what they have learned.
     *
     * <p>Function playerIsImmune coded on 260829, commented in full on 260829.
     *
     * @param player  the character to ask about; the reading comes from their calculated state,
     *                which {@code calcBonuses} must therefore have filled
     * @param element the element to test; must be one of the real elements, since the state's
     *                per-element map holds no entry for {@code ELEM_NONE} or {@code ELEM_MAX}
     * @return {@code true} if the player's resistance level for that element is exactly 3
     */
    public static boolean playerIsImmune(Player player, ElementEnum element) {
        return player.getPlayerState().getElInfo().get(element).getResLevel() == 3;
    }

    /**
     * Recomputes and stores the depth Word of Recall should return the player to — the port of C's
     * recall-depth handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     */
    public static void setRecallDepth() {
        // Stub function TODO: implement
    }

    /**
     * Tests whether the given resting counter denotes one of the "rest until a condition is met"
     * sentinel values (as opposed to a fixed turn count) — the port of C's special resting-count
     * handling.
     *
     * <p><b>Stub:</b> not yet implemented.
     *
     * @param restingCounter the resting counter to classify
     * @return {@code true} if the counter is a special "rest until…" value
     */
    static boolean restingIsSpecial(int restingCounter) {
        // Stub function TODO: implement
        return false;
    }
}
