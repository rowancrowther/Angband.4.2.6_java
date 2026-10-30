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

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.TerrainRegistry;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link TerrainDataLoader#loadTerrainFeatures()}, the port of C's {@code feat_parser}
 * ({@code init_parse_feat}, {@code run_parse_feat} and {@code finish_parse_feat} in {@code init.c}).
 *
 * <p><b>Expected values come from C's data, not from the port.</b> The shipped {@code terrain.txt}
 * is the C original with a {@code record-count:} line added. The expectations below were read from
 * it: 25 records, one per {@code FEAT_} code and in the order of {@code list-terrain.h}; eight
 * {@code SHOP} features (the seven stores, then the home), numbered 1 to 8 by
 * {@code finish_parse_feat}, which also sets {@code z_info->store_max} to 8; one {@code mimic:}
 * ({@code SECRET} as {@code GRANITE}); {@code digging:} on nine features; a {@code look-prefix:} on
 * the stores and on lava, a {@code look-in-preposition:} on the stores and on the open and broken
 * doors, each given a trailing space by {@code finish_parse_feat}; and one {@code resist-flag:}
 * ({@code IM_FIRE}, on lava).
 *
 * <p><b>The C failure behaviour.</b> {@code parse_file_quit_not_found} quits on a missing file, and
 * C quits on any bad line, including a second record for the same code ({@code parse_feat_name}
 * returns {@code PARSE_ERROR_REPEATED_DIRECTIVE}). The port stops the same way: a missing file, a
 * parse that leaves errors and no features, and every assembler error (a bad code, mimic, flag,
 * resist flag or number, or a second name for a repeated code) throw before anything is installed.
 * There is no count guard: C's {@code f_info} is a fixed array of {@code FEAT_MAX} slots and a short
 * file loads there with a zeroed slot, and it loads here with no entry for the missing code. The
 * tests say so.
 *
 * <p><b>{@code store_max}.</b> {@code finish_parse_feat} sets {@code z_info->store_max} to the number
 * of shops numbered. The port sets it when the assembler returns, so any error, which throws first,
 * leaves it alone.
 *
 * <p><b>The accepted colour deviation.</b> C's {@code color_text_to_attr} and
 * {@code color_char_to_attr} turn an unknown colour into white, so a {@code graphics:} line never
 * fails there. The port's lexer rejects it, as a hard grammar error; the assembler itself falls back
 * to white as C does, but the grammar never lets an unknown colour reach it. One test pins the lexer
 * rejection, and a name-spelled colour that does resolve.
 *
 * <p>Failure-path tests rewritten on 261030 for the assembler's fatal-error model.
 *
 * <p>Each test points {@code ANGBAND_DIRS.GAMEDATA} at a temporary directory (the real-data tests
 * leave it alone) and puts the feature list and the store count back afterwards.
 *
 * <p>Class TerrainDataLoaderFeaturesTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
@DisplayName("TerrainDataLoader.loadTerrainFeatures")
class TerrainDataLoaderFeaturesTest {

    /** The 25 codes in file order, which is the order of {@code list-terrain.h}. */
    private static final List<TerrainFlags> C_ORDER = List.of(
            TerrainFlags.FEAT_NONE, TerrainFlags.FEAT_FLOOR, TerrainFlags.FEAT_CLOSED,
            TerrainFlags.FEAT_OPEN, TerrainFlags.FEAT_BROKEN, TerrainFlags.FEAT_LESS,
            TerrainFlags.FEAT_MORE, TerrainFlags.FEAT_STORE_GENERAL, TerrainFlags.FEAT_STORE_ARMOR,
            TerrainFlags.FEAT_STORE_WEAPON, TerrainFlags.FEAT_STORE_BOOK,
            TerrainFlags.FEAT_STORE_ALCHEMY, TerrainFlags.FEAT_STORE_MAGIC,
            TerrainFlags.FEAT_STORE_BLACK, TerrainFlags.FEAT_HOME, TerrainFlags.FEAT_SECRET,
            TerrainFlags.FEAT_RUBBLE, TerrainFlags.FEAT_MAGMA, TerrainFlags.FEAT_QUARTZ,
            TerrainFlags.FEAT_MAGMA_K, TerrainFlags.FEAT_QUARTZ_K, TerrainFlags.FEAT_GRANITE,
            TerrainFlags.FEAT_PERM, TerrainFlags.FEAT_LAVA, TerrainFlags.FEAT_PASS_RUBBLE);

