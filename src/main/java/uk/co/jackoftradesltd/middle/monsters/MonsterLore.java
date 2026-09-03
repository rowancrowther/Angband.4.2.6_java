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

package uk.co.jackoftradesltd.middle.monsters;

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

import java.util.List;
import java.util.Map;

/**
 * The player's accumulated knowledge ("lore") about a monster race — what has
 * been observed through play and is therefore shown in the monster recall: kill
 * and sighting counts, observed drops/spells/blows, and the subset of flags the
 * player has learned. This is the Java port of the C original's
 * {@code struct monster_lore} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterLore {
    /**
     * Number of times the monster has been seen.
     */
    private int sightings;
    /**
     * Number of times this monster has killed the player.
     */
    private int deaths;
    /**
     * Number of player kills of this monster (this life).
     */
    private int pkills;
    /**
     * Number of times the monster has stolen from the player.
     */
    private int thefts;
    /**
     * Total kills of this monster across all lives.
     */
    private int tkills;
    /**
     * Observed wake-up behaviour counter.
     */
    private int wake;
    /**
     * Observed "ignore the player" behaviour counter.
     */
    private int ignore;
    /**
     * Number of times observed dropping gold.
     */
    private int dropGold;
    /**
     * Number of times observed dropping items.
     */
    private int dropItem;
    /**
     * Number of innate attacks observed.
     */
    private int caseInnate;
    /**
     * Number of spells observed cast.
     */
    private int castSpell;

    /**
     * The base type the lore relates to.
     */
    private MonsterBase monsterBase;
    /**
     * The blows the player has observed.
     */
    private List<MonsterBlow> blows;
    /**
     * The race flags the player has learned.
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * The spell flags the player has learned.
     */
    private Flag<MonsterSpell> spellFlags;

    /**
     * Drops the player has observed.
     */
    private List<MonsterDrop> drops;
    /**
     * Companion races the player has observed.
     */
    private List<MonsterFriends> friends;
    /**
     * Companion base types the player has observed.
     */
    private List<MonsterFriendsBase> friendsBase;
    /**
     * Mimic kinds the player has observed.
     */
    private List<MonsterMimic> mimicKinds;

    /**
     * How many times each blow has been observed (drives blow-damage lore).
     */
    private Map<MonsterBlow, Integer> timeBlowsSeen;

    /**
     * Build a lore record from its parsed/observed fields.
     *
     * @param sightings     times seen
     * @param deaths        times it killed the player
     * @param pkills        player kills this life
     * @param thefts        times it stole
     * @param tkills        total kills across lives
     * @param wake          wake-behaviour counter
     * @param ignore        ignore-behaviour counter
     * @param dropGold      gold-drop observations
     * @param dropItem      item-drop observations
     * @param caseInnate    innate-attack observations
     * @param castSpell     spell-cast observations
     * @param flags         learned race flags
     * @param spellFlags    learned spell flags
     * @param drops         observed drops
     * @param friends       observed companion races
     * @param friendsBase   observed companion base types
     * @param mimicKinds    observed mimic kinds
     * @param timeBlowsSeen per-blow observation counts
     * @param monsterBase   the related base type
     */
    public MonsterLore(int sightings, int deaths, int pkills, int thefts, int tkills, int wake, int ignore,
                       int dropGold, int dropItem, int caseInnate, int castSpell,
                       Flag<MonsterRaceFlag> flags, Flag<MonsterSpell> spellFlags, List<MonsterDrop> drops,
                       List<MonsterFriends> friends, List<MonsterFriendsBase> friendsBase,
                       List<MonsterMimic> mimicKinds, Map<MonsterBlow, Integer> timeBlowsSeen,
                       MonsterBase monsterBase) {
        this.sightings = sightings;
        this.deaths = deaths;
        this.pkills = pkills;
        this.thefts = thefts;
        this.tkills = tkills;
        this.wake = wake;
        this.ignore = ignore;
        this.dropGold = dropGold;
        this.dropItem = dropItem;
        this.caseInnate = caseInnate;
        this.castSpell = castSpell;
        this.flags = flags;
        this.spellFlags = spellFlags;
        this.drops = drops;
        this.friends = friends;
        this.friendsBase = friendsBase;
        this.mimicKinds = mimicKinds;
        this.timeBlowsSeen = timeBlowsSeen;
        this.monsterBase = monsterBase;
    }

    /**
     * Sets the number of this monster the player has killed this life, C's {@code lore->pkills}. C
     * only ever writes this field directly (never derives it), and this setter does the same, with
     * no range checking even though C declares the field as an unsigned 16-bit count.
     *
     * <p>Function setPSkills coded on 260903, commented in full on 260903.
     *
     * @param pSkills the new player-kill count for this monster
     */
    public void setPSkills(int pSkills) {
        this.pkills = pSkills;
    }

    /**
     * Sets the number of times this monster has stolen from the player this life, C's
     * {@code lore->thefts}. C only ever writes this field directly, and this setter does the same,
     * with no range checking even though C declares the field as an unsigned 16-bit count.
     *
     * <p>Function setThefts coded on 260903, commented in full on 260903.
     *
     * @param thefts the new theft count for this monster
     */
    public void setThefts(int thefts) {
        this.thefts = thefts;
    }
}