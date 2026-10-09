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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.enums.ChannelEntryFlag;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.uichannel.UIEntryValue;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.combiners.CombinerName;
import uk.co.jackoftradesltd.middle.game.globals.cached.CachedPlayerData;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.PlayerProperty;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.PlayerShape;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Probe for the cache-fill block of C's {@code compute_ui_entry_values_for_player}: when
 * {@code *cache} is NULL it is filled once from {@code player_flags}, {@code player_flags_timed}
 * and {@code p->timed[TMD_TRAPSAFE]} (non-zero only while the effect runs), and the caller keeps it.
 */
@ExtendWith(SeededPlayerRegistry.class)
class CachedPlayerDataProbeTest {
    private static final String ENTRY = "PROBE";

    /**
     * A player whose race grants OF_FREE_ACT and whose class grants nothing.
     */
    private static Player playerWithRaceFreeAct() {
        Flag<ObjectFlag> raceFlags = new Flag<>(ObjectFlag.class);
        raceFlags.on(ObjectFlag.OF_FREE_ACT);
        Map<uk.co.jackoftradesltd.middle.enums.Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        PlayerRace race = new PlayerRace("Probe Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0,
                SeededPlayerRegistry.humanoidBody(), stats, skills, raceFlags,
                new Flag<>(PlayerFlag.class), null, new HashMap<>());
        PlayerClass cls = new PlayerClass("Probe Class", List.of(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), 0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                0, 0, 0, List.of(), null);
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        player.setRace(race);
        player.setClass(cls);
        player.setState(new uk.co.jackoftradesltd.middle.player.PlayerState());
        return player;
    }

    private static PlayerShape plainShape() {
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        Map<ObjectModifier, Integer> mods = new HashMap<>();
        Map<ElementEnum, ElementInfo> els = new HashMap<>();
        for (ElementEnum e : ElementEnum.values()) {
            if (e == ElementEnum.ELEM_NONE || e == ElementEnum.ELEM_MAX) continue;
            els.put(e, new ElementInfo());
        }
        return new PlayerShape("S", 0, 0, 0, skills, new Flag<>(ObjectFlag.class),
                new Flag<>(PlayerFlag.class), mods, els, List.of(), 0, List.of());
    }

    private static PlayerProperty objectFlagPlayerProperty(ObjectFlag oFlag) {
        return new PlayerProperty(PlayerProperty.PlayerPropertyType.PROP_TYPE_OBJECT, null, oFlag, null, null,
                List.of(new PlayerProperty.BindUI(ENTRY, 0, true, false)),
                "test", "", 0);
    }

    @BeforeEach
    void reset() {
        UIEntryValueRegistry.clearEntryBindings();
    }

    // --- CachedPlayerData.populateFlags on its own ------------------------------------------

    @Test
    @DisplayName("populateFlags fills untimed from the player's race (player_flags)")
    void populateFillsUntimedFromRace() {
        CachedPlayerData cache = new CachedPlayerData();
        cache.populateFlags(playerWithRaceFreeAct());
        assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
    }

