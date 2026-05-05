package uk.co.jackoftrades.frontend.entries;

import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

public class UIEntryRenderer {
    private String name;
    private UIEntryRendererEnum code;
    private String colours;
    private String labelColours;
    private String symbols;
    private int nDigit;
    private UIEntryEnum sign;

    public UIEntryRenderer(String name,
                           UIEntryRendererEnum code,
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
        return "UIEntryRenderer{" +
                "name='" + name + '\'' +
                ", code=" + code +
                ", colours='" + colours + '\'' +
                ", labelColours='" + labelColours + '\'' +
                ", symbols='" + symbols + '\'' +
                ", nDigit=" + nDigit +
                ", sign=" + sign +
                '}';
    }

    public String getName() {
        return name;
    }
}