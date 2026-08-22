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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.enums.DamageAspect;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.utils.NumberUtils;

import java.util.*;

import static uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum.ORIGIN_MIXED;

/**
 * A concrete item instance in the game — a specific sword, potion, etc. — as
 * opposed to its {@link ObjectKind} template. It records the kind plus any
 * {@link EgoItem}/{@link Artifact}, the player's known view of it, its location,
 * combat values, flags/modifiers/element-info, brands/slays/curses, stack count,
 * notice flags, origin and inscription. The methods implement the rules for when
 * two items are "similar" enough to stack and how stacks merge/absorb. This is
 * the Java port of the C original's {@code struct object} ({@code src/object.h}).
 *
 * <p><b>This type is for real items only.</b> C reuses {@code struct object} for two things that
 * are not items: {@code p->obj_k}, the player's accumulated rune knowledge, and the {@code obj}
 * hanging off each curse definition. The first of those is {@link KnownObject} here, because a
 * struct that exists to answer "can the player read to-hit bonuses" has no business carrying a
 * grid, a weight, a timeout, or a {@link #known} pointer to a known version of itself. Nothing in
 * this class is shaped around that second role, so do not press it back into service for it.
 *
 * @author Rowan Crowther
 * @see KnownObject
 */
public class ItemObject {
    /**
     * Logger used to report stack-merge errors.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The object kind this item is an instance of.
     */
    private ObjectKind kind;
    /**
     * The ego type applied to this item, if any.
     */
    private EgoItem ego;
    /**
     * The artifact this item is, if any.
     */
    private Artifact artifact;

    /**
     * The player's known/identified view of this item — a parallel {@code ItemObject} carrying
     * only what has been discovered about this one, so that display code can describe the item as
     * the player sees it without having to ask, field by field, whether each value is readable.
     * C's {@code obj->known}.
     *
     * <p>Filled in by the knowledge code from two sources: this item's real values, and the
     * player's rune knowledge deciding which of them come through. That is where the 0/1
     * multipliers on {@link KnownObject} are spent — C writes {@code obj->known->ac = obj->ac *
     * p->obj_k->ac}, zeroing an unreadable value rather than branching on it.
     *
     * <p>Null on an item the player has never seen. It is not the same object as this one and
     * never points back at it; C's {@code obj_k} having a {@code known} of its own is an artefact
     * of the struct reuse this port drops.
     */
    private ItemObject known;

    /**
     * The grid this item lies on (when on the floor).
     */
    private Loc location;

    /**
     * The item type value (tval).
     */
    private TValue tValue;
    /**
     * The sub-type value (sval).
     */
    private int sValue;

    /**
     * The item's extra parameter value (pval).
     */
    private int pValue;

    /**
     * The item's weight.
     */
    private int weight;

    /**
     * Number of damage dice.
     */
    private int damageDice;
    /**
     * Sides per damage die.
     */
    private int damageSides;
    /**
     * Base damage, as a dice expression.
     */
    private Random baseDamage;
    /**
     * Base armour class.
     */
    private int baseAC;
    /**
     * This item's own to-armour-class bonus — the rolled result, not the dice it came from. C's
     * {@code obj->to_a} ({@code object.h}), an {@code int16_t}.
     *
     * <p>The dice live one level up, on the kind's {@code toA}, because they belong to the
     * recipe rather than to any particular item: {@code object.txt} writes {@code armor:32:0} once
     * and every suit rolled from it gets its own figure. By the time an item exists this is a
     * settled number, so reading it is a plain field access and never a fresh roll.
     *
     * <p>Field toAC coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815.
     */
    private int toAC;
    /**
     * This item's own to-damage bonus, rolled at generation. C's {@code obj->to_d}. See
     * {@link #toAC} for why the instance holds a number and the kind holds dice.
     *
     * <p>Field toDam coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815.
     */
    private int toDam;
    /**
     * This item's own to-hit bonus, rolled at generation. C's {@code obj->to_h}. See
     * {@link #toAC} for why the instance holds a number and the kind holds dice.
     *
     * <p>Unlike the other two, a non-zero value here does not by itself mean the item is doing
     * anything unusual — body armour carries a to-hit penalty from its kind. {@link #hasStandardToH}
     * is the test that knows the difference.
     *
     * <p>Field toHit coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815.
     */
    private int toHit;

    /**
     * The item's object flags.
     */
    private Flag<ObjectFlag> flags;
    /**
     * The item's numeric modifiers, keyed by modifier — the port of C's {@code obj->modifiers}.
     *
     * <p>Values already rolled for this particular object, not the dice they came from: an ego's
     * {@code +1d4} stealth becomes a {@code 3} here when the object is generated. The dice live on
     * {@link ObjectKind} and {@link EgoItem}, which is what makes recognising an ego by its
     * modifiers a question about ranges rather than about this number.
     *
     * <p>Comment corrected on 260816, when the field's type changed from the unparsed dice text it
     * had previously held.
     */
    private Map<ObjectModifier, Integer> modifiers;
    /**
     * Per-element relation info.
     */
    private Map<ElementEnum, ElementInfo> elInfo;
    /**
     * Brands on the item — C's {@code obj->brands}. A set, because membership is the whole of the
     * state; C indexes an array by registry position and stores a bare boolean.
     *
     * <p>Field brands commented in full on 260817.
     */
    private Set<Brand> brands;
    /**
     * Slays on the item — C's {@code obj->slays}. As {@link #brands}.
     *
     * <p>Field slays commented in full on 260817.
     */
    private Set<Slay> slays;
    /**
     * Curses on the item, each mapped to its per-object {@link CurseData} — the power it has here
     * and the countdown to its next effect. C's {@code obj->curses}.
     *
     * <p>A map holding only the curses the object actually carries, where C keeps an array with a
     * slot for every curse in the game and reads a power of zero as "not cursed with this". The two
     * agree because nothing stores a curse at power zero: absence is the port's way of saying the
     * same thing.
     *
     * <p>Null until the first curse is added, which the accessors absorb rather than pass on —
     * {@link #getCurses()} reports an empty map and the mutators create the map on demand.
     *
     * <p>Field curses retyped from {@code Map<Curse.CurseEntry, Boolean>} on 260817, commented in
     * full on 260817.
     */
    private LinkedHashMap<Curse, CurseData> curses;

    /**
     * Effects this item produces when used.
     */
    private List<Effect> effect;
    /**
     * Message shown when the item's effect fires.
     */
    private String effectMessage;
    /**
     * Activations available on this item.
     */
    private List<Activation> activation;
    /**
     * Recharge time, as a dice expression.
     */
    private Random time;
    /**
     * Turns until the item can be used again (0 = ready).
     */
    private int timeout;

    /**
     * Quantity in this stack.
     */
    private int number;
    /**
     * The player's notice flags for this item (worn/assessed/ignore/imagined).
     */
    private Flag<ObjectNotice> notice;

    /**
     * Index of the monster holding this item, or 0 if not held.
     */
    private int heldMIndex;
    /**
     * Index of the monster mimicking this item, or 0 if none.
     */
    private int mimickingMIndex;

    /**
     * Where this item came from (for the description history line).
     */
    private ObjectOriginEnum origin;
    /**
     * The depth at which the item originated.
     */
    private int originDepth;
    /**
     * The monster race that dropped the item, if applicable.
     */
    private MonsterRace originRace = new MonsterRace();

    /**
     * The player's inscription on the item.
     */
    private String note;

    /**
     * Build an empty item (used as a blank slot/placeholder).
     */
    public ItemObject() {
    }

    /**
     * Build a fully-specified item from its parsed data-file fields, resolving the
     * dice strings into {@link Random}s and copying the curse map.
     *
     * @param kind            object kind
     * @param ego             ego type, if any
     * @param artifact        artifact, if any
     * @param known           known/identified view
     * @param location        floor location
     * @param tValue          item type value
     * @param sValue          sub-type value
     * @param pValue          extra-parameter value (as string)
     * @param weight          weight
     * @param damageDice      number of damage dice
     * @param damageSides     sides per damage die
     * @param normalAC        base armour class
     * @param toAC            this item's rolled to-AC bonus
     * @param baseDamage      base-damage dice string
     * @param toDam           this item's rolled to-damage bonus
     * @param toHit           this item's rolled to-hit bonus
     * @param flags           object flags
     * @param modifiers       modifier dice strings
     * @param elInfo          per-element info
     * @param brands          brands
     * @param slays           slays
     * @param curses          curses
     * @param effect          effects
     * @param effectMessage   effect message
     * @param activation      activations
     * @param time            recharge dice string
     * @param timeout         current cooldown
     * @param number          stack quantity
     * @param notice          notice flags
     * @param heldMIndex      holding-monster index
     * @param mimickingMIndex mimicking-monster index
     * @param origin          origin category
     * @param originDepth     origin depth
     * @param originRace      origin monster race
     * @param note            inscription
     */
    public ItemObject(ObjectKind kind, EgoItem ego,
                      Artifact artifact, ItemObject known,
                      Loc location, TValue tValue, int sValue,
                      String pValue, int weight, int damageDice,
                      int damageSides, int normalAC, int toAC,
                      String baseDamage, int toDam, int toHit,
                      Flag<ObjectFlag> flags,
                      Map<ObjectModifier, Integer> modifiers,
                      Map<ElementEnum, ElementInfo> elInfo,
                      Set<Brand> brands, Set<Slay> slays,
                      LinkedHashMap<Curse, CurseData> curses,
                      List<Effect> effect, String effectMessage,
                      List<Activation> activation, String time,
                      int timeout, int number,
                      Flag<ObjectNotice> notice, int heldMIndex,
                      int mimickingMIndex,
                      ObjectOriginEnum origin, int originDepth,
                      MonsterRace originRace, String note) {
        this.kind = kind;
        this.ego = ego;
        this.artifact = artifact;
        this.known = known;
        this.location = location;
        this.tValue = tValue;
        this.sValue = sValue;
        if (pValue.isEmpty())
            this.pValue = 0;
        else
            this.pValue = Integer.parseInt(pValue);
        this.weight = weight;
        this.damageDice = damageDice;
        this.damageSides = damageSides;
        this.baseAC = normalAC;
        this.toAC = toAC;
        this.baseDamage = Random.parseStr(baseDamage);
        this.toDam = toDam;
        this.toHit = toHit;
        this.flags = flags;
        this.modifiers = modifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = curses;
        this.effect = effect;
        this.effectMessage = effectMessage;
        this.activation = activation;
        this.time = Random.parseStr(time);
        this.timeout = timeout;
        this.number = number;
        this.notice = notice;
        this.heldMIndex = heldMIndex;
        this.mimickingMIndex = mimickingMIndex;
        this.origin = origin;
        this.originDepth = originDepth;
        this.originRace = originRace;
        this.note = note;
    }

