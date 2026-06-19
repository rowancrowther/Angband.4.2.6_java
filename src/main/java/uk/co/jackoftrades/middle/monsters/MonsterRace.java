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

import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftrades.backend.strings.AngbandDisplayCharacter;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.VisualsColourCyclesByRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;
import uk.co.jackoftrades.middle.objects.ObjectKind;

import java.util.List;

public class MonsterRace {
    private String name;
    private String text;
    private String plural;

    private MonsterBase base;

    private int averageHP;

    private int ac;

    private int sleep;
    private int hearing;
    private int smell;
    private int speed;
    private int light;

    private int mexp;

    private int freqInnate;
    private int freqSpell;
    private int spellPower;

    private Flag<MonsterRaceFlag> flags;
    private Flag<MonsterRaceFlag> flagsOff;
    private List<MonsterSpell> spells;

    private List<MonsterBlow> blow;

    private int level;
    private int rarity;

    private AngbandDisplayCharacter adc;

    private int maxNum;
    private int curNum;

    private List<MonsterAltmsg> spellMessages;
    private List<MonsterDrop> drops;

    private List<MonsterFriends> friends;
    private List<MonsterFriendsBase> friendsBase;

    private List<ObjectKind> mimicKinds;

    private List<String> shapes;
    private int numShapes;

    public MonsterRace(String name, String text, String plural, MonsterBase base, int averageHP, int ac, int sleep,
                       int hearing, int smell, int speed, int light, int mexp, int freqInnate, int freqSpell,
                       int spellPower, Flag<MonsterRaceFlag> flags, Flag<MonsterRaceFlag> flagsOff,
                       List<MonsterSpell> spells, List<MonsterBlow> blow, int level, int rarity,
                       AngbandDisplayCharacter adc, int maxNum, int curNum, List<MonsterAltmsg> spellMessages,
                       List<MonsterDrop> drops, List<MonsterFriends> friends, List<MonsterFriendsBase> friendsBase,
                       List<ObjectKind> mimicKinds, List<String> shapes, int numShapes,
                       String groupName, String cycleName) {
        this.name = name;
        this.text = text;
        this.plural = plural;
        this.base = base;
        this.averageHP = averageHP;
        this.ac = ac;
        this.sleep = sleep;
        this.hearing = hearing;
        this.smell = smell;
        this.speed = speed;
        this.light = light;
        this.mexp = mexp;
        this.freqInnate = freqInnate;
        this.freqSpell = freqSpell;
        this.spellPower = spellPower;
        this.flags = flags;
        this.flagsOff = flagsOff;
        this.spells = spells;
        this.blow = blow;
        this.level = level;
        this.rarity = rarity;
        this.adc = adc;
        this.maxNum = maxNum;
        this.curNum = curNum;
        this.spellMessages = spellMessages;
        this.drops = drops;
        this.friends = friends;
        this.friendsBase = friendsBase;
        this.mimicKinds = mimicKinds;
        this.shapes = shapes;
        this.numShapes = numShapes;
        VisualsColourCyclesByRace.setCycleForRace(this, groupName, cycleName);
    }

    @TestOnly
    public MonsterRace() {
        flags = new Flag<>(MonsterRaceFlag.class);
    }

    public Flag<MonsterRaceFlag> getFlags() {
        return flags;
    }
}