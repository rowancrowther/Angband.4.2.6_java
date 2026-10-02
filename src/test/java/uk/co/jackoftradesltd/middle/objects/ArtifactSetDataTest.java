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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ArtifactIndex;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.ObjIntConsumer;
import java.util.function.ToIntFunction;
import java.util.function.IntConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link ArtifactSetData}'s public accessors - the Java face of C's
 * {@code struct artifact_set_data} ({@code obj-randart.h}), as built by
 * {@code artifact_set_data_new()} in {@code obj-randart.c}.
 *
 * <p>{@code ObjectRandartArtifactSetDataNewTest} already pins the constructed state through
 * reflection; this class checks the same C defaults are what a caller sees through the getters, that
 * each setter writes the field its getter reads (no crossed wires between thirty near-identical
 * pairs), that a keyed write touches only its own key as an array element write does in C, and that
 * C's {@code +=} / {@code ++} idiom survives being split into a get and a set.
 *
 * <p>Class ArtifactSetDataTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
class ArtifactSetDataTest {

    /**
     * The artifact table as it was before each test.
     */
    private List<Artifact> savedArtifacts;

    /**
     * A first loaded artifact.
     */
    private Artifact grond;

    /**
     * A second loaded artifact, used to check a keyed write does not leak across keys.
     */
    private Artifact ringil;

    /**
     * A minimal artifact, distinguished only by name - enough to be a distinct map key.
     *
     * @param name the artifact's name
     * @return the artifact
     */
    private static Artifact artifact(String name) {
        return new Artifact(name, "", TValue.TV_SWORD, "long sword",
                0, 0, 0, 0, "1d1", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(),
                0, 0, 0, 0, null, "", new Random(0, 1, 1, 1, false));
    }

    /**
     * The registry's static {@code artifacts} field, made accessible.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field artifactsField() throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField("artifacts");
        f.setAccessible(true);
        return f;
    }

    /**
     * Saves the artifact table and loads two artifacts in its place.
     *
     * @throws Exception if the field cannot be reached
     */
    @BeforeEach
    @SuppressWarnings("unchecked")
    void loadTwoArtifacts() throws Exception {
        savedArtifacts = (List<Artifact>) artifactsField().get(null);
        grond = artifact("Grond");
        ringil = artifact("Ringil");
        ObjectRegistry.setArtifacts(List.of(grond, ringil));
    }

    /**
     * Puts the artifact table back, including a table that was {@code null} before the test ran.
     *
     * @throws Exception if the field cannot be reached
     */
    @AfterEach
    void restoreArtifacts() throws Exception {
        artifactsField().set(null, savedArtifacts);
    }

    /**
     * Each setter writes exactly the field its getter reads. Every pair is given a distinct value
     * before any is read back, so a setter wired to the wrong field shows up as a mismatch.
     */
    @Test
    @DisplayName("each scalar setter writes the field its getter reads")
    void scalarSettersRoundTrip() {
        ArtifactSetData data = new ArtifactSetData();

        IntConsumer[] setters = {
                data::setHitIncrement, data::setDamIncrement, data::setHitStartVal, data::setDamStartVal,
                data::setAcStartVal, data::setAcIncrement, data::setBowTotal, data::setMeleeTotal,
                data::setBootTotal, data::setGloveTotal, data::setHeadgearTotal, data::setShieldTotal,
                data::setCloakTotal, data::setArmourTotal, data::setOtherTotal, data::setTotal,
                data::setNegPowerTotal, data::setMaxPower, data::setMinPower, data::setAvgPower,
                data::setVarPower};
        IntSupplier[] getters = {
                data::getHitIncrement, data::getDamIncrement, data::getHitStartVal, data::getDamStartVal,
                data::getAcStartVal, data::getAcIncrement, data::getBowTotal, data::getMeleeTotal,
                data::getBootTotal, data::getGloveTotal, data::getHeadgearTotal, data::getShieldTotal,
                data::getCloakTotal, data::getArmourTotal, data::getOtherTotal, data::getTotal,
                data::getNegPowerTotal, data::getMaxPower, data::getMinPower, data::getAvgPower,
                data::getVarPower};

        for (int i = 0; i < setters.length; i++) {
            setters[i].accept(100 + i);
        }
        for (int i = 0; i < getters.length; i++) {
            assertEquals(100 + i, getters[i].getAsInt(), "pair " + i);
        }
    }

