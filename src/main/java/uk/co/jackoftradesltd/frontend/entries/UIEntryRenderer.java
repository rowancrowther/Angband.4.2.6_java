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

import java.util.OptionalInt;

/**
 * A concrete, named renderer instance for a UI status entry — the data-file
 * counterpart of the built-in {@link UIEntryRendererEnum} defaults. Corresponds
 * to one element of C's {@code renderers} array, a {@code struct renderer_info}
 * ({@code ui-entry-renderers.c}).
 * <p>
 * Each of the colour table, label colour table, symbol table, digit count and
 * sign is held twice:
 * <ul>
 *   <li>a <em>raw</em> value — what the data file's {@code ui_entry_renderer}
 *       lines left in the C struct before {@code finish_parse_ui_entry_renderer}
 *       runs: the tables not yet padded by {@code augment_colors} /
 *       {@code augment_symbols}, {@code ndigit} possibly still {@code INT_MIN},
 *       {@code sign} possibly still {@code UI_ENTRY_SIGN_DEFAULT};</li>
 *   <li>a <em>resolved</em> value — the same field after
 *       {@code finish_parse_ui_entry_renderer} has filled it from the backend's
 *       defaults.</li>
 * </ul>
 * C needs only the one copy, because a repeated {@code name:} line reopens the
 * existing record ({@code parse_renderer_name} finds it with
 * {@code ui_entry_renderer_lookup}) and defaults are applied once, at finish.
 * {@code UIEntryRendererAssembler} resolves each record as it goes, so it keeps
 * the raw value to resolve again when a later record of the same name adds to
 * it. This class only stores what it is given; all resolution is done by the
 * assembler.
 * <p>
 * Representation differences from the C struct:
 * <ul>
 *   <li>{@code ncolors}, {@code nlabcolors} and {@code nsym} are not stored;
 *       they are the lengths of the corresponding strings.</li>
 *   <li>Colours are kept as colour-code characters, not as the attribute
 *       numbers C's {@code convert_chars_to_attrs} turns them into.</li>
 *   <li>{@code comb_rend_nm}, {@code combined_renderer_index},
 *       {@code combiner_index}, {@code units_label} and {@code units_nlabel}
 *       are not carried, as the {@code combine:}, {@code units:} and
 *       {@code combined-renderer:} directives are not parsed. With no
 *       {@code combine:} line C's finish takes the backend's
 *       {@code default_combiner_name}, which is what
 *       {@link UIEntryRendererEnum} already supplies through {@link #getCode()}.</li>
 * </ul>
 *
 * <p>Class UIEntryRenderer coded before 260916, commented in full on 261104.
 *
 * @author Rowan Crowther
 */
public class UIEntryRenderer {
    /**
     * The renderer's name, as set from the data file's {@code name:} field.
     * Corresponds to C's {@code name} ({@code struct renderer_info},
     * {@code ui-entry-renderers.c}), copied in by {@code parse_renderer_name}
     * and matched by {@code ui_entry_renderer_lookup}.
     *
     * <p>Field name coded before 260916, commented in full on 261104.
     */
    private String name;
    /**
     * The built-in renderer kind this instance is based on, resolved from the
     * data file's {@code code:} field. Corresponds to the {@code backend}
     * pointer C's {@code parse_renderer_code} ({@code ui-entry-renderers.c})
     * sets into {@code struct renderer_info}. C leaves {@code backend} NULL
     * when no {@code code:} line is seen, and finish then skips all default
     * resolution for the record; the assembler stands
     * {@link UIEntryRendererEnum#UI_ENTRY_RENDERER_NONE} in for that NULL.
     * {@code null} only between construction and the assembler's
     * {@link #setCode(UIEntryRendererEnum)} call.
     *
     * <p>Field code coded before 260916, commented in full on 261104.
     */
    private UIEntryRendererEnum code;

    /**
     * Per-state value colours as the data file gave them, one colour code per
     * character, before any padding from the backend's defaults. Corresponds
     * to C's {@code colors} / {@code ncolors} ({@code ui-entry-renderers.c})
     * as {@code parse_renderer_colors} leaves them, already cut to
     * {@code MAX_PALETTE}; an empty string where C has {@code NULL} and a
     * count of 0. Kept so a later record of the same name can be padded
     * afresh.
     *
     * <p>Field rawColours coded before 261104, commented in full on 261104.
     */
    private String rawColours;

