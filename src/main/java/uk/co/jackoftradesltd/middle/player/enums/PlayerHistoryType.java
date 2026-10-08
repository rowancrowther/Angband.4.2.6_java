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

package uk.co.jackoftradesltd.middle.player.enums;

/**
 * The kinds of entry the character history can hold — the port of C's anonymous history type enum
 * ({@code player-history.h}), whose constants are generated from the {@code HIST()} lines of
 * {@code list-history-types.h}.
 *
 * <p>In C an entry's {@code type} is a {@code bitflag} array sized {@code HIST_SIZE}, so one entry
 * can carry several types at once ({@code history_lose_artifact} adds
 * {@code HIST_ARTIFACT_LOST} to an existing artifact entry and leaves its other bits alone, and
 * when it has to create the entry it sets {@code HIST_ARTIFACT_UNKNOWN} and
 * {@code HIST_ARTIFACT_LOST} together). The Java counterpart is a
 * {@code Flag<PlayerHistoryType>} on {@code HistoryInfo}, which is why this is an enum and not a
 * set of {@code int} constants. The constants are kept in C's order and with C's numbering, so
 * {@code HIST_NONE} is zero and {@code HIST_MAX} is eleven; nothing in the port depends on the
 * ordinals, but a savefile carrying the bit array would.
 *
 * <p>Each constant carries the description string from {@code list-history-types.h}. C's
 * {@code HIST()} macro takes that string as its second argument but the enum expansion
 * ({@code #define HIST(a, b) HIST_##a,}) discards it, and no other C file expands the macro, so
 * nothing in C reads the text; it is carried here as documentation only.
 *
 * <p>Enum PlayerHistoryType coded before 261008, commented in full on 261008.
 *
 * @author Rowan Crowther
 */
public enum PlayerHistoryType {

    /**
     * No type; C's value 0. No C caller gives it to an entry. It is the first bit position in
     * the array, so it is a real (if unused) flag and not an "empty" marker.
     */
    HIST_NONE(""),
    /**
     * The character was born; C's value 1. Logged by {@code do_cmd_accept_character}
     * ({@code player-birth.c}) as "Began the quest to destroy Morgoth." straight after
     * {@code history_clear}.
     */
    HIST_PLAYER_BIRTH("Player was born"),
    /**
     * An artifact is in the log but not yet identified; C's value 2. Set by
     * {@code history_lose_artifact} when it has to create an entry for an artifact the player
     * never logged. It is cleared, with {@link #HIST_ARTIFACT_KNOWN} set in its place, by
     * {@code history_find_artifact} and by {@code history_unmask_unknown}, the latter run once
     * for the final character dump.
     */
    HIST_ARTIFACT_UNKNOWN("Player found but not IDd an artifact"),
    /**
     * An artifact has been identified; C's value 3. Set by {@code history_find_artifact},
     * which either upgrades an existing entry or adds a "Found ..." entry carrying only this
     * bit, and tested by {@code history_is_artifact_known}.
     */
    HIST_ARTIFACT_KNOWN("Player has IDed an artifact"),
    /**
     * An artifact was lost for good; C's value 4. Set by {@code history_lose_artifact}, on top
     * of the entry's other bits, and tested by {@code ui-history.c} to append " (LOST)" to the
     * entry in both the history screen and the character dump.
     */
    HIST_ARTIFACT_LOST("Player had an artifact and lost it"),
    /**
     * The character has died; C's value 5. No C source file uses it.
     */
    HIST_PLAYER_DEATH("Player has been slain"),
    /**
     * The character killed a unique monster; C's value 6. Logged as "Killed ..." from
     * {@code mon-util.c}.
     */
    HIST_SLAY_UNIQUE("Player has slain a unique monster"),
    /**
     * A note typed by the player; C's value 7. Logged by the note command in
     * {@code cmd-misc.c}.
     */
    HIST_USER_INPUT("User-added note"),
    /**
     * Added when a savefile from an older version is imported; C's value 8. No C source file
     * uses it.
     */
    HIST_SAVEFILE_IMPORT("Added when an older version savefile is imported"),
    /**
     * The character gained a level; C's value 9. Logged as "Reached level N" from
     * {@code player.c} when a level is gained verbosely.
     */
    HIST_GAIN_LEVEL("Player gained a level"),
    /**
     * Anything not covered above; C's value 10. The C comment marks it unused and no C
     * source file uses it.
     */
    HIST_GENERIC("Anything else not covered here (unused)"),
    /**
     * End-of-type marker, not a history type; C's value 11. C uses it only to size the
     * {@code type} bit array ({@code HIST_SIZE}); the Java {@code Flag} is backed by an
     * {@link java.util.EnumSet}, so the marker is carried for fidelity and the size is implicit.
     */
    HIST_MAX("");

    /**
     * The text from the second argument of C's {@code HIST()} line in
     * {@code list-history-types.h}. Empty for {@link #HIST_NONE} and {@link #HIST_MAX}.
     *
     * <p>Field description coded before 261008, commented in full on 261008.
     */
    private final String description;

    /**
     * Builds a constant with its {@code list-history-types.h} description.
     *
     * <p>Constructor coded before 261008, commented in full on 261008.
     *
     * @param description the C description string, verbatim
     */
    PlayerHistoryType(String description) {
        this.description = description;
    }

    /**
     * Returns the description C records alongside this type in {@code list-history-types.h}.
     * Nothing in the C source ever reads it, so no caller depends on it; it is an accessor for
     * display or debugging only.
     *
     * <p>Method getDescription coded before 261008, commented in full on 261008.
     *
     * @return the description, empty for {@link #HIST_NONE} and {@link #HIST_MAX}
     */
    public String getDescription() {
        return description;
    }
}
