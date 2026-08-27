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
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A unique artifact definition (as loaded from {@code artifact.txt}) — a one-of-a-kind
 * item with fixed bonuses, flags, modifiers, brands/slays/curses, an optional
 * activation, and allocation parameters. This is the Java port of the C
 * original's {@code struct artifact} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class Artifact {
    /**
     * The artifact's name.
     */
    private String name;
    /**
     * Flavour/description text.
     */
    private String text;

    /**
     * The base item type value (tval) the artifact is built on.
     */
    private TValue tValue;
    /**
     * The base sub-type value (sval).
     */
    private String sValue;

    /**
     * To-hit bonus.
     */
    private int toHit;
    /**
     * To-damage bonus.
     */
    private int toDam;
    /**
     * To-armour-class bonus.
     */
    private int toAC;
    /**
     * Base armour class.
     */
    private int ac;

    // private int damageDice;
    // private int damageSides;
    /**
     * Damage dice expression as a string.
     */
    private String diceString;

    /**
     * Weight.
     */
    private int weight;

    /**
     * Cost/value.
     */
    private int cost;

    /**
     * Object flags this artifact grants.
     */
    private Flag<ObjectFlag> flags;

    /**
     * Numeric modifiers granted, keyed by modifier.
     */
    private Map<ObjectModifier, Integer> modifiers;
    /**
     * Per-element relation info.
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands the artifact carries.
     */
    private Set<Brand> brands;
    /**
     * Slays the artifact carries.
     */
    private Set<Slay> slays;
    /**
     * Curses the artifact carries, each with its instance data.
     */
    private Map<Curse, CurseData> curses;

    /**
     * The artifact's native level.
     */
    private int level;

    /**
     * Allocation probability weight.
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
     * The artifact's activation, if any.
     */
    private Activation activation;
    /**
     * Message shown when the activation is used.
     */
    private String activationMessage;

    /**
     * Recharge time for the activation, as a dice expression.
     */
    private Random time;

    /**
     * Build an artifact from its parsed {@code artifact.txt} fields.
     *
     * <p>Every collection argument is <b>stored, not copied</b>. The artifacts are loaded once into
     * the registry and read from there, so each artifact owns the maps and sets the assembler built
     * for it and nothing else holds a reference; {@link #copy()} is what callers use when they need
     * an independent one.
     *
     * <p>Constructor Artifact commented in full on 260827.
     *
     * @param name              display name
     * @param text              flavour text shown on examination
     * @param tValue            the object type this artifact is built on
     * @param sValue            the sub-type within that object type, as the data file spells it
     * @param toHit             to-hit bonus
     * @param toDam             to-damage bonus
     * @param toAC              to-armour bonus
     * @param ac                base armour class
     * @param diceString        damage dice, as a dice expression
     * @param weight            weight in tenth-pounds
     * @param cost              base cost in gold
     * @param flags             object flags; stored, not copied
     * @param modifiers         per-modifier values; stored, not copied
     * @param elInfo            per-element resistances and ignores; stored, not copied
     * @param brands            brands carried; stored, not copied
     * @param slays             slays carried; stored, not copied
     * @param curses            curses carried, with their instance data; stored, not copied
     * @param level             native depth
     * @param allocProb         allocation probability within the depth band
     * @param allocMin          shallowest depth this artifact may be generated at
     * @param allocMax          deepest depth this artifact may be generated at
     * @param activation        the activation this artifact grants, or {@code null}
     * @param activationMessage message shown when the activation is used
     * @param time              recharge time for the activation, as a dice expression
     */
    public Artifact(String name, String text, TValue tValue, String sValue,
                    int toHit, int toDam, int toAC, int ac, String diceString,
                    int weight, int cost, Flag<ObjectFlag> flags,
                    Map<ObjectModifier, Integer> modifiers,
                    Map<ElementEnum, ElementInfo> elInfo, Set<Brand> brands,
                    Set<Slay> slays, Map<Curse, CurseData> curses, int level,
                    int allocProb, int allocMin, int allocMax, Activation activation,
                    String activationMessage, Random time) {
        this.name = name;
        this.text = text;
        this.tValue = tValue;
        this.sValue = sValue;
        this.toHit = toHit;
        this.toDam = toDam;
        this.toAC = toAC;
        this.ac = ac;
        this.diceString = diceString;
        this.weight = weight;
        this.cost = cost;
        this.flags = flags;
        this.modifiers = modifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = curses;
        this.level = level;
        this.allocProb = allocProb;
        this.allocMin = allocMin;
        this.allocMax = allocMax;
        this.activation = activation;
        this.activationMessage = activationMessage;
        this.time = time;
    }

    /**
     * @return the artifact's display name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the artifact's flavour/description text
     */
    public String getText() {
        return text;
    }

    /**
     * @return the base type (tval) of the item the artifact is built on
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * @return the subtype name (sval) of the item the artifact is built on
     */
    public String getsValue() {
        return sValue;
    }

    /**
     * @return the to-hit combat bonus
     */
    public int getToHit() {
        return toHit;
    }

    /**
     * @return the to-damage combat bonus
     */
    public int getToDam() {
        return toDam;
    }

    /**
     * @return the to-armour-class combat bonus
     */
    public int getToAC() {
        return toAC;
    }

    /**
     * @return the base armour class
     */
    public int getAc() {
        return ac;
    }

    /**
     * @return the unparsed damage dice string for the artifact's weapon
     */
    public String getDiceString() {
        return diceString;
    }

    /**
     * @return the artifact's weight (in tenths of a pound)
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @return the artifact's base monetary value
     */
    public int getCost() {
        return cost;
    }

    /**
     * @return the object flags the artifact grants
     */
    public Flag<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * @return the additive numeric modifiers the artifact grants (obj_mods)
     */
    public Map<ObjectModifier, Integer> getModifiers() {
        return modifiers;
    }

    /**
     * @return the per-element resistance levels and hates/ignores flags the artifact imposes
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return elInfo;
    }

    /**
     * @return the brands the artifact adds to its attacks
     */
    public Set<Brand> getBrands() {
        return brands;
    }

    /**
     * @return the slays the artifact adds to its attacks
     */
    public Set<Slay> getSlays() {
        return slays;
    }

    /**
     * @return the curses attached to the artifact, keyed by curse
     */
    public Map<Curse, CurseData> getCurses() {
        return curses;
    }

    /**
     * @return the artifact's native depth/level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the allocation probability weight used when generating this artifact
     */
    public int getAllocProb() {
        return allocProb;
    }

    /**
     * @return the minimum depth at which the artifact may be generated
     */
    public int getAllocMin() {
        return allocMin;
    }

    /**
     * @return the maximum depth at which the artifact may be generated
     */
    public int getAllocMax() {
        return allocMax;
    }

    /**
     * @return the artifact's activation effect, or {@code null} if it has none
     */
    public Activation getActivation() {
        return activation;
    }

    /**
     * @return the message shown when the artifact is activated
     */
    public String getActivationMessage() {
        return activationMessage;
    }

    /**
     * @return the recharge interval (dice) for the artifact's activation
     */
    public Random getTime() {
        return time;
    }

    /**
     * Returns an independent copy of this artifact, deep where it needs to be and shallow where it
     * does not.
     *
     * <p>Deep-copied because their contents are mutable and a shared reference would let one copy's
     * state show up on the other: the flag set, the modifier map, the element info (each
     * {@code ElementInfo} copied in turn, not just the map), the curse map (each
     * {@code CurseData} rebuilt), the activation, and the recharge dice.
     *
     * <p>Shallow-copied deliberately: the brand and slay sets are rebuilt as new sets, but their
     * members are shared, because a {@code Brand} and a {@code Slay} are immutable registry entries
     * that every object carrying them points at - exactly as C shares its {@code brands[]} and
     * {@code slays[]} rows. Primitives and {@link String}s are passed straight through.
     *
     * <p>The locals exist to make that division legible at the call to the constructor rather than
     * for any technical reason.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new artifact that shares no mutable state with this one
     */
    public Artifact copy() {
        String newName = name;
        String newText = text;
        TValue newtValue = tValue;
        String newsValue = sValue;
        int newtoHit = toHit;
        int newtoDam = toDam;
        int newtoAC = toAC;
        int newac = ac;
        String newdiceString = diceString;
        int newweight = weight;
        int newcost = cost;
        Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
        oFlags.copyFrom(this.flags);
        Map<ObjectModifier, Integer> newModifiers = new HashMap<>();
        for (ObjectModifier modifier : modifiers.keySet()) {
            newModifiers.put(modifier, modifiers.get(modifier));
        }
        Map<ElementEnum, ElementInfo> newElInfo = new HashMap<>();
        for (ElementEnum element : elInfo.keySet()) {
            newElInfo.put(element, elInfo.get(element).copy());
        }
        Set<Brand> newBrands = new HashSet<>(brands);
        Set<Slay> newSlays = new HashSet<>(slays);
        Map<Curse, CurseData> newCurses = new HashMap<>();
        for (Curse curse : curses.keySet()) {
            newCurses.put(curse, new CurseData(curses.get(curse)));
        }
        int newLevel = level;
        int newAllocProb = allocProb;
        int newAllocMin = allocMin;
        int newAllocMax = allocMax;
        Activation newActivation = activation.copy();
        String newActivationMessage = activationMessage;
        Random newTime = time.copy();

        return new Artifact(newName, newText, newtValue, newsValue, newtoHit,
                newtoDam, newtoAC, newac, newdiceString, newweight, newcost,
                oFlags, newModifiers, newElInfo, newBrands, newSlays,
                newCurses, newLevel, newAllocProb, newAllocMin, newAllocMax,
                newActivation, newActivationMessage, newTime);
    }
}
