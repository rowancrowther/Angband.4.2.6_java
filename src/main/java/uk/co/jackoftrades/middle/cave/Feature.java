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

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftrades.middle.cave.enums.TerrainFlags;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.Objects;

/**
 * The definition of one terrain feature type (as loaded from {@code terrain.txt})
 * — its code, name, display glyph, the {@link TerrainFeatureFlags} that give it
 * its behaviour, and the various messages shown when the player interacts with
 * it. The many {@code isXxx()} predicates are convenience tests over {@link
 * #flags}. This is the Java port of the C original's {@code struct feature}
 * ({@code src/cave.h}).
 *
 * @author ClaudeCode
 */
public class Feature {
    /**
     * The feature's terrain code (its {@link TerrainFlags} identity).
     *
     * @author ClaudeCode
     */
    private TerrainFlags code;
    /**
     * The feature's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * Human-readable description of the feature.
     *
     * @author ClaudeCode
     */
    private String description;
    /**
     * Index of this feature in the global feature table.
     *
     * @author ClaudeCode
     */
    private int featureIndex;

    /**
     * The terrain this feature mimics/disguises as, or {@code null} if none.
     *
     * @author ClaudeCode
     */
    private TerrainFlags mimic;
    /**
     * Drawing priority when several features could be shown for a grid.
     *
     * @author ClaudeCode
     */
    private int priority;

    /**
     * Shop number when this feature is a shop entrance.
     *
     * @author ClaudeCode
     */
    private int shopNum;
    /**
     * Difficulty of digging through this feature.
     *
     * @author ClaudeCode
     */
    private int dig;

    /**
     * The feature's behaviour flags (passable, wall, door, stair, …).
     *
     * @author ClaudeCode
     */
    Flag<TerrainFeatureFlags> flags;

    /**
     * The glyph and colour used to draw this feature.
     *
     * @author ClaudeCode
     */
    private AngbandDisplayCharacter displayCharacter;

