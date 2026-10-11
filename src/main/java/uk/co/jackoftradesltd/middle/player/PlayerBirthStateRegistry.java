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

package uk.co.jackoftradesltd.middle.player;

import uk.co.jackoftradesltd.middle.enums.Stats;

import java.util.HashMap;
import java.util.Map;

/**
 * The birth-state variables kept alongside the point-buy stat roller - the port of the block of C
 * statics near the top of {@code player-birth.c} ({@code stats}, {@code points_spent}, {@code
 * points_inc}, {@code points_left}, {@code quickstart_allowed}, {@code rolled_stats}, {@code prev}
 * and {@code quickstart_prev}). C's own comment above the first block reads "All of these should be
 * in some kind of 'birth state' struct somewhere else"; this class is that struct, given a name and
 * a home, extended to the two {@link Birther} snapshots C declares a little further down the same
 * file.
 *
 * <p>Where C indexes {@code stats}, {@code points_spent} and {@code points_inc} by the raw stat
 * integer into an array sized {@code STAT_MAX}, the port keys each on {@link Stats} directly, so
 * there is no bound to check and no sentinel index that would read past the end of the table.
 *
 * <p>The three per-stat maps are created together, lazily, the first time any of them is touched,
 * rather than at class load, and never recreated after that - see {@link
 * #initPlayerBirthStateRegistry()}. C has no equivalent step: a file-scope static array is zeroed
 * by the loader before {@code main} ever runs and stays the one array for the life of the process,
 * and Java's static fields carry no matching guarantee for a {@code Map}. A stat asked for before
 * it has ever been set answers {@code -1} - {@link Stats#STAT_NONE}'s own value, and the project's
 * usual "not found" sentinel - rather than C's implicit zero; every C caller of these fields writes
 * the whole array (through {@code reset_stats} or {@code generate_stats}) before the birth screen
 * ever reads it, so the two defaults are never observably different in practice.
 *
 * <p>{@link #prev} and {@link #quickstartPrev} get the same lazy treatment individually, not
 * through {@link #initPlayerBirthStateRegistry()} - a lone object needs no map to guard, so {@link
 * #getPrev()} and {@link #getQuickstartPrev()} each backfill a fresh {@link Birther} the first time
 * they are read before anything has been set. A freshly-constructed {@link Birther} carries
 * {@code age} at its own default of zero, which is exactly the sentinel C's own comment on
 * {@code prev} relies on ("We rely on prev.age being zero to determine whether there is a stored
 * character or not") - so the port's lazily-created instance and C's zero-initialized static
 * answer that check identically, even though one exists from process start and the other only
 * from first read.
 *
 * <p>The two guards differ in what they test. The whole-map accessors ({@link #getStats()},
 * {@link #setStats(Map)} and their two siblings) test {@code inited}; the per-stat accessors
 * ({@link #getStat(Stats)}, {@link #setStat(Stats, int)} and theirs) test whether their own map is
 * {@code null}. Both end in {@link #initPlayerBirthStateRegistry()}, so whichever is reached first
 * creates all three maps.
 *
 * <p>The class is static state, as the C globals it replaces are: nothing here is ported from a
 * function, and the methods below are plain accessors rather than translations of control flow.
 *
 * <p><b>Who uses it.</b> {@link PlayerBirth} is the only caller in {@code src/main}. It fetches
 * the three maps through the whole-map getters and passes them to {@code resetStats}, {@code
 * generateStats}, {@code buyStat} and {@code sellStat} to be mutated in place; it threads {@code
 * pointsLeft} through {@link #getPointsLeft()} and {@link #setPointsLeft(int)}; it sets and reads
 * both flags; it saves into and reads back {@code prev} and {@code quickstartPrev}; and {@code
 * doCmdRollStats} zeroes {@code pointsSpent} and {@code pointsInc} one stat at a time. Nothing in
 * {@code src/main} calls the per-stat reads and writes of the stat value ({@link #getStat(Stats)},
 * {@link #setStat(Stats, int)}), the per-stat reads of {@code pointsSpent} and {@code pointsInc},
 * or the three whole-map setters.
 *
 * <p>Class PlayerBirthStateRegistry coded on 260907, commented in full on 261011.
 *
 * @author Rowan Crowther
 */
