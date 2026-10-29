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

package uk.co.jackoftradesltd.backend.parser.playertimed;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.backend.io.bespokeexceptions.DataFileParseException;
import uk.co.jackoftradesltd.backend.parser.BrandReader;
import uk.co.jackoftradesltd.backend.parser.SlayReader;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.enums.MessageType;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.player.PlayerTimedEffect;
import uk.co.jackoftradesltd.middle.player.TimedFailure;
import uk.co.jackoftradesltd.middle.player.TimedGrade;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffectReasonType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins what {@link PlayerTimedAssembler} does with each field of a {@code player_timed.txt} record,
 * with the expected values taken from the C handlers {@code parse_player_timed_*} in
 * {@code player-timed.c} rather than from the Java. The grade rules live in
 * {@link PlayerTimedAssemblerGradeTest}, the repeated-name merge in
 * {@link PlayerTimedAssemblerDuplicateNameTest}, and the real file in {@code PlayerTimedReaderTest}.
 *
 * <p>Every bad value must end the load with a {@link DataFileParseException} that names the
 * record's line and the file, as C's parser stops on its first error. Records are built by hand, so
 * a record's {@code fail:} lines are given in the order the grammar delivers them, which is
 * reversed file order.
 *
 * <p>Class PlayerTimedAssemblerTest coded on 261029, commented in full on 261029.
 *
 * @author Rowan Crowther
 */
class PlayerTimedAssemblerTest {

    private static final String FILE = "player_timed.txt";
    private static final int LINE = 7;

    private static Object savedBrands;
    private static Object savedSlays;

    /**
     * Loads the shipped brands and slays into the object registry, since the {@code brand:} and
     * {@code slay:} directives resolve against them.
     *
     * @throws Exception if the registry fields cannot be reached
     */
    @BeforeAll
    static void seedRegistries() throws Exception {
        savedBrands = set("brands", new BrandReader().parseWithResults("lib/gamedata/brand.txt").items());
        savedSlays = set("slays", new SlayReader().parseWithResults("lib/gamedata/slay.txt").items());
    }

    @AfterAll
    static void restoreRegistries() throws Exception {
        set("brands", savedBrands);
        set("slays", savedSlays);
    }

    private static Object set(String field, Object value) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    // ---- fixtures ---------------------------------------------------------

    /**
     * The grade every record carries unless a test is about grades: it needs one so the effect
     * has a band, and its fields are not looked at.
     */
    private static final PlayerTimedParseRecord.PlayerTimedGradeParseRecord GRADE =
            new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", "10", "Status", null, "Up");

    /**
     * A record with every optional field absent, as the grammar leaves it.
     *
     * @param name the {@code name:} token
     * @return the parse record
     */
    private static PlayerTimedParseRecord bare(String name) {
        return new PlayerTimedParseRecord(name, "", List.of(GRADE), "", "", "", "", List.of(), null, null,
                "", "", "", "", "", "", List.of(), LINE);
    }

    /**
     * A bare {@code FAST} record with the given {@code fail:} lines, in grammar (reversed file) order.
     *
     * @param fails the lines
     * @return the parse record
     */
    private static PlayerTimedParseRecord withFails(PlayerTimedParseRecord.FailureParseRecord... fails) {
        return new PlayerTimedParseRecord("FAST", "", List.of(GRADE), "", "", "", "", List.of(fails), null,
                null, "", "", "", "", "", "", List.of(), LINE);
    }

    /**
     * A bare {@code FAST} record with one field set through the full constructor.
     *
     * @param description   the {@code desc:} text
     * @param onEnd         the {@code on-end:} text
     * @param onIncrease    the {@code on-increase:} text
     * @param onDecrease    the {@code on-decrease:} text
     * @param msgT          the {@code msgt:} token
     * @param resist        the {@code resist:} token
     * @param brand         the {@code brand:} code
     * @param slay          the {@code slay:} code
     * @param synonym       the {@code flag-synonym:} code
     * @param synonymExact  the {@code flag-synonym:} exact value
     * @param lowerBound    the {@code lower-bound:} text
     * @param flags         the {@code flags:} tokens
     * @return the parse record
     */
    private static PlayerTimedParseRecord fields(String description, String onEnd, String onIncrease,
                                                 String onDecrease, String msgT, String resist, String brand,
                                                 String slay, String synonym, String synonymExact,
                                                 String lowerBound, List<String> flags) {
        return new PlayerTimedParseRecord("FAST", description, List.of(GRADE), onEnd, onIncrease, onDecrease,
                msgT, List.of(), null, null, resist, brand, slay, synonym, synonymExact, lowerBound, flags, LINE);
    }

