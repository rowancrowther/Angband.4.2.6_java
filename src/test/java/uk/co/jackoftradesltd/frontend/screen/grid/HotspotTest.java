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

package uk.co.jackoftradesltd.frontend.screen.grid;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Hotspot} against the boundary-crossing contract set out in
 * {@code docs/UIPanelArchitecture.md} - there is no C original to check against, since this is
 * new architecture, not a port.
 *
 * <p>{@link Hotspot} is a plain record with a generated canonical constructor, so there is
 * little behaviour of its own to test. What is worth pinning down is exactly what its Javadoc
 * claims: the generated {@code equals} compares all five components, and {@code event} being
 * declared {@code Object} means the comparison is only ever as good as whatever {@code equals}
 * the value handed in actually has - value equality for a {@code String}, reference equality
 * for a bare {@code Object}.
 *
 * <p>Class HotspotTest coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
class HotspotTest {

    @Test
    @DisplayName("accessors return exactly the values passed to the constructor")
    void accessorsReturnConstructorArguments() {
        Object event = new Object();

        Hotspot hotspot = new Hotspot(1, 2, 3, 4, event);

        assertEquals(1, hotspot.top());
        assertEquals(2, hotspot.left());
        assertEquals(3, hotspot.rows());
        assertEquals(4, hotspot.cols());
        assertSame(event, hotspot.event());
    }

    @Test
    @DisplayName("two hotspots with identical rectangle and equal event values are equal")
    void hotspotsWithEqualComponentsAreEqual() {
        Hotspot first = new Hotspot(0, 0, 1, 1, "event");
        Hotspot second = new Hotspot(0, 0, 1, 1, "event");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    @DisplayName("differing top breaks equality")
    void differingTopBreaksEquality() {
        Hotspot first = new Hotspot(0, 0, 1, 1, "event");
        Hotspot second = new Hotspot(9, 0, 1, 1, "event");

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("differing left breaks equality")
    void differingLeftBreaksEquality() {
        Hotspot first = new Hotspot(0, 0, 1, 1, "event");
        Hotspot second = new Hotspot(0, 9, 1, 1, "event");

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("differing rows breaks equality")
    void differingRowsBreaksEquality() {
        Hotspot first = new Hotspot(0, 0, 1, 1, "event");
        Hotspot second = new Hotspot(0, 0, 9, 1, "event");

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("differing cols breaks equality")
    void differingColsBreaksEquality() {
        Hotspot first = new Hotspot(0, 0, 1, 1, "event");
        Hotspot second = new Hotspot(0, 0, 1, 9, "event");

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("differing event breaks equality when the event type compares by value")
    void differingEventBreaksEquality() {
        Hotspot first = new Hotspot(0, 0, 1, 1, "one");
        Hotspot second = new Hotspot(0, 0, 1, 1, "two");

        assertNotEquals(first, second);
    }

    /**
     * A bare {@code Object} declares no {@code equals} of its own, so the generated
     * {@code equals} this record inherits falls back to reference identity for
     * {@link Hotspot#event()} when the event is one - two hotspots built over the same
     * rectangle but distinct plain-{@code Object} events are unequal even though every
     * {@code int} component matches.
     */
    @Test
    @DisplayName("same rectangle but distinct plain-Object events are not equal")
    void distinctPlainObjectEventsAreNotEqual() {
        Hotspot first = new Hotspot(0, 0, 1, 1, new Object());
        Hotspot second = new Hotspot(0, 0, 1, 1, new Object());

        assertNotEquals(first, second);
    }
}
