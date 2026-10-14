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

package uk.co.jackoftradesltd.middle.player;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.enums.Stats;
import uk.co.jackoftradesltd.middle.game.GameWorld;
import uk.co.jackoftradesltd.middle.game.globals.registry.PlayerRegistry;
import uk.co.jackoftradesltd.middle.magic.ClassMagic;
import uk.co.jackoftradesltd.middle.objects.ElementInfo;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerSkill;
import uk.co.jackoftradesltd.middle.player.enums.TimedEffect;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the parts of {@link PlayerCalcs#calcBonuses} that turn on arithmetic rather than on what a
 * single item contributes: the stat-to-index conversion, the speed penalty for weight, hunger,
 * the timed statuses, vulnerabilities, and the launcher and weapon analysis. The port of C's
 * {@code calc_bonuses} ({@code player-calcs.c}).
 *
 * <p>{@code PlayerCalcBonusesTest} asks what one worn item contributes; this class asks what the
 * function does with the numbers once it has them. Every expected value is worked from the C source
 * and the C stat tables, and the working is in the comment on each case.
 *
 * <p><b>The character is fed.</b> The fixture's registry holds no timed-effect definitions, so with
 * nothing seeded C's "outside the Fed range" branch would fire for a character with a food counter
 * of zero and every figure here would carry a starvation penalty. {@link #setUp} registers the
 * {@code FOOD}, {@code STUN} and {@code OPP_FIRE} effects with C's grade boundaries (the food
 * boundaries are the percentages in {@code player_timed.txt} times the {@code food-value} of 100)
 * and sets the food counter to 5000, which is in the Fed range. The hunger tests then move it.
 *
 * <p><b>Stat 10 is index 7</b>, and the fixture gives every stat that value. At index 7 every
 * adjustment table in the file gives a known figure: the to-hit, to-dam and armour tables give 0,
 * and each skill table gives 1 ({@code adj_int_dev}, {@code adj_int_dis}, {@code adj_dex_dis},
 * {@code adj_wis_sav}), so a skill probe reads its scaled base plus 1.
 *
 * <p>Class PlayerCalcBonusesDerivedTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcBonusesDerivedTest {

    /**
     * The skill a probe class gives every skill it tests, large enough that the fractional
     * adjustments are whole numbers.
     */
    private static final int PROBE = 100;

    /**
     * What each of the four skill tables adds at stat index 7.
     */
    private static final int AT_TEN = 1;

    /**
     * The character under test.
     */
    private CalcBonusesFixture fixture;

    /**
     * The registry's timed effects before the test, put back afterwards.
     */
    private Object savedEffects;

    /**
     * Whether the dungeon flag was set before the test.
     */
    private boolean savedCharacterDungeon;

    /**
     * Installs the timed-effect definitions the tests need, builds a plain fed character.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void setUp() throws Exception {
        Field registry = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        registry.setAccessible(true);
        savedEffects = registry.get(null);
        List<PlayerTimedEffect> effects = new ArrayList<>();
        effects.add(effect(TimedEffect.TMD_FOOD, null,
                grade(1, 100, "Starving"), grade(2, 400, "Faint"), grade(3, 800, "Weak"),
                grade(4, 1500, "Hungry"), grade(5, 9000, "Fed"), grade(6, 10000, "Full")));
        effects.add(effect(TimedEffect.TMD_STUN, null,
                grade(1, 50, "Stun"), grade(2, 150, "Heavy Stun"), grade(3, 10000, "Knocked Out")));
        effects.add(effect(TimedEffect.TMD_OPP_FIRE, ElementEnum.ELEM_FIRE));
        registry.set(null, effects);

        savedCharacterDungeon = GameWorld.hasCharacterDungeon();

        fixture = CalcBonusesFixture.plainCharacter();
        fixture.timed(TimedEffect.TMD_FOOD, 5000);
    }

    /**
     * Puts the registry and the dungeon flag back.
     *
     * @throws Exception if the registry cannot be reached
     */
    @AfterEach
    void tearDown() throws Exception {
        Field registry = PlayerRegistry.class.getDeclaredField("playerTimedEffects");
        registry.setAccessible(true);
        registry.set(null, savedEffects);
        GameWorld.setCharacterDungeon(savedCharacterDungeon);
    }

    /**
     * One grade of a timed effect.
     *
     * @param number the grade's ordinal
     * @param max    its upper bound
     * @param status its name
     * @return the grade
     */
    private static TimedGrade grade(int number, int max, String status) {
        return new TimedGrade(number, ColourEnum.COLOUR_WHITE, max, status, "", "");
    }

    /**
     * A timed-effect definition with the given grades and temporary resistance.
     *
     * @param name       the effect
     * @param tempResist the element it temporarily resists, or {@code null}
     * @param grades     its grades, lowest first
     * @return the definition
     */
    private static PlayerTimedEffect effect(TimedEffect name, ElementEnum tempResist, TimedGrade... grades) {
        return new PlayerTimedEffect(name, "", "", "", "", null, List.of(), List.of(grades),
                null, null, false, 0, ObjectFlag.OF_NONE, false, tempResist, null, null);
    }

    /**
     * A class whose skills, extra skills and player flags the test chooses; everything else is the
     * plain class's.
     *
     * @param skills the skills to set, all others zero
     * @param extra  the per-level skills to set, all others zero
     * @param flags  the player flags the class carries
     * @return the class
     */
    private static PlayerClass classWith(Map<PlayerSkill, Integer> skills, Map<PlayerSkill, Integer> extra,
                                         PlayerFlag... flags) {
        Map<Stats, Integer> stats = new HashMap<>();
        for (Stats stat : Stats.values()) {
            if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
            stats.put(stat, 0);
        }
        Map<PlayerSkill, Integer> base = new HashMap<>();
        Map<PlayerSkill, Integer> perLevel = new HashMap<>();
        for (PlayerSkill skill : PlayerSkill.values()) {
            if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
            base.put(skill, skills.getOrDefault(skill, 0));
            perLevel.put(skill, extra.getOrDefault(skill, 0));
        }
        Flag<PlayerFlag> pflags = new Flag<>(PlayerFlag.class);
        for (PlayerFlag flag : flags) pflags.on(flag);
        return new PlayerClass("Test Class", List.of(), stats, base, perLevel, 0, 0,
                new Flag<>(ObjectFlag.class), pflags, 5, 30, 5, List.of(), ClassMagic.NONE);
    }

    /**
     * A class that gives {@link #PROBE} in the five skills the hunger and status adjustments scale.
     *
     * @param flags the player flags the class carries
     * @return the class
     */
    private static PlayerClass probeClass(PlayerFlag... flags) {
        Map<PlayerSkill, Integer> skills = new HashMap<>();
        for (PlayerSkill skill : List.of(PlayerSkill.SKILL_DEVICE, PlayerSkill.SKILL_DISARM_PHYS,
                PlayerSkill.SKILL_DISARM_MAGIC, PlayerSkill.SKILL_SAVE, PlayerSkill.SKILL_SEARCH)) {
            skills.put(skill, PROBE);
        }
        return classWith(skills, Map.of(), flags);
    }

    /**
     * Reads one of {@link PlayerState}'s combat totals, which have no public getter.
     *
     * @param state the state to read
     * @param name  the field: {@code toH} or {@code toD}
     * @return the total
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static int total(PlayerState state, String name) throws ReflectiveOperationException {
        Field field = PlayerState.class.getDeclaredField(name);
        field.setAccessible(true);
        return (int) field.get(state);
    }

    /**
     * An element-info record at the given level.
     *
     * @param level the resistance level
     * @return the record
     */
    private static ElementInfo level(int level) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(level);
        return info;
    }

    /**
     * The stat-to-index conversion: raw values on the 3-to-18-then-tens scale become the rung that
     * every table in the file is read with.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the stat index")
    class StatIndex {

        /**
         * Sets strength and returns the index {@code calcBonuses} makes of it.
         *
         * @param value the strength
         * @return the strength index
         * @throws Exception if the fixture cannot reach a field
         */
        private int indexOf(int value) throws Exception {
            return fixture.stat(Stats.STAT_STR, value).calculate().getStatInd(Stats.STAT_STR);
        }

        /**
         * C's {@code use <= 3} branch gives index 0, not -0 or 3 - 3: strength 3 is the floor and
         * the rung below index 1.
         */
        @Test
        @DisplayName("3 is index 0")
        void three() throws Exception {
            assertEquals(0, indexOf(3));
        }

        /**
         * The first value of the {@code use <= 18} branch, {@code use - 3}: 4 is index 1.
         */
        @Test
        @DisplayName("4 is index 1")
        void four() throws Exception {
            assertEquals(1, indexOf(4));
        }

        /**
         * Ordinary 18 is {@code 18 - 3 = 15}, the last of the plain values.
         */
        @Test
        @DisplayName("18 is index 15")
        void eighteen() throws Exception {
            assertEquals(15, indexOf(18));
        }

        /**
         * From 19 the tens bands begin: {@code 15 + (use - 18) / 10}. Strength 19 to 27 are all
         * still index 15, the band 18/00 to 18/09, so the bands are ten wide starting at 18.
         */
        @Test
        @DisplayName("19 and 27 are still the first band")
        void firstBand() throws Exception {
            assertAll(
                    () -> assertEquals(15, indexOf(19)),
                    () -> assertEquals(15, indexOf(27)));
        }

        /**
         * 28 is 18/10, the second band: {@code 15 + 10 / 10 = 16}.
         */
        @Test
        @DisplayName("28 begins the second band")
        void secondBand() throws Exception {
            assertEquals(16, indexOf(28));
        }

        /**
         * 237 is 18/219, the last value of the last full band: {@code 15 + 219 / 10 = 36}.
         */
        @Test
        @DisplayName("237 is the last of the banded range")
        void lastBanded() throws Exception {
            assertEquals(36, indexOf(237));
        }

        /**
         * 238 is past {@code 18 + 219} and takes the final branch, 37 (18/220 and above), as does
         * anything larger.
         */
        @Test
        @DisplayName("238 and above are index 37")
        void top() throws Exception {
            assertAll(
                    () -> assertEquals(37, indexOf(238)),
                    () -> assertEquals(37, indexOf(250)));
        }

        /**
         * The displayed use value and the top value are the stat after bonuses: a strength of 10
         * with nothing else is 10 for both.
         */
        @Test
        @DisplayName("the use and top figures equal the stat when nothing modifies it")
        void useAndTop() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 14).calculate();

            assertAll(
                    () -> assertEquals(14, state.getStatUse(Stats.STAT_STR)),
                    () -> assertEquals(14, state.getStatTop(Stats.STAT_STR)));
        }

        /**
         * A modifier moves the stat through C's {@code modify_stat_value}: below 18 a point is one
         * step, from 18 up a point is ten. Strength 17 with a bonus of 2 goes 17 to 18 to 28, which
         * is index 16.
         */
        @Test
        @DisplayName("a bonus of 2 on 17 carries it over 18 into the next band")
        void bonusCrossesEighteen() throws Exception {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.setModifiers(Map.of(ObjectModifier.OM_STR, 2));

            PlayerState state = fixture.stat(Stats.STAT_STR, 17)
                    .knows(ObjectModifier.OM_STR)
                    .wear("right hand", ring)
                    .calculate();

            assertAll(
                    () -> assertEquals(28, state.getStatUse(Stats.STAT_STR)),
                    () -> assertEquals(16, state.getStatInd(Stats.STAT_STR)));
        }
    }

    /**
     * The hypothetical pass: with {@code update} clear the new strength and dexterity indices are
     * added to the ones the incoming state carried, clamped to 3 and 37.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the hypothetical pass")
    class Hypothetical {

        /**
         * Strength and dexterity 10 are index 7 each. A real pass leaves 7; a hypothetical pass on
         * the same state then adds the incoming 7 to the new 7, giving 14. Intelligence is not
         * nudged: only STR and DEX are, so it stays 7.
         */
        @Test
        @DisplayName("strength and dexterity are nudged by the incoming indices, the others are not")
        void nudged() throws Exception {
            fixture.calculate();

            PlayerState state = fixture.calculateHypothetical();

            assertAll(
                    () -> assertEquals(14, state.getStatInd(Stats.STAT_STR)),
                    () -> assertEquals(14, state.getStatInd(Stats.STAT_DEX)),
                    () -> assertEquals(7, state.getStatInd(Stats.STAT_INT)));
        }

        /**
         * Strength 3 is index 0, so the hypothetical sum is {@code 0 + 0} — and C then clamps it up
         * to 3: {@code ind = MAX(ind, 3)}.
         */
        @Test
        @DisplayName("the lower clamp is 3")
        void lowerClamp() throws Exception {
            fixture.stat(Stats.STAT_STR, 3).calculate();

            PlayerState state = fixture.calculateHypothetical();

            assertEquals(3, state.getStatInd(Stats.STAT_STR));
        }

        /**
         * Strength 250 is index 37; added to the incoming 37 it would be 74, and is clamped down to
         * 37: {@code ind = MIN(ind, 37)}.
         */
        @Test
        @DisplayName("the upper clamp is 37")
        void upperClamp() throws Exception {
            fixture.stat(Stats.STAT_STR, 250).calculate();

            PlayerState state = fixture.calculateHypothetical();

            assertEquals(37, state.getStatInd(Stats.STAT_STR));
        }

        /**
         * A real pass is never nudged: calculating twice gives 7 both times.
         */
        @Test
        @DisplayName("a real pass ignores the incoming indices")
        void realPassNotNudged() throws Exception {
            fixture.calculate();

            assertEquals(7, fixture.calculate().getStatInd(Stats.STAT_STR));
        }
    }

    /**
     * What the stat tables add once the indices are known.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the stat-derived bonuses")
    class StatTables {

        /**
         * Strength and dexterity 118 (18/100) are index 25. From the C tables at index 25:
         * {@code adj_str_td} 6, {@code adj_str_th} 5, {@code adj_dex_th} 6 and {@code adj_dex_ta} 6,
         * so to-dam 6, to-hit {@code 5 + 6 = 11} and armour 6.
         */
        @Test
        @DisplayName("strong and nimble add to-hit, to-dam and armour")
        void strongAndNimble() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 118).stat(Stats.STAT_DEX, 118).calculate();

            assertAll(
                    () -> assertEquals(6, total(state, "toD")),
                    () -> assertEquals(11, total(state, "toH")),
                    () -> assertEquals(6, state.getToAc()));
        }

        /**
         * Strength and dexterity 3 are index 0: {@code adj_str_td} -2, {@code adj_str_th} -3,
         * {@code adj_dex_th} -3 and {@code adj_dex_ta} -4, so to-dam -2, to-hit -6 and armour -4.
         */
        @Test
        @DisplayName("weak and clumsy subtract")
        void weakAndClumsy() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 3).stat(Stats.STAT_DEX, 3).calculate();

            assertAll(
                    () -> assertEquals(-2, total(state, "toD")),
                    () -> assertEquals(-6, total(state, "toH")),
                    () -> assertEquals(-4, state.getToAc()));
        }

        /**
         * Intelligence, wisdom, dexterity and strength 118 (index 25) feed the five skill tables:
         * {@code adj_int_dev} 6, {@code adj_int_dis} 10, {@code adj_dex_dis} 10,
         * {@code adj_wis_sav} 7 and {@code adj_str_dig} 50.
         */
        @Test
        @DisplayName("high stats raise the stat-driven skills")
        void skillTables() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_INT, 118).stat(Stats.STAT_WIS, 118)
                    .stat(Stats.STAT_DEX, 118).stat(Stats.STAT_STR, 118).calculate();

            assertAll(
                    () -> assertEquals(6, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(10, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC)),
                    () -> assertEquals(10, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)),
                    () -> assertEquals(7, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)),
                    () -> assertEquals(50, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING)));
        }

        /**
         * A class's per-level skill is {@code x_skill * level / 10} with integer division: 25 at
         * level 12 is {@code 300 / 10 = 30}, and at level 1 is {@code 25 / 10 = 2}.
         */
        @Test
        @DisplayName("per-level class skills divide by ten and truncate")
        void perLevelSkills() throws Exception {
            fixture.playerClass(classWith(Map.of(), Map.of(PlayerSkill.SKILL_DEVICE, 25)));

            int atTwelve = fixture.level(12).calculate().getPlayerSkill(PlayerSkill.SKILL_DEVICE);
            int atOne = fixture.level(1).calculate().getPlayerSkill(PlayerSkill.SKILL_DEVICE);

            assertAll(
                    () -> assertEquals(AT_TEN + 30, atTwelve),
                    () -> assertEquals(AT_TEN + 2, atOne));
        }

        /**
         * Stealth is held to the range 0 to 30 after everything is added: a class value of 40 gives
         * 30 and one of -5 gives 0.
         */
        @Test
        @DisplayName("stealth is clamped to 0..30")
        void stealthClamped() throws Exception {
            int high = fixture.playerClass(classWith(Map.of(PlayerSkill.SKILL_STEALTH, 40), Map.of()))
                    .calculate().getPlayerSkill(PlayerSkill.SKILL_STEALTH);
            int low = fixture.playerClass(classWith(Map.of(PlayerSkill.SKILL_STEALTH, -5), Map.of()))
                    .calculate().getPlayerSkill(PlayerSkill.SKILL_STEALTH);

            assertAll(
                    () -> assertEquals(30, high),
                    () -> assertEquals(0, low));
        }

        /**
         * Digging is floored at 1: strength 3 gives {@code adj_str_dig[0] = 0} and the class gives
         * nothing, so the floor, not the sum, is what is reported.
         */
        @Test
        @DisplayName("digging is floored at one")
        void diggingFloor() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 3).calculate();

            assertEquals(1, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING));
        }
    }

    /**
     * Carried weight against the strength limit.
     *
     * <p>Strength 10 is index 7, {@code adj_str_wgt[7] = 12}, so the limit is 1200 tenth-pounds.
     * The penalty starts above half of it, 600, and is {@code (weight - 600) / 120} speed points.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("weight")
    class Weight {

        /**
         * Sets the carried weight and returns the resulting speed.
         *
         * @param weight the total weight in tenth-pounds
         * @return the speed
         * @throws Exception if the fixture cannot reach a field
         */
        private int speedAt(int weight) throws Exception {
            fixture.player().getPlayerUpkeep().setTotalWeight(weight);
            return fixture.calculate().getSpeed();
        }

        /**
         * At exactly half the limit there is no penalty: C's test is {@code j > i / 2}.
         */
        @Test
        @DisplayName("half the limit costs nothing")
        void halfTheLimit() throws Exception {
            assertEquals(110, speedAt(600));
        }

        /**
         * One over half is a penalty of {@code 1 / 120 = 0}.
         */
        @Test
        @DisplayName("one over half still costs nothing, by truncation")
        void justOverHalf() throws Exception {
            assertEquals(110, speedAt(601));
        }

        /**
         * 720 is {@code (720 - 600) / 120 = 1} point.
         */
        @Test
        @DisplayName("a full tenth over half costs one point")
        void onePoint() throws Exception {
            assertEquals(109, speedAt(720));
        }

        /**
         * 1200, the whole limit, is {@code 600 / 120 = 5} points.
         */
        @Test
        @DisplayName("the whole limit costs five points")
        void atTheLimit() throws Exception {
            assertEquals(105, speedAt(1200));
        }

        /**
         * A load far beyond the limit would take speed below zero and is floored at 0.
         */
        @Test
        @DisplayName("speed is floored at zero")
        void floor() throws Exception {
            assertEquals(0, speedAt(100000));
        }

        /**
         * A speed bonus past 199 is clamped: a ring of +100 gives 210, held to 199.
         */
        @Test
        @DisplayName("speed is capped at 199")
        void cap() throws Exception {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.setModifiers(Map.of(ObjectModifier.OM_SPEED, 100));

            PlayerState state = fixture.knows(ObjectModifier.OM_SPEED).wear("right hand", ring).calculate();

            assertEquals(199, state.getSpeed());
        }
    }

    /**
     * Vulnerabilities, which are remembered during the walk and applied once at the end.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("vulnerabilities")
    class Vulnerabilities {

        /**
         * A race with a vulnerability to fire.
         *
         * @return the race
         */
        private PlayerRace fireVulnerableRace() {
            Map<Stats, Integer> stats = new HashMap<>();
            for (Stats stat : Stats.values()) {
                if (stat == Stats.STAT_NONE || stat == Stats.STAT_MAX) continue;
                stats.put(stat, 0);
            }
            Map<PlayerSkill, Integer> skills = new HashMap<>();
            for (PlayerSkill skill : PlayerSkill.values()) {
                if (skill == PlayerSkill.SKILL_NONE || skill == PlayerSkill.SKILL_MAX) continue;
                skills.put(skill, 0);
            }
            Map<ElementEnum, ElementInfo> resists = new HashMap<>();
            resists.put(ElementEnum.ELEM_FIRE, level(-1));
            return new PlayerRace("Test Race", 0, 10, 100, 14, 6, 72, 6, 180, 25, 0, fixture.body(),
                    stats, skills, new Flag<>(ObjectFlag.class), new Flag<>(PlayerFlag.class), null, resists);
        }

        /**
         * A ring giving fire the stated level.
         *
         * @param fire the fire level
         * @return the ring
         */
        private ItemObject ringOfFire(int fire) {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.setElInfo(Map.of(ElementEnum.ELEM_FIRE, level(fire)));
            return ring;
        }

        /**
         * An item that is vulnerable to fire makes the character vulnerable: the level is lowered
         * from 0 to -1.
         */
        @Test
        @DisplayName("an item's vulnerability lowers the level by one")
        void itemVulnerability() throws Exception {
            PlayerState state = fixture.wear("right hand", ringOfFire(-1)).calculate();

            assertEquals(-1, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * The race's vulnerability is remembered rather than stored, and applied at the end: -1.
         */
        @Test
        @DisplayName("a racial vulnerability lowers the level by one")
        void racialVulnerability() throws Exception {
            PlayerState state = fixture.race(fireVulnerableRace()).calculate();

            assertEquals(-1, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * A resistance of 1 from another source is compared against the unpenalized level and then
         * lowered: 1 less 1 is 0, so the vulnerability cancels the resistance.
         */
        @Test
        @DisplayName("a vulnerability cancels a resistance")
        void cancelsResistance() throws Exception {
            PlayerState state = fixture.race(fireVulnerableRace())
                    .wear("right hand", ringOfFire(1)).calculate();

            assertEquals(0, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * Immunity (level 3) is not lowered: C's condition is {@code res_level < 3}.
         */
        @Test
        @DisplayName("immunity is not lowered")
        void immunityKept() throws Exception {
            PlayerState state = fixture.race(fireVulnerableRace())
                    .wear("right hand", ringOfFire(3)).calculate();

            assertEquals(3, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * Two sources of the same vulnerability cost one point, not two: it is a flag, not a count.
         */
        @Test
        @DisplayName("two sources of one vulnerability lower the level once")
        void countedOnce() throws Exception {
            PlayerState state = fixture.race(fireVulnerableRace())
                    .wear("right hand", ringOfFire(-1)).calculate();

            assertEquals(-1, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * Resistance from two items is the better of the two, not the sum: levels 1 and 2 give 2.
         */
        @Test
        @DisplayName("resistance from two items is the better, not the sum")
        void betterOfTwo() throws Exception {
            PlayerState state = fixture.wear("right hand", ringOfFire(1))
                    .wear("left hand", ringOfFire(2)).calculate();

            assertEquals(2, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * A player with {@code PF_UNLIGHT} gets dark resistance in the dungeon.
         */
        @Test
        @DisplayName("PF_UNLIGHT resists dark once the character is in the dungeon")
        void unlightInDungeon() throws Exception {
            GameWorld.setCharacterDungeon(true);

            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_UNLIGHT))
                    .calculate();

            assertEquals(1, state.getResLevel(ElementEnum.ELEM_DARK));
        }

        /**
         * But not before there is a dungeon: C's test includes {@code character_dungeon}.
         */
        @Test
        @DisplayName("PF_UNLIGHT gives nothing before the dungeon exists")
        void unlightBeforeDungeon() throws Exception {
            GameWorld.setCharacterDungeon(false);

            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_UNLIGHT))
                    .calculate();

            assertEquals(0, state.getResLevel(ElementEnum.ELEM_DARK));
        }

        /**
         * {@code PF_EVIL} in the dungeon resists nether and is vulnerable to holy orb, set after the
         * vulnerability pass so the -1 is the stored level, not a lowered one.
         */
        @Test
        @DisplayName("PF_EVIL resists nether and fears holy orb in the dungeon")
        void evilInDungeon() throws Exception {
            GameWorld.setCharacterDungeon(true);

            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_EVIL))
                    .calculate();

            assertAll(
                    () -> assertEquals(1, state.getResLevel(ElementEnum.ELEM_NETHER)),
                    () -> assertEquals(-1, state.getResLevel(ElementEnum.ELEM_HOLY_ORB)));
        }

        /**
         * And nothing before the dungeon exists.
         */
        @Test
        @DisplayName("PF_EVIL gives nothing before the dungeon exists")
        void evilBeforeDungeon() throws Exception {
            GameWorld.setCharacterDungeon(false);

            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_EVIL))
                    .calculate();

            assertAll(
                    () -> assertEquals(0, state.getResLevel(ElementEnum.ELEM_NETHER)),
                    () -> assertEquals(0, state.getResLevel(ElementEnum.ELEM_HOLY_ORB)));
        }
    }

    /**
     * Hunger and surfeit: the penalties outside the Fed range.
     *
     * <p>The grade boundaries are 1500 for Hungry and 9000 for Fed, with a maximum of 10000. A
     * counter {@code v} below 1500 gives {@code lack = (1500 - v) * 20 / 1500}; one above 9000 gives
     * {@code excess = (v - 9000) * 10 / 1000} points off speed.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("hunger")
    class Hunger {

        /**
         * Calculates a probe-class character at the given food counter.
         *
         * @param food the food counter
         * @return the state
         * @throws Exception if the fixture cannot reach a field
         */
        private PlayerState at(int food) throws Exception {
            return fixture.playerClass(probeClass()).timed(TimedEffect.TMD_FOOD, food).calculate();
        }

        /**
         * Inside the Fed range nothing is penalized: speed 110, to-hit 0, and the probe skills read
         * their base plus the table figure.
         */
        @Test
        @DisplayName("Fed has no penalty")
        void fed() throws Exception {
            PlayerState state = at(5000);

            assertAll(
                    () -> assertEquals(110, state.getSpeed()),
                    () -> assertEquals(0, total(state, "toH")),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * Exactly 1500 is the top of Hungry, not Fed — {@code lack} is 0 and nothing applies.
         */
        @Test
        @DisplayName("the top of Hungry has lack zero and no penalty")
        void topOfHungry() throws Exception {
            PlayerState state = at(1500);

            assertAll(
                    () -> assertEquals(0, total(state, "toH")),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * 1000 gives {@code lack = 500 * 20 / 1500 = 6}: to-hit and to-dam fall by 6 and no skill is
         * touched (they start above 10).
         */
        @Test
        @DisplayName("lack 6 costs six to hit and to damage only")
        void lackSix() throws Exception {
            PlayerState state = at(1000);

            assertAll(
                    () -> assertEquals(-6, total(state, "toH")),
                    () -> assertEquals(-6, total(state, "toD")),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * 700 gives {@code lack = 800 * 20 / 1500 = 10}, the last value before device skill is
         * touched: C's bucket is {@code lack > 10}.
         */
        @Test
        @DisplayName("lack 10 does not yet touch device skill")
        void lackTen() throws Exception {
            PlayerState state = at(700);

            assertAll(
                    () -> assertEquals(-10, total(state, "toH")),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * 650 gives {@code lack = 850 * 20 / 1500 = 11}: device skill is reduced by a tenth, rounding
         * the reduction up, {@code (100 + 9) / 10 = 10}, so 90 before the table adds 1.
         */
        @Test
        @DisplayName("lack 11 reduces device skill by a tenth")
        void lackEleven() throws Exception {
            PlayerState state = at(650);

            assertAll(
                    () -> assertEquals(-11, total(state, "toH")),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)));
        }

        /**
         * 375 gives {@code lack = 1125 * 20 / 1500 = 15}, the top of the first bucket: still a tenth
         * off device skill and the disarm skills untouched.
         */
        @Test
        @DisplayName("lack 15 is the top of the first bucket")
        void lackFifteen() throws Exception {
            PlayerState state = at(375);

            assertAll(
                    () -> assertEquals(-15, total(state, "toH")),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)));
        }

        /**
         * 300 gives {@code lack = 1200 * 20 / 1500 = 16}, the start of the second bucket: device
         * skill loses a fifth, {@code (100 + 4) / 5 = 20}, so 80; the two disarm skills are
         * multiplied by 9 and divided by 10, so 90; save and search are untouched.
         */
        @Test
        @DisplayName("lack 16 starts the second bucket")
        void lackSixteen() throws Exception {
            PlayerState state = at(300);

            assertAll(
                    () -> assertEquals(-16, total(state, "toH")),
                    () -> assertEquals(80 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC)),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)),
                    () -> assertEquals(PROBE, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH)));
        }

        /**
         * 76 gives {@code lack = 1424 * 20 / 1500 = 18}, the top of the second bucket: the same
         * penalties as 16.
         */
        @Test
        @DisplayName("lack 18 is the top of the second bucket")
        void lackEighteen() throws Exception {
            PlayerState state = at(76);

            assertAll(
                    () -> assertEquals(-18, total(state, "toH")),
                    () -> assertEquals(80 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)));
        }

        /**
         * 75 gives {@code lack = 1425 * 20 / 1500 = 19}, the start of the third bucket: device skill
         * loses three tenths, {@code (300 + 9) / 10 = 30}, so 70; disarm skills are multiplied by
         * 8 and divided by 10, so 80; save and search by 9 over 10, so 90.
         */
        @Test
        @DisplayName("lack 19 starts the third bucket")
        void lackNineteen() throws Exception {
            PlayerState state = at(75);

            assertAll(
                    () -> assertEquals(-19, total(state, "toH")),
                    () -> assertEquals(70 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(80 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_PHYS)),
                    () -> assertEquals(80 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DISARM_MAGIC)),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_SAVE)),
                    () -> assertEquals(90, state.getPlayerSkill(PlayerSkill.SKILL_SEARCH)));
        }

        /**
         * A counter of zero is {@code lack = 20}: to-hit and to-dam fall by 20 and the third bucket
         * applies.
         */
        @Test
        @DisplayName("starving is lack 20")
        void starving() throws Exception {
            PlayerState state = at(0);

            assertAll(
                    () -> assertEquals(-20, total(state, "toH")),
                    () -> assertEquals(-20, total(state, "toD")),
                    () -> assertEquals(70 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * 9000 is the top of Fed: no penalty. 9001 is Full, but {@code excess = 1 * 10 / 1000 = 0},
         * so it costs nothing either.
         */
        @Test
        @DisplayName("the edge of Full costs no speed")
        void edgeOfFull() throws Exception {
            assertAll(
                    () -> assertEquals(110, at(9000).getSpeed()),
                    () -> assertEquals(110, at(9001).getSpeed()));
        }

        /**
         * 9100 is {@code excess = 100 * 10 / 1000 = 1}: one point off speed.
         */
        @Test
        @DisplayName("a little over Fed costs a point of speed")
        void littleOverFed() throws Exception {
            assertEquals(109, at(9100).getSpeed());
        }

        /**
         * 10000 is {@code excess = 1000 * 10 / 1000 = 10}: ten points off speed.
         */
        @Test
        @DisplayName("the maximum costs ten points of speed")
        void maximum() throws Exception {
            assertEquals(100, at(10000).getSpeed());
        }

        /**
         * A vampire is not slowed by being full: C's test is {@code excess > 0 && !timed[ATT_VAMP]},
         * and as the character is not hungry either the whole branch falls through.
         */
        @Test
        @DisplayName("a vampire is not slowed by surfeit")
        void vampire() throws Exception {
            fixture.timed(TimedEffect.TMD_ATT_VAMP, 10);

            assertEquals(110, at(10000).getSpeed());
        }
    }

    /**
     * The timed statuses. Each reads a probe-class device skill of 100 (plus 1 from the table), so a
     * change is visible as an exact figure.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("timed statuses")
    class Statuses {

        /**
         * Sets one status on a fresh, fed, probe-class character and calculates. The character is
         * rebuilt on every call so that a test can compare several statuses without one leaking
         * into the next.
         *
         * @param effect the status
         * @param value  its counter
         * @return the state
         * @throws Exception if the fixture cannot reach a field
         */
        private PlayerState with(TimedEffect effect, int value) throws Exception {
            fixture = CalcBonusesFixture.plainCharacter();
            fixture.timed(TimedEffect.TMD_FOOD, 5000);
            return fixture.playerClass(probeClass()).timed(effect, value).calculate();
        }

        /**
         * Blessing: +5 armour, +10 to hit, and device skill up by a twentieth: {@code 100 * 1 / 20}
         * is 5.
         */
        @Test
        @DisplayName("blessed")
        void blessed() throws Exception {
            PlayerState state = with(TimedEffect.TMD_BLESSED, 10);

            assertAll(
                    () -> assertEquals(5, state.getToAc()),
                    () -> assertEquals(10, total(state, "toH")),
                    () -> assertEquals(105 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * Heroism: +12 to hit and the same twentieth of device skill.
         */
        @Test
        @DisplayName("heroism")
        void heroism() throws Exception {
            PlayerState state = with(TimedEffect.TMD_HERO, 10);

            assertAll(
                    () -> assertEquals(12, total(state, "toH")),
                    () -> assertEquals(105 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * Two bonuses compound in the order C applies them: blessed first (100 to 105), then hero
         * ({@code 105 / 20 = 5}, to 110).
         */
        @Test
        @DisplayName("blessing and heroism compound")
        void compound() throws Exception {
            fixture.playerClass(probeClass()).timed(TimedEffect.TMD_BLESSED, 10).timed(TimedEffect.TMD_HERO, 10);

            PlayerState state = fixture.calculate();

            assertEquals(110 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE));
        }

        /**
         * Berserk strength: +75 melee skill, -10 armour, and device skill down by a tenth, so 90.
         */
        @Test
        @DisplayName("berserk")
        void berserk() throws Exception {
            PlayerState state = with(TimedEffect.TMD_SHERO, 10);

            assertAll(
                    () -> assertEquals(75, state.getPlayerSkill(PlayerSkill.SKILL_TO_HIT_MELEE)),
                    () -> assertEquals(-10, state.getToAc()),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * The three armour statuses: invulnerability +100, shield +50, and stoneskin +40 at -5
         * speed.
         */
        @Test
        @DisplayName("armour statuses")
        void armourStatuses() throws Exception {
            assertAll(
                    () -> assertEquals(100, with(TimedEffect.TMD_INVULN, 5).getToAc()),
                    () -> assertEquals(50, with(TimedEffect.TMD_SHIELD, 5).getToAc()),
                    () -> assertEquals(40, with(TimedEffect.TMD_STONESKIN, 5).getToAc()),
                    () -> assertEquals(105, with(TimedEffect.TMD_STONESKIN, 5).getSpeed()));
        }

        /**
         * Speed statuses: fast +10, slow -10, terror +10.
         */
        @Test
        @DisplayName("speed statuses")
        void speedStatuses() throws Exception {
            assertAll(
                    () -> assertEquals(120, with(TimedEffect.TMD_FAST, 5).getSpeed()),
                    () -> assertEquals(100, with(TimedEffect.TMD_SLOW, 5).getSpeed()),
                    () -> assertEquals(120, with(TimedEffect.TMD_TERROR, 5).getSpeed()));
        }

        /**
         * Fast and sprint together are one +10, not two: C's test is {@code FAST || SPRINT}.
         */
        @Test
        @DisplayName("fast and sprint together give one bonus")
        void fastAndSprint() throws Exception {
            fixture.timed(TimedEffect.TMD_FAST, 5).timed(TimedEffect.TMD_SPRINT, 5);

            assertEquals(120, fixture.calculate().getSpeed());
        }

        /**
         * Fast and slow cancel.
         */
        @Test
        @DisplayName("fast and slow cancel")
        void fastAndSlow() throws Exception {
            fixture.timed(TimedEffect.TMD_FAST, 5).timed(TimedEffect.TMD_SLOW, 5);

            assertEquals(110, fixture.calculate().getSpeed());
        }

        /**
         * The device-skill penalties: confusion a quarter ({@code (100 + 3) / 4 = 25}, so 75), amnesia
         * and hallucination a fifth (80), poison a twentieth ({@code (100 + 19) / 20 = 5}, so 95).
         */
        @Test
        @DisplayName("device-skill penalties")
        void devicePenalties() throws Exception {
            assertAll(
                    () -> assertEquals(75 + AT_TEN, with(TimedEffect.TMD_CONFUSED, 5)
                            .getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(80 + AT_TEN, with(TimedEffect.TMD_AMNESIA, 5)
                            .getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(80 + AT_TEN, with(TimedEffect.TMD_IMAGE, 5)
                            .getPlayerSkill(PlayerSkill.SKILL_DEVICE)),
                    () -> assertEquals(95 + AT_TEN, with(TimedEffect.TMD_POISONED, 5)
                            .getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * Bloodlust adds half its counter to damage and a twentieth of it to blows, in hundredths:
         * a counter of 45 is +22 damage ({@code 45 / 2}) and {@code 45 / 20 = 2} extra blows. An
         * unarmed strength-and-dexterity-10 character has 105 blows, so 305.
         */
        @Test
        @DisplayName("bloodlust")
        void bloodlust() throws Exception {
            PlayerState state = with(TimedEffect.TMD_BLOODLUST, 45);

            assertAll(
                    () -> assertEquals(22, total(state, "toD")),
                    () -> assertEquals(305, state.getNumBlows()));
        }

        /**
         * The stealth status adds 10 to stealth.
         */
        @Test
        @DisplayName("the stealth status adds ten")
        void stealth() throws Exception {
            assertEquals(10, with(TimedEffect.TMD_STEALTH, 5).getPlayerSkill(PlayerSkill.SKILL_STEALTH));
        }

        /**
         * A status at counter zero is dormant and does nothing.
         */
        @Test
        @DisplayName("a zero counter does nothing")
        void dormant() throws Exception {
            PlayerState state = with(TimedEffect.TMD_BLESSED, 0);

            assertEquals(0, state.getToAc());
        }
    }

    /**
     * Stun is graded: the penalty depends on which grade the counter is in, by name.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("stun")
    class Stun {

        /**
         * Sets stun on a probe-class character and calculates.
         *
         * @param value the stun counter
         * @return the state
         * @throws Exception if the fixture cannot reach a field
         */
        private PlayerState at(int value) throws Exception {
            return fixture.playerClass(probeClass()).timed(TimedEffect.TMD_STUN, value).calculate();
        }

        /**
         * Counter 50 is the top of Stun: -5 to hit and to damage and device skill down a tenth.
         */
        @Test
        @DisplayName("the top of Stun")
        void topOfStun() throws Exception {
            PlayerState state = at(50);

            assertAll(
                    () -> assertEquals(-5, total(state, "toH")),
                    () -> assertEquals(-5, total(state, "toD")),
                    () -> assertEquals(90 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * Counter 51 is Heavy Stun: -20 to hit and to damage and device skill down a fifth.
         */
        @Test
        @DisplayName("the start of Heavy Stun")
        void startOfHeavyStun() throws Exception {
            PlayerState state = at(51);

            assertAll(
                    () -> assertEquals(-20, total(state, "toH")),
                    () -> assertEquals(-20, total(state, "toD")),
                    () -> assertEquals(80 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * Counter 151 is Knocked Out, a grade C's calculation does not name: no penalty at all.
         */
        @Test
        @DisplayName("Knocked Out carries no penalty here")
        void knockedOut() throws Exception {
            PlayerState state = at(151);

            assertAll(
                    () -> assertEquals(0, total(state, "toH")),
                    () -> assertEquals(PROBE + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }

        /**
         * A real pass cancels fast casting when stunned: the counter goes to zero.
         */
        @Test
        @DisplayName("a real pass cancels fast casting")
        void realPassCancelsFastCasting() throws Exception {
            fixture.timed(TimedEffect.TMD_FASTCAST, 7);

            at(10);

            assertEquals(0, fixture.player().getTimedEffect(TimedEffect.TMD_FASTCAST));
        }

        /**
         * A hypothetical pass must not write to the player: the counter is left at 7.
         */
        @Test
        @DisplayName("a hypothetical pass leaves fast casting alone")
        void hypotheticalLeavesFastCasting() throws Exception {
            fixture.playerClass(probeClass()).timed(TimedEffect.TMD_FASTCAST, 7)
                    .timed(TimedEffect.TMD_STUN, 10);

            fixture.calculateHypothetical();

            assertEquals(7, fixture.player().getTimedEffect(TimedEffect.TMD_FASTCAST));
        }
    }

    /**
     * Temporary resistances: a running status raises its element by one, up to a cap of 2.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("temporary resistance")
    class TemporaryResistance {

        /**
         * A ring giving fire the stated level.
         *
         * @param fire the fire level
         * @return the ring
         */
        private ItemObject ringOfFire(int fire) {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.setElInfo(Map.of(ElementEnum.ELEM_FIRE, level(fire)));
            return ring;
        }

        /**
         * With the status running and nothing else, fire goes from 0 to 1.
         */
        @Test
        @DisplayName("the status gives a bare character resistance")
        void bare() throws Exception {
            PlayerState state = fixture.timed(TimedEffect.TMD_OPP_FIRE, 10).calculate();

            assertEquals(1, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * On top of a resistance of 1 it makes 2.
         */
        @Test
        @DisplayName("it stacks on a resistance")
        void stacksOnResistance() throws Exception {
            PlayerState state = fixture.timed(TimedEffect.TMD_OPP_FIRE, 10)
                    .wear("right hand", ringOfFire(1)).calculate();

            assertEquals(2, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * Already at 2 is not below 2, so it stays at 2.
         */
        @Test
        @DisplayName("it does not take a level past 2")
        void capAtTwo() throws Exception {
            PlayerState state = fixture.timed(TimedEffect.TMD_OPP_FIRE, 10)
                    .wear("right hand", ringOfFire(2)).calculate();

            assertEquals(2, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * Immunity is left alone.
         */
        @Test
        @DisplayName("it leaves immunity alone")
        void immunityUntouched() throws Exception {
            PlayerState state = fixture.timed(TimedEffect.TMD_OPP_FIRE, 10)
                    .wear("right hand", ringOfFire(3)).calculate();

            assertEquals(3, state.getResLevel(ElementEnum.ELEM_FIRE));
        }

        /**
         * A counter of zero does nothing.
         */
        @Test
        @DisplayName("a zero counter does nothing")
        void dormant() throws Exception {
            PlayerState state = fixture.timed(TimedEffect.TMD_OPP_FIRE, 0).calculate();

            assertEquals(0, state.getResLevel(ElementEnum.ELEM_FIRE));
        }
    }

    /**
     * Fear, the object flag that makes a character a poor fighter.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("fear")
    class Fear {

        /**
         * {@code OF_AFRAID} on a worn item: -20 to hit, +8 armour and device skill down a twentieth
         * ({@code (100 + 19) / 20 = 5}, so 95).
         */
        @Test
        @DisplayName("an afraid character fights badly but is harder to hit")
        void afraid() throws Exception {
            ItemObject cowardlyHelm = CalcBonusesFixture.item(TValue.TV_HELM);
            cowardlyHelm.setFlag(ObjectFlag.OF_AFRAID);

            PlayerState state = fixture.playerClass(probeClass()).wear("head", cowardlyHelm).calculate();

            assertAll(
                    () -> assertEquals(-20, total(state, "toH")),
                    () -> assertEquals(8, state.getToAc()),
                    () -> assertEquals(95 + AT_TEN, state.getPlayerSkill(PlayerSkill.SKILL_DEVICE)));
        }
    }

    /**
     * The launcher analysis.
     *
     * <p>Strength 10 is index 7 and {@code adj_str_hold[7]} is 14, so a launcher is too heavy when
     * {@code weight / 10 > 14}: 149 is fine and 150 is not.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the launcher")
    class Launcher {

        /**
         * A bow that fires arrows, of the given weight and multiplier.
         *
         * @param weight     the weight in tenth-pounds
         * @param multiplier the launcher's multiplier
         * @return the bow
         */
        private ItemObject bow(int weight, int multiplier) {
            ItemObject bow = CalcBonusesFixture.item(TValue.TV_BOW);
            bow.setWeight(weight);
            bow.setpValue(multiplier);
            Flag<ObjectKindFlag> kindFlags = new Flag<>(ObjectKindFlag.class);
            kindFlags.on(ObjectKindFlag.KF_SHOOTS_ARROWS);
            ItemFixture.set(bow.getKind(), "kindFlags", kindFlags);
            return bow;
        }

        /**
         * Without a launcher there are no shots and no ammunition type.
         */
        @Test
        @DisplayName("no launcher means no shots")
        void none() throws Exception {
            PlayerState state = fixture.calculate();

            assertAll(
                    () -> assertEquals(0, state.getNumShots()),
                    () -> assertEquals(TValue.TV_NONE, state.getAmmoTval()),
                    () -> assertFalse(state.isHeavyShoot()));
        }

        /**
         * A light bow: 10 shots (one a turn, scaled by ten), arrows, and the bow's multiplier.
         */
        @Test
        @DisplayName("a bow gives ten shots, arrows and its multiplier")
        void ordinaryBow() throws Exception {
            PlayerState state = fixture.wear("shooting", bow(30, 2)).calculate();

            assertAll(
                    () -> assertEquals(10, state.getNumShots()),
                    () -> assertEquals(TValue.TV_ARROW, state.getAmmoTval()),
                    () -> assertEquals(2, state.getAmmoMult()),
                    () -> assertFalse(state.isHeavyShoot()));
        }

        /**
         * Extra shots and extra might add to a bow that can be drawn: 10 + 5 shots and 2 + 1 might.
         */
        @Test
        @DisplayName("extra shots and might are added")
        void extras() throws Exception {
            ItemObject bow = bow(30, 2);
            bow.setModifiers(Map.of(ObjectModifier.OM_SHOTS, 5, ObjectModifier.OM_MIGHT, 1));

            PlayerState state = fixture.knows(ObjectModifier.OM_SHOTS, ObjectModifier.OM_MIGHT)
                    .wear("shooting", bow).calculate();

            assertAll(
                    () -> assertEquals(15, state.getNumShots()),
                    () -> assertEquals(3, state.getAmmoMult()));
        }

        /**
         * A shooter's class adds a third of the level to the shots: level 30 adds 10.
         */
        @Test
        @DisplayName("PF_FAST_SHOT adds a third of the level")
        void fastShot() throws Exception {
            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_FAST_SHOT))
                    .level(30).wear("shooting", bow(30, 2)).calculate();

            assertEquals(20, state.getNumShots());
        }

        /**
         * A penalty cannot take shots below one a turn: 10 less 5 is floored to 10.
         */
        @Test
        @DisplayName("shots are floored at ten")
        void floor() throws Exception {
            ItemObject bow = bow(30, 2);
            bow.setModifiers(Map.of(ObjectModifier.OM_SHOTS, -5));

            PlayerState state = fixture.knows(ObjectModifier.OM_SHOTS).wear("shooting", bow).calculate();

            assertEquals(10, state.getNumShots());
        }

        /**
         * 149 tenth-pounds is {@code 14} pounds, not more than the hold of 14: not heavy.
         */
        @Test
        @DisplayName("just under the hold is not heavy")
        void justUnder() throws Exception {
            PlayerState state = fixture.wear("shooting", bow(149, 2)).calculate();

            assertAll(
                    () -> assertFalse(state.isHeavyShoot()),
                    () -> assertEquals(0, total(state, "toH")));
        }

        /**
         * 150 is 15 pounds, over the hold of 14: heavy. To-hit falls by {@code 2 * (14 - 15) = 2},
         * and the extras are withheld — the shots and might are the bow's own.
         */
        @Test
        @DisplayName("over the hold is heavy and withholds the extras")
        void overTheHold() throws Exception {
            ItemObject bow = bow(150, 2);
            bow.setModifiers(Map.of(ObjectModifier.OM_SHOTS, 5, ObjectModifier.OM_MIGHT, 1));

            PlayerState state = fixture.knows(ObjectModifier.OM_SHOTS, ObjectModifier.OM_MIGHT)
                    .wear("shooting", bow).calculate();

            assertAll(
                    () -> assertTrue(state.isHeavyShoot()),
                    () -> assertEquals(-2, total(state, "toH")),
                    () -> assertEquals(10, state.getNumShots()),
                    () -> assertEquals(2, state.getAmmoMult()));
        }
    }

    /**
     * The weapon analysis.
     *
     * <p>Strength 10 holds 14 pounds. For the blows cases strength and dexterity are 68 (18/50),
     * index 20: {@code adj_str_hold[20]} is 50, {@code adj_str_blow[20]} is 70 and
     * {@code adj_dex_blow[20]} is 4. With the class's multiplier of 5 an unarmed character has
     * strength rung {@code 350 / 30 = 11} and cell {@code blows_table[11][4] = 22}, which is 454
     * blows; a weapon of weight 100 has rung {@code 350 / 100 = 3} and cell
     * {@code blows_table[3][4] = 42}, which is 238.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the weapon")
    class Weapon {

        /**
         * A sword of the given weight.
         *
         * @param weight the weight in tenth-pounds
         * @return the sword
         */
        private ItemObject sword(int weight) {
            ItemObject sword = CalcBonusesFixture.item(TValue.TV_SWORD);
            sword.setWeight(weight);
            return sword;
        }

        /**
         * Unarmed: blows from the table at the class minimum weight.
         */
        @Test
        @DisplayName("unarmed blows come from the table")
        void unarmed() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 68).stat(Stats.STAT_DEX, 68).calculate();

            assertEquals(454, state.getNumBlows());
        }

        /**
         * A weapon the character can hold gives blows by its weight, and adds a tenth of its weight
         * to digging: weight 100 adds 10 on top of {@code adj_str_dig[20] = 25}.
         */
        @Test
        @DisplayName("a usable weapon gives table blows and digging")
        void usableWeapon() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 68).stat(Stats.STAT_DEX, 68)
                    .wear("weapon", sword(100)).calculate();

            assertAll(
                    () -> assertFalse(state.isHeavyWield()),
                    () -> assertEquals(238, state.getNumBlows()),
                    () -> assertEquals(25 + 10, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING)));
        }

        /**
         * A weapon over the hold: 600 is 60 pounds against a hold of 50. To-hit falls by
         * {@code 2 * (50 - 60) = 20} from the {@code 4 + 1 = 5} the tables give at index 20
         * ({@code adj_dex_th} and {@code adj_str_th}), the blows stay at the default 100 and
         * digging gets nothing from the weight.
         */
        @Test
        @DisplayName("a weapon over the hold is heavy, one blow, and no digging help")
        void heavyWeapon() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 68).stat(Stats.STAT_DEX, 68)
                    .wear("weapon", sword(600)).calculate();

            assertAll(
                    () -> assertTrue(state.isHeavyWield()),
                    () -> assertEquals(-20 + 5, total(state, "toH"), "-20 for the weight, +5 from the tables"),
                    () -> assertEquals(100, state.getNumBlows()),
                    () -> assertEquals(25, state.getPlayerSkill(PlayerSkill.SKILL_DIGGING)));
        }

        /**
         * A weapon exactly at the hold is not heavy: 500 is 50 pounds, and the test is
         * {@code hold < weight / 10}.
         */
        @Test
        @DisplayName("a weapon exactly at the hold is not heavy")
        void atTheHold() throws Exception {
            PlayerState state = fixture.stat(Stats.STAT_STR, 68).wear("weapon", sword(500)).calculate();

            assertFalse(state.isHeavyWield());
        }

        /**
         * A class with {@code PF_BLESS_WEAPON} wielding a hafted weapon gets +2 damage and is
         * attuned.
         */
        @Test
        @DisplayName("a blessed-weapon class wielding a hafted weapon is attuned")
        void hafted() throws Exception {
            ItemObject mace = CalcBonusesFixture.item(TValue.TV_HAFTED);

            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_BLESS_WEAPON))
                    .wear("weapon", mace).calculate();

            assertAll(
                    () -> assertTrue(state.isBlessWield()),
                    () -> assertEquals(2, total(state, "toD")));
        }

        /**
         * A sword is not hafted, and without {@code OF_BLESSED} the same class is not attuned.
         */
        @Test
        @DisplayName("a plain sword is not attuned")
        void plainSword() throws Exception {
            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_BLESS_WEAPON))
                    .wear("weapon", sword(30)).calculate();

            assertAll(
                    () -> assertFalse(state.isBlessWield()),
                    () -> assertEquals(0, total(state, "toD")));
        }

        /**
         * A blessed sword is attuned: the {@code OF_BLESSED} flag stands in for being hafted.
         */
        @Test
        @DisplayName("a blessed sword is attuned")
        void blessedSword() throws Exception {
            ItemObject sword = sword(30);
            sword.setFlag(ObjectFlag.OF_BLESSED);

            PlayerState state = fixture.playerClass(classWith(Map.of(), Map.of(), PlayerFlag.PF_BLESS_WEAPON))
                    .wear("weapon", sword).calculate();

            assertTrue(state.isBlessWield());
        }

        /**
         * Without the class flag nothing is attuned, even to a mace.
         */
        @Test
        @DisplayName("without the class flag a mace is nothing special")
        void noClassFlag() throws Exception {
            ItemObject mace = CalcBonusesFixture.item(TValue.TV_HAFTED);

            PlayerState state = fixture.wear("weapon", mace).calculate();

            assertFalse(state.isBlessWield());
        }
    }
}