public class PlayerBirthStateRegistry {
    /**
     * Whether the three per-stat maps below have been created yet - see {@link
     * #initPlayerBirthStateRegistry()}. Has no C equivalent: C's file-scope arrays are zeroed
     * once by the loader and always exist, so nothing there needs a "have I initialized this"
     * flag.
     *
     * <p>Field inited coded on 260907, commented in full on 260907.
     */
    private static boolean inited = false;

    /**
     * The point-buy value for every stat, keyed by {@link Stats} - the port of C's {@code
     * stats[STAT_MAX]} ({@code player-birth.c}). See {@link #getStat(Stats)}/{@link
     * #setStat(Stats, int)} and {@link #getStats()}/{@link #setStats(Map)}.
     *
     * <p>Field stats coded on 260907, commented in full on 261011.
     */
    private static Map<Stats, Integer> stats;

    /**
     * The points spent raising every stat so far, keyed by {@link Stats} - the port of C's
     * {@code points_spent[STAT_MAX]} ({@code player-birth.c}). See {@link
     * #getPointsSpent(Stats)}/{@link #setPointsSpent(Stats, int)} and {@link
     * #getPointsSpent()}/{@link #setPointsSpent(Map)}.
     *
     * <p>Field pointsSpent coded on 260907, commented in full on 261011.
     */
    private static Map<Stats, Integer> pointsSpent;

    /**
     * The cost of the next point-buy point on every stat, keyed by {@link Stats} - the port of
     * C's {@code points_inc[STAT_MAX]} ({@code player-birth.c}). See {@link
     * #getPointsInc(Stats)}/{@link #setPointsInc(Stats, int)} and {@link #getPointsInc()}/{@link
     * #setPointsInc(Map)}.
     *
     * <p>Field pointsInc coded on 260907, commented in full on 261011.
     */
    private static Map<Stats, Integer> pointsInc;

    /**
     * The point-buy points not yet spent on any stat - the port of C's {@code points_left}
     * ({@code player-birth.c}). See {@link #getPointsLeft()}/{@link #setPointsLeft(int)}.
     *
     * <p>Field pointsLeft coded on 260907, commented in full on 261011.
     */
    private static int pointsLeft;

    /**
     * Whether the previously-saved roller data is eligible for quickstart - the port of C's
     * {@code quickstart_allowed} ({@code player-birth.c}). See {@link
     * #isQuickstartAllowed()}/{@link #setQuickstartAllowed(boolean)}.
     *
     * <p>Field quickstartAllowed coded on 260907, commented in full on 261011.
     */
    private static boolean quickstartAllowed;

    /**
     * Whether the current stats came from the dice roller rather than point-buy - the port of
     * C's {@code rolled_stats = false} ({@code player-birth.c}), the one field of the six C
     * initializes explicitly rather than leaving to implicit zero. See {@link
     * #isRolledStats()}/{@link #setRolledStats(boolean)}.
     *
     * <p>Field rolledStats coded on 260907, commented in full on 261011.
     */
    private static boolean rolledStats = false;

    /**
     * The character snapshot kept for the birth screen's "flick between two rolls" undo - the
     * port of C's {@code prev} ({@code player-birth.c}). See {@link #getPrev()}/{@link
     * #setPrev(Birther)}.
     *
     * <p>Field prev coded on 260907, commented in full on 261011.
     */
    private static Birther prev;