    /**
     * Message shown when the player walks onto this feature.
     *
     * @author ClaudeCode
     */
    private String walkMsg;
    /**
     * Message shown when the player runs onto/through this feature.
     *
     * @author ClaudeCode
     */
    private String runMsg;
    /**
     * Message shown when the feature hurts the player.
     *
     * @author ClaudeCode
     */
    private String hurtMsg;
    /**
     * Message shown when the player dies to this feature.
     *
     * @author ClaudeCode
     */
    private String dieMsg;
    /**
     * Message shown when the player is confused on this feature.
     *
     * @author ClaudeCode
     */
    private String confusedMsg;
    /**
     * Prefix used when describing this feature in "look" output.
     *
     * @author ClaudeCode
     */
    private String lookPrefix;
    /**
     * Preposition used when describing being "in" this feature in "look" output.
     *
     * @author ClaudeCode
     */
    private String lookInPreposition;
    /**
     * Monster race flag granting resistance to this feature's effects.
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag resistFlag;

    /**
     * Build a feature definition from its parsed data-file fields.
     *
     * @param code              terrain code identity
     * @param name              feature name
     * @param description       human-readable description
     * @param mimic             terrain this feature mimics, or {@code null}
     * @param priority          drawing priority
     * @param shopNum           shop number (for shop entrances)
     * @param dig               digging difficulty
     * @param flags             behaviour flags
     * @param displayCharacter  display glyph and colour
     * @param walkMsg           walk-onto message
     * @param runMsg            run-onto message
     * @param hurtMsg           hurt message
     * @param dieMsg            death message
     * @param confusedMsg       confused message
     * @param lookPrefix        look-output prefix
     * @param lookInPreposition look-output "in" preposition
     * @param resistFlag        race flag granting resistance
     * @author ClaudeCode
     */
    public Feature(TerrainFlags code,
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

    /**
     * @return this feature's terrain code
     * @author ClaudeCode
     */
    public TerrainFlags getTerrainFlag() {
        return code;
    }

    /*
     *   Feature predicates
     */

    /**
     * Tests to see whether this feature is mimicing or not
     *
     * @return true if this feature is not what it seems
     */
    public boolean isMimicing() {
        return mimic != null;
    }

    /**
     * Tests for Magma
     *
     * @return true if feature is magma
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isMagma() {
        return flags.has(TerrainFeatureFlags.TF_MAGMA);
    }

    /**
     * Tests for Quartz
     *
     * @return true if feature is quartz
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isQuartz() {
        return flags.has(TerrainFeatureFlags.TF_QUARTZ);
    }

    /**
     * Test for Granite
     *
     * @return true if feature is granite
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isGranite() {
        return flags.has(TerrainFeatureFlags.TF_GRANITE);
    }

    /**
     * Test for mineral wall with treasure
     *
     * @return true if feature is mineral (magma/quartz) with gold
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isTreasure() {
        return flags.has(TerrainFeatureFlags.TF_GOLD);
    }

    /**
     * Test if the feature is a solid wall (not rubble)
     *
     * @return true if the feature is a solid wall
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isWall() {
        return flags.has(TerrainFeatureFlags.TF_WALL);
    }

    /**
     * Test for whether the feature is a floor
     *
     * @return true if the feature is floor
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isFloor() {
        return flags.has(TerrainFeatureFlags.TF_FLOOR);
    }

    /**
     * Test if the feature can hold a trap
     *
     * @return true if the feature can hold a trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isTrapHolding() {
        return flags.has(TerrainFeatureFlags.TF_TRAP);
    }

    /**
     * Test to see if this feature can hold an object
     *
     * @return true if the feature can hold a trap
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isObjectHolding() {
        return flags.has(TerrainFeatureFlags.TF_OBJECT);
    }

    /**
     * Test to see if monsters can walk through this feature
     *
     * @return true if this feature is passable
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isMonsterWalkable() {
        return flags.has(TerrainFeatureFlags.TF_PASSABLE);
    }

    /**
     * Test to see if the feature is a shop
     *
     * @return true if the feature is a shop entrance
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isShop() {
        return flags.has(TerrainFeatureFlags.TF_SHOP);
    }

    /**
     * Test line of sight
     *
     * @return true if the feature allows line of sight
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isLos() {
        return flags.has(TerrainFeatureFlags.TF_LOS);
    }

    /**
     * Test player passability
     *
     * @return true if the player can pass through this feature
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isPassable() {
        return flags.has(TerrainFeatureFlags.TF_PASSABLE);
    }

    /**
     * Test projection passibility
     *
     * @return true if the feature allows projectables through it
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isProjectable() {
        return flags.has(TerrainFeatureFlags.TF_PROJECT);
    }

    /**
     * Tests whether a feature can be lit by light sources
     *
     * @return true if this feature can be lit by light sources
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isTorch() {
        return flags.has(TerrainFeatureFlags.TF_TORCH);
    }

    /**
     * Test for internally lit features
     *
     * @return true if this feature is bright
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isBright() {
        return flags.has(TerrainFeatureFlags.TF_BRIGHT);
    }

    /**
     * Test for internally lit features
     *
     * @return true if this feature if fiery
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isFiery() {
        return flags.has(TerrainFeatureFlags.TF_FIERY);
    }

    /**
     * Test for whether the feature carries monster flow information
     *
     * @return true if the feature DOESN'T carry monster flow information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isNoFlow() {
        return flags.has(TerrainFeatureFlags.TF_NO_FLOW);
    }

    /**
     * Tests for whether this feature carries player scent
     *
     * @return true if the feature DOESN'T carry player scent information
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isNoScent() {
        return flags.has(TerrainFeatureFlags.TF_NO_SCENT);
    }

    /**
     * Test for whether this feature carries player scent
     *
     * @return true if the feature DOESN'T carry player scent
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean noScent() {
        return flags.has(TerrainFeatureFlags.TF_NO_SCENT);
    }

    /**
     * Test whether the feature should have smooth boundaries (for dungeon generation)
     *
     * @return true if the feature should have smooth boundaries
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isSmooth() {
        return flags.has(TerrainFeatureFlags.TF_SMOOTH);
    }

    /**
     * Test to see if there is any door in this feature
     *
     * @return true if this feature is any door type
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean hasAnyDoor() {
        return flags.has(TerrainFeatureFlags.TF_DOOR_ANY);
    }

    /**
     * Test for whether a feature is that of a permanent wall
     *
     * @return true if this is a permanent wall
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isPermanent() {
        return flags.has(TerrainFeatureFlags.TF_PERMANENT);
    }

    /**
     * Tests to see if the feature is rock
     *
     * @return true if the feature is rock
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isRock() {
        return flags.has(TerrainFeatureFlags.TF_ROCK);
    }

    /**
     * Tests to see if the feature is an open door
     *
     * @return true for open doors
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isOpenDoor() {
        return flags.has(TerrainFeatureFlags.TF_CLOSABLE);
    }

    /**
     * Tests to see if this feature is a closed door
     *
     * @return true for closed doors
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isClosedDoor() {
        return flags.has(TerrainFeatureFlags.TF_DOOR_CLOSED);
    }

    /**
     * Tests to see whether the feature is a door which can be closed
     *
     * @return true for closeable doors
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isCloseable() {
        return flags.has(TerrainFeatureFlags.TF_CLOSABLE);
    }

    /**
     * Tests for the presence of any staricase
     *
     * @return true for any staircase
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isStair() {
        return flags.has(TerrainFeatureFlags.TF_STAIR);
    }

    /**
     * Test for the presence of an up staircase
     *
     * @return true if this is an up staircase
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isUpStair() {
        return flags.has(TerrainFeatureFlags.TF_UPSTAIR);
    }

    /**
     * Tests for presence of a down staircase
     *
     * @return true if this is a down staircase
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isDownStair() {
        return flags.has(TerrainFeatureFlags.TF_DOWNSTAIR);
    }

    /**
     * Tests to see whether anything is known about this feature
     *
     * @return true if NOTHING is known about this feature
     */
    @CheckReturnValue
    @Contract(pure = true)
    public boolean isNoFeat() {
        return flags.has(TerrainFeatureFlags.TF_NONE);
    }

    /**
     * Test whether this feature is interesting or not
     *
     * @return true if this feature is flagged as interesting (noticed when looking around)
     */
    public boolean isInteresting() {
        return flags.has(TerrainFeatureFlags.TF_INTERESTING);
    }

    /**
     * Get the name of this square
     *
     * @return the name of this square
     */
    public String getName() {
        return name;
    }

    /**
     * @return this feature's terrain code (same as {@link #getTerrainFlag()})
     * @author ClaudeCode
     */
    public TerrainFlags getCodeFlags() {
        return code;
    }

    /**
     * Resolve the feature this one mimics by looking it up in the global table.
     *
     * @return the mimicked {@link Feature}
     * @author ClaudeCode
     */
    public Feature getMimic() {
        return GameConstants.lookupFeature(mimic);
    }

    /**
     * @return a debug string listing this feature's fields
     * @author ClaudeCode
     */
    @Override
    public String toString() {
        return "Feature{" +
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

    /**
     * Value equality across all fields.
     *
     * @param o the object to compare against
     * @return true if {@code o} is an equivalent {@code Feature}
     * @author ClaudeCode
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Feature feature = (Feature) o;
        return featureIndex == feature.featureIndex && priority == feature.priority && shopNum == feature.shopNum && dig == feature.dig && code == feature.code && Objects.equals(getName(), feature.getName()) && Objects.equals(description, feature.description) && mimic == feature.mimic && Objects.equals(flags, feature.flags) && Objects.equals(displayCharacter, feature.displayCharacter) && Objects.equals(walkMsg, feature.walkMsg) && Objects.equals(runMsg, feature.runMsg) && Objects.equals(hurtMsg, feature.hurtMsg) && Objects.equals(dieMsg, feature.dieMsg) && Objects.equals(confusedMsg, feature.confusedMsg) && Objects.equals(lookPrefix, feature.lookPrefix) && Objects.equals(lookInPreposition, feature.lookInPreposition) && resistFlag == feature.resistFlag;
    }

    /**
     * Hash code consistent with {@link #equals(Object)}, combining all fields.
     *
     * @return this feature's hash code
     * @author ClaudeCode
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(code);
        result = 31 * result + Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(description);
        result = 31 * result + featureIndex;
        result = 31 * result + Objects.hashCode(mimic);
        result = 31 * result + priority;
        result = 31 * result + shopNum;
        result = 31 * result + dig;
        result = 31 * result + Objects.hashCode(flags);
        result = 31 * result + Objects.hashCode(displayCharacter);
        result = 31 * result + Objects.hashCode(walkMsg);
        result = 31 * result + Objects.hashCode(runMsg);
        result = 31 * result + Objects.hashCode(hurtMsg);
        result = 31 * result + Objects.hashCode(dieMsg);
        result = 31 * result + Objects.hashCode(confusedMsg);
        result = 31 * result + Objects.hashCode(lookPrefix);
        result = 31 * result + Objects.hashCode(lookInPreposition);
        result = 31 * result + Objects.hashCode(resistFlag);
        return result;
    }
}