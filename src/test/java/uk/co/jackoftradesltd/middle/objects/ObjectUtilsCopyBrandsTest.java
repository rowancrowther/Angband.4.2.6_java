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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link ObjectUtils#copyBrands}, the port of C's {@code copy_brands}
 * ({@code obj-slays.c}).
 *
 * <p>Same shape as {@link ObjectUtilsCopySlaysTest}: C ORs a fixed-index {@code bool} table and
 * dedups by clearing the weaker of any pair sharing a name; the port scans the item's brands for a
 * name match and keeps whichever of the pair has the higher {@link Brand#getMultiplier()}. Private,
 * so reached by reflection rather than through its only caller, {@link ObjectUtils#objectPrep}.
 *
 * @author Rowan Crowther
 */
class ObjectUtilsCopyBrandsTest {

    /**
     * Runs the method under test.
     *
     * @param dest   the item the merge writes into
     * @param source the kind whose brands are being added
     */
    private static void copyBrands(ItemObject dest, ObjectKind source) {
        try {
            Method method = ObjectUtils.class.getDeclaredMethod("copyBrands", ItemObject.class, ObjectKind.class);
            method.setAccessible(true);
            method.invoke(null, dest, source);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("copyBrands threw", e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("copyBrands is no longer reachable", e);
        }
    }

    /**
     * A brand at a given strength, following {@code brand.txt}'s convention of one name shared by
     * several tiers ({@code ACID_3}/{@code ACID_2}, both named {@code acid}).
     *
     * @param code       the brand's code, e.g. {@code ACID_3}
     * @param name       the element name — what {@link ObjectUtils#copyBrands} groups by
     * @param multiplier the standard damage multiplier
     * @return the brand
     */
    private static Brand brand(String code, String name, int multiplier) {
        return new Brand(code, name, "burns", MonsterRaceFlag.RF_IM_FIRE, null, multiplier,
                multiplier * 8, 100);
    }

    /**
     * The ordinary path: brands for elements the item's brands does not yet cover.
     */
    @Nested
    @DisplayName("adding brands for new elements")
    class Adding {

        /**
         * A brand for an element not yet present is added outright.
         */
        @Test
        @DisplayName("an empty dest gains every brand from source")
        void addsToEmptyDest() {
            ItemObject dest = new ItemObject();
            Brand acid = brand("ACID_3", "acid", 3);

            ObjectKind source = new ObjectKind();
            source.addBrand(acid);
            copyBrands(dest, source);

            assertEquals(Set.of(acid), dest.getBrands());
        }

        /**
         * Two brands naming different elements both survive - the name comparison never matches
         * across elements.
         */
        @Test
        @DisplayName("brands for distinct elements coexist")
        void distinctElementsCoexist() {
            ItemObject dest = new ItemObject();
            dest.addBrand(brand("ACID_3", "acid", 3));
            Brand cold = brand("COLD_3", "cold", 3);

            ObjectKind source = new ObjectKind();
            source.addBrand(cold);
            copyBrands(dest, source);

            assertEquals(2, dest.getBrands().size());
            assertTrue(dest.containsBrand(cold));
        }
    }

    /**
     * Two brands naming the same element - {@code brand.txt}'s tiers, e.g. {@code ACID_3} vs
     * {@code ACID_2}, both named {@code acid}.
     */
    @Nested
    @DisplayName("merging two brands of the same name")
    class SameName {

        /**
         * The stronger of the two survives when {@code source}'s is the stronger one - dest's
         * {@code ACID_2} loses to a merged-in {@code ACID_3}.
         */
        @Test
        @DisplayName("a stronger source brand replaces a weaker dest one")
        void strongerSourceReplacesWeakerDest() {
            ItemObject dest = new ItemObject();
            Brand weak = brand("ACID_2", "acid", 2);
            dest.addBrand(weak);
            Brand strong = brand("ACID_3", "acid", 3);

            ObjectKind source = new ObjectKind();
            source.addBrand(strong);
            copyBrands(dest, source);

            assertEquals(1, dest.getBrands().size(), "same-name brands must merge into one entry");
            Brand survivor = dest.getBrands().iterator().next();
            assertEquals(3, survivor.getMultiplier());
            assertEquals("ACID_3", survivor.getCode());
        }

        /**
         * The replacement is {@link Brand#copy}, not {@code source}'s own reference - the object the
         * caller passed in as {@code source} must not end up aliased into {@code dest}.
         */
        @Test
        @DisplayName("the surviving stronger brand is a copy, not source's own instance")
        void survivorIsACopyOfSource() {
            ItemObject dest = new ItemObject();
            dest.addBrand(brand("ACID_2", "acid", 2));
            Brand strong = brand("ACID_3", "acid", 3);

            ObjectKind source = new ObjectKind();
            source.addBrand(strong);
            copyBrands(dest, source);

            assertNotSame(strong, dest.getBrands().iterator().next());
        }

        /**
         * A weaker source brand changes nothing, and dest keeps its own instance rather than being
         * replaced by an equal-valued copy.
         */
        @Test
        @DisplayName("a weaker source brand leaves the stronger dest one exactly as it was")
        void weakerSourceLeavesStrongerDestAlone() {
            ItemObject dest = new ItemObject();
            Brand strong = brand("ACID_3", "acid", 3);
            dest.addBrand(strong);
            Brand weak = brand("ACID_2", "acid", 2);

            ObjectKind source = new ObjectKind();
            source.addBrand(weak);
            copyBrands(dest, source);

            assertEquals(1, dest.getBrands().size());
            assertSame(strong, dest.getBrands().iterator().next(),
                    "the surviving instance must be dest's own, not a copy of the loser");
        }
    }

    /**
     * A {@code source} that adds nothing.
     */
    @Nested
    @DisplayName("an empty source")
    class EmptySource {

        /**
         * Leaves the item's brands exactly as it was.
         */
        @Test
        @DisplayName("dest is unchanged")
        void leavesDestUnchanged() {
            ItemObject dest = new ItemObject();
            Brand acid = brand("ACID_3", "acid", 3);
            dest.addBrand(acid);

            copyBrands(dest, new ObjectKind());

            assertEquals(Set.of(acid), dest.getBrands());
        }
    }

    /**
     * The order the merged brands come out in. The sets keep the order they were filled in, and the
     * merge does not re-sort by registry position.
     */
    @Nested
    @DisplayName("the order of the merged brands")
    class Order {

        /**
         * An empty item takes the kind's order exactly, which is the case {@link ObjectUtils#objectPrep}
         * always presents and the one that matches C, because the kind's set is loaded in registry order.
         */
        @Test
        @DisplayName("an empty dest takes the source's order")
        void emptyDestTakesSourceOrder() {
            List<Brand> expected = new ArrayList<>();
            for (int i : new int[]{7, 2, 11, 0, 5, 9, 3, 10, 1, 8, 4, 6}) {
                expected.add(brand("B" + i, "b" + i, 3));
            }
            ObjectKind source = new ObjectKind();
            expected.forEach(source::addBrand);

            ItemObject dest = new ItemObject();
            copyBrands(dest, source);

            assertEquals(expected, new ArrayList<>(dest.getBrands()));
        }

        /**
         * Pins the Java behaviour where it differs from C. C walks the combined set in table order;
         * here the item's brands stay first and the kind's unmatched ones follow, in the kind's order.
         */
        @Test
        @DisplayName("source brands with no partner are appended after dest's, not re-sorted")
        void unmatchedSourceBrandsAreAppendedAfterDestsHere() {
            Brand late = brand("LATE", "late", 3);
            Brand early1 = brand("EARLY1", "early1", 3);
            Brand early2 = brand("EARLY2", "early2", 3);
            ItemObject dest = new ItemObject();
            dest.addBrand(late);
            ObjectKind source = new ObjectKind();
            source.addBrand(early1);
            source.addBrand(early2);

            copyBrands(dest, source);

            assertEquals(List.of(late, early1, early2), new ArrayList<>(dest.getBrands()));
        }

        /**
         * A stronger source brand takes the place of the weaker dest one it replaces.
         */
        @Test
        @DisplayName("a replacement stays where the brand it replaced stood")
        void replacementKeepsThePlace() {
            Brand first = brand("FIRST", "first", 3);
            Brand weakAcid = brand("ACID_2", "acid", 2);
            Brand last = brand("LAST", "last", 3);
            ItemObject dest = new ItemObject();
            dest.addBrand(first);
            dest.addBrand(weakAcid);
            dest.addBrand(last);
            Brand strongAcid = brand("ACID_3", "acid", 3);
            ObjectKind source = new ObjectKind();
            source.addBrand(strongAcid);

            copyBrands(dest, source);

            List<Brand> result = new ArrayList<>(dest.getBrands());
            assertEquals(List.of("FIRST", "ACID_3", "LAST"),
                    result.stream().map(Brand::getCode).toList());
        }

        /**
         * Pins the Java behaviour where it differs from C. On an equal multiplier C clears the
         * lower table index, so the later brand survives; here the item's own brand, met first,
         * survives whatever the registry position of the two. {@code brand.txt} has no such pair
         * (see {@code BrandSlayNoTieDataTest}), so it cannot show on the shipped data.
         */
        @Test
        @DisplayName("on an exact tie dest's brand survives, where C keeps the higher index")
        void tieKeepsDestsBrandHereWhereCKeepsTheHigherIndex() {
            Brand destBrand = brand("ACID_A", "acid", 3);
            Brand sourceBrand = brand("ACID_B", "acid", 3);
            ItemObject dest = new ItemObject();
            dest.addBrand(destBrand);
            ObjectKind source = new ObjectKind();
            source.addBrand(sourceBrand);

            copyBrands(dest, source);

            assertEquals(1, dest.getBrands().size());
            assertSame(destBrand, dest.getBrands().iterator().next());
        }
    }
}
