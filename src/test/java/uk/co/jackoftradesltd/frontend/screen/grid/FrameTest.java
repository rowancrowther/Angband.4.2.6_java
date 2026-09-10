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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Frame} against the boundary-crossing contract set out in
 * {@code docs/UIPanelArchitecture.md} - there is no C original to check against, since this is
 * new architecture, not a port.
 *
 * <p>{@link Frame} is a plain record with a generated canonical constructor, so there is little
 * behaviour of its own to test. What is worth pinning down is exactly what its Javadoc claims:
 * the record does not defensively copy either component, and its generated {@code equals}
 * inherits whatever equality {@link CellGrid} and {@code List} happen to have - reference
 * equality for the former, value equality for the latter, an asymmetry easy to assume away.
 *
 * <p>Class FrameTest coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
class FrameTest {

    private static CellGrid aOneCellGrid(char c) {
        CellGrid grid = new CellGrid(1, 1);
        grid.set(0, 0, new AngbandDisplayCharacter(c, ColourEnum.COLOUR_WHITE));
        return grid;
    }

    @Test
    @DisplayName("grid() returns exactly the instance passed in, not a copy")
    void gridAccessorReturnsThePassedInstance() {
        CellGrid grid = aOneCellGrid('X');

        Frame frame = new Frame(grid, List.of());

        assertSame(grid, frame.grid());
    }

    @Test
    @DisplayName("hotspots() returns exactly the list passed in, not a copy")
    void hotspotsAccessorReturnsThePassedList() {
        List<Hotspot> hotspots = List.of(new Hotspot(0, 0, 1, 1, "event"));

        Frame frame = new Frame(aOneCellGrid('X'), hotspots);

        assertSame(hotspots, frame.hotspots());
    }

    @Test
    @DisplayName("two frames over the same grid instance and equal hotspots are equal")
    void framesOverSameGridAndEqualHotspotsAreEqual() {
        CellGrid grid = aOneCellGrid('X');
        Hotspot hotspot = new Hotspot(0, 0, 1, 1, "event");

        Frame first = new Frame(grid, List.of(hotspot));
        Frame second = new Frame(grid, List.of(hotspot));

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    /**
     * {@link CellGrid} declares no {@code equals} of its own, so the generated {@code equals}
     * this record inherits falls back to {@code Object.equals} for {@link Frame#grid()} -
     * reference identity, not cell-by-cell content.
     */
    @Test
    @DisplayName("two frames over structurally identical but distinct grid instances are not equal")
    void framesOverDistinctButIdenticalGridsAreNotEqual() {
        Frame first = new Frame(aOneCellGrid('X'), List.of());
        Frame second = new Frame(aOneCellGrid('X'), List.of());

        assertNotEquals(first, second);
    }

    @Test
    @DisplayName("two frames over the same grid but different hotspots are not equal")
    void framesOverSameGridButDifferentHotspotsAreNotEqual() {
        CellGrid grid = aOneCellGrid('X');

        Frame first = new Frame(grid, List.of(new Hotspot(0, 0, 1, 1, "one")));
        Frame second = new Frame(grid, List.of(new Hotspot(0, 0, 1, 1, "two")));

        assertNotEquals(first, second);
    }
}
