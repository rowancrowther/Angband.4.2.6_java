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

public class PlayerEventStatusUpdate {
    private static PlayerStatusView cachedPlayerStatusView;

    /*
     * Seeds {@link #playerStatusView} with an all-default snapshot at class load, before any
     * {@code Player} exists to read values from. C needs no equivalent step: its {@code player}
     * global is zero-initialised static storage from program start, so a stray {@code prt_*} call
     * before birth simply reads zeros and null strings; this block reproduces that "nothing has
     * happened yet" state explicitly, one {@code 0}/{@code null}/{@code false} per
     * {@link PlayerStatusView} field in declaration order, so {@link #getPlayerStatusView()}
     * always has a value to hand back. The two stat arrays are sized {@code 5}, matching C's
     * {@code STAT_MAX} ({@code player.h:37}).
     *
     * <p>Static initializer coded before 260912, commented in full on 260912.
     */
    static {
        int[] currentStats = {0, 0, 0, 0, 0};
        int[] maxStats = {0, 0, 0, 0, 0};
        String[] statString = {"STR", "INT", "WIS", "DEX", "CON"};
        cachedPlayerStatusView = new PlayerStatusView(null, null, null,
                null, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, currentStats,
                maxStats, statString, 0, 0, 0, false,
                false, false, false,
                false, false, false,
                false, false, false,
                0, null, null, null,
                null, null, null,
                0);
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
    public static void updatePlayerStatusStatsString(String[] value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
    public static void updatePlayerStatusBodyCount(int value) {
        cachedPlayerStatusView = new PlayerStatusView(cachedPlayerStatusView.name(),
                cachedPlayerStatusView.title(),
                cachedPlayerStatusView.raceName(),
                cachedPlayerStatusView.className(),
                cachedPlayerStatusView.level(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                value,
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                value,
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                value,
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                value,
                cachedPlayerStatusView.lightLevel(),
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                value,
                cachedPlayerStatusView.equipmentSlotCount());
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
                cachedPlayerStatusView.bodyCount(),
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
                cachedPlayerStatusView.studyStatus(),
                cachedPlayerStatusView.studyConditions(),
                cachedPlayerStatusView.detectionStatus(),
                cachedPlayerStatusView.restingRepeatingState(),
                cachedPlayerStatusView.levelFeeling(),
                cachedPlayerStatusView.lightLevel(),
                value);
    }
}
