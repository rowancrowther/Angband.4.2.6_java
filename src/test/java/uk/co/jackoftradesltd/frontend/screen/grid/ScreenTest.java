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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Screen} against the ownership and publication contract set out in
 * {@code docs/UIPanelArchitecture.md} - there is no C original to check against, since this is
 * new architecture, not a port.
 *
 * <p>Two things are worth pinning down: that {@link Screen#root()} hands back a region that
 * writes through to the live grid this screen owns, sized to exactly cover it; and that
 * {@link Screen#frame()} publishes a snapshot that is independent of whatever this screen goes
 * on to do to its live state afterwards - the invariant the whole design rests on.
 *
 * <p>Class ScreenTest coded on 260909, commented in full on 260909.
 *
 * @author Rowan Crowther
 */
class ScreenTest {

    @Test
    @DisplayName("root() returns a region that writes through to the live grid")
    void rootWritesThroughToLiveGrid() {
        CellGrid live = new CellGrid(2, 3);
        Screen screen = new Screen(live, List.of());

        screen.root().put(1, 2, 'X', ColourEnum.COLOUR_WHITE);

        assertEquals('X', live.get(1, 2).getCharacter());
    }

    @Test
    @DisplayName("root() is sized to exactly cover the live grid, not more")
    void rootIsSizedToExactlyCoverTheLiveGrid() {
        CellGrid live = new CellGrid(2, 3);
        Screen screen = new Screen(live, List.of());

        // (2, 0) and (0, 3) are one past the grid's own bounds in each direction, so a region
        // sized to exactly cover the grid must clip both rather than writing off the end.
        screen.root().put(2, 0, 'A', ColourEnum.COLOUR_WHITE);
        screen.root().put(0, 3, 'B', ColourEnum.COLOUR_WHITE);

        assertNull(live.get(1, 2));
    }

    @Test
    @DisplayName("frame() copies the live grid, not a reference to it")
    void frameCopiesTheLiveGrid() {
        CellGrid live = new CellGrid(1, 1);
        Screen screen = new Screen(live, List.of());

        Frame frame = screen.frame();

        assertNotSame(live, frame.grid());
    }

    @Test
    @DisplayName("mutating the live grid after frame() is taken leaves the frame unaffected")
    void mutatingLiveGridAfterFrameLeavesFrameUnaffected() {
        CellGrid live = new CellGrid(1, 1);
        Screen screen = new Screen(live, List.of());
        Frame frame = screen.frame();

        screen.root().put(0, 0, 'X', ColourEnum.COLOUR_WHITE);

        assertNull(frame.grid().get(0, 0));
    }

    @Test
    @DisplayName("frame() copies the hotspot list, not a reference to it")
    void frameCopiesTheHotspotList() {
        List<Hotspot> hotspots = new ArrayList<>();
        hotspots.add(new Hotspot(0, 0, 1, 1, "one"));
        Screen screen = new Screen(new CellGrid(1, 1), hotspots);

        Frame frame = screen.frame();

        assertNotSame(hotspots, frame.hotspots());
    }

    @Test
    @DisplayName("mutating the hotspot list after frame() is taken leaves the frame unaffected")
    void mutatingHotspotListAfterFrameLeavesFrameUnaffected() {
        List<Hotspot> hotspots = new ArrayList<>();
        hotspots.add(new Hotspot(0, 0, 1, 1, "one"));
        Screen screen = new Screen(new CellGrid(1, 1), hotspots);

        Frame frame = screen.frame();
        hotspots.add(new Hotspot(0, 0, 1, 1, "two"));

        assertEquals(1, frame.hotspots().size());
    }
}
