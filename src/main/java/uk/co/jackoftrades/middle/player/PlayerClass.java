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

package uk.co.jackoftrades.middle.player;

import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.magic.ClassMagic;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

public class PlayerClass {
    private String name;

    private List<String> titles;
    private Map<Stats, Integer> stats;
    private Map<PlayerSkill, Integer> classSkills;
    private Map<PlayerSkill, Integer> extraSkills;

    private int hpAdj;
    private int expAdj;

    private Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
    private Flag<PlayerFlag> pFlags = new Flag<>(PlayerFlag.class);

    private int maxAttacks;
    private int minWeight;
    private int attMultiplier;

    List<StartItem> startItems;

    private ClassMagic magic;

    public PlayerClass(String name, List<String> titles, Map<Stats, Integer> stats,
                       Map<PlayerSkill, Integer> classSkills,
                       Map<PlayerSkill, Integer> extraSkills, int hpAdj, int expAdj,
                       Flag<ObjectFlag> oFlags, Flag<PlayerFlag> pFlags, int maxAttacks,
                       int minWeight, int attMultiplier, List<StartItem> startItems,
                       ClassMagic magic) {
        this.name = name;
        this.titles = titles;
        this.stats = stats;
        this.classSkills = classSkills;
        this.extraSkills = extraSkills;
        this.hpAdj = hpAdj;
        this.expAdj = expAdj;
        this.oFlags = oFlags;
        this.pFlags = pFlags;
        this.maxAttacks = maxAttacks;
        this.minWeight = minWeight;
        this.attMultiplier = attMultiplier;
        this.startItems = startItems;
        this.magic = magic;
    }
}
