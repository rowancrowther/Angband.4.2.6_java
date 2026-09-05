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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.enums.ProjectionEnum;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.effect.Effect;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeEnum;
import uk.co.jackoftradesltd.middle.effect.EffectSubTypeWrapper;
import uk.co.jackoftradesltd.middle.enums.DamageAspect;
import uk.co.jackoftradesltd.middle.enums.EffectEnum;
import uk.co.jackoftradesltd.middle.enums.ElementInfoEnum;
import uk.co.jackoftradesltd.middle.game.globals.GameConstants;
import uk.co.jackoftradesltd.middle.game.globals.data.GameConstantsData;
import uk.co.jackoftradesltd.middle.game.globals.data.ObjMakeData;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests {@link ObjectUtils#objectPrep}, the port of C's {@code object_prep} ({@code obj-make.c:817}).
 *
 * <p>Every kind here is built on {@link ItemFixture#kindWithDice}, so {@code toH}/{@code toD}/
 * {@code toA} are never null; each test then overwrites whichever dice it is asking about with a
 * value distinct from every other field's, so a transposition between two adjacent assignments —
 * the same risk {@link ObjectKindAccessorsTest} was written against — would show up as a wrong
 * number rather than passing by accident. Every dice term uses {@link DamageAspect#MAXIMIZE}, whose
 * {@code randCalc} is a pure function of the dice ({@code base + dice*sides + mBonus}, C's
 * {@code EXTREMIFY}/{@code MAXIMIZE} branch) and never reads {@link GameConstants} or
 * {@code WorldRegistry} — unlike {@code RANDOMIZE} and {@code AVERAGE}, which
 * {@link ObjectUtilsCopyCursesTest} has to seed global state to exercise. The one exception is the
 * light-fuel default, which reads {@link GameConstants} directly regardless of aspect, and is seeded
 * for accordingly.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsObjectPrepTest {

    /**
     * The {@code GameConstants.data} in place before this class replaced it.
     */
    private static Object savedConstants;

    /**
     * Seeds just the {@code objMake} fuel constants the light-source branch reads — everything else
     * {@code objectPrep} touches in these tests is {@link DamageAspect#MAXIMIZE}, which never
     * consults {@link GameConstants}.
     */
    @BeforeAll
    static void seedFuelConstants() {
        ObjMakeData objMake = new ObjMakeData(0, 0, 0, 5000, 0, 7500);
        GameConstantsData seed = new GameConstantsData(
                null, null, null, null, null, null, null, objMake, null,
                null, null, null, null, null, null, null, null);
        savedConstants = setStatic(GameConstants.class, "data", seed);
    }

    /**
     * Puts back whatever {@code GameConstants.data} held before {@link #seedFuelConstants()}.
     */
    @AfterAll
    static void restoreConstants() {
        setStatic(GameConstants.class, "data", savedConstants);
    }

    /**
     * Writes a private static field, returning its previous value.
     *
     * @param owner the declaring class
     * @param name  the declared field name
     * @param value the value to write
     * @return the value the field held beforehand
     */
    private static Object setStatic(Class<?> owner, String name, Object value) {
        try {
            Field field = owner.getDeclaredField(name);
            field.setAccessible(true);
            Object previous = field.get(null);
            field.set(null, value);
            return previous;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(owner.getSimpleName() + "." + name
                    + " is no longer settable by reflection", e);
        }
    }

    /**
     * A minimal effect, distinguishable only by identity — {@code objectPrep} is meant to share the
     * kind's effect list rather than copy it, so identity is all any test here needs.
     *
     * @return the effect
     */
    private static Effect anEffect() {
        return new Effect(EffectEnum.EF_NONE, new Random(0, 0, 0, 1, false), "", 0, 0,
                EffectSubTypeEnum.EST_NONE, new EffectSubTypeWrapper(ProjectionEnum.PROJ_ACID),
                0, 0, new Random(0, 0, 0, 1, false), new ArrayList<>(), "");
    }

    /**
     * A kind of the given type with distinct, deterministic dice for {@code toH}/{@code toD}/
     * {@code toA} (sides 4/6/8) so the three combat bonuses cannot pass by a transposition, and an
     * otherwise-empty modifier map (the constructor {@link ItemFixture#kindWithDice} uses already
     * allocates one).
     *
     * @param tValue the kind's item type
     * @return the kind
     */
    private static ObjectKind kind(TValue tValue) {
        ObjectKind kind = ItemFixture.kindWithDice(tValue);
        set(kind, "toH", new Random(0, 0, 1, 4, false));
        set(kind, "toD", new Random(0, 0, 1, 6, false));
        set(kind, "toA", new Random(0, 0, 1, 8, false));
        return kind;
    }

    /**
     * The plain, non-dice fields copied straight across, and the two collections
     * ({@code effect}/{@code number}) whose semantics ({@code stored, not copied}/{@code always 1})
     * are not otherwise exercised.
     */
    @Nested
    @DisplayName("the plain fields")
    class PlainFields {

        /**
         * A sword hits none of {@code objectPrep}'s conditional branches — not a wand or staff, not a
         * potion/edible/fuel/launcher, not a light — so this isolates the unconditional field copy
         * from every branch that follows it.
         */
        @Test
        @DisplayName("kind, tval, sval, ac, damage dice, weight, effect and number are all copied")
        void plainFieldsCopied() {
            ObjectKind kind = kind(TValue.TV_SWORD);
            set(kind, "ac", 5);
            set(kind, "damageDice", 2);
            set(kind, "damageSides", 3);
            kind.setWeight(7);
            kind.setsVal(9);
            Effect effect = anEffect();
            kind.getEffect().add(effect);

            ItemObject obj = new ItemObject();

            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertSame(kind, obj.getKind());
            assertEquals(TValue.TV_SWORD, obj.gettValue());
            assertEquals(9, obj.getsValue());
            assertEquals(5, obj.getBaseAC());
            assertEquals(2, obj.getDamageDice());
            assertEquals(3, obj.getDamageSides());
            assertEquals(7, obj.getWeight());
            assertEquals(1, obj.getNumber());
            assertSame(kind.getEffect(), obj.getEffect(),
                    "C's obj->effect = k->effect shares the same list, not a copy");
            assertTrue(obj.getEffect().contains(effect));
        }

        /**
         * A sword takes neither pval branch and is not a light, so a bare {@link ItemObject}'s
         * zeroed {@code pval}/{@code timeout} must survive untouched.
         */
        @Test
        @DisplayName("a weapon's pval and timeout stay at zero")
        void weaponHasNoPvalOrTimeout() {
            ObjectKind kind = kind(TValue.TV_SWORD);

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(0, obj.getpValue());
            assertEquals(0, obj.getTimeout());
        }
    }

    /**
     * The flag copy — C's redundant {@code of_copy(obj->flags, k->base->flags)} immediately
     * overwritten by {@code of_copy(obj->flags, k->flags)}, so only the kind's own flags ever reach
     * the object. See {@link ObjectUtils#objectPrep}'s Javadoc for the full reasoning.
     */
    @Nested
    @DisplayName("the flag copy")
    class FlagCopy {

        /**
         * A flag set only on the kind's base — which C's second, overwriting {@code of_copy} call
         * always erases before the object ever sees it — must not appear on the prepared object,
         * while a flag the kind carries directly must.
         */
        @Test
        @DisplayName("only the kind's own flags reach the object; the base's are never applied")
        void baseFlagsAreNotApplied() {
            ObjectBase base = new ObjectBase(TValue.TV_SWORD, "edged", null,
                    new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, 40);
            set(base, "flags", new Flag<>(ObjectFlag.class, ObjectFlag.OF_IMPAIR_HP));

            ObjectKind kind = kind(TValue.TV_SWORD);
            set(kind, "base", base);
            set(kind, "flags", new Flag<>(ObjectFlag.class, ObjectFlag.OF_SEE_INVIS));

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertTrue(obj.hasFlag(ObjectFlag.OF_SEE_INVIS), "the kind's own flag must carry across");
            assertFalse(obj.hasFlag(ObjectFlag.OF_IMPAIR_HP), "the base's flag must not");
        }
    }

    /**
     * The modifier loop — every real {@link ObjectModifier} rolled from the kind's dice, with a
     * modifier the kind never mentions falling back to zero.
     */
    @Nested
    @DisplayName("the modifier loop")
    class Modifiers {

        /**
         * A modifier the kind carries rolls its own dice; one it does not falls back to
         * {@link ObjectKind#getModifier}'s zero-value default, matching C's zero-initialised
         * {@code k->modifiers[i]} for a slot {@code object.txt} never named.
         */
        @Test
        @DisplayName("a carried modifier rolls its dice; an uncarried one comes back zero")
        void modifiersRollOrFallBackToZero() {
            ObjectKind kind = kind(TValue.TV_SWORD);
            Map<ObjectModifier, Random> modifiers = new HashMap<>();
            modifiers.put(ObjectModifier.OM_STR, new Random(0, 0, 1, 2, false));
            set(kind, "modifiers", modifiers);

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(2, obj.getModifiers().get(ObjectModifier.OM_STR));
            assertEquals(0, obj.getModifiers().get(ObjectModifier.OM_DEX));
        }
    }

    /**
     * The two mutually exclusive pval sources — a wand or staff rolls its pval from the kind's
     * charge dice, while a potion/edible/fuel/launcher rolls it from the kind's own pval dice.
     * Neither tval satisfies the other's check, so C's two {@code if}s (and this port's) never both
     * fire for the same object.
     */
    @Nested
    @DisplayName("the pval branches")
    class PvalBranches {

        /**
         * A wand's pval is its charge count — C's {@code tval_can_have_charges(obj)} branch.
         */
        @Test
        @DisplayName("a wand's pval comes from the kind's charge dice")
        void wandPvalFromCharge() {
            ObjectKind kind = kind(TValue.TV_WAND);
            set(kind, "charge", new Random(0, 0, 1, 12, false));

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(12, obj.getpValue());
        }

        /**
         * A potion's pval is the kind's own pval dice — C's
         * {@code tval_is_potion(obj) || ...} branch.
         */
        @Test
        @DisplayName("a potion's pval comes from the kind's own pval dice")
        void potionPvalFromPVal() {
            ObjectKind kind = kind(TValue.TV_POTION);
            set(kind, "pVal", new Random(0, 0, 1, 10, false));

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(10, obj.getpValue());
        }
    }

    /**
     * The light-source default fuel — the one branch that reads {@link GameConstants} directly,
     * regardless of the {@link DamageAspect} in force.
     */
    @Nested
    @DisplayName("the light-source default fuel")
    class LightFuel {

        /**
         * {@code OF_BURNS_OUT} (a torch) is seeded to the fuel-torch constant.
         */
        @Test
        @DisplayName("a torch is given the fuel-torch default")
        void torchGetsFuelTorchDefault() {
            ObjectKind kind = kind(TValue.TV_LIGHT);
            set(kind, "flags", new Flag<>(ObjectFlag.class, ObjectFlag.OF_BURNS_OUT));

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(5000, obj.getTimeout());
        }

        /**
         * {@code OF_TAKES_FUEL} (a lamp) is seeded to the default-lamp constant instead.
         */
        @Test
        @DisplayName("a lamp is given the default-lamp default")
        void lampGetsDefaultLampDefault() {
            ObjectKind kind = kind(TValue.TV_LIGHT);
            set(kind, "flags", new Flag<>(ObjectFlag.class, ObjectFlag.OF_TAKES_FUEL));

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(7500, obj.getTimeout());
        }

        /**
         * A light with neither flag - an artifact light such as a magical lantern with unlimited
         * fuel, say - is left at the timeout a fresh {@link ItemObject} already has.
         */
        @Test
        @DisplayName("a light with neither fuel flag keeps its zero timeout")
        void unfuelledLightStaysAtZero() {
            ObjectKind kind = kind(TValue.TV_LIGHT);

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(0, obj.getTimeout());
        }
    }

    /**
     * The three combat bonus dice, each rolled from the kind's own dice with a value distinct from
     * the other two - the same transposition risk {@link ObjectKindAccessorsTest.CombatDice} was
     * written against.
     */
    @Nested
    @DisplayName("to-hit, to-damage and to-AC")
    class CombatBonuses {

        @Test
        @DisplayName("each bonus is rolled from its own dice, not one of the others'")
        void bonusesDoNotCrossOver() {
            ObjectKind kind = kind(TValue.TV_SWORD);

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertEquals(4, obj.getToHit());
            assertEquals(6, obj.getToDam());
            assertEquals(8, obj.getToAC());
        }
    }

    /**
     * Slays and brands are folded on from the kind - the wiring only, since
     * {@link ObjectUtilsCopySlaysTest} and {@link ObjectUtilsCopyBrandsTest} already cover
     * {@link ObjectUtils#copySlays} and {@link ObjectUtils#copyBrands} themselves. Curses are not
     * exercised here: every kind's curse map is empty (as {@link ItemFixture#kindWithDice} leaves
     * it), and {@link ObjectUtilsCopyCursesTest} already covers {@link ObjectUtils#copyCurses}, whose
     * timeout roll always uses {@link DamageAspect#RANDOMIZE} regardless of what is passed here and
     * so needs the world-depth tables seeded — a cost this class does not otherwise pay.
     */
    @Nested
    @DisplayName("slays and brands")
    class SlaysAndBrands {

        @Test
        @DisplayName("a slay and a brand on the kind both reach the object")
        void slaysAndBrandsCopiedFromKind() {
            ObjectKind kind = kind(TValue.TV_SWORD);
            Slay slay = new Slay("EVIL_2", "evil", null, "smite", "pierces",
                    null, 2, 2, 5);
            Brand brand = new Brand("FIRE", "fire", "burns", null, null, 3, 3, 5);
            Set<Slay> slays = new HashSet<>();
            slays.add(slay);
            Set<Brand> brands = new HashSet<>();
            brands.add(brand);
            set(kind, "slays", slays);
            set(kind, "brands", brands);

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            assertTrue(obj.getSlays().stream().anyMatch(s -> s.sameMonsterSlain(slay)));
            assertTrue(obj.getBrands().stream().anyMatch(b -> b.getName().equals("fire")));
        }
    }

    /**
     * The per-element resistances - each entry starts as a copy of the kind's own {@code el_info},
     * and the kind's base contributes its flags as a union, never overwriting what the kind already
     * set. This is the boundary stage 1 found broken: {@link ItemObject#setElInfo} used to clear the
     * incoming map before storing it, so every element came back empty regardless of what
     * {@code objectPrep} had just built.
     */
    @Nested
    @DisplayName("per-element resistances")
    class ElementInfo_ {

        /**
         * {@code ELEM_ACID}, present on both the kind and its base, ends up with the kind's
         * resistance level and the union of both sides' flags - C's
         * {@code el_info[i].flags = k->el_info[i].flags; el_info[i].flags |= k->base->el_info[i].flags;}.
         * {@code ELEM_FIRE}, named by neither, comes back at the zero default both sides would give
         * C's zero-initialised array.
         */
        @Test
        @DisplayName("an element on both kind and base gets the kind's level and the union of both flag sets")
        void elementInfoUnionsKindAndBaseFlags() {
            ObjectBase base = new ObjectBase(TValue.TV_SWORD, "edged", null,
                    new Flag<>(ObjectKindFlag.class), new Flag<>(ElementEnum.class), 0, 40);
            base.getElementMap().put(ElementEnum.ELEM_ACID, new uk.co.jackoftradesltd.middle.objects.ElementInfo());
            base.getElementMap().get(ElementEnum.ELEM_ACID).getFlags().on(ElementInfoEnum.EL_INFO_HATES);

            ObjectKind kind = kind(TValue.TV_SWORD);
            set(kind, "base", base);
            uk.co.jackoftradesltd.middle.objects.ElementInfo kindAcid = new uk.co.jackoftradesltd.middle.objects.ElementInfo();
            kindAcid.setResLevel(3);
            kindAcid.on(ElementInfoEnum.EL_INFO_IGNORE);
            set(kind, "elInfo", new HashMap<>(Map.of(ElementEnum.ELEM_ACID, kindAcid)));

            ItemObject obj = new ItemObject();
            ObjectUtils.objectPrep(obj, kind, 1, DamageAspect.MAXIMIZE);

            uk.co.jackoftradesltd.middle.objects.ElementInfo objAcid = obj.getElInfo().get(ElementEnum.ELEM_ACID);
            assertEquals(3, objAcid.getResLevel());
            assertTrue(objAcid.has(ElementInfoEnum.EL_INFO_IGNORE), "the kind's own flag must survive");
            assertTrue(objAcid.has(ElementInfoEnum.EL_INFO_HATES), "the base's flag must be unioned in, not lost");

            uk.co.jackoftradesltd.middle.objects.ElementInfo objFire = obj.getElInfo().get(ElementEnum.ELEM_FIRE);
            assertEquals(0, objFire.getResLevel());
            assertFalse(objFire.has(ElementInfoEnum.EL_INFO_HATES));
            assertFalse(objFire.has(ElementInfoEnum.EL_INFO_IGNORE));
        }
    }
}