    /** The seven stores and the home, in order, which {@code finish_parse_feat} numbers 1 to 8. */
    private static final List<TerrainFlags> C_SHOPS = List.of(
            TerrainFlags.FEAT_STORE_GENERAL, TerrainFlags.FEAT_STORE_ARMOR,
            TerrainFlags.FEAT_STORE_WEAPON, TerrainFlags.FEAT_STORE_BOOK,
            TerrainFlags.FEAT_STORE_ALCHEMY, TerrainFlags.FEAT_STORE_MAGIC,
            TerrainFlags.FEAT_STORE_BLACK, TerrainFlags.FEAT_HOME);

    /** {@code digging:} values in the C file; every other feature has none, so 0. */
    private static final Map<TerrainFlags, Integer> C_DIGGING = Map.of(
            TerrainFlags.FEAT_CLOSED, 5, TerrainFlags.FEAT_SECRET, 5, TerrainFlags.FEAT_RUBBLE, 1,
            TerrainFlags.FEAT_MAGMA, 2, TerrainFlags.FEAT_QUARTZ, 3, TerrainFlags.FEAT_MAGMA_K, 2,
            TerrainFlags.FEAT_QUARTZ_K, 3, TerrainFlags.FEAT_GRANITE, 4,
            TerrainFlags.FEAT_PASS_RUBBLE, 1);

    @TempDir
    Path tempDir;

    private String savedGamedata;
    private List<Feature> savedFeatures;
    private int savedStoreMax;

    /**
     * Saves the gamedata directory, the feature list and the store count. The feature list has no
     * "is it loaded" accessor, so a missing list is recorded as {@code null}.
     */
    @BeforeEach
    void save() {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        try {
            savedFeatures = new ArrayList<>(TerrainRegistry.getFeatures());
        } catch (NullPointerException notLoaded) {
            savedFeatures = null;
        }
        savedStoreMax = GameConstants.getStoreMax();
    }

