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

package uk.co.jackoftradesltd.backend.parser.monsterspell;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord.MonsterSpellLevelParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellLevel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MonsterSpellLevelAssembler} on hand-built {@link MonsterSpellLevelParseRecord}s, so
 * the chain of levels is exercised without the grammar in front of it.
 *
 * <p>Every expected value comes from the C source, not from the Java. In {@code mon-init.c},
 * {@code parse_mon_spell_name()} allocates a zeroed level (power 0, no text, colours 0 which is
 * {@code COLOUR_DARK}); {@code parse_mon_spell_power_cutoff()} appends another zeroed level
 * carrying {@code parser_getint()}'s value; and the lore, colour and message handlers all walk to
 * the last level, {@code string_append()}ing text and overwriting a colour. The colour handlers
 * go through {@code color_text_to_attr()} (more than one character, case-blind name) or
 * {@code color_char_to_attr()} (one character, case-sensitive), both in {@code z-color.c}, and
 * both answer white for something they do not know and {@code COLOUR_DARK} for a space.
 *
 * <p>Class MonsterSpellLevelAssemblerTest coded and commented in full on 261027.
 */
class MonsterSpellLevelAssemblerTest {

    private static final String FILE = "monster_spell.txt";

    /** A record with the grammar's defaults: empty text, no colours, and the given power (or null). */
    private static MonsterSpellLevelParseRecord level(String power) {
        return new MonsterSpellLevelParseRecord(power, "", null, null, null, "", "", "", "", 5);
    }

    private static MonsterSpellLevelParseRecord text(String power, String lore, String vis,
                                                      String invis, String miss, String save) {
        return new MonsterSpellLevelParseRecord(power, lore, null, null, null, vis, invis, miss, save, 5);
    }

    private static MonsterSpellLevelParseRecord colours(String power, String base, String resist,
                                                         String immune) {
        return new MonsterSpellLevelParseRecord(power, "", base, resist, immune, "", "", "", "", 5);
    }

    private static List<MonsterSpellLevel> assemble(MonsterSpellLevelParseRecord... records) {
        return new MonsterSpellLevelAssembler().assemble(List.of(records), new ArrayList<>(), FILE);
    }

    private static List<Integer> powers(List<MonsterSpellLevel> levels) {
        List<Integer> powers = new ArrayList<>();
        for (MonsterSpellLevel level : levels) {
            powers.add(level.getPower());
        }
        return powers;
    }