    /**
     * The character snapshot kept for restoring a save file's quickstart character - the port of
     * C's {@code quickstart_prev} ({@code player-birth.c}). See {@link
     * #getQuickstartPrev()}/{@link #setQuickstartPrev(Birther)}.
     *
     * <p>Field quickstartPrev coded on 260907, commented in full on 261011.
     */
    private static Birther quickstartPrev;

    /**
     * The point-buy value for every stat, keyed by {@link Stats} - the port of the whole-array
     * form of reading C's {@code stats} ({@code player-birth.c}), the shape callers like
     * {@code reset_stats}, {@code generate_stats} and {@code get_stats} use when they take the
     * array by reference rather than one stat at a time.
     *
     * <p>The map returned is the live one, not a copy: {@link PlayerBirth#resetStats},
     * {@link PlayerBirth#buyStat}, {@link PlayerBirth#sellStat}, {@link PlayerBirth#generateStats}
     * and {@link PlayerBirth#getStats} write into it, which is how the port gets C's
     * array-decays-to-pointer behaviour.
     *
     * <p>Creates all three per-stat maps on first call if they do not yet exist - the {@code
     * inited} test, where {@link #getStat(Stats)} tests whether its own map is {@code null}; see
     * the class Javadoc.
     *
     * <p>Method getStats coded on 260907, commented in full on 261011.
     *
     * @return the live stats map, never {@code null}
     */
    public static Map<Stats, Integer> getStats() {
        if (!inited) initPlayerBirthStateRegistry();
        return stats;
    }

    /**
     * Writes the point-buy value for every stat, replacing the map wholesale - the port of the
     * whole-array form of writing C's {@code stats}, the shape a caller like {@code reset_stats}
     * or {@code generate_stats} uses when it fills the array afresh. Nothing in {@code src/main}
     * calls it: {@link PlayerBirth} mutates the map {@link #getStats()} hands out instead.
     *
     * <p>Guards with the same {@code inited} check {@link #initPlayerBirthStateRegistry()} uses
     * before assigning, so a set called before anything else has touched the three maps still
     * counts as the maps' creation - without this, a later getter's own lazy-init check would
     * still see {@code inited == false} and silently recreate all three maps, discarding
     * whatever was just stored here. Only the {@code stats} map is replaced; the other two keep
     * their identity.
     *
     * <p>Method setStats coded on 260907, commented in full on 261011.
     *
     * @param stats the stats map to store
     */
    public static void setStats(Map<Stats, Integer> stats) {
        if (!inited) initPlayerBirthStateRegistry();
        PlayerBirthStateRegistry.stats = stats;
    }

    /**
     * The points spent raising every stat so far, keyed by {@link Stats} - the port of the
     * whole-array form of reading C's {@code points_spent} ({@code player-birth.c}), the
     * shape {@code reset_stats}, {@code generate_stats}, {@code buy_stat} and {@code sell_stat}
     * use when they take the array by reference.
     *
     * <p>The map returned is the live one, not a copy, mutated in place by the same five {@link
     * PlayerBirth} methods that mutate the map from {@link #getStats()}. The same map is passed
     * to the birth-point event.
     *
     * <p>Creates all three per-stat maps on first call if they do not yet exist - the {@code
     * inited} test, where {@link #getPointsSpent(Stats)} tests whether its own map is {@code
     * null}; see the class Javadoc.
     *
     * <p>Method getPointsSpent coded on 260907, commented in full on 261011.
     *
     * @return the live points-spent map, never {@code null}
     */
    public static Map<Stats, Integer> getPointsSpent() {
        if (!inited) initPlayerBirthStateRegistry();
        return pointsSpent;
    }

