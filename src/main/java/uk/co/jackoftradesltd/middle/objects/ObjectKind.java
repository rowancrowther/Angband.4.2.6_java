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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.magic.MagicBook;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.*;
import uk.co.jackoftradesltd.middle.player.Player;

import java.util.*;

/**
 * The template for a kind of object (as loaded from {@code object.txt}) — e.g.
 * "Wooden Torch" or "Long Sword": its base type, combat bonuses and dice,
 * weight/cost, flags/modifiers/element info, brands/slays/curses, allocation
 * parameters, activations/effects, flavour and the player's awareness state. Live
 * items ({@link ItemObject}) reference an {@code ObjectKind}. This is the Java
 * port of the C original's {@code struct object_kind} ({@code src/object.h}).
 *
 * <p>A kind is a recipe, not an instance: the dice-valued fields ({@code pVal}, {@code toH},
 * {@code toD}, {@code toA}, {@code time}, {@code charge}, {@code stackSize}) state a range once, and
 * {@link ObjectUtils#objectPrep} rolls each item's own figure from it. The fields that are the
 * player's rather than the game's ({@code aware}, {@code tried}, {@code ignore}, {@code everseen},
 * and the autoinscriptions) are the ones C also writes to the savefile.
 *
 * <p>Where the port differs in shape from C: the {@code next} link and the {@code k_info} array
 * position are replaced by {@link #getKindIndex}; fixed-length arrays indexed by registry position
 * ({@code brands}, {@code slays}, {@code curses}, {@code modifiers}, {@code el_info}) become sets and
 * maps that hold only what {@code object.txt} names, with the getters answering a zero value for
 * the rest; C's single {@code activation} pointer becomes a list; and C's quark-numbered
 * autoinscriptions become plain strings, {@code null} for none.
 *
 * <p>There are four constructors, one per way C builds a kind. The no-argument one is an empty
 * shell that {@link #copy} fills in. The ten-argument one follows {@code write_book_kind} in
 * {@code init.c}. The long one takes fields the object loader has already resolved
 * ({@code parse_object_*} and {@code finish_parse_object} in {@code obj-init.c}). The artifact one
 * follows {@code write_dummy_object_record} in {@code obj-init.c}.
 *
 * <p>Class ObjectKind coded before 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public class ObjectKind {
    /**
     * The kind's name — C's {@code kind->name}, with the {@code &} article and {@code ~}
     * pluralization markers still in it (see {@link #stripToRawSval}).
     *
     * <p>Field name coded before 261008, commented in full on 261008.
     */
    private String name;
    /**
     * The kind's description — C's {@code kind->text}, built from the {@code desc:} lines of
     * {@code object.txt}. Not the flavour: that is {@link #flavour}, a separate record.
     *
     * <p>Field text coded before 261008, commented in full on 261008.
     */
    private String text;

    /**
     * The base this kind belongs to — C's {@code kind->base}, a pointer into {@code kb_info}. Shared
     * by every kind of the same tval, and the source of the flags and element info a kind inherits.
     *
     * <p>Field base coded before 261008, commented in full on 261008.
     */
    private ObjectBase base;
    /**
     * This kind's position in the registry's kind table — C's {@code kind->kidx}. Assigned by
     * {@link ObjectRegistry#addObjectKind} when the kind is registered; the loader's {@code 0} is
     * a placeholder.
     *
     * <p>Field kindIndex coded before 261008, commented in full on 261008.
     */
    private int kindIndex;

    /**
     * The item type — C's {@code kind->tval}.
     *
     * <p>Field tValue coded before 261008, commented in full on 261008.
     */
    private TValue tValue;
    /**
     * The sub-type by name — the kind's name with the object-name flavour markers stripped
     * ({@link #stripToRawSval}). This is the human-readable reference the data files use; the numeric
     * {@link #sVal} is the resolved index. Kept separate because C's sval is always an int at runtime
     * but a name-or-digit reference in the data files (see {@code lookup_sval}).
     *
     * <p>Field sValueName coded before 261008, commented in full on 261008.
     */
    private String sValueName;

    /**
     * The resolved numeric sub-type value (sval), assigned when the kind is registered under its
     * base (see {@link ObjectRegistry#addObjectKind}) — C's {@code kind->sval}, which is the base's
     * running {@code num_svals} count at the moment the kind is parsed. Svals count from one within
     * each tval, so the number means nothing without the tval beside it.
     *
     * <p>Field sVal coded before 261008, commented in full on 261008.
     */
    private int sVal;

    /**
     * The dice the item's pval is rolled from — C's {@code kind->pval}, from the {@code pval:}
     * line. {@link ObjectUtils#objectPrep} uses it for food, oil, launchers and potions; wands and
     * staves roll their pval from {@link #charge} instead.
     *
     * <p>Field pVal coded before 261008, commented in full on 261008.
     */
    private Random pVal; // Item extra parameter

    /**
     * The dice the to-hit bonus is rolled from — C's {@code kind->to_h}, the first bonus on the
     * {@code attack:} line.
     *
     * <p>Field toH coded before 261008, commented in full on 261008.
     */
    private Random toH;
    /**
     * The dice the to-damage bonus is rolled from — C's {@code kind->to_d}, the second bonus on the
     * {@code attack:} line. Not the weapon's damage dice: those are {@link #damageDice} and
     * {@link #damageSides}.
     *
     * <p>Field toD coded before 261008, commented in full on 261008.
     */
    private Random toD;
    /**
     * The dice the to-armour bonus is rolled from — C's {@code kind->to_a}, from the
     * {@code armor:} line.
     *
     * <p>Field toA coded before 261008, commented in full on 261008.
     */
    private Random toA;

    /**
     * Base armour class — C's {@code kind->ac}, from the {@code armor:} line. A plain number, not
     * dice: only the bonus on top of it varies.
     *
     * <p>Field ac coded before 261008, commented in full on 261008.
     */
    private int ac;
    /**
     * The whole {@code hd} term of the {@code attack:} line as dice. C has no such field: it keeps
     * only the two halves, {@code kind->dd} and {@code kind->ds}, which are {@link #damageDice} and
     * {@link #damageSides} here. This one is stored and copied but nothing reads it back.
     *
     * <p>Field baseDamage coded before 261008, commented in full on 261008.
     */
    private Random baseDamage;
    /**
     * The number of damage dice — C's {@code kind->dd}. A plain count; every item of the kind rolls
     * the same dice.
     *
     * <p>Field damageDice coded before 261008, commented in full on 261008.
     */
    private int damageDice;
    /**
     * The sides on each damage die — C's {@code kind->ds}. See {@link #damageDice}.
     *
     * <p>Field damageSides coded before 261008, commented in full on 261008.
     */
    private int damageSides;
    /**
     * Base weight in tenths of a pound — C's {@code kind->weight}.
     *
     * <p>Field weight coded before 261008, commented in full on 261008.
     */
    private int weight;

    /**
     * Base cost in gold — C's {@code kind->cost}, from the {@code cost:} line. What an aware object
     * is priced at before bonuses are added.
     *
     * <p>Field cost coded before 261008, commented in full on 261008.
     */
    private int cost;

    /**
     * The object flags every item of this kind carries — C's {@code kind->flags}, from the
     * {@code flags:} line of {@code object.txt}. The base's own flags are not in here; C adds those
     * separately at {@code object_prep}.
     *
     * <p>Field flags coded before 261008, commented in full on 261008.
     */
    private Flag<ObjectFlag> flags;
    /**
     * The kind flags ({@code KF_*}) — C's {@code kind->kind_flags}, which control generation and
     * display rather than anything an item has. In C the union of the {@code flags:} line and the
     * base's own kind flags is formed once, in {@code finish_parse_object}; here the loader or
     * constructor that builds the kind is responsible for the base's share.
     *
     * <p>Field kindFlags coded before 261008, commented in full on 261008.
     */
    private Flag<ObjectKindFlag> kindFlags;

    /**
     * The numeric modifiers the kind grants, as dice — C's {@code kind->modifiers}, from the
     * {@code values:} line. Holds only the modifiers the line names; {@link #getModifier} answers a
     * zero value for the rest, as C's zeroed array slots do.
     *
     * <p>Field modifiers coded before 261008, commented in full on 261008.
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * The per-element resist and ignore info — C's {@code kind->el_info}. Holds only the elements that
     * were set: those the kind's {@code flags:} line names, the four base elements for a dungeon
     * book, or a copy of the base's entries for a special artifact kind. {@link #getElInfo} answers
     * a zero value for the rest.
     *
     * <p>Field elInfo coded before 261008, commented in full on 261008.
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
     * The default display glyph and colour — C's {@code kind->d_char} and {@code kind->d_attr}
     * together. A flavoured item is drawn with its flavour's glyph instead.
     *
     * <p>Field character coded before 261008, commented in full on 261008.
     */
    private AngbandDisplayCharacter character;

    /**
     * How common the kind is — C's {@code kind->alloc_prob}, the first number of the {@code alloc:}
     * line. Zero keeps the kind out of ordinary allocation.
     *
     * <p>Field alloc_prob coded before 261008, commented in full on 261008.
     */
    private int alloc_prob;
    /**
     * The shallowest depth the kind is allocated at — C's {@code kind->alloc_min}, the first half of
     * the {@code alloc:} range. (C's own comment on the field swaps the two descriptions.)
     *
     * <p>Field alloc_min coded before 261008, commented in full on 261008.
     */
    private int alloc_min;
    /**
     * The deepest depth the kind is allocated at — C's {@code kind->alloc_max}, the second half of
     * the {@code alloc:} range.
     *
     * <p>Field alloc_max coded before 261008, commented in full on 261008.
     */
    private int alloc_max;
    /**
     * The kind's level — C's {@code kind->level}, from the {@code level:} line. Also the difficulty
     * of activating it, which is why a special artifact's kind takes its artifact's level.
     *
     * <p>Field level coded before 261008, commented in full on 261008.
     */
    private int level;

    /**
     * The activations the kind carries. C's {@code kind->activation} is a single pointer; the port
     * keeps a list, which in practice holds at most the one a special light artifact hands to its
     * kind.
     *
     * <p>Field activations coded before 261008, commented in full on 261008.
     */
    private List<Activation> activations;
    /**
     * What using an item of this kind does — C's {@code kind->effect}, the chain built from the
     * {@code effect:} lines.
     *
     * <p>Field effect coded before 261008, commented in full on 261008.
     */
    private List<Effect> effect;
    /**
     * The power of the kind's effect — C's {@code kind->power}, from the {@code power:} line. What the
     * power calculation adds for an item that has no activation of its own.
     *
     * <p>Field power coded before 261008, commented in full on 261008.
     */
    private int power;
    /**
     * The message shown when the effect is used — C's {@code kind->effect_msg}, from {@code msg:}.
     * {@code null} for none.
     *
     * <p>Field effectMessage coded before 261008, commented in full on 261008.
     */
    private String effectMessage;
    /**
     * The message shown when the effect is seen happening to something else — C's
     * {@code kind->vis_msg}, from {@code vis-msg:}. {@code null} for none.
     *
     * <p>Field visMessage coded before 261008, commented in full on 261008.
     */
    private String visMessage;

    /**
     * The dice the charge count is rolled from — C's {@code kind->charge}, from {@code charges:}.
     * Wands and staves only.
     *
     * <p>Field charge coded before 261008, commented in full on 261008.
     */
    private Random charge;

    /**
     * The chance, as a percentage, of generating a pile rather than a single item — C's
     * {@code kind->gen_mult_prob}, the first half of the {@code pile:} line.
     *
     * <p>Field genMultProb coded before 261008, commented in full on 261008.
     */
    private int genMultProb;
    /**
     * The dice the pile size is rolled from — C's {@code kind->stack_size}, the second half of the
     * {@code pile:} line.
     *
     * <p>Field stackSize coded before 261008, commented in full on 261008.
     */
    private Random stackSize;

    /**
     * The flavour this kind is disguised behind until the player is aware of it — C's
     * {@code kind->flavor}. {@code null} for a kind that has none, and that null is meaningful: see
     * {@link #getFlavour}.
     *
     * <p>Field flavour coded before 261008, commented in full on 261008.
     */
    private Flavour flavour;

    /**
     * The autoinscription applied once the player is aware of the kind — C's
     * {@code kind->note_aware}, a quark there and a string here. {@code null} for none.
     *
     * <p>Field noteAware coded before 261008, commented in full on 261008.
     */
    private String noteAware;
    /**
     * The autoinscription applied while the player is unaware of the kind — C's
     * {@code kind->note_unaware}. {@code null} for none.
     *
     * <p>Field noteUnaware coded before 261008, commented in full on 261008.
     */
    private String noteUnaware;

    /**
     * Whether the player is aware of what this kind does — C's {@code kind->aware}. Held on the
     * kind, because discovering a flavour is discovering it for every item that wears it.
     *
     * <p>Field aware coded before 261008, commented in full on 261008.
     */
    private boolean aware;
    /**
     * Whether the player has tried this kind — C's {@code kind->tried}.
     *
     * <p>Field tried coded before 261008, commented in full on 261008.
     */
    private boolean tried;

    /**
     * The player's ignore settings for this kind — C's {@code kind->ignore}, a byte there and a
     * flag set here. Holds {@link IgnoreFlag#IGNORE_IF_AWARE} and {@link IgnoreFlag#IGNORE_IF_UNAWARE}.
     * Every constructor gives the kind its own empty set.
     *
     * <p>Field ignore coded before 261008, commented in full on 261008.
     */
    private Flag<IgnoreFlag> ignore;

    /**
     * Whether the kind has ever been seen — C's {@code kind->everseen}, used to keep the ignore
     * menus from spoiling kinds the player has not met.
     *
     * <p>Field everseen coded before 261008, commented in full on 261008.
     */
    private boolean everseen;

    /**
     * Whether this kind exists solely to back one special (instanced) artifact.
     *
     * <p>C asks the question by position — {@code kidx >= z_info->ordinary_kind_max} — because it
     * appends the synthesized artifact kinds after the ordinary ones and can then read the answer
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
     * The recharge/effect timing dice from the kind's {@code time:} line — C's {@code kind->time}.
     * Dice rather than a settled figure: {@link ObjectUtils#objectPrep} rolls each item's own value
     * from it. A kind with no {@code time:} line holds a zero {@link Random}, as C's zeroed
     * {@code random_value} does: the loader passes one, and so do the partly-specified and artifact
     * constructors. The fully-specified constructor stores what it is given, so passing a zero
     * {@link Random} rather than {@code null} is the caller's job; the no-argument constructor
     * leaves the field {@code null} until {@link #setTime} is called.
     *
     * <p>Field time retyped from a dice string to {@link Random} and moved off {@link Effect} onto the
     * kind on 261008.
     */
    private Random time;

    /**
     * Builds an empty object kind with fresh, empty collections, for {@link #copy} to fill in.
     *
     * <p>No C original: C copies a struct with {@code memcpy}. Only the flag sets, the element map,
     * the activation and effect lists, and the brand, slay and curse collections are created here.
     * Everything else — the dice, {@link #modifiers}, the display character — is left {@code null}
     * or zero, so a kind built this way is not usable until every field has been assigned.
     *
     * <p>Constructor ObjectKind() coded before 261008, commented in full on 261008.
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
     * Builds a partly-specified object kind, following C's {@code write_book_kind} ({@code init.c}),
     * which makes a kind for a class's spell book that {@code object.txt} does not list.
     *
     * <p>As C does, it starts from a zeroed kind and sets: one damage die of one side, weight 30, the
     * base's kind flags (C's {@code kf_union} against {@code kb_info[tval]}, skipped here when
     * {@code base} is {@code null}), and, for a dungeon book only, an {@code EL_INFO_IGNORE} on each
     * of the four base elements and {@code KF_GOOD}. The dice fields are zero {@link Random}s, and
     * {@code ignore} is an empty set, as C's zeroed bytes are.
     *
     * <p>Nothing in {@code src/main} calls this constructor: the spell-book loader fills the same
     * fields itself and goes through the long constructor. It is what the test fixtures use to make
     * a minimal kind. Unlike C it takes the cost, level and allocation range as arguments, and it
     * leaves the sval to {@link ObjectRegistry#addObjectKind}.
     *
     * <p>Constructor ObjectKind(adc, cost, ...) coded before 261008, commented in full on 261008.
     *
     * @param adc        display character
     * @param cost       base cost
     * @param level      native level
     * @param min        minimum allocation depth
     * @param max        maximum allocation depth
     * @param name       kind name
     * @param tvalue     item type
     * @param sValueName sub-type name
     * @param base       the base this kind belongs to, or {@code null} for none
     * @param isDungeon  whether this is a dungeon book, which is ignore-marked for the base elements
     *                   and flagged good
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
        flags = new Flag<>(ObjectFlag.class);
        
        if (base != null)
            kindFlags.union(base.getKindFlags());

        if (isDungeon) {
            for (ElementEnum ee : ElementEnum.values()) {
                if (ee.isBase()) {
                    ElementInfo ei = new ElementInfo();
                    ei.on(ElementInfoEnum.EL_INFO_IGNORE);
                    elInfo.put(ee, ei);
                }
            }
            
            kindFlags.on(ObjectKindFlag.KF_GOOD);
        }

        modifiers = new HashMap<>();
        brands = new HashSet<>();
        slays = new HashSet<>();
        curses = new HashMap<>();
        activations = new ArrayList<>();
        effect = new ArrayList<>();
        isSpecialArtifactKind = false;

        this.pVal = Random.Zero();
        this.toH = Random.Zero();
        this.toD = Random.Zero();
        this.toA = Random.Zero();
        this.baseDamage = Random.Zero();
        this.time = Random.Zero();
        this.charge = Random.Zero();
        this.stackSize = Random.Zero();
        this.ignore = new Flag<>(IgnoreFlag.class);
    }

    /**
     * Build a fully-specified object kind from fields the object loader has already resolved.
     *
     * <p>Nothing is parsed here: every dice argument ({@code pVal}, {@code toH}, {@code toD},
     * {@code toA}, {@code baseDamage}, {@code time}, {@code charge}, {@code stackSize}) arrives as
     * a {@link Random} and is stored as given, so the caller supplies a zero {@link Random} where
     * the data file has no line, never {@code null}. {@code time} in particular is the kind's own
     * {@code time:} dice, which the loader reads from the record rather than from the kind's last
     * {@link Effect}. Every other argument except {@code curses} is stored as given too, the brand
     * and slay sets and the effect list included. The one deep copy is the curse map: each
     * {@link CurseData} is copied into a fresh map, so the kind's template never shares an
     * instance with the caller or with an item (see {@link #curses}).
     *
     * <p>The sub-type name is derived rather than passed: {@code sValueName} is {@code name} with
     * the {@code &} and {@code ~} markers stripped. The numeric sval is left at zero for
     * {@link ObjectRegistry#addObjectKind} to assign, and the kind is never a special-artifact kind.
     *
     * <p>Unlike C, whose {@code finish_parse_object} unions the base's kind flags into every kind as
     * it copies the parsed list into {@code k_info}, this constructor does not touch
     * {@code kindFlags}: the caller passes the finished set, base flags included.
     *
     * <p>Constructor ObjectKind(name, text, ...) coded before 261008, commented in full on 261008
     * (the wrong "dice string" and "copying the brand/slay" wording corrected, {@code time} retyped
     * from a dice string to {@link Random}, {@code power} documented, parameter descriptions
     * rewritten to name the {@code object.txt} line each comes from).
     *
     * @param name          kind name, with its {@code &} and {@code ~} markers
     * @param text          the description, from the {@code desc:} lines; {@code null} for none
     * @param base          the base this kind belongs to
     * @param kindIndex     position in the kind table; the registry overwrites it on registration
     * @param pVal          pval dice, from {@code pval:}
     * @param toH           to-hit dice, from {@code attack:}
     * @param toD           to-damage dice, from {@code attack:}
     * @param toA           to-armour dice, from {@code armor:}
     * @param ac            base armour class, from {@code armor:}
     * @param baseDamage    the whole damage-dice term of {@code attack:} (see {@link #baseDamage})
     * @param damageDice    number of damage dice, from {@code attack:}
     * @param damageSides   sides per damage die, from {@code attack:}
     * @param weight        base weight in tenths of a pound
     * @param cost          base cost in gold
     * @param flags         object flags, from {@code flags:}
     * @param kindFlags     kind flags, from {@code flags:} plus the base's
     * @param modifiers     modifier dice, from {@code values:}
     * @param elInfo        per-element info, from {@code flags:}
     * @param brands        brands, from {@code brand:}; stored as given
     * @param slays         slays, from {@code slay:}; stored as given
     * @param curses        curses with their powers, from {@code curse:}; deep-copied
     * @param character     default glyph and colour, from {@code graphics:}
     * @param alloc_prob    allocation weight, from {@code alloc:}
     * @param alloc_min     shallowest allocation depth, from {@code alloc:}
     * @param alloc_max     deepest allocation depth, from {@code alloc:}
     * @param level         native level, from {@code level:}
     * @param activations   activations the kind carries
     * @param effect        effects, from {@code effect:}
     * @param effectMessage message on use, from {@code msg:}; {@code null} for none
     * @param visMessage    message when seen, from {@code vis-msg:}; {@code null} for none
     * @param time          recharge/effect timing dice, zero if the kind has no {@code time:} line
     * @param charge        charge dice, from {@code charges:}
     * @param genMultProb   percentage chance of a pile, from {@code pile:}
     * @param stackSize     pile size dice, from {@code pile:}
     * @param flavour       the flavour the kind hides behind, or {@code null}
     * @param noteAware     aware autoinscription, or {@code null}
     * @param noteUnaware   unaware autoinscription, or {@code null}
     * @param aware         whether the player is aware of the kind
     * @param tried         whether the player has tried the kind
     * @param ignore        the ignore settings; stored as given, so never {@code null}
     * @param everseen      whether the kind has been seen
     * @param tValue        item type
     * @param power         the kind's power rating, from {@code power:}
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
                      String visMessage, Random time,
                      Random charge, int genMultProb,
                      Random stackSize, Flavour flavour,
                      String noteAware, String noteUnaware,
                      boolean aware, boolean tried,
                      Flag<IgnoreFlag> ignore, boolean everseen,
                      TValue tValue, int power) {
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
        this.time = time;
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
        this.power = power;
    }

    /**
     * Synthesizes the kind that backs a special (instanced) artifact — the port of C's
     * {@code write_dummy_object_record} ({@code obj-init.c}), which {@code parse_artifact_base_object}
     * calls when an artifact's {@code base-object:} sval names no kind in {@code object.txt}.
     *
     * <p>As C does, it starts from a zeroed kind and sets: the name {@code "& <sval>~"}, the
     * artifact's level, the base's tval, a copy of the base's object flags, kind flags and element
     * info, {@link ObjectKindFlag#KF_INSTA_ART} on top of the kind flags, and a red {@code '*'}
     * glyph that {@link #setCharacter} replaces when the artifact's {@code graphics:} line is read.
     * The dice fields are zero {@link Random}s and the kind is marked as a special-artifact kind
     * (see {@link #isSpecialArtifactKind}).
     *
     * <p>C also counts the new kind into the base's {@code num_svals} and hands the sval back to the
     * artifact. Here that is {@link ObjectRegistry#addObjectKind}'s job, which the caller must run
     * on the result.
     *
     * <p>Constructor ObjectKind(artifact, ...) coded before 261008, commented in full on 261008
     * (the C original was misnamed {@code write_special_kinds}/{@code special_item}).
     *
     * @param artifact the artifact this kind is being created for
     * @param sValName the sub-type name to give the kind
     * @param base     the base whose flags, kind flags, element info and tval are inherited
     */
    public ObjectKind(Artifact artifact, String sValName, ObjectBase base) {
        this.flags = new Flag<>(ObjectFlag.class);
        this.flags.copyFrom(base.getFlags());
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

        this.toH = Random.Zero();
        this.toD = Random.Zero();
        this.toA = Random.Zero();
        this.baseDamage = Random.Zero();
        this.time = Random.Zero();
        this.charge = Random.Zero();
        this.stackSize = Random.Zero();
        this.pVal = Random.Zero();
    }

    /**
     * Sets the kind's default display glyph and colour — C's {@code kind->d_char} and
     * {@code kind->d_attr}, which C also writes after the fact: {@code parse_artifact_graphics}
     * sets them on a special artifact's kind.
     *
     * <p>Function setCharacter coded before 261008, commented in full on 261008.
     *
     * @param character the glyph and colour
     */
    public void setCharacter(AngbandDisplayCharacter character) {
        this.character = character;
    }

    /**
     * Strips the object-name template markers ({@code "& "} article slot and {@code "~"}
     * pluralization slot) from a kind's name to recover the bare sval reference used elsewhere.
     *
     * <p>The port of the stripping C does inside {@code lookup_sval} ({@code obj-util.c}), which runs
     * {@code obj_desc_name_format} over each kind's name every time it compares one. The port does
     * it once, at construction, and keeps the result beside the numeric sval so a data-file
     * reference such as a book name in {@code class.txt} can be matched against it (see
     * {@link #getsValueName}).
     *
     * <p>Handles only the two markers {@code object.txt} actually uses. C's formatter also expands
     * {@code |singular|plural|} pairs and a {@code #} modifier slot; no kind name in the shipped data
     * has either, so the two agree on every real name.
     *
     * <p>Function stripToRawSval commented in full on 261008.
     *
     * @param name the templated kind name
     * @return the name with the {@code &}/{@code ~} markers removed
     */
    private String stripToRawSval(String name) {
        return name.replace("& ", "").replace("~", "");
    }

    /**
     * Returns the kind's numeric sub-type — the port of reading C's {@code kind->sval}.
     *
     * <p>Svals count from one within each tval, so this is only meaningful beside {@link #gettValue}:
     * the pair is what C's {@code lookup_kind} searches on, and what a class's book list names. It
     * is zero until {@link ObjectRegistry#addObjectKind} has registered the kind.
     *
     * <p>Function getsVal commented in full on 261008.
     *
     * @return the resolved numeric sub-type value (sval)
     */
    public int getsVal() {
        return sVal;
    }

    /**
     * Sets the kind's numeric sub-type — C writes {@code kind->sval} straight from
     * {@code ++kb_info[tval].num_svals} while parsing, and the port does the same count in
     * {@link ObjectRegistry#addObjectKind}, which is the one caller.
     *
     * <p>Function setsVal commented in full on 261008.
     *
     * @param sVal the sval to assign
     */
    public void setsVal(int sVal) {
        this.sVal = sVal;
    }

    /**
     * Returns the kind's name - the port of reading C's {@code kind->name}, such as "Dagger" or
     * "Black Dragon Scale Mail".
     *
     * <p>The ignore machinery matches against this, not the ego's name: {@code egoHasIgnoreType} looks
     * for a {@code qualityMapping} identifier inside it.
     *
     * <p>Function getName commented in full on 261008.
     *
     * @return the kind's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the base this kind belongs to — the port of reading C's {@code kind->base}.
     *
     * <p>One base serves every kind of its tval, so this is shared rather than copied, even by
     * {@link #copy}.
     *
     * <p>Function getBase commented in full on 261008.
     *
     * @return the kind's base type
     */
    public ObjectBase getBase() {
        return base;
    }

    /**
     * Sets the allocation weight — C's {@code kind->alloc_prob}.
     *
     * <p>No caller in {@code src/main} today; the loader passes the figure through the constructor.
     *
     * <p>Function setAlloc_prob commented in full on 261008.
     *
     * @param alloc_prob the allocation probability weight
     */
    public void setAlloc_prob(int alloc_prob) {
        this.alloc_prob = alloc_prob;
    }

    /**
     * Sets the shallowest allocation depth — C's {@code kind->alloc_min}.
     *
     * <p>No caller in {@code src/main} today; see {@link #setAlloc_prob}.
     *
     * <p>Function setAlloc_min commented in full on 261008.
     *
     * @param alloc_min the minimum allocation depth
     */
    public void setAlloc_min(int alloc_min) {
        this.alloc_min = alloc_min;
    }

    /**
     * Sets the deepest allocation depth — C's {@code kind->alloc_max}.
     *
     * <p>No caller in {@code src/main} today; see {@link #setAlloc_prob}.
     *
     * <p>Function setAlloc_max commented in full on 261008.
     *
     * @param alloc_max the maximum allocation depth
     */
    public void setAlloc_max(int alloc_max) {
        this.alloc_max = alloc_max;
    }

    /**
     * Returns the activations the kind carries — C's {@code kind->activation}, which is a single
     * pointer there; see {@link #activations}.
     *
     * <p>Handed out as the live list, not a copy: the artifact loader adds a special light's
     * activation to it, as {@code parse_artifact_act} does to {@code kind->activation}.
     *
     * <p>Function getActivations commented in full on 261008.
     *
     * @return the activations available on this kind
     */
    public List<Activation> getActivations() {
        return activations;
    }

    /**
     * Sets the recharge/effect timing dice — C's {@code kind->time}, which {@code parse_artifact_time}
     * overwrites for a special light artifact, whose activation belongs to its kind rather than to the
     * artifact.
     *
     * <p>Function setTime commented in full on 261008.
     *
     * @param time the recharge/effect timing dice to assign
     */
    public void setTime(Random time) {
        this.time = time;
    }

    /**
     * Returns the kind's sub-type by name — what C's {@code lookup_sval} compares a data-file
     * reference against after stripping the markers from {@code kind->name}.
     *
     * <p>{@link ObjectRegistry} matches a name-form sval reference against this, ignoring case.
     *
     * <p>Function getsValueName commented in full on 261008.
     *
     * @return the sub-type by name (the kind's name with its {@code &} and {@code ~} markers removed)
     */
    public String getsValueName() {
        return sValueName;
    }

    /**
     * Returns the kind flags ({@code KF_*}) — the port of reading C's {@code kind->kind_flags}, as
     * {@code kf_has} does.
     *
     * <p>A read-only view, because the set is final once the kind is built. It already includes the
     * base's kind flags, so {@code KF_EASY_KNOW} on a base shows up here for every kind of that base.
     *
     * <p>Function getKindFlags commented in full on 261008.
     *
     * @return a read-only view of the kind flags set on this kind
     */
    public FlagView<ObjectKindFlag> getKindFlags() {
        return kindFlags;
    }

    /**
     * Returns the kind's item type - the port of reading C's {@code kind->tval}.
     *
     * <p>The first test {@code egoHasIgnoreType} applies to each {@code qualityMapping} row: the row's
     * tval must equal this one before its identifier is even looked for.
     *
     * <p>Function gettValue commented in full on 261008.
     *
     * @return the item type value (tval)
     */
    public TValue gettValue() {
        return tValue;
    }

    /**
     * Returns the kind's position in the registry's kind table — the port of reading C's
     * {@code kind->kidx}.
     *
     * <p>Zero until {@link ObjectRegistry#addObjectKind} registers the kind. Special-artifact kinds
     * are registered after every ordinary one, which is what C's
     * {@code kidx >= z_info->ordinary_kind_max} test relied on; the port also records the answer in
     * {@link #isSpecialArtifactKind}.
     *
     * <p>Function getKindIndex commented in full on 261008.
     *
     * @return this kind's index in the object-kind table
     */
    public int getKindIndex() {
        return kindIndex;
    }

    /**
     * Sets the kind's position in the registry's kind table — C's {@code kind->kidx}, which
     * {@code finish_parse_object} assigns as it copies the parsed kinds into {@code k_info}.
     *
     * <p>Function setKindIndex commented in full on 261008.
     *
     * @param kindIndex this kind's index in the object-kind table
     */
    public void setKindIndex(int kindIndex) {
        this.kindIndex = kindIndex;
    }

    /**
     * Returns the dice this kind's to-hit bonus is rolled from, C's {@code kind->to_h}.
     *
     * <p>Dice, not a number: this is the recipe every item of this kind is made to, and the figure
     * an individual item ended up with lives on that item instead. The distinction is the whole
     * point of {@link ItemObject#hasStandardToH}, which compares an item's settled to-hit against
     * {@link Random#getBase} here to decide whether the item has drifted from what its kind
     * prescribes. {@link ObjectUtils#objectPrep} is the other side: it rolls an item's figure from
     * these dice in the first place.
     *
     * <p>Function getToH coded on 260815, commented in full on 261008 (the claim that
     * {@code hasStandardToH} was the only caller removed).
     *
     * @return this kind's to-hit dice
     */
    public Random getToH() {
        return toH;
    }

    /**
     * Returns the kind's base armour class — the port of reading C's {@code kind->ac}.
     *
     * <p>A plain number where {@link #getToA} is dice: the base is fixed for the kind and only the
     * bonus on top of it is rolled per item.
     *
     * <p>Function getAc commented in full on 261008.
     *
     * @return this kind's base armour class
     */
    public int getAc() {
        return ac;
    }

    /**
     * Reports whether the player has identified what this kind is, the port of reading C's
     * {@code kind->aware}.
     *
     * <p>Held on the kind, not on any item, because that is the scope of the discovery: learning
     * that the pink potion is a Potion of Speed is learning it about every pink potion at once. See
     * {@code PlayerKnowledge.flavourAware}, which sets it and then puts the
     * rest of the world in step.
     *
     * <p>Function isAware commented in full on 261008 (the setter named as {@code Player.flavourAware}
     * corrected to {@code PlayerKnowledge.flavourAware}).
     *
     * @return {@code true} if the player knows what this kind is
     */
    public boolean isAware() {
        return aware;
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
     * Records that the player has identified what this kind is — C's {@code kind->aware = true}.
     *
     * <p>Should generally be reached through
     * {@code PlayerKnowledge.flavourAware} rather than called directly:
     * awareness has consequences — the ignore fixup, the pack refresh, the floor redraw — and
     * setting the flag here does none of them. The flavour set-up in {@code ObjectUtils} and the
     * new-character reset in {@code PlayerBirth} do call it directly, since they set the flag as
     * part of building or wiping the whole table.
     *
     * <p>Function setAware commented in full on 261008 (the caller named as {@code Player.flavourAware}
     * corrected to {@code PlayerKnowledge.flavourAware}).
     *
     * @param aware whether the player knows what this kind is
     */
    public void setAware(boolean aware) {
        this.aware = aware;
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
     * Returns what using an item of this kind does — the port of reading C's {@code kind->effect},
     * the chain built from the {@code effect:} lines of {@code object.txt}.
     *
     * <p>Handed out as the live list, not a copy. Empty for a kind with no effect, never
     * {@code null}, so a caller can loop without checking.
     *
     * <p>Function getEffect commented in full on 261008.
     *
     * @return what items of this kind do when used
     */
    public List<Effect> getEffect() {
        return effect;
    }

    /**
     * Sets whether identified items of this kind are ignored, the port of C's
     * {@code kind_ignore_when_aware}.
     *
     * <p>Called by {@code PlayerKnowledge.flavourAware} to carry a standing
     * decision across the moment of identification: a player who was ignoring unknown potions is
     * taken to be ignoring this one, so the pile they were stepping over does not reappear under a
     * name. See {@link IgnoreFlag}.
     *
     * <p>Function setIgnoredAware commented in full on 261008 (the caller named as
     * {@code Player.flavourAware} corrected to {@code PlayerKnowledge.flavourAware}).
     *
     * @param ignoredAware whether to ignore identified items of this kind
     */
    public void setIgnoredAware(boolean ignoredAware) {
        if (ignoredAware) ignore.on(IgnoreFlag.IGNORE_IF_AWARE);
        else ignore.off(IgnoreFlag.IGNORE_IF_AWARE);
    }

    /**
     * Reports whether this kind exists to back a special artifact, the port of C's
     * {@code obj->kind->kidx >= z_info->ordinary_kind_max} test. C reads the answer off the kind's
     * position, because the artifact kinds are appended after the ordinary ones; the port reads the
     * flag the artifact constructor set (see {@link #isSpecialArtifactKind}).
     *
     * <p>A kind of this sort is its own artifact rather than a template many items share, so there
     * is nothing to be unsure of once it is in hand: {@code knowObject} makes the player aware of a
     * non-jewellery special artifact outright rather than waiting for its runes to be read.
     *
     * <p>Function isSpecialArtifactKind commented in full on 261008.
     *
     * @return {@code true} if this kind is a special artifact
     */
    public boolean isSpecialArtifactKind() {
        return isSpecialArtifactKind;
    }

    /**
     * Reports whether an item of this kind has ever been seen identified, the port of reading C's
     * {@code kind->everseen}.
     *
     * <p>Not knowledge but a record of whether the news has been broken, so that recognizing a kind
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
     * The flags every object of this kind carries — C's {@code kind->flags}, the {@code flags:} line
     * in {@code object.txt}.
     *
     * <p>These are the kind's flags, not an object's. An object gets its own copy at
     * {@code object_prep}, and thereafter the two can differ. The kind's set is consulted again only
     * once the player is aware of the item's flavour: {@code object_flags_known} ({@code obj-util.c})
     * folds it back in when {@code object_flavor_is_aware} holds, making the kind's properties
     * public knowledge.
     *
     * <p>Handed out as a read-only view; the base's own object flags are not part of it.
     *
     * <p>Function getFlags commented in full on 261008.
     *
     * @return a read-only view of this kind's flags
     */
    public FlagView<ObjectFlag> getFlags() {
        return flags;
    }

    /**
     * Returns the dice this kind's to-armour bonus is rolled from, the port of reading C's
     * {@code kind->to_a}.
     *
     * <p>Dice, not a number, for the same reason as {@link #getToH}: the recipe lives here and the
     * figure an item ended up with lives on the item. The ignore code compares an item's own
     * to-armour against this to judge it good, bad or average for its type.
     *
     * <p>Shared with this instance rather than copied, so the caller must not alter it.
     *
     * <p>Function getToA commented in full on 261008.
     *
     * @return the to-armour dice for this kind
     */
    public Random getToA() {
        return toA;
    }

    /**
     * Returns the number of damage dice items of this kind roll, the port of reading C's
     * {@code kind->dd}.
     *
     * <p>A plain count, not a range — unlike {@link #getToD} next door, which is a {@link Random}
     * because it is rolled per item. Every Long Sword has the same {@code 2d5}; what differs between
     * two of them is the bonus on top. That is why {@code object_set_base_known} can copy this onto
     * a counterpart the moment the kind is recognized: knowing what the item <em>is</em> settles its
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
     * Returns an independent copy of this object kind.
     *
     * <p>Deep-copied because their contents are mutable: every {@link uk.co.jackoftradesltd.middle.numerics.Random}
     * term, the two flag sets and the ignore flags, the modifier map (each value copied in turn),
     * the element info (each entry copied), the curse map (each {@code CurseData} rebuilt), the
     * display character, the activation and effect lists, the flavour, and the stack-size and charge
     * dice.
     *
     * <p>Shared deliberately: {@link #base}, because one base serves every kind of its tval and holds
     * the running count of svals, so a copy that owned its own would drift from the registry's; and
     * the members of the brand and slay sets, which are registry entries with no setters that every
     * carrier points at. C shares the same pointers.
     *
     * <p>The flavour is the exception to that logic: it is copied, so the copy's flavour is a
     * separate object from the one the flavour table holds, and marking either aware or tried does
     * not reach the other.
     *
     * <p>Built member by member on a fresh instance through the no-argument constructor, which has
     * already given the copy empty collections; that is why the collections are cleared or added
     * into rather than assigned. No C original: C copies a kind by assigning the struct.
     *
     * <p>Throws {@link NullPointerException} on a kind whose dice, modifier map or display character
     * were never assigned, such as one from the ten-argument constructor given a {@code null}
     * display character, or the no-argument shell itself.
     *
     * <p>Function copy commented in full on 261008 (the claims that bases are immutable and that
     * the kind has more fields than any constructor takes removed).
     *
     * @return a new object kind that shares no mutable state with this one, bar the base and the
     *         brand and slay members
     */
    public ObjectKind copy() {
        ObjectKind copy = new ObjectKind();
        copy.name = this.name;
        copy.text = this.text;
        copy.base = this.base; // One base serves every kind of its tval, so the copy shares it
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
        copy.flavour = null;
        if (this.flavour != null)
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
     * Returns the power of this kind's effect — the port of reading C's {@code kind->power}, from the
     * {@code power:} line of {@code object.txt}.
     *
     * <p>{@code ItemObject.effectsPower} falls back on it when the object itself carries no
     * activation, which is what C's {@code effects_power} in {@code obj-power.c} does with
     * {@code obj->kind->power}.
     *
     * <p>Function getPower commented in full on 261008.
     *
     * @return the power this kind contributes to an object built on it
     */
    public int getPower() {
        return power;
    }

    /**
     * Returns the kind's base cost in gold — the port of reading C's {@code kind->cost}.
     *
     * <p>The price of an aware object before any bonus or ego is added: C's
     * {@code object_value_base} ({@code obj-power.c}) returns it outright when the flavour is aware,
     * and only falls back to a per-tval guess when it is not.
     *
     * <p>Function getCost commented in full on 261008 (the old note had the aware and unaware
     * cases the wrong way round).
     *
     * @return the base cost of this kind in gold
     */
    public int getCost() {
        return cost;
    }

    /**
     * Answers whether the player's class can read this kind as a spell book - the port of C's
     * {@code obj_kind_can_browse} ({@code obj-util.c}). C's {@code obj_can_browse} is a one-line
     * wrapper that passes an object's kind to it; {@code ItemObject.canBrowse} stands in for that.
     *
     * <p>Walks the class's own list of magic books and matches each on both halves of its
     * {@code (tval, sval)} pair, so the same book is browsable by a mage and not by a priest. Both
     * halves are needed: each item type numbers its sub-types from one upwards, so every realm has a
     * book with the same sval and the sub-type alone cannot tell a prayer book from a magic one.
     *
     * <p>Reaches the live player through {@code GameState}, so it answers for whoever is playing
     * rather than taking the player as an argument, unlike its C original.
     *
     * <p>A class with no magic holds {@code ClassMagic.NONE}, whose book list is empty, so the loop
     * runs zero times and the answer is {@code false}, as C's {@code num_books} of 0 gives. The early
     * return on {@code ClassMagic.NONE} says the same thing sooner.
     *
     * <p>Function canBrowse coded before 260827, commented in full on 261003.
     *
     * @return {@code true} if the current player's class can browse this kind
     */
    public boolean canBrowse() {
        Player player = GameState.getPlayer();

        if (player.getPlayerClass().getMagic() == ClassMagic.NONE)
            return false;

        for (MagicBook mb : player.getPlayerClass().getMagic().getMagicBooks()) {
            if (this.gettValue() == mb.getBookTValue() && this.sVal == mb.getSval())
                return true;
        }

        return false;
    }

    /**
     * Sets the base cost — C's {@code kind->cost}, which {@code parse_artifact_cost} overwrites with
     * the artifact's own cost when the kind is a special artifact's.
     *
     * <p>Function setCost commented in full on 261008.
     *
     * @param cost the base cost in gold
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    /**
     * Returns the kind's base weight — the port of reading C's {@code kind->weight}.
     *
     * <p>In tenths of a pound, as C keeps it, so a weight of 30 is three pounds.
     * {@link ObjectUtils#objectPrep} copies it onto each new item.
     *
     * <p>Function getWeight commented in full on 261008.
     *
     * @return this kind's base weight, in tenths of a pound
     */
    public int getWeight() {
        return weight;
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

    /**
     * Sets the base weight — C's {@code kind->weight}, which {@code parse_artifact_weight}
     * overwrites with the artifact's own weight when the kind is a special artifact's.
     *
     * <p>Function setWeight commented in full on 261008.
     *
     * @param weight the kind's weight (in tenths of a pound)
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Returns the recharge/effect timing dice for this kind, the port of reading C's
     * {@code kind->time}.
     *
     * <p>Dice rather than a settled figure, for the same reason as {@link #getToH}: {@code object.txt}
     * states the recipe once and {@link ObjectUtils#objectPrep} rolls each item's own value from it.
     *
     * <p>Function getTime commented in full on 260904.
     *
     * @return this kind's recharge/effect timing dice
     */
    public Random getTime() {
        return time;
    }

    /**
     * Returns the dice for one of this kind's numeric modifiers, the port of reading C's
     * {@code kind->modifiers[i]}.
     *
     * <p>C keeps every modifier in a fixed {@code OBJ_MOD_MAX}-length array, so a modifier
     * {@code object.txt} never mentions still reads back as a valid, zero-value {@code random_value}
     * rather than as an absence. This kind keeps modifiers in a {@link Map} instead, populated only
     * for the modifiers a kind's {@code values:} line actually names, so a plain {@code get} would
     * return {@code null} for the common case of an unmentioned modifier - {@code getModifier}
     * falls back to a fresh zero dice ({@code base}/{@code dice}/{@code mBonus} all {@code 0}) in that
     * case, whose {@link Random#randCalc} always comes out {@code 0} regardless of aspect, matching
     * what C's zeroed array slot would compute.
     *
     * <p>Function getModifier commented in full on 260904.
     *
     * @param modifier the modifier to look up
     * @return the dice for that modifier, or a zero-value dice if this kind does not carry it
     */
    public Random getModifier(ObjectModifier modifier) {
        return modifiers.getOrDefault(modifier, new Random(0, 0, 0, 1, false));
    }

    /**
     * Returns the charge-count dice for this kind (wands and staves), the port of reading C's
     * {@code kind->charge}.
     *
     * <p>Dice rather than a settled figure, for the same reason as {@link #getTime}: rolled per item
     * by {@link ObjectUtils#objectPrep} rather than fixed on the kind.
     *
     * <p>Function getCharge commented in full on 260904.
     *
     * @return this kind's charge-count dice
     */
    public Random getCharge() {
        return charge;
    }

    /**
     * Returns the slays this kind carries — see {@link #slays}.
     *
     * <p>Function getSlays commented in full on 260904.
     *
     * @return this kind's slays
     */
    public Set<Slay> getSlays() {
        return slays;
    }

    /**
     * Returns the brands this kind carries — see {@link #brands}.
     *
     * <p>Function getBrands commented in full on 260904.
     *
     * @return this kind's brands
     */
    public Set<Brand> getBrands() {
        return brands;
    }

    /**
     * Returns the curses this kind carries, each mapped to the {@link CurseData} the kind
     * prescribes — see {@link #curses}.
     *
     * <p>Function getCurses commented in full on 260904.
     *
     * @return this kind's curses
     */
    public Map<Curse, CurseData> getCurses() {
        return curses;
    }

    /**
     * Returns this kind's per-element info for the given element, the port of reading C's
     * {@code kind->el_info[element]}.
     *
     * <p>This kind's {@link #elInfo} map only holds an entry for an element that {@code object.txt}
     * actually sets; C's array is fixed-size and every unmentioned slot reads back as an
     * already-zeroed {@code element_info} — no resistance, no flags. A missing map entry answers
     * the same way here, with a freshly built {@link ElementInfo} rather than a shared placeholder,
     * so that {@link ObjectUtils#objectPrep}'s {@code .copy()} of whatever comes back is always
     * copying something this kind alone owns.
     *
     * <p>Function getElInfo commented in full on 260904.
     *
     * @param elementEnum the element to look up
     * @return this kind's info for that element, or a fresh zero-value {@link ElementInfo} if this
     * kind carries none
     */
    public ElementInfo getElInfo(ElementEnum elementEnum) {
        return elInfo.getOrDefault(elementEnum, new ElementInfo());
    }

    /**
     * Returns the dice this kind's pval (extra parameter) is rolled from, the port of reading C's
     * {@code kind->pval}.
     *
     * <p>Dice, not a settled number, for the same reason as {@link #getToH}: {@code object.txt}
     * states the range once and {@link ObjectUtils#objectPrep} rolls each item's own figure from it
     * — here for food, oil, launchers and potions, where a wand or staff instead rolls its pval from
     * {@link #getCharge}.
     *
     * <p>Function getPVal commented in full on 260904.
     *
     * @return this kind's pval dice
     */
    public Random getPVal() {
        return pVal;
    }

    /**
     * Reports whether this kind is disguised behind a flavour, the port of C's null check on
     * {@code kind->flavor} — for example the {@code obj->kind->flavor} test in
     * {@code object_set_base_known} ({@code obj-knowledge.c}). See {@link #getFlavour()} for
     * the flavour itself, and why the field is load-bearing rather than incidental.
     *
     * <p>Function hasFlavour coded before 260904, commented in full on 260904.
     *
     * @return {@code true} if this kind has a flavour to hide behind
     */
    public boolean hasFlavour() {
        return flavour != null;
    }

    /**
     * Returns the autoinscription applied once the player is aware of this kind, the port of the
     * aware branch of C's {@code get_autoinscription} ({@code obj-ignore.c}), which reads
     * {@code kind->note_aware} through {@code quark_str}. C's quark table returns {@code NULL} for
     * an unset quark ({@code quark_t} 0), which is why a plain {@code null} field here needs no
     * extra translation - an inscription never set reports the same absence both sides of the
     * boundary.
     *
     * <p>Function getNoteAware coded on 260905, commented in full on 261008 (C line number removed).
     *
     * @return the aware autoinscription, or {@code null} if none is set
     */
    public String getNoteAware() {
        return noteAware;
    }

    /**
     * Returns the autoinscription applied while the player remains unaware of this kind, the port
     * of the unaware branch of C's {@code get_autoinscription} ({@code obj-ignore.c}), which
     * reads {@code kind->note_unaware} through {@code quark_str}. See {@link #getNoteAware()} for
     * why a {@code null} field matches C's unset-quark answer without further work.
     *
     * <p>Function getNoteUnaware coded on 260905, commented in full on 261008 (C line number removed).
     *
     * @return the unaware autoinscription, or {@code null} if none is set
     */
    public String getNoteUnaware() {
        return noteUnaware;
    }

    /**
     * Reports whether this kind's ignore setting includes the given flag - the general form behind
     * C's per-flag bit tests such as {@code kind_is_ignored_aware} and {@code kind_is_ignored_unaware}
     * ({@code obj-ignore.c}), each of which is just {@code kind->ignore & FLAG} written out
     * for one flag. Testing either bit through the same {@link Flag#has} call is what lets
     * {@link ObjectIgnore#kindIsIgnoredUnaware} stay a one-line wrapper instead of repeating the
     * flag-set lookup itself.
     *
     * <p>Function hasIgnoreFlag coded on 260905, commented in full on 261008 (C line numbers removed).
     *
     * @param ignoreFlag the flag to test
     * @return {@code true} if the flag is set in this kind's ignore setting
     */
    public boolean hasIgnoreFlag(IgnoreFlag ignoreFlag) {
        return ignore.has(ignoreFlag);
    }

    /**
     * Sets whether this kind has ever been seen identified - the port of writing C's
     * {@code kind->everseen} directly. C has no dedicated setter for the field; every call site
     * ({@code object_desc} in {@code obj-desc.c}, {@code player_outfit} in {@code player-birth.c},
     * {@code rd_object_memory} in {@code load.c}) assigns it in place, which is why this setter
     * takes the value rather than only ever setting {@code true}.
     *
     * <p>{@link #isEverseen} is the read side of the same flag.
     *
     * <p>Function setEverSeen commented in full on 261008 (C line numbers replaced by function names).
     *
     * @param everseen the new everseen value
     */
    public void setEverSeen(boolean everseen) {
        this.everseen = everseen;
    }

    /**
     * Sets the given flag in this kind's ignore setting - the general form behind C's per-flag
     * setters {@code kind_ignore_when_aware} and {@code kind_ignore_when_unaware}
     * ({@code obj-ignore.c}), each of which is just {@code kind->ignore |= FLAG} written out
     * for one flag. Setting a flag that is already on is a no-op either way, since a bitwise OR and
     * {@link Flag#on} both leave an already-set bit alone.
     *
     * <p>Both C functions also raise {@code player->upkeep->notice |= PN_IGNORE} straight after the
     * bit set; that side effect is not this setter's job, the same way {@link #hasIgnoreFlag} carries
     * none of the side effects belonging to the C reads it generalizes. It is the caller's boundary to
     * cross, the way {@link ObjectIgnore#kindIgnoreWhenAware} takes a {@code Player} for exactly that
     * purpose.
     *
     * <p>{@link #hasIgnoreFlag} is the read side of the same flag set.
     *
     * <p>Function setIgnoreFlag commented in full on 261008 (C line numbers removed).
     *
     * @param ignoreFlag the flag to set
     */
    public void setIgnoreFlag(IgnoreFlag ignoreFlag) {
        this.ignore.on(ignoreFlag);
    }

    /**
     * Clears every ignore flag held on this kind - the port of the {@code kind->ignore = 0} line
     * that appears twice in C: once in {@code kind_ignore_clear} ({@code obj-ignore.c}), and
     * once, unrolled into a loop over every kind, in {@code ignore_birth_init}
     * ({@code obj-ignore.c}). Both zero the same packed byte; this method zeroes the same
     * two bits by clearing the underlying {@link Flag}'s {@code EnumSet}.
     *
     * <p>C's {@code kind_ignore_clear} also raises {@code player->upkeep->notice |= PN_IGNORE}
     * straight after the clear, the same side effect {@link #setIgnoreFlag} leaves to its caller.
     * This method carries none of it either, which matches its one caller,
     * {@link ObjectIgnore#ignoreBirthInit}, exactly: that method is the port of
     * {@code ignore_birth_init}, and C's birth-time reset does not raise the notice flag at all.
     *
     * <p>Function wipeIgnoreFlags coded on 260907, commented in full on 261008 (C line numbers
     * removed).
     */
    public void wipeIgnoreFlags() {
        this.ignore.wipe();
    }

    /**
     * Sets the flavour this kind is disguised behind, the port of C's direct {@code kind->flavor}
     * field write — there is no dedicated C setter; every call site assigns the struct field inline.
     * C uses that same write for two opposite purposes, and this setter carries both unchanged: a
     * real {@link Flavour} in {@code flavor_assign_fixed} and {@code flavor_assign_random}
     * ({@code obj-util.c}), and {@code NULL} to scrub it back off in {@code flavor_init}'s
     * new-player reset ({@code obj-util.c}).
     *
     * <p>No validation either side — C overwrites the pointer unconditionally, and this setter
     * overwrites {@link #flavour} unconditionally, {@code null} included. See {@link #getFlavour()}
     * for why that null is load-bearing rather than incidental.
     *
     * <p>Function setFlavour coded before 260908, commented in full on 261008 (C line numbers
     * removed).
     *
     * @param flavour the flavour to disguise this kind behind, or {@code null} to clear it
     */
    public void setFlavour(Flavour flavour) {
        this.flavour = flavour;
    }

    /**
     * Returns this kind's display glyph and colour, the port of reading C's {@code kind->d_char}/
     * {@code kind->d_attr} ({@code object.h}) together. {@link ItemObject#objectKindChar()} and
     * {@link ItemObject#objectKindAttr()} are the callers, falling back to this whenever an item
     * of this kind is not drawn with its flavour's glyph instead.
     *
     * <p>Function getCharacter coded before 260904, commented in full on 260928.
     *
     * @return this kind's display glyph and colour
     */
    public AngbandDisplayCharacter getCharacter() {
        return character;
    }
}
