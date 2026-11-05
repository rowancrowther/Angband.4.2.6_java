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

package uk.co.jackoftradesltd.channel.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ElementEnum#lookup(String)}, the port of the {@code streq} search of
 * {@code element_names} in {@code grab_element_flag} ({@code obj-init.c}).
 *
 * <p>The expected values are worked out from the C, not from the Java. {@code element_names} is the
 * 25 names of {@code list-elements.h}, in that order, from {@code ACID} to {@code ARROW}, with no
 * {@code NONE} and no {@code MAX}; the loop runs {@code i < ELEM_MAX} and compares with
 * {@code streq}, so a name matches only if every byte is identical. Nothing is trimmed, folded or
 * partially matched. The Java-only placeholders {@code ELEM_NONE} and {@code ELEM_MAX} are an
 * accepted divergence: C finds nothing for {@code "NONE"} and {@code "MAX"}, lookup finds the
 * placeholder and the caller refuses it.
 *
 * @author Rowan Crowther
 */
class ElementEnumLookupTest {

    /** Name text and expected constant for each of the 25 names in {@code list-elements.h}. */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") gives {1}")
    @CsvSource({
            "ACID,ELEM_ACID", "ELEC,ELEM_ELEC", "FIRE,ELEM_FIRE", "COLD,ELEM_COLD",
            "POIS,ELEM_POIS", "LIGHT,ELEM_LIGHT", "DARK,ELEM_DARK", "SOUND,ELEM_SOUND",
            "SHARD,ELEM_SHARD", "NEXUS,ELEM_NEXUS", "NETHER,ELEM_NETHER", "CHAOS,ELEM_CHAOS",
            "DISEN,ELEM_DISEN", "WATER,ELEM_WATER", "ICE,ELEM_ICE", "GRAVITY,ELEM_GRAVITY",
            "INERTIA,ELEM_INERTIA", "FORCE,ELEM_FORCE", "TIME,ELEM_TIME", "PLASMA,ELEM_PLASMA",
            "METEOR,ELEM_METEOR", "MISSILE,ELEM_MISSILE", "MANA,ELEM_MANA",
            "HOLY_ORB,ELEM_HOLY_ORB", "ARROW,ELEM_ARROW"
    })
    void everyRealElementIsFoundByItsBareName(String name, ElementEnum expected) {
        assertSame(expected, ElementEnum.lookup(name));
    }

    /** The one name with an underscore of its own is found whole. */
    @Test
    void holyOrbKeepsItsOwnUnderscore() {
        assertSame(ElementEnum.ELEM_HOLY_ORB, ElementEnum.lookup("HOLY_ORB"));
        assertNull(ElementEnum.lookup("ORB"));
        assertNull(ElementEnum.lookup("HOLY"));
    }

    /** The accepted divergence: names C's list lacks resolve to the placeholders. */
    @Test
    void placeholdersAreFoundWhereCFindsNothing() {
        assertSame(ElementEnum.ELEM_NONE, ElementEnum.lookup("NONE"));
        assertSame(ElementEnum.ELEM_MAX, ElementEnum.lookup("MAX"));
    }

    /** The comparison is {@code streq}: case-sensitive for ASCII letters. */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {"fire", "Fire", "fIRE", "acid", "holy_orb", "Holy_Orb", "none", "max"})
    void caseMustMatchExactly(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /** No trimming, prefixes, suffixes or partial names: {@code streq} wants every byte. */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {" FIRE", "FIRE ", " FIRE ", "\tFIRE", "FIRE\n", "FIR", "FIREX", "FIRE_",
            "_FIRE", "ELEM_FIRE", "ELEM_", "PROJ_FIRE", "RES_FIRE", "LIGHTS", "LIGH", "ACID|ELEC",
            "ACID ELEC", "HOLY_ORB ", "HOLY ORB", "HOLY-ORB"})
    void anythingButTheWholeNameFindsNothing(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /** Empty text is no element name; a null name gives null rather than throwing. */
    @ParameterizedTest(name = "[{index}] lookup({0}) finds nothing")
    @NullAndEmptySource
    void emptyAndNullFindNothing(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /** Non-element names that are valid in other lists the elements are locked to. */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {"DEFAULT", "IGNORE", "HATES", "BASE", "HIGH", "SPELL", "MIXED"})
    void unrelatedWordsFindNothing(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /**
     * Cross-check against the C list: exactly 25 names are found, none for the placeholders'
     * ELEM_ prefix alone, and each found constant's own name is its search text with {@code ELEM_}.
     */
    @Test
    void exactlyTwentyFiveRealNamesAreFoundAndEachRoundTrips() {
        int found = 0;
        for (ElementEnum e : ElementEnum.values()) {
            String bare = e.name().substring("ELEM_".length());
            assertSame(e, ElementEnum.lookup(bare));
            if (e != ElementEnum.ELEM_NONE && e != ElementEnum.ELEM_MAX) found++;
        }
        assertEquals(25, found);
        assertEquals(27, ElementEnum.values().length);
    }
}
