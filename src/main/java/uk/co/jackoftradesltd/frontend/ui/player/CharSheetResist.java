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

package uk.co.jackoftradesltd.frontend.ui.player;

import uk.co.jackoftradesltd.frontend.entries.UIEntry;

/**
 * One resistance-panel entry: the {@link UIEntry} it displays and that entry's rendered label —
 * the Java form of C's {@code struct char_sheet_resist} in {@code ui-player.c}.
 *
 * <p>C's {@code label} is a fixed six-character buffer, filled by {@code get_ui_entry_label} and
 * then overwritten at its last slot with a colon by a separate {@code text_mbstowcs} call, both
 * in {@code ui-player.c}, function {@code init_char_sheet_config()}; the Java port builds that
 * same content plus colon as a single {@link String} before storing it here (see
 * {@code UIPlayer.java}), so {@link #label} needs no fixed width or separate colon slot.
 *
 * <p>One instance exists per row of each of the four resistance-panel regions; they are held in
 * {@link CharSheetConfig} (C's {@code resists_by_region}), and the panel-drawing code reads
 * {@link #entry} to pick the renderer and {@link #label} to draw the row heading.
 *
 * <p>Class CharSheetResist coded on 260925, commented in full on 260929.
 *
 * @author Rowan Crowther
 */
public class CharSheetResist {
    /**
     * The UI entry this resist panel row displays — the Java form of C's {@code entry} member of
     * {@code struct char_sheet_resist}. It is a shared reference into the UI entry registry, not a
     * copy, and may be {@code null} only in tests that never render the row.
     *
     * <p>Field entry coded on 260925, commented in full on 260929.
     */
    UIEntry entry;

    /**
     * This entry's rendered display label, including its trailing colon — the Java form of C's
     * {@code label[6]} collapsed into a single {@link String}; see the class Javadoc. Defaults to
     * the empty string, standing in for C's not-yet-filled buffer, until the real label is
     * computed and stored with {@link #setLabel(String)}.
     *
     * <p>Field label coded on 260925, commented in full on 260929.
     */
    String label = "";

    /**
     * Wraps a UI entry for display in the resistance panel, leaving {@link #label} at its empty
     * default until it is set separately. Mirrors C storing {@code entry} first and filling
     * {@code label} on the following statements.
     *
     * <p>Constructor CharSheetResist coded on 260925, commented in full on 260929.
     *
     * @param entry the UI entry this resist panel row displays
     */
    public CharSheetResist(UIEntry entry) {
        this.entry = entry;
    }

    /**
     * Returns the row's label: the empty string until {@link #setLabel(String)} has been called,
     * then the padded label plus trailing colon. Java-only accessor; C reads the {@code label}
     * member directly.
     *
     * <p>Method getLabel coded on 260925, commented in full on 260929.
     *
     * @return this entry's rendered display label, including its trailing colon
     */
    public String getLabel() {
        return label;
    }

    /**
     * Replaces the row's label. The caller supplies the finished text, colon included — this
     * method does no padding or truncation, which C does in {@code get_ui_entry_label}. Java-only
     * accessor; C writes the {@code label} buffer directly.
     *
     * <p>Method setLabel coded on 260925, commented in full on 260929.
     *
     * @param label this entry's rendered display label, including its trailing colon
     */
    public void setLabel(String label) {
        this.label = label;
    }
}