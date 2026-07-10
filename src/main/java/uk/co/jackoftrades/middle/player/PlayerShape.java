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
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
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
    /**
     * Additive stat/modifier adjustments — the {@code obj_mods} half of the {@code values:}
     * line (C: {@code player_shape.modifiers}). Keyed by {@link ObjectModifier}; the
     * {@code RES_}-prefixed resistances of the same line live in {@link #elementValueModifiers}.
     */
    private Map<ObjectModifier, Integer> objectValueModifiers;
    /**
     * Per-element resistance levels — the {@code RES_} half of the {@code values:} line
     * (C: {@code player_shape.el_info[].res_level}). Keyed by {@link ElementEnum}; the
     * additive modifiers of the same line live in {@link #objectValueModifiers}.
     */
    private Map<ElementEnum, ElementInfo> elementValueModifiers;

    /**
     * Effects fired when this shape is assumed (C: {@code player_shape.effect}); empty if none.
     */
    private List<Effect> effect;

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
                       Map<ObjectModifier, Integer> objectValueModifiers,
                       Map<ElementEnum, ElementInfo> elementValueModifiers,
                       List<Effect> effect,
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
        this.objectValueModifiers = objectValueModifiers;
        this.elementValueModifiers = elementValueModifiers;
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

    /**
     * @return the armour-class bonus granted while in this shape
     */
    public int getToAc() {
        return toAc;
    }

    /**
     * @return the to-hit bonus granted while in this shape
     */
    public int getToHit() {
        return toHit;
    }

    /**
     * @return the to-damage bonus granted while in this shape
     */
    public int getToDam() {
        return toDam;
    }

    /**
     * @return the per-skill adjustments applied while in this shape
     */
    public Map<PlayerSkill, Integer> getSkills() {
        return skills;
    }

    /**
     * @return the object flags conferred while in this shape
     */
    public Flag<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * @return the player flags conferred while in this shape
     */
    public Flag<PlayerFlag> getPflags() {
        return pflags;
    }

    /**
     * @return the additive stat/modifier adjustments (the {@code obj_mods} half of the
     * {@code values:} line) applied while in this shape
     */
    public Map<ObjectModifier, Integer> getObjectValueModifiers() {
        return objectValueModifiers;
    }

    /**
     * @return the per-element resistance levels (the {@code RES_} half of the
     * {@code values:} line) applied while in this shape
     */
    public Map<ElementEnum, ElementInfo> getElementValueModifiers() {
        return elementValueModifiers;
    }

    /**
     * @return the effects fired when this shape is assumed (empty if the shape has none)
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * @return the number of unarmed blows this shape grants (blow lines counted with
     * duplicates, which weight selection frequency)
     */
    public int getNumBlows() {
        return numBlows;
    }

    /**
     * @return the named unarmed blows available in this shape
     */
    public List<PlayerBlow> getPlayerBlow() {
        return playerBlow;
    }
}