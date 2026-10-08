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

package uk.co.jackoftradesltd.middle.objects;

import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.*;

/**
 * A unique artifact definition (as loaded from {@code artifact.txt}) — a one-of-a-kind
 * item with fixed bonuses, flags, modifiers, brands/slays/curses, an optional
 * activation, and allocation parameters. This is the Java port of the C
 * original's {@code struct artifact} ({@code object.h}), the unchanging half of C's artifact
 * data; the half that changes during play lives in {@link ArtifactUpkeep}, reached through
 * {@link #getAup()}.
 *
 * <p>Two C fields are not ported. {@code aidx} existed to cross-index {@code a_info} against
 * {@code aup_info}, and the Java artifact owns its {@link ArtifactUpkeep} directly, so there is
 * no second array to index. {@code next} was the list link the parser threaded through the
 * records while loading; the registry holds the artifacts now. C's {@code dd} and {@code ds}
 * are held together as the single {@link #getDiceString() dice string}.
 *
 * <p>C never copies a {@code struct artifact}: it reads the shared registry entry through
 * {@code copy_artifact_data} in {@code obj-make.c} when it builds the object. {@link #copy()}
 * is a Java addition for callers that need an independent definition.
 *
 * <p>Class Artifact coded before 260827, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public class Artifact {
    /**
     * The artifact's name — C's {@code artifact->name}.
     *
     * <p>Field name coded before 260827, commented in full on 261008.
     */
    private String name;
    /**
     * Flavour/description text — C's {@code artifact->text}.
     *
     * <p>Field text coded before 260827, commented in full on 261008.
     */
    private String text;

    /**
     * The base item type (tval) the artifact is built on — C's {@code artifact->tval}, held as the
     * {@link TValue} enum rather than a bare {@code int}.
     *
     * <p>Field tValue coded before 260827, commented in full on 261008.
     */
    private TValue tValue;
    /**
     * The base sub-type (sval) the artifact is built on, held as the sub-type's name as the data
     * file spells it. C's {@code artifact->sval} is the numeric index that {@code lookup_sval}
     * resolves that name to; the name is resolved against {@link #tValue} when the kind is wanted.
     *
     * <p>Field sValue coded before 260827, commented in full on 261008.
     */
    private String sValue;

    /**
     * To-hit bonus — C's {@code artifact->to_h}.
     *
     * <p>Field toHit coded before 260827, commented in full on 261008.
     */
    private int toHit;
    /**
     * To-damage bonus — C's {@code artifact->to_d}.
     *
     * <p>Field toDam coded before 260827, commented in full on 261008.
     */
    private int toDam;
    /**
     * To-armour-class bonus — C's {@code artifact->to_a}.
     *
     * <p>Field toAC coded before 260827, commented in full on 261008.
     */
    private int toAC;
    /**
     * Base armour class — C's {@code artifact->ac}.
     *
     * <p>Field ac coded before 260827, commented in full on 261008.
     */
    private int ac;

    // private int damageDice;
    // private int damageSides;
    /**
     * The damage dice as the single expression the data file gives, such as {@code 3d5}. C splits
     * this into {@code artifact->dd} (dice) and {@code artifact->ds} (sides); the port keeps the
     * unsplit text.
     *
     * <p>Field diceString coded before 260827, commented in full on 261008.
     */
    private String diceString;

    /**
     * Weight in tenths of a pound — C's {@code artifact->weight}.
     *
     * <p>Field weight coded before 260827, commented in full on 261008.
     */
    private int weight;

    /**
     * The artifact's pseudo-worth in gold — C's {@code artifact->cost}.
     *
     * <p>Field cost coded before 260827, commented in full on 261008.
     */
    private int cost;

    /**
     * Object flags this artifact grants — C's {@code artifact->flags}. When C builds the object the
     * artifact's flags are unioned onto the object's own.
     *
     * <p>Field flags coded before 260827, commented in full on 261008.
     */
    private Flag<ObjectFlag> flags;

    /**
     * Numeric modifiers granted, keyed by modifier — C's {@code artifact->modifiers[]}. C holds a
     * value (zero by default) for every modifier; this map is keyed only by the modifiers the
     * artifact was given.
     *
     * <p>Field modifiers coded before 260827, commented in full on 261008.
     */
    private Map<ObjectModifier, Integer> modifiers;
    /**
     * Per-element resistance level and hates/ignores flags — C's {@code artifact->el_info[]},
     * which has a slot for every element; this map is keyed only by the elements the assembler
     * gave an entry.
     *
     * <p>Field elInfo coded before 260827, commented in full on 261008.
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands the artifact carries — C's {@code artifact->brands}, which is a boolean array indexed
     * by brand and is {@code NULL} when there are none. The members are shared registry entries.
     *
     * <p>Field brands coded before 260827, commented in full on 261008.
     */
    private Set<Brand> brands;
    /**
     * Slays the artifact carries — C's {@code artifact->slays}, a boolean array indexed by slay
     * and {@code NULL} when there are none. The members are shared registry entries.
     *
     * <p>Field slays coded before 260827, commented in full on 261008.
     */
    private Set<Slay> slays;
    /**
     * Curses the artifact carries, each with its instance data — C's {@code artifact->curses}, an
     * array of curse powers indexed by curse.
     *
     * <p>Field curses coded before 260827, commented in full on 261008.
     */
    private Map<Curse, CurseData> curses;

    /**
     * The artifact's difficulty level — C's {@code artifact->level}, which C documents as the
     * "difficulty level for activation". It is the figure {@code get_use_device_chance} reads as the
     * item level when working out a device fail rate. It is not a generation depth; that is
     * {@link #allocMin} and {@link #allocMax}.
     *
     * <p>Field level coded before 260827, commented in full on 261008.
     */
    private int level;

    /**
     * Chance of being generated, out of 100 — C's {@code artifact->alloc_prob}, the "rarity roll"
     * made once an artifact is otherwise eligible.
     *
     * <p>Field allocProb coded before 260827, commented in full on 261008.
     */
    private int allocProb;
    /**
     * Minimum generation depth — C's {@code artifact->alloc_min}. This is a loose bound: the
     * artifact can appear shallower, but only after an out-of-depth roll that gets harder the
     * further above this depth the player is.
     *
     * <p>Field allocMin coded before 260827, commented in full on 261008.
     */
    private int allocMin;
    /**
     * Maximum generation depth — C's {@code artifact->alloc_max}. Strict: the artifact is never
     * generated deeper than this.
     *
     * <p>Field allocMax coded before 260827, commented in full on 261008.
     */
    private int allocMax;

    /**
     * The artifact's activation, or {@code null} — C's {@code artifact->activation}. For a special
     * light C stores the activation and recharge time on the base kind instead, so this stays
     * {@code null} for those artifacts.
     *
     * <p>Field activation coded before 260827, commented in full on 261008.
     */
    private Activation activation;
    /**
     * Message shown when the activation is used, or {@code null} — C's {@code artifact->alt_msg}.
     * C's activation code uses it in place of the activation's own message when it is set.
     *
     * <p>Field activationMessage coded before 260827, commented in full on 261008.
     */
    private String activationMessage;

    /**
     * Recharge time for the activation — C's {@code artifact->time}, a {@code random_value}.
     *
     * <p>Field time coded before 260827, commented in full on 261008.
     */
    private Random time;

    /**
     * The mutable play-time state of this artifact (created, seen, ever seen) — the slot of C's
     * parallel {@code aup_info} array that {@code artifact->aidx} would index. Created fresh by
     * the constructor, so every artifact starts with all three flags clear.
     *
     * <p>Field aupInfo coded on 260902, commented in full on 261008.
     */
    private ArtifactUpkeep aupInfo;

    /**
     * Build an artifact from its parsed {@code artifact.txt} fields.
     *
     * <p>Every collection argument is <b>stored, not copied</b>. The artifacts are loaded once into
     * the registry and read from there, so each artifact owns the maps and sets the assembler built
     * for it and nothing else holds a reference; {@link #copy()} is what callers use when they need
     * an independent one.
     *
     * <p>The artifact's {@link ArtifactUpkeep} is not a parameter: the constructor attaches a
     * fresh one, with created, seen and ever-seen all clear, as C's zero-initialized
     * {@code aup_info} slot starts. {@link #copy()} therefore resets it rather than carrying it
     * across.
     *
     * <p>Constructor Artifact coded before 260827, upkeep attached on 260902, commented in full on
     * 261008.
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
        this.aupInfo = new ArtifactUpkeep();
    }

    /**
     * Reads the artifact's display name, C's {@code artifact->name}.
     *
     * <p>Function getName coded before 260827, commented in full on 261008.
     *
     * @return the artifact's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Reads the artifact's flavour text, C's {@code artifact->text}.
     *
     * <p>Function getText coded before 260827, commented in full on 261008.
     *
     * @return the artifact's flavour/description text
     */
    public String getText() {
        return text;
    }

    /**
     * Reads the base item type the artifact is built on, C's {@code artifact->tval}.
     *
     * <p>Function gettValue coded before 260827, commented in full on 261008.
     *
     * @return the base type (tval) of the item the artifact is built on
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * Reads the base sub-type the artifact is built on, as a name rather than C's numeric
     * {@code artifact->sval}.
     *
     * <p>Function getsValue coded before 260827, commented in full on 261008.
     *
     * @return the subtype name (sval) of the item the artifact is built on
     */
    public String getsValue() {
        return sValue;
    }

    /**
     * Reads the to-hit bonus, C's {@code artifact->to_h}.
     *
     * <p>Function getToHit coded before 260827, commented in full on 261008.
     *
     * @return the to-hit combat bonus
     */
    public int getToHit() {
        return toHit;
    }

    /**
     * Reads the to-damage bonus, C's {@code artifact->to_d}.
     *
     * <p>Function getToDam coded before 260827, commented in full on 261008.
     *
     * @return the to-damage combat bonus
     */
    public int getToDam() {
        return toDam;
    }

    /**
     * Reads the to-armour-class bonus, C's {@code artifact->to_a}.
     *
     * <p>Function getToAC coded before 260827, commented in full on 261008.
     *
     * @return the to-armour-class combat bonus
     */
    public int getToAC() {
        return toAC;
    }

    /**
     * Reads the base armour class, C's {@code artifact->ac}.
     *
     * <p>Function getAc coded before 260827, commented in full on 261008.
     *
     * @return the base armour class
     */
    public int getAc() {
        return ac;
    }

    /**
     * Reads the damage dice as the single expression the data file gives; C holds it split as
     * {@code artifact->dd} and {@code artifact->ds}.
     *
     * <p>Function getDiceString coded before 260827, commented in full on 261008.
     *
     * @return the unparsed damage dice string for the artifact's weapon
     */
    public String getDiceString() {
        return diceString;
    }

    /**
     * Reads the weight in tenths of a pound, C's {@code artifact->weight}.
     *
     * <p>Function getWeight coded before 260827, commented in full on 261008.
     *
     * @return the artifact's weight (in tenths of a pound)
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Reads the pseudo-worth in gold, C's {@code artifact->cost}.
     *
     * <p>Function getCost coded before 260827, commented in full on 261008.
     *
     * @return the artifact's base monetary value
     */
    public int getCost() {
        return cost;
    }

    /**
     * Reads the object flags the artifact grants, C's {@code artifact->flags}. The live set is
     * returned, not a copy.
     *
     * <p>Function getFlags coded before 260827, commented in full on 261008.
     *
     * @return the object flags the artifact grants
     */
    public Flag<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * Reads the numeric modifiers the artifact grants, C's {@code artifact->modifiers[]}. The live
     * map is returned, not a copy.
     *
     * <p>Function getModifiers coded before 260827, commented in full on 261008.
     *
     * @return the additive numeric modifiers the artifact grants (obj_mods)
     */
    public Map<ObjectModifier, Integer> getModifiers() {
        return modifiers;
    }

    /**
     * Reads the per-element resistance levels and flags, C's {@code artifact->el_info[]}. The live
     * map is returned, not a copy.
     *
     * <p>Function getElInfo coded before 260827, commented in full on 261008.
     *
     * @return the per-element resistance levels and hates/ignores flags the artifact imposes
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return elInfo;
    }

    /**
     * Reads the brands the artifact carries, C's {@code artifact->brands}. The live set is
     * returned, not a copy.
     *
     * <p>Function getBrands coded before 260827, commented in full on 261008.
     *
     * @return the brands the artifact adds to its attacks
     */
    public Set<Brand> getBrands() {
        return brands;
    }

    /**
     * Reads the slays the artifact carries, C's {@code artifact->slays}. The live set is returned,
     * not a copy.
     *
     * <p>Function getSlays coded before 260827, commented in full on 261008.
     *
     * @return the slays the artifact adds to its attacks
     */
    public Set<Slay> getSlays() {
        return slays;
    }

    /**
     * Reads the curses the artifact carries, C's {@code artifact->curses}. The live map is
     * returned, not a copy.
     *
     * <p>Function getCurses coded before 260827, commented in full on 261008.
     *
     * @return the curses attached to the artifact, keyed by curse
     */
    public Map<Curse, CurseData> getCurses() {
        return curses;
    }

    /**
     * Reads the artifact's difficulty level, C's {@code artifact->level}: the item level used for
     * device fail rates, not a generation depth (see {@link #getAllocMin()} and
     * {@link #getAllocMax()}).
     *
     * <p>Function getLevel coded before 260827, commented in full on 261008.
     *
     * @return the artifact's difficulty level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Reads the chance, out of 100, that an otherwise eligible artifact is generated — C's
     * {@code artifact->alloc_prob}.
     *
     * <p>Function getAllocProb coded before 260827, commented in full on 261008.
     *
     * @return the allocation probability used when generating this artifact
     */
    public int getAllocProb() {
        return allocProb;
    }

    /**
     * Reads the loose minimum generation depth, C's {@code artifact->alloc_min}; shallower
     * generation is possible but needs an out-of-depth roll.
     *
     * <p>Function getAllocMin coded before 260827, commented in full on 261008.
     *
     * @return the depth below which generation becomes an out-of-depth roll
     */
    public int getAllocMin() {
        return allocMin;
    }

    /**
     * Reads the strict maximum generation depth, C's {@code artifact->alloc_max}.
     *
     * <p>Function getAllocMax coded before 260827, commented in full on 261008.
     *
     * @return the maximum depth at which the artifact may be generated
     */
    public int getAllocMax() {
        return allocMax;
    }

    /**
     * Reads the artifact's activation, C's {@code artifact->activation}. {@code null} means none
     * on the artifact itself; C then falls back to the base kind's activation when it builds the
     * object, and for special lights the activation lives only on the kind.
     *
     * <p>Function getActivation coded before 260827, commented in full on 261008.
     *
     * @return the artifact's activation effect, or {@code null} if it has none
     */
    public Activation getActivation() {
        return activation;
    }

    /**
     * Reads the message shown when the artifact is activated, C's {@code artifact->alt_msg};
     * {@code null} when the data file gave none.
     *
     * <p>Function getActivationMessage coded before 260827, commented in full on 261008.
     *
     * @return the message shown when the artifact is activated, or {@code null}
     */
    public String getActivationMessage() {
        return activationMessage;
    }

    /**
     * Reads the recharge time of the activation, C's {@code artifact->time}.
     *
     * <p>Function getTime coded before 260827, commented in full on 261008.
     *
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
     * <p>The activation is deep-copied even though C shares the registry's {@code activation}
     * pointer between every object built from the artifact; the port treats the copy as wholly its
     * own. The activation message is a {@link String} and is passed straight through. A
     * {@code null} activation stays {@code null} and a {@code null} message stays {@code null}; a
     * {@code null} recharge time becomes a zero {@link Random}, since C's {@code random_value}
     * is a plain struct with no null state.
     *
     * <p>The copy gets a fresh {@link ArtifactUpkeep} from the constructor: created, seen and
     * ever-seen are <em>not</em> carried across. C has no artifact copy to say otherwise.
     *
     * <p>The locals exist to make that division legible at the call to the constructor rather than
     * for any technical reason.
     *
     * <p>Function copy coded on 260827, activation and message carried across on 261008, commented
     * in full on 261008.
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
        Activation newActivation = null;
        if (activation != null) {
            newActivation = activation.copy();
        }
        String newActivationMessage = null;
        if (activationMessage != null) {
            newActivationMessage = activationMessage;
        }
        Random newTime = Random.Zero();
        if (time != null)
            newTime = time.copy();

        Artifact copyArtifact = new Artifact(newName, newText, newtValue, newsValue, newtoHit,
                newtoDam, newtoAC, newac, newdiceString, newweight, newcost,
                oFlags, newModifiers, newElInfo, newBrands, newSlays,
                newCurses, newLevel, newAllocProb, newAllocMin, newAllocMax,
                newActivation, newActivationMessage, newTime);
        
        return copyArtifact;
    }
    
    /**
     * Replaces this artifact's play-time state wholesale. Private and without callers: the
     * constructor attaches the upkeep and {@link #getAup()} hands out the same instance for the
     * mutators to change, so nothing needs to swap it.
     *
     * <p>Function setArtifactUpkeep coded on 260902, commented in full on 261008.
     *
     * @param artifactUpkeep the upkeep to attach
     */
    private void setArtifactUpkeep(ArtifactUpkeep artifactUpkeep) {
        this.aupInfo = artifactUpkeep;
    }

    /**
     * Reads this artifact's play-time state — what C reaches as {@code aup_info[art->aidx]}. The
     * live instance is returned, so the setters on it change the artifact's own state; this is how
     * the artifact markers in {@code ObjectUtils} record created and seen.
     *
     * <p>Function getAup coded on 260902, commented in full on 261008.
     *
     * @return this artifact's {@link ArtifactUpkeep}
     */
    public ArtifactUpkeep getAup() {
        return aupInfo;
    }
}
