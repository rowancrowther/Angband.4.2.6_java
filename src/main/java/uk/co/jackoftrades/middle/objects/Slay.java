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

public class Slay {
    private String code;
    private MonsterRaceFlag monsterType;
    private int monsterLevel;
    private String name;
    private MonsterBase base;
    private String meleeVerb;
    private String rangedVerb;
    private MonsterRaceFlag raceFlag;
    private int multiplier;
    private int oMultiplier;
    private int power;

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

    public String getCode() {
        return code;
    }

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