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
import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.middle.Message;
import uk.co.jackoftradesltd.middle.magic.MagicRealm;
import uk.co.jackoftradesltd.middle.player.*;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.*;

/**
 * Runtime holder for all player-domain game data — properties, shapes, history charts, bodies,
 * races, magic realms, classes, and timed effects — together with the derived {@code *Max}
 * counters and the name/index lookups the running game queries.
 *
 * <p>In C this data is a scatter of file-scope globals: the linked lists {@code races},
 * {@code bodies}, {@code realms}, {@code shapes} and {@code timed_effects}, the counters
 * {@code z_info->equip_slots_max} and {@code z_info->shape_max}, the {@code PY_FOOD_*} ints, and
 * the lookups that walk them ({@code lookup_realm} in {@code player.c},
 * {@code lookup_player_shape} in {@code player-util.c}, the static {@code findchart} in
 * {@code init.c}, and the race-by-name loop in {@code load.c}). The port gathers them into one
 * class but keeps them static, so a caller still reaches them as globals rather than through an
 * instance.
 *
 * <p>Two differences of shape run through the whole class. First, C builds every parsed list by
 * prepending, so its lists run in reverse file order; the port keeps file order. Every lookup here
 * is a first-match search over names or numbers that are unique in the shipped data, so the order
 * does not change which record is found. Second, each lookup first checks that its list has been
 * loaded and throws {@link IllegalStateException} if not. C has no equivalent: an unloaded C list
 * is simply {@code NULL}, and its walk finds nothing.
 *
 * <p>This is the read side of the player slice: it is populated once at startup by
 * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.PlayerDataLoader}, whose loaders read
 * their cross-domain dependencies (UI entries, summons, item objects) from the other registries.
 * Thereafter it is only read. It was split out of {@code GameConstants} as one domain slice of the
 * loader/registry refactor.
 *
 * <p>Class PlayerRegistry commented in full on 261008.
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
    /**
     * The highest character level. The port of C's {@code PY_MAX_LEVEL} ({@code player.h}), which
     * carries the same value.
     *
     * <p>It is also the length of C's experience table {@code player_exp} and of the per-level
     * hit-point array {@code player_hp}, so the last level's entry in either sits at index
     * {@code PY_MAX_LEVEL - 1}. The level-up loops in {@code player.c} stop once the level reaches
     * it, and {@code player-birth.c} uses it to set the bounds a rolled hit-point total must fall
     * within.
     *
     * <p>Field PY_MAX_LEVEL commented in full on 261008.
     */
    public static final int PY_MAX_LEVEL = 50;
    /**
     * The level at which a character learns all runes. The port of C's {@code PY_KNOW_LEVEL}
     * ({@code player.h}), which carries the same value.
     *
     * <p>C defines it but no 4.2.6 C source file reads it, so it is carried here for completeness
     * rather than because any ported logic needs it yet.
     *
     * <p>Field PY_KNOW_LEVEL commented in full on 261008.
     */
    public static final int PY_KNOW_LEVEL = 30;
    /**
     * Logger for this registry. The lookups use it to log, at {@code fatal}, an access made before
     * the data was loaded, just before they throw the matching exception; {@link #lookupRealm}
     * also logs, at {@code error}, a realm name it cannot resolve.
     *
     * <p>Field logger commented in full on 261008.
     */
    private static final Logger logger = LogManager.getLogger();
    /**
     * The "Starving" threshold, first of the six nourishment thresholds the game compares a
     * {@code TMD_FOOD} counter against - the port of C's {@code PY_FOOD_*} globals
     * ({@code player-timed.c}).
     *
     * <p><b>These are not constants in C either</b>, which is why they sit here among the loaded
     * data rather than beside {@link #PY_MAX_EXP}. C declares them as bare {@code int}s and fills
     * them in during parsing: {@code parse_player_timed_grade} matches each grade of the
     * {@code FOOD} timed effect by name and copies its maximum into the matching global. The port
     * does the same work at the same point, in {@link #setPlayerTimedEffects}, and they are zero
     * until that runs.
     *
     * <p>The maxima come from {@code player_timed.txt} as percentages - {@code 1 / 4 / 8 / 15 / 90
     * / 100} - and are scaled by {@code player:food-value} from {@code constants.txt} before being
     * stored, so the figures held here are the products: {@code 100 / 400 / 800 / 1500 / 9000 /
     * 10000}. They are only meaningful against a counter on that same scale.
     *
     * <p>Fields PY_FOOD_* commented in full on 261008.
     */
    private static int PY_FOOD_STARVE;
    /**
     * The "Faint" threshold; C's {@code PY_FOOD_FAINT}. See {@link #PY_FOOD_STARVE} for how the six
     * are filled.
     *
     * <p>Field PY_FOOD_FAINT commented in full on 261008.
     */
    private static int PY_FOOD_FAINT;
    /**
     * The "Weak" threshold; C's {@code PY_FOOD_WEAK}. See {@link #PY_FOOD_STARVE} for how the six
     * are filled.
     *
     * <p>Field PY_FOOD_WEAK commented in full on 261008.
     */
    private static int PY_FOOD_WEAK;
    /**
     * The "Hungry" threshold; C's {@code PY_FOOD_HUNGRY}. See {@link #PY_FOOD_STARVE} for how the
     * six are filled.
     *
     * <p>Field PY_FOOD_HUNGRY commented in full on 261008.
     */
    private static int PY_FOOD_HUNGRY;
    /**
     * The "Fed" threshold; C's {@code PY_FOOD_FULL}. Note the name shift: C stores the grade called
     * "Fed" in {@code PY_FOOD_FULL}, and the grade called "Full" in {@link #PY_FOOD_MAX}. See
     * {@link #PY_FOOD_STARVE} for how the six are filled.
     *
     * <p>Field PY_FOOD_FULL commented in full on 261008.
     */
    private static int PY_FOOD_FULL;

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
     * The "Full" threshold and the food counter's ceiling; C's {@code PY_FOOD_MAX}. See
     * {@link #PY_FOOD_STARVE} for how the six are filled.
     *
     * <p>Field PY_FOOD_MAX commented in full on 261008.
     */
    private static int PY_FOOD_MAX;

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

    /**
     * The experience-to-level table, keyed by level index (level 1 at key {@code 0}) with the
     * total experience needed to reach it. The port of C's {@code player_exp[PY_MAX_LEVEL]}
     * constant array ({@code player.c}), which carries the same fifty values in the same order.
     *
     * <p>Unlike the C array this is mutable and starts empty; it is filled once at startup by
     * {@link uk.co.jackoftradesltd.middle.game.globals.loaders.PlayerDataLoader#initializeExpLevel}
     * rather than being a compile-time constant, but nothing in the running game writes to it
     * afterwards.
     *
     * <p>Field playerExperience coded on 260925, commented in full on 260925.
     */
    public static Map<Integer, Long> playerExperience = new HashMap<>();
    /**
     * The most equipment slots any loaded body has - the port of C's
     * {@code z_info->equip_slots_max}.
     *
     * <p>C computes it in {@code finish_parse_body} ({@code init.c}) by scanning every body for the
     * largest {@code count}, and then sizes every body's slot array to it. The port computes the
     * same maximum in {@link #setPlayerBodies}; with the shipped data there is a single
     * {@code Humanoid} body of twelve slots, so the value is twelve. It is zero until the bodies
     * are loaded.
     *
     * <p>Field playerEquipmentSlotsMax commented in full on 261008.
     */
    private static int playerEquipmentSlotsMax;
    /**
     * The number of loaded player shapes - the port of C's {@code z_info->shape_max}.
     *
     * <p>C increments that counter once per {@code name:} line in {@code shape.txt}, as each shape
     * is parsed ({@code parse_shape_name} in {@code init.c}); the port takes the size of the
     * assembled list in {@link #setPlayerShape}. The two agree because every parsed name yields
     * one shape. It is zero until the shapes are loaded.
     *
     * <p>Field playerShapeMax commented in full on 261008.
     */
    private static int playerShapeMax;
    /**
     * The loaded player shapes in file order - the port of C's {@code shapes} list (which C holds
     * in reverse file order). Resolved by name via {@link #lookupPlayerShape}; {@code null} until
     * loaded.
     *
     * <p>Field playerShapes commented in full on 261008.
     */
    private static List<PlayerShape> playerShapes;
    /**
     * The loaded background history charts in file order - the port of C's static
     * {@code histories} list in {@code init.c}. Resolved by chart number via
     * {@link #lookupPlayerHistoryChart}; {@code null} until loaded.
     *
     * <p>Field playerHistoryCharts commented in full on 261008.
     */
    private static List<PlayerHistoryChart> playerHistoryCharts;
    /**
     * The loaded body layouts in file order - the port of C's {@code bodies} list. Resolved by
     * position via {@link #lookupPlayerBody}; {@code null} until loaded.
     *
     * <p>Field playerBodies commented in full on 261008.
     */
    private static List<PlayerBody> playerBodies;
    /**
     * The loaded player races in file order - the port of C's {@code races} list (which C holds in
     * reverse file order). Resolved by name via {@link #lookupPlayerRace}; {@code null} until
     * loaded.
     *
     * <p>Field playerRaces commented in full on 261008.
     */
    private static List<PlayerRace> playerRaces;
    /**
     * The loaded magic realms in file order - the port of C's {@code realms} list. Resolved by
     * name, ignoring case, via {@link #lookupRealm}; {@code null} until loaded.
     *
     * <p>Field realms commented in full on 261008.
     */
    private static List<MagicRealm> realms;
    /**
     * The loaded player classes in file order - the port of C's {@code classes} list. This class
     * offers no lookup over it; callers take the whole list from {@link #getPlayerClasses}.
     * {@code null} until loaded.
     *
     * <p>Field playerClasses commented in full on 261008.
     */
    private static List<PlayerClass> playerClasses;
    /**
     * The loaded timed-effect definitions in file order - the port of C's
     * {@code timed_effects[TMD_MAX]} array. C indexes that array by the {@code TMD_*} constant;
     * the port searches this list by {@link TimedEffect} identity in
     * {@link #lookupPlayerTimedEffect}. {@code null} until loaded.
     *
     * <p>Field playerTimedEffects commented in full on 261008.
     */
    private static List<PlayerTimedEffect> playerTimedEffects;
    /**
     * The loaded player properties (the abilities and other descriptors from
     * {@code player_property.txt}) in file order - the port of C's {@code player_abilities} list.
     * {@code null} until loaded.
     *
     * <p>Field playerProperties commented in full on 261008.
     */
    private static List<PlayerProperty> playerProperties;

    /**
     * The "Fed" grade's ceiling: comfortably nourished, the state in which no food adjustment
     * applies at all. Anything above it is a surfeit that costs speed.
     *
     * <p>This is the one a new character starts just inside:
     * {@link uk.co.jackoftradesltd.middle.player.PlayerBirth#playerGenerate} writes this value
     * less one, as C's {@code player_generate} does in {@code player-birth.c}.
     *
     * <p>Function getPyFoodFull commented in full on 261008.
     *
     * @return C's {@code PY_FOOD_FULL}, or zero if the timed effects are not loaded yet
     */
    public static int getPyFoodFull() {
        return PY_FOOD_FULL;
    }

    /**
     * Returns the loaded player properties.
     *
     * <p>Function getPlayerProperties commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded player properties
     * @throws NullPointerException if the properties have not been loaded
     */
    public static List<PlayerProperty> getPlayerProperties() {
        return Collections.unmodifiableList(PlayerRegistry.playerProperties);
    }

    /**
     * Stores the loaded player properties. Called once by {@code PlayerDataLoader}; the list is
     * kept by reference, not copied.
     *
     * <p>Function setPlayerProperties commented in full on 261008.
     *
     * @param playerProperties the assembled player properties, in file order
     */
    public static void setPlayerProperties(List<PlayerProperty> playerProperties) {
        PlayerRegistry.playerProperties = playerProperties;
    }

    /**
     * Stores the loaded player shapes and records their count in {@link #playerShapeMax}, the port
     * of C's {@code z_info->shape_max}. Called once by {@code PlayerDataLoader}; the list is kept
     * by reference, not copied.
     *
     * <p>C counts the shapes one at a time as it parses them; counting the assembled list once
     * gives the same total, since each parsed shape becomes one list entry.
     *
     * <p>Function setPlayerShape commented in full on 261008.
     *
     * @param playerShape the assembled player shapes, in file order
     */
    public static void setPlayerShape(@NotNull List<PlayerShape> playerShape) {
        PlayerRegistry.playerShapes = playerShape;
        playerShapeMax = playerShape.size();
    }

    /**
     * Returns the loaded player shapes.
     *
     * <p>Function getPlayerShapes commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded player shapes
     * @throws NullPointerException if the shapes have not been loaded
     */
    public static List<PlayerShape> getPlayerShapes() {
        return Collections.unmodifiableList(playerShapes);
    }

    /**
     * Returns the loaded background history charts.
     *
     * <p>Function getPlayerHistoryCharts commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded background history charts
     * @throws NullPointerException if the history charts have not been loaded
     */
    public static List<PlayerHistoryChart> getPlayerHistoryCharts() {
        return Collections.unmodifiableList(playerHistoryCharts);
    }

    /**
     * Stores the loaded background history charts. Called once by {@code PlayerDataLoader}, before
     * the races, which refer to a chart by number; the list is kept by reference, not copied.
     *
     * <p>Function setPlayerHistoryCharts commented in full on 261008.
     *
     * @param playerHistoryCharts the assembled history charts, in file order
     */
    public static void setPlayerHistoryCharts(@NotNull List<PlayerHistoryChart> playerHistoryCharts) {
        PlayerRegistry.playerHistoryCharts = playerHistoryCharts;
    }

    /**
     * Returns the loaded body layouts. These are the shared originals, not copies; a caller that
     * means to give a body to a player should use {@link #lookupPlayerBody}, which copies.
     *
     * <p>Function getPlayerBodies commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded body layouts
     * @throws NullPointerException if the bodies have not been loaded
     */
    public static List<PlayerBody> getPlayerBodies() {
        return Collections.unmodifiableList(playerBodies);
    }

    /**
     * Stores the loaded body layouts and records the largest slot count among them in
     * {@link #playerEquipmentSlotsMax}. Called once by {@code PlayerDataLoader}, before the races;
     * the list is kept by reference, not copied.
     *
     * <p>The scan is the port of the first half of C's {@code finish_parse_body} ({@code init.c}),
     * which resets {@code z_info->equip_slots_max} to zero and raises it to each body's
     * {@code count} in turn when that is larger. The port's {@code <} with the operands swapped is
     * the same test, so an empty list leaves the maximum at zero, and ties do not matter. The
     * second half of the C function, which copies each body's linked slot list into an array of
     * that length, has no counterpart here: the port's bodies already hold their slots in a list.
     *
     * <p>Function setPlayerBodies commented in full on 261008.
     *
     * @param playerBodies the assembled body layouts, in file order
     */
    public static void setPlayerBodies(@NotNull List<PlayerBody> playerBodies) {
        PlayerRegistry.playerBodies = playerBodies;

        int slotCount = 0;
        for (PlayerBody playerBody : playerBodies) {
            if (slotCount < playerBody.getCount())
                slotCount = playerBody.getCount();
        }

        playerEquipmentSlotsMax = slotCount;
    }

    /**
     * Returns the loaded player races.
     *
     * <p>Function getPlayerRaces commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded player races
     * @throws NullPointerException if the races have not been loaded
     */
    public static List<PlayerRace> getPlayerRaces() {
        return Collections.unmodifiableList(playerRaces);
    }

    /**
     * Stores the loaded player races. Called once by {@code PlayerDataLoader}, after the bodies
     * and history charts the races refer to; the list is kept by reference, not copied.
     *
     * <p>Function setPlayerRaces commented in full on 261008.
     *
     * @param playerRaces the assembled player races, in file order
     */
    public static void setPlayerRaces(@NotNull List<PlayerRace> playerRaces) {
        PlayerRegistry.playerRaces = playerRaces;
    }

    /**
     * Stores the loaded magic realms. Called once by {@code PlayerDataLoader}, before the classes,
     * whose spell books name a realm; the list is kept by reference, not copied.
     *
     * <p>Function setMagicRealm commented in full on 261008.
     *
     * @param realms the assembled magic realms, in file order
     */
    public static void setMagicRealm(List<MagicRealm> realms) {
        PlayerRegistry.realms = realms;
    }

    /**
     * Returns the loaded magic realms.
     *
     * <p>Function getMagicRealms commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded magic realms
     * @throws NullPointerException if the realms have not been loaded
     */
    public static List<MagicRealm> getMagicRealms() {
        return Collections.unmodifiableList(realms);
    }

    /**
     * Returns the loaded player classes.
     *
     * <p>Function getPlayerClasses commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded player classes
     * @throws NullPointerException if the classes have not been loaded
     */
    public static List<PlayerClass> getPlayerClasses() {
        return Collections.unmodifiableList(playerClasses);
    }

    /**
     * Stores the loaded player classes. Called once by {@code PlayerDataLoader}, after the realms;
     * the list is kept by reference, not copied.
     *
     * <p>Function setPlayerClasses commented in full on 261008.
     *
     * @param playerClasses the assembled player classes, in file order
     */
    public static void setPlayerClasses(List<PlayerClass> playerClasses) {
        PlayerRegistry.playerClasses = playerClasses;
    }

    /**
     * Returns the loaded timed-effect definitions.
     *
     * <p>Function getPlayerTimedEffects commented in full on 261008.
     *
     * @return an unmodifiable view of the loaded timed-effect definitions
     * @throws NullPointerException if the timed effects have not been loaded
     */
    public static List<PlayerTimedEffect> getPlayerTimedEffects() {
        return Collections.unmodifiableList(playerTimedEffects);
    }

    /**
     * Stores the loaded timed-effect definitions; set once by {@code PlayerDataLoader}, and fills
     * in the {@code PY_FOOD_*} thresholds from the {@code FOOD} effect on the way through.
     *
     * <p>The second job looks like an oddity of the port but is faithful to C. There the food
     * thresholds are written grade by grade as {@code player_timed.txt} is parsed
     * ({@code parse_player_timed_grade} in {@code player-timed.c}); a name matching one of the six
     * fixed strings copies that grade's maximum into the corresponding global, and a grade whose
     * name is a dummy - one character, which C reduces to {@code NULL} - matches nothing and is
     * skipped. The port does the same matching once, here, over the assembled list. Doing it at
     * registration rather than during parsing keeps the assembler free of the dependency, and the
     * end state is the same because the only thing that reads the thresholds is the running game.
     *
     * <p>C's matching is not confined to {@code FOOD}: it runs on every grade of every effect. The
     * port only searches {@code FOOD}'s grades. The two agree because no other effect in
     * {@code player_timed.txt} has a grade bearing any of the six names. The {@code null} skip also
     * covers the implicit zero grade at the head of each grade list, which C allocates with no
     * name.
     *
     * <p>The grade maxima arrive already multiplied by {@code player:food-value}: the assembler
     * applies that scale as C does, so the values stored here are the same
     * {@code 100 / 400 / 800 / 1500 / 9000 / 10000} the game compares a {@code TMD_FOOD} counter
     * against. Nothing rescales them afterwards.
     *
     * <p>Both versions tolerate the {@code FOOD} effect being absent - C never enters the branch,
     * the port's search yields nothing - and leave the thresholds at zero, which is why they
     * cannot be read meaningfully before the load has run.
     *
     * <p><b>Outstanding:</b> C guards the whole block on {@code food_scl != 1}, so were
     * {@code player:food-value} ever set to 1 the C globals would stay at zero while the port
     * would store the unscaled percentages. No shipped data reaches that case -
     * {@code constants.txt} sets the value to 100 - and the divergence is a difference in what
     * degenerate data does, not in what the game does.
     *
     * <p>Function setPlayerTimedEffects commented in full on 261008.
     *
     * @param playerTimedEffects the assembled timed-effect definitions, in file order
     */
    public static void setPlayerTimedEffects(@NotNull List<PlayerTimedEffect> playerTimedEffects) {
        PlayerRegistry.playerTimedEffects = playerTimedEffects;

        PlayerTimedEffect food = playerTimedEffects.stream()
                .filter(t -> t.getName() == TimedEffect.TMD_FOOD).findFirst().orElse(null);
        if (food != null) {
            for (TimedGrade grade : food.getGrade()) {
                if (grade.status() == null) continue;
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
     * <p>The port of C's static {@code findchart} in {@code init.c}, which walks a chart list and
     * returns the first chart whose {@code idx} equals the number asked for, or {@code NULL} at the
     * end of the list. The port does the same first-match search over the loaded list and returns
     * {@code null} on a miss. C's chart numbers are {@code unsigned int}; the port's are
     * {@code int}, which makes no difference for the small positive numbers {@code history.txt}
     * uses, but means a negative argument simply finds nothing.
     *
     * <p>C uses {@code findchart} in two places beyond lookups at run time: while parsing, to add
     * an entry to a chart already started, and while finishing, to resolve each entry's successor
     * chart. In the port those jobs belong to the history assembler, so this method serves only
     * callers that run after the load.
     *
     * <p>Function lookupPlayerHistoryChart commented in full on 261008.
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
     * Look up a player race by its display name, matching case exactly.
     *
     * <p>The port of the race-by-name loop in C's {@code rd_player} ({@code load.c}), which walks
     * the {@code races} list comparing with {@code streq} and keeps the first match. C has no
     * separate lookup function for this; the loop is written inline in the savefile reader. A miss
     * there leaves the player's race {@code NULL}, and the reader reports
     * {@code "Invalid player race (%s)."} and fails the load. The port returns {@code null} and
     * leaves that reporting to the caller.
     *
     * <p>The comparison is case-sensitive, unlike {@link #lookupRealm}: C uses {@code streq} here,
     * not {@code my_stricmp}, because the name was written to the savefile from the race record
     * itself and so always matches it exactly.
     *
     * <p>Function lookupPlayerRace commented in full on 261008.
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
     * Look up a player body layout by its position in load order, and return a copy of it — the
     * value a race stores as its body reference (C's {@code bodies[race->body]}). Index 0 is the
     * humanoid body, which is the only body every race currently uses.
     *
     * <p>C reaches the body by subscript and then {@code memcpy}s it into the player's own
     * {@code body} field, before giving the copy freshly allocated slots ({@code player_embody} in
     * {@code player-birth.c}). The port returns {@link PlayerBody#copy} for the same reason: the
     * player's body is changed as gear is worn, and those changes must not reach the shared
     * original.
     *
     * <p>C's index is unchecked, and an out-of-range one reads past the list. The port logs it at
     * {@code fatal} and rethrows the {@link IndexOutOfBoundsException}. In the shipped data every
     * race's body is 0 and there is one body, so the case cannot arise from loaded data.
     *
     * <p>Function lookupPlayerBody commented in full on 261008.
     *
     * @param number the body's index in the loaded body list
     * @return a copy of the {@link PlayerBody} at that index (never {@code null})
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
     * Look up a player shape by name, matching case exactly.
     *
     * <p>The port of C's {@code lookup_player_shape} in {@code player-util.c}, which walks the
     * {@code shapes} list comparing with {@code streq} and returns the first match. On a miss C
     * shows the player {@code "Could not find %s shape!"} through {@code msg} and returns
     * {@code NULL}; the port sends the same text through {@link Message#message} and returns
     * {@code null}. The miss is not fatal in either version, so callers must guard. The shape
     * returned is the shared original, not a copy, as in C.
     *
     * <p>Function lookupPlayerShape commented in full on 261008.
     *
     * @param name the shape name, e.g. {@code "normal"} or {@code "bear"}
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

        PlayerShape shape = playerShapes.stream().filter(s -> name.equals(s.getName()))
                .findFirst().orElse(null);

        if (shape == null)
            Message.message("Could not find %s shape!", name);

        return shape;
    }

    /**
     * Returns the most equipment slots any loaded body has — C's {@code z_info->equip_slots_max}.
     *
     * <p>Function getPlayerEquipmentSlotsMax commented in full on 261008.
     *
     * @return the value set by {@link #setPlayerBodies}, or zero if the bodies are not loaded yet
     */
    public static int getPlayerEquipmentSlotsMax() {
        return playerEquipmentSlotsMax;
    }

    /**
     * Returns the number of loaded player shapes — C's {@code z_info->shape_max}.
     *
     * <p>Function getPlayerShapeMax commented in full on 261008.
     *
     * @return the value set by {@link #setPlayerShape}, or zero if the shapes are not loaded yet
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
            String message = "Invalid attempt to access playerTimedEffects when it hasn't been initialized";
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        return playerTimedEffects.stream().filter(e -> e.getName() == timedEffect)
                .findFirst().orElse(null);
    }
}
