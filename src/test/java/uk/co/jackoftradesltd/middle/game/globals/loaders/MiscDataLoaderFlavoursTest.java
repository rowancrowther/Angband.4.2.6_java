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
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.objects.FlavourKind;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@code MiscDataLoader.loadFlavours} against C's {@code flavor_parser} ({@code init.c}). C
 * stops the game with {@code quit} when {@code flavor.txt} cannot be opened
 * ({@code parse_file_quit_not_found}) or the parser reports an error ({@code run_parser} in
 * {@code datafile.c}); the loader throws in the same two cases and registers nothing. A missing
 * file is wrapped in a {@code RuntimeException} naming it; a grammar error
 * ({@link ParseCancellationException}) or an assembler error ({@link DataFileParseException})
 * passes through unwrapped.
 *
 * <p>The files written here use only {@code flavor:} lines, never {@code fixed:} ones, so no object
 * kinds need seeding: a {@code fixed:} line resolves its sub-type through the loaded kind table,
 * which the end-to-end reader test covers. The expected glyph and tval come from C's
 * {@code parse_flavor_kind}, which stores the {@code kind:} line's tval and glyph on every flavour
 * that follows.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at a temporary directory and puts it, and the
 * registry's flavours, back afterwards.
 *
 * <p>Class MiscDataLoaderFlavoursTest coded before 261104, commented in full on 261104 for the
 * removal of the soft-error path.
 *
 * @author Rowan Crowther
 */
@DisplayName("MiscDataLoader.loadFlavours")
class MiscDataLoaderFlavoursTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<FlavourKind> savedFlavours;

    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        savedFlavours = currentFlavours();
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        if (savedFlavours != null) {
            MiscRegistry.setFlavours(savedFlavours);
        }
    }

    /**
     * The registry's flavours, or {@code null} if the loader has not run in this JVM yet.
     */
    private static List<FlavourKind> currentFlavours() {
        try {
            return List.copyOf(MiscRegistry.getFlavours());
        } catch (NullPointerException notLoaded) {
            return null;
        }
    }

    private void writeFlavours(String text) throws IOException {
        Files.writeString(tempDir.resolve("flavor.txt"), text);
    }

    @Test
    @DisplayName("a good file registers one FlavourKind per kind: block, with its tval and glyph")
    void goodFileLoads() throws IOException {
        writeFlavours("kind:ring:=\nflavor:2:Green:Alexandrite\nflavor:3:Red:Ruby\n");

        MiscDataLoader.loadFlavours();

        List<FlavourKind> loaded = MiscRegistry.getFlavours();
        assertEquals(1, loaded.size());
        assertEquals(TValue.TV_RING, loaded.get(0).getValue());
        assertEquals('=', loaded.get(0).getGlyph());
        assertEquals(2, loaded.get(0).getFlavours().size());
    }

    @Test
    @DisplayName("a missing file stops start-up, as parse_file_quit_not_found does, and registers nothing")
    void missingFileThrows() throws IOException {
        writeFlavours("kind:ring:=\nflavor:2:Green:Alexandrite\n");
        MiscDataLoader.loadFlavours();
        List<FlavourKind> before = List.copyOf(MiscRegistry.getFlavours());
        Files.delete(tempDir.resolve("flavor.txt"));

        RuntimeException thrown = assertThrows(RuntimeException.class, MiscDataLoader::loadFlavours);

        assertInstanceOf(IOException.class, thrown.getCause());
        assertTrue(thrown.getMessage().contains("flavor.txt"));
        assertEquals(before, MiscRegistry.getFlavours());
    }

    /**
     * A grammar error stops start-up, as C's {@code run_parser} quits on a parser error, and the
     * registry keeps the previous load. The {@link ParseCancellationException} passes through the
     * loader unwrapped.
     *
     * <p>Function hardGrammarErrorThrows coded before 261104, commented in full on 261104, rewritten
     * on 261104 for the removal of the soft-error path.
     */
    @Test
    @DisplayName("a grammar error stops start-up and registers nothing")
    void hardGrammarErrorThrows() throws IOException {
        writeFlavours("kind:ring:=\nflavor:2:Green:Alexandrite\n");
        MiscDataLoader.loadFlavours();
        List<FlavourKind> before = List.copyOf(MiscRegistry.getFlavours());
        writeFlavours("kind:ring:=\nflavor:2:Green:Alexandrite\nnonsense\n");

        assertThrows(ParseCancellationException.class, MiscDataLoader::loadFlavours);

        assertEquals(before, MiscRegistry.getFlavours());
    }

    /**
     * An unrecognised tval stops start-up, as C's {@code parse_flavor_kind} returns
     * {@code PARSE_ERROR_UNRECOGNISED_TVAL}.
     *
     * <p>Function unrecognisedTvalThrows coded before 261104, commented in full on 261104, renamed
     * on 261104: the block is no longer dropped, so the load stops at it.
     */
    @Test
    @DisplayName("an unrecognised tval stops start-up")
    void unrecognisedTvalThrows() throws IOException {
        writeFlavours("kind:notatval:=\nflavor:1:Green:Foo\n");

        assertThrows(DataFileParseException.class, MiscDataLoader::loadFlavours);
    }
}
