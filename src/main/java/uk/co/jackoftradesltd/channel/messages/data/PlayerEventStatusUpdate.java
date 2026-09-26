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

package uk.co.jackoftradesltd.channel.messages.data;

/**
 * The UI thread's cached snapshot of the player-status sidebar and the character sheet — the
 * two-channel migration's stand-in for C's live reads of the {@code player}/{@code cave} globals
 * inside the {@code prt_*} family in {@code ui-display.c} and the {@code display_panel} family in
 * {@code ui-player.c}. C has no cached copy at all: every line is repainted by reading the
 * current global state directly at redraw time, so there is nothing there resembling
 * {@link #cachedPlayerStatusView}/{@link #cachedPlayerCharSheetView} to keep in sync. This class
 * exists because the UI thread on this side of the boundary cannot reach across and read those
 * globals itself; the core instead pushes each value across as it changes, through the
 * {@code updatePlayerStatus*}/{@code updatePlayerCharSheet*} field setters below, and the UI
 * thread reads back the accumulated snapshots through {@link #getPlayerStatusView()}/
 * {@link #getPlayerCharSheetView()}.
 *
 * <p>Two caches, split because the fields they hold answer to different C call sites: the sidebar
 * ({@code prt_*}, {@code ui-display.c}) versus the still-unported character-sheet panels
 * ({@code display_panel}, {@code ui-player.c}). Each cache offers the same two ways to update
 * it — {@link #updatePlayerStatusView}/{@link #updatePlayerCharSheetView} replace the whole
 * record wholesale, while every {@code updatePlayerStatus*}/{@code updatePlayerCharSheet*} field
 * setter below rebuilds the snapshot from the current one with exactly one field changed, since
 * both {@link PlayerStatusView} and {@link PlayerCharSheetView} are immutable records with no
 * in-place field write available, unlike C's direct mutation of the live {@code player} struct.
 * The static initializer seeds an all-default snapshot of both caches at class load, before any
 * {@code Player} exists to read values from; C needs no equivalent step, since its {@code player}
 * global is zero-initialised static storage from program start.
 *
 * <p>Class PlayerEventStatusUpdate coded before 260912, commented in full on 260925.
 */
public class PlayerEventStatusUpdate {
    /**
     * The current player-status-sidebar snapshot — replaced wholesale by
     * {@link #updatePlayerStatusView} or rebuilt field-by-field by the
     * {@code updatePlayerStatus*} setters below; see the class Javadoc above for why this cache
     * exists in Java where C keeps none. Never observably {@code null}: the static initializer
     * below seeds it before any other code can run, so every read through
     * {@link #getPlayerStatusView()} sees at least the all-default snapshot.
     *
     * <p>Field cachedPlayerStatusView coded before 260912, commented in full on 260915.
     */
    private static PlayerStatusView cachedPlayerStatusView;

    /**
     * The current character-sheet snapshot — replaced wholesale by
     * {@link #updatePlayerCharSheetView} or rebuilt field-by-field by the
     * {@code updatePlayerCharSheet*} setters below; the {@link PlayerCharSheetView} counterpart to
     * {@link #cachedPlayerStatusView}, split out because its fields answer to C's
     * {@code display_panel} family in {@code ui-player.c} rather than the {@code prt_*} sidebar
     * family. Never observably {@code null}: the static initializer below seeds it before any
     * other code can run, so every read through {@link #getPlayerCharSheetView()} sees at least
     * the all-default snapshot.
     *
     * <p>Field cachedPlayerCharSheetView coded on 260925, commented in full on 260925.
     */
    private static PlayerCharSheetView cachedPlayerCharSheetView;

