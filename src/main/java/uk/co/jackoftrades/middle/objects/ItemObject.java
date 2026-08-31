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
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.middle.Message;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.enums.DamageAspect;
import uk.co.jackoftrades.middle.enums.MessageType;
import uk.co.jackoftrades.middle.enums.Stats;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.gameinput.GameInputHolder;
import uk.co.jackoftrades.middle.numerics.Guards;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.cave.Loc;
import uk.co.jackoftrades.middle.effect.Effect;
import uk.co.jackoftrades.middle.enums.ElementInfoEnum;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterRace;
import uk.co.jackoftrades.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftrades.middle.objects.enums.*;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.middle.strings.MessageTag;
import uk.co.jackoftrades.middle.utils.NumberUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum.ORIGIN_MIXED;
import static uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum.ORIGIN_NONE;

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
     * The player this object's calculations are asked about - the equipment slots it would be worn
     * in, the quiver it might sit in, the cave its knowledge lives in.
     *
     * <p>Static, and so shared by every item: C reaches the same information through its
     * {@code player} global, and the port keeps the shape rather than threading a player through
     * every power and pricing call.
     */
    private static Player player;

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
     * The sentinel resistance level meaning "vulnerable and resistant at once", which the curse
     * merge uses while combining and then flattens to plain zero before the caller sees it - the
     * port of C's magic {@code -32768} in {@code apply_curse_attributes} ({@code obj-curse.c}).
     *
     * <p>Spelled as the minimum {@code short} because that is what the value is in C, where the
     * field it lives in is an {@code int16_t}.
     */
    private final int VULN_AND_RES = Short.MIN_VALUE;

    /**
     * The player's inscription on the item.
     */
    private String note;
    /**
     * The monster race that dropped the item, if applicable.
     */
    private MonsterRace originRace = null;

    /**
     * Build an empty item (used as a blank slot/placeholder).
     */
    public ItemObject() {
        player = GameState.getPlayer();
        origin = ObjectOriginEnum.ORIGIN_NONE;
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
        player = GameState.getPlayer();
    }

    /**
     * Decides whether one object should be listed before another - the port of C's
     * {@code earlier_object} ({@code obj-gear.c}).
     *
     * <p>Answers for the pack ordering that {@code calcInventory} builds: given the object currently
     * holding a slot and a candidate for it, {@code true} means the candidate belongs earlier.
     *
     * <p>The two null tests come first and are not symmetrical by accident: a null candidate never
     * displaces anything, while a null incumbent is always displaced, which is how the first
     * candidate for an empty slot is accepted.
     *
     * <p>The store flag suppresses the preferences that only make sense for a character's own pack -
     * a shop lists its stock by its own rules.
     *
     * <p>The comparisons run in C's order, each returning as soon as it separates the two: readable
     * books, then usable ammunition, then object type by decreasing tval, then flavour awareness,
     * then sub-type by increasing sval, then unaware flavoured items, then lights by decreasing
     * fuel, and finally value - increasing for ammunition, decreasing for everything else. Two
     * objects that survive all of them are equal in the pack's eyes, and the answer is "no
     * preference".
     *
     * <p>Function earlierObject commented in full on 260827.
     *
     * @param origObj the object currently holding the position, or {@code null}
     * @param newObj  the candidate, or {@code null}
     * @param store   {@code true} when ordering a shop's stock rather than the player's pack
     * @return {@code true} if {@code newObj} should come before {@code origObj}
     */
    public static boolean earlierObject(ItemObject origObj, ItemObject newObj, boolean store) {
        // Are both of the objects real
        if (newObj == null) return false;
        if (origObj == null) return true;

        if (!store) {
            // readable books always come first
            if (origObj.canBrowse() && !newObj.canBrowse()) return false;
            if (!origObj.canBrowse() && newObj.canBrowse()) return true;
        }

        // Usable ammo is before other ammo
        if (origObj.gettValue().isAmmo() && newObj.gettValue().isAmmo()) {
            // first favour usable ammo
            if ((player.getPlayerState().getAmmoTval() == origObj.gettValue()) &&
                    (player.getPlayerState().getAmmoTval() != newObj.gettValue())) return false;
            if ((player.getPlayerState().getAmmoTval() != origObj.gettValue()) &&
                    (player.getPlayerState().getAmmoTval() == newObj.gettValue())) return true;
        }

        // Objects sort by decreasing tvalue ordinals
        if (origObj.gettValue().ordinal() > newObj.gettValue().ordinal()) return false;
        if (origObj.gettValue().ordinal() < newObj.gettValue().ordinal()) return true;

        if (!store) {
            // Non-aware (flavoured) objects always come last
            if (!newObj.flavourIsAware()) return false;
            if (!origObj.flavourIsAware()) return true;
        }

        // Objects sort by increasing sval
        if (origObj.getsValue() < newObj.getsValue()) return false;
        if (origObj.getsValue() > newObj.getsValue()) return true;

        if (!store) {
            // Unaware items always come last
            if (newObj.getKind().getFlavour() != null && !newObj.objectFlavourIsAware()) return false;
            if (origObj.getKind().getFlavour() != null && !origObj.objectFlavourIsAware()) return true;

            // Sort lights by decreasing fuel
            if (origObj.gettValue().isLight()) {
                if (origObj.getpValue() > newObj.getpValue()) return false;
                if (origObj.getpValue() < newObj.getpValue()) return true;
            }
        }

        // Objects sort by decreasing value apart from ammo
        if (origObj.gettValue().isAmmo()) {
            if (origObj.objectValue(1) < newObj.objectValue(1)) return false;
            if (origObj.objectValue(1) > newObj.objectValue(1)) return true;
        } else {
            if (origObj.objectValue(1) > newObj.objectValue(1)) return false;
            if (origObj.objectValue(1) < newObj.objectValue(1)) return true;
        }

        // No preference
        return false;
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
     * <p>Function similar coded before 260822, commented in full on 260824.
     *
     * @param itm2 the other object to compare against
     * @param mode the {@link ObjectStackEnum} flags selecting which stacking rules apply
     * @return {@code true} if the two objects may occupy the same stack
     */
    @CheckReturnValue
    public boolean similar(@NotNull ItemObject itm2, @NotNull Flag<ObjectStackEnum> mode) {
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
        } else if (tVal.isWeapon() || tVal.isArmour() || tVal.isJewellery() || tVal.isLight()) {
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
     * Combine the origin of another object with this one
     *
     * @param item the item to combine into this one if possible
     */
    private void originCombine(@NotNull ItemObject item) {
        if (originRace != item.originRace) {
            boolean uniqThis = (this.originRace != null && this.originRace.hasMonsterRaceFlag(MonsterRaceFlag.RF_UNIQUE));
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
        } else if (this.origin != item.origin || this.originDepth != item.originDepth) {
            this.origin = ORIGIN_MIXED;
        }
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
     * Fills the given set with this item's object flags — the port of C's
     * {@code object_flags(obj, flags)}.
     *
     * <p>The out-parameter form of {@link #getFlags}. C has no way to return an array, so it hands
     * the function a caller-owned {@code bitflag flags[OF_SIZE]} to fill; the callers that already
     * hold a working set — {@code object_flags_known} building on it, {@code obj-power.c} reusing
     * one across an item — are the reason to keep that shape here rather than making every caller
     * take a fresh allocation.
     *
     * <p>The set is wiped before the copy, so whatever the caller had in it is discarded, not
     * merged. That is C's own {@code of_wipe} then {@code of_copy}, and the wipe is what makes a
     * reused buffer safe. {@link Flag#copyFrom} wipes for itself as well, so the explicit call is
     * redundant in Java; it stays because it is the clause C writes, and a reader comparing the two
     * should find them line for line.
     *
     * <p>C guards with {@code if (!obj) return}, leaving the wiped set behind for a null item. An
     * instance method has no such case to answer — a caller with no item cannot reach this at all —
     * so a Java caller that could be holding nothing wipes its own set on that path.
     *
     * <p>Function objectFlags coded on 260829 / commented in full on 260829.
     *
     * @param flag the set to fill; wiped first, then written with this item's flags
     */
    public void objectFlags(Flag<ObjectFlag> flag) {
        flag.wipe();
        flag.copyFrom(this.flags);
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
     * Records this item's resistance level against one element, the port of C's
     * {@code obj->el_info[i].res_level = level}.
     *
     * <p>There is no C function behind this one. {@code res_level} is a plain struct field that C
     * assigns inline wherever it needs to — the data-file parsers at {@code obj-init.c:2058}, the
     * curse stacking in {@code obj-curse.c}, the knowledge code at {@code obj-knowledge.c:1059} and
     * {@code obj-knowledge.c:2153}. The method exists to give those assignments one place to land.
     *
     * <p>The boundary between the two representations is what the body is for. C declares
     * {@code struct element_info el_info[ELEM_MAX]} inside the object struct, so a slot exists for
     * every element from the moment {@code object_new} zero-fills it and the assignment can never
     * fail. Here the map is sparse and may not exist at all — the no-argument constructor leaves it
     * null, and those are exactly the counterpart ("known") items {@code equip_learn_element} writes
     * to. So both the map and the entry are created on demand, and a fresh {@link ElementInfo}
     * starts with empty flags and a zero level, which is what C's zero-fill leaves behind.
     *
     * <p>Writes the level only, leaving {@link ElementInfo#getFlags flags} untouched, as the C
     * assignment does. The knowledge sites that copy both halves ({@code obj-knowledge.c:1059-1060},
     * {@code obj-knowledge.c:2153-2154}) need the flags dealt with separately, or
     * {@link #putElInfo} with a whole value.
     *
     * <p>The level is passed through uninterpreted; the scale is C's, where zero is neutral,
     * positive resists and negative is a vulnerability.
     *
     * <p>Function setElInfoResLevel commented in full on 260830.
     *
     * @param element the element being described
     * @param level   the resistance level to store against it
     */
    public void setElInfoResLevel(ElementEnum element, int level) {
        if (elInfo == null) elInfo = new HashMap<>();
        if (elInfo.get(element) != null)
            elInfo.get(element).setResLevel(level);
        else {
            ElementInfo ei = new ElementInfo();
            ei.setResLevel(level);
            elInfo.put(element, ei);
        }
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
     * Records this item's value for one modifier - the port of assigning into C's
     * {@code obj->modifiers[i]}.
     *
     * <p>Creates the map on demand, for the same reason {@link #putElInfo} does: an item built by
     * the no-argument constructor has none, and {@link #getModifiers()} answers an immutable empty
     * map for that state, which takes no writes.
     *
     * <p>Function putModifier commented in full on 260827.
     *
     * @param modifier the modifier being set
     * @param value    its value on this item
     */
    public void putModifier(ObjectModifier modifier, int value) {
        if (this.modifiers == null) {
            this.modifiers = new HashMap<>();
        }
        this.modifiers.put(modifier, value);
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
            if (getCurses().get(curse) != null && getCurses().get(curse).getPower() != 0)
                result = curse.modifyWeightForCurse(result);
        }

        return result;
    }

    /**
     * Counts how many times an inscription fragment occurs in this object's note - the port of C's
     * {@code check_for_inscrip} ({@code obj-util.c:423}). Callers use it as a yes/no test:
     * a non-zero answer means the tag is present.
     *
     * <p>Occurrences may overlap, because the scan resumes one character past the start of each
     * match rather than past the whole of it - C's {@code s++}. So {@code "!!!"} holds {@code "!!"}
     * twice.
     *
     * <p>An object with no note, and an empty or null fragment, count as zero rather than failing.
     *
     * <p>Function checkForInscription coded before 260822, commented in full on 260824.
     *
     * @param s the inscription fragment to look for, e.g. {@code "!d"}
     * @return the number of occurrences, {@code 0} if none
     */
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

    /**
     * Puts a yes/no question to the player about this object - the port of C's
     * {@code verify_object} ({@code obj-util.c:1072}).
     *
     * <p>The object is described with prefix, combat values and extra detail, and appended to the
     * caller's prompt, so {@code "Really take off and drop"} becomes
     * {@code "Really take off and drop a Long Sword (+3,+4)? "}. The question goes out through
     * {@code GameInputHolder}, which is the boundary the middle layer asks the player through.
     *
     * <p>Function verifyObject coded before 260822, commented in full on 260824.
     *
     * @param prompt the question, without the object name or the question mark
     * @param player the player whose knowledge shapes the description
     * @return {@code true} if the player answered yes
     */
    public boolean verifyObject(String prompt, Player player) {
        Flag<ObjectDescription> descFlags = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_PREFIX,
                ObjectDescription.ODESC_COMBAT, ObjectDescription.ODESC_EXTRA);
        String objectName = description(descFlags, player);

        String out = String.format("%s %s? ", prompt, objectName);

        return GameInputHolder.getInstance().getCheck(out);
    }

    /**
     * Sets this object's inscription, replacing any existing one.
     *
     * <p>{@code null} clears it, and is the normal state - an uninscribed object has no note rather
     * than an empty one, which is why every reader tests for {@code null} first.
     *
     * <p>Field note set here on behalf of C, which writes {@code obj->note} directly as a quark;
     * the port keeps the string.
     *
     * <p>Function setNote coded before 260822, corrected on 260824 to assign its argument,
     * commented in full on 260824.
     *
     * @param note the inscription to store, or {@code null} to clear it
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Reports which ignore category this object falls into - the port of C's
     * {@code ignore_type_of} ({@code obj-ignore.c:382}).
     *
     * <p>The quality mapping table is searched for the first entry matching this object's tval. An
     * entry may narrow that further with an identifier, which has to match the kind's name - that
     * is how, say, diggers are split out from the other tools sharing their tval.
     *
     * <p>Function getIgnoreTypeOf coded on 260822, commented in full on 260824.
     *
     * @return the matching {@link IgnoreType}, or {@link IgnoreType#ITYPE_MAX} if the object is not
     * subject to quality ignoring at all
     */
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

    /**
     * @return {@code true} if this item is an ego item - the port of C's truth test on
     * {@code obj->ego}
     */
    public boolean isEgo() {
        return ego != null;
    }

    /**
     * Answers whether this item's ego is marked ignorable under one category - the port of C's
     * {@code ego_is_ignored(obj->ego->eidx, type)} ({@code obj-ignore.c:606}).
     *
     * <p>Reads the item's <em>real</em> ego, while its one caller gates the question on the
     * <em>known</em> one. That split is C's and is deliberate: an ego the player has not yet learned
     * must not make the item disappear.
     *
     * <p>Function egoIsIgnored commented in full on 260827.
     *
     * @param type the ignore category to test
     * @return {@code true} if this item has an ego and that ego is marked under the category
     */
    public boolean egoIsIgnored(IgnoreType type) {
        if (ego == null) return false;
        return ego.getIgnoreType(type);
    }

    /**
     * Reports the quality band this object would be ignored at - the port of C's
     * {@code ignore_level_of} ({@code obj-ignore.c:464}). The caller compares the answer against
     * the player's setting for the object's {@link IgnoreType}.
     *
     * <p>An object the player does not know returns {@link QualityValueEnum#IGNORE_MAX}, which no
     * setting reaches, so it is never ignored on quality.
     *
     * <p>Jewellery is judged separately and only ever comes back bad or average, because a ring or
     * amulet has no base type to be good relative to. One positive modifier or combat bonus makes
     * it average, one negative combat bonus with no positives makes it bad. Every value read there
     * comes from the known object: an unlearned modifier must not sway the decision.
     *
     * <p>Everything else is graded against its kind's expected bonuses by {@code isGood}, then
     * overridden - an ego is {@link QualityValueEnum#IGNORE_ALL}, an artifact
     * {@link QualityValueEnum#IGNORE_MAX}. An object known well enough to have been assessed but
     * not fully known is treated as {@code IGNORE_ALL} unless it is an artifact.
     *
     * <p>Function ignoreLevelOf coded on 260822, commented in full on 260824.
     *
     * @return the {@link QualityValueEnum} band this object sits in
     */
    public QualityValueEnum ignoreLevelOf() {
        if (!isKnown()) return QualityValueEnum.IGNORE_MAX;

        // Jewellery treated specially
        if (tValue.isJewellery()) {
            // One positive modifier means not bad
            for (ObjectModifier mod : this.getKnown().getModifiers().keySet()) {
                if (this.getKnown().getModifierValue(mod) > 0)
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
            if (known.notice.has(ObjectNotice.OBJ_NOTICE_ASSESSED) && !isArtifact())
                value = QualityValueEnum.IGNORE_ALL;
            else
                value = QualityValueEnum.IGNORE_MAX;
        }

        return value;
    }

    /**
     * Scores how far this item's combat bonuses exceed what its kind rolls at worst - the port of
     * C's {@code is_object_good} ({@code obj-ignore.c:448}).
     *
     * <p>Weighted rather than counted: to-damage is worth four, to-hit two and to-armour one, so a
     * weapon is judged mostly on the bonus that matters for a weapon. A positive answer means good,
     * negative means bad, zero means average, and {@link #ignoreLevelOf()} turns that into a quality
     * band.
     *
     * <p>Function isGood commented in full on 260827.
     *
     * @return a positive, zero or negative score
     */
    private int isGood() {
        int good = 0;

        good += 4 * compareObjectTrait(toDam, kind.getToD());
        good += 2 * compareObjectTrait(toHit, kind.getToH());
        good += compareObjectTrait(toAC, kind.getToA());
        return good;
    }

    /**
     * Compares one combat bonus against the worst its kind could roll - the port of C's
     * {@code cmp_object_trait} ({@code obj-ignore.c:434}).
     *
     * <p>The kind's minimum is clamped to zero first, so an item is never judged good merely for
     * failing to be as negative as it might have been.
     *
     * <p>Function compareObjectTrait commented in full on 260827.
     *
     * @param bonus this item's bonus
     * @param base  the kind's dice for that bonus
     * @return {@code 1}, {@code 0} or {@code -1} as the bonus is above, equal to or below the floor
     */
    private int compareObjectTrait(int bonus, Random base) {
        int amount = base.randCalc(0, DamageAspect.MINIMIZE);

        if (amount > 0) amount = 0;
        return NumberUtils.cmp(bonus, amount);

    }

    /**
     * Answers whether this item is currently in the quiver - the port of C's
     * {@code object_is_in_quiver}.
     *
     * <p>Compares by identity, not equality: two identical stacks of arrows are still different
     * stacks, and the question is about this one.
     *
     * <p>The answer decides which stacking limits apply when two stacks are merged, since the quiver
     * caps a slot more tightly than the pack does.
     *
     * <p>Function isInQuiver commented in full on 260827.
     *
     * @param player the player whose quiver to search
     * @return {@code true} if this exact object sits in a quiver slot
     */
    public boolean isInQuiver(Player player) {
        for (ItemObject item : player.getPlayerUpkeep().getQuiver()) {
            if (item == this) return true;
        }

        return false;
    }

    /**
     * Tests whether {@code toMerge} could be folded into this stack in its entirety - the port of
     * C's {@code object_mergeable} ({@code obj-pile.c:512}).
     *
     * <p>The whole-stack question, as against {@link #objectStackable}, which only asks whether the
     * two could share a slot at all. The difference is capacity: the combined count has to fit
     * within this kind's {@code max_stack}, and within {@code carry-cap:quiver-slot-size} as well
     * when the stack is in the quiver - divided by {@code carry-cap:thrown-quiver-mult} for a
     * thrown weapon, which takes several slots' worth of room per item.
     *
     * <p>The quiver test is nested inside the store test rather than beside it, because a store
     * stack has no limits at all and the quiver limit must be waived along with the rest.
     *
     * <p>The maximum is read from this object's kind. The two kinds will be identical by the time
     * the answer matters, but only {@link #similar} establishes that, and it runs afterwards.
     *
     * <p>Function mergeable coded on 260822, commented in full on 260824.
     *
     * @param toMerge    the stack that would be absorbed whole
     * @param stackModes the {@link ObjectStackEnum} flags in force
     * @return {@code true} if the two stacks may be merged into one
     */
    public boolean mergeable(ItemObject toMerge, Flag<ObjectStackEnum> stackModes) {
        int total = this.number + toMerge.number;

        if (!stackModes.has(ObjectStackEnum.OSTACK_STORE)) {
            if (total > this.getKind().getBase().getMaxStack()) return false;


            // Quiver can impose stricter limits
            if (stackModes.has(ObjectStackEnum.OSTACK_QUIVER)) {
                if (toMerge.gettValue().isAmmo()) {
                    if (total > GameConstants.getCarryCapQuiverSlotSize()) return false;
                } else {
                    if (total > GameConstants.getCarryCapQuiverSlotSize()
                            / GameConstants.getCarryCapThrownQuiverMult()) return false;
                }
            }
        }

        return objectStackable(toMerge, stackModes);
    }

    /**
     * Tests whether two objects may share a stack, capacity aside - the port of C's
     * {@code object_stackable} ({@code obj-pile.c:499}).
     *
     * <p>{@link #similar} settles everything about the objects themselves; this adds the
     * inscription rule. Two objects are compatible when either is uninscribed, or when both carry
     * the same inscription - an uninscribed item takes on whatever the stack it joins is called,
     * but two differently inscribed stacks stay apart so the player's tags survive.
     *
     * <p>Function objectStackable coded on 260822, commented in full on 260824.
     *
     * @param toMerge    the other object
     * @param stackModes the {@link ObjectStackEnum} flags in force
     * @return {@code true} if the two may occupy one stack
     */
    public boolean objectStackable(ItemObject toMerge, Flag<ObjectStackEnum> stackModes) {
        if (similar(toMerge, stackModes)) {
            return toMerge.getNote() == null || this.getNote() == null || toMerge.getNote().equals(this.getNote());
        }

        return false;
    }

    /**
     * Folds another stack into this one entirely, destroying it - the port of C's
     * {@code object_absorb} ({@code obj-pile.c:676}).
     *
     * <p>The counts are added, capped at the kind's {@code max_stack}, and everything else that has
     * to travel between the two is handled by {@link #objectAbsorbMerge}. The absorbed object is
     * then disposed of, along with its known half: excised from whatever pile holds it, delisted
     * from the cave's object list, and deleted.
     *
     * <p>The excise is skipped for a known object at the origin, because a zero grid means it is
     * not on the floor to be excised from - C's {@code loc_is_zero}, which compares coordinates.
     * The port must compare coordinates too: {@code Loc.zero} is one particular instance, and an
     * independently constructed {@code Loc(0, 0)} is a different object with the same value.
     *
     * <p>Deleting the absorbed object is what removes it from the player's gear, so this must be
     * reached; a caller that leaves it out ends up with the emptied stack still in the pack at its
     * old count.
     *
     * <p>Function objectAbsorb coded on 260822, commented in full on 260824.
     *
     * @param toAbsorb the stack to fold in; it does not survive the call
     */
    public void objectAbsorb(ItemObject toAbsorb) {
        ItemObject known = toAbsorb.getKnown();

        int total = this.number + toAbsorb.number;

        this.number = Math.min(total, this.getKind().getBase().getMaxStack());

        this.objectAbsorbMerge(toAbsorb, player, true);
        if (known != null) {
            Chunk cave = player.getCave();
            if (cave != null && !known.getGrid().isZero())
                cave.getSquare(known.getGrid()).pileExcise(known);
            if (cave != null) {
                cave.delistObject(known);
                cave.objectDelete(null, known);
            }
        }
        GameState.getCave().objectDelete(player.getCave(), toAbsorb);
    }

    /**
     * Carries everything except the counts across from one stack to another - the port of C's
     * {@code object_absorb_merge} ({@code obj-pile.c:579}). Shared by the whole and partial absorbs.
     *
     * <p>Knowledge first: the surviving object's known half is brought up to date with its own real
     * effect and the player is told about the object again, which is how learning one stack teaches
     * the other. The direction matters - what is written into the known object is the surviving
     * object's reality, never the absorbed object's knowledge.
     *
     * <p>An inscription on the absorbed stack carries over. Charges and timeouts are pooled only
     * when the caller asks: rod timeouts add, and wand and staff charges add up to
     * {@code MAX_PVAL}. A partial absorb passes {@code false} for anything but money, because the
     * charges have already been shared out by {@code distributeCharges}. Origins are combined last.
     *
     * <p>Function objectAbsorbMerge coded on 260822, commented in full on 260824.
     *
     * @param toAbsorb               the stack being folded in
     * @param player                 the player whose knowledge is updated
     * @param combineChargesTimeouts whether to pool charges and timeouts as well
     */
    private void objectAbsorbMerge(ItemObject toAbsorb, Player player, boolean combineChargesTimeouts) {
        int total;

        // This object gains extra knowledge from toMerge
        if (this.getKnown() != null && toAbsorb.getKnown() != null) {
            if (toAbsorb.getKnown().getEffect() != null && !toAbsorb.getKnown().getEffect().isEmpty())
                this.getKnown().setEffect(this.getEffect());
            player.knowObject(this);
        }

        if (toAbsorb.getNote() != null && !toAbsorb.getNote().isEmpty())
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

    /**
     * Forgets this item's known half without disturbing the known object itself - the port of C's
     * {@code obj->known = NULL}.
     *
     * <p>Used where an object is about to be absorbed or deleted and its knowledge has already been
     * dealt with separately; clearing the link first stops the disposal from following it a second
     * time.
     *
     * <p>Function nullKnown commented in full on 260827.
     */
    public void nullKnown() {
        this.known = null;
    }

    /**
     * Moves as much of one stack onto another as the limits allow, leaving both alive - the port of
     * C's {@code object_absorb_partial} ({@code obj-pile.c:624}).
     *
     * <p>Both new sizes are worked out before either is written, and they always conserve the total
     * count. Which limit applies depends on where the two stacks are:
     *
     * <ul>
     *   <li>both in the quiver - this stack is filled to the per-slot limit and the remainder stays
     *       with {@code item2};</li>
     *   <li>this one in the quiver, {@code item2} not - this stack takes exactly the per-slot
     *       limit, {@code item2} keeps whatever is over;</li>
     *   <li>{@code item2} in the quiver, this one not - the same the other way round;</li>
     *   <li>neither in the quiver - this stack is filled to the kind's {@code max_stack}.</li>
     * </ul>
     *
     * <p>The per-slot limit is {@code carry-cap:quiver-slot-size}, divided by
     * {@code carry-cap:thrown-quiver-mult} for a thrown weapon, and it is taken from whichever of
     * the two stacks the quiver mode applies to.
     *
     * <p>Where C asserts, the port throws. Neither mode may be {@code OSTACK_STORE}, which the
     * caller is required to guarantee, and in the two mixed-quiver cases the size that ends up in
     * the pack must fit the kind's {@code max_stack}. These are impossible states rather than
     * conditions to recover from: returning quietly would leave the caller believing a split had
     * happened when the counts were never touched.
     *
     * <p>Charges are distributed before the counts change, since
     * {@code distributeCharges} works from the number moving.
     *
     * <p>Function objectAbsorbPartial coded on 260822, corrected on 260824, commented in full on
     * 260824.
     *
     * @param item2      the stack being drawn from, which survives with a reduced count
     * @param stackMode1 the stacking rules in force for this stack
     * @param stackMode2 the stacking rules in force for {@code item2}
     */
    public void objectAbsorbPartial(ItemObject item2,
                                    Flag<ObjectStackEnum> stackMode1,
                                    Flag<ObjectStackEnum> stackMode2) {
        int smallest = Math.min(this.getNumber(), item2.getNumber());
        int largest = Math.max(this.getNumber(), item2.getNumber());
        int newThisSize;
        int newItm2Size;

        if (stackMode1.has(ObjectStackEnum.OSTACK_STORE) || stackMode2.has(ObjectStackEnum.OSTACK_STORE)) {
            String message = "One or other of the stack modes implies this absorb is happening in a store.";
            logger.error(message);
            throw new RuntimeException(message);
        }

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
                if (newItm2Size >= this.getKind().getBase().getMaxStack()) {
                    String message = "New size is greater than max stack item on item: " + this.getKind().getName();
                    logger.error(message);
                    throw new RuntimeException(message);
                }
            }
        } else if (stackMode2.has(ObjectStackEnum.OSTACK_QUIVER)) {
            // Handle possible different limits
            int limit = GameConstants.getCarryCapQuiverSlotSize()
                    / (item2.gettValue().isAmmo() ? 1 : GameConstants.getCarryCapThrownQuiverMult());

            newThisSize = largest + smallest - limit;
            newItm2Size = limit;
            if (newThisSize >= this.getKind().getBase().getMaxStack()) {
                String message = "New size is greater than max stack item on item: " + this.getKind().getName();
                logger.error(message);
                throw new RuntimeException(message);
            }
        } else {
            int difference = this.getKind().getBase().getMaxStack() - largest;

            newThisSize = largest + difference;
            newItm2Size = smallest - difference;
        }

        item2.distributeCharges(this, item2.getNumber() - newItm2Size, false);
        this.setNumber(newThisSize);
        item2.setNumber(newItm2Size);

        objectAbsorbMerge(item2, player, this.gettValue().isMoney());
    }

    /**
     * Splits a number of items off this stack into a new one - the port of C's {@code object_split}
     * ({@code obj-pile.c:790}).
     *
     * <p>The new stack is a copy of this one, so it carries the same kind, bonuses, flags and
     * inscription; what it does not carry is a share of the counts and charges, which are handed
     * over afterwards. Charges are distributed with {@code destNew} set, because the destination is
     * brand new and should take its share rather than add to one.
     *
     * <p>The known halves are split alongside, and their counts written to match, so that knowledge
     * and truth do not drift apart over the split.
     *
     * <p>Refuses to split off the whole stack or more: C asserts on it, and a caller wanting all of
     * it should move the stack rather than split it.
     *
     * <p>Function objectSplit commented in full on 260827.
     *
     * @param amount how many items to move to the new stack
     * @return the new stack, holding {@code amount} items
     * @throws IllegalArgumentException if {@code amount} is not fewer than this stack holds
     */
    public ItemObject objectSplit(int amount) {
        ItemObject destination;

        // Get a copy of the object, pass in true to ensure the known is copied once
        destination = this.copy(true);

        // Check legality
        if (this.getNumber() <= amount) {
            String message = "Invalid amount passed to objectSplit. Was: " + amount + " should " +
                    "have been more than " + this.getNumber();
            logger.error(message);
            throw new IllegalArgumentException(message);
        }

        // Distribute charges of wands/staves/rods
        this.distributeCharges(destination, amount, true);
        if (this.getKnown() != null) {
            this.getKnown().distributeCharges(destination.getKnown(), amount, true);
        }

        // Modify quantity
        destination.setNumber(amount);
        this.setNumber(this.getNumber() - amount);
        if (this.getNote() != null)
            destination.setNote(this.getNote());
        if (this.getKnown() != null) {
            destination.getKnown().setNumber(destination.getNumber());
            this.getKnown().setNumber(this.getNumber());
            destination.getKnown().setNote(this.getKnown().getNote());
        }

        return destination;
    }

    /**
     * Returns an independent copy of this item - the port of C's {@code object_copy}
     * ({@code obj-pile.c}).
     *
     * <p>Deep-copied because their contents are mutable: the flag and notice sets, the modifier map,
     * the element info (each entry copied in turn), the curse map (each {@code CurseData} rebuilt),
     * the dice, and the brand and slay sets where they exist.
     *
     * <p>Shared deliberately: the kind, ego and artifact templates, which C shares as pointers and
     * which every item built on them points at; the origin race, for the same reason - identity is
     * what tells two origins apart, so copying it would make two items from the same monster look
     * like items from different ones.
     *
     * <p>Null is preserved rather than normalised for the brand, slay and curse collections, because
     * elsewhere the class distinguishes "no collection" from "an empty one" - the accessors answer
     * an immutable empty collection for the former, which takes no writes.
     *
     * <p>The known half is copied only when asked for. A caller splitting a stack wants both halves
     * copied; a caller building a scratch item to price wants the knowledge left alone.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @param includingKnown {@code true} to copy the known half as well
     * @return a new item that shares no mutable state with this one, bar the noted templates
     */
    private ItemObject copy(boolean includingKnown) {
        ItemObject copy = new ItemObject();

        copy.setKind(this.getKind());
        copy.setEgo(this.getEgo());
        copy.artifact = this.artifact;
        // Don't get into infinite recursion
        if (includingKnown) {
            if (this.getKnown() == null)
                copy.known = null;
            else
                copy.known = this.known.copy(false);
        }
        if (this.location == null)
            copy.location = null;
        else
            copy.location = this.location.copy();
        copy.tValue = this.tValue;
        copy.sValue = this.sValue;
        copy.pValue = this.pValue;
        copy.weight = this.weight;
        copy.damageDice = this.damageDice;
        copy.damageSides = this.damageSides;
        copy.baseDamage = this.baseDamage.copy();
        copy.baseAC = this.baseAC;
        copy.toAC = this.toAC;
        copy.toDam = this.toDam;
        copy.toHit = this.toHit;
        Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
        oFlags.copyFrom(this.flags);
        copy.flags = oFlags;
        Map<ObjectModifier, Integer> newMods = new HashMap<>();
        for (ObjectModifier mod : this.getModifiers().keySet()) {
            newMods.put(mod, this.getModifiers().get(mod));
        }
        copy.modifiers = newMods;
        Map<ElementEnum, ElementInfo> eeMap = new HashMap<>();
        for (ElementEnum ee : this.getElInfo().keySet()) {
            eeMap.put(ee, this.getElInfo().get(ee).copy());
        }
        copy.elInfo = eeMap;
        if (this.brands == null)
            copy.brands = null;
        else
            copy.brands = new HashSet<>(this.brands);
        if (this.slays == null)
            copy.slays = null;
        else
            copy.slays = new HashSet<>(this.slays);
        if (this.curses == null)
            copy.curses = null;
        else {
            LinkedHashMap<Curse, CurseData> newCurses = new LinkedHashMap<>();
            for (Curse c : this.curses.keySet()) {
                newCurses.put(c, new CurseData(this.curses.get(c)));
            }
            copy.curses = newCurses;
        }
        copy.effect = this.effect;
        copy.effectMessage = this.effectMessage;
        copy.activation = this.activation;
        copy.time = this.time.copy();
        copy.timeout = this.timeout;
        copy.number = this.number;
        Flag<ObjectNotice> nFlags = new Flag<>(ObjectNotice.class);
        nFlags.copyFrom(this.notice);
        copy.notice = nFlags;
        copy.heldMIndex = this.heldMIndex;
        copy.mimickingMIndex = this.mimickingMIndex;
        copy.origin = origin;
        copy.originRace = originRace;
        copy.originDepth = this.originDepth;
        copy.note = this.note;

        return copy;
    }

    /**
     * Prices a stack as the player would see it - the port of C's {@code object_value}
     * ({@code obj-power.c}).
     *
     * <p>Which of the two pricing routes is taken depends on what the player is entitled to know. An
     * object whose worth varies with its bonuses is priced from its <em>known</em> half, so an
     * unidentified sword is not priced as the fine one it may turn out to be. A flavoured object the
     * player has learned is priced in full. Anything else gets the flat base price for its type,
     * multiplied by the count.
     *
     * <p>Function objectValue commented in full on 260827.
     *
     * @param quantity how many items are being priced
     * @return the price of the stack in gold
     */
    private int objectValue(int quantity) {
        int value;

        // Variable power items are assess by what is known about them
        if (this.gettValue().hasVariablePower() && this.getKnown() != null) {
            value = this.getKnown().objectValueReal(quantity);
        } else if (this.gettValue().canHaveFlavour() && this.flavourIsAware()) {
            value = objectValueReal(quantity);
        } else {
            // Unknown constant-price items just get a base value
            value = objectValueBase() * quantity;
        }

        return value;
    }

    /**
     * Guesses the worth of an object the player has not identified - the port of C's
     * {@code object_value_base} ({@code obj-power.c:1058}).
     *
     * <p>An object whose flavour is known is worth its kind's listed cost. One that is not is worth
     * a flat figure for its type, rising from food through potions and scrolls to rods: the player
     * knows roughly what an unidentified rod is worth without knowing which rod it is. Types not
     * listed are worth nothing unidentified.
     *
     * <p>Function objectValueBase commented in full on 260827.
     *
     * @return the price of one such object in gold
     */
    private int objectValueBase() {
        if (objectFlavourIsAware())
            return getKind().getCost();

        return switch (gettValue()) {
            case TV_FOOD, TV_MUSHROOM -> 5;
            case TV_POTION, TV_SCROLL -> 20;
            case TV_RING, TV_AMULET -> 45;
            case TV_WAND -> 50;
            case TV_STAFF -> 70;
            case TV_ROD -> 90;
            default -> 0;
        };
    }

    /**
     * Prices a stack from what it can actually do - the port of C's {@code object_value_real}
     * ({@code obj-power.c:1101}).
     *
     * <p>Two routes, chosen by whether the type's worth varies with its properties.
     *
     * <p><b>Variable-power objects</b> are priced from {@link #objectPower}, through the quadratic
     * {@code power * (power * a + b)}. The quadratic is what makes a strong object worth
     * disproportionately more than a middling one, rather than merely proportionately more. A
     * negative power - a cursed object - is priced by the mirror of the same curve, and comes out
     * negative.
     *
     * <p>The overflow checks around each multiply are C's, kept rather than replaced by wider
     * arithmetic so that the saturating behaviour matches: a price too large to represent becomes
     * the largest representable one, not a wrapped negative. The coefficients are locals here
     * because C has them as locals too, with the same comment that both must stay non-negative.
     *
     * <p>Expendables are then divided down: a burning light or a missile is not worth what its power
     * suggests, because it is consumed. A price that rounds to nothing is lifted to one, so that a
     * cheap-but-real object is not worthless - C raises zero only, not negative values.
     *
     * <p><b>Fixed-price objects</b> take the kind's listed cost, with a surcharge for the charges a
     * wand or staff carries, rounded up. The total is floored at zero.
     *
     * <p>Function objectValueReal commented in full on 260827.
     *
     * @param quantity how many items are being priced
     * @return the price of the stack in gold, never negative
     */
    private int objectValueReal(int quantity) {
        int a = 1; // Quadratic coefficient for power - must be non-negative
        int b = 5; // Linear coefficient for power - must be non-negative
        int value;
        int totalValue;

        if (this.gettValue().hasVariablePower()) {
            int power = this.objectPower(false, null);

            if (power > 0) {
                if (a > 0) {
                    if (power <= (Integer.MAX_VALUE / power - b) / a) {
                        value = power * (power * a + b);
                    } else {
                        value = Integer.MAX_VALUE;
                    }
                } else if (b > 0) {
                    if (power <= (Integer.MAX_VALUE / b)) {
                        value = power * b;
                    } else {
                        value = Integer.MAX_VALUE;
                    }
                } else {
                    value = 0;
                }
            } else if (power < 0) {
                if (a > 0) {
                    if (power > Integer.MIN_VALUE && power >= (Integer.MIN_VALUE / (-power) + b) / a) {
                        value = -power * (power * a - b);
                    } else {
                        value = Integer.MIN_VALUE;
                    }
                } else if (b > 0) {
                    if (power >= Integer.MIN_VALUE / b) {
                        value = power * b;
                    } else {
                        value = Integer.MIN_VALUE;
                    }
                } else {
                    value = 0;
                }
            } else {
                value = 0;
            }

            // Rescale for expendables
            if ((gettValue().isLight() && this.getFlags().has(ObjectFlag.OF_BURNS_OUT)
                    && ego == null) || gettValue().isAmmo()) {
                value = value / ObjectRegistry.AMMO_RESCALER;
            }

            // Round up to make sure things like cloaks are not worthless
            if (value == 0) value = 1;

            // get the total value
            totalValue = Math.max(0, value * quantity);
        } else {
            ObjectKind kind = getKind();

            if (kind == null || kind.getCost() == 0) return 0;

            // base costs
            value = kind.getCost();

            // Analyze the type and quantity
            if (gettValue().canHaveCharges()) {
                int charges;

                totalValue = value * quantity;

                // Calculate the number of charges rounded up
                charges = getpValue() * quantity / getNumber();

                if ((getpValue() * quantity) % getNumber() != 0)
                    charges++;

                // Pay extra for charges 
                totalValue += value * charges / 20;
            } else {
                totalValue = value * quantity;
            }

            // No non-negative values
            totalValue = Math.max(0, totalValue);
        }

        return totalValue;
    }

    /**
     * Prices this object's usefulness as a single number - the port of C's {@code object_power}
     * ({@code obj-power.c:1005}).
     *
     * <p>Power is not the same as gold: it is what {@link #objectValueReal} feeds its curve, what
     * the artifact generator judges its creations by, and what the {@code INHIBIT_} thresholds
     * refuse. Roughly, it is what the object does for whoever carries it.
     *
     * <p>The order of the steps is C's and matters: the damage terms come first and are multiplied
     * by blows, shots and might, so a change to any of those scales everything before it. Only then
     * do the armour, jewellery and property terms add on, and the curse and weight terms adjust the
     * total.
     *
     * <p>Three early returns on {@code INHIBIT_POWER} stop the calculation as soon as the object is
     * beyond what should exist, matching C - there is no point pricing the rest of it.
     *
     * <p>The multiplier returned by the extra-might step is assigned and then unused, as in C, where
     * it is a by-value argument that goes no further.
     *
     * <p>Function objectPower commented in full on 260827.
     *
     * @param verbose     {@code true} to log the brand and slay breakdown as well as the running
     *                    totals
     * @param logFileName the log file to write the breakdown to, or {@code null}
     * @return this object's power
     */
    private int objectPower(boolean verbose, String logFileName) {
        // Get all the attack power
        int power = toDamagePower();
        int dicePower = damageDicePower();
        power += dicePower;
        if (dicePower != 0) logger.info("total is {}", power);
        power += ammoDamagePower(power);
        int mult = bowMulitplier();
        power = launcherAmmoDamagePower(power);
        power = extraBlowsPower(power);
        if (power > ObjectRegistry.INHIBIT_POWER) return power;
        power = extraShotsPower(power);
        if (power > ObjectRegistry.INHIBIT_POWER) return power;
        PowerAndMult pm = new PowerAndMult(power, mult);
        PowerAndMult outgoing = extraMightPower(pm);
        power = outgoing.power();
        mult = outgoing.mult();
        if (power > ObjectRegistry.INHIBIT_POWER) return power;
        power = slayPower(power, verbose, dicePower);
        power = rescaleBowPower(power);
        power = toHitPower(power);

        // Armour class power
        power = acPower(power);
        power = toAcPower(power);

        // Bonus for jewellery
        power = jewelleryPower(power);

        // Other object properties
        power = modifierPower(power);
        power = flagsPower(power);
        power = elementPower(power);
        power = effectsPower(power);
        power = cursePower(power, verbose, logFileName);
        power = nonStandardWeightPower(power);

        logger.info("FINAL POWER IS {}", power);

        return power;
    }

    /**
     * Prices a curse's usefulness, mirroring {@link #objectPower(boolean, String)} step for step.
     *
     * <p><b>Why there are two of these.</b> In C a curse <em>is</em> an object - {@code curses[i].obj}
     * is a real {@code struct object} with a tval, flags, modifiers and element info - so
     * {@code curse_power} simply runs the ordinary calculation over it ({@code obj-power.c:774}).
     * The port's {@link Curse} is a flattened record instead, so almost every power function has a
     * second overload taking one, and this method calls them in the same order the object version
     * calls theirs.
     *
     * <p>Many of those overloads return their input unchanged. That is not laziness: running C's
     * calculation over a curse object reaches the same answer, because a curse object has no base
     * armour, is not jewellery, is not wielded in the shooting slot, is not ammunition or a bow,
     * carries no brands or slays in {@code curse.txt}, and has no curses of its own. Each such
     * overload says which of those facts makes it an identity.
     *
     * <p>When the two paths drift, there is no single function to correct - a change on one side
     * needs the same change considered on the other.
     *
     * <p>Function objectPower commented in full on 260827.
     *
     * @param curse       the curse to price
     * @param verbose     {@code true} to log the breakdown
     * @param logFileName the log file to write to, or {@code null}
     * @return the curse's power
     */
    private int objectPower(Curse curse, boolean verbose, String logFileName) {
        // Get all the attack power
        int power = toDamagePower(curse);
        int dicePower = damageDicePower(curse);
        power += dicePower;
        if (dicePower != 0) logger.info("total is {}", power);
        power += ammoDamagePower(curse, power);
        int mult = bowMulitplier(curse);
        power = launcherAmmoDamagePower(curse, power);
        power = extraBlowsPower(curse, power);
        if (power > ObjectRegistry.INHIBIT_POWER) return power;
        power = extraShotsPower(curse, power);
        if (power > ObjectRegistry.INHIBIT_POWER) return power;
        PowerAndMult pm = new PowerAndMult(power, mult);
        PowerAndMult outgoing = extraMightPower(curse, pm);
        power = outgoing.power();
        mult = outgoing.mult();
        if (power > ObjectRegistry.INHIBIT_POWER) return power;
        power = slayPower(curse, power, verbose, dicePower);
        power = rescaleBowPower(curse, power);
        power = toHitPower(curse, power);

        // Armour class power
        power = acPower(curse, power);
        power = toAcPower(curse, power);

        // Bonus for jewellery
        power = jewelleryPower(curse, power);

        // Other object properties
        power = modifierPower(curse, power);
        power = flagsPower(curse, power);
        power = elementPower(curse, power);
        power = effectsPower(curse, power);
        power = cursePower(curse, power, verbose, logFileName);
        power = nonStandardWeightPower(curse, power);

        logger.info("FINAL POWER IS {}", power);

        return power;
    }

    /**
     * Adjusts power for an object that is heavier or lighter than its kind - the port of C's
     * {@code nonstandard_weight_power} ({@code obj-power.c:930}).
     *
     * <p>Only curses can produce the difference: the object's own weight is the kind's, and
     * {@link #weightOne()} is what the curses have made of it.
     *
     * <p>Two separate adjustments, and an object can take both. An object with no base armour class
     * is judged on carrying capacity - lighter is better, because it leaves room for something else.
     * An object with the {@code THROWING} flag is judged the other way, because a heavier missile
     * hits harder. Objects that do provide base armour are skipped for the first: {@link #acPower}
     * has already accounted for their weight, and charging twice would be wrong.
     *
     * <p>Flags are merged from the object and its active curses first, because a curse can be what
     * makes the object throwable in the first place.
     *
     * <p>C's comment lists what is deliberately not modelled: blows, heavy-wield status, criticals
     * and shield bashes all move with weight and none of them are priced here.
     *
     * <p>Function nonStandardWeightPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with any weight adjustment applied
     */
    private int nonStandardWeightPower(int power) {
        int standardWeight = Math.max(getWeight(), 0);
        int nonStandardWeight = weightOne();
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        int adjustment = 0;

        if (nonStandardWeight < 0) {
            String message = "Negative weight.";
            logger.error(message);
            throw new RuntimeException(message);
        }

        if (standardWeight == nonStandardWeight) {
            // no change to weight, so no change to power
            return power;
        }

        // Merge flags from base object and any curses
        flags.copyFrom(getFlags());
        if (getCurses() != null && !getCurses().isEmpty()) {
            for (Curse c : getCurses().keySet()) {
                if (getCurses().get(c).getPower() != 0) {
                    flags.union(c.getObjectFlags());
                }
            }
        }

        /*
         * ac_power() accounted for the weight when the object provides a base
         * amount of armour so do not adjust the power for those objects here.
         * For objects which do not provide a base amount of armour, adjust
         * the power under the assumption that lighter than normal is beneficial
         * (more room under the weight cap for other stuff) and heavier than
         * normal is harmful.
         */
        if (getBaseAC() == 0) {
            int adjustWC = (standardWeight - nonStandardWeight) / ObjectRegistry.WGT_POWER_DEN_NOBASEAC;

            logger.info("Add {} power for non-standard weight of object not  " +
                    "affecting base armour.", adjustWC);
            adjustment = Guards.addGuardI(adjustment, adjustWC);
        }

        // Objects with the "THROWING" flag increase damage with increasing weight
        if (flags.has(ObjectFlag.OF_THROWING)) {
            int adjustThrow = nonStandardWeight / ObjectRegistry.WGT_POWER_DEN_THROW
                    - standardWeight / ObjectRegistry.WGT_POWER_DEN_THROW;

            logger.info("Add {} power for non-standard weight of object good " +
                    "for throwing", adjustThrow);
            adjustment = Guards.addGuardI(adjustment, adjustThrow);
        }

        /*
         * Weight also affects number of blows (melee weapons only),
         * heavy wield status (melee weapon or launcher; strength-dependent
         * and normally only relevant for quite heavy objects), criticals
         * (for melee, launched missile, or thrown missile but only in non-O
         * combat calculations; increasing weight can increase the chance of
         * a critical and the amount of damage from the critical if it occurs),
         * and shield bashes (more weight is better; only relevant for some
         * classes).  None of those are accounted for here.
         */
        if (adjustment != 0) {
            power = Guards.addGuardI(power, adjustment);
            logger.info("Add {} power combined for non-standard weight; total is {}", adjustment, power);
        }

        return power;
    }

    /**
     * Returns its input: a curse never carries a weight adjustment of its own.
     *
     * <p>C reaches {@code nonstandard_weight_power(curses[i].obj, p)}, which compares the curse
     * object's weight against {@code object_weight_one(curse_obj)}. A curse object has no curses of
     * its own, so {@code object_weight_one} returns the weight unchanged ({@code obj-util.c:276}),
     * the two figures are equal, and the function's first test returns {@code p} untouched. Always -
     * including for a {@code MULTIPLY_WEIGHT} curse of weight 100, which means "no change".
     *
     * <p>Function nonStandardWeightPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return {@code power}, unchanged
     */
    private int nonStandardWeightPower(Curse curse, int power) {
        return power;
    }

    /**
     * Adjusts power for the curses on this object - the port of C's {@code curse_power}
     * ({@code obj-power.c:736}).
     *
     * <p>Two passes, because curses come in two kinds and the second kind cannot be priced
     * individually.
     *
     * <p>An ordinary curse is priced on its own, by running the whole power calculation over it, and
     * then discounted by a tenth of its strength - a curse that resists removal is worth less to the
     * carrier than one that can be shrugged off.
     *
     * <p>A weight-affecting curse cannot be priced that way, because weight interacts with
     * everything else the object does. Those are priced by difference instead: the object is copied,
     * all its curses applied, and priced; then copied again with one curse held back, and priced
     * again. The gap between the two is that curse's contribution. Where the gap is negative - the
     * curse makes the object worse - it is scaled by how hard the curse is to remove, because a
     * penalty you cannot escape counts for more.
     *
     * <p>Splitting them keeps the answers identical to the previous version of the algorithm for the
     * common case, which is C's stated reason for not treating all curses the way the second pass
     * treats these.
     *
     * <p>Function cursePower commented in full on 260827.
     *
     * @param power       the running power total
     * @param verbose     {@code true} to log the breakdown
     * @param logFileName the log file to write to, or {@code null}
     * @return the total with the curse adjustment applied
     */
    private int cursePower(int power, boolean verbose, String logFileName) {
        int q = 0;

        if (!getCurses().isEmpty()) {
            /*
             * Treat weight-affecting curses differently since those may
             * not be modeled well with power(base object)
             * + power(curse 1) + ....  Could treat all curses the way
             * weight-affecting curses are, but separating them out keeps
             * the results the same as the 4.2.5 calculations when the
             * object does not have weight-affecting curses.
             */
            boolean weightAffecting = false;

            for (Curse c : getCurses().keySet()) {
                int cursePower;

                if (getCurses().get(c).getPower() == 0) continue;

                if (c.getObjectFlags().has(ObjectFlag.OF_MULTIPLY_WEIGHT)) {
                    if (c.getWeight() != 100) {
                        weightAffecting = true;
                        continue;
                    }
                } else {
                    if (c.getWeight() != 0) {
                        weightAffecting = true;
                        continue;
                    }
                }

                logger.info("Calculating {} curse power...", c.getName());
                cursePower = objectPower(c, verbose, logFileName);
                cursePower -= getCurses().get(c).getPower() / 10;
                logger.info("Adjust for strength of curse, {} for {} curse power", cursePower, c.getName());
                q += cursePower;
            }

            if (weightAffecting) {
                // Get the power for the object with all the curses attributes combined
                // with those for the base object.
                ItemObject local = this.copy(true);
                int powerAllCurses;
                local.applyCurseAttributes(null);

                // Clear all the curses on local that have been included by applyCurseAttributes
                local.freeCurses();
                powerAllCurses = local.objectPower(verbose, logFileName);
                local.freeBrands();
                local.freeSlays();
                logger.info("Power is {} with all curses applied", powerAllCurses);

                /*
                 * Now get the power for the object which has one of
                 * the active curses removed.  The difference between
                 * that power and p_all_curse is the power of the
                 * curse.  Skip the non-weight-affecting curses handled
                 * in the first pass.
                 */
                for (Curse c : getCurses().keySet()) {
                    int powerAllButC;
                    int powerCurse;

                    if (getCurses().get(c).getPower() == 0) continue;

                    if (c.getObjectFlags().has(ObjectFlag.OF_MULTIPLY_WEIGHT)) {
                        if (c.getWeight() == 100) continue;
                    } else {
                        if (c.getWeight() == 0) continue;
                    }

                    ItemObject localItem = this.copy(true);
                    localItem.applyCurseAttributes(c);

                    // Clear curses since all of interested included by applyCurseAttributes above
                    localItem.freeCurses();
                    powerAllButC = localItem.objectPower(verbose, logFileName);
                    localItem.freeBrands();
                    localItem.freeSlays();
                    logger.info("Power is {} with all but {} curse applied", powerAllButC, c.getName());

                    /*
                     * The effect of this curse on the total power
                     * is the difference between p_all_curse and
                     * p_all_but_i.  If that difference is
                     * is not negative, use it as is:  at least
                     * according to the power calculation, it does
                     * not make sense to remove that curse so the
                     * curse's resistance to removal does not
                     * matter.
                     */
                    powerCurse = Guards.subGuardI(powerAllCurses, powerAllButC);
                    if (powerCurse < 0) {
                        /*
                         * The curse reduces the object's
                         * power: scale the contribution to
                         * power attributed to the curse by
                         * a factor that increases with the
                         * curse's resistance to removal.
                         */
                        int resistance = Math.clamp(getCurses().get(c).getPower(), 20, 100);

                        powerCurse = (powerCurse >= Integer.MIN_VALUE / resistance)
                                ? powerCurse * resistance
                                : Integer.MIN_VALUE;

                        powerCurse /= 100;
                    }
                    logger.info("Adjusted power is {} for {} curse", powerCurse, c.getName());

                    q = Guards.addGuardI(q, powerCurse);
                }
            }
        }

        if (q != 0) {
            power += q;
            logger.info("Total of {} power added for curses, total is {}", q, power);
        }

        return power;
    }

    /**
     * Empties this object's slays - the port of C's {@code mem_free(obj_local.slays)}.
     *
     * <p>Called on the scratch copies the curse pricing builds, once they have been priced, so that
     * the copy releases what it borrowed. Assigns a fresh empty set rather than null, which keeps
     * the accessors' distinction between "no collection" and "an empty one" pointing the right way.
     *
     * <p>Function freeSlays commented in full on 260827.
     */
    private void freeSlays() {
        this.slays = new HashSet<>();
    }

    /**
     * Empties this object's brands - the counterpart of {@link #freeSlays()}, and used in the same
     * place for the same reason.
     *
     * <p>Function freeBrands commented in full on 260827.
     */
    private void freeBrands() {
        this.brands = new HashSet<>();
    }

    /**
     * Empties this object's curses - the port of C clearing {@code obj_local.curses} after
     * {@code apply_curse_attributes} has folded them in ({@code obj-power.c:795}).
     *
     * <p>Necessary rather than tidy: the scratch copy has just had every curse's attributes merged
     * into its own, so leaving the curses on it as well would price them twice.
     *
     * <p>Function freeCurses commented in full on 260827.
     */
    private void freeCurses() {
        this.curses = new LinkedHashMap<>();
    }

    /**
     * Folds every active curse's attributes into this object - the port of C's
     * {@code apply_curse_attributes} ({@code obj-curse.c:450}).
     *
     * <p>Called on a scratch copy by the curse pricing, which then prices the merged object as a
     * whole. One curse may be held back, which is how the pricing takes the difference a single
     * curse makes; passing {@code null} merges them all.
     *
     * <p>Weight, the three combat bonuses, the flags and the modifiers all combine additively, the
     * combat bonuses through the saturating adds so that a long chain cannot wrap round.
     *
     * <p><b>Resistances combine by rule, not by addition.</b> An immunity beats everything; a
     * resistance meeting a vulnerability - in either order - becomes both at once, held as
     * {@link #VULN_AND_RES} while the merge runs; and an element the object says nothing about takes
     * whatever the curse says. The pass at the foot flattens any surviving both-at-once to plain
     * zero, so the caller never sees the sentinel.
     *
     * <p>A curse that mentions an element the object does not is handled by creating the entry: C's
     * element array has a slot for every element and the port's map does not, so absence has to be
     * turned into a real entry rather than skipped. A curse silent about an element reads as
     * resistance level zero, which is C's default and means no change.
     *
     * <p>Function applyCurseAttributes commented in full on 260827.
     *
     * @param curseToIgnore the one curse to leave out, or {@code null} to merge them all
     */
    private void applyCurseAttributes(Curse curseToIgnore) {
        if (getCurses() == null || getCurses().isEmpty()) {
            // no curses - nothing to merge
            return;
        }

        for (Curse curse : getCurses().keySet()) {
            if (curse == curseToIgnore || getCurses().get(curse).getPower() == 0) continue;

            // We have a flattened curse data - so don't look at an object, look directly at the curse
            this.setWeight(curse.modifyWeightForCurse(this.getWeight()));

            // Curses can adjust the ac, hit and dam modifiers
            this.setToAC(Guards.addGuardI(this.getToAC(), curse.getCombatAC()));
            this.setToHit(Guards.addGuardI(this.getToHit(), curse.getCombatToHit()));
            this.setToDam(Guards.addGuardI(this.getToDam(), curse.getCombatDam()));

            // The curse may extend the objects flags - C's of_union(obj->flags, curse_obj->flags).
            // setFlags is the named mutator for that, and unions into the real set. getFlags() must
            // NOT be used here: it hands back a copy, so unioning into it would build the merged set
            // and then throw it away, leaving this object's flags untouched and the curse silently
            // unapplied - a mistake the compiler cannot catch, which prices the object as though the
            // curse carried no flags at all.
            this.setFlags(curse.getObjectFlags());

            // The curses modifiers combine additively with those from this object;
            for (ObjectModifier om : curse.getModifiers().keySet()) {
                if (this.getModifiers().containsKey(om)) {
                    this.getModifiers().put(om, this.getModifiers().getOrDefault(om, 0) + curse.getModifiers().getOrDefault(om, 0));
                } else {
                    this.getModifiers().put(om, curse.getModifiers().getOrDefault(om, 0));
                }
            }

            // Resistances combine with standard logic for combining them.
            for (ElementEnum elem : ElementEnum.values()) {
                if (elem == ElementEnum.ELEM_MAX || elem == ElementEnum.ELEM_NONE) continue;
                ElementInfo curseElInfo = curse.getElInfo().getOrDefault(elem, null);
                int curseResLevel = curseElInfo == null ? 0 : curseElInfo.getResLevel();
                ElementInfo elInfo = getElInfo().getOrDefault(elem, null);
                int elInfoResLevel = elInfo == null ? 0 : elInfo.getResLevel();
                if (elInfo != null) {
                    if (elInfoResLevel >= 3) {
                        // Already immune
                        continue;
                    } else if (elInfoResLevel == 1) {
                        /*
                         * Has resistance.  An immunity will override
                         * that.  A resistance or no resistance on
                         * the curse will do nothing.  A vulnerability
                         * will convert the resistance to
                         * vulnerability + resistance.
                         */
                        if (curseResLevel >= 3) {
                            elInfo.setResLevel(3);
                        } else if (curseResLevel < 0) {
                            elInfo.setResLevel(VULN_AND_RES);
                        }
                    } else if (elInfoResLevel == VULN_AND_RES) {
                        // Combined result so far is vulnerability and resistance.
                        // Only change if there is an immunity
                        if (curseResLevel >= 3) {
                            elInfo.setResLevel(3);
                        }
                    } else if (elInfoResLevel < 0) {
                        /*
                         * Has vulnerability.  An immunity will override
                         * that.  A vulnerability or no resistance on
                         * the curse will do nothing.  A resistance will
                         * convert the vulnerability to vulnerability +
                         * resistance.
                         */
                        if (curseResLevel >= 3) {
                            elInfo.setResLevel(3);
                        } else if (curseResLevel == 1) {
                            elInfo.setResLevel(VULN_AND_RES);
                        }
                    } else {
                        /*
                         * With no resistance in the base attributes,
                         * the merged result will be the same as
                         * whatever is in the curse.
                         */
                        if (elInfoResLevel != 0) {
                            String message = "Invalid Resistance Level. Was " + elInfoResLevel + " expecting 0";
                            logger.error(message);
                            throw new RuntimeException(message);
                        }
                        elInfo.setResLevel(curseResLevel);
                    }
                } else {
                    if (curseElInfo != null) {
                        ElementInfo newElInfo = new ElementInfo();
                        newElInfo.setResLevel(curseResLevel);
                        putElInfo(elem, newElInfo);
                    }
                }
            }
        }

        // Fix up any resistances that ended up as VULN_AND_RES so they look like no resistance to the caller
        for (ElementEnum elem : this.getElInfo().keySet()) {
            if (this.getElInfo().get(elem).getResLevel() == VULN_AND_RES)
                this.getElInfo().get(elem).setResLevel(0);
        }
    }

    /**
     * Returns its input: a curse carries no curses of its own.
     *
     * <p>C reaches {@code curse_power(curses[i].obj, ...)}, whose whole body sits behind
     * {@code if (obj->curses)} ({@code obj-power.c:741}), and a curse object's curse list is empty.
     *
     * <p>Function cursePower commented in full on 260827.
     *
     * @param curse       the curse being priced
     * @param power       the running power total
     * @param verbose     unused
     * @param logFileName unused
     * @return {@code power}, unchanged
     */
    private int cursePower(Curse curse, int power, boolean verbose, String logFileName) {
        return power;
    }

    /**
     * Adds power for what this object does when used - the port of C's {@code effects_power}
     * ({@code obj-power.c:715}).
     *
     * <p>An object's own activation is worth its activation's power; failing that, the kind's power
     * stands in, which is how an ordinary wand or staff is priced for what it casts.
     *
     * <p>The guard asks whether there is an activation <em>at all</em>. C tests a single pointer;
     * the port holds a list, where the equivalent question is non-null and non-empty - an empty list
     * is the shape an object with no activation has, and treating it as an activation would both
     * throw and hide the fallback.
     *
     * <p>Function effectsPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with any activation power added
     */
    private int effectsPower(int power) {
        int q = 0;

        if (!activation.isEmpty() && activation.getFirst() != null)
            q = activation.getFirst().getPower();
        else if (getKind() != null)
            q = getKind().getPower();

        if (q != 0) {
            power += q;
            logger.info("Add {} power for item activation, total is {}", q, power);
        }

        return power;
    }

    /**
     * Returns its input: curses carry no activation.
     *
     * <p>C reaches {@code effects_power(curses[i].obj, p)}, whose first branch tests
     * {@code obj->activation} and whose second tests {@code obj->kind->power}
     * ({@code obj-power.c:719-722}). A curse object has no activation, and the shared curse object
     * kind carries no power, so both are zero and {@code p} comes back untouched.
     *
     * <p>Function effectsPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return {@code power}, unchanged
     */
    private int effectsPower(Curse curse, int power) {
        return power;
    }

    /**
     * Adds power for this object's elemental protections - the port of C's {@code element_power}
     * ({@code obj-power.c:637}).
     *
     * <p>Two things at once, and the order matters. Walking the elements prices each one on its own -
     * ignoring, resisting, being immune to or being vulnerable to it - and at the same time counts
     * how many fall into each of the combination rows. Only when that walk is finished are the
     * combination bonuses added, because a count read part-way through is not the object's.
     *
     * <p>An immunity is priced as immunity plus resistance, because it subsumes the resistance it
     * replaces.
     *
     * <p>An element the object says nothing about is skipped. That matches C, where a zero entry
     * satisfies neither the ignore test nor any of the three level tests, and cannot reach a
     * combination row either, because every row demands a level above zero.
     *
     * <p>The combination rows are shared mutable state, zeroed here before use; see
     * {@link ElementSet}.
     *
     * <p>Function elementPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with the elemental terms added
     */
    private int elementPower(int power) {
        int q;

        // zero the counts
        for (ElementSet elementSet : ObjectRegistry.elementSets) {
            elementSet.setCount(0);
        }

        // Analyse each element for ignore, vulnerability, resistance or immunity
        for (ElementPowers element : ObjectRegistry.elementPowers) {
            ElementInfo elInfo = getElInfo().get(element.getElement());
            if (elInfo != null && elInfo.getFlags() != null) {
                if (elInfo.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE)) {
                    if (element.getIgnorePower() != 0) {
                        q = element.getIgnorePower();
                        power += q;
                        logger.info("Add {} power for ignoring {}, total is {}", q, element.getName(), power);
                    }
                }
            }

            if (elInfo != null) {
                if (elInfo.getResLevel() == -1) {
                    if (element.getVulnPower() != 0) {
                        q = element.getVulnPower();
                        power += q;
                        logger.info("Add {} power for vulnerability to {}, total is {}", q, element.getName(), power);
                    }
                } else if (elInfo.getResLevel() == 1) {
                    if (element.getResPower() != 0) {
                        q = element.getResPower();
                        power += q;
                        logger.info("Add {} power for resistance to {}, total is {}", q, element.getName(), power);
                    }
                } else if (elInfo.getResLevel() == 3) {
                    if (element.getImPower() != 0) {
                        q = element.getImPower() + element.getResPower();
                        power += q;
                        logger.info("Add {} power for immunity to {}, total is {}", q, element.getName(), power);
                    }
                }
            }

            // Track combinations of element properties
            for (ElementSet set : ObjectRegistry.elementSets) {
                if ((set.getType() == element.getType())
                        && (elInfo != null && set.getResLevel() <= elInfo.getResLevel())) {
                    set.setCount(set.getCount() + 1);
                }
            }
        }

        // Add bonus if item has a full set of these flags
        for (ElementSet set : ObjectRegistry.elementSets) {
            if (set.getCount() > 1) {
                q = set.getFactor() * set.getCount() * set.getCount();
                power += q;
                logger.info("Add {} power for multiple {}, total is {}", q, set.getDescription(), power);
            }

            if (set.getCount() == set.getSize()) {
                q = set.getBonus();
                power += q;
                logger.info("Add {} power for full set of {}, total is {}", q, set.getDescription(), power);
            }
        }

        return power;
    }

    /**
     * Adds power for a curse's elemental protections, mirroring
     * {@link #elementPower(int)} against the curse's own element info.
     *
     * <p>Not an identity, unlike most of the curse overloads: {@code curse.txt} does grant and
     * withhold resistances, and C prices them by running the same function over the curse object.
     *
     * <p>Function elementPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total with the curse's elemental terms added
     */
    private int elementPower(Curse curse, int power) {
        int q;

        // zero the counts
        for (ElementSet elementSet : ObjectRegistry.elementSets) {
            elementSet.setCount(0);
        }

        // Analyse each element for ignore, vulnerability, resistance or immunity
        for (ElementPowers element : ObjectRegistry.elementPowers) {
            ElementInfo elInfo = curse.getElInfo().get(element.getElement());
            if (elInfo != null && elInfo.getFlags() != null) {
                if (elInfo.getFlags().has(ElementInfoEnum.EL_INFO_IGNORE)) {
                    if (element.getIgnorePower() != 0) {
                        q = element.getIgnorePower();
                        power += q;
                        logger.info("Add {} power for ignoring {}, total is {}", q, element.getName(), power);
                    }
                }
            }

            if (elInfo != null) {
                if (elInfo.getResLevel() == -1) {
                    if (element.getVulnPower() != 0) {
                        q = element.getVulnPower();
                        power += q;
                        logger.info("Add {} power for vulnerability to {}, total is {}", q, element.getName(), power);
                    }
                } else if (elInfo.getResLevel() == 1) {
                    if (element.getResPower() != 0) {
                        q = element.getResPower();
                        power += q;
                        logger.info("Add {} power for resistance to {}, total is {}", q, element.getName(), power);
                    }
                } else if (elInfo.getResLevel() == 3) {
                    if (element.getImPower() != 0) {
                        q = element.getImPower() + element.getResPower();
                        power += q;
                        logger.info("Add {} power for immunity to {}, total is {}", q, element.getName(), power);
                    }
                }
            }

            // Track combinations of element properties
            for (ElementSet set : ObjectRegistry.elementSets) {
                if ((set.getType() == element.getType())
                        && (elInfo != null && set.getResLevel() <= elInfo.getResLevel())) {
                    set.setCount(set.getCount() + 1);
                }
            }
        }

        // Add bonus if item has a full set of these flags
        for (ElementSet set : ObjectRegistry.elementSets) {
            if (set.getCount() > 1) {
                q = set.getFactor() * set.getCount() * set.getCount();
                power += q;
                logger.info("Add {} power for multiple {}, total is {}", q, set.getDescription(), power);
            }

            if (set.getCount() == set.getSize()) {
                q = set.getBonus();
                power += q;
                logger.info("Add {} power for full set of {}, total is {}", q, set.getDescription(), power);
            }
        }

        return power;
    }

    /**
     * Adds power for this object's flags - the port of C's {@code flags_power}
     * ({@code obj-power.c:581}).
     *
     * <p>Each flag is looked up in the object property table and priced at its base power times the
     * multiplier for this object's type, because the same flag is worth different amounts on
     * different things. A flag the table prices at zero is a derived one and adds nothing.
     *
     * <p>As with the elements, the walk both prices individual flags and counts them into families,
     * and the family bonuses are added only once the walk is done.
     *
     * <p>A flag the property table does not know is a data error rather than a runtime condition, so
     * this throws rather than skipping it.
     *
     * <p>The family rows are shared mutable state, zeroed here before use; see {@link FlagSet}.
     *
     * <p>Function flagsPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with the flag terms added
     */
    private int flagsPower(int power) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.copyFrom(this.getFlags());
        int q;

        // Zero the flag counts
        for (FlagSet flagSet : ObjectRegistry.flagSets.values()) {
            flagSet.setCount(0);
        }

        for (ObjectFlag flag : flags) {
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag);
            ObjectProperty property = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, wrapper);

            if (property == null) {
                String message = "Unknown ObjectProperty type in flagsPower.";
                logger.error(message);
                throw new RuntimeException(message);
            }

            if (property.getPower() != 0) {
                q = property.getPower() * property.getTypeMult(gettValue());
                power += q;
                logger.info("Add {} for {}, total is {}", q, property.getName(), power);
            }

            // Track combinations of flag types
            for (FlagSet flagSet : ObjectRegistry.flagSets.values()) {
                if (flagSet.getType() == property.getSubtype())
                    flagSet.setCount(flagSet.getCount() + 1);
            }
        }

        // Add extra power for multiple flags of the same type
        for (FlagSet flagSet : ObjectRegistry.flagSets.values()) {
            if (flagSet.getCount() > 1) {
                q = flagSet.getFactor() * flagSet.getCount() * flagSet.getCount();
                power += q;
                logger.info("Add {} power for multiple {}, total {}", q, flagSet.getDescription(), power);
            }

            // Add bonus if item has a full set of these flags
            if (flagSet.getCount() == flagSet.getSize()) {
                q = flagSet.getBonus();
                power += q;
                logger.info("Add {} power for full set of {}, total is {}", q, flagSet.getDescription(), power);
            }
        }

        return power;
    }

    /**
     * Adds power for a curse's flags, mirroring {@link #flagsPower(int)} against the curse's own
     * flag set.
     *
     * <p>The type multiplier is 1 rather than a lookup, and deliberately so: C prices the curse
     * object, whose tval is never assigned and so is {@code TV_NONE}, and no property in
     * {@code object_property.txt} names that type - so every one of them falls back on the table's
     * default of 1.
     *
     * <p>Function flagsPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total with the curse's flag terms added
     */
    private int flagsPower(Curse curse, int power) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.copyFrom(curse.getObjectFlags());
        int q;

        // Zero the flag counts
        for (FlagSet flagSet : ObjectRegistry.flagSets.values()) {
            flagSet.setCount(0);
        }

        for (ObjectFlag flag : flags) {
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag);
            ObjectProperty property = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, wrapper);

            if (property == null) {
                String message = "Unknown ObjectProperty type in flagsPower.";
                logger.error(message);
                throw new RuntimeException(message);
            }

            if (property.getPower() != 0) {
                q = property.getPower();
                power += q;
                logger.info("Add {} for {}, total is {}", q, property.getName(), power);
            }

            // Track combinations of flag types
            for (FlagSet flagSet : ObjectRegistry.flagSets.values()) {
                if (flagSet.getType() == property.getSubtype())
                    flagSet.setCount(flagSet.getCount() + 1);
            }
        }

        // Add extra power for multiple flags of the same type
        for (FlagSet flagSet : ObjectRegistry.flagSets.values()) {
            if (flagSet.getCount() > 1) {
                q = flagSet.getFactor() * flagSet.getCount() * flagSet.getCount();
                power += q;
                logger.info("Add {} power for multiple {}, total {}", q, flagSet.getDescription(), power);
            }

            // Add bonus if item has a full set of these flags
            if (flagSet.getCount() == flagSet.getSize()) {
                q = flagSet.getBonus();
                power += q;
                logger.info("Add {} power for full set of {}, total is {}", q, flagSet.getDescription(), power);
            }
        }

        return power;
    }

    /**
     * Adds power for this object's modifiers - the port of C's {@code modifier_power}
     * ({@code obj-power.c:544}).
     *
     * <p>Each modifier is priced at its value times its base power times the multiplier for this
     * object's type. A modifier the object does not carry reads as zero, which is what C's fixed
     * array gives and what the {@code getOrDefault} here stands in for.
     *
     * <p>Separately, the modifiers accumulate a weighted total - not all of them count equally - and
     * a large total buys a further bonus from the ability table, or a refusal if it is large enough.
     * That is what stops an object with many strong modifiers being priced as merely the sum of
     * them.
     *
     * <p>Function modifierPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with the modifier terms and any ability bonus added
     */
    private int modifierPower(int power) {
        int extraStatBonus = 0;
        int q;

        for (ObjectModifier om : ObjectModifier.values()) {
            if (om == ObjectModifier.OM_MAX || om == ObjectModifier.OM_NONE) continue;

            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, om);
            ObjectProperty mod = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, wrapper);
            if (mod == null) {
                String message = "Modifier nonexistent for " + om.name();
                logger.error(message);
                throw new RuntimeException(message);
            }

            int k;
            if (getModifiers() == null)
                k = 0;
            else
                k = getModifiers().getOrDefault(om, 0);
            extraStatBonus += k * mod.getMultiplier();

            if (mod.getPower() != 0) {
                q = (k * (mod.getPower() * mod.getTypeMult(gettValue())));
                power += q;
                if (q != 0)
                    logger.info("Add {} power for {} {}, total is {}", q, k, mod.getName(), power);
            }
        }

        // Add extra power term if there are a lot of ability bonuses
        if (extraStatBonus > 249) {
            logger.info("Inhibiting - Total ability bonus of {}} is too high", extraStatBonus);
            power += ObjectRegistry.INHIBIT_POWER;
        } else if (extraStatBonus > 0) {
            q = ObjectRegistry.abilityPower[extraStatBonus / 10];
            if (q == 0) return power;
            power += q;
            logger.info("Add {} power for modifier total of {}. total is {}", q, extraStatBonus, power);
        }

        return power;
    }

    /**
     * Adds power for a curse's modifiers, mirroring {@link #modifierPower(int)} against the curse's
     * own modifier map.
     *
     * <p>Walks the curse's declared modifiers rather than every modifier there is, which reaches the
     * same answer because an undeclared one contributes nothing.
     *
     * <p>No type multiplier, for the reason given on {@link #flagsPower(Curse, int)}: the curse
     * object's type is one no property names, so the multiplier is always 1.
     *
     * <p>Function modifierPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total with the curse's modifier terms added
     */
    private int modifierPower(Curse curse, int power) {
        int extraStatBonus = 0;
        int q;

        for (ObjectModifier om : curse.getModifiers().keySet()) {
            if (om == ObjectModifier.OM_MAX || om == ObjectModifier.OM_NONE) continue;

            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, om);
            ObjectProperty mod = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, wrapper);
            if (mod == null) {
                String message = "Modifier nonexistent for " + om.name();
                logger.error(message);
                throw new RuntimeException(message);
            }

            int k;
            if (curse.getModifiers() == null)
                k = 0;
            else
                k = curse.getModifiers().getOrDefault(om, 0);
            extraStatBonus += k * mod.getMultiplier();

            if (mod.getPower() != 0) {
                q = (k * mod.getPower());
                power += q;
                if (q != 0)
                    logger.info("Add {} power for {} {}, total is {}", q, k, mod.getName(), power);
            }
        }

        // Add extra power term if there are a lot of ability bonuses
        if (extraStatBonus > 249) {
            logger.info("Inhibiting - Total ability bonus of {}} is too high", extraStatBonus);
            power += ObjectRegistry.INHIBIT_POWER;
        } else if (extraStatBonus > 0) {
            q = ObjectRegistry.abilityPower[extraStatBonus / 10];
            if (q == 0) return power;
            power += q;
            logger.info("Add {} power for modifier total of {}. total is {}", q, extraStatBonus, power);
        }

        return power;
    }

    /**
     * Adds the flat bonus every piece of jewellery carries - the port of C's {@code jewelry_power}
     * ({@code obj-power.c:531}).
     *
     * <p>A ring or amulet is worth something for being one, before anything it does is counted.
     *
     * <p>Function jewelleryPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total, with the jewellery bonus added if this object is jewellery
     */
    private int jewelleryPower(int power) {
        if (gettValue().isJewellery()) {
            power += ObjectRegistry.BASE_JEWELERY_POWER;
            logger.info("Adding {} power for jewelery, total is {}",
                    ObjectRegistry.BASE_JEWELERY_POWER, power);
        }

        return power;
    }

    /**
     * Returns its input: a curse object is not jewellery.
     *
     * <p>C's {@code jewelry_power} tests {@code tval_is_jewelry(obj)} ({@code obj-power.c:533}), and
     * a curse object's tval is {@code TV_NONE}.
     *
     * <p>Function jewelleryPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return {@code power}, unchanged
     */
    private int jewelleryPower(Curse curse, int power) {
        return power;
    }

    /**
     * Adds power for this object's to-armour bonus - the port of C's {@code to_ac_power}
     * ({@code obj-power.c:500}).
     *
     * <p>Priced in bands rather than linearly: every point is worth the base rate, points above the
     * high threshold are worth it again, and points above the very high threshold twice again - so
     * a large bonus is worth disproportionately more than a small one. A bonus at or above the
     * inhibit threshold is refused outright rather than priced.
     *
     * <p>A zero bonus returns early, which keeps the log clean rather than changing the answer.
     *
     * <p>Function toAcPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with the to-armour terms added
     */
    private int toAcPower(int power) {
        if (getToAC() == 0) return power;

        int q = (getToAC() * ObjectRegistry.TO_AC_POWER) / 2;
        power += q;
        logger.info("Add {} for toAC of {}, total is {}", q, getToAC(), power);
        if (getToAC() > ObjectRegistry.HIGH_TO_AC) {
            q = ((getToAC() - (ObjectRegistry.HIGH_TO_AC - 1)) * ObjectRegistry.TO_AC_POWER);
            power += q;
            logger.info("Add {} power for high toAC, total is {}", q, power);
        }
        if (getToAC() > ObjectRegistry.VERYHIGH_TO_AC) {
            q = (getToAC() - (ObjectRegistry.VERYHIGH_TO_AC - 1)) * ObjectRegistry.TO_AC_POWER * 2;
            power += q;
            logger.info("Add {} power for very high toAC, total is {}", q, power);
        }
        if (getToAC() >= ObjectRegistry.INHIBIT_AC) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING: AC bonus too high.");
        }

        return power;
    }

    /**
     * Adds power for a curse's to-armour bonus, mirroring {@link #toAcPower(int)} against the
     * curse's own figure.
     *
     * <p>Not an identity: {@code curse.txt} does grant and withhold armour bonuses, and C prices
     * them by running the same function over the curse object.
     *
     * <p>Function toAcPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total with the curse's to-armour terms added
     */
    private int toAcPower(Curse curse, int power) {
        if (curse.getCombatAC() == 0) return power;

        int q = (curse.getCombatAC() * ObjectRegistry.TO_AC_POWER) / 2;
        power += q;
        logger.info("Add {} for toAC of {}, total is {}", q, curse.getCombatAC(), power);
        if (curse.getCombatAC() > ObjectRegistry.HIGH_TO_AC) {
            q = ((curse.getCombatAC() - (ObjectRegistry.HIGH_TO_AC - 1)) * ObjectRegistry.TO_AC_POWER);
            power += q;
            logger.info("Add {} power for high toAC, total is {}", q, power);
        }
        if (curse.getCombatAC() > ObjectRegistry.VERYHIGH_TO_AC) {
            q = (curse.getCombatAC() - (ObjectRegistry.VERYHIGH_TO_AC - 1)) * ObjectRegistry.TO_AC_POWER * 2;
            power += q;
            logger.info("Add {} power for very high toAC, total is {}", q, power);
        }
        if (curse.getCombatAC() >= ObjectRegistry.INHIBIT_AC) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING: AC bonus too high.");
        }

        return power;
    }

    /**
     * Adds power for this object's base armour class, adjusted for weight - the port of C's
     * {@code ac_power} ({@code obj-power.c:465}).
     *
     * <p>An object with base armour is worth a flat bonus for being armour at all - it halves acid
     * damage - plus a figure for the armour itself, scaled by how much armour it gives per unit of
     * weight. Light armour is therefore worth more than heavy armour of the same class, which is the
     * point.
     *
     * <p>The scaling is capped, explicitly so as not to overprice elven cloaks, which give a good
     * deal of armour for almost no weight. A weightless object cannot be scaled at all and takes a
     * fixed multiple instead.
     *
     * <p>The weight used is {@link #weightOne()}, so curses that make the object heavier or lighter
     * are already reflected; that is also why {@link #nonStandardWeightPower(int)} skips objects
     * with base armour, having been accounted for here.
     *
     * <p>Function acPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with the base armour terms added
     */
    private int acPower(int power) {
        int weight = weightOne();
        int q = 0;

        if (getBaseAC() != 0) {
            power += ObjectRegistry.BASE_ARMOUR_POWER;
            q += getBaseAC() * ObjectRegistry.BASE_AC_POWER / 2;
            logger.info("Adding {} power for base AC value", q);

            // Add power for AC per unit weight
            if (weight > 0) {
                int i = 750 * (getBaseAC() + getToAC()) / weight;

                // Don't overcharge for elven cloaks
                i = Math.min(450, i);

                q *= i;
                q /= 100;
            } else {
                // weightless (ethereal) armour items get fixed bonus
                q *= 5;
            }
            power += q;
            logger.info("Add {} power for AX per unit weight, now {}", q, power);
        }

        return power;
    }

    /**
     * Returns its input: a curse has no base armour class.
     *
     * <p>C's {@code ac_power} sits entirely behind {@code if (obj->ac)} ({@code obj-power.c:470}),
     * and {@code curse.txt} has no syntax for giving a curse base armour - {@code obj-curse.c} says
     * so where it merges the field.
     *
     * <p>Function acPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return {@code power}, unchanged
     */
    private int acPower(Curse curse, int power) {
        return power;
    }

    /**
     * Adds power for this object's to-hit bonus - the port of C's {@code to_hit_power}
     * ({@code obj-power.c:454}).
     *
     * <p>Linear, unlike the to-armour term: every point is worth the same. The rate is halved, which
     * is why the constant is doubled and the expression divides by two.
     *
     * <p>Function toHitPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total with the to-hit term added
     */
    private int toHitPower(int power) {
        int q = (toHit * ObjectRegistry.TO_HIT_POWER / 2);
        power += q;
        if (power != 0) {
            logger.info("Add {} power for to hit, total is {}", q, power);
        }

        return power;
    }

    /**
     * Adds power for a curse's to-hit bonus, mirroring {@link #toHitPower(int)} against the curse's
     * own figure.
     *
     * <p>Not an identity: curses adjust to-hit, and C prices that by running the same function over
     * the curse object.
     *
     * <p>Function toHitPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total with the curse's to-hit term added
     */
    private int toHitPower(Curse curse, int power) {
        int q = curse.getCombatToHit() * ObjectRegistry.TO_HIT_POWER / 2;
        power += q;
        if (power != 0) {
            logger.info("Add {} power for to hit, total is {}", q, power);
        }

        return power;
    }

    /**
     * Divides a launcher's power down so it can be compared with a melee weapon's - the port of C's
     * {@code rescale_bow_power} ({@code obj-power.c:442}).
     *
     * <p>The damage terms above assume a melee weapon landing {@code MAX_BLOWS} blows a turn. A
     * launcher does not, so its total is divided by the same figure; without it every bow would
     * outprice every sword.
     *
     * <p>Applies to whatever is worn in the shooting slot, which is how the test is phrased rather
     * than by asking whether the object is a bow.
     *
     * <p>Function rescaleBowPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total, rescaled if this object is worn in the shooting slot
     */
    private int rescaleBowPower(int power) {
        if (wieldSlot() == player.slotByName("shooting")) {
            power /= ObjectRegistry.MAX_BLOWS;
            logger.info("Rescaling bow power, total is {}", power);
        }

        return power;
    }

    /**
     * Returns its input: a curse is not worn in the shooting slot.
     *
     * <p>C's {@code rescale_bow_power} tests {@code wield_slot(obj) == slot_by_name(player,
     * "shooting")} ({@code obj-power.c:444}); a curse object is not wielded at all.
     *
     * <p>Function rescaleBowPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return {@code power}, unchanged
     */
    private int rescaleBowPower(Curse curse, int power) {
        return power;
    }

    /**
     * Adds power for this object's brands and slays - the port of C's {@code slay_power}
     * ({@code obj-power.c:335}).
     *
     * <p>Priced from the <em>best</em> brand or slay rather than the sum of them, because only one
     * applies to any given blow. That best figure is a percentage-style number where 100 means "no
     * better than a bare weapon", so subtracting 100 is what turns it into a bonus - and what lets a
     * weak brand price negatively. The floor of 1 rather than 0 is C's, and gives a deliberate
     * penalty when every brand and slay present is worthless.
     *
     * <p>The result is scaled by the damage dice squared, so the same brand is worth far more on a
     * heavy weapon than on a light one.
     *
     * <p>Carrying several then buys further bonuses - separately for slays, for brands, for having
     * both, and for kills, which are slays with a multiplier above three and counted apart from
     * them. Holding a complete set of any of the three buys a flat bonus on top.
     *
     * <p>Returns early when there is nothing to price, which is the common case.
     *
     * <p>Function slayPower commented in full on 260827.
     *
     * @param power     the running power total
     * @param verbose   {@code true} to log each brand and slay and the best figure
     * @param dicePower the damage-dice term this object was priced at, which scales the result
     * @return the total with the brand and slay terms added
     */
    private int slayPower(int power, boolean verbose, int dicePower) {
        int bestPower = 1;
        int numBrands = this.getBrands().size();
        int numSlays = 0;
        int numKills = 0;

        for (Brand b : this.getBrands()) {
            bestPower = Math.max(b.getPower(), bestPower);
        }

        for (Slay s : this.getSlays()) {
            if (s.getMultiplier() <= 3)
                numSlays++;
            else
                numKills++;

            bestPower = Math.max(bestPower, s.getPower());
        }

        // Return if no slays or brands
        if (numBrands + numKills + numSlays == 0)
            return power;

        if (verbose) {
            logger.info("Slay and brands: ");

            for (Brand b : this.getBrands()) {
                logger.info("{} x {}", b.getName(), b.getMultiplier());
            }

            for (Slay s : this.getSlays()) {
                logger.info("{} x {}", s.getName(), s.getMultiplier());
            }

            logger.info("Best power is {}", bestPower);
        }

        int q = (dicePower * dicePower * (bestPower - 100)) / 2500;
        power += q;
        logger.info("Add {} for slay power, total is {}", q, power);

        // Bonuses for multiple brands and slays
        if (numSlays > 1) {
            q = (numSlays * numSlays * dicePower) / (ObjectRegistry.DAMAGE_POWER * 5);
            power += q;
            logger.info("Add {} for multiple slays, total is {}", q, power);
        }
        if (numBrands > 1) {
            q = (2 * numBrands * numBrands * dicePower) / (ObjectRegistry.DAMAGE_POWER * 5);
            power += q;
            logger.info("Add {} for multiple brands, total is {}", q, power);
        }
        if (numSlays != 0 && numBrands != 0) {
            q = (numSlays * numBrands * dicePower) / (ObjectRegistry.DAMAGE_POWER * 5);
            power += q;
            logger.info("Add {} for slay and brand, total is {}", q, power);
        }
        if (numKills > 1) {
            q = (3 * numKills * numKills * dicePower) / (ObjectRegistry.DAMAGE_POWER * 5);
            power += q;
            logger.info("Add {} for multiple kills, total is {}", q, power);
        }
        if (numSlays == 8) {
            power += 10;
            logger.info("Add 10 power for full set of slays, total is {}", power);
        }
        if (numBrands == 5) {
            power += 20;
            logger.info("Add 20 power for full set of brands, total is {}", power);
        }
        if (numKills == 3) {
            power += 20;
            logger.info("Add 20 power for full set of kills, total is {}", power);
        }

        return power;
    }

    /**
     * Returns its input: no curse in {@code curse.txt} carries a brand or a slay.
     *
     * <p>C's {@code slay_power} returns {@code p} as soon as the counts come to zero
     * ({@code obj-power.c:366}), which is what running it over a curse object does.
     *
     * <p>Function slayPower commented in full on 260827.
     *
     * @param curse     the curse being priced
     * @param power     the running power total
     * @param verbose   unused
     * @param dicePower unused
     * @return {@code power}, unchanged
     */
    private int slayPower(Curse curse, int power, boolean verbose, int dicePower) {
        return power;
    }

    /**
     * Applies extra shooting might to the running total - the port of C's
     * {@code extra_might_power} ({@code obj-power.c:302}).
     *
     * <p>Might multiplies rather than adds: it raises the launcher's multiplier, and the whole
     * damage total so far is multiplied by the result. That is why this step comes after the damage
     * terms and before everything else.
     *
     * <p>Might at or above the inhibit threshold refuses the object instead of pricing it, returning
     * at once with the multiplier untouched.
     *
     * <p>Returns both figures because the caller keeps the multiplier as well; C passes it by value
     * and returns only the power, having no need of it afterwards.
     *
     * <p>Function extraMightPower commented in full on 260827.
     *
     * @param incoming the running power total and current multiplier
     * @return the updated total and multiplier
     */
    private PowerAndMult extraMightPower(PowerAndMult incoming) {
        int power = incoming.power();
        int mult = incoming.mult();
        int modMight;

        modMight = getModifiers().getOrDefault(ObjectModifier.OM_MIGHT, 0);

        if (modMight >= ObjectRegistry.INHIBIT_MIGHT) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING - too much extra might - quitting");
            return new PowerAndMult(power, mult);
        } else {
            mult += modMight;
        }
        logger.info("Mult after extra might is {}", mult);
        power *= mult;
        logger.info("After multiplying power for might, total is {}", power);
        return new PowerAndMult(power, mult);
    }

    /**
     * Applies a curse's extra shooting might, mirroring {@link #extraMightPower(PowerAndMult)}
     * against the curse's own modifier.
     *
     * <p>Not an identity: curses can carry a might modifier, and C prices it by running the same
     * function over the curse object.
     *
     * <p>Function extraMightPower commented in full on 260827.
     *
     * @param curse    the curse being priced
     * @param incoming the running power total and current multiplier
     * @return the updated total and multiplier
     */
    private PowerAndMult extraMightPower(Curse curse, PowerAndMult incoming) {
        int power = incoming.power();
        int mult = incoming.mult();
        int modMight;

        if (curse.getModifiers() != null)
            modMight = curse.getModifiers().getOrDefault(ObjectModifier.OM_MIGHT, 0);
        else
            modMight = 0;

        if (modMight >= ObjectRegistry.INHIBIT_MIGHT) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING - too much extra might - quitting");
            PowerAndMult outgoing = new PowerAndMult(power, mult);
            return outgoing;
        } else {
            mult += modMight;
        }

        logger.info("Mult after extra might is {}", mult);
        power *= mult;
        logger.info("After multiplying power for might, total is {}", power);
        return new PowerAndMult(power, mult);
    }

    /**
     * Applies extra shots to the running total - the port of C's {@code extra_shots_power}
     * ({@code obj-power.c:322}).
     *
     * <p>Proportional rather than additive: each extra shot raises the total by a tenth, because
     * shots multiply everything the launcher already does.
     *
     * <p>Shots at or above the inhibit threshold refuse the object. Negative shots are not handled,
     * as C's own comment says.
     *
     * <p>Function extraShotsPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total, scaled up for any extra shots
     */
    private int extraShotsPower(int power) {
        if (!getModifiers().containsKey(ObjectModifier.OM_SHOTS)
                || getModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0) == 0)
            return power;

        int modShots = getModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0);
        if (modShots >= ObjectRegistry.INHIBIT_SHOTS) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING - too many extra shots - quitting");
            return power;
        } else if (modShots > 0) {
            power *= (10 + modShots);
            power /= 10;
            logger.info("Adding {}% power for extra shots, total is {}", 10 * modShots, power);
        }

        return power;
    }

    /**
     * Applies a curse's extra shots, mirroring {@link #extraShotsPower(int)} against the curse's own
     * modifier.
     *
     * <p>Not an identity: curses can carry a shots modifier.
     *
     * <p>Function extraShotsPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total, scaled up for any extra shots the curse grants
     */
    private int extraShotsPower(Curse curse, int power) {
        if (!curse.getModifiers().containsKey(ObjectModifier.OM_SHOTS)
                || curse.getModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0) == 0) {
            return power;
        }

        int modShots = curse.getModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0);
        if (modShots >= ObjectRegistry.INHIBIT_SHOTS) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING - too many extra shots - quitting");
            return power;
        } else if (modShots > 0) {
            power *= (10 + modShots);
            power /= 10;
            logger.info("Adding {}% power for extra shots, total is {}", 10 * modShots, power);
        }

        return power;
    }

    /**
     * Applies extra blows to the running total - the port of C's {@code extra_blows_power}
     * ({@code obj-power.c:268}).
     *
     * <p>Two parts. The total is scaled by the blows the object gives relative to the assumed
     * maximum, and then a flat amount is added for damage the player deals that does not come from
     * the weapon - rings and the like - which extra blows also multiply.
     *
     * <p>Blows at or above the inhibit threshold refuse the object.
     *
     * <p>Function extraBlowsPower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total, scaled and boosted for any extra blows
     */
    private int extraBlowsPower(int power) {
        int q = power;

        if (getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0) == 0)
            return power;

        if (getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0) >= ObjectRegistry.INHIBIT_BLOWS) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING - too many extra blows - quitting");
        } else {
            power = power * (ObjectRegistry.MAX_BLOWS + getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0))
                    / ObjectRegistry.MAX_BLOWS;
            // Add boost for assumed off-weapon damage
            power += (ObjectRegistry.NONWEAP_DAMAGE * getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0)
                    * ObjectRegistry.DAMAGE_POWER / 2);
            logger.info("Add {} power for extra blows, total is {}", power - q, power);
        }

        return power;
    }

    /**
     * Applies a curse's extra blows, mirroring {@link #extraBlowsPower(int)} against the curse's own
     * modifier.
     *
     * <p>Not an identity: curses can carry a blows modifier.
     *
     * <p>Function extraBlowsPower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return the total, scaled and boosted for any extra blows the curse grants
     */
    private int extraBlowsPower(Curse curse, int power) {
        int q = power;

        if (curse.getModifiers() != null && curse.getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0) == 0)
            return power;

        if (curse.getModifiers() != null
                && curse.getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0) >= ObjectRegistry.INHIBIT_BLOWS) {
            power += ObjectRegistry.INHIBIT_POWER;
            logger.info("INHIBITING - too many extra blows - quitting");
            return power;
        } else {
            power = power * (ObjectRegistry.MAX_BLOWS + curse.getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0))
                    / ObjectRegistry.MAX_BLOWS;
            // Add boost for assumed off-weapon damage
            power += (ObjectRegistry.NONWEAP_DAMAGE * curse.getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0)
                    * ObjectRegistry.DAMAGE_POWER / 2);
            logger.info("Add {} power for extra blows, total is {}", power - q, power);
        }

        return power;
    }

    /**
     * Prices ammunition for the launcher that will fire it - the port of C's
     * {@code launcher_ammo_damage_power} ({@code obj-power.c:255}).
     *
     * <p>A missile is worth little on its own and a great deal once launched, so its total is
     * multiplied by the assumed launcher multiplier for its type and then rescaled to a per-turn
     * figure. Ego ammunition additionally takes the launcher's assumed to-damage bonus, because an
     * ego missile is the one worth enchanting.
     *
     * <p>The stored multiplier is doubled, which is why the divisor is twice the assumed blows; see
     * {@link Archery}.
     *
     * <p>Function launcherAmmoDamagePower commented in full on 260827.
     *
     * @param power the running power total
     * @return the total, multiplied and rescaled if this object is ammunition
     */
    private int launcherAmmoDamagePower(int power) {
        TValue ammoType;

        if (gettValue().isAmmo()) {
            ammoType = gettValue();
            if (ego != null)
                power += ObjectRegistry.archery.get(ammoType).getLaunchDamage() * ObjectRegistry.DAMAGE_POWER / 2;
            power = power * ObjectRegistry.archery.get(ammoType).getLaunchMult() / (2 * ObjectRegistry.MAX_BLOWS);
            logger.info("After multiplying ammo and rescaling, power is {}", power);
        }

        return power;
    }

    /**
     * Returns its input: a curse is not ammunition.
     *
     * <p>C's {@code launcher_ammo_damage_power} sits behind {@code tval_is_ammo(obj)}
     * ({@code obj-power.c:261}), and a curse object's tval is {@code TV_NONE}.
     *
     * <p>Function launcherAmmoDamagePower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power the running power total
     * @return {@code power}, unchanged
     */
    private int launcherAmmoDamagePower(Curse curse, int power) {
        return power;
    }

    /**
     * Reports the damage multiplier a launcher gives - the port of C's {@code bow_multiplier}
     * ({@code obj-power.c:158}).
     *
     * <p>Anything that is not a bow multiplies by one, which lets the caller apply the result
     * unconditionally. For a bow the multiplier is its {@code pval}, where the data file keeps it.
     *
     * <p>The method name has its letters transposed, which is worth knowing when searching for
     * callers.
     *
     * <p>Function bowMulitplier commented in full on 260827.
     *
     * @return the launcher's multiplier, or 1 for anything that is not one
     */
    private int bowMulitplier() {
        int mult = 1;

        if (gettValue() != TValue.TV_BOW)
            return mult;
        else
            mult = getpValue();

        logger.info("Base mult for this weapon is {}", mult);
        return mult;
    }

    /**
     * Returns 1: a curse is not a bow, so it multiplies nothing.
     *
     * <p>C's {@code bow_multiplier} returns its initial {@code mult} of 1 for any object whose tval
     * is not {@code TV_BOW} ({@code obj-power.c:162}).
     *
     * <p>Function bowMulitplier commented in full on 260827.
     *
     * @param curse the curse being priced
     * @return {@code 1}
     */
    private int bowMulitplier(Curse curse) {
        return 1;
    }

    /**
     * Prices a launcher for the ammunition it will fire - the port of C's
     * {@code ammo_damage_power} ({@code obj-power.c:232}).
     *
     * <p>The mirror of {@link #launcherAmmoDamagePower(int)}: a bow does no damage by itself, so it
     * is priced by what its ammunition is assumed to average. Which ammunition that is comes from
     * the launcher's kind flags, since a sling, a bow and a crossbow take different things.
     *
     * <p>Applies to whatever is worn in the shooting slot. Returns an increment rather than a new
     * total, which is why the caller adds it on.
     *
     * <p>Function ammoDamagePower commented in full on 260827.
     *
     * @param power the running power total, used only for the log line
     * @return the power to add for the ammunition this launcher fires, or zero
     */
    private int ammoDamagePower(int power) {
        int q = 0;
        TValue shoots = null;

        if (this.getKind() == null) return 0;
        ObjectKind kind = this.getKind();

        if (wieldSlot() == player.slotByName("shooting")) {
            if (kind.getKindFlags().has(ObjectKindFlag.KF_SHOOTS_SHOTS))
                shoots = TValue.TV_SHOT;
            else if (kind.getKindFlags().has(ObjectKindFlag.KF_SHOOTS_ARROWS))
                shoots = TValue.TV_ARROW;
            else if (kind.getKindFlags().has(ObjectKindFlag.KF_SHOOTS_BOLTS))
                shoots = TValue.TV_BOLT;

            if (shoots != null) {
                Archery arch = ObjectRegistry.archery.get(shoots);
                q = (arch.getAmmoDamage() * ObjectRegistry.DAMAGE_POWER / 2);
                logger.info("Adding {} power from ammo, total is {}", q, power + q);
            }
        }

        return q;
    }

    /**
     * Returns zero: a curse is not worn in the shooting slot, so there is no ammunition to price.
     *
     * <p>C's {@code ammo_damage_power} returns its {@code q} of 0 unless the object is in that slot
     * ({@code obj-power.c:238}).
     *
     * <p>Function ammoDamagePower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @param power unused
     * @return {@code 0}
     */
    private int ammoDamagePower(Curse curse, int power) {
        return 0;
    }

    /**
     * Prices what this object's damage dice are worth - the port of C's
     * {@code damage_dice_power} ({@code obj-power.c:199}).
     *
     * <p>A melee weapon or a missile is priced from its dice: average damage times the rate.
     *
     * <p>Anything else that is not a launcher can still be worth a damage term, if it carries
     * something that makes the player's other attacks better - a brand, a slay, or a blows, shots or
     * might modifier. Such an object is credited with a flat assumed damage instead, because there
     * are no dice to price.
     *
     * <p>Returns the dice term alone rather than a running total; the caller adds it on and keeps it,
     * because the brand and slay pricing needs it later.
     *
     * <p>Function damageDicePower commented in full on 260827.
     *
     * @return the damage-dice term for this object
     */
    private int damageDicePower() {
        int dice = 0;

        // Add damage from dice for any wearable weapon or ammo
        if (this.gettValue().isMeleeWeapon() || this.gettValue().isAmmo()) {
            dice = ((this.damageDice * (this.damageSides + 1) * ObjectRegistry.DAMAGE_POWER) / 4);
            logger.info("Add {} power for damage dice, ", dice);
        } else if (wieldSlot() != player.slotByName("shooting")) {
            if (!this.getBrands().isEmpty() || !this.getSlays().isEmpty()
                    || getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0) > 0
                    || getModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0) > 0
                    || getModifiers().getOrDefault(ObjectModifier.OM_MIGHT, 0) > 0) {
                dice = (ObjectRegistry.WEAP_DAMAGE * ObjectRegistry.DAMAGE_POWER);
                logger.info("Add {} power for non-weapon combat bonuses.", dice);
            }
        }

        return dice;
    }

    /**
     * Prices what a curse's combat modifiers are worth as a damage term, mirroring the second branch
     * of {@link #damageDicePower()}.
     *
     * <p>Only that branch applies: a curse has no dice of its own and is not worn in the shooting
     * slot, so C reaches the same test - blows, shots or might above zero - and credits the same
     * flat assumed damage.
     *
     * <p>Function damageDicePower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @return the damage-dice term for the curse
     */
    private int damageDicePower(Curse curse) {
        int dice = 0;

        // Add damage from dice for any wearable weapon or ammo
        if (curse.getModifiers() != null) {
            if (curse.getModifiers().getOrDefault(ObjectModifier.OM_BLOWS, 0) > 0
                    || curse.getModifiers().getOrDefault(ObjectModifier.OM_SHOTS, 0) > 0
                    || curse.getModifiers().getOrDefault(ObjectModifier.OM_MIGHT, 0) > 0) {
                dice = (ObjectRegistry.WEAP_DAMAGE * ObjectRegistry.DAMAGE_POWER);
                logger.info("Add {} power for non-combat bonuses.", dice);
            }
        }

        return dice;
    }

    /**
     * Adds power for this object's to-damage bonus - the port of C's {@code to_damage_power}
     * ({@code obj-power.c:177}).
     *
     * <p>Counted twice for an object that is neither a weapon, nor ammunition, nor worn in the
     * shooting slot. That is deliberate in C: a ring of damage improves every blow the player lands,
     * where a weapon's bonus improves only its own, so the ring is worth more per point.
     *
     * <p>Function toDamagePower commented in full on 260827.
     *
     * @return this object's to-damage term
     */
    private int toDamagePower() {
        int power = (this.toDam * ObjectRegistry.DAMAGE_POWER / 2);
        if (power != 0)
            logger.info("{} power from to_dam", power);

        // Add second lot of damage power for non weapons
        if ((this.wieldSlot() != player.slotByName("shooting"))
                && !this.gettValue().isMeleeWeapon()
                && !this.gettValue().isAmmo()) {
            int nonWeaponPower = this.toDam * ObjectRegistry.DAMAGE_POWER;
            power += nonWeaponPower;
            if (nonWeaponPower != 0)
                logger.info("Add {} from non-weapon to_dam, total {}", nonWeaponPower, power);
        }

        return power;
    }

    /**
     * Adds power for a curse's to-damage bonus, mirroring {@link #toDamagePower()} against the
     * curse's own figure.
     *
     * <p>Takes the second lot of damage power unconditionally, and that is right: a curse object is
     * not a weapon, not ammunition and not worn in the shooting slot, so C always reaches that
     * branch.
     *
     * <p>Function toDamagePower commented in full on 260827.
     *
     * @param curse the curse being priced
     * @return the curse's to-damage term
     */
    private int toDamagePower(Curse curse) {
        int power = curse.getCombatDam() * ObjectRegistry.DAMAGE_POWER / 2;
        if (power != 0) logger.info("{} power from to_dam", power);

        // add second lot of damage power for non weapons
        int q = curse.getCombatDam() * ObjectRegistry.DAMAGE_POWER;
        power += q;
        if (q != 0) logger.info("Add {} from to_dam, total {}", q, power);

        return power;
    }

    /**
     * Reports which equipment slot this object would be worn in - the port of C's
     * {@code wield_slot} ({@code obj-gear.c}).
     *
     * <p>Most types map straight onto a slot. Weapons, rings and lights go through the slot search
     * instead, because there may be more than one of them and an empty one is preferred; rings in
     * particular is why the search exists.
     *
     * <p>Answers a slot index rather than a slot, which is what the callers compare against
     * {@code slotByName}. An object that belongs in no slot answers {@code -1}.
     *
     * <p>Function wieldSlot commented in full on 260827.
     *
     * @return the index of the slot this object would occupy, or {@code -1} if it is not wearable
     */
    private int wieldSlot() {
        switch (this.gettValue()) {
            case TV_BOW:
                return player.slotByType(EquipmentSlotsEnum.EQUIP_BOW, false);
            case TV_AMULET:
                return player.slotByType(EquipmentSlotsEnum.EQUIP_AMULET, false);
            case TV_CLOAK:
                return player.slotByType(EquipmentSlotsEnum.EQUIP_CLOAK, false);
            case TV_SHIELD:
                return player.slotByType(EquipmentSlotsEnum.EQUIP_SHIELD, false);
            case TV_GLOVES:
                return player.slotByType(EquipmentSlotsEnum.EQUIP_GLOVES, false);
            case TV_BOOTS:
                return player.slotByType(EquipmentSlotsEnum.EQUIP_BOOTS, false);
        }

        if (this.gettValue().isMeleeWeapon())
            return player.slotByType(EquipmentSlotsEnum.EQUIP_WEAPON, false);
        else if (this.gettValue().isRing())
            return player.slotByType(EquipmentSlotsEnum.EQUIP_RING, false);
        else if (this.gettValue().isLight())
            return player.slotByType(EquipmentSlotsEnum.EQUIP_LIGHT, false);
        else if (this.gettValue().isBodyArmour())
            return player.slotByType(EquipmentSlotsEnum.EQUIP_BODY_ARMOR, false);
        else if (this.gettValue().isHeadArmour())
            return player.slotByType(EquipmentSlotsEnum.EQUIP_HAT, false);

        // No slots available
        return -1;
    }

    /**
     * Answers whether the player has learned what this object's flavour means - the port of C's
     * {@code object_flavor_is_aware} ({@code obj-desc.c}).
     *
     * <p>Awareness lives on the kind, not the object: learning that one blue potion is cure light
     * wounds teaches the player about every blue potion.
     *
     * <p>Throws for an object with no kind, where {@link #flavourIsAware()} answers {@code false}
     * for the same state. The two differ because this one is called where a kind must exist and a
     * missing one is a defect rather than a case.
     *
     * <p>Function objectFlavourIsAware commented in full on 260827.
     *
     * @return {@code true} if the player is aware of this object's flavour
     */
    private boolean objectFlavourIsAware() {
        if (getKind() == null) {
            String message = "Illegal call on objectFlavourIsAware - no kind exists";
            logger.error(message);
            throw new RuntimeException(message);
        }
        return getKind().isAware();
    }

    /**
     * Answers whether the player's class can read this object as a spell book - the port of C's
     * {@code obj_can_browse} ({@code obj-util.c}).
     *
     * <p>Delegates to the kind, since browsability is a property of the book rather than the copy.
     * Read by the pack ordering, which lists readable books first.
     *
     * <p>Function canBrowse commented in full on 260827.
     *
     * @return {@code true} if the current player's class can browse this object
     */
    private boolean canBrowse() {
        return this.getKind().canBrowse();
    }

    /**
     * Prints a message with the object's own details substituted into it - the port of C's
     * {@code print_custom_message} ({@code obj-util.c}).
     *
     * <p>Messages in the data files are written with tags in braces, so a single line in
     * {@code object.txt} serves whatever object triggers it. The tags are replaced here and the
     * finished text handed to {@link Message#messageType} under the caller's type. Four tags are
     * understood, looked up by {@link MessageTag#getTag}:
     *
     * <ul>
     * <li>{@code {name}} - the object's full name with its quantity prefix, from
     *     {@link #description} under {@code ODESC_PREFIX | ODESC_BASE}.</li>
     * <li>{@code {kind}} - the kind's name alone, from {@link #objectKindName} with
     *     {@code easyKnow} set: no quantity, no ego or artifact name, no runes.</li>
     * <li>{@code {s}} - the verb ending, written as {@code glow{s}}. It yields an {@code s} for a
     *     single object and nothing at all for a pile, so the same sentence reads for both.</li>
     * <li>{@code {is}} - {@code is} for a single object, {@code are} for a pile.</li>
     * </ul>
     *
     * <p>A tag in braces that is not one of those is dropped whole, braces included, exactly as
     * C's {@code default} arm does. A brace with no closing brace after it - either running to the
     * end of the string or stopped by a non-letter - is itself dropped and the text following it
     * kept verbatim, which is again what C does by resuming from the character after the brace.
     *
     * <p>C reads the object from a pointer that may be {@code null}, which is how the unarmed
     * player is described: with no object, {@code {name}} and {@code {kind}} both become
     * {@code hands}, {@code {is}} becomes {@code are}, and {@code {s}} prints nothing. This
     * version is called on the object itself, so {@code noObject} carries that case instead, and
     * every place C tests {@code obj} this tests the flag.
     *
     * <p>Two divergences from the C, neither reachable from the shipped data files. Tag lookup
     * matches the whole tag where C's {@code msg_tag_lookup} matches only its opening letters, so
     * a malformed {@code {names}} is dropped here and read as {@code {name}} there. And C builds
     * the message in a 1024-byte buffer and silently truncates at it, where this builds a string
     * and cannot.
     *
     * <p>Function printCustomMessage coded 260829, commented in full on 260829.
     *
     * @param string   the message template, which may be {@code null} - C is called with the
     *                 message field of a property that need not have one, and answers by printing
     *                 nothing
     * @param msgT     the message type to tag the finished text with, for the front-end to colour
     *                 and sound it by
     * @param player   the player the name is described to, passed through to {@link #description}
     * @param noObject whether to describe the player's bare hands rather than this object
     */
    public void printCustomMessage(String string, MessageType msgT, Player player, boolean noObject) {
        if (string == null) return;

        StringBuilder sb = new StringBuilder();

        // Strings have tags in surrounded by {}. extract them and replace with appropriate text
        int next = string.indexOf('{');
        while (next >= 0) {
            sb.append(string.substring(0, next));
            string = string.substring(next + 1);

            StringBuilder tagSB = new StringBuilder();
            int index = 0;
            while (index < string.length() && string.charAt(index) != '}'
                    && Character.isAlphabetic(string.charAt(index))) {
                tagSB.append(string.charAt(index));
                index++;
            }

            if (index == string.length()) {
                // No closing brace was found - add the opening brace and
                // the tag in and jump to the next open brace
                sb.append(tagSB.toString());
                string = "";
                break;
            }

            String tag = tagSB.append("}").toString();

            if (string.charAt(index) == '}') {
                MessageTag mtag = MessageTag.getTag(tag);
                switch (mtag) {
                    case MSG_TAG_NAME -> {
                        Flag<ObjectDescription> descs = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_PREFIX,
                                ObjectDescription.ODESC_BASE);
                        if (noObject) sb.append("hands");
                        else sb.append(description(descs, player));
                        string = string.substring(mtag.getSize());
                    }
                    case MSG_TAG_KIND -> {
                        if (noObject) sb.append("hands");
                        else sb.append(objectKindName(getKind(), true));
                        string = string.substring(mtag.getSize());
                    }
                    case MSG_TAG_VERB -> {
                        if (!noObject && getNumber() == 1) {
                            sb.append("s");
                        }
                        string = string.substring(mtag.getSize());
                    }
                    case MSG_TAG_VERB_IS -> {
                        if (noObject || getNumber() > 1) sb.append("are");
                        else sb.append("is");
                        string = string.substring(mtag.getSize());
                    }
                    default -> string = string.substring(tag.length());
                }

            }

            next = string.indexOf('{');
        }

        sb.append(string);

        Message.messageType(msgT, sb.toString());
    }

    /**
     * Builds a stripped-down name for a kind - the port of C's {@code object_kind_name}
     * ({@code obj-desc.c}).
     *
     * <p>An unaware flavoured kind answers with the bare flavour text, so an unidentified potion
     * reads as its colour rather than its effect. Everything else answers with the kind's own
     * name, run through {@link #objDescNameFormat} with no modifier and in the singular, which
     * strips the {@code &} article marker and resolves any {@code ~} or {@code |x|y|} in the
     * template.
     *
     * <p>{@code easyKnow} forces the identified name regardless of awareness. C uses it where the
     * caller already knows what the kind is - the knowledge menus, the wizard-mode object list and
     * the ignore settings - rather than as a property of the object.
     *
     * <p>Note this is the kind's name, not an object's: there is no quantity prefix, no ego or
     * artifact name, and no runes.
     *
     * <p>C writes into a caller-supplied buffer and truncates to its size. This version returns a
     * string and so cannot truncate, matching the divergence already recorded on
     * {@link #objDescNameFormat}.
     *
     * <p>Function objectKindName commented in full on 260829.
     *
     * @param kind     the kind to name
     * @param easyKnow whether to use the identified name even when the player is unaware
     * @return the flavour text for an unaware flavoured kind, otherwise the formatted kind name
     */
    private String objectKindName(@NotNull ObjectKind kind, boolean easyKnow) {
        if (!easyKnow && !kind.isAware() && kind.getFlavour() != null)
            return kind.getFlavour().getText();

        return objDescNameFormat(kind.getName(), null, false);
    }

    /**
     * Formats an object-name template into display text - the port of C's
     * {@code obj_desc_name_format} ({@code obj-desc.c}).
     *
     * <p>Templates come from {@code object.txt}, {@code object_base.txt} and the hard-coded
     * basenames in C's {@code obj_desc_get_basename}, and carry four formatting characters:
     *
     * <ul>
     * <li>{@code &} and the spaces following it are dropped. The article they stand for is chosen
     *     further out, by the quantity prefix, which looks for the {@code &} in the unformatted
     *     template.</li>
     * <li>{@code ~} at the end of a word pluralises it when {@code pluralise} is set, as
     *     {@code es} after {@code s}, {@code x} or {@code h} and {@code s} otherwise.</li>
     * <li>{@code |x|y|} yields {@code x} when singular and {@code y} when plural, which is how
     *     {@code Sta|ff|ves|} becomes either staff or staves.</li>
     * <li>{@code #} is replaced by {@code modString} - a flavour for flavoured kinds, the book's
     *     own name for books - formatted first by a recursive call that carries the same
     *     pluralisation but no further modifier of its own.</li>
     * </ul>
     *
     * <p>C walks the template once, left to right, copying bytes into a bounded buffer. This
     * version instead rewrites an immutable string in passes: ampersands, then the modifier, then
     * tildes, then bars. That is a deliberate divergence, and it buys four differences in
     * behaviour, none of them reachable from the shipped game data:
     *
     * <ul>
     * <li>Because the modifier goes in before the tilde pass, a {@code ~} written directly after a
     *     {@code #} pluralises against the last character of the substituted modifier, where C
     *     sees the {@code #} itself and so always adds a bare {@code s}. No basename puts the two
     *     in that order.</li>
     * <li>A template whose bar count is not a multiple of three is rejected whole and returned
     *     unformatted. C has no such check and instead truncates everything from the unmatched bar
     *     onwards.</li>
     * <li>A {@code ~} with no character before it is reported, and the text formatted so far is
     *     returned. C reads the byte before the {@code ~} unconditionally, which at the first
     *     character of the template is off the front of the allocation.</li>
     * <li>There is no bound on the output. C truncates to the caller's buffer size.</li>
     * </ul>
     *
     * <p>Both error exits hand back the text with any unconsumed bars and tildes still in it, so a
     * malformed template shows up in the game rather than being quietly swallowed.
     *
     * <p>Function objDescNameFormat commented in full on 260829.
     *
     * @param string    the name template to format
     * @param modString the text to substitute for {@code #}, or {@code null} to leave any
     *                  {@code #} in place
     * @param pluralise whether to take the plural form of every {@code ~} and {@code |x|y|}
     * @return the formatted name
     */
    private String objDescNameFormat(@NotNull String string, @Nullable String modString, boolean pluralise) {
        StringBuilder result = new StringBuilder();

        // Trim '&'
        while (string.contains("&")) {
            int amp = string.indexOf('&');
            String start = string.substring(0, amp);
            String end = string.substring(amp + 1);
            while (end.startsWith(" ")) {
                end = end.substring(1);
            }
            string = start + end;
        }

        // Swap in ModString if we need to
        if (string.contains("#") && modString != null) {
            string = string.replace("#", objDescNameFormat(modString, null, pluralise));
        }

        // Check that the number of | in the string is strictly divisible by 3.
        int noOfBars = string.contains("|") ? string.split("\\|", -1).length - 1 : 0;
        if (noOfBars % 3 != 0) {
            String message = "Error: " + noOfBars + " bars found in string, should be a multiple of 3.";
            logger.error(message);
            return string;
        }

        // Find words we need to pluralise and do so
        if (pluralise) {
            while (string.contains("~")) {
                int plural = string.indexOf('~');
                if (plural == 0) {
                    String message = "Error: ~ found at position 1 in string: " + string;
                    logger.error(message);
                    return result.toString() + string;
                }
                result.append(string, 0, plural);
                char prev = string.charAt(plural - 1);
                if (prev == 's' || prev == 'x' || prev == 'h') {
                    result.append("es");
                } else {
                    result.append("s");
                }
                string = string.substring(plural + 1);
            }
            string = result.toString() + string;

            // Pluralise special plurals
            // Remove the bits |SINGLE|plural| bits
            while (string.contains("|")) {
                int first = string.indexOf('|');
                int second = string.indexOf('|', first + 1);
                int third = string.indexOf('|', second + 1);
                string = string.substring(0, first)
                        + string.substring(second + 1, third)
                        + string.substring(third + 1);
            }
        } else {
            // remove ~ characters
            string = string.replace("~", "");

            // Remove the |single|PLURAL| bits
            while (string.contains("|")) {
                int first = string.indexOf('|');
                int second = string.indexOf('|', first + 1);
                int third = string.indexOf('|', second + 1);
                string = string.substring(0, first)
                        + string.substring(first + 1, second)
                        + string.substring(third + 1);
            }
        }

        return string;
    }

    /**
     * A running power total and the shooting multiplier that goes with it, returned together by the
     * extra-might step.
     *
     * <p>Exists because C's {@code extra_might_power} takes the multiplier as an argument and
     * returns the power, mutating nothing; the port's version needs to hand back both, and a record
     * says so more plainly than an out-parameter would.
     *
     * @param power the running power total
     * @param mult  the shooting multiplier after any extra might
     */
    private record PowerAndMult(int power, int mult) {
    }
}