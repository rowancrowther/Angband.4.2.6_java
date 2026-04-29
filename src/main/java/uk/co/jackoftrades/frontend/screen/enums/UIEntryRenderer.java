package uk.co.jackoftrades.frontend.screen.enums;

public enum UIEntryRenderer {
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

    UIEntryRenderer(CombinerName combiner, String defaultColours, String defaultLabelColours, String defaultSymbols, int defaultDigits, UIEntryEnum entry) {
        this.combiner = combiner;
        this.defaultColours = defaultColours;
        this.defaultLabelColours = defaultLabelColours;
        this.defaultSymbols = defaultSymbols;
        this.defaultDigits = defaultDigits;
        this.entry = entry;
    }
}