    /**
     * Writes the points spent raising every stat so far, replacing the map wholesale - the port
     * of the whole-array form of writing C's {@code points_spent}, the shape a caller like
     * {@code reset_stats} or {@code generate_stats} uses when it fills the array afresh. Nothing in
     * {@code src/main} calls it.
     *
     * <p>Guards with the same {@code inited} check {@link #initPlayerBirthStateRegistry()} uses
     * before assigning - see {@link #setStats(Map)} for why. Only the {@code pointsSpent} map is
     * replaced.
     *
     * <p>Method setPointsSpent coded on 260907, commented in full on 261011.
     *
     * @param pointsSpent the points-spent map to store
     */
    public static void setPointsSpent(Map<Stats, Integer> pointsSpent) {
        if (!inited) initPlayerBirthStateRegistry();
        PlayerBirthStateRegistry.pointsSpent = pointsSpent;
    }

    /**
     * The cost of the next point-buy point on every stat, keyed by {@link Stats} - the port of
     * the whole-array form of reading C's {@code points_inc} ({@code player-birth.c}), the
     * shape {@code reset_stats}, {@code generate_stats}, {@code buy_stat} and {@code sell_stat}
     * use when they take the array by reference.
     *
     * <p>The map returned is the live one, not a copy, mutated in place by the same {@link
     * PlayerBirth} methods that mutate {@link #getPointsSpent()}'s map. {@code buyStat} checks the
     * cost it prices against this map before it spends.
     *
     * <p>Creates all three per-stat maps on first call if they do not yet exist - the {@code
     * inited} test, where {@link #getPointsInc(Stats)} tests whether its own map is {@code null};
     * see the class Javadoc.
     *
     * <p>Method getPointsInc coded on 260907, commented in full on 261011.
     *
     * @return the live points-inc map, never {@code null}
     */
    public static Map<Stats, Integer> getPointsInc() {
        if (!inited) initPlayerBirthStateRegistry();
        return pointsInc;
    }

    /**
     * Writes the cost of the next point-buy point on every stat, replacing the map wholesale -
     * the port of the whole-array form of writing C's {@code points_inc}, the shape a caller
     * like {@code reset_stats} or {@code generate_stats} uses when it fills the array afresh.
     * Nothing in {@code src/main} calls it.
     *
     * <p>Guards with the same {@code inited} check {@link #initPlayerBirthStateRegistry()} uses
     * before assigning - see {@link #setStats(Map)} for why. Only the {@code pointsInc} map is
     * replaced.
     *
     * <p>Method setPointsInc coded on 260907, commented in full on 261011.
     *
     * @param pointsInc the points-inc map to store
     */
    public static void setPointsInc(Map<Stats, Integer> pointsInc) {
        if (!inited) initPlayerBirthStateRegistry();
        PlayerBirthStateRegistry.pointsInc = pointsInc;
    }

    /**
     * The character snapshot kept for the birth screen's "flick between two rolls" undo - the port
     * of reading C's {@code prev} ({@code player-birth.c}).
     *
     * <p>Never answers {@code null}: a read before anything has been {@link #setPrev(Birther) set}
     * lazily backfills a fresh {@link Birther}, whose default {@code age} of zero is the same
     * sentinel C's own zero-initialized static gives - see the class Javadoc. {@link
     * PlayerBirth#doCmdPrevStats} tests that age to decide whether there is a roll to go back to,
     * {@link PlayerBirth#doCmdRollStats} saves into the object, and {@link
     * PlayerBirth#doCmdAcceptCharacter} clears its cached history.
     *
     * <p>Method getPrev coded on 260907, commented in full on 261011.
     *
     * @return the previous roll, never {@code null}
     */
    public static Birther getPrev() {
        if (prev == null) {
            prev = new Birther();
        }
        return prev;
    }

    /**
     * Writes the character snapshot kept for the birth screen's "flick between two rolls" undo -
     * the port of writing C's {@code prev = value}. C's {@code prev} is a struct that {@code
     * save_roller_data(&prev)} fills in place; the port's callers store the {@link Birther} that
     * {@link PlayerBirth#saveRollerData} or {@link PlayerBirth#LoadRollerData} returns, which is the
     * object they passed in, so the registry ends up holding the same instance as before.
     *
     * <p>Method setPrev coded on 260907, commented in full on 261011.
     *
     * @param prev the snapshot to store
     */
    public static void setPrev(Birther prev) {
        PlayerBirthStateRegistry.prev = prev;
    }

