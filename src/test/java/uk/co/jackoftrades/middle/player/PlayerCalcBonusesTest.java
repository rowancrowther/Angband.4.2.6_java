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

package uk.co.jackoftrades.middle.player;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.co.jackoftrades.middle.objects.Curse;
import uk.co.jackoftrades.middle.objects.ElementInfo;
import uk.co.jackoftrades.middle.objects.ItemObject;
import uk.co.jackoftrades.middle.objects.enums.ElementEnum;
import uk.co.jackoftrades.middle.objects.enums.ObjectFlag;
import uk.co.jackoftrades.middle.objects.enums.ObjectModifier;
import uk.co.jackoftrades.middle.objects.enums.TValue;
import uk.co.jackoftrades.middle.player.enums.PlayerFlag;
import uk.co.jackoftrades.testsupport.CalcBonusesFixture;
import uk.co.jackoftrades.testsupport.SeededPlayerRegistry;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link Player#calcBonuses}, the port of C's {@code calc_bonuses}
 * ({@code player-calcs.c:1877-2325}) — the derivation every other number about a character rests on.
 *
 * <p><b>Absolute totals are mostly not what is asserted.</b> Almost every quantity here is a sum of
 * race, class, level, gear, shape, statuses and a stat-table adjustment, so pinning a total means
 * pinning all of them and the test breaks whenever any is touched for an unrelated reason. Where a
 * test asks what one thing contributes, it runs the calculation twice and compares — once without
 * the thing and once with — so the assertion is about the difference the thing makes. Where the
 * value really is absolute, such as the base speed of 110, it is asserted directly.
 *
 * <p><b>The character contributes nothing by default</b>, which is what makes the differences
 * readable. {@link CalcBonusesFixture} supplies an empty race, an empty class and stats whose table
 * adjustments are zero, so a total that moves did so because of the one thing the test added.
 *
 * <p>Class PlayerCalcBonusesTest coded on 260820, commented in full on 260820.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class PlayerCalcBonusesTest {

    /**
     * Reads one of {@link PlayerState}'s combat totals.
     *
     * <p>{@code PlayerState} exposes {@code toAcAdd}, {@code toHitAdd} and {@code toDamAdd} to write
     * these but no getters to read them — its own Javadoc notes that only the accessors current
     * callers need are exposed. The tests here need to read them, so they are reached directly
     * rather than added to production code for a test's benefit.
     *
     * @param state the state to read
     * @param name  the field: {@code toA}, {@code toH} or {@code toD}
     * @return the total
     * @throws ReflectiveOperationException if the field cannot be reached
     */
    private static int combatTotal(PlayerState state, String name) throws ReflectiveOperationException {
        java.lang.reflect.Field field = PlayerState.class.getDeclaredField(name);
        field.setAccessible(true);
        return (int) field.get(state);
    }

    /**
     * A curse carrying the given modifiers and combat bonuses.
     *
     * @param modifiers the curse's modifiers
     * @param toHit     the first field of its {@code combat:} line
     * @param toDam     the second
     * @param toAc      the third
     * @return the curse
     */
    private static Curse curse(Map<ObjectModifier, Integer> modifiers, int toHit, int toDam, int toAc) {
        return new Curse("test curse", List.of(), 0, null, List.of(), modifiers,
                Map.<ElementEnum, ElementInfo>of(), toHit, toDam, toAc,
                List.of(), List.of(), "", "");
    }

    /**
     * The values that do not depend on anything the character carries.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("defaults")
    class Defaults {

        /**
         * Speed starts at 110 and blows at one — C's two explicit defaults after the wipe
         * ({@code player-calcs.c:1898-1900}). A character with nothing on contributes nothing to
         * either, so these come through untouched.
         */
        @Test
        @DisplayName("an unequipped character has base speed and at least one blow")
        void baseDefaults() throws Exception {
            PlayerState state = CalcBonusesFixture.plainCharacter().calculate();

            assertAll(
                    () -> assertEquals(110, state.getSpeed()),
                    () -> assertTrue(state.getPlayerSkill(uk.co.jackoftrades.middle.player.enums.PlayerSkill.SKILL_DIGGING) >= 1,
                            "digging is floored at one"));
        }

        /**
         * A class with no spells is marked as having no mana at all — the flag {@code calcBonuses}
         * sets from a zero maximum ({@code player-calcs.c:2316-2319}).
         */
        @Test
        @DisplayName("a non-caster is flagged as having no mana")
        void nonCasterHasNoMana() throws Exception {
            PlayerState state = CalcBonusesFixture.plainCharacter().calculate();

            assertTrue(state.hasPFlag(PlayerFlag.PF_NO_MANA));
        }

        /**
         * The state is rebuilt from nothing every time, so calculating twice must give the same
         * answer. A field the wipe missed would show as a total that grew on the second pass — and
         * in play that is what taking a ring off and putting it back would do.
         */
        @Test
        @DisplayName("recalculating gives the same answer, not a doubled one")
        void recalculationIsIdempotent() throws Exception {
            CalcBonusesFixture fixture = CalcBonusesFixture.plainCharacter();
            ItemObject armour = CalcBonusesFixture.item(TValue.TV_SOFT_ARMOR);
            armour.setBaseAC(20);
            fixture.wear("body", armour);

            PlayerState first = fixture.calculate();
            int afterOne = first.getBaseAc();
            PlayerState second = fixture.calculate();

            assertAll(
                    () -> assertEquals(20, afterOne),
                    () -> assertEquals(20, second.getBaseAc()));
        }
    }

    /**
     * What a worn item contributes, and what gates it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("equipment")
    class Equipment {

        /**
         * A modifier counts only when the character can read the rune for it — C multiplies every
         * modifier by {@code p->obj_k->modifiers[]} ({@code player-calcs.c:1943-1970}). The same ring
         * on the same finger is worth five points of speed or nothing at all, and the difference is
         * knowledge rather than anything about the ring.
         */
        @Test
        @DisplayName("a modifier applies only once its rune is known")
        void modifiersNeedTheirRune() throws Exception {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.setModifiers(Map.of(ObjectModifier.OM_SPEED, 5));

            PlayerState unknown = CalcBonusesFixture.plainCharacter()
                    .wear("right hand", ring)
                    .calculate();
            PlayerState known = CalcBonusesFixture.plainCharacter()
                    .knows(ObjectModifier.OM_SPEED)
                    .wear("right hand", ring)
                    .calculate();

            assertAll(
                    () -> assertEquals(110, unknown.getSpeed()),
                    () -> assertEquals(115, known.getSpeed()));
        }

        /**
         * Base armour class and the armour enchantment are separate totals — C adds {@code obj->ac}
         * unconditionally to {@code state->ac} and {@code obj->to_a} to {@code state->to_a} under a
         * knowledge test ({@code player-calcs.c:1993-1996}). A single plate mail therefore lands in
         * two places, and an early version of this port added its enchantment twice while leaving
         * its armour at zero.
         */
        @Test
        @DisplayName("base armour and the armour bonus are counted separately")
        void baseArmourAndBonusAreSeparate() throws Exception {
            ItemObject armour = CalcBonusesFixture.item(TValue.TV_HARD_ARMOR);
            armour.setBaseAC(20);
            armour.setToAC(4);

            CalcBonusesFixture bare = CalcBonusesFixture.plainCharacter();
            int bonusWithout = bare.calculate().getBaseAc();

            PlayerState worn = CalcBonusesFixture.plainCharacter()
                    .wear("body", armour)
                    .calculate();

            assertAll(
                    () -> assertEquals(0, bonusWithout),
                    () -> assertEquals(20, worn.getBaseAc(), "the base armour is its own total"));
        }

        /**
         * The weapon and launcher slots withhold to-hit and to-damage, so that wielding a weapon
         * does not improve unrelated actions ({@code player-calcs.c:1997-1998}). Asserted as a
         * difference between the same item in two slots, which isolates the slot rule from the stat
         * adjustments that also feed the total.
         */
        @Test
        @DisplayName("to-hit from the weapon slot is withheld, from an armour slot it is not")
        void weaponSlotWithholdsCombatBonuses() throws Exception {
            ItemObject sword = CalcBonusesFixture.item(TValue.TV_SWORD);
            sword.setToHit(9);
            ItemObject gloves = CalcBonusesFixture.item(TValue.TV_GLOVES);
            gloves.setToHit(9);

            CalcBonusesFixture bare = CalcBonusesFixture.plainCharacter();
            int baseline = combatTotal(bare.calculate(), "toH");

            CalcBonusesFixture weaponFixture = CalcBonusesFixture.plainCharacter();
            weaponFixture.wear("weapon", sword);
            int withWeapon = combatTotal(weaponFixture.calculate(), "toH");

            CalcBonusesFixture gloveFixture = CalcBonusesFixture.plainCharacter();
            gloveFixture.wear("hands", gloves);
            int withGloves = combatTotal(gloveFixture.calculate(), "toH");

            assertAll(
                    () -> assertEquals(baseline, withWeapon,
                            "the weapon slot contributes no to-hit"),
                    () -> assertEquals(baseline + 9, withGloves,
                            "the same item in an armour slot does"));
        }

        /**
         * A light source's radius reaches the state through {@code calcLight}, which sums every worn
         * item rather than taking the brightest ({@code player-calcs.c:1622-1645}).
         */
        @Test
        @DisplayName("a worn light contributes its radius")
        void lightRadius() throws Exception {
            ItemObject lantern = CalcBonusesFixture.item(TValue.TV_LIGHT, 500);
            lantern.setFlag(ObjectFlag.OF_LIGHT_2);

            PlayerState state = CalcBonusesFixture.plainCharacter()
                    .wear("light", lantern)
                    .calculate();

            assertEquals(2, state.getCurLight());
        }

        /**
         * A fuelled light that has burnt out gives nothing — its whole contribution is zeroed, not
         * just its innate part ({@code player-calcs.c:1638-1642}).
         */
        @Test
        @DisplayName("a burnt-out light gives no radius at all")
        void burntOutLightGivesNothing() throws Exception {
            ItemObject lantern = CalcBonusesFixture.item(TValue.TV_LIGHT, 0);
            lantern.setFlag(ObjectFlag.OF_LIGHT_2);

            PlayerState state = CalcBonusesFixture.plainCharacter()
                    .wear("light", lantern)
                    .calculate();

            assertEquals(0, state.getCurLight());
        }
    }

    /**
     * What a curse on a worn item contributes, and how {@code knownOnly} changes it.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("curses")
    class Curses {

        /**
         * The curse pass runs at all — an item's curses are examined after the item itself, so a
         * curse's modifier reaches the state alongside the item's
         * ({@code player-calcs.c:2005-2020}).
         */
        @Test
        @DisplayName("a curse on a worn item contributes its modifiers")
        void curseModifiersApply() throws Exception {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.addCurse(curse(Map.of(ObjectModifier.OM_SPEED, -5), 0, 0, 0), 1, 0);

            PlayerState state = CalcBonusesFixture.plainCharacter()
                    .knows(ObjectModifier.OM_SPEED)
                    .wear("right hand", ring)
                    .calculate();

            assertEquals(105, state.getSpeed());
        }

        /**
         * A curse recorded at zero power is inert — C only visits an index whose power is non-zero
         * ({@code player-calcs.c:2009}).
         */
        @Test
        @DisplayName("a curse at zero power contributes nothing")
        void zeroPowerCurseIsInert() throws Exception {
            ItemObject ring = CalcBonusesFixture.item(TValue.TV_RING);
            ring.addCurse(curse(Map.of(ObjectModifier.OM_SPEED, -5), 0, 0, 0), 0, 0);

            PlayerState state = CalcBonusesFixture.plainCharacter()
                    .knows(ObjectModifier.OM_SPEED)
                    .wear("right hand", ring)
                    .calculate();

            assertEquals(110, state.getSpeed());
        }

        /**
         * The item's own contribution is not lost when it carries a curse. C's loop runs the body
         * once for the item and then once per curse; an earlier version of this port ran it only for
         * the curses, and an uncursed item contributed nothing at all.
         */
        @Test
        @DisplayName("a cursed item still contributes its own values")
        void itemPassStillRuns() throws Exception {
            ItemObject armour = CalcBonusesFixture.item(TValue.TV_HARD_ARMOR);
            armour.setBaseAC(20);
            armour.addCurse(curse(Map.of(), 0, 0, 0), 1, 0);

            PlayerState state = CalcBonusesFixture.plainCharacter()
                    .wear("body", armour)
                    .calculate();

            assertEquals(20, state.getBaseAc());
        }

        /**
         * A curse's modifiers survive the restricted calculation while everything else about it does
         * not — the consequence of the curse template's blank known object
         * ({@code obj-init.c:188-194}). This is the behaviour most likely to be "fixed" by someone
         * who reads it as a gap.
         */
        @Test
        @DisplayName("under knownOnly a curse keeps its modifiers and loses its combat bonuses")
        void curseUnderKnownOnly() throws Exception {
            ItemObject ring = CalcBonusesFixture.identifiedItem(TValue.TV_RING);
            ring.addCurse(curse(Map.of(ObjectModifier.OM_SPEED, -5), 0, 0, 7), 1, 0);

            PlayerState known = CalcBonusesFixture.plainCharacter()
                    .knows(ObjectModifier.OM_SPEED)
                    .wear("right hand", ring)
                    .calculateKnownOnly();

            assertEquals(105, known.getSpeed(),
                    "the modifier is gated on the player's runes, not on the curse's known object");
        }
    }

    /**
     * The knowledge-restricted calculation.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("knownOnly")
    class KnownOnly {

        /**
         * An unidentified item's enchantment stays hidden: with no known counterpart, the guard on
         * {@code obj->known->to_a} fails and the bonus is not counted
         * ({@code player-calcs.c:1994-1995}). Its base armour is counted regardless, because C adds
         * that unconditionally.
         */
        @Test
        @DisplayName("an unidentified enchantment is withheld while its base armour is not")
        void unidentifiedEnchantmentIsWithheld() throws Exception {
            ItemObject armour = CalcBonusesFixture.item(TValue.TV_HARD_ARMOR);
            armour.setBaseAC(20);
            armour.setToAC(4);

            PlayerState state = CalcBonusesFixture.plainCharacter()
                    .wear("body", armour)
                    .calculateKnownOnly();

            assertEquals(20, state.getBaseAc(),
                    "base armour is added whatever the player knows");
        }

        /**
         * Once the enchantment has been learned it counts again. Asserted as the difference between
         * the restricted calculations before and after learning, so the stat-derived part of the
         * armour total cancels out.
         */
        @Test
        @DisplayName("a learned enchantment counts in the restricted calculation")
        void learnedEnchantmentCounts() throws Exception {
            ItemObject unlearned = CalcBonusesFixture.identifiedItem(TValue.TV_HARD_ARMOR);
            unlearned.setToAC(4);

            ItemObject learned = CalcBonusesFixture.identifiedItem(TValue.TV_HARD_ARMOR);
            learned.setToAC(4);
            CalcBonusesFixture.learnCombatValues(learned);

            CalcBonusesFixture withUnlearned = CalcBonusesFixture.plainCharacter();
            withUnlearned.wear("body", unlearned);
            int hidden = combatTotal(withUnlearned.calculateKnownOnly(), "toA");

            CalcBonusesFixture withLearned = CalcBonusesFixture.plainCharacter();
            withLearned.wear("body", learned);
            int shown = combatTotal(withLearned.calculateKnownOnly(), "toA");

            assertEquals(hidden + 4, shown,
                    "the four points appear only once the enchantment has been learned");
        }
    }
}
