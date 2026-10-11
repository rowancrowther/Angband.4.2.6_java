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
import uk.co.jackoftradesltd.middle.objects.enums.CombatRunes;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.player.PlayerKnowledge;

/**
 * Questions about what an individual item carries and what handling it can teach, ported from the
 * object half of C's {@code obj-knowledge.c}: {@link #objectHasRune} (does this item have the
 * property a rune stands for) and {@link #objectFlavourTried} (mark an item's kind as tried).
 *
 * <p>Everything here is a static function over an {@link ItemObject}, as in C, and none of it
 * touches the player's own rune knowledge. That store is {@link KnownObject}, reached through
 * {@link PlayerKnowledge}'s {@code learnRune} wrappers; this class supplies the facts about the
 * item those wrappers weigh it against.
 *
 * <p>Class ObjectKnowledge coded before 260905, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class ObjectKnowledge {
    /**
     * Reports the "unknown object passed in" case of {@link #objectFlavourTried}, the only place
     * this class logs. C has no equivalent: it asserts, and the assertion compiles out of release
     * builds.
     *
     * <p>Field logger coded before 260905, commented in full on 261011.
     */
    private static final Logger logger = LogManager.getLogger(ObjectKnowledge.class);

    /**
     * Reports whether an item actually carries the property a rune stands for — not whether the
     * player has learned that property, which is {@link KnownObject}'s question. Ports C's
     * {@code object_has_rune} ({@code obj-knowledge.c}), the fact-finding half of the
     * rune-based identification system that {@link PlayerKnowledge}'s {@code learnRune} wrappers
     * use to decide what a played object teaches.
     *
     * <p>The switch is over {@link RuneVariety} rather than C's {@code r->variety} plus
     * {@code r->index} pair, so each branch reads the subject straight off the rune instead of
     * indexing back into a global array with it — see {@link RuneVariety} for why. Being a switch
     * over a sealed interface, it is checked exhaustive at compile time, where C's chain ends in an
     * unreachable {@code return NULL} because it has no such guarantee.
     *
     * <p><b>The combat runes are the one branch where "has the rune" is not simply "the figure is
     * nonzero".</b> To-armour and to-damage are: no ordinary item has either, so any value at all is
     * something to learn. To-hit is not — body armour carries a built-in penalty from its kind
     * (Chain Mail's {@code attack:1d4:-2:0}), which a plain {@code getToHit() != 0} would mistake for
     * enchantment. This method therefore asks {@link ItemObject#hasStandardToH()} instead, exactly
     * as C asks {@code !object_has_standard_to_h(obj)}; see that method for the three-way test
     * behind the answer.
     *
     * <p><b>A resistance rune treats an element the item has no entry for as level zero.</b> C's
     * {@code el_info} is a fixed array with a slot for every element, each unset slot reading back
     * as zeroed, so {@code obj->el_info[index].res_level} is always safe. {@link ItemObject}'s map
     * is only dense for an item built by {@code objectPrep}; one assembled by hand, or whose map was
     * never allocated ({@link ItemObject#getElInfo()} then answers an empty map), has no entry for
     * most elements. The lookup is therefore null-checked, and an absent entry reads as "no
     * resistance, no vulnerability" — the answer C gives from the zeroed slot — rather than failing.
     *
     * <p>Brands and slays are matched by the same test {@link Rune#initRunes()} used to group them
     * in the first place — name for brands, {@link Slay#sameMonsterSlain} for slays — so an object
     * carrying any member of a group is reported as having the rune that represents it, not only the
     * particular one the rune happened to keep. Curses are matched by key identity in the object's
     * curse map, with a zero power reading as "not cursed" — {@link ItemObject#getCurses()}'s
     * convention for the same reason C tests {@code obj->curses[r->index].power}: an absent curse and
     * one stacked to nothing must agree.
     *
     * <p>Function objectHasRune coded before 260905, commented in full on 261011.
     *
     * @param obj  the item to examine
     * @param rune the rune to test for
     * @return whether the item carries the property this rune represents
     */
    public static boolean objectHasRune(ItemObject obj, Rune rune) {
        RuneVariety variety = rune.getVariety();
        switch (variety) {
            case RuneVariety.CombatKey combatKey -> {
                if (combatKey.key() == CombatRunes.COMBAT_RUNE_TO_A && obj.getToAC() != 0)
                    return true;
                else if (combatKey.key() == CombatRunes.COMBAT_RUNE_TO_H && !obj.hasStandardToH())
                    return true;
                else if (combatKey.key() == CombatRunes.COMBAT_RUNE_TO_D && obj.getToDam() != 0)
                    return true;
            }
            case RuneVariety.ModKey modKey -> {
                if (obj.getModifierValue(modKey.key()) != 0)
                    return true;
            }
            case RuneVariety.ResistKey resKey -> {
                if (obj.getElInfo().get(resKey.key()) != null && obj.getElInfo().get(resKey.key()).getResLevel() != 0)
                    return true;
            }
            case RuneVariety.BrandKey brandKey -> {
                if (!obj.getBrands().isEmpty()) {
                    for (Brand brand : obj.getBrands()) {
                        if (brandKey.key().getName().equals(brand.getName())) {
                            return true;
                        }
                    }
                }
            }
            case RuneVariety.SlayKey slayKey -> {
                if (!obj.getSlays().isEmpty()) {
                    for (Slay slay : obj.getSlays()) {
                        if (slayKey.key().sameMonsterSlain(slay)) {
                            return true;
                        }
                    }
                }
            }
            case RuneVariety.CurseKey curseKey -> {
                if (!obj.getCurses().isEmpty()) {
                    for (Curse curse : obj.getCurses().keySet()) {
                        CurseData curseData = obj.getCurses().get(curse);

                        if (curseKey.key() == curse && curseData.getPower() != 0) {
                            return true;
                        }
                    }
                }
            }
            case RuneVariety.FlagKey flagKey -> {
                if (obj.hasFlag(flagKey.key())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Marks this item's kind as tried - something the player has used, read, or otherwise
     * experienced without thereby becoming aware of what it does. Ports C's
     * {@code object_flavor_tried} ({@code obj-knowledge.c}), the guarding wrapper that keeps
     * {@link ObjectKind#setTried} from ever being called on an artifact kind.
     *
     * <p>C guards by position - {@code obj->kind->kidx >= z_info->ordinary_kind_max} - this method
     * asks the same question through {@link ObjectKind#isSpecialArtifactKind()}, the precomputed
     * boolean that stands in for that comparison; see that method for why.
     *
     * <p>C's {@code assert(obj); assert(obj->kind);} are debug-only checks that compile out of
     * release builds, so C never actually handles a null object here. This method substitutes a
     * null check that logs and returns rather than asserting, since Java has no equivalent of a
     * compiled-out assertion.
     *
     * <p>Function objectFlavourTried coded before 260905, commented in full on 261011.
     *
     * @param obj the item whose kind should be marked tried
     */
    public static void objectFlavourTried(ItemObject obj) {
        if (obj == null || obj.getKind() == null) {
            String message = "Unknown object passed into ObjectFlavourTried";
            logger.error(message);
            return;
        }

        if (obj.getKind().isSpecialArtifactKind()) return;

        obj.getKind().setTried(true);
    }
}
