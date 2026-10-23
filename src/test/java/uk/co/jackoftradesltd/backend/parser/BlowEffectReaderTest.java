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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.event.projection.Projection;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.monsters.BlowEffect;
import uk.co.jackoftradesltd.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link BlowEffectReader}: file text -&gt;
 * {@code BlowEffectLexer}/{@code BlowEffectGrammar} -&gt; {@code BlowEffectParseRecord} -&gt;
 * {@code BlowEffectAssembler} -&gt; {@link BlowEffect}.
 * <p>
 * The assembler is where the C semantics have to be reproduced by hand, and every boundary
 * is exercised here. Expected values come from the C source, not from the Java:
 * <ul>
 *   <li>{@code lash-type:} is resolved through [C] {@code proj_name_to_idx}
 *       ({@code project.c}), which searches the merged element-then-projection name list
 *       ignoring case. An unresolvable name is <em>not</em> an error: [C]
 *       {@code parse_eff_lash_type} ({@code mon-init.c}) falls back to {@code PROJ_MISSILE}.
 *       The shipped file relies on this - SHATTER says {@code SHARDS} but the projection
 *       code is {@code SHARD}. With no line, C's zeroed {@code lash_type} is
 *       {@code PROJ_ACID}.</li>
 *   <li>{@code resist:} is polymorphic on {@code effect-type:} ([C] {@code parse_eff_resist}):
 *       {@code element} resolves against the element list ignoring case, {@code flag}
 *       against the object flags exactly, and anything else is
 *       {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE}, which ends the load.</li>
 *   <li>Records with no {@code effect-type:} at all are legal and keep C's zeroed resist,
 *       which reads as {@code ELEM_ACID} and {@code OF_NONE}.</li>
 *   <li>Lore colours follow [C] {@code color_text_to_attr} and {@code color_char_to_attr}
 *       ({@code z-color.c}): an unknown name or character is {@code COLOUR_WHITE}, never an
 *       error.</li>
 *   <li>{@code power:} and {@code eval:} are read as [C] {@code parser_getint} reads them,
 *       with {@code strtol} in base 0, so a leading {@code 0} is octal.</li>
 * </ul>
 *
 * <p>Class BlowEffectReaderTest coded before 261023, commented in full on 261023, updated on
 * 261023 for the BlowEffectAssembler port.
 *
 * @author Rowan Crowther
 */
class BlowEffectReaderTest {

    private static final String REAL_FILE = "lib/gamedata/blow_effects.txt";
    private static final String PROJECTION_FILE = "lib/gamedata/projection.txt";

    private static Object savedProjections;

    /**
     * The assembler resolves {@code lash-type:} through
     * {@link WorldRegistry#lookupProjectionByLash}, so the projection table has to be
     * populated before any blow effect can be assembled.
     */
    @BeforeAll
    static void seed() throws Exception {
        List<Projection> projections = new ProjectionReader().parseWithResults(PROJECTION_FILE).items();
        savedProjections = setStatic("projections", projections);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("projections", savedProjections);
    }

    /**
     * Overwrite a private static on the owning registry (resolved via {@link RegistrySeeding}),
     * returning the previous value so
     * {@link #restore()} can put the real table back and leave the suite's shared statics
     * as they were found.
     */
    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static BlowEffect byName(List<BlowEffect> effects, String name) {
        return effects.stream().filter(e -> e.getName().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError("no blow effect named " + name));
    }

    private static List<BlowEffect> realFile() throws IOException {
        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(REAL_FILE);
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        return result.items();
    }

    // ---- Happy path: the real shipped file -------------------------------

    @Test
    void cleanLoadOfTheRealFileYieldsThirtyBlowEffects() throws IOException {
        assertEquals(30, realFile().size());
    }

    @Test
    void everyLashTypeInTheRealFileResolvesToAProjection() throws IOException {
        // A null lash would mean the projection lookup silently failed; the fallback in the
        // assembler guarantees a non-null Projection for every record that names a lash.
        // NONE names no lash-type:, and takes C's zeroed default, so it is not null either.
        assertTrue(realFile().stream().allMatch(e -> e.getLashType() != null),
                "every effect has a lash projection");
    }

    // ---- lash-type resolution --------------------------------------------

    @Test
    void lashTypeResolvesThroughTheProjectionCodeNotTheLashDescription() throws IOException {
        // projection.txt's lash-desc for poison is "venom"; the code is POIS. Matching on
        // lash-desc would find nothing at all.
        Projection lash = byName(realFile(), "POISON").getLashType();

        assertNotNull(lash);
        assertEquals(ProjectionEnum.PROJ_POIS, lash.getProjection());
    }

