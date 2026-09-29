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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererAssembler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the migrated UIEntryRenderer pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryRendererReader} → {@link UIEntryRendererAssembler}
 * → resolved {@link UIEntryRenderer} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>These drive real fixtures through the whole chain, so they exercise both of the reader's error
 * channels:
 * <ul>
 *   <li><b>hard</b> grammar/lex errors, caught by {@code ParseErrors} - fail-closed: empty list +
 *       collected errors (missing {@code record-count} header, a record missing a mandatory field);</li>
 *   <li><b>soft</b> interpretation/validation errors, appended to the returned list while partial
 *       results survive (unknown {@code code}, invalid {@code sign}, {@code record-count} mismatch).</li>
 * </ul>
 * The final test injects several distinct failures at once to confirm every one is logged
 * independently and the valid records still come through.
 *
 * @author Rowan Crowther
 */
class UIEntryRendererReaderTest {

    private static final String REAL_FILE = "lib/gamedata/ui_entry_renderer.txt";

    /**
     * A code whose value/label/symbol/digit/sign defaults are all distinct.
     */
    private static final UIEntryRendererEnum FLAG =
            UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX;

    @TempDir
    Path tempDir;

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- happy path -------------------------------------------------------------------------

    @Test
    void cleanLoadOfTheRealFileResolvesAllRenderers() throws IOException {
        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(5, result.items().size());

        // First renderer omits ndigit/sign -> both default from the resolved code.
        UIEntryRendererEnum firstCode = UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX;
        UIEntryRenderer first = result.items().get(0);
        assertEquals("char_screen1_resist_renderer", first.getName());
        assertEquals(firstCode, first.getCode());
        assertEquals("wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW", first.getColours());
        assertEquals(firstCode.getDefaultDigits(), first.getnDigit());
        assertEquals(firstCode.getEntry(), first.getSign());

        // Last renderer is the only one that sets ndigit and sign explicitly.
        UIEntryRenderer last = result.items().get(4);
        assertEquals("char_screen1_stat_mod_renderer", last.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, last.getCode());
        assertEquals(1, last.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, last.getSign());
    }