    /**
     * The tval-keyed pairs, given distinct values at the first and last tval before reading back.
     * Each write must land only under its own key, as {@code data->tv_probs[i] = x} does in C.
     */
    @Test
    @DisplayName("each tval-keyed setter writes only its own map and key")
    @SuppressWarnings("unchecked")
    void tvalSettersRoundTrip() {
        ArtifactSetData data = new ArtifactSetData();
        TValue first = TValue.values()[0];
        TValue last = TValue.values()[TValue.values().length - 1];
        TValue untouched = TValue.TV_SWORD;

        ObjIntConsumer<TValue>[] setters = new ObjIntConsumer[]{
                (ObjIntConsumer<TValue>) data::setTvProbs, (ObjIntConsumer<TValue>) data::setTvNum,
                (ObjIntConsumer<TValue>) data::setTvFreq, (ObjIntConsumer<TValue>) data::setAvgTvPower,
                (ObjIntConsumer<TValue>) data::setMinTvPower, (ObjIntConsumer<TValue>) data::setMaxTvPower};
        ToIntFunction<TValue>[] getters = new ToIntFunction[]{
                (ToIntFunction<TValue>) data::getTvProbs, (ToIntFunction<TValue>) data::getTvNum,
                (ToIntFunction<TValue>) data::getTvFreq, (ToIntFunction<TValue>) data::getAvgTvPower,
                (ToIntFunction<TValue>) data::getMinTvPower, (ToIntFunction<TValue>) data::getMaxTvPower};

        for (int i = 0; i < setters.length; i++) {
            setters[i].accept(first, 10 + i);
            setters[i].accept(last, 20 + i);
        }
        for (int i = 0; i < getters.length; i++) {
            assertEquals(10 + i, getters[i].applyAsInt(first), "pair " + i + " at " + first);
            assertEquals(20 + i, getters[i].applyAsInt(last), "pair " + i + " at " + last);
            assertEquals(0, getters[i].applyAsInt(untouched), "pair " + i + " at " + untouched);
        }
    }

    /**
     * The artifact-keyed pairs: a write against one artifact leaves the other at {@code 0}.
     */
    @Test
    @DisplayName("each artifact-keyed setter writes only its own map and key")
    @SuppressWarnings("unchecked")
    void artifactSettersRoundTrip() {
        ArtifactSetData data = new ArtifactSetData();

        ObjIntConsumer<Artifact>[] setters = new ObjIntConsumer[]{
                (ObjIntConsumer<Artifact>) data::setBasePower, (ObjIntConsumer<Artifact>) data::setBaseItemLevel,
                (ObjIntConsumer<Artifact>) data::setBaseItemProb, (ObjIntConsumer<Artifact>) data::setBaseArtAlloc};
        ToIntFunction<Artifact>[] getters = new ToIntFunction[]{
                (ToIntFunction<Artifact>) data::getBasePower, (ToIntFunction<Artifact>) data::getBaseItemLevel,
                (ToIntFunction<Artifact>) data::getBaseItemProb, (ToIntFunction<Artifact>) data::getBaseArtAlloc};

        for (int i = 0; i < setters.length; i++) {
            setters[i].accept(grond, 30 + i);
        }
        for (int i = 0; i < getters.length; i++) {
            assertEquals(30 + i, getters[i].applyAsInt(grond), "pair " + i + " at Grond");
            assertEquals(0, getters[i].applyAsInt(ringil), "pair " + i + " at Ringil");
        }
    }

    /**
     * The {@code art_probs} pair, at the first index and at a {@code _SUPER} index next to the
     * sentinel; a neighbouring index stays at {@code 0}.
     */
    @Test
    @DisplayName("setArtProbs writes only its own index")
    void artProbsRoundTrip() {
        ArtifactSetData data = new ArtifactSetData();
        ArtifactIndex first = ArtifactIndex.values()[0];

        data.setArtProbs(first, 3);
        data.setArtProbs(ArtifactIndex.ART_IDX_GEN_AC_SUPER, 9);

        assertEquals(3, data.getArtProbs(first));
        assertEquals(9, data.getArtProbs(ArtifactIndex.ART_IDX_GEN_AC_SUPER));
        assertEquals(0, data.getArtProbs(ArtifactIndex.ART_IDX_MELEE_AC_SUPER));
    }

    /**
     * C accumulates in place - {@code data->art_probs[ART_IDX_WEAPON_HIT] += bonus;} followed by
     * {@code (data->art_probs[ART_IDX_MELEE_AC_SUPER])++;} in {@code count_weapon_abilities()}.
     * Done here as get-then-set, two bonuses of 2 and 3 must total 5, and one increment must give 1.
     */
    @Test
    @DisplayName("C's += and ++ accumulate correctly as get-then-set")
    void accumulateAsGetThenSet() {
        ArtifactSetData data = new ArtifactSetData();

        data.setArtProbs(ArtifactIndex.ART_IDX_WEAPON_HIT, data.getArtProbs(ArtifactIndex.ART_IDX_WEAPON_HIT) + 2);
        data.setArtProbs(ArtifactIndex.ART_IDX_WEAPON_HIT, data.getArtProbs(ArtifactIndex.ART_IDX_WEAPON_HIT) + 3);
        data.setArtProbs(ArtifactIndex.ART_IDX_MELEE_AC_SUPER,
                data.getArtProbs(ArtifactIndex.ART_IDX_MELEE_AC_SUPER) + 1);
        data.setTvNum(TValue.TV_BOW, data.getTvNum(TValue.TV_BOW) + 1);

        assertEquals(5, data.getArtProbs(ArtifactIndex.ART_IDX_WEAPON_HIT));
        assertEquals(1, data.getArtProbs(ArtifactIndex.ART_IDX_MELEE_AC_SUPER));
        assertEquals(1, data.getTvNum(TValue.TV_BOW));
    }