    @Test
    void unresolvableLashTypeFallsBackToMissileRatherThanErroring() throws IOException {
        // [C] parse_eff_lash_type (mon-init.c) - `type >= 0 ? type : PROJ_MISSILE`. SHATTER says lash-type:SHARDS
        // but the projection code is SHARD (singular), upstream included, so this fallback is
        // load-bearing for the shipped data rather than defensive.
        Projection lash = byName(realFile(), "SHATTER").getLashType();

        assertNotNull(lash);
        assertEquals(ProjectionEnum.PROJ_MISSILE, lash.getProjection());
    }

    // ---- resist is polymorphic on effect-type ----------------------------

    @Test
    void elementEffectTypeResolvesResistToAnElement() throws IOException {
        BlowEffect poison = byName(realFile(), "POISON");

        assertEquals(BlowEffectType.BET_ELEMENT, poison.getEffectType());
        assertEquals(ElementEnum.ELEM_POIS, poison.getElementEnumResist());
        assertEquals(ObjectFlag.OF_NONE, poison.getObjectFlagResist(),
                "the flag slot stays empty for an element resist");
    }

    @Test
    void flagEffectTypeResolvesResistToAnObjectFlag() throws IOException {
        BlowEffect paralyze = byName(realFile(), "PARALYZE");

        assertEquals(BlowEffectType.BET_FLAG, paralyze.getEffectType());
        assertEquals(ObjectFlag.OF_FREE_ACT, paralyze.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_ACID, paralyze.getElementEnumResist(),
                "the element slot keeps C's zeroed resist, element 0");
    }

    @Test
    void everyElementAndFlagResistInTheRealFileResolves() throws IOException {
        List<BlowEffect> effects = realFile();

        long elementResists = effects.stream()
                .filter(e -> e.getEffectType() == BlowEffectType.BET_ELEMENT)
                .filter(e -> e.getElementEnumResist() != ElementEnum.ELEM_NONE).count();
        long flagResists = effects.stream()
                .filter(e -> e.getEffectType() == BlowEffectType.BET_FLAG)
                .filter(e -> e.getObjectFlagResist() != ObjectFlag.OF_NONE).count();

        assertEquals(3, elementResists, "POISON, DISENCHANT, HALLU");
        assertEquals(13, flagResists, "5 sustains + 4 HOLD_LIFE + 3 PROT_* + FREE_ACT");
    }

    @Test
    void allSustainsEffectTypeResolvesAndCarriesNoResist() throws IOException {
        // The spelling in the data file is all_sustains with an underscore, unlike the
        // hyphenated eat-food/eat-light.
        BlowEffect loseAll = byName(realFile(), "LOSE_ALL");

        assertEquals(BlowEffectType.BET_ALL_SUSTAINS, loseAll.getEffectType());
        assertEquals(ElementEnum.ELEM_ACID, loseAll.getElementEnumResist());
        assertEquals(ObjectFlag.OF_NONE, loseAll.getObjectFlagResist());
    }

    @Test
    void recordWithNoEffectTypeKeepsNullTypeAndEmptyResists() throws IOException {
        // Eight records ship no effect-type: line at all - NONE is the first record in the file,
        // so this path is hit immediately on load.
        BlowEffect none = byName(realFile(), "NONE");

        assertNull(none.getEffectType());
        assertEquals(ElementEnum.ELEM_ACID, none.getElementEnumResist());
        assertEquals(ObjectFlag.OF_NONE, none.getObjectFlagResist());
        assertEquals(ProjectionEnum.PROJ_ACID, none.getLashType().getProjection(),
                "no lash-type: leaves C's zeroed lash_type, projection 0");
    }

    // ---- scalar and colour fields ----------------------------------------

    @Test
    void powerAndEvalAreParsedAsIntegers() throws IOException {
        BlowEffect hurt = byName(realFile(), "HURT");

        assertEquals(40, hurt.getPower());
        assertEquals(0, hurt.getEval());
        assertEquals("attack", hurt.getDesc());
    }

    // ---- The reader itself: ordering, error channels, file handling -------

    /** Writes {@code text} to a fresh file under {@code dir} and returns its path as a string. */
    private static String write(Path dir, String text) throws IOException {
        Path file = dir.resolve("blow_effects.txt");
        Files.writeString(file, text);
        return file.toString();
    }

