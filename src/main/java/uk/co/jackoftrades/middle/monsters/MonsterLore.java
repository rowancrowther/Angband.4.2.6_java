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

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.util.List;
import java.util.Map;

public class MonsterLore {
    private int sightings;
    private int deaths;
    private int pkills;
    private int thefts;
    private int tkills;
    private int wake;
    private int ignore;
    private int dropGold;
    private int dropItem;
    private int caseInnate;
    private int castSpell;

    private MonsterBase monsterBase;
    private List<MonsterBlow> blows;
    private Flag<MonsterRaceFlag> flags;
    private Flag<MonsterSpell> spellFlags;

    private List<MonsterDrop> drops;
    private List<MonsterFriends> friends;
    private List<MonsterFriendsBase> friendsBase;
    private List<MonsterMimic> mimicKinds;

    private Map<MonsterBlow, Integer> timeBlowsSeen;

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
}