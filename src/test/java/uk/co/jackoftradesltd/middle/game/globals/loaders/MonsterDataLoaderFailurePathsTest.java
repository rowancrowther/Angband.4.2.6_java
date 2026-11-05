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
 * a missing file always ends the program. The port stops the load on every error too, and registers
 * nothing from a file that fails: a missing file is wrapped in a {@code RuntimeException} naming it,
 * and a grammar error ({@link ParseCancellationException}) or an assembler error
 * ({@link DataFileParseException}) passes through the loader unwrapped.
 *
 * <p><b>What is tested.</b> A missing file for each of the nine loaders (an empty temporary directory
 * stands in for the gamedata directory), then, using {@code pain.txt} as the example, the outcomes of
 * the two error kinds: an assembler error and a grammar error each stop the load and leave the
 * registry alone. The clean path through all nine is covered by
 * {@link MonsterDataLoaderRegistryTest} and {@link MonsterDataLoaderLoreTest}, which run
 * {@code GameConstants.init} on the real gamedata.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at the temporary directory. {@code pain.txt} is
 * loaded from the real gamedata before each test, and again after it, so the pain registry is the
 * real one both before and after.
 *
 * <p>Class MonsterDataLoaderFailurePathsTest coded on 261013, commented in full on 261013, updated on
 * 261104 for the removal of the soft-error path and the {@code record-count:} header.
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
    @DisplayName("an assembler error stops the load and leaves the registry alone")
    void anAssemblerErrorStopsTheLoad() throws IOException {
        Files.writeString(tempDir.resolve("pain.txt"), painSet("-1"));

        DataFileParseException thrown = assertThrows(DataFileParseException.class, MonsterDataLoader::loadPain);

        assertTrue(thrown.getMessage().contains("pain.txt"), thrown.getMessage());
        assertEquals(12, MonsterRegistry.getMonsterPainMsgMax(), "pain counter");
        assertNotNull(MonsterRegistry.getPainFromIndex(12), "real pain set 12 still registered");
    }

    /**
     * A clean one-set file replaces the real twelve: the registry holds exactly what the file
     * gave, and the counter is the record count.
     *
     * <p>Function aCleanFileReplacesTheRegistry coded on 261104, commented in full on 261104. It
     * replaces miscountWithRecordsRegistersThem, which needed the removed {@code record-count:}
     * header to produce a soft error.
     */
    @Test
    @DisplayName("a clean file replaces the registered sets")
    void aCleanFileReplacesTheRegistry() throws IOException {
        Files.writeString(tempDir.resolve("pain.txt"), painSet("1"));

        MonsterDataLoader.loadPain();

        assertEquals(1, MonsterRegistry.getMonsterPainMsgMax(), "pain counter is the record count");
        assertNotNull(MonsterRegistry.getPainFromIndex(1));
        assertNull(MonsterRegistry.getPainFromIndex(2), "the real set 2 was replaced");
    }

    /**
     * An empty file is a grammar error, since {@code PainGrammar} needs at least one set. The
     * {@link ParseCancellationException} passes through the loader unwrapped and the registry is
     * not touched.
     *
     * <p>Function syntaxErrorStopsTheLoad coded on 261013, commented in full on 261013, rewritten on
     * 261104 for the removal of the soft-error path.
     */
    @Test
    @DisplayName("a grammar error stops the load and leaves the registry alone")
    void syntaxErrorStopsTheLoad() throws IOException {
        Files.writeString(tempDir.resolve("pain.txt"), "");

        assertThrows(ParseCancellationException.class, MonsterDataLoader::loadPain);

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