    /** Puts everything saved by {@link #save()} back. */
    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        TerrainRegistry.setFeatures(savedFeatures);
        GameConstants.setStoreMax(savedStoreMax);
    }

    private void useTempDir() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    private String shippedTerrain() throws IOException {
        return Files.readString(Path.of(savedGamedata, "terrain.txt"));
    }

    private void writeTerrain(String text) throws IOException {
        Files.writeString(tempDir.resolve("terrain.txt"), text);
        useTempDir();
    }

    /** A private {@link Feature} field, which has no accessor yet. */
    private static Object field(Feature feature, String name) throws Exception {
        Field f = Feature.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.get(feature);
    }

    private static Feature feature(TerrainFlags code) {
        return TerrainRegistry.lookupFeature(code);
    }

    /** The shipped file, checked against the values read from C's data. */
    @Nested
    @DisplayName("against the shipped terrain.txt")
    class RealData {

        @BeforeEach
        void load() {
            TerrainRegistry.setFeatures(null);
            TerrainDataLoader.loadTerrainFeatures();
        }

        @Test
        @DisplayName("loads one feature per FEAT_ code, in list-terrain.h order")
        void loadsEveryCodeInOrder() {
            List<TerrainFlags> codes = TerrainRegistry.getFeatures().stream()
                    .map(Feature::getCodeFlags).toList();

            assertEquals(C_ORDER, codes);
            assertEquals(TerrainFlags.FEAT_MAX.ordinal(), codes.size());
        }

        @Test
        @DisplayName("hands out shop numbers 1 to 8 to the stores and the home, 0 to the rest, and sets store_max to 8")
        void shopNumbers() throws Exception {
            for (Feature f : TerrainRegistry.getFeatures()) {
                int expected = C_SHOPS.contains(f.getCodeFlags()) ? C_SHOPS.indexOf(f.getCodeFlags()) + 1 : 0;
                assertEquals(expected, field(f, "shopNum"), f.getName());
            }
            assertEquals(8, GameConstants.getStoreMax());
        }

        @Test
        @DisplayName("gives only the secret door a mimic, and it is granite; the rest are not mimicking, not FEAT_NONE")
        void mimics() {
            for (Feature f : TerrainRegistry.getFeatures()) {
                assertEquals(f.getCodeFlags() == TerrainFlags.FEAT_SECRET, f.isMimicing(), f.getName());
            }
            assertEquals(TerrainFlags.FEAT_GRANITE, feature(TerrainFlags.FEAT_SECRET).getMimic().getCodeFlags());
            // with no mimic, getMimic() answers the feature itself, as C's "mimic ? mimic : &f_info[actual]" does
            assertEquals(TerrainFlags.FEAT_FLOOR, feature(TerrainFlags.FEAT_FLOOR).getMimic().getCodeFlags());
        }

        @Test
        @DisplayName("reads digging: on the nine features that have one and leaves the rest at 0")
        void digging() throws Exception {
            for (Feature f : TerrainRegistry.getFeatures()) {
                assertEquals(C_DIGGING.getOrDefault(f.getCodeFlags(), 0), field(f, "dig"), f.getName());
            }
        }

        @Test
        @DisplayName("reads priority: as an integer")
        void priority() throws Exception {
            assertEquals(2, field(feature(TerrainFlags.FEAT_NONE), "priority"));
            assertEquals(5, field(feature(TerrainFlags.FEAT_FLOOR), "priority"));
            assertEquals(17, field(feature(TerrainFlags.FEAT_CLOSED), "priority"));
            assertEquals(13, field(feature(TerrainFlags.FEAT_RUBBLE), "priority"));
        }

        @Test
        @DisplayName("adds the trailing space to look-prefix and look-in-preposition, and leaves absent ones empty")
        void lookStrings() throws Exception {
            for (TerrainFlags shop : C_SHOPS) {
                assertEquals("the entrance to the ", field(feature(shop), "lookPrefix"), shop.name());
                assertEquals("at ", field(feature(shop), "lookInPreposition"), shop.name());
            }
            assertEquals("some ", field(feature(TerrainFlags.FEAT_LAVA), "lookPrefix"));
            assertEquals("", field(feature(TerrainFlags.FEAT_LAVA), "lookInPreposition"));
            assertEquals("in ", field(feature(TerrainFlags.FEAT_OPEN), "lookInPreposition"));
            assertEquals("in ", field(feature(TerrainFlags.FEAT_BROKEN), "lookInPreposition"));
            assertEquals("", field(feature(TerrainFlags.FEAT_FLOOR), "lookPrefix"));
            assertEquals("", field(feature(TerrainFlags.FEAT_FLOOR), "lookInPreposition"));
        }

        @Test
        @DisplayName("gives lava the IM_FIRE resist flag and every other feature RF_NONE")
        void resistFlag() throws Exception {
            for (Feature f : TerrainRegistry.getFeatures()) {
                MonsterRaceFlag expected = f.getCodeFlags() == TerrainFlags.FEAT_LAVA
                        ? MonsterRaceFlag.RF_IM_FIRE : MonsterRaceFlag.RF_NONE;
                assertEquals(expected, field(f, "resistFlag"), f.getName());
            }
        }

        @Test
        @DisplayName("builds the display character from the graphics: glyph and colour")
        void displayCharacter() throws Exception {
            AngbandDisplayCharacter none = (AngbandDisplayCharacter) field(feature(TerrainFlags.FEAT_NONE), "displayCharacter");
            AngbandDisplayCharacter closed = (AngbandDisplayCharacter) field(feature(TerrainFlags.FEAT_CLOSED), "displayCharacter");
            AngbandDisplayCharacter rubble = (AngbandDisplayCharacter) field(feature(TerrainFlags.FEAT_RUBBLE), "displayCharacter");

            assertEquals(' ', none.getNarrowCharacter());
            assertEquals(ColourEnum.fromCode('w'), none.getAttributeColour());
            assertEquals('+', closed.getNarrowCharacter());
            assertEquals(ColourEnum.fromCode('U'), closed.getAttributeColour());
            // a ':' glyph still reads as the glyph, not the separator
            assertEquals(':', rubble.getNarrowCharacter());
            assertEquals(ColourEnum.fromCode('W'), rubble.getAttributeColour());
        }

        @Test
        @DisplayName("loading twice replaces the list rather than appending")
        void reloadReplaces() {
            TerrainDataLoader.loadTerrainFeatures();

            assertEquals(25, TerrainRegistry.getFeatures().size());
            assertEquals(8, GameConstants.getStoreMax());
        }
    }

    /** What happens to the registry when the file cannot be used. */
    @Nested
    @DisplayName("when the file cannot be used")
    class FailurePaths {

        private List<Feature> sentinel;

        @BeforeEach
        void plantSentinel() {
            sentinel = new ArrayList<>();
            TerrainRegistry.setFeatures(sentinel);
        }

        @Test
        @DisplayName("a missing terrain.txt throws, as parse_file_quit_not_found quits, and leaves the registry alone")
        void missingFile() {
            useTempDir();

            RuntimeException thrown = assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            assertTrue(thrown.getMessage().contains("terrain.txt"));
            assertSame(sentinel, installedList());
        }

        @Test
        @DisplayName("a file in which every record is dropped throws and leaves the registry alone")
        void everyRecordDropped() throws IOException {
            writeTerrain("record-count:1\n\ncode:NOTACODE\nname:nothing\ngraphics:.:w\n\n");

            assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            assertSame(sentinel, installedList());
        }

        @Test
        @DisplayName("one bad flag in an otherwise good file throws, as C quits, and leaves the registry and store_max alone")
        void oneBadFlagIsFatal() throws IOException {
            GameConstants.setStoreMax(99);
            writeTerrain(shippedTerrain().replace("flags:LOS | PROJECT | PASSABLE | FLOOR",
                    "flags:NOTAFLAG | PASSABLE | FLOOR"));

            RuntimeException thrown = assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            assertTrue(thrown.getMessage().contains("NOTAFLAG"), thrown.getMessage());
            assertSame(sentinel, installedList());
            assertEquals(99, GameConstants.getStoreMax());
        }

        @Test
        @DisplayName("a file that lacks the last record loads the other 24, as C does, but has no entry for the missing code")
        void shortFileLoads() throws IOException {
            String shipped = shippedTerrain();
            String withoutLast = shipped.substring(0, shipped.indexOf("code:PASS_RUBBLE"))
                    .replace("record-count:25", "record-count:24");
            writeTerrain(withoutLast);

            TerrainDataLoader.loadTerrainFeatures();

            assertEquals(24, TerrainRegistry.getFeatures().size());
            // C hands back a zeroed f_info slot here; the list has nothing to hand back
            assertNull(feature(TerrainFlags.FEAT_PASS_RUBBLE));
            assertEquals("lava", feature(TerrainFlags.FEAT_LAVA).getName());
        }

        @Test
        @DisplayName("a record that repeats an earlier code throws, as C quits, and leaves the registry and store_max alone")
        void duplicateCodeIsFatal() throws IOException {
            GameConstants.setStoreMax(99);
            String shipped = shippedTerrain();
            String floor = shipped.substring(shipped.indexOf("code:FLOOR"), shipped.indexOf("code:CLOSED"))
                    .replace("name:open floor", "name:second floor");
            writeTerrain(shipped.replace("record-count:25", "record-count:26")
                    .replace("code:CLOSED", floor + "code:CLOSED"));

            RuntimeException thrown = assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            assertTrue(thrown.getMessage().contains("second name value: second floor"), thrown.getMessage());
            assertTrue(thrown.getMessage().contains("FLOOR"), thrown.getMessage());
            assertSame(sentinel, installedList());
            assertEquals(99, GameConstants.getStoreMax());
        }

        @Test
        @DisplayName("a bad flag on a shop entrance throws before the shops are numbered, so store_max is left alone")
        void badShopEntranceLeavesStoreMaxAlone() throws Exception {
            GameConstants.setStoreMax(99);
            String shipped = shippedTerrain();
            int armour = shipped.indexOf("code:STORE_ARMOR");
            int flagsLine = shipped.indexOf("flags:SHOP", armour);
            writeTerrain(shipped.substring(0, flagsLine) + "flags:NOTAFLAG | SHOP"
                    + shipped.substring(flagsLine + "flags:SHOP".length()));

            assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            assertSame(sentinel, installedList());
            assertEquals(99, GameConstants.getStoreMax());
        }

        @Test
        @DisplayName("a failed load leaves store_max at its previous value, since the shop numbering runs after the error check")
        void storeMaxSurvivesAFailedLoad() throws IOException {
            GameConstants.setStoreMax(99);
            // no records survive: the only record has a bad code, so the parse has errors and no features
            writeTerrain("record-count:1\n\ncode:NOTACODE\nname:nothing\ngraphics:.:w\n\n");

            assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            assertSame(sentinel, installedList());
            assertEquals(99, GameConstants.getStoreMax());
        }

        /** The list object the registry holds, read without the unmodifiable wrapper. */
        private List<Feature> installedList() {
            try {
                Field f = TerrainRegistry.class.getDeclaredField("features");
                f.setAccessible(true);
                @SuppressWarnings("unchecked") List<Feature> list = (List<Feature>) f.get(null);
                return list;
            } catch (ReflectiveOperationException e) {
                throw new AssertionError(e);
            }
        }
    }

    /** The accepted deviation: C turns an unknown colour into white, the port's lexer rejects it. */
    @Nested
    @DisplayName("the colour check, stricter than C")
    class ColourCheck {

        @Test
        @DisplayName("an unknown colour is rejected rather than read as white")
        void unknownColourIsRejected() throws IOException {
            List<Feature> sentinel = new ArrayList<>();
            TerrainRegistry.setFeatures(sentinel);
            writeTerrain(shippedTerrain().replace("graphics:.:w", "graphics:.:Q"));

            RuntimeException thrown = assertThrows(RuntimeException.class, TerrainDataLoader::loadTerrainFeatures);

            // the lexer only has tokens for the colours in C's table, so this is a hard grammar error that
            // fails the whole file, not a dropped record that the count guard then catches
            assertFalse(thrown.getMessage().contains("Wrong number"), thrown.getMessage());
            assertTrue(TerrainRegistry.getFeatures().isEmpty());
        }

        @Test
        @DisplayName("a colour spelled as a name resolves, as color_text_to_attr does")
        void colourNameResolves() throws Exception {
            writeTerrain(shippedTerrain().replace("graphics:.:w", "graphics:.:Light Umber"));

            TerrainDataLoader.loadTerrainFeatures();

            AngbandDisplayCharacter floor = (AngbandDisplayCharacter) field(feature(TerrainFlags.FEAT_FLOOR), "displayCharacter");
            assertEquals(ColourEnum.fromCode('U'), floor.getAttributeColour());
        }
    }
}
