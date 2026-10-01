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

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionAssembler;
import uk.co.jackoftradesltd.backend.parser.projection.ProjectionParseRecord;
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
 * {@code message_lookup_by_name()} in {@code message.c}, and the shipped
 * {@code lib/gamedata/projection.txt}.
 *
 * <p>Most tests are driven from hand-built {@link ProjectionParseRecord}s so that one field can be
 * varied at a time; {@link #realFileLoadsAllFiftySixProjections()} runs the shipped file through
 * the full reader.
 *
 * <p>The row positions used below follow the file: records 1-25 are the elements, so index 4 is
 * {@code POIS} (position 5) and index 24 is {@code ARROW}; index 30 is {@code MAKE_DOOR}, an
 * environs row.
 *
 * <p>Test class ProjectionAssemblerTest coded on 261001, commented in full on 261001.
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
                "1", "1", "1", "1600", "", "1", "0", "White", line);
    }

    /**
     * Every projection of the shipped file in order, with types taken from the constants'
     * positions: the first 25 are elements, the next 7 environs, the remaining 24 monster.
     */
    private static List<ProjectionParseRecord> validFile() {
        List<ProjectionParseRecord> records = new ArrayList<>();
        int line = 0;
        for (ProjectionEnum e : ProjectionEnum.values()) {
            if (e == ProjectionEnum.PROJ_NONE) continue;
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
        return new ProjectionAssembler().assemble(records, errors);
    }

    private static long countOfType(List<Projection> projections, ProjectionType type) {
        return projections.stream().filter(p -> p.getType() == type).count();
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
     * and message 0, so it must load rather than fail on the empty strings.
     */
    @Test
    void absentOptionalFieldsLoadAsDefaults() {
        List<ProjectionParseRecord> records = validFile();
        records.set(MAKE_DOOR, with(records.get(MAKE_DOOR), "", "", "", "", "", "", "", ""));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(56, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
    }

    @Test
    void diceDenominatorIsAccepted() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, withDenominator(records.get(POIS), "2d4"));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(56, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
    }

    // ---- Element position (parse_projection_code) -------------------------

    /**
     * C compares the first record's code with {@code element_names[0]} and fails the parse on a
     * mismatch, so swapping ACID and ELEC is fatal at the first record.
     */
    @Test
    void swappedElementsThrowAtTheFirstMismatch() {
        List<ProjectionParseRecord> records = validFile();
        ProjectionParseRecord first = records.get(0);
        records.set(0, records.get(1));
        records.set(1, first);

        ParseCancellationException e = assertThrows(ParseCancellationException.class,
                () -> assemble(records, new ArrayList<>()));
        assertTrue(e.getMessage().contains("line: 2"), e.getMessage());
    }

    /**
     * A non-element projection before the elements puts every element one position late, which C
     * rejects at index 0.
     */
    @Test
    void nonElementBeforeTheElementsThrows() {
        List<ProjectionParseRecord> records = validFile();
        records.add(0, record("LIGHT_WEAK", "environs", 99));

        assertThrows(ParseCancellationException.class, () -> assemble(records, new ArrayList<>()));
    }

    /**
     * With ARROW missing, LIGHT_WEAK lands in position 25 where C expects {@code ARROW}.
     */
    @Test
    void missingElementThrows() {
        List<ProjectionParseRecord> records = validFile();
        records.remove(ARROW);

        assertThrows(ParseCancellationException.class, () -> assemble(records, new ArrayList<>()));
    }

    /**
     * C's {@code index} counts every record, so an unknown code at position 1 shifts ACID to
     * position 2 and the parse fails there.
     */
    @Test
    void unknownCodeInTheElementBlockStillCountsAsAPosition() {
        List<ProjectionParseRecord> records = validFile();
        records.add(0, record("BOGUS", "element", 99));

        assertThrows(ParseCancellationException.class, () -> assemble(records, new ArrayList<>()));
    }

    /**
     * Errors gathered before the fatal mismatch ride out in the exception message, since the
     * reader discards the {@code errors} list when it catches the exception.
     */
    @Test
    void positionExceptionCarriesEarlierErrors() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, withNumerator(records.get(POIS), "-3"));
        ProjectionParseRecord eleven = records.get(10);
        records.set(10, records.get(11));
        records.set(11, eleven);

        ParseCancellationException e = assertThrows(ParseCancellationException.class,
                () -> assemble(records, new ArrayList<>()));
        assertTrue(e.getMessage().contains("negative numerator"), e.getMessage());
    }

    // ---- Element count (finish_parse_projection) --------------------------

    /**
     * C counts every record whose {@code type} string is {@code element} and quits with "Too many
     * elements" past 25.
     */
    @Test
    void extraElementTypedRecordIsRejected() {
        List<ProjectionParseRecord> records = validFile();
        records.add(record("LIGHT_WEAK", "element", 99));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.getLast().contains("expected 25 got 26"), errors.toString());
    }

    @Test
    void environsRowRetypedElementIsRejected() {
        List<ProjectionParseRecord> records = validFile();
        records.set(MAKE_DOOR, record("MAKE_DOOR", "element", 31));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.getLast().contains("got 26"), errors.toString());
    }

    /**
     * C quits with "Too few elements" when one element is not typed {@code element}.
     */
    @Test
    void elementRetypedEnvironsIsRejected() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, record("POIS", "environs", 5));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.getLast().contains("expected 25 got 24 dropped 1"), errors.toString());
    }

    /**
     * Dropped element rows do not count. Each case breaks one field of POIS; the row is skipped,
     * the survivors number 24, and the file is rejected. In C each of these is a fatal parse error.
     */
    @Test
    void droppedElementRecordFailsTheFile() {
        List<ProjectionParseRecord> base = validFile();
        List<ProjectionParseRecord> bad = new ArrayList<>();
        bad.add(withNumerator(base.get(POIS), "-3"));
        bad.add(withNumerator(base.get(POIS), "x"));
        bad.add(withDenominator(base.get(POIS), "garbage"));
        bad.add(withDivisor(base.get(POIS), "-1"));
        bad.add(withDivisor(base.get(POIS), "x"));
        bad.add(withDamageCap(base.get(POIS), "-1"));
        bad.add(withDamageCap(base.get(POIS), "x"));
        bad.add(withMsgt(base.get(POIS), "NOSUCHMSG"));
        bad.add(withObvious(base.get(POIS), "x"));
        bad.add(withObvious(base.get(POIS), "-1"));
        bad.add(withWake(base.get(POIS), "x"));
        bad.add(withWake(base.get(POIS), "-1"));
        bad.add(record("BOGUS", "element", 5));

        for (ProjectionParseRecord broken : bad) {
            List<ProjectionParseRecord> records = validFile();
            records.set(POIS, broken);
            List<String> errors = new ArrayList<>();

            List<Projection> result = assemble(records, errors);

            assertTrue(result.isEmpty(), broken.toString());
            assertTrue(errors.getLast().contains("expected 25 got 24"), errors.toString());
        }
    }

    // ---- Soft errors on non-element rows ----------------------------------

    /**
     * A bad field on a non-element row is reported and the row skipped; the rest of the file still
     * loads. (C would abort; this is the port's soft-error contract.)
     */
    @Test
    void badFieldOnNonElementRowSkipsOnlyThatRow() {
        List<ProjectionParseRecord> records = validFile();
        records.set(MAKE_DOOR, withNumerator(records.get(MAKE_DOOR), "-3"));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(55, result.size());
        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("negative numerator"), errors.toString());
    }

    /**
     * An unknown code after the element block is reported and skipped; C would not look the code
     * up at all beyond index 24, so nothing in C corresponds to the error.
     */
    @Test
    void unknownCodeAfterTheElementBlockIsReportedAndSkipped() {
        List<ProjectionParseRecord> records = validFile();
        records.add(record("BOGUS", "monster", 99));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(56, result.size());
        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("Unknown projection code"), errors.toString());
    }

    // ---- Colour (parse_projection_color) ----------------------------------

    /**
     * {@code color_text_to_attr} and {@code color_char_to_attr} both default to white, so an
     * unrecognised colour is not an error.
     */
    @Test
    void unknownColourFallsBackToWhiteWithoutAnError() {
        List<ProjectionParseRecord> records = validFile();
        records.set(POIS, withColour(records.get(POIS), "Nonsense"));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(56, result.size());
        assertTrue(errors.isEmpty(), errors.toString());
    }

    @Test
    void colourNamesAreCaseInsensitiveAndEmptyOrBlankIsAccepted() {
        for (String colour : new String[]{"light dark", "LIGHT DARK", "Light Dark", "", " ", "w", "o"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(POIS, withColour(records.get(POIS), colour));
            List<String> errors = new ArrayList<>();

            assertEquals(56, assemble(records, errors).size(), "colour '" + colour + "'");
            assertTrue(errors.isEmpty(), errors.toString());
        }
    }

    // ---- Message type (message_lookup_by_name) ----------------------------

    /**
     * {@code message_lookup_by_name} accepts a number only when it is below {@code MSG_MAX}, so the
     * last valid index is {@code MSG_MAX - 1}.
     */
    @Test
    void numericMsgtBoundsFollowMsgMax() {
        int max = MessageType.MSG_MAX.ordinal();
        String[] accepted = {"0", String.valueOf(max - 1), "5 ", " 5"};
        String[] rejected = {String.valueOf(max), String.valueOf(max + 1),
                String.valueOf(MessageType.values().length), "-1"};

        for (String value : accepted) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withMsgt(records.get(MAKE_DOOR), value));
            List<String> errors = new ArrayList<>();

            assertEquals(56, assemble(records, errors).size(), "msgt '" + value + "'");
            assertTrue(errors.isEmpty(), "msgt '" + value + "': " + errors);
        }
        for (String value : rejected) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withMsgt(records.get(MAKE_DOOR), value));
            List<String> errors = new ArrayList<>();

            assertEquals(55, assemble(records, errors).size(), "msgt '" + value + "'");
            assertEquals(1, errors.size(), "msgt '" + value + "': " + errors);
        }
    }

    /**
     * Names are matched case-insensitively after a {@code MSG_} prefix, as {@code my_stricmp} does.
     */
    @Test
    void msgtNamesAreCaseInsensitive() {
        for (String value : new String[]{"BR_ACID", "br_acid", "Br_Acid", "GENERIC"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withMsgt(records.get(MAKE_DOOR), value));
            List<String> errors = new ArrayList<>();

            assertEquals(56, assemble(records, errors).size(), "msgt '" + value + "'");
            assertTrue(errors.isEmpty(), "msgt '" + value + "': " + errors);
        }
    }

    @Test
    void unknownMsgtNameIsReportedAndSkipped() {
        List<ProjectionParseRecord> records = validFile();
        records.set(MAKE_DOOR, withMsgt(records.get(MAKE_DOOR), "NOSUCHMSG"));
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(records, errors);

        assertEquals(55, result.size());
        assertTrue(errors.getFirst().contains("Unknown projection message type"), errors.toString());
    }

    // ---- uint fields (parser_getuint) -------------------------------------

    /**
     * {@code parser_getuint} rejects negatives and non-numbers for the numeric fields.
     */
    @Test
    void negativeOrNonNumericUintFieldsSkipTheRow() {
        List<ProjectionParseRecord> base = validFile();
        List<ProjectionParseRecord> bad = new ArrayList<>();
        bad.add(withNumerator(base.get(MAKE_DOOR), "-1"));
        bad.add(withNumerator(base.get(MAKE_DOOR), "x"));
        bad.add(withDivisor(base.get(MAKE_DOOR), "-1"));
        bad.add(withDivisor(base.get(MAKE_DOOR), "x"));
        bad.add(withDamageCap(base.get(MAKE_DOOR), "-1"));
        bad.add(withDamageCap(base.get(MAKE_DOOR), "x"));
        bad.add(withObvious(base.get(MAKE_DOOR), "-1"));
        bad.add(withWake(base.get(MAKE_DOOR), "-1"));
        bad.add(withObvious(base.get(MAKE_DOOR), "x"));
        bad.add(withWake(base.get(MAKE_DOOR), "x"));

        for (ProjectionParseRecord broken : bad) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, broken);
            List<String> errors = new ArrayList<>();

            assertEquals(55, assemble(records, errors).size(), broken.toString());
            assertEquals(1, errors.size(), errors.toString());
        }
    }

    /**
     * {@code obvious} and {@code wake} are true only for exactly 1; 0 and 2 are both false and
     * both accepted, as in {@code parse_projection_obvious} and {@code parse_projection_wake}.
     */
    @Test
    void booleanFieldsAcceptAnyNonNegativeNumber() {
        for (String value : new String[]{"0", "1", "2"}) {
            List<ProjectionParseRecord> records = validFile();
            records.set(MAKE_DOOR, withWake(withObvious(records.get(MAKE_DOOR), value), value));
            List<String> errors = new ArrayList<>();

            assertEquals(56, assemble(records, errors).size(), "value " + value);
            assertTrue(errors.isEmpty(), errors.toString());
        }
    }

    // ---- Types through the assembler --------------------------------------

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

    @Test
    void emptyFileIsRejectedForHavingNoElements() {
        List<String> errors = new ArrayList<>();

        List<Projection> result = assemble(new ArrayList<>(), errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.getLast().contains("expected 25 got 0"), errors.toString());
    }
}
