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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.cave.Chunk;
import uk.co.jackoftrades.middle.game.gameengine.GameState;
import uk.co.jackoftrades.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.Player;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;
import uk.co.jackoftrades.testsupport.ItemFixture;

import java.lang.reflect.Field;

import static uk.co.jackoftrades.testsupport.ItemFixture.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#objectAbsorb} and {@link ItemObject#objectSplit} — the two halves of
 * moving items between stacks, and the port of C's {@code object_absorb} and {@code object_split}
 * ({@code obj-pile.c:676}, {@code obj-pile.c:790}).
 *
 * <p>Both need the two caves wired, and that is the point of the fixture here. C keeps the real
 * level and the player's remembered copy of it as separate chunks, and an absorb touches both: the
 * absorbed stack's <em>known</em> half is excised and deleted from the player's cave, and the real
 * object from the level. Getting the two the wrong way round would leave one list holding an object
 * the other has destroyed, and nothing would say so until something walked that list.
 *
 * <p>The split is the reverse, and its own asymmetry is worth pinning: the new stack takes a
 * <em>share</em> of the charges, while the counts are moved outright.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectAbsorbTest {

    /**
     * The greatest number of one kind that may share a stack.
     */
    private static final int MAX_STACK = 40;

    /**
     * The real level.
     */
    private Chunk level;

    /**
     * The player's remembered copy of it.
     */
    private Chunk known;

    /**
     * The player, holding the remembered level.
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
     * The kind both stacks are built on, which decides the stacking limit.
     */
    private ObjectKind kind;

    /**
     * Wires a real level, a remembered one, and a player holding the second.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void wireCaves() throws Exception {
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
        // objectAbsorbMerge tells the player to re-know the surviving stack, and that reads the
        // player's general rune knowledge, which the constructor leaves unset.
        set(player, "itemKnowledge", new KnownObject());

        // knowObject compares the item's bonuses against the ranges its kind rolls, so the kind
        // needs its dice as well as the base whose max stack the merge reads.
        kind = ItemFixture.loadedKind(TValue.TV_ARROW, "arrow", MAX_STACK);
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
     * A stack of the given size, with a known half attached and both halves listed in the caves the
     * way a real object would be.
     *
     * @param number the stack size
     * @return the stack
     * @throws Exception if a field cannot be reached
     */
    private ItemObject stack(int number) throws Exception {
        ItemObject item = bareStack(number);
        ItemObject counterpart = bareStack(number);
        set(counterpart, "kind", kind);
        set(item, "known", counterpart);

        level.getObjects();
        listInLevel(item);
        listInKnown(counterpart);

        return item;
    }

    /**
     * A stack with no knowledge and no listing, for the cases that do not need either.
     *
     * @param number the stack size
     * @return the stack
     * @throws Exception if a field cannot be reached
     */
    private ItemObject bareStack(int number) {
        // The split copies the item, and the copy calls copy() on the base damage and the recharge
        // time rather than testing them for null. Building through the fixture resolves both from
        // dice strings the way the parser does, so the copy has something to copy.
        return ItemFixture.item(TValue.TV_ARROW).kind(kind).number(number)
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 1, null).build();
    }

    /**
     * Adds an object to the real level's list.
     *
     * @param item the object to list
     * @throws Exception if the list cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void listInLevel(ItemObject item) throws Exception {
        Field field = Chunk.class.getDeclaredField("objects");
        field.setAccessible(true);
        ((java.util.List<ItemObject>) field.get(level)).add(item);
    }

    /**
     * Adds an object to the remembered level's list.
     *
     * @param item the object to list
     * @throws Exception if the list cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void listInKnown(ItemObject item) throws Exception {
        Field field = Chunk.class.getDeclaredField("objects");
        field.setAccessible(true);
        ((java.util.List<ItemObject>) field.get(known)).add(item);
    }

    /**
     * The whole absorb, which folds one stack into another and destroys the emptied one.
     */
    @Nested
    @DisplayName("objectAbsorb")
    class Absorb {

        /**
         * The counts are added, and the survivor holds the total.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the counts are added into the survivor")
        void countsAreAdded() throws Exception {
            ItemObject survivor = stack(10);
            ItemObject absorbed = stack(15);

            survivor.objectAbsorb(absorbed);

            assertEquals(25, survivor.getNumber());
        }

        /**
         * The combined count is capped at the kind's stacking limit, so two large stacks do not
         * produce an impossible one.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the total is capped at the kind's limit")
        void totalIsCapped() throws Exception {
            ItemObject survivor = stack(30);
            ItemObject absorbed = stack(25);

            survivor.objectAbsorb(absorbed);

            assertEquals(MAX_STACK, survivor.getNumber());
        }

        /**
         * The absorbed object is removed from the real level's list — the disposal that makes the
         * merge final.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the absorbed stack leaves the level")
        void absorbedLeavesTheLevel() throws Exception {
            ItemObject survivor = stack(10);
            ItemObject absorbed = stack(5);

            assertTrue(level.getObjects().contains(absorbed));

            survivor.objectAbsorb(absorbed);

            assertFalse(level.getObjects().contains(absorbed),
                    "the real object was deleted from the level");
        }

        /**
         * And its known half leaves the player's remembered level, which is the other cave the
         * absorb has to touch. The survivor's own knowledge stays.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the absorbed stack's knowledge leaves the remembered level")
        void knowledgeLeavesTheRememberedLevel() throws Exception {
            ItemObject survivor = stack(10);
            ItemObject absorbed = stack(5);
            ItemObject absorbedKnowledge = absorbed.getKnown();

            survivor.objectAbsorb(absorbed);

            assertFalse(known.getObjects().contains(absorbedKnowledge),
                    "the knowledge went with the object");
            assertTrue(known.getObjects().contains(survivor.getKnown()),
                    "the survivor's knowledge stayed");
        }

        /**
         * The survivor keeps its own knowledge rather than taking the absorbed stack's — merging two
         * stacks does not make the player forget which one they were looking at.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the survivor keeps its own knowledge")
        void survivorKeepsItsKnowledge() throws Exception {
            ItemObject survivor = stack(10);
            ItemObject survivorKnowledge = survivor.getKnown();

            survivor.objectAbsorb(stack(5));

            assertSame(survivorKnowledge, survivor.getKnown());
        }

        /**
         * An absorbed stack with no knowledge at all skips the whole knowledge half and is still
         * disposed of — the shape an object generated but never seen is in.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a stack with no knowledge is absorbed all the same")
        void unknownStackIsAbsorbed() throws Exception {
            ItemObject survivor = stack(10);
            ItemObject absorbed = bareStack(5);
            listInLevel(absorbed);

            survivor.objectAbsorb(absorbed);

            assertEquals(15, survivor.getNumber());
            assertFalse(level.getObjects().contains(absorbed));
        }
    }

    /**
     * The split, which moves part of a stack into a new one.
     */
    @Nested
    @DisplayName("objectSplit")
    class Split {

        /**
         * The counts move: the new stack holds what was asked for and the original keeps the rest,
         * with the total conserved.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the counts move and the total is conserved")
        void countsMove() throws Exception {
            ItemObject original = stack(20);

            ItemObject split = original.objectSplit(8);

            assertEquals(8, split.getNumber());
            assertEquals(12, original.getNumber());
        }

        /**
         * Both known halves are kept in step with their objects, so knowledge and truth do not drift
         * apart over a split.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the known halves follow the counts")
        void knowledgeFollowsTheCounts() throws Exception {
            ItemObject original = stack(20);

            ItemObject split = original.objectSplit(8);

            assertEquals(8, split.getKnown().getNumber());
            assertEquals(12, original.getKnown().getNumber());
        }

        /**
         * The new stack is a separate object with its own knowledge, not a second reference to the
         * original's.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the new stack is its own object")
        void newStackIsSeparate() throws Exception {
            ItemObject original = stack(20);

            ItemObject split = original.objectSplit(8);

            assertFalse(split == original);
            assertFalse(split.getKnown() == original.getKnown(),
                    "and its knowledge is its own too");
        }

        /**
         * Splitting off the whole stack, or more than it holds, is refused — a caller wanting all of
         * it should move the stack rather than split it.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("splitting off the whole stack is refused")
        void wholeStackIsRefused() throws Exception {
            ItemObject original = stack(20);

            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                    () -> original.objectSplit(20));
            org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                    () -> original.objectSplit(25));
        }

        /**
         * An inscription is carried onto the new stack, so splitting a labelled pile does not lose
         * the label on half of it.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the inscription is carried onto the new stack")
        void inscriptionIsCarried() throws Exception {
            ItemObject original = stack(20);
            original.setNote("@f1");

            ItemObject split = original.objectSplit(8);

            assertEquals("@f1", split.getNote());
        }

        /**
         * A stack with no knowledge splits too — the knowledge half of the work is skipped rather
         * than being required.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a stack with no knowledge splits all the same")
        void unknownStackSplits() throws Exception {
            ItemObject original = bareStack(20);

            ItemObject split = original.objectSplit(8);

            assertEquals(8, split.getNumber());
            assertEquals(12, original.getNumber());
            assertNull(split.getKnown());
        }
    }
}
