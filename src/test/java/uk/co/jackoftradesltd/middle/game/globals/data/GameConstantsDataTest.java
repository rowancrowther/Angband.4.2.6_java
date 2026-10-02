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

package uk.co.jackoftradesltd.middle.game.globals.data;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.enums.MessageType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link GameConstantsData.GameConstantsBuilder} against the C it ports: the
 * {@code parse_constants_*()} handlers, {@code check_critical_levels()} and
 * {@code finish_parse_constants()} in {@code init.c}, and the zeroed {@code struct angband_constants}
 * that {@code init_parse_constants()} allocates.
 *
 * <p>Expected values come from the C. A constant no line sets reads {@code 0} ({@code mem_zalloc}).
 * Each {@code constants.txt} label lands in exactly one {@code z_info} field. Level rows keep file
 * order (C appends to the tail of a linked list). The O-combat level handlers reject a zero
 * {@code chance}, and their {@code uint} fields reject anything negative. The melee and ranged
 * cutoffs must strictly increase from the second row to the second-to-last, with the last row's cutoff
 * never compared. The one deliberate Java-only rule (a middle {@code -1} cutoff is rejected) is
 * tested separately and labelled as a divergence.
 *
 * <p>Class GameConstantsDataTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
class GameConstantsDataTest {

    private static final String MELEE_NOT_INCREASING = "Melee critical level data not strictly increasing.";
    private static final String RANGED_NOT_INCREASING = "Ranged critical level data not strictly increasing.";
    private static final String MELEE_SENTINEL =
            "Invalid sentinel value -1 found in middle of melee critical level data";
    private static final String RANGED_SENTINEL =
            "Invalid sentinel value -1 found in middle of ranged critical level data";

    /**
     * Builds a melee level row with only the cutoff mattering; the other fields are fixed.
     *
     * <p>Function melee coded on 261002, commented in full on 261002.
     *
     * @param cutoff the power cutoff
     * @return the row
     */
    private static MeleeCriticalLevelData melee(int cutoff) {
        return new MeleeCriticalLevelData(cutoff, 2, 5, MessageType.MSG_HIT_GOOD);
    }

    /**
     * Builds a ranged level row with only the cutoff mattering; the other fields are fixed.
     *
     * <p>Function ranged coded on 261002, commented in full on 261002.
     *
     * @param cutoff the power cutoff
     * @return the row
     */
    private static RangedCriticalLevelData ranged(int cutoff) {
        return new RangedCriticalLevelData(cutoff, 2, 5, MessageType.MSG_HIT_GOOD);
    }

    /**
     * Returns a builder holding the given melee cutoffs in order, and no ranged rows.
     *
     * <p>Function withMelee coded on 261002, commented in full on 261002.
     *
     * @param cutoffs the cutoffs, in file order
     * @return the builder
     */
    private static GameConstantsData.GameConstantsBuilder withMelee(int... cutoffs) {
        GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
        for (int c : cutoffs) {
            b.addMeleeCriticalLevelData(melee(c));
        }
        return b;
    }

    /**
     * Returns a builder holding the given ranged cutoffs in order, and no melee rows.
     *
     * <p>Function withRanged coded on 261002, commented in full on 261002.
     *
     * @param cutoffs the cutoffs, in file order
     * @return the builder
     */
    private static GameConstantsData.GameConstantsBuilder withRanged(int... cutoffs) {
        GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
        for (int c : cutoffs) {
            b.addRangedCriticalLevelData(ranged(c));
        }
        return b;
    }

    /**
     * {@code build()}: defaulting, the error gate, and the label-to-field wiring.
     *
     * <p>Class Build coded on 261002, commented in full on 261002.
     */
    @Nested
    @DisplayName("build")
    class Build {

