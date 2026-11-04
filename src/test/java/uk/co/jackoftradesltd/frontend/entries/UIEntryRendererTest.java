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

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryRenderer}, checked against the
 * {@code struct renderer_info} states C's parser produces in
 * {@code ui-entry-renderers.c} (functions {@code parse_renderer_name},
 * {@code parse_renderer_colors}, {@code finish_parse_ui_entry_renderer},
 * {@code augment_colors}) and the {@code UI_ENTRY_RENDERER} table entries in
 * {@code list-ui-entry-renderers.h}.
 *
 * <p>{@link UIEntryRenderer} has no control flow of its own — it is a field
 * carrier — so these tests check that each raw and resolved field stores and
 * returns exactly what it is given, independently of its partner, using values
 * worked out from the C table and C's padding rule rather than from the Java
 * enum or assembler.
 *
 * @author Rowan Crowther
 */
class UIEntryRendererTest {

    // ---- NUMERIC_RENDERER_WITH_COMBINED_AUX defaults (list-ui-entry-renderers.h) -----------------

    @Test
    void gettersReturnTheConstructorArgumentsForANumericRenderer() {
        UIEntryRenderer r = new UIEntryRenderer(
                "HP",
                UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX,
                "wwwboBbPrRowwwboBbPrRo",
                "swwwBBBrrr",
                "?0000+-",
                OptionalInt.of(1),
                UIEntryEnum.UI_ENTRY_NO_SIGN);

        assertEquals("HP", r.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX, r.getCode());
        assertEquals("wwwboBbPrRowwwboBbPrRo", r.getColours());
        assertEquals("swwwBBBrrr", r.getLabelColours());
        assertEquals("?0000+-", r.getSymbols());
        assertEquals(OptionalInt.of(1), r.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getSign());
    }

    // ---- COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX defaults (list-ui-entry-renderers.h) ----------
    // Zero-digit renderer, so this also exercises nDigit == 0 as a legitimate stored value
    // rather than treating it as unset (C's own "unset" sentinel is INT_MIN, not 0).

    @Test
    void gettersReturnTheConstructorArgumentsForAZeroDigitRenderer() {
        UIEntryRenderer r = new UIEntryRenderer(
                "RES_ACID",
                UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX,
                "wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW",
                "swBrgwBrwBwBr",
                "?..+-*!^.=.%%%~!=%~+=~",
                OptionalInt.of(0),
                UIEntryEnum.UI_ENTRY_NO_SIGN);

        assertEquals(OptionalInt.of(0), r.getnDigit());
        assertEquals("?..+-*!^.=.%%%~!=%~+=~", r.getSymbols());
    }

    // ---- Construction leaves the raw half unset ---------------------------------------------------

    @Test
    void rawFieldsAreNullUntilTheirSettersAreCalled() {
        UIEntryRenderer r = new UIEntryRenderer("HP", null, null, null, null, OptionalInt.empty(), null);

        assertNull(r.getRawColours());
        assertNull(r.getRawLabelColours());
        assertNull(r.getRawSymbols());
        assertNull(r.getRawNDigit());
        assertNull(r.getRawSign());
    }

    // ---- A partial colors: line, as finish_parse_ui_entry_renderer leaves it ----------------------
    // name:X / code:NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX / colors:rG
    // parse_renderer_colors stores "rG" (ncolors 2); augment_colors copies default chars 2..21 of
    // "wwwGowGowGoWWWGoWGoWGo" after it. No ndigit:/sign: lines, so C's INT_MIN and
    // UI_ENTRY_SIGN_DEFAULT are replaced by default_ndigit 0 and NO_SIGN.

