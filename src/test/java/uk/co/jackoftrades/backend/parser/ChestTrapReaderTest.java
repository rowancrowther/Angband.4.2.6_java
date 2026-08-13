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
import uk.co.jackoftrades.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftrades.middle.monsters.MonsterBase;
import uk.co.jackoftrades.middle.monsters.MonsterPain;
import uk.co.jackoftrades.middle.monsters.Summon;
import uk.co.jackoftrades.middle.objects.ChestTrap;
import uk.co.jackoftrades.middle.objects.enums.ChestTrapCode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for {@link ChestTrapReader}: file text -> {@code ChestTrapLexer}/
 * {@code ChestTrapGrammar} -> {@code ChestTrapParseRecord} -> {@code ChestTrapAssembler} ->
 * {@link ChestTrap}, the exact chain {@code ObjectDataLoader.loadChestTraps()} drives.
 *
 * <p>These pin both halves. On the grammar side: that {@code code:}, {@code msg:} and
 * {@code msg-death:} take the rest of the line whole (so punctuation and embedded colons survive),
 * that a record may repeat {@code effect:} and that the following {@code dice:} binds to the block
 * above it, and that {@code code:} is required where every other directive is optional. On the
 * assembler side: the pval bits, and the four structural rules {@code chest_trap.txt} states only in
 * prose - first record is the no-trap, codes match {@link ChestTrapCode} positionally, levels
 * ascend, and the file must be complete.
 *
 * <p>The whole-file contract is the thing most worth pinning: unlike the other readers here, a
 * structural failure rejects the <em>file</em>, so the result is an empty list plus errors rather
 * than the records that happened to survive. Several fixtures below are valid prefixes of the real
 * file and are still rejected; that is the intended behaviour, because a trap's pval bit is its
 * position and a partial load would silently renumber nothing but would leave gaps.
 *
 * <p>The shipped {@code chest_trap.txt} uses an {@code EF_SUMMON} effect (the summoning runes),
 * which {@code EffectAssembler} resolves via {@link MonsterRegistry#lookupSummon}; loading summons
 * needs monster bases, which need monster pains. Rather than run the whole heavy {@code init()},
 * {@link #seed()} loads {@code pain.txt}, {@code monster_base.txt} and {@code summon.txt} through
 * their readers and injects them into the private static registries by reflection, restoring them
 * afterwards so no global state leaks to other suites.
 *
 * @author Rowan Crowther
 */
class ChestTrapReaderTest {

    private static final String REAL_FILE = "lib/gamedata/chest_trap.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";
    private static final String SUMMON_FILE = "lib/gamedata/summon.txt";

    /**
     * The real file, verbatim, as the base for fixtures that alter one thing about it. Kept here
     * rather than read from disk so a test that mutates it cannot depend on the file it is meant to
     * be varying from.
     */
    private static final String FULL_FILE = """
            record-count:7
            name:locked
            code:NO_TRAP
            level:1
            
            name:gas trap
            code:POISON
            level:1
            effect:TIMED_INC:POISONED
            dice:10+d20
            msg:A puff of green gas surrounds you!
            
            name:poison needle
            code:LOSE_STR
            level:2
            effect:DAMAGE
            dice:1d4
            effect:DRAIN_STAT:STR
            msg:A small needle has pricked you!
            msg-death:a poison needle
            
            name:poison needle
            code:LOSE_CON
            level:3
            effect:DAMAGE
            dice:1d4
            effect:DRAIN_STAT:CON
            msg:A small needle has pricked you!
            msg-death:a poison needle
            
            name:summoning runes
            code:SUMMON
            level:15
            effect:SUMMON:ANY
            dice:2+1d3
            magic:1
            msg:You are enveloped in a cloud of smoke!
            
            name:gas trap
            code:PARALYZE
            level:19
            effect:TIMED_INC:PARALYZED
            dice:10+d20
            msg:A puff of yellow gas surrounds you!
            
            name:explosion device
            code:EXPLODE
            level:25
            effect:DAMAGE
            dice:5d8
            destroy:1
            msg:There is a sudden explosion! Everything inside the chest is destroyed!
            msg-death:an exploding chest
            """;

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
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    private static List<ChestTrap> loadReal() throws IOException {
        return new ChestTrapReader().parseWithResults(REAL_FILE).items();
    }

    private static ChestTrap byCode(List<ChestTrap> traps, ChestTrapCode code) {
        return traps.stream()
                .filter(t -> t.getCode() == code)
                .findFirst().orElse(null);
    }

    private String tempFile(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return file.toString();
    }

    private ParseResult<ChestTrap> parse(String name, String content) throws IOException {
        return new ChestTrapReader().parseWithResults(tempFile(name, content));
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAll7TrapsWithNoErrors() throws IOException {
        ParseResult<ChestTrap> result = new ChestTrapReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(7, result.items().size());
    }

    @Test
    void codesAppearInEnumOrderOnTheRealFile() throws IOException {
        // The order is load-bearing: it is what the pval bit is derived from.
        assertEquals(List.of(ChestTrapCode.values()),
                loadReal().stream().map(ChestTrap::getCode).collect(Collectors.toList()));
    }

    @Test
    void pvalsAreOneBitPerTrapInFileOrder() throws IOException {
        // C synthesises these while parsing (t->pval = h->pval * 2, obj-chest.c:64-72); here they
        // come from ChestTrapCode as 1 << ordinal(). Same numbers either way.
        List<ChestTrap> traps = loadReal();

        assertEquals(1, byCode(traps, ChestTrapCode.NO_TRAP).getPVal());
        assertEquals(2, byCode(traps, ChestTrapCode.POISON).getPVal());
        assertEquals(4, byCode(traps, ChestTrapCode.LOSE_STR).getPVal());
        assertEquals(8, byCode(traps, ChestTrapCode.LOSE_CON).getPVal());
        assertEquals(16, byCode(traps, ChestTrapCode.SUMMON).getPVal());
        assertEquals(32, byCode(traps, ChestTrapCode.PARALYZE).getPVal());
        assertEquals(64, byCode(traps, ChestTrapCode.EXPLODE).getPVal());

        // No two traps share a bit, and no bit is the sign bit of C's int16 pval.
        int all = traps.stream().mapToInt(ChestTrap::getPVal).reduce(0, (a, b) -> a | b);
        assertEquals(127, all);
    }

    @Test
    void namesAreNotUniqueButCodesAre() throws IOException {
        // Two traps are "gas trap" and two "poison needle" - the reason nothing may key on name.
        List<ChestTrap> traps = loadReal();

        assertEquals(5, traps.stream().map(ChestTrap::getName).distinct().count());
        assertEquals(7, traps.stream().map(ChestTrap::getCode).distinct().count());
    }

    @Test
    void levelsAreTakenFromTheFile() throws IOException {
        List<ChestTrap> traps = loadReal();

        assertEquals(1, byCode(traps, ChestTrapCode.NO_TRAP).getLevel());
        assertEquals(2, byCode(traps, ChestTrapCode.LOSE_STR).getLevel());
        assertEquals(15, byCode(traps, ChestTrapCode.SUMMON).getLevel());
        assertEquals(25, byCode(traps, ChestTrapCode.EXPLODE).getLevel());
    }

    @Test
    void repeatedEffectLinesAccumulateIntoOneTrap() throws IOException {
        // Both poison needles are DAMAGE followed by DRAIN_STAT - two effect: lines in one record,
        // with the dice: line between them binding to the first, as C's walk-to-tail does.
        List<ChestTrap> traps = loadReal();

        assertEquals(2, byCode(traps, ChestTrapCode.LOSE_STR).getEffect().size());
        assertEquals(2, byCode(traps, ChestTrapCode.LOSE_CON).getEffect().size());
        assertEquals(1, byCode(traps, ChestTrapCode.EXPLODE).getEffect().size());
    }

    @Test
    void theLockedEntryHasNoEffectAndNoMessages() throws IOException {
        // Record 0 exists only to occupy pval bit 1; it is the "locked but untrapped" chest.
        ChestTrap locked = byCode(loadReal(), ChestTrapCode.NO_TRAP);

        assertTrue(locked.getEffect().isEmpty());
        assertEquals("", locked.getMessage());
        assertEquals("", locked.getMessageDeath());
        assertFalse(locked.isDestroy());
        assertFalse(locked.isMagic());
    }

    @Test
    void destroyAndMagicAreSetOnlyWhereTheFileSaysSo() throws IOException {
        List<ChestTrap> traps = loadReal();

        // destroy:1 appears once, on the explosion device.
        assertEquals(List.of(ChestTrapCode.EXPLODE), traps.stream()
                .filter(ChestTrap::isDestroy).map(ChestTrap::getCode).toList());

        // magic:1 appears once, on the summoning runes.
        assertEquals(List.of(ChestTrapCode.SUMMON), traps.stream()
                .filter(ChestTrap::isMagic).map(ChestTrap::getCode).toList());
    }

    @Test
    void messagesKeepTheirPunctuationAndSpacing() throws IOException {
        // msg: pushes REST_OF_LINE, so the value is one token: spaces, '!' and all.
        List<ChestTrap> traps = loadReal();

        assertEquals("There is a sudden explosion! Everything inside the chest is destroyed!",
                byCode(traps, ChestTrapCode.EXPLODE).getMessage());
        assertEquals("an exploding chest", byCode(traps, ChestTrapCode.EXPLODE).getMessageDeath());
        assertEquals("A puff of green gas surrounds you!",
                byCode(traps, ChestTrapCode.POISON).getMessage());

        // A record with no msg-death: gets "" from the grammar's @init, never null.
        assertEquals("", byCode(traps, ChestTrapCode.POISON).getMessageDeath());
    }

    // ---- Grammar-level behaviours (synthetic fixtures) --------------------

    @Test
    void messageTextMayContainColonsAndIsNotSplit() throws IOException {
        // The directive prefix is matched, then the rest of the line is one STRING - so a colon in
        // the message is just text, not a field separator.
        String path = tempFile("colons.txt",
                FULL_FILE.replace("msg:A puff of green gas surrounds you!",
                        "msg:Warning: gas! Run: now"));

        ParseResult<ChestTrap> result = new ChestTrapReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("Warning: gas! Run: now",
                byCode(result.items(), ChestTrapCode.POISON).getMessage());
    }

    @Test
    void commentsAndBlankLinesAreSkipped() throws IOException {
        String path = tempFile("comments.txt",
                "# leading comment\n\n" + FULL_FILE.replace("name:locked",
                        "# a comment mid-file\n\nname:locked"));

        ParseResult<ChestTrap> result = new ChestTrapReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(7, result.items().size());
    }

    @Test
    void directivesMayAppearInAnyOrderWithinARecord() throws IOException {
        // Everything after code: is an order-free repetition, matching C's per-directive parser.
        String path = tempFile("reordered.txt", FULL_FILE.replace("""
                level:25
                effect:DAMAGE
                dice:5d8
                destroy:1""", """
                destroy:1
                effect:DAMAGE
                dice:5d8
                level:25"""));

        ParseResult<ChestTrap> result = new ChestTrapReader().parseWithResults(path);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        ChestTrap explode = byCode(result.items(), ChestTrapCode.EXPLODE);
        assertEquals(25, explode.getLevel());
        assertTrue(explode.isDestroy());
        assertEquals(1, explode.getEffect().size());
    }

    @Test
    void aMissingCodeLineIsAHardParseErrorAndRejectsTheFile() throws IOException {
        // code: is the one directive the grammar requires after name:, because this port cannot
        // identify a trap without it - where C would carry on with a null code string.
        ParseResult<ChestTrap> result = parse("no-code.txt",
                FULL_FILE.replace("code:POISON\n", ""));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("code:")),
                () -> result.errors().toString());
    }

    @Test
    void recordCountMismatchIsASoftError() throws IOException {
        // record-count: is this port's addition; C declares no total for chest_trap.txt.
        ParseResult<ChestTrap> result = parse("bad-count.txt",
                FULL_FILE.replace("record-count:7", "record-count:9"));

        assertTrue(result.hasErrors());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("record-count") && e.contains("9")),
                () -> result.errors().toString());
    }

    // ---- Structural validation: the file is accepted or rejected whole ----

    @Test
    void anUnknownCodeIsReportedAndRejectsTheFile() throws IOException {
        ParseResult<ChestTrap> result = parse("bad-code.txt",
                FULL_FILE.replace("code:POISON", "code:NOT_A_TRAP"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("invalid trap code") && e.contains("NOT_A_TRAP")),
                () -> result.errors().toString());
    }

    @Test
    void aFirstRecordThatIsNotNoTrapIsRejected() throws IOException {
        // pick_one_chest_trap draws from chest_traps->next, so record 0 must be the no-trap entry.
        ParseResult<ChestTrap> result = parse("no-locked.txt",
                FULL_FILE.replace("""
                        name:locked
                        code:NO_TRAP
                        level:1
                        
                        """, "").replace("record-count:7", "record-count:6"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("NO_TRAP")),
                () -> result.errors().toString());
    }

    @Test
    void codesOutOfEnumOrderAreRejected() throws IOException {
        // Swapping two records keeps every code present but moves the bits: LOSE_CON would take
        // bit 4 and LOSE_STR bit 8. Silent in C; an error here.
        String swapped = FULL_FILE
                .replace("code:LOSE_STR", "code:PLACEHOLDER")
                .replace("code:LOSE_CON", "code:LOSE_STR")
                .replace("code:PLACEHOLDER", "code:LOSE_CON");

        ParseResult<ChestTrap> result = parse("swapped.txt", swapped);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream()
                        .anyMatch(e -> e.contains("LOSE_STR") || e.contains("LOSE_CON")),
                () -> result.errors().toString());
    }

    @Test
    void aDuplicatedCodeIsRejected() throws IOException {
        ParseResult<ChestTrap> result = parse("duplicate.txt",
                FULL_FILE.replace("code:PARALYZE", "code:EXPLODE"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void levelsThatDescendAreRejected() throws IOException {
        // The data file's rule 2: traps appear in ascending order of level. Equal levels are fine -
        // the real file has two at level 1 - so the fixture has to actually descend.
        ParseResult<ChestTrap> result = parse("descending.txt",
                FULL_FILE.replace("level:15", "level:2"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("level")),
                () -> result.errors().toString());
    }

    @Test
    void aValidPrefixOfTheFileIsStillRejectedAsIncomplete() throws IOException {
        // Three well-formed traps in the right order, but the file is short. Accepting it would
        // leave ChestTrapCode constants with no trap behind them, so it is rejected whole.
        ParseResult<ChestTrap> result = parse("short.txt", """
                record-count:2
                name:locked
                code:NO_TRAP
                level:1
                
                name:gas trap
                code:POISON
                level:1
                effect:TIMED_INC:POISONED
                dice:10+d20
                msg:A puff of green gas surrounds you!
                """);

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("expected 7")),
                () -> result.errors().toString());
    }

    @Test
    void anUnresolvableEffectDropsTheRecordAndSoRejectsTheFile() throws IOException {
        // EffectAssembler's all-or-nothing contract drops the owning record; that record's absence
        // then fails the completeness check, so one bad effect costs the whole file.
        ParseResult<ChestTrap> result = parse("bad-effect.txt",
                FULL_FILE.replace("effect:TIMED_INC:POISONED", "effect:NOT_A_REAL_EFFECT"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("NOT_A_REAL_EFFECT")),
                () -> result.errors().toString());
    }
}
