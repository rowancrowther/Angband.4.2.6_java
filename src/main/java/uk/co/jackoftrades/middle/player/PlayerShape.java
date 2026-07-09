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
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.ValueEnum;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerSkill;

import java.util.List;
import java.util.Map;

/**
 * An alternative form the player can take on — a shapechange such as bat, vampire or
 * werewolf — bundling the combat bonuses, skills, flags, resistances, value modifiers and
 * unarmed blows that apply while the form is held.
 *
 * <p>Ports the C {@code struct player_shape} ({@code player.h}), defined by {@code shape.txt}.
 * While shapechanged the player's effective profile is the base character adjusted by this
 * form's contributions, and an optional {@link Effect} can fire on assuming the shape.
 *
 * <p><b>Why a self-contained bundle:</b> a shape overrides or augments many disparate parts of
 * the character at once (to-hit / to-dam / AC, per-skill values, object and player flags,
 * elemental resists, arbitrary value modifiers, blow count and named blows). Collecting them on
 * one object lets the form be applied and removed as a single unit.
 *
 * @author Rowan Crowther
 */
public class PlayerShape {
    /**
     * Logger for this type.
     */
    private final Logger logger = LogManager.getLogger();

    /** Display name of the shape, e.g. {@code "bat"} (C: {@code player_shape.name}). */
    private String name;
    /** Armour-class bonus granted while in this shape. */
    private int toAc;
    /** To-hit bonus granted while in this shape. */
    private int toHit;
    /** To-damage bonus granted while in this shape. */
    private int toDam;

    /** Per-skill adjustments applied while in this shape. */
    private Map<PlayerSkill, Integer> skills;
    /**
     * Object flags conferred while in this shape.
     */
    private Flag<ObjectFlag> flags;
    /** Player (class/race) flags conferred while in this shape. */
    private Flag<PlayerFlag> pflags;
    //private Map<PlayerSkill, Integer> skillModifiers;
    //private Map<Stats, Integer> statModifiers;
    /** Miscellaneous modifier-indexed value adjustments applied while in this shape. */
    private Map<ValueEnum, Integer> valueModifiers;
    /**
     * Elements resisted while in this shape.
     */
    private List<ElementEnum> resists;

    /** Optional effect fired when the shape is assumed (C: {@code player_shape.effect}). */
    private Effect effect;

    /** Number of unarmed blows the shape grants. */
    private int numBlows;
    /** The named unarmed blows available in this shape (see {@link PlayerBlow}). */
    private List<PlayerBlow> playerBlow;

    /**
     * Creates a fully-specified shape from its parsed attributes.
     *
     * <p>All the form's contributions are supplied at once; the object is otherwise an immutable
     * description, applied to the player when the shape is entered.
     *
     * @param name           the shape's display name
     * @param toAc           armour-class bonus
     * @param toHit          to-hit bonus
     * @param toDam          to-damage bonus
     * @param skills         per-skill adjustments
     * @param flags          object flags conferred
     * @param pflags         player flags conferred
     * @param valueModifiers miscellaneous value adjustments
     * @param resists        elements resisted
     * @param effect         effect fired on assuming the shape (may be {@code null})
     * @param numBlows       number of unarmed blows granted
     * @param playerBlow     the named unarmed blows
     */
    public PlayerShape(String name,
                       int toAc,
                       int toHit,
                       int toDam,
                       Map<PlayerSkill, Integer> skills,
                       Flag<ObjectFlag> flags,
                       Flag<PlayerFlag> pflags,
                       Map<ValueEnum, Integer> valueModifiers,
                       List<ElementEnum> resists,
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

    /**
     * Returns a debug representation listing every contribution of this shape.
     *
     * <p>Intended for logging/diagnostics while the shape system is being ported, not for
     * player-facing display.
     *
     * @return a field-by-field dump of this shape
     */
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

    /**
     * @return this shape's display name
     */
    public String getName() {
        return name;
    }
}