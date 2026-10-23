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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodAssembler;
import uk.co.jackoftradesltd.backend.parser.blowmethod.BlowMethodParseRecord;
import uk.co.jackoftradesltd.middle.combat.BlowMethod;
import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link BlowMethodAssembler}.
 *
 * <p>These construct {@link BlowMethodParseRecord}s directly - no grammar, no reader - so values
 * the lexer would never produce (a {@code msg:} with a form feed, a digit from another script) can
 * still reach the assembler. End-to-end cases live in {@code BlowMethodReaderTest}.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>the four flags from {@code parse_meth_cut} and its siblings in {@code mon-init.c}, which
 *       read with {@code parser_getuint} ({@code strtoul} in base 0, refusing a leading
 *       {@code -}) and store {@code val ? true : false};</li>
 *   <li>{@code msg:} from {@code parse_meth_message_type} and {@code message_lookup_by_name} in
 *       {@code message.c}. Every row of {@link #msgValuesC} was checked against a small C program
 *       built from the real {@code list-message.h}, with {@code strtoul} and a case-blind compare
 *       standing in for {@code my_stricmp};</li>
 *   <li>the list order from {@code parse_meth_name} and {@code finish_parse_meth}: the last
 *       record in the file is {@code blow_methods[1]};</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class BlowMethodAssemblerTest coded on 261023, commented in full on 261023.
 *
 * @author Rowan Crowther
 */
class BlowMethodAssemblerTest {

    private static final String FILE = "blow_methods.txt";

    /**
     * Builds a parse record with the given flag and message text, and fixed act and desc.
     */
    private static BlowMethodParseRecord rec(String name, String cut, String stun, String miss,
                                             String phys, String msg, int line) {
        return new BlowMethodParseRecord(name, cut, stun, miss, phys, msg,
                List.of("first", "second"), "desc of " + name, line);
    }

    private static BlowMethod one(BlowMethodParseRecord record) {
        List<BlowMethod> methods = new BlowMethodAssembler().assemble(List.of(record), new ArrayList<>(), FILE);
        assertEquals(1, methods.size());
        return methods.getFirst();
    }

    private static BlowMethod withFlags(String cut, String stun, String miss, String phys) {
        return one(rec("POKE", cut, stun, miss, phys, "MON_HIT", 2));
    }

    private static BlowMethod withMsg(String msg) {
        return one(rec("POKE", "0", "0", "0", "0", msg, 2));
    }

    // ---- The four flags --------------------------------------------------

    @Test
    void eachFlagIsReadFromItsOwnField() {
        // TOUCH and ENGULF ship miss:1 phys:0, so a phys read from miss would show here.
        BlowMethod m = withFlags("1", "0", "1", "0");
        assertTrue(m.isCut());
        assertFalse(m.isStun());
        assertTrue(m.isMiss());
        assertFalse(m.isPhys());

        m = withFlags("0", "1", "0", "1");
        assertFalse(m.isCut());
        assertTrue(m.isStun());
        assertFalse(m.isMiss());
        assertTrue(m.isPhys());
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "7", "01", "010", "4294967295"})
    void anyNonZeroFlagIsTrue(String value) {
        BlowMethod m = withFlags(value, value, value, value);
        assertTrue(m.isCut());
        assertTrue(m.isStun());
        assertTrue(m.isMiss());
        assertTrue(m.isPhys());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "00", "000", "08", "09"})
    void aFlagThatStrtoulReadsAsZeroIsFalse(String value) {
        // Base 0: a leading 0 is octal, and strtoul stops at 8 or 9, so "08" is 0.
        BlowMethod m = withFlags(value, value, value, value);
        assertFalse(m.isCut());
        assertFalse(m.isStun());
        assertFalse(m.isMiss());
        assertFalse(m.isPhys());
    }

    @Test
    void aFlagTooBigForAnIntIsClampedTrueWhereCWrapsToZero() {
        // Accepted deviation recorded on HelperFunctions: C's unsigned int keeps the low 32 bits,
        // so 4294967296 is 0 and false there. Pinned so a change to the clamp is noticed.
        assertTrue(withFlags("4294967296", "0", "0", "0").isCut());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0", "-1"})
    void aNegativeFlagEndsTheLoadOnEveryField(String value) {
        // parser_getuint refuses any token that starts with '-', even "-0".
        assertThrows(DataFileParseException.class, () -> withFlags(value, "0", "0", "0"));
        assertThrows(DataFileParseException.class, () -> withFlags("0", value, "0", "0"));
        assertThrows(DataFileParseException.class, () -> withFlags("0", "0", value, "0"));
        assertThrows(DataFileParseException.class, () -> withFlags("0", "0", "0", value));
    }

    // ---- msg: ------------------------------------------------------------

    @Test
    void absentOrBareMsgIsGeneric() {
        // parser_hasval is false, so msgt keeps its zeroed MSG_GENERIC.
        assertEquals(MessageType.MSG_GENERIC, withMsg("").getMesgT());
        assertEquals(MessageType.MSG_GENERIC, withMsg(null).getMesgT());
    }

    /**
     * The C results of {@code message_lookup_by_name} for each text, as list-message.h names.
     */
    static Stream<Arguments> msgValuesC() {
        return Stream.of(
                Arguments.of("MON_HIT", MessageType.MSG_MON_HIT),
                Arguments.of("mon_hit", MessageType.MSG_MON_HIT),
                Arguments.of("Mon_Hit", MessageType.MSG_MON_HIT),
                Arguments.of("MAX", MessageType.MSG_MAX),
                Arguments.of("34", MessageType.MSG_MON_HIT),
                Arguments.of("0", MessageType.MSG_GENERIC),
                Arguments.of("3", MessageType.MSG_MISS),
                Arguments.of("003", MessageType.MSG_MISS),
                Arguments.of("152", MessageType.MSG_SCRAMBLE),
                Arguments.of("3 ", MessageType.MSG_MISS),
                Arguments.of("3\t", MessageType.MSG_MISS),
                Arguments.of(" 3", MessageType.MSG_MISS),
                Arguments.of("\t3", MessageType.MSG_MISS),
                Arguments.of("\u000b3", MessageType.MSG_MISS),
                Arguments.of("\f3", MessageType.MSG_MISS),
                Arguments.of(" \t3 \t", MessageType.MSG_MISS),
                Arguments.of("+3", MessageType.MSG_MISS),
                Arguments.of(" +3", MessageType.MSG_MISS),
                Arguments.of("+0", MessageType.MSG_GENERIC),
                Arguments.of("-0", MessageType.MSG_GENERIC));
    }

    @ParameterizedTest
    @MethodSource("msgValuesC")
    void msgResolvesAsMessageLookupByNameDoes(String msg, MessageType expected) {
        assertEquals(expected, withMsg(msg).getMesgT());
    }

    @ParameterizedTest
    @ValueSource(strings = {"BOGUS", "NONE", "none", " MON_HIT", "MON_HIT ", "\tMON_HIT",
            "153", "154", "99999999999", "-1", "-3",
            "3x", "3 4", "3\t4", "3\u000b", "3\f", "3+", "3 +",
            "+ 3", "- 0", "+\t3", "++3", "+-3", "+", "-", "   ", "٣"})
    void msgThatCRefusesEndsTheLoad(String msg) {
        // message_lookup_by_name returns -1, so parse_meth_message_type returns
        // PARSE_ERROR_INVALID_MESSAGE. NONE is Java-only and 153 is C's MSG_MAX.
        assertThrows(DataFileParseException.class, () -> withMsg(msg));
    }

    @Test
    void theMsgErrorNamesTheFileTheLineAndTheValue() {
        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> one(rec("POKE", "0", "0", "0", "0", "BOGUS", 42)));
        assertTrue(e.getMessage().contains("line: 42"), e::getMessage);
        assertTrue(e.getMessage().contains(FILE), e::getMessage);
        assertTrue(e.getMessage().contains("BOGUS"), e::getMessage);
    }

    // ---- Order, pass-through and the error list --------------------------

    @Test
    void recordsComeBackInReverseFileOrder() {
        List<BlowMethodParseRecord> records = List.of(
                rec("FIRST", "0", "0", "0", "0", "MON_HIT", 2),
                rec("SECOND", "0", "0", "0", "0", "MON_HIT", 10),
                rec("THIRD", "0", "0", "0", "0", "MON_HIT", 18));

        List<String> names = new BlowMethodAssembler().assemble(records, new ArrayList<>(), FILE)
                .stream().map(BlowMethod::getName).toList();

        assertEquals(List.of("THIRD", "SECOND", "FIRST"), names);
    }

    @Test
    void nameActAndDescArePassedThrough() {
        BlowMethod m = withMsg("MON_HIT");
        assertEquals("POKE", m.getName());
        assertEquals(List.of("first", "second"), m.getBlowMessage());
        assertEquals("desc of POKE", m.getDesc());
    }

    @Test
    void theFirstBadRecordInFileOrderIsTheOneReported() {
        List<BlowMethodParseRecord> records = List.of(
                rec("GOOD", "0", "0", "0", "0", "MON_HIT", 2),
                rec("BAD1", "0", "0", "0", "0", "BOGUS_ONE", 10),
                rec("BAD2", "0", "0", "0", "0", "BOGUS_TWO", 18));

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new BlowMethodAssembler().assemble(records, new ArrayList<>(), FILE));
        assertTrue(e.getMessage().contains("BOGUS_ONE"), e::getMessage);
    }

    @Test
    void theErrorListIsNeverWritten() {
        List<String> errors = new ArrayList<>();
        new BlowMethodAssembler().assemble(List.of(rec("POKE", "1", "1", "1", "1", "MON_HIT", 2)), errors, FILE);
        assertTrue(errors.isEmpty());

        assertThrows(DataFileParseException.class, () -> new BlowMethodAssembler()
                .assemble(List.of(rec("POKE", "0", "0", "0", "0", "BOGUS", 2)), errors, FILE));
        assertTrue(errors.isEmpty());
    }
}
