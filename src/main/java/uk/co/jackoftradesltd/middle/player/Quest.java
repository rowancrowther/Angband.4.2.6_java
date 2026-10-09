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
 * <p>Ports the C {@code struct quest} ({@code player.h}), whose standard records are defined by
 * {@code quest.txt}. In Angband 4.2 the quests are the fixed end-game boss objectives (Sauron and
 * Morgoth): each names the monster {@link #race} to be killed, the dungeon {@link #level} it
 * occupies, and a {@link #currentNumber}/{@link #maxNumber} tally recording kill progress.
 *
 * <p><b>One class, two roles.</b> The same type serves as the shared, never-modified template
 * loaded from {@code quest.txt} (see {@code QuestReader}/{@code QuestAssembler}, held in
 * {@link uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry#getQuests}) and as each
 * character's own history entry, made from a template by {@link #copy()} at birth and kept in
 * {@link Player#getQuests}. C does the same with one struct. Only the per-character entries are ever
 * meant to change, and a finished quest is recorded by zeroing its {@link #level}.
 *
 * <p><b>Not carried over:</b> C's {@code next} link, which only chains the records together while
 * {@code quest.txt} is being parsed; the port keeps them in a {@link java.util.List}. C's
 * {@code index} and {@code level} are {@code uint8_t}, so a value above 255 would wrap there; the
 * port holds plain {@code int}s, and the shipped data (levels 99 and 100) never gets near that.
 *
 * <p><b>Status:</b> the accessors, {@link #copy()} and the {@code quest.txt} load are ported, and
 * {@link PlayerQuest#isQuest} and {@link PlayerQuest#playerQuestsReset} use them. The class has no
 * setters yet: the only C code that changes a quest after birth is {@code quest_check}, which bumps
 * {@code cur_num} and zeroes {@code level}, and it is not ported.
 *
 * <p>Class Quest coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class Quest {
    /**
     * Position of this quest in the shared standard list (C: {@code quest.index}). The assembler
     * numbers the loaded templates contiguously from 0 in file order; a per-character copy made by
     * {@link #copy()} has it zeroed, as C's freshly allocated history array does.
     *
     * <p>Field index coded before 261009, commented in full on 261009.
     */
    private int index;
    /**
     * Display name of the quest (C: {@code quest.name}), e.g. {@code Sauron}.
     *
     * <p>Field name coded before 261009, commented in full on 261009.
     */
    private String name;
    /**
     * Dungeon depth at which the quest target is found (C: {@code quest.level}). On a character's own
     * entry this is set to zero when the quest is completed, so {@link PlayerQuest#isQuest} stops
     * matching it; the zero is the town's depth, which is why that method guards the town first.
     *
     * <p>Field level coded before 261009, commented in full on 261009.
     */
    private int level;
    /**
     * The monster race that must be killed to complete the quest (C: {@code quest.race}). Shared
     * between the template and every character's copy, never duplicated: C's {@code quest_check}
     * compares it by identity with the race of the monster just killed. See {@link #copy()}.
     *
     * <p>Field race coded before 261009, commented in full on 261009.
     */
    private MonsterRace race;
    /**
     * Number of the target killed so far; starts at 0 (C: {@code quest.cur_num}). C's comment calls it
     * "unused", but {@code quest_check} increments it and compares it with {@code max_num}.
     *
     * <p>Field currentNumber coded before 261009, commented in full on 261009.
     */
    private int currentNumber;
    /**
     * Number that must be killed for completion (C: {@code quest.max_num}); 1 for both quests in the
     * shipped {@code quest.txt}. Like {@code cur_num}, C marks it "unused" yet {@code quest_check}
     * reads it.
     *
     * <p>Field maxNumber coded before 261009, commented in full on 261009.
     */
    private int maxNumber;

    /**
     * Construct a fully-resolved quest, as produced by {@code QuestAssembler} from one
     * {@code quest.txt} record, or by {@link #copy()} for a character's own entry.
     *
     * <p>Constructor Quest coded before 261009, commented in full on 261009.
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
     * Reads {@link #index}: this quest's position in the shared standard list, or 0 on a
     * character's own copy.
     *
     * <p>Function getIndex coded before 261009, commented in full on 261009.
     *
     * @return this quest's stable index/identifier
     */
    public int getIndex() {
        return index;
    }

    /**
     * Reads {@link #name}.
     *
     * <p>Function getName coded before 261009, commented in full on 261009.
     *
     * @return this quest's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Reads {@link #level}. On a character's own entry, 0 means the quest has been completed.
     *
     * <p>Function getLevel coded before 261009, commented in full on 261009.
     *
     * @return the dungeon depth at which the target is found
     */
    public int getLevel() {
        return level;
    }

    /**
     * Reads {@link #race}, the shared reference, not a copy.
     *
     * <p>Function getRace coded before 261009, commented in full on 261009.
     *
     * @return the monster race that must be killed to complete the quest
     */
    public MonsterRace getRace() {
        return race;
    }

    /**
     * Reads {@link #currentNumber}.
     *
     * <p>Function getCurrentNumber coded before 261009, commented in full on 261009.
     *
     * @return the number of the target killed so far
     */
    public int getCurrentNumber() {
        return currentNumber;
    }

    /**
     * Reads {@link #maxNumber}.
     *
     * <p>Function getMaxNumber coded before 261009, commented in full on 261009.
     *
     * @return the number that must be killed for completion
     */
    public int getMaxNumber() {
        return maxNumber;
    }

    /**
     * Builds this quest's per-character copy — the port of the four field assignments inside C's
     * {@code player_quests_reset} loop ({@code player-quest.c}) that fill one entry of
     * {@code p->quests[i]} from the matching entry of the shared {@code quests[]} array.
     *
     * <p><b>{@link #race} is shared, not copied.</b> C writes {@code p->quests[i].race = quests[i].race;}
     * — a bare pointer copy, so the player's quest and the shared template point at the same
     * {@code struct monster_race}. This matters beyond birth: the still-unported {@code quest_check}
     * credits a kill with {@code m->race == p->quests[i].race} ({@code player-quest.c}), an
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
     * <p>Function copy coded on 260903, commented in full on 261009.
     *
     * @return a fresh {@link Quest} for one character's quest history: independent of this quest and
     * of the shared standard set for every field except {@link #race}, which both continue to share
     */
    public Quest copy() {
        return new Quest(0, name, level, race, 0, maxNumber);
    }
}