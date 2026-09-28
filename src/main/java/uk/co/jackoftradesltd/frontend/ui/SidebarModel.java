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

package uk.co.jackoftradesltd.frontend.ui;

/**
 * The UI-owned model for the character sidebar's hit-point row — the front end's replacement for
 * reading {@code player->chp}/{@code mhp} straight off the shared player global, the way C's
 * {@code prt_hp} ({@code [C] ui-display.c}, function {@code prt_hp}) and {@code get_panel_topleft}
 * ({@code [C] ui-player.c}, function {@code get_panel_topleft}) both do at their own call time.
 *
 * <p>Written only by
 * {@link RedrawRouter#setHP(uk.co.jackoftradesltd.channel.messages.data.GameEventData)}, on the UI
 * thread that drains
 * {@code uiChannel}, and read only by the drawing code that runs on that same thread — see
 * {@code docs/implementation/260926_change_in_architecture_from_cache_to_messages.md}'s "the UI
 * thread owns the models" conclusion. Nothing running on the event dispatch thread may touch this
 * class; the EDT only ever sees the painted {@code Screen} snapshot, never the model itself.
 *
 * <p>Named for what it, and its siblings still to be written, replace field by field:
 * {@link uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate}'s static cache is
 * being architected out in their favour. Today this class holds the HP and SP pairs, the player's
 * level and maximum level, title, race name, class name, full name, wizard and total-winner flags,
 * shape name and shapechanged status, current and displayed experience, and gold; the rest of C's
 * {@code prt_*} family in {@code [C] ui-display.c} join it as their own {@code EVENT_*} payloads
 * are ported.
 *
 * <p>The setters are package-private and the getters public, so only a class in this package —
 * today, only {@link RedrawRouter} — can write, while any caller may read.
 *
 * <p>Class SidebarModel coded on 260926, commented in full on 260928.
 *
 * @author Rowan Crowther
 */
public class SidebarModel {
    /**
     * The player's current hit points, C's {@code player->chp}, written each time an
     * {@code EVENT_HP} message is routed and read whenever the sidebar's HP row is drawn.
     *
     * <p>Field currentHP coded on 260926, commented in full on 260926.
     */
    private static int currentHP;

    /**
     * The player's maximum hit points, C's {@code player->mhp}, written each time an
     * {@code EVENT_HP} message is routed and read whenever the sidebar's HP row is drawn.
     *
     * <p>Field maxHP coded on 260926, commented in full on 260926.
     */
    private static int maxHP;

    /**
     * The player's current spell points, C's {@code player->csp}, written each time an
     * {@code EVENT_MANA} message is routed and read whenever the sidebar's SP row is drawn.
     *
     * <p>Field currentSP coded on 260926, commented in full on 260926.
     */
    private static int currentSP;

    /**
     * The player's maximum spell points, C's {@code player->msp}, written each time an
     * {@code EVENT_MANA} message is routed and read whenever the sidebar's SP row is drawn.
     *
     * <p>Field maxSP coded on 260926, commented in full on 260926.
     */
    private static int maxSP;

    /**
     * The player's title, C's {@code show_title()}, written each time an {@code EVENT_PLAYERTITLE}
     * message is routed and read whenever the character sheet's Title row is drawn.
     *
     * <p>Field title coded before 260926, commented in full on 260926.
     */
    private static String title;

    /**
     * The player's class name, C's {@code player->class->name}, written each time an
     * {@code EVENT_RACE_CLASS} message is routed and read whenever the character sheet's Class row
     * is drawn.
     *
     * <p>Field className coded before 260926, commented in full on 260926.
     */
    private static String className;

    /**
     * The player's race name, C's {@code player->race->name}, written each time an
     * {@code EVENT_RACE_CLASS} message is routed and read whenever the character sheet's Race row
     * is drawn.
     *
     * <p>Field raceName coded before 260926, commented in full on 260926.
     */
    private static String raceName;

    /**
     * The player's full name, C's {@code player->full_name}, written each time an
     * {@code EVENT_PLAYER_NAME} message is routed and read whenever the character sheet's Name row
     * is drawn.
     *
     * <p>Field name coded before 260926, commented in full on 260926.
     */
    private static String name;