    /**
     * The character snapshot kept for restoring a save file's quickstart character - the port of
     * reading C's {@code quickstart_prev} ({@code player-birth.c}).
     *
     * <p>Never answers {@code null}, for the same reason {@link #getPrev()} does not - see the
     * class Javadoc. {@link PlayerBirth#doCmdBirthInit} reads this to hand straight to {@link
     * PlayerBirth#saveRollerData}, which writes every field of whatever it is given
     * unconditionally and with no null check of its own; without the lazy backfill here, the first
     * quickstart birth of a run would hand it {@code null} and throw. {@link
     * PlayerBirth#doCmdBirthReset} reads it to restore from, and {@link
     * PlayerBirth#doCmdAcceptCharacter} clears its cached history.
     *
     * <p>Method getQuickstartPrev coded on 260907, commented in full on 261011.
     *
     * @return the previous quickstart snapshot, never {@code null}
     */
    public static Birther getQuickstartPrev() {
        if (quickstartPrev == null) {
            quickstartPrev = new Birther();
        }
        return quickstartPrev;
    }

    /**
     * Writes the character snapshot kept for restoring a save file's quickstart character - the
     * port of writing C's {@code quickstart_prev = value}. {@link PlayerBirth#doCmdBirthInit} stores
     * the {@link Birther} that {@link PlayerBirth#saveRollerData} returns, which is the one it
     * passed in.
     *
     * <p>Method setQuickstartPrev coded on 260907, commented in full on 261011.
     *
     * @param prev the snapshot to store
     */
    public static void setQuickstartPrev(Birther prev) {
        PlayerBirthStateRegistry.quickstartPrev = prev;
    }

    /**
     * Creates the three per-stat maps this registry holds - {@code stats}, {@code pointsSpent}
     * and {@code pointsInc} - fresh and empty, the first time this is ever called. Every later
     * call is a no-op, guarded by {@code inited}: once created, the three maps are never
     * recreated, which matches C more closely than always recreating them would - {@code stats},
     * {@code points_spent} and {@code points_inc} are file-scope arrays zeroed once by the loader
     * and never re-zeroed, the same array identity for the whole life of the process. That also
     * means calling this at the start of every birth, as {@link PlayerBirth#doCmdBirthInit} does,
     * does not wipe a previous run's values; C does not wipe them either.
     *
     * <p>Every other accessor on the three maps calls this itself when it finds them missing - see
     * {@link #getStat(Stats)} and {@link #getStats()} for the two ways of testing that.
     *
     * <p>Method initPlayerBirthStateRegistry coded on 260907, commented in full on 261011.
     */
    public static void initPlayerBirthStateRegistry() {
        if (inited) return;
        stats = new HashMap<>();
        pointsSpent = new HashMap<>();
        pointsInc = new HashMap<>();
        inited = true;
    }

    /**
     * The stat's current point-buy value - the port of reading C's {@code stats[stat]} ({@code
     * player-birth.c}).
     *
     * <p>Answers {@code -1} for a stat that has never been {@link #setStat(Stats, int) set},
     * matching {@link Stats#STAT_NONE}'s own value rather than C's implicit zero; see the class
     * Javadoc for why that never diverges from C in practice. Creates the maps first if the
     * {@code stats} map is still {@code null}. Nothing in {@code src/main} calls it.
     *
     * <p>Method getStat coded on 260907, commented in full on 261011.
     *
     * @param stat the stat to read
     * @return the stat's current value, or {@code -1} if it has not been set
     */
    public static int getStat(Stats stat) {
        if (stats == null) initPlayerBirthStateRegistry();
        return stats.getOrDefault(stat, -1);
    }

