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

package uk.co.jackoftradesltd.backend.parser.monsterspell;

import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.monsterspell.MonsterSpellParseRecord.MonsterSpellLevelParseRecord;
import uk.co.jackoftradesltd.middle.monsters.MonsterSpellType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link MonsterSpellAssembler} on hand-built {@link MonsterSpellParseRecord}s, so each
 * conversion is exercised without the grammar in front of it. {@code MonsterSpellReaderTest} covers
 * the same class through the real file and the grammar.
 *
 * <p>Every expected value comes from the C source, not from the Java: the spell name from
 * {@code grab_name()} in {@code datafile.c} (exact case, the {@code NONE} to {@code MAX} rows of
 * {@code list-mon-spells.h}), the message type from {@code message_lookup_by_name()} in
 * {@code message.c} (case-blind, a number below {@code MSG_MAX}, no {@code NONE}; the indices are
 * the rows of {@code list-message.h}, where {@code GENERIC} is 0, {@code KILL} is 6 and
 * {@code SCRAMBLE} is 152), the hit chance from the {@code uint} test in {@code parser.c} (a token
 * starting with {@code -} is refused, base 0 otherwise), and the order from
 * {@code parse_mon_spell_name()} in {@code mon-init.c}, which pushes each record on the head of the
 * chain.
 *
 * <p>Class MonsterSpellAssemblerTest coded and commented in full on 261027.
 */
class MonsterSpellAssemblerTest {

    private static final String FILE = "monster_spell.txt";

    private static MonsterSpellLevelParseRecord level() {
        return new MonsterSpellLevelParseRecord("", "lore", "", "", "", "", "", "", "", 4);
    }

    private static MonsterSpellParseRecord record(String name, String msgt, String hit) {
        return new MonsterSpellParseRecord(name, msgt, hit, List.of(), List.of(level()), 3);
    }

    private static List<MonsterSpellType> assemble(MonsterSpellParseRecord... records) {
        return new MonsterSpellAssembler().assemble(List.of(records), new ArrayList<>(), FILE);
    }

    private static Object field(Object target, String name) throws Exception {
        Field f = target.getClass().getDeclaredField(name);
        f.setAccessible(true);
        return f.get(target);
    }

    private static String msgtOf(String msgt) throws Exception {
        return field(assemble(record("BLINK", msgt, "100")).get(0), "msgT").toString();
    }

    private static int hitOf(String hit) throws Exception {
        return (Integer) field(assemble(record("BLINK", "", hit)).get(0), "hit");
    }

    // ---- The spell name ---------------------------------------------------

    @Test
    void aKnownNameResolvesToItsRsfConstant() throws Exception {
        assertEquals("RSF_BLINK", field(assemble(record("BLINK", "", "100")).get(0), "index").toString());
    }

    @Test
    void noneAndMaxResolveAsTheyAreRowsOfListMonSpellsH() throws Exception {
        // r_info_spell_flags runs NONE ... MAX and grab_name() scans N_ELEMENTS - 1 of it.
        assertEquals("RSF_NONE", field(assemble(record("NONE", "", "100")).get(0), "index").toString());
        assertEquals("RSF_MAX", field(assemble(record("MAX", "", "100")).get(0), "index").toString());
    }

