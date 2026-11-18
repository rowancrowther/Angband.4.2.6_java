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
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ElementEnum#lookup(String, boolean)}, the two-argument form whose
 * {@code true} setting stands in for {@code proj_name_to_idx()} in {@code project.c}.
 *
 * <p>The expected values are worked out from the C, not from the Java. {@code proj_name_to_idx()}
 * walks {@code proj_name_list} and compares each entry with {@code my_stricmp}, so the case of the
 * name never matters. The list starts with the 25 names of {@code list-elements.h}, {@code ACID}
 * to {@code ARROW}, with no prefix, then the projections that are not elements, then a trailing
 * {@code "MAX"}; it has no {@code NONE}. Nothing is trimmed or partly matched.
 *
 * <p>The Java form searches the element constants only, supplies a missing {@code ELEM_} prefix
 * (the prefix test is case-sensitive, even when the comparison is not), and can return the
 * placeholders {@code ELEM_NONE} and {@code ELEM_MAX}, which C's name list does not give as
 * elements. The {@code false} setting is the exact search of {@link ElementEnum#lookup(String)},
 * which {@code ElementEnumLookupTest} covers in full; here it is only checked to agree with it.
 *
 * <p>Class ElementEnumLookupIgnoreCaseTest coded on 261117, commented in full on 261117.
 *
 * @author Rowan Crowther
 */
class ElementEnumLookupIgnoreCaseTest {

    /** The first letter in upper case and the rest in lower case: {@code "Holy_orb"}. */
    private static String capitalised(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    /**
     * Each of the 25 names of {@code list-elements.h} is found as written, in lower case, with the
     * first letter capitalised, and after the {@code ELEM_} prefix in lower case. {@code my_stricmp}
     * accepts every one of those spellings.
     *
     * <p>Method everyRealElementIsFoundWhateverTheCase coded on 261117.
     */
    @ParameterizedTest(name = "[{index}] {0} is found in any case")
    @CsvSource({
            "ACID", "ELEC", "FIRE", "COLD", "POIS", "LIGHT", "DARK", "SOUND", "SHARD", "NEXUS",
            "NETHER", "CHAOS", "DISEN", "WATER", "ICE", "GRAVITY", "INERTIA", "FORCE", "TIME",
            "PLASMA", "METEOR", "MISSILE", "MANA", "HOLY_ORB", "ARROW"
    })
    void everyRealElementIsFoundWhateverTheCase(String name) {
        ElementEnum expected = ElementEnum.valueOf("ELEM_" + name);

        assertSame(expected, ElementEnum.lookup(name, true), name);
        assertSame(expected, ElementEnum.lookup(name.toLowerCase(), true), name.toLowerCase());
        assertSame(expected, ElementEnum.lookup(capitalised(name), true), capitalised(name));
        assertSame(expected, ElementEnum.lookup("ELEM_" + name, true), "ELEM_" + name);
        assertSame(expected, ElementEnum.lookup("ELEM_" + name.toLowerCase(), true),
                "ELEM_" + name.toLowerCase());
    }

    /**
     * The one name with an underscore of its own keeps it: the underscore is part of the name, so
     * dropping it or splitting the name finds nothing.
     *
     * <p>Method holyOrbKeepsItsOwnUnderscore coded on 261117.
     */
    @Test
    void holyOrbKeepsItsOwnUnderscore() {
        assertSame(ElementEnum.ELEM_HOLY_ORB, ElementEnum.lookup("holy_orb", true));
        assertSame(ElementEnum.ELEM_HOLY_ORB, ElementEnum.lookup("Holy_Orb", true));
        assertSame(ElementEnum.ELEM_HOLY_ORB, ElementEnum.lookup("hOLY_oRB", true));
        assertNull(ElementEnum.lookup("holy orb", true));
        assertNull(ElementEnum.lookup("holyorb", true));
        assertNull(ElementEnum.lookup("holy", true));
        assertNull(ElementEnum.lookup("orb", true));
    }

    /**
     * The prefix is accepted in upper case only: a name that starts with exactly {@code ELEM_} is
     * compared as given, and the rest of it may be any case. C has no prefix at all, so it finds
     * nothing for either spelling of the prefix; the upper-case one is the accepted widening of
     * {@code ElementEnum.lookup}.
     *
     * <p>Method onlyAnUpperCasePrefixCountsAsAPrefix coded on 261117.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\", true) gives {1}")
    @CsvSource({"ELEM_fire, ELEM_FIRE", "ELEM_Fire, ELEM_FIRE", "ELEM_fIRE, ELEM_FIRE",
            "ELEM_acid, ELEM_ACID", "ELEM_holy_orb, ELEM_HOLY_ORB", "ELEM_Holy_Orb, ELEM_HOLY_ORB"})
    void onlyAnUpperCasePrefixCountsAsAPrefix(String name, ElementEnum expected) {
        assertSame(expected, ElementEnum.lookup(name, true));
    }

    /**
     * A prefix that is not exactly {@code ELEM_} is treated as part of a bare name, so
     * {@code "elem_fire"} is searched for as {@code "ELEM_elem_fire"} and finds nothing.
     *
     * <p>Method aLowerCasePrefixIsPartOfTheNameAndFindsNothing coded on 261117.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\", true) finds nothing")
    @ValueSource(strings = {"elem_fire", "Elem_Fire", "eLEM_fire", "ELEm_fire", "elem_acid", "elem_holy_orb",
            "elem_elem_fire", "elem", "ELEM"})
    void aLowerCasePrefixIsPartOfTheNameAndFindsNothing(String name) {
        assertNull(ElementEnum.lookup(name, true));
    }

    /**
     * The Java-only placeholders are found whatever the case, with or without the prefix. C finds
     * nothing for {@code "NONE"} (its list has no such entry) and gives the {@code PROJ_MAX} index
     * for {@code "MAX"}, which is no element; this is the same accepted divergence as for the exact
     * form, and the caller refuses the placeholder.
     *
     * <p>Method placeholdersAreFoundWhateverTheCase coded on 261117.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\", true) gives {1}")
    @CsvSource({"none, ELEM_NONE", "None, ELEM_NONE", "NONE, ELEM_NONE", "ELEM_NONE, ELEM_NONE",
            "ELEM_none, ELEM_NONE", "max, ELEM_MAX", "Max, ELEM_MAX", "MAX, ELEM_MAX",
            "ELEM_MAX, ELEM_MAX", "ELEM_max, ELEM_MAX"})
    void placeholdersAreFoundWhateverTheCase(String name, ElementEnum expected) {
        assertSame(expected, ElementEnum.lookup(name, true));
    }

    /**
     * Ignoring case is the only thing relaxed: no trimming, suffixes or partial names, as with
     * {@code my_stricmp}, which compares every byte of both strings up to the end of each.
     *
     * <p>Method ignoringCaseDoesNotRelaxAnythingElse coded on 261117.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\", true) finds nothing")
    @ValueSource(strings = {" fire", "fire ", " fire ", "\tfire", "fire\n", "fir", "firee", "fire_", "_fire",
            "fire|cold", "fire cold", "fire-cold", "light_", "ligh", "ELEM_", "ELEM_ELEM_FIRE",
            "ELEM_elem_fire", " ELEM_FIRE", "ELEM_FIRE ", "ELEM_FIR", "ELEM_FIREX", "ELEM__FIRE",
            "RES_FIRE", "IGNORE_FIRE"})
    void ignoringCaseDoesNotRelaxAnythingElse(String name) {
        assertNull(ElementEnum.lookup(name, true));
    }

    /**
     * Empty text is no element name, and a null name finds nothing without throwing, in both
     * settings.
     *
     * <p>Method emptyAndNullFindNothing coded on 261117.
     */
    @Test
    void emptyAndNullFindNothing() {
        assertNull(ElementEnum.lookup("", true));
        assertNull(ElementEnum.lookup("", false));
        assertNull(ElementEnum.lookup(null, true));
        assertNull(ElementEnum.lookup(null, false));
    }

    /**
     * {@code proj_name_to_idx()} finds a projection that is not an element too, after the last
     * element in {@code proj_name_list}. This search covers the elements only, so those names find
     * nothing here; {@code ProjectionEnum.lookup} is the search for them, and the blow effect
     * assembler uses both.
     *
     * <p>Method aProjectionThatIsNotAnElementFindsNothing coded on 261117.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\", true) finds nothing")
    @ValueSource(strings = {"KILL_WALL", "kill_wall", "Kill_Wall", "LIGHT_WEAK", "light_weak", "MON_SLOW",
            "mon_slow", "PROJ_FIRE", "proj_fire", "ELEM_KILL_WALL"})
    void aProjectionThatIsNotAnElementFindsNothing(String name) {
        assertNull(ElementEnum.lookup(name, true));
    }

    /**
     * With {@code false} the answer is that of the one-argument form for every kind of input: a
     * found name, one found only with a prefix put on, a name in the wrong case, a lower-case
     * prefix, a placeholder, empty text and a doubled prefix.
     *
     * <p>Method falseGivesTheSameAnswerAsTheExactForm coded on 261117.
     */
    @Test
    void falseGivesTheSameAnswerAsTheExactForm() {
        String[] names = {"ELEM_FIRE", "FIRE", "fire", "Fire", "ELEM_fire", "elem_fire", "HOLY_ORB",
                "holy_orb", "NONE", "none", "ELEM_MAX", "MAX", "", " FIRE", "ELEM_ELEM_FIRE", "KILL_WALL"};

        for (String name : names) {
            assertSame(ElementEnum.lookup(name), ElementEnum.lookup(name, false), "\"" + name + "\"");
        }
    }

    /**
     * What {@code false} keeps and {@code true} relaxes, stated as outcomes: the exact spelling is
     * found by both, and the wrong-case one only by {@code true}.
     *
     * <p>Method onlyTrueFindsTheWrongCaseSpelling coded on 261117.
     */
    @Test
    void onlyTrueFindsTheWrongCaseSpelling() {
        assertSame(ElementEnum.ELEM_FIRE, ElementEnum.lookup("FIRE", false));
        assertSame(ElementEnum.ELEM_FIRE, ElementEnum.lookup("FIRE", true));

        assertNull(ElementEnum.lookup("fire", false));
        assertSame(ElementEnum.ELEM_FIRE, ElementEnum.lookup("fire", true));

        assertNull(ElementEnum.lookup("ELEM_fire", false));
        assertSame(ElementEnum.ELEM_FIRE, ElementEnum.lookup("ELEM_fire", true));
    }

    /**
     * Cross-check against the C list: every constant, the two placeholders included, is found by
     * its own constant name, so 25 real elements and 2 placeholders in all.
     *
     * <p>Method everyConstantIsFoundByItsOwnName coded on 261117.
     */
    @Test
    void everyConstantIsFoundByItsOwnName() {
        int found = 0;
        for (ElementEnum e : ElementEnum.values()) {
            assertSame(e, ElementEnum.lookup(e.name(), true), e.name());
            assertSame(e, ElementEnum.lookup(e.name().toLowerCase().replaceFirst("^elem_", "ELEM_"), true),
                    e.name());
            found++;
        }
        assertEquals(27, found);
    }
}
