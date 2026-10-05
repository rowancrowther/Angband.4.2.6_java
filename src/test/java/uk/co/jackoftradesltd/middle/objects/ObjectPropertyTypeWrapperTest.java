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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.game.globals.registry.ObjectRegistry;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link ObjectPropertyTypeWrapper}, the typed stand-in for C's {@code obj_property->index}.
 *
 * <p>The expected values come from {@code lookup_obj_property()} in {@code obj-properties.c} and
 * {@code parse_object_property_code()} in {@code obj-init.c}. C's lookup is two clauses: the type and
 * the index must both match, or the request is for a mod and the property is a stat with the same
 * index. The wrapper stands in for the index only, so everything it has to get right is that the
 * index half of those clauses compares by payload and never by type, and that the family each
 * constructor and getter accepts matches the table the C parser indexes into.
 *
 * <p>Class ObjectPropertyTypeWrapperTest commented in full on 261005.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyTypeWrapperTest {

    private static final ObjPropertyType STAT = ObjPropertyType.OBJ_PROPERTY_STAT;
    private static final ObjPropertyType MOD = ObjPropertyType.OBJ_PROPERTY_MOD;
    private static final ObjPropertyType FLAG = ObjPropertyType.OBJ_PROPERTY_FLAG;
    private static final ObjPropertyType IGNORE = ObjPropertyType.OBJ_PROPERTY_IGNORE;
    private static final ObjPropertyType RESIST = ObjPropertyType.OBJ_PROPERTY_RESIST;
    private static final ObjPropertyType VULN = ObjPropertyType.OBJ_PROPERTY_VULN;
    private static final ObjPropertyType IMM = ObjPropertyType.OBJ_PROPERTY_IMM;

    /**
     * An object property over the given payload, tagged with the given type, with nothing else set.
     *
     * @param type    the property's category
     * @param payload the property's index
     * @return the property
     */
    private static ObjectProperty property(ObjPropertyType type, ObjectPropertyTypeWrapper payload) {
        return new ObjectProperty(type, ObjectFlagType.OFT_MISC, ObjectFlagID.OFID_WIELD, payload,
                0, 0, Map.of(), "n", "a", "na", "m", "d", List.of());
    }

    /**
     * Each constructor stores the discriminator and its own payload slot, and leaves the others
     * empty.
     */
    @Nested
    @DisplayName("constructors")
    class Constructors {

        /**
         * A stat and a mod both index {@code obj_mods} in C, so both are stored as a modifier.
         */
        @Test
        void statAndModStoreTheModifier() {
            ObjectPropertyTypeWrapper stat = new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STR);
            ObjectPropertyTypeWrapper mod = new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STEALTH);

            assertEquals(STAT, stat.getType());
            assertSame(ObjectModifier.OM_STR, stat.getModifier(STAT));
            assertEquals(MOD, mod.getType());
            assertSame(ObjectModifier.OM_STEALTH, mod.getModifier(MOD));
        }

        /**
         * A flag is indexed into {@code obj_flags}.
         */
        @Test
        void flagStoresTheFlag() {
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR);

            assertEquals(FLAG, wrapper.getType());
            assertSame(ObjectFlag.OF_SUST_STR, wrapper.getFlag(FLAG));
        }

        /**
         * All four element relations index {@code element_names} in C, so each is stored as an
         * element and keeps its own discriminator.
         */
        @Test
        void eachElementRelationStoresTheElement() {
            for (ObjPropertyType type : List.of(IGNORE, RESIST, VULN, IMM)) {
                ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(type, ElementEnum.ELEM_FIRE);

                assertEquals(type, wrapper.getType());
                assertSame(ElementEnum.ELEM_FIRE, wrapper.getElement(type));
            }
        }

        /**
         * The modifier constructor takes only stat and mod. A flag type, an element type and the two
         * sentinels are all refused, where C would have looked the code up in the wrong table.
         */
        @Test
        void modifierConstructorRefusesEveryOtherType() {
            for (ObjPropertyType type : List.of(FLAG, IGNORE, RESIST, VULN, IMM,
                    ObjPropertyType.OBJ_PROPERTY_NONE, ObjPropertyType.OBJ_PROPERTY_MAX)) {
                assertThrows(InvalidParameterException.class,
                        () -> new ObjectPropertyTypeWrapper(type, ObjectModifier.OM_STR), type.name());
            }
        }

        /**
         * The flag constructor takes only {@code OBJ_PROPERTY_FLAG}.
         */
        @Test
        void flagConstructorRefusesEveryOtherType() {
            for (ObjPropertyType type : List.of(STAT, MOD, IGNORE, RESIST, VULN, IMM,
                    ObjPropertyType.OBJ_PROPERTY_NONE, ObjPropertyType.OBJ_PROPERTY_MAX)) {
                assertThrows(InvalidParameterException.class,
                        () -> new ObjectPropertyTypeWrapper(type, ObjectFlag.OF_SUST_STR), type.name());
            }
        }

        /**
         * The element constructor takes only the four element relations.
         */
        @Test
        void elementConstructorRefusesEveryOtherType() {
            for (ObjPropertyType type : List.of(STAT, MOD, FLAG,
                    ObjPropertyType.OBJ_PROPERTY_NONE, ObjPropertyType.OBJ_PROPERTY_MAX)) {
                assertThrows(InvalidParameterException.class,
                        () -> new ObjectPropertyTypeWrapper(type, ElementEnum.ELEM_FIRE), type.name());
            }
        }
    }

    /**
     * The getters check the family of the type asked for, not the stored type.
     */
    @Nested
    @DisplayName("typed getters")
    class Getters {

        /**
         * Stats count as mods: asking a stat wrapper for its modifier as a mod, or as a stat, both
         * work, as do the reverse. This is the property {@code OBJ_PROPERTY_MOD} lookups on the
         * stat entries of {@code object_property.txt} depend on.
         */
        @Test
        void modifierGetterAcceptsStatAndModOnEitherWrapper() {
            ObjectPropertyTypeWrapper stat = new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_INT);
            ObjectPropertyTypeWrapper mod = new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_INFRA);

            assertSame(ObjectModifier.OM_INT, stat.getModifier(MOD));
            assertSame(ObjectModifier.OM_INT, stat.getModifier(STAT));
            assertSame(ObjectModifier.OM_INFRA, mod.getModifier(STAT));
            assertSame(ObjectModifier.OM_INFRA, mod.getModifier(MOD));
        }

        /**
         * Any of the four element relations opens the element slot, whichever one the wrapper was
         * built with.
         */
        @Test
        void elementGetterAcceptsAnyElementRelation() {
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(RESIST, ElementEnum.ELEM_ACID);

            for (ObjPropertyType type : List.of(IGNORE, RESIST, VULN, IMM)) {
                assertSame(ElementEnum.ELEM_ACID, wrapper.getElement(type), type.name());
            }
        }

        /**
         * A getter asked for a type outside its family refuses, whatever the wrapper holds.
         */
        @Test
        void gettersRefuseTypesOutsideTheirFamily() {
            ObjectPropertyTypeWrapper stat = new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STR);
            ObjectPropertyTypeWrapper flag = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR);
            ObjectPropertyTypeWrapper element = new ObjectPropertyTypeWrapper(IMM, ElementEnum.ELEM_FIRE);

            assertThrows(InvalidParameterException.class, () -> stat.getModifier(FLAG));
            assertThrows(InvalidParameterException.class, () -> stat.getModifier(IGNORE));
            assertThrows(InvalidParameterException.class, () -> flag.getFlag(MOD));
            assertThrows(InvalidParameterException.class, () -> flag.getFlag(ObjPropertyType.OBJ_PROPERTY_NONE));
            assertThrows(InvalidParameterException.class, () -> element.getElement(MOD));
            assertThrows(InvalidParameterException.class, () -> element.getElement(FLAG));
            assertThrows(InvalidParameterException.class,
                    () -> element.getElement(ObjPropertyType.OBJ_PROPERTY_MAX));
        }

        /**
         * A getter in the right family on a wrapper holding another kind of payload answers
         * {@code null} rather than throwing, because only the requested type is validated.
         */
        @Test
        void gettersAnswerNullForAnotherKindOfPayload() {
            ObjectPropertyTypeWrapper flag = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR);
            ObjectPropertyTypeWrapper stat = new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STR);

            assertNull(flag.getModifier(MOD));
            assertNull(flag.getElement(RESIST));
            assertNull(stat.getFlag(FLAG));
            assertNull(stat.getElement(IMM));
        }
    }

    /**
     * Equality is the index half of {@code lookup_obj_property()}.
     */
    @Nested
    @DisplayName("equals")
    class Equality {

        /**
         * C's special case: a stat property is found by a mod request, because the index is all
         * that is compared there. The same modifier tagged stat and tagged mod must be equal.
         */
        @Test
        void sameModifierEqualsWhetherTaggedStatOrMod() {
            ObjectPropertyTypeWrapper stat = new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STR);
            ObjectPropertyTypeWrapper mod = new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STR);

            assertEquals(stat, mod);
            assertEquals(mod, stat);
        }

        /**
         * Element relations are told apart by the element alone, as {@code obj_property->index}
         * would be, so the same element under a different relation compares equal and the type
         * test is left to the lookup.
         */
        @Test
        void sameElementEqualsAcrossRelationsAndDifferentElementsDiffer() {
            ObjectPropertyTypeWrapper resistFire = new ObjectPropertyTypeWrapper(RESIST, ElementEnum.ELEM_FIRE);
            ObjectPropertyTypeWrapper immFire = new ObjectPropertyTypeWrapper(IMM, ElementEnum.ELEM_FIRE);
            ObjectPropertyTypeWrapper resistCold = new ObjectPropertyTypeWrapper(RESIST, ElementEnum.ELEM_COLD);

            assertEquals(resistFire, immFire);
            assertNotEquals(resistFire, resistCold);
        }

        /**
         * Two wrappers built over the same flag are equal, and over different flags are not.
         */
        @Test
        void flagsCompareByFlag() {
            ObjectPropertyTypeWrapper one = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR);
            ObjectPropertyTypeWrapper same = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR);
            ObjectPropertyTypeWrapper other = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_INT);

            assertEquals(one, same);
            assertNotEquals(one, other);
        }

        /**
         * Wrappers of different families are never equal: C separates them by the type clause, and
         * the wrapper separates them because each holds its payload in a different slot.
         */
        @Test
        void differentFamiliesAreNeverEqual() {
            ObjectPropertyTypeWrapper mod = new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STR);
            ObjectPropertyTypeWrapper flag = new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR);
            ObjectPropertyTypeWrapper element = new ObjectPropertyTypeWrapper(RESIST, ElementEnum.ELEM_FIRE);

            assertNotEquals(mod, flag);
            assertNotEquals(flag, mod);
            assertNotEquals(mod, element);
            assertNotEquals(flag, element);
        }

        /**
         * The ordinary contract: reflexive, and unequal to {@code null} and to a foreign object.
         */
        @Test
        void reflexiveAndUnequalToNullAndForeignObjects() {
            ObjectPropertyTypeWrapper wrapper = new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STR);

            assertEquals(wrapper, wrapper);
            assertNotEquals(null, wrapper);
            assertNotEquals("OM_STR", wrapper);
            assertNotEquals(ObjectModifier.OM_STR, wrapper);
        }
    }

    /**
     * {@code lookup_obj_property()} end to end, which is the only consumer of the wrapper's equality.
     * The registry is a static list, so it is saved and put back around each test.
     */
    @Nested
    @DisplayName("lookupObjectProperty")
    class Lookup {

        private List<ObjectProperty> saved;
        private ObjectProperty strStat;
        private ObjectProperty stealthMod;
        private ObjectProperty sustStr;
        private ObjectProperty resistFire;
        private ObjectProperty immFire;

        @BeforeEach
        void loadProperties() {
            try {
                saved = List.copyOf(ObjectRegistry.getObjectProperties());
            } catch (NullPointerException ex) {
                saved = null;
            }
            strStat = property(STAT, new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STR));
            stealthMod = property(MOD, new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STEALTH));
            sustStr = property(FLAG, new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR));
            resistFire = property(RESIST, new ObjectPropertyTypeWrapper(RESIST, ElementEnum.ELEM_FIRE));
            immFire = property(IMM, new ObjectPropertyTypeWrapper(IMM, ElementEnum.ELEM_FIRE));
            ObjectRegistry.setObjectProperties(List.of(strStat, stealthMod, sustStr, resistFire, immFire));
        }

        @AfterEach
        void restoreProperties() {
            if (saved != null) ObjectRegistry.setObjectProperties(saved);
        }

        /**
         * A stat property is found by a stat request and, by the special case, by a mod request.
         */
        @Test
        void statIsFoundAsStatAndAsMod() {
            assertSame(strStat, ObjectRegistry.lookupObjectProperty(STAT,
                    new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STR)));
            assertSame(strStat, ObjectRegistry.lookupObjectProperty(MOD,
                    new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STR)));
        }

        /**
         * The special case runs one way only: a genuine mod is not found by a stat request.
         */
        @Test
        void modIsNotFoundAsStat() {
            assertNull(ObjectRegistry.lookupObjectProperty(STAT,
                    new ObjectPropertyTypeWrapper(STAT, ObjectModifier.OM_STEALTH)));
            assertSame(stealthMod, ObjectRegistry.lookupObjectProperty(MOD,
                    new ObjectPropertyTypeWrapper(MOD, ObjectModifier.OM_STEALTH)));
        }

        /**
         * The same element under two relations resolves to the property of the relation asked for,
         * because the property's own type is compared as well as its payload.
         */
        @Test
        void sameElementResolvesByRelation() {
            assertSame(resistFire, ObjectRegistry.lookupObjectProperty(RESIST,
                    new ObjectPropertyTypeWrapper(RESIST, ElementEnum.ELEM_FIRE)));
            assertSame(immFire, ObjectRegistry.lookupObjectProperty(IMM,
                    new ObjectPropertyTypeWrapper(IMM, ElementEnum.ELEM_FIRE)));
            assertNull(ObjectRegistry.lookupObjectProperty(VULN,
                    new ObjectPropertyTypeWrapper(VULN, ElementEnum.ELEM_FIRE)));
        }

        /**
         * A flag is found, and a flag nobody defined is not, which is C's {@code NULL}.
         */
        @Test
        void flagFoundAndMissingFlagIsNull() {
            assertSame(sustStr, ObjectRegistry.lookupObjectProperty(FLAG,
                    new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_STR)));
            assertNull(ObjectRegistry.lookupObjectProperty(FLAG,
                    new ObjectPropertyTypeWrapper(FLAG, ObjectFlag.OF_SUST_INT)));
        }
    }
}
