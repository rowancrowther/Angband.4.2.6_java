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
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the two known-artifact predicates: {@link ItemObject#isKnownArtifact()}, the port of
 * {@code object_is_known_artifact} in {@code obj-knowledge.c}, and
 * {@link ObjectUtils#objIsKnownArtifact}, the port of {@code obj_is_known_artifact} in
 * {@code obj-util.c}.
 *
 * <p>Expected values come from the C bodies. The first reads only the known half
 * ({@code obj->known && obj->known->artifact}); the second requires the real object's artifact,
 * a known half, and the known half's artifact, in that order. The two differ on exactly one input
 * shape, a known half with an artifact on a real object with none, and a test of that case is the
 * one that would catch the pair being swapped.
 *
 * <p>Every item is built through the full constructor, because {@link ItemObject} has no artifact
 * setter and so that is the only way to give a known half an artifact.
 *
 * <p>Test class KnownArtifactTest written on 261008.
 */
class KnownArtifactTest {

    /**
     * Builds an item through the full constructor with only the artifact and known half varied.
     *
     * @param artifact the real object's artifact, or {@code null}
     * @param known    the known half, or {@code null}
     * @return the constructed item
     */
    private static ItemObject item(Artifact artifact, ItemObject known) {
        return new ItemObject(new ObjectKind(), null, artifact, known, Loc.zero, TValue.TV_SWORD, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, null);
    }

    /**
     * A minimal artifact definition; only its being non-null matters to the predicates.
     *
     * @return an artifact with every field empty
     */
    private static Artifact artifact() {
        return new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                0, 0, 0, 0, null, null, null);
    }

    /**
     * A known half, with or without an artifact.
     *
     * @param artifact the artifact the player has learned, or {@code null}
     * @return the known half
     */
    private static ItemObject knownHalf(Artifact artifact) {
        return item(artifact, null);
    }

    @Test
    @DisplayName("isKnownArtifact: no known half is false")
    void isKnownNoKnownHalf() {
        assertFalse(item(artifact(), null).isKnownArtifact());
    }

    @Test
    @DisplayName("isKnownArtifact: known half without an artifact is false")
    void isKnownHalfWithoutArtifact() {
        assertFalse(item(artifact(), knownHalf(null)).isKnownArtifact());
    }

    @Test
    @DisplayName("isKnownArtifact: known half with an artifact is true")
    void isKnownHalfWithArtifact() {
        assertTrue(item(artifact(), knownHalf(artifact())).isKnownArtifact());
    }

    @Test
    @DisplayName("isKnownArtifact ignores the real object's own artifact")
    void isKnownIgnoresRealArtifact() {
        // C: object_is_known_artifact tests obj->known->artifact only.
        assertTrue(item(null, knownHalf(artifact())).isKnownArtifact());
        assertFalse(item(artifact(), knownHalf(null)).isKnownArtifact());
    }

    @Test
    @DisplayName("objIsKnownArtifact: real object without an artifact is false")
    void objNoRealArtifact() {
        assertFalse(ObjectUtils.objIsKnownArtifact(item(null, null)));
        assertFalse(ObjectUtils.objIsKnownArtifact(item(null, knownHalf(null))));
    }

    @Test
    @DisplayName("objIsKnownArtifact: known half with an artifact on a real object with none is false")
    void objKnownHalfOnlyIsFalse() {
        // The one input shape on which the two C predicates disagree.
        ItemObject odd = item(null, knownHalf(artifact()));
        assertTrue(odd.isKnownArtifact());
        assertFalse(ObjectUtils.objIsKnownArtifact(odd));
    }

    @Test
    @DisplayName("objIsKnownArtifact: artifact with no known half is false")
    void objNoKnownHalf() {
        assertFalse(ObjectUtils.objIsKnownArtifact(item(artifact(), null)));
    }

    @Test
    @DisplayName("objIsKnownArtifact: artifact not yet learned is false")
    void objArtifactNotLearned() {
        assertFalse(ObjectUtils.objIsKnownArtifact(item(artifact(), knownHalf(null))));
    }

    @Test
    @DisplayName("objIsKnownArtifact: artifact on both halves is true")
    void objBothHalves() {
        assertTrue(ObjectUtils.objIsKnownArtifact(item(artifact(), knownHalf(artifact()))));
    }
}
