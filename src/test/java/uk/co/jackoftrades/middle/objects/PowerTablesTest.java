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

package uk.co.jackoftrades.middle.objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlagType;
import uk.co.jackoftrades.middle.objects.enums.ResType;
import uk.co.jackoftrades.middle.objects.enums.TValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests the four power lookup tables — {@link Archery}, {@link ElementPowers}, {@link ElementSet}
 * and {@link FlagSet} — which are the port of the static tables at the head of C's
 * {@code obj-power.c}.
 *
 * <p>They are data holders, so the tests are mostly that each figure comes back from the accessor it
 * was given to. That is worth asserting rather than assuming: three of the four classes take five or
 * more same-typed {@code int}s in a row, so a transposed pair in a constructor would compile, load
 * and silently misprice every object in the game. The rows built here carry C's real values, so a
 * transposition would have to be a plausible one to survive.
 *
 * <p>The two set tables also carry a mutable count, which the power calculation zeroes and
 * increments in place rather than treating as loaded data; those tests cover that it behaves as
 * working state.
 *
 * @author Rowan Crowther
 */
class PowerTablesTest {

    /**
     * The launcher-and-ammo assumptions, C's {@code archery[]} ({@code obj-power.c:52}).
     */
    @Nested
    @DisplayName("Archery")
    class ArcheryRows {

        /**
         * C's bolt row: {@code {TV_BOLT, 14, 9, 7}}. Every field is a small integer and three of
         * them sit adjacent, so each is checked against its own accessor.
         */
        @Test
        @DisplayName("each figure comes back from its own accessor")
        void fieldsRoundTrip() {
            Archery bolt = new Archery(TValue.TV_BOLT, 14, 9, 7);

            assertEquals(TValue.TV_BOLT, bolt.getAmmoType());
            assertEquals(14, bolt.getAmmoDamage());
            assertEquals(9, bolt.getLaunchDamage());
            assertEquals(7, bolt.getLaunchMult());
        }

        /**
         * The three real rows differ in ammo damage and multiplier but share a launcher bonus of 9,
         * which is the shape of C's table and worth pinning: a row that came back all-equal would
         * mean the wrong field was being read.
         */
        @Test
        @DisplayName("the three rows differ where C's differ and agree where C's agree")
        void rowsMatchCsShape() {
            Archery shot = new Archery(TValue.TV_SHOT, 10, 9, 4);
            Archery arrow = new Archery(TValue.TV_ARROW, 12, 9, 5);
            Archery bolt = new Archery(TValue.TV_BOLT, 14, 9, 7);

            assertNotEquals(shot.getAmmoDamage(), arrow.getAmmoDamage());
            assertNotEquals(arrow.getAmmoDamage(), bolt.getAmmoDamage());
            assertNotEquals(shot.getLaunchMult(), bolt.getLaunchMult());

            assertEquals(9, shot.getLaunchDamage());
            assertEquals(9, arrow.getLaunchDamage());
            assertEquals(9, bolt.getLaunchDamage());
        }
    }

    /**
     * What one element is worth at each level of protection, C's {@code el_powers[]}
     * ({@code obj-power.c:112}).
     */
    @Nested
    @DisplayName("ElementPowers")
    class ElementPowerRows {

        /**
         * C's fire row: {@code {"fire", T_LRES, 3, -6, 6, 40}}. The four power figures are adjacent
         * ints in the constructor, so each is read back separately.
         */
        @Test
        @DisplayName("a low-resist row returns each of its four prices")
        void lowResistRow() {
            ElementPowers fire = new ElementPowers(ElementEnum.ELEM_FIRE, "fire", ResType.T_LRES, 3, -6, 6, 40);

            assertEquals(ElementEnum.ELEM_FIRE, fire.getElement());
            assertEquals("fire", fire.getName());
            assertEquals(ResType.T_LRES, fire.getType());
            assertEquals(3, fire.getIgnorePower());
            assertEquals(-6, fire.getVulnPower());
            assertEquals(6, fire.getResPower());
            assertEquals(40, fire.getImPower());
        }

        /**
         * C's high elements carry a resistance price only; the other three figures are zero. Poison
         * is the extreme case at 28, far above any low resist, which is why the two groups are
         * counted separately.
         */
        @Test
        @DisplayName("a high-resist row prices resistance only")
        void highResistRow() {
            ElementPowers poison = new ElementPowers(ElementEnum.ELEM_POIS, "poison", ResType.T_HRES, 0, 0, 28, 0);

            assertEquals(ResType.T_HRES, poison.getType());
            assertEquals(28, poison.getResPower());
            assertEquals(0, poison.getIgnorePower());
            assertEquals(0, poison.getVulnPower());
            assertEquals(0, poison.getImPower());
        }

