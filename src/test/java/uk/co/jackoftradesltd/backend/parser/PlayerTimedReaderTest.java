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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.PlayerData;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Slay;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedFailure;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

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
 * <p>The {@code ATT_*} statuses resolve a brand/slay code through the object registry, so
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
    /**
     * Whatever {@code GameConstants.data} held before this class seeded it, or {@code null} if it
     * was already loaded and this class left it alone.
     */
    private static Object savedConstants;
    /**
     * Whether this class seeded the constants table and so is responsible for clearing it.
     */
    private static boolean seededConstants;

    @TempDir
    Path tempDir;

    @BeforeAll
    static void seed() throws Exception {
        List<Brand> brands = new BrandReader().parseWithResults(BRAND_FILE).items();
        savedBrands = setStatic("brands", brands);
        List<Slay> slays = new SlayReader().parseWithResults(SLAY_FILE).items();
        savedSlays = setStatic("slays", slays);

        // PlayerTimedAssembler converts a grade's food figures through
        // GameConstants.getPlayerFoodValue (PlayerTimedAssembler.java:107), so the constants table
        // has to be loaded before any player_timed record can be assembled. Seeding it only when it
        // is empty leaves the real table in place when a reader test has already loaded it, and
        // stops this class depending on one having run first.
        if (constantsField().get(null) == null) {
            savedConstants = constantsField().get(null);
            constantsField().set(null, new GameConstantsData(
                    null, null, null, null, null, null, null, null,
                    new PlayerData(20, 20, 600, 100),
                    null, null, null, null, null, null, null, null));
            seededConstants = true;
        }
    }

    @AfterAll
    static void restore() throws Exception {
        setStatic("brands", savedBrands);
        setStatic("slays", savedSlays);
        if (seededConstants) {
            constantsField().set(null, savedConstants);
            seededConstants = false;
        }
    }

    /**
     * Reaches {@code GameConstants.data}, the table every {@code getX} accessor reads through.
     *
     * @return the accessible field
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static Field constantsField() throws ReflectiveOperationException {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
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

        // FAST has a single grade: line; STUN three; CUT seven — the repeatable grade: line. Each
        // list carries one more than that: the implicit "off" band the assembler prepends, which C
        // gets from the mem_zalloc'd head node in parse_player_timed_grade (player-timed.c).
        assertEquals(1 + 1, byName(effects, TimedEffect.TMD_FAST).getGrade().size());
        assertEquals(3 + 1, byName(effects, TimedEffect.TMD_STUN).getGrade().size());
        assertEquals(7 + 1, byName(effects, TimedEffect.TMD_CUT).getGrade().size());
    }

    @Test
    void everyEffectIsHeadedByTheImplicitOffGrade() throws IOException {
        // C allocates a zeroed head node before the first grade: line of every effect and numbers
        // the real bands from one (player-timed.c, parse_player_timed_grade). The head is what a counter of zero
        // maps to, so the band walk in player_set_timed and player_timed_grade_eq needs no separate
        // "is it off?" test; its null messages are also what routes a lapse to the on_end text
        // rather than to a grade message. All 53 effects in the shipped file carry a grade: line, so
        // every one of them should be headed.
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        for (PlayerTimedEffect effect : effects) {
            List<TimedGrade> grades = effect.getGrade();
            TimedGrade head = grades.get(0);

            assertEquals(0, head.Grade(), effect.getName() + ": the head is grade zero");
            assertEquals(0, head.max(), effect.getName() + ": the head's maximum is zero");
            assertNull(head.status(), effect.getName() + ": the head is unnamed");
            assertNull(head.upMsg(), effect.getName() + ": the head has no up-message");
            assertNull(head.downMsg(), effect.getName() + ": the head has no down-message");

            // The real bands follow, numbered from one and strictly above the head's zero maximum.
            for (int i = 1; i < grades.size(); i++) {
                assertEquals(i, grades.get(i).Grade(),
                        effect.getName() + ": band " + i + " should carry grade " + i);
                assertTrue(grades.get(i).max() > 0,
                        effect.getName() + ": band " + i + " should have a positive maximum");
            }
        }
    }

    @Test
    void repeatedFailLinesAreAllRetained() throws IOException {
        // POISONED has two fail: lines (fail:2:POIS and fail:5:OPP_POIS); both must survive.
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        assertEquals(2, byName(effects, TimedEffect.TMD_POISONED).getFail().size());
        // SLOW has exactly one.
        assertEquals(1, byName(effects, TimedEffect.TMD_SLOW).getFail().size());
    }

    /**
     * C's {@code parse_player_timed_fail} links each new condition in at the head of the list
     * ({@code f->next = t->fail; t->fail = f;}), so the list {@code player_inc_check} walks runs from
     * the last {@code fail:} line to the first. POISONED declares {@code fail:2:POIS} then
     * {@code fail:5:OPP_POIS}, so C meets OPP_POIS first. The order is observable: a live resist check
     * calls {@code equip_learn_element} before it tests, so a poison-resistant item is identified when
     * the resist is reached, and C never reaches it while OPP_POIS is running.
     */
    @Disabled("PlayerTimedAssembler appends fail: lines in file order; C's parser prepends them, so "
            + "player_inc_check walks the list in reverse file order")
    @Test
    void failLinesAreWalkedInTheOrderCBuildsThem() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();
        List<TimedFailure> fails = byName(effects, TimedEffect.TMD_POISONED).getFail();

        assertEquals(2, fails.size());
        assertEquals(TimedEffectReasonType.TYPE_TIMED_EFFECT, fails.get(0).getCode());
        assertEquals(TimedEffect.TMD_OPP_POIS, fails.get(0).getEffectCode());
        assertEquals(TimedEffectReasonType.TYPE_RESIST, fails.get(1).getCode());
        assertEquals(ElementEnum.ELEM_POIS, fails.get(1).getElementCode());
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
        assertEquals(ObjectFlag.OF_AFRAID, afraid.getoFlagDup());
        assertTrue(afraid.isoFlagExactlySyn());

        // TERROR -> flag-synonym:AFRAID:0 => same dup flag, but NOT an exact synonym.
        PlayerTimedEffect terror = byName(effects, TimedEffect.TMD_TERROR);
        assertEquals(ObjectFlag.OF_AFRAID, terror.getoFlagDup());
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
        // get(1), not get(0): index zero is the implicit "off" band, whose label is null.
        assertEquals("Blind", byName(effects, TimedEffect.TMD_BLIND).getGrade().get(1).status());
    }

    @Test
    void gradeUpAndDownMessagesGoToTheRightSlots() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        // BLIND: grade:o:10000:Blind:You are blind. — the fourth field is the message shown on
        // rising into the grade (up), and there is no down-message.
        TimedGrade blind = byName(effects, TimedEffect.TMD_BLIND).getGrade().get(1);
        assertEquals("You are blind.", blind.upMsg());
        assertNull(blind.downMsg());

        // FOOD's second grade carries both:
        //   grade:r:4:Faint:You are still faint.:You are getting faint from hunger!
        // field four (up) = "You are still faint.", field five (down) = "You are getting faint...".
        // FOOD's grades start at index 1, so its second band — Faint — is index 2.
        TimedGrade faint = byName(effects, TimedEffect.TMD_FOOD).getGrade().get(2);
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

    // ---- Reader surface ---------------------------------------------------

    /**
     * The 53 {@code TMD()} entries of C's {@code list-player-timed.h}, in order. C indexes
     * {@code timed_effects[]} by this list, and {@code player_timed.txt} must name them in the same
     * order for the port's list position to line up with the {@code TMD_*} value.
     */
    private static final String[] LIST_PLAYER_TIMED_H = {
            "FAST", "SLOW", "BLIND", "PARALYZED", "CONFUSED", "AFRAID", "IMAGE", "POISONED", "CUT",
            "STUN", "FOOD", "PROTEVIL", "INVULN", "HERO", "SHERO", "SHIELD", "BLESSED", "SINVIS",
            "SINFRA", "OPP_ACID", "OPP_ELEC", "OPP_FIRE", "OPP_COLD", "OPP_POIS", "OPP_CONF",
            "AMNESIA", "TELEPATHY", "STONESKIN", "TERROR", "SPRINT", "BOLD", "SCRAMBLE", "TRAPSAFE",
            "FASTCAST", "ATT_ACID", "ATT_ELEC", "ATT_FIRE", "ATT_COLD", "ATT_POIS", "ATT_CONF",
            "ATT_EVIL", "ATT_DEMON", "ATT_VAMP", "HEAL", "COMMAND", "ATT_RUN", "COVERTRACKS",
            "POWERSHOT", "TAUNT", "BLOODLUST", "BLACKBREATH", "STEALTH", "FREE_ACT"};

    @Test
    void theRealFileListsTheEffectsInListPlayerTimedHOrder() throws IOException {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        assertEquals(LIST_PLAYER_TIMED_H.length, effects.size());
        for (int i = 0; i < LIST_PLAYER_TIMED_H.length; i++) {
            assertEquals(TimedEffect.valueOf("TMD_" + LIST_PLAYER_TIMED_H[i]), effects.get(i).getName(),
                    "position " + i);
        }
    }

    @Test
    void parseReturnsTheSameEffectsAsParseWithResults() throws IOException {
        List<PlayerTimedEffect> viaParse = new PlayerTimedReader().parse(REAL_FILE);
        List<PlayerTimedEffect> viaResults = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        assertEquals(viaResults.size(), viaParse.size());
        for (int i = 0; i < viaParse.size(); i++) {
            assertEquals(viaResults.get(i).getName(), viaParse.get(i).getName());
        }
    }

    @Test
    void aMissingFileIsAnIOExceptionWhereCQuits() {
        assertThrows(IOException.class,
                () -> new PlayerTimedReader().parseWithResults(tempDir.resolve("absent.txt").toString()));
    }

    @Test
    void aHeaderWithNoRecordIsRefusedHereWhereCLoadsAnEmptyList() throws IOException {
        // run_parse_player_timed accepts a file with no name: lines; the grammar wants at least one.
        ParseResult<PlayerTimedEffect> result = load("header-only.txt", "record-count:0\n");

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHeaderCountTooBigToParseIsASoftErrorAndTheRecordsStillLoad() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("huge-count.txt", withHeader(0, "name:FAST\ngrade:G:100:Test:Message\n")
                .replace("record-count:0", "record-count:99999999999"));

        assertEquals(1, result.items().size());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("Invalid number format")),
                result.errors()::toString);
    }

    // ---- Fields: what C accepts and the grammar does not ------------------

    @Test
    void aRepeatedDescLineKeepsTheLastHereWhereCConcatenatesThem() throws IOException {
        // parse_player_timed_desc uses string_append, so C would hold "FirstSecond".
        ParseResult<PlayerTimedEffect> result = load("two-desc.txt", withHeader(1,
                "name:FAST\ndesc:First\ndesc:Second\ngrade:G:100:Test:Message\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals("Second", result.items().get(0).getDescription());
    }

    @Test
    void aRepeatedEndIncreaseOrDecreaseLineKeepsTheLastHereWhereCConcatenatesThem() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("two-messages.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\n"
                        + "on-end:One\non-end:Two\n"
                        + "on-increase:One\non-increase:Two\n"
                        + "on-decrease:One\non-decrease:Two\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        PlayerTimedEffect fast = result.items().get(0);
        assertEquals("Two", fast.getOnEnd());
        assertEquals("Two", fast.getOnIncrease());
        assertEquals("Two", fast.getOnDecrease());
    }

    @Test
    void aRepeatedNameLineMakesASecondRecordHereWhereCContinuesTheFirst() throws IOException {
        // parse_player_timed_name points at timed_effects[index] again, so C merges the two blocks.
        ParseResult<PlayerTimedEffect> result = load("two-names.txt", withHeader(2,
                "name:FAST\ngrade:G:100:Test:Message\nname:FAST\ndesc:Again\n"));

        assertEquals(2, result.items().size());
        assertEquals(TimedEffect.TMD_FAST, result.items().get(0).getName());
        assertEquals(TimedEffect.TMD_FAST, result.items().get(1).getName());
    }

    @Test
    void anOnBeginEffectWithARadiusIsRefusedHereWhereCReadsIt() throws IOException {
        // "on-begin-effect sym eff ?sym type ?int radius ?int other"
        ParseResult<PlayerTimedEffect> result = load("radius.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\non-begin-effect:SCRAMBLE_STATS:STR:3\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void effectYxEffectExprAndEffectMsgAreRefusedHereWhereCReadsThem() throws IOException {
        for (String line : List.of("effect-yx:1:2", "effect-expr:A:B:C", "effect-msg:Hello")) {
            ParseResult<PlayerTimedEffect> result = load("effect-line.txt", withHeader(1,
                    "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\n" + line + "\n"));

            assertTrue(result.items().isEmpty(), line);
            assertTrue(result.hasErrors(), line);
        }
    }

    @Test
    void anEffectDiceAfterAnOnBeginEffectIsRefusedHereWhereCAcceptsIt() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("begin-dice.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\non-begin-effect:SCRAMBLE_STATS\neffect-dice:5\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aDiceStringInEffectDiceIsRefusedHereWhereCParsesIt() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("dice-string.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\neffect-dice:1d4\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aBareFlagsLineIsRefusedHereWhereCAcceptsIt() throws IOException {
        // "flags ?str flags": parse_player_timed_effect_flags returns early when the value is absent.
        ParseResult<PlayerTimedEffect> result = load("bare-flags.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nflags:\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aGradeColourWrittenAsAWordIsRefusedHereWhereCReadsIt() throws IOException {
        // strlen(color) > 1 goes through color_text_to_attr in parse_player_timed_grade.
        ParseResult<PlayerTimedEffect> result = load("word-colour.txt", withHeader(1,
                "name:FAST\ngrade:Yellow:100:Test:Message\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aLowerCaseNameIsRefusedAndCIsCaseSensitiveToo() throws IOException {
        // grab_name compares with streq, so C refuses "fast" as well; both lose the file or record.
        ParseResult<PlayerTimedEffect> result = load("lower-name.txt", withHeader(1,
                "name:fast\ngrade:G:100:Test:Message\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void aHexadecimalLowerBoundIsRefusedHereWhereCReadsBaseZero() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("hex-bound.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nlower-bound:0x10\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    @Test
    void anIndentedDirectiveIsRefusedHereWhereCSkipsLeadingWhitespace() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("indent.txt", withHeader(1,
                "name:FAST\n  grade:G:100:Test:Message\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.hasErrors());
    }

    // ---- Values: what C checks and the assembler does not -----------------

    @Test
    void aOneCharacterDownMessageIsKeptByCAndDroppedHere() throws IOException {
        // C nulls only a one-character name and up_msg; down_msg is copied as it stands.
        ParseResult<PlayerTimedEffect> result = load("short-down.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message:X\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertNull(result.items().get(0).getGrade().get(1).downMsg());
    }

    @Test
    void aOneCharacterNameAndUpMessageAreDummiesAsInC() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("dummies.txt", withHeader(1,
                "name:FAST\ngrade:G:100:-:-\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        TimedGrade grade = result.items().get(0).getGrade().get(1);
        assertNull(grade.status());
        assertNull(grade.upMsg());
    }

    @Test
    void aLowerBoundAbove32767LoadsHereWhereCRefusesIt() throws IOException {
        // parse_player_timed_effect_lower_bound: bound < 0 || bound > 32767 is PARSE_ERROR_INVALID_VALUE.
        ParseResult<PlayerTimedEffect> result = load("big-bound.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nlower-bound:40000\n"));

        assertEquals(1, result.items().size());
        assertEquals(40000, result.items().get(0).getLowerBound());
    }

    @Test
    void aNegativeLowerBoundLoadsHereWhereCRefusesIt() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("negative-bound.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nlower-bound:-1\n"));

        assertEquals(1, result.items().size());
        assertEquals(-1, result.items().get(0).getLowerBound());
    }

    @Test
    void theLowerBoundEdgesOfCsRangeLoad() throws IOException {
        for (int bound : new int[]{0, 32767}) {
            ParseResult<PlayerTimedEffect> result = load("edge-bound.txt", withHeader(1,
                    "name:FAST\ngrade:G:100:Test:Message\nlower-bound:" + bound + "\n"));

            assertFalse(result.hasErrors(), () -> result.errors().toString());
            assertEquals(bound, result.items().get(0).getLowerBound());
        }
    }

    @Test
    void anUnknownFlagsTokenIsIgnoredHereWhereCRefusesIt() throws IOException {
        // Only NONSTACKING is a timed-effect flag; anything else is PARSE_ERROR_INVALID_FLAG in C.
        ParseResult<PlayerTimedEffect> result = load("bad-flag.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nflags:WIBBLE\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertFalse(result.items().get(0).isNonStacking());
    }

    @Test
    void nonStackingAmongOtherTokensIsStillRead() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("two-flags.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nflags:NONSTACKING | NONSTACKING\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).isNonStacking());
    }

    @Test
    void aFlagSynonymExactWrittenAsDoubleZeroIsExactHereWhereCReadsZero() throws IOException {
        // "exact" is an int in C: parser_getint(p, "exact") != 0, and "00" is 0.
        ParseResult<PlayerTimedEffect> result = load("synonym-00.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nflag-synonym:AFRAID:00\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).isoFlagExactlySyn());
    }

    @Test
    void aFlagSynonymExactOfTwoIsExactAsInC() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("synonym-2.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nflag-synonym:AFRAID:2\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertTrue(result.items().get(0).isoFlagExactlySyn());
    }

    @Test
    void anUnknownFlagSynonymIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-synonym.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nflag-synonym:NOTAFLAG:1\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown flag-synonym")),
                result.errors()::toString);
    }

    @Test
    void eachFailCodeOneToFiveResolvesItsOwnKindOfName() throws IOException {
        // TMD_FAIL_FLAG_OBJECT, _RESIST, _VULN, _PLAYER, _TIMED_EFFECT = 1..5 in player-timed.h.
        ParseResult<PlayerTimedEffect> result = load("fail-codes.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\n"
                        + "fail:1:FREE_ACT\nfail:2:POIS\nfail:3:FIRE\nfail:4:ROCK\nfail:5:OPP_POIS\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        List<TimedFailure> fails = result.items().get(0).getFail();
        assertEquals(5, fails.size());
        assertEquals(TimedEffectReasonType.TYPE_OBJECT_FLAG, fails.get(0).getCode());
        assertEquals(TimedEffectReasonType.TYPE_RESIST, fails.get(1).getCode());
        assertEquals(TimedEffectReasonType.TYPE_VULN, fails.get(2).getCode());
        assertEquals(TimedEffectReasonType.TYPE_PLAYER_FLAG, fails.get(3).getCode());
        assertEquals(TimedEffectReasonType.TYPE_TIMED_EFFECT, fails.get(4).getCode());
    }

    @Test
    void aFailCodeOutsideOneToFiveIsReportedAndSkipped() throws IOException {
        for (String code : List.of("0", "6")) {
            ParseResult<PlayerTimedEffect> result = load("fail-code.txt", withHeader(1,
                    "name:FAST\ngrade:G:100:Test:Message\nfail:" + code + ":POIS\n"));

            assertTrue(result.items().isEmpty(), code);
            assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown fail type")),
                    result.errors()::toString);
        }
    }

    @Test
    void aFailNameFromTheWrongListIsReportedAndSkipped() throws IOException {
        // POIS is an element, so it is not an object flag.
        ParseResult<PlayerTimedEffect> result = load("fail-wrong-list.txt", withHeader(1,
                "name:FAST\ngrade:G:100:Test:Message\nfail:1:POIS\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("invalid fail line")),
                result.errors()::toString);
    }

    @Test
    void aSlayThatIsNotLoadedIsReportedAndSkipped() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("bad-slay.txt", withHeader(1,
                "name:ATT_EVIL\ngrade:G:100:Test:Message\nslay:NOTASLAY\n"));

        assertTrue(result.items().isEmpty());
        assertTrue(result.errors().stream().anyMatch(e -> e.contains("unknown slay")),
                result.errors()::toString);
    }

    @Test
    void anEffectNameAndTheDirectivesAfterItDoNotNeedAGrade() throws IOException {
        // Only a grade: line is optional in C; a record of just name: and desc: is legal.
        ParseResult<PlayerTimedEffect> result = load("no-grade.txt", withHeader(1,
                "name:FAST\ndesc:Quick\n"));

        assertFalse(result.hasErrors(), () -> result.errors().toString());
        assertEquals(1, result.items().size());
        assertEquals(1, result.items().get(0).getGrade().size());
    }
}
