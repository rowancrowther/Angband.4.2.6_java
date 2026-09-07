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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link Player#getFullName} and {@link Player#setFullName} — the read and write halves of
 * C's {@code p->full_name} ({@code player.h:571}, {@code char[PLAYER_NAME_LEN]}, 32 bytes including
 * the terminator).
 *
 * <p>{@code setFullName} is the port's stand-in for C's {@code my_strcpy(player->full_name, src,
 * sizeof(player->full_name))}: since the port's {@code String} has no fixed-size buffer to
 * overflow, the truncation C gets for free from the buffer copy is done explicitly with
 * {@code Math.min(31, name.length())}. The boundary that matters is C's — {@code my_strcpy} only
 * shortens a string once its length reaches the 32-byte buffer, so 31 characters survive untouched
 * and 32 or more are cut to the first 31.
 *
 * <p>Class PlayerFullNameTest coded on 260906, commented in full on 260906, updated on 260907 when
 * {@link Player#setFullName} gained a write path and the truncation tests were rewritten to match
 * it.
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
     * The ordinary path: a name well under the 31-character cutover comes back unchanged, the same
     * as C's {@code my_strcpy} leaves a short string untouched.
     */
    @Test
    @DisplayName("returns the stored name")
    void returnsStoredName() {
        player.setFullName("Frodo");
        assertEquals("Frodo", player.getFullName());
    }

    /**
     * The empty string is the low edge of {@code my_strcpy}'s copy - {@code strlen} is 0, nothing is
     * copied, and the buffer ends up empty rather than untouched or null.
     */
    @Test
    @DisplayName("an empty name is stored as empty, not truncated or null")
    void emptyNameIsStoredAsEmpty() {
        player.setFullName("");
        assertEquals("", player.getFullName());
    }

    /**
     * The high edge of the untouched side of {@code my_strcpy}'s cutover: with
     * {@code PLAYER_NAME_LEN} at 32 ({@code option.h:23}), a 31-character name has
     * {@code strlen(src) == 31 < bufsize}, so C copies it whole. {@code Math.min(31, 31)} picks the
     * same 31 characters, so nothing is lost.
     */
    @Test
    @DisplayName("a 31-character name is stored whole")
    void thirtyOneCharacterNameIsNotTruncated() {
        String name = "A".repeat(31);
        player.setFullName(name);
        assertEquals(name, player.getFullName());
    }

    /**
     * The low edge of the truncated side: at 32 characters, {@code strlen(src) == bufsize}, so
     * {@code my_strcpy} keeps only the first 31 and drops the last. This is the length past which
     * the port's earlier {@code substring(0, 31)} attempt would have thrown instead of truncating -
     * the case that regression would have gotten right by accident.
     */
    @Test
    @DisplayName("a 32-character name is truncated to 31 characters")
    void thirtyTwoCharacterNameIsTruncatedToThirtyOne() {
        String name = "A".repeat(31) + "B";
        player.setFullName(name);
        assertEquals("A".repeat(31), player.getFullName());
    }

    /**
     * Well past the buffer, {@code my_strcpy} still only keeps the first 31 characters of whatever
     * it is given; a 64-character name should come back cut to the same first 31, not to its own
     * length minus one or left whole the way the pre-fix port would have.
     */
    @Test
    @DisplayName("a name far longer than the buffer is truncated to its first 31 characters")
    void longNameIsTruncatedToFirstThirtyOneCharacters() {
        String name = "Frodo Baggins of Bag End, Shire, Middle-earth, Eriador";
        player.setFullName(name);
        assertEquals(31, player.getFullName().length());
        assertEquals(name.substring(0, 31), player.getFullName());
    }

    /**
     * Writing twice keeps the second value, the shape every C writer's {@code my_strcpy} assumes:
     * the in-play rename command ({@code ui-player.c:1254}) and quickstart's roller restore
     * ({@code player-birth.c:214}) both overwrite a name set earlier in the same session.
     */
    @Test
    @DisplayName("the last write wins")
    void lastWriteWins() {
        player.setFullName("Bilbo");
        player.setFullName("Frodo");
        assertEquals("Frodo", player.getFullName());
    }
}
