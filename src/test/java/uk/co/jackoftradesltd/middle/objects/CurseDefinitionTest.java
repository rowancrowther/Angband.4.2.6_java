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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
     * A curse with the given allowed tvals and conflict names, everything else empty.
     *
     * @param tvals         the tvals this curse may attach to, C's {@code curse->poss}
     * @param conflictNames the names of the curses it will not sit alongside
     * @return the curse
     */
    private static Curse curse(List<TValue> tvals, List<String> conflictNames) {
        return CurseFixture.curse("test curse", tvals, 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, conflictNames, new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * The tvals a curse may attach to, and the test that reads them. C keeps them as
     * {@code curse->poss[tval]} and every reader ({@code obj-make.c}, {@code obj-randart.c},
     * {@code effect-handler-general.c}) tests {@code poss[obj->tval]}, so the answer depends on the
     * object's tval alone.
     */
    @Nested
    @DisplayName("allowed tvals")
    class AllowedBases {

        /**
         * The list comes back as it was given, in order.
         */
        @Test
        @DisplayName("the tvals come back as given")
        void tvalsRoundTrip() {
            Curse weaponCurse = curse(List.of(TValue.TV_SWORD, TValue.TV_SHIELD), List.of());

            assertEquals(List.of(TValue.TV_SWORD, TValue.TV_SHIELD), weaponCurse.getTValues());
        }

        /**
         * {@code canAfflict} answers by the base's tval, as C's {@code poss[obj->tval]} does, so a
         * second base instance with the same tval is afflicted too.
         */
        @Test
        @DisplayName("a curse afflicts every base whose tval it lists")
        void canAfflictIsByTval() {
            ObjectBase sword = base(TValue.TV_SWORD, "sword");
            ObjectBase shield = base(TValue.TV_SHIELD, "shield");
            ObjectBase otherSword = base(TValue.TV_SWORD, "sword");

            Curse swordCurse = curse(List.of(TValue.TV_SWORD), List.of());

            assertTrue(swordCurse.canAfflict(sword));
            assertFalse(swordCurse.canAfflict(shield));
            assertTrue(swordCurse.canAfflict(otherSword),
                    "C tests poss[obj->tval], so any base of that tval matches");
        }

        /**
         * A curse listing no tvals afflicts nothing, rather than everything, as C's zero-filled
         * {@code poss} array does.
         */
        @Test
        @DisplayName("a curse with no tvals afflicts nothing")
        void emptyTvalsAfflictNothing() {
            assertFalse(curse(List.of(), List.of()).canAfflict(base(TValue.TV_SWORD, "sword")));
        }

        /**
         * C's {@code poss[]} is indexed by tval, so a curse listing one tval afflicts a base of that
         * tval and no other. Checked for every tval against every tval.
         */
        @Test
        @DisplayName("each tval afflicts its own base and no other, for every tval")
        void everyTvalAgainstEveryTval() {
            for (TValue listed : TValue.values()) {
                Curse c = curse(List.of(listed), List.of());
                for (TValue asked : TValue.values()) {
                    assertEquals(listed == asked, c.canAfflict(base(asked, asked.getName())),
                            listed + " asked about " + asked);
                }
            }
        }

        /**
         * Two {@code type:cloak} lines set {@code poss[TV_CLOAK]} twice in C; the list keeps both
         * entries, and the answer is the same.
         */
        @Test
        @DisplayName("a repeated tval is kept twice and still afflicts")
        void repeatedTvalIsKeptAndStillAfflicts() {
            Curse c = curse(List.of(TValue.TV_CLOAK, TValue.TV_CLOAK), List.of());

            assertEquals(List.of(TValue.TV_CLOAK, TValue.TV_CLOAK), c.getTValues());
            assertTrue(c.canAfflict(base(TValue.TV_CLOAK, "cloak")));
            assertFalse(c.canAfflict(base(TValue.TV_BOOTS, "boots")));
        }

        /**
         * {@code tval_find_idx} accepts {@code none} and {@code other book}, which have no object
         * base; C sets the flag and it never matches a real object.
         */
        @Test
        @DisplayName("a tval with no object base is held and matches no other base")
        void baselessTvalIsHeld() {
            Curse c = curse(List.of(TValue.TV_NONE, TValue.TV_OTHER_BOOK), List.of());

            assertEquals(List.of(TValue.TV_NONE, TValue.TV_OTHER_BOOK), c.getTValues());
            assertFalse(c.canAfflict(base(TValue.TV_MAGIC_BOOK, "magic book")));
        }

        /**
         * The getter hands out the curse's own list, so a change to it changes the answer.
         */
        @Test
        @DisplayName("getTValues is the live list, not a copy")
        void getTValuesIsLive() {
            List<TValue> tvals = new ArrayList<>(List.of(TValue.TV_SWORD));
            Curse c = curse(tvals, List.of());

            c.getTValues().add(TValue.TV_SHIELD);

            assertSame(tvals, c.getTValues());
            assertTrue(c.canAfflict(base(TValue.TV_SHIELD, "shield")));
        }

        /**
         * C always has an object to index by; a {@code null} base here is a caller error.
         */
        @Test
        @DisplayName("a null base throws")
        void nullBaseThrows() {
            Curse c = curse(List.of(TValue.TV_SWORD), List.of());

            assertThrows(NullPointerException.class, () -> c.canAfflict(null));
        }

        /**
         * C's {@code parse_curse_name} always allocates {@code poss}; a curse built with a
         * {@code null} list cannot answer.
         */
        @Test
        @DisplayName("a curse built with a null tval list throws when asked")
        void nullListThrowsWhenAsked() {
            Curse c = curse(null, List.of());

            assertThrows(NullPointerException.class, () -> c.canAfflict(base(TValue.TV_SWORD, "sword")));
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

            Curse quiet = CurseFixture.curse("quiet", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                    Map.of(), Map.of(), 0, 0, 0, new ArrayList<>(), flags, "", "", 0);

            assertTrue(quiet.getConflictFlags().has(ObjectFlag.OF_AGGRAVATE));
            assertTrue(quiet.getConflictNames().isEmpty(),
                    "a flag conflict is not a named-curse conflict");
        }
    }
}
