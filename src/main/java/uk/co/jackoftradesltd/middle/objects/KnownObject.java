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

import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;

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
 * play with — {@link #getDd()}, {@link #getDs()} and {@link #getAc()} set to 1, and the light and
 * digging flags switched on — is applied by the birth code ({@code player_outfit},
 * {@code src/player-birth.c}), not by the constructor.
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
     */
    private Flag<ObjectModifier> modifierFlag;
    /**
     * Which object flags the player can read, C's {@code obj_k->flags}. The one field where C's
     * representation and this one already agree, both being a set of flags.
     */
    private Flag<ObjectFlag> objectFlags;

    private Flag<ObjectNotice> noticeFlags;
    /**
     * Which elemental resistances the player can read, C's {@code obj_k->el_info[].res_level}.
     *
     * <p>A boolean rather than an {@link ElementInfo}, because on the knowledge side the level is
     * not a level: C writes 1 to mean "known" and tests it for truth. The {@code flags} half of
     * C's {@code element_info} is dropped, being savefile-only.
     */
    private Map<ElementEnum, Boolean> elementResistInfo;
    /**
     * Whether the player can read to-hit bonuses, C's {@code obj_k->to_h}. Kept as an int rather
     * than a boolean because C multiplies by it — {@code obj->known->to_h = p->obj_k->to_h *
     * obj->to_h} — so the 0/1 value does the masking directly.
     */
    private int toH;
    /**
     * Whether the player can read to-damage bonuses, C's {@code obj_k->to_d}. See {@link #toH}.
     */
    private int toD;
    /**
     * Whether the player can read to-armour bonuses, C's {@code obj_k->to_a}. See {@link #toH}.
     */
    private int toA;
    /**
     * Which curses the player recognises, C's {@code obj_k->curses[].power}.
     *
     * <p>C's {@code curse_data} carries a power and a timeout, but the knowledge copy uses only
     * power, and only as 0/1 — {@code player_knows_curse} is {@code curses[index].power == 1}.
     * Held as a map rather than a set because, unlike brands and slays, it is populated up front
     * from the registry so that an unrecognised curse is distinguishable from a known-false one.
     */
    private Map<Curse, Boolean> curses;
    /**
     * Which brands the player recognises, C's {@code obj_k->brands[]}.
     *
     * <p>A set rather than a map, because membership is the whole of the state: C's array is
     * indexed by registry position and holds nothing but a bool. Membership stands for the brand's
     * whole equivalence class — see {@link #learnBrand(Brand)}.
     */
    private Set<Brand> brands;
    /**
     * Which slays the player recognises, C's {@code obj_k->slays[]}. As {@link #brands}, with the
     * class defined by monsters slain rather than by name — see {@link #learnSlay(Slay)}.
     */
    private Set<Slay> slays;
    /**
     * Whether the player can read armour class, C's {@code obj_k->ac}. A 0/1 multiplier like
     * {@link #toH}: {@code obj->known->ac = obj->ac * p->obj_k->ac}, and {@code obj-desc.c} gates
     * printing an armour value on it.
     *
     * <p>Zero here is the pre-birth state. {@code player_outfit} raises it to 1 as part of the
     * "obvious object knowledge" every character starts with, so it is 1 for the whole of play.
     */
    private int ac = 0;
    /**
     * Whether the player can read damage dice, C's {@code obj_k->dd}. See {@link #ac} for the
     * multiplier convention and the birth-time initialisation; {@code obj-desc.c} prints the dice
     * only when this and {@link #ds} are both set.
     */
    private int dd = 0;
    /**
     * Whether the player can read damage sides, C's {@code obj_k->ds}. See {@link #dd}.
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
     * Populates the curse map with every registered curse, all unrecognised. C reaches the same
     * state with {@code mem_zalloc(z_info->curse_max * sizeof(struct curse_data))}.
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
     * a curse the player's knowledge has never heard of cannot be one they recognise.
     *
     * @param curse the curse to ask about
     * @return true if the player recognises this curse
     */
    public boolean curseIsKnown(Curse curse) {
        if (curses.containsKey(curse))
            return curses.get(curse);
        return false;
    }

    /**
     * Records that the player now recognises a curse. Curses are the one property with no
     * equivalence class — each has its own rune — so this marks exactly the curse it is given.
     *
     * @param curse the curse now recognised
     * @return true if this was new knowledge, false if the curse was already recognised
     */
    public boolean learnCurse(Curse curse) {
        boolean learned = !curseIsKnown(curse);
        curses.put(curse, true);
        return learned;
    }

    /**
     * Clears the three combat bonuses to unknown. Written out rather than left to Java's default
     * field initialisation so that the constructor's list of {@code init} calls reads as the
     * complete account of the starting state.
     */
    private void initToValues() {
        toH = 0;
        toD = 0;
        toA = 0;
    }

    /**
     * @return true if the player can read an item's to-hit bonus
     */
    public boolean toHIsKnown() {
        return toH != 0;
    }

    /**
     * Records that the player can now read to-hit bonuses. The port of the {@code COMBAT_RUNE_TO_H}
     * arm of {@code player_learn_rune}.
     *
     * @return true if this was new knowledge
     */
    public boolean learnToH() {
        boolean learned = toH == 0;
        toH = 1;
        return learned;
    }

    /**
     * @return true if the player can read an item's to-damage bonus
     */
    public boolean toDIsKnown() {
        return toD != 0;
    }

    /**
     * Records that the player can now read to-damage bonuses. See {@link #learnToH()}.
     *
     * @return true if this was new knowledge
     */
    public boolean learnToD() {
        boolean learned = toD == 0;
        toD = 1;
        return learned;
    }

    /**
     * @return true if the player can read an item's to-armour bonus
     */
    public boolean toAIsKnown() {
        return toA != 0;
    }

    /**
     * Records that the player can now read to-armour bonuses. See {@link #learnToH()}.
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
     * The port of C's {@code obj_k->el_info[element].res_level} test.
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
     * there is no rune for {@code ELEM_NONE} to learn.
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
     */
    private void initObjectFlags() {
        objectFlags = new Flag<>(ObjectFlag.class);
    }

    /**
     * The port of C's {@code of_has(p->obj_k->flags, flag)}.
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
     * @return an independent copy of the known object flags
     */
    public Flag<ObjectFlag> getFlags() {
        Flag<ObjectFlag> flag = new Flag<>(ObjectFlag.class);
        flag.copyFrom(objectFlags);
        return flag;
    }

    /**
     * Creates the empty modifier set, the port of the zeroed {@code obj_k->modifiers[]}.
     */
    private void initModifiers() {
        modifierFlag = new Flag<>(ObjectModifier.class);
    }

    /**
     * The port of C's {@code p->obj_k->modifiers[index]} test.
     *
     * @param modifier the modifier to ask about
     * @return true if the player can read this modifier on an item
     */
    public boolean modifierIsKnown(ObjectModifier modifier) {
        return modifierFlag.has(modifier);
    }

    /**
     * Records that the player can now read a modifier.
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
     * @param brand the brand to ask about
     * @return true if the player recognises this brand
     */
    public boolean brandIsKnown(Brand brand) {
        return brands.contains(brand);
    }

    /**
     * Records that the player now recognises a brand — and every other brand of the same name.
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
     * is the state: an absent brand is an unrecognised one.
     */
    public void initBrands() {
        brands = new HashSet<>();
    }

    /**
     * The port of C's {@code player_knows_slay}. As {@link #brandIsKnown(Brand)}, a plain
     * membership test made cheap by the grouping happening on the learning side.
     *
     * @param slay the slay to ask about
     * @return true if the player recognises this slay
     */
    public boolean slayIsKnown(Slay slay) {
        return slays.contains(slay);
    }

    /**
     * Records that the player now recognises a slay — and every other slay that kills the same
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
     */
    public void initSlays() {
        slays = new HashSet<>();
    }

    /**
     * Returns the armour-class knowledge as the 0/1 multiplier C uses it as, so that a caller can
     * write {@code item.getAc() * knowledge.getAc()} and get either the real value or nothing.
     *
     * @return 1 if the player can read armour class, 0 if not
     */
    public int getAc() {
        return ac;
    }

    /**
     * @return 1 if the player can read damage dice, 0 if not
     * @see #getAc()
     */
    public int getDd() {
        return dd;
    }

    /**
     * @return 1 if the player can read damage sides, 0 if not
     * @see #getAc()
     */
    public int getDs() {
        return ds;
    }

    /**
     * @return 1 if the player can read to-hit bonuses, 0 if not
     * @see #getAc()
     */
    public int getToH() {
        return toH;
    }

    /**
     * @return 1 if the player can read to-damage bonuses, 0 if not
     * @see #getAc()
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
     * <p>Function getElementResistInfo commented in full on 260816.
     *
     * @return the per-element knowledge bits, shared with this instance
     */
    public Map<ElementEnum, Boolean> getElementResistInfo() {
        return elementResistInfo;
    }

    /**
     * @return 1 if the player can read to-armour bonuses, 0 if not
     * @see #getAc()
     */
    public int getToA() {
        return toA;
    }

    public boolean noticeFlagOn(ObjectNotice notice) {
        return noticeFlags.on(notice);
    }
}
