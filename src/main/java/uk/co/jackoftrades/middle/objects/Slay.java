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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

/**
 * A weapon slay (as loaded from {@code slay.txt}) — bonus damage against a class
 * of monster, with the targeted monster type/level, the melee and ranged verbs,
 * the damage multipliers (standard and O-combat) and a power rating. The
 * {@code code} encodes the monster type and level (e.g. {@code EVIL_2}), which
 * the constructor splits out. This is the Java port of the C original's
 * {@code struct slay} ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class Slay {
    /**
     * The slay's code, encoding monster type and level (e.g. {@code EVIL_2}).
     *
     * @author ClaudeCode
     */
    private String code;
    /**
     * The monster race flag this slay targets (parsed from {@link #code}).
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag monsterType;
    /**
     * The monster level component parsed from {@link #code}.
     *
     * @author ClaudeCode
     */
    private int monsterLevel;
    /**
     * The slay's display name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * A specific monster base targeted by the slay, if any.
     *
     * @author ClaudeCode
     */
    private MonsterBase base;
    /**
     * The verb used when the slay triggers in melee.
     *
     * @author ClaudeCode
     */
    private String meleeVerb;
    /**
     * The verb used when the slay triggers at range.
     *
     * @author ClaudeCode
     */
    private String rangedVerb;
    /**
     * The race flag identifying eligible targets.
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag raceFlag;
    /**
     * Damage multiplier in the standard combat system.
     *
     * @author ClaudeCode
     */
    private int multiplier;
    /**
     * Damage multiplier in the O-combat system.
     *
     * @author ClaudeCode
     */
    private int oMultiplier;
    /**
     * The slay's power rating (for item valuation).
     *
     * @author ClaudeCode
     */
    private int power;

    /**
     * Build a slay from its parsed data-file fields, splitting the monster type
     * and level out of {@code code}.
     *
     * @param code        slay code (type + level, e.g. {@code EVIL_2})
     * @param name        display name
     * @param base        targeted monster base, if any
     * @param meleeVerb   melee trigger verb
     * @param rangedVerb  ranged trigger verb
     * @param raceFlag    eligible-target race flag
     * @param multiplier  standard damage multiplier
     * @param oMultiplier O-combat damage multiplier
     * @param power       power rating
     * @author ClaudeCode
     */
    public Slay(String code, String name, MonsterBase base, String meleeVerb, String rangedVerb,
                MonsterRaceFlag raceFlag, int multiplier, int oMultiplier, int power) {
        this.code = code;
        String[] splits = this.code.split("_");
        this.monsterType = MonsterRaceFlag.valueOf("RF_" + splits[0]);
        this.monsterLevel = Integer.parseInt(splits[1]);
        this.name = name;
        this.base = base;
        this.meleeVerb = meleeVerb;
        this.rangedVerb = rangedVerb;
        this.raceFlag = raceFlag;
        this.multiplier = multiplier;
        this.oMultiplier = oMultiplier;
        this.power = power;
    }

    /**
     * @return the slay's code
     * @author ClaudeCode
     */
    public String getCode() {
        return code;
    }

    /**
     * @return a debug string listing this slay's fields
     * @author ClaudeCode
     */
    @Override
    public String toString() {
        return "Slay{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", base=" + base +
                ", meleeVerb='" + meleeVerb + '\'' +
                ", rangedVerb='" + rangedVerb + '\'' +
                ", raceFlag=" + raceFlag +
                ", multiplier=" + multiplier +
                ", oMultiplier=" + oMultiplier +
                ", power=" + power +
                '}';
    }
}