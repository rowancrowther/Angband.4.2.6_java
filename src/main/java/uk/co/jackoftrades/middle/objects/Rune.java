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
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.game.event.projection.Projection;
import uk.co.jackoftrades.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftrades.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftrades.middle.objects.enums.*;

import java.util.*;

/**
 * One rune in the rune-based identification system — a single object property the player can learn,
 * after which every object carrying that property is known to carry it. This is the Java port of
 * the C original's {@code struct rune} ({@code src/obj-knowledge.h}).
 *
 * <p>What a rune refers to is carried by its {@link RuneVariety}, which replaces C's
 * {@code variety} + {@code index} pair; see that interface for why. The C {@code name} field is
 * likewise gone — every variety can supply its own name from the object it holds, and the one
 * exception, the combat runes, carries its text on {@link CombatRunes}.
 *
 * <p>The complete list is built once by {@link #initRunes()} and held by {@link ObjectRegistry}.
 * Its order is significant: it is the order runes appear in the knowledge menu, and C's savefile
 * stores auto-inscriptions against a rune's <em>position</em> in that list rather than against
 * anything intrinsic to the rune.
 *
 * @author Rowan Crowther
 */
public class Rune {
    private static final Logger logger = LogManager.getLogger(Rune.class);

    /**
     * What this is a rune of — the property the player learns, and the object it applies to.
     *
     * @author Rowan Crowther
     */
    private final RuneVariety variety;

    /**
     * The player's auto-inscription for this rune, or {@code null} if none is set. Objects found to
     * carry the rune are inscribed with it automatically. Ports C's {@code note} field, a
     * {@code quark_t} whose zero value means "no inscription" — hence {@code null} here rather than
     * an empty string, which would read as an inscription of nothing.
     *
     * <p>This is the only mutable state on a rune, and the only part of one that C persists.
     *
     * @author Rowan Crowther
     */
    private String note;

    /**
     * Build a rune for the given property. The auto-inscription starts unset.
     *
     * @param variety what this is a rune of
     * @author Rowan Crowther
     */
    public Rune(RuneVariety variety) {
        this.variety = variety;
    }

    /**
     * Builds the complete rune list and stores it in {@link ObjectRegistry}. Ports C's
     * {@code init_rune} ({@code src/obj-knowledge.c}), and must run after the object properties,
     * projections, brands, slays and curses have been loaded, since every rune resolves its subject
     * as it is created.
     *
     * <p>Runes are added variety by variety, in the same order as the original — combat, modifiers,
     * resistances, brands, slays, curses, flags — because the knowledge menu buckets the list by a
     * single pass that assumes runs of one group are contiguous. C counts the runes in a first pass
     * and fills a fixed-size array in a second; a growable list makes the count pass unnecessary.
     *
     * <p>Brands and slays are de-duplicated: several data-file entries can describe the same
     * property at different strengths, and the player learns the property, not the strength. Brands
     * group by name, slays by {@link Slay#sameMonsterSlain}. In each case the first entry of a
     * group becomes the rune's subject and the rest are skipped, so the surviving entry is a
     * representative — see {@link RuneVariety.BrandKey}. This walks the lists in load order, where
     * C walks its arrays in reverse data-file order; since the grouping key is shared by every
     * member of a group, the choice affects only which member is retained and the order the groups
     * appear in, never which objects match.
     *
     * <p>Sentinel enum constants are skipped throughout, and a subject that fails to resolve throws
     * rather than being quietly omitted: in the original these cases are impossible by construction
     * — an element with no projection is caught when {@code projection.txt} is parsed, and a
     * property lookup is dereferenced unguarded — so a failure here means the data files and the
     * enums have drifted apart, which is worth failing loudly for.
     *
     * @throws RuntimeException if a modifier, flag or element cannot be resolved to its definition
     * @author Rowan Crowther
     */

