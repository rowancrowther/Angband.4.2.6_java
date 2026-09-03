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

import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.*;

/**
 * The template for a kind of object (as loaded from {@code object.txt}) — e.g.
 * "Wooden Torch" or "Long Sword": its base type, combat bonuses and dice,
 * weight/cost, flags/modifiers/element info, brands/slays/curses, allocation
 * parameters, activations/effects, flavour and the player's awareness state. Live
 * items ({@link ItemObject}) reference an {@code ObjectKind}. This is the Java
 * port of the C original's {@code struct object_kind} ({@code src/object.h}).
 *
 * @author Rowan Crowther
 */
public class ObjectKind {
    /**
     * The kind's name.
     */
    private String name;
    /**
     * Flavour/description text.
     */
    private String text;

    /**
     * The base type this kind belongs to.
     */
    private ObjectBase base;
    /**
     * Index of this kind in the global kind table.
     */
    private int kindIndex;

    /**
     * The item type value (tval).
     */
    private TValue tValue;
    /**
     * The sub-type by name — the kind's name with the object-name flavour markers stripped
     * ({@link #stripToRawSval}). This is the human-readable reference the data files use; the numeric
     * {@link #sVal} is the resolved index. Kept separate because C's sval is always an int at runtime
     * but a name-or-digit reference in the data files (see {@code lookup_sval}).
     */
    private String sValueName;

    /**
     * The resolved numeric sub-type value (sval), assigned when the kind is registered under its
     * base (see {@link ObjectRegistry#addObjectKind}).
     */
    private int sVal;

    /**
     * Extra parameter value (the item's "pval"), as a dice expression.
     */
    private Random pVal; // Item extra parameter

    /**
     * Base to-hit bonus, as a dice expression.
     */
    private Random toH;
    /**
     * Base to-damage bonus, as a dice expression.
     */
    private Random toD;
    /**
     * Base to-armour-class bonus, as a dice expression.
     */
    private Random toA;

    /**
     * Base armour class.
     */
    private int ac;
    /**
     * Base damage, as a dice expression.
     */
    private Random baseDamage;
    /**
     * Number of damage dice.
     */
    private int damageDice;
    /**
     * Sides per damage die.
     */
    private int damageSides;
    /**
     * Base weight.
     */
    private int weight;

    /**
     * Base cost/value.
     */
    private int cost;

    /**
     * Object flags this kind grants.
     */
    private Flag<ObjectFlag> flags;
    /**
     * Kind flags controlling generation/display.
     */
    private Flag<ObjectKindFlag> kindFlags;

    /**
     * Numeric modifiers granted, keyed by modifier, as dice expressions.
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * Per-element relation info (resist/ignore/etc.).
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * Brands every item of this kind carries — C's {@code kind->brands}. A set, because membership
     * is the whole of the state; C indexes an array by registry position and stores a bare boolean.
     *
     * <p>Field brands commented in full on 260817.
     */
    private Set<Brand> brands;
    /**
     * Slays every item of this kind carries — C's {@code kind->slays}. As {@link #brands}.
     *
     * <p>Field slays commented in full on 260817.
     */
    private Set<Slay> slays;
    /**
     * Curses every item of this kind carries, each with the {@link CurseData} the kind prescribes —
     * a power from {@code object.txt} and a timeout of zero, the timeout being rolled per item
     * rather than stated by the template.
     *
     * <p>Copied on the way in and on the way out to items, never shared: the data is mutable and an
     * item's curse ticks its own timeout down, so a shared instance would let one cursed sword
     * count down the template every other sword is made from.
     *
     * <p>Field curses commented in full on 260817.
     */
    private Map<Curse, CurseData> curses;

    /**
     * The display glyph and colour.
     */
    private AngbandDisplayCharacter character;

    /**
     * Allocation probability weight.
     */
    private int alloc_prob;
    /**
     * Minimum depth at which the kind is allocated.
     */
    private int alloc_min;
    /**
     * Maximum depth at which the kind is allocated.
     */
    private int alloc_max;
    /**
     * The kind's native level.
     */
    private int level;

