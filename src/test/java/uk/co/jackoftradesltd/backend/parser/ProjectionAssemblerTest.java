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
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionAssembler;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.combat.enums.ProjectionType;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ProjectionAssembler}. The expected values come from the C original's
 * {@code parse_projection_*} functions and {@code finish_parse_projection()} in {@code obj-init.c},
 * {@code parse_random()} and the {@code uint} branch of {@code parser_parse()} in {@code parser.c},
 * {@code message_lookup_by_name()} in {@code message.c}, {@code color_text_to_attr()} and
 * {@code color_char_to_attr()} in {@code z-color.c}, and the shipped
 * {@code lib/gamedata/projection.txt}.
 *
 * <p>Most tests are driven from hand-built {@link ProjectionParseRecord}s so that one field can be
 * varied at a time; {@link #realFileLoadsAllFiftySixProjections()} runs the shipped file through
 * the full reader. {@link Projection} has getters for only some of its fields, so the rest are read
 * by reflection through {@link #field(Projection, String)}.
 *
 * <p>The row positions used below follow the file: records 1-25 are the elements, so index 4 is
 * {@code POIS} (position 5) and index 24 is {@code ARROW}; index 30 is {@code MAKE_DOOR}, an
 * environs row.
 *
 * <p>C quits on the first bad line, so every fault is expected to stop the load by throwing
 * {@link DataFileParseException}, never to skip a record or return an empty list, and the
 * {@code errors} list is never written to. The code of a record is only looked at for the first 25
 * records, as in C's {@code parse_projection_code()}: an unknown or empty code stops the load
 * there, and past them an unknown code is stored as {@code PROJ_NONE} with no error, while an empty
 * one still stops it.
 *
 * <p>Accepted deviation (decided 261029): an all-digit {@code denominator:} above {@code INT_MAX}
 * is clamped, where C's {@code parse_random()} refuses it. {@link
 * #aDigitOnlyDenominatorAboveIntMaxIsClampedWhereCRefusesIt()} pins the Java side.
 *
 * <p>Test class ProjectionAssemblerTest coded on 261001, commented in full on 261029; the
 * stop-the-load tests rewritten, and the field, denominator, base and colour tests added, on
 * 261029.
 *
 * @author Rowan Crowther
 */
class ProjectionAssemblerTest {

    private static final String REAL_FILE = "lib/gamedata/projection.txt";
    private static final int POIS = 4;
    private static final int ARROW = 24;
    private static final int MAKE_DOOR = 30;

    /**
     * A record with every field valid, so a test can vary one thing.
     */
    private static ProjectionParseRecord record(String code, String type, int line) {
        return new ProjectionParseRecord(code, code.toLowerCase(), type, "d", "pd", "bd", "ld",
                "1", "1", "1", "1600", null, "1", "0", "White", line);
    }

    /**
     * Every projection of the shipped file in order, with types taken from the constants'
     * positions: the first 25 are elements, the next 7 environs, the remaining 24 monster. The
     * enum's two sentinels, {@code PROJ_NONE} at the front and {@code PROJ_MAX} at the end, are not
     * projections in the file, so neither gets a record.
     */
    private static List<ProjectionParseRecord> validFile() {
        List<ProjectionParseRecord> records = new ArrayList<>();
        int line = 0;
        for (ProjectionEnum e : ProjectionEnum.values()) {
            if (e == ProjectionEnum.PROJ_NONE || e == ProjectionEnum.PROJ_MAX) continue;
            String type = e.ordinal() <= ProjectionEnum.PROJ_ARROW.ordinal() ? "element"
                    : e.ordinal() <= ProjectionEnum.PROJ_MAKE_TRAP.ordinal() ? "environs" : "monster";
            records.add(record(e.name().substring(5), type, ++line));
        }
        return records;
    }

    private static ProjectionParseRecord with(ProjectionParseRecord r, String numerator, String denominator,
                                              String divisor, String damageCap, String msgt,
                                              String obvious, String wake, String colour) {
        return new ProjectionParseRecord(r.code(), r.name(), r.type(), r.desc(), r.playerDesc(),
                r.blindDesc(), r.lashDesc(), numerator, denominator, divisor, damageCap, msgt,
                obvious, wake, colour, r.lineNumber());
    }

    private static ProjectionParseRecord withNumerator(ProjectionParseRecord r, String numerator) {
        return with(r, numerator, r.denominator(), r.divisor(), r.damageCap(), r.msgt(), r.obvious(),
                r.willWake(), r.colour());
    }

    private static ProjectionParseRecord withDenominator(ProjectionParseRecord r, String denominator) {
        return with(r, r.numerator(), denominator, r.divisor(), r.damageCap(), r.msgt(), r.obvious(),
                r.willWake(), r.colour());
    }

    private static ProjectionParseRecord withDivisor(ProjectionParseRecord r, String divisor) {
        return with(r, r.numerator(), r.denominator(), divisor, r.damageCap(), r.msgt(), r.obvious(),
                r.willWake(), r.colour());
    }

    private static ProjectionParseRecord withDamageCap(ProjectionParseRecord r, String damageCap) {
        return with(r, r.numerator(), r.denominator(), r.divisor(), damageCap, r.msgt(), r.obvious(),
                r.willWake(), r.colour());
    }

    private static ProjectionParseRecord withMsgt(ProjectionParseRecord r, String msgt) {
        return with(r, r.numerator(), r.denominator(), r.divisor(), r.damageCap(), msgt, r.obvious(),
                r.willWake(), r.colour());
    }

    private static ProjectionParseRecord withObvious(ProjectionParseRecord r, String obvious) {
        return with(r, r.numerator(), r.denominator(), r.divisor(), r.damageCap(), r.msgt(), obvious,
                r.willWake(), r.colour());
    }

    private static ProjectionParseRecord withWake(ProjectionParseRecord r, String wake) {
        return with(r, r.numerator(), r.denominator(), r.divisor(), r.damageCap(), r.msgt(), r.obvious(),
                wake, r.colour());
    }

    private static ProjectionParseRecord withColour(ProjectionParseRecord r, String colour) {
        return with(r, r.numerator(), r.denominator(), r.divisor(), r.damageCap(), r.msgt(), r.obvious(),
                r.willWake(), colour);
    }

    private static List<Projection> assemble(List<ProjectionParseRecord> records, List<String> errors) {
        return new ProjectionAssembler().assemble(records, errors, "test.txt");
    }

    /**
     * Assembles {@code records} and returns the exception that stops the load, failing the test if
     * the assembler returns instead.
     */
    private static DataFileParseException assembleAndStop(List<ProjectionParseRecord> records) {
        return assertThrows(DataFileParseException.class, () -> assemble(records, new ArrayList<>()));
    }

    private static long countOfType(List<Projection> projections, ProjectionType type) {
        return projections.stream().filter(p -> p.getType() == type).count();
    }

    /**
     * Reads a private field of a {@link Projection}, which has getters for only five of them.
     */
    private static Object field(Projection projection, String name) {
        try {
            java.lang.reflect.Field f = Projection.class.getDeclaredField(name);
            f.setAccessible(true);
            return f.get(projection);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Assembles the valid file with {@code broken} in place of the record at {@code index} and
     * returns the record that came out at that index.
     */
    private static Projection assembleWith(int index, ProjectionParseRecord broken) {
        List<ProjectionParseRecord> records = validFile();
        records.set(index, broken);
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(56, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
        return result.get(index);
    }

    // ---- Happy path -------------------------------------------------------

    /**
     * The shipped file has 56 {@code code:} lines: 25 elements, 7 environs and 24 monster. C
     * loads it without complaint, so the port must too.
     */
    @Test
    void realFileLoadsAllFiftySixProjections() throws IOException {
        ParseResult<Projection> result = new ProjectionReader().parseWithResults(REAL_FILE);

        assertTrue(result.errors().isEmpty(), result.errors().toString());
        List<Projection> items = result.items();
        assertEquals(56, items.size());
        assertEquals(25, countOfType(items, ProjectionType.PT_ELEMENT));
        assertEquals(7, countOfType(items, ProjectionType.PT_ENVIRONS));
        assertEquals(24, countOfType(items, ProjectionType.PT_MONSTER));
        // Only the element block is positional. Past it the file lists AWAY_EVIL before AWAY_SPIRIT,
        // the enum the other way round, and C does not check that part of the order.
        for (int i = 0; i < 25; i++) {
            assertEquals(ProjectionEnum.values()[i + 1], items.get(i).getProjection(),
                    "projection at index " + i);
        }
        java.util.Set<ProjectionEnum> seen = new java.util.HashSet<>();
        for (Projection p : items) {
            seen.add(p.getProjection());
        }
        assertEquals(56, seen.size());
        assertFalse(seen.contains(ProjectionEnum.PROJ_NONE));
    }

    /**
     * The ACID block in {@code projection.txt}: name, descriptions and type as the file states.
     */
    @Test
    void realFileAcidRecordMatchesTheDataFile() throws IOException {
        Projection acid = new ProjectionReader().parseWithResults(REAL_FILE).items().getFirst();

        assertEquals(ProjectionEnum.PROJ_ACID, acid.getProjection());
        assertEquals("acid", acid.getName());
        assertEquals("acid", acid.getPlayerDescription());
        assertEquals("acid", acid.getLashDescription());
        assertEquals(ProjectionType.PT_ELEMENT, acid.getType());
    }

    /**
     * LIGHT_WEAK is {@code type:environs} in the file; C keeps the string, the port must keep the
     * type as an enum rather than collapsing it to NONE.
     */
    @Test
    void realFileLightWeakIsEnvirons() throws IOException {
        List<Projection> items = new ProjectionReader().parseWithResults(REAL_FILE).items();
        Projection lightWeak = items.get(ProjectionEnum.PROJ_LIGHT_WEAK.ordinal() - 1);

        assertEquals(ProjectionEnum.PROJ_LIGHT_WEAK, lightWeak.getProjection());
        assertEquals(ProjectionType.PT_ENVIRONS, lightWeak.getType());
        assertEquals("weak light", lightWeak.getPlayerDescription());
    }

    /**
     * MON_CRUSH is the last block and {@code type:monster}.
     */
    @Test
    void realFileMonCrushIsMonster() throws IOException {
        Projection last = new ProjectionReader().parseWithResults(REAL_FILE).items().getLast();

        assertEquals(ProjectionEnum.PROJ_MON_CRUSH, last.getProjection());
        assertEquals(ProjectionType.PT_MONSTER, last.getType());
    }

    @Test
    void wellFormedHandBuiltFileLoadsWithNoErrors() {
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(validFile(), errors);

        assertEquals(56, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
    }

    /**
     * A record whose optional lines are all absent: C's zero-filled struct gives 0, false, colour 0
     * (dark) and message 0 ({@code GENERIC}), so it must load rather than fail on the {@code null}
     * strings the grammar gives an absent line.
     */
    @Test
    void absentOptionalFieldsLoadAsTheZeroFilledStruct() {
        Projection proj = assembleWith(MAKE_DOOR,
                with(validFile().get(MAKE_DOOR), null, null, null, null, null, null, null, ""));

        assertEquals(0, field(proj, "numerator"));
        assertEquals(0, field(proj, "denominator"));
        assertNull(field(proj, "diceDenominator"));
        assertEquals(0, field(proj, "divisor"));
        assertEquals(0, field(proj, "damageCap"));
        assertEquals(MessageType.MSG_GENERIC, field(proj, "msgt"));
        assertEquals(false, field(proj, "isObvious"));
        assertEquals(false, field(proj, "willWake"));
        assertEquals(ColourEnum.COLOUR_DARK, field(proj, "colour"));
    }

    // ---- Element position (parse_projection_code) -------------------------

    /**
     * C compares the first record's code with {@code element_names[0]} and quits on a mismatch, so
     * swapping ACID and ELEC stops the load at the first record.
     */
    @Test
    void swappedElementsStopTheLoadAtTheFirstMismatch() {
        List<ProjectionParseRecord> records = validFile();
        ProjectionParseRecord first = records.get(0);
        records.set(0, records.get(1));
        records.set(1, first);

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 2"), e.getMessage());
        assertTrue(e.getMessage().contains("Input ordinal: 2 is not equal to expected ordinal: 1"), e.getMessage());
    }

    /**
     * A non-element projection before the elements puts every element one position late, which C
     * rejects at index 0.
     */
    @Test
    void nonElementBeforeTheElementsStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.add(0, record("LIGHT_WEAK", "environs", 99));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 99"), e.getMessage());
    }

    /**
     * With ARROW missing, LIGHT_WEAK lands in position 25 where C expects {@code ARROW}.
     */
    @Test
    void missingElementStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.remove(ARROW);

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("Input ordinal: 26 is not equal to expected ordinal: 25"), e.getMessage());
    }

    /**
     * C compares the code of each of the first 25 records with {@code element_names[index]}. A code
     * that is not a projection at all cannot match, so an unknown code at position 1 stops the load
     * as C's mismatch does, and the message names the code.
     */
    @Test
    void unknownCodeInTheElementBlockStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.add(0, record("BOGUS", "element", 99));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 99"), e.getMessage());
        assertTrue(e.getMessage().contains("an illegal code value: BOGUS"), e.getMessage());
    }

    /**
     * Position 25 is the last one C checks ({@code index < ELEM_MAX} with {@code ELEM_MAX} 25 and
     * {@code index} from 0), so an unknown code there stops the load too.
     */
    @Test
    void unknownCodeAtTheLastElementPositionStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.set(ARROW, record("BOGUS", "element", 25));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 25"), e.getMessage());
        assertTrue(e.getMessage().contains("an illegal code value: BOGUS"), e.getMessage());
    }

    /**
     * The code is compared with {@code streq}, so a lower-case tag does not match
     * {@code element_names[0]} ({@code ACID}) and stops the load.
     */
    @Test
    void aLowerCaseCodeInTheElementBlockStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.set(0, record("acid", "element", 1));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("an illegal code value: acid"), e.getMessage());
    }

    /**
     * {@code NONE} and {@code MAX} are Java sentinels with no entry in {@code list-elements.h}, so
     * neither can equal an element name; both stop the load when they stand in an element position.
     */
    @Test
    void theSentinelCodesNoneAndMaxStopTheLoadInTheElementBlock() {
        for (String code : new String[]{"NONE", "MAX"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(POIS, record(code, "element", 5));

            DataFileParseException e = assembleAndStop(records);

            assertTrue(e.getMessage().contains("line: 5"), code + ": " + e.getMessage());
            assertTrue(e.getMessage().contains("expected ordinal: 5"), code + ": " + e.getMessage());
        }
    }

    /**
     * A {@code code:} with nothing after it is {@code PARSE_ERROR_MISSING_FIELD} in C's
     * {@code parser_parse}, a hard error at any position. Inside the element block it cannot equal
     * an element name either.
     */
    @Test
    void emptyCodeInTheElementBlockStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, record("", "element", 5));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 5"), e.getMessage());
    }

    /**
     * Past position 25 C never reads the code, but an empty {@code code:} never reaches
     * {@code parse_projection_code}: {@code parser_parse} quits first, so the load stops here too.
     */
    @Test
    void emptyCodeAfterTheElementBlockStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.add(record("", "monster", 99));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 99"), e.getMessage());
        assertTrue(e.getMessage().contains("has an empty code"), e.getMessage());
    }

    /**
     * C quits on the first bad line, so when the load holds a bad numerator at record 5 and a
     * swapped pair at records 11 and 12, the numerator, which comes first in the file, is the fault
     * the message names.
     */
    @Test
    void theFirstFaultInFileOrderIsTheOneNamed() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, withNumerator(records.get(POIS), "-3"));
        ProjectionParseRecord eleven = records.get(10);
        records.set(10, records.get(11));
        records.set(11, eleven);

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("line: 5"), e.getMessage());
        assertTrue(e.getMessage().contains("negative numerator"), e.getMessage());
    }

    // ---- Past the element block -------------------------------------------

    /**
     * C does not read the code of a record past the element block ({@code parse_projection_code}
     * checks it only while {@code index < ELEM_MAX}) and keeps the record, so a code that is not a
     * projection is stored, as {@code PROJ_NONE} because the port has no constant for it, with no
     * error.
     */
    @Test
    void unknownCodeAfterTheElementBlockIsStoredAsProjNoneWithNoError() {
        List<ProjectionParseRecord> records = validFile();
        records.add(record("BOGUS", "monster", 99));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(57, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(ProjectionEnum.PROJ_NONE, result.getLast().getProjection());
        assertEquals(ProjectionType.PT_MONSTER, result.getLast().getType());
    }

    /**
     * Past the element block a code that resolves is kept even when it repeats an element, because
     * C does not look at it.
     */
    @Test
    void aResolvableCodeAfterTheElementBlockIsKeptWhateverItIs() {
        List<ProjectionParseRecord> records = validFile();
        records.add(record("ACID", "monster", 99));

        List<Projection> result = assemble(records, new ArrayList<>());

        assertEquals(57, result.size());
        assertEquals(ProjectionEnum.PROJ_ACID, result.getLast().getProjection());
    }

    /**
     * Only the element block is positional, so two environs rows swapped after it load in the order
     * given, each keeping its own code.
     */
    @Test
    void theOrderBeyondTheElementBlockIsNotChecked() {
        List<ProjectionParseRecord> records = validFile();
        ProjectionParseRecord first = records.get(25);
        records.set(25, records.get(26));
        records.set(26, first);
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(56, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
        assertEquals(ProjectionEnum.PROJ_DARK_WEAK, result.get(25).getProjection());
        assertEquals(ProjectionEnum.PROJ_LIGHT_WEAK, result.get(26).getProjection());
    }

    // ---- Element count (finish_parse_projection) --------------------------

    /**
     * C counts every record whose {@code type} string is {@code element} and quits with "Too many
     * elements" past 25.
     */
    @Test
    void extraElementTypedRecordStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.add(record("LIGHT_WEAK", "element", 99));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("expected: 25 got: 26"), e.getMessage());
    }

    @Test
    void environsRowRetypedElementStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.set(MAKE_DOOR, record("MAKE_DOOR", "element", 31));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("got: 26"), e.getMessage());
    }

    /**
     * C quits with "Too few elements" when one element is not typed {@code element}.
     */
    @Test
    void elementRetypedEnvironsStopsTheLoad() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, record("POIS", "environs", 5));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("expected: 25 got: 24 dropped: 1"), e.getMessage());
    }

    /**
     * The type is a case-sensitive {@code streq(type, "element")} in C, so {@code Element} is not
     * an element, and the count comes up one short.
     */
    @Test
    void aTypeInTheWrongCaseIsNotAnElement() {
        List<ProjectionParseRecord> records = validFile();
        records.set(ARROW, record("ARROW", "Element", 25));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("expected: 25 got: 24"), e.getMessage());
    }

    @Test
    void emptyFileStopsTheLoadForHavingNoElements() {
        DataFileParseException e = assembleAndStop(new ArrayList<>());

        assertTrue(e.getMessage().contains("expected: 25 got: 0"), e.getMessage());
    }

    @Test
    void typeStringsMapToTheirEnumConstants() {
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(validFile(), errors);

        assertEquals(ProjectionType.PT_ELEMENT, result.get(0).getType());
        assertEquals(ProjectionType.PT_ELEMENT, result.get(24).getType());
        assertEquals(ProjectionType.PT_ENVIRONS, result.get(25).getType());
        assertEquals(ProjectionType.PT_ENVIRONS, result.get(31).getType());
        assertEquals(ProjectionType.PT_MONSTER, result.get(32).getType());
        assertEquals(ProjectionType.PT_MONSTER, result.get(55).getType());
    }

    // ---- A bad field stops the load, wherever the record is ---------------

    /**
     * Each case breaks one field of POIS (an element) or MAKE_DOOR (an environs row). C returns a
     * parse error at the bad line and the game quits, so the load stops either way and the message
     * names the line and the field. Nothing is skipped.
     */
    @Test
    void aBadFieldStopsTheLoadOnAnElementOrANonElementRecord() {
        String[][] cases = {
                // field, bad value
                {"numerator", "-3"}, {"numerator", "x"}, {"numerator", "-0"},
                {"divisor", "-1"}, {"divisor", "x"}, {"divisor", "-0"},
                {"damage cap", "-1"}, {"damage cap", "x"}, {"damage cap", "-0"},
                {"obvious", "-1"}, {"obvious", "x"}, {"obvious", "-0"},
                {"will wake", "-1"}, {"will wake", "x"}, {"will wake", "-0"},
        };
        for (int index : new int[]{POIS, MAKE_DOOR}) {
            for (String[] c : cases) {
                List<ProjectionParseRecord> records = validFile();
                ProjectionParseRecord base = records.get(index);
                records.set(index, switch (c[0]) {
                    case "numerator" -> withNumerator(base, c[1]);
                    case "divisor" -> withDivisor(base, c[1]);
                    case "damage cap" -> withDamageCap(base, c[1]);
                    case "obvious" -> withObvious(base, c[1]);
                    default -> withWake(base, c[1]);
                });
                List<String> errors = new ArrayList<>();

                DataFileParseException e = assertThrows(DataFileParseException.class,
                        () -> assemble(records, errors), c[0] + " " + c[1]);

                assertTrue(e.getMessage().contains("line: " + (index + 1)), c[0] + " " + c[1] + ": " + e.getMessage());
                assertTrue(e.getMessage().contains(c[0].equals("numerator") ? "numerator"
                        : c[0].equals("divisor") ? "divisor" : c[0].equals("damage cap") ? "damage cap"
                        : c[0].equals("obvious") ? "obvious" : "will wake"), c[0] + " " + c[1] + ": " + e.getMessage());
                assertTrue(errors.isEmpty(), "errors is never written to: " + errors);
            }
        }
    }

    /**
     * The four {@code uint} fields refuse a leading {@code -} before any digit is read, so the
     * message calls it negative: {@code parser_parse} tests {@code *tok == '-'}. {@code -0} is
     * refused too.
     */
    @Test
    void aMinusOnAUintFieldIsCalledNegative() {
        for (String value : new String[]{"-1", "-0"}) {
            ProjectionParseRecord base = validFile().get(MAKE_DOOR);
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withNumerator(base, value));

            assertTrue(assembleAndStop(records).getMessage().contains("illegal negative numerator value: " + value));

            records.set(MAKE_DOOR, withDivisor(base, value));
            assertTrue(assembleAndStop(records).getMessage().contains("illegal negative divisor value: " + value));

            records.set(MAKE_DOOR, withDamageCap(base, value));
            assertTrue(assembleAndStop(records).getMessage().contains("illegal negative damage cap value: " + value));

            records.set(MAKE_DOOR, withObvious(base, value));
            assertTrue(assembleAndStop(records).getMessage().contains("illegal negative obvious value: " + value));

            records.set(MAKE_DOOR, withWake(base, value));
            assertTrue(assembleAndStop(records).getMessage().contains("illegal negative will wake value: " + value));
        }
    }

    /**
     * A {@code uint} is read by {@code strtoul} in base 0, so a leading {@code 0} is octal:
     * {@code 010} is 8, {@code 0x10} is 16 and {@code 08} stops at the 8 and gives 0.
     */
    @Test
    void uintFieldsAreReadInBaseZero() {
        String[][] cases = {{"10", "10"}, {"010", "8"}, {"0x10", "16"}, {"08", "0"}, {"0", "0"}};
        for (String[] c : cases) {
            ProjectionParseRecord base = validFile().get(MAKE_DOOR);
            Projection proj = assembleWith(MAKE_DOOR,
                    withDamageCap(withDivisor(withNumerator(base, c[0]), c[0]), c[0]));

            int expected = Integer.parseInt(c[1]);
            assertEquals(expected, field(proj, "numerator"), "numerator " + c[0]);
            assertEquals(expected, field(proj, "divisor"), "divisor " + c[0]);
            assertEquals(expected, field(proj, "damageCap"), "damageCap " + c[0]);
        }
    }

    /**
     * {@code obvious} and {@code wake} store {@code (answer == 1)}: 1 is true, and 0 and 2 are
     * false but accepted. {@code 01} is octal 1, so it is true. Neither flag changes the other.
     */
    @Test
    void obviousAndWakeAreTrueOnlyForExactlyOne() {
        Object[][] cases = {{"0", false}, {"1", true}, {"2", false}, {"01", true}, {"10", false}};
        for (Object[] c : cases) {
            ProjectionParseRecord base = validFile().get(MAKE_DOOR);

            Projection both = assembleWith(MAKE_DOOR, withWake(withObvious(base, (String) c[0]), (String) c[0]));
            assertEquals(c[1], field(both, "isObvious"), "obvious " + c[0]);
            assertEquals(c[1], field(both, "willWake"), "wake " + c[0]);

            Projection onlyObvious = assembleWith(MAKE_DOOR, withWake(withObvious(base, (String) c[0]), "0"));
            assertEquals(c[1], field(onlyObvious, "isObvious"), "obvious " + c[0]);
            assertEquals(false, field(onlyObvious, "willWake"), "wake stays 0 with obvious " + c[0]);
        }
    }

    // ---- Denominator (parse_projection_denominator, parse_random) ----------

    /**
     * {@code parse_random} reads a plain figure with {@code strtoul} in base 10, so {@code 010} is
     * 10 where the {@code uint} fields would read octal 8, and a leading {@code -} negates it: the
     * base is {@code -5}.
     */
    @Test
    void aPlainDenominatorIsReadInBaseTenAndMayBeNegative() {
        String[][] cases = {{"5", "5"}, {"010", "10"}, {"0", "0"}, {"-5", "-5"}, {"-0", "0"},
                {"2147483647", "2147483647"}, {"-2147483647", "-2147483647"}};
        for (String[] c : cases) {
            Projection proj = assembleWith(MAKE_DOOR, withDenominator(validFile().get(MAKE_DOOR), c[0]));

            assertEquals(Integer.parseInt(c[1]), field(proj, "denominator"), "denominator " + c[0]);
            assertNull(field(proj, "diceDenominator"), "denominator " + c[0]);
        }
    }

    /**
     * A denominator that is not a plain figure is a dice value, kept as dice with the integer left
     * at 0. The dice grammar is {@code RandomReader}'s; one valid form from {@code projection.txt}
     * is enough here.
     */
    @Test
    void aDiceDenominatorIsKeptAsDice() {
        Projection proj = assembleWith(POIS, withDenominator(validFile().get(POIS), "2d4"));

        assertNotNull(field(proj, "diceDenominator"));
        assertEquals(0, field(proj, "denominator"));
    }

    /**
     * {@code parse_random} returns false for text that is neither a figure nor a dice value, which
     * is {@code PARSE_ERROR_NOT_RANDOM}, so the load stops.
     */
    @Test
    void aDenominatorThatIsNotARandomValueStopsTheLoad() {
        for (String value : new String[]{"garbage", "-", "d", "1d", "2d4x"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(POIS, withDenominator(records.get(POIS), value));

            DataFileParseException e = assembleAndStop(records);

            assertTrue(e.getMessage().contains("line: 5"), value + ": " + e.getMessage());
            assertTrue(e.getMessage().contains("denominator"), value + ": " + e.getMessage());
        }
    }

    /**
     * Accepted deviation (decided 261029, recorded under Projection in
     * {@code docs/Accepted_Deviations.md}): C's {@code parse_random} refuses a figure whose
     * {@code strtoul} value is above {@code INT_MAX}, so {@code 2147483648} stops the load in C.
     * Java clamps to the {@code int} range and loads it. This pins the Java side, so a change that
     * makes the assembler refuse it will fail here and prompt the entry to be revisited.
     */
    @Test
    void aDigitOnlyDenominatorAboveIntMaxIsClampedWhereCRefusesIt() {
        String[][] cases = {{"2147483648", "2147483647"}, {"99999999999", "2147483647"},
                {"-2147483648", "-2147483648"}, {"-99999999999", "-2147483648"}};
        for (String[] c : cases) {
            Projection proj = assembleWith(MAKE_DOOR, withDenominator(validFile().get(MAKE_DOOR), c[0]));

            assertEquals(Integer.parseInt(c[1]), field(proj, "denominator"), "denominator " + c[0]);
        }
    }

    // ---- Colour (parse_projection_color) ----------------------------------

    /**
     * {@code color_text_to_attr} and {@code color_char_to_attr} both default to white, so an
     * unrecognised colour, name or character, is not an error.
     */
    @Test
    void unknownColourFallsBackToWhiteWithoutAnError() {
        for (String colour : new String[]{"Nonsense", "q", "Shade"}) {
            Projection proj = assembleWith(POIS, withColour(validFile().get(POIS), colour));

            assertEquals(ColourEnum.COLOUR_WHITE, field(proj, "colour"), "colour '" + colour + "'");
        }
    }

    /**
     * Names are matched with {@code my_stricmp}, so case does not matter: {@code Light Dark} is
     * {@code color_table} slot 8. A single character is matched with {@code ==}, so case does
     * matter: {@code D} is Light Dark and {@code d} is Dark. A space or an empty value is slot 0,
     * dark, as is a record with no colour.
     */
    @Test
    void colourNamesAreCaseInsensitiveAndCharactersAreNot() {
        Object[][] cases = {
                {"light dark", ColourEnum.COLOUR_LIGHT_DARK}, {"LIGHT DARK", ColourEnum.COLOUR_LIGHT_DARK},
                {"Light Dark", ColourEnum.COLOUR_LIGHT_DARK}, {"D", ColourEnum.COLOUR_LIGHT_DARK},
                {"d", ColourEnum.COLOUR_DARK}, {"w", ColourEnum.COLOUR_WHITE},
                {"W", ColourEnum.COLOUR_LIGHT_SLATE}, {"o", ColourEnum.COLOUR_ORANGE},
                {"", ColourEnum.COLOUR_DARK}, {" ", ColourEnum.COLOUR_DARK},
        };
        for (Object[] c : cases) {
            Projection proj = assembleWith(POIS, withColour(validFile().get(POIS), (String) c[0]));

            assertEquals(c[1], field(proj, "colour"), "colour '" + c[0] + "'");
        }
    }

    // ---- Message type (message_lookup_by_name) ----------------------------

    /**
     * {@code message_lookup_by_name} accepts a number below {@code MSG_MAX} (153), counting from
     * {@code GENERIC} at 0 and {@code BIRTH} at 1, so 152 ({@code SCRAMBLE}) is the last number.
     * {@code MessageType} carries {@code MSG_NONE} at ordinal 0, which the lookup steps over.
     * Trailing spaces are allowed ({@code contains_only_spaces}) and {@code strtoul} skips leading
     * ones.
     */
    @Test
    void numericMsgtCountsFromGenericAndStopsBelowMsgMax() {
        Object[][] cases = {{"0", MessageType.MSG_GENERIC}, {"1", MessageType.MSG_BIRTH},
                {"152", MessageType.MSG_SCRAMBLE}, {"1 ", MessageType.MSG_BIRTH},
                {" 1", MessageType.MSG_BIRTH}, {"-0", MessageType.MSG_GENERIC}};
        for (Object[] c : cases) {
            Projection proj = assembleWith(MAKE_DOOR, withMsgt(validFile().get(MAKE_DOOR), (String) c[0]));

            assertEquals(c[1], field(proj, "msgt"), "msgt '" + c[0] + "'");
        }
    }

    /**
     * 153 is {@code MSG_MAX}, which {@code number < MSG_MAX} refuses, and {@code -1} wraps in
     * {@code strtoul} to a huge value that is refused too. Both are {@code
     * PARSE_ERROR_INVALID_MESSAGE}, so the load stops.
     */
    @Test
    void numericMsgtAtOrAboveMsgMaxStopsTheLoad() {
        for (String value : new String[]{"153", "154", "-1"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withMsgt(records.get(MAKE_DOOR), value));

            DataFileParseException e = assembleAndStop(records);

            assertTrue(e.getMessage().contains("line: 31"), value + ": " + e.getMessage());
            assertTrue(e.getMessage().contains("an illegal message value: " + value), value + ": " + e.getMessage());
        }
    }

    /**
     * Names are matched case-insensitively after a {@code MSG_} prefix, as {@code my_stricmp} does.
     */
    @Test
    void msgtNamesAreCaseInsensitive() {
        Object[][] cases = {{"BR_ACID", MessageType.MSG_BR_ACID}, {"br_acid", MessageType.MSG_BR_ACID},
                {"Br_Acid", MessageType.MSG_BR_ACID}, {"GENERIC", MessageType.MSG_GENERIC}};
        for (Object[] c : cases) {
            Projection proj = assembleWith(MAKE_DOOR, withMsgt(validFile().get(MAKE_DOOR), (String) c[0]));

            assertEquals(c[1], field(proj, "msgt"), "msgt '" + c[0] + "'");
        }
    }

    /**
     * A name with no match is {@code -1} from {@code message_lookup_by_name} and
     * {@code PARSE_ERROR_INVALID_MESSAGE} in the parser, so the load stops. {@code NONE} is the
     * Java-only sentinel with no C name, so it is unknown too.
     */
    @Test
    void anUnknownMsgtNameStopsTheLoad() {
        for (String value : new String[]{"NOSUCHMSG", "NONE"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withMsgt(records.get(MAKE_DOOR), value));

            DataFileParseException e = assembleAndStop(records);

            assertTrue(e.getMessage().contains("an illegal message value: " + value), value + ": " + e.getMessage());
        }
    }

    // ---- Fault order inside a record ---------------------------------------

    /**
     * Accepted ordering difference: C reads lines top to bottom and the assembler checks the
     * numerator before the {@code msgt}, whatever order the lines came in. A record with both
     * faults stops the load in both, and only the fault named can differ.
     */
    @Test
    void aRecordWithTwoFaultsNamesTheNumeratorBeforeTheMsgt() {
        List<ProjectionParseRecord> records = validFile();
        records.set(MAKE_DOOR, withMsgt(withNumerator(records.get(MAKE_DOOR), "-1"), "NOSUCHMSG"));

        DataFileParseException e = assembleAndStop(records);

        assertTrue(e.getMessage().contains("negative numerator"), e.getMessage());
    }
}
