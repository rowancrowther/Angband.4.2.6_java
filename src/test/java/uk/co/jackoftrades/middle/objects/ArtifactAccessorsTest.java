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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.Activation;
import uk.co.jackoftrades.middle.numerics.Random;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Artifact}'s data-file accessors and its copy.
 *
 * <p>The accessors are storage, and are checked because the constructor takes twenty-four arguments,
 * eight of them adjacent {@code int}s — allocation probability, the two depth bounds and the cost
 * among them. A transposed pair there would compile and load, and would show up only as an artifact
 * generated at the wrong depth some hours into a game.
 *
 * <p>{@link Artifact#copy()} is the part with judgement in it: deep where the contents are mutable,
 * shallow where the value is a shared registry entry, exactly as C's artifacts share their
 * {@code brands[]} and {@code slays[]} rows.
 *
 * @author Rowan Crowther
 */
class ArtifactAccessorsTest {

    /**
     * Builds an artifact with distinguishable values in every adjacent numeric field.
     *
     * @return the artifact
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
     * The element info is copied entry by entry rather than by sharing the entries, because
     * {@link ElementInfo} is mutable — two artifacts sharing one would be unable to differ.
     */
    @Test
    @DisplayName("element info entries are copied, not shared")
    void elementInfoEntriesCopied() {
        Artifact original = artifact();
        ElementInfo fire = new ElementInfo();
        fire.setResLevel(1);
        original.getElInfo().put(ElementEnum.ELEM_FIRE, fire);

        Artifact duplicate = original.copy();

        assertNotSame(fire, duplicate.getElInfo().get(ElementEnum.ELEM_FIRE));
        assertEquals(1, duplicate.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
    }

    /**
     * The four numbers describing where in the dungeon an artifact may appear, plus the cost beside
     * them. All five are adjacent {@code int}s at some point in the constructor.
     */
    @Nested
    @DisplayName("allocation and cost")
    class AllocationAndCost {

        /**
         * Each figure comes back from its own accessor. The values here are deliberately all
         * different, so a transposition cannot pass.
         */
        @Test
        @DisplayName("each figure comes back from its own accessor")
        void figuresRoundTrip() {
            Artifact blade = artifact();

            assertEquals(40, blade.getLevel());
            assertEquals(11, blade.getAllocProb());
            assertEquals(12, blade.getAllocMin());
            assertEquals(13, blade.getAllocMax());
            assertEquals(4500, blade.getCost());
        }

        /**
         * The depth bounds are a range, and the shallower one comes first — the ordering the
         * generator relies on when deciding whether an artifact may appear on a level.
         */
        @Test
        @DisplayName("the depth bounds are the right way round")
        void depthBoundsOrdered() {
            Artifact blade = artifact();

            assertEquals(true, blade.getAllocMin() <= blade.getAllocMax());
        }
    }

    /**
     * The descriptive fields, one of which is easy to confuse with the object type beside it.
     */
    @Nested
    @DisplayName("descriptive fields")
    class Descriptive {

        /**
         * The sub-type is the data file's text, not a number: an artifact says which kind of sword
         * it is built on by name.
         */
        @Test
        @DisplayName("the sub-type name comes back")
        void subTypeName() {
            assertEquals("long sword", artifact().getsValue());
        }

        /**
         * The damage dice are stored as their unparsed text alongside the parsed form, because the
         * description code prints what the data file said.
         */
        @Test
        @DisplayName("the dice string comes back")
        void diceString() {
            assertEquals("3d5", artifact().getDiceString());
        }
    }

    /**
     * The activation and the message shown when it fires.
     */
    @Nested
    @DisplayName("activation")
    class ActivationFields {

        /**
         * The activation itself is shared with the caller, so the artifact and whatever built it
         * refer to the same one.
         */
        @Test
        @DisplayName("the activation comes back")
        void activationComesBack() {
            Artifact blade = artifact();

            assertEquals("test activation", blade.getActivation().getName());
            assertEquals(30, blade.getActivation().getPower());
        }

        /**
         * The message is the artifact's own field, held beside the activation rather than inside it,
         * so the same activation on two artifacts can be announced differently.
         */
        @Test
        @DisplayName("the artifact carries its own activation message")
        void messageIsTheArtifacts() {
            Artifact blade = artifact();

            assertEquals("The blade glows.", blade.getActivationMessage());
            assertEquals("test activation", blade.getActivation().getName(),
                    "the activation is a separate value with its own identity");
        }
    }

    /**
     * The copy, which has to know which of its fields are shared registry entries and which are the
     * artifact's own.
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * The mutable collections are rebuilt, so a modifier added to one artifact does not appear
         * on the other.
         */
        @Test
        @DisplayName("the mutable collections are rebuilt")
        void collectionsRebuilt() {
            Artifact original = artifact();
            Artifact duplicate = original.copy();

            assertNotSame(original, duplicate);
            assertNotSame(original.getModifiers(), duplicate.getModifiers());
            assertNotSame(original.getElInfo(), duplicate.getElInfo());
            assertNotSame(original.getFlags(), duplicate.getFlags());

            original.getModifiers().put(ObjectModifier.OM_BLOWS, 2);
            assertEquals(0, duplicate.getModifiers().size(), "the copy has its own modifier map");
        }

        /**
         * The activation and the recharge dice are copied too, so the two artifacts can be
         * recharged independently.
         */
        @Test
        @DisplayName("the activation and dice are duplicated")
        void activationAndDiceDuplicated() {
            Artifact original = artifact();
            Artifact duplicate = original.copy();

            assertNotSame(original.getActivation(), duplicate.getActivation());
            assertNotSame(original.getTime(), duplicate.getTime());
            assertEquals(original.getActivation().getPower(), duplicate.getActivation().getPower());
        }

        /**
         * The brand and slay <em>sets</em> are rebuilt but their members are shared, because a brand
         * is a registry entry that every object carrying it points at. C shares the same rows.
         */
        @Test
        @DisplayName("brand and slay members are shared, not copied")
        void brandMembersShared() {
            Artifact original = artifact();
            Brand fire = new Brand("FIRE_2", "fire", "burns", null, null, 2, 3, 20);
            original.getBrands().add(fire);

            Artifact duplicate = original.copy();

            assertNotSame(original.getBrands(), duplicate.getBrands());
            assertEquals(1, duplicate.getBrands().size());
            assertSame(fire, duplicate.getBrands().iterator().next());
        }

        /**
         * The scalar fields all survive, including the four allocation figures that the accessors
         * above prove are distinct.
         */
        @Test
        @DisplayName("the scalar fields survive the copy")
        void scalarsSurvive() {
            Artifact duplicate = artifact().copy();

            assertEquals("Test Blade", duplicate.getName());
            assertEquals(TValue.TV_SWORD, duplicate.gettValue());
            assertEquals(11, duplicate.getAllocProb());
            assertEquals(12, duplicate.getAllocMin());
            assertEquals(13, duplicate.getAllocMax());
            assertEquals(4500, duplicate.getCost());
            assertEquals("3d5", duplicate.getDiceString());
        }
    }
}
