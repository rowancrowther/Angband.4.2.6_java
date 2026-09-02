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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

/**
 * Tests {@link Player#playerFlags}, the port of C's {@code player_flags} ({@code player.c:290}).
 *
 * <p>The method gathers the object flags a player has innately: those from the race, those from the
 * class, and {@link ObjectFlag#OF_PROT_FEAR} for a class carrying
 * {@link PlayerFlag#PF_BRAVERY_30} once it has reached level 30. Nothing here depends on what the
 * player is wearing or what statuses are running — those are gathered by {@code calc_bonuses} and
 * {@link Player#flagsTimed} respectively.
 *
 * <p><b>The set is replaced, not added to</b>, which is the opposite of {@link Player#flagsTimed}
 * despite the near-identical signature, and is the case most worth pinning. C opens with a
 * {@code memcpy} over the whole set rather than an {@code of_union}, so anything the caller had
 * there is discarded. Both C call sites happen to pass an empty set, so only a test can hold the
 * port to it.
 *
 * <p><b>The bravery flag is a conjunction of two independent things</b> — the class flag and the
 * level — and each half is tested on its own, since dropping either half is a change that leaves
 * the common cases passing.
 *
 * <p><b>The state is a parameter, diverging from C</b>, which reads {@code p->state} directly. The
 * divergence matters at the call sites that build a hypothetical state, so a case below passes a
 * state that is not the player's own and pins that the parameter is what gets read.
 *
 * <p>Fixtures are built by hand. {@link Player} has no setters for race, class or level, and
 * {@link PlayerState} none for its player flags, so all four are reached reflectively; the
 * alternative is loading real game data, which would tie the test to {@code class.txt} for a method
 * that treats the flags as opaque.
 *
 * <p>Class PlayerFlagsTest coded on 260818, commented in full on 260818.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerFlagsTest {

    /**
     * The player under test.
     */
    private Player player;

    /**
     * The state handed to the method, holding no player flags unless a test adds one.
     */
    private PlayerState state;

    /**
     * The set handed to the method, fresh and empty unless a test seeds it.
     */
    private Flag<ObjectFlag> flags;

    /**
     * Builds a race carrying the given object flags and nothing else that this method reads.
     *
     * @param oFlags the race's innate object flags
     * @return the race
     */
    private static PlayerRace race(Flag<ObjectFlag> oFlags) {
        return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, null,
                Map.of(), Map.of(), oFlags, new Flag<>(PlayerFlag.class), null, Map.of());
    }

    /**
     * Builds a class carrying the given object flags and nothing else that this method reads.
     *
     * <p>The class's own {@code pFlags} are deliberately left empty: the method reads
     * {@link PlayerFlag#PF_BRAVERY_30} from the state, never from the class, and an empty list here
     * keeps the two apart.
     *
     * @param oFlags the class's innate object flags
     * @return the class
     */
    private static PlayerClass playerClass(Flag<ObjectFlag> oFlags) {
        return new PlayerClass("Test Class", List.of(), Map.of(), Map.of(), Map.of(), 0, 0,
                oFlags, new Flag<>(PlayerFlag.class), 0, 0, 0, List.of(), null);
    }

    /**
     * @param objectFlags the flags to switch on
     * @return a set holding exactly those flags
     */
    private static Flag<ObjectFlag> setOf(ObjectFlag... objectFlags) {
        Flag<ObjectFlag> f = new Flag<>(ObjectFlag.class);
        for (ObjectFlag flag : objectFlags) f.on(flag);
        return f;
    }

    /**
     * Writes a private field on an object, there being no setter for any of the four this test
     * needs.
     *
     * @param target    the object to write to
     * @param type      the class declaring the field
     * @param fieldName the field's name
     * @param value     the value to write
     * @throws Exception if the field cannot be reached
     */
    private static void setField(Object target, Class<?> type, String fieldName, Object value)
            throws Exception {
        Field f = type.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    /**
     * Switches a player flag on in a state's flag set.
     *
     * @param target the state to write to
     * @param flag   the player flag to switch on
     * @throws Exception if the field cannot be reached
     */
    @SuppressWarnings("unchecked")
    private static void givePFlag(PlayerState target, PlayerFlag flag) throws Exception {
        Field f = PlayerState.class.getDeclaredField("pflags");
        f.setAccessible(true);
        ((Flag<PlayerFlag>) f.get(target)).on(flag);
    }

    /**
     * Gives the player a race and a class carrying the given flags.
     *
     * @param raceFlags  the race's object flags
     * @param classFlags the class's object flags
     * @throws Exception if a field cannot be reached
     */
    private void giveRaceAndClass(Flag<ObjectFlag> raceFlags, Flag<ObjectFlag> classFlags)
            throws Exception {
        setField(player, Player.class, "race", race(raceFlags));
        setField(player, Player.class, "playerClass", playerClass(classFlags));
    }

    /**
     * @param value the character level to give the player
     * @throws Exception if the field cannot be reached
     */
    private void setLevel(int value) throws Exception {
        setField(player, Player.class, "level", value);
    }

    @BeforeEach
    void setUp() throws Exception {
        player = new Player();
        state = new PlayerState();
        flags = new Flag<>(ObjectFlag.class);
        giveRaceAndClass(new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class));
    }

    /**
     * The two innate sources, race and class.
     */
    @Nested
    class RaceAndClassContribute {

        @Test
        void aRaceFlagIsGathered() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), new Flag<>(ObjectFlag.class));

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_FEATHER));
            assertEquals(1, flags.count());
        }

        @Test
        void aClassFlagIsGathered() throws Exception {
            giveRaceAndClass(new Flag<>(ObjectFlag.class), setOf(ObjectFlag.OF_FREE_ACT));

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_FREE_ACT));
            assertEquals(1, flags.count());
        }

        @Test
        void bothSourcesAreGatheredTogether() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SEE_INVIS),
                    setOf(ObjectFlag.OF_FREE_ACT));

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_FEATHER));
            assertTrue(flags.has(ObjectFlag.OF_SEE_INVIS));
            assertTrue(flags.has(ObjectFlag.OF_FREE_ACT));
            assertEquals(3, flags.count());
        }

        /**
         * A flag on both race and class is held once, the set being a set.
         */
        @Test
        void aFlagOnBothSourcesIsHeldOnce() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), setOf(ObjectFlag.OF_FEATHER));

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_FEATHER));
            assertEquals(1, flags.count());
        }

        @Test
        void aPlayerWithNoInnateFlagsGathersNothing() {
            player.playerFlags(state, flags);

            assertTrue(flags.isEmpty());
        }

        /**
         * The sources are read, not taken over: the method must not leave the caller's set aliasing
         * the race's own, or a later addition would silently edit the race definition.
         */
        @Test
        void theRacesOwnSetIsNotModifiedByTheGather() throws Exception {
            Flag<ObjectFlag> raceFlags = setOf(ObjectFlag.OF_FEATHER);
            giveRaceAndClass(raceFlags, setOf(ObjectFlag.OF_FREE_ACT));

            player.playerFlags(state, flags);
            flags.on(ObjectFlag.OF_HOLD_LIFE);

            assertFalse(raceFlags.has(ObjectFlag.OF_FREE_ACT));
            assertFalse(raceFlags.has(ObjectFlag.OF_HOLD_LIFE));
            assertEquals(1, raceFlags.count());
        }
    }

    /**
     * The set is replaced, not added to — the opposite of {@link Player#flagsTimed}.
     */
    @Nested
    class TheCallersSetIsReplaced {

        @Test
        void aFlagAlreadyInTheSetIsDiscarded() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), new Flag<>(ObjectFlag.class));
            flags.on(ObjectFlag.OF_FREE_ACT);

            player.playerFlags(state, flags);

            assertFalse(flags.has(ObjectFlag.OF_FREE_ACT),
                    "C opens with a memcpy over the whole set, not a union");
            assertTrue(flags.has(ObjectFlag.OF_FEATHER));
            assertEquals(1, flags.count());
        }

        @Test
        void aPlayerWithNoInnateFlagsEmptiesTheSet() {
            flags.on(ObjectFlag.OF_FREE_ACT);
            flags.on(ObjectFlag.OF_FEATHER);

            player.playerFlags(state, flags);

            assertTrue(flags.isEmpty());
        }

        @Test
        void callingTwiceLeavesTheSameSet() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), setOf(ObjectFlag.OF_FREE_ACT));

            player.playerFlags(state, flags);
            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_FEATHER));
            assertTrue(flags.has(ObjectFlag.OF_FREE_ACT));
            assertEquals(2, flags.count());
        }

        /**
         * A second call after the race has changed reflects only the new race, which is the point of
         * replacing rather than accumulating.
         */
        @Test
        void aSecondCallDoesNotKeepTheFirstCallsFlags() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), new Flag<>(ObjectFlag.class));
            player.playerFlags(state, flags);

            giveRaceAndClass(setOf(ObjectFlag.OF_SEE_INVIS), new Flag<>(ObjectFlag.class));
            player.playerFlags(state, flags);

            assertFalse(flags.has(ObjectFlag.OF_FEATHER));
            assertTrue(flags.has(ObjectFlag.OF_SEE_INVIS));
            assertEquals(1, flags.count());
        }
    }

    /**
     * The level-30 fear immunity, the method's one conditional.
     */
    @Nested
    class BraveryAtLevelThirty {

        @Test
        void theFlagAndTheLevelTogetherGrantFearProtection() throws Exception {
            givePFlag(state, PlayerFlag.PF_BRAVERY_30);
            setLevel(30);

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        void theFlagWithoutTheLevelGrantsNothing() throws Exception {
            givePFlag(state, PlayerFlag.PF_BRAVERY_30);
            setLevel(29);

            player.playerFlags(state, flags);

            assertFalse(flags.has(ObjectFlag.OF_PROT_FEAR));
            assertTrue(flags.isEmpty());
        }

        @Test
        void theLevelWithoutTheFlagGrantsNothing() throws Exception {
            setLevel(50);

            player.playerFlags(state, flags);

            assertFalse(flags.has(ObjectFlag.OF_PROT_FEAR));
            assertTrue(flags.isEmpty());
        }

        /**
         * The test is {@code >= 30}, so it holds for the rest of the character's career.
         */
        @Test
        void theProtectionPersistsAboveLevelThirty() throws Exception {
            givePFlag(state, PlayerFlag.PF_BRAVERY_30);
            setLevel(50);

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        void aFreshPlayerAtLevelZeroGetsNoProtection() throws Exception {
            givePFlag(state, PlayerFlag.PF_BRAVERY_30);

            player.playerFlags(state, flags);

            assertFalse(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        /**
         * A different player flag on the state is not mistaken for bravery.
         */
        @Test
        void anotherPlayerFlagDoesNotGrantFearProtection() throws Exception {
            givePFlag(state, PlayerFlag.PF_NO_MANA);
            setLevel(50);

            player.playerFlags(state, flags);

            assertFalse(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        /**
         * Fear protection granted by the race survives a player who does not qualify for the class
         * ability, the conditional only ever switching the flag on.
         */
        @Test
        void fearProtectionFromTheRaceSurvivesAnUnqualifiedPlayer() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_PROT_FEAR), new Flag<>(ObjectFlag.class));
            setLevel(1);

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        void braveryAddsToTheInnateFlagsRatherThanReplacingThem() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), setOf(ObjectFlag.OF_FREE_ACT));
            givePFlag(state, PlayerFlag.PF_BRAVERY_30);
            setLevel(30);

            player.playerFlags(state, flags);

            assertTrue(flags.has(ObjectFlag.OF_FEATHER));
            assertTrue(flags.has(ObjectFlag.OF_FREE_ACT));
            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
            assertEquals(3, flags.count());
        }
    }

    /**
     * The state parameter, the port's divergence from C.
     */
    @Nested
    class TheStateParameterIsWhatIsRead {

        /**
         * The state handed in is read, not the player's own. C reads {@code p->state} and so would
         * answer this case the other way about.
         */
        @Test
        void aHypotheticalStateGrantsProtectionThePlayersOwnStateWouldNot() throws Exception {
            setField(player, Player.class, "state", new PlayerState());
            setLevel(30);

            PlayerState hypothetical = new PlayerState();
            givePFlag(hypothetical, PlayerFlag.PF_BRAVERY_30);

            player.playerFlags(hypothetical, flags);

            assertTrue(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        /**
         * The converse: bravery on the player's own state is ignored when a state without it is
         * passed.
         */
        @Test
        void thePlayersOwnStateIsNotConsultedWhenAnotherIsPassed() throws Exception {
            PlayerState own = new PlayerState();
            givePFlag(own, PlayerFlag.PF_BRAVERY_30);
            setField(player, Player.class, "state", own);
            setLevel(30);

            player.playerFlags(new PlayerState(), flags);

            assertFalse(flags.has(ObjectFlag.OF_PROT_FEAR));
        }

        @Test
        void theStatePassedInIsNotModified() throws Exception {
            giveRaceAndClass(setOf(ObjectFlag.OF_FEATHER), new Flag<>(ObjectFlag.class));
            givePFlag(state, PlayerFlag.PF_BRAVERY_30);
            setLevel(30);

            player.playerFlags(state, flags);

            assertFalse(state.hasOFlag(ObjectFlag.OF_FEATHER),
                    "the method fills the set it is given, never the state's own flags");
            assertFalse(state.hasOFlag(ObjectFlag.OF_PROT_FEAR));
            assertTrue(state.hasPFlag(PlayerFlag.PF_BRAVERY_30));
        }
    }
}
