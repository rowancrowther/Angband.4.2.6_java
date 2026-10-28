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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.pain.PainAssembler;
import uk.co.jackoftradesltd.backend.parser.pain.PainParseRecord;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link PainAssembler}. The expected values come from the C original's
 * {@code parse_pain_type()} and {@code parse_pain_message()} in {@code mon-init.c}, from
 * {@code parser_parse()} in {@code parser.c} (the {@code uint} reading: {@code strtoul} in base 0,
 * and a first character of {@code -} is {@code PARSE_ERROR_NOT_NUMBER}), and from the header of
 * the shipped {@code lib/gamedata/pain.txt}.
 *
 * <p>Every test is driven from a hand-built {@link PainParseRecord}, whose message list is
 * {@code [typeNum, msg1..msgN]} as the grammar hands it over, so one thing can be varied at a time.
 * C quits on the first bad line, so the refusals expect a {@link DataFileParseException}, never an
 * entry in {@code errors} and never a record dropped. The serials are the text of an
 * {@code INTEGER} token, which is digits with an optional {@code -}; hexadecimal cannot reach the
 * assembler.
 *
 * <p>Number-reading cases that C wraps at 32 bits (such as {@code 4294967296}) are not tested: the
 * port clamps to an {@code int} on purpose (decided 261027).
 *
 * <p>Test class PainAssemblerTest coded on 261028, commented in full on 261028.
 *
 * @author Rowan Crowther
 */
class PainAssemblerTest {

    private static final String FILE = "pain.txt";

    /**
     * A record as the grammar builds it: the serial text first, then {@code count} messages named
     * {@code m1}, {@code m2} and so on.
     */
    private static PainParseRecord record(String serial, int count, int line) {
        List<String> values = new ArrayList<>();
        values.add(serial);
        for (int i = 1; i <= count; i++) {
            values.add("m" + i);
        }
        return new PainParseRecord(values, line);
    }

    private static List<MonsterPain> assemble(PainParseRecord... records) {
        return new PainAssembler().assemble(List.of(records), new ArrayList<>(), FILE);
    }

    private static int indexOf(String serial) {
        return assemble(record(serial, 7, 1)).get(0).getPainIndex();
    }

    private static DataFileParseException refused(PainParseRecord... records) {
        return assertThrows(DataFileParseException.class, () -> assemble(records));
    }

    // ---- The ordinary path ------------------------------------------------

    /**
     * {@code parse_pain_message} fills {@code messages[0..6]} in order, so the first line is the
     * mildest and the serial never lands in the list.
     */
    @Test
    void sevenMessagesAreKeptInOrderAndTheSerialIsPeeledOff() {
        MonsterPain pain = assemble(record("4", 7, 1)).get(0);

        assertEquals(4, pain.getPainIndex());
        for (int level = 0; level < 7; level++) {
            assertEquals("m" + (level + 1), pain.getMessage(level));
        }
    }

    /**
     * {@code finish_parse_pain} copies the records by serial, but the Java list is in file order.
     * Two records come back in the order given and each keeps its own messages.
     */
    @Test
    void severalRecordsComeBackInTheOrderGiven() {
        List<MonsterPain> pains = assemble(record("2", 7, 1), record("1", 7, 10));

        assertEquals(2, pains.size());
        assertEquals(2, pains.get(0).getPainIndex());
        assertEquals(1, pains.get(1).getPainIndex());
    }

    /** No records is an empty list and no error. */
    @Test
    void noRecordsGivesAnEmptyList() {
        assertTrue(assemble().isEmpty());
    }

    /**
     * The assembler throws rather than collects, so a clean load leaves the soft-error sink as it
     * found it.
     */
    @Test
    void theErrorSinkIsLeftEmpty() {
        List<String> errors = new ArrayList<>();

        new PainAssembler().assemble(List.of(record("1", 7, 1)), errors, FILE);

        assertTrue(errors.isEmpty(), errors::toString);
    }

    /**
     * {@code string_make(parser_getstr(...))} copies the text exactly, colons, brackets and edge
     * spaces included.
     */
    @Test
    void messageTextIsPassedOnAsWritten() {
        List<String> values = List.of("1", "cr[ies|y] out in pain.", " lead", "a:b", "m4", "m5", "m6", "trail ");

        MonsterPain pain = assemble(new PainParseRecord(values, 1)).get(0);

        assertEquals("cr[ies|y] out in pain.", pain.getMessage(0));
        assertEquals(" lead", pain.getMessage(1));
        assertEquals("a:b", pain.getMessage(2));
        assertEquals("trail ", pain.getMessage(6));
    }

    // ---- The serial: parser_getuint, strtoul in base 0 ---------------------

    /** Plain decimal serials, up to the twelve the shipped file uses. */
    @Test
    void decimalSerialsAreRead() {
        assertEquals(1, indexOf("1"));
        assertEquals(12, indexOf("12"));
        assertEquals(1000, indexOf("1000"));
    }

    /**
     * Zero is a legal {@code uint}. C would file it in the blank slot 0 of {@code pain_messages};
     * the assembler treats it as any other serial.
     */
    @Test
    void zeroIsAcceptedAsASerial() {
        assertEquals(0, indexOf("0"));
        assertEquals(0, indexOf("00"));
    }

