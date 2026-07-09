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

package uk.co.jackoftrades.middle.cave;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.frontend.colour.enums.ColourType;
import uk.co.jackoftrades.middle.cave.enums.PitRoomType;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.monsters.enums.MonsterSpell;

import java.util.List;

/**
 * The recipe for a themed monster pit or nest (as loaded from {@code pit.txt}):
 * which monsters may appear (by allowed/forbidden race flags, spell flags and
 * base types), how deep/rare it is, and the colours used for its theme. The level
 * generator uses this to select and populate pit/nest rooms. This is the Java
 * port of the C original's {@code struct pit_profile} ({@code src/mon-make.h}).
 *
 * @author Rowan Crowther
 */
public class PitProfile {
    /**
     * The pit profile's name.
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Whether this profile describes a pit, a nest or another room type.
     *
     * @author Rowan Crowther
     */
    private PitRoomType roomType;
    /**
     * Average native depth of this pit (used in depth/rarity selection).
     *
     * @author Rowan Crowther
     */
    private int ave;
    /**
     * Rarity weighting controlling how often this profile is chosen.
     *
     * @author Rowan Crowther
     */
    private int rarity;
    /**
     * Rarity of objects generated within the pit.
     *
     * @author Rowan Crowther
     */
    private int objectRarity;
    /**
     * Race flags a monster must have to belong in this pit.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * Race flags that exclude a monster from this pit.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterRaceFlag> forbiddenFlags;
    /**
     * Required innate-attack frequency for eligible monsters.
     *
     * @author Rowan Crowther
     */
    private int freqInnate;
    /**
     * Spell flags a monster must have to belong in this pit.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterSpell> spellsFlags;
    /**
     * Spell flags that exclude a monster from this pit.
     *
     * @author Rowan Crowther
     */
    private Flag<MonsterSpell> forbiddenSpellFlags;
    /**
     * Monster base types allowed in this pit.
     *
     * @author Rowan Crowther
     */
    private List<MonsterBase> bases;
    /**
     * Theme colours associated with this pit.
     *
     * @author Rowan Crowther
     */
    private List<ColourType> colours;
    /**
     * Specific monster races explicitly forbidden from this pit.
     *
     * @author Rowan Crowther
     */
    private List<MonsterRace> forbiddenMonsters;

    /**
     * Build a pit profile from its parsed data-file fields.
     *
     * @param name                the profile name
     * @param roomType            pit/nest/other room type
     * @param ave                 average native depth
     * @param rarity              rarity weighting
     * @param objectRarity        rarity of objects within
     * @param flags               required race flags
     * @param forbiddenFlags      excluding race flags
     * @param freqInnate          required innate-attack frequency
     * @param spellsFlags         required spell flags
     * @param forbiddenSpellFlags excluding spell flags
     * @param bases               allowed monster base types
     * @param colours             theme colours
     * @param forbiddenMonsters   explicitly forbidden races
     * @author Rowan Crowther
     */
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
