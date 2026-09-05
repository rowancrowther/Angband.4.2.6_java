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
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.cave.Loc;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.CombatRunes;
import uk.co.jackoftradesltd.middle.objects.enums.ElementEnum;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectOriginEnum;
import uk.co.jackoftradesltd.middle.objects.enums.RuneVariety;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ObjectKnowledge#objectHasRune}, the port of C's {@code object_has_rune}
 * ({@code obj-knowledge.c:609}), and {@link ObjectKnowledge#objectFlavourTried}, the port of
 * C's {@code object_flavor_tried} ({@code obj-knowledge.c:2350}).
 *
 * <p>Every case is expected against what the C branch it corresponds to would do, not against
 * this port's own behaviour — the combat-rune cases in particular are here because the to-hit
 * branch once read {@code getToHit() != 0} where C reads {@code !object_has_standard_to_h(obj)},
 * a divergence a mirrored test would never have caught.
 *
 * <p>Items are built with the long constructor rather than the no-argument one, and with live
 * mutable collections in every slot the no-argument constructor leaves {@code null} — matching
 * what {@code ObjectUtils}'s object-creation path actually hands a played item, where every
 * element up to {@code ELEM_MAX} is present (C's zero-filled {@code el_info} array has the same
 * guarantee) and {@code modifiers}/{@code flags} are never bare references. That is what lets each
 * case call the item's own adders and setters directly instead of reaching past them.
 *
 * <p>Class ObjectKnowledgeTest coded on 260905, commented in full on 260905.
 *
 * @author Rowan Crowther
 */
class ObjectKnowledgeTest {

    /**
     * Writes a private field on an {@link ObjectKind}, since it has no setter for {@code toH} —
     * a real kind gets it from the {@code object.txt} parser, which this suite does not run. Mirrors
     * {@code ItemObjectStandardToHTest}'s helper of the same purpose.
     *
     * @param kind  the kind to modify
     * @param name  the declared field name
     * @param value the value to write
     */
    private static void poke(ObjectKind kind, String name, Object value) throws Exception {
        Field f = ObjectKind.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(kind, value);
    }

    /**
     * Reads a private field off an {@link ObjectKind}, since {@code tried} has a setter but no
     * getter — C reads {@code kind->tried} straight off the struct, which this mirrors.
     *
     * @param kind the kind to inspect
     * @param name the declared field name
     * @return the field's current value
     */
    private static boolean peek(ObjectKind kind, String name) throws Exception {
        Field f = ObjectKind.class.getDeclaredField(name);
        f.setAccessible(true);
        return f.getBoolean(kind);
    }

    /**
     * A kind whose to-hit is the fixed value given, built with the integer constructor rather than
     * {@link Random#parseStr} so the test does not depend on the dice parser.
     *
     * @param toH the kind's fixed to-hit figure
     * @return a kind declaring that to-hit and nothing else
     */
    private static ObjectKind kindWithFixedToH(int toH) throws Exception {
        ObjectKind kind = new ObjectKind();
        poke(kind, "toH", toH < 0
                ? new Random(-toH, 0, 0, 0, true)
                : new Random(toH, 0, 0, 0, false));
        return kind;
    }

    /**
     * An item of the given type and kind, with every collection field live and empty rather than
     * the shared immutables the no-argument constructor would leave — see the class comment for why
     * that matters here.
     *
     * @param tValue the item type
     * @param kind   the kind this item is an instance of, or {@code null} for none
     * @return the item, with toAC/toDam/toHit all at zero
     */
    private static ItemObject item(TValue tValue, ObjectKind kind) {
        return new ItemObject(kind, null, null, null, Loc.zero, tValue, 0, "0",
                0, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new LinkedHashMap<>(),
                List.of(), null, List.of(), "0", 0, 1,
                new Flag<>(ObjectNotice.class), 0, 0,
                ObjectOriginEnum.ORIGIN_NONE, 0, null, "");
    }

    /**
     * A minimal curse, distinguishable from another only by identity — {@link Curse} declares no
     * {@code equals}, which is exactly what the curse-rune branch relies on for its key lookup.
     *
     * @param name carried only so a failure names the curse
     * @return a curse with every other field empty
     */
    private static Curse curse(String name) {
        return new Curse(name, List.of(), 0, null, new Flag<>(ObjectFlag.class), java.util.Map.of(),
                java.util.Map.of(), 0, 0, 0, List.of(), new Flag<>(ObjectFlag.class), "", "");
    }

    /**
     * The to-armour and to-damage combat runes. Both test their figure against zero directly, so
     * there is no standard-value trap to fall into and each needs only the ordinary two cases.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("combat rune: to-armour and to-damage")
    class CombatToAAndToD {

        @Test
        @DisplayName("to-armour is present once the figure is nonzero")
        void toArmourNonzero() {
            ItemObject obj = item(TValue.TV_SWORD, kindZero());
            Rune rune = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A));

            assertFalse(ObjectKnowledge.objectHasRune(obj, rune));

            obj.setToAC(3);
            assertTrue(ObjectKnowledge.objectHasRune(obj, rune));
        }

        @Test
        @DisplayName("to-armour is present for a penalty as well as a bonus")
        void toArmourNegative() {
            ItemObject obj = item(TValue.TV_SWORD, kindZero());
            obj.setToAC(-2);

            assertTrue(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_A))));
        }

        @Test
        @DisplayName("to-damage is present once the figure is nonzero")
        void toDamageNonzero() {
            ItemObject obj = item(TValue.TV_SWORD, kindZero());
            Rune rune = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_D));

            assertFalse(ObjectKnowledge.objectHasRune(obj, rune));

            obj.setToDam(4);
            assertTrue(ObjectKnowledge.objectHasRune(obj, rune));
        }

        private ObjectKind kindZero() {
            try {
                return kindWithFixedToH(0);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }
    }

    /**
     * The to-hit combat rune — the branch this pass exists to pin down, since it is the one that
     * cannot be answered by comparing against zero. C asks {@code !object_has_standard_to_h(obj)};
     * this suite exercises exactly the cases {@code ItemObjectStandardToHTest} names as the ones a
     * plain non-zero test gets wrong.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("combat rune: to-hit")
    class CombatToH {

        private final Rune toHitRune = new Rune(new RuneVariety.CombatKey(CombatRunes.COMBAT_RUNE_TO_H));

        /**
         * The exact scenario the fix addressed: Chain Mail as {@code object.txt} writes it, a kind
         * to-hit of -2 that every hauberk has and none of them earned. Before the fix, a plain
         * {@code getToHit() != 0} read this as enchantment; C, and now this port, do not.
         */
        @Test
        @DisplayName("body armour at its kind's built-in penalty has no rune")
        void bodyArmourAtKindPenaltyHasNoRune() throws Exception {
            ItemObject chainMail = item(TValue.TV_HARD_ARMOR, kindWithFixedToH(-2));
            chainMail.setToHit(-2);

            assertFalse(ObjectKnowledge.objectHasRune(chainMail, toHitRune));
        }

        @Test
        @DisplayName("body armour moved off its kind's penalty has the rune")
        void bodyArmourAwayFromKindPenaltyHasRune() throws Exception {
            ItemObject enchantedChainMail = item(TValue.TV_HARD_ARMOR, kindWithFixedToH(-2));
            enchantedChainMail.setToHit(0);

            assertTrue(ObjectKnowledge.objectHasRune(enchantedChainMail, toHitRune));
        }

        @Test
        @DisplayName("a non-armour item at zero to-hit has no rune")
        void nonArmourAtZeroHasNoRune() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD, kindWithFixedToH(0));

            assertFalse(ObjectKnowledge.objectHasRune(sword, toHitRune));
        }

        @Test
        @DisplayName("a non-armour item with any to-hit has the rune")
        void nonArmourNonzeroHasRune() throws Exception {
            ItemObject sword = item(TValue.TV_SWORD, kindWithFixedToH(0));
            sword.setToHit(3);

            assertTrue(ObjectKnowledge.objectHasRune(sword, toHitRune));
        }

        /**
         * C's hack for curse object structures — {@code if (!obj->kind) return true;} inside
         * {@code object_has_standard_to_h} — reached the same way here: no kind means standard,
         * whatever the figure, so the rune is never reported.
         */
        @Test
        @DisplayName("an item with no kind has no rune, whatever its to-hit")
        void noKindHasNoRuneWhateverTheFigure() {
            ItemObject bare = item(TValue.TV_SWORD, null);
            bare.setToHit(-5);

            assertFalse(ObjectKnowledge.objectHasRune(bare, toHitRune));
        }
    }

    /**
     * Object modifiers — read straight off {@code obj->modifiers[r->index]} in C, so the only thing
     * worth pinning is that the branch checks the specific modifier the rune names, not merely
     * whether the object carries any modifier at all.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("modifier rune")
    class ModifierRune {

        @Test
        @DisplayName("is absent when the object carries no value for that modifier")
        void absentWhenUnset() {
            ItemObject obj = item(TValue.TV_RING, null);

            assertFalse(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, null))));
        }

        @Test
        @DisplayName("is present once the object carries a nonzero value for it")
        void presentWhenSet() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setModifiers(java.util.Map.of(ObjectModifier.OM_STR, 2));

            assertTrue(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ModKey(ObjectModifier.OM_STR, null))));
        }

        @Test
        @DisplayName("checks only the modifier the rune names, not any other the object carries")
        void checksTheNamedModifierOnly() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setModifiers(java.util.Map.of(ObjectModifier.OM_STR, 2));

            assertFalse(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ModKey(ObjectModifier.OM_DEX, null))));
        }
    }

    /**
     * Elemental resistances — {@code obj->el_info[r->index].res_level != 0} in C. The port's map is
     * sparse in general, but every element the resist runes cover is populated by the time a real
     * object exists ({@code ObjectUtils}'s creation path fills {@code ELEM_NONE}..{@code ELEM_MAX}
     * unconditionally, matching C's zero-filled array), so the fixtures here set every element they
     * touch explicitly rather than leaving one implicitly absent.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("resistance rune")
    class ResistanceRune {

        @Test
        @DisplayName("is absent at resistance level zero")
        void absentAtZero() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setElInfoResLevel(ElementEnum.ELEM_FIRE, 0);

            assertFalse(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null))));
        }

        @Test
        @DisplayName("is present at a positive resistance level")
        void presentWhenResisted() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setElInfoResLevel(ElementEnum.ELEM_FIRE, 3);

            assertTrue(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null))));
        }

        @Test
        @DisplayName("is present at a negative (vulnerable) resistance level")
        void presentWhenVulnerable() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setElInfoResLevel(ElementEnum.ELEM_FIRE, -1);

            assertTrue(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_FIRE, null))));
        }

        @Test
        @DisplayName("checks only the element the rune names")
        void checksTheNamedElementOnly() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setElInfoResLevel(ElementEnum.ELEM_FIRE, 3);
            obj.setElInfoResLevel(ElementEnum.ELEM_COLD, 0);

            assertFalse(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.ResistKey(ElementEnum.ELEM_COLD, null))));
        }
    }

    /**
     * Brand runes — matched by name across the whole set the object carries, mirroring how
     * {@link Rune#initRunes()} groups brands that share a name into one representative rune.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("brand rune")
    class BrandRune {

        @Test
        @DisplayName("is absent when the object carries no brand at all")
        void absentWithNoBrands() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Brand fire = new Brand("FIRE", "fire", "burns", null, null, 2, 2, 10);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.BrandKey(fire))));
        }

        @Test
        @DisplayName("is present when a carried brand shares the rune's name, even a different instance")
        void presentByNameNotIdentity() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Brand fireOnRune = new Brand("FIRE", "fire", "burns", null, null, 2, 2, 10);
            Brand fireOnItem = new Brand("FIRE_BRAND_3", "fire", "flares", null, null, 3, 3, 20);
            obj.addBrand(fireOnItem);

            assertTrue(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.BrandKey(fireOnRune))));
        }

        @Test
        @DisplayName("is absent when the object's brands are all differently named")
        void absentWhenNamesDiffer() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Brand cold = new Brand("COLD", "cold", "freezes", null, null, 2, 2, 10);
            obj.addBrand(cold);
            Brand fireOnRune = new Brand("FIRE", "fire", "burns", null, null, 2, 2, 10);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.BrandKey(fireOnRune))));
        }
    }

    /**
     * Slay runes — matched by {@link Slay#sameMonsterSlain}, not by name, mirroring how
     * {@link Rune#initRunes()} groups slays.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("slay rune")
    class SlayRune {

        private Slay slay(String code, String name, MonsterRaceFlag raceFlag) {
            return new Slay(code, name, null, "hits", "strikes", raceFlag, 2, 2, 10);
        }

        @Test
        @DisplayName("is absent when the object carries no slay at all")
        void absentWithNoSlays() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Slay evil = slay("EVIL_2", "evil creatures", MonsterRaceFlag.RF_EVIL);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.SlayKey(evil))));
        }

        @Test
        @DisplayName("is present when a carried slay kills the same monsters, even at a different strength")
        void presentBySameMonstersSlain() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Slay evilOnRune = slay("EVIL_2", "evil creatures", MonsterRaceFlag.RF_EVIL);
            Slay evilOnItem = slay("EVIL_5", "evil creatures", MonsterRaceFlag.RF_EVIL);
            obj.addSlay(evilOnItem);

            assertTrue(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.SlayKey(evilOnRune))));
        }

        @Test
        @DisplayName("is absent when the object's slays target different monsters")
        void absentWhenMonstersDiffer() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            obj.addSlay(slay("ORC_2", "orcs", MonsterRaceFlag.RF_ORC));
            Slay evilOnRune = slay("EVIL_2", "evil creatures", MonsterRaceFlag.RF_EVIL);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.SlayKey(evilOnRune))));
        }
    }

    /**
     * Curse runes — matched by key identity in the object's curse map, with a zero power treated as
     * "not cursed", the same convention {@link ItemObjectCursesTest} pins for the map itself.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("curse rune")
    class CurseRune {

        @Test
        @DisplayName("is absent when the object carries no curses at all")
        void absentWithNoCurses() {
            ItemObject obj = item(TValue.TV_SWORD, null);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.CurseKey(curse("siren")))));
        }

        @Test
        @DisplayName("is present once the curse is stacked to a nonzero power")
        void presentAtNonzeroPower() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Curse siren = curse("siren");
            obj.addCurse(siren, 3, 0);

            assertTrue(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.CurseKey(siren))));
        }

        @Test
        @DisplayName("is absent when the curse is present at power zero")
        void absentAtZeroPower() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            Curse siren = curse("siren");
            obj.addCurse(siren, 0, 0);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.CurseKey(siren))));
        }

        @Test
        @DisplayName("is absent for a curse the object does not carry, even one of the same name")
        void absentForADifferentCurseOfTheSameName() {
            ItemObject obj = item(TValue.TV_SWORD, null);
            obj.addCurse(curse("siren"), 3, 0);

            assertFalse(ObjectKnowledge.objectHasRune(obj, new Rune(new RuneVariety.CurseKey(curse("siren")))));
        }
    }

    /**
     * Flag runes — a plain membership test against the object's flag set.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("flag rune")
    class FlagRune {

        @Test
        @DisplayName("is absent when the flag is not set")
        void absentWhenUnset() {
            ItemObject obj = item(TValue.TV_RING, null);

            assertFalse(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_FREE_ACT, null))));
        }

        @Test
        @DisplayName("is present once the flag is set")
        void presentWhenSet() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setFlag(ObjectFlag.OF_FREE_ACT);

            assertTrue(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_FREE_ACT, null))));
        }

        @Test
        @DisplayName("checks only the flag the rune names")
        void checksTheNamedFlagOnly() {
            ItemObject obj = item(TValue.TV_RING, null);
            obj.setFlag(ObjectFlag.OF_SEE_INVIS);

            assertFalse(ObjectKnowledge.objectHasRune(obj,
                    new Rune(new RuneVariety.FlagKey(ObjectFlag.OF_FREE_ACT, null))));
        }
    }

    /**
     * {@link ObjectKnowledge#objectFlavourTried} — C's {@code object_flavor_tried}
     * ({@code obj-knowledge.c:2350}). The only branch is the special-artifact guard, so that is
     * the one thing worth pinning beyond the ordinary set-to-true path; the null cases pin the
     * substitution for C's compiled-out {@code assert}s noted on the port itself.
     *
     * @author Rowan Crowther
     */
    @Nested
    @DisplayName("objectFlavourTried")
    class ObjectFlavourTried {

        @Test
        @DisplayName("marks an untried ordinary kind as tried")
        void marksUntriedOrdinaryKindAsTried() throws Exception {
            ObjectKind kind = new ObjectKind();
            ItemObject obj = item(TValue.TV_SWORD, kind);

            ObjectKnowledge.objectFlavourTried(obj);

            assertTrue(peek(kind, "tried"));
        }

        @Test
        @DisplayName("leaves an already-tried ordinary kind tried")
        void leavesAlreadyTriedOrdinaryKindTried() throws Exception {
            ObjectKind kind = new ObjectKind();
            kind.setTried(true);
            ItemObject obj = item(TValue.TV_SWORD, kind);

            ObjectKnowledge.objectFlavourTried(obj);

            assertTrue(peek(kind, "tried"));
        }

        @Test
        @DisplayName("does not mark a special-artifact kind as tried")
        void doesNotMarkSpecialArtifactKindAsTried() throws Exception {
            ObjectKind kind = new ObjectKind();
            poke(kind, "isSpecialArtifactKind", true);
            ItemObject obj = item(TValue.TV_SWORD, kind);

            ObjectKnowledge.objectFlavourTried(obj);

            assertFalse(peek(kind, "tried"));
        }

        @Test
        @DisplayName("does nothing to an already-tried special-artifact kind")
        void doesNothingToAnAlreadyTriedSpecialArtifactKind() throws Exception {
            ObjectKind kind = new ObjectKind();
            poke(kind, "isSpecialArtifactKind", true);
            kind.setTried(true);
            ItemObject obj = item(TValue.TV_SWORD, kind);

            ObjectKnowledge.objectFlavourTried(obj);

            assertTrue(peek(kind, "tried"));
        }

        @Test
        @DisplayName("does not throw for a null object")
        void doesNotThrowForANullObject() {
            ObjectKnowledge.objectFlavourTried(null);
        }

        @Test
        @DisplayName("does not throw for an object with no kind")
        void doesNotThrowForAnObjectWithNoKind() {
            ItemObject obj = item(TValue.TV_SWORD, null);

            ObjectKnowledge.objectFlavourTried(obj);
        }
    }
}