    @Test
    void recordsComeBackInTheOrderTheFileListsThem() throws IOException {
        // The order of the `name:` lines in upstream lib/gamedata/blow_effects.txt. C's
        // finish_parse_eff reverses its linked list back into file order, so blow_effects[0] is
        // NONE and the last real record is BLACK_BREATH.
        List<String> expected = List.of("NONE", "HURT", "POISON", "DISENCHANT", "DRAIN_CHARGES",
                "EAT_GOLD", "EAT_ITEM", "EAT_FOOD", "EAT_LIGHT", "ACID", "ELEC", "FIRE", "COLD",
                "BLIND", "CONFUSE", "TERRIFY", "PARALYZE", "LOSE_STR", "LOSE_INT", "LOSE_WIS",
                "LOSE_DEX", "LOSE_CON", "LOSE_ALL", "SHATTER", "EXP_10", "EXP_20", "EXP_40",
                "EXP_80", "HALLU", "BLACK_BREATH");

        assertEquals(expected, realFile().stream().map(BlowEffect::getName).toList());
    }

    @Test
    void parseReturnsTheSameEffectsAsParseWithResults() throws IOException {
        BlowEffectReader reader = new BlowEffectReader();

        List<String> viaParse = reader.parse(REAL_FILE).stream().map(BlowEffect::getName).toList();
        List<String> viaResults = reader.parseWithResults(REAL_FILE).items().stream()
                .map(BlowEffect::getName).toList();

        assertEquals(viaResults, viaParse);
        assertEquals(30, viaParse.size());
    }

    @Test
    void missingFileIsAnIOExceptionNotAnEmptyResult(@TempDir Path dir) {
        // C: parse_file_quit_not_found() quits with "Cannot open 'blow_effects.txt'".
        String missing = dir.resolve("no_such_file.txt").toString();

        assertThrows(IOException.class, () -> new BlowEffectReader().parseWithResults(missing));
        assertThrows(IOException.class, () -> new BlowEffectReader().parse(missing));
    }

