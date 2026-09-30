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

package uk.co.jackoftradesltd.middle.cave;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link TrapKind#lookupTrap(String)} against {@code lookup_trap()} in {@code trap.c}.
 *
 * <p>The fixture table copies a slice of {@code lib/gamedata/trap.txt} in file order, so the expected results are
 * what C returns for the same descriptions: an exact, case-sensitive {@code streq} match wins outright; otherwise
 * the first kind whose description contains the argument case-insensitively ({@code my_stristr}); unnamed slots
 * are skipped. In C an empty string is a substring of every description, so it returns the first named kind.
 *
 * <p>Class TrapKindTest coded on 260930, commented in full on 260930.
 *
 * @author Rowan Crowther
 */
class TrapKindTest {

    private List<TrapKind> saved;
    private TrapKind unnamed;
    private TrapKind noTrap;
    private TrapKind doorLock;
    private TrapKind pit;
    private TrapKind spikedPit;
    private TrapKind poisonPit;
    private TrapKind runeFoe;
    private TrapKind runeSummoning;

    private static TrapKind kind(String name, String desc, int index) {
        return new TrapKind(name, "", desc, "", "", "", "", index, null, 0, 0, 0, null,
                null, null, new ArrayList<>(), new ArrayList<>());
    }

    @BeforeEach
    void installTable() {
        saved = new ArrayList<>(TerrainRegistry.getTrapInfo());
        unnamed = kind(null, "pit", 0);
        noTrap = kind("no trap", "no trap", 1);
        doorLock = kind("door lock", "door lock", 2);
        pit = kind("pit", "pit", 3);
        spikedPit = kind("pit", "spiked pit", 4);
        poisonPit = kind("pit", "poison pit", 5);
        runeFoe = kind("strange rune", "rune of summon foe", 6);
        runeSummoning = kind("strange rune", "rune of summoning", 7);
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(
                unnamed, noTrap, doorLock, pit, spikedPit, poisonPit, runeFoe, runeSummoning)));
    }

    @AfterEach
    void restoreTable() {
        TerrainRegistry.setTrapInfo(saved);
    }

    @Test
    @DisplayName("an exact description returns that kind")
    void exactMatch() {
        assertAll(
                () -> assertSame(doorLock, TrapKind.lookupTrap("door lock")),
                () -> assertSame(spikedPit, TrapKind.lookupTrap("spiked pit")),
                () -> assertSame(runeSummoning, TrapKind.lookupTrap("rune of summoning")));
    }

    @Test
    @DisplayName("an exact match later in the table beats an earlier close match")
    void exactBeatsEarlierClose() {
        // "spiked pit" contains "pit" and is scanned first, so it becomes the close match, but the later
        // exact "pit" must still win because C returns on streq immediately.
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(spikedPit, pit)));
        assertSame(pit, TrapKind.lookupTrap("pit"));
    }

    @Test
    @DisplayName("with no exact match the first case-insensitive substring match wins")
    void closeMatchFirstWins() {
        assertAll(
                () -> assertSame(runeFoe, TrapKind.lookupTrap("rune of summon")),
                () -> assertSame(spikedPit, TrapKind.lookupTrap("SPIKED")),
                () -> assertSame(doorLock, TrapKind.lookupTrap("LOCK")));
    }

    @Test
    @DisplayName("exact matching is case-sensitive, so a differently cased full name falls to the close match")
    void exactIsCaseSensitive() {
        assertSame(pit, TrapKind.lookupTrap("PIT"));
    }

    @Test
    @DisplayName("an argument that matches nothing returns null")
    void noMatchIsNull() {
        assertNull(TrapKind.lookupTrap("nonexistent"));
    }

    @Test
    @DisplayName("kinds with a null name are skipped even when their description matches")
    void unnamedKindsSkipped() {
        // unnamed carries desc "pit" and sits first; C's `if (!kind->name) continue` passes over it.
        assertSame(pit, TrapKind.lookupTrap("pit"));
        TerrainRegistry.setTrapInfo(new ArrayList<>(List.of(unnamed)));
        assertNull(TrapKind.lookupTrap("pit"));
    }

    @Test
    @DisplayName("an empty string is a substring of every description, so the first named kind is returned")
    void emptyStringReturnsFirstNamed() {
        assertSame(noTrap, TrapKind.lookupTrap(""));
    }

    @Test
    @DisplayName("an empty table returns null")
    void emptyTable() {
        TerrainRegistry.setTrapInfo(new ArrayList<>());
        assertNull(TrapKind.lookupTrap("pit"));
    }

    @Test
    @DisplayName("getDescription, getTrapKindName and getTrapKindIndex return the constructor fields")
    void accessors() {
        assertAll(
                () -> assertEquals("spiked pit", spikedPit.getDescription()),
                () -> assertEquals("pit", spikedPit.getTrapKindName()),
                () -> assertEquals(4, spikedPit.getTrapKindIndex()));
    }
}
