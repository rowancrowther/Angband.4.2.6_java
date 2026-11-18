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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
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
 * {@code BlowEffect2Lexer}/{@code BlowEffect2Grammar} -&gt; {@code BlowEffect2ParseRecord} -&gt;
 * {@code BlowEffect2Assembler} -&gt; {@link BlowEffect}.
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
 *       {@code PROJ_ACID}, and the port's {@code BlowEffect()} default looks up that same
 *       projection, so the lash is the ACID projection, not null.</li>
 *   <li>{@code resist:} is polymorphic on {@code effect-type:} ([C] {@code parse_eff_resist}):
 *       {@code element} resolves against the element list ignoring case, {@code flag}
 *       against the object flags exactly, and anything else is
 *       {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE}, which ends the load.</li>
 *   <li>Records with no {@code effect-type:} at all are legal and keep C's zeroed resist,
 *       which reads as {@code ELEM_ACID} and {@code OF_NONE} in C. The port holds
 *       {@code ELEM_NONE}, {@code PROJ_NONE} and {@code OF_NONE} instead.</li>
 *   <li>Lore colours follow [C] {@code color_text_to_attr} and {@code color_char_to_attr}
 *       ({@code z-color.c}): an unknown name or character is {@code COLOUR_WHITE}, never an
 *       error.</li>
 *   <li>{@code power:} and {@code eval:} are read as [C] {@code parser_getint} reads them,
 *       with {@code strtol} in base 0, so a leading {@code 0} is octal.</li>
 * </ul>
 *
 * <p>Class BlowEffectReaderTest coded before 261023, commented in full on 261023, updated on
 * 261023 for the BlowEffectAssembler port, updated on 261118 for the move to the
 * {@code BlowEffect2} grammar, parse record and assembler.
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
        // NONE names no lash-type:. C's zeroed lash_type is projection 0, which is PROJ_ACID, and
        // the BlowEffect() default looks that projection up, so NONE ends with the ACID projection.
        assertTrue(realFile().stream().allMatch(e -> e.getLashType() != null),
                "every effect has a lash projection");
        assertEquals(ProjectionEnum.PROJ_ACID, byName(realFile(), "NONE").getLashType().getProjection(),
                "NONE names no lash-type:, so it keeps C's zeroed index 0, PROJ_ACID");
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
        assertEquals(ElementEnum.ELEM_NONE, paralyze.getElementEnumResist(),
                "the element slot holds ELEM_NONE where C's zeroed resist reads as element 0 (ACID)");
        assertEquals(ProjectionEnum.PROJ_NONE, paralyze.getProjectionResist());
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
        assertEquals(ElementEnum.ELEM_NONE, loseAll.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, loseAll.getProjectionResist());
        assertEquals(ObjectFlag.OF_NONE, loseAll.getObjectFlagResist());
    }

    @Test
    void recordWithNoEffectTypeKeepsNullTypeAndEmptyResists() throws IOException {
        // Eight records ship no effect-type: line at all - NONE is the first record in the file,
        // so this path is hit immediately on load.
        BlowEffect none = byName(realFile(), "NONE");

        assertNull(none.getEffectType());
        assertEquals(ElementEnum.ELEM_NONE, none.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, none.getProjectionResist());
        assertEquals(ObjectFlag.OF_NONE, none.getObjectFlagResist());
        assertNotNull(none.getLashType(), "no lash-type: leaves C's zeroed lash_type, projection 0");
        assertEquals(ProjectionEnum.PROJ_ACID, none.getLashType().getProjection(),
                "C's projection 0 is ACID, the first entry of proj_name_list");
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
    void aSyntaxErrorEndsTheLoad(@TempDir Path dir) throws IOException {
        // A directive before any name: has no record to belong to. C asserts or returns
        // PARSE_ERROR_MISSING_RECORD_HEADER; either way nothing is loaded.
        String file = write(dir, "\npower:5\n");

        assertThrows(ParseCancellationException.class, () -> new BlowEffectReader().parseWithResults(file));

    }

    @Test
    void parseThrowsOnASyntaxErrorTooRatherThanReturningAnEmptyList(@TempDir Path dir) throws IOException {
        String file = write(dir, "\npower:5\n");

        assertThrows(ParseCancellationException.class, () -> new BlowEffectReader().parse(file));
    }

    @Test
    void aResistWithNoEffectTypeInTheMiddleRecordEndsTheWholeLoad(@TempDir Path dir) throws IOException {
        // C reads a NULL effect_type in parse_eff_resist (streq on NULL) and crashes; the port refuses
        // the line instead, and the refusal ends the whole load rather than skipping the record. The
        // message quotes the resist: line (6), not the name: line of its record (5).
        String file = write(dir, "\nname:NONE\npower:0\n\nname:HURT\nresist:POIS\n\nname:POISON\npower:20\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new BlowEffectReader().parseWithResults(file));
        assertTrue(e.getMessage().contains("line: 6"), e.getMessage());
        assertTrue(e.getMessage().contains("resist before a effect type"), e.getMessage());
    }

    @Test
    void repeatedDirectiveKeepsTheLastValue(@TempDir Path dir) throws IOException {
        // C: parse_eff_power just overwrites eff->power.
        String file = write(dir, "\nname:HURT\npower:10\npower:40\n");

        assertEquals(40, new BlowEffectReader().parseWithResults(file).items().get(0).getPower());
    }

    @Test
    void duplicateNamesAreBothKept(@TempDir Path dir) throws IOException {
        // C's parse_eff_name allocates a new record every time and never looks for a clash.
        String file = write(dir, "\nname:HURT\npower:10\n\nname:HURT\npower:40\n");

        List<BlowEffect> items = new BlowEffectReader().parseWithResults(file).items();

        assertEquals(2, items.size());
        assertEquals(10, items.get(0).getPower());
        assertEquals(40, items.get(1).getPower());
    }

    @Test
    void resistBeforeEffectTypeInTheSameRecordEndsTheLoad(@TempDir Path dir) throws IOException {
        // C resolves resist: while parsing, so effect-type: must come first (the other way round
        // reads a null effect_type and crashes). The port resolves directives in file order too and
        // refuses the resist: line, even though the effect-type: follows it in the same record.
        // Accepted deviation, decided 261117 (docs/Accepted_Deviations.md, Blow effect).
        String file = write(dir, "\nname:POISON\nresist:POIS\neffect-type:element\n");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> new BlowEffectReader().parseWithResults(file));
        assertTrue(e.getMessage().contains("line: 3"), e.getMessage());
        assertTrue(e.getMessage().contains("resist before a effect type"), e.getMessage());
    }

    @Test
    void resistWithNoEffectTypeEndsTheLoad(@TempDir Path dir) throws IOException {
        // C: streq on a NULL effect_type; the port treats it as the missing type it is.
        String file = write(dir, "\nname:POISON\nresist:POIS\n");

        assertThrows(DataFileParseException.class, () -> new BlowEffectReader().parseWithResults(file));
    }

    @Test
    void anUnknownEffectTypeEndsTheLoadWhereCStoresTheString(@TempDir Path dir) throws IOException {
        // C: parse_eff_effect_type stores any string unchecked, and blow_color() in mon-lore.c falls
        // through to the base colour for one it does not know. The port's BlowEffectType is a closed
        // enum, so it refuses the line and quotes it. Accepted deviation, decided 261117
        // (docs/Accepted_Deviations.md, Blow effect). The match is exact, so the case matters too.
        for (String type : List.of("BOGUS", "Element", "FLAG", "BET_FLAG")) {
            String file = write(dir, "\nname:X\neffect-type:" + type + "\n");

            DataFileParseException e = assertThrows(DataFileParseException.class,
                    () -> new BlowEffectReader().parseWithResults(file), type);
            assertTrue(e.getMessage().contains("line: 3"), e.getMessage());
            assertTrue(e.getMessage().contains("illegal effect-type value: " + type), e.getMessage());
        }
    }

    @Test
    void everySpellingTheRealFileUsesIsAKnownEffectType(@TempDir Path dir) throws IOException {
        // The seven spellings blow_effects.txt uses, exactly as C's blow_color() compares them.
        for (String type : List.of("element", "flag", "drain", "theft", "eat-food", "eat-light", "all_sustains")) {
            assertNotNull(one(dir, "effect-type:" + type + "\n").getEffectType(), type);
        }
    }

    @Test
    void resistOnANonResistEffectTypeEndsTheLoad(@TempDir Path dir) throws IOException {
        // C: parse_eff_resist returns PARSE_ERROR_MISSING_BLOW_EFF_TYPE for "theft".
        String file = write(dir, "\nname:EAT_GOLD\neffect-type:theft\nresist:POIS\n");

        assertThrows(DataFileParseException.class, () -> new BlowEffectReader().parseWithResults(file));
    }

    // ---- Single-record cases, expected values from the C source ------------

    /** Loads a one-record file whose record is {@code name:X} followed by {@code body}. */
    private static BlowEffect one(Path dir, String body) throws IOException {
        String file = write(dir, "\nname:X\n" + body);
        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);
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
    void anElementResistLeavesTheFlagSlotEmptyAndViceVersa(@TempDir Path dir) throws IOException {
        // C keeps one int, so only one kind of resist exists at a time. The port holds ELEM_NONE,
        // PROJ_NONE and OF_NONE for the slots that are not in use.
        BlowEffect element = one(dir, "effect-type:element\nresist:CHAOS\n");
        assertEquals(ObjectFlag.OF_NONE, element.getObjectFlagResist());

        BlowEffect flag = one(dir, "effect-type:flag\nresist:HOLD_LIFE\n");
        assertEquals(ElementEnum.ELEM_NONE, flag.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, flag.getProjectionResist());
    }

    // ---- projectionResist: C's proj_name_to_idx result for an element resist ----

    @Test
    void anElementResistFillsTheElementAndProjectionFields(@TempDir Path dir) throws IOException {
        // proj_name_to_idx finds POIS in the element part of the list; PROJ_POIS == ELEM_POIS.
        BlowEffect poison = one(dir, "effect-type:element\nresist:POIS\n");
        assertEquals(ElementEnum.ELEM_POIS, poison.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_POIS, poison.getProjectionResist());

        // my_stricmp: the case does not matter.
        BlowEffect lower = one(dir, "effect-type:element\nresist:pois\n");
        assertEquals(ElementEnum.ELEM_POIS, lower.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_POIS, lower.getProjectionResist());
    }

    @Test
    void missileIsAnElementSoItFillsBothFields(@TempDir Path dir) throws IOException {
        // list-elements.h ends PLASMA, METEOR, MISSILE, MANA, HOLY_ORB, ARROW, so MISSILE is
        // inside the element part of proj_name_list.
        BlowEffect e = one(dir, "effect-type:element\nresist:MISSILE\n");

        assertEquals(ElementEnum.ELEM_MISSILE, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_MISSILE, e.getProjectionResist());
    }

    @Test
    void aProjectionOnlyNameFillsTheProjectionFieldAndLeavesTheElementNone(@TempDir Path dir)
            throws IOException {
        // KILL_WALL is after ARROW in proj_name_list, so C stores an index above the last element;
        // there is no ElementEnum for it.
        BlowEffect e = one(dir, "effect-type:element\nresist:KILL_WALL\n");

        assertEquals(ProjectionEnum.PROJ_KILL_WALL, e.getProjectionResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
    }

    @Test
    void resistMaxStoresTheProjectionMaxIndex(@TempDir Path dir) throws IOException {
        // proj_name_list ends with "MAX", so proj_name_to_idx("MAX") is PROJ_MAX, not -1.
        assertEquals(ProjectionEnum.PROJ_MAX, one(dir, "effect-type:element\nresist:MAX\n").getProjectionResist());
    }

    @Test
    void anUnknownOrNoneElementNameStoresNoProjection(@TempDir Path dir) throws IOException {
        // proj_name_to_idx returns -1 for both (the list has no NONE); PROJ_NONE stands in for -1.
        assertEquals(ProjectionEnum.PROJ_NONE, one(dir, "effect-type:element\nresist:BOGUS\n").getProjectionResist());
        assertEquals(ProjectionEnum.PROJ_NONE, one(dir, "effect-type:element\nresist:NONE\n").getProjectionResist());
    }

    @Test
    void aFlagResistLeavesTheProjectionNone(@TempDir Path dir) throws IOException {
        assertEquals(ProjectionEnum.PROJ_NONE, one(dir, "effect-type:flag\nresist:FREE_ACT\n").getProjectionResist());
    }

    // ---- Type A, Resist 1, Type B, Resist 2, Resist 3 ---------------------------------

    @Test
    void theLastResistUnderTheLastTypeIsTheOneKept(@TempDir Path dir) throws IOException {
        // C resolves each resist: against the type read before it, into one int that the next
        // resist: overwrites, so only Resist 3 survives, resolved under type B.
        BlowEffect toElement = one(dir,
                "effect-type:flag\nresist:FREE_ACT\neffect-type:element\nresist:FIRE\nresist:POIS\n");
        assertEquals(BlowEffectType.BET_ELEMENT, toElement.getEffectType());
        assertEquals(ElementEnum.ELEM_POIS, toElement.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_POIS, toElement.getProjectionResist());
        assertEquals(ObjectFlag.OF_NONE, toElement.getObjectFlagResist());

        BlowEffect toFlag = one(dir,
                "effect-type:element\nresist:FIRE\neffect-type:flag\nresist:FREE_ACT\nresist:SUST_STR\n");
        assertEquals(BlowEffectType.BET_FLAG, toFlag.getEffectType());
        assertEquals(ObjectFlag.OF_SUST_STR, toFlag.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, toFlag.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, toFlag.getProjectionResist());
    }

    @Test
    void anUnknownFlagAfterAnElementResistLeavesNoElementResistBehind(@TempDir Path dir) throws IOException {
        // C: the second resist: stores flag_index_by_name's -1 into the same int, so the earlier
        // FIRE is gone. The port must clear the projection field as well as the element field.
        BlowEffect e = one(dir, "effect-type:element\nresist:FIRE\neffect-type:flag\nresist:BOGUS\n");

        assertEquals(ObjectFlag.OF_NONE, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, e.getProjectionResist());
    }

    // ---- effect-type: changing after a resist: (accepted deviation, decided 261117) ----------
    // C keeps one int for the resist, and parse_eff_effect_type only replaces the type string, so
    // the number survives a later effect-type: and is read in the new type's list. The port keeps
    // a typed field per list, so effect-type: clears the fields that do not belong to the new type
    // (docs/Accepted_Deviations.md, Blow effect).

    /** Asserts the three resist fields all hold the port's "no resist" constants. */
    private static void assertNoResist(BlowEffect e, String message) {
        assertEquals(ObjectFlag.OF_NONE, e.getObjectFlagResist(), message);
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist(), message);
        assertEquals(ProjectionEnum.PROJ_NONE, e.getProjectionResist(), message);
    }

    @Test
    void aRepeatedElementTypeKeepsTheElementResist(@TempDir Path dir) throws IOException {
        // C: the second effect-type:element leaves the resist int at FIRE's place in proj_name_list,
        // which is still a valid element index under the same type. The port agrees.
        BlowEffect e = one(dir, "effect-type:element\nresist:FIRE\neffect-type:element\n");

        assertEquals(BlowEffectType.BET_ELEMENT, e.getEffectType());
        assertEquals(ElementEnum.ELEM_FIRE, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_FIRE, e.getProjectionResist());
        assertEquals(ObjectFlag.OF_NONE, e.getObjectFlagResist());
    }

    @Test
    void aRepeatedFlagTypeKeepsTheFlagResist(@TempDir Path dir) throws IOException {
        // C: the second effect-type:flag leaves the resist int at FREE_ACT's flag index, still a
        // valid flag index under the same type. The port agrees.
        BlowEffect e = one(dir, "effect-type:flag\nresist:FREE_ACT\neffect-type:flag\n");

        assertEquals(BlowEffectType.BET_FLAG, e.getEffectType());
        assertEquals(ObjectFlag.OF_FREE_ACT, e.getObjectFlagResist());
        assertEquals(ElementEnum.ELEM_NONE, e.getElementEnumResist());
        assertEquals(ProjectionEnum.PROJ_NONE, e.getProjectionResist());
    }

    @Test
    void anElementToFlagSwapClearsTheElementResistWhereCReadsItsNumberAsAFlag(@TempDir Path dir)
            throws IOException {
        // C: resist stays 2, FIRE's place in proj_name_list (ACID, ELEC, FIRE), and under flag that
        // is obj_flags[2], which is SUST_INT (NONE, SUST_STR, SUST_INT). The port stores no resist
        // at all, which is the saner result and the accepted deviation.
        BlowEffect e = one(dir, "effect-type:element\nresist:FIRE\neffect-type:flag\n");

        assertEquals(BlowEffectType.BET_FLAG, e.getEffectType());
        assertNoResist(e, "element to flag");
    }

    @Test
    void aFlagToElementSwapClearsTheFlagResistWhereCReadsItsNumberAsAProjection(@TempDir Path dir)
            throws IOException {
        // C: resist stays at FREE_ACT's flag index, and under element that is read as an index into
        // proj_name_list. The port stores no resist.
        BlowEffect e = one(dir, "effect-type:flag\nresist:FREE_ACT\neffect-type:element\n");

        assertEquals(BlowEffectType.BET_ELEMENT, e.getEffectType());
        assertNoResist(e, "flag to element");
    }

    @Test
    void aTypeThatTakesNoResistClearsEveryResist(@TempDir Path dir) throws IOException {
        // C keeps the number, but blow_color() in mon-lore.c reads blow->resist only in its element
        // and flag cases, so for these five types the number is never looked at. Clearing it in the
        // port therefore shows no difference.
        for (String type : List.of("drain", "theft", "eat-food", "eat-light", "all_sustains")) {
            BlowEffect fromElement = one(dir, "effect-type:element\nresist:FIRE\neffect-type:" + type + "\n");
            assertEquals(BlowEffectType.getFromString(type), fromElement.getEffectType(), type);
            assertNoResist(fromElement, "element to " + type);

            BlowEffect fromFlag = one(dir, "effect-type:flag\nresist:FREE_ACT\neffect-type:" + type + "\n");
            assertEquals(BlowEffectType.getFromString(type), fromFlag.getEffectType(), type);
            assertNoResist(fromFlag, "flag to " + type);
        }
    }

    @Test
    void anElementResistNamedNoneStoresNoResistWhereCStoresMinusOne(@TempDir Path dir) throws IOException {
        // proj_name_list has no NONE entry, so C stores proj_name_to_idx's -1. The port's NONE
        // constants are the typed form of that sentinel. Accepted deviation, decided 261117.
        assertNoResist(one(dir, "effect-type:element\nresist:NONE\n"), "element NONE");
    }

    @Test
    void lashTypeMaxFindsNoProjectionRecordSoTheLashIsNull(@TempDir Path dir) throws IOException {
        // proj_name_list ends with "MAX", so proj_name_to_idx("MAX") is PROJ_MAX, not -1, and C stores
        // PROJ_MAX as the lash type. projection.txt has no code:MAX record, so the port finds
        // nothing. Accepted deviation, decided 261117.
        assertNull(one(dir, "lash-type:MAX\n").getLashType());
    }

    @Test
    void lashTypeNoneIsMissileLikeAnyOtherNameCDoesNotKnow(@TempDir Path dir) throws IOException {
        // proj_name_list has no NONE entry, so proj_name_to_idx returns -1 and parse_eff_lash_type
        // stores PROJ_MISSILE. The port resolves NONE to PROJ_NONE first and maps that to missile.
        assertEquals(ProjectionEnum.PROJ_MISSILE, one(dir, "lash-type:NONE\n").getLashType().getProjection());
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

    /**
     * A {@code PROJ_}-prefixed name finds its projection, where C finds no such name. C's
     * {@code proj_name_list} holds bare names ({@code "FIRE"}), so {@code proj_name_to_idx("PROJ_FIRE")}
     * is -1 and {@code parse_eff_lash_type} stores {@code PROJ_MISSILE}. {@code ProjectionEnum.lookup}
     * puts {@code PROJ_} on a name that lacks it and takes one that has it, so the port stores FIRE.
     * The prefix test is case-sensitive, so {@code proj_fire} is an unknown name and is missile in
     * both. Accepted deviation, decided 261117.
     *
     * <p>Function aPrefixedLashNameFindsItsProjectionWhereCFallsBackToMissile coded on 261117,
     * commented in full on 261117.
     *
     * @param dir the temporary directory the data file is written to
     * @throws IOException if the file cannot be written or read
     */
    @Test
    void aPrefixedLashNameFindsItsProjectionWhereCFallsBackToMissile(@TempDir Path dir) throws IOException {
        assertEquals(ProjectionEnum.PROJ_FIRE, one(dir, "lash-type:PROJ_FIRE\n").getLashType().getProjection());
        assertEquals(ProjectionEnum.PROJ_MISSILE, one(dir, "lash-type:proj_fire\n").getLashType().getProjection());
    }

    /**
     * A {@code PROJ_}-prefixed element resist fills the projection field and leaves the element
     * field at none. C finds no {@code "PROJ_FIRE"} in {@code proj_name_list}, so
     * {@code parse_eff_resist} stores -1. The port's projection lookup takes the prefix, and its
     * element lookup then searches for {@code ELEM_PROJ_FIRE}, which no element has. Accepted
     * deviation, decided 261117.
     *
     * <p>Function aPrefixedElementResistFillsOnlyTheProjectionField coded on 261117, commented in
     * full on 261117.
     *
     * @param dir the temporary directory the data file is written to
     * @throws IOException if the file cannot be written or read
     */
    @Test
    void aPrefixedElementResistFillsOnlyTheProjectionField(@TempDir Path dir) throws IOException {
        BlowEffect fire = one(dir, "effect-type:element\nresist:PROJ_FIRE\n");

        assertEquals(ProjectionEnum.PROJ_FIRE, fire.getProjectionResist());
        assertEquals(ElementEnum.ELEM_NONE, fire.getElementEnumResist());
    }

    /**
     * Text after a second colon on a colour line is part of the value in the port and is dropped
     * in C. The three {@code lore-color-*} directives are {@code sym} fields, which {@code parser_parse}
     * cuts with {@code strtok(sp, ":")}, so {@code Light Green:x} reads {@code Light Green} and
     * {@code G:x} reads {@code G}. The port's lexer takes the rest of the line whole, so each is an
     * unknown name, which is white. Accepted deviation, decided 261117.
     *
     * <p>Function aColourFollowedByAColonAndTextIsWhiteWhereCDropsTheText coded on 261117, commented
     * in full on 261117.
     *
     * @param dir the temporary directory the data file is written to
     * @throws IOException if the file cannot be written or read
     */
    @Test
    void aColourFollowedByAColonAndTextIsWhiteWhereCDropsTheText(@TempDir Path dir) throws IOException {
        assertEquals(ColourEnum.COLOUR_WHITE, one(dir, "lore-color-base:Light Green:x\n").getLoreAttr());
        assertEquals(ColourEnum.COLOUR_WHITE, one(dir, "lore-color-base:G:x\n").getLoreAttr());
        assertEquals(ColourEnum.COLOUR_LIGHT_GREEN, one(dir, "lore-color-base:Light Green\n").getLoreAttr());
    }
}
