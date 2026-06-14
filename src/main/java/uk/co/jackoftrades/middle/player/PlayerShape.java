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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.combat.enums.Element;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagName;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

public class PlayerShape {
    private final Logger logger = LogManager.getLogger();

    private String name;
    private int toAc;
    private int toHit;
    private int toDam;

    private Map<PlayerSkill, Integer> skills;
    private Flag<ObjectFlagName> flags;
    private Flag<PlayerFlag> pflags;
    //private Map<PlayerSkill, Integer> skillModifiers;
    //private Map<Stats, Integer> statModifiers;
    private Map<ValueEnum, Integer> valueModifiers;
    private List<Element> resists;

    private Effect effect;

    private int numBlows;
    private List<PlayerBlow> playerBlow;

    public PlayerShape(String name,
                       int toAc,
                       int toHit,
                       int toDam,
                       Map<PlayerSkill, Integer> skills,
                       Flag<ObjectFlagName> flags,
                       Flag<PlayerFlag> pflags,
                       Map<ValueEnum, Integer> valueModifiers,
                       List<Element> resists,
                       Effect effect,
                       int numBlows,
                       List<PlayerBlow> playerBlow) {
        this.name = name;
        // this.sidx = sidx;
        this.toAc = toAc;
        this.toHit = toHit;
        this.toDam = toDam;
        this.skills = skills;
        this.flags = flags;
        this.pflags = pflags;
        this.valueModifiers = valueModifiers;
        this.resists = resists;
        this.effect = effect;
        this.numBlows = numBlows;
        this.playerBlow = playerBlow;
    }

    @Override
    public String toString() {
        return "PlayerShape{" +
                "logger=" + logger +
                ", name='" + name + '\'' +
                // ", sidx=" + sidx +
                ", toAc=" + toAc +
                ", toHit=" + toHit +
                ", toDam=" + toDam +
                ", skills=" + skills +
                ", flags=" + flags +
                ", pflags=" + pflags +
                ", valueModifiers=" + valueModifiers +
                ", resists=" + resists +
                ", effect=" + effect +
                ", numBlows=" + numBlows +
                ", playerBlow=" + playerBlow +
                '}';
    }

    public String getName() {
        return name;
    }
}