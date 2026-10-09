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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the rest of {@link Pile} beyond {@link Pile#insert}, which {@link PileInsertTest} covers:
 * {@code insertEnd} for one object and for a whole pile, {@code excise}, {@code lastItem},
 * {@code contains}, the index and bulk helpers, and {@code hasArtifact}.
 *
 * <p>Ports C's {@code pile_insert_end}, {@code pile_excise}, {@code pile_last_item} and
 * {@code pile_contains} ({@code obj-pile.c}). Every expected order below is written the way C
 * would list it, head to tail, because that is how the C functions are specified: {@code pile_insert}
 * links at the head, {@code pile_insert_end} links after the tail, and {@code pile_last_item}
 * returns the tail. This class holds its list backwards, so {@code reversed()} (head first) is
 * the view that must equal C's list, and index 0 is C's tail.
 *
 * <p>Class PileTest written on 261009.
 *
 * @author Rowan Crowther
 */
class PileTest {

    /**
     * Builds a pile by {@code pile_insert}ing the given objects in order, so the last one given
     * is C's head.
     *
     * @param items the objects, oldest first
     * @return a pile holding them
     */
    private static Pile pileOf(ItemObject... items) {
        Pile pile = new Pile();
        for (ItemObject item : items) pile.insert(item);
        return pile;
    }

    /**
     * Reads a pile the way C walks it, head to tail.
     *
     * @param pile the pile to read
     * @return a copy of the objects, head first
     */
    private static List<ItemObject> headToTail(Pile pile) {
        return new ArrayList<>(pile.reversed());
    }

    /**
     * Gives an item an artifact definition by reflection. {@link ItemObject#isArtifact()} only
     * asks whether the field is non-null, so nothing in the definition is read.
     *
     * @param item the item to make an artefact
     */
    private static void makeArtifact(ItemObject item) {
        Artifact artifact = new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(), new LinkedHashMap<>(),
                0, 0, 0, 0, null, null, null);
        try {
            Field field = ItemObject.class.getDeclaredField("artifact");
            field.setAccessible(true);
            field.set(item, artifact);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("ItemObject.artifact is no longer settable by reflection", e);
        }
    }

    /**
     * {@code pile_insert_end} for a single object: link it after the tail.
     */
    @Nested
    @DisplayName("insertEnd(ItemObject)")
    class InsertEndOne {

        @Test
        @DisplayName("into an empty pile the object becomes the whole list")
        void intoEmptyPile() {
            Pile pile = new Pile();
            ItemObject a = new ItemObject();

            pile.insertEnd(a);

            assertEquals(List.of(a), headToTail(pile));
            assertSame(pile, a.getOwningPile());
            assertSame(a, pile.lastItem());
            assertSame(a, pile.peekLastItem());
        }

        @Test
        @DisplayName("goes after the existing tail, not before the head")
        void goesAfterTheTail() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b); // C head to tail: b, a

            pile.insertEnd(c);

            assertEquals(List.of(b, a, c), headToTail(pile));
            assertSame(c, pile.lastItem());
            assertSame(b, pile.peekLastItem());
            assertSame(pile, c.getOwningPile());
        }

        @Test
        @DisplayName("repeated calls build the list in call order, head to tail")
        void repeatedCallsKeepCallOrder() {
            Pile pile = new Pile();
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();

            pile.insertEnd(a);
            pile.insertEnd(b);
            pile.insertEnd(c);

            assertEquals(List.of(a, b, c), headToTail(pile));
        }

        @Test
        @DisplayName("rejects an object already in this pile")
        void rejectsObjectInThisPile() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            assertThrows(RuntimeException.class, () -> pile.insertEnd(a));

            assertEquals(List.of(b, a), headToTail(pile));
            assertSame(pile, a.getOwningPile());
        }

        @Test
        @DisplayName("rejects an object that sits mid-list in another pile")
        void rejectsMidListObject() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile source = pileOf(a, b, c);
            Pile destination = new Pile();

            assertThrows(RuntimeException.class, () -> destination.insertEnd(b));

            assertTrue(destination.isEmpty());
            assertSame(source, b.getOwningPile());
            assertEquals(List.of(c, b, a), headToTail(source));
        }

        @Test
        @DisplayName("rejects the sole member of another pile, which is stricter than C")
        void rejectsSoloMemberOfAnotherPile() {
            ItemObject solo = new ItemObject();
            Pile source = pileOf(solo);
            Pile destination = new Pile();

            assertThrows(RuntimeException.class, () -> destination.insertEnd(solo));

            assertTrue(destination.isEmpty());
            assertSame(source, solo.getOwningPile());
        }
    }

    /**
     * {@code pile_insert_end} for the head of a whole list, as {@code wield_all} uses it.
     */
    @Nested
    @DisplayName("insertEnd(Pile)")
    class InsertEndPile {

        @Test
        @DisplayName("appends the new list after the tail, keeping both internal orders")
        void appendsAfterTheTail() {
            ItemObject g0 = new ItemObject();
            ItemObject g1 = new ItemObject();
            ItemObject n0 = new ItemObject();
            ItemObject n1 = new ItemObject();
            Pile gear = pileOf(g0, g1);  // C head to tail: g1, g0
            Pile added = pileOf(n0, n1); // C head to tail: n1, n0

            gear.insertEnd(added);

            // C: the chain n1, n0 is linked on after the old tail g0.
            assertEquals(List.of(g1, g0, n1, n0), headToTail(gear));
            assertSame(g1, gear.peekLastItem());
            assertSame(n0, gear.lastItem());
            assertSame(n0, gear.get(0));
            assertSame(n1, gear.get(1));
            assertSame(g0, gear.get(2));
            assertSame(g1, gear.get(3));
        }

        @Test
        @DisplayName("every moved object is owned by the destination")
        void movedObjectsAreOwnedByTheDestination() {
            ItemObject g0 = new ItemObject();
            ItemObject n0 = new ItemObject();
            ItemObject n1 = new ItemObject();
            Pile gear = pileOf(g0);
            Pile added = pileOf(n0, n1);

            gear.insertEnd(added);

            assertSame(gear, g0.getOwningPile());
            assertSame(gear, n0.getOwningPile());
            assertSame(gear, n1.getOwningPile());
        }

        @Test
        @DisplayName("into an empty pile the new list becomes the whole list")
        void intoEmptyPile() {
            ItemObject n0 = new ItemObject();
            ItemObject n1 = new ItemObject();
            Pile gear = new Pile();
            Pile added = pileOf(n0, n1);

            gear.insertEnd(added);

            assertEquals(List.of(n1, n0), headToTail(gear));
            assertSame(n0, gear.lastItem());
        }

        @Test
        @DisplayName("an empty new list changes nothing")
        void emptyNewListChangesNothing() {
            ItemObject g0 = new ItemObject();
            ItemObject g1 = new ItemObject();
            Pile gear = pileOf(g0, g1);

            gear.insertEnd(new Pile());

            assertEquals(List.of(g1, g0), headToTail(gear));
            assertSame(gear, g0.getOwningPile());
            assertSame(gear, g1.getOwningPile());
        }

        @Test
        @DisplayName("a one-object new list goes at the tail")
        void singleObjectList() {
            ItemObject g0 = new ItemObject();
            ItemObject n0 = new ItemObject();
            Pile gear = pileOf(g0);

            gear.insertEnd(pileOf(n0));

            assertEquals(List.of(g0, n0), headToTail(gear));
        }

        @Test
        @DisplayName("gives the same list as appending the new list's head, then each next object")
        void matchesAppendingOneByOneHeadFirst() {
            ItemObject g0 = new ItemObject();
            ItemObject n0 = new ItemObject();
            ItemObject n1 = new ItemObject();
            ItemObject n2 = new ItemObject();
            Pile viaChain = pileOf(g0);
            viaChain.insertEnd(pileOf(n0, n1, n2)); // new list, C head to tail: n2, n1, n0

            // By hand, C appends the chain's head first and then follows obj->next: n2, n1, n0.
            ItemObject h0 = new ItemObject();
            ItemObject m0 = new ItemObject();
            ItemObject m1 = new ItemObject();
            ItemObject m2 = new ItemObject();
            Pile byHand = pileOf(h0);
            byHand.insertEnd(m2);
            byHand.insertEnd(m1);
            byHand.insertEnd(m0);

            assertEquals(List.of(g0, n2, n1, n0), headToTail(viaChain));
            assertEquals(List.of(h0, m2, m1, m0), headToTail(byHand));
        }

        @Test
        @DisplayName("appending a pile to itself throws and changes nothing")
        void selfAppendThrows() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            assertThrows(RuntimeException.class, () -> pile.insertEnd(pile));

            assertEquals(List.of(b, a), headToTail(pile));
            assertSame(pile, a.getOwningPile());
            assertSame(pile, b.getOwningPile());
        }

        @Test
        @DisplayName("the source pile is spent: its objects now belong to the destination")
        void sourcePileIsSpent() {
            ItemObject g0 = new ItemObject();
            ItemObject n0 = new ItemObject();
            Pile gear = pileOf(g0);
            Pile added = pileOf(n0);

            gear.insertEnd(added);

            assertThrows(RuntimeException.class, () -> added.excise(n0));
            assertSame(gear, n0.getOwningPile());
            assertTrue(gear.contains(n0));
        }
    }

    /**
     * {@code pile_excise}: unlink an object from the list.
     */
    @Nested
    @DisplayName("excise")
    class Excise {

        @Test
        @DisplayName("the head can be removed, leaving the rest in order")
        void removesTheHead() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c); // C head to tail: c, b, a

            pile.excise(c);

            assertEquals(List.of(b, a), headToTail(pile));
            assertNull(c.getOwningPile());
            assertSame(b, pile.peekLastItem());
        }

        @Test
        @DisplayName("a middle object can be removed, leaving the rest in order")
        void removesTheMiddle() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c);

            pile.excise(b);

            assertEquals(List.of(c, a), headToTail(pile));
            assertNull(b.getOwningPile());
        }

        @Test
        @DisplayName("the tail can be removed, and the next object becomes the tail")
        void removesTheTail() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c);

            pile.excise(a);

            assertEquals(List.of(c, b), headToTail(pile));
            assertSame(b, pile.lastItem());
            assertNull(a.getOwningPile());
        }

        @Test
        @DisplayName("the only object can be removed, leaving an empty pile")
        void removesTheOnlyObject() {
            ItemObject a = new ItemObject();
            Pile pile = pileOf(a);

            pile.excise(a);

            assertTrue(pile.isEmpty());
            assertNull(pile.lastItem());
        }

        @Test
        @DisplayName("a removed object can be inserted again")
        void removedObjectCanBeReinserted() {
            ItemObject a = new ItemObject();
            Pile first = pileOf(a);
            Pile second = new Pile();

            first.excise(a);
            second.insert(a);

            assertSame(second, a.getOwningPile());
            assertTrue(first.isEmpty());
        }

        @Test
        @DisplayName("an object from another pile throws and keeps its owner")
        void objectFromAnotherPileThrows() {
            ItemObject a = new ItemObject();
            ItemObject x = new ItemObject();
            Pile pile = pileOf(a);
            Pile other = pileOf(x);

            assertThrows(RuntimeException.class, () -> pile.excise(x));

            assertSame(other, x.getOwningPile());
            assertTrue(other.contains(x));
            assertEquals(List.of(a), headToTail(pile));
        }

        @Test
        @DisplayName("an object in no pile throws")
        void unownedObjectThrows() {
            Pile pile = pileOf(new ItemObject());
            ItemObject stray = new ItemObject();

            assertThrows(RuntimeException.class, () -> pile.excise(stray));

            assertEquals(1, pile.size());
            assertNull(stray.getOwningPile());
        }
    }

    /**
     * {@code pile_last_item}: the tail, without removing it, and {@code NULL} for no pile.
     */
    @Nested
    @DisplayName("lastItem and peekLastItem")
    class LastItem {

        @Test
        @DisplayName("lastItem on an empty pile is null")
        void lastItemOfEmptyPileIsNull() {
            assertNull(new Pile().lastItem());
        }

        @Test
        @DisplayName("lastItem is the tail, the oldest object added by insert")
        void lastItemIsTheTail() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c);

            assertSame(a, pile.lastItem());
        }

        @Test
        @DisplayName("lastItem does not remove the object or change its owner")
        void lastItemDoesNotRemove() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            assertSame(a, pile.lastItem());
            assertSame(a, pile.lastItem());

            assertEquals(2, pile.size());
            assertSame(pile, a.getOwningPile());
        }

        @Test
        @DisplayName("lastItem of a one-object pile is that object, which is also the head")
        void singleObjectIsBothEnds() {
            ItemObject a = new ItemObject();
            Pile pile = pileOf(a);

            assertSame(a, pile.lastItem());
            assertSame(a, pile.peekLastItem());
        }

        @Test
        @DisplayName("peekLastItem is the head, the opposite end to lastItem")
        void peekLastItemIsTheHead() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            assertSame(b, pile.peekLastItem());
            assertEquals(2, pile.size());
        }

        @Test
        @DisplayName("peekLastItem on an empty pile throws, although it is declared @Nullable")
        void peekLastItemOfEmptyPileThrows() {
            Pile pile = new Pile();

            assertThrows(NoSuchElementException.class, pile::peekLastItem);
        }
    }

    /**
     * {@code pile_contains}: pointer comparison down the list.
     */
    @Nested
    @DisplayName("contains")
    class Contains {

        @Test
        @DisplayName("finds each object in the pile")
        void findsEachObject() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            assertTrue(pile.contains(a));
            assertTrue(pile.contains(b));
        }

        @Test
        @DisplayName("an empty pile contains nothing")
        void emptyPileContainsNothing() {
            assertFalse(new Pile().contains(new ItemObject()));
        }

        @Test
        @DisplayName("compares identity, so a separate but identical object is not found")
        void comparesIdentity() {
            ItemObject a = new ItemObject();
            ItemObject lookalike = new ItemObject();
            Pile pile = pileOf(a);

            assertFalse(pile.contains(lookalike));
        }

        @Test
        @DisplayName("an object in another pile is not found")
        void otherPilesObjectIsNotFound() {
            ItemObject a = new ItemObject();
            ItemObject x = new ItemObject();
            Pile pile = pileOf(a);
            pileOf(x);

            assertFalse(pile.contains(x));
        }
    }

    /**
     * The index and bulk helpers that have no function of their own in {@code obj-pile.c}.
     */
    @Nested
    @DisplayName("index and bulk helpers")
    class Helpers {

        @Test
        @DisplayName("get counts from the tail: 0 is C's last item, size - 1 is C's head")
        void getCountsFromTheTail() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c);

            assertSame(a, pile.get(0));
            assertSame(b, pile.get(1));
            assertSame(c, pile.get(2));
            assertThrows(IndexOutOfBoundsException.class, () -> pile.get(3));
        }

        @Test
        @DisplayName("getIterator walks head first, as C's obj->next walk does")
        void iteratorWalksHeadFirst() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c); // C head to tail: c, b, a

            Iterator<ItemObject> it = pile.getIterator();
            List<ItemObject> walked = new ArrayList<>();
            while (it.hasNext()) walked.add(it.next());

            assertEquals(List.of(c, b, a), walked);
            assertEquals(headToTail(pile), walked);
        }

        @Test
        @DisplayName("getIterator on an empty pile has nothing to give")
        void iteratorOfEmptyPile() {
            assertFalse(new Pile().getIterator().hasNext());
        }

        @Test
        @DisplayName("getIterator starts at peekLastItem, the head, and ends at lastItem, the tail")
        void iteratorRunsFromHeadToTail() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            Iterator<ItemObject> it = pile.getIterator();
            ItemObject first = it.next();
            ItemObject second = it.next();

            assertSame(pile.peekLastItem(), first);
            assertSame(pile.lastItem(), second);
            assertFalse(it.hasNext());
        }

        @Test
        @DisplayName("reversed walks head first, as C does")
        void reversedWalksHeadFirst() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c);

            assertEquals(List.of(c, b, a), pile.reversed());
        }

        @Test
        @DisplayName("size counts the objects")
        void sizeCountsObjects() {
            Pile pile = new Pile();
            assertEquals(0, pile.size());
            pile.insert(new ItemObject());
            pile.insertEnd(new ItemObject());
            assertEquals(2, pile.size());
        }

        @Test
        @DisplayName("removeIf removes the object and clears its owner")
        void removeIfRemovesAndClearsOwner() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            pile.removeIf(a);

            assertEquals(List.of(b), headToTail(pile));
            assertNull(a.getOwningPile());
            assertSame(pile, b.getOwningPile());
        }

        @Test
        @DisplayName("removeIf ignores an object that is not in the pile and leaves its owner alone")
        void removeIfIgnoresAbsentObject() {
            ItemObject a = new ItemObject();
            ItemObject x = new ItemObject();
            Pile pile = pileOf(a);
            Pile other = pileOf(x);

            pile.removeIf(x);

            assertEquals(List.of(a), headToTail(pile));
            assertSame(other, x.getOwningPile());
        }

        @Test
        @DisplayName("remove(int) removes by position from the tail and clears the owner")
        void removeByIndex() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            ItemObject c = new ItemObject();
            Pile pile = pileOf(a, b, c);

            pile.remove(0);

            assertEquals(List.of(c, b), headToTail(pile));
            assertNull(a.getOwningPile());
            assertSame(b, pile.lastItem());
        }

        @Test
        @DisplayName("remove(int) rejects an index past the end")
        void removeByIndexOutOfRange() {
            Pile pile = pileOf(new ItemObject());

            assertThrows(IndexOutOfBoundsException.class, () -> pile.remove(1));
            assertEquals(1, pile.size());
        }

        @Test
        @DisplayName("clear empties the pile and clears every owner")
        void clearEmptiesAndClearsOwners() {
            ItemObject a = new ItemObject();
            ItemObject b = new ItemObject();
            Pile pile = pileOf(a, b);

            pile.clear();

            assertTrue(pile.isEmpty());
            assertNull(a.getOwningPile());
            assertNull(b.getOwningPile());
        }
    }

    /**
     * {@code hasArtifact}: true when any object in the pile has an artefact definition.
     */
    @Nested
    @DisplayName("hasArtifact")
    class HasArtifact {

        @Test
        @DisplayName("an empty pile has no artefact")
        void emptyPileHasNone() {
            assertFalse(new Pile().hasArtifact());
        }

        @Test
        @DisplayName("a pile of ordinary objects has no artefact")
        void ordinaryObjectsHaveNone() {
            assertFalse(pileOf(new ItemObject(), new ItemObject()).hasArtifact());
        }

        @Test
        @DisplayName("an artefact at the head is found")
        void findsArtifactAtTheHead() {
            ItemObject plain = new ItemObject();
            ItemObject artefact = new ItemObject();
            makeArtifact(artefact);

            assertTrue(pileOf(plain, artefact).hasArtifact());
        }

        @Test
        @DisplayName("an artefact at the tail is found")
        void findsArtifactAtTheTail() {
            ItemObject plain = new ItemObject();
            ItemObject artefact = new ItemObject();
            makeArtifact(artefact);

            assertTrue(pileOf(artefact, plain).hasArtifact());
        }
    }
}