        /**
         * With nothing set, every constant reads 0 and every level list is empty, as in C's zeroed
         * struct.
         *
         * <p>Function nothingSetGivesAllZero coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("nothing set -> every constant 0, every list empty (mem_zalloc)")
        void nothingSetGivesAllZero() {
            GameConstantsData d = new GameConstantsData.GameConstantsBuilder().build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(new LevelMaxData(0), d.levelMax());
            assertEquals(new MonGenData(0, 0, 0, 0, 0, 0, 0, 0, 0), d.monGen());
            assertEquals(new MonPlayData(0, 0, 0, 0, 0), d.monPlay());
            assertEquals(new DunGenData(0, 0, 0, 0, 0, 0, 0, 0), d.dunGen());
            assertEquals(new WorldData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), d.world());
            assertEquals(new CarryCapData(0, 0, 0, 0, 0), d.carryCap());
            assertEquals(new StoreData(0, 0, 0, 0), d.store());
            assertEquals(new ObjMakeData(0, 0, 0, 0, 0, 0), d.objMake());
            assertEquals(new PlayerData(0, 0, 0, 0), d.player());
            assertEquals(new MeleeCriticalData(0, 0, 0, 0, 0, 0, 0, 0, 0), d.meleeCritical());
            assertEquals(new RangedCriticalData(0, 0, 0, 0, 0, 0, 0, 0, 0, 0), d.rangedCritical());
            assertEquals(new OMeleeCriticalData(0, 0, 0, 0, 0, 0), d.oMeleeCritical());
            assertEquals(new ORangedCriticalData(0, 0, 0, 0, 0, 0, 0, 0), d.oRangedCritical());
            assertTrue(d.meleeCriticalLevel().isEmpty());
            assertTrue(d.rangedCriticalLevel().isEmpty());
            assertTrue(d.oMeleeCriticalLevel().isEmpty());
            assertTrue(d.oRangedCriticalLevel().isEmpty());
        }

        /**
         * Any error already collected makes the whole file fail, as C's {@code run_parser()} does.
         *
         * <p>Function anyErrorGivesNull coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("errors not empty -> null")
        void anyErrorGivesNull() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            b.setWorldMaxDepth(128);
            assertNull(b.build(new ArrayList<>(List.of("Line: 3 unknown category"))));
        }

        /**
         * Setting only some constants leaves the rest at 0, and the set ones keep their values.
         *
         * <p>Function partialSetLeavesRestZero coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("partial set -> unset constants still 0")
        void partialSetLeavesRestZero() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            b.setWorldMaxDepth(128);
            b.setWorldMoveEnergy(100);
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(new WorldData(128, 0, 0, 0, 0, 0, 0, 0, 0, 100), d.world());
        }

        /**
         * Every setter lands in the record component that matches its C {@code z_info} field. Each
         * constant gets a distinct value, so any swapped argument in {@code build()} shows up.
         *
         * <p>Function everySetterLandsInItsOwnComponent coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("each label lands in its own field (distinct values catch any swap)")
        void everySetterLandsInItsOwnComponent() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            b.setLevelMaxMonsters(1);

            b.setMonGenChance(11);
            b.setMonGenLevelMin(12);
            b.setMonGenTownDay(13);
            b.setMonGenTownNight(14);
            b.setMonGenReproMax(15);
            b.setMonGenOodChance(16);
            b.setMonGenOodAmount(17);
            b.setMonGenGroupMax(18);
            b.setMonGenGroupDist(19);

            b.setMonPlayBreakGlyph(21);
            b.setMonPlayMultRate(22);
            b.setMonPlayLifeDrain(23);
            b.setMonPlayFleeRange(24);
            b.setMonPlayTurnRange(25);

            b.setDunGenCentMax(31);
            b.setDunGenDoorMax(32);
            b.setDunGenWallMax(33);
            b.setDunGenTunnMax(34);
            b.setDunGenAmtRoom(35);
            b.setDunGenAmtItem(36);
            b.setDunGenAmtGold(37);
            b.setDunGenPitMax(38);

            b.setWorldMaxDepth(41);
            b.setWorldDayLength(42);
            b.setWorldDungeonHgt(43);
            b.setWorldDungeonWid(44);
            b.setWorldTownHgt(45);
            b.setWorldTownWid(46);
            b.setWorldFeelingTotal(47);
            b.setWorldFeelingNeed(48);
            b.setWorldStairSkip(49);
            b.setWorldMoveEnergy(50);

            b.setCarryCapPackSize(51);
            b.setCarryCapQuiverSize(52);
            b.setCarryCapQuiverSlotSize(53);
            b.setCarryCapThrownQuiverMult(54);
            b.setCarryCapFloorSize(55);

            b.setStoreInvenMax(61);
            b.setStoreTurns(62);
            b.setStoreShuffle(63);
            b.setStoreMagicLevel(64);

            b.setObjMakeMaxDepth(71);
            b.setObjGreatObj(72);
            b.setObjGreatEgo(73);
            b.setObjFuelTorch(74);
            b.setObjFuelLamp(75);
            b.setObjDefaultLamp(76);

            b.setPlayerMaxSight(81);
            b.setPlayerMaxRange(82);
            b.setPlayerStartGold(83);
            b.setPlayerFoodValue(84);

            b.setMeleeCriticalDebuffToh(91);
            b.setMeleeCriticalChanceWeightScale(92);
            b.setMeleeCriticalChanceTohScale(93);
            b.setMeleeCriticalChanceLevelScale(94);
            b.setMeleeCriticalChanceTohSkillScale(95);
            b.setMeleeCriticalChanceOffset(96);
            b.setMeleeCriticalChanceRange(97);
            b.setMeleeCriticalPowerWeightScale(98);
            b.setMeleeCriticalPowerRandom(99);

            b.setRangedCriticalDebuffToh(101);
            b.setRangedCriticalChanceWeightScale(102);
            b.setRangedCriticalChanceTohScale(103);
            b.setRangedCriticalChanceLevelScale(104);
            b.setRangedCriticalChanceLaunchedTohSkillScale(105);
            b.setRangedCriticalChanceThrownTohSkillScale(106);
            b.setRangedCriticalChanceOffset(107);
            b.setRangedCriticalChanceRange(108);
            b.setRangedCriticalPowerWeightScale(109);
            b.setRangedCriticalPowerRandom(110);

            b.setOMeleeCriticalDebuffToh(111);
            b.setOMeleeCriticalPowerTohScaleNumerator(112);
            b.setOMeleeCriticalPowerTohScaleDenominator(113);
            b.setOMeleeCriticalChancePowerScaleNumerator(114);
            b.setOMeleeCriticalChancePowerScaleDenominator(115);
            b.setOMeleeCriticalChanceAddDenominator(116);

            b.setORangedCriticalDebuffToh(121);
            b.setORangedCriticalPowerLaunchedTohScaleNumerator(122);
            b.setORangedCriticalPowerLaunchedTohScaleDenominator(123);
            b.setORangedCriticalPowerThrownTohScaleNumerator(124);
            b.setORangedCriticalPowerThrownTohScaleDenominator(125);
            b.setORangedCriticalChancePowerScaleNumerator(126);
            b.setORangedCriticalChancePowerScaleDenominator(127);
            b.setORangedCriticalChanceAddDenominator(128);

            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(new LevelMaxData(1), d.levelMax());
            assertEquals(new MonGenData(11, 12, 13, 14, 15, 16, 17, 18, 19), d.monGen());
            assertEquals(new MonPlayData(21, 22, 23, 24, 25), d.monPlay());
            assertEquals(new DunGenData(31, 32, 33, 34, 35, 36, 37, 38), d.dunGen());
            assertEquals(new WorldData(41, 42, 43, 44, 45, 46, 47, 48, 49, 50), d.world());
            assertEquals(new CarryCapData(51, 52, 53, 54, 55), d.carryCap());
            assertEquals(new StoreData(61, 62, 63, 64), d.store());
            assertEquals(new ObjMakeData(71, 72, 73, 74, 75, 76), d.objMake());
            assertEquals(new PlayerData(81, 82, 83, 84), d.player());
            assertEquals(new MeleeCriticalData(91, 92, 93, 94, 95, 96, 97, 98, 99), d.meleeCritical());
            assertEquals(new RangedCriticalData(101, 102, 103, 104, 105, 106, 107, 108, 109, 110),
                    d.rangedCritical());
            assertEquals(new OMeleeCriticalData(111, 112, 113, 114, 115, 116), d.oMeleeCritical());
            assertEquals(new ORangedCriticalData(121, 122, 123, 124, 125, 126, 127, 128), d.oRangedCritical());
        }

        /**
         * Critical constants may be negative: C's {@code parse_constants_*_critical()} handlers have no
         * {@code value < 0} check, so a negative value is stored as given.
         *
         * <p>Function negativeCriticalConstantKept coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("negative critical constant kept (C has no value < 0 check there)")
        void negativeCriticalConstantKept() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            b.setMeleeCriticalChanceOffset(-20);
            b.setORangedCriticalDebuffToh(-3);
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(-20, d.meleeCritical().chanceOffset());
            assertEquals(-3, d.oRangedCritical().debuffToh());
        }
    }

    /**
     * The four level lists: append order and the O-combat row checks.
     *
     * <p>Class LevelRows coded on 261002, commented in full on 261002.
     */
    @Nested
    @DisplayName("level rows")
    class LevelRows {