    /**
     * Sets the grid location this object occupies on the floor.
     *
     * @param grid the map location, or {@code null} if the object is not on the floor
     */
    public void setGrid(Loc grid) {
        location = grid;
    }

    /**
     * @return the grid location this object occupies, or {@code null} if it is not on the floor
     */
    public Loc getGrid() {
        return location;
    }

    /**
     * Raises a notice flag on this object, recording something the player has learned or noticed
     * about it.
     *
     * @param notice the {@link ObjectNotice} flag to set
     */
    public void orNotice(ObjectNotice notice) {
        this.notice.on(notice);
    }

    /**
     * @return {@code true} if this object is an artifact (has an associated artifact definition)
     */
    public boolean isArtifact() {
        return artifact != null;
    }

    /**
     * @return the object kind (base type) this object is an instance of
     */
    public ObjectKind getKind() {
        return kind;
    }

    /**
     * Sets the index of the monster currently holding this object.
     *
     * @param heldMIndex the holding monster's index (0 if not held by a monster)
     */
    public void setHeldMIndex(int heldMIndex) {
        this.heldMIndex = heldMIndex;
    }

    /**
     * Sets the index of the monster this object is mimicking, for a mimic disguised as an item.
     *
     * @param mimickingMIndex the mimicking monster's index (0 if this object is not a mimic)
     */
    public void setMimickingMIndex(int mimickingMIndex) {
        this.mimickingMIndex = mimickingMIndex;
    }

    /**
     * Tests whether two objects are similar enough to stack, under the given stacking mode — the
     * port of C's {@code object_similar} ({@code obj-util.c}). The comparison covers kind, ego,
     * artifact, known/unknown state, curses, runes, effect knowledge and any mode-specific rules.
     *
     * @param itm2 the other object to compare against
     * @param mode the {@link ObjectStackEnum} flags selecting which stacking rules apply
     * @return {@code true} if the two objects may occupy the same stack
     */
    @CheckReturnValue
    public boolean similar(@NotNull ItemObject itm2, @NotNull Flag<ObjectStackEnum> mode) {
        Player player = GameState.getPlayer();

        // Check for equipped items
        if (player.getPlayerBody().itemIsEquipped(this)) return false;
        if (player.getPlayerBody().itemIsEquipped(itm2)) return false;

        // Check for mimicked items
        if (this.mimickingMIndex != 0 || itm2.mimickingMIndex != 0) return false;

        // Check for unknown items
        if (mode.has(ObjectStackEnum.OSTACK_LIST) && this.kind != this.known.kind) return false;
        if (mode.has(ObjectStackEnum.OSTACK_LIST) && itm2.kind != itm2.known.kind) return false;

        // Can't stack an item with itself
        if (this == itm2) return false;

        // Must be the same kind of item
        if (!this.kind.equals(itm2.kind)) return false;
        if (!this.tValue.equals(itm2.tValue)) return false;

        // must have the same flags
        if (!this.flags.isEqual(itm2.flags)) return false;

        // Different elements don't stack
        if (!checkElementStacking(this, itm2)) return false;
        if (!checkElementStacking(itm2, this)) return false;

        if (this.artifact != null || itm2.artifact != null) return false;

        // Analyse the items
        TValue tVal = this.tValue;
        if (tVal.isChest()) {
            return false;
        } else if (tVal.isEdible() || tVal.isPotion() || tVal.isScroll() || tVal.isRod()) {
            return true;
        } else if (tVal.canHaveCharges() || tVal.isMoney()) {
            return this.pValue + itm2.pValue <= GameConstants.MAX_PVAL;
        } else if (tVal.isWeapon() || tVal.isArmour() || tVal.isJewelry() || tVal.isLight()) {
            boolean thisKnown = isFullyKnown();
            boolean itm2Known = itm2.isFullyKnown();

            // Identical values
            if (this.baseAC != itm2.baseAC) return false;
            if (this.damageDice != itm2.damageDice) return false;
            if (this.damageSides != itm2.damageSides) return false;

            // identical bonuses
            if (this.toHit != itm2.toHit) return false;
            if (this.toDam != itm2.toDam) return false;
            if (this.toAC != itm2.toAC) return false;

            // identical modifiers
            for (ObjectModifier mod : this.modifiers.keySet()) {
                if (!this.modifiers.get(mod).equals(itm2.modifiers.get(mod))) return false;
            }
            for (ObjectModifier mod : itm2.modifiers.keySet()) {
                if (!itm2.modifiers.get(mod).equals(this.modifiers.get(mod))) return false;
            }

            // Same ego item
            if (ego != itm2.ego) return false;

            if (!cursesAreEqual(itm2)) return false;

            // Never stack recharging wearables
            if ((timeout != 0 || itm2.timeout != 0) && !tVal.isLight()) return false;
            else if (timeout != itm2.timeout) return false;

            return !mode.has(ObjectStackEnum.OSTACK_LIST) || thisKnown == itm2Known;
        }

        // probably similar enough by now
        return true;
    }

    /**
     * Compares two objects' element info one way round, reporting whether everything the second
     * records is matched by the first. Extracted from {@link #similar} to carry the element half of
     * C's {@code object_stackable} ({@code obj-util.c}), which rejects a stack when two objects
     * differ in either their resistance levels or their {@code EL_INFO_HATES}/{@code EL_INFO_IGNORE}
     * flags.
     *
     * <p><b>Why it is one-directional, and called twice.</b> C compares full arrays indexed by
     * element, so a single loop over {@code 0..ELEM_MAX} sees both objects' entries at once. Here
     * the info is a map holding only the elements an object actually carries, so a loop over one
     * object's keys cannot see an element recorded solely on the other. {@link #similar} calls this
     * with the arguments both ways round, and the pair of passes covers what C's single loop does.
     *
     * <p>It is stricter than C in one corner: C treats a missing entry as a resistance level of
     * zero and would call that equal to an explicit zero, whereas the {@code containsKey} test here
     * refuses the stack outright. That errs towards keeping two objects apart, which costs the
     * player a merged pile at worst.
     *
     * <p>Reads through {@link #getElInfo()} rather than the field so that an object built by the
     * no-argument constructor, whose map is still null, compares as carrying no element info rather
     * than throwing.
     *
     * <p>Function checkElementStacking coded on 260817, commented in full on 260817.
     *
     * @param itm1 the object whose element info must cover the other's
     * @param itm2 the object whose recorded elements are walked
     * @return {@code true} if every element {@code itm2} records is matched on {@code itm1}
     */
    private boolean checkElementStacking(ItemObject itm1, ItemObject itm2) {
        for (ElementEnum e : itm2.getElInfo().keySet()) {
            if (e == ElementEnum.ELEM_NONE || e == ElementEnum.ELEM_MAX) continue;

            if (!itm1.getElInfo().containsKey(e)) return false;
            if (itm2.getElInfo().get(e).getResLevel() != itm1.getElInfo().get(e).getResLevel()) return false;

            Flag<ElementInfoEnum> itm1ELFlags = itm1.getElInfo().get(e).getFlags();
            Flag<ElementInfoEnum> itm2ELFlags = itm2.getElInfo().get(e).getFlags();

            boolean itm1Hates = itm1ELFlags.has(ElementInfoEnum.EL_INFO_HATES);
            boolean itm1Ignores = itm1ELFlags.has(ElementInfoEnum.EL_INFO_IGNORE);
            boolean itm2Hates = itm2ELFlags.has(ElementInfoEnum.EL_INFO_HATES);
            boolean itm2Ignores = itm2ELFlags.has(ElementInfoEnum.EL_INFO_IGNORE);

            if (itm1Hates != itm2Hates || itm1Ignores != itm2Ignores) return false;
        }

        return true;
    }

    /**
     * Checks whether the player knows everything there is to know about this object — the port of
     * C's {@code object_fully_known} ({@code obj-knowledge.c}).
     *
     * <p>Two questions, both of which must answer yes: every rune on the item has been learned, and
     * its effect is known. They are separate because they are learned by different means — runes by
     * the property doing its job, an effect by using the item — so an item can easily be complete on
     * one count and not the other.
     *
     * <p>Its role in {@link uk.co.jackoftrades.middle.player.Player#equipLearnFlag} is the
     * interesting one, and it is a negative: an item that is <em>not</em> yet fully known gets a
     * flag switched on in its known set to record that the flag was ruled out. Once the item is
     * fully known there is nothing left to rule out, so the bookkeeping stops.
     *
     * <p>Function isFullyKnown coded before 260815 as the private {@code fullyKnown}, made public on
     * 260815 when a second copy of it was folded back in. Commented in full on 260815.
     *
     * @return true if the player has full knowledge of this object
     */
    public boolean isFullyKnown() {
        if (!runesKnown()) return false;

        return effectIsKnown();
    }

