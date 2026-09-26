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
 * being architected out in their favour. Today this class holds the HP and SP pairs; the rest of
 * C's {@code prt_*} family in {@code [C] ui-display.c} join it as their own {@code EVENT_*}
 * payloads are ported.
 *
 * <p>The setters are package-private and the getters public, so only a class in this package —
 * today, only {@link RedrawRouter} — can write, while any caller may read.
 *
 * <p>Class SidebarModel coded on 260926, commented in full on 260926.
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
}
