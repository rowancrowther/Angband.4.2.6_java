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
import uk.co.jackoftrades.channel.colour.ColourEnum;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.IgnoreFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the parts of {@link ObjectKind} that do work rather than assign — the curse copying in the
 * long constructor, the special-artifact marker, and the two ignore settings.
 *
 * <p><b>The three have one thing in common: each is a place where a kind and the items made from it
 * could be confused for one another.</b> A kind is a template that many items share, so anything it
 * hands out by reference is shared by every item of that type, and anything it fails to record has
 * to be re-derived by whoever asks. Curses are the reference case, the special-artifact flag the
 * recording case, and the ignore settings the case where the player's decision has to live on the
 * kind rather than on any one item — ignoring a potion means ignoring all of them.
 *
 * <p>Class ObjectKindTest coded on 260817, commented in full on 260817.
 *
 * @author Rowan Crowther
 */
class ObjectKindTest {

    /**
     * Reads a kind's curse map by reflection.
     *
     * <p>{@link ObjectKind} exposes no accessor for it — the field is written by the constructors
     * and read by nothing yet — so this is the only way to see what the copy loop produced. If a
     * getter is added later, these tests should move onto it.
     *
     * @param kind the kind to read
     * @return the kind's curse map
     */
    @SuppressWarnings("unchecked")
    private static Map<Curse, CurseData> cursesOf(ObjectKind kind) {
        try {
            java.lang.reflect.Field field = ObjectKind.class.getDeclaredField("curses");
            field.setAccessible(true);
            return (Map<Curse, CurseData>) field.get(kind);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("ObjectKind.curses is no longer readable by reflection", e);
        }
    }

    /**
     * A kind built through the long constructor with the given curses and everything else empty.
     *
     * @param curses the curse map to pass in
     * @return the constructed kind
     */
    private static ObjectKind kindWith(Map<Curse, CurseData> curses) {
        return new ObjectKind("& Test Sword~", "", base(), 0, null, null, null, null, 0, null,
                1, 1, 30, 0, new Flag<>(ObjectFlag.class), new Flag<>(ObjectKindFlag.class),
                new HashMap<>(), new HashMap<>(), new HashSet<>(), new HashSet<>(), curses,
                null, 0, 0, 0, 0, new ArrayList<>(), new ArrayList<>(), "", "", "0", null, 0,
                null, null, null, null, false, false, new Flag<>(IgnoreFlag.class),
                false, TValue.TV_SWORD);
    }

