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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.player.PlayerShape;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests how the eight loaders in {@link PlayerDataLoader} stop the load, against what C does at the
 * same point.
 *
 * <p><b>The C behaviour.</b> Every player-slice parser in {@code init.c} (and {@code player_timed}
 * in {@code player-timed.c}) reads its file through {@code parse_file_quit_not_found} in
 * {@code datafile.c}, which calls {@code quit()} with "Cannot open" when the file is missing, and
 * {@code init_arrays()} calls {@code quit_fmt("Cannot initialize %s.")} when a parser reports an
 * error. So in C a missing file always ends the program. The port stops the load on every error
 * too, and registers nothing from a file that fails: a missing file is wrapped in a
 * {@code RuntimeException} naming it, and a grammar error ({@link ParseCancellationException}) or
 * an assembler error ({@link DataFileParseException}) passes through the loader unwrapped.
 *
 * <p><b>What is tested.</b> A missing file for each of the eight loaders (an empty temporary
 * directory stands in for the gamedata directory), then, using {@code shape.txt} as the example,
 * the two error kinds: a shape that does not resolve and a grammar error each stop the load and
 * leave the registry alone, and a clean file replaces it. The clean path through all eight is
 * covered by the tests that run
 * {@code GameConstants.init} on the real gamedata.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at the temporary directory and puts it, and the
 * registered shapes, back afterwards.
 *
 * <p>Class PlayerDataLoaderFailurePathsTest coded on 261011, commented in full on 261011, updated on
 * 261104 for the removal of the soft-error path and the {@code record-count:} header.
 *
 * @author Rowan Crowther
 */
@DisplayName("PlayerDataLoader failure paths")
class PlayerDataLoaderFailurePathsTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<PlayerShape> savedShapes;

    /**
     * Points the gamedata directory at the empty temporary directory and saves the registered
     * shapes, which are the one registry these tests write to.
     */
    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        try {
            savedShapes = List.copyOf(PlayerRegistry.getPlayerShapes());
        } catch (RuntimeException notLoadedYet) {
            savedShapes = null;
        }
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    /**
     * Puts the gamedata directory and the shapes back.
     */
    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        if (savedShapes != null) {
            PlayerRegistry.setPlayerShape(savedShapes);
        }
    }

    /**
     * One loader, with the data file it reads.
     *
     * @param file   the file name, as it appears in the exception message
     * @param loader the loader call
     */
    private record Case(String file, Runnable loader) {
        @Override
        public String toString() {
            return file;
        }
    }

    /**
     * The eight loaders. {@code loadPlayerShapes} and {@code loadPlayerProperties} used to declare
     * a checked {@code IOException}; none of them does now, so all eight fit a {@link Runnable}.
     */
    static Stream<Case> loaders() {
        return Stream.of(
                new Case("player_timed.txt", PlayerDataLoader::loadPlayerTimedProperties),
                new Case("class.txt", PlayerDataLoader::loadPlayerClasses),
                new Case("realm.txt", PlayerDataLoader::loadMagicRealms),
                new Case("p_race.txt", PlayerDataLoader::loadPlayerRaces),
                new Case("body.txt", PlayerDataLoader::loadBodies),
                new Case("history.txt", PlayerDataLoader::loadPlayerHistories),
                new Case("shape.txt", PlayerDataLoader::loadPlayerShapes),
                new Case("player_property.txt", PlayerDataLoader::loadPlayerProperties));
    }

    /**
     * C's {@code parse_file_quit_not_found} quits on a missing file. Each loader must throw, name
     * the file, and keep the underlying {@code IOException} as the cause.
     */
    @ParameterizedTest(name = "missing {0} stops the load")
    @MethodSource("loaders")
    @DisplayName("a missing data file stops the load, naming the file")
    void missingFileStopsTheLoad(Case loaderCase) {
        RuntimeException thrown = assertThrows(RuntimeException.class, loaderCase.loader()::run);

        assertTrue(thrown.getMessage().contains(loaderCase.file()), thrown.getMessage());
        assertInstanceOf(IOException.class, thrown.getCause());
    }

    /**
     * A flag name that does not resolve ends the shape load with a {@code DataFileParseException}
     * out of {@code ShapeAssembler}, as C's {@code init_arrays()} stops on any parser error, and
     * the registry is not touched.
     */
    @Test
    @DisplayName("a shape that does not resolve stops the load and leaves the registry alone")
    void anUnresolvedShapeStopsTheLoad() throws IOException {
        Files.writeString(tempDir.resolve("shape.txt"),
                "name:weird\nobj-flags:NOTAFLAG\n");

        DataFileParseException thrown =
                assertThrows(DataFileParseException.class, PlayerDataLoader::loadPlayerShapes);

        assertTrue(thrown.getMessage().contains("shape.txt"), thrown.getMessage());
        assertTrue(thrown.getMessage().contains("illegal object flag value: NOTAFLAG"), thrown.getMessage());
        assertEquals(savedShapes, currentShapes(), "registered shapes");
    }

    /**
     * A clean one-shape file replaces whatever was registered, and the counter follows.
     *
     * <p>Function aCleanFileReplacesTheShapes coded on 261104, commented in full on 261104. It
     * replaces aRecordCountMismatchRegistersTheShapesRead, which needed the removed
     * {@code record-count:} header to produce a soft error.
     */
    @Test
    @DisplayName("a clean file replaces the registered shapes")
    void aCleanFileReplacesTheShapes() throws IOException {
        Files.writeString(tempDir.resolve("shape.txt"),
                "name:plain\ncombat:0:0:0\n");

        PlayerDataLoader.loadPlayerShapes();

        List<PlayerShape> shapes = PlayerRegistry.getPlayerShapes();
        assertEquals(1, shapes.size(), "shape count");
        assertEquals("plain", shapes.get(0).getName());
        assertEquals(1, PlayerRegistry.getPlayerShapeMax(), "shape counter");
    }

    /**
     * The shape grammar needs at least one {@code name:} record, so an empty file is a grammar
     * error. The {@link ParseCancellationException} passes through the loader unwrapped and the
     * registry is not touched.
     *
     * <p>Function syntaxErrorStopsTheLoad coded on 261011, commented in full on 261011, rewritten on
     * 261104 for the removal of the soft-error path.
     */
    @Test
    @DisplayName("a grammar error stops the load and leaves the registry alone")
    void syntaxErrorStopsTheLoad() throws IOException {
        Files.writeString(tempDir.resolve("shape.txt"), "");

        assertThrows(ParseCancellationException.class, PlayerDataLoader::loadPlayerShapes);

        assertEquals(savedShapes, currentShapes(), "registered shapes");
    }

    /**
     * The shapes now in the registry, or {@code null} if none have been loaded.
     */
    private static List<PlayerShape> currentShapes() {
        try {
            return List.copyOf(PlayerRegistry.getPlayerShapes());
        } catch (RuntimeException notLoadedYet) {
            return null;
        }
    }
}