    /**
     * Writes the stat's current point-buy value - the port of writing C's {@code stats[stat] =
     * value}. Creates the maps first if the {@code stats} map is still {@code null}. Nothing in
     * {@code src/main} calls it.
     *
     * <p>Method setStat coded on 260907, commented in full on 261011.
     *
     * @param stat  the stat to write
     * @param value the value to store
     */
    public static void setStat(Stats stat, int value) {
        if (stats == null) initPlayerBirthStateRegistry();
        stats.put(stat, value);
    }

    /**
     * The points spent raising a stat so far - the port of reading C's {@code
     * points_spent[stat]} ({@code player-birth.c}).
     *
     * <p>Answers {@code -1} for a stat with nothing recorded yet, the same unset sentinel {@link
     * #getStat(Stats)} uses, in place of C's implicit zero; see the class Javadoc. Creates the
     * maps first if the {@code pointsSpent} map is still {@code null}. Nothing in {@code src/main}
     * calls it.
     *
     * <p>Method getPointsSpent coded on 260907, commented in full on 261011.
     *
     * @param stat the stat to read
     * @return the points spent on that stat, or {@code -1} if none have been recorded
     */
    public static int getPointsSpent(Stats stat) {
        if (pointsSpent == null) initPlayerBirthStateRegistry();
        return pointsSpent.getOrDefault(stat, -1);
    }

    /**
     * Writes the points spent raising a stat so far - the port of writing C's {@code
     * points_spent[stat] = value}. Creates the maps first if the {@code pointsSpent} map is still
     * {@code null}. {@link PlayerBirth#doCmdRollStats} calls it to zero each real stat.
     *
     * <p>Method setPointsSpent coded on 260907, commented in full on 261011.
     *
     * @param stat  the stat to write
     * @param value the points spent to store
     */
    public static void setPointsSpent(Stats stat, int value) {
        if (pointsSpent == null) initPlayerBirthStateRegistry();
        pointsSpent.put(stat, value);
    }

    /**
     * The cost of the next point-buy point on a stat - the port of reading C's {@code
     * points_inc[stat]} ({@code player-birth.c}).
     *
     * <p>Answers {@code -1} for a stat with no cost recorded yet, the same unset sentinel {@link
     * #getStat(Stats)} uses, in place of C's implicit zero; see the class Javadoc. Creates the
     * maps first if the {@code pointsInc} map is still {@code null}. Nothing in {@code src/main}
     * calls it.
     *
     * <p>Method getPointsInc coded on 260907, commented in full on 261011.
     *
     * @param stat the stat to read
     * @return the cost of that stat's next point, or {@code -1} if none has been recorded
     */
    public static int getPointsInc(Stats stat) {
        if (pointsInc == null) initPlayerBirthStateRegistry();
        return pointsInc.getOrDefault(stat, -1);
    }

    /**
     * Writes the cost of the next point-buy point on a stat - the port of writing C's {@code
     * points_inc[stat] = value}. Creates the maps first if the {@code pointsInc} map is still
     * {@code null}. {@link PlayerBirth#doCmdRollStats} calls it to zero each real stat.
     *
     * <p>Method setPointsInc coded on 260907, commented in full on 261011.
     *
     * @param stat  the stat to write
     * @param value the cost to store
     */
    public static void setPointsInc(Stats stat, int value) {
        if (pointsInc == null) initPlayerBirthStateRegistry();
        pointsInc.put(stat, value);
    }

    /**
     * The point-buy points not yet spent on any stat - the port of reading C's {@code
     * points_left} ({@code player-birth.c}).
     *
     * <p>Unlike the three per-stat maps, this is a plain field with no lazy creation to guard:
     * an {@code int} defaults to {@code 0} the same way C's file-scope {@code points_left} does,
     * so there is nothing here for {@link #initPlayerBirthStateRegistry()} to do. C passes {@code
     * &points_left} to {@code reset_stats} and {@code generate_stats} and they write through it;
     * the port's equivalents return the new total, so each caller reads this, passes the value in
     * and stores the result with {@link #setPointsLeft(int)}.
     *
     * <p>Method getPointsLeft coded on 260907, commented in full on 261011.
     *
     * @return the points left unspent
     */
    public static int getPointsLeft() {
        return pointsLeft;
    }

