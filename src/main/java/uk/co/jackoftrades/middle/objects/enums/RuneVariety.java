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
     * The player-visible name of a rune of this variety — "resist fire", "acid brand", "slay
     * demons", "Strength". Ports C's {@code rune_name} ({@code src/obj-knowledge.c}), which the
     * knowledge browser uses both as a list entry and as the title of a rune's detail page, and
     * which the learning message reads as "You have learned the rune of %s."
     *
     * <p>C writes this as a chain of {@code if}s over the variety, wrapping the rune's stored name
     * in a format string for four of the seven — {@code "%s brand"}, {@code "slay %s"},
     * {@code "%s curse"}, {@code "resist %s"} — and returning it unadorned for the rest. Here each
     * record supplies its own, so the wrapping sits next to the thing being wrapped and the
     * compiler, rather than a final {@code else}, guarantees every variety has an answer. C's
     * function ends with an unreachable {@code return NULL} because its chain cannot make that
     * promise.
     *
     * <p>The name is derived on each call rather than cached. C flattens it once at init into
     * {@code struct rune}'s {@code name} field, which is why {@code rune_desc} has to reach back
     * into {@code curses[]} for the description it did not copy; holding the subject itself makes
     * that second lookup unnecessary and keeps the two in step if the underlying data changes.
     *
     * @return the rune's name as the player sees it
     * @author Rowan Crowther
     */
    String runeName();

    /**
     * A rune of one of the three fixed combat enchantments.
     *
     * <p>The only variety whose name is not looked up from loaded data: the three enchantments are
     * a closed set, so {@link CombatRunes} carries its own text and {@link #runeName()} reads
     * {@link CombatRunes#getDescription()} — the port of the {@code c_rune[]} table C indexes here.
     * 
     * @param key which combat enchantment
     * @author Rowan Crowther
     */
    record CombatKey(CombatRunes key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.COMBAT;
        }

        public String runeName() {
            return key.getDescription();
        }
    }

    /**
     * A rune of an object modifier — a numeric bonus such as a stat, speed or stealth.
     *
     * <p>{@link #runeName()} is the property's name with nothing added, matching the {@code else}
     * branch C falls through to for this variety. The property is the one
     * {@code lookup_obj_property(OBJ_PROPERTY_MOD, i)} finds, so the name is whatever
     * {@code object_property.txt} gives it.
     *
     * @param key      which modifier
     * @param property the modifier's property definition, held for its name and power
     * @author Rowan Crowther
     */
    record ModKey(ObjectModifier key, ObjectProperty property) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.MODIFIERS;
        }

        public String runeName() {
            return property.getName();
        }
    }

    /**
     * A rune of an elemental resistance. Only elements up to and including disenchantment carry
     * one — C bounds the loop at {@code ELEM_HIGH_MAX}, which {@link ElementEnum#isHasResistRune()}
     * ports.
     *
     * <p>The name comes from the projection, not the element: C fills the rune's name field with
     * {@code projections[i].name}, and {@code list-elements.h} has no name column at all — an
     * element is only the tag {@code ELEM(ACID)}. The distinction is easy to miss because most of
     * the pairs read alike, but not all do; {@code ELEM_ELEC} projects as "lightning" and
     * {@code ELEM_DISEN} as "disenchantment", so naming a resist rune off the element would put
     * two of the thirteen wrong.
     *
     * @param key        which element
     * @param projection the element's projection, held for its name
     * @author Rowan Crowther
     */
    record ResistKey(ElementEnum key, Projection projection) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.RESIST;
        }

        public String runeName() {
            return String.format("resist %s", projection.getName());
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

        public String runeName() {
            return String.format("%s brand", key.getName());
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

        public String runeName() {
            return String.format("slay %s", key.getName());
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

        public String runeName() {
            return String.format("%s curse", key.getName());
        }
    }

    /**
     * A rune of an object flag — a boolean property such as a sustain or a protection. Listed under
     * {@link RuneGroup#OTHER}, following the C original's group headings.
     *
     * <p>Named like {@link ModKey}: the property's name unadorned, C's {@code else} branch again.
     *
     * @param key      which flag
     * @param property the flag's property definition, held for its name and subtype
     * @author Rowan Crowther
     */
    record FlagKey(ObjectFlag key, ObjectProperty property) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.OTHER;
        }

        public String runeName() {
            return property.getName();
        }
    }
}
