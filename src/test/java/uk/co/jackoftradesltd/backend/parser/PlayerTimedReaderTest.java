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
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.parser.DataFileParseException;
import uk.co.jackoftradesltd.channel.parser.ParseResult;
import uk.co.jackoftradesltd.middle.effect.Effect;
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
 * the {@code flag-synonym} dup/exact split, {@code NONSTACKING}). A syntax error ends the load with
 * a {@code ParseCancellationException}, and a value the assembler cannot resolve
 * ends the load with a {@link DataFileParseException}, as C's parser stops on its first error.
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
        // GameConstants.getPlayerFoodValue (in PlayerTimedAssembler.assemble), so the constants table
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


    private ParseResult<PlayerTimedEffect> load(String name, String content) throws IOException {
        Path file = tempDir.resolve(name);
        Files.writeString(file, content);
        return new PlayerTimedReader().parseWithResults(file.toString());
    }

    /**
     * Loads a one-record file and checks that the assembler stopped the load: it throws a
     * {@link DataFileParseException} whose message names the record's line and the file and ends
     * with the given tail, as C's parser stops on its first error.
     *
     * @param fileName the name to give the temporary file
     * @param body     the record text, opening on line 1
     * @param line     the line the failing {@code name:} opens on
     * @param tail     the part of the message that says what was wrong
     */
    private void assertStopsTheLoad(String fileName, String body, int line, String tail) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> load(fileName, body));

        assertTrue(thrown.getMessage().contains(tail), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: " + line + " "), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(fileName), thrown::getMessage);
    }

    private static PlayerTimedEffect byName(List<PlayerTimedEffect> effects, TimedEffect name) {
        return effects.stream().filter(e -> e.getName() == name).findFirst().orElse(null);
    }

    // ---- Happy path: the real file ---------------------------------------

    @Test
    void realFileLoadsAllFiftyThreeEffectsWithNoErrors() throws IOException {
        ParseResult<PlayerTimedEffect> result = new PlayerTimedReader().parseWithResults(REAL_FILE);

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

        // Most statuses have neither; C's list is NULL, here an empty list (not a crash, not a dropped record).
        PlayerTimedEffect fast = byName(effects, TimedEffect.TMD_FAST);
        assertTrue(fast.getOnBeginEffect().isEmpty());
        assertTrue(fast.getOnEndEffect().isEmpty());
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

    // ---- A value that does not resolve stops the load ---------------------

    @Test
    void anUnknownTimedEffectNameStopsTheLoad() {
        assertStopsTheLoad("bad-name.txt", "name:NOTATIMEDEFFECT\ngrade:G:100:Test:Message\n", 1,
                "has an illegal name value: NOTATIMEDEFFECT");
    }

    @Test
    void anUnknownBrandStopsTheLoad() {
        assertStopsTheLoad("bad-brand.txt", "name:ATT_ACID\ngrade:G:100:Test:Message\nbrand:NOTAREALBRAND\n", 1,
                "has an illegal brand value: NOTAREALBRAND");
    }

    @Test
    void anUnknownResistElementStopsTheLoad() {
        assertStopsTheLoad("bad-resist.txt", "name:OPP_ACID\ngrade:G:100:Test:Message\nresist:NOTREAL\n", 1,
                "has an illegal resist value: NOTREAL");
    }

    @Test
    void anUnknownMessageTypeStopsTheLoad() {
        assertStopsTheLoad("bad-msgt.txt", "name:FAST\ngrade:G:100:Test:Message\nmsgt:NOTAMESSAGE\n", 1,
                "has an illegal message type value: NOTAMESSAGE");
    }

    @Test
    void aBadRecordAfterAGoodOneStopsTheWholeLoad() {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> load("mixed.txt", "name:FAST\ngrade:G:100:Test:Message\n"
                        + "name:NOTATIMEDEFFECT\ngrade:G:100:Test:Message\n"));

        assertTrue(thrown.getMessage().contains("line: 3 "), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("has an illegal name value: NOTATIMEDEFFECT"), thrown::getMessage);
    }

    // ---- Grade arity: optional fifth field -------------------------------

    @Test
    void gradeWithNoDownMessageParses() throws IOException {
        // Four fields: colour:max:status:up — the down-message is omitted entirely.
        ParseResult<PlayerTimedEffect> result = load("four-field-grade.txt", "name:FAST\ngrade:G:100:Hasted:You speed up.\n");

        assertEquals(1, result.items().size());
    }

    @Test
    void gradeWithEmptyTrailingDownMessageParses() throws IOException {
        // Five colons but an empty fifth field, as FOOD's 'Full' grade has it.
        ParseResult<PlayerTimedEffect> result = load("empty-down-grade.txt", "name:FAST\ngrade:G:100:Hasted:You speed up.:\n");

        assertEquals(1, result.items().size());
    }

    // ---- hard (fail-closed) errors ------------------------

    @Test
    void recordNotOpeningWithNameIsAHardError() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("no-name.txt", "grade:G:100:Test:Message\n"));

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

    /**
     * C's {@code run_parse_player_timed} accepts a file with no {@code name:} lines and leaves every
     * timed effect as it was, and the {@code file} rule is {@code (playerTimed)* EOF}, so an empty
     * file loads an empty list.
     *
     * <p>Function aHeaderWithNoRecordLoadsAnEmptyListAsInC coded on 261119, commented in full on
     * 261119 (replaces the pin that refused it).
     */
    @Test
    void aHeaderWithNoRecordLoadsAnEmptyListAsInC() throws IOException {
        assertTrue(load("header-only.txt", "").items().isEmpty());
    }

    // ---- Fields: what C accepts and the grammar does not ------------------

    /**
     * A second {@code desc:} line in one record is joined to the first with no separator, as C's
     * {@code parse_player_timed_desc} does with {@code string_append}, so the description is
     * {@code FirstSecond}.
     *
     * <p>Function aRepeatedDescLineIsJoinedAsInC coded on 261119, commented in full on 261119
     * (flipped from {@code aRepeatedDescLineKeepsTheLastHereWhereCConcatenatesThem}).
     */
    @Test
    void aRepeatedDescLineIsJoinedAsInC() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("two-desc.txt", "name:FAST\ndesc:First\ndesc:Second\ngrade:G:100:Test:Message\n");

        assertEquals("FirstSecond", result.items().get(0).getDescription());
    }

    /**
     * A repeated {@code on-end:}, {@code on-increase:} or {@code on-decrease:} line in one record is
     * joined to the one before it with no separator, as C's matching {@code parse_player_timed_*}
     * handlers do with {@code string_append}, so each field is {@code OneTwo}.
     *
     * <p>Function aRepeatedEndIncreaseOrDecreaseLineIsJoinedAsInC coded on 261119, commented in full
     * on 261119 (flipped from
     * {@code aRepeatedEndIncreaseOrDecreaseLineKeepsTheLastHereWhereCConcatenatesThem}).
     */
    @Test
    void aRepeatedEndIncreaseOrDecreaseLineIsJoinedAsInC() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("two-messages.txt", "name:FAST\ngrade:G:100:Test:Message\n"
                        + "on-end:One\non-end:Two\n"
                        + "on-increase:One\non-increase:Two\n"
                        + "on-decrease:One\non-decrease:Two\n");

        PlayerTimedEffect fast = result.items().get(0);
        assertEquals("OneTwo", fast.getOnEnd());
        assertEquals("OneTwo", fast.getOnIncrease());
        assertEquals("OneTwo", fast.getOnDecrease());
    }

    @Test
    void aRepeatedNameLineContinuesTheFirstEffectAsInC() throws IOException {
        // parse_player_timed_name points at timed_effects[index] again, so the two blocks are one effect.
        ParseResult<PlayerTimedEffect> result = load("two-names.txt", "name:FAST\ngrade:G:100:Test:Message\nname:FAST\ndesc:Again\n");

        assertEquals(1, result.items().size());
        assertEquals(TimedEffect.TMD_FAST, result.items().get(0).getName());
        assertEquals("Again", result.items().get(0).getDescription());
    }

    @Test
    void anOnBeginEffectWithARadiusIsRefusedHereWhereCReadsIt() throws IOException {
        // "on-begin-effect sym eff ?sym type ?int radius ?int other"
        assertThrows(ParseCancellationException.class, () -> load("radius.txt", "name:FAST\ngrade:G:100:Test:Message\non-begin-effect:SCRAMBLE_STATS:STR:3\n"));

    }

    @Test
    void effectYxEffectExprAndEffectMsgAreRefusedHereWhereCReadsThem() throws IOException {
        for (String line : List.of("effect-yx:1:2", "effect-expr:A:B:C", "effect-msg:Hello")) {
            assertThrows(ParseCancellationException.class, () -> load("effect-line.txt", "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\n" + line + "\n"), line);

        }
    }

    // ---- effect-dice: attaches to the effect opened last, as C's ps->e does ----------

    /**
     * The effects of one chain, named as {@code Effect.index} holds them and joined by commas, so a
     * test can say which effects a chain holds and in what order ({@code Effect} has no accessor).
     *
     * <p>Function effectNames coded on 261119, commented in full on 261119.
     *
     * @param chain a begin or end effect chain
     * @return the {@code EF_*} names in chain order, e.g. {@code EF_SCRAMBLE_STATS,EF_UNSCRAMBLE_STATS}
     * @throws Exception if the field cannot be reached
     */
    private static String effectNames(List<Effect> chain) throws Exception {
        Field index = Effect.class.getDeclaredField("index");
        index.setAccessible(true);
        StringBuilder names = new StringBuilder();
        for (Effect effect : chain) {
            if (!names.isEmpty()) names.append(',');
            names.append(index.get(effect));
        }
        return names.toString();
    }

    /**
     * C's {@code parse_player_timed_effect_dice} puts the dice on {@code ps->e}, the effect opened by
     * the last {@code on-begin-effect:} line, so it is not only an end-effect directive.
     *
     * <p>Function anEffectDiceAfterAnOnBeginEffectLoadsAsInC coded on 261119, commented in full on
     * 261119 (replaces the pin that refused it).
     */
    @Test
    void anEffectDiceAfterAnOnBeginEffectLoadsAsInC() throws Exception {
        PlayerTimedEffect fast = load("begin-dice.txt",
                "name:FAST\ngrade:G:100:Test:Message\non-begin-effect:SCRAMBLE_STATS\neffect-dice:5\n")
                .items().get(0);

        assertEquals(1, fast.getOnBeginEffect().size());
        assertEquals("0,0,5", EffectProbe.yxBase(fast.getOnBeginEffect().getFirst()));
        assertTrue(fast.getOnEndEffect().isEmpty());
    }

    /**
     * Other directives can sit between an effect line and its {@code effect-dice:}, because
     * {@code ps->e} is only cleared by the next {@code name:}.
     *
     * <p>Function anEffectDiceAfterAnotherDirectiveReachesItsEffectAsInC coded on 261119, commented
     * in full on 261119.
     */
    @Test
    void anEffectDiceAfterAnotherDirectiveReachesItsEffectAsInC() throws Exception {
        PlayerTimedEffect fast = load("dice-after-msgt.txt",
                "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\nmsgt:SPEED\n"
                        + "on-end:Done\nlower-bound:3\neffect-dice:100\n").items().get(0);

        assertEquals(1, fast.getOnEndEffect().size());
        assertEquals("0,0,100", EffectProbe.yxBase(fast.getOnEndEffect().getFirst()));
        assertEquals("Done", fast.getOnEnd());
        assertEquals(3, fast.getLowerBound());
    }

    /**
     * A second {@code effect-dice:} for the same effect replaces the dice, as C's handler frees the
     * old dice and keeps the new.
     *
     * <p>Function aSecondEffectDiceLineReplacesTheFirstAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void aSecondEffectDiceLineReplacesTheFirstAsInC() throws Exception {
        PlayerTimedEffect fast = load("two-dice.txt",
                "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\n"
                        + "effect-dice:5\neffect-dice:6\n").items().get(0);

        assertEquals("0,0,6", EffectProbe.yxBase(fast.getOnEndEffect().getFirst()));
    }

    /**
     * With a begin and an end effect both open, the dice go to whichever line came last in the
     * file, not to the end effect by default, and the other effect keeps no dice.
     *
     * <p>Function anEffectDiceGoesToTheEffectOpenedLastAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void anEffectDiceGoesToTheEffectOpenedLastAsInC() throws Exception {
        PlayerTimedEffect endLast = load("end-last.txt",
                "name:FAST\ngrade:G:100:Test:Message\non-begin-effect:SCRAMBLE_STATS\n"
                        + "on-end-effect:TIMED_INC_NO_RES:SLOW\neffect-dice:7\n").items().get(0);
        assertEquals("0,0,", EffectProbe.yxBase(endLast.getOnBeginEffect().getFirst()));
        assertEquals("0,0,7", EffectProbe.yxBase(endLast.getOnEndEffect().getFirst()));

        PlayerTimedEffect beginLast = load("begin-last.txt",
                "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\n"
                        + "on-begin-effect:SCRAMBLE_STATS\neffect-dice:9\n").items().get(0);
        assertEquals("0,0,9", EffectProbe.yxBase(beginLast.getOnBeginEffect().getFirst()));
        assertEquals("0,0,", EffectProbe.yxBase(beginLast.getOnEndEffect().getFirst()));
    }

    /**
     * Every {@code on-begin-effect:} or {@code on-end-effect:} line appends a new effect to its
     * chain, so a record keeps them all in file order, and each {@code effect-dice:} lands on the
     * effect before it.
     *
     * <p>Function everyEffectLineOfARecordIsKeptInFileOrderAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void everyEffectLineOfARecordIsKeptInFileOrderAsInC() throws Exception {
        PlayerTimedEffect fast = load("many-effects.txt",
                "name:FAST\ngrade:G:100:Test:Message\n"
                        + "on-begin-effect:SCRAMBLE_STATS\neffect-dice:1\n"
                        + "on-begin-effect:UNSCRAMBLE_STATS\neffect-dice:2\n"
                        + "on-end-effect:TIMED_INC_NO_RES:SLOW\neffect-dice:3\n"
                        + "on-end-effect:UNSCRAMBLE_STATS\neffect-dice:4\n").items().get(0);

        assertEquals("EF_SCRAMBLE_STATS,EF_UNSCRAMBLE_STATS", effectNames(fast.getOnBeginEffect()));
        assertEquals("EF_TIMED_INC_NO_RES,EF_UNSCRAMBLE_STATS", effectNames(fast.getOnEndEffect()));
        assertEquals("0,0,1", EffectProbe.yxBase(fast.getOnBeginEffect().get(0)));
        assertEquals("0,0,2", EffectProbe.yxBase(fast.getOnBeginEffect().get(1)));
        assertEquals("0,0,3", EffectProbe.yxBase(fast.getOnEndEffect().get(0)));
        assertEquals("0,0,4", EffectProbe.yxBase(fast.getOnEndEffect().get(1)));
    }

    /**
     * An effect line with no {@code effect-dice:} loads with an empty dice string, as C's zeroed
     * effect has no dice.
     *
     * <p>Function anEffectLineWithNoDiceLoadsWithEmptyDiceAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void anEffectLineWithNoDiceLoadsWithEmptyDiceAsInC() throws Exception {
        PlayerTimedEffect fast = load("no-dice.txt",
                "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\n").items().get(0);

        assertEquals("0,0,", EffectProbe.yxBase(fast.getOnEndEffect().getFirst()));
    }

    /**
     * The shipped {@code player_timed.txt} keeps every effect and the one {@code effect-dice:} it
     * has: {@code SPRINT} ends with {@code TIMED_INC_NO_RES} carrying dice 100, and {@code SCRAMBLE}
     * begins with {@code SCRAMBLE_STATS} and ends with {@code UNSCRAMBLE_STATS}, neither with dice.
     *
     * <p>Function theShippedEffectsAndTheirDiceLoadAsInC coded on 261119, commented in full on
     * 261119.
     */
    @Test
    void theShippedEffectsAndTheirDiceLoadAsInC() throws Exception {
        List<PlayerTimedEffect> effects = new PlayerTimedReader().parseWithResults(REAL_FILE).items();

        PlayerTimedEffect sprint = byName(effects, TimedEffect.TMD_SPRINT);
        assertEquals("EF_TIMED_INC_NO_RES", effectNames(sprint.getOnEndEffect()));
        assertEquals("0,0,100", EffectProbe.yxBase(sprint.getOnEndEffect().getFirst()));
        assertTrue(sprint.getOnBeginEffect().isEmpty());

        PlayerTimedEffect scramble = byName(effects, TimedEffect.TMD_SCRAMBLE);
        assertEquals("EF_SCRAMBLE_STATS", effectNames(scramble.getOnBeginEffect()));
        assertEquals("EF_UNSCRAMBLE_STATS", effectNames(scramble.getOnEndEffect()));
        assertEquals("0,0,", EffectProbe.yxBase(scramble.getOnBeginEffect().getFirst()));
        assertEquals("0,0,", EffectProbe.yxBase(scramble.getOnEndEffect().getFirst()));
    }

    /**
     * Parked 2026-11-19 (see {@code docs/Parked.md}). C's handler returns
     * {@code PARSE_ERROR_MISSING_RECORD_HEADER} and stops the load when {@code effect-dice:} comes
     * with no effect open; the grammar drops the dice and loads the record.
     *
     * <p>Function anEffectDiceWithNoEffectOpenIsDroppedHereWhereCRefusesIt coded on 261119, commented
     * in full on 261119.
     */
    @Test
    void anEffectDiceWithNoEffectOpenIsDroppedHereWhereCRefusesIt() throws Exception {
        PlayerTimedEffect fast = load("dice-no-effect.txt",
                "name:FAST\ngrade:G:100:Test:Message\neffect-dice:5\n").items().get(0);

        assertTrue(fast.getOnBeginEffect().isEmpty());
        assertTrue(fast.getOnEndEffect().isEmpty());
    }

    /**
     * Parked 2026-11-19 (see {@code docs/Parked.md}). The same refusal in C when {@code effect-dice:}
     * comes before the effect line it belongs to: {@code ps->e} is not yet set, so C stops the load.
     * The grammar drops that dice and the later effect loads with none.
     *
     * <p>Function anEffectDiceBeforeItsEffectLineIsDroppedHereWhereCRefusesIt coded on 261119,
     * commented in full on 261119.
     */
    @Test
    void anEffectDiceBeforeItsEffectLineIsDroppedHereWhereCRefusesIt() throws Exception {
        PlayerTimedEffect fast = load("dice-first.txt",
                "name:FAST\ngrade:G:100:Test:Message\neffect-dice:5\non-end-effect:TIMED_INC_NO_RES:SLOW\n")
                .items().get(0);

        assertEquals(1, fast.getOnEndEffect().size());
        assertEquals("0,0,", EffectProbe.yxBase(fast.getOnEndEffect().getFirst()));
    }

    @Test
    void aDiceStringInEffectDiceIsRefusedHereWhereCParsesIt() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("dice-string.txt", "name:FAST\ngrade:G:100:Test:Message\non-end-effect:TIMED_INC_NO_RES:SLOW\neffect-dice:1d4\n"));

    }

    // ---- flags: reads C's separator (Stage 7d) -----------------------------

    /**
     * The separators C's {@code strtok(flags, " |")} collapses in
     * {@code parse_player_timed_effect_flags} ({@code player-timed.c}): any run of blanks and bars,
     * a single blank included, splits two names.
     */
    private static final List<String> SEPARATORS = List.of(" ", "    ", "|", " |", "| ", " | ", "  |  ",
            "||", " | | ", "| |", " |  | ");

    /**
     * Runs of blanks and bars with no name in them, which {@code strtok} skips at either end of a list.
     */
    private static final List<String> DELIMITER_RUNS = List.of(" ", "  ", "|", " | ", "||", " | | ", "|  |");

    /** A FAST record with only its grade, opening on line 1, for a {@code flags:} line to follow. */
    private static final String FAST_HEAD = "name:FAST\ngrade:G:100:Test:Message\n";

    /** A second record after the {@code flags:} line, to show the line ended cleanly. */
    private static final String SLOW_TAIL = "name:SLOW\ngrade:G:100:Test:Message\n";

    /**
     * Loads FAST with {@code flagsLine} (no line end of its own) followed by SLOW, checks both records
     * loaded, and returns FAST.
     */
    private PlayerTimedEffect fastWithFlagsMidFile(String flagsLine) throws IOException {
        List<PlayerTimedEffect> items = load("flags-mid.txt", FAST_HEAD + flagsLine + "\n" + SLOW_TAIL).items();

        assertEquals(2, items.size(), flagsLine);
        assertEquals(TimedEffect.TMD_SLOW, items.get(1).getName(), flagsLine);
        assertFalse(items.get(1).isNonStacking(), flagsLine);
        return items.get(0);
    }

    @Test
    void anyRunOfBlanksAndBarsSeparatesFlagsAsInC() throws IOException {
        for (String sep : SEPARATORS) {
            assertTrue(fastWithFlagsMidFile("flags:NONSTACKING" + sep + "NONSTACKING").isNonStacking(),
                    () -> "sep=[" + sep + "]");

            // Only NONSTACKING is a timed-effect flag, so a second name that C splits off reaches the
            // flag check and stops the load as PARSE_ERROR_INVALID_FLAG; an unsplit line could not.
            assertStopsTheLoad("flags-split.txt", FAST_HEAD + "flags:NONSTACKING" + sep + "WIBBLE\n", 1,
                    "has an illegal flag value: WIBBLE");
        }
    }

    @Test
    void aLeadingOrTrailingDelimiterOnFlagsLoadsAsInC() throws IOException {
        // strtok skips delimiters before the first name and finds nothing after the last.
        for (String run : DELIMITER_RUNS) {
            for (String line : List.of("flags:" + run + "NONSTACKING", "flags:NONSTACKING" + run)) {
                assertTrue(fastWithFlagsMidFile(line).isNonStacking(), () -> "[" + line + "]");
            }
        }
    }

    @Test
    void aBareOrDelimiterOnlyFlagsLineLoadsWithNoFlagsAsInC() throws IOException {
        // "flags ?str flags": parse_player_timed_effect_flags returns early when the value is absent,
        // and strtok finds no name in a value that is only blanks and bars.
        assertFalse(fastWithFlagsMidFile("flags:").isNonStacking());
        for (String run : DELIMITER_RUNS) {
            assertFalse(fastWithFlagsMidFile("flags:" + run).isNonStacking(), () -> "run=[" + run + "]");
        }
    }

    @Test
    void aFlagsLineWithNoFinalNewlineLoadsAsInC() throws IOException {
        // file_getl hands C an unterminated last line as it stands; the answer matches the same
        // line with a newline after it.
        List<String> named = List.of("flags:NONSTACKING", "flags:NONSTACKING NONSTACKING",
                "flags:NONSTACKING | | NONSTACKING", "flags:| NONSTACKING", "flags: NONSTACKING",
                "flags:NONSTACKING |", "flags:NONSTACKING ||");
        List<String> empty = List.of("flags:", "flags: ", "flags:|", "flags:||", "flags: | ", "flags:| |");

        for (String line : named) {
            List<PlayerTimedEffect> items = load("flags-last.txt", FAST_HEAD + line).items();
            assertEquals(1, items.size(), line);
            assertTrue(items.get(0).isNonStacking(), () -> "[" + line + "]");
        }
        for (String line : empty) {
            List<PlayerTimedEffect> items = load("flags-last.txt", FAST_HEAD + line).items();
            assertEquals(1, items.size(), line);
            assertFalse(items.get(0).isNonStacking(), () -> "[" + line + "]");
        }
    }

    @Test
    void aSecondNameOnASingleValueDirectiveIsRefusedAsInC() {
        // The flag mode is shared with these directives, and only flags: is a list. In C a second
        // name stays in the one value and fails its lookup: grab_name for name: (str), and for the
        // sym directives message_lookup_by_name (msgt:), proj_name_to_idx (resist:) and the code
        // compare against brands[] or slays[] (brand:, slay:). Java refuses each at the parser.
        List<String> bodies = List.of(
                "name:FAST SLOW\ngrade:G:100:Test:Message\n",
                "name:FAST|SLOW\ngrade:G:100:Test:Message\n",
                "name:FAST |\ngrade:G:100:Test:Message\n",
                FAST_HEAD + "msgt:SPEED SLOW\n",
                FAST_HEAD + "msgt:SPEED|SLOW\n",
                FAST_HEAD + "resist:FIRE COLD\n",
                FAST_HEAD + "resist:FIRE|COLD\n",
                FAST_HEAD + "brand:FIRE_3 COLD_3\n",
                FAST_HEAD + "slay:EVIL_2 ANIMAL_2\n");

        for (String body : bodies) {
            assertThrows(ParseCancellationException.class, () -> load("one-value.txt", body), body);
        }
    }

    @Test
    void aGradeColourWrittenAsAWordIsRefusedHereWhereCReadsIt() throws IOException {
        // strlen(color) > 1 goes through color_text_to_attr in parse_player_timed_grade.
        assertThrows(ParseCancellationException.class, () -> load("word-colour.txt", "name:FAST\ngrade:Yellow:100:Test:Message\n"));

    }

    @Test
    void aLowerCaseNameIsRefusedAndCIsCaseSensitiveToo() throws IOException {
        // grab_name compares with streq, so C refuses "fast" as well; both lose the file or record.
        assertThrows(ParseCancellationException.class, () -> load("lower-name.txt", "name:fast\ngrade:G:100:Test:Message\n"));

    }

    @Test
    void aHexadecimalLowerBoundIsRefusedHereWhereCReadsBaseZero() throws IOException {
        assertThrows(ParseCancellationException.class, () -> load("hex-bound.txt", "name:FAST\ngrade:G:100:Test:Message\nlower-bound:0x10\n"));

    }

    @Test
    void anIndentedDirectiveLoadsAsInC() throws IOException {
        // parser_parse skips leading white space, and the INDENT rule in CommentsAndEol does too.
        assertEquals(1, load("indent.txt", "name:FAST\n  grade:G:100:Test:Message\n").items().size());
    }

    // ---- Values: what C checks and the assembler does not -----------------

    @Test
    void aOneCharacterDownMessageIsKeptAsInC() throws IOException {
        // C nulls only a one-character name and up_msg; down_msg is copied as it stands.
        ParseResult<PlayerTimedEffect> result = load("short-down.txt", "name:FAST\ngrade:G:100:Test:Message:X\n");

        assertEquals("X", result.items().get(0).getGrade().get(1).downMsg());
    }

    @Test
    void aOneCharacterNameAndUpMessageAreDummiesAsInC() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("dummies.txt", "name:FAST\ngrade:G:100:-:-\n");

        TimedGrade grade = result.items().get(0).getGrade().get(1);
        assertNull(grade.status());
        assertNull(grade.upMsg());
    }

    @Test
    void aLowerBoundAbove32767StopsTheLoadAsInC() {
        // parse_player_timed_effect_lower_bound: bound < 0 || bound > 32767 is PARSE_ERROR_INVALID_VALUE.
        assertStopsTheLoad("big-bound.txt", "name:FAST\ngrade:G:100:Test:Message\nlower-bound:40000\n", 1,
                "has an out of bounds lower bound value: 40000");
    }

    @Test
    void aNegativeLowerBoundStopsTheLoadAsInC() {
        assertStopsTheLoad("negative-bound.txt", "name:FAST\ngrade:G:100:Test:Message\nlower-bound:-1\n", 1,
                "has an out of bounds lower bound value: -1");
    }

    @Test
    void theLowerBoundEdgesOfCsRangeLoad() throws IOException {
        for (int bound : new int[]{0, 32767}) {
            ParseResult<PlayerTimedEffect> result = load("edge-bound.txt", "name:FAST\ngrade:G:100:Test:Message\nlower-bound:" + bound + "\n");

            assertEquals(bound, result.items().get(0).getLowerBound());
        }
    }

    @Test
    void anUnknownFlagsTokenStopsTheLoadAsInC() {
        // Only NONSTACKING is a timed-effect flag; anything else is PARSE_ERROR_INVALID_FLAG in C.
        assertStopsTheLoad("bad-flag.txt", "name:FAST\ngrade:G:100:Test:Message\nflags:WIBBLE\n", 1,
                "has an illegal flag value: WIBBLE");
    }

    @Test
    void nonStackingAmongOtherTokensIsStillRead() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("two-flags.txt", "name:FAST\ngrade:G:100:Test:Message\nflags:NONSTACKING | NONSTACKING\n");

        assertTrue(result.items().get(0).isNonStacking());
    }

    @Test
    void aFlagSynonymExactWrittenAsDoubleZeroIsNotExactAsInC() throws IOException {
        // "exact" is an int in C: parser_getint(p, "exact") != 0, and "00" is 0.
        ParseResult<PlayerTimedEffect> result = load("synonym-00.txt", "name:FAST\ngrade:G:100:Test:Message\nflag-synonym:AFRAID:00\n");

        assertFalse(result.items().get(0).isoFlagExactlySyn());
    }

    @Test
    void aFlagSynonymExactOfTwoIsExactAsInC() throws IOException {
        ParseResult<PlayerTimedEffect> result = load("synonym-2.txt", "name:FAST\ngrade:G:100:Test:Message\nflag-synonym:AFRAID:2\n");

        assertTrue(result.items().get(0).isoFlagExactlySyn());
    }

    @Test
    void anUnknownFlagSynonymStopsTheLoad() {
        assertStopsTheLoad("bad-synonym.txt", "name:FAST\ngrade:G:100:Test:Message\nflag-synonym:NOTAFLAG:1\n", 1,
                "has an illegal object flag synonym value: NOTAFLAG");
    }

    @Test
    void eachFailCodeOneToFiveResolvesItsOwnKindOfName() throws IOException {
        // TMD_FAIL_FLAG_OBJECT, _RESIST, _VULN, _PLAYER, _TIMED_EFFECT = 1..5 in player-timed.h.
        // The lines are written 1 to 5, and C links each in at the head, so the list runs 5 to 1.
        ParseResult<PlayerTimedEffect> result = load("fail-codes.txt", "name:FAST\ngrade:G:100:Test:Message\n"
                        + "fail:1:FREE_ACT\nfail:2:POIS\nfail:3:FIRE\nfail:4:ROCK\nfail:5:OPP_POIS\n");

        List<TimedFailure> fails = result.items().get(0).getFail();
        assertEquals(5, fails.size());
        assertEquals(TimedEffectReasonType.TYPE_TIMED_EFFECT, fails.get(0).getCode());
        assertEquals(TimedEffectReasonType.TYPE_PLAYER_FLAG, fails.get(1).getCode());
        assertEquals(TimedEffectReasonType.TYPE_VULN, fails.get(2).getCode());
        assertEquals(TimedEffectReasonType.TYPE_RESIST, fails.get(3).getCode());
        assertEquals(TimedEffectReasonType.TYPE_OBJECT_FLAG, fails.get(4).getCode());
    }

    @Test
    void aFailCodeOutsideOneToFiveStopsTheLoad() {
        for (String code : List.of("0", "6")) {
            assertStopsTheLoad("fail-code.txt", "name:FAST\ngrade:G:100:Test:Message\nfail:" + code + ":POIS\n", 1,
                    "has an illegal fail type value: " + code);
        }
    }

    @Test
    void aFailNameFromTheWrongListStopsTheLoad() {
        // POIS is an element, so it is not an object flag.
        assertStopsTheLoad("fail-wrong-list.txt", "name:FAST\ngrade:G:100:Test:Message\nfail:1:POIS\n", 1,
                "has an illegal fail value: POIS");
    }

    @Test
    void aSlayThatIsNotLoadedStopsTheLoad() {
        assertStopsTheLoad("bad-slay.txt", "name:ATT_EVIL\ngrade:G:100:Test:Message\nslay:NOTASLAY\n", 1,
                "has an illegal slay value: NOTASLAY");
    }

    @Test
    void anEffectNameAndTheDirectivesAfterItDoNotNeedAGrade() throws IOException {
        // Only a grade: line is optional in C; a record of just name: and desc: is legal.
        ParseResult<PlayerTimedEffect> result = load("no-grade.txt", "name:FAST\ndesc:Quick\n");

        assertEquals(1, result.items().size());
        assertEquals(1, result.items().get(0).getGrade().size());
    }
}
