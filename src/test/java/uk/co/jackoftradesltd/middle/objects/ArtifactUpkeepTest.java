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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests {@link ArtifactUpkeep}'s three flags.
 *
 * <p>C's {@code struct artifact_upkeep} is zeroed when {@code aup_info} is allocated, so a fresh
 * entry starts with every flag clear; the port relies on Java's own default-{@code false} for
 * booleans to give the same starting state. Each flag is otherwise an independent storage cell —
 * C reads and writes {@code created}, {@code seen} and {@code everseen} separately, and nothing in
 * {@code obj-util.c} ties one to another, so the interesting failure mode is a setter writing to
 * the wrong field.
 *
 * @author Rowan Crowther
 */
class ArtifactUpkeepTest {

    /**
     * A fresh instance starts with every flag clear, matching a zeroed {@code aup_info} entry.
     */
    @Test
    @DisplayName("a fresh instance starts with every flag clear")
    void freshInstanceAllClear() {
        ArtifactUpkeep upkeep = new ArtifactUpkeep();

        assertFalse(upkeep.isCreated());
        assertFalse(upkeep.isSeen());
        assertFalse(upkeep.isEverseen());
    }

    /**
     * Each flag round-trips through its own accessor, and setting one leaves the other two alone —
     * the check that would catch a setter writing to the wrong field.
     */
    @Test
    @DisplayName("each flag round-trips independently")
    void flagsRoundTripIndependently() {
        ArtifactUpkeep upkeep = new ArtifactUpkeep();

        upkeep.setCreated(true);
        assertEquals(true, upkeep.isCreated());
        assertFalse(upkeep.isSeen());
        assertFalse(upkeep.isEverseen());

        upkeep.setSeen(true);
        assertEquals(true, upkeep.isSeen());
        assertFalse(upkeep.isEverseen());

        upkeep.setEverseen(true);
        assertEquals(true, upkeep.isEverseen());

        upkeep.setCreated(false);
        assertFalse(upkeep.isCreated());
        assertEquals(true, upkeep.isSeen(), "clearing created must not clear seen");
        assertEquals(true, upkeep.isEverseen(), "clearing created must not clear everseen");
    }
}