    @Test
    void rawAndResolvedValuesForAPartialColourLineAreHeldSeparately() {
        UIEntryRenderer r = new UIEntryRenderer("X", null, null, null, null, OptionalInt.empty(), null);
        r.setCode(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX);
        r.setRawColours("rG");
        r.setColours("rGwGowGowGoWWWGoWGoWGo");
        r.setRawLabelColours("");
        r.setLabelColours("swwwBBBrrr");
        r.setRawSymbols("");
        r.setSymbols("?....+!+--=");
        r.setRawNDigit(OptionalInt.empty());
        r.setnDigit(OptionalInt.of(0));
        r.setRawSign(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT);
        r.setSign(UIEntryEnum.UI_ENTRY_NO_SIGN);

        assertEquals("rG", r.getRawColours());
        assertEquals("rGwGowGowGoWWWGoWGoWGo", r.getColours());
        assertEquals(22, r.getColours().length());
        assertEquals("", r.getRawLabelColours());
        assertEquals("swwwBBBrrr", r.getLabelColours());
        assertEquals("", r.getRawSymbols());
        assertEquals("?....+!+--=", r.getSymbols());
        assertEquals(OptionalInt.empty(), r.getRawNDigit());
        assertEquals(OptionalInt.of(0), r.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT, r.getRawSign());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getSign());
    }

    // ---- No code: line — C's finish skips the record, leaving INT_MIN and SIGN_DEFAULT ------------

    @Test
    void aRendererWithNoBackendKeepsTheUnresolvedPlaceholders() {
        UIEntryRenderer r = new UIEntryRenderer("X", null, null, null, null, OptionalInt.empty(), null);
        r.setCode(UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE);
        r.setRawColours("");
        r.setColours("");
        r.setRawNDigit(OptionalInt.empty());
        r.setnDigit(OptionalInt.empty());
        r.setRawSign(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT);
        r.setSign(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT);

        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE, r.getCode());
        assertEquals("", r.getColours());
        assertTrue(r.getnDigit().isEmpty());
        assertEquals(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT, r.getSign());
    }

    // ---- Explicit ndigit:/sign: lines are kept as given, in both halves -----------------------------

    @Test
    void explicitDigitAndSignAreStoredInBothHalves() {
        UIEntryRenderer r = new UIEntryRenderer("X", null, null, null, null, OptionalInt.empty(), null);
        r.setRawNDigit(OptionalInt.of(3));
        r.setnDigit(OptionalInt.of(3));
        r.setRawSign(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN);
        r.setSign(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN);

        assertEquals(OptionalInt.of(3), r.getRawNDigit());
        assertEquals(OptionalInt.of(3), r.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, r.getRawSign());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, r.getSign());
    }

    // ---- A raw setter never touches its resolved partner, and vice versa --------------------------

    @Test
    void rawAndResolvedSettersAreIndependent() {
        UIEntryRenderer r = new UIEntryRenderer("X", null, "res", "lab", "sym", OptionalInt.of(1),
                UIEntryEnum.UI_ENTRY_NO_SIGN);
        r.setRawColours("r");
        r.setRawLabelColours("l");
        r.setRawSymbols("s");
        r.setRawNDigit(OptionalInt.of(9));
        r.setRawSign(UIEntryEnum.UI_ENTRY_NEGATIVE_SIGN);

        assertEquals("res", r.getColours());
        assertEquals("lab", r.getLabelColours());
        assertEquals("sym", r.getSymbols());
        assertEquals(OptionalInt.of(1), r.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getSign());

        r.setColours("RES");
        assertEquals("r", r.getRawColours());
    }

    // ---- A later line replaces, as parse_renderer_colors frees and reallocates --------------------

    @Test
    void aSecondSetReplacesTheFirst() {
        UIEntryRenderer r = new UIEntryRenderer("X", null, null, null, null, OptionalInt.empty(), null);
        r.setRawColours("rG");
        r.setRawColours("b");
        r.setName("Y");
        r.setCode(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX);
        r.setCode(UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX);

        assertEquals("b", r.getRawColours());
        assertEquals("Y", r.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX, r.getCode());
    }

    // ---- toString(): Java-side convenience, no C counterpart ---------------------------------------

    @Test
    void toStringIncludesTheNameAndDoesNotThrowWithNullFields() {
        UIEntryRenderer r = new UIEntryRenderer("HP", null, null, null, null, OptionalInt.of(1), null);

        assertTrue(r.toString().contains("HP"));
    }

    @Test
    void toStringListsResolvedFieldsOnly() {
        UIEntryRenderer r = new UIEntryRenderer("HP", null, "res", null, null, OptionalInt.of(1), null);
        r.setRawColours("rawvalue");

        String s = r.toString();
        assertTrue(s.contains("colours='res'"));
        assertFalse(s.contains("rawvalue"));
    }
}
