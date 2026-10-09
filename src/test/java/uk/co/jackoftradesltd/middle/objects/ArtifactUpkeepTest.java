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

    /**
     * With all three flags set, clearing each one in turn clears only that flag. C's
     * {@code mark_artifact_seen()} and {@code mark_artifact_everseen()} take a bool and write it
     * straight through, so {@code false} is as valid an argument as {@code true}; this covers the
     * {@code seen} and {@code everseen} clears that the previous test does not reach.
     */
    @Test
    @DisplayName("each flag can be cleared without disturbing the others")
    void eachFlagClearsIndependently() {
        ArtifactUpkeep upkeep = new ArtifactUpkeep();
        upkeep.setCreated(true);
        upkeep.setSeen(true);
        upkeep.setEverseen(true);

        upkeep.setSeen(false);
        assertEquals(true, upkeep.isCreated(), "clearing seen must not clear created");
        assertFalse(upkeep.isSeen());
        assertEquals(true, upkeep.isEverseen(), "clearing seen must not clear everseen");

        upkeep.setSeen(true);
        upkeep.setEverseen(false);
        assertEquals(true, upkeep.isCreated(), "clearing everseen must not clear created");
        assertEquals(true, upkeep.isSeen(), "clearing everseen must not clear seen");
        assertFalse(upkeep.isEverseen());
    }

    /**
     * Two instances share no state — C gives each artifact its own {@code aup_info} slot, so
     * marking one artifact created must not mark another.
     */
    @Test
    @DisplayName("instances do not share flag state")
    void instancesAreIndependent() {
        ArtifactUpkeep first = new ArtifactUpkeep();
        ArtifactUpkeep second = new ArtifactUpkeep();

        first.setCreated(true);
        first.setSeen(true);
        first.setEverseen(true);

        assertFalse(second.isCreated());
        assertFalse(second.isSeen());
        assertFalse(second.isEverseen());
    }
}
