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
 * Unit tests for {@link ProjectionEnum#lookup(String)} and {@link ProjectionEnum#getFromElementEnum},
 * which stand in for {@code proj_name_to_idx} in {@code project.c} and for C's subscripting of
 * {@code projections[]} with an element value.
 *
 * <p>The expected values are worked out from the C, not from the Java. {@code proj_name_list} is the
 * 25 names of {@code list-elements.h}, then the 31 names of {@code list-projections.h}, then
 * {@code "MAX"}, all bare and in that order. {@code proj_name_to_idx} compares each with
 * {@code my_stricmp}, so ASCII case is ignored but nothing is trimmed or partially matched, and it
 * returns the index of the first match or -1.
 *
 * <p>The Java search puts {@code PROJ_} on a name that lacks it and then compares the whole constant
 * name ignoring case, so the bare C name and the whole constant name find the same constant. The
 * Java-only {@code PROJ_NONE} is an accepted divergence: C finds nothing for {@code "NONE"}, lookup
 * finds the placeholder and the caller refuses it. C's -1 becomes {@code null}.
 *
 * <p>Class ProjectionEnumLookupTest coded on 261116.
 *
 * @author Rowan Crowther
 */
class ProjectionEnumLookupTest {

    /**
     * The 56 bare names of {@code list-elements.h} then {@code list-projections.h}, in C order, which
     * is {@code proj_name_list} less its trailing {@code "MAX"}.
     *
     * <p>Field C_NAMES coded on 261116.
     */
    private static final String[] C_NAMES = {
            "ACID", "ELEC", "FIRE", "COLD", "POIS", "LIGHT", "DARK", "SOUND", "SHARD", "NEXUS",
            "NETHER", "CHAOS", "DISEN", "WATER", "ICE", "GRAVITY", "INERTIA", "FORCE", "TIME",
            "PLASMA", "METEOR", "MISSILE", "MANA", "HOLY_ORB", "ARROW",
            "LIGHT_WEAK", "DARK_WEAK", "KILL_WALL", "KILL_DOOR", "KILL_TRAP", "MAKE_DOOR",
            "MAKE_TRAP", "AWAY_UNDEAD", "AWAY_SPIRIT", "AWAY_EVIL", "AWAY_ALL", "TURN_UNDEAD",
            "TURN_EVIL", "TURN_LIVING", "TURN_ALL", "DISP_UNDEAD", "DISP_EVIL", "DISP_ALL",
            "SLEEP_UNDEAD", "SLEEP_EVIL", "SLEEP_ALL", "MON_CLONE", "MON_POLY", "MON_HEAL",
            "MON_SPEED", "MON_SLOW", "MON_CONF", "MON_HOLD", "MON_STUN", "MON_DRAIN", "MON_CRUSH"
    };

    /**
     * Every C name is found at C's index plus one (the {@code PROJ_NONE} slot), bare, prefixed, in
     * lower case and in mixed case, as {@code my_stricmp} would accept all but the prefixed forms.
     *
     * <p>Method everyCNameIsFoundInCOrder coded on 261116.
     */
    @Test
    void everyCNameIsFoundInCOrder() {
        for (int i = 0; i < C_NAMES.length; i++) {
            String name = C_NAMES[i];
            ProjectionEnum expected = ProjectionEnum.values()[i + 1];
            assertEquals("PROJ_" + name, expected.name(), "constant order at C index " + i);
            assertSame(expected, ProjectionEnum.lookup(name), name);
            assertSame(expected, ProjectionEnum.lookup(name.toLowerCase()), name + " lower");
            assertSame(expected, ProjectionEnum.lookup("PROJ_" + name), "PROJ_" + name);
        }
    }

    /**
     * The enum holds C's 56 projections between the two placeholders.
     *
     * <p>Method enumHoldsFiftySixRealProjectionsAndTwoPlaceholders coded on 261116.
     */
    @Test
    void enumHoldsFiftySixRealProjectionsAndTwoPlaceholders() {
        assertEquals(56, C_NAMES.length);
        assertEquals(58, ProjectionEnum.values().length);
        assertSame(ProjectionEnum.PROJ_NONE, ProjectionEnum.values()[0]);
        assertSame(ProjectionEnum.PROJ_MAX, ProjectionEnum.values()[57]);
    }

    /**
     * Bare names found as C finds them, including the underscore ones and a mixed-case spelling.
     *
     * <p>Method bareNamesAndCaseAreAccepted coded on 261116.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") gives {1}")
    @CsvSource({"FIRE, PROJ_FIRE", "fire, PROJ_FIRE", "Fire, PROJ_FIRE", "fIRE, PROJ_FIRE",
            "HOLY_ORB, PROJ_HOLY_ORB", "Holy_Orb, PROJ_HOLY_ORB", "holy_orb, PROJ_HOLY_ORB",
            "mon_crush, PROJ_MON_CRUSH", "Light_Weak, PROJ_LIGHT_WEAK", "ACID, PROJ_ACID",
            "ARROW, PROJ_ARROW"})
    void bareNamesAndCaseAreAccepted(String name, ProjectionEnum expected) {
        assertSame(expected, ProjectionEnum.lookup(name));
    }

    /**
     * The widening over C: the whole constant name, prefix included, finds the same constant.
     *
     * <p>Method theWholeConstantNameIsAcceptedToo coded on 261116.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") gives {0}")
    @ValueSource(strings = {"PROJ_FIRE", "PROJ_HOLY_ORB", "PROJ_MON_CRUSH", "PROJ_LIGHT_WEAK"})
    void theWholeConstantNameIsAcceptedToo(String name) {
        assertSame(ProjectionEnum.valueOf(name), ProjectionEnum.lookup(name));
    }

    /**
     * {@code "MAX"} is the last entry of C's name list, so it is found. {@code "NONE"} is not in C's
     * list and is the accepted divergence: lookup finds the placeholder.
     *
     * <p>Method maxIsFoundAndNoneIsTheAcceptedDivergence coded on 261116.
     */
    @Test
    void maxIsFoundAndNoneIsTheAcceptedDivergence() {
        assertSame(ProjectionEnum.PROJ_MAX, ProjectionEnum.lookup("MAX"));
        assertSame(ProjectionEnum.PROJ_MAX, ProjectionEnum.lookup("max"));
        assertSame(ProjectionEnum.PROJ_MAX, ProjectionEnum.lookup("PROJ_MAX"));
        assertSame(ProjectionEnum.PROJ_NONE, ProjectionEnum.lookup("NONE"));
        assertSame(ProjectionEnum.PROJ_NONE, ProjectionEnum.lookup("PROJ_NONE"));
    }

    /**
     * No trimming, suffixes or partial names, as {@code my_stricmp} wants every byte. A lower-case
     * {@code proj_} is not a prefix, so it is taken as part of a bare name and finds nothing, as it
     * does in C.
     *
     * <p>Method anythingButTheWholeNameFindsNothing coded on 261116.
     */
    @ParameterizedTest(name = "[{index}] lookup(\"{0}\") finds nothing")
    @ValueSource(strings = {" FIRE", "FIRE ", " FIRE ", "\tFIRE", "FIRE\n", "FIR", "FIREX", "FIRE_",
            "_FIRE", "HOLY", "ORB", "HOLY ORB", "HOLY-ORB", "ACID|ELEC", "ACID ELEC", "LIGHT_",
            "proj_fire", "Proj_Fire", "PROJ_", "PROJ", "PROJ__FIRE", "PROJ_PROJ_FIRE",
            "ELEM_FIRE", "RES_FIRE", "IGNORE_FIRE"})
    void anythingButTheWholeNameFindsNothing(String name) {
        assertNull(ProjectionEnum.lookup(name));
    }

    /**
     * Empty text is no projection name, and a null name finds nothing without throwing.
     *
     * <p>Method emptyAndNullFindNothing coded on 261116.
     */
    @ParameterizedTest(name = "[{index}] lookup({0}) finds nothing")
    @NullAndEmptySource
    void emptyAndNullFindNothing(String name) {
        assertNull(ProjectionEnum.lookup(name));
    }

    /**
     * Every element maps to the projection of the same name, as {@code PROJ_ACID == ELEM_ACID} in C,
     * and the placeholders map to each other.
     *
     * <p>Method everyElementMapsToItsProjection coded on 261116.
     */
    @Test
    void everyElementMapsToItsProjection() {
        for (int i = 0; i < 25; i++) {
            ElementEnum element = ElementEnum.values()[i + 1];
            assertSame(ProjectionEnum.lookup(C_NAMES[i]), ProjectionEnum.getFromElementEnum(element),
                    element.name());
        }
        assertSame(ProjectionEnum.PROJ_NONE, ProjectionEnum.getFromElementEnum(ElementEnum.ELEM_NONE));
        assertSame(ProjectionEnum.PROJ_MAX, ProjectionEnum.getFromElementEnum(ElementEnum.ELEM_MAX));
    }
}
