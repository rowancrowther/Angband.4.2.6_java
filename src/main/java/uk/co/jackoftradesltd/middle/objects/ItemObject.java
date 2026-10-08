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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.gameinput.GameInputHolder;
import uk.co.jackoftradesltd.middle.numerics.Guards;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;
import uk.co.jackoftradesltd.middle.strings.MessageTag;
import uk.co.jackoftradesltd.middle.utils.NumberUtils;

import java.util.*;

import static uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum.ORIGIN_MIXED;
import static uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum.ORIGIN_NONE;

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
 * <p>The class also holds the power calculation of {@code obj-power.c}, which prices an item's
 * usefulness ({@code objectPower}) and feeds the gold value. A {@link Curse} holds an item of its
 * own ({@link Curse#getItemObject()}), but the calculation is not run over it as C does; most of
 * it exists twice, once for an item and once for a curse, the curse versions reading the curse's
 * item and in several cases reduced to an identity. The curse pricing also builds scratch copies of
 * the item with the curses folded in.
 *
 * <p>The knowledge, ignoring and recharge queries ({@link #isKnown}, {@link #flagsKnown},
 * {@link #ignoreLevelOf}, {@link #numberCharging} and their neighbours) report on an item and
 * change nothing, with one exception: {@link #rechargeTimeout} spends a turn of the item's
 * timeout. The quality table they read for ignoring lives in {@code ObjectInfo}, and the marks
 * a player has put on an ego live on the {@link EgoItem}, not here.
 *
 * <p>{@link #modMessage} is the message half of learning a modifier by use: it prints the line
 * for a modifier the player has just noticed, from the sign of this item's value, and changes
 * nothing. The learning itself is done by the knowledge code that calls it.
 *
 * <p>The text and placement side turns an item into words and says where it goes.
 * {@link #description} is still a stub, {@link #printCustomMessage} fills the tags of a data-file
 * message from the item, and {@link #objectKindName} and {@link #objDescNameFormat} build a kind's
 * name. Those two and {@link #objectValue}, which prices a stack for the shops and for ordering
 * the pack, are {@code public} as in C. {@link #wieldSlot} answers which equipment slot the item would be worn in, {@link #canBrowse}
 * whether the player's class can read it, and {@link #getItemObjectADC} how it is drawn.
 * {@link #wipe} blanks every field, and {@link #initCurses} gives the item a fresh curse map.
 * {@link #copy} makes an independent duplicate, rebuilding every mutable container and sharing the
 * templates the item points at.
 *
 * <p>The rest is the field-by-field surface of {@code struct object}. Two constructors build an
 * item, one blank and one from every parsed field, and each field then has a getter, a setter or
 * both. C reads and assigns these struct members inline wherever it likes, so the port gathers
 * each access into one named method. {@link #getFlags()} and {@link #getNotice()} hand out copies
 * and take changes through named mutators ({@link #getObjectFlags()} is the live exception);
 * {@link #getNoticeHas} tests one notice flag without a copy. The modifier, element, brand, slay and curse collections are edited
 * through add, put, remove and clear methods, because their getters answer an immutable empty
 * collection for a field that was never built. A setter that takes a whole collection stores it
 * as given, except {@link #setCurses} and {@link #clearAndPutCurses}, which copy the map they are
 * handed.
 *
 * <p>Class ItemObject commented in full on 261002, knowledge and recharge note added on 261002,
 * text and placement note added on 261002, constructor and accessor note added on 261002, notice
 * mutator note added on 261003, notice test note added on 261003, modifier message note added
 * on 261004, copy note added on 261007, power calculation note corrected on 261007, public
 * pricing and name methods noted on 261008.
 *
 * @author Rowan Crowther
 * @see KnownObject
 */
public class ItemObject {
    /**
     * Logger used to report the impossible states the port throws on where C asserts: a split that
     * would take the whole stack, an absorb run in a store, a negative weight reaching the power
     * calculation, and an element held at an impossible resistance level during a curse merge.
     *
     * <p>It also carries the power calculation's running commentary at info level, where C writes
     * the same lines to a log file with {@code log_obj}.
     *
     * <p>{@link #flagMessage} reports its two data errors here, a flag with no entry in
     * {@code object_property.txt} and a flag index that could never be valid, where C prints a
     * "Bug:" line to the player.
     *
     * <p>{@link #objDescNameFormat} reports its two malformed-template exits here, a bar count that
     * is not a multiple of three and a {@code ~} with no character before it, where C either
     * truncates the name or reads off the front of the template.
     *
     * <p>Field logger commented in full on 261002, flag message note added on 261002, name template
     * note added on 261008.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The order an item's {@link #curses} map keeps its entries in: ascending curse index, with the
     * name as a tie-break. It stands in for the order of C's {@code obj->curses[i]} array, which is
     * walked from slot 0 up because the array is indexed by curse.
     *
     * <p>Every map built by {@link #cursesFactory()} uses it, so {@link #getCurses()} iterates in
     * C's order whatever order the curses were added in. The name only matters for two curses that
     * share an index, which a loaded registry never has; the test curses built by hand all carry
     * index zero, so for them the name decides.
     *
     * <p>The name comparison is null-safe ({@code nullsFirst}), so a {@code null} name on a tied
     * index sorts first rather than throwing from {@link TreeMap#put}. It is a second layer: the
     * {@link Curse} constructor rejects a {@code null} or empty name, so no {@link Curse} reaches
     * this comparator with one. C has no counterpart, as its curses are array slots and carry no
     * comparator.
     *
     * <p>Package-private so {@link ObjectUtils#copyCurses} can build its merged map with the same
     * ordering.
     *
     * <p>Field CURSE_ORDER commented in full on 261003, amended on 261007.
     */
    static final Comparator<Curse> CURSE_ORDER = Comparator.comparing(Curse::getIndex)
            .thenComparing(Curse::getName, Comparator.nullsFirst(Comparator.naturalOrder()));

    /**
     * The player this object's calculations are asked about - the equipment slots it would be worn
     * in, the quiver it might sit in, the cave its knowledge lives in. Stands in for C's
     * {@code player} global, which C functions read at the moment they run.
     *
     * <p>An instance field, one per item, set from {@link GameState#getPlayer()} by both
     * constructors. It is only a snapshot: an item built before a character exists holds
     * {@code null}, and one that outlives a change of player holds the old one. Methods that must
     * see the live player - {@link #earlierObject} and {@link #similar} - therefore refresh it from
     * {@link GameState#getPlayer()} on every call, which means they overwrite it. {@link #objectAbsorb}
     * refreshes it as well, and hands it on to {@link #objectAbsorbMerge} as a parameter.
     * {@link #wieldSlot} refreshes it too, so every caller of that sees the live body. The partial
     * absorb is the one reader that still depends on the snapshot, because it hands the field to
     * {@link #objectAbsorbMerge} without refreshing it.
     *
     * <p>The power calculation reads it through {@link #wieldSlot}, which every shooting-slot test
     * in the damage and bow steps goes through, and which refreshes it itself, so those steps see
     * the live player as C's {@code player} global does. {@link #toDamagePower()},
     * {@link #damageDicePower()} and {@link #ammoDamagePower(int)} also refresh it before they look
     * up the shooting slot, a second refresh of the same value, and {@link #rescaleBowPower(int)}
     * reads {@link GameState#getPlayer()} directly for that lookup. The refresh finds {@code null}
     * when no character exists, and the slot lookup then throws where C would crash.
     *
     * <p>Field player commented in full on 261002, power note revised on 261002 and again on 261002
     * for the damage steps, wieldSlot refresh added on 261002.
     */
    private Player player;

    /**
     * The object kind this item is an instance of. C's {@code obj->kind}.
     *
     * <p>Two items are only candidates to stack if their kinds are equal, and awareness of the
     * item's flavour is read from the kind, not the item. The pricing code takes its figures from
     * here too: {@link #objectValueBase} returns the kind's {@code cost} for an aware object, and
     * the fixed-price route of {@link #objectValueReal} starts from it. {@link #effectsPower(int)}
     * falls back on the kind's power when the item carries no activation of its own, and skips the
     * fallback for an item with no kind, which C would dereference.
     *
     * <p>{@link #ammoDamagePower(int)} reads the kind's flags to learn which ammunition a launcher
     * fires, and answers zero for an item with no kind where C would dereference it.
     *
     * <p>The knowledge and ignore queries read it too. {@link #flavourIsAware} and {@link #easyKnow}
     * ask it whether the player is aware of the kind, {@link #flagsKnown} adds the kind's own flags
     * back in for an aware one, {@link #getIgnoreTypeOf} matches its name against the quality table,
     * and {@link #isGood} compares the item's bonuses with the kind's dice. {@link #hasStandardToH}
     * calls an item with no kind standard, which is how a curse's bare object passes.
     *
     * <p>{@link #objectKindName} is handed one to name, and {@link #printCustomMessage} passes this
     * item's own. The glyph methods read its flavour, glyph and colour, and {@link #canBrowse} asks
     * it whether the player's class can read it. {@link #wipe} sets it to {@code null}.
     *
     * <p>Field kind commented in full on 261002, pricing added on 261002, effects power added on 261002,
     * ammunition read added on 261002, knowledge reads added on 261002, text and glyph reads added
     * on 261002.
     */
    private ObjectKind kind;
    /**
     * The ego type applied to this item, or {@code null} for an ordinary or artifact item. C's
     * {@code obj->ego}.
     *
     * <p>Compared by identity in {@link #similar}, as C compares pointers: two items stack only
     * if they carry the very same ego entry from the registry, or both carry none. {@link
     * #objectValueReal} also tests it: a burning light is divided down as an expendable only when
     * it has no ego. {@link #launcherAmmoDamagePower(int)} tests it too: only ego ammunition takes
     * the launcher's assumed to-damage bonus.
     *
     * <p>{@link #isEgo} is the truth test on it. {@link #egoIsIgnored} asks the ego whether the
     * player has marked it ignorable under a category, which reads the marks held on the shared
     * registry entry, so they apply to every item of that ego. {@link #flagsKnown} folds an
     * easy-known ego's flags in and its suppressed flags out, and {@link #ignoreLevelOf} grades any
     * fully known ego item {@code IGNORE_ALL}.
     *
     * <p>{@link #wipe} sets it to {@code null}.
     *
     * <p>Field ego commented in full on 261002, pricing added on 261002, ammunition read added on
     * 261002, knowledge and ignore reads added on 261002, wipe reset added on 261002.
     */
    private EgoItem ego;
    /**
     * The artifact this item is, or {@code null} if it is not one. C's {@code obj->artifact}.
     *
     * <p>Any item with an artifact, on either side of the comparison, never stacks: {@link #similar}
     * rejects it before looking at anything else about its type.
     *
     * <p>{@link #ignoreLevelOf} grades a fully known artifact {@code IGNORE_MAX}, which no setting
     * reaches, and refuses to promote one to {@code IGNORE_ALL} merely for having been assessed.
     *
     * <p>{@link #wipe} sets it to {@code null}.
     *
     * <p>On the known half the same field records that the player has learned the item is an
     * artifact, which is what {@link #isKnownArtifact} reads and
     * {@link ObjectUtils#objIsKnownArtifact} compares against the real object's.
     *
     * <p>Field artifact commented in full on 261002, ignore read added on 261002, wipe reset added
     * on 261002, known-artifact reads added on 261008.
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
     * <p>The stack operations keep the two halves in step. {@link #objectSplit} aligns the known
     * count before copying and writes both counts afterwards, {@link #objectAbsorbMerge} brings the
     * surviving known effect up to date, and {@link #objectAbsorb} deletes the absorbed object's
     * known half along with it. {@link #copy} copies it only when asked, and {@link #nullKnown}
     * clears the link.
     *
     * <p>{@link #objectValue} prices a variable-power object from this half rather than from the
     * real one, so that the price never reveals a bonus the player has not learned. With no known
     * half that route is skipped.
     *
     * <p>{@link #isKnown} reports whether it is present. {@link #flagsKnown} intersects this item's
     * flags with the known half's flags, and {@link #ignoreLevelOf} reads the jewellery modifiers, the combat
     * bonuses and the notice flags from it, so that a figure the player has not learned cannot sway
     * an ignore decision.
     *
     * <p>{@link #wipe} sets it to {@code null} without touching the counterpart itself.
     *
     * <p>{@link #isKnownArtifact} and {@link ObjectUtils#objIsKnownArtifact} read its
     * {@link #artifact} to tell whether the player has identified this item as an artifact.
     *
     * <p>Field known commented in full on 261002, pricing added on 261002, knowledge reads added on
     * 261002, wipe reset added on 261002, {@code flagsKnown} description corrected on 261003,
     * known-artifact reads added on 261008.
     */
    private ItemObject known;

    /**
     * The grid this item lies on, or {@code null} when it has none. C's {@code obj->grid}, a
     * {@code struct loc}: "position on map, or (0, 0)".
     *
     * <p>{@code null} and the origin both mean "not on the floor", and {@link #getGrid()} reads
     * them alike: it answers {@link Loc#zero} for a {@code null} field, so no caller sees the
     * {@code null}. A grid at the origin makes {@link #objectAbsorb} skip excising a known object
     * from a pile. The test compares coordinates, as C's {@code loc_is_zero} does, and
     * {@link #copy} copies the {@link Loc} (leaving a {@code null} as {@code null}) so the copy can
     * move without moving the original.
     *
     * <p>The no-argument constructor and {@link #wipe} leave it {@code null}, which stands for C's
     * zeroed grid; {@link #setGrid} stores whatever it is given, {@code null} included.
     *
     * <p>Field location commented in full on 261002, wipe reset added on 261002, {@code Loc.zero}
     * read through {@link #getGrid()} noted on 261007.
     */
    private Loc location;

    /**
     * The item type value, copied from the kind. C's {@code obj->tval}, a {@code uint8_t}.
     *
     * <p>{@code TValue} is declared in {@code list-tvals.h} order, so its ordinal is C's tval
     * number; {@link #earlierObject} orders the pack by it. {@link #similar} reads it to decide
     * which stacking rules apply. The pricing code reads it through the {@link TValue} predicates
     * - {@link TValue#hasVariablePower()}, {@link TValue#canHaveFlavour()},
     * {@link TValue#canHaveCharges()}, {@link TValue#isLight()} and {@link TValue#isAmmo()} - to
     * pick a route in {@link #objectValue} and {@link #objectValueReal}, and switches on it in
     * {@link #objectValueBase}.
     *
     * <p>The power calculation reads it three more ways: {@link #flagsPower(int)} and
     * {@link #modifierPower(int)} pass it to the property's type multiplier, which is why the same
     * flag can be worth more on one kind of object than another, and {@link #jewelleryPower(int)}
     * asks whether it is a ring or an amulet.
     *
     * <p>The damage steps read it as well. {@link #damageDicePower()} and {@link #toDamagePower()}
     * ask whether it is a melee weapon or ammunition, {@link #launcherAmmoDamagePower(int)} takes
     * its archery row from it, and {@link #bowMultiplier()} treats only {@code TV_BOW} as a
     * launcher.
     *
     * <p>{@link #getIgnoreTypeOf} matches it against the quality table, {@link #hasStandardToH} asks
     * whether it is body armour, and {@link #ignoreLevelOf} asks whether it is jewellery, which is
     * graded by a rule of its own.
     *
     * <p>{@link #wieldSlot} switches on it to pick the slot type, falling back on the {@link TValue}
     * predicates for weapons, rings, lights and armour, and answers {@code -1} for a type that is
     * never worn. {@link #wipe} and the no-argument constructor set it to {@link TValue#TV_NONE},
     * C's tval 0, which is what C's zero-filled {@code object_new} holds. {@link #gettValue()} and
     * {@link #settValue} are its accessors.
     *
     * <p>Field tValue commented in full on 261002, pricing added on 261002, property pricing added
     * on 261002, damage pricing added on 261002, ignore reads added on 261002, slot read and wipe
     * reset added on 261002, constructor default added on 261002.
     */
    private TValue tValue;
    /**
     * The item's sub-type within its type. C's {@code obj->sval}, a {@code uint8_t}, copied from the
     * kind alongside {@link #tValue}.
     *
     * <p>Carried across by {@link #copy}. Nothing in the stacking code reads it directly, because
     * two items of the same {@link #kind} already share it.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field sValue commented in full on 261002, wipe reset added on 261002.
     */
    private int sValue;

    /**
     * The item's extra parameter. C's {@code obj->pval}, an {@code int16_t}, whose meaning depends
     * on the type: charges for a wand or staff, an amount for gold, remaining fuel for a light.
     *
     * <p>{@link #similar} caps the combined {@code pval} of charged items and gold at
     * {@code MAX_PVAL}, {@link #earlierObject} sorts lights by it, and {@link #distributeCharges}
     * divides it between stacks. {@link #objectAbsorbMerge} adds the absorbed stack's value to this
     * one's, capped at {@code MAX_PVAL}, for charged items and gold. {@link #objectValueReal}
     * prices the charges of a wand or staff from it, per item as {@code pValue * quantity / number}.
     *
     * <p>{@link #bowMultiplier()} reads it as a launcher's damage multiplier, and only for
     * {@code TV_BOW}.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field pValue commented in full on 261002, pricing added on 261002, bow multiplier added on
     * 261002, wipe reset added on 261002.
     */
    private int pValue;
    /**
     * The sentinel resistance level meaning "vulnerable and resistant at once", which the curse
     * merge uses while combining and then flattens to plain zero before the caller sees it - the
     * port of C's magic {@code -32768} in {@code apply_curse_attributes} ({@code obj-curse.c}).
     *
     * <p>Spelled as the minimum {@code short} because that is what the value is in C, where the
     * field it lives in is an {@code int16_t}. It is far from the real levels (-1 vulnerable, 0 none,
     * 1 resistant, 3 immune), and {@code applyCurseAttributes} tests for it before it tests for
     * "less than zero", as C does, so it is not mistaken for a plain vulnerability. An instance
     * field only by accident; every object holds the same constant.
     *
     * <p>Field VULN_AND_RES commented in full on 261002.
     */
    private final int VULN_AND_RES = Short.MIN_VALUE;

    /**
     * Number of damage dice. C's {@code obj->dd}, a {@code uint8_t}. One of the values
     * {@link #similar} requires to be identical before two weapons stack.
     *
     * <p>{@link #damageDicePower()} prices a melee weapon or a missile from it, as
     * {@code damageDice * (damageSides + 1) * DAMAGE_POWER / 4}.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field damageDice commented in full on 261002, damage pricing added on 261002, wipe reset
     * added on 261002.
     */
    private int damageDice;
    /**
     * Sides per damage die. C's {@code obj->ds}, a {@code uint8_t}. Compared with
     * {@link #damageDice} by {@link #similar}.
     *
     * <p>{@link #damageDicePower()} reads it as the side count plus one, which is twice the average
     * roll of a die.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field damageSides commented in full on 261002, damage pricing added on 261002, wipe reset
     * added on 261002.
     */
    private int damageSides;
    /**
     * Base damage, as a dice expression, parsed from the string the constructor is given.
     *
     * <p>Has no field counterpart in C's {@code struct object}, which keeps the rolled dice as the
     * two integers {@link #damageDice} and {@link #damageSides}. Nothing in this class reads it
     * after construction: {@link #copy} deep-copies it, so the copy shares no dice with the
     * original, and {@link #wipe} clears it.
     *
     * <p>Field baseDamage commented in full on 261002.
     */
    private Random baseDamage;
    /**
     * The weight of one of this item, in tenth-pounds, before its curses are applied. C's
     * {@code obj->weight}, an {@code int16_t}, which {@code obj-util.c} documents as "only the base
     * weight and does not include curses".
     *
     * <p>Not the figure to use for the burden an item puts on the player: {@link #objectWeightOne}
     * floors it at zero and then lets each active curse adjust it, and callers multiply that by
     * {@link #number}. {@link #copy} carries the base figure across unchanged.
     *
     * <p>The power calculation uses it as the <em>standard</em> weight in
     * {@link #nonStandardWeightPower(int)}, and {@link #applyCurseAttributes} overwrites it on a
     * scratch copy with the weight the curses give.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field weight commented in full on 261002, power added on 261002, wipe reset added on 261002.
     */
    private int weight;
    /**
     * Base armour class, before any to-armour-class bonus. C's {@code obj->ac}, an
     * {@code int16_t}. {@link #similar} requires it to be identical before wearables stack.
     *
     * <p>{@link #nonStandardWeightPower(int)} tests it against zero: an object with base armour has
     * had its weight priced already by {@link #acPower}. {@link #applyCurseAttributes} does not
     * change it, because C adds a curse's base armour and {@code curse.txt} cannot supply one.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field baseAC commented in full on 261002, power added on 261002, wipe reset added on 261002.
     */
    private int baseAC;
    /**
     * This item's own to-damage bonus, rolled at generation. C's {@code obj->to_d}. See
     * {@link #toAC} for why the instance holds a number and the kind holds dice.
     *
     * <p>{@link #toDamagePower()} prices it at half of {@code DAMAGE_POWER} a point, and again at
     * the full figure for an object that is not a weapon, missile or launcher.
     *
     * <p>{@link #applyCurseAttributes} adds each active curse's figure to it with the saturating
     * 16-bit add, on a scratch copy only.
     *
     * <p>{@link #isGood} weighs it against the kind's worst roll at four times the weight of
     * {@link #toAC}, so a weapon is judged chiefly on this bonus.
     *
     * <p>Field toDam coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815, damage pricing added on 261002, ignore read added on 261002,
     * curse note added on 261008.
     * {@link #wipe} sets it to zero, added on 261002.
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
     * <p>{@link #toHitPower(int)} prices it linearly, at one and a half power a point.
     * {@link #applyCurseAttributes} adds each active curse's figure to it with the saturating
     * 16-bit add, on a scratch copy only.
     *
     * <p>{@link #hasStandardToH} compares it with the kind's fixed figure for body armour and with
     * zero for everything else. {@link #isGood} weighs it against the kind's worst roll at twice the
     * weight of {@link #toAC}.
     *
     * <p>Field toHit coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815, power read added on 261002, knowledge and ignore reads added on
     * 261002, curse note added on 261008. {@link #wipe} sets it to zero, added on 261002.
     */
    private int toHit;
    /**
     * This item's own to-armour-class bonus — the rolled result, not the dice it came from. C's
     * {@code obj->to_a} ({@code object.h}), an {@code int16_t}.
     *
     * <p>The dice live one level up, on the kind's {@code toA}, because they belong to the
     * recipe rather than to any particular item: {@code object.txt} writes {@code armor:32:0} once
     * and every suit rolled from it gets its own figure. By the time an item exists this is a
     * settled number, so reading it is a plain field access and never a fresh roll.
     *
     * <p>{@link #applyCurseAttributes} adds each active curse's figure to this, and to
     * {@link #toDam} and {@link #toHit}, with the saturating 16-bit add, on a scratch copy only.
     *
     * <p>The power calculation reads it twice: {@link #toAcPower(int)} prices it in bands, and
     * {@link #acPower(int)} adds it to the base armour class when scaling that by weight.
     *
     * <p>{@link #isGood} weighs it against the kind's worst roll at the lowest weight of the three
     * bonuses, one.
     *
     * <p>Field toAC coded before 260815, retyped from {@code Random} to {@code int} on 260815.
     * Commented in full on 260815, power added on 261002, ignore read added on 261002.
     * {@link #wipe} sets it to zero, added on 261002.
     */
    private int toAC;
    /**
     * The item's object flags. C's {@code obj->flags}, a bitflag array.
     *
     * <p>{@link #similar} requires the whole set to be equal, so two items differing in a single
     * flag never stack. {@link #objectValueReal} reads {@code OF_BURNS_OUT} from it to recognise a
     * light that is consumed as it is used. {@link #nonStandardWeightPower(int)} merges it with the
     * active curses' flags to see whether the object is throwable, and
     * {@link #applyCurseAttributes} unions the curses' flags into it through {@link #setFlags}
     * (the live set, not the copy {@link #getFlags()} returns). {@link #flagsPower(int)} prices the
     * item's own flags only, from a copy, as C's {@code object_flags} is a plain copy of
     * {@code obj->flags}; the curses' flags are priced separately.
     *
     * <p>{@link #hasFlag} tests one flag, {@link #objectFlags} fills a caller's set from it, and
     * {@link #flagsKnown} starts from a copy and narrows it to what the player has learned.
     *
     * <p>{@link #wipe} replaces the set with a fresh empty one rather than clearing it, so a handle
     * from {@link #getObjectFlags()} taken earlier no longer reaches this item.
     *
     * <p>Field flags commented in full on 261002, pricing added on 261002, power added on 261002,
     * knowledge reads added on 261002, wipe reset added on 261002.
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
     * <p>A map that may omit a modifier, where C's array has a slot for every one and a slot that
     * was never set reads zero. Code comparing two objects must therefore read an absent entry as
     * zero, as {@link #getModifierValue(ObjectModifier)} does and as {@link #similar} does.
     *
     * <p>{@link #applyCurseAttributes} adds each active curse's modifiers into this map, saturating
     * at the 16-bit limits, and writes into the live map: {@link #getModifiers()} hands it back
     * shared, but an immutable empty one if it was never created. {@link #modifierPower(int)} reads
     * it for every modifier there is, treating an absent entry as zero.
     *
     * <p>The damage steps read three entries by name, again as zero when absent:
     * {@link #extraBlowsPower(int)}, {@link #extraShotsPower(int)} and
     * {@link #extraMightPower(PowerAndMult)} price blows, shots and might, and
     * {@link #damageDicePower()} treats any of the three above zero as a reason to credit a
     * non-weapon with a flat assumed damage.
     *
     * <p>{@link #modMessage(ObjectModifier)} reads the sign of one entry through
     * {@link #getModifierValue(ObjectModifier)}, so a {@code null} map or an absent entry reads as
     * zero and prints nothing, where C's array would hold a zero.
     *
     * <p>Comment corrected on 260816, when the field's type changed from the unparsed dice text it
     * had previously held. Field modifiers commented in full on 261002, power added on 261002,
     * damage steps added on 261002, message note added on 261004.
     *
     * <p>{@link #wipe} replaces the map with a fresh empty {@link LinkedHashMap}, the type the
     * constructors build, so the order it is walked in is the order entries were put in.
     * Wipe reset added on 261002.
     */
    private Map<ObjectModifier, Integer> modifiers;
    /**
     * Per-element relation info: resistance level and the hates/ignores flags for each element the
     * item has something to say about. C's {@code obj->el_info}.
     *
     * <p>A map holding only the elements recorded, where C keeps an entry for every element.
     * {@link #checkElementStacking} compares two of these for {@link #similar}, reading an element
     * that is absent from one item's map as C reads an untouched array slot: resistance level 0
     * with no flags.
     *
     * <p>{@link #applyCurseAttributes} merges the active curses' resistance levels into it, creating
     * an entry for an element the item did not mention but a curse does, and temporarily holds the
     * level {@link #VULN_AND_RES} while it works. {@link #copy} deep-copies each entry, so that merge
     * never reaches the original.
     *
     * <p>{@link #elementPower(int)} looks each element up here and treats a missing entry as an
     * element the item says nothing about.
     *
     * <p>{@link #wipe} replaces the map with a fresh empty {@link LinkedHashMap}, as it does for
     * {@link #modifiers}.
     *
     * <p>Field elInfo commented in full on 261002, power added on 261002, wipe reset added on 261002.
     */
    private Map<ElementEnum, ElementInfo> elInfo;
    /**
     * Brands on the item — C's {@code obj->brands}. A set, because membership is the whole of the
     * state; C indexes an array by registry position and stores a bare boolean.
     *
     * <p>{@link #applyCurseAttributes} does not merge brands, because curses cannot carry any.
     * {@link #freeBrands()} replaces the set on a scratch copy with an empty one once it has been
     * priced.
     *
     * <p>{@link #slayPower(int, boolean, int)} counts them and takes the best figure among them, and
     * {@link #damageDicePower()} treats a non-empty set as a reason to credit a non-weapon with a
     * flat assumed damage. {@link #getBrands()} answers an empty set while none exists.
     *
     * <p>{@link #wipe} replaces the set with a fresh empty one, as C frees the array first.
     *
     * <p>Field brands commented in full on 260817, power added on 261002, damage steps added on
     * 261002, wipe reset added on 261002.
     */
    private Set<Brand> brands;
    /**
     * Slays on the item — C's {@code obj->slays}. As {@link #brands}, including the part played by
     * {@link #applyCurseAttributes} and {@link #freeSlays()}.
     *
     * <p>{@link #slayPower(int, boolean, int)} splits them into slays and kills by multiplier, so a
     * slay of three or less counts as a slay and anything above counts as a kill.
     *
     * <p>{@link #wipe} replaces the set with a fresh empty one, as C frees the array first.
     *
     * <p>Field slays commented in full on 260817, power added on 261002, damage steps added on
     * 261002, wipe reset added on 261002.
     */
    private Set<Slay> slays;

    /**
     * Effects this item produces when used. C's {@code obj->effect}, a linked chain.
     *
     * <p>Whether the player knows the effect is a comparison between this list and the one on the
     * {@link #known} counterpart, made by {@link #effectIsKnown()}.
     *
     * <p>{@link #copy} shares the list with the original, as C's {@code object_copy} copies the
     * {@code struct effect *} pointer and not the chain behind it. Both constructors start with an
     * empty list, and the full constructor stores the list it is given without copying it.
     * {@link #wipe} replaces the list with a fresh empty one.
     *
     * <p>Field effect commented in full on 261002, wipe reset added on 261002, copy and constructor
     * note added on 261007.
     */
    private List<Effect> effect;
    /**
     * Message shown when the item's effect fires, or {@code null} if it has none. C's
     * {@code obj->effect_msg}, a {@code char *} that {@code struct object} holds as a shared
     * pointer.
     *
     * <p>A string is immutable, so {@link #copy} shares it safely where it must deep-copy the
     * mutable fields.
     *
     * <p>{@link #wipe} sets it to {@code null}.
     *
     * <p>Field effectMessage commented in full on 261002, wipe reset added on 261002.
     */
    private String effectMessage;
    /**
     * Activations available on this item. C's {@code obj->activation}, a pointer to a single
     * activation record in the shared registry, which the port holds as a list.
     *
     * <p>{@link #copy} shares the list with the original, as C shares the pointer: the activations
     * are registry templates, not per-item state.
     *
     * <p>Not always present: the no-argument constructor never assigns it, so an item built that way
     * holds {@code null}, which is the port's form of C's null pointer. {@link #wipe} assigns a
     * fresh empty list. The full constructor stores whatever it is given, so a {@code null} passed
     * there stays {@code null} and a list passed there is shared with the caller.
     * {@link #effectsPower(int)} treats null, an empty list and an empty first entry alike, as no
     * activation, and prices only the first entry.
     *
     * <p>Field activation commented in full on 261002, effects power added on 261002, constructor
     * note corrected on 261002.
     */
    private List<Activation> activation;
    /**
     * Recharge time, as a dice expression. C's {@code obj->time}, a {@code random_value}, used by
     * rods and activations. {@link #distributeCharges} takes its average to cap how much of a
     * rod's timeout a moved stack can hold.
     *
     * <p>{@link #numberCharging} evaluates it at its average to find how long one item takes to
     * recharge, and {@link #getTime} hands out a copy of it. It is never {@code null}: an item with
     * no recharge interval holds a zero {@link Random}, as C holds a zeroed {@code random_value},
     * which {@link #numberCharging} reads as nothing charging. The constructors, {@link #wipe} and
     * {@link #setTime} all land on {@link Random#Zero()} rather than {@code null}.
     *
     * <p>Field time commented in full on 261002, recharge reads added on 261002, wipe reset added
     * on 261002, rewritten for the never-null field on 261003.
     */
    private Random time;
    /**
     * Turns until the item can be used again (0 = ready). C's {@code obj->timeout}, an
     * {@code int16_t}.
     *
     * <p>{@link #similar} will not stack a recharging wearable (other than a light, whose timeout
     * is its fuel and must match), and {@link #distributeCharges} shares a rod's remaining timeout
     * out between stacks.
     *
     * <p>A stack of rods pools one timeout rather than keeping one per rod. {@link #numberCharging}
     * works out how many rods that pool still covers, and {@link #rechargeTimeout} takes that many
     * turns off it each game turn, never past zero.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field timeout commented in full on 261002, recharge reads added on 261002, wipe reset
     * added on 261002.
     */
    private int timeout;

    /**
     * Quantity in this stack. C's {@code obj->number}, a {@code uint8_t}.
     *
     * <p>{@link #distributeCharges} divides by it, so it must not be zero when that is called.
     * {@link #objectValueReal} divides by it as well, to share a wand's or staff's charges out per
     * item, and throws {@link ArithmeticException} on zero where C would fault.
     *
     * <p>{@link #numberCharging} caps the count of items still charging at it, so a stack can never
     * report more rods recharging than it holds.
     *
     * <p>{@link #printCustomMessage} reads it for the {@code {s}} and {@code {is}} tags: one item
     * gives {@code s} and {@code is}, a pile gives nothing and {@code are}. A stack of zero is the
     * odd case: {@code {s}} prints nothing, as for a pile, but {@code {is}} prints {@code is},
     * because only a count above one gives {@code are}, as in C. {@link #wipe} sets it to zero.
     *
     * <p>Field number commented in full on 261002, pricing added on 261002, recharge read added on
     * 261002, message tags and wipe reset added on 261002.
     */
    private int number;
    /**
     * The player's notice flags for this item (worn/assessed/ignore/imagined). C's
     * {@code obj->notice}, a bitflag: the "attention paid to the object".
     *
     * <p>A mutable set, so {@link #copy} builds a new one rather than sharing it - noticing
     * something on the copy must not mark the original.
     *
     * <p>{@link #ignoreLevelOf} reads {@code OBJ_NOTICE_ASSESSED} from the {@link #known} half's set
     * to tell an item the player has examined closely from one merely sensed across a room.
     *
     * <p>{@link #wipe} replaces the set with a fresh empty one.
     *
     * <p>Changes go through {@link #orNotice} and {@link #setNoticeOn} to raise a flag and
     * {@link #setNoticeOff} to lower one; {@link #getNotice()} only hands out a copy. A single flag
     * is tested with {@link #getNoticeHas}, which reads this set directly.
     *
     * <p>Field notice commented in full on 261002, ignore read added on 261002, wipe reset added on
     * 261002, mutators note added on 261003, single-flag read added on 261003.
     */
    private Flag<ObjectNotice> notice;

    /**
     * Index of the monster holding this item, or 0 if not held. C's {@code obj->held_m_idx}, an
     * {@code int16_t}: "monster holding us (if any)".
     *
     * <p>Carried across by {@link #copy}, so a copy of a carried item is still marked as carried.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field heldMIndex commented in full on 261002, wipe reset added on 261002.
     */
    private int heldMIndex;
    /**
     * Index of the monster mimicking this item, or 0 if none. C's {@code obj->mimicking_m_idx}, an
     * {@code int16_t}. An item that is a monster's disguise never stacks, so {@link #similar}
     * rejects a non-zero value on either side.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field mimickingMIndex commented in full on 261002, wipe reset added on 261002.
     */
    private int mimickingMIndex;

    /**
     * Where this item came from (for the description history line). C's {@code obj->origin}, a
     * {@code uint8_t}. {@link #originCombine} sets it to {@code ORIGIN_MIXED} when two stacks with
     * different origins are merged.
     *
     * <p>{@link #wipe} sets it to {@code ORIGIN_NONE}, the value C's zeroed byte lands on.
     *
     * <p>Field origin commented in full on 261002, wipe reset added on 261002.
     */
    private ObjectOriginEnum origin;
    /**
     * The depth at which the item originated. C's {@code obj->origin_depth}, a {@code uint8_t}.
     * {@link #originCombine} treats a difference in depth, with the same origin, as a mixed origin.
     *
     * <p>{@link #wipe} sets it to zero.
     *
     * <p>Field originDepth commented in full on 261002, wipe reset added on 261002.
     */
    private int originDepth;
    /**
     * Curses on the item, each mapped to its per-object {@link CurseData} — the power it has here
     * and the countdown to its next effect. C's {@code obj->curses}.
     *
     * <p>A map holding only the curses the object actually carries, where C keeps an array with a
     * slot for every curse in the game and reads a power of zero as "not cursed with this".
     * Absence is the port's way of saying the same thing, but an entry at power zero can still be
     * stored: {@link #addCurse(Curse, int, int)} and {@link #addCurse(Curse, CurseData)} keep
     * whatever they are handed, where {@link #setCursePower} never stores one and
     * {@link ObjectUtils#copyCurses} skips a zero-power source entry. An empty map stands for C's
     * null array, and a map of only zero-power entries for C's allocated all-zero array, so {@link #cursesAreEqual} reads
     * a zero-power entry and an absent one as equal only when both items carry a non-empty map;
     * against an empty map, any entry at all is a difference.
     *
     * <p>Both constructors and {@link #wipe} build a fresh map; the full constructor copies the
     * entries of the map it is given into it and reads a {@code null} argument as empty. Nothing
     * assigns {@code null} except {@code copy}, from a source that is itself {@code null}, so
     * the field is not {@code null} in practice. The accessors still absorb a {@code null} rather
     * than pass it on — {@link #getCurses()} reports an empty map and the editing mutators create
     * the map on demand — and that guard is what C's {@code NULL} array would have needed.
     * {@link #setCurses} and {@link #clearAndPutCurses} replace the field with a copy of the map
     * they are handed, and leave it untouched when handed {@code null}, as C's {@code copy_curses}
     * returns early on a {@code NULL} source.
     *
     * <p>The map is a {@link TreeMap} ordered by {@link #CURSE_ORDER}, so it walks in ascending curse
     * index, as C's loop over {@code obj->curses[i]} does, regardless of the order the curses were
     * added in. Every path that builds a map for it goes through {@link #cursesFactory()}, which
     * is what keeps that true; the one exception is a copy of an item whose map was never built,
     * which stays {@code null} like its source. A copy rebuilds each {@link CurseData} as well, so
     * the copy's timeouts tick independently of the original's, as C's {@code object_copy}, which
     * duplicates the whole array ({@code obj-pile.c}), leaves them. The {@link Curse} keys are
     * shared, being the registry's definitions. {@link #cursePower(int, boolean, String)} reads each entry's
     * power to decide whether a curse is active and how much to discount it, and
     * {@link #freeCurses()} replaces the map on a scratch copy once the curses have been merged in.
     *
     * <p>{@link #getCurses()} is the only way to read the field from outside, and it answers an
     * unmodifiable view of it.
     *
     * <p>Field curses retyped from {@code Map<Curse.CurseEntry, Boolean>} on 260817, commented in
     * full on 260817, comment corrected on 261002, power added on 261002, constructor and setter
     * note added on 261002, comparison note corrected on 261002, power-zero note corrected on 261003,
     * ordering note rewritten for the {@link TreeMap} on 261003, copy note added on 261003, null
     * argument note added on 261007.
     *
     * <p>{@link #wipe} and {@link #initCurses} each replace the map with a fresh empty one from
     * {@link #cursesFactory()}, discarding every curse the item carried. Wipe reset and initialiser
     * added on 261002.
     */
    private TreeMap<Curse, CurseData> curses;

    /**
     * Builds a blank item, the port of C's {@code object_new}, which is {@code mem_zalloc} of one
     * {@code struct object} and so leaves every member at zero.
     *
     * <p>Where C's zero is a value, the port lands on it: {@link #origin} is {@code ORIGIN_NONE},
     * {@link #tValue} is {@link TValue#TV_NONE} (C's tval 0), and the numeric fields are zero.
     * Where C's zero is a collection, the port builds an empty one rather than leaving {@code null}:
     * {@link #flags} and {@link #notice} are empty sets, {@link #modifiers} and {@link #elInfo} are
     * empty {@link LinkedHashMap}s, {@link #curses} is an empty {@link TreeMap} from
     * {@link #cursesFactory()}, {@link #brands} and {@link #slays} are empty sets, and
     * {@link #effect} is an empty list. The first two maps are insertion-ordered so the order they
     * are walked in does not depend on how their enum keys hash; the curse map walks in ascending
     * curse index, as C's array does.
     *
     * <p>{@link #time} is a zero {@link Random} (C's four zero dice) and is never {@code null}.
     *
     * <p>The rest stay {@code null}: {@link #kind}, {@link #ego}, {@link #artifact}, {@link #known},
     * {@link #location}, {@link #baseDamage}, {@link #effectMessage}, {@link #activation},
     * {@link #originRace} and {@link #note}. Unlike an item that {@link #wipe} has blanked, this one
     * has no activation list. {@link #location} being {@code null} stands for C's grid of (0, 0),
     * which is what {@link #getGrid()} answers for it ({@link Loc#zero}).
     *
     * <p>{@link #player} is set from {@link GameState#getPlayer()}, so an item built before a
     * character exists holds {@code null} there. {@code PlayerBirth} builds the known counterpart
     * of a starting item this way and fills it in afterwards, and {@link #copy} builds its result
     * the same way, as C does with the {@code object_new} it makes for {@code obj->known}.
     *
     * <p>Constructor ItemObject() coded before 260904, commented in full on 261002, TV_NONE default
     * added on 261002, curse map and time notes corrected on 261007.
     */
    public ItemObject() {
        player = GameState.getPlayer();
        origin = ObjectOriginEnum.ORIGIN_NONE;
        owningPile = null;
        notice = new Flag<>(ObjectNotice.class);
        flags = new Flag<>(ObjectFlag.class);
        modifiers = new LinkedHashMap<>();
        curses = cursesFactory();
        elInfo = new LinkedHashMap<>();
        brands = new HashSet<>();
        slays = new HashSet<>();
        effect = new ArrayList<>();
        tValue = TValue.TV_NONE;
        time = Random.Zero();
    }

    /**
     * The player's inscription on the item, or {@code null} if it has none. C's {@code obj->note},
     * a quark - an index into a table of interned strings.
     *
     * <p>The port keeps the string itself. C compares two quarks with {@code ==}, which is
     * equality of the text because the table never holds a string twice, so the port compares with
     * {@code equals}; {@link #objectStackable} does so. {@link #checkForInscription} searches it,
     * {@link #objectAbsorbMerge} takes the absorbed stack's note when it has one, and
     * {@link #objectSplit} gives the new stack the same note as the old.
     *
     * <p>{@link #wipe} sets it to {@code null}, C's quark 0.
     *
     * <p>Field note commented in full on 261002, wipe reset added on 261002.
     */
    private String note;
    /**
     * The monster race that dropped the item, or {@code null} if none did. C's
     * {@code obj->origin_race}, a pointer. {@link #originCombine} compares it by identity and
     * prefers to keep the record of a unique.
     *
     * <p>{@link #wipe} sets it to {@code null}.
     *
     * <p>Field originRace commented in full on 261002, wipe reset added on 261002.
     */
    private MonsterRace originRace = null;

    /**
     * The {@link Pile} this item currently belongs to, or {@code null} if it belongs to none.
     *
     * <p>Has no single field counterpart in C: {@code struct object} threads a pile together
     * itself, with intrusive {@code prev}/{@code next} pointers ({@code object.h}) linking one
     * item directly to its neighbours, so the pile <em>is</em> the chain and nothing needs to
     * point back to a container. The port keeps piles as a separate {@link Pile} collection
     * instead, so an item needs this back-reference to answer which pile, if any, currently holds
     * it - the way {@link #getOwningPile()} is used to keep a {@link Pile}'s own bookkeeping in
     * step with the items it is given.
     *
     * <p>{@link #wipe} sets it to {@code null}, the port's form of C's {@code memset} zeroing
     * {@code prev} and {@code next}. The pile itself is not told, so a wiped item that was in one
     * is left for the caller to remove from it first.
     *
     * <p>Field owningPile coded before 260904, commented in full on 260928, wipe reset added on
     * 261002.
     */
    private Pile owningPile;

    /**
     * Builds an item with every field supplied, assigning each argument to the member of the same
     * name. C has no equivalent: it makes a blank object with {@code object_new} and then fills
     * members in one at a time. No production code calls this form yet; the tests use it to build
     * an item whose every value they hold.
     *
     * <p><b>Only the curse map is copied.</b> {@code flags}, {@code modifiers}, {@code elInfo},
     * {@code brands}, {@code slays}, {@code effect}, {@code activation}, {@code notice},
     * {@code location}, {@code known} and the rest are stored by reference, so the new item and the
     * caller share them and a change through one shows in the other. Callers that need an
     * independent item build fresh collections to pass in. A {@code null} modifier map, element
     * info map, brand set or slay set stays {@code null}, which the getters for those absorb;
     * {@link #getEffect()} is the exception, and hands a {@code null} straight back.
     *
     * <p>{@code curses} is the one collection that is copied: its entries are put into a new
     * {@link #cursesFactory()} map, so adding or removing a key through the caller's map does not
     * reach the item, and a {@code null} argument gives an empty map. The {@link CurseData}
     * instances are still shared with the caller's map, so a template's data passed here would be
     * ticked by the item; C's {@code copy_curses} always makes fresh data and rolls a timeout.
     * {@code flags} and {@code notice} are never left {@code null}: a {@code null} argument for
     * either is replaced with an empty set.
     *
     * <p>Three arguments are parsed. {@code pValue} arrives as text: the empty string is zero,
     * anything else goes through {@link Integer#parseInt}, so a {@code null} or a non-number throws.
     * {@code baseDamage} and {@code time} go through {@link Random#parseStr}, which answers
     * {@code null} for the empty string and rejects a {@code null} argument. A {@code time} of
     * {@code ""} therefore stores a zero {@link Random} in {@link #time}, which
     * {@link #numberCharging} reads as nothing charging.
     *
     * <p>{@link #player} is taken from {@link GameState#getPlayer()} at the end, and
     * {@link #owningPile} starts {@code null}: a new item belongs to no pile.
     *
     * <p>Constructor ItemObject(...) coded before 260904, commented in full on 261002; the claim
     * that it copies the curse map was removed on 261002, and the by-reference note rewritten on
     * 261007 to match the code, which copies that map, and to cover {@code flags} and
     * {@code notice}.
     *
     * @param kind            object kind
     * @param ego             ego type, if any
     * @param artifact        artifact, if any
     * @param known           known/identified view
     * @param location        floor location; {@code null} reads back from {@link #getGrid()} as
     *                        {@link Loc#zero}
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
                      Map<Curse, CurseData> curses,
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
        this.flags = Objects.requireNonNullElseGet(flags, () -> new Flag<>(ObjectFlag.class));
        this.modifiers = modifiers;
        this.elInfo = elInfo;
        this.brands = brands;
        this.slays = slays;
        this.curses = cursesFactory();
        if (curses != null) this.curses.putAll(curses);
        this.effect = effect;
        this.effectMessage = effectMessage;
        this.activation = activation;
        this.time = Random.parseStr(time);
        if (this.time == null)
            this.time = Random.Zero();
        this.timeout = timeout;
        this.number = number;
        this.notice = Objects.requireNonNullElseGet(notice, () -> new Flag<>(ObjectNotice.class));
        this.heldMIndex = heldMIndex;
        this.mimickingMIndex = mimickingMIndex;
        this.origin = origin;
        this.originDepth = originDepth;
        this.originRace = originRace;
        this.note = note;
        player = GameState.getPlayer();
        owningPile = null;
    }

    /**
     * Builds the empty map every curse field starts from and is replaced with, ordered by
     * {@link #CURSE_ORDER}.
     *
     * <p>The single place the field's ordering is decided, so a path that assigned a plain
     * {@link java.util.HashMap} or {@link LinkedHashMap} to {@link #curses} would change what
     * {@link #getCurses()} walks in. The field's type is {@link TreeMap}, so the compiler turns
     * that into an error rather than a quiet change of order.
     *
     * <p>Function cursesFactory commented in full on 261003.
     *
     * @return a new, empty curse map in C's index order
     */
    private TreeMap<Curse, CurseData> cursesFactory() {
        TreeMap<Curse, CurseData> result = new TreeMap<>(CURSE_ORDER);
        return result;
    }

    /**
     * Decides whether one object should be listed before another - the port of C's
     * {@code earlier_object} ({@code player-calcs.c}).
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
     * <p>This is an instance method only so that it can set {@link #player}: the usable-ammunition
     * test needs the player's {@code ammo_tval}, which C reads from its {@code player} global at
     * the moment of the call, so the current player is fetched from {@link GameState#getPlayer()}
     * on every call rather than relying on the one captured at construction. The receiver takes no
     * part in the ordering itself - only {@code origObj} and {@code newObj} are compared - and
     * callers conventionally pass the incumbent as both receiver and {@code origObj}. A side effect
     * is that every call overwrites the receiver's {@code player}.
     *
     * <p>Object type is compared by {@code TValue} ordinal, which equals C's {@code tval} value
     * because {@code TValue} is declared in {@code list-tvals.h} order. The flavour tests go
     * through {@link #flavourIsAware()} and {@link #objectFlavourIsAware()}, both of which read the
     * kind's awareness as C's {@code object_flavor_is_aware} does, and the value tests use
     * {@code objectValue(1)}, C's {@code object_value(obj, 1)}.
     *
     * <p>Function earlierObject commented in full on 261002.
     *
     * @param origObj the object currently holding the position, or {@code null}
     * @param newObj  the candidate, or {@code null}
     * @param store   {@code true} when ordering a shop's stock rather than the player's pack
     * @return {@code true} if {@code newObj} should come before {@code origObj}
     */
    public boolean earlierObject(ItemObject origObj, ItemObject newObj, boolean store) {
        // Are both of the objects real
        if (newObj == null) return false;
        if (origObj == null) return true;

        if (!store) {
            // readable books always come first
            if (origObj.canBrowse() && !newObj.canBrowse()) return false;
            if (!origObj.canBrowse() && newObj.canBrowse()) return true;
        }

        player = GameState.getPlayer();
        
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
     * Returns the grid this object lies on, the port of reading C's {@code obj->grid}.
     *
     * <p>Never {@code null}. An item that has never been placed, has been wiped, or was given a
     * {@code null} by {@link #setGrid} answers {@link Loc#zero}, the (0, 0) grid C holds for an
     * item that is not on the map. The {@link #location} field itself stays {@code null} in those
     * cases. A caller that must tell "on the floor" from "not" tests {@code isZero()} on the
     * result, as {@link #objectAbsorb} does.
     *
     * <p>Function getGrid coded before 260904, commented in full on 261002, rewritten on 261007
     * for the never-null answer.
     *
     * @return the grid this object occupies, or {@link Loc#zero} if it is not on the map
     */
    public Loc getGrid() {
        return location == null ? Loc.zero : location;
    }

    /**
     * Sets the grid this object lies on, the port of C's {@code obj->grid = grid}.
     *
     * <p>Stores the {@link Loc} given. {@link Loc} is immutable, so sharing it with the caller is
     * safe where C copies its {@code struct loc} by value. {@code null} and the origin both mean
     * "not on the floor", the reading {@link #objectAbsorb} gives them; see {@link #location}.
     * A {@code null} is stored as {@code null}, not as {@link Loc#zero}, and {@link #getGrid()}
     * then answers {@link Loc#zero} for it.
     *
     * <p>Function setGrid coded before 260904, commented in full on 261002, {@code Loc.zero} read
     * noted on 261007.
     *
     * @param grid the map location, or {@code null} if the object is not on the floor; read back
     *             as {@link Loc#zero}
     */
    public void setGrid(Loc grid) {
        location = grid;
    }

    /**
     * Raises a notice flag on this object, recording something the player has learned or noticed
     * about it. The port of C's {@code obj->notice |= flag}.
     *
     * <p>Sets the one flag and leaves the rest alone. {@link #getNotice()} hands out a copy, so this
     * is the way to change the item's notice flags.
     *
     * <p>Function orNotice coded before 260904, commented in full on 261002.
     *
     * @param notice the {@link ObjectNotice} flag to set
     */
    public void orNotice(ObjectNotice notice) {
        if (this.notice == null)
            this.notice = new Flag<>(ObjectNotice.class);
        this.notice.on(notice);
    }

    /**
     * Reports whether the player knows this object to be an artifact, the port of
     * {@code object_is_known_artifact} in {@code obj-knowledge.c}.
     *
     * <p>Reads the known half only: {@code true} when {@link #known} is present and itself carries
     * an artifact. It never looks at this object's own artifact, so it answers {@code false} for an
     * item with no known half and {@code false} when the known half's artifact is {@code null}.
     * Compare {@link ObjectUtils#objIsKnownArtifact}, the port of {@code obj_is_known_artifact} in
     * {@code obj-util.c}, which also requires the real object to be an artifact.
     *
     * <p>C sets the known half's artifact in {@code object_touch}. This class has no artifact setter
     * yet, so a known half answers {@code true} here only if it was built with an artifact through
     * the full constructor.
     *
     * <p>Function isKnownArtifact coded on 261008 / commented in full on 261008.
     *
     * @return {@code true} if this object has a known half that records an artifact
     */
    public boolean isKnownArtifact() {
        if (getKnown() == null)
            return false;

        return getKnown().isArtifact();
    }
    
    /**
     * Reports whether this object is an artifact, the port of testing C's {@code obj->artifact}
     * against {@code NULL}.
     *
     * <p>This class has no getter for the artifact itself, so this test is the only view of it from
     * outside.
     *
     * <p>Function isArtifact coded before 260904, commented in full on 261002.
     *
     * @return {@code true} if this object is an artifact (has an associated artifact definition)
     */
    public boolean isArtifact() {
        return artifact != null;
    }

    /**
     * Returns the kind this object is an instance of, the port of reading C's {@code obj->kind}.
     *
     * <p>{@code null} for the bare object hanging off a curse definition and for a wiped item. See
     * {@link #setKind} for why that is a marker and not a missing value.
     *
     * <p>Function getKind coded before 260904, commented in full on 261002.
     *
     * @return the object kind (base type) this object is an instance of
     */
    public ObjectKind getKind() {
        return kind;
    }

    /**
     * Sets the kind this item is an instance of — C's {@code obj->kind}.
     *
     * <p>A null kind is not a missing value but a marker: the bearer-less item hanging off a curse
     * definition has one, and {@code knowObject} stops early on exactly that test.
     *
     * <p>Sets only the kind. The type, sub-type, weight, dice and the other values C copies from it
     * are separate fields with their own setters, which the item-preparation code calls in turn.
     *
     * <p>Function setKind coded before 260904, commented in full on 261002.
     *
     * @param kind the kind to set
     */
    public void setKind(ObjectKind kind) {
        this.kind = kind;
    }

    /**
     * Sets the index of the monster currently holding this object, the port of C's
     * {@code obj->held_m_idx = idx}.
     *
     * <p>Zero means no monster holds it. The index is stored as given; nothing checks that a
     * monster with that index exists.
     *
     * <p>Function setHeldMIndex coded before 260904, commented in full on 261002.
     *
     * @param heldMIndex the holding monster's index (0 if not held by a monster)
     */
    public void setHeldMIndex(int heldMIndex) {
        this.heldMIndex = heldMIndex;
    }

    /**
     * Tests whether one item like this one can be stacked with one item like {@code itm2}, ignoring
     * inscriptions - the port of C's {@code object_similar} ({@code obj-pile.c}). {@code
     * object_stackable} adds the inscription check on top of this.
     *
     * <p>The tests run in C's order and the first to fail answers {@code false}: equipped items,
     * mimics, unknown kinds (only in {@code OSTACK_LIST} mode), an item with itself, differing
     * kind, flags or element info, and artifacts. The rest depends on the item type:
     * <ul>
     *   <li>Chests never stack.</li>
     *   <li>Food, potions, scrolls and rods always stack, as the kinds are identical.</li>
     *   <li>Wands, staves and gold stack unless the combined {@code pval} would exceed
     *       {@code MAX_PVAL}.</li>
     *   <li>Weapons, armour, jewellery and lights must also match in armour class, dice, to-hit,
     *       to-damage, to-armour-class, every modifier, ego, curses and timeout. A wearable that is
     *       recharging never stacks unless it is a light, and a light needs the same fuel.
     *       Finally, in {@code OSTACK_LIST} mode an item whose runes and effect are fully known will
     *       not stack with one that is not, so the object list does not merge identified items with
     *       unidentified ones.</li>
     *   <li>Anything else is similar.</li>
     * </ul>
     *
     * <p>The current player is fetched from {@link GameState#getPlayer()} on each call, standing in
     * for C's {@code player} global, so that the equipped test never sees a stale or missing
     * player; this overwrites the receiver's {@code player}.
     *
     * <p>Where the port is deliberately not a transliteration:
     * <ul>
     *   <li><b>Modifiers</b> are held in a map that may omit a modifier, where C compares every slot
     *       of a fixed array. The loop over every modifier therefore treats an absent entry as zero
     *       - one side absent and the other zero stacks - and compares by value when both are
     *       present.</li>
     *   <li><b>Element info</b> is held in a map that may omit an element, where C compares every
     *       slot of a fixed array. {@link #checkElementStacking} reads an absent entry as resistance
     *       level 0 with no flags, so one side absent and the other at that default stacks.</li>
     *   <li><b>Curses</b> are checked by {@link #cursesAreEqual}.</li>
     *   <li>The explicit {@code tValue} equality test has no C counterpart; the kind test already
     *       implies it.</li>
     * </ul>
     *
     * <p>Like C, which dereferences {@code obj->known}, this throws a {@code NullPointerException}
     * in {@code OSTACK_LIST} mode if either item has no known counterpart. The modifier loop also
     * assumes both modifier maps exist.
     *
     * <p>Function similar coded before 260822, commented in full on 261002, element note
     * rewritten on 261003.
     *
     * @param itm2 the other object to compare against
     * @param mode the {@link ObjectStackEnum} flags selecting which stacking rules apply
     * @return {@code true} if the two objects may occupy the same stack
     */
    @CheckReturnValue
    public boolean similar(@NotNull ItemObject itm2, @NotNull Flag<ObjectStackEnum> mode) {
        player = GameState.getPlayer();
        
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
            for (ObjectModifier om : ObjectModifier.values()) {
                if (om == ObjectModifier.OM_NONE || om == ObjectModifier.OM_MAX)
                    continue;

                if (this.modifiers.containsKey(om) && itm2.modifiers.containsKey(om)) {
                    if (this.modifiers.get(om).equals(itm2.modifiers.get(om))) continue;

                    return false;
                } else if ((this.modifiers.containsKey(om) && !itm2.modifiers.containsKey(om))
                        || (!this.modifiers.containsKey(om) && itm2.modifiers.containsKey(om))) {
                    if (this.modifiers.containsKey(om) && this.modifiers.get(om) == 0) continue;
                    if (itm2.modifiers.containsKey(om) && itm2.modifiers.get(om) == 0) continue;

                    return false;
                }
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
     * Compares two objects' element info, reporting whether they agree on every element. Extracted
     * from {@link #similar} to carry the element half of C's {@code object_similar}
     * ({@code obj-pile.c}), which rejects a stack when two objects differ in either their
     * resistance level or their {@code EL_INFO_HATES}/{@code EL_INFO_IGNORE} flags for any element.
     * The {@code ELEM_NONE} and {@code ELEM_MAX} sentinels are skipped.
     *
     * <p><b>Why it walks the enum, not either map.</b> C compares full arrays indexed by element,
     * so a slot nothing has touched reads as resistance level 0 with no flags. Here the info is a
     * map holding only the elements an object carries, so the loop runs over every
     * {@link ElementEnum} value and an element missing from either map is given those same
     * defaults. A missing entry therefore equals an explicit level-0, no-flags entry, as in C, and
     * the comparison is symmetric: {@code similar} calls it once, with either argument order.
     *
     * <p>Only the hates and ignores bits are compared, as C masks the flags with
     * {@code EL_INFO_HATES | EL_INFO_IGNORE}; any other element flag is ignored.
     *
     * <p>Reads through {@link #getElInfo()} rather than the field so that an object built by the
     * no-argument constructor, whose map is still null, compares as carrying no element info rather
     * than throwing.
     *
     * <p>Function checkElementStacking coded on 260817, commented in full on 261003.
     *
     * @param itm1 the first object to compare
     * @param itm2 the second object to compare
     * @return {@code true} if the two agree on resistance level, hates and ignores for every element
     */
    private boolean checkElementStacking(ItemObject itm1, ItemObject itm2) {
        for (ElementEnum elem : ElementEnum.values()) {
            if (elem == ElementEnum.ELEM_MAX || elem == ElementEnum.ELEM_NONE) continue;

            Map<ElementEnum, ElementInfo> info1 = itm1.getElInfo();
            Map<ElementEnum, ElementInfo> info2 = itm2.getElInfo();

            int res1;
            int res2;
            boolean hates1;
            boolean hates2;
            boolean ignores1;
            boolean ignores2;

            if (info1.containsKey(elem)) {
                res1 = info1.get(elem).getResLevel();
                hates1 = info1.get(elem).has(ElementInfoEnum.EL_INFO_HATES);
                ignores1 = info1.get(elem).has(ElementInfoEnum.EL_INFO_IGNORE);
            } else {
                res1 = 0;
                hates1 = false;
                ignores1 = false;
            }

            if (info2.containsKey(elem)) {
                res2 = info2.get(elem).getResLevel();
                hates2 = info2.get(elem).has(ElementInfoEnum.EL_INFO_HATES);
                ignores2 = info2.get(elem).has(ElementInfoEnum.EL_INFO_IGNORE);
            } else {
                res2 = 0;
                hates2 = false;
                ignores2 = false;
            }

            if (res1 != res2 || hates1 != hates2 || ignores1 != ignores2) return false;

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
     * <p>Its role in {@link PlayerKnowledge#equipLearnFlag} is the
     * interesting one, and it is a negative: an item that is <em>not</em> yet fully known gets a
     * flag switched on in its known set to record that the flag was ruled out. Once the item is
     * fully known there is nothing left to rule out, so the bookkeeping stops.
     *
     * <p>The two checks are {@link #runesKnown()}, which answers {@code false} for an item with no
     * known counterpart, and {@link #effectIsKnown()}. Taking them in that order matters in C
     * as well: {@code object_effect_is_known} dereferences {@code obj->known}, and it is only safe
     * because the rune check has already rejected an item without one.
     *
     * <p>Function isFullyKnown coded before 260815 as the private {@code fullyKnown}, made public on
     * 260815 when a second copy of it was folded back in. Commented in full on 261002.
     *
     * @return true if the player has full knowledge of this object
     */
    public boolean isFullyKnown() {
        if (!runesKnown()) return false;

        return effectIsKnown();
    }

    /**
     * Checks whether two objects have the exact same curses - the port of C's {@code
     * curses_are_equal} ({@code obj-curse.c}).
     *
     * <p>C begins by comparing the curse arrays themselves: both null is equal, and exactly one
     * null is not, even if the other array holds only zeros. The port keeps maps rather than
     * arrays, with an empty map standing for C's null array, so exactly one empty map answers
     * {@code false} before any power is read. A map holding only zero-power entries is not empty,
     * and so stands for C's allocated all-zero array: against an item with no curses it does not
     * match.
     *
     * <p>Otherwise every curse in {@code ObjectRegistry.getCurses()} is visited and only the
     * <em>power</em> is compared; the countdown to the curse's next effect is ignored, as in C. In
     * this walk a curse an object does not carry reads as power zero, so once both objects carry
     * curses, a curse at power zero on one matches the curse being absent from the other, and a
     * curse absent from both matches. Both present means the powers must be equal. A curse the
     * registry does not hold is never visited, so it is only ever seen by the empty-map check.
     *
     * <p>Reads through {@link #getCurses()}, so an object whose map has never been created counts
     * as having no curses. Used by {@link #similar} and {@link #runesKnown()}, the latter comparing
     * an object with its own known counterpart.
     *
     * <p>Function cursesAreEqual coded before 261002, commented in full on 261002, empty-map check
     * added and comment updated on 261002.
     *
     * @param itm2 the object to compare with this object
     * @return {@code true} if the two objects carry the same curses at the same powers
     */
    @CheckReturnValue
    @Contract(pure = true)
    private boolean cursesAreEqual(@NotNull ItemObject itm2) {
        if ((this.getCurses().isEmpty() && !itm2.getCurses().isEmpty())
                || (!this.getCurses().isEmpty() && itm2.getCurses().isEmpty()))
            return false;
        for (Curse curse : ObjectRegistry.getCurses()) {
            if (this.getCurses().containsKey(curse) && itm2.getCurses().containsKey(curse)) {
                if (this.getCurses().get(curse).getPower() != itm2.getCurses().get(curse).getPower())
                    return false;
            } else if (!this.getCurses().containsKey(curse) && !itm2.getCurses().containsKey(curse)) {
                // Do nothing
            } else {
                if (this.getCurses().containsKey(curse) && this.getCurses().get(curse).getPower() != 0)
                    return false;
                if (itm2.getCurses().containsKey(curse) && itm2.getCurses().get(curse).getPower() != 0)
                    return false;
            }
        }

        return true;
    }

    /**
     * Checks whether all the runes on this object are known to the player - the port of C's
     * {@code object_runes_known} ({@code obj-knowledge.c}).
     *
     * <p>An object with no known counterpart answers {@code false}. Otherwise its curses must match
     * its known counterpart's exactly, by {@link #cursesAreEqual}, and then the answer is that of
     * {@code PlayerKnowledge.nonCurseRunesKnown}, the port of C's
     * {@code object_non_curse_runes_known}, which covers every other rune.
     *
     * <p>Called by {@link #isFullyKnown()}.
     *
     * <p>Function runesKnown coded before 261002, commented in full on 261002.
     *
     * @return {@code true} if every rune on this object, curses included, is known
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean runesKnown() {
        if (known == null) return false;

        if (!cursesAreEqual(known)) return false;

        return PlayerKnowledge.nonCurseRunesKnown(this);
    }

    /**
     * Checks whether the player is aware of what the object's effect does - the port of C's
     * {@code object_effect_is_known} ({@code obj-knowledge.c}).
     *
     * <p>C compares the two {@code effect} pointers: the known counterpart either shares the
     * object's effect chain once the player has learned it, or holds none. The port keeps lists, so
     * it compares contents both ways: every effect on this object must be on the known counterpart
     * and every effect on the known counterpart must be on this object, which is {@code true} for
     * two empty lists as well as for a fully learned one.
     *
     * <p>An object with no known counterpart answers {@code false}, where C would dereference a null
     * pointer; {@link #isFullyKnown()} never reaches here without one.
     *
     * <p>Function effectIsKnown coded before 261002, commented in full on 261002.
     *
     * @return {@code true} if the known counterpart's effects are the same as this object's
     */
    @Contract(pure = true)
    @CheckReturnValue
    private boolean effectIsKnown() {
        if (known == null) return false;
        List<Effect> knownEffects = known.getEffect();
        for (Effect eff : this.getEffect()) {
            if (!knownEffects.contains(eff)) return false;
        }
        for (Effect eff : known.getEffect()) {
            if (!effect.contains(eff)) return false;
        }
        return true;
    }

    /**
     * Combines the origin of another object into this one when the two are merged - the port of C's
     * {@code object_origin_combine} ({@code obj-pile.c}). Only this object changes.
     *
     * <p>If the two came from different monster races, a unique's record is preferred over a
     * non-unique's: this keeps its own if it is the unique, and takes the other's origin, depth and
     * race if that is the unique. If neither or both are unique the origin becomes
     * {@code ORIGIN_MIXED}. If they came from the same race (including both having none) the origin
     * becomes {@code ORIGIN_MIXED} only when the origin type or depth differs, and is left alone
     * otherwise. Race is compared by identity, as C compares pointers.
     *
     * <p>Function originCombine coded before 261002, commented in full on 261002.
     *
     * @param item the item being merged into this one; not changed
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
     * Shares out the charges and recharge timeout of rods, staves and wands when some of a stack
     * moves to another - the port of C's {@code distribute_charges} ({@code obj-util.c}). This
     * object is C's {@code source}, {@code item} is its {@code dest}, and {@code amount} is its
     * {@code amt}.
     *
     * <p>Wands and staves split their total {@code pval} in proportion, {@code pval * amount /
     * number} in integer division. The destination gets that share - replacing its own {@code pval}
     * if {@code destNew}, adding to it otherwise - and the source loses it, unless the whole stack
     * is moving, which leaves the source's {@code pval} alone for a neater message.
     *
     * <p>Rods also share out their timeout. The destination takes all the time remaining, up to the
     * most that {@code amount} rods can hold, which is the average recharge time multiplied by the
     * count. For a new destination the cap is {@code amount} rods; for an existing one it is the
     * destination's rods after the move, and nothing is moved if the destination is already at or
     * past that cap. The source loses whatever the destination gains, again unless the whole stack
     * is moving.
     *
     * <p>Divides by this object's {@code number}, so a stack of zero throws, as C would fault.
     *
     * <p>Function distributeCharges coded before 261002, commented in full on 261002.
     *
     * @param item    the object receiving the share, of the same type as this one
     * @param amount  how many items are being moved
     * @param destNew {@code true} to ignore whatever charges or timeout the destination holds and
     *                treat it as a new stack
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
     * <p>An empty map stands for "no curses", which is how C's {@code curses_are_equal} treats a
     * null curse array. C reads the bare pointer and tests it for {@code NULL} before indexing, so
     * a caller here that checks {@code isEmpty()} first is the port of that test. Both
     * constructors and {@link #wipe} build the map, so the backing field is not {@code null} in
     * practice; the {@code null} check here is a guard, and answers an empty map if it ever is,
     * rather than pushing the null onto every caller.
     *
     * <p>Only the curses the object carries are present, where C's array has a slot for every
     * curse in the game; see {@link #curses} for how absence, power zero and C's null and
     * all-zero arrays line up. The entries walk in ascending curse index, which is the order of
     * C's loop over the array ({@link #CURSE_ORDER}).
     *
     * <p>A lookup by a {@code null} curse has no C counterpart, as C's key is an integer index. It
     * throws {@link NullPointerException} on a non-empty map, whose comparator reads the key's
     * index, but answers {@code false} on an empty one. Callers pass curses taken from the
     * registry or from another curse map.
     *
     * <p>Function getCurses coded before 260817, commented in full on 260817, rewritten on 261003
     * for the ordering and null-key notes.
     *
     * @return this object's curses and their instance data, as an unmodifiable view, in curse
     *         index order
     */
    public Map<Curse, CurseData> getCurses() {
        if (curses == null)
            return Map.of();

        return Collections.unmodifiableMap(curses);
    }

    /**
     * Replaces this object's curses with a copy of the given map, the port of the loop in C's
     * {@code copy_curses} ({@code obj-curse.c}) that writes the power and the rolled timeout into
     * each slot.
     *
     * <p>The field is assigned a new map from {@link #cursesFactory()} and the argument's entries
     * are put into it, so the argument map is not kept and the curses end up in curse index order
     * ({@link #CURSE_ORDER}) whatever order the argument holds them in. The {@link CurseData}
     * values are shared, not copied, and whatever the object carried before is discarded. Because
     * the field is reassigned before the entries are read, passing this object's own
     * {@link #getCurses()} view is safe.
     *
     * <p>C's loop merges into the curses already on the object. This method does not merge: its
     * only production caller, {@link ObjectUtils#copyCurses}, builds the merged map itself, rolling
     * each timeout, and hands the result over. A {@code null} argument is ignored and the object
     * keeps the curses it had, as C's {@code copy_curses} returns at once on a {@code NULL}
     * source.
     *
     * <p>Function setCurses coded before 261002, commented in full on 261002, ordering and null
     * argument notes corrected on 261007.
     *
     * @param destCurseMap the curses this object should carry; the map is copied, the instance data
     *                     in it is taken by reference
     */
    public void setCurses(Map<Curse, CurseData> destCurseMap) {
        if (destCurseMap == null)
            return;
        
        curses = cursesFactory();
        curses.putAll(destCurseMap);
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
     * never carried a curse. A {@code null} curse adds nothing.
     *
     * <p>This is a deliberate simplification, not a port of C's {@code append_object_curse}
     * ({@code obj-curse.c}). That function refuses a curse unless its power beats the one already
     * on the item, refuses one that conflicts with a curse or an object property the item has
     * ({@code curses_conflict}, the {@code TIMED_INC} failure tests and {@code conflict_flags}),
     * rolls the timeout itself, and answers whether it applied the curse. This method does none of
     * that: it writes what it is handed, as the bare {@code obj->curses[i]} assignments in
     * {@code obj-knowledge.c} do. A port of {@code append_object_curse} belongs with
     * {@code apply_curse} in {@code obj-make.c}, which has no Java counterpart yet.
     *
     * <p>Function addCurse coded before 260817, commented in full on 260817, simplification note
     * added on 261007.
     *
     * @param curse   the curse to apply; {@code null} is ignored
     * @param power   the curse's power on this object
     * @param timeout turns until the curse's first effect
     */
    public void addCurse(Curse curse, int power, int timeout) {
        CurseData curseData = new CurseData(power, timeout);
        if (this.curses == null) {
            this.curses = cursesFactory();
        }

        if (curse == null)
            return;
        
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
     * <p>A {@code null} curse adds nothing. As with the numeric form, this is a plain write, not a
     * port of C's {@code append_object_curse}: no conflict test, no power comparison, no rolled
     * timeout.
     *
     * <p>Function addCurse coded before 260817, commented in full on 260817, null and
     * simplification notes added on 261007.
     *
     * @param curse     the curse to apply; {@code null} is ignored
     * @param curseData the instance data to store, taken by reference
     */
    public void addCurse(Curse curse, CurseData curseData) {
        if (this.curses == null) {
            this.curses = cursesFactory();
        }
        if (curse == null) return;
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
     * <p>A {@code null} argument throws {@link NullPointerException}. The {@code null} is the
     * parameter {@code curses}, not the field of the same name, which this method creates on demand.
     * That differs from {@link #setCurses} and {@link #clearAndPutCurses}, which ignore a
     * {@code null} as C's {@code copy_curses} does; C has no function for this batch add.
     *
     * <p>Function addCurses coded before 260817, commented in full on 260817, null argument note
     * added on 261007.
     *
     * @param curses the curses to add, with their instance data taken by reference; must not be
     *               {@code null}
     */
    public void addCurses(Map<Curse, CurseData> curses) {
        if (this.curses == null) {
            this.curses = cursesFactory();
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
     * <p>The field is replaced with a new map from {@link #cursesFactory()}, filled from the
     * argument, so the argument map itself is not kept, later changes to it do not reach this
     * object, and the curses walk in curse index order ({@link #CURSE_ORDER}). The
     * {@link CurseData} values are shared, not copied. Because the field is reassigned before the
     * entries are read, passing this object's own {@link #getCurses()} view is safe and leaves the
     * curses as they were. The same holds for {@link #setCurses}.
     *
     * <p>A {@code null} argument is ignored and the object keeps the curses it had, matching
     * {@link #setCurses}.
     *
     * <p>Function clearAndPutCurses coded before 260817, renamed from {@code clearAndPut} on 260817,
     * commented in full on 260817, rewritten on 261002 for the copying replace, ordering and null
     * argument notes corrected on 261007.
     *
     * @param curseEntries the curses this object should carry; the map is copied, the instance data
     *                     in it is taken by reference
     */
    public void clearAndPutCurses(Map<Curse, CurseData> curseEntries) {
        if (curseEntries == null)
            return;
        
        this.curses = cursesFactory();
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
            curses = cursesFactory();
        curses.clear();
    }

    /**
     * Changes the power of a curse already on this object, leaving its timeout alone.
     *
     * <p>The port of C's bare {@code obj->curses[i].power = ...} assignment, which appears wherever
     * a curse is weakened or strengthened without being added or taken away.
     *
     * <p>C writes into the slot whether or not the curse was active, and callers rely on that:
     * {@code obj-knowledge.c} copies power onto the known object, whose slot may be empty. So a
     * positive power on a curse the object does not carry adds it, with a timeout of zero as in
     * C's zero-filled slot; callers that need a timeout set it separately. {@code append_object_curse}
     * ({@code obj-curse.c}) is not this write: it also stores a rolled timeout
     * ({@code randcalc(c->obj->time, 0, RANDOMISE)}) alongside the power, and only after its
     * conflict tests pass.
     *
     * <p>In C a power of zero means the curse is off, and in this port that means the key is absent
     * from the map (see {@link #removeCurse}). A power of zero or below therefore removes the
     * entry if there is one, and adds nothing if there is not, so the map never holds a
     * power-zero entry from this method.
     *
     * <p>Creates the curse map first if the object has none.
     *
     * <p>Function setCursePower coded on 260817, commented in full on 261003, timeout note
     * corrected on 261007.
     *
     * @param curse the curse to adjust; ignored if {@code null}
     * @param power the curse's new power; zero or below takes the curse off
     */
    public void setCursePower(Curse curse, int power) {
        if (curse == null) return;
        
        if (this.curses == null) {
            this.curses = cursesFactory();
        }

        if (power <= 0) {
            if (this.curses.containsKey(curse)) {
                this.curses.remove(curse);
            }
            return;
        }

        if (this.curses.containsKey(curse)) {
            this.curses.get(curse).setPower(power);
            return;
        }

        CurseData curseData = new CurseData(power, 0);
        this.curses.put(curse, curseData);
    }

    /**
     * Takes a curse off this object.
     *
     * <p>The port of C's {@code obj->curses[i].power = 0}. C cannot delete an entry from an array
     * indexed by curse, so it zeroes the power and reads that back as "no curse"; the port holds a
     * map, where absence says the same thing directly. {@link #setCursePower} with a power of zero
     * or below removes the entry too, so either call takes the curse out of {@link #getCurses()}.
     *
     * <p>Silently does nothing for a curse the object does not carry, or for a {@code null} curse.
     *
     * <p>This is not a port of C's {@code remove_object_curse} ({@code obj-curse.c}), which
     * answers whether the object had the curse, prints "The %s curse is removed!" when asked, and
     * leaves a slot that is already at power zero alone. This method returns nothing, prints
     * nothing, and removes an entry even if it holds power zero. Removing the last curse leaves an
     * empty map, which is what C's {@code check_object_curses} achieves by freeing the array.
     *
     * <p>Function removeCurse coded on 260817, commented in full on 260817, power-zero note
     * corrected on 261003, simplification note added on 261007.
     *
     * @param curse the curse to remove; {@code null} is ignored
     */
    public void removeCurse(Curse curse) {
        if (this.curses == null) {
            this.curses = cursesFactory();
        }
        if (curse == null) return;
        this.curses.remove(curse);
    }

    /**
     * Advances this object's recharge by one game turn, the port of C's {@code recharge_timeout}
     * ({@code obj-util.c}).
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
     * goes: three rods on a ten-turn interval hold thirty turns of pooled charge, which drains over
     * eighteen game turns as the rate steps down from three per turn to one. The first rod is
     * ready on the fourth turn, the second on the eighth and the third on the eighteenth.
     *
     * <p>Nothing charging is the early return: with a zero count the timeout is left untouched and
     * the answer is {@code false}, so a ready object costs one call to {@link #numberCharging} and
     * no write.
     *
     * <p>Function rechargeTimeout coded before 261002, commented in full on 261002.
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
     * <p>The division rounds up, so any timeout left at all counts one more item as charging: a
     * timeout of 1 on a ten-turn rod is one rod charging, and 10 is still one, but 11 is two. The
     * interval is taken at its average, which truncates for dice, so a {@code 1d10} interval is
     * five. An interval that averages to zero or below, like a missing one, means nothing is ever
     * charging.
     *
     * <p>Function numberCharging coded before 261002, commented in full on 261002.
     *
     * @return the number of items currently charging (0 if none)
     */
    public int numberCharging() {
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
     * Returns the number of items in this stack, the port of reading C's {@code obj->number}.
     *
     * <p>Zero only on a blank or wiped item. A pile holds one object per stack, so this is the
     * stack size, not the number of objects in the pile.
     *
     * <p>Function getNumber coded before 260904, commented in full on 261002.
     *
     * @return the number of items in this stack
     */
    public int getNumber() {
        return number;
    }

    /**
     * Sets the stack size, the port of C's {@code obj->number = ...} assignment. Stored as given;
     * C's field is a {@code uint8_t} and the port's is an {@code int}, so nothing caps it at 255.
     *
     * <p>Function setNumber coded before 260904, commented in full on 261002.
     *
     * @param number the stack count to set — C's {@code obj->number}
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Returns this object's item type, the port of reading C's {@code obj->tval}.
     *
     * <p>{@link TValue#TV_NONE}, C's tval 0, on a blank or wiped item. Unlike {@link #getKind()}
     * this is a copy of the kind's type held on the object itself, so it can be read when the kind
     * is {@code null}.
     *
     * <p>Function gettValue coded before 260904, commented in full on 261002.
     *
     * @return this object's base type (tval)
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * Sets this item's type, the port of C's {@code obj->tval = ...} assignment. Stored as given,
     * and independent of {@link #kind}: nothing checks that the two agree.
     *
     * <p>Function settValue coded before 260904, commented in full on 261002.
     *
     * @param tValue the item type value to set — C's {@code obj->tval}
     */
    public void settValue(TValue tValue) {
        this.tValue = tValue;
    }

    /**
     * Returns the turns remaining until this object can be used again, the port of reading C's
     * {@code obj->timeout}.
     *
     * <p>For a stack of rods this is one pooled figure, not a figure per rod; see
     * {@link #numberCharging} for how many rods it still covers. For a light it is the fuel left.
     *
     * <p>Function getTimeout coded before 260904, commented in full on 261002.
     *
     * @return the turns remaining until this object is ready to use again (0 = ready)
     */
    public int getTimeout() {
        return timeout;
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
     * <p>This is emphatically not "has the player identified this item". C makes the counterpart
     * the first time the player senses, sees or grabs the item ({@code object_sense},
     * {@code object_see} and {@code object_grab} in {@code obj-knowledge.c}), and it starts nearly
     * empty, so the answer turns to yes long before anything about the item has been learned. What
     * the player actually knows is the <em>content</em> of that companion. Reading this as
     * identification is the mistake the name invites.
     *
     * <p>C uses the test two ways. The learning code asserts it, as {@code assert(obj->known)}, a
     * sanity check that the pairing was set up, on its own line and never folded into a condition
     * that decides whether to learn something. {@code ignore_level_of} instead branches on it: an
     * item the player has never met is graded {@code IGNORE_MAX}, so it is never ignored on
     * quality. {@link #ignoreLevelOf} is that caller here.
     *
     * <p>Function isKnown coded before 261002, commented in full on 261002.
     *
     * @return whether a known counterpart has been attached to this item
     */
    public boolean isKnown() {
        return known != null;
    }

    /**
     * Reports whether this item's to-hit bonus is the one it ought to have — that is, whether it
     * is carrying nothing worth learning from. The port of C's {@code object_has_standard_to_h}
     * ({@code obj-knowledge.c}).
     *
     * <p>The question exists because to-hit is the one combat figure an ordinary item can have
     * without being remarkable. Body armour is heavy and gets in the way, so its kind declares a
     * penalty as a matter of course — Chain Mail is {@code attack:1d4:-2:0} in {@code object.txt},
     * and every Chain Mail rolled has {@code toHit == -2}. A plain {@code getToHit() != 0} would
     * read that as evidence of enchantment and teach the to-hit rune to anyone who put one on, which
     * is why {@link PlayerKnowledge#equipLearnOnMeleeAttack} asks this
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
     * <p>Body armour here means soft, hard and dragon armour, as C's {@code tval_is_body_armor}
     * does. Worked through: a Chain Mail at {@code -2} is standard and one at {@code 0} or
     * {@code +3} is not; a Dagger at {@code 0} is standard and at {@code -1} is not.
     *
     * <p>Function hasStandardToH coded on 260815, commented in full on 261002.
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
     * <p><b>Where this differs from C.</b> C's {@code obj_has_flag} also looks at the object flags of
     * every curse on the item; this method reads only the item's own set, which is the
     * {@code of_has(obj->flags, flag)} every current caller corresponds to. C's one caller of
     * {@code obj_has_flag}, {@code obj_can_takeoff} (the {@code OF_STICKY} test), has no Java port
     * yet, and it will need a curse-aware method when it arrives. Curse flags reach the player
     * calculations through {@code CurseSource} entries instead. An item whose flag set was never
     * allocated gets an empty one here rather than failing.
     *
     * <p>Function hasFlag coded on 260815, commented in full on 261007. Corrected on 260816: the
     * previous version placed an item's readable flags on {@link KnownObject}, which is a different
     * store, and routed them through {@code getKnownFlags}, since withdrawn. Checked against C
     * again on 261002.
     *
     * @param flag the flag to test for
     * @return whether this item carries it
     */
    public boolean hasFlag(ObjectFlag flag) {
        if (flags == null) {
            flags = new Flag<>(ObjectFlag.class);
        }
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
     * <p>What is still to port is {@code object_desc} and the static helpers beside it in
     * {@code obj-desc.c}, which build the base name, the quantity prefix, the combat, charge and
     * light details and the inscription in turn. Of those, only {@link #objDescNameFormat} and
     * {@link #objectKindName} exist so far. Today the stub reaches the player through
     * {@link #printCustomMessage}'s {@code {name}} tag, {@link #verifyObject}, and the messages
     * {@code PlayerKnowledge} builds as the player learns a rune or wields an item, which pass the
     * name on to {@link #flagMessage} and to their own text. None of them can say anything about
     * the item yet.
     *
     * <p>Function description coded on 260815, commented in full on 261002, callers corrected on
     * 261007.
     *
     * @param descriptionFlags how much of the name to build, C's {@code mode}
     * @param player           the player whose knowledge decides what may appear in the name
     * @return the item's name; the placeholder tag while stubbed
     */
    public String description(Flag<ObjectDescription> descriptionFlags, Player player) {
        // Stub function
        // TODO: Implement as part of Chapter 7
        return "{DESCRIPTION_TAG}";
    }

    /**
     * Announces that a flag has shown itself on a named item — the port of C's
     * {@code flag_message} ({@code obj-properties.c}). Called at the moment of noticing, so the
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
     * returns without a word. C marks that case with a {@code NULL} message; the port's parser holds
     * {@code ""} for an absent {@code msg:} (see {@link ObjectProperty#getNoticeMessage}), so both
     * {@code null} and the empty string are treated as "no message" here and nothing reaches the
     * message log. Wielding a Wooden Torch, whose {@code OF_BURNS_OUT}, {@code OF_TAKES_FUEL} and
     * {@code OF_LIGHT_2} all lack a {@code msg:}, is the ordinary case. The two errors are logged here, where C prints a "Bug:" line to the
     * player. C numbers its {@code OF_NONE} as zero, which is a valid index, so it would report
     * that one as a missing entry; here it is reported as an invalid index.
     *
     * <p><b>Where the substitution differs from C.</b> C walks the message and, for any
     * {@code {tag}} of letters whose spelling starts {@code name}, inserts the item's name; it drops
     * every other tag, and it truncates the result at 1,024 characters. The plain replace here
     * handles only the exact {@code {name}}, leaves other tags in place and does not truncate.
     * None of that is reachable with the shipped data, where every {@code msg:} line that carries a
     * tag uses {@code {name}} and nothing else, but a new tag in the data file would need this
     * method teaching.
     *
     * <p>The finished text goes to {@link Message#message} as a {@code "%s"} argument, never as the
     * pattern, so a percent sign in an item's name cannot be read as a format directive.
     *
     * <p>Function flagMessage coded on 260815, commented in full on 261007.
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
        if (toSend == null || toSend.isEmpty()) return;
        toSend = toSend.replace("{name}", name);
        Message.message("%s", toSend);
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
        if (flags != null)
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
     * reused buffer safe. {@link Flag#copyFrom} wipes for itself as well, so for a distinct set the
     * explicit call is redundant in Java; it stays because it is the clause C writes, and a reader
     * comparing the two should find them line for line.
     *
     * <p>Passing this item's own live flag set as the argument empties it and leaves it empty,
     * because the wipe comes before the copy. {@link Flag#copyFrom} survives a self-copy, but the
     * wipe here has already discarded the source. C's {@code of_wipe} then {@code of_copy} on the
     * same array does the same, so this matches C; no caller passes the live set.
     *
     * <p>C guards with {@code if (!obj) return}, leaving the wiped set behind for a null item. An
     * instance method has no such case to answer — a caller with no item cannot reach this at all —
     * so a Java caller that could be holding nothing wipes its own set on that path.
     *
     * <p>Function objectFlags coded on 260829 / commented in full on 261007.
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
        if (notice != null)
            flags.copyFrom(notice);
        return flags;
    }

    /**
     * Returns this object's recharge interval, the port of reading C's {@code obj->time}.
     *
     * <p>The dice, not a rolled figure: a rod's {@code time:} line gives the interval and every
     * recharge re-rolls from it. {@link #numberCharging} takes its average to work out how many
     * items in a stack are still charging. For a curse's bare object it is the dice re-rolled into
     * each cursed object's timeout.
     *
     * <p>Returns a copy, as every read of C's {@code obj->time} copies the {@code random_value}
     * struct, so a caller that changes the result cannot change this item's recharge dice. It is
     * never {@code null}: an object with no recharge interval holds a zero {@link Random}, as C
     * holds a zeroed {@code random_value}, and {@link #numberCharging} reads that as nothing
     * charging.
     *
     * <p>Function getTime commented in full on 261002, copy and never-null note rewritten on
     * 261003.
     *
     * @return a copy of the random interval between activations of this object's effect; a zero
     * value if it has none
     */
    public Random getTime() {
        return time.copy();
    }

    /**
     * Sets the recharge-time dice — the port of C's {@code obj->time = k->time;} struct assign
     * (e.g. {@code object_prep} in {@code obj-make.c}).
     *
     * <p>C's {@code random_value} is a plain struct, so assigning it copies the four dice terms by
     * value; this class's {@link Random} is a mutable reference type, so a bare field assignment
     * here would instead alias this item's dice with the caller's — a later change to one would leak
     * into the other. {@link Random#copy()} restores the value semantics C gets for free.
     *
     * <p>{@code null} resets the dice to a zero {@link Random}, which C's struct assign cannot
     * express but which is C's zeroed {@code random_value}; the field is never left {@code null}.
     * No current caller passes it.
     *
     * <p>Function setTime coded before 260904, commented in full on 260904, C line number removed on
     * 261002, null handling rewritten on 261003.
     *
     * @param time the recharge dice to copy in, or {@code null} to reset it to zero
     */
    public void setTime(Random time) {
        if (time == null)
            this.time = Random.Zero();
        else
            this.time = time.copy();
    }

    /**
     * Raises a notice flag on this item, the port of C's {@code obj->notice |= flag}.
     *
     * <p>C has no function for this: it ORs the bit in wherever it likes, and
     * {@code ui-wizard.c} does it to {@code known_obj->notice} when it makes an imagined item. Here
     * it goes through the {@link Flag} set, which adds the one flag and leaves the others alone.
     * Raising a flag that is already up changes nothing, as with the C bit-OR.
     *
     * <p>The boolean return has no C counterpart, because {@code |=} yields no answer. It is the
     * {@link Flag#on} result passed straight up, so a caller can tell a new notice from a repeat.
     * {@link #orNotice} does the same job with no return; the two are interchangeable for a caller
     * that ignores the answer. Only the item's own set changes: {@link #getNotice()} hands out a
     * copy, so editing that copy marks nothing.
     *
     * <p>Function setNoticeOn coded before 261003, commented in full on 261003.
     *
     * @param flag the {@link ObjectNotice} flag to raise
     * @return {@code true} if the flag was newly set, {@code false} if it was already up
     */
    public boolean setNoticeOn(ObjectNotice flag) {
        if (notice == null)
            notice = new Flag<>(ObjectNotice.class);
        return notice.on(flag);
    }

    /**
     * Sets the turns remaining before this item can be used again — the port of C's
     * {@code obj->timeout = ...} field assignment (e.g. {@code object_prep} in {@code obj-make.c},
     * which starts a light's timeout at its fuel).
     *
     * <p>Stored as given. For a stack of rods the figure is one pooled timeout for the whole stack,
     * and for a light it is the fuel left; {@link #getTimeout()} reads it back.
     *
     * <p>Function setTimeout coded before 260904, commented in full on 260904, rewritten on 261002.
     *
     * @param timeout turns until ready; {@code 0} means ready now
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the player's inscription on this object, or {@code null} if it carries none.
     *
     * <p>C stores this as {@code quark_t note} in {@code object.h} — an index into the global
     * quark table, where {@code 0} means "no inscription" and the text is fetched with
     * {@code quark_str}. The port holds the text directly, so {@code null} is the equivalent of
     * C's {@code 0} and callers test it rather than the index.
     *
     * <p>Function getNote coded before 260904, commented in full on 261002; the C line number was
     * removed on 261002.
     *
     * @return the inscription, or {@code null} if the object is uninscribed
     */
    public String getNote() {
        return note;
    }

    /**
     * Lowers a notice flag on this item, the port of C's {@code obj->notice &= ~flag}.
     *
     * <p>C has no function for this either. The one use in the source is {@code ui-object.c},
     * where choosing "unignore this item" runs {@code obj->known->notice &= ~(OBJ_NOTICE_IGNORE)}.
     * Here the {@link Flag} set drops the one flag and leaves the others alone. Lowering a flag
     * that is already down changes nothing, as with the C mask.
     *
     * <p>The boolean return has no C counterpart, because {@code &=} yields no answer. It is the
     * {@link Flag#off} result passed straight up, so a caller can tell a flag that was cleared from
     * one that was never up. Only the item's own set changes; {@link #getNotice()} hands out a copy.
     * Unlike the raising side there is no second name for this: it is the item's only way to lower a
     * notice flag.
     *
     * <p>Function setNoticeOff coded before 261003, commented in full on 261003.
     *
     * @param flag the {@link ObjectNotice} flag to lower
     * @return {@code true} if the flag was up and has been cleared, {@code false} if it was
     * already down
     */
    public boolean setNoticeOff(ObjectNotice flag) {
        if (notice == null)
            notice = new Flag<>(ObjectNotice.class);
        return notice.off(flag);
    }

    /**
     * Returns this item's sub-type within its type, the port of reading C's {@code obj->sval}.
     *
     * <p>Copied from the kind when the item is prepared, so two items of one kind always agree.
     * {@link #earlierObject} sorts the pack by it within a type.
     *
     * <p>Function getsValue coded before 260904, commented in full on 261002.
     *
     * @return this item's sub-type value — C's {@code obj->sval}
     */
    public int getsValue() {
        return sValue;
    }

    /**
     * Sets this item's sub-type, the port of C's {@code obj->sval = ...} assignment. Stored as
     * given; C's field is a {@code uint8_t} and the port's is an {@code int}, so nothing narrows it.
     *
     * <p>Function setsValue coded before 260904, commented in full on 261002.
     *
     * @param sValue the sub-type value to set — C's {@code obj->sval}
     */
    public void setsValue(int sValue) {
        this.sValue = sValue;
    }

    /**
     * Returns the base weight of one of this item, the port of reading C's {@code obj->weight}.
     *
     * <p>The base figure only, as the field note on {@link #weight} says. For the burden an item
     * puts on the player, with curses applied, use {@link #objectWeightOne()}.
     *
     * <p>Function getWeight coded before 260904, commented in full on 261002.
     *
     * @return this item's weight in tenths of a pound — C's {@code obj->weight}
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the base weight of one of this item, the port of C's {@code obj->weight = ...}
     * assignment. Stored as given, with no floor at zero; {@link #objectWeightOne()} applies that
     * when the weight is read for a burden.
     *
     * <p>Function setWeight coded before 260904, commented in full on 261002.
     *
     * @param weight the weight to set — C's {@code obj->weight}
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Returns the number of damage dice this item rolls, the port of reading C's {@code obj->dd}.
     *
     * <p>Function getDamageDice coded before 260904, commented in full on 261002.
     *
     * @return the number of damage dice this item rolls — C's {@code obj->dd}
     */
    public int getDamageDice() {
        return damageDice;
    }

    /**
     * Sets the number of damage dice — C's {@code obj->dd}. See {@link #setBaseAC} for why a known
     * counterpart may be given a zero here rather than the truth.
     *
     * <p>Function setDamageDice coded before 260904, commented in full on 261002.
     *
     * @param damageDice the number of damage dice to set
     */
    public void setDamageDice(int damageDice) {
        this.damageDice = damageDice;
    }

    /**
     * Returns the sides on each damage die, the port of reading C's {@code obj->ds}.
     *
     * <p>Function getDamageSides coded before 260904, commented in full on 261002.
     *
     * @return the sides per damage die — C's {@code obj->ds}
     */
    public int getDamageSides() {
        return damageSides;
    }

    /**
     * Sets the sides on each damage die — C's {@code obj->ds}. See {@link #setBaseAC} for why a
     * known counterpart may be given a zero here rather than the truth.
     *
     * <p>Function setDamageSides coded before 260904, commented in full on 261002.
     *
     * @param damageSides the sides per damage die to set — C's {@code obj->ds}
     */
    public void setDamageSides(int damageSides) {
        this.damageSides = damageSides;
    }

    /**
     * Returns this item's base armour class, before any to-armour-class bonus — the port of reading
     * C's {@code obj->ac}.
     *
     * <p>Function getBaseAC coded before 260904, commented in full on 261002.
     *
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
     * <p>Function setBaseAC coded before 260904, commented in full on 261002.
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
     * <p>Function setToHit coded before 260904, commented in full on 261002.
     *
     * @param toHit the to-hit bonus to set
     */
    public void setToHit(int toHit) {
        this.toHit = toHit;
    }

    /**
     * Returns this item's extra parameter, the port of reading C's {@code obj->pval}.
     *
     * <p>What it means depends on the type: charges for a wand or staff, an amount for gold, a
     * launcher's multiplier for a bow. See the field note on {@link #pValue}.
     *
     * <p>Function getpValue coded before 260904, commented in full on 261002.
     *
     * @return this item's extra parameter value — C's {@code obj->pval}
     */
    public int getpValue() {
        return pValue;
    }

    /**
     * Sets this item's extra parameter, the port of C's {@code obj->pval = ...} assignment. Stored
     * as given; C's field is an {@code int16_t} and the port's is an {@code int}, so nothing clamps
     * it to {@code MAX_PVAL}. The stacking code applies that cap itself.
     *
     * <p>Function setpValue coded before 260904, commented in full on 261002.
     *
     * @param pValue the extra parameter value to set — C's {@code obj->pval}
     */
    public void setpValue(int pValue) {
        this.pValue = pValue;
    }

    /**
     * Sets the to-armour-class bonus — C's {@code obj->to_a}. See {@link #setBaseAC} for why a
     * known counterpart may be given a zero here rather than the truth.
     *
     * <p>Function setToAC coded before 260904, commented in full on 261002.
     *
     * @param toAC the to-armour-class bonus to set — C's {@code obj->to_a}
     */
    public void setToAC(int toAC) {
        this.toAC = toAC;
    }

    /**
     * Sets the to-damage bonus — C's {@code obj->to_d}. See {@link #setBaseAC} for why a known
     * counterpart may be given a zero here rather than the truth.
     *
     * <p>Function setToDam coded before 260904, commented in full on 261002.
     *
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
     * Replaces this item's modifier map, the port of filling C's {@code obj->modifiers} array.
     *
     * <p>Stores the map given, without copying it, so the item and the caller share it afterwards.
     * The old map is dropped untouched: it is not cleared, so a map the item shared with another is
     * left alone, and passing {@link #getModifiers()} back in changes nothing. A {@code null} makes
     * {@link #getModifiers()} answer an empty map. {@link #putModifier} changes one entry instead.
     * Callers that build a map for this call, as the item-preparation and knowledge code do, choose
     * its type; a {@link HashMap} walks in hash order and a {@link LinkedHashMap} in insertion order.
     *
     * <p>Function setModifiers coded before 260904, commented in full on 261002.
     *
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
     * Replaces this item's element info map, the port of filling C's {@code obj->el_info} array.
     *
     * <p>Stores the map given, without copying it, and drops the old map untouched, as
     * {@link #setModifiers} does. The {@link ElementInfo} values are shared too, so a caller
     * copying them from another item wants {@link ElementInfo#copy} first. {@link #putElInfo}
     * and {@link #setElInfoResLevel} change one entry instead.
     *
     * <p>Function setElInfo coded before 260904, commented in full on 261002.
     *
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
     * assigns inline wherever it needs to — the data-file parsers {@code parse_object_values},
     * {@code parse_curse_values} and {@code parse_ego_values} in {@code obj-init.c}, the curse
     * merge {@code apply_curse_attributes} in {@code obj-curse.c}, and {@code player_know_object}
     * in {@code obj-knowledge.c}. The method exists to give those assignments one place to land.
     *
     * <p>The boundary between the two representations is what the body is for. C declares
     * {@code struct element_info el_info[ELEM_MAX]} inside the object struct, so a slot exists for
     * every element from the moment {@code object_new} zero-fills it and the assignment can never
     * fail. Here the map is sparse, so the entry is created on demand, and a fresh
     * {@link ElementInfo} starts with empty flags and a zero level, which is what C's zero-fill
     * leaves behind. The map is created as well if the field is {@code null}, which only the full
     * constructor can leave it, and that map is a {@link HashMap}, not the insertion-ordered
     * {@link LinkedHashMap} the other paths build.
     *
     * <p>Writes the level only, leaving {@link ElementInfo#getFlags flags} untouched, as the C
     * assignment does. The knowledge code in {@code player_know_object}, which copies both halves,
     * needs the flags dealt with separately, or {@link #putElInfo} with a whole value.
     *
     * <p>The level is passed through uninterpreted; the scale is C's, where zero is neutral,
     * positive resists and negative is a vulnerability.
     *
     * <p>Function setElInfoResLevel commented in full on 260830, rewritten on 261002 without the C
     * line numbers.
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
     * <p>Exists because {@link #getElInfo()} answers {@code Map.of()} for an item whose map is
     * {@code null}, and an immutable empty map takes no writes, so a caller cannot add an entry
     * through the getter. The no-argument constructor and {@link #wipe} build an empty
     * insertion-ordered map, so the map is only created here, as a {@link HashMap}, for an item the
     * full constructor was given {@code null}.
     *
     * <p>Stores the {@link ElementInfo} given rather than copying it. That matters more here than for
     * most values: {@code ElementInfo} is mutable, so handing over a real item's instance would leave
     * the item and its counterpart unable to differ. Callers copying one object's element info onto
     * another want {@link ElementInfo#copy} first — {@code knowObject} does.
     *
     * <p>Function putElInfo coded on 260817, commented in full on 260817, null-map note corrected on
     * 261002.
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
     * <p>Creates the map on demand, for the same reason {@link #putElInfo} does: an item the full
     * constructor was given {@code null} for has none, and {@link #getModifiers()} answers an
     * immutable empty map for that state, which takes no writes. An item built any other way
     * already has an insertion-ordered map, and the one created here is a {@link HashMap}.
     *
     * <p>Function putModifier commented in full on 260827, null-map note corrected on 261002.
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
     * Sets the effects this item produces, the port of C's {@code obj->effect = ...} pointer
     * assignment.
     *
     * <p>Stores the list given, without copying it, which matches C sharing the effect chain
     * between a kind and the items made from it. A {@code null} is turned into a fresh empty list,
     * so {@link #getEffect()} never answers {@code null} for an item that has been through here.
     *
     * <p>Function setEffect coded before 260904, commented in full on 261002.
     *
     * @param effect the effect list to set — C's {@code obj->effect}; stored, not copied
     */
    public void setEffect(List<Effect> effect) {
        if (effect != null) this.effect = effect;
        else this.effect = new ArrayList<>();
    }

    /**
     * Tests whether one notice flag is up on this item, the port of C's
     * {@code obj->notice & OBJ_NOTICE_x} read.
     *
     * <p>C has no function for this: every reader tests the bit inline, as in
     * {@code obj-desc.c}, {@code obj-ignore.c}, {@code obj-knowledge.c}, {@code cave-square.c} and
     * {@code ui-object.c}. The C test is a bitwise AND, so it is true for any overlap and could be
     * handed several flags at once; no C caller does that, and here one {@link ObjectNotice} is
     * passed, so the question is always "is this one flag up".
     *
     * <p>It reads this item's own set. Most C readers test {@code obj->known->notice}, because the
     * attention paid to an item is kept on the player's known half, so a caller wanting that
     * answer calls this on {@link #getKnown()}. The exceptions are {@code obj-desc.c}, which tests
     * {@code obj->notice} for {@code OBJ_NOTICE_ASSESSED}, and {@code cave-square.c}, which tests
     * it for {@code OBJ_NOTICE_IMAGINED}. It is a read of the live set with no copy, unlike
     * {@link #getNotice()}, and changes nothing.
     *
     * <p>Function getNoticeHas coded before 261003, commented in full on 261003.
     *
     * @param flag the {@link ObjectNotice} flag to test
     * @return {@code true} if the flag is up on this item, {@code false} if it is down
     */
    public boolean getNoticeHas(ObjectNotice flag) {
        if (notice == null)
            notice = new Flag<>(ObjectNotice.class);
        return notice.has(flag);
    }

    /**
     * Replaces this item's slay set, the port of assigning C's {@code obj->slays} array.
     *
     * <p>Stores the set given, without copying it, and drops the old one untouched. A {@code null}
     * is kept and makes {@link #getSlays()} answer an empty set. There is no setter for brands;
     * {@link #appendSlay}, {@link #removeSlay} and {@link #clearSlays} change the set in place.
     *
     * <p>Function setSlays coded before 260904, commented in full on 261002.
     *
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
     * Sets this item's ego type, the port of C's {@code obj->ego = ...} pointer assignment.
     *
     * <p>Stores the registry entry itself, not a copy, because {@link #similar} compares egos by
     * identity. {@code null} makes the item an ordinary one.
     *
     * <p>Function setEgo coded before 260904, commented in full on 261002.
     *
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
        if (flags == null)
            flags = new Flag<>(ObjectFlag.class);
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
        if (flags == null)
            flags = new Flag<>(ObjectFlag.class);
        return flags.set(flag);
    }

    /**
     * Replaces this item's flags with the given set — the port of C's {@code of_wipe} followed by
     * {@code of_copy}.
     *
     * <p>Copies in. The argument stays the caller's and the two sets share nothing afterwards, which
     * is the point: assigning the reference instead would leave a known counterpart holding its
     * item's own set, after which knowledge and truth are the same object and can never diverge.
     * The wipe is said explicitly here even though {@link Flag#copyFrom} snapshots its source before
     * it unions: {@code copyFrom} leaves a self-copy untouched, so without the wipe
     * {@code item.setFlagsTo(item.getObjectFlags())} would keep the flags. With it, that call empties
     * the item's flags, as C's {@code of_wipe} followed by {@code of_copy} on one array does. No
     * production caller passes the live set; they all pass a freshly built one.
     *
     * <p>Replaces; it does not add. {@link #setFlags} is the one that adds. A {@code null} argument
     * is read as an empty set, so the item's flags end up empty.
     *
     * <p>Function setFlagsTo coded on 260816, commented in full on 260816, rewritten on 261007 for
     * the explicit wipe and the null argument.
     *
     * @param flags the flags this item should end up with; read, never retained
     */
    public void setFlagsTo(Flag<ObjectFlag> flags) {
        if (flags == null)
            flags = new Flag<>(ObjectFlag.class);

        // The below is explicit to handl the case of ItemObject obj; obj.setFlagsTo(obj.getFlags);
        this.flags.wipe();
        this.flags.copyFrom(flags);
    }

    /**
     * Offers a brand to this item, the port of C's {@code append_brand} ({@code obj-slays.c}). The
     * brand goes on only if the item has no brand of the same element, or has a weaker one, which it
     * then displaces.
     *
     * <p>"The same element" is the same {@link Brand#getName() name}, as C's {@code streq} on
     * {@code brands[i].name} has it; the code is not looked at, so {@code FIRE_2} and {@code FIRE_3}
     * are rivals. "Stronger" is {@link Brand#getMultiplier()}, not {@link Brand#getPower()}, which is
     * the rating the power calculation uses and a different number. The outcomes:
     * <ul>
     *   <li>no brand of that name on the item: added, {@code true};</li>
     *   <li>one present with a lower multiplier: removed, the new one added, {@code true};</li>
     *   <li>one present with the same or a higher multiplier: item untouched, {@code false}.</li>
     * </ul>
     *
     * <p>Like C, this relies on the set never holding two brands of one name, which each append
     * keeps true. A set filled any other way (see {@code ObjectUtils.copyBrands}, which dedupes in
     * bulk) must already satisfy it, or only the first match found is considered.
     *
     * <p>The set is created here on demand. {@link #getBrands()} answers {@code Set.of()} for an item
     * whose set has never been created, and an immutable empty set takes no writes. The knowledge
     * code writes brands onto counterpart objects built by the no-argument constructor, which are
     * exactly those items; C reaches the same place with the {@code mem_zalloc} its own brand block
     * performs before its first write.
     *
     * <p>Appending a brand the item already holds answers {@code false}, not a second member: same
     * name, equal multiplier. {@code PlayerKnowledge.knowObject} offers a counterpart only brands the
     * real item carries, so a repeat call finds the brand already there and ignores the {@code false}.
     *
     * <p>Function appendBrand coded on 260817 as a plain add, commented in full on 260817, rewritten
     * on 261007 for the replace-if-stronger rule and the {@code boolean} result.
     *
     * @param brand the brand offered to this item
     * @return {@code true} if the item now carries {@code brand}; {@code false} if it already holds a
     *         brand of the same name that is at least as strong, and so was left unchanged
     */
    public boolean appendBrand(Brand brand) {
        if (brands == null) {
            brands = new HashSet<>();
        }

        Brand oldBrand = brands.stream().filter(b -> b.getName().equals(brand.getName()))
                .findFirst().orElse(null);
        if (oldBrand == null) {
            brands.add(brand);
            return true;
        }

        if (oldBrand.getMultiplier() < brand.getMultiplier()) {
            brands.remove(oldBrand);
            brands.add(brand);
            return true;
        }

        return false;
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
     * Offers a slay to this item, the port of C's {@code append_slay} ({@code obj-slays.c}). The slay
     * counterpart of {@link #appendBrand}, with the same outcomes and the same on-demand set
     * creation, because {@link #getSlays()} answers {@code Set.of()} for an item whose set has never
     * been created.
     *
     * <p>Two slays are rivals when {@link Slay#sameMonsterSlain} says they kill the same monsters:
     * the same race flag and the same base. That is C's {@code same_monsters_slain}, and it is not a
     * name match. In {@code slay.txt} each name has exactly one race flag, so the two agree on shipped
     * data; a hand-built slay with a different name for the same race still displaces or is refused.
     * "Stronger" is {@link Slay#getMultiplier()}.
     * <ul>
     *   <li>no rival on the item: added, {@code true};</li>
     *   <li>a rival with a lower multiplier: removed, the new one added, {@code true};</li>
     *   <li>a rival with the same or a higher multiplier: item untouched, {@code false}.</li>
     * </ul>
     *
     * <p>Replacing relies on {@link Slay#equals} to find the old member in the set, which is why that
     * method compares every field and not just the grouping C uses.
     *
     * <p>Function appendSlay coded on 260817 as a plain add, commented in full on 260817, rewritten
     * on 261007 for the replace-if-stronger rule and the {@code boolean} result.
     *
     * @param slay the slay offered to this item
     * @return {@code true} if the item now carries {@code slay}; {@code false} if it already holds a
     *         slay on the same monsters that is at least as strong, and so was left unchanged
     */
    public boolean appendSlay(Slay slay) {
        if (slays == null) {
            slays = new HashSet<>();
        }

        Slay oldSlay = slays.stream().filter(s -> s.sameMonsterSlain(slay))
                .findFirst().orElse(null);

        if (oldSlay == null) {
            slays.add(slay);
            return true;
        }

        if (oldSlay.getMultiplier() < slay.getMultiplier()) {
            slays.remove(oldSlay);
            slays.add(slay);
            return true;
        }

        return false;
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
     * {@code object_flavor_is_aware} ({@code obj-knowledge.c}).
     *
     * <p>Awareness belongs to the <em>kind</em>, not to the object: drinking one unlabelled potion
     * teaches the player what every potion of that kind is, so the answer is the same for every
     * object sharing this one's kind. For an unflavoured object the kind is aware from the start.
     *
     * <p>C asserts that the kind exists; the port answers {@code false} for a kindless object
     * instead, which is the safe reading — nothing is known about an object with no kind to know
     * about. {@link #objectFlavourIsAware()} is a second port of the same C function that throws
     * for that state instead. This one is the lenient form, for the knowledge code, where a curse's
     * bare object can reach it.
     *
     * <p>Function flavourIsAware commented in full on 261002.
     *
     * @return {@code true} if the player knows what objects of this kind are
     */
    public boolean flavourIsAware() {
        if (kind == null) return false;
        return kind.isAware();
    }

    /**
     * Whether this object is of a kind that gives up everything at a glance — the port of C's
     * {@code easy_know} ({@code obj-knowledge.c}).
     *
     * <p>Both halves are required: the kind must be one the player is aware of, and it must carry
     * {@code KF_EASY_KNOW}. The flag marks kinds with nothing hidden to discover — a scroll's
     * properties are wholly determined by which scroll it is — so once the player recognises the
     * kind there is no further identification to do. {@code flagsKnown} uses it to decide whether an
     * ego's flags may be folded in without the player having learned the individual runes.
     *
     * <p>Neither half is enough alone: an easy-know kind the player has not yet met is not known,
     * and an aware kind without the flag still has runes to find.
     *
     * <p>C asserts that the kind exists; the port answers {@code false} for a kindless object.
     *
     * <p>Function easyKnow commented in full on 261002.
     *
     * @return {@code true} if recognising this object's kind reveals all of its properties
     */
    public boolean easyKnow() {
        if (kind == null) return false;
        return kind.isAware() && kind.getKindFlags().has(ObjectKindFlag.KF_EASY_KNOW);
    }

    /**
     * This object's flags reduced to what the player has actually learned — the port of C's
     * {@code object_flags_known} ({@code obj-util.c}).
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
     * <p>An object with no kind returns after the intersection, so only the known flags it really
     * has survive, with nothing added back.
     *
     * <p>Function flagsKnown commented in full on 261002.
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
     * <p>The name is built by dropping the {@code STAT_} prefix and adding {@code OM_}. The two
     * sentinels follow the same rule: {@code STAT_NONE} and {@code STAT_MAX} resolve to
     * {@code OM_NONE} and {@code OM_MAX}, which exist and which the item-preparation code never
     * fills, so they answer zero and do not throw. C would read outside the array for {@code STAT_MAX}.
     *
     * <p>Function getModifierValue commented in full on 260820, sentinel note corrected on 261002.
     *
     * @param stat one of the five real stats; the {@code STAT_NONE} and {@code STAT_MAX} sentinels
     *             are accepted and answer zero
     * @return the object's modifier for that stat, or zero if it carries none
     * @throws IllegalArgumentException if the stat has no correspondingly named modifier, which
     *                                  none of the current {@code Stats} values lacks
     */
    public int getModifierValue(Stats stat) {
        return getModifierValue(ObjectModifier.valueOf("OM_" + stat.name().substring(5)));
    }

    /**
     * The object's value for one modifier — C's {@code obj->modifiers[om]}.
     *
     * <p>Raw, and not the whole story where the player's knowledge matters: {@code calcBonuses}
     * multiplies every modifier it reads by the player's rune knowledge for it
     * (in {@code player-calcs.c}), so a value returned here may still contribute nothing.
     * A modifier the object does not carry reads as zero, matching C's zeroed array, and so does
     * every modifier on an item whose map is {@code null}, because the read goes through
     * {@link #getModifiers()}.
     *
     * <p>Function getModifierValue commented in full on 260820, null-map note and the C line
     * numbers corrected on 261002.
     *
     * @param om the modifier to read
     * @return the object's value for it, or zero
     */
    public int getModifierValue(ObjectModifier om) {
        return this.getModifiers().getOrDefault(om, 0);
    }

    /**
     * The weight of a single one of these, after its curses have had their say — the port of C's
     * {@code object_weight_one} ({@code obj-util.c}).
     *
     * <p>One, not the stack: a pile of twenty arrows answers with the weight of one arrow. Callers
     * wanting the burden of the stack multiply by the count themselves, as C does.
     *
     * <p>Curses can make an item heavier or lighter, and they compose: each curse of non-zero power
     * is applied in turn to the running result, so two weight curses both take effect rather than
     * the last one winning. A curse present at zero power is skipped — it is recorded on the object
     * but not active. The base weight is floored at zero before any curse sees it.
     *
     * <p>The curses are applied in ascending curse index, as C's loop over its curse array does, and
     * that order matters because the weight changes do not commute (adding ten then doubling is not
     * doubling then adding ten). Nothing here sorts: {@link #getCurses()} walks a {@link TreeMap}
     * ordered by {@link #CURSE_ORDER}, so the order is the map's own whatever order the curses were
     * added in. C starts its loop at index 1 because slot 0 of its array is a placeholder; the port
     * numbers its curses from 0 and has no placeholder, so it skips nothing.
     *
     * <p>Each step goes through {@link Curse#modifyWeightForCurse(int)}, which clamps to
     * [0, {@link Short#MAX_VALUE}], so a cursed weight stays inside C's {@code int16_t} range. With
     * no active curse the answer is the base weight floored at zero, and an item whose curse map was
     * never created reads as having none, because {@link #getCurses()} answers an empty map.
     *
     * <p>Function objectWeightOne coded on 260820, commented in full on 261002, curse order
     * corrected on 261008.
     *
     * @return this object's individual weight in tenth-pounds, never negative
     */
    public int objectWeightOne() {
        int result = Math.max(weight, 0);

        for (Map.Entry<Curse, CurseData> curse : getCurses().entrySet())
            if (curse.getValue().getPower() != 0)
                result = curse.getKey().modifyWeightForCurse(result);

        return result;
    }

    /**
     * Counts how many times an inscription fragment occurs in this object's note - the port of C's
     * {@code check_for_inscrip} ({@code obj-util.c}). Callers use it as a yes/no test:
     * a non-zero answer means the tag is present.
     *
     * <p>Occurrences may overlap, because the scan resumes one character past the start of each
     * match rather than past the whole of it - C's {@code s++}. So {@code "!!!"} holds {@code "!!"}
     * twice.
     *
     * <p>An object with no note, and an empty or null fragment, count as zero rather than failing.
     * C has no such guard for an empty fragment, where {@code strstr} would match at every position;
     * no caller passes one.
     *
     * <p>Function checkForInscription coded before 260822, commented in full on 261002.
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
            // s++ in check_for_inscrip (obj-util.c).
            location = result;
            result = note.indexOf(s, location) + 1;
        }

        return count;
    }

    /**
     * Puts a yes/no question to the player about this object - the port of C's
     * {@code verify_object} ({@code obj-util.c}).
     *
     * <p>The object is described with prefix, combat values and extra detail, and appended to the
     * caller's prompt, so {@code "Really take off and drop"} becomes
     * {@code "Really take off and drop a Long Sword (+3,+4)? "}. The question goes out through
     * {@code GameInputHolder}, which is the boundary the middle layer asks the player through.
     *
     * <p>The three description flags are C's {@code ODESC_PREFIX | ODESC_FULL}, with {@code ODESC_FULL}
     * written out as {@code ODESC_COMBAT} and {@code ODESC_EXTRA} because the port's
     * {@link ObjectDescription} has no combined constant. C builds the name into an 80-character
     * buffer and the prompt into a 160-character one, truncating either; the port does not truncate.
     *
     * <p><b>Outstanding:</b> {@link #description} is still a stub that returns a placeholder tag, so
     * until the object description code lands in Chapter 7 the prompt carries that tag in place of the
     * object's name.
     *
     * <p>Function verifyObject coded before 260822, commented in full on 261002.
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
     * {@code ignore_type_of} ({@code obj-ignore.c}).
     *
     * <p>The quality mapping table is searched for the first entry matching this object's tval. An
     * entry may narrow that further with an identifier, which has to match the kind's name - that
     * is how, say, diggers are split out from the other tools sharing their tval.
     *
     * <p>The first match wins, so the order of the table matters: a Sword named for Chaos is a
     * {@code ITYPE_GREAT} because that row comes before the plain sword row, while any other sword
     * is {@code ITYPE_SHARP}. The identifier is a substring test on the kind's name, not an
     * equality, as C's {@code strstr}. The table is in {@code ObjectInfo} and keeps C's order.
     *
     * <p>Reads the kind's name only for a row that has an identifier, so an object with no kind
     * throws there, where C would dereference null.
     *
     * <p>Function getIgnoreTypeOf coded on 260822, commented in full on 261002.
     *
     * @return the matching {@link IgnoreType}, or {@link IgnoreType#ITYPE_MAX} if the object is not
     * subject to quality ignoring at all
     */
    public IgnoreType getIgnoreTypeOf() {
        for (ObjectInfo.QualityMapping mapping : ObjectInfo.qualityMapping) {
            if (mapping.tval() == gettValue()) {
                // Is there a matching identifier
                if (!mapping.identifier().isEmpty()) {
                    if (!getKind().getName().contains(mapping.identifier())) {
                        continue;
                    }
                }
                return mapping.ignoreType();
            }
        }

        return IgnoreType.ITYPE_MAX;
    }

    /**
     * Reports whether this item is an ego item - the port of C's truth test on {@code obj->ego}.
     *
     * <p>Asks about the item's <em>real</em> ego, not whether the player has learned it. An
     * artifact is not an ego item, because an item holds one or the other.
     *
     * <p>{@link #ignoreLevelOf} reads it to grade a fully known ego item {@code IGNORE_ALL}.
     *
     * <p>Function isEgo coded before 261002, commented in full on 261002.
     *
     * @return {@code true} if this item is an ego item
     */
    public boolean isEgo() {
        return ego != null;
    }

    /**
     * Answers whether this item's ego is marked ignorable under one category - the port of C's
     * {@code ego_is_ignored(obj->ego->eidx, type)} ({@code obj-ignore.c}).
     *
     * <p>Reads the item's <em>real</em> ego, while its one caller gates the question on the
     * <em>known</em> one. That split is C's and is deliberate: an ego the player has not yet learned
     * must not make the item disappear.
     *
     * <p>C keeps the marks in a table indexed by ego and category; here each {@link EgoItem} holds
     * its own, so the answer is the same for every item sharing that ego. An item with no ego is
     * never ignored this way: C has no such case to answer, because its caller has already tested
     * {@code obj->known->ego}, so the {@code null} test is a guard the port adds. The caller here
     * is {@code ObjectIgnore}, which makes the same known-ego test first.
     *
     * <p>Function egoIsIgnored commented in full on 261002.
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
     * {@code ignore_level_of} ({@code obj-ignore.c}). The caller compares the answer against
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
     * {@link QualityValueEnum#IGNORE_MAX}. That override applies only to an object the player knows
     * in full. An object not yet fully known is {@code IGNORE_ALL} if it has been assessed, unless it
     * is an artifact, and {@code IGNORE_MAX} otherwise, so that an unfinished identification can
     * never lose the player something good.
     *
     * <p>Worked through: a ring with a known {@code +2} to-hit is average, one with a known
     * {@code -1} to-AC and nothing positive is bad, and one with every known figure at zero is
     * average. A fully known Dagger, whose kind rolls no bonuses, is average at {@code +0,+0}, good
     * at {@code +0,+1} and bad at {@code -1,+0} (to-hit, to-damage); an ego one is {@code IGNORE_ALL} whatever its
     * bonuses.
     *
     * <p>Function ignoreLevelOf coded on 260822, commented in full on 261002.
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
     * C's {@code is_object_good} ({@code obj-ignore.c}).
     *
     * <p>Weighted rather than counted: to-damage is worth four, to-hit two and to-armour one, so a
     * weapon is judged mostly on the bonus that matters for a weapon. A positive answer means good,
     * negative means bad, zero means average, and {@link #ignoreLevelOf()} turns that into a quality
     * band.
     *
     * <p>Reads this item's own bonuses and its kind's dice, never the known half's, because
     * {@link #ignoreLevelOf()} has already insisted the item is fully known before it asks. Each
     * figure contributes only its sign, so a to-damage of {@code +9} counts four and a to-AC of
     * {@code +1} counts one. With every floor at zero, a {@code +1} to-damage with a to-hit of
     * {@code -1} nets four minus two, which is good.
     *
     * <p>Throws for an item with no kind, where C would dereference null.
     *
     * <p>Function isGood commented in full on 261002.
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
     * {@code cmp_object_trait} ({@code obj-ignore.c}).
     *
     * <p>The kind's minimum is clamped to zero first, so an item is never judged good merely for
     * failing to be as negative as it might have been.
     *
     * <p>The floor is the kind's dice at their minimum, evaluated at level zero, so a kind that
     * rolls {@code -2} at worst puts the floor at {@code -2} and a kind that rolls {@code +3} at
     * worst puts it at zero. A bonus of {@code -2} on the first is average and on the second is bad;
     * {@code 0} is good on the first and average on the second.
     *
     * <p>Function compareObjectTrait commented in full on 261002.
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
     * {@code object_is_in_quiver} ({@code obj-gear.c}).
     *
     * <p>Compares by identity, not equality: two identical stacks of arrows are still different
     * stacks, and the question is about this one. Every slot of the quiver is checked, empty ones
     * included, and an empty slot never matches because this object is never {@code null}.
     *
     * <p>The answer decides which stacking limits apply when two stacks are merged, since the quiver
     * caps a slot more tightly than the pack does: callers turn it into
     * {@code OSTACK_QUIVER} or not before asking {@link #mergeable}.
     *
     * <p>Function objectIsInQuiver coded before 260827, commented in full on 261002.
     *
     * @param player the player whose quiver to search
     * @return {@code true} if this exact object sits in a quiver slot
     */
    public boolean objectIsInQuiver(Player player) {
        for (ItemObject item : player.getPlayerUpkeep().getQuiver()) {
            if (item == this) return true;
        }

        return false;
    }

    /**
     * Tests whether {@code toMerge} could be folded into this stack in its entirety - the port of
     * C's {@code object_mergeable} ({@code obj-pile.c}).
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
     * <p>The maximum and the ammunition test are both read from this object, as C reads them from
     * its first argument. The two objects will be of one kind and type by the time the answer
     * matters, but only {@link #similar} establishes that, and it runs afterwards.
     *
     * <p>Function mergeable coded on 260822, commented in full on 261002.
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
                if (this.gettValue().isAmmo()) {
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
     * {@code object_stackable} ({@code obj-pile.c}).
     *
     * <p>{@link #similar} settles everything about the objects themselves; this adds the
     * inscription rule. Two objects are compatible when either is uninscribed, or when both carry
     * the same inscription - an uninscribed item takes on whatever the stack it joins is called,
     * but two differently inscribed stacks stay apart so the player's tags survive.
     *
     * <p>Inscriptions are compared as text. C compares the quarks with {@code ==}, which comes to
     * the same thing because the quark table never holds one string twice.
     *
     * <p>{@link #similar} refreshes {@link #player} as it runs, so this does too.
     *
     * <p>Function objectStackable coded on 260822, commented in full on 261002.
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
     * {@code object_absorb} ({@code obj-pile.c}).
     *
     * <p>The counts are added, capped at the kind's {@code max_stack}, and everything else that has
     * to travel between the two is handled by {@link #objectAbsorbMerge}. The absorbed object is
     * then disposed of, along with its known half: excised from whatever pile holds it, delisted
     * from the player's cave object list, and deleted.
     *
     * <p>C reads its {@code player} global throughout. The port refreshes {@link #player} from
     * {@link GameState#getPlayer()} first, so the cave it excises from and deletes through is the
     * live player's, never a snapshot from when this item was built. The absorbed object itself is
     * deleted through the current level's cave, with the player's cave passed as its view.
     *
     * <p>The excise is skipped for a known object at the origin, because a zero grid means it is
     * not on the floor to be excised from - C's {@code loc_is_zero}, which compares coordinates.
     * {@link #getGrid()} answers {@link Loc#zero} for an item with no grid, so the test never meets
     * a {@code null}. The port must compare coordinates too: {@code Loc.zero} is one particular
     * instance, and an independently constructed {@code Loc(0, 0)} is a different object with the
     * same value.
     *
     * <p>Deleting the absorbed object is what removes it from the player's gear, so this must be
     * reached; a caller that leaves it out ends up with the emptied stack still in the pack at its
     * old count.
     *
     * <p>Function objectAbsorb coded on 260822, corrected on 261002 to refresh the player, commented
     * in full on 261002, grid note added on 261007.
     *
     * @param toAbsorb the stack to fold in; it does not survive the call
     */
    public void objectAbsorb(ItemObject toAbsorb) {
        ItemObject known = toAbsorb.getKnown();

        int total = this.number + toAbsorb.number;

        this.number = Math.min(total, this.getKind().getBase().getMaxStack());

        player = GameState.getPlayer();
        
        this.objectAbsorbMerge(toAbsorb, player, true);
        if (known != null) {
            Chunk cave = player.getCave();
            if (cave != null && known.getGrid() != null && !known.getGrid().isZero())
                cave.getSquare(known.getGrid()).pileExcise(known);
            if (cave != null) {
                cave.delistObject(known);
                cave.objectDelete(null, known);
            }
        }
        GameState.getCave().objectDelete(player.getCave(), toAbsorb);
    }

    /**
     * Returns the live brand set, not a copy, matching how C hands out {@code obj->brands} — an
     * array on the struct that callers read and write in place.
     *
     * <p>The brands here are the ones the item actually has, each at its own strength. That is a
     * different question from whether the player can read them, which is
     * {@link KnownObject#brandIsKnown} and is not per-item at all.
     *
     * <p>An immutable empty set while the field is {@code null}, which takes no writes; use
     * {@link #appendBrand}, {@link #removeBrand} and {@link #clearBrands} to change the brands.
     *
     * <p>Function getBrands coded before 260817, commented in full on 261002.
     *
     * @return this item's brands, shared with this instance
     */
    public @NotNull Set<Brand> getBrands() {
        if (brands == null)
            return Set.of();
        return brands;
    }

    /**
     * Forgets this item's known half without disturbing the known object itself - the port of C's
     * {@code obj->known = NULL}.
     *
     * <p>Used where an object is about to be absorbed or deleted and its knowledge has already been
     * dealt with separately; clearing the link first stops the disposal from following it a second
     * time. C does this to the absorbed stack only, in {@code combine_pack} ({@code obj-gear.c}),
     * after absorbing the known halves. {@code ObjectUtils} calls it on the absorbed stack when it
     * combines two pack stacks, once the known object has been removed from the player's known
     * gear. It also calls it on that known object, whose own {@code known} link is already
     * {@code null}, so that call changes nothing.
     *
     * <p>Function nullKnown coded before 260827, commented in full on 261002, caller note corrected
     * on 261007.
     */
    public void nullKnown() {
        this.known = null;
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
    public @NotNull Set<Slay> getSlays() {
        if (slays == null) {
            return Set.of();
        }
        return slays;
    }

    /**
     * Splits a number of items off this stack into a new one - the port of C's {@code object_split}
     * ({@code obj-pile.c}).
     *
     * <p>The new stack is a copy of this one, so it carries the same kind, bonuses, flags and
     * inscription; what it does not carry is a share of the counts and charges, which are handed
     * over afterwards. Charges are distributed with {@code destNew} set, because the destination is
     * brand new and should take its share rather than add to one.
     *
     * <p>The known halves are split alongside. This stack's known count is set to its own before
     * anything is copied, because {@code distributeCharges} divides by the count it finds, and the
     * known halves' counts are written to match again at the end, so that knowledge and truth do
     * not drift apart over the split. C also zeroes the {@code oidx} item-list index of both new
     * objects; the port carries no such index.
     *
     * <p>Refuses to split off the whole stack or more: C asserts on it, and a caller wanting all of
     * it should move the stack rather than split it.
     *
     * <p>Function objectSplit coded before 260827, corrected on 261002 to align the known count
     * first, commented in full on 261002.
     *
     * @param amount how many items to move to the new stack
     * @return the new stack, holding {@code amount} items
     * @throws IllegalArgumentException if {@code amount} is not fewer than this stack holds
     */
    public ItemObject objectSplit(int amount) {
        ItemObject destination;

        // Get a copy of the object, pass in true to ensure the known is copied once
        if (this.getKnown() != null)
            this.getKnown().setNumber(this.getNumber());
        destination = this.copy(true);

        // Check legality
        if (this.getNumber() <= amount) {
            String message = "Invalid amount passed to objectSplit. Was: " + amount + " should " +
                    "have been less than " + this.getNumber();
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
     * Carries everything except the counts across from one stack to another - the port of C's
     * {@code object_absorb_merge} ({@code obj-pile.c}, where it is {@code static}). Shared by the
     * whole and partial absorbs.
     *
     * <p>Knowledge first: when both objects have a known half, and the absorbed one's known half
     * has an effect, the surviving known half takes this object's real effect, and the player is
     * told about the object again, which is how learning one stack teaches the other. The direction
     * matters - what is written into the known object is the surviving object's reality, never the
     * absorbed object's knowledge.
     *
     * <p>An inscription on the absorbed stack carries over, replacing any note this stack had; the
     * stacking rules guarantee the two do not conflict. Like C, the port tests only that the absorbed
     * note exists, not that it has text, so an empty inscription on the absorbed stack replaces a
     * real one.
     *
     * <p>Charges and timeouts are pooled only when the caller asks: rod timeouts add, and wand,
     * staff and gold values add up to {@code MAX_PVAL}. A partial absorb passes {@code false} for
     * anything but money, because the charges have already been shared out by
     * {@code distributeCharges}. Origins are combined last.
     *
     * <p>The player is a parameter, where C reads its global, so the caller decides whose
     * knowledge is updated; {@link #objectAbsorb} passes the refreshed player.
     *
     * <p>Function objectAbsorbMerge coded on 260822, commented in full on 261002, note rule
     * corrected on 261003.
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
            PlayerKnowledge.knowObject(player, this);
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

    /**
     * Prices a stack as the player would see it - the port of C's {@code object_value}
     * ({@code obj-power.c}).
     *
     * <p>Which of the three pricing routes is taken depends on what the player is entitled to know.
     * An object whose type has variable power ({@link TValue#hasVariablePower()}: weapons, armour,
     * lights, jewellery and ammunition) is priced from its <em>known</em> half through
     * {@link #objectValueReal}, so an unidentified sword is not priced as the fine one it may turn
     * out to be. A flavoured type whose flavour the player has learned is priced in full from the
     * object itself. Anything else gets {@link #objectValueBase}'s flat figure multiplied by the
     * count.
     *
     * <p>The routes are tried in that order, and a variable-power object with no {@code known}
     * half falls past the first test. No variable-power type can have a flavour, so it lands on the
     * base route. That route answers the kind's listed cost when the kind is aware, and a kind with
     * no flavour is made aware at start-up ({@code ObjectUtils.flavourInit}, as C's
     * {@code flavor_init} does), so a sword with no known half is priced at its kind's cost, not at
     * zero. Zero is reached only for an unaware kind of a type {@link #objectValueBase} does not
     * list, such as a special artifact kind before the player has learned it. C's
     * {@code tval_can_have_flavor_k} takes the kind's type where this reads
     * the object's own {@link #tValue}; the two agree because an object copies its type from its
     * kind. Unlike C, which would stop on a kindless object, {@link #flavourIsAware()} answers
     * {@code false} for one, though {@link #objectValueBase} then throws.
     *
     * <p>Read by {@link #earlierObject} to order stock by price, and by the shop and wizard-mode
     * code C routes through {@code object_value}. It is {@code public}, as C's is, so that the
     * store code can call it; {@link #earlierObject} is its only caller so far.
     *
     * <p>Function objectValue coded before 260827, commented in full on 261007, visibility note
     * corrected on 261008.
     *
     * @param quantity how many items are being priced
     * @return the price of the stack in gold
     */
    public int objectValue(int quantity) {
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
     * {@code object_value_base} ({@code obj-power.c}, a {@code static} function there).
     *
     * <p>An object whose flavour is known is worth its kind's listed cost, whatever its type. One
     * that is not is worth a flat figure for its type: 5 for food and mushrooms, 20 for potions and
     * scrolls, 45 for rings and amulets, 50 for wands, 70 for staves and 90 for rods. The player
     * knows roughly what an unidentified rod is worth without knowing which rod it is. A type not
     * listed is worth nothing, but only while its kind is unaware. A wearable's kind has no flavour
     * and is made aware at start-up, so the aware branch answers first for every wearable that is
     * not jewellery; the zero applies to an unaware kind of an unlisted type, such as a special
     * artifact kind before it is learned.
     *
     * <p>Awareness goes through {@link #objectFlavourIsAware()}, which throws for a kindless
     * object as C's assertion does, so the kind is dereferenced safely on the aware branch. The
     * switch has no null guard: an object with no type would throw, where C's switch falls through
     * to zero.
     *
     * <p>Only {@link #objectValue} calls it, and it multiplies the result by the count.
     *
     * <p>Function objectValueBase coded before 260827, commented in full on 261007.
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
     * ({@code obj-power.c}).
     *
     * <p>Two routes, chosen by whether the type's worth varies with its properties
     * ({@link TValue#hasVariablePower()}).
     *
     * <p><b>Variable-power objects</b> are priced from {@code #objectPower}, through the quadratic
     * {@code power * (power * a + b)} with {@code a = 1} and {@code b = 5}. The quadratic is what
     * makes a strong object worth disproportionately more than a middling one, rather than merely
     * proportionately more. A negative power - a cursed object - is priced by the mirror of the same
     * curve, {@code -power * (power * a - b)}, which is negative for a single item. Zero power
     * gives zero.
     *
     * <p>The overflow checks around each multiply are C's, kept rather than replaced by wider
     * arithmetic so that the saturating behaviour matches: a price too large to represent becomes
     * {@code Integer.MAX_VALUE}, and one too negative becomes {@code Integer.MIN_VALUE}, not a
     * wrapped value. The coefficients are locals here because C has them as locals too, with the
     * same comment that both must stay non-negative, so the branches that apply when {@code a} is
     * zero cannot be reached with the values in force.
     *
     * <p>Expendables are then divided down by {@link ObjectRegistry#AMMO_RESCALER}, C's
     * {@code AMMO_RESCALER} of 20: ammunition, and any light that burns out and has no ego, are
     * consumed, so are not worth what their power suggests. The division truncates toward zero, as
     * C's does. A price that is then exactly zero is lifted to one so that a cheap-but-real object
     * such as a cloak is not worthless; a negative price is not touched. The stack total is
     * {@code value * quantity}, floored at zero, so a cursed stack is never worth less than
     * nothing - the negative single-item price survives only inside the calculation.
     *
     * <p><b>Fixed-price objects</b> take the kind's listed cost, and a kind that costs nothing
     * returns zero at once. A wand or staff is charged extra for the charges it carries: its
     * {@link #pValue} is shared out per item as {@code pValue * quantity / number}, rounded up
     * when the division is inexact, and each charge adds one twentieth of the kind's cost. The
     * total is floored at zero. A kindless object is priced at zero here where C would dereference
     * a null kind.
     *
     * <p>{@link #objectValue} calls it on the known half or on the object itself, and
     * {@code PlayerBirth} calls it directly to charge the player for their starting kit, as
     * {@code player-birth.c} does.
     *
     * <p>Function objectValueReal coded before 260827, commented in full on 261002.
     *
     * @param quantity how many items are being priced
     * @return the price of the stack in gold, never negative
     */
    public int objectValueReal(int quantity) {
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
     * ({@code obj-power.c}).
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
     * beyond what should exist, matching C - there is no point pricing the rest of it. They follow
     * the blows, shots and might steps, and the value returned is the running total at that point,
     * not a capped one.
     *
     * <p>The multiplier returned by the extra-might step is assigned and then unused, as in C, where
     * it is a by-value argument that goes no further.
     *
     * <p>C's running log of each step is written through {@code log_obj} to a file chosen by the
     * caller. The port logs the same lines at info level instead, so {@code logFileName} is only
     * handed on to {@link #cursePower(int, boolean, String)}, which hands it on to the scratch
     * copies it prices; nothing in the chain opens a file.
     *
     * <p>Function objectPower commented in full on 261002.
     *
     * @param verbose     {@code true} to log the brand and slay breakdown as well as the running
     *                    totals
     * @param logFileName C's log file name, kept for the signature; no file is written
     * @return this object's power
     */
    private int objectPower(boolean verbose, String logFileName) {
        // Get all the attack power
        int power = toDamagePower();
        int dicePower = damageDicePower();
        power += dicePower;
        if (dicePower != 0) logger.info("total is {}", power);
        power += ammoDamagePower(power);
        int mult = bowMultiplier();
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
     * Adjusts power for an object that is heavier or lighter than its kind - the port of C's
     * {@code nonstandard_weight_power} ({@code obj-power.c}).
     *
     * <p>Only curses can produce the difference: the object's own {@link #weight} is the standard
     * figure, floored at zero, and {@link #objectWeightOne()} is what the curses have made of it.
     * Equal figures return the power untouched. A negative figure from {@code objectWeightOne} is
     * impossible; C asserts on it and the port logs and throws a {@link RuntimeException}.
     *
     * <p>Two separate adjustments, and an object can take both. An object with no base armour class
     * is judged on carrying capacity - lighter is better, because it leaves room for something else.
     * That adjustment is one point for each {@code WGT_POWER_DEN_NOBASEAC} (50) tenth-pounds, the
     * difference divided with Java's truncating integer division exactly as C divides. An object
     * with the {@code THROWING} flag is judged the other way, because a heavier missile hits harder:
     * the two weights are each divided by {@code WGT_POWER_DEN_THROW} (12) <em>before</em> they are
     * subtracted, so the rounding of each is separate, and the difference is multiplied by
     * {@code WGT_POWER_NUM_THROW} (15). Both products are clamped to the {@code int} range, and the
     * two are summed and added to the power with the saturating adds. Objects that do provide base
     * armour are skipped for the first: {@link #acPower} has already accounted for their weight, and
     * charging twice would be wrong.
     *
     * <p>Flags are merged from the object and its active curses first, because a curse can be what
     * makes the object throwable in the first place. A curse recorded at power zero is not active
     * and adds none.
     *
     * <p>C's comment lists what is deliberately not modelled: blows, heavy-wield status, criticals
     * and shield bashes all move with weight and none of them are priced here.
     *
     * <p>Function nonStandardWeightPower commented in full on 261002.
     *
     * @param power the running power total
     * @return the total with any weight adjustment applied
     * @throws RuntimeException if {@link #objectWeightOne()} is negative
     */
    private int nonStandardWeightPower(int power) {
        int standardWeight = Math.max(getWeight(), 0);
        int nonStandardWeight = objectWeightOne();
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
                    flags.union(c.getItemObject().getFlags());
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
            if (adjustWC >= 0) {
                adjustWC = (adjustWC < Integer.MAX_VALUE / ObjectRegistry.WGT_POWER_NUM_NOBASEAC) ?
                        adjustWC * ObjectRegistry.WGT_POWER_NUM_NOBASEAC : Integer.MAX_VALUE;
            } else {
                adjustWC = (adjustWC > Integer.MIN_VALUE / ObjectRegistry.WGT_POWER_NUM_NOBASEAC) ?
                        adjustWC * ObjectRegistry.WGT_POWER_NUM_NOBASEAC : Integer.MIN_VALUE;
            }

            logger.info("Add {} power for non-standard weight of object not  " +
                    "affecting base armour.", adjustWC);
            adjustment = Guards.addGuardI(adjustment, adjustWC);
        }

        // Objects with the "THROWING" flag increase damage with increasing weight
        if (flags.has(ObjectFlag.OF_THROWING)) {
            int adjustThrow = ((nonStandardWeight / ObjectRegistry.WGT_POWER_DEN_THROW)
                    - (standardWeight / ObjectRegistry.WGT_POWER_DEN_THROW));
            if (adjustThrow >= 0) {
                adjustThrow = adjustThrow < Integer.MAX_VALUE / ObjectRegistry.WGT_POWER_NUM_THROW ?
                        adjustThrow * ObjectRegistry.WGT_POWER_NUM_THROW : Integer.MAX_VALUE;
            } else {
                adjustThrow = adjustThrow > Integer.MIN_VALUE / ObjectRegistry.WGT_POWER_NUM_THROW ?
                        adjustThrow * ObjectRegistry.WGT_POWER_NUM_THROW : Integer.MIN_VALUE;
            }

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
     * Adjusts power for the curses on this object - the port of C's {@code curse_power}
     * ({@code obj-power.c}).
     *
     * <p>Two passes, because curses come in two kinds and the second kind cannot be priced
     * individually. A curse recorded at power zero is not active and is skipped by both.
     *
     * <p>An ordinary curse - one with no weight effect, meaning an additive weight of zero or a
     * {@code MULTIPLY_WEIGHT} factor of exactly 100 - is priced on its own, by running the whole
     * power calculation over it. The result is then reduced by a tenth of the power the curse has on
     * this object (integer division), and the figures are summed into one total, {@code q}.
     *
     * <p>A weight-affecting curse cannot be priced that way, because weight interacts with
     * everything else the object does. Those are priced by difference instead: the object is copied
     * with {@link #copy(boolean)}, all its curses applied with
     * {@link #applyCurseAttributes(Curse)} and then cleared so they are not priced twice, and the
     * copy priced; then copied again with one curse held back, and priced again. The gap between
     * the two, found with the saturating subtract, is that curse's contribution. Where the gap is
     * negative - the curse makes the object worse - it is multiplied by the curse's power on this
     * object held between 20 and 100, then divided by 100. That keeps between a fifth and all of the
     * penalty, so a curse that is hard to remove counts for more than one that is easy to shed. A
     * gap that is not negative is used as it stands. The contributions join {@code q} with the
     * saturating add.
     *
     * <p>Splitting them keeps the answers identical to the previous version of the algorithm for the
     * common case, which is C's stated reason for not treating all curses the way the second pass
     * treats these. The log text from C's {@code log_obj} is written at info level.
     *
     * <p>Both passes walk the curses in C's order. The {@link #curses} map is a {@code TreeMap}
     * under {@link #CURSE_ORDER}, so iterating it gives ascending curse index whatever order the
     * curses were added in. C's loop starts at slot 1 because slot 0 is a placeholder; the port's
     * registry has no placeholder, so nothing is skipped.
     *
     * <p>Function cursePower commented in full on 261007.
     *
     * @param power       the running power total
     * @param verbose     {@code true} to log the breakdown
     * @param logFileName C's log file name, kept for the signature and handed on; no file is written
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

                if (c.getItemObject().getFlags().has(ObjectFlag.OF_MULTIPLY_WEIGHT)) {
                    if (c.getItemObject().getWeight() != 100) {
                        weightAffecting = true;
                        continue;
                    }
                } else {
                    if (c.getItemObject().getWeight() != 0) {
                        weightAffecting = true;
                        continue;
                    }
                }

                logger.info("Calculating {} curse power...", c.getName());
                cursePower = c.getItemObject().objectPower(verbose, logFileName);
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

                    if (c.getItemObject().getFlags().has(ObjectFlag.OF_MULTIPLY_WEIGHT)) {
                        if (c.getItemObject().getWeight() == 100) continue;
                    } else {
                        if (c.getItemObject().getWeight() == 0) continue;
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
     * Empties this object's slays - the port of C's {@code mem_free(obj_local.slays)} in
     * {@code curse_power}.
     *
     * <p>Called on the scratch copies the curse pricing builds, once they have been priced. In C
     * this returns memory; Java's collector does that, so the call changes nothing the pricing can
     * see and is kept to mirror the C. It assigns a fresh empty set rather than null, which keeps
     * the accessors' distinction between "no collection" and "an empty one" pointing the right way,
     * and never touches the set the copy was made from, because {@link #copy(boolean)} gave the
     * scratch copy its own.
     *
     * <p>Function freeSlays commented in full on 261002.
     */
    private void freeSlays() {
        this.slays = new HashSet<>();
    }

    /**
     * Empties this object's brands - the port of C's {@code mem_free(obj_local.brands)}, the
     * counterpart of {@link #freeSlays()}, and used in the same place for the same reason: it
     * mirrors C's memory release and is harmless on a scratch copy.
     *
     * <p>Function freeBrands commented in full on 261002.
     */
    private void freeBrands() {
        this.brands = new HashSet<>();
    }

    /**
     * Moves as much of one stack onto another as the limits allow, leaving both alive - the port of
     * C's {@code object_absorb_partial} ({@code obj-pile.c}).
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
     * the pack must be strictly below the kind's {@code max_stack}, C's {@code assert(size <
     * max_stack)}, so a size equal to it throws. These are impossible states rather than
     * conditions to recover from: returning quietly would leave the caller believing a split had
     * happened when the counts were never touched.
     *
     * <p>Charges are distributed before the counts change, since
     * {@code distributeCharges} works from the number moving.
     *
     * <p>Function objectAbsorbPartial coded on 260822, corrected on 260824, commented in full on
     * 260824, C line number removed on 261003, max-stack bound corrected on 261007.
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
        player = GameState.getPlayer();

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
     * Folds every active curse's attributes into this object - the port of C's
     * {@code apply_curse_attributes} ({@code obj-curse.c}).
     *
     * <p>Called on a scratch copy by the curse pricing, which then prices the merged object as a
     * whole. One curse may be held back, which is how the pricing takes the difference a single
     * curse makes; passing {@code null} merges them all. An object with no curses is left as it is:
     * C returns early only on a {@code NULL} curse array, while the port also returns on an empty
     * map, which changes nothing because C's pass over an all-zero array would find no active curse
     * and its clean-up loop nothing to flatten.
     *
     * <p>The curses are applied in ascending registry index, which is C's order and matters because
     * the weight changes do not commute. {@link #getCurses()} walks a {@link TreeMap} ordered by
     * {@link #CURSE_ORDER}, so that order needs no sorting here. Each active curse - present, with a power other than zero -
     * first changes the weight through {@link Curse#modifyWeightForCurse(int)}. Its to-armour,
     * to-hit and to-damage figures are then added to the object's through the saturating 16-bit
     * add, so a long chain cannot wrap round; its flags are unioned in; and its modifiers are added
     * to the object's, again saturating, a modifier the object lacks being taken from the curse
     * as it stands.
     *
     * <p>C also adds the curse object's base armour class. {@code curse.txt} has no way to set one,
     * so it is always zero and the port has nothing to add; if the data file ever gains one, this
     * method and {@link Curse} both need it. Brands, slays and the curse list are left alone: C
     * leaves the last for the caller to clear, which is what {@link #freeCurses()} is for.
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
     * resistance level zero, which is C's default and means no change. Only the resistance level is
     * merged; the curse's hates and ignores flags are not, as C's header says.
     *
     * <p>An element the object holds at a level the merge does not expect - 2, say, which is none
     * of immune, resistant, both-at-once, vulnerable or none - is impossible, and the port logs and
     * throws where C asserts.
     *
     * <p>The modifier and element maps are written to in place, which is safe because the pricing
     * calls this only on a {@link #copy(boolean)}; on an object whose maps were never created the
     * accessors answer immutable empties and the modifier write would fail.
     *
     * <p>The held-back curse is matched by curse index, as C's {@code j == i} does, so a different
     * {@link Curse} instance carrying the held-back curse's index is held back too. A {@code null}
     * argument holds nothing back.
     *
     * <p>Function applyCurseAttributes coded before 261007, commented in full on 261007, curse order
     * and empty-map notes added on 261008.
     *
     * @param curseToIgnore the one curse to leave out, or {@code null} to merge them all
     * @throws RuntimeException if an element is held at an impossible resistance level
     */
    private void applyCurseAttributes(Curse curseToIgnore) {
        if (getCurses() == null || getCurses().isEmpty()) {
            // no curses - nothing to merge
            return;
        }

        for (Curse curse : getCurses().keySet()) {
            if (curseToIgnore != null && curse.getIndex() == curseToIgnore.getIndex()) continue;

            if (getCurses().get(curse).getPower() == 0)
                continue;

            // C reads curses[i].obj; the port's curse holds that object, so read it through
            // curse.getItemObject() below. The weight change goes through the curse itself.
            this.setWeight(curse.modifyWeightForCurse(this.getWeight()));

            // Curses can adjust the ac, hit and dam modifiers
            this.setToAC(Guards.addGuardI16(this.getToAC(), curse.getItemObject().getToAC()));
            this.setToHit(Guards.addGuardI16(this.getToHit(), curse.getItemObject().getToHit()));
            this.setToDam(Guards.addGuardI16(this.getToDam(), curse.getItemObject().getToDam()));

            // The curse may extend the objects flags - C's of_union(obj->flags, curse_obj->flags).
            // setFlags is the named mutator for that, and unions into the real set. getFlags() must
            // NOT be used here: it hands back a copy, so unioning into it would build the merged set
            // and then throw it away, leaving this object's flags untouched and the curse silently
            // unapplied - a mistake the compiler cannot catch, which prices the object as though the
            // curse carried no flags at all.
            this.setFlags(curse.getItemObject().getFlags());

            // The curses modifiers combine additively with those from this object;
            for (ObjectModifier om : curse.getItemObject().getModifiers().keySet()) {
                if (this.getModifiers().containsKey(om)) {
                    this.getModifiers().put(om, Guards.addGuardI16(this.getModifiers().getOrDefault(om, 0),
                            curse.getItemObject().getModifiers().getOrDefault(om, 0)));
                } else {
                    this.getModifiers().put(om, curse.getItemObject().getModifiers().getOrDefault(om, 0));
                }
            }

            // Resistances combine with standard logic for combining them.
            for (ElementEnum elem : ElementEnum.values()) {
                if (elem == ElementEnum.ELEM_MAX || elem == ElementEnum.ELEM_NONE) continue;
                ElementInfo curseElInfo = curse.getItemObject().getElInfo().getOrDefault(elem, null);
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
     * Adds power for what this object does when used - the port of C's {@code effects_power}
     * ({@code obj-power.c}).
     *
     * <p>An object's own activation is worth its activation's power; failing that, the kind's power
     * stands in, which is how an ordinary wand or staff is priced for what it casts. Only one of the
     * two counts, never both, and a figure of zero adds nothing and logs nothing.
     *
     * <p>The guard asks whether there is an activation <em>at all</em>. C tests a single pointer,
     * which a zeroed object leaves null. The port holds a list, so the equivalent question is whether
     * the list exists, is not empty and has a first entry; a list that is null or empty is the shape
     * an object with no activation has, and treating either as an activation would throw and hide the
     * fallback. Only the first entry is priced, because C's pointer names one activation.
     *
     * <p>C dereferences {@code obj->kind} without a test. The port skips the fallback for an item with
     * no kind, which therefore prices at zero here.
     *
     * <p>Function effectsPower coded before 260827, commented in full on 261002.
     *
     * @param power the running power total
     * @return the total with any activation power added
     */
    private int effectsPower(int power) {
        int q = 0;

        if (activation != null && !activation.isEmpty() && activation.getFirst() != null)
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
     * Adds power for this object's elemental protections - the port of C's {@code element_power}
     * ({@code obj-power.c}).
     *
     * <p>Two things at once, and the order matters. Walking the elements prices each one on its own -
     * ignoring, resisting, being immune to or being vulnerable to it - and at the same time counts
     * how many fall into each of the combination rows. Only when that walk is finished are the
     * combination bonuses added, because a count read part-way through is not the object's.
     *
     * <p>An immunity is priced as immunity plus resistance, because it subsumes the resistance it
     * replaces. Ignoring an element and being at a resistance level are separate tests, so one
     * element can score for both. A resistance level of 2 matches none of the three level tests and
     * scores nothing on its own.
     *
     * <p>The rows are immunities, low resists and high resists. A row scores its factor times the
     * square of its count once the count passes one, and a flat bonus when the count reaches the
     * row's size. A level-3 element also counts in the low-resists row, because the row asks for a
     * level of at least 1. The immunities row's bonus is the inhibit figure, so immunity to all four
     * basic elements is refused outright.
     *
     * <p>An element the object says nothing about is skipped. That matches C, where a zero entry
     * satisfies neither the ignore test nor any of the three level tests, and cannot reach a
     * combination row either, because every row demands a level above zero.
     *
     * <p>Each row of the element-power table names its element, where C relies on the row's position
     * matching the element's index; the two tables list the same thirteen elements in the same order.
     *
     * <p>The combination rows are shared mutable state, zeroed here before use; see
     * {@link ElementSet}.
     *
     * <p>Function elementPower coded before 260827, commented in full on 261002.
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
     * Adds power for this object's flags - the port of C's {@code flags_power}
     * ({@code obj-power.c}).
     *
     * <p>Each flag is looked up in the object property table and priced at its base power times the
     * multiplier for this object's type, because the same flag is worth different amounts on
     * different things. A type the table names no multiplier for gets 1. A flag the table prices at
     * zero is a derived one and adds nothing to the sum, but it still counts towards its family.
     *
     * <p>As with the elements, the walk both prices individual flags and counts them into families,
     * and the family bonuses are added only once the walk is done. The families are sustains,
     * protections and miscellaneous abilities; a family scores its factor times the square of its
     * count once the count passes one, and a flat bonus when the count reaches the family's size.
     *
     * <p>The flags are those of the item itself, read from a copy. The flags its curses add are not
     * folded in here, because C's {@code object_flags} is a plain copy and the curses are priced
     * separately by {@link #cursePower(int, boolean, String)}.
     *
     * <p>A flag the property table does not know is a data error rather than a runtime condition, so
     * this throws rather than skipping it, where C asserts.
     *
     * <p>The family rows are shared mutable state, zeroed here before use; see {@link FlagSet}.
     *
     * <p>Function flagsPower coded before 260827, commented in full on 261002.
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
     * Adds power for this object's modifiers - the port of C's {@code modifier_power}
     * ({@code obj-power.c}).
     *
     * <p>Each modifier is priced at its value times its base power times the multiplier for this
     * object's type. A modifier the object does not carry reads as zero, which is what C's fixed
     * array gives and what the {@code getOrDefault} here stands in for. A negative modifier prices
     * negatively. A modifier the property table does not know is a data error, so this throws where
     * C asserts.
     *
     * <p>Separately, the modifiers accumulate a weighted total - each value times the property's own
     * multiplier, so not all of them count equally - and a large total buys a further bonus from the
     * ability table, or a refusal if it is large enough. A total above 249 adds the inhibit figure;
     * otherwise a positive total is divided by ten to index the table, whose first seven entries are
     * zero, so a total under 70 adds nothing and a zero or negative total is not looked up at all.
     * That is what stops an object with many strong modifiers being priced as merely the sum of
     * them.
     *
     * <p>Function modifierPower coded before 260827, commented in full on 261002.
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
            logger.info("Inhibiting - Total ability bonus of {} is too high", extraStatBonus);
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
     * ({@code obj-power.c}).
     *
     * <p>A ring or amulet is worth something for being one, before anything it does is counted. The
     * bonus is {@code BASE_JEWELRY_POWER}, 4, and the test is {@link TValue#isJewellery()}.
     *
     * <p>Function jewelleryPower coded before 260827, commented in full on 261002.
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
     * Adds power for this object's to-armour bonus - the port of C's {@code to_ac_power}
     * ({@code obj-power.c}).
     *
     * <p>Priced in bands rather than linearly: every point is worth the base rate, points above the
     * high threshold are worth it again, and points above the very high threshold twice again - so
     * a large bonus is worth disproportionately more than a small one. A bonus at or above the
     * inhibit threshold is refused outright rather than priced.
     *
     * <p>The thresholds are 26, 36 and 56. The base term is the bonus times {@code TO_AC_POWER}
     * halved, which with the constant at 2 is the bonus itself; above 26 each point over 25 adds
     * twice that again, above 36 each point over 35 adds four times it, and from 56 the inhibit
     * figure is added on top. A bonus of +30 therefore prices at 40 and one of +56 at 20202. A
     * negative bonus scores only the base term, and so prices negatively.
     *
     * <p>A zero bonus returns early, which keeps the log clean rather than changing the answer.
     *
     * <p>Function toAcPower coded before 260827, commented in full on 261002.
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
     * Adds power for this object's base armour class, adjusted for weight - the port of C's
     * {@code ac_power} ({@code obj-power.c}).
     *
     * <p>An object with base armour is worth a flat bonus for being armour at all,
     * {@code BASE_ARMOUR_POWER}, plus a figure for the armour itself, scaled by how much armour it
     * gives per unit of weight. Light armour is therefore worth more than heavy armour of the same class,
     * which is the point.
     *
     * <p>The armour figure starts as the base class times {@code BASE_AC_POWER} halved. It is then
     * multiplied by 750 times base class plus to-armour bonus, divided by the weight, and divided by
     * a hundred, each division truncating. The scaling is capped at 450, explicitly so as not to
     * overprice elven cloaks, which give a good deal of armour for almost no weight; the cap has no
     * floor, so a large enough negative bonus scales the figure below zero. A weightless object
     * cannot be scaled at all and takes a fixed multiple of five instead. Base class 10 at weight
     * 100 prices at 8: the flat 1, plus 10 scaled by 75 per cent and truncated to 7.
     *
     * <p>The weight used is {@link #objectWeightOne()}, so curses that make the object heavier or lighter
     * are already reflected; that is also why {@link #nonStandardWeightPower(int)} skips objects
     * with base armour, having been accounted for here. The weight is read before the test for base
     * armour, where C reads it inside, which makes no difference because reading it has no effect.
     *
     * <p>Function acPower coded before 260827, commented in full on 261002.
     *
     * @param power the running power total
     * @return the total with the base armour terms added
     */
    private int acPower(int power) {
        int weight = objectWeightOne();
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
            logger.info("Add {} power for AC per unit weight, now {}", q, power);
        }

        return power;
    }

    /**
     * Adds power for this object's to-hit bonus - the port of C's {@code to_hit_power}
     * ({@code obj-power.c}).
     *
     * <p>Linear, unlike the to-armour term: every point is worth the same. The rate is halved, which
     * is why the constant is doubled and the expression divides by two. The division truncates
     * towards zero, so +1 prices at 1, +2 at 3 and -1 at -1.
     *
     * <p>Unlike the to-armour term there is no early return for a zero bonus. The log line is written
     * when the running <em>total</em> is nonzero, not when the term is, as in C.
     *
     * <p>Function toHitPower coded before 260827, commented in full on 261002.
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
     * Divides a launcher's power down so it can be compared with a melee weapon's - the port of C's
     * {@code rescale_bow_power} ({@code obj-power.c}).
     *
     * <p>The damage terms above assume a melee weapon landing {@code MAX_BLOWS} blows a turn. A
     * launcher does not, so its total is divided by the same figure, 5; without it every bow would
     * outprice every sword. The division truncates towards zero, so a total of -7 becomes -1.
     *
     * <p>Applies to whatever is worn in the shooting slot, which is how the test is phrased rather
     * than by asking whether the object is a bow.
     *
     * <p>The shooting slot is looked up on the live player from {@link GameState#getPlayer()}, as C
     * reads its {@code player} global at the call, and the lookup is made for every object, not only
     * bows. It therefore needs a player to exist, and throws if the player's body has no slot named
     * {@code shooting}. The slot this object would occupy comes from {@link #wieldSlot()}, which
     * refreshes {@link #player} itself, so both sides of the comparison see the live player.
     *
     * <p>Function rescaleBowPower coded before 260827, commented in full on 261002.
     *
     * @param power the running power total
     * @return the total, rescaled if this object is worn in the shooting slot
     */
    private int rescaleBowPower(int power) {
        if (wieldSlot() == ObjectUtils.slotByName(GameState.getPlayer(), "shooting")) {
            power /= ObjectRegistry.MAX_BLOWS;
            logger.info("Rescaling bow power, total is {}", power);
        }

        return power;
    }

    /**
     * Adds power for this object's brands and slays - the port of C's {@code slay_power}
     * ({@code obj-power.c}).
     *
     * <p>Priced from the <em>best</em> brand or slay rather than the sum of them, because only one
     * applies to any given blow. That best figure is a percentage-style number where 100 means "no
     * better than a bare weapon", so subtracting 100 is what turns it into a bonus - and what lets a
     * weak brand price negatively. The best figure itself starts at 1 rather than 0, as C's
     * {@code best_power} does, so a member scoring below 100 prices negatively against the 100
     * baseline; only a best of 1 would take the full penalty of 99. With the shipped
     * {@code brand.txt} and {@code slay.txt} every member scores at least 101, so the start value
     * never becomes the result for a non-empty set.
     *
     * <p>The result is scaled by the damage dice squared and divided by 2500, truncating, so the
     * same brand is worth far more on a heavy weapon than on a light one.
     *
     * <p>Carrying several then buys further bonuses - separately for slays, for brands, for having
     * both, and for kills, which are slays with a multiplier above three and counted apart from
     * them. Holding a complete set of any of the three buys a flat bonus on top: 10 for eight
     * slays, 20 for five brands, 20 for three kills.
     *
     * <p>Returns early when there is nothing to price, which is the common case.
     *
     * <p>Function slayPower coded before 260827, commented in full on 261007.
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
     * Applies extra shooting might to the running total - the port of C's
     * {@code extra_might_power} ({@code obj-power.c}).
     *
     * <p>Might multiplies rather than adds: it adds to the launcher's multiplier, and the whole
     * damage total so far is multiplied by the result. That is why this step comes after the damage
     * terms and before the brand and slay pricing. An object with no might still multiplies by its
     * launcher multiplier, which is one for anything that is not a bow.
     *
     * <p>Might at or above the inhibit threshold refuses the object instead of pricing it, adding
     * {@code INHIBIT_POWER} and returning at once with the multiplier untouched.
     *
     * <p>Returns both figures because the caller keeps the multiplier as well; C passes it by value
     * and returns only the power, having no need of it afterwards.
     *
     * <p>Function extraMightPower coded before 260827, commented in full on 261002.
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
     * Applies extra shots to the running total - the port of C's {@code extra_shots_power}
     * ({@code obj-power.c}).
     *
     * <p>Proportional rather than additive: each point of the shots modifier raises the total by a
     * tenth of itself, truncating, because shots multiply everything the launcher already does. The
     * log reports ten times the modifier as the percentage.
     *
     * <p>Shots at or above the inhibit threshold refuse the object. Negative shots are not handled,
     * as C's own comment says: they leave the total as it was.
     *
     * <p>Function extraShotsPower coded before 260827, commented in full on 261002.
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
     * Applies extra blows to the running total - the port of C's {@code extra_blows_power}
     * ({@code obj-power.c}).
     *
     * <p>Two parts. The total is scaled by {@code (MAX_BLOWS + blows) / MAX_BLOWS}, truncating, and
     * then a flat amount is added - {@code NONWEAP_DAMAGE} times the blows times half of
     * {@code DAMAGE_POWER} - which C labels a boost for assumed off-weapon damage, standing for
     * damage the player deals that does not come from the weapon.
     *
     * <p>Blows at or above the inhibit threshold refuse the object. A negative figure goes through
     * the same scaling and takes power away.
     *
     * <p>Function extraBlowsPower coded before 260827, commented in full on 261007.
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
     * Prices ammunition for the launcher that will fire it - the port of C's
     * {@code launcher_ammo_damage_power} ({@code obj-power.c}).
     *
     * <p>A missile is worth little on its own and a great deal once launched, so its total is
     * multiplied by the assumed launcher multiplier for its type and then divided by twice
     * {@code MAX_BLOWS}, truncating, which rescales it against a melee weapon's blows. Ego
     * ammunition additionally takes the launcher's assumed to-damage bonus before that, at half of
     * {@code DAMAGE_POWER} a point.
     *
     * <p>The stored multiplier is doubled, which is why the divisor is twice the assumed blows; see
     * {@link Archery}. The row comes from {@code ObjectRegistry.archery}, keyed by this object's
     * type, where C indexes by the type's distance from {@code TV_SHOT}.
     *
     * <p>Function launcherAmmoDamagePower coded before 260827, commented in full on 261002.
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
     * Reports the damage multiplier a launcher gives - the port of C's {@code bow_multiplier}
     * ({@code obj-power.c}).
     *
     * <p>Anything that is not a bow multiplies by one, which lets the caller apply the result
     * unconditionally. For a bow the multiplier is its {@code pval}, as in C.
     *
     * <p>The method name has its letters transposed, which is worth knowing when searching for
     * callers.
     *
     * <p>Function bowMultiplier coded before 260827, commented in full on 261002.
     *
     * @return the launcher's multiplier, or 1 for anything that is not one
     */
    private int bowMultiplier() {
        int mult = 1;

        if (gettValue() != TValue.TV_BOW)
            return mult;
        else
            mult = getpValue();

        logger.info("Base mult for this weapon is {}", mult);
        return mult;
    }

    /**
     * Prices a launcher for the ammunition it will fire - the port of C's
     * {@code ammo_damage_power} ({@code obj-power.c}).
     *
     * <p>The mirror of {@link #launcherAmmoDamagePower(int)}: a bow does no damage by itself, so it
     * is priced by what its ammunition is assumed to average, at half of {@code DAMAGE_POWER} a
     * point of that average. Which ammunition that is comes from the launcher's kind flags, since a
     * sling, a bow and a crossbow take different things, tested in the order shots, arrows, bolts.
     *
     * <p>Applies to anything {@link #wieldSlot()} sends to the shooting slot, worn or not. Returns
     * an increment rather than a new total, which is why the caller adds it on. The player is
     * refreshed from {@link GameState#getPlayer()} first, so the slot comparison sees the live
     * body.
     *
     * <p>An object with no kind answers zero, where C would dereference it.
     *
     * <p>Function ammoDamagePower coded before 260827, commented in full on 261002.
     *
     * @param power the running power total, used only for the log line
     * @return the power to add for the ammunition this launcher fires, or zero
     */
    private int ammoDamagePower(int power) {
        int q = 0;
        TValue shoots = null;

        if (this.getKind() == null) return 0;
        ObjectKind kind = this.getKind();

        player = GameState.getPlayer();

        if (wieldSlot() == ObjectUtils.slotByName(player, "shooting")) {
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
     * Prices what this object's damage dice are worth - the port of C's
     * {@code damage_dice_power} ({@code obj-power.c}).
     *
     * <p>A melee weapon or a missile is priced from its dice, as
     * {@code dice * (sides + 1) * DAMAGE_POWER / 4}: the sides plus one is twice the average roll,
     * and the rate is half of {@code DAMAGE_POWER}, which together make the average damage times
     * the rate.
     *
     * <p>Anything else that {@link #wieldSlot()} does not send to the shooting slot can still be
     * worth a damage term, if it carries something that makes the player's other attacks better - a
     * brand, a slay, or a blows, shots or might modifier above zero. Such an object is credited with
     * {@code WEAP_DAMAGE * DAMAGE_POWER} instead, because there are no dice to price. A launcher
     * takes neither branch, and its term is zero.
     *
     * <p>Returns the dice term alone rather than a running total; the caller adds it on and keeps it,
     * because the brand and slay pricing needs it later. The player is refreshed from
     * {@link GameState#getPlayer()} first, so the slot comparison sees the live body.
     *
     * <p>Function damageDicePower coded before 260827, commented in full on 261002.
     *
     * @return the damage-dice term for this object
     */
    private int damageDicePower() {
        int dice = 0;
        player = GameState.getPlayer();

        // Add damage from dice for any wearable weapon or ammo
        if (this.gettValue().isMeleeWeapon() || this.gettValue().isAmmo()) {
            dice = ((this.damageDice * (this.damageSides + 1) * ObjectRegistry.DAMAGE_POWER) / 4);
            logger.info("Add {} power for damage dice, ", dice);
        } else if (wieldSlot() != ObjectUtils.slotByName(player, "shooting")) {
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
     * Adds power for this object's to-damage bonus - the port of C's {@code to_damage_power}
     * ({@code obj-power.c}).
     *
     * <p>The first lot is half of {@code DAMAGE_POWER} a point, truncating. An object that is
     * neither a melee weapon, nor ammunition, nor sent to the shooting slot by {@link #wieldSlot()}
     * takes a second lot at the full {@code DAMAGE_POWER} a point, as C does, so a point of
     * to-damage on a ring is priced at three times a point on a weapon.
     *
     * <p>The player is refreshed from {@link GameState#getPlayer()} before the slot is asked for,
     * so the comparison sees the live body.
     *
     * <p>Function toDamagePower coded before 260827, commented in full on 261002.
     *
     * @return this object's to-damage term
     */
    private int toDamagePower() {
        int power = (this.toDam * ObjectRegistry.DAMAGE_POWER / 2);
        if (power != 0)
            logger.info("{} power from to_dam", power);

        player = GameState.getPlayer();
        // Add second lot of damage power for non weapons
        if ((this.wieldSlot() != ObjectUtils.slotByName(player, "shooting"))
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
     * Reports which equipment slot this object would be worn in - the port of C's
     * {@code wield_slot} ({@code obj-gear.c}).
     *
     * <p>The object's type picks a slot type: a bow, amulet, cloak, shield, pair of gloves or pair
     * of boots has one of its own, a melee weapon, ring or light is sent to the weapon, ring or light
     * slot, body armour to the body slot and a helm or crown to the head slot. Every one of them is
     * then resolved by {@link ObjectUtils#slotByType}, asking for an <em>empty</em> slot. Where a
     * body has several slots of a type, as it does of rings, the first empty one is answered, and
     * the first of that type if all are occupied.
     *
     * <p>Answers a slot index rather than a slot, which is what the callers compare against
     * {@code slotByName}. There are two different answers for "nowhere". A type that maps to no slot
     * type at all, a potion say, answers {@code -1}, as C's {@code wield_slot} does. A type that
     * maps to a slot type the body has none of answers the slot count, one past the last valid
     * index, which is {@code slotByType}'s own "not found". A caller that goes on to index the body
     * has to reject both.
     *
     * <p>The player is refreshed from {@link GameState#getPlayer()} on every call, standing in for C's
     * {@code player} global, so an item built before a character exists, or before the character
     * changed, is still asked about the live body. With no character at all the lookup throws a
     * {@code NullPointerException} where C would crash on its null global.
     *
     * <p>Function wieldSlot commented in full on 260827, corrected on 261002 to refresh the player,
     * rewritten in full on 261002.
     *
     * @return the index of the slot this object would occupy, {@code -1} if its type is never worn,
     * or the body's slot count if the body has no slot of the type it needs
     */
    public int wieldSlot() {
        player = GameState.getPlayer();
        switch (this.gettValue()) {
            case TV_BOW:
                return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_BOW, false);
            case TV_AMULET:
                return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_AMULET, false);
            case TV_CLOAK:
                return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_CLOAK, false);
            case TV_SHIELD:
                return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_SHIELD, false);
            case TV_GLOVES:
                return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_GLOVES, false);
            case TV_BOOTS:
                return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_BOOTS, false);
        }

        if (this.gettValue().isMeleeWeapon())
            return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_WEAPON, false);
        else if (this.gettValue().isRing())
            return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_RING, false);
        else if (this.gettValue().isLight())
            return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_LIGHT, false);
        else if (this.gettValue().isBodyArmour())
            return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_BODY_ARMOR, false);
        else if (this.gettValue().isHeadArmour())
            return ObjectUtils.slotByType(player, EquipmentSlotsEnum.EQUIP_HAT, false);

        // No slots available
        return -1;
    }

    /**
     * Answers whether the player has learned what this object's flavour means - the port of C's
     * {@code object_flavor_is_aware} ({@code obj-knowledge.c}).
     *
     * <p>Awareness lives on the kind, not the object: learning that one blue potion is cure light
     * wounds teaches the player about every blue potion.
     *
     * <p>Throws for an object with no kind, where {@link #flavourIsAware()} answers {@code false}
     * for the same state. The two differ because this one is called where a kind must exist and a
     * missing one is a defect rather than a case. Both ports are of the one C function, which
     * asserts a kind; this is the strict reading of that assertion.
     *
     * <p>Function objectFlavourIsAware commented in full on 261002. The previous comment named
     * {@code obj-desc.c} as the C file; the function is in {@code obj-knowledge.c}.
     *
     * @return {@code true} if the player is aware of this object's flavour
     */
    boolean objectFlavourIsAware() {
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
     * <p>Delegates to {@link ObjectKind#canBrowse()}, since browsability is a property of the book
     * rather than the copy. That walks the class's books and needs both the type and the sub-type to
     * match, and reads the live player through {@link GameState#getPlayer()} at the moment of the
     * call, as C's {@code obj_kind_can_browse} reads its {@code player} global.
     *
     * <p>Read by {@link #earlierObject}, which lists readable books first in a player's pack and
     * does not ask at all when ordering a store's stock. An object with no kind throws a
     * {@code NullPointerException} here, where C would dereference null.
     *
     * <p>Function canBrowse commented in full on 260827, rewritten in full on 261002.
     *
     * @return {@code true} if the current player's class can browse this object
     */
    private boolean canBrowse() {
        return this.getKind().canBrowse();
    }

    /**
     * Returns an independent copy of this item - the port of C's {@code object_copy}
     * ({@code obj-pile.c}).
     *
     * <p>Deep-copied because their contents are mutable: the flag and notice sets, the modifier map,
     * the element info (each entry copied in turn), the curse map (each {@code CurseData} rebuilt),
     * the dice, and the brand and slay sets where they exist. The modifier and element maps are
     * rebuilt as insertion-ordered {@link LinkedHashMap}s and the curse map through
     * {@link #cursesFactory()}, so the copy walks them in the same order as the original.
     *
     * <p>Shared deliberately: the kind, ego and artifact templates, which C shares as pointers and
     * which every item built on them points at; the origin race, for the same reason - identity is
     * what tells two origins apart, so copying it would make two items from the same monster look
     * like items from different ones. The {@code effect} list and the {@code activation} list are
     * shared too, as C's {@code memcpy} copies the {@code struct effect *} and
     * {@code struct activation *} pointers and {@code object_copy} never duplicates the chain
     * behind them, and so is the {@code effectMessage} string, which is immutable.
     *
     * <p>Null is preserved rather than normalised for the brand, slay and curse collections, because
     * elsewhere the class distinguishes "no collection" from "an empty one" - the accessors answer
     * an immutable empty collection for the former, which takes no writes.
     *
     * <p>The known half is copied only when asked for, and then without its own known half. C's
     * {@code object_copy} is a {@code memcpy}, so it always copies the {@code known} pointer and the
     * copy aliases the original's known object; callers there either overwrite it at once, copy an
     * object that is itself a known half (whose pointer is null), or never read it. The port
     * instead gives {@code copy(false)} a null known half, which is safer than an alias and gives
     * the same answer at every call site that exists. A caller splitting a stack passes
     * {@code true}, as does the object power code that works on a scratch copy; a caller copying a
     * known half passes {@code false}.
     *
     * <p>Not carried across, because the port has no such fields: C's {@code prev} and {@code next}
     * pile pointers, which {@code object_copy} sets to null, and {@code oidx}, which it copies.
     * {@code object_copy_amt}, the variant that also sets the count and shares out charges, has no
     * port yet; C uses it only in the store code, which belongs to Chapter 8.
     *
     * <p>Function copy coded before 260827, commented in full on 261002, map order and shared
     * effect notes added on 261007.
     *
     * @param includingKnown {@code true} to copy the known half as well
     * @return a new item that shares no mutable state with this one, bar the noted templates
     */
    public ItemObject copy(boolean includingKnown) {
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
        if (this.baseDamage == null)
            copy.baseDamage = null;
        else
            copy.baseDamage = this.baseDamage.copy();
        copy.baseAC = this.baseAC;
        copy.toAC = this.toAC;
        copy.toDam = this.toDam;
        copy.toHit = this.toHit;
        Flag<ObjectFlag> oFlags = new Flag<>(ObjectFlag.class);
        oFlags.copyFrom(this.flags);
        copy.flags = oFlags;
        Map<ObjectModifier, Integer> newMods = new LinkedHashMap<>();
        for (ObjectModifier mod : this.getModifiers().keySet()) {
            newMods.put(mod, this.getModifiers().get(mod));
        }
        copy.modifiers = newMods;
        Map<ElementEnum, ElementInfo> eeMap = new LinkedHashMap<>();
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
            TreeMap<Curse, CurseData> newCurses = cursesFactory();
            for (Curse curse : curses.keySet()) {
                CurseData data = curses.get(curse);
                newCurses.put(curse, new CurseData(data.getPower(), data.getTimeout()));
            }
            copy.curses = newCurses;
        }
        copy.effect = this.effect;
        copy.effectMessage = this.effectMessage;
        copy.activation = this.activation;
        if (this.time == null)
            copy.time = Random.Zero();
        else
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
     * Builds a stripped-down name for a kind - the port of C's {@code object_kind_name}
     * ({@code obj-desc.c}).
     *
     * <p>An unaware flavoured kind answers with the bare flavour text, so an unidentified potion
     * reads as its colour rather than its effect. Everything else answers with the kind's own
     * name, run through {@link #objDescNameFormat} with no modifier and in the singular, which
     * strips the {@code &} article marker and resolves any {@code ~} or {@code |x|y|} in the
     * template.
     *
     * <p>{@code easyKnow} forces the identified name regardless of awareness. C's callers choose it
     * by what they are showing, not by a property of the object: the message tags and the wizard-mode
     * object picker pass {@code true}, the ignore menus pass the kind's own awareness, and the
     * knowledge menu passes the {@code cheat_xtra} option. The only caller here is
     * {@link #printCustomMessage}, which always passes {@code true}, so the flavour-text branch is
     * reached only by the unit tests until the ignore and knowledge menus are ported. The method is
     * {@code public}, as C's is, ready for them.
     *
     * <p>Note this is the kind's name, not an object's: there is no quantity prefix, no ego or
     * artifact name, and no runes.
     *
     * <p>C writes into a caller-supplied buffer and truncates to its size. This version returns a
     * string and so cannot truncate, matching the divergence already recorded on
     * {@link #objDescNameFormat}.
     *
     * <p>Function objectKindName commented in full on 261002; the C callers were corrected on 261002;
     * visibility note added on 261008.
     *
     * @param kind     the kind to name
     * @param easyKnow whether to use the identified name even when the player is unaware
     * @return the flavour text for an unaware flavoured kind, otherwise the formatted kind name
     */
    public String objectKindName(@NotNull ObjectKind kind, boolean easyKnow) {
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
     * behaviour, none of them reachable from the shipped game data (two more, for two {@code ~} in
     * a row and for a {@code ~} inside a bar alternative, follow the list):
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
     * <p>Two {@code ~} in a row take the same exit when pluralising. After the first is replaced the
     * rest of the template is treated afresh, so the second sits at the front of it and is read as a
     * {@code ~} with nothing before it, where C reads the first {@code ~} as the preceding character
     * and writes a bare {@code s}.
     *
     * <p>A {@code ~} inside the alternative that {@code |x|y|} keeps is also read differently. C
     * copies the chosen alternative raw, so the {@code ~} survives into the output, and it never
     * looks at the alternative it drops. Here the {@code ~} pass runs over the whole template
     * before the bar pass, so that {@code ~} is removed when singular and turned into {@code s} or
     * {@code es} when plural. {@code "A|b~|c|"} singular is {@code Ab~} in C and {@code Ab} here;
     * {@code "A|b|c~|"} plural is {@code Ac~} in C and {@code Acs} here. A {@code ~} in the
     * alternative that is dropped changes nothing, because the dropped text is discarded either
     * way. No template in {@code object.txt}, {@code object_base.txt} or C's hard-coded basenames
     * has both a bar and a {@code ~}.
     *
     * <p>The method is {@code public}, as C's is. Its only caller outside the class's own recursion
     * is {@link #objectKindName}, which passes no modifier and the singular, so the {@code #} and
     * plural paths are exercised only by the unit tests until {@code object_desc}, the save code and
     * the object-kind comparison are ported.
     *
     * <p>Function objDescNameFormat commented in full on 261002, bar-alternative note added on
     * 261007, visibility and caller note added on 261008.
     *
     * @param string    the name template to format
     * @param modString the text to substitute for {@code #}, or {@code null} to leave any
     *                  {@code #} in place
     * @param pluralise whether to take the plural form of every {@code ~} and {@code |x|y|}
     * @return the formatted name
     */
    public String objDescNameFormat(@NotNull String string, @Nullable String modString, boolean pluralise) {
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
     * Returns this item's live flag set, unlike {@link #getFlags()}, which hands back a defensive
     * copy — a caller here can mutate the set and reach the item's actual flags.
     *
     * <p>Reading through it skips the copy {@link #getFlags()} makes, which is how
     * {@code ObjectUtils} tests a flag on a known counterpart. It is also the unnamed write path
     * into the flags that {@link #getFlags()} warns about, so prefer {@link #hasFlag} for a single
     * test. {@link #wipe} replaces the set, so a handle taken from here before a wipe no longer
     * reaches the item.
     *
     * <p>Function getObjectFlags coded before 260904, commented in full on 261002.
     *
     * @return this object's flags
     */
    public Flag<ObjectFlag> getObjectFlags() {
        if (flags == null)
            flags = new Flag<>(ObjectFlag.class);
        return flags;
    }

    /**
     * Empties this object's curses - the port of C freeing and nulling {@code obj_local.curses}
     * after {@code apply_curse_attributes} has folded them in, in {@code curse_power}
     * ({@code obj-power.c}).
     *
     * <p>Necessary rather than tidy: the scratch copy has just had every curse's attributes merged
     * into its own, so leaving the curses on it as well would price them twice - the copy's own
     * {@link #cursePower(int, boolean, String)} and {@link #nonStandardWeightPower(int)} would find
     * them and run again. C clears the curses <em>before</em> pricing the copy and frees the brands
     * and slays after, and the caller keeps that order. Assigns an empty map, not null; the original
     * keeps its own, as {@link #copy(boolean)} deep-copied it.
     *
     * <p>Function freeCurses commented in full on 261002.
     */
    private void freeCurses() {
        this.curses = cursesFactory();
    }

    /**
     * Prints a message with the object's own details substituted into it - the port of C's
     * {@code print_custom_message} ({@code obj-util.c}).
     *
     * <p>Messages in the data files are written with tags in braces, so a single line in
     * {@code activation.txt}, {@code artifact.txt} or {@code player_timed.txt} serves whatever
     * object triggers it. The tags are replaced here and the
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
     * <p>A tag is recognised by its opening letters, as in C's {@code msg_tag_lookup}
     * ({@code obj-util.c}), tested in this order: {@code name} (four letters), {@code kind} (four),
     * {@code s} (one), then {@code is} (two). So {@code {names}} is read as {@code {name}},
     * {@code {sx}} and {@code {size}} as {@code {s}}, and {@code {isn}} as {@code {is}}, while
     * {@code {nam}} matches nothing. Whatever the tag, recognised or not, the text resumes just
     * past its closing brace.
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
     * <p>Two divergences from the C, neither reachable from the shipped data files. A tag's letters
     * are tested with {@link Character#isAlphabetic}, which accepts any Unicode letter, where C's
     * {@code isalpha} takes ASCII only. And C builds the message in a 1024-byte buffer and
     * truncates at it, where this builds a string of any length and leaves the cut to
     * {@link Message#messageType}, which makes it at 1023 characters.
     *
     * <p>One more difference is a fault in C. Its {@code {name}} arm gives
     * {@code object_desc} the start of its buffer instead of the write position, and
     * {@code object_desc} begins writing at the start it is given, so text ahead of the tag is
     * overwritten there. Every message in {@code activation.txt} and {@code artifact.txt} that
     * carries {@code {name}} begins with it, which hides the fault. Here the name is appended where
     * the tag stood, as the other three tags are.
     *
     * <p>Function printCustomMessage coded 260829, commented in full on 261002; the truncation note
     * was corrected and the data files and the {@code {name}} arm added on 261002; the tag lookup
     * rewritten for prefix matching on 261003.
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

            String tag = tagSB.toString();

            if (string.charAt(index) == '}') {
                MessageTag mtag = MessageTag.getTag(tag);
                switch (mtag) {
                    case MSG_TAG_NAME -> {
                        Flag<ObjectDescription> descs = new Flag<>(ObjectDescription.class, ObjectDescription.ODESC_PREFIX,
                                ObjectDescription.ODESC_BASE);
                        if (noObject) sb.append("hands");
                        else sb.append(description(descs, player));
                        string = string.substring(index + 1);
                    }
                    case MSG_TAG_KIND -> {
                        if (noObject) sb.append("hands");
                        else sb.append(objectKindName(getKind(), true));
                        string = string.substring(index + 1);
                    }
                    case MSG_TAG_VERB -> {
                        if (!noObject && getNumber() == 1) {
                            sb.append("s");
                        }
                        string = string.substring(index + 1);
                    }
                    case MSG_TAG_VERB_IS -> {
                        if (noObject || getNumber() > 1) sb.append("are");
                        else sb.append("is");
                        string = string.substring(index + 1);
                    }
                    default -> string = string.substring(index + 1);
                }

            }

            next = string.indexOf('{');
        }

        sb.append(string);

        Message.messageType(msgT, "%s", sb.toString());
    }

    /**
     * Resets every field of this object to the blank value C's zero fill leaves, whatever it held.
     *
     * <p>The port of C's {@code object_wipe} ({@code obj-pile.c}), which frees {@code slays},
     * {@code brands} and {@code curses} and then {@code memset}s the whole struct to zero. The port
     * has no manual frees to make — the old collections are simply replaced — and where C's zero
     * fill lands on a collection field, this method assigns a fresh empty collection rather than
     * {@code null}, so callers see "empty" rather than risking a {@code NullPointerException}.
     *
     * <p>The collections are <em>replaced</em>, not cleared in place. A handle taken earlier from
     * {@link #getObjectFlags()} or {@link #getModifiers()} therefore stops reaching the item, and a
     * set or map the item shared with another is left alone. {@code modifiers} and {@code elInfo}
     * become insertion-ordered {@link LinkedHashMap}s, as the constructors build them, so that the
     * order they are walked in does not depend on how their enum keys happen to hash.
     *
     * <p>Where C's zero is not {@code null} the port still lands on it. {@code origin} resets to
     * {@link uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum#ORIGIN_NONE} rather than
     * {@code null}: C's zeroed {@code origin} byte lands on ordinal 0, which is {@code ORIGIN_NONE}
     * in both the C {@code ORIGIN(...)} list ({@code list-origins.h}) and this enum, matching the
     * convention the no-arg {@link #ItemObject()} constructor already uses. {@code tValue} resets to
     * {@link TValue#TV_NONE}, which is C's tval 0.
     *
     * <p>Two fields come back as {@code null} where C's zero is a value. {@code location} is
     * {@code null} where C's grid is (0, 0); {@link #getGrid()} answers {@link Loc#zero} for it, and
     * {@link #objectAbsorb} reads both as "not on the floor". {@code baseDamage} has no C counterpart at all. {@code time} is not one of the two:
     * it comes back as a zero {@link Random}, which is C's four zeros, and {@link #numberCharging}
     * reads that as nothing charging. Unlike a freshly built item, a wiped one has an empty
     * {@code activation} list rather than {@code null}.
     *
     * <p>C's {@code memset} also zeroes {@code prev} and {@code next}, the pile pointers, and
     * {@code oidx}, the item-list index. The port keeps no index, and {@code owningPile} stands for
     * the pointers, so it is reset to {@code null}; the pile itself is not told. {@code player}, the
     * snapshot the port adds, is left as it was.
     *
     * <p>Function wipe coded before 260904, commented in full on 261002; the pile and map notes and
     * the C line number were removed on 261002, the {@code time} note corrected on 261003, the
     * count of null fields corrected on 261007, {@code Loc.zero} read noted on 261007.
     *
     * @author Rowan Crowther
     */
    public void wipe() {
        kind = null;
        ego = null;
        artifact = null;
        known = null;
        location = null;
        tValue = TValue.TV_NONE;
        sValue = 0;
        pValue = 0;
        weight = 0;
        damageDice = 0;
        damageSides = 0;
        baseAC = 0;
        toAC = 0;
        baseDamage = null;
        toDam = 0;
        toHit = 0;
        flags = new Flag<>(ObjectFlag.class);
        modifiers = new LinkedHashMap<>();
        elInfo = new LinkedHashMap<>();
        brands = new HashSet<>();
        slays = new HashSet<>();
        curses = cursesFactory();
        effect = new ArrayList<>();
        effectMessage = null;
        activation = new ArrayList<>();
        time = Random.Zero();
        timeout = 0;
        number = 0;
        notice = new Flag<>(ObjectNotice.class);
        heldMIndex = 0;
        origin = ORIGIN_NONE;
        originDepth = 0;
        originRace = null;
        note = null;
        mimickingMIndex = 0;
        owningPile = null;
    }

    /**
     * Gives this object a fresh, empty curse map, discarding whatever it already held.
     *
     * <p>The port of the allocation branch inside C's {@code copy_curses}
     * ({@code obj-curse.c}), {@code obj->curses = mem_zalloc(z_info->curse_max * sizeof(struct
     * curse_data))}, which C runs only when {@code obj->curses} is still {@code null}. This method
     * has no such guard and always discards what the item held. {@link ObjectUtils#copyCurses} no
     * longer calls it, because that method builds its merged map in a scratch copy and replaces the
     * field with {@link #setCurses}, so no allocation step is needed. It is kept as the public way
     * to empty an item's curse map.
     *
     * <p>An empty map is this port's equivalent of the zeroed array {@code mem_zalloc} hands
     * back: {@link #getCurses()} already reads "no entry" the way C reads a curse slot at power
     * zero, so there is no C-side loop to mirror here. The map is built by {@link #cursesFactory()},
     * so it walks in ascending curse index, as the {@code curses} field requires.
     *
     * <p>Function initCurses coded before 260904, commented in full on 261002; the line numbers were
     * removed on 261002 and the note on {@code copyCurses} corrected on 261003, the ordering note
     * corrected on 261007.
     */
    public void initCurses() {
        curses = cursesFactory();
    }

    /**
     * Sets where this item came from, the port of C's direct field assignment
     * {@code obj->origin = origin}, repeated at each of C's origin-setting call sites (for example
     * {@code place_object} in {@code gen-util.c} on generation, and the item-stealing blow in
     * {@code mon-blows.c}) rather than gathered behind one function.
     *
     * <p>Only the origin is set. {@link #originDepth} and {@link #originRace} have no setter in
     * this class.
     *
     * <p>Function setOrigin coded before 260904, commented in full on 260904, C line numbers removed
     * on 261002.
     *
     * @param objectOriginEnum the new origin
     */
    public void setOrigin(ObjectOriginEnum objectOriginEnum) {
        this.origin = objectOriginEnum;
    }

    /**
     * Attaches the player's known view of this item, the port of C's direct field assignment
     * {@code obj->known = known}, made at each of C's own known-object call sites (for example
     * {@code object_sense}, {@code object_see} and {@code object_grab} in {@code obj-knowledge.c},
     * when a fresh known object is minted) rather than gathered behind one function. See
     * {@link #getKnown()} for what the counterpart holds.
     *
     * <p>Stores the reference; the two items are not linked back, and the previous counterpart, if
     * any, is dropped without being touched.
     *
     * <p>Function setKnown coded before 260904, commented in full on 260904, C line number removed
     * on 261002.
     *
     * @param known the known counterpart to attach, or {@code null} to detach it
     */
    public void setKnown(ItemObject known) {
        this.known = known;
    }

    /**
     * Returns the {@link Pile} this item currently belongs to. The port has no field of C's to
     * read: C answers the question by walking the item's {@code prev} and {@code next} pointers.
     *
     * <p>{@link Pile} keeps this in step as it inserts and removes items, so it answers
     * {@code null} for an item that is in no pile and for one that has been wiped.
     *
     * <p>Function getOwningPile coded before 260904, commented in full on 261002.
     *
     * @return the {@link Pile} this item currently belongs to, or {@code null} if it belongs to
     * none - see {@link #owningPile}
     */
    public Pile getOwningPile() {
        return this.owningPile;
    }

    /**
     * Sets the {@link Pile} this item belongs to - see {@link #owningPile}. Takes {@code null} to
     * record that the item has left every pile.
     *
     * <p>Records the back-reference only and does not add the item to the pile or take it out of
     * its previous one; {@link Pile} calls this as part of its own insert and remove. Calling it
     * from elsewhere leaves the pile and the item disagreeing.
     *
     * <p>Function setOwningPile coded before 260904, commented in full on 261002.
     *
     * @param owner the pile to record as owning this item, or {@code null}
     */
    public void setOwningPile(Pile owner) {
        this.owningPile = owner;
    }

    /**
     * Returns the index of the monster mimicking this item, the port of reading C's
     * {@code obj->mimicking_m_idx}.
     *
     * <p>Zero means the item is not a disguise, and {@link #similar} will not stack one that is.
     * Its partner is {@link #setMimickingMIndex}; this class has no getter for
     * {@link #heldMIndex}.
     *
     * <p>Function getMimickingMIndex coded before 261002, commented in full on 261002.
     *
     * @return the mimicking monster's index, or 0 if this object is not a mimic's disguise
     */
    public int getMimickingMIndex() {
        return mimickingMIndex;
    }

    /**
     * Builds the glyph/colour pair this item is drawn as, the port of calling C's
     * {@code object_char}/{@code object_attr} ({@code ui-object.c}) on the same object and
     * combining the two results. Used today by {@code PlayerCalcs.redrawStuff}'s {@code PR_EQUIP}
     * arm to build the equippy row's payload, one call per equipped item.
     *
     * <p>C's two functions read the {@code kind_x_char}, {@code kind_x_attr}, {@code flavor_x_char}
     * and {@code flavor_x_attr} tables, which start out as the data files' glyphs and colours and
     * which a pref file can then remap. This port has no such tables and reads the parsed data
     * directly, so a remapped glyph is not honoured. An item with no kind throws a
     * {@code NullPointerException}, where C would dereference null.
     *
     * <p>Function getItemObjectADC coded on 260927, commented in full on 261002.
     *
     * @return this item's display glyph and colour
     */
    public AngbandDisplayCharacter getItemObjectADC() {
        char ch = objectKindChar();
        ColourEnum attr = objectKindAttr();
        return new AngbandDisplayCharacter(ch, attr);
    }

    /**
     * Picks this item's display glyph, the port of C's {@code object_char} calling
     * {@code object_kind_char} ({@code ui-object.c}): the flavour's glyph while
     * {@link #useFlavourGlyph()} holds, the kind's own glyph otherwise.
     *
     * <p>The flavour's glyph is the one the {@code kind:} block of {@code flavor.txt} gives, which
     * {@link FlavourKind#getGlyph()} holds once for every flavour of that type, where C copies it
     * onto each flavour. So every potion that is still unidentified is drawn with the same glyph and
     * differs only in colour.
     *
     * <p>Function objectKindChar coded on 260927, commented in full on 261002.
     *
     * @return the glyph this item is drawn as
     */
    private char objectKindChar() {
        return useFlavourGlyph() ? kind.getFlavour().getFlavourKind().getGlyph()
                : kind.getCharacter().getCharacter();
    }

    /**
     * Picks this item's display colour, the port of C's {@code object_attr} calling
     * {@code object_kind_attr} ({@code ui-object.c}): the flavour's colour while
     * {@link #useFlavourGlyph()} holds, the kind's own colour otherwise.
     *
     * <p>The flavour's colour is its own, from its line in {@code flavor.txt}, so it is what tells
     * two unidentified potions apart on screen.
     *
     * <p>Function objectKindAttr coded on 260927, commented in full on 261002.
     *
     * @return the colour this item is drawn in
     */
    private ColourEnum objectKindAttr() {
        return useFlavourGlyph() ? kind.getFlavour().getColour()
                : kind.getCharacter().getAttributeColour();
    }

    /**
     * Decides whether this item should be drawn with its flavour's glyph/colour rather than its
     * kind's own - the port of C's {@code use_flavor_glyph} ({@code ui-object.c}).
     *
     * <p>Matches C's {@code kind->flavor && !(kind->tval == TV_SCROLL && kind->aware)} exactly:
     * a flavoured kind uses its flavour unless it is both a scroll and identified, in which case
     * an aware scroll is shown by its own glyph instead - the one case where being identified
     * turns the flavour glyph back off rather than on. The test reads the kind's type, not the
     * object's, as C does.
     *
     * <p>Function useFlavourGlyph coded on 260927, commented in full on 261002.
     *
     * @return {@code true} if this item's flavour glyph/colour should be used over its kind's own
     */
    private boolean useFlavourGlyph() {
        return kind.getFlavour() != null && !(kind.gettValue().isScroll() && kind.isAware());
    }

    /**
     * Sets the index of the monster this object is mimicking, for a mimic disguised as an item. The
     * port of C's {@code obj->mimicking_m_idx = idx}.
     *
     * <p>Zero means the item is not a disguise. {@link #similar} refuses to stack an item with a
     * non-zero value, and {@link #getMimickingMIndex()} reads it back.
     *
     * <p>Function setMimickingMIndex coded before 260904, commented in full on 261002.
     *
     * @param mimickingMIndex the mimicking monster's index (0 if this object is not a mimic)
     */
    public void setMimickingMIndex(int mimickingMIndex) {
        this.mimickingMIndex = mimickingMIndex;
    }

    /**
     * Prints the message for a modifier the player has just learned by using this item - the port
     * of C's {@code mod_message} ({@code obj-knowledge.c}), which is {@code static} there and
     * {@code public} here so the knowledge code in {@code PlayerKnowledge} can call it.
     *
     * <p>The message depends on both which modifier it is and the sign of this item's value for it.
     * Strength, intelligence, wisdom, dexterity, constitution, stealth, speed, blows and shots each
     * have one line for a positive value ("You feel stronger!") and one for a negative value ("You
     * feel weaker!"); a value of exactly zero prints nothing. Infravision and light are not
     * sign-dependent: "Your eyes tingle." and "It glows!" print whenever the modifier is named, even
     * on an item whose value for it is zero. Every other modifier (searching, tunnelling, might,
     * moves, damage reduction) has no message and prints nothing, as in C's {@code default} case.
     *
     * <p>The value is read through {@link #getModifierValue(ObjectModifier)}, so a modifier the
     * item does not carry reads as zero, as C's zeroed array does, and an item whose
     * {@link #modifiers} map is {@code null} is silent for every sign-dependent modifier instead of
     * throwing. A {@code null} {@code mod} is the port's own addition and returns without printing;
     * C's {@code int mod} cannot be absent.
     *
     * <p>Each line goes out through {@link Message#message}, which C's {@code msg()} corresponds to,
     * so it is logged and signalled as a generic message. Nothing on the item is changed.
     *
     * <p>Function modMessage coded on 261004, commented in full on 261004.
     *
     * @param mod the modifier that was just noticed; {@code null} prints nothing
     */
    public void modMessage(ObjectModifier mod) {
        if (mod == null) return;
        switch (mod) {
            case OM_STR -> {
                if (getModifierValue(ObjectModifier.OM_STR) > 0)
                    Message.message("You feel stronger!");
                else if (getModifierValue(ObjectModifier.OM_STR) < 0)
                    Message.message("You feel weaker!");
            }
            case OM_INT -> {
                if (getModifierValue(ObjectModifier.OM_INT) > 0)
                    Message.message("You feel smarter!");
                else if (getModifierValue(ObjectModifier.OM_INT) < 0)
                    Message.message("You feel more stupid!");
            }
            case OM_WIS -> {
                if (getModifierValue(ObjectModifier.OM_WIS) > 0)
                    Message.message("You feel wiser!");
                else if (getModifierValue(ObjectModifier.OM_WIS) < 0)
                    Message.message("You feel more naive!");
            }
            case OM_DEX -> {
                if (getModifierValue(ObjectModifier.OM_DEX) > 0)
                    Message.message("You feel more dextrous!");
                else if (getModifierValue(ObjectModifier.OM_DEX) < 0)
                    Message.message("You feel clumsier!");
            }
            case OM_CON -> {
                if (getModifierValue(ObjectModifier.OM_CON) > 0)
                    Message.message("You feel healthier!");
                else if (getModifierValue(ObjectModifier.OM_CON) < 0)
                    Message.message("You feel sicklier!");
            }
            case OM_STEALTH -> {
                if (getModifierValue(ObjectModifier.OM_STEALTH) > 0)
                    Message.message("You feel stealthier.");
                else if (getModifierValue(ObjectModifier.OM_STEALTH) < 0)
                    Message.message("You feel noisier.");
            }
            case OM_SPEED -> {
                if (getModifierValue(ObjectModifier.OM_SPEED) > 0)
                    Message.message("You feel strangely quick.");
                else if (getModifierValue(ObjectModifier.OM_SPEED) < 0)
                    Message.message("You feel strangely sluggish.");
            }
            case OM_BLOWS -> {
                if (getModifierValue(ObjectModifier.OM_BLOWS) > 0)
                    Message.message("Your weapon tingles in your hands.");
                else if (getModifierValue(ObjectModifier.OM_BLOWS) < 0)
                    Message.message("Your weapon aches in your hands.");
            }
            case OM_SHOTS -> {
                if (getModifierValue(ObjectModifier.OM_SHOTS) > 0)
                    Message.message("Your missile weapon tingles in your hands.");
                else if (getModifierValue(ObjectModifier.OM_SHOTS) < 0)
                    Message.message("Your missile weapon aches in your hands.");
            }
            case OM_INFRA -> Message.message("Your eyes tingle.");
            case OM_LIGHT -> Message.message("It glows!");
            default -> {
            }
        }
    }

    /**
     * Sets the message shown when this item's effect fires, the port of C's
     * {@code obj->effect_msg = message}.
     *
     * <p>Stores the string as given, {@code null} included, which stands for "no message". A string
     * is immutable, so sharing it is as safe as C's shared {@code char *}. The only production
     * caller is {@code CurseAssembler}, which gives a curse object the message of the curse it
     * stands for. Nothing reads the field back except {@link #copy}; {@code do_curse_effect} in
     * {@code obj-curse.c} will be the first real reader when {@code ObjectUtils.doCurseEffect} is
     * ported.
     *
     * <p>Function setEffectMessage coded before 261007, commented in full on 261007.
     *
     * @param message the effect message, or {@code null} for none
     */
    public void setEffectMessage(String message) {
        this.effectMessage = message;
    }

    /**
     * A running power total and the shooting multiplier that goes with it, returned together by the
     * extra-might step.
     *
     * <p>C's {@code extra_might_power} takes the multiplier as an argument and returns only the
     * power, and {@code object_power} reads the multiplier from {@code bow_multiplier} once and
     * never again. The port's {@link #extraMightPower(PowerAndMult)} instead takes and returns
     * both through this record, and {@link #objectPower(boolean, String)} stores the returned
     * multiplier back into a local that nothing then reads. The multiplier it hands back, the
     * launcher's plus any extra might, is therefore informational. When extra might reaches the
     * inhibit threshold the step adds {@code INHIBIT_POWER} to the power and returns the
     * multiplier unchanged.
     *
     * <p>Record PowerAndMult coded before 260827, commented in full on 261002.
     *
     * @param power the running power total
     * @param mult  the shooting multiplier after any extra might
     */
    private record PowerAndMult(int power, int mult) {
    }
}