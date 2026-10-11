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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.Channels;
import uk.co.jackoftradesltd.channel.StartupOptions;
import uk.co.jackoftradesltd.channel.messages.UIMessage;
import uk.co.jackoftradesltd.channel.uichannel.UIEntrySpec;
import uk.co.jackoftradesltd.frontend.entries.UIEntry;
import uk.co.jackoftradesltd.frontend.ui.globals.UIDataLoader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.Core;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterLore;
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@link MonsterDataLoader#loadMonsters()} gives every race its own blank
 * {@link MonsterLore}, the Java form of the {@code l_list} allocation at the end of C's
 * {@code finish_parse_monster} ({@code mon-init.c}).
 *
 * <p>C allocates {@code r_max} zeroed records, one per race and including index 0, each with a
 * blow array. The Java record lives on its race, and its blow list lines up by position with
 * {@code race.getBlows()}, so each list is as long as that race's own blow count (not the global
 * {@code mon_blows_max}). Per-blow known-ness is derived from {@code timesSeen}, so every slot has
 * to be a real, blank {@code MonsterBlow}: a null slot cannot report a count.
 *
 * <p>The whole init chain runs once against the real {@code lib/gamedata}, as
 * {@code MonsterReaderTest} does.
 *
 * @author Rowan Crowther
 */
class MonsterDataLoaderLoreTest {

    @BeforeAll
    static void bootstrap() throws IOException {
        UIDataLoader.loadUIEntryRenderers();
        UIDataLoader.loadUIEntryBases();
        UIDataLoader.loadUIEntries();

        List<UIEntrySpec> uiEntrySpecs = new ArrayList<>();
        for (UIEntry entry : UIRegistry.getUIEntries()) {
            uiEntrySpecs.add(new UIEntrySpec(entry.getName(), entry.getCombineType(), entry.getEntryFlag()));
        }

        Channels channels = Channels.create();
        channels.uiChannel().uiSender().send(new UIMessage.UIEntriesLoaded(uiEntrySpecs));
        Core core = new Core(channels.coreChannel(),
                new StartupOptions(false, false, false, false, "", "", List.of()));
        GameConstants.init(core);
    }

    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
    }

    @Test
    @DisplayName("every race, including the <player> placeholder at index 0, has a lore record")
    void everyRaceHasLore() {
        List<MonsterRace> races = MonsterRegistry.getMonsterRaces();
        assertFalse(races.isEmpty(), "monster.txt should have loaded");

        for (int i = 0; i < races.size(); i++) {
            assertNotNull(races.get(i).getLore(), "race " + i + " has no lore record");
        }
    }

    @Test
    @DisplayName("no two races share a lore record")
    void recordsAreNotShared() {
        Set<MonsterLore> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
            assertTrue(seen.add(race.getLore()), "a lore record is attached to more than one race");
        }
    }

    @Test
    @DisplayName("each lore's blow list is as long as its own race's blow list")
    void blowListMatchesRaceBlowCount() {
        List<MonsterRace> races = MonsterRegistry.getMonsterRaces();
        for (int i = 0; i < races.size(); i++) {
            MonsterRace race = races.get(i);
            assertEquals(race.getBlows().size(), race.getLore().getBlows().size(),
                    "blow slots for race " + i);
        }
    }

    @Test
    @DisplayName("every blow slot is a blank MonsterBlow that has never been seen")
    void blowSlotsAreBlankBlows() {
        List<MonsterRace> races = MonsterRegistry.getMonsterRaces();
        for (int i = 0; i < races.size(); i++) {
            MonsterLore lore = races.get(i).getLore();
            for (int slot = 0; slot < lore.getBlows().size(); slot++) {
                assertNotNull(lore.getBlows().get(slot), "race " + i + " blow slot " + slot + " is null");
                assertEquals(0, lore.getBlows().get(slot).getTimesSeen());
            }
        }
    }

    @Test
    @DisplayName("a fresh record is zeroed and knows nothing")
    void recordsStartZeroed() {
        for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
            MonsterLore lore = race.getLore();
            assertEquals(0, lore.getPkills());
            assertEquals(0, lore.getThefts());
            assertEquals(0, lore.getTkills());
            assertEquals(0, lore.getSightings());
            assertFalse(lore.isAllKnown());
            assertFalse(lore.isArmourKnown());
            assertFalse(lore.isDropKnown());
            assertFalse(lore.isSleepKnown());
        }
    }

    @Test
    @DisplayName("the longest lore blow list equals MonsterRegistry.getMonsterBlowsMax()")
    void longestListIsTheGlobalMaximum() {
        int longest = 0;
        for (MonsterRace race : MonsterRegistry.getMonsterRaces()) {
            longest = Math.max(longest, race.getLore().getBlows().size());
        }
        assertEquals(MonsterRegistry.getMonsterBlowsMax(), longest);
    }
}
