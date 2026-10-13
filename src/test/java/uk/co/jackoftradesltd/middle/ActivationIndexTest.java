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

package uk.co.jackoftradesltd.middle;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.ActivationReader;
import uk.co.jackoftradesltd.backend.parser.activation.ActivationAssembler;
import uk.co.jackoftradesltd.backend.parser.activation.ActivationParseRecord;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the activation {@code index}: {@link Activation#setIndex(int)} and the numbering
 * {@code ActivationAssembler} does with it.
 *
 * <p>The expected values are read off C's {@code finish_parse_act} in {@code obj-init.c}, which
 * copies the parser's list into {@code activations[]} from the head and sets
 * {@code activations[count].index = count} as it goes. The head is the last record in the file, so
 * the index runs up the stored list: the first activation in the array has the lowest index, and
 * the index equals the slot. C counts from 1, because slot 0 is a blank record; the port keeps no
 * blank slot (an accepted deviation), so its indexes are C's minus one, from 0. C's list holds every
 * record that parsed, so there are no gaps.
 *
 * <p>{@code Activation} has no getter for the index yet, so the tests read the field by reflection.
 *
 * <p>Class ActivationIndexTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
class ActivationIndexTest {

    private static final String REAL_FILE = "lib/gamedata/activation.txt";

    /**
     * Reads an activation's private {@code index} field.
     */
    private static int indexOf(Activation activation) throws Exception {
        Field f = Activation.class.getDeclaredField("index");
        f.setAccessible(true);
        return (int) f.get(activation);
    }

    /**
     * A parse record with an empty effect list, so no registry is consulted.
     */
    private static ActivationParseRecord rec(String name, String level, int line) {
        return new ActivationParseRecord(name, "0", level, "1", "", "", List.of(), line);
    }

    @Test
    @DisplayName("setIndex stores the value as given")
    void setIndexStoresTheValue() throws Exception {
        Activation activation = new Activation("TEST", 0, false, 1, 1, List.of(), "", "");

        activation.setIndex(42);

        assertEquals(42, indexOf(activation));
    }

    @Test
    @DisplayName("copy keeps the original's index")
    void copyKeepsTheIndex() throws Exception {
        Activation activation = new Activation("TEST", 0, false, 1, 1, List.of(), "", "");
        activation.setIndex(7);

        assertEquals(7, indexOf(activation.copy()));
    }

    @Test
    @DisplayName("on the real file the index equals the slot: DRAGON_POWER 0 up to CURE_POISON 162")
    void indexEqualsSlotOnTheRealFile() throws Exception {
        List<Activation> activations = new ActivationReader().parseWithResult(REAL_FILE).items();

        assertEquals(163, activations.size());
        for (int slot = 0; slot < activations.size(); slot++) {
            assertEquals(slot, indexOf(activations.get(slot)), "index of " + activations.get(slot).getName());
        }
        assertEquals("DRAGON_POWER", activations.getFirst().getName());
        assertEquals("CURE_POISON", activations.getLast().getName());
        assertEquals(162, indexOf(activations.getLast()));
    }

    @Test
    @DisplayName("a dropped record leaves no gap: the survivors are numbered 0, 1, 2 in stored order")
    void droppedRecordLeavesNoGap() throws Exception {
        List<String> errors = new ArrayList<>();

        List<Activation> out = new ActivationAssembler().assemble(
                List.of(rec("FIRST", "5", 3), rec("BROKEN", "high", 9), rec("LAST", "5", 15)), errors);

        assertEquals(1, errors.size());
        assertEquals(2, out.size());
        assertEquals("LAST", out.get(0).getName(), "last file record is stored first, as in C");
        assertEquals(0, indexOf(out.get(0)));
        assertEquals("FIRST", out.get(1).getName());
        assertEquals(1, indexOf(out.get(1)));
    }

    @Test
    @DisplayName("an empty record list assembles to an empty list")
    void emptyInputGivesEmptyList() {
        assertTrue(new ActivationAssembler().assemble(List.of(), new ArrayList<>()).isEmpty());
    }

    @Test
    @DisplayName("the real file loads without errors")
    void realFileIsClean() throws IOException {
        assertFalse(new ActivationReader().parseWithResult(REAL_FILE).hasErrors());
    }
}
