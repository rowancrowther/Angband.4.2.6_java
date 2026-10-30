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
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleAssembler;
import uk.co.jackoftradesltd.backend.parser.visuals.VisualsCycleParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.frontend.colour.ColourCycle;
import uk.co.jackoftradesltd.frontend.colour.VisualsCycler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link VisualsCycleAssembler}.
 *
 * <p>These construct {@link VisualsCycleParseRecord}s directly - no grammar, no reader - so records
 * the grammar would never produce (an empty colour, a block with no colours) can still reach the
 * assembler. End-to-end cases live in {@code VisualsReaderTest}.
 *
 * <p>The expected values are worked out from C, not from the Java:
 * <ul>
 *   <li>colour resolution from {@code color_char_to_attr} in {@code z-color.c}: a space is
 *       {@code COLOUR_DARK}, an unknown character is {@code COLOUR_WHITE}, and the search is
 *       case-sensitive ({@code 'W'} is Light Slate);</li>
 *   <li>the empty colour from {@code visuals_parse_cycle_color}'s {@code strlen} check
 *       ({@code PARSE_ERROR_INVALID_COLOR}), and the 33rd colour from its {@code max_steps} check
 *       against {@code VISUALS_STEPS_MAX} (32, {@code PARSE_ERROR_TOO_MANY_ENTRIES});</li>
 *   <li>the 513th {@code cycle:} line from {@code visuals_parse_cycle}'s {@code cycles_index}
 *       check against {@code max_cycles} ({@code 8 * 64 + 1}, starting at 1);</li>
 *   <li>the silent drop of a ninth group ({@code VISUALS_GROUPS_MAX}, 8) and a 65th new name
 *       ({@code VISUALS_CYCLES_MAX}, 64) from {@code visuals_parse_cycle}'s slot searches, which
 *       still let a repeated name replace its slot;</li>
 *   <li>the empty-file and empty-cycle failures from {@code visuals_parse_context_convert}:
 *       {@code visuals_cycler_new} and {@code visuals_color_cycle_new} both return NULL for a
 *       count of 0, and only cycles still in a slot are copied;</li>
 *   <li>the stop-the-load policy: C's first parser error ends the whole file.</li>
 * </ul>
 *
 * <p>Class VisualsCycleAssemblerTest coded on 261030, commented in full on 261030.
 *
 * @author Rowan Crowther
 */
class VisualsCycleAssemblerTest {

    private static final String FILE = "visuals.txt";

    private static VisualsCycleParseRecord record(String group, String name, String... colours) {
        return new VisualsCycleParseRecord(group, name, List.of(colours), 7);
    }

    private static VisualsCycleParseRecord steps(String group, String name, int count) {
        return new VisualsCycleParseRecord(group, name, Collections.nCopies(count, "r"), 7);
    }

    private static VisualsCycler assemble(List<VisualsCycleParseRecord> records) {
        List<String> errors = new ArrayList<>();
        List<VisualsCycler> result = new VisualsCycleAssembler().assemble(records, errors, FILE);
        assertEquals(1, result.size());
        assertTrue(errors.isEmpty(), "every error is fatal, none is reported into the list");
        return result.getFirst();
    }

    private static void refused(List<VisualsCycleParseRecord> records) {
        List<String> errors = new ArrayList<>();
        assertThrows(DataFileParseException.class,
                () -> new VisualsCycleAssembler().assemble(records, errors, FILE));
        assertTrue(errors.isEmpty(), "a load-ending error is thrown, never added to the error list");
    }

    private static List<ColourEnum> colours(ColourCycle cycle) {
        List<ColourEnum> out = new ArrayList<>();
        for (int i = 0; i < cycle.getSize(); i++) {
            out.add(cycle.getColour(i));
        }
        return out;
    }

    private static List<ColourEnum> colours(VisualsCycler cycler, String group, String name) {
        return colours(cycler.getByGroup(group).get(name));
    }

    // ---- Ordinary path -----------------------------------------------------