    private static PlayerTimedParseRecord.FailureParseRecord fail(String type, String value) {
        return new PlayerTimedParseRecord.FailureParseRecord(type, value);
    }

    private static PlayerTimedEffect assembleOne(PlayerTimedParseRecord record) {
        List<PlayerTimedEffect> effects = new PlayerTimedAssembler().assemble(List.of(record), new ArrayList<>(), FILE);
        assertEquals(1, effects.size());
        return effects.get(0);
    }

    /**
     * Assembles one record and checks the load stops: the message ends with the given tail and
     * names the record's line and the file.
     *
     * @param record the record to assemble
     * @param tail   the part of the message that says what was wrong
     */
    private static void assertStopsTheLoad(PlayerTimedParseRecord record, String tail) {
        DataFileParseException thrown = assertThrows(DataFileParseException.class,
                () -> new PlayerTimedAssembler().assemble(List.of(record), new ArrayList<>(), FILE));

        assertTrue(thrown.getMessage().contains(tail), thrown::getMessage);
        assertTrue(thrown.getMessage().contains("line: " + LINE + " "), thrown::getMessage);
        assertTrue(thrown.getMessage().contains(FILE), thrown::getMessage);
    }

    // ---- defaults ---------------------------------------------------------

    @Test
    @DisplayName("a record with only a name and a grade gets C's zeroed defaults")
    void aBareRecordGetsTheDefaults() {
        PlayerTimedEffect fast = assembleOne(bare("FAST"));

        assertAll(
                () -> assertEquals(TimedEffect.TMD_FAST, fast.getName()),
                () -> assertNull(fast.getDescription(), "no desc: line, so C's pointer is NULL"),
                () -> assertNull(fast.getOnEnd()),
                () -> assertNull(fast.getOnIncrease()),
                () -> assertNull(fast.getOnDecrease()),
                () -> assertEquals(MessageType.MSG_GENERIC, fast.getMsgT(), "msgt starts at 0"),
                () -> assertTrue(fast.getFail().isEmpty()),
                () -> assertTrue(fast.getOnBeginEffect().isEmpty()),
                () -> assertTrue(fast.getOnEndEffect().isEmpty()),
                () -> assertEquals(ElementEnum.ELEM_NONE, fast.getTempResist(), "C holds -1"),
                () -> assertNull(fast.getTempBrand(), "C holds -1"),
                () -> assertNull(fast.getTempSlay(), "C holds -1"),
                () -> assertEquals(ObjectFlag.OF_NONE, fast.getoFlagDup()),
                () -> assertFalse(fast.isoFlagExactlySyn()),
                () -> assertEquals(0, fast.getLowerBound()),
                () -> assertFalse(fast.isNonStacking()));
    }

    @Test
    @DisplayName("text lines that are given are kept as written")
    void textLinesAreKept() {
        PlayerTimedEffect fast = assembleOne(fields("slow thing", "It ends.", "Up.", "Down.", "", "", "", "", "",
                "", "", List.of()));

        assertAll(
                () -> assertEquals("slow thing", fast.getDescription()),
                () -> assertEquals("It ends.", fast.getOnEnd()),
                () -> assertEquals("Up.", fast.getOnIncrease()),
                () -> assertEquals("Down.", fast.getOnDecrease()));
    }

    // ---- name -------------------------------------------------------------

    @Test
    @DisplayName("an unknown name stops the load")
    void anUnknownNameStopsTheLoad() {
        assertStopsTheLoad(bare("WIBBLE"), "has an illegal name value: WIBBLE");
    }

    @Test
    @DisplayName("a name is case-sensitive, as grab_name's streq is")
    void aLowerCaseNameStopsTheLoad() {
        assertStopsTheLoad(bare("fast"), "has an illegal name value: fast");
    }

    @Test
    @DisplayName("NONE and MAX are refused as names")
    void theSentinelNamesAreRefused() {
        assertStopsTheLoad(bare("NONE"), "has an illegal sentinel name value: NONE");
        assertStopsTheLoad(bare("MAX"), "has an illegal sentinel name value: MAX");
    }

    // ---- msgt -------------------------------------------------------------

