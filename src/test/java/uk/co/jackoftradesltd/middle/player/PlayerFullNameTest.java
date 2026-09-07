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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Player#getFullName} — the read half of C's {@code p->full_name}
 * ({@code player.h:571}, {@code char[PLAYER_NAME_LEN]}, 32 bytes including the terminator).
 *
 * <p>There is no {@code setFullName} in the port yet, so unlike {@link Player#getAge} or
 * {@link Player#getHeightBirth} this accessor cannot be exercised through a public write path —
 * the field is written here by reflection, the same way {@code PlayerStatValueAccessorTest} writes
 * fields {@link Player} has no setter for. What is worth pinning in the meantime is that the getter
 * answers from the field rather than from a computed or cached copy, and that the port's
 * {@code String} carries no trace of C's fixed-size buffer.
 *
 * <p>Class PlayerFullNameTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerFullNameTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * A new player, as the constructor leaves one.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Writes the player's private {@code fullName} field directly, standing in for the write path
     * the port does not have yet.
     *
     * @param name the name to store
     * @throws Exception if the field cannot be reached
     */
    private void setFullNameField(String name) throws Exception {
        Field field = Player.class.getDeclaredField("fullName");
        field.setAccessible(true);
        field.set(player, name);
    }

    /**
     * A new player has no name: the field is a reference type left at Java's default, matching the
     * intent behind C's zeroed struct even though C's version zeroes an actual buffer rather than
     * leaving a null pointer.
     */
    @Test
    @DisplayName("a new player has no name")
    void newPlayerHasNoName() {
        assertNull(player.getFullName());
    }

    /**
     * The ordinary path: whatever is stored in the field comes back unchanged.
     *
     * @throws Exception if the field cannot be reached
     */
    @Test
    @DisplayName("returns the stored name")
    void returnsStoredName() throws Exception {
        setFullNameField("Frodo");
        assertEquals("Frodo", player.getFullName());
    }

    /**
     * C's buffer holds 31 usable characters plus a terminator ({@code PLAYER_NAME_LEN} is 32,
     * {@code option.h:23}); {@code my_strcpy} truncates anything longer at every write site. The
     * port's {@code String} field has no such limit, so a name past that length must still come back
     * whole — this is the one place the wider Java type is a real behavioural difference from C
     * rather than a harmless widening, since a length-32-plus name would be silently cut in the
     * original and is not cut here.
     *
     * @throws Exception if the field cannot be reached
     */
    @Test
    @DisplayName("does not truncate a name past C's buffer length")
    void doesNotTruncateLongName() throws Exception {
        String longName = "A".repeat(64);
        setFullNameField(longName);
        assertEquals(64, player.getFullName().length());
        assertEquals(longName, player.getFullName());
    }

    /**
     * Writing twice keeps the second value, the shape every C writer's {@code my_strcpy} assumes:
     * the in-play rename command ({@code ui-player.c:1254}) and quickstart's roller restore
     * ({@code player-birth.c:214}) both overwrite a name set earlier in the same session.
     *
     * @throws Exception if the field cannot be reached
     */
    @Test
    @DisplayName("the last write wins")
    void lastWriteWins() throws Exception {
        setFullNameField("Bilbo");
        setFullNameField("Frodo");
        assertEquals("Frodo", player.getFullName());
    }
}
