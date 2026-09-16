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

package uk.co.jackoftradesltd.frontend.entries.enums;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import uk.co.jackoftradesltd.frontend.screen.enums.CombinerName;

/**
 * The built-in renderers used to draw UI status entries, one constant per row
 * of the C original's {@code list-ui-entry-renderers.h} table (compiled into
 * the {@code backends[]} array of {@code struct backend_info} in {@code
 * ui-entry-renderers.c}), plus a Java-only {@link #UI_ENTRY_RENDERER_NONE}
 * sentinel with no C row of its own. It stands in for C's invalid/unset
 * renderer index: valid indices there run from {@code
 * ui_entry_renderer_get_min_index()} (1) up to but excluding {@code
 * ui_entry_renderer_get_index_limit()}, leaving 0 free to mean "none". Each
 * constant bundles a value {@link CombinerName combiner} with the default
 * colour strings, label-colour string, symbol string, digit count and sign
 * mode a data-file renderer falls back on for any field its own {@code
 * ui_entry_renderers.txt} block leaves unset ({@code
 * finish_parse_ui_entry_renderer}, {@code ui-entry-renderers.c}). The packed
 * string parameters are colour/symbol lookup tables (one character per
 * state), so the individual constants are documented collectively here
 * rather than spelling out every table.
 *
 * <p>Class UIEntryRendererEnum coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public enum UIEntryRendererEnum {
    UI_ENTRY_RENDERER_NONE(CombinerName.NONE, "", "", "", 0, UIEntryEnum.UI_ENTRY_NO_SIGN),
    UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX(CombinerName.RESIST_0,
            "wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW",
            "swBrgwBrwBwBr", "?..+-*!^.=.%%%~!=%~+=~", 0, UIEntryEnum.UI_ENTRY_NO_SIGN),
    UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX(CombinerName.LOGICAL_OR, "wwwwGWWWWG", "swBw", "?..+!", 0, UIEntryEnum.UI_ENTRY_NO_SIGN),
    UI_ENTRY_RENDERER_COMPACT_FLAG_WITH_CANCEL_RENDERER_WITH_COMBINED_AUX(CombinerName.LOGICAL_OR_WITH_CANCEL, "wwwwwGwwGGwWWWWWGWWGGW", "swwwwBw", "?..+-!+-=.-", 0, UIEntryEnum.UI_ENTRY_NO_SIGN),
    UI_ENTRY_RENDERER_NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX(CombinerName.ADD, "wwwGowGowGoWWWGoWGoWGo", "swwwBBBrrr", "?....+!+--=", 0, UIEntryEnum.UI_ENTRY_NO_SIGN),
    UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX(CombinerName.ADD, "wwwboBbPrRowwwboBbPrRo", "swwwBBBrrr", "?0000+-", 1, UIEntryEnum.UI_ENTRY_NO_SIGN),
    UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX(CombinerName.ADD, "wdsgGgrRwdsgGgrR", "wwwwwww", "? .s*=", 1, UIEntryEnum.UI_ENTRY_NO_SIGN);

    /**
     * How multiple contributing values are merged before rendering.
     * Corresponds to C's {@code default_combiner_name} ({@code struct
     * backend_info}, {@code ui-entry-renderers.c}), resolved from its string
     * name to a {@link CombinerName} constant.
     *
     * <p>Field combiner coded before 260916, commented in full on 260916.
     */
    private final CombinerName combiner;
    /**
     * Per-state value colours, one colour code per character. Corresponds to
     * C's {@code default_colors} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Field defaultColours coded before 260916, commented in full on 260916.
     */
    private final String defaultColours;
    /**
     * Per-state label colours, one colour code per character. Corresponds to
     * C's {@code default_labelcolors} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Field defaultLabelColours coded before 260916, commented in full on 260916.
     */
    private final String defaultLabelColours;
    /**
     * Per-state display symbols, one symbol per character. Corresponds to
     * C's {@code default_symbols} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Field defaultSymbols coded before 260916, commented in full on 260916.
     */
    private final String defaultSymbols;
    /**
     * Number of digits to use when rendering a numeric value. Corresponds to
     * C's {@code default_ndigit} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Field defaultDigits coded before 260916, commented in full on 260916.
     */
    private final int defaultDigits;
    /**
     * The sign-display mode applied to this renderer's values. Corresponds
     * to C's {@code default_sign} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Field entry coded before 260916, commented in full on 260916.
     */
    private final UIEntryEnum entry;

    /**
     * Build a renderer descriptor from its combiner, colour/symbol tables, digit
     * count and sign mode — the Java equivalent of one row's worth of fields
     * from C's {@code backends[]} table ({@code UI_ENTRY_RENDERER} macro
     * expansion over {@code list-ui-entry-renderers.h}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Function UIEntryRendererEnum coded before 260916, commented in full on 260916.
     *
     * @param combiner            value-combining strategy
     * @param defaultColours      per-state value colour table
     * @param defaultLabelColours per-state label colour table
     * @param defaultSymbols      per-state symbol table
     * @param defaultDigits       digit count for numeric rendering
     * @param entry               sign-display mode
     */
    UIEntryRendererEnum(CombinerName combiner, String defaultColours, String defaultLabelColours, String defaultSymbols, int defaultDigits, UIEntryEnum entry) {
        this.combiner = combiner;
        this.defaultColours = defaultColours;
        this.defaultLabelColours = defaultLabelColours;
        this.defaultSymbols = defaultSymbols;
        this.defaultDigits = defaultDigits;
        this.entry = entry;
    }

    /**
     * Returns the default digit count used when rendering a numeric value.
     * Corresponds to C's {@code default_ndigit} ({@code struct
     * backend_info}, {@code ui-entry-renderers.c}).
     *
     * <p>Function getDefaultDigits coded before 260916, commented in full on 260916.
     *
     * @return the default number of digits for this UI Entry Renderer enum
     */
    @Contract(pure = true)
    @CheckReturnValue
    public int getDefaultDigits() {
        return defaultDigits;
    }

    /**
     * Returns the default sign-display mode applied by this renderer.
     * Corresponds to C's {@code default_sign} ({@code struct backend_info},
     * {@code ui-entry-renderers.c}).
     *
     * <p>Function getEntry coded before 260916, commented in full on 260916.
     *
     * @return the default sign for this UI Entry Renderer enum
     */
    @Contract(pure = true)
    @CheckReturnValue
    public UIEntryEnum getEntry() {
        return entry;
    }

    /**
     * Returns the default per-state value colour table. Corresponds to C's
     * {@code default_colors} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Function getDefaultColours coded before 260916, commented in full on 260916.
     *
     * @return the default colours for UI Entry Renderers
     */
    @Contract(pure = true)
    @CheckReturnValue
    public String getDefaultColours() {
        return defaultColours;
    }

    /**
     * Returns the default per-state label colour table. Corresponds to C's
     * {@code default_labelcolors} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Function getDefaultLabelColours coded before 260916, commented in full on 260916.
     *
     * @return the default label colours for UI Entry Renderers
     */
    @Contract(pure = true)
    @CheckReturnValue
    public String getDefaultLabelColours() {
        return defaultLabelColours;
    }

    /**
     * Returns the default per-state display symbol table. Corresponds to
     * C's {@code default_symbols} ({@code struct backend_info}, {@code
     * ui-entry-renderers.c}).
     *
     * <p>Function getDefaultSymbols coded before 260916, commented in full on 260916.
     *
     * @return the default symbols for UI Entry Renderers
     */
    @Contract(pure = true)
    @CheckReturnValue
    public String getDefaultSymbols() {
        return defaultSymbols;
    }
}