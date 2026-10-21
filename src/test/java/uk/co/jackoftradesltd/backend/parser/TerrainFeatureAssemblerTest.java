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
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureAssembler;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureParseRecord;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TerrainFeatureAssembler}, driven from hand-built
 * {@link TerrainFeatureParseRecord}s rather than through the grammar.
 *
 * <p>The expected values come from the C original, {@code init.c}: the {@code parse_feat_*}
 * handlers and {@code finish_parse_feat()}, and {@code lookup_feat_code()} in {@code cave.c}. The
 * main point of the suite is the boundary the lexer cannot guard: {@code FEAT_MAX} and
 * {@code TF_MAX} exist as Java constants, but C's lookup tables are built from
 * {@code list-terrain.h} and {@code list-terrain-flags.h} and have no {@code MAX} entry, so
 * {@code code:MAX}, {@code mimic:MAX} and {@code flags:MAX} must be refused while {@code NONE},
 * which is a real entry in every table, stays valid.
 *
 * <p>{@code TerrainReaderTest} covers the assembler as the grammar reaches it; here the records are
 * built directly so each branch is hit once and the resolved {@link Feature} can be inspected
 * through its debug string, since {@code Feature} exposes no getters for priority, digging or the
 * look strings.
 *
 * @author Rowan Crowther
 */
class TerrainFeatureAssemblerTest {

    /**
     * A record with only the three mandatory header directives set, as the grammar leaves every
     * optional directive: the empty string, or an empty list.
     */
    private static TerrainFeatureParseRecord record(String code) {
        return full(code, "", "", "", List.of(), "w", "", "", "", 1);
    }

    private static TerrainFeatureParseRecord full(String code, String mimic, String priority,
                                                  String dig, List<String> flags, String colour,
                                                  String lookPrefix, String lookInPreposition,
                                                  String resistFlag, int line) {
        return new TerrainFeatureParseRecord(code, "name of " + code, "", mimic, priority, dig,
                flags, ".", colour, "", "", "", "", "", lookPrefix, lookInPreposition,
                resistFlag, line);
    }

    private static List<Feature> assemble(List<TerrainFeatureParseRecord> records,
                                          List<String> errors) {
        return new TerrainFeatureAssembler().assemble(records, errors, "test.txt");
    }

    private static List<Feature> assemble(TerrainFeatureParseRecord record, List<String> errors) {
        return assemble(List.of(record), errors);
    }

    // ---- Ordinary path ----------------------------------------------------