    /**
     * Whether the player is in wizard mode, C's {@code player->wizard}, written each time an
     * {@code EVENT_PLAYERTITLE} message is routed and read whenever the sidebar's title row is
     * drawn.
     *
     * <p>Field wizard coded on 260927, commented in full on 260927.
     */
    private static boolean wizard;

    /**
     * Whether the player has won the game, C's {@code player->total_winner}, written each time an
     * {@code EVENT_PLAYERTITLE} message is routed and read whenever the sidebar's title row is
     * drawn.
     *
     * <p>Field isTotalWinner coded on 260927, commented in full on 260927.
     */
    private static boolean isTotalWinner;

    /**
     * The name of the player's current shape, C's {@code player->shape->name}, written each time an
     * {@code EVENT_PLAYERTITLE} message is routed and read whenever the sidebar's title row is
     * drawn.
     *
     * <p>Field shapeName coded on 260927, commented in full on 260927.
     */
    private static String shapeName;

    /**
     * The player's current character level, C's {@code player->lev}, written each time an
     * {@code EVENT_PLAYERLEVEL} message is routed and read whenever the sidebar's level row is
     * drawn.
     *
     * <p>Field level coded on 260927, commented in full on 260927.
     */
    private static int level;

    /**
     * The player's highest character level yet attained, C's {@code player->max_lev}, written each
     * time an {@code EVENT_PLAYERLEVEL} message is routed and read whenever the sidebar's level row
     * is drawn.
     *
     * <p>Field maxLevel coded on 260927, commented in full on 260927.
     */
    private static int maxLevel;

    /**
     * The player's current experience total, C's {@code player->exp}, written each time an
     * {@code EVENT_EXPERIENCE} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataLongStat} is routed and read
     * whenever the sidebar's experience row is drawn.
     *
     * <p>Field experience coded on 260927, commented in full on 260928.
     */
    private static long experience;

    /**
     * The figure the sidebar's experience row prints, C's local {@code xp} in {@code prt_exp}
     * ({@code [C] ui-display.c}) - the experience needed to reach the next level, or the running
     * total once the character has reached level fifty. Written each time an
     * {@code EVENT_EXPERIENCE} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataLong} is routed; the choice
     * between the two figures is made core-side, in {@code PlayerCalcs.redrawStuff}'s
     * {@code PR_EXP} arm, not here.
     *
     * <p>Field xpToLevel coded on 260927, commented in full on 260928.
     */
    private static long xpToLevel;

    /**
     * The player's highest experience total yet held, C's {@code player->max_exp}, written each
     * time an {@code EVENT_EXPERIENCE} message carrying an
     * {@link uk.co.jackoftradesltd.channel.messages.data.EventDataLongStat} is routed and read
     * whenever the sidebar's experience row is drawn, to decide whether the row is coloured as
     * drained or full.
     *
     * <p>Field maxXp coded on 260927, commented in full on 260928.
     */
    private static long maxXp;
    /**
     * Whether the player is currently in a non-normal shape, C's {@code player_is_shapechanged}
     * ({@code [C] player-util.c}) — the port of what {@code prt_race} and {@code prt_class}
     * ({@code [C] ui-display.c}) each re-check against the live global at draw time, so that a
     * shapechanged player's race and class fields are blanked rather than shown.
     *
     * <p>Written by {@link RedrawRouter#setRaceClass} from the third element of the
     * {@code EVENT_RACE_CLASS} payload, which {@code PlayerCalcs.redrawStuff}'s {@code PR_MISC} arm
     * packs from {@code player.isShapeChanged()}. Read by
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtRace(int, int)} and
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtClass(int, int)} to
     * decide whether to blank those rows.
     *
     * <p>Field playerIsShapechanged coded on 260927, commented in full on 260927.
     */
    private static boolean playerIsShapechanged;

    /**
     * The player's current gold total, C's {@code player->au}, written each time an
     * {@code EVENT_GOLD} message is routed and read whenever the sidebar's gold row is drawn.
     *
     * <p>Field gold coded on 260927, commented in full on 260928.
     */
    private static long gold;

    /**
     * Read the gold total last written by {@link #setGold(long)}, for the sidebar's gold row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_gold} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getGold coded on 260927, commented in full on 260928.
     *
     * @return the player's current gold total, C's {@code player->au}
     */
    public static long getGold() {
        return gold;
    }

