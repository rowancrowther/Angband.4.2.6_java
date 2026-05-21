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

package uk.co.jackoftrades.backend.strings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.backend.AngbandModule;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Quark implements AngbandModule {
    private String name;
    private Map<Integer, String> quarks;
    private final Logger logger = LogManager.getLogger();

    /**
     * Add a string to this set of quarks and return a unique value to it
     *
     * @param string The string to add
     * @return a unique key for it where the key value is 1 higher than the previous highest value in this map.
     * This allows deletions from the middle of the map without looking at the deleted position by accident in the
     * future.
     * @throws NullPointerException if this collection of quarks is null
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public int add(@NotNull String string) throws NullPointerException {
        if (quarks == null) {
            String message = "Quark is null. String '" + string + "' cannot be added to a null quark.";
            logger.error(message);
            throw new NullPointerException(message);
        }

        int i = quarks.size();

        for (int index : quarks.keySet()) {
            if (index > i) i = index;
        }

        quarks.put(i + 1, string);
        return i + 1;
    }

    /**
     * Merge a set of quarks from one another Quark into this one
     *
     * @param other the other Quark to merge this one with
     */
    @CheckReturnValue
    @Contract(mutates = "this")
    public void merge(Quark other) {
        // if the other is null or has no entries, just return
        if (other == null || other.size() == 0) return;

        // Work out the offset for quarks in this set
        int offset = Collections.max(quarks.keySet()) + 1;

        // Insert the new quarks using their existing keys plus the offset
        for (Map.Entry<Integer, String> entry : other.quarks.entrySet()) {
            quarks.put(entry.getKey() + offset, entry.getValue());
        }
    }

    /**
     * Gets the value of the quark with given quark key
     *
     * @param key the key of the string we wish to obtain from the map
     * @return if the key is a valid key (contained in the key set), then return the appropriate string - otherwise
     * return null.
     * @throws NullPointerException when this quark hasn't been initiated yet
     */
    @CheckReturnValue
    @Contract(pure = true)
    public String getQuark(int key) throws NullPointerException {
        if (quarks == null) {
            String message = "Quark is null. Key '" + key + "' will not be present.";
            logger.error(message);
            throw new NullPointerException(message);
        }

        if (quarks.containsKey(key)) return quarks.get(key);
        return null;
    }

    /**
     * Get the number of quarks in the list
     *
     * @return The number of quarks in this quark list
     */
    @CheckReturnValue
    @Contract(pure = true)
    public int size() {
        return quarks.size();
    }

    /**
     * @return the name of this module
     */
    @Override
    @CheckReturnValue
    @Contract(pure = true)
    public String getName() {
        return name;
    }

    /**
     * Initialize this module
     */
    @Contract(mutates = "this")
    @Override
    public void init() {
        quarks = new LinkedHashMap<>();
        name = "Quark";
    }

    /**
     * Clean up set the linked hash map to null to allow for recycling
     */
    @Contract(mutates = "this")
    @Override
    public void cleanup() {
        quarks = null;
    }
}