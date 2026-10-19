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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.parser.Reader;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for what {@link LoreReader} does today: it is a parked stub whose {@code parse} returns an
 * empty list and reads nothing. The expected values come from C's {@code lore_parser} in
 * {@code mon-init.c}, whose {@code run_parse_lore} treats a missing file as no error ("No monster
 * lore file found", then success), so a missing, empty or unreadable file all leave the lore blank.
 *
 * <p>{@link LoreReaderTest} holds the specs for the real parser and is disabled as a whole while
 * the grammar is parked, so these tests sit apart from it and keep running. The one case where the
 * stub and C part company on purpose, a file that does hold records, is pinned here as the stub's
 * result so that restoring the parser shows up as a failing test to replace, not a silent change.
 *
 * <p>Class LoreReaderStubTest coded on 261019, commented in full on 261019.
 *
 * @author Rowan Crowther
 */
class LoreReaderStubTest {

    /**
     * The shipped save-side file, relative to the Gradle working directory (project root). It holds
     * ten {@code name:} records, so C would load ten of them.
     */
    private static final String REAL_FILE = "lib/user/lore.txt";

    @TempDir
    Path tempDir;

    /**
     * C's {@code run_parse_lore} signals a message and returns success when the file is missing, so
     * the stub must not throw where the other readers do.
     */
    @Test
    void aMissingFileIsNotAnError() throws IOException {
        List<MonsterLore> lore = new LoreReader().parse(tempDir.resolve("absent.txt").toString());

        assertNotNull(lore);
        assertTrue(lore.isEmpty());
    }

    /**
     * C's {@code parse_file} fails on a path it cannot open as a file, and {@code run_parse_lore}
     * swallows the failure the same way as for a missing one.
     */
    @Test
    void aDirectoryInPlaceOfTheFileIsNotAnError() throws IOException {
        List<MonsterLore> lore = new LoreReader().parse(tempDir.toString());

        assertTrue(lore.isEmpty());
    }

    /**
     * An empty file has no {@code name:} line, so C leaves every {@code l_list} record zeroed.
     */
    @Test
    void anEmptyFileGivesNoRecords() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        assertTrue(new LoreReader().parse(file.toString()).isEmpty());
    }

    /**
     * One real record. C would fill the {@code l_list} entry for the large white snake; the stub
     * reads nothing, so this is the divergence the Javadoc on {@link LoreReader} describes.
     */
    @Test
    void aFileWithARecordStillGivesNoRecordsWhileTheParserIsParked() throws IOException {
        Path file = tempDir.resolve("one.txt");
        Files.writeString(file, String.join("\n",
                "name:large white snake",
                "counts:1:0:1:0:0:0:0",
                ""));

        assertTrue(new LoreReader().parse(file.toString()).isEmpty());
    }

    /**
     * The shipped file has ten records and C would load all ten. The assertion on the file's
     * presence stops this passing for the wrong reason if the file is moved.
     */
    @Test
    void theShippedFileStillGivesNoRecordsWhileTheParserIsParked() throws IOException {
        assertTrue(Files.isRegularFile(Path.of(REAL_FILE)), REAL_FILE + " should exist");

        assertTrue(new LoreReader().parse(REAL_FILE).isEmpty());
    }

    /**
     * The {@link Reader} contract promises a non-null list. The stub hands back a new modifiable
     * list each call, not a shared constant, so one caller adding to it cannot affect the next.
     */
    @Test
    void eachCallReturnsANewModifiableList() throws IOException {
        LoreReader reader = new LoreReader();
        String path = tempDir.resolve("absent.txt").toString();

        List<MonsterLore> first = reader.parse(path);
        first.add(new MonsterLore());
        List<MonsterLore> second = reader.parse(path);

        assertNotSame(first, second);
        assertTrue(second.isEmpty());
    }

    /**
     * Unlike the disabled spec in {@link LoreReaderTest}, which expects the monster races to be
     * loaded first, the stub never reaches the registry, so a call with nothing loaded does not
     * throw.
     */
    @Test
    void parsingNeedsNoRegistriesLoaded() {
        assertDoesNotThrow(() -> new LoreReader().parse(REAL_FILE));
    }
}
