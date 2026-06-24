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
 *    Java code copyright (c) Rowan Crowther 2026
 */

package uk.co.jackoftrades.middle.objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * A pile of objects is defined as a LIFO ArrayList of ItemObjects
 */
public class Pile {
    /**
     * Logger used to report pile integrity failures.
     *
     * @author ClaudeCode
     */
    private final static Logger logger = LogManager.getLogger();
    /**
     * The backing LIFO list of items.
     *
     * @author ClaudeCode
     */
    private ArrayList<ItemObject> pile;

    /**
     * Diagnostic snapshot of the pile that triggered an integrity failure.
     *
     * @author ClaudeCode
     */
    private static ArrayList<ItemObject> failPile;
    /**
     * Diagnostic snapshot of the object list around a failure.
     *
     * @author ClaudeCode
     */
    private static ArrayList<ItemObject> failObject;
    /**
     * Index of the offending object within {@link #failObject}.
     *
     * @author ClaudeCode
     */
    private static int failObjectIndex;

    /**
     * Source file recorded for an integrity failure (legacy; see {@link #integrityFail}).
     *
     * @author ClaudeCode
     */
    private static String failFile;
    /**
     * Source line recorded for an integrity failure (legacy).
     *
     * @author ClaudeCode
     */
    private static int failLine;

    /**
     * Build an empty pile.
     *
     * @author ClaudeCode
     */
    public Pile() {
        pile = new ArrayList<>();
        failPile = new ArrayList<>();
    }

    /**
     * Peek at the top object (last object added)
     *
     * @return the top object from the stack
     */
    @CheckReturnValue
    @Contract(pure = true)
    private ItemObject peek() {
        return pile.getLast();
    }

    /**
     * Checks to see if this pile is empty or not
     *
     * @return true if there are no items in this pile
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean isEmpty() {
        return pile.isEmpty();
    }

    /**
     * Return the top object (last object added) and remove it from the stack
     *
     * @return the top object from the stack
     */
    @CheckReturnValue
    private ItemObject pop() {
        return pile.removeLast();
    }

    /**
     * Push a new object onto the stack
     *
     * @param item the object to put on the top
     */
    private void push(@NotNull ItemObject item) {
        pile.addLast(item);
    }

    /**
     * Write the current pile to the fatal log file, as this is a fatal error.
     */
    @Contract(pure = true)
    private void writePile() {
        StringBuilder result = new StringBuilder();

        result.append("Pile integrity failure at ").append(failFile).append(":").append(failLine).append("\n\n")
                .append("Guilty object\n=============\n");

        ItemObject item = failObject.get(failObjectIndex);

        if (item != null && item.getKind() != null) {
            result.append("Name: ").append(item.getKind().getName()).append("\n");

            if (failObjectIndex >= 0) {
                result.append("Previous: ");
                ItemObject prev = failObject.get(failObjectIndex - 1);
                if (prev != null && prev.getKind() != null)
                    result.append(prev.getKind().getName()).append("\n");
                else
                    result.append("bad object/n");
            }

            if (failObjectIndex < failObject.size() - 1) {
                result.append("Next: ");
                ItemObject next = failObject.get(failObjectIndex + 1);
                if (next != null && next.getKind() != null)
                    result.append(next.getKind().getName()).append("\n");
                else
                    result.append("bad object/n");
            }

            result.append("\n");
        }

        if (failPile != null) {
            result.append("Guilty pile\n===========\n");
            for (ItemObject object : failPile) {
                if (object.getKind() != null)
                    result.append("Name: ").append(object.getKind().getName()).append("\n");
                else
                    result.append("bad object/n");
            }
        }

        logger.fatal(result.toString());

    }

    /**
     * Deal with an integrity fail<br/><br/>
     * Note: This is unlikely to ever be needed in java, and the use of filename and line are not going to work, as
     * we are not parsing the files line by line but by using ANTLR4
     *
     * @param item     the object which is causing a fail
     * @param fileName the file we read the object from
     * @param line     the line in the file we read the object from
     */
    private void integrityFail(@NotNull ItemObject item, @NotNull String fileName, int line) {
        failPile = this.pile;

        failObjectIndex = 0;
        for (ItemObject object : failPile) {
            if (item.equals(object)) break;
            failObjectIndex++;
        }

        if (failObjectIndex >= failPile.size()) {
            logger.fatal("Object {} not found in pile despite causing a pile integrity error.", item.getKind().getName());
            System.exit(-1);
        }

        failFile = fileName;
        failLine = line;

        writePile();
        System.exit(-1);
    }

    /**
     * Push an object onto the top of the stack
     *
     * @param item The object to push
     */
    public void insert(@NotNull ItemObject item) {
        push(item);
    }

    /**
     * Inserts a new object at the bottom of the stack
     *
     * @param item the object to insert
     */
    public void insertEnd(@NotNull ItemObject item) {
        pile.addFirst(item);
    }

    /**
     * Returns whether the pile has an artefact in it or not.
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
     * Remove an object from the pile
     *
     * @param item the object to remove
     */
    public void excise(@NotNull ItemObject item) {
        pile.remove(item);
    }

    /**
     * This is a FILO stack, so return the top item being the last item to be added
     *
     * @return the last item to be added to this stack
     */
    @CheckReturnValue
    public ItemObject lastItem() {
        return pop();
    }

    /**
     * Check to see if an object is in this pile's stack
     *
     * @param item the object to look for
     * @return true if the object exists in the stack
     */
    @Contract(pure = true)
    @CheckReturnValue
    public boolean contains(@NotNull ItemObject item) {
        return pile.contains(item);
    }

    /**
     * Get an iterator to allow stepping through the pile
     *
     * @return an iterator of type ItemObject
     */
    @CheckReturnValue
    @Contract(pure = true)
    public Iterator<ItemObject> getIterator() {
        return pile.iterator();
    }

    /**
     * Test-only helper that empties the pile.
     *
     * @author ClaudeCode
     */
    @TestOnly
    public void clear() {
        pile.clear();
    }
}