    /**
     * Activations this kind provides.
     */
    private List<Activation> activations;
    /**
     * Effects this kind produces when used.
     */
    private List<Effect> effect;
    /**
     * The kind's power rating.
     */
    private int power;
    /**
     * Message shown when the kind's effect is used.
     */
    private String effectMessage;
    /**
     * Message shown when the effect is seen.
     */
    private String visMessage;
    /**
     * Recharge/effect timing, as a dice expression.
     */
    private Random time;
    /**
     * Charge count (for wands/staves), as a dice expression.
     */
    private Random charge;

    /**
     * Probability used when generating multiple of this kind.
     */
    private int genMultProb;
    /**
     * Stack size when generated, as a dice expression.
     */
    private Random stackSize;

    /**
     * The randomised flavour for unidentified instances.
     */
    private Flavour flavour;

    /**
     * Inscription note used once the kind is identified.
     */
    private String noteAware;
    /**
     * Inscription note used while the kind is unidentified.
     */
    private String noteUnaware;

    /**
     * Whether the player is aware of (has identified) this kind.
     */
    private boolean aware;
    /**
     * Whether the player has tried this kind.
     */
    private boolean tried;

    /**
     * The player's ignore setting for this kind.
     */
    private Flag<IgnoreFlag> ignore;
    
    /**
     * Whether the player has ever seen this kind.
     */
    private boolean everseen;

    /**
     * Whether this kind exists solely to back one special (instanced) artifact.
     *
     * <p>C asks the question by position — {@code kidx >= z_info->ordinary_kind_max} — because it
     * appends the synthesised artifact kinds after the ordinary ones and can then read the answer
     * off the index. The port records it instead, so that nothing depends on where a kind sits in
     * the table; the artifact constructor sets it, and the other three clear it.
     *
     * <p>What turns on it: a special artifact is its own item rather than a template many items
     * share, so there is nothing about it left to be unsure of once it is in hand.
     * {@code PlayerKnowledge.knowObject} makes the player aware of a
     * non-jewellery special artifact outright rather than waiting for its runes to be read.
     *
     * <p>Field isSpecialArtifactKind coded before 260817, commented in full on 260817.
     */
    private boolean isSpecialArtifactKind;

    /**
     * Build an empty object kind with fresh collections.
     */
    public ObjectKind() {
        elInfo = new HashMap<>();
        kindFlags = new Flag<>(ObjectKindFlag.class);
        flags = new Flag<>(ObjectFlag.class);
        activations = new ArrayList<>();
        effect = new ArrayList<>();
        brands = new HashSet<>();
        slays = new HashSet<>();
        curses = new HashMap<>();
        ignore = new Flag<>(IgnoreFlag.class);
        isSpecialArtifactKind = false;
    }

    /**
     * Set the kind's display glyph/colour.
     *
     * @param character the display character
     */
    public void setCharacter(AngbandDisplayCharacter character) {
        this.character = character;
    }

    /**
     * Build a partly-specified object kind (used for store/dungeon kinds), seeding
     * default damage/weight and, for dungeon kinds, marking all elements ignored
     * and the kind as "good".
     *
     * @param adc       display character
     * @param cost      base cost
     * @param level     native level
     * @param min       minimum allocation depth
     * @param max       maximum allocation depth
     * @param name      kind name
     * @param tvalue    item type value
     * @param sValueName    sub-type value
     * @param base      base type
     * @param isDungeon whether this is a dungeon-generated kind
     */
    public ObjectKind(AngbandDisplayCharacter adc, int cost,
                      int level, int min, int max,
                      String name, TValue tvalue, String sValueName,
                      ObjectBase base, boolean isDungeon
    ) {
        this.name = name;
        this.character = adc;
        this.damageDice = 1;
        this.damageSides = 1;
        this.weight = 30;
        this.cost = cost;
        this.level = level;
        this.alloc_min = min;
        this.alloc_max = max;
        this.tValue = tvalue;
        this.sValueName = sValueName;
        this.base = base;

        elInfo = new HashMap<>();
        kindFlags = new Flag<>(ObjectKindFlag.class);
        if (isDungeon) {
            for (ElementEnum ee : ElementEnum.values()) {
                if (ee.isBase()) {
                    ElementInfo ei = new ElementInfo();
                    ei.on(ElementInfoEnum.EL_INFO_IGNORE);
                    elInfo.put(ee, ei);

                    kindFlags.on(ObjectKindFlag.KF_GOOD);
                }
            }
        }

        modifiers = new HashMap<>();
        flags = new Flag<>(ObjectFlag.class);
        brands = new HashSet<>();
        slays = new HashSet<>();
        curses = new HashMap<>();
        activations = new ArrayList<>();
        effect = new ArrayList<>();
        isSpecialArtifactKind = false;
    }

