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

package uk.co.jackoftrades.backend.parser;

import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.backend.parser.chesttrap.ChestTrapAssembler;
import uk.co.jackoftrades.backend.parser.chesttrap.ChestTrapParseRecord;
import uk.co.jackoftrades.middle.objects.ChestTrap;
import uk.co.jackoftrades.middle.objects.enums.ChestTrapCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code ChestTrapAssembler}, driven from hand-built {@link ChestTrapParseRecord}s
 * rather than through the grammar.
 *
 * <p>{@code ChestTrapReaderTest} covers the assembler as the grammar can actually reach it; this
 * suite exists for the states the grammar cannot produce but the assembler still guards - a
 * {@code null} in any field, an empty code, a record list longer than {@link ChestTrapCode} has
 * constants. Those arise from ANTLR's error recovery, which leaves a sub-rule's return unset when
 * the directive was present but malformed, so they are reachable in a broken file even though no
 * well-formed file can produce them.
 *
 * <p>Effects are left empty throughout: resolving one needs the monster registries seeded, and
 * effect resolution is {@code EffectAssembler}'s contract to keep, not this assembler's.
 *
 * @author Rowan Crowther
 */
class ChestTrapAssemblerTest {

    /**
     * A record with every field valid for the given position, so a test can vary one thing.
     */
    private static ChestTrapParseRecord record(ChestTrapCode code, String level, int line) {
        return new ChestTrapParseRecord("trap " + code, code.name(), level,
                new ArrayList<>(), "", "", "", "", line);
    }

    /**
     * The seven records a well-formed file yields, with ascending levels.
     */
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

    private static List<String> errors() {
        return new ArrayList<>();
    }

    private static List<ChestTrap> assemble(List<ChestTrapParseRecord> records, List<String> errors) {
        return new ChestTrapAssembler().assemble(records, errors);
    }

    // ---- Happy path -------------------------------------------------------

    private static List<ChestTrapParseRecord> brokenBy(
            java.util.function.Consumer<List<ChestTrapParseRecord>> breakage) {
        List<ChestTrapParseRecord> records = validFile();
        breakage.accept(records);
        return records;
    }

    @Test
    void aWellFormedRecordListAssemblesInOrder() {
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(validFile(), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(List.of(ChestTrapCode.values()), traps.stream().map(ChestTrap::getCode).toList());
        assertEquals(1, traps.getFirst().getPVal());
        assertEquals(64, traps.getLast().getPVal());
    }

    // ---- destroy: and magic: ---------------------------------------------

    @Test
    void anAbsentLevelBecomesZero() {
        // "" is what the grammar's @init leaves when the directive was absent. Level 0 is below
        // every real trap's level, so such a trap is drawable by every chest.
        List<ChestTrapParseRecord> records = validFile();
        records.set(0, record(ChestTrapCode.NO_TRAP, "", 2));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(0, traps.getFirst().getLevel());
    }

    @Test
    void onlyTheLiteralOneSetsDestroyAndMagic() {
        // The data file documents these as "1 if ...", so 1 is the whole of the true case. Note C
        // is looser - parse_chest_trap_destroy takes any non-zero - so "2" diverges deliberately.
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("varied", "POISON", "2",
                new ArrayList<>(), "2", "0", "", "", 6));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertFalse(traps.get(1).isDestroy());
        assertFalse(traps.get(1).isMagic());
    }

    // ---- null fields from error recovery ---------------------------------

