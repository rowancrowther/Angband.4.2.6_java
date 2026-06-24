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

/**
 * A named, game-state-driven scaling expression attached to an {@link Effect}.
 * It binds a single-letter placeholder used in the effect's dice string to a
 * base quantity ({@link EffectBaseType}) plus a chain of arithmetic operations,
 * so an effect's magnitude can scale with (say) player level or spell power at
 * evaluation time. This is the Java port of the C original's {@code expression}
 * ({@code src/datafile.c} / {@code src/parser.c} expression support).
 *
 * @author ClaudeCode
 */
public class Expression {
    /**
     * The single-letter placeholder this expression substitutes for in the dice string.
     *
     * @author ClaudeCode
     */
    private char codeLetter;
    /**
     * The base game quantity the expression starts from.
     *
     * @author ClaudeCode
     */
    private EffectBaseType baseType;
    /**
     * The chain of arithmetic operations applied to the base value.
     *
     * @author ClaudeCode
     */
    private String operations;
    /**
     * The dice string the evaluated expression feeds into.
     *
     * @author ClaudeCode
     */
    private String diceString;

    /**
     * Build an expression binding a placeholder to a base value and operations.
     *
     * @param codeLetter the placeholder letter
     * @param baseType   the base quantity
     * @param operations the arithmetic operation chain
     * @author ClaudeCode
     */
    public Expression(char codeLetter, EffectBaseType baseType, String operations) {
        this.codeLetter = codeLetter;
        this.baseType = baseType;
        this.operations = operations;
    }

    /**
     * Set the dice string this expression applies to.
     *
     * @param diceString the dice expression string
     * @author ClaudeCode
     */
    public void setDiceString(String diceString) {
        this.diceString = diceString;
    }
}