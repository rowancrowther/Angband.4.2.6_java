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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourAssembler;
import uk.co.jackoftradesltd.backend.parser.flavour.FlavourParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.objects.Flavour;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link FlavourAssembler} against {@code parse_flavor_flavor} in {@code init.c}. Expected values
 * come from C: {@code index} is a {@code uint} that {@code parser_parse} reads with {@code strtoul}
 * base 0 after refusing a leading {@code -}; {@code attr} goes to {@code color_char_to_attr} when
 * it is one character long and {@code color_text_to_attr} otherwise.
 */
class FlavourAssemblerTest {

    private static Flavour one(String index, String sVal, String attr, String text) {
        List<Flavour> out = new FlavourAssembler().assemble(
                List.of(new FlavourParseRecord(index, sVal, attr, text, 7)), new ArrayList<>(), "flavor.txt");
        assertEquals(1, out.size());
        return out.getFirst();
    }

    @ParameterizedTest
    @CsvSource({"5,5", "0,0", "+5,5", "010,8", "08,0", "12abc,12", "0x10,16", "0X1f,31", "0xff,255",
            "+0x10,16", "+0X1f,31", "+010,8", "+08,0", "+07,7", "+0,0", "0x,0", "0xg,0", "09,0"})
    void indexIsReadAsStrtoulBase0Reads(String text, int expected) {
        assertEquals(expected, one(text, null, "Green", "Foo").getIndex());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-5", "-0", "-0x10"})
    void aLeadingMinusOnTheIndexStopsTheLoad(String text) {
        assertThrows(DataFileParseException.class, () -> one(text, null, "Green", "Foo"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "x10"})
    void aNonNumericIndexStopsTheLoad(String text) {
        assertThrows(DataFileParseException.class, () -> one(text, null, "Green", "Foo"));
    }

    /** strtoul skips leading whitespace, so C reads " 5" as 5. */
    @Test
    void leadingWhitespaceOnTheIndexIsSkippedAsStrtoulSkipsIt() {
        assertEquals(5, one(" 5", null, "Green", "Foo").getIndex());
    }

    @ParameterizedTest
    @CsvSource({"g,COLOUR_GREEN", "w,COLOUR_WHITE", "G,COLOUR_LIGHT_GREEN", "Green,COLOUR_GREEN",
            "green,COLOUR_GREEN", "Light Blue,COLOUR_LIGHT_BLUE"})
    void knownColoursResolve(String attr, ColourEnum expected) {
        assertEquals(expected, one("1", null, attr, "Foo").getColour());
    }

    /** color_char_to_attr: a character in no row is white. */
    @Test
    void anUnknownColourCharacterIsWhite() {
        assertEquals(ColourEnum.COLOUR_WHITE, one("1", null, "q", "Foo").getColour());
    }

    /** color_text_to_attr: source says "Default to white" (see the stage 1 note on NULL names). */
    @Test
    void anUnknownColourNameIsWhite() {
        assertEquals(ColourEnum.COLOUR_WHITE, one("1", null, "Mauve", "Foo").getColour());
    }

    /** C has no colour named "Shade" (colour 28 has no color_table row), so it is white. */
    @Test
    void shadeIsNotANameC_Knows() {
        assertEquals(ColourEnum.COLOUR_WHITE, one("1", null, "Shade", "Foo").getColour());
    }

    @Test
    void aBlankColourIsDark() {
        assertEquals(ColourEnum.COLOUR_DARK, one("1", null, " ", "Foo").getColour());
    }

    /** The name match is case-insensitive (my_stricmp) but the one-character match is not. */
    @ParameterizedTest
    @CsvSource({"GREEN,COLOUR_GREEN", "light BLUE,COLOUR_LIGHT_BLUE", "W,COLOUR_LIGHT_SLATE", "v,COLOUR_VIOLET",
            "V,COLOUR_LIGHT_VIOLET", "P,COLOUR_LIGHT_PURPLE"})
    void colourCaseFollowsTheCRules(String attr, ColourEnum expected) {
        assertEquals(expected, one("1", null, attr, "Foo").getColour());
    }

    /** Every record becomes a flavour, in order: a bad colour is white, not a dropped record. */
    @Test
    void everyRecordIsKeptInOrderAndNoSoftErrorIsRaised() {
        List<String> errors = new ArrayList<>();
        List<Flavour> out = new FlavourAssembler().assemble(List.of(
                new FlavourParseRecord("2", null, "Green", "A", 3),
                new FlavourParseRecord("3", null, "Mauve", "B", 4),
                new FlavourParseRecord("4", "Some Ring", "q", "C", 5)), errors, "flavor.txt");
        assertEquals(List.of(2, 3, 4), out.stream().map(Flavour::getIndex).toList());
        assertEquals(List.of(ColourEnum.COLOUR_GREEN, ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_WHITE),
                out.stream().map(Flavour::getColour).toList());
        assertTrue(errors.isEmpty());
    }

    @Test
    void anEmptyBlockGivesNoFlavours() {
        assertTrue(new FlavourAssembler().assemble(List.of(), new ArrayList<>(), "flavor.txt").isEmpty());
    }

    /** The error names the record, its line and the file. */
    @Test
    void theIndexErrorNamesTheLineAndFile() {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> one("-5", null, "Green", "Foo"));
        assertTrue(e.getMessage().contains("line: 7"));
        assertTrue(e.getMessage().contains("flavor.txt"));
    }

    @Test
    void aNullSvalIsARandomFlavourAndANonNullOneIsFixed() {
        Flavour random = one("1", null, "Green", null);
        assertFalse(random.isFixed());
        assertNull(random.getText());
        Flavour fixed = one("1", "Ring of Power", "Yellow", "Plain Gold");
        assertTrue(fixed.isFixed());
        assertEquals("Ring of Power", fixed.getsValStr());
        assertEquals("Plain Gold", fixed.getText());
    }
}
