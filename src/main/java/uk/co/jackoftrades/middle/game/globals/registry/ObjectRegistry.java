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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.objects.*;
import uk.co.jackoftrades.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.*;

/**
 * Runtime holder for all object-domain game data — object bases, object kinds, slays, brands,
 * curses, item objects, activations, ego items, artifacts, chest traps, and object properties —
 * together with the derived {@code *Max} counters and the lookups the running game queries.
 *
 * <p>Unlike the other registries, the object-kind table is a live, mutable registry rather than a
 * load-once list: {@link #addObjectKind} appends a kind and indexes it in {@link #kindsByTvalSval}
 * (the tval&rarr;sval&rarr;kind index kept in sync with {@code objectKinds}), and {@link #reset}
 * clears both so a re-initialisation does not double-register. {@link #unknownGoldKind} and
 * {@link #unknownItemKind} are the sentinel kinds for unidentified gold and items.
 *
 * <p>This is the read side of the object slice: it is populated at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.ObjectDataLoader} — ordinary kinds
 * registered by {@code loadItemObjects} and special artifact kinds synthesised into the same table
 * by {@code loadArtifacts}. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class ObjectRegistry {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Number of ordinary object kinds loaded from {@code object.txt} — the artifact-synthesis ceiling.
     */
    private static int objectBaseKindMax;
    /**
     * Number of loaded artifacts.
     */
    private static int artifactKindMax;
    /**
     * Number of loaded ego items (set from {@link #setEgoItems}).
     */
    private static int egoItemKindMax;
    /**
     * Number of activations available to random artifacts.
     */
    private static int randartActivationsMax;
    /**
     * Number of loaded curses (set from {@link #setCurses}).
     */
    private static int curseMax;
    /**
     * Number of loaded slays (set from {@link #setSlays}).
     */
    private static int slayMax;
    /**
     * Number of loaded brands (set from {@link #setBrands}).
     */
    private static int brandMax;
    /**
     * Number of object-power calculation records.
     */
    private static int objectPowerCalculationMax;
    /**
     * Number of loaded object properties (set from {@link #setObjectProperties}).
     */
    private static int objectPropertyMax;
    /**
     * Number of object records declared in {@code object.txt}.
     */
    private static int objectsInObject_txt;
    /**
     * The loaded object bases, resolved by name/tval via {@link #lookupObjectBase}.
     */
    private static List<ObjectBase> objectBases;
    /**
     * The loaded slays, resolved by code via {@link #lookupSlay}.
     */
    private static List<Slay> slays;
    /**
     * The loaded brands, resolved by code via {@link #lookupBrandCode}.
     */
    private static List<Brand> brands;
    /**
     * The loaded curses, resolved by name via {@link #lookupCurse}.
     */
    private static List<Curse> curses;
    /**
     * The loaded item-object templates.
     */
    private static List<ItemObject> itemObjects;
    /**
     * The loaded activations, resolved by name via {@link #lookupActivation}.
     */
    private static List<Activation> activations;
    /**
     * The loaded ego-item templates.
     */
    private static List<EgoItem> egoItems;
    /**
     * The loaded artifacts.
     */
    private static List<Artifact> artifacts;
    /**
     * The loaded object properties.
     */
    private static List<ObjectProperty> objectProperties;
    /**
     * The tval&rarr;sval&rarr;kind index over {@link #objectKinds}, maintained by {@link #addObjectKind}.
     */
    private static Map<TValue, Map<Integer, ObjectKind>> kindsByTvalSval = new HashMap<>();
    /**
     * The live, mutable object-kind table — grown by {@link #addObjectKind}, cleared by {@link #reset}.
     */
    private static List<ObjectKind> objectKinds = new ArrayList<>();

    /**
     * The loaded chest traps, in file order. Order is load-bearing twice over: each trap's pval bit
     * is its position, and {@code pick_one_chest_trap} draws only from the entries <em>after</em> the
     * first, which is always the "locked" no-trap entry. C holds the same data as a linked list
     * headed by its global {@code chest_traps} ({@code obj-chest.c:53}).
     */
    private static final List<ChestTrap> chestTraps = new ArrayList<>();

    /**
     * Replaces the loaded chest traps with the ones just read; set once by {@code ObjectDataLoader}.
     * Like {@link #setRunes}, this copies into the existing list rather than rebinding the field, so
     * the list itself stays final.
     *
     * @param chestTraps the chest traps to store, in file order
     */
    public static void setChestTraps(List<ChestTrap> chestTraps) {
        ObjectRegistry.chestTraps.clear();
        ObjectRegistry.chestTraps.addAll(chestTraps);
    }

    /**
     * Sentinel kind representing an unidentified pile of gold.
     */
    public static final ObjectKind unknownGoldKind = new ObjectKind();
    /**
     * Sentinel kind representing an unidentified item.
     */
    public static final ObjectKind unknownItemKind = new ObjectKind();

    /**
     * The complete rune list, built by {@link Rune#initRunes()}. Order is significant — it is the
     * order runes are listed in the knowledge menu, and C identifies a rune in its savefile by
     * position in this list.
     */
    private static final List<Rune> allRunes = new ArrayList<>();

    /**
     * Replaces the rune list with the one just built. Unlike the other setters here this copies
     * into the existing list rather than rebinding the field, so the list itself stays final.
     *
     * @param runes the runes to store, in list order
     */
    public static void setRunes(List<Rune> runes) {
        allRunes.clear();
        allRunes.addAll(runes);
    }

    /**
     * Records the current object-kind count as {@code objectBaseKindMax} — the ordinary-kind ceiling.
     */
    public static void updateObjectBaseKindMax() {
        ObjectRegistry.objectBaseKindMax = ObjectRegistry.objectKinds.size();
    }

    /**
     * @return the number of loaded artifacts
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getArtifactKindMax() {
        return artifactKindMax;
    }

    /**
     * @return the number of loaded ego items
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getEgoItemKindMax() {
        return egoItemKindMax;
    }

    /**
     * @return the number of activations available to random artifacts
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getRandartActivationsMax() {
        return randartActivationsMax;
    }

    /**
     * @return the number of loaded curses
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getCurseMax() {
        return curseMax;
    }

    /**
     * @return the number of loaded slays
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getSlayMax() {
        return slayMax;
    }

    /**
     * @return the number of loaded brands
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getBrandMax() {
        return brandMax;
    }

    /**
     * @return the number of object-power calculation records
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsPowerCalculationMax() {
        return objectPowerCalculationMax;
    }

    /**
     * @return the number of loaded object properties
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsPropertyMax() {
        return objectPropertyMax;
    }

    /**
     * @return the number of object records declared in {@code object.txt}
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectsInObject_txt() {
        return objectsInObject_txt;
    }

    /**
     * @return the current number of registered object kinds (ordinary plus synthesised)
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectKindCount() {
        return objectKinds.size();
    }

    /**
     * @return an unmodifiable view of the live object-kind table
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectKind> getObjectKinds() {
        return Collections.unmodifiableList(objectKinds);
    }

    /**
     * @return the ordinary-kind ceiling
     */
    @Contract(pure = true)
    @CheckReturnValue
    public static int getObjectBaseKindMax() {
        return objectBaseKindMax;
    }

    /**
     * @return an unmodifiable view of the loaded object bases
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectBase> getObjectBases() {
        return Collections.unmodifiableList(objectBases);
    }

    /**
     * Stores the loaded object bases; set once by {@code ObjectDataLoader} (before the kinds that reference them).
     */
    public static void setObjectBases(@NotNull List<ObjectBase> objectBases) {
        ObjectRegistry.objectBases = objectBases;
    }

    /**
     * @return an unmodifiable view of the loaded slays
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Slay> getSlays() {
        return Collections.unmodifiableList(slays);
    }

    /**
     * Stores the loaded slays and records their count in {@code slayMax}.
     */
    public static void setSlays(@NotNull List<Slay> slays) {
        ObjectRegistry.slays = slays;
        slayMax = slays.size();
    }

    /**
     * @return an unmodifiable view of the loaded brands
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Brand> getBrands() {
        return Collections.unmodifiableList(brands);
    }

    /**
     * Stores the loaded brands and records their count in {@code brandMax}.
     */
    public static void setBrands(@NotNull List<Brand> brands) {
        ObjectRegistry.brands = brands;
        brandMax = brands.size();
    }

    /**
     * @return an unmodifiable view of the loaded curses
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Curse> getCurses() {
        return Collections.unmodifiableList(curses);
    }

    /**
     * Stores the loaded curses and records their count in {@code curseMax}.
     */
    public static void setCurses(@NotNull List<Curse> curses) {
        ObjectRegistry.curses = curses;
        curseMax = curses.size();
    }

    /**
     * @return an unmodifiable view of the loaded item-object templates
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ItemObject> getItemObjects() {
        return Collections.unmodifiableList(itemObjects);
    }

    /**
     * Stores the loaded item-object templates; set once by {@code ObjectDataLoader}.
     */
    public static void setItemObjects(@NotNull List<ItemObject> itemObjects) {
        ObjectRegistry.itemObjects = itemObjects;
    }

    /**
     * @return an unmodifiable view of the loaded activations
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Activation> getActivations() {
        return Collections.unmodifiableList(activations);
    }

    /**
     * Stores the loaded activations; set once by {@code ObjectDataLoader}.
     */
    public static void setActivations(@NotNull List<Activation> activations) {
        ObjectRegistry.activations = activations;
    }

    /**
     * @return an unmodifiable view of the loaded ego-item templates
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<EgoItem> getEgoItems() {
        return Collections.unmodifiableList(egoItems);
    }

    /**
     * Stores the loaded ego items and records their count in {@code egoItemKindMax}.
     */
    public static void setEgoItems(@NotNull List<EgoItem> egoItems) {
        ObjectRegistry.egoItems = egoItems;
        egoItemKindMax = egoItems.size();
    }

    /**
     * @return an unmodifiable view of the loaded artifacts
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<Artifact> getArtifacts() {
        return Collections.unmodifiableList(artifacts);
    }

    /**
     * Stores the loaded artifacts; set once by {@code ObjectDataLoader}.
     */
    public static void setArtifacts(@NotNull List<Artifact> artifacts) {
        ObjectRegistry.artifacts = artifacts;
    }

    /**
     * @return an unmodifiable view of the loaded object properties
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static List<ObjectProperty> getObjectProperties() {
        return Collections.unmodifiableList(objectProperties);
    }

    /**
     * Stores the loaded object properties and records their count in {@code objectPropertyMax}.
     */
    public static void setObjectProperties(@NotNull List<ObjectProperty> objectProperties) {
        ObjectRegistry.objectProperties = objectProperties;
        objectPropertyMax = objectProperties.size();
    }

    /**
     * @return an unmodifiable view of the kindsByTvalSval map.
     */
    @UnmodifiableView
    @Contract(pure = true)
    @NotNull
    public static Map<TValue, Map<Integer, ObjectKind>> getKindsByTvalSval() {
        return Collections.unmodifiableMap(kindsByTvalSval);
    }

    /**
     * Replaces the object-kind table wholesale. Note this does <em>not</em> rebuild
     * {@link #kindsByTvalSval}; prefer {@link #addObjectKind} for individual registration.
     */
    public static void setObjectKinds(@NotNull List<ObjectKind> objectKinds) {
        ObjectRegistry.objectKinds = objectKinds;
    }

    /**
     * Clears the object-kind table and its {@link #kindsByTvalSval} index together, so a
     * re-initialisation ({@code GameConstants.init()}) starts from an empty registry rather than
     * double-registering kinds.
     */
    public static void reset() {
        objectKinds.clear();
        kindsByTvalSval.clear();
    }

    /**
     * Looks up an object kind by its numeric (tval, sval) via the {@link #kindsByTvalSval} index —
     * the constant-time counterpart to the name-based {@link #lookupObjectKind(TValue, String)}.
     *
     * @param tValue the object type value
     * @param sValue the numeric sub-type value
     * @return the matching {@link ObjectKind}, or {@code null} if none is indexed
     * @throws IllegalStateException if object kinds have not been loaded
     * @author Rowan Crowther
     */
    @CheckReturnValue
    @Nullable
    public static ObjectKind lookupObjectKind(TValue tValue, int sValue) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        Map<Integer, ObjectKind> map = kindsByTvalSval.get(tValue);
        return map == null ? null : map.get(sValue);
    }

    /**
     * Get an ObjectKind based on its name
     *
     * @param name the name of the object kind we are searching for
     * @return the object kind with that name or null if it doesn't
     * exist
     */
    @CheckReturnValue
    @Nullable
    public static ObjectKind lookupObjectKind(@NotNull String name) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectKinds.stream()
                .filter(e -> name.equals(e.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Look up an object kind by its tval and an sval <em>reference</em>, resolving the reference the
     * way the data files use it (C's {@code lookup_sval}): if {@code ref} is all digits it is treated
     * as a literal numeric sval and dispatched to {@link #lookupObjectKind(TValue, int)}; otherwise it
     * is matched case-insensitively against each kind's {@link ObjectKind#getsValueName() sval name}.
     *
     * @param tval the object type value
     * @param ref  the sval reference — either a decimal sval or a sub-type name
     * @return the matching {@link ObjectKind}, or {@code null} if none matches
     * @throws IllegalStateException if object kinds have not been loaded
     * @author ClaudeCode
     */
    @CheckReturnValue
    @Nullable
    public static ObjectKind lookupObjectKind(@NotNull TValue tval, @NotNull String ref) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        try {
            return lookupObjectKind(tval, Integer.parseInt(ref.trim()));
        } catch (NumberFormatException NAN) {
            return objectKinds.stream()
                    .filter(k -> tval.equals(k.gettValue()) &&
                            ref.equalsIgnoreCase(k.getsValueName()))
                    .findFirst().orElse(null);
        }
    }

    /**
     * Collect every loaded object kind of a given type — the tval fan-out used, for example, when an
     * ego {@code type:} line applies to all kinds of a tval.
     *
     * @param tval the object type value
     * @return every {@link ObjectKind} with that tval (possibly empty, never {@code null})
     * @throws IllegalStateException if object kinds have not been loaded
     */
    @NotNull
    @CheckReturnValue
    public static List<ObjectKind> lookupObjectKind(@NotNull TValue tval) {
        if (objectKinds.isEmpty()) {
            String message = "Invalid attempt to access objectKinds when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        List<ObjectKind> results = new ArrayList<>();
        for (ObjectKind k : objectKinds) {
            if (tval.equals(k.gettValue())) {
                results.add(k);
            }
        }

        return results;
    }

    /**
     * Search through the slays to get a slay with the same code as
     * the incoming parameter
     *
     * @param slayName the name/code of the slay to find
     * @return A slay where the code name is the same as the incoming
     * parameter, or null
     */
    @Nullable
    @CheckReturnValue
    public static Slay lookupSlay(String slayName) {
        if (slays == null) {
            String message = "Invalid attempt to access slays when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return slays.stream()
                .filter(s -> s.getCode().equals(slayName))
                .findFirst().orElse(null);
    }

    /**
     * Locate a cruse by its name
     *
     * @param curseName the name of the curse we are looking for
     * @return The curse with the relevant name or null
     */
    @Nullable
    @CheckReturnValue
    public static Curse lookupCurse(String curseName) {
        if (curses == null) {
            String message = "Invalid attempt to access curses when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return curses.stream()
                .filter(c -> c.getName().equals(curseName))
                .findFirst().orElse(null);
    }

    /**
     * Register an object kind: allocate it the next sval under its base (svals are 1-based per base),
     * append it to {@link #objectKinds}, and index it in {@link #kindsByTvalSval} for fast
     * (tval, sval) lookup. This is the single choke-point that keeps a kind's numeric sval and the
     * lookup index in step, so synthesised kinds (e.g. spellbooks) register exactly like file-loaded
     * ones.
     *
     * @param toAdd the ObjectKind to register
     * @author Rowan Crowther
     */
    public static void addObjectKind(@NotNull ObjectKind toAdd) {
        ObjectBase base = toAdd.getBase();
        int sVal = base.getNumSvals() + 1;      // svals are 1-based within each base
        base.setNumSvals(sVal);
        toAdd.setsVal(sVal);
        toAdd.setKindIndex(objectKinds.size());
        objectKinds.add(toAdd);
        kindsByTvalSval
                .computeIfAbsent(toAdd.gettValue(), k -> new HashMap<>())
                .put(sVal, toAdd);
    }

    /**
     * Get the ObjectBase which has a given string as its name
     *
     * @param name the name we are searching the object base list for
     * @return the object base with given name or null
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase lookupObjectBase(@NotNull String name) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(o -> name.equals(o.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Get the ObjectBase which has a given string as its name and TValue as its TValue
     *
     * @param name   the name we are searching the object base list for
     * @param tValue the TValue we are searching for
     * @return the object base with given name or null
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase lookupObjectBase(@NotNull String name, @NotNull TValue tValue) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(o -> (name.equals(o.getName()) && tValue == o.gettVal()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Lookup an item base based on its tval and return the first base found
     *
     * @param tVal The tval we are looking for
     * @return the first item base with that tVal
     */
    @Nullable
    @CheckReturnValue
    public static ObjectBase getBaseFromTVal(@NotNull TValue tVal) {
        if (objectBases == null) {
            String message = "Invalid attempt to access objectBases when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return objectBases.stream()
                .filter(ob -> tVal.equals(ob.gettVal()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Return a brand based on the brand name
     *
     * @param name the name of the brand to return
     * @return the brand or null if it isn't found
     */
    @Nullable
    public static Brand lookupBrandCode(@NotNull String name) {
        if (brands == null) {
            String message = "Invalid attempt to access brands when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return brands.stream().filter(b -> name.equals(b.getCode()))
                .findFirst().orElse(null);
    }

    /**
     * Get an activation by its name
     *
     * @param name The name of the activation we are searching for
     * @return the Activation in the List activations with the name equal to the incoming parameter
     */
    @Nullable
    public static Activation lookupActivation(@NotNull String name) {
        if (activations == null) {
            String message = "Invalid attempt to access activations when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return activations.stream().filter(e -> name.equals(e.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Finds the property definition describing a given flag, modifier or element. Ports C's
     * {@code lookup_obj_property} ({@code src/obj-properties.c}), which searches the same loaded
     * property list.
     * <p>
     * A request for {@code OBJ_PROPERTY_MOD} also matches properties declared as
     * {@code OBJ_PROPERTY_STAT}. The two are one contiguous range in C — the stats occupy the first
     * few modifier slots — so a lookup by modifier finds a stat without needing to know which of
     * the two it is asking about.
     *
     * @param type    the category of property wanted
     * @param payload the flag, modifier or element to find the definition for
     * @return the matching property, or {@code null} if the loaded data declares none, which means
     * the data files and the enums have drifted apart
     */
    public static ObjectProperty lookupObjectProperty(ObjPropertyType type, ObjectPropertyTypeWrapper payload) {
        for (ObjectProperty property : objectProperties) {
            if (property.getType().equals(type) && property.getPayload().equals(payload))
                return property;

            if (type.equals(ObjPropertyType.OBJ_PROPERTY_MOD) && property.getType().equals(ObjPropertyType.OBJ_PROPERTY_STAT)
                    && property.getPayload().equals(payload))
                return property;
        }

        return null;
    }
}
