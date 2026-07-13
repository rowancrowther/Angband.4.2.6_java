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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.Map;

/**
 * A playable character race — Human, Half-Troll, High-Elf and so on — and the innate
 * modifiers it confers.
 *
 * <p>Ports the C {@code struct player_race} ({@code player.h}), defined by {@code p_race.txt}.
 * A race contributes to nearly every derived attribute at character creation and recalculation:
 * stat and skill adjustments, hit-dice and experience scaling, the physical ranges used to roll
 * age/height/weight, infravision radius, an equipment {@link PlayerBody}, innate object/player
 * flags, the background-history chart to roll on, and elemental resistance modifiers.
 *
 * <p><b>Why everything hangs off one immutable record:</b> race is fixed for a character's life
 * and combines with {@link PlayerClass} to produce the player's effective profile, so the port
 * keeps all of a race's contributions on a single value object — loaded once from data, then
 * only read.
 *
 * @author Rowan Crowther
 */
public class PlayerRace {
    /**
     * Logger for this type.
     */
    private static final Logger logger = LogManager.getLogger();

    /** Display name of the race, e.g. {@code "Half-Troll"} (C: {@code player_race.name}). */
    private String name;
    /** Stable race index/identifier (C: {@code player_race.ridx}). */
    private int rIndex;

    /** The race's share of maximum hit points (its hit-dice contribution). */
    private int raceMhp;
    /** Experience-point multiplier; a higher value means the race levels more slowly. */
    private int raceExp;

    /** Base starting age in years, before the random spread. */
    private int baseAge;
    /** Random spread added to {@link #baseAge} when rolling starting age. */
    private int modAge;

    /** Base height before the random spread. */
    private int baseHeight;
    /** Random spread added to {@link #baseHeight}. */
    private int modHeight;
    /** Base weight before the random spread. */
    private int baseWeight;
    /** Random spread added to {@link #baseWeight}. */
    private int modWeight;

    /** Infravision radius in grids — how far the race sees warm creatures in the dark. */
    private int infravision;

    /** The equipment-slot layout this race uses (see {@link PlayerBody}). */
    private PlayerBody body;

    /** Per-stat adjustments applied to a character of this race. */
    private Map<Stats, Integer> statsAdj;
    /** Per-skill adjustments applied to a character of this race. */
    private Map<PlayerSkill, Integer> skillsAdj;

    /** Innate object flags the race confers (e.g. sustain, free action). */
    private Flag<ObjectFlag> oFlags;
    /** Innate player flags the race confers (see {@link PlayerFlag}). */
    private Flag<PlayerFlag> pFlags;

    /** The background-history chart this race's biography generation starts from. */
    private PlayerHistoryChart history;

    /**
     * Resistance/element modifiers conferred by the race, keyed by {@link ElementEnum}.
     */
    private Map<ElementEnum, ElementInfo> resists;

    /**
     * Builds a race from its parsed attributes; each parameter populates the like-named field
     * (see those fields for detail).
     *
     * @param name        display name
     * @param rIndex      stable race index
     * @param raceMhp     hit-dice contribution
     * @param raceExp     experience multiplier
     * @param baseAge     base starting age
     * @param modAge      random age spread
     * @param baseHeight  base height
     * @param modHeight   random height spread
     * @param baseWeight  base weight
     * @param modWeight   random weight spread
     * @param infravision infravision radius
     * @param body        equipment-slot layout
     * @param statsAdj    per-stat adjustments
     * @param skillsAdj   per-skill adjustments
     * @param oFlags      innate object flags
     * @param pFlags      innate player flags
     * @param history     background-history starting chart
     * @param resists     resistance/element modifiers
     */
    public PlayerRace(String name, int rIndex, int raceMhp, int raceExp, int baseAge, int modAge, int baseHeight,
                      int modHeight, int baseWeight, int modWeight, int infravision, PlayerBody body,
                      Map<Stats, Integer> statsAdj, Map<PlayerSkill, Integer> skillsAdj, Flag<ObjectFlag> oFlags,
                      Flag<PlayerFlag> pFlags, PlayerHistoryChart history, Map<ElementEnum, ElementInfo> resists) {
        this.name = name;
        this.rIndex = rIndex;
        this.raceMhp = raceMhp;
        this.raceExp = raceExp;
        this.baseAge = baseAge;
        this.modAge = modAge;
        this.baseHeight = baseHeight;
        this.modHeight = modHeight;
        this.baseWeight = baseWeight;
        this.modWeight = modWeight;
        this.infravision = infravision;
        this.body = body;
        this.statsAdj = statsAdj;
        this.skillsAdj = skillsAdj;
        this.oFlags = oFlags;
        this.pFlags = pFlags;
        this.history = history;
        this.resists = resists;
    }

    public String getName() {
        return name;
    }
}