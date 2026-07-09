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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererAssembler;
import uk.co.jackoftrades.backend.parser.uientryrenderer.UIEntryRendererParseRecord;
import uk.co.jackoftrades.frontend.entries.UIEntryRenderer;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftrades.frontend.entries.enums.UIEntryRendererEnum;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryRendererAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link UIEntryRendererParseRecord}s directly (no grammar/reader), so they
 * exercise the assembler in isolation - including the decision-(b) empty-field fallbacks, which
 * the current grammar cannot yet emit. Behaviour asserted: {@code code}/{@code sign} enum
 * resolution, backend-default fallback for every optional field, and skip-and-continue (a record
 * whose required resolution fails is dropped with an error, the rest survive).
 *
 * @author Rowan Crowther
 */
class UIEntryRendererAssemblerTest {

    /**
     * A code whose value/label/symbol/digit/sign defaults are all distinct, so a wrong-default
     * fallback (e.g. label taking the value-colour default) is detectable.
     */
    private static final UIEntryRendererEnum FLAG =
            UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX;

    private static UIEntryRendererParseRecord rec(String name, String code, String colours,
                                                  String labelColours, String symbols,
                                                  String nDigits, String sign) {
        return new UIEntryRendererParseRecord("1", name, code, colours, labelColours, symbols, nDigits, sign);
    }

    @Test
    void resolvesAllFieldsWhenPresent() {
        List<String> errors = new ArrayList<>();
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(
                List.of(rec("r", "NUMERIC_RENDERER_WITH_BOOL_AUX", "abc", "def", "ghi", "3", "ALWAYS_SIGN")),
                errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntryRenderer u = out.get(0);
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, u.getCode());
        assertEquals("abc", u.getColours());
        assertEquals("def", u.getLabelColours());
        assertEquals("ghi", u.getSymbols());
        assertEquals(3, u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, u.getSign());
    }

    @Test
    void emptyOptionalFieldsFallBackToBackendDefaults() {
        List<String> errors = new ArrayList<>();
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(
                List.of(rec("r", "COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX", "", "", "", "", "")),
                errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntryRenderer u = out.get(0);
        assertEquals(FLAG.getDefaultColours(), u.getColours());
        assertEquals(FLAG.getDefaultLabelColours(), u.getLabelColours());   // must NOT be the value-colour default
        assertEquals(FLAG.getDefaultSymbols(), u.getSymbols());
        assertEquals(FLAG.getDefaultDigits(), u.getnDigit());
        assertEquals(FLAG.getEntry(), u.getSign());
    }

    @Test
    void unknownCodeIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(
                List.of(rec("r", "NOTACODE", "", "", "", "", "")), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("NOTACODE")), errors::toString);
    }

    @Test
    void presentButInvalidSignIsSkippedAndReported() {
        List<String> errors = new ArrayList<>();
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(
                List.of(rec("r", "COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX", "", "", "", "", "BOGUS")), errors);

        assertTrue(out.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("sign") && e.contains("BOGUS")), errors::toString);
    }

    @Test
    void partialResultsSurviveABadRecord() {
        List<String> errors = new ArrayList<>();
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(
                List.of(rec("bad", "NOTACODE", "", "", "", "", ""),
                        rec("good", "COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX", "", "", "", "", "")),
                errors);

        assertEquals(1, out.size());
        assertEquals("good", out.get(0).getName());
        assertFalse(errors.isEmpty());
    }
}
