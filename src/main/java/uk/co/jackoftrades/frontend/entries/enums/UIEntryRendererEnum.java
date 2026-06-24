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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.frontend.entries.enums;

import uk.co.jackoftrades.frontend.screen.enums.CombinerName;

/**
 * The built-in renderers used to draw UI status entries, ported from the
 * default renderer table of the C original's UI-entry system. Each constant
 * bundles a value {@link CombinerName combiner} with the default colour strings,
 * label-colour string, symbol string, digit count and sign mode that renderer
 * uses. The packed string parameters are colour/symbol lookup tables (one
 * character per state), so the individual constants are documented collectively
 * here rather than spelling out every table.
 *
 * @author ClaudeCode
 */
public enum UIEntryRendererEnum {
    UI_ENTRY_RENDERER_NONE(CombinerName.NONE, "", "", "", 1, UIEntryEnum.UI_ENTRY_NO_SIGN),
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
     *
     * @author ClaudeCode
     */
    private final CombinerName combiner;
    /**
     * Per-state value colours, one colour code per character.
     *
     * @author ClaudeCode
     */
    private final String defaultColours;
    /**
     * Per-state label colours, one colour code per character.
     *
     * @author ClaudeCode
     */
    private final String defaultLabelColours;
    /**
     * Per-state display symbols, one symbol per character.
     *
     * @author ClaudeCode
     */
    private final String defaultSymbols;
    /**
     * Number of digits to use when rendering a numeric value.
     *
     * @author ClaudeCode
     */
    private final int defaultDigits;
    /**
     * The sign-display mode applied to this renderer's values.
     *
     * @author ClaudeCode
     */
    private final UIEntryEnum entry;

    /**
     * Build a renderer descriptor from its combiner, colour/symbol tables, digit
     * count and sign mode.
     *
     * @param combiner            value-combining strategy
     * @param defaultColours      per-state value colour table
     * @param defaultLabelColours per-state label colour table
     * @param defaultSymbols      per-state symbol table
     * @param defaultDigits       digit count for numeric rendering
     * @param entry               sign-display mode
     * @author ClaudeCode
     */
    UIEntryRendererEnum(CombinerName combiner, String defaultColours, String defaultLabelColours, String defaultSymbols, int defaultDigits, UIEntryEnum entry) {
        this.combiner = combiner;
        this.defaultColours = defaultColours;
        this.defaultLabelColours = defaultLabelColours;
        this.defaultSymbols = defaultSymbols;
        this.defaultDigits = defaultDigits;
        this.entry = entry;
    }
}