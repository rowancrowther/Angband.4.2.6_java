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

package uk.co.jackoftradesltd.frontend.entries;

import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;

/**
 * A concrete, named renderer instance for a UI status entry — the data-file
 * counterpart of the built-in {@link UIEntryRendererEnum} defaults. It carries
 * the colour/symbol tables, digit count and sign mode actually used to draw an
 * entry's value, matching the finished, post-default {@code struct
 * renderer_info} ({@code ui-entry-renderers.c}) once C's
 * {@code finish_parse_ui_entry_renderer} has resolved it against its backend.
 * This class only stores what it is given; the default-filling itself is done
 * by {@code UIEntryRendererAssembler} before construction, not here.
 *
 * <p>Class UIEntryRenderer coded before 260916, commented in full on 260916.
 *
 * @author Rowan Crowther
 */
public class UIEntryRenderer {
    /**
     * The renderer's name, as set from the data file's {@code name:} field
     * and looked up via {@code ui_entry_renderer_lookup} ({@code
     * ui-entry-renderers.c}).
     *
     * <p>Field name coded before 260916, commented in full on 260916.
     */
    private String name;
    /**
     * The built-in renderer kind this instance is based on, resolved from the
     * data file's {@code code:} field. Corresponds to the {@code backend}
     * pointer C's {@code parse_renderer_code} ({@code ui-entry-renderers.c})
     * sets into {@code struct renderer_info}.
     *
     * <p>Field code coded before 260916, commented in full on 260916.
     */
    private UIEntryRendererEnum code;
    /**
     * Per-state value colours, one colour code per character. Corresponds to
     * C's {@code colors} array ({@code ui-entry-renderers.c}) after
     * {@code augment_colors} has padded it out to the backend's default
     * length.
     *
     * <p>Field colours coded before 260916, commented in full on 260916.
     */
    private String colours;
    /**
     * Per-state label colours, one colour code per character. Corresponds to
     * C's {@code label_colors} array ({@code ui-entry-renderers.c}) after
     * {@code augment_colors} has padded it out to the backend's default
     * length.
     *
     * <p>Field labelColours coded before 260916, commented in full on 260916.
     */
    private String labelColours;
    /**
     * Per-state display symbols, one symbol per character. Corresponds to
     * C's {@code symbols} array ({@code ui-entry-renderers.c}) after
     * {@code augment_symbols} has padded it out to the backend's default
     * length.
     *
     * <p>Field symbols coded before 260916, commented in full on 260916.
     */
    private String symbols;
    /**
     * Number of digits used when rendering a numeric value. Corresponds to
     * C's {@code ndigit} ({@code ui-entry-renderers.c}), set from the data
     * file's {@code ndigit:} field or, when unset, the backend's
     * {@code default_ndigit}.
     *
     * <p>Field nDigit coded before 260916, commented in full on 260916.
     */
    private int nDigit;
    /**
     * The sign-display mode applied to this renderer's values. Corresponds
     * to C's {@code sign} ({@code ui-entry-renderers.c}), set from the data
     * file's {@code sign:} field or, when unset, the backend's
     * {@code default_sign}; C's {@code UI_ENTRY_SIGN_DEFAULT} placeholder is
     * always resolved to a concrete mode before this field is populated.
     *
     * <p>Field sign coded before 260916, commented in full on 260916.
     */
    private UIEntryEnum sign;

    /**
     * Build a renderer instance from its parsed fields. Corresponds to
     * populating one entry of C's {@code renderers} array ({@code
     * ui-entry-renderers.c}) once {@code finish_parse_ui_entry_renderer} has
     * run; every field here is expected to already carry its resolved,
     * defaulted value.
     *
     * <p>Function UIEntryRenderer coded before 260916, commented in full on 260916.
     *
     * @param name         renderer name
     * @param code         the built-in renderer kind it is based on
     * @param colours      per-state value colour table
     * @param labelColours per-state label colour table
     * @param symbols      per-state symbol table
     * @param nDigit       digit count for numeric rendering
     * @param sign         sign-display mode
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
     * Builds a debug string listing this renderer's fields, in declaration
     * order. Has no C counterpart — {@code struct renderer_info} has no
     * analogous dump routine — so this is a Java-side convenience for
     * logging and debugging only.
     *
     * <p>Function toString coded before 260916, commented in full on 260916.
     *
     * @return a debug string listing this renderer's fields
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
     * Returns this renderer's name, the value the data file's {@code name:}
     * field set and other records reference through the {@code renderer:}
     * field of {@code ui_entry.txt} entries.
     *
     * <p>Function getName coded before 260916, commented in full on 260916.
     *
     * @return this renderer's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the built-in renderer kind this instance is based on.
     * Corresponds to the resolved form of C's {@code backend} pointer
     * ({@code ui-entry-renderers.c}), set from the data file's {@code code:}
     * field.
     *
     * <p>Function getCode coded before 260916, commented in full on 260916.
     *
     * @return this renderer's built-in kind
     */
    public UIEntryRendererEnum getCode() {
        return code;
    }

    /**
     * Returns the per-state value colour table. Corresponds to C's
     * {@code colors} array ({@code ui-entry-renderers.c}) after default
     * augmentation.
     *
     * <p>Function getColours coded before 260916, commented in full on 260916.
     *
     * @return this renderer's value colour table
     */
    public String getColours() {
        return colours;
    }

    /**
     * Returns the per-state label colour table. Corresponds to C's
     * {@code label_colors} array ({@code ui-entry-renderers.c}) after
     * default augmentation.
     *
     * <p>Function getLabelColours coded before 260916, commented in full on 260916.
     *
     * @return this renderer's label colour table
     */
    public String getLabelColours() {
        return labelColours;
    }

    /**
     * Returns the per-state display symbol table. Corresponds to C's
     * {@code symbols} array ({@code ui-entry-renderers.c}) after default
     * augmentation.
     *
     * <p>Function getSymbols coded before 260916, commented in full on 260916.
     *
     * @return this renderer's symbol table
     */
    public String getSymbols() {
        return symbols;
    }

    /**
     * Returns the digit count used when rendering a numeric value.
     * Corresponds to C's {@code ndigit} ({@code ui-entry-renderers.c}) after
     * default resolution.
     *
     * <p>Function getnDigit coded before 260916, commented in full on 260916.
     *
     * @return this renderer's digit count
     */
    public int getnDigit() {
        return nDigit;
    }

    /**
     * Returns the sign-display mode applied to this renderer's values.
     * Corresponds to C's {@code sign} ({@code ui-entry-renderers.c}) after
     * default resolution; never {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT}.
     *
     * <p>Function getSign coded before 260916, commented in full on 260916.
     *
     * @return this renderer's sign-display mode
     */
    public UIEntryEnum getSign() {
        return sign;
    }
}