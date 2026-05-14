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

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

public class TerrainFeature {
    private TerrainFlags code;
    private String name;
    private String description;
    private int featureIndex;

    private TerrainFlags mimic;
    private int priority;

    private int shopNum;
    private int dig;

    Flag<TerrainFeatureFlags> flags;

    private AngbandDisplayCharacter displayCharacter;

    private String walkMsg;
    private String runMsg;
    private String hurtMsg;
    private String dieMsg;
    private String confusedMsg;
    private String lookPrefix;
    private String lookInPreposition;
    private MonsterRaceFlag resistFlag;

    public TerrainFeature(TerrainFlags code,
                          String name,
                          String description,
                          TerrainFlags mimic,
                          int priority,
                          int shopNum,
                          int dig,
                          Flag<TerrainFeatureFlags> flags,
                          AngbandDisplayCharacter displayCharacter,
                          String walkMsg,
                          String runMsg,
                          String hurtMsg,
                          String dieMsg,
                          String confusedMsg,
                          String lookPrefix,
                          String lookInPreposition,
                          MonsterRaceFlag resistFlag) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.mimic = mimic;
        this.priority = priority;
        this.shopNum = shopNum;
        this.dig = dig;
        this.flags = flags;
        this.displayCharacter = displayCharacter;
        this.walkMsg = walkMsg;
        this.runMsg = runMsg;
        this.hurtMsg = hurtMsg;
        this.dieMsg = dieMsg;
        this.confusedMsg = confusedMsg;
        this.lookPrefix = lookPrefix;
        this.lookInPreposition = lookInPreposition;
        this.resistFlag = resistFlag;
    }

    @Override
    public String toString() {
        return "TerrainFeature{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", featureIndex=" + featureIndex +
                ", mimic=" + mimic +
                ", priority=" + priority +
                ", shopNum=" + shopNum +
                ", dig=" + dig +
                ", flags=" + flags +
                ", displayCharacter=" + displayCharacter +
                ", walkMsg='" + walkMsg + '\'' +
                ", runMsg='" + runMsg + '\'' +
                ", hurtMsg='" + hurtMsg + '\'' +
                ", dieMsg='" + dieMsg + '\'' +
                ", confusedMsg='" + confusedMsg + '\'' +
                ", lookPrefix='" + lookPrefix + '\'' +
                ", lookInPreposition='" + lookInPreposition + '\'' +
                ", resistFlag=" + resistFlag +
                '}';
    }

    public String getName() {
        return name;
    }
}