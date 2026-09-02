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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Curse}'s definition-side accessors — the ones describing what a curse may attach to
 * and what it will not sit alongside, rather than what it does to an object once attached.
 *
 * <p>These carry the two-pass shape of the curse loader. {@code curse.txt} names conflicting curses
 * as strings, because a curse may name one defined later in the file; the assembler's second pass
 * resolves those names into curses once every curse exists. So a curse is legitimately half-built
 * for a while, and the tests below cover both states.
 *
 * @author Rowan Crowther
 */
class CurseDefinitionTest {

    /**
     * An object base, which is what a curse's allowed-types list holds.
     *
     * @param tval the object type this base describes
     * @param name the base's name
     * @return the base
     */
    private static ObjectBase base(TValue tval, String name) {
        return new ObjectBase(tval, name, ColourEnum.COLOUR_WHITE,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, 40);
    }

    /**
     * A curse with the given allowed bases and conflict names, everything else empty.
     *
     * @param bases         the object bases this curse may attach to
     * @param conflictNames the names of the curses it will not sit alongside
     * @return the curse
     */
    private static Curse curse(List<ObjectBase> bases, List<String> conflictNames) {
        return new Curse("test curse", bases, 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, conflictNames, new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * The list of object bases a curse may attach to, and the test that reads it.
     */
    @Nested
    @DisplayName("allowed object bases")
    class AllowedBases {

        /**
         * The list comes back as it was given, in order.
         */
        @Test
        @DisplayName("the bases come back as given")
        void basesRoundTrip() {
            ObjectBase sword = base(TValue.TV_SWORD, "sword");
            ObjectBase shield = base(TValue.TV_SHIELD, "shield");

            Curse weaponCurse = curse(List.of(sword, shield), List.of());

            assertEquals(List.of(sword, shield), weaponCurse.getObjectBases());
        }

        /**
         * {@code canAfflict} is membership of that list, by identity — the bases are registry
         * entries every object of that type points at, so two bases with the same name are still
         * different bases.
         */
        @Test
        @DisplayName("a curse afflicts exactly the bases it lists")
        void canAfflictIsMembership() {
            ObjectBase sword = base(TValue.TV_SWORD, "sword");
            ObjectBase shield = base(TValue.TV_SHIELD, "shield");
            ObjectBase otherSword = base(TValue.TV_SWORD, "sword");

            Curse swordCurse = curse(List.of(sword), List.of());

            assertTrue(swordCurse.canAfflict(sword));
            assertFalse(swordCurse.canAfflict(shield));
            assertFalse(swordCurse.canAfflict(otherSword),
                    "a different base instance is a different base, even with the same name");
        }

        /**
         * A curse listing no bases afflicts nothing, rather than everything.
         */
        @Test
        @DisplayName("a curse with no bases afflicts nothing")
        void emptyBasesAfflictNothing() {
            assertFalse(curse(List.of(), List.of()).canAfflict(base(TValue.TV_SWORD, "sword")));
        }
    }

    /**
     * The conflict list, in both its unresolved and resolved states.
     */
    @Nested
    @DisplayName("conflicts")
    class Conflicts {

        /**
         * As loaded, a curse holds the <em>names</em> of what it conflicts with and no resolved
         * curses at all — the second pass has not run yet.
         */
        @Test
        @DisplayName("a freshly loaded curse holds names and no resolved curses")
        void namesBeforeResolution() {
            Curse siren = curse(List.of(), List.of("teleportation", "vulnerability"));

            assertEquals(List.of("teleportation", "vulnerability"), siren.getConflictNames());
            assertNull(siren.getConflict(),
                    "nothing is resolved until the assembler's second pass runs");
        }

        /**
         * The second pass stores the resolved curses, which is what the rest of the game reads.
         */
        @Test
        @DisplayName("the second pass stores the resolved curses")
        void resolutionStoresCurses() {
            Curse siren = curse(List.of(), List.of("teleportation"));
            Curse teleportation = curse(List.of(), List.of());

            siren.setConflict(List.of(teleportation));

            assertEquals(1, siren.getConflict().size());
            assertSame(teleportation, siren.getConflict().get(0));
        }

        /**
         * Resolving does not consume the names. They stay, which is what lets the loader be re-run
         * without re-parsing the file, and lets a diagnostic report what a curse asked for as well
         * as what it got.
         */
        @Test
        @DisplayName("resolving leaves the names in place")
        void namesSurviveResolution() {
            Curse siren = curse(List.of(), List.of("teleportation"));
            siren.setConflict(List.of(curse(List.of(), List.of())));

            assertEquals(List.of("teleportation"), siren.getConflictNames());
        }

        /**
         * The conflicting <em>flags</em> are a separate mechanism from the conflicting curses: a
         * curse can refuse to sit on an object carrying a given flag as well as refusing to sit
         * beside a named curse.
         */
        @Test
        @DisplayName("conflict flags are separate from conflict names")
        void conflictFlagsAreSeparate() {
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.set(List.of(ObjectFlag.OF_AGGRAVATE));

            Curse quiet = new Curse("quiet", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                    Map.of(), Map.of(), 0, 0, 0, new ArrayList<>(), flags, "", "");

            assertTrue(quiet.getConflictFlags().has(ObjectFlag.OF_AGGRAVATE));
            assertTrue(quiet.getConflictNames().isEmpty(),
                    "a flag conflict is not a named-curse conflict");
        }
    }
}
