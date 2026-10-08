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

package uk.co.jackoftradesltd.middle.game.globals.registry;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.objects.ObjectBase;
import uk.co.jackoftradesltd.middle.objects.ObjectKind;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.Rune;
import uk.co.jackoftradesltd.middle.objects.enums.CombatRunes;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins {@link ObjectRegistry} to figures read out of the C source, where the existing
 * {@code ObjectRegistryTest} and {@code ObjectRegistryTablesTest} pin its own rules: the
 * {@code obj-power.h} constants and the {@code ability_power} table ({@code obj-power.c}), the
 * {@code lookup_obj_property} walk ({@code obj-properties.c}) including its "stats count as mods"
 * rule, the two sentinel kinds C resolves at the end of {@code finish_parse_artifact}
 * ({@code obj-init.c}), the rune count of {@code max_runes} ({@code obj-knowledge.c}), and the
 * edges of {@code lookup_sval}'s number-or-name reference ({@code obj-util.c}).
 *
 * <p>Every expected value is typed in from the C file, not read back from the port. The registry is
 * global static state, so each test saves the fields it writes and restores them afterwards.
 *
 * <p>Class ObjectRegistryCReferenceTest commented in full on 261008.
 *
 * @author Rowan Crowther
 */
class ObjectRegistryCReferenceTest {

    private static final List<String> TOUCHED = List.of(
            "objectProperties", "objectKinds", "kindsByTvalSval", "objectBases",
            "unknownGoldKind", "unknownItemKind", "allRunes");

    private final Map<String, Object> saved = new HashMap<>();

    private static Field field(String name) throws Exception {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static Object get(String name) throws Exception {
        return field(name).get(null);
    }

    private static void set(String name, Object value) throws Exception {
        field(name).set(null, value);
    }

    private static ObjectBase base(TValue tval, String name) {
        return new ObjectBase(tval, name, ColourEnum.COLOUR_WHITE,
                new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), -1, -1);
    }

    /**
     * A kind carrying only what the registry reads: name, base, tval and sval name.
     */
    private static ObjectKind kind(String name, ObjectBase base, String svalName) throws Exception {
        ObjectKind k = new ObjectKind();
        for (Map.Entry<String, Object> entry : Map.of(
                        "name", name, "base", base, "tValue", base.gettVal(), "sValueName", svalName)
                .entrySet()) {
            Field f = ObjectKind.class.getDeclaredField(entry.getKey());
            f.setAccessible(true);
            f.set(k, entry.getValue());
        }
        return k;
    }

    private static ObjectProperty property(ObjPropertyType type, ObjectPropertyTypeWrapper payload) {
        return new ObjectProperty(type, ObjectFlagType.OFT_MISC, ObjectFlagID.OFID_WIELD, payload,
                0, 0, Map.of(), "n", "a", "na", "m", "d", List.of());
    }

    @BeforeEach
    void saveAndIsolate() throws Exception {
        for (String name : TOUCHED) {
            saved.put(name, get(name));
        }
        set("objectKinds", new ArrayList<ObjectKind>());
        set("kindsByTvalSval", new HashMap<TValue, Map<Integer, ObjectKind>>());
        set("unknownGoldKind", null);
        set("unknownItemKind", null);
    }

    @AfterEach
    void restore() throws Exception {
        for (String name : TOUCHED) {
            set(name, saved.get(name));
        }
    }

    // ---- obj-power.h and obj-power.c constants ------------------------------

    /**
     * Each constant against its {@code #define} in {@code obj-power.h}.
     */
    @Test
    void thePowerConstantsMatchObjPowerH() {
        assertEquals(15, ObjectRegistry.NONWEAP_DAMAGE);
        assertEquals(12, ObjectRegistry.WEAP_DAMAGE);
        assertEquals(4, ObjectRegistry.BASE_JEWELERY_POWER, "BASE_JEWELRY_POWER in C");
        assertEquals(1, ObjectRegistry.BASE_ARMOUR_POWER);
        assertEquals(5, ObjectRegistry.DAMAGE_POWER, "i.e. 2.5");
        assertEquals(3, ObjectRegistry.TO_HIT_POWER, "i.e. 1.5");
        assertEquals(2, ObjectRegistry.BASE_AC_POWER, "i.e. 1");
        assertEquals(2, ObjectRegistry.TO_AC_POWER, "i.e. 1");
        assertEquals(5, ObjectRegistry.MAX_BLOWS);
        assertEquals(1, ObjectRegistry.WGT_POWER_NUM_NOBASEAC);
        assertEquals(50, ObjectRegistry.WGT_POWER_DEN_NOBASEAC);
        assertEquals(15, ObjectRegistry.WGT_POWER_NUM_THROW);
        assertEquals(12, ObjectRegistry.WGT_POWER_DEN_THROW);
    }

