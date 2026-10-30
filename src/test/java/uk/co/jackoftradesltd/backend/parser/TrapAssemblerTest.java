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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.backend.parser.trap.TrapAssembler;
import uk.co.jackoftradesltd.backend.parser.trap.TrapParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.enums.TrapEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code TrapAssembler}, driven from hand-built {@link TrapParseRecord}s rather than
 * through the grammar.
 *
 * <p>Expected values come from the {@code parse_trap_*} handlers and {@code finish_parse_trap()} in
 * {@code init.c}: {@code appear:} is three {@code uint} fields ({@code strtoul} in base 0, a leading
 * {@code -} refused), {@code graphics:} defaults an unknown colour to white through
 * {@code color_char_to_attr()}/{@code color_text_to_attr()} ({@code z-color.c}), {@code visibility:}
 * is {@code dice_parse_string()} then {@code dice_random_value()}, flags go through
 * {@code lookup_flag()} ({@code datafile.c}) which starts past the {@code NONE} slot, and
 * {@code tidx} counts from 0 in file order. An absent directive leaves {@code mem_zalloc}'s zero.
 *
 * <p>Every error ends the load with a {@link DataFileParseException}, as a C handler error ends the
 * {@code trap.txt} load, so the failure tests assert the throw. {@code TrapKind} has no getters for
 * the display character, the {@code appear:} numbers or the power, so those are read by reflection.
 * Effects that resolve need registries seeded and are left to {@code TrapReaderTest}; the one effect
 * here has no type, which {@code EffectAssembler} refuses before it reaches any registry.
 *
 * @author Rowan Crowther
 */
class TrapAssemblerTest {

    private static final String FILE = "trap.txt";

    /** A record with the given graphics, appear, visibility, flags and save flags, and no effects. */
    private static TrapParseRecord record(String glyph, String colour, String rarity, String minDepth,
                                          String maxNum, String power, List<String> flags,
                                          List<String> saveFlags, int line) {
        return new TrapParseRecord("trap", "Some text.", "rune", "", "", "", "",
                glyph, colour, rarity, minDepth, maxNum, power, flags, saveFlags,
                new ArrayList<>(), new ArrayList<>(), line);
    }

    /** A plain valid record: {@code ^} in red, {@code appear:1:2:0}, no visibility or flags. */
    private static TrapParseRecord plain(int line) {
        return record("^", "r", "1", "2", "0", "", List.of(), List.of(), line);
    }

    private static TrapParseRecord withAppear(String rarity, String minDepth, String maxNum) {
        return record("^", "r", rarity, minDepth, maxNum, "", List.of(), List.of(), 1);
    }

    private static TrapParseRecord withColour(String glyph, String colour) {
        return record(glyph, colour, "1", "1", "0", "", List.of(), List.of(), 1);
    }

    private static TrapParseRecord withPower(String power) {
        return record("^", "r", "1", "1", "0", power, List.of(), List.of(), 1);
    }

    private static TrapParseRecord withFlags(List<String> flags, List<String> saveFlags) {
        return record("^", "r", "1", "1", "0", "", flags, saveFlags, 1);
    }

    private static TrapKind one(TrapParseRecord record) {
        List<TrapKind> kinds = new TrapAssembler().assemble(List.of(record), new ArrayList<>(), FILE);
        assertEquals(1, kinds.size());
        return kinds.get(0);
    }

    private static void assertThrowsFor(TrapParseRecord record) {
        assertThrows(DataFileParseException.class,
                () -> new TrapAssembler().assemble(List.of(record), new ArrayList<>(), FILE));
    }

    @SuppressWarnings("unchecked")
    private static <T> T field(TrapKind kind, String name) {
        try {
            Field f = TrapKind.class.getDeclaredField(name);
            f.setAccessible(true);
            return (T) f.get(kind);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("no field " + name, e);
        }
    }

    // --- ordinary path, index and name/desc crossover ---

    @Test
    void emptyRecordListGivesNoKinds() {
        assertTrue(new TrapAssembler().assemble(List.of(), new ArrayList<>(), FILE).isEmpty());
    }

    @Test
    void indicesCountFromZeroInFileOrder() {
        // finish_parse_trap gives the first record in the file tidx 0
        List<TrapKind> kinds = new TrapAssembler().assemble(
                List.of(plain(1), plain(10), plain(20)), new ArrayList<>(), FILE);
        assertEquals(3, kinds.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(i, kinds.get(i).getTrapKindIndex());
        }
    }

    @Test
    void nameAndDescCrossoverIsKept() {
        TrapKind kind = one(plain(1));
        assertEquals("trap", kind.getTrapKindName());   // C name
        assertEquals("rune", kind.getDescription());    // C desc, the name: line's second field
        assertEquals("Some text.", kind.getText());     // C text, from desc:
    }

