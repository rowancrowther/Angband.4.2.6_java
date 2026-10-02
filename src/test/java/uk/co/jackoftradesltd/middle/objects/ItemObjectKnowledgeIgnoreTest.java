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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.channel.utils.FlagView;
import uk.co.jackoftradesltd.middle.numerics.Random;
import uk.co.jackoftradesltd.middle.objects.enums.IgnoreType;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectModifier;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectNotice;
import uk.co.jackoftradesltd.middle.objects.enums.QualityValueEnum;
import uk.co.jackoftradesltd.middle.objects.enums.TValue;
import uk.co.jackoftradesltd.testsupport.ItemFixture;
import uk.co.jackoftradesltd.testsupport.SeededPlayerRegistry;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.jackoftradesltd.testsupport.ItemFixture.set;

/**
 * Tests the knowledge and ignoring queries of {@link ItemObject} that the older classification,
 * quality and recharge tests leave open: {@link ItemObject#flagsKnown()}, the weapon branch of
 * {@link ItemObject#ignoreLevelOf()} with its two private helpers, the ego and artifact overrides
 * of that grading, {@link ItemObject#egoIsIgnored}, the rest of the quality table behind
 * {@link ItemObject#getIgnoreTypeOf()}, and the strict flavour-awareness test.
 *
 * <p>Expected values are worked from C, not read off the port. {@code is_object_good} in
 * {@code obj-ignore.c} sums {@code 4 * cmp(to_d) + 2 * cmp(to_h) + 1 * cmp(to_a)}, where each
 * {@code cmp} compares the item's bonus with the kind's minimum roll clamped to at most zero, and
 * {@code ignore_level_of} turns the sign of the sum into good, bad or average before an ego or an
 * artifact overrides it. {@code object_flags_known} copies the flags, intersects with the known
 * half's, adds the kind's flags for an aware kind, and for an easy-known ego adds its flags and then
 * removes its suppressed ones, in that order.
 *
 * <p>The weights are pinned by cases where the sign of the sum depends on them: a to-damage point
 * outweighs a to-hit point and a to-armour point together, and a to-hit point outweighs a
 * to-armour point.
 *
 * @author Rowan Crowther
 */
@ExtendWith(SeededPlayerRegistry.class)
class ItemObjectKnowledgeIgnoreTest {

    /**
     * A dice expression with a fixed base and a number of dice, which is all a minimum roll reads.
     *
     * <p>The minimum of {@code base + dice * 1} is what {@code randcalc(..., MINIMISE)} gives at
     * level zero, so {@code floor(-4, 2)} has a minimum of {@code -2}.
     *
     * @param base the fixed part
     * @param dice the number of dice, each of which contributes at least one
     * @return the dice
     */
    private static Random floor(int base, int dice) {
        return new Random(base, 0, dice, 5, false);
    }

    /**
     * A sword kind whose three bonus dice are the given ones.
     *
     * @param toH the to-hit dice
     * @param toD the to-damage dice
     * @param toA the to-armour dice
     * @return the kind
     */
    private static ObjectKind swordKind(Random toH, Random toD, Random toA) {
        ObjectKind kind = ItemFixture.kindWithDice(TValue.TV_SWORD);
        set(kind, "toH", toH);
        set(kind, "toD", toD);
        set(kind, "toA", toA);
        return kind;
    }

    /**
     * A sword whose kind has a minimum roll of zero for every bonus.
     *
     * @return the kind
     */
    private static ObjectKind plainSwordKind() {
        return swordKind(floor(0, 0), floor(0, 0), floor(0, 0));
    }

    /**
     * A fully known sword with the given bonuses, on both halves so that it stays fully known.
     *
     * @param kind the kind it is built on
     * @param toH  the to-hit bonus
     * @param toD  the to-damage bonus
     * @param toA  the to-armour bonus
     * @return the item
     */
    private static ItemObject sword(ObjectKind kind, int toH, int toD, int toA) {
        return withBonuses(ItemFixture.item(TValue.TV_SWORD).kind(kind).fullyKnown().build(), toH, toD, toA);
    }

