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
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import uk.co.jackoftrades.middle.magic.MagicRealm;
import uk.co.jackoftrades.middle.player.*;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.util.Collections;
import java.util.List;

/**
 * Runtime holder for all player-domain game data — properties, shapes, history charts, bodies,
 * races, magic realms, classes, and timed effects — together with the derived {@code *Max}
 * counters and the name/index lookups the running game queries.
 *
 * <p>This is the read side of the player slice: it is populated once at startup by
 * {@link uk.co.jackoftrades.middle.game.globals.loaders.PlayerDataLoader}, whose loaders read
 * their cross-domain dependencies (UI entries, summons, item objects) from the other registries.
 * Thereafter it is only read. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class PlayerRegistry {
    /**
     * Logger for this registry, used to record an access made before the data was loaded before
     * the matching exception is thrown.
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * Number of equipment slots on the default body (derived at load time).
     */
    private static int playerEquipmentSlotsMax;
    /**
     * Number of loaded player shapes (set from {@link #setPlayerShape}).
     */
    private static int playerShapeMax;
    /**
     * The loaded player shapes, resolved by name via {@link #lookupPlayerShape}.
     */
    private static List<PlayerShape> playerShapes;
    /**
     * The loaded background history charts, resolved by number via {@link #lookupPlayerHistoryChart}.
     */
    private static List<PlayerHistoryChart> playerHistoryCharts;
    /**
     * The loaded body layouts, resolved by index via {@link #lookupPlayerBody}.
     */
    private static List<PlayerBody> playerBodies;
    /**
     * The loaded player races, resolved by name via {@link #lookupPlayerRace}.
     */
    private static List<PlayerRace> playerRaces;
    /**
     * The loaded magic realms, resolved by name via {@link #lookupRealm}.
     */
    private static List<MagicRealm> realms;
    /**
     * The loaded player classes.
     */
    private static List<PlayerClass> playerClasses;
    /**
     * The loaded timed-effect definitions.
     */
    private static List<PlayerTimedEffect> playerTimedEffects;
    /**
     * Highest spell count across the magic realms.
     */
    private static int magicSpellMax;
    /**
     * The loaded player properties (stat and skill descriptors).
     */
    private static List<PlayerProperty> playerProperties;

    /**
     * Stores the loaded player properties; set once by {@code PlayerDataLoader}.
     */
    public static void setPlayerProperties(List<PlayerProperty> playerProperties) {
        PlayerRegistry.playerProperties = playerProperties;
    }

    /**
     * @return an unmodifiable view of the loaded player properties
     */
    public static List<PlayerProperty> getPlayerProperties() {
        return Collections.unmodifiableList(PlayerRegistry.playerProperties);
    }

    /**
     * Stores the loaded player shapes and records their count in {@code playerShapeMax}.
     */
    public static void setPlayerShape(@NotNull List<PlayerShape> playerShape) {
        PlayerRegistry.playerShapes = playerShape;
        playerShapeMax = playerShape.size();
    }

    /**
     * @return an unmodifiable view of the loaded player shapes
     */
    public static List<PlayerShape> getPlayerShapes() {
        return Collections.unmodifiableList(playerShapes);
    }

    /**
     * Stores the loaded background history charts; set once by {@code PlayerDataLoader}.
     */
    public static void setPlayerHistoryCharts(@NotNull List<PlayerHistoryChart> playerHistoryCharts) {
        PlayerRegistry.playerHistoryCharts = playerHistoryCharts;
    }

    /**
     * @return an unmodifiable view of the loaded background history charts
     */
    public static List<PlayerHistoryChart> getPlayerHistoryCharts() {
        return Collections.unmodifiableList(playerHistoryCharts);
    }

    /**
     * Stores the loaded body layouts; set once by {@code PlayerDataLoader} (before races).
     */
    public static void setPlayerBodies(@NotNull List<PlayerBody> playerBodies) {
        PlayerRegistry.playerBodies = playerBodies;
    }

    /**
     * @return an unmodifiable view of the loaded body layouts
     */
    public static List<PlayerBody> getPlayerBodies() {
        return Collections.unmodifiableList(playerBodies);
    }

    /**
     * Stores the loaded player races; set once by {@code PlayerDataLoader} (after bodies and history).
     */
    public static void setPlayerRaces(@NotNull List<PlayerRace> playerRaces) {
        PlayerRegistry.playerRaces = playerRaces;
    }

    /**
     * @return an unmodifiable view of the loaded player races
     */
    public static List<PlayerRace> getPlayerRaces() {
        return Collections.unmodifiableList(playerRaces);
    }

    /**
     * Stores the loaded magic realms; set once by {@code PlayerDataLoader} (before classes).
     */
    public static void setMagicRealm(List<MagicRealm> realms) {
        PlayerRegistry.realms = realms;
    }

    /**
     * @return an unmodifiable view of the loaded magic realms
     */
    public static List<MagicRealm> getMagicRealms() {
        return Collections.unmodifiableList(realms);
    }

    /**
     * Stores the loaded player classes; set once by {@code PlayerDataLoader}.
     */
    public static void setPlayerClasses(List<PlayerClass> playerClasses) {
        PlayerRegistry.playerClasses = playerClasses;
    }

    /**
     * @return an unmodifiable view of the loaded player classes
     */
    public static List<PlayerClass> getPlayerClasses() {
        return Collections.unmodifiableList(playerClasses);
    }

    /**
     * Stores the loaded timed-effect definitions; set once by {@code PlayerDataLoader}.
     */
    public static void setPlayerTimedEffects(@NotNull List<PlayerTimedEffect> playerTimedEffects) {
        PlayerRegistry.playerTimedEffects = playerTimedEffects;
    }

    /**
     * @return an unmodifiable view of the loaded timed-effect definitions
     */
    public static List<PlayerTimedEffect> getPlayerTimedEffects() {
        return Collections.unmodifiableList(playerTimedEffects);
    }

    /**
     * Searches for a magic realm based on the magic realm name
     *
     * @param realmName the name of the realm to return
     * @return the MagicRealm with the relevant name or null
     */
    @Nullable
    @CheckReturnValue
    public static MagicRealm lookupRealm(String realmName) {
        if (realms == null) {
            String message = "Invalid attempt to access realms when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return realms.stream()
                .filter(r -> realmName.equals(r.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Look up a player history chart by its chart number.
     *
     * @param chartId the chart number
     * @return the matching {@link PlayerHistoryChart}, or {@code null} if none matches
     * @throws IllegalStateException if history charts have not been loaded
     */
    @Nullable
    public static PlayerHistoryChart lookupPlayerHistoryChart(int chartId) {
        if (playerHistoryCharts == null) {
            String message = "Invalid attempt to access playerHistoryCharts when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerHistoryCharts.stream().filter(c -> c.getChartNumber() == chartId)
                .findFirst().orElse(null);
    }

    /**
     * Look up a player race by its display name, mirroring C's by-name race resolution when a
     * savefile is loaded ({@code load.c}).
     *
     * @param name the race's display name, e.g. {@code "Half-Troll"}
     * @return the matching {@link PlayerRace}, or {@code null} if no race has that name
     * @throws IllegalStateException if player races have not been loaded
     */
    @Nullable
    public static PlayerRace lookupPlayerRace(@NotNull String name) {
        if (playerRaces == null) {
            String message = "Invalid attempt to access playerRaces when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerRaces.stream().filter(p -> name.equals(p.getName()))
                .findFirst().orElse(null);
    }

    /**
     * Look up a player body layout by its position in load order — the value a race stores as its
     * body reference (C's {@code bodies[race->body]}). Index 0 is the humanoid body, which is the
     * only body every race currently uses.
     *
     * @param number the body's index in the loaded body list
     * @return the {@link PlayerBody} at that index (never {@code null})
     * @throws IllegalStateException     if player bodies have not been loaded
     * @throws IndexOutOfBoundsException if {@code number} is not a valid body index
     */
    public static PlayerBody lookupPlayerBody(int number) {
        if (playerBodies == null) {
            String message = "Invalid attempt to access playerBodies when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        try {
            return playerBodies.get(number);
        } catch (IndexOutOfBoundsException e) {
            String message = "Body number: " + number + " is out of bounds.";
            logger.fatal(message, e);
            throw e;
        }
    }

    /**
     * Look up a player shape by name.
     *
     * @param name the shape name
     * @return the matching {@link PlayerShape}, or {@code null} if none matches
     * @throws IllegalStateException if player shapes have not been loaded
     */
    @Nullable
    public static PlayerShape lookupPlayerShape(@NotNull String name) {
        if (playerShapes == null) {
            String message = "Invalid attempt to access playerShapes when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerShapes.stream().filter(s -> name.equalsIgnoreCase(s.getName()))
                .findFirst().orElse(null);
    }

    /**
     * @return the configured value of {@code magicSpellMax}
     */
    public static int getMagicSpellMax() {
        return magicSpellMax;
    }

    /**
     * @return the configured value of {@code playerEquipmentSlotsMax}
     */
    public static int getPlayerEquipmentSlotsMax() {
        return playerEquipmentSlotsMax;
    }

    /**
     * @return the configured value of {@code playerShapeMax}
     */
    public static int getPlayerShapeMax() {
        return playerShapeMax;
    }

    /**
     * Look up a timed effect's static definition by its {@link TimedEffect} identity.
     *
     * <p>This is the port of C's {@code &timed_effects[idx]}, and the difference in shape is worth
     * seeing. C's effects live in a fixed array indexed by the {@code TMD_*} constant itself, so
     * the lookup is an array subscript that cannot fail. The port keys them by enum identity and
     * searches the loaded list, because the two are only tied together by name when
     * {@code player_timed.txt} is parsed — an effect the data file never defined has no entry
     * here at all.
     *
     * <p>Hence the null return, which C has no equivalent of. {@link TimedEffect#TMD_NONE} is the
     * standing example: it is a sentinel the parsers hand back for an unresolvable name, not a
     * status, so no record is ever loaded for it. Callers are expected to guard — see
     * {@link uk.co.jackoftrades.middle.player.Player#timedGradeEq}.
     *
     * <p>Function lookupPlayerTimedEffect coded on 260818, commented in full on 260818.
     *
     * @param timedEffect the effect whose definition is wanted
     * @return the matching {@link PlayerTimedEffect}, or {@code null} if none was loaded for it
     * @throws IllegalStateException if the timed effects have not been loaded
     */
    @Nullable
    @CheckReturnValue
    public static PlayerTimedEffect lookupPlayerTimedEffect(@NotNull TimedEffect timedEffect) {
        if (playerTimedEffects == null) {
            String message = "Invalid attempt to access playerTimedEffects when it hasn't been initialised";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerTimedEffects.stream().filter(e -> e.getName() == timedEffect)
                .findFirst().orElse(null);
    }
}
