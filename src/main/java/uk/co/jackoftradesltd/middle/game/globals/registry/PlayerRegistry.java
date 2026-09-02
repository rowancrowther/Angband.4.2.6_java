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
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.player.*;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.*;

/**
 * Runtime holder for all player-domain game data — properties, shapes, history charts, bodies,
 * races, magic realms, classes, and timed effects — together with the derived {@code *Max}
 * counters and the name/index lookups the running game queries.
 *
 * <p>This is the read side of the player slice: it is populated once at startup by
 * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.PlayerDataLoader}, whose loaders read
 * their cross-domain dependencies (UI entries, summons, item objects) from the other registries.
 * Thereafter it is only read. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * @author Rowan Crowther
 */
public class PlayerRegistry {

    /**
     * The ceiling on a player's experience, both current and maximum. The port of C's
     * {@code PY_MAX_EXP} ({@code player.h}), which carries the same value.
     *
     * <p>{@code Player.adjustLevel} clamps both {@code exp} and {@code maxExp} to this before
     * recomputing the character level, so no amount of experience gain can push either past it.
     * Experience is also refused outright once a player is already at the cap.</p>
     *
     * <p>Field PY_MAX_EXP coded on 260831, commented in full on 260831.</p>
     */
    public static final long PY_MAX_EXP = 99999999L;
    public static final int PY_MAX_LEVEL = 50;
    public static final int PY_KNOW_LEVEL = 30;
    /**
     * The six nourishment thresholds the game compares a {@code TMD_FOOD} counter against - the
     * port of C's {@code PY_FOOD_*} globals ({@code player-timed.c:36-41}).
     *
     * <p><b>These are not constants in C either</b>, which is why they sit here among the loaded
     * data rather than beside {@link #PY_MAX_EXP}. C declares them as bare {@code int}s and fills
     * them in during parsing, matching each grade of the {@code FOOD} timed effect by name and
     * copying its maximum into the matching global ({@code player-timed.c:321-336}). The port does
     * the same work at the same point, in {@link #setPlayerTimedEffects}, and they are zero until
     * that runs.
     *
     * <p>The maxima come from {@code player_timed.txt} as percentages - {@code 1 / 4 / 8 / 15 / 90
     * / 100} - and are scaled by {@code player:food-value} from {@code constants.txt} before being
     * stored, so the figures held here are the products: {@code 100 / 400 / 800 / 1500 / 9000 /
     * 10000}. They are only meaningful against a counter on that same scale.
     *
     * <p>Fields PY_FOOD_* commented in full on 260902.
     */
    private static int PY_FOOD_STARVE;
    private static int PY_FOOD_FAINT;
    private static int PY_FOOD_WEAK;
    private static int PY_FOOD_HUNGRY;
    private static int PY_FOOD_FULL;
    private static int PY_FOOD_MAX;

    /**
     * The "Starving" grade's ceiling. Below it the character takes damage from hunger every turn.
     *
     * <p>Function getPyFoodStarve commented in full on 260902.
     *
     * @return C's {@code PY_FOOD_STARVE}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodStarve() {
        return PY_FOOD_STARVE;
    }

    /**
     * The "Faint" grade's ceiling - the band in which the character passes out at random.
     *
     * <p>Function getPyFoodFaint commented in full on 260902.
     *
     * @return C's {@code PY_FOOD_FAINT}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodFaint() {
        return PY_FOOD_FAINT;
    }

    /**
     * The "Weak" grade's ceiling.
     *
     * <p>Function getPyFoodWeak commented in full on 260902.
     *
     * @return C's {@code PY_FOOD_WEAK}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodWeak() {
        return PY_FOOD_WEAK;
    }

    /**
     * The "Hungry" grade's ceiling, and the point below which {@code calcBonuses} starts taking
     * to-hit, to-damage and skill away. It serves as both the origin and the divisor when that
     * shortfall is scaled.
     *
     * <p>Function getPyFoodHungry commented in full on 260902.
     *
     * @return C's {@code PY_FOOD_HUNGRY}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodHungry() {
        return PY_FOOD_HUNGRY;
    }

    /**
     * The "Fed" grade's ceiling: comfortably nourished, the state in which no food adjustment
     * applies at all. Anything above it is a surfeit that costs speed.
     *
     * <p>This is the one a new character starts just inside:
     * {@link uk.co.jackoftradesltd.middle.player.PlayerBirth#playerGenerate} writes this value
     * less one, as C does at {@code player-birth.c:1021}.
     *
     * <p>Function getPyFoodFull commented in full on 260902.
     *
     * @return C's {@code PY_FOOD_FULL}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodFull() {
        return PY_FOOD_FULL;
    }

    /**
     * The "Full" grade's ceiling and the counter's maximum - bloated. The gap between this and
     * {@link #getPyFoodFull} is the range the speed penalty is scaled over, so the two are only
     * meaningful as a pair.
     *
     * <p>Function getPyFoodMax commented in full on 260902.
     *
     * @return C's {@code PY_FOOD_MAX}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodMax() {
        return PY_FOOD_MAX;
    }

    public static Map<Integer, Long> playerExperience = new HashMap<>();
    
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
     * Stores the loaded timed-effect definitions; set once by {@code PlayerDataLoader}, and fills
     * in the {@code PY_FOOD_*} thresholds from the {@code FOOD} effect on the way through.
     *
     * <p>The second job looks like an oddity of the port but is faithful to C. There the food
     * thresholds are written grade by grade as {@code player_timed.txt} is parsed
     * ({@code player-timed.c:321-336}); a name matching one of the six fixed strings copies that
     * grade's maximum into the corresponding global, and a grade whose name is a dummy - one
     * character, which C reduces to {@code NULL} - matches nothing and is skipped. The port does
     * the same matching once, here, over the assembled list. Doing it at registration rather than
     * during parsing keeps the assembler free of the dependency, and the end state is the same
     * because the only thing that reads the thresholds is the running game.
     *
     * <p>The grade maxima arrive already multiplied by {@code player:food-value}: the assembler
     * applies that scale as C does ({@code player-timed.c:263, 322}), so the values stored here
     * are the same {@code 100 / 400 / 800 / 1500 / 9000 / 10000} the game compares a
     * {@code TMD_FOOD} counter against. Nothing rescales them afterwards.
     *
     * <p>Both versions tolerate the {@code FOOD} effect being absent - C never enters the branch,
     * the port's search yields nothing - and leave the thresholds at zero, which is why they
     * cannot be read meaningfully before the load has run.
     *
     * <p><b>Outstanding:</b> C guards the whole block on {@code food_scl != 1}, so were
     * {@code player:food-value} ever set to 1 the C globals would stay at zero while the port
     * would store the unscaled percentages. No shipped data reaches that case -
     * {@code constants.txt:204} sets the value to 100 - and the divergence is a difference in what
     * degenerate data does, not in what the game does.
     *
     * <p>Function setPlayerTimedEffects commented in full on 260902.
     *
     * @param playerTimedEffects the assembled timed-effect definitions, in file order
     */
    public static void setPlayerTimedEffects(@NotNull List<PlayerTimedEffect> playerTimedEffects) {
        PlayerRegistry.playerTimedEffects = playerTimedEffects;

        PlayerTimedEffect food = playerTimedEffects.stream()
                .filter(t -> t.getName() == TimedEffect.TMD_FOOD).findFirst().orElse(null);
        if (food != null) {
            for (TimedGrade grade : food.getGrade()) {
                switch (grade.status()) {
                    case "Starving" -> PY_FOOD_STARVE = grade.max();
                    case "Faint" -> PY_FOOD_FAINT = grade.max();
                    case "Weak" -> PY_FOOD_WEAK = grade.max();
                    case "Hungry" -> PY_FOOD_HUNGRY = grade.max();
                    case "Fed" -> PY_FOOD_FULL = grade.max();
                    case "Full" -> PY_FOOD_MAX = grade.max();
                }
            }
        }
    }

