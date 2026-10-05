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
import uk.co.jackoftradesltd.middle.Activation;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the property components of the power calculation in {@link ItemObject}: {@code effectsPower},
 * {@code elementPower}, {@code flagsPower}, {@code modifierPower}, {@code jewelleryPower},
 * {@code toAcPower}, {@code acPower}, {@code toHitPower} and {@code rescaleBowPower}, each with its
 * plain and {@link Curse} overload. They are the ports of {@code effects_power},
 * {@code element_power}, {@code flags_power}, {@code modifier_power}, {@code jewelry_power},
 * {@code to_ac_power}, {@code ac_power}, {@code to_hit_power} and {@code rescale_bow_power}
 * ({@code obj-power.c}).
 *
 * <p>Every expected figure is worked out by hand from the C source, the constants in
 * {@code obj-power.h} and the figures in {@code lib/gamedata/object_property.txt}, not read back from
 * the port. The comment on each case shows the arithmetic, because the point of several is that C
 * truncates a division, or scores a boundary one way and not the other, and the number alone would
 * hide that.
 *
 * <p>The boundaries worth the most are these. The to-armour bands switch at 26, 36 and 56, where C
 * tests {@code >} for the first two and {@code >=} for the last. The modifier total buys nothing under
 * 70 and is refused above 249, so 69, 70, 249 and 250 are all worth a case. A table index needs a
 * division by ten. {@code ac_power} divides twice, and the second division is what makes a light
 * armour of 3 at weight 70 worth nothing but the flat bonus. And the same flag is priced twice over
 * on a cloak that it is once over on a sword, which only the type multiplier explains.
 *
 * <p>The registries the power calculation reads are seeded here, with the element and flag tables
 * built from the figures in C, and put back afterwards so the class passes alone and leaves nothing
 * behind for another suite.
 *
 * <p>Class ItemObjectPropertyPowerTest coded on 261002, commented in full on 261002.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectPropertyPowerTest {

    private static Object savedRenderers;
    private static Object savedUiBases;
    private static Object savedUiEntries;
    private static Object savedProperties;
    private static Object savedFlagSets;
    private static Object savedElementSets;
    private static Object savedElementPowers;

    /**
     * The player the game held before each test.
     */
    private Player savedPlayer;

    /**
     * Loads the real object-property data and installs the three power tables with the figures C
     * gives them.
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

        // C's flag_sets[]: { type, factor, bonus, size }
        Map<ObjectFlagType, FlagSet> flagSets = new HashMap<>();
        flagSets.put(ObjectFlagType.OFT_SUST, new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains"));
        flagSets.put(ObjectFlagType.OFT_PROT, new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections"));
        flagSets.put(ObjectFlagType.OFT_MISC, new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities"));
        savedFlagSets = ItemFixture.setStatic(ObjectRegistry.class, "flagSets", flagSets);

        // C's element_sets[]: { type, res_level, factor, bonus, size }
        List<ElementSet> elementSets = new ArrayList<>();
        elementSets.add(new ElementSet(ResType.T_LRES, 3, 6, ObjectRegistry.INHIBIT_POWER, 4, 0, "immunities"));
        elementSets.add(new ElementSet(ResType.T_LRES, 1, 1, 10, 4, 0, "low resists"));
        elementSets.add(new ElementSet(ResType.T_HRES, 1, 2, 10, 9, 0, "high resists"));
        savedElementSets = ItemFixture.setStatic(ObjectRegistry.class, "elementSets", elementSets);

        // C's el_powers[]: { name, type, ignore, vuln, res, im }
        List<ElementPowers> elementPowers = new ArrayList<>();
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_ACID, "acid", ResType.T_LRES, 3, -6, 5, 38));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_ELEC, "electricity", ResType.T_LRES, 1, -6, 6, 35));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_FIRE, "fire", ResType.T_LRES, 3, -6, 6, 40));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_COLD, "cold", ResType.T_LRES, 1, -6, 6, 37));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_POIS, "poison", ResType.T_HRES, 0, 0, 28, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_LIGHT, "light", ResType.T_HRES, 0, 0, 6, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_DARK, "dark", ResType.T_HRES, 0, 0, 16, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_SOUND, "sound", ResType.T_HRES, 0, 0, 14, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_SHARD, "shards", ResType.T_HRES, 0, 0, 8, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_NEXUS, "nexus", ResType.T_HRES, 0, 0, 15, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_NETHER, "nether", ResType.T_HRES, 0, 0, 20, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_CHAOS, "chaos", ResType.T_HRES, 0, 0, 20, 0));
        elementPowers.add(new ElementPowers(ElementEnum.ELEM_DISEN, "disenchantment", ResType.T_HRES, 0, 0, 20, 0));
        savedElementPowers = ItemFixture.setStatic(ObjectRegistry.class, "elementPowers", elementPowers);
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

    /**
     * Prices an item with one of the plain, {@code int}-only components.
     *
     * @param item  the item
     * @param name  the component, for example {@code "toAcPower"}
     * @param power the running total handed in
     * @return the running total handed back
     */
    private static int price(ItemObject item, String name, int power) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{int.class}, power);
    }

    /**
     * Prices a curse with one of the {@link Curse} overloads of the components.
     *
     * @param item  any item; the curse overloads do not read it
     * @param name  the component
     * @param curse the curse
     * @param power the running total handed in
     * @return the running total handed back
     */
    private static int price(ItemObject item, String name, Curse curse, int power) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{Curse.class, int.class}, curse, power);
    }

    /**
     * A bare item of the given type, with every modifier at zero and every element plain.
     *
     * @param tValue the item's type
     * @return the item
     */
    private static ItemObject item(TValue tValue) {
        return ItemFixture.item(tValue).build();
    }

    /**
     * A bare item of the given type carrying the given flags.
     *
     * @param tValue the item's type
     * @param flags  the flags it carries
     * @return the item
     */
    private static ItemObject itemWith(TValue tValue, ObjectFlag... flags) {
        return ItemFixture.item(tValue).flags(flags).build();
    }

    /**
     * A curse with the given combat figures and nothing else.
     *
     * @param toHit C's {@code to_h}
     * @param toAC  C's {@code to_a}
     * @return the curse
     */
    private static Curse combatCurse(int toHit, int toAC) {
        return new Curse("test curse", List.of(), new ItemObject(), 0, null, new Flag<>(ObjectFlag.class),
                new HashMap<>(), new HashMap<>(), toHit, 0, toAC, List.of(),
                new Flag<>(ObjectFlag.class), "", "", 1);
    }

    /**
     * Sets how an item stands to an element.
     *
     * @param item    the item
     * @param element the element
     * @param level   the resistance level, -1 to 3
     */
    private static void resist(ItemObject item, ElementEnum element, int level) {
        item.getElInfo().get(element).setResLevel(level);
    }

    /**
     * Installs a player with a body, which the bow test needs for its slot comparison. The player is
     * installed before any item is built, because an item captures the player at construction.
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
     * C's {@code effects_power}: the activation's power, or failing that the kind's.
     */
    @Nested
    @DisplayName("effectsPower")
    class Effects {

        private ItemObject withKindPower(int kindPower) {
            ObjectKind kind = new ObjectKind();
            set(kind, "power", kindPower);
            return ItemFixture.item(TValue.TV_WAND).kind(kind).build();
        }

        private List<Activation> activations(int power) {
            List<Activation> list = new ArrayList<>();
            list.add(new Activation("test", 1, false, 1, power, new ArrayList<>(), "", ""));
            return list;
        }

        /**
         * An activation of 7 is added; the kind's 3 is ignored, because the kind is only the fallback
         * ({@code else if}). 100 + 7.
         */
        @Test
        @DisplayName("an activation is priced and the kind's power is not added as well")
        void activationWins() throws Exception {
            ItemObject item = withKindPower(3);
            set(item, "activation", activations(7));

            assertEquals(107, price(item, "effectsPower", 100));
        }

        /**
         * With no activation the kind's 3 stands in. 100 + 3.
         */
        @Test
        @DisplayName("with no activation the kind's power stands in")
        void kindFallback() throws Exception {
            assertEquals(103, price(withKindPower(3), "effectsPower", 100));
        }

        /**
         * An item built by the no-argument constructor never has its activation assigned, and C's
         * pointer would be null. It must price from the kind, not throw.
         */
        @Test
        @DisplayName("a null activation list prices from the kind")
        void nullActivation() throws Exception {
            ItemObject item = withKindPower(3);
            set(item, "activation", null);

            assertEquals(103, price(item, "effectsPower", 100));
        }

        /**
         * A list whose only entry is null is C's null pointer too; the kind's 3 is used.
         */
        @Test
        @DisplayName("a list holding a null entry prices from the kind")
        void nullEntry() throws Exception {
            ItemObject item = withKindPower(3);
            List<Activation> list = new ArrayList<>();
            list.add(null);
            set(item, "activation", list);

            assertEquals(103, price(item, "effectsPower", 100));
        }

        /**
         * Only the first entry is the activation, as C's single pointer; a second of 50 is ignored.
         */
        @Test
        @DisplayName("only the first activation is priced")
        void firstOnly() throws Exception {
            ItemObject item = withKindPower(0);
            List<Activation> list = activations(7);
            list.addAll(activations(50));
            set(item, "activation", list);

            assertEquals(107, price(item, "effectsPower", 100));
        }

        /**
         * Neither source has any power, so the total comes back as it went in.
         */
        @Test
        @DisplayName("no activation and no kind power adds nothing")
        void nothing() throws Exception {
            assertEquals(100, price(withKindPower(0), "effectsPower", 100));
        }

        /**
         * An item with no kind at all prices at zero rather than throwing; C would dereference null.
         */
        @Test
        @DisplayName("an item with no kind adds nothing")
        void kindless() throws Exception {
            ItemObject item = item(TValue.TV_WAND);
            set(item, "kind", null);

            assertEquals(100, price(item, "effectsPower", 100));
        }

        /**
         * A curse object has no activation and its kind has no power, so nothing is added.
         */
        @Test
        @DisplayName("the curse overload adds nothing")
        void curseOverload() throws Exception {
            assertEquals(100, price(item(TValue.TV_CLOAK), "effectsPower", combatCurse(0, 0), 100));
        }
    }

    /**
     * C's {@code element_power}: per-element terms, then the combination rows.
     */
    @Nested
    @DisplayName("elementPower")
    class Elements {

        private ItemObject cloak() {
            return item(TValue.TV_CLOAK);
        }

        /**
         * Resistance to acid is worth 5.
         */
        @Test
        @DisplayName("a single resistance is priced at its resistance power")
        void singleResistance() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_ACID, 1);

            assertEquals(5, price(item, "elementPower", 0));
        }

        /**
         * Immunity to fire is {@code im_power + res_power}, 40 + 6. It is the only element at level 3,
         * so neither the immunities row (count 1) nor any other row scores.
         */
        @Test
        @DisplayName("an immunity is priced as immunity plus resistance")
        void singleImmunity() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_FIRE, 3);

            assertEquals(46, price(item, "elementPower", 0));
        }

        /**
         * Vulnerability to cold is -6, and the combination rows all ask for a level of at least 1, so
         * it counts in none.
         */
        @Test
        @DisplayName("a vulnerability is negative and counts in no row")
        void vulnerability() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_COLD, -1);

            assertEquals(-6, price(item, "elementPower", 0));
        }

        /**
         * C has no branch for level 2: it is neither -1, 1 nor 3. It still reaches the low-resists row
         * (1 <= 2) but a count of one scores nothing, so the total is zero.
         */
        @Test
        @DisplayName("a resistance level of 2 scores nothing on its own")
        void levelTwo() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_ACID, 2);

            assertEquals(0, price(item, "elementPower", 0));
        }

        /**
         * Ignoring acid is 3, and it is a separate test from the level, so ignoring and resisting
         * score together: 3 + 5.
         */
        @Test
        @DisplayName("ignoring and resisting an element both score")
        void ignoreAndResist() throws Exception {
            ItemObject item = cloak();
            item.getElInfo().get(ElementEnum.ELEM_ACID).on(ElementInfoEnum.EL_INFO_IGNORE);
            resist(item, ElementEnum.ELEM_ACID, 1);

            assertEquals(8, price(item, "elementPower", 0));
        }

        /**
         * Ignoring poison has an ignore power of zero in C's table, so it adds nothing.
         */
        @Test
        @DisplayName("ignoring an element with no ignore power adds nothing")
        void ignoreWithoutPower() throws Exception {
            ItemObject item = cloak();
            item.getElInfo().get(ElementEnum.ELEM_POIS).on(ElementInfoEnum.EL_INFO_IGNORE);

            assertEquals(0, price(item, "elementPower", 0));
        }

        /**
         * Acid and electricity resisted: 5 + 6 = 11. The low-resists row counts two, so factor 1 times
         * 2 squared is 4 more. The row's size is four, so no bonus. 15.
         */
        @Test
        @DisplayName("two low resists add the quadratic term")
        void twoLowResists() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_ACID, 1);
            resist(item, ElementEnum.ELEM_ELEC, 1);

            assertEquals(15, price(item, "elementPower", 0));
        }

        /**
         * All four basic resists: 5 + 6 + 6 + 6 = 23. Low resists count four: 1 * 16 = 16, and the
         * count equals the size, so the bonus of 10. 23 + 16 + 10 = 49. The immunities row needs level
         * 3 and counts none.
         */
        @Test
        @DisplayName("a full set of low resists adds the bonus")
        void fullLowResists() throws Exception {
            ItemObject item = cloak();
            for (ElementEnum e : List.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC,
                    ElementEnum.ELEM_FIRE, ElementEnum.ELEM_COLD)) {
                resist(item, e, 1);
            }

            assertEquals(49, price(item, "elementPower", 0));
        }

        /**
         * All four immunities: (38+5) + (35+6) + (40+6) + (37+6) = 43 + 41 + 46 + 43 = 173.
         * Immunities row, count four: 6 * 16 = 96, and size four gives INHIBIT_POWER, 20000.
         * Low resists row also counts four (level 3 is at least 1): 16 + 10.
         * 173 + 96 + 20000 + 16 + 10 = 20295.
         */
        @Test
        @DisplayName("immunity to all four basic elements is refused")
        void fullImmunities() throws Exception {
            ItemObject item = cloak();
            for (ElementEnum e : List.of(ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC,
                    ElementEnum.ELEM_FIRE, ElementEnum.ELEM_COLD)) {
                resist(item, e, 3);
            }

            assertEquals(20295, price(item, "elementPower", 0));
        }

        /**
         * All nine high resists: 28+6+16+14+8+15+20+20+20 = 147. Count nine: 2 * 81 = 162, and the
         * count equals the size, so 10. 147 + 162 + 10 = 319.
         */
        @Test
        @DisplayName("a full set of high resists adds the bonus")
        void fullHighResists() throws Exception {
            ItemObject item = cloak();
            for (ElementEnum e : List.of(ElementEnum.ELEM_POIS, ElementEnum.ELEM_LIGHT, ElementEnum.ELEM_DARK,
                    ElementEnum.ELEM_SOUND, ElementEnum.ELEM_SHARD, ElementEnum.ELEM_NEXUS,
                    ElementEnum.ELEM_NETHER, ElementEnum.ELEM_CHAOS, ElementEnum.ELEM_DISEN)) {
                resist(item, e, 1);
            }

            assertEquals(319, price(item, "elementPower", 0));
        }

        /**
         * The rows are zeroed at the start, so pricing the same item twice gives the same answer
         * rather than counting on from the first call.
         */
        @Test
        @DisplayName("the combination counts are reset on every call")
        void countsReset() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_ACID, 1);
            resist(item, ElementEnum.ELEM_ELEC, 1);

            assertEquals(15, price(item, "elementPower", 0));
            assertEquals(15, price(item, "elementPower", 0));
        }

        /**
         * The total passed in is carried through: 100 + 5.
         */
        @Test
        @DisplayName("the running total is carried through")
        void runningTotal() throws Exception {
            ItemObject item = cloak();
            resist(item, ElementEnum.ELEM_ACID, 1);

            assertEquals(105, price(item, "elementPower", 100));
        }

        /**
         * The curse overload prices the curse's own element info the same way: resisting acid and
         * electricity is 15.
         */
        @Test
        @DisplayName("the curse overload prices the curse's resistances")
        void curseOverload() throws Exception {
            Curse curse = combatCurse(0, 0);
            ElementInfo acid = new ElementInfo();
            acid.setResLevel(1);
            ElementInfo elec = new ElementInfo();
            elec.setResLevel(1);
            curse.getElInfo().put(ElementEnum.ELEM_ACID, acid);
            curse.getElInfo().put(ElementEnum.ELEM_ELEC, elec);

            assertEquals(15, price(cloak(), "elementPower", curse, 0));
        }
    }

    /**
     * C's {@code flags_power}: per-flag terms scaled by type, then the family rows.
     */
    @Nested
    @DisplayName("flagsPower")
    class FlagsPowerCases {

        /**
         * Sustain strength is worth 9, and a family of one scores nothing extra.
         */
        @Test
        @DisplayName("a single flag is priced at its power")
        void single() throws Exception {
            assertEquals(9, price(itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SUST_STR), "flagsPower", 0));
        }

        /**
         * Sustain strength and intelligence: 9 + 4 = 13. The sustains family counts two: 1 * 4 = 4.
         * 17.
         */
        @Test
        @DisplayName("two flags of a family add the quadratic term")
        void twoOfAFamily() throws Exception {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_SUST_INT);

            assertEquals(17, price(item, "flagsPower", 0));
        }

        /**
         * All five sustains: 9 + 4 + 4 + 7 + 8 = 32. Count five: 1 * 25 = 25, and the size is five,
         * so the bonus of 10. 32 + 25 + 10 = 67.
         */
        @Test
        @DisplayName("a full set of sustains adds the bonus")
        void fullSustains() throws Exception {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_SUST_INT,
                    ObjectFlag.OF_SUST_WIS, ObjectFlag.OF_SUST_DEX, ObjectFlag.OF_SUST_CON);

            assertEquals(67, price(item, "flagsPower", 0));
        }

        /**
         * Fear and blindness protection: 6 + 16 = 22. Protections have factor 3, count two: 3 * 4 = 12.
         * 34.
         */
        @Test
        @DisplayName("protections use their own factor")
        void protectionFactor() throws Exception {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_PROT_FEAR, ObjectFlag.OF_PROT_BLIND);

            assertEquals(34, price(item, "flagsPower", 0));
        }

        /**
         * All four protections: 6 + 16 + 24 + 12 = 58. Count four: 3 * 16 = 48, and the size is four,
         * so 15. 58 + 48 + 15 = 121.
         */
        @Test
        @DisplayName("a full set of protections adds the bonus")
        void fullProtections() throws Exception {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_PROT_FEAR, ObjectFlag.OF_PROT_BLIND,
                    ObjectFlag.OF_PROT_CONF, ObjectFlag.OF_PROT_STUN);

            assertEquals(121, price(item, "flagsPower", 0));
        }

        /**
         * Regeneration is 5 and {@code object_property.txt} gives it a type multiplier of 2 for a
         * cloak, so a cloak prices at 10 and a sword, which names none and so gets 1, at 5.
         */
        @Test
        @DisplayName("the type multiplier scales a flag, and an unlisted type gets one")
        void typeMultiplier() throws Exception {
            assertEquals(10, price(itemWith(TValue.TV_CLOAK, ObjectFlag.OF_REGEN), "flagsPower", 0));
            assertEquals(5, price(itemWith(TValue.TV_SWORD, ObjectFlag.OF_REGEN), "flagsPower", 0));
        }

        /**
         * All eight misc abilities, priced on a cloak (multiplier 2 where the data names one):
         * slow digestion 2, feather falling 1, regeneration 5*2, telepathy 35*2, see invisible 6*2,
         * free action 8*2, hold life 5*2, trap immunity 5*2.
         * 2 + 1 + 10 + 70 + 12 + 16 + 10 + 10 = 131. Count eight: 1 * 64 = 64, and the size is eight,
         * so 25. 131 + 64 + 25 = 220.
         */
        @Test
        @DisplayName("a full set of misc abilities adds the bonus")
        void fullMisc() throws Exception {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SLOW_DIGEST, ObjectFlag.OF_FEATHER,
                    ObjectFlag.OF_REGEN, ObjectFlag.OF_TELEPATHY, ObjectFlag.OF_SEE_INVIS,
                    ObjectFlag.OF_FREE_ACT, ObjectFlag.OF_HOLD_LIFE, ObjectFlag.OF_TRAP_IMMUNE);

            assertEquals(220, price(item, "flagsPower", 0));
        }

        /**
         * A bad flag is negative: aggravation is -20.
         */
        @Test
        @DisplayName("a bad flag prices negatively")
        void badFlag() throws Exception {
            assertEquals(-20, price(itemWith(TValue.TV_CLOAK, ObjectFlag.OF_AGGRAVATE), "flagsPower", 0));
        }

        /**
         * Burns-out has a power of zero, which is the shape of a derived flag, and adds nothing.
         */
        @Test
        @DisplayName("a flag priced at zero adds nothing")
        void zeroPower() throws Exception {
            assertEquals(0, price(itemWith(TValue.TV_CLOAK, ObjectFlag.OF_BURNS_OUT), "flagsPower", 0));
        }

        /**
         * A flag the property table does not know is a data error. C asserts; the port throws.
         */
        @Test
        @DisplayName("a flag with no property is an error")
        void unknownFlag() {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_NONE);

            assertThrows(RuntimeException.class, () -> price(item, "flagsPower", 0));
        }

        /**
         * The family counts are reset on every call: pricing twice gives 17 twice.
         */
        @Test
        @DisplayName("the family counts are reset on every call")
        void countsReset() throws Exception {
            ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_SUST_INT);

            assertEquals(17, price(item, "flagsPower", 0));
            assertEquals(17, price(item, "flagsPower", 0));
        }

        /**
         * The curse overload takes the curse's flags and no type multiplier: regeneration is 5, not
         * the 10 a cloak would get, because the curse object's type is C's tval zero.
         */
        @Test
        @DisplayName("the curse overload uses no type multiplier")
        void curseOverload() throws Exception {
            Curse curse = new Curse("test curse", List.of(), new ItemObject(), 0, null,
                    flagsOf(ObjectFlag.OF_REGEN), new HashMap<>(), new HashMap<>(), 0, 0, 0, List.of(),
                    new Flag<>(ObjectFlag.class), "", "", 1);

            assertEquals(5, price(item(TValue.TV_CLOAK), "flagsPower", curse, 0));
        }

        private Flag<ObjectFlag> flagsOf(ObjectFlag... on) {
            Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
            flags.set(List.of(on));
            return flags;
        }
    }

    /**
     * C's {@code modifier_power}: the per-modifier terms and the ability-table bonus.
     */
    @Nested
    @DisplayName("modifierPower")
    class Modifiers {

        private ItemObject cloakWith(Object... modifiersAndValues) {
            ItemObject item = item(TValue.TV_CLOAK);
            for (int i = 0; i < modifiersAndValues.length; i += 2) {
                item.getModifiers().put((ObjectModifier) modifiersAndValues[i], (Integer) modifiersAndValues[i + 1]);
            }
            return item;
        }

        /**
         * An item with no modifiers is worth nothing from them.
         */
        @Test
        @DisplayName("an item with no modifiers adds nothing")
        void none() throws Exception {
            assertEquals(0, price(cloakWith(), "modifierPower", 0));
        }

        /**
         * Strength is power 9, mult 13. +2: 18 power, total 26, index 2, table 0.
         */
        @Test
        @DisplayName("a small modifier is priced and buys no ability bonus")
        void small() throws Exception {
            assertEquals(18, price(cloakWith(ObjectModifier.OM_STR, 2), "modifierPower", 0));
        }

        /**
         * Strength +5: power 45, total 65, index 6, table 0 - the last total that buys nothing.
         * Strength +6: power 54, total 78, index 7, table 2. 56.
         */
        @Test
        @DisplayName("the ability table starts paying at a total of 70")
        void tableBoundary() throws Exception {
            assertEquals(45, price(cloakWith(ObjectModifier.OM_STR, 5), "modifierPower", 0));
            assertEquals(56, price(cloakWith(ObjectModifier.OM_STR, 6), "modifierPower", 0));
        }

        /**
         * Dexterity is power 8, mult 10. +24: power 192, total 240, index 24, table 110. 302.
         * +25: power 200, total 250, which is above 249, so INHIBIT_POWER instead. 20200.
         */
        @Test
        @DisplayName("a total above 249 is refused, and 240 reads the last table entry")
        void inhibitBoundary() throws Exception {
            assertEquals(302, price(cloakWith(ObjectModifier.OM_DEX, 24), "modifierPower", 0));
            assertEquals(20200, price(cloakWith(ObjectModifier.OM_DEX, 25), "modifierPower", 0));
        }

        /**
         * Strength -3: power -27, total -39. A negative total is neither above 249 nor above zero, so
         * there is no table lookup. -27.
         */
        @Test
        @DisplayName("a negative modifier prices negatively and is not looked up")
        void negative() throws Exception {
            assertEquals(-27, price(cloakWith(ObjectModifier.OM_STR, -3), "modifierPower", 0));
        }

        /**
         * Extra blows has power 0 but mult 50. +2 adds no power of its own, but the total is 100,
         * index 10, table 8. So 8, which is the ability bonus alone.
         */
        @Test
        @DisplayName("a modifier with no power still counts towards the total")
        void totalWithoutPower() throws Exception {
            assertEquals(8, price(cloakWith(ObjectModifier.OM_BLOWS, 2), "modifierPower", 0));
        }

        /**
         * Strength +4 and intelligence +4: power 4*9 + 4*5 = 56. Total 4*13 + 4*10 = 92, index 9,
         * table 6. 62.
         */
        @Test
        @DisplayName("modifiers are summed before the table is read")
        void summed() throws Exception {
            ItemObject item = cloakWith(ObjectModifier.OM_STR, 4, ObjectModifier.OM_INT, 4);

            assertEquals(62, price(item, "modifierPower", 0));
        }

        /**
         * Dexterity has a type multiplier of 2 for gloves. +3: 3 * 8 * 2 = 48, total 30, index 3,
         * table 0. A cloak names none and gets 1: 24.
         */
        @Test
        @DisplayName("the type multiplier scales a modifier")
        void typeMultiplier() throws Exception {
            ItemObject gloves = item(TValue.TV_GLOVES);
            gloves.getModifiers().put(ObjectModifier.OM_DEX, 3);

            assertEquals(48, price(gloves, "modifierPower", 0));
            assertEquals(24, price(cloakWith(ObjectModifier.OM_DEX, 3), "modifierPower", 0));
        }

        /**
         * The curse overload walks the curse's own modifiers with no type multiplier: dexterity +3 is
         * 24 even though the item handed in is gloves.
         */
        @Test
        @DisplayName("the curse overload prices the curse's modifiers without a type multiplier")
        void curseOverload() throws Exception {
            Curse curse = combatCurse(0, 0);
            curse.getModifiers().put(ObjectModifier.OM_DEX, 3);

            assertEquals(24, price(item(TValue.TV_GLOVES), "modifierPower", curse, 0));
        }

        /**
         * A curse with no modifiers adds nothing.
         */
        @Test
        @DisplayName("a curse with no modifiers adds nothing")
        void curseWithNone() throws Exception {
            assertEquals(0, price(item(TValue.TV_CLOAK), "modifierPower", combatCurse(0, 0), 0));
        }
    }

    /**
     * C's {@code jewelry_power}: the flat bonus for a ring or amulet.
     */
    @Nested
    @DisplayName("jewelleryPower")
    class Jewellery {

        /**
         * BASE_JEWELRY_POWER is 4, for a ring and for an amulet.
         */
        @ParameterizedTest(name = "{0} adds {1}")
        @CsvSource({"TV_RING, 4", "TV_AMULET, 4", "TV_CLOAK, 0", "TV_LIGHT, 0", "TV_SWORD, 0"})
        @DisplayName("only rings and amulets are jewellery")
        void onlyJewellery(TValue tValue, int expected) throws Exception {
            assertEquals(expected, price(item(tValue), "jewelleryPower", 0));
        }

        /**
         * The total passed in is carried through: 10 + 4.
         */
        @Test
        @DisplayName("the running total is carried through")
        void runningTotal() throws Exception {
            assertEquals(14, price(item(TValue.TV_RING), "jewelleryPower", 10));
        }

        /**
         * A curse object is not jewellery.
         */
        @Test
        @DisplayName("the curse overload adds nothing")
        void curseOverload() throws Exception {
            assertEquals(10, price(item(TValue.TV_RING), "jewelleryPower", combatCurse(0, 0), 10));
        }
    }

    /**
     * C's {@code to_ac_power}: the banded price of a to-armour bonus.
     */
    @Nested
    @DisplayName("toAcPower")
    class ToAc {

        private int priceFor(int toAC) throws Exception {
            ItemObject item = item(TValue.TV_CLOAK);
            set(item, "toAC", toAC);
            return price(item, "toAcPower", 0);
        }

        /**
         * With TO_AC_POWER 2 the first term is {@code to_a * 2 / 2}, the bonus itself, up to the high
         * threshold of 26, which is tested with {@code >} and so is not yet in the second band.
         * Above it each point over 25 adds twice more, and above 36 each point over 35 adds four
         * times more, with {@code >=} 56 adding INHIBIT_POWER on top.
         *
         * <p>27: 27 + (27-25)*2 = 31. 36: 36 + (36-25)*2 = 58, and 36 is not above 36.
         * 37: 37 + 24 + (37-35)*4 = 69. 55: 55 + 60 + 80 = 195.
         * 56: 56 + 62 + 84 + 20000 = 20202.
         */
        @ParameterizedTest(name = "to_a {0} prices at {1}")
        @CsvSource({
                "0, 0",
                "1, 1",
                "26, 26",
                "27, 31",
                "36, 58",
                "37, 69",
                "55, 195",
                "56, 20202",
                "-3, -3"
        })
        @DisplayName("the bands switch where C's comparisons put them")
        void bands(int toAC, int expected) throws Exception {
            assertEquals(expected, priceFor(toAC));
        }

        /**
         * The total passed in is carried through: 100 + 30 + (30-25)*2 = 140.
         */
        @Test
        @DisplayName("the running total is carried through")
        void runningTotal() throws Exception {
            ItemObject item = item(TValue.TV_CLOAK);
            set(item, "toAC", 30);

            assertEquals(140, price(item, "toAcPower", 100));
        }

        /**
         * The curse overload reads the curse's to_a and prices it in the same bands: 30 is 40, 56 is
         * 20202, and 0 adds nothing.
         */
        @ParameterizedTest(name = "curse to_a {0} prices at {1}")
        @CsvSource({"0, 0", "30, 40", "56, 20202", "-3, -3"})
        @DisplayName("the curse overload prices the curse's figure")
        void curseOverload(int toAC, int expected) throws Exception {
            assertEquals(expected, price(item(TValue.TV_CLOAK), "toAcPower", combatCurse(0, toAC), 0));
        }
    }

    /**
     * C's {@code ac_power}: base armour, scaled by weight.
     */
    @Nested
    @DisplayName("acPower")
    class Ac {

        private ItemObject armour(int baseAC, int toAC, int weight) {
            ItemObject item = item(TValue.TV_SOFT_ARMOR);
            set(item, "baseAC", baseAC);
            set(item, "toAC", toAC);
            set(item, "weight", weight);
            return item;
        }

        /**
         * No base armour: the whole function is skipped, so there is no flat bonus either.
         */
        @Test
        @DisplayName("an object with no base armour adds nothing")
        void noBase() throws Exception {
            assertEquals(100, price(armour(0, 10, 100), "acPower", 100));
        }

        /**
         * Base 10 at weight 100: flat 1. q = 10 * 2 / 2 = 10. i = 750 * 10 / 100 = 75. q = 10 * 75 / 100
         * = 7 (750 / 100). 1 + 7 = 8.
         */
        @Test
        @DisplayName("base armour is scaled by armour per unit of weight")
        void scaled() throws Exception {
            assertEquals(8, price(armour(10, 0, 100), "acPower", 0));
        }

        /**
         * The to-armour bonus counts towards the ratio: base 10, to_a 5, weight 100. i = 750 * 15 / 100
         * = 112 (11250 / 100). q = 10 * 112 / 100 = 11 (1120 / 100). 1 + 11 = 12.
         */
        @Test
        @DisplayName("the to-armour bonus counts towards the ratio")
        void toAcCounts() throws Exception {
            assertEquals(12, price(armour(10, 5, 100), "acPower", 0));
        }

        /**
         * The ratio is capped at 450 so that elven cloaks are not overpriced: base 10, weight 10.
         * i = 750 * 10 / 10 = 750, capped to 450. q = 10 * 450 / 100 = 45. 1 + 45 = 46.
         */
        @Test
        @DisplayName("the ratio is capped at 450")
        void capped() throws Exception {
            assertEquals(46, price(armour(10, 0, 10), "acPower", 0));
        }

        /**
         * Both divisions truncate: base 3, weight 70. q = 3 * 2 / 2 = 3. i = 750 * 3 / 70 = 32
         * (2250 / 70 = 32.1). q = 3 * 32 / 100 = 0 (96 / 100). So only the flat 1 is added.
         */
        @Test
        @DisplayName("the divisions truncate")
        void truncates() throws Exception {
            assertEquals(1, price(armour(3, 0, 70), "acPower", 0));
        }

        /**
         * A weightless item cannot be scaled and takes a fixed multiple of five: base 5, weight 0.
         * q = 5, q * 5 = 25. 1 + 25 = 26.
         */
        @Test
        @DisplayName("a weightless object takes the fixed multiple")
        void weightless() throws Exception {
            assertEquals(26, price(armour(5, 0, 0), "acPower", 0));
        }

        /**
         * A negative to-armour bonus large enough to cancel the base drives the ratio to zero, and the
         * cap has no floor: base 10, to_a -10, weight 100. i = 0. q = 0. Only the flat 1.
         */
        @Test
        @DisplayName("a bonus that cancels the base leaves only the flat bonus")
        void cancelled() throws Exception {
            assertEquals(1, price(armour(10, -10, 100), "acPower", 0));
        }

        /**
         * The total passed in is carried through: 50 + 8.
         */
        @Test
        @DisplayName("the running total is carried through")
        void runningTotal() throws Exception {
            assertEquals(58, price(armour(10, 0, 100), "acPower", 50));
        }

        /**
         * A curse has no base armour class.
         */
        @Test
        @DisplayName("the curse overload adds nothing")
        void curseOverload() throws Exception {
            assertEquals(50, price(armour(10, 0, 100), "acPower", combatCurse(0, 10), 50));
        }
    }

    /**
     * C's {@code to_hit_power}: linear, at half of TO_HIT_POWER a point.
     */
    @Nested
    @DisplayName("toHitPower")
    class ToHit {

        private int priceFor(int toHit) throws Exception {
            ItemObject item = item(TValue.TV_SWORD);
            set(item, "toHit", toHit);
            return price(item, "toHitPower", 0);
        }

        /**
         * TO_HIT_POWER is 3, so each point is {@code to_h * 3 / 2}, truncated toward zero: +1 is 1,
         * +2 is 3, +3 is 4, and -1 is -1, not the -2 a floor would give.
         */
        @ParameterizedTest(name = "to_h {0} prices at {1}")
        @CsvSource({"0, 0", "1, 1", "2, 3", "3, 4", "-1, -1", "-2, -3", "20, 30"})
        @DisplayName("each point is one and a half, truncated toward zero")
        void linear(int toHit, int expected) throws Exception {
            assertEquals(expected, priceFor(toHit));
        }

        /**
         * The total passed in is carried through: 10 + 4 * 3 / 2 = 16.
         */
        @Test
        @DisplayName("the running total is carried through")
        void runningTotal() throws Exception {
            ItemObject item = item(TValue.TV_SWORD);
            set(item, "toHit", 4);

            assertEquals(16, price(item, "toHitPower", 10));
        }

        /**
         * The curse overload reads the curse's to_h in the same way.
         */
        @ParameterizedTest(name = "curse to_h {0} prices at {1}")
        @CsvSource({"0, 0", "1, 1", "2, 3", "-1, -1", "-2, -3"})
        @DisplayName("the curse overload prices the curse's figure")
        void curseOverload(int toHit, int expected) throws Exception {
            assertEquals(expected, price(item(TValue.TV_SWORD), "toHitPower", combatCurse(toHit, 0), 0));
        }
    }

    /**
     * C's {@code rescale_bow_power}: a launcher's total divided by MAX_BLOWS.
     */
    @Nested
    @DisplayName("rescaleBowPower")
    class RescaleBow {

        /**
         * MAX_BLOWS is 5 and the division truncates toward zero: 103 is 20, 4 is 0, and -7 is -1, not
         * the -2 a floor would give.
         */
        @ParameterizedTest(name = "a bow at {0} rescales to {1}")
        @CsvSource({"103, 20", "100, 20", "4, 0", "0, 0", "-7, -1"})
        @DisplayName("a launcher is divided by MAX_BLOWS, truncating toward zero")
        void bowDivided(int power, int expected) throws Exception {
            assertEquals(expected, price(item(TValue.TV_BOW), "rescaleBowPower", power));
        }

        /**
         * Only the shooting slot is rescaled; other things worn are left alone.
         */
        @ParameterizedTest(name = "{0} is left alone")
        @CsvSource({"TV_SWORD", "TV_CLOAK", "TV_RING", "TV_SOFT_ARMOR", "TV_ARROW"})
        @DisplayName("an object that is not worn in the shooting slot is unchanged")
        void othersUnchanged(TValue tValue) throws Exception {
            assertEquals(103, price(item(tValue), "rescaleBowPower", 103));
        }

        /**
         * A curse object is not wielded.
         */
        @Test
        @DisplayName("the curse overload is unchanged")
        void curseOverload() throws Exception {
            assertEquals(103, price(item(TValue.TV_BOW), "rescaleBowPower", combatCurse(0, 0), 103));
        }
    }
}
