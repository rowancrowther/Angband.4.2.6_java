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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.Trap;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The read side of the terrain slice: the loaded terrain features and trap kinds, plus the trap count, held as static
 * state. It stands in for three C globals: {@code f_info} (declared in {@code cave.h}), {@code trap_info} (declared in
 * {@code trap.h}) and {@code z_info->trap_max}. {@link uk.co.jackoftradesltd.middle.game.globals.loaders.TerrainDataLoader}
 * fills it through the setters.
 *
 * <p>Both lists keep the order of their data file, as the C arrays do. In C a feature is found by indexing
 * {@code f_info} with its {@code FEAT_} constant, so {@link #lookupFeature} searches by terrain code instead. A trap
 * kind's table position is its {@code trapKindIndex}, and element 0 is the real {@code no trap} kind, which
 * {@link #lookupTrap} skips as {@code lookup_trap()} in {@code trap.c} does.
 *
 * <p>Class coded before 261009, commented in full on 261009.
 *
 * @author Rowan Crowther
 */
public class TerrainRegistry {
    /**
     * Logger used to record an attempt to read the features before they have been loaded.
     *
     * <p>Field logger coded before 261009, commented in full on 261009.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The number of trap kinds (C {@code z_info->trap_max}), counting the {@code no trap} kind at index 0. It is
     * set by {@link #setTrapInfo} to the size of the list it is given and is 0 until then. If a trap record fails to
     * assemble, its {@code trapKindIndex} is still used up, so the highest index can exceed {@code trapMax - 1}.
     *
     * <p>Field trapMax coded before 261009, given its value in {@link #setTrapInfo} on 261009, commented in full on
     * 261009.
     */
    private static int trapMax;
    /**
     * Every terrain feature in {@code terrain.txt} order (C {@code f_info}). It is {@code null} until
     * {@link #setFeatures} is called, and {@link #lookupFeature} treats that as an error.
     *
     * <p>Field features coded before 261009, commented in full on 261009.
     */
    private static List<Feature> features;
    /**
     * Every trap kind in {@code trap.txt} order (C {@code trap_info}). Element 0 is the {@code no trap} kind. It
     * starts as an empty list, so a trap lookup before loading finds nothing.
     *
     * <p>Field trapInfo coded before 261009, commented in full on 261009.
     */
    private static List<TrapKind> trapInfo = new ArrayList<>();

    /**
     * Returns the loaded terrain features in file order.
     *
     * <p>Function getFeatures coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the feature list
     * @throws NullPointerException if the features have not been loaded
     */
    public static List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    /**
     * Installs the terrain features, replacing any already held. The list is kept by reference, not copied.
     *
     * <p>Function setFeatures coded before 261009, commented in full on 261009.
     *
     * @param features the features in {@code terrain.txt} order
     */
    public static void setFeatures(List<Feature> features) {
        TerrainRegistry.features = features;
    }

    /**
     * Returns the loaded trap kinds in file order, including the {@code no trap} kind at index 0. It returns the
     * same view as {@link #getTrapKinds}.
     *
     * <p>Function getTrapInfo coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the trap-kind list
     */
    public static List<TrapKind> getTrapInfo() {
        return Collections.unmodifiableList(trapInfo);
    }

    /**
     * Installs the trap kinds and sets {@code trapMax} to the number of kinds in the list, as
     * {@code finish_parse_trap()} in {@code init.c} sets {@code z_info->trap_max} to the number of records. The
     * list is kept by reference, not copied.
     *
     * <p>Function setTrapInfo coded before 261009, trapMax assignment added on 261009, commented in full on 261009.
     *
     * @param trapInfo the trap kinds in {@code trap.txt} order, with {@code no trap} first
     */
    public static void setTrapInfo(List<TrapKind> trapInfo) {
        TerrainRegistry.trapInfo = trapInfo;
        trapMax = trapInfo.size();
    }

    /**
     * Looks up a terrain feature by its terrain code. C reaches the same feature by indexing {@code f_info} with the
     * {@code FEAT_} constant; this returns the first feature whose code equals the argument, so it relies on each
     * code appearing once in {@code terrain.txt}. A code with no feature gives {@code null}, where C would read a
     * zeroed or out-of-range slot.
     *
     * <p>Function lookupFeature coded before 261009, commented in full on 261009.
     *
     * @param flag the terrain code
     * @return the matching {@link Feature}, or {@code null} if none matches
     * @throws IllegalStateException if features have not been loaded
     */
    @Nullable
    public static Feature lookupFeature(@NotNull TerrainFlags flag) {
        if (features == null) {
            String message = "Invalid attempt to access features when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return features.stream().filter(f -> flag.equals(f.getTerrainFlag()))
                .findFirst().orElse(null);
    }

    /**
     * Looks up a trap kind by its short description, the Java form of {@code lookup_trap()} in {@code trap.c}. The
     * scan skips the kind at {@code trapKindIndex} 0 (the {@code no trap} kind), as C starts its loop at 1, and skips
     * any kind with a {@code null} name. The first kind whose description equals the argument exactly
     * (case-sensitive) is returned at once. Otherwise the first kind whose description contains the argument
     * case-insensitively (C {@code my_stristr}) is returned. An empty string therefore returns the kind at index 1,
     * {@code glyph of warding} in the shipped data, and the argument {@code "no trap"} finds nothing.
     *
     * <p>Function lookupTrap coded before 261009, index-0 skip added on 261009, commented in full on 261009.
     *
     * @param description the trap description to match, exactly or as a substring
     * @return the matching {@link TrapKind}, or {@code null} if none matches
     */
    @CheckReturnValue
    public static @Nullable TrapKind lookupTrap(@NotNull String description) {
        TrapKind closest = null;

        for (TrapKind tk : TerrainRegistry.getTrapKinds()) {
            // skip the first trap
            if (tk.getTrapKindIndex() == 0) continue;
            if (tk.getTrapKindName() == null) continue;

            // Test for equality
            if (tk.getDescription().equals(description)) {
                return tk;
            }

            // Test for close matches
            if (closest == null && tk.getDescription().toLowerCase(Locale.ROOT)
                    .contains(description.toLowerCase(Locale.ROOT))) {
                closest = tk;
            }
        }

        // Return 1st close match
        return closest;
    }

    /**
     * Returns the number of trap kinds (C {@code z_info->trap_max}), counting the {@code no trap} kind at index 0.
     *
     * <p>Function getTrapMax coded before 261009, commented in full on 261009.
     *
     * @return the size of the list last given to {@link #setTrapInfo}, or 0 before any trap kinds are loaded
     */
    public static int getTrapMax() {
        return trapMax;
    }

    /**
     * Returns the loaded trap kinds in file order, including the {@code no trap} kind at index 0. It returns the
     * same view as {@link #getTrapInfo}.
     *
     * <p>Function getTrapKinds coded before 261009, commented in full on 261009.
     *
     * @return an unmodifiable view of the trap-kind list
     */
    public static List<TrapKind> getTrapKinds() {
        return Collections.unmodifiableList(trapInfo);
    }
}