    /**
     * Build a fully-specified object kind from parsed data-file fields, resolving
     * the various dice strings into {@link Random}s and copying the brand/slay/
     * curse maps.
     *
     * @param name          kind name
     * @param text          flavour text
     * @param base          base type
     * @param kindIndex     index in the kind table
     * @param pVal          extra-parameter dice string
     * @param toH           to-hit dice string
     * @param toD           to-damage dice string
     * @param toA           to-AC dice string
     * @param ac            base armour class
     * @param baseDamage    base damage dice string
     * @param damageDice    number of damage dice
     * @param damageSides   sides per damage die
     * @param weight        base weight
     * @param cost          base cost
     * @param flags         object flags
     * @param kindFlags     kind flags
     * @param modifiers     modifier dice strings by modifier
     * @param elInfo        per-element info
     * @param brands        brands (intrinsic flag)
     * @param slays         slays (intrinsic flag)
     * @param curses        curses (intrinsic flag)
     * @param character     display character
     * @param alloc_prob    allocation probability
     * @param alloc_min     minimum allocation depth
     * @param alloc_max     maximum allocation depth
     * @param level         native level
     * @param activations   activations
     * @param effect        effects
     * @param effectMessage effect message
     * @param visMessage    seen-effect message
     * @param time          timing dice string
     * @param charge        charge dice string
     * @param genMultProb   multi-generation probability
     * @param stackSize     stack-size dice string
     * @param flavour       unidentified flavour
     * @param noteAware     identified inscription
     * @param noteUnaware   unidentified inscription
     * @param aware         whether the player is aware of the kind
     * @param tried         whether the player has tried the kind
     * @param ignore        ignore setting
     * @param everseen      whether ever seen
     * @param tValue        item type value
     */
    public ObjectKind(String name, String text, ObjectBase base,
                      int kindIndex, Random pVal, Random toH,
                      Random toD, Random toA, int ac, Random baseDamage,
                      int damageDice, int damageSides,
                      int weight, int cost,
                      Flag<ObjectFlag> flags,
                      Flag<ObjectKindFlag> kindFlags,
                      Map<ObjectModifier, Random> modifiers,
                      Map<ElementEnum, ElementInfo> elInfo,
                      Set<Brand> brands, Set<Slay> slays,
                      Map<Curse, CurseData> curses,
                      AngbandDisplayCharacter character,
                      int alloc_prob, int alloc_min,
                      int alloc_max, int level,
                      List<Activation> activations,
                      List<Effect> effect, String effectMessage,
                      String visMessage, String time,
                      Random charge, int genMultProb,
                      Random stackSize, Flavour flavour,
                      String noteAware, String noteUnaware,
                      boolean aware, boolean tried,
                      Flag<IgnoreFlag> ignore, boolean everseen, TValue tValue) {
        this.name = name;
        this.text = text;
        this.base = base;
        this.kindIndex = kindIndex;
        this.pVal = pVal;
        this.toH = toH;
        this.toD = toD;
        this.toA = toA;
        this.ac = ac;
        this.baseDamage = baseDamage;
        this.damageDice = damageDice;
        this.damageSides = damageSides;
        this.weight = weight;
        this.cost = cost;
        this.flags = flags;
        this.kindFlags = kindFlags;
        this.modifiers = modifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = new HashMap<>();
        for (Curse curse : curses.keySet()) {
            this.curses.put(curse, new CurseData(curses.get(curse)));
        }
        this.character = character;
        this.alloc_prob = alloc_prob;
        this.alloc_min = alloc_min;
        this.alloc_max = alloc_max;
        this.level = level;
        this.activations = activations;
        this.effect = effect;
        this.effectMessage = effectMessage;
        this.visMessage = visMessage;
        this.time = Random.parseStr(time);
        this.charge = charge;
        this.genMultProb = genMultProb;
        this.stackSize = stackSize;
        this.flavour = flavour;
        this.noteAware = noteAware;
        this.noteUnaware = noteUnaware;
        this.aware = aware;
        this.tried = tried;
        this.ignore = ignore;
        this.everseen = everseen;
        this.tValue = tValue;
        this.sValueName = stripToRawSval(name);
        this.isSpecialArtifactKind = false;
    }

