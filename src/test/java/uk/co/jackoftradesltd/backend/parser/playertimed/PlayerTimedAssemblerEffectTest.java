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

package uk.co.jackoftradesltd.backend.parser.playertimed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins how {@link PlayerTimedAssembler} turns the {@code on-begin-effect:} and
 * {@code on-end-effect:} blocks of a record into effects, with the expected behaviour taken from
 * C's {@code parse_player_timed_on_begin_effect} and {@code parse_player_timed_on_end_effect} in
 * {@code player-timed.c}: each such line appends to the end of the timed effect's chain, so a
 * record that repeats an earlier name adds to the chain rather than replacing it, and a failure to
 * resolve the effect name ends the load.
 *
 * <p>The effects are counted rather than inspected, because {@code Effect} offers no accessor for
 * its resolved type; the shipped-data test covers which real effects land on which status.
 *
 * <p>Class PlayerTimedAssemblerEffectTest coded on 261107, commented in full on 261107.
 *
 * @author Rowan Crowther
 */
class PlayerTimedAssemblerEffectTest {

    private static final String FILE = "player_timed.txt";
    private static final int LINE = 7;

    private static final PlayerTimedParseRecord.PlayerTimedGradeParseRecord GRADE =
            new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", "10", "Status", null, "Up");

    /**
     * An effect block as the grammar leaves it: the name and sub-type given, every other field
     * the empty string.
     *
     * @param name    the effect name
     * @param subType the sub-type token, or empty
     * @return the parse record
     */
    private static EffectParseRecord effect(String name, String subType) {
        return new EffectParseRecord(name, subType, "", "", "", "", "", "", "", "", "", LINE);
    }

    /**
     * A {@code name:} record with the given effect blocks and one grade.
     *
     * @param name  the {@code name:} token
     * @param begin the {@code on-begin-effect:} block, or {@code null}
     * @param end   the {@code on-end-effect:} block, or {@code null}
     * @return the parse record
     */
    private static PlayerTimedParseRecord record(String name, EffectParseRecord begin, EffectParseRecord end) {
        return new PlayerTimedParseRecord(name, "", List.of(GRADE), "", "", "", "", List.of(), begin, end,
                "", "", "", "", "", "", List.of(), LINE);
    }

    /**
     * A repeat of an earlier {@code name:} record that gives no grades, so the strictly ascending
     * grade rule does not apply to it.
     *
     * @param name  the {@code name:} token
     * @param begin the {@code on-begin-effect:} block, or {@code null}
     * @param end   the {@code on-end-effect:} block, or {@code null}
     * @return the parse record
     */
    private static PlayerTimedParseRecord repeat(String name, EffectParseRecord begin, EffectParseRecord end) {
        return new PlayerTimedParseRecord(name, "", List.of(), "", "", "", "", List.of(), begin, end,
                "", "", "", "", "", "", List.of(), LINE);
    }

    private static List<PlayerTimedEffect> assemble(PlayerTimedParseRecord... records) {
        return new PlayerTimedAssembler().assemble(List.of(records), FILE);
    }

    @Test
    @DisplayName("a record with no effect blocks leaves both chains empty")
    void noBlocksLeaveBothChainsEmpty() {
        PlayerTimedEffect fast = assemble(record("FAST", null, null)).get(0);
        assertAll(
                () -> assertTrue(fast.getOnBeginEffect().isEmpty()),
                () -> assertTrue(fast.getOnEndEffect().isEmpty()));
    }

    @Test
    @DisplayName("a begin block lands on the begin chain only, and an end block on the end chain only")
    void eachBlockLandsOnItsOwnChain() {
        PlayerTimedEffect scramble = assemble(record("SCRAMBLE",
                effect("SCRAMBLE_STATS", ""), effect("UNSCRAMBLE_STATS", ""))).get(0);
        assertAll(
                () -> assertEquals(1, scramble.getOnBeginEffect().size()),
                () -> assertEquals(1, scramble.getOnEndEffect().size()));

        PlayerTimedEffect beginOnly = assemble(record("SCRAMBLE", effect("SCRAMBLE_STATS", ""), null)).get(0);
        assertAll(
                () -> assertEquals(1, beginOnly.getOnBeginEffect().size()),
                () -> assertTrue(beginOnly.getOnEndEffect().isEmpty()));

        PlayerTimedEffect endOnly = assemble(record("SPRINT", null, effect("TIMED_INC_NO_RES", "SLOW"))).get(0);
        assertAll(
                () -> assertTrue(endOnly.getOnBeginEffect().isEmpty()),
                () -> assertEquals(1, endOnly.getOnEndEffect().size()));
    }

    @Test
    @DisplayName("a repeated name adds to the chains C already holds instead of replacing them")
    void aRepeatedNameAppendsToTheChains() {
        List<PlayerTimedEffect> effects = assemble(
                record("SCRAMBLE", effect("SCRAMBLE_STATS", ""), effect("UNSCRAMBLE_STATS", "")),
                repeat("SCRAMBLE", effect("SCRAMBLE_STATS", ""), null));
        assertEquals(1, effects.size());
        assertAll(
                () -> assertEquals(2, effects.get(0).getOnBeginEffect().size()),
                () -> assertEquals(1, effects.get(0).getOnEndEffect().size()));
    }

    @Test
    @DisplayName("a repeat with no block keeps the chains from the first block")
    void aRepeatWithNoBlockKeepsTheChains() {
        List<PlayerTimedEffect> effects = assemble(
                record("SCRAMBLE", effect("SCRAMBLE_STATS", ""), effect("UNSCRAMBLE_STATS", "")),
                repeat("SCRAMBLE", null, null));
        assertAll(
                () -> assertEquals(1, effects.get(0).getOnBeginEffect().size()),
                () -> assertEquals(1, effects.get(0).getOnEndEffect().size()));
    }

    @Test
    @DisplayName("an unknown effect name in either block stops the load")
    void anUnknownEffectNameStopsTheLoad() {
        DataFileParseException begin = assertThrows(DataFileParseException.class,
                () -> assemble(record("SCRAMBLE", effect("NOT_AN_EFFECT", ""), null)));
        assertTrue(begin.getMessage().contains(FILE), begin.getMessage());

        DataFileParseException end = assertThrows(DataFileParseException.class,
                () -> assemble(record("SCRAMBLE", null, effect("NOT_AN_EFFECT", ""))));
        assertTrue(end.getMessage().contains(FILE), end.getMessage());
    }
}
