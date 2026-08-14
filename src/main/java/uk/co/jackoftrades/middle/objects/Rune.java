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
import uk.co.jackoftrades.channel.enums.ProjectionEnum;
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

            ProjectionEnum projectionEnum = e.getProjectionEnum();
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

    /**
     * Finds the rune for one of the three fixed combat enchantments — the first of the seven
     * lookups that together port C's {@code rune_index} ({@code src/obj-knowledge.c:194}).
     *
     * <p>C identifies a rune by a {@code (variety, index)} pair and returns its <em>position</em> in
     * {@code rune_list}. Every caller hands that position straight back to
     * {@code player_learn_rune}, which reads the rune out of the array again and switches on the
     * variety it already knew — so the integer is a round trip, and these return the {@link Rune}
     * itself. C's "can't find it" {@code -1} becomes {@code null}.
     *
     * <p>The {@code variety} argument is gone as well. It is a literal at every C call site —
     * {@code rune_index(RUNE_VAR_COMBAT, COMBAT_RUNE_TO_A)} — so overload resolution can carry it,
     * and the pair becomes unrepresentable rather than merely discouraged: there is no way to ask
     * for an element as though it were a flag, which {@code size_t variety} allows.
     *
     * <p>Each overload takes the raw subject rather than a {@link RuneVariety}, so a caller never
     * has to rebuild a key — and, for the two varieties that hold a property alongside their
     * subject, never has to repeat the registry lookup that resolved it.
     *
     * <p>All seven scan the rune list, as C's does, rather than consulting an index built beside it.
     * The list is short and the scan stops at the match, and asking
     * {@link ObjectRegistry#getRunes()} for it costs nothing — it hands back the stored list itself
     * rather than a copy, which is why each lookup can fetch it afresh instead of caching a
     * reference that a later re-init would strand. If these ever become hot enough to matter, the
     * scans are what an index would replace, and none of the signatures here would change.
     *
     * @param key which combat enchantment
     * @return the rune for that enchantment, or {@code null} if there is none
     * @author Rowan Crowther
     */
    public static Rune runeIndex(CombatRunes key) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.CombatKey k && key == k.key())
                return rune;
        }

        return null;
    }

    /**
     * Finds the rune for an object modifier. See {@link #runeIndex(CombatRunes)} for what this
     * overload set replaces.
     *
     * <p>Every modifier {@link #initRunes()} could resolve a property for has a rune, so a
     * {@code null} here means the sentinels, or a modifier added to the enum without a matching
     * entry in {@code object_property.txt}.
     *
     * @param key which modifier
     * @return the rune for that modifier, or {@code null} if there is none
     * @author Rowan Crowther
     */
    public static Rune runeIndex(ObjectModifier key) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.ModKey k && k.key() == key)
                return rune;
        }

        return null;
    }

    /**
     * Finds the rune for an elemental resistance. See {@link #runeIndex(CombatRunes)} for what this
     * overload set replaces.
     *
     * <p>Only the elements up to and including disenchantment carry a resistance rune — C bounds
     * the loop in {@code init_rune} at {@code ELEM_HIGH_MAX}, which
     * {@link ElementEnum#isHasResistRune()} ports — so asking about anything above it is a
     * legitimate question with the answer {@code null}, not a lookup failure.
     *
     * @param key which element
     * @return the rune for that element's resistance, or {@code null} if it has none
     * @author Rowan Crowther
     */
    public static Rune runeIndex(ElementEnum key) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.ResistKey k && k.key() == key)
                return rune;
        }

        return null;
    }

    /**
     * Finds the rune for a weapon brand. See {@link #runeIndex(CombatRunes)} for what this overload
     * set replaces.
     *
     * <p>Matched by name rather than by identity or equality, because brands sharing a name share a
     * rune and the one the rune holds is only a representative of that group — see
     * {@link RuneVariety.BrandKey}. Any acid brand must therefore find the acid rune, not just the
     * particular one {@link #initRunes()} happened to keep. C reaches the same result from the other
     * direction: it stores nothing but the name and compares with {@code streq}.
     *
     * @param key any brand of the wanted kind, at any strength
     * @return the rune covering brands of that name, or {@code null} if there is none
     * @author Rowan Crowther
     */
    public static Rune runeIndex(Brand key) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.BrandKey b && b.key().getName().equals(key.getName()))
                return rune;
        }

        return null;
    }

    /**
     * Finds the rune for a weapon slay. See {@link #runeIndex(CombatRunes)} for what this overload
     * set replaces.
     *
     * <p>As with {@link #runeIndex(Brand)} the slay held by a rune is a representative, but the
     * grouping is by {@link Slay#sameMonsterSlain} rather than by name — so this cannot match on a
     * name, and slays that kill the same monsters under different names still find the same rune.
     * The test used here is the same one {@link #initRunes()} de-duplicates with, so the two cannot
     * disagree about which slays share a rune.
     *
     * @param slay any slay of the wanted kind, at any strength
     * @return the rune covering slays that kill the same monsters, or {@code null} if there is none
     * @author Rowan Crowther
     */
    public static Rune runeIndex(Slay slay) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.SlayKey s && s.key().sameMonsterSlain(slay))
                return rune;
        }

        return null;
    }

    /**
     * Finds the rune for a curse. See {@link #runeIndex(CombatRunes)} for what this overload set
     * replaces.
     *
     * <p>Curses are never grouped, so each has its own rune and identity would serve — but the name
     * is matched instead, following C, whose caller resolves the curse by name before asking:
     * {@code rune_index(RUNE_VAR_CURSE, lookup_curse(curse->name))}. That also makes the lookup
     * indifferent to where the curse came from, where identity would quietly fail for one that did
     * not come from the registry.
     *
     * @param curse the curse to look up
     * @return the rune for that curse, or {@code null} if there is none — C's {@code lookup_curse}
     *         miss, which it reports as {@code -1} and its callers largely do not check
     * @author Rowan Crowther
     */
    public static Rune runeIndex(Curse curse) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.CurseKey c && c.key().getName().equals(curse.getName()))
                return rune;
        }

        return null;
    }

    /**
     * Finds the rune for an object flag. See {@link #runeIndex(CombatRunes)} for what this overload
     * set replaces.
     *
     * <p>Not every flag is a learnable property: {@link #initRunes()} skips the placeholder
     * subtypes, the ones describing the object rather than the player (light, digging, throwing),
     * and those that only ever appear on curses. Asking about one of those is expected — the
     * learning code walks whole flag sets — and answers {@code null}, C's {@code -1}.
     *
     * @param flag which flag
     * @return the rune for that flag, or {@code null} if it is not a learnable property
     * @author Rowan Crowther
     */
    public static Rune runeIndex(ObjectFlag flag) {
        List<Rune> runes = ObjectRegistry.getRunes();

        for (Rune rune : runes) {
            if (rune.getVariety() instanceof RuneVariety.FlagKey f && f.key().equals(flag))
                return rune;
        }

        return null;
    }
}