    @Test
    @DisplayName("msgt: resolves a message name, without regard to case")
    void msgtResolvesWithoutRegardToCase() {
        assertEquals(MessageType.MSG_HIT, assembleOne(fields("", "", "", "", "HIT", "", "", "", "", "", "",
                List.of())).getMsgT());
        assertEquals(MessageType.MSG_HIT, assembleOne(fields("", "", "", "", "hit", "", "", "", "", "", "",
                List.of())).getMsgT());
    }

    @Test
    @DisplayName("an unknown msgt: stops the load")
    void anUnknownMsgtStopsTheLoad() {
        assertStopsTheLoad(fields("", "", "", "", "NOTAMESSAGE", "", "", "", "", "", "", List.of()),
                "has an illegal message type value: NOTAMESSAGE");
    }

    // ---- fail -------------------------------------------------------------

    @Test
    @DisplayName("fail codes 1 to 5 select an object flag, resist, vulnerability, player flag, timed effect")
    void failCodesOneToFiveSelectTheirKind() {
        PlayerTimedEffect fast = assembleOne(withFails(fail("5", "OPP_POIS"), fail("4", "ROCK"), fail("3", "FIRE"),
                fail("2", "POIS"), fail("1", "FREE_ACT")));
        List<TimedFailure> fails = fast.getFail();

        assertAll(
                () -> assertEquals(5, fails.size()),
                () -> assertEquals(TimedEffectReasonType.TYPE_TIMED_EFFECT, fails.get(0).getCode()),
                () -> assertEquals(TimedEffect.TMD_OPP_POIS, fails.get(0).getEffectCode()),
                () -> assertEquals(TimedEffectReasonType.TYPE_PLAYER_FLAG, fails.get(1).getCode()),
                () -> assertEquals(PlayerFlag.PF_ROCK, fails.get(1).getPlayerFlagCode()),
                () -> assertEquals(TimedEffectReasonType.TYPE_VULN, fails.get(2).getCode()),
                () -> assertEquals(ElementEnum.ELEM_FIRE, fails.get(2).getElementCode()),
                () -> assertEquals(TimedEffectReasonType.TYPE_RESIST, fails.get(3).getCode()),
                () -> assertEquals(ElementEnum.ELEM_POIS, fails.get(3).getElementCode()),
                () -> assertEquals(TimedEffectReasonType.TYPE_OBJECT_FLAG, fails.get(4).getCode()),
                () -> assertEquals(ObjectFlag.OF_FREE_ACT, fails.get(4).getObjFlagCode()));
    }

    @Test
    @DisplayName("the stored fail list runs last file line to first, as C's head insertion builds it")
    void failLinesAreKeptInTheOrderCBuildsThem() {
        // The file says ACID, ELEC, FIRE; the grammar hands them over as FIRE, ELEC, ACID.
        PlayerTimedEffect fast = assembleOne(withFails(fail("2", "FIRE"), fail("2", "ELEC"), fail("2", "ACID")));

        assertEquals(List.of(ElementEnum.ELEM_FIRE, ElementEnum.ELEM_ELEC, ElementEnum.ELEM_ACID),
                fast.getFail().stream().map(TimedFailure::getElementCode).toList());
    }

    @Test
    @DisplayName("a fail code outside 1 to 5 stops the load")
    void aFailCodeOutsideOneToFiveStopsTheLoad() {
        assertStopsTheLoad(withFails(fail("0", "POIS")), "has an illegal fail type value: 0");
        assertStopsTheLoad(withFails(fail("6", "POIS")), "has an illegal fail type value: 6");
    }

    @Test
    @DisplayName("a fail name from the wrong list stops the load")
    void aFailNameFromTheWrongListStopsTheLoad() {
        assertStopsTheLoad(withFails(fail("1", "POIS")), "has an illegal fail value: POIS");
        assertStopsTheLoad(withFails(fail("2", "FREE_ACT")), "has an illegal fail value: FREE_ACT");
        assertStopsTheLoad(withFails(fail("4", "FREE_ACT")), "has an illegal fail value: FREE_ACT");
        assertStopsTheLoad(withFails(fail("5", "POIS")), "has an illegal fail value: POIS");
    }

