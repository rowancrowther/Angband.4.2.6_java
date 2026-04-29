package uk.co.jackoftrades.frontend.screen;

import uk.co.jackoftrades.frontend.screen.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.screen.enums.UIEntryRenderer;

public class UIEntry {
    private String name;
    private UIEntryRenderer code;
    private String colours;
    private String labelColours;
    private String symbols;
    private int nDigit;
    private UIEntryEnum sign;

    public UIEntry(String name,
                   UIEntryRenderer code,
                   String colours,
                   String labelColours,
                   String symbols,
                   int nDigit,
                   UIEntryEnum sign) {
        this.sign = sign;
        this.nDigit = nDigit;
        this.symbols = symbols;
        this.labelColours = labelColours;
        this.colours = colours;
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return "UIEntry{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", colours='" + colours + '\'' +
                ", labelColours='" + labelColours + '\'' +
                ", symbols='" + symbols + '\'' +
                ", nDigit=" + nDigit +
                ", sign=" + sign +
                '}';
    }
}