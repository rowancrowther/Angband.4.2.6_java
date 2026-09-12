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

import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.frontend.entries.UIEntryIterator;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetConfig;

public class UIPlayer {
    private CharSheetConfig cachedConfig = null;

    public void displayPlayer(PlayerDisplayMode mode) {
        if (!haveValidCharSheetConfig()) {
            configureCharSheet();
        }
    }

    private void configureCharSheet() {
        String[] regionCategories = {"resistances", "abilities", "hinderances", "modifiers"};
        cachedConfig = new CharSheetConfig();

        String[] testCategories = {"CHAR_SCREEN", "stat_modifiers"};
        UIEntryIterator uiIter = UIEntryCode.initialiseUIEntryIterator();
    }

    /**
     * Reports whether the cached character-sheet layout is still usable for the current
     * player — the port of C's {@code have_valid_char_sheet_config} ({@code ui-player.c:146-156}).
     *
     * <p>There is nothing to reuse until a layout has been built at least once, so a
     * {@code null} {@link #cachedConfig} fails outright. Once one exists, the check is
     * narrower: it only re-derives {@code resCols} from {@code resNLabel} and the player's
     * current body part count and compares that against the cached value. C computes
     * {@code res_cols} the same way in {@code configure_char_sheet} ({@code ui-player.c:223-224}),
     * so the two can only disagree when the player's body shape has changed since the
     * layout was cached — a race switch mid-game being the case that matters, since a
     * different race can bring a different equipment slot count.
     *
     * <p>{@link #displayPlayer(PlayerDisplayMode)} calls this as a boundary guard before
     * drawing; a stale cache should trigger a rebuild before rendering continues.
     *
     * <p>Method haveValidCharSheetConfig coded on 260911, commented in full on 260911.
     *
     * @return {@code true} if the cached layout's resistance-panel column count still
     * matches the current player's body, {@code false} if there is no cached layout
     * or it is stale
     */
    private boolean haveValidCharSheetConfig() {
        if (cachedConfig == null) {
            return false;
        }
        return cachedConfig.getResCols() == cachedConfig.getResNLabel() + 1
                + PlayerEventStatusUpdate.getPlayerStatusView().bodyCount();
    }

    public enum PlayerDisplayMode {
        DISPLAY_FULL, DISPLAY_EXTRA
    }
}
