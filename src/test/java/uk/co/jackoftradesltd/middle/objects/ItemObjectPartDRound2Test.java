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
import uk.co.jackoftradesltd.middle.game.gameengine.GameState;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ResType;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.middle.player.Player;
import uk.co.jackoftradesltd.testsupport.CurseFixture;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * The second-round tests for the power components of {@link ItemObject}, the ports of
 * {@code effects_power}, {@code element_power}, {@code flags_power}, {@code modifier_power},
 * {@code to_ac_power}, {@code ac_power}, {@code to_hit_power} and {@code rescale_bow_power}
 * ({@code obj-power.c}). They add to {@code ItemObjectPropertyPowerTest} rather than repeat it,
 * and cover what its first pass left out: the activation of power zero that blocks the kind's
 * fallback, the unfloored armour ratio and the exact cap boundary, mixed and double immunities, the
 * curse overloads at the same thresholds as the item ones, and the shared count rows being re-zeroed
 * between an item and a curse.
 *
 * <p>Every expected figure is worked out by hand from the C source, the constants in
 * {@code obj-power.h} and the data in {@code lib/gamedata/object_property.txt}, with the arithmetic in
 * the comment on each case. None is read back from the port.
 *
 * <p>Class ItemObjectPartDRound2Test coded on 261007, commented in full on 261007.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectPartDRound2Test {

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
     */
    private static int price(ItemObject item, String name, int power) throws Exception {
        return (int) invoke(item, name, new Class<?>[]{int.class}, power);
    }

    /**
     * Prices with one of the {@link Curse} overloads.
     */
    private static int price(ItemObject item, String name, Curse curse, int power) throws Exception {
        return (int) invoke(curse.getItemObject(), name, new Class<?>[]{int.class}, power);
    }

    private static ItemObject item(TValue tValue) {
        return ItemFixture.item(tValue).build();
    }

    private static ItemObject itemWith(TValue tValue, ObjectFlag... flags) {
        return ItemFixture.item(tValue).flags(flags).build();
    }

    private static Flag<ObjectFlag> flagsOf(ObjectFlag... on) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        flags.set(List.of(on));
        return flags;
    }

    /**
     * A curse carrying the given flags and combat figures and nothing else.
     */
    private static Curse curse(Flag<ObjectFlag> flags, int toHit, int toAC) {
        return CurseFixture.curse("test curse", List.of(), 0, null, flags, new HashMap<>(), new HashMap<>(),
                toHit, 0, toAC, List.of(), new Flag<>(ObjectFlag.class), "", "", 1);
    }

    private static void resist(ItemObject item, ElementEnum element, int level) {
        item.getElInfo().get(element).setResLevel(level);
    }

    private static ItemObject armour(int baseAC, int toAC, int weight) {
        ItemObject item = item(TValue.TV_SOFT_ARMOR);
        set(item, "baseAC", baseAC);
        set(item, "toAC", toAC);
        set(item, "weight", weight);
        return item;
    }

    private static Curse resistCurse(int level, ElementEnum... elements) {
        Curse curse = curse(new Flag<>(ObjectFlag.class), 0, 0);
        for (ElementEnum element : elements) {
            ElementInfo info = new ElementInfo();
            info.setResLevel(level);
            curse.getItemObject().getElInfo().put(element, info);
        }
        return curse;
    }

    private static Curse modifierCurse(ObjectModifier modifier, int value) {
        Curse curse = curse(new Flag<>(ObjectFlag.class), 0, 0);
        curse.getItemObject().getModifiers().put(modifier, value);
        return curse;
    }

    /**
     * Installs a player with a body, which the bow comparison needs. It goes in before any item is
     * built, because an item captures the player at construction.
     */
    @BeforeEach
    void installPlayer() {
        savedPlayer = GameState.getPlayer();
        GameState.setPlayer(new Player());
    }

    // ---------------------------------------------------------------- effectsPower

    /**
     * Puts the game's player back.
     */
    @AfterEach
    void restorePlayer() {
        GameState.setPlayer(savedPlayer);
    }

    // ---------------------------------------------------------------- acPower

    /**
     * C tests the activation pointer, not its power: an activation of power 0 is still the
     * activation, so the kind's 12 is not consulted. 100 + 0.
     */
    @Test
    @DisplayName("effectsPower: an activation of power zero blocks the kind's fallback")
    void zeroPowerActivationBlocksKind() throws Exception {
        ObjectKind kind = new ObjectKind();
        set(kind, "power", 12);
        ItemObject item = ItemFixture.item(TValue.TV_WAND).kind(kind).build();
        List<Activation> list = new ArrayList<>();
        list.add(new Activation("test", 1, false, 1, 0, new ArrayList<>(), "", ""));
        set(item, "activation", list);

        assertEquals(100, price(item, "effectsPower", 100));
    }

    /**
     * No floor on the ratio: base 10, to_a -20, weight 100. q = 10 * 2 / 2 = 10. i = 750 * (10 - 20)
     * / 100 = -75. q = 10 * -75 = -750, / 100 = -7 (C truncates toward zero, not -8). 1 + -7 = -6.
     */
    @Test
    @DisplayName("acPower: a negative ratio is not floored and truncates toward zero")
    void negativeRatio() throws Exception {
        assertEquals(-6, price(armour(10, -20, 100), "acPower", 0));
    }

    // ---------------------------------------------------------------- toAcPower and toHitPower

    /**
     * Base 18 so q = 18. Weight 30: i = 750 * 18 / 30 = 450 exactly, no cap needed; q = 18 * 450 /
     * 100 = 81; 82. Weight 29: i = 13500 / 29 = 465, capped to 450; the same 82. Weight 31: i =
     * 13500 / 31 = 435 (435.48); q = 18 * 435 / 100 = 78 (7830 / 100); 79.
     */
    @ParameterizedTest(name = "weight {0} gives {1}")
    @CsvSource({"29, 82", "30, 82", "31, 79"})
    @DisplayName("acPower: the cap at 450 is exact")
    void capBoundary(int weight, int expected) throws Exception {
        assertEquals(expected, price(armour(18, 0, weight), "acPower", 0));
    }

    /**
     * A negative bonus scores only the base term and none of the bands: -40 is -40, and 25, the last
     * value under the first band (the test is {@code > 26}), is 25.
     */
    @ParameterizedTest(name = "to_a {0} is {1}")
    @CsvSource({"-40, -40", "25, 25"})
    @DisplayName("toAcPower: negative and sub-band figures")
    void toAcEdges(int toAC, int expected) throws Exception {
        ItemObject item = item(TValue.TV_CLOAK);
        set(item, "toAC", toAC);

        assertEquals(expected, price(item, "toAcPower", 0));
        assertEquals(expected, price(item, "toAcPower", curse(new Flag<>(ObjectFlag.class), 0, toAC), 0));
    }

    // ---------------------------------------------------------------- rescaleBowPower

    /**
     * q = to_h * 3 / 2 truncating toward zero: +3 is 9 / 2 = 4, -3 is -9 / 2 = -4 (a floor would
     * give -5), +4 is 6, -1 is -3 / 2 = -1. The total handed in is added to, not replaced: 10 + q.
     */
    @ParameterizedTest(name = "to_h {0} adds {1}")
    @CsvSource({"3, 4", "-3, -4", "4, 6", "-1, -1"})
    @DisplayName("toHitPower: truncation toward zero, both overloads, total carried")
    void toHitTruncation(int toHit, int expected) throws Exception {
        ItemObject item = item(TValue.TV_SWORD);
        set(item, "toHit", toHit);

        assertEquals(10 + expected, price(item, "toHitPower", 10));
        assertEquals(10 + expected, price(item, "toHitPower", curse(new Flag<>(ObjectFlag.class), toHit, 0), 10));
    }

    // ---------------------------------------------------------------- elementPower

    /**
     * MAX_BLOWS is 5: 5 is 1, -5 is -1, 9 is 1, -4 is 0 (truncation, not a floor of -1).
     */
    @ParameterizedTest(name = "a bow at {0} rescales to {1}")
    @CsvSource({"5, 1", "-5, -1", "9, 1", "-4, 0"})
    @DisplayName("rescaleBowPower: more truncation boundaries")
    void bowBoundaries(int power, int expected) throws Exception {
        assertEquals(expected, price(item(TValue.TV_BOW), "rescaleBowPower", power));
    }

    /**
     * Acid and fire immune: (38 + 5) + (40 + 6) = 89. Immunities row count two: 6 * 4 = 24, not full.
     * Level 3 is at least 1, so low resists also count two: 1 * 4 = 4. 89 + 24 + 4 = 117.
     */
    @Test
    @DisplayName("elementPower: two immunities count in both the immunities and low-resists rows")
    void twoImmunities() throws Exception {
        ItemObject item = item(TValue.TV_CLOAK);
        resist(item, ElementEnum.ELEM_ACID, 3);
        resist(item, ElementEnum.ELEM_FIRE, 3);

        assertEquals(117, price(item, "elementPower", 0));
    }

    /**
     * Acid immune (43), lightning resistant (6) = 49. Immunities row counts only acid: one, no
     * multiple. Low resists count both: 1 * 4 = 4. 53.
     */
    @Test
    @DisplayName("elementPower: an immunity and a resistance share the low-resists row")
    void mixedImmunityAndResist() throws Exception {
        ItemObject item = item(TValue.TV_CLOAK);
        resist(item, ElementEnum.ELEM_ACID, 3);
        resist(item, ElementEnum.ELEM_ELEC, 1);

        assertEquals(53, price(item, "elementPower", 0));
    }

    /**
     * The curse overload meets the same rows: four immunities is 173 + 96 + 20000 + 16 + 10 = 20295.
     */
    @Test
    @DisplayName("elementPower: a curse granting all four immunities is refused")
    void curseImmunities() throws Exception {
        Curse curse = resistCurse(3, ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC, ElementEnum.ELEM_FIRE,
                ElementEnum.ELEM_COLD);

        assertEquals(20295, price(item(TValue.TV_CLOAK), "elementPower", curse, 0));
    }

    // ---------------------------------------------------------------- flagsPower

    /**
     * The rows are zeroed on every call, whichever overload ran last: pricing a curse that fills
     * every low row, then a bare item, then the curse again, leaves the bare item at 0 and the curse
     * at the same figure both times.
     */
    @Test
    @DisplayName("elementPower: the shared rows are re-zeroed between the item and curse overloads")
    void rowsResetAcrossOverloads() throws Exception {
        Curse curse = resistCurse(1, ElementEnum.ELEM_ACID, ElementEnum.ELEM_ELEC, ElementEnum.ELEM_FIRE,
                ElementEnum.ELEM_COLD);
        ItemObject bare = item(TValue.TV_CLOAK);

        assertEquals(49, price(bare, "elementPower", curse, 0));
        assertEquals(0, price(bare, "elementPower", 0));
        assertEquals(49, price(bare, "elementPower", curse, 0));
    }

    /**
     * Pricing reads a copy of the item's flags and leaves the item as it was.
     */
    @Test
    @DisplayName("flagsPower: the item's own flags are not changed")
    void flagsUnchanged() throws Exception {
        ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_PROT_FEAR);

        price(item, "flagsPower", 0);

        assertTrue(item.getFlags().has(ObjectFlag.OF_SUST_STR));
        assertTrue(item.getFlags().has(ObjectFlag.OF_PROT_FEAR));
    }

    /**
     * The curse overload scores the curse's flags the way the item overload scores an item's, with a
     * multiplier of 1. Two sustains: 9 + 4 + 1 * 4 = 17. Four protections: 6 + 16 + 24 + 12 = 58, 3 *
     * 16 = 48, full set 15: 121. Aggravation alone: -20.
     */
    @Test
    @DisplayName("flagsPower: the curse overload applies the family rows")
    void curseFamilies() throws Exception {
        ItemObject cloak = item(TValue.TV_CLOAK);

        assertEquals(17, price(cloak, "flagsPower",
                curse(flagsOf(ObjectFlag.OF_SUST_STR, ObjectFlag.OF_SUST_INT), 0, 0), 0));
        assertEquals(121, price(cloak, "flagsPower",
                curse(flagsOf(ObjectFlag.OF_PROT_FEAR, ObjectFlag.OF_PROT_BLIND, ObjectFlag.OF_PROT_CONF,
                        ObjectFlag.OF_PROT_STUN), 0, 0), 0));
        assertEquals(-20, price(cloak, "flagsPower", curse(flagsOf(ObjectFlag.OF_AGGRAVATE), 0, 0), 0));
    }

    // ---------------------------------------------------------------- modifierPower (curse)

    /**
     * The family rows are re-zeroed on every call: five sustains on the item (67), then a curse with
     * one sustain, which is 9 and not 9 plus a leftover count of five.
     */
    @Test
    @DisplayName("flagsPower: the family rows are re-zeroed between the item and curse overloads")
    void flagRowsResetAcrossOverloads() throws Exception {
        ItemObject item = itemWith(TValue.TV_CLOAK, ObjectFlag.OF_SUST_STR, ObjectFlag.OF_SUST_INT,
                ObjectFlag.OF_SUST_WIS, ObjectFlag.OF_SUST_DEX, ObjectFlag.OF_SUST_CON);

        assertEquals(67, price(item, "flagsPower", 0));
        assertEquals(9, price(item, "flagsPower", curse(flagsOf(ObjectFlag.OF_SUST_STR), 0, 0), 0));
    }

    /**
     * The curse overload meets the same thresholds as the item one, with no type multiplier.
     * Strength is power 9, mult 13: +5 is 45 with total 65 (nothing from the table); +6 is 54, total
     * 78, index 7, table 2: 56. Dexterity is power 8, mult 10: +24 is 192, total 240, index 24,
     * table 110: 302; +25 is 200, total 250, which is above 249: 200 + 20000. Strength -3 is -27 and
     * the negative total is not looked up. Extra blows is power 0, mult 50: +2 is total 100, index
     * 10, table 8.
     */
    @ParameterizedTest(name = "{0} {1} prices at {2}")
    @CsvSource({"OM_STR, 5, 45", "OM_STR, 6, 56", "OM_DEX, 24, 302", "OM_DEX, 25, 20200", "OM_STR, -3, -27",
            "OM_BLOWS, 2, 8"})
    @DisplayName("modifierPower: the curse overload meets the same thresholds as the item")
    void curseThresholds(ObjectModifier modifier, int value, int expected) throws Exception {
        assertEquals(expected, price(item(TValue.TV_GLOVES), "modifierPower", modifierCurse(modifier, value), 0));
    }
}
