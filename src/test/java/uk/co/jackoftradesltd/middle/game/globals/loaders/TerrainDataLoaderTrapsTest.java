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

package uk.co.jackoftradesltd.middle.game.globals.loaders;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.cave.TrapKind;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.Summon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link TerrainDataLoader#loadTraps()}, the Java guard that the kind at index 0 of {@code trap.txt} is
 * {@code no trap}.
 *
 * <p>C has no such check: {@code lookup_trap()} in {@code trap.c} starts its loop at 1 and
 * {@code finish_parse_trap()} in {@code init.c} numbers records from 0 in file order, and C quits on any bad record.
 * Java drops a bad record and carries on, so a dropped {@code no trap} would hand index 0 to the next kind and
 * {@code lookupTrap} would skip it. The loader therefore stops the load unless the first kept kind is
 * {@code no trap}.
 *
 * <p>Beyond the guard, the tests pin the rest of {@code trap_parser}: a missing file stops the load as
 * {@code parse_file_quit_not_found} quits and leaves the registry alone, a reload replaces the list rather than
 * appending, and the shipped {@code trap.txt} gives the 40 records counted in C's data, in file order, with
 * {@code z_info->trap_max} (40) equal to the record count and indexes 0 to 39 as {@code finish_parse_trap} numbers
 * them. C's extra blank element after the last kind has no Java counterpart.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at a temporary directory holding a small {@code trap.txt}, and
 * both that path and the trap registry are put back afterwards. The fixture records use no effects, so no monster
 * data needs loading. The shipped file does carry summon effects, so its test loads the pain messages, monster bases
 * and summons first, in that order, and puts those three registry fields back afterwards.
 *
 * <p>Class TerrainDataLoaderTrapsTest coded on 261009, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
class TerrainDataLoaderTrapsTest {

    private static final String NO_TRAP = "name:no trap:no trap\ngraphics: :w\n\n";
    private static final String GLYPH = "name:glyph of warding:glyph of warding\ngraphics:;:y\nflags:GLYPH\n\n";
    private static final String BAD_FLAG = "name:%s:%s\ngraphics:^:w\nflags:NOTAFLAG\n\n";

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<TrapKind> savedTraps;