    @Test
    @DisplayName("the NONE sentinel is refused in every fail list")
    void theNoneSentinelIsRefusedInEveryFailList() {
        assertStopsTheLoad(withFails(fail("1", "NONE")), "has an illegal object flag sentinel value: NONE");
        assertStopsTheLoad(withFails(fail("2", "NONE")), "has an illegal resist element sentinel value: NONE");
        assertStopsTheLoad(withFails(fail("3", "NONE")), "has an illegal vulnerable element sentinel value: NONE");
        assertStopsTheLoad(withFails(fail("4", "NONE")), "has an illegal player flag sentinel value: NONE");
        assertStopsTheLoad(withFails(fail("5", "NONE")), "has an illegal timed effect sentinel value: NONE");
    }

    @Test
    @DisplayName("MAX is refused for objects and elements, but C's timed effect list holds it")
    void maxIsRefusedWhereCsListLacksIt() {
        assertStopsTheLoad(withFails(fail("1", "MAX")), "has an illegal object flag sentinel value: MAX");
        assertStopsTheLoad(withFails(fail("2", "MAX")), "has an illegal resist element sentinel value: MAX");
        assertEquals(TimedEffect.TMD_MAX,
                assembleOne(withFails(fail("5", "MAX"))).getFail().get(0).getEffectCode());
    }

    // ---- grade presentation ----------------------------------------------

