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
 * <p><b>Status:</b> loaded from {@code quest.txt} (see {@code QuestReader}/{@code QuestAssembler})
 * with the accessors below. The runtime quest-progress logic - crediting a kill to
 * {@link #currentNumber}, testing completion, and the C convention of zeroing {@link #level} once a
 * quest is done - is still to be ported.
 *
 * @author Rowan Crowther
 */
public class Quest {
    /**
     * Stable quest index/identifier (C: {@code quest.index}).
     */
    private int index;
    /**
     * Display name of the quest (C: {@code quest.name}).
     */
    private String name;
    /** Dungeon depth at which the quest target is found (C: {@code quest.level}). */
    private int level;
    /** The monster race that must be killed to complete the quest (C: {@code quest.race}). */
    private MonsterRace race;
    /** Number of the target killed so far; starts at 0 (C: {@code quest.cur_num}). */
    private int currentNumber;
    /** Number that must be killed for completion (C: {@code quest.max_num}). */
    private int maxNumber;

    /**
     * Construct a fully-resolved quest, as produced by {@code QuestAssembler} from one
     * {@code quest.txt} record.
     *
     * @param index         stable quest index (assembled contiguously in file order)
     * @param name          display name
     * @param level         dungeon depth of the target
     * @param race          the resolved target monster race
     * @param currentNumber kills credited so far (0 at load)
     * @param maxNumber     kills required for completion
     */
    public Quest(int index, String name, int level, MonsterRace race, int currentNumber, int maxNumber) {
        this.index = index;
        this.name = name;
        this.level = level;
        this.race = race;
        this.currentNumber = currentNumber;
        this.maxNumber = maxNumber;
    }

    /**
     * @return this quest's stable index/identifier
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return this quest's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the dungeon depth at which the target is found
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the monster race that must be killed to complete the quest
     */
    public MonsterRace getRace() {
        return race;
    }

    /**
     * @return the number of the target killed so far
     */
    public int getCurrentNumber() {
        return currentNumber;
    }

    /**
     * @return the number that must be killed for completion
     */
    public int getMaxNumber() {
        return maxNumber;
    }
}