    /**
     * Writes the point-buy points not yet spent on any stat - the port of writing C's {@code
     * points_left = value}. This is where the totals {@link PlayerBirth#resetStats}, {@link
     * PlayerBirth#generateStats} and the buy and sell handlers return end up, standing in for C's
     * write through the pointer.
     *
     * <p>Method setPointsLeft coded on 260907, commented in full on 261011.
     *
     * @param pointsLeft the points left unspent to store
     */
    public static void setPointsLeft(int pointsLeft) {
        PlayerBirthStateRegistry.pointsLeft = pointsLeft;
    }

    /**
     * Whether the previously-saved roller data is eligible for quickstart - the port of reading
     * C's {@code quickstart_allowed} ({@code player-birth.c}).
     *
     * <p>Defaults to {@code false}, matching C's implicit zero-initialized {@code bool}. {@link
     * PlayerBirth#doCmdBirthInit} sets it and signals it to the UI, and {@link
     * PlayerBirth#doCmdBirthReset} passes it to {@code doBirthReset} to decide whether to restore
     * the quickstart character.
     *
     * <p>Method isQuickstartAllowed coded on 260907, commented in full on 261011.
     *
     * @return {@code true} if quickstart is currently allowed
     */
    public static boolean isQuickstartAllowed() {
        return quickstartAllowed;
    }

    /**
     * Writes whether the previously-saved roller data is eligible for quickstart - the port of
     * writing C's {@code quickstart_allowed = value}. Only {@link PlayerBirth#doCmdBirthInit}
     * calls it: {@code true} when the player carries a quickstart character, {@code false} when
     * birth starts from the first race and class.
     *
     * <p>Method setQuickstartAllowed coded on 260907, commented in full on 261011.
     *
     * @param quickstartAllowed the new quickstart-eligibility flag
     */
    public static void setQuickstartAllowed(boolean quickstartAllowed) {
        PlayerBirthStateRegistry.quickstartAllowed = quickstartAllowed;
    }

    /**
     * Whether the current stats came from the dice roller rather than point-buy - the port of
     * reading C's {@code rolled_stats} ({@code player-birth.c}). While this is {@code true},
     * C's {@code do_cmd_buy_stat}, {@code do_cmd_sell_stat} and {@code do_cmd_refresh_stats} all
     * become no-ops, since a rolled character has no point-buy budget to spend; the port's {@link
     * PlayerBirth#doCmdBuyStat}, {@link PlayerBirth#doCmdSellStat} and {@link
     * PlayerBirth#doCmdRefreshStats} test it the same way.
     *
     * <p>Defaults to {@code false}, matching C's own explicit {@code static bool rolled_stats =
     * false} - the one field of the six given an initializer rather than left to implicit zero.
     *
     * <p>Method isRolledStats coded on 260907, commented in full on 261011.
     *
     * @return {@code true} if the stats currently held were rolled rather than bought
     */
    public static boolean isRolledStats() {
        return rolledStats;
    }

    /**
     * Writes whether the current stats came from the dice roller rather than point-buy - the
     * port of writing C's {@code rolled_stats = value}. {@link PlayerBirth#doCmdRollStats} sets it
     * {@code true}; the reset, race, class and reset-stats handlers set it {@code false}.
     *
     * <p>Method setRolledStats coded on 260907, commented in full on 261011.
     *
     * @param rolledStats the new rolled-vs-bought flag
     */
    public static void setRolledStats(boolean rolledStats) {
        PlayerBirthStateRegistry.rolledStats = rolledStats;
    }
}