    @Test
    @DisplayName("TMD_TRAPSAFE at 0 does not put OF_TRAP_IMMUNE in the timed set")
    void trapSafeAtZeroLeavesTimedEmpty() {
        CachedPlayerData cache = new CachedPlayerData();
        cache.populateFlags(playerWithRaceFreeAct());
        assertFalse(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
    }

    @Test
    @DisplayName("TMD_TRAPSAFE running puts OF_TRAP_IMMUNE in the timed set")
    void trapSafeRunningSetsTimedFlag() {
        Player player = playerWithRaceFreeAct();
        player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);
        CachedPlayerData cache = new CachedPlayerData();
        cache.populateFlags(player);
        assertTrue(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
    }

    @Test
    @DisplayName("onTimedFlag before populateFlags does not stop the fill")
    void onTimedFlagBeforePopulateStillFills() {
        CachedPlayerData cache = new CachedPlayerData();
        cache.onTimedFlag(ObjectFlag.OF_FEATHER);
        cache.populateFlags(playerWithRaceFreeAct());
        assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
    }

    @Test
    @DisplayName("getUntimedFlags before populateFlags does not stop the fill")
    void getterBeforePopulateStillFills() {
        CachedPlayerData cache = new CachedPlayerData();
        cache.getUntimedFlags();
        cache.getTimedFlags();
        cache.populateFlags(playerWithRaceFreeAct());
        assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
    }

    @Test
    @DisplayName("the cache is filled once: a second populateFlags does not refresh it")
    void filledOnce() {
        Player player = playerWithRaceFreeAct();
        CachedPlayerData cache = new CachedPlayerData();
        cache.populateFlags(player);
        player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);
        cache.populateFlags(player);
        assertFalse(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
    }

    // --- through computeForPlayer -----------------------------------------------------------

    @Test
    @DisplayName("a fresh, non-null cache is filled by computeForPlayer (C: *cache == NULL)")
    void freshCacheIsFilledByCompute() {
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                List.of(objectFlagPlayerProperty(ObjectFlag.OF_FREE_ACT)), CombinerName.ADD,
                new Flag<>(ChannelEntryFlag.class));
        Player player = playerWithRaceFreeAct();
        player.setShape(plainShape());

        CachedPlayerData cache = new CachedPlayerData();
        UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, cache);

        assertEquals(1, result.val(), "race grants OF_FREE_ACT; shape contributes nothing");
        assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT), "the caller keeps the filled cache");
    }

    @Test
    @DisplayName("TIMED_AS_AUX on OF_TRAP_IMMUNE: aux is 0 while TMD_TRAPSAFE is not running")
    void trapImmuneAuxIsZeroWhenNotRunning() {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                List.of(objectFlagPlayerProperty(ObjectFlag.OF_TRAP_IMMUNE)), CombinerName.ADD, flags);
        Player player = playerWithRaceFreeAct();
        player.setShape(plainShape());

        UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData());
        assertEquals(0, result.auxVal());
    }

    @Test
    @DisplayName("null cache, TIMED_AS_AUX on OF_TRAP_IMMUNE: aux is 0 while TMD_TRAPSAFE is not running")
    void nullCacheTrapImmuneAuxIsZeroWhenNotRunning() {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                List.of(objectFlagPlayerProperty(ObjectFlag.OF_TRAP_IMMUNE)), CombinerName.ADD, flags);
        Player player = playerWithRaceFreeAct();
        player.setShape(plainShape());

        UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, null);
        assertEquals(0, result.auxVal());
    }

    @Test
    @DisplayName("null cache, TIMED_AS_AUX on OF_TRAP_IMMUNE: aux is 1 while TMD_TRAPSAFE is running")
    void nullCacheTrapImmuneAuxIsOneWhenRunning() {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                List.of(objectFlagPlayerProperty(ObjectFlag.OF_TRAP_IMMUNE)), CombinerName.ADD, flags);
        Player player = playerWithRaceFreeAct();
        player.setShape(plainShape());
        player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);

        UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, null);
        assertEquals(1, result.auxVal());
    }

    @Test
    @DisplayName("TIMED_AS_AUX on OF_TRAP_IMMUNE: aux is 1 while TMD_TRAPSAFE is running")
    void trapImmuneAuxIsOneWhenRunning() {
        Flag<ChannelEntryFlag> flags = new Flag<>(ChannelEntryFlag.class);
        flags.on(ChannelEntryFlag.ENTRY_FLAG_TIMED_AS_AUX);
        UIEntryValueRegistry.addEntryBinding(ENTRY, List.of(),
                List.of(objectFlagPlayerProperty(ObjectFlag.OF_TRAP_IMMUNE)), CombinerName.ADD, flags);
        Player player = playerWithRaceFreeAct();
        player.setShape(plainShape());
        player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);

        UIEntryValue result = UIEntryValueRegistry.computeForPlayer(ENTRY, player, new CachedPlayerData());
        assertEquals(1, result.auxVal());
    }
}