    /**
     * Check the curse lists between this and the incoming object and confirm they are equal
     *
     * @param itm2 The object to compare with this object
     * @return true if the two curse lists on this and the incoming item are identical
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean cursesAreEqual(@NotNull ItemObject itm2) {
        return this.getCurses().equals(itm2.getCurses());
    }

    /**
     * Checks to see if all the runes on this object are known
     *
     * @return true if all the runes on this object are known
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean runesKnown() {
        if (known == null) return false;

        if (!cursesAreEqual(known)) return false;

        return Player.nonCurseRunesKnown(this);
    }

    /**
     * Checks whether the player is aware of the object's effect
     *
     * @return true if the object's known effect is the same as its effect
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean effectIsKnown() {
        if (known == null) return false;
        List<Effect> knownEffects = known.getEffect();
        for (Effect eff : this.getEffect()) {
            if (!knownEffects.contains(eff)) return false;
        }
        return true;
    }

    /**
     * Check if the inscriptions between this object and itm2 are similar enough to allow stacking
     *
     * @param itm2 the second object to check against this one
     * @param mode the mode of stacking we are checking for
     * @return true if the object itm2 has the same inscriptions as this one
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean stackable(@NotNull ItemObject itm2, @NotNull Flag<ObjectStackEnum> mode) {
        if (similar(itm2, mode)) {
            return (note == null || itm2.note == null || note.equals(itm2.note));
        }
        return false;
    }

    /**
     * Combine the origin of another object with this one
     *
     * @param item the item to combine into this one if possible
     */
    private void originCombine(@NotNull ItemObject item) {
        if (!originRace.equals(item.originRace)) {
            boolean uniqThis = (this.originRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));
            boolean uniqItem = (item.originRace != null && item.originRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));