    /**
     * @return an unmodifiable view of the loaded timed-effect definitions
     */
    public static List<PlayerTimedEffect> getPlayerTimedEffects() {
        return Collections.unmodifiableList(playerTimedEffects);
    }

    /**
     * Look up a magic realm by name, ignoring case.
     *
     * <p>This is the port of C's {@code lookup_realm} in {@code player.c}. C walks the
     * {@code realms} linked list in load order and compares with {@code my_stricmp}, an
     * ASCII case-insensitive compare; the port streams the loaded list in the same order and
     * uses {@link String#equalsIgnoreCase}, which agrees with it for the realm names the data
     * files carry ({@code arcane}, {@code divine}, {@code nature}, {@code shadow}). The names
     * are stored as parsed, so the fold has to happen at the comparison — callers such as
     * {@code ClassSpellBookAssembler} pass whatever spelling {@code class.txt} used.
     *
     * <p>A miss is fatal in C: {@code lookup_realm} ends in {@code quit_fmt("Failed to find %s
     * magic realm", name)}, which tears the game down rather than returning. The port keeps
     * that severity by throwing {@link IllegalArgumentException} with the same message text,
     * so an unresolvable realm name still stops loading instead of quietly yielding nothing.
     *
     * <p>Function lookupRealm coded on 260831, commented in full on 260831.
     *
     * @param realmName the realm's name, in any case
     * @return the matching {@link MagicRealm}; never {@code null}
     * @throws IllegalStateException    if the realms have not been loaded
     * @throws IllegalArgumentException if no loaded realm bears that name
     */
    @CheckReturnValue
    public static MagicRealm lookupRealm(String realmName) {
        if (realms == null) {
            String message = "Invalid attempt to access realms when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        MagicRealm result = getMagicRealms().stream()
                .filter(realm -> realm.getName().equalsIgnoreCase(realmName))
                .findFirst().orElse(null);

        if (result == null) {
            String msg = "Failed to find " + realmName + " magic realm";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        return result;
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
     * The first race in load order — the port of reaching for C's {@code races} list head.
     *
     * <p>C builds its parsed lists by prepending, so its "first" race is the data file's last; the
     * port keeps file order, so this is the first race in {@code p_race.txt}. Callers wanting a
     * particular race should look it up by name instead — this is for the places that need
     * <em>some</em> race, such as seeding a default character before the player has chosen.
     *
     * <p>Function getFirstPlayerRace commented in full on 260820.
     *
     * @return the first loaded race, or {@code null} if the list loaded empty
     * @throws IllegalStateException if the player races have not been loaded
     */
    @Nullable
    public static PlayerRace getFirstPlayerRace() {
        if (playerRaces == null) {
            String message = "Invalid attempt to access playerRaces when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerRaces.stream().findFirst().orElse(null);
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
            return playerBodies.get(number).copy();
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
     * {@link PlayerTimed#timedGradeEq}.
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