    @Test
    @DisplayName("a one-character status or up message is a dummy, a longer one is kept")
    void oneCharacterStatusAndUpMessageAreDummies() {
        PlayerTimedParseRecord dummy = new PlayerTimedParseRecord("FAST", "", List.of(
                new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", "10", "-", null, "-"),
                new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", "20", "Hi", "Dn", "Up")), "", "", "", "",
                List.of(), null, null, "", "", "", "", "", "", List.of(), LINE);
        List<TimedGrade> grades = assembleOne(dummy).getGrade();

        assertAll(
                () -> assertNull(grades.get(1).status()),
                () -> assertNull(grades.get(1).upMsg()),
                () -> assertNull(grades.get(1).downMsg(), "no fifth field"),
                () -> assertEquals("Hi", grades.get(2).status()),
                () -> assertEquals("Up", grades.get(2).upMsg()),
                () -> assertEquals("Dn", grades.get(2).downMsg()));
    }

    @Test
    @DisplayName("a one-character down message is kept as written")
    void aOneCharacterDownMessageIsKept() {
        PlayerTimedParseRecord record = new PlayerTimedParseRecord("FAST", "", List.of(
                new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("w", "10", "Status", "X", "Up")), "", "", "",
                "", List.of(), null, null, "", "", "", "", "", "", List.of(), LINE);

        assertEquals("X", assembleOne(record).getGrade().get(1).downMsg());
    }

    @Test
    @DisplayName("a grade colour is its letter, and an unknown letter becomes white")
    void aGradeColourIsItsLetterAndUnknownIsWhite() {
        PlayerTimedParseRecord record = new PlayerTimedParseRecord("FAST", "", List.of(
                new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("g", "10", "Status", null, "Up"),
                new PlayerTimedParseRecord.PlayerTimedGradeParseRecord("?", "20", "Status", null, "Up")), "", "", "",
                "", List.of(), null, null, "", "", "", "", "", "", List.of(), LINE);
        List<TimedGrade> grades = assembleOne(record).getGrade();

        assertAll(
                () -> assertEquals(ColourEnum.COLOUR_GREEN, grades.get(1).colour()),
                () -> assertEquals(ColourEnum.COLOUR_WHITE, grades.get(2).colour()));
    }

    // ---- resist, brand, slay ---------------------------------------------

    @Test
    @DisplayName("resist: resolves an element, without regard to case")
    void resistResolvesWithoutRegardToCase() {
        assertEquals(ElementEnum.ELEM_ACID, assembleOne(fields("", "", "", "", "", "ACID", "", "", "", "", "",
                List.of())).getTempResist());
        assertEquals(ElementEnum.ELEM_ACID, assembleOne(fields("", "", "", "", "", "acid", "", "", "", "", "",
                List.of())).getTempResist());
    }

    @Test
    @DisplayName("an unknown resist: stops the load, and NONE and MAX are refused")
    void anUnknownResistStopsTheLoad() {
        assertStopsTheLoad(fields("", "", "", "", "", "NOTREAL", "", "", "", "", "", List.of()),
                "has an illegal resist value: NOTREAL");
        assertStopsTheLoad(fields("", "", "", "", "", "NONE", "", "", "", "", "", List.of()),
                "has an illegal resist sentinel value: NONE");
        assertStopsTheLoad(fields("", "", "", "", "", "MAX", "", "", "", "", "", List.of()),
                "has an illegal resist sentinel value: MAX");
    }

    @Test
    @DisplayName("brand: and slay: resolve by code, and an unknown code stops the load")
    void brandAndSlayResolveByCode() {
        PlayerTimedEffect fast = assembleOne(fields("", "", "", "", "", "", "ACID_3", "EVIL_2", "", "", "", List.of()));

        assertAll(
                () -> assertEquals("ACID_3", fast.getTempBrand().getCode()),
                () -> assertEquals("EVIL_2", fast.getTempSlay().getCode()));
        assertStopsTheLoad(fields("", "", "", "", "", "", "NOTABRAND", "", "", "", "", List.of()),
                "has an illegal brand value: NOTABRAND");
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "NOTASLAY", "", "", "", List.of()),
                "has an illegal slay value: NOTASLAY");
    }

    @Test
    @DisplayName("brand and slay codes are case-sensitive, as streq is")
    void brandAndSlayCodesAreCaseSensitive() {
        assertStopsTheLoad(fields("", "", "", "", "", "", "acid_3", "", "", "", "", List.of()),
                "has an illegal brand value: acid_3");
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "evil_2", "", "", "", List.of()),
                "has an illegal slay value: evil_2");
    }

    // ---- flag-synonym -----------------------------------------------------

    @Test
    @DisplayName("flag-synonym: sets the flag, and exact is true for any non-zero value")
    void flagSynonymExactIsNonZero() {
        assertAll(
                () -> assertFalse(assembleOne(fields("", "", "", "", "", "", "", "", "AFRAID", "0", "", List.of()))
                        .isoFlagExactlySyn()),
                () -> assertFalse(assembleOne(fields("", "", "", "", "", "", "", "", "AFRAID", "00", "", List.of()))
                        .isoFlagExactlySyn(), "00 reads as 0"),
                () -> assertTrue(assembleOne(fields("", "", "", "", "", "", "", "", "AFRAID", "1", "", List.of()))
                        .isoFlagExactlySyn()),
                () -> assertTrue(assembleOne(fields("", "", "", "", "", "", "", "", "AFRAID", "2", "", List.of()))
                        .isoFlagExactlySyn()),
                () -> assertTrue(assembleOne(fields("", "", "", "", "", "", "", "", "AFRAID", "-1", "", List.of()))
                        .isoFlagExactlySyn()),
                () -> assertEquals(ObjectFlag.OF_AFRAID,
                        assembleOne(fields("", "", "", "", "", "", "", "", "AFRAID", "1", "", List.of()))
                                .getoFlagDup()));
    }

    @Test
    @DisplayName("an unknown or sentinel flag-synonym: stops the load")
    void anUnknownFlagSynonymStopsTheLoad() {
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "NOTAFLAG", "1", "", List.of()),
                "has an illegal object flag synonym value: NOTAFLAG");
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "NONE", "1", "", List.of()),
                "has an illegal object flag synonym sentinel value: NONE");
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "MAX", "1", "", List.of()),
                "has an illegal object flag synonym sentinel value: MAX");
    }

    // ---- lower-bound ------------------------------------------------------

    @Test
    @DisplayName("lower-bound: takes 0 to 32767 and refuses anything outside")
    void lowerBoundTakesZeroTo32767() {
        assertEquals(0, assembleOne(fields("", "", "", "", "", "", "", "", "", "", "0", List.of())).getLowerBound());
        assertEquals(32767, assembleOne(fields("", "", "", "", "", "", "", "", "", "", "32767", List.of()))
                .getLowerBound());
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "", "", "32768", List.of()),
                "has an out of bounds lower bound value: 32768");
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "", "", "-1", List.of()),
                "has an out of bounds lower bound value: -1");
    }

    // ---- flags ------------------------------------------------------------

    @Test
    @DisplayName("flags: accepts NONSTACKING, alone or repeated, and nothing else")
    void flagsAcceptOnlyNonStacking() {
        assertTrue(assembleOne(fields("", "", "", "", "", "", "", "", "", "", "", List.of("NONSTACKING")))
                .isNonStacking());
        assertTrue(assembleOne(fields("", "", "", "", "", "", "", "", "", "", "",
                List.of("NONSTACKING", "NONSTACKING"))).isNonStacking());
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "", "", "", List.of("WIBBLE")),
                "has an illegal flag value: WIBBLE");
        assertStopsTheLoad(fields("", "", "", "", "", "", "", "", "", "", "", List.of("nonstacking")),
                "has an illegal flag value: nonstacking");
    }
}
