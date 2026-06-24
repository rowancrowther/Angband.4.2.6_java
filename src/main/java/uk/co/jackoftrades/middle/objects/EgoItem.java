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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.Map;

/**
 * An ego item template (as loaded from {@code ego_item.txt}) — a named modifier
 * applied to a base item to make a "magical" variant (e.g. "of Slaying"),
 * defining the flags/modifiers it adds (and minimums), brands/slays/curses,
 * bonus ranges, which base items it may apply to, and an optional activation.
 * This is the Java port of the C original's {@code struct ego_item}
 * ({@code src/object.h}).
 *
 * @author ClaudeCode
 */
public class EgoItem {
    /**
     * The ego type's name (e.g. "of Slaying").
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * Flavour/description text.
     *
     * @author ClaudeCode
     */
    private String text;
    /**
     * The item type this ego applies to.
     *
     * @author ClaudeCode
     */
    private TValue type;

    /**
     * Index in the global ego-item table.
     *
     * @author ClaudeCode
     */
    private int egoIndex;

    /**
     * Added cost, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random cost;

    /**
     * Object flags this ego adds.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectFlag> flags;
    /**
     * Object flags this ego removes.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectFlag> flagsOff;
    /**
     * Kind flags this ego adds.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectKindFlag> kindFLags;
    /**
     * Kind flags this ego removes.
     *
     * @author ClaudeCode
     */
    private Flag<ObjectKindFlag> kindFLagsOff;

    /**
     * Modifiers this ego grants, as dice expressions.
     *
     * @author ClaudeCode
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * Minimum guaranteed values for the granted modifiers.
     *
     * @author ClaudeCode
     */
    private Map<ObjectModifier, Random> minModifiers;
    /**
     * Per-element relation info added by this ego.
     *
     * @author ClaudeCode
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands this ego adds (intrinsic flag).
     *
     * @author ClaudeCode
     */
    private Map<Brand, Boolean> brands;
    /**
     * Slays this ego adds (intrinsic flag).
     *
     * @author ClaudeCode
     */
    private Map<Slay, Boolean> slays;
    /**
     * Curses this ego adds, with their power.
     *
     * @author ClaudeCode
     */
    private Map<CurseData, Integer> curses;

    /**
     * Level-feeling rating contribution, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random rating;
    /**
     * Allocation probability, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random allocProb;
    /**
     * Minimum allocation depth.
     *
     * @author ClaudeCode
     */
    private int allocMin;
    /**
     * Maximum allocation depth.
     *
     * @author ClaudeCode
     */
    private int allocMax;

    /**
     * The base object kinds this ego may be applied to, keyed by type.
     *
     * @author ClaudeCode
     */
    private Map<TValue, ObjectKind> possItems;

    /**
     * To-hit bonus range, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random toHit;
    /**
     * To-damage bonus range, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random toDam;
    /**
     * To-armour-class bonus range, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random toAC;

    /**
     * Minimum guaranteed to-hit bonus.
     *
     * @author ClaudeCode
     */
    private Random minToHit;
    /**
     * Minimum guaranteed to-damage bonus.
     *
     * @author ClaudeCode
     */
    private Random minToDam;
    /**
     * Minimum guaranteed to-armour-class bonus.
     *
     * @author ClaudeCode
     */
    private Random minToAC;

    /**
     * An activation this ego grants, if any.
     *
     * @author ClaudeCode
     */
    private Activation activation;
    /**
     * Recharge time for the activation, as a dice expression.
     *
     * @author ClaudeCode
     */
    private Random time;

    /**
     * Whether the player has ever seen this ego type.
     *
     * @author ClaudeCode
     */
    private boolean everSeen;

    /**
     * Build an ego-item template from its parsed data-file fields.
     *
     * @param name         ego name
     * @param type         applicable item type
     * @param egoIndex     ego-table index
     * @param cost         added cost
     * @param flags        added object flags
     * @param flagsOff     removed object flags
     * @param kindFLags    added kind flags
     * @param kindFLagsOff removed kind flags
     * @param modifiers    granted modifiers
     * @param minModifiers minimum modifier values
     * @param elInfo       per-element info
     * @param brands       added brands
     * @param slays        added slays
     * @param curses       added curses
     * @param rating       rating contribution
     * @param allocProb    allocation probability
     * @param allocMin     minimum allocation depth
     * @param allocMax     maximum allocation depth
     * @param possItems    applicable base kinds
     * @param toHit        to-hit bonus range
     * @param toDam        to-damage bonus range
     * @param toAC         to-AC bonus range
     * @param minToHit     minimum to-hit bonus
     * @param minToDam     minimum to-damage bonus
     * @param minToAC      minimum to-AC bonus
     * @param activation   granted activation
     * @param time         activation recharge time
     * @param everSeen     whether ever seen
     * @author ClaudeCode
     */
    public EgoItem(String name, TValue type, int egoIndex,
                   Random cost, Flag<ObjectFlag> flags,
                   Flag<ObjectFlag> flagsOff,
                   Flag<ObjectKindFlag> kindFLags,
                   Flag<ObjectKindFlag> kindFLagsOff,
                   Map<ObjectModifier, Random> modifiers,
                   Map<ObjectModifier, Random> minModifiers,
                   Map<ElementEnum, ElementInfo> elInfo,
                   Map<Brand, Boolean> brands,
                   Map<Slay, Boolean> slays,
                   Map<CurseData, Integer> curses, Random rating,
                   Random allocProb, int allocMin, int allocMax,
                   Map<TValue, ObjectKind> possItems, Random toHit,
                   Random toDam, Random toAC, Random minToHit,
                   Random minToDam, Random minToAC,
                   Activation activation, Random time,
                   boolean everSeen) {
        this.name = name;
        this.type = type;
        this.egoIndex = egoIndex;
        this.cost = cost;
        this.flags = flags;
        this.flagsOff = flagsOff;
        this.kindFLags = kindFLags;
        this.kindFLagsOff = kindFLagsOff;
        this.modifiers = modifiers;
        this.minModifiers = minModifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = curses;
        this.rating = rating;
        this.allocProb = allocProb;
        this.allocMin = allocMin;
        this.allocMax = allocMax;
        this.possItems = possItems;
        this.toHit = toHit;
        this.toDam = toDam;
        this.toAC = toAC;
        this.minToHit = minToHit;
        this.minToDam = minToDam;
        this.minToAC = minToAC;
        this.activation = activation;
        this.time = time;
        this.everSeen = everSeen;
    }
}