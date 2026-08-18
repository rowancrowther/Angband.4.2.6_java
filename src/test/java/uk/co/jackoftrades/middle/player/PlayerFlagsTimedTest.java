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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#flagsTimed}, the port of C's {@code player_flags_timed}
 * ({@code player.c:310}).
 *
 * <p>Several timed effects are, while they run, indistinguishable from carrying an object with a
 * particular flag: being under {@code TMD_OPP_CONF} is confusion protection, so it duplicates
 * {@code OF_PROT_CONF}. This method folds those duplicated flags into the flag set the equipment
 * has already contributed to, so that consumers ask about protection once rather than asking both
 * "does the gear grant it?" and "is the status running?".
 *
 * <p><b>The set is added to, never replaced.</b> Its callers pass a set that already holds the
 * flags gathered from equipment — {@code calc_bonuses} passes {@code state->flags}
 * ({@code player-calcs.c:2135}) — so a method that wiped first, or that answered in a fresh set,
 * would silently drop the gear's contribution. Several cases below pin that.
 *
 * <p><b>{@link TimedEffect#TMD_TRAPSAFE} is excluded although it names a duplicate flag.</b> Being
 * unable to tell the two sources of a flag apart is normally the point, but the trap code needs to:
 * finding {@code OF_TRAP_IMMUNE} in the player's flags is the cue to learn the trap-immunity rune
 * from the equipment ({@code trap.c:518}), which would be wrong for a player who merely drank a
 * potion. C labels the exclusion a "Hack" in the comment above {@code player_flags_timed}. It is
 * the one special case in the method and has a section to itself.
 *
 * <p><b>Fixtures are built by hand rather than parsed.</b> The pairings used here are the shipped
 * {@code flag-synonym} lines from {@code player_timed.txt} — {@code OPP_CONF}/{@code PROT_CONF},
 * {@code SINVIS}/{@code SEE_INVIS}, {@code TRAPSAFE}/{@code TRAP_IMMUNE} — because real pairings
 * read better, but nothing depends on that file: the method treats the pairing as opaque data.
 *
 * <p>{@link Player} exposes no way to set a timed counter — {@code setTimed} is still a stub that
 * writes nothing — so {@link #setTimedValue} reaches the private map reflectively, as
 * {@code PlayerTimedGradeEqTest} does.
 *
 * <p>{@link PlayerRegistry} is global static state shared with the reader suites, so the loaded
 * effects are saved and put back around every test.
 *
 * <p>Class PlayerFlagsTimedTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
class PlayerFlagsTimedTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The set handed to the method, fresh and empty unless a test seeds it.
     */
    private Flag<ObjectFlag> flags;

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
     * Builds a definition that carries nothing but its identity and its duplicated flag, which are
     * the only two fields this method reads.
     *
     * @param effect  the effect the definition is for
     * @param dupFlag the object flag it duplicates, or {@link ObjectFlag#OF_NONE} for none
     * @return the definition
     */
    private static PlayerTimedEffect definition(TimedEffect effect, ObjectFlag dupFlag) {
        return new PlayerTimedEffect(effect, "test effect", null, null, null, null,
                List.of(), List.of(), null, null, false, 0, dupFlag, false, null, null, null);
    }

    /**
     * Loads the registry with exactly the given definitions, discarding anything already there.
     *
     * @param definitions the definitions to load
     * @throws Exception if the field cannot be reached
     */
    private static void load(PlayerTimedEffect... definitions) throws Exception {
        List<PlayerTimedEffect> all = new ArrayList<>(List.of(definitions));
        registryField().set(null, all);
    }

    @BeforeEach
    void setUp() throws Exception {
        savedEffects = registryField().get(null);
        player = new Player();
        flags = new Flag<>(ObjectFlag.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        registryField().set(null, savedEffects);
    }

    /**
     * Writes a timed effect's counter directly, bypassing the stubbed {@code setTimed}.
     *
     * @param effect the effect to set
     * @param value  the counter value to give it
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private void setTimedValue(TimedEffect effect, int value) throws Exception {
        Field f = Player.class.getDeclaredField("timed");
        f.setAccessible(true);
        ((Map<TimedEffect, Integer>) f.get(player)).put(effect, value);
    }

    /**
     * @return how many flags are switched on in the set under test
     */
    private int flagCount() {
        int count = 0;
        for (ObjectFlag ignored : flags) count++;
        return count;
    }

    /**
     * A running effect contributes its duplicated flag.
     */
    @Nested
    class RunningEffectsContribute {

        @Test
        void aRunningEffectAddsItsDuplicatedFlag() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF));
        }

        @Test
        void severalRunningEffectsEachAddTheirOwnFlag() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF),
                    definition(TimedEffect.TMD_SINVIS, ObjectFlag.OF_SEE_INVIS),
                    definition(TimedEffect.TMD_BOLD, ObjectFlag.OF_PROT_FEAR));
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);
            setTimedValue(TimedEffect.TMD_SINVIS, 10);
            setTimedValue(TimedEffect.TMD_BOLD, 10);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF));
            assertTrue(flags.has(ObjectFlag.OF_SEE_INVIS));
            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
            assertEquals(3, flagCount());
        }

        @Test
        void onlyTheRunningOnesOfSeveralLoadedEffectsContribute() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF),
                    definition(TimedEffect.TMD_SINVIS, ObjectFlag.OF_SEE_INVIS));
            setTimedValue(TimedEffect.TMD_SINVIS, 10);

            player.flagsTimed(flags);

            assertFalse(flags.has(ObjectFlag.OF_PROT_CONF));
            assertTrue(flags.has(ObjectFlag.OF_SEE_INVIS));
        }

        /**
         * Two effects duplicating one flag add it once, quietly.
         *
         * <p>Real data does this: {@code TMD_HERO}, {@code TMD_SHERO} and {@code TMD_BOLD} all name
         * {@code PROT_FEAR}. Setting an already-set flag has to be a no-op rather than an error.
         */
        @Test
        void twoEffectsSharingAFlagAddItOnce() throws Exception {
            load(definition(TimedEffect.TMD_HERO, ObjectFlag.OF_PROT_FEAR),
                    definition(TimedEffect.TMD_BOLD, ObjectFlag.OF_PROT_FEAR));
            setTimedValue(TimedEffect.TMD_HERO, 10);
            setTimedValue(TimedEffect.TMD_BOLD, 10);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
            assertEquals(1, flagCount());
        }

        /**
         * A negative counter counts as running, as in C.
         *
         * <p>C's test is the truthiness of {@code p->timed[i]}, which a negative passes; the port's
         * {@code != 0} agrees. Nothing should produce a negative counter, but the two must not
         * disagree about it.
         */
        @Test
        void aNegativeCounterCountsAsRunning() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            setTimedValue(TimedEffect.TMD_OPP_CONF, -5);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF));
        }
    }

    /**
     * The set is added to, never replaced — what the callers rely on.
     */
    @Nested
    class TheCallersSetIsPreserved {

        @Test
        void flagsAlreadyInTheSetSurvive() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);
            flags.on(ObjectFlag.OF_FREE_ACT);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_FREE_ACT),
                    "the caller's set holds the equipment's flags and must not be wiped");
            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF));
        }

        @Test
        void aFlagFromGearIsNotRemovedWhenItsEffectIsDormant() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            flags.on(ObjectFlag.OF_PROT_CONF);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF),
                    "a dormant status must not clear a flag the equipment granted");
        }

        @Test
        void nothingIsAddedWhenNoEffectIsRunning() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF),
                    definition(TimedEffect.TMD_SINVIS, ObjectFlag.OF_SEE_INVIS));

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
        }

        @Test
        void aFreshPlayerContributesNothing() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
        }

        @Test
        void callingTwiceLeavesTheSameSet() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);

            player.flagsTimed(flags);
            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF));
            assertEquals(1, flagCount());
        }
    }

    /**
     * The {@code TMD_TRAPSAFE} exclusion, the method's one special case.
     */
    @Nested
    class TrapSafeIsExcluded {

        @Test
        void aRunningTrapSafeDoesNotAddTrapImmunity() throws Exception {
            load(definition(TimedEffect.TMD_TRAPSAFE, ObjectFlag.OF_TRAP_IMMUNE));
            setTimedValue(TimedEffect.TMD_TRAPSAFE, 10);

            player.flagsTimed(flags);

            assertFalse(flags.has(ObjectFlag.OF_TRAP_IMMUNE),
                    "trap immunity in the flags must mean equipment, so the rune is learnt from an item");
            assertEquals(0, flagCount());
        }

        /**
         * The exclusion is by effect, not by flag: were it written the other way round, an item's
         * {@code OF_TRAP_IMMUNE} could be filtered out too.
         */
        @Test
        void trapImmunityAlreadyInTheSetIsLeftAlone() throws Exception {
            load(definition(TimedEffect.TMD_TRAPSAFE, ObjectFlag.OF_TRAP_IMMUNE));
            setTimedValue(TimedEffect.TMD_TRAPSAFE, 10);
            flags.on(ObjectFlag.OF_TRAP_IMMUNE);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_TRAP_IMMUNE));
        }

        @Test
        void otherEffectsStillContributeWhileTrapSafeRuns() throws Exception {
            load(definition(TimedEffect.TMD_TRAPSAFE, ObjectFlag.OF_TRAP_IMMUNE),
                    definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            setTimedValue(TimedEffect.TMD_TRAPSAFE, 10);
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);

            player.flagsTimed(flags);

            assertFalse(flags.has(ObjectFlag.OF_TRAP_IMMUNE));
            assertTrue(flags.has(ObjectFlag.OF_PROT_CONF));
        }

        /**
         * Only {@code TMD_TRAPSAFE} is excluded, not whichever effect happens to name
         * {@code OF_TRAP_IMMUNE}. The pairing is data and could change.
         */
        @Test
        void anotherEffectNamingTrapImmunityIsNotExcluded() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_TRAP_IMMUNE));
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);

            player.flagsTimed(flags);

            assertTrue(flags.has(ObjectFlag.OF_TRAP_IMMUNE));
        }
    }

    /**
     * Effects with nothing to contribute, and the port's guards against absent data.
     */
    @Nested
    class NothingToContribute {

        @Test
        void anEffectDuplicatingNoFlagAddsNothing() throws Exception {
            load(definition(TimedEffect.TMD_POISONED, ObjectFlag.OF_NONE));
            setTimedValue(TimedEffect.TMD_POISONED, 10);

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
            assertFalse(flags.has(ObjectFlag.OF_NONE),
                    "OF_NONE is the absence of a flag and must never be switched on");
        }

        /**
         * An effect with no loaded definition is skipped.
         *
         * <p>No counterpart in C, which indexes a static table that is always full. Here the
         * definitions come from {@code player_timed.txt} and
         * {@link PlayerRegistry#lookupPlayerTimedEffect} answers null for an effect with none.
         */
        @Test
        void aRunningEffectWithNoDefinitionIsSkipped() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            setTimedValue(TimedEffect.TMD_POISONED, 10);

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
        }

        @Test
        void anEmptyRegistryListAddsNothingRatherThanThrowing() throws Exception {
            registryField().set(null, new ArrayList<PlayerTimedEffect>());
            setTimedValue(TimedEffect.TMD_OPP_CONF, 10);

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
        }

        /**
         * The port's extra {@link TimedEffect#TMD_NONE} sentinel, which C's enum has no equivalent
         * of, contributes nothing even if a definition somehow names a flag for it.
         */
        @Test
        void theSentinelEffectContributesNothing() throws Exception {
            load(definition(TimedEffect.TMD_NONE, ObjectFlag.OF_PROT_CONF));

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
        }

        @Test
        void anEmptyTimedMapAddsNothingRatherThanThrowing() throws Exception {
            load(definition(TimedEffect.TMD_OPP_CONF, ObjectFlag.OF_PROT_CONF));
            Field f = Player.class.getDeclaredField("timed");
            f.setAccessible(true);
            f.set(player, new HashMap<TimedEffect, Integer>());

            player.flagsTimed(flags);

            assertEquals(0, flagCount());
        }
    }
}
