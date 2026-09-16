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

package uk.co.jackoftradesltd.frontend.entries.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryRendererEnum}, checked against the
 * {@code UI_ENTRY_RENDERER} table entries in C's
 * {@code src/list-ui-entry-renderers.h}, not against the Java source, so a
 * transcription error in the enum's packed colour/symbol strings would still
 * be visible here.
 *
 * <p>{@link UIEntryRendererEnum} has no getter for its {@code combiner}
 * field, so the combiner column of the C table (RESIST_0, LOGICAL_OR,
 * LOGICAL_OR_WITH_CANCEL, ADD, ADD, ADD) is not independently checkable here.
 *
 * @author Rowan Crowther
 */
class UIEntryRendererEnumTest {

    // ---- COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX: list-ui-entry-renderers.h line 13 --------------

    @Test
    void compactResistRendererMatchesTheCTableRow() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX;

        assertEquals("wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW", r.getDefaultColours());
        assertEquals("swBrgwBrwBwBr", r.getDefaultLabelColours());
        assertEquals("?..+-*!^.=.%%%~!=%~+=~", r.getDefaultSymbols());
        assertEquals(0, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX: list-ui-entry-renderers.h line 14 ----------------

    @Test
    void compactFlagRendererMatchesTheCTableRow() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX;

        assertEquals("wwwwGWWWWG", r.getDefaultColours());
        assertEquals("swBw", r.getDefaultLabelColours());
        assertEquals("?..+!", r.getDefaultSymbols());
        assertEquals(0, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- COMPACT_FLAG_WITH_CANCEL_RENDERER_WITH_COMBINED_AUX: line 15 ------------------------------

    @Test
    void compactFlagWithCancelRendererMatchesTheCTableRow() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_WITH_CANCEL_RENDERER_WITH_COMBINED_AUX;

        assertEquals("wwwwwGwwGGwWWWWWGWWGGW", r.getDefaultColours());
        assertEquals("swwwwBw", r.getDefaultLabelColours());
        assertEquals("?..+-!+-=.-", r.getDefaultSymbols());
        assertEquals(0, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX: line 16 ---------------------------------------

    @Test
    void numericAsSignRendererMatchesTheCTableRow() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX;

        assertEquals("wwwGowGowGoWWWGoWGoWGo", r.getDefaultColours());
        assertEquals("swwwBBBrrr", r.getDefaultLabelColours());
        assertEquals("?....+!+--=", r.getDefaultSymbols());
        assertEquals(0, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- NUMERIC_RENDERER_WITH_COMBINED_AUX: line 17 (1-digit boundary) ----------------------------

    @Test
    void numericRendererMatchesTheCTableRow() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX;

        assertEquals("wwwboBbPrRowwwboBbPrRo", r.getDefaultColours());
        assertEquals("swwwBBBrrr", r.getDefaultLabelColours());
        assertEquals("?0000+-", r.getDefaultSymbols());
        assertEquals(1, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- NUMERIC_RENDERER_WITH_BOOL_AUX: line 18 (1-digit boundary) --------------------------------

    @Test
    void numericRendererWithBoolAuxMatchesTheCTableRow() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX;

        assertEquals("wdsgGgrRwdsgGgrR", r.getDefaultColours());
        assertEquals("wwwwwww", r.getDefaultLabelColours());
        assertEquals("? .s*=", r.getDefaultSymbols());
        assertEquals(1, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- UI_ENTRY_RENDERER_NONE: Java-only sentinel for C's invalid index 0 ------------------------
    // C's list-ui-entry-renderers.h has no row for this; ui_entry_renderer_get_min_index() returning 1
    // is what leaves 0 (and so this sentinel) meaning "no renderer".

    @Test
    void noneSentinelCarriesEmptyTables() {
        UIEntryRendererEnum r = UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE;

        assertEquals("", r.getDefaultColours());
        assertEquals("", r.getDefaultLabelColours());
        assertEquals("", r.getDefaultSymbols());
        assertEquals(0, r.getDefaultDigits());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getEntry());
    }

    // ---- Distinctness: a packed-string typo could accidentally collide two renderers ---------------

    @Test
    void allSevenConstantsExist() {
        assertEquals(7, UIEntryRendererEnum.values().length);
    }
}
