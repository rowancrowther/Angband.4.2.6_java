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
import uk.co.jackoftradesltd.middle.game.Name;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.player.enums.RandnameType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@code MiscDataLoader.loadNames}: a good {@code names.txt} fills the registry, and a bad
 * one stops start-up as C does, in three ways. A missing file throws, as C's
 * {@code parse_file_quit_not_found} quits. A hard grammar error, which leaves no records and
 * reports errors, throws. An out-of-range {@code section:} that {@code MiscRegistry.setNames}
 * rejects is thrown on, so that {@code GameConstants.init} stops start-up. Soft errors that leave a
 * record are logged and the record still registers.
 *
 * <p>The range is C's {@code parse_names_section} ({@code init.c}): a section at or above
 * {@code RANDNAME_NUM_TYPES} (3) is {@code PARSE_ERROR_OUT_OF_BOUNDS}. The port also rejects
 * section zero, which C accepts (see {@code MiscRegistry.setNames}). C stops the load on that
 * error, so the port matches C for sections 3 and above; section 0 is stricter than C.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at a temporary directory and puts it, and the
 * registry's names, back afterwards.
 *
 * @author Rowan Crowther
 */
@DisplayName("MiscDataLoader.loadNames")
class MiscDataLoaderNamesTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<Name> savedNames;

    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        savedNames = safeNames();
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        if (savedNames != null) {
            MiscRegistry.setNames(savedNames);
        }
    }

    /**
     * The registry's current records, less any with an unusable section. A failed
     * {@code setNames} stores its list before it checks it, so an earlier test's bad record can
     * still be the registry's {@code names}, and putting that back would throw.
     */
    private static List<Name> safeNames() {
        try {
            return MiscRegistry.getNames().stream()
                    .filter(name -> RandnameType.fromIndex(name.getSection()) != null)
                    .toList();
        } catch (NullPointerException notLoaded) {
            return null;
        }
    }

    private void writeNames(String text) throws IOException {
        Files.writeString(tempDir.resolve("names.txt"), text);
    }

    @Test
    @DisplayName("a good file fills both sections")
    void goodFileLoads() throws IOException {
        writeNames("record-count:2\nsection:1\nword:beleg\nword:turin\nsection:2\nword:aar\n");

        MiscDataLoader.loadNames();

        assertEquals(List.of("beleg", "turin"),
                MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        assertEquals(List.of("aar"), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
    }

    @Test
    @DisplayName("a file with no section: block and record-count:0 loads, as C accepts it, with every section empty")
    void emptyFileLoadsEmptySections() throws IOException {
        writeNames("record-count:1\nsection:1\nword:beleg\nsection:2\nword:aar\n");
        MiscDataLoader.loadNames();
        writeNames("record-count:0\n");

        MiscDataLoader.loadNames();

        assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
        assertTrue(MiscRegistry.getNames().isEmpty());
    }

    @Test
    @DisplayName("a section: line with no word: lines is accepted, as in C, and leaves that section empty")
    void sectionWithNoWordsLoads() throws IOException {
        writeNames("record-count:2\nsection:1\nsection:2\nword:aar\n");

        MiscDataLoader.loadNames();

        assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        assertEquals(List.of("aar"), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
    }

    @Test
    @DisplayName("a record-count promising sections the file lacks leaves no records with errors, which stops start-up")
    void promisedSectionsMissingThrows() throws IOException {
        writeNames("record-count:1\nsection:1\nword:beleg\n");
        MiscDataLoader.loadNames();
        writeNames("record-count:2\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, MiscDataLoader::loadNames);

        assertTrue(thrown.getMessage().contains("names.txt"));
        assertEquals(List.of("beleg"), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
    }

    @Test
    @DisplayName("a word: line before any section: line stays a hard error, though C files it under section 0")
    void wordBeforeSectionThrows() throws IOException {
        writeNames("record-count:1\nword:orphan\nsection:1\nword:beleg\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, MiscDataLoader::loadNames);

        assertTrue(thrown.getMessage().contains("names.txt"));
    }

    @Test
    @DisplayName("section 3, C's PARSE_ERROR_OUT_OF_BOUNDS, escapes so that start-up stops")
    void sectionAtMarkerEscapes() throws IOException {
        writeNames("record-count:2\nsection:1\nword:beleg\nsection:3\nword:bad\n");

        assertThrows(IllegalArgumentException.class, MiscDataLoader::loadNames);
    }

    @Test
    @DisplayName("section 0, which C accepts but the port rejects, escapes too")
    void sectionZeroEscapes() throws IOException {
        writeNames("record-count:1\nsection:0\nword:bad\n");

        assertThrows(IllegalArgumentException.class, MiscDataLoader::loadNames);
    }

    @Test
    @DisplayName("a bad section throws and leaves the words filed before it in place")
    void badSectionLeavesEarlierRecordsFiled() throws IOException {
        writeNames("record-count:3\nsection:1\nword:beleg\nsection:3\nword:bad\nsection:2\nword:aar\n");

        assertThrows(IllegalArgumentException.class, MiscDataLoader::loadNames);

        assertEquals(List.of("beleg"), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
        assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL));
    }

    @Test
    @DisplayName("a missing file stops start-up, as parse_file_quit_not_found does, and leaves the registry as it was")
    void missingFileThrows() throws IOException {
        writeNames("record-count:1\nsection:1\nword:beleg\n");
        MiscDataLoader.loadNames();
        Files.delete(tempDir.resolve("names.txt"));

        RuntimeException thrown = assertThrows(RuntimeException.class, MiscDataLoader::loadNames);

        assertInstanceOf(IOException.class, thrown.getCause());
        assertTrue(thrown.getMessage().contains("names.txt"));
        assertEquals(List.of("beleg"), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
    }

    @Test
    @DisplayName("a hard grammar error yields no records with errors, which stops start-up")
    void hardGrammarErrorThrows() throws IOException {
        writeNames("record-count:1\nsection:1\nword:beleg\n");
        MiscDataLoader.loadNames();
        writeNames("section:1\nword:no-record-count-header\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, MiscDataLoader::loadNames);

        assertTrue(thrown.getMessage().contains("names.txt"));
        assertEquals(List.of("beleg"), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
    }

    @Test
    @DisplayName("a soft error that leaves a record is logged and the record still registers")
    void softErrorWithRecordsStillRegisters() throws IOException {
        writeNames("record-count:5\nsection:1\nword:beleg\n");

        MiscDataLoader.loadNames();

        assertEquals(List.of("beleg"), MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN));
    }

    @Test
    @DisplayName("the shipped names.txt loads both sections with words")
    void shippedFileLoads() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);

        MiscDataLoader.loadNames();

        assertFalse(MiscRegistry.getNameSection(RandnameType.RANDNAME_TOLKIEN).isEmpty());
        assertFalse(MiscRegistry.getNameSection(RandnameType.RANDNAME_SCROLL).isEmpty());
        assertEquals(List.of(), MiscRegistry.getNameSection(RandnameType.RANDNAME_NUM_TYPES));
    }
}