        /**
         * A vulnerability is priced negatively, which is what makes it subtract when the power
         * calculation adds it. Asserting the sign guards against a row loaded with its magnitude
         * only.
         */
        @Test
        @DisplayName("vulnerability is priced negatively")
        void vulnerabilityIsNegative() {
            ElementPowers acid = new ElementPowers(ElementEnum.ELEM_ACID, "acid", ResType.T_LRES, 3, -6, 5, 38);

            assertEquals(-6, acid.getVulnPower());
        }
    }

    /**
     * The elemental combinations worth more together than apart, C's {@code element_sets[]}
     * ({@code obj-power.c:93}).
     */
    @Nested
    @DisplayName("ElementSet")
    class ElementSetRows {

        /**
         * C's low resists row: {@code {T_LRES, 1, 1, 10, 4, 0, "low resists"}}. Six of the seven
         * arguments are ints or a same-typed enum, so every one is read back.
         */
        @Test
        @DisplayName("each figure comes back from its own accessor")
        void fieldsRoundTrip() {
            ElementSet lowResists = new ElementSet(ResType.T_LRES, 1, 1, 10, 4, 0, "low resists");

            assertEquals(ResType.T_LRES, lowResists.getType());
            assertEquals(1, lowResists.getResLevel());
            assertEquals(1, lowResists.getFactor());
            assertEquals(10, lowResists.getBonus());
            assertEquals(4, lowResists.getSize());
            assertEquals(0, lowResists.getCount());
            assertEquals("low resists", lowResists.getDescription());
        }

        /**
         * The immunities row demands resistance level 3 where the resist rows demand 1 — the field
         * that decides which elements count towards which row.
         */
        @Test
        @DisplayName("the immunities row demands a higher level than the resist rows")
        void immunitiesDemandLevelThree() {
            ElementSet immunities = new ElementSet(ResType.T_LRES, 3, 6, 20000, 4, 0, "immunities");
            ElementSet highResists = new ElementSet(ResType.T_HRES, 1, 2, 10, 9, 0, "high resists");

            assertEquals(3, immunities.getResLevel());
            assertEquals(1, highResists.getResLevel());
        }

        /**
         * The count is working state: the power calculation zeroes every row, increments as it walks
         * the elements, and reads the totals back afterwards. This is that cycle in miniature.
         */
        @Test
        @DisplayName("the count is writable working state, not loaded data")
        void countIsWorkingState() {
            ElementSet row = new ElementSet(ResType.T_LRES, 1, 1, 10, 4, 3, "low resists");

            assertEquals(3, row.getCount(), "the constructor's count is honoured");

            row.setCount(0);
            assertEquals(0, row.getCount(), "a caller can zero it before a pass");

            row.setCount(row.getCount() + 1);
            row.setCount(row.getCount() + 1);
            assertEquals(2, row.getCount(), "and increment it as the pass proceeds");
        }
    }

    /**
     * The flag families worth more together than apart, C's {@code flag_sets[]}
     * ({@code obj-power.c:71}).
     */
    @Nested
    @DisplayName("FlagSet")
    class FlagSetRows {

        /**
         * C's protections row: {@code {OFT_PROT, 3, 15, 4, 0, "protections"}}.
         */
        @Test
        @DisplayName("each figure comes back from its own accessor")
        void fieldsRoundTrip() {
            FlagSet protections = new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections");

            assertEquals(ObjectFlagType.OFT_PROT, protections.getType());
            assertEquals(3, protections.getFactor());
            assertEquals(15, protections.getBonus());
            assertEquals(4, protections.getSize());
            assertEquals(0, protections.getCount());
            assertEquals("protections", protections.getDescription());
        }

        /**
         * The three rows differ in every figure but the starting count, which is what makes a
         * mis-assigned constructor argument visible rather than harmless.
         */
        @Test
        @DisplayName("the three rows carry C's differing weights")
        void rowsMatchCsShape() {
            FlagSet sustains = new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains");
            FlagSet protections = new FlagSet(ObjectFlagType.OFT_PROT, 3, 15, 4, 0, "protections");
            FlagSet misc = new FlagSet(ObjectFlagType.OFT_MISC, 1, 25, 8, 0, "misc abilities");

            assertEquals(5, sustains.getSize());
            assertEquals(4, protections.getSize());
            assertEquals(8, misc.getSize());

            assertEquals(3, protections.getFactor(), "protections are the row with the heavier factor");
            assertEquals(25, misc.getBonus(), "misc abilities carry the largest full-set bonus");
        }

        /**
         * As with the element rows, the count is working state rather than data.
         */
        @Test
        @DisplayName("the count is writable working state, not loaded data")
        void countIsWorkingState() {
            FlagSet row = new FlagSet(ObjectFlagType.OFT_SUST, 1, 10, 5, 0, "sustains");

            row.setCount(4);
            assertEquals(4, row.getCount());

            row.setCount(0);
            assertEquals(0, row.getCount());
        }
    }
}
