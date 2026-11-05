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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.Hint;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@code MiscDataLoader.loadHints} against C's {@code hints_parser} ({@code init.c}). C's
 * {@code H:} lines each become a hint, and the parse is stopped with {@code quit} if the file
 * cannot be opened ({@code parse_file_quit_not_found}) or the parser reports an error
 * ({@code run_parser} in {@code datafile.c}, then {@code init_arrays}). The loader stops start-up
 * the same way by throwing, and registers nothing: a missing file is wrapped in a
 * {@code RuntimeException} naming it, and a grammar error ({@link ParseCancellationException})
 * passes through unwrapped.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at a temporary directory and puts it, and the
 * registry's hints, back afterwards. The expected texts are taken from {@code hints.txt} as C ships
 * it, not from what the loader returns.
 *
 * <p>Class MiscDataLoaderHintsTest coded before 261104, commented in full on 261104 for the removal
 * of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
@DisplayName("MiscDataLoader.loadHints")
class MiscDataLoaderHintsTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<Hint> savedHints;

    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        savedHints = currentHints();
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        if (savedHints != null) {
            MiscRegistry.setHints(savedHints);
        }
    }

    /**
     * The registry's hints, or {@code null} if the loader has not run in this JVM yet.
     */
    private static List<Hint> currentHints() {
        try {
            return List.copyOf(MiscRegistry.getHints());
        } catch (NullPointerException notLoaded) {
            return null;
        }
    }

    private static List<String> texts(List<Hint> hints) {
        return hints.stream().map(Hint::getHint).toList();
    }

    private void writeHints(String text) throws IOException {
        Files.writeString(tempDir.resolve("hints.txt"), text);
    }

    @Test
    @DisplayName("a good file registers every H: line, in file order")
    void goodFileLoads() throws IOException {
        writeHints("H:Staves can be used even when confused and blinded.\n"
                + "H:Resistances cut damage to 1/3rd for the four basic elements.\n");

        MiscDataLoader.loadHints();

        assertEquals(List.of("Staves can be used even when confused and blinded.",
                        "Resistances cut damage to 1/3rd for the four basic elements."),
                texts(MiscRegistry.getHints()));
    }

    @Test
    @DisplayName("a file with no H: line loads, as C accepts it, with an empty list")
    void emptyFileLoadsEmptyList() throws IOException {
        writeHints("H:Old hint.\n");
        MiscDataLoader.loadHints();
        writeHints("");

        MiscDataLoader.loadHints();

        assertTrue(MiscRegistry.getHints().isEmpty());
    }

    @Test
    @DisplayName("a missing file stops start-up, as parse_file_quit_not_found does, and registers nothing")
    void missingFileThrows() throws IOException {
        writeHints("H:Old hint.\n");
        MiscDataLoader.loadHints();
        Files.delete(tempDir.resolve("hints.txt"));

        RuntimeException thrown = assertThrows(RuntimeException.class, MiscDataLoader::loadHints);

        assertInstanceOf(IOException.class, thrown.getCause());
        assertTrue(thrown.getMessage().contains("hints.txt"));
        assertEquals(List.of("Old hint."), texts(MiscRegistry.getHints()));
    }

    /**
     * A grammar error stops start-up and the registry keeps the previous load. The
     * {@link ParseCancellationException} passes through the loader unwrapped.
     *
     * <p>Function hardGrammarErrorThrows coded before 261104, commented in full on 261104, rewritten
     * on 261104 for the removal of the soft-error path.
     */
    @Test
    @DisplayName("a grammar error stops start-up and registers nothing")
    void hardGrammarErrorThrows() throws IOException {
        writeHints("H:Old hint.\n");
        MiscDataLoader.loadHints();
        writeHints("H:A good hint.\nnonsense\n");

        assertThrows(ParseCancellationException.class, MiscDataLoader::loadHints);

        assertEquals(List.of("Old hint."), texts(MiscRegistry.getHints()));
    }

    @Test
    @DisplayName("the shipped hints.txt loads all 99 H: lines, the first and last as C has them")
    void shippedFileLoads() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);

        MiscDataLoader.loadHints();

        List<String> loaded = texts(MiscRegistry.getHints());
        assertEquals(99, loaded.size());
        assertEquals("Cure Critical Wounds potions will cure blindness and confusion; carry lots.",
                loaded.get(0));
    }
}
