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
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.game.globals.registry.UIEntryValueRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@code bindui:} wiring phase of {@link GameConstants#init(Core)} — the block that
 * groups every loaded {@link ObjectProperty}/{@link PlayerProperty} by the {@link UIEntrySpec} its
 * {@code bindui:} lines name and hands each group to
 * {@link UIEntryValueRegistry#addEntryBinding}. That block has no direct C counterpart to compare
 * against clause by clause (see {@link GameConstants#init(Core)}'s own Javadoc for why the port
 * batches what C does incrementally), so this pins its actual output against real
 * {@code object_property.txt}/{@code player_property.txt} data instead: a full
 * {@link GameConstants#init(Core)} is run once, the same way {@code PitReaderTest#bootstrap()}
 * runs it, and the private {@link UIEntryValueRegistry} state the wiring block feeds is read back
 * through reflection, since nothing public exposes it directly.
 *
 * <p>{@code resist_ui_compact_0<DARK>} is bound from both sides of the wiring at once —
 * {@code object_property.txt}'s {@code "dark resistance"} record and
 * {@code player_property.txt}'s {@code PF_UNLIGHT} record both {@code bindui:} to it — which is
 * what lets {@link #playerPropertyReachesTheSameEntryAlongsideTheObjectHalf()} pin the one thing
 * genuinely specific to this block: that {@link UIEntryValueRegistry#addEntryBinding}'s
 * null-keeps-existing merge survives the two separate passes {@link GameConstants#init(Core)} makes
 * over the object-property and player-property maps, rather than the second pass silently erasing
 * the first's half of the binding.
 *
 * <p>Class GameConstantsInitUIBindingTest coded on 260924, commented in full on 260924.
 *
 * @author Rowan Crowther
 */
class GameConstantsInitUIBindingTest {

    @BeforeAll
    static void bootstrap() throws IOException {
        // Mirrors PitReaderTest's bootstrap(): the front end's UIEntry load has to happen and be
        // sent before GameConstants.init() reaches the point where it blocks waiting for it.
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

    /**
     * {@link GameConstants#init(Core)} populates {@link ObjectRegistry} and
     * {@link UIEntryValueRegistry} in place; reset both to the empty baseline so this heavy load
     * does not leak into order-sensitive suites, matching {@code PitReaderTest#cleanup()}.
     */
    @AfterAll
    static void cleanup() {
        ObjectRegistry.reset();
        UIEntryValueRegistry.clearEntryBindings();
    }

    /**
     * Reads {@link UIEntryValueRegistry}'s private {@code bindingByEntry} map, since nothing public
     * exposes the raw per-entry binding this test needs to inspect.
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> bindingByEntry() throws Exception {
        Field f = UIEntryValueRegistry.class.getDeclaredField("bindingByEntry");
        f.setAccessible(true);
        return (Map<String, Object>) f.get(null);
    }

    /**
     * @return the registered {@code EntryBinding} for {@code entryName}, or {@code null} if
     * nothing bound to it
     */
    private static Object binding(String entryName) throws Exception {
        return bindingByEntry().get(entryName);
    }

    /**
     * Reads one record component off the private {@code EntryBinding} instance {@code binding} by
     * name, since the record type itself is inaccessible from outside {@link UIEntryValueRegistry}.
     */
    @SuppressWarnings("unchecked")
    private static <T> T component(Object binding, String name) throws Exception {
        Method m = binding.getClass().getDeclaredMethod(name);
        m.setAccessible(true);
        return (T) m.invoke(binding);
    }

    @Test
    @DisplayName("an object property's bindui: line reaches the entry binding registry")
    void objectPropertyReachesItsBoundEntry() throws Exception {
        Object entryBinding = binding("resist_ui_compact_0<DARK>");
        assertNotNull(entryBinding, "no binding registered for resist_ui_compact_0<DARK>");

        List<ObjectProperty> objProps = component(entryBinding, "objectProperties");
        assertNotNull(objProps);
        assertTrue(objProps.stream().anyMatch(p -> "dark resistance".equals(p.getName())),
                () -> "object properties bound: " + objProps.stream().map(ObjectProperty::getName).toList());
    }

    @Test
    @DisplayName("a stat object property reaches its own tagged entry")
    void statObjectPropertyReachesItsBoundEntry() throws Exception {
        Object entryBinding = binding("stat_mod_ui_compact_0<STR>");
        assertNotNull(entryBinding, "no binding registered for stat_mod_ui_compact_0<STR>");

        List<ObjectProperty> objProps = component(entryBinding, "objectProperties");
        assertNotNull(objProps);
        assertTrue(objProps.stream().anyMatch(p -> "strength".equals(p.getName())),
                () -> "object properties bound: " + objProps.stream().map(ObjectProperty::getName).toList());
    }

    @Test
    @DisplayName("a player property's bindui: line reaches the same entry, without erasing the object half")
    void playerPropertyReachesTheSameEntryAlongsideTheObjectHalf() throws Exception {
        Object entryBinding = binding("resist_ui_compact_0<DARK>");
        assertNotNull(entryBinding, "no binding registered for resist_ui_compact_0<DARK>");

        List<PlayerProperty> playerProps = component(entryBinding, "playerProperties");
        assertNotNull(playerProps);
        assertTrue(playerProps.stream().anyMatch(p -> p.getpCode() == PlayerFlag.PF_UNLIGHT),
                () -> "player properties bound: " + playerProps.stream().map(PlayerProperty::getName).toList());

        // The object half a sibling test also pins must still be here - proving the player-property
        // pass's addEntryBinding call kept the object-property pass's list rather than nulling it.
        List<ObjectProperty> objProps = component(entryBinding, "objectProperties");
        assertNotNull(objProps);
        assertFalse(objProps.isEmpty());
    }

    @Test
    @DisplayName("an entry name nothing binds to has no registered binding")
    void anUnboundEntryHasNoBinding() throws Exception {
        assertNull(binding("not_a_real_ui_entry_name"));
    }
}
