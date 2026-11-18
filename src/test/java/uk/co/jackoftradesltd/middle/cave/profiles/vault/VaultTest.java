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

package uk.co.jackoftradesltd.middle.cave.profiles.vault;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.enums.RoomFlags;
import uk.co.jackoftradesltd.middle.cave.roombuilders.RoomType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Vault} holder, the port of C's {@code struct vault} in
 * {@code generate.h}.
 *
 * <p>{@code Vault} has no behaviour of its own, so these pin the boundary it owns: every field of
 * the C struct round-trips through the constructor and accessors untouched, and the two layout
 * shapes index the way C's {@code text[y * wid + x]} does. The parse-and-validate behaviour is
 * covered end to end in {@code VaultReaderTest}.
 *
 * <p>Layout values come from the first record of {@code lib/gamedata/vault.txt}, "Round".
 *
 * @author Rowan Crowther
 */
class VaultTest {

    /**
     * A 3x4 layout (rows x columns) with a padded row, as the data file writes them.
     */
    private static final List<String> ROWS = List.of("####", "#. #", "####");

    private static final String FLAT = "####" + "#. #" + "####";

    private static Vault vault(Flag<RoomFlags> flags, int minLevel, int maxLevel) {
        return new Vault("Round", RoomType.LESSER_VAULT, FLAT, ROWS, flags, 5, 3, 4, minLevel, maxLevel);
    }

    /**
     * Every field of C's {@code struct vault} (bar the {@code next} link) comes back as given.
     */
    @Test
    void everyFieldRoundTrips() {
        Flag<RoomFlags> flags = new Flag<>(RoomFlags.class, RoomFlags.FEW_ENTRANCES);

        Vault v = vault(flags, 8, 16);

        assertEquals("Round", v.getName());
        assertEquals(RoomType.LESSER_VAULT, v.getType());
        assertEquals(FLAT, v.getMapLines());
        assertEquals(ROWS, v.getMap());
        assertSame(flags, v.getFlags());
        assertEquals(5, v.getRating());
        assertEquals(3, v.getHeight());
        assertEquals(4, v.getWidth());
        assertEquals(8, v.getMinLevel());
        assertEquals(16, v.getMaxLevel());
    }

    /**
     * {@code min-depth:0} is already the right floor, so the holder keeps a zero minimum as is.
     */
    @Test
    void aZeroMinimumIsKept() {
        assertEquals(0, vault(new Flag<>(RoomFlags.class), 0, 127).getMinLevel());
    }

    /**
     * An empty flag set stays empty rather than becoming {@code null}.
     */
    @Test
    void noFlagsMeansAnEmptySetNotNull() {
        Vault v = vault(new Flag<>(RoomFlags.class), 0, 127);

        assertNotNull(v.getFlags());
        assertTrue(v.getFlags().isEmpty());
    }

    /**
     * C indexes the flat layout as {@code text[y * wid + x]}; the per-row list must give the same
     * character at every cell, including the padded space.
     */
    @Test
    void flatAndPerRowLayoutsAgreeCellForCell() {
        Vault v = vault(new Flag<>(RoomFlags.class), 0, 127);

        for (int y = 0; y < v.getHeight(); y++) {
            for (int x = 0; x < v.getWidth(); x++) {
                assertEquals(v.getMapLines().charAt(y * v.getWidth() + x),
                        v.getMap().get(y).charAt(x), "cell " + x + "," + y);
            }
        }
        assertEquals(' ', v.getMapLines().charAt(1 * v.getWidth() + 2), "padding is kept");
    }

    /**
     * The holder stores {@code uint8_t}-range overflow as given. C narrows {@code rat},
     * {@code hgt}, {@code wid}, {@code min_lev} and {@code max_lev} to 8 bits; the narrowing is the
     * assembler's job here, so 300 must come back as 300, not 44.
     */
    @Test
    void valuesPastTheUint8RangeAreNotWrapped() {
        Vault v = new Vault("Big", RoomType.LESSER_VAULT, FLAT, ROWS,
                new Flag<>(RoomFlags.class), 300, 300, 300, 300, 300);

        assertEquals(300, v.getRating());
        assertEquals(300, v.getHeight());
        assertEquals(300, v.getWidth());
        assertEquals(300, v.getMinLevel());
        assertEquals(300, v.getMaxLevel());
    }

    /**
     * C's {@code parse_vault_max_depth} turns {@code max-depth:0} into {@code z_info->max_depth};
     * the holder does not, so a 0 handed to it comes back as 0.
     */
    @Test
    void aZeroMaximumIsNotRewrittenByTheHolder() {
        assertEquals(0, vault(new Flag<>(RoomFlags.class), 0, 0).getMaxLevel());
    }

    /**
     * The constructor does no checking, so an empty vault (as the assembler begins with) holds its
     * nulls and zeros.
     */
    @Test
    void anEmptyVaultKeepsNullsAndZeros() {
        Vault v = new Vault(null, null, null, new java.util.ArrayList<>(), null, 0, 0, 0, 0, 0);

        assertNull(v.getName());
        assertNull(v.getType());
        assertNull(v.getMapLines());
        assertTrue(v.getMap().isEmpty());
        assertNull(v.getFlags());
        assertEquals(0, v.getRating());
        assertEquals(0, v.getHeight());
        assertEquals(0, v.getWidth());
        assertEquals(0, v.getMinLevel());
        assertEquals(0, v.getMaxLevel());
    }

    /**
     * Each setter replaces its own field and leaves the other nine alone.
     */
    @Test
    void eachSetterReplacesOnlyItsOwnField() {
        Flag<RoomFlags> flags = new Flag<>(RoomFlags.class, RoomFlags.FEW_ENTRANCES);
        Flag<RoomFlags> otherFlags = new Flag<>(RoomFlags.class, RoomFlags.MAX);
        List<String> otherRows = List.of("##", "##");

        Vault v = vault(flags, 8, 16);
        v.setName("Other");
        assertEquals("Other", v.getName());
        assertEquals(RoomType.LESSER_VAULT, v.getType());
        assertEquals(5, v.getRating());

        v.setType(RoomType.GREATER_VAULT);
        assertEquals(RoomType.GREATER_VAULT, v.getType());
        assertEquals(3, v.getHeight(), "no re-check against the new type");

        v.setMapLines("####");
        assertEquals("####", v.getMapLines());
        assertEquals(ROWS, v.getMap(), "map is not kept in step with mapLines");

        v.setMap(otherRows);
        assertSame(otherRows, v.getMap());
        assertEquals("####", v.getMapLines());

        v.setFlags(otherFlags);
        assertSame(otherFlags, v.getFlags());

        v.setRating(9);
        v.setHeight(2);
        v.setWidth(2);
        v.setMinLevel(1);
        v.setMaxLevel(40);
        assertEquals(9, v.getRating());
        assertEquals(2, v.getHeight());
        assertEquals(2, v.getWidth());
        assertEquals(1, v.getMinLevel());
        assertEquals(40, v.getMaxLevel());
        assertEquals("Other", v.getName());
    }
}
