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

package uk.co.jackoftrades.middle.objects;

import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.util.List;
import java.util.Map;

/**
 * An ego item template (as loaded from {@code ego_item.txt}) — a named modifier
 * applied to a base item to make a "magical" variant (e.g. "of Slaying"),
 * defining the flags/modifiers it adds (and minimums), brands/slays/curses,
 * bonus ranges, which base items it may apply to, and an optional activation.
 * This is the Java port of the C original's {@code struct ego_item}
 * ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class EgoItem {
    /**
     * The ego type's name (e.g. "of Slaying").
     *
     * @author Rowan Crowther
     */
    private String name;
    /**
     * Flavour/description text.
     *
     * @author Rowan Crowther
     */
    private String text;
    /**
     * The item type this ego applies to.
     *
     * @author Rowan Crowther
     */
    private List<ObjectKind> possItems;

    /**
     * Index in the global ego-item table.
     *
     * @author Rowan Crowther
     */
    private int egoIndex;

    /**
     * Added cost, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private int cost;

    /**
     * Object flags this ego adds.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectFlag> flags;
    /**
     * Object flags this ego removes.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectFlag> flagsOff;
    /**
     * Kind flags this ego adds.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectKindFlag> kindFLags;

    /**
     * Modifiers this ego grants, as dice expressions.
     *
     * @author Rowan Crowther
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * Minimum guaranteed values for the granted modifiers.
     *
     * @author Rowan Crowther
     */
    private Map<ObjectModifier, Integer> minModifiers;
    /**
     * Per-element relation info added by this ego.
     *
     * @author Rowan Crowther
     */
    private Map<ElementEnum, ElementInfo> elInfo;
    /**
     * Per-element relation info added by this ego.
     *
     * @author Rowan Crowther
     */
    private Map<ElementEnum, ElementInfo> minElInfo;

    /**
     * Brands this ego adds (intrinsic flag).
     *
     * @author Rowan Crowther
     */
    private Map<Brand, Boolean> brands;
    /**
     * Slays this ego adds (intrinsic flag).
     *
     * @author Rowan Crowther
     */
    private Map<Slay, Boolean> slays;
    /**
     * Curses this ego adds, with their power.
     *
     * @author Rowan Crowther
     */
    private Map<Curse, CurseData> curses;

    /**
     * Level-feeling rating contribution, as an integer expression.
     *
     * @author Rowan Crowther
     */
    private int rating;
    /**
     * Allocation probability, as an integer expression.
     *
     * @author Rowan Crowther
     */
    private int allocProb;
    /**
     * Minimum allocation depth.
     *
     * @author Rowan Crowther
     */
    private int allocMin;
    /**
     * Maximum allocation depth.
     *
     * @author Rowan Crowther
     */
    private int allocMax;

    /**
     * To-hit bonus range, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random toHit;
    /**
     * To-damage bonus range, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random toDam;
    /**
     * To-armour-class bonus range, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random toAC;

    /**
     * Minimum guaranteed to-hit bonus.
     *
     * @author Rowan Crowther
     */
    private int minToHit;
    /**
     * Minimum guaranteed to-damage bonus.
     *
     * @author Rowan Crowther
     */
    private int minToDam;
    /**
     * Minimum guaranteed to-armour-class bonus.
     *
     * @author Rowan Crowther
     */
    private int minToAC;

    /**
     * An activation this ego grants, if any.
     *
     * @author Rowan Crowther
     */
    private Activation activation;
    /**
     * Recharge time for the activation, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random time;

    /**
     * Whether the player has ever seen this ego type.
     *
     * @author Rowan Crowther
     */
    private boolean everSeen;

    /**
     * Build an ego-item template from its parsed data-file fields.
     *
     * @param name         ego name
     * @param egoIndex     ego-table index
     * @param cost         added cost
     * @param flags        added object flags
     * @param flagsOff     removed object flags
     * @param kindFLags    added kind flags
     * @param modifiers    granted modifiers
     * @param minModifiers minimum modifier values
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
     * @author Rowan Crowther
     */
    public EgoItem(String name, String desc, int egoIndex,
                   int cost, Flag<ObjectFlag> flags,
                   Flag<ObjectFlag> flagsOff,
                   Flag<ObjectKindFlag> kindFLags,
                   Map<ObjectModifier, Random> modifiers,
                   Map<ObjectModifier, Integer> minModifiers,
                   Map<ElementEnum, ElementInfo> elInfo,
                   Map<Brand, Boolean> brands,
                   Map<Slay, Boolean> slays,
                   Map<Curse, CurseData> curses, int rating,
                   int allocProb, int allocMin, int allocMax,
                   List<ObjectKind> possItems, Random toHit,
                   Random toDam, Random toAC, int minToHit,
                   int minToDam, int minToAC,
                   Activation activation, Random time,
                   boolean everSeen) {
        this.name = name;
        this.text = desc;
        this.egoIndex = egoIndex;
        this.cost = cost;
        this.flags = flags;
        this.flagsOff = flagsOff;
        this.kindFLags = kindFLags;
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