    @Test
    void absentMessagesAreEmptyText() {
        TrapKind kind = one(plain(1));
        assertEquals("", field(kind, "message"));
        assertEquals("", field(kind, "messageOnSave"));
        assertEquals("", field(kind, "messageOnFailure"));
        assertEquals("", field(kind, "messageOnExtraEffect"));
    }

    @Test
    void noEffectsGiveEmptyLists() {
        TrapKind kind = one(plain(1));
        assertNotNull(kind.getEffect());
        assertTrue(kind.getEffect().isEmpty());
        assertNotNull(kind.getEffectXtra());
        assertTrue(kind.getEffectXtra().isEmpty());
    }

    // --- graphics: ---

    @Test
    void singleCharacterColourIsAnIndexCharacter() {
        AngbandDisplayCharacter adc = field(one(withColour("^", "r")), "angbandDisplayCharacter");
        assertEquals('^', adc.getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_RED, adc.getAttributeColour());
    }

    @Test
    void longerColourIsANameMatchedIgnoringCase() {
        // color_text_to_attr uses my_stricmp
        AngbandDisplayCharacter adc = field(one(withColour("^", "red")), "angbandDisplayCharacter");
        assertEquals(ColourEnum.COLOUR_RED, adc.getAttributeColour());
        adc = field(one(withColour("^", "Red")), "angbandDisplayCharacter");
        assertEquals(ColourEnum.COLOUR_RED, adc.getAttributeColour());
    }

    @Test
    void unknownColourCharacterDefaultsToWhite() {
        // color_char_to_attr: "If we don't find the color, we assume white"
        AngbandDisplayCharacter adc = field(one(withColour("^", "q")), "angbandDisplayCharacter");
        assertEquals(ColourEnum.COLOUR_WHITE, adc.getAttributeColour());
    }

    @Test
    void unknownColourNameDefaultsToWhite() {
        // color_text_to_attr: "Default to white"
        AngbandDisplayCharacter adc = field(one(withColour("^", "Puce")), "angbandDisplayCharacter");
        assertEquals(ColourEnum.COLOUR_WHITE, adc.getAttributeColour());
    }

    @Test
    void supplementaryGlyphIsKeptWhole() {
        String glyph = new String(Character.toChars(0x1F600));
        AngbandDisplayCharacter adc = field(one(withColour(glyph, "r")), "angbandDisplayCharacter");
        assertEquals(0x1F600, adc.getWideCharacter());
    }

    @Test
    void absentGraphicsIsBlank() {
        // C leaves d_attr zeroed, COLOUR_DARK
        AngbandDisplayCharacter adc = field(one(withColour("", "")), "angbandDisplayCharacter");
        assertSame(AngbandDisplayCharacter.blank, adc);
        assertEquals(ColourEnum.COLOUR_DARK, adc.getAttributeColour());
    }

    // --- appear: ---

    @Test
    void appearNumbersAreRead() {
        TrapKind kind = one(withAppear("1", "40", "3"));
        assertEquals(1, (int) field(kind, "rarity"));
        assertEquals(40, (int) field(kind, "minDepth"));
        assertEquals(3, (int) field(kind, "maxNum"));
    }

    @Test
    void appearTakesStrtoulBaseZeroPrefixes() {
        // strtoul(tok, &z, 0): 0x10 is 16, 010 is 8
        TrapKind kind = one(withAppear("0x10", "010", "0"));
        assertEquals(16, (int) field(kind, "rarity"));
        assertEquals(8, (int) field(kind, "minDepth"));
        assertEquals(0, (int) field(kind, "maxNum"));
    }

    @Test
    void absentAppearIsZero() {
        TrapKind kind = one(withAppear("", "", ""));
        assertEquals(0, (int) field(kind, "rarity"));
        assertEquals(0, (int) field(kind, "minDepth"));
        assertEquals(0, (int) field(kind, "maxNum"));
    }

    @Test
    void negativeRarityThrows() {
        assertThrowsFor(withAppear("-1", "1", "0"));
    }

    @Test
    void negativeMinDepthThrows() {
        assertThrowsFor(withAppear("1", "-1", "0"));
    }

    @Test
    void negativeMaxNumThrows() {
        assertThrowsFor(withAppear("1", "1", "-1"));
    }

    @Test
    void minusZeroThrows() {
        // C refuses on *tok == '-' before looking at the digits
        assertThrowsFor(withAppear("-0", "1", "0"));
    }

    @Test
    void nonNumericAppearThrows() {
        // z == tok gives PARSE_ERROR_NOT_NUMBER
        assertThrowsFor(withAppear("abc", "1", "0"));
    }

    // --- visibility: ---

    @Test
    void absentVisibilityIsZero() {
        Random power = field(one(withPower("")), "power");
        assertNotNull(power);
        assertEquals(0, power.getBase());
        assertEquals(0, power.getDice());
        assertEquals(0, power.getSides());
        assertEquals(0, power.getMBonus());
    }

