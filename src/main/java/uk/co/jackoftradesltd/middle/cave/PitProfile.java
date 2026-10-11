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

package uk.co.jackoftradesltd.middle.cave;

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.PitRoomType;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterSpell;

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
     */
    private String name;
    /**
     * Whether this profile describes a pit, a nest or another room type.
     */
    private PitRoomType roomType;
    /**
     * Average native depth of this pit (used in depth/rarity selection).
     */
    private int ave;
    /**
     * Rarity weighting controlling how often this profile is chosen.
     */
    private int rarity;
    /**
     * Rarity of objects generated within the pit.
     */
    private int objectRarity;
    /**
     * Race flags a monster must have to belong in this pit.
     */
    private Flag<MonsterRaceFlag> flags;
    /**
     * Race flags that exclude a monster from this pit.
     */
    private Flag<MonsterRaceFlag> forbiddenFlags;
    /**
     * Required innate-attack frequency for eligible monsters, held as {@code 100 / pct} where
     * {@code pct} is the percentage on the {@code innate-freq:} line of {@code pit.txt}. This is the
     * same convention as a race's {@code freqInnate}, so the two compare directly: a race is
     * rejected for the pit when its {@code freqInnate} is lower than this value. A pit with no
     * {@code innate-freq:} line holds 0, which no race falls below.
     *
     * <p>Field freqInnate coded before 261011, commented in full on 261011, updated on 261011 to say
     * it holds {@code 100 / pct}.
     */
    private int freqInnate;
    /**
     * Spell flags a monster must have to belong in this pit.
     */
    private Flag<MonsterSpell> spellsFlags;
    /**
     * Spell flags that exclude a monster from this pit.
     */
    private Flag<MonsterSpell> forbiddenSpellFlags;
    /**
     * Monster base types allowed in this pit.
     */
    private List<MonsterBase> bases;
    /**
     * Theme colours associated with this pit.
     */
    private List<ColourEnum> colours;
    /**
     * Specific monster races explicitly forbidden from this pit.
     */
    private List<MonsterRace> forbiddenMonsters;

    /**
     * Build a pit profile from its parsed data-file fields. The values are stored as given, so the
     * caller does the conversions C's pit parser does: {@code freqInnate} must already be
     * {@code 100 / pct}, not the percentage as written in the data file.
     *
     * <p>Function PitProfile coded before 261011, commented in full on 261011, updated on 261011 to
     * document the {@code freqInnate} convention.
     *
     * @param name               the profile name
     * @param roomType            pit/nest/other room type
     * @param ave                 average native depth
     * @param rarity              rarity weighting
     * @param objectRarity        rarity of objects within
     * @param flags               required race flags
     * @param forbiddenFlags      excluding race flags
     * @param freqInnate          required innate-attack frequency, as {@code 100 / pct} (0 if the pit has none)
     * @param spellsFlags         required spell flags
     * @param forbiddenSpellFlags excluding spell flags
     * @param bases               allowed monster base types
     * @param colours             theme colours
     * @param forbiddenMonsters   explicitly forbidden races
     */
    public PitProfile(String name, PitRoomType roomType, int ave, int rarity, int objectRarity,
                      Flag<MonsterRaceFlag> flags, Flag<MonsterRaceFlag> forbiddenFlags, int freqInnate,
                      Flag<MonsterSpell> spellsFlags, Flag<MonsterSpell> forbiddenSpellFlags, List<MonsterBase> bases,
                      List<ColourEnum> colours, List<MonsterRace> forbiddenMonsters) {
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
