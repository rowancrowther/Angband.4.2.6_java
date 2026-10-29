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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedFailure;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins what {@link PlayerTimedAssembler} does when {@code player_timed.txt} opens the same
 * {@code name:} twice. In C, {@code parse_player_timed_name} only points {@code ps->t} at the
 * existing {@code timed_effects[]} slot, so the second block's lines act on the first block's
 * effect: text lines are appended ({@code string_append}), grades continue the numbering and are
 * checked against every earlier grade, {@code fail:} conditions are pushed on the front of the
 * list, {@code flags:} are ORed in, and {@code msgt:}, {@code resist:}, {@code flag-synonym:} and
 * {@code lower-bound:} overwrite. Nothing is refused merely because a value is already set.
 *
 * <p>Class PlayerTimedAssemblerDuplicateNameTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class PlayerTimedAssemblerDuplicateNameTest {

    /**
     * One {@code grade:} line, still textual.
     *
     * @param max    the maximum as written in the data file
     * @param status the band's name
     * @return the parse record
     */
    private static PlayerTimedParseRecord.PlayerTimedGradeParseRecord grade(String max, String status) {
        return new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", max, status, null, "up");
    }

    /**
     * A record with only the given directives set; every other field is absent, as the grammar
     * leaves it.
     *
     * @param name        the {@code name:} token
     * @param description the {@code desc:} text, or empty
     * @param onEnd       the {@code on-end:} text, or empty
     * @param msgT        the {@code msgt:} token, or empty
     * @param resist      the {@code resist:} token, or empty
     * @param lowerBound  the {@code lower-bound:} text, or empty
     * @param fail        the {@code fail:} lines
     * @param flags       the {@code flags:} tokens
     * @param grades      the {@code grade:} lines
     * @return the parse record
     */
    private static PlayerTimedParseRecord record(String name, String description, String onEnd, String msgT,
                                                 String resist, String lowerBound,
                                                 List<PlayerTimedParseRecord.FailureParseRecord> fail,
                                                 List<String> flags,
                                                 PlayerTimedParseRecord.PlayerTimedGradeParseRecord... grades) {
        return new PlayerTimedParseRecord(name, description, List.of(grades), onEnd, "", "", msgT,
                fail, null, null, resist, "", "", "", "", lowerBound, flags, 1);
    }

    private static PlayerTimedParseRecord grades(String name,
                                                 PlayerTimedParseRecord.PlayerTimedGradeParseRecord... grades) {
        return record(name, "", "", "", "", "", List.of(), List.of(), grades);
    }

    private static List<PlayerTimedEffect> assemble(PlayerTimedParseRecord... records) {
        return new PlayerTimedAssembler().assemble(new ArrayList<>(List.of(records)), new ArrayList<>(), "test.txt");
    }

    private static PlayerTimedEffect only(List<PlayerTimedEffect> effects, TimedEffect name) {
        List<PlayerTimedEffect> found = effects.stream().filter(e -> e.getName() == name).toList();
        assertEquals(1, found.size(), "exactly one effect named " + name + " in " + effects.size());
        return found.get(0);
    }

    @Test
    @DisplayName("a repeated name is one effect, not two")
    void aRepeatedNameIsOneEffect() {
        List<PlayerTimedEffect> effects = assemble(grades("STUN", grade("10", "a1")), grades("STUN", grade("20", "a2")));

        assertEquals(1, effects.size());
    }

    @Test
    @DisplayName("grades continue the numbering after one head grade")
    void gradesContinueTheNumbering() {
        PlayerTimedEffect stun = only(assemble(
                grades("STUN", grade("10", "a1"), grade("20", "a2")),
                grades("STUN", grade("30", "a3"))), TimedEffect.TMD_STUN);

        List<TimedGrade> g = stun.getGrade();

        assertAll(
                () -> assertEquals(4, g.size(), "head plus three, C makes the head once"),
                () -> assertEquals(0, g.get(0).Grade()),
                () -> assertEquals(1, g.get(1).Grade()),
                () -> assertEquals(2, g.get(2).Grade()),
                () -> assertEquals(3, g.get(3).Grade()),
                () -> assertEquals(30, g.get(3).max()));
    }

    @Test
    @DisplayName("a second-block grade not above the first block's last is refused")
    void aSecondBlockGradeMustBeAboveTheFirstBlocks() {
        assertThrows(DataFileParseException.class, () -> assemble(
                grades("STUN", grade("10", "a1"), grade("20", "a2")),
                grades("STUN", grade("15", "a3"))));
    }

    @Test
    @DisplayName("text lines are appended with no separator")
    void textLinesAreAppended() {
        PlayerTimedEffect stun = only(assemble(
                record("STUN", "foo", "end1", "", "", "", List.of(), List.of(), grade("10", "a1")),
                record("STUN", "bar", "end2", "", "", "", List.of(), List.of())), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals("foobar", stun.getDescription()),
                () -> assertEquals("end1end2", stun.getOnEnd()));
    }

    @Test
    @DisplayName("an absent field in the second block keeps the first block's value")
    void anAbsentFieldKeepsTheFirstValue() {
        PlayerTimedEffect stun = only(assemble(
                record("STUN", "", "", "HIT", "ACID", "5", List.of(), List.of(), grade("10", "a1")),
                grades("STUN")), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals(MessageType.MSG_HIT, stun.getMsgT()),
                () -> assertEquals(ElementEnum.ELEM_ACID, stun.getTempResist()),
                () -> assertEquals(5, stun.getLowerBound()));
    }

    @Test
    @DisplayName("a present field in the second block overwrites the first block's value")
    void aPresentFieldOverwrites() {
        PlayerTimedEffect stun = only(assemble(
                record("STUN", "", "", "HIT", "ACID", "5", List.of(), List.of(), grade("10", "a1")),
                record("STUN", "", "", "MISS", "FIRE", "7", List.of(), List.of())), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals(MessageType.MSG_MISS, stun.getMsgT()),
                () -> assertEquals(ElementEnum.ELEM_FIRE, stun.getTempResist()),
                () -> assertEquals(7, stun.getLowerBound()));
    }

    @Test
    @DisplayName("fail lines of the later record go in front of the earlier record's")
    void failLinesAreFrontLoaded() {
        PlayerTimedEffect stun = only(assemble(
                // The file says ACID then ELEC; the grammar hands each record's lines over reversed.
                record("STUN", "", "", "", "", "", List.of(
                        new PlayerTimedParseRecord.FailureParseRecord("2", "ELEC"),
                        new PlayerTimedParseRecord.FailureParseRecord("2", "ACID")), List.of(), grade("10", "a1")),
                record("STUN", "", "", "", "", "", List.of(
                        new PlayerTimedParseRecord.FailureParseRecord("2", "FIRE")), List.of())), TimedEffect.TMD_STUN);

        List<TimedFailure> fails = stun.getFail();

        assertAll(
                () -> assertEquals(3, fails.size()),
                () -> assertEquals(ElementEnum.ELEM_FIRE, fails.get(0).getElementCode()),
                () -> assertEquals(ElementEnum.ELEM_ELEC, fails.get(1).getElementCode()),
                () -> assertEquals(ElementEnum.ELEM_ACID, fails.get(2).getElementCode()));
    }

    @Test
    @DisplayName("NONSTACKING in either block sets it")
    void nonStackingIsOredIn() {
        PlayerTimedEffect first = only(assemble(
                record("STUN", "", "", "", "", "", List.of(), List.of("NONSTACKING"), grade("10", "a1")),
                grades("STUN")), TimedEffect.TMD_STUN);
        PlayerTimedEffect second = only(assemble(
                grades("STUN", grade("10", "a1")),
                record("STUN", "", "", "", "", "", List.of(), List.of("NONSTACKING"))), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertTrue(first.isNonStacking(), "set in the first block"),
                () -> assertTrue(second.isNonStacking(), "set in the second block"));
    }

    @Test
    @DisplayName("an unknown flags token in the second block is refused")
    void anUnknownFlagInTheSecondBlockIsRefused() {
        assertThrows(DataFileParseException.class, () -> assemble(
                grades("STUN", grade("10", "a1")),
                record("STUN", "", "", "", "", "", List.of(), List.of("WIBBLE"))));
    }

    @Test
    @DisplayName("a repeat separated by another effect still joins the first")
    void aNonAdjacentRepeatStillJoins() {
        List<PlayerTimedEffect> effects = assemble(
                grades("STUN", grade("10", "a1")),
                grades("CUT", grade("5", "b1")),
                grades("STUN", grade("20", "a2")));

        PlayerTimedEffect stun = only(effects, TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals(2, effects.size()),
                () -> assertEquals(3, stun.getGrade().size()));
    }

    @Test
    @DisplayName("the ascending check also runs across a non-adjacent repeat")
    void theAscendingCheckRunsAcrossANonAdjacentRepeat() {
        assertThrows(DataFileParseException.class, () -> assemble(
                grades("STUN", grade("10", "a1"), grade("20", "a2")),
                grades("CUT", grade("5", "b1")),
                grades("STUN", grade("15", "a3"))));
    }

    @Test
    @DisplayName("an explicit lower-bound:0 in the second block overwrites")
    void anExplicitZeroLowerBoundOverwrites() {
        PlayerTimedEffect stun = only(assemble(
                record("STUN", "", "", "", "", "5", List.of(), List.of(), grade("10", "a1")),
                record("STUN", "", "", "", "", "0", List.of(), List.of())), TimedEffect.TMD_STUN);

        assertEquals(0, stun.getLowerBound());
    }

    @Test
    @DisplayName("an unknown token after NONSTACKING in one block is refused")
    void anUnknownTokenAfterNonStackingIsRefused() {
        assertThrows(DataFileParseException.class, () -> assemble(
                record("STUN", "", "", "", "", "", List.of(), List.of("NONSTACKING", "WIBBLE"), grade("10", "a1"))));
    }

    @Test
    @DisplayName("an unknown token in the second block is refused when the first set NONSTACKING")
    void anUnknownTokenInTheSecondBlockIsRefusedAfterNonStacking() {
        assertThrows(DataFileParseException.class, () -> assemble(
                record("STUN", "", "", "", "", "", List.of(), List.of("NONSTACKING"), grade("10", "a1")),
                record("STUN", "", "", "", "", "", List.of(), List.of("WIBBLE"))));
    }

    @Test
    @DisplayName("an absent flag-synonym in the second block keeps the first block's pair")
    void anAbsentFlagSynonymKeepsThePair() {
        PlayerTimedParseRecord first = new PlayerTimedParseRecord("STUN", "", List.of(grade("10", "a1")), "", "", "",
                "", List.of(), null, null, "", "", "", "FREE_ACT", "1", "", List.of(), 1);
        PlayerTimedEffect stun = only(assemble(first, grades("STUN")), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals(uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag.OF_FREE_ACT, stun.getoFlagDup()),
                () -> assertTrue(stun.isoFlagExactlySyn()));
    }

    @Test
    @DisplayName("a flag-synonym in a first block is kept")
    void aFlagSynonymInAFirstBlockIsKept() {
        PlayerTimedParseRecord only = new PlayerTimedParseRecord("STUN", "", List.of(grade("10", "a1")), "", "", "",
                "", List.of(), null, null, "", "", "", "FREE_ACT", "1", "", List.of(), 1);
        PlayerTimedEffect stun = only(assemble(only), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals(uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag.OF_FREE_ACT, stun.getoFlagDup()),
                () -> assertTrue(stun.isoFlagExactlySyn()));
    }

    @Test
    @DisplayName("FOOD grades are checked ascending inside one block")
    void foodGradesAreCheckedAscendingInsideOneBlock() throws Exception {
        java.lang.reflect.Field f = uk.co.jackoftradesltd.middle.game.globals.GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        Object saved = f.get(null);
        try {
            f.set(null, new uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData(null, null, null, null, null,
                    null, null, null, new uk.co.jackoftradesltd.middle.game.globals.data.PlayerData(20, 10, 600, 100),
                    null, List.of(), null, List.of(), null, List.of(), null, List.of()));
            assertThrows(DataFileParseException.class, () -> assemble(
                    grades("FOOD", grade("90", "a1"), grade("50", "a2"))));
        } finally {
            f.set(null, saved);
        }
    }

    @Test
    @DisplayName("FOOD grades are checked ascending across blocks")
    void foodGradesAreCheckedAscendingAcrossBlocks() throws Exception {
        java.lang.reflect.Field f = uk.co.jackoftradesltd.middle.game.globals.GameConstants.class.getDeclaredField("data");
        f.setAccessible(true);
        Object saved = f.get(null);
        try {
            f.set(null, new uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData(null, null, null, null, null,
                    null, null, null, new uk.co.jackoftradesltd.middle.game.globals.data.PlayerData(20, 10, 600, 100),
                    null, List.of(), null, List.of(), null, List.of(), null, List.of()));
            assertThrows(DataFileParseException.class, () -> assemble(
                    grades("FOOD", grade("90", "a1")),
                    grades("FOOD", grade("50", "a2"))));
            assertEquals(2, only(assemble(grades("FOOD", grade("50", "a1")),
                    grades("FOOD", grade("90", "a2"))), TimedEffect.TMD_FOOD).getGrade().size() - 1);
        } finally {
            f.set(null, saved);
        }
    }

    @Test
    @DisplayName("a flag-synonym in the second block overwrites both the flag and exact")
    void aSecondBlockFlagSynonymOverwritesBoth() {
        PlayerTimedParseRecord first = new PlayerTimedParseRecord("STUN", "", List.of(grade("10", "a1")), "", "", "",
                "", List.of(), null, null, "", "", "", "FREE_ACT", "0", "", List.of(), 1);
        PlayerTimedParseRecord second = new PlayerTimedParseRecord("STUN", "", List.of(), "", "", "",
                "", List.of(), null, null, "", "", "", "PROT_FEAR", "1", "", List.of(), 1);
        PlayerTimedEffect stun = only(assemble(first, second), TimedEffect.TMD_STUN);

        assertAll(
                () -> assertEquals(uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag.OF_PROT_FEAR, stun.getoFlagDup()),
                () -> assertTrue(stun.isoFlagExactlySyn()));
    }
}
