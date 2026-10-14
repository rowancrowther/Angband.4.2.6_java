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

import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;

import java.util.*;

/**
 * An ego item template (as loaded from {@code ego_item.txt}) — a named modifier
 * applied to a base item to make a "magical" variant (e.g. "of Slaying"),
 * defining the flags/modifiers it adds (and minimums), brands/slays/curses,
 * bonus ranges, which base items it may apply to, and an optional activation.
 * This is the Java port of the C original's {@code struct ego_item}
 * ({@code src/object.h}).
 *
 * <p>Two things differ in shape from C. First, C's fixed-length arrays indexed by brand, slay, curse
 * and modifier ({@code bool *brands}, {@code bool *slays}, {@code int *curses},
 * {@code modifiers[OBJ_MOD_MAX]}) become sets and maps that hold only the entries this ego actually
 * has, so a key that is absent is C's zero or {@code false} slot. Second, the player's ignore
 * choices are not part of {@code struct ego_item} in C: they live in a separate table,
 * {@code ego_ignore_types[eidx][itype]} in {@code obj-ignore.c}. Here each ego carries its own row
 * of that table ({@link #ignoreTypes}), read and written through {@link #getIgnoreType},
 * {@link #setIgnoreType} and {@link #clearIgnoreType}.
 *
 * <p>An instance is a template, one per record of {@code ego_item.txt}; it holds no per-object
 * state, which belongs on the item the ego is applied to.
 *
 * <p>Class EgoItem coded before 260816, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
public class EgoItem {
    /**
     * The ego type's name, the port of C's {@code ego->name}, from the {@code name:} line of
     * {@code ego_item.txt}. Most carry the leading "of" ("of Shadows"); a few are bracketed
     * instead ("(Everburning)", "(Holy Avenger)"). Several egos may share one name, as the data
     * file's header notes for the armour and shield "of Resistance".
     *
     * <p>Field name coded before 260816, commented in full on 261014.
     */
    private String name;
    /**
     * The ego's description, the port of C's {@code ego->text}, built from the {@code desc:} lines
     * of {@code ego_item.txt} (C appends each line to the last). The constructor's {@code desc}
     * parameter is stored here.
     *
     * <p>Field text coded before 260816, commented in full on 261014.
     */
    private String text;
    /**
     * The object kinds this ego may be applied to - the port of walking C's {@code ego->poss_items}
     * list (each {@code poss_item} resolved from its {@code kidx} to the kind itself).
     *
     * <p>Read by {@link #egoHasIgnoreType(IgnoreType)} to decide which ignore categories are valid for
     * this ego.
     *
     * <p>Field possItems commented in full on 261008.
     */
    private List<ObjectKind> possItems;

    /**
     * This ego's position in the ego table, the port of C's {@code ego->eidx}.
     *
     * <p>C assigns it in {@code finish_parse_ego} when it copies the parsed records into
     * {@code e_info}, so it is the record's place in {@code ego_item.txt} order. {@code EgoItemAssembler}
     * passes a counter that starts at {@code 0} and moves on after each ego it builds, so the first
     * ego holds {@code 0} and the numbers follow file order, as in C. A record the assembler drops for
     * an error does not use a number, so the egos after it sit one lower than C would number them;
     * C instead abandons the whole parse on an error. This class never revises the value, and nothing
     * in it reads it: the ignore choices C indexes by it are held per instance here instead.
     *
     * <p>Field egoIndex coded before 260816, commented in full on 261014.
     */
    private int egoIndex;

    /**
     * The ego's added value, the port of C's {@code ego->cost}, from the first number on the
     * {@code info:} line of {@code ego_item.txt}. A plain integer, not a dice expression.
     *
     * <p>Field cost coded before 260816, commented in full on 261014.
     */
    private int cost;

    /**
     * The object flags this ego adds to the item, the port of C's {@code ego->flags}. Read through
     * {@link #getFlags()}.
     *
     * <p>Field flags coded before 260816, commented in full on 261014.
     */
    private Flag<ObjectFlag> flags;
    /**
     * The object flags this ego removes from the item, the port of C's {@code ego->flags_off}, from
     * the {@code flags-off:} line of {@code ego_item.txt}. Read through {@link #getOffFlags()}.
     *
     * <p>Field flagsOff coded before 260816, commented in full on 261014.
     */
    private Flag<ObjectFlag> flagsOff;
    /**
     * The kind flags this ego adds, the port of C's {@code ego->kind_flags}. In {@code ego_item.txt}
     * the {@code flags:} line takes object flags and kind flags alike, and C sorts each token into
     * whichever set knows it; this is the kind-flag half.
     *
     * <p>Field kindFLags coded before 260816, commented in full on 261014.
     */
    private Flag<ObjectKindFlag> kindFLags;

    /**
     * The ranges this ego rolls for the object modifiers it grants, the port of C's
     * {@code ego->modifiers[]}, from the {@code values:} line. A modifier the ego does not touch has
     * no entry, where C holds a zeroed {@code random_value}. Read through
     * {@link #getModifier(ObjectModifier)}.
     *
     * <p>Field modifiers coded before 260816, commented in full on 261014.
     */
    private Map<ObjectModifier, Random> modifiers;
    /**
     * The floors for the granted modifiers, the port of C's {@code ego->min_modifiers[]}, from the
     * {@code min-values:} line. A modifier with no entry has no floor, as a zero in C's array.
     *
     * <p>Field minModifiers coded before 260816, commented in full on 261014.
     */
    private Map<ObjectModifier, Integer> minModifiers;
    /**
     * The resistances and vulnerabilities this ego confers, the port of C's {@code ego->el_info[]}.
     * Read through {@link #getElInfo()}.
     *
     * <p>Field elInfo coded before 260816, commented in full on 261014.
     */
    private Map<ElementEnum, ElementInfo> elInfo;

    /**
     * The brands this ego confers, the port of C's {@code ego->brands}, from the {@code brand:}
     * lines. C holds a {@code bool} per brand index; here the brands that are set are the members.
     * Read through {@link #getBrands()}.
     *
     * <p>Field brands coded before 260816, commented in full on 261014.
     */
    private Set<Brand> brands;
    /**
     * The slays this ego confers, the port of C's {@code ego->slays}, from the {@code slay:} lines.
     * C holds a {@code bool} per slay index; here the slays that are set are the members. Read
     * through {@link #getSlays()}.
     *
     * <p>Field slays coded before 260816, commented in full on 261014.
     */
    private Set<Slay> slays;
    /**
     * The curses this ego carries, the port of C's {@code ego->curses}, from the {@code curse:}
     * lines. C holds only a power per curse index ({@code int *curses}); the map's
     * {@link CurseData} is the instance record an object's own curse uses, and the assembler fills
     * it with the ego's power and a timeout of {@code 0}, adding only a curse whose power is above
     * {@code 0} as C's {@code parse_ego_curse} does. The timeout is not an ego property: C rolls
     * it from the curse's own {@code time} when it appends the curse to an object
     * ({@code append_object_curse} in {@code obj-curse.c}). Read through {@link #getCurses()}.
     *
     * <p>Field curses coded before 260816, commented in full on 261014.
     */
    private Map<Curse, CurseData> curses;

    /**
     * How much this ego adds to the level-feeling rating, the port of C's {@code ego->rating}, from
     * the second number on the {@code info:} line.
     *
     * <p>Field rating coded before 260816, commented in full on 261014.
     */
    private int rating;
    /**
     * How common this ego is, the port of C's {@code ego->alloc_prob}, from the {@code alloc:}
     * line's commonness. C's allocation table skips an ego whose value is zero.
     *
     * <p>Field allocProb coded before 260816, commented in full on 261014.
     */
    private int allocProb;
    /**
     * The shallowest depth this ego is normally generated at, the port of C's {@code ego->alloc_min}
     * (the "min" of the {@code alloc:} line's {@code min to max}). It can appear earlier by a chance
     * that falls with the depth shortfall.
     *
     * <p>Field allocMin coded before 260816, commented in full on 261014.
     */
    private int allocMin;
    /**
     * The deepest depth this ego is generated at, the port of C's {@code ego->alloc_max} (the "max"
     * of the {@code alloc:} line). Unlike {@link #allocMin} it is a hard limit: the data file says the
     * ego will never appear deeper.
     *
     * <p>Field allocMax coded before 260816, commented in full on 261014.
     */
    private int allocMax;

    /**
     * The extra to-hit this ego rolls, the port of C's {@code ego->to_h}, from the first value of the
     * {@code combat:} line. A range, not a number, because it is rolled per object. {@code null} when
     * the data file gives no {@code combat:} line.
     *
     * <p>Field toHit coded before 260816, commented in full on 261014.
     */
    private Random toHit;
    /**
     * The extra to-damage this ego rolls, the port of C's {@code ego->to_d}, from the second value of
     * the {@code combat:} line. {@code null} when the data file gives no {@code combat:} line.
     *
     * <p>Field toDam coded before 260816, commented in full on 261014.
     */
    private Random toDam;
    /**
     * The extra to-armour-class this ego rolls, the port of C's {@code ego->to_a}, from the third
     * value of the {@code combat:} line. {@code null} when the data file gives no {@code combat:}
     * line.
     *
     * <p>Field toAC coded before 260816, commented in full on 261014.
     */
    private Random toAC;

    /**
     * The floor on the to-hit bonus, the port of C's {@code ego->min_to_h}, from the
     * {@code min-combat:} line. <b>C's value {@code 255} ({@code NO_MINIMUM} in
     * {@code obj-make.h}) means "no minimum"</b>, which is how zero stays a usable floor; C's
     * {@code parse_ego_name} starts every ego at {@code 255} and {@code ego_apply_magic} raises a
     * bonus only when the floor is not {@code 255}. {@code EgoItemAssembler} does the same: it starts
     * the field at {@code 255} and replaces it only when the {@code min-combat:} line gives a value,
     * so an ego without the line holds {@code 255} here. This class has no getter for the field yet,
     * so nothing applies the floor.
     *
     * <p>Field minToHit coded before 260816, commented in full on 261014.
     */
    private int minToHit;
    /**
     * The floor on the to-damage bonus, the port of C's {@code ego->min_to_d}; {@code 255} means no
     * minimum. See {@link #minToHit}.
     *
     * <p>Field minToDam coded before 260816, commented in full on 261014.
     */
    private int minToDam;
    /**
     * The floor on the to-armour-class bonus, the port of C's {@code ego->min_to_a}; {@code 255}
     * means no minimum. See {@link #minToHit}.
     *
     * <p>Field minToAC coded before 260816, commented in full on 261014.
     */
    private int minToAC;

    /**
     * The activation this ego grants, the port of C's {@code ego->activation}, from the {@code act:}
     * line; {@code null} for an ego that grants none.
     *
     * <p>Field activation coded before 260816, commented in full on 261014.
     */
    private Activation activation;
    /**
     * The recharge time of the granted activation, the port of C's {@code ego->time}, from the
     * {@code time:} line. {@code null} when the line is absent.
     *
     * <p>Field time coded before 260816, commented in full on 261014.
     */
    private Random time;

    /**
     * Whether the player has met an identified item of this ego, the port of C's {@code ego->everseen}.
     * It is what keeps the ignore menus from spoiling egos the player has not yet seen. Read through
     * {@link #isEverSeen()}.
     *
     * <p>The assembler passes {@code false}, and {@link #setEverSeen(boolean)} is the only later
     * write. C sets it in {@code object_desc} ({@code obj-desc.c}) when the player's known copy of an
     * object names its ego, and restores it from the savefile ({@code load.c}); nothing calls the
     * setter for either yet.
     *
     * <p>Field everSeen coded before 260816, commented in full on 261014.
     */
    private boolean everSeen;

    /**
     * Which ignore categories the player has told the game to hide this ego under - the port of C's
     * {@code ego->ignore_types[]}.
     *
     * <p>A map where C has a fixed array, but not a sparse one: the constructor fills it with a
     * {@code false} for every category, so it answers for all of them from the start.
     * {@link #getIgnoreType(IgnoreType)} unboxes the value directly and relies on that. C's row has
     * {@code ITYPE_MAX} entries, so {@link IgnoreType#ITYPE_MAX} has no slot there; here it gets a
     * key like the rest, and it is the callers' loops that stop short of it.
     *
     * <p>Field ignoreTypes coded before 261014, commented in full on 261014.
     */
    private Map<IgnoreType, Boolean> ignoreTypes;

    /**
     * Build an ego-item template from its parsed data-file fields.
     *
     * <p>Every argument is stored as given, with no copy: the collections and the flag sets become
     * this ego's own, shared with the caller, which is why {@link #copy()} builds fresh ones before
     * calling this. The one thing the constructor makes itself is {@link #ignoreTypes}, which it
     * fills with {@code false} for every {@link IgnoreType} (the zero-filled row C allocates for
     * each ego in its ignore table), so a new ego starts with nothing marked as ignorable.
     *
     * <p>Constructor EgoItem coded before 260817, commented in full on 261014.
     *
     * @param name         ego name, as in {@code ego->name}
     * @param desc         description, stored as this ego's {@code text}
     * @param egoIndex     ego-table index, as in {@code ego->eidx}
     * @param cost         the ego's added value; a plain integer
     * @param flags        object flags this ego adds
     * @param flagsOff     object flags this ego removes
     * @param kindFLags    kind flags this ego adds
     * @param modifiers    the ranges rolled for the modifiers this ego grants
     * @param minModifiers the floors for those modifiers
     * @param elInfo       per-element resistances and vulnerabilities this ego adds
     * @param brands       brands this ego adds
     * @param slays        slays this ego adds
     * @param curses       curses this ego carries, each holding the ego's power for it
     * @param rating       level-feeling rating contribution
     * @param allocProb    commonness; zero keeps the ego out of the allocation table
     * @param allocMin     shallowest normal depth
     * @param allocMax     deepest depth, never exceeded
     * @param possItems    the base kinds this ego may be applied to
     * @param toHit        to-hit bonus range, or {@code null}
     * @param toDam        to-damage bonus range, or {@code null}
     * @param toAC         to-AC bonus range, or {@code null}
     * @param minToHit     floor on the to-hit bonus; C's {@code 255} means none
     * @param minToDam     floor on the to-damage bonus; C's {@code 255} means none
     * @param minToAC      floor on the to-AC bonus; C's {@code 255} means none
     * @param activation   granted activation, or {@code null}
     * @param time         activation recharge time, or {@code null}
     * @param everSeen     whether the player has already seen this ego
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
        this.ignoreTypes = new HashMap<>();
        for (IgnoreType type : IgnoreType.values()) {
            this.ignoreTypes.put(type, false);
        }
    }

    /**
     * Reports whether the player has ever seen an ego of this type identified, the port of reading
     * C's {@code ego->everseen}.
     *
     * <p>Not knowledge, but a record of whether the news has been broken. Recognizing a Long Sword of
     * Extra Attacks for the first time is worth a message; the tenth is not, and this flag is how
     * {@link PlayerKnowledge#knowObject} tells the two apart.
     *
     * <p>Function isEverSeen commented in full on 260816.
     *
     * @return {@code true} if an ego of this type has been seen before
     */
    public boolean isEverSeen() {
        return everSeen;
    }

    /**
     * Records whether the player has met an identified item of this ego, the port of writing C's
     * {@code ego->everseen}.
     *
     * <p>A plain write of the value given. C's one routine write is {@code object_desc}
     * ({@code obj-desc.c}), which sets it {@code true} whenever the player's known copy of an object
     * names its ego (and the call is not a spoiler), and never clears it; {@code load.c} also writes it
     * from the savefile, either way. This method does neither of those tests: deciding when to call it
     * is the caller's, and nothing calls it yet. {@link #isEverSeen()} is the read.
     *
     * <p>Function setEverSeen coded on 261014, commented in full on 261014.
     *
     * @param seen {@code true} once the player has seen an identified item of this ego
     */
    public void setEverSeen(boolean seen) {
        this.everSeen = seen;
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
     * Ring of the Mouse gives some amount of stealth, not a fixed amount. That is why recognizing an
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
     * <p>Each {@link ElementInfo} is what the ego adds for that element, from the resist, immunity and
     * vulnerability tokens on the {@code flags:} line and the element values on the {@code values:}
     * line of {@code ego_item.txt}. C keeps a full {@code ELEM_MAX} array, so a caller that finds no
     * entry here is looking at a slot C would have left zeroed.
     *
     * <p>The map is handed out as is, not copied; callers must not modify it.
     *
     * <p>Function getElInfo coded before 260816, commented in full on 261014.
     *
     * @return this ego's element info by element, shared with this instance
     */
    public Map<ElementEnum, ElementInfo> getElInfo() {
        return elInfo;
    }

    /**
     * Returns the brands this ego confers, the port of reading C's {@code ego->brands}.
     *
     * <p>C holds a {@code bool} per brand index and tests {@code ego->brands[i]}; here a brand the ego
     * confers is a member of the set and every other brand is absent. The set is handed out as is, not
     * copied; callers must not modify it.
     *
     * <p>Function getBrands coded before 260816, commented in full on 261014.
     *
     * @return this ego's brands, shared with this instance
     */
    public Set<Brand> getBrands() {
        return brands;
    }

    /**
     * Returns the slays this ego confers, the port of reading C's {@code ego->slays}.
     *
     * <p>C holds a {@code bool} per slay index and tests {@code ego->slays[i]}; here a slay the ego
     * confers is a member of the set and every other slay is absent. The set is handed out as is, not
     * copied; callers must not modify it.
     *
     * <p>Function getSlays coded before 260816, commented in full on 261014.
     *
     * @return this ego's slays, shared with this instance
     */
    public Set<Slay> getSlays() {
        return slays;
    }

    /**
     * Returns the curses this ego carries, the port of reading C's {@code ego->curses}.
     *
     * <p>Each curse is mapped to a {@link CurseData} whose power is the one the ego's {@code curse:}
     * line gives, not to whether the player knows of it, which is not a property of the ego at all. C
     * stores that power alone ({@code int *curses}, indexed by curse); the timeout half of the
     * {@code CurseData} is not an ego property. C rolls it from the curse's own {@code time} when the
     * curse is appended to an object, and the assembler leaves it at {@code 0} here. A curse with no
     * entry is one C would hold as a zero power.
     *
     * <p>Function getCurses coded before 260816, commented in full on 261014.
     *
     * @return this ego's curses and their data, shared with this instance
     */
    public Map<Curse, CurseData> getCurses() {
        return curses;
    }

    /**
     * The flags this ego <em>removes</em> from the item it is applied to — C's {@code ego->flags_off}
     * , the {@code flags-off:} line in {@code ego_item.txt}.
     *
     * <p>Subtractive rather than additive, and applied after the additive sets: {@code object_flags_known}
     * in {@code obj-util.c} unions in the ego's flags and then differences these out again, and
     * {@code ego_apply_magic} in {@code obj-make.c} does the same to the object's real flags, so an
     * ego can cancel a flag its base kind granted. The data file's own example is the Everburning
     * lantern, whose {@code flags-off:TAKES_FUEL} line removes the fuel requirement the lantern kind
     * carries.
     *
     * <p>Function getOffFlags coded before 260820, commented in full on 261014.
     *
     * @return a read-only view of the flags this ego suppresses
     */
    public FlagView<ObjectFlag> getOffFlags() {
        return flagsOff;
    }

    /**
     * Answers whether the player has marked this ego as ignorable under one category - the port of
     * reading C's {@code ego->ignore_types[type]}.
     *
     * <p>Read by {@code ItemObject.egoIsIgnored}, and through it by the ignore machinery that
     * decides what {@code noticeStuff} drops.
     *
     * <p>Unboxes the map's value directly, which is safe because the constructor fills the map for
     * every category; see the note on {@link #ignoreTypes}.
     *
     * <p>Function getIgnoreType commented in full on 260827.
     *
     * @param type the ignore category to test
     * @return {@code true} if this ego is marked ignorable under that category
     */
    public boolean getIgnoreType(IgnoreType type) {
        return ignoreTypes.get(type);
    }

    /**
     * Marks this ego as ignorable under one category - the port of setting C's
     * {@code ego->ignore_types[type] = true}.
     *
     * <p>Only the table write: C's {@code ego_ignore} (called from the item ignore command in
     * {@code ui-object.c}) also sets {@code PN_IGNORE} in {@code player->upkeep->notice} so the pack
     * is re-checked, and that is the caller's to do. {@link #clearIgnoreType(IgnoreType)} is the
     * opposite write. C's savefile reader ({@code load.c}) restores the marks by toggling rather than
     * setting, which has no method here.
     *
     * <p>Function setIgnoreType coded before 260827, commented in full on 261014.
     *
     * @param ignoreType the ignore category to mark
     */
    public void setIgnoreType(IgnoreType ignoreType) {
        ignoreTypes.put(ignoreType, true);
    }

    /**
     * Clears this ego's ignorable mark for one category - the port of setting C's
     * {@code ego_ignore_types[eidx][itype] = false}.
     *
     * <p>Used two ways in C: {@code ego_ignore_clear} does it for the category of one object, and
     * {@code ignore_birth_init} ({@code obj-ignore.c}) does it for every category of every ego when a
     * new character is born, which is what {@code ObjectIgnore.ignoreBirthInit} calls this for.
     * Clearing a category that is not marked is harmless.
     *
     * <p>Only the table write: C's {@code ego_ignore_clear} also sets {@code PN_IGNORE} in
     * {@code player->upkeep->notice}, and that is the caller's to do. C's {@code ego_ignore_toggle}
     * flips the entry instead, and has no method here.
     *
     * <p>Function clearIgnoreType coded before 261014, commented in full on 261014.
     *
     * @param ignoreType the ignore category to unmark
     */
    public void clearIgnoreType(IgnoreType ignoreType) {
        ignoreTypes.put(ignoreType, false);
    }

    /**
     * Returns an independent copy of this ego template.
     *
     * <p>Deep-copied because their contents are mutable: the three flag sets, the modifier map (each
     * {@link uk.co.jackoftradesltd.middle.numerics.Random} copied in turn), the minimum-modifier map,
     * the element info (each entry copied), the curse map (each {@code CurseData} rebuilt), the
     * three combat dice, the activation, the recharge dice and the ignore-category map.
     *
     * <p>Shared deliberately: the brand and slay sets are rebuilt but their members are not, because
     * a {@code Brand} and a {@code Slay} are immutable registry entries every carrier points at; the
     * possible-items list is likewise rebuilt around shared {@code ObjectKind} templates, as C
     * shares its {@code kind} pointers.
     *
     * <p>The ignore-category map is assigned after construction because the constructor does not
     * take it.
     *
     * <p>Function copy commented in full on 260827.
     *
     * @return a new ego template that shares no mutable state with this one, bar the noted exception
     */
    public EgoItem copy() {
        String newName = this.name;
        String newText = this.text;
        List<ObjectKind> possItems = new ArrayList<>(this.possItems);
        int egoIndex = this.egoIndex;
        int cost = this.cost;
        Flag<ObjectFlag> oFlagOn = new Flag<>(ObjectFlag.class);
        oFlagOn.copyFrom(flags);
        Flag<ObjectFlag> oFlagOff = new Flag<>(ObjectFlag.class);
        oFlagOff.copyFrom(flagsOff);
        Flag<ObjectKindFlag> kFlag = new Flag<>(ObjectKindFlag.class);
        kFlag.copyFrom(kindFLags);
        Map<ObjectModifier, Random> newModifiers = new HashMap<>();
        for (ObjectModifier om : this.modifiers.keySet()) {
            newModifiers.put(om, this.modifiers.get(om).copy());
        }
        Map<ObjectModifier, Integer> newMinModifiers = new HashMap<>();
        for (ObjectModifier om : this.minModifiers.keySet()) {
            newMinModifiers.put(om, this.minModifiers.get(om));
        }
        Map<ElementEnum, ElementInfo> newElInfo = new HashMap<>();
        for (ElementEnum em : this.elInfo.keySet()) {
            newElInfo.put(em, this.elInfo.get(em).copy());
        }
        Set<Brand> newBrands = new HashSet<>(this.brands);
        Set<Slay> newSlays = new HashSet<>(this.slays);
        Map<Curse, CurseData> newCurses = new HashMap<>();
        for (Curse c : this.curses.keySet()) {
            newCurses.put(c, new CurseData(this.curses.get(c)));
        }
        int newRating = this.rating;
        int newAllocProb = this.allocProb;
        int newAllocMin = this.allocMin;
        int newAllocMax = this.allocMax;
        Random toHit = this.toHit == null ? null : this.toHit.copy();
        Random toDam = this.toDam == null ? null : this.toDam.copy();
        Random toAC = this.toAC == null ? null : this.toAC.copy();
        int newMinToHit = this.minToHit;
        int newMinToDam = this.minToDam;
        int newMinToAC = this.minToAC;
        Activation newActivation = this.activation == null ? null : this.activation.copy();
        Random time = this.time == null ? null : this.time.copy();
        boolean newEverSeen = this.everSeen;
        Map<IgnoreType, Boolean> newIgnoreTypes = new HashMap<>();
        for (IgnoreType type : this.ignoreTypes.keySet()) {
            newIgnoreTypes.put(type, this.ignoreTypes.get(type));
        }

        EgoItem copy = new EgoItem(newName, newText, egoIndex, cost, oFlagOn, oFlagOff, kFlag,
                newModifiers, newMinModifiers, newElInfo, newBrands, newSlays, newCurses,
                newRating, newAllocProb, newAllocMin, newAllocMax, possItems, toHit,
                toDam, toAC, newMinToHit, newMinToDam, newMinToAC, newActivation,
                time, newEverSeen);
        copy.ignoreTypes = newIgnoreTypes;
        return copy;
    }

    /**
     * Answers whether an ignore category is a valid one for this ego - the port of C's
     * {@code ego_has_ignore_type} in {@code obj-ignore.c}.
     *
     * <p>Walks every kind this ego may be applied to ({@link #possItems}) and, for each, every row of
     * {@link ObjectIgnoreTables#qualityMapping}. It answers {@code true} on the first row where all three hold:
     * the row's tval is the kind's tval, the row's ignore type is the one asked for, and the row's
     * identifier occurs somewhere inside the <em>kind's</em> name (C's {@code strstr}, here
     * {@link String#contains}). The identifier is matched against the kind, not the ego: "Chaos" is
     * looked for in "Chaos Dragon Scale Mail", never in "of Slaying". An empty identifier occurs in every
     * name, so a row with {@code ""} means "any kind of this tval".
     *
     * <p>The match is case sensitive in both languages, so {@code "Bow"} does not hit "Light Crossbow".
     * An ego with no possible kinds, or none whose tval and name fit a row for that category, answers
     * {@code false}.
     *
     * <p>Used to decide which ignore categories the options menu offers for an ego; it does not read
     * {@link #ignoreTypes}, which records what the player has chosen rather than what is valid.
     *
     * <p>Function egoHasIgnoreType commented in full on 261008.
     *
     * @param ignoreType the ignore category to test
     * @return {@code true} if some possible kind of this ego falls under that category
     */
    public boolean egoHasIgnoreType(IgnoreType ignoreType) {
        // Go through all possible item kinds
        for (ObjectKind egoKind : possItems) {
            for (ObjectIgnoreTables.QualityMapping mapping : ObjectIgnoreTables.qualityMapping) {
                if (mapping.tval() == egoKind.gettValue()
                        && mapping.ignoreType() == ignoreType
                        && egoKind.getName().contains(mapping.identifier()))
                    return true;
            }
        }

        return false;
    }
}