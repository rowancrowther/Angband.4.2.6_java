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
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
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
import uk.co.jackoftrades.middle.objects.Curse.CurseEntry;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.Player;

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
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The object kind this item is an instance of.
     *
     * @author Rowan Crowther
     */
    private ObjectKind kind;
    /**
     * The ego type applied to this item, if any.
     *
     * @author Rowan Crowther
     */
    private EgoItem ego;
    /**
     * The artifact this item is, if any.
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
     */
    private ItemObject known;

    /**
     * The grid this item lies on (when on the floor).
     *
     * @author Rowan Crowther
     */
    private Loc location;

    /**
     * The item type value (tval).
     *
     * @author Rowan Crowther
     */
    private TValue tValue;
    /**
     * The sub-type value (sval).
     *
     * @author Rowan Crowther
     */
    private int sValue;

    /**
     * The item's extra parameter value (pval).
     *
     * @author Rowan Crowther
     */
    private int pValue;

    /**
     * The item's weight.
     *
     * @author Rowan Crowther
     */
    private int weight;

    /**
     * Number of damage dice.
     *
     * @author Rowan Crowther
     */
    private int damageDice;
    /**
     * Sides per damage die.
     *
     * @author Rowan Crowther
     */
    private int damageSides;
    /**
     * Base damage, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random baseDamage;
    /**
     * Base armour class.
     *
     * @author Rowan Crowther
     */
    private int normalAC;
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
     *
     * @author Rowan Crowther
     */
    private int toAC;
    /**
     * This item's own to-damage bonus, rolled at generation. C's {@code obj->to_d}. See
     * {@link #toAC} for why the instance holds a number and the kind holds dice.
     *
     * <p>Field toDam coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815.
     *
     * @author Rowan Crowther
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
     *
     * @author Rowan Crowther
     */
    private int toHit;

    /**
     * The item's object flags.
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectFlag> flags;
    /**
     * The item's numeric modifiers (as unparsed dice strings), keyed by modifier.
     *
     * @author Rowan Crowther
     */
    private Map<ObjectModifier, String> modifiers;
    /**
     * Per-element relation info.
     *
     * @author Rowan Crowther
     */
    private Map<ElementEnum, ElementInfo> elInfo;
    /**
     * Brands on the item (mapped to whether intrinsic).
     *
     * @author Rowan Crowther
     */
    private Set<Brand> brands;
    /**
     * Slays on the item (mapped to whether intrinsic).
     *
     * @author Rowan Crowther
     */
    private Set<Slay> slays;
    /**
     * Curses on the item (mapped to whether intrinsic).
     *
     * @author Rowan Crowther
     */
    private Map<CurseEntry, Boolean> curses;

    /**
     * Effects this item produces when used.
     *
     * @author Rowan Crowther
     */
    private List<Effect> effect;
    /**
     * Message shown when the item's effect fires.
     *
     * @author Rowan Crowther
     */
    private String effectMessage;
    /**
     * Activations available on this item.
     *
     * @author Rowan Crowther
     */
    private List<Activation> activation;
    /**
     * Recharge time, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random time;
    /**
     * Turns until the item can be used again (0 = ready).
     *
     * @author Rowan Crowther
     */
    private int timeout;

    /**
     * Quantity in this stack.
     *
     * @author Rowan Crowther
     */
    private int number;
    /**
     * The player's notice flags for this item (worn/assessed/ignore/imagined).
     *
     * @author Rowan Crowther
     */
    private Flag<ObjectNotice> notice;

    /**
     * Index of the monster holding this item, or 0 if not held.
     *
     * @author Rowan Crowther
     */
    private int heldMIndex;
    /**
     * Index of the monster mimicking this item, or 0 if none.
     *
     * @author Rowan Crowther
     */
    private int mimickingMIndex;

    /**
     * Where this item came from (for the description history line).
     *
     * @author Rowan Crowther
     */
    private ObjectOriginEnum origin;
    /**
     * The depth at which the item originated.
     *
     * @author Rowan Crowther
     */
    private int originDepth;
    /**
     * The monster race that dropped the item, if applicable.
     *
     * @author Rowan Crowther
     */
    private MonsterRace originRace = new MonsterRace();

    /**
     * The player's inscription on the item.
     *
     * @author Rowan Crowther
     */
    private String note;

    /**
     * Build an empty item (used as a blank slot/placeholder).
     *
     * @author Rowan Crowther
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
     * @author Rowan Crowther
     */
    public ItemObject(ObjectKind kind, EgoItem ego,
                      Artifact artifact, ItemObject known,
                      Loc location, TValue tValue, int sValue,
                      String pValue, int weight, int damageDice,
                      int damageSides, int normalAC, int toAC,
                      String baseDamage, int toDam, int toHit,
                      Flag<ObjectFlag> flags,
                      Map<ObjectModifier, String> modifiers,
                      Map<ElementEnum, ElementInfo> elInfo,
                      Set<Brand> brands, Set<Slay> slays,
                      Map<CurseEntry, Boolean> curses,
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
        this.normalAC = normalAC;
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
        for (ElementEnum e : ElementEnum.values()) {
            if (this.elInfo.get(e).getResLevel() != itm2.elInfo.get(e).getResLevel()) return false;

            Flag<ElementInfoEnum> thisELFlags = this.elInfo.get(e).getFlags();
            Flag<ElementInfoEnum> itm2ELFlags = itm2.elInfo.get(e).getFlags();

            boolean thisHates = thisELFlags.has(ElementInfoEnum.EL_INFO_HATES);
            boolean thisIgnores = thisELFlags.has(ElementInfoEnum.EL_INFO_IGNORE);
            boolean itm2Hates = itm2ELFlags.has(ElementInfoEnum.EL_INFO_HATES);
            boolean itm2Ignores = itm2ELFlags.has(ElementInfoEnum.EL_INFO_IGNORE);

            if (thisHates != itm2Hates || thisIgnores != itm2Ignores) return false;
        }

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
            if (this.normalAC != itm2.normalAC) return false;
            if (this.damageDice != itm2.damageDice) return false;
            if (this.damageSides != itm2.damageSides) return false;

            // identical bonuses
            if (this.toHit != itm2.toHit) return false;
            if (this.toDam != itm2.toDam) return false;
            if (this.toAC != itm2.toAC) return false;

            // identical modifiers
            for (ObjectModifier mod : ObjectModifier.values()) {
                if (!this.modifiers.get(mod).equals(itm2.modifiers.get(mod))) return false;
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
     * @author Rowan Crowther
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
        return this.curses.equals(itm2.curses);
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

        return nonCurseRunesKnown();
    }

    /**
     * Check to see the knowledge of this verses this.known
     *
     * @return true if this and this.known are known to the player
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean nonCurseRunesKnown() {
        if (known == null) return false;

        if (known.toAC != toAC) return false;
        if (known.toDam != toDam) return false;
        if (known.toHit != toHit) return false;

        for (ObjectModifier mod : ObjectModifier.values()) {
            if (!known.modifiers.get(mod).equals(modifiers.get(mod))) return false;
        }

        for (ElementEnum e : ElementEnum.values()) {
            if (known.elInfo.get(e).getResLevel() == 0
                    && elInfo.get(e).getResLevel() != 0)
                return false;
        }

        if (!known.brands.equals(brands)) return false;

        if (!known.slays.equals(slays)) return false;

        // TODO: Check that this test is the right way round
        return known.flags.isSubset(flags);
    }

    /**
     * Checks whether the player is aware of the object's effect
     *
     * @return true if the object's known effect is the same as its effect
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean effectIsKnown() {
        for (Effect eff : known.effect) {
            if (!effect.contains(eff)) return false;
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
        // In c we check that this and item are not null. We don't need to do that here
        if (!item.known.effect.isEmpty()) {
            this.known.effect = this.effect;
            GameState.getPlayer().knowObject(this);
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

        if (!thisMode.has(ObjectStackEnum.OSTACK_STORE) && !itemMode.has(ObjectStackEnum.OSTACK_STORE)) {
            logger.error("Either this or the incoming object have a store stacking mode set");
            return;
        }

        if (thisMode.has(ObjectStackEnum.OSTACK_QUIVER)) {
            int limit = GameConstants.getCarryCapQuiverSlotSize();
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
            if (newsz1 < this.kind.getBase().getMaxStack()) {
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
        if (item.tValue.canHaveCharges()) {
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

        if (item.tValue.canHaveTimeout()) {
            int chargeTime = this.time.randCalc(0, DamageAspect.AVERAGE);
            int maxTime = chargeTime * amount;

            if (destNew) {
                item.timeout = Math.min(this.timeout, maxTime);
                if (amount < this.number)
                    this.number -= item.number;
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
     * @return this object's curses, each mapped to a boolean marking whether the
     * player is aware of it (unmodifiable) — the port of C's {@code obj->curses}
     * @author Rowan Crowther
     */
    public Map<CurseEntry, Boolean> getCurses() {
        return Collections.unmodifiableMap(curses);
    }

    /**
     * @return the random interval between activations of this object's effect — the
     * port of C's {@code obj->time}; for a curse template this is the dice re-rolled
     * into each cursed object's timeout
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
     */
    public Set<Brand> getBrands() {
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * @author Rowan Crowther
     */
    public boolean isKnown() {
        return known != null;
    }

    /**
     * Learns the to-AC rune, and the curse's own rune, if any curse on this item contributes an
     * armour-class change the player has just felt. The port of C's
     * {@code object_curses_find_to_a} ({@code obj-knowledge.c:1557}), one of six near-identical
     * functions covering to-AC, to-hit, to-damage, flags, modifiers and elements.
     *
     * <p>A curse is a thing the player learns by being bitten by it, which is why this is reached
     * from {@link uk.co.jackoftrades.middle.player.Player#equipLearnOnDefend} rather than from
     * anything to do with inspecting the item. Two runes are learned, not one: the fact that
     * <em>something</em> is altering the armour class, and the identity of the curse doing it.
     *
     * <p><b>Where the numbers come from.</b> The armour-class figure belongs to the curse
     * definition, not to this item — {@link Curse#getCombatAC}, the port of {@code curses[i].obj->to_a},
     * parsed once from {@code curse.txt}. What the item holds is the instance data: the power and
     * timeout in {@link CurseData}. C keeps those in two arrays indexed alike, so every one of these
     * functions has to walk {@code 1 .. curse_max} and read {@code obj->curses[i].power} and
     * {@code curses[i].obj->to_a} at the same subscript. {@link CurseEntry} pairs them directly, so
     * the loop visits only the curses this item actually carries and no index arithmetic survives
     * the port.
     *
     * <p>That also disposes of C's two guards. {@code !obj->curses[i].power} is what stops a dense
     * array from reporting curses the item does not have, and is unnecessary against a map that only
     * contains the ones it does — but the power test is kept anyway, because
     * {@link CurseData#setPower} with a zero is how a curse is removed, so a zeroed entry can
     * outlive the curse. {@code !curses[i].obj} is dead code upstream: the parser allocates that
     * object at the {@code name:} line, so the only null in the array is index 0, the reserved
     * no-curse slot the loop already skips.
     *
     * <p>The rune is resolved once, before the loop. C recomputes it into the same {@code index}
     * variable it then overwrites with the curse's rune, so on a second qualifying curse it relearns
     * the previous curse instead of the to-AC rune — harmless there only because the to-AC rune is
     * already known by that point. Hoisting the lookup out makes the bug unexpressible.
     *
     * <p>Function cursesFindToA coded before 260815, commented in full before 260815.
     *
     * @param player the player doing the learning; knowledge is player state, so the item is only
     *               the thing being read
     * @author Rowan Crowther
     */
    public void cursesFindToA(Player player) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_A);
        if (!curses.isEmpty()) {
            for (CurseEntry curseEntry : curses.keySet()) {
                if (curseEntry.curseData().getPower() != 0)
                    if (curseEntry.curse().getCombatAC() != 0) {
                        // Learn the to AC rune
                        player.learnRune(rune, true);
                        // Learn the to AC Curse rune
                        player.learnRune(Rune.runeIndex(curseEntry.curse()), true);
                    }
            }
        }
    }

    /**
     * Learns the to-damage rune, and the curse's own rune, if any curse on this item contributes
     * a damage change the player has just dealt. The port of C's {@code object_curses_find_to_d}
     * ({@code obj-knowledge.c:1603}), the to-damage sibling of {@link #cursesFindToA}.
     *
     * <p>Structurally identical to that method, and the reasoning there applies unchanged: the
     * figure belongs to the curse definition ({@link Curse#getCombatDam}, C's
     * {@code curses[i].obj->to_d}) rather than to this item, the power test survives because
     * {@link CurseData#setPower} with a zero is how a curse is removed, and the rune is resolved
     * once above the loop so the second qualifying curse cannot relearn the first.
     *
     * <p>What differs is the occasion. This is reached from
     * {@link uk.co.jackoftrades.middle.player.Player#equipLearnOnMeleeAttack} — a curse that saps
     * damage announces itself when a blow lands softly, not when one is taken.
     *
     * <p>Function cursesFindToD coded on 260815, commented in full on 260815.
     *
     * @param player the player doing the learning; knowledge is player state, so the item is only
     *               the thing being read
     * @author Rowan Crowther
     */
    public void cursesFindToD(Player player) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_D);
        if (!curses.isEmpty()) {
            for (CurseEntry curseEntry : curses.keySet()) {
                if (curseEntry.curseData().getPower() != 0)
                    if (curseEntry.curse().getCombatDam() != 0) {
                        // Learn the to-damage rune
                        player.learnRune(rune, true);
                        // Learn the rune of the curse that caused it
                        player.learnRune(Rune.runeIndex(curseEntry.curse()), true);
                    }
            }
        }
    }

    /**
     * Learns the to-hit rune, and the curse's own rune, if any curse on this item contributes an
     * accuracy change the player has just felt. The port of C's {@code object_curses_find_to_h}
     * ({@code obj-knowledge.c:1580}), the to-hit sibling of {@link #cursesFindToA}.
     *
     * <p>Structurally identical to that method, and the reasoning there applies unchanged — see it
     * for why the figure is read from the curse definition ({@link Curse#getCombatToHit}, C's
     * {@code curses[i].obj->to_h}), why the power test is kept, and why the rune is hoisted above
     * the loop.
     *
     * <p>This is the one of the three reached from both attack methods,
     * {@link uk.co.jackoftrades.middle.player.Player#equipLearnOnMeleeAttack} and
     * {@link uk.co.jackoftrades.middle.player.Player#equipLearnOnRangedAttack}: a curse that spoils
     * the player's aim shows itself whichever way they attack.
     *
     * <p>Note that the curse's contribution is judged by a plain non-zero test, with no counterpart
     * to {@link #hasStandardToH}. That asymmetry is correct: "standard" is a fact about what a kind
     * of item normally carries, and a curse has no normal to-hit to be measured against.
     *
     * <p>Function cursesFindToH coded on 260815, commented in full on 260815.
     *
     * @param player the player doing the learning; knowledge is player state, so the item is only
     *               the thing being read
     * @author Rowan Crowther
     */
    public void cursesFindToH(Player player) {
        Rune rune = Rune.runeIndex(CombatRunes.COMBAT_RUNE_TO_H);
        if (!curses.isEmpty()) {
            for (CurseEntry curseEntry : curses.keySet()) {
                if (curseEntry.curseData().getPower() != 0)
                    if (curseEntry.curse().getCombatToHit() != 0) {
                        // Learn the to-hit rune
                        player.learnRune(rune, true);
                        // Learn the rune of the curse that caused it
                        player.learnRune(Rune.runeIndex(curseEntry.curse()), true);
                    }
            }
        }
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
     * @author Rowan Crowther
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
     * <p>This asks what the item <em>is</em>, not what the player knows about it. The two are
     * separate questions throughout the knowledge code and are deliberately kept in separate
     * places: the flags an item has live here, while the flags the player can read live on
     * {@link KnownObject} and are reached through {@link #getKnownFlags}. C's
     * {@code equip_learn_flag} plays the two against each other — an item that has the flag may
     * teach it, an item that does not gets the flag marked on its known counterpart as having been
     * ruled out.
     *
     * <p>Function hasFlag coded on 260815, commented in full on 260815.
     *
     * @param flag the flag to test for
     * @return whether this item carries it
     * @author Rowan Crowther
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
     * is ported. The placeholder is deliberately conspicuous rather than empty, because the return
     * value is not inspected by its callers — {@link #flagMessage} substitutes it straight into a
     * message and shows it to the player. An empty string would produce "Your  glows." and read as
     * a spacing bug; the tag reads as a thing not yet built.
     *
     * <p>Function description coded on 260815, commented in full on 260815.
     *
     * @param descriptionFlags how much of the name to build, C's {@code mode}
     * @param player           the player whose knowledge decides what may appear in the name
     * @return the item's name; the placeholder tag while stubbed
     * @author Rowan Crowther
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
     * @author Rowan Crowther
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
     * Returns the live flag set, not a copy — C hands out {@code obj->flags} as an array on the
     * struct and callers write to it in place.
     *
     * <p>Package-private on purpose. The only caller is {@link #getKnownFlags}, reaching across to
     * the known counterpart of the same class; outside this class the flags an item has are read
     * through {@link #hasFlag}, and nothing outside has business writing them.
     *
     * <p>Function getObjectFlags coded on 260815, commented in full on 260815.
     *
     * @return this item's flags, shared with this instance
     * @author Rowan Crowther
     */
    Flag<ObjectFlag> getObjectFlags() {
        return flags;
    }

    /**
     * Returns the flag set on this item's known counterpart — the flags the player can currently
     * read — as C reaches {@code obj->known->flags}. Live, not a copy, because the point of it is to
     * be written to: {@code equip_learn_flag} switches a flag on here to record that the item has
     * had its chance to display that property and did not.
     *
     * <p>That is a subtle piece of bookkeeping. A flag being on the known set means the player has
     * settled the question, which includes settling it in the negative — the item was worn through
     * an event that would have revealed the flag, so its absence is now knowledge rather than
     * ignorance. It is how an item becomes fully identified by being used rather than by being
     * examined.
     *
     * <p>Answers {@code null} rather than throwing when there is no known counterpart. C is
     * entitled to dereference {@code obj->known} straight away because {@code assert(obj->known)}
     * has just run; the port drops those asserts (see
     * {@link uk.co.jackoftrades.middle.player.Player#equipLearnOnDefend}), so the null has to be
     * expressible instead. Every live item has a counterpart from the moment it is created, so a
     * null here means a fixture or a generation path that skipped it — see {@link #isKnown} for why
     * the presence of that companion is not the same as the item being identified.
     *
     * <p>Function getKnownFlags coded on 260815, commented in full on 260815.
     *
     * @return the known counterpart's flags, or {@code null} if this item has no counterpart
     * @author Rowan Crowther
     */
    public Flag<ObjectFlag> getKnownFlags() {
        if (known == null) return null;

        return known.getObjectFlags();
    }

    /**
     * Learns any of the given flags that a curse on this item has just betrayed, together with
     * the rune of the curse betraying them — the port of C's {@code object_curses_find_flags}
     * ({@code obj-knowledge.c:1634}), the flag member of the same family as {@link #cursesFindToA}
     * and its two siblings.
     *
     * <p><b>Why this one takes a set where the others take nothing.</b> The to-AC, to-hit and
     * to-damage finders each pursue a single fixed property, so the caller has nothing to say. Flags
     * are a population, and the caller decides which of them this occasion could plausibly have
     * revealed. C passes that as a {@code bitflag *test_flags} and intersects it with the curse's
     * own flags, keeping only what is in both. Three call sites, three different sets: a one-element
     * set built on the spot by {@code equip_learn_flag}, the {@code obvious_mask} of everything a
     * wield could show, and the {@code timed_mask} of what only prolonged wear reveals.
     *
     * <p>As in the sibling finders, two runes are learned per hit and not one — the flag itself, and
     * the identity of the curse that carries it. The curse's rune is learned whether or not the flag
     * was new, since meeting a curse is knowledge even when its effect was already understood.
     *
     * <p><b>The intersection is taken on a copy, and it has to be.</b> {@link Flag#inter} is
     * {@code retainAll} — it mutates the set it is called on. The flags being intersected belong to
     * the {@link Curse} definition parsed once from {@code curse.txt} and shared by every item
     * carrying that curse, so intersecting them in place would permanently delete from the
     * definition every flag this one occasion happened not to be asking about.
     * {@link Flag#set(java.util.List)} copies element by element into a fresh set, which is what
     * keeps the definition intact.
     *
     * <p><b>The curse's rune is learned inside the flag loop, not beside it.</b> That is C's
     * placement and it is load-bearing in one direction: a curse whose flags do not meet the test
     * set teaches nothing at all, not even its own existence, because the player has had no
     * evidence of it. It also means a curse matching two flags learns its rune twice, which the
     * guard inside {@link uk.co.jackoftrades.middle.player.Player#learnRune} makes harmless.
     *
     * <p><b>The message is conditional where the learning is not.</b> C wraps only
     * {@code flag_message} in {@code p->upkeep->playing}, so knowledge is recorded during character
     * generation and loading but nothing is announced into a game that has not started. The
     * returned {@code boolean} is C's {@code new} — true if any flag was learned that was not
     * already known, ignored by this caller and used by the wield-time learning.
     *
     * <p>The per-curse guard is on {@link CurseData#getPower}, as in the three sibling finders and
     * as C's {@code if (!obj->curses[i].power)} requires. Power is what says the curse is on the
     * item at all — {@link CurseData#setPower} with a zero is how a curse is removed, so a zeroed
     * entry can outlive the curse it names. C's second guard, {@code !curses[i].obj}, has no
     * counterpart: it exists to skip the reserved index 0 of a dense array, and a map holding only
     * the curses this item carries has no such hole.
     *
     * <p><b>Outstanding:</b> {@link ItemObject#description} is still a stub, so the message names
     * the item with a placeholder.
     *
     * <p>Function cursesFindFlags coded on 260815, commented in full on 260815, implemented on
     * 260815 having landed as a stub earlier the same day.
     *
     * @param player    the player doing the learning; knowledge is player state, so the item is only
     *                  the thing being read
     * @param testFlags the flags this occasion could have revealed, C's {@code test_flags}
     * @return whether any flag was learned that the player did not already know
     * @author Rowan Crowther
     */
    public boolean cursesFindFlags(Player player, Flag<ObjectFlag> testFlags) {
        boolean curseLearned = false;

        Flag<ObjectDescription> baseDesc = new Flag<>(ObjectDescription.class);
        baseDesc.on(ObjectDescription.ODESC_BASE);
        String name = description(baseDesc, player);

        if (curses.isEmpty()) return false;

        // Only loop through the curses on the object, not the entire set of curses
        for (CurseEntry curseEntry : curses.keySet()) {
            if (curseEntry.curseData().getPower() == 0) continue;

            Flag<ObjectFlag> toTest = new Flag<>(ObjectFlag.class);
            toTest.set(curseEntry.curse().getObjectFlags());
            toTest.inter(testFlags);

            for (ObjectFlag testSubject : toTest) {
                if (!player.hasKnownFlag(testSubject)) {
                    curseLearned = true;
                    player.learnRune(Rune.runeIndex(testSubject), true);
                    if (player.getPlayerUpkeep().isPlaying())
                        flagMessage(testSubject, name);
                }

                // Learn the curse
                Rune rune = Rune.runeIndex(curseEntry.curse());
                if (rune != null)
                    player.learnRune(rune, true);
            }
        }

        return curseLearned;
    }
}