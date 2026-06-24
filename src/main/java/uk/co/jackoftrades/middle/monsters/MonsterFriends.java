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

/**
 * A "friends" entry for a monster race: another specific {@link MonsterRace} that
 * may be generated alongside it, with the group role they take, the chance they
 * appear, and a dice expression for how many. This is the Java port of the C
 * original's {@code struct monster_friends} ({@code src/monster.h}).
 *
 * @author ClaudeCode
 */
public class MonsterFriends {
    /**
     * The name of the companion race (resolved to {@link #race} after loading).
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The resolved companion race.
     *
     * @author ClaudeCode
     */
    private MonsterRace race;
    /**
     * The group role the companions take.
     *
     * @author ClaudeCode
     */
    private MonsterGroupRole role;
    /**
     * Percentage chance the companions appear.
     *
     * @author ClaudeCode
     */
    private int percentChance;
    /**
     * Number of dice for the companion count.
     *
     * @author ClaudeCode
     */
    private int numberDice;
    /**
     * Sides per die for the companion count.
     *
     * @author ClaudeCode
     */
    private int numberSides;

    /**
     * Build a friends entry (the race is resolved later from {@link #name}).
     *
     * @param name          companion race name
     * @param role          group role of the companions
     * @param percentChance appearance chance
     * @param numberDice    dice for the count
     * @param numberSides   sides per die for the count
     * @author ClaudeCode
     */
    public MonsterFriends(String name, MonsterGroupRole role, int percentChance, int numberDice,
                          int numberSides) {
        this.name = name;
        this.role = role;
        this.percentChance = percentChance;
        this.numberDice = numberDice;
        this.numberSides = numberSides;
    }

    /**
     * Set the resolved companion race.
     *
     * @param race the companion race
     * @author ClaudeCode
     */
    public void setRace(MonsterRace race) {
        this.race = race;
    }
}
