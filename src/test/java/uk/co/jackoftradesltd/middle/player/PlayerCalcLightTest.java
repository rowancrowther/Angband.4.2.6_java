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
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.CarryCapData;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.LevelMaxData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.objects.ItemObject;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.enums.PlayerFlag;
import uk.co.jackoftradesltd.middle.player.enums.PlayerUpdateEnum;
import uk.co.jackoftradesltd.testsupport.CalcBonusesFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link PlayerCalcs#calcLight}, the port of C's {@code calc_light} ({@code player-calcs.c}):
 * the radius of light the player sheds, summed over everything worn.
 *
 * <p>The expected values are worked from the C function, not read off the Java. The C comment above
 * the loop says "use the brightest" but the loop adds, and these tests follow the loop.
 *
 * <p>The town-by-day branch needs a world clock. {@code is_daytime()} is
 * {@code turn % (10 * day_length) < (10 * day_length) / 2}, and the constants table the port reads
 * the day length from is empty in a test, so the town tests install a table with the shipped
 * {@code world:day-length} of 10000 and put it back afterwards. Day then runs from turn 0 to
 * 49999 and night from 50000 to 99999.
 *
 * <p>Class PlayerCalcLightTest coded on 261014, commented in full on 261014.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcLightTest {

    /**
     * The shipped {@code world:day-length}, from {@code constants.txt}.
     */
    private static final int DAY_LENGTH = 10000;

    /**
     * The first turn of the night: half of {@code 10 * DAY_LENGTH}.
     */
    private static final int FIRST_NIGHT_TURN = 50000;

    /**
     * The character under test.
     */
    private CalcBonusesFixture fixture;

    /**
     * The state the light radius is written to.
     */
    private PlayerState state;

    /**
     * The constants table before the test, put back afterwards.
     */
    private Object savedConstants;

    /**
     * The game turn before the test, put back afterwards.
     */
    private int savedTurn;

    /**
     * The constants holder, made accessible.
     *
     * @return the field
     * @throws Exception if it cannot be reached
     */
    private static Field constantsField() throws Exception {
        Field field = GameConstants.class.getDeclaredField("data");
        field.setAccessible(true);
        return field;
    }

    /**
     * Builds a plain character at depth 1 with a wiped state, and installs a constants table that
     * has a day length.
     *
     * @throws Exception if a field cannot be reached
     */
    @BeforeEach
    void setUp() throws Exception {
        fixture = CalcBonusesFixture.plainCharacter();
        state = new PlayerState();
        state.wipe();

        savedConstants = constantsField().get(null);
        savedTurn = GameState.getTurn();
        constantsField().set(null, new GameConstantsData(
                new LevelMaxData(1024), null, null, null,
                new WorldData(128, DAY_LENGTH, 66, 198, 22, 66, 100, 10, 1, 100),
                new CarryCapData(23, 10, 40, 5, 16),
                null, null, null, null, null, null, null, null, null, null, null));
    }

    /**
     * Puts the constants and the turn back.
     *
     * @throws Exception if the constants holder cannot be reached
     */
    @AfterEach
    void tearDown() throws Exception {
        constantsField().set(null, savedConstants);
        GameState.setTurn(savedTurn);
    }

    /**
     * Runs the real calculation into the test's state.
     */
    private void calculate() {
        PlayerCalcs.calcLight(fixture.player(), state, true);
    }

    /**
     * A fuelled light with the given innate radius flag.
     *
     * @param timeout the fuel remaining
     * @param flags   the flags it carries
     * @return the light
     */
    private static ItemObject light(int timeout, ObjectFlag... flags) {
        ItemObject lantern = CalcBonusesFixture.item(TValue.TV_LIGHT, timeout);
        for (ObjectFlag flag : flags) lantern.setFlag(flag);
        return lantern;
    }

    /**
     * A ring with the given light modifier.
     *
     * @param modifier the {@code OBJ_MOD_LIGHT} value
     * @return the ring
     */
    private static ItemObject ringOfLight(int modifier) {
        ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
        ring.setModifiers(Map.of(ObjectModifier.OM_LIGHT, modifier));
        return ring;
    }

    /**
     * What each item contributes and how contributions combine.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the equipment sum")
    class EquipmentSum {

        /**
         * Nothing worn gives no light, and the figure is rebuilt from zero: C's first act is
         * {@code state->cur_light = 0}, so a stale radius does not survive.
         */
        @Test
        @DisplayName("nothing worn gives nothing, whatever the state held before")
        void nothingWorn() {
            state.setCurLight(7);

            calculate();

            assertEquals(0, state.getCurLight());
        }

        /**
         * {@code OF_LIGHT_2} is a radius of 2.
         */
        @Test
        @DisplayName("OF_LIGHT_2 gives 2")
        void lightTwo() throws Exception {
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_2));

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * {@code OF_LIGHT_3} is a radius of 3.
         */
        @Test
        @DisplayName("OF_LIGHT_3 gives 3")
        void lightThree() throws Exception {
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_3));

            calculate();

            assertEquals(3, state.getCurLight());
        }

        /**
         * The two innate flags are an {@code if} / {@code else if}: an item carrying both gets the
         * first, 2, and not 3 or 5.
         */
        @Test
        @DisplayName("an item with both flags gets only the first")
        void bothFlags() throws Exception {
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_2, ObjectFlag.OF_LIGHT_3));

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * The light modifier is added to the innate radius: 2 + 1.
         */
        @Test
        @DisplayName("the modifier is added to the innate radius")
        void modifierAdds() throws Exception {
            ItemObject lantern = light(500, ObjectFlag.OF_LIGHT_2);
            lantern.setModifiers(Map.of(ObjectModifier.OM_LIGHT, 1));
            fixture.wear("light", lantern);

            calculate();

            assertEquals(3, state.getCurLight());
        }

        /**
         * A ring with a light modifier and no innate flag contributes just the modifier.
         */
        @Test
        @DisplayName("a ring's modifier is its whole contribution")
        void ringOnly() throws Exception {
            fixture.wear("right hand", ringOfLight(1));

            calculate();

            assertEquals(1, state.getCurLight());
        }

        /**
         * Contributions are summed, not picked from: a lantern of 2 and a ring of 1 shed 3.
         */
        @Test
        @DisplayName("contributions from different slots add")
        void slotsAdd() throws Exception {
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_2));
            fixture.wear("right hand", ringOfLight(1));

            calculate();

            assertEquals(3, state.getCurLight());
        }

        /**
         * A negative modifier subtracts: a lantern of 2 and a ring of -1 shed 1. C applies no
         * floor to the item or to the total.
         */
        @Test
        @DisplayName("a negative modifier subtracts")
        void negativeModifier() throws Exception {
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_2));
            fixture.wear("right hand", ringOfLight(-1));

            calculate();

            assertEquals(1, state.getCurLight());
        }

        /**
         * With no floor the total itself can go negative: a lone ring of -1 gives -1.
         */
        @Test
        @DisplayName("the total is not floored at zero")
        void totalCanBeNegative() throws Exception {
            fixture.wear("right hand", ringOfLight(-1));

            calculate();

            assertEquals(-1, state.getCurLight());
        }
    }

    /**
     * The lowering of a positive modifier for a player who relies on the dark.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("PF_UNLIGHT")
    class Unlight {

        /**
         * A player with {@code PF_UNLIGHT} loses a point from a positive modifier, so a ring of 1
         * sheds nothing for them where it sheds 1 for anyone else.
         */
        @Test
        @DisplayName("a positive modifier loses a point")
        void positiveModifierLosesAPoint() throws Exception {
            fixture.wear("right hand", ringOfLight(1));
            state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            calculate();

            assertEquals(0, state.getCurLight());
        }

        /**
         * The lowering is applied to the item once, whatever the radius: a lantern of 2 with a
         * modifier of 1 is {@code 2 + 1 - 1 = 2}.
         */
        @Test
        @DisplayName("the point comes off the item once, however bright it is")
        void comesOffOnce() throws Exception {
            ItemObject lantern = light(500, ObjectFlag.OF_LIGHT_2);
            lantern.setModifiers(Map.of(ObjectModifier.OM_LIGHT, 1));
            fixture.wear("light", lantern);
            state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * An innate radius with no positive modifier is untouched: the test is on the
         * <em>modifier</em>, not on the item being a light.
         */
        @Test
        @DisplayName("an innate radius with no modifier is untouched")
        void innateRadiusUntouched() throws Exception {
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_2));
            state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * A modifier of zero or less is not "positive", so a ring of -1 still takes its full point
         * and is not softened to 0.
         */
        @Test
        @DisplayName("a negative modifier is not softened")
        void negativeModifierNotSoftened() throws Exception {
            fixture.wear("right hand", ringOfLight(-1));
            state.playerFlagOn(PlayerFlag.PF_UNLIGHT);

            calculate();

            assertEquals(-1, state.getCurLight());
        }
    }

    /**
     * Fuel: a burnt-out light gives nothing at all.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("fuel")
    class Fuel {

        /**
         * A fuelled light with no fuel left has its whole contribution zeroed — the modifier as well
         * as the innate radius — so {@code 2 + 1} becomes 0, not 1.
         */
        @Test
        @DisplayName("a burnt-out light loses its modifier as well as its radius")
        void burntOutLosesEverything() throws Exception {
            ItemObject lantern = light(0, ObjectFlag.OF_LIGHT_2);
            lantern.setModifiers(Map.of(ObjectModifier.OM_LIGHT, 1));
            fixture.wear("light", lantern);

            calculate();

            assertEquals(0, state.getCurLight());
        }

        /**
         * One turn of fuel is enough: the test is {@code timeout == 0}, not {@code <= 1}.
         */
        @Test
        @DisplayName("one turn of fuel still lights")
        void oneTurnOfFuel() throws Exception {
            fixture.wear("light", light(1, ObjectFlag.OF_LIGHT_2));

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * A light flagged {@code OF_NO_FUEL} — a magical one — shines at a timeout of zero.
         */
        @Test
        @DisplayName("a light that needs no fuel shines at timeout zero")
        void noFuelNeeded() throws Exception {
            fixture.wear("light", light(0, ObjectFlag.OF_LIGHT_3, ObjectFlag.OF_NO_FUEL));

            calculate();

            assertEquals(3, state.getCurLight());
        }

        /**
         * The fuel test is on lights only. A ring has a timeout of zero as a matter of course, and
         * its modifier still counts.
         */
        @Test
        @DisplayName("a non-light with a zero timeout is not affected")
        void nonLightIgnoresTimeout() throws Exception {
            ItemObject ring = ringOfLight(1);
            fixture.wear("right hand", ring);

            calculate();

            assertEquals(1, state.getCurLight());
        }

        /**
         * A burnt-out light zeroes only itself: a ring of 1 worn beside it still counts.
         */
        @Test
        @DisplayName("a burnt-out light does not darken the other slots")
        void otherSlotsSurvive() throws Exception {
            fixture.wear("light", light(0, ObjectFlag.OF_LIGHT_2));
            fixture.wear("right hand", ringOfLight(1));

            calculate();

            assertEquals(1, state.getCurLight());
        }
    }

    /**
     * The town by day.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("the town by day")
    class TownByDay {

        /**
         * Puts the character in the town at the given turn, carrying a lantern of radius 2.
         *
         * @param turn the game turn
         * @throws Exception if the fixture cannot reach a field
         */
        private void inTownAt(int turn) throws Exception {
            fixture.depth(0);
            fixture.wear("light", light(500, ObjectFlag.OF_LIGHT_2));
            GameState.setTurn(turn);
        }

        /**
         * By day in the town, on a real recalculation, the radius is 0 whatever is worn.
         */
        @Test
        @DisplayName("a real recalculation by day gives zero despite a lantern")
        void daytimeGivesZero() throws Exception {
            inTownAt(0);

            calculate();

            assertEquals(0, state.getCurLight());
        }

        /**
         * The last turn of the day is still day: turn 49999 is below the half-way mark.
         */
        @Test
        @DisplayName("the last turn of the day is still day")
        void lastTurnOfTheDay() throws Exception {
            inTownAt(FIRST_NIGHT_TURN - 1);

            calculate();

            assertEquals(0, state.getCurLight());
        }

        /**
         * The first turn of the night is not: turn 50000 is not below the half-way mark, so the
         * lantern counts.
         */
        @Test
        @DisplayName("the first turn of the night sums the equipment")
        void firstTurnOfTheNight() throws Exception {
            inTownAt(FIRST_NIGHT_TURN);

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * The cycle repeats: turn 100000 is day again.
         */
        @Test
        @DisplayName("the cycle repeats after a full day")
        void cycleRepeats() throws Exception {
            inTownAt(10 * DAY_LENGTH);

            calculate();

            assertEquals(0, state.getCurLight());
        }

        /**
         * Below the town it is never "day": depth 1 at turn 0 sums the equipment.
         */
        @Test
        @DisplayName("in the dungeon the time of day does not matter")
        void dungeonIgnoresTheClock() throws Exception {
            inTownAt(0);
            fixture.depth(1);

            calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * A hypothetical recalculation takes no shortcut: with {@code update} clear the lantern is
         * summed even in the town at noon.
         */
        @Test
        @DisplayName("a hypothetical recalculation by day sums the equipment")
        void hypotheticalIgnoresDaytime() throws Exception {
            inTownAt(0);

            PlayerCalcs.calcLight(fixture.player(), state, false);

            assertEquals(2, state.getCurLight());
        }

        /**
         * If the character's current state recorded a different radius, the early return asks for
         * the view and the monsters to be refreshed, as C's
         * {@code p->upkeep->update |= (PU_UPDATE_VIEW | PU_MONSTERS)}.
         */
        @Test
        @DisplayName("a changed radius by day raises the view and monster updates")
        void changedRadiusRaisesFlags() throws Exception {
            inTownAt(0);
            fixture.player().getPlayerState().setCurLight(2);

            calculate();

            PlayerUpkeep upkeep = fixture.player().getPlayerUpkeep();
            assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_UPDATE_VIEW));
            assertTrue(upkeep.updateHas(PlayerUpdateEnum.PU_MONSTERS));
        }

        /**
         * If the radius is the same as before, nothing is raised: C compares before it flags.
         */
        @Test
        @DisplayName("an unchanged radius by day raises nothing")
        void unchangedRadiusRaisesNothing() throws Exception {
            inTownAt(0);
            fixture.player().getPlayerState().setCurLight(0);

            calculate();

            PlayerUpkeep upkeep = fixture.player().getPlayerUpkeep();
            assertFalse(upkeep.updateHas(PlayerUpdateEnum.PU_UPDATE_VIEW));
            assertFalse(upkeep.updateHas(PlayerUpdateEnum.PU_MONSTERS));
        }

        /**
         * Port-only guard: a character with no state yet takes the early return without raising
         * anything and without throwing, where C's zeroed inline struct would compare equal to 0.
         */
        @Test
        @DisplayName("a character with no state yet raises nothing and does not throw")
        void noStateYet() throws Exception {
            inTownAt(0);
            Field field = Player.class.getDeclaredField("state");
            field.setAccessible(true);
            field.set(fixture.player(), null);

            calculate();

            assertEquals(0, state.getCurLight());
            assertFalse(fixture.player().getPlayerUpkeep().updateHas(PlayerUpdateEnum.PU_MONSTERS));
        }
    }
}