    /**
     * Per-state value colours, one colour code per character. Corresponds to
     * C's {@code colors} array ({@code ui-entry-renderers.c}) after
     * {@code augment_colors} in {@code finish_parse_ui_entry_renderer} has
     * padded it out to the backend's {@code default_colors} length.
     *
     * <p>Field colours coded before 260916, commented in full on 261104.
     */
    private String colours;

    /**
     * Per-state label colours as the data file gave them, one colour code per
     * character, before any padding from the backend's defaults. Corresponds
     * to C's {@code label_colors} / {@code nlabcolors}
     * ({@code ui-entry-renderers.c}) as {@code parse_renderer_labelcolors}
     * leaves them, already cut to {@code MAX_PALETTE}; an empty string where C
     * has {@code NULL} and a count of 0.
     *
     * <p>Field rawLabelColours coded before 261104, commented in full on 261104.
     */
    private String rawLabelColours;

    /**
     * Per-state label colours, one colour code per character. Corresponds to
     * C's {@code label_colors} array ({@code ui-entry-renderers.c}) after
     * {@code augment_colors} in {@code finish_parse_ui_entry_renderer} has
     * padded it out to the backend's {@code default_labelcolors} length.
     *
     * <p>Field labelColours coded before 260916, commented in full on 261104.
     */
    private String labelColours;

    /**
     * Per-state display symbols as the data file gave them, one symbol per
     * character, before any padding from the backend's defaults. Corresponds
     * to C's {@code symbols} / {@code nsym} ({@code ui-entry-renderers.c}) as
     * {@code parse_renderer_symbols} leaves them; an empty string where C has
     * {@code NULL} and a count of 0.
     *
     * <p>Field rawSymbols coded before 261104, commented in full on 261104.
     */
    private String rawSymbols;

    /**
     * Per-state display symbols, one symbol per character. Corresponds to
     * C's {@code symbols} array ({@code ui-entry-renderers.c}) after
     * {@code augment_symbols} in {@code finish_parse_ui_entry_renderer} has
     * padded it out to the backend's {@code default_symbols} length.
     *
     * <p>Field symbols coded before 260916, commented in full on 261104.
     */
    private String symbols;
    /**
     * Number of digits used when rendering a numeric value. Corresponds to
     * C's {@code ndigit} ({@code ui-entry-renderers.c}) after
     * {@code finish_parse_ui_entry_renderer}: the data file's {@code ndigit:}
     * value or, when unset, the backend's {@code default_ndigit}. Empty only
     * where C's finish skips the record (no backend), leaving {@code ndigit}
     * at {@code INT_MIN}.
     *
     * <p>Field nDigit coded before 260916, commented in full on 261104.
     */
    private OptionalInt nDigit;

    /**
     * Number of digits as the data file gave it, before the backend's
     * default is applied. Corresponds to C's {@code ndigit}
     * ({@code ui-entry-renderers.c}) as {@code parse_renderer_ndigit} leaves
     * it; empty where C still holds the {@code INT_MIN} that
     * {@code parse_renderer_name} starts it at. {@code null} until the
     * assembler calls {@link #setRawNDigit(OptionalInt)}, as the constructor
     * does not set it.
     *
     * <p>Field rawNDigit coded before 261104, commented in full on 261104.
     */
    private OptionalInt rawNDigit;

    /**
     * The sign-display mode applied to this renderer's values. Corresponds
     * to C's {@code sign} ({@code ui-entry-renderers.c}) after
     * {@code finish_parse_ui_entry_renderer}: the data file's {@code sign:}
     * value or, when unset, the backend's {@code default_sign}.
     * {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT} only where C's finish skips
     * the record (no backend) and leaves the placeholder in place.
     *
     * <p>Field sign coded before 260916, commented in full on 261104.
     */
    private UIEntryEnum sign;

    /**
     * The sign-display mode as the data file gave it, before the backend's
     * default is applied. Corresponds to C's {@code sign}
     * ({@code ui-entry-renderers.c}) as {@code parse_renderer_sign} leaves it;
     * {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT} where no {@code sign:} line has
     * been seen, the value {@code parse_renderer_name} starts it at.
     * {@code null} until the assembler calls {@link #setRawSign(UIEntryEnum)},
     * as the constructor does not set it.
     *
     * <p>Field rawSign coded before 261104, commented in full on 261104.
     */
    private UIEntryEnum rawSign;