    /**
     * The inhibiting values and thresholds from the second block of {@code obj-power.h}.
     */
    @Test
    void theInhibitingThresholdsMatchObjPowerH() {
        assertEquals(20000, ObjectRegistry.INHIBIT_POWER);
        assertEquals(3, ObjectRegistry.INHIBIT_BLOWS);
        assertEquals(4, ObjectRegistry.INHIBIT_MIGHT);
        assertEquals(21, ObjectRegistry.INHIBIT_SHOTS);
        assertEquals(26, ObjectRegistry.HIGH_TO_AC);
        assertEquals(36, ObjectRegistry.VERYHIGH_TO_AC);
        assertEquals(56, ObjectRegistry.INHIBIT_AC);
        assertEquals(16, ObjectRegistry.HIGH_TO_HIT);
        assertEquals(26, ObjectRegistry.VERYHIGH_TO_HIT);
        assertEquals(16, ObjectRegistry.HIGH_TO_DAM);
        assertEquals(26, ObjectRegistry.VERYHIGH_TO_DAM);
        assertEquals(20, ObjectRegistry.AMMO_RESCALER);
    }

    /**
     * {@code ability_power[25]} in {@code obj-power.c}, entry for entry.
     */
    @Test
    void theAbilityPowerTableMatchesObjPowerC() {
        int[] expected = {0, 0, 0, 0, 0, 0, 0, 2, 4, 6, 8,
                12, 16, 20, 24, 30, 36, 42, 48, 56, 64,
                74, 84, 96, 110};

        assertArrayEquals(expected, ObjectRegistry.abilityPower);
        assertEquals(25, ObjectRegistry.abilityPower.length);
    }

    // ---- lookup_obj_property ----------------------------------------------