    /**
     * Writes the three bonuses onto an item and its known half.
     *
     * @param item the item
     * @param toH  the to-hit bonus
     * @param toD  the to-damage bonus
     * @param toA  the to-armour bonus
     * @return the same item
     */
    private static ItemObject withBonuses(ItemObject item, int toH, int toD, int toA) {
        for (ItemObject half : new ItemObject[]{item, item.getKnown()}) {
            set(half, "toHit", toH);
            set(half, "toDam", toD);
            set(half, "toAC", toA);
        }
        return item;
    }

    /**
     * An ego with the given flags and suppressed flags, and nothing else.
     *
     * @param flags    the flags it adds
     * @param flagsOff the flags it suppresses
     * @return the ego
     */
    private static EgoItem ego(Flag<ObjectFlag> flags, Flag<ObjectFlag> flagsOff) {
        return new EgoItem("of Test", "", 1, 0,
                flags, flagsOff, new Flag<>(ObjectKindFlag.class),
                new HashMap<>(), new HashMap<>(), new HashMap<>(),
                new HashSet<>(), new HashSet<>(), new HashMap<>(),
                0, 0, 0, 0, new java.util.ArrayList<ObjectKind>(),
                null, null, null, 0, 0, 0, null, null, false);
    }

    /**
     * An ego with no flags either way.
     *
     * @return the ego
     */
    private static EgoItem plainEgo() {
        return ego(new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class));
    }

    /**
     * A bare artifact, which is all {@code ignoreLevelOf} asks of one: that it exists.
     *
     * @return the artifact
     */
    private static Artifact artifact() {
        return new Artifact("Test", null, TValue.TV_SWORD, null, 0, 0, 0, 0, "0", 0, 0,
                new Flag<>(ObjectFlag.class), Map.of(), Map.of(), Set.of(), Set.of(),
                new LinkedHashMap<>(), 0, 0, 0, 0, null, null, null);
    }

    /**
     * A flag set holding exactly the given flags.
     *
     * @param on the flags to raise
     * @return the set
     */
    private static Flag<ObjectFlag> flagSet(ObjectFlag... on) {
        Flag<ObjectFlag> flags = new Flag<>(ObjectFlag.class);
        for (ObjectFlag flag : on) {
            flags.on(flag);
        }
        return flags;
    }

    /**
     * The members of a flag set, for comparing two sets without relying on their equality.
     *
     * @param flags the set to read
     * @return the flags that are on
     */
    private static Set<ObjectFlag> members(FlagView<ObjectFlag> flags) {
        Set<ObjectFlag> members = EnumSet.noneOf(ObjectFlag.class);
        for (ObjectFlag flag : flags) {
            members.add(flag);
        }
        return members;
    }

    /**
     * Marks a kind as one the player is aware of.
     *
     * @param kind the kind
     */
    private static void makeAware(ObjectKind kind) {
        set(kind, "aware", true);
    }

    /**
     * Marks a kind as easy to know: aware, and carrying {@code KF_EASY_KNOW}.
     *
     * @param kind the kind
     */
    private static void makeEasyKnow(ObjectKind kind) {
        makeAware(kind);
        Flag<ObjectKindFlag> kindFlags = new Flag<>(ObjectKindFlag.class);
        kindFlags.on(ObjectKindFlag.KF_EASY_KNOW);
        set(kind, "kindFlags", kindFlags);
    }

    /**
     * The weapon branch of the quality grading, which is where {@code isGood} and
     * {@code compareObjectTrait} do their work.
     */
    @Nested
    @DisplayName("ignoreLevelOf: weapons")
    class WeaponGrading {

        /**
         * With every kind minimum at zero, the sign of {@code 4*toD + 2*toH + 1*toA} (each term
         * reduced to its sign) decides the band. Rows where the sign flips with the weights pin the
         * weights themselves.
         *
         * @param toH      the item's to-hit
         * @param toD      the item's to-damage
         * @param toA      the item's to-armour
         * @param expected the band
         */
        @ParameterizedTest(name = "to-hit {0}, to-dam {1}, to-AC {2} -> {3}")
        @CsvSource({
                " 0,  0,  0, IGNORE_AVERAGE",
                " 0,  1,  0, IGNORE_GOOD",
                " 1,  0,  0, IGNORE_GOOD",
                " 0,  0,  1, IGNORE_GOOD",
                " 0, -1,  0, IGNORE_BAD",
                "-1,  0,  0, IGNORE_BAD",
                " 0,  0, -1, IGNORE_BAD",
                "-1,  1,  0, IGNORE_GOOD",
                " 1, -1,  0, IGNORE_BAD",
                " 1,  0, -1, IGNORE_GOOD",
                "-1,  0,  1, IGNORE_BAD",
                " 1, -1,  1, IGNORE_BAD",
                "-1,  1, -1, IGNORE_GOOD",
                "50,  0,  0, IGNORE_GOOD",
                "-50, 0,  0, IGNORE_BAD"
        })
        @DisplayName("the signs are weighted four, two and one")
        void weightedSum(int toH, int toD, int toA, QualityValueEnum expected) {
            assertEquals(expected, sword(plainSwordKind(), toH, toD, toA).ignoreLevelOf());
        }

        /**
         * The kind's minimum roll is the yardstick, so a kind that can roll a penalty makes that
         * penalty average and anything above it good, while a kind whose minimum is positive is
         * judged against zero instead.
         *
         * @param floorBase the base of the kind's to-hit dice
         * @param floorDice the number of those dice
         * @param toH       the item's to-hit
         * @param expected  the band
         */
        @ParameterizedTest(name = "kind to-hit {0} + {1} dice, item {2} -> {3}")
        @CsvSource({
                "-2, 0, -2, IGNORE_AVERAGE",
                "-2, 0,  0, IGNORE_GOOD",
                "-2, 0, -1, IGNORE_GOOD",
                "-2, 0, -3, IGNORE_BAD",
                " 3, 0,  0, IGNORE_AVERAGE",
                " 3, 0,  1, IGNORE_GOOD",
                " 3, 0,  3, IGNORE_GOOD",
                " 3, 0, -1, IGNORE_BAD",
                "-4, 2, -2, IGNORE_AVERAGE",
                "-4, 2, -1, IGNORE_GOOD",
                "-4, 2, -3, IGNORE_BAD"
        })
        @DisplayName("the kind's minimum roll, clamped to zero, is the yardstick")
        void kindFloor(int floorBase, int floorDice, int toH, QualityValueEnum expected) {
            ObjectKind kind = swordKind(floor(floorBase, floorDice), floor(0, 0), floor(0, 0));

            assertEquals(expected, sword(kind, toH, 0, 0).ignoreLevelOf());
        }

        /**
         * Each of the three bonuses has its own floor, read from its own dice. Every floor here is
         * {@code -2}: an item at exactly that is average, and one point under it on any one bonus is
         * bad.
         *
         * @param field the bonus pushed below its floor
         */
        @ParameterizedTest(name = "{0} one below its floor")
        @CsvSource({"toHit", "toDam", "toAC"})
        @DisplayName("each bonus is compared with its own dice")
        void eachBonusHasItsOwnFloor(String field) {
            ObjectKind kind = swordKind(floor(-2, 0), floor(-2, 0), floor(-2, 0));
            ItemObject atFloor = sword(kind, -2, -2, -2);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, atFloor.ignoreLevelOf());

            ItemObject below = sword(kind, -2, -2, -2);
            set(below, field, -3);
            set(below.getKnown(), field, -3);

            assertEquals(QualityValueEnum.IGNORE_BAD, below.ignoreLevelOf());
        }

        /**
         * An ego item is {@code IGNORE_ALL} whatever its bonuses say, so it is never hidden by a
         * quality setting the player has not pushed to the top.
         */
        @Test
        @DisplayName("a fully known ego item is IGNORE_ALL even when its bonuses are bad")
        void egoOverridesBad() {
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).kind(plainSwordKind())
                    .ego(plainEgo()).fullyKnown().build();
            withBonuses(item, 0, -3, 0);

            assertEquals(QualityValueEnum.IGNORE_ALL, item.ignoreLevelOf());
        }

        /**
         * An artifact is {@code IGNORE_MAX}, above every setting, so it is never ignored on quality.
         */
        @Test
        @DisplayName("a fully known artifact is IGNORE_MAX even when its bonuses are bad")
        void artifactOverridesBad() {
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).kind(plainSwordKind())
                    .artifact(artifact()).fullyKnown().build();
            withBonuses(item, 0, -3, 0);

            assertEquals(QualityValueEnum.IGNORE_MAX, item.ignoreLevelOf());
        }

        /**
         * The ego test comes first in C, so an item holding both is graded as an ego.
         */
        @Test
        @DisplayName("an ego beats an artifact when an item somehow holds both")
        void egoBeatsArtifact() {
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).kind(plainSwordKind())
                    .ego(plainEgo()).artifact(artifact()).fullyKnown().build();

            assertEquals(QualityValueEnum.IGNORE_ALL, item.ignoreLevelOf());
        }

        /**
         * The assessed shortcut for a not fully known item excludes artifacts: an artifact that has
         * been examined but not identified must still never be hidden.
         */
        @Test
        @DisplayName("an assessed, partly known artifact is IGNORE_MAX, not IGNORE_ALL")
        void assessedArtifactIsMax() {
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).kind(plainSwordKind())
                    .artifact(artifact()).fullyKnown().build();
            set(item, "toHit", 4);
            set(item.getKnown(), "toHit", 0);
            item.getKnown().orNotice(ObjectNotice.OBJ_NOTICE_ASSESSED);

            assertEquals(QualityValueEnum.IGNORE_MAX, item.ignoreLevelOf());
        }

        /**
         * A partly known ego item is graded as an unfinished identification, not as an ego: the
         * ego override applies only once the item is fully known.
         */
        @Test
        @DisplayName("a partly known, unassessed ego item is IGNORE_MAX")
        void partlyKnownEgoIsMax() {
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).kind(plainSwordKind())
                    .ego(plainEgo()).fullyKnown().build();
            set(item, "toHit", 4);
            set(item.getKnown(), "toHit", 0);

            assertEquals(QualityValueEnum.IGNORE_MAX, item.ignoreLevelOf());
        }
    }

    /**
     * The jewellery branch, beyond the cases {@code ItemObjectQualityTest} already holds.
     */
    @Nested
    @DisplayName("ignoreLevelOf: jewellery")
    class JewelleryGrading {

        /**
         * A fully known ring of the given type.
         *
         * @return the ring
         */
        private ItemObject ring() {
            return ItemFixture.item(TValue.TV_RING).kind(ItemFixture.kindWithDice(TValue.TV_RING))
                    .fullyKnown().build();
        }

        /**
         * Any one of the three combat figures, positive on the known half, lifts a ring to average.
         *
         * @param field the figure
         */
        @ParameterizedTest(name = "known {0} +1 -> average")
        @CsvSource({"toHit", "toDam", "toAC"})
        @DisplayName("each positive combat figure makes a ring average")
        void positiveFigure(String field) {
            ItemObject ring = ring();
            set(ring.getKnown(), field, 1);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, ring.ignoreLevelOf());
        }

        /**
         * Any one of the three, negative alone, makes a ring bad.
         *
         * @param field the figure
         */
        @ParameterizedTest(name = "known {0} -1 -> bad")
        @CsvSource({"toHit", "toDam", "toAC"})
        @DisplayName("each negative combat figure makes a ring bad")
        void negativeFigure(String field) {
            ItemObject ring = ring();
            set(ring.getKnown(), field, -1);

            assertEquals(QualityValueEnum.IGNORE_BAD, ring.ignoreLevelOf());
        }

        /**
         * A positive on one figure beats a negative on another, because the positive test runs
         * first.
         */
        @Test
        @DisplayName("a positive figure outweighs a negative one on another")
        void positiveBeatsNegativeAcrossFigures() {
            ItemObject ring = ring();
            set(ring.getKnown(), "toHit", 1);
            set(ring.getKnown(), "toAC", -5);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, ring.ignoreLevelOf());
        }

        /**
         * Only a positive modifier counts. A ring whose only entry is a penalty to a stat has no
         * positive modifier and no combat figure, so it is average, not bad.
         */
        @Test
        @DisplayName("a negative modifier alone leaves a ring average")
        void negativeModifierAlone() {
            ItemObject ring = ring();
            ring.getKnown().putModifier(ObjectModifier.OM_STEALTH, -3);

            assertEquals(QualityValueEnum.IGNORE_AVERAGE, ring.ignoreLevelOf());
        }

        /**
         * Everything read comes from the known half. A penalty the player has not learned cannot
         * make a ring bad, and a bonus they have not learned cannot lift one.
         */
        @Test
        @DisplayName("jewellery is graded on the known half, not the real one")
        void knownHalfOnly() {
            ItemObject cursed = ring();
            set(cursed, "toAC", -7);
            assertEquals(QualityValueEnum.IGNORE_AVERAGE, cursed.ignoreLevelOf(),
                    "an unlearned penalty must not make the ring bad");

            ItemObject blessed = ring();
            blessed.putModifier(ObjectModifier.OM_STEALTH, 3);
            set(blessed.getKnown(), "toAC", -1);
            assertEquals(QualityValueEnum.IGNORE_BAD, blessed.ignoreLevelOf(),
                    "an unlearned modifier must not lift the ring");
        }
    }

    /**
     * The known-flags calculation: a copy, an intersection, then what awareness and an easy-known
     * ego add and remove.
     */
    @Nested
    @DisplayName("flagsKnown")
    class FlagsKnown {

        /**
         * A sword with the given real flags, a known half carrying the given flags, and the given
         * kind and ego.
         *
         * @param kind       the kind, which may be {@code null}
         * @param ego        the ego, which may be {@code null}
         * @param real       the item's own flags
         * @param knownFlags the flags the player has learned for it
         * @return the item
         */
        private ItemObject item(ObjectKind kind, EgoItem ego, Flag<ObjectFlag> real, Flag<ObjectFlag> knownFlags) {
            ItemObject known = ItemFixture.item(TValue.TV_SWORD)
                    .flags(members(knownFlags).toArray(new ObjectFlag[0])).build();
            return ItemFixture.item(TValue.TV_SWORD).kind(kind).ego(ego).known(known)
                    .flags(members(real).toArray(new ObjectFlag[0])).build();
        }

        /**
         * A kind with the given kind-level flags.
         *
         * @param flags the flags every item of this kind carries
         * @return the kind
         */
        private ObjectKind kindWith(Flag<ObjectFlag> flags) {
            ObjectKind kind = ItemFixture.kindWithDice(TValue.TV_SWORD);
            set(kind, "flags", flags);
            return kind;
        }

        /**
         * With no known half nothing is known, however many flags the item really has.
         */
        @Test
        @DisplayName("an item with no known half knows nothing")
        void noKnownHalf() {
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).flags(ObjectFlag.OF_FEATHER).build();

            assertTrue(members(item.flagsKnown()).isEmpty());
        }

        /**
         * The known half can only take flags away: a flag the item does not have is not added just
         * because the known half lists it, and a real flag the player has not learned drops out.
         */
        @Test
        @DisplayName("the result is the intersection of the real and known flags")
        void intersection() {
            ItemObject item = item(kindWith(flagSet()), null,
                    flagSet(ObjectFlag.OF_FEATHER, ObjectFlag.OF_REGEN),
                    flagSet(ObjectFlag.OF_FEATHER, ObjectFlag.OF_SEE_INVIS));

            assertEquals(EnumSet.of(ObjectFlag.OF_FEATHER), members(item.flagsKnown()));
        }

        /**
         * With no kind the calculation stops after the intersection.
         */
        @Test
        @DisplayName("an item with no kind returns just the intersection")
        void noKind() {
            ItemObject item = item(null, null,
                    flagSet(ObjectFlag.OF_FEATHER), flagSet(ObjectFlag.OF_FEATHER));

            assertEquals(EnumSet.of(ObjectFlag.OF_FEATHER), members(item.flagsKnown()));
        }

        /**
         * The kind's flags come back only once the player is aware of the kind.
         */
        @Test
        @DisplayName("kind flags are added only for an aware kind")
        void awareKindAddsItsFlags() {
            ObjectKind kind = kindWith(flagSet(ObjectFlag.OF_FREE_ACT));
            ItemObject item = item(kind, null, flagSet(), flagSet());

            assertTrue(members(item.flagsKnown()).isEmpty(), "unaware: the kind's flags stay hidden");

            makeAware(kind);

            assertEquals(EnumSet.of(ObjectFlag.OF_FREE_ACT), members(item.flagsKnown()),
                    "aware: added even though neither half lists it");
        }

        /**
         * An ego's flags need the easy-know status, not just awareness of the kind.
         */
        @Test
        @DisplayName("an ego's flags are not added for an aware kind that is not easy-know")
        void awareIsNotEnoughForAnEgo() {
            ObjectKind kind = kindWith(flagSet());
            makeAware(kind);
            ItemObject item = item(kind, ego(flagSet(ObjectFlag.OF_SEE_INVIS), flagSet()),
                    flagSet(), flagSet());

            assertTrue(members(item.flagsKnown()).isEmpty());
        }

        /**
         * The easy-know flag without awareness is not enough either.
         */
        @Test
        @DisplayName("an ego's flags are not added for an easy-know kind that is not aware")
        void easyKnowFlagAloneIsNotEnough() {
            ObjectKind kind = kindWith(flagSet());
            Flag<ObjectKindFlag> kindFlags = new Flag<>(ObjectKindFlag.class);
            kindFlags.on(ObjectKindFlag.KF_EASY_KNOW);
            set(kind, "kindFlags", kindFlags);
            ItemObject item = item(kind, ego(flagSet(ObjectFlag.OF_SEE_INVIS), flagSet()),
                    flagSet(), flagSet());

            assertTrue(members(item.flagsKnown()).isEmpty());
        }

        /**
         * With an easy-known ego the ego's flags are added and its suppressed flags removed, and
         * the removal reaches a flag that came from the item, one from the kind, and one that has
         * just been added.
         */
        @Test
        @DisplayName("an easy-known ego adds its flags and then removes its suppressed ones")
        void easyKnownEgo() {
            ObjectKind kind = kindWith(flagSet(ObjectFlag.OF_REGEN, ObjectFlag.OF_FREE_ACT));
            makeEasyKnow(kind);
            EgoItem ego = ego(flagSet(ObjectFlag.OF_SEE_INVIS, ObjectFlag.OF_SLOW_DIGEST),
                    flagSet(ObjectFlag.OF_FEATHER, ObjectFlag.OF_REGEN, ObjectFlag.OF_SLOW_DIGEST));
            ItemObject item = item(kind, ego,
                    flagSet(ObjectFlag.OF_FEATHER), flagSet(ObjectFlag.OF_FEATHER));

            assertEquals(EnumSet.of(ObjectFlag.OF_FREE_ACT, ObjectFlag.OF_SEE_INVIS), members(item.flagsKnown()),
                    "feather (item), regen (kind) and slow digestion (ego) are all suppressed");
        }

        /**
         * The result is a new set: changing it must not change the item.
         */
        @Test
        @DisplayName("the result is a fresh set, not the item's own")
        void freshSet() {
            ItemObject item = item(kindWith(flagSet()), null,
                    flagSet(ObjectFlag.OF_FEATHER), flagSet(ObjectFlag.OF_FEATHER));

            item.flagsKnown().off(ObjectFlag.OF_FEATHER);

            assertTrue(item.hasFlag(ObjectFlag.OF_FEATHER));
            assertTrue(item.getKnown().hasFlag(ObjectFlag.OF_FEATHER));
        }
    }

    /**
     * The ego ignore marks, read through the item.
     */
    @Nested
    @DisplayName("egoIsIgnored")
    class EgoIgnored {

        /**
         * An ego the player has marked under a category is ignored under that category and no other.
         */
        @Test
        @DisplayName("a marked ego is ignored under its own category only")
        void markedCategoryOnly() {
            EgoItem ego = plainEgo();
            ItemObject item = ItemFixture.item(TValue.TV_SWORD).ego(ego).build();

            assertFalse(item.egoIsIgnored(IgnoreType.ITYPE_SHARP), "nothing marked yet");

            ego.setIgnoreType(IgnoreType.ITYPE_SHARP);

            assertTrue(item.egoIsIgnored(IgnoreType.ITYPE_SHARP));
            assertFalse(item.egoIsIgnored(IgnoreType.ITYPE_BLUNT), "other categories are unaffected");
        }

        /**
         * The marks live on the ego, so two items of the same ego agree.
         */
        @Test
        @DisplayName("every item of one ego shares its marks")
        void sharedAcrossItems() {
            EgoItem ego = plainEgo();
            ItemObject first = ItemFixture.item(TValue.TV_SWORD).ego(ego).build();
            ItemObject second = ItemFixture.item(TValue.TV_SWORD).ego(ego).build();

            ego.setIgnoreType(IgnoreType.ITYPE_SHARP);

            assertTrue(first.egoIsIgnored(IgnoreType.ITYPE_SHARP));
            assertTrue(second.egoIsIgnored(IgnoreType.ITYPE_SHARP));
        }
    }

    /**
     * The strict flavour test, and the easy-know case the lenient tests miss.
     */
    @Nested
    @DisplayName("flavour awareness")
    class Flavour {

        /**
         * The strict test follows the kind's awareness.
         */
        @Test
        @DisplayName("objectFlavourIsAware follows the kind")
        void follows() {
            ObjectKind kind = ItemFixture.kindWithDice(TValue.TV_POTION);
            ItemObject potion = ItemFixture.item(TValue.TV_POTION).kind(kind).build();

            assertFalse(potion.objectFlavourIsAware());

            makeAware(kind);

            assertTrue(potion.objectFlavourIsAware());
        }

        /**
         * With no kind the strict test throws, where the lenient one answers false.
         */
        @Test
        @DisplayName("objectFlavourIsAware throws for an item with no kind")
        void throwsWithoutKind() {
            ItemObject kindless = ItemFixture.item(TValue.TV_POTION).kind(null).build();

            assertThrows(RuntimeException.class, kindless::objectFlavourIsAware);
            assertFalse(kindless.flavourIsAware(), "the lenient port answers false for the same state");
        }

        /**
         * The easy-know flag on a kind the player has not met is not easy-know.
         */
        @Test
        @DisplayName("easy-know needs awareness even when the kind carries the flag")
        void flagWithoutAwareness() {
            ObjectKind kind = ItemFixture.kindWithDice(TValue.TV_SCROLL);
            Flag<ObjectKindFlag> kindFlags = new Flag<>(ObjectKindFlag.class);
            kindFlags.on(ObjectKindFlag.KF_EASY_KNOW);
            set(kind, "kindFlags", kindFlags);
            ItemObject scroll = ItemFixture.item(TValue.TV_SCROLL).kind(kind).build();

            assertFalse(scroll.easyKnow());

            makeAware(kind);

            assertTrue(scroll.easyKnow());
        }
    }

    /**
     * The rest of the quality table, worked from the C table in {@code obj-ignore.c}: the first row
     * matching the type, and for a row with an identifier a substring of the kind's name, wins.
     */
    @Nested
    @DisplayName("getIgnoreTypeOf: the quality table")
    class QualityTable {

        /**
         * Rows of the table not exercised elsewhere, plus the case and substring rules.
         *
         * @param tValue   the item type
         * @param name     the kind's name
         * @param expected the category C's table gives
         */
        @ParameterizedTest(name = "{0} \"{1}\" -> {2}")
        @CsvSource({
                "TV_SWORD,       Blade of Chaos,            ITYPE_GREAT",
                "TV_SWORD,       blade of chaos,            ITYPE_SHARP",
                "TV_POLEARM,     Glaive of Slicing,         ITYPE_GREAT",
                "TV_POLEARM,     Spear,                     ITYPE_SHARP",
                "TV_HAFTED,      Mace of Disruption,        ITYPE_GREAT",
                "TV_SHOT,        Iron Shot,                 ITYPE_SHOT",
                "TV_BOLT,        Bolt,                      ITYPE_BOLT",
                "TV_SOFT_ARMOR,  Robe,                      ITYPE_ROBE",
                "TV_SOFT_ARMOR,  Soft Leather Armour,       ITYPE_BODY_ARMOR",
                "TV_HARD_ARMOR,  Chain Mail,                ITYPE_BODY_ARMOR",
                "TV_DRAG_ARMOR,  Black Dragon Scale Mail,   ITYPE_BASIC_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Green Dragon Scale Mail,   ITYPE_BASIC_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Black Multi Dragon Mail,   ITYPE_BASIC_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Multi Dragon Scale Mail,   ITYPE_MULTI_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Shining Dragon Scale Mail, ITYPE_HIGH_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Law Dragon Scale Mail,     ITYPE_HIGH_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Gold Dragon Scale Mail,    ITYPE_HIGH_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Chaos Dragon Scale Mail,   ITYPE_HIGH_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Balance Dragon Scale Mail, ITYPE_BALANCE_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Power Dragon Scale Mail,   ITYPE_POWER_DRAGON_ARMOR",
                "TV_DRAG_ARMOR,  Plain Dragon Scale Mail,   ITYPE_MAX",
                "TV_CLOAK,       Elven Cloak,               ITYPE_ELVEN_CLOAK",
                "TV_CLOAK,       Cloak,                     ITYPE_CLOAK",
                "TV_SHIELD,      Leather Shield,            ITYPE_SHIELD",
                "TV_HELM,        Hard Leather Cap,          ITYPE_HEADGEAR",
                "TV_CROWN,       Iron Crown,                ITYPE_HEADGEAR",
                "TV_GLOVES,      Leather Gloves,            ITYPE_HANDGEAR",
                "TV_BOOTS,       Leather Boots,             ITYPE_FEET",
                "TV_DIGGING,     Shovel,                    ITYPE_DIGGER",
                "TV_RING,        Ring of Protection,        ITYPE_RING",
                "TV_AMULET,      Amulet of Slow Digestion,  ITYPE_AMULET",
                "TV_LIGHT,       Wooden Torch,              ITYPE_LIGHT"
        })
        @DisplayName("the first matching row of C's table wins")
        void tableRows(TValue tValue, String name, IgnoreType expected) {
            ObjectKind kind = new ObjectKind(null, 0, 0, 0, 0, name, tValue, "test", null, false);
            ItemObject item = ItemFixture.item(tValue).kind(kind).build();

            assertEquals(expected, item.getIgnoreTypeOf());
        }
    }
}
