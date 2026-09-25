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

import uk.co.jackoftradesltd.channel.globals.ChannelRegistry;
import uk.co.jackoftradesltd.channel.messages.data.PlayerEventStatusUpdate;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.entries.UIEntryIterator;
import uk.co.jackoftradesltd.frontend.screen.Term;
import uk.co.jackoftradesltd.frontend.screen.TermData;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.frontend.ui.output.Region;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetConfig;
import uk.co.jackoftradesltd.frontend.ui.player.CharSheetResist;

import java.util.Optional;

/**
 * Character-sheet display driver — the partial Java port of C's {@code ui-player.c} ("character
 * screens and dumps").
 *
 * <p>Owns the resistance-panel layout cache ({@link #cachedConfig}) built by
 * {@link #configureCharSheet()}, reused across draws while {@link #haveValidCharSheetConfig()}
 * still holds, and consulted by {@link #displayPlayer(PlayerDisplayMode)} before each draw.
 *
 * <p>Class UIPlayer coded before 260925, commented in full on 260925.
 *
 * @author Rowan Crowther
 */
public class UIPlayer {
    /**
     * One-shot latch guarding {@link #spc}'s initialisation inside {@link #getUIEntryLabel}, mirroring
     * C's function-static {@code first_call} inside {@code get_ui_entry_label}
     * ({@code [C] ui-entry.c:342,347-352}). Set once and never reset, so {@link #spc} is only ever
     * populated on the first call across the life of the JVM.
     *
     * <p>Field firstCallOfGetEntryLabel coded before 260925, commented in full on 260925.
     */
    private static boolean firstCallOfGetEntryLabel = true;
    /**
     * The two-character space/terminator pair set on the first call to {@link #getUIEntryLabel}, the
     * Java form of C's function-static {@code wchar_t spc[2]} ({@code [C] ui-entry.c:343,348}):
     * {@code spc[0]} the padding space, {@code spc[1]} the terminating null. {@link #getUIEntryLabel}
     * pads with a literal {@code " "} instead of reading this array, so it is set but never consulted.
     *
     * <p>Field spc coded before 260925, commented in full on 260925.
     */
    private static char[] spc = new char[2];

    /**
     * The cached resistance-panel layout for the current player, built by
     * {@link #configureCharSheet()} and read back by {@link #haveValidCharSheetConfig()} on every
     * draw — the Java form of C's function-static {@code cached_config}
     * ({@code [C] ui-player.c:142}).
     *
     * <p>{@code null} until the first {@link #configureCharSheet()} call, mirroring C's
     * {@code static struct char_sheet_config *cached_config = NULL;} initialisation. Once built,
     * it is reused across draws for as long as {@link #haveValidCharSheetConfig()} reports the
     * layout still matches the player's current body shape; when it does not, a fresh
     * {@link CharSheetConfig} replaces this field outright rather than being mutated in place.
     *
     * <p>Field cachedConfig coded before 260925, commented in full on 260925.
     */
    private CharSheetConfig cachedConfig = null;

    /**
     * Tests whether a UI entry belongs to both of the two categories named in {@code closure} —
     * the port of C's {@code check_for_two_categories} ({@code [C] ui-player.c:176-183}), used as
     * the iterator predicate throughout {@link #configureCharSheet()}.
     *
     * <p>A {@code null} entry is treated as not matching rather than passed on to
     * {@link UIEntry#uiEntryHasCategory(String)}, mirroring the fact that C's
     * {@code ui_entry_has_category} is never called with a null {@code entry} either — the
     * iterator only ever offers real entries to its predicate.
     *
     * <p>{@code closure} carries the two category names to test, in the same order on every call:
     * {@code closure[0]} is always {@code "CHAR_SCREEN1"} across every {@link #configureCharSheet()}
     * use, while {@code closure[1]} changes per call — {@code "stat_modifiers"} for the
     * stat-modifier pass, then in turn each of {@code "resistances"}, {@code "abilities"},
     * {@code "hindrances"} and {@code "modifiers"} for the four resistance-panel regions.
     *
     * <p>Method checkForTwoCategories coded before 260925, commented in full on 260925.
     *
     * @param closure a two-element array of category names to test against
     * @param entry   the UI entry to test, or {@code null}
     * @return {@code true} if {@code entry} is non-null and belongs to both named categories
     */
    private static boolean checkForTwoCategories(String[] closure,
                                                 UIEntry entry) {
        if (entry == null) return false;

        Optional<Integer> res1 = entry.uiEntryHasCategory(closure[0]);
        Optional<Integer> res2 = entry.uiEntryHasCategory(closure[1]);

        return (res1.isPresent() && res2.isPresent());
    }

