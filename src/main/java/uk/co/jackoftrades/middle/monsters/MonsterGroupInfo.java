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
 * A monster's membership info for its group: the group index it belongs to and
 * the {@link MonsterGroupRole} it plays. This is the Java port of the C
 * original's {@code struct monster_group_info} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterGroupInfo {
    /**
     * The index of the group this monster belongs to.
     *
     * @author Rowan Crowther
     */
    private int index;
    /**
     * The role this monster plays in its group.
     *
     * @author Rowan Crowther
     */
    private MonsterGroupRole role;
}
