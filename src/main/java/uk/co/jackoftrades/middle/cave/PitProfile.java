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

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.util.List;

public class PitProfile {
    private String name;
    private PitRoomType roomType;
    private int ave;
    private int rarity;
    private int objectRarity;
    private Flag<MonsterRaceFlag> flags;
    private Flag<MonsterRaceFlag> forbiddenFlags;
    private int freqInnate;
    private Flag<MonsterSpell> spellsFlags;
    private Flag<MonsterSpell> forbiddenSpellFlags;
    private List<MonsterBase> bases;
    private List<ColourType> colours;
    private List<MonsterRace> forbiddenMonsters;

    public PitProfile(String name, PitRoomType roomType, int ave, int rarity, int objectRarity,
                      Flag<MonsterRaceFlag> flags, Flag<MonsterRaceFlag> forbiddenFlags, int freqInnate,
                      Flag<MonsterSpell> spellsFlags, Flag<MonsterSpell> forbiddenSpellFlags, List<MonsterBase> bases,
                      List<ColourType> colours, List<MonsterRace> forbiddenMonsters) {
        this.name = name;
        this.roomType = roomType;
        this.ave = ave;
        this.rarity = rarity;
        this.objectRarity = objectRarity;
        this.flags = flags;
        this.forbiddenFlags = forbiddenFlags;
        this.freqInnate = freqInnate;
        this.spellsFlags = spellsFlags;
        this.forbiddenSpellFlags = forbiddenSpellFlags;
        this.bases = bases;
        this.colours = colours;
        this.forbiddenMonsters = forbiddenMonsters;
    }
}