    /**
     * C's SECRET record: {@code mimic:GRANITE}, {@code priority:10}, {@code digging:5}. The mimic
     * resolves to the named feature and not to {@code FEAT_NONE}.
     */
    @Test
    void fullRecordResolvesEveryDirective() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("SECRET", "GRANITE", "10", "5",
                List.of("WALL", "ROCK", "DOOR_ANY", "GRANITE", "TORCH"), "w", "", "", "", 7), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, result.size());
        Feature secret = result.get(0);
        assertEquals(TerrainFlags.FEAT_SECRET, secret.getCodeFlags());
        assertTrue(secret.isMimicing());
        assertTrue(secret.toString().contains("mimic=FEAT_GRANITE"), secret::toString);
        assertTrue(secret.toString().contains("priority=10"), secret::toString);
        assertTrue(secret.toString().contains("dig=5"), secret::toString);
        assertTrue(secret.isWall(), "TF_WALL should have been set");
        assertTrue(secret.hasAnyDoor(), "TF_DOOR_ANY should have been set");
        assertTrue(secret.isGranite(), "TF_GRANITE should have been set");
    }

    /**
     * With only the header, everything optional takes C's zeroed default: priority 0, digging 0,
     * {@code RF_NONE}, no mimic.
     */
    @Test
    void absentOptionalDirectivesTakeTheZeroedDefaults() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(record("FLOOR"), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        Feature floor = result.get(0);
        assertFalse(floor.isMimicing(), "no mimic: line must leave the mimic null, not FEAT_NONE");
        assertTrue(floor.toString().contains("priority=0"), floor::toString);
        assertTrue(floor.toString().contains("dig=0"), floor::toString);
        assertTrue(floor.toString().contains("resistFlag=RF_NONE"), floor::toString);
    }

    @Test
    void recordsComeBackInFileOrder() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(List.of(record("FLOOR"), record("CLOSED"), record("OPEN")),
                errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(List.of(TerrainFlags.FEAT_FLOOR, TerrainFlags.FEAT_CLOSED, TerrainFlags.FEAT_OPEN),
                result.stream().map(Feature::getCodeFlags).toList());
    }

    // ---- Code: lookup_feat_code ------------------------------------------

    /** {@code FEAT(NONE)} is index 0 in {@code list-terrain.h}, so {@code code:NONE} is valid. */
    @Test
    void codeNoneIsValid() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(record("NONE"), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(TerrainFlags.FEAT_NONE, result.get(0).getCodeFlags());
    }

    /**
     * {@code list-terrain.h} has no {@code MAX} entry, so {@code lookup_feat_code("MAX")} is -1 and
     * {@code parse_feat_code} fails with {@code PARSE_ERROR_OUT_OF_BOUNDS}. Java has a
     * {@code FEAT_MAX} constant, so this is the case a bare {@code valueOf} would wrongly accept.
     * The record is skipped softly, with no exception.
     */
    @Test
    void codeMaxIsRefusedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(record("MAX"), errors);

        assertTrue(result.isEmpty(), result::toString);
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("illegal terrain feature code") && errors.get(0).contains("MAX"),
                errors::toString);
    }

    /**
     * The refused {@code MAX} must not be remembered as a seen code: a real {@code NONE} record
     * after it still loads, and so does one before it.
     */
    @Test
    void refusedMaxDoesNotCollideWithRealNone() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(List.of(record("NONE"), record("MAX"), record("FLOOR")), errors);

        assertEquals(1, errors.size(), errors::toString);
        assertEquals(List.of(TerrainFlags.FEAT_NONE, TerrainFlags.FEAT_FLOOR),
                result.stream().map(Feature::getCodeFlags).toList());
    }

    @Test
    void unknownCodeIsReportedWithItsLine() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("NOTACODE", "", "", "", List.of(), "w", "", "", "", 12), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("line: 12") && errors.get(0).contains("NOTACODE"), errors::toString);
    }

    /** C compares with {@code streq()}, which is case-sensitive; so does {@code valueOf}. */
    @Test
    void codeLookupIsCaseSensitive() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(record("floor"), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
    }

    /**
     * C returns {@code PARSE_ERROR_REPEATED_DIRECTIVE} from {@code parse_feat_name} for a second
     * record on the same {@code f_info} slot, and the load quits. Java throws.
     */
    @Test
    void repeatedCodeThrows() {
        List<TerrainFeatureParseRecord> records = List.of(record("FLOOR"), record("CLOSED"), record("FLOOR"));

        RuntimeException e = assertThrows(RuntimeException.class, () -> assemble(records, new ArrayList<>()));

        assertTrue(e.getMessage().contains("FEAT_FLOOR"), e::getMessage);
    }

    // ---- Mimic: parse_feat_mimic -----------------------------------------

    /** {@code mimic:NONE} resolves in C to {@code &f_info[0]}, which is non-null. */
    @Test
    void mimicNoneIsValidAndIsAMimic() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "NONE", "", "", List.of(), "w", "", "", "", 1), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertTrue(result.get(0).isMimicing());
        assertTrue(result.get(0).toString().contains("mimic=FEAT_NONE"), () -> result.get(0).toString());
    }

    /** Same lookup as the code, so {@code mimic:MAX} is out of bounds in C too. */
    @Test
    void mimicMaxIsRefusedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "MAX", "", "", List.of(), "w", "", "", "", 3), errors);

        assertTrue(result.isEmpty(), result::toString);
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("illegal mimic feature code") && errors.get(0).contains("MAX"),
                errors::toString);
    }

    @Test
    void unknownMimicIsReportedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "NOTAMIMIC", "", "", List.of(), "w", "", "", "", 1), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("NOTAMIMIC"), errors::toString);
    }

    @Test
    void mimicLookupIsCaseSensitive() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("SECRET", "granite", "", "", List.of(), "w", "", "", "", 1), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
    }

    // ---- Flags: grab_flag against terrain_flags --------------------------

    /**
     * {@code list-terrain-flags.h} has no {@code MAX} row, so {@code grab_flag} fails and
     * {@code parse_feat_flags} returns {@code PARSE_ERROR_INVALID_FLAG}. Java has a
     * {@code TF_MAX} constant.
     */
    @Test
    void flagMaxIsRefusedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of("LOS", "MAX"), "w", "", "", "", 4),
                errors);

        assertTrue(result.isEmpty(), result::toString);
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("flag: MAX") && errors.get(0).contains("line: 4"), errors::toString);
    }

    /** {@code TF(NONE, "")} is the first row of {@code list-terrain-flags.h}, so it is valid. */
    @Test
    void flagNoneIsValid() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of("NONE", "FLOOR"), "w", "", "", "", 1),
                errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isFloor());
    }

    @Test
    void unknownFlagIsReportedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of("NOTAFLAG"), "w", "", "", "", 1), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
    }

    /**
     * Java reports every bad flag on the line, where C's {@code grab_flag} loop breaks at the
     * first. The record is skipped either way; this pins the Java behaviour documented on
     * {@code assemble}.
     */
    @Test
    void everyBadFlagOnTheLineIsReported() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of("BAD1", "LOS", "MAX"), "w", "", "", "", 1),
                errors);

        assertTrue(result.isEmpty());
        assertEquals(2, errors.size(), errors::toString);
    }

    // ---- Digging: parse_feat_digging -------------------------------------

    /** C accepts {@code DIGGING_RUBBLE + 1} (1) up to but excluding {@code DIGGING_MAX + 1} (6). */
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void diggingInsideOneToFiveIsAccepted(String dig) {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", dig, List.of(), "w", "", "", "", 1), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertTrue(result.get(0).toString().contains("dig=" + dig), () -> result.get(0).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "6", "100"})
    void diggingOutsideOneToFiveIsRefused(String dig) {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", dig, List.of(), "w", "", "", "", 1), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("out of range digging"), errors::toString);
    }

    @Test
    void overflowingDiggingIsAFormatError() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "99999999999999999999", List.of(), "w", "", "", "", 1),
                errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.get(0).contains("illegal number format on digging"), errors::toString);
    }

    // ---- Priority: parse_feat_priority -----------------------------------

    @Test
    void priorityZeroIsAccepted() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "0", "", List.of(), "w", "", "", "", 1), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertTrue(result.get(0).toString().contains("priority=0"));
    }

    /** {@code parser_getuint()} refuses a negative value. */
    @Test
    void negativePriorityIsRefused() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "-1", "", List.of(), "w", "", "", "", 1), errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.get(0).contains("negative priority"), errors::toString);
    }

    @Test
    void overflowingPriorityIsAFormatError() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "99999999999999999999", "", List.of(), "w", "", "", "", 1),
                errors);

        assertTrue(result.isEmpty());
        assertTrue(errors.get(0).contains("illegal number format on priority"), errors::toString);
    }

    // ---- Look strings: finish_parse_feat ---------------------------------

    /**
     * {@code finish_parse_feat()} appends a space to a prefix or preposition that does not end in
     * one. The stock STORE_* records ship {@code look-prefix:the entrance to the} and
     * {@code look-in-preposition:at}, both without the space.
     */
    @Test
    void lookStringsWithoutATrailingSpaceGetOne() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("STORE_GENERAL", "", "", "", List.of("SHOP"), "w",
                "the entrance to the", "at", "", 1), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        String text = result.get(0).toString();
        assertTrue(text.contains("lookPrefix='the entrance to the '"), text);
        assertTrue(text.contains("lookInPreposition='at '"), text);
    }

    /** {@code suffix(str, " ")} is already true, so no second space is added. */
    @Test
    void lookStringsAlreadyEndingInASpaceAreLeftAlone() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("OPEN", "", "", "", List.of(), "w", "the ", "in ", "", 1), errors);

        String text = result.get(0).toString();
        assertTrue(text.contains("lookPrefix='the '"), text);
        assertTrue(text.contains("lookInPreposition='in '"), text);
    }

    /** C's NULL stays NULL, so the callers' "a"/"an" and "on" fallbacks still apply. */
    @Test
    void absentLookStringsStayEmpty() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(record("FLOOR"), errors);

        String text = result.get(0).toString();
        assertTrue(text.contains("lookPrefix=''"), text);
        assertTrue(text.contains("lookInPreposition=''"), text);
    }

    // ---- Resist flag: parse_feat_resist_flag -----------------------------

    @Test
    void resistFlagResolvesIncludingDigitTails() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of(), "w", "", "", "RAND_50", 1), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertTrue(result.get(0).toString().contains("resistFlag=RF_RAND_50"), () -> result.get(0).toString());
    }

    /** {@code RF(NONE, ...)} is the first row of {@code list-mon-race-flags.h}, so it is valid. */
    @Test
    void resistFlagNoneIsValid() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of(), "w", "", "", "NONE", 1), errors);

        assertTrue(errors.isEmpty(), errors::toString);
        assertEquals(1, result.size());
    }

    @Test
    void unknownResistFlagIsReportedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of(), "w", "", "", "NOTARESIST", 1), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("NOTARESIST"), errors::toString);
    }

    // ---- Colour: parse_feat_graphics -------------------------------------

    /**
     * Java is stricter than C here: {@code color_text_to_attr()} falls back to white for an unknown
     * name, where the assembler drops the record. The lexer normally stops such a token first, so
     * this is the guard behind it.
     */
    @Test
    void unknownColourIsReportedAndTheRecordSkipped() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(full("FLOOR", "", "", "", List.of(), "NotAColour", "", "", "", 9), errors);

        assertTrue(result.isEmpty());
        assertEquals(1, errors.size(), errors::toString);
        assertTrue(errors.get(0).contains("line: 9") && errors.get(0).contains("NotAColour"), errors::toString);
    }

    // ---- Partial results --------------------------------------------------

    /** A skipped record costs only itself: the good ones either side still load, each bad one reported. */
    @Test
    void badRecordsAreSkippedAndGoodOnesSurvive() {
        List<String> errors = new ArrayList<>();

        List<Feature> result = assemble(List.of(
                record("FLOOR"),
                record("MAX"),
                full("CLOSED", "MAX", "", "", List.of(), "w", "", "", "", 5),
                full("OPEN", "", "", "", List.of("MAX"), "w", "", "", "", 6),
                record("BROKEN")), errors);

        assertEquals(3, errors.size(), errors::toString);
        assertEquals(List.of(TerrainFlags.FEAT_FLOOR, TerrainFlags.FEAT_BROKEN),
                result.stream().map(Feature::getCodeFlags).toList());
    }

    @Test
    void emptyInputGivesEmptyResult() {
        List<String> errors = new ArrayList<>();

        assertTrue(assemble(List.of(), errors).isEmpty());
        assertTrue(errors.isEmpty());
    }
}
