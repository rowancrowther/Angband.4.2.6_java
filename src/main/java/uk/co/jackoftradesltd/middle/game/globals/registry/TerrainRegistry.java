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

public class TerrainRegistry {
    private static final Logger logger = LogManager.getLogger();

    private static int trapMax;
    private static List<Feature> features;
    private static List<TrapKind> trapInfo = new ArrayList<>();

    public static List<Feature> getFeatures() {
        return Collections.unmodifiableList(features);
    }

    public static void setFeatures(List<Feature> features) {
        TerrainRegistry.features = features;
    }

    public static List<TrapKind> getTrapInfo() {
        return Collections.unmodifiableList(trapInfo);
    }

    public static void setTrapInfo(List<TrapKind> trapInfo) {
        TerrainRegistry.trapInfo = trapInfo;
    }

    /**
     * Look up a terrain feature by its terrain code.
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
     * Look up a trap kind by its description, trying an exact match first and
     * then a case-insensitive match.
     *
     * @param description the trap description
     * @return the matching {@link TrapKind}, or {@code null} if none matches
     */
    @CheckReturnValue
    public static @Nullable TrapKind lookupTrap(@NotNull String description) {
        TrapKind closest = null;

        for (TrapKind tk : TerrainRegistry.getTrapKinds()) {
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
     * @return the configured value of {@code trapMax}
     */
    public static int getTrapMax() {
        return trapMax;
    }

    public static List<TrapKind> getTrapKinds() {
        return Collections.unmodifiableList(trapInfo);
    }
}
