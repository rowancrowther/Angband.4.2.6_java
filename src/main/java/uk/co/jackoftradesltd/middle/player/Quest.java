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

package uk.co.jackoftradesltd.middle.player;

import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

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

    /**
     * Builds this quest's per-character copy — the port of the four field assignments inside C's
     * {@code player_quests_reset} loop ({@code player-quest.c:166-171}) that fill one entry of
     * {@code p->quests[i]} from the matching entry of the shared {@code quests[]} array.
     *
     * <p><b>{@link #race} is shared, not copied.</b> C writes {@code p->quests[i].race = quests[i].race;}
     * — a bare pointer copy, so the player's quest and the shared template point at the same
     * {@code struct monster_race}. This matters beyond birth: the still-unported {@code quest_check}
     * credits a kill with {@code m->race == p->quests[i].race} ({@code player-quest.c:229}), an
     * identity test against the race of the monster just killed. A deep copy here would give every
     * player's quest its own {@link uk.co.jackoftradesltd.middle.monsters.MonsterRace} instance, and
     * that test could then never succeed, however faithfully everything else was ported. So this
     * carries the reference across untouched, exactly as C carries the pointer.
     *
     * <p><b>{@link #index} and {@link #currentNumber} come back zero, not copied.</b> C's array is
     * {@code mem_zalloc}'d before the loop runs, and the loop body only ever assigns {@code name},
     * {@code level}, {@code race} and {@code max_num} — {@code index} and {@code cur_num} are left at
     * the zero the allocation gave them. This quest's own {@link #index} and {@link #currentNumber}
     * are therefore deliberately not read here, whatever they currently hold.
     *
     * <p>{@link #name}, {@link #level} and {@link #maxNumber} are copied by value, matching C's
     * {@code string_make}, {@code level} and {@code max_num} assignments respectively; a Java
     * {@code String} needs no explicit duplication to get the same independence C's fresh allocation
     * gives {@code name}.
     *
     * <p>Function copy coded on 260903, commented in full on 260903.
     *
     * @return a fresh {@link Quest} for one character's quest history: independent of this quest and
     * of the shared standard set for every field except {@link #race}, which both continue to share
     */
    public Quest copy() {
        return new Quest(0, name, level, race, 0, maxNumber);
    }
}