    /** {@code strtoul} with base 0 reads a leading {@code 0} as octal, so {@code 010} is 8. */
    @Test
    void aLeadingZeroSerialIsReadAsOctal() {
        assertEquals(8, indexOf("010"));
        assertEquals(7, indexOf("007"));
        assertEquals(15, indexOf("017"));
    }

    /**
     * An octal reading stops at the first 8 or 9, and {@code parser_parse} does not look at what
     * is left, so {@code 08} and {@code 0089} are 0 and {@code 078} is 7.
     */
    @Test
    void anOctalReadingStopsAtAnEightOrNine() {
        assertEquals(0, indexOf("08"));
        assertEquals(0, indexOf("0089"));
        assertEquals(7, indexOf("078"));
    }

    // ---- The serial: a leading minus --------------------------------------

    /**
     * C tests {@code *tok == '-'} on a {@code uint} and returns {@code PARSE_ERROR_NOT_NUMBER}, so
     * a negative serial stops the load and names the line, the file and the text.
     */
    @Test
    void aNegativeSerialStopsTheLoad() {
        DataFileParseException thrown = refused(record("-1", 7, 5));

        assertTrue(thrown.getMessage().contains("illegal negative found on the type value: -1"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 5"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    /** C tests the first character, not the value, so {@code -0} is refused although it is 0. */
    @Test
    void minusZeroIsRefusedLikeAnyOtherNegative() {
        DataFileParseException thrown = refused(record("-0", 7, 1));

        assertTrue(thrown.getMessage().contains("illegal negative found on the type value: -0"), thrown::getMessage);
    }

    // ---- The message count -------------------------------------------------

    /**
     * An eighth {@code message:} line is {@code PARSE_ERROR_TOO_MANY_ENTRIES} in C. The grammar
     * lets it through, so the assembler stops the load, naming the {@code type:} line and the count.
     */
    @Test
    void anEighthMessageStopsTheLoad() {
        DataFileParseException thrown = refused(record("1", 8, 3));

        assertTrue(thrown.getMessage().contains("illegal number of messages. Should be 7, is: 8"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: 3"), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    /** Seven is the limit, not eight: a ninth and a tenth are refused the same way. */
    @Test
    void moreThanEightMessagesAreRefusedToo() {
        assertTrue(refused(record("1", 9, 1)).getMessage().contains("is: 9"));
        assertTrue(refused(record("1", 10, 1)).getMessage().contains("is: 10"));
    }

    /**
     * {@code parse_pain_message} does not demand seven: it leaves the slots it was not given
     * {@code NULL}. The assembler pads with {@code null} to match, so six messages give six real
     * strings and a seventh that is {@code null}.
     */
    @Test
    void fewerThanSevenMessagesArePaddedWithNull() {
        MonsterPain six = assemble(record("1", 6, 1)).get(0);
        MonsterPain three = assemble(record("2", 3, 1)).get(0);

        assertEquals("m6", six.getMessage(5));
        assertNull(six.getMessage(6));
        assertEquals("m3", three.getMessage(2));
        for (int level = 3; level < 7; level++) {
            assertNull(three.getMessage(level), "level " + level);
        }
    }

    /** A {@code type:} line with no {@code message:} lines at all leaves all seven slots {@code NULL}. */
    @Test
    void noMessagesAtAllGivesSevenNulls() {
        MonsterPain pain = assemble(record("5", 0, 1)).get(0);

        assertEquals(5, pain.getPainIndex());
        for (int level = 0; level < 7; level++) {
            assertNull(pain.getMessage(level), "level " + level);
        }
    }

    // ---- Order of the checks and the first bad record ----------------------

    /**
     * C reads the {@code type:} line before the messages under it, so a record that is wrong in
     * both ways is reported for its serial.
     */
    @Test
    void theSerialIsCheckedBeforeTheMessageCount() {
        DataFileParseException thrown = refused(record("-1", 8, 1));

        assertTrue(thrown.getMessage().contains("illegal negative"), thrown::getMessage);
    }

    /**
     * C stops at the first bad line and never reads on, so a bad record after good ones throws
     * and the good ones before it are not returned.
     */
    @Test
    void aBadRecordStopsTheWholeLoad() {
        DataFileParseException thrown = refused(record("1", 7, 1), record("2", 8, 10), record("3", 7, 20));

        assertTrue(thrown.getMessage().contains("line: 10"), thrown::getMessage);
    }

    // ---- Serials C does not check ------------------------------------------

    /**
     * {@code parse_pain_type} reads a {@code uint} and checks nothing else, whatever the header of
     * {@code pain.txt} says about serials increasing, so a repeat and a descending pair both load.
     */
    @Test
    void repeatedAndDescendingSerialsAreNotChecked() {
        List<MonsterPain> pains = assemble(record("5", 7, 1), record("2", 7, 10), record("2", 7, 20));

        assertEquals(List.of(5, 2, 2), pains.stream().map(MonsterPain::getPainIndex).toList());
    }

    /**
     * Accepted deviation (261027): a serial too big for an {@code int} is clamped and loads, where
     * C would keep the low 32 bits. Pinned so a change to the clamp shows up here.
     */
    @Test
    void aSerialBeyondIntRangeIsClamped() {
        assertEquals(Integer.MAX_VALUE, indexOf("99999999999"));
    }
}