    @Test
    void plainVisibility() {
        Random power = field(one(withPower("70")), "power");
        assertEquals(70, power.getBase());
        assertEquals(0, power.getDice());
    }

    @Test
    void baseAndDieVisibility() {
        // trap.txt: visibility:30+d5
        Random power = field(one(withPower("30+d5")), "power");
        assertEquals(30, power.getBase());
        assertEquals(1, power.getDice());
        assertEquals(5, power.getSides());
    }

    @Test
    void diceAndBonusVisibility() {
        // trap.txt: visibility:2d10M50
        Random power = field(one(withPower("2d10M50")), "power");
        assertEquals(0, power.getBase());
        assertEquals(2, power.getDice());
        assertEquals(10, power.getSides());
        assertEquals(50, power.getMBonus());
    }

    @Test
    void bonusOnlyVisibility() {
        // trap.txt: visibility:M50
        Random power = field(one(withPower("M50")), "power");
        assertEquals(50, power.getMBonus());
        assertEquals(0, power.getDice());
    }

    @Test
    void unreadableVisibilityThrows() {
        // dice_parse_string fails: PARSE_ERROR_NOT_RANDOM
        assertThrowsFor(withPower("abc"));
    }

    // --- flags: and save: ---

    @Test
    void trapFlagsAreSet() {
        TrapKind kind = one(withFlags(List.of("TRAP", "FLOOR", "ONETIME"), List.of()));
        assertTrue(kind.getFlags().has(TrapEnum.TRF_TRAP));
        assertTrue(kind.getFlags().has(TrapEnum.TRF_FLOOR));
        assertTrue(kind.getFlags().has(TrapEnum.TRF_ONETIME));
        assertFalse(kind.getFlags().has(TrapEnum.TRF_PIT));
    }

    @Test
    void noFlagsGiveEmptySets() {
        TrapKind kind = one(withFlags(List.of(), List.of()));
        assertTrue(kind.getFlags().isEmpty());
        assertTrue(kind.getSaveFlags().isEmpty());
    }

    @Test
    void unknownTrapFlagThrows() {
        assertThrowsFor(withFlags(List.of("TRAP", "BOGUS"), List.of()));
    }

    @Test
    void trapFlagIsCaseExact() {
        // lookup_flag matches with streq
        assertThrowsFor(withFlags(List.of("trap"), List.of()));
    }

    @Test
    void trapFlagNoneThrows() {
        // "NONE" sits at index 0 of trap_flags; lookup_flag starts at FLAG_START (1)
        assertThrowsFor(withFlags(List.of("NONE"), List.of()));
    }

    @Test
    void trapFlagMaxThrows() {
        // trap_flags holds no "MAX"
        assertThrowsFor(withFlags(List.of("MAX"), List.of()));
    }

    @Test
    void saveFlagIsSet() {
        // trap.txt: save:FEATHER
        TrapKind kind = one(withFlags(List.of(), List.of("FEATHER")));
        assertTrue(kind.getSaveFlags().has(ObjectFlag.OF_FEATHER));
    }

    @Test
    void unknownSaveFlagThrows() {
        assertThrowsFor(withFlags(List.of(), List.of("BOGUS")));
    }

    @Test
    void saveFlagIsCaseExact() {
        assertThrowsFor(withFlags(List.of(), List.of("feather")));
    }

    @Test
    void saveFlagNoneThrows() {
        // "NONE" sits at index 0 of list_obj_flag_names
        assertThrowsFor(withFlags(List.of(), List.of("NONE")));
    }

    @Test
    void saveFlagMaxThrows() {
        assertThrowsFor(withFlags(List.of(), List.of("MAX")));
    }

    // --- effects ---

    private static EffectParseRecord untypedEffect() {
        return new EffectParseRecord("", "", "", "", "", "", "", "", "", "", null, 5);
    }

    @Test
    void badPrimaryEffectThrows() {
        TrapParseRecord r = new TrapParseRecord("trap", "", "rune", "", "", "", "",
                "^", "r", "1", "1", "0", "", List.of(), List.of(),
                List.of(untypedEffect()), new ArrayList<>(), 1);
        assertThrowsFor(r);
    }

    @Test
    void badExtraEffectThrows() {
        TrapParseRecord r = new TrapParseRecord("trap", "", "rune", "", "", "", "",
                "^", "r", "1", "1", "0", "", List.of(), List.of(),
                new ArrayList<>(), List.of(untypedEffect()), 1);
        assertThrowsFor(r);
    }

    @Test
    void badLaterRecordStopsTheWholeLoad() {
        // C stops the load at the first handler error; nothing is returned
        assertThrows(DataFileParseException.class, () -> new TrapAssembler().assemble(
                List.of(plain(1), withAppear("-1", "1", "0")), new ArrayList<>(), FILE));
    }
}
