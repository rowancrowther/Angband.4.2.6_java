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
 * the Java form of C's {@code struct char_sheet_resist} ({@code [C] ui-player.c:128-131}).
 *
 * <p>C's {@code label} is a fixed six-character buffer, filled by {@code get_ui_entry_label} and
 * then overwritten at its last slot with a colon by a separate {@code text_mbstowcs} call
 * ({@code [C] ui-player.c:251-252}); the Java port builds that same content plus colon as a
 * single {@link String} before storing it here, so {@link #label} needs no fixed width or
 * separate colon slot.
 *
 * <p>Class CharSheetResist coded on 260925, commented in full on 260925.
 *
 * @author Rowan Crowther
 */
public class CharSheetResist {
    /**
     * The UI entry this resist panel row displays — the Java form of C's {@code entry}
     * ({@code [C] ui-player.c:129}).
     *
     * <p>Field entry coded on 260925, commented in full on 260925.
     */
    UIEntry entry;

    /**
     * This entry's rendered display label, including its trailing colon — the Java form of C's
     * {@code label[6]} ({@code [C] ui-player.c:130}) collapsed into a single {@link String}; see
     * the class Javadoc. Defaults to the empty string until the real label is computed.
     *
     * <p>Field label coded on 260925, commented in full on 260925.
     */
    String label = "";

    /**
     * Wraps a UI entry for display in the resistance panel, leaving {@link #label} at its empty
     * default until it is set separately.
     *
     * <p>Constructor CharSheetResist coded on 260925, commented in full on 260925.
     *
     * @param entry the UI entry this resist panel row displays
     */
    public CharSheetResist(UIEntry entry) {
        this.entry = entry;
    }

    /**
     * Method getLabel coded on 260925, commented in full on 260925.
     *
     * @return this entry's rendered display label, including its trailing colon
     */
    public String getLabel() {
        return label;
    }

    /**
     * Method setLabel coded on 260925, commented in full on 260925.
     *
     * @param label this entry's rendered display label, including its trailing colon
     */
    public void setLabel(String label) {
        this.label = label;
    }
}