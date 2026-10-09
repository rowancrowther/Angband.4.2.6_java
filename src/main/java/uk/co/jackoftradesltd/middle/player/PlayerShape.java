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

package uk.co.jackoftradesltd.middle.player;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;

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
 * <p><b>Deliberate differences from the C struct.</b>
 * <ul>
 *   <li>{@code sidx}, the shape's index, is not held. C numbers shapes as {@code shape.txt} is
 *       parsed and finds them again by index or name; the port resolves by name through
 *       {@code PlayerRegistry.lookupPlayerShape}, and {@code Player} keeps a reference to the
 *       shape itself.</li>
 *   <li>The fixed C arrays ({@code skills[SKILL_MAX]}, {@code modifiers[OBJ_MOD_MAX]},
 *       {@code el_info[ELEM_MAX]}) become maps holding only the entries the data file named.
 *       An absent key reads as zero, which is what C's zero-filled array holds for it.</li>
 *   <li>C's linked {@code effect} and {@code blows} chains become lists, with {@link #numBlows}
 *       the size of the blow list as {@code num_blows} is in C. C pushes each {@code blow:} line
 *       onto the head of its chain, so it holds them in reverse file order; the port keeps file
 *       order.</li>
 * </ul>
 *
 * <p>Class PlayerShape coded before 260820, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class PlayerShape {
    /**
     * Logger for this type. Not part of the C struct; it is an instance field, so it appears in
     * {@link #toString()}.
     *
     * <p>Field logger coded before 260820, commented in full on 261009.
     */
    private final Logger logger = LogManager.getLogger();

    /**
     * Display name of the shape, e.g. {@code "bat"} (C: {@code player_shape.name}, set by the
     * {@code name:} line of {@code shape.txt}). It is the key {@code PlayerRegistry} looks shapes
     * up by.
     *
     * <p>Field name coded before 260820, commented in full on 261009.
     */
    private String name;
    /**
     * Armour-class bonus granted while in this shape (C: {@code player_shape.to_a}, the
     * {@code to-a} field of the {@code combat:} line).
     *
     * <p>Field toAc coded before 260820, commented in full on 261009.
     */
    private int toAc;
    /**
     * To-hit bonus granted while in this shape (C: {@code player_shape.to_h}, the {@code to-h}
     * field of the {@code combat:} line).
     *
     * <p>Field toHit coded before 260820, commented in full on 261009.
     */
    private int toHit;
    /**
     * To-damage bonus granted while in this shape (C: {@code player_shape.to_d}, the {@code to-d}
     * field of the {@code combat:} line).
     *
     * <p>Field toDam coded before 260820, commented in full on 261009.
     */
    private int toDam;

    /**
     * Per-skill adjustments applied while in this shape (C: {@code player_shape.skills}). Only
     * the skills a {@code skill-*:} line set are present; the rest read as zero.
     *
     * <p>Field skills coded before 260820, commented in full on 261009.
     */
    private Map<PlayerSkill, Integer> skills;
    /**
     * Object flags conferred while in this shape (C: {@code player_shape.flags}, from the
     * {@code obj-flags:} line), merged into the player's flags when the shape is worn.
     *
     * <p>Field flags coded before 260820, commented in full on 261009.
     */
    private Flag<ObjectFlag> flags;
    /**
     * Player (class/race) flags conferred while in this shape (C: {@code player_shape.pflags},
     * from the {@code player-flags:} line), merged into the player's flags when the shape is worn.
     *
     * <p>Field pflags coded before 260820, commented in full on 261009.
     */
    private Flag<PlayerFlag> pflags;
    //private Map<PlayerSkill, Integer> skillModifiers;
    //private Map<Stats, Integer> statModifiers;
    /**
     * Additive stat/modifier adjustments — the {@code obj_mods} half of the {@code values:}
     * line (C: {@code player_shape.modifiers}). Keyed by {@link ObjectModifier}; the
     * {@code RES_}-prefixed resistances of the same line live in {@link #elementValueModifiers}.
     * Only the modifiers the line named are present; the rest read as zero.
     *
     * <p>Field objectValueModifiers coded before 260820, commented in full on 261009.
     */
    private Map<ObjectModifier, Integer> objectValueModifiers;
    /**
     * Per-element resistance levels — the {@code RES_} half of the {@code values:} line
     * (C: {@code player_shape.el_info[].res_level}). Keyed by {@link ElementEnum}; the
     * additive modifiers of the same line live in {@link #objectValueModifiers}. Only the elements
     * the line named are present; the rest are absent rather than carrying a zero level. A
     * level of {@code -1} marks a vulnerability, which {@code calc_shapechange} treats
     * separately from a resistance.
     *
     * <p>Field elementValueModifiers coded before 260820, commented in full on 261009.
     */
    private Map<ElementEnum, ElementInfo> elementValueModifiers;

    /**
     * Effects fired when this shape is assumed (C: {@code player_shape.effect}, a linked chain
     * built by the {@code effect:} / {@code dice:} / {@code expr:} lines); empty if none. The
     * assembler always supplies a list, never {@code null}.
     *
     * <p>Field effect coded before 260820, commented in full on 261009.
     */
    private List<Effect> effect;

    /**
     * Number of {@code blow:} lines the shape carries (C: {@code player_shape.num_blows}), which
     * is the size of {@link #playerBlow}. Duplicate names count separately, so a name repeated
     * in the data file is picked proportionally more often when a blow name is chosen at random.
     *
     * <p>Field numBlows coded before 260820, commented in full on 261009.
     */
    private int numBlows;
    /**
     * The named unarmed blows available in this shape (C: {@code player_shape.blows}, see
     * {@link PlayerBlow}). Held in file order; C holds the same names in reverse file order.
     *
     * <p>Field playerBlow coded before 260820, commented in full on 261009.
     */
    private List<PlayerBlow> playerBlow;

    /**
     * Creates a fully-specified shape from its parsed attributes.
     *
     * <p>All the form's contributions are supplied at once and stored as given, with no copying
     * or validation. The class has no setters, so the object is an immutable description once
     * built, though the maps, flag sets and lists passed in are shared rather than copied.
     * {@code ShapeAssembler} is the production caller; it passes {@code numBlows} as the size of
     * {@code playerBlow}, and the class does not check that the two agree.
     *
     * <p>Constructor PlayerShape coded before 260820, commented in full on 261009.
     *
     * @param name           the shape's display name
     * @param toAc           armour-class bonus
     * @param toHit          to-hit bonus
     * @param toDam          to-damage bonus
     * @param skills         per-skill adjustments
     * @param flags          object flags conferred
     * @param pflags         player flags conferred
     * @param objectValueModifiers the additive modifiers from the {@code values:} line
     * @param elementValueModifiers the {@code RES_} levels from the {@code values:} line
     * @param effect         effects fired on assuming the shape (empty if none)
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
     * Returns a debug representation of this shape.
     *
     * <p>Lists the logger, name, the three combat bonuses, skills, both flag sets, effects, blow
     * count and blow names. It does not list {@link #objectValueModifiers} or
     * {@link #elementValueModifiers}, so a dump will not show a shape's stat, speed or resistance
     * values. Intended for logging/diagnostics, not for player-facing display. Not in C.
     *
     * <p>Function toString coded before 260820, commented in full on 261009.
     *
     * @return a dump of this shape's fields, omitting the two {@code values:} maps
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
     * Reads {@link #name}.
     *
     * <p>Function getName coded before 260820, commented in full on 261009.
     *
     * @return this shape's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Reads {@link #toAc}.
     *
     * <p>Function getToAc coded before 260820, commented in full on 261009.
     *
     * @return the armour-class bonus granted while in this shape
     */
    public int getToAc() {
        return toAc;
    }

    /**
     * Reads {@link #toHit}.
     *
     * <p>Function getToHit coded before 260820, commented in full on 261009.
     *
     * @return the to-hit bonus granted while in this shape
     */
    public int getToHit() {
        return toHit;
    }

    /**
     * Reads {@link #toDam}.
     *
     * <p>Function getToDam coded before 260820, commented in full on 261009.
     *
     * @return the to-damage bonus granted while in this shape
     */
    public int getToDam() {
        return toDam;
    }

    /**
     * Reads {@link #skills}; the live map is returned, not a copy, and a skill the shape does
     * not adjust is absent.
     *
     * <p>Function getSkills coded before 260820, commented in full on 261009.
     *
     * @return the per-skill adjustments applied while in this shape
     */
    public Map<PlayerSkill, Integer> getSkills() {
        return skills;
    }

    /**
     * Reads {@link #flags}; the live set is returned, not a copy.
     *
     * <p>Function getFlags coded before 260820, commented in full on 261009.
     *
     * @return the object flags conferred while in this shape
     */
    public Flag<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * Reads {@link #pflags}; the live set is returned, not a copy.
     *
     * <p>Function getPflags coded before 260820, commented in full on 261009.
     *
     * @return the player flags conferred while in this shape
     */
    public Flag<PlayerFlag> getPflags() {
        return pflags;
    }

    /**
     * Reads {@link #objectValueModifiers}; the live map is returned, not a copy, and a modifier
     * the shape does not adjust is absent. Stats are read through {@link #getModifier(Stats)}
     * instead.
     *
     * <p>Function getObjectValueModifiers coded before 260820, commented in full on 261009.
     *
     * @return the additive stat/modifier adjustments (the {@code obj_mods} half of the
     * {@code values:} line) applied while in this shape
     */
    public Map<ObjectModifier, Integer> getObjectValueModifiers() {
        return objectValueModifiers;
    }

    /**
     * Reads {@link #elementValueModifiers}; the live map is returned, not a copy, and an element
     * the shape does not touch is absent.
     *
     * <p>Function getElementValueModifiers coded before 260820, commented in full on 261009.
     *
     * @return the per-element resistance levels (the {@code RES_} half of the
     * {@code values:} line) applied while in this shape
     */
    public Map<ElementEnum, ElementInfo> getElementValueModifiers() {
        return elementValueModifiers;
    }

    /**
     * Reads {@link #effect}; the live list is returned, not a copy.
     *
     * <p>Function getEffect coded before 260820, commented in full on 261009.
     *
     * @return the effects fired when this shape is assumed (empty if the shape has none)
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * Reads {@link #numBlows}.
     *
     * <p>Function getNumBlows coded before 260820, commented in full on 261009.
     *
     * @return the number of unarmed blows this shape grants (blow lines counted with
     * duplicates, which weight selection frequency)
     */
    public int getNumBlows() {
        return numBlows;
    }

    /**
     * Reads {@link #playerBlow}; the live list is returned, not a copy.
     *
     * <p>Function getPlayerBlow coded before 260820, commented in full on 261009.
     *
     * @return the named unarmed blows available in this shape
     */
    public List<PlayerBlow> getPlayerBlow() {
        return playerBlow;
    }

    /**
     * This shape's adjustment to one stat — the port of reading C's {@code shape->modifiers[i]} for
     * {@code i < STAT_MAX} (the stats loop in {@code calc_shapechange}, {@code player-calcs.c}).
     *
     * <p>C can subscript the modifier array with a stat because the two lists start alike:
     * {@code list-object-modifiers.h} opens with STR, INT, WIS, DEX and CON in the order
     * {@code list-stats.h} declares them, so a stat index and a modifier index coincide for the
     * first five. That is a coincidence the data files maintain rather than a rule the code
     * enforces. The port refuses to rely on it and resolves the name instead, mapping
     * {@code STAT_STR} to {@code OM_STR}, so a reordering of either list cannot silently move a
     * shape's constitution bonus onto its stealth.
     *
     * <p>The name is built as {@code "OM_"} plus the stat's name without its {@code STAT_} prefix.
     * The two sentinels therefore resolve too, to {@code OM_NONE} and {@code OM_MAX}, which are
     * sentinels in {@link ObjectModifier} as well. A shape never carries a value for either, so
     * they read as zero rather than throwing. C would subscript outside the stat range there, so
     * callers (the stats loop in {@code PlayerCalcs}) skip both.
     *
     * <p>Function getModifier coded before 260820, commented in full on 261009.
     *
     * @param stat the stat to read; pass one of the five real stats
     * @return the shape's adjustment for that stat, or zero if the shape names none (which is
     *         also what the {@code STAT_NONE} and {@code STAT_MAX} sentinels return)
     * @throws NullPointerException if {@code stat} is {@code null}
     */
    public int getModifier(Stats stat) {
        ObjectModifier om = ObjectModifier.valueOf("OM_" + stat.name().substring(5));
        if (!objectValueModifiers.containsKey(om))
            return 0;
        return objectValueModifiers.get(om);
    }
}