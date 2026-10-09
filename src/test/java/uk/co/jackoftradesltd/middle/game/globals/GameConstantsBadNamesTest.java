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

package uk.co.jackoftradesltd.middle.game.globals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.directories.AngbandDirs;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.registry.MiscRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that a {@code names.txt} with a section C rejects stops {@code GameConstants.init}, as C's
 * {@code PARSE_ERROR_OUT_OF_BOUNDS} from {@code parse_names_section} ({@code init.c}) stops the
 * game from starting. {@code MiscDataLoader.loadNames} lets the exception from
 * {@code MiscRegistry.setNames} escape, and {@code init} wraps it in a {@link RuntimeException}
 * that names the file.
 *
 * <p>The game data is copied to a temporary directory with only {@code names.txt} replaced, so the
 * load runs through the real files up to the bad one. The registries a partial {@code init} fills
 * are reset afterwards, as the other whole-{@code init} suites do.
 *
 * @author Rowan Crowther
 */
@DisplayName("GameConstants.init — names.txt with a section C rejects")
class GameConstantsBadNamesTest {

    @TempDir
    Path tempDir;

    private String savedGamedata;

    @BeforeEach
    void pointAtCopy() throws IOException {
        savedGamedata = AngbandDirs.ANGBAND_DIRS.GAMEDATA.getPath();
        Path source = Path.of(savedGamedata);
        try (Stream<Path> files = Files.list(source)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                Files.copy(file, tempDir.resolve(file.getFileName()));
            }
        }
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(tempDir + File.separator);
    }

    @AfterEach
    void restore() {
        AngbandDirs.ANGBAND_DIRS.GAMEDATA.setPath(savedGamedata);
        ObjectRegistry.reset();
    }

    private static Core core() throws IOException {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> specs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            specs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }
        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(specs));
        return new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
    }

    @Test
    @DisplayName("section 3 stops start-up with an error that names names.txt")
    void sectionAtMarkerStopsStartUp() throws IOException {
        Files.writeString(tempDir.resolve("names.txt"),
                "record-count:2\nsection:1\nword:beleg\nsection:3\nword:bad\n");
        Core core = core();

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> GameConstants.init(core));

        assertTrue(thrown.getMessage().contains("names.txt"), thrown.getMessage());
        assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
    }

    @Test
    @DisplayName("a good names.txt does not stop start-up at the name load")
    void goodFileGetsPastTheNameLoad() throws IOException {
        Core core = core();

        try {
            GameConstants.init(core);
        } catch (RuntimeException later) {
            assertTrue(!later.getMessage().contains("names.txt"), later.getMessage());
        }

        assertEquals(false, MiscRegistry.getNameSection(
                uk.co.jackoftradesltd.middle.player.enums.RandnameType.RANDNAME_TOLKIEN).isEmpty());
    }
}