    /**
     * Synthesises the object kind that backs a special (instanced) artifact — the port of C's
     * {@code write_special_kinds}/{@code special_item} handling. An artifact whose base has no
     * ordinary kind gets a fresh kind built here: it copies the base's kind-flags and per-element
     * info, marks itself {@link ObjectKindFlag#KF_INSTA_ART}, takes the artifact's level, and adopts
     * a red {@code '*'} display glyph and a flavour-templated name derived from {@code sValName}.
     *
     * @param artifact the artifact this kind is being created for
     * @param sValName the subtype name to give the synthesised kind
     * @param base     the object base whose defaults (kind-flags, elements, tval) are inherited
     */
    public ObjectKind(Artifact artifact, String sValName, ObjectBase base) {
        this.flags = new Flag<>(ObjectFlag.class);
        Flag<ObjectKindFlag> copy = new Flag<>(ObjectKindFlag.class);
        copy.copyFrom(base.getKindFlags());
        this.kindFlags = copy;
        this.modifiers = new HashMap<>();
        this.elInfo = new HashMap<>();
        this.brands = new HashSet<>();
        this.slays = new HashSet<>();
        this.curses = new HashMap<>();
        this.activations = new ArrayList<>();
        this.effect = new ArrayList<>();
        this.sValueName = sValName;
        this.name = "& " + sValName + "~";
        this.tValue = base.gettVal();
        this.level = artifact.getLevel();
        this.ignore = new Flag<>(IgnoreFlag.class);
        this.kindFlags.on(ObjectKindFlag.KF_INSTA_ART);
        for (ElementEnum ee : base.getElementMap().keySet()) {
            ElementInfo oldEi = base.getElementMap().get(ee);
            ElementInfo newEi = oldEi.copy();
            this.elInfo.put(ee, newEi);
        }

        this.character = new AngbandDisplayCharacter('*', ColourEnum.COLOUR_RED);

        this.base = base;
        this.isSpecialArtifactKind = true;
    }

    /**
     * Strips the object-name flavour-template markers ({@code "& "} article slot and {@code "~"}
     * pluralisation slot) from a kind's name to recover the bare sval reference used elsewhere.
     *
     * @param name the templated kind name
     * @return the name with the {@code &}/{@code ~} markers removed
     */
    private String stripToRawSval(String name) {
        return name.replace("& ", "").replace("~", "");
    }

    /**
     * @return the resolved numeric sub-type value (sval)
     */
    public int getsVal() {
        return sVal;
    }

    /**
     * Set the resolved numeric sval; called when the kind is registered under its base.
     *
     * @param sVal the sval to assign
     */
    public void setsVal(int sVal) {
        this.sVal = sVal;
    }

    /**
     * @return the kind's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the kind's base type
     */
    public ObjectBase getBase() {
        return base;
    }

    /**
     * @param alloc_prob the allocation probability weight
     */
    public void setAlloc_prob(int alloc_prob) {
        this.alloc_prob = alloc_prob;
    }

    /**
     * @param alloc_min the minimum allocation depth
     */
    public void setAlloc_min(int alloc_min) {
        this.alloc_min = alloc_min;
    }

    /**
     * @param alloc_max the maximum allocation depth
     */
    public void setAlloc_max(int alloc_max) {
        this.alloc_max = alloc_max;
    }

