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
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseUtils;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ParseUtils#grabElementFlag}, the port of {@code grab_element_flag} in
 * {@code obj-init.c}.
 *
 * <p>The expected values are worked out from the C, not from the Java. C cuts the word at the first
 * underscore, runs through {@code element_names} (the {@code list-elements.h} names, 25 of them, no
 * {@code NONE} or {@code MAX}) for one that {@code streq}-matches the text after it, and then tests
 * the text before it with {@code strncmp(flag_name, "IGNORE", under - flag_name)} and then with
 * {@code "HATES"}. Because the length handed to {@code strncmp} is the length of the text before the
 * underscore, that text need only be a prefix of the word; an empty one compares nothing and matches
 * {@code IGNORE}; and one longer than the word reaches the word's terminating NUL and mismatches.
 *
 * @author Rowan Crowther
 */
class ParseUtilsGrabElementFlagTest {

    private static Map<ElementEnum, ElementInfo> newMap() {
        return new EnumMap<>(ElementEnum.class);
    }

    /** The 25 real elements: everything but the Java-only {@code ELEM_NONE} and {@code ELEM_MAX}. */
    private static boolean real(ElementEnum e) {
        return e != ElementEnum.ELEM_NONE && e != ElementEnum.ELEM_MAX;
    }

    private static String bare(ElementEnum e) {
        return e.name().substring("ELEM_".length());
    }

    @ParameterizedTest(name = "[{index}] IGNORE_ and HATES_ {0}")
    @EnumSource(ElementEnum.class)
    void everyRealElementTakesBothFlagsAndNoneAndMaxTakeNeither(ElementEnum e) {
        Map<ElementEnum, ElementInfo> map = newMap();
        if (real(e)) {
            assertTrue(ParseUtils.grabElementFlag(map, "IGNORE_" + bare(e)));
            assertTrue(map.get(e).has(ElementInfoEnum.EL_INFO_IGNORE));
            assertFalse(map.get(e).has(ElementInfoEnum.EL_INFO_HATES));
            assertTrue(ParseUtils.grabElementFlag(map, "HATES_" + bare(e)));
            assertTrue(map.get(e).has(ElementInfoEnum.EL_INFO_HATES));
            assertEquals(1, map.size());
        } else {
            assertFalse(ParseUtils.grabElementFlag(map, "IGNORE_" + bare(e)));
            assertFalse(ParseUtils.grabElementFlag(map, "HATES_" + bare(e)));
            assertTrue(map.isEmpty());
        }
    }

