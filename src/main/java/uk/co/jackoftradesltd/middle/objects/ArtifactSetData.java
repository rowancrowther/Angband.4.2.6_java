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
import java.util.List;
import java.util.Map;

public class ArtifactSetData {
    // Mean start and increment values for toHit, toDam and toAC
    private int hitIncrement;
    private int damIncrement;
    private int hitStartVal;
    private int damStartVal;
    private int acStartVal;
    private int acIncrement;

    // Data structures for learned probabilities
    private Map<ArtifactIndex, Integer> artProbs;
    private Map<TValue, Integer> tvProbs;
    private Map<TValue, Integer> tvNum;
    private int bowTotal;
    private int meleeTotal;
    private int bootTotal;
    private int gloveTotal;
    private int headgearTotal;
    private int shieldTotal;
    private int cloakTotal;
    private int armourTotal;
    private int otherTotal;
    private int total;
    private int negPowerTotal;

    // TVal frequency values
    private Map<TValue, Integer> tvFreq;

    // Artifact power ratings
    private Map<Artifact, Integer> basePower;
    private int maxPower;
    private int minPower;
    private int avgPower;
    private int varPower;
    private Map<TValue, Integer> avgTvPower;
    private Map<TValue, Integer> minTvPower;
    private Map<TValue, Integer> maxTvPower;

    // Base item levels
    private Map<Artifact, Integer> baseItemLevel;

    // Base item rarities
    private Map<Artifact, Integer> baseItemProb;

    // Artifact rarities
    private Map<Artifact, Integer> baseArtAlloc;

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

    public int getHitIncrement() {
        return hitIncrement;
    }

    public void setHitIncrement(int hitIncrement) {
        this.hitIncrement = hitIncrement;
    }

    public int getDamIncrement() {
        return damIncrement;
    }

    public void setDamIncrement(int damIncrement) {
        this.damIncrement = damIncrement;
    }

    public int getHitStartVal() {
        return hitStartVal;
    }

    public void setHitStartVal(int hitStartVal) {
        this.hitStartVal = hitStartVal;
    }

    public int getDamStartVal() {
        return damStartVal;
    }

    public void setDamStartVal(int damStartVal) {
        this.damStartVal = damStartVal;
    }

    public int getAcStartVal() {
        return acStartVal;
    }

    public void setAcStartVal(int acStartVal) {
        this.acStartVal = acStartVal;
    }

    public int getAcIncrement() {
        return acIncrement;
    }

    public void setAcIncrement(int acIncrement) {
        this.acIncrement = acIncrement;
    }

    public int getBowTotal() {
        return bowTotal;
    }

    public void setBowTotal(int bowTotal) {
        this.bowTotal = bowTotal;
    }

    public int getMeleeTotal() {
        return meleeTotal;
    }

    public void setMeleeTotal(int meleeTotal) {
        this.meleeTotal = meleeTotal;
    }

    public int getBootTotal() {
        return bootTotal;
    }

    public void setBootTotal(int bootTotal) {
        this.bootTotal = bootTotal;
    }

    public int getGloveTotal() {
        return gloveTotal;
    }

    public void setGloveTotal(int gloveTotal) {
        this.gloveTotal = gloveTotal;
    }

    public int getHeadgearTotal() {
        return headgearTotal;
    }

    public void setHeadgearTotal(int headgearTotal) {
        this.headgearTotal = headgearTotal;
    }

    public int getShieldTotal() {
        return shieldTotal;
    }

    public void setShieldTotal(int shieldTotal) {
        this.shieldTotal = shieldTotal;
    }

    public int getCloakTotal() {
        return cloakTotal;
    }

    public void setCloakTotal(int cloakTotal) {
        this.cloakTotal = cloakTotal;
    }

    public int getArmourTotal() {
        return armourTotal;
    }

    public void setArmourTotal(int armourTotal) {
        this.armourTotal = armourTotal;
    }

    public int getOtherTotal() {
        return otherTotal;
    }

    public void setOtherTotal(int otherTotal) {
        this.otherTotal = otherTotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNegPowerTotal() {
        return negPowerTotal;
    }

    public void setNegPowerTotal(int negPowerTotal) {
        this.negPowerTotal = negPowerTotal;
    }

    public int getMaxPower() {
        return maxPower;
    }

    public void setMaxPower(int maxPower) {
        this.maxPower = maxPower;
    }

    public int getMinPower() {
        return minPower;
    }

    public void setMinPower(int minPower) {
        this.minPower = minPower;
    }

    public int getAvgPower() {
        return avgPower;
    }

    public void setAvgPower(int avgPower) {
        this.avgPower = avgPower;
    }

    public int getVarPower() {
        return varPower;
    }

    public void setVarPower(int varPower) {
        this.varPower = varPower;
    }

    public int getArtProbs(ArtifactIndex index) {
        return artProbs.get(index);
    }

    public void setArtProbs(ArtifactIndex index, int artProbs) {
        this.artProbs.put(index, artProbs);
    }

    public int getTvProbs(TValue index) {
        return tvProbs.get(index);
    }

    public void setTvProbs(TValue index, int tvProbs) {
        this.tvProbs.put(index, tvProbs);
    }

    public int getTvNum(TValue index) {
        return tvNum.get(index);
    }

    public void setTvNum(TValue index, int tvNum) {
        this.tvNum.put(index, tvNum);
    }

    public int getTvFreq(TValue index) {
        return tvFreq.get(index);
    }

    public void setTvFreq(TValue index, int tvFreq) {
        this.tvFreq.put(index, tvFreq);
    }

    public int getBasePower(Artifact index) {
        return basePower.get(index);
    }

    public void setBasePower(Artifact index, int basePower) {
        this.basePower.put(index, basePower);
    }

    public int getAvgTvPower(TValue index) {
        return avgTvPower.get(index);
    }

    public void setAvgTvPower(TValue index, int avgTvPower) {
        this.avgTvPower.put(index, avgTvPower);
    }

    public int getMinTvPower(TValue index) {
        return minTvPower.get(index);
    }

    public void setMinTvPower(TValue index, int minTvPower) {
        this.minTvPower.put(index, minTvPower);
    }

    public int getMaxTvPower(TValue index) {
        return maxTvPower.get(index);
    }

    public void setMaxTvPower(TValue index, int maxTvPower) {
        this.maxTvPower.put(index, maxTvPower);
    }

    public int getBaseItemLevel(Artifact index) {
        return baseItemLevel.get(index);
    }

    public void setBaseItemLevel(Artifact index, int baseItemLevel) {
        this.baseItemLevel.put(index, baseItemLevel);
    }

    public int getBaseItemProb(Artifact index) {
        return baseItemProb.get(index);
    }

    public void setBaseItemProb(Artifact index, int baseItemProb) {
        this.baseItemProb.put(index, baseItemProb);
    }

    public int getBaseArtAlloc(Artifact index) {
        return baseArtAlloc.get(index);
    }

    public void setBaseArtAlloc(Artifact index, int baseArtAlloc) {
        this.baseArtAlloc.put(index, baseArtAlloc);
    }
}
