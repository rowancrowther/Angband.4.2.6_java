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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

import java.util.Objects;

/**
 * A weapon/ammo brand (as loaded from {@code brand.txt}) — extra elemental damage
 * a weapon deals, with the damage verb, the monster flags that resist or are
 * vulnerable to it, the damage multipliers (standard and O-combat) and a power
 * rating. This is the Java port of the C original's {@code struct brand}
 * ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class Brand {
    /**
     * The brand's code (used for cross-references).
     */
    private String code;
    /**
     * The brand's display name.
     */
    private String name;
    /**
     * The verb describing the brand's damage (e.g. "burns").
     */
    private String verb;
    /**
     * Monster flag granting resistance to this brand.
     */
    private MonsterRaceFlag resistFlag;
    /**
     * Monster flag marking vulnerability to this brand.
     */
    private MonsterRaceFlag vulnerableFlag;

    /**
     * Damage multiplier in the standard combat system.
     */
    private int multiplier;
    /**
     * Damage multiplier in the O-combat system.
     */
    private int oMultiplier;
    /**
     * The brand's power rating (for item valuation).
     */
    private int power;

    /**
     * Build a brand from its parsed data-file fields.
     *
     * @param code           brand code
     * @param name           display name
     * @param verb           damage verb
     * @param resistFlag     resisting monster flag
     * @param vulnerableFlag vulnerable monster flag
     * @param multiplier     standard damage multiplier
     * @param oMultiplier    O-combat damage multiplier
     * @param power          power rating
     */
    public Brand(String code, String name, String verb, MonsterRaceFlag resistFlag, MonsterRaceFlag vulnerableFlag, int multiplier, int oMultiplier, int power) {
        this.code = code;
        this.name = name;
        this.verb = verb;
        this.resistFlag = resistFlag;
        this.vulnerableFlag = vulnerableFlag;
        this.multiplier = multiplier;
        this.oMultiplier = oMultiplier;
        this.power = power;
    }

    /**
     * @return the brand's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the brand's code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return a debug string listing this brand's fields
     */
    @Override
    public String toString() {
        return "Brand{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", verb='" + verb + '\'' +
                ", resistFlag=" + resistFlag +
                ", vulnerableFlag=" + vulnerableFlag +
                ", multiplier=" + multiplier +
                ", oMultiplier=" + oMultiplier +
                ", power=" + power +
                '}';
    }

    /**
     * Value equality over every field. Note this is stricter than the test used to group brands
     * into runes, which compares names alone — two brands can be the same property at different
     * strengths, and so share a rune, while remaining unequal here.
     *
     * @param o the object to compare against
     * @return {@code true} if {@code o} is a brand with identical fields
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Brand brand)) return false;
        return multiplier == brand.multiplier
                && oMultiplier == brand.oMultiplier
                && power == brand.power
                && Objects.equals(code, brand.code)
                && Objects.equals(name, brand.name)
                && Objects.equals(verb, brand.verb)
                && resistFlag == brand.resistFlag
                && vulnerableFlag == brand.vulnerableFlag;
    }

    /**
     * @return a hash consistent with {@link #equals}
     */
    @Override
    public int hashCode() {
        return Objects.hash(code, name, verb, resistFlag, vulnerableFlag, multiplier, oMultiplier, power);
    }
}