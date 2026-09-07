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
 * statics at {@code player-birth.c:118-138} ({@code stats}, {@code points_spent}, {@code
 * points_inc}, {@code points_left}, {@code quickstart_allowed}, {@code rolled_stats}, {@code prev}
 * and {@code quickstart_prev}). C's own comment above the first block reads "All of these should be
 * in some kind of 'birth state' struct somewhere else" ({@code player-birth.c:112-115}); this class
 * is that struct, given a name and a home, extended to the two {@link Birther} snapshots C declares
 * a little further down the same file.
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
 * character or not", {@code player-birth.c:127-129}) - so the port's lazily-created instance and
 * C's zero-initialised static answer that check identically, even though one exists from process
 * start and the other only from first read.
 *
 * <p>The class is static state, as the C globals it replaces are: nothing here is ported from a
 * function, and the methods below are plain accessors rather than translations of control flow.
 *
 * <p>Outstanding: {@link PlayerBirth#doCmdBirthInit} is the only caller so far, and only reaches
 * {@link #initPlayerBirthStateRegistry()}, {@link #getQuickstartPrev()}, {@link
 * #setQuickstartPrev(Birther)}, {@link #isQuickstartAllowed()} and {@link
 * #setQuickstartAllowed(boolean)} - the stat maps, {@code pointsLeft}, {@code rolledStats} and
 * {@code prev} are all still unread and unwritten anywhere else in {@code src/main}.
 *
 * <p>Class PlayerBirthStateRegistry coded on 260907, commented in full on 260907.
 *
 * @author Rowan Crowther
 */
public class PlayerBirthStateRegistry {
    /**
     * Whether the three per-stat maps below have been created yet - see {@link
     * #initPlayerBirthStateRegistry()}. Has no C equivalent: C's file-scope arrays are zeroed
     * once by the loader and always exist, so nothing there needs a "have I initialised this"
     * flag.
     *
     * <p>Field inited coded on 260907, commented in full on 260907.
     */
    private static boolean inited = false;

    /**
     * The point-buy value for every stat, keyed by {@link Stats} - the port of C's {@code
     * stats[STAT_MAX]} ({@code player-birth.c:118}). See {@link #getStat(Stats)}/{@link
     * #setStat(Stats, int)} and {@link #getStats()}/{@link #setStats(Map)}.
     *
     * <p>Field stats coded on 260907, commented in full on 260907.
     */
    private static Map<Stats, Integer> stats;

    /**
     * The points spent raising every stat so far, keyed by {@link Stats} - the port of C's
     * {@code points_spent[STAT_MAX]} ({@code player-birth.c:119}). See {@link
     * #getPointsSpent(Stats)}/{@link #setPointsSpent(Stats, int)} and {@link
     * #getPointsSpent()}/{@link #setPointsSpent(Map)}.
     *
     * <p>Field pointsSpent coded on 260907, commented in full on 260907.
     */
    private static Map<Stats, Integer> pointsSpent;

    /**
     * The cost of the next point-buy point on every stat, keyed by {@link Stats} - the port of
     * C's {@code points_inc[STAT_MAX]} ({@code player-birth.c:120}). See {@link
     * #getPointsInc(Stats)}/{@link #setPointsInc(Stats, int)} and {@link #getPointsInc()}/{@link
     * #setPointsInc(Map)}.
     *
     * <p>Field pointsInc coded on 260907, commented in full on 260907.
     */
    private static Map<Stats, Integer> pointsInc;

    /**
     * The point-buy points not yet spent on any stat - the port of C's {@code points_left}
     * ({@code player-birth.c:121}). See {@link #getPointsLeft()}/{@link #setPointsLeft(int)}.
     *
     * <p>Field pointsLeft coded on 260907, commented in full on 260907.
     */
    private static int pointsLeft;

    /**
     * Whether the previously-saved roller data is eligible for quickstart - the port of C's
     * {@code quickstart_allowed} ({@code player-birth.c:123}). See {@link
     * #isQuickstartAllowed()}/{@link #setQuickstartAllowed(boolean)}.
     *
     * <p>Field quickstartAllowed coded on 260907, commented in full on 260907.
     */
    private static boolean quickstartAllowed;

    /**
     * Whether the current stats came from the dice roller rather than point-buy - the port of
     * C's {@code rolled_stats = false} ({@code player-birth.c:124}), the one field of the six C
     * initialises explicitly rather than leaving to implicit zero. See {@link
     * #isRolledStats()}/{@link #setRolledStats(boolean)}.
     *
     * <p>Field rolledStats coded on 260907, commented in full on 260907.
     */
    private static boolean rolledStats = false;

    /**
     * The character snapshot kept for the birth screen's "flick between two rolls" undo - the
     * port of C's {@code prev} ({@code player-birth.c:131}). See {@link #getPrev()}/{@link
     * #setPrev(Birther)}.
     *
     * <p>Field prev coded on 260907, commented in full on 260907.
     */
    private static Birther prev;

    /**
     * The character snapshot kept for restoring a save file's quickstart character - the port of
     * C's {@code quickstart_prev} ({@code player-birth.c:138}). See {@link
     * #getQuickstartPrev()}/{@link #setQuickstartPrev(Birther)}.
     *
     * <p>Field quickstartPrev coded on 260907, commented in full on 260907.
     */
    private static Birther quickstartPrev;

    /**
     * The point-buy value for every stat, keyed by {@link Stats} - the port of the whole-array
     * form of reading C's {@code stats} ({@code player-birth.c:118}), the shape callers like
     * {@code reset_stats}, {@code generate_stats} and {@code get_stats} use when they take the
     * array by reference rather than one stat at a time ({@code player-birth.c:715, 829, 1181}).
     *
     * <p>Lazily creates all three per-stat maps on first call, the same guard {@link
     * #getStat(Stats)} uses - see the class Javadoc.
     *
     * <p>Method getStats coded on 260907, commented in full on 260907.
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
     * or {@code generate_stats} uses when it fills the array afresh ({@code player-birth.c:715,
     * 829}).
     *
     * <p>Guards with the same {@code inited} check {@link #initPlayerBirthStateRegistry()} uses
     * before assigning, so a set called before anything else has touched the three maps still
     * counts as the maps' creation - without this, a later getter's own lazy-init check would
     * still see {@code inited == false} and silently recreate all three maps, discarding
     * whatever was just stored here.
     *
     * <p>Method setStats coded on 260907, commented in full on 260907.
     *
     * @param stats the stats map to store
     */
    public static void setStats(Map<Stats, Integer> stats) {
        if (!inited) initPlayerBirthStateRegistry();
        PlayerBirthStateRegistry.stats = stats;
    }

    /**
     * The points spent raising every stat so far, keyed by {@link Stats} - the port of the
     * whole-array form of reading C's {@code points_spent} ({@code player-birth.c:119}), the
     * shape {@code reset_stats}, {@code generate_stats}, {@code buy_stat} and {@code sell_stat}
     * use when they take the array by reference ({@code player-birth.c:715, 829, 1137, 1148}).
     *
     * <p>Lazily creates all three per-stat maps on first call, the same guard {@link
     * #getPointsSpent(Stats)} uses - see the class Javadoc.
     *
     * <p>Method getPointsSpent coded on 260907, commented in full on 260907.
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
     * {@code reset_stats} or {@code generate_stats} uses when it fills the array afresh ({@code
     * player-birth.c:715, 829}).
     *
     * <p>Guards with the same {@code inited} check {@link #initPlayerBirthStateRegistry()} uses
     * before assigning - see {@link #setStats(Map)} for why.
     *
     * <p>Method setPointsSpent coded on 260907, commented in full on 260907.
     *
     * @param pointsSpent the points-spent map to store
     */
    public static void setPointsSpent(Map<Stats, Integer> pointsSpent) {
        if (!inited) initPlayerBirthStateRegistry();
        PlayerBirthStateRegistry.pointsSpent = pointsSpent;
    }

    /**
     * The cost of the next point-buy point on every stat, keyed by {@link Stats} - the port of
     * the whole-array form of reading C's {@code points_inc} ({@code player-birth.c:120}), the
     * shape {@code reset_stats}, {@code generate_stats}, {@code buy_stat} and {@code sell_stat}
     * use when they take the array by reference ({@code player-birth.c:715, 829, 1137, 1148}).
     *
     * <p>Lazily creates all three per-stat maps on first call, the same guard {@link
     * #getPointsInc(Stats)} uses - see the class Javadoc.
     *
     * <p>Method getPointsInc coded on 260907, commented in full on 260907.
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
     * like {@code reset_stats} or {@code generate_stats} uses when it fills the array afresh
     * ({@code player-birth.c:715, 829}).
     *
     * <p>Guards with the same {@code inited} check {@link #initPlayerBirthStateRegistry()} uses
     * before assigning - see {@link #setStats(Map)} for why.
     *
     * <p>Method setPointsInc coded on 260907, commented in full on 260907.
     *
     * @param pointsInc the points-inc map to store
     */
    public static void setPointsInc(Map<Stats, Integer> pointsInc) {
        if (!inited) initPlayerBirthStateRegistry();
        PlayerBirthStateRegistry.pointsInc = pointsInc;
    }

    /**
     * The character snapshot kept for the birth screen's "flick between two rolls" undo - the port
     * of reading C's {@code prev} ({@code player-birth.c:131}).
     *
     * <p>Never answers {@code null}: a read before anything has been {@link #setPrev(Birther) set}
     * lazily backfills a fresh {@link Birther}, whose default {@code age} of zero is the same
     * sentinel C's own zero-initialised static gives - see the class Javadoc.
     *
     * <p>Method getPrev coded on 260907, commented in full on 260907.
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
     * the port of writing C's {@code prev = value} ({@code player-birth.c:131}).
     *
     * <p>Method setPrev coded on 260907, commented in full on 260907.
     *
     * @param prev the snapshot to store
     */
    public static void setPrev(Birther prev) {
        PlayerBirthStateRegistry.prev = prev;
    }

    /**
     * The character snapshot kept for restoring a save file's quickstart character - the port of
     * reading C's {@code quickstart_prev} ({@code player-birth.c:138}).
     *
     * <p>Never answers {@code null}, for the same reason {@link #getPrev()} does not - see the
     * class Javadoc. {@link PlayerBirth#doCmdBirthInit} is the one caller so far, reading this to
     * hand straight to {@link PlayerBirth#saveRollerData}, which writes every field of whatever it
     * is given unconditionally and with no null check of its own; without the lazy backfill here,
     * the first quickstart birth of a run would hand it {@code null} and throw.
     *
     * <p>Method getQuickstartPrev coded on 260907, commented in full on 260907.
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
     * port of writing C's {@code quickstart_prev = value} ({@code player-birth.c:138}).
     *
     * <p>Method setQuickstartPrev coded on 260907, commented in full on 260907.
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
     * {@code points_spent} and {@code points_inc} are file-scope arrays ({@code
     * player-birth.c:118-120}) zeroed once by the loader and never re-zeroed, the same array
     * identity for the whole life of the process.
     *
     * <p>Every other accessor on the three maps calls this itself, the first time it is asked
     * for a stat before any has been set - see {@link #getStat(Stats)}.
     *
     * <p>Method initPlayerBirthStateRegistry coded on 260907, commented in full on 260907.
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
     * player-birth.c:118}).
     *
     * <p>Answers {@code -1} for a stat that has never been {@link #setStat(Stats, int) set},
     * matching {@link Stats#STAT_NONE}'s own value rather than C's implicit zero; see the class
     * Javadoc for why that never diverges from C in practice.
     *
     * <p>Method getStat coded on 260907, commented in full on 260907.
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
     * value} ({@code player-birth.c:118}).
     *
     * <p>Method setStat coded on 260907, commented in full on 260907.
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
     * points_spent[stat]} ({@code player-birth.c:119}).
     *
     * <p>Answers {@code -1} for a stat with nothing recorded yet, the same unset sentinel {@link
     * #getStat(Stats)} uses, in place of C's implicit zero; see the class Javadoc.
     *
     * <p>Method getPointsSpent coded on 260907, commented in full on 260907.
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
     * points_spent[stat] = value} ({@code player-birth.c:119}).
     *
     * <p>Method setPointsSpent coded on 260907, commented in full on 260907.
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
     * points_inc[stat]} ({@code player-birth.c:120}).
     *
     * <p>Answers {@code -1} for a stat with no cost recorded yet, the same unset sentinel {@link
     * #getStat(Stats)} uses, in place of C's implicit zero; see the class Javadoc.
     *
     * <p>Method getPointsInc coded on 260907, commented in full on 260907.
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
     * points_inc[stat] = value} ({@code player-birth.c:120}).
     *
     * <p>Method setPointsInc coded on 260907, commented in full on 260907.
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
     * points_left} ({@code player-birth.c:121}).
     *
     * <p>Unlike the three per-stat maps, this is a plain field with no lazy creation to guard:
     * an {@code int} defaults to {@code 0} the same way C's file-scope {@code points_left} does,
     * so there is nothing here for {@link #initPlayerBirthStateRegistry()} to do.
     *
     * <p>Method getPointsLeft coded on 260907, commented in full on 260907.
     *
     * @return the points left unspent
     */
    public static int getPointsLeft() {
        return pointsLeft;
    }

    /**
     * Writes the point-buy points not yet spent on any stat - the port of writing C's {@code
     * points_left = value} ({@code player-birth.c:121}).
     *
     * <p>Method setPointsLeft coded on 260907, commented in full on 260907.
     *
     * @param pointsLeft the points left unspent to store
     */
    public static void setPointsLeft(int pointsLeft) {
        PlayerBirthStateRegistry.pointsLeft = pointsLeft;
    }

    /**
     * Whether the previously-saved roller data is eligible for quickstart - the port of reading
     * C's {@code quickstart_allowed} ({@code player-birth.c:123}).
     *
     * <p>Defaults to {@code false}, matching C's implicit zero-initialised {@code bool}.
     *
     * <p>Method isQuickstartAllowed coded on 260907, commented in full on 260907.
     *
     * @return {@code true} if quickstart is currently allowed
     */
    public static boolean isQuickstartAllowed() {
        return quickstartAllowed;
    }

    /**
     * Writes whether the previously-saved roller data is eligible for quickstart - the port of
     * writing C's {@code quickstart_allowed = value} ({@code player-birth.c:123}).
     *
     * <p>Method setQuickstartAllowed coded on 260907, commented in full on 260907.
     *
     * @param quickstartAllowed the new quickstart-eligibility flag
     */
    public static void setQuickstartAllowed(boolean quickstartAllowed) {
        PlayerBirthStateRegistry.quickstartAllowed = quickstartAllowed;
    }

    /**
     * Whether the current stats came from the dice roller rather than point-buy - the port of
     * reading C's {@code rolled_stats} ({@code player-birth.c:124}). While this is {@code true},
     * C's {@code do_cmd_buy_stat}, {@code do_cmd_sell_stat} and {@code do_cmd_refresh_stats}
     * ({@code player-birth.c:1132, 1143, 1168}) all become no-ops, since a rolled character has
     * no point-buy budget to spend.
     *
     * <p>Defaults to {@code false}, matching C's own explicit {@code static bool rolled_stats =
     * false} - the one field of the six given an initialiser rather than left to implicit zero.
     *
     * <p>Method isRolledStats coded on 260907, commented in full on 260907.
     *
     * @return {@code true} if the stats currently held were rolled rather than bought
     */
    public static boolean isRolledStats() {
        return rolledStats;
    }

    /**
     * Writes whether the current stats came from the dice roller rather than point-buy - the
     * port of writing C's {@code rolled_stats = value} ({@code player-birth.c:124}).
     *
     * <p>Method setRolledStats coded on 260907, commented in full on 260907.
     *
     * @param rolledStats the new rolled-vs-bought flag
     */
    public static void setRolledStats(boolean rolledStats) {
        PlayerBirthStateRegistry.rolledStats = rolledStats;
    }
}