    private static String badFlagRecord(String name) {
        return String.format(BAD_FLAG, name, name);
    }

    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        savedTraps = new ArrayList<>(TerrainRegistry.getTrapInfo());
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        TerrainRegistry.setTrapInfo(savedTraps);
    }

    private void writeTrapFile(int recordCount, String... records) throws IOException {
        Files.writeString(tempDir.resolve("trap.txt"), "record-count:" + recordCount + "\n" + String.join("", records));
    }

    @Test
    @DisplayName("a file that opens with no trap loads, and the registry holds every record in file order")
    void noTrapFirstLoads() throws IOException {
        writeTrapFile(2, NO_TRAP, GLYPH);

        TerrainDataLoader.loadTraps();

        List<TrapKind> loaded = TerrainRegistry.getTrapInfo();
        assertAll(
                () -> assertEquals(2, loaded.size()),
                () -> assertEquals("no trap", loaded.get(0).getTrapKindName()),
                () -> assertEquals(0, loaded.get(0).getTrapKindIndex()),
                () -> assertEquals("glyph of warding", loaded.get(1).getTrapKindName()),
                () -> assertEquals(1, loaded.get(1).getTrapKindIndex()),
                () -> assertEquals(2, TerrainRegistry.getTrapMax()));
    }

    @Test
    @DisplayName("a later record dropped on a soft error does not stop the load")
    void laterDroppedRecordIsTolerated() throws IOException {
        writeTrapFile(3, NO_TRAP, badFlagRecord("broken"), GLYPH);

        TerrainDataLoader.loadTraps();

        List<TrapKind> loaded = TerrainRegistry.getTrapInfo();
        assertAll(
                () -> assertEquals(2, loaded.size()),
                () -> assertEquals("no trap", loaded.get(0).getTrapKindName()),
                // the dropped record takes no index, so the glyph closes up to index 1
                () -> assertEquals(1, loaded.get(1).getTrapKindIndex()));
    }

    @Test
    @DisplayName("when no trap itself is dropped the load stops and the registry is left as it was")
    void droppedNoTrapStopsTheLoad() throws IOException {
        TerrainRegistry.setTrapInfo(new ArrayList<>());
        writeTrapFile(2, badFlagRecord("no trap"), GLYPH);

        assertThrows(RuntimeException.class, TerrainDataLoader::loadTraps);

        assertTrue(TerrainRegistry.getTrapInfo().isEmpty());
        assertEquals(0, TerrainRegistry.getTrapMax());
    }

    @Test
    @DisplayName("a file whose first record is some other kind stops the load")
    void otherFirstKindStopsTheLoad() throws IOException {
        writeTrapFile(2, GLYPH, NO_TRAP);

        RuntimeException thrown = assertThrows(RuntimeException.class, TerrainDataLoader::loadTraps);

        assertTrue(thrown.getMessage().contains("no trap"));
    }

    @Test
    @DisplayName("a file in which every record is dropped (an empty result) stops the load")
    void emptyResultStopsTheLoad() throws IOException {
        TerrainRegistry.setTrapInfo(new ArrayList<>());
        writeTrapFile(1, badFlagRecord("no trap"));

        assertThrows(RuntimeException.class, TerrainDataLoader::loadTraps);

        assertTrue(TerrainRegistry.getTrapInfo().isEmpty());
    }

    @Test
    @DisplayName("the loaded list is the one the registry holds, so lookupTrap skips no trap")
    void loadedListFeedsLookupTrap() throws IOException {
        writeTrapFile(2, NO_TRAP, GLYPH);

        TerrainDataLoader.loadTraps();

        assertAll(
                () -> assertSame(TerrainRegistry.getTrapInfo().get(1), TerrainRegistry.lookupTrap("glyph of warding")),
                () -> assertEquals(null, TerrainRegistry.lookupTrap("no trap")));
    }

    @Test
    @DisplayName("a missing trap.txt throws, as parse_file_quit_not_found quits, and leaves the registry alone")
    void missingFileStopsTheLoad() {
        List<TrapKind> sentinel = new ArrayList<>();
        TerrainRegistry.setTrapInfo(sentinel);

        RuntimeException thrown = assertThrows(RuntimeException.class, TerrainDataLoader::loadTraps);

        assertTrue(thrown.getMessage().contains("trap.txt"));
        assertTrue(TerrainRegistry.getTrapInfo().isEmpty());
        assertEquals(0, TerrainRegistry.getTrapMax());
    }

    @Test
    @DisplayName("loading twice replaces the list rather than appending")
    void reloadReplaces() throws IOException {
        writeTrapFile(2, NO_TRAP, GLYPH);

        TerrainDataLoader.loadTraps();
        TerrainDataLoader.loadTraps();

        assertEquals(2, TerrainRegistry.getTrapInfo().size());
        assertEquals(2, TerrainRegistry.getTrapMax());
    }

    @Test
    @DisplayName("the shipped trap.txt gives 40 kinds in file order, trap_max 40, no trap first and the last record last")
    void shippedFile() throws Exception {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        // the shipped traps carry summon effects, which resolve against the summons, which resolve against
        // the monster bases, which resolve against the pain messages
        Field summonsField = MonsterRegistry.class.getDeclaredField("summons");
        summonsField.setAccessible(true);
        @SuppressWarnings("unchecked") List<Summon> savedSummons = (List<Summon>) summonsField.get(null);
        Field basesField = MonsterRegistry.class.getDeclaredField("monsterBases");
        basesField.setAccessible(true);
        @SuppressWarnings("unchecked") List<MonsterBase> savedBases = (List<MonsterBase>) basesField.get(null);
        Field painField = MonsterRegistry.class.getDeclaredField("monsterPains");
        painField.setAccessible(true);
        @SuppressWarnings("unchecked") List<MonsterPain> savedPains = (List<MonsterPain>) painField.get(null);
        try {
            MonsterDataLoader.loadPain();
            MonsterDataLoader.loadMonsterBases();
            MonsterDataLoader.loadSummons();
            TerrainDataLoader.loadTraps();
        } finally {
            MonsterRegistry.setSummons(savedSummons);
            MonsterRegistry.setMonsterBases(savedBases);
            // the setter rejects null, which is what an unloaded registry holds
            painField.set(null, savedPains);
        }

        List<TrapKind> loaded = TerrainRegistry.getTrapInfo();
        assertAll(
                () -> assertEquals(40, loaded.size()),
                () -> assertEquals(40, TerrainRegistry.getTrapMax()),
                () -> assertEquals("no trap", loaded.get(0).getTrapKindName()),
                () -> assertEquals("glyph of warding", loaded.get(1).getTrapKindName()),
                () -> assertEquals("decoy", loaded.get(2).getTrapKindName()),
                () -> assertEquals("petrifying trap", loaded.get(39).getDescription()),
                () -> assertEquals(39, loaded.get(39).getTrapKindIndex()));
    }
}
