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
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Everything the player has learned about object properties in general — which runes they can
 * read. The port of C's {@code p->obj_k} ({@code src/player.h}), and the store behind
 * {@code player_knows_rune} and {@code player_learn_rune} ({@code src/obj-knowledge.c}).
 *
 * <p>This is knowledge of <em>properties</em>, not of items. Learning the rune of fire resistance
 * once means every future item carrying that property shows it immediately; the player does not
 * relearn it per sword. What an individual item reveals is separate, and lives on that item's
 * {@code known} counterpart.
 *
 * <p><b>Deliberate divergence from the C original.</b> C hangs this off the player as a whole
 * {@code struct object}, allocated by {@code object_new()} in {@code init_player}. That buys C a
 * place to put the knowledge without declaring a new type, and costs it a struct where all but
 * twelve fields are meaningless: an obj_k has a kind, an ego, an artifact, a grid, a weight, a
 * timeout, an origin, and — worst of the set — a {@code known} pointer back to a "known version"
 * of the knowledge itself. Every dereference of {@code obj_k} in the 4.2.6 tree touches only
 * {@code flags}, {@code modifiers}, {@code el_info[].res_level}, {@code brands}, {@code slays},
 * {@code curses[].power}, {@code to_h}, {@code to_d}, {@code to_a}, {@code ac}, {@code dd} and
 * {@code ds}. Those twelve are the fields below, and nothing else is carried.
 *
 * <p>Two shapes change with the split. C stores the property fields as integers or index arrays
 * because it is reusing an object; here each is the narrowest thing that answers the question —
 * a {@link Flag} where C has a bitflag or an array used only as 0/1, a {@link Set} where C has a
 * {@code bool} array indexed by registry position. The one C field with no counterpart is
 * {@code el_info[].flags}, which the savefile writes and reads but which no code ever consults.
 *
 * <p>Every accessor comes in a pair: a {@code somethingIsKnown} query and a {@code learnSomething}
 * mutator that returns whether the call changed anything. That return is not incidental —
 * {@code player_learn_rune} prints "You have learned the rune of..." only when something was
 * genuinely new, so a learner that lies about it produces either a silent discovery or a message
 * on every subsequent hit.
 *
 * <p>An instance starts empty, matching C's zeroing allocation. The knowledge a character begins
 * play with is applied by the birth code, not by the constructor. {@code player_outfit}
 * ({@code src/player-birth.c}) sets {@link #getDd()}, {@link #getDs()} and {@link #getAc()} to 1
 * and switches on every object flag whose property subtype is light, digging, throwing or
 * curse-only; {@code do_cmd_accept_character} (same file) then sets the three combat bonuses to 1
 * under a comment calling it a hack.
 *
 * <p>Class KnownObject coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 * @see ItemObject
 */
public class KnownObject {
    /**
     * Which object modifiers the player can read, C's {@code obj_k->modifiers[]}.
     *
     * <p>C declares that array as {@code int16_t} but only ever writes 1 into it and tests it for
     * truth, so a {@link Flag} loses nothing. It does change the call sites that multiply by it —
     * {@code player-calcs.c} computes {@code stat_add[STAT_STR] * obj_k->modifiers[OBJ_MOD_STR]}
     * to zero out an unknown bonus, which becomes a conditional here.
     *
     * <p>Field modifierFlag coded before 261009, commented in full on 261009.
     */
    private Flag<ObjectModifier> modifierFlag;
    /**
     * Which object flags the player can read, C's {@code obj_k->flags}. The one field where C's
     * representation and this one already agree, both being a set of flags.
     *
     * <p>Field objectFlags coded before 261009, commented in full on 261009.
     */
    private Flag<ObjectFlag> objectFlags;

    /**
     * Which elemental resistances the player can read, C's {@code obj_k->el_info[].res_level}.
     *
     * <p>A boolean rather than an {@link ElementInfo}, because on the knowledge side the level is
     * not a level: C writes 1 to mean "known" and tests it for truth. The {@code flags} half of
     * C's {@code element_info} is dropped, being savefile-only. The map holds an entry for every
     * real element and none for {@code ELEM_NONE} or {@code ELEM_MAX}.
     *
     * <p>Field elementResistInfo coded before 261009, commented in full on 261009.
     */
    private Map<ElementEnum, Boolean> elementResistInfo;
    /**
     * Whether the player can read to-hit bonuses, C's {@code obj_k->to_h}. Kept as an int rather
     * than a boolean because C multiplies by it — {@code obj->known->to_h = p->obj_k->to_h *
     * obj->to_h} — so the 0/1 value does the masking directly.
     *
     * <p>Field toH coded before 261009, commented in full on 261009.
     */
    private int toH;
    /**
     * Whether the player can read to-damage bonuses, C's {@code obj_k->to_d}. See {@link #toH}.
     *
     * <p>Field toD coded before 261009, commented in full on 261009.
     */
    private int toD;
    /**
     * Whether the player can read to-armour bonuses, C's {@code obj_k->to_a}. See {@link #toH}.
     *
     * <p>Field toA coded before 261009, commented in full on 261009.
     */
    private int toA;
    /**
     * Which curses the player recognizes, C's {@code obj_k->curses[].power}.
     *
     * <p>C's {@code curse_data} carries a power and a timeout, but the knowledge copy uses only
     * power, and only as 0/1 — {@code player_knows_curse} is {@code curses[index].power == 1}.
     * Held as a map rather than a set because, unlike brands and slays, it is populated up front
     * from the registry so that an unrecognized curse is distinguishable from a known-false one.
     *
     * <p>Field curses coded before 261009, commented in full on 261009.
     */
    private Map<Curse, Boolean> curses;
    /**
     * Which brands the player recognizes, C's {@code obj_k->brands[]}.
     *
     * <p>A set rather than a map, because membership is the whole of the state: C's array is
     * indexed by registry position and holds nothing but a bool. Membership stands for the brand's
     * whole equivalence class — see {@link #learnBrand(Brand)}.
     *
     * <p>Field brands coded before 261009, commented in full on 261009.
     */
    private Set<Brand> brands;
    /**
     * Which slays the player recognizes, C's {@code obj_k->slays[]}. As {@link #brands}, with the
     * class defined by monsters slain rather than by name — see {@link #learnSlay(Slay)}.
     *
     * <p>Field slays coded before 261009, commented in full on 261009.
     */
    private Set<Slay> slays;
    /**
     * Whether the player can read armour class, C's {@code obj_k->ac}. A 0/1 multiplier like
     * {@link #toH}: {@code obj->known->ac = obj->ac * p->obj_k->ac}, and {@code obj-desc.c} gates
     * printing an armour value on it.
     *
     * <p>Zero here is the pre-birth state. {@code player_outfit} raises it to 1 as part of the
     * "obvious object knowledge" every character starts with, so it is 1 for the whole of play.
     *
     * <p>Field ac coded before 261009, commented in full on 261009.
     */
    private int ac = 0;
    /**
     * Whether the player can read damage dice, C's {@code obj_k->dd}. See {@link #ac} for the
     * multiplier convention and the birth-time initialization; {@code obj-desc.c} prints the dice
     * only when this and {@link #ds} are both set.
     *
     * <p>Field dd coded before 261009, commented in full on 261009.
     */
    private int dd = 0;
    /**
     * Whether the player can read damage sides, C's {@code obj_k->ds}. See {@link #dd}.
     *
     * <p>Field ds coded before 261009, commented in full on 261009.
     */
    private int ds = 0;

    /**
     * Builds an empty knowledge set — nothing learned, every property unreadable. The port of the
     * {@code object_new()} and {@code mem_zalloc} calls that build {@code p->obj_k} in
     * {@code init_player} ({@code src/player.c}).
     *
     * <p>Construction reads {@link ObjectRegistry} for the curse list, so it cannot run before the
     * data files are parsed. C has the same ordering constraint for the same reason — it sizes
     * {@code obj_k}'s arrays from {@code z_info->curse_max} and friends — which is why
     * {@code p->obj_k} is allocated in {@code init_player} rather than when the player struct
     * itself is created.
     *
     * <p>Function KnownObject coded before 261009, commented in full on 261009.
     */
    public KnownObject() {
        initSlays();
        initBrands();
        initModifiers();
        initObjectFlags();
        initResistances();
        initToValues();
        initCurses();
    }

    /**
     * Populates the curse map with every registered curse, all unrecognized. C reaches the same
     * state with {@code mem_zalloc(z_info->curse_max * sizeof(struct curse_data))}.
     *
     * <p>Replaces any previous map, so calling it again forgets every recognized curse.
     *
     * <p>Function initCurses coded before 261009, commented in full on 261009.
     */
    public void initCurses() {
        curses = new HashMap<>();
        for (Curse curse : ObjectRegistry.getCurses()) {
            curses.put(curse, false);
        }
    }

    /**
     * The port of C's {@code player_knows_curse}, which is a bare {@code curses[index].power == 1}
     * on an array guaranteed to be long enough. This has to allow for a curse that is not in the
     * map at all — one built outside the registry — and answers false for it, on the grounds that
     * a curse the player's knowledge has never heard of cannot be one they recognize.
     *
     * <p>Function curseIsKnown coded before 261009, commented in full on 261009.
     *
     * @param curse the curse to ask about
     * @return true if the player recognizes this curse
     */
    public boolean curseIsKnown(Curse curse) {
        if (curses.containsKey(curse))
            return curses.get(curse);
        return false;
    }

    /**
     * Records that the player now recognizes a curse. Curses are the one property with no
     * equivalence class — each has its own rune — so this marks exactly the curse it is given. The
     * port of the curse arm of {@code player_learn_rune} ({@code obj-knowledge.c}), which sets
     * {@code curses[j].power = 1} only when {@code player_knows_curse} is false.
     *
     * <p>A curse outside the registry is added to the map rather than rejected; C would trip an
     * assert, and the registry is the only legitimate source of curses.
     *
     * <p>Function learnCurse coded before 261009, commented in full on 261009.
     *
     * @param curse the curse now recognized
     * @return true if this was new knowledge, false if the curse was already recognized
     */
    public boolean learnCurse(Curse curse) {
        boolean learned = !curseIsKnown(curse);
        curses.put(curse, true);
        return learned;
    }

    /**
     * Clears the three combat bonuses to unknown. Written out rather than left to Java's default
     * field initialization so that the constructor's list of {@code init} calls reads as the
     * complete account of the starting state.
     *
     * <p>Function initToValues coded before 261009, commented in full on 261009.
     */
    private void initToValues() {
        toH = 0;
        toD = 0;
        toA = 0;
    }

    /**
     * Asks whether the player can read to-hit bonuses on items, the port of the truth test on C's
     * {@code p->obj_k->to_h} (for example in {@code obj-desc.c}).
     *
     * <p>Function toHIsKnown coded before 261009, commented in full on 261009.
     *
     * @return true if the player can read an item's to-hit bonus
     */
    public boolean toHIsKnown() {
        return toH != 0;
    }

    /**
     * Records that the player can now read to-hit bonuses. The port of the {@code COMBAT_RUNE_TO_H}
     * arm of {@code player_learn_rune} ({@code obj-knowledge.c}), which sets {@code to_h} to 1 only
     * if it was 0 and counts the rune as learned only in that case.
     *
     * <p>Function learnToH coded before 261009, commented in full on 261009.
     *
     * @return true if this was new knowledge
     */
    public boolean learnToH() {
        boolean learned = toH == 0;
        toH = 1;
        return learned;
    }

    /**
     * Asks whether the player can read to-damage bonuses on items. See {@link #toHIsKnown()}.
     *
     * <p>Function toDIsKnown coded before 261009, commented in full on 261009.
     *
     * @return true if the player can read an item's to-damage bonus
     */
    public boolean toDIsKnown() {
        return toD != 0;
    }

    /**
     * Records that the player can now read to-damage bonuses. The {@code COMBAT_RUNE_TO_D} arm of
     * {@code player_learn_rune}; see {@link #learnToH()}.
     *
     * <p>Function learnToD coded before 261009, commented in full on 261009.
     *
     * @return true if this was new knowledge
     */
    public boolean learnToD() {
        boolean learned = toD == 0;
        toD = 1;
        return learned;
    }

    /**
     * Asks whether the player can read to-armour bonuses on items. See {@link #toHIsKnown()}.
     *
     * <p>Function toAIsKnown coded before 261009, commented in full on 261009.
     *
     * @return true if the player can read an item's to-armour bonus
     */
    public boolean toAIsKnown() {
        return toA != 0;
    }

    /**
     * Records that the player can now read to-armour bonuses. The {@code COMBAT_RUNE_TO_A} arm of
     * {@code player_learn_rune}; see {@link #learnToH()}.
     *
     * <p>Function learnToA coded before 261009, commented in full on 261009.
     *
     * @return true if this was new knowledge
     */
    public boolean learnToA() {
        boolean learned = toA == 0;
        toA = 1;
        return learned;
    }

    /**
     * Populates the resistance map with every real element, all unknown. C indexes an array by
     * element, so its bounds are the elements; here the two sentinels have to be skipped by hand,
     * and are skipped again on the way in and out so that neither can be marked or reported known.
     *
     * <p>Function initResistances coded before 261009, commented in full on 261009.
     */
    private void initResistances() {
        elementResistInfo = new HashMap<>();

        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX)
                continue;

            elementResistInfo.put(element, false);
        }
    }

    /**
     * The port of C's {@code obj_k->el_info[element].res_level} test. C indexes by element number
     * and so never sees a sentinel; here the two sentinels answer false rather than being looked up.
     *
     * <p>Function resistanceIsKnown coded before 261009, commented in full on 261009.
     *
     * @param element the element to ask about
     * @return true if the player can read resistance to this element; false for the sentinels
     */
    public boolean resistanceIsKnown(ElementEnum element) {
        if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX)
            return false;
        return elementResistInfo.getOrDefault(element, false);
    }

    /**
     * Records that the player can now read resistance to an element. Answers false for a sentinel
     * without recording anything, which is also the right answer to "was that new knowledge" —
     * there is no rune for {@code ELEM_NONE} to learn. The port of the resist arm of
     * {@code player_learn_rune} ({@code obj-knowledge.c}), which sets {@code res_level} to 1 only
     * if it was 0.
     *
     * <p>Function learnResistance coded before 261009, commented in full on 261009.
     *
     * @param element the element whose resistance is now readable
     * @return true if this was new knowledge
     */
    public boolean learnResistance(ElementEnum element) {
        if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX)
            return false;

        boolean learned = !resistanceIsKnown(element);
        elementResistInfo.put(element, true);
        return learned;
    }

    /**
     * Creates the empty object-flag set, the port of the zeroed {@code obj_k->flags}.
     *
     * <p>Function initObjectFlags coded before 261009, commented in full on 261009.
     */
    private void initObjectFlags() {
        objectFlags = new Flag<>(ObjectFlag.class);
    }

    /**
     * The port of C's {@code of_has(p->obj_k->flags, flag)}.
     *
     * <p>Function flagIsKnown coded before 261009, commented in full on 261009.
     *
     * @param flag the object flag to ask about
     * @return true if the player can read this flag on an item
     */
    public boolean flagIsKnown(ObjectFlag flag) {
        return objectFlags.has(flag);
    }

    /**
     * Records that the player can now read an object flag. C's flag arm of
     * {@code player_learn_rune} is a bare {@code if (of_on(p->obj_k->flags, r->index)) learned =
     * true;} — {@link Flag#on} already answers the "was it new" question the same way, so this
     * needs no test of its own.
     *
     * <p>Function learnFlag coded before 261009, commented in full on 261009.
     *
     * @param flag the object flag now readable
     * @return true if this was new knowledge
     */
    public boolean learnFlag(ObjectFlag flag) {
        return objectFlags.on(flag);
    }

    /**
     * Returns a copy of the known object flags, for the callers that need the set whole rather
     * than one flag at a time — {@code player_knows_ego} intersects it against an ego's flags, and
     * {@code equip_learn_after_time} negates it to find the timed flags still unlearned.
     *
     * <p>A copy, not the live set, so that a caller inverting it to compute "everything not yet
     * known" cannot leave the player omnisciently marked. C is exposed to exactly that and dodges
     * it by copying first: {@code object_flags(p->obj_k, f); of_negate(f);} negates {@code f},
     * never the player's own flags.
     *
     * <p>Function getFlags coded before 261009, commented in full on 261009.
     *
     * @return an independent copy of the known object flags
     */
    public Flag<ObjectFlag> getFlags() {
        Flag<ObjectFlag> flag = new Flag<>(ObjectFlag.class);
        flag.copyFrom(objectFlags);
        return flag;
    }

    /**
     * Creates the empty modifier set, the port of the zeroed {@code obj_k->modifiers[]}.
     *
     * <p>Function initModifiers coded before 261009, commented in full on 261009.
     */
    private void initModifiers() {
        modifierFlag = new Flag<>(ObjectModifier.class);
    }

    /**
     * The port of C's {@code p->obj_k->modifiers[index]} test.
     *
     * <p>Function modifierIsKnown coded before 261009, commented in full on 261009.
     *
     * @param modifier the modifier to ask about
     * @return true if the player can read this modifier on an item
     */
    public boolean modifierIsKnown(ObjectModifier modifier) {
        return modifierFlag.has(modifier);
    }

    /**
     * Records that the player can now read a modifier. The port of the mod arm of
     * {@code player_learn_rune} ({@code obj-knowledge.c}), which sets
     * {@code modifiers[r->index] = 1} only if it was 0 and counts the rune as learned only then.
     *
     * <p>Function learnModifier coded before 261009, commented in full on 261009.
     *
     * @param modifier the modifier now readable
     * @return true if this was new knowledge
     */
    public boolean learnModifier(ObjectModifier modifier) {
        return modifierFlag.on(modifier);
    }

    /**
     * The port of C's {@code player_knows_brand}, which is a bare array lookup. It can afford to
     * be that cheap because the cost of grouping is paid on the learning side — see
     * {@link #learnBrand(Brand)} — and this port keeps the same division of labour.
     *
     * <p>Function brandIsKnown coded before 261009, commented in full on 261009.
     *
     * @param brand the brand to ask about
     * @return true if the player recognizes this brand
     */
    public boolean brandIsKnown(Brand brand) {
        return brands.contains(brand);
    }

    /**
     * Records that the player now recognizes a brand — and every other brand of the same name.
     *
     * <p>The fan-out is the point. Brands come in strengths: {@code brand.txt} holds ten entries
     * that are five names twice over, so a lightning brand and a strong lightning brand are
     * separate {@link Brand} objects that are not equal to each other. They share one rune, and
     * reading it reveals both. C does this the same way and in the same place, inside the brand
     * arm of {@code player_learn_rune}:
     *
     * <pre>{@code
     * for (j = 1; j < z_info->brand_max; j++)
     *     if (streq(brands[r->index].name, brands[j].name)) {
     *         p->obj_k->brands[j] = true;
     *         learned = true;
     *     }
     * }</pre>
     *
     * <p>Matching on the name follows C, and matters beyond mere fidelity: the brand arriving here
     * is whichever member of the group the rune happens to hold, so identity would learn the
     * representative and leave its twin unreadable.
     *
     * <p>The early return is C's guard on the same loop. It changes no answer — if the group is
     * already known every {@code add} returns false and the result is false anyway — but it saves
     * walking the registry on the repeat calls, which are the common case.
     *
     * <p>C walks {@code j = 1 .. brand_max - 1}, skipping the blank slot 0; the Java registry is
     * base 0 and holds only real brands, so the walk covers all of it (accepted deviation).
     *
     * <p>Function learnBrand coded before 261009, commented in full on 261009.
     *
     * @param brand any brand of the wanted kind, at any strength
     * @return true if this was new knowledge for any member of the group
     */
    public boolean learnBrand(Brand brand) {
        if (brandIsKnown(brand)) return false;

        boolean learned = false;

        for (Brand b : ObjectRegistry.getBrands()) {
            if (b.getName().equals(brand.getName())) {
                learned |= brands.add(b);
            }
        }

        return learned;
    }

    /**
     * Creates the empty brand set. Nothing is pre-populated from the registry, because membership
     * is the state: an absent brand is an unrecognized one. C's equivalent is the
     * {@code mem_zalloc(z_info->brand_max * sizeof(bool))} in {@code init_player}.
     *
     * <p>Function initBrands coded before 261009, commented in full on 261009.
     */
    public void initBrands() {
        brands = new HashSet<>();
    }

    /**
     * The port of C's {@code player_knows_slay}. As {@link #brandIsKnown(Brand)}, a plain
     * membership test made cheap by the grouping happening on the learning side.
     *
     * <p>Function slayIsKnown coded before 261009, commented in full on 261009.
     *
     * @param slay the slay to ask about
     * @return true if the player recognizes this slay
     */
    public boolean slayIsKnown(Slay slay) {
        return slays.contains(slay);
    }

    /**
     * Records that the player now recognizes a slay — and every other slay that kills the same
     * monsters. The slay counterpart of {@link #learnBrand(Brand)}, with one difference: the
     * equivalence is {@link Slay#sameMonsterSlain} rather than a name match, following C's
     * {@code same_monsters_slain} in the slay arm of {@code player_learn_rune}. Names would be too
     * coarse an axis — {@code slay.txt} has three names appearing twice at different strengths,
     * but the grouping C wants is over the monsters hit, which is a comparison of race flag and
     * base rather than of what the slay is called.
     *
     * <p>It is the same test {@code Rune.initRunes} de-duplicates the rune list with, so the two
     * cannot disagree about where the group boundaries fall.
     *
     * <p>As with brands, C's walk starts at slot 1 and this one covers the whole base-0 registry
     * (accepted deviation).
     *
     * <p>Function learnSlay coded before 261009, commented in full on 261009.
     *
     * @param slay any slay of the wanted kind, at any strength
     * @return true if this was new knowledge for any member of the group
     */
    public boolean learnSlay(Slay slay) {
        if (slayIsKnown(slay)) return false;

        boolean learned = false;

        for (Slay s : ObjectRegistry.getSlays()) {
            if (s.sameMonsterSlain(slay)) {

                learned |= slays.add(s);
            }
        }

        return learned;
    }

    /**
     * Creates the empty slay set. See {@link #initBrands()}.
     *
     * <p>Function initSlays coded before 261009, commented in full on 261009.
     */
    public void initSlays() {
        slays = new HashSet<>();
    }

    /**
     * Returns the armour-class knowledge as the 0/1 multiplier C uses it as, so that a caller can
     * write {@code item.getAc() * knowledge.getAc()} and get either the real value or nothing.
     *
     * <p>Function getAc coded before 261009, commented in full on 261009.
     *
     * @return 1 if the player can read armour class, 0 if not
     */
    public int getAc() {
        return ac;
    }

    /**
     * Returns the damage-dice knowledge as the 0/1 multiplier C uses it as
     * ({@code obj->known->dd = obj->dd * p->obj_k->dd}). See {@link #getAc()}.
     *
     * <p>Function getDd coded before 261009, commented in full on 261009.
     *
     * @return 1 if the player can read damage dice, 0 if not
     */
    public int getDd() {
        return dd;
    }

    /**
     * Returns the damage-sides knowledge as the 0/1 multiplier C uses it as
     * ({@code obj->known->ds = obj->ds * p->obj_k->ds}). See {@link #getAc()}.
     *
     * <p>Function getDs coded before 261009, commented in full on 261009.
     *
     * @return 1 if the player can read damage sides, 0 if not
     */
    public int getDs() {
        return ds;
    }

    /**
     * Returns the to-hit knowledge as the 0/1 multiplier C uses it as
     * ({@code obj->known->to_h = p->obj_k->to_h * obj->to_h}). See {@link #getAc()}.
     *
     * <p>Function getToH coded before 261009, commented in full on 261009.
     *
     * @return 1 if the player can read to-hit bonuses, 0 if not
     */
    public int getToH() {
        return toH;
    }

    /**
     * Returns the to-damage knowledge as the 0/1 multiplier C uses it as
     * ({@code obj->known->to_d = p->obj_k->to_d * obj->to_d}). See {@link #getAc()}.
     *
     * <p>Function getToD coded before 261009, commented in full on 261009.
     *
     * @return 1 if the player can read to-damage bonuses, 0 if not
     */
    public int getToD() {
        return toD;
    }

    /**
     * Returns which elements the player can read resistances for, the port of reading the
     * {@code res_level} column of C's {@code p->obj_k->el_info}.
     *
     * <p>The map has an entry for every element, so it is the {@link Boolean} value that carries the
     * answer, not the presence of the key. C stores a whole {@code element_info} per element and
     * uses its {@code res_level} as the one-or-zero knowledge bit; the port keeps only the bit,
     * because the flags beside it were never read on the knowledge object.
     *
     * <p>Live, not a copy. Callers read it; the write path is {@link #learnResistance}.
     *
     * <p>Function getElementResistInfo coded before 260816, commented in full on 261009.
     *
     * @return the per-element knowledge bits, shared with this instance
     */
    public Map<ElementEnum, Boolean> getElementResistInfo() {
        return elementResistInfo;
    }

    /**
     * Returns the to-armour knowledge as the 0/1 multiplier C uses it as
     * ({@code obj->known->to_a = p->obj_k->to_a * obj->to_a}). See {@link #getAc()}.
     *
     * <p>Function getToA coded before 261009, commented in full on 261009.
     *
     * @return 1 if the player can read to-armour bonuses, 0 if not
     */
    public int getToA() {
        return toA;
    }

    /**
     * Sets the damage-dice knowledge bit - the port of writing C's {@code p->obj_k->dd}. Birth sets
     * it to 1 outright in {@code player_outfit} ({@code player-birth.c}), giving the player damage
     * dice on every item from the start; the savefile loader ({@code load.c}) is the only other
     * writer, and nothing in the 4.2.6 tree ever writes it back to 0. Assignment itself does no
     * validation in either language.
     *
     * <p>Function setDD coded before 260904, commented in full on 261009.
     *
     * @param dd 1 if the player can read damage dice, 0 if not
     * @see #getDd()
     */
    public void setDD(int dd) {
        this.dd = dd;
    }

    /**
     * Sets the damage-sides knowledge bit - the port of writing C's {@code p->obj_k->ds}. Birth sets
     * it to 1 outright in {@code player_outfit} ({@code player-birth.c}); see {@link #setDD} for
     * the rest of that boundary, which the same statement group shares.
     *
     * <p>Function setDS coded before 260904, commented in full on 261009.
     *
     * @param ds 1 if the player can read damage sides, 0 if not
     * @see #getDs()
     */
    public void setDS(int ds) {
        this.ds = ds;
    }

    /**
     * Sets the armour-class knowledge bit - the port of writing C's {@code p->obj_k->ac}. Birth
     * sets it to 1 outright in {@code player_outfit} ({@code player-birth.c}); see {@link #setDD}
     * for the rest of that boundary, which the same statement group shares.
     *
     * <p>Function setAC coded before 260904, commented in full on 261009.
     *
     * @param ac 1 if the player can read armour class, 0 if not
     * @see #getAc()
     */
    public void setAC(int ac) {
        this.ac = ac;
    }

    /**
     * Sets the to-hit knowledge bit - the port of writing C's {@code p->obj_k->to_h}. Birth sets it
     * to 1 outright in {@code do_cmd_accept_character} ({@code player-birth.c}) under a comment
     * calling it a hack, on the grounds that it shouldn't really be a rune at all;
     * {@code player_learn_rune}'s {@code COMBAT_RUNE_TO_H} arm ({@link #learnToH()}) is the other
     * writer. Assignment itself does no validation in either language.
     *
     * <p>Function setToH coded before 260908, commented in full on 261009.
     *
     * @param toH 1 if the player can read to-hit bonuses, 0 if not
     * @see #getToH()
     */
    public void setToH(int toH) {
        this.toH = toH;
    }

    /**
     * Sets the to-damage knowledge bit - the port of writing C's {@code p->obj_k->to_d}. Birth sets
     * it to 1 outright in {@code do_cmd_accept_character} ({@code player-birth.c}); see
     * {@link #setToH} for the rest of that boundary, which the same statement group shares.
     * {@link #learnToD()} is the other writer.
     *
     * <p>Function setToD coded before 260908, commented in full on 261009.
     *
     * @param toD 1 if the player can read to-damage bonuses, 0 if not
     * @see #getToD()
     */
    public void setToD(int toD) {
        this.toD = toD;
    }

    /**
     * Sets the to-armour knowledge bit - the port of writing C's {@code p->obj_k->to_a}. Birth sets
     * it to 1 outright in {@code do_cmd_accept_character} ({@code player-birth.c}); see
     * {@link #setToH} for the rest of that boundary, which the same statement group shares.
     * {@link #learnToA()} is the other writer.
     *
     * <p>Function setToA coded before 260908, commented in full on 261009.
     *
     * @param toA 1 if the player can read to-armour bonuses, 0 if not
     * @see #getToA()
     */
    public void setToA(int toA) {
        this.toA = toA;
    }

    /**
     * Checks whether the player is entitled to be told that {@code item} carries a resistance (or
     * vulnerability) to {@code element} — the item-shaped port of C's {@code object_element_is_known}
     * ({@code obj-knowledge.c}). The curse-shaped counterpart is
     * {@link ObjectUtils#objectElementIsKnown(Player, Curse, ElementEnum)}, which mirrors this
     * method's structure exactly.
     *
     * <p>The two {@code ELEM_NONE}/{@code ELEM_MAX} sentinels answer false outright, matching C's
     * {@code element < 0 || element >= ELEM_MAX} test. Past that, three independent routes to "yes",
     * tried in the same order C tries them:
     * <ol>
     *   <li>The item is {@link ItemObject#isFullyKnown() fully known}, so every resistance on it is
     *   readable regardless of how it got that way.</li>
     *   <li>{@link #elementResistInfo}, this very instance's own field, already covers the element —
     *   C's {@code p->obj_k->el_info[element].res_level}. Because this method is called on the
     *   player's own {@link KnownObject} (as {@code player.getItemKnowledge().objectElementIsKnown(...)}),
     *   {@code this} already <em>is</em> {@code p->obj_k}, so no further use is made of the
     *   {@code player} parameter — it is carried only so the signature matches the curse-shaped
     *   sibling above, which has no such instance to call through.</li>
     *   <li>The item's own known-shadow, {@code item}'s {@link ItemObject#getKnown()} (C's
     *   {@code obj->known}), already carries a non-zero {@link ElementInfo#getResLevel()} for this
     *   element — C's {@code obj->known->el_info[element].res_level}. A missing map entry defaults to
     *   a fresh {@link ElementInfo}, whose resistance level is zero, so it answers the same as C's
     *   zero-initialized array read.</li>
     * </ol>
     * Failing all three, the element is not known and the method answers false.
     *
     * <p>Function objectElementIsKnown coded before 260924, commented in full on 261009.
     *
     * @param player  the player asking (unused; see the second route above)
     * @param item    the object being asked about
     * @param element the element whose knowledge is in question
     * @return true if the player is currently entitled to see resistance to {@code element} on
     * {@code item}
     */
    public boolean objectElementIsKnown(Player player, ItemObject item, ElementEnum element) {
        if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX)
            return false;

        // Object fully known is yes
        if (item.isFullyKnown()) return true;

        // Known element means yes
        if (elementResistInfo.getOrDefault(element, false))
            return true;

        // Object has been exposed to the element
        return item.getKnown().getElInfo().getOrDefault(element, new ElementInfo()).getResLevel() != 0;
    }
}
