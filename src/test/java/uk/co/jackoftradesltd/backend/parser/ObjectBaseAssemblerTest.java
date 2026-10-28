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
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseAssembler;
import uk.co.jackoftradesltd.backend.parser.objectbase.ObjectBaseParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ObjectBaseAssembler}.
 *
 * <p>These build {@link ObjectBaseParseRecord}s directly, with no grammar, so text the lexer would
 * never produce (a lower-case flag, an empty number, a colour of several words) still reaches the
 * assembler. End-to-end cases against the shipped file live in {@code ObjectBaseReaderTest}.
 *
 * <p>The expected values are worked out from {@code obj-init.c}, not from the Java:
 * <ul>
 *   <li>{@code parse_object_base_flags} offers each {@code strtok} token to {@code grab_flag} on
 *       the object-flag table, {@code grab_flag} on the kind-flag table and
 *       {@code grab_element_flag}, and a token none of them accepts is
 *       {@code PARSE_ERROR_INVALID_FLAG}. {@code lookup_flag} starts at {@code FLAG_START} (1), so
 *       the {@code NONE} placeholder at index 0 of either table never matches; {@code MAX} is the
 *       one real name in both tables;</li>
 *   <li>{@code grab_element_flag} splits at the first underscore, needs the rest to equal one of
 *       the {@code ELEM_MAX} element names exactly, and then compares only as many characters as
 *       the tag has with {@code strncmp}, so {@code HAT_ACID} counts as {@code HATES_ACID};</li>
 *   <li>{@code parse_object_base_graphics}: one character goes to {@code color_char_to_attr}
 *       (case-sensitive, white if unknown), more than one to {@code color_text_to_attr}
 *       ({@code my_stricmp}, white if unknown);</li>
 *   <li>{@code parse_object_base_break} and {@code _max_stack}: {@code parser_getint}, which is
 *       {@code strtol} in base 0, and a default of 0 when the file gave none;</li>
 *   <li>{@code finish_parse_object_base}: tval 0 is a zeroed {@code kb_info} slot with no name,
 *       colour 0, no flags and no limits;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 * One accepted deviation is pinned as well: a number outside the {@code int} range is clamped where
 * C keeps the low 32 bits.
 *
 * <p>Class ObjectBaseAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class ObjectBaseAssemblerTest {

    private static final String FILE = "object_base.txt";

    private static ObjectBaseParseRecord record(String name, String tVal, String colour,
                                                List<String> flags, String breakChance,
                                                String maxStack) {
        return new ObjectBaseParseRecord(name, tVal, colour, flags, breakChance, maxStack, 7);
    }

    private static ObjectBaseParseRecord withFlags(String... flags) {
        return record("S~", "sword", "white", List.of(flags), "10", "40");
    }

    private static List<ObjectBase> assemble(ObjectBaseParseRecord... records) {
        return new ObjectBaseAssembler().assemble(List.of(records), new ArrayList<>(), FILE);
    }

    /** The one declared base, with the trailing sentinel checked and dropped. */
    private static ObjectBase one(ObjectBaseParseRecord record) {
        List<ObjectBase> bases = assemble(record);
        assertEquals(2, bases.size(), "the record and the sentinel");
        assertEquals(TValue.TV_NONE, bases.get(1).gettVal());
        return bases.getFirst();
    }

    private static void assertRefused(ObjectBaseParseRecord record, String expectedInMessage) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class, () -> assemble(record));
        assertTrue(thrown.getMessage().contains(expectedInMessage), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 7"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    private static Set<ElementInfoEnum> bitsOn(ObjectBase base, ElementEnum element) {
        return Set.copyOf(base.getElementMap().get(element).getFlags().toList());
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void everyFieldOfOneRecordLandsOnTheBase() {
        ObjectBase base = one(record("Bladed weapon~", "sword", "white",
                List.of("HATES_ACID", "SHOW_DICE"), "35", "20"));

        assertEquals(TValue.TV_SWORD, base.gettVal());
        assertEquals("Bladed weapon~", base.getName());
        assertEquals(ColourEnum.COLOUR_WHITE, base.getAttr());
        assertEquals(35, base.getBreakPerc());
        assertEquals(20, base.getMaxStack());
        assertTrue(base.getKindFlags().has(ObjectKindFlag.KF_SHOW_DICE));
        assertEquals(Set.of(ElementEnum.ELEM_ACID), base.getElementMap().keySet());
        assertEquals(0, base.getNumSvals(), "C: kb->num_svals = 0");
    }

    /** {@code parser_hasval(p, "name")} false leaves C's {@code name} {@code NULL}. */
    @Test
    void anEmptyPluralNameIsNull() {
        assertNull(one(record("", "gold", "yellow", List.of(), "10", "40")).getName());
    }

    @Test
    void recordsComeBackInFileOrderWithTheSentinelLast() {
        List<ObjectBase> bases = assemble(
                record("A~", "shot", "white", List.of(), "1", "1"),
                record("B~", "arrow", "white", List.of(), "2", "2"),
                record("C~", "bolt", "white", List.of(), "3", "3"));

        assertEquals(List.of(TValue.TV_SHOT, TValue.TV_ARROW, TValue.TV_BOLT, TValue.TV_NONE),
                bases.stream().map(ObjectBase::gettVal).toList());
    }

    /**
     * C's chain overwrites {@code kb_info[tval]} from the last record back, so the earlier record
     * of a repeated tval wins; the list keeps both and a first-match search finds the same one.
     */
    @Test
    void aRepeatedTvalKeepsBothRecordsEarlierFirst() {
        List<ObjectBase> bases = assemble(
                record("First~", "sword", "white", List.of(), "1", "1"),
                record("Second~", "sword", "red", List.of(), "2", "2"));

        assertEquals(3, bases.size());
        assertEquals("First~", bases.get(0).getName());
        assertEquals("Second~", bases.get(1).getName());
    }

    @Test
    void theErrorsListIsNeverWrittenTo() {
        List<String> errors = new ArrayList<>();

        new ObjectBaseAssembler().assemble(List.of(withFlags("SHOW_DICE")), errors, FILE);

        assertTrue(errors.isEmpty());
    }

    // ---- The sentinel ------------------------------------------------------

    /** {@code kb_info} is zero-allocated: tval 0, no name, colour 0, no flags, no limits. */
    @Test
    void theSentinelIsCsZeroedSlot() {
        List<ObjectBase> bases = assemble(withFlags("SHOW_DICE"));
        ObjectBase none = bases.getLast();

        assertEquals(TValue.TV_NONE, none.gettVal());
        assertNull(none.getName());
        assertEquals(ColourEnum.COLOUR_DARK, none.getAttr());
        assertTrue(none.getKindFlags().isEmpty());
        assertTrue(none.getFlags().isEmpty());
        assertTrue(none.getElementMap().isEmpty());
        assertEquals(0, none.getBreakPerc());
        assertEquals(0, none.getMaxStack());
    }

    /** The sentinel is not driven by a record, so it is there when the file declared none. */
    @Test
    void anEmptyRecordListStillGivesTheSentinel() {
        List<ObjectBase> bases = assemble();

        assertEquals(1, bases.size());
        assertEquals(TValue.TV_NONE, bases.getFirst().gettVal());
    }

    // ---- Tval (tval_find_idx) ----------------------------------------------

    @ParameterizedTest
    @CsvSource({
            "sword, TV_SWORD",
            "SWORD, TV_SWORD",
            "Light, TV_LIGHT",
            "magic book, TV_MAGIC_BOOK",
            "digger, TV_DIGGING",
            "soft armor, TV_SOFT_ARMOR",
            "soft armour, TV_SOFT_ARMOR",
            "hard armour, TV_HARD_ARMOR",
            "dragon armor, TV_DRAG_ARMOR",
            "dragon armour, TV_DRAG_ARMOR",
            "9, TV_SWORD",
            "none, TV_NONE",
            "0, TV_NONE"
    })
    void aTvalNameOrNumberResolvesAsInC(String text, TValue expected) {
        assertEquals(expected, one(record("S~", text, "white", List.of(), "10", "40")).gettVal());
    }

    @ParameterizedTest
    @ValueSource(strings = {"notatval", "TV_SWORD", "sword2", "9999", "-1", "5x"})
    void aTvalCRejectsEndsTheLoad(String text) {
        assertRefused(record("S~", text, "white", List.of(), "10", "40"), "illegal tValue value");
    }

    // ---- Colour (color_char_to_attr / color_text_to_attr) -------------------

    @ParameterizedTest
    @CsvSource({
            "slate, COLOUR_SLATE",
            "light umber, COLOUR_LIGHT_UMBER",
            "LIGHT UMBER, COLOUR_LIGHT_UMBER",
            "Light Umber, COLOUR_LIGHT_UMBER",
            "light purple, COLOUR_LIGHT_PURPLE",
            "w, COLOUR_WHITE",
            "W, COLOUR_LIGHT_SLATE",
            "u, COLOUR_UMBER",
            "U, COLOUR_LIGHT_UMBER"
    })
    void aColourNameOrIndexCharacterResolves(String text, ColourEnum expected) {
        assertEquals(expected, one(record("S~", "sword", text, List.of(), "10", "40")).getAttr());
    }

    /** Both C lookups default to white, with no error. */
    @ParameterizedTest
    @ValueSource(strings = {"nosuchcolour", "light umbre", "#", "q"})
    void anUnknownColourIsWhiteAsInC(String text) {
        assertEquals(ColourEnum.COLOUR_WHITE,
                one(record("S~", "sword", text, List.of(), "10", "40")).getAttr());
    }

    // ---- Element flags (grab_element_flag) ----------------------------------

    @Test
    void aHatesFlagSetsOnlyTheHatesBit() {
        ObjectBase base = one(withFlags("HATES_ACID"));

        assertEquals(Set.of(ElementEnum.ELEM_ACID), base.getElementMap().keySet());
        assertEquals(Set.of(ElementInfoEnum.EL_INFO_HATES), bitsOn(base, ElementEnum.ELEM_ACID));
        assertEquals(0, base.getElementMap().get(ElementEnum.ELEM_ACID).getResLevel());
        assertTrue(base.getKindFlags().isEmpty());
        assertTrue(base.getFlags().isEmpty());
    }

    @Test
    void anIgnoreFlagSetsOnlyTheIgnoreBit() {
        ObjectBase base = one(withFlags("IGNORE_FIRE"));

        assertEquals(Set.of(ElementEnum.ELEM_FIRE), base.getElementMap().keySet());
        assertEquals(Set.of(ElementInfoEnum.EL_INFO_IGNORE), bitsOn(base, ElementEnum.ELEM_FIRE));
    }

    @Test
    void hatesAndIgnoreOnTheSameElementKeepBothBits() {
        ObjectBase base = one(withFlags("HATES_COLD", "IGNORE_COLD"));

        assertEquals(Set.of(ElementEnum.ELEM_COLD), base.getElementMap().keySet());
        assertEquals(Set.of(ElementInfoEnum.EL_INFO_HATES, ElementInfoEnum.EL_INFO_IGNORE),
                bitsOn(base, ElementEnum.ELEM_COLD));
    }

    /** The element name is everything after the first underscore, so {@code HOLY_ORB} works. */
    @Test
    void anElementNameWithAnUnderscoreResolves() {
        ObjectBase base = one(withFlags("HATES_HOLY_ORB"));

        assertEquals(Set.of(ElementEnum.ELEM_HOLY_ORB), base.getElementMap().keySet());
    }

    /** All 25 real elements are accepted by both prefixes. */
    @Test
    void everyRealElementIsAcceptedByBothPrefixes() {
        List<String> flags = new ArrayList<>();
        for (ElementEnum element : ElementEnum.values()) {
            if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) continue;
            String name = element.name().substring("ELEM_".length());
            flags.add("HATES_" + name);
            flags.add("IGNORE_" + name);
        }
        ObjectBase base = one(withFlags(flags.toArray(String[]::new)));

        assertEquals(25, base.getElementMap().size());
        base.getElementMap().values().forEach(info -> assertEquals(
                Set.of(ElementInfoEnum.EL_INFO_HATES, ElementInfoEnum.EL_INFO_IGNORE),
                Set.copyOf(info.getFlags().toList())));
    }

    /**
     * {@code strncmp(flag_name, "HATES", under - flag_name)} compares only as many characters as the
     * tag has, so any non-empty prefix of the keyword is accepted.
     */
    @ParameterizedTest
    @CsvSource({"H_ACID, HATES", "HAT_ACID, HATES", "HATE_ACID, HATES", "I_ACID, IGNORE", "IGN_ACID, IGNORE",
            "IGNOR_ACID, IGNORE"})
    void aTagThatIsAPrefixOfTheKeywordCounts(String flag, String keyword) {
        ObjectBase base = one(withFlags(flag));

        assertEquals(Set.of(keyword.equals("HATES") ? ElementInfoEnum.EL_INFO_HATES : ElementInfoEnum.EL_INFO_IGNORE),
                bitsOn(base, ElementEnum.ELEM_ACID));
    }

    /**
     * A tag that is not a prefix of either keyword, or runs past it, is not an element flag, and
     * then no flag table has it either.
     */
    @ParameterizedTest
    @ValueSource(strings = {"HATESX_ACID", "IGNORED_ACID", "LOVES_ACID", "HI_ACID", "ACID_HATES"})
    void aTagThatIsNotAPrefixIsRefused(String flag) {
        assertRefused(withFlags(flag), "illegal flag value");
    }

    /** {@code element_names} has exactly the elements: not the placeholder, not the count. */
    @ParameterizedTest
    @ValueSource(strings = {"HATES_NONE", "HATES_MAX", "IGNORE_NONE", "IGNORE_MAX"})
    void thePlaceholderAndCountAreNotElements(String flag) {
        assertRefused(withFlags(flag), "illegal sentinel element flag value");
    }

    /** The element name must equal C's exactly: {@code streq}, so no case folding. */
    @ParameterizedTest
    @ValueSource(strings = {"HATES_acid", "HATES_Acid", "HATES_", "HATES_ACID ", "HATES_WATERY"})
    void anElementNameThatIsNotExactlyOneIsRefused(String flag) {
        assertRefused(withFlags(flag), "illegal hates element flag value");
    }

    @Test
    void anUnknownIgnoreElementIsRefused() {
        assertRefused(withFlags("IGNORE_SPAM"), "illegal ignore element flag value");
    }

    // ---- Kind and object flags (grab_flag) ----------------------------------

    @Test
    void aKindFlagSetsTheKindBitOnly() {
        ObjectBase base = one(withFlags("SHOW_DICE", "EASY_KNOW"));

        assertEquals(Set.of(ObjectKindFlag.KF_SHOW_DICE, ObjectKindFlag.KF_EASY_KNOW),
                Set.copyOf(base.getKindFlags().toList()));
        assertTrue(base.getFlags().isEmpty());
        assertTrue(base.getElementMap().isEmpty());
    }

    /**
     * Object flags are in the same {@code flags:} line and go to {@code kb->flags}. The names with an
     * underscore ({@code SUST_STR}, {@code IMPAIR_HP}) must not be mistaken for element flags: their
     * tags {@code SUST} and {@code IMPAIR} are not prefixes of {@code HATES} or {@code IGNORE}.
     */
    @Test
    void anObjectFlagSetsTheObjectBitOnly() {
        ObjectBase base = one(withFlags("FEATHER", "SUST_STR", "IMPAIR_HP"));

        assertEquals(Set.of(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_IMPAIR_HP),
                Set.copyOf(base.getFlags().toList()));
        assertTrue(base.getKindFlags().isEmpty());
        assertTrue(base.getElementMap().isEmpty());
    }

    @Test
    void allThreeKindsOfFlagCanShareOneRecord() {
        ObjectBase base = one(withFlags("HATES_ACID", "IGNORE_ELEC", "SHOW_MULT", "REGEN"));

        assertEquals(Set.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC), base.getElementMap().keySet());
        assertTrue(base.getKindFlags().has(ObjectKindFlag.KF_SHOW_MULT));
        assertTrue(base.getFlags().has(ObjectFlag.OF_REGEN));
    }

    /** No flags at all is fine: C's loop never runs. */
    @Test
    void aRecordWithNoFlagsHasNoneSet() {
        ObjectBase base = one(withFlags());

        assertTrue(base.getKindFlags().isEmpty());
        assertTrue(base.getFlags().isEmpty());
        assertTrue(base.getElementMap().isEmpty());
    }

    /** {@code lookup_flag} starts past index 0, so the {@code NONE} placeholder never matches. */
    @Test
    void theNoneFlagIsRefused() {
        assertRefused(withFlags("NONE"), "illegal sentinal object/object kind flag value");
    }

    /** {@code MAX} is in both tables after the placeholder, so both counts come on. */
    @Test
    void theMaxFlagIsInBothTablesAndSetsBothSentinelBits() {
        ObjectBase base = one(withFlags("MAX"));

        assertTrue(base.getKindFlags().has(ObjectKindFlag.KF_MAX));
        assertTrue(base.getFlags().has(ObjectFlag.OF_MAX));
    }

    /** {@code streq} again: no case folding, and a prefix is not a name. */
    @ParameterizedTest
    @ValueSource(strings = {"show_dice", "Show_Dice", "SHOW", "SHOW_DICE2", "SHOW_2", "KF_SHOW_DICE",
            "OF_FEATHER", "NOTAFLAG"})
    void aFlagNameThatIsNotExactlyOneIsRefused(String flag) {
        assertRefused(withFlags(flag), "illegal flag value");
    }

    /** C stops at the first bad token, so a good token before or after it does not save the record. */
    @Test
    void oneBadFlagAmongGoodOnesEndsTheLoad() {
        assertRefused(withFlags("SHOW_DICE", "NOTAFLAG", "HATES_ACID"), "NOTAFLAG");
    }

    // ---- Numbers (parser_getint) --------------------------------------------

    @ParameterizedTest
    @CsvSource({"0, 0", "10, 10", "100, 100", "-5, -5", "+7, 7", "010, 8", "0x10, 16", "0X1f, 31", "-010, -8",
            "07, 7", "08, 0"})
    void aBreakChanceIsReadAsStrtolBaseZero(String text, int expected) {
        assertEquals(expected, one(record("S~", "sword", "white", List.of(), text, "40")).getBreakPerc());
    }

    @ParameterizedTest
    @CsvSource({"0, 0", "40, 40", "1, 1", "-1, -1", "0x28, 40", "050, 40"})
    void aMaxStackIsReadAsStrtolBaseZero(String text, int expected) {
        assertEquals(expected, one(record("S~", "sword", "white", List.of(), "10", text)).getMaxStack());
    }

    /**
     * The grammar leaves a number the file never gave as the empty string. C had it from the zeroed
     * defaults, so it is 0, and the other field is untouched.
     */
    @Test
    void anAbsentNumberIsZero() {
        ObjectBase base = one(record("S~", "sword", "white", List.of(), "", "40"));
        assertEquals(0, base.getBreakPerc());
        assertEquals(40, base.getMaxStack());

        base = one(record("S~", "sword", "white", List.of(), "7", ""));
        assertEquals(7, base.getBreakPerc());
        assertEquals(0, base.getMaxStack());
    }

    /** {@code strtol} reads no digit: {@code PARSE_ERROR_NOT_NUMBER}. */
    @ParameterizedTest
    @ValueSource(strings = {"abc", "x10", "+-5", "  "})
    void aBreakChanceWithNoDigitEndsTheLoad(String text) {
        assertRefused(record("S~", "sword", "white", List.of(), text, "40"), "break chance");
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "x10", "+-5", "  "})
    void aMaxStackWithNoDigitEndsTheLoad(String text) {
        assertRefused(record("S~", "sword", "white", List.of(), "10", text), "max stack");
    }

    /**
     * Accepted deviation ({@code docs/Accepted_Deviations.md}, "An integer too big for its C type is
     * clamped, where C wraps it"): C stores the {@code long} {@code strtol} result in a 32-bit int,
     * Java clamps.
     */
    @Test
    void aNumberOutsideTheIntRangeIsClampedWhereCWraps() {
        ObjectBase base = one(record("S~", "sword", "white", List.of(), "99999999999999999999",
                "-99999999999999999999"));

        assertEquals(Integer.MAX_VALUE, base.getBreakPerc());
        assertEquals(Integer.MIN_VALUE, base.getMaxStack());
    }

    // ---- The stop-the-load policy -------------------------------------------

    /** The line and file in the message are those of the record at fault, not the first record. */
    @Test
    void theMessageNamesTheRecordAtFault() {
        ObjectBaseParseRecord good = new ObjectBaseParseRecord("A~", "shot", "white", List.of(), "1", "1", 4);
        ObjectBaseParseRecord bad = new ObjectBaseParseRecord("B~", "nope", "white", List.of(), "1", "1", 11);

        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new ObjectBaseAssembler().assemble(List.of(good, bad), new ArrayList<>(), "mod_base.txt"));

        assertTrue(thrown.getMessage().contains("line: 11"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("mod_base.txt"), thrown::getMessage);
        assertFalse(thrown.getMessage().contains("line: 4"), thrown::getMessage);
    }

    /** A bad record after good ones still ends the load: no partial list comes back. */
    @Test
    void aBadLaterRecordDiscardsTheGoodEarlierOnes() {
        ObjectBaseParseRecord good = record("A~", "shot", "white", List.of(), "1", "1");
        ObjectBaseParseRecord bad = withFlags("NOTAFLAG");

        assertThrows(DataFileParseException.class, () -> assemble(good, bad));
    }
}
