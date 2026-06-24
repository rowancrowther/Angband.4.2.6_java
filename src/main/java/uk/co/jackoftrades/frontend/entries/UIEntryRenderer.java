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

package uk.co.jackoftrades.frontend.entries;

import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

/**
 * A concrete, named renderer instance for a UI status entry — the data-file
 * counterpart of the built-in {@link UIEntryRendererEnum} defaults. It carries
 * the colour/symbol tables, digit count and sign mode actually used to draw an
 * entry's value (Java port of the C original's UI-entry renderer definitions).
 *
 * @author ClaudeCode
 */
public class UIEntryRenderer {
    /**
     * The renderer's name.
     *
     * @author ClaudeCode
     */
    private String name;
    /**
     * The built-in renderer kind this instance is based on.
     *
     * @author ClaudeCode
     */
    private UIEntryRendererEnum code;
    /**
     * Per-state value colours, one colour code per character.
     *
     * @author ClaudeCode
     */
    private String colours;
    /**
     * Per-state label colours, one colour code per character.
     *
     * @author ClaudeCode
     */
    private String labelColours;
    /**
     * Per-state display symbols, one symbol per character.
     *
     * @author ClaudeCode
     */
    private String symbols;
    /**
     * Number of digits used when rendering a numeric value.
     *
     * @author ClaudeCode
     */
    private int nDigit;
    /**
     * The sign-display mode applied to this renderer's values.
     *
     * @author ClaudeCode
     */
    private UIEntryEnum sign;

    /**
     * Build a renderer instance from its parsed fields.
     *
     * @param name         renderer name
     * @param code         the built-in renderer kind it is based on
     * @param colours      per-state value colour table
     * @param labelColours per-state label colour table
     * @param symbols      per-state symbol table
     * @param nDigit       digit count for numeric rendering
     * @param sign         sign-display mode
     * @author ClaudeCode
     */
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

    /**
     * @return a debug string listing this renderer's fields
     * @author ClaudeCode
     */
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

    /**
     * @return this renderer's name
     * @author ClaudeCode
     */
    public String getName() {
        return name;
    }
}