    @Test
    void nullDestroyAndMagicAreFalseRatherThanThrowing() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("varied", "POISON", "2",
                new ArrayList<>(), null, null, "", "", 6));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertFalse(traps.get(1).isDestroy());
        assertFalse(traps.get(1).isMagic());
    }

    @Test
    void aNullCodeIsReportedAndRejectsTheFile() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("nameless", null, "2",
                new ArrayList<>(), "", "", "", "", 6));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(traps.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("null code")), errors::toString);
    }

    @Test
    void anEmptyCodeNeverBuildsATrapWithANullCode() {
        // The guard that matters: ChestTrap.getPVal() dereferences the code, so a trap carrying a
        // null one would throw the first time anything asked for its pval bit.
        List<ChestTrapParseRecord> records = validFile();
        records.set(0, new ChestTrapParseRecord("locked", "", "1",
                new ArrayList<>(), "", "", "", "", 2));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(traps.isEmpty());
        assertFalse(errors.isEmpty());
    }

    @Test
    void aNullEffectListIsReportedAndRejectsTheFile() {
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord("broken", "POISON", "2",
                null, "", "", "", "", 6));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(traps.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("no effect")), errors::toString);
    }

    // ---- Structural rules -------------------------------------------------

    @Test
    void nullNameAndMessagesBecomeEmptyStrings() {
        // These three are not worth rejecting a file over, so they are coerced rather than reported
        // - but they must not reach the domain object as null.
        List<ChestTrapParseRecord> records = validFile();
        records.set(1, new ChestTrapParseRecord(null, "POISON", "2",
                new ArrayList<>(), "", "", null, null, 6));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals("", traps.get(1).getName());
        assertEquals("", traps.get(1).getMessage());
        assertEquals("", traps.get(1).getMessageDeath());
    }

    @Test
    void moreRecordsThanThereAreCodesIsReported() {
        List<ChestTrapParseRecord> records = validFile();
        records.add(record(ChestTrapCode.EXPLODE, "30", 40));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(traps.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("trap codes exist")), errors::toString);
    }

    @Test
    void equalLevelsAreAllowedButDescendingOnesAreNot() {
        // The real file has two traps at level 1, so the rule is non-decreasing, not increasing.
        List<ChestTrapParseRecord> equal = validFile();
        equal.set(1, record(ChestTrapCode.POISON, "1", 6));
        List<String> equalErrors = errors();

        assertEquals(7, assemble(equal, equalErrors).size());
        assertTrue(equalErrors.isEmpty(), equalErrors::toString);

        List<ChestTrapParseRecord> descending = validFile();
        descending.set(4, record(ChestTrapCode.SUMMON, "1", 18));
        List<String> descendingErrors = errors();

        assertTrue(assemble(descending, descendingErrors).isEmpty());
        assertTrue(descendingErrors.stream().anyMatch(e -> e.contains("level")),
                descendingErrors::toString);
    }

    @Test
    void everyCodeMustSitAtItsOwnOrdinal() {
        // Swap two records: all seven codes are still present, but two bits have moved.
        List<ChestTrapParseRecord> records = validFile();
        records.set(2, record(ChestTrapCode.LOSE_CON, "2", 14));
        records.set(3, record(ChestTrapCode.LOSE_STR, "3", 18));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(traps.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("LOSE_STR")), errors::toString);
    }

    @Test
    void anIncompleteFileIsRejectedEvenWhenEveryRecordIsValid() {
        List<ChestTrapParseRecord> records = new ArrayList<>(validFile().subList(0, 3));
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(records, errors);

        assertTrue(traps.isEmpty());
        assertTrue(errors.stream().anyMatch(e -> e.contains("expected")), errors::toString);
    }

    @Test
    void anEmptyRecordListYieldsAnEmptyResult() {
        // Pins current behaviour: nothing to assemble is not itself reported, so a caller must not
        // read "empty and no errors" as proof the file was read.
        List<String> errors = errors();

        List<ChestTrap> traps = assemble(new ArrayList<>(), errors);

        assertNotNull(traps);
        assertTrue(traps.isEmpty());
    }

    @Test
    void everyFailurePathReturnsAnEmptyListRatherThanNull() {
        // The reader hands items() straight to ObjectRegistry.setChestTraps, which would throw on a
        // null, so no rejection path may return one.
        for (List<ChestTrapParseRecord> broken : Arrays.asList(
                brokenBy(r -> r.set(0, record(ChestTrapCode.POISON, "1", 2))),
                brokenBy(r -> r.set(1, new ChestTrapParseRecord("x", null, "2",
                        new ArrayList<>(), "", "", "", "", 6))),
                brokenBy(r -> r.set(1, new ChestTrapParseRecord("x", "NOPE", "2",
                        new ArrayList<>(), "", "", "", "", 6))),
                brokenBy(r -> r.remove(6)))) {
            List<String> errors = errors();

            List<ChestTrap> traps = assemble(broken, errors);

            assertNotNull(traps, errors::toString);
            assertTrue(traps.isEmpty(), errors::toString);
            assertFalse(errors.isEmpty());
        }
    }
}
