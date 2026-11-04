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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererAssembler;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererParseRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UIEntryRendererAssembler} (grammar-suite assembler track).
 *
 * <p>These construct {@link UIEntryRendererParseRecord}s directly (no grammar/reader), so they
 * exercise the assembler in isolation - including the decision-(b) empty-field fallbacks, which
 * the current grammar cannot yet emit. Behaviour asserted: {@code code}/{@code sign} enum
 * resolution, backend-default fallback for every optional field, and the fatal refusal of a bad
 * {@code code}, {@code sign} or {@code ndigit} (soft errors were made fatal). Expected values come
 * from {@code ui-entry-renderers.c}: the {@code parse_renderer_*} callbacks for what a single block
 * sets, {@code parse_renderer_name} for list order and same-name merging, and
 * {@code finish_parse_ui_entry_renderer} for the backend padding and defaults applied to the merged
 * record. A block with no {@code code:} has a {@code NULL} backend in C, which the port represents
 * as {@code UI_ENTRY_RENDERER_NONE}.
 *
 * <p>Class UIEntryRendererAssemblerTest coded before 261104, extended and commented in full on
 * 261104 (list order, same-name merging, palette cap and case-sensitivity tests added).
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
                errors, "test.txt");

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntryRenderer u = out.get(0);
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, u.getCode());
        // Palettes shorter than the backend default are topped up from it at the same index
        // (augment_colors / augment_symbols in ui-entry-renderers.c): defaults are
        // "wdsgGgrRwdsgGgrR", "wwwwwww" and "? .s*=".
        assertEquals("abcgGgrRwdsgGgrR", u.getColours());
        assertEquals("defwwww", u.getLabelColours());
        assertEquals("ghis*=", u.getSymbols());
        assertEquals(OptionalInt.of(3), u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, u.getSign());
    }

    @Test
    void emptyOptionalFieldsFallBackToBackendDefaults() {
        List<String> errors = new ArrayList<>();
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(
                List.of(rec("r", "COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX", "", "", "", "", "")),
                errors, "test.txt");

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, out.size());
        UIEntryRenderer u = out.get(0);
        assertEquals(FLAG.getDefaultColours(), u.getColours());
        assertEquals(FLAG.getDefaultLabelColours(), u.getLabelColours());   // must NOT be the value-colour default
        assertEquals(FLAG.getDefaultSymbols(), u.getSymbols());
        assertEquals(OptionalInt.of(FLAG.getDefaultDigits()), u.getnDigit());
        assertEquals(FLAG.getEntry(), u.getSign());
    }

    private static DataFileParseException fatal(UIEntryRendererParseRecord... records) {
        return assertThrows(DataFileParseException.class,
                () -> new UIEntryRendererAssembler().assemble(List.of(records), new ArrayList<>(), "test.txt"));
    }

    private static UIEntryRenderer one(UIEntryRendererParseRecord record) {
        List<UIEntryRenderer> out = new UIEntryRendererAssembler()
                .assemble(List.of(record), new ArrayList<>(), "test.txt");
        assertEquals(1, out.size());
        return out.get(0);
    }

    private static final String FLAG_CODE = "COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX";

    // ---- code (C: parse_renderer_code, lookup_backend_by_name) -------------------------------

    @Test
    void unknownCodeIsFatal() {
        DataFileParseException e = fatal(rec("r", "NOTACODE", "", "", "", "", ""));
        assertTrue(e.getMessage().contains("NOTACODE"), e::getMessage);
    }

    @Test
    void theNoneSentinelIsNotABackendName() {
        // C's backend_names has only the six real rows; NONE is Java-only.
        DataFileParseException e = fatal(rec("r", "NONE", "", "", "", "", ""));
        assertTrue(e.getMessage().contains("NONE"), e::getMessage);
    }

    // ---- sign (C: parse_renderer_sign) --------------------------------------------------------

    @Test
    void presentButInvalidSignIsFatal() {
        DataFileParseException e = fatal(rec("r", FLAG_CODE, "", "", "", "", "BOGUS"));
        assertTrue(e.getMessage().contains("sign") && e.getMessage().contains("BOGUS"), e::getMessage);
    }

    @Test
    void signDefaultIsNotAcceptableInAFile() {
        // C accepts only NO_SIGN, ALWAYS_SIGN and NEGATIVE_SIGN; the message must quote the sign.
        DataFileParseException e = fatal(rec("r", FLAG_CODE, "", "", "", "", "SIGN_DEFAULT"));
        assertTrue(e.getMessage().contains("SIGN_DEFAULT"), e::getMessage);
    }

    // ---- ndigit (C: parse_renderer_ndigit, strtol base 0) ------------------------------------

    @Test
    void ndigitBelowOneIsFatalWhateverTheCode() {
        for (String code : List.of(FLAG_CODE, "")) {
            for (String bad : List.of("0", "-3")) {
                DataFileParseException e = fatal(rec("r", code, "", "", "", bad, ""));
                assertTrue(e.getMessage().contains("number of digits") && e.getMessage().contains(bad),
                        "code '" + code + "' ndigit " + bad + ": " + e.getMessage());
            }
        }
    }

    @Test
    void ndigitIsReadInBaseZeroAsStrtolDoes() {
        // strtol(tok, &z, 0): 0x10 is 16, 010 is 8.
        assertEquals(OptionalInt.of(16), one(rec("r", "NUMERIC_RENDERER_WITH_BOOL_AUX", "", "", "", "0x10", "")).getnDigit());
        assertEquals(OptionalInt.of(8), one(rec("r", "NUMERIC_RENDERER_WITH_BOOL_AUX", "", "", "", "010", "")).getnDigit());
    }

    @Test
    void ndigitOfEightWithALeadingZeroReadsAsZeroAndIsRefused() {
        // strtol("08", &z, 0) reads the octal prefix 0, stops at 8, and gives 0, which is below 1.
        fatal(rec("r", "NUMERIC_RENDERER_WITH_BOOL_AUX", "", "", "", "08", ""));
    }

    @Test
    void ndigitThatIsNotANumberIsADataFileError() {
        // C: PARSE_ERROR_NOT_NUMBER. Not an unchecked NumberFormatException.
        fatal(rec("r", "NUMERIC_RENDERER_WITH_BOOL_AUX", "", "", "", "abc", ""));
    }

    // ---- a block with no code: (C: backend stays NULL, finish_parse_ui_entry_renderer skips it) ---

    @Test
    void aRecordWithNoCodeLoadsWithNothingResolved() {
        UIEntryRenderer u = one(rec("r", "", "", "", "", "", ""));

        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE, u.getCode());   // C: backend stays NULL
        assertEquals("", u.getColours());
        assertEquals("", u.getLabelColours());
        assertEquals("", u.getSymbols());
        assertEquals(OptionalInt.empty(), u.getnDigit());                 // C: ndigit stays INT_MIN
        assertEquals(UIEntryEnum.UI_ENTRY_SIGN_DEFAULT, u.getSign());     // C: sign stays UI_ENTRY_SIGN_DEFAULT
    }

    @Test
    void aRecordWithNoCodeKeepsWhatItWasGiven() {
        UIEntryRenderer u = one(rec("r", "", "abc", "def", "ghi", "3", "ALWAYS_SIGN"));

        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NONE, u.getCode());
        assertEquals("abc", u.getColours());
        assertEquals("def", u.getLabelColours());
        assertEquals("ghi", u.getSymbols());
        assertEquals(OptionalInt.of(3), u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, u.getSign());
    }

    @Test
    void theFirstBadRecordStopsTheLoad() {
        fatal(rec("bad", "NOTACODE", "", "", "", "", ""),
                rec("good", FLAG_CODE, "", "", "", "", ""));
    }

    // ---- case sensitivity (C: streq in lookup_backend_by_name and parse_renderer_sign) --------

    @Test
    void codeIsMatchedCaseSensitively() {
        fatal(rec("r", FLAG_CODE.toLowerCase(), "", "", "", "", ""));
    }

    @Test
    void signIsMatchedCaseSensitively() {
        fatal(rec("r", FLAG_CODE, "", "", "", "", "no_sign"));
    }

    @Test
    void theOtherTwoFileSignsAreAccepted() {
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, one(rec("r", FLAG_CODE, "", "", "", "", "NO_SIGN")).getSign());
        assertEquals(UIEntryEnum.UI_ENTRY_NEGATIVE_SIGN,
                one(rec("r", FLAG_CODE, "", "", "", "", "NEGATIVE_SIGN")).getSign());
    }

    // ---- palettes (C: parse_renderer_colors etc. cut to MAX_PALETTE; augment_* pad) ------------

    @Test
    void aPaletteLongerThanSixtyFourIsCutToSixtyFour() {
        String seventy = "x".repeat(70);
        UIEntryRenderer u = one(rec("r", FLAG_CODE, seventy, seventy, seventy, "", ""));

        assertEquals("x".repeat(64), u.getColours());
        assertEquals("x".repeat(64), u.getLabelColours());
        assertEquals("x".repeat(64), u.getSymbols());
    }

    @Test
    void aPaletteAtLeastAsLongAsTheDefaultIsNotPadded() {
        // FLAG defaults: colours 10, label colours 4, symbols 5 characters. augment_* only acts when
        // the given count is below the default's.
        UIEntryRenderer u = one(rec("r", FLAG_CODE, "abcdefghij", "abcdef", "abcde", "", ""));

        assertEquals("abcdefghij", u.getColours());
        assertEquals("abcdef", u.getLabelColours());
        assertEquals("abcde", u.getSymbols());
    }

    // ---- list order and same-name blocks (C: parse_renderer_name) -------------------------------

    @Test
    void newNamesAreAddedInFileOrder() {
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", FLAG_CODE, "", "", "", "", ""),
                rec("b", FLAG_CODE, "", "", "", "", ""),
                rec("c", FLAG_CODE, "", "", "", "", "")), new ArrayList<>(), "test.txt");

        assertEquals(List.of("a", "b", "c"), out.stream().map(UIEntryRenderer::getName).toList());
    }

    @Test
    void aRepeatedNameEditsTheEarlierRecordInPlace() {
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", FLAG_CODE, "", "", "", "", ""),
                rec("b", FLAG_CODE, "", "", "", "", ""),
                rec("a", "", "", "", "", "5", "")), new ArrayList<>(), "test.txt");

        assertEquals(List.of("a", "b"), out.stream().map(UIEntryRenderer::getName).toList());
        assertEquals(OptionalInt.of(5), out.get(0).getnDigit());
    }

    @Test
    void aRepeatedNameKeepsWhatTheEarlierBlockSet() {
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", "NUMERIC_RENDERER_WITH_BOOL_AUX", "abc", "def", "ghi", "3", "ALWAYS_SIGN"),
                rec("a", "", "", "", "", "", "")), new ArrayList<>(), "test.txt");

        UIEntryRenderer u = out.get(0);
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, u.getCode());
        assertEquals("abcgGgrRwdsgGgrR", u.getColours());
        assertEquals("defwwww", u.getLabelColours());
        assertEquals("ghis*=", u.getSymbols());
        assertEquals(OptionalInt.of(3), u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, u.getSign());
    }

    @Test
    void aLaterBlockWithNoCodePadsItsPaletteFromTheInheritedBackend() {
        // FLAG defaults "wwwwGWWWWG", "swBw", "?..+!".
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", FLAG_CODE, "", "", "", "", ""),
                rec("a", "", "x", "y", "z", "", "")), new ArrayList<>(), "test.txt");

        UIEntryRenderer u = out.get(0);
        assertEquals("xwwwGWWWWG", u.getColours());
        assertEquals("ywBw", u.getLabelColours());
        assertEquals("z..+!", u.getSymbols());
    }

    @Test
    void aLaterCodeRepadsTheEarlierPaletteFromTheNewBackend() {
        // C pads once, at finish, from the backend in force then. The earlier block's "abc" is
        // padded from FLAG's defaults, not BOOL's.
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", "NUMERIC_RENDERER_WITH_BOOL_AUX", "abc", "d", "e", "", ""),
                rec("a", FLAG_CODE, "", "", "", "", "")), new ArrayList<>(), "test.txt");

        UIEntryRenderer u = out.get(0);
        assertEquals(FLAG, u.getCode());
        assertEquals("abcwGWWWWG", u.getColours());
        assertEquals("dwBw", u.getLabelColours());
        assertEquals("e..+!", u.getSymbols());
        assertEquals(OptionalInt.of(FLAG.getDefaultDigits()), u.getnDigit());
    }

    @Test
    void aCodeAddedLaterResolvesWhatAnUncodedBlockLeftUnset() {
        // First block has no backend, so C's finish would skip it; once the second block gives a
        // backend, the merged record is padded and defaulted from it.
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", "", "ab", "", "", "", ""),
                rec("a", "NUMERIC_RENDERER_WITH_BOOL_AUX", "", "", "", "", "")), new ArrayList<>(), "test.txt");

        UIEntryRenderer u = out.get(0);
        assertEquals("absgGgrRwdsgGgrR", u.getColours());
        assertEquals("wwwwwww", u.getLabelColours());
        assertEquals("? .s*=", u.getSymbols());
        assertEquals(OptionalInt.of(1), u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, u.getSign());   // backend default_sign
    }

    @Test
    void aSignSetEarlierSurvivesALaterCodeChange() {
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", "", "", "", "", "", "NEGATIVE_SIGN"),
                rec("a", FLAG_CODE, "", "", "", "", "")), new ArrayList<>(), "test.txt");

        assertEquals(UIEntryEnum.UI_ENTRY_NEGATIVE_SIGN, out.get(0).getSign());
    }

    @Test
    void aLaterSignReplacesAnEarlierOne() {
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", FLAG_CODE, "", "", "", "", "ALWAYS_SIGN"),
                rec("a", "", "", "", "", "", "NO_SIGN")), new ArrayList<>(), "test.txt");

        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, out.get(0).getSign());
    }

    @Test
    void aPaletteCutToSixtyFourStaysCutWhenALaterBlockInheritsIt() {
        String seventy = "x".repeat(70);
        List<UIEntryRenderer> out = new UIEntryRendererAssembler().assemble(List.of(
                rec("a", "", seventy, "", "", "", ""),
                rec("a", FLAG_CODE, "", "", "", "", "")), new ArrayList<>(), "test.txt");

        assertEquals("x".repeat(64), out.get(0).getColours());
    }
}
