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
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.monsters.Monster;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

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
     */
    private static Loc grid;

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
    private static boolean targetable(Monster monster) {
        Player player = GameState.getPlayer();
        Flag<ProjectEnum> flag = new Flag<>(ProjectEnum.class, ProjectEnum.PROJECT_NONE);
        return monster != null && monster.getMonsterRace() != null && monster.isObvious()
                && ChunkUtils.isProjectable(GameState.getCave(), player.getGrid(), monster.getGrid(), flag)
                && player.getTimedEffect(TimedEffect.TMD_IMAGE) == 0;
    }
}
