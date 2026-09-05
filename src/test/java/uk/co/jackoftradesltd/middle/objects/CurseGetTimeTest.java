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
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link Curse#getTime()} — the read of C's {@code curse->obj->time}
 * ({@code object.h:459}), a field the port has no direct home for since a curse holds at most one
 * {@link Effect} rather than the {@code struct object} C nests it in.
 *
 * <p>{@code curse.txt} pairs every {@code time:} line with the {@code effect:} line just above it, and
 * no curse has more than one effect block, so the port folds the timing dice onto that single
 * {@link Effect} instead. A curse with no effect block at all still has a valid — zero — timing value
 * in C, because {@code curse->obj} is a zeroed {@code struct object} regardless of whether it carries
 * an effect. <em>air swing</em> ({@code curse.txt:388-394}) is exactly this case: a combat penalty and
 * nothing else.
 *
 * @author Rowan Crowther
 */
class CurseGetTimeTest {

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
     * A minimal effect carrying only the timing dice under test, everything else the placeholder
     * {@code EF_NONE} identity uses elsewhere in the effect test suite.
     *
     * @param time the timing dice to attach
     * @return the effect
     */
    private static Effect effectWithTime(Random time) {
        return new Effect(EffectEnum.EF_NONE, new Random(0, 0, 0, 1, false), "", 0, 0,
                EffectSubTypeEnum.EST_NONE, new EffectSubTypeWrapper(ProjectionEnum.PROJ_ACID),
                0, 0, time, new ArrayList<>(), "");
    }

    /**
     * A curse with the given effect (or {@code null}, mirroring a curse with no {@code effect:}
     * line), everything else empty.
     *
     * @param effect the curse's single effect, or {@code null}
     * @return the curse
     */
    private static Curse curse(Effect effect) {
        return new Curse("test curse", List.of(), 0, effect, new Flag<>(ObjectFlag.class),
                Map.of(), Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * The ordinary path: a curse with an effect reads that effect's timing dice.
     */
    @Nested
    @DisplayName("a curse with an effect")
    class WithEffect {

        /**
         * {@code getTime} hands back the very {@link Random} the effect carries, not a copy — the
         * same object {@code poison} ({@code curse.txt:161-181}, {@code time:1d500}) would produce.
         */
        @Test
        @DisplayName("delegates to the effect's own timing dice")
        void delegatesToEffectTime() {
            Random poisonTime = new Random(0, 0, 1, 500, false);
            Curse poison = curse(effectWithTime(poisonTime));

            assertSame(poisonTime, poison.getTime());
        }
    }

    /**
     * The boundary found in stage 1: a curse with no {@code effect:} line at all.
     */
    @Nested
    @DisplayName("a curse with no effect")
    class WithoutEffect {

        /**
         * C's {@code curse->obj->time} is a zero {@code random_value} here, read unconditionally by
         * every caller ({@code copy_curses}, {@code obj-curse.c:67,203}, {@code game-world.c:368}).
         * The port must answer {@code 0} under every {@link DamageAspect} rather than throwing.
         */
        @ParameterizedTest
        @DisplayName("answers a zero-valued Random under every damage aspect, rather than throwing")
        @EnumSource(DamageAspect.class)
        void answersZeroForEveryAspect(DamageAspect aspect) {
            Curse airSwing = curse(null);

            assertEquals(0, airSwing.getTime().randCalc(0, aspect));
        }

        /**
         * {@code mBonusCalc}'s {@code RANDOMIZE} branch scales with dungeon level; a zero
         * {@code m_bonus} must still floor to zero rather than picking up a level-derived value.
         */
        @Test
        @DisplayName("stays zero at a non-zero dungeon level")
        void staysZeroAtDepth() {
            Curse airSwing = curse(null);

            assertEquals(0, airSwing.getTime().randCalc(50, DamageAspect.RANDOMIZE));
        }
    }
}
