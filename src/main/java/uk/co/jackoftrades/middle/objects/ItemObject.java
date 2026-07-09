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
import uk.co.jackoftrades.backend.enums.DamageAspect;
import uk.co.jackoftrades.backend.numerics.Random;
import uk.co.jackoftrades.backend.parser.itemobject.ItemObjectParser;
import uk.co.jackoftrades.backend.strings.Quark;
import uk.co.jackoftrades.backend.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @author Rowan Crowther
 */
public class ItemObject {
    /**
     * A curse paired with its instance data, as carried by a live item.
     *
     * @param curse     the curse
     * @param curseData its instance data (power/timeout)
     * @author Rowan Crowther
     */
    public record CurseEntry(Curse curse, CurseData curseData) {
    }

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
     * The player's known/identified view of this item.
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
     * To-armour-class bonus, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random toAC;
    /**
     * To-damage bonus, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random toDam;
    /**
     * To-hit bonus, as a dice expression.
     *
     * @author Rowan Crowther
     */
    private Random toHit;

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
    private Map<Brand, Boolean> brands;
    /**
     * Slays on the item (mapped to whether intrinsic).
     *
     * @author Rowan Crowther
     */
    private Map<Slay, Boolean> slays;
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
    private Quark note;

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
     * @param toAC            to-AC dice string
     * @param baseDamage      base-damage dice string
     * @param toDam           to-damage dice string
     * @param toHit           to-hit dice string
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
                      int damageSides, int normalAC, String toAC,
                      String baseDamage, String toDam, String toHit,
                      Flag<ObjectFlag> flags,
                      Map<ObjectModifier, String> modifiers,
                      Map<ElementEnum, ElementInfo> elInfo,
                      Map<Brand, Boolean> brands,
                      Map<Slay, Boolean> slays,
                      Map<ItemObjectParser.CurseEntry, Boolean> curses,
                      List<Effect> effect, String effectMessage,
                      List<Activation> activation, String time,
                      int timeout, int number,
                      Flag<ObjectNotice> notice, int heldMIndex,
                      int mimickingMIndex,
                      ObjectOriginEnum origin, int originDepth,
                      MonsterRace originRace, Quark note) {
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
        this.toAC = Random.parseStr(toAC);
        this.baseDamage = Random.parseStr(baseDamage);
        this.toDam = Random.parseStr(toDam);
        this.toHit = Random.parseStr(toHit);
        this.flags = flags;
        this.modifiers = modifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = new HashMap<>();
        for (ItemObjectParser.CurseEntry ce : curses.keySet()) {
            Boolean b = curses.get(ce);
            CurseEntry curseEntry = new CurseEntry(ce.curse(), ce.curseData());
            this.curses.put(curseEntry, b);
        }
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

    public void setGrid(Loc grid) {
        location = grid;
    }

    public void orNotice(ObjectNotice notice) {
        this.notice.on(notice);
    }

    public boolean isArtifact() {
        return artifact != null;
    }

    public ObjectKind getKind() {
        return kind;
    }

    public void setHeldMIndex(int heldMIndex) {
        this.heldMIndex = heldMIndex;
    }

    public void setMimickingMIndex(int mimickingMIndex) {
        this.mimickingMIndex = mimickingMIndex;
    }

    @CheckReturnValue
    @Contract(pure = true)
    public boolean similar(@NotNull ItemObject itm2, @NotNull Flag<ObjectStackEnum> mode) {
        Player player = GameConstants.mainPlayer;

        // Check for equipped items
        if (player.getPlayerBody().isEquipped(this)) return false;
        if (player.getPlayerBody().isEquipped(itm2)) return false;

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

            if (!thisELFlags.has(ElementInfoEnum.EL_INFO_HATES)) thisELFlags.on(ElementInfoEnum.EL_INFO_HATES);
            if (!thisELFlags.has(ElementInfoEnum.EL_INFO_IGNORE)) thisELFlags.on(ElementInfoEnum.EL_INFO_IGNORE);

            if (!itm2ELFlags.has(ElementInfoEnum.EL_INFO_HATES)) itm2ELFlags.on(ElementInfoEnum.EL_INFO_HATES);
            if (!itm2ELFlags.has(ElementInfoEnum.EL_INFO_IGNORE)) itm2ELFlags.on(ElementInfoEnum.EL_INFO_IGNORE);

            if (thisELFlags.equals(itm2ELFlags)) return false;
        }

        if (this.artifact != null || itm2.artifact != null) return false;

        // Analyse the items
        TValue tVal = this.tValue;
        if (tVal.isChest()) {
            return false;
        } else if (tVal.isEdible() || tVal.isPotion() || tVal.isScroll() || tVal.isRod()) {
            return true;
        } else if (tVal.canHaveCharges() || tVal.isMoney()) {
            if (this.pValue + itm2.pValue > GameConstants.MAX_PVAL)
                return false;
        } else if (tVal.isWeapon() || tVal.isArmour() || tVal.isJewelry() || tVal.isLight()) {
            boolean thisKnown = fullyKnown();
            boolean itm2Known = itm2.fullyKnown();

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
                if (this.modifiers.get(mod) != itm2.modifiers.get(mod)) return false;
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
     * Checks to see whether this object is known fully
     *
     * @return true if the player has full knowledge of this object
     */
    private boolean fullyKnown() {
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
            boolean uniqThis = (this.originRace.getFlags().has(MonsterRaceFlag.RF_UNIQUE));
            boolean uniqItem = (item.originRace != null && item.originRace.getFlags().has(MonsterRaceFlag.RF_UNIQUE));

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
     * TODO: Check this out once you understand the quark situation
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
            GameConstants.mainPlayer.knowObject(this);
        }

        if (item.note != null)
            this.note.merge(item.note);

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
        Chunk playerCave = GameConstants.mainPlayer.getCave();

        absorbMerge(item, true);
        if (itemKnown != null) {
            if (!itemKnown.location.equals(Loc.zero))
                playerCave.squareExciseObject(itemKnown.location, itemKnown);

            playerCave.delistObject(itemKnown);
            playerCave.objectDelete(null, itemKnown);
        }

        GameConstants.cave.objectDelete(playerCave, item);
    }
}