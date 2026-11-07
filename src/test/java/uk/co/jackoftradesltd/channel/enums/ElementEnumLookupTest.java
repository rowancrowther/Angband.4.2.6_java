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
 * Unit tests for {@link ElementEnum#lookup(String)}, which stands in for the {@code streq} search of
 * {@code element_names} in {@code grab_element_flag} ({@code obj-init.c}).
 *
 * <p>The expected values are worked out from the C, not from the Java. {@code element_names} is the
 * 25 names of {@code list-elements.h}, in that order, from {@code ACID} to {@code ARROW}, with no
 * {@code NONE} and no {@code MAX}; the loop runs {@code i < ELEM_MAX} and compares with
 * {@code streq}, so a name matches only if every byte is identical. Nothing is trimmed, folded or
 * partially matched.
 *
 * <p>The Java search takes the constant's whole name, {@code ELEM_} included, because
 * {@code ParseUtils.grabElementFlag} and {@code ItemObjectAssembler} put the prefix back on the
 * text they cut from the data file. So the bare C name finds nothing here, and every test below
 * spells the prefix. The Java-only placeholders {@code ELEM_NONE} and {@code ELEM_MAX} are an
 * accepted divergence: C finds nothing for {@code "NONE"} and {@code "MAX"}, lookup finds the
 * placeholder and the caller refuses it.
 *
 * <p>Class ElementEnumLookupTest coded before 261105, commented in full on 261105 (moved from the
 * bare-name form to the whole-constant-name form on 261105).
 *
 * @author Rowan Crowther
 */
class ElementEnumLookupTest {

    /**
     * Constant name for each of the 25 names in {@code list-elements.h}, found by that same text.
     *
     * <p>Method everyRealElementIsFoundByItsConstantName coded before 261105, commented in full on
     * 261105.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") gives \"{0}\"")
    @CsvSource({
            "ELEM_ACID", "ELEM_ELEC", "ELEM_FIRE", "ELEM_COLD",
            "ELEM_POIS", "ELEM_LIGHT", "ELEM_DARK", "ELEM_SOUND",
            "ELEM_SHARD", "ELEM_NEXUS", "ELEM_NETHER", "ELEM_CHAOS",
            "ELEM_DISEN", "ELEM_WATER", "ELEM_ICE", "ELEM_GRAVITY",
            "ELEM_INERTIA", "ELEM_FORCE", "ELEM_TIME", "ELEM_PLASMA",
            "ELEM_METEOR", "ELEM_MISSILE", "ELEM_MANA",
            "ELEM_HOLY_ORB", "ELEM_ARROW"
    })
    void everyRealElementIsFoundByItsConstantName(String name) {
        assertSame(ElementEnum.valueOf(name), ElementEnum.lookup(name));
    }

    /**
     * The one name with an underscore of its own is found whole.
     *
     * <p>Method holyOrbKeepsItsOwnUnderscore coded before 261105, commented in full on 261105.
     */
    @Test
    void holyOrbKeepsItsOwnUnderscore() {
        assertSame(ElementEnum.ELEM_HOLY_ORB, ElementEnum.lookup("ELEM_HOLY_ORB"));
        assertNull(ElementEnum.lookup("ELEM_ORB"));
        assertNull(ElementEnum.lookup("ELEM_HOLY"));
    }

    /**
     * The accepted divergence: names C's list lacks resolve to the placeholders.
     *
     * <p>Method placeholdersAreFoundWhereCFindsNothing coded before 261105, commented in full on
     * 261105.
     */
    @Test
    void placeholdersAreFoundWhereCFindsNothing() {
        assertSame(ElementEnum.ELEM_NONE, ElementEnum.lookup("ELEM_NONE"));
        assertSame(ElementEnum.ELEM_MAX, ElementEnum.lookup("ELEM_MAX"));
    }

    /**
     * The comparison is {@code streq}: case-sensitive for ASCII letters, prefix included.
     *
     * <p>Method caseMustMatchExactly coded before 261105, commented in full on 261105.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {"elem_fire", "Elem_Fire", "ELEM_fire", "ELEM_Fire", "ELEM_fIRE", "ELEM_acid",
            "ELEM_holy_orb", "ELEM_Holy_Orb", "ELEM_none", "ELEM_max"})
    void caseMustMatchExactly(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /**
     * No trimming, prefixes, suffixes or partial names: {@code streq} wants every byte. The bare C
     * name is in this list, because the Java search wants the {@code ELEM_} prefix too.
     *
     * <p>Method anythingButTheWholeNameFindsNothing coded before 261105, commented in full on 261105.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {"FIRE", "HOLY_ORB", "NONE", "MAX", " ELEM_FIRE", "ELEM_FIRE ", " ELEM_FIRE ",
            "\tELEM_FIRE", "ELEM_FIRE\n", "ELEM_FIR", "ELEM_FIREX", "ELEM_FIRE_", "ELEM__FIRE",
            "ELEM_ELEM_FIRE", "ELEM_", "ELEM", "PROJ_FIRE", "RES_FIRE", "ELEM_LIGHTS", "ELEM_LIGH",
            "ELEM_ACID|ELEM_ELEC", "ELEM_ACID ELEM_ELEC", "ELEM_HOLY_ORB ", "ELEM_HOLY ORB",
            "ELEM_HOLY-ORB"})
    void anythingButTheWholeNameFindsNothing(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /**
     * Empty text is no element name. A null name also finds nothing and does not throw.
     *
     * <p>Method emptyAndNullFindNothing coded before 261105, commented in full on 261105.
     */
    @ParameterizedTest(name = "[{index}] lookup({0}) finds nothing")
    @NullAndEmptySource
    void emptyAndNullFindNothing(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /**
     * Words that are valid in other lists the elements are locked to, with and without the prefix.
     *
     * <p>Method unrelatedWordsFindNothing coded before 261105, commented in full on 261105.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {"DEFAULT", "IGNORE", "HATES", "BASE", "HIGH", "SPELL", "MIXED",
            "ELEM_DEFAULT", "ELEM_IGNORE", "ELEM_HATES", "ELEM_BASE", "ELEM_HIGH", "ELEM_SPELL",
            "ELEM_MIXED"})
    void unrelatedWordsFindNothing(String name) {
        assertNull(ElementEnum.lookup(name));
    }

    /**
     * Cross-check against the C list: every constant is found by its own name, 25 of them are real
     * elements, and the enum holds those 25 plus the two placeholders.
     *
     * <p>Method exactlyTwentyFiveRealNamesAreFoundAndEachRoundTrips coded before 261105, commented in
     * full on 261105.
     */
    @Test
    void exactlyTwentyFiveRealNamesAreFoundAndEachRoundTrips() {
        int found = 0;
        for (ElementEnum e : ElementEnum.values()) {
            assertSame(e, ElementEnum.lookup(e.name()));
            if (e != ElementEnum.ELEM_NONE && e != ElementEnum.ELEM_MAX) found++;
        }
        assertEquals(25, found);
        assertEquals(27, ElementEnum.values().length);
    }
}
