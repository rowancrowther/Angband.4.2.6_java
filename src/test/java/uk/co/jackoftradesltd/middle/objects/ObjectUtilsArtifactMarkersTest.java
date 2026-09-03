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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests {@link ObjectUtils#markArtifactCreated} and {@link ObjectUtils#markArtifactSeen}, the ports
 * of C's {@code mark_artifact_created} and {@code mark_artifact_seen} ({@code obj-util.c}).
 *
 * <p>C writes into a parallel {@code aup_info} array; the port writes into the {@link ArtifactUpkeep}
 * the {@link Artifact} constructor now attaches to every artifact it builds. That attachment is the
 * part worth locking in here: before it existed, {@link Artifact#getAup()} returned {@code null} and
 * both methods threw on any artifact built the normal way, including on the call path exercised by
 * {@code PlayerBirth.playerInit}, which calls both once per registry artifact.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsArtifactMarkersTest {

    /**
     * Builds an artifact through the same constructor every real artifact goes through, so the test
     * exercises the same {@code aupInfo} wiring the game does.
     *
     * @return a freshly built artifact
     */
    private static Artifact artifact() {
        return new Artifact("Test Blade", "It gleams.", TValue.TV_SWORD, "long sword",
                5, 6, 7, 8, "3d5", 120, 4500,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(),
                40, 11, 12, 13,
                new Activation("test activation", 1, false, 5, 30, new ArrayList<>(), "It fires.", "fires"),
                "The blade glows.", new Random(0, 1, 1, 20, false));
    }

    /**
     * A freshly built artifact does not throw when marked — the regression this suite exists to
     * catch. Before {@code Artifact}'s constructor attached an {@link ArtifactUpkeep}, {@code getAup()}
     * returned {@code null} and both marker methods threw a {@link NullPointerException} here.
     */
    @Test
    @DisplayName("marking a freshly built artifact does not throw")
    void markingFreshArtifactDoesNotThrow() {
        Artifact art = artifact();

        assertDoesNotThrow(() -> ObjectUtils.markArtifactCreated(art, true));
        assertDoesNotThrow(() -> ObjectUtils.markArtifactSeen(art, true));
    }

    /**
     * {@code markArtifactCreated(art, true)} sets the {@code created} flag, matching C's
     * {@code aup_info[art->aidx].created = created}.
     */
    @Test
    @DisplayName("markArtifactCreated(true) sets created")
    void markArtifactCreatedTrue() {
        Artifact art = artifact();

        ObjectUtils.markArtifactCreated(art, true);

        assertTrue(art.getAup().isCreated());
    }

    /**
     * {@code markArtifactCreated(art, false)} clears an already-set {@code created} flag — the
     * overwrite path C's {@code player_init} (player-birth.c:407-410) relies on to reset every
     * artifact at the start of a new game, regardless of what a previous game left behind.
     */
    @Test
    @DisplayName("markArtifactCreated(false) clears an already-set created")
    void markArtifactCreatedFalseOverwrites() {
        Artifact art = artifact();
        ObjectUtils.markArtifactCreated(art, true);

        ObjectUtils.markArtifactCreated(art, false);

        assertFalse(art.getAup().isCreated());
    }

    /**
     * {@code markArtifactSeen(art, true)} sets the {@code seen} flag, matching C's
     * {@code aup_info[art->aidx].seen = seen}.
     */
    @Test
    @DisplayName("markArtifactSeen(true) sets seen")
    void markArtifactSeenTrue() {
        Artifact art = artifact();

        ObjectUtils.markArtifactSeen(art, true);

        assertTrue(art.getAup().isSeen());
    }

    /**
     * {@code markArtifactSeen(art, false)} clears an already-set {@code seen} flag — the same
     * overwrite path C's {@code player_init} relies on for {@code seen} as for {@code created}.
     */
    @Test
    @DisplayName("markArtifactSeen(false) clears an already-set seen")
    void markArtifactSeenFalseOverwrites() {
        Artifact art = artifact();
        ObjectUtils.markArtifactSeen(art, true);

        ObjectUtils.markArtifactSeen(art, false);

        assertFalse(art.getAup().isSeen());
    }

    /**
     * Setting {@code created} must not touch {@code seen}, and vice versa — C treats the two as
     * independent struct fields, so a shared-storage bug in the port would show up as one write
     * bleeding into the other.
     */
    @Test
    @DisplayName("created and seen are independent")
    void createdAndSeenAreIndependent() {
        Artifact art = artifact();

        ObjectUtils.markArtifactCreated(art, true);
        assertFalse(art.getAup().isSeen(), "marking created must not set seen");

        ObjectUtils.markArtifactSeen(art, true);
        ObjectUtils.markArtifactCreated(art, false);
        assertTrue(art.getAup().isSeen(), "clearing created must not clear seen");
    }
}
