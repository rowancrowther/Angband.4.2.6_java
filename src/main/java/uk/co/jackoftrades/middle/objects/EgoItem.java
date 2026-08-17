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

import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
     */
    private String name;
    /**
     * Flavour/description text.
     */
    private String text;
    /**
     * The item type this ego applies to.
     */
    private List<ObjectKind> possItems;

    /**
     * Index in the global ego-item table.
     */
    private int egoIndex;

    /**
     * Added cost, as a dice expression.
     */
    private int cost;

    /**
     * Object flags this ego adds.
     */
    private Flag<ObjectFlag> flags;
    /**
     * Object flags this ego removes.
     */
    private Flag<ObjectFlag> flagsOff;
    /**
     * Kind flags this ego adds.
     */
    private Flag<ObjectKindFlag> kindFLags;

    /**
     * Modifiers this ego grants, as dice expressions.
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * Minimum guaranteed values for the granted modifiers.
     */
    private Map<ObjectModifier, Integer> minModifiers;
    /**
     * Per-element relation info added by this ego.
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands this ego adds (intrinsic flag).
     */
    private Set<Brand> brands;
    /**
     * Slays this ego adds (intrinsic flag).
     */
    private Set<Slay> slays;
    /**
     * Curses this ego adds, with their power.
     */
    private Map<Curse, CurseData> curses;

    /**
     * Level-feeling rating contribution, as an integer expression.
     */
    private int rating;
    /**
     * Allocation probability, as an integer expression.
     */
    private int allocProb;
    /**
     * Minimum allocation depth.
     */
    private int allocMin;
    /**
     * Maximum allocation depth.
     */
    private int allocMax;

    /**
     * To-hit bonus range, as a dice expression.
     */
    private Random toHit;
    /**
     * To-damage bonus range, as a dice expression.
     */
    private Random toDam;
    /**
     * To-armour-class bonus range, as a dice expression.
     */
    private Random toAC;

    /**
     * Minimum guaranteed to-hit bonus.
     */
    private int minToHit;
    /**
     * Minimum guaranteed to-damage bonus.
     */
    private int minToDam;
    /**
     * Minimum guaranteed to-armour-class bonus.
     */
    private int minToAC;

    /**
     * An activation this ego grants, if any.
     */
    private Activation activation;
    /**
     * Recharge time for the activation, as a dice expression.
     */
    private Random time;

    /**
     * Whether the player has ever seen this ego type.
     */
    private boolean everSeen;

    /**
     * Build an ego-item template from its parsed data-file fields.
     *
     * <p>Constructor EgoItem coded before 260817, commented in full on 260817.
     *
     * @param name         ego name
     * @param desc         flavour text, stored as this ego's {@code text}
     * @param egoIndex     ego-table index
     * @param cost         added cost
     * @param flags        added object flags
     * @param flagsOff     removed object flags
     * @param kindFLags    added kind flags
     * @param modifiers    granted modifiers
     * @param minModifiers minimum modifier values
     * @param elInfo       per-element resistances and vulnerabilities this ego adds
     * @param brands       added brands
     * @param slays        added slays
     * @param curses       added curses, each with the power and timeout this ego rolls for it
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
     */
    public EgoItem(String name, String desc, int egoIndex,
                   int cost, Flag<ObjectFlag> flags,
                   Flag<ObjectFlag> flagsOff,
                   Flag<ObjectKindFlag> kindFLags,
                   Map<ObjectModifier, Random> modifiers,
                   Map<ObjectModifier, Integer> minModifiers,
                   Map<ElementEnum, ElementInfo> elInfo,
                   Set<Brand> brands,
                   Set<Slay> slays,
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

    /**
     * Reports whether the player has ever seen an ego of this type identified, the port of reading
     * C's {@code ego->everseen}.
     *
     * <p>Not knowledge, but a record of whether the news has been broken. Recognising a Long Sword of
     * Extra Attacks for the first time is worth a message; the tenth is not, and this flag is how
     * {@link uk.co.jackoftrades.middle.player.Player#knowObject} tells the two apart.
     *
     * <p>Function isEverSeen commented in full on 260816.
     *
     * @return {@code true} if an ego of this type has been seen before
     */
    public boolean isEverSeen() {
        return everSeen;
    }

    /**
     * Returns the object flags this ego confers, the port of reading C's {@code ego->flags}.
     *
     * <p>These are the flags an ego of this type <em>always</em> grants, which is what makes them
     * usable as a recognition test: the player can identify an ego by its properties only if every
     * flag in this set is one they can read.
     *
     * <p>Function getFlags commented in full on 260816.
     *
     * @return this ego's flags, shared with this instance
     */
    public Flag<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * Returns the range a given modifier takes on this ego, the port of reading C's
     * {@code ego->modifiers[i]}.
     *
     * <p>A {@link Random}, not an {@code int}, because an ego's modifiers are rolled per object: a
     * Ring of the Mouse gives some amount of stealth, not a fixed amount. That is why recognising an
     * ego by its modifiers takes the trouble of evaluating the range at both extremes — a range that
     * spans zero can be present on an object showing nothing.
     *
     * <p>Answers {@code null} for a modifier this ego does not touch, where C reads a zeroed
     * {@code random_value} out of a full-length array. Callers should treat the null as that zero.
     *
     * <p>Function getModifier commented in full on 260816.
     *
     * @param modifier the modifier to look up
     * @return the range this ego rolls for that modifier, or {@code null} if it does not affect it
     */
    public Random getModifier(ObjectModifier modifier) {
        return modifiers.get(modifier);
    }

    /**
     * Returns the elemental resistances and vulnerabilities this ego confers, the port of reading
     * C's {@code ego->el_info}.
     *
     * <p>Function getElInfo commented in full on 260816.
     *
     * @return this ego's element info by element, shared with this instance
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return elInfo;
    }

    /**
     * Returns the brands this ego confers, the port of reading C's {@code ego->brands}.
     *
     * <p>Function getBrands commented in full on 260816.
     *
     * @return this ego's brands, shared with this instance
     */
    public Set<Brand> getBrands() {
        return brands;
    }

    /**
     * Returns the slays this ego confers, the port of reading C's {@code ego->slays}.
     *
     * <p>Function getSlays commented in full on 260816.
     *
     * @return this ego's slays, shared with this instance
     */
    public Set<Slay> getSlays() {
        return slays;
    }

    /**
     * Returns the curses this ego carries, the port of reading C's {@code ego->curses}.
     *
     * <p>Each is mapped to the {@link CurseData} the ego rolls for it — a power and a timeout — not
     * to whether the player knows of it, which is not a property of the ego at all.
     *
     * <p>Function getCurses commented in full on 260816.
     *
     * @return this ego's curses and their data, shared with this instance
     */
    public Map<Curse, CurseData> getCurses() {
        return curses;
    }
}