        /**
         * Rows keep file order, as C appends each one to the tail of its linked list. These are the
         * {@code o-melee-critical-level} rows from the shipped {@code constants.txt}.
         *
         * <p>Function oMeleeRowsKeepFileOrder coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("o-melee rows keep file order (shipped constants.txt rows)")
        void oMeleeRowsKeepFileOrder() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            List<OMeleeCriticalLevelData> rows = List.of(
                    new OMeleeCriticalLevelData(40, 5, MessageType.MSG_HIT_HI_SUPERB),
                    new OMeleeCriticalLevelData(12, 4, MessageType.MSG_HIT_HI_GREAT),
                    new OMeleeCriticalLevelData(3, 3, MessageType.MSG_HIT_SUPERB),
                    new OMeleeCriticalLevelData(2, 2, MessageType.MSG_HIT_GREAT),
                    new OMeleeCriticalLevelData(1, 1, MessageType.MSG_HIT_GOOD));
            for (OMeleeCriticalLevelData r : rows) {
                assertEquals("", b.addOMeleeCriticalLevelData(r));
            }
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(rows, d.oMeleeCriticalLevel());
        }

        /**
         * Shipped {@code o-ranged-critical-level} rows are all accepted and kept in order.
         *
         * <p>Function oRangedRowsKeepFileOrder coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("o-ranged rows keep file order (shipped constants.txt rows)")
        void oRangedRowsKeepFileOrder() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            List<ORangedCriticalLevelData> rows = List.of(
                    new ORangedCriticalLevelData(50, 3, MessageType.MSG_HIT_SUPERB),
                    new ORangedCriticalLevelData(10, 2, MessageType.MSG_HIT_GREAT),
                    new ORangedCriticalLevelData(1, 1, MessageType.MSG_HIT_GOOD));
            for (ORangedCriticalLevelData r : rows) {
                assertEquals("", b.addORangedCriticalLevel(r));
            }
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(rows, d.oRangedCriticalLevel());
        }

        /**
         * {@code chance == 0} is C's {@code PARSE_ERROR_INVALID_VALUE}; the row is not added.
         *
         * <p>Function zeroChanceRejected coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("chance 0 -> rejected, not added (C: PARSE_ERROR_INVALID_VALUE)")
        void zeroChanceRejected() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            assertEquals("Negative or zero chance found in oMelee critical level data",
                    b.addOMeleeCriticalLevelData(new OMeleeCriticalLevelData(0, 1, MessageType.MSG_HIT_GOOD)));
            assertEquals("Negative or zero chance found in oRanged critical level data",
                    b.addORangedCriticalLevel(new ORangedCriticalLevelData(0, 1, MessageType.MSG_HIT_GOOD)));
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertTrue(d.oMeleeCriticalLevel().isEmpty());
            assertTrue(d.oRangedCriticalLevel().isEmpty());
        }

        /**
         * A negative chance or dice is refused by C's {@code uint} parse (a leading {@code '-'} is
         * {@code PARSE_ERROR_NOT_NUMBER}), so the port must refuse it too.
         *
         * <p>Function negativeChanceOrDiceRejected coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("negative chance or dice -> rejected (C: uint refuses '-')")
        void negativeChanceOrDiceRejected() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            assertEquals("Negative or zero chance found in oMelee critical level data",
                    b.addOMeleeCriticalLevelData(new OMeleeCriticalLevelData(-1, 1, MessageType.MSG_HIT_GOOD)));
            assertEquals("Negative dice found in oMelee critical level data",
                    b.addOMeleeCriticalLevelData(new OMeleeCriticalLevelData(1, -1, MessageType.MSG_HIT_GOOD)));
            assertEquals("Negative or zero chance found in oRanged critical level data",
                    b.addORangedCriticalLevel(new ORangedCriticalLevelData(-1, 1, MessageType.MSG_HIT_GOOD)));
            assertEquals("Negative dice found in oRanged critical level data",
                    b.addORangedCriticalLevel(new ORangedCriticalLevelData(1, -1, MessageType.MSG_HIT_GOOD)));
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertTrue(d.oMeleeCriticalLevel().isEmpty());
            assertTrue(d.oRangedCriticalLevel().isEmpty());
        }

        /**
         * The smallest legal row, {@code chance 1} and {@code dice 0}, is accepted: C only refuses
         * {@code chance == 0}, and {@code dice 0} is a valid {@code uint}.
         *
         * <p>Function chanceOneDiceZeroAccepted coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("chance 1, dice 0 -> accepted (boundary)")
        void chanceOneDiceZeroAccepted() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            assertEquals("", b.addOMeleeCriticalLevelData(new OMeleeCriticalLevelData(1, 0, MessageType.MSG_HIT_GOOD)));
            assertEquals("", b.addORangedCriticalLevel(new ORangedCriticalLevelData(1, 0, MessageType.MSG_HIT_GOOD)));
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(1, d.oMeleeCriticalLevel().size());
            assertEquals(1, d.oRangedCriticalLevel().size());
        }

        /**
         * Melee and ranged rows are appended with no value checks, as in C (which checks only the
         * message), and keep file order.
         *
         * <p>Function meleeAndRangedRowsUncheckedInOrder coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("melee / ranged rows appended unchecked, in order")
        void meleeAndRangedRowsUncheckedInOrder() {
            GameConstantsData.GameConstantsBuilder b = new GameConstantsData.GameConstantsBuilder();
            MeleeCriticalLevelData m1 = new MeleeCriticalLevelData(400, -2, -5, MessageType.MSG_HIT_GOOD);
            MeleeCriticalLevelData m2 = new MeleeCriticalLevelData(-1, 4, 20, MessageType.MSG_HIT_HI_SUPERB);
            RangedCriticalLevelData r1 = new RangedCriticalLevelData(500, 2, 5, MessageType.MSG_HIT_GOOD);
            RangedCriticalLevelData r2 = new RangedCriticalLevelData(-1, 3, 15, MessageType.MSG_HIT_SUPERB);
            b.addMeleeCriticalLevelData(m1);
            b.addMeleeCriticalLevelData(m2);
            b.addRangedCriticalLevelData(r1);
            b.addRangedCriticalLevelData(r2);
            GameConstantsData d = b.build(new ArrayList<>());
            assertNotNull(d);
            assertEquals(List.of(m1, m2), d.meleeCriticalLevel());
            assertEquals(List.of(r1, r2), d.rangedCriticalLevel());
        }
    }

    /**
     * {@code checkCriticalLevelDataLists()} against {@code check_critical_levels()}.
     *
     * <p>Class CheckCriticalLevels coded on 261002, commented in full on 261002.
     */
    @Nested
    @DisplayName("checkCriticalLevelDataLists")
    class CheckCriticalLevels {