    @Test
    void omittedColourFieldsDefaultThroughTheWholePipeline() throws IOException {
        // Only the two mandatory fields; the loosened grammar accepts this, and every optional
        // field must fall back to the resolved code's backend default (decision (b), end-to-end).
        String path = tempFile("defaults.txt",
                "record-count:1\nname:only_mandatory\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        UIEntryRenderer u = result.items().get(0);
        assertEquals(FLAG, u.getCode());
        assertEquals(FLAG.getDefaultColours(), u.getColours());
        assertEquals(FLAG.getDefaultLabelColours(), u.getLabelColours());
        assertEquals(FLAG.getDefaultSymbols(), u.getSymbols());
        assertEquals(FLAG.getDefaultDigits(), u.getnDigit());
        assertEquals(FLAG.getEntry(), u.getSign());
    }

    // ---- soft errors (partial results survive) ----------------------------------------------

    @Test
    void unknownCodeIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-code.txt",
                "record-count:1\nname:x\ncode:NOTACODE\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("illegal code enum value") && e.contains("NOTACODE")),
                result.errors()::toString);
    }

    @Test
    void invalidSignIsLoggedAndRecordSkipped() throws IOException {
        String path = tempFile("bad-sign.txt",
                "record-count:1\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\nsign:BOGUS\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("illegal sign enum value") && e.contains("BOGUS")),
                result.errors()::toString);
    }

    @Test
    void recordCountMismatchIsLoggedButValidRecordSurvives() throws IOException {
        // Header over-declares (5) vs one actual record; the record itself is valid and comes through.
        String path = tempFile("bad-count.txt",
                "record-count:5\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("declares 5") && e.contains("contains 1")),
                result.errors()::toString);
    }

    // ---- hard errors (fail-closed: empty list) ----------------------------------------------

    @Test
    void missingRecordCountHeaderFailsClosed() throws IOException {
        // No record-count directive -> the file rule can't match -> grammar error via ParseErrors.
        String path = tempFile("no-header.txt",
                "name:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordMissingMandatoryCodeFailsClosed() throws IOException {
        // 'code' is mandatory; a record with only 'name' is a grammar error, not a soft one.
        String path = tempFile("no-code.txt",
                "record-count:1\nname:x\ncolors:w\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- several distinct failures at once ---------------------------------------------------

    @Test
    void multipleDistinctFailuresAreEachLoggedAndGoodRecordsSurvive() throws IOException {
        // Four records: valid, unknown-code, invalid-sign, valid. record-count matches the raw
        // count (4), so the only errors are the two semantic ones - and both valid records survive.
        String path = tempFile("mixed.txt", String.join("\n",
                "record-count:4",
                "name:good_first",
                "code:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX",
                "name:bad_code",
                "code:NOTACODE",
                "name:bad_sign",
                "code:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX",
                "sign:BOGUS",
                "name:good_last",
                "code:NUMERIC_RENDERER_WITH_BOOL_AUX",
                ""));

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        // Two valid records survive, in order.
        List<UIEntryRenderer> items = result.items();
        assertEquals(2, items.size(), items::toString);
        assertEquals("good_first", items.get(0).getName());
        assertEquals("good_last", items.get(1).getName());

        // Exactly the two semantic errors are logged - no spurious count mismatch, one per failure.
        List<String> errors = result.errors();
        assertEquals(2, errors.size(), errors::toString);
        assertTrue(errors.stream()
                        .anyMatch(e -> e.contains("illegal code enum value") && e.contains("NOTACODE")),
                errors::toString);
        assertTrue(errors.stream()
                        .anyMatch(e -> e.contains("illegal sign enum value") && e.contains("BOGUS")),
                errors::toString);
    }

    // ---- palette top-up and cap (C: augment_colors, augment_symbols, MAX_PALETTE) ------------

    /**
     * Header for a one-record file bound to the flag backend, whose defaults in C's
     * {@code list-ui-entry-renderers.h} are colours {@code wwwwGWWWWG}, label colours
     * {@code swBw}, symbols {@code ?..+!}, 0 digits and no sign.
     */
    private static final String FLAG_HEADER =
            "record-count:1\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n";

    private UIEntryRenderer loadOne(String body) throws IOException {
        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader()
                .parseWithResults(tempFile("one.txt", FLAG_HEADER + body));
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        return result.items().get(0);
    }

    @Test
    void shortColoursAreToppedUpFromTheBackendDefaultAtTheSameIndex() throws IOException {
        // augment_colors keeps the two file entries and copies default[2..] after them.
        assertEquals("GGwwGWWWWG", loadOne("colors:GG\n").getColours());
    }

    @Test
    void shortLabelColoursAreToppedUpFromTheBackendDefaultAtTheSameIndex() throws IOException {
        assertEquals("BBBw", loadOne("labelcolors:BB\n").getLabelColours());
    }

    @Test
    void shortSymbolsAreToppedUpFromTheBackendDefaultAtTheSameIndex() throws IOException {
        // augment_symbols: "XY" then default[2..] = ".+!".
        assertEquals("XY.+!", loadOne("symbols:XY\n").getSymbols());
    }

    @Test
    void aPaletteEqualToTheDefaultLengthIsUnchanged() throws IOException {
        UIEntryRenderer u = loadOne("colors:GGGGGGGGGG\nlabelcolors:BBBB\nsymbols:abcde\n");
        assertEquals("GGGGGGGGGG", u.getColours());
        assertEquals("BBBB", u.getLabelColours());
        assertEquals("abcde", u.getSymbols());
    }

    @Test
    void aPaletteLongerThanTheDefaultIsKeptWhole() throws IOException {
        // augment_* only act when the file's palette is shorter than the default.
        UIEntryRenderer u = loadOne("colors:wwwwGWWWWGw\nlabelcolors:swBwwww\nsymbols:?..+!abc\n");
        assertEquals("wwwwGWWWWGw", u.getColours());
        assertEquals("swBwwww", u.getLabelColours());
        assertEquals("?..+!abc", u.getSymbols());
    }

    @Test
    void palettesAreCappedAtSixtyFourEntries() throws IOException {
        // MAX_PALETTE is 64 in ui-entry-renderers.c.
        UIEntryRenderer u = loadOne("colors:" + "w".repeat(70) + "\nlabelcolors:" + "s".repeat(70)
                + "\nsymbols:" + "a".repeat(70) + "\n");
        assertEquals("w".repeat(64), u.getColours());
        assertEquals("s".repeat(64), u.getLabelColours());
        assertEquals("a".repeat(64), u.getSymbols());
    }

    // ---- ndigit and sign (C: parse_renderer_ndigit, parse_renderer_sign) ---------------------

    @Test
    void explicitNdigitBelowOneIsRejectedButABackendDefaultOfZeroIsNot() throws IOException {
        // C checks ndigit < 1 only on the explicit directive; the flag backend's default of 0 is fine.
        assertEquals(0, loadOne("").getnDigit());

        for (String bad : List.of("0", "-3")) {
            ParseResult<UIEntryRenderer> result = new UIEntryRendererReader()
                    .parseWithResults(tempFile("nd" + bad + ".txt", FLAG_HEADER + "ndigit:" + bad + "\n"));
            assertTrue(result.items().isEmpty(), "ndigit:" + bad);
            assertTrue(result.errors().stream().anyMatch(e -> e.contains("nDigits") && e.contains(bad)),
                    result.errors()::toString);
        }
    }

    @Test
    void explicitNdigitIsStoredAndOverridesTheBackendDefault() throws IOException {
        assertEquals(1, loadOne("ndigit:1\n").getnDigit());
        assertEquals(7, loadOne("ndigit:7\n").getnDigit());
    }

    @Test
    void everySignNameResolvesToItsEnumValue() throws IOException {
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, loadOne("sign:NO_SIGN\n").getSign());
        assertEquals(UIEntryEnum.UI_ENTRY_ALWAYS_SIGN, loadOne("sign:ALWAYS_SIGN\n").getSign());
        assertEquals(UIEntryEnum.UI_ENTRY_NEGATIVE_SIGN, loadOne("sign:NEGATIVE_SIGN\n").getSign());
    }

    // ---- backend defaults (C: list-ui-entry-renderers.h) -------------------------------------

    @Test
    void everyBackendDefaultsFromTheCHeaderWhenOnlyNameAndCodeAreGiven() throws IOException {
        // code, colours, label colours, symbols, digits - copied from list-ui-entry-renderers.h.
        String[][] rows = {
                {"COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX",
                        "wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW", "swBrgwBrwBwBr", "?..+-*!^.=.%%%~!=%~+=~", "0"},
                {"COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX", "wwwwGWWWWG", "swBw", "?..+!", "0"},
                {"COMPACT_FLAG_WITH_CANCEL_RENDERER_WITH_COMBINED_AUX",
                        "wwwwwGwwGGwWWWWWGWWGGW", "swwwwBw", "?..+-!+-=.-", "0"},
                {"NUMERIC_AS_SIGN_RENDERER_WITH_COMBINED_AUX",
                        "wwwGowGowGoWWWGoWGoWGo", "swwwBBBrrr", "?....+!+--=", "0"},
                {"NUMERIC_RENDERER_WITH_COMBINED_AUX",
                        "wwwboBbPrRowwwboBbPrRo", "swwwBBBrrr", "?0000+-", "1"},
                {"NUMERIC_RENDERER_WITH_BOOL_AUX", "wdsgGgrRwdsgGgrR", "wwwwwww", "? .s*=", "1"},
        };
        for (String[] row : rows) {
            String path = tempFile(row[0] + ".txt", "record-count:1\nname:x\ncode:" + row[0] + "\n");
            ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);
            assertFalse(result.hasErrors(), () -> row[0] + " " + result.errors());
            UIEntryRenderer u = result.items().get(0);
            assertEquals(row[1], u.getColours(), row[0]);
            assertEquals(row[2], u.getLabelColours(), row[0]);
            assertEquals(row[3], u.getSymbols(), row[0]);
            assertEquals(Integer.parseInt(row[4]), u.getnDigit(), row[0]);
            assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, u.getSign(), row[0]);
        }
    }

    // ---- known divergences from C (see "Things not to drop" in docs/precis/260929.md) --------
    // Each pins what C does; disabled until the port catches up, so the gap stays visible.

    @Test
    @Disabled("Known divergence: the Java-only NONE sentinel is accepted as a code; C rejects it as an unknown backend")
    void codeNoneIsRejectedAsAnUnknownBackend() throws IOException {
        String path = tempFile("none.txt", "record-count:1\nname:x\ncode:NONE\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    @Disabled("Known divergence: the grammar fixes the directive order; C accepts any order")
    void directivesAfterNameMayComeInAnyOrder() throws IOException {
        String path = tempFile("order.txt",
                "record-count:1\nname:x\nsymbols:?\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\ncolors:GG\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("GGwwGWWWWG", result.items().get(0).getColours());
    }

    @Test
    @Disabled("Known divergence: combine, units and combined-renderer are not in the grammar or the parse record")
    void combineUnitsAndCombinedRendererDirectivesAreAccepted() throws IOException {
        String path = tempFile("extras.txt",
                "record-count:1\nname:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n"
                        + "combine:ADD\nunits:%\ncombined-renderer:x\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    @Disabled("Known divergence: C merges a repeated name into one renderer; Java has no merge and the grammar rejects it")
    void aRepeatedNameMergesIntoOneRendererFieldByField() throws IOException {
        // parse_renderer_name reopens the existing renderer, so the second block only overrides sign.
        String path = tempFile("merge.txt", "record-count:2\nname:foo\ncode:NUMERIC_RENDERER_WITH_BOOL_AUX\n"
                + "ndigit:2\nname:foo\nsign:NO_SIGN\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        UIEntryRenderer u = result.items().get(0);
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, u.getCode());
        assertEquals(2, u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, u.getSign());
    }
}
