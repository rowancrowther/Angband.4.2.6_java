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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.ItemObject;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the four predicates {@link Square} answers about who is standing on it — {@link
 * Square#isPlayer}, {@link Square#isOccupied}, {@link Square#isFree} and {@link Square#isOpen} — and
 * the two built on top of them, {@link Square#isEmpty} and {@link Square#isArrivable}.
 *
 * <p><b>All six read one {@code int}, and that is exactly why they are worth testing.</b> C encodes
 * three states in {@code square->mon}: zero for nobody, a positive monster index, and a negative
 * value for the player. Every predicate here is a different comparison against that one field, so
 * they are trivial to write and trivial to write backwards — nothing about {@code mon != 0} looks
 * wrong until you notice the method it is in is called {@code isOpen}. Each test names the C it
 * ports so the sense can be checked against the original rather than against the Java's own
 * reading.
 *
 * <p>The pairs matter as much as the individual answers. {@link Square#isOpen} must disagree with
 * {@link Square#isOccupied} on every input, and {@link Square#isFree} must be its exact negation;
 * asserting the relationship catches an inversion that asserting the values one at a time can miss.
 *
 * <p><b>Nothing here seeds the trap registry, deliberately.</b> {@link Square#isEmpty} and
 * {@link Square#isArrivable} both veto on trap predicates, and those resolve a trap kind out of
 * {@code TerrainRegistry} — which throws when it has not been loaded. They are only safe on a
 * trapless square because each tests {@link Square#isTrap} before reaching for the registry. Running
 * these against an unseeded registry is what holds that: an eager lookup reintroduced anywhere on
 * the path fails here rather than in whatever loads a level.
 *
 * <p>Class SquareOccupancyTest coded on 260817, commented in full on 260817.
 *
 * @author Rowan Crowther
 */
class SquareOccupancyTest {

    /**
     * A terrain feature carrying the given flags and nothing else. The occupancy predicates read
     * only {@code TF_FLOOR}, through {@link Square#isFloor}, so the rest of the feature is left
     * empty rather than built up from the registry — which would need seeding for no gain.
     *
     * @param flags the terrain flags the feature carries
     * @return the constructed feature
     */
    private static Feature feature(TerrainFeatureFlags... flags) {
        Flag<TerrainFeatureFlags> set = new Flag<>(TerrainFeatureFlags.class);
        for (TerrainFeatureFlags flag : flags) {
            set.on(flag);
        }
        return new Feature(null, "test", "", null, 0, 0, set, null, "", "", "", "", "", "", "",
                new Flag<>(MonsterRaceFlag.class));
    }

    /**
     * A floor square with the given occupant.
     *
     * @param monsterIndex zero for nobody, positive for a monster, negative for the player
     * @return the constructed square
     */
    private static Square floor(int monsterIndex) {
        return new Square(feature(TerrainFeatureFlags.TF_FLOOR), 0, monsterIndex);
    }

    /**
     * A non-floor square with the given occupant, for the tests that need
     * {@link Square#isFloor} to answer false.
     *
     * @param monsterIndex zero for nobody, positive for a monster, negative for the player
     * @return the constructed square
     */
    private static Square wall(int monsterIndex) {
        return new Square(feature(), 0, monsterIndex);
    }

    /**
     * The three-way reading of the one field, which everything else is built on.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("who is on the square")
    class Occupancy {

        /**
         * C's {@code square_isplayer} is {@code square(c, grid)->mon < 0}. The player is stored as a
         * negative rather than as a flag of its own, which is what lets one field answer all three
         * questions.
         */
        @Test
        @DisplayName("a negative index is the player")
        void negativeIsPlayer() {
            assertAll(
                    () -> assertTrue(floor(-1).isPlayer()),
                    () -> assertFalse(floor(0).isPlayer()),
                    () -> assertFalse(floor(1).isPlayer()));
        }

        /**
         * C's {@code square_isoccupied} is {@code mon != 0}, so it takes in the player as well as
         * monsters. The name invites the reading "a monster is here", and the player case is what
         * separates the two.
         */
        @Test
        @DisplayName("occupied covers the player as well as monsters")
        void occupiedCoversPlayer() {
            assertAll(
                    () -> assertTrue(floor(1).isOccupied()),
                    () -> assertTrue(floor(-1).isOccupied()),
                    () -> assertFalse(floor(0).isOccupied()));
        }

        /**
         * {@link Square#isFree} must be the exact negation of {@link Square#isOccupied} across all
         * three states. Asserting the relationship rather than three separate values is what catches
         * one of the pair being changed without the other.
         */
        @Test
        @DisplayName("free is the negation of occupied")
        void freeIsNotOccupied() {
            for (int mon : new int[]{-1, 0, 1, 7}) {
                Square square = floor(mon);
                assertFalse(square.isFree() == square.isOccupied(),
                        "isFree and isOccupied agreed for mon = " + mon);
            }
        }
    }

    /**
     * The predicates that combine occupancy with terrain.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("open, empty and arrivable")
    class Derived {

        /**
         * C's {@code square_isopen} is {@code square_isfloor(c, grid) && !square(c, grid)->mon} —
         * floor <em>and nobody on it</em>. The negation is the whole content of the method, and
         * dropping it turns "open" into "occupied floor", which is not a state anything wants.
         */
        @Test
        @DisplayName("open means floor with nobody on it")
        void openIsEmptyFloor() {
            assertAll(
                    () -> assertTrue(floor(0).isOpen()),
                    () -> assertFalse(floor(1).isOpen()),
                    () -> assertFalse(floor(-1).isOpen()));
        }

        /**
         * The other half of {@code square_isopen}: a clear square that is not floor is not open. Both
         * conjuncts need a test or an implementation could satisfy one and be judged correct.
         */
        @Test
        @DisplayName("a clear non-floor square is not open")
        void nonFloorIsNotOpen() {
            assertFalse(wall(0).isOpen());
        }

        /**
         * {@link Square#isOpen} and {@link Square#isOccupied} must never agree on a floor square.
         * They read the same field with opposite senses, so an inversion in either makes them equal —
         * and an inversion in {@code isOpen} in particular reads perfectly naturally, since
         * {@code mon != 0} is the correct body for the method next door.
         */
        @Test
        @DisplayName("open and occupied never agree on a floor square")
        void openAndOccupiedDisagree() {
            for (int mon : new int[]{-1, 0, 1, 7}) {
                Square square = floor(mon);
                assertFalse(square.isOpen() == square.isOccupied(),
                        "isOpen and isOccupied agreed for mon = " + mon);
            }
        }

        /**
         * C's {@code square_isempty} is {@code square_isopen(c, grid) && !square_object(c, grid)},
         * with player traps and webs vetoing first. Being built on {@code isOpen}, it inherits any
         * inversion there, so it is worth confirming that an occupied floor is not empty even with
         * nothing lying on it.
         */
        @Test
        @DisplayName("empty means open and carrying no objects")
        void emptyIsOpenAndObjectless() {
            Square clear = floor(0);
            Square withObject = floor(0);
            withObject.getObjectPile().insert(new ItemObject());

            assertAll(
                    () -> assertTrue(clear.isEmpty()),
                    () -> assertFalse(withObject.isEmpty()),
                    () -> assertFalse(floor(1).isEmpty()));
        }

        /**
         * C's {@code square_isarrivable} rejects on {@code square(c, grid)->mon} directly, so the
         * player's own square is not somewhere that can be arrived at either. Floor qualifies;
         * anything that is neither floor nor stairs does not.
         */
        @Test
        @DisplayName("arrivable needs clear floor or stairs")
        void arrivableNeedsClearFloor() {
            assertAll(
                    () -> assertTrue(floor(0).isArrivable()),
                    () -> assertFalse(floor(1).isArrivable()),
                    () -> assertFalse(floor(-1).isArrivable()),
                    () -> assertFalse(wall(0).isArrivable()));
        }
    }
}
