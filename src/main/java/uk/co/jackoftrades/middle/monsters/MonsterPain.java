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

package uk.co.jackoftrades.middle.monsters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A set of "pain" messages for a class of monster (as loaded from {@code pain.txt})
 * — exactly seven messages describing reactions at decreasing health fractions,
 * keyed by an index that monster bases reference. This is the Java port of the C
 * original's {@code struct monster_pain} ({@code src/monster.h}).
 *
 * @author Rowan Crowther
 */
public class MonsterPain {
    /**
     * Logger used to report malformed pain definitions.
     *
     * @author Rowan Crowther
     */
    private static final Logger logger = LogManager.getLogger();

    /**
     * The index linking this pain set to the monster bases that use it.
     *
     * @author Rowan Crowther
     */
    private final int painIndex;
    /**
     * The seven ordered pain messages (most to least hurt).
     *
     * @author Rowan Crowther
     */
    private final List<String> messages;

    /**
     * Constructor for a Monster Pain class
     *
     * @param painIndex The index of this class used to link with the monster
     *                  base to ensure the right base calls the right pain message
     * @param messages  A list of exactly 7 error messages in a strict order;
     */
    public MonsterPain(int painIndex, @NotNull List<String> messages) {
        // Check to ensure that exactly 7 messages have come in
        if (messages.size() != 7) {
            String message = "Illegal number of messages found on Monster Pain type " + painIndex;
            IllegalStateException e = new IllegalStateException(message);
            logger.fatal(message, e);
            throw e;
        }

        this.painIndex = painIndex;
        this.messages = List.copyOf(messages);
    }

    /**
     * Getter for message
     *
     * @param level The pain level of message to get
     * @return The level'th entry in messages
     */
    public String getMessage(int level) {
        if (level < 0 || level > 6)
            throw new IllegalArgumentException("level must be between 0 and 6");

        return messages.get(level);
    }

    @Override
    @Contract(pure = true)
    public String toString() {
        StringBuilder result = new StringBuilder("MonsterPain{" +
                "painIndex=" + painIndex +
                ", messages=");

        for (String message : messages) {
            result.append("'").append(message).append("', ");
        }

        result.append("}");

        return result.toString();
    }

    /**
     * @return the index linking this pain set to its monster bases
     * @author Rowan Crowther
     */
    public int getPainIndex() {
        return painIndex;
    }
}
