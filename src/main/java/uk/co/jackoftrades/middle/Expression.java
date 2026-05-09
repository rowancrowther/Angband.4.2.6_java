package uk.co.jackoftrades.middle;

import uk.co.jackoftrades.middle.enums.EffectBaseType;

public class Expression {
    private char codeLetter;
    private EffectBaseType baseType;
    private String operations;
    private String diceString;

    public Expression(char codeLetter, EffectBaseType baseType, String operations) {
        this.codeLetter = codeLetter;
        this.baseType = baseType;
        this.operations = operations;
    }

    public void setDiceString(String diceString) {
        this.diceString = diceString;
    }
}