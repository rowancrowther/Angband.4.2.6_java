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

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Player#setOptions(PlayerOptions)} - the port of the plain field write C makes to
 * {@code p->opts} in {@code player_generate} ({@code player-birth.c:445}), which copies
 * {@code opts_save} back onto the freshly wiped player so options persist across a birth/restart.
 * That assignment does no validation or copying of the incoming settings, and these tests check
 * that the port does the same, following {@link PlayerTimedAndItemKnowledgeAccessorTest}'s pattern
 * for the sibling {@code setTimed}/{@code setItemKnowledge} pair.
 *
 * <p>Class PlayerSetOptionsTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerSetOptionsTest {

    /**
     * The character whose options field is written, fresh for each test.
     */
    private Player player;

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * The struct handed in is the struct held afterwards - the same reference, not a copy. C's
     * {@code p->opts = opts_save} is a struct copy in the original, but the Java field holds a
     * reference, so the port stores exactly the object it is given.
     */
    @Test
    @DisplayName("stores the given options by identity")
    void storesByIdentity() {
        PlayerOptions options = new PlayerOptions();
        player.setOptions(options);
        assertSame(options, player.getPlayerOptions());
    }

    /**
     * A second call replaces the first settings outright, discarding them rather than merging into
     * them - C's {@code p->opts} assignment in {@code player_generate} likewise overwrites whatever
     * the freshly wiped struct held.
     */
    @Test
    @DisplayName("a second write replaces the first")
    void secondWriteReplaces() {
        PlayerOptions first = new PlayerOptions();
        PlayerOptions second = new PlayerOptions();

        player.setOptions(first);
        player.setOptions(second);

        assertSame(second, player.getPlayerOptions());
        assertNotSame(first, player.getPlayerOptions());
    }
}
