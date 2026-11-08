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
import uk.co.jackoftradesltd.channel.colour.ColourEnum;
import uk.co.jackoftradesltd.channel.strings.AngbandDisplayCharacter;
import uk.co.jackoftradesltd.channel.utils.Flag;
import uk.co.jackoftradesltd.middle.monsters.enums.MonsterRaceFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectFlag;
import uk.co.jackoftradesltd.middle.objects.enums.ObjectKindFlag;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Pins that an item's, an ego's and a kind's brands and slays come out of a copy, a clear and a
 * rebuild in the order they went in, as the Stage 4 rule "always echo C's ordering" requires.
 *
 * <p>The assemblers fill these sets in C's registry order, and C's {@code copy_brands} and
 * {@code copy_slays} walk them in that order. A set that forgot the order on the way through a
 * copy would throw that away after the load had got it right, so each test here builds the set in
 * one order, sends it through the operation, and compares the result as a list.
 *
 * <p>The fixtures are twelve brands and eight slays, written in a deliberately non-alphabetical
 * order. A {@link HashSet} holding them would almost certainly iterate in another order, which
 * {@link FixtureIsDiscriminating} checks, so a pass here cannot be an accident of a small set.
 *
 * <p>Order through {@code objectPrep}, {@code copyBrands} and {@code copySlays} is pinned in
 * {@link ObjectUtilsObjectPrepTest}, {@link ObjectUtilsCopyBrandsTest} and
 * {@link ObjectUtilsCopySlaysTest}.
 *
 * @author Rowan Crowther
 */
class BrandSlayOrderThroughCopiesTest {

    /**
     * Twelve brands with distinct names, in an order that is neither sorted nor the order a hash
     * would give.
     *
     * @return the brands, in the order to be pinned
     */
    private static List<Brand> brands() {
        List<Brand> list = new ArrayList<>();
        int[] order = {7, 2, 11, 0, 5, 9, 3, 10, 1, 8, 4, 6};
        for (int i : order) {
            String name = String.format("b%02d", i);
            list.add(new Brand(name.toUpperCase(), name, "burns", MonsterRaceFlag.RF_IM_FIRE, null,
                    3, 24, 100));
        }
        return list;
    }

    /**
     * Eight slays against distinct races, in an order that is neither alphabetical nor the order
     * the enum declares them.
     *
     * @return the slays, in the order to be pinned
     */
    private static List<Slay> slays() {
        MonsterRaceFlag[] order = {MonsterRaceFlag.RF_DEMON, MonsterRaceFlag.RF_ORC,
                MonsterRaceFlag.RF_UNDEAD, MonsterRaceFlag.RF_ANIMAL, MonsterRaceFlag.RF_GIANT,
                MonsterRaceFlag.RF_EVIL, MonsterRaceFlag.RF_TROLL, MonsterRaceFlag.RF_DRAGON};
        List<Slay> list = new ArrayList<>();
        for (MonsterRaceFlag flag : order) {
            list.add(new Slay(flag.name(), flag.name().toLowerCase(), null, "smite", "pierce",
                    flag, 2, 16, 100));
        }
        return list;
    }

    /**
     * A kind that {@link ObjectKind#copy()} can run on: it dereferences the display character, which
     * a bare kind does not have, and nothing else the copy needs is missing.
     *
     * @return the kind
     */
    private static ObjectKind copyableKind() {
        ObjectKind kind = new ObjectKind();
        kind.setCharacter(new AngbandDisplayCharacter('|', ColourEnum.COLOUR_WHITE));
        return kind;
    }

    /**
     * An ego carrying the given sets and nothing else the copy cares about.
     *
     * @param brands the ego's brands
     * @param slays  the ego's slays
     * @return the ego
     */
    private static EgoItem ego(Set<Brand> brands, Set<Slay> slays) {
        return new EgoItem("of Testing", "a test ego", 1, 0,
                new Flag<>(ObjectFlag.class), new Flag<>(ObjectFlag.class),
                new Flag<>(ObjectKindFlag.class), new HashMap<>(), new HashMap<>(),
                new HashMap<>(), brands, slays, new TreeMap<>(Curse.CURSE_ORDER),
                0, 0, 0, 0, new ArrayList<>(), null, null, null, 0, 0, 0,
                null, null, false);
    }

    /**
     * Guards the other tests: if a {@link HashSet} happened to keep these fixtures in insertion
     * order, a regression back to one would pass unnoticed.
     */
    @Nested
    @DisplayName("the fixtures")
    class FixtureIsDiscriminating {

        @Test
        @DisplayName("a HashSet does not keep the brand fixture in order")
        void hashSetScramblesTheBrands() {
            List<Brand> expected = brands();
            assertNotEquals(expected, new ArrayList<>(new HashSet<>(expected)));
        }

        @Test
        @DisplayName("a HashSet does not keep the slay fixture in order")
        void hashSetScramblesTheSlays() {
            List<Slay> expected = slays();
            assertNotEquals(expected, new ArrayList<>(new HashSet<>(expected)));
        }
    }