        /**
         * The shipped {@code constants.txt} lists pass: melee 400, 700, 900, 1300, -1 and ranged 500,
         * 1000, -1.
         *
         * <p>Function shippedListsPass coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("shipped constants.txt cutoffs -> pass")
        void shippedListsPass() {
            GameConstantsData.GameConstantsBuilder b = withMelee(400, 700, 900, 1300, -1);
            b.addRangedCriticalLevelData(ranged(500));
            b.addRangedCriticalLevelData(ranged(1000));
            b.addRangedCriticalLevelData(ranged(-1));
            assertEquals("", b.checkCriticalLevelDataLists());
        }

        /**
         * Empty lists pass ({@code if (!head) return 0}).
         *
         * <p>Function emptyPasses coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("empty lists -> pass")
        void emptyPasses() {
            assertEquals("", new GameConstantsData.GameConstantsBuilder().checkCriticalLevelDataLists());
        }

        /**
         * One row: C's loop body never runs. Two rows: C compares nothing, because the second row is
         * the last and is skipped. Both pass, whatever the values.
         *
         * <p>Function oneOrTwoRowsAlwaysPass coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("one or two rows -> pass whatever the values")
        void oneOrTwoRowsAlwaysPass() {
            assertEquals("", withMelee(-1).checkCriticalLevelDataLists());
            assertEquals("", withMelee(500, 3).checkCriticalLevelDataLists());
            assertEquals("", withMelee(500, 500).checkCriticalLevelDataLists());
            assertEquals("", withRanged(500, -1).checkCriticalLevelDataLists());
        }

        /**
         * The last row's cutoff is never compared, so a last cutoff below its predecessor passes.
         *
         * <p>Function lastCutoffIgnored coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("last cutoff below previous -> pass (last is ignored)")
        void lastCutoffIgnored() {
            assertEquals("", withMelee(400, 700, 500).checkCriticalLevelDataLists());
            assertEquals("", withRanged(400, 700, 0).checkCriticalLevelDataLists());
        }

        /**
         * Equal cutoffs fail: C's test is {@code cutoff <= prev_cutoff}, so the increase must be strict.
         *
         * <p>Function equalMiddleCutoffFails coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("equal middle cutoff -> not strictly increasing")
        void equalMiddleCutoffFails() {
            assertEquals(MELEE_NOT_INCREASING, withMelee(400, 400, -1).checkCriticalLevelDataLists());
            assertEquals(RANGED_NOT_INCREASING, withRanged(400, 400, -1).checkCriticalLevelDataLists());
        }

        /**
         * The first cutoff takes part as the "previous" value, so a second cutoff below it fails.
         *
         * <p>Function firstCutoffIsComparedAgainst coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("second cutoff below first -> not strictly increasing")
        void firstCutoffIsComparedAgainst() {
            assertEquals(MELEE_NOT_INCREASING, withMelee(1000, 500, -1).checkCriticalLevelDataLists());
            assertEquals("", withMelee(1000, 2000, -1).checkCriticalLevelDataLists());
        }

        /**
         * A decrease deep in the middle is found: the comparison walks every compared position, not
         * just the first pair.
         *
         * <p>Function laterDecreaseFails coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("decrease at a later middle row -> not strictly increasing")
        void laterDecreaseFails() {
            assertEquals(MELEE_NOT_INCREASING, withMelee(400, 700, 900, 800, -1).checkCriticalLevelDataLists());
        }

        /**
         * C checks melee first and returns on its error, so when both lists are bad, the melee error
         * is the one reported.
         *
         * <p>Function meleeReportedBeforeRanged coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("both lists bad -> melee error reported first")
        void meleeReportedBeforeRanged() {
            GameConstantsData.GameConstantsBuilder b = withMelee(400, 300, -1);
            b.addRangedCriticalLevelData(ranged(400));
            b.addRangedCriticalLevelData(ranged(300));
            b.addRangedCriticalLevelData(ranged(-1));
            assertEquals(MELEE_NOT_INCREASING, b.checkCriticalLevelDataLists());
        }

        /**
         * A good melee list does not hide a bad ranged list.
         *
         * <p>Function rangedCheckedAfterGoodMelee coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("melee good, ranged bad -> ranged error")
        void rangedCheckedAfterGoodMelee() {
            GameConstantsData.GameConstantsBuilder b = withMelee(400, 700, -1);
            b.addRangedCriticalLevelData(ranged(400));
            b.addRangedCriticalLevelData(ranged(300));
            b.addRangedCriticalLevelData(ranged(-1));
            assertEquals(RANGED_NOT_INCREASING, b.checkCriticalLevelDataLists());
        }

        /**
         * Intentional divergence (Rowan, 261002): a middle {@code -1} is rejected with the sentinel
         * message. With the shape [400, -1, 900, -1], C also fails, but with
         * {@code PARSE_ERROR_NON_SEQUENTIAL_RECORDS}. With [-5, -1, 10, -1], C passes and Java fails.
         * Both are pinned here so a later change to the rule is a visible decision.
         *
         * <p>Function middleMinusOneRejectedDivergence coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("DIVERGENCE: middle -1 -> sentinel error (C: order error, or pass if still increasing)")
        void middleMinusOneRejectedDivergence() {
            assertEquals(MELEE_SENTINEL, withMelee(400, -1, 900, -1).checkCriticalLevelDataLists());
            assertEquals(MELEE_SENTINEL, withMelee(-5, -1, 10, -1).checkCriticalLevelDataLists());
            assertEquals(RANGED_SENTINEL, withRanged(-5, -1, 10, -1).checkCriticalLevelDataLists());
        }

        /**
         * A {@code -1} as the first cutoff is not in a compared position, so the sentinel rule does
         * not fire. The list then follows C's order rule alone.
         *
         * <p>Function firstMinusOneNotSentinelChecked coded on 261002, commented in full on 261002.
         */
        @Test
        @DisplayName("first cutoff -1 -> only the order rule applies")
        void firstMinusOneNotSentinelChecked() {
            assertEquals("", withMelee(-1, 400, -1).checkCriticalLevelDataLists());
        }
    }
}
