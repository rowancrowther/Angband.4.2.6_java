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
import uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapAssembler;
import uk.co.jackoftradesltd.backend.parser.chesttrap.ChestTrapParseRecord;
import uk.co.jackoftradesltd.backend.parser.grammars.EffectParseRecord;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;
import uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code ChestTrapAssembler}, driven from hand-built {@link ChestTrapParseRecord}s
 * rather than through the grammar.
 *
 * <p>Expected values come from the {@code parse_chest_trap_*} handlers in {@code obj-chest.c}:
 * {@code parser_getint} is {@code strtol} in base 0, {@code destroy:} and {@code magic:} test
 * {@code if (val)}, and an absent directive leaves {@code mem_zalloc}'s 0 or {@code NULL}. Where
 * Java refuses what C loads (codes, order, count, descending levels) the test names the accepted
 * deviation in {@code docs/Accepted_Deviations.md}.
 *
 * <p>Every error ends the load with a {@link DataFileParseException} at the first bad record, as C's
 * parser errors end the {@code chest_trap.txt} load, so the failure tests assert the throw and its
 * message rather than an error list.
 *
 * <p>{@code ChestTrapReaderTest} covers the same rules as the grammar can reach them; this suite also
 * covers the {@code null} fields only a hand-built record can carry, because {@code ChestTrapReader}
 * throws on any syntax error before the assembler runs. Effects that resolve need the monster
 * registries seeded and are left to that suite; the one effect here has no type, which
 * {@code EffectAssembler} refuses before it reaches any registry.
 *
 * @author Rowan Crowther
 */
class ChestTrapAssemblerTest {

    /** A record valid at its position, with no effect, flags or messages. */
    private static ChestTrapParseRecord record(ChestTrapCode code, String level, int line) {
        return new ChestTrapParseRecord("trap " + code, code.name(), level,
                new ArrayList<>(), "", "", "", "", line);
    }

    /** A record valid at its position with the given destroy: and magic: text. */
    private static ChestTrapParseRecord flags(ChestTrapCode code, String level, String destroy,
                                              String magic, int line) {
        return new ChestTrapParseRecord("trap " + code, code.name(), level,
                new ArrayList<>(), destroy, magic, "", "", line);
    }

    /** The seven records a well-formed file yields, with levels 1 to 7 and lines 2, 6, 10 and on. */
    private static List<ChestTrapParseRecord> validFile() {
        List<ChestTrapParseRecord> records = new ArrayList<>();
        int line = 2;
        int level = 1;
        for (ChestTrapCode code : ChestTrapCode.values()) {
            records.add(record(code, String.valueOf(level++), line));
            line += 4;
        }
        return records;
    }

    private static List<ChestTrap> assemble(List<ChestTrapParseRecord> records) {
        return new ChestTrapAssembler().assemble(records, "test.txt");
    }

    private static DataFileParseException refused(List<ChestTrapParseRecord> records) {
        return assertThrows(DataFileParseException.class, () -> assemble(records));
    }

    // ---- Happy path -------------------------------------------------------

    @Test
    void aWellFormedRecordListAssemblesInFileOrder() {
        // parse_chest_trap_name appends to the tail, so C's list is file order.

        List<ChestTrap> traps = assemble(validFile());

        assertEquals(List.of(ChestTrapCode.values()), traps.stream().map(ChestTrap::getCode).toList());
        assertEquals(List.of(1, 2, 3, 4, 5, 6, 7), traps.stream().map(ChestTrap::getLevel).toList());
    }

    @Test
    void pvalsDoubleFromOneInFileOrder() {
        // parse_chest_trap_name: the first record gets 1, each later one twice its predecessor.
        List<ChestTrap> traps = assemble(validFile());

        assertEquals(List.of(1, 2, 4, 8, 16, 32, 64), traps.stream().map(ChestTrap::getPVal).toList());
    }

