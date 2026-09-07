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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Birther}, the port of C's {@code struct birther} ({@code player-birth.c:93-109}).
 *
 * <p>Every accessor here is a bare field read or write, so there is no arithmetic to derive an
 * expected value from — what these tests pin down instead is the handful of places a plain
 * struct-to-object port can still go wrong: a reference field silently deep-copied instead of
 * shared, a fixed-size C member silently gaining or losing the cap Java doesn't need, and a
 * constructor leaving a field the C zero-initialisation would have given a real value.
 *
 * <p>The race and class fixtures are deliberately minimal — built with
 * {@link SeededPlayerRegistry#plainRace} and a matching bare {@link PlayerClass} — since nothing
 * under test ever calls a method on either; only their identity, matching C's pointer-copy
 * semantics, is at stake.
 *
 * <p>Class BirtherTest coded on 260906, commented in full on 260906.
 *
 * @author Rowan Crowther
 */
class BirtherTest {

    /**
     * The snapshot under test, fresh for each test.
     */
    private Birther birther;

    /**
     * Builds a class contributing nothing, the {@link PlayerClass} counterpart of
     * {@link SeededPlayerRegistry#plainRace}.
     *
     * @return the class
     */
    private static PlayerClass plainClass() {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        return new PlayerClass("Test Class", null, stats, new HashMap<>(), new HashMap<>(),
                0, 0, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), 0, 0, 0, null, null);
    }

    @BeforeEach
    void build() {
        birther = new Birther();
    }

    /**
     * The one thing the constructor does beyond leaving Java's own defaults in place - the port of
     * declaring a {@code birther} with an implicit {@code int16_t stat[STAT_MAX]} member.
     */
    @Nested
    @DisplayName("a fresh snapshot")
    class FreshSnapshot {

        /**
         * C's array member exists the moment the struct is declared, with every slot readable
         * (zero, for the {@code static} instances the port has not reached yet). The port's
         * {@link Map} has no such implicit existence, so the constructor has to create it - a
         * caller reading {@link Birther#getStat()} before any {@link Birther#setStat} must get an
         * empty map, not a {@code NullPointerException} on the first {@code put}.
         */
        @Test
        @DisplayName("has a non-null, empty stat map")
        void hasEmptyStatMap() {
            assertNotNull(birther.getStat());
            assertTrue(birther.getStat().isEmpty());
        }

        /**
         * Every other field starts at Java's own default rather than anything read off C, since no
         * caller of this constructor relies on the difference yet (see the class Javadoc).
         */
        @Test
        @DisplayName("otherwise starts at Java's defaults")
        void otherwiseStartsAtDefaults() {
            assertEquals(null, birther.getRace());
            assertEquals(null, birther.getPlayerClass());
            assertEquals(0, birther.getAge());
            assertEquals(0, birther.getWeight());
            assertEquals(0, birther.getHeight());
            assertEquals(0, birther.getSc());
            assertEquals(0L, birther.getAu());
            assertNull(birther.getHistory());
            assertNull(birther.getName());
        }
    }

    /**
     * {@code birther.race} and {@code birther.class} - C's {@code const struct player_race *}/
     * {@code const struct player_class *}, pointers into the shared registry rather than owned
     * copies.
     */
    @Nested
    @DisplayName("race and class")
    class RaceAndClass {

        /**
         * {@code tosave->race = player->race} ({@code player-birth.c:152}) copies the pointer, not
         * the pointed-to struct, so the accessor has to hand back the identical race object it was
         * given rather than an equal-but-different one.
         */
        @Test
        @DisplayName("race is stored by reference, not copied")
        void raceStoredByReference() {
            PlayerRace race = SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody());
            birther.setRace(race);
            assertSame(race, birther.getRace());
        }

        /**
         * {@code tosave->class = player->class} ({@code player-birth.c:153}), the same pointer-copy
         * as the race field.
         */
        @Test
        @DisplayName("class is stored by reference, not copied")
        void classStoredByReference() {
            PlayerClass playerClass = plainClass();
            birther.setPlayerClass(playerClass);
            assertSame(playerClass, birther.getPlayerClass());
        }
    }

    /**
     * {@code birther.age}, {@code birther.wt}, {@code birther.ht}, {@code birther.sc} and
     * {@code birther.au} - the scalar fields, {@code int16_t}/{@code int32_t} in C and plain
     * {@code int}/{@code long} here, with nothing computed on the way in or out.
     */
    @Nested
    @DisplayName("scalar fields")
    class ScalarFields {

        /**
         * {@code tosave->age = player->age} ({@code player-birth.c:154}) - stored as given.
         */
        @Test
        @DisplayName("age is stored as given")
        void ageStoredAsGiven() {
            birther.setAge(42);
            assertEquals(42, birther.getAge());
        }

        /**
         * {@code tosave->wt = player->wt_birth} ({@code player-birth.c:155}) - the field itself
         * takes whatever it is given; which of the player's two weight fields feeds it is
         * {@code PlayerBirth.saveRollerData}'s concern, not this class's.
         */
        @Test
        @DisplayName("weight is stored as given")
        void weightStoredAsGiven() {
            birther.setWeight(165);
            assertEquals(165, birther.getWeight());
        }

        /**
         * {@code tosave->ht = player->ht_birth} ({@code player-birth.c:156}).
         */
        @Test
        @DisplayName("height is stored as given")
        void heightStoredAsGiven() {
            birther.setHeight(69);
            assertEquals(69, birther.getHeight());
        }

        /**
         * {@code birther.sc} is declared but never read or written anywhere else in
         * {@code player-birth.c} — the field is vestigial in C, not merely unported — so the only
         * thing to pin down here is that the port's accessor is at least as capable as C's dead
         * field: it stores whatever it is given without complaint.
         */
        @Test
        @DisplayName("sc is stored as given, though nothing in C ever writes it")
        void scStoredAsGiven() {
            birther.setSc(7);
            assertEquals(7, birther.getSc());
        }

        /**
         * {@code tosave->au = player->au_birth} ({@code player-birth.c:157}) - C's {@code int32_t}
         * widened to {@code long}; a value at the top of C's range must still round-trip exactly.
         */
        @Test
        @DisplayName("au is stored as given, including values beyond int32_t's own field width")
        void auStoredAsGiven() {
            birther.setAu(600L);
            assertEquals(600L, birther.getAu());
        }
    }

    /**
     * {@code birther.stat[STAT_MAX]} - a fixed {@code int16_t} array in C, a {@link Map} here.
     */
    @Nested
    @DisplayName("the stat map")
    class StatMap {

        /**
         * {@code for (i = 0; i < STAT_MAX; i++) tosave->stat[i] = player->stat_birth[i]}
         * ({@code player-birth.c:159-160}) - each of the five real stats round-trips independently.
         */
        @Test
        @DisplayName("holds one independent entry per real stat")
        void holdsOneEntryPerStat() {
            birther.setStat(Stats.STAT_STR, 18);
            birther.setStat(Stats.STAT_CON, 12);

            assertEquals(18, birther.getStat().get(Stats.STAT_STR));
            assertEquals(12, birther.getStat().get(Stats.STAT_CON));
            assertEquals(2, birther.getStat().size());
        }

        /**
         * C's array is sized {@code STAT_MAX} entries and could never be indexed by a sentinel;
         * the port's map has no such structural limit, and nothing in this class adds one back -
         * the guard against writing {@code STAT_NONE}/{@code STAT_MAX} lives in
         * {@code PlayerBirth.saveRollerData}'s loop, not here. This is the divergence the class
         * Javadoc names, made concrete.
         */
        @Test
        @DisplayName("does not itself guard against the STAT_NONE/STAT_MAX sentinels")
        void doesNotGuardSentinels() {
            birther.setStat(Stats.STAT_NONE, 99);
            assertEquals(99, birther.getStat().get(Stats.STAT_NONE));
        }
    }

    /**
     * {@code birther.history} - C's {@code char *}, a plain {@link String} here.
     */
    @Nested
    @DisplayName("history")
    class History {

        /**
         * {@code tosave->history = player->history} ({@code player-birth.c:166}) hands the pointer
         * across; the port's setter does the same with the reference.
         */
        @Test
        @DisplayName("is stored verbatim")
        void storedVerbatim() {
            birther.setHistoryBirth("You are one of several children of a Serf.  ");
            assertEquals("You are one of several children of a Serf.  ", birther.getHistory());
        }

        /**
         * {@code if (tosave->history) string_free(tosave->history);} then
         * {@code tosave->history = player->history;} ({@code player-birth.c:163-166}) - C frees the
         * old string before the overwrite; the port's assignment simply drops the old reference,
         * with the same net result of the new value alone remaining.
         */
        @Test
        @DisplayName("a second write replaces the first")
        void secondWriteReplaces() {
            birther.setHistoryBirth("of a Serf.  ");
            birther.setHistoryBirth("of a Royal Blood Line.  ");
            assertEquals("of a Royal Blood Line.  ", birther.getHistory());
        }
    }

    /**
     * {@code birther.name[PLAYER_NAME_LEN]} - a fixed 32-byte C buffer
     * ({@code option.h:23, player-birth.c:108}), a plain {@link String} here.
     */
    @Nested
    @DisplayName("name")
    class Name {

        /**
         * {@code my_strcpy(tosave->name, player->full_name, sizeof(tosave->name))}
         * ({@code player-birth.c:168}) truncates anything past {@code PLAYER_NAME_LEN - 1}
         * characters; the port's {@link String} has no such cap, and a name at exactly the C limit
         * must still come back whole rather than clipped.
         */
        @Test
        @DisplayName("is stored verbatim, including at C's buffer limit")
        void storedVerbatimAtBufferLimit() {
            String name = "A".repeat(32);
            birther.setName(name);
            assertEquals(name, birther.getName());
        }

        /**
         * The same point made past the limit: C's {@code my_strcpy} would silently drop everything
         * from the 32nd character on, and the port must not.
         */
        @Test
        @DisplayName("is stored verbatim, beyond C's buffer limit")
        void storedVerbatimBeyondBufferLimit() {
            String name = "A".repeat(40);
            birther.setName(name);
            assertEquals(name, birther.getName());
        }
    }

    /**
     * {@link Birther#copy()} - the port of what C's raw struct assignment
     * {@code *prev_player = temp;} ({@code player-birth.c:223}) does to a whole {@code birther}. C
     * has no named function for this; the comparison is against what a plain struct assignment's
     * memberwise copy does to each field, not a single C function.
     */
    @Nested
    @DisplayName("copy")
    class Copy {

        /**
         * {@code tosave->race = player->race} is a pointer copy elsewhere in this class
         * ({@link RaceAndClass}), and the raw struct assignment {@code copy()} stands in for is no
         * different - the result shares the very same race and class records as the source rather
         * than owning independent ones.
         */
        @Test
        @DisplayName("shares race and class by reference, like the source")
        void sharesRaceAndClassByReference() {
            PlayerRace race = SeededPlayerRegistry.plainRace(SeededPlayerRegistry.humanoidBody());
            PlayerClass playerClass = plainClass();
            birther.setRace(race);
            birther.setPlayerClass(playerClass);

            Birther result = birther.copy();

            assertSame(race, result.getRace());
            assertSame(playerClass, result.getPlayerClass());
        }

        /**
         * {@code birther.stat[STAT_MAX]} is an embedded array, so the struct assignment copies its
         * contents into an independent array rather than sharing it, unlike race and class. A write
         * to the result's stats after copying must not reach the source.
         */
        @Test
        @DisplayName("copies the stat map independently, not by reference")
        void copiesStatMapIndependently() {
            birther.setStat(Stats.STAT_STR, 18);

            Birther result = birther.copy();
            result.setStat(Stats.STAT_STR, 3);

            assertEquals(18, birther.getStat().get(Stats.STAT_STR));
            assertEquals(3, result.getStat().get(Stats.STAT_STR));
        }

        /**
         * The reverse direction of the same point: a write to the source after copying must not
         * reach the result either.
         */
        @Test
        @DisplayName("a write to the source after copying does not reach the result")
        void writeToSourceAfterCopyDoesNotReachResult() {
            birther.setStat(Stats.STAT_CON, 12);

            Birther result = birther.copy();
            birther.setStat(Stats.STAT_CON, 99);

            assertEquals(99, birther.getStat().get(Stats.STAT_CON));
            assertEquals(12, result.getStat().get(Stats.STAT_CON));
        }

        /**
         * The scalar fields, copied by value either side of the port, same as every other plain
         * struct member.
         */
        @Test
        @DisplayName("copies the scalar fields by value")
        void copiesScalarFieldsByValue() {
            birther.setAge(42);
            birther.setWeight(150);
            birther.setHeight(70);
            birther.setSc(7);
            birther.setAu(600L);

            Birther result = birther.copy();

            assertEquals(42, result.getAge());
            assertEquals(150, result.getWeight());
            assertEquals(70, result.getHeight());
            assertEquals(7, result.getSc());
            assertEquals(600L, result.getAu());
        }

        /**
         * {@code history} and {@code name} carry across too - by reference, but since {@link String}
         * is immutable that is indistinguishable from C's byte-copied buffer.
         */
        @Test
        @DisplayName("copies history and name")
        void copiesHistoryAndName() {
            birther.setHistoryBirth("A rolled background.");
            birther.setName("Bilbo");

            Birther result = birther.copy();

            assertEquals("A rolled background.", result.getHistory());
            assertEquals("Bilbo", result.getName());
        }

        /**
         * The result is always a new object, matching C writing into a distinct destination struct
         * rather than aliasing the source itself.
         */
        @Test
        @DisplayName("returns a different instance from the source")
        void returnsADifferentInstance() {
            Birther result = birther.copy();
            assertNotSame(birther, result);
        }
    }
}