    @Test
    void twoGroupsAssembleWithTheirStepsInFileOrder() {
        VisualsCycler cycler = assemble(List.of(
                record("fancy", "fire", "r", "o", "y"),
                record("flicker", "acid", "s", "g"),
                record("fancy", "cold", "w", "B")));

        assertEquals(2, cycler.getKeys().size());
        assertEquals(List.of(ColourEnum.COLOUR_RED, ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                colours(cycler, "fancy", "fire"));
        assertEquals(List.of(ColourEnum.COLOUR_WHITE, ColourEnum.COLOUR_LIGHT_BLUE),
                colours(cycler, "fancy", "cold"));
        assertEquals(List.of(ColourEnum.COLOUR_SLATE, ColourEnum.COLOUR_GREEN),
                colours(cycler, "flicker", "acid"));
    }

    @Test
    void aRepeatedNameReplacesTheEarlierCycle() {
        // visuals_parse_cycle points the matching slot at the new cycle.
        VisualsCycler cycler = assemble(List.of(
                record("fancy", "fire", "r"),
                record("fancy", "fire", "o", "y")));

        assertEquals(1, cycler.getByGroup("fancy").size());
        assertEquals(List.of(ColourEnum.COLOUR_ORANGE, ColourEnum.COLOUR_YELLOW),
                colours(cycler, "fancy", "fire"));
    }

    @Test
    void theSameNameInTwoGroupsIsTwoCycles() {
        VisualsCycler cycler = assemble(List.of(
                record("fancy", "fire", "r"),
                record("flicker", "fire", "b")));

        assertEquals(List.of(ColourEnum.COLOUR_RED), colours(cycler, "fancy", "fire"));
        assertEquals(List.of(ColourEnum.COLOUR_BLUE), colours(cycler, "flicker", "fire"));
    }

    // ---- Colours -----------------------------------------------------------

    @ParameterizedTest
    @CsvSource(quoteCharacter = '\'', value = {
            "d,   COLOUR_DARK",
            "' ', COLOUR_DARK",          // color_char_to_attr's early return
            "w,   COLOUR_WHITE",
            "W,   COLOUR_LIGHT_SLATE",   // case-sensitive
            "v,   COLOUR_VIOLET",
            "V,   COLOUR_LIGHT_VIOLET",
            "Z,   COLOUR_DEEP_LIGHT_BLUE",
            "x,   COLOUR_WHITE",         // no match: "we assume white"
            "#,   COLOUR_WHITE"
    })
    void aCodeResolvesAsColorCharToAttr(String code, ColourEnum expected) {
        assertEquals(List.of(expected), colours(assemble(List.of(record("fancy", "c", code))), "fancy", "c"));
    }

    @Test
    void anEmptyColourEndsTheLoad() {
        refused(List.of(record("fancy", "c", "r", "")));
    }

    @Test
    void thirtyTwoColoursAreAccepted() {
        assertEquals(32, assemble(List.of(steps("fancy", "c", 32))).getByGroup("fancy").get("c").getSize());
    }

    @Test
    void aThirtyThirdColourEndsTheLoad() {
        refused(List.of(steps("fancy", "c", 33)));
    }

    // ---- Record count ------------------------------------------------------

    @Test
    void fiveHundredAndTwelveCycleLinesAreAccepted() {
        // Repeats of one name still use up a cycles_index each in C.
        List<VisualsCycleParseRecord> records = new ArrayList<>(Collections.nCopies(512, record("fancy", "c", "r")));
        assertEquals(1, assemble(records).getByGroup("fancy").size());
    }

    @Test
    void theFiveHundredAndThirteenthCycleLineEndsTheLoad() {
        refused(new ArrayList<>(Collections.nCopies(513, record("fancy", "c", "r"))));
    }

    // ---- Group and name limits ----------------------------------------------

    private static List<VisualsCycleParseRecord> eightGroups() {
        List<VisualsCycleParseRecord> records = new ArrayList<>();
        for (int g = 0; g < 8; g++) {
            records.add(record("g" + g, "c", "r"));
        }
        return records;
    }

    @Test
    void aNinthGroupIsDroppedSilently() {
        List<VisualsCycleParseRecord> records = eightGroups();
        records.add(record("g8", "c", "b"));

        VisualsCycler cycler = assemble(records);
        assertEquals(8, cycler.getKeys().size());
        assertNull(cycler.getByGroup("g8"));
    }

    @Test
    void anExistingGroupStillTakesNewNamesOnceEightAreFull() {
        List<VisualsCycleParseRecord> records = eightGroups();
        records.add(record("g3", "extra", "b"));

        assertEquals(List.of(ColourEnum.COLOUR_BLUE), colours(assemble(records), "g3", "extra"));
    }

    private static List<VisualsCycleParseRecord> sixtyFourNames() {
        List<VisualsCycleParseRecord> records = new ArrayList<>();
        for (int n = 0; n < 64; n++) {
            records.add(record("fancy", "n" + n, "r"));
        }
        return records;
    }

    @Test
    void aSixtyFifthNewNameIsDroppedSilently() {
        List<VisualsCycleParseRecord> records = sixtyFourNames();
        records.add(record("fancy", "n64", "b"));

        Map<String, ColourCycle> fancy = assemble(records).getByGroup("fancy");
        assertEquals(64, fancy.size());
        assertFalse(fancy.containsKey("n64"));
    }

    @Test
    void aRepeatedNameStillReplacesInAFullGroup() {
        List<VisualsCycleParseRecord> records = sixtyFourNames();
        records.add(record("fancy", "n10", "b", "g"));

        VisualsCycler cycler = assemble(records);
        assertEquals(64, cycler.getByGroup("fancy").size());
        assertEquals(List.of(ColourEnum.COLOUR_BLUE, ColourEnum.COLOUR_GREEN), colours(cycler, "fancy", "n10"));
    }

    // ---- Empty file and empty cycles ----------------------------------------

    @Test
    void noRecordsEndTheLoad() {
        // visuals_cycler_new(0) returns NULL.
        refused(List.of());
    }

    @Test
    void aLoneEmptyCycleEndsTheLoad() {
        refused(List.of(record("fancy", "c")));
    }

    @Test
    void anEmptyCycleThatReplacesAGoodOneEndsTheLoad() {
        refused(List.of(record("fancy", "c", "r"), record("fancy", "c")));
    }

    @Test
    void anEmptyCycleReplacedByALaterBlockIsHarmless() {
        VisualsCycler cycler = assemble(List.of(record("fancy", "c"), record("fancy", "c", "g")));
        assertEquals(List.of(ColourEnum.COLOUR_GREEN), colours(cycler, "fancy", "c"));
    }

    @Test
    void anEmptyCycleInADroppedNinthGroupIsHarmless() {
        List<VisualsCycleParseRecord> records = eightGroups();
        records.add(record("g8", "c"));
        assertEquals(8, assemble(records).getKeys().size());
    }

    @Test
    void anEmptyCycleAsADroppedSixtyFifthNameIsHarmless() {
        List<VisualsCycleParseRecord> records = sixtyFourNames();
        records.add(record("fancy", "n64"));
        assertEquals(64, assemble(records).getByGroup("fancy").size());
    }

    @Test
    void anEmptyCycleStillClaimsAGroupSlot() {
        // Seven groups, then an empty cycle opens an eighth (later filled), so a ninth is dropped.
        List<VisualsCycleParseRecord> records = new ArrayList<>();
        for (int g = 0; g < 7; g++) {
            records.add(record("g" + g, "c", "r"));
        }
        records.add(record("g7", "c"));
        records.add(record("g8", "c", "b"));
        records.add(record("g7", "c", "y"));

        VisualsCycler cycler = assemble(records);
        assertNull(cycler.getByGroup("g8"));
        assertEquals(List.of(ColourEnum.COLOUR_YELLOW), colours(cycler, "g7", "c"));
    }
}
