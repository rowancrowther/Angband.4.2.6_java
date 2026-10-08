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
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.middle.cave.Chunk;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;

import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ItemObject#objectAbsorb} and {@link ItemObject#objectSplit} — the two halves of
 * moving items between stacks, and the port of C's {@code object_absorb}, {@code object_absorb_merge}
 * and {@code object_split} ({@code obj-pile.c}).
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
     */
    private ItemObject bareStack(int number) {
        // The split copies the item, and the copy calls copy() on the base damage and the recharge
        // time rather than testing them for null. Building through the fixture resolves both from
        // dice strings the way the parser does, so the copy has something to copy.
        return ItemFixture.item(TValue.TV_ARROW).kind(kind).number(number)
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 1, null).build();
    }

    /**
     * A stack of an item type that pools charges, gold or timeouts, with no knowledge and listed in
     * the real level so the absorb can delete it.
     *
     * @param tValue the item type
     * @param number the stack size
     * @return the stack
     * @throws Exception if a field cannot be reached
     */
    private ItemObject bareLoaded(TValue tValue, int number) throws Exception {
        ObjectKind loaded = ItemFixture.loadedKind(tValue, tValue.name(), MAX_STACK);
        ItemObject item = ItemFixture.item(tValue).kind(loaded).number(number)
                .origin(ObjectOriginEnum.ORIGIN_FLOOR, 1, null).build();
        listInLevel(item);
        return item;
    }

    /**
     * Installs a constants structure that has a world block, because averaging a recharge dice
     * divides its level bonus by the maximum depth even when that bonus is zero.
     *
     * @return what the constants holder held before, to hand to {@link #restoreConstants}
     * @throws Exception if the constants holder cannot be reached
     */
    private Object seedWorldDepth() throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        Object saved = data.get(null);
        data.set(null, new GameConstantsData(
                null, null, null, null,
                new WorldData(128, 10000, 0, 0, 0, 0, 0, 0, 0, 0),
                null, null, null, null, null, java.util.List.of(),
                null, java.util.List.of(), null, java.util.List.of(),
                null, java.util.List.of()));
        return saved;
    }

    /**
     * Puts the constants back.
     *
     * @param saved what {@link #seedWorldDepth} returned
     * @throws Exception if the constants holder cannot be reached
     */
    private void restoreConstants(Object saved) throws Exception {
        Field data = GameConstants.class.getDeclaredField("data");
        data.setAccessible(true);
        data.set(null, saved);
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

        /**
         * An item remembers the player that was current when it was built, and C reads its global
         * at the moment of the call. An absorb on an item whose snapshot is empty or stale must
         * still reach the live player's caves, so the player is refreshed first.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a stale player snapshot is refreshed before the absorb")
        void stalePlayerIsRefreshed() throws Exception {
            ItemObject survivor = stack(10);
            ItemObject absorbed = stack(5);
            ItemObject absorbedKnowledge = absorbed.getKnown();
            set(survivor, "player", null);

            survivor.objectAbsorb(absorbed);

            assertEquals(15, survivor.getNumber());
            assertFalse(level.getObjects().contains(absorbed), "the live level was used");
            assertFalse(known.getObjects().contains(absorbedKnowledge), "and the live player's cave");
        }

        /**
         * An inscription on the absorbed stack replaces the survivor's, as C's
         * {@code if (obj2->note) obj1->note = obj2->note}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the absorbed stack's inscription is taken")
        void absorbedInscriptionIsTaken() throws Exception {
            ItemObject survivor = bareStack(10);
            ItemObject absorbed = bareStack(5);
            absorbed.setNote("!d");
            listInLevel(absorbed);

            survivor.objectAbsorb(absorbed);

            assertEquals("!d", survivor.getNote());
        }

        /**
         * An absorbed stack with no inscription leaves the survivor's untouched.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the survivor's inscription stays when the absorbed stack has none")
        void survivorInscriptionStays() throws Exception {
            ItemObject survivor = bareStack(10);
            survivor.setNote("@f1");
            ItemObject absorbed = bareStack(5);
            listInLevel(absorbed);

            survivor.objectAbsorb(absorbed);

            assertEquals("@f1", survivor.getNote());
        }

        /**
         * Wand charges are pooled: C adds the two {@code pval}s, capped at {@code MAX_PVAL}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("wand charges are added")
        void wandChargesAreAdded() throws Exception {
            ItemObject survivor = bareLoaded(TValue.TV_WAND, 2);
            ItemObject absorbed = bareLoaded(TValue.TV_WAND, 3);
            set(survivor, "pValue", 5);
            set(absorbed, "pValue", 7);

            survivor.objectAbsorb(absorbed);

            assertEquals(12, survivor.getpValue());
        }

        /**
         * The pooled charges stop at {@code MAX_PVAL}, 32767 in C's {@code obj-util.h}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("pooled charges are capped at 32767")
        void chargesAreCapped() throws Exception {
            ItemObject survivor = bareLoaded(TValue.TV_WAND, 2);
            ItemObject absorbed = bareLoaded(TValue.TV_WAND, 3);
            set(survivor, "pValue", 30000);
            set(absorbed, "pValue", 5000);

            survivor.objectAbsorb(absorbed);

            assertEquals(32767, survivor.getpValue());
        }

        /**
         * Gold is pooled the same way as charges, because C's test is
         * {@code tval_can_have_charges(obj1) || tval_is_money(obj1)}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("gold is added")
        void goldIsAdded() throws Exception {
            ItemObject survivor = bareLoaded(TValue.TV_GOLD, 1);
            ItemObject absorbed = bareLoaded(TValue.TV_GOLD, 1);
            set(survivor, "pValue", 100);
            set(absorbed, "pValue", 250);

            survivor.objectAbsorb(absorbed);

            assertEquals(350, survivor.getpValue());
        }

        /**
         * Rod timeouts are added, so a stack of rods is as far from ready as the two were together.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("rod timeouts are added")
        void rodTimeoutsAreAdded() throws Exception {
            ItemObject survivor = bareLoaded(TValue.TV_ROD, 1);
            ItemObject absorbed = bareLoaded(TValue.TV_ROD, 1);
            set(survivor, "timeout", 30);
            set(absorbed, "timeout", 12);

            survivor.objectAbsorb(absorbed);

            assertEquals(42, survivor.getTimeout());
        }

        /**
         * Ordinary items do not pool a {@code pval}: C only does so for charged items and gold.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("an ordinary item's pval is not pooled")
        void ordinaryPvalIsNotPooled() throws Exception {
            ItemObject survivor = bareStack(10);
            ItemObject absorbed = bareStack(5);
            set(survivor, "pValue", 3);
            set(absorbed, "pValue", 4);
            listInLevel(absorbed);

            survivor.objectAbsorb(absorbed);

            assertEquals(3, survivor.getpValue());
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

        /**
         * A wand's charges are shared out in proportion to the count moved, rounding down for the
         * new stack: C's {@code pval * amt / number}, here {@code 10 * 1 / 4 = 2}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("wand charges are shared out by the count moved")
        void chargesAreShared() throws Exception {
            ObjectKind wand = ItemFixture.loadedKind(TValue.TV_WAND, "wand", MAX_STACK);
            ItemObject original = ItemFixture.item(TValue.TV_WAND).kind(wand).number(4).build();
            set(original, "pValue", 10);

            ItemObject split = original.objectSplit(1);

            assertEquals(2, split.getpValue());
            assertEquals(8, original.getpValue());
        }

        /**
         * Splitting off most of the stack moves most of the charges, with the remainder left behind
         * and the total conserved: {@code 10 * 3 / 4 = 7}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("most of the stack takes most of the charges")
        void mostChargesMove() throws Exception {
            ObjectKind wand = ItemFixture.loadedKind(TValue.TV_WAND, "wand", MAX_STACK);
            ItemObject original = ItemFixture.item(TValue.TV_WAND).kind(wand).number(4).build();
            set(original, "pValue", 10);

            ItemObject split = original.objectSplit(3);

            assertEquals(7, split.getpValue());
            assertEquals(3, original.getpValue());
        }

        /**
         * C sets the known half's count to the real count before it copies and shares out the known
         * charges, because {@code distribute_charges} divides by the count it finds. A known half
         * left at a stale count must not skew the share: with the real count 4 and the stale count
         * 7, the new known stack still takes {@code 10 * 1 / 4 = 2} charges, not {@code 10 * 1 / 7 = 1}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("a stale known count does not skew the known charges")
        void staleKnownCountIsAligned() throws Exception {
            ObjectKind wand = ItemFixture.loadedKind(TValue.TV_WAND, "wand", MAX_STACK);
            ItemObject counterpart = ItemFixture.item(TValue.TV_WAND).kind(wand).number(7).build();
            set(counterpart, "pValue", 10);
            ItemObject original = ItemFixture.item(TValue.TV_WAND).kind(wand).number(4)
                    .known(counterpart).build();
            set(original, "pValue", 10);

            ItemObject split = original.objectSplit(1);

            assertEquals(2, split.getKnown().getpValue());
            assertEquals(8, original.getKnown().getpValue());
            assertEquals(1, split.getKnown().getNumber());
            assertEquals(3, original.getKnown().getNumber());
        }

        /**
         * A rod stack whose known half is built bare, as {@code PlayerBirth} builds one, splits
         * without throwing. {@code distribute_charges} reads the recharge dice of the known half
         * too, and C's known half holds a zeroed {@code random_value} there; the port's must hold
         * a zero rather than {@code null}.
         *
         * <p>The real half shares its timeout out against its own dice: {@code 10} per rod, one rod
         * moving, so the new stack takes {@code min(25, 10) = 10} and the original keeps 15. The
         * known half's dice are zero, so its timeout, which starts at zero, stays at zero on both
         * stacks.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("rods split through a bare known half, which shares no timeout")
        void rodSplitsThroughBareKnownHalf() throws Exception {
            ObjectKind rod = ItemFixture.loadedKind(TValue.TV_ROD, "rod", MAX_STACK);
            ItemObject original = ItemFixture.item(TValue.TV_ROD).kind(rod).number(3).build();
            original.setTime(new Random(10, 0, 0, 1, false));
            original.setTimeout(25);
            Object savedConstants = seedWorldDepth();

            ItemObject counterpart = new ItemObject();
            counterpart.settValue(TValue.TV_ROD);
            counterpart.setKind(rod);
            counterpart.setNumber(3);
            original.setKnown(counterpart);

            ItemObject split;
            try {
                split = original.objectSplit(1);
            } finally {
                restoreConstants(savedConstants);
            }

            assertEquals(1, split.getNumber());
            assertEquals(2, original.getNumber());
            assertEquals(10, split.getTimeout());
            assertEquals(15, original.getTimeout());
            assertEquals(1, split.getKnown().getNumber());
            assertEquals(2, original.getKnown().getNumber());
            assertEquals(0, split.getKnown().getTimeout());
            assertEquals(0, original.getKnown().getTimeout());
        }

        /**
         * The known half of the new stack takes the known half's own inscription, as C's
         * {@code dest->known->note = src->known->note}.
         *
         * @throws Exception if a fixture field cannot be reached
         */
        @Test
        @DisplayName("the new known half takes the known half's inscription")
        void knownInscriptionIsCarried() throws Exception {
            ItemObject original = stack(20);
            original.getKnown().setNote("@k");

            ItemObject split = original.objectSplit(8);

            assertEquals("@k", split.getKnown().getNote());
        }
    }
}