    public static void initRunes() {
        List<Rune> runes = new ArrayList<Rune>();

        // Combat runes are a fixed set, so they need no lookup - just the three constants
        for (CombatRunes r : CombatRunes.values()) {
            if (r == CombatRunes.COMBAT_RUNE_MAX) continue;

            runes.add(new Rune(new RuneVariety.CombatKey(r)));
        }

        // Every modifier gets a rune; the property definition supplies its name
        for (ObjectModifier mod : ObjectModifier.values()) {
            if (mod == ObjectModifier.OM_NONE || mod == ObjectModifier.OM_MAX) continue;

            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, mod);
            ObjectProperty property = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, wrapper);
            if (property != null) {
                RuneVariety.ModKey modKey = new RuneVariety.ModKey(mod, property);
                runes.add(new Rune(modKey));
            } else {
                String message = "No property found for Object Modifier: " + mod;
                RuntimeException e = new RuntimeException(message);
                logger.error(message, e);
                throw e;
            }
        }

        // Only the elements up to disenchantment are resistable - C bounds this at ELEM_HIGH_MAX
        for (ElementEnum e : ElementEnum.values()) {
            if (e == ElementEnum.ELEM_NONE || e == ElementEnum.ELEM_MAX) continue;
            if (!e.isHasResistRune()) continue;

            ProjectionEnum projectionEnum = ProjectionEnum.getFromElementEnum(e);
            if (projectionEnum == null) {
                String message = "No projection enum found for element enum: " + e;
                RuntimeException ex = new RuntimeException(message);
                logger.error(message, ex);
                throw ex;
            }

            Projection projection = WorldRegistry.lookupProjectionByLash(projectionEnum);
            if (projection == null) {
                String message = "No projection found for projection enum: " + projectionEnum;
                RuntimeException ex = new RuntimeException(message);
                logger.error(message, ex);
                throw ex;
            }

            runes.add(new Rune(new RuneVariety.ResistKey(e, projection)));
        }

        // Brands sharing a name are the same property at different strengths, so they share a rune
        List<Brand> brands = ObjectRegistry.getBrands();
        Set<String> brandNames = new HashSet<>();
        for (Brand brand : brands) {
            if (brand.getName() == null) continue;

            if (!brandNames.contains(brand.getName())) {
                brandNames.add(brand.getName());
                runes.add(new Rune(new RuneVariety.BrandKey(brand)));
            }
        }

        List<Slay> slays = ObjectRegistry.getSlays();
        // Slays group by the monsters they kill rather than by name, so there is no key to hash on;
        // needs to stay a nested for loop as we need the sameMonsterSlain test
        for (int i = 0; i < slays.size(); i++) {
            if (slays.get(i).getName() == null) continue;
            boolean counted = false;
            for (int j = 0; j < i; j++) {
                if (slays.get(i).sameMonsterSlain(slays.get(j))) {
                    counted = true;
                }
            }
            if (!counted)
                runes.add(new Rune(new RuneVariety.SlayKey(slays.get(i))));
        }

        // Curses are never grouped - each one gets its own rune
        List<Curse> curses = ObjectRegistry.getCurses();
        for (Curse curse : curses) {
            if (curse.getName() == null) continue;

            runes.add(new Rune(new RuneVariety.CurseKey(curse)));
        }

        // Flags whose subtype is a placeholder, or which describe the object rather than the player
        // (light, digging, throwing) or only ever appear on curses, are not learnable properties
        for (ObjectFlag flag : ObjectFlag.values()) {
            if (flag == ObjectFlag.OF_NONE || flag == ObjectFlag.OF_MAX) continue;

            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag);
            ObjectProperty property = ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, wrapper);
            if (property == null) {
                String message = "No property found for Object Flag: " + flag;
                RuntimeException e = new RuntimeException(message);
                logger.error(message, e);
                throw e;
            }

            ObjectFlagType sub = property.getSubtype();
            if (sub == ObjectFlagType.OFT_MAX || sub == ObjectFlagType.OFT_NONE ||
                    sub == ObjectFlagType.OFT_LIGHT || sub == ObjectFlagType.OFT_DIG ||
                    sub == ObjectFlagType.OFT_THROW || sub == ObjectFlagType.OFT_CURSE_ONLY)
                continue;

            runes.add(new Rune(new RuneVariety.FlagKey(flag, property)));
        }

        ObjectRegistry.setRunes(runes);
    }

    /**
     * @return the player's auto-inscription for this rune, or {@code null} if none is set
     * @author Rowan Crowther
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets or clears the player's auto-inscription for this rune. Ports C's {@code rune_set_note},
     * which likewise treats a null argument as "uninscribe".
     *
     * @param note the inscription to apply, or {@code null} to clear it
     * @author Rowan Crowther
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return what this is a rune of
     * @author Rowan Crowther
     */
    public RuneVariety getVariety() {
        return variety;
    }
}
