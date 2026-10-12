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

package uk.co.jackoftradesltd.middle.combat;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.cave.ChunkUtils;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.Command;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.Objects;

/**
 * The player's current attack/spell target: either a specific {@link Monster} or a bare grid
 * {@link Loc} the player is aiming at. This is the Java home of the file-level state in the C
 * original's {@code target.c}: the {@code target_set} and {@code target_fixed} flags and the
 * {@code struct target} ({@code target.h}) holding {@code midx} and {@code grid}. All of it is
 * static, because C keeps it in file-scope statics and there is only ever one player target.
 *
 * <p>C identifies the targeted monster only by its index, {@code midx}, with {@code 0} meaning "no
 * monster, grid only". Java keeps that index in {@link #targetMonsterIndex} and resolves it through
 * the cave with {@link #getTargetMonster(Chunk)}; the extra {@link #monster} reference has no C
 * counterpart and is kept in step with the index by {@link #setTargetMonster(Monster)}.
 *
 * <p>Class Target coded before 261001, commented in full on 261001.
 *
 * @author Rowan Crowther
 */
public class Target {
    /**
     * The targeted grid location, {@code target.grid} in C. {@link Loc#zero} when nothing is
     * targeted.
     *
     * <p>Never {@code null}: it starts as {@link Loc#zero}, as C's zeroed file-scope {@code target}
     * does, and {@link #setGrid} turns a {@code null} into {@link Loc#zero}.
     *
     * <p>Field grid initialised to {@code Loc.zero} on 261012.
     */
    private static Loc grid = Loc.zero;

    /**
     * Whether the target is fixed for the duration of a spell, {@code target_fixed} in C. While it
     * is set, {@link #setTargetMonster(Monster)} will not unset the target when handed a monster
     * that has died or become untargetable, so later effects of the same spell can still aim at
     * the grid.
     */
    private static boolean targetFixed;

    /**
     * Whether a target is currently set, {@code target_set} in C.
     */
    private static boolean targetSet;

    /**
     * The targeted monster, or {@code null} when only a grid is targeted. Has no C counterpart,
     * where {@code target.midx} is the only record of which monster is targeted.
     */
    private static Monster monster;

    /**
     * The cave index of the targeted monster, {@code target.midx} in C. {@code 0} means no monster
     * is targeted.
     */
    private static int targetMonsterIndex;

    /**
     * Sets the target to a monster, or to nobody, ported from {@code target_set_monster}
     * ({@code target.c}). A monster that passes {@link #targetable(Monster)} becomes the target:
     * the set flag is raised and its index and grid are recorded. Otherwise, if the target is
     * fixed, the index is zeroed but the grid, and the set flag, are left alone, so a monster that
     * dies mid-spell leaves its grid behind as the aiming point. With the target not fixed, the
     * target is reset to unset, index {@code 0} and {@link Loc#zero}.
     *
     * <p>C reads the index from {@code mon->midx}; this method takes it from
     * {@link Monster#getMonIndex()} in the same way, rather than from the caller.
     *
     * <p>Method setTargetMonster coded before 261001, commented in full on 261001.
     *
     * @param monster the monster to target, or {@code null} to clear the target
     * @return {@code true} if a monster was targeted, or the target is fixed; {@code false} if the
     * target was reset
     */
    public static boolean setTargetMonster(Monster monster) {
        if (monster != null && targetable(monster)) {
            targetSet = true;
            Target.monster = monster;
            targetMonsterIndex = monster.getMonIndex();
            Target.grid = monster.getGrid();
            return true;
        } else if (targetFixed) {
            targetMonsterIndex = 0;
            Target.monster = null;
            return true;
        }

        targetSet = false;
        targetMonsterIndex = 0;
        Target.monster = null;
        grid = Loc.zero;
        return false;
    }

    /**
     * Returns the currently targeted monster, ported from {@code target_get_monster}
     * ({@code target.c}). The monster is looked up in the given cave by the stored index, so
     * the result is {@code null} when the index is {@code 0} (grid-only target or no target) or
     * the slot is empty, as {@code cave_monster} returns in C.
     *
     * <p>Method getTargetMonster coded before 261001, commented in full on 261001.
     *
     * @param cave the cave to look the monster up in
     * @return the monster in the targeted slot, or {@code null} if there is none
     */
    public static Monster getTargetMonster(Chunk cave) {
        return cave.caveMonster(targetMonsterIndex);
    }

