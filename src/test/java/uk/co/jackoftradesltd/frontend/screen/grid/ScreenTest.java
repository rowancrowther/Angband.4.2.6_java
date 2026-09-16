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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Screen} against the ownership and publication contract set out in
 * {@code docs/UIPanelArchitecture.md}, and, for {@link Screen#splashScreenNote(String)}, against
 * the non-birth branch of {@code splashscreen_note} ({@code [C] src/ui-display.c}) it ports.
 *
 * <p>Three things are worth pinning down: that {@link Screen#root()} hands back a region that
 * writes through to the live grid this screen owns, sized to exactly cover it; that
 * {@link Screen#frame()} publishes a snapshot that is independent of whatever this screen goes
 * on to do to its live state afterwards - the invariant the whole design rests on; and that
 * {@link Screen#splashScreenNote(String)} brackets, centres, and erases the way C's
 * {@code format}/{@code Term_erase}/{@code Term_putstr} sequence does for an 80x24 terminal.
 *
 * <p>Class ScreenTest coded on 260909, commented in full on 260916.
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

    /**
     * Reads row {@code row} of {@code grid}, columns {@code 0} to {@code grid.cols() - 1}, as a
     * plain string - blank cells (still {@code null}, never written) come back as a space so a
     * freshly erased row reads the same as one filled with literal spaces.
     */
    private static String rowText(CellGrid grid, int row) {
        StringBuilder text = new StringBuilder();
        for (int col = 0; col < grid.cols(); col++) {
            AngbandDisplayCharacter cell = grid.get(row, col);
            text.append(cell == null ? ' ' : cell.getCharacter());
        }
        return text.toString();
    }

    // splashScreenNote() ports the non-birth branch of splashscreen_note() ([C] src/ui-display.c):
    // format("[%s]", msg), erase row (Term->hgt - 23) / 5 + 23 (== 23 for the standard 24-row
    // terminal this port assumes), then Term_putstr the bracketed string centred on
    // (Term->wid - strlen(s)) / 2 (== 80-column centring here), in COLOUR_WHITE.

    @Test
    @DisplayName("splashScreenNote() brackets the message and centres it on row 23")
    void splashScreenNoteBracketsAndCentresTheMessage() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());

        screen.splashScreenNote("Initializing");

        // "[Initializing]" is 14 characters; (80 - 14) / 2 == 33, matching C's
        // (Term->wid - strlen(s)) / 2 for an 80-column terminal.
        assertEquals(" ".repeat(33) + "[Initializing]" + " ".repeat(33), rowText(live, 23));
        assertEquals(ColourEnum.COLOUR_WHITE, live.get(23, 33).getAttributeColour());
    }

    @Test
    @DisplayName("splashScreenNote() truncates the centring column the same way for an odd remainder")
    void splashScreenNoteTruncatesOddRemainderTowardZero() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());

        screen.splashScreenNote("abcdefghijklmno");

        // "[abcdefghijklmno]" is 17 characters; (80 - 17) / 2 == 31 (63 / 2 truncates to 31, not
        // 31.5), the same integer division C's (Term->wid - strlen(s)) / 2 performs in C.
        assertEquals(" ".repeat(31) + "[abcdefghijklmno]" + " ".repeat(32), rowText(live, 23));
    }

    @Test
    @DisplayName("splashScreenNote() brackets an empty message rather than skipping the write")
    void splashScreenNoteBracketsEmptyMessage() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());

        screen.splashScreenNote("");

        // "[]" is 2 characters; (80 - 2) / 2 == 39.
        assertEquals(" ".repeat(39) + "[]" + " ".repeat(39), rowText(live, 23));
    }

    @Test
    @DisplayName("splashScreenNote() erases the previous contents of row 23 before writing")
    void splashScreenNoteErasesRowBeforeWriting() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        screen.root().put(23, 5, 'Q', ColourEnum.COLOUR_RED);

        screen.splashScreenNote("Initializing");

        // Column 5 falls outside "[Initializing]"'s centred span (columns 33-46), so surviving
        // there would mean the erase - Term_erase(0, y, 255) in C - never happened.
        assertEquals(' ', live.get(23, 5).getCharacter());
    }

    @Test
    @DisplayName("splashScreenNote() leaves other rows untouched")
    void splashScreenNoteLeavesOtherRowsUntouched() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());
        screen.root().put(2, 0, 'Y', ColourEnum.COLOUR_WHITE);

        screen.splashScreenNote("Initializing");

        assertEquals('Y', live.get(2, 0).getCharacter());
        assertNull(live.get(22, 33));
    }

    @Test
    @DisplayName("splashScreenNote() clips a message too wide for the row instead of throwing")
    void splashScreenNoteClipsAnOverwideMessage() {
        CellGrid live = new CellGrid(24, 80);
        Screen screen = new Screen(live, List.of());

        // "[" + 88 'x's + "]" is 90 characters; (80 - 90) / 2 == -5, so Region's negative-column
        // rule drops the bracketed string's first 5 characters ('[' plus four 'x's) and its
        // right-edge rule then clips the remainder to the row's 80 columns - all 'x', with
        // neither bracket surviving. This mirrors C's Term_putstr silently clipping to Term->wid.
        screen.splashScreenNote("x".repeat(88));

        assertEquals("x".repeat(80), rowText(live, 23));
    }
}
