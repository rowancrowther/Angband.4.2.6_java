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
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.registry.MonsterRegistry;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.monsters.Summon;
import uk.co.jackoftradesltd.middle.objects.ChestTrap;
import uk.co.jackoftradesltd.middle.objects.enums.ChestTrapCode;

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
 * <p>The whole-file contract is the thing most worth pinning: a structural failure ends the load
 * with a {@link DataFileParseException} from the assembler, as C's parser errors end the
 * {@code chest_trap.txt} load, rather than returning the records that happened to survive. The
 * order, count and level rules are the accepted deviation "{@code chest_trap.txt} must match
 * {@code ChestTrapCode} record for record" in {@code docs/Accepted_Deviations.md}: C checks none of
 * them. A valid prefix of the real file still ends the load, because a trap's pval bit is its
 * position and a partial load would leave {@link ChestTrapCode} constants with no trap.
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
        // C synthesises these while parsing (t->pval = h->pval * 2 in parse_chest_trap_name); here they
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
    void anUnknownCodeEndsTheLoad() throws IOException {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parse("bad-code.txt",
                FULL_FILE.replace("code:POISON", "code:NOT_A_TRAP")));

        assertTrue(e.getMessage().contains("invalid trap code value: NOT_A_TRAP"), e::getMessage);
    }

    @Test
    void aFirstRecordThatIsNotNoTrapEndsTheLoad() throws IOException {
        // pick_one_chest_trap draws from chest_traps->next, so record 0 must be the no-trap entry.
        String noLocked = FULL_FILE.replace("""
                name:locked
                code:NO_TRAP
                level:1
                
                """, "").replace("record-count:7", "record-count:6");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> parse("no-locked.txt", noLocked));

        assertTrue(e.getMessage().contains("should be NO_TRAP"), e::getMessage);
    }

    @Test
    void codesOutOfEnumOrderEndTheLoad() throws IOException {
        // Swapping two records keeps every code present but moves the bits: LOSE_CON would take
        // bit 4 and LOSE_STR bit 8. Silent in C; the load ends here.
        String swapped = FULL_FILE
                .replace("code:LOSE_STR", "code:PLACEHOLDER")
                .replace("code:LOSE_CON", "code:LOSE_STR")
                .replace("code:PLACEHOLDER", "code:LOSE_CON");

        DataFileParseException e = assertThrows(DataFileParseException.class,
                () -> parse("swapped.txt", swapped));

        assertTrue(e.getMessage().contains("LOSE_CON should be LOSE_STR"), e::getMessage);
    }

    @Test
    void aDuplicatedCodeEndsTheLoad() throws IOException {
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parse("duplicate.txt",
                FULL_FILE.replace("code:PARALYZE", "code:EXPLODE")));

        assertTrue(e.getMessage().contains("EXPLODE should be PARALYZE"), e::getMessage);
    }

    @Test
    void levelsThatDescendEndTheLoad() throws IOException {
        // The data file's rule 2: traps appear in ascending order of level. Equal levels are fine -
        // the real file has two at level 1 - so the fixture has to actually descend.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parse("descending.txt",
                FULL_FILE.replace("level:15", "level:2")));

        assertTrue(e.getMessage().contains("must not descend"), e::getMessage);
    }

    @Test
    void aValidPrefixOfTheFileEndsTheLoadAsIncomplete() throws IOException {
        // Two well-formed traps in the right order, but the file is short. Accepting it would
        // leave ChestTrapCode constants with no trap behind them, so the load ends.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parse("short.txt", """
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
                """));

        assertTrue(e.getMessage().contains("Expected: 7 received: 2"), e::getMessage);
    }

    @Test
    void anUnresolvableEffectEndsTheLoad() throws IOException {
        // EffectAssembler throws for the block, naming the effect's own line, which ends the load
        // as an error from grab_effect_data ends it in C. effect:TIMED_INC:POISONED is line 9 of
        // FULL_FILE: record-count is 1, then name, code, level, a blank line, name, code, level.
        DataFileParseException e = assertThrows(DataFileParseException.class, () -> parse("bad-effect.txt",
                FULL_FILE.replace("effect:TIMED_INC:POISONED", "effect:NOT_A_REAL_EFFECT")));

        assertTrue(e.getMessage().contains("line: 9")
                        && e.getMessage().contains("illegal element value: NOT_A_REAL_EFFECT"),
                e::getMessage);
    }

    // ---- Where this reader differs from C's chest_trap_parser --------------

    @Test
    void theUpstreamFileWithNoHeaderIsRefusedHereWhereCLoadsIt() throws IOException {
        // Upstream chest_trap.txt has no record-count: line; C's parser registers no such
        // directive. The grammar's file rule opens with recordCount, so the unmodified upstream
        // file is a hard error and loads as nothing.
        ParseResult<ChestTrap> result = new ChestTrapReader()
                .parseWithResults("/home/rowan/Desktop/Angband-4.2.6/lib/gamedata/chest_trap.txt");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aFileWithAHeaderAndNoTrapsIsRefusedHereWhereCLoadsAnEmptyList() throws IOException {
        // C's run_parse_chest_trap accepts a file with no name: lines and leaves chest_traps NULL.
        ParseResult<ChestTrap> result = parse("empty.txt", "record-count:0\n");

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aRepeatedMsgLineKeepsTheLastHereWhereCConcatenatesThem() throws IOException {
        // parse_chest_trap_msg does t->msg = string_append(t->msg, text), so C would hold
        // "A puff of green gas surrounds you!Twice." Here the later line overwrites the earlier.
        ParseResult<ChestTrap> result = parse("two-msg.txt",
                FULL_FILE.replace("msg:A puff of green gas surrounds you!",
                        "msg:A puff of green gas surrounds you!\nmsg:Twice."));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("Twice.", byCode(result.items(), ChestTrapCode.POISON).getMessage());
    }

    @Test
    void aRepeatedMsgDeathLineKeepsTheLastHereWhereCConcatenatesThem() throws IOException {
        // Same string_append in parse_chest_trap_msg_death: C would hold "a poison needlenow".
        ParseResult<ChestTrap> result = parse("two-death.txt",
                FULL_FILE.replaceFirst("msg-death:a poison needle",
                        "msg-death:a poison needle\nmsg-death:now"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("now", byCode(result.items(), ChestTrapCode.LOSE_STR).getMessageDeath());
    }

    @Test
    void aDiceLineBeforeAnyEffectIsRefusedHereWhereCIgnoresIt() throws IOException {
        // parse_chest_trap_dice returns PARSE_ERROR_NONE when the trap has no effect yet ("assume
        // that this is human and not parser error"), so C loads the record without the dice.
        ParseResult<ChestTrap> result = parse("stray-dice.txt",
                FULL_FILE.replace("level:1\n\nname:gas trap", "level:1\ndice:1d4\n\nname:gas trap"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aNonZeroDestroyOtherThanOneIsTrueAsInC() throws IOException {
        // parse_chest_trap_destroy sets t->destroy = true for any non-zero val.
        ParseResult<ChestTrap> result = parse("destroy-2.txt",
                FULL_FILE.replace("destroy:1", "destroy:2"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(byCode(result.items(), ChestTrapCode.EXPLODE).isDestroy());
    }

    @Test
    void aNonZeroMagicOtherThanOneIsTrueAsInC() throws IOException {
        // parse_chest_trap_magic, likewise: if (val) t->magic = true.
        ParseResult<ChestTrap> result = parse("magic-3.txt",
                FULL_FILE.replace("magic:1", "magic:3"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(byCode(result.items(), ChestTrapCode.SUMMON).isMagic());
    }

    @Test
    void destroyAndMagicOfZeroAreFalseAsInC() throws IOException {
        ParseResult<ChestTrap> result = parse("zero-flags.txt",
                FULL_FILE.replace("destroy:1", "destroy:0").replace("magic:1", "magic:0"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().stream().noneMatch(t -> t.isDestroy() || t.isMagic()));
    }

    @Test
    void aHexadecimalLevelIsRefusedHereWhereCReadsBaseZero() throws IOException {
        // parser_getint is strtol in base zero, so C reads level:0x19 as 25.
        ParseResult<ChestTrap> result = parse("hex-level.txt",
                FULL_FILE.replace("level:25", "level:0x19"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void aDirectiveBeforeAnyNameIsRefusedAsInC() throws IOException {
        // C answers PARSE_ERROR_MISSING_RECORD_HEADER for every directive but name:.
        ParseResult<ChestTrap> result = parse("no-name.txt",
                "record-count:7\nlevel:1\n" + FULL_FILE.replace("record-count:7\n", ""));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    // ---- The shipped file against the upstream table ------------------------

    @Test
    void everyRecordAgreesWithTheUpstreamFile() throws IOException {
        // Values read off the upstream chest_trap.txt, one row per record:
        // name, code, level, effect count, destroy, magic, msg, msg-death.
        List<ChestTrap> traps = loadReal();

        assertEquals(7, traps.size());
        assertTrap(traps.get(0), "locked", ChestTrapCode.NO_TRAP, 1, 0, false, false, "", "");
        assertTrap(traps.get(1), "gas trap", ChestTrapCode.POISON, 1, 1, false, false,
                "A puff of green gas surrounds you!", "");
        assertTrap(traps.get(2), "poison needle", ChestTrapCode.LOSE_STR, 2, 2, false, false,
                "A small needle has pricked you!", "a poison needle");
        assertTrap(traps.get(3), "poison needle", ChestTrapCode.LOSE_CON, 3, 2, false, false,
                "A small needle has pricked you!", "a poison needle");
        assertTrap(traps.get(4), "summoning runes", ChestTrapCode.SUMMON, 15, 1, false, true,
                "You are enveloped in a cloud of smoke!", "");
        assertTrap(traps.get(5), "gas trap", ChestTrapCode.PARALYZE, 19, 1, false, false,
                "A puff of yellow gas surrounds you!", "");
        assertTrap(traps.get(6), "explosion device", ChestTrapCode.EXPLODE, 25, 1, true, false,
                "There is a sudden explosion! Everything inside the chest is destroyed!",
                "an exploding chest");
    }

    private static void assertTrap(ChestTrap trap, String name, ChestTrapCode code, int level,
                                   int effects, boolean destroy, boolean magic, String msg,
                                   String msgDeath) {
        assertEquals(name, trap.getName());
        assertEquals(code, trap.getCode());
        assertEquals(level, trap.getLevel());
        assertEquals(effects, trap.getEffect().size());
        assertEquals(destroy, trap.isDestroy());
        assertEquals(magic, trap.isMagic());
        assertEquals(msg, trap.getMessage());
        assertEquals(msgDeath, trap.getMessageDeath());
    }

    // ---- Reader surface -------------------------------------------------------

    @Test
    void parseReturnsTheSameTrapsAsParseWithResults() throws IOException {
        List<ChestTrap> viaParse = new ChestTrapReader().parse(REAL_FILE);

        assertEquals(7, viaParse.size());
        assertEquals(loadReal().stream().map(ChestTrap::getCode).toList(),
                viaParse.stream().map(ChestTrap::getCode).toList());
    }

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        // run_parse_chest_trap uses parse_file_quit_not_found.
        assertThrows(IOException.class, () ->
                new ChestTrapReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void aNonNumericRecordCountIsAHardError() throws IOException {
        ParseResult<ChestTrap> result = parse("bad-header.txt",
                FULL_FILE.replace("record-count:7", "record-count:abc"));

        assertTrue(result.hasErrors());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void theSummonTrapNeedsSummonsLoadedFirst() throws Exception {
        // C runs summon_parser before chest_trap_parser in init_arrays; running this reader first
        // throws out of MonsterRegistry.lookupSummon rather than reporting a soft error.
        Object loaded = setStatic("summons", null);
        try {
            assertThrows(IllegalStateException.class, () -> parse("early.txt", FULL_FILE));
        } finally {
            setStatic("summons", loaded);
        }
    }
}
