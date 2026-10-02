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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.backend.parser.ObjectPropertyReader;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.ui.entry.reader.UIEntryReader;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.read;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the driver and curse plumbing of the power calculation in {@link ItemObject}: both
 * {@code objectPower} overloads, both {@code nonStandardWeightPower} overloads, both
 * {@code cursePower} overloads, {@code freeSlays}, {@code freeBrands}, {@code freeCurses} and
 * {@code applyCurseAttributes}. They are the ports of {@code object_power},
 * {@code nonstandard_weight_power} and {@code curse_power} ({@code obj-power.c}) and
 * {@code apply_curse_attributes} ({@code obj-curse.c}).
 *
 * <p>Every expected figure is worked out from the C source and the constants in
 * {@code obj-power.h}, not read back from the port. The item throughout is a cloak, because a
 * cloak with no base armour class has a power of exactly its {@code to_a} (for {@code to_a} under
 * 26): {@code to_a * TO_AC_POWER / 2} is {@code to_a} when {@code TO_AC_POWER} is 2. That makes
 * the effect of a curse's combat bonus visible as plain arithmetic.
 *
 * <p>The boundaries worth the most are the integer divisions. {@code nonstandard_weight_power}
 * divides a weight difference by 50 and C truncates toward zero, so 30 tenth-pounds heavier is no
 * adjustment at all and 80 heavier is minus one, where a floor would give minus one and minus two.
 * The throwing term divides each weight by 12 <em>before</em> subtracting. And a weight-affecting
 * curse that lowers power is scaled by its strength held between 20 and 100 and then divided by
 * 100, which truncates toward zero too: minus three at strength 50 is minus one, not minus two.
 *
 * <p>The registries the power calculation reads are seeded here and put back afterwards, so the
 * class passes alone and leaves nothing behind for another suite.
 *
 * <p>Class ItemObjectPowerCursesTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectPowerCursesTest {

    private static Object savedRenderers;
    private static Object savedUiBases;
    private static Object savedUiEntries;
    private static Object savedProperties;
    private static Object savedFlagSets;
    private static Object savedElementSets;
    private static Object savedElementPowers;
    private static Object savedCurses;
    /**
     * The kind every fixture cloak shares.
     */
    private static ObjectKind cloakKind;
    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * Loads the real object-property data and installs the power tables, because the power
     * calculation looks every modifier up even for an object with none.
     */
    @BeforeAll
    static void seedRegistries() throws Exception {
        savedRenderers = ItemFixture.setStatic(UIRegistry.class, "uiEntryRenderers",
                new UIEntryRendererReader().parseWithResults("lib/gamedata/ui_entry_renderer.txt").items());
        savedUiBases = ItemFixture.setStatic(UIRegistry.class, "uiEntryBases",
                new UIEntryBaseReader().parseWithResults("lib/gamedata/ui_entry_base.txt").items());
        savedUiEntries = ItemFixture.setStatic(UIRegistry.class, "uiEntries",
                new UIEntryReader().parseWithResults("lib/gamedata/ui_entry.txt").items());
        savedProperties = ItemFixture.setStatic(ObjectRegistry.class, "objectProperties",
                new ObjectPropertyReader().parseWithResults("lib/gamedata/object_property.txt").items());

        Map<ObjectFlagType, FlagSet> flagSets = new HashMap<>();
        flagSets.put(ObjectFlagType.OFT_SUST, new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains"));
        flagSets.put(ObjectFlagType.OFT_PROT, new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections"));
        flagSets.put(ObjectFlagType.OFT_MISC, new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities"));
        savedFlagSets = ItemFixture.setStatic(ObjectRegistry.class, "flagSets", flagSets);

        savedElementSets = ItemFixture.setStatic(ObjectRegistry.class, "elementSets", new ArrayList<ElementSet>());
        savedElementPowers = ItemFixture.setStatic(ObjectRegistry.class, "elementPowers",
                new ArrayList<ElementPowers>());

        savedCurses = ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<Curse>());

        cloakKind = ItemFixture.loadedKind(TValue.TV_CLOAK, "cloak", 40);
    }

    /**
     * Puts the registries back.
     */
    @AfterAll
    static void restoreRegistries() {
        ItemFixture.setStatic(UIRegistry.class, "uiEntryRenderers", savedRenderers);
        ItemFixture.setStatic(UIRegistry.class, "uiEntryBases", savedUiBases);
        ItemFixture.setStatic(UIRegistry.class, "uiEntries", savedUiEntries);
        ItemFixture.setStatic(ObjectRegistry.class, "objectProperties", savedProperties);
        ItemFixture.setStatic(ObjectRegistry.class, "flagSets", savedFlagSets);
        ItemFixture.setStatic(ObjectRegistry.class, "elementSets", savedElementSets);
        ItemFixture.setStatic(ObjectRegistry.class, "elementPowers", savedElementPowers);
        ItemFixture.setStatic(ObjectRegistry.class, "curses", savedCurses);
    }

    /**
     * Builds a curse with only the properties these tests care about.
     *
     * @param index    the curse's registry index, which fixes the order curses are applied in
     * @param weight   the curse's weight figure
     * @param multiply whether it carries {@code OF_MULTIPLY_WEIGHT}
     * @param toAC     its armour-class adjustment, C's {@code to_a}
     * @param flags    any further object flags it grants
     * @return the curse
     */
    private static Curse curse(int index, int weight, boolean multiply, int toAC, ObjectFlag... flags) {
        Flag<ObjectFlag> objectFlags = new Flag<>(ObjectFlag.class);
        if (multiply) {
            objectFlags.set(List.of(ObjectFlag.OF_MULTIPLY_WEIGHT));
        }
        if (flags.length > 0) {
            objectFlags.set(List.of(flags));
        }

        return new Curse("curse" + index, List.of(), weight, null, objectFlags,
                new HashMap<>(), new HashMap<>(), 0, 0, toAC, List.of(),
                new Flag<>(ObjectFlag.class), "", "", index);
    }

    /**
     * Makes the given curses the registry's list, which {@code applyCurseAttributes} walks in
     * index order.
     *
     * @param curses the curses
     */
    private static void register(Curse... curses) {
        ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<>(List.of(curses)));
    }

    /**
     * Builds a cloak of the given weight and to-armour bonus, carrying the given curses in the
     * order given.
     *
     * @param weight the base weight in tenth-pounds
     * @param toAC   the to-armour-class bonus
     * @param laid   alternating curse and power, in the order they are laid on the item; each curse
     *               is also added to the registry if it is not already there
     * @return the cloak
     */
    private static ItemObject cloak(int weight, int toAC, Object... laid) {
        ItemFixture fixture = ItemFixture.item(TValue.TV_CLOAK).kind(cloakKind);
        List<Curse> registered = new ArrayList<>(ObjectRegistry.getCurses());
        for (int i = 0; i < laid.length; i += 2) {
            Curse laidCurse = (Curse) laid[i];
            fixture.curse(laidCurse, new CurseData((int) laid[i + 1], 0));
            // The registry is what applyCurseAttributes walks, so a carried curse must be in it
            if (!registered.contains(laidCurse)) {
                registered.add(laidCurse);
            }
        }
        register(registered.toArray(new Curse[0]));
        ItemObject cloak = fixture.build();
        set(cloak, "weight", weight);
        set(cloak, "toAC", toAC);
        return cloak;
    }

    /**
     * Calls a private method, unwrapping any exception it throws.
     *
     * @param target the object to call it on
     * @param name   the method's name
     * @param types  the parameter types
     * @param args   the argument values
     * @return what it returns
     */
    private static Object invoke(Object target, String name, Class<?>[] types, Object... args) throws Exception {
        Method method = ItemObject.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    private static int weightPower(ItemObject item, int power) throws Exception {
        return (int) invoke(item, "nonStandardWeightPower", new Class<?>[]{int.class}, power);
    }

    private static int cursePower(ItemObject item, int power) throws Exception {
        return (int) invoke(item, "cursePower", new Class<?>[]{int.class, boolean.class, String.class},
                power, false, null);
    }

    private static int objectPower(ItemObject item) throws Exception {
        return (int) invoke(item, "objectPower", new Class<?>[]{boolean.class, String.class}, false, null);
    }

    private static void applyCurses(ItemObject item, Curse ignore) throws Exception {
        invoke(item, "applyCurseAttributes", new Class<?>[]{Curse.class}, ignore);
    }

    private static ElementInfo withLevel(int level) {
        ElementInfo info = new ElementInfo();
        info.setResLevel(level);
        return info;
    }

    private static Flag<ObjectFlag> flagsOf(ObjectFlag... on) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.set(List.of(on));
        return flags;
    }

    /**
     * Installs a player with a body, which the power calculation needs for its slot comparisons,
     * and empties the registry's curse list. The player is installed before any item is built,
     * because an item captures the player at construction.
     */
    @BeforeEach
    void installPlayer() {
        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());
        ItemFixture.setStatic(ObjectRegistry.class, "curses", new ArrayList<Curse>());
    }

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * C's {@code nonstandard_weight_power}: the adjustment for a weight the curses have changed.
     */
    @Nested
    @DisplayName("nonStandardWeightPower")
    class NonStandardWeight {

        /**
         * With no curses the weights are equal and the first test returns the power untouched.
         */
        @Test
        @DisplayName("an uncursed object is unchanged")
        void uncursed() throws Exception {
            assertEquals(100, weightPower(cloak(100, 0), 100));
        }

        /**
         * The weight difference over 50 truncates toward zero: 30 heavier is zero, not minus one.
         */
        @Test
        @DisplayName("a difference under 50 tenth-pounds truncates to nothing")
        void truncatesSmallDifference() throws Exception {
            Curse heavier = curse(1, 30, false, 0);

            assertEquals(100, weightPower(cloak(100, 0, heavier, 50), 100));
        }

        /**
         * 80 heavier is {@code -80 / 50}, which truncates to -1; a floor division would give -2.
         */
        @Test
        @DisplayName("heavier by 80 loses one point, not two")
        void heavierLosesOne() throws Exception {
            Curse heavier = curse(1, 80, false, 0);

            assertEquals(99, weightPower(cloak(100, 0, heavier, 50), 100));
        }

        /**
         * Lighter is better for something that is not armour: 80 lighter is {@code 80 / 50}, one
         * point; 100 lighter, to weightlessness, is two.
         */
        @Test
        @DisplayName("lighter gains a point for each 50")
        void lighterGains() throws Exception {
            assertEquals(101, weightPower(cloak(100, 0, curse(1, -80, false, 0), 50), 100));
            assertEquals(102, weightPower(cloak(100, 0, curse(1, -100, false, 0), 50), 100));
        }

        /**
         * A multiplying curse works the same way on the figure it produces: 100 times two is 200,
         * and {@code -100 / 50} is -2.
         */
        @Test
        @DisplayName("a multiplying curse is judged on the weight it produces")
        void multiplyingCurse() throws Exception {
            assertEquals(98, weightPower(cloak(100, 0, curse(1, 200, true, 0), 50), 100));
        }

        /**
         * A curse at power zero is not active, so it changes no weight and the weights are equal.
         */
        @Test
        @DisplayName("a curse at power zero changes nothing")
        void inactiveCurse() throws Exception {
            assertEquals(100, weightPower(cloak(100, 0, curse(1, 80, false, 0), 0), 100));
        }

        /**
         * An object with base armour has had its weight priced by {@code ac_power}, so the
         * carrying-capacity term is skipped.
         */
        @Test
        @DisplayName("an object with base armour skips the carrying-capacity term")
        void baseArmourSkips() throws Exception {
            ItemObject item = cloak(100, 0, curse(1, 80, false, 0), 50);
            set(item, "baseAC", 5);

            assertEquals(100, weightPower(item, 100));
        }

        /**
         * Each weight is divided by 12 before they are subtracted: 110 and 100 give 9 and 8, so one
         * step of 15. Subtracting first would give {@code 10 / 12}, which is zero. The
         * carrying-capacity term is {@code -10 / 50}, zero.
         */
        @Test
        @DisplayName("throwing divides each weight before subtracting")
        void throwingDividesFirst() throws Exception {
            ItemObject item = cloak(100, 0, curse(1, 10, false, 0), 50);
            set(item, "flags", flagsOf(ObjectFlag.OF_THROWING));

            assertEquals(115, weightPower(item, 100));
        }

        /**
         * The carrying-capacity and throwing terms are both taken: 150 and 100 divide to 12 and 8,
         * four steps of 15 is 60, and the {@code -50 / 50} is -1, for 59 together.
         */
        @Test
        @DisplayName("the two terms add")
        void bothTerms() throws Exception {
            ItemObject item = cloak(100, 0, curse(1, 50, false, 0), 50);
            set(item, "flags", flagsOf(ObjectFlag.OF_THROWING));

            assertEquals(159, weightPower(item, 100));
        }

        /**
         * A lighter missile is worth less: 60 and 100 divide to 5 and 8, minus three steps of 15 is
         * -45, and {@code 40 / 50} is zero.
         */
        @Test
        @DisplayName("a lighter throwing object loses power")
        void lighterThrowing() throws Exception {
            ItemObject item = cloak(100, 0, curse(1, -40, false, 0), 50);
            set(item, "flags", flagsOf(ObjectFlag.OF_THROWING));

            assertEquals(55, weightPower(item, 100));
        }

        /**
         * A curse can be what makes the object throwable, but only while it is active.
         */
        @Test
        @DisplayName("an active curse's THROWING flag counts, an inactive one's does not")
        void throwingFromCurse() throws Exception {
            Curse heavier = curse(1, 10, false, 0);
            Curse throwing = curse(2, 0, false, 0, ObjectFlag.OF_THROWING);

            assertEquals(115, weightPower(cloak(100, 0, heavier, 50, throwing, 50), 100));
            assertEquals(100, weightPower(cloak(100, 0, heavier, 50, throwing, 0), 100));
        }

        /**
         * The adjustment is added with a saturating add, so it cannot wrap round at either end.
         */
        @Test
        @DisplayName("the total saturates")
        void saturates() throws Exception {
            assertEquals(Integer.MAX_VALUE,
                    weightPower(cloak(100, 0, curse(1, -100, false, 0), 50), Integer.MAX_VALUE));
            assertEquals(Integer.MIN_VALUE,
                    weightPower(cloak(100, 0, curse(1, 80, false, 0), 50), Integer.MIN_VALUE));
        }

        /**
         * The curse overload returns its input: a curse object has no curses of its own, so its
         * weight equals {@code object_weight_one} of it, even with a negative or a 100% weight.
         */
        @Test
        @DisplayName("the curse overload returns its input")
        void curseOverload() throws Exception {
            ItemObject item = cloak(100, 0);

            for (Curse c : List.of(curse(1, -40, false, 0), curse(1, 100, true, 0), curse(1, 200, true, 0))) {
                assertEquals(77, (int) invoke(item, "nonStandardWeightPower",
                        new Class<?>[]{Curse.class, int.class}, c, 77));
            }
        }
    }

    /**
     * C's {@code apply_curse_attributes}: folding the active curses into an object.
     */
    @Nested
    @DisplayName("applyCurseAttributes")
    class ApplyCurseAttributes {

        /**
         * An object with no curses is left exactly as it was.
         */
        @Test
        @DisplayName("an object with no curses is unchanged")
        void noCurses() throws Exception {
            register(curse(1, 30, false, -4));
            ItemObject item = cloak(100, 5);

            applyCurses(item, null);

            assertEquals(100, item.getWeight());
            assertEquals(5, item.getToAC());
        }

        /**
         * Curses apply in registry order whatever order they were laid on the object: +30 then
         * times 1.5 is 195, where the other order would give 180. The curses are laid in reverse.
         */
        @Test
        @DisplayName("weights apply in index order")
        void weightOrder() throws Exception {
            Curse plus = curse(1, 30, false, 0);
            Curse times = curse(2, 150, true, 0);
            register(plus, times);
            ItemObject item = cloak(100, 0, times, 50, plus, 50);

            applyCurses(item, null);

            assertEquals(195, item.getWeight());
        }

        /**
         * The three combat figures add, and a long chain saturates at the 16-bit limits.
         */
        @Test
        @DisplayName("combat bonuses add and saturate")
        void combatBonuses() throws Exception {
            Curse c = curse(1, 0, false, -4);
            register(c);
            ItemObject item = cloak(100, 5, c, 50);

            applyCurses(item, null);
            assertEquals(1, item.getToAC());

            Curse big = curse(1, 0, false, 10);
            register(big);
            ItemObject high = cloak(100, 32760, big, 50);
            applyCurses(high, null);
            assertEquals(32767, high.getToAC());

            Curse low = curse(1, 0, false, -10);
            register(low);
            ItemObject deep = cloak(100, -32760, low, 50);
            applyCurses(deep, null);
            assertEquals(-32768, deep.getToAC());
        }

        /**
         * The ignored curse, an inactive curse and a registered curse the object does not carry
         * are all left out.
         */
        @Test
        @DisplayName("the ignored, inactive and absent curses are skipped")
        void skipped() throws Exception {
            Curse ignored = curse(1, 0, false, -1);
            Curse inactive = curse(2, 0, false, -10);
            Curse absent = curse(3, 0, false, -100);
            Curse applied = curse(4, 0, false, -4);
            register(ignored, inactive, absent, applied);
            ItemObject item = cloak(100, 50, ignored, 50, inactive, 0, applied, 50);

            applyCurses(item, ignored);

            assertEquals(46, item.getToAC());
        }

        /**
         * The curse's flags are unioned into the object's own, if the curse is active.
         */
        @Test
        @DisplayName("an active curse's flags are added")
        void flagsUnion() throws Exception {
            Curse throwing = curse(1, 0, false, 0, ObjectFlag.OF_THROWING);
            Curse inactive = curse(2, 0, false, 0, ObjectFlag.OF_TELEPATHY);
            register(throwing, inactive);
            ItemObject item = cloak(100, 0, throwing, 50, inactive, 0);

            applyCurses(item, null);

            assertTrue(item.getFlags().has(ObjectFlag.OF_THROWING));
            assertFalse(item.getFlags().has(ObjectFlag.OF_TELEPATHY));
        }

        /**
         * Modifiers add; one the object lacks is taken from the curse as it stands; and the sum
         * saturates at the 16-bit limit.
         */
        @Test
        @DisplayName("modifiers add, appear and saturate")
        void modifiers() throws Exception {
            Curse c = curse(1, 0, false, 0);
            c.getModifiers().put(ObjectModifier.OM_STEALTH, 3);
            c.getModifiers().put(ObjectModifier.OM_SPEED, -2);
            c.getModifiers().put(ObjectModifier.OM_STR, 1);
            register(c);
            ItemObject item = cloak(100, 0, c, 50);
            item.getModifiers().put(ObjectModifier.OM_STEALTH, 2);
            item.getModifiers().remove(ObjectModifier.OM_SPEED);
            item.getModifiers().put(ObjectModifier.OM_STR, 32767);

            applyCurses(item, null);

            assertEquals(5, item.getModifiers().get(ObjectModifier.OM_STEALTH));
            assertEquals(-2, item.getModifiers().get(ObjectModifier.OM_SPEED));
            assertEquals(32767, item.getModifiers().get(ObjectModifier.OM_STR));
        }

        /**
         * C's table for combining an object's resistance with a curse's: immune beats all, resist
         * meeting vulnerable becomes both and is flattened to none, and none takes the curse's
         * level.
         *
         * @param object   the object's level for the element
         * @param curse    the curse's level
         * @param expected what the caller sees afterwards
         */
        @ParameterizedTest(name = "object {0} with curse {1} gives {2}")
        @CsvSource({
                "3, -1, 3", "3, 0, 3", "3, 1, 3", "3, 3, 3",
                "1, -1, 0", "1, 0, 1", "1, 1, 1", "1, 3, 3",
                "-1, -1, -1", "-1, 0, -1", "-1, 1, 0", "-1, 3, 3",
                "0, -1, -1", "0, 0, 0", "0, 1, 1", "0, 3, 3"
        })
        void resistanceTable(int object, int curse, int expected) throws Exception {
            Curse c = curse(1, 0, false, 0);
            c.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(curse));
            register(c);
            ItemObject item = cloak(100, 0, c, 50);
            item.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(object));

            applyCurses(item, null);

            assertEquals(expected, item.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());
        }

        /**
         * The both-at-once state survives between curses, so order inside the merge matters: a
         * resistance, a vulnerability, then an immunity ends immune; the same without the immunity
         * ends at none; and a second vulnerability does not undo the first.
         */
        @Test
        @DisplayName("both-at-once persists until an immunity or the end")
        void bothPersists() throws Exception {
            Curse vuln = curse(1, 0, false, 0);
            vuln.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(-1));
            Curse immune = curse(2, 0, false, 0);
            immune.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(3));
            Curse resist = curse(2, 0, false, 0);
            resist.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(1));
            Curse vuln2 = curse(2, 0, false, 0);
            vuln2.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(-1));

            register(vuln, immune);
            ItemObject item = cloak(100, 0, vuln, 50, immune, 50);
            item.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(1));
            applyCurses(item, null);
            assertEquals(3, item.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());

            register(vuln, resist);
            item = cloak(100, 0, vuln, 50, resist, 50);
            item.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(1));
            applyCurses(item, null);
            assertEquals(0, item.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());

            register(vuln, vuln2);
            item = cloak(100, 0, vuln, 50, vuln2, 50);
            item.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(1));
            applyCurses(item, null);
            assertEquals(0, item.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());
        }

        /**
         * An element the object does not mention takes the curse's level, as C's array slot would
         * read zero; one neither mentions stays absent.
         */
        @Test
        @DisplayName("an element the object lacks is created from the curse")
        void absentElement() throws Exception {
            Curse c = curse(1, 0, false, 0);
            c.getElInfo().put(ElementEnum.ELEM_FIRE, withLevel(-1));
            register(c);
            ItemObject item = cloak(100, 0, c, 50);
            item.getElInfo().remove(ElementEnum.ELEM_FIRE);
            item.getElInfo().remove(ElementEnum.ELEM_COLD);

            applyCurses(item, null);

            assertEquals(-1, item.getElInfo().get(ElementEnum.ELEM_FIRE).getResLevel());
            assertFalse(item.getElInfo().containsKey(ElementEnum.ELEM_COLD));
        }

        /**
         * C asserts that a level the merge does not expect is zero; the port throws.
         */
        @Test
        @DisplayName("an impossible resistance level throws where C asserts")
        void impossibleLevel() {
            Curse c = curse(1, 0, false, 0);
            c.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(-1));
            register(c);
            ItemObject item = cloak(100, 0, c, 50);
            item.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(2));

            assertThrows(RuntimeException.class, () -> applyCurses(item, null));
        }

        /**
         * The curse list is left as it was, for the caller to clear, and the base armour class is
         * untouched.
         */
        @Test
        @DisplayName("the curse list and base armour are left alone")
        void leavesCurses() throws Exception {
            Curse c = curse(1, 0, false, -1);
            register(c);
            ItemObject item = cloak(100, 0, c, 50);
            set(item, "baseAC", 7);

            applyCurses(item, null);

            assertEquals(1, item.getCurses().size());
            assertEquals(7, item.getBaseAC());
        }
    }

    /**
     * C's {@code curse_power}: the adjustment to power for an object's curses.
     */
    @Nested
    @DisplayName("cursePower")
    class CursePower {

        /**
         * No curses, no adjustment.
         */
        @Test
        @DisplayName("an uncursed object is unchanged")
        void uncursed() throws Exception {
            assertEquals(10, cursePower(cloak(100, 5), 10));
        }

        /**
         * An ordinary curse with nothing to price costs a tenth of its strength, integer division:
         * 40 is 4, 9 is nothing, and two curses of 35 take 3 each, where one of 70 takes 7.
         */
        @Test
        @DisplayName("an ordinary curse is discounted by a tenth of its strength")
        void ordinaryDiscount() throws Exception {
            Curse a = curse(1, 0, false, 0);
            Curse b = curse(2, 0, false, 0);

            assertEquals(96, cursePower(cloak(100, 0, a, 40), 100));
            assertEquals(100, cursePower(cloak(100, 0, a, 9), 100));
            assertEquals(90, cursePower(cloak(100, 0, a, 100), 100));
            assertEquals(94, cursePower(cloak(100, 0, a, 35, b, 35), 100));
        }

        /**
         * A curse at power zero is not active and is not priced.
         */
        @Test
        @DisplayName("a curse at power zero is ignored")
        void inactive() throws Exception {
            assertEquals(100, cursePower(cloak(100, 0, curse(1, 0, false, 0), 0), 100));
            assertEquals(100, cursePower(cloak(100, 0, curse(1, 30, false, -4), 0), 100));
        }

        /**
         * An ordinary curse is priced by the whole calculation run over it: to_a of -3 is -3
         * power, and the strength of 40 takes 4 more.
         */
        @Test
        @DisplayName("an ordinary curse's own combat bonus is priced")
        void ordinaryPriced() throws Exception {
            assertEquals(93, cursePower(cloak(100, 0, curse(1, 0, false, -3), 40), 100));
        }

        /**
         * A weight-affecting curse is priced by difference, and a negative one is scaled by its
         * strength held between 20 and 100 over 100. With a to_a of -4 the gap is -4: strength 50
         * gives {@code -200 / 100}, which is -2.
         */
        @Test
        @DisplayName("a weight-affecting penalty is scaled by strength")
        void weightPenaltyScaled() throws Exception {
            assertEquals(8, cursePower(cloak(100, 5, curse(1, 30, false, -4), 50), 10));
        }

        /**
         * Strength under 20 is held at 20: {@code -4 * 20 / 100} is {@code -80 / 100}, which
         * truncates to zero. Strength over 100 is held at 100, the whole penalty.
         */
        @Test
        @DisplayName("the strength is held between 20 and 100")
        void strengthClamped() throws Exception {
            // One curse for both items: two at the same index would both be applied
            Curse heavy = curse(1, 30, false, -4);

            assertEquals(10, cursePower(cloak(100, 5, heavy, 10), 10));
            assertEquals(6, cursePower(cloak(100, 5, heavy, 150), 10));
        }

        /**
         * The scaled penalty truncates toward zero: -3 at strength 50 is {@code -150 / 100}, which
         * is -1, where a floor would give -2.
         */
        @Test
        @DisplayName("the scaled penalty truncates toward zero")
        void truncatesTowardZero() throws Exception {
            assertEquals(9, cursePower(cloak(100, 5, curse(1, 30, false, -3), 50), 10));
        }

        /**
         * A gap that is not negative is used as it is, whatever the strength.
         */
        @Test
        @DisplayName("a benefit is not scaled")
        void benefitUnscaled() throws Exception {
            assertEquals(14, cursePower(cloak(100, 5, curse(1, 30, false, 4), 20), 10));
        }

        /**
         * Each weight-affecting curse is priced against all the others: with to_a of -2 and -3 on
         * an item of 5, the gaps are -2 and -3, in full at strength 100.
         */
        @Test
        @DisplayName("two weight-affecting curses are priced separately")
        void twoWeightCurses() throws Exception {
            Curse a = curse(1, 30, false, -2);
            Curse b = curse(2, 150, true, -3);
            register(a, b);

            assertEquals(15, cursePower(cloak(100, 5, a, 100, b, 100), 20));
        }

        /**
         * Both passes run. The ordinary curse takes a tenth of 40 and nothing else; the
         * weight-affecting one takes its gap of -4 in full.
         */
        @Test
        @DisplayName("an ordinary and a weight-affecting curse both count")
        void mixed() throws Exception {
            Curse ordinary = curse(1, 0, false, 0);
            Curse heavy = curse(2, 30, false, -4);
            register(ordinary, heavy);

            assertEquals(92, cursePower(cloak(100, 5, ordinary, 40, heavy, 100), 100));
        }

        /**
         * C applies <em>every</em> curse to the scratch copy, the ordinary ones included, so an
         * ordinary curse's to_a also shows in the weight-affecting curse's gap. Here the ordinary
         * curse takes -1 for itself and 4 for strength 40 (-5), and the heavy curse's gap is 4
         * against a copy already lowered by 1 (-4), for -9.
         */
        @Test
        @DisplayName("an ordinary curse's bonus is in the scratch copies")
        void ordinaryInScratchCopies() throws Exception {
            Curse ordinary = curse(1, 0, false, -1);
            Curse heavy = curse(2, 30, false, -4);
            register(ordinary, heavy);

            assertEquals(91, cursePower(cloak(100, 5, ordinary, 40, heavy, 100), 100));
        }

        /**
         * The pricing works on copies: the object keeps its weight, bonus and curses.
         */
        @Test
        @DisplayName("the object is not changed by being priced")
        void objectUnchanged() throws Exception {
            Curse heavy = curse(1, 30, false, -4);
            register(heavy);
            ItemObject item = cloak(100, 5, heavy, 50);
            item.getElInfo().put(ElementEnum.ELEM_ACID, withLevel(1));

            cursePower(item, 10);

            assertEquals(100, item.getWeight());
            assertEquals(5, item.getToAC());
            assertEquals(1, item.getCurses().size());
            assertEquals(1, item.getElInfo().get(ElementEnum.ELEM_ACID).getResLevel());
        }

        /**
         * The curse overload returns its input: a curse object has no curses of its own.
         */
        @Test
        @DisplayName("the curse overload returns its input")
        void curseOverload() throws Exception {
            assertEquals(77, (int) invoke(cloak(100, 0), "cursePower",
                    new Class<?>[]{Curse.class, int.class, boolean.class, String.class},
                    curse(1, 30, false, -4), 77, false, null));
        }
    }

    /**
     * C's {@code object_power}: the driver.
     */
    @Nested
    @DisplayName("objectPower")
    class ObjectPower {

        /**
         * A plain cloak is worth its {@code to_a}.
         */
        @Test
        @DisplayName("a plain cloak is worth its to_a")
        void plainCloak() throws Exception {
            assertEquals(5, objectPower(cloak(100, 5)));
        }

        /**
         * The curse step runs and takes a tenth of the strength.
         */
        @Test
        @DisplayName("the curse step runs")
        void curseStep() throws Exception {
            assertEquals(1, objectPower(cloak(100, 5, curse(1, 0, false, 0), 40)));
        }

        /**
         * The weight step runs last: a weight curse with no other effect has a gap of nothing in
         * the curse step, but leaves the object 50 heavier than standard, so {@code -50 / 50} is -1.
         */
        @Test
        @DisplayName("the weight step runs")
        void weightStep() throws Exception {
            Curse heavy = curse(1, 50, false, 0);
            register(heavy);

            assertEquals(4, objectPower(cloak(100, 5, heavy, 50)));
        }

        /**
         * Three extra blows give {@code INHIBIT_POWER} before the curse and weight steps are
         * reached, so the result is the same with a curse as without, and is above the limit.
         */
        @Test
        @DisplayName("an inhibited object returns before the curse step")
        void inhibitedReturnsEarly() throws Exception {
            Curse c = curse(1, 0, false, 0);
            ItemObject plain = cloak(100, 0);
            plain.getModifiers().put(ObjectModifier.OM_BLOWS, 3);
            ItemObject cursed = cloak(100, 0, c, 100);
            cursed.getModifiers().put(ObjectModifier.OM_BLOWS, 3);

            assertEquals(20060, objectPower(plain));
            assertEquals(20060, objectPower(cursed));
        }

        /**
         * The curse overload mirrors the item one: a curse of to_a -3 is worth -3, an empty one 0.
         */
        @Test
        @DisplayName("the curse overload prices a curse")
        void curseOverload() throws Exception {
            ItemObject item = cloak(100, 0);
            Class<?>[] types = {Curse.class, boolean.class, String.class};

            assertEquals(-3, (int) invoke(item, "objectPower", types, curse(1, 0, false, -3), false, null));
            assertEquals(0, (int) invoke(item, "objectPower", types, curse(1, 0, false, 0), false, null));
        }
    }

    /**
     * The three {@code free} methods: they clear a scratch copy and leave the original alone.
     */
    @Nested
    @DisplayName("freeSlays, freeBrands and freeCurses")
    class Free {

        /**
         * Each empties its collection on the copy; the original keeps its own.
         */
        @Test
        @DisplayName("each empties the copy and not the original")
        @SuppressWarnings("unchecked")
        void emptiesCopyOnly() throws Exception {
            Curse c = curse(1, 0, false, 0);
            ItemObject item = cloak(100, 0, c, 50);
            Brand brand = new Brand("FIRE_3", "fire", "burn", null, null, 3, 2, 1);
            Slay slay = new Slay("EVIL_2", "evil", null, "slay", "slay", null, 2, 1, 1);
            Set<Brand> brands = new HashSet<>(Set.of(brand));
            Set<Slay> slays = new HashSet<>(Set.of(slay));
            set(item, "brands", brands);
            set(item, "slays", slays);

            ItemObject copy = item.copy(true);
            assertEquals(1, ((Set<Brand>) read(copy, "brands")).size());
            assertEquals(1, ((Set<Slay>) read(copy, "slays")).size());
            assertEquals(1, copy.getCurses().size());

            invoke(copy, "freeBrands", new Class<?>[0]);
            invoke(copy, "freeSlays", new Class<?>[0]);
            invoke(copy, "freeCurses", new Class<?>[0]);

            assertNotNull(read(copy, "brands"));
            assertTrue(((Set<Brand>) read(copy, "brands")).isEmpty());
            assertTrue(((Set<Slay>) read(copy, "slays")).isEmpty());
            assertTrue(copy.getCurses().isEmpty());
            assertEquals(1, ((Set<Brand>) read(item, "brands")).size());
            assertEquals(1, ((Set<Slay>) read(item, "slays")).size());
            assertEquals(1, item.getCurses().size());
        }
    }
}
