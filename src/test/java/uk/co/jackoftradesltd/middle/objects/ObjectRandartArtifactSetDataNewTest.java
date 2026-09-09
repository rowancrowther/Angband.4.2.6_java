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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectRandart#artifactSetDataNew()} and the {@link ArtifactSetData} constructor it
 * wraps — the port of C's {@code artifact_set_data_new()} ({@code obj-randart.c:2993}).
 *
 * <p>C allocates the struct with {@code mem_zalloc}, so every one of its eleven learned-probability
 * arrays reads {@code 0} at any valid index before a single artifact has been examined. Neither
 * {@link ObjectRandart#artifactSetDataNew()} nor {@link ArtifactSetData} expose that state through an
 * accessor yet, so every assertion here reaches it through reflection — the same technique
 * {@code ObjectRegistryTablesTest} and {@code PlayerBirthGetAHWTest} already use for fields with no
 * getters.
 *
 * <p>{@link ObjectRegistry}'s artifact table is static and read by the constructor, so each test
 * saves and restores it, matching {@code ObjectRegistryTablesTest}'s pattern.
 *
 * <p>Only construction is covered — {@code do_randart}'s later passes ({@code design_artifact} and
 * friends) are not yet ported, so there is nothing downstream to test against.
 *
 * <p>Class ObjectRandartArtifactSetDataNewTest coded on 260908, commented in full on 260908.
 *
 * @author Rowan Crowther
 */
class ObjectRandartArtifactSetDataNewTest {

    /**
     * The artifact table as it was before each test.
     */
    private List<Artifact> savedArtifacts;

