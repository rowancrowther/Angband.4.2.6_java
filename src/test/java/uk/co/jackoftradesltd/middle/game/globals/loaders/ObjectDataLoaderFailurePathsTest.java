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
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests how the ten file loaders in {@link ObjectDataLoader} stop the load, against what C does at
 * the same point.
 *
 * <p><b>The C behaviour.</b> Every object-slice parser in {@code obj-init.c}, and the chest-trap
 * parser in {@code obj-chest.c}, reads its file through {@code parse_file_quit_not_found} in
 * {@code datafile.c}, which calls {@code quit()} with "Cannot open" when the file is missing, and
 * {@code run_parser} quits when a parser reports an error. So in C a missing file always ends the
 * program. The port stops the load the same way for a missing file, and for a file whose errors left
 * no records at all; it deliberately lets through a file whose soft errors still left some records
 * (the partial-results contract).
 *
 * <p><b>What is tested.</b> A missing file for each of the ten loaders (an empty temporary directory
 * stands in for the gamedata directory). Then, for a file that is not valid at all, that the load
 * stops and that the registry's counter is left as it was: {@code brand.txt} and {@code slay.txt}
 * for the plain route, which lets the {@link RuntimeException} from the error check escape as it is,
 * and {@code curse.txt} for the one loader whose {@code catch} is on {@code Exception}, which wraps
 * that same exception a second time. The clean path through all ten is covered by
 * {@link ObjectDataLoaderRegistryTest}, which runs {@code GameConstants.init} on the real gamedata.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at the temporary directory and puts it back
 * afterwards. Nothing here loads a good file, so the registry is only read, never written.
 *
 * <p>Class ObjectDataLoaderFailurePathsTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
@DisplayName("ObjectDataLoader failure paths")
class ObjectDataLoaderFailurePathsTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;

    /**
     * Points the gamedata directory at the empty temporary directory.
     */
    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    /**
     * Puts the gamedata directory back.
     */
    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
    }

    /**
     * One loader, with the data file it reads. Some loaders declare a checked {@code IOException}
     * that can never fire, so the call is an {@link Executable} rather than a {@link Runnable}.
     *
     * @param file   the file name, as it appears in the exception message
     * @param loader the loader call
     */
    private record Case(String file, Executable loader) {
        @Override
        public String toString() {
            return file;
        }
    }

    /**
     * The ten file loaders. {@code parseCurseKinds} reads no file and is covered by
     * {@link ObjectDataLoaderCurseKindsTest}.
     */
    static Stream<Case> loaders() {
        return Stream.of(
                new Case("object_base.txt", ObjectDataLoader::loadObjectBases),
                new Case("slay.txt", ObjectDataLoader::loadSlays),
                new Case("brand.txt", ObjectDataLoader::loadBrands),
                new Case("curse.txt", ObjectDataLoader::loadCurses),
                new Case("object.txt", ObjectDataLoader::loadItemObjects),
                new Case("activation.txt", ObjectDataLoader::loadActivations),
                new Case("ego_item.txt", ObjectDataLoader::loadEgoItems),
                new Case("artifact.txt", ObjectDataLoader::loadArtifacts),
                new Case("object_property.txt", ObjectDataLoader::loadObjectProperties),
                new Case("chest_trap.txt", ObjectDataLoader::loadChestTraps));
    }

    /**
     * C's {@code parse_file_quit_not_found} quits on a missing file. Each loader must throw, name the
     * file, and keep the underlying {@code IOException} as the cause.
     */
    @ParameterizedTest(name = "missing {0} stops the load")
    @MethodSource("loaders")
    @DisplayName("a missing data file stops the load, naming the file")
    void missingFileStopsTheLoad(Case loaderCase) {
        RuntimeException thrown = assertThrows(RuntimeException.class, loaderCase.loader()::execute);

        assertTrue(thrown.getMessage().contains(loaderCase.file()), thrown.getMessage());
        assertInstanceOf(IOException.class, thrown.getCause());
    }

    /**
     * A file that is not a brand file at all is a hard syntax error. The reader turns it into an
     * empty result carrying the error, so the error check answers {@code false} and the loader's own
     * {@code RuntimeException} escapes unwrapped. {@code setBrands} is never reached, so the
     * registry's brand counter is what it was.
     */
    @Test
    @DisplayName("an invalid brand file stops the load and leaves the registry alone")
    void invalidBrandFileStopsTheLoad() throws IOException {
        int brandMaxBefore = ObjectRegistry.getBrandMax();
        Files.writeString(tempDir.resolve("brand.txt"), "this is not a brand file\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, ObjectDataLoader::loadBrands);

        assertTrue(thrown.getMessage().contains("brand.txt"), thrown.getMessage());
        assertNull(thrown.getCause(), "the plain route throws its own exception, wrapping nothing");
        assertEquals(brandMaxBefore, ObjectRegistry.getBrandMax(), "brand counter");
    }

    /**
     * The same for slays, the symmetric loader beside brands.
     */
    @Test
    @DisplayName("an invalid slay file stops the load and leaves the registry alone")
    void invalidSlayFileStopsTheLoad() throws IOException {
        int slayMaxBefore = ObjectRegistry.getSlayMax();
        Files.writeString(tempDir.resolve("slay.txt"), "this is not a slay file\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, ObjectDataLoader::loadSlays);

        assertTrue(thrown.getMessage().contains("slay.txt"), thrown.getMessage());
        assertEquals(slayMaxBefore, ObjectRegistry.getSlayMax(), "slay counter");
    }

    /**
     * {@code loadCurses} catches {@code Exception}, so the {@code RuntimeException} it throws for a
     * failed error check is caught by its own handler and wrapped: the exception that escapes says
     * "Error while loading file" and carries the "Error parsing file" one as its cause.
     */
    @Test
    @DisplayName("an invalid curse file stops the load, wrapped a second time")
    void invalidCurseFileIsWrapped() throws IOException {
        int curseMaxBefore = ObjectRegistry.getCurseMax();
        Files.writeString(tempDir.resolve("curse.txt"), "this is not a curse file\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, ObjectDataLoader::loadCurses);

        assertTrue(thrown.getMessage().startsWith("Error while loading file"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("curse.txt"), thrown.getMessage());
        assertInstanceOf(RuntimeException.class, thrown.getCause());
        assertTrue(thrown.getCause().getMessage().startsWith("Error parsing file"), thrown.getCause().getMessage());
        assertEquals(curseMaxBefore, ObjectRegistry.getCurseMax(), "curse counter");
    }
}