    @Test
    void syntaxErrorYieldsNoItemsAndAnErrorMessage(@TempDir Path dir) throws IOException {
        // A directive before any name: has no record to belong to. C asserts or returns
        // PARSE_ERROR_MISSING_RECORD_HEADER; either way nothing is loaded.
        String file = write(dir, "record-count:1\n\npower:5\n");

        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void parseDropsErrorsSoASyntaxErrorLooksLikeAnEmptyList(@TempDir Path dir) throws IOException {
        String file = write(dir, "record-count:1\n\npower:5\n");

        assertTrue(new BlowEffectReader().parse(file).isEmpty());
    }

    @Test
    void recordCountMismatchIsASoftErrorAndTheRecordsStillLoad(@TempDir Path dir) throws IOException {
        // record-count: is a Java-only header; upstream has no count to check.
        String file = write(dir, "record-count:5\n\nname:NONE\npower:0\n\nname:HURT\npower:40\n");

        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);

        assertEquals(2, result.items().size());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors().get(0).contains("5"), result.errors().toString());
    }

    @Test
    void matchingRecordCountLeavesNoErrors(@TempDir Path dir) throws IOException {
        String file = write(dir, "record-count:2\n\nname:NONE\npower:0\n\nname:HURT\npower:40\n");

        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);

        assertFalse(result.hasErrors(), result.errors().toString());
        assertEquals(2, result.items().size());
    }

    @Test
    void nonNumericRecordCountIsAHardErrorHere(@TempDir Path dir) throws IOException {
        // The grammar only accepts an INTEGER after record-count:, so a bad count is a syntax
        // error rather than the soft one checkRecordCount reports: nothing loads.
        String file = write(dir, "record-count:many\n\nname:NONE\npower:0\n");

        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aBadRecordEndsTheLoadAsInC(@TempDir Path dir) throws IOException {
        // C stops at the first parse error, and so does the port: a resist: with no effect-type:
        // in the middle record ends the whole load rather than being skipped.
        String file = write(dir, "record-count:3\n\nname:NONE\npower:0\n\nname:HURT\nresist:POIS\n\nname:POISON\npower:20\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new BlowEffectReader().parseWithResults(file));
        assertTrue(e.getMessage().contains("line: 6"), e.getMessage());
    }

    @Test
    void repeatedDirectiveKeepsTheLastValue(@TempDir Path dir) throws IOException {
        // C: parse_eff_power just overwrites eff->power.
        String file = write(dir, "record-count:1\n\nname:HURT\npower:10\npower:40\n");

        assertEquals(40, new BlowEffectReader().parseWithResults(file).items().get(0).getPower());
    }

    @Test
    void duplicateNamesAreBothKept(@TempDir Path dir) throws IOException {
        // C's parse_eff_name allocates a new record every time and never looks for a clash.
        String file = write(dir, "record-count:2\n\nname:HURT\npower:10\n\nname:HURT\npower:40\n");

        List<BlowEffect> items = new BlowEffectReader().parseWithResults(file).items();

        assertEquals(2, items.size());
        assertEquals(10, items.get(0).getPower());
        assertEquals(40, items.get(1).getPower());
    }

    @Test
    void resistMayPrecedeEffectTypeUnlikeC(@TempDir Path dir) throws IOException {
        // C resolves resist: while parsing, so effect-type: must come first (the other way round
        // reads a null effect_type). The port resolves from the finished record, so order is free.
        String file = write(dir, "record-count:1\n\nname:POISON\nresist:POIS\neffect-type:element\n");

        BlowEffect poison = new BlowEffectReader().parseWithResults(file).items().get(0);

        assertEquals(BlowEffectType.BET_ELEMENT, poison.getEffectType());
        assertEquals(ElementEnum.ELEM_POIS, poison.getElementEnumResist());
    }

    @Test
    void resistWithNoEffectTypeEndsTheLoad(@TempDir Path dir) throws IOException {
        // C: streq on a NULL effect_type; the port treats it as the missing type it is.
        String file = write(dir, "record-count:1\n\nname:POISON\nresist:POIS\n");

        assertThrows(DataFileParseException.class, () -> new BlowEffectReader().parseWithResults(file));
    }

    @Test
    void resistOnANonResistEffectTypeEndsTheLoad(@TempDir Path dir) throws IOException {
        // C: parse_eff_resist returns PARSE_ERROR_MISSING_BLOW_EFF_TYPE for "theft".
        String file = write(dir, "record-count:1\n\nname:EAT_GOLD\neffect-type:theft\nresist:POIS\n");

        assertThrows(DataFileParseException.class, () -> new BlowEffectReader().parseWithResults(file));
    }

    // ---- Single-record cases, expected values from the C source ------------

    /** Loads a one-record file whose record is {@code name:X} followed by {@code body}. */
    private static BlowEffect one(Path dir, String body) throws IOException {
        String file = write(dir, "record-count:1\n\nname:X\n" + body);
        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);
        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        return result.items().get(0);
    }

    @Test
    void anUnknownColourCharacterIsWhiteNotAnError(@TempDir Path dir) throws IOException {
        // color_char_to_attr: no index_char matches 'Q', so "we assume white".
        assertEquals(ColourEnum.COLOUR_WHITE, one(dir, "lore-color-base:Q\n").getLoreAttr());
    }

    @Test
    void colourCharactersMatchCaseSensitively(@TempDir Path dir) throws IOException {
        // color_table: 'w' is White, 'W' is Light Slate.
        BlowEffect e = one(dir, "lore-color-base:w\nlore-color-resist:W\n");

        assertEquals(ColourEnum.COLOUR_WHITE, e.getLoreAttr());
        assertEquals(ColourEnum.COLOUR_LIGHT_SLATE, e.getLoreAttrResist());
    }

    @Test
    void colourNamesMatchIgnoringCase(@TempDir Path dir) throws IOException {
        // color_text_to_attr compares with my_stricmp.
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, one(dir, "lore-color-immune:light GREEN\n").getLoreAttrImmune());
    }

    @Test
    void anUnknownColourNameIsWhite(@TempDir Path dir) throws IOException {
        // color_text_to_attr: "Default to white". (Purple is in C's table; Chartreuse is not.)
        assertEquals(ColourEnum.COLOUR_WHITE, one(dir, "lore-color-base:Chartreuse\n").getLoreAttr());
    }

    @Test
    void twoCharactersAreReadAsANameNotACharacter(@TempDir Path dir) throws IOException {
        // strlen("ww") > 1 sends it to color_text_to_attr, where no name matches: white, not 'w'.
        // 'b' would be Blue as a character; "bb" is still white.
        assertEquals(ColourEnum.COLOUR_WHITE, one(dir, "lore-color-base:bb\n").getLoreAttr());
    }

    @Test
    void shadeIsWhiteBecauseCHasNoRowNamedShade(@TempDir Path dir) throws IOException {
        // C's color_table has no "Shade" row, so color_text_to_attr falls through to white.
        assertEquals(ColourEnum.COLOUR_WHITE, one(dir, "lore-color-resist:Shade\n").getLoreAttrResist());
    }

    @Test
    void powerAndEvalWithALeadingZeroAreOctal(@TempDir Path dir) throws IOException {
        // parser_getint: strtol(tok, &z, 0), so 010 is eight and 017 is fifteen.
        BlowEffect e = one(dir, "power:010\neval:017\n");

        assertEquals(8, e.getPower());
        assertEquals(15, e.getEval());
    }

    @Test
    void negativePowerAndEvalAreKept(@TempDir Path dir) throws IOException {
        // strtol takes a leading '-'; parse_eff_power stores it without a range check.
        BlowEffect e = one(dir, "power:-5\neval:-12\n");

        assertEquals(-5, e.getPower());
        assertEquals(-12, e.getEval());
    }

    @Test
    void anOversizedPowerIsClampedTheAcceptedDeviation(@TempDir Path dir) throws IOException {
        // C keeps the low 32 bits of strtol's 64-bit result (3000000000 -> -1294967296); the
        // port clamps, as recorded in ParseUtils and docs/Accepted_Deviations.md.
        assertEquals(Integer.MAX_VALUE, one(dir, "power:3000000000\n").getPower());
    }

    @Test
    void anElementResistMatchesIgnoringCase(@TempDir Path dir) throws IOException {
        // proj_name_to_idx compares with my_stricmp.
        assertEquals(ElementEnum.ELEM_POIS, one(dir, "effect-type:element\nresist:pois\n").getElementEnumResist());
    }

    @Test
    void anUnknownElementResistIsElemNone(@TempDir Path dir) throws IOException {
        // C stores proj_name_to_idx's -1, which has no ElementEnum constant; ELEM_NONE stands in.
        assertEquals(ElementEnum.ELEM_NONE, one(dir, "effect-type:element\nresist:BOGUS\n").getElementEnumResist());
    }

    @Test
    void aFlagResistMatchesExactly(@TempDir Path dir) throws IOException {
        // flag_index_by_name uses streq, so "free_act" is not FREE_ACT; C stores -1, here OF_NONE.
        assertEquals(ObjectFlag.OF_FREE_ACT, one(dir, "effect-type:flag\nresist:FREE_ACT\n").getObjectFlagResist());
        assertEquals(ObjectFlag.OF_NONE, one(dir, "effect-type:flag\nresist:free_act\n").getObjectFlagResist());
    }

    @Test
    void anElementResistLeavesTheFlagSlotAtZeroAndViceVersa(@TempDir Path dir) throws IOException {
        // C's one zeroed int: element 0 is ACID, flag 0 is NONE.
        BlowEffect element = one(dir, "effect-type:element\nresist:CHAOS\n");
        assertEquals(ObjectFlag.OF_NONE, element.getObjectFlagResist());

        BlowEffect flag = one(dir, "effect-type:flag\nresist:HOLD_LIFE\n");
        assertEquals(ElementEnum.ELEM_ACID, flag.getElementEnumResist());
    }

    @Test
    void lashTypeMatchesIgnoringCase(@TempDir Path dir) throws IOException {
        // proj_name_to_idx compares with my_stricmp.
        assertEquals(ProjectionEnum.PROJ_FIRE, one(dir, "lash-type:fire\n").getLashType().getProjection());
    }

    @Test
    void anUnknownLashTypeIsMissile(@TempDir Path dir) throws IOException {
        // parse_eff_lash_type: type >= 0 ? type : PROJ_MISSILE.
        assertEquals(ProjectionEnum.PROJ_MISSILE, one(dir, "lash-type:BOGUS\n").getLashType().getProjection());
    }

    @Test
    void aNonElementProjectionIsAValidLashType(@TempDir Path dir) throws IOException {
        // proj_name_list holds elements then projections, so a projection-only name resolves.
        assertEquals(ProjectionEnum.PROJ_MISSILE, one(dir, "lash-type:MISSILE\n").getLashType().getProjection());
        assertEquals(ProjectionEnum.PROJ_ICE, one(dir, "lash-type:ICE\n").getLashType().getProjection());
    }

    @Test
    void repeatedDescLinesAreJoined(@TempDir Path dir) throws IOException {
        // parse_eff_desc: string_append onto the existing desc, no separator.
        assertEquals("ab", one(dir, "desc:a\ndesc:b\n").getDesc());
    }
}
