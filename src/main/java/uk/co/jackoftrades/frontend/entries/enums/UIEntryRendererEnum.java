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

    private final CombinerName combiner;
    private final String defaultColours;
    private final String defaultLabelColours;
    private final String defaultSymbols;
    private final int defaultDigits;
    private final UIEntryEnum entry;

    UIEntryRendererEnum(CombinerName combiner, String defaultColours, String defaultLabelColours, String defaultSymbols, int defaultDigits, UIEntryEnum entry) {
        this.combiner = combiner;
        this.defaultColours = defaultColours;
        this.defaultLabelColours = defaultLabelColours;
        this.defaultSymbols = defaultSymbols;
        this.defaultDigits = defaultDigits;
        this.entry = entry;
    }
}