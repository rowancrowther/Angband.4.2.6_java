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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.frontend.entries.UIEntryRenderer;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryEnum;
import uk.co.jackoftradesltd.frontend.entries.enums.UIEntryRendererEnum;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.assembler.UIEntryRendererAssembler;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests for the migrated UIEntryRenderer pipeline: text file → ANTLR lexer/parser →
 * {@link UIEntryRendererReader} → {@link UIEntryRendererAssembler}
 * → resolved {@link UIEntryRenderer} domain objects, wrapped in a {@link ParseResult}.
 *
 * <p>These drive real fixtures through the whole chain, so they exercise both ways a load stops:
 * <ul>
 *   <li>grammar/lex errors, caught by {@code ParseErrors} and thrown as a
 *       {@code ParseCancellationException} (a record missing a mandatory field);</li>
 *   <li>assembler refusals, thrown as a {@code DataFileParseException} (unknown {@code code},
 *       invalid {@code sign}).</li>
 * </ul>
 * The several-failures test confirms the first bad record stops the load and names its line, and
 * nothing after it is reached.
 *
 * <p>Test class UIEntryRendererReaderTest coded before 261105, updated on 261105 for the removal of
 * the soft-error path.
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

        assertEquals(5, result.items().size());

        // First renderer omits ndigit/sign -> both default from the resolved code.
        UIEntryRendererEnum firstCode = UIEntryRendererEnum.UI_ENTRY_RENDERER_COMPACT_RESIST_RENDERER_WITH_COMBINED_AUX;
        UIEntryRenderer first = result.items().get(0);
        assertEquals("char_screen1_resist_renderer", first.getName());
        assertEquals(firstCode, first.getCode());
        assertEquals("wwwwwwGGGrrGGGwGrGwwrwWWWWWWGGGrrGGGWGrGWWrW", first.getColours());
        assertEquals(OptionalInt.of(firstCode.getDefaultDigits()), first.getnDigit());
        assertEquals(firstCode.getEntry(), first.getSign());

        // Last renderer is the only one that sets ndigit and sign explicitly.
        UIEntryRenderer last = result.items().get(4);
        assertEquals("char_screen1_stat_mod_renderer", last.getName());
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, last.getCode());
        assertEquals(OptionalInt.of(1), last.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, last.getSign());
    }

    @Test
    void omittedColourFieldsDefaultThroughTheWholePipeline() throws IOException {
        // Only the two mandatory fields; the loosened grammar accepts this, and every optional
        // field must fall back to the resolved code's backend default (decision (b), end-to-end).
        String path = tempFile("defaults.txt",
                "name:only_mandatory\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        UIEntryRenderer u = result.items().get(0);
        assertEquals(FLAG, u.getCode());
        assertEquals(FLAG.getDefaultColours(), u.getColours());
        assertEquals(FLAG.getDefaultLabelColours(), u.getLabelColours());
        assertEquals(FLAG.getDefaultSymbols(), u.getSymbols());
        assertEquals(OptionalInt.of(FLAG.getDefaultDigits()), u.getnDigit());
        assertEquals(FLAG.getEntry(), u.getSign());
    }

    // ---- assembler refusals ------------------------------------------------------------------

    @Test
    void unknownCodeIsFatal() throws IOException {
        // C: lookup_backend_by_name returns 0, so parse_renderer_code gives PARSE_ERROR_INVALID_VALUE.
        String path = tempFile("bad-code.txt",
                "name:x\ncode:NOTACODE\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new UIEntryRendererReader().parseWithResults(path));

        assertTrue(e.getMessage().contains("illegal code value") && e.getMessage().contains("NOTACODE"),
                e::getMessage);
    }

    @Test
    void invalidSignIsFatal() throws IOException {
        // C: parse_renderer_sign accepts only NO_SIGN, ALWAYS_SIGN and NEGATIVE_SIGN.
        for (String bad : List.of("BOGUS", "SIGN_DEFAULT")) {
            String path = tempFile("bad-sign-" + bad + ".txt",
                    "name:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\nsign:" + bad + "\n");

            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> new UIEntryRendererReader().parseWithResults(path), bad);

            assertTrue(e.getMessage().contains("illegal") && e.getMessage().contains("sign")
                    && e.getMessage().contains(bad), e::getMessage);
        }
    }

    // ---- grammar errors -----------------------------------------------------------------------

    @Test
    void recordMissingMandatoryCodeFailsClosed() throws IOException {
        // 'code' is mandatory; a record with only 'name' is a grammar error.
        String path = tempFile("no-code.txt",
                "name:x\ncolors:w\n");

        assertThrows(ParseCancellationException.class, () -> new UIEntryRendererReader().parseWithResults(path));

    }

    // ---- several distinct failures at once ---------------------------------------------------

    @Test
    void theFirstBadRecordStopsTheLoadAndNamesItsLine() throws IOException {
        // Soft errors were made fatal: the NOTACODE record is the first failure, so the load
        // stops there and nothing after it (the BOGUS sign) is reached.
        String path = tempFile("mixed.txt", String.join("\n",
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

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new UIEntryRendererReader().parseWithResults(path));

        assertTrue(e.getMessage().contains("NOTACODE"), e::getMessage);
        assertFalse(e.getMessage().contains("BOGUS"), e::getMessage);
    }

    // ---- palette top-up and cap (C: augment_colors, augment_symbols, MAX_PALETTE) ------------

    /**
     * Header for a one-record file bound to the flag backend, whose defaults in C's
     * {@code list-ui-entry-renderers.h} are colours {@code wwwwGWWWWG}, label colours
     * {@code swBw}, symbols {@code ?..+!}, 0 digits and no sign.
     */
    private static final String FLAG_HEADER =
            "name:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n";

    private UIEntryRenderer loadOne(String body) throws IOException {
        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader()
                .parseWithResults(tempFile("one.txt", FLAG_HEADER + body));
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
        assertEquals(OptionalInt.of(0), loadOne("").getnDigit());

        for (String bad : List.of("0", "-3")) {
            String path = tempFile("nd" + bad + ".txt", FLAG_HEADER + "ndigit:" + bad + "\n");
            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> new UIEntryRendererReader().parseWithResults(path), "ndigit:" + bad);
            assertTrue(e.getMessage().contains("number of digits") && e.getMessage().contains(bad), e::getMessage);
        }
    }

    @Test
    void explicitNdigitIsStoredAndOverridesTheBackendDefault() throws IOException {
        assertEquals(OptionalInt.of(1), loadOne("ndigit:1\n").getnDigit());
        assertEquals(OptionalInt.of(7), loadOne("ndigit:7\n").getnDigit());
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
            String path = tempFile(row[0] + ".txt", "name:x\ncode:" + row[0] + "\n");
            ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);
            UIEntryRenderer u = result.items().get(0);
            assertEquals(row[1], u.getColours(), row[0]);
            assertEquals(row[2], u.getLabelColours(), row[0]);
            assertEquals(row[3], u.getSymbols(), row[0]);
            assertEquals(OptionalInt.of(Integer.parseInt(row[4])), u.getnDigit(), row[0]);
            assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, u.getSign(), row[0]);
        }
    }

    // ---- known divergences from C (see "Things not to drop" in docs/precis/260929.md) --------
    // Each pins what C does; disabled until the port catches up, so the gap stays visible.

    @Test
    @Disabled("Known divergence: the Java-only NONE sentinel is accepted as a code; C rejects it as an unknown backend")
    void codeNoneIsRejectedAsAnUnknownBackend() throws IOException {
        String path = tempFile("none.txt", "name:x\ncode:NONE\n");

        assertThrows(DataFileParseException.class, () -> new UIEntryRendererReader().parseWithResults(path));

    }

    @Test
    @Disabled("Known divergence: the grammar fixes the directive order; C accepts any order")
    void directivesAfterNameMayComeInAnyOrder() throws IOException {
        String path = tempFile("order.txt",
                "name:x\nsymbols:?\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\ncolors:GG\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals("GGwwGWWWWG", result.items().get(0).getColours());
    }

    @Test
    @Disabled("Known divergence: combine, units and combined-renderer are not in the grammar or the parse record")
    void combineUnitsAndCombinedRendererDirectivesAreAccepted() throws IOException {
        String path = tempFile("extras.txt",
                "name:x\ncode:COMPACT_FLAG_RENDERER_WITH_COMBINED_AUX\n"
                        + "combine:ADD\nunits:%\ncombined-renderer:x\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals(1, result.items().size());
    }

    @Test
    @Disabled("Known divergence: C merges a repeated name into one renderer; Java has no merge and the grammar rejects it")
    void aRepeatedNameMergesIntoOneRendererFieldByField() throws IOException {
        // parse_renderer_name reopens the existing renderer, so the second block only overrides sign.
        String path = tempFile("merge.txt", "name:foo\ncode:NUMERIC_RENDERER_WITH_BOOL_AUX\n"
                + "ndigit:2\nname:foo\nsign:NO_SIGN\n");

        ParseResult<UIEntryRenderer> result = new UIEntryRendererReader().parseWithResults(path);

        assertEquals(1, result.items().size());
        UIEntryRenderer u = result.items().get(0);
        assertEquals(UIEntryRendererEnum.UI_ENTRY_RENDERER_NUMERIC_RENDERER_WITH_BOOL_AUX, u.getCode());
        assertEquals(OptionalInt.of(2), u.getnDigit());
        assertEquals(UIEntryEnum.UI_ENTRY_NO_SIGN, u.getSign());
    }
}