    /**
     * An ordinary request: the type must match and so must the index. A flag property whose
     * payload is a different flag, and a property of another type over the same flag, are both
     * passed over.
     */
    @Test
    void anObjectPropertyIsFoundByTypeAndPayload() throws Exception {
        ObjectProperty sustStr = property(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_SUST_STR));
        ObjectProperty protFear = property(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_PROT_FEAR));
        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(sustStr, protFear)));

        assertSame(protFear, ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_PROT_FEAR)));
        assertSame(sustStr, ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_SUST_STR)));
        assertNull(ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG,
                        new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_FREE_ACT)),
                "a flag no property declares");
    }

    /**
     * The property list is empty: C's loop runs zero times and returns {@code NULL}.
     */
    @Test
    void anEmptyPropertyListAnswersNull() {
        ObjectRegistry.setObjectProperties(new ArrayList<>());

        assertNull(ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_SUST_STR)));
    }

    /**
     * "Special case - stats count as mods": a request for {@code OBJ_PROPERTY_MOD} finds a property
     * declared as {@code OBJ_PROPERTY_STAT} with the same index.
     */
    @Test
    void aModRequestFindsAStatProperty() {
        ObjectProperty str = property(ObjPropertyType.OBJ_PROPERTY_STAT,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR));
        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(str)));

        assertSame(str, ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR)));
    }

    /**
     * The rule runs one way only: C's second clause requires {@code type == OBJ_PROPERTY_MOD}, so a
     * request for {@code OBJ_PROPERTY_STAT} does not find a property declared as a plain mod.
     */
    @Test
    void aStatRequestDoesNotFindAModProperty() {
        ObjectProperty stealth = property(ObjPropertyType.OBJ_PROPERTY_MOD,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH));
        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(stealth)));

        assertNull(ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_STAT,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STEALTH)));
    }

    /**
     * Both a stat and a mod over the same modifier are loaded: the walk returns whichever comes
     * first in list order, as C's single pass does, whether that entry matches by the main clause or
     * the special one.
     */
    @Test
    void theFirstQualifyingPropertyInListOrderWins() {
        ObjectPropertyTypeWrapper asStat = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR);
        ObjectPropertyTypeWrapper asMod = new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STR);
        ObjectProperty statFirst = property(ObjPropertyType.OBJ_PROPERTY_STAT, asStat);
        ObjectProperty modSecond = property(ObjPropertyType.OBJ_PROPERTY_MOD, asMod);

        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(statFirst, modSecond)));
        assertSame(statFirst, ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, asMod));

        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(modSecond, statFirst)));
        assertSame(modSecond, ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, asMod));
    }

    /**
     * Two properties of different types over different families must not be confused: a flag
     * request never returns a modifier property just because it is first in the list.
     */
    @Test
    void aRequestOfOneFamilyIgnoresPropertiesOfAnother() {
        ObjectProperty stealth = property(ObjPropertyType.OBJ_PROPERTY_MOD,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_STEALTH));
        ObjectProperty sustStr = property(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_SUST_STR));
        ObjectRegistry.setObjectProperties(new ArrayList<>(List.of(stealth, sustStr)));

        assertSame(sustStr, ObjectRegistry.lookupObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_SUST_STR)));
    }

    // ---- unknown_gold_kind / unknown_item_kind -----------------------------

    /**
     * C sets both sentinels from {@code object.txt} entries named {@code <unknown item>} and
     * {@code <unknown treasure>}, both under tval {@code none}.
     */
    @Test
    void loadUnknownKindsResolvesBothSentinelsByTheirDataFileNames() throws Exception {
        ObjectBase none = base(TValue.TV_NONE, "none");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(none)));
        ObjectKind pile = kind("<pile>", none, "<pile>");
        ObjectKind item = kind("<unknown item>", none, "<unknown item>");
        ObjectKind gold = kind("<unknown treasure>", none, "<unknown treasure>");
        ObjectRegistry.addObjectKind(pile);
        ObjectRegistry.addObjectKind(item);
        ObjectRegistry.addObjectKind(gold);

        ObjectRegistry.loadUnknownKinds();

        assertSame(gold, ObjectRegistry.unknownGoldKind);
        assertSame(item, ObjectRegistry.unknownItemKind);
    }

    /**
     * With no kinds loaded the method returns without touching the sentinels, where every lookup
     * would throw.
     */
    @Test
    void loadUnknownKindsDoesNothingWhileTheTableIsEmpty() {
        assertDoesNotThrow(ObjectRegistry::loadUnknownKinds);

        assertNull(ObjectRegistry.unknownGoldKind);
        assertNull(ObjectRegistry.unknownItemKind);
    }

    /**
     * A table that holds neither name leaves both sentinels null rather than failing.
     */
    @Test
    void loadUnknownKindsLeavesNullWhenTheNamesAreAbsent() throws Exception {
        ObjectBase sword = base(TValue.TV_SWORD, "sword");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(sword)));
        ObjectRegistry.addObjectKind(kind("Dagger", sword, "Dagger"));

        ObjectRegistry.loadUnknownKinds();

        assertNull(ObjectRegistry.unknownGoldKind);
        assertNull(ObjectRegistry.unknownItemKind);
    }

    /**
     * The sentinels point into the table, so {@code reset} drops them with it.
     */
    @Test
    void resetNullsTheSentinels() throws Exception {
        ObjectBase none = base(TValue.TV_NONE, "none");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(none)));
        ObjectRegistry.addObjectKind(kind("<unknown item>", none, "<unknown item>"));
        ObjectRegistry.addObjectKind(kind("<unknown treasure>", none, "<unknown treasure>"));
        ObjectRegistry.loadUnknownKinds();
        assertNotNull(ObjectRegistry.unknownGoldKind);

        ObjectRegistry.reset();

        assertNull(ObjectRegistry.unknownGoldKind);
        assertNull(ObjectRegistry.unknownItemKind);
    }

    // ---- max_runes / rune_list --------------------------------------------

    /**
     * {@code max_runes()} returns {@code rune_max}, the length of {@code rune_list}: zero for an
     * empty list, and the list's length otherwise.
     */
    @Test
    void theRuneCountIsTheLengthOfTheList() {
        ObjectRegistry.setRunes(new ArrayList<>());
        assertEquals(0, ObjectRegistry.getMaxRunes());
        assertTrue(ObjectRegistry.getRunes().isEmpty());

        List<Rune> two = new ArrayList<>();
        two.add(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A)));
        two.add(new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H)));
        ObjectRegistry.setRunes(two);

        assertEquals(2, ObjectRegistry.getMaxRunes());
        assertEquals(2, ObjectRegistry.getRunes().size());
    }

    /**
     * Position is a rune's identity (the savefile addresses runes by it), so the list keeps the
     * order it was given, and a later change to the caller's list reaches neither the stored list
     * nor the count.
     */
    @Test
    void theRuneListKeepsItsOrderAndIsNotAliasedToTheCallersList() {
        Rune toArmour = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A));
        Rune toHit = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H));
        List<Rune> callers = new ArrayList<>(List.of(toArmour, toHit));

        ObjectRegistry.setRunes(callers);
        callers.clear();

        assertEquals(2, ObjectRegistry.getMaxRunes());
        assertSame(toArmour, ObjectRegistry.getRunes().get(0));
        assertSame(toHit, ObjectRegistry.getRunes().get(1));
        assertThrows(UnsupportedOperationException.class, () -> ObjectRegistry.getRunes().add(toHit));
    }

    // ---- lookup_sval edges --------------------------------------------------

    /**
     * {@code lookup_sval} accepts a number followed only by spaces or tabs, and {@code strtoul}
     * accepts a leading plus sign: all of these name sval 2.
     */
    @Test
    void aNumericReferenceMayCarryASignOrTrailingBlanks() throws Exception {
        ObjectBase sword = base(TValue.TV_SWORD, "sword");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(sword)));
        ObjectKind dagger = kind("Dagger", sword, "Dagger");
        ObjectKind rapier = kind("Rapier", sword, "Rapier");
        ObjectRegistry.addObjectKind(dagger);
        ObjectRegistry.addObjectKind(rapier);

        assertSame(rapier, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "+2"));
        assertSame(rapier, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "2  "));
        assertSame(rapier, ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "2\t"));
    }

    /**
     * Numbers C refuses (negative, which {@code strtoul} wraps past {@code INT_MAX}; beyond
     * {@code INT_MAX}; or followed by something other than blanks) all end at "no kind" in C, and do
     * here.
     */
    @Test
    void aNumericReferenceCRefusesFindsNoKindHere() throws Exception {
        ObjectBase sword = base(TValue.TV_SWORD, "sword");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(sword)));
        ObjectRegistry.addObjectKind(kind("Dagger", sword, "Dagger"));
        ObjectRegistry.addObjectKind(kind("Rapier", sword, "Rapier"));

        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "-1"), "negative");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "2147483647"), "INT_MAX itself");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "99999999999"), "beyond INT_MAX");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "2x"), "digits then text");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, "2 2"), "digits, blank, digits");
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SWORD, ""), "empty reference");
    }

    /**
     * C compares the kind's name after {@code obj_desc_name_format} has dropped its {@code &} and
     * {@code ~} markers, so a data-file reference names a kind without them.
     */
    @Test
    void aNameReferenceIgnoresTheMarkersInTheKindsDataFileName() throws Exception {
        ObjectBase shot = base(TValue.TV_SHOT, "shot");
        ObjectRegistry.setObjectBases(new ArrayList<>(List.of(shot)));
        ObjectKind iron = kind("& Iron Shot~", shot, "Iron Shot");
        ObjectRegistry.addObjectKind(iron);

        assertSame(iron, ObjectRegistry.lookupObjectKind(TValue.TV_SHOT, "Iron Shot"));
        assertNull(ObjectRegistry.lookupObjectKind(TValue.TV_SHOT, "& Iron Shot~"),
                "the raw name, markers and all, is not what lookup_sval compares");
    }
}
