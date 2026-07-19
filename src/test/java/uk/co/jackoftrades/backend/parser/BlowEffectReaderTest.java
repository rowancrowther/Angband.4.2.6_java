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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.combat.enums.ProjectionEnum;
import uk.co.jackoftrades.middle.game.Projection;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.BlowEffect;
import uk.co.jackoftrades.middle.monsters.enums.BlowEffectType;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.io.IOException;
import java.lang.reflect.Field;
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
     * {@link GameConstants#lookupProjectionByLash}, so the projection table has to be
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
     * Overwrite a private static on {@link GameConstants}, returning the previous value so
     * {@link #restore()} can put the real table back and leave the suite's shared statics
     * as they were found.
     */
    private static Object setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
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
}
