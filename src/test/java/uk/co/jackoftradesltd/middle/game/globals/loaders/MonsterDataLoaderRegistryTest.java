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
import uk.co.jackoftradesltd.middle.monsters.MonsterRace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests what {@link MonsterDataLoader} leaves in {@link MonsterRegistry} after the whole init chain
 * has run against the real {@code lib/gamedata}.
 *
 * <p>The expected values are counts taken from the C distribution's own data files, not from the
 * Java readers: 12 sets in {@code pain.txt}, 19 in {@code blow_methods.txt}, 30 in
 * {@code blow_effects.txt}, 40 in {@code pit.txt} and 624 {@code name:} records in
 * {@code monster.txt}, the first being {@code <player>}. The longest blow list in the file belongs to
 * the 9-headed hydra, nine {@code blow:} lines, which is what C's {@code finish_parse_monster} takes
 * as {@code z_info->mon_blows_max}. C's own counters ({@code mp_max}, {@code blow_methods_max},
 * {@code blow_effects_max}, {@code pit_max}, {@code r_max}) are all one higher than these because
 * each array carries a blank slot; that is the accepted deviation recorded in the registry's class
 * block, so the tests assert the plain record count.
 *
 * <p>A successful {@code loadMonsters} also proves the second pass: every friend and shape
 * reference in the 624 races resolved, because an unresolvable one throws out of
 * {@code resolveFriends} or {@code resolveShapes}. Lore sizing has its own class,
 * {@link MonsterDataLoaderLoreTest}.
 *
 * <p>Class MonsterDataLoaderRegistryTest coded on 261013, commented in full on 261013.
 *
 * @author Rowan Crowther
 */
class MonsterDataLoaderRegistryTest {

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
    @DisplayName("monster.txt registers 624 races, <player> first")
    void racesLoaded() {
        List<MonsterRace> races = MonsterRegistry.getMonsterRaces();

        assertEquals(624, races.size());
        assertEquals(624, MonsterRegistry.getMonsterRaceMax());
        assertEquals("<player>", races.getFirst().getName());
        assertSame(races.getFirst(), MonsterRegistry.lookupMonsterRace("<player>"));
    }

    @Test
    @DisplayName("mon_blows_max is nine, the 9-headed hydra's blow count, and its lore is sized to match")
    void longestBlowListIsTheHydras() {
        assertEquals(9, MonsterRegistry.getMonsterBlowsMax());

        MonsterRace hydra = MonsterRegistry.lookupMonsterRace("9-headed hydra");
        assertEquals(9, hydra.getBlows().size());
        assertEquals(9, hydra.getLore().getBlows().size());
    }

    @Test
    @DisplayName("the <player> record has no blows, so its lore has no blow slots")
    void playerRecordHasNoBlows() {
        MonsterRace player = MonsterRegistry.getMonsterRaces().getFirst();

        assertEquals(0, player.getBlows().size());
        assertEquals(0, player.getLore().getBlows().size());
    }

    @Test
    @DisplayName("pain.txt registers 12 sets, indices 1 to 12, and nothing at 13")
    void painSetsLoaded() {
        assertEquals(12, MonsterRegistry.getMonsterPainMsgMax());
        for (int index = 1; index <= 12; index++) {
            assertNotNull(MonsterRegistry.getPainFromIndex(index), "pain set " + index);
        }
        assertNull(MonsterRegistry.getPainFromIndex(13));
    }

    @Test
    @DisplayName("blow methods, blow effects and pit profiles carry the plain record counts")
    void countersAreRecordCounts() {
        assertEquals(19, MonsterRegistry.getMonsterBlowsMethodsMax());
        assertEquals(30, MonsterRegistry.getMonsterBlowsEffectsMax());
        assertEquals(40, MonsterRegistry.getMonsterPitTypeMax());
    }

    @Test
    @DisplayName("monster bases and summons resolve by exact name, with no substring fallback")
    void basesAndSummonsResolve() {
        assertNotNull(MonsterRegistry.lookupMonsterBase("canine"));
        assertNull(MonsterRegistry.lookupMonsterBase("canin"));

        assertNotNull(MonsterRegistry.lookupSummon("KIN"));
        assertNull(MonsterRegistry.lookupSummon("NOSUCHSUMMON"));
    }

    @Test
    @DisplayName("visuals.txt registers both the cycler table and the flicker table")
    void visualTablesLoaded() {
        assertNotNull(MonsterRegistry.getVisualsCyclerTable());
        assertNotNull(MonsterRegistry.visualsFlickerTable);
    }
}
