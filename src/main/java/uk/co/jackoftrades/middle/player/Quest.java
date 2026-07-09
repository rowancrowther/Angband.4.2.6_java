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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.middle.monsters.MonsterRace;

/**
 * A quest the player can undertake — slay a target number of a specific monster race —
 * tracked from acceptance through to completion.
 *
 * <p>Ports the C {@code struct quest} ({@code quest.h}), defined by {@code quest.txt}. In
 * Angband 4.2 the quests are the fixed end-game boss objectives (Sauron and Morgoth): each
 * names the monster {@link #race} to be killed, the dungeon {@link #level} it occupies, and a
 * {@link #currentNumber}/{@link #maxNumber} tally recording kill progress.
 *
 * <p><b>Status:</b> currently a field-only stub; accessors and quest-progress logic are still
 * to be ported.
 *
 * @author Rowan Crowther
 */
public class Quest {
    /**
     * Stable quest index/identifier (C: {@code quest.index}).
     */
    private int index;
    /** Display name of the quest. */
    private String name;
    /** Dungeon depth at which the quest target is found. */
    private int level;
    /** The monster race that must be killed to complete the quest. */
    private MonsterRace race;
    /** Number of the target killed so far. */
    private int currentNumber;
    /** Number that must be killed for completion. */
    private int maxNumber;
}
