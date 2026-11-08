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

package uk.co.jackoftradesltd.frontend.colour;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.VisualsReader;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link FlickerTable#getAttr}, the port of C's
 * {@code visuals_flicker_get_attr_for_frame()} and the {@code visuals_flicker_get_color()} read it
 * ends in ({@code ui-visuals.c}).
 *
 * <p>Expected values come from C, not from the Java: the index arithmetic is
 * {@code cycles[attr * 3 + (frame % 3)]} over a {@code mem_zalloc}'d table, so an unwritten row
 * reads as colour 0 ({@code COLOUR_DARK}); the real-file expectations are the {@code flicker:}
 * blocks of {@code lib/gamedata/visuals.txt} transcribed by hand.
 *
 * <p>Class FlickerTableTest coded on 261108, commented in full on 261108.
 *
 * @author Rowan Crowther
 */
class FlickerTableTest {

    private static final String REAL_FILE = "lib/gamedata/visuals.txt";

    private static ColourCycle cycle(String name, ColourEnum a, ColourEnum b, ColourEnum c) {
        return ColourCycle.colourCycle(name, List.of(a, b, c));
    }

    private static FlickerTable redTable() {
        Map<ColourEnum, ColourCycle> map = new HashMap<>();
        map.put(ColourEnum.COLOUR_RED, cycle("red", ColourEnum.COLOUR_RED,
                ColourEnum.COLOUR_LIGHT_RED, ColourEnum.COLOUR_LIGHT_PINK));
        return new FlickerTable(map);
    }

