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
import uk.co.jackoftrades.middle.game.globals.GameConstants;
import uk.co.jackoftrades.middle.objects.Brand;
import uk.co.jackoftrades.middle.objects.Slay;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.player.PlayerTimedEffect;
import uk.co.jackoftrades.middle.player.TimedGrade;
import uk.co.jackoftrades.middle.player.enums.TimedEffect;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end throughput tests for the player-timed pipeline: file text -> {@code PlayerTimedLexer}/
 * {@code PlayerTimedGrammar} -> {@code PlayerTimedParseRecord} -> {@code PlayerTimedAssembler} ->
 * {@link PlayerTimedEffect}. These pin both the grammar (every directive shape, the optional
 * fifth grade field, the repeatable {@code fail:}) and the assembler (enum/brand/slay resolution,
 * the {@code flag-synonym} dup/exact split, {@code NONSTACKING}) as well as the two error channels
 * {@code GrammarDriver} threads through {@link ParseResult}: the soft skip-and-continue
 * {@code errors} list and the hard fail-closed channel.
 *
 * <p>The {@code ATT_*} statuses resolve a brand/slay code through {@link GameConstants}, so
 * {@link #seed()} loads {@code brand.txt}/{@code slay.txt} directly through their readers and injects
 * them into the private static registries by reflection, restoring them afterwards so no global
 * state leaks to other suites.
 *
 * @author Rowan Crowther
 */
class PlayerTimedReaderTest {

    private static final String REAL_FILE = "lib/gamedata/player_timed.txt";
    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";

    private static Object savedBrands;
    private static Object savedSlays;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seed() throws Exception {
        List<Brand> brands = new BrandReader().parseWithResults(BRAND_FILE).items();
        savedBrands = setStatic("brands", brands);
        List<Slay> slays = new SlayReader().parseWithResults(SLAY_FILE).items();
        savedSlays = setStatic("slays", slays);
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("brands", savedBrands);
        setStatic("slays", savedSlays);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = GameConstants.class.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    // ---- fixture helpers -------------------------------------------------

    private static String withHeader(int count, String body) {
        return "record-count:" + count + "\n" + body;
    }

    private ParseResult<PlayerTimedEffect> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new PlayerTimedReader().parseWithResults(file.toString());
    }

    private static PlayerTimedEffect byName(List<PlayerTimedEffect> effects, TimedEffect name) {
        return effects.stream().filter(e -> e.getName() == name).findFirst().orElse(null);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllFiftyThreeEffectsWithNoErrors() throws IOException {
        ParseResult<PlayerTimedEffect> result = new PlayerTimedReader().parseWithResults(REAL_FILE);

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(53, result.items().size());
    }

    @Test
    void gradesAreCountedAndOrdered() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        // FAST has a single grade; STUN three; CUT seven — the repeatable grade: line.
        assertEquals(1, byName(effects, TimedEffect.TMD_FAST).getGrade().size());
        assertEquals(3, byName(effects, TimedEffect.TMD_STUN).getGrade().size());
        assertEquals(7, byName(effects, TimedEffect.TMD_CUT).getGrade().size());
    }

    @Test
    void repeatedFailLinesAreAllRetained() throws IOException {
        // POISONED has two fail: lines (fail:2:POIS and fail:5:OPP_POIS); both must survive.
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        assertEquals(2, byName(effects, TimedEffect.TMD_POISONED).getFail().size());
        // SLOW has exactly one.
        assertEquals(1, byName(effects, TimedEffect.TMD_SLOW).getFail().size());
    }

    @Test
    void brandAndSlayCodesResolveToRegistryEntries() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        // FLAG coverage incl. digit-bearing codes: brand ACID_3, slay EVIL_2.
        Brand acidBrand = byName(effects, TimedEffect.TMD_ATT_ACID).getTempBrand();
        assertNotNull(acidBrand);
        assertEquals("ACID_3", acidBrand.getCode());

        Slay evilSlay = byName(effects, TimedEffect.TMD_ATT_EVIL).getTempSlay();
        assertNotNull(evilSlay);
        assertEquals("EVIL_2", evilSlay.getCode());

        // A status with neither leaves both null.
        assertNull(byName(effects, TimedEffect.TMD_FAST).getTempBrand());
        assertNull(byName(effects, TimedEffect.TMD_FAST).getTempSlay());
    }

    @Test
    void resistElementResolves() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        assertEquals(ElementEnum.ELEM_ACID, byName(effects, TimedEffect.TMD_OPP_ACID).getTempResist());
    }

    @Test
    void flagSynonymSplitsIntoDupFlagAndExactness() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        // AFRAID -> flag-synonym:AFRAID:1  => dup flag OF_AFRAID, exact synonym true.
        PlayerTimedEffect afraid = byName(effects, TimedEffect.TMD_AFRAID);
        assertEquals(ObjectFlag.OF_AFRAID, afraid.getoFlagSyn());
        assertTrue(afraid.isoFlagExactlySyn());

        // TERROR -> flag-synonym:AFRAID:0 => same dup flag, but NOT an exact synonym.
        PlayerTimedEffect terror = byName(effects, TimedEffect.TMD_TERROR);
        assertEquals(ObjectFlag.OF_AFRAID, terror.getoFlagSyn());
        assertFalse(terror.isoFlagExactlySyn());
    }

    @Test
    void nonStackingFlagIsRead() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        assertTrue(byName(effects, TimedEffect.TMD_PARALYZED).isNonStacking());
        assertFalse(byName(effects, TimedEffect.TMD_FAST).isNonStacking());
    }

    @Test
    void lowerBoundIsRead() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        // FOOD has lower-bound:1; a status without the directive defaults to 0.
        assertEquals(1, byName(effects, TimedEffect.TMD_FOOD).getLowerBound());
        assertEquals(0, byName(effects, TimedEffect.TMD_FAST).getLowerBound());
    }

    @Test
    void beginAndEndEffectsAreResolvedOnlyWhenPresent() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        // SCRAMBLE has both on-begin-effect and on-end-effect.
        PlayerTimedEffect scramble = byName(effects, TimedEffect.TMD_SCRAMBLE);
        assertNotNull(scramble.getOnBeginEffect());
        assertNotNull(scramble.getOnEndEffect());

        // Most statuses have neither; they must stay null (not crash, not a dropped record).
        PlayerTimedEffect fast = byName(effects, TimedEffect.TMD_FAST);
        assertNull(fast.getOnBeginEffect());
        assertNull(fast.getOnEndEffect());
    }

    @Test
    void gradeStatusLabelIsPreserved() throws IOException {
        // BLIND's single grade is grade:o:10000:Blind:You are blind. — the status-line label is a
        // real multi-character string and must be kept (C only nulls a *one-character* dummy label).
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        assertEquals("Blind", byName(effects, TimedEffect.TMD_BLIND).getGrade().get(0).status());
    }

    @Test
    void gradeUpAndDownMessagesGoToTheRightSlots() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        // BLIND: grade:o:10000:Blind:You are blind. — the fourth field is the message shown on
        // rising into the grade (up), and there is no down-message.
        TimedGrade blind = byName(effects, TimedEffect.TMD_BLIND).getGrade().get(0);
        assertEquals("You are blind.", blind.upMsg());
        assertNull(blind.downMsg());

        // FOOD's second grade carries both:
        //   grade:r:4:Faint:You are still faint.:You are getting faint from hunger!
        // field four (up) = "You are still faint.", field five (down) = "You are getting faint...".
        TimedGrade faint = byName(effects, TimedEffect.TMD_FOOD).getGrade().get(1);
        assertEquals("Faint", faint.status());
        assertEquals("You are still faint.", faint.upMsg());
        assertEquals("You are getting faint from hunger!", faint.downMsg());
    }

    // ---- Soft errors: dropped record (skip-and-continue) -----------------

    @Test
    void unknownTimedEffectNameIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-name.txt", withHeader(1,
                "name:NOTATIMEDEFFECT\ngrade:G:100:Test:Message\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid timed effect name")),
                result.errors()::toString);
    }

    @Test
    void unknownBrandIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-brand.txt", withHeader(1,
                "name:ATT_ACID\ngrade:G:100:Test:Message\nbrand:NOTAREALBRAND\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown brand")),
                result.errors()::toString);
    }

    @Test
    void unknownResistElementIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-resist.txt", withHeader(1,
                "name:OPP_ACID\ngrade:G:100:Test:Message\nresist:NOTREAL\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown resist element")),
                result.errors()::toString);
    }

    @Test
    void unknownMessageTypeIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-msgt.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nmsgt:NOTAMESSAGE\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown message type")),
                result.errors()::toString);
    }

    @Test
    void validAndInvalidRecordsInOneFileReportTheBadAndKeepTheGood() throws IOException {
        String body = "name:FAST\ngrade:G:100:Test:Message\n"
                + "name:NOTATIMEDEFFECT\ngrade:G:100:Test:Message\n";
        ParseResult<PlayerTimedEffect> result = load("mixed.txt", withHeader(2, body));

        assertEquals(1, result.items().size());
        assertEquals(TimedEffect.TMD_FAST, result.items().get(0).getName());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid timed effect name")),
                result.errors()::toString);
    }

    // ---- Grade arity: optional fifth field -------------------------------

    @Test
    void gradeWithNoDownMessageParses() throws IOException {
        // Four fields: colour:max:status:up — the down-message is omitted entirely.
        ParseResult<PlayerTimedEffect> result = load("four-field-grade.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Hasted:You speed up.\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    @Test
    void gradeWithEmptyTrailingDownMessageParses() throws IOException {
        // Five colons but an empty fifth field, as FOOD's 'Full' grade has it.
        ParseResult<PlayerTimedEffect> result = load("empty-down-grade.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Hasted:You speed up.:\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
    }

    // ---- record-count + hard (fail-closed) errors ------------------------

    @Test
    void recordCountMismatchIsReportedButValidRecordStillLoads() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-count.txt", withHeader(5,
                "name:FAST\ngrade:G:100:Test:Message\n"));

        assertEquals(1, result.items().size());
        assertTrue(result.hasErrors());
    }

    @Test
    void missingRecordCountHeaderIsAHardError() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("no-header.txt",
                "name:FAST\ngrade:G:100:Test:Message\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("no-name.txt", withHeader(1,
                "grade:G:100:Test:Message\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }
}