    public void displayPlayer(PlayerDisplayMode mode) {
        if (!haveValidCharSheetConfig()) {
            configureCharSheet();
        }

        // Erase the screen
        Term term = TermData.getTerm();
        term.clearFrom(0);
        // TODO <-- Here
    }

    /**
     * Builds and caches the character-sheet's resistance-panel layout for the current player —
     * the port of C's {@code configure_char_sheet} ({@code [C] ui-player.c:186-266}). Replaces
     * {@link #cachedConfig} outright with a freshly-populated {@link CharSheetConfig}; C instead
     * frees and reallocates {@code cached_config} via {@code release_char_sheet_config}
     * ({@code [C] ui-player.c:160-173, 198}), but the two reach the same end state, since neither
     * leaves any part of the previous layout observable afterwards.
     *
     * <p>First builds the stat-modifier entry list: every {@link UIEntry} in both the
     * {@code "CHAR_SCREEN1"} and {@code "stat_modifiers"} categories, capped at
     * {@link ChannelRegistry#STAT_MAX} because the stat-modifier display is hardwired to that many
     * rows — C applies the same {@code STAT_MAX} clamp for the same reason
     * ({@code [C] ui-player.c:211-213}).
     *
     * <p>Then, for each of the four resistance-panel regions ({@code "resistances"},
     * {@code "abilities"}, {@code "hindrances"}, {@code "modifiers"}, in that order): positions
     * the region's {@link Region} at column {@code index * (resCols + 1)}, row
     * {@code 2 + STAT_MAX} and width {@code resCols}; counts the matching entries and, if fitting
     * them plus two rows of chrome below that region's row would overflow row 22, shortens the
     * count to {@code 20 - row} instead — the same 22/20 bounds C uses to keep the panel inside
     * its display ({@code [C] ui-player.c:238-244}); then, for each matching entry in turn, builds
     * a {@link CharSheetResist} wrapping it, files it under that region's index and that entry's
     * position within the region via
     * {@link CharSheetConfig#setResistsByRegion(int, int, CharSheetResist)}, and computes its
     * display label via {@link #getUIEntryLabel} with a trailing {@code ":"} appended — standing
     * in for C's separate {@code get_ui_entry_label} plus {@code text_mbstowcs} call that writes
     * the colon into the last slot of the entry's own six-character label buffer
     * ({@code [C] ui-player.c:250-252}). {@link CharSheetConfig}'s resist storage, keyed by
     * (region, entry-within-region), is the Java form of C's {@code resists_by_region[i][j]} — an
     * array of pointers, each to a separately-sized array of per-region entries
     * ({@code [C] ui-player.c:135, 246}).
     *
     * <p>{@link CharSheetConfig#getResRows()} tracks the largest per-region entry count seen
     * across all four regions as the loop runs; once every region has been processed, every
     * region's {@code pageRows} is set to that maximum plus two, in a final pass mirroring C's own
     * closing loop ({@code [C] ui-player.c:262-265}).
     *
     * <p>Method configureCharSheet coded before 260925, commented in full on 260925.
     */
    private void configureCharSheet() {
        String[] regionCategories = {"resistances", "abilities", "hindrances", "modifiers"};
        cachedConfig = new CharSheetConfig();

        String[] testCategories = {"CHAR_SCREEN1", "stat_modifiers"};
        UIEntryIterator uiIter = UIEntryCode.initialiseUIEntryIterator(UIPlayer::checkForTwoCategories, testCategories,
                testCategories[1]);
        int num = Math.min(uiIter.getNum(), ChannelRegistry.STAT_MAX);

        cachedConfig.setNStatModEntries(num);
        cachedConfig.initStatModEntries(num);
        for (int index = 0; index < num; index++) {
            cachedConfig.setStatModEntry(index, uiIter.advance());
        }

        cachedConfig.setResNlabel(6);
        cachedConfig.setResCols(cachedConfig.getResNLabel() + 1
                + PlayerEventStatusUpdate.getPlayerStatusView().bodyCount());
        cachedConfig.setResRows(0);

        for (int index = 0; index < 4; index++) {
            cachedConfig.getResRegion(index).setCol(index *
                    (cachedConfig.getResCols() + 1));
            cachedConfig.getResRegion(index).setRow(2 + ChannelRegistry.STAT_MAX);
            cachedConfig.getResRegion(index).setWidth(cachedConfig.getResCols());

            testCategories[1] = regionCategories[index];
            uiIter = UIEntryCode.initialiseUIEntryIterator(UIPlayer::checkForTwoCategories, testCategories,
                    regionCategories[index]);
            num = uiIter.getNum();
            // Fit in a 23 row display; leave at least one row blank before prompt on last row.
            if (num + 2 + cachedConfig.getResRegion(index).getRow() > 22) {
                num = 20 - cachedConfig.getResRegion(index).getRow();
            }
            cachedConfig.setnResistsByRegion(index, num);
            for (int iterIndex = 0; iterIndex < num; iterIndex++) {
                UIEntry uiEntry = uiIter.advance();

                CharSheetResist resist = new CharSheetResist(uiEntry);
                cachedConfig.setResistsByRegion(index, iterIndex, resist);
                String label = getUIEntryLabel(uiEntry, cachedConfig.getResNLabel(), true,
                        cachedConfig.getResistsByRegion(index, iterIndex).getLabel()) + ":";
                cachedConfig.getResistsByRegion(index, iterIndex).setLabel(label);
            }

            if (cachedConfig.getResRows() < cachedConfig.getnResistsByRegion(index)) {
                cachedConfig.setResRows(cachedConfig.getnResistsByRegion(index));
            }
        }

        for (int index = 0; index < 4; index++) {
            cachedConfig.getResRegion(index).setPageRows(cachedConfig.getResRows() + 2);
        }
    }