    private static void assertBlank(MonsterSpellLevel level) {
        assertEquals(0, level.getPower());
        assertNull(level.getLoreDesc());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrImmune());
        assertNull(level.getMessage());
        assertNull(level.getBlindMessage());
        assertNull(level.getMissMessage());
        assertNull(level.getSaveMessage());
    }

    private static ColourEnum baseColourOf(String colour) {
        return assemble(colours(null, colour, null, null)).get(0).getLoreAttr();
    }

    // ---- The implicit level 0 ----------------------------------------------

    @Test
    void noRecordsStillGiveTheOneBlankLevelCAllocatesAtNameTime() {
        List<MonsterSpellLevel> levels = assemble();

        assertEquals(1, levels.size());
        assertBlank(levels.get(0));
    }

    @Test
    void aFirstRecordWithNoPowerIsLevelZeroItself() {
        List<MonsterSpellLevel> levels = assemble(text(null, "lore", "seen", "unseen", "miss", "save"));

        assertEquals(1, levels.size());
        MonsterSpellLevel only = levels.get(0);
        assertEquals(0, only.getPower());
        assertEquals("lore", only.getLoreDesc());
        assertEquals("seen", only.getMessage());
        assertEquals("unseen", only.getBlindMessage());
        assertEquals("miss", only.getMissMessage());
        assertEquals("save", only.getSaveMessage());
    }

    @Test
    void aLeadingPowerCutoffKeepsABlankLevelZeroInFrontOfIt() {
        List<MonsterSpellLevel> levels = assemble(text("20", "lore", "", "", "", ""));

        assertEquals(List.of(0, 20), powers(levels));
        assertBlank(levels.get(0));
        assertEquals("lore", levels.get(1).getLoreDesc());
    }

    @Test
    void aLeadingPowerCutoffOfZeroIsStillALevelOfItsOwn() {
        // parse_mon_spell_power_cutoff() appends whatever power it is given, 0 included.
        assertEquals(List.of(0, 0), powers(assemble(level("0"))));
    }

    @Test
    void theNumberOfLevelsIsOneMoreThanTheNumberOfPowerCutoffs() {
        assertEquals(1, assemble(level(null)).size());
        assertEquals(2, assemble(level(null), level("10")).size());
        assertEquals(4, assemble(level(null), level("10"), level("20"), level("30")).size());
    }

    // ---- Powers --------------------------------------------------------------

    @Test
    void powersComeBackInFileOrderWithNoCheckThatTheyRise() {
        // C never sorts or compares: the chain is in the order the lines were read.
        assertEquals(List.of(0, 50, 25, 25), powers(assemble(level(null), level("50"), level("25"), level("25"))));
    }

    @Test
    void aPowerIsReadByStrtolInBaseZero() {
        // parser_getint() is strtol(tok, &z, 0): 0x prefix is hex, a leading 0 is octal.
        assertEquals(25, powers(assemble(level("25"))).get(1));
        assertEquals(16, powers(assemble(level("0x10"))).get(1));
        assertEquals(8, powers(assemble(level("010"))).get(1));
        assertEquals(-5, powers(assemble(level("-5"))).get(1));
    }

    @Test
    void aPowerWithNoDigitsStopsTheLoad() {
        // parser_parse() answers PARSE_ERROR_NOT_NUMBER when strtol consumes nothing.
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(new MonsterSpellLevelParseRecord("abc", "", null, null, null, "", "", "", "", 7)));

        assertEquals("Monster spell level beginning at line: 7 of file: " + FILE
                + " has an invalid power cutoff value: abc", e.getMessage());
    }

    @Test
    void aBadPowerLaterInTheListStopsTheLoadAllTheSame() {
        assertThrows(DataFileParseException.class,
                () -> assemble(level(null), level("10"), level("x")));
    }

    // ---- Repeated text and colours go to the last level ---------------------------

    @Test
    void aPowerLessRecordAfterTheFirstIsAppendedToTheLevelBeforeIt() {
        // The grammar emits one when lore: or message-*: lines come back after an effect:, hit: or
        // msgt: line. C's handlers work on the last level, string_append()ing each line.
        List<MonsterSpellLevel> levels = assemble(
                text(null, "one", "a", "b", "c", "d"),
                text(null, " two", "A", "B", "C", "D"));

        assertEquals(1, levels.size());
        MonsterSpellLevel merged = levels.get(0);
        assertEquals("one two", merged.getLoreDesc());
        assertEquals("aA", merged.getMessage());
        assertEquals("bB", merged.getBlindMessage());
        assertEquals("cC", merged.getMissMessage());
        assertEquals("dD", merged.getSaveMessage());
    }

    @Test
    void aPowerLessRecordIsAppendedToTheLastLevelAndKeepsItsPower() {
        List<MonsterSpellLevel> levels = assemble(
                text(null, "zero", "", "", "", ""),
                text("40", "forty", "", "", "", ""),
                text(null, " more", "", "", "", ""));

        assertEquals(List.of(0, 40), powers(levels));
        assertEquals("zero", levels.get(0).getLoreDesc());
        assertEquals("forty more", levels.get(1).getLoreDesc());
    }

    @Test
    void textMissingFromOneSideOfAMergeIsLeftAsTheOtherSideHadIt() {
        // string_append(NULL, s) copies s, and appending nothing leaves the string alone.
        List<MonsterSpellLevel> levels = assemble(
                text(null, "lore", "", "", "", ""),
                text(null, "", "seen", "", "", ""));

        MonsterSpellLevel merged = levels.get(0);
        assertEquals("lore", merged.getLoreDesc());
        assertEquals("seen", merged.getMessage());
        assertNull(merged.getBlindMessage());
        assertNull(merged.getMissMessage());
        assertNull(merged.getSaveMessage());
    }

    @Test
    void aMergeOfTwoEmptyTextsLeavesNullAsCsUnsetStringIs() {
        List<MonsterSpellLevel> levels = assemble(level(null), level(null));

        assertEquals(1, levels.size());
        assertBlank(levels.get(0));
    }

    @Test
    void aColourInALaterRecordReplacesAndOneNotNamedIsKept() {
        // The colour handlers assign: lore_attr = attr. Resist and immune are not touched by a
        // lore-color-base: line.
        List<MonsterSpellLevel> levels = assemble(
                colours(null, "Red", "Green", "Blue"),
                colours(null, "Yellow", null, null));

        assertEquals(1, levels.size());
        assertEquals(ColourEnum.COLOUR_YELLOW, levels.get(0).getLoreAttr());
        assertEquals(ColourEnum.COLOUR_GREEN, levels.get(0).getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_BLUE, levels.get(0).getLoreAttrImmune());
    }

    @Test
    void aLaterRecordNamingTheDarkColourStillReplaces() {
        // Naming COLOUR_DARK is a directive like any other, not the same as saying nothing.
        List<MonsterSpellLevel> levels = assemble(
                colours(null, "Red", null, null),
                colours(null, "Dark", null, null));

        assertEquals(ColourEnum.COLOUR_DARK, levels.get(0).getLoreAttr());
    }

    @Test
    void aRecordWithAPowerNeverMergesEvenAtTheSamePower() {
        List<MonsterSpellLevel> levels = assemble(
                text("10", "a", "", "", "", ""),
                text("10", "b", "", "", "", ""));

        assertEquals(List.of(0, 10, 10), powers(levels));
        assertEquals("a", levels.get(1).getLoreDesc());
        assertEquals("b", levels.get(2).getLoreDesc());
    }

    // ---- Colours -----------------------------------------------------------------

    @Test
    void aLevelWithNoColourDirectiveHoldsColourDarkAsCsZeroedFieldDoes() {
        MonsterSpellLevel level = assemble(text(null, "lore", "", "", "", "")).get(0);

        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_DARK, level.getLoreAttrImmune());
    }

    @Test
    void eachOfTheThreeColoursIsReadIntoItsOwnField() {
        MonsterSpellLevel level = assemble(colours(null, "Orange", "Light Red", "Teal")).get(0);

        assertEquals(ColourEnum.COLOUR_ORANGE, level.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, level.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_TEAL, level.getLoreAttrImmune());
    }

    @Test
    void aColourNameIsCaseBlindAsMyStricmpIs() {
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, baseColourOf("Light Red"));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, baseColourOf("light red"));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, baseColourOf("LIGHT RED"));
    }

    @Test
    void aColourCharacterIsCaseSensitive() {
        // color_char_to_attr() compares index_char exactly: r is Red, R is Light Red.
        assertEquals(ColourEnum.COLOUR_RED, baseColourOf("r"));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, baseColourOf("R"));
        assertEquals(ColourEnum.COLOUR_WHITE, baseColourOf("w"));
        assertEquals(ColourEnum.COLOUR_LIGHT_SLATE, baseColourOf("W"));
    }

    @Test
    void aColourNothingMatchesReadsWhiteNotAFailure() {
        // Both lookups end "Default to white" / "we assume white", and the handlers only reject a
        // negative answer, which neither gives.
        assertEquals(ColourEnum.COLOUR_WHITE, baseColourOf("Nonsense"));
        assertEquals(ColourEnum.COLOUR_WHITE, baseColourOf("#"));
        assertEquals(ColourEnum.COLOUR_WHITE, baseColourOf("q"));
    }

    @Test
    void anUnknownColourOnAnyOfTheThreeDirectivesReadsWhite() {
        MonsterSpellLevel level = assemble(colours(null, "Nonsense", "#", "zzz")).get(0);

        assertEquals(ColourEnum.COLOUR_WHITE, level.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_WHITE, level.getLoreAttrResist());
        assertEquals(ColourEnum.COLOUR_WHITE, level.getLoreAttrImmune());
    }

    @Test
    void aSpaceColourCharacterIsDarkAsInColorCharToAttr() {
        assertEquals(ColourEnum.COLOUR_DARK, baseColourOf(" "));
    }

    // ---- The error list -----------------------------------------------------------

    @Test
    void nothingIsWrittenToTheErrorList() {
        List<String> errors = new ArrayList<>();

        new MonsterSpellLevelAssembler().assemble(List.of(colours("10", "Nonsense", "#", "zzz")), errors, FILE);

        assertTrue(errors.isEmpty(), errors::toString);
    }
}