    /**
     * One of {@code owner}'s declared fields, made accessible.
     *
     * @param owner the class declaring the field
     * @param name  the field's name
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field field(Class<?> owner, String name) throws Exception {
        Field f = owner.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * Invokes the private {@link ObjectRandart#artifactSetDataNew()} and hands back what it built.
     * Unwraps reflection's {@link InvocationTargetException} so a caller can assert on the real
     * exception the method threw, exactly as if it had been called directly.
     *
     * @return a freshly built {@link ArtifactSetData}
     * @throws Exception if the method cannot be reached, or whatever it threw
     */
    private static ArtifactSetData newData() throws Exception {
        Method m = ObjectRandart.class.getDeclaredMethod("artifactSetDataNew");
        m.setAccessible(true);
        try {
            return (ArtifactSetData) m.invoke(null);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException re) throw re;
            throw e;
        }
    }

    /**
     * A minimal artifact, distinguished only by name — enough to be a distinct map key.
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
     * Saves {@link ObjectRegistry}'s artifact table, which every test either replaces or clears.
     *
     * @throws Exception if the field cannot be reached
     */
    @BeforeEach
    @SuppressWarnings("unchecked")
    void saveArtifacts() throws Exception {
        savedArtifacts = (List<Artifact>) field(ObjectRegistry.class, "artifacts").get(null);
    }

    /**
     * Puts the artifact table back, including a table that was {@code null} before the test ran.
     *
     * @throws Exception if the field cannot be reached
     */
    @AfterEach
    void restoreArtifacts() throws Exception {
        field(ObjectRegistry.class, "artifacts").set(null, savedArtifacts);
    }

    /**
     * The six mean start and increment values, which C sets by hand after the {@code mem_zalloc}
     * ({@code obj-randart.c:3011-3016}) rather than leaving them zeroed.
     *
     * @throws Exception if a field cannot be reached
     */
    @Test
    @DisplayName("the hit/dam/AC start and increment values match C's hand-set defaults")
    void startAndIncrementValuesMatchC() throws Exception {
        ObjectRegistry.setArtifacts(List.of());
        ArtifactSetData data = newData();

        assertEquals(4, field(ArtifactSetData.class, "hitIncrement").getInt(data));
        assertEquals(4, field(ArtifactSetData.class, "damIncrement").getInt(data));
        assertEquals(10, field(ArtifactSetData.class, "hitStartVal").getInt(data));
        assertEquals(10, field(ArtifactSetData.class, "damStartVal").getInt(data));
        assertEquals(15, field(ArtifactSetData.class, "acStartVal").getInt(data));
        assertEquals(5, field(ArtifactSetData.class, "acIncrement").getInt(data));
    }

    /**
     * The plain {@code int} counters and power stats, all of which C leaves at the zero
     * {@code mem_zalloc} gives them until {@code store_base_power} and the tval-total passes run.
     *
     * @throws Exception if a field cannot be reached
     */
    @Test
    @DisplayName("the running totals and power stats start at zero")
    void countersStartAtZero() throws Exception {
        ObjectRegistry.setArtifacts(List.of());
        ArtifactSetData data = newData();

        String[] zeroFields = {"bowTotal", "meleeTotal", "bootTotal", "gloveTotal", "headgearTotal",
                "shieldTotal", "cloakTotal", "armourTotal", "otherTotal", "total", "negPowerTotal",
                "maxPower", "minPower", "avgPower", "varPower"};

        for (String name : zeroFields) {
            assertEquals(0, field(ArtifactSetData.class, name).getInt(data), name + " should start at 0");
        }
    }

    /**
     * {@code art_probs}, keyed by {@link ArtifactIndex} and {@code mem_zalloc}'d to
     * {@code ART_IDX_TOTAL} entries ({@code list-randart-properties.h}'s last row).
     * {@code ART_IDX_TOTAL} is itself a generated enum constant, one past every real index C ever
     * writes into the array — the port's loop does not special-case it out, so it picks up a zero
     * entry too. Harmless, since nothing reads it, but worth pinning down rather than assuming.
     *
     * @throws Exception if a field cannot be reached
     */
    @Test
    @DisplayName("artProbs holds one zero entry per ArtifactIndex, sentinel included")
    @SuppressWarnings("unchecked")
    void artProbsIsFullyZeroed() throws Exception {
        ObjectRegistry.setArtifacts(List.of());
        ArtifactSetData data = newData();

        Map<ArtifactIndex, Integer> artProbs =
                (Map<ArtifactIndex, Integer>) field(ArtifactSetData.class, "artProbs").get(data);

        assertEquals(ArtifactIndex.values().length, artProbs.size());
        for (ArtifactIndex index : ArtifactIndex.values()) {
            assertEquals(0, artProbs.get(index), "artProbs[" + index + "] should start at 0");
        }
        assertEquals(0, artProbs.get(ArtifactIndex.ART_IDX_TOTAL),
                "the sentinel gets a zero entry too, even though C never indexes it");
    }

    /**
     * Every field this test reads from expects the artifact registry to already be loaded — the
     * same implicit precondition C has on {@code a_info} being populated before {@code do_randart}
     * runs. An unloaded registry is a caller error, not a case the constructor tolerates quietly.
     *
     * @throws Exception if the field cannot be reached, or is not the expected exception
     */
    @Test
    @DisplayName("construction fails loudly if the artifact registry hasn't been loaded")
    void unloadedRegistryIsACallerError() throws Exception {
        field(ObjectRegistry.class, "artifacts").set(null, null);

        assertThrows(NullPointerException.class, ObjectRandartArtifactSetDataNewTest::newData);
    }

    /**
     * C's {@code mem_zalloc} hands back fresh memory on every call; two calls to
     * {@link ObjectRandart#artifactSetDataNew()} must be equally independent, not share a map that
     * a later {@code design_artifact} pass could cross-contaminate between a real run and a
     * mid-generation retry.
     *
     * @throws Exception if a field cannot be reached
     */
    @Test
    @DisplayName("two calls build independent, unshared state")
    @SuppressWarnings("unchecked")
    void callsAreIndependent() throws Exception {
        ObjectRegistry.setArtifacts(List.of());

        ArtifactSetData first = newData();
        ArtifactSetData second = newData();

        assertNotSame(first, second);

        Map<TValue, Integer> firstTvProbs = (Map<TValue, Integer>) field(ArtifactSetData.class, "tvProbs").get(first);
        Map<TValue, Integer> secondTvProbs = (Map<TValue, Integer>) field(ArtifactSetData.class, "tvProbs").get(second);

        assertNotSame(firstTvProbs, secondTvProbs);
        firstTvProbs.put(TValue.TV_SWORD, 7);
        assertEquals(0, secondTvProbs.get(TValue.TV_SWORD),
                "the second call's map must not see the first's mutation");
    }

    /**
     * The six maps keyed by {@link TValue} — C's {@code tv_probs}, {@code tv_num}, {@code tv_freq},
     * {@code avg_tv_power}, {@code min_tv_power} and {@code max_tv_power}, each
     * {@code mem_zalloc}'d to {@code TV_MAX} entries. C's array lets every valid index read
     * {@code 0}; the port has to earn that by pre-populating the map, which is what this checks.
     */
    @Nested
    @DisplayName("the TValue-keyed maps")
    class TvalKeyedMaps {

        /**
         * Every {@link TValue} the enum declares — the full {@code TV_MAX} domain — has an entry,
         * and that entry reads {@code 0}.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("hold one zero entry per TValue")
        @SuppressWarnings("unchecked")
        void everyTvalIsZeroed() throws Exception {
            ObjectRegistry.setArtifacts(List.of());
            ArtifactSetData data = newData();

            String[] mapFields = {"tvProbs", "tvNum", "tvFreq", "avgTvPower", "minTvPower", "maxTvPower"};

            for (String name : mapFields) {
                Map<TValue, Integer> map = (Map<TValue, Integer>) field(ArtifactSetData.class, name).get(data);
                assertEquals(TValue.values().length, map.size(), name + " should cover every TValue");

                for (TValue value : TValue.values()) {
                    assertEquals(0, map.get(value), name + "[" + value + "] should start at 0");
                }
            }
        }
    }

    /**
     * The four maps keyed by {@link Artifact} — C's {@code base_power}, {@code base_item_level},
     * {@code base_item_prob} and {@code base_art_alloc}, each {@code mem_zalloc}'d to
     * {@code z_info->a_max} entries.
     */
    @Nested
    @DisplayName("the artifact-keyed maps")
    class ArtifactKeyedMaps {

        /**
         * Every loaded {@link Artifact} gets a zero entry in all four maps.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("hold one zero entry per loaded artifact")
        @SuppressWarnings("unchecked")
        void everyArtifactIsZeroed() throws Exception {
            Artifact grond = artifact("Grond");
            Artifact ringil = artifact("Ringil");
            Artifact narya = artifact("Narya");
            ObjectRegistry.setArtifacts(List.of(grond, ringil, narya));

            ArtifactSetData data = newData();

            String[] mapFields = {"basePower", "baseItemLevel", "baseItemProb", "baseArtAlloc"};

            for (String name : mapFields) {
                Map<Artifact, Integer> map = (Map<Artifact, Integer>) field(ArtifactSetData.class, name).get(data);
                assertEquals(3, map.size(), name + " should cover every loaded artifact");
                assertEquals(0, map.get(grond), name + "[Grond] should start at 0");
                assertEquals(0, map.get(ringil), name + "[Ringil] should start at 0");
                assertEquals(0, map.get(narya), name + "[Narya] should start at 0");
            }
        }

        /**
         * No artifacts loaded is a valid, if degenerate, {@code z_info->a_max} — the maps come back
         * empty rather than failing, just as {@code mem_zalloc(0)} does in C.
         *
         * @throws Exception if a field cannot be reached
         */
        @Test
        @DisplayName("come back empty, not failing, when no artifacts are loaded")
        @SuppressWarnings("unchecked")
        void noArtifactsLoadedGivesEmptyMaps() throws Exception {
            ObjectRegistry.setArtifacts(List.of());
            ArtifactSetData data = newData();

            for (String name : new String[]{"basePower", "baseItemLevel", "baseItemProb", "baseArtAlloc"}) {
                Map<Artifact, Integer> map = (Map<Artifact, Integer>) field(ArtifactSetData.class, name).get(data);
                assertTrue(map.isEmpty(), name + " should be empty when the artifact table is empty");
            }
        }
    }
}
