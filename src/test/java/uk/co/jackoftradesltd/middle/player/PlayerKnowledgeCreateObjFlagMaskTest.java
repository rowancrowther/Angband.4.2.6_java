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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.ObjectProperty;
import uk.co.jackoftradesltd.middle.objects.ObjectPropertyTypeWrapper;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@code PlayerKnowledge.createObjFlagMask}, the port of C's {@code create_obj_flag_mask}
 * ({@code obj-properties.c}) - the selection of every flag property whose id type, or whose
 * subtype, is one of a list.
 *
 * <p>The expected sets are read off C's loop, not off the port: C walks every property, skips any
 * whose type is not {@code OBJ_PROPERTY_FLAG}, and switches on the property's flag when the id type
 * ({@code id} true) or the subtype ({@code id} false) equals one of the listed values. The fixture
 * is the real classification of a spread of flags from {@code lib/gamedata/object_property.txt} -
 * the id-type and subtype each one is given in the data file - so the sets below are what the real
 * game would select, not values chosen to suit the code. The one property with neither line is the
 * zero default C's {@code mem_zalloc} gives a record that omits them.
 *
 * <p>The method is private, so it is reached by reflection.
 *
 * <p>Test class coded on 261003.
 */
@DisplayName("PlayerKnowledge.createObjFlagMask")
class PlayerKnowledgeCreateObjFlagMaskTest {
    private static Object savedProperties;
    private static Object savedPropertyMax;
    private static Method method;

    @BeforeAll
    static void saveAndReflect() throws Exception {
        savedProperties = registryField("objectProperties").get(null);
        savedPropertyMax = registryField("objectPropertyMax").get(null);
        method = PlayerKnowledge.class.getDeclaredMethod("createObjFlagMask", boolean.class, Enum[].class);
        method.setAccessible(true);
    }

    @AfterAll
    static void restore() throws Exception {
        registryField("objectProperties").set(null, savedProperties);
        registryField("objectPropertyMax").set(null, savedPropertyMax);
    }

    private static Field registryField(String name) throws NoSuchFieldException {
        Field f = ObjectRegistry.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    // ---- by id type ------------------------------------------------------------------------

    private static ObjectProperty flag(ObjectFlag flag, ObjectFlagType subtype, ObjectFlagID idType) {
        return new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, subtype, idType,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, flag),
                0, 0, null, flag.name(), null, null, null, null, null);
    }

    @SuppressWarnings("unchecked")
    private static Flag<ObjectFlag> call(boolean maskByID, Enum<?>... flags) throws Exception {
        try {
            return (Flag<ObjectFlag>) method.invoke(null, maskByID, (Object) flags);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Exception cause) throw cause;
            throw e;
        }
    }

    private static Set<ObjectFlag> set(Flag<ObjectFlag> flag) {
        Set<ObjectFlag> result = EnumSet.noneOf(ObjectFlag.class);
        for (ObjectFlag f : flag) result.add(f);
        return result;
    }

    private static Set<ObjectFlag> mask(boolean maskByID, Enum<?>... flags) throws Exception {
        return set(call(maskByID, flags));
    }

    /**
     * The real data-file classification of ten flags, plus a stat and a classification-free flag.
     * Column order: flag, subtype, id-type, as {@code object_property.txt} gives them.
     *
     * <pre>
     * SUST_STR        sustain        on effect
     * PROT_FEAR       protection     on effect
     * REGEN           misc ability   timed
     * TELEPATHY       misc ability   on wield
     * BLESSED         melee          on wield
     * AFRAID          bad            on wield
     * IMPAIR_HP       bad            timed
     * DIG_1           dig            on wield
     * THROWING        throw          on wield
     * MULTIPLY_WEIGHT curse-only     on wield
     * </pre>
     *
     * <p>{@code NO_TELEPORT} is given neither (not what the data file says of it - it is
     * {@code bad}/{@code on effect} there - but the zeroed default C gives a record with no
     * {@code subtype:} or {@code id-type:}, which is the case the {@code OFT_NONE}/{@code OFID_NONE}
     * tests need). The strength stat has no flag payload at all and the same zero classification,
     * and is there to prove a non-flag is skipped even when its classification matches.
     */
    @BeforeEach
    void loadProperties() {
        List<ObjectProperty> props = new ArrayList<>();
        props.add(flag(ObjectFlag.OF_SUST_STR, ObjectFlagType.OFT_SUST, ObjectFlagID.OFID_NORMAL));
        props.add(flag(ObjectFlag.OF_PROT_FEAR, ObjectFlagType.OFT_PROT, ObjectFlagID.OFID_NORMAL));
        props.add(flag(ObjectFlag.OF_REGEN, ObjectFlagType.OFT_MISC, ObjectFlagID.OFID_TIMED));
        props.add(flag(ObjectFlag.OF_TELEPATHY, ObjectFlagType.OFT_MISC, ObjectFlagID.OFID_WIELD));
        props.add(flag(ObjectFlag.OF_BLESSED, ObjectFlagType.OFT_MELEE, ObjectFlagID.OFID_WIELD));
        props.add(flag(ObjectFlag.OF_AFRAID, ObjectFlagType.OFT_BAD, ObjectFlagID.OFID_WIELD));
        props.add(flag(ObjectFlag.OF_IMPAIR_HP, ObjectFlagType.OFT_BAD, ObjectFlagID.OFID_TIMED));
        props.add(flag(ObjectFlag.OF_DIG_1, ObjectFlagType.OFT_DIG, ObjectFlagID.OFID_WIELD));
        props.add(flag(ObjectFlag.OF_THROWING, ObjectFlagType.OFT_THROW, ObjectFlagID.OFID_WIELD));
        props.add(flag(ObjectFlag.OF_MULTIPLY_WEIGHT, ObjectFlagType.OFT_CURSE_ONLY, ObjectFlagID.OFID_WIELD));
        props.add(flag(ObjectFlag.OF_NO_TELEPORT, ObjectFlagType.OFT_NONE, ObjectFlagID.OFID_NONE));
        props.add(new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectFlagType.OFT_NONE,
                ObjectFlagID.OFID_NONE,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_STAT, ObjectModifier.OM_STR),
                9, 13, null, "strength", "strong", "weak", null, null, null));
        ObjectRegistry.setObjectProperties(props);
    }

    // ---- by subtype ------------------------------------------------------------------------

    @Test
    @DisplayName("by id: OFID_WIELD selects exactly the on-wield flags")
    void byIdWield() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_TELEPATHY, ObjectFlag.OF_BLESSED, ObjectFlag.OF_AFRAID,
                        ObjectFlag.OF_DIG_1, ObjectFlag.OF_THROWING, ObjectFlag.OF_MULTIPLY_WEIGHT),
                mask(true, ObjectFlagID.OFID_WIELD));
    }

    @Test
    @DisplayName("by id: OFID_TIMED selects exactly the timed flags")
    void byIdTimed() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_REGEN, ObjectFlag.OF_IMPAIR_HP),
                mask(true, ObjectFlagID.OFID_TIMED));
    }

    @Test
    @DisplayName("by id: OFID_NORMAL selects exactly the on-effect flags")
    void byIdNormal() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_SUST_STR, ObjectFlag.OF_PROT_FEAR),
                mask(true, ObjectFlagID.OFID_NORMAL));
    }

    @Test
    @DisplayName("by id: two values give the union of both selections")
    void byIdTwoValues() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_REGEN, ObjectFlag.OF_IMPAIR_HP, ObjectFlag.OF_SUST_STR,
                        ObjectFlag.OF_PROT_FEAR),
                mask(true, ObjectFlagID.OFID_TIMED, ObjectFlagID.OFID_NORMAL));
    }

    @Test
    @DisplayName("by id: OFID_NONE selects the flag with no id-type, not the stat that shares the classification")
    void byIdNoneSkipsNonFlags() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_NO_TELEPORT), mask(true, ObjectFlagID.OFID_NONE));
    }

    @Test
    @DisplayName("by subtype: OFT_SUST selects the sustains")
    void bySubtypeSustain() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_SUST_STR), mask(false, ObjectFlagType.OFT_SUST));
    }

    @Test
    @DisplayName("by subtype: OFT_MISC selects every misc ability regardless of how it is identified")
    void bySubtypeMisc() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_REGEN, ObjectFlag.OF_TELEPATHY),
                mask(false, ObjectFlagType.OFT_MISC));
    }

    // ---- boundaries ------------------------------------------------------------------------

    @Test
    @DisplayName("by subtype: OFT_BAD selects the bad flags across id types")
    void bySubtypeBad() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_AFRAID, ObjectFlag.OF_IMPAIR_HP),
                mask(false, ObjectFlagType.OFT_BAD));
    }

    @Test
    @DisplayName("by subtype: several values give the union, as the curse-and-ego callers in C rely on")
    void bySubtypeSeveralValues() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_SUST_STR, ObjectFlag.OF_PROT_FEAR, ObjectFlag.OF_REGEN,
                        ObjectFlag.OF_TELEPATHY),
                mask(false, ObjectFlagType.OFT_PROT, ObjectFlagType.OFT_SUST, ObjectFlagType.OFT_MISC));
    }

    @Test
    @DisplayName("by subtype: OFT_NONE selects the flag with no subtype, not the stat")
    void bySubtypeNoneSkipsNonFlags() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_NO_TELEPORT), mask(false, ObjectFlagType.OFT_NONE));
    }

    @Test
    @DisplayName("by subtype: the rarely used subtypes each pick out their one flag")
    void bySubtypeSingletons() throws Exception {
        assertEquals(EnumSet.of(ObjectFlag.OF_BLESSED), mask(false, ObjectFlagType.OFT_MELEE));
        assertEquals(EnumSet.of(ObjectFlag.OF_DIG_1), mask(false, ObjectFlagType.OFT_DIG));
        assertEquals(EnumSet.of(ObjectFlag.OF_THROWING), mask(false, ObjectFlagType.OFT_THROW));
        assertEquals(EnumSet.of(ObjectFlag.OF_MULTIPLY_WEIGHT),
                mask(false, ObjectFlagType.OFT_CURSE_ONLY));
    }

    @Test
    @DisplayName("by subtype: a subtype with no flag in the registry gives an empty mask")
    void bySubtypeNoMatch() throws Exception {
        assertTrue(mask(false, ObjectFlagType.OFT_LIGHT).isEmpty());
    }

    // ---- where C and Java could diverge ----------------------------------------------------

    @Test
    @DisplayName("an empty list gives an empty mask, as an immediate OFT_MAX terminator does in C")
    void emptyList() throws Exception {
        assertTrue(mask(true).isEmpty());
        assertTrue(mask(false).isEmpty());
    }

    @Test
    @DisplayName("OFT_MAX, C's terminator, matches no property")
    void terminatorMatchesNothing() throws Exception {
        assertTrue(mask(false, ObjectFlagType.OFT_MAX).isEmpty());
    }

    @Test
    @DisplayName("listing a value twice changes nothing")
    void duplicateValues() throws Exception {
        assertEquals(mask(true, ObjectFlagID.OFID_WIELD),
                mask(true, ObjectFlagID.OFID_WIELD, ObjectFlagID.OFID_WIELD));
    }

    @Test
    @DisplayName("the order of the list does not matter")
    void orderIndependent() throws Exception {
        assertEquals(mask(false, ObjectFlagType.OFT_SUST, ObjectFlagType.OFT_BAD),
                mask(false, ObjectFlagType.OFT_BAD, ObjectFlagType.OFT_SUST));
    }

    /**
     * Every flag property is in exactly one id-type group, so the four id types together select all
     * eleven flag properties and nothing else - the stat, whose classification is also OFID_NONE,
     * does not make it twelve. The same holds by subtype.
     */
    @Test
    @DisplayName("the id types partition the flag properties, and the stat is in neither partition")
    void classificationsPartitionTheFlags() throws Exception {
        assertEquals(11, mask(true, ObjectFlagID.values()).size());
        assertEquals(11, mask(false, ObjectFlagType.values()).size());
    }

    // ---- helpers ---------------------------------------------------------------------------

    @Test
    @DisplayName("a family mismatch selects nothing: an OFT value with id true, or an OFID value with id false")
    void wrongFamilySelectsNothing() throws Exception {
        // C compares the two as bare ints, so OFT_SUST (1) with id true would match OFID_NORMAL
        // (1) - the on-effect flags. The Java compares by constant, so it matches nothing.
        assertTrue(mask(true, ObjectFlagType.OFT_SUST).isEmpty());
        assertTrue(mask(false, ObjectFlagID.OFID_NORMAL).isEmpty());
    }

    @Test
    @DisplayName("a mixed list honours the family each entry belongs to")
    void mixedFamiliesInOneList() throws Exception {
        // id true: the OFID entry applies, the OFT entry is inert.
        assertEquals(EnumSet.of(ObjectFlag.OF_REGEN, ObjectFlag.OF_IMPAIR_HP),
                mask(true, ObjectFlagType.OFT_SUST, ObjectFlagID.OFID_TIMED));
    }

    @Test
    @DisplayName("a property built without a classification (null) is skipped without throwing")
    void nullClassification() throws Exception {
        List<ObjectProperty> props = new ArrayList<>();
        props.add(new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_FLAG, null, null,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_FLAG, ObjectFlag.OF_REGEN),
                0, 0, null, "regen", null, null, null, null, null));
        ObjectRegistry.setObjectProperties(props);

        // The assembler never produces a null (an absent line becomes OFID_NONE/OFT_NONE), so this
        // is hand-built-property territory only; the note is that it does not NPE and, unlike C's
        // zeroed default, does not count as the NONE group either.
        assertDoesNotThrow(() -> mask(true, ObjectFlagID.OFID_NONE));
        assertTrue(mask(true, ObjectFlagID.OFID_NONE).isEmpty());
        assertTrue(mask(false, ObjectFlagType.OFT_NONE).isEmpty());
    }

    @Test
    @DisplayName("every call returns a fresh set; changing one result does not change the next")
    void freshResultEachCall() throws Exception {
        Flag<ObjectFlag> first = call(true, ObjectFlagID.OFID_WIELD);
        Flag<ObjectFlag> second = call(true, ObjectFlagID.OFID_WIELD);
        assertNotSame(first, second);

        first.on(ObjectFlag.OF_FEATHER);
        assertEquals(mask(true, ObjectFlagID.OFID_WIELD), set(second));
    }

    @Test
    @DisplayName("an empty registry gives an empty mask")
    void emptyRegistry() throws Exception {
        ObjectRegistry.setObjectProperties(new ArrayList<>());
        assertTrue(mask(true, ObjectFlagID.OFID_WIELD).isEmpty());
        assertTrue(mask(false, ObjectFlagType.OFT_SUST).isEmpty());
    }
}
