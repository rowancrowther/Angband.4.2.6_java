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

package uk.co.jackoftrades.middle.monsters;

import uk.co.jackoftrades.middle.monsters.enums.MonsterGroupRole;

/**
 * Like {@link MonsterFriends}, but the companions are chosen by monster
 * <em>base</em> type rather than a specific race — a {@link MonsterBase} that may
 * be generated alongside the monster, with role, chance and a dice count. This is
 * the Java port of the C original's {@code struct monster_friends_base}
 * ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterFriendsBase {
    /**
     * The companion monster base type.
     *
     * @author Rowan Crowther
     */
    private MonsterBase base;
    /**
     * The group role the companions take.
     *
     * @author Rowan Crowther
     */
    private MonsterGroupRole role;
    /**
     * Percentage chance the companions appear.
     *
     * @author Rowan Crowther
     */
    private int percentChance;
    /**
     * Number of dice for the companion count.
     *
     * @author Rowan Crowther
     */
    private int numberDice;
    /**
     * Sides per die for the companion count.
     *
     * @author Rowan Crowther
     */
    private int numberSides;

    /**
     * Build a by-base friends entry.
     *
     * @param base          companion monster base type
     * @param role          group role of the companions
     * @param percentChance appearance chance
     * @param numberDice    dice for the count
     * @param numberSides   sides per die for the count
     * @author Rowan Crowther
     */
    public MonsterFriendsBase(MonsterBase base, MonsterGroupRole role, int percentChance, int numberDice,
                              int numberSides) {
        this.base = base;
        this.role = role;
        this.percentChance = percentChance;
        this.numberDice = numberDice;
        this.numberSides = numberSides;
    }
}
