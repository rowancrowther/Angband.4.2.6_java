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
     * The sentence shown on a rune's detail page in the knowledge browser, below the title —
     * "Object brands the player's attacks with fire.", "Object gives the player a magical bonus to
     * strength.". Ports C's {@code rune_desc} ({@code src/obj-knowledge.c}), whose sole caller is
     * {@code ui-knowledge.c}'s rune entry, appending it to the textblock straight after the name.
     *
     * <p>Every variety but one wraps a stored string in a format string, and the wrappers are all
     * different, so there is nothing to share between the seven — hence a method on each record
     * rather than a helper taking a template. The exception is {@link CombatKey}, whose three
     * sentences are literals because the enchantments are a closed set.
     *
     * <p><strong>The description wraps the raw name, not {@link #runeName()}.</strong> C reads
     * {@code r->name} — the string {@code init_rune} copied off the underlying object — and the two
     * functions treat it differently: {@code rune_name} adds "resist ", "slay ", " brand", " curse",
     * while {@code rune_desc} takes it bare. Only {@link ModKey} and {@link FlagKey} produce the same
     * string from both, and only because their name is the property's, unadorned. Calling
     * {@code runeName()} from here would read "resistance to resist fire" for a resist rune, so each
     * record reaches for the subject's own name instead.
     *
     * <p>{@link CurseKey} is the one variety that needs a field {@code struct rune} does not hold:
     * its sentence is built from the curse's <em>description</em>, which is why C has to index back
     * into {@code curses[]} with the rune's stored index. Holding the {@link Curse} itself makes that
     * a plain accessor.
     *
     * @return the rune's description as the player sees it
     * @author Rowan Crowther
     */
    String runeDesc();

    /**
     * A rune of one of the three fixed combat enchantments.
     *
     * <p>The only variety whose name is not looked up from loaded data: the three enchantments are
     * a closed set, so {@link CombatRunes} carries its own text and {@link #runeName()} reads
     * {@link CombatRunes#getDescription()} — the port of the {@code c_rune[]} table C indexes here.
     *
     * <p>{@link #runeDesc()} is likewise the only one written as literals rather than assembled from
     * loaded data, and the only one whose sentences carry no closing full stop — C's three combat
     * strings end bare where its other six formats end in ".", and the knowledge browser prints
     * whichever it is given. The spelling follows the port's convention of "armour" against C's
     * "armor", matching {@link CombatRunes#COMBAT_RUNE_TO_A}'s own text.
     *
     * <p>{@link CombatRunes#COMBAT_RUNE_MAX} is a count sentinel, not a rune, so it has no
     * description; naming it in the switch is what makes the switch exhaustive and lets the compiler
     * confirm the three real cases are all covered. It answers {@code null} to mirror the
     * {@code return NULL} C reaches when its {@code if} chain falls through — but where C can reach
     * that line from any unhandled variety, here it is reachable only by constructing a
     * {@code CombatKey} around the sentinel, which callers are required not to do.
     *
     * @param key which combat enchantment
     * @author Rowan Crowther
     */
    record CombatKey(CombatRunes key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.COMBAT;
        }

        @Override
        public String runeName() {
            return key.getDescription();
        }

        @Override
        public String runeDesc() {
            return switch (key) {
                case COMBAT_RUNE_TO_A -> "Object magically increases the player's armour class";
                case COMBAT_RUNE_TO_H -> "Object magically increases the player's chance to hit";
                case COMBAT_RUNE_TO_D -> "Object magically increases the player's damage";
                case COMBAT_RUNE_MAX -> null;
            };
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
     * <p>Because that name is unadorned, this is one of the two varieties where {@link #runeDesc()}
     * could have been written in terms of {@link #runeName()} and still produced C's string. It is
     * not: both read {@link ObjectProperty#getName()}, so the pairing is a property of the data
     * rather than a dependency between the two methods, and adding a prefix to the name later cannot
     * silently reword the description.
     *
     * @param key      which modifier
     * @param property the modifier's property definition, held for its name and power
     * @author Rowan Crowther
     */
    record ModKey(ObjectModifier key, ObjectProperty property) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.MODIFIERS;
        }

        @Override
        public String runeName() {
            return property.getName();
        }

        @Override
        public String runeDesc() {
            return String.format("Object gives the player a magical bonus to %s.", property.getName());
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
     * <p>This is the variety where the difference between {@link #runeName()} and
     * {@link #runeDesc()} shows most plainly: the name prefixes the projection with "resist", the
     * description reads "…resistance to fire." from the same projection unprefixed. Both go to
     * {@link Projection#getName()} directly, because building the description out of the name would
     * repeat the prefix inside the sentence.
     *
     * @param key        which element
     * @param projection the element's projection, held for its name
     * @author Rowan Crowther
     */
    record ResistKey(ElementEnum key, Projection projection) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.RESIST;
        }

        @Override
        public String runeName() {
            return String.format("resist %s", projection.getName());
        }

        @Override
        public String runeDesc() {
            return String.format("Object affects the player's resistance to %s.", projection.getName());
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
     * <p>{@link #runeDesc()} reads the same name the title does, unwrapped — "acid brand" as a
     * heading, "…attacks with acid." in the sentence — so it too stays clear of the fields the
     * grouping makes ambiguous.
     *
     * @param key a brand representing all brands with the same name
     * @author Rowan Crowther
     */
    record BrandKey(Brand key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.BRAND;
        }

        @Override
        public String runeName() {
            return String.format("%s brand", key.getName());
        }

        @Override
        public String runeDesc() {
            return String.format("Object brands the player's attacks with %s.", key.getName());
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
     * <p>The name is nonetheless what both the title and {@link #runeDesc()} display, C reading it
     * off whichever slay it happened to store first. Slay names are plural noun phrases — "animals",
     * "evil creatures" — which is what lets the same string serve "slay animals" as a heading and
     * "…attacks against animals more powerful." as a sentence.
     *
     * @param key a slay representing all slays that kill the same monsters
     * @author Rowan Crowther
     */
    record SlayKey(Slay key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.SLAY;
        }

        @Override
        public String runeName() {
            return String.format("slay %s", key.getName());
        }

        @Override
        public String runeDesc() {
            return String.format("Object makes the player's attacks against %s more powerful.", key.getName());
        }
    }

    /**
     * A rune of a curse. Curses are not grouped — each gets its own rune.
     *
     * <p>The only variety whose {@link #runeDesc()} does not wrap the same string its
     * {@link #runeName()} wraps: the name is built from {@link Curse#getName()} ("siren curse"), the
     * description from {@link Curse#getDescription()}, which is phrased as a predicate so that
     * "Object %s." reads as a sentence. C cannot do this from {@code struct rune} alone — it copied
     * only the name at init — so {@code rune_desc} indexes back into {@code curses[]} with the rune's
     * stored index. Holding the curse makes both a plain accessor and keeps them in step.
     *
     * @param key which curse
     * @author Rowan Crowther
     */
    record CurseKey(Curse key) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.CURSE;
        }

        @Override
        public String runeName() {
            return String.format("%s curse", key.getName());
        }

        @Override
        public String runeDesc() {
            return String.format("Object %s.", key.getDescription());
        }
    }

    /**
     * A rune of an object flag — a boolean property such as a sustain or a protection. Listed under
     * {@link RuneGroup#OTHER}, following the C original's group headings.
     *
     * <p>Named like {@link ModKey}: the property's name unadorned, C's {@code else} branch again,
     * and the other variety whose name and description wrap the same string for the same reason.
     * The sentence differs — a flag is something the object <em>gives</em> rather than a bonus it
     * adds to.
     *
     * @param key      which flag
     * @param property the flag's property definition, held for its name and subtype
     * @author Rowan Crowther
     */
    record FlagKey(ObjectFlag key, ObjectProperty property) implements RuneVariety {
        public RuneGroup group() {
            return RuneGroup.OTHER;
        }

        @Override
        public String runeName() {
            return property.getName();
        }

        @Override
        public String runeDesc() {
            return String.format("Object gives the player the property of %s.", property.getName());
        }
    }
}