    /**
     * Builds a label for a UI entry, padded or truncated to an exact display width — the port of
     * C's {@code get_ui_entry_label} ({@code [C] ui-entry.c:339-387}).
     *
     * <p>{@code length} counts the visible characters only; C's buffer additionally reserves a
     * terminating null, so where C computes padding and truncation against {@code length - 1},
     * the returned {@link String} (no terminator to reserve) reproduces that same
     * {@code length - 1} content width directly. A {@code length} of zero or less is a no-op
     * that returns {@code label} unchanged, matching C's early return before the buffer is ever
     * touched; a {@code length} of one returns the empty string, standing in for C's single
     * null-only buffer.
     *
     * <p>The source text comes from {@link UIEntry#getShortenedLabel(int)} when {@code length}
     * falls within {@link UIRegistry#MAX_SHORTENED} plus one, and from {@link UIEntry#getLabel()}
     * otherwise, exactly as C selects between {@code entry->shortened_labels} and
     * {@code entry->label}. If that source text is too long for the requested width it is
     * truncated to {@code length - 1} characters; if it is too short it is padded with spaces on
     * the side {@code padLeft} names, {@code length - 1 - numChars} of them, so the padded and
     * truncated cases both end up with the same content width.
     *
     * <p>{@link #firstCallOfGetEntryLabel} and {@link #spc} mirror C's function-static one-shot
     * space initialisation but are not themselves read by this method — the padding here is
     * built with a literal {@code " "} instead.
     *
     * <p>Called from {@link #configureCharSheet()} while laying out the resistance panel.
     *
     * <p>Method getUIEntryLabel coded before 260925, commented in full on 260925.
     *
     * @param entry   the UI entry whose label text is being formatted
     * @param length  the desired content width in characters; zero or less is a no-op, one
     *                returns the empty string
     * @param padLeft {@code true} pads on the left, {@code false} pads on the right
     * @param label   the fallback value returned unchanged when {@code length} is zero or less
     * @return the entry's label padded or truncated to {@code length - 1} characters, the empty
     * string when {@code length} is one, or {@code label} unchanged when {@code length} is zero
     * or less
     */
    private String getUIEntryLabel(UIEntry entry, int length, boolean padLeft, String label) {
        if (firstCallOfGetEntryLabel) {
            spc[0] = ' ';
            spc[1] = '\0';
            firstCallOfGetEntryLabel = false;
        }

        String src;
        int numChars;

        if (length <= 0) return label;

        if (length == 1) {
            return "";
        }

        if (length <= UIRegistry.MAX_SHORTENED + 1) {
            src = entry.getShortenedLabel(length - 2);
            numChars = src.length();
        } else {
            src = entry.getLabel();
            numChars = src.length();
        }
        if (numChars < length - 1) {
            if (padLeft) {
                String left = " ".repeat(length - numChars - 1);
                label = left + src;
            } else {
                label = src + " ".repeat(length - numChars - 1);
            }
        } else {
            label = src.substring(0, length - 1);
        }
        return label;
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