    /**
     * Build a renderer instance from its resolved fields. Corresponds in part
     * to C's {@code parse_renderer_name} ({@code ui-entry-renderers.c})
     * opening a new {@code struct renderer_info}. The raw fields are not
     * parameters and stay {@code null} until their setters are called;
     * {@code UIEntryRendererAssembler} constructs with
     * {@code null}s and an empty digit count, then fills every field, raw and
     * resolved, through the setters.
     *
     * <p>Function UIEntryRenderer coded before 260916, commented in full on 261104.
     *
     * @param name         renderer name
     * @param code         the built-in renderer kind it is based on
     * @param colours      resolved per-state value colour table
     * @param labelColours resolved per-state label colour table
     * @param symbols      resolved per-state symbol table
     * @param nDigit       resolved digit count for numeric rendering
     * @param sign         resolved sign-display mode
     */
    public UIEntryRenderer(String name,
                           UIEntryRendererEnum code,
                           String colours,
                           String labelColours,
                           String symbols,
                           OptionalInt nDigit,
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
     * Builds a debug string listing this renderer's resolved fields, in
     * declaration order; the raw fields are left out. Has no C counterpart —
     * {@code struct renderer_info} has no analogous dump routine — so this is
     * a Java-side convenience for logging and debugging only.
     *
     * <p>Function toString coded before 260916, commented in full on 261104.
     *
     * @return a debug string listing this renderer's resolved fields
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
     * field of {@code ui_entry.txt} entries. C's {@code ui_entry_renderer_get_name}
     * ({@code ui-entry-renderers.c}) returns the same string by index.
     *
     * <p>Function getName coded before 260916, commented in full on 261104.
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
     * field; {@link UIEntryRendererEnum#UI_ENTRY_RENDERER_NONE} where C's
     * pointer is NULL.
     *
     * <p>Function getCode coded before 260916, commented in full on 261104.
     *
     * @return this renderer's built-in kind
     */
    public UIEntryRendererEnum getCode() {
        return code;
    }

    /**
     * Returns the resolved per-state value colour table. Corresponds to C's
     * {@code colors} array ({@code ui-entry-renderers.c}) after
     * {@code augment_colors}.
     *
     * <p>Function getColours coded before 260916, commented in full on 261104.
     *
     * @return this renderer's resolved value colour table
     */
    public String getColours() {
        return colours;
    }

    /**
     * Returns the resolved per-state label colour table. Corresponds to C's
     * {@code label_colors} array ({@code ui-entry-renderers.c}) after
     * {@code augment_colors}.
     *
     * <p>Function getLabelColours coded before 260916, commented in full on 261104.
     *
     * @return this renderer's resolved label colour table
     */
    public String getLabelColours() {
        return labelColours;
    }

    /**
     * Returns the resolved per-state display symbol table. Corresponds to C's
     * {@code symbols} array ({@code ui-entry-renderers.c}) after
     * {@code augment_symbols}.
     *
     * <p>Function getSymbols coded before 260916, commented in full on 261104.
     *
     * @return this renderer's resolved symbol table
     */
    public String getSymbols() {
        return symbols;
    }

    /**
     * Returns the resolved digit count used when rendering a numeric value.
     * Corresponds to C's {@code ndigit} ({@code ui-entry-renderers.c}) after
     * {@code finish_parse_ui_entry_renderer}; empty where C leaves
     * {@code INT_MIN}.
     *
     * <p>Function getnDigit coded before 260916, commented in full on 261104.
     *
     * @return this renderer's resolved digit count
     */
    public OptionalInt getnDigit() {
        return nDigit;
    }

    /**
     * Returns the resolved sign-display mode applied to this renderer's
     * values. Corresponds to C's {@code sign} ({@code ui-entry-renderers.c})
     * after {@code finish_parse_ui_entry_renderer}; it is
     * {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT} only for a renderer with no
     * backend, which C's finish skips.
     *
     * <p>Function getSign coded before 260916, commented in full on 261104.
     *
     * @return this renderer's resolved sign-display mode
     */
    public UIEntryEnum getSign() {
        return sign;
    }

    /**
     * Sets the built-in renderer kind. Corresponds to C's
     * {@code parse_renderer_code} ({@code ui-entry-renderers.c}) setting
     * {@code backend}.
     *
     * <p>Function setCode coded before 261104, commented in full on 261104.
     *
     * @param code the built-in renderer kind
     */
    public void setCode(UIEntryRendererEnum code) {
        this.code = code;
    }

    /**
     * Sets the resolved per-state value colour table.
     *
     * <p>Function setColours coded before 261104, commented in full on 261104.
     *
     * @param colours the resolved value colour table
     */
    public void setColours(String colours) {
        this.colours = colours;
    }

    /**
     * Sets the resolved per-state label colour table.
     *
     * <p>Function setLabelColours coded before 261104, commented in full on 261104.
     *
     * @param labelColours the resolved label colour table
     */
    public void setLabelColours(String labelColours) {
        this.labelColours = labelColours;
    }

    /**
     * Sets the renderer's name.
     *
     * <p>Function setName coded before 261104, commented in full on 261104.
     *
     * @param name the renderer's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the resolved digit count.
     *
     * <p>Function setnDigit coded before 261104, commented in full on 261104.
     *
     * @param nDigit the resolved digit count; empty where C leaves {@code INT_MIN}
     */
    public void setnDigit(OptionalInt nDigit) {
        this.nDigit = nDigit;
    }

    /**
     * Sets the resolved sign-display mode.
     *
     * <p>Function setSign coded before 261104, commented in full on 261104.
     *
     * @param sign the resolved sign-display mode
     */
    public void setSign(UIEntryEnum sign) {
        this.sign = sign;
    }

    /**
     * Sets the resolved per-state display symbol table.
     *
     * <p>Function setSymbols coded before 261104, commented in full on 261104.
     *
     * @param symbols the resolved symbol table
     */
    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    /**
     * Returns the value colour table as the data file gave it, before
     * padding; see the {@code rawColours} field.
     *
     * <p>Function getRawColours coded before 261104, commented in full on 261104.
     *
     * @return the raw value colour table, empty when none was given
     */
    public String getRawColours() {
        return rawColours;
    }

    /**
     * Sets the raw value colour table; see the {@code rawColours} field.
     *
     * <p>Function setRawColours coded before 261104, commented in full on 261104.
     *
     * @param rawColours the raw value colour table
     */
    public void setRawColours(String rawColours) {
        this.rawColours = rawColours;
    }

    /**
     * Returns the label colour table as the data file gave it, before
     * padding; see the {@code rawLabelColours} field.
     *
     * <p>Function getRawLabelColours coded before 261104, commented in full on 261104.
     *
     * @return the raw label colour table, empty when none was given
     */
    public String getRawLabelColours() {
        return rawLabelColours;
    }

    /**
     * Sets the raw label colour table; see the {@code rawLabelColours} field.
     *
     * <p>Function setRawLabelColours coded before 261104, commented in full on 261104.
     *
     * @param rawLabelColours the raw label colour table
     */
    public void setRawLabelColours(String rawLabelColours) {
        this.rawLabelColours = rawLabelColours;
    }

    /**
     * Returns the symbol table as the data file gave it, before padding;
     * see the {@code rawSymbols} field.
     *
     * <p>Function getRawSymbols coded before 261104, commented in full on 261104.
     *
     * @return the raw symbol table, empty when none was given
     */
    public String getRawSymbols() {
        return rawSymbols;
    }

    /**
     * Sets the raw symbol table; see the {@code rawSymbols} field.
     *
     * <p>Function setRawSymbols coded before 261104, commented in full on 261104.
     *
     * @param rawSymbols the raw symbol table
     */
    public void setRawSymbols(String rawSymbols) {
        this.rawSymbols = rawSymbols;
    }

    /**
     * Returns the digit count as the data file gave it, before the backend's
     * default; see the {@code rawNDigit} field.
     *
     * <p>Function getRawNDigit coded before 261104, commented in full on 261104.
     *
     * @return the raw digit count, empty where C holds {@code INT_MIN};
     *         {@code null} if never set
     */
    public OptionalInt getRawNDigit() {
        return rawNDigit;
    }

    /**
     * Sets the raw digit count; see the {@code rawNDigit} field.
     *
     * <p>Function setRawNDigit coded before 261104, commented in full on 261104.
     *
     * @param rawNDigit the raw digit count, empty for none given
     */
    public void setRawNDigit(OptionalInt rawNDigit) {
        this.rawNDigit = rawNDigit;
    }

    /**
     * Returns the sign-display mode as the data file gave it, before the
     * backend's default; see the {@code rawSign} field.
     *
     * <p>Function getRawSign coded before 261104, commented in full on 261104.
     *
     * @return the raw sign mode, {@link UIEntryEnum#UI_ENTRY_SIGN_DEFAULT}
     *         when none was given; {@code null} if never set
     */
    public UIEntryEnum getRawSign() {
        return rawSign;
    }

    /**
     * Sets the raw sign-display mode; see the {@code rawSign} field.
     *
     * <p>Function setRawSign coded before 261104, commented in full on 261104.
     *
     * @param rawSign the raw sign mode
     */
    public void setRawSign(UIEntryEnum rawSign) {
        this.rawSign = rawSign;
    }
}