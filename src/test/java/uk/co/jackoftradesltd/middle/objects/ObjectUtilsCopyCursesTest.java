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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.World;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.WorldData;
import uk.co.jackoftradesltd.middle.game.globals.registry.WorldRegistry;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#copyCurses}, the port of C's {@code copy_curses} ({@code obj-curse.c:52}).
 *
 * <p>Two things distinguish this merge from {@link ObjectUtils#copySlays} and
 * {@link ObjectUtils#copyBrands}: a curse named by {@code source} always overwrites whatever
 * {@code dest} held for it, with no "keep the stronger one" comparison, and the timeout written is
 * always freshly rolled from the curse's own dice rather than carried over from {@code source}'s
 * {@link CurseData}. The other thing under test is the regression this session found and fixed: a
 * {@link ItemObject} whose curse map has never been created — {@code new ItemObject()}, never
 * {@link ItemObject#wipe()}'d — used to throw a {@link NullPointerException} out of
 * {@link ItemObject#setCurses}, because nothing called {@link ItemObject#initCurses} first.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsCopyCursesTest {

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private static Object savedConstants;

    /**
     * The {@code WorldRegistry.worlds} in place before this class replaced it.
     */
    private static Object savedWorlds;

    /**
     * Seeds just enough of {@code GameConstants} and {@code WorldRegistry} for a {@link Random} to
     * resolve under {@link uk.co.jackoftradesltd.middle.enums.DamageAspect#RANDOMIZE} —
     * {@code copyCurses} always rolls a curse's timeout under that aspect, and {@code randCalc}
     * reads the global depth tables unconditionally even when the roll itself is deterministic. See
     * {@link CurseGetTimeTest#seedDepthTables()}, which this mirrors.
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
     * {@link #seedDepthTables()}.
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
     * A minimal effect carrying only the timing dice under test, following
     * {@link CurseGetTimeTest#effectWithTime}.
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
     * A curse whose timing dice always resolve to the given, deterministic value — {@code base}
     * with no dice ({@code sides:1}) and no level scaling, so
     * {@code getTime().randCalc(anyLevel, anyAspect)} always answers {@code base}.
     *
     * @param name the curse's name, for {@link Object#toString()} only
     * @param base the fixed value its timeout always rolls to
     * @return the curse
     */
    private static Curse curseWithFixedTimeout(String name, int base) {
        return new Curse(name, List.of(), 0, effectWithTime(new Random(base, 0, 0, 1, false)),
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * Reads the private {@code curses} field directly, bypassing {@link ItemObject#getCurses}'s
     * null-to-{@code Map.of()} translation, so identity can be checked as well as content.
     *
     * @param item the item to read
     * @return whatever {@code item.curses} currently holds, {@code null} included
     */
    private static Map<Curse, CurseData> rawCurses(ItemObject item) {
        try {
            Field field = ItemObject.class.getDeclaredField("curses");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Curse, CurseData> value = (Map<Curse, CurseData>) field.get(item);
            return value;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("ItemObject.curses is no longer reachable", e);
        }
    }

    /**
     * The ordinary path and the overwrite/leave-alone rules that follow from it.
     */
    @Nested
    @DisplayName("merging into an item that already has curses")
    class Merging {

        /**
         * A curse named only in {@code source} is added, with the source's power and a freshly
         * rolled timeout.
         */
        @Test
        @DisplayName("a curse absent from dest is added, power and timeout both from source's dice")
        void addsNewCurse() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse fresh = curseWithFixedTimeout("fresh curse", 42);

            ObjectUtils.copyCurses(dest, Map.of(fresh, new CurseData(5, 999)));

            assertEquals(5, dest.getCurses().get(fresh).getPower());
            assertEquals(42, dest.getCurses().get(fresh).getTimeout(),
                    "timeout must come from the curse's own dice, not source's CurseData");
        }

        /**
         * A curse present in both is overwritten unconditionally - C's {@code obj->curses[i].power
         * = source[i]} has no comparison against the old value, and neither does the port. This is
         * what distinguishes {@code copyCurses} from {@link ObjectUtils#copySlays} and
         * {@link ObjectUtils#copyBrands}, which both keep whichever side is stronger.
         */
        @Test
        @DisplayName("a curse present in both is overwritten, even when dest's power was higher")
        void overwritesRegardlessOfWhichIsStronger() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse shared = curseWithFixedTimeout("shared curse", 7);
            dest.addCurse(shared, 99, 1);

            ObjectUtils.copyCurses(dest, Map.of(shared, new CurseData(3, 0)));

            assertEquals(3, dest.getCurses().get(shared).getPower(),
                    "source always wins, unlike copySlays/copyBrands's keep-the-stronger rule");
        }

        /**
         * A curse dest already carries, that {@code source} does not name, is left exactly as it
         * was - C only ever touches indices where {@code source[i]} is set.
         */
        @Test
        @DisplayName("a curse absent from source is left untouched")
        void leavesUnnamedCurseAlone() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse untouched = curseWithFixedTimeout("untouched curse", 1);
            Curse other = curseWithFixedTimeout("other curse", 2);
            dest.addCurse(untouched, 7, 123);

            ObjectUtils.copyCurses(dest, Map.of(other, new CurseData(4, 0)));

            assertEquals(7, dest.getCurses().get(untouched).getPower());
            assertEquals(123, dest.getCurses().get(untouched).getTimeout(),
                    "an untouched curse's timeout must not be re-rolled either");
        }
    }

    /**
     * A {@code null} source, C's {@code !source}.
     */
    @Nested
    @DisplayName("a null source")
    class NullSource {

        /**
         * Returns immediately without writing anything - matching C's {@code if (!source) return;}
         * ahead of its allocation branch.
         */
        @Test
        @DisplayName("dest is left completely untouched, curse map identity included")
        void leavesDestUntouched() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse existing = curseWithFixedTimeout("existing curse", 5);
            dest.addCurse(existing, 3, 8);
            Map<Curse, CurseData> before = rawCurses(dest);

            ObjectUtils.copyCurses(dest, null);

            assertSame(before, rawCurses(dest), "a null source must not even reallocate the curse map");
            assertEquals(3, dest.getCurses().get(existing).getPower());
        }
    }

    /**
     * The boundary stage 1 found: an {@link ItemObject} whose curse map was never populated.
     * {@code new ItemObject()} now creates that map empty rather than leaving it {@code null}
     * (the bare constructor's own fix, 260905), so the regression this nested class was written
     * to pin - a {@link NullPointerException} out of {@link ItemObject#setCurses}'s
     * {@code curses.clear()}, before {@code copyCurses} called {@link ItemObject#initCurses} - can
     * no longer arise from a null field. The tests stay to pin the same outward behaviour by the
     * route that is now reachable.
     */
    @Nested
    @DisplayName("an item whose curse map was never populated")
    class NeverInitialised {

        /**
         * The starting state {@code copyCurses} has to cope with, and the transfer it performs
         * onto it.
         */
        @Test
        @DisplayName("does not throw, and the curse is applied")
        void doesNotThrowAndApplies() {
            ItemObject dest = new ItemObject();
            assertTrue(rawCurses(dest).isEmpty(), "precondition: a fresh ItemObject has no curses yet");
            Curse curse = curseWithFixedTimeout("regression curse", 11);

            assertDoesNotThrow(() -> ObjectUtils.copyCurses(dest, Map.of(curse, new CurseData(6, 0))));

            assertEquals(6, dest.getCurses().get(curse).getPower());
            assertEquals(11, dest.getCurses().get(curse).getTimeout());
        }

        /**
         * The same call with a non-null but empty source - {@code setCurses} still runs, since it is
         * not inside the merge loop, so this is a distinct case from a null source rather than a
         * weaker version of it.
         */
        @Test
        @DisplayName("an empty (not null) source does not throw either")
        void emptySourceDoesNotThrow() {
            ItemObject dest = new ItemObject();

            assertDoesNotThrow(() -> ObjectUtils.copyCurses(dest, Map.of()));
            assertTrue(dest.getCurses().isEmpty());
        }
    }
}
