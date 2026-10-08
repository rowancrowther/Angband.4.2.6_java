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

package uk.co.jackoftradesltd.middle.player;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.StatTables;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

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
 * the character and its gear, it is never serialized - it is rebuilt on demand.
 *
 * <p>This is a work in progress: the field set mirrors C's {@code struct player_state}, but only the
 * accessors that current callers need are exposed so far.
 *
 * <p>Class PlayerState commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public class PlayerState {
    /**
     * Equipment stat bonuses added to each stat - the port of C's {@code state.stat_add}.
     *
     * <p>Field statAdd commented in full on 261008.
     */
    private Map<Stats, Integer> statAdd;
    /**
     * Indexes into the internal stat tables - the port of C's {@code state.stat_ind}.
     *
     * <p>Field statInd commented in full on 261008.
     */
    private Map<Stats, Integer> statInd;
    /**
     * Current modified (in-use) stat values - the port of C's {@code state.stat_use}.
     *
     * <p>Field statUse commented in full on 261008.
     */
    private Map<Stats, Integer> statUse;
    /**
     * Maximal modified stat values - the port of C's {@code state.stat_top}.
     *
     * <p>Field statTop commented in full on 261008.
     */
    private Map<Stats, Integer> statTop;

    /**
     * The player's calculated skill values - the port of C's {@code state.skills}.
     *
     * <p>Left empty by {@link #wipe()} rather than filled with zeroes; {@link #getPlayerSkill} treats a
     * missing key as zero.
     *
     * <p>Field skills commented in full on 261008.
     */
    private Map<PlayerSkill, Integer> skills;

    /**
     * Current speed - the port of C's {@code state.speed}.
     *
     * <p>Field speed commented in full on 261008.
     */
    private int speed;

    /**
     * Number of blows per turn, scaled x100 - the port of C's {@code state.num_blows}.
     *
     * <p>Field numBlows commented in full on 261008.
     */
    private int numBlows;
    /**
     * Number of shots per turn, scaled x10 - the port of C's {@code state.num_shots}.
     *
     * <p>Field numShots commented in full on 261008.
     */
    private int numShots;
    /**
     * Number of extra movement actions - the port of C's {@code state.num_moves}.
     *
     * <p>Field numMoves commented in full on 261008.
     */
    private int numMoves;

    /**
     * Ammo damage multiplier from the launcher - the port of C's {@code state.ammo_mult}.
     *
     * <p>Field ammoMult commented in full on 261008.
     */
    private int ammoMult;
    /**
     * The variety of ammo the wielded launcher fires - the port of C's {@code state.ammo_tval}.
     *
     * <p>Never {@code null}: with no launcher wielded it holds {@link TValue#TV_NONE}, which is C's
     * tval {@code 0} and what {@link #wipe()} leaves behind, so C's {@code !state.ammo_tval} test
     * becomes {@code == TValue.TV_NONE}.
     *
     * <p>Field ammoTVal commented in full on 261008.
     */
    private TValue ammoTVal;

    /**
     * Base armour class - the port of C's {@code state.ac}.
     *
     * <p>Field ac commented in full on 261008.
     */
    private int ac;
    /**
     * Flat damage reduction - the port of C's {@code state.dam_red}.
     *
     * <p>Field damRed commented in full on 261008.
     */
    private int damRed;
    /**
     * Percentage damage reduction - the port of C's {@code state.perc_dam_red}.
     *
     * <p>Only ever zero in the port, as in C: nothing in C assigns {@code perc_dam_red}, so the field has no
     * setter and is read by the damage-reduction code alone.
     *
     * <p>Field perDamRed commented in full on 261008.
     */
    private int perDamRed;
    /**
     * Bonus to armour class - the port of C's {@code state.to_a}.
     *
     * <p>Field toA commented in full on 261008.
     */
    private int toA;
    /**
     * Bonus to hit - the port of C's {@code state.to_h}.
     *
     * <p>Field toH commented in full on 261008.
     */
    private int toH;
    /**
     * Bonus to damage - the port of C's {@code state.to_d}.
     *
     * <p>Field toD commented in full on 261008.
     */
    private int toD;

    /**
     * Infravision range - the port of C's {@code state.see_infra}.
     *
     * <p>Field seeInfra commented in full on 261008.
     */
    private int seeInfra;
    /**
     * Radius of the light the player sheds, if any - the port of C's {@code state.cur_light}.
     *
     * <p>Field curLight commented in full on 261008.
     */
    private int curLight;

    /**
     * True when the wielded weapon is too heavy for the player - the port of C's {@code state.heavy_wield}.
     *
     * <p>Field heavyWield commented in full on 261008.
     */
    private boolean heavyWield;
    /**
     * True when the wielded launcher is too heavy for the player - the port of C's {@code state.heavy_shoot}.
     *
     * <p>Field heavyShoot commented in full on 261008.
     */
    private boolean heavyShoot;
    /**
     * True when the wielded weapon is blessed (or blunt) - the port of C's {@code state.bless_wield}.
     *
     * <p>Field blessWield commented in full on 261008.
     */
    private boolean blessWield;
    /**
     * True when worn armour is heavy enough to drain mana - the port of C's {@code state.cumber_armor}.
     *
     * <p>Field cumberArmour commented in full on 261008. The spelling follows the port's Oxford
     * convention; the C field name keeps the US {@code armor}.
     */
    private boolean cumberArmour;

    /**
     * Status flags folded in from race and items - the port of C's {@code state.flags}.
     *
     * <p>Field flags commented in full on 261008.
     */
    private Flag<ObjectFlag> flags;
    /**
     * The player's intrinsic flags - the port of C's {@code state.pflags}.
     *
     * <p>Field pflags commented in full on 261008.
     */
    private Flag<PlayerFlag> pflags;
    /**
     * Elemental resistances folded in from race and items - the port of C's {@code state.el_info}.
     *
     * <p>Field elInfo commented in full on 261008.
     */
    private HashMap<ElementEnum, ElementInfo> elInfo;

    /**
     * Creates an empty state: the collections are made and then {@link #wipe()} sets every field to
     * its zero. The port of C's {@code struct player_state} being a zeroed value.
     *
     * <p>Function PlayerState commented in full on 261008.
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
     * ({@code player-calcs.c}).
     *
     * <p>{@code calcBonuses} derives the whole state on every call rather than updating it, so this
     * is what guarantees no contribution outlives the gear that made it.
     *
     * <p><b>Two of the maps are not merely cleared but refilled with zeroes</b> — the four stat maps
     * and the element map, each given an entry for every real value of its enum. C stores both as
     * fixed arrays, so after its {@code memset} every index is present and reads as zero; a Java
     * map that had only been cleared would instead have no key at all, and the difference shows up
     * as a {@link NullPointerException} on unboxing rather than as a wrong number. Refilling here
     * buys that back once, so that the rest of the calculation — and every reader of the finished
     * state — can index a stat or an element without first asking whether it is there.
     *
     * <p>The skills map is the deliberate exception. It is cleared and left empty, because
     * {@code calcBonuses} sets a value for every real skill from race and class within a few lines
     * of calling this, and {@link #getPlayerSkill} defaults a missing key to zero in any case.
     *
     * <p>Both loops skip their enum's {@code NONE} and {@code MAX} guard values. Those exist to
     * bound iteration and stand in for C's absent-value sentinel; they are not real stats or
     * elements and nothing ever reads one.
     *
     * <p><b>The ammunition type is reset to {@link TValue#TV_NONE}, not {@code null}.</b> C's
     * {@code ammo_tval} is an {@code int}, so after the {@code memset} it reads {@code 0}, which
     * is {@code TV_NONE}. Resetting to {@code TV_NONE} keeps "no launcher" to a single
     * representation, the one {@code calcBonuses} also writes for a launcher that has no kind, and
     * lets a Java comparison against an object's tval behave as C's does.
     *
     * <p>Function wipe commented in full on 261008.
     */
    public void wipe() {
        statAdd.clear();
        statInd.clear();
        statUse.clear();
        statTop.clear();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_MAX || stat == Stats.STAT_NONE) continue;
            statAdd.put(stat, 0);
            statInd.put(stat, 0);
            statUse.put(stat, 0);
            statTop.put(stat, 0);
        }

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
        ammoTVal = TValue.TV_NONE;
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
     * Test to see if a given flag is set on this player state - C's {@code pf_has(state.pflags, flag)},
     * and the body of the {@code player_has} macro.
     *
     * <p>Function hasPFlag commented in full on 261008.
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
     * Tests an object flag on the calculated state - C's {@code of_has(state.flags, flag)}.
     *
     * <p>Function hasOFlag commented in full on 261008.
     *
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
     * <p>A stat with no entry reads {@code 0}, as C's zeroed array does.
     *
     * <p>Function getStatInd commented in full on 261008.
     *
     * @param stat the stat to look up
     * @return the stat's index into the stat-adjustment tables
     */
    public int getStatInd(Stats stat) {
        return statInd.getOrDefault(stat, 0);
    }

    /**
     * Get the current light value - C's {@code state.cur_light}, the radius of the light the player sheds.
     *
     * <p>Function getCurLight commented in full on 261008.
     *
     * @return the current light value
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getCurLight() {
        return curLight;
    }

    /**
     * Sets the radius of light the player sheds — C's {@code state.cur_light = n}.
     *
     * <p>Function setCurLight commented in full on 261008.
     *
     * @param i the radius of light the player sheds
     */
    public void setCurLight(int i) {
        curLight = i;
    }

    /**
     * Reads the calculated speed - the port of C's {@code state.speed}.
     *
     * <p>Function getSpeed commented in full on 261008.
     *
     * @return the player's current calculated speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Sets the calculated speed - C's {@code state.speed = n}.
     *
     * <p>Function setSpeed commented in full on 261008.
     *
     * @param speed the new speed, on the scale where 110 is normal
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Reads the percentage damage reduction - C's {@code state.perc_dam_red}. The accessor is named for the
     * field rather than {@code get...}.
     *
     * <p>Function perDamRed commented in full on 261008.
     *
     * @return the player's percentage damage reduction, always {@code 0} while nothing sets it
     */
    public int perDamRed() {
        return perDamRed;
    }

    /**
     * Replaces the infravision range outright. Used once, to seed the range from the race; every
     * later contribution goes through {@link #infraAdd(int)}.
     *
     * <p>Function setSeeInfra commented in full on 261008.
     *
     * @param seeInfra the infravision range in units of ten feet — C's {@code state.see_infra}
     */
    public void setSeeInfra(int seeInfra) {
        this.seeInfra = seeInfra;
    }

    /**
     * Replaces one skill's value outright — for the many places {@code calcBonuses} recomputes a
     * skill from its own previous value. {@link #skillAdd(PlayerSkill, int)} is the additive form.
     * C's {@code state.skills[skill] = n}.
     *
     * <p>Function setStateSkill commented in full on 261008.
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
     * either method. C's {@code state.el_info[element].res_level = n}.
     *
     * <p>Function setElInfo commented in full on 261008.
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
     * the class's are unioned on top (both in {@code calc_bonuses}, {@code player-calcs.c}), which is
     * why one of the two needs to be a replacement.
     *
     * <p>Function copyPlayerFlag commented in full on 261008.
     *
     * @param newFlags the flags to copy in
     */
    public void copyPlayerFlag(FlagView<PlayerFlag> newFlags) {
        pflags.copyFrom(newFlags);
    }

    /**
     * Adds player flags to those already held — C's {@code pf_union}. Never removes one.
     *
     * <p>Function unionPlayerFlags commented in full on 261008.
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
     * <p>Function unionObjectFlags commented in full on 261008.
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
     * <p>Function statAdd commented in full on 261008.
     *
     * @param stat   the stat to adjust
     * @param amount the points to add, which may be negative
     */
    public void statAdd(Stats stat, int amount) {
        int oldValue = statAdd.getOrDefault(stat, 0);
        statAdd.put(stat, oldValue + amount);
    }

    /**
     * Reads the accumulated stat bonus - C's {@code state.stat_add[stat]}. A stat with no entry reads
     * {@code 0}.
     *
     * <p>Function getStatAdd commented in full on 261008.
     *
     * @param stat the stat to read
     * @return the accumulated bonus in points for that stat
     */
    public int getStatAdd(Stats stat) {
        return statAdd.getOrDefault(stat, 0);
    }

    /**
     * Adds to one skill — C's {@code state.skills[skill] += n}. A skill with no value yet starts from
     * {@code 0}, as in C's zeroed array.
     *
     * <p>Function skillAdd commented in full on 261008.
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
     * <p>Function infraAdd commented in full on 261008.
     *
     * @param amount the range to add, in units of ten feet
     */
    public void infraAdd(int amount) {
        this.seeInfra += amount;
    }

    /**
     * Reads the flat damage reduction — C's {@code state.dam_red}.
     *
     * <p>Function getDamRed commented in full on 261008.
     *
     * @return flat damage reduction, subtracted from incoming damage before any percentage reduction
     */
    public int getDamRed() {
        return damRed;
    }

    /**
     * Sets the flat damage reduction — C's {@code state.dam_red = n}.
     *
     * <p>Function setDamRed commented in full on 261008.
     *
     * @param damRed the new flat damage reduction
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
     * <p>Function getElInfo commented in full on 261008.
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
     * <p>Function toAcAdd commented in full on 261008.
     *
     * @param amount the bonus to add, which may be negative
     */
    public void toAcAdd(int amount) {
        toA += amount;
    }

    /**
     * Adds to the to-hit bonus — C's {@code state.to_h}.
     *
     * <p>Function toHitAdd commented in full on 261008.
     *
     * @param amount the bonus to add, which may be negative
     */
    public void toHitAdd(int amount) {
        toH += amount;
    }

    /**
     * Adds to the to-damage bonus — C's {@code state.to_d}.
     *
     * <p>Function toDamAdd commented in full on 261008.
     *
     * @param amount the bonus to add, which may be negative
     */
    public void toDamAdd(int amount) {
        toD += amount;
    }

    /**
     * Sets a stat's maximal modified value — C's {@code state.stat_top[stat] = n}.
     *
     * <p>Function setStatTop commented in full on 261008.
     *
     * @param stat the stat to set
     * @param top  the stat's maximum value with bonuses applied — C's {@code state.stat_top}, what
     *             the stat would be if nothing had drained it
     */
    public void setStatTop(Stats stat, int top) {
        statTop.put(stat, top);
    }

    /**
     * Sets a stat's current modified value — C's {@code state.stat_use[stat] = n}.
     *
     * <p>Function setStatUse commented in full on 261008.
     *
     * @param stat the stat to set
     * @param use  the stat's current value with bonuses applied — C's {@code state.stat_use}, the
     *             number the player actually has the use of
     */
    public void setStatUse(Stats stat, int use) {
        statUse.put(stat, use);
    }

    /**
     * Sets a stat's compressed table index — C's {@code state.stat_ind[stat] = n}.
     *
     * <p>Function setStatInd commented in full on 261008.
     *
     * @param stat the stat to set
     * @param ind  the stat's compressed table index — C's {@code state.stat_ind}, derived from
     *             {@link #setStatUse} and used to subscript every {@code adj_*} table
     */
    public void setStatInd(Stats stat, int ind) {
        statInd.put(stat, ind);
    }

    /**
     * Reads one skill's calculated value — C's {@code state.skills[skill]}. {@link #wipe()} leaves the
     * skills map empty, so a skill nothing has set reads zero, which is what C's zeroed array gives.
     *
     * <p>Function getPlayerSkill commented in full on 261008.
     *
     * @param skill the skill to read
     * @return its calculated value, or zero if nothing has set it
     */
    public int getPlayerSkill(PlayerSkill skill) {
        return skills.getOrDefault(skill, 0);
    }

    /**
     * The player-flag set itself — C's {@code state.pflags}.
     *
     * <p>Live and mutable, not a view. Callers testing a single flag should use
     * {@link #hasPFlag(PlayerFlag)} and callers setting one {@link #playerFlagOn(PlayerFlag)};
     * this is for whole-set work.
     *
     * <p>Function getPlayerFlag commented in full on 261008.
     *
     * @return the player flags, shared with this state
     */
    public Flag<PlayerFlag> getPlayerFlag() {
        return pflags;
    }

    /**
     * Switches one player flag on in this state — the port of C's {@code pf_on}
     * ({@code player.h}), which is {@code flag_on_dbg} over {@code state.pflags}
     * ({@code z-bitflag.c}).
     *
     * <p>The return value is C's, and it reports change rather than success: {@code false} when
     * the flag was already on and nothing was written, {@code true} when this call is what turned
     * it on. Most callers have no use for it — {@code calcBonuses} raising {@code PF_NO_MANA} and
     * the monster-knowledge sweep copying flags across both set unconditionally and drop the
     * answer — but a caller that wants to act only on a genuine transition can test it without
     * reading the flag first.
     *
     * <p>C reaches {@code flag_on_dbg} rather than {@code flag_on} so that a flag index past the
     * end of the bit array aborts with a diagnostic instead of corrupting the neighbouring bytes.
     * A Java enum cannot be out of range, so the two C variants collapse into one method here.
     *
     * <p>Function playerFlagOn coded on 260831, commented in full on 260831.
     *
     * @param playerFlag the flag to switch on
     * @return {@code true} if the flag was off and is now on, {@code false} if it was already on
     */
    public boolean playerFlagOn(PlayerFlag playerFlag) {
        return pflags.on(playerFlag);
    }

    /**
     * Switches one player flag off in this state — the port of C's {@code pf_off}
     * ({@code player.h}), which is {@code flag_off} over {@code state.pflags}
     * ({@code z-bitflag.c}).
     *
     * <p>The mirror of {@link #playerFlagOn(PlayerFlag)}, and its return value reads the same way
     * round: {@code true} when the flag was on and this call cleared it, {@code false} when it was
     * already off and nothing changed. C clears the bit with {@code &= ~flag_binary}, so a flag
     * that is off stays off — switching one off twice is not an error, it is simply a second call
     * that answers {@code false}.
     *
     * <p>{@code pf_off} is the one member of the pair C does not route through a debug wrapper; it
     * asserts on an out-of-range index instead. That distinction has no port, as an enum constant
     * is always in range.
     *
     * <p>Function playerFlagOff coded on 260831, commented in full on 260831.
     *
     * @param playerFlag the flag to switch off
     * @return {@code true} if the flag was on and is now off, {@code false} if it was already off
     */
    public boolean playerFlagOff(PlayerFlag playerFlag) {
        return pflags.off(playerFlag);
    }

    /**
     * The object-flag set itself — C's {@code state.flags}.
     *
     * <p>Live and mutable, and deliberately so: {@code calcBonuses} hands it straight to
     * {@code flagsTimed}, which adds the flags the running statuses duplicate to whatever the
     * equipment already contributed ({@code calc_bonuses}, {@code player-calcs.c}).
     *
     * <p>Function getObjectFlag commented in full on 261008.
     *
     * @return the object flags, shared with this state
     */
    @CheckReturnValue
    public Flag<ObjectFlag> getObjectFlag() {
        return flags;
    }

    /**
     * The weight the player can carry before slowing down — the port of C's {@code weight_limit}
     * ({@code player-calcs.c}).
     *
     * <p>Strength alone decides it: the strength table's value at the player's index, times 100. The
     * limit is not a hard ceiling — the penalty starts at half of it and grows by a point of speed
     * for every further tenth (applied in {@code calc_bonuses}, {@code player-calcs.c}).
     *
     * <p>C takes the {@code player_state} as a parameter; here it is the receiver, so
     * {@code weight_limit(&p->state)} becomes {@code p.getPlayerState().weightLimit()}. A state
     * with no strength index set reads index {@code 0}, as C's zeroed array does, and gives
     * {@code 500}.
     *
     * <p>Function weightLimit commented in full on 261008.
     *
     * @return the carrying limit in tenth-pounds
     */
    public int weightLimit() {
        return StatTables.adjStrWgt[getStatInd(Stats.STAT_STR)] * 100;
    }

    /**
     * Sets the variety of ammunition the wielded launcher fires — C's {@code state.ammo_tval}.
     *
     * <p>Pass {@link TValue#TV_NONE} for "nothing to fire", never {@code null}: that is the value
     * C holds after its {@code memset} and the one {@link #wipe()} restores, so a state that has
     * been told "no ammunition" and a state that has never been told anything compare alike.
     *
     * <p>Function setAmmoTValue commented in full on 261008.
     *
     * @param tValue the kind of ammunition the wielded launcher fires, or
     *               {@link TValue#TV_NONE} for none
     */
    public void setAmmoTValue(TValue tValue) {
        this.ammoTVal = tValue;
    }

    /**
     * Reads the shots per turn — C's {@code state.num_shots}.
     *
     * <p>Function getNumShots commented in full on 261008.
     *
     * @return shots per turn multiplied by 10
     */
    public int getNumShots() {
        return numShots;
    }

    /**
     * Sets the shots per turn — C's {@code state.num_shots = n}.
     *
     * <p>Function setNumShots commented in full on 261008.
     *
     * @param numShots shots per turn multiplied by 10
     */
    public void setNumShots(int numShots) {
        this.numShots = numShots;
    }

    /**
     * Reads the heavy-weapon flag — C's {@code state.heavy_wield}.
     *
     * <p>Function isHeavyWield commented in full on 261008.
     *
     * @return {@code true} if the wielded weapon is too heavy
     */
    public boolean isHeavyWield() {
        return heavyWield;
    }

    /**
     * Sets the heavy-weapon flag — C's {@code state.heavy_wield}.
     *
     * <p>Function setHeavyWield commented in full on 261008.
     *
     * @param wield {@code true} if the wielded weapon is too heavy for the player's strength, which also
     *              costs the blow calculation entirely
     */
    public void setHeavyWield(boolean wield) {
        this.heavyWield = wield;
    }

    /**
     * Sets the extra movement actions — C's {@code state.num_moves = n}.
     *
     * <p>Function setNumMoves commented in full on 261008.
     *
     * @param extraMoves extra movement actions per turn
     */
    public void setNumMoves(int extraMoves) {
        this.numMoves = extraMoves;
    }

    /**
     * Reads the heavy-launcher flag — C's {@code state.heavy_shoot}.
     *
     * <p>Function isHeavyShoot commented in full on 261008.
     *
     * @return {@code true} if the wielded launcher is too heavy
     */
    public boolean isHeavyShoot() {
        return heavyShoot;
    }

    /**
     * Sets the heavy-launcher flag — C's {@code state.heavy_shoot}.
     *
     * <p>Function setHeavyShoot commented in full on 261008.
     *
     * @param heavyshoot {@code true} if the wielded launcher is too heavy for the player's strength,
     *                   which also suppresses extra shots and might
     */
    public void setHeavyShoot(boolean heavyshoot) {
        this.heavyShoot = heavyshoot;
    }

    /**
     * Reads the launcher's damage multiplier — C's {@code state.ammo_mult}.
     *
     * <p>Function getAmmoMult commented in full on 261008.
     *
     * @return the launcher's damage multiplier
     */
    public int getAmmoMult() {
        return ammoMult;
    }

    /**
     * Sets the launcher's damage multiplier — C's {@code state.ammo_mult = n}.
     *
     * <p>Function setAmmoMult commented in full on 261008.
     *
     * @param mult the launcher's damage multiplier
     */
    public void setAmmoMult(int mult) {
        this.ammoMult = mult;
    }

    /**
     * Reads the base armour class — C's {@code state.ac}.
     *
     * <p>Function getBaseAc commented in full on 261008.
     *
     * @return the armour the worn gear is worth before enchantment, which {@link #toAcAdd(int)}
     * accumulates separately
     */
    public int getBaseAc() {
        return ac;
    }

    /**
     * Sets the base armour class — C's {@code state.ac = n}.
     *
     * <p>Function setBaseAc commented in full on 261008.
     *
     * @param ac the new base armour class
     */
    public void setBaseAc(int ac) {
        this.ac = ac;
    }

    /**
     * Sets one element's resistance level, requiring the element to have an entry already — which
     * after {@link #wipe()} every real element has. C's {@code state.el_info[element].res_level = n}.
     *
     * <p>Function setResLevel commented in full on 261008.
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
     * Reads one element's resistance level — C's {@code state.el_info[element].res_level}.
     *
     * <p>Function getResLevel commented in full on 261008.
     *
     * @param element the element to read
     * @return its resistance level
     * @throws NullPointerException if the state has no entry for that element
     */
    public int getResLevel(ElementEnum element) {
        return elInfo.get(element).getResLevel();
    }

    /**
     * Reads the armour-class bonus - C's {@code state.to_a}, separate from {@link #getBaseAc()}.
     *
     * <p>Function getToAc commented in full on 261008.
     *
     * @return the bonus to armour class from equipment and effects
     */
    public int getToAc() {
        return this.toA;
    }

    /**
     * Reads the blessed-weapon flag - C's {@code state.bless_wield}, which decides whether a priest
     * suffers for their weapon.
     *
     * <p>Function isBlessWield commented in full on 261008.
     *
     * @return {@code true} when the wielded weapon is blessed, or blunt enough not to offend
     */
    public boolean isBlessWield() {
        return blessWield;
    }

    /**
     * Sets whether the wielded weapon is blessed (or blunt) — C's {@code state.bless_wield}.
     *
     * <p>Function setBlessWield commented in full on 261008.
     *
     * @param wield {@code true} if a priestly class is wielding a weapon its god approves of
     */
    public void setBlessWield(boolean wield) {
        this.blessWield = wield;
    }

    /**
     * Returns one stat's maximal modified value - the port of reading C's
     * {@code state.stat_top[stat]}, the stat as it would be with nothing draining it.
     *
     * <p>Unboxes the map's value directly, so it throws for a stat the state has no entry for - the
     * same shape as {@link #getResLevel(ElementEnum)} above.
     *
     * <p>Function getStatTop commented in full on 260827.
     *
     * @param stat the stat to read
     * @return that stat's maximal modified value
     * @throws NullPointerException if the state has no entry for that stat
     */
    public int getStatTop(Stats stat) {
        return statTop.get(stat);
    }

    /**
     * Returns one stat's current modified value - the port of reading C's
     * {@code state.stat_use[stat]}, the stat as it stands after any drain.
     *
     * <p>Unboxes the map's value directly, so it throws for a stat the state has no entry for.
     *
     * <p>Function getStatUse commented in full on 260827.
     *
     * @param stat the stat to read
     * @return that stat's current modified value
     * @throws NullPointerException if the state has no entry for that stat
     */
    public int getStatUse(Stats stat) {
        return statUse.get(stat);
    }

    /**
     * Reads the mana-draining-armour flag - C's {@code state.cumber_armor}.
     *
     * <p>Function isCumberArmour commented in full on 261008.
     *
     * @return {@code true} when the armour worn is heavy enough to drain mana
     */
    public boolean isCumberArmour() {
        return cumberArmour;
    }

    /**
     * Sets the mana-draining-armour flag — C's {@code state.cumber_armor}.
     *
     * <p>Function setCumberArmour commented in full on 261008.
     *
     * @param cumber {@code true} if worn armour exceeds the class's allowance and is costing mana
     */
    public void setCumberArmour(boolean cumber) {
        this.cumberArmour = cumber;
    }

    /**
     * Returns an independent duplicate of this state — the port of C assigning one
     * {@code struct player_state} to another, as {@code update_bonuses} does
     * ({@code player-calcs.c}).
     *
     * <p>C gets this for nothing: {@code struct player_state state = p->state;} copies every byte,
     * so the local and the field are thereafter separate values. Java would bind a second name to
     * the same object, which is not a copy at all, and the caller that most needs one is exactly the
     * one that would suffer for it — {@link PlayerCalcs#updateBonuses(Player)} recalculates into the duplicate
     * and then compares it field by field against the original, so an alias would reduce every
     * comparison to an object against itself. This method exists to make that assignment mean in
     * Java what it means in C.
     *
     * <p><b>Deep where it has to be, shallow where it is safe.</b> The maps and flag sets are
     * rebuilt rather than shared, and each {@code ElementInfo} is copied in turn, since one of those
     * is a mutable object and handing over the same instance would let a later calculation reach
     * back through the duplicate and alter the original's resistances. The primitives are copied by
     * value, and {@code ammoTVal} is copied by reference only because it is an enum constant and so
     * has nothing to alter. It is never {@code null}, so the copy needs no guard for that.
     *
     * <p>The stat maps are read unguarded, which is safe because {@link #wipe()} guarantees an entry
     * for every real stat and the constructor calls it. The skills map is guarded on the key being
     * present, because {@code wipe} leaves that one empty by design and a state that has not yet
     * been through {@code calcBonuses} genuinely has no skills in it.
     *
     * <p>Function copy commented in full on 261008.
     *
     * @return a new state holding the same values, sharing no mutable structure with this one
     */
    public PlayerState copy() {
        PlayerState result = new PlayerState();

        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;

            result.statAdd.put(stat, this.statAdd.get(stat));
            result.statInd.put(stat, this.statInd.get(stat));
            result.statUse.put(stat, this.statUse.get(stat));
            result.statTop.put(stat, this.statTop.get(stat));
        }

        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;

            if (skills.containsKey(skill))
                result.setStateSkill(skill, this.skills.get(skill));
        }

        result.flags.copyFrom(flags);
        result.pflags.copyFrom(pflags);

        for (ElementEnum element : ElementEnum.values()) {
            if (this.elInfo.containsKey(element))
                result.elInfo.put(element, this.elInfo.get(element).copy());
        }

        result.speed = this.speed;
        result.numBlows = this.numBlows;
        result.numShots = this.numShots;
        result.numMoves = this.numMoves;
        result.ammoMult = this.ammoMult;
        result.ammoTVal = this.ammoTVal;
        result.ac = this.ac;
        result.damRed = this.damRed;
        result.perDamRed = this.perDamRed;
        result.toA = this.toA;
        result.toH = this.toH;
        result.toD = this.toD;
        result.seeInfra = this.seeInfra;
        result.curLight = this.curLight;
        result.heavyWield = this.heavyWield;
        result.heavyShoot = this.heavyShoot;
        result.blessWield = this.blessWield;
        result.cumberArmour = this.cumberArmour;

        return result;
    }

    /**
     * Reads the variety of ammunition the wielded launcher fires — C's {@code state.ammo_tval}.
     *
     * <p>Never {@code null}. With no launcher wielded, or one whose kind fires no recognised ammo,
     * the answer is {@link TValue#TV_NONE}, C's tval {@code 0}. C tests this with
     * {@code !state.ammo_tval} in {@code player-attack.c} and {@code player-util.c}; the port
     * writes it {@code == TValue.TV_NONE}. A direct comparison against an object's tval, as
     * {@code earlier_object} makes, needs no special case: both sides are enum constants and the
     * "none" value is the same one on each.
     *
     * <p>Function getAmmoTval commented in full on 261008.
     *
     * @return the ammunition variety, or {@link TValue#TV_NONE} when there is none to fire
     */
    public TValue getAmmoTval() {
        return ammoTVal;
    }

    /**
     * Sets a single object flag on the player's calculated state — C's {@code of_on(state->flags, f)}.
     *
     * <p>Idempotent, and the return value is the report of whether it did anything: {@code false} when
     * the flag was already held and nothing changed, {@code true} when the set gained it. C's
     * {@code flag_on} answers the same way, which is what lets the calculation fold the same flag in
     * from race, class, shape and every piece of gear without the order of the folds mattering.
     *
     * <p>Function oFlagOn commented in full on 260831.
     *
     * @param objFlag the object flag to set
     * @return {@code true} if the flag was not already set
     */
    public boolean oFlagOn(ObjectFlag objFlag) {
        return flags.on(objFlag);
    }

    /**
     * Reads the blows per turn, multiplied by 100 — C's {@code state.num_blows}.
     *
     * <p>Function getNumBlows commented in full on 260906.
     *
     * @return the player's blows per turn, multiplied by 100
     */
    public int getNumBlows() {
        return numBlows;
    }

    /**
     * Clears a single object flag from the player's calculated state — C's {@code of_off(state->flags, f)}.
     *
     * <p>The mirror of {@link #oFlagOn(ObjectFlag)}, and idempotent in the same way: the return value
     * reports whether the set actually changed, {@code true} when the flag was held and has now been
     * removed, {@code false} when it was already absent and nothing happened. C's {@code flag_off}
     * answers identically, testing the bit before clearing it rather than clearing unconditionally.
     *
     * <p>Note this is not part of the state calculation itself, which builds a state from empty and so
     * only ever needs to switch flags on. Clearing is for the monster's picture of the player: when
     * {@code update_smart_learn} finds the player lacks a flag it writes that absence into
     * {@code known_pstate}, correcting a belief the monster may already hold.
     *
     * <p>Function oFlagOff commented in full on 260831.
     *
     * @param objFlag the object flag to clear
     * @return {@code true} if the flag was set before this call
     */
    public boolean oFlagOff(ObjectFlag objFlag) {
        return flags.off(objFlag);
    }

    /**
     * Sets the blows per turn - C's {@code state.num_blows = n}.
     *
     * <p>Function setNumBlows commented in full on 261008.
     *
     * @param numBlows blows per turn multiplied by 100
     */
    public void setNumBlows(int numBlows) {
        this.numBlows = numBlows;
    }
}