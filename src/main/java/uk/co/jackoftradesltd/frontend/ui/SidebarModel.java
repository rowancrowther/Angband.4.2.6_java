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
 * title, race name, class name, full name and shapechanged status; the rest of C's {@code prt_*}
 * family in {@code [C] ui-display.c} join it as their own {@code EVENT_*} payloads are ported.
 *
 * <p>The setters are package-private and the getters public, so only a class in this package —
 * today, only {@link RedrawRouter} — can write, while any caller may read.
 *
 * <p><b>Outstanding:</b> {@link #playerIsShapechanged} has no writer yet. Nothing in
 * {@link RedrawRouter} or the {@code EVENT_RACE_CLASS} payload it reads carries a shapechanged
 * flag across the boundary, so the field keeps its Java default of {@code false} forever — see
 * {@link #playerIsShapechanged}'s own Javadoc. Deliberately not yet implemented.
 *
 * <p>Class SidebarModel coded on 260926, commented in full on 260927.
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
     * Whether the player is currently in a non-normal shape, C's {@code player_is_shapechanged}
     * ({@code [C] player-util.c}) — the port of what {@code prt_race} and {@code prt_class}
     * ({@code [C] ui-display.c}) each re-check against the live global at draw time, so that a
     * shapechanged player's race and class fields are blanked rather than shown.
     *
     * <p><b>Outstanding:</b> nothing writes this field yet. {@link RedrawRouter#setRaceClass} only
     * carries the race and class names in from the {@code EVENT_RACE_CLASS} payload, and that
     * payload — built in {@code PlayerCalcs.redrawStuff}'s {@code PR_MISC} arm — never packs a
     * shapechanged flag in the first place. So this field keeps Java's default of {@code false}
     * forever today, and {@link HandlersHolder#prtRace}'s blanking branch is unreachable until a
     * writer is wired up — see
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtRace(int, int)}.
     * Deliberately not yet implemented, not a discrepancy in this field itself.
     *
     * <p>Field playerIsShapechanged coded on 260927, commented in full on 260927.
     */
    private static boolean playerIsShapechanged;

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
     * {@link uk.co.jackoftradesltd.frontend.screen.handlers.HandlersHolder#prtRace(int, int)}'s
     * port of C's {@code prt_race} ({@code [C] ui-display.c}) is today's only reader.
     *
     * <p><b>Outstanding:</b> see {@link #playerIsShapechanged}'s own Javadoc — nothing writes this
     * field yet, so this always returns {@code false} today.
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
     * Writes the shapechanged flag. Package-private, so only a class in this package could write
     * the model directly — see {@link #playerIsShapechanged}'s own Javadoc for why nothing in this
     * package calls it yet.
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
