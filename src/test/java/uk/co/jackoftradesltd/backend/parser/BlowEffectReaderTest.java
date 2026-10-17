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
 * The assembler is where the C semantics have to be reproduced by hand, and all three
 * seams are exercised here:
 * <ul>
 *   <li>{@code lash-type:} is resolved through [C] {@code proj_name_to_idx}
 *       ({@code project.c:60}), which searches the merged element-then-projection name
 *       list. An unresolvable name is <em>not</em> an error: [C] {@code mon-init.c:409}
 *       falls back to {@code PROJ_MISSILE}. The shipped file relies on this - SHATTER
 *       says {@code SHARDS} but the projection code is {@code SHARD}.</li>
 *   <li>{@code resist:} is polymorphic on {@code effect-type:} ([C]
 *       {@code mon-init.c:388-400}): {@code element} resolves against the element list,
 *       {@code flag} against the object flags, and anything else is
 *       {@code PARSE_ERROR_MISSING_BLOW_EFF_TYPE}.</li>
 *   <li>Records with no {@code effect-type:} at all are legal and keep the "none"
 *       sentinels on both resist fields.</li>
 * </ul>
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
        assertTrue(realFile().stream()
                        .filter(e -> !e.getName().equals("NONE"))
                        .allMatch(e -> e.getLashType() != null),
                "every effect except NONE ships a lash-type");
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
        // [C] mon-init.c:409 - `type >= 0 ? type : PROJ_MISSILE`. SHATTER says lash-type:SHARDS
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
                "the element slot stays empty for a flag resist");
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
        assertEquals(ObjectFlag.OF_NONE, loseAll.getObjectFlagResist());
    }

    @Test
    void recordWithNoEffectTypeKeepsNullTypeAndEmptyResists() throws IOException {
        // Eight records ship no effect-type: line at all - NONE is the first record in the file,
        // so this path is hit immediately on load.
        BlowEffect none = byName(realFile(), "NONE");

        assertNull(none.getEffectType());
        assertEquals(ElementEnum.ELEM_NONE, none.getElementEnumResist());
        assertEquals(ObjectFlag.OF_NONE, none.getObjectFlagResist());
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
    void aBadRecordIsSkippedAndTheRestStillLoadInOrder(@TempDir Path dir) throws IOException {
        // C stops at the first parse error; the port's partial-results contract keeps going. A
        // resist: with no effect-type: is rejected by the assembler (not the grammar), so it is
        // the soft channel that carries it.
        String file = write(dir, "record-count:3\n\nname:NONE\npower:0\n\nname:HURT\nresist:POIS\n\nname:POISON\npower:20\n");

        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);

        assertEquals(List.of("NONE", "POISON"), result.items().stream().map(BlowEffect::getName).toList());
        assertEquals(1, result.errors().size());
        assertTrue(result.errors().get(0).contains("POIS"), result.errors().toString());
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
    void resistWithNoEffectTypeIsASoftErrorAndTheRecordIsSkipped(@TempDir Path dir) throws IOException {
        // C: PARSE_ERROR_MISSING_BLOW_EFF_TYPE.
        String file = write(dir, "record-count:1\n\nname:POISON\nresist:POIS\n");

        ParseResult<BlowEffect> result = new BlowEffectReader().parseWithResults(file);

        assertTrue(result.items().isEmpty());
        assertEquals(1, result.errors().size());
    }
}