    /**
     * @param cost the base cost/value
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * @param weight the kind's weight (in tenths of a pound)
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * @return the activations available on this kind
     */
    public List<Activation> getActivations() {
        return activations;
    }

    /**
     * @param time the recharge/effect timing dice to assign
     */
    public void setTime(Random time) {
        this.time = time;
    }

    /**
     * @return the item type value (tval)
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * @return the sub-type by name (the flavour-stripped kind name)
     */
    public String getsValueName() {
        return sValueName;
    }

    /**
     * @return the kind-level flags ({@code KF_*}) set on this kind
     */
    public FlagView<ObjectKindFlag> getKindFlags() {
        return kindFlags;
    }

    /**
     * @return this kind's stable index in the object-kind table
     */
    public int getKindIndex() {
        return kindIndex;
    }

    /**
     * @param kindIndex this kind's stable index in the object-kind table
     */
    public void setKindIndex(int kindIndex) {
        this.kindIndex = kindIndex;
    }

    /**
     * Returns the dice this kind's to-hit bonus is rolled from, C's {@code kind->to_h}.
     *
     * <p>Dice, not a number: this is the recipe every item of this kind is made to, and the figure
     * an individual item ended up with lives on that item instead. The distinction is the whole
     * point of {@link ItemObject#hasStandardToH}, which is currently the only caller — it compares
     * an item's settled to-hit against {@link Random#getBase} here to decide whether the item has
     * drifted from what its kind prescribes.
     *
     * <p>Function getToH coded on 260815, commented in full on 260815.
     *
     * @return this kind's to-hit dice
     */
    public Random getToH() {
        return toH;
    }

    /**
     * Returns the to-damage range this kind rolls, the port of reading C's {@code kind->to_d}.
     *
     * <p>A {@link Random}, because it is the recipe rather than a result: {@code object.txt} states
     * the range once and every item generated from the kind rolls its own figure into
     * {@link ItemObject#getToDam}. Not to be mistaken for the kind's damage dice, which are
     * {@code damageDice} and {@code damageSides} and mean something else entirely.
     *
     * <p>Function getToD commented in full on 260816.
     *
     * @return the to-damage range for this kind
     */
    public Random getToD() {
        return toD;
    }

    /**
     * @return this kind's base armour class — C's {@code kind->ac}
     */
    public int getAc() {
        return ac;
    }

    /**
     * Returns the flavour this kind is disguised behind, the port of reading C's
     * {@code kind->flavor}.
     *
     * <p>Null for a kind that has none, and that null is load-bearing rather than incidental: a
     * sword is a sword on sight, while a potion is "a pink potion" until identified. The knowledge
     * code pairs this with {@link #isAware} to decide whether an item's pval and effect can be
     * shown — flavoured-and-aware and unflavoured-non-wearable are the two cases that qualify.
     *
     * <p>Function getFlavour commented in full on 260816.
     *
     * @return this kind's flavour, or {@code null} if it has none
     */
    public Flavour getFlavour() {
        return flavour;
    }

    /**
     * Reports whether the player has identified what this kind is, the port of reading C's
     * {@code kind->aware}.
     *
     * <p>Held on the kind, not on any item, because that is the scope of the discovery: learning
     * that the pink potion is a Potion of Speed is learning it about every pink potion at once. See
     * {@code Player.flavourAware}, which sets it and then puts the
     * rest of the world in step.
     *
     * <p>Function isAware commented in full on 260816.
     *
     * @return {@code true} if the player knows what this kind is
     */
    public boolean isAware() {
        return aware;
    }

    /**
     * Records that the player has identified what this kind is — C's {@code kind->aware = true}.
     *
     * <p>Should generally be reached through
     * {@code Player.flavourAware} rather than called directly:
     * awareness has consequences — the ignore fixup, the pack refresh, the floor redraw — and
     * setting the flag here does none of them.
     *
     * <p>Function setAware commented in full on 260816.
     *
     * @param aware whether the player knows what this kind is
     */
    public void setAware(boolean aware) {
        this.aware = aware;
    }

