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

package uk.co.jackoftradesltd.middle.objects;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.testsupport.CurseFixture;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;

/**
 * Tests the read of C's {@code curse->obj->time}, which the port makes as
 * {@code curse.getItemObject().getTime()}. {@code Curse} no longer has a {@code getTime} of its own:
 * the dice live on the curse's object, as C's {@code parse_curse_time} writes them.
 *
 * <p>A curse with no {@code time:} line still has a valid — zero — timing value in C, because
 * {@code curse->obj} is a zeroed {@code struct object} whether or not it carries an effect. The
 * port's object starts with a zero {@link Random} and never holds {@code null}. <em>air swing</em>
 * ({@code curse.txt}) is the case in the data: a combat penalty and nothing else.
 *
 * <p>{@code CurseAssembler} does not yet write the dice onto the object, and
 * {@code Effect_time_migration.md} owns that move; these tests set them by hand.
 *
 * <p>Class CurseObjectTimeTest coded on 261005, replacing {@code CurseGetTimeTest}, which tested
 * the {@code Curse.getTime} that the unflattening removed.
 *
 * @author Rowan Crowther
 */
class CurseObjectTimeTest {

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private static Object savedConstants;

    /**
     * The {@code WorldRegistry.worlds} in place before this class replaced it.
     */
    private static Object savedWorlds;

    /**
     * Seeds just enough of {@code GameConstants} and {@code WorldRegistry} for a zero-valued
     * {@link Random} to resolve under every {@link DamageAspect}, following the same rationale as
     * {@link ItemObjectRechargeTest#seedWorldMaxDepth()}: {@code randCalc}'s {@code AVERAGE} and
     * {@code RANDOMIZE} aspects read the global depth tables unconditionally through {@code
     * RandomValueUtils.mBonusCalc}, even when the {@code m_bonus} term is {@code 0} and contributes
     * nothing — so an unseeded table throws before the zero can ever be produced.
     */
    @BeforeAll
    static void seedDepthTables() {
        GameConstantsData seed = new GameConstantsData(
                null, null, null, null,
                new WorldData(128, 0, 0, 0, 0, 0, 0, 0, 0, 0),
                null, null, null, null, null, null, null, null, null, null, null, null);
        savedConstants = setStatic(GameConstants.class, "data", seed);
        savedWorlds = setStatic(WorldRegistry.class, "worlds",
                List.of(new World(0, "Town", null, null)));
    }

    /**
     * Puts back whatever {@code GameConstants.data} and {@code WorldRegistry.worlds} held before
     * {@link #seedDepthTables()}, so a test class running later in the same JVM sees the state it
     * expects.
     */
    @AfterAll
    static void restoreDepthTables() {
        setStatic(GameConstants.class, "data", savedConstants);
        setStatic(WorldRegistry.class, "worlds", savedWorlds);
    }

    /**
     * Writes a private static field, returning its previous value.
     *
     * @param owner the declaring class
     * @param name  the declared field name
     * @param value the value to write
     * @return the value the field held beforehand
     */
    private static Object setStatic(Class<?> owner, String name, Object value) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            Object previous = field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(owner.getSimpleName() + "." + name
                    + " is no longer settable by reflection", e);
        }
    }

    /**
     * A curse with nothing on it but a name.
     *
     * @return the curse
     */
    private static Curse bare() {
        return CurseFixture.curse("test curse", List.of(), 0, null, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "", 0);
    }

    /**
     * A curse whose object has been given timing dice, as {@code poison}'s {@code time:1d500} would.
     */
    @Nested
    @DisplayName("a curse whose object has timing dice")
    class WithDice {

        /**
         * {@code getTime} hands back a copy of the dice, as every read of C's {@code obj->time}
         * copies the {@code random_value} struct, so a caller cannot change the curse's own.
         */
        @Test
        @DisplayName("answers a copy of the dice that were set")
        void copyOfDice() {
            Curse poison = bare();
            poison.getItemObject().setTime(new Random(0, 0, 1, 500, false));

            Random read = poison.getItemObject().getTime();

            assertEquals(1, read.getDice());
            assertEquals(500, read.getSides());
            assertEquals(0, read.getBase());
            assertNotSame(read, poison.getItemObject().getTime());
        }

        /**
         * Changing the {@link Random} after handing it over must not reach the curse: C's struct
         * assign copies by value.
         */
        @Test
        @DisplayName("is not changed by later changes to the dice that were handed in")
        void handedInDiceAreCopied() {
            Curse poison = bare();
            Random given = new Random(0, 0, 1, 500, false);
            poison.getItemObject().setTime(given);

            given.setSides(5);

            assertEquals(500, poison.getItemObject().getTime().getSides());
        }
    }

    /**
     * A curse with no {@code time:} line at all.
     */
    @Nested
    @DisplayName("a curse with no timing dice")
    class WithoutDice {

        /**
         * C's {@code curse->obj->time} is a zero {@code random_value} here, read unconditionally by
         * every caller ({@code copy_curses}, {@code obj-curse.c}, {@code game-world.c}). The port
         * must answer {@code 0} under every {@link DamageAspect} rather than throwing.
         */
        @ParameterizedTest
        @DisplayName("answers a zero-valued Random under every damage aspect, rather than throwing")
        @EnumSource(DamageAspect.class)
        void answersZeroForEveryAspect(DamageAspect aspect) {
            Random time = bare().getItemObject().getTime();

            assertNotNull(time);
            assertEquals(0, time.randCalc(0, aspect));
        }

        /**
         * {@code mBonusCalc}'s {@code RANDOMIZE} branch scales with dungeon level; a zero
         * {@code m_bonus} must still floor to zero rather than picking up a level-derived value.
         */
        @Test
        @DisplayName("stays zero at a non-zero dungeon level")
        void staysZeroAtDepth() {
            assertEquals(0, bare().getItemObject().getTime().randCalc(50, DamageAspect.RANDOMIZE));
        }
    }
}
