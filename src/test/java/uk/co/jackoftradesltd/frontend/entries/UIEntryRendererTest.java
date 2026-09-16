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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryRenderer}, checked against the finished,
 * post-default {@code struct renderer_info} values C's
 * {@code finish_parse_ui_entry_renderer} ({@code src/ui-entry-renderers.c})
 * produces and the {@code UI_ENTRY_RENDERER} table entries in
 * {@code src/list-ui-entry-renderers.h}.
 *
 * <p>{@link UIEntryRenderer} has no control flow of its own — the class is a
 * plain field carrier — so these tests only check that the constructor stores
 * and the getters return exactly what they were given, using values pulled
 * straight from the C default table rather than the Java enum, so a mismatch
 * between {@link UIEntryRendererEnum}'s defaults and the C table would still
 * be visible in a test built against this class.
 *
 * @author Rowan Crowther
 */
class UIEntryRendererTest {

    // ---- NUMERIC_RENDERER_WITH_COMBINED_AUX: list-ui-entry-renderers.h line 17 --------------------

    @Test
    void gettersReturnTheConstructorArgumentsForANumericRenderer() {
        UIEntryRenderer r = new UIEntryRenderer(
                "HP",
                UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX,
                "wwwboBbPrRowwwboBbPrRo",
                "swwwBBBrrr",
                "?0000+-",
                1,
                UIEntryEnum.UI_ENTRY_NO_SIGN);

        assertEquals("HP", r.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_COMBINED_AUX, r.getCode());
        assertEquals("wwwboBbPrRowwwboBbPrRo", r.getColours());
        assertEquals("swwwBBBrrr", r.getLabelColours());
        assertEquals("?0000+-", r.getSymbols());
        assertEquals(1, r.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, r.getSign());
    }

    // ---- COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX: list-ui-entry-renderers.h line 13 --------------
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
                0,
                UIEntryEnum.UI_ENTRY_NO_SIGN);

        assertEquals(0, r.getnDigit());
        assertEquals("?..+-*!^.=.%%%~!=%~+=~", r.getSymbols());
    }

    // ---- toString(): Java-side convenience, no C counterpart ---------------------------------------

    @Test
    void toStringIncludesTheNameAndDoesNotThrowWithNullFields() {
        UIEntryRenderer r = new UIEntryRenderer("HP", null, null, null, null, 1, null);

        assertTrue(r.toString().contains("HP"));
    }
}