    /**
     * A kind synthesised to back a special artifact.
     *
     * @return the constructed kind
     */
    private static ObjectKind artifactKind() {
        Artifact artifact = new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0",
                0, 0, new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(),
                Map.of(), 7, 0, 0, 0, null, null, null);
        return new ObjectKind(artifact, "Test", base());
    }

    /**
     * A minimal object base, which the kind constructors read for kind-flags and element info.
     *
     * @return the constructed base
     */
    private static ObjectBase base() {
        return new ObjectBase(TValue.TV_SWORD, "sword", ColourEnum.COLOUR_WHITE,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, 40);
    }

    /**
     * A minimal curse definition, distinct from every other by identity.
     *
     * @param name the curse's name
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return new Curse(name, List.of(), 0, null, List.of(), Map.of(), Map.of(), 0, 0, 0,
                List.of(), List.of(), "", "");
    }

    /**
     * The long constructor copies its curse map rather than storing it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("curse copying")
    class Curses {

        /**
         * The kind must not share {@link CurseData} instances with whatever built it. The data is
         * mutable and an item's curse ticks its own timeout down, so a shared instance would let one
         * cursed sword count down the template every other sword is made from — and the drift would
         * show up as curses firing early on items that had never been worn.
         */
        @Test
        @DisplayName("curse data is copied, not shared")
        void curseDataIsCopied() {
            Curse siren = curse("siren");
            CurseData source = new CurseData(3, 9);
            Map<Curse, CurseData> curses = new HashMap<>();
            curses.put(siren, source);

            ObjectKind kind = kindWith(curses);
            CurseData stored = cursesOf(kind).get(siren);

            assertAll(
                    () -> assertNotSame(source, stored),
                    () -> assertEquals(3, stored.getPower()),
                    () -> assertEquals(9, stored.getTimeout()));

            stored.decrementTimeout();
            assertEquals(9, source.getTimeout());
        }

        /**
         * The map itself is copied too, so a curse added to the argument afterwards does not appear
         * on the kind. Copying the values but keeping the caller's map would leave the kind's curse
         * list open to later change from outside.
         */
        @Test
        @DisplayName("the curse map is copied, not adopted")
        void curseMapIsCopied() {
            Map<Curse, CurseData> curses = new HashMap<>();
            curses.put(curse("siren"), new CurseData(1, 0));

            ObjectKind kind = kindWith(curses);
            curses.put(curse("teleportation"), new CurseData(1, 0));

            assertEquals(1, cursesOf(kind).size());
        }
    }

    /**
     * Which kinds are special artifacts.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the special-artifact marker")
    class SpecialArtifact {

        /**
         * C asks the question by position — {@code kidx >= z_info->ordinary_kind_max} — because it
         * appends the synthesised artifact kinds after the ordinary ones. The port records it
         * instead, which means every constructor has to set it, and the artifact one is the only
         * one that sets it true.
         *
         * <p>What turns on it is not small: {@code knowObject} makes the player aware of a
         * non-jewellery special artifact outright rather than waiting for its runes to be read. A
         * flag left false everywhere would leave that branch unreachable, and nothing else would
         * fail.
         */
        @Test
        @DisplayName("only a kind built for an artifact is a special artifact kind")
        void onlyArtifactKindsAreSpecial() {
            assertAll(
                    () -> assertFalse(new ObjectKind().isSpecialArtifactKind()),
                    () -> assertFalse(kindWith(new HashMap<>()).isSpecialArtifactKind()),
                    () -> assertTrue(artifactKind().isSpecialArtifactKind()));
        }

        /**
         * The artifact constructor gives its kind a red {@code '*'} and the {@code KF_INSTA_ART}
         * flag, which is what marks the kind as being its own artifact rather than a template.
         */
        @Test
        @DisplayName("an artifact kind is marked as an instant artifact")
        void artifactKindIsInstaArt() {
            assertTrue(artifactKind().getKindFlags().has(ObjectKindFlag.KF_INSTA_ART));
        }
    }

    /**
     * The player's two ignore decisions.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("ignore settings")
    class Ignore {

        /**
         * The two settings are independent, and have to be: a player can be ignoring unidentified
         * potions while wanting to see the identified ones, which is the whole reason there are two
         * flags rather than one. Setting either must leave the other alone.
         */
        @Test
        @DisplayName("the aware and unaware settings are independent")
        void settingsAreIndependent() {
            ObjectKind kind = new ObjectKind();

            kind.setIgnoredUnaware(true);

            assertAll(
                    () -> assertTrue(kind.isIgnoredUnaware()),
                    () -> assertFalse(kind.isIgnoredAware()));

            kind.setIgnoredAware(true);
            kind.setIgnoredUnaware(false);

            assertAll(
                    () -> assertFalse(kind.isIgnoredUnaware()),
                    () -> assertTrue(kind.isIgnoredAware()));
        }

        /**
         * C's macro only ever switches the bit on; the port takes a boolean and so can clear it,
         * which the player's ignore menu will want. Pinning the clearing path means it stays working
         * until there is something to use it.
         */
        @Test
        @DisplayName("both settings can be cleared as well as set")
        void settingsCanBeCleared() {
            ObjectKind kind = new ObjectKind();
            kind.setIgnoredAware(true);
            kind.setIgnoredUnaware(true);

            kind.setIgnoredAware(false);
            kind.setIgnoredUnaware(false);

            assertAll(
                    () -> assertFalse(kind.isIgnoredAware()),
                    () -> assertFalse(kind.isIgnoredUnaware()));
        }

        /**
         * Every constructor has to leave a usable ignore set behind it, including the artifact one,
         * which builds its collections by hand rather than sharing the others' code. A kind whose
         * flag set was never created answers the question with a null pointer instead of a boolean —
         * and the caller is {@code Player.flavourAware}, which asks it of whatever kind it is handed
         * rather than of a kind it built.
         */
        @Test
        @DisplayName("every constructor leaves the ignore settings answerable")
        void everyConstructorInitialisesIgnore() {
            assertAll(
                    () -> assertFalse(new ObjectKind().isIgnoredUnaware()),
                    () -> assertFalse(kindWith(new HashMap<>()).isIgnoredUnaware()),
                    () -> assertFalse(artifactKind().isIgnoredUnaware()),
                    () -> assertFalse(artifactKind().isIgnoredAware()));
        }
    }
}
