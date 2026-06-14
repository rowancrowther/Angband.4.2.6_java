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

package uk.co.jackoftrades.middle.effect;

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