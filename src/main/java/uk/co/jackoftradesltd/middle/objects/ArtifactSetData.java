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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ArtifactIndex;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.HashMap;
import java.util.Map;

/**
 * The statistics the random-artifact generator learns from one set of artifacts - the port of C's
 * {@code struct artifact_set_data} ({@code obj-randart.h}), built by {@code artifact_set_data_new()}
 * in {@code obj-randart.c}. {@code do_randart} builds one of these for the standard artifacts, learns
 * their power ratings, base-item mix and property frequencies, designs a randart set from those
 * figures, and then builds a second one to measure the set it made.
 *
 * <p>The fields fall into the same groups as C's struct: the mean start and increment values for
 * to-hit, to-dam and AC; the learned property probabilities and per-tval counts; the cumulative
 * tval frequency table used to pick a base item; the power ratings; and the base-item level, base-item
 * rarity and artifact rarity recorded per artifact.
 *
 * <p>Each of C's eleven {@code int *} arrays becomes a {@link Map}, keyed by what C indexed it with:
 * {@link ArtifactIndex} for {@code art_probs}, {@link TValue} for the six {@code TV_MAX}-sized arrays,
 * and {@link Artifact} for the four {@code z_info->a_max}-sized ones (C indexes those by the
 * artifact's position in {@code a_info}). C {@code mem_zalloc}s every array, so any valid index reads
 * {@code 0} before anything is learned; the constructor earns the same by putting a {@code 0} entry
 * under every key. Two consequences: {@code artProbs} also carries an entry for the
 * {@link ArtifactIndex#ART_IDX_TOTAL} sentinel, which is one past the end of C's array and is never
 * read; and a lookup against a key that was not present at construction (an artifact loaded later)
 * throws {@link NullPointerException} on unboxing, where C would read past the end of its array.
 *
 * <p>C's {@code artifact_set_data_free()} has no counterpart; the garbage collector reclaims the maps.
 *
 * <p>Class ArtifactSetData coded on 260908, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
public class ArtifactSetData {
    // Mean start and increment values for toHit, toDam and toAC
    /**
     * The size of one step of to-hit bonus above {@link #hitStartVal}. Port of {@code hit_increment};
     * {@code artifact_set_data_new()} sets it to {@code 4}. The frequency counters in
     * {@code obj-randart.c} divide an artifact's surplus to-hit by it to decide how many to-hit
     * "picks" that artifact represents, and the designer adds to-hit in steps scaled by it.
     *
     * <p>Field hitIncrement coded on 260908, commented in full on 261002.
     */
    private int hitIncrement;

    /**
     * The size of one step of to-dam bonus above {@link #damStartVal}. Port of {@code dam_increment};
     * {@code artifact_set_data_new()} sets it to {@code 4}. Used the same way as
     * {@link #hitIncrement}, for to-dam.
     *
     * <p>Field damIncrement coded on 260908, commented in full on 261002.
     */
    private int damIncrement;

    /**
     * The to-hit a weapon starts with before any learned bonus. Port of {@code hit_startval};
     * {@code artifact_set_data_new()} sets it to {@code 10}. The counters subtract it before dividing
     * by {@link #hitIncrement}, and a new weapon randart is given half of it plus a random amount up
     * to it.
     *
     * <p>Field hitStartVal coded on 260908, commented in full on 261002.
     */
    private int hitStartVal;

    /**
     * The to-dam a weapon starts with before any learned bonus. Port of {@code dam_startval};
     * {@code artifact_set_data_new()} sets it to {@code 10}. Used the same way as
     * {@link #hitStartVal}, for to-dam.
     *
     * <p>Field damStartVal coded on 260908, commented in full on 261002.
     */
    private int damStartVal;

    /**
     * The armour class bonus an armour piece starts with before any learned bonus. Port of
     * {@code ac_startval}; {@code artifact_set_data_new()} sets it to {@code 15}. Used the same way as
     * {@link #hitStartVal}, for AC, with {@link #acIncrement} as the step.
     *
     * <p>Field acStartVal coded on 260908, commented in full on 261002.
     */
    private int acStartVal;

    /**
     * The size of one step of AC bonus above {@link #acStartVal}. Port of {@code ac_increment};
     * {@code artifact_set_data_new()} sets it to {@code 5}.
     *
     * <p>Field acIncrement coded on 260908, commented in full on 261002.
     */
    private int acIncrement;

    // Data structures for learned probabilities
    /**
     * How often each randart property appears across the artifact set, one counter per
     * {@link ArtifactIndex}. Port of {@code art_probs}, an array of {@code ART_IDX_TOTAL} ints. The
     * {@code count_*} functions in {@code obj-randart.c} add to it, and {@code rescale_freqs()} and
     * {@code adjust_freqs()} turn the counts into the probabilities the designer draws from. Starts
     * with a {@code 0} entry for every constant, the {@code ART_IDX_TOTAL} sentinel included.
     *
     * <p>Field artProbs coded on 260908, commented in full on 261002.
     */
    private Map<ArtifactIndex, Integer> artProbs;

    /**
     * How many artifacts in the set are built on a base item of each tval. Port of {@code tv_probs},
     * an array of {@code TV_MAX} ints. {@code collect_artifact_data()} counts into it, and
     * {@code parse_frequencies()} weights it down and accumulates it into {@link #tvFreq}. Starts with
     * a {@code 0} entry for every {@link TValue}.
     *
     * <p>Field tvProbs coded on 260908, commented in full on 261002.
     */
    private Map<TValue, Integer> tvProbs;

    /**
     * How many artifacts of each tval {@code store_base_power()} has rated. Port of {@code tv_num},
     * an array of {@code TV_MAX} ints. It is the divisor for {@link #avgTvPower}, and
     * {@code create_artifact_set()} sizes the new set's per-tval quota from it. Starts with a
     * {@code 0} entry for every {@link TValue}.
     *
     * <p>Field tvNum coded on 260908, commented in full on 261002.
     */
    private Map<TValue, Integer> tvNum;

    /**
     * How many artifacts in the set are launchers ({@code TV_BOW}). Port of {@code bow_total}; counted
     * by {@code store_base_power()} and used by {@code rescale_freqs()} to scale the bow-only property
     * counts up to the whole set.
     *
     * <p>Field bowTotal coded on 260908, commented in full on 261002.
     */
    private int bowTotal;

    /**
     * How many artifacts in the set are melee weapons (sword, polearm or hafted). Port of
     * {@code melee_total}; counted by {@code store_base_power()} and used by {@code rescale_freqs()}.
     *
     * <p>Field meleeTotal coded on 260908, commented in full on 261002.
     */
    private int meleeTotal;

    /**
     * How many artifacts in the set are boots. Port of {@code boot_total}; counted by
     * {@code store_base_power()}.
     *
     * <p>Field bootTotal coded on 260908, commented in full on 261002.
     */
    private int bootTotal;

    /**
     * How many artifacts in the set are gloves. Port of {@code glove_total}; counted by
     * {@code store_base_power()}.
     *
     * <p>Field gloveTotal coded on 260908, commented in full on 261002.
     */
    private int gloveTotal;

    /**
     * How many artifacts in the set are helms or crowns. Port of {@code headgear_total}; counted by
     * {@code store_base_power()}.
     *
     * <p>Field headgearTotal coded on 260908, commented in full on 261002.
     */
    private int headgearTotal;

    /**
     * How many artifacts in the set are shields. Port of {@code shield_total}; counted by
     * {@code store_base_power()}.
     *
     * <p>Field shieldTotal coded on 260908, commented in full on 261002.
     */
    private int shieldTotal;

    /**
     * How many artifacts in the set are cloaks. Port of {@code cloak_total}; counted by
     * {@code store_base_power()}.
     *
     * <p>Field cloakTotal coded on 260908, commented in full on 261002.
     */
    private int cloakTotal;

    /**
     * How many artifacts in the set are body armour (soft, hard or dragon). Port of
     * {@code armor_total}; counted by {@code store_base_power()}.
     *
     * <p>Field armourTotal coded on 260908, commented in full on 261002.
     */
    private int armourTotal;

    /**
     * How many artifacts in the set fall into none of the named groups - jewellery, light sources,
     * diggers and the like. Port of {@code other_total}; counted by {@code store_base_power()}. An
     * empty {@code TV_NULL} slot is not counted here.
     *
     * <p>Field otherTotal coded on 260908, commented in full on 261002.
     */
    private int otherTotal;

    /**
     * How many artifact slots {@code store_base_power()} walked. Port of {@code total}. Unlike the
     * per-group totals, C increments this for every slot, empty {@code TV_NULL} ones included, so it
     * can exceed the sum of the groups. {@code rescale_freqs()} scales the per-group property counts
     * up to it.
     *
     * <p>Field total coded on 260908, commented in full on 261002.
     */
    private int total;

    /**
     * How many artifacts in the set rate below zero power, which is to say are cursed. Port of
     * {@code neg_power_total}; counted by {@code store_base_power()}, and {@code design_artifact()}
     * uses it to set the odds of making a new artifact bad.
     *
     * <p>Field negPowerTotal coded on 260908, commented in full on 261002.
     */
    private int negPowerTotal;

    // TVal frequency values
    /**
     * The running total of {@link #tvProbs} up to and including each tval, so the entry for the last
     * tval holds the whole sum. Port of {@code tv_freq}, an array of {@code TV_MAX} ints, filled by
     * {@code parse_frequencies()}. {@code get_base_item_tval()} rolls against the last entry and walks
     * up the table to pick a weighted random tval. Starts with a {@code 0} entry for every
     * {@link TValue}.
     *
     * <p>Field tvFreq coded on 260908, commented in full on 261002.
     */
    private Map<TValue, Integer> tvFreq;

    // Artifact power ratings
    /**
     * Each artifact's power rating as {@code artifact_power()} computes it. Port of
     * {@code base_power}, an array of {@code z_info->a_max} ints, filled by
     * {@code store_base_power()}. Starts with a {@code 0} entry for every artifact loaded when the
     * object was built.
     *
     * <p>Field basePower coded on 260908, commented in full on 261002.
     */
    private Map<Artifact, Integer> basePower;

    /**
     * The highest power in the set, ignoring artifacts at or above {@code INHIBIT_POWER}. Port of
     * {@code max_power}; set by {@code store_base_power()}.
     *
     * <p>Field maxPower coded on 260908, commented in full on 261002.
     */
    private int maxPower;

    /**
     * The lowest positive power in the set. Port of {@code min_power}; {@code store_base_power()}
     * starts it at {@code INHIBIT_POWER + 1} and lowers it, so the {@code 0} it holds at construction
     * is never the value it is read at.
     *
     * <p>Field minPower coded on 260908, commented in full on 261002.
     */
    private int minPower;

    /**
     * The mean power of the set. Port of {@code avg_power}; set by {@code store_base_power()}.
     *
     * <p>Field avgPower coded on 260908, commented in full on 261002.
     */
    private int avgPower;

    /**
     * The variance of the set's power. Port of {@code var_power}; set by {@code store_base_power()}.
     *
     * <p>Field varPower coded on 260908, commented in full on 261002.
     */
    private int varPower;

    /**
     * The mean power of the artifacts of each tval. Port of {@code avg_tv_power}, an array of
     * {@code TV_MAX} ints, set by {@code store_base_power()} for each tval with at least one artifact.
     * {@code design_artifact()} samples a new artifact's target power around it. Starts with a
     * {@code 0} entry for every {@link TValue}.
     *
     * <p>Field avgTvPower coded on 260908, commented in full on 261002.
     */
    private Map<TValue, Integer> avgTvPower;

    /**
     * The lowest power among the artifacts of each tval. Port of {@code min_tv_power}, an array of
     * {@code TV_MAX} ints; {@code store_base_power()} resets every entry to {@code INHIBIT_POWER + 1}
     * before lowering them. Starts with a {@code 0} entry for every {@link TValue}.
     *
     * <p>Field minTvPower coded on 260908, commented in full on 261002.
     */
    private Map<TValue, Integer> minTvPower;

    /**
     * The highest power among the artifacts of each tval. Port of {@code max_tv_power}, an array of
     * {@code TV_MAX} ints, set by {@code store_base_power()}. Starts with a {@code 0} entry for every
     * {@link TValue}.
     *
     * <p>Field maxTvPower coded on 260908, commented in full on 261002.
     */
    private Map<TValue, Integer> maxTvPower;

    // Base item levels
    /**
     * The dungeon level of each artifact's base object kind. Port of {@code base_item_level}, an
     * array of {@code z_info->a_max} ints, filled by {@code store_base_power()}. Starts with a
     * {@code 0} entry for every artifact loaded when the object was built.
     *
     * <p>Field baseItemLevel coded on 260908, commented in full on 261002.
     */
    private Map<Artifact, Integer> baseItemLevel;

    // Base item rarities
    /**
     * The allocation probability of each artifact's base object kind. Port of
     * {@code base_item_prob}, an array of {@code z_info->a_max} ints, filled by
     * {@code store_base_power()}. Starts with a {@code 0} entry for every artifact loaded when the
     * object was built.
     *
     * <p>Field baseItemProb coded on 260908, commented in full on 261002.
     */
    private Map<Artifact, Integer> baseItemProb;

    // Artifact rarities
    /**
     * Each artifact's own allocation probability. Port of {@code base_art_alloc}, an array of
     * {@code z_info->a_max} ints, filled by {@code store_base_power()}. Starts with a {@code 0} entry
     * for every artifact loaded when the object was built.
     *
     * <p>Field baseArtAlloc coded on 260908, commented in full on 261002.
     */
    private Map<Artifact, Integer> baseArtAlloc;

    /**
     * Builds an empty set of statistics - the port of C's {@code artifact_set_data_new()} in
     * {@code obj-randart.c}, which {@link ObjectRandart} reaches through its own
     * {@code artifactSetDataNew()} wrapper.
     *
     * <p>C {@code mem_zalloc}s the struct and each of its eleven arrays, then sets the six start and
     * increment values by hand: to-hit and to-dam increments of {@code 4}, to-hit and to-dam start
     * values of {@code 10}, an AC start value of {@code 15} and an AC increment of {@code 5}. This
     * constructor sets the same six values, zeroes every {@code int} counter, and puts a {@code 0}
     * entry in each map for every key C's array could be indexed by: every {@link ArtifactIndex}
     * (the {@code ART_IDX_TOTAL} sentinel too), every {@link TValue} (standing in for
     * {@code TV_MAX}), and every artifact {@link ObjectRegistry#getArtifacts()} holds (standing in for
     * {@code z_info->a_max}).
     *
     * <p>C's implicit precondition that {@code a_info} is loaded is explicit here: if the artifact
     * registry has never been set, {@link ObjectRegistry#getArtifacts()} throws
     * {@link NullPointerException}. An empty registry is accepted and leaves the four artifact-keyed
     * maps empty, as {@code mem_zalloc(0)} would.
     *
     * <p>Constructor ArtifactSetData coded on 260908, commented in full on 261002.
     *
     * @throws NullPointerException if the artifact registry has not been loaded
     */
    public ArtifactSetData() {
        this.hitIncrement = 4;
        this.damIncrement = 4;
        this.hitStartVal = 10;
        this.damStartVal = 10;
        this.acStartVal = 15;
        this.acIncrement = 5;
        this.artProbs = new HashMap<>();
        this.tvProbs = new HashMap<>();
        this.tvNum = new HashMap<>();
        this.bowTotal = 0;
        this.meleeTotal = 0;
        this.bootTotal = 0;
        this.gloveTotal = 0;
        this.headgearTotal = 0;
        this.shieldTotal = 0;
        this.cloakTotal = 0;
        this.armourTotal = 0;
        this.otherTotal = 0;
        this.total = 0;
        this.negPowerTotal = 0;
        this.tvFreq = new HashMap<>();
        this.basePower = new HashMap<>();
        this.maxPower = 0;
        this.minPower = 0;
        this.avgPower = 0;
        this.varPower = 0;
        this.avgTvPower = new HashMap<>();
        this.minTvPower = new HashMap<>();
        this.maxTvPower = new HashMap<>();
        this.baseItemLevel = new HashMap<>();
        this.baseItemProb = new HashMap<>();
        this.baseArtAlloc = new HashMap<>();

        // Initialise the maps
        for (ArtifactIndex index : ArtifactIndex.values()) {
            artProbs.put(index, 0);
        }

        for (TValue value : TValue.values()) {
            tvProbs.put(value, 0);
            tvNum.put(value, 0);
            tvFreq.put(value, 0);
            avgTvPower.put(value, 0);
            minTvPower.put(value, 0);
            maxTvPower.put(value, 0);
        }

        for (Artifact artifact : ObjectRegistry.getArtifacts()) {
            basePower.put(artifact, 0);
            baseItemLevel.put(artifact, 0);
            baseItemProb.put(artifact, 0);
            baseArtAlloc.put(artifact, 0);
        }
    }

    /**
     * Returns the to-hit step size, {@link #hitIncrement}.
     *
     * <p>Function getHitIncrement coded on 260908, commented in full on 261002.
     *
     * @return the to-hit step size
     */
    public int getHitIncrement() {
        return hitIncrement;
    }

    /**
     * Replaces the to-hit step size, {@link #hitIncrement}.
     *
     * <p>Function setHitIncrement coded on 260908, commented in full on 261002.
     *
     * @param hitIncrement the new to-hit step size
     */
    public void setHitIncrement(int hitIncrement) {
        this.hitIncrement = hitIncrement;
    }

    /**
     * Returns the to-dam step size, {@link #damIncrement}.
     *
     * <p>Function getDamIncrement coded on 260908, commented in full on 261002.
     *
     * @return the to-dam step size
     */
    public int getDamIncrement() {
        return damIncrement;
    }

    /**
     * Replaces the to-dam step size, {@link #damIncrement}.
     *
     * <p>Function setDamIncrement coded on 260908, commented in full on 261002.
     *
     * @param damIncrement the new to-dam step size
     */
    public void setDamIncrement(int damIncrement) {
        this.damIncrement = damIncrement;
    }

    /**
     * Returns the starting to-hit, {@link #hitStartVal}.
     *
     * <p>Function getHitStartVal coded on 260908, commented in full on 261002.
     *
     * @return the starting to-hit
     */
    public int getHitStartVal() {
        return hitStartVal;
    }

    /**
     * Replaces the starting to-hit, {@link #hitStartVal}.
     *
     * <p>Function setHitStartVal coded on 260908, commented in full on 261002.
     *
     * @param hitStartVal the new starting to-hit
     */
    public void setHitStartVal(int hitStartVal) {
        this.hitStartVal = hitStartVal;
    }

    /**
     * Returns the starting to-dam, {@link #damStartVal}.
     *
     * <p>Function getDamStartVal coded on 260908, commented in full on 261002.
     *
     * @return the starting to-dam
     */
    public int getDamStartVal() {
        return damStartVal;
    }

    /**
     * Replaces the starting to-dam, {@link #damStartVal}.
     *
     * <p>Function setDamStartVal coded on 260908, commented in full on 261002.
     *
     * @param damStartVal the new starting to-dam
     */
    public void setDamStartVal(int damStartVal) {
        this.damStartVal = damStartVal;
    }

    /**
     * Returns the starting AC bonus, {@link #acStartVal}.
     *
     * <p>Function getAcStartVal coded on 260908, commented in full on 261002.
     *
     * @return the starting AC bonus
     */
    public int getAcStartVal() {
        return acStartVal;
    }

    /**
     * Replaces the starting AC bonus, {@link #acStartVal}.
     *
     * <p>Function setAcStartVal coded on 260908, commented in full on 261002.
     *
     * @param acStartVal the new starting AC bonus
     */
    public void setAcStartVal(int acStartVal) {
        this.acStartVal = acStartVal;
    }

    /**
     * Returns the AC step size, {@link #acIncrement}.
     *
     * <p>Function getAcIncrement coded on 260908, commented in full on 261002.
     *
     * @return the AC step size
     */
    public int getAcIncrement() {
        return acIncrement;
    }

    /**
     * Replaces the AC step size, {@link #acIncrement}.
     *
     * <p>Function setAcIncrement coded on 260908, commented in full on 261002.
     *
     * @param acIncrement the new AC step size
     */
    public void setAcIncrement(int acIncrement) {
        this.acIncrement = acIncrement;
    }

    /**
     * Returns the launcher count, {@link #bowTotal}.
     *
     * <p>Function getBowTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are launchers
     */
    public int getBowTotal() {
        return bowTotal;
    }

    /**
     * Replaces the launcher count, {@link #bowTotal}.
     *
     * <p>Function setBowTotal coded on 260908, commented in full on 261002.
     *
     * @param bowTotal the new launcher count
     */
    public void setBowTotal(int bowTotal) {
        this.bowTotal = bowTotal;
    }

    /**
     * Returns the melee weapon count, {@link #meleeTotal}.
     *
     * <p>Function getMeleeTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are melee weapons
     */
    public int getMeleeTotal() {
        return meleeTotal;
    }

    /**
     * Replaces the melee weapon count, {@link #meleeTotal}.
     *
     * <p>Function setMeleeTotal coded on 260908, commented in full on 261002.
     *
     * @param meleeTotal the new melee weapon count
     */
    public void setMeleeTotal(int meleeTotal) {
        this.meleeTotal = meleeTotal;
    }

    /**
     * Returns the boots count, {@link #bootTotal}.
     *
     * <p>Function getBootTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are boots
     */
    public int getBootTotal() {
        return bootTotal;
    }

    /**
     * Replaces the boots count, {@link #bootTotal}.
     *
     * <p>Function setBootTotal coded on 260908, commented in full on 261002.
     *
     * @param bootTotal the new boots count
     */
    public void setBootTotal(int bootTotal) {
        this.bootTotal = bootTotal;
    }

    /**
     * Returns the gloves count, {@link #gloveTotal}.
     *
     * <p>Function getGloveTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are gloves
     */
    public int getGloveTotal() {
        return gloveTotal;
    }

    /**
     * Replaces the gloves count, {@link #gloveTotal}.
     *
     * <p>Function setGloveTotal coded on 260908, commented in full on 261002.
     *
     * @param gloveTotal the new gloves count
     */
    public void setGloveTotal(int gloveTotal) {
        this.gloveTotal = gloveTotal;
    }

    /**
     * Returns the headgear count, {@link #headgearTotal}.
     *
     * <p>Function getHeadgearTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are helms or crowns
     */
    public int getHeadgearTotal() {
        return headgearTotal;
    }

    /**
     * Replaces the headgear count, {@link #headgearTotal}.
     *
     * <p>Function setHeadgearTotal coded on 260908, commented in full on 261002.
     *
     * @param headgearTotal the new headgear count
     */
    public void setHeadgearTotal(int headgearTotal) {
        this.headgearTotal = headgearTotal;
    }

    /**
     * Returns the shield count, {@link #shieldTotal}.
     *
     * <p>Function getShieldTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are shields
     */
    public int getShieldTotal() {
        return shieldTotal;
    }

    /**
     * Replaces the shield count, {@link #shieldTotal}.
     *
     * <p>Function setShieldTotal coded on 260908, commented in full on 261002.
     *
     * @param shieldTotal the new shield count
     */
    public void setShieldTotal(int shieldTotal) {
        this.shieldTotal = shieldTotal;
    }

    /**
     * Returns the cloak count, {@link #cloakTotal}.
     *
     * <p>Function getCloakTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are cloaks
     */
    public int getCloakTotal() {
        return cloakTotal;
    }

    /**
     * Replaces the cloak count, {@link #cloakTotal}.
     *
     * <p>Function setCloakTotal coded on 260908, commented in full on 261002.
     *
     * @param cloakTotal the new cloak count
     */
    public void setCloakTotal(int cloakTotal) {
        this.cloakTotal = cloakTotal;
    }

    /**
     * Returns the body armour count, {@link #armourTotal}.
     *
     * <p>Function getArmourTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set are body armour
     */
    public int getArmourTotal() {
        return armourTotal;
    }

    /**
     * Replaces the body armour count, {@link #armourTotal}.
     *
     * <p>Function setArmourTotal coded on 260908, commented in full on 261002.
     *
     * @param armourTotal the new body armour count
     */
    public void setArmourTotal(int armourTotal) {
        this.armourTotal = armourTotal;
    }

    /**
     * Returns the count of artifacts in no named group, {@link #otherTotal}.
     *
     * <p>Function getOtherTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set fall into no named group
     */
    public int getOtherTotal() {
        return otherTotal;
    }

    /**
     * Replaces the count of artifacts in no named group, {@link #otherTotal}.
     *
     * <p>Function setOtherTotal coded on 260908, commented in full on 261002.
     *
     * @param otherTotal the new count
     */
    public void setOtherTotal(int otherTotal) {
        this.otherTotal = otherTotal;
    }

    /**
     * Returns the number of artifact slots walked, {@link #total}, empty slots included.
     *
     * <p>Function getTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifact slots were walked
     */
    public int getTotal() {
        return total;
    }

    /**
     * Replaces the number of artifact slots walked, {@link #total}.
     *
     * <p>Function setTotal coded on 260908, commented in full on 261002.
     *
     * @param total the new slot count
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Returns the number of negative-power (cursed) artifacts, {@link #negPowerTotal}.
     *
     * <p>Function getNegPowerTotal coded on 260908, commented in full on 261002.
     *
     * @return how many artifacts in the set rate below zero power
     */
    public int getNegPowerTotal() {
        return negPowerTotal;
    }

    /**
     * Replaces the number of negative-power artifacts, {@link #negPowerTotal}.
     *
     * <p>Function setNegPowerTotal coded on 260908, commented in full on 261002.
     *
     * @param negPowerTotal the new count
     */
    public void setNegPowerTotal(int negPowerTotal) {
        this.negPowerTotal = negPowerTotal;
    }

    /**
     * Returns the highest power below {@code INHIBIT_POWER}, {@link #maxPower}.
     *
     * <p>Function getMaxPower coded on 260908, commented in full on 261002.
     *
     * @return the set's highest power
     */
    public int getMaxPower() {
        return maxPower;
    }

    /**
     * Replaces the highest power, {@link #maxPower}.
     *
     * <p>Function setMaxPower coded on 260908, commented in full on 261002.
     *
     * @param maxPower the new highest power
     */
    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

    /**
     * Returns the lowest positive power, {@link #minPower}.
     *
     * <p>Function getMinPower coded on 260908, commented in full on 261002.
     *
     * @return the set's lowest positive power
     */
    public int getMinPower() {
        return minPower;
    }

    /**
     * Replaces the lowest positive power, {@link #minPower}.
     *
     * <p>Function setMinPower coded on 260908, commented in full on 261002.
     *
     * @param minPower the new lowest positive power
     */
    public void setMinPower(int minPower) {
        this.minPower = minPower;
    }

    /**
     * Returns the mean power, {@link #avgPower}.
     *
     * <p>Function getAvgPower coded on 260908, commented in full on 261002.
     *
     * @return the set's mean power
     */
    public int getAvgPower() {
        return avgPower;
    }

    /**
     * Replaces the mean power, {@link #avgPower}.
     *
     * <p>Function setAvgPower coded on 260908, commented in full on 261002.
     *
     * @param avgPower the new mean power
     */
    public void setAvgPower(int avgPower) {
        this.avgPower = avgPower;
    }

    /**
     * Returns the power variance, {@link #varPower}.
     *
     * <p>Function getVarPower coded on 260908, commented in full on 261002.
     *
     * @return the variance of the set's power
     */
    public int getVarPower() {
        return varPower;
    }

    /**
     * Replaces the power variance, {@link #varPower}.
     *
     * <p>Function setVarPower coded on 260908, commented in full on 261002.
     *
     * @param varPower the new variance
     */
    public void setVarPower(int varPower) {
        this.varPower = varPower;
    }

    /**
     * Returns one property's counter from {@link #artProbs} - C's {@code data->art_probs[index]}.
     *
     * <p>Function getArtProbs coded on 260908, commented in full on 261002.
     *
     * @param index the property to read
     * @return that property's counter
     */
    public int getArtProbs(ArtifactIndex index) {
        return artProbs.get(index);
    }

    /**
     * Stores one property's counter in {@link #artProbs} - C's {@code data->art_probs[index] = ...}.
     * C's {@code +=} and {@code ++} updates become a get followed by this set.
     *
     * <p>Function setArtProbs coded on 260908, commented in full on 261002.
     *
     * @param index    the property to write
     * @param artProbs the new counter
     */
    public void setArtProbs(ArtifactIndex index, int artProbs) {
        this.artProbs.put(index, artProbs);
    }

    /**
     * Returns one tval's base-item count from {@link #tvProbs} - C's {@code data->tv_probs[index]}.
     *
     * <p>Function getTvProbs coded on 260908, commented in full on 261002.
     *
     * @param index the tval to read
     * @return how many artifacts are built on that tval
     */
    public int getTvProbs(TValue index) {
        return tvProbs.get(index);
    }

    /**
     * Stores one tval's base-item count in {@link #tvProbs}.
     *
     * <p>Function setTvProbs coded on 260908, commented in full on 261002.
     *
     * @param index   the tval to write
     * @param tvProbs the new count
     */
    public void setTvProbs(TValue index, int tvProbs) {
        this.tvProbs.put(index, tvProbs);
    }

    /**
     * Returns how many artifacts of one tval were rated, from {@link #tvNum} - C's
     * {@code data->tv_num[index]}.
     *
     * <p>Function getTvNum coded on 260908, commented in full on 261002.
     *
     * @param index the tval to read
     * @return how many artifacts of that tval were rated
     */
    public int getTvNum(TValue index) {
        return tvNum.get(index);
    }

    /**
     * Stores how many artifacts of one tval were rated, in {@link #tvNum}.
     *
     * <p>Function setTvNum coded on 260908, commented in full on 261002.
     *
     * @param index the tval to write
     * @param tvNum the new count
     */
    public void setTvNum(TValue index, int tvNum) {
        this.tvNum.put(index, tvNum);
    }

    /**
     * Returns one tval's running frequency total from {@link #tvFreq} - C's
     * {@code data->tv_freq[index]}.
     *
     * <p>Function getTvFreq coded on 260908, commented in full on 261002.
     *
     * @param index the tval to read
     * @return the running total of base-item counts up to and including that tval
     */
    public int getTvFreq(TValue index) {
        return tvFreq.get(index);
    }

    /**
     * Stores one tval's running frequency total in {@link #tvFreq}.
     *
     * <p>Function setTvFreq coded on 260908, commented in full on 261002.
     *
     * @param index  the tval to write
     * @param tvFreq the new running total
     */
    public void setTvFreq(TValue index, int tvFreq) {
        this.tvFreq.put(index, tvFreq);
    }

    /**
     * Returns one artifact's power rating from {@link #basePower} - C's
     * {@code data->base_power[aidx]}.
     *
     * <p>Function getBasePower coded on 260908, commented in full on 261002.
     *
     * @param index the artifact to read
     * @return that artifact's power rating
     * @throws NullPointerException if {@code index} was not loaded when this object was built
     */
    public int getBasePower(Artifact index) {
        return basePower.get(index);
    }

    /**
     * Stores one artifact's power rating in {@link #basePower}.
     *
     * <p>Function setBasePower coded on 260908, commented in full on 261002.
     *
     * @param index     the artifact to write
     * @param basePower the new power rating
     */
    public void setBasePower(Artifact index, int basePower) {
        this.basePower.put(index, basePower);
    }

    /**
     * Returns one tval's mean power from {@link #avgTvPower} - C's {@code data->avg_tv_power[index]}.
     *
     * <p>Function getAvgTvPower coded on 260908, commented in full on 261002.
     *
     * @param index the tval to read
     * @return the mean power of that tval's artifacts
     */
    public int getAvgTvPower(TValue index) {
        return avgTvPower.get(index);
    }

    /**
     * Stores one tval's mean power in {@link #avgTvPower}.
     *
     * <p>Function setAvgTvPower coded on 260908, commented in full on 261002.
     *
     * @param index      the tval to write
     * @param avgTvPower the new mean power
     */
    public void setAvgTvPower(TValue index, int avgTvPower) {
        this.avgTvPower.put(index, avgTvPower);
    }

    /**
     * Returns one tval's lowest power from {@link #minTvPower} - C's {@code data->min_tv_power[index]}.
     *
     * <p>Function getMinTvPower coded on 260908, commented in full on 261002.
     *
     * @param index the tval to read
     * @return the lowest power among that tval's artifacts
     */
    public int getMinTvPower(TValue index) {
        return minTvPower.get(index);
    }

    /**
     * Stores one tval's lowest power in {@link #minTvPower}.
     *
     * <p>Function setMinTvPower coded on 260908, commented in full on 261002.
     *
     * @param index      the tval to write
     * @param minTvPower the new lowest power
     */
    public void setMinTvPower(TValue index, int minTvPower) {
        this.minTvPower.put(index, minTvPower);
    }

    /**
     * Returns one tval's highest power from {@link #maxTvPower} - C's {@code data->max_tv_power[index]}.
     *
     * <p>Function getMaxTvPower coded on 260908, commented in full on 261002.
     *
     * @param index the tval to read
     * @return the highest power among that tval's artifacts
     */
    public int getMaxTvPower(TValue index) {
        return maxTvPower.get(index);
    }

    /**
     * Stores one tval's highest power in {@link #maxTvPower}.
     *
     * <p>Function setMaxTvPower coded on 260908, commented in full on 261002.
     *
     * @param index      the tval to write
     * @param maxTvPower the new highest power
     */
    public void setMaxTvPower(TValue index, int maxTvPower) {
        this.maxTvPower.put(index, maxTvPower);
    }

    /**
     * Returns the level of one artifact's base object kind from {@link #baseItemLevel} - C's
     * {@code data->base_item_level[aidx]}.
     *
     * <p>Function getBaseItemLevel coded on 260908, commented in full on 261002.
     *
     * @param index the artifact to read
     * @return the level of that artifact's base object kind
     * @throws NullPointerException if {@code index} was not loaded when this object was built
     */
    public int getBaseItemLevel(Artifact index) {
        return baseItemLevel.get(index);
    }

    /**
     * Stores the level of one artifact's base object kind in {@link #baseItemLevel}.
     *
     * <p>Function setBaseItemLevel coded on 260908, commented in full on 261002.
     *
     * @param index         the artifact to write
     * @param baseItemLevel the new level
     */
    public void setBaseItemLevel(Artifact index, int baseItemLevel) {
        this.baseItemLevel.put(index, baseItemLevel);
    }

    /**
     * Returns the allocation probability of one artifact's base object kind from
     * {@link #baseItemProb} - C's {@code data->base_item_prob[aidx]}.
     *
     * <p>Function getBaseItemProb coded on 260908, commented in full on 261002.
     *
     * @param index the artifact to read
     * @return the allocation probability of that artifact's base object kind
     * @throws NullPointerException if {@code index} was not loaded when this object was built
     */
    public int getBaseItemProb(Artifact index) {
        return baseItemProb.get(index);
    }

    /**
     * Stores the allocation probability of one artifact's base object kind in {@link #baseItemProb}.
     *
     * <p>Function setBaseItemProb coded on 260908, commented in full on 261002.
     *
     * @param index        the artifact to write
     * @param baseItemProb the new allocation probability
     */
    public void setBaseItemProb(Artifact index, int baseItemProb) {
        this.baseItemProb.put(index, baseItemProb);
    }

    /**
     * Returns one artifact's own allocation probability from {@link #baseArtAlloc} - C's
     * {@code data->base_art_alloc[aidx]}.
     *
     * <p>Function getBaseArtAlloc coded on 260908, commented in full on 261002.
     *
     * @param index the artifact to read
     * @return that artifact's allocation probability
     * @throws NullPointerException if {@code index} was not loaded when this object was built
     */
    public int getBaseArtAlloc(Artifact index) {
        return baseArtAlloc.get(index);
    }

    /**
     * Stores one artifact's own allocation probability in {@link #baseArtAlloc}.
     *
     * <p>Function setBaseArtAlloc coded on 260908, commented in full on 261002.
     *
     * @param index        the artifact to write
     * @param baseArtAlloc the new allocation probability
     */
    public void setBaseArtAlloc(Artifact index, int baseArtAlloc) {
        this.baseArtAlloc.put(index, baseArtAlloc);
    }
}
