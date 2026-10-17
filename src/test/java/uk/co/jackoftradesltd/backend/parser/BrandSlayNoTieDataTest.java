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

package uk.co.jackoftradesltd.backend.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.co.jackoftradesltd.middle.monsters.MonsterBase;
import uk.co.jackoftradesltd.middle.monsters.MonsterPain;
import uk.co.jackoftradesltd.middle.objects.Brand;
import uk.co.jackoftradesltd.middle.objects.Slay;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the one fact that lets {@code ObjectUtils.copyBrands} and {@code copySlays} get away with
 * applying no ordering: the real {@code brand.txt} and {@code slay.txt} never declare two entries
 * that C's {@code copy_brands} and {@code copy_slays} would treat as duplicates and then have to
 * separate on table position.
 *
 * <p>C walks every pair of set indices {@code j < i} and, for a pair that names the same element
 * (brands) or kills the same monsters (slays), clears the one with the lower multiplier. On an
 * exact tie its {@code else} arm clears {@code j}, the lower index, so the survivor depends on
 * table order. The port scans for a match and keeps the higher multiplier, which on a tie keeps
 * whichever it met first, and the brand and slay sets are unordered {@link java.util.HashSet}s, so
 * a tie would resolve arbitrarily. The two agree only while no tie exists in the data.
 *
 * <p>If this test fails, the data has gained a tie. The sort by registry position belongs inside
 * {@code copyBrands} and {@code copySlays} (see the ordering note in their Javadoc), not on the
 * sets.
 *
 * <p>Brands group by name, as C's {@code streq(brands[i].name, brands[j].name)}; slays group by
 * {@link Slay#sameMonsterSlain}. The slay load resolves {@code base:} through the monster base
 * list, so {@link #seed()} loads {@code pain.txt} and {@code monster_base.txt} directly and
 * injects them by reflection, restoring them afterwards as {@link SlayReaderTest} does.
 *
 * @author Rowan Crowther
 */
class BrandSlayNoTieDataTest {

    private static final String BRAND_FILE = "lib/gamedata/brand.txt";
    private static final String SLAY_FILE = "lib/gamedata/slay.txt";
    private static final String PAIN_FILE = "lib/gamedata/pain.txt";
    private static final String BASE_FILE = "lib/gamedata/monster_base.txt";

    private static Object savedPains;
    private static Object savedBases;

    /**
     * Loads the monster lists the slay assembler needs and keeps what was there.
     *
     * @throws Exception if a data file cannot be read or a registry field cannot be reached
     */
    @BeforeAll
    static void seed() throws Exception {
        List<MonsterPain> pains = new PainReader().parseWithResults(PAIN_FILE).items();
        savedPains = setStatic("monsterPains", pains);

        List<MonsterBase> bases = new MonsterBaseReader().parseWithResults(BASE_FILE).items();
        savedBases = setStatic("monsterBases", bases);
    }

    /**
     * Puts the registry fields back as they were so no state leaks to other suites.
     *
     * @throws Exception if a registry field cannot be reached
     */
    @AfterAll
    static void restore() throws Exception {
        setStatic("monsterPains", savedPains);
        setStatic("monsterBases", savedBases);
    }

    private static Object setStatic(String field, Object value) throws Exception {
        Field f = RegistrySeeding.resolve(field);
        f.setAccessible(true);
        Object old = f.get(null);
        f.set(null, value);
        return old;
    }

    /**
     * No two brands share a name and a multiplier.
     *
     * @throws Exception if {@code brand.txt} cannot be read
     */
    @Test
    @DisplayName("brand.txt has no two brands of one name at one multiplier")
    void noBrandTie() throws Exception {
        List<Brand> brands = new BrandReader().parseWithResults(BRAND_FILE).items();
        assertFalse(brands.isEmpty(), "brand.txt loaded no brands, so the check below proves nothing");

        List<String> ties = new ArrayList<>();
        for (int i = 0; i < brands.size(); i++) {
            for (int j = 0; j < i; j++) {
                Brand a = brands.get(i);
                Brand b = brands.get(j);
                if (a.getName().equals(b.getName()) && a.getMultiplier() == b.getMultiplier()) {
                    ties.add(a.getCode() + " and " + b.getCode() + " (name " + a.getName()
                            + ", multiplier " + a.getMultiplier() + ")");
                }
            }
        }

        assertTrue(ties.isEmpty(), () -> "copyBrands has no tie-break for: " + ties);
    }

    /**
     * No two slays kill the same monsters at the same multiplier.
     *
     * @throws Exception if {@code slay.txt} cannot be read
     */
    @Test
    @DisplayName("slay.txt has no two slays on the same monsters at one multiplier")
    void noSlayTie() throws Exception {
        List<Slay> slays = new SlayReader().parseWithResults(SLAY_FILE).items();
        assertFalse(slays.isEmpty(), "slay.txt loaded no slays, so the check below proves nothing");

        List<String> ties = new ArrayList<>();
        for (int i = 0; i < slays.size(); i++) {
            for (int j = 0; j < i; j++) {
                Slay a = slays.get(i);
                Slay b = slays.get(j);
                if (a.sameMonsterSlain(b) && a.getMultiplier() == b.getMultiplier()) {
                    ties.add(a.getCode() + " and " + b.getCode() + " (multiplier "
                            + a.getMultiplier() + ")");
                }
            }
        }

        assertTrue(ties.isEmpty(), () -> "copySlays has no tie-break for: " + ties);
    }
}