    /**
     * {@link ItemObject}: adding, clearing and copying.
     */
    @Nested
    @DisplayName("ItemObject")
    class Item {

        @Test
        @DisplayName("brands come back in the order they were added")
        void brandsKeepAddOrder() {
            ItemObject item = new ItemObject();
            List<Brand> expected = brands();
            expected.forEach(item::addBrand);

            assertEquals(expected, new ArrayList<>(item.getBrands()));
        }

        @Test
        @DisplayName("slays come back in the order they were added")
        void slaysKeepAddOrder() {
            ItemObject item = new ItemObject();
            List<Slay> expected = slays();
            expected.forEach(item::addSlay);

            assertEquals(expected, new ArrayList<>(item.getSlays()));
        }

        @Test
        @DisplayName("copy keeps the brand order")
        void copyKeepsBrandOrder() {
            ItemObject item = new ItemObject();
            List<Brand> expected = brands();
            expected.forEach(item::addBrand);

            assertEquals(expected, new ArrayList<>(item.copy(false).getBrands()));
        }

        @Test
        @DisplayName("copy keeps the slay order")
        void copyKeepsSlayOrder() {
            ItemObject item = new ItemObject();
            List<Slay> expected = slays();
            expected.forEach(item::addSlay);

            assertEquals(expected, new ArrayList<>(item.copy(false).getSlays()));
        }

        @Test
        @DisplayName("brands added after a clear keep the new order")
        void brandsAfterClearKeepOrder() {
            ItemObject item = new ItemObject();
            brands().forEach(item::addBrand);
            item.clearBrands();
            List<Brand> expected = brands().reversed();
            expected.forEach(item::addBrand);

            assertEquals(expected, new ArrayList<>(item.getBrands()));
        }

        @Test
        @DisplayName("slays added after a clear keep the new order")
        void slaysAfterClearKeepOrder() {
            ItemObject item = new ItemObject();
            slays().forEach(item::addSlay);
            item.clearSlays();
            List<Slay> expected = slays().reversed();
            expected.forEach(item::addSlay);

            assertEquals(expected, new ArrayList<>(item.getSlays()));
        }
    }

    /**
     * {@link EgoItem}: copying.
     */
    @Nested
    @DisplayName("EgoItem")
    class Ego {

        @Test
        @DisplayName("copy keeps the brand order")
        void copyKeepsBrandOrder() {
            List<Brand> expected = brands();
            EgoItem ego = ego(new LinkedHashSet<>(expected), new LinkedHashSet<>());

            assertEquals(expected, new ArrayList<>(ego.copy().getBrands()));
        }

        @Test
        @DisplayName("copy keeps the slay order")
        void copyKeepsSlayOrder() {
            List<Slay> expected = slays();
            EgoItem ego = ego(new LinkedHashSet<>(), new LinkedHashSet<>(expected));

            assertEquals(expected, new ArrayList<>(ego.copy().getSlays()));
        }
    }

    /**
     * {@link ObjectKind}: adding, clearing and copying.
     */
    @Nested
    @DisplayName("ObjectKind")
    class Kind {

        @Test
        @DisplayName("brands come back in the order they were added")
        void brandsKeepAddOrder() {
            ObjectKind kind = new ObjectKind();
            List<Brand> expected = brands();
            expected.forEach(kind::addBrand);

            assertEquals(expected, new ArrayList<>(kind.getBrands()));
        }

        @Test
        @DisplayName("slays come back in the order they were added")
        void slaysKeepAddOrder() {
            ObjectKind kind = new ObjectKind();
            List<Slay> expected = slays();
            expected.forEach(kind::addSlay);

            assertEquals(expected, new ArrayList<>(kind.getSlays()));
        }

        @Test
        @DisplayName("copy keeps the brand order")
        void copyKeepsBrandOrder() {
            ObjectKind kind = copyableKind();
            List<Brand> expected = brands();
            expected.forEach(kind::addBrand);

            assertEquals(expected, new ArrayList<>(kind.copy().getBrands()));
        }

        @Test
        @DisplayName("copy keeps the slay order")
        void copyKeepsSlayOrder() {
            ObjectKind kind = copyableKind();
            List<Slay> expected = slays();
            expected.forEach(kind::addSlay);

            assertEquals(expected, new ArrayList<>(kind.copy().getSlays()));
        }

        @Test
        @DisplayName("brands added after a clear keep the new order")
        void brandsAfterClearKeepOrder() {
            ObjectKind kind = new ObjectKind();
            brands().forEach(kind::addBrand);
            kind.clearBrands();
            List<Brand> expected = brands().reversed();
            expected.forEach(kind::addBrand);

            assertEquals(expected, new ArrayList<>(kind.getBrands()));
        }

        @Test
        @DisplayName("slays added after a clear keep the new order")
        void slaysAfterClearKeepOrder() {
            ObjectKind kind = new ObjectKind();
            slays().forEach(kind::addSlay);
            kind.clearSlays();
            List<Slay> expected = slays().reversed();
            expected.forEach(kind::addSlay);

            assertEquals(expected, new ArrayList<>(kind.getSlays()));
        }
    }
}