    /**
     * Determines whether a monster makes a reasonable target, ported from {@code target_able}
     * ({@code target.c}). A monster is targetable if it exists, has a race, is obvious (visible
     * and not camouflaged), the player can hit it with a projection carrying no flags
     * ({@code PROJECT_NONE}), and the player is not hallucinating ({@code TMD_IMAGE}). The checks
     * run in that order and stop at the first failure, as the C {@code &&} chain does.
     *
     * <p>Method targetable coded before 261001, commented in full on 261001.
     *
     * @param monster the candidate monster, may be {@code null}
     * @return {@code true} if the monster can be targeted
     */
    public static boolean targetable(Monster monster) {
        Player player = GameState.getPlayer();
        Flag<ProjectEnum> flag = new Flag<>(ProjectEnum.class, ProjectEnum.PROJECT_NONE);
        return monster != null && monster.getMonsterRace() != null && monster.isObvious()
                && ChunkUtils.isProjectable(GameState.getCave(), player.getGrid(), monster.getGrid(), flag)
                && player.getTimedEffect(TimedEffect.TMD_IMAGE) == 0;
    }

    /**
     * Tells the UI whether a target is currently set, the port of C's {@code target_is_set}
     * ({@code target.c}). Reads {@link #targetSet} and nothing else, so it says nothing about
     * whether the target is still usable; {@link #targetOkay()} answers that.
     *
     * <p>Method isTargetSet coded before 261009, commented in full on 261009.
     *
     * @return {@code true} if a monster or grid target has been set
     */
    public static boolean isTargetSet() {
        return targetSet;
    }

    /**
     * Overwrites the stored target grid, {@code target.grid} in C, leaving the set flag and the
     * monster index alone. C has no function for this; {@code target_okay} assigns the field
     * directly, and {@link #targetOkay()} calls this method to do the same.
     *
     * <p>A {@code null} argument is stored as {@link Loc#zero}, so the stored grid is never
     * {@code null}. C's {@code struct loc} is passed by value and cannot be null.
     *
     * <p>Method setGrid coded before 261009, commented in full on 261009, {@code null} stored as
     * {@code Loc.zero} on 261012.
     *
     * @param grid the grid to store, or {@code null} for {@link Loc#zero}
     */
    public static void setGrid(Loc grid) {
        Target.grid = Objects.requireNonNullElse(grid, Loc.zero);
    }

    /**
     * Returns the stored target grid, the port of C's {@code target_get} ({@code target.c}), which
     * copies {@code target.grid} out through a pointer. Java returns the immutable {@link Loc}
     * itself. The result is {@link Loc#zero} after the target has been reset and before the first
     * target is ever set, because the static {@link #grid} starts at {@link Loc#zero} as C's
     * file-scope {@code target} starts zeroed. It is never {@code null}.
     *
     * <p>Method getGrid coded before 261009, commented in full on 261009, never-{@code null} answer
     * noted on 261012.
     *
     * @return the stored grid, which is {@link Loc#zero} before any target has been set
     */
    public static Loc getGrid() {
        return grid;
    }

    /**
     * Updates and verifies the target - the port of C's {@code target_okay} ({@code target.c}).
     * {@link Command#getTarget} calls this before honouring a queued {@code DIR_TARGET} argument,
     * so a target that has since died or moved out of sight forces a fresh aim rather than being
     * reused.
     *
     * <p>The answer is {@code false} when no target is set. When {@link #targetMonsterIndex} is
     * above {@code 0} the target is a monster: it is looked up in the current cave, and the answer
     * is {@code true} only if the slot holds a monster for which {@link #targetable(Monster)}
     * still holds, in which case the stored grid is refreshed from the monster's current position.
     * A monster target that fails, including one whose slot is now empty, answers {@code false}
     * and does not fall back to the grid, as in C. Only with an index of {@code 0} is the stored
     * grid considered, and then the answer is {@code true} when both its x and y are non-zero, so
     * a grid on row 0 or column 0 is not a usable target.
     *
     * <p>The stored grid is never {@code null} (see {@link #getGrid()}), so there is no guard for
     * one: before any target is set it is {@link Loc#zero}, which fails the non-zero test exactly
     * as C's zeroed grid does. The test is on each coordinate, as in C, and is not
     * {@link Loc#isZero()}: a grid on row 0 or column 0 fails it although it is not the origin.
     *
     * <p>Function targetOkay coded before 260903, commented in full on 261009, {@code null} guard
     * removed on 261012.
     *
     * @return {@code true} while the current target may be used
     */
    public static boolean targetOkay() {
        if (!isTargetSet()) return false;
        
        if (targetMonsterIndex > 0) {
            Monster monster = getTargetMonster(GameState.getCave());
            if (monster != null && targetable(monster)) {
                setGrid(monster.getGrid());
                
                // Good target
                return true;
            }
        } else if (getGrid().getY() != 0 && getGrid().getX() != 0) {
            // Allow a direction without a monster
            return true;
        }
        
        // Assume no target
        return false;
    }
}