    @Test
    void nameAndMessagesAreCopiedVerbatim() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("gas trap", "POISON", "2", new ArrayList<>(),
                "", "", "A puff of green gas: surrounds you!", "a  gas trap", 6));

        ChestTrap trap = assemble(records).get(1);

        assertEquals("gas trap", trap.getName());
        assertEquals("A puff of green gas: surrounds you!", trap.getMessage());
        assertEquals("a  gas trap", trap.getMessageDeath());
    }

    // ---- level: -----------------------------------------------------------

    @Test
    void anAbsentLevelIsZeroAsMemZallocLeavesIt() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(0, record(ChestTrapCode.NO_TRAP, "", 2));

        assertEquals(0, assemble(records).getFirst().getLevel());
    }

    @Test
    void aLeadingZeroLevelIsOctalAsStrtolBaseZeroReadsIt() {
        // parser_getint is strtol(..., 0): level:010 is 8 and level:07 is 7.
        List<ChestTrapParseRecord> records = validFile();
        records.set(6, record(ChestTrapCode.EXPLODE, "010", 26));
        records.set(5, record(ChestTrapCode.PARALYZE, "07", 22));

        List<ChestTrap> traps = assemble(records);

        assertEquals(7, traps.get(5).getLevel());
        assertEquals(8, traps.get(6).getLevel());
    }

    @Test
    void aLevelWithTrailingTextIsReadUpToItAsStrtolDoes() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(6, record(ChestTrapCode.EXPLODE, "25abc", 26));

        assertEquals(25, assemble(records).get(6).getLevel());
    }

    @Test
    void aLevelWithNoDigitsEndsTheLoadAsNotNumberDoesInC() {
        // parser_getint fails to convert, so the handler never runs: PARSE_ERROR_NOT_NUMBER.
        List<ChestTrapParseRecord> records = validFile();
        records.set(3, record(ChestTrapCode.LOSE_CON, "abc", 14));

        refused(records);
    }

    @Test
    void aLevelTooBigForAnIntIsClampedWhereCWraps() {
        // Accepted deviation (ParseUtils): C keeps the low 32 bits, -1294967296; Java clamps.
        List<ChestTrapParseRecord> records = validFile();
        records.set(6, record(ChestTrapCode.EXPLODE, "3000000000", 26));

        assertEquals(Integer.MAX_VALUE, assemble(records).get(6).getLevel());
    }

    @Test
    void equalLevelsPassAsTheShippedFileHasTwoAtLevelOne() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, record(ChestTrapCode.POISON, "1", 6));

        List<ChestTrap> traps = assemble(records);

        assertEquals(1, traps.get(0).getLevel());
        assertEquals(1, traps.get(1).getLevel());
    }

    @Test
    void aDescendingLevelEndsTheLoadWhereCLoadsIt() {
        // Accepted deviation: C checks no level order.
        List<ChestTrapParseRecord> records = validFile();
        records.set(4, record(ChestTrapCode.SUMMON, "1", 18));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("line: 18"), e::getMessage);
        assertTrue(e.getMessage().contains("must not descend"), e::getMessage);
    }

    @Test
    void aFirstLevelOfZeroPassesButANegativeOneEndsTheLoad() {
        // The first level is compared against 0; C would store -1 and load it.
        List<ChestTrapParseRecord> zero = validFile();
        zero.set(0, record(ChestTrapCode.NO_TRAP, "0", 2));
        assertEquals(0, assemble(zero).getFirst().getLevel());

        List<ChestTrapParseRecord> negative = validFile();
        negative.set(0, record(ChestTrapCode.NO_TRAP, "-1", 2));
        refused(negative);
    }

    // ---- destroy: and magic: ----------------------------------------------

    @Test
    void anyNonZeroDestroyOrMagicIsTrueAsCTestsIfVal() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, flags(ChestTrapCode.POISON, "2", "1", "1", 6));
        records.set(2, flags(ChestTrapCode.LOSE_STR, "3", "2", "3", 10));
        records.set(3, flags(ChestTrapCode.LOSE_CON, "4", "-1", "255", 14));

        List<ChestTrap> traps = assemble(records);

        for (int i = 1; i <= 3; i++) {
            assertTrue(traps.get(i).isDestroy(), "record " + i);
            assertTrue(traps.get(i).isMagic(), "record " + i);
        }
    }

    @Test
    void zeroAndAbsentDestroyAndMagicAreFalse() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, flags(ChestTrapCode.POISON, "2", "0", "0", 6));
        records.set(2, flags(ChestTrapCode.LOSE_STR, "3", "", "", 10));
        records.set(3, flags(ChestTrapCode.LOSE_CON, "4", "-0", "00", 14));

        List<ChestTrap> traps = assemble(records);

        for (int i = 1; i <= 3; i++) {
            assertFalse(traps.get(i).isDestroy(), "record " + i);
            assertFalse(traps.get(i).isMagic(), "record " + i);
        }
    }

    @Test
    void anOctalDestroyIsReadInBaseZero() {
        // 010 is 8 in base 0, non-zero, so true; 00 is 0, so false.
        List<ChestTrapParseRecord> records = validFile();
        records.set(6, flags(ChestTrapCode.EXPLODE, "7", "010", "00", 26));

        ChestTrap trap = assemble(records).get(6);

        assertTrue(trap.isDestroy());
        assertFalse(trap.isMagic());
    }

    @Test
    void aDestroyOrMagicWithNoDigitsEndsTheLoad() {
        List<ChestTrapParseRecord> destroy = validFile();
        destroy.set(6, flags(ChestTrapCode.EXPLODE, "7", "yes", "", 26));
        refused(destroy);

        List<ChestTrapParseRecord> magic = validFile();
        magic.set(4, flags(ChestTrapCode.SUMMON, "5", "", "yes", 18));
        refused(magic);
    }

    @Test
    void aHexDestroyOrMagicIsReadInBaseZero() {
        // strtol(..., 0): 0x10 is 16, so true; 0x0 is 0, so false; a bare 0x reads the 0 and stops,
        // so also false (the handler runs, val is 0).
        List<ChestTrapParseRecord> records = validFile();
        records.set(4, flags(ChestTrapCode.SUMMON, "5", "0x10", "0x0", 18));
        records.set(5, flags(ChestTrapCode.PARALYZE, "6", "0x", "0X1F", 22));

        List<ChestTrap> traps = assemble(records);

        assertTrue(traps.get(4).isDestroy());
        assertFalse(traps.get(4).isMagic());
        assertFalse(traps.get(5).isDestroy());
        assertTrue(traps.get(5).isMagic());
    }

    @Test
    void destroyAndMagicStopAtTheFirstCharacterThatIsNotADigitOfTheBase() {
        // strtol stops at the first non-digit and ignores the rest: 5abc is 5 (true); 08 reads the
        // 0 and stops at the 8, which is not octal (false); 0abc reads the 0 (false).
        List<ChestTrapParseRecord> records = validFile();
        records.set(4, flags(ChestTrapCode.SUMMON, "5", "5abc", "08", 18));
        records.set(5, flags(ChestTrapCode.PARALYZE, "6", "0abc", "1 2", 22));

        List<ChestTrap> traps = assemble(records);

        assertTrue(traps.get(4).isDestroy());
        assertFalse(traps.get(4).isMagic());
        assertFalse(traps.get(5).isDestroy());
        assertTrue(traps.get(5).isMagic());
    }

    @Test
    void destroyAndMagicSkipLeadingWhitespaceAndAcceptAPlusSign() {
        // strtol skips leading whitespace and takes one + or -.
        List<ChestTrapParseRecord> records = validFile();
        records.set(4, flags(ChestTrapCode.SUMMON, "5", " 1", "\t+1", 18));
        records.set(5, flags(ChestTrapCode.PARALYZE, "6", "+0", " -0", 22));

        List<ChestTrap> traps = assemble(records);

        assertTrue(traps.get(4).isDestroy());
        assertTrue(traps.get(4).isMagic());
        assertFalse(traps.get(5).isDestroy());
        assertFalse(traps.get(5).isMagic());
    }

    @Test
    void aSignWithNoDigitsOrOnlyWhitespaceInDestroyOrMagicEndsTheLoad() {
        // strtol reads nothing (z == tok), so PARSE_ERROR_NOT_NUMBER: "-", "+", "- 1", "--1", "  ".
        for (String bad : new String[]{"-", "+", "- 1", "--1", "  "}) {
            List<ChestTrapParseRecord> destroy = validFile();
            destroy.set(6, flags(ChestTrapCode.EXPLODE, "7", bad, "", 26));
            DataFileParseException e = refused(destroy);
            assertTrue(e.getMessage().contains("invalid destroy value"), bad + ": " + e.getMessage());

            List<ChestTrapParseRecord> magic = validFile();
            magic.set(4, flags(ChestTrapCode.SUMMON, "5", "", bad, 18));
            e = refused(magic);
            assertTrue(e.getMessage().contains("invalid magic value"), bad + ": " + e.getMessage());
        }
    }

    @Test
    void aDestroyOrMagicTooBigForAnIntEndsTheLoadWhereCWraps() {
        // Accepted deviation (numbers outside the int range are refused): C keeps the low 32 bits,
        // so 4294967296 is 0 (false) and 2147483648 is INT_MIN (true); Java loads neither.
        for (String big : new String[]{"4294967296", "2147483648", "99999999999999999999"}) {
            List<ChestTrapParseRecord> destroy = validFile();
            destroy.set(6, flags(ChestTrapCode.EXPLODE, "7", big, "", 26));
            refused(destroy);

            List<ChestTrapParseRecord> magic = validFile();
            magic.set(4, flags(ChestTrapCode.SUMMON, "5", "", big, 18));
            refused(magic);
        }
    }

    @Test
    void aDestroyErrorNamesTheRecordLineAndTheOffendingText() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(6, flags(ChestTrapCode.EXPLODE, "7", "yes", "", 26));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("line: 26"), e::getMessage);
        assertTrue(e.getMessage().contains("file: test.txt"), e::getMessage);
        assertTrue(e.getMessage().contains("yes"), e::getMessage);
    }

    @Test
    void aHexPlusSignedOrPaddedLevelIsReadAsStrtolBaseZeroReadsIt() {
        // 0x10 is 16, +7 is 7, " 25" is 25 (leading whitespace skipped).
        List<ChestTrapParseRecord> hex = validFile();
        hex.set(6, record(ChestTrapCode.EXPLODE, "0x10", 26));
        assertEquals(16, assemble(hex).get(6).getLevel());

        List<ChestTrapParseRecord> plus = validFile();
        plus.set(6, record(ChestTrapCode.EXPLODE, "+7", 26));
        assertEquals(7, assemble(plus).get(6).getLevel());

        List<ChestTrapParseRecord> padded = validFile();
        padded.set(6, record(ChestTrapCode.EXPLODE, " 25", 26));
        assertEquals(25, assemble(padded).get(6).getLevel());
    }

    @Test
    void aLevelThatIsOnlyASignOrWhitespaceEndsTheLoad() {
        // strtol reads no digits: PARSE_ERROR_NOT_NUMBER.
        for (String bad : new String[]{"-", "+", "  ", "- 5"}) {
            List<ChestTrapParseRecord> records = validFile();
            records.set(6, record(ChestTrapCode.EXPLODE, bad, 26));
            refused(records);
        }
    }

    @Test
    void destroyAndMagicAreDecidedIndependentlyWhenOnlyOneIsPresent() {
        // parse_chest_trap_destroy and parse_chest_trap_magic are separate handlers: a record that
        // names one leaves the other at mem_zalloc's false.
        List<ChestTrapParseRecord> records = validFile();
        records.set(4, flags(ChestTrapCode.SUMMON, "5", "", "1", 18));
        records.set(6, flags(ChestTrapCode.EXPLODE, "7", "1", "", 26));

        List<ChestTrap> traps = assemble(records);

        assertFalse(traps.get(4).isDestroy());
        assertTrue(traps.get(4).isMagic());
        assertTrue(traps.get(6).isDestroy());
        assertFalse(traps.get(6).isMagic());
    }

    @Test
    void aDescendingLevelMessageQuotesTheLevelNotTheCode() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(4, record(ChestTrapCode.SUMMON, "1", 18));

        String message = refused(records).getMessage();

        assertTrue(message.contains("invalid level value: 1 "), message);
        assertFalse(message.contains("SUMMON"), message);
    }

    // ---- code: ------------------------------------------------------------

    @Test
    void anUnknownCodeEndsTheLoadWhereCStoresItAsAString() {
        // Accepted deviation: C's parse_chest_trap_code keeps any string.
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("x", "NOT_A_TRAP", "2",
                new ArrayList<>(), "", "", "", "", 6));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("invalid trap code value: NOT_A_TRAP"), e::getMessage);
        assertTrue(e.getMessage().contains("line: 6"), e::getMessage);
    }

    @Test
    void theCodeIsMatchedCaseSensitively() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("x", "poison", "2",
                new ArrayList<>(), "", "", "", "", 6));

        refused(records);
    }

    @Test
    void anEmptyCodeEndsTheLoadAsItMatchesNoPosition() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(0, new ChestTrapParseRecord("locked", "", "1",
                new ArrayList<>(), "", "", "", "", 2));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("should be NO_TRAP"), e::getMessage);
    }

    @Test
    void aCodeOutOfDeclarationOrderEndsTheLoad() {
        // Swap two records: every code is present, but C would give LOSE_CON bit 4 and LOSE_STR 8.
        List<ChestTrapParseRecord> records = validFile();
        records.set(2, record(ChestTrapCode.LOSE_CON, "3", 10));
        records.set(3, record(ChestTrapCode.LOSE_STR, "4", 14));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("LOSE_CON should be LOSE_STR"), e::getMessage);
    }

    @Test
    void aFirstRecordThatIsNotNoTrapEndsTheLoad() {
        // pick_one_chest_trap starts from chest_traps->next, so record 0 must be the locked entry.
        List<ChestTrapParseRecord> records = validFile();
        records.removeFirst();

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("should be NO_TRAP"), e::getMessage);
    }

    // ---- Record count -----------------------------------------------------

    @Test
    void moreRecordsThanThereAreCodesEndsTheLoad() {
        List<ChestTrapParseRecord> records = validFile();
        records.add(record(ChestTrapCode.EXPLODE, "30", 30));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("out of bounds"), e::getMessage);
        assertTrue(e.getMessage().contains("line: 30"), e::getMessage);
    }

    @Test
    void aValidPrefixOfTheFileEndsTheLoadAsIncomplete() {
        List<ChestTrapParseRecord> records = new ArrayList<>(validFile().subList(0, 3));

        DataFileParseException e = refused(records);

        assertTrue(e.getMessage().contains("Expected: 7 received: 3"), e::getMessage);
    }

    @Test
    void anEmptyRecordListEndsTheLoadWhereCLoadsNoTraps() {
        // C's run_parse_chest_trap leaves chest_traps NULL; the count check refuses it here.
        DataFileParseException e = refused(new ArrayList<>());

        assertTrue(e.getMessage().contains("Expected: 7 received: 0"), e::getMessage);
    }

    // ---- Effects ----------------------------------------------------------

    @Test
    void aRecordWithNoEffectBlockGetsAnEmptyEffectList() {
        // C leaves t->effect NULL; the Java list is empty, never null.
        ChestTrap locked = assemble(validFile()).getFirst();

        assertNotNull(locked.getEffect());
        assertTrue(locked.getEffect().isEmpty());
    }

    @Test
    void anEffectBlockThatFailsToResolveEndsTheLoad() {
        // As an error from grab_effect_data ends the C load. EffectAssembler throws the
        // DataFileParseException itself, naming the effect's own line (7).
        EffectParseRecord untyped = new EffectParseRecord("", "", "", "", "", "", "", "", "", "", "", 7);
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("gas trap", "POISON", "2",
                new ArrayList<>(List.of(untyped)), "", "", "", "", 6));

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(records));

        assertTrue(e.getMessage().contains("Effect starting at line: 7"), e::getMessage);
        assertTrue(e.getMessage().contains("has an empty element value"), e::getMessage);
    }

    // ---- Error handling ---------------------------------------------------

    @Test
    void theFirstBadRecordEndsTheLoadAndAddsNoErrors() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("x", "FIRST_BAD", "2",
                new ArrayList<>(), "", "", "", "", 6));
        records.set(2, new ChestTrapParseRecord("x", "SECOND_BAD", "3",
                new ArrayList<>(), "", "", "", "", 10));

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> assemble(records));

        assertTrue(e.getMessage().contains("FIRST_BAD"), e::getMessage);
        assertFalse(e.getMessage().contains("SECOND_BAD"), e::getMessage);
    }

    // ---- null fields only a hand-built record can carry ----------------------

    @Test
    void aNullCodeEndsTheLoad() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("x", null, "2",
                new ArrayList<>(), "", "", "", "", 6));

        refused(records);
    }

    @Test
    void aNullDestroyOrMagicEndsTheLoad() {
        List<ChestTrapParseRecord> destroy = validFile();
        destroy.set(1, flags(ChestTrapCode.POISON, "2", null, "", 6));
        assertTrue(refused(destroy).getMessage().contains("invalid destroy value: null"));

        List<ChestTrapParseRecord> magic = validFile();
        magic.set(1, flags(ChestTrapCode.POISON, "2", "", null, 6));
        assertTrue(refused(magic).getMessage().contains("invalid magic value: null"));
    }

    @Test
    void aNullNameBecomesAnEmptyStringButAbsentMessagesStayNull() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord(null, "POISON", "2",
                new ArrayList<>(), "", "", null, null, 6));

        ChestTrap trap = assemble(records).get(1);

        assertEquals("", trap.getName());
        // C leaves msg and msg_death NULL when the file has no such line.
        assertNull(trap.getMessage());
        assertNull(trap.getMessageDeath());
    }

    @Test
    void aNullEffectListIsTreatedAsNoEffects() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("x", "POISON", "2",
                null, "", "", "", "", 6));

        assertTrue(assemble(records).get(1).getEffect().isEmpty());
    }

    @Test
    void aNullLevelThrowsNullPointerExceptionAsItHasNoGuard() {
        // Pins the asymmetry the assemble Javadoc records: unreachable through ChestTrapReader.
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, record(ChestTrapCode.POISON, null, 6));

        assertThrows(NullPointerException.class, () -> assemble(records));
    }
}
