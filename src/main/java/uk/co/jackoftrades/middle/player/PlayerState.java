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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.HashMap;

/**
 * The player's fully calculated combat and character state - the port of C's {@code struct player_state}
 * (player.h). Where {@link Player} holds the raw, saved character, this holds the quantities <em>derived</em>
 * from it each time the character sheet is recalculated: the modified stats, skills, speed, blow/shot/move
 * counts, armour class and combat bonuses, light and infravision ranges, the assorted "heavy weapon" style
 * booleans, and the folded-in status flags and elemental resistances contributed by race and equipment.
 *
 * <p>A {@link Player} carries two of these: {@code state}, the true calculated state, and {@code known_state},
 * the version restricted to what the player has actually learned. Because everything here is recomputed from
 * the character and its gear, it is never serialised - it is rebuilt on demand.
 *
 * <p>This is a work in progress: the field set mirrors C's {@code struct player_state}, but only the
 * accessors that current callers need are exposed so far.
 *
 * @author Rowan Crowther
 */
public class PlayerState {
    /**
     * Equipment stat bonuses added to each stat - the port of C's {@code state.stat_add}.
     */
    private HashMap<Stats, Integer> statAdd;
    /** Indexes into the internal stat tables - the port of C's {@code state.stat_ind}. */
    private HashMap<Stats, Integer> statInd;
    /** Current modified (in-use) stat values - the port of C's {@code state.stat_use}. */
    private HashMap<Stats, Integer> statUse;
    /** Maximal modified stat values - the port of C's {@code state.stat_top}. */
    private HashMap<Stats, Integer> statTop;

    /** The player's calculated skill values - the port of C's {@code state.skills}. */
    private HashMap<PlayerSkill, Integer> skills;

    /** Current speed - the port of C's {@code state.speed}. */
    private int speed;

    /** Number of blows per turn, scaled x100 - the port of C's {@code state.num_blows}. */
    private int numBlows;
    /** Number of shots per turn, scaled x10 - the port of C's {@code state.num_shots}. */
    private int numShots;
    /** Number of extra movement actions - the port of C's {@code state.num_moves}. */
    private int numMoves;

    /** Ammo damage multiplier from the launcher - the port of C's {@code state.ammo_mult}. */
    private int ammoMult;
    /** The variety of ammo the wielded launcher fires - the port of C's {@code state.ammo_tval}. */
    private TValue ammoTVal;

    /** Base armour class - the port of C's {@code state.ac}. */
    private int ac;
    /** Flat damage reduction - the port of C's {@code state.dam_red}. */
    private int damRed;
    /** Percentage damage reduction - the port of C's {@code state.perc_dam_red}. */
    private int perDamRed;
    /** Bonus to armour class - the port of C's {@code state.to_a}. */
    private int toA;
    /** Bonus to hit - the port of C's {@code state.to_h}. */
    private int toH;
    /** Bonus to damage - the port of C's {@code state.to_d}. */
    private int toD;

    /** Infravision range - the port of C's {@code state.see_infra}. */
    private int seeInfra;
    /** Radius of the light the player sheds, if any - the port of C's {@code state.cur_light}. */
    private int curLight;

    /** True when the wielded weapon is too heavy for the player - the port of C's {@code state.heavy_wield}. */
    private boolean heavyWield;
    /** True when the wielded launcher is too heavy for the player - the port of C's {@code state.heavy_shoot}. */
    private boolean heavyShoot;
    /** True when the wielded weapon is blessed (or blunt) - the port of C's {@code state.bless_wield}. */
    private boolean blessWield;
    /** True when worn armour is heavy enough to drain mana - the port of C's {@code state.cumber_armor}. */
    private boolean cumberArmour;

    /** Status flags folded in from race and items - the port of C's {@code state.flags}. */
    private Flag<ObjectFlag> flags;
    /** The player's intrinsic flags - the port of C's {@code state.pflags}. */
    private Flag<PlayerFlag> pflags;
    /** Elemental resistances folded in from race and items - the port of C's {@code state.el_info}. */
    private HashMap<ElementInfoEnum, ElementInfo> elInfo;

    /**
     * Test to see if a given flag is set on this player state
     *
     * @param flag the player flag to test for
     * @return true if the player flag is set
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasPFlag(@NotNull PlayerFlag flag) {
        return pflags.has(flag);
    }

    /**
     * @param flag the object flag to test
     * @return {@code true} if the player's calculated state carries the given object flag
     */
    public boolean hasOFlag(@NotNull ObjectFlag flag) {
        return flags.has(flag);
    }

    /**
     * Look up a stat's compressed table index — the {@code 0}-based rung into the
     * {@code adj_*} stat tables, not the raw stat value. The port of indexing C's
     * {@code state.stat_ind[stat]}.
     *
     * @param stat the stat to look up
     * @return the stat's index into the stat-adjustment tables
     */
    public int getStatInd(Stats stat) {
        return statInd.get(stat);
    }

    /**
     * Get the current light value
     *
     * @return the current light value
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getCurLight() {
        return curLight;
    }

    /**
     * @return the player's current calculated speed - the port of C's {@code state.speed}
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @return the player's percentage damage reduction (C: {@code state.perc_dam_red})
     */
    public int getPercDamageReduction() {
        return perDamRed;
    }
}