    /**
     * Write the gold total. Package-private, so only {@link RedrawRouter#setGold} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setGold coded on 260927, commented in full on 260928.
     *
     * @param gold the player's current gold total, C's {@code player->au}
     */
    static void setGold(long gold) {
        SidebarModel.gold = gold;
    }

    /**
     * Read the maximum experience last written by {@link #setMaxXp(long)}, for the sidebar's
     * experience row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_exp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getMaxXp coded on 260927, commented in full on 260928.
     *
     * @return the player's highest experience total yet held, C's {@code player->max_exp}
     */
    public static long getMaxXp() {
        return maxXp;
    }

    /**
     * Write the maximum experience. Package-private, so only {@link RedrawRouter#setExperience} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setMaxXp coded on 260927, commented in full on 260928.
     *
     * @param maxXp the player's highest experience total yet held, C's {@code player->max_exp}
     */
    static void setMaxXp(long maxXp) {
        SidebarModel.maxXp = maxXp;
    }

    /**
     * Read the displayed experience figure last written by {@link #setXpToLevel(long)}, for the
     * sidebar's experience row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_exp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getXpToLevel coded on 260927, commented in full on 260928.
     *
     * @return the experience needed to reach the next level, or the running total at level fifty,
     * C's local {@code xp} in {@code prt_exp}
     */
    public static long getXpToLevel() {
        return xpToLevel;
    }

    /**
     * Write the displayed experience figure. Package-private, so only
     * {@link RedrawRouter#setExperience} - the only class in this package today - can write the
     * model directly.
     *
     * <p>Method setXpToLevel coded on 260927, commented in full on 260928.
     *
     * @param xpToLevel the experience needed to reach the next level, or the running total at
     *                  level fifty
     */
    static void setXpToLevel(long xpToLevel) {
        SidebarModel.xpToLevel = xpToLevel;
    }

    /**
     * Read the current experience last written by {@link #setExperience(long)}, for the sidebar's
     * experience row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_exp} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getExperience coded on 260927, commented in full on 260928.
     *
     * @return the player's current experience total, C's {@code player->exp}
     */
    public static long getExperience() {
        return experience;
    }

    /**
     * Write the current experience. Package-private, so only {@link RedrawRouter#setExperience} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setExperience coded on 260927, commented in full on 260928.
     *
     * @param experience the player's current experience total, C's {@code player->exp}
     */
    static void setExperience(long experience) {
        SidebarModel.experience = experience;
    }

