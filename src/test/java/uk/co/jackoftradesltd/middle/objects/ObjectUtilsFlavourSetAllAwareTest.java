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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#flavourSetAllAware}, the port of C's {@code flavor_set_all_aware}
 * ({@code obj-util.c:253}).
 *
 * <p>C walks every {@code k_info} entry, skips those with no name (the empty slots), and sets
 * {@code kind->aware = true} for the rest only where {@code kind->flavor} is non-null. The port
 * walks {@link ObjectRegistry#getObjectKinds()} and does the same: skip a kind whose
 * {@link ObjectKind#getName()} is {@code null}, otherwise set {@link ObjectKind#setAware(boolean)}
 * true only where {@link ObjectKind#getFlavour()} is non-null. Covered below: the ordinary
 * flavoured path, an unflavoured kind left untouched (both never-aware and already-aware, since
 * C's assignment only ever sets true, it never resets to false), the empty-slot skip taking
 * priority over a bound flavour, and several kinds walked together.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsFlavourSetAllAwareTest {

    private List<ObjectKind> savedObjectKinds;

    private static Field objectKindsField() throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField("objectKinds");
        f.setAccessible(true);
        return f;
    }

    /**
     * An {@link ObjectKind} with the given name, flavour and starting awareness. {@code name} is
     * set by reflection since the no-arg constructor leaves it {@code null} and there is no public
     * setter — the port relies on the constructors that take a name, none of which suit a bare
     * fixture like this.
     */
    private static ObjectKind objectKind(String name, Flavour flavour, boolean aware) throws Exception {
        ObjectKind kind = new ObjectKind();
        Field nameField = ObjectKind.class.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(kind, name);
        kind.setFlavour(flavour);
        kind.setAware(aware);
        return kind;
    }

    private static Flavour someFlavour() {
        return new Flavour("Ruby", ColourEnum.COLOUR_RED, 1);
    }

    @BeforeEach
    @SuppressWarnings("unchecked")
    void isolate() throws Exception {
        savedObjectKinds = (List<ObjectKind>) objectKindsField().get(null);
    }

    @AfterEach
    void restore() throws Exception {
        objectKindsField().set(null, savedObjectKinds);
    }

    /**
     * The ordinary path: a named kind carrying a flavour becomes aware, matching C's
     * {@code if (kind->flavor) kind->aware = true}.
     */
    @Nested
    @DisplayName("a flavoured kind")
    class FlavouredKind {

        @Test
        @DisplayName("becomes aware")
        void becomesAware() throws Exception {
            ObjectKind ring = objectKind("Ring", someFlavour(), false);
            ObjectRegistry.setObjectKinds(List.of(ring));

            ObjectUtils.flavourSetAllAware();

            assertTrue(ring.isAware());
        }
    }

    /**
     * The skip branch on the inner condition: no flavour means the kind is left alone, whatever
     * its starting awareness was — C's assignment is one-directional, it only ever sets
     * {@code aware} true, never resets it to false.
     */
    @Nested
    @DisplayName("an unflavoured kind")
    class UnflavouredKind {

        @Test
        @DisplayName("stays unaware")
        void staysUnaware() throws Exception {
            ObjectKind potion = objectKind("Potion", null, false);
            ObjectRegistry.setObjectKinds(List.of(potion));

            ObjectUtils.flavourSetAllAware();

            assertFalse(potion.isAware());
        }

        @Test
        @DisplayName("stays aware when it started aware")
        void staysAwareWhenAlreadyAware() throws Exception {
            ObjectKind sword = objectKind("Sword", null, true);
            ObjectRegistry.setObjectKinds(List.of(sword));

            ObjectUtils.flavourSetAllAware();

            assertTrue(sword.isAware());
        }
    }

    /**
     * The outer skip: an empty slot (no name) is never touched, even if it carries a flavour —
     * C's {@code if (!kind->name) continue;} runs before the flavour check.
     */
    @Nested
    @DisplayName("an empty slot")
    class EmptySlot {

        @Test
        @DisplayName("is skipped even when it carries a flavour")
        void skippedRegardlessOfFlavour() throws Exception {
            ObjectKind empty = objectKind(null, someFlavour(), false);
            ObjectRegistry.setObjectKinds(List.of(empty));

            ObjectUtils.flavourSetAllAware();

            assertFalse(empty.isAware());
        }
    }

    /**
     * Several kinds at once, confirming every entry in the registry is walked independently.
     */
    @Nested
    @DisplayName("multiple kinds")
    class MultipleKinds {

        @Test
        @DisplayName("each is judged on its own name and flavour")
        void eachJudgedIndependently() throws Exception {
            ObjectKind flavoured = objectKind("Ring", someFlavour(), false);
            ObjectKind unflavoured = objectKind("Sword", null, false);
            ObjectKind empty = objectKind(null, someFlavour(), false);
            ObjectRegistry.setObjectKinds(List.of(flavoured, unflavoured, empty));

            ObjectUtils.flavourSetAllAware();

            assertTrue(flavoured.isAware());
            assertFalse(unflavoured.isAware());
            assertFalse(empty.isAware());
        }
    }
}
