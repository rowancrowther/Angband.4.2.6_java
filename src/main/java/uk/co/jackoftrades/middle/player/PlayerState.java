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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.player;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.HashMap;

public class PlayerState {
    private HashMap<Stats, Integer> statAdd;
    private HashMap<Stats, Integer> statInd;
    private HashMap<Stats, Integer> statUse;
    private HashMap<Stats, Integer> statTop;

    private HashMap<PlayerSkill, Integer> skills;

    private int speed;

    private int numBlows;
    private int numShots;
    private int numMoves;

    private int ammoMult;
    private TValue ammoTVal;

    private int ac;
    private int damRed;
    private int perDamRed;
    private int toA;
    private int toH;
    private int toD;

    private int seeInfra;
    private int curLight;

    private boolean heavyWield;
    private boolean heavyShoot;
    private boolean blessWield;
    private boolean cumberArmour;

    private Flag<ObjectFlag> flags;
    private Flag<PlayerFlag> pflags;
    private HashMap<ElementInfoEnum, ElementInfo> elInfo;

    /**
     * Test to see if a given flag is set on this player state
     *
     * @param flag the player flag to test for
     * @return true if the player flag is set
     */
    @CheckReturnValue
    @Contract(pure = true)
    boolean hasPFlag(@NotNull PlayerFlag flag) {
        return pflags.has(flag);
    }

    /**
     * Get the current light value
     *
     * @return the current light value
     */
    @Contract(pure = true)
    @CheckReturnValue
    int getCurLight() {
        return curLight;
    }
}