    @Test
    void ignoreSetsOnlyIgnoreOnlyOnTheNamedElement() {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, "IGNORE_ACID"));
        assertEquals(1, map.size());
        ElementInfo acid = map.get(ElementEnum.ELEM_ACID);
        assertTrue(acid.has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(acid.has(ElementInfoEnum.EL_INFO_HATES));
        assertFalse(acid.has(ElementInfoEnum.EL_INFO_RANDOM));
    }

    @Test
    void hatesSetsOnlyHates() {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, "HATES_FIRE"));
        ElementInfo fire = map.get(ElementEnum.ELEM_FIRE);
        assertTrue(fire.has(ElementInfoEnum.EL_INFO_HATES));
        assertFalse(fire.has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    @Test
    void elementNameKeepsItsOwnUnderscore() {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, "HATES_HOLY_ORB"));
        assertTrue(map.get(ElementEnum.ELEM_HOLY_ORB).has(ElementInfoEnum.EL_INFO_HATES));
        assertEquals(1, map.size());
    }

    // strncmp with the length of the text before the underscore: that text need only be a prefix.
    @ParameterizedTest(name = "[{index}] {0} is a prefix of IGNORE")
    @ValueSource(strings = {"I_FIRE", "IG_FIRE", "IGN_FIRE", "IGNO_FIRE", "IGNOR_FIRE", "_FIRE"})
    void prefixesOfIgnoreIgnore(String word) {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, word));
        ElementInfo fire = map.get(ElementEnum.ELEM_FIRE);
        assertTrue(fire.has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(fire.has(ElementInfoEnum.EL_INFO_HATES));
    }

    @ParameterizedTest(name = "[{index}] {0} is a prefix of HATES")
    @ValueSource(strings = {"H_COLD", "HA_COLD", "HAT_COLD", "HATE_COLD"})
    void prefixesOfHatesHate(String word) {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, word));
        ElementInfo cold = map.get(ElementEnum.ELEM_COLD);
        assertTrue(cold.has(ElementInfoEnum.EL_INFO_HATES));
        assertFalse(cold.has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    @Test
    void emptyPrefixBeforeAHolyOrbStillMeansIgnoreAndCutsAtTheFirstUnderscore() {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, "_HOLY_ORB"));
        assertTrue(map.get(ElementEnum.ELEM_HOLY_ORB).has(ElementInfoEnum.EL_INFO_IGNORE));
    }

    // No underscore: C returns false before looking at any element.
    @ParameterizedTest(name = "[{index}] \"{0}\" has no underscore")
    @ValueSource(strings = {"", "IGNORE", "HATES", "IGNOREACID", "ACID", "HOLYORB"})
    void noUnderscoreIsRefused(String word) {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertFalse(ParseUtils.grabElementFlag(map, word));
        assertTrue(map.isEmpty());
    }

    // The text after the first underscore is not an element name, or is one only in other case.
    @ParameterizedTest(name = "[{index}] \"{0}\" names no element")
    @ValueSource(strings = {"IGNORE_", "IGNORE_FOO", "IGNORE_NONE", "HATES_MAX", "IGNORE_ELEM_FIRE",
            "IGNORE_fire", "IGNORE_Fire", "HATES_HOLY", "HATES_ORB", "IGNORE_FIRE_ACID",
            "IGNORE_FIRE ", "IGNORE_ FIRE", "IGNORE__FIRE", "HOLY_ORB"})
    void unknownElementIsRefused(String word) {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertFalse(ParseUtils.grabElementFlag(map, word));
        assertTrue(map.isEmpty());
    }

    // The text before the underscore is a prefix of neither word, is longer than the word, or is the
    // wrong case (strncmp is case-sensitive).
    @ParameterizedTest(name = "[{index}] \"{0}\" has an unusable prefix")
    @ValueSource(strings = {"RESIST_FIRE", "IGNORED_FIRE", "IGNORES_FIRE", "HATESX_FIRE", "HATED_FIRE",
            "ignore_FIRE", "Ignore_FIRE", "hates_FIRE", "HI_FIRE", "IH_FIRE", "X_FIRE", "IGNOREHATES_FIRE",
            "IGNORE _FIRE", " IGNORE_FIRE"})
    void unusablePrefixIsRefused(String word) {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertFalse(ParseUtils.grabElementFlag(map, word));
        assertTrue(map.isEmpty());
    }

    @Test
    void repeatedWordStillReturnsTrueAndTheFlagsAccumulateOnOneEntry() {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, "IGNORE_ELEC"));
        ElementInfo first = map.get(ElementEnum.ELEM_ELEC);
        assertTrue(ParseUtils.grabElementFlag(map, "IGNORE_ELEC"));
        assertSame(first, map.get(ElementEnum.ELEM_ELEC));
        assertTrue(ParseUtils.grabElementFlag(map, "HATES_ELEC"));
        assertSame(first, map.get(ElementEnum.ELEM_ELEC));
        assertTrue(first.has(ElementInfoEnum.EL_INFO_IGNORE));
        assertTrue(first.has(ElementInfoEnum.EL_INFO_HATES));
        assertEquals(1, map.size());
    }

    @Test
    void anExistingEntryIsKeptAndItsOtherFieldsAreLeftAlone() {
        Map<ElementEnum, ElementInfo> map = newMap();
        ElementInfo existing = new ElementInfo();
        existing.setResLevel(1);
        existing.on(ElementInfoEnum.EL_INFO_RANDOM);
        map.put(ElementEnum.ELEM_POIS, existing);

        assertTrue(ParseUtils.grabElementFlag(map, "HATES_POIS"));

        assertSame(existing, map.get(ElementEnum.ELEM_POIS));
        assertEquals(1, existing.getResLevel());
        assertTrue(existing.has(ElementInfoEnum.EL_INFO_RANDOM));
        assertTrue(existing.has(ElementInfoEnum.EL_INFO_HATES));
    }

    @Test
    void aRefusedWordLeavesExistingEntriesUntouched() {
        Map<ElementEnum, ElementInfo> map = newMap();
        assertTrue(ParseUtils.grabElementFlag(map, "IGNORE_DARK"));
        assertFalse(ParseUtils.grabElementFlag(map, "RESIST_DARK"));
        assertFalse(ParseUtils.grabElementFlag(map, "HATES_NONE"));
        assertEquals(1, map.size());
        assertTrue(map.get(ElementEnum.ELEM_DARK).has(ElementInfoEnum.EL_INFO_IGNORE));
        assertFalse(map.get(ElementEnum.ELEM_DARK).has(ElementInfoEnum.EL_INFO_HATES));
    }

    /**
     * {@code grabElementFlag} hands {@link ElementEnum#lookup} the text after the underscore with
     * {@code ELEM_} put back on the front, so the lookup is by the constant's whole name and finds
     * the two placeholders too (the caller refuses them).
     *
     * <p>Method lookupFindsTheWholeConstantNameIncludingThePlaceholders coded on 261105, commented in
     * full on 261105 (replaces the bare-name form, which the code stopped using on 261105).
     */
    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource({"ELEM_ACID, ELEM_ACID", "ELEM_HOLY_ORB, ELEM_HOLY_ORB", "ELEM_ARROW, ELEM_ARROW",
            "ELEM_NONE, ELEM_NONE", "ELEM_MAX, ELEM_MAX"})
    void lookupFindsTheWholeConstantNameIncludingThePlaceholders(String name, ElementEnum expected) {
        assertSame(expected, ElementEnum.lookup(name));
    }

    /**
     * The bare name no longer matches, and nor does anything that is not the whole constant name
     * byte for byte.
     *
     * <p>Method lookupIsExactAndCaseSensitive coded before 261105, commented in full on 261105
     * (expectations moved to the whole-name form on 261105).
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {"", "acid", "ACID", "elem_acid", "ELEM_acid", "ELEM_HOLY", "ELEM_ACID "})
    void lookupIsExactAndCaseSensitive(String name) {
        assertNull(ElementEnum.lookup(name));
    }
}
