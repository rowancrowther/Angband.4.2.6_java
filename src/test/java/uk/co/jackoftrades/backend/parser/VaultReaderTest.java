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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.cave.enums.RoomFlags;
import uk.co.jackoftrades.middle.cave.profiles.vault.Vault;
import uk.co.jackoftrades.middle.cave.roombuilders.RoomType;
import uk.co.jackoftrades.middle.game.globals.GameConstants;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput test for {@link VaultReader}: file text -> {@code VaultLexer}/
 * {@code VaultGrammar} -> {@code VaultParseRecord} -> {@code VaultAssembler} -> {@link Vault}.
 *
 * <p>Expected values are taken from {@code lib/gamedata/vault.txt} itself and from the C parser it
 * ports, {@code init_parse_vault} ({@code [C] src/generate.c:595}).
 *
 * @author Rowan Crowther
 */
class VaultReaderTest {

    private static final String REAL_FILE = "lib/gamedata/vault.txt";

    /**
     * vault.txt's {@code record-count:} header, and the number of {@code name:} records in it.
     */
    private static final int EXPECTED_VAULTS = 162;

    /**
     * How many records carry a {@code flags:} line; all 35 carry {@code FEW_ENTRANCES}.
     */
    private static final int EXPECTED_FLAGGED = 35;

    /**
     * How many records declare {@code max-depth:0}, meaning "no maximum"; the other 33 declare a
     * real cap.
     */
    private static final int EXPECTED_UNCAPPED = 129;

    /**
     * @param vaults the assembled vaults
     * @param name   the vault name to look for
     * @return the vault with that name
     * @throws java.util.NoSuchElementException if no vault has that name
     */
    private static Vault byName(List<Vault> vaults, String name) {
        return vaults.stream().filter(v -> v.getName().equals(name)).findFirst().orElseThrow();
    }

    /**
     * {@code max-depth:} is rewritten to the world's maximum depth when the file says 0, so the
     * assembler needs the game constants loaded.
     */
    @BeforeAll
    static void bootstrap() {
        GameConstants.init();
    }

    @Test
    void parsesEveryVaultWithoutError() throws Exception {
        ParseResult<Vault> result = new VaultReader().parseWithResults(REAL_FILE);

        assertTrue(result.errors().isEmpty(), () -> "unexpected errors: " + result.errors());
        assertEquals(EXPECTED_VAULTS, result.items().size());
    }

    @Test
    void readsTheFirstVaultsFields() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        Vault round = vaults.getFirst();
        assertEquals("Round", round.getName());
        assertEquals(RoomType.LESSER_VAULT, round.getType());
        assertEquals(5, round.getRating());
        assertEquals(12, round.getHeight());
        assertEquals(20, round.getWidth());
        assertEquals(0, round.getMinLevel());
        assertTrue(round.getFlags().isEmpty());
    }

    /**
     * C's {@code parse_vault_max_depth} turns a declared 0 into {@code z_info->max_depth}
     * ({@code [C] src/generate.c:561}), so no assembled vault may keep a 0 maximum.
     *
     * <p>129 of the 162 records declare 0 and so come out at the world maximum; the other 33
     * declare a real cap and must keep it untouched.
     */
    @Test
    void rewritesAZeroMaxDepthToTheWorldMaximum() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertNotEquals(0, vault.getMaxLevel(), vault.getName() + " kept a 0 maximum");
        }

        long atWorldMaximum = vaults.stream()
                .filter(v -> v.getMaxLevel() == GameConstants.getWorldMaxDepth())
                .count();
        assertEquals(EXPECTED_UNCAPPED, atWorldMaximum);

        Vault hatchery = byName(vaults, "Baby dragon hatchery -ES-");
        assertEquals(16, hatchery.getMaxLevel(), "a declared maximum must survive the rewrite");
        assertEquals(8, hatchery.getMinLevel());
    }

    /**
     * The layout is stored twice: as one line per row, and as the rows concatenated into the flat
     * string C keeps in {@code vault.text} and indexes as {@code text[y * wid + x]}. Both must agree
     * with the declared {@code rows:}/{@code columns:} - the port of the length check in
     * {@code parse_vault_d} ({@code [C] src/generate.c:588}).
     */
    @Test
    void everyLayoutMatchesItsDeclaredDimensions() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertEquals(vault.getHeight(), vault.getMap().size(), vault.getName() + " row count");
            for (String row : vault.getMap()) {
                assertEquals(vault.getWidth(), row.length(), vault.getName() + " row width");
            }
            assertEquals(vault.getHeight() * vault.getWidth(), vault.getMapLines().length(),
                    vault.getName() + " flat layout length");
        }
    }

    /**
     * No vault may be larger than the cap its room type carries from {@code list-rooms.h}, the
     * check C makes in {@code parse_vault_rows}/{@code parse_vault_columns}
     * ({@code [C] src/generate.c:521, 540}).
     */
    @Test
    void noVaultExceedsItsRoomTypeSizeCap() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertTrue(vault.getHeight() <= vault.getType().getMaxHeight(),
                    vault.getName() + " is taller than " + vault.getType().getName() + " allows");
            assertTrue(vault.getWidth() <= vault.getType().getMaxWidth(),
                    vault.getName() + " is wider than " + vault.getType().getName() + " allows");
        }
    }

    @Test
    void readsTheFlagsLines() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        long flagged = vaults.stream().filter(v -> !v.getFlags().isEmpty()).count();
        assertEquals(EXPECTED_FLAGGED, flagged);

        Vault cross = byName(vaults, "Cross");
        assertTrue(cross.getFlags().has(RoomFlags.FEW_ENTRANCES));
        assertEquals(1, cross.getFlags().count());
    }

    /**
     * Every vault type named in the file must resolve to a {@link RoomType}; C treats a name it
     * cannot match as {@code PARSE_ERROR_NO_ROOM_FOUND}.
     */
    @Test
    void resolvesEveryVaultType() throws Exception {
        List<Vault> vaults = new VaultReader().parse(REAL_FILE);

        for (Vault vault : vaults) {
            assertNotNull(vault.getType(), vault.getName());
        }
    }
}
