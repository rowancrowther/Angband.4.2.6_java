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
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureAssembler;
import uk.co.jackoftradesltd.backend.parser.terrainfeature.TerrainFeatureParseRecord;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.middle.cave.Feature;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFeatureFlags;
import uk.co.jackoftradesltd.middle.cave.enums.TerrainFlags;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TerrainFeatureAssembler}, driven from hand-built
 * {@link TerrainFeatureParseRecord}s rather than through the grammar.
 *
 * <p>The expected values come from the C original, {@code init.c}: the {@code parse_feat_*}
 * handlers and {@code finish_parse_feat()}, with {@code lookup_feat_code()} in {@code cave.c},
 * {@code lookup_flag()} in {@code datafile.c}, {@code string_append()} in {@code z-virt.c} and the
 * colour lookups in {@code z-color.c}. Three things drive most of the cases:
 * <ul>
 *   <li>every {@code parse_feat_*} error ends the load in C, so every error here must throw
 *       {@link DataFileParseException} and leave the {@code errors} list alone;</li>
 *   <li>{@code lookup_flag()} starts at {@code FLAG_START} (1), so {@code NONE} is refused for
 *       terrain and resist flags, while {@code lookup_feat_code()} starts at 0 and accepts
 *       {@code NONE} as a code or mimic; no table has a {@code MAX} entry;</li>
 *   <li>a repeated {@code code:} carries on filling the same {@code f_info} slot, so text lines
 *       append, flags add up, {@code graphics:} replaces, and the single-value lines replace only
 *       when present.</li>
 * </ul>
 *
 * <p>Test class rewritten on 261030 for the fatal-error model and repeated codes.
 *
 * @author Rowan Crowther
 */
class TerrainFeatureAssemblerTest {

    /**
     * A mutable stand-in for one {@code terrain.txt} block. Every optional directive starts as the
     * grammar leaves it when absent: the empty string, or an empty list.
     */
    private static final class Rec {
        String code;
        String name;
        String desc = "";
        String mimic = "";
        String priority = "";
        String dig = "";
        List<String> flags = List.of();
        String glyph = ".";
        String colour = "w";
        String walk = "";
        String run = "";
        String hurt = "";
        String die = "";
        String confused = "";
        String lookPrefix = "";
        String lookIn = "";
        String resist = "";
        int line = 1;

        Rec(String code) {
            this.code = code;
            this.name = "name of " + code;
        }

        TerrainFeatureParseRecord build() {
            return new TerrainFeatureParseRecord(code, name, desc, mimic, priority, dig, flags, glyph,
                    colour, walk, run, hurt, die, confused, lookPrefix, lookIn, resist, line);
        }
    }

    private static Rec rec(String code) {
        return new Rec(code);
    }

    private static List<Feature> assemble(List<String> errors, Rec... recs) {
        List<TerrainFeatureParseRecord> records = new ArrayList<>();
        for (Rec r : recs) {
            records.add(r.build());
        }
        return new TerrainFeatureAssembler().assemble(records, errors, "test.txt");
    }

    private static List<Feature> assemble(Rec... recs) {
        List<String> errors = new ArrayList<>();
        List<Feature> result = assemble(errors, recs);
        assertTrue(errors.isEmpty(), errors::toString);
        return result;
    }

    private static Feature one(Rec r) {
        List<Feature> result = assemble(r);
        assertEquals(1, result.size());
        return result.get(0);
    }