    /**
     * The documented divergence: an artifact not loaded when the object was built has no entry, so
     * every artifact-keyed getter throws on unboxing rather than returning {@code 0}. C has no
     * equivalent - its array is sized to {@code z_info->a_max} at allocation and a later index would
     * read past the end.
     */
    @Test
    @DisplayName("an artifact loaded after construction has no entry, so its getters throw")
    void lateArtifactHasNoEntry() {
        ArtifactSetData data = new ArtifactSetData();
        Artifact late = artifact("Narya");

        assertThrows(NullPointerException.class, () -> data.getBasePower(late));
        assertThrows(NullPointerException.class, () -> data.getBaseItemLevel(late));
        assertThrows(NullPointerException.class, () -> data.getBaseItemProb(late));
        assertThrows(NullPointerException.class, () -> data.getBaseArtAlloc(late));
    }

    /**
     * What a caller sees through the getters straight after construction.
     */
    @Nested
    @DisplayName("straight after construction")
    class Defaults {

        /**
         * The six hand-set values from {@code artifact_set_data_new()}.
         */
        @Test
        @DisplayName("the start and increment getters return C's hand-set values")
        void startAndIncrementGetters() {
            ArtifactSetData data = new ArtifactSetData();

            assertEquals(4, data.getHitIncrement());
            assertEquals(4, data.getDamIncrement());
            assertEquals(10, data.getHitStartVal());
            assertEquals(10, data.getDamStartVal());
            assertEquals(15, data.getAcStartVal());
            assertEquals(5, data.getAcIncrement());
        }

        /**
         * Every scalar counter and power figure reads the zero {@code mem_zalloc} leaves.
         */
        @Test
        @DisplayName("every scalar counter getter returns 0")
        void scalarGettersReturnZero() {
            ArtifactSetData data = new ArtifactSetData();

            assertEquals(0, data.getBowTotal());
            assertEquals(0, data.getMeleeTotal());
            assertEquals(0, data.getBootTotal());
            assertEquals(0, data.getGloveTotal());
            assertEquals(0, data.getHeadgearTotal());
            assertEquals(0, data.getShieldTotal());
            assertEquals(0, data.getCloakTotal());
            assertEquals(0, data.getArmourTotal());
            assertEquals(0, data.getOtherTotal());
            assertEquals(0, data.getTotal());
            assertEquals(0, data.getNegPowerTotal());
            assertEquals(0, data.getMaxPower());
            assertEquals(0, data.getMinPower());
            assertEquals(0, data.getAvgPower());
            assertEquals(0, data.getVarPower());
        }

        /**
         * Every valid index of every zeroed array reads {@code 0} through its getter - including the
         * first and last tval ({@code TV_NULL} and {@code TV_MAX - 1}), the array boundaries in C.
         */
        @Test
        @DisplayName("every keyed getter returns 0 for every key")
        void keyedGettersReturnZero() {
            ArtifactSetData data = new ArtifactSetData();

            for (ArtifactIndex index : ArtifactIndex.values()) {
                assertEquals(0, data.getArtProbs(index), "artProbs[" + index + "]");
            }
            for (TValue tval : TValue.values()) {
                assertEquals(0, data.getTvProbs(tval), "tvProbs[" + tval + "]");
                assertEquals(0, data.getTvNum(tval), "tvNum[" + tval + "]");
                assertEquals(0, data.getTvFreq(tval), "tvFreq[" + tval + "]");
                assertEquals(0, data.getAvgTvPower(tval), "avgTvPower[" + tval + "]");
                assertEquals(0, data.getMinTvPower(tval), "minTvPower[" + tval + "]");
                assertEquals(0, data.getMaxTvPower(tval), "maxTvPower[" + tval + "]");
            }
            for (Artifact art : List.of(grond, ringil)) {
                assertEquals(0, data.getBasePower(art));
                assertEquals(0, data.getBaseItemLevel(art));
                assertEquals(0, data.getBaseItemProb(art));
                assertEquals(0, data.getBaseArtAlloc(art));
            }
        }
    }
}
