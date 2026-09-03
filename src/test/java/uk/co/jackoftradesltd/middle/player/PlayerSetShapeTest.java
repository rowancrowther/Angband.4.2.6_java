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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Player#setShape(PlayerShape)} - the port of the plain field write C makes to
 * {@code p->shape} at each of its assignment sites: birth's
 * {@code lookup_player_shape("normal")} ({@code player-birth.c:457}), the shapechange effect
 * ({@code effect-handler-general.c:3453}), returning to normal form
 * ({@code player-util.c:1053}), and load restoring a save ({@code load.c:685}). None of those
 * sites validate or copy the shape being assigned, and these tests check that the port does the
 * same, following {@link PlayerSetOptionsTest}'s pattern for the sibling setter.
 *
 * <p>Class PlayerSetShapeTest coded on 260903, commented in full on 260903.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerSetShapeTest {

    /**
     * The character whose shape field is written, fresh for each test.
     */
    private Player player;

    /**
     * Builds a minimal named shape, the same construction {@link PlayerScalarStateTest} uses for
     * its own shape fixtures.
     *
     * @param name the shape's display name
     * @return a bare {@link PlayerShape} carrying that name and no other contribution
     */
    private static PlayerShape shape(String name) {
        return new PlayerShape(name, 0, 0, 0, Map.of(),
                new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                Map.of(), Map.of(), List.of(), 1, List.of());
    }

    /**
     * Builds a new character.
     */
    @BeforeEach
    void build() {
        player = new Player();
    }

    /**
     * The shape handed in is the shape held afterwards - the same reference, not a copy, exactly
     * as {@link Player#getShape} documents the field as pointing at the registry's own entry
     * rather than owning a private one.
     */
    @Test
    @DisplayName("stores the given shape by identity")
    void storesByIdentity() {
        PlayerShape bat = shape("bat");
        player.setShape(bat);
        assertSame(bat, player.getShape());
    }

    /**
     * A second call replaces the first shape outright, discarding it rather than merging into it
     * - C's {@code p->shape} assignment likewise always overwrites whatever the pointer held
     * before, whether that was another form or {@code "normal"}.
     */
    @Test
    @DisplayName("a second write replaces the first")
    void secondWriteReplaces() {
        PlayerShape first = shape("bat");
        PlayerShape second = shape("vampire");

        player.setShape(first);
        player.setShape(second);

        assertSame(second, player.getShape());
        assertNotSame(first, player.getShape());
    }

    /**
     * Writing {@code null} back over a held shape is accepted without complaint - the port never
     * validates the incoming value, matching C's unconditional field write. The field starts
     * {@code null} until birth is ported ({@link Player#getShape}), so returning to that state
     * from a shapechanged one has to work too.
     */
    @Test
    @DisplayName("a null write clears a previously set shape")
    void nullWriteClears() {
        player.setShape(shape("bat"));
        player.setShape(null);
        assertNull(player.getShape());
    }
}