    /**
     * Read the maximum level last written by {@link #setMaxLevel(int)}, for the sidebar's level row
     * - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_level} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getMaxLevel coded on 260927, commented in full on 260927.
     *
     * @return the player's highest character level yet attained, C's {@code player->max_lev}
     */
    public static int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Write the maximum level. Package-private, so only {@link RedrawRouter#setPlayerLevel} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setMaxLevel coded on 260927, commented in full on 260927.
     *
     * @param maxLevel the player's highest character level yet attained, C's {@code player->max_lev}
     */
    static void setMaxLevel(int maxLevel) {
        SidebarModel.maxLevel = maxLevel;
    }

    /**
     * Read the current level last written by {@link #setLevel(int)}, for the sidebar's level row -
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code prt_level} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getLevel coded on 260927, commented in full on 260927.
     *
     * @return the player's current character level, C's {@code player->lev}
     */
    public static int getLevel() {
        return level;
    }

    /**
     * Write the current level. Package-private, so only {@link RedrawRouter#setPlayerLevel} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setLevel coded on 260927, commented in full on 260927.
     *
     * @param level the player's current character level, C's {@code player->lev}
     */
    static void setLevel(int level) {
        SidebarModel.level = level;
    }

    /**
     * Read the shape name last written by {@link #setShapeName(String)}, for the sidebar's title
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code fmt_title} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method getShapeName coded on 260927, commented in full on 260927.
     *
     * @return the name of the player's current shape, C's {@code player->shape->name}
     */
    public static String getShapeName() {
        return shapeName;
    }

    /**
     * Write the shape name. Package-private, so only {@link RedrawRouter#setTitle} - the only class
     * in this package today - can write the model directly.
     *
     * <p>Method setShapeName coded on 260927, commented in full on 260927.
     *
     * @param shapeName the name of the player's current shape, C's {@code player->shape->name}
     */
    static void setShapeName(String shapeName) {
        SidebarModel.shapeName = shapeName;
    }

    /**
     * Read the total-winner flag last written by {@link #setTotalWinner(boolean)}, for the
     * sidebar's title row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s
     * port of C's {@code fmt_title} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method isTotalWinner coded on 260927, commented in full on 260927.
     *
     * @return {@code true} once the player has won the game, C's {@code player->total_winner}
     */
    public static boolean isTotalWinner() {
        return isTotalWinner;
    }

    /**
     * Write the total-winner flag. Package-private, so only {@link RedrawRouter#setTitle} - the
     * only class in this package today - can write the model directly.
     *
     * <p>Method setTotalWinner coded on 260927, commented in full on 260927.
     *
     * @param totalWinner {@code true} once the player has won the game, C's
     *                    {@code player->total_winner}
     */
    static void setTotalWinner(boolean totalWinner) {
        isTotalWinner = totalWinner;
    }

    /**
     * Read the wizard-mode flag last written by {@link #setWizard(boolean)}, for the sidebar's title
     * row - {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder}'s port of C's
     * {@code fmt_title} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p>Method isWizard coded on 260927, commented in full on 260927.
     *
     * @return {@code true} while the player is in wizard mode, C's {@code player->wizard}
     */
    public static boolean isWizard() {
        return wizard;
    }
    
    /**
     * Write the wizard-mode flag. Package-private, so only {@link RedrawRouter#setTitle} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setWizard coded on 260927, commented in full on 260927.
     *
     * @param wizard {@code true} while the player is in wizard mode, C's {@code player->wizard}
     */
    static void setWizard(boolean wizard) {
        SidebarModel.wizard = wizard;
    }

    /**
     * Read the current hit-point value last written by {@link #setCurrentHP(int)}, for the
     * sidebar's HP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getCurrentHP coded on 260926, commented in full on 260926.
     *
     * @return the player's current hit points, C's {@code player->chp}
     */
    public static int getCurrentHP() {
        return currentHP;
    }

    /**
     * Write the current hit-point value. Package-private, so only {@link RedrawRouter#setHP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setCurrentHP coded on 260926, commented in full on 260926.
     *
     * @param currentHP the player's current hit points, C's {@code player->chp}
     */
    static void setCurrentHP(int currentHP) {
        SidebarModel.currentHP = currentHP;
    }

    /**
     * Read the maximum hit-point value last written by {@link #setMaxHP(int)}, for the sidebar's
     * HP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getMaxHP coded on 260926, commented in full on 260926.
     *
     * @return the player's maximum hit points, C's {@code player->mhp}
     */
    public static int getMaxHP() {
        return maxHP;
    }

    /**
     * Write the maximum hit-point value. Package-private, so only {@link RedrawRouter#setHP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setMaxHP coded on 260926, commented in full on 260926.
     *
     * @param maxHP the player's maximum hit points, C's {@code player->mhp}
     */
    static void setMaxHP(int maxHP) {
        SidebarModel.maxHP = maxHP;
    }

    /**
     * Read the current spell-point value last written by {@link #setCurrentSP(int)}, for the
     * sidebar's SP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getCurrentSP coded on 260926, commented in full on 260926.
     *
     * @return the player's current spell points, C's {@code player->csp}
     */
    public static int getCurrentSP() {
        return currentSP;
    }

    /**
     * Write the current spell-point value. Package-private, so only {@link RedrawRouter#setSP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setCurrentSP coded on 260926, commented in full on 260926.
     *
     * @param currentSP the player's current spell points, C's {@code player->csp}
     */
    static void setCurrentSP(int currentSP) {
        SidebarModel.currentSP = currentSP;
    }

    /**
     * Read the maximum spell-point value last written by {@link #setMaxSP(int)}, for the sidebar's
     * SP row — {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getMaxSP coded on 260926, commented in full on 260926.
     *
     * @return the player's maximum spell points, C's {@code player->msp}
     */
    public static int getMaxSP() {
        return maxSP;
    }

    /**
     * Write the maximum spell-point value. Package-private, so only {@link RedrawRouter#setSP} —
     * the only class in this package today — can write the model directly.
     *
     * <p>Method setMaxSP coded on 260926, commented in full on 260926.
     *
     * @param maxSP the player's maximum spell points, C's {@code player->msp}
     */
    static void setMaxSP(int maxSP) {
        SidebarModel.maxSP = maxSP;
    }

    /**
     * Read the class name last written by {@link #setClassName(String)}, for the character sheet's
     * Class row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getClassName coded before 260926, commented in full on 260926.
     *
     * @return the player's class name, C's {@code player->class->name}
     */
    public static String getClassName() {
        return className;
    }

    /**
     * Write the class name. Package-private, so only {@link RedrawRouter#setRaceClass} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setClassName coded before 260926, commented in full on 260926.
     *
     * @param className the player's class name, C's {@code player->class->name}
     */
    static void setClassName(String className) {
        SidebarModel.className = className;
    }

    /**
     * Read the full name last written by {@link #setName(String)}, for the character sheet's Name
     * row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getName coded before 260926, commented in full on 260926.
     *
     * @return the player's full name, C's {@code player->full_name}
     */
    public static String getName() {
        return name;
    }

    /**
     * Write the full name. Package-private, so only {@link RedrawRouter#setName} - the only class
     * in this package today - can write the model directly.
     *
     * <p>Method setName coded before 260926, commented in full on 260926.
     *
     * @param name the player's full name, C's {@code player->full_name}
     */
    static void setName(String name) {
        SidebarModel.name = name;
    }

    /**
     * Read the race name last written by {@link #setRaceName(String)}, for the character sheet's
     * Race row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getRaceName coded before 260926, commented in full on 260926.
     *
     * @return the player's race name, C's {@code player->race->name}
     */
    public static String getRaceName() {
        return raceName;
    }

    /**
     * Write the race name. Package-private, so only {@link RedrawRouter#setRaceClass} - the only
     * class in this package today - can write the model directly.
     *
     * <p>Method setRaceName coded before 260926, commented in full on 260926.
     *
     * @param raceName the player's race name, C's {@code player->race->name}
     */
    static void setRaceName(String raceName) {
        SidebarModel.raceName = raceName;
    }

    /**
     * Read the title last written by {@link #setTitle(String)}, for the character sheet's Title
     * row - {@link UIPlayer#getPanelTopLeft()}'s port of C's {@code get_panel_topleft}
     * ({@code [C] ui-player.c}) is today's only reader.
     *
     * <p>Method getTitle coded before 260926, commented in full on 260926.
     *
     * @return the player's title, C's {@code show_title()}
     */
    public static String getTitle() {
        return title;
    }

    /**
     * Write the title. Package-private, so only {@link RedrawRouter#setTitle} - the only class in
     * this package today - can write the model directly.
     *
     * <p>Method setTitle coded before 260926, commented in full on 260926.
     *
     * @param title the player's title, C's {@code show_title()}
     */
    static void setTitle(String title) {
        SidebarModel.title = title;
    }

    /**
     * Reads the shapechanged flag last written by {@link #setPlayerIsShapechanged(boolean)}, for
     * the sidebar's race and class rows —
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtRace(int, int)} and
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtClass(int, int)},
     * both ports of C's {@code prt_race}/{@code prt_class} ({@code [C] ui-display.c}), are today's
     * readers.
     *
     * <p>Method isPlayerIsShapechanged coded on 260927, commented in full on 260927.
     *
     * @return {@code true} when the player is currently shapechanged, C's
     * {@code player_is_shapechanged(player)}
     */
    public static boolean isPlayerIsShapechanged() {
        return playerIsShapechanged;
    }

    /**
     * Writes the shapechanged flag. Package-private, so only {@link RedrawRouter#setRaceClass} -
     * the only class in this package today - can write the model directly.
     *
     * <p>Method setPlayerIsShapechanged coded on 260927, commented in full on 260927.
     *
     * @param playerIsShapechanged whether the player is currently shapechanged, C's
     *                             {@code player_is_shapechanged(player)}
     */
    static void setPlayerIsShapechanged(boolean playerIsShapechanged) {
        SidebarModel.playerIsShapechanged = playerIsShapechanged;
    }
}