    @Test
    void theNameIsExactCaseAndTakesNoPrefix() {
        // grab_name() uses streq(), and the table holds BLINK, not RSF_BLINK.
        for (String name : new String[]{"blink", "Blink", "RSF_BLINK", "", "NOT_A_SPELL"}) {
            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> assemble(record(name, "", "100")), "name:" + name);
            assertEquals("Monster spell starting at line: 3 of file: " + FILE
                    + " has an illegal monster spell value: " + name, e.getMessage());
        }
    }

    // ---- The message type -------------------------------------------------

    @Test
    void anAbsentMsgtIsGenericAsCsZeroedFieldIs() throws Exception {
        assertEquals("MSG_GENERIC", msgtOf(""));
    }

    @Test
    void msgtIsCaseBlind() throws Exception {
        assertEquals("MSG_BR_ACID", msgtOf("BR_ACID"));
        assertEquals("MSG_BR_ACID", msgtOf("br_acid"));
        assertEquals("MSG_BR_ACID", msgtOf("Br_Acid"));
    }

    @Test
    void msgtMayBeANumberBelowMsgMax() throws Exception {
        assertEquals("MSG_GENERIC", msgtOf("0"));
        assertEquals("MSG_KILL", msgtOf("6"));
        assertEquals("MSG_SCRAMBLE", msgtOf("152"));
    }

    @Test
    void msgtMaxResolvesByNameButNotByNumber() throws Exception {
        // MSG(MAX, NULL) is a row of list-message.h, so the name matches; the number 153 is not
        // below MSG_MAX.
        assertEquals("MSG_MAX", msgtOf("MAX"));
        assertThrows(DataFileParseException.class, () -> msgtOf("153"));
    }

    @Test
    void msgtNoneAndOtherBadTokensStopTheLoad() {
        // list-message.h has no NONE, and a number must not be negative.
        for (String msgt : new String[]{"NONE", "none", "NOT_A_MESSAGE", "-1", "6x"}) {
            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> assemble(record("BLINK", msgt, "100")), "msgt:" + msgt);
            assertEquals("Monster spell starting at line: 3 of file: " + FILE
                    + " has an illegal message type value: " + msgt, e.getMessage());
        }
    }

    // ---- The hit chance ---------------------------------------------------

    @Test
    void anAbsentHitIsZeroAsCsZeroedFieldIs() throws Exception {
        assertEquals(0, hitOf(""));
    }

    @Test
    void hitIsReadInBaseZeroAndNotRangeChecked() throws Exception {
        // parser_getuint is strtoul with base 0; C does not check 0 to 100.
        assertEquals(0, hitOf("0"));
        assertEquals(100, hitOf("100"));
        assertEquals(8, hitOf("010"));
        assertEquals(500, hitOf("500"));
    }

    @Test
    void aLeadingMinusOnHitStopsTheLoadEvenForZero() {
        for (String hit : new String[]{"-1", "-5", "-0"}) {
            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> assemble(record("BLINK", "", hit)), "hit:" + hit);
            assertEquals("Monster spell starting at line: 3 of file: " + FILE
                    + " has an illegal monster hit value: " + hit, e.getMessage());
        }
    }

    // ---- Levels, order and the error list ----------------------------------

    @Test
    void aLevelListThatComesBackNullStopsTheLoad() {
        // MonsterSpellLevelAssembler returns null for a power-cutoff that is not a number.
        MonsterSpellParseRecord bad = new MonsterSpellParseRecord("BLINK", "", "100", List.of(),
                List.of(new MonsterSpellLevelParseRecord("abc", "lore", "", "", "", "", "", "", "", 4)), 3);

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(bad));

        assertTrue(e.getMessage().startsWith("Monster spell starting at line: 3 of file: " + FILE),
                e::getMessage);
    }

    @Test
    void theSpellsComeBackLastRecordFirstAsInCsChain() throws Exception {
        List<MonsterSpellType> spells = assemble(record("BLINK", "", "10"),
                record("TPORT", "", "20"), record("HEAL", "", "30"));

        assertEquals(3, spells.size());
        assertEquals("RSF_HEAL", field(spells.get(0), "index").toString());
        assertEquals("RSF_TPORT", field(spells.get(1), "index").toString());
        assertEquals("RSF_BLINK", field(spells.get(2), "index").toString());
    }

    @Test
    void aRepeatedNameKeepsBothAndAFirstMatchFindsTheLaterRecord() throws Exception {
        // monster_spell_by_index() walks the chain from the head and stops at the first match, so
        // in C the later record in the file wins.
        List<MonsterSpellType> spells = assemble(record("BLINK", "", "10"), record("BLINK", "", "20"));

        assertEquals(2, spells.size());
        assertEquals(20, field(spells.get(0), "hit"));
        assertEquals(10, field(spells.get(1), "hit"));
    }

    @Test
    void noRecordsGiveAnEmptyList() {
        assertTrue(assemble().isEmpty());
    }

    @Test
    void aCleanAssemblyWritesNothingToTheErrorList() {
        List<String> errors = new ArrayList<>();

        List<MonsterSpellType> spells = new MonsterSpellAssembler()
                .assemble(List.of(record("BLINK", "TELEPORT", "100")), errors, FILE);

        assertEquals(1, spells.size());
        assertTrue(errors.isEmpty(), errors::toString);
    }

    @Test
    void theFatalErrorNamesTheLineOfTheNameDirective() {
        MonsterSpellParseRecord bad = new MonsterSpellParseRecord("NOPE", "", "100", List.of(),
                List.of(level()), 57);

        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(bad));

        assertTrue(e.getMessage().contains("line: 57 of file: " + FILE), e::getMessage);
    }
}