    /*
     * Seeds {@link #cachedPlayerStatusView} and {@link #cachedPlayerCharSheetView} with an
     * all-default snapshot at class load, before any {@code Player} exists to read values from. C
     * needs no equivalent step: its {@code player} global is zero-initialised static storage from
     * program start, so a stray {@code prt_*}/{@code display_panel} call before birth simply reads
     * zeros and null strings; this block reproduces that "nothing has happened yet" state
     * explicitly, one {@code 0}/{@code null}/{@code false} per field in declaration order, so
     * {@link #getPlayerStatusView()}/{@link #getPlayerCharSheetView()} always have a value to hand
     * back. The stat arrays are sized {@code 5}, matching C's {@code STAT_MAX}
     * ({@code player.h:37}).
     *
     * <p>Static initializer coded before 260912, commented in full on 260925.
     */
    static {
        int[] currentStats = {0, 0, 0, 0, 0};
        int[] maxStats = {0, 0, 0, 0, 0};
        String[] statString = {"STR", "INT", "WIS", "DEX", "CON"};
        cachedPlayerStatusView = new PlayerStatusView(null, null, null,
                null, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, currentStats,
                maxStats, statString, 0, 0, false,
                false, false, false,
                false, false, false,
                false, false, false,
                0, 0, null, null, null,
                null, null, null,
                0, 0, 0, 0);
        cachedPlayerCharSheetView = new PlayerCharSheetView(0, true,
                new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0},
                new int[]{0, 0, 0, 0, 0}, new int[]{0, 0, 0, 0, 0}, 0, 0,
                new long[]{}, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Replaces the whole {@link #cachedPlayerStatusView} snapshot wholesale, rather than rebuilding it
     * field-by-field like the {@code updatePlayerStatus*} family below. C has no equivalent: the
     * sidebar is always painted live from the {@code player}/{@code cave} globals via the
     * {@code prt_*} functions in {@code ui-display.c}, so there is nothing there resembling a
     * cached, wholesale-replaceable snapshot.
     *
     * <p>Method updatecachedPlayerStatusView coded before 260912, commented in full on 260912.
     *
     * @param cachedPlayerStatusView the new snapshot to publish on the UI channel
     */
    public static void updatePlayerStatusView(PlayerStatusView cachedPlayerStatusView) {
        PlayerEventStatusUpdate.cachedPlayerStatusView = cachedPlayerStatusView;
    }

    /**
     * The current player-status snapshot most recently published to the UI channel — see
     * {@link #updatePlayerStatusView} and the {@code updatePlayerStatus*} field setters below.
     *
     * <p>Method getcachedPlayerStatusView coded before 260912, commented in full on 260912.
     *
     * @return the current {@link cachedPlayerStatusView} snapshot
     */
    public static PlayerStatusView getPlayerStatusView() {
        return cachedPlayerStatusView;
    }

    /**
     * Replaces the whole {@link #cachedPlayerCharSheetView} snapshot wholesale, rather than
     * rebuilding it field-by-field like the {@code updatePlayerCharSheet*} family below — the
     * {@link PlayerCharSheetView} counterpart to {@link #updatePlayerStatusView}. C has no
     * equivalent: the character sheet is always painted live from the {@code player} globals via
     * the {@code display_panel} family in {@code ui-player.c}, so there is nothing there
     * resembling a cached, wholesale-replaceable snapshot.
     *
     * <p>Method updatePlayerCharSheetView coded on 260925, commented in full on 260925.
     *
     * @param cachedPlayerCharSheetView the new snapshot to publish on the UI channel
     */
    public static void updatePlayerCharSheetView(PlayerCharSheetView cachedPlayerCharSheetView) {
        PlayerEventStatusUpdate.cachedPlayerCharSheetView = cachedPlayerCharSheetView;
    }

    /**
     * The current character-sheet snapshot most recently published to the UI channel — see
     * {@link #updatePlayerCharSheetView} and the {@code updatePlayerCharSheet*} field setters
     * below.
     *
     * <p>Method getPlayerCharSheetView coded on 260925, commented in full on 260925.
     *
     * @return the current {@link #cachedPlayerCharSheetView} snapshot
     */
    public static PlayerCharSheetView getPlayerCharSheetView() {
        return cachedPlayerCharSheetView;
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new player name, leaving every other field
     * untouched — the Java-side equivalent of C repainting a single sidebar line in place.
     * {@link cachedPlayerStatusView} is an immutable record, so unlike C, which mutates the live
     * {@code player} struct and simply reprints the affected field, changing one value here
     * means building a whole new instance from the current one, field by field.
     *
     * <p>The player's name has no dedicated sidebar line in {@code ui-display.c}; it is C's
     * {@code player->full_name}, shown on the character screen rather than the sidebar that the
     * rest of this family of methods otherwise mirrors.
     *
     * <p>Method updatePlayerStatusPlayerName coded before 260912, commented in full on 260912.
     *
     * @param newPlayerName the player's name to store in the rebuilt view
     */
    public static void updatePlayerStatusPlayerName(String newPlayerName) {
        cachedPlayerStatusView = new PlayerStatusView(newPlayerName,
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new title, leaving every other field untouched
     * — the port of C's {@code prt_title} ({@code ui-display.c:195}), which formats
     * {@code player->class->title[...]} (or the wizard/winner/shapechange overrides from
     * {@code fmt_title}) and reprints it in place.
     *
     * <p>Method updatePlayerStatusPlayerTitle coded before 260912, commented in full on 260912.
     *
     * @param newPlayerTitle the title text to store in the rebuilt view
     */
    public static void updatePlayerStatusPlayerTitle(String newPlayerTitle) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                newPlayerTitle,
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new race name, leaving every other field
     * untouched — the port of C's {@code prt_race} ({@code ui-display.c:553}).
     *
     * <p>Method updatePlayerStatusRaceName coded before 260912, commented in full on 260912.
     *
     * @param value the race name to store in the rebuilt view
     */
    public static void updatePlayerStatusRaceName(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                value,
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new class name, leaving every other field
     * untouched — the port of C's {@code prt_class} ({@code ui-display.c:576}).
     *
     * <p>Method updatePlayerStatusClassName coded before 260912, commented in full on 260912.
     *
     * @param value the class name to store in the rebuilt view
     */
    public static void updatePlayerStatusClassName(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                value,
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new character level, leaving every other field
     * untouched — the port of C's {@code prt_level} ({@code ui-display.c:207}), which reads
     * {@code player->lev} directly.
     *
     * <p>Method updatePlayerStatusLevel coded before 260912, commented in full on 260912.
     *
     * @param value the character level to store in the rebuilt view
     */
    public static void updatePlayerStatusLevel(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                value,
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    public static void updatePlayerStatusMaxLevel(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                value,
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new experience total, leaving every other field
     * untouched — the port of C's {@code prt_exp} ({@code ui-display.c:226}), which reads
     * {@code player->exp} (or the XP-to-next-level figure derived from it, below level 50).
     *
     * <p>Method updatePlayerStatusExperience coded before 260912, commented in full on 260912.
     *
     * @param value the experience total to store in the rebuilt view
     */
    public static void updatePlayerStatusExperience(long value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                value,
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new maximum-experience total, leaving every
     * other field untouched — the port of C's {@code player->max_exp}, the threshold
     * {@code prt_exp} ({@code ui-display.c:226}) compares against to decide whether to print
     * {@code "Exp"}/{@code "Nxt"} (still climbing) or {@code "EXP"}/{@code "NXT"} (at a new
     * high).
     *
     * <p>Method updatePlayerStatusmaxExperience coded before 260912, commented in full on
     * 260912.
     *
     * @param value the maximum experience total to store in the rebuilt view
     */
    public static void updatePlayerStatusMaxExperience(long value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                value,
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new gold total, leaving every other field
     * untouched — the port of C's {@code prt_gold} ({@code ui-display.c:256}), which reads
     * {@code player->au}.
     *
     * <p>Method updatePlayerStatusGold coded before 260912, commented in full on 260912.
     *
     * @param value the gold total to store in the rebuilt view
     */
    public static void updatePlayerStatusGold(long value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                value,
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new current-hit-point total, leaving every
     * other field untouched — the port of C's {@code prt_hp} ({@code ui-display.c:314}), which
     * reads {@code player->chp}.
     *
     * <p>Method updatePlayerStatusCurrentHP coded before 260912, commented in full on 260912.
     *
     * @param value the current hit points to store in the rebuilt view
     */
    public static void updatePlayerStatusCurrentHP(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                value,
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new maximum-hit-point total, leaving every
     * other field untouched — the port of C's {@code prt_hp} ({@code ui-display.c:314}), which
     * reads {@code player->mhp}.
     *
     * <p>Method updatePlayerStatusMaxHP coded before 260912, commented in full on 260912.
     *
     * @param value the maximum hit points to store in the rebuilt view
     */
    public static void updatePlayerStatusMaxHP(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                value,
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new current-spell-point total, leaving every
     * other field untouched — the port of C's {@code prt_sp} ({@code ui-display.c:332}), which
     * reads {@code player->csp}.
     *
     * <p>Method updatePlayerStatusCurrentSP coded before 260912, commented in full on 260912.
     *
     * @param value the current spell points to store in the rebuilt view
     */
    public static void updatePlayerStatusCurrentSP(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                value,
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new maximum-spell-point total, leaving every
     * other field untouched — the port of C's {@code prt_sp} ({@code ui-display.c:332}), which
     * reads {@code player->msp}.
     *
     * <p>Method updatePlayerStatusMaxSP coded before 260912, commented in full on 260912.
     *
     * @param value the maximum spell points to store in the rebuilt view
     */
    public static void updatePlayerStatusMaxSP(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                value,
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new armour class, leaving every other field
     * untouched — the port of C's {@code prt_ac} ({@code ui-display.c:301}), which reads
     * {@code player->known_state.ac + player->known_state.to_a}.
     *
     * <p>Method updatePlayerStatusArmourClass coded before 260912, commented in full on 260912.
     *
     * @param value the armour class to store in the rebuilt view
     */
    public static void updatePlayerStatusArmourClass(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                value,
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new speed, leaving every other field untouched
     * — the port of C's {@code prt_speed}/{@code prt_speed_aux} ({@code ui-display.c:508, 475}),
     * which reads {@code player->state.speed} (110 is normal).
     *
     * <p>Method updatePlayerStatusSpeed coded before 260912, commented in full on 260912.
     *
     * @param value the speed to store in the rebuilt view
     */
    public static void updatePlayerStatusSpeed(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                value,
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with new current stat values, leaving every other
     * field untouched — the port of C's {@code prt_stat} ({@code ui-display.c:153}), which
     * reads {@code player->state.stat_use[stat]} for each of the five stats in one pass per
     * stat.
     *
     * <p>Method updatePlayerStatusCurrentStats coded before 260912, commented in full on
     * 260912.
     *
     * @param value the current stat values to store in the rebuilt view
     */
    public static void updatePlayerStatusCurrentStats(int[] value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                value,
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with new maximum stat values, leaving every other
     * field untouched — the port of C's {@code prt_stat} ({@code ui-display.c:153}), which
     * compares {@code player->stat_cur[stat]} against {@code player->stat_max[stat]} to decide
     * whether a stat is shown injured (reduced name, yellow) or healthy (full name, green).
     *
     * <p>Method updatePlayerStatusMaxStats coded before 260912, commented in full on 260912.
     *
     * @param value the maximum stat values to store in the rebuilt view
     */
    public static void updatePlayerStatusMaxStats(int[] value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                value,
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with new stat-label strings, leaving every other
     * field untouched — the port of C's {@code stat_names} array ({@code ui-display.c:99-102}),
     * the abbreviation each stat row is printed under in {@code prt_stat}
     * ({@code ui-display.c:153}). {@code stat_names} is fixed schema data, the same for every
     * character ({@code "STR"}, {@code "INT"}, {@code "WIS"}, {@code "DEX"}, {@code "CON"}, as
     * seeded by the static initializer above and mirrored in
     * {@link uk.co.jackoftradesltd.middle.enums.Stats#getStatString()}), not a per-player value
     * — unlike the numeric stat arrays this method sits beside.
     *
     * <p>C additionally holds {@code stat_names_reduced} ({@code ui-display.c:107-110}), the
     * lowercase form {@code prt_stat} swaps in when a stat is injured
     * ({@code stat_cur[stat] < stat_max[stat]}); this snapshot carries only the healthy-form
     * labels, so choosing between the two forms (or colouring by injury) is left to whatever
     * renders {@link #cachedPlayerStatusView}, from {@link cachedPlayerStatusView}.
     *
     * <p>Method updatePlayerStatusStatsString coded before 260912, commented in full on 260915.
     *
     * @param value the stat-label strings to store in the rebuilt view
     */
    public static void updatePlayerStatusStatsString(String[] value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                value,
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with a new body-part count, leaving every other
     * field untouched — the port of C's {@code player->body.count}, as read by
     * {@code configure_char_sheet}/{@code have_valid_char_sheet_config} ({@code src/ui-player.c})
     * to size the character screen's resistance-panel column count
     * ({@code res_nlabel + 1 + body.count}).
     *
     * <p>This is a separate cached field from {@link PlayerStatusView#equipmentSlotCount()} even
     * though both ultimately trace back to the same C value: that field mirrors
     * {@code player->body.count} as read by {@code prt_equippy} ({@code ui-display.c:269}) for
     * the sidebar's equippy-char loop, a different call site the C original reads live from the
     * same global. This snapshot design has no single live global to read from, so each call
     * site's reading is cached and updated independently.
     *
     * <p>Method updatePlayerCharSheetBodyCount coded on 260925, commented in full on 260925.
     *
     * @param value the body-part count to store in the rebuilt view
     */
    public static void updatePlayerCharSheetBodyCount(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(value,
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with the tracked monster's current hit points, leaving
     * every other field untouched — the port of C's {@code prt_health_aux}
     * ({@code ui-display.c:425}), which reads {@code mon->hp} for the monster at
     * {@code player->upkeep->health_who}.
     *
     * <p>Method updatePlayerStatusMonsterHealth coded before 260912, commented in full on
     * 260912.
     *
     * @param value the tracked monster's current hit points to store in the rebuilt view
     */
    public static void updatePlayerStatusMonsterHealth(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                value,
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with the tracked monster's maximum hit points, leaving
     * every other field untouched — the port of C's {@code prt_health_aux}
     * ({@code ui-display.c:425}), which reads {@code mon->maxhp} to compute the health-bar
     * percentage.
     *
     * <p>Method updatePlayerStatusMaxMonsterHealth coded before 260912, commented in full on
     * 260912.
     *
     * @param value the tracked monster's maximum hit points to store in the rebuilt view
     */
    public static void updatePlayerStatusMaxMonsterHealth(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                value,
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is currently visible,
     * leaving every other field untouched — the port of C's {@code prt_health_aux}
     * ({@code ui-display.c:425}), which calls {@code monster_is_visible(mon)} to decide between
     * the real health bar and the "unknown" {@code [----------]} placeholder.
     *
     * <p>Method updatePlayerStatusMonsterVisible coded before 260912, commented in full on
     * 260912.
     *
     * @param value {@code true} if the tracked monster is visible, {@code false} otherwise
     */
    public static void updatePlayerStatusMonsterVisible(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                value,
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the player is hallucinating, leaving
     * every other field untouched — the port of C's {@code player->timed[TMD_IMAGE]} check in
     * {@code prt_health_aux} ({@code ui-display.c:425}), which forces the "unknown" health-bar
     * placeholder while hallucinating even for a visible, healthy monster.
     *
     * <p>Method updatePlayerStatusPlayerHallucinating coded before 260912, commented in full on
     * 260912.
     *
     * @param value {@code true} if the player is hallucinating, {@code false} otherwise
     */
    public static void updatePlayerStatusPlayerHallucinating(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                value,
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether a monster is currently being tracked,
     * leaving every other field untouched — the port of C's {@code player->upkeep->health_who}
     * null check in {@code prt_health_aux} ({@code ui-display.c:425}), which clears the health
     * bar entirely when nothing is tracked.
     *
     * <p>Method updatePlayerStatusMonsterTracked coded before 260912, commented in full on
     * 260912.
     *
     * @param value {@code true} if a monster is being tracked, {@code false} otherwise
     */
    public static void updatePlayerStatusMonsterTracked(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                value,
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is afraid, leaving
     * every other field untouched — the port of C's {@code mon->m_timed[MON_TMD_FEAR]} check in
     * {@code monster_health_attr} ({@code ui-display.c:365}), which colours the health bar
     * violet while it holds.
     *
     * <p>Method updatePlayerStatusMonTmdFear coded before 260912, commented in full on 260912.
     *
     * @param value {@code true} if the tracked monster is afraid, {@code false} otherwise
     */
    public static void updatePlayerStatusMonTmdFear(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                value,
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is disenchanted,
     * leaving every other field untouched — the port of C's
     * {@code mon->m_timed[MON_TMD_DISEN]} check in {@code monster_health_attr}
     * ({@code ui-display.c:365}), which colours the health bar light umber while it holds.
     *
     * <p>Method updatePlayerStatusMonTmdDisen coded before 260912, commented in full on 260912.
     *
     * @param value {@code true} if the tracked monster is disenchanted, {@code false} otherwise
     */
    public static void updatePlayerStatusMonTmdDisen(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                value,
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is under command,
     * leaving every other field untouched — the port of C's
     * {@code mon->m_timed[MON_TMD_COMMAND]} check in {@code monster_health_attr}
     * ({@code ui-display.c:365}), which colours the health bar light purple while it holds.
     *
     * <p>Method updatePlayerStatusMonTmdCommand coded before 260912, commented in full on
     * 260912.
     *
     * @param value {@code true} if the tracked monster is under command, {@code false}
     *              otherwise
     */
    public static void updatePlayerStatusMonTmdCommand(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                value,
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is confused, leaving
     * every other field untouched — the port of C's {@code mon->m_timed[MON_TMD_CONF]} check in
     * {@code monster_health_attr} ({@code ui-display.c:365}), which colours the health bar
     * umber while it holds.
     *
     * <p>Method updatePlayerStatusMonTmdConf coded before 260912, commented in full on 260912.
     *
     * @param value {@code true} if the tracked monster is confused, {@code false} otherwise
     */
    public static void updatePlayerStatusMonTmdConf(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                value,
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is stunned, leaving
     * every other field untouched — the port of C's {@code mon->m_timed[MON_TMD_STUN]} check in
     * {@code monster_health_attr} ({@code ui-display.c:365}), which colours the health bar
     * light blue while it holds.
     *
     * <p>Method updatePlayerStatusMonTmdStun coded before 260912, commented in full on 260912.
     *
     * @param value {@code true} if the tracked monster is stunned, {@code false} otherwise
     */
    public static void updatePlayerStatusMonTmdStun(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                value,
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is asleep, leaving
     * every other field untouched — the port of C's {@code mon->m_timed[MON_TMD_SLEEP]} check
     * in {@code monster_health_attr} ({@code ui-display.c:365}), which colours the health bar
     * blue while it holds.
     *
     * <p>Method updatePlayerStatusMonTmdSleep coded before 260912, commented in full on 260912.
     *
     * @param value {@code true} if the tracked monster is asleep, {@code false} otherwise
     */
    public static void updatePlayerStatusMonTmdSleep(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                value,
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with whether the tracked monster is held, leaving
     * every other field untouched — the port of C's {@code mon->m_timed[MON_TMD_HOLD]} check in
     * {@code monster_health_attr} ({@code ui-display.c:365}), which colours the health bar blue
     * while it holds (the same colour as asleep — C does not distinguish the two on the bar).
     *
     * <p>Method updatePlayerStatusMonTmdHold coded before 260912, commented in full on 260912.
     *
     * @param value {@code true} if the tracked monster is held, {@code false} otherwise
     */
    public static void updatePlayerStatusMonTmdHold(boolean value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                value,
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new dungeon depth, leaving every other field
     * untouched — the port of C's {@code prt_depth} ({@code ui-display.c:532}), which formats
     * {@code player->depth} via {@code fmt_depth}.
     *
     * <p>Method updatePlayerStatusDepth coded before 260912, commented in full on 260912.
     *
     * @param value the dungeon depth to store in the rebuilt view
     */
    public static void updatePlayerStatusDepth(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                value,
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new dungeon depth, leaving every other field
     * untouched — the port of C's {@code prt_depth} ({@code ui-display.c:532}), which formats
     * {@code player->depth} via {@code fmt_depth}.
     *
     * <p>Method updatePlayerStatusDepth coded before 260912, commented in full on 260912.
     *
     * @param value the dungeon depth to store in the rebuilt view
     */
    public static void updatePlayerStatusMaxDepth(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                value,
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new study-status message, leaving every other
     * field untouched — the port of C's {@code prt_study} ({@code ui-display.c:1226}), which
     * formats {@code "Study (%d)"} from {@code player->upkeep->new_spells} when the player can
     * learn a new spell.
     *
     * <p>Method updatePlayerStatusStudyStatus coded before 260912, commented in full on 260912.
     *
     * @param value the study-status message to store in the rebuilt view
     */
    public static void updatePlayerStatusStudyStatus(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                value,
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new study-conditions message, leaving every
     * other field untouched — the port of C's {@code player_book_has_unlearned_spells} check in
     * {@code prt_study} ({@code ui-display.c:1226}), which dims the study message when the
     * player is not carrying a book with spells left to learn.
     *
     * <p>Method updatePlayerStatusStudyConditions coded before 260912, commented in full on
     * 260912.
     *
     * @param value the study-conditions message to store in the rebuilt view
     */
    public static void updatePlayerStatusStudyConditions(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                value,
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new detection-status message, leaving every
     * other field untouched — the port of C's {@code prt_dtrap} ({@code ui-display.c:1207}),
     * which prints {@code "DTrap"} (yellow on the border of a trap-detected area, green within
     * it) while the player's grid has been trap-detected.
     *
     * <p>Method updatePlayerStatusDetectionStatus coded before 260912, commented in full on
     * 260912.
     *
     * @param value the detection-status message to store in the rebuilt view
     */
    public static void updatePlayerStatusDetectionStatus(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                value,
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new resting/repeat-status message, leaving
     * every other field untouched — the port of C's {@code prt_state} ({@code ui-display.c:957}),
     * which prints {@code "Rest"} with a turn count while resting, or {@code "Repeat"} with a
     * count while a command is being repeated.
     *
     * <p>Method updatePlayerStatusRestingRepeatStatus coded before 260912, commented in full on
     * 260912.
     *
     * @param value the resting/repeat-status message to store in the rebuilt view
     */
    public static void updatePlayerStatusRestingRepeatStatus(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                value,
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new level-feeling message, leaving every other
     * field untouched — the port of C's {@code prt_level_feeling} ({@code ui-display.c:1053}),
     * which formats {@code "LF:"} followed by the monster and object feelings derived from
     * {@code cave->feeling}.
     *
     * <p>Method updatePlayerStatusLevelFeeling coded before 260912, commented in full on
     * 260912.
     *
     * @param value the level-feeling message to store in the rebuilt view
     */
    public static void updatePlayerStatusLevelFeeling(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                value,
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new light-level message, leaving every other
     * field untouched — the port of C's {@code prt_light} ({@code ui-display.c:1129}), which
     * formats {@code "Light %d"} from {@code square_light(cave, player->grid)}.
     *
     * <p>Method updatePlayerStatusLightLevel coded before 260912, commented in full on 260912.
     *
     * @param value the light-level message to store in the rebuilt view
     */
    public static void updatePlayerStatusLightLevel(String value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                value,
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    /**
     * Rebuilds {@link #cachedPlayerStatusView} with a new equipment-slot count, leaving every other
     * field untouched — the port of C's {@code prt_equippy} ({@code ui-display.c:269}), which
     * loops {@code player->body.count} times to draw one equippy character per slot.
     *
     * <p>Method updatePlayerStatusEquipSlotCount coded before 260912, commented in full on
     * 260912.
     *
     * @param value the equipment-slot count to store in the rebuilt view
     */
    public static void updatePlayerStatusEquipSlotCount(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                value,
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    public static void updatePlayerStatusTurn(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                value,
                cachedPlayerStatusView.totalEnergy(),
                cachedPlayerStatusView.restingTurn());
    }

    public static void updatePlayerStatusTotalEnergy(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                value,
                cachedPlayerStatusView.restingTurn());
    }

    public static void updatePlayerStatusRestingEnergy(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
                cachedPlayerStatusView.maxLevel(),
                cachedPlayerStatusView.experience(),
                cachedPlayerStatusView.maxExperience(),
                cachedPlayerStatusView.gold(),
                cachedPlayerStatusView.chp(),
                cachedPlayerStatusView.mhp(),
                cachedPlayerStatusView.csp(),
                cachedPlayerStatusView.msp(),
                cachedPlayerStatusView.armourClass(),
                cachedPlayerStatusView.speed(),
                cachedPlayerStatusView.currentStats(),
                cachedPlayerStatusView.maxStats(),
                cachedPlayerStatusView.statString(),
                cachedPlayerStatusView.monsterHealth(),
                cachedPlayerStatusView.maxMonsterHealth(),
                cachedPlayerStatusView.monsterVisible(),
                cachedPlayerStatusView.playerHallucinating(),
                cachedPlayerStatusView.monsterTracked(),
                cachedPlayerStatusView.monsterTmdFear(),
                cachedPlayerStatusView.monsterTmdDisen(),
                cachedPlayerStatusView.monsterTmdCommand(),
                cachedPlayerStatusView.monsterTmdConf(),
                cachedPlayerStatusView.monsterTmdStun(),
                cachedPlayerStatusView.monsterTmdSleep(),
                cachedPlayerStatusView.monsterTmdHold(),
                cachedPlayerStatusView.depth(),
                cachedPlayerStatusView.maxDepth(),
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount(),
                cachedPlayerStatusView.turn(),
                cachedPlayerStatusView.totalEnergy(),
                value);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with whether the player is actively in a live
     * game, leaving every other field untouched — the port of C's
     * {@code player->upkeep->playing} check in {@code display_player} ({@code src/ui-player.c}),
     * which skips repainting the character screen in a background sub-window once play has
     * ended, while the foreground window (C's {@code angband_term[0]}) keeps repainting
     * regardless.
     *
     * <p>Method updatePlayerCharSheetIsPlaying coded on 260925, commented in full on 260925.
     *
     * @param value {@code true} if the player is actively in a live game, {@code false} otherwise
     */
    public static void updatePlayerCharSheetIsPlaying(boolean value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                value,
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with new racial stat-bonus values, leaving
     * every other field untouched — the port of the "Race Bonus" (RB) column in C's
     * {@code display_player_stat_info} ({@code src/ui-player.c}), which reads
     * {@code player->race->r_adj[stat]} for each of the five stats.
     *
     * <p>Method updatePlayerCharSheetRaceStatBonuses coded on 260925, commented in full on
     * 260925.
     *
     * @param value the five racial stat-bonus values to store in the rebuilt view
     */
    public static void updatePlayerCharSheetRaceStatBonuses(int[] value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                value,
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with new class stat-bonus values, leaving every
     * other field untouched — the port of the "Class Bonus" (CB) column in C's
     * {@code display_player_stat_info} ({@code src/ui-player.c}), which reads
     * {@code player->class->c_adj[stat]} for each of the five stats.
     *
     * <p>Method updatePlayerCharSheetClassStatBonuses coded on 260925, commented in full on
     * 260925.
     *
     * @param value the five class stat-bonus values to store in the rebuilt view
     */
    public static void updatePlayerCharSheetClassStatBonuses(int[] value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                value,
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with new equipment stat-bonus values, leaving
     * every other field untouched — the port of the "Equipment Bonus" (EB) column in C's
     * {@code display_player_stat_info} ({@code src/ui-player.c}), which reads
     * {@code player->state.stat_add[stat]} for each of the five stats.
     *
     * <p>Method updatePlayerCharSheetEquipStatBonuses coded on 260925, commented in full on
     * 260925.
     *
     * @param value the five equipment stat-bonus values to store in the rebuilt view
     */
    public static void updatePlayerCharSheetEquipStatBonuses(int[] value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                value,
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with new resulting-maximum stat values, leaving
     * every other field untouched — the port of the "Best" column in C's
     * {@code display_player_stat_info} ({@code src/ui-player.c}), which reads
     * {@code player->state.stat_top[stat]}, the natural maximum after racial, class and
     * equipment bonuses are applied, for each of the five stats.
     *
     * <p>Method updatePlayerCharSheetTotalStatBonuses coded on 260925, commented in full on
     * 260925.
     *
     * @param value the five resulting-maximum stat values to store in the rebuilt view
     */
    public static void updatePlayerCharSheetTotalStatBonuses(int[] value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                value,
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with new current (drained) stat values, leaving
     * every other field untouched — the port of C's {@code player->state.stat_use[stat]} read in
     * {@code display_player_stat_info} ({@code src/ui-player.c}), which prints this value only
     * for a stat currently below its maximum
     * ({@code player->stat_cur[stat] < player->stat_max[stat]}).
     *
     * <p>Despite the method name, this sets {@link #cachedPlayerCharSheetView}'s
     * {@code playerCurrModStat} component, not a bonus value — C's own value here is the drained
     * stat reading itself, not a bonus, unlike this method's four siblings above
     * ({@link #updatePlayerCharSheetRaceStatBonuses}, {@link #updatePlayerCharSheetClassStatBonuses},
     * {@link #updatePlayerCharSheetEquipStatBonuses}, {@link #updatePlayerCharSheetTotalStatBonuses}),
     * each of which is named after the field it sets.
     *
     * <p>Method updatePlayerCharSheetCurrentStatBonuses coded on 260925, commented in full on
     * 260925.
     *
     * @param value the five current (drained) stat values to store in the rebuilt view
     */
    public static void updatePlayerCharSheetCurrentStatBonuses(int[] value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                value,
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    /**
     * Rebuilds {@link #cachedPlayerCharSheetView} with a new total carried weight, leaving every
     * other field untouched — the port of C's {@code player->upkeep->total_weight}
     * ({@code player.h:487}), read by the "Burden" line in {@code get_panel_midleft}
     * ({@code src/ui-player.c}). The Java side reads the same value via
     * {@code Player.getPlayerUpkeep().getTotalWeight()}.
     *
     * <p>Method updatePlayerCharSheetTotalWeight coded on 260925, commented in full on 260925.
     *
     * @param value the total carried weight, in tenth-pounds, to store in the rebuilt view
     */
    public static void updatePlayerCharSheetTotalWeight(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                value,
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetWeightLimit(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                value,
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetExpToLevel(long[] value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                value,
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetExpFactor(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                value,
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetHeight(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                value,
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetWeight(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                value,
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetAge(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                value,
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetToA(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                value,
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetToD(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                value,
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetToH(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                value,
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetMeleeSkill(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                value,
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetShootSkill(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                value,
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetBthPlusAdj(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                value,
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetMeleeDice(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                value,
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetMeleeSides(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                value,
                cachedPlayerCharSheetView.numBlows(),
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }


    public static void updatePlayerCharSheetnumBlows(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                value,
                cachedPlayerCharSheetView.numShots(), 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }

    public static void updatePlayerCharSheetNumShots(int value) {
        cachedPlayerCharSheetView = new PlayerCharSheetView(cachedPlayerCharSheetView.bodyCount(),
                cachedPlayerCharSheetView.playerIsPlaying(),
                cachedPlayerCharSheetView.playerRaceStatBonuses(),
                cachedPlayerCharSheetView.playerClassStatBonuses(),
                cachedPlayerCharSheetView.playerEquipStatBonuses(),
                cachedPlayerCharSheetView.playerTotalStatBonuses(),
                cachedPlayerCharSheetView.playerCurrModStat(),
                cachedPlayerCharSheetView.totalWeight(),
                cachedPlayerCharSheetView.weightLimit(),
                cachedPlayerCharSheetView.expToLevel(),
                cachedPlayerCharSheetView.expFactor(),
                cachedPlayerCharSheetView.height(),
                cachedPlayerCharSheetView.weight(),
                cachedPlayerCharSheetView.age(),
                cachedPlayerCharSheetView.toA(),
                cachedPlayerCharSheetView.toD(),
                cachedPlayerCharSheetView.toH(),
                cachedPlayerCharSheetView.meleeSkill(),
                cachedPlayerCharSheetView.shootSkill(),
                cachedPlayerCharSheetView.bthPlusAdj(),
                cachedPlayerCharSheetView.meleeDice(),
                cachedPlayerCharSheetView.meleeSides(),
                cachedPlayerCharSheetView.numBlows(),
                value, 0, 0, 0, 0,
                0, 0, 0, 0, false);
    }
}
