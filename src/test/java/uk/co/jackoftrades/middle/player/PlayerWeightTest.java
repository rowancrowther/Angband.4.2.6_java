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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.channel.utils.Flag;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;

import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#setWeight}, {@link Player#getWeight}, {@link Player#setWeightBirth},
 * {@link PlayerRace#getBaseWeight} and {@link PlayerRace#getModWeight} — the pieces C's
 * {@code get_ahw} needs to roll a starting weight, and the getter play reads it back through.
 *
 * <p>All of them are storage, so a read-back proves nothing on its own. What is worth pinning is
 * the arithmetic they exist to serve, and every expected number below is derived from the C rather
 * than from the port. The birth roll is a single statement assigning to both fields:
 *
 * <pre>{@code
 * p->wt = p->wt_birth = Rand_normal(p->race->base_wgt, p->race->mod_wgt);   // player-birth.c:360
 * }</pre>
 *
 * <p>and the two race fields are the pair parsed from the {@code weight:base_wgt:mod_wgt} line of
 * {@code p_race.txt} ({@code init.c:2716-2717}, registered at {@code init.c:2810}). The shipped
 * values used below are read from that file: Human {@code 165:35}, Hobbit {@code 55:5} (the
 * lightest, tied with the Gnome and Kobold for the narrowest spread), Half-Orc {@code 135:15} and
 * Half-Troll {@code 240:60} (the heaviest and the widest).
 *
 * <p>The unit is pounds. The character sheet prints it in stones with {@code player->wt / 14} and
 * {@code player->wt % 14} ({@code ui-player.c:830}), so that split is asserted here as the contract
 * the stored value has to satisfy — fourteen, not the height's twelve.
 *
 * <p>Weight differs from height in being read during play: a shield bash's quality is
 * {@code p->state.skills[SKILL_TO_HIT_MELEE] / 4 + p->wt / 8 + p->upkeep->total_weight / 80 +
 * object_weight_one(shield) / 2} ({@code player-attack.c:929}). The {@code / 8} term truncates, so
 * the value is asserted to feed that arithmetic in C's units rather than in stones or in tenths.
 *
 * <p>As with the height, {@code base_wgt} is a <em>mean</em> and not a floor:
 * {@code Rand_normal(mean, stand)} returns {@code mean} unchanged when {@code stand < 1}
 * ({@code z-rand.c:296}) and otherwise {@code mean ± stand * low / RANDNOR_STD}, with {@code low}
 * in {@code 0 .. 256} and {@code RANDNOR_STD} 64 ({@code z-rand.c:314}). The reachable weights are
 * therefore {@code base_wgt - 4 * mod_wgt .. base_wgt + 4 * mod_wgt} — which for the Half-Troll is
 * {@code 0 .. 480}, a low tail that reaches zero exactly.
 *
 * <p>{@code setWeight} is checked for what it does <em>not</em> do as much as for what it does: C
 * keeps no clamp on {@code p->wt} anywhere — the stats collector writes 150 straight over a rolled
 * value ({@code main-stats.c:470}) — so the port must accept any int, including zero and negatives,
 * without adjusting it.
 *
 * <p>Class PlayerWeightTest coded on 260902, commented in full on 260902.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerWeightTest {

    /**
     * The player under test, fresh for each test since both weights are mutable.
     */
    private Player player;

    /**
     * Builds a race carrying the given weight pair and nothing else this test reads.
     *
     * @param name       the race's display name
     * @param baseWeight C's {@code base_wgt}
     * @param modWeight  C's {@code mod_wgt}
     * @return the race
     */
    private static PlayerRace race(String name, int baseWeight, int modWeight) {
        return new PlayerRace(name, 0, 10, 100, 14, 6, 69, 10, baseWeight, modWeight, 0, null,
                Map.of(), Map.of(), new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class),
                null, Map.of());
    }

    /**
     * A new player, as the constructor leaves one.
     */
    @BeforeEach
    void newPlayer() {
        player = new Player();
    }

    /**
     * Reads the player's private {@code wtBirth} field, which has no getter yet.
     *
     * @return the stored birth weight in pounds
     * @throws Exception if the field cannot be reached
     */
    private int wtBirth() throws Exception {
        Field field = Player.class.getDeclaredField("wtBirth");
        field.setAccessible(true);
        return field.getInt(player);
    }

    /**
     * {@code setWeight} and {@code getWeight}, the two halves of C's {@code p->wt}.
     */
    @Nested
    @DisplayName("Player.setWeight / getWeight")
    class SetWeight {

        /**
         * A new player weighs zero: C zeroes the whole player struct before birth, and the port's
         * default {@code int} agrees.
         */
        @Test
        @DisplayName("a new player has weight zero")
        void newPlayerIsZero() {
            assertEquals(0, player.getWeight());
        }

        /**
         * The ordinary path: a weight rolled at birth is stored as given. A hundred and sixty-five
         * pounds is the Human mean, the single most likely value {@code Rand_normal} can return for
         * that race.
         */
        @Test
        @DisplayName("stores the value it is given")
        void storesTheValue() {
            player.setWeight(165);
            assertEquals(165, player.getWeight());
        }

        /**
         * Writing twice keeps the second value. Quickstart overwrites a rolled weight with the saved
         * one ({@code player-birth.c:197}), and the save loader writes over it again
         * ({@code load.c:720}).
         */
        @Test
        @DisplayName("the last write wins")
        void lastWriteWins() {
            player.setWeight(240);
            player.setWeight(55);
            assertEquals(55, player.getWeight());
        }

        /**
         * No clamping in either direction. C applies none, and the port must not invent any: a lower
         * bound would silently correct both the zero a struct starts at and the zero the
         * Half-Troll's low tail can genuinely roll.
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() {
            player.setWeight(0);
            assertEquals(0, player.getWeight());
            player.setWeight(-5);
            assertEquals(-5, player.getWeight());
            player.setWeight(30000);
            assertEquals(30000, player.getWeight());
        }

        /**
         * The stored value is pounds, which the character sheet splits into stones and pounds with
         * {@code wt / 14} and {@code wt % 14} ({@code ui-player.c:830}). A stone is fourteen pounds,
         * not twelve as the height's foot is, so 168 must read as 12st 0lb and the pound either
         * side of it must not carry.
         */
        @Test
        @DisplayName("holds pounds, which split into stones and pounds")
        void holdsPounds() {
            player.setWeight(167);
            assertEquals(11, player.getWeight() / 14);
            assertEquals(13, player.getWeight() % 14);

            player.setWeight(168);
            assertEquals(12, player.getWeight() / 14);
            assertEquals(0, player.getWeight() % 14);

            player.setWeight(55);
            assertEquals(3, player.getWeight() / 14);
            assertEquals(13, player.getWeight() % 14);
        }

        /**
         * The weight feeds a shield bash's quality as {@code p->wt / 8}
         * ({@code player-attack.c:929}). That division truncates, so eight pounds of body weight buy
         * one point of quality and the seven under the next multiple buy nothing — the boundary is
         * walked here because a value stored in stones or in tenths of a pound would pass every
         * other test in this class and quietly wreck this one term.
         */
        @Test
        @DisplayName("feeds shield-bash quality in pounds")
        void feedsBashQuality() {
            player.setWeight(55);
            assertEquals(6, player.getWeight() / 8);

            player.setWeight(56);
            assertEquals(7, player.getWeight() / 8);

            player.setWeight(63);
            assertEquals(7, player.getWeight() / 8);

            player.setWeight(240);
            assertEquals(30, player.getWeight() / 8);
        }
    }

    /**
     * {@code setWeightBirth}, the write half of C's {@code p->wt_birth}.
     */
    @Nested
    @DisplayName("Player.setWeightBirth")
    class SetWeightBirth {

        /**
         * A new player has a birth weight of zero, matching the zeroed struct C starts from.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("a new player has a birth weight of zero")
        void newPlayerIsZero() throws Exception {
            assertEquals(0, wtBirth());
        }

        /**
         * The two weights are separate storage. Writing the birth copy must leave the working weight
         * alone, or the quickstart record and the live value would be one field wearing two names —
         * which is exactly what the port did before this was checked.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not touch the working weight")
        void doesNotTouchWorkingWeight() throws Exception {
            player.setWeight(165);
            player.setWeightBirth(240);
            assertEquals(165, player.getWeight());
            assertEquals(240, wtBirth());
            assertNotEquals(player.getWeight(), wtBirth());
        }

        /**
         * The reverse direction: writing the working weight leaves the birth copy where it was. This
         * is the case quickstart depends on — it reads {@code player->wt_birth} out to the saved
         * character ({@code player-birth.c:154}) after a whole game has been played.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("survives a later write to the working weight")
        void survivesLaterWorkingWrite() throws Exception {
            player.setWeightBirth(165);
            player.setWeight(55);
            assertEquals(165, wtBirth());
            assertEquals(55, player.getWeight());
        }

        /**
         * The birth idiom itself: C's chained assignment puts one roll into both fields
         * ({@code player-birth.c:360}), which the port spells as two calls sharing a single rolled
         * value. Both must end up holding it.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("the birth assignment leaves both fields equal")
        void birthAssignmentSetsBoth() throws Exception {
            int rolled = 143;
            player.setWeight(rolled);
            player.setWeightBirth(player.getWeight());
            assertEquals(rolled, player.getWeight());
            assertEquals(rolled, wtBirth());
        }

        /**
         * Quickstart's round trip, both directions: the birth weight is read out to a saved
         * character and then written back over both fields —
         * {@code player->wt = player->wt_birth = saved->wt} ({@code player-birth.c:197}) — after the
         * working weight has moved on.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("survives the quickstart round trip")
        void survivesQuickstartRoundTrip() throws Exception {
            player.setWeight(165);
            player.setWeightBirth(player.getWeight());

            int saved = wtBirth();
            player.setWeight(240);

            player.setWeight(saved);
            player.setWeightBirth(saved);
            assertEquals(165, player.getWeight());
            assertEquals(165, wtBirth());
        }

        /**
         * No clamping here either — the same reasoning as the working weight.
         *
         * @throws Exception if the field cannot be reached
         */
        @Test
        @DisplayName("does not clamp")
        void doesNotClamp() throws Exception {
            player.setWeightBirth(0);
            assertEquals(0, wtBirth());
            player.setWeightBirth(-5);
            assertEquals(-5, wtBirth());
            player.setWeightBirth(30000);
            assertEquals(30000, wtBirth());
        }
    }

    /**
     * {@code getBaseWeight} and {@code getModWeight}, the pair the birth roll reads.
     */
    @Nested
    @DisplayName("PlayerRace weight fields")
    class RaceWeightFields {

        /**
         * The shipped values come back as the constructor was given them, and the two do not cross —
         * a swapped pair would compile and read plausibly.
         */
        @Test
        @DisplayName("base and mod are reported separately")
        void baseAndModAreSeparate() {
            PlayerRace human = race("Human", 165, 35);
            assertEquals(165, human.getBaseWeight());
            assertEquals(35, human.getModWeight());

            PlayerRace halfTroll = race("Half-Troll", 240, 60);
            assertEquals(240, halfTroll.getBaseWeight());
            assertEquals(60, halfTroll.getModWeight());
        }

        /**
         * The weight pair is neither the height pair nor the age pair. A race carries all three, and
         * a copy-paste getter reading the wrong one would go unnoticed for a race whose numbers
         * happened to be close.
         */
        @Test
        @DisplayName("weight does not read the height or age fields")
        void weightIsNotHeightOrAge() {
            PlayerRace hobbit = race("Hobbit", 55, 5);
            assertEquals(55, hobbit.getBaseWeight());
            assertEquals(5, hobbit.getModWeight());
            assertEquals(69, hobbit.getBaseHeight());
            assertEquals(10, hobbit.getModHeight());
            assertEquals(14, hobbit.getBaseAge());
            assertEquals(6, hobbit.getModAge());
        }

        /**
         * The base is the mean of {@code Rand_normal}, so unlike the age base it is itself a
         * reachable weight — the offset is zero whenever the binary search lands at index zero
         * ({@code z-rand.c:314}) — and roughly half of a race's characters weigh less.
         */
        @Test
        @DisplayName("the base is a reachable weight, not a floor")
        void baseIsReachable() {
            PlayerRace hobbit = race("Hobbit", 55, 5);
            player.setWeight(hobbit.getBaseWeight());
            assertEquals(55, player.getWeight());

            player.setWeight(hobbit.getBaseWeight() - 1);
            assertTrue(player.getWeight() < hobbit.getBaseWeight());
        }

        /**
         * The spread is a standard deviation scaled by {@code RANDNOR_STD} of 64, and the search
         * index tops out at 256, so the offset reaches four times {@code mod_wgt} either side of the
         * base ({@code z-rand.c:314}). Walking the extremes gives the Half-Troll {@code 0 .. 480} —
         * the one shipped race whose low tail reaches zero exactly, which is why a lower clamp of
         * one would be wrong as well as unfaithful — and the Hobbit {@code 35 .. 75}.
         */
        @Test
        @DisplayName("the spread reaches four deviations either side")
        void spreadReachesFourDeviations() {
            PlayerRace halfTroll = race("Half-Troll", 240, 60);
            int maxOffset = halfTroll.getModWeight() * 256 / 64;
            assertEquals(240, maxOffset);
            player.setWeight(halfTroll.getBaseWeight() - maxOffset);
            assertEquals(0, player.getWeight());
            player.setWeight(halfTroll.getBaseWeight() + maxOffset);
            assertEquals(480, player.getWeight());

            PlayerRace hobbit = race("Hobbit", 55, 5);
            int hobbitOffset = hobbit.getModWeight() * 256 / 64;
            assertEquals(20, hobbitOffset);
            player.setWeight(hobbit.getBaseWeight() - hobbitOffset);
            assertEquals(35, player.getWeight());
            player.setWeight(hobbit.getBaseWeight() + hobbitOffset);
            assertEquals(75, player.getWeight());
        }

        /**
         * Every offset the table can produce for the Hobbit, walked one search index at a time. The
         * integer division in {@code stand * low / RANDNOR_STD} truncates, so a spread of 5 moves in
         * whole pounds only every 13 index positions or so — the point being that the port's fields
         * feed that arithmetic unscaled, in the same units C uses.
         */
        @Test
        @DisplayName("feeds the offset arithmetic in C's units")
        void feedsOffsetArithmetic() {
            PlayerRace hobbit = race("Hobbit", 55, 5);

            for (int low = 0; low <= 256; low += 16) {
                int offset = hobbit.getModWeight() * low / 64;
                assertEquals(low * 5 / 64, offset);

                player.setWeight(hobbit.getBaseWeight() + offset);
                assertEquals(55 + low * 5 / 64, player.getWeight());
            }
        }

        /**
         * A spread below one short-circuits the roll entirely — {@code Rand_normal} returns the mean
         * untouched ({@code z-rand.c:296}). No shipped race does this, but the getter has to be able
         * to report it, so a zero spread is checked to come back as zero rather than being defaulted
         * to something usable.
         */
        @Test
        @DisplayName("reports a spread below one unchanged")
        void reportsZeroSpread() {
            PlayerRace fixed = race("Fixed", 150, 0);
            assertEquals(0, fixed.getModWeight());
            assertEquals(150, fixed.getBaseWeight());

            player.setWeight(fixed.getModWeight() < 1 ? fixed.getBaseWeight() : -1);
            assertEquals(150, player.getWeight());
        }
    }
}
