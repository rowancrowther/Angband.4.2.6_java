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

package uk.co.jackoftrades.middle.objects.enums;

import uk.co.jackoftrades.middle.game.event.projection.Projection;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ObjectProperty;
import uk.co.jackoftrades.middle.objects.Slay;

/**
 * What a {@link uk.co.jackoftrades.middle.objects.Rune} is a rune <em>of</em> — a tagged union over
 * the seven kinds of object property the player can learn, each carrying the thing it refers to.
 *
 * <p>This replaces the {@code variety} + {@code index} pair in the C original's
 * {@code struct rune} ({@code src/obj-knowledge.h}). There, a rune identifies its subject by an
 * integer index into whichever global array the variety implies — {@code brands[]},
 * {@code slays[]}, {@code curses[]}, or an enum's value. Those arrays are one-based with a zeroed
 * slot 0, so the index is only meaningful alongside the variety that selects the array. Holding the
 * object itself removes the shared coordinate system entirely, and with it the off-by-one hazards
 * of porting one-based C arrays to dense Java lists.
 *
 * <p>Because the interface is sealed, a {@code switch} over the permitted types is checked for
 * exhaustiveness at compile time. C cannot do this: its equivalents ({@code rune_name},
 * {@code rune_desc}) fall through to {@code NULL} on an unhandled variety.
 *
 * <p>Records that need a name for display hold the resolved object rather than a copied string, so
 * the registry lookups happen once when the rune list is built and never again while rendering. The
 * C original flattens each name to a {@code const char *} at init, which is why {@code rune_desc}
 * has to reach back into {@code curses[]} for the one field it did not copy.
 *
 * @author Rowan Crowther
 */
public sealed interface RuneVariety permits RuneVariety.CombatKey, RuneVariety.ModKey,
        RuneVariety.ResistKey, RuneVariety.BrandKey, RuneVariety.SlayKey, RuneVariety.CurseKey, RuneVariety.FlagKey {
    /**
     * @return the knowledge-menu heading this variety is listed under
     * @author Rowan Crowther
     */
    RuneGroup group();

    /**
     * A rune of one of the three fixed combat enchantments.
     *
     * @param key which combat enchantment
     * @author Rowan Crowther
     */
    record CombatKey(CombatRunes key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.COMBAT;
        }
    }

    /**
     * A rune of an object modifier — a numeric bonus such as a stat, speed or stealth.
     *
     * @param key      which modifier
     * @param property the modifier's property definition, held for its name and power
     * @author Rowan Crowther
     */
    record ModKey(ObjectModifier key, ObjectProperty property) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.MODIFIERS;
        }
    }

    /**
     * A rune of an elemental resistance. Only elements up to and including disenchantment carry
     * one — C bounds the loop at {@code ELEM_HIGH_MAX}, which {@link ElementEnum#isHasResistRune()}
     * ports.
     *
     * @param key        which element
     * @param projection the element's projection, held for its name
     * @author Rowan Crowther
     */
    record ResistKey(ElementEnum key, Projection projection) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.RESIST;
        }
    }

    /**
     * A rune of a weapon brand.
     *
     * <p>One rune covers every brand sharing a name, so {@code key} is a <em>representative</em> of
     * its group rather than a specific brand: the acid rune stands for both acid brands, and which
     * of them is held depends only on data-file order. Only the name is meaningful — reading the
     * multiplier or power off it would silently pick one member of the group. C avoids the question
     * by storing just the name, and matches objects against it with {@code streq} rather than by
     * identity.
     *
     * @param key a brand representing all brands with the same name
     * @author Rowan Crowther
     */
    record BrandKey(Brand key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.BRAND;
        }
    }

    /**
     * A rune of a weapon slay.
     *
     * <p>As with {@link BrandKey}, {@code key} represents a group rather than a single slay — but
     * the grouping is by {@link Slay#sameMonsterSlain}, not by name, so slays that kill the same
     * monsters share a rune even where their names or multipliers differ. Only the race flag and
     * base are meaningful.
     *
     * @param key a slay representing all slays that kill the same monsters
     * @author Rowan Crowther
     */
    record SlayKey(Slay key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.SLAY;
        }
    }

    /**
     * A rune of a curse. Curses are not grouped — each gets its own rune.
     *
     * @param key which curse
     * @author Rowan Crowther
     */
    record CurseKey(Curse key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.CURSE;
        }
    }

    /**
     * A rune of an object flag — a boolean property such as a sustain or a protection. Listed under
     * {@link RuneGroup#OTHER}, following the C original's group headings.
     *
     * @param key      which flag
     * @param property the flag's property definition, held for its name and subtype
     * @author Rowan Crowther
     */
    record FlagKey(ObjectFlag key, ObjectProperty property) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.OTHER;
        }
    }
}