    /** Assert the load throws, the errors list stays empty, and the message names each fragment. */
    private static void assertFatal(Rec[] recs, String... fragments) {
        List<String> errors = new ArrayList<>();
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> assemble(errors, recs));
        assertTrue(errors.isEmpty(), "a fatal error must not also go to the errors list: " + errors);
        for (String fragment : fragments) {
            assertTrue(e.getMessage().contains(fragment), e::getMessage);
        }
    }

    private static void assertFatal(Rec r, String... fragments) {
        assertFatal(new Rec[]{r}, fragments);
    }

    // ---- Ordinary path ----------------------------------------------------

    /** C's SECRET record: {@code mimic:GRANITE}, {@code priority:10}, {@code digging:5}. */
    @Test
    void fullRecordResolvesEveryDirective() {
        Rec r = rec("SECRET");
        r.mimic = "GRANITE";
        r.priority = "10";
        r.dig = "5";
        r.flags = List.of("WALL", "ROCK", "DOOR_ANY", "GRANITE", "TORCH");
        r.desc = "A door that looks like a wall.";

        Feature secret = one(r);

        assertEquals(TerrainFlags.FEAT_SECRET, secret.getCodeFlags());
        assertEquals("name of SECRET", secret.getName());
        assertEquals("A door that looks like a wall.", secret.getDescription());
        assertTrue(secret.isMimicing());
        assertEquals(TerrainFlags.FEAT_GRANITE, secret.getMimicTerrainFlags());
        assertEquals(10, secret.getPriority());
        assertEquals(5, secret.getDig());
        assertTrue(secret.isWall());
        assertTrue(secret.hasAnyDoor());
        assertTrue(secret.isGranite());
    }

    /** With only the header, everything optional takes C's zeroed {@code f_info} default. */
    @Test
    void absentOptionalDirectivesTakeTheZeroedDefaults() {
        Feature floor = one(rec("FLOOR"));

        assertFalse(floor.isMimicing(), "no mimic: line must leave the mimic null, not FEAT_NONE");
        assertNull(floor.getMimicTerrainFlags());
        assertEquals(0, floor.getPriority());
        assertEquals(0, floor.getDig());
        assertTrue(floor.getFlags().isEmpty());
        assertEquals(MonsterRaceFlag.RF_NONE, floor.getResistFlag());
        assertEquals("", floor.getDescription());
        assertEquals("", floor.getWalkMsg());
    }

    @Test
    void recordsComeBackInFileOrder() {
        List<Feature> result = assemble(rec("FLOOR"), rec("CLOSED"), rec("OPEN"));

        assertEquals(List.of(TerrainFlags.FEAT_FLOOR, TerrainFlags.FEAT_CLOSED, TerrainFlags.FEAT_OPEN),
                result.stream().map(Feature::getCodeFlags).toList());
    }

    @Test
    void emptyInputGivesEmptyResult() {
        assertTrue(assemble().isEmpty());
    }

    // ---- Code: lookup_feat_code ------------------------------------------

    /** {@code FEAT(NONE)} is index 0 of {@code feat_code_list}, and the search starts at 0. */
    @Test
    void codeNoneIsValid() {
        assertEquals(TerrainFlags.FEAT_NONE, one(rec("NONE")).getCodeFlags());
    }

    /** {@code list-terrain.h} has no {@code MAX} row, so C returns {@code PARSE_ERROR_OUT_OF_BOUNDS}. */
    @Test
    void codeMaxIsFatal() {
        Rec r = rec("MAX");
        r.line = 4;
        assertFatal(r, "sentinel terrain feature", "MAX", "line: 4", "test.txt");
    }

    @Test
    void unknownCodeIsFatalWithItsLine() {
        Rec r = rec("NOTACODE");
        r.line = 12;
        assertFatal(r, "illegal terrain feature value", "NOTACODE", "line: 12");
    }

    /** C compares with {@code streq()}, which is case-sensitive. */
    @Test
    void codeLookupIsCaseSensitive() {
        assertFatal(rec("floor"), "floor");
    }

    /** A bad record later in the file still stops the whole load: no partial result. */
    @Test
    void aBadRecordAfterGoodOnesIsStillFatal() {
        assertFatal(new Rec[]{rec("FLOOR"), rec("CLOSED"), rec("MAX")}, "MAX");
    }

    // ---- Repeated code: the same f_info slot ------------------------------

    /** {@code parse_feat_name()} returns {@code PARSE_ERROR_REPEATED_DIRECTIVE} when the slot has a name. */
    @Test
    void secondNameForARepeatedCodeIsFatal() {
        Rec first = rec("FLOOR");
        Rec second = rec("FLOOR");
        second.name = "another floor";
        second.line = 9;

        assertFatal(new Rec[]{first, rec("CLOSED"), second},
                "second name value: another floor", "FLOOR", "line: 9");
    }

    /** No {@code name:} on the later record: the slot's name is left as it was. */
    @Test
    void repeatedCodeWithoutANameKeepsTheEarlierName() {
        Rec second = rec("FLOOR");
        second.name = "";

        Feature floor = one2(rec("FLOOR"), second);

        assertEquals("name of FLOOR", floor.getName());
    }

    /** The slot had no name, so C's {@code f->name} is NULL and the later name is taken. */
    @Test
    void repeatedCodeTakesALaterNameWhenTheEarlierHadNone() {
        Rec first = rec("FLOOR");
        first.name = "";

        Feature floor = one2(first, rec("FLOOR"));

        assertEquals("name of FLOOR", floor.getName());
    }

    /** The merged feature stays where the code first appeared, and there is only one of it. */
    @Test
    void repeatedCodeKeepsItsFirstPosition() {
        List<Feature> result = assemble(rec("FLOOR"), rec("CLOSED"), unnamed("FLOOR"), rec("OPEN"));

        assertEquals(List.of(TerrainFlags.FEAT_FLOOR, TerrainFlags.FEAT_CLOSED, TerrainFlags.FEAT_OPEN),
                result.stream().map(Feature::getCodeFlags).toList());
    }

    /** {@code string_append()} joins with no separator, for the description and all five messages. */
    @Test
    void repeatedCodeAppendsTheTextLines() {
        Rec first = rec("LAVA");
        first.desc = "Hot. ";
        first.walk = "w1";
        first.run = "r1";
        first.hurt = "h1";
        first.die = "d1";
        first.confused = "c1";
        Rec second = unnamed("LAVA");
        second.desc = "Very hot.";
        second.walk = "w2";
        second.run = "r2";
        second.hurt = "h2";
        second.die = "d2";
        second.confused = "c2";

        Feature lava = one2(first, second);

        assertEquals("Hot. Very hot.", lava.getDescription());
        assertEquals("w1w2", lava.getWalkMsg());
        assertEquals("r1r2", lava.getRunMsg());
        assertEquals("h1h2", lava.getHurtMsg());
        assertEquals("d1d2", lava.getDieMsg());
        assertEquals("c1c2", lava.getConfusedMsg());
    }

    /** {@code grab_flag()} turns bits on in the slot's existing set; nothing is cleared. */
    @Test
    void repeatedCodeAddsToTheFlags() {
        Rec first = rec("FLOOR");
        first.flags = List.of("LOS");
        Rec second = unnamed("FLOOR");
        second.flags = List.of("PROJECT");

        Feature floor = one2(first, second);

        assertTrue(floor.getFlags().has(TerrainFeatureFlags.TF_LOS));
        assertTrue(floor.getFlags().has(TerrainFeatureFlags.TF_PROJECT));
    }

    /** {@code parse_feat_graphics()} overwrites both {@code d_char} and {@code d_attr}. */
    @Test
    void repeatedCodeReplacesTheGraphics() {
        Rec first = rec("FLOOR");
        first.glyph = ".";
        first.colour = "w";
        Rec second = unnamed("FLOOR");
        second.glyph = "#";
        second.colour = "r";

        Feature floor = one2(first, second);

        assertEquals('#', floor.getDisplayCharacter().getNarrowCharacter());
        assertEquals(ColourEnum.COLOUR_RED, floor.getDisplayCharacter().getAttributeColour());
    }

    /** The single-value lines are kept when the later record leaves them out. */
    @Test
    void repeatedCodeKeepsSingleValuesItDoesNotRepeat() {
        Rec first = rec("SECRET");
        first.mimic = "GRANITE";
        first.priority = "10";
        first.dig = "5";
        first.resist = "IM_FIRE";

        Feature secret = one2(first, unnamed("SECRET"));

        assertEquals(TerrainFlags.FEAT_GRANITE, secret.getMimicTerrainFlags());
        assertEquals(10, secret.getPriority());
        assertEquals(5, secret.getDig());
        assertEquals(MonsterRaceFlag.RF_IM_FIRE, secret.getResistFlag());
    }

    /** ... and replaced when it gives them again. */
    @Test
    void repeatedCodeReplacesSingleValuesItRepeats() {
        Rec first = rec("SECRET");
        first.mimic = "GRANITE";
        first.priority = "10";
        first.dig = "5";
        first.resist = "IM_FIRE";
        Rec second = unnamed("SECRET");
        second.mimic = "MAGMA";
        second.priority = "3";
        second.dig = "2";
        second.resist = "IM_COLD";

        Feature secret = one2(first, second);

        assertEquals(TerrainFlags.FEAT_MAGMA, secret.getMimicTerrainFlags());
        assertEquals(3, secret.getPriority());
        assertEquals(2, secret.getDig());
        assertEquals(MonsterRaceFlag.RF_IM_COLD, secret.getResistFlag());
    }

    /** No mimic on either record: C's {@code f->mimic} stays NULL, so the feature must not mimic itself. */
    @Test
    void repeatedCodeWithNoMimicAnywhereIsNotAMimic() {
        Feature floor = one2(rec("FLOOR"), unnamed("FLOOR"));

        assertFalse(floor.isMimicing());
        assertNull(floor.getMimicTerrainFlags());
    }

    /** A mimic first given on the later record is taken. */
    @Test
    void repeatedCodeTakesALaterMimic() {
        Rec second = unnamed("SECRET");
        second.mimic = "GRANITE";

        assertEquals(TerrainFlags.FEAT_GRANITE, one2(rec("SECRET"), second).getMimicTerrainFlags());
    }

    // ---- Mimic: parse_feat_mimic -----------------------------------------

    /** {@code mimic:NONE} resolves in C to {@code &f_info[0]}, which is non-null. */
    @Test
    void mimicNoneIsValidAndIsAMimic() {
        Rec r = rec("FLOOR");
        r.mimic = "NONE";

        Feature floor = one(r);

        assertTrue(floor.isMimicing());
        assertEquals(TerrainFlags.FEAT_NONE, floor.getMimicTerrainFlags());
    }

    @Test
    void mimicMaxIsFatal() {
        Rec r = rec("FLOOR");
        r.mimic = "MAX";
        r.line = 3;
        assertFatal(r, "sentinel mimic", "MAX", "line: 3");
    }

    @Test
    void unknownMimicIsFatal() {
        Rec r = rec("FLOOR");
        r.mimic = "NOTAMIMIC";
        assertFatal(r, "illegal mimic value", "NOTAMIMIC");
    }

    @Test
    void mimicLookupIsCaseSensitive() {
        Rec r = rec("SECRET");
        r.mimic = "granite";
        assertFatal(r, "granite");
    }

    // ---- Flags: grab_flag against terrain_flags --------------------------

    /** {@code list-terrain-flags.h} has no {@code MAX} row; Java has a {@code TF_MAX} constant. */
    @Test
    void flagMaxIsFatal() {
        Rec r = rec("FLOOR");
        r.flags = List.of("LOS", "MAX");
        r.line = 4;
        assertFatal(r, "sentinel terrain flag", "MAX", "line: 4");
    }

    /** {@code lookup_flag()} starts at {@code FLAG_START} (1), so the {@code NONE} row is never matched. */
    @Test
    void flagNoneIsFatal() {
        Rec r = rec("FLOOR");
        r.flags = List.of("NONE", "FLOOR");
        assertFatal(r, "sentinel terrain flag", "NONE");
    }

    @Test
    void unknownFlagIsFatal() {
        Rec r = rec("FLOOR");
        r.flags = List.of("NOTAFLAG");
        assertFatal(r, "illegal terrain flag value", "NOTAFLAG");
    }

    /** {@code parse_feat_flags()} breaks at the first bad flag, so that is the one reported. */
    @Test
    void theFirstBadFlagIsTheOneReported() {
        Rec r = rec("FLOOR");
        r.flags = List.of("LOS", "BAD1", "MAX");
        assertFatal(r, "BAD1");
    }

    // ---- Digging: parse_feat_digging -------------------------------------

    /** C accepts {@code DIGGING_RUBBLE + 1} (1) up to but excluding {@code DIGGING_MAX + 1} (6). */
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "4", "5"})
    void diggingInsideOneToFiveIsAccepted(String dig) {
        Rec r = rec("FLOOR");
        r.dig = dig;
        assertEquals(Integer.parseInt(dig), one(r).getDig());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "6", "100", "-1"})
    void diggingOutsideOneToFiveIsFatal(String dig) {
        Rec r = rec("FLOOR");
        r.dig = dig;
        assertFatal(r, "out of range digging", dig);
    }

    @Test
    void overflowingDiggingIsFatal() {
        Rec r = rec("FLOOR");
        r.dig = "99999999999999999999";
        assertFatal(r, "digging");
    }

    // ---- Priority: parse_feat_priority -----------------------------------

    @Test
    void priorityZeroIsAccepted() {
        Rec r = rec("FLOOR");
        r.priority = "0";
        assertEquals(0, one(r).getPriority());
    }

    /** {@code parser_parse()} refuses a {@code uint} field whose token starts with {@code -}, including {@code -0}. */
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-0"})
    void negativePriorityIsFatal(String priority) {
        Rec r = rec("FLOOR");
        r.priority = priority;
        assertFatal(r, "negative priority", priority);
    }

    /**
     * Accepted deviation (261027): C's {@code strtoul} result wraps into the 32-bit field, while
     * {@code ParseUtils.parseIntString} clamps to the {@code int} range.
     */
    @Test
    void overflowingPriorityClampsToIntMax() {
        Rec r = rec("FLOOR");
        r.priority = "99999999999999999999";
        assertEquals(Integer.MAX_VALUE, one(r).getPriority());
    }

    // ---- Look strings: finish_parse_feat ---------------------------------

    /** The stock STORE_* records ship {@code look-prefix:the entrance to the} and {@code look-in-preposition:at}. */
    @Test
    void lookStringsWithoutATrailingSpaceGetOne() {
        Rec r = rec("STORE_GENERAL");
        r.flags = List.of("SHOP");
        r.lookPrefix = "the entrance to the";
        r.lookIn = "at";

        Feature store = one(r);

        assertEquals("the entrance to the ", store.getLookPrefix());
        assertEquals("at ", store.getLookInPreposition());
    }

    /** {@code suffix(str, " ")} is already true, so no second space is added. */
    @Test
    void lookStringsAlreadyEndingInASpaceAreLeftAlone() {
        Rec r = rec("OPEN");
        r.lookPrefix = "the ";
        r.lookIn = "in ";

        Feature open = one(r);

        assertEquals("the ", open.getLookPrefix());
        assertEquals("in ", open.getLookInPreposition());
    }

    /** C's NULL stays NULL, so the callers' "a"/"an" and "on" fallbacks still apply. */
    @Test
    void absentLookStringsStayEmpty() {
        Feature floor = one(rec("FLOOR"));

        assertEquals("", floor.getLookPrefix());
        assertEquals("", floor.getLookInPreposition());
    }

    /** {@code finish_parse_feat()} runs once at the end, so the raw text is joined before the space goes on. */
    @Test
    void repeatedCodeJoinsLookStringsRawThenAddsOneSpace() {
        Rec first = rec("FLOOR");
        first.lookPrefix = "a";
        first.lookIn = "o";
        Rec second = unnamed("FLOOR");
        second.lookPrefix = "b";
        second.lookIn = "n";

        Feature floor = one2(first, second);

        assertEquals("ab ", floor.getLookPrefix());
        assertEquals("on ", floor.getLookInPreposition());
    }

    /** A space the file itself put at the end of the earlier text is kept. */
    @Test
    void repeatedCodeKeepsASpaceTheEarlierTextAlreadyHad() {
        Rec first = rec("FLOOR");
        first.lookPrefix = "a ";
        Rec second = unnamed("FLOOR");
        second.lookPrefix = "b";

        assertEquals("a b ", one2(first, second).getLookPrefix());
    }

    // ---- Resist flag: parse_feat_resist_flag -----------------------------

    @Test
    void resistFlagResolvesIncludingDigitTails() {
        Rec r = rec("FLOOR");
        r.resist = "RAND_50";
        assertEquals(MonsterRaceFlag.RF_RAND_50, one(r).getResistFlag());
    }

    /** {@code lookup_flag()} starts at {@code FLAG_START} (1), so {@code RF(NONE)} at index 0 is never matched. */
    @Test
    void resistFlagNoneIsFatal() {
        Rec r = rec("FLOOR");
        r.resist = "NONE";
        assertFatal(r, "sentinel monster race flag", "NONE");
    }

    @Test
    void unknownResistFlagIsFatal() {
        Rec r = rec("FLOOR");
        r.resist = "NOTARESIST";
        assertFatal(r, "illegal monster race flag value", "NOTARESIST");
    }

    // ---- Graphics: parse_feat_graphics -----------------------------------

    /** A single character goes through {@code color_char_to_attr()}, which is case-sensitive. */
    @Test
    void singleLetterColourResolves() {
        Rec r = rec("FLOOR");
        r.colour = "r";
        assertEquals(ColourEnum.COLOUR_RED, one(r).getDisplayCharacter().getAttributeColour());
    }

    /** A longer name goes through {@code color_text_to_attr()}, which compares with {@code my_stricmp()}. */
    @Test
    void colourNameIsCaseInsensitive() {
        Rec r = rec("FLOOR");
        r.colour = "RED";
        assertEquals(ColourEnum.COLOUR_RED, one(r).getDisplayCharacter().getAttributeColour());
    }

    /** {@code color_text_to_attr()} ends with "Default to white". */
    @Test
    void unknownColourNameFallsBackToWhite() {
        Rec r = rec("FLOOR");
        r.colour = "NotAColour";
        assertEquals(ColourEnum.COLOUR_WHITE, one(r).getDisplayCharacter().getAttributeColour());
    }

    /** {@code color_char_to_attr()} ends with "we assume white". */
    @Test
    void unknownColourLetterFallsBackToWhite() {
        Rec r = rec("FLOOR");
        r.colour = "!";
        assertEquals(ColourEnum.COLOUR_WHITE, one(r).getDisplayCharacter().getAttributeColour());
    }

    @Test
    void glyphIsTakenFromTheRecord() {
        Rec r = rec("PASS_RUBBLE");
        r.glyph = ":";
        assertEquals(':', one(r).getDisplayCharacter().getNarrowCharacter());
    }

    /** {@code parser_getchar()} reads one wide character, so a glyph outside the BMP is kept whole. */
    @Test
    void supplementaryGlyphIsKeptWhole() {
        Rec r = rec("FLOOR");
        r.glyph = new String(Character.toChars(0x1F600));
        assertEquals(0x1F600, one(r).getDisplayCharacter().getWideCharacter());
    }

    @Test
    void emptyGlyphIsFatal() {
        Rec r = rec("FLOOR");
        r.glyph = "";
        assertFatal(r, "empty glyph");
    }

    // ---- Helpers ----------------------------------------------------------

    private static Rec unnamed(String code) {
        Rec r = rec(code);
        r.name = "";
        return r;
    }

    private static Feature one2(Rec first, Rec second) {
        List<Feature> result = assemble(first, second);
        assertEquals(1, result.size(), "a repeated code must give one feature, not two");
        return result.get(0);
    }
}
