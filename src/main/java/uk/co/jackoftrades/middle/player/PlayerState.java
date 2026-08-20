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
import uk.co.jackoftrades.channel.utils.FlagView;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.registry.StatTables;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private Map<Stats, Integer> statAdd;
    /** Indexes into the internal stat tables - the port of C's {@code state.stat_ind}. */
    private Map<Stats, Integer> statInd;
    /** Current modified (in-use) stat values - the port of C's {@code state.stat_use}. */
    private Map<Stats, Integer> statUse;
    /** Maximal modified stat values - the port of C's {@code state.stat_top}. */
    private Map<Stats, Integer> statTop;

    /** The player's calculated skill values - the port of C's {@code state.skills}. */
    private Map<PlayerSkill, Integer> skills;

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
    private HashMap<ElementEnum, ElementInfo> elInfo;

    /**
     * Creates an empty state: the collections are made and then {@link #wipe()} sets every field to
     * its zero. The port of C's {@code struct player_state} being a zeroed value.
     */
    public PlayerState() {
        statAdd = new HashMap<>();
        statInd = new HashMap<>();
        statUse = new HashMap<>();
        statTop = new HashMap<>();
        skills = new HashMap<>();

        flags = new Flag<>(ObjectFlag.class);
        pflags = new Flag<>(PlayerFlag.class);
        elInfo = new HashMap<>();
        wipe();
    }

    /**
     * Resets every field to zero, ready to be filled from scratch — the port of C's
     * {@code memset(state, 0, sizeof *state)} at the head of {@code calc_bonuses}
     * ({@code player-calcs.c:1895}).
     *
     * <p>{@code calcBonuses} derives the whole state on every call rather than updating it, so this
     * is what guarantees no contribution outlives the gear that made it. The element map is not
     * merely cleared but repopulated with a zeroed entry for every real element, so that the rest of
     * the calculation can read and compare resistance levels without first testing whether the key
     * exists — C's fixed array gives that for free.
     */
    public void wipe() {
        statAdd.clear();
        statInd.clear();
        statUse.clear();
        statTop.clear();
        skills.clear();

        flags.wipe();
        pflags.wipe();
        elInfo.clear();

        for (ElementEnum el : ElementEnum.values()) {
            if (el == ElementEnum.ELEM_NONE || el == ElementEnum.ELEM_MAX) continue;
            ElementInfo ei = new ElementInfo();
            ei.setResLevel(0);
            elInfo.put(el, ei);
        }

        speed = 0;
        numBlows = 0;
        numShots = 0;
        numMoves = 0;
        ammoMult = 0;
        ammoTVal = null;
        ac = 0;
        damRed = 0;
        perDamRed = 0;
        toA = 0;
        toH = 0;
        toD = 0;
        seeInfra = 0;
        curLight = 0;
        heavyWield = false;
        heavyShoot = false;
        blessWield = false;
        cumberArmour = false;
    }

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
        return statInd.getOrDefault(stat, 0);
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
    public int perDamRed() {
        return perDamRed;
    }

    /**
     * @param speed the new speed, on the scale where 110 is normal — C's {@code state.speed}
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * @param numBlows blows per turn multiplied by 100 — C's {@code state.num_blows}
     */
    public void setNumBlows(int numBlows) {
        this.numBlows = numBlows;
    }

    /**
     * Replaces the infravision range outright. Used once, to seed the range from the race; every
     * later contribution goes through {@link #infraAdd(int)}.
     *
     * @param seeInfra the infravision range in units of ten feet — C's {@code state.see_infra}
     */
    public void setSeeInfra(int seeInfra) {
        this.seeInfra = seeInfra;
    }

    /**
     * Replaces one skill's value outright — for the many places {@code calcBonuses} recomputes a
     * skill from its own previous value. {@link #skillAdd(PlayerSkill, int)} is the additive form.
     *
     * @param skill the skill to set
     * @param value its new value
     */
    public void setStateSkill(PlayerSkill skill, int value) {
        skills.put(skill, value);
    }

    /**
     * Sets one element's resistance level, creating the entry if the element has none.
     *
     * <p>The tolerant of the two setters: {@link #setResLevel(ElementEnum, int)} requires the entry
     * to exist already. After {@link #wipe()} every real element has one, so the difference only
     * shows for {@code ELEM_NONE}, {@code ELEM_MAX} or a null key — none of which should reach
     * either method.
     *
     * @param element the element to set
     * @param level   the resistance level: {@code -1} vulnerable, {@code 0} neutral, higher values
     *                successive grades of resistance
     */
    public void setElInfo(ElementEnum element, int level) {
        ElementInfo info;
        if (elInfo.containsKey(element)) info = elInfo.get(element);
        else info = new ElementInfo();
        info.setResLevel(level);

        elInfo.put(element, info);
    }

    /**
     * Replaces the player flags wholesale, discarding what was there — C's {@code pf_copy}.
     *
     * <p>Paired with {@link #unionPlayerFlags}: the race's flags are copied to establish the set and
     * the class's are unioned on top ({@code player-calcs.c:1917-1919}), which is why one of the two
     * needs to be a replacement.
     *
     * @param newFlags the flags to copy in
     */
    public void copyPlayerFlag(FlagView<PlayerFlag> newFlags) {
        pflags.copyFrom(newFlags);
    }

    /**
     * Adds player flags to those already held — C's {@code pf_union}. Never removes one.
     *
     * @param newFlags the flags to add
     * @return {@code true} if the set changed
     */
    public boolean unionPlayerFlags(FlagView<PlayerFlag> newFlags) {
        return pflags.union(newFlags);
    }

    /**
     * Adds object flags to those already held — C's {@code of_union}. Never removes one, which is
     * what lets the gear's flags, the race's and the running statuses' be folded into one set that
     * consumers can ask a single question of.
     *
     * @param newFlags the flags to add
     * @return {@code true} if the set changed
     */
    public boolean unionObjectFlags(FlagView<ObjectFlag> newFlags) {
        return flags.union(newFlags);
    }

    /**
     * Accumulates a stat bonus from equipment or a shape — C's {@code state.stat_add[stat] += n}.
     *
     * <p>Points, not stat values: the total is applied through {@code modifyStatValue} at the end of
     * the calculation, because a point is worth one below 18 and ten above it.
     *
     * @param stat   the stat to adjust
     * @param amount the points to add, which may be negative
     */
    public void statAdd(Stats stat, int amount) {
        int oldValue = statAdd.getOrDefault(stat, 0);
        statAdd.put(stat, oldValue + amount);
    }

    /**
     * @param stat the stat to read
     * @return the accumulated bonus in points for that stat — C's {@code state.stat_add[stat]}
     */
    public int getStatAdd(Stats stat) {
        return statAdd.getOrDefault(stat, 0);
    }

    /**
     * Adds to one skill — C's {@code state.skills[skill] += n}.
     *
     * @param skill  the skill to adjust
     * @param amount the amount to add, which may be negative
     */
    public void skillAdd(PlayerSkill skill, int amount) {
        int oldValue = skills.getOrDefault(skill, 0);
        skills.put(skill, oldValue + amount);
    }

    /**
     * Adds to the infravision range — C's {@code state.see_infra += n}.
     *
     * @param amount the range to add, in units of ten feet
     */
    public void infraAdd(int amount) {
        this.seeInfra += amount;
    }

    /**
     * @return flat damage reduction — C's {@code state.dam_red}, subtracted from incoming damage
     * before any percentage reduction
     */
    public int getDamRed() {
        return damRed;
    }

    /**
     * @param damRed the new flat damage reduction — C's {@code state.dam_red}
     */
    public void setDamRed(int damRed) {
        this.damRed = damRed;
    }

    /**
     * The whole elemental-resistance map — C's {@code state.el_info}.
     *
     * <p><b>Read-only in name only.</b> The map itself is wrapped, but the {@link ElementInfo}
     * values inside it are the live ones, so a caller holding one can still change a resistance
     * through it. Use {@link #getResLevel} and {@link #setResLevel} for a single element; this is
     * for callers that need to iterate.
     *
     * @return an unmodifiable view of the resistance map
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return Collections.unmodifiableMap(elInfo);
    }

    /**
     * Adds to the armour-class bonus — C's {@code state.to_a}, the enchantment total, kept separate
     * from the base armour {@link #setBaseAc(int)} holds.
     *
     * @param amount the bonus to add, which may be negative
     */
    public void toAcAdd(int amount) {
        toA += amount;
    }

    /**
     * Adds to the to-hit bonus — C's {@code state.to_h}.
     *
     * @param amount the bonus to add, which may be negative
     */
    public void toHitAdd(int amount) {
        toH += amount;
    }

    /**
     * Adds to the to-damage bonus — C's {@code state.to_d}.
     *
     * @param amount the bonus to add, which may be negative
     */
    public void toDamAdd(int amount) {
        toD += amount;
    }

    /**
     * @param i the radius of light the player sheds — C's {@code state.cur_light}
     */
    public void setCurLight(int i) {
        curLight = i;
    }

    /**
     * @param stat the stat to set
     * @param top  the stat's maximum value with bonuses applied — C's {@code state.stat_top}, what
     *             the stat would be if nothing had drained it
     */
    public void setStatTop(Stats stat, int top) {
        statTop.put(stat, top);
    }

    /**
     * @param stat the stat to set
     * @param use  the stat's current value with bonuses applied — C's {@code state.stat_use}, the
     *             number the player actually has the use of
     */
    public void setStatUse(Stats stat, int use) {
        statUse.put(stat, use);
    }

    /**
     * @param stat the stat to set
     * @param ind  the stat's compressed table index — C's {@code state.stat_ind}, derived from
     *             {@link #setStatUse} and used to subscript every {@code adj_*} table
     */
    public void setStatInd(Stats stat, int ind) {
        statInd.put(stat, ind);
    }

    /**
     * @param skill the skill to read
     * @return its calculated value, or zero if nothing has set it — C's
     * {@code state.skills[skill]}
     */
    public int getPlayerSkill(PlayerSkill skill) {
        return skills.getOrDefault(skill, 0);
    }

    /**
     * The player-flag set itself — C's {@code state.pflags}.
     *
     * <p>Live and mutable, not a view. Callers testing a single flag should use
     * {@link #hasPFlag(PlayerFlag)} and callers setting one {@link #setPlayerFlag(PlayerFlag)};
     * this is for whole-set work.
     *
     * @return the player flags, shared with this state
     */
    public Flag<PlayerFlag> getPlayerFlag() {
        return pflags;
    }

    /**
     * Switches one player flag on — C's {@code pf_on}. There is no matching way to switch one off:
     * the state is rebuilt from nothing on every calculation, so flags are only ever added.
     *
     * @param playerFlag the flag to set
     */
    public void setPlayerFlag(PlayerFlag playerFlag) {
        pflags.on(playerFlag);
    }

    /**
     * The object-flag set itself — C's {@code state.flags}.
     *
     * <p>Live and mutable, and deliberately so: {@code calcBonuses} hands it straight to
     * {@code flagsTimed}, which adds the flags the running statuses duplicate to whatever the
     * equipment already contributed ({@code player-calcs.c:2135}).
     *
     * @return the object flags, shared with this state
     */
    @CheckReturnValue
    public Flag<ObjectFlag> getObjectFlag() {
        return flags;
    }

    /**
     * The weight the player can carry before slowing down — the port of C's {@code weight_limit}
     * ({@code player-calcs.c:1741-1750}).
     *
     * <p>Strength alone decides it: the strength table's value at the player's index, times 100. The
     * limit is not a hard ceiling — the penalty starts at half of it and grows by a point of speed
     * for every further tenth ({@code player-calcs.c:2222-2227}).
     *
     * @return the carrying limit in tenth-pounds
     */
    public int weightLimit() {
        return StatTables.adjStrWgt[getStatInd(Stats.STAT_STR)] * 100;
    }

    /**
     * @param tValue the kind of ammunition the wielded launcher fires — C's
     *               {@code state.ammo_tval}
     */
    public void setAmmoTValue(TValue tValue) {
        this.ammoTVal = tValue;
    }

    /**
     * @return shots per turn multiplied by 10 — C's {@code state.num_shots}
     */
    public int getNumShots() {
        return numShots;
    }

    /**
     * @param numShots shots per turn multiplied by 10 — C's {@code state.num_shots}
     */
    public void setNumShots(int numShots) {
        this.numShots = numShots;
    }

    /**
     * @param wield {@code true} if a priestly class is wielding a weapon its god approves of — C's
     *              {@code state.bless_wield}
     */
    public void setBlessWield(boolean wield) {
        this.blessWield = wield;
    }

    /**
     * @return {@code true} if the wielded weapon is too heavy — C's {@code state.heavy_wield}
     */
    public boolean isHeavyWield() {
        return heavyWield;
    }

    /**
     * @param wield {@code true} if the wielded weapon is too heavy for the player's strength — C's
     *              {@code state.heavy_wield}, which also costs the blow calculation entirely
     */
    public void setHeavyWield(boolean wield) {
        this.heavyWield = wield;
    }

    /**
     * @param cumber {@code true} if worn armour exceeds the class's allowance and is costing mana —
     *               C's {@code state.cumber_armor}
     */
    public void setCumberArmour(boolean cumber) {
        this.cumberArmour = cumber;
    }

    /**
     * @param extraMoves extra movement actions per turn — C's {@code state.num_moves}
     */
    public void setNumMoves(int extraMoves) {
        this.numMoves = extraMoves;
    }

    /**
     * @return {@code true} if the wielded launcher is too heavy — C's {@code state.heavy_shoot}
     */
    public boolean isHeavyShoot() {
        return heavyShoot;
    }

    /**
     * @param heavyshoot {@code true} if the wielded launcher is too heavy for the player's strength
     *                   — C's {@code state.heavy_shoot}, which also suppresses extra shots and might
     */
    public void setHeavyShoot(boolean heavyshoot) {
        this.heavyShoot = heavyshoot;
    }

    /**
     * @return the launcher's damage multiplier — C's {@code state.ammo_mult}
     */
    public int getAmmoMult() {
        return ammoMult;
    }

    /**
     * @param mult the launcher's damage multiplier — C's {@code state.ammo_mult}
     */
    public void setAmmoMult(int mult) {
        this.ammoMult = mult;
    }

    /**
     * @return the base armour class — C's {@code state.ac}, the armour the worn gear is worth
     * before enchantment, which {@link #toAcAdd(int)} accumulates separately
     */
    public int getBaseAc() {
        return ac;
    }

    /**
     * @param ac the new base armour class — C's {@code state.ac}
     */
    public void setBaseAc(int ac) {
        this.ac = ac;
    }

    /**
     * Sets one element's resistance level, requiring the element to have an entry already — which
     * after {@link #wipe()} every real element has.
     *
     * @param element  the element to set
     * @param resLevel the resistance level: {@code -1} vulnerable, {@code 0} neutral, higher values
     *                 successive grades of resistance
     * @throws NullPointerException if the state has no entry for that element
     */
    public void setResLevel(ElementEnum element, int resLevel) {
        ElementInfo elementInfo = elInfo.get(element);
        elementInfo.setResLevel(resLevel);
    }

    /**
     * @param element the element to read
     * @return its resistance level — C's {@code state.el_info[element].res_level}
     * @throws NullPointerException if the state has no entry for that element
     */
    public int getResLevel(ElementEnum element) {
        return elInfo.get(element).getResLevel();
    }
}