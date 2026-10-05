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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectUtils#copyCurses}, the port of C's {@code copy_curses} ({@code obj-curse.c}).
 *
 * <p>C's source is an array indexed by curse, so a curse only exists if it is in the global table.
 * The port walks {@link ObjectRegistry#getCurses()} in the same way, which means every curse these
 * tests merge has to be registered first; {@link #register} does that and the class restores the
 * registry afterwards. A curse named by {@code source} but not in the registry is dropped, as C
 * could not hold it at all, and one test pins that.
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
     * The {@code ObjectRegistry.curses} in place before this class started registering fixtures.
     */
    private static Object savedCurses;

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
        savedCurses = setStatic(ObjectRegistry.class, "curses", null);
    }

    /**
     * Puts back whatever {@code GameConstants.data} and {@code WorldRegistry.worlds} held before
     * {@link #seedDepthTables()}.
     */
    @AfterAll
    static void restoreDepthTables() {
        setStatic(GameConstants.class, "data", savedConstants);
        setStatic(WorldRegistry.class, "worlds", savedWorlds);
        setStatic(ObjectRegistry.class, "curses", savedCurses);
    }

    /**
     * Registers the given curses, in the order given, as the loaded curse table.
     *
     * <p>{@code copyCurses} only visits registered curses, as C's loop only visits indices below
     * {@code curse_max}.
     *
     * @param curses the curses to register, in index order
     */
    private static void register(Curse... curses) {
        ObjectRegistry.setCurses(new ArrayList<>(List.of(curses)));
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
        return curseWithFixedTimeout(name, base, 0);
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
     * As {@link #curseWithFixedTimeout(String, int)}, with the curse's index in the table.
     *
     * @param name  the curse's name, for {@link Object#toString()} only
     * @param base  the fixed value its timeout always rolls to
     * @param index the curse's index, which is its position in the registry
     * @return the curse
     */
    private static Curse curseWithFixedTimeout(String name, int base, int index) {
        return new Curse(name, List.of(), 0, effectWithTime(new Random(base, 0, 0, 1, false)),
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), 0, 0, 0,
                List.of(), new Flag<>(ObjectFlag.class), "", "", index);
    }

    /**
     * Starts each test with an empty curse registry, so a curse one test registered cannot be
     * visited by the next.
     */
    @BeforeEach
    @AfterEach
    void emptyRegistry() {
        ObjectRegistry.setCurses(new ArrayList<>());
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
            register(fresh);

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
            register(shared);
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
            Curse other = curseWithFixedTimeout("other curse", 2, 1);
            register(untouched, other);
            dest.addCurse(untouched, 7, 123);

            ObjectUtils.copyCurses(dest, Map.of(other, new CurseData(4, 0)));

            assertEquals(7, dest.getCurses().get(untouched).getPower());
            assertEquals(123, dest.getCurses().get(untouched).getTimeout(),
                    "an untouched curse's timeout must not be re-rolled either");
        }
    }

    /**
     * What the walk over the registry adds: C's power-zero skip, its index order, and the fact that
     * a curse outside the table cannot be held.
     */
    @Nested
    @DisplayName("walking the registry")
    class RegistryWalk {

        /**
         * C starts each pass with {@code if (!source[i]) continue;}, so a source entry at power zero
         * is "no curse": it writes neither a power nor a timeout, and leaves whatever dest held.
         */
        @Test
        @DisplayName("a power-zero source entry is skipped and dest keeps what it had")
        void powerZeroIsSkipped() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse held = curseWithFixedTimeout("held curse", 9, 0);
            Curse absent = curseWithFixedTimeout("absent curse", 9, 1);
            register(held, absent);
            dest.addCurse(held, 4, 77);

            ObjectUtils.copyCurses(dest, Map.of(held, new CurseData(0, 0), absent, new CurseData(0, 0)));

            assertEquals(4, dest.getCurses().get(held).getPower(), "zero in source does not overwrite");
            assertEquals(77, dest.getCurses().get(held).getTimeout(), "and does not re-roll the timeout");
            assertFalse(dest.getCurses().containsKey(absent), "a zero entry never adds a curse");
        }

        /**
         * C's array is walked by index, so new curses land in index order however the source map
         * happens to be ordered. A {@code Map.of} has no order of its own to rely on.
         */
        @Test
        @DisplayName("new curses are added in registry order")
        void addedInRegistryOrder() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse first = curseWithFixedTimeout("first", 1, 0);
            Curse second = curseWithFixedTimeout("second", 2, 1);
            Curse third = curseWithFixedTimeout("third", 3, 2);
            register(first, second, third);

            ObjectUtils.copyCurses(dest, Map.of(
                    third, new CurseData(1, 0),
                    first, new CurseData(1, 0),
                    second, new CurseData(1, 0)));

            assertEquals(List.of(first, second, third), new ArrayList<>(dest.getCurses().keySet()));
        }

        /**
         * A curse already on dest does not keep a front place: the map orders by curse index, so a
         * new curse with a lower index lists ahead of it, lowest first, as C's walk of the array
         * does.
         */
        @Test
        @DisplayName("a curse added with a lower index lists ahead of one dest already holds")
        void lowerIndexListsFirst() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse lower = curseWithFixedTimeout("lower", 1, 0);
            Curse higher = curseWithFixedTimeout("higher", 2, 1);
            register(lower, higher);
            dest.addCurse(higher, 3, 5);

            ObjectUtils.copyCurses(dest, Map.of(lower, new CurseData(1, 0)));

            assertEquals(List.of(lower, higher), new ArrayList<>(dest.getCurses().keySet()));
            assertEquals(3, dest.getCurses().get(higher).getPower(), "the untouched curse is unchanged");
            assertEquals(1, dest.getCurses().get(lower).getPower());
        }

        /**
         * A curse the registry does not hold has no index, and C's array could not hold it. The port
         * drops it, so a caller that builds a curse by hand has to register it first.
         */
        @Test
        @DisplayName("a curse the registry does not hold is dropped")
        void unregisteredCurseIsDropped() {
            ItemObject dest = new ItemObject();
            dest.wipe();
            Curse registered = curseWithFixedTimeout("registered", 1, 0);
            Curse stranger = curseWithFixedTimeout("stranger", 2, 1);
            register(registered);

            ObjectUtils.copyCurses(dest, Map.of(registered, new CurseData(3, 0), stranger, new CurseData(3, 0)));

            assertTrue(dest.getCurses().containsKey(registered));
            assertFalse(dest.getCurses().containsKey(stranger));
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
            register(curse);

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
