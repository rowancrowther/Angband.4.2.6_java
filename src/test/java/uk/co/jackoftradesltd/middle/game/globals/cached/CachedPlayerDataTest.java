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

package uk.co.jackoftradesltd.middle.game.globals.cached;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.objects.KnownObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.middle.player.PlayerClass;
import uk.co.jackoftradesltd.middle.player.PlayerRace;
import uk.co.jackoftradesltd.middle.player.PlayerState;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link CachedPlayerData}, the port of C's {@code struct cached_player_data} and of the
 * {@code if (*cache == NULL)} block at the top of {@code compute_ui_entry_values_for_player}
 * ({@code ui-entry.c}).
 *
 * <p>The expected values come from the C source, not from the port: {@code player_flags}
 * ({@code player.c}) is race flags, then class flags, then {@code OF_PROT_FEAR} for
 * {@code PF_BRAVERY_30} at level 30 or above; {@code player_flags_timed} adds the flag a running
 * effect duplicates and deliberately omits {@code TMD_TRAPSAFE}; and the block under test then adds
 * {@code OF_TRAP_IMMUNE} to the timed set when {@code p->timed[TMD_TRAPSAFE]} is non-zero. The
 * duplicate-flag pairings used are the shipped {@code flag-synonym} lines of
 * {@code player_timed.txt}: {@code OPP_CONF}/{@code PROT_CONF}, {@code SINVIS}/{@code SEE_INVIS},
 * {@code BOLD}/{@code PROT_FEAR} and {@code TRAPSAFE}/{@code TRAP_IMMUNE}.
 *
 * <p>The per-flag accessors have no C function of their own, since C writes
 * {@code of_on((*cache)->timed, f)} inline, so those tests pin the return conventions of
 * {@code flag_on}, {@code flag_off} and {@code flag_has} ({@code z-bitflag.c}) and the port's
 * allocate-on-write, never-allocate-on-read behaviour. {@code CachedPlayerDataProbeTest} covers the
 * same fill through {@code UIEntryValueRegistry.computeForPlayer}; this class tests the holder on
 * its own.
 *
 * <p>{@link PlayerRegistry} is global static state shared with the reader suites, so the loaded
 * timed effects are saved and put back around every test.
 *
 * <p>Class CachedPlayerDataTest coded on 261006, commented in full on 261006.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class CachedPlayerDataTest {

    /**
     * Whatever the registry held before this test, put back afterwards.
     */
    private Object savedEffects;

    /**
     * @return the registry's private list of loaded timed effects, made accessible
     * @throws Exception if the field cannot be reached
     */
    private static Field registryField() throws Exception {
        Field f = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        f.setAccessible(true);
        return f;
    }

    /**
     * Builds a timed-effect definition carrying only its identity and its duplicated flag.
     *
     * @param effect  the effect the definition is for
     * @param dupFlag the object flag it duplicates
     * @return the definition
     */
    private static PlayerTimedEffect definition(TimedEffect effect, ObjectFlag dupFlag) {
        return new PlayerTimedEffect(effect, "test effect", null, null, null, null,
                List.of(), List.of(), null, null, false, 0, dupFlag, false, null, null, null);
    }

    /**
     * Loads the registry with the four shipped pairings the tests rely on.
     *
     * @throws Exception if the field cannot be reached
     */
    private static void loadPairings() throws Exception {
        List<PlayerTimedEffect> all = new ArrayList<>();
        all.add(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
        all.add(definition(TimedEffect.TMD_SINVIS, ObjectFlag.OF_SEE_INVIS));
        all.add(definition(TimedEffect.TMD_BOLD, ObjectFlag.OF_PROT_FEAR));
        all.add(definition(TimedEffect.TMD_TRAPSAFE, ObjectFlag.OF_TRAP_IMMUNE));
        registryField().set(null, all);
    }

    /**
     * Builds a player whose race and class grant the given object flags, with an empty calculated
     * state at level 1.
     *
     * @param raceFlags  flags the race grants
     * @param classFlags flags the class grants
     * @return the player
     */
    private static Player player(Flag<ObjectFlag> raceFlags, Flag<ObjectFlag> classFlags) {
        Map<Stats, Integer> stats = new HashMap<>();
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        PlayerRace race = new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0,
                SeededPlayerRegistry.humanoidBody(), stats, skills, raceFlags,
                new Flag<>(PlayerFlag.class), null, new HashMap<>());
        PlayerClass cls = new PlayerClass("Test Class", List.of(), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), 0, 0, classFlags, new Flag<>(PlayerFlag.class),
                0, 0, 0, List.of(), null);
        Player player = new Player();
        player.setItemKnowledge(new KnownObject());
        player.setRace(race);
        player.setClass(cls);
        player.setState(new PlayerState());
        return player;
    }

    /**
     * @return a player with no race or class flags at all
     */
    private static Player plainPlayer() {
        return player(new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class));
    }

    /**
     * @param flags the flags to put in a new set
     * @return a set holding exactly those flags
     */
    private static Flag<ObjectFlag> setOf(ObjectFlag... flags) {
        Flag<ObjectFlag> set = new Flag<>(ObjectFlag.class);
        for (ObjectFlag flag : flags) set.on(flag);
        return set;
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        loadPairings();
    }

    @AfterEach
    void tearDown() throws Exception {
        registryField().set(null, savedEffects);
    }

    @Nested
    @DisplayName("populateFlags: the untimed set (C: player_flags)")
    class Untimed {

        @Test
        @DisplayName("race flags and class flags are both in untimed")
        void raceAndClassFlagsBothPresent() {
            Player player = player(setOf(ObjectFlag.OF_FREE_ACT), setOf(ObjectFlag.OF_SEE_INVIS));
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT), "from the race");
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_SEE_INVIS), "from the class");
        }

        @Test
        @DisplayName("a flag on both race and class is simply present")
        void flagOnBothIsPresent() {
            Player player = player(setOf(ObjectFlag.OF_FREE_ACT), setOf(ObjectFlag.OF_FREE_ACT));
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("a flag neither race nor class grants is absent")
        void ungrantedFlagAbsent() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player(setOf(ObjectFlag.OF_FREE_ACT), setOf(ObjectFlag.OF_SEE_INVIS)));
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_PROT_CONF));
        }

        @Test
        @DisplayName("PF_BRAVERY_30 at level 30 puts OF_PROT_FEAR in untimed")
        void bravery30AtThirty() {
            Player player = plainPlayer();
            player.getPlayerState().playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            player.setLevel(30);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        @DisplayName("PF_BRAVERY_30 at level 29 does not (boundary: >= 30)")
        void bravery30AtTwentyNine() {
            Player player = plainPlayer();
            player.getPlayerState().playerFlagOn(PlayerFlag.PF_BRAVERY_30);
            player.setLevel(29);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        @DisplayName("level 50 without PF_BRAVERY_30 does not get OF_PROT_FEAR")
        void highLevelWithoutPflag() {
            Player player = plainPlayer();
            player.setLevel(50);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        @DisplayName("a running timed effect's flag does not leak into untimed")
        void timedFlagNotInUntimed() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_OPP_CONF, 10);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_PROT_CONF));
        }
    }

    @Nested
    @DisplayName("populateFlags: the timed set (C: player_flags_timed + the TMD_TRAPSAFE hack)")
    class Timed {

        @Test
        @DisplayName("a running effect puts the flag it duplicates in timed")
        void runningEffectDuplicatesFlag() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_OPP_CONF, 10);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
        }

        @Test
        @DisplayName("an effect at 0 contributes nothing (C: p->timed[i] is tested first)")
        void dormantEffectContributesNothing() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(plainPlayer());
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_SEE_INVIS));
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        @DisplayName("an effect at 1 (the last turn) still contributes")
        void lastTurnStillContributes() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_SINVIS, 1);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_SEE_INVIS));
        }

        @Test
        @DisplayName("two running effects each contribute their own flag")
        void twoEffectsBothContribute() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_OPP_CONF, 10);
            player.putTimed(TimedEffect.TMD_BOLD, 10);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_PROT_FEAR));
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_SEE_INVIS));
        }

        @Test
        @DisplayName("an innate flag (race) is not in timed")
        void untimedFlagNotInTimed() {
            Player player = player(setOf(ObjectFlag.OF_FREE_ACT), setOf());
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("the same flag granted both ways is in both sets")
        void sameFlagInBothSets() {
            Player player = player(setOf(ObjectFlag.OF_PROT_CONF), setOf());
            player.putTimed(TimedEffect.TMD_OPP_CONF, 10);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_PROT_CONF));
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
        }

        @Test
        @DisplayName("TMD_TRAPSAFE running puts OF_TRAP_IMMUNE in timed, even though player_flags_timed omits it")
        void trapSafeRunning() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
        }

        @Test
        @DisplayName("TMD_TRAPSAFE at 1 (the last turn) still puts OF_TRAP_IMMUNE in timed")
        void trapSafeLastTurn() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_TRAPSAFE, 1);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
        }

        @Test
        @DisplayName("TMD_TRAPSAFE at 0 leaves OF_TRAP_IMMUNE out of timed")
        void trapSafeDormant() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(plainPlayer());
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
        }

        @Test
        @DisplayName("TMD_TRAPSAFE running never puts OF_TRAP_IMMUNE in untimed")
        void trapSafeNotInUntimed() {
            Player player = plainPlayer();
            player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
        }

        @Test
        @DisplayName("OF_TRAP_IMMUNE from the race is untimed only; a dormant TMD_TRAPSAFE adds nothing to timed")
        void trapImmuneFromRaceStaysUntimed() {
            Player player = player(setOf(ObjectFlag.OF_TRAP_IMMUNE), setOf());
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
        }
    }

    @Nested
    @DisplayName("populateFlags: fill once (C: *cache == NULL)")
    class FillOnce {

        @Test
        @DisplayName("a second call, after the player changes, leaves the first snapshot in place")
        void secondCallDoesNotRefresh() {
            Player player = plainPlayer();
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);

            player.putTimed(TimedEffect.TMD_OPP_CONF, 10);
            player.putTimed(TimedEffect.TMD_TRAPSAFE, 10);
            cache.populateFlags(player);

            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_TRAP_IMMUNE));
        }

        @Test
        @DisplayName("a second call with a different player is ignored too")
        void secondCallWithOtherPlayerIgnored() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player(setOf(ObjectFlag.OF_FREE_ACT), setOf()));
            cache.populateFlags(player(setOf(ObjectFlag.OF_SEE_INVIS), setOf()));
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_SEE_INVIS));
        }

        @Test
        @DisplayName("the sets survive a second call as the same objects")
        void setsKeptAcrossSecondCall() {
            Player player = plainPlayer();
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(player);
            Flag<ObjectFlag> untimedBefore = cache.getUntimedFlags();
            Flag<ObjectFlag> timedBefore = cache.getTimedFlags();
            cache.populateFlags(player);
            assertSame(untimedBefore, cache.getUntimedFlags());
            assertSame(timedBefore, cache.getTimedFlags());
        }

        @Test
        @DisplayName("separate caches for one player are independent")
        void separateCachesIndependent() {
            Player player = plainPlayer();
            CachedPlayerData first = new CachedPlayerData();
            first.populateFlags(player);
            player.putTimed(TimedEffect.TMD_OPP_CONF, 10);
            CachedPlayerData second = new CachedPlayerData();
            second.populateFlags(player);
            assertFalse(first.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
            assertTrue(second.hasTimedFlag(ObjectFlag.OF_PROT_CONF));
        }

        @Test
        @DisplayName("the first fill replaces flags set by hand beforehand; C never writes before the fill")
        void firstFillReplacesHandSetFlags() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.onTimedFlag(ObjectFlag.OF_FEATHER);
            cache.onUntimedFlag(ObjectFlag.OF_FEATHER);
            cache.populateFlags(plainPlayer());
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_FEATHER));
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_FEATHER));
        }

        @Test
        @DisplayName("a getter's set before the first fill is detached from the cache afterwards")
        void getterReferenceDetachedByFirstFill() {
            CachedPlayerData cache = new CachedPlayerData();
            Flag<ObjectFlag> early = cache.getUntimedFlags();
            cache.populateFlags(player(setOf(ObjectFlag.OF_FREE_ACT), setOf()));
            assertNotSame(early, cache.getUntimedFlags());
            assertFalse(early.has(ObjectFlag.OF_FREE_ACT));
            assertTrue(cache.getUntimedFlags().has(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("a flag set by hand after the fill is kept (the fill is not repeated)")
        void handSetAfterFillKept() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.populateFlags(plainPlayer());
            cache.onTimedFlag(ObjectFlag.OF_FEATHER);
            cache.populateFlags(plainPlayer());
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_FEATHER));
        }
    }

    @Nested
    @DisplayName("per-flag accessors (C: of_on / of_off / of_has on (*cache)->timed and ->untimed)")
    class Accessors {

        @Test
        @DisplayName("onTimedFlag returns true when it changed the set, false when already set (flag_on)")
        void onTimedReturnValue() {
            CachedPlayerData cache = new CachedPlayerData();
            assertTrue(cache.onTimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.onTimedFlag(ObjectFlag.OF_FREE_ACT));
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("onUntimedFlag returns true when it changed the set, false when already set (flag_on)")
        void onUntimedReturnValue() {
            CachedPlayerData cache = new CachedPlayerData();
            assertTrue(cache.onUntimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.onUntimedFlag(ObjectFlag.OF_FREE_ACT));
            assertTrue(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("offTimedFlag returns true when the flag was set, false when not (flag_off)")
        void offTimedReturnValue() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.onTimedFlag(ObjectFlag.OF_FREE_ACT);
            assertTrue(cache.offTimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.offTimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("offUntimedFlag returns true when the flag was set, false when not (flag_off)")
        void offUntimedReturnValue() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.onUntimedFlag(ObjectFlag.OF_FREE_ACT);
            assertTrue(cache.offUntimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.offUntimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("on an empty cache, the has/off accessors answer false")
        void emptyCacheReadsAndRemovesAnswerFalse() {
            CachedPlayerData cache = new CachedPlayerData();
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.offTimedFlag(ObjectFlag.OF_FREE_ACT));
            assertFalse(cache.offUntimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("the timed and untimed sets are independent")
        void setsIndependent() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.onTimedFlag(ObjectFlag.OF_FREE_ACT);
            assertFalse(cache.hasUntimedFlag(ObjectFlag.OF_FREE_ACT));
            cache.onUntimedFlag(ObjectFlag.OF_SEE_INVIS);
            assertFalse(cache.hasTimedFlag(ObjectFlag.OF_SEE_INVIS));
            cache.offUntimedFlag(ObjectFlag.OF_FREE_ACT);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_FREE_ACT));
        }

        @Test
        @DisplayName("getters return the live set: a flag set through the accessor shows in it")
        void gettersReturnLiveSets() {
            CachedPlayerData cache = new CachedPlayerData();
            cache.onTimedFlag(ObjectFlag.OF_FREE_ACT);
            cache.onUntimedFlag(ObjectFlag.OF_SEE_INVIS);
            assertTrue(cache.getTimedFlags().has(ObjectFlag.OF_FREE_ACT));
            assertTrue(cache.getUntimedFlags().has(ObjectFlag.OF_SEE_INVIS));
            cache.getTimedFlags().on(ObjectFlag.OF_FEATHER);
            assertTrue(cache.hasTimedFlag(ObjectFlag.OF_FEATHER));
        }

        @Test
        @DisplayName("the getters never answer null and repeat calls answer the same set")
        void gettersNonNullAndStable() {
            CachedPlayerData cache = new CachedPlayerData();
            assertNotNull(cache.getUntimedFlags());
            assertNotNull(cache.getTimedFlags());
            assertSame(cache.getUntimedFlags(), cache.getUntimedFlags());
            assertSame(cache.getTimedFlags(), cache.getTimedFlags());
            assertNotSame(cache.getTimedFlags(), cache.getUntimedFlags());
        }

        @Test
        @DisplayName("a getter on an empty cache answers an empty set")
        void gettersEmptyOnFreshCache() {
            CachedPlayerData cache = new CachedPlayerData();
            assertTrue(cache.getUntimedFlags().isEmpty());
            assertTrue(cache.getTimedFlags().isEmpty());
        }
    }
}
