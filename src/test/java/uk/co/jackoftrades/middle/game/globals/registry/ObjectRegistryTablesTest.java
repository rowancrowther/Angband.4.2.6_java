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

package uk.co.jackoftrades.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.Artifact;
import uk.co.jackoftrades.middle.objects.EgoItem;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.ObjectKind;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectRegistry}'s three loaded object tables — items, egos and artifacts — and the
 * count fields the loaders leave behind.
 *
 * <p>The registry is static, so every test here saves what it found and puts it back; a table left
 * seeded would change the answer for any test class running afterwards.
 *
 * <p>Two things are worth asserting rather than assuming. The getters hand back <em>unmodifiable</em>
 * views while the setters take the caller's list and keep it, so the tables are read-only from
 * outside and writable only through the loader — a distinction that a later defensive copy in the
 * setter would quietly reverse, decoupling the registry from the loader that filled it. And
 * {@link ObjectRegistry#setEgoItems} maintains a count beside the list while the other two setters
 * do not, which is the sort of asymmetry that is invisible until a count goes stale.
 *
 * @author Rowan Crowther
 */
class ObjectRegistryTablesTest {

    /**
     * The item table as it was before each test.
     */
    private List<ItemObject> savedItems;

    /**
     * The ego table as it was before each test.
     */
    private List<EgoItem> savedEgos;

    /**
     * The artifact table as it was before each test.
     */
    private List<Artifact> savedArtifacts;

    /**
     * The ego count as it was before each test, since {@code setEgoItems} writes it.
     */
    private int savedEgoMax;

    /**
     * One of the registry's private table fields, made accessible.
     *
     * @param name the field's name
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field field(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    /**
     * Writes one of the registry's private count fields.
     *
     * @param name  the field's name
     * @param value the value to store
     * @throws Exception if the field cannot be reached
     */
    private static void setInt(String name, int value) throws Exception {
        Field field = ObjectRegistry.class.getDeclaredField(name);
        field.setAccessible(true);
        field.setInt(null, value);
    }

    /**
     * A minimal ego template.
     *
     * @param name the ego's name
     * @return the ego
     */
    private static EgoItem ego(String name) {
        return new EgoItem(name, "a test ego", 1, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectKindFlag.class),
                new HashMap<>(), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(),
                0, 0, 0, 0, new ArrayList<ObjectKind>(),
                null, null, null, 0, 0, 0, null, null, false);
    }

    /**
     * A minimal artifact.
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
     * Saves the three tables and the ego count.
     *
     * <p>Read through the fields rather than the getters, because the getters wrap the list in an
     * unmodifiable view and that throws when the table has never been loaded — which is the state a
     * unit test starts in.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    @SuppressWarnings("unchecked")
    void saveTables() throws Exception {
        savedItems = (List<ItemObject>) field("itemObjects").get(null);
        savedEgos = (List<EgoItem>) field("egoItems").get(null);
        savedArtifacts = (List<Artifact>) field("artifacts").get(null);
        savedEgoMax = ObjectRegistry.getEgoItemKindMax();
    }

    /**
     * Puts all four back, including a table that was null before the test ran.
     *
     * @throws Exception if a field cannot be reached
     */
    @AfterEach
    void restoreTables() throws Exception {
        field("itemObjects").set(null, savedItems);
        field("egoItems").set(null, savedEgos);
        field("artifacts").set(null, savedArtifacts);
        setInt("egoItemKindMax", savedEgoMax);
    }

    /**
     * The object-base table, which is read-only from outside for the same reason as the other three.
     *
     * @throws Exception if the table field cannot be reached
     */
    @Test
    @DisplayName("the object bases are a read-only view")
    void objectBasesAreReadOnly() throws Exception {
        Object savedBases = field("objectBases").get(null);
        try {
            field("objectBases").set(null, new ArrayList<>());

            assertThrows(UnsupportedOperationException.class,
                    () -> ObjectRegistry.getObjectBases().add(null));
        } finally {
            field("objectBases").set(null, savedBases);
        }
    }

    /**
     * A table that has never been loaded is not an empty one: the getter wraps a null field and
     * throws. Worth pinning, because it means the registry has to be filled before anything reads
     * it, and a caller cannot treat "nothing loaded" as "nothing there".
     *
     * @throws Exception if a table field cannot be reached
     */
    @Test
    @DisplayName("an unloaded table throws rather than reading as empty")
    void unloadedTableThrows() throws Exception {
        field("itemObjects").set(null, null);

        assertThrows(NullPointerException.class, ObjectRegistry::getItemObjects);
    }

    /**
     * The three tables, which behave alike on the way in and out.
     */
    @Nested
    @DisplayName("the loaded tables")
    class Tables {

        /**
         * Each table stores what the loader gave it and hands it back in order.
         */
        @Test
        @DisplayName("each table round-trips in order")
        void tablesRoundTrip() {
            ItemObject first = new ItemObject();
            ItemObject second = new ItemObject();
            ObjectRegistry.setItemObjects(new ArrayList<>(List.of(first, second)));

            assertEquals(2, ObjectRegistry.getItemObjects().size());
            assertSame(first, ObjectRegistry.getItemObjects().get(0));
            assertSame(second, ObjectRegistry.getItemObjects().get(1));

            EgoItem sharpness = ego("of Sharpness");
            ObjectRegistry.setEgoItems(new ArrayList<>(List.of(sharpness)));
            assertSame(sharpness, ObjectRegistry.getEgoItems().get(0));

            Artifact ringil = artifact("Ringil");
            ObjectRegistry.setArtifacts(new ArrayList<>(List.of(ringil)));
            assertSame(ringil, ObjectRegistry.getArtifacts().get(0));
        }

        /**
         * The getters are read-only views. Handing out a writable list would let any caller add to a
         * loaded table, which is the sort of thing that shows up as an object nobody can account
         * for.
         */
        @Test
        @DisplayName("the getters hand out read-only views")
        void gettersAreReadOnly() {
            ObjectRegistry.setItemObjects(new ArrayList<>());
            ObjectRegistry.setEgoItems(new ArrayList<>());
            ObjectRegistry.setArtifacts(new ArrayList<>());

            assertThrows(UnsupportedOperationException.class,
                    () -> ObjectRegistry.getItemObjects().add(new ItemObject()));
            assertThrows(UnsupportedOperationException.class,
                    () -> ObjectRegistry.getEgoItems().add(ego("of Nothing")));
            assertThrows(UnsupportedOperationException.class,
                    () -> ObjectRegistry.getArtifacts().add(artifact("Nothing")));
        }

        /**
         * The view is of the live table, not a snapshot: the setter keeps the caller's list, so a
         * later write through that list is visible through the getter. That is what couples the
         * registry to the loader that fills it, and is the reason the getter has to wrap rather than
         * copy.
         */
        @Test
        @DisplayName("the view follows the live table")
        void viewFollowsTheTable() {
            List<ItemObject> live = new ArrayList<>();
            ObjectRegistry.setItemObjects(live);

            assertTrue(ObjectRegistry.getItemObjects().isEmpty());

            live.add(new ItemObject());

            assertEquals(1, ObjectRegistry.getItemObjects().size(),
                    "the registry kept the loader's list rather than copying it");
        }

        /**
         * Setting a table replaces it outright rather than adding to it, so a re-load starts from
         * what it was given.
         */
        @Test
        @DisplayName("setting a table replaces it")
        void settingReplaces() {
            ObjectRegistry.setItemObjects(new ArrayList<>(List.of(new ItemObject())));
            List<ItemObject> replacement = new ArrayList<>();
            ObjectRegistry.setItemObjects(replacement);

            assertTrue(ObjectRegistry.getItemObjects().isEmpty());
            assertNotSame(savedItems, replacement);
        }
    }

    /**
     * The count fields, which the loaders write beside the tables.
     */
    @Nested
    @DisplayName("count fields")
    class Counts {

        /**
         * The ego setter maintains its count, so the two cannot fall out of step.
         */
        @Test
        @DisplayName("setting the egos updates their count")
        void egoCountFollowsTheTable() {
            ObjectRegistry.setEgoItems(new ArrayList<>(List.of(ego("one"), ego("two"))));

            assertEquals(2, ObjectRegistry.getEgoItemKindMax());

            ObjectRegistry.setEgoItems(new ArrayList<>());

            assertEquals(0, ObjectRegistry.getEgoItemKindMax());
        }

        /**
         * The artifact and item setters do not, which is the asymmetry worth knowing about: their
         * counts are written elsewhere in the load, so setting the table alone leaves them as they
         * were.
         *
         * @throws Exception if a count field cannot be reached
         */
        @Test
        @DisplayName("the artifact count is not maintained by its setter")
        void artifactCountIsNotMaintained() throws Exception {
            setInt("artifactKindMax", 7);

            ObjectRegistry.setArtifacts(new ArrayList<>(List.of(artifact("Ringil"))));

            assertEquals(7, ObjectRegistry.getArtifactKindMax(),
                    "the setter stores the table and leaves the count to the loader");
        }

        /**
         * The remaining counts are plain reads of what the load recorded. Each is checked against
         * its own field, since they are five same-typed statics whose names differ by a word.
         *
         * @throws Exception if a count field cannot be reached
         */
        @Test
        @DisplayName("each count reads its own field")
        void countsReadTheirOwnFields() throws Exception {
            setInt("artifactKindMax", 11);
            setInt("randartActivationsMax", 12);
            setInt("objectPowerCalculationMax", 13);
            setInt("objectPropertyMax", 14);
            setInt("objectsInObject_txt", 15);

            assertEquals(11, ObjectRegistry.getArtifactKindMax());
            assertEquals(12, ObjectRegistry.getRandartActivationsMax());
            assertEquals(13, ObjectRegistry.getObjectsPowerCalculationMax());
            assertEquals(14, ObjectRegistry.getObjectsPropertyMax());
            assertEquals(15, ObjectRegistry.getObjectsInObject_txt());
        }
    }
}
