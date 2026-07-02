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

package uk.co.jackoftrades.backend.parser.uientryrenderer;

import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.TestOnly;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

/**
 * Thin parse-DTO produced by the UIEntryRenderer reader for a single
 * UIEntryRenderer value in lib/gamedata/ui_entry_renderer.txt.
 *
 * <p>This class is the boundary between the grammar layer and the reader layer.
 * The grammar's only job is to extract raw text from the data file and populate
 * this record - no enum resolution, no domain object construction, no calls to
 * GameConstants. All of that work happens in the reader (UIEntryRendererReader),
 * which converts a {@code List<UIEntryRendererParseRecord>} into the real domain
 * objects once the domain API is stable enough to run.</p>
 *
 * <p>A DTO rather than returning the domain values directly
 * from the grammar, as the domain model is still being ported and its
 * constructors change frequently. Design decision to un-couple a grammar
 * from a changing constructor. The DTO decouples the two: the grammar is
 * "done" once it parses correctly, regardless of whether the domain class
 * it feeds is ready.</p>
 *
 * <p>Field types are kept as primitives and Strings wherever possible.</p>
 *
 * @author Rowan Crowther
 */
public class UIEntryRendererParseRecord {
    /**
     * Line number where this renderer record starts in lib/gamedata/ui_entry_renderer.txt
     */
    private int lineNumber;

    /**
     * Holder for name of this renderer
     */
    private String name;

    /**
     * Holder for code of this renderer
     */
    private UIEntryRendererEnum code;

    /**
     * Holder for list of colours of this renderer
     */
    private String colours;

    /**
     * Holder for list of label colours of this renderer
     */
    private String labelColours;

    /**
     * Holder for list of symbols of this renderer
     */
    private String symbols;

    /**
     * Holder for optional number of digits to display for this renderer
     */
    private int nDigits;

    /**
     * Holder for how this renderer displays any relevant sign
     */
    private UIEntryEnum sign;

    /**
     * Constructor for a UI Entry Renderer
     *
     * @param lineNumber   line number in lib/gamedata/ui_entry_renderer.txt
     * @param name         name of the UI Entry Renderer, used to link with an
     *                     interface in ui_entry.txt
     * @param code         binds the renderer to a specific backend in the
     *                     executable. Must be one of the enums in UIEntryRendererEnum.
     * @param colours      Configures the palette of colours used for the value
     * @param labelColours Configures the palette of colours used for the
     *                     label
     * @param symbols      the palette of symbols the renderer uses if it converts
     *                     values to symbols
     * @param nDigits      the number of digits to display for a numeric value
     * @param sign         whether signs are shown with numeric values - if it is
     *                     not set the backend default will be used. Must be one of
     *                     the values from UIEntryEnum
     * @author Rowan Crowther
     */
    public UIEntryRendererParseRecord(int lineNumber, String name, UIEntryRendererEnum code, String colours,
                                      String labelColours, String symbols, int nDigits, UIEntryEnum sign) {
        this.lineNumber = lineNumber;
        this.name = name;
        this.code = code;
        this.colours = colours;
        this.labelColours = labelColours;
        this.symbols = symbols;
        this.nDigits = nDigits;
        this.sign = sign;
    }

    /**
     * Getter for the line number
     *
     * @return the line number in the lib/gamedata/ui_entry_renderer.txt file
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Getter for the name of this renderer
     *
     * @return the name of this renderer
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public String getName() {
        return name;
    }

    /**
     * Getter for the code of this renderer
     *
     * @return the code of this renderer
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public UIEntryRendererEnum getCode() {
        return code;
    }

    /**
     * Getter for the colour palette of this renderer's values
     *
     * @return the colour palette of this renderer's values
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public String getColours() {
        return colours;
    }

    /**
     * Getter for the colour palette of this renderer's labels
     *
     * @return the colour palette of this renderer's labels
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public String getLabelColours() {
        return labelColours;
    }

    /**
     * Getter for the symbols of this renderer
     *
     * @return the symbols of this renderer
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public String getSymbols() {
        return symbols;
    }

    /**
     * Getter for the number of digits of this renderer's numeric values
     *
     * @return the number of digits of this renderer's numeric values
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public int getnDigits() {
        return nDigits;
    }

    /**
     * Getter for the display of numeric sign of this renderer
     *
     * @return the display of numeric sign of this renderer
     * @author Rowan Crowther
     */
    @TestOnly
    @CheckReturnValue
    @Contract(pure = true)
    public UIEntryEnum getSign() {
        return sign;
    }
}