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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests how the nine loaders in {@link MonsterDataLoader} stop the load, against what C does at the
 * same point.
 *
 * <p><b>The C behaviour.</b> Every monster-slice parser in {@code mon-init.c} and
 * {@code mon-summon.c}, and the visuals parser in {@code ui-visuals.c}, reads its file through
 * {@code parse_file_quit_not_found} in {@code datafile.c}, which calls {@code quit()} with "Cannot
 * open" when the file is missing, and {@code run_parser} quits when a parser reports an error. So in C
 * a missing file always ends the program. The port stops the load the same way for a missing file,
 * and for a file whose errors left no records at all; it deliberately lets through a file whose soft
 * errors still left some records (the partial-results contract).
 *
 * <p><b>What is tested.</b> A missing file for each of the nine loaders (an empty temporary directory
 * stands in for the gamedata directory), then, using {@code pain.txt} as the example, the outcomes of
 * the error check: errors that leave no records stop the load and leave the registry alone, a header
 * miscount that still leaves records registers them, and a hard syntax error, which the reader turns
 * into an empty result, stops the load too. The clean path through all nine is covered by
 * {@link MonsterDataLoaderRegistryTest} and {@link MonsterDataLoaderLoreTest}, which run
 * {@code GameConstants.init} on the real gamedata.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at the temporary directory. {@code pain.txt} is
 * loaded from the real gamedata before each test, and again after it, so the pain registry the soft
 * error tests write to is the real one both before and after.
 *
 * <p>Class MonsterDataLoaderFailurePathsTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
@DisplayName("MonsterDataLoader failure paths")
class MonsterDataLoaderFailurePathsTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;

    /**
     * Loads the real pain sets, then points the gamedata directory at the empty temporary directory.
     */
    @BeforeEach
    void pointAtTempDir() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        MonsterDataLoader.loadPain();
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    /**
     * Puts the gamedata directory back and reloads the real pain sets over anything a test registered.
     */
    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        MonsterDataLoader.loadPain();
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
     * The nine loaders. {@code loadPain}, {@code loadMonsterBases} and {@code loadSummons} used to
     * declare a checked {@code IOException}; none of them does now, so all nine fit a {@link Runnable}.
     */
    static Stream<Case> loaders() {
        return Stream.of(
                new Case("pain.txt", MonsterDataLoader::loadPain),
                new Case("monster_base.txt", MonsterDataLoader::loadMonsterBases),
                new Case("summon.txt", MonsterDataLoader::loadSummons),
                new Case("blow_methods.txt", MonsterDataLoader::loadBlowMethods),
                new Case("blow_effects.txt", MonsterDataLoader::loadBlowEffects),
                new Case("monster_spell.txt", MonsterDataLoader::loadMonsterSpellTypes),
                new Case("visuals.txt", MonsterDataLoader::loadVisualTables),
                new Case("monster.txt", MonsterDataLoader::loadMonsters),
                new Case("pit.txt", MonsterDataLoader::loadPitProfiles));
    }

    /**
     * C's {@code parse_file_quit_not_found} quits on a missing file. Each loader must throw, name the
     * file, and keep the underlying {@code IOException} as the cause.
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
     * A negative type number passes the lexer, but C reads it as a {@code uint} and refuses a
     * leading {@code -}, so {@code PainAssembler} throws a {@code DataFileParseException} naming
     * the file. The load stops, as C's {@code run_parser} does on any parser error, before anything
     * is registered, so the real pain sets are still there.
     */
    @Test
    @DisplayName("errors that leave no records stop the load and leave the registry alone")
    void errorsWithNoRecordsStopTheLoad() throws IOException {
        Files.writeString(tempDir.resolve("pain.txt"), "record-count:1\n" + painSet("-1"));

        RuntimeException thrown = assertThrows(RuntimeException.class, MonsterDataLoader::loadPain);

        assertTrue(thrown.getMessage().contains("pain.txt"), thrown.getMessage());
        assertEquals(12, MonsterRegistry.getMonsterPainMsgMax(), "pain counter");
        assertNotNull(MonsterRegistry.getPainFromIndex(12), "real pain set 12 still registered");
    }

    /**
     * A {@code record-count} header that disagrees with the number of sets is a soft error that
     * never discards the records that did parse. This is the deliberate softening of C: the one set
     * is registered, replacing the real twelve, and the load carries on.
     */
    @Test
    @DisplayName("a header miscount that still leaves records registers them and carries on")
    void miscountWithRecordsRegistersThem() throws IOException {
        Files.writeString(tempDir.resolve("pain.txt"), "record-count:5\n" + painSet("1"));

        MonsterDataLoader.loadPain();

        assertEquals(1, MonsterRegistry.getMonsterPainMsgMax(), "pain counter is the record count");
        assertNotNull(MonsterRegistry.getPainFromIndex(1));
        assertNull(MonsterRegistry.getPainFromIndex(2), "the real set 2 was replaced");
    }

    /**
     * {@code record-count:0} followed by nothing is a hard syntax error. {@code GrammarDriver.run}
     * catches it and returns an empty result carrying the error, which the loader treats as a file
     * that yielded nothing: the load stops with the loader's own message and the registry is not
     * touched.
     */
    @Test
    @DisplayName("a hard syntax error stops the load through the empty-result route")
    void syntaxErrorStopsTheLoad() throws IOException {
        Files.writeString(tempDir.resolve("pain.txt"), "record-count:0\n");

        RuntimeException thrown = assertThrows(RuntimeException.class, MonsterDataLoader::loadPain);

        assertEquals("Error while parsing file " + tempDir + File.separator + "pain.txt",
                thrown.getMessage());
        assertEquals(12, MonsterRegistry.getMonsterPainMsgMax(), "pain counter");
    }

    /**
     * One pain set as {@code pain.txt} writes it: the {@code type:} line and its seven messages.
     */
    private static String painSet(String type) {
        StringBuilder set = new StringBuilder("type:").append(type).append('\n');
        for (int i = 0; i < 7; i++) {
            set.append("message:flinch[es].\n");
        }
        return set.toString();
    }
}
