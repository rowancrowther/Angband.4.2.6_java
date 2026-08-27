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

package uk.co.jackoftrades.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.ObjectNotice;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Chunk}'s object disposal — {@code objectDelete}, {@code delistObject} and
 * {@code squareExciseObject}, the port of C's {@code object_delete}, {@code delist_object} and
 * {@code square_excise_object} ({@code obj-pile.c:308}, {@code cave.c}).
 *
 * <p>{@code objectDelete} has two outcomes and choosing between them is its whole job. An object the
 * player still remembers is <b>orphaned</b>: stripped of its position and marked as imaginary, but
 * left in existence, because the player's memory of it must still point somewhere. An object with no
 * such counterpart is deleted outright. C makes that choice by testing whether both chunks list the
 * object at its index, and the port tests both lists the same way.
 *
 * <p>The order matters as much as the choice: C tests the lists <em>before</em> clearing them, so a
 * removal at the top of the method would make the orphan branch unreachable. That is asserted here
 * rather than left to inspection.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ChunkObjectDisposalTest {

    /**
     * The real level.
     */
    private Chunk level;

    /**
     * The player's remembered copy of it.
     */
    private Chunk known;

    /**
     * The player holding the remembered level.
     */
    private Player player;

    /**
     * The chunk the game held before each test.
     */
    private Chunk savedCave;

    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * Adds an object to a chunk's list, standing in for the generation code.
     *
     * @param chunk the chunk to list it in
     * @param item  the object to list
     * @throws Exception if the list cannot be reached
     */
    @SuppressWarnings("unchecked")
    private static void list(Chunk chunk, ItemObject item) throws Exception {
        Field field = Chunk.class.getDeclaredField("objects");
        field.setAccessible(true);
        ((List<ItemObject>) field.get(chunk)).add(item);
    }

    /**
     * An object with a known half attached.
     *
     * @return the object
     * @throws Exception if a field cannot be reached
     */
    private static ItemObject knownObject() throws Exception {
        ItemObject item = new ItemObject();
        ItemObject counterpart = new ItemObject();
        Field field = ItemObject.class.getDeclaredField("notice");
        field.setAccessible(true);
        field.set(counterpart, new uk.co.jackoftrades.channel.utils.Flag<>(ObjectNotice.class));
        Field knownField = ItemObject.class.getDeclaredField("known");
        knownField.setAccessible(true);
        knownField.set(item, counterpart);
        return item;
    }

    /**
     * Wires a real level, a remembered one, and a player holding the second.
     */
    @BeforeEach
    void wireCaves() {
        savedCave = GameState.getCave();
        savedPlayer = GameState.getPlayer();

        player = new Player();
        level = new Chunk("level", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);
        known = new Chunk("known", 0, 0, 0, 0, 0, false, 6, 6, 0, 4, 2, 0, 0, 0, player);

        GameState.setCave(level);
        level.setCurrentLevel(level);
        known.setCurrentLevel(level);
        player.setCave(known);
        GameState.setPlayer(player);
    }

    /**
     * Puts the game's cave and player back.
     */
    @AfterEach
    void restoreGame() {
        GameState.setCave(savedCave);
        GameState.setPlayer(savedPlayer);
    }

    /**
     * The current-level pointer, which several accessors compare themselves against.
     */
    @Test
    @DisplayName("a chunk knows which level is current")
    void currentLevelIsRecorded() {
        assertSame(level, level.getCurrentLevel(), "the real level points at itself");
        assertSame(level, known.getCurrentLevel(), "and the remembered one points at the real one");
    }

    /**
     * The two outcomes of a delete.
     */
    @Nested
    @DisplayName("objectDelete")
    class Delete {

        /**
         * An object both chunks list is orphaned rather than deleted: it keeps its place in the
         * lists, loses its position on the level, and is marked as existing only in the player's
         * memory.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("an object the player remembers is orphaned, not deleted")
        void rememberedObjectIsOrphaned() throws Exception {
            ItemObject item = knownObject();
            list(level, item);
            list(known, item);
            item.setGrid(Loc.row(2).col(3));

            level.objectDelete(known, item);

            assertTrue(level.getObjects().contains(item), "the object still exists");
            assertTrue(item.getGrid().isZero(), "but has no position on the level");
            assertTrue(item.getKnown().getNotice().has(ObjectNotice.OBJ_NOTICE_IMAGINED),
                    "and the player's memory of it is marked imaginary");
        }

        /**
         * An object the player does not remember is deleted outright — removed from the level's
         * list, with nothing left pointing at it.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("an object the player does not remember is deleted")
        void unrememberedObjectIsDeleted() throws Exception {
            ItemObject item = knownObject();
            list(level, item);

            level.objectDelete(known, item);

            assertFalse(level.getObjects().contains(item));
        }

        /**
         * With no remembered level at all — the state between dungeon levels — there is nothing to
         * orphan for, and the object is deleted.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("with no remembered level, the object is deleted")
        void noRememberedLevelDeletes() throws Exception {
            ItemObject item = knownObject();
            list(level, item);

            level.objectDelete(null, item);

            assertFalse(level.getObjects().contains(item));
        }

        /**
         * The orphan test runs before anything is removed. If the receiver's list were cleared
         * first, an object both chunks list would fail the test and be deleted instead — so this
         * asserts the ordering by asserting the outcome that depends on it.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the orphan test is made before the lists are cleared")
        void orphanTestPrecedesRemoval() throws Exception {
            ItemObject item = knownObject();
            list(level, item);
            list(known, item);

            level.objectDelete(known, item);

            assertTrue(level.getObjects().contains(item),
                    "a removal before the test would have deleted this object");
        }

        /**
         * Deleting the object the player is examining stops them examining it, so nothing is left
         * tracking something that no longer exists.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("deleting the tracked object stops the tracking")
        void deletingTrackedObjectClearsTracking() throws Exception {
            ItemObject item = knownObject();
            list(level, item);
            player.getPlayerUpkeep().setObject(item);

            level.objectDelete(known, item);

            assertNull(player.getPlayerUpkeep().getObject());
        }

        /**
         * Deleting something else leaves the tracking alone — the test is on identity, not on
         * whether anything at all was deleted.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("deleting something else leaves the tracking alone")
        void deletingOtherObjectKeepsTracking() throws Exception {
            ItemObject tracked = knownObject();
            ItemObject other = knownObject();
            list(level, other);
            player.getPlayerUpkeep().setObject(tracked);

            level.objectDelete(known, other);

            assertSame(tracked, player.getPlayerUpkeep().getObject());
        }
    }

    /**
     * The list removal, which is the delete's quieter cousin.
     */
    @Nested
    @DisplayName("delistObject")
    class Delist {

        /**
         * An object the chunk does not list is left alone rather than reported.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("an unlisted object is ignored")
        void unlistedIsIgnored() throws Exception {
            known.delistObject(knownObject());

            assertTrue(known.getObjects().isEmpty());
        }

        /**
         * An object the remembered level lists is removed from it.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a listed object is removed")
        void listedIsRemoved() throws Exception {
            ItemObject item = knownObject();
            list(known, item);

            known.delistObject(item);

            assertFalse(known.getObjects().contains(item));
        }

        /**
         * An object the player still remembers is <em>not</em> removed from the real level, which is
         * the same protection the delete's orphan branch gives — the memory has to keep pointing
         * somewhere.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the level keeps an object the player remembers")
        void levelKeepsRememberedObject() throws Exception {
            ItemObject item = knownObject();
            list(level, item);
            list(known, item);

            level.delistObject(item);

            assertTrue(level.getObjects().contains(item));
        }
    }

    /**
     * Removing an object from the pile on one square.
     */
    @Nested
    @DisplayName("squareExciseObject")
    class Excise {

        /**
         * The object leaves that square's pile.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the object leaves the square's pile")
        void objectLeavesThePile() throws Exception {
            ItemObject item = knownObject();
            Loc grid = Loc.row(2).col(3);
            level.getSquare(grid).getObjectPile().insert(item);

            assertTrue(level.getSquare(grid).getObjectPile().contains(item));

            level.squareExciseObject(grid, item);

            assertFalse(level.getSquare(grid).getObjectPile().contains(item));
        }

        /**
         * Only that square's pile: an object of the same identity elsewhere is untouched, because
         * the excise works on the pile it was pointed at rather than searching the level.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("only the named square's pile is touched")
        void onlyThatSquareIsTouched() throws Exception {
            ItemObject item = knownObject();
            Loc here = Loc.row(2).col(3);
            Loc there = Loc.row(4).col(1);
            level.getSquare(here).getObjectPile().insert(item);
            level.getSquare(there).getObjectPile().insert(item);

            level.squareExciseObject(here, item);

            assertTrue(level.getSquare(there).getObjectPile().contains(item));
        }

        /**
         * A grid off the level is a coding error and is refused, rather than being quietly ignored
         * as the read-only square accessors are — a caller excising from nowhere has lost track of
         * where the object is.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a grid off the level is refused")
        void offLevelGridIsRefused() throws Exception {
            ItemObject item = knownObject();

            assertThrows(IndexOutOfBoundsException.class,
                    () -> level.squareExciseObject(Loc.row(-1).col(0), item));
        }
    }
}
