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
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjPropertyType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagID;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectProperty}'s pricing accessors, and the multipliers on {@link Brand} and
 * {@link Slay} that sit beside them.
 *
 * <p>The one with behaviour rather than storage is {@link ObjectProperty#getTypeMult(TValue)}, and
 * its default is the point: C fills every slot of its {@code type_mult} array with 1 before parsing
 * any {@code type-mult:} line ({@code obj-init.c:3186}), so a type the data file does not name is
 * priced <em>normally</em> rather than at nothing. A default of 0 would silently zero the flag and
 * modifier terms for most objects in the game, and nothing else would report it.
 *
 * @author Rowan Crowther
 */
class ObjectPropertyAccessorsTest {

    /**
     * An object property with the given power, ability multiplier and per-type multipliers.
     *
     * @param power     the base power
     * @param mult      the ability-bonus multiplier
     * @param typeMults the per-type multipliers the data file named
     * @return the property
     */
    private static ObjectProperty property(int power, int mult, Map<TValue, Integer> typeMults) {
        return new ObjectProperty(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectFlagType.OFT_MISC,
                ObjectFlagID.OFID_WIELD,
                new ObjectPropertyTypeWrapper(ObjPropertyType.OBJ_PROPERTY_MOD, ObjectModifier.OM_BLOWS),
                power, mult, typeMults,
                "extra blows", "deadly", "clumsy", "You feel more dangerous.",
                "Grants extra blows.", List.of());
    }

    /**
     * The per-type multiplier, which decides what a property is worth on a given kind of object.
     */
    @Nested
    @DisplayName("getTypeMult")
    class TypeMultiplier {

        /**
         * A named type gets the figure the data file gave it. Extra blows really are worth three
         * times as much on a ring as on a weapon, which is the kind of asymmetry this table exists
         * to express.
         */
        @Test
        @DisplayName("a named type gets its own multiplier")
        void namedTypeUsesItsFigure() {
            ObjectProperty blows = property(0, 0, Map.of(TValue.TV_RING, 3, TValue.TV_BOW, 0));

            assertEquals(3, blows.getTypeMult(TValue.TV_RING));
            assertEquals(0, blows.getTypeMult(TValue.TV_BOW));
        }

        /**
         * An unnamed type multiplies by one — priced normally, not at nothing. This is the fallback
         * C builds by pre-filling its array.
         */
        @Test
        @DisplayName("an unnamed type multiplies by one")
        void unnamedTypeDefaultsToOne() {
            ObjectProperty blows = property(0, 0, Map.of(TValue.TV_RING, 3));

            assertEquals(1, blows.getTypeMult(TValue.TV_SWORD));
            assertEquals(1, blows.getTypeMult(TValue.TV_POTION));
        }

        /**
         * A property naming no types at all multiplies by one everywhere, which is the common case:
         * most properties carry no {@code type-mult:} line.
         */
        @Test
        @DisplayName("a property with no type multipliers is neutral everywhere")
        void noMultipliersIsNeutral() {
            ObjectProperty plain = property(5, 0, Map.of());

            for (TValue tval : TValue.values()) {
                assertEquals(1, plain.getTypeMult(tval), "unnamed " + tval + " should price normally");
            }
        }

        /**
         * {@code TV_NONE} is the type a curse object carries, and it is never named in
         * {@code object_property.txt} — so a curse's flags and modifiers are priced at the default,
         * which is what the curse pricing relies on.
         */
        @Test
        @DisplayName("the curse object's type falls back to the default")
        void curseTypeUsesTheDefault() {
            ObjectProperty blows = property(0, 0, Map.of(TValue.TV_RING, 3));

            assertEquals(1, blows.getTypeMult(TValue.TV_NONE));
        }
    }

    /**
     * The stored figures, each read back from its own accessor. Two of them are {@code int}s that
     * mean quite different things, which is why both are checked.
     */
    @Nested
    @DisplayName("stored figures")
    class StoredFigures {

        /**
         * The power is the base figure the type multiplier scales; the multiplier is how heavily the
         * property counts towards the combined ability bonus. They are unrelated, and adjacent in
         * the constructor.
         */
        @Test
        @DisplayName("power and ability multiplier are distinct")
        void powerAndMultiplierAreDistinct() {
            ObjectProperty blows = property(30, 2, Map.of());

            assertEquals(30, blows.getPower());
            assertEquals(2, blows.getMultiplier());
        }

        /**
         * The notice message is what the player is told when the property shows itself.
         */
        @Test
        @DisplayName("the notice message comes back")
        void noticeMessage() {
            assertEquals("You feel more dangerous.", property(0, 0, Map.of()).getNoticeMessage());
        }

        /**
         * The payload says which property this is, and carries the discriminator with it so that it
         * cannot be read as the wrong kind.
         */
        @Test
        @DisplayName("the payload identifies the property")
        void payloadIdentifiesTheProperty() {
            ObjectProperty blows = property(0, 0, Map.of());

            assertSame(ObjPropertyType.OBJ_PROPERTY_MOD, blows.getType());
            assertEquals(ObjectModifier.OM_BLOWS,
                    blows.getPayload().getModifier(ObjPropertyType.OBJ_PROPERTY_MOD));
        }
    }

    /**
     * A UI binding, the record pairing an entry with the value it should display.
     */
    @Nested
    @DisplayName("UIBinding")
    class Bindings {

        /**
         * The record's three components come back as given, including a null value — which means
         * "this entry takes no number", not "unset".
         */
        @Test
        @DisplayName("a binding carries its entry, value and auxiliary flag")
        void bindingCarriesItsParts() {
            ObjectProperty.UIBinding withValue = new ObjectProperty.UIBinding(null, 3, false);
            ObjectProperty.UIBinding withoutValue = new ObjectProperty.UIBinding(null, null, true);

            assertEquals(3, withValue.value());
            assertNull(withoutValue.value());
            assertTrue(withoutValue.aux());
        }

        /**
         * The value component is nullable on purpose, and a binding built without one is still a
         * valid binding — the entry simply displays no number.
         */
        @Test
        @DisplayName("two bindings differing only in their value are not equal")
        void bindingsCompareByComponents() {
            ObjectProperty.UIBinding withValue = new ObjectProperty.UIBinding(null, 3, false);
            ObjectProperty.UIBinding withoutValue = new ObjectProperty.UIBinding(null, null, false);

            assertEquals(withValue, new ObjectProperty.UIBinding(null, 3, false));
            assertTrue(!withValue.equals(withoutValue));
        }
    }

    /**
     * The brand and slay multipliers, which the power calculation reads alongside their powers.
     */
    @Nested
    @DisplayName("brand and slay multipliers")
    class Multipliers {

        /**
         * A brand carries two multipliers — the standard combat one and the O-combat one — and the
         * accessor answers the standard one. They are adjacent {@code int}s in the constructor.
         */
        @Test
        @DisplayName("a brand's multiplier is the standard-combat figure")
        void brandMultiplier() {
            Brand fire = new Brand("FIRE_2", "fire", "burns",
                    MonsterRaceFlag.RF_IM_FIRE, MonsterRaceFlag.RF_HURT_FIRE, 2, 3, 20);

            assertEquals(2, fire.getMultiplier());
            assertEquals(20, fire.getPower());
        }

        /**
         * A slay's multiplier decides whether it counts as an ordinary slay or as a kill: three or
         * less is a slay, more is a kill, and the power calculation counts the two separately.
         */
        @Test
        @DisplayName("a slay's multiplier separates slays from kills")
        void slayMultiplier() {
            Slay evil = new Slay("EVIL_3", "evil", null, "smites", "smites",
                    MonsterRaceFlag.RF_EVIL, 3, 3, 15);
            Slay kill = new Slay("EVIL_5", "evil", null, "smites", "smites",
                    MonsterRaceFlag.RF_EVIL, 5, 5, 25);

            assertTrue(evil.getMultiplier() <= 3, "three or less is an ordinary slay");
            assertTrue(kill.getMultiplier() > 3, "more than three makes it a kill");
            assertEquals(15, evil.getPower());
        }
    }
}
