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

import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;

/**
 * A weapon/ammo brand (as loaded from {@code brand.txt}) — extra elemental damage
 * a weapon deals, with the damage verb, the monster flags that resist or are
 * vulnerable to it, the damage multipliers (standard and O-combat) and a power
 * rating. This is the Java port of the C original's {@code struct brand}
 * ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class Brand {
    /**
     * The brand's code (used for cross-references).
     *
     * @author ClaudeCode
     */
    private String code;
    /**
     * The brand's display name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The verb describing the brand's damage (e.g. "burns").
     *
     * @author ClaudeCode
     */
    private String verb;
    /**
     * Monster flag granting resistance to this brand.
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag resistFlag;
    /**
     * Monster flag marking vulnerability to this brand.
     *
     * @author ClaudeCode
     */
    private MonsterRaceFlag vulnerableFlag;

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
     * The brand's power rating (for item valuation).
     *
     * @author ClaudeCode
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
     * @author ClaudeCode
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
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }

    /**
     * @return the brand's code
     * @author ClaudeCode
     */
    public String getCode() {
        return code;
    }

    /**
     * @return a debug string listing this brand's fields
     * @author ClaudeCode
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
}