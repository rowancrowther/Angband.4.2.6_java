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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.cave.profiles.dungeon.CaveProfile;
import uk.co.jackoftradesltd.middle.cave.profiles.room.RoomTemplate;
import uk.co.jackoftradesltd.middle.cave.profiles.vault.Vault;
import uk.co.jackoftradesltd.middle.game.globals.loaders.DungeonLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DungeonRegistry}, the port's home for the three file-scope globals of C's
 * {@code generate.c}: {@code cave_profiles}, {@code room_templates} and {@code vaults}.
 *
 * <p><b>Expected values come from C, not from the port.</b> In C each global is a plain pointer
 * assigned by the matching {@code finish_parse_*} function: it is {@code NULL} before the parser
 * runs, the assignment replaces whatever was there, and the three are independent of one another.
 * The order of the contents is also C's: {@code finish_parse_profile()} copies the prepended
 * profile list into the array back to front, so {@code cave_profiles} is in file order, whereas
 * {@code parse_room_name()} and {@code parse_vault_name()} prepend and {@code finish_parse_room()}
 * and {@code finish_parse_vault()} keep the head, so {@code room_templates} and {@code vaults} run
 * in reverse file order. The last records of {@code room_template.txt} and {@code vault.txt} are
 * therefore the first of C's lists, read from the shipped files with {@code grep}.
 *
 * <p><b>What is tested.</b> Each setter installs the list it is given, by identity and not as a
 * copy; a second call replaces the first; {@code null} returns a list to the unloaded state; one
 * setter leaves the other two alone; and the room templates, loaded from the real
 * {@code room_template.txt}, arrive in reverse file order. The vaults' order is checked in
 * {@code DungeonLoaderTest}, because that loader needs {@code GameConstants}.
 *
 * <p>The fields have no accessor yet, so they are read by reflection. The three lists are saved
 * before each test and put back afterwards.
 *
 * <p>Class DungeonRegistryTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
@DisplayName("DungeonRegistry")
class DungeonRegistryTest {

    private List<CaveProfile> savedProfiles;
    private List<RoomTemplate> savedRooms;
    private List<Vault> savedVaults;

    /**
     * Saves the three registry lists and clears them, so each test starts from C's unloaded state.
     */
    @BeforeEach
    void saveAndClear() throws Exception {
        savedProfiles = read("caveProfiles");
        savedRooms = read("roomTemplates");
        savedVaults = read("vaultTemplates");
        DungeonRegistry.setCaveProfiles(null);
        DungeonRegistry.setRoomTemplates(null);
        DungeonRegistry.setVaultTemplates(null);
    }

    /**
     * Puts back the lists saved by {@link #saveAndClear()}.
     */
    @AfterEach
    void restore() {
        DungeonRegistry.setCaveProfiles(savedProfiles);
        DungeonRegistry.setRoomTemplates(savedRooms);
        DungeonRegistry.setVaultTemplates(savedVaults);
    }

    /**
     * Reads one of {@code DungeonRegistry}'s lists, which have no accessor yet.
     *
     * @param field the registry field name
     * @return the list, or {@code null} if nothing has been installed
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> read(String field) throws Exception {
        Field f = DungeonRegistry.class.getDeclaredField(field);
        f.setAccessible(true);
        return (List<T>) f.get(null);
    }

    @Test
    @DisplayName("each setter installs the very list it is given, not a copy")
    void settersStoreByIdentity() throws Exception {
        List<CaveProfile> profiles = new ArrayList<>();
        List<RoomTemplate> rooms = new ArrayList<>();
        List<Vault> vaults = new ArrayList<>();

        DungeonRegistry.setCaveProfiles(profiles);
        DungeonRegistry.setRoomTemplates(rooms);
        DungeonRegistry.setVaultTemplates(vaults);

        assertSame(profiles, read("caveProfiles"));
        assertSame(rooms, read("roomTemplates"));
        assertSame(vaults, read("vaultTemplates"));
    }

    /**
     * C's {@code finish_parse_*} functions assign the pointer, so a second run leaves only the
     * second set.
     */
    @Test
    @DisplayName("a second call replaces the first rather than appending")
    void secondCallReplaces() throws Exception {
        List<CaveProfile> first = new ArrayList<>();
        List<CaveProfile> second = new ArrayList<>();
        DungeonRegistry.setCaveProfiles(first);
        DungeonRegistry.setCaveProfiles(second);
        assertSame(second, read("caveProfiles"));
        assertNotSame(first, read("caveProfiles"));

        List<RoomTemplate> firstRooms = new ArrayList<>();
        List<RoomTemplate> secondRooms = new ArrayList<>();
        DungeonRegistry.setRoomTemplates(firstRooms);
        DungeonRegistry.setRoomTemplates(secondRooms);
        assertSame(secondRooms, read("roomTemplates"));

        List<Vault> firstVaults = new ArrayList<>();
        List<Vault> secondVaults = new ArrayList<>();
        DungeonRegistry.setVaultTemplates(firstVaults);
        DungeonRegistry.setVaultTemplates(secondVaults);
        assertSame(secondVaults, read("vaultTemplates"));
    }

    /**
     * Before the parsers have run C's three pointers are {@code NULL}; a {@code null} install puts
     * the Java lists back in that state.
     */
    @Test
    @DisplayName("null returns each list to the unloaded state")
    void nullUnloads() throws Exception {
        DungeonRegistry.setCaveProfiles(new ArrayList<>());
        DungeonRegistry.setRoomTemplates(new ArrayList<>());
        DungeonRegistry.setVaultTemplates(new ArrayList<>());

        DungeonRegistry.setCaveProfiles(null);
        DungeonRegistry.setRoomTemplates(null);
        DungeonRegistry.setVaultTemplates(null);

        assertNull(read("caveProfiles"));
        assertNull(read("roomTemplates"));
        assertNull(read("vaultTemplates"));
    }

    /**
     * Each C global is assigned by its own parser, so installing one must not disturb the others.
     */
    @Test
    @DisplayName("installing one list leaves the other two untouched")
    void listsAreIndependent() throws Exception {
        List<RoomTemplate> rooms = new ArrayList<>();

        DungeonRegistry.setRoomTemplates(rooms);

        assertNull(read("caveProfiles"), "profiles");
        assertSame(rooms, read("roomTemplates"), "rooms");
        assertNull(read("vaultTemplates"), "vaults");

        List<Vault> vaults = new ArrayList<>();
        DungeonRegistry.setVaultTemplates(vaults);

        assertNull(read("caveProfiles"), "profiles");
        assertSame(rooms, read("roomTemplates"), "rooms");
        assertSame(vaults, read("vaultTemplates"), "vaults");
    }

    /**
     * {@code room_template.txt} runs from "Tiny hidden room" to "Rod chamber" (500 records), and
     * C's list is built by prepending, so its head is the last record in the file.
     */
    @Test
    @DisplayName("the loaded room templates run in reverse file order, as C's linked list does")
    void roomTemplatesAreInReverseFileOrder() throws Exception {
        DungeonLoader.loadRoomTemplates();

        List<RoomTemplate> rooms = read("roomTemplates");
        assertEquals(500, rooms.size(), "record count");
        assertEquals("Rod chamber", rooms.get(0).getName(), "C's head: the last record in the file");
        assertEquals("Tiny hidden room", rooms.get(rooms.size() - 1).getName(),
                "C's tail: the first record in the file");
    }
}
