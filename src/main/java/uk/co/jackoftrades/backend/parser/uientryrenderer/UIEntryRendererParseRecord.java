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

/**
 * Thin parse-DTO produced by the UIEntryRenderer reader for a single
 * UIEntryRenderer value in lib/gamedata/ui_entry_renderer.txt.
 *
 * <p>This class is the boundary between the grammar layer and the reader layer.
 * The grammar's only job is to extract raw text from the data file and populate
 * this record - no enum resolution, no domain object construction, no calls to
 * GameConstants. All of that work happens in the assembler
 * {@link UIEntryRendererAssembler},
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
 *
 * @author Rowan Crowther
 */
public record UIEntryRendererParseRecord(int lineNumber, String name, String code,
                                         String colours, String labelColours, String symbols,
                                         String nDigits, String sign) {
}