    /**
     * @return what items of this kind do when used — C's {@code kind->effect}
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * Reports whether an item of this kind has ever been seen identified, the port of reading C's
     * {@code kind->everseen}.
     *
     * <p>Not knowledge but a record of whether the news has been broken, so that recognising a kind
     * for the first time is worth a message and the tenth is not. {@link EgoItem#isEverSeen} is its
     * counterpart for ego types.
     *
     * <p>Function isEverseen commented in full on 260816.
     *
     * @return {@code true} if this kind has been seen identified before
     */
    public boolean isEverseen() {
        return everseen;
    }

    /**
     * Reports whether the player has chosen to ignore this kind while it is still an unidentified
     * flavour, the port of C's {@code kind_is_ignored_unaware}.
     *
     * <p>See {@link IgnoreFlag} for why this is a different question from
     * {@link #isIgnoredAware} and why the two must not be collapsed.
     *
     * <p>Function isIgnoredUnaware commented in full on 260816.
     *
     * @return {@code true} if unidentified items of this kind are ignored
     */
    public boolean isIgnoredUnaware() {
        return ignore.has(IgnoreFlag.IGNORE_IF_UNAWARE);
    }

    /**
     * Sets whether unidentified items of this kind are ignored — C's {@code IGNORE_IF_UNAWARE}.
     *
     * <p>Takes a boolean where C's macro only ever switches the bit on, so this can also clear the
     * choice; nothing in the port does yet, but the player's ignore menu will want to.
     *
     * <p>Function setIgnoredUnaware commented in full on 260816.
     *
     * @param ignoredUnaware whether to ignore unidentified items of this kind
     */
    public void setIgnoredUnaware(boolean ignoredUnaware) {
        if (ignoredUnaware) ignore.on(IgnoreFlag.IGNORE_IF_UNAWARE);
        else ignore.off(IgnoreFlag.IGNORE_IF_UNAWARE);
    }

    /**
     * Reports whether the player has chosen to ignore this kind once they know what it is, the port
     * of reading C's {@code IGNORE_IF_AWARE}.
     *
     * <p>Function isIgnoredAware commented in full on 260816.
     *
     * @return {@code true} if identified items of this kind are ignored
     */
    public boolean isIgnoredAware() {
        return ignore.has(IgnoreFlag.IGNORE_IF_AWARE);
    }

    /**
     * Sets whether identified items of this kind are ignored, the port of C's
     * {@code kind_ignore_when_aware}.
     *
     * <p>Called by {@code Player.flavourAware} to carry a standing
     * decision across the moment of identification: a player who was ignoring unknown potions is
     * taken to be ignoring this one, so the pile they were stepping over does not reappear under a
     * name. See {@link IgnoreFlag}.
     *
     * <p>Function setIgnoredAware commented in full on 260816.
     *
     * @param ignoredAware whether to ignore identified items of this kind
     */
    public void setIgnoredAware(boolean ignoredAware) {
        if (ignoredAware) ignore.on(IgnoreFlag.IGNORE_IF_AWARE);
        else ignore.off(IgnoreFlag.IGNORE_IF_AWARE);
    }

    /**
     * Reports whether this kind sits above {@code z_info->ordinary_kind_max} — the special-artifact
     * range, the port of C's {@code obj->kind->kidx >= z_info->ordinary_kind_max} test.
     *
     * <p>A kind in that range is its own artifact rather than a template many items share, so there
     * is nothing to be unsure of once it is in hand: {@code knowObject} makes the player aware of a
     * non-jewellery special artifact outright rather than waiting for its runes to be read.
     *
     * <p>Function isSpecialArtifactKind commented in full on 260816.
     *
     * @return {@code true} if this kind is a special artifact
     */
    public boolean isSpecialArtifactKind() {
        return isSpecialArtifactKind;
    }

    /**
     * Returns the number of damage dice items of this kind roll, the port of reading C's
     * {@code kind->dd}.
     *
     * <p>A plain count, not a range — unlike {@link #getToD} next door, which is a {@link Random}
     * because it is rolled per item. Every Long Sword has the same {@code 2d5}; what differs between
     * two of them is the bonus on top. That is why {@code object_set_base_known} can copy this onto
     * a counterpart the moment the kind is recognised: knowing what the item <em>is</em> settles its
     * dice, while its enchantment still has to be learned.
     *
     * <p>Not to be confused with the kind's {@code toD}, whose {@code getDice()} is the dice of the
     * to-damage <em>range</em> and means something else entirely.
     *
     * <p>Function getDamageDice coded on 260816, commented in full on 260816.
     *
     * @return the number of damage dice for this kind
     */
    public int getDamageDice() {
        return damageDice;
    }

    /**
     * Returns the sides per damage die for items of this kind, the port of reading C's
     * {@code kind->ds}. See {@link #getDamageDice} for why this is a count and not a range.
     *
     * <p>Function getDamageSides coded on 260816, commented in full on 260816.
     *
     * @return the sides per damage die for this kind
     */
    public int getDamageSides() {
        return damageSides;
    }

    /**
     * The flags every object of this kind carries — C's {@code kind->flags}, the {@code flags:} line
     * in {@code object.txt}.
     *
     * <p>These are the kind's flags, not an object's. An object gets its own copy at
     * {@code object_prep}, and thereafter the two can differ. The kind's set is consulted again only
     * for a flavoured object the player has become aware of: {@code object_flags_known} folds it back
     * in once awareness makes the kind's properties public knowledge
     * ({@code obj-util.c:371-373}).
     *
     * <p>Function getFlags commented in full on 260820.
     *
     * @return a read-only view of this kind's flags
     */
    public FlagView<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * @return the to-armour bonus this kind rolls, shared with this instance - C's
     * {@code kind->to_a}. Compared against an item's own to-armour by the ignore code, which
     * is how an item is judged good, bad or average for its type
     */
    public Random getToA() {
        return toA;
    }

    /**
     * Returns an independent copy of this object kind.
     *
     * <p>Deep-copied because their contents are mutable: every {@link uk.co.jackoftradesltd.middle.numerics.Random}
     * term, the two flag sets and the ignore flags, the modifier map (each value copied in turn),
     * the element info (each entry copied), the curse map (each {@code CurseData} rebuilt), the
     * display character, the activation and effect lists, the flavour, and the stack-size and charge
     * dice.
     *
     * <p>Shared deliberately: {@link #base} - the comment at that line says why, one immutable base
     * serves many kinds - and the members of the brand and slay sets, which are immutable registry
     * entries every carrier points at. C shares the same pointers.
     *
     * <p>Built member by member on a fresh instance rather than through a constructor, because the
     * kind has more fields than any constructor takes. That is also why the collections are cleared
     * or added into rather than assigned: the no-argument constructor has already given the copy
     * empty ones.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new object kind that shares no mutable state with this one, bar the base and the
     *         brand and slay members
     */
    public ObjectKind copy() {
        ObjectKind copy = new ObjectKind();
        copy.name = this.name;
        copy.text = this.text;
        copy.base = this.base; // Bases are immutable, so one base can have many kinds
        copy.kindIndex = this.kindIndex;
        copy.tValue = this.tValue;
        copy.sValueName = this.sValueName;
        copy.sVal = this.sVal;
        copy.pVal = this.pVal.copy();
        copy.toH = this.toH.copy();
        copy.toD = this.toD.copy();
        copy.toA = this.toA.copy();
        copy.ac = this.ac;
        copy.baseDamage = this.baseDamage.copy();
        copy.damageDice = this.damageDice;
        copy.damageSides = this.damageSides;
        copy.weight = this.weight;
        copy.cost = this.cost;
        Flag<ObjectFlag> oFlag = new Flag<>(ObjectFlag.class);
        oFlag.copyFrom(this.flags);
        copy.flags = oFlag;
        Flag<ObjectKindFlag> kFlag = new Flag<>(ObjectKindFlag.class);
        kFlag.copyFrom(this.kindFlags);
        copy.kindFlags = kFlag;
        Map<ObjectModifier, Random> modMap = new HashMap<>();
        for (ObjectModifier mod : this.modifiers.keySet()) {
            Random newRandom = this.modifiers.get(mod).copy();
            modMap.put(mod, newRandom);
        }
        copy.modifiers = modMap;
        Map<ElementEnum, ElementInfo> newElInfo = new HashMap<>();
        for (ElementEnum ee : this.elInfo.keySet()) {
            ElementInfo ei = this.elInfo.get(ee).copy();
            newElInfo.put(ee, ei);
        }
        copy.elInfo = newElInfo;
        copy.brands.addAll(this.brands);
        copy.slays.addAll(this.slays);
        for (Curse c : this.curses.keySet()) {
            CurseData cd = new CurseData(this.curses.get(c));
            copy.curses.put(c, cd);
        }
        copy.character = new AngbandDisplayCharacter(character.getCharacter(), character.getAttributeColour());
        copy.alloc_prob = this.alloc_prob;
        copy.alloc_min = this.alloc_min;
        copy.alloc_max = this.alloc_max;
        copy.level = this.level;
        copy.activations.clear();
        for (Activation a : this.activations) {
            copy.activations.add(a.copy());
        }
        for (Effect e : this.effect) {
            copy.effect.add(e.copy());
        }
        copy.power = this.power;
        copy.effectMessage = this.effectMessage;
        copy.visMessage = this.visMessage;
        copy.time = this.time.copy();
        copy.charge = this.charge.copy();
        copy.genMultProb = this.genMultProb;
        copy.stackSize = this.stackSize.copy();
        copy.flavour = this.flavour.copy();
        copy.noteAware = this.noteAware;
        copy.noteUnaware = this.noteUnaware;
        copy.aware = this.aware;
        copy.tried = this.tried;
        Flag<IgnoreFlag> iFlag = new Flag<>(IgnoreFlag.class);
        iFlag.copyFrom(this.ignore);
        copy.ignore = iFlag;
        copy.everseen = this.everseen;
        copy.isSpecialArtifactKind = this.isSpecialArtifactKind;

        return copy;
    }

    /**
     * Answers whether the player's class can read this kind as a spell book - the port of C's
     * {@code obj_can_browse} ({@code obj-util.c}).
     *
     * <p>Walks the class's own list of magic books and matches each on both halves of its
     * {@code (tval, sval)} pair, so the same book is browsable by a mage and not by a priest. Both
     * halves are needed: each item type numbers its sub-types from one upwards, so every realm has a
     * book with the same sval and the sub-type alone cannot tell a prayer book from a magic one.
     *
     * <p>Reaches the live player through {@code GameState}, so it answers for whoever is playing
     * rather than taking the player as an argument, unlike its C original.
     *
     * <p>Function canBrowse commented in full on 260827.
     *
     * @return {@code true} if the current player's class can browse this kind
     */
    public boolean canBrowse() {
        for (MagicBook mb : GameState.getPlayer().getPlayerClass().getMagic().getMagicBooks()) {
            if (this.gettValue() == mb.getBookTValue() && this.sVal == mb.getSval())
                return true;
        }

        return false;
    }

    /**
     * @return the power this kind contributes to an object built on it - C's {@code kind->power},
     * which {@code ItemObject.effectsPower} falls back on when the object itself carries no
     * activation
     */
    public int getPower() {
        return power;
    }

    /**
     * @return the base cost of this kind in gold, before any bonus or ego is priced - C's
     * {@code kind->cost}, and the figure the unaware-object valuation returns directly
     */
    public int getCost() {
        return cost;
    }

    /**
     * Records that the player has tried this kind - C's {@code kind->tried}, which the
     * unaware-flavour code checks before offering a flavour's inferred name.
     *
     * <p>This is the raw field-set only. C also has
     * {@code object_flavor_tried}, a wrapper that guards artifact kinds out before
     * calling this same assignment - that guard is not reproduced here.
     *
     * <p>Function setTried commented in full on 260903.
     *
     * @param tried whether the player has tried this kind
     */
    public void setTried(boolean tried) {
        this.tried = tried;
    }
}
