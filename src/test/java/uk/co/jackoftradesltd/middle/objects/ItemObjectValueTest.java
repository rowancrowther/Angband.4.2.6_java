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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.frontend.ui.entry.reader.UIEntryReader;
import uk.co.jackoftradesltd.frontend.ui.entrybase.reader.UIEntryBaseReader;
import uk.co.jackoftradesltd.frontend.ui.entryrenderer.reader.UIEntryRendererReader;
import uk.co.jackoftradesltd.frontend.ui.globals.UIRegistry;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.read;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the three pricing methods of {@link ItemObject} - {@code objectValue},
 * {@code objectValueBase} and {@link ItemObject#objectValueReal(int)} - the ports of C's
 * {@code object_value}, {@code object_value_base} and {@code object_value_real}
 * ({@code obj-power.c}).
 *
 * <p>Every expected figure is worked out from the C source and the constants in
 * {@code obj-power.h}, not read back from the port. The fixed-price figures need nothing but the
 * kind's cost and the charge arithmetic. The variable-power figures need a power, and the power
 * calculation is the subject of later batches, so the objects here are chosen to make it short: a
 * plain cloak or arrow has only the {@code to_a} term, whose value is {@code to_a} for
 * {@code to_a} up to 26, and an object with three extra blows gets {@code INHIBIT_POWER} of 20000
 * from the blows step. A ring or light also gets 60 for the non-weapon combat bonus, so passes
 * 20000 and takes the early return; an arrow has no such bonus, stays at exactly 20000, which is
 * not above the limit, and carries on to the modifier step, where its 150 of ability total adds
 * {@code ability_power[15]}, which is 30.
 *
 * <p>Three behaviours are worth the most. The routing in {@code objectValue} decides what the player
 * is entitled to be told, so each of its three routes and the order they are tried in are pinned.
 * The expendable rescale truncates toward zero and is followed by the lift-zero-to-one rule, so a
 * small positive or negative ammunition power both price at one gold. And a cursed stack's price is
 * floored at zero rather than going negative.
 *
 * <p>The overflow clamps in {@code object_value_real} are not tested. They apply only once the
 * power passes about 46,000, where {@code power * (power + 5)} no longer fits an {@code int}, and
 * the largest power reachable through a short path is about 20000, priced at 402,503,900.
 *
 * <p>The registries the power calculation reads are seeded here and put back afterwards, so the
 * class passes alone and leaves nothing behind for another suite.
 *
 * <p>Class ItemObjectValueTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectValueTest {

    /**
     * The price of one non-weapon, non-ammunition object at power 20060: three extra blows give
     * {@code INHIBIT_POWER} of 20000, and 60 is {@code WEAP_DAMAGE * DAMAGE_POWER} for a
     * non-weapon with a combat modifier. {@code 20060 * (20060 * 1 + 5)}.
     */
    private static final int PRICE_AT_20060 = 402_503_900;

    /**
     * The price of one arrow at power 20030, before the rescale. Ammunition has no 60, because it
     * takes its damage from dice, but its three blows count 150 towards the ability total, which
     * is worth {@code ability_power[15]} of 30. {@code 20030 * (20030 * 1 + 5)}.
     */
    private static final int PRICE_AT_20030 = 401_301_050;

    /**
     * The registry contents the class replaced, in the order {@link #seedRegistries()} saved them.
     */
    private static Object savedRenderers;
    private static Object savedUiBases;
    private static Object savedUiEntries;
    private static Object savedProperties;
    private static Object savedFlagSets;
    private static Object savedElementSets;
    private static Object savedElementPowers;
    private static Object savedArchery;

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

        // The three rows of C's flag_sets[]. An object with no flags counts nothing in any of them.
        Map<ObjectFlagType, FlagSet> flagSets = new HashMap<>();
        flagSets.put(ObjectFlagType.OFT_SUST, new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains"));
        flagSets.put(ObjectFlagType.OFT_PROT, new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections"));
        flagSets.put(ObjectFlagType.OFT_MISC, new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities"));
        savedFlagSets = ItemFixture.setStatic(ObjectRegistry.class, "flagSets", flagSets);

        // No element rows: an object whose elements are all neutral adds nothing, so none are needed.
        savedElementSets = ItemFixture.setStatic(ObjectRegistry.class, "elementSets", new ArrayList<ElementSet>());
        savedElementPowers = ItemFixture.setStatic(ObjectRegistry.class, "elementPowers",
                new ArrayList<ElementPowers>());

        // The arrow row of C's archery[], read by the ammunition step of the power calculation.
        Map<TValue, Archery> archery = new HashMap<>();
        archery.put(TValue.TV_ARROW, new Archery(TValue.TV_ARROW, 12, 9, 5));
        savedArchery = ItemFixture.setStatic(ObjectRegistry.class, "archery", archery);
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
        ItemFixture.setStatic(ObjectRegistry.class, "archery", savedArchery);
    }

    /**
     * A kind of the given type with a listed cost, and whether the player has learned it.
     *
     * @param tValue the kind's type
     * @param cost   the kind's listed cost, C's {@code kind->cost}
     * @param aware  whether the player knows what the kind is
     * @return the kind
     */
    private static ObjectKind kind(TValue tValue, int cost, boolean aware) {
        ObjectKind kind = ItemFixture.loadedKind(tValue, "test", 40);
        set(kind, "cost", cost);
        set(kind, "aware", aware);
        return kind;
    }

    /**
     * An item of the given kind and stack size, with no known half.
     *
     * @param kind   the kind
     * @param number the stack size
     * @return the item
     */
    private static ItemObject item(ObjectKind kind, int number) {
        return ItemFixture.item(kind.getBase().gettVal()).kind(kind).number(number).build();
    }

    /**
     * Gives an item three extra blows, which makes its power 20000 plus whatever the damage terms
     * before it add.
     *
     * @param item the item
     * @return the same item
     */
    @SuppressWarnings("unchecked")
    private static ItemObject withThreeBlows(ItemObject item) {
        ((Map<ObjectModifier, Integer>) read(item, "modifiers")).put(ObjectModifier.OM_BLOWS, 3);
        return item;
    }

    /**
     * Calls one of the private pricing methods.
     *
     * @param item the object to price
     * @param name the method's name
     * @param args the argument values, as {@code int}s
     * @return the price
     */
    private static int call(ItemObject item, String name, int... args) throws Exception {
        Class<?>[] types = new Class<?>[args.length];
        Object[] values = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = int.class;
            values[i] = args[i];
        }
        Method method = ItemObject.class.getDeclaredMethod(name, types);
        method.setAccessible(true);
        try {
            return (int) method.invoke(item, values);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        }
    }

    /**
     * Installs a player with a body, which the power calculation needs for its slot comparisons. It
     * is installed before any item is built, because an item captures the player at construction.
     */
    @BeforeEach
    void installPlayer() {
        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());
    }

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    /**
     * The flat figure for an object the player cannot identify: C's {@code object_value_base}.
     */
    @Nested
    @DisplayName("objectValueBase")
    class Base {

        /**
         * Each listed type has its own figure, and the kind's cost, set to 999 here, plays no part.
         * An unlisted type is worth nothing, including every wearable that is not jewellery.
         *
         * @param tValue   the object's type
         * @param expected the figure in C's switch
         */
        @ParameterizedTest(name = "{0} unaware is worth {1}")
        @CsvSource({
                "TV_FOOD, 5", "TV_MUSHROOM, 5", "TV_POTION, 20", "TV_SCROLL, 20",
                "TV_RING, 45", "TV_AMULET, 45", "TV_WAND, 50", "TV_STAFF, 70", "TV_ROD, 90",
                "TV_SWORD, 0", "TV_ARROW, 0", "TV_CLOAK, 0", "TV_LIGHT, 0"
        })
        void unawareFiguresByType(TValue tValue, int expected) throws Exception {
            ItemObject item = item(kind(tValue, 999, false), 1);

            assertEquals(expected, call(item, "objectValueBase"));
        }

        /**
         * An aware object is worth its kind's cost, whatever its type - even a type the switch
         * does not list.
         */
        @Test
        @DisplayName("an aware object is worth its kind's cost")
        void awareIsKindCost() throws Exception {
            assertEquals(300, call(item(kind(TValue.TV_SWORD, 300, true), 1), "objectValueBase"));
            assertEquals(12, call(item(kind(TValue.TV_POTION, 12, true), 1), "objectValueBase"));
        }
    }

    /**
     * The price from what the object can do: C's {@code object_value_real}, fixed-price route.
     */
    @Nested
    @DisplayName("objectValueReal, fixed-price types")
    class FixedPrice {

        /**
         * A type with neither variable power nor charges is worth its cost for each item.
         */
        @Test
        @DisplayName("cost times quantity")
        void costTimesQuantity() {
            assertEquals(40, item(kind(TValue.TV_POTION, 10, true), 4).objectValueReal(4));
            assertEquals(10, item(kind(TValue.TV_POTION, 10, true), 4).objectValueReal(1));
        }

        /**
         * A rod has a timeout rather than charges, so its pval adds nothing.
         */
        @Test
        @DisplayName("a rod is not charged for a pval")
        void rodHasNoChargeSurcharge() {
            ItemObject rod = item(kind(TValue.TV_ROD, 100, true), 1);
            set(rod, "pValue", 8);

            assertEquals(100, rod.objectValueReal(1));
        }

        /**
         * A wand adds one twentieth of its cost for each charge. Two wands sharing sixteen charges
         * hold eight each, and a quantity of two prices all sixteen: {@code 200 + 100 * 8 / 20}
         * is 240 for the stack of two, taken as a whole.
         */
        @Test
        @DisplayName("a wand pays a twentieth of its cost per charge")
        void wandChargeSurcharge() {
            ItemObject wands = item(kind(TValue.TV_WAND, 100, true), 2);
            set(wands, "pValue", 8);

            // charges = 8 * 2 / 2 = 8, exactly: 200 + 100 * 8 / 20
            assertEquals(240, wands.objectValueReal(2));
            // charges = 8 * 1 / 2 = 4, exactly: 100 + 100 * 4 / 20
            assertEquals(120, wands.objectValueReal(1));
        }

        /**
         * A staff is charged the same way as a wand.
         */
        @Test
        @DisplayName("a staff pays the same surcharge")
        void staffChargeSurcharge() {
            ItemObject staff = item(kind(TValue.TV_STAFF, 200, true), 1);
            set(staff, "pValue", 5);

            // charges = 5, 200 + 200 * 5 / 20
            assertEquals(250, staff.objectValueReal(1));
        }

        /**
         * The charge count rounds up when the share is not whole: seven charges over two wands is
         * three and a half for one wand, which C takes as four. Rounding down would give 115.
         */
        @Test
        @DisplayName("an inexact share of charges rounds up")
        void chargesRoundUp() {
            ItemObject wands = item(kind(TValue.TV_WAND, 100, true), 2);
            set(wands, "pValue", 7);

            // charges = 7 * 1 / 2 = 3 remainder 1, so 4: 100 + 100 * 4 / 20
            assertEquals(120, wands.objectValueReal(1));
            // charges = 7 * 2 / 2 = 7, exactly: 200 + 100 * 7 / 20
            assertEquals(235, wands.objectValueReal(2));
        }

        /**
         * The surcharge is an integer division: a cost of 30 with one charge adds 30 / 20, which is
         * one.
         */
        @Test
        @DisplayName("the surcharge truncates")
        void surchargeTruncates() {
            ItemObject wand = item(kind(TValue.TV_WAND, 30, true), 1);
            set(wand, "pValue", 1);

            assertEquals(31, wand.objectValueReal(1));
        }

        /**
         * A kind that costs nothing is worth nothing, charges or not.
         */
        @Test
        @DisplayName("a free kind is worth nothing")
        void freeKind() {
            ItemObject wand = item(kind(TValue.TV_WAND, 0, true), 1);
            set(wand, "pValue", 10);

            assertEquals(0, wand.objectValueReal(1));
        }

        /**
         * A negative cost is not an error but never prices below nothing.
         */
        @Test
        @DisplayName("a negative cost is floored at zero")
        void negativeCostFloored() {
            assertEquals(0, item(kind(TValue.TV_POTION, -10, true), 2).objectValueReal(2));
        }
    }

    /**
     * The price from the object's power: C's {@code object_value_real}, variable-power route.
     */
    @Nested
    @DisplayName("objectValueReal, variable-power types")
    class VariablePower {

        /**
         * A cloak with {@code to_a} of 5 has power 5, so the price is {@code 5 * (5 * 1 + 5)}.
         */
        @Test
        @DisplayName("price is power times (power + 5)")
        void quadraticCurve() {
            ItemObject cloak = item(kind(TValue.TV_CLOAK, 1, true), 1);
            set(cloak, "toAC", 5);

            assertEquals(50, cloak.objectValueReal(1));
            assertEquals(150, cloak.objectValueReal(3));
        }

        /**
         * Above {@code HIGH_TO_AC} of 26 the to-AC term adds a second lot: a bonus of 26 is power
         * 26 and prices at {@code 26 * 31}, but 27 adds {@code (27 - 25) * 2}, making power 31 and a
         * price of {@code 31 * 36}.
         */
        @Test
        @DisplayName("the high to-AC threshold steepens the curve")
        void highToAc() {
            ItemObject at26 = item(kind(TValue.TV_CLOAK, 1, true), 1);
            set(at26, "toAC", 26);
            ItemObject at27 = item(kind(TValue.TV_CLOAK, 1, true), 1);
            set(at27, "toAC", 27);

            assertEquals(806, at26.objectValueReal(1));
            assertEquals(1116, at27.objectValueReal(1));
        }

        /**
         * An object with no power is lifted to one gold, so a plain cloak is not worthless, and the
         * lift applies to each item before the quantity multiplies.
         */
        @Test
        @DisplayName("zero power is lifted to one")
        void zeroLiftedToOne() {
            ItemObject cloak = item(kind(TValue.TV_CLOAK, 1, true), 3);

            assertEquals(1, cloak.objectValueReal(1));
            assertEquals(3, cloak.objectValueReal(3));
        }

        /**
         * A cursed object has negative power, whose mirrored curve gives a negative price for one
         * item; the stack total is floored at zero, so it is never worth less than nothing.
         */
        @Test
        @DisplayName("negative power is floored at zero for the stack")
        void negativePowerFloored() {
            ItemObject cloak = item(kind(TValue.TV_CLOAK, 1, true), 2);
            set(cloak, "toAC", -5);

            // value = -(-5) * (-5 * 1 - 5) = -50
            assertEquals(0, cloak.objectValueReal(1));
            assertEquals(0, cloak.objectValueReal(2));
        }

        /**
         * Ammunition is divided by {@code AMMO_RESCALER} of 20: power 10 is 150, which is 7 after
         * truncation, and two arrows are 14.
         */
        @Test
        @DisplayName("ammunition is divided by twenty")
        void ammunitionRescaled() {
            ItemObject arrows = item(kind(TValue.TV_ARROW, 1, true), 2);
            set(arrows, "toAC", 10);

            assertEquals(7, arrows.objectValueReal(1));
            assertEquals(14, arrows.objectValueReal(2));
        }

        /**
         * A rescaled price that truncates to zero is then lifted to one, whichever side of zero it
         * came from. Power 1 is 6 and power -1 is -6, and both divide to zero.
         *
         * @param toAC the arrow's to-AC bonus
         */
        @ParameterizedTest(name = "an arrow with to_a {0} is worth one gold each")
        @CsvSource({"1", "-1"})
        void smallAmmunitionLiftedToOne(int toAC) {
            ItemObject arrows = item(kind(TValue.TV_ARROW, 1, true), 4);
            set(arrows, "toAC", toAC);

            assertEquals(1, arrows.objectValueReal(1));
            assertEquals(4, arrows.objectValueReal(4));
        }

        /**
         * A negative rescaled price that does not truncate to zero is left alone by the lift, and
         * the stack total is floored. Power -5 is -50, which is -2 after division.
         */
        @Test
        @DisplayName("a negative rescaled price is not lifted")
        void negativeAmmunitionFloored() {
            ItemObject arrows = item(kind(TValue.TV_ARROW, 1, true), 3);
            set(arrows, "toAC", -5);

            assertEquals(0, arrows.objectValueReal(3));
        }

        /**
         * The {@code INHIBIT_POWER} early return keeps a large power exactly: three extra blows on
         * a ring give 20060, and the price is the curve without any clamp. The ring is not ammunition
         * or a burning light, so it is not rescaled.
         */
        @Test
        @DisplayName("a very high power prices without clamping")
        void inhibitedPower() {
            ItemObject ring = withThreeBlows(item(kind(TValue.TV_RING, 1, true), 1));

            assertEquals(PRICE_AT_20060, ring.objectValueReal(1));
            assertEquals(2 * PRICE_AT_20060, ring.objectValueReal(2));
        }

        /**
         * A light that burns out and has no ego is an expendable and is divided by twenty:
         * {@code 402,503,900 / 20}.
         */
        @Test
        @DisplayName("a burning light is rescaled")
        void burningLightRescaled() {
            ItemObject torch = ItemFixture.item(TValue.TV_LIGHT)
                    .kind(kind(TValue.TV_LIGHT, 1, true)).flags(ObjectFlag.OF_BURNS_OUT).build();
            withThreeBlows(torch);

            assertEquals(PRICE_AT_20060 / 20, torch.objectValueReal(1));
        }

        /**
         * A burning light with an ego is not rescaled, and nor is a light that does not burn out.
         */
        @Test
        @DisplayName("an ego light and a permanent light are not rescaled")
        void otherLightsNotRescaled() {
            EgoItem ego = new EgoItem("of Testing", "a test ego", 1, 0,
                    new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                    new Flag<>(ObjectKindFlag.class), new HashMap<>(), new HashMap<>(),
                    new HashMap<>(), new HashSet<>(), new HashSet<>(), new HashMap<>(),
                    0, 0, 0, 0, new ArrayList<>(), null, null, null, 0, 0, 0,
                    null, null, false);
            ItemObject egoTorch = ItemFixture.item(TValue.TV_LIGHT)
                    .kind(kind(TValue.TV_LIGHT, 1, true)).flags(ObjectFlag.OF_BURNS_OUT).ego(ego).build();
            ItemObject lantern = ItemFixture.item(TValue.TV_LIGHT)
                    .kind(kind(TValue.TV_LIGHT, 1, true)).build();

            assertEquals(PRICE_AT_20060, withThreeBlows(egoTorch).objectValueReal(1));
            assertEquals(PRICE_AT_20060, withThreeBlows(lantern).objectValueReal(1));
        }

        /**
         * The ammunition rescale applies to a very high power: {@code 401,301,050 / 20}, which
         * truncates to 20,065,052, then times the quantity. The division is per item, so three
         * arrows are three times the truncated figure and not a third of the stack total.
         */
        @Test
        @DisplayName("a very high power arrow is rescaled per item")
        void inhibitedArrowRescaled() {
            ItemObject arrows = withThreeBlows(item(kind(TValue.TV_ARROW, 1, true), 3));

            assertEquals(20_065_052, arrows.objectValueReal(1));
            assertEquals(3 * 20_065_052, arrows.objectValueReal(3));
            assertEquals(PRICE_AT_20030 / 20, arrows.objectValueReal(1));
        }
    }

    /**
     * The price the player sees: C's {@code object_value}, and the order of its three routes.
     */
    @Nested
    @DisplayName("objectValue routing")
    class Routing {

        /**
         * A variable-power object with a known half is priced from the known half, never from the
         * real one. The real ring is plain and the known ring has the blows, so a price of
         * 402,503,900 can only have come from the known half.
         */
        @Test
        @DisplayName("variable power is priced from the known half")
        void knownHalfIsUsed() throws Exception {
            ItemObject known = withThreeBlows(item(kind(TValue.TV_RING, 1, false), 1));
            ItemObject ring = ItemFixture.item(TValue.TV_RING).kind(kind(TValue.TV_RING, 1, false))
                    .known(known).build();

            assertEquals(PRICE_AT_20060, call(ring, "objectValue", 1));
        }

        /**
         * That route is taken whether or not the flavour is known: an unaware ring with a known
         * half is still priced by power, not by the flat 45.
         */
        @Test
        @DisplayName("the known-half route does not need an aware flavour")
        void knownHalfIgnoresAwareness() throws Exception {
            ItemObject known = withThreeBlows(item(kind(TValue.TV_RING, 1, false), 1));
            ItemObject ring = ItemFixture.item(TValue.TV_RING).kind(kind(TValue.TV_RING, 1, false))
                    .known(known).build();

            assertEquals(PRICE_AT_20060, call(ring, "objectValue", 1));
            assertEquals(2 * PRICE_AT_20060, call(ring, "objectValue", 2));
        }

        /**
         * A ring the player has never seen, so with no known half, takes the second route if its
         * flavour is known: it is priced by its real power. The price here comes from the real
         * object, which carries the blows.
         */
        @Test
        @DisplayName("an aware flavoured object without a known half is priced in full")
        void awareFlavourPricedInFull() throws Exception {
            ItemObject ring = withThreeBlows(item(kind(TValue.TV_RING, 45, true), 1));

            assertEquals(PRICE_AT_20060, call(ring, "objectValue", 1));
        }

        /**
         * The same ring with an unknown flavour falls to the base route and is worth 45 for each,
         * whatever its kind's listed cost and whatever its real power.
         */
        @Test
        @DisplayName("an unaware flavoured object without a known half gets the flat figure")
        void unawareFlavourGetsBase() throws Exception {
            ItemObject ring = withThreeBlows(item(kind(TValue.TV_RING, 999, false), 3));

            assertEquals(45, call(ring, "objectValue", 1));
            assertEquals(135, call(ring, "objectValue", 3));
        }

        /**
         * A flavoured object that is not variable-power ignores its known half altogether: an
         * unaware potion is worth 20 each.
         */
        @Test
        @DisplayName("a fixed-price type ignores a known half")
        void fixedPriceIgnoresKnownHalf() throws Exception {
            ItemObject known = item(kind(TValue.TV_POTION, 999, false), 1);
            ItemObject potion = ItemFixture.item(TValue.TV_POTION).kind(kind(TValue.TV_POTION, 999, false))
                    .number(2).known(known).build();

            assertEquals(40, call(potion, "objectValue", 2));
        }

        /**
         * An aware potion takes the second route and is worth its cost for each.
         */
        @Test
        @DisplayName("an aware potion is worth its cost")
        void awarePotion() throws Exception {
            assertEquals(30, call(item(kind(TValue.TV_POTION, 10, true), 3), "objectValue", 3));
        }

        /**
         * A wearable that has not been seen, with no known half and no flavour, falls to the base
         * route when it is unaware and so is worth nothing; the player is not told what an unseen
         * sword might fetch.
         */
        @Test
        @DisplayName("an unseen sword is worth nothing")
        void unseenSwordWorthless() throws Exception {
            assertEquals(0, call(item(kind(TValue.TV_SWORD, 300, false), 1), "objectValue", 1));
        }

        /**
         * An aware wearable with no known half also falls to the base route, which answers the
         * kind's cost for an aware object, multiplied by the quantity.
         */
        @Test
        @DisplayName("an aware sword with no known half is worth its kind's cost")
        void awareSwordWithoutKnownHalf() throws Exception {
            assertEquals(300, call(item(kind(TValue.TV_SWORD, 300, true), 1), "objectValue", 1));
            assertEquals(600, call(item(kind(TValue.TV_SWORD, 300, true), 2), "objectValue", 2));
        }

        /**
         * The quantity asked about, not the stack's own number, scales the answer.
         */
        @Test
        @DisplayName("the quantity argument scales the price")
        void quantityArgument() throws Exception {
            ItemObject scrolls = item(kind(TValue.TV_SCROLL, 999, false), 10);

            assertEquals(20, call(scrolls, "objectValue", 1));
            assertEquals(100, call(scrolls, "objectValue", 5));
        }
    }
}
