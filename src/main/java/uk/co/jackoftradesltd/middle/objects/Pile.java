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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A pile of objects: the port of the doubly linked list of {@code struct object} that C threads
 * through {@code obj->prev} and {@code obj->next} and passes around as a {@code struct object *}
 * head pointer ({@code obj-pile.c}). The floor of a grid, the player's gear and the known gear
 * are all piles.
 *
 * <p><b>Layout.</b> The backing list is held backwards. C's head, the object most recently put at
 * the front by {@code pile_insert}, is the <em>last</em> index here, and C's tail, where
 * {@code pile_insert_end} appends, is index 0. This is why {@link #insert} adds at the end of the
 * list and {@link #insertEnd(ItemObject)} adds at the front, and it fixes the meaning of two names
 * that look alike:
 *
 * <ul>
 *   <li>{@link #lastItem()} is C's {@code pile_last_item}: the <em>tail</em>, index 0, which is the
 *       oldest object added with {@code insert}.</li>
 *   <li>{@link #peekLastItem()} is the <em>head</em>, the last index, which is what C's
 *       {@code square_object} returns.</li>
 * </ul>
 *
 * <p><b>Ownership.</b> Each {@link ItemObject} records the pile it sits in
 * ({@link ItemObject#getOwningPile()}). That record stands in for the {@code prev}/{@code next}
 * checks C makes before it links an object in or unlinks it, and an {@code ArrayList} cannot hold
 * a bad link or a loop, so C's {@code pile_check_integrity} has nothing to check and is not
 * ported. Where C calls {@code pile_integrity_fail} and quits after writing {@code pile_error.txt},
 * this class logs and throws a {@link RuntimeException}; the diagnostic file ({@code write_pile})
 * is not ported. Membership tests compare identity, as C compares pointers, because
 * {@link ItemObject} does not override {@code equals}.
 *
 * <p>Only {@link #hasArtifact()} and the index and bulk helpers at the foot of the class
 * ({@link #size()}, {@link #get(int)}, {@link #reversed()}, {@link #removeIf(ItemObject)},
 * {@link #remove(int)}) have no function of their own in {@code obj-pile.c}; they exist because
 * Java callers cannot follow {@code obj->next}.
 *
 * <p>Class Pile coded before 260905, commented in full on 261009.
 */
public class Pile {
    /**
     * Logger used to report pile integrity failures before they are thrown.
     *
     * <p>Field logger coded before 260905, commented in full on 261009.
     */
    private final static Logger logger = LogManager.getLogger();
    /**
     * The backing list of items, held backwards: index 0 is C's tail and the last index is C's
     * head (see the class description). The list is owned by this pile alone and is not exposed
     * directly.
     *
     * <p>Field pile coded before 260905, commented in full on 261009.
     */
    private List<ItemObject> pile;

    /**
     * Builds an empty pile, the port of C's {@code NULL} head pointer.
     *
     * <p>Constructor Pile coded before 260905, commented in full on 261009.
     */
    public Pile() {
        pile = new ArrayList<>();
    }

    /**
     * Reads C's head of the pile, the last index, without removing it. {@link #peekLastItem()}
     * is the public route to it.
     *
     * <p>The list is not checked for emptiness, so an empty pile throws
     * {@link NoSuchElementException} from {@code getLast()}; C's head pointer would be
     * {@code NULL}. Callers test {@link #isEmpty()} first.
     *
     * <p>Method peek coded before 260905, commented in full on 261009.
     *
     * @return the head object, the one most recently added by {@link #insert}
     * @throws NoSuchElementException if the pile is empty
     */
    @CheckReturnValue
    @Contract(pure = true)
    private ItemObject peek() {
        return pile.getLast();
    }

    /**
     * Tests whether the pile holds no objects, the port of C's {@code *pile == NULL} test on a
     * head pointer.
     *
     * <p>Method isEmpty coded before 260905, commented in full on 261009.
     *
     * @return true if there are no items in this pile
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isEmpty() {
        return pile.isEmpty();
    }

    /**
     * Puts an object at C's head of the pile, the last index, and records this pile as its owner.
     *
     * <p>This does no precondition check; {@link #insert} makes the {@code pile_insert} check
     * before it calls here.
     *
     * <p>Method push coded before 260905, commented in full on 261009.
     *
     * @param item the object to put at the head
     */
    private void push(@NotNull ItemObject item) {
        item.setOwningPile(this);
        pile.addLast(item);
    }

    /**
     * Inserts a new object at the top of the stack.
     *
     * <p>Ports C's {@code pile_insert} ({@code obj-pile.c}): the newest item becomes the pile's
     * head there, which corresponds to this pile's last (top) index, so {@link #push(ItemObject)}
     * is the correct match. C guards the precondition by checking {@code obj->prev || obj->next} —
     * an approximation that misses an {@code obj} that is the sole element of some other list, since
     * a singleton has null prev and next either way. This port tracks ownership directly via
     * {@link ItemObject#getOwningPile()}, so the check here catches that case too rather than
     * missing it.
     *
     * <p>Method insert commented in full on 260905.
     *
     * @param item the object to insert; must not already belong to a pile
     * @throws RuntimeException if {@code item} already belongs to a pile
     */
    public void insert(@NotNull ItemObject item) {
        if (item.getOwningPile() != null) {
            String message = "Pile integrity failure";
            logger.fatal(message);
            throw new RuntimeException(message);
        }

        item.setOwningPile(this);
        push(item);
    }

    /**
     * Inserts a new object at the tail of the pile, index 0 of the backing list.
     *
     * <p>Ports C's {@code pile_insert_end} ({@code obj-pile.c}) for a single object: C walks to
     * the last object with {@code pile_last_item} and links the new one on after it, which is
     * {@code addFirst} on this class's backwards list. C's guard is {@code obj->prev}, so it lets
     * through an object that heads a chain of its own, which is how {@code wield_all} appends a
     * whole list at once. That chain case is {@link #insertEnd(Pile)}; this method is stricter
     * and rejects any object that already has an owner, including a sole member of another pile.
     * An object that C would reject because it sits mid-list is rejected here too.
     *
     * <p>Method insertEnd coded before 260905, commented in full on 261009.
     *
     * @param item the object to insert; must not already belong to a pile
     * @throws RuntimeException if {@code item} already belongs to a pile, this one or another
     */
    public void insertEnd(@NotNull ItemObject item) {
        if (item.getOwningPile() != null) {
            logger.error("Pile integrity failure");
            throw new RuntimeException("Pile integrity failure");
        }
        
        item.setOwningPile(this);
        pile.addFirst(item);
    }

    /**
     * Appends the whole of another pile at the tail of this one, keeping its internal order.
     *
     * <p>Ports C's {@code pile_insert_end} ({@code obj-pile.c}) in the case its header comment
     * describes, where {@code obj} is the beginning of a new list. {@code wield_all}
     * ({@code player-birth.c}) uses it to add the split-off objects to {@code gear} and
     * {@code gear_k}. C's order afterwards, head to tail, is this pile and then {@code items}. On
     * this class's backwards list that puts {@code items} in front of the existing elements, so the
     * loop walks {@code items} from its head (last index) to its tail (index 0) and pushes each
     * through {@link #insertEnd(ItemObject)}, which leaves {@code items}' own tail at index 0 of
     * the result. Each object's owner is cleared first, because the single-object method rejects
     * an owned object, and is set to this pile by that method.
     *
     * <p>The source pile is spent afterwards. It still lists the objects, as C's head pointer
     * still points at the chain once it is joined on, but they now belong to this pile, so
     * {@code items.excise(...)} would throw. Appending a pile to itself throws, where C would
     * produce a circular list and fail its integrity check. An empty {@code items} changes nothing.
     *
     * <p>Method insertEnd(Pile) coded and reworked on 261009, commented in full on 261009.
     *
     * @param items the pile to append; must not be this pile
     * @throws RuntimeException if {@code items} is this pile
     */
    public void insertEnd(@NotNull Pile items) {
        if (this == items) {
            logger.error("Pile integrity failure");
            throw new RuntimeException("Pile integrity failure");
        }
        
        for (ItemObject object : items.pile.reversed()) {
            object.setOwningPile(null);
            insertEnd(object);
        }
    }

    /**
     * Tests whether any object in the pile is an artefact.
     *
     * <p>{@code obj-pile.c} has no function of this name. The helper answers whether any object
     * has its {@code artifact} field set, and the order of the walk does not matter to the answer.
     *
     * <p>Method hasArtifact coded before 260905, commented in full on 261009.
     *
     * @return true if one of the objects in the pile is an artefact
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean hasArtifact() {
        for (ItemObject object : pile) {
            if (object.isArtifact()) return true;
        }
        return false;
    }

    /**
     * Removes an object from this pile and clears its owner.
     *
     * <p>Ports C's {@code pile_excise} ({@code obj-pile.c}). C first calls {@code pile_contains}
     * and fails the integrity check if the object is not in the pile; here the owner test stands
     * in for that, and a failure throws before the object's owner is touched, so an object that
     * belongs to another pile keeps its owner. C's checks that the head has no {@code prev} and
     * that a non-head has one cannot arise in a list, and the {@code prev}/{@code next} relinking
     * is done by the list itself.
     *
     * <p>Method excise coded before 260905, commented in full on 261009.
     *
     * @param item the object to remove; must belong to this pile
     * @throws RuntimeException if {@code item} does not belong to this pile
     */
    public void excise(@NotNull ItemObject item) {
        if (item.getOwningPile() != this) {
            logger.fatal("Pile integrity failure");
            throw new RuntimeException("Pile integrity failure");
        }
        item.setOwningPile(null);
        pile.remove(item);
    }

    /**
     * Returns the tail of the pile, C's last item, without removing it.
     *
     * <p>Ports C's {@code pile_last_item} ({@code obj-pile.c}), which runs down {@code obj->next}
     * to the end of the list and returns {@code NULL} for no pile. The tail is index 0 of the
     * backing list, so this is the <em>oldest</em> object added with {@link #insert}, the opposite
     * end from {@link #peekLastItem()}. An empty pile returns {@code null}; the method is not
     * annotated {@code @Nullable}.
     *
     * <p>Method lastItem coded before 260905, commented in full on 261009.
     *
     * @return the tail object, or {@code null} if the pile is empty
     */
    @CheckReturnValue
    @Nullable
    public ItemObject lastItem() {
        try {
            return pile.getFirst();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * Tests whether an object is in this pile.
     *
     * <p>Ports C's {@code pile_contains} ({@code obj-pile.c}), which compares pointers while it
     * walks down {@code obj->next}. {@link ItemObject} does not override {@code equals}, so the
     * list's {@code contains} compares identity too. It searches the list, not the object's owner
     * field, so it does not depend on the ownership record being right.
     *
     * <p>Method contains coded before 260905, commented in full on 261009.
     *
     * @param item the object to look for
     * @return true if the object is in this pile
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean contains(@NotNull ItemObject item) {
        return pile.contains(item);
    }

    /**
     * Returns an iterator over the pile from the <em>head</em> (the last index) to the
     * <em>tail</em> (index 0).
     *
     * <p>That is the order of C's walk, {@code for (obj = pile; obj; obj = obj->next)}, so a
     * caller whose result depends on the order, such as one that stops at the first match or that
     * numbers the objects, sees the objects as C does. The iterator is the one {@link #reversed()}
     * gives, so it is a live view of the pile's own list. It supports {@code remove()}, which
     * unlinks an object without clearing its owner. {@link #get(int)} and {@link #remove(int)}
     * count from the tail, so they run the other way.
     *
     * <p>Method getIterator coded before 260905, order changed to head first and commented in
     * full on 261009.
     *
     * @return an iterator of type ItemObject, head first
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Iterator<ItemObject> getIterator() {
        return pile.reversed().iterator();
    }

    /**
     * Test-only helper that empties the pile, clearing the owner of every object it held.
     *
     * <p>{@code obj-pile.c} has no function of this name.
     *
     * <p>Method clear coded before 260905, commented in full on 261009.
     */
    @TestOnly
    public void clear() {
        for (ItemObject object : pile) {
            object.setOwningPile(null);
        }
        pile.clear();
    }

    /**
     * Counts the objects in the pile, replacing the loop C writes down {@code obj->next}.
     *
     * <p>Method size coded before 260905, commented in full on 261009.
     *
     * @return the number of objects in the pile
     */
    public int size() {
        return pile.size();
    }

    /**
     * Reads the object at a position in the backing list, without removing it.
     *
     * <p>The index counts from the tail: 0 is C's last item and {@code size() - 1} is C's head,
     * so C's "n-th object from the head" is {@code size() - 1 - n} here. An out-of-range index
     * throws {@link IndexOutOfBoundsException}.
     *
     * <p>Method get coded before 260905, commented in full on 261009.
     *
     * @param index the position, counted from the tail
     * @return the object at that position
     */
    public ItemObject get(int index) {
        return pile.get(index);
    }

    /**
     * Returns the pile's objects in C's order, head first, as a reverse view of the backing list.
     *
     * <p>This is the order of C's {@code for (obj = pile; obj; obj = obj->next)} walk. It is a
     * live view of the pile's own list, not a copy, so changing the pile while iterating it
     * is unsafe.
     *
     * <p>Method reversed coded before 260905, commented in full on 261009.
     *
     * @return the pile's objects, head first
     */
    public List<ItemObject> reversed() {
        return pile.reversed();
    }

    /**
     * Removes one specific object from the pile, found by identity, and clears its owner.
     *
     * <p>Despite the name this takes an object, not a predicate. It is the lenient sibling of
     * {@link #excise}, standing in for {@code pile_excise} where the caller does not know the
     * object is present: an object not in the pile is ignored and no error is raised, where C
     * would fail its integrity check. The owner is cleared only for a match.
     *
     * <p>Method removeIf coded before 260905, commented in full on 261009.
     *
     * @param obj the object to remove; compared by identity
     */
    public void removeIf(ItemObject obj) {
        pile.stream().filter(i -> i == obj).forEach(i -> i.setOwningPile(null));
        pile.removeIf(item -> item == obj);
    }

    /**
     * Removes the object at a position in the backing list and clears its owner.
     *
     * <p>The index counts from the tail, as in {@link #get(int)}. It is {@link #excise} for a
     * caller that holds a position rather than the object, and, unlike {@code excise}, it makes
     * no ownership check. An out-of-range index throws {@link IndexOutOfBoundsException}.
     *
     * <p>Method remove coded before 260905, commented in full on 261009.
     *
     * @param index the position, counted from the tail
     */
    public void remove(int index) {
        ItemObject item = pile.get(index);
        item.setOwningPile(null);
        pile.remove(index);
    }

    /**
     * Returns the head of the pile, C's first object, without removing it.
     *
     * <p>This is what C's {@code square_object} returns for a grid, the last index of the backing
     * list and the newest object added with {@link #insert}; compare {@link #lastItem()}, the
     * opposite end.
     *
     * <p>Method peekLastItem coded before 260905, commented in full on 261009.
     *
     * @return the head object
     * @throws NoSuchElementException if the pile is empty
     */
    public ItemObject peekLastItem() {
        return peek();
    }
}