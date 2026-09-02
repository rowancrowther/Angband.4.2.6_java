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

import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

/**
 * A weapon slay (as loaded from {@code slay.txt}) — bonus damage against a class
 * of monster, with the targeted monster type/level, the melee and ranged verbs,
 * the damage multipliers (standard and O-combat) and a power rating. The
 * {@code code} encodes the monster type and level (e.g. {@code EVIL_2}), which
 * the constructor splits out. This is the Java port of the C original's
 * {@code struct slay} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class Slay {
    /**
     * The slay's code, encoding monster type and level (e.g. {@code EVIL_2}).
     */
    private String code;
    /**
     * The monster race flag this slay targets (parsed from {@link #code}).
     */
    private MonsterRaceFlag monsterType;
    /**
     * The monster level component parsed from {@link #code}.
     */
    private int monsterLevel;
    /**
     * The slay's display name.
     */
    private String name;
    /**
     * A specific monster base targeted by the slay, if any.
     */
    private MonsterBase base;
    /**
     * The verb used when the slay triggers in melee.
     */
    private String meleeVerb;
    /**
     * The verb used when the slay triggers at range.
     */
    private String rangedVerb;
    /**
     * The race flag identifying eligible targets.
     */
    private MonsterRaceFlag raceFlag;
    /**
     * Damage multiplier in the standard combat system.
     */
    private int multiplier;
    /**
     * Damage multiplier in the O-combat system.
     */
    private int oMultiplier;
    /**
     * The slay's power rating (for item valuation).
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
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the slay's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return a debug string listing this slay's fields
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

    /**
     * Whether this slay and another kill exactly the same monsters — that is, whether they target
     * the same race flag and the same monster base. Ports C's {@code same_monsters_slain}
     * ({@code src/obj-slays.c}).
     *
     * <p>Note that the name is deliberately not compared. Two slays with different names and
     * different multipliers are "the same" by this test if they pick out the same monsters, which
     * is what makes it the right grouping for runes: the player learns which monsters a weapon is
     * good against, not how good it is against them. This is why slay runes group by this method
     * where brand runes group by name.
     *
     * <p>The base comparison is by identity, which is correct while bases are interned by the
     * registry — and moot in 4.2.6's data, where no slay declares a base at all and every
     * comparison is {@code null} against {@code null}.
     *
     * @param other the slay to compare against
     * @return {@code true} if both slays kill the same monsters
     */
    public boolean sameMonsterSlain(Slay other) {
        if (this.raceFlag != other.raceFlag) return false;
        return this.base == other.base;
    }

    /**
     * @return the damage multiplier in the standard combat system. Three or less is an ordinary
     * slay; more than three makes it a kill, which the power code counts separately
     */
    public int getMultiplier() {
        return multiplier;
    }

    /**
     * @return this slay's power rating - the figure {@code ItemObject.slayPower} takes the best of
     * across an object's brands and slays
     */
    public int getPower() {
        return power;
    }
}