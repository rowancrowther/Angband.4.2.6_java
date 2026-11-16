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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the blank record {@link ObjectProperty#ObjectProperty()} builds, and the setters that fill it one line at a
 * time.
 *
 * <p>The expected values come from {@code parse_object_property_name} and the handlers after it in {@code obj-init.c}.
 * {@code parse_object_property_name} {@code mem_zalloc}s a {@code struct obj_property} (so every field is 0 or
 * {@code NULL}) and then sets every {@code type_mult} slot to 1. Each later handler writes one field and, for
 * {@code power:}, {@code mult:}, {@code adjective:}, {@code neg-adjective:}, {@code msg:}, {@code desc:} and
 * {@code type-mult:}, replaces whatever the field held, so a repeated line keeps the last. {@code type:} writes only
 * the type, and {@code bindui:} binds every line it reads. The full constructor is covered by
 * {@code ObjectPropertyAccessorsTest}; this class covers the other route.
 *
 * <p>Class ObjectPropertyBlankRecordTest coded on 261115, commented in full on 261115.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyBlankRecordTest {

    /**
     * What C's zero-fill and the {@code type_mult} pre-fill leave before any line after {@code name:} has arrived.
     */
    @Nested
    @DisplayName("a blank record")
    class Defaults {

        /**
         * The three enum fields are C's zero: {@code OBJ_PROPERTY_NONE}, {@code OFT_NONE} and {@code OFID_NONE}.
         */
        @Test
        @DisplayName("type, subtype and id type are C's zero")
        void enumsAreZero() {
            ObjectProperty blank = new ObjectProperty();

            assertSame(ObjPropertyType.OBJ_PROPERTY_NONE, blank.getType());
            assertSame(ObjectFlagType.OFT_NONE, blank.getSubtype());
            assertSame(ObjectFlagID.OFID_NONE, blank.getIdType());
        }

        /**
         * {@code power} and {@code mult} are 0 through both of their accessor names.
         */
        @Test
        @DisplayName("power and mult are zero")
        void numbersAreZero() {
            ObjectProperty blank = new ObjectProperty();

            assertEquals(0, blank.getPower());
            assertEquals(0, blank.getMult());
            assertEquals(0, blank.getMultiplier());
        }

        /**
         * The name and the four text fields are {@code NULL} in C, and {@code null} here, not {@code ""}.
         */
        @Test
        @DisplayName("the name and the text fields are null")
        void stringsAreNull() {
            ObjectProperty blank = new ObjectProperty();

            assertNull(blank.getName());
            assertNull(blank.getAdjective());
            assertNull(blank.getNegAdjective());
            assertNull(blank.getMessage());
            assertNull(blank.getNoticeMessage());
            assertNull(blank.getDescription());
        }

        /**
         * {@code index} is 0 in C. The port's equivalent is the NONE wrapper: type NONE, element
         * {@code ELEM_NONE}, and no flag or modifier.
         */
        @Test
        @DisplayName("the payload is the NONE wrapper")
        void payloadIsNone() {
            ObjectPropertyTypeWrapper payload = new ObjectProperty().getPayload();

            assertSame(ObjPropertyType.OBJ_PROPERTY_NONE, payload.getType());
            assertSame(ElementEnum.ELEM_NONE, payload.getElement(ObjPropertyType.OBJ_PROPERTY_RESIST));
            assertNull(payload.getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
            assertNull(payload.getFlag(ObjPropertyType.OBJ_PROPERTY_FLAG));
        }

        /**
         * Nothing is bound and no type has a figure of its own.
         */
        @Test
        @DisplayName("no bindings and no named multipliers")
        void collectionsAreEmpty() {
            ObjectProperty blank = new ObjectProperty();

            assertTrue(blank.getBoundEntries().isEmpty());
            assertTrue(blank.getTypeMults().isEmpty());
        }

        /**
         * C's pre-fill sets {@code type_mult[i] = 1} for every {@code i} below {@code TV_MAX}; the port's empty map
         * must answer 1 for every type, {@code TV_NONE} (C's slot 0) included.
         */
        @Test
        @DisplayName("every object type multiplies by one")
        void everyTypeIsOne() {
            ObjectProperty blank = new ObjectProperty();

            for (TValue tval : TValue.values()) {
                assertEquals(1, blank.getTypeMult(tval), "type " + tval + " should default to 1");
            }
        }

        /**
         * Two blank records must not share a map or a list, or a {@code bindui:} line on one record would bind the
         * next one too.
         */
        @Test
        @DisplayName("two blank records share no collection")
        void recordsAreIndependent() {
            ObjectProperty first = new ObjectProperty();
            ObjectProperty second = new ObjectProperty();

            first.putTypeMults(TValue.TV_RING, 3);
            first.addBoundEntry(new ObjectProperty.UIBinding("entry", null, false, null, null));

            assertNotSame(first.getTypeMults(), second.getTypeMults());
            assertNotSame(first.getBoundEntries(), second.getBoundEntries());
            assertEquals(1, second.getTypeMult(TValue.TV_RING));
            assertTrue(second.getBoundEntries().isEmpty());
        }
    }

    /**
     * The handlers that overwrite a single field.
     */
    @Nested
    @DisplayName("filling a record")
    class Setters {

        /**
         * Each scalar and enum setter is read back from its getter, with values the blank record does not already
         * hold so a setter that did nothing would fail.
         */
        @Test
        @DisplayName("each setter is read back")
        void settersRoundTrip() {
            ObjectProperty property = new ObjectProperty();
            ObjectPropertyTypeWrapper payload =
                    new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_BLOWS);

            property.setName("extra blows");
            property.setType(ObjPropertyType.OBJ_PROPERTY_MOD);
            property.setSubtype(ObjectFlagType.OFT_MELEE);
            property.setIdType(ObjectFlagID.OFID_WIELD);
            property.setPayload(payload);
            property.setPower(30);
            property.setMult(2);
            property.setAdjective("deadly");
            property.setNegAdjective("clumsy");
            property.setMessage("You feel more dangerous.");
            property.setDescription("Grants extra blows.");

            assertEquals("extra blows", property.getName());
            assertSame(ObjPropertyType.OBJ_PROPERTY_MOD, property.getType());
            assertSame(ObjectFlagType.OFT_MELEE, property.getSubtype());
            assertSame(ObjectFlagID.OFID_WIELD, property.getIdType());
            assertSame(payload, property.getPayload());
            assertEquals(30, property.getPower());
            assertEquals(2, property.getMult());
            assertEquals(2, property.getMultiplier());
            assertEquals("deadly", property.getAdjective());
            assertEquals("clumsy", property.getNegAdjective());
            assertEquals("You feel more dangerous.", property.getMessage());
            assertEquals("You feel more dangerous.", property.getNoticeMessage());
            assertEquals("Grants extra blows.", property.getDescription());
        }

        /**
         * C replaces the earlier value on a repeated {@code power:}, {@code mult:}, {@code adjective:},
         * {@code neg-adjective:}, {@code msg:} or {@code desc:} line, so the last wins, and a negative power is
         * legal.
         */
        @Test
        @DisplayName("a second value replaces the first")
        void lastValueWins() {
            ObjectProperty property = new ObjectProperty();

            property.setPower(30);
            property.setPower(-4);
            property.setMult(2);
            property.setMult(0);
            property.setAdjective("first");
            property.setAdjective("second");
            property.setNegAdjective("first");
            property.setNegAdjective("second");
            property.setMessage("first");
            property.setMessage("second");
            property.setDescription("first");
            property.setDescription("second");

            assertEquals(-4, property.getPower());
            assertEquals(0, property.getMult());
            assertEquals("second", property.getAdjective());
            assertEquals("second", property.getNegAdjective());
            assertEquals("second", property.getMessage());
            assertEquals("second", property.getDescription());
        }

        /**
         * {@code parse_object_property_type} writes only {@code prop->type}. A later {@code type:} line therefore
         * leaves the index (and here the payload) as {@code code:} set it.
         */
        @Test
        @DisplayName("a later type leaves the payload alone")
        void typeLeavesPayloadAlone() {
            ObjectProperty property = new ObjectProperty();
            ObjectPropertyTypeWrapper payload =
                    new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_BLOWS);
            property.setType(ObjPropertyType.OBJ_PROPERTY_MOD);
            property.setPayload(payload);

            property.setType(ObjPropertyType.OBJ_PROPERTY_STAT);

            assertSame(ObjPropertyType.OBJ_PROPERTY_STAT, property.getType());
            assertSame(payload, property.getPayload());
        }
    }

    /**
     * {@code parse_object_property_type_mult}, which writes {@code type_mult[tval]} for one type.
     */
    @Nested
    @DisplayName("type multipliers")
    class TypeMults {

        /**
         * A named type takes its figure and every other type stays at the pre-filled 1.
         */
        @Test
        @DisplayName("a named type changes only itself")
        void namedTypeChangesOnlyItself() {
            ObjectProperty property = new ObjectProperty();

            property.putTypeMults(TValue.TV_RING, 3);

            assertEquals(3, property.getTypeMult(TValue.TV_RING));
            assertEquals(1, property.getTypeMult(TValue.TV_AMULET));
            assertEquals(1, property.getTypeMult(TValue.TV_NONE));
        }

        /**
         * A second line for the same type replaces the figure, as the array write does.
         */
        @Test
        @DisplayName("a second line for a type replaces the first")
        void secondLineReplaces() {
            ObjectProperty property = new ObjectProperty();

            property.putTypeMults(TValue.TV_RING, 3);
            property.putTypeMults(TValue.TV_RING, 5);

            assertEquals(5, property.getTypeMult(TValue.TV_RING));
            assertEquals(1, property.getTypeMults().size());
        }

        /**
         * Zero and negative figures are stored as written. A zero is the way the data file prices an object type at
         * nothing, and must not fall through to the default of 1.
         */
        @Test
        @DisplayName("zero and negative figures are kept, not defaulted")
        void zeroAndNegativeAreKept() {
            ObjectProperty property = new ObjectProperty();

            property.putTypeMults(TValue.TV_BOW, 0);
            property.putTypeMults(TValue.TV_SHOT, -2);

            assertEquals(0, property.getTypeMult(TValue.TV_BOW));
            assertEquals(-2, property.getTypeMult(TValue.TV_SHOT));
        }

        /**
         * Replacing the whole map swaps the figures, and an empty replacement is neutral again.
         */
        @Test
        @DisplayName("replacing the map swaps every figure")
        void replacingTheMap() {
            ObjectProperty property = new ObjectProperty();
            property.putTypeMults(TValue.TV_RING, 3);
            Map<TValue, Integer> replacement = new HashMap<>();
            replacement.put(TValue.TV_BOW, 4);

            property.setTypeMults(replacement);

            assertSame(replacement, property.getTypeMults());
            assertEquals(4, property.getTypeMult(TValue.TV_BOW));
            assertEquals(1, property.getTypeMult(TValue.TV_RING));

            property.setTypeMults(new HashMap<>());

            assertEquals(1, property.getTypeMult(TValue.TV_BOW));
        }
    }

    /**
     * {@code parse_object_property_bindui}, which binds every {@code bindui:} line, where the full-constructor route
     * keeps one.
     */
    @Nested
    @DisplayName("bound UI entries")
    class BoundEntries {

        /**
         * Each line adds one binding, kept in file order, with the optional {@code uival} carried as {@code null}
         * when the line gave none (C's {@code have_value} false).
         */
        @Test
        @DisplayName("every bindui line adds a binding, in file order")
        void everyLineBinds() {
            ObjectProperty property = new ObjectProperty();
            ObjectProperty.UIBinding first = new ObjectProperty.UIBinding("compact_0", null, false, null, null);
            ObjectProperty.UIBinding second = new ObjectProperty.UIBinding("aux_1", 7, true, null, null);

            property.addBoundEntry(first);
            property.addBoundEntry(second);
            property.addBoundEntry(new ObjectProperty.UIBinding("compact_0", null, false, null, null));

            List<ObjectProperty.UIBinding> bound = property.getBoundEntries();
            assertEquals(3, bound.size());
            assertSame(first, bound.get(0));
            assertSame(second, bound.get(1));
            assertEquals(first, bound.get(2));
        }

        /**
         * Replacing the list drops what was there.
         */
        @Test
        @DisplayName("replacing the list drops the earlier bindings")
        void replacingTheList() {
            ObjectProperty property = new ObjectProperty();
            property.addBoundEntry(new ObjectProperty.UIBinding("old", null, false, null, null));
            List<ObjectProperty.UIBinding> replacement = new java.util.ArrayList<>();
            replacement.add(new ObjectProperty.UIBinding("new", 1, false, null, null));

            property.setBoundEntries(replacement);

            assertSame(replacement, property.getBoundEntries());
            assertEquals(1, property.getBoundEntries().size());
            assertEquals("new", property.getBoundEntries().getFirst().entry());
        }
    }
}