            if (uniqThis && !uniqItem) {
                // Do nothing - keep a unique rather than destroy it
            } else if (!uniqThis && uniqItem) {
                this.originRace = item.originRace;
                this.origin = item.origin;
                this.originDepth = item.originDepth;
            } else {
                this.origin = ORIGIN_MIXED;
            }
        } else if (!this.origin.equals(item.origin) || this.originDepth != item.originDepth) {
            this.origin = ORIGIN_MIXED;
        }
    }

    /**
     * Allows one item to absorb a similar item.
     * <br/><br/>
     * The note merging should work no matter if there is none, one or two null values
     * <br/><br/>
     * These assumptions are enforced by the mergeable() code
     *
     * @param item                   the object to be absorbed by this one
     * @param combineChargesTimeouts whether we are merging timeouts and charges
     */
    private void absorbMerge(@NotNull ItemObject item, boolean combineChargesTimeouts) {
        if (item.getKnown() != null && this.getKnown() != null) {
            if (!item.getKnown().effect.isEmpty()) {
                this.getKnown().effect = this.effect;
                GameState.getPlayer().knowObject(this);
            }
        }

        if (item.note != null && !item.note.isEmpty())
            this.note = item.note;

        if (combineChargesTimeouts) {
            if (this.tValue.canHaveTimeout())
                this.timeout += item.timeout;

            if (this.tValue.canHaveCharges() || this.tValue.isMoney()) {
                int total = this.pValue + item.pValue;
                this.pValue = Math.min(total, GameConstants.MAX_PVAL);
            }
        }

        originCombine(item);
    }

    /**
     * Merge a smaller stack into a larger one, leaving two uneven stacks
     *
     * @param item     the object to merge into this one
     * @param thisMode the behaviour of the first (this) stack, mostly that it's not OSTACK_STORE, which has no limit
     *                 for the size of the stack
     * @param itemMode the behaviour of the second (item) stack, mostly that it's not OSTACK_STORE, which has no limit
     *                 for the size of the stack
     */
    private void absorbPartial(ItemObject item, Flag<ObjectStackEnum> thisMode, Flag<ObjectStackEnum> itemMode) {
        int smallest = Math.min(this.number, item.number);
        int largest = Math.max(this.number, item.number);
        int newsz1, newsz2;

        if (thisMode.has(ObjectStackEnum.OSTACK_STORE) || itemMode.has(ObjectStackEnum.OSTACK_STORE)) {
            logger.error("Either this or the incoming object have a store stacking mode set");
            return;
        }

        if (thisMode.has(ObjectStackEnum.OSTACK_QUIVER)) {
            int limit = GameConstants.getCarryCapQuiverSlotSize() / (this.tValue.isAmmo()
                    ? 1
                    : GameConstants.getCarryCapThrownQuiverMult());
            if (itemMode.has(ObjectStackEnum.OSTACK_QUIVER)) {
                int difference = limit - largest;

                newsz1 = largest + difference;
                newsz2 = smallest - difference;
            } else {
                newsz1 = limit;
                newsz2 = (largest + smallest) - limit;

                if (newsz2 >= this.kind.getBase().getMaxStack()) {
                    logger.error("New size outside acceptable limits after merging");
                    return;
                }
            }
        } else if (itemMode.has(ObjectStackEnum.OSTACK_QUIVER)) {
            int limit = GameConstants.getCarryCapQuiverSlotSize() / (item.tValue.isAmmo()
                    ? 1
                    : GameConstants.getCarryCapThrownQuiverMult());

            newsz1 = largest + smallest - limit;
            newsz2 = limit;
            if (newsz1 >= this.kind.getBase().getMaxStack()) {
                logger.error("New size outside acceptable limits after merging");
                return;
            }
        } else {
            int difference = this.kind.getBase().getMaxStack() - largest;

            newsz1 = largest + difference;
            newsz2 = smallest - difference;
        }

        item.distributeCharges(this, item.number - newsz2, false);
        this.number = newsz1;
        item.number = newsz2;

        absorbMerge(item, this.tValue.isMoney());
    }

    /**
     * Distribute the charges of rods, staves and wands which are being merged
     *
     * @param item    the item which will be the destination of the merge
     * @param amount  the amount of items we are merging
     * @param destNew whether the destination is a new object, or an existing one
     */
    private void distributeCharges(@NotNull ItemObject item, int amount, boolean destNew) {
        if (this.tValue.canHaveCharges()) {
            int change = this.pValue * amount / this.number;

            if (destNew) {
                item.pValue = change;
            } else {
                item.pValue += change;
            }
            if (amount < this.number) {
                this.pValue -= change;
            }
        }

        if (this.tValue.canHaveTimeout()) {
            int chargeTime = this.time.randCalc(0, DamageAspect.AVERAGE);
            int maxTime = chargeTime * amount;

            if (destNew) {
                item.timeout = Math.min(this.timeout, maxTime);
                if (amount < this.number)
                    this.timeout -= item.timeout;
            } else {
                int change = Math.min(this.timeout, maxTime);

                maxTime = chargeTime * (item.number + amount);

                if (item.timeout < maxTime) {
                    if (change > maxTime - item.timeout)
                        change = maxTime - item.timeout;

                    item.timeout += change;
                    if (amount < this.number) {
                        this.timeout -= change;
                    }
                }
            }
        }
    }

    /**
     * Merge two stacks into one completely
     *
     * @param item the object to merge into this stack
     */
    private void absorb(@NotNull ItemObject item) {
        ItemObject itemKnown = item.known;
        int total = this.number + item.number;

        this.number = Math.min(total, this.kind.getBase().getMaxStack());
        Chunk playerCave = GameState.getPlayer().getCave();

        absorbMerge(item, true);
        if (itemKnown != null) {
            if (!itemKnown.location.equals(Loc.zero))
                playerCave.squareExciseObject(itemKnown.location, itemKnown);

            playerCave.delistObject(itemKnown);
            playerCave.objectDelete(null, itemKnown);
        }

        GameState.getCave().objectDelete(playerCave, item);
    }

    /**
     * Returns the curses on this object, each mapped to its instance data — the port of reading C's
     * {@code obj->curses}.
     *
     * <p>The value is the curse's {@link CurseData}, its power and the countdown to its next
     * effect. Whether the <em>player</em> knows of a curse is a different question and is not
     * recorded here; that lives on {@link KnownObject}, which maps a curse to a plain boolean.
     *
     * <p>An unmodifiable view, not a copy, and the distinction matters in both directions. Because
     * it is a view, the {@link CurseData} values are the live ones, so the curse tick in
     * {@code GameWorld} can decrement a timeout in place through what it reads here, exactly as C's
     * {@code curse[j].timeout--} does. Because it is unmodifiable, adding or removing a curse has
     * to go through {@link #addCurse}, {@link #removeCurse} and their neighbours rather than
     * happening behind this object's back.
     *
     * <p>An empty map stands for "no curses", including for an object whose backing map has never
     * been created — the null is absorbed here rather than pushed onto every caller, which is also
     * how C's {@code curses_are_equal} treats a null curse array.
     *
     * <p>Function getCurses coded before 260817, commented in full on 260817.
     *
     * @return this object's curses and their instance data, as an unmodifiable view
     */
    public Map<Curse, CurseData> getCurses() {
        if (curses == null)
            return Map.of();
        return Collections.unmodifiableMap(curses);
    }

    /**
     * Puts a curse on this object at a given power and timeout, building the instance data from the
     * two figures.
     *
     * <p>The form to reach for when the caller holds numbers rather than an existing
     * {@link CurseData} — notably the knowledge code, which copies a curse's power onto an object's
     * known counterpart and leaves the timeout at zero, as C's {@code player_know_object} does with
     * {@code obj->known->curses[i].power = obj->curses[i].power}. Building fresh data here is what
     * keeps the counterpart from sharing the real object's countdown.
     *
     * <p>Replaces any data already held for that curse, matching the plain assignment C makes into
     * its curse array. The backing map is created on demand, so this is safe on an object that has
     * never carried a curse.
     *
     * <p>Function addCurse coded before 260817, commented in full on 260817.
     *
     * @param curse   the curse to apply
     * @param power   the curse's power on this object
     * @param timeout turns until the curse's first effect
     */
    public void addCurse(Curse curse, int power, int timeout) {
        CurseData curseData = new CurseData(power, timeout);
        if (this.curses == null) {
            this.curses = new LinkedHashMap<>();
        }
        this.curses.put(curse, curseData);
    }

    /**
     * Puts a curse on this object with instance data the caller already holds.
     *
     * <p>Stores the {@link CurseData} given, without copying it. That is deliberate — it lets a
     * caller keep a handle on the data it just installed — but it makes the caller responsible for
     * not handing over an instance something else is still using. A template's curse data in
     * particular must be copied first, or the tick that decrements this object's timeout will
     * decrement the template's; {@link ObjectKind}'s constructor copies on the way in for that
     * reason.
     *
     * <p>Function addCurse coded before 260817, commented in full on 260817.
     *
     * @param curse     the curse to apply
     * @param curseData the instance data to store, taken by reference
     */
    public void addCurse(Curse curse, CurseData curseData) {
        if (this.curses == null) {
            this.curses = new LinkedHashMap<>();
        }
        this.curses.put(curse, curseData);
    }

    /**
     * Adds a whole set of curses at once, the batch form of {@link #addCurse(Curse, CurseData)}.
     *
     * <p>Adds; it does not replace. Curses already on this object and not named in the argument
     * stay, which is what an object picking up an ego's or an artifact's curses on top of its
     * kind's needs. Use {@link #clearAndPutCurses} for the replacing form.
     *
     * <p>Shares the argument's {@link CurseData} instances rather than copying them, with the same
     * caveat as the single-curse form: a map belonging to a template must be copied by the caller.
     *
     * <p>Function addCurses coded before 260817, commented in full on 260817.
     *
     * @param curses the curses to add, with their instance data taken by reference
     */
    public void addCurses(Map<Curse, CurseData> curses) {
        if (this.curses == null) {
            this.curses = new LinkedHashMap<>();
        }
        this.curses.putAll(curses);
    }

    /**
     * Replaces this object's curses with the given set, discarding whatever was there.
     *
     * <p>The replacing counterpart of {@link #addCurses}: this object ends up carrying exactly the
     * curses named and no others. That is the operation wanted when an object's curse list is being
     * rebuilt from a source of truth rather than accumulated.
     *
     * <p>Function clearAndPutCurses coded before 260817, renamed from {@code clearAndPut} on 260817,
     * commented in full on 260817.
     *
     * @param curseEntries the curses this object should carry, with their instance data taken by
     *                     reference
     */
    public void clearAndPutCurses(Map<Curse, CurseData> curseEntries) {
        if (this.curses == null) {
            this.curses = new LinkedHashMap<>();
        }
        this.curses.clear();
        this.curses.putAll(curseEntries);
    }

    /**
     * Removes every curse from this object.
     *
     * <p>The port of what C achieves by freeing the curse array and setting the pointer to null —
     * {@code mem_free(obj->known->curses); obj->known->curses = NULL;} in
     * {@code player_know_object}, which uses it to wipe a known counterpart's curses when the real
     * object turns out to have none the player recognises.
     *
     * <p>Exists as a method because {@link #getCurses()} hands back an unmodifiable view, so a
     * caller cannot clear the map through it. Leaves an empty map rather than a null one; the two
     * are indistinguishable from outside, {@link #getCurses()} reporting empty for both.
     *
     * <p>Function clearCurses coded on 260817, commented in full on 260817.
     */
    public void clearCurses() {
        if (curses == null)
            curses = new LinkedHashMap<>();
        curses.clear();
    }

    /**
     * Changes the power of a curse already on this object, leaving its timeout alone.
     *
     * <p>The port of C's bare {@code obj->curses[i].power = ...} assignment, which appears wherever
     * a curse is weakened or strengthened without being added or taken away.
     *
     * <p>Does nothing for a curse this object does not carry. That is the safe reading of the
     * request: in this port an absent curse and a curse of power zero are the same state, so there
     * is no meaningful power to set on one that is not there, and creating an entry would invent a
     * curse rather than adjust one. Setting a curse's power to zero is therefore not the way to
     * remove it — use {@link #removeCurse} for that.
     *
     * <p>Function setCursePower coded on 260817, commented in full on 260817.
     *
     * @param curse the curse to adjust; ignored if {@code null} or not on this object
     * @param power the curse's new power
     */
    public void setCursePower(Curse curse, int power) {
        if (this.curses == null) {
            this.curses = new LinkedHashMap<>();
        }
        if (curse == null || !this.curses.containsKey(curse)) return;
        CurseData curseData = this.curses.get(curse);
        curseData.setPower(power);
    }

    /**
     * Takes a curse off this object.
     *
     * <p>The port of C's {@code obj->curses[i].power = 0}. C cannot delete an entry from an array
     * indexed by curse, so it zeroes the power and reads that back as "no curse"; the port holds a
     * map, where absence says the same thing directly. The two representations agree because
     * nothing here ever stores a curse at power zero — which is also what lets
     * {@code cursesAreEqual} compare two maps and reach C's answer.
     *
     * <p>Silently does nothing for a curse the object does not carry.
     *
     * <p>Function removeCurse coded on 260817, commented in full on 260817.
     *
     * @param curse the curse to remove
     */
    public void removeCurse(Curse curse) {
        if (this.curses == null) {
            this.curses = new LinkedHashMap<>();
        }
        this.curses.remove(curse);
    }

    /**
     * @return the random interval between activations of this object's effect — the
     * port of C's {@code obj->time}; for a curse template this is the dice re-rolled
     * into each cursed object's timeout
     */
    public Random getTime() {
        return time;
    }

    /**
     * Advances this object's recharge by one game turn, the port of C's {@code recharge_timeout}
     * ({@code obj-util.c:1043-1065}).
     *
     * <p>A stack of rods is a single object with one pooled {@link #timeout} rather than a counter
     * per rod, so the turn's charge is spent on every rod still charging at once: {@link #timeout}
     * falls by {@link #numberCharging()}, clamped so it can never run past zero into a value
     * {@link #numberCharging()} would read back as ready.
     *
     * <p>The return value is a <em>transition</em>, not a state. It is {@code false} on every turn
     * that merely reduces the timeout, and {@code true} only on the turn that takes the charging
     * count down — which is the turn one more rod becomes usable, and so the turn the player is
     * told about it. Callers wanting to know whether the object is ready should read
     * {@link #getTimeout()} instead.
     *
     * <p>Because the drain rate is the number still charging, a stack recharges more slowly as it
     * goes: three rods on a ten-turn interval spend thirty turns of pooled charge over eighteen
     * game turns, not ten, as the rate steps down from three per turn to one.
     *
     * @return {@code true} if at least one item obtained a charge this turn
     */
    public boolean rechargeTimeout() {
        int chargingBefore = numberCharging();

        if (chargingBefore == 0)
            return false;

        timeout -= Math.min(chargingBefore, timeout);

        int chargingAfter = numberCharging();

        return (chargingAfter < chargingBefore);
    }

    /**
     * Returns how many items in this (possibly stacked) object are still charging — the port of
     * C's {@code number_charging} ({@code obj-util.c}).
     *
     * <p>Derived from the remaining {@link #timeout} and the per-item recharge interval
     * ({@link #time}, evaluated at its average), clamped to the stack size {@link #number}.
     * Objects with no recharge interval or no outstanding timeout have nothing charging.
     *
     * @return the number of items currently charging (0 if none)
     */
    public int numberCharging() {
        if (time == null) return 0;

        int chargeTime = time.randCalc(0, DamageAspect.AVERAGE);

        // Item has no timeout
        if (chargeTime <= 0) return 0;

        // No items are charging
        if (timeout <= 0) return 0;

        // Calculate number charging based on timeout
        int numCharging = (timeout + chargeTime - 1) / chargeTime;

        // Number charging cannot exceed stack size
        if (numCharging > number) numCharging = number;

        return numCharging;
    }

    /**
     * @return the number of items in this stack
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return this object's base type (tval)
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * @return the turns remaining until this object is ready to use again (0 = ready)
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Returns the player's inscription on this object, or {@code null} if it carries none.
     *
     * <p>C stores this as {@code quark_t note} ({@code object.h:472}) — an index into the global
     * quark table, where {@code 0} means "no inscription" and the text is fetched with
     * {@code quark_str}. The port holds the text directly, so {@code null} is the equivalent of
     * C's {@code 0} and callers test it rather than the index.
     *
     * @return the inscription, or {@code null} if the object is uninscribed
     */
    public String getNote() {
        return note;
    }

    /**
     * Returns the live brand set, not a copy, matching how C hands out {@code obj->brands} — an
     * array on the struct that callers read and write in place.
     *
     * <p>The brands here are the ones the item actually has, each at its own strength. That is a
     * different question from whether the player can read them, which is
     * {@link KnownObject#brandIsKnown} and is not per-item at all.
     *
     * @return this item's brands, shared with this instance
     */
    public Set<Brand> getBrands() {
        if (brands == null)
            return Set.of();
        return brands;
    }

    /**
     * Returns this item's own to-hit bonus, the port of reading C's {@code obj->to_h} directly.
     *
     * <p>Callers deciding whether the player has just <em>felt</em> this bonus should generally not
     * use this — see {@link #hasStandardToH}, which knows that body armour's built-in penalty is
     * normal and teaches nothing.
     *
     * <p>Function getToHit coded on 260815, replacing the stubbed {@code isBoostedToH}. Commented in
     * full on 260815.
     *
     * @return this item's rolled to-hit bonus, which may be negative
     */
    public int getToHit() {
        return toHit;
    }

    /**
     * Returns this item's own to-damage bonus, the port of reading C's {@code obj->to_d}
     * directly.
     *
     * <p>Here the raw comparison is the right one: C's learning code tests a plain
     * {@code if (obj->to_d)}, because nothing has a to-damage figure as a matter of course the way
     * armour has a to-hit penalty. There is no {@code hasStandardToD} to reach for.
     *
     * <p>Function getToDam coded on 260815, replacing the stubbed {@code isBoostedToD}. Commented in
     * full on 260815.
     *
     * @return this item's rolled to-damage bonus, which may be negative
     */
    public int getToDam() {
        return toDam;
    }

    /**
     * Returns this item's own to-armour-class bonus, the port of reading C's {@code obj->to_a}
     * directly. As with {@link #getToDam}, a plain non-zero test is the faithful comparison.
     *
     * <p>Function getToAC coded on 260815, replacing the stubbed {@code isBoostedToA}. Commented in
     * full on 260815.
     *
     * @return this item's rolled to-AC bonus, which may be negative
     */
    public int getToAC() {
        return toAC;
    }

    /**
     * Reports whether this item has a known counterpart — the object that records how much of it
     * the player can currently see. The port of C's {@code obj->known} tested for non-NULL.
     *
     * <p>This is emphatically not "has the player identified this item". Every object in play
     * carries a {@code known} companion from the moment it is created, so on a live item the answer
     * is yes long before anything about it has been learned; what the player actually knows is the
     * <em>content</em> of that companion. Reading this as identification is the mistake the name
     * invites, and it is why C only ever uses the test the way {@code equip_learn_on_defend} does —
     * as {@code assert(obj->known)}, a sanity check that the pairing was set up, on its own line and
     * never folded into a condition that decides whether to learn something.
     *
     * @return whether a known counterpart has been attached to this item
     */
    public boolean isKnown() {
        return known != null;
    }

    /**
     * Reports whether this item's to-hit bonus is the one it ought to have — that is, whether it
     * is carrying nothing worth learning from. The port of C's {@code object_has_standard_to_h}
     * ({@code obj-knowledge.c:580}).
     *
     * <p>The question exists because to-hit is the one combat figure an ordinary item can have
     * without being remarkable. Body armour is heavy and gets in the way, so its kind declares a
     * penalty as a matter of course — Chain Mail is {@code attack:1d4:-2:0} in {@code object.txt},
     * and every hauberk ever rolled has {@code toHit == -2}. A plain {@code getToHit() != 0} would
     * read that as evidence of enchantment and teach the to-hit rune to anyone who put one on, which
     * is why {@link uk.co.jackoftrades.middle.player.Player#equipLearnOnMeleeAttack} asks this
     * instead. To-damage and to-AC need no such test: nothing has those as standard equipment, so
     * {@link #getToDam} and {@link #getToAC} are compared against zero directly.
     *
     * <p><b>The three answers.</b>
     * <ol>
     *   <li>No kind at all → standard. C's {@code if (!obj->kind) return true;}, commented there as
     *       a hack for curse object structures: a curse's contribution is carried on a bare
     *       {@code struct object} that was never generated from a template, so there is no normal
     *       value to compare against and the honest answer is "nothing unusual here".</li>
     *   <li>Body armour whose kind declares a <em>fixed</em> to-hit → standard iff it still equals
     *       that fixed value. The {@code !varies()} guard matters: if the kind rolled its penalty
     *       from dice there is no single figure to have been expected, so the comparison would be
     *       meaningless and C falls through to the last case rather than picking one end of the
     *       range. {@link Random#varies} is the port of {@code randcalc_varies}, minimum against
     *       maximum.</li>
     *   <li>Everything else → standard iff zero. A sword has no built-in accuracy, so any figure at
     *       all came from an ego, an artifact or a curse.</li>
     * </ol>
     *
     * <p>Note this is a fact about the item, not about the player: it says what is there to be
     * learned, and says nothing about whether the player has learned it. That second question is
     * {@link KnownObject#toHIsKnown}.
     *
     * <p>Function hasStandardToH coded on 260815, commented in full on 260815.
     *
     * @return whether this item's to-hit bonus is the unremarkable one for its kind
     */
    public boolean hasStandardToH() {
        if (kind == null) return true;

        if (tValue.isBodyArmour() && !kind.getToH().varies())
            return toHit == kind.getToH().getBase();
        else
            return toHit == 0;
    }

    /**
     * Reports whether this item carries the given object flag, the port of C's
     * {@code of_has(obj->flags, flag)}.
     *
     * <p>This asks what the item <em>is</em>, not what the player knows about it. Those are separate
     * questions throughout the knowledge code, and there are <em>two</em> stores of knowledge to
     * keep apart from this one:
     *
     * <ul>
     *   <li>the flags <b>this item has</b> — here, and read through this method;</li>
     *   <li>the flags the player can read <b>on this item</b> — on the item's counterpart, reached
     *       as {@code getKnown().getFlags()};</li>
     *   <li>the runes the player can read <b>at all</b>, on any item — {@link KnownObject}, which
     *       belongs to the player and mentions no item.</li>
     * </ul>
     *
     * <p>The middle one is derived from the other two: {@code player_know_object} sets a
     * counterpart's flags to the intersection of what the player understands with what the item
     * actually carries. Conflating the last two is the easy mistake, because both are "what the
     * player knows" — but one is general and one is per-item, and the whole knowledge subsystem is
     * the traffic between them.
     *
     * <p>C's {@code equip_learn_flag} plays this method against the second store — an item that has
     * the flag may teach it, an item that does not gets the flag marked on its counterpart as having
     * been ruled out.
     *
     * <p>Function hasFlag coded on 260815, commented in full on 260815. Corrected on 260816: the
     * previous version placed an item's readable flags on {@link KnownObject}, which is a different
     * store, and routed them through {@code getKnownFlags}, since withdrawn.
     *
     * @param flag the flag to test for
     * @return whether this item carries it
     */
    public boolean hasFlag(ObjectFlag flag) {
        return flags.has(flag);
    }

    /**
     * Builds the player-facing name of this item at the requested level of detail — the port of
     * C's {@code object_desc} ({@code obj-desc.c}), which assembles a name from the kind, the
     * flavour, the player's knowledge of it, the stack count and the inscription according to the
     * {@link ObjectDescription} flags it is given.
     *
     * <p><b>Stub:</b> returns the literal {@code {DESCRIPTION_TAG}} until the description subsystem
     * is ported, which is deferred to Chapter 7. The placeholder is deliberately conspicuous rather
     * than empty, because the return
     * value is not inspected by its callers — {@link #flagMessage} substitutes it straight into a
     * message and shows it to the player. An empty string would produce "Your  glows." and read as
     * a spacing bug; the tag reads as a thing not yet built.
     *
     * <p>Function description coded on 260815, commented in full on 260815.
     *
     * @param descriptionFlags how much of the name to build, C's {@code mode}
     * @param player           the player whose knowledge decides what may appear in the name
     * @return the item's name; the placeholder tag while stubbed
     */
    public String description(Flag<ObjectDescription> descriptionFlags, Player player) {
        // Stub function
        // TODO: Implement
        return "{DESCRIPTION_TAG}";
    }

    /**
     * Announces that a flag has shown itself on a named item — the port of C's
     * {@code flag_message} ({@code obj-properties.c:86}). Called at the moment of noticing, so the
     * message describes an event rather than a fact: the player did not read the property off the
     * item, the property did something and gave itself away.
     *
     * <p>The wording belongs to the property, not to this class. {@code object_property.txt} gives
     * each flag a {@code msg:} line — {@code Your {name} glows.} and the like — and the
     * {@code {name}} tag is where the item's description goes. C walks the string looking for
     * braces and silently drops any tag it does not recognise; a plain replace is equivalent here
     * because {@code {name}} is the only tag the data file uses.
     *
     * <p><b>Two ways to have no message, and they are not the same.</b> A property that is missing
     * from {@code object_property.txt} altogether is a data error and is logged as one, with C's
     * distinction preserved between a flag index that could never be valid ({@link ObjectFlag#OF_NONE},
     * {@link ObjectFlag#OF_MAX}) and a real flag that simply has no entry. A property that exists
     * but declares no {@code msg:} is not an error at all — most flags are learned silently — and
     * returns without a word.
     *
     * <p>Function flagMessage coded on 260815, commented in full on 260815.
     *
     * @param flag the flag that has just shown itself
     * @param name the item's description, as {@link #description} builds it
     */
    public void flagMessage(ObjectFlag flag, String name) {
        ObjectPropertyTypeWrapper payload = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag);
        ObjectProperty property = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, payload);

        if (property == null) {
            if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX)
                logger.error("Invalid flag index, " + flag.toString() + " passed to ItemObject.flagMessage().");
            else
                logger.error("Flag (" + flag.toString() + ") has been passed to ItemObject.flagMessage() " +
                        "but no entry in object_property.txt.");

            return;
        }
        String toSend = property.getNoticeMessage();
        if (toSend == null) return;
        toSend = toSend.replace("{name}", name);
        Message.message(toSend);
    }

    /**
     * Returns this item's object flags, the port of reading C's {@code obj->flags}.
     *
     * <p><b>A copy, deliberately.</b> Every write to a flag set goes through {@link #setFlag},
     * {@link #setFlags} or {@link #setFlagsTo}, so nothing needs a mutable handle on the real set,
     * and handing one out would leave a fourth, unnamed write path open beside the three named ones.
     * That path has caused two bugs already — a write discarded because the "live" set was a copy,
     * and a known object left sharing its item's set so that knowledge could never afterwards differ
     * from truth.
     *
     * <p>Most readers want one flag rather than the set; {@link #hasFlag} answers that without the
     * allocation.
     *
     * <p>Function getFlags commented in full on 260816, when it changed from returning the live set.
     *
     * @return a copy of this item's flags
     */
    public Flag<ObjectFlag> getFlags() {
        Flag<ObjectFlag> toReturn = new Flag<>(ObjectFlag.class);
        toReturn.copyFrom(flags);
        return toReturn;
    }

    /**
     * Returns the player's known view of this item, the port of reading C's {@code obj->known}.
     *
     * <p>Itself an {@link ItemObject}, carrying only what has been discovered — which is why the
     * knowledge code reads a property off this one and writes it to that one. Null on an item the
     * player has never seen, so callers check; C is entitled to skip the check because
     * {@code assert(obj->known)} has just run.
     *
     * <p>Function getKnown commented in full on 260816.
     *
     * @return the known counterpart, or {@code null} if this item has none
     */
    public ItemObject getKnown() {
        return known;
    }

    /**
     * Returns this item's notice flags, the port of reading C's {@code obj->notice}.
     *
     * <p>How far the player has got with this particular item — sensed, assessed, ignored — as
     * distinct from what they know about its properties. {@code knowObject} reads
     * {@code OBJ_NOTICE_ASSESSED} here to tell an object examined up close from one merely seen
     * across a room.
     *
     * <p>A copy, for the reason given on {@link #getFlags}.
     *
     * <p>Function getNotice commented in full on 260816.
     *
     * @return a copy of this item's notice flags
     */
    public Flag<ObjectNotice> getNotice() {
        Flag<ObjectNotice> flags = new Flag<>(ObjectNotice.class);
        flags.copyFrom(notice);
        return flags;
    }

    /**
     * @return this item's sub-type value — C's {@code obj->sval}
     */
    public int getsValue() {
        return sValue;
    }

    /**
     * @param sValue the sub-type value to set — C's {@code obj->sval}
     */
    public void setsValue(int sValue) {
        this.sValue = sValue;
    }

    /**
     * @return this item's weight in tenths of a pound — C's {@code obj->weight}
     */
    public int getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set — C's {@code obj->weight}
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Sets the kind this item is an instance of — C's {@code obj->kind}.
     *
     * <p>A null kind is not a missing value but a marker: the bearer-less item hanging off a curse
     * definition has one, and {@code knowObject} stops early on exactly that test.
     *
     * @param kind the kind to set
     */
    public void setKind(ObjectKind kind) {
        this.kind = kind;
    }

    /**
     * @param tValue the item type value to set — C's {@code obj->tval}
     */
    public void settValue(TValue tValue) {
        this.tValue = tValue;
    }

    /**
     * @param number the stack count to set — C's {@code obj->number}
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * @return the number of damage dice this item rolls — C's {@code obj->dd}
     */
    public int getDamageDice() {
        return damageDice;
    }

    /**
     * Sets the number of damage dice — C's {@code obj->dd}. See {@link #setBaseAC} for why a known
     * counterpart may be given a zero here rather than the truth.
     *
     * @param damageDice the number of damage dice to set
     */
    public void setDamageDice(int damageDice) {
        this.damageDice = damageDice;
    }

    /**
     * @return the sides per damage die — C's {@code obj->ds}
     */
    public int getDamageSides() {
        return damageSides;
    }

    /**
     * @param damageSides the sides per damage die to set — C's {@code obj->ds}
     */
    public void setDamageSides(int damageSides) {
        this.damageSides = damageSides;
    }

    /**
     * @return this item's base armour class — C's {@code obj->ac}
     */
    public int getBaseAC() {
        return baseAC;
    }

    /**
     * Sets the base armour class — C's {@code obj->ac}.
     *
     * <p>On a known counterpart this is written as {@code real * knowledgeBit}, so a player who
     * cannot read armour class is given a zero rather than the truth. That is C's idiom and the
     * zero is meaningful: it is what the display shows for an unknown quantity.
     *
     * @param baseAC the base armour class to set
     */
    public void setBaseAC(int baseAC) {
        this.baseAC = baseAC;
    }

    /**
     * Sets the to-hit bonus — C's {@code obj->to_h}. See {@link #hasStandardToH} for why a non-zero
     * value here is not by itself remarkable: body armour carries a to-hit penalty from its kind.
     *
     * @param toHit the to-hit bonus to set
     */
    public void setToHit(int toHit) {
        this.toHit = toHit;
    }

    /**
     * @return this item's extra parameter value — C's {@code obj->pval}
     */
    public int getpValue() {
        return pValue;
    }

    /**
     * @param pValue the extra parameter value to set — C's {@code obj->pval}
     */
    public void setpValue(int pValue) {
        this.pValue = pValue;
    }

    /**
     * @param toAC the to-armour-class bonus to set — C's {@code obj->to_a}
     */
    public void setToAC(int toAC) {
        this.toAC = toAC;
    }

    /**
     * @param toDam the to-damage bonus to set — C's {@code obj->to_d}
     */
    public void setToDam(int toDam) {
        this.toDam = toDam;
    }

    /**
     * Returns this item's rolled modifier values, the port of reading C's {@code obj->modifiers}.
     *
     * <p>Live, not a copy, and written through by the knowledge code — unlike {@link #getFlags},
     * which was narrowed to a copy once named mutators existed for it. The same case could be made
     * here; it has not been made yet, and until it is, a caller holding this map holds the item's
     * own state.
     *
     * <p>Function getModifiers commented in full on 260816.
     *
     * @return this item's modifiers, shared with this instance
     */
    public Map<ObjectModifier, Integer> getModifiers() {
        if (modifiers == null) {
            return Map.of();
        }
        return modifiers;
    }

    /**
     * @param modifiers the modifier map to set — C's {@code obj->modifiers}; stored, not copied
     */
    public void setModifiers(Map<ObjectModifier, Integer> modifiers) {
        this.modifiers = modifiers;
    }

    /**
     * Returns this item's per-element resistances and vulnerabilities, the port of reading C's
     * {@code obj->el_info}.
     *
     * <p>Live, and written through — see {@link #getModifiers} for the same note. The values are
     * mutable {@link ElementInfo} objects, so sharing goes one level deeper than the map: copying a
     * value from a real item to its known counterpart wants {@link ElementInfo#copy}, not the
     * reference, or the two stop being able to differ.
     *
     * <p>Function getElInfo commented in full on 260816.
     *
     * @return this item's element info by element, shared with this instance
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        if (elInfo == null) {
            return Map.of();
        }
        return elInfo;
    }

    /**
     * @param elInfo the element info map to set — C's {@code obj->el_info}; stored, not copied
     */
    public void setElInfo(Map<ElementEnum, ElementInfo> elInfo) {
        this.elInfo = elInfo;
    }

    /**
     * Records this item's relation to one element, the port of assigning into C's
     * {@code obj->el_info[i]}.
     *
     * <p>Exists because {@link #getElInfo()} answers {@code Map.of()} for an item whose map has never
     * been created, and an immutable empty map takes no writes. The knowledge code writes element
     * info onto counterpart objects built by the no-argument constructor, which are exactly those
     * items, so the map is created here on demand.
     *
     * <p>Stores the {@link ElementInfo} given rather than copying it. That matters more here than for
     * most values: {@code ElementInfo} is mutable, so handing over a real item's instance would leave
     * the item and its counterpart unable to differ. Callers copying one object's element info onto
     * another want {@link ElementInfo#copy} first — {@code knowObject} does.
     *
     * <p>Function putElInfo coded on 260817, commented in full on 260817.
     *
     * @param element the element being described
     * @param elInfo  this item's relation to it, taken by reference
     */
    public void putElInfo(ElementEnum element, ElementInfo elInfo) {
        if (this.elInfo == null) {
            this.elInfo = new HashMap<>();
        }
        this.elInfo.put(element, elInfo);
    }

    /**
     * Returns what this item does when used, the port of reading C's {@code obj->effect}.
     *
     * <p>Copied onto the known counterpart only once the player is entitled to it: an aware flavour,
     * an unflavoured non-wearable, or a wearable whose kind has a standard activation. Comparing
     * this against the counterpart's is how {@code effectIsKnown} answers.
     *
     * <p>Function getEffect commented in full on 260816.
     *
     * @return this item's effects, shared with this instance
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * @param effect the effect list to set — C's {@code obj->effect}; stored, not copied
     */
    public void setEffect(List<Effect> effect) {
        this.effect = effect;
    }

    /**
     * Returns the slays this item carries, the port of reading C's {@code obj->slays}.
     *
     * <p>The slays the item actually has. Whether the player can read one is a separate question
     * and not a per-item one — see {@link KnownObject#slayIsKnown}.
     *
     * <p>Function getSlays commented in full on 260816.
     *
     * @return this item's slays, shared with this instance
     */
    public Set<Slay> getSlays() {
        if (slays == null) {
            return Set.of();
        }
        return slays;
    }

    /**
     * @param slays the slay set to set — C's {@code obj->slays}; stored, not copied
     */
    public void setSlays(Set<Slay> slays) {
        this.slays = slays;
    }

    /**
     * Returns this item's ego type, the port of reading C's {@code obj->ego}.
     *
     * <p>Shared with the registry rather than owned: two Long Swords of Extra Attacks hold the same
     * definition, which is why {@code similar} compares egos by reference.
     *
     * <p>Function getEgo commented in full on 260816.
     *
     * @return this item's ego, or {@code null} if it has none
     */
    public EgoItem getEgo() {
        return ego;
    }

    /**
     * @param ego the ego type to set — C's {@code obj->ego}
     */
    public void setEgo(EgoItem ego) {
        this.ego = ego;
    }

    /**
     * Switches on every flag in the given set, leaving the rest alone — the port of C's
     * {@code of_union(obj->flags, mask)}.
     *
     * <p>The batch form of {@link #setFlag}. C uses it to rule out a whole family of properties at
     * once: an item worn through an event that would have displayed any of the timed flags has had
     * its chance at all of them, so all of them are settled together.
     *
     * <p>Adds; it does not replace. {@link #setFlagsTo} is the one that replaces, and the two are
     * easy to confuse from their names alone.
     *
     * <p>Function setFlags coded on 260816, commented in full on 260816.
     *
     * @param mask the flags to switch on; read, never retained
     * @return {@code true} if any flag was not already set
     */
    public boolean setFlags(Flag<ObjectFlag> mask) {
        return flags.union(mask);
    }

    /**
     * Switches on a single flag — the port of C's {@code of_on(obj->flags, flag)}.
     *
     * <p>On a known counterpart this records that the item has had its chance to display the
     * property and did not, which is knowledge in the negative: enough such rulings identify an item
     * by use rather than by examination. It is not rune-learning and does not go near
     * {@code learnRune}'s guard — what is being recorded is a fact about this item, not something
     * the player now understands in general.
     *
     * <p>Function setFlag coded on 260816, commented in full on 260816.
     *
     * @param flag the flag to switch on
     * @return {@code true} if the flag was not already set
     */
    public boolean setFlag(ObjectFlag flag) {
        return flags.set(flag);
    }

    /**
     * Replaces this item's flags with the given set — the port of C's {@code of_wipe} followed by
     * {@code of_copy}.
     *
     * <p>Copies in. The argument stays the caller's and the two sets share nothing afterwards, which
     * is the point: assigning the reference instead would leave a known counterpart holding its
     * item's own set, after which knowledge and truth are the same object and can never diverge.
     * {@link Flag#copyFrom} wipes before it unions, so the wipe does not need saying twice.
     *
     * <p>Replaces; it does not add. {@link #setFlags} is the one that adds.
     *
     * <p>Function setFlagsTo coded on 260816, commented in full on 260816.
     *
     * @param flags the flags this item should end up with; read, never retained
     */
    public void setFlagsTo(Flag<ObjectFlag> flags) {
        this.flags.copyFrom(flags);
    }

    /**
     * Records that this item carries a brand, the port of C's {@code obj->brands[i] = true}.
     *
     * <p>The three brand mutators exist because {@link #getBrands()} answers {@code Set.of()} for an
     * item whose set has never been created, and an immutable empty set takes no writes. The
     * knowledge code writes brands onto counterpart objects built by the no-argument constructor,
     * which are exactly those items, so the set is created here on demand — C reaches the same place
     * with the {@code mem_zalloc} its own brand block performs before its first write.
     *
     * <p>Membership is the whole of the state, so adding a brand twice is not distinguishable from
     * adding it once. C's array of {@code bool} says the same thing.
     *
     * <p>Function addBrand coded on 260817, commented in full on 260817.
     *
     * @param brand the brand this item carries
     */
    public void addBrand(Brand brand) {
        if (brands == null) {
            brands = new HashSet<>();
        }
        brands.add(brand);
    }

    /**
     * Records that this item does not carry a brand, the port of C's {@code obj->brands[i] = false}.
     *
     * <p>Removal rather than a stored false, absence being how this port says "not branded" where C
     * has a slot for every brand and writes a boolean into it. The two agree because nothing here
     * stores a brand it does not mean.
     *
     * <p>Removing a brand the item does not carry is not an error; C assigns false over false.
     *
     * <p>Function removeBrand coded on 260817, commented in full on 260817.
     *
     * @param brand the brand to take off
     */
    public void removeBrand(Brand brand) {
        if (brands == null) {
            brands = new HashSet<>();
        }
        brands.remove(brand);
    }

    /**
     * Takes every brand off this item, the port of C freeing the brand array and nulling the pointer.
     *
     * <p>{@code knowObject} uses it on a counterpart whose item turned out to carry no brand the
     * player recognises — C's {@code if (!known_brand) { mem_free(...); obj->known->brands = NULL; }}.
     *
     * <p>Leaves an empty set rather than a null one. Callers cannot tell the two apart, {@link
     * #getBrands()} reporting empty for both, which is why the null field never needs restoring.
     *
     * <p>Function clearBrands coded on 260817, commented in full on 260817.
     */
    public void clearBrands() {
        if (brands == null) {
            brands = new HashSet<>();
        }
        brands.clear();
    }

    /**
     * Records that this item carries a slay, the port of C's {@code obj->slays[i] = true}. The slay
     * counterpart of {@link #addBrand}, and there for the same reason: {@link #getSlays()} answers
     * {@code Set.of()} for an item whose set has never been created.
     *
     * <p>Function addSlay coded on 260817, commented in full on 260817.
     *
     * @param slay the slay this item carries
     */
    public void addSlay(Slay slay) {
        if (slays == null) {
            slays = new HashSet<>();
        }
        slays.add(slay);
    }

    /**
     * Records that this item does not carry a slay, the port of C's {@code obj->slays[i] = false}.
     * See {@link #removeBrand} for why absence stands in for C's stored false.
     *
     * <p>Function removeSlay coded on 260817, commented in full on 260817.
     *
     * @param slay the slay to take off
     */
    public void removeSlay(Slay slay) {
        if (slays == null) {
            slays = new HashSet<>();
        }
        slays.remove(slay);
    }

    /**
     * Takes every slay off this item, the port of C freeing the slay array. See {@link #clearBrands}.
     *
     * <p>Function clearSlays coded on 260817, commented in full on 260817.
     */
    public void clearSlays() {
        if (slays == null) {
            slays = new HashSet<>();
        }
        slays.clear();
    }

    /**
     * Whether the player has learned what this object's flavour is — the port of C's
     * {@code object_flavor_is_aware} ({@code obj-knowledge.c:2239-2243}).
     *
     * <p>Awareness belongs to the <em>kind</em>, not to the object: drinking one unlabelled potion
     * teaches the player what every potion of that kind is, so the answer is the same for every
     * object sharing this one's kind. For an unflavoured object the kind is aware from the start.
     *
     * <p>C asserts that the kind exists; the port answers {@code false} for a kindless object
     * instead, which is the safe reading — nothing is known about an object with no kind to know
     * about.
     *
     * <p>Function flavourIsAware commented in full on 260820.
     *
     * @return {@code true} if the player knows what objects of this kind are
     */
    public boolean flavourIsAware() {
        if (kind == null) return false;
        return kind.isAware();
    }

    /**
     * Whether this object is of a kind that gives up everything at a glance — the port of C's
     * {@code easy_know} ({@code obj-knowledge.c:2225-2232}).
     *
     * <p>Both halves are required: the kind must be one the player is aware of, and it must carry
     * {@code KF_EASY_KNOW}. The flag marks kinds with nothing hidden to discover — a scroll's
     * properties are wholly determined by which scroll it is — so once the player recognises the
     * kind there is no further identification to do. {@code flagsKnown} uses it to decide whether an
     * ego's flags may be folded in without the player having learned the individual runes.
     *
     * <p>Function easyKnow commented in full on 260820.
     *
     * @return {@code true} if recognising this object's kind reveals all of its properties
     */
    public boolean easyKnow() {
        if (kind == null) return false;
        return kind.isAware() && kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW);
    }

    /**
     * This object's flags reduced to what the player has actually learned — the port of C's
     * {@code object_flags_known} ({@code obj-util.c:362-379}).
     *
     * <p>Built in three movements, and the order matters. The object's real flags are copied, then
     * <em>intersected</em> with the known counterpart's, which is the whole of the restriction: a
     * flag the player has not learned the rune for drops out here. Awareness then adds back what
     * recognising the kind reveals, and an easy-know ego adds its own flags and removes the ones it
     * suppresses — additions after a restriction, because knowing what something <em>is</em> can
     * tell the player more than they learned by carrying it.
     *
     * <p>Returns a fresh set rather than filling a caller's, and an object with no known counterpart
     * returns an empty one rather than its real flags — nothing is known. C has neither case: it
     * wipes the caller's buffer first and dereferences {@code obj->known} unguarded.
     *
     * <p>Function flagsKnown commented in full on 260820.
     *
     * @return a new flag set holding only the flags the player knows this object to have
     */
    public Flag<ObjectFlag> flagsKnown() {
        Flag<ObjectFlag> result = new Flag<>(ObjectFlag.class);
        Flag<ObjectFlag> empty = new Flag<>(ObjectFlag.class);

        result.copyFrom(getFlags());

        if (known == null) return empty;

        result.inter(known.getFlags());

        if (kind == null) return result;

        if (flavourIsAware())
            result.union(kind.getFlags());

        if (ego != null && easyKnow()) {
            result.union(ego.getFlags());
            result.diff(ego.getOffFlags());
        }

        return result;
    }

    /**
     * The object's modifier for one stat, named rather than indexed.
     *
     * <p>Convenience over {@link #getModifierValue(ObjectModifier)}: C subscripts
     * {@code obj->modifiers} with a stat index because {@code list-object-modifiers.h} happens to
     * begin with the five stats in {@code list-stats.h} order. The port resolves
     * {@code STAT_STR} to {@code OM_STR} by name so that correspondence is stated rather than
     * assumed.
     *
     * <p>Function getModifierValue commented in full on 260820.
     *
     * @param stat one of the five real stats; the {@code STAT_NONE} and {@code STAT_MAX} sentinels
     *             have no matching modifier
     * @return the object's modifier for that stat, or zero if it carries none
     * @throws IllegalArgumentException if the stat has no correspondingly named modifier
     */
    public int getModifierValue(Stats stat) {
        return getModifierValue(ObjectModifier.valueOf("OM_" + stat.name().substring(5)));
    }

    /**
     * The object's value for one modifier — C's {@code obj->modifiers[om]}.
     *
     * <p>Raw, and not the whole story where the player's knowledge matters: {@code calcBonuses}
     * multiplies every modifier it reads by the player's rune knowledge for it
     * ({@code player-calcs.c:1943-1970}), so a value returned here may still contribute nothing.
     * A modifier the object does not carry reads as zero, matching C's zeroed array.
     *
     * <p>Function getModifierValue commented in full on 260820.
     *
     * @param om the modifier to read
     * @return the object's value for it, or zero
     */
    public int getModifierValue(ObjectModifier om) {
        return modifiers.getOrDefault(om, 0);
    }

    /**
     * The weight of a single one of these, after its curses have had their say — the port of C's
     * {@code object_weight_one} ({@code obj-util.c:274-289}).
     *
     * <p>One, not the stack: a pile of twenty arrows answers with the weight of one arrow. Callers
     * wanting the burden of the stack multiply by the count themselves, as C does.
     *
     * <p>Curses can make an item heavier or lighter, and they compose: each curse of non-zero power
     * is applied in turn to the running result, so two weight curses both take effect rather than
     * the last one winning. A curse present at zero power is skipped — it is recorded on the object
     * but not active. The base weight is floored at zero before any curse sees it.
     *
     * <p>Function weightOne commented in full on 260820.
     *
     * @return this object's individual weight in tenth-pounds, never negative
     */
    public int weightOne() {
        int result = Math.max(weight, 0);

        for (Curse curse : getCurses().keySet()) {
            if (curses.get(curse).getPower() != 0)
                result = curse.modifyWeightForCurse(result);
        }

        return result;
    }

    public int checkForInscription(String s) {
        if (note == null || s == null || s.isEmpty()) return 0;

        int count = 0;
        int location = 0;

        // Shift indexOf's answer up by one, so its "not found" (-1) becomes 0 and a match at
        // the very start becomes 1. That frees 0 to be the loop's stop value and makes result
        // the position to resume from, both in one number.
        int result = note.indexOf(s, location) + 1;

        // 0 is "no match left", the shifted form of indexOf returning -1
        while (result != 0) {
            count++;

            // Resume one character past the match's first character, not past the whole match,
            // so overlapping occurrences are each counted - "!!!" holds "!!" twice. This is C's
            // s++ in check_for_inscrip (obj-util.c:437).
            location = result;
            result = note.indexOf(s, location) + 1;
        }

        return count;
    }

    public boolean verifyObject(String prompt, Player player) {
        Flag<ObjectDescription> descFlags = new Flag<>(ObjectDescription.class);
        descFlags.on(ObjectDescription.ODESC_PREFIX);
        descFlags.on(ObjectDescription.ODESC_COMBAT);
        descFlags.on(ObjectDescription.ODESC_EXTRA);
        String objectName = description(descFlags, player);

        String out = String.format("%s %s? ", prompt, objectName);

        return GameInputHolder.getInstance().getCheck(out);
    }

    public String setNote(String s) {
        return note;
    }

    public IgnoreType getIgnoreTypeOf() {
        for (ObjectInfo.QualityMapping mapping : ObjectInfo.qualityMapping) {
            if (mapping.tval() == gettValue()) {
                // Is there a matching identifier
                if (!mapping.identifier().isEmpty()) {
                    if (!getKind().getName().equals(mapping.identifier())) {
                        continue;
                    }
                }
                return mapping.ignoreType();
            }
        }

        return IgnoreType.ITYPE_MAX;
    }

    public boolean isEgo() {
        return ego != null;
    }

    public boolean egoIsIgnored(IgnoreType type) {
        if (ego == null) return false;
        return ego.getIgnoreType(type);
    }

    public QualityValueEnum ignoreLevelOf() {
        if (!isKnown()) return QualityValueEnum.IGNORE_MAX;

        // Jewellery treated specially
        if (tValue.isJewelry()) {
            // One positive modifier means not bad
            for (ObjectModifier mod : this.modifiers.keySet()) {
                if (modifiers.get(mod) > 0)
                    return QualityValueEnum.IGNORE_AVERAGE;
            }

            // One positive combat value means not bad
            if (known.toHit > 0 || known.toDam > 0 || known.toAC > 0)
                return QualityValueEnum.IGNORE_AVERAGE;
            if (known.toHit < 0 || known.toDam < 0 || known.toAC < 0)
                return QualityValueEnum.IGNORE_BAD;

            return QualityValueEnum.IGNORE_AVERAGE;
        }

        // Now just bad, average, good, ego
        QualityValueEnum value;
        if (isFullyKnown()) {
            int isGood = isGood();

            if (isGood > 0)
                value = QualityValueEnum.IGNORE_GOOD;
            else if (isGood < 0)
                value = QualityValueEnum.IGNORE_BAD;
            else
                value = QualityValueEnum.IGNORE_AVERAGE;

            if (isEgo())
                value = QualityValueEnum.IGNORE_ALL;
            else if (isArtifact())
                value = QualityValueEnum.IGNORE_MAX;
        } else {
            if (known.notice.on(ObjectNotice.OBJ_NOTICE_ASSESSED) && !isArtifact())
                value = QualityValueEnum.IGNORE_ALL;
            else
                value = QualityValueEnum.IGNORE_MAX;
        }

        return value;
    }

    private int isGood() {
        int good = 0;

        good += 4 * compareObjectTrait(toDam, kind.getToD());
        good += 2 * compareObjectTrait(toHit, kind.getToH());
        good += compareObjectTrait(toAC, kind.getToA());
        return good;
    }

    private int compareObjectTrait(int bonus, Random base) {
        int amount = base.randCalc(0, DamageAspect.MINIMIZE);

        if (amount > 0) amount = 0;
        return NumberUtils.cmp(bonus, amount);

    }

    public boolean isInQuiver(Player player) {
        for (ItemObject item : player.getPlayerUpkeep().getQuiver()) {
            if (item == this) return true;
        }

        return false;
    }

    public boolean mergeable(ItemObject toMerge, Flag<ObjectStackEnum> stackModes) {
        int total = this.number + toMerge.number;

        if (!stackModes.has(ObjectStackEnum.OSTACK_STORE)) {
            if (total > toMerge.getKind().getBase().getMaxStack()) return false;
        }

        // Quiver can impose stricter limits
        if (stackModes.has(ObjectStackEnum.OSTACK_QUIVER)) {
            if (toMerge.gettValue().isAmmo()) {
                if (total > GameConstants.getCarryCapQuiverSize()) return false;
            } else {
                if (total > GameConstants.getCarryCapQuiverSize()
                        / GameConstants.getCarryCapThrownQuiverMult()) return false;
            }
        }

        return objectStackable(toMerge, stackModes);
    }

    public boolean objectStackable(ItemObject toMerge, Flag<ObjectStackEnum> stackModes) {
        if (similar(toMerge, stackModes)) {
            return toMerge.getNote() != null || this.getNote() != null || toMerge.getNote().equals(this.getNote());
        }

        return false;
    }

    public void objectAbsorb(ItemObject toAbsorb) {
        ItemObject known = toAbsorb.getKnown();
        Player player = GameState.getPlayer();

        int total = this.number + toAbsorb.number;

        this.number = Math.min(total, this.getKind().getBase().getMaxStack());

        this.objectAbsorbMerge(toAbsorb, player, true);
        if (known != null) {
            if (known.getGrid() != Loc.zero)
                player.getCave().getSquare(known.getGrid()).pileExcise(known);
        }
    }

    private void objectAbsorbMerge(ItemObject toAbsorb, Player player, boolean combineChargesTimeouts) {
        int total;

        // This object gains extra knowledge from toMerge
        if (this.getKnown() != null && toAbsorb.getKnown() != null) {
            if (toAbsorb.getKnown().getEffect() != null)
                this.effect = new ArrayList<>(toAbsorb.getKnown().getEffect());
            player.knowObject(this);
        }

        if (toAbsorb.getNote() != null)
            this.note = toAbsorb.getNote();

        // Combine tValues information
        if (combineChargesTimeouts) {
            // Rods
            if (this.gettValue().canHaveTimeout())
                this.timeout += toAbsorb.getTimeout();

            // wands and staves
            if (this.gettValue().canHaveCharges() || this.gettValue().isMoney()) {
                total = this.getpValue() + toAbsorb.getpValue();
                this.pValue = Math.min(total, GameConstants.MAX_PVAL);
            }
        }

        // Combine origin as best we can
        this.originCombine(toAbsorb);
    }

    public void nullKnown() {
        this.known = null;
    }

    public void objectAbsorbPartial(ItemObject item2,
                                    Flag<ObjectStackEnum> stackMode1,
                                    Flag<ObjectStackEnum> stackMode2) {
        int smallest = Math.min(this.getNumber(), item2.getNumber());
        int largest = Math.max(this.getNumber(), item2.getNumber());
        int newThisSize;
        int newItm2Size;

        if (stackMode1.has(ObjectStackEnum.OSTACK_STORE) || stackMode2.has(ObjectStackEnum.OSTACK_STORE)) return;

        // Quivers can have stricter limits
        if (stackMode1.has(ObjectStackEnum.OSTACK_QUIVER)) {
            int limit = GameConstants.getCarryCapQuiverSlotSize() /
                    (this.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult());

            if (stackMode2.has(ObjectStackEnum.OSTACK_QUIVER)) {
                int difference = limit - largest;
                newThisSize = largest + difference;
                newItm2Size = smallest - difference;
            } else {
                newThisSize = limit;
                newItm2Size = largest + smallest - limit;
                if (newItm2Size >= this.getKind().getBase().getMaxStack()) return;
            }
        } else if (stackMode2.has(ObjectStackEnum.OSTACK_QUIVER)) {
            // Handle possible different limits
            int limit = GameConstants.getCarryCapQuiverSlotSize()
                    / (item2.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult());

            newThisSize = largest + smallest - limit;
            newItm2Size = limit;
            if (newItm2Size >= this.getKind().getBase().getMaxStack()) return;
        } else {
            int difference = this.getKind().getBase().getMaxStack() - largest;

            newThisSize = largest + difference;
            newItm2Size = largest - difference;
        }

        item2.distributeCharges(this, item2.getNumber() - newItm2Size, false);
        this.setNumber(newThisSize);
        item2.setNumber(newItm2Size);

        objectAbsorbMerge(item2, GameState.getPlayer(), this.gettValue().isMoney());
    }
}