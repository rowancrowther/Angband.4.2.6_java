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
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftrades.middle.cave.TrapKind;
import uk.co.jackoftrades.middle.enums.TrapEnum;
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link TrapReader}: file text -> {@code TrapLexer}/
 * {@code TrapGrammar} -> {@code TrapParseRecord} -> {@code TrapAssembler} -> {@link TrapKind},
 * the exact chain {@code GameConstants.loadTraps()} drives.
 *
 * <p>These pin the assembler half: the {@code name:}/{@code desc:} crossover (C wires the name
 * line's second field into {@code desc}/our {@code description} and the {@code desc:} directive
 * into {@code text}), that {@code save:} resolves to <em>object</em> flags while {@code flags:}
 * resolves to <em>trap</em> flags, that repeated {@code effect:}/{@code effect-xtra:} lines
 * accumulate into lists, that {@code trapKindIndex} is the file position, and both error channels.
 *
 * <p>The shipped {@code trap.txt} uses {@code EF_SUMMON} effects (the summoning runes), which
 * {@code EffectAssembler} resolves via {@link GameConstants#lookupSummon}; loading summons needs
 * monster bases, which need monster pains. Rather than run the whole heavy {@code init()},
 * {@link #seed()} loads {@code pain.txt}, {@code monster_base.txt} and {@code summon.txt} through
 * their readers and injects them into the private static registries by reflection, restoring them
 * afterwards so no global state leaks to other suites.
 *
 * @author Rowan Crowther
 */
class TrapReaderTest {

    private static final String REAL_FILE = "lib/gamedata/trap.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";

    private static Object savedPains;
    private static Object savedBases;
    private static Object savedSummons;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seed() throws Exception {
        // Order matters: summons resolve monster bases, bases resolve pains.
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        savedPains = setStatic("monsterPains", pains);

        List<MonsterBase> bases = new MonsterBaseReader().parseWithResults(BASE_FILE).items();
        savedBases = setStatic("monsterBases", bases);

        List<Summon> summons = new SummonReader().parseWithResults(SUMMON_FILE).items();
        savedSummons = setStatic("summons", summons);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
        setStatic("summons", savedSummons);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static List<TrapKind> loadReal() throws IOException {
        return new TrapReader().parseWithResults(REAL_FILE).items();
    }

    private static TrapKind byDescription(List<TrapKind> traps, String description) {
        return traps.stream()
                .filter(t -> description.equals(t.getDescription()))
                .findFirst().orElse(null);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAll40TrapsWithNoErrors() throws IOException {
        ParseResult<TrapKind> result = new TrapReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(40, result.items().size());
    }

    @Test
    void crossoverDescriptionsResolveOnTheRealFile() throws IOException {
        // description <- name:'s 2nd field; the three "pit" records share field 1 but keep
        // distinct descriptions, so resolving them by description proves it is field 2, not field 1.
        List<TrapKind> traps = loadReal();

        assertNotNull(byDescription(traps, "trap door"));
        assertNotNull(byDescription(traps, "glyph of warding"));
        assertNotNull(byDescription(traps, "pit"));
        assertNotNull(byDescription(traps, "spiked pit"));
        assertNotNull(byDescription(traps, "poison pit"));
        assertNotNull(byDescription(traps, "no trap"));
    }

    @Test
    void textHoldsDescDirectiveWhileDescriptionHoldsNameField() throws IOException {
        // The crossover, the other way round: text <- desc: directive, description <- name: field 2.
        List<TrapKind> traps = loadReal();

        TrapKind trapDoor = byDescription(traps, "trap door");
        assertEquals("trap door", trapDoor.getDescription());
        assertEquals("It looks like floor, but step incautiously and you will fall through.",
                trapDoor.getText());

        // "no trap" carries no desc: line, so text accumulates to the empty string.
        assertEquals("", byDescription(traps, "no trap").getText());
    }

    @Test
    void saveFlagsAreObjectFlagsAndTrapFlagsAreSeparate() throws IOException {
        // trap door: flags:TRAP | FLOOR | DOWN  and  save:FEATHER.
        TrapKind trapDoor = byDescription(loadReal(), "trap door");

        // save: -> object flags (OF_), not trap flags.
        assertTrue(trapDoor.getSaveFlags().has(ObjectFlag.OF_FEATHER));

        // flags: -> trap flags (TRF_); the DOWN/FLOOR/TRAP set is present, MAGICAL is not.
        assertTrue(trapDoor.getFlags().has(TrapEnum.TRF_TRAP));
        assertTrue(trapDoor.getFlags().has(TrapEnum.TRF_FLOOR));
        assertTrue(trapDoor.getFlags().has(TrapEnum.TRF_DOWN));
        assertFalse(trapDoor.getFlags().has(TrapEnum.TRF_MAGICAL));
    }

    @Test
    void effectsAndXtraEffectsAccumulatePerRecord() throws IOException {
        List<TrapKind> traps = loadReal();

        // blast trap: four back-to-back effect: lines (LIGHT/SOUND/FIRE/FORCE), no effect-xtra.
        TrapKind blast = byDescription(traps, "blast trap");
        assertEquals(4, blast.getEffect().size());
        assertEquals(0, blast.getEffectXtra().size());

        // brain smashing trap: DAMAGE + four TIMED_INC effects.
        assertEquals(5, byDescription(traps, "brain smashing trap").getEffect().size());

        // spiked pit: one primary effect, two effect-xtra (DAMAGE, TIMED_INC:CUT).
        TrapKind spiked = byDescription(traps, "spiked pit");
        assertEquals(1, spiked.getEffect().size());
        assertEquals(2, spiked.getEffectXtra().size());
    }

    @Test
    void trapKindIndicesAreDistinctAndFileOrdered() throws IOException {
        List<TrapKind> traps = loadReal();

        Set<Integer> indices = traps.stream()
                .map(TrapKind::getTrapKindIndex)
                .collect(Collectors.toSet());
        assertEquals(40, indices.size(), "indices must be distinct");
        assertEquals(0, Collections.min(indices));
        assertEquals(39, Collections.max(indices));

        // File position, 0-based: no trap first, glyph of warding second, trap door sixth.
        assertEquals(0, byDescription(traps, "no trap").getTrapKindIndex());
        assertEquals(1, byDescription(traps, "glyph of warding").getTrapKindIndex());
        assertEquals(5, byDescription(traps, "trap door").getTrapKindIndex());
    }

    // ---- Soft-error drops (synthetic fixtures) ---------------------------

    @Test
    void unknownTrapFlagIsSoftErrorAndDropsTheRecord() throws IOException {
        String path = tempFile("bad-flag.txt",
                "record-count:1\n" +
                        "name:test:test trap\n" +
                        "graphics:^:w\n" +
                        "flags:TRAP | NOTAFLAG\n");

        ParseResult<TrapKind> result = new TrapReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("unknown flag") && e.contains("NOTAFLAG")),
                () -> result.errors().toString());
    }

    @Test
    void unknownSaveFlagIsSoftErrorAndDropsTheRecord() throws IOException {
        String path = tempFile("bad-save.txt",
                "record-count:1\n" +
                        "name:test:test trap\n" +
                        "graphics:^:w\n" +
                        "save:NOTAFLAG\n");

        ParseResult<TrapKind> result = new TrapReader().parseWithResults(path);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("unknown save flag") && e.contains("NOTAFLAG")),
                () -> result.errors().toString());
    }
}
