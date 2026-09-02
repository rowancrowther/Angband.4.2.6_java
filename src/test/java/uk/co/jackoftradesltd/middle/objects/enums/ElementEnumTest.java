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

package uk.co.jackoftradesltd.middle.objects.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ElementEnum} — the damage elements, the port of C's {@code ELEM_*} list
 * ({@code src/list-elements.h}).
 *
 * <p>Two things here are worth defending. The <b>declaration order</b> is locked to
 * {@code list-elements.h} because C's projection parser checks the leading entries of
 * {@code projection.txt} against it position for position and refuses to load if they disagree —
 * so a reordering here is not a cosmetic change, it breaks the data files. And
 * {@link ElementEnum#getProjectionEnum()} resolves the element-to-projection correspondence
 * <b>by name</b> rather than by ordinal, which is what keeps this port clear of the fact that both
 * enums carry a {@code NONE} placeholder C lacks; the tests below exercise every element so a
 * single mistyped constant on either side is caught.
 *
 * @author Rowan Crowther
 */
class ElementEnumTest {

    /**
     * C has 25 elements. This port brackets them with {@code ELEM_NONE} and the {@code ELEM_MAX}
     * count sentinel, giving 27.
     */
    @Test
    void theListIsCsTwentyFiveElementsPlusItsTwoSentinels() {
        assertEquals(27, ElementEnum.values().length,
                "25 elements from list-elements.h, plus ELEM_NONE and ELEM_MAX");
        assertSame(ElementEnum.ELEM_NONE, ElementEnum.values()[0]);
        assertSame(ElementEnum.ELEM_MAX, ElementEnum.values()[ElementEnum.values().length - 1]);
    }

    /**
     * The real elements, in C's order. Pinned as a list because the order is load-bearing for the
     * projection parser, not merely conventional.
     */
    @Test
    void theElementsAreInTheirCOrder() {
        List<String> expected = List.of(
                "ELEM_ACID", "ELEM_ELEC", "ELEM_FIRE", "ELEM_COLD", "ELEM_POIS",
                "ELEM_LIGHT", "ELEM_DARK", "ELEM_SOUND", "ELEM_SHARD", "ELEM_NEXUS",
                "ELEM_NETHER", "ELEM_CHAOS", "ELEM_DISEN", "ELEM_WATER", "ELEM_ICE",
                "ELEM_GRAVITY", "ELEM_INERTIA", "ELEM_FORCE", "ELEM_TIME", "ELEM_PLASMA",
                "ELEM_METEOR", "ELEM_MISSILE", "ELEM_MANA", "ELEM_HOLY_ORB", "ELEM_ARROW");

        List<String> actual = Arrays.stream(ElementEnum.values())
                .map(Enum::name)
                .filter(name -> !name.equals("ELEM_NONE") && !name.equals("ELEM_MAX"))
                .collect(Collectors.toList());

        assertEquals(expected, actual, "the order is locked to src/list-elements.h - C's projection "
                + "parser validates projection.txt against it position for position");
    }

    /**
     * Every real element resolves to the projection of the same name.
     *
     * @param element the element under test; the two sentinels are excluded by the test body
     */
    @ParameterizedTest
    @EnumSource(ElementEnum.class)
    void everyRealElementResolvesToItsProjection(ElementEnum element) {
        if (element == ElementEnum.ELEM_NONE || element == ElementEnum.ELEM_MAX) {
            return;
        }

        ProjectionEnum projection = element.getProjectionEnum();

        assertNotNull(projection, element.name() + " should resolve to a projection");
        assertEquals("PROJ_" + element.name().substring("ELEM_".length()), projection.name(),
                "the projection should be the one named after the element");
    }

    /**
     * The named cases from the Javadoc, spelled out so the mapping is legible without running the
     * parameterised test in one's head.
     */
    @Test
    void theWorkedExamplesFromTheJavadocHold() {
        assertSame(ProjectionEnum.PROJ_FIRE, ElementEnum.ELEM_FIRE.getProjectionEnum());
        assertSame(ProjectionEnum.PROJ_ACID, ElementEnum.ELEM_ACID.getProjectionEnum());
        assertSame(ProjectionEnum.PROJ_HOLY_ORB, ElementEnum.ELEM_HOLY_ORB.getProjectionEnum());
    }

    /**
     * The two sentinels behave differently, because the name-based lookup can only find what
     * exists on the other side.
     *
     * <p>{@code ELEM_NONE} resolves to {@code PROJ_NONE}: both enums carry a zero placeholder this
     * port added and C lacks, so the names happen to correspond and the lookup succeeds. That is
     * harmless — a placeholder mapping to a placeholder — but it is worth pinning, because it
     * means a null return is <em>not</em> a reliable "this is not a real element" test.
     *
     * <p>{@code ELEM_MAX} has no counterpart, so the lookup returns null rather than throwing.
     * Null is the signal that the two lists have drifted apart, which is exactly what a missing
     * {@code PROJ_MAX} looks like from here.
     */
    @Test
    void theSentinelsResolveOnlyAsFarAsTheirNamesExist() {
        assertSame(ProjectionEnum.PROJ_NONE, ElementEnum.ELEM_NONE.getProjectionEnum(),
                "both enums carry a NONE placeholder, so the names correspond");
        assertNull(ElementEnum.ELEM_MAX.getProjectionEnum(),
                "there is no PROJ_MAX, so the count sentinel resolves to nothing");
    }

    /**
     * The four base elements are exactly acid, electricity, fire and cold — C's {@code base}
     * column in {@code list-elements.h}. Objects ignore them as a group, so a fifth appearing
     * silently would change what a spellbook burns in.
     */
    @Test
    void exactlyTheFourPhysicalElementsAreBaseElements() {
        List<String> base = Arrays.stream(ElementEnum.values())
                .filter(ElementEnum::isBase)
                .map(Enum::name)
                .collect(Collectors.toList());

        assertEquals(List.of("ELEM_ACID", "ELEM_ELEC", "ELEM_FIRE", "ELEM_COLD"), base);
    }

    /**
     * Resistable elements run from acid through disenchantment and stop there — C's
     * {@code ELEM_HIGH_MAX} bound, which C enforces by ordinal comparison and this port carries as
     * a per-constant flag.
     *
     * <p>Since the flag is per-constant rather than positional, nothing stops it being set on an
     * element past the boundary; the test checks the set is the contiguous run C's bound describes,
     * which is the property the ordinal comparison gave for free.
     */
    @Test
    void theResistableElementsAreTheContiguousRunUpToDisenchantment() {
        List<String> resistable = Arrays.stream(ElementEnum.values())
                .filter(ElementEnum::isHasResistRune)
                .map(Enum::name)
                .collect(Collectors.toList());

        assertEquals(List.of("ELEM_ACID", "ELEM_ELEC", "ELEM_FIRE", "ELEM_COLD", "ELEM_POIS",
                "ELEM_LIGHT", "ELEM_DARK", "ELEM_SOUND", "ELEM_SHARD", "ELEM_NEXUS",
                "ELEM_NETHER", "ELEM_CHAOS", "ELEM_DISEN"), resistable);
    }

    /**
     * Every base element is also resistable — the base elements are the first four of the
     * resistable run, so a flag set that violated this would mean one of the two columns had been
     * mistranscribed.
     */
    @Test
    void everyBaseElementIsAlsoResistable() {
        for (ElementEnum element : ElementEnum.values()) {
            if (element.isBase()) {
                assertTrue(element.isHasResistRune(),
                        element.name() + " is a base element and so must be resistable");
            }
        }
    }
}