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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;

public class MonsterFriends {
    private String name;
    private MonsterRace race;
    private MonsterGroupRole role;
    private int percentChance;
    private int numberDice;
    private int numberSides;

    public MonsterFriends(String name, MonsterGroupRole role, int percentChance, int numberDice,
                          int numberSides) {
        this.name = name;
        this.role = role;
        this.percentChance = percentChance;
        this.numberDice = numberDice;
        this.numberSides = numberSides;
    }

    public void setRace(MonsterRace race) {
        this.race = race;
    }
}