    /** Frames 0, 1, 2 select steps 0, 1, 2 of the shipped {@code r} block: r, R, I. */
    @Test
    void framesZeroToTwoSelectEachStep() {
        FlickerTable table = redTable();
        assertEquals(ColourEnum.COLOUR_RED, table.getAttr(ColourEnum.COLOUR_RED, 0));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, table.getAttr(ColourEnum.COLOUR_RED, 1));
        assertEquals(ColourEnum.COLOUR_LIGHT_PINK, table.getAttr(ColourEnum.COLOUR_RED, 2));
    }

    /** C's {@code frame % 3}: frame 3 wraps to step 0, 4 to step 1, 5 to step 2. */
    @Test
    void framesWrapModuloThree() {
        FlickerTable table = redTable();
        assertEquals(ColourEnum.COLOUR_RED, table.getAttr(ColourEnum.COLOUR_RED, 3));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, table.getAttr(ColourEnum.COLOUR_RED, 4));
        assertEquals(ColourEnum.COLOUR_LIGHT_PINK, table.getAttr(ColourEnum.COLOUR_RED, 5));
    }

    /**
     * C's frame counter is a {@code uint8_t}, so the largest frame it ever passes is 255
     * (255 % 3 == 0) and the counter then restarts at 0 (also step 0). A large frame is still just
     * the remainder.
     */
    @Test
    void largestCounterValueAndLargeFramesUseTheRemainder() {
        FlickerTable table = redTable();
        assertEquals(ColourEnum.COLOUR_RED, table.getAttr(ColourEnum.COLOUR_RED, 255));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, table.getAttr(ColourEnum.COLOUR_RED, 256));
        assertEquals(ColourEnum.COLOUR_LIGHT_PINK, table.getAttr(ColourEnum.COLOUR_RED, 1_000_001));
    }

    /**
     * C never passes a negative frame ({@code size_t}), so the answer there is Java-defined:
     * {@code floorMod} keeps the result a valid step rather than throwing.
     */
    @Test
    void negativeFrameStaysOnTheCycle() {
        FlickerTable table = redTable();
        assertEquals(ColourEnum.COLOUR_LIGHT_PINK, table.getAttr(ColourEnum.COLOUR_RED, -1));
        assertEquals(ColourEnum.COLOUR_RED, table.getAttr(ColourEnum.COLOUR_RED, -3));
    }

    /**
     * A base attribute no block wrote is a zeroed row in C, so every frame reads colour 0, dark;
     * not {@code BASIC_COLORS}, which C keeps for a missing table or an attribute of 32 or more.
     */
    @Test
    void absentKeyIsDarkAtEveryFrame() {
        FlickerTable table = redTable();
        for (int frame = 0; frame < 7; frame++) {
            assertEquals(ColourEnum.COLOUR_DARK, table.getAttr(ColourEnum.COLOUR_BLUE, frame),
                    "frame " + frame);
        }
    }

    /** An empty table behaves as C's untouched zeroed table: dark for every key. */
    @Test
    void emptyTableIsDarkForEveryKey() {
        FlickerTable table = new FlickerTable(new HashMap<>());
        assertEquals(ColourEnum.COLOUR_DARK, table.getAttr(ColourEnum.COLOUR_RED, 0));
        assertEquals(ColourEnum.COLOUR_DARK, table.getAttr(ColourEnum.COLOUR_WHITE, 5));
    }

    /** Keys do not bleed into each other: each key answers from its own row. */
    @Test
    void eachKeyReadsItsOwnRow() {
        Map<ColourEnum, ColourCycle> map = new HashMap<>();
        map.put(ColourEnum.COLOUR_RED, cycle("r", ColourEnum.COLOUR_RED,
                ColourEnum.COLOUR_LIGHT_RED, ColourEnum.COLOUR_LIGHT_PINK));
        map.put(ColourEnum.COLOUR_GREEN, cycle("g", ColourEnum.COLOUR_GREEN,
                ColourEnum.COLOUR_LIGHT_GREEN, ColourEnum.COLOUR_LIGHT_TEAL));
        FlickerTable table = new FlickerTable(map);
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, table.getAttr(ColourEnum.COLOUR_GREEN, 1));
        assertEquals(ColourEnum.COLOUR_LIGHT_RED, table.getAttr(ColourEnum.COLOUR_RED, 1));
    }

    /**
     * A cycle that holds a dark step (the assembler's padding for a short block) returns it like
     * any other step, as C returns the zero byte in an unwritten slot.
     */
    @Test
    void paddedDarkStepIsReturned() {
        Map<ColourEnum, ColourCycle> map = new HashMap<>();
        map.put(ColourEnum.COLOUR_BLUE, cycle("b", ColourEnum.COLOUR_BLUE,
                ColourEnum.COLOUR_DARK, ColourEnum.COLOUR_DARK));
        FlickerTable table = new FlickerTable(map);
        assertEquals(ColourEnum.COLOUR_BLUE, table.getAttr(ColourEnum.COLOUR_BLUE, 0));
        assertEquals(ColourEnum.COLOUR_DARK, table.getAttr(ColourEnum.COLOUR_BLUE, 1));
        assertEquals(ColourEnum.COLOUR_DARK, table.getAttr(ColourEnum.COLOUR_BLUE, 2));
    }

    /**
     * Every {@code flicker:} block of the shipped visuals.txt, as C would read it back through
     * {@code visuals_flicker_get_attr_for_frame()} at frames 0, 1, 2 and 3. Each row is the base
     * header code in {@code keys}, its three step codes in {@code steps}, transcribed from the file.
     */
    @Test
    void shippedFileBlocksReadBackAsInC() throws IOException {
        String[] keys = {"d", "w", "s", "o", "r", "g", "b", "u", "D", "W", "P", "y", "R", "G",
                "B", "U", "p", "v", "t", "m", "Y", "i", "T", "V", "I", "M", "z", "Z"};
        String[] steps = {"dDR", "wWB", "swD", "oyR", "rRI", "gGT", "bBs", "uUM", "DsV", "wsW",
                "PpV", "yYM", "RrI", "GTg", "BZz", "Uum", "pvi", "vVi", "tTG", "myu", "YwU",
                "iIR", "TWt", "VPv", "IRW", "Myu", "zbs", "ZBb"};
        assertEquals(keys.length, steps.length);

        FlickerTable table = new VisualsReader().parseFlickerWithResults(REAL_FILE).items().getFirst();

        for (int i = 0; i < keys.length; i++) {
            ColourEnum key = ColourEnum.fromCode(keys[i].charAt(0));
            for (int frame = 0; frame < 4; frame++) {
                ColourEnum expected = ColourEnum.fromCode(steps[i].charAt(frame % 3));
                assertEquals(expected, table.getAttr(key, frame),
                        "block " + keys[i] + " frame " + frame);
            }
        }
    }

    /**
     * The one colour the shipped file gives no block, {@code COLOUR_SHADE}, is a zeroed row in C:
     * dark at every frame.
     */
    @Test
    void shippedFileLeavesShadeDark() throws IOException {
        FlickerTable table = new VisualsReader().parseFlickerWithResults(REAL_FILE).items().getFirst();
        for (int frame = 0; frame < 4; frame++) {
            assertEquals(ColourEnum.COLOUR_DARK, table.getAttr(ColourEnum.COLOUR_SHADE, frame